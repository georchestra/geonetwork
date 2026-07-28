/*
 * Copyright (C) 2001-2026 Food and Agriculture Organization of the
 * United Nations (FAO-UN), United Nations World Food Programme (WFP)
 * and United Nations Environment Programme (UNEP)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
 *
 * Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
 * Rome - Italy. email: geonetwork@osgeo.org
 */

package org.fao.geonet.listener.metadata;

import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.domain.Metadata;
import org.fao.geonet.domain.MetadataType;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.fao.geonet.events.md.MetadataAdd;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.geonet.languages.FeedbackLanguages;
import org.fao.geonet.repository.UserRepository;
import org.fao.geonet.util.MailUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class XPathEmailNotifierTest {

    private static final String TRIGGER_XPATH = ".//*[local-name()='Trigger']";

    private XPathEmailNotifier notifier;
    private SettingManager settingManager;
    private MockedStatic<MailUtil> mailUtilStatic;
    private MockedStatic<ApplicationContextHolder> applicationContextHolderStatic;
    private MockedStatic<ServiceContext> serviceContextStatic;

    @Before
    public void setUp() {
        notifier = new XPathEmailNotifier();

        UserRepository userRepository = mock(UserRepository.class);
        User admin = new User().setEmailAddresses(Collections.singleton("admin@example.org"));
        when(userRepository.findAllByProfile(Profile.Administrator)).thenReturn(Collections.singletonList(admin));

        settingManager = mock(SettingManager.class);
        when(settingManager.getSiteName()).thenReturn("Test Site");
        when(settingManager.getNodeURL()).thenReturn("http://localhost:8080/geonetwork/");

        FeedbackLanguages feedbackLanguages = mock(FeedbackLanguages.class);
        when(feedbackLanguages.getLocales(any(Locale.class))).thenReturn(new Locale[]{Locale.ENGLISH});

        ReflectionTestUtils.setField(notifier, "userRepository", userRepository);
        ReflectionTestUtils.setField(notifier, "settingManager", settingManager);
        ReflectionTestUtils.setField(notifier, "feedbackLanguages", feedbackLanguages);
        ReflectionTestUtils.setField(notifier, "xpaths", Collections.singletonList(TRIGGER_XPATH));

        // LocalizedEmail (used to build the subject/message) pulls FeedbackLanguages from the
        // application context rather than from an injected field.
        ConfigurableApplicationContext applicationContext = mock(ConfigurableApplicationContext.class);
        when(applicationContext.getBean(FeedbackLanguages.class)).thenReturn(feedbackLanguages);
        applicationContextHolderStatic = mockStatic(ApplicationContextHolder.class);
        applicationContextHolderStatic.when(ApplicationContextHolder::get).thenReturn(applicationContext);

        mailUtilStatic = mockStatic(MailUtil.class);

        // ServiceContext.get() returns the thread-local context set up for the current request;
        // stub it here so onMetadataAdd() can read the logged-in user's profile from it.
        serviceContextStatic = mockStatic(ServiceContext.class);
        setCurrentUserProfile(Profile.Editor);
    }

    @After
    public void tearDown() {
        mailUtilStatic.close();
        applicationContextHolderStatic.close();
        serviceContextStatic.close();
        SecurityContextHolder.clearContext();
    }

    private void setCurrentUserProfile(Profile profile) {
        User user = new User().setUsername("test-user").setProfile(profile);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(user, "n/a", user.getAuthorities()));

        UserSession session = new UserSession();
        session.loginAs(user);

        ServiceContext context = mock(ServiceContext.class);
        when(context.getUserSession()).thenReturn(session);
        serviceContextStatic.when(ServiceContext::get).thenReturn(context);
    }

    @Test
    public void sendsNotificationWhenRecordMatchesConfiguredXPath() {
        Metadata metadata = newMetadata("record-1", "<root><Trigger/></root>");

        notifier.onMetadataAdd(new MetadataAdd(metadata));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> toCaptor = ArgumentCaptor.forClass(List.class);
        mailUtilStatic.verify(() -> MailUtil.sendMail(toCaptor.capture(), anyString(), anyString(), eq(settingManager)));
        assertEquals(Collections.singletonList("admin@example.org"), toCaptor.getValue());
    }

    @Test
    public void doesNotSendNotificationWhenRecordDoesNotMatchConfiguredXPath() {
        Metadata metadata = newMetadata("record-2", "<root><Other/></root>");

        notifier.onMetadataAdd(new MetadataAdd(metadata));

        mailUtilStatic.verifyNoInteractions();
    }

    @Test
    public void doesNotSendNotificationWhenCurrentUserIsAdministrator() {
        setCurrentUserProfile(Profile.Administrator);
        Metadata metadata = newMetadata("record-3", "<root><Trigger/></root>");

        notifier.onMetadataAdd(new MetadataAdd(metadata));

        mailUtilStatic.verifyNoInteractions();
    }

    @Test
    public void parsesConfiguredXPathsContainingQuotesAndCommas() {
        // Real-world config: several comma-separated XPath expressions, each using quoted
        // string literals - must not be mangled by the property parsing (see init()).
        String config = ".//*[local-name()='MD_ScopeCode'][@codeListValue='dataset'],"
            + ".//*[local-name()='MD_ScopeCode'][@codeListValue='series']";
        ReflectionTestUtils.setField(notifier, "xpathsConfig", config);
        ReflectionTestUtils.invokeMethod(notifier, "init");

        @SuppressWarnings("unchecked")
        List<String> xpaths = (List<String>) ReflectionTestUtils.getField(notifier, "xpaths");
        assertEquals(2, xpaths.size());
        assertEquals(".//*[local-name()='MD_ScopeCode'][@codeListValue='dataset']", xpaths.get(0));
        assertEquals(".//*[local-name()='MD_ScopeCode'][@codeListValue='series']", xpaths.get(1));
    }

    private static Metadata newMetadata(String uuid, String xmlData) {
        Metadata metadata = new Metadata();
        metadata.setUuid(uuid);
        metadata.getDataInfo().setType(MetadataType.METADATA);
        metadata.getHarvestInfo().setHarvested(false);
        metadata.setData(xmlData);
        return metadata;
    }
}
