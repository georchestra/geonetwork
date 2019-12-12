package org.georchestra.geonetwork;

import static org.fao.geonet.repository.specification.MetadataSpecs.isHarvested;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.domain.Metadata;
import org.fao.geonet.kernel.datamanager.IMetadataValidator;
import org.fao.geonet.repository.MetadataRepository;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import jeeves.server.context.ServiceContext;

@Component
public class SchematronJob extends QuartzJobBean {

    private AtomicBoolean started = new AtomicBoolean(false);
    private final Logger Log = Logger.getLogger(this.getClass());

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private IMetadataValidator metadataValidator;

    @Override
    protected void executeInternal(final JobExecutionContext jobExecContext) throws JobExecutionException {
        if (started.get() == true) {
            Log.info("Job already running, skipping execution");
            return;
        }
        try {
            started.set(true);

            if ((applicationContext == null) || (metadataValidator == null)) {
                Log.error("applicationContext or _dataManager is null, skipping execution");
                return;
            }
            ApplicationContextHolder.set(applicationContext);
            MetadataRepository mdrepo = applicationContext.getBean(MetadataRepository.class);
            ServiceContext serviceContext = ServiceContext.get();
            
            List<Integer> mdToValidate = mdrepo.findAllIdsBy((Specification<Metadata>) isHarvested(false));
            for (Integer mdId : mdToValidate) {
                Metadata record = mdrepo.findOne(mdId);
                try {
                	metadataValidator.validateMetadata(record.getDataInfo().getSchemaId(),
                			record.getXmlData(false), serviceContext, " ");
                } catch (Exception e) {
                    Log.error("Error validating metadata id " + record.getUuid());
                }
            }
        } catch (Exception e) {
            Log.error("Exception occured: ", e);
        } finally {
            Log.info("Finished validation job");
            started.set(false);
        }
    }
}
