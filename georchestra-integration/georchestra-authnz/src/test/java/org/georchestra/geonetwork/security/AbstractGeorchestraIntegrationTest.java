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

package org.georchestra.geonetwork.security;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;

import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.kernel.datamanager.base.BaseMetadataUtils;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.geonetwork.testcontainers.postgres.GeorchestraDatabaseContainer;
import org.georchestra.geonetwork.security.integration.CanonicalModelMapper;
import org.georchestra.geonetwork.security.integration.IntegrationTestSupport;
import org.georchestra.testcontainers.console.GeorchestraConsoleContainer;
import org.georchestra.testcontainers.ldap.GeorchestraLdapContainer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.Testcontainers;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = { //
        "georchestra.datadir=${user.dir}/src/test/resources/data_directory", //
        "geonetwork.scheduled.enabled=false"//
})
@ContextConfiguration(locations = { //
        "classpath:config-spring-geonetwork.xml", //
        "classpath:config-security-georchestra.xml", //
        "classpath:domain-repository-test-context.xml" })
@DirtiesContext // reset the app context for each test
public abstract class AbstractGeorchestraIntegrationTest {

    protected static final Logger log = LoggerFactory
            .getLogger(AbstractGeorchestraIntegrationTest.class.getPackage().getName());

    public static @ClassRule GeorchestraLdapContainer ldap = new GeorchestraLdapContainer();
    public static @ClassRule GeorchestraDatabaseContainer db = new GeorchestraDatabaseContainer();
    public static GeorchestraConsoleContainer console;
    // it'd be too much of a pita to actually do CRUD operations on the console
    // app, so spy the repo to tweak its responses when needed
    protected @SpyBean CanonicalAccountsRepository consoleAccountsRepository;

    public @MockBean BaseMetadataUtils baseMetadataUtils;
    private @MockBean(name = "authenticationManager") AuthenticationManager authenticationManager;

    private @Autowired ExternalizedSecurityProperties configProps;
    protected @Autowired CanonicalModelMapper mapper;

    public IntegrationTestSupport support;

    @SuppressWarnings("resource")
    public static @BeforeClass void startUpConsoleContainer() {

        Testcontainers.exposeHostPorts(ldap.getMappedLdapPort(), db.getMappedDatabasePort());

        console = new GeorchestraConsoleContainer()//
                .withFileSystemBind(resolveHostDataDirectory(), "/etc/georchestra")//
                .withEnv("pgsqlHost", "host.testcontainers.internal")//
                .withEnv("pgsqlPort", String.valueOf(db.getMappedDatabasePort()))//
                .withEnv("ldapHost", "host.testcontainers.internal")//
                .withEnv("ldapPort", String.valueOf(ldap.getMappedLdapPort()))//
                .withLogToStdOut();

        console.start();
        System.setProperty("georchestra.console.url",
                String.format("http://localhost:%d", console.getMappedConsolePort()));
    }

    private static String resolveHostDataDirectory() {
        File dataDir = new File("src/test/resources/data_directory").getAbsoluteFile();
        assertTrue(dataDir.isDirectory());
        return dataDir.getAbsolutePath();
    }

    public static @AfterClass void shutDownConsoleContainer() {
        console.stop();
    }

    public @Before void prepareConsoleAccountsRepositorySpiedMock() {
        this.support = new IntegrationTestSupport(configProps);
        ApplicationContextHolder.clear();
        when(consoleAccountsRepository.findAllUsers()).thenCallRealMethod();
        when(consoleAccountsRepository.findAllOrganizations()).thenCallRealMethod();
        when(consoleAccountsRepository.findAllRoles()).thenCallRealMethod();
    }
}
