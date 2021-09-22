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
package org.geonetwork.security.external.configuration;

import org.fao.geonet.kernel.datamanager.base.BaseMetadataUtils;
import org.geonetwork.security.external.integration.IntegrationTestSupport;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config to delegate mocks initialization to AccountsTestSupport.Config only
 * when running this test (otherwise will be loaded by the package-level
 * component-scan directives in the xml config files
 */
@Configuration
public class IntegrationTestContextConfiguration {

    public @MockBean BaseMetadataUtils baseMetadataUtils;

    public @Bean IntegrationTestSupport testSupport() {
        return new IntegrationTestSupport();
    }

}
