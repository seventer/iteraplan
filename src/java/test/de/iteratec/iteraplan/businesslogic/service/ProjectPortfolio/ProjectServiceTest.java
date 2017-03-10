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
package de.iteratec.iteraplan.businesslogic.service.ProjectPortfolio;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;


/**
 * Test class for the the service interface {@link ProjectService}.
 */
public class ProjectServiceTest extends BaseTransactionalTestSupport {

  private static final String             TEST_PROJ_NAME_A = "Project-A";
  private static final String             TEST_PROJ_NAME_B = "Project-B";
  private static final String             TEST_DESCRIPTION = "testDescription";
  @Autowired
  private ProjectService                  projectService;
  @Autowired
  private InformationSystemReleaseService releaseService;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.ProjectServiceImpl#deleteEntity(de.iteratec.iteraplan.model.Project)}
   * The method tests if the deleteEntity() throws correctly an IteraplanBusinessException if the
   * user tries to delete the root element.
   */
  @Test
  public void testDeleteEntityCaseIteraplanBusinessException() throws Exception {
    try {
      // create data
      Project firstTestP = testDataHelper.createProject("firstTestProject", TEST_DESCRIPTION);
      Project secondTestP = testDataHelper.createProject("secondTestProject", TEST_DESCRIPTION);
      Project thirdTestP = testDataHelper.createProject("thirdTestProject", TEST_DESCRIPTION);
      commit();
      beginTransaction();

      firstTestP = projectService.loadObjectById(firstTestP.getId());
      secondTestP = projectService.loadObjectById(secondTestP.getId());
      thirdTestP = projectService.loadObjectById(thirdTestP.getId());

      Project root = projectService.getFirstElement();
      firstTestP.addParent(root);
      secondTestP.addParent(root);
      thirdTestP.addParent(root);

      projectService.saveOrUpdate(firstTestP);
      projectService.saveOrUpdate(secondTestP);
      projectService.saveOrUpdate(thirdTestP);
      commit();

      // delete rootISD
      beginTransaction();
      projectService.deleteEntity(projectService.getFirstElement());
      fail("Expected IteraplanBusinessException");
    } catch (IteraplanBusinessException e) {
      // do noting, it's OK
    }
  }

  @Test
  public void testDeleteProject() {

    // Create the data.
    InformationSystem informationSystem = testDataHelper.createInformationSystem("System");
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(informationSystem, "1.0");
    Project projectA = testDataHelper.createProject(TEST_PROJ_NAME_A, "-");
    Project projectB = testDataHelper.createProject(TEST_PROJ_NAME_B, "-");
    commit();
    beginTransaction();

    projectA = projectService.loadObjectById(projectA.getId());
    projectB = projectService.loadObjectById(projectB.getId());

    Project root = projectService.getFirstElement();
    projectA.addParent(root);
    projectB.addParent(projectA);
    projectService.saveOrUpdate(projectA);
    projectService.saveOrUpdate(projectB);
    testDataHelper.addIsrToProject(release, projectA);
    testDataHelper.addIsrToProject(release, projectB);
    commit();

    // Delete the project
    beginTransaction();
    Integer idA = projectA.getId();
    Integer idB = projectB.getId();
    projectA = projectService.loadObjectById(idA);
    projectService.deleteEntity(projectA);
    commit();

    // ASSERT.
    beginTransaction();

    // Both elements have been deleted.
    projectA = projectService.loadObjectByIdIfExists(idA);
    if (projectA != null) {
      fail();
    }

    projectB = projectService.loadObjectByIdIfExists(idB);
    if (projectB != null) {
      fail();
    }

    // The root element has got no more children.
    root = projectService.getFirstElement();
    assertEquals(0, root.getChildren().size());

    // The information system release has got no more associated projects.
    release = releaseService.loadObjectById(release.getId());
    assertEquals(0, release.getProjects().size());

    commit();
  }

