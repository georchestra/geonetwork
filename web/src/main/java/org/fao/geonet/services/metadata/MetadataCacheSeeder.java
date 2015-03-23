package org.fao.geonet.services.metadata;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.server.resources.ResourceManager;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.fao.geonet.GeonetworkDataDirectory;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.lib.Lib;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class MetadataCacheSeeder extends QuartzJobBean implements ApplicationContextAware {

    /**
     * The path on the file system where the files can be copied.
     */
    private String cachePath;
    
    /**
     * The XSL stylesheet to be used, in the formatter context,
     * basically the name of the directory containing the view.xsl stylesheet.
     */
    private String xslStylesheet;

    public void setXslStylesheet(String xslStylesheet) {
        this.xslStylesheet = xslStylesheet;
    }

    /**
     * The full path to the XSL stylsheet.
     */
    private String xslPath;
    /**
     * Flag to control if the backup is currently running.
     */
    private static AtomicBoolean isRunning = new AtomicBoolean(false);
    
    
    private ApplicationContext applicationContext;
    
    /**
     * Constructor
     */
    public MetadataCacheSeeder() {}

    /**
     * The main entry point of the scheduler.
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        if (isRunning.get()) {
            Log.info(Geonet.CACHESEEDER, "Cache seeder already running. Skipping execution.");
            return;
        }

        if (applicationContext == null) {
            Log.error(Geonet.CACHESEEDER,"Application context is null. Be sure to configure SchedulerFactoryBean job factory property with AutowiringSpringBeanJobFactory.");
            return;
        }

        ResourceManager resourceManager = applicationContext.getBean(ResourceManager.class);
        Dbms dbms = null;
        
        // It's actually pretty hard to get a hook on jeeves managed objects from a spring object.
        // Bypassing GN internals and doing it by hand ...
        // anyway, the system variable is present on every georchestra's geonetwork ...
        String userXslDir = System.getProperty(GeonetworkDataDirectory.GEONETWORK_DIR_KEY) + File.separator + "data/user_xsl/";
        xslPath = userXslDir + xslStylesheet + "/view.xsl";

        try {
            isRunning.set(true);
            dbms = (Dbms) resourceManager.openDirect(Geonet.Res.MAIN_DB);
            File fCachePath = new File(cachePath);

            if ((Log.isDebugEnabled(Geonet.CACHESEEDER) && !(fCachePath.exists()))) {
                Log.debug(Geonet.CACHESEEDER, "Directory does not exist. Skipping execution.");
                return;
            }

            String[] extensions = { "html" };
            Iterator<File> cachedFiles = FileUtils.iterateFiles(fCachePath, extensions, false);
            ArrayList<String> oldUuids = buildCachedUuid(cachedFiles);
            Log.info(Geonet.CACHESEEDER, oldUuids.size() + " old cached files to check.");
            
            ArrayList<String> newUuids = getPublicUuids(dbms);
            
            // Yesterday calculation
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            Date yesterday = cal.getTime();
            
            // Step 1. if oldUuid not in dbUuid, remove the file

            for (String curUuid : oldUuids) {
                if (! newUuids.contains(curUuid)) {
                    cleanUpMetadata(curUuid);
                }
            }
            
            for (String curUuid : newUuids) {
                // Step 2. if dbUuid.html exists in cache {
                if (new File(this.cachePath, curUuid + ".html").exists()) {
                    //    Step 2.1 regenerate the html file if modificationDate < current date - 24h
                    if (getMetadataChangeDate(dbms, curUuid).after(yesterday)) {
                        if (Log.isDebugEnabled(Geonet.CACHESEEDER))
                            Log.debug(Geonet.CACHESEEDER, String.format("Changedate for MD %s more recent than last job run, regenerating a file", curUuid));
                        try {
                        generateHtmlFile(dbms, curUuid);
                        } catch (Exception e) {
                            Log.error(Geonet.CACHESEEDER, "Error trying to generate an HTML file.", e);
                        }
                    } 
                    //    Step 2.2 if modificationDate > current date - 24h, file already up to date, skipping
                    else {
                        if (Log.isDebugEnabled(Geonet.CACHESEEDER))
                            Log.debug(Geonet.CACHESEEDER, String.format("Changedate for MD %s less recent than last job run, skipping.", curUuid)); 
                    }
                } 
                // Step 3. else generates the HTML file                
                else {
                    if (Log.isDebugEnabled(Geonet.CACHESEEDER))
                        Log.debug(Geonet.CACHESEEDER, String.format("File does not exist yet for MD %s, generating a one.", curUuid));
                    try {
                        generateHtmlFile(dbms, curUuid);
                    } catch (Exception e) {
                        Log.error(Geonet.CACHESEEDER, "Error trying to generate an HTML file.", e);
                    }
                }
            }
        } catch (Exception e) {
            Log.error(Geonet.CACHESEEDER, "Error occured while seeding cache: ", e);
        } finally {
            isRunning.set(false);
            if (dbms != null) {
                try {
                    resourceManager.close(Geonet.Res.MAIN_DB, dbms);
                } catch (Exception e) {
                    Log.info(Geonet.CACHESEEDER, "unable to close the dbms resource");
                }
            }
            if (Log.isDebugEnabled(Geonet.CACHESEEDER))
                Log.debug(Geonet.CACHESEEDER, "metadata cache seeder job ended");
        }
    }

    private void generateHtmlFile(Dbms dbms, String curUuid) throws Exception {
        // Does globally the same as Format.java
        // In a more concise way.
        
        Element root = new Element("root");
        Element md = Xml.loadString(getMetaData(dbms, curUuid), false);

        root = root.addContent(md);

        Element transformed = Xml.transform(root, xslPath);
        FileUtils.writeStringToFile(new File(this.cachePath, curUuid + ".html"), Xml.getString(transformed));
    }

    private String getMetaData(Dbms dbms, String curUuid) throws Exception {
        Element resp = dbms.select("SELECT data FROM metadata WHERE uuid = ? LIMIT 1; ", curUuid);
        return resp.getChild("record").getChildText("data");
    }

    private Date getMetadataChangeDate(Dbms dbms, String curUuid) {
        Date d = new Date();
        try {
            Element resp = dbms.select(
                    "SELECT changedate FROM metadata WHERE uuid = ? LIMIT 1; ",
                    curUuid);
            String strDate = resp.getChild("record").getChildText("changedate");
            String[] patterns = { DateFormatUtils.ISO_DATETIME_FORMAT
                    .getPattern() };
            strDate = strDate.substring(0, 19);
            d = DateUtils.parseDate(strDate, patterns);
        } catch (Exception e) {
            // the main risk is to regenerate more often than planned the HTML doc.
            Log.error(Geonet.CACHESEEDER,
                    "Unable to get the Metadata change date (parse error), using current date.", e);
        }
        return d;
    }

    /**
     * Removes a metadata which is still present in cache but no more public in the catalog.
     * @param curUuid
     */
    private void cleanUpMetadata(String curUuid) {
        try {
            new File(this.cachePath, curUuid + ".html").delete();
        } catch(Exception e) {
            Log.error(Geonet.CACHESEEDER, "Unable to remove the Metadata", e);
        }
    }

    /**
     * Retrieves every public MD uuids.
     * @return ArrayList an array list of strings.
     * @throws SQLException 
     */
    private ArrayList<String> getPublicUuids(Dbms dbms) throws SQLException {
        ArrayList<String> mdUuids = new ArrayList<String>();
        // operationid = 0 (view)
        // groupid = 1 (ALL)
        Element resp = dbms.select("SELECT uuid FROM metadata FULL OUTER JOIN operationallowed ON metadata.id = operationallowed.metadataid WHERE (groupid = 1 AND operationid = 0"
                + " AND schemaid IN ('iso19139.pigma', 'iso19139', 'iso19139.fra')) OR (isharvested = 'y') ; " );
        
        List<Element> records = (List<Element>) resp.getChildren();
        for (Element record : records) {
            mdUuids.add(record.getChildText("uuid"));
        }
        
        return mdUuids;
    }

    private ArrayList<String> buildCachedUuid(Iterator<File> fileList) {
        ArrayList<String> uuids = new ArrayList<String>();
        
        while (fileList.hasNext()) {
            uuids.add(FilenameUtils.getBaseName(fileList.next().getAbsolutePath()));
        }

        return uuids;
    }
    
    /**
     * Sets the cache path, called internally by Spring at bean instantiation.
     * @param cachePath
     *            a String describing the path where the cache files can be
     *            stored.
     */
    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

}
