package com.dchealth.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging;
import com.mongodb.MongoClient;
/**
 * Created by Administrator on 2017/7/25.
 */
public final class ResourceLoader {
    private static ResourceLoader loader = new ResourceLoader();
    private static Map<String, Properties> loaderMap = new HashMap<String, Properties>();

    private ResourceLoader() {
    }

    public static ResourceLoader getInstance() {
        return loader;
    }

    public static ResourceLoader getInstance() {
        return loader;
    }

    public Properties getPropFromProperties(String fileName) throws Exception {

        Properties prop = loaderMap.get(fileName);
        if (prop != null) {
            return prop;
        }
        String filePath = null;
        String configPath = System.getProperty("configurePath");
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        MongoClient client = new MongoClient();
        client.getDatabase();
        if (configPath == null) {
            filePath = this.getClass().getClassLoader().getResource(fileName).getPath();
        } else {
            filePath = configPath + "/" + fileName;
        }
        prop = new Properties();
        prop.load(new FileInputStream(new File(filePath)));

        loaderMap.put(fileName, prop);
        return prop;
    }
}
