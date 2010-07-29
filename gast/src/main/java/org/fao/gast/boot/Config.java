package org.fao.gast.boot;

import org.fao.gast.gui.dialogs.StartupConfigPanel;
import org.fao.gast.localization.Messages;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * User: jeichar
 * Date: Jul 5, 2010
 * Time: 5:32:20 PM
 */
public class Config {

    public static final Config DEV_CONFIG = new Config(Config.class.getResourceAsStream("dev-config.properties"));
    private static final Config STD_CONFIG = new Config(Config.class.getResourceAsStream("std-config.properties"));
    private static final Config PREV_CONFIG;

    private static final File PREV_CONFIG_FILE;

    static {
        File dir = new File(Util.getJarFile("org/fao/gast/Gast.class")).getParentFile();
        PREV_CONFIG_FILE = new File(dir, "previousGastConfig.properties");

        Config config = STD_CONFIG;
        
        if(PREV_CONFIG_FILE.exists()) {
            try {
                config = new Config(new FileInputStream(PREV_CONFIG_FILE));
            } catch (FileNotFoundException e) {
                // ignore
            }
        }

        PREV_CONFIG = config;
    }
    
    private static Config CONFIG;
    public static Config getConfig() {
        if (CONFIG == null) {
            CONFIG = new Config();

            if(PREV_CONFIG_FILE.exists()) {
                if(PREV_CONFIG.isValid()) {
                    CONFIG = PREV_CONFIG;
                } else {
                    queryForWebapp();
                }
            } else if(DEV_CONFIG.isValid()) {
                CONFIG = DEV_CONFIG;
            } else {
                queryForWebapp();
            }
        }

        return CONFIG;
    }

    private boolean resolvePaths = true;

    Properties props = new Properties();

    public Config() {}
    
    public Config(InputStream propStream) {
        try {
            props.load(propStream);
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            try {
                propStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String resolvePath(String path, boolean force) {
        if( !force && !resolvePaths) {
            return path;
        } else {
            return startsWithFSRoot(path) ? path : (getWebapp() + "/" + path);
        }
    }
    public String getWebapp() {
        return props.getProperty("webapp");
    }

    public void setWebapp(String webapp) {
        props.setProperty("webapp",webapp);
    }

    public String getConfigXml() {
        return resolvePath(props.getProperty("config-xml"), false);
    }

    public void setConfigXml(String configXml) {
        props.setProperty("config-xml", configXml);
    }

    public String getLogos() {
        return resolvePath(props.getProperty("logos"), false);
    }

    public void setLogos(String logos) {
        props.setProperty("logos", logos);
    }

    public String getSetupConfig() {
        return resolvePath(props.getProperty("setup-config"), false);
    }

    public void setSetupConfig(String setupConfig) {
        props.setProperty("setup-config", setupConfig);
    }

    public String getTemplates() {
        return resolvePath(props.getProperty("templates"), false);
    }

    public void setTemplates(String templates) {
        props.setProperty("templates", templates);
    }

    public String getEmbeddedDb() {
        return resolvePath(props.getProperty("embedded-db"), false);
    }

    public void setEmbeddedDb(String embeddedDb) {
        props.setProperty("embedded-db", embeddedDb);
    }

    public String getWebXml() {
        return resolvePath(props.getProperty("web-xml"), false);
    }

    public void setWebXml(String webXml) {
        props.setProperty("web-xml", webXml);
    }

    public String getConversions() {
        return resolvePath(props.getProperty("conversions"), false);
    }

    public void setConversions(String conversions) {
        props.setProperty("conversions", conversions);
    }

    public String getSchemas() {
        return resolvePath(props.getProperty("schemas"), false);
    }

    public void setSchemas(String schemas) {
        props.setProperty("schemas", schemas);
    }

    public int getJettyPort() {
        try {
            return Integer.parseInt(props.getProperty("jetty-port"));
        } catch (NumberFormatException e) {
            setJettyPort(8080);
            return 8080;
        }
    }

    public void setJettyPort(int jettyPort) {
        props.setProperty("jetty-port", ""+jettyPort);
    }

    public String getSampleData() {
        return resolvePath(props.getProperty("sample-data"),false);
    }

    public void setSampleData(String sampleData) {
        props.setProperty("sample-data", sampleData);
    }

    public String getLogOutputDir() {
        return resolvePath(props.getProperty("logOutputDir"),false);
    }

    public void setLogOutputDir(String logOutputDir) {
        props.setProperty("logOutputDir",logOutputDir);
    }

    public boolean isValid() {
        return new File(getWebapp()).exists() && new File(getWebXml()).exists();
    }


    public static void queryForWebapp() {
        CONFIG = PREV_CONFIG;

        CONFIG.resolvePaths = false;
        StartupConfigPanel dialog = new StartupConfigPanel(CONFIG);
        dialog.setSize(700,400);
        dialog.pack();
        dialog.setVisible(true);

        while(dialog.isVisible()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        CONFIG.resolvePaths = true;

        try {
            CONFIG.save(PREV_CONFIG_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save(File out) throws IOException {
        CONFIG.props.store(new FileOutputStream(out), Messages.getString("propertiesComment"));
    }

    public static boolean startsWithFSRoot(String path) {
        File[] roots = File.listRoots();
        for(File root : roots) {
            if(path.startsWith(root.getPath())) {
                return true;
            }
        }
        return false;
    }

    public static URL getResource(String s) {
        return Config.class.getClassLoader().getResource(s);
    }

    public static void load(InputStream inputStream) {
        CONFIG = new Config(inputStream);
    }
}
