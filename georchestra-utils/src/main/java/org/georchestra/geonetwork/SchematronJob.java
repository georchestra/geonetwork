package org.georchestra.geonetwork;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.domain.Metadata;
import org.fao.geonet.domain.MetadataType;
import org.fao.geonet.kernel.datamanager.IMetadataValidator;
import org.fao.geonet.repository.MetadataRepository;
import org.fao.geonet.repository.specification.MetadataSpecs;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class SchematronJob extends QuartzJobBean {

    private AtomicBoolean started = new AtomicBoolean(false);
    private final Logger Log = Logger.getLogger(this.getClass());

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private IMetadataValidator metadataValidator;
    @Value("${schematron.job.activated:false}")
    private boolean activated;

    @Override
    protected void executeInternal(final JobExecutionContext jobExecContext) throws JobExecutionException {
        if (activated == false) {
            Log.info("schematronJob is not activated, skipping execution");
            return;
        }
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
            
            final Specifications<Metadata> spec =
                    Specifications.where((Specification<Metadata>) MetadataSpecs.isType(MetadataType.METADATA))
                        .and((Specification<Metadata>) MetadataSpecs.isHarvested(false));

            List<Integer> mdToValidate = mdrepo.findAllIdsBy(spec);
            for (Integer mdId : mdToValidate) {
                Metadata record = mdrepo.findOne(mdId);
                try {
                    metadataValidator.doValidate(record, "eng");
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