  @Test
  public void testGetProjectsFiltered() {
    Project projectA = testDataHelper.createProject(TEST_PROJ_NAME_A, "-");
    Project projectB = testDataHelper.createProject(TEST_PROJ_NAME_B, "-");
    Project projectC = testDataHelper.createProject("Project-C", "-");
    Project projectD = testDataHelper.createProject("Project-D", "-");
    Project projectE = testDataHelper.createProject("Project-E", "-");
    commit();
    beginTransaction();

    projectA = projectService.loadObjectById(projectA.getId());
    projectB = projectService.loadObjectById(projectB.getId());
    projectC = projectService.loadObjectById(projectC.getId());
    projectD = projectService.loadObjectById(projectD.getId());
    projectE = projectService.loadObjectById(projectE.getId());

    Project root = projectService.getFirstElement();
    projectA.addParent(root);
    projectB.addParent(projectA);
    projectC.addParent(projectA);
    projectD.addParent(projectB);
    projectE.addParent(projectB);
    projectService.saveOrUpdate(projectA);
    projectService.saveOrUpdate(projectB);
    projectService.saveOrUpdate(projectC);
    projectService.saveOrUpdate(projectD);
    projectService.saveOrUpdate(projectE);
    commit();

    // ASSERT.
    beginTransaction();

    List<Project> l = projectService.getEntitiesFiltered(null, false);
    String expected = "Project-A, Project-A : Project-B, Project-A : Project-C, Project-A : Project-B : Project-D, Project-A : Project-B : Project-E, ";
    assertEquals(expected, listToNames(l));

    List<Project> elementsToExclude = Lists.newArrayList(projectD, projectE);
    l = projectService.getEntitiesFiltered(elementsToExclude, true);
    expected = "-, Project-A, Project-A : Project-B, Project-A : Project-C, ";
    assertEquals(expected, listToNames(l));

    commit();
  }

  @Test
  public void testGetAvailableChildren() {
    Project projectA = testDataHelper.createProject(TEST_PROJ_NAME_A, "-");
    Project projectB = testDataHelper.createProject(TEST_PROJ_NAME_B, "-");
    Project projectC = testDataHelper.createProject("Project-C", "-");
    Project projectD = testDataHelper.createProject("Project-D", "-");
    Project projectE = testDataHelper.createProject("Project-E", "-");
    Project projectF = testDataHelper.createProject("Project-F", "-");
    Project projectG = testDataHelper.createProject("Project-G", "-");
    Project projectH = testDataHelper.createProject("Project-H", "-");
    commit();
    beginTransaction();

    projectA = projectService.loadObjectById(projectA.getId());
    projectB = projectService.loadObjectById(projectB.getId());
    projectC = projectService.loadObjectById(projectC.getId());
    projectD = projectService.loadObjectById(projectD.getId());
    projectE = projectService.loadObjectById(projectE.getId());
    projectF = projectService.loadObjectById(projectF.getId());
    projectG = projectService.loadObjectById(projectG.getId());
    projectH = projectService.loadObjectById(projectH.getId());

    Project root = projectService.getFirstElement();
    projectA.addParent(root);
    projectB.addParent(projectA);
    projectC.addParent(projectA);
    projectD.addParent(projectB);
    projectE.addParent(projectB);
    projectF.addParent(projectE);
    projectG.addParent(projectF);
    projectH.addParent(projectF);
    projectService.saveOrUpdate(projectA);
    projectService.saveOrUpdate(projectB);
    projectService.saveOrUpdate(projectC);
    projectService.saveOrUpdate(projectD);
    projectService.saveOrUpdate(projectE);
    projectService.saveOrUpdate(projectF);
    projectService.saveOrUpdate(projectG);
    projectService.saveOrUpdate(projectH);
    commit();

    // ASSERT.
    beginTransaction();

    List<Project> list = projectService.getAvailableChildren(projectF, null);
    String expected = "Project-A : Project-B : Project-D, Project-A : Project-B : Project-E : Project-F : Project-G, Project-A : Project-B : Project-E : Project-F : Project-H, Project-A : Project-C, ";
    assertEquals(expected, listToNames(list));

    List<Project> elementsToExclude = new ArrayList<Project>();
    elementsToExclude.add(projectD);
    elementsToExclude.add(projectH);

    list = projectService.getAvailableChildren(projectF, elementsToExclude);
    assertEquals("Project-A : Project-B : Project-E : Project-F : Project-G, Project-A : Project-C, ", listToNames(list));

    list = projectService.getAvailableChildren(projectF, elementsToExclude);
    assertEquals("Project-A : Project-B : Project-E : Project-F : Project-G, Project-A : Project-C, ", listToNames(list));
  }

