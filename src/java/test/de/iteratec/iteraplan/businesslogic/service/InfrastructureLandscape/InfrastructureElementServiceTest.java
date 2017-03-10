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
package de.iteratec.iteraplan.businesslogic.service.InfrastructureLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Class for testing the service methods of the {@link InfrastructureElementService} interface.
 * 
 * @author mma
 */
public class InfrastructureElementServiceTest extends BaseTransactionalTestSupport {
  private static final String              TEST_IE_NAME_A   = "Infra-A";
  private static final String              TEST_IE_NAME_B   = "Infra-B";
  private static final String              TEST_IE_NAME_C   = "Infra-C";
  private static final String              TEST_DESCRIPTION = "testDescription";

  @Autowired
  private InfrastructureElementService     infrastructureElementService;
  @Autowired
  private InformationSystemReleaseService  informationSystemReleaseService;
  @Autowired
  private TechnicalComponentReleaseService technicalComponentReleaseService;
  @Autowired
  private TestDataHelper2                  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InfrastructureElementServiceImpl#deleteEntity(de.iteratec.iteraplan.model.InfrastructureElement)}
   * The method tests if the deleteEntity() throws correctly an IteraplanBusinessException if the
   * user tries to delete the root element.
   */
  @Test
  public void testDeleteEntityCaseIteraplanBusinessException() throws Exception {
    try {
      // create data
      InfrastructureElement firstTestP = testDataHelper.createInfrastructureElement("firstTestInfrastructureElement", TEST_DESCRIPTION);
      InfrastructureElement secondTestP = testDataHelper.createInfrastructureElement("secondTestInfrastructureElement", TEST_DESCRIPTION);
      InfrastructureElement thirdTestP = testDataHelper.createInfrastructureElement("thirdTestInfrastructureElement", TEST_DESCRIPTION);
      commit();
      beginTransaction();

      firstTestP = infrastructureElementService.loadObjectById(firstTestP.getId());
      secondTestP = infrastructureElementService.loadObjectById(secondTestP.getId());
      thirdTestP = infrastructureElementService.loadObjectById(thirdTestP.getId());

      InfrastructureElement root = infrastructureElementService.getFirstElement();
      firstTestP.addParent(root);
      secondTestP.addParent(root);
      thirdTestP.addParent(root);

      infrastructureElementService.saveOrUpdate(firstTestP);
      infrastructureElementService.saveOrUpdate(secondTestP);
      infrastructureElementService.saveOrUpdate(thirdTestP);
      commit();

      // delete rootISD
      beginTransaction();
      infrastructureElementService.deleteEntity(infrastructureElementService.getFirstElement());
      fail("Expected IteraplanBusinessException");
    } catch (IteraplanBusinessException e) {
      // do noting, it's OK
    }
  }

