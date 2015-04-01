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
import jeeves.server.resources.ResourceManager;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.fao.geonet.GeonetworkDataDirectory;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.QuartzJobBean;
/**
 * This class defines a background process responsible of several things:
 *
 * - It generates a html output from a XSL stylesheet for every public or harvested metadatas
 *   stored in the catalog,
 * 
 * - it maintains a sitemap.xml file for better indexation against search engines ;
 * 
 * To function properly, it has to be configured (defined as bean) into config-spring-geonetwork.xml, using the following
 * snippet:
 * 
 *     <bean name="metadataCacheSeeder" class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
 *       <property name="jobClass" value="org.fao.geonet.services.metadata.MetadataCacheSeeder" />
 *       <property name="jobDataAsMap">
 *         <map>
 *           <entry key="xslStylesheet" value="pigma-static-html" />
 *           <entry key="cachePath" value="/tmp/pigma-md-cache" />
 *         </map>
 *       </property>
 *   </bean>
 *
 *   <bean id="metadataCacheSeederCronTrigger" class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
 *       <property name="jobDetail" ref="metadataCacheSeeder" />
 *       <!-- runs every days at 4:00 AM -->
 *       <property name="cronExpression" value="0 0 4 * * ?" />
 *       <property name="startDelay" value="30000"/>
 *   </bean>
 *   
 *   <bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
 *       <property name="jobFactory">
 *           <bean class="org.fao.geonet.util.spring.AutowiringSpringBeanJobFactory"/>
 *       </property>
 *       <property name="triggers">
 *           <list>
 *               <ref bean="metadataCacheSeederCronTrigger" />
 *           </list>
 *       </property>
 *   </bean>
 *
 * @author pmauduit
 *
 */
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

    /**
     * The full path to the XSL stylsheet.
     */
    private String xslPath;
    
    /**
     * The prefix where the files would be publicly available.
     * (e.g. http://ids.pigma.org/mds for http://ids.pigma.org/mds/[uuid].html)
     */
    private String urlPath = "http://ids.pigma.org/mds";
    
    /**
     * Default change frequency for the sitemap items.
     */
    private String changeFreq = "daily";
    /**
     * Flag to control if the process is currently running.
     */
    private static AtomicBoolean isRunning = new AtomicBoolean(false);
    
    /**
     * The namespace to be used for generating the sitemap.
     */
    private final String SITEMAP_NS = "http://www.google.com/schemas/sitemap/0.84";
    
    
    /**
     * The spring application context, used to get a hook to necessary beans of the running catalogue.
     */
    private ApplicationContext applicationContext;
    
    /**
     * Flag indicating if the service is enabled or not.
     */
    private boolean enabled = true;
    
    /**
     * Constructor
     */
    public MetadataCacheSeeder() {}

    /**
     * The main entry point of the scheduler.
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        if (! enabled) {
            return;
        }
        
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
        // Anyway, the system variable is present on every georchestra's geonetwork ...
        String userXslDir = System.getProperty(GeonetworkDataDirectory.GEONETWORK_DIR_KEY) + File.separator + "data/user_xsl/";
        xslPath = userXslDir + xslStylesheet + "/view.xsl";

        try {
            isRunning.set(true);
            dbms = (Dbms) resourceManager.openDirect(Geonet.Res.MAIN_DB);
            File fCachePath = new File(cachePath);

            if (! fCachePath.isDirectory()) {
                try {
                    FileUtils.forceMkdir(fCachePath);
                } catch (IOException e) {
                    Log.debug(Geonet.CACHESEEDER, "Unable to create the cache directory. Skipping execution and deactivating the service.");
                    enabled = false;
                    return;                    
                }
            }

            if (! fCachePath.isDirectory()) {
                if (Log.isDebugEnabled(Geonet.CACHESEEDER)) {
                    Log.debug(Geonet.CACHESEEDER, "Directory does not exist. Skipping execution.");
                }
                enabled = false;
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
                
                // step 4. generate a sitemap.xml
                cachedFiles = FileUtils.iterateFiles(fCachePath, extensions, false);
                generateSitemapXml(cachedFiles);
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

    
    /**
     * This method is responsible of generating a sitemap.xml file in the
     * cache path folder.
     * 
     * @param Iterator<File> cachedFiles the list of files to include
     * to the sitemap.xml.
     */
    private void generateSitemapXml(Iterator<File> cachedFiles) {
        Element urlset = new Element("urlset", SITEMAP_NS);
        while (cachedFiles.hasNext()) {
            File f = cachedFiles.next();
            String lastMod = new ISODate(f.lastModified()).toString();
            
            String strf = f.getAbsolutePath();
            Element url = new Element("url",SITEMAP_NS);
            url.addContent(new Element("loc", SITEMAP_NS).setText(urlPath + "/" + FilenameUtils.getName(strf)));
            url.addContent(new Element("changefreq", SITEMAP_NS).setText(changeFreq));
            url.addContent(new Element("lastmod", SITEMAP_NS).setText(lastMod));
            urlset.addContent(url);
        }
        
        try {
            FileUtils.writeStringToFile(new File(cachePath, "sitemap.xml"), Xml.getString(urlset));
        } catch (IOException e) {
            Log.error(Geonet.CACHESEEDER, "Unable to generate the sitemap.xml file: ", e);
        }
    }

    /**
     * This method generates a single html file from a given UUID.
     * 
     * @param dbms a DBMS resource to access datas from the database.
     * @param curUuid the UUID of the metadata.
     *
     * @throws Exception
     */
    private void generateHtmlFile(Dbms dbms, String curUuid) throws Exception {
        // This does globally the same as Format.java
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
        // or harvested MDs (which are considered public)
        Element resp = dbms.select("SELECT "
                + "                        uuid "
                + "                 FROM "
                + "                        metadata"
                + "                 FULL OUTER JOIN "
                + "                        operationallowed "
                + "                 ON "
                + "                        metadata.id = operationallowed.metadataid "
                + "                 WHERE "
                + "                       (groupid = 1 AND operationid = 0 AND schemaid IN ('iso19139.pigma', 'iso19139', 'iso19139.fra')) "
                + "                 OR "
                + "                       (isharvested = 'y') ; " );
        
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
     * 
     * @param cachePath a String describing the path where the cache files can be stored.
     */
    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public void setXslStylesheet(String xslStylesheet) {
        this.xslStylesheet = xslStylesheet;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

}
