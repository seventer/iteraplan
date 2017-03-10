/*
 * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2004 - 2014 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact iteratec GmbH headquarters at Inselkammerstr. 4
 * 82008 Munich - Unterhaching, Germany, or at email address info@iteratec.de.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "iteraplan" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by iteraplan".
 */
package de.iteratec.iteraplan.businesslogic.exchange.xmi.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;


/**
 * Integration test for {@link XmiImportServiceImpl} class.
 *
 */
public class XmiImportServiceImplTest extends BaseTransactionalTestSupport {

  @Autowired
  private XmiImportService xmiDeserializer;
  @Autowired
  private ProjectService   projectService;

  public void setXmiDeserializer(XmiImportService xmiDeserializer) {
    this.xmiDeserializer = xmiDeserializer;
  }

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public XmiImportServiceImplTest() {
    setInsertDataOnSetUp(false);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiImportServiceImpl#importXmi(javax.servlet.http.HttpServletResponse)}.
   * @throws IOException if the import file will be not found
   */
  @Test
  public void testSaveObjects() throws IOException {
    createTempUserContext();

    Resource importFile = new ClassPathResource("xmi/iteraplanData.xmi");
    xmiDeserializer.importXmi(importFile.getInputStream());

    //test some entities, if they were imported
    assertNotNull(projectService.getFirstElement());
    List<Project> projects = projectService.loadElementList();
    assertEquals(18, projects.size());

    List<Project> filteredProjects = projectService.findByNames(Sets.newHashSet("Consolidation of banking core"));
    assertEquals(1, filteredProjects.size());
    Project project = filteredProjects.get(0);
    RuntimePeriod runtimePeriod = project.getRuntimePeriodNullSafe();
    assertEquals(new LocalDate(2011, 2, 1).toDateMidnight().toDate(), runtimePeriod.getStart());
    assertEquals(new LocalDate(2013, 3, 31).toDateMidnight().toDate(), runtimePeriod.getEnd());

    String systemSize = project.getAttributeValue("Costs", Locale.UK);
    assertEquals("100.00", systemSize);
    String accountability = project.getAttributeValue("Accountability", Locale.UK);
    assertEquals("joe", accountability);
    String strategicDrivers = project.getAttributeValue("Strategic drivers", Locale.UK);
    assertEquals("operational", strategicDrivers);

    Set<InformationSystemRelease> isrs = project.getInformationSystemReleases();
    assertEquals(1, isrs.size());
    InformationSystemRelease isr = Iterables.get(isrs, 0);
    assertEquals("Funds txs # r13", isr.getName());
  }

  @Test
  public void testImportInitialData() throws IOException {
    createTempUserContext();

    Resource importFile = new ClassPathResource("xmi/iteraplanData.xmi");
    xmiDeserializer.importInitialXmi(importFile.getInputStream(), true);

    assertNotNull(projectService.getFirstElement());
  }

  private void createTempUserContext() {
    User user = new User();
    user.setLoginName("XmiImporter");

    Role role = new Role();
    role.setRoleName(Role.SUPERVISOR_ROLE_NAME);

    Set<Role> roles = Sets.newHashSet(role);
    UserContext userContext = new UserContext("XmiImporter", roles, Locale.UK, user);
    UserContext.setCurrentUserContext(userContext);
  }

}