  @Test
  public void testSaveEntity() {
    InformationSystem informationSystem = testDataHelper.createInformationSystem("System");
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(informationSystem, "1.0");
    TechnicalComponent technicalComponent = testDataHelper.createTechnicalComponent("TechComp", true, true);
    TechnicalComponentRelease techRelease = testDataHelper.createTCRelease(technicalComponent, "1.1", true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_IE_NAME_A, "desc");
    commit();
    beginTransaction();

    ie = infrastructureElementService.loadObjectById(ie.getId());

    InfrastructureElement root = infrastructureElementService.getFirstElement();
    ie.addParent(root);
    infrastructureElementService.saveOrUpdate(ie);
    testDataHelper.addIeToIsr(release, ie);
    testDataHelper.addIeToTcr(techRelease, ie);
    commit();

    Integer id = ie.getId();

    beginTransaction();

    ie = infrastructureElementService.loadObjectByIdIfExists(id);
    assertNotNull(ie);

    // The root element has a child.
    root = infrastructureElementService.getFirstElement();
    assertEquals(1, root.getChildren().size());
    assertEquals(TEST_IE_NAME_A, ie.getName());

    // check the technical component release
    Set<TechnicalComponentRelease> expTCRSet = ie.getTechnicalComponentReleases();
    assertNotNull(expTCRSet);
    assertEquals(1, expTCRSet.size());
    assertEquals("TechComp # 1.1", expTCRSet.iterator().next().getName());
    assertEquals("1.1", expTCRSet.iterator().next().getVersion());

    // check the information system release
    Set<InformationSystemRelease> expISRSet = ie.getInformationSystemReleases();
    assertNotNull(expISRSet);
    assertEquals(1, expISRSet.size());
    assertEquals("System # 1.0", expISRSet.iterator().next().getName());
    assertEquals("1.0", expISRSet.iterator().next().getVersion());

    // The information system release has got an association to the current IE.
    release = informationSystemReleaseService.loadObjectById(release.getId());
    assertNotNull(release);
    Set<InfrastructureElement> expIESet = release.getInfrastructureElements();
    assertNotNull(expIESet);
    assertEquals(1, expIESet.size());
    assertEquals(TEST_IE_NAME_A, expIESet.iterator().next().getName());
    assertEquals("desc", expIESet.iterator().next().getDescription());

    // The technical component release has got an association to the current IE.
    techRelease = technicalComponentReleaseService.loadObjectById(techRelease.getId());
    assertNotNull(techRelease);
    Set<InfrastructureElement> expIESet2 = techRelease.getInfrastructureElements();
    assertNotNull(expIESet2);
    assertEquals(1, expIESet2.size());
    assertEquals(TEST_IE_NAME_A, expIESet2.iterator().next().getName());
    assertEquals("desc", expIESet2.iterator().next().getDescription());

    commit();
  }
  @Test
  public void testDeleteEntity() {
    // Create the data.
    InformationSystem informationSystem = testDataHelper.createInformationSystem("System");
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(informationSystem, "1.0");
    TechnicalComponent technicalComponent = testDataHelper.createTechnicalComponent("TechComp", true, true);
    TechnicalComponentRelease techRelease = testDataHelper.createTCRelease(technicalComponent, "1.1", true);
    InfrastructureElement itemA = testDataHelper.createInfrastructureElement(TEST_IE_NAME_A, "-");
    InfrastructureElement itemB = testDataHelper.createInfrastructureElement(TEST_IE_NAME_B, "-");
    InfrastructureElement itemC = testDataHelper.createInfrastructureElement(TEST_IE_NAME_C, "-");
    commit();
    beginTransaction();

    itemA = infrastructureElementService.loadObjectById(itemA.getId());
    itemB = infrastructureElementService.loadObjectById(itemB.getId());
    itemC = infrastructureElementService.loadObjectById(itemC.getId());

    InfrastructureElement root = infrastructureElementService.getFirstElement();
    itemA.addParent(root);
    itemB.addParent(itemA);
    itemC.addParent(itemB);
    infrastructureElementService.saveOrUpdate(itemA);
    infrastructureElementService.saveOrUpdate(itemB);
    infrastructureElementService.saveOrUpdate(itemC);
    testDataHelper.addIeToIsr(release, itemA);
    testDataHelper.addIeToIsr(release, itemB);
    testDataHelper.addIeToTcr(techRelease, itemA);
    testDataHelper.addIeToTcr(techRelease, itemB);

    commit();

    // Delete the configuration item.
    beginTransaction();
    Integer idA = itemA.getId();
    Integer idB = itemB.getId();
    itemA = infrastructureElementService.loadObjectById(idA);
    infrastructureElementService.deleteEntity(itemA);
    commit();

    // ASSERT.
    beginTransaction();

    // Both elements have been deleted.
    itemA = infrastructureElementService.loadObjectByIdIfExists(idA);
    if (itemA != null) {
      fail();
    }

    itemB = infrastructureElementService.loadObjectByIdIfExists(idB);
    if (itemB != null) {
      fail();
    }

    // The root element has got no more children.
    root = infrastructureElementService.getFirstElement();
    assertEquals(0, root.getChildren().size());

    // The information system release has got no more associated configuration items.
    release = informationSystemReleaseService.loadObjectById(release.getId());
    assertEquals(0, release.getInfrastructureElements().size());

    // The technical component release has got no more associated configuration items.
    techRelease = technicalComponentReleaseService.loadObjectById(techRelease.getId());
    assertEquals(0, techRelease.getInfrastructureElements().size());

    commit();
  }
  @Test
  public void testGetEntitiesFiltered() {
    // Create the data.
    InfrastructureElement itemA = testDataHelper.createInfrastructureElement(TEST_IE_NAME_A, "-");
    InfrastructureElement itemB = testDataHelper.createInfrastructureElement(TEST_IE_NAME_B, "-");
    InfrastructureElement itemC = testDataHelper.createInfrastructureElement("Infra-C", "-");
    InfrastructureElement itemD = testDataHelper.createInfrastructureElement("Infra-D", "-");
    InfrastructureElement itemE = testDataHelper.createInfrastructureElement("Infra-E", "-");
    commit();
    beginTransaction();

    itemA = infrastructureElementService.loadObjectById(itemA.getId());
    itemB = infrastructureElementService.loadObjectById(itemB.getId());
    itemC = infrastructureElementService.loadObjectById(itemC.getId());
    itemD = infrastructureElementService.loadObjectById(itemD.getId());
    itemE = infrastructureElementService.loadObjectById(itemE.getId());

    InfrastructureElement root = infrastructureElementService.getFirstElement();
    itemA.addParent(root);
    itemB.addParent(itemA);
    itemC.addParent(itemA);
    itemD.addParent(itemB);
    itemE.addParent(itemB);
    infrastructureElementService.saveOrUpdate(itemA);
    infrastructureElementService.saveOrUpdate(itemB);
    infrastructureElementService.saveOrUpdate(itemC);
    infrastructureElementService.saveOrUpdate(itemD);
    infrastructureElementService.saveOrUpdate(itemE);
    commit();

    // ASSERT.
    beginTransaction();

    List<InfrastructureElement> l = infrastructureElementService.getEntitiesFiltered(null, false);
    String expected = "Infra-A, Infra-A : Infra-B, Infra-A : Infra-C, Infra-A : Infra-B : Infra-D, Infra-A : Infra-B : Infra-E, ";
    assertEquals(expected, listToNames(l));

    List<InfrastructureElement> elementsToExclude = new ArrayList<InfrastructureElement>();
    elementsToExclude.add(itemD);
    elementsToExclude.add(itemE);

    l = infrastructureElementService.getEntitiesFiltered(elementsToExclude, true);
    expected = "-, Infra-A, Infra-A : Infra-B, Infra-A : Infra-C, ";
    assertEquals(expected, listToNames(l));

    commit();
  }
  @Test
  public void testGetAvailableChildren() {
    // Create the data.
    InfrastructureElement itemA = testDataHelper.createInfrastructureElement(TEST_IE_NAME_A, "-");
    InfrastructureElement itemB = testDataHelper.createInfrastructureElement(TEST_IE_NAME_B, "-");
    InfrastructureElement itemC = testDataHelper.createInfrastructureElement("Infra-C", "-");
    InfrastructureElement itemD = testDataHelper.createInfrastructureElement("Infra-D", "-");
    InfrastructureElement itemE = testDataHelper.createInfrastructureElement("Infra-E", "-");
    InfrastructureElement itemF = testDataHelper.createInfrastructureElement("Infra-F", "-");
    InfrastructureElement itemG = testDataHelper.createInfrastructureElement("Infra-G", "-");
    InfrastructureElement itemH = testDataHelper.createInfrastructureElement("Infra-H", "-");
    commit();
    beginTransaction();

    itemA = infrastructureElementService.loadObjectById(itemA.getId());
    itemB = infrastructureElementService.loadObjectById(itemB.getId());
    itemC = infrastructureElementService.loadObjectById(itemC.getId());
    itemD = infrastructureElementService.loadObjectById(itemD.getId());
    itemE = infrastructureElementService.loadObjectById(itemE.getId());
    itemF = infrastructureElementService.loadObjectById(itemF.getId());
    itemG = infrastructureElementService.loadObjectById(itemG.getId());
    itemH = infrastructureElementService.loadObjectById(itemH.getId());

    InfrastructureElement root = infrastructureElementService.getFirstElement();
    itemA.addParent(root);
    itemB.addParent(itemA);
    itemC.addParent(itemA);
    itemD.addParent(itemB);
    itemE.addParent(itemB);
    itemF.addParent(itemE);
    itemG.addParent(itemF);
    itemH.addParent(itemF);
    infrastructureElementService.saveOrUpdate(itemA);
    infrastructureElementService.saveOrUpdate(itemB);
    infrastructureElementService.saveOrUpdate(itemC);
    infrastructureElementService.saveOrUpdate(itemD);
    infrastructureElementService.saveOrUpdate(itemE);
    infrastructureElementService.saveOrUpdate(itemF);
    infrastructureElementService.saveOrUpdate(itemG);
    infrastructureElementService.saveOrUpdate(itemH);
    commit();

    // ASSERT.
    beginTransaction();

    List<InfrastructureElement> list = infrastructureElementService.getAvailableChildren(itemF, null);
    StringBuffer expected = new StringBuffer(150);
    expected
        .append("Infra-A : Infra-B : Infra-D, Infra-A : Infra-B : Infra-E : Infra-F : Infra-G, Infra-A : Infra-B : Infra-E : Infra-F : Infra-H, Infra-A : Infra-C, ");
    assertEquals(expected.toString(), listToNames(list));

    List<InfrastructureElement> elementsToExclude = new ArrayList<InfrastructureElement>();
    elementsToExclude.add(itemD);
    elementsToExclude.add(itemH);

    list = infrastructureElementService.getAvailableChildren(itemF, elementsToExclude);
    assertEquals("Infra-A : Infra-B : Infra-E : Infra-F : Infra-G, Infra-A : Infra-C, ", listToNames(list));

    list = infrastructureElementService.getAvailableChildren(itemF, elementsToExclude);
    assertEquals("Infra-A : Infra-B : Infra-E : Infra-F : Infra-G, Infra-A : Infra-C, ", listToNames(list));
  }
  @Test
  public void testGetAvailableParents() {
    // Create the data.
    InfrastructureElement itemA = testDataHelper.createInfrastructureElement(TEST_IE_NAME_A, "-");
    InfrastructureElement itemB = testDataHelper.createInfrastructureElement(TEST_IE_NAME_B, "-");
    InfrastructureElement itemC = testDataHelper.createInfrastructureElement("Infra-C", "-");
    InfrastructureElement itemD = testDataHelper.createInfrastructureElement("Infra-D", "-");
    InfrastructureElement itemE = testDataHelper.createInfrastructureElement("Infra-E", "-");
    InfrastructureElement itemF = testDataHelper.createInfrastructureElement("Infra-F", "-");
    InfrastructureElement itemG = testDataHelper.createInfrastructureElement("Infra-G", "-");
    InfrastructureElement itemH = testDataHelper.createInfrastructureElement("Infra-H", "-");
    commit();
    beginTransaction();

    itemA = infrastructureElementService.loadObjectById(itemA.getId());
    itemB = infrastructureElementService.loadObjectById(itemB.getId());
    itemC = infrastructureElementService.loadObjectById(itemC.getId());
    itemD = infrastructureElementService.loadObjectById(itemD.getId());
    itemE = infrastructureElementService.loadObjectById(itemE.getId());
    itemF = infrastructureElementService.loadObjectById(itemF.getId());
    itemG = infrastructureElementService.loadObjectById(itemG.getId());
    itemH = infrastructureElementService.loadObjectById(itemH.getId());

    InfrastructureElement root = infrastructureElementService.getFirstElement();
    itemA.addParent(root);
    itemB.addParent(itemA);
    itemC.addParent(itemA);
    itemD.addParent(itemB);
    itemE.addParent(itemB);
    itemF.addParent(itemE);
    itemG.addParent(itemF);
    itemH.addParent(itemF);
    infrastructureElementService.saveOrUpdate(itemA);
    infrastructureElementService.saveOrUpdate(itemB);
    infrastructureElementService.saveOrUpdate(itemC);
    infrastructureElementService.saveOrUpdate(itemD);
    infrastructureElementService.saveOrUpdate(itemE);
    infrastructureElementService.saveOrUpdate(itemF);
    infrastructureElementService.saveOrUpdate(itemG);
    infrastructureElementService.saveOrUpdate(itemH);
    commit();

    // ASSERT.
    beginTransaction();

    List<InfrastructureElement> list = infrastructureElementService.getAvailableParents(itemE.getId());
    StringBuffer expected = new StringBuffer("-, Infra-A, Infra-A : Infra-B, Infra-A : Infra-B : Infra-D, Infra-A : Infra-C, ");
    assertEquals(expected.toString(), listToNames(list));
  }

