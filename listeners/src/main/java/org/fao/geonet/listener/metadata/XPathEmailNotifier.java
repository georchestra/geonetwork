/*
 * Copyright (C) 2001-2024 Food and Agriculture Organization of the
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

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.domain.MetadataType;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.fao.geonet.events.md.MetadataAdd;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.geonet.languages.FeedbackLanguages;
import org.fao.geonet.repository.UserRepository;
import org.fao.geonet.util.LocalizedEmail;
import org.fao.geonet.util.LocalizedEmailComponent;
import org.fao.geonet.util.LocalizedEmailParameter;
import org.fao.geonet.util.MailUtil;
import org.fao.geonet.utils.Log;
import org.fao.geonet.utils.Xml;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.fao.geonet.util.LocalizedEmailComponent.ComponentType.*;
import static org.fao.geonet.util.LocalizedEmailComponent.KeyType;
import static org.fao.geonet.util.LocalizedEmailComponent.ReplacementType.*;
import static org.fao.geonet.util.LocalizedEmailParameter.ParameterType;

/**
 * Sends an email notification to all GeoNetwork administrators when a newly created
 * record, added by a non-administrator user, matches one of the XPath expressions
 * configured via the {@code xpath.email.notifier.xpaths} property (see config.properties).
 */
@Component
public class XPathEmailNotifier implements ApplicationListener<MetadataAdd> {

    private static final List<Namespace> NAMESPACES = Arrays.asList(
        Geonet.Namespaces.GMD,
        Geonet.Namespaces.GCO,
        Namespace.getNamespace("mdb", "http://standards.iso.org/iso/19115/-3/mdb/2.0"),
        Namespace.getNamespace("mcc", "http://standards.iso.org/iso/19115/-3/mcc/1.0")
    );

    @Value("${xpath.email.notifier.xpaths:}")
    private String xpathsConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SettingManager settingManager;

    @Autowired
    private FeedbackLanguages feedbackLanguages;

    @Override
    public void onApplicationEvent(MetadataAdd event) {
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMetadataAdd(MetadataAdd event) {
        try {
            if (event.getMd().getDataInfo().getType() != MetadataType.METADATA) {
                return;
            }

            if (event.getMd().getHarvestInfo().isHarvested()) {
                return;
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(Profile.Administrator.name()))) {
                return;
            }

            if (StringUtils.isBlank(xpathsConfig)) {
                return;
            }

            Element xmlData = event.getMd().getXmlData(false);
            if (!matchesXPathTrigger(xmlData)) {
                return;
            }

            List<String> adminEmails = userRepository.findAllByProfile(Profile.Administrator)
                .stream()
                .map(User::getEmail)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

            if (adminEmails.isEmpty()) {
                Log.debug(Geonet.DATA_MANAGER, "XPathEmailNotifier: no administrator email addresses found, skipping notification");
                return;
            }

            String uuid = event.getMd().getUuid();
            String siteName = settingManager.getSiteName();
            String recordUrl = settingManager.getNodeURL() + "api/records/" + uuid;

            Locale[] feedbackLocales = feedbackLanguages.getLocales(new Locale(Geonet.DEFAULT_LANGUAGE));

            LocalizedEmailComponent subjectComponent = new LocalizedEmailComponent(SUBJECT, "xpath_email_notifier_subject", KeyType.MESSAGE_KEY, NUMERIC_FORMAT);
            LocalizedEmailComponent messageComponent = new LocalizedEmailComponent(MESSAGE, "xpath_email_notifier_text", KeyType.MESSAGE_KEY, NUMERIC_FORMAT);

            for (Locale feedbackLocale : feedbackLocales) {
                subjectComponent.addParameters(feedbackLocale,
                    new LocalizedEmailParameter(ParameterType.RAW_VALUE, 0, siteName));
                messageComponent.addParameters(feedbackLocale,
                    new LocalizedEmailParameter(ParameterType.RAW_VALUE, 0, uuid),
                    new LocalizedEmailParameter(ParameterType.RAW_VALUE, 1, recordUrl));
            }

            LocalizedEmail localizedEmail = new LocalizedEmail(false);
            localizedEmail.addComponents(subjectComponent, messageComponent);

            String subject = localizedEmail.getParsedSubject(feedbackLocales);
            String message = localizedEmail.getParsedMessage(feedbackLocales);

            MailUtil.sendMail(adminEmails, subject, message, settingManager);

        } catch (Exception e) {
            Log.error(Geonet.DATA_MANAGER, "XPathEmailNotifier: error sending notification for record "
                + event.getMd().getUuid(), e);
        }
    }

    private boolean matchesXPathTrigger(Element xmlData) {
        for (String xpath : xpathsConfig.split(",")) {
            String trimmed = xpath.trim();
            if (StringUtils.isBlank(trimmed)) {
                continue;
            }
            try {
                if (!Xml.selectNodes(xmlData, trimmed, NAMESPACES).isEmpty()) {
                    return true;
                }
            } catch (JDOMException e) {
                Log.warning(Geonet.DATA_MANAGER, "XPathEmailNotifier: invalid XPath expression '" + trimmed + "': " + e.getMessage());
            }
        }
        return false;
    }
}
