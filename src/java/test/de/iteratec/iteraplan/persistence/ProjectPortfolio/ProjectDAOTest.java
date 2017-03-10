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
package de.iteratec.iteraplan.persistence.ProjectPortfolio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Locale;

import org.hibernate.LazyInitializationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.persistence.dao.ProjectDAO;


public class ProjectDAOTest extends BaseTransactionalTestSupport {

  @Autowired
  private ProjectDAO     projectDao;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testUpdate() {
    Date startDate = DateUtils.parseAsDate("01.01.2010", Locale.GERMAN);
    Date endDate = DateUtils.parseAsDate("01.01.2011", Locale.GERMAN);

    Project project = testDataHelper.createProject("Project", "-", startDate, endDate);
    Project parent = testDataHelper.createProject("Parent", "-", startDate, endDate);
    project.addParent(parent);
    commit();

    beginTransaction();
    projectService.saveOrUpdate(project);
    commit();

    beginTransaction();
    assertNotNull(project);
    assertEquals("Project", project.getName());
    assertEquals("-", project.getDescription());
    assertEquals(startDate, project.runtimeStartsAt());
    assertEquals(endDate, project.runtimeEndsAt());
    assertEquals("Parent", project.getParent().getName());
    commit();
  }

  @Test
  public void testProjectWithIsReleases() {
    // create test data and save to database
    InformationSystem is = testDataHelper.createInformationSystem("A");
    testDataHelper.createInformationSystemRelease(is, "1.0", null, null, null, null);
    InformationSystemRelease isRelease = testDataHelper.createInformationSystemRelease(is, "2.0", null, null, null, null);
    Project project = testDataHelper.createProject("TASK-A", "desc TASK-A");
    assertEquals(0, project.getInformationSystemReleases().size());
    testDataHelper.addIsrToProject(isRelease, project);
    assertEquals(1, project.getInformationSystemReleases().size());
    commit();

    // begin new transaction
    beginTransaction();

    // get project back from DB WITHOUT IS Releases
    Project project3 = projectDao.loadObjectById(project.getId());
    // end transaction
    commit();

    try {
      project3.getInformationSystemReleases().size();
      fail();
    } catch (LazyInitializationException e) {
      // LazyInitializationException wird erwartet, da informationSystemReleases nicht mit geladen wurden; 
      // falls sie nicht kommt, dann fail()
    }
    // begin new transaction
    beginTransaction();

    // get project back from DB WITH IS releases
    Project project2 = projectDao.loadObjectById(project.getId(), new String[] { "informationSystemReleases" });
    commit();

    assertEquals(1, project2.getInformationSystemReleases().size());
  }
}