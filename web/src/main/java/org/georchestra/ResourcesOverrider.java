package org.georchestra;

import org.fao.geonet.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ResourcesOverrider {

    @Autowired
    private ServletContext context;

    @Value("${georchestra.datadir}")
    private String georchestraDatadirPath;

    @PostConstruct
    public void replaceLayout() throws IOException {
        Log.debug(Log.RESOURCES,"about to override layout, " + georchestraDatadirPath);

        File source = Paths.get(georchestraDatadirPath,"geonetwork", "overrides", "base-layout.xsl").toFile();
        File target = Paths.get(context.getRealPath("xslt"), "base-layout.xsl").toFile();

        if(source.exists() && target.canWrite()) {
            Log.debug(Log.RESOURCES, String.format("Override layout, copy %s to %s.",
                    source.getAbsolutePath(),
                    target.getAbsolutePath()));
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            Log.debug(Log.RESOURCES, String.format("Override layout, CANNOT copy %s to %s.",
                    source.getAbsolutePath(),
                    target.getAbsolutePath()));
        }
    }
}