  @Test
  public void testGetAvailableParents() {
    Project projectA = testDataHelper.createProject(TEST_PROJ_NAME_A, "-");
    Project projectB = testDataHelper.createProject(TEST_PROJ_NAME_B, "-");
    Project projectC = testDataHelper.createProject("Project-C", "-");
    Project projectD = testDataHelper.createProject("Project-D", "-");
    Project projectE = testDataHelper.createProject("Project-E", "-");
    Project projectF = testDataHelper.createProject("Project-F", "-");
    Project projectG = testDataHelper.createProject("Project-G", "-");
    Project projectH = testDataHelper.createProject("Project-H", "-");
    commit();
    beginTransaction();

    projectA = projectService.loadObjectById(projectA.getId());
    projectB = projectService.loadObjectById(projectB.getId());
    projectC = projectService.loadObjectById(projectC.getId());
    projectD = projectService.loadObjectById(projectD.getId());
    projectE = projectService.loadObjectById(projectE.getId());
    projectF = projectService.loadObjectById(projectF.getId());
    projectG = projectService.loadObjectById(projectG.getId());
    projectH = projectService.loadObjectById(projectH.getId());

    Project root = projectService.getFirstElement();
    projectA.addParent(root);
    projectB.addParent(projectA);
    projectC.addParent(projectA);
    projectD.addParent(projectB);
    projectE.addParent(projectB);
    projectF.addParent(projectE);
    projectG.addParent(projectF);
    projectH.addParent(projectF);
    projectService.saveOrUpdate(projectA);
    projectService.saveOrUpdate(projectB);
    projectService.saveOrUpdate(projectC);
    projectService.saveOrUpdate(projectD);
    projectService.saveOrUpdate(projectE);
    projectService.saveOrUpdate(projectF);
    projectService.saveOrUpdate(projectG);
    projectService.saveOrUpdate(projectH);
    commit();

    // ASSERT.
    beginTransaction();

    List<Project> list = projectService.getAvailableParents(projectE.getId());
    String expected = "-, Project-A, Project-A : Project-B, Project-A : Project-B : Project-D, Project-A : Project-C, ";
    assertEquals(expected, listToNames(list));
  }

  private String listToNames(List<Project> list) {
    StringBuilder sb = new StringBuilder();
    for (Project project : list) {
      sb.append(project.getHierarchicalName()).append(", ");
    }

    return sb.toString();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.ProjectServiceImpl#getProjectsBySearch(de.iteratec.iteraplan.model.Project)}
   * The method tests if the getProjectBySearch() returns correct list with Projects.
   */
  @Test
  public void testGetProjectBySearch() {
    Project testP = testDataHelper.createProject("testProject", TEST_DESCRIPTION);
    commit();

    beginTransaction();
    // get Unit by search
    // search for Project
    List<Project> actualList = projectService.getEntityResultsBySearch(testP);
    commit();

    List<Project> expected = arrayList();
    expected.add(testP);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.ProjectServiceImpl#getProjectById(java.lang.Integer)}
   * The method tests if the getProjectById() returns correct Project with a given id.
   */
  @Test
  public void testGetProjectById() {
    Project testP = testDataHelper.createProject("testProject", TEST_DESCRIPTION);
    commit();

    beginTransaction();
    Project actual = projectService.loadObjectById(testP.getId());
    assertEquals(testP, actual);
  }

  /**
   * Tests the method {@link ProjectService#findByNames(java.util.Set)}
   */
  @Test
  public void testFindByNames() {
    Project projectA = testDataHelper.createProject(TEST_PROJ_NAME_A, "-");
    Project projectB = testDataHelper.createProject(TEST_PROJ_NAME_B, "-");
    testDataHelper.createProject("Project-C", "-");
    commit();
    beginTransaction();

    List<Project> foundProjects = projectService.findByNames(Sets.newHashSet(TEST_PROJ_NAME_A, TEST_PROJ_NAME_B, "nonExisting"));
    assertEquals(2, foundProjects.size());
    assertEquals(Lists.newArrayList(projectA, projectB), foundProjects);
  }
}