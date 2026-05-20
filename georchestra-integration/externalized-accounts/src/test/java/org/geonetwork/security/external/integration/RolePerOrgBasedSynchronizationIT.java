// java
package org.geonetwork.security.external.integration;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.assertj.core.util.Sets;
import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.User;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.UserLink;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

@Transactional
@DirtiesContext
public class RolePerOrgBasedSynchronizationIT extends AbstractAccountsReconcilingServiceIntegrationTest {

    @Before
    public void setUp_SetSyncModeToRolePerOrg() {
        support.setRolePerOrgSyncMode();
    }


    @Test
    public void Synchronize_on_empty_geonetwork_db_creates_all_users_and_groups_from_orgs() {
        List<CanonicalUser> users = super.defaultUsers;
        List<CanonicalGroup> orgs = super.defaultGroups;

        assertEquals(0, support.gnUserRepository.count());
        assertEquals(0, support.gnGroupRepository.count());

        service.synchronize();
        verify(users, orgs);
    }

    @Test
    public void RolePerOrg_user_with_prefixed_role_maps_to_org_group() {
        List<CanonicalGroup> orgGroups = super.defaultGroups;
        CanonicalGroup org = orgGroups.get(0); // just to be explicit
        CanonicalGroup role = super.createRole("PSC:GN_REVIEWER"); // role name in external repo
        List<CanonicalGroup> orgs = new ArrayList<>(super.defaultGroups);

        // Create a user that belongs to organization "PSC" and has role "PSC:GN_REVIEWER"
        CanonicalUser u = super.setUpNewUser("prefixed", org, role);

        when(canonicalAccountsRepositoryMock.findAllOrganizations()).thenReturn(orgs);
        when(canonicalAccountsRepositoryMock.findAllUsers()).thenReturn(List.of(u));

        service.synchronize();

        // verify group link exists for the organization and user is linked to it
        //support.assertGroupLinkassertUserLink(org);
        UserLink link = support.assertUserLink(u);
        support.assertGroup(link.getInternalUser(), org);
    }


    private void verify(List<CanonicalUser> expectedUsers, List<CanonicalGroup> expectedOrgs) {
        assertEquals(expectedOrgs.size(), support.groupLinkRepository.findAll().size());
        assertEquals(expectedUsers.size(), support.userLinkRepository.findAll().size());

        for (CanonicalGroup expected : expectedOrgs) {
            support.assertGroupLink(expected);
        }
        for (CanonicalUser expected : expectedUsers) {
            support.assertUserLink(expected);
        }
    }
}
