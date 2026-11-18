package org.georchestra.geonetwork.security.integration;

import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * Configuration to manually bind {@link ExternalizedSecurityProperties} from
 * {@code geonetwork.properties}.
 * <p>
 * Wouldn't be necessary with spring-boot.
 */
@Configuration
class ExternalizedSecurityPropertiesConfiguration {
    static final Logger log = LoggerFactory
            .getLogger(ExternalizedSecurityPropertiesConfiguration.class.getPackage().getName());

    private @Autowired Environment environment;

    @Bean
    @Primary // override defaultExternalizedSecurityConfigProperties
    public ExternalizedSecurityProperties georchestraSecurityConfigProperties() {
        log.info("Loading externalized security configuration from environment");
        String prefix = "geonetwork";
        Binder binder = Binder.get(environment);
        ExternalizedSecurityProperties propsBean = binder.bindOrCreate(prefix, ExternalizedSecurityProperties.class);
        return propsBean;
    }
}
