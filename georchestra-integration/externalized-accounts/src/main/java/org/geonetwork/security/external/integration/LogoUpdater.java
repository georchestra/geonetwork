package org.geonetwork.security.external.integration;

import jeeves.server.context.ServiceContext;
import jeeves.server.dispatchers.ServiceManager;
import org.fao.geonet.domain.Group;
import org.fao.geonet.resources.Resources;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class LogoUpdater {

    private CanonicalAccountsRepository canonicalAccounts;

    private @Autowired
    ConfigurableApplicationContext appContext;

    private @Autowired
    ServiceManager serviceManager;

    private  @Autowired
    Resources resources;

    private Path directoryPath;
    private ServiceContext serviceContext;


    public LogoUpdater(CanonicalAccountsRepository canonicalAccounts) {
        this.canonicalAccounts = canonicalAccounts;
    }

    @PostConstruct
    public void init() {
        directoryPath = resources.locateHarvesterLogosDirSMVC(appContext);
        serviceContext = serviceManager.createServiceContext("synchronizing", appContext);
        serviceContext.setLanguage("eng");
    }

    public void synchronize(String canonicalGroupId, Group group) {
        try {
            Optional<byte[]> optLogo = canonicalAccounts.getLogo(canonicalGroupId);
            if (optLogo.isPresent()) {
                Resources.ResourceHolder holder = resources.getWritableImage(serviceContext, canonicalGroupId, directoryPath);
                Files.copy(new ByteArrayInputStream(optLogo.get()), holder.getPath(), StandardCopyOption.REPLACE_EXISTING);
                group.setLogo(canonicalGroupId);
            } else {
                resources.deleteImageIfExists(canonicalGroupId, directoryPath);
                group.setLogo(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
