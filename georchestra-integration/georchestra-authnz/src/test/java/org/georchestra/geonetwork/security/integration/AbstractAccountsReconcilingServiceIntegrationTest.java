/*
 * Copyright (C) 2021 by the geOrchestra PSC
 *
 * This file is part of geOrchestra.
 *
 * geOrchestra is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * geOrchestra is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * geOrchestra.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.georchestra.geonetwork.security.integration;

import javax.transaction.Transactional;

import org.geonetwork.security.external.integration.AbstractAccountsReconcilingServiceIntegrationTest;
import org.geonetwork.security.external.integration.AccountsReconcilingService;
import org.geonetwork.security.external.integration.IntegrationTestSupport;
import org.geonetwork.testcontainers.postgres.GeonetworkPostgresContainer;
import org.georchestra.geonetwork.logging.Logging;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = "georchestra.datadir=src/test/resources/data_directory")
@ContextConfiguration(locations = { //
        "classpath:config-spring-geonetwork-parent.xml", //
        "classpath:config-security-georchestra-authzn.xml", //
        "classpath:domain-repository-test-context.xml" })
@DirtiesContext // reset the app context for each test
//@Transactional // run each test on a TestTransaction so no manual db cleanup is needed
public abstract class AbstractAccountsReconcilingServiceIntegrationTest {
    // config to delegate mocks initialization to AccountsTestSupport.Config only
    // when running this test (otherwise will be loaded by the package-level
    // component-scan directives in the xml config files
    static @Configuration class Config {
        public @Bean IntegrationTestSupport testSupport() {
            return new IntegrationTestSupport();
        }
    }

    protected static final Logging log = Logging
            .getLogger(AbstractAccountsReconcilingServiceIntegrationTest.class.getPackage().getName());

    /**
     * Set up container as a classrule so its ready when the tests run and we don't
     * need to implement a wait-for-db strategy for entityManager
     */
    public static @ClassRule GeonetworkPostgresContainer postgresContainer = new GeonetworkPostgresContainer();

    public @Autowired @Rule IntegrationTestSupport support;

    protected @Autowired AccountsReconcilingService service;

    public static @BeforeClass void logContainer() {
        Integer localPort = postgresContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);
        System.setProperty("jdbc.port", localPort.toString());
        log.info("set system property jdbc.port=%d, as reported by test container %s", localPort,
                postgresContainer.getContainerName());
    }
}