  private String listToNames(List<InfrastructureElement> list) {

    StringBuffer sb = new StringBuffer();
    for (InfrastructureElement item : list) {
      sb.append(item.getHierarchicalName()).append(", ");
    }

    return sb.toString();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InfrastructureElementServiceImpl#updateFromWorkingCopy(de.iteratec.iteraplan.model.InfrastructureElement, de.iteratec.iteraplan.model.InfrastructureElement)}
   */@Test
  public void testUpdateEntity() {
    // Create the data.
    InformationSystem informationSystem = testDataHelper.createInformationSystem("System");
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(informationSystem, "1.0");
    TechnicalComponent technicalComponent = testDataHelper.createTechnicalComponent("TechComp", true, true);
    TechnicalComponentRelease techRelease = testDataHelper.createTCRelease(technicalComponent, "1.1", true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_IE_NAME_A, "desc");
    commit();
    beginTransaction();

    ie = infrastructureElementService.loadObjectById(ie.getId());

    InfrastructureElement root = infrastructureElementService.getFirstElement();
    ie.addParent(root);
    infrastructureElementService.saveOrUpdate(ie);
    testDataHelper.addIeToIsr(release, ie);
    testDataHelper.addIeToTcr(techRelease, ie);
    commit();

    beginTransaction();

    ie.setName(TEST_IE_NAME_A + " changed");
    ie.setDescription("desc changed");

    InfrastructureElement savedIE = infrastructureElementService.saveOrUpdate(ie);
    assertEquals(TEST_IE_NAME_A + " changed", savedIE.getName());
    assertEquals("desc changed", savedIE.getDescription());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InfrastructureElementServiceImpl#getInfrastructureElementsBySearch(de.iteratec.iteraplan.model.InfrastructureElement, int, int)}
   * The method tests if the getInfrastructureElementsBySearch() returns correct list with
   * InfrastructureElements.
   */@Test
  public void testGetInfrastructureElementsBySearch() {
    InfrastructureElement testIE = testDataHelper.createInfrastructureElement("testIE", TEST_DESCRIPTION);
    commit();
    beginTransaction();

    testIE = infrastructureElementService.loadObjectById(testIE.getId());

    InfrastructureElement root = infrastructureElementService.getFirstElement();
    testIE.addParent(root);

    infrastructureElementService.saveOrUpdate(testIE);
    commit();

    beginTransaction();
    // get Object by search
    // search for ObjectA
    List<InfrastructureElement> actualList = infrastructureElementService.getEntityResultsBySearch(testIE);
    commit();

    List<InfrastructureElement> expected = arrayList();
    expected.add(testIE);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InfrastructureElementServiceImpl#getInfrastructureElementById(java.lang.Integer)}
   * The method tests if the getInfrastructureElementById() returns correct InfrastructureElement
   * with a given id.
   */@Test
  public void testGetInfrastructureElementById() {

    // create data
    InfrastructureElement testP = testDataHelper.createInfrastructureElement("testInfrastructureElement", TEST_DESCRIPTION);
    commit();

    beginTransaction();
    InfrastructureElement actual = infrastructureElementService.loadObjectById(testP.getId());
    assertEquals(testP, actual);
  }
}
