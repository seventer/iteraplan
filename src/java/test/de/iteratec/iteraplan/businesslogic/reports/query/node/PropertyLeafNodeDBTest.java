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
package de.iteratec.iteraplan.businesslogic.reports.query.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.businesslogic.reports.query.type.ArchitecturalDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessMappingTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessProcessTypeQ;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessUnitQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemInterfaceTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.persistence.dao.BusinessUnitDAO;
import de.iteratec.iteraplan.persistence.dao.ProductDAO;


/**
 * Test class for queries regarding simple properties of building block instances.
 */
public class PropertyLeafNodeDBTest extends AbstractNodeTestBase {

  private static final Logger LOGGER                     = Logger.getIteraplanLogger(PropertyLeafNodeDBTest.class);

  private static final String STANDARD_START_DATE_2005   = "1.1.2005";
  private static final String STANDARD_START_DATE_2005_B = "1.7.2005";
  private static final String STANDARD_START_DATE_2000   = "1.1.2000";
  private static final String STANDARD_START_DATE_2000_B = "1.4.2000";
  private static final String STANDARD_END_DATE_2005     = "31.12.2005";
  private static final String STANDARD_END_DATE_2005_B   = "30.9.2005";
  private static final String STANDARD_END_DATE_2000     = "31.12.2001";
  private static final String STANDARD_END_DATE_2000_B   = "30.6.2000";

  private static final String WRONG_ELEMENT_NR_ERROR_MSG = "Wrong number of elements in result set!";
  private static final String WRONG_ELEMENT_ERROR_MSG    = "Wrong element found!";
  private static final String TEST_DESCRIPTION           = "testDescription";

