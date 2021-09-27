package org.georchestra.geonetwork.security.integration;

import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Configuration to manually bind {@link ExternalizedSecurityProperties} from
 * {@code geonetwork.properties}.
 * <p>
 * Wouldn't be necessary with spring-boot.
 */
@Configuration
@PropertySource(value = "file:${georchestra.datadir}/geonetwork/geonetwork.properties")
class ExternalizedSecurityPropertiesConfiguration {

    private @Autowired Environment environment;

    @Bean
    @Primary // override defaultExternalizedSecurityConfigProperties
    public ExternalizedSecurityProperties georchestraSecurityConfigProperties() {
        String prefix = "geonetwork";
        Binder binder = Binder.get(environment);
        ExternalizedSecurityProperties propsBean = binder.bindOrCreate(prefix, ExternalizedSecurityProperties.class);
        return propsBean;
    }
}