  @Autowired
  private ProductDAO          productDAO;
  @Autowired
  private BusinessUnitDAO     businessUnitDAO;
  @Autowired
  private TestDataHelper2     testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testIsReleasePropsCorrect() throws Exception {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);

    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.PLANNED);

    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B,
        STANDARD_END_DATE_2005_B, InformationSystemRelease.TypeOfStatus.PLANNED);

    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    commit();
    beginTransaction();

    // act and assert:

    Type<InformationSystemRelease> isrType = InformationSystemReleaseTypeQu.getInstance();

    doLeafTest(isrType);

    LOGGER.debug("lastModificationTime");
    doALeafLoopTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_LAST_MODIFICATION_DATE, Comparator.GEQ, "01.01.2005", 3,
        BBAttribute.FIXED_ATTRIBUTE_DATETYPE);
    LOGGER.debug("lastModificationTime");
    commit();
  }

  @Test
  public void testConnectionPropsCorrect() throws Exception {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, STANDARD_END_DATE_2005_B,
        InformationSystemRelease.TypeOfStatus.PLANNED);

    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    InformationSystem j = testDataHelper.createInformationSystem("J");
    InformationSystemRelease j1 = testDataHelper.createInformationSystemRelease(j, "j1", TEST_DESCRIPTION, "1.1.2006", "31.12.2006",
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease j2 = testDataHelper.createInformationSystemRelease(j, "j2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B, null,
        InformationSystemRelease.TypeOfStatus.TARGET);
    InformationSystemRelease j3 = testDataHelper.createInformationSystemRelease(j, "j3", TEST_DESCRIPTION, null, STANDARD_END_DATE_2005_B,
        InformationSystemRelease.TypeOfStatus.TARGET);

    testDataHelper.addChildToIsr(j1, j2);
    testDataHelper.addChildToIsr(j2, j3);

    testDataHelper.createInformationSystemInterface(i1, j3, null, "c1 desc");
    testDataHelper.createInformationSystemInterface(i2, i3, null, "c2");
    testDataHelper.createInformationSystemInterface(i2, j2, null, "c3 desc");

    commit();

    // act and assert:
    beginTransaction();
    Type<InformationSystemInterface> interfaceType = InformationSystemInterfaceTypeQu.getInstance();
    doALeafFixedTypeTest(interfaceType, null, InformationSystemInterfaceTypeQu.PROPERTY_DESCRIPTION, Comparator.LIKE, "*es*", 2);

    commit();
  }

  @Test
  public void testTcReleasePropsCorrect() throws Exception {
    TechnicalComponent t = testDataHelper.createTechnicalComponent("T", false, true);
    testDataHelper.createTCRelease(t, "t1", TEST_DESCRIPTION, null, null, TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    testDataHelper.createTCRelease(t, "t2", TEST_DESCRIPTION, STANDARD_START_DATE_2000, "31.12.2007", TechnicalComponentRelease.TypeOfStatus.PLANNED,
        true);
    testDataHelper.createTCRelease(t, "t3", TEST_DESCRIPTION, "1.1.2007", null, TechnicalComponentRelease.TypeOfStatus.TARGET, true);
    testDataHelper.createTCRelease(t, "t4", TEST_DESCRIPTION, null, STANDARD_END_DATE_2000, TechnicalComponentRelease.TypeOfStatus.INACTIVE, true);

    commit();

    // act and assert:
    beginTransaction();
    Type<TechnicalComponentRelease> tcrType = TechnicalComponentReleaseTypeQu.getInstance();

    // act: start date query    
    doALeafDateTest(tcrType, null, TechnicalComponentReleaseTypeQu.PROPERTY_ENDDATE, Comparator.GEQ, STANDARD_START_DATE_2005, 3);

    // act: start date query    
    doALeafDateTest(tcrType, null, TechnicalComponentReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ, "1.1.2010", 2);

    // act: end date query    
    doALeafDateTest(tcrType, null, TechnicalComponentReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ, STANDARD_START_DATE_2005, 3);

    // act: end date query    
    doALeafDateTest(tcrType, null, TechnicalComponentReleaseTypeQu.PROPERTY_ENDDATE, Comparator.GEQ, "1.1.2001", 3);

    commit();
  }

  @Test
  public void testIsReleaseInformationSystemDomainPropsCorrect() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, STANDARD_END_DATE_2005_B,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.createInformationSystemRelease(i, "i4", TEST_DESCRIPTION, null, null, InformationSystemRelease.TypeOfStatus.INACTIVE);
    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    InformationSystemDomain d1 = testDataHelper.createInformationSystemDomain("d1", "d1 desc");
    InformationSystemDomain d2 = testDataHelper.createInformationSystemDomain("d2", "d2 desc");

    testDataHelper.addIsrToIsd(i1, d1);
    testDataHelper.addIsrToIsd(i2, d1);
    testDataHelper.addIsrToIsd(i3, d2);

    commit();

    // act and assert:
    beginTransaction();

    Type<InformationSystemRelease> isrType = InformationSystemReleaseTypeQu.getInstance();
    Type<InformationSystemDomain> isdType = InformationSystemDomainTypeQu.getInstance();
    Extension ex = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_INFORMATIONSYSTEMDOMAIN);
    ExtensionNode root = new ExtensionNode(isrType, ex);

    LOGGER.debug("act & assert: simple LIKE d1 name query");
    doATreeFixedTypeTest(isdType, null, root, InformationSystemDomainTypeQu.PROPERTY_NAME, Comparator.LIKE, "d1", 2);

    LOGGER.debug("act & assert: simple NOT LIKE d1 name query");
    doATreeFixedTypeTest(isdType, null, root, InformationSystemDomainTypeQu.PROPERTY_NAME, Comparator.NOT_LIKE, "d1", 1);

    LOGGER.debug("act & assert: simple LIKE '' name query");
    doATreeFixedTypeTest(isdType, null, root, InformationSystemDomainTypeQu.PROPERTY_NAME, Comparator.LIKE, "", 0);

    LOGGER.debug("act & assert: simple NOT LIKE '' name query");
    doATreeFixedTypeTest(isdType, null, root, InformationSystemDomainTypeQu.PROPERTY_NAME, Comparator.NOT_LIKE, "", 3);

    commit();
  }

  @Test
  public void testIsReleaseProjectPropsCorrect() throws Exception {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B,
        STANDARD_END_DATE_2005_B, InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    InformationSystem j = testDataHelper.createInformationSystem("J");
    InformationSystemRelease j1 = testDataHelper.createInformationSystemRelease(j, "j1", TEST_DESCRIPTION, STANDARD_START_DATE_2000, "31.12.2001",
        InformationSystemRelease.TypeOfStatus.INACTIVE);

    Project proj1 = testDataHelper.createProject("proj1", "proj1 desc", STANDARD_START_DATE_2000, STANDARD_END_DATE_2000);
    Project proj2 = testDataHelper.createProject("proj2", "proj2 desc", STANDARD_START_DATE_2000, STANDARD_END_DATE_2000_B);
    Project proj3 = testDataHelper.createProject("proj3", "proj3 desc", STANDARD_START_DATE_2000_B, STANDARD_END_DATE_2000_B);
    testDataHelper.createProject("proj4", "proj4 desc", "", "");
    testDataHelper.addElementOf(proj1, proj2);
    testDataHelper.addElementOf(proj2, proj3);

    testDataHelper.addIsrToProject(i2, proj1);
    testDataHelper.addIsrToProject(j1, proj2);
    testDataHelper.addIsrToProject(i3, proj3);

    commit();
    // act and assert:
    beginTransaction();
    Type<InformationSystemRelease> isrType = InformationSystemReleaseTypeQu.getInstance();
    Type<Project> projectType = ProjectQueryType.getInstance();
    Extension ex = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_PROJECTS);
    ExtensionNode root = new ExtensionNode(isrType, ex);

    LOGGER.debug("act & assert: simple LIKE proj* name query");
    doATreeFixedTypeTest(projectType, null, root, ProjectQueryType.PROPERTY_NAME, Comparator.LIKE, "proj*", 3);

    LOGGER.debug("act & assert: simple NOT LIKE proj1 name query");
    doATreeFixedTypeTest(projectType, null, root, ProjectQueryType.PROPERTY_NAME, Comparator.NOT_LIKE, "proj1", 2);

    LOGGER.debug("act & assert: simple LIKE '' name query");
    doATreeFixedTypeTest(projectType, null, root, ProjectQueryType.PROPERTY_NAME, Comparator.LIKE, "", 0);

    LOGGER.debug("act & assert: simple NOT LIKE '' name query");
    doATreeFixedTypeTest(projectType, null, root, ProjectQueryType.PROPERTY_NAME, Comparator.NOT_LIKE, "", 3);

    // act: start date query    
    doATreeDateTest(projectType, null, root, ProjectQueryType.PROPERTY_STARTDATE, Comparator.LEQ, "31.08.2000", 1);

    // act: start date query 2    
    doATreeDateTest(projectType, null, root, ProjectQueryType.PROPERTY_ENDDATE, Comparator.GEQ, "31.04.2000", 3);

    // act: start date query 3    
    doATreeDateTest(projectType, null, root, ProjectQueryType.PROPERTY_STARTDATE, Comparator.LEQ, "01.01.2010", 0);

    // act: end date query    
    doATreeDateTest(projectType, null, root, ProjectQueryType.PROPERTY_ENDDATE, Comparator.GEQ, "31.08.2000", 3);

    // act: end date query 2    
    doALeafDateTest(isrType, ex, ProjectQueryType.PROPERTY_STARTDATE, Comparator.LEQ, "31.04.2000", 3);

    // act: end date query 3    
    doALeafDateTest(isrType, ex, ProjectQueryType.PROPERTY_ENDDATE, Comparator.GEQ, "01.01.1990", 0);

    commit();

  }

  @Test
  public void testProjectIsReleasePropsCorrect() throws Exception {
    Project proj1 = testDataHelper.createProject("proj1", "proj1 desc", STANDARD_START_DATE_2000, STANDARD_END_DATE_2000);
    Project proj2 = testDataHelper.createProject("proj2", "proj2 desc", STANDARD_START_DATE_2000, STANDARD_END_DATE_2000_B);
    Project proj3 = testDataHelper.createProject("proj3", "proj3 desc", STANDARD_START_DATE_2000_B, STANDARD_END_DATE_2000_B);
    testDataHelper.createProject("proj4", "proj3 desc");
    testDataHelper.addElementOf(proj1, proj2);
    testDataHelper.addElementOf(proj2, proj3);

    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B,
        STANDARD_END_DATE_2005_B, InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    testDataHelper.addIsrToProject(i2, proj1);
    testDataHelper.addIsrToProject(i3, proj3);

    commit();

    // act and assert:
    beginTransaction();

    Type<Project> projectType = ProjectQueryType.getInstance();
    Type<InformationSystemRelease> isrType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = projectType.getExtension(ProjectQueryType.EXTENSION_INFORMATIONSYSTEMRELEASES);
    ExtensionNode root = new ExtensionNode(projectType, ex);

    // act: simple name query
    doATreeFixedTypeTest(isrType, null, root, InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.LIKE, "i*", 2);

    // act: start date query    
    doATreeDateTest(isrType, null, root, InformationSystemReleaseTypeQu.PROPERTY_ENDDATE, Comparator.GEQ, "31.08.2005", 2);

    // act: start date query 2        
    doATreeDateTest(isrType, null, root, InformationSystemReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ, "01.11.2005", 1);

    // act: start date query 3        
    doATreeDateTest(isrType, null, root, InformationSystemReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ, "01.01.2010", 0);

    // act: end date query
    doATreeDateTest(isrType, null, root, InformationSystemReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ, "31.08.2005", 2);

    // act: end date query 2        
    doATreeDateTest(isrType, null, root, InformationSystemReleaseTypeQu.PROPERTY_ENDDATE, Comparator.GEQ, "31.04.2005", 0);

    // act: end date query 3    
    doATreeDateTest(isrType, null, root, InformationSystemReleaseTypeQu.PROPERTY_ENDDATE, Comparator.GEQ, "01.01.1990", 0);

    commit();
  }

  @Test
  public void testIsReleaseTcReleaseArchitecturalDomainPropsCorrect() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);

    TechnicalComponent t = testDataHelper.createTechnicalComponent("T", true, true);
    TechnicalComponentRelease t1 = testDataHelper.createTCRelease(t, "t1", "t1 desc", STANDARD_START_DATE_2005, STANDARD_END_DATE_2005,
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);

    ArchitecturalDomain a1 = testDataHelper.createArchitecturalDomain("a1", "a1 desc");

    testDataHelper.addADToTCRelease(t1, a1);
    testDataHelper.addTcrToIsr(i1, t1);

    commit();

    // act and assert:
    beginTransaction();

    Type<InformationSystemRelease> isrType = InformationSystemReleaseTypeQu.getInstance();
    Type<ArchitecturalDomain> adType = ArchitecturalDomainTypeQu.getInstance();
    Extension ex = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_ARCHITECTURALDOMAINS_VIA_TECHNICALCOMPONENT);
    ExtensionNode root = new ExtensionNode(isrType, ex);

    // act: simple description query    
    doATreeFixedTypeTest(adType, null, root, InformationSystemReleaseTypeQu.PROPERTY_DESCRIPTION, Comparator.LIKE, "a*", 1);

    commit();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testIsReleaseBusinessMappingPropsCorrect() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, null, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, null,
        InformationSystemRelease.TypeOfStatus.TARGET);

    BusinessProcess bp1 = testDataHelper.createBusinessProcess("bp1", "bp1 desc");
    BusinessProcess bp2 = testDataHelper.createBusinessProcess("bp2", "bp2 desc");
    testDataHelper.createBusinessProcess("bp3", "bp3 desc");

    BusinessUnit bu1 = testDataHelper.createBusinessUnit("bu1", "bu1 desc");
    BusinessUnit bu2 = testDataHelper.createBusinessUnit("bu2", "bu2 desc");
    testDataHelper.createBusinessUnit("bu3", "bu3 desc");

    Product prRoot = productDAO.getFirstElement();
    BusinessUnit buRoot = businessUnitDAO.getFirstElement();
    testDataHelper.createBusinessMapping(i1, bp1, bu1, prRoot);
    testDataHelper.createBusinessMapping(i1, bp1, bu2, prRoot);
    testDataHelper.createBusinessMapping(i2, bp1, buRoot, prRoot);
    testDataHelper.createBusinessMapping(i3, bp2, buRoot, prRoot);

    commit();

    // act and assert:
    beginTransaction();

    Type<InformationSystemRelease> isrType = InformationSystemReleaseTypeQu.getInstance();
    Type<BusinessMapping> bmType = BusinessMappingTypeQu.getInstance();
    Type<BusinessProcess> bpType = BusinessProcessTypeQ.getInstance();
    Type<BusinessUnit> buType = BusinessUnitQueryType.getInstance();

    Extension rootEx = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_BUSINESSMAPPINGS);
    ExtensionNode rootNode = new ExtensionNode(isrType, rootEx);

    Extension intermediaryBpEx = bmType.getExtension(BusinessMappingTypeQu.EXTENSION_BUSINESSPROCESS);
    ExtensionNode intermediaryBp = new ExtensionNode(bmType, intermediaryBpEx);
    AbstractLeafNode leafBp = new PropertyLeafNode(bpType, null, BusinessProcessTypeQ.PROPERTY_NAME, Comparator.LIKE, "bp1",
        BBAttribute.FIXED_ATTRIBUTE_TYPE);
    LOGGER.debug("leafBpResults: " + getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafBp));
    intermediaryBp.setChild(leafBp);
    Set<BuildingBlock> intermediaryBpResults = getQueryDAO().evaluateQueryTree(intermediaryBp);
    LOGGER.debug("intermediaryBpResults: " + intermediaryBpResults);

    Extension intermediaryBuEx = bmType.getExtension(BusinessMappingTypeQu.EXTENSION_BUSINESSUNIT);
    ExtensionNode intermediaryBu = new ExtensionNode(bmType, intermediaryBuEx);
    AbstractLeafNode leafBu = new PropertyLeafNode(buType, null, BusinessUnitQueryType.PROPERTY_NAME, Comparator.LIKE, "bu2",
        BBAttribute.FIXED_ATTRIBUTE_TYPE);
    LOGGER.debug("leafBuResults: " + getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafBu));
    intermediaryBu.setChild(leafBu);
    Set<BuildingBlock> intermediaryOuResults = getQueryDAO().evaluateQueryTree(intermediaryBu);
    LOGGER.debug("intermediaryBuResults: " + intermediaryOuResults);

    List<InformationSystemRelease> results;

    LOGGER.debug("only bp query");
    rootNode.setChild(intermediaryBp);
    results = new ArrayList(getQueryDAO().evaluateQueryTree(rootNode));
    LOGGER.debug("only bp query results:" + results);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());
    Collections.sort(results);
    assertEquals(WRONG_ELEMENT_ERROR_MSG, "i1", results.get(0).getVersion());
    assertEquals(WRONG_ELEMENT_ERROR_MSG, "i2", results.get(1).getVersion());

    LOGGER.debug("only bu query");
    rootNode.setChild(intermediaryBu);
    results = new ArrayList(getQueryDAO().evaluateQueryTree(rootNode));
    LOGGER.debug("only bu query results:" + results);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());
    assertEquals(WRONG_ELEMENT_ERROR_MSG, "i1", results.get(0).getVersion());

    LOGGER.debug("bp and bu query");
    OperationNode and = new OperationNode(Operation.AND);
    rootNode.setChild(and);
    and.addChild(intermediaryBp);
    and.addChild(intermediaryBu);
    results = new ArrayList(getQueryDAO().evaluateQueryTree(rootNode));
    LOGGER.debug("bp and bu query results:" + results);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());
    assertEquals(WRONG_ELEMENT_ERROR_MSG, "i1", results.get(0).getVersion());

    commit();
  }

  private void doLeafTest(Type<InformationSystemRelease> isrType) {
    LOGGER.debug("act & assert: start date query 1");
    doALeafDateTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_ENDDATE, Comparator.GEQ, "31.08.2005", 3);

    LOGGER.debug("act & assert: start date query 2");
    doALeafDateTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ, "01.01.2000", 3);

    LOGGER.debug("act & assert: start date query 3");
    doALeafDateTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ, "31.12.2010", 0);

    LOGGER.debug("act & assert: end date query 1");
    doALeafDateTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_ENDDATE, Comparator.GEQ, "01.03.2005", 1);

    LOGGER.debug("act & assert: typeofstatus query 1");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        InformationSystemRelease.TypeOfStatus.CURRENT, 1);

    LOGGER.debug("act & assert: typeofstatus query 2");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        InformationSystemRelease.TypeOfStatus.TARGET, 0);

    LOGGER.debug("act & assert: typeofstatus query 3");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        InformationSystemRelease.TypeOfStatus.PLANNED, 2);

    LOGGER.debug("act & assert: name query 1");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.LIKE, "I", 3);

    LOGGER.debug("act & assert: name query 2");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.NOT_LIKE, "I", 0);

    LOGGER.debug("act & assert: name query 3");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.LIKE, "J", 0);

    LOGGER.debug("act & assert: name query 4");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.NOT_LIKE, "J", 3);

    LOGGER.debug("act & assert: name query 5");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.LIKE, "", 0);

    LOGGER.debug("act & assert: name query 6");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.NOT_LIKE, "", 3);

    LOGGER.debug("act & assert: version query 1");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_VERSION, Comparator.LIKE, "i*", 3);

    LOGGER.debug("act & assert: version query 2");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_VERSION, Comparator.NOT_LIKE, "i*", 0);

    LOGGER.debug("act & assert: version query 3");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_VERSION, Comparator.LIKE, "j?", 0);

    LOGGER.debug("act & assert: version query 4");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_VERSION, Comparator.NOT_LIKE, "j?", 3);

    LOGGER.debug("act & assert: version query 5");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_VERSION, Comparator.LIKE, "", 0);

    LOGGER.debug("act & assert: version query 6");
    doALeafFixedTypeTest(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_VERSION, Comparator.NOT_LIKE, "", 3);

  }

  private void doALeafDateTest(Type<?> resultType, Extension extension, String property, Comparator comparator, String param, int expect) {
    Date theDate = null;
    try {
      theDate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY).parse(param);
    } catch (ParseException pex) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INCORRECT_DATE_FORMAT, pex);
    }
    doALeafTest(resultType, extension, property, comparator, theDate, expect, BBAttribute.FIXED_ATTRIBUTE_DATETYPE);

  }

  private void doALeafFixedTypeTest(Type<?> resultType, Extension extension, String property, Comparator comparator, Object param, int expect) {
    doALeafTest(resultType, extension, property, comparator, param, expect, BBAttribute.FIXED_ATTRIBUTE_TYPE);
  }

  private void doALeafTest(Type<?> resultType, Extension extension, String property, Comparator comparator, Object param, int expect, String attrType) {
    AbstractLeafNode leaf = new PropertyLeafNode(resultType, extension, property, comparator, param, attrType);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leaf);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, expect, results.size());
  }

  private void doALeafLoopTest(Type<?> resultType, Extension extension, String property, Comparator comparator, Object param, int expect,
                               String attrType) {
    AbstractLeafNode leaf = new PropertyLeafNode(resultType, extension, property, comparator, param, attrType);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leaf);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, expect, results.size());
    Calendar cal = GregorianCalendar.getInstance(Locale.GERMAN);
    cal.set(2005, 01, 01);
    for (BuildingBlock bb : results) {
      assertTrue((bb.getLastModificationTime().after(cal.getTime())));
    }
  }

  private void doATreeDateTest(Type<?> projectType, Extension extension, ExtensionNode root, String property, Comparator comparator, String param,
                               int expect) {
    Date theDate = null;
    try {
      theDate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY).parse(param);
    } catch (ParseException pex) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INCORRECT_DATE_FORMAT, pex);
    }
    doATreeTest(projectType, extension, root, property, comparator, theDate, expect, BBAttribute.FIXED_ATTRIBUTE_DATETYPE);
  }

  private void doATreeFixedTypeTest(Type<?> projectType, Extension extension, ExtensionNode root, String property, Comparator comparator,
                                    Object param, int expect) {
    doATreeTest(projectType, extension, root, property, comparator, param, expect, BBAttribute.FIXED_ATTRIBUTE_TYPE);
  }

  private void doATreeTest(Type<?> projectType, Extension extension, ExtensionNode root, String property, Comparator comparator, Object param,
                           int expect, String attrType) {
    AbstractLeafNode leaf = new PropertyLeafNode(projectType, extension, property, comparator, param, attrType);
    root.setChild(leaf);
    Set<BuildingBlock> results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, expect, results.size());
  }

}
