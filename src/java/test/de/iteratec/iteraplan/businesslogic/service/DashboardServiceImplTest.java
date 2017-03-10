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
package de.iteratec.iteraplan.businesslogic.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.MockTestHelper;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.DashboardElementLists;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.SealState;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.dao.ArchitecturalDomainDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessDomainDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessFunctionDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessObjectDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessProcessDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessUnitDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemDomainDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO;
import de.iteratec.iteraplan.persistence.dao.InfrastructureElementDAO;
import de.iteratec.iteraplan.persistence.dao.ProductDAO;
import de.iteratec.iteraplan.persistence.dao.ProjectDAO;
import de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO;


/**
 *
 */
public class DashboardServiceImplTest {

  private BusinessDomainDAO             businessDomainDAOm;
  private BusinessProcessDAO            businessProcessDAOm;
  private BusinessFunctionDAO           businessFunctionDAOm;
  private ProductDAO                    productDAOm;
  private BusinessUnitDAO               businessUnitDAOm;
  private BusinessObjectDAO             businessObjectDAOm;
  private InformationSystemDomainDAO    informationSystemDomainDAOm;
  private InformationSystemReleaseDAO   informationSystemReleaseDAOm;
  private InformationSystemInterfaceDAO informationSystemInterfaceDAOm;
  private ArchitecturalDomainDAO        architecturalDomainDAOm;
  private TechnicalComponentReleaseDAO  technicalComponentReleaseDAOm;
  private InfrastructureElementDAO      infrastructureElementDAOm;
  private ProjectDAO                    projectDAOm;
  private AttributeValueService         attributeValueServiceM;
  private AttributeTypeService          attributeTypeServiceM;

  // to test
  private DashboardServiceImpl          dashboardServiceImpl = new DashboardServiceImpl();

  @Before
  public void setUp() {
    businessDomainDAOm = MockTestHelper.createNiceMock(BusinessDomainDAO.class);
    businessProcessDAOm = MockTestHelper.createNiceMock(BusinessProcessDAO.class);
    businessFunctionDAOm = MockTestHelper.createNiceMock(BusinessFunctionDAO.class);
    productDAOm = MockTestHelper.createNiceMock(ProductDAO.class);
    businessUnitDAOm = MockTestHelper.createNiceMock(BusinessUnitDAO.class);
    businessObjectDAOm = MockTestHelper.createNiceMock(BusinessObjectDAO.class);
    informationSystemDomainDAOm = MockTestHelper.createNiceMock(InformationSystemDomainDAO.class);
    informationSystemReleaseDAOm = MockTestHelper.createNiceMock(InformationSystemReleaseDAO.class);
    informationSystemInterfaceDAOm = MockTestHelper.createNiceMock(InformationSystemInterfaceDAO.class);
    architecturalDomainDAOm = MockTestHelper.createNiceMock(ArchitecturalDomainDAO.class);
    technicalComponentReleaseDAOm = MockTestHelper.createNiceMock(TechnicalComponentReleaseDAO.class);
    infrastructureElementDAOm = MockTestHelper.createNiceMock(InfrastructureElementDAO.class);
    projectDAOm = MockTestHelper.createNiceMock(ProjectDAO.class);
    attributeTypeServiceM = MockTestHelper.createNiceMock(AttributeTypeService.class);
    attributeValueServiceM = MockTestHelper.createNiceMock(AttributeValueService.class);

    dashboardServiceImpl.setArchitecturalDomainDAO(architecturalDomainDAOm);
    dashboardServiceImpl.setAttributeTypeService(attributeTypeServiceM);
    dashboardServiceImpl.setAttributeValueService(attributeValueServiceM);
    dashboardServiceImpl.setBusinessDomainDAO(businessDomainDAOm);
    dashboardServiceImpl.setBusinessFunctionDAO(businessFunctionDAOm);
    dashboardServiceImpl.setBusinessObjectDAO(businessObjectDAOm);
    dashboardServiceImpl.setBusinessProcessDAO(businessProcessDAOm);
    dashboardServiceImpl.setBusinessUnitDAO(businessUnitDAOm);
    dashboardServiceImpl.setInformationSystemDomainDAO(informationSystemDomainDAOm);
    dashboardServiceImpl.setInformationSystemInterfaceDAO(informationSystemInterfaceDAOm);
    dashboardServiceImpl.setInformationSystemReleaseDAO(informationSystemReleaseDAOm);
    dashboardServiceImpl.setInfrastructureElementDAO(infrastructureElementDAOm);
    dashboardServiceImpl.setProductDAO(productDAOm);
    dashboardServiceImpl.setProjectDAO(projectDAOm);
    dashboardServiceImpl.setTechnicalComponentReleaseDAO(technicalComponentReleaseDAOm);

    User user = new User();
    user.setDataSource("MASTER");
    user.setLoginName("system");
    UserContext userContext = new UserContext("system", new HashSet<Role>(), Locale.ENGLISH, user);
    UserContext.setCurrentUserContext(userContext);

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DashboardServiceImpl#getElementLists()}.
   */
  @Test
  public void testGetElementLists() {
    // mocking dao-returns
    List<BusinessDomain> bdList = new ArrayList<BusinessDomain>();
    BusinessDomain bd = new BusinessDomain();
    bdList.add(bd);
    MockTestHelper.expect(businessDomainDAOm.loadElementList(null)).andReturn(bdList);
    List<BusinessProcess> bpList = new ArrayList<BusinessProcess>();
    BusinessProcess bp = new BusinessProcess();
    bpList.add(bp);
    MockTestHelper.expect(businessProcessDAOm.loadElementList(null)).andReturn(bpList);
    List<BusinessFunction> bfList = new ArrayList<BusinessFunction>();
    BusinessFunction bf = new BusinessFunction();
    bfList.add(bf);
    MockTestHelper.expect(businessFunctionDAOm.loadElementList(null)).andReturn(bfList);
    List<Product> prodList = new ArrayList<Product>();
    Product prod = new Product();
    prodList.add(prod);
    MockTestHelper.expect(productDAOm.loadElementList(null)).andReturn(prodList);
    List<BusinessUnit> buList = new ArrayList<BusinessUnit>();
    BusinessUnit bu = new BusinessUnit();
    buList.add(bu);
    MockTestHelper.expect(businessUnitDAOm.loadElementList(null)).andReturn(buList);
    List<BusinessObject> boList = new ArrayList<BusinessObject>();
    BusinessObject bo = new BusinessObject();
    boList.add(bo);
    MockTestHelper.expect(businessObjectDAOm.loadElementList(null)).andReturn(boList);
    List<InformationSystemDomain> isdList = new ArrayList<InformationSystemDomain>();
    InformationSystemDomain isd = new InformationSystemDomain();
    isdList.add(isd);
    MockTestHelper.expect(informationSystemDomainDAOm.loadElementList(null)).andReturn(isdList);
    List<InformationSystemRelease> isrList = new ArrayList<InformationSystemRelease>();
    InformationSystemRelease isr = new InformationSystemRelease();
    isrList.add(isr);
    MockTestHelper.expect(informationSystemReleaseDAOm.loadElementList(null)).andReturn(isrList);
    List<InformationSystemInterface> isiList = new ArrayList<InformationSystemInterface>();
    InformationSystemInterface isi = new InformationSystemInterface();
    isiList.add(isi);
    MockTestHelper.expect(informationSystemInterfaceDAOm.loadElementList(null)).andReturn(isiList);
    List<ArchitecturalDomain> adList = new ArrayList<ArchitecturalDomain>();
    ArchitecturalDomain ad = new ArchitecturalDomain();
    adList.add(ad);
    MockTestHelper.expect(architecturalDomainDAOm.loadElementList(null)).andReturn(adList);
    List<TechnicalComponentRelease> tcrList = new ArrayList<TechnicalComponentRelease>();
    TechnicalComponentRelease tcr = new TechnicalComponentRelease();
    tcrList.add(tcr);
    MockTestHelper.expect(technicalComponentReleaseDAOm.loadElementList(null)).andReturn(tcrList);
    List<InfrastructureElement> ieList = new ArrayList<InfrastructureElement>();
    InfrastructureElement ie = new InfrastructureElement();
    ieList.add(ie);
    MockTestHelper.expect(infrastructureElementDAOm.loadElementList(null)).andReturn(ieList);
    List<Project> projList = new ArrayList<Project>();
    Project proj = new Project();
    projList.add(proj);
    MockTestHelper.expect(projectDAOm.loadElementList(null)).andReturn(projList);

    MockTestHelper.replay(businessDomainDAOm, businessProcessDAOm, businessFunctionDAOm, productDAOm, businessUnitDAOm, businessObjectDAOm,
        informationSystemDomainDAOm, informationSystemInterfaceDAOm, informationSystemReleaseDAOm, architecturalDomainDAOm,
        technicalComponentReleaseDAOm, infrastructureElementDAOm, projectDAOm);

    DashboardElementLists elements = dashboardServiceImpl.getElementLists();
    // DashboardElementLists doesn't implement equals(), therefore  just some checks on the lists, 
    // code remains the same
    assertEquals(elements.getBdList(), bdList);
    assertEquals(elements.getIsrList(), isrList);
    assertEquals(elements.getProjList(), projList);

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DashboardServiceImpl#getNumberOfElementsMap(de.iteratec.iteraplan.model.DashboardElementLists)}.
   */
  @Test
  public void testGetNumberOfElementsMap() {
    // mocking dao-returns with lists of various sizes
    List<BusinessDomain> bdList = new ArrayList<BusinessDomain>();
    bdList.add(new BusinessDomain());
    bdList.add(new BusinessDomain());
    bdList.add(new BusinessDomain());
    MockTestHelper.expect(businessDomainDAOm.loadElementList(null)).andReturn(bdList);
    List<BusinessProcess> bpList = new ArrayList<BusinessProcess>();
    bpList.add(new BusinessProcess());
    bpList.add(new BusinessProcess());
    bpList.add(new BusinessProcess());
    bpList.add(new BusinessProcess());
    MockTestHelper.expect(businessProcessDAOm.loadElementList(null)).andReturn(bpList);
    List<BusinessFunction> bfList = new ArrayList<BusinessFunction>();
    //bfList empty
    MockTestHelper.expect(businessFunctionDAOm.loadElementList(null)).andReturn(bfList);
    List<Product> prodList = new ArrayList<Product>();
    prodList.add(new Product());
    MockTestHelper.expect(productDAOm.loadElementList(null)).andReturn(prodList);
    List<BusinessUnit> buList = new ArrayList<BusinessUnit>();
    BusinessUnit bu = new BusinessUnit();
    buList.add(bu);
    MockTestHelper.expect(businessUnitDAOm.loadElementList(null)).andReturn(buList);
    List<BusinessObject> boList = new ArrayList<BusinessObject>();
    BusinessObject bo = new BusinessObject();
    boList.add(bo);
    boList.add(new BusinessObject());
    MockTestHelper.expect(businessObjectDAOm.loadElementList(null)).andReturn(boList);
    List<InformationSystemDomain> isdList = new ArrayList<InformationSystemDomain>();
    //isdList empty
    MockTestHelper.expect(informationSystemDomainDAOm.loadElementList(null)).andReturn(isdList);
    List<InformationSystemRelease> isrList = new ArrayList<InformationSystemRelease>();
    InformationSystemRelease isr = new InformationSystemRelease();
    isrList.add(isr);
    MockTestHelper.expect(informationSystemReleaseDAOm.loadElementList(null)).andReturn(isrList);
    List<InformationSystemInterface> isiList = new ArrayList<InformationSystemInterface>();
    InformationSystemInterface isi = new InformationSystemInterface();
    isiList.add(isi);
    isiList.add(new InformationSystemInterface());
    isiList.add(new InformationSystemInterface());
    MockTestHelper.expect(informationSystemInterfaceDAOm.loadElementList(null)).andReturn(isiList);
    List<ArchitecturalDomain> adList = new ArrayList<ArchitecturalDomain>();
    ArchitecturalDomain ad = new ArchitecturalDomain();
    adList.add(ad);
    MockTestHelper.expect(architecturalDomainDAOm.loadElementList(null)).andReturn(adList);
    List<TechnicalComponentRelease> tcrList = new ArrayList<TechnicalComponentRelease>();
    TechnicalComponentRelease tcr = new TechnicalComponentRelease();
    tcrList.add(tcr);
    tcrList.add(new TechnicalComponentRelease());
    MockTestHelper.expect(technicalComponentReleaseDAOm.loadElementList(null)).andReturn(tcrList);
    List<InfrastructureElement> ieList = new ArrayList<InfrastructureElement>();
    // ieList empty
    MockTestHelper.expect(infrastructureElementDAOm.loadElementList(null)).andReturn(ieList);
    List<Project> projList = new ArrayList<Project>();
    Project proj = new Project();
    projList.add(proj);
    projList.add(new Project());
    projList.add(new Project());
    projList.add(new Project());
    projList.add(new Project());
    projList.add(new Project());
    MockTestHelper.expect(projectDAOm.loadElementList(null)).andReturn(projList);

    MockTestHelper.replay(businessDomainDAOm, businessProcessDAOm, businessFunctionDAOm, productDAOm, businessUnitDAOm, businessObjectDAOm,
        informationSystemDomainDAOm, informationSystemInterfaceDAOm, informationSystemReleaseDAOm, architecturalDomainDAOm,
        technicalComponentReleaseDAOm, infrastructureElementDAOm, projectDAOm);

    // expected sizes
    Map<String, Integer> numberMap = new LinkedHashMap<String, Integer>();
    numberMap.put(Constants.BB_BUSINESSDOMAIN_BASE, Integer.valueOf(3));
    numberMap.put(Constants.BB_BUSINESSPROCESS_BASE, Integer.valueOf(4));
    numberMap.put(Constants.BB_BUSINESSFUNCTION_BASE, Integer.valueOf(0));
    numberMap.put(Constants.BB_PRODUCT_BASE, Integer.valueOf(1));
    numberMap.put(Constants.BB_BUSINESSUNIT_BASE, Integer.valueOf(1));
    numberMap.put(Constants.BB_BUSINESSOBJECT_BASE, Integer.valueOf(2));
    numberMap.put(Constants.BB_INFORMATIONSYSTEMDOMAIN_BASE, Integer.valueOf(0));
    numberMap.put(Constants.BB_INFORMATIONSYSTEMRELEASE_BASE, Integer.valueOf(1));
    numberMap.put(Constants.BB_INFORMATIONSYSTEMINTERFACE_BASE, Integer.valueOf(3));
    numberMap.put(Constants.BB_ARCHITECTURALDOMAIN_BASE, Integer.valueOf(1));
    numberMap.put(Constants.BB_TECHNICALCOMPONENTRELEASE_BASE, Integer.valueOf(2));
    numberMap.put(Constants.BB_INFRASTRUCTUREELEMENT_BASE, Integer.valueOf(0));
    numberMap.put(Constants.BB_PROJECT_BASE, Integer.valueOf(6));

    DashboardElementLists elements = dashboardServiceImpl.getElementLists();
    assertEquals(numberMap, dashboardServiceImpl.getNumberOfElementsMap(elements));

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DashboardServiceImpl#getIsrStatusMap(java.util.List)}.
   */
  @Test
  public void testGetIsrStatusMap() {
    List<InformationSystemRelease> isrs = new LinkedList<InformationSystemRelease>();
    InformationSystemRelease isr1 = new InformationSystemRelease();
    isr1.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.CURRENT);
    isrs.add(isr1);
    InformationSystemRelease isr2 = new InformationSystemRelease();
    isr2.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.CURRENT);
    isrs.add(isr2);
    InformationSystemRelease isr3 = new InformationSystemRelease();
    isr3.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.CURRENT);
    isrs.add(isr3);
    InformationSystemRelease isr4 = new InformationSystemRelease();
    isr4.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.INACTIVE);
    isrs.add(isr4);
    InformationSystemRelease isr5 = new InformationSystemRelease();
    isr5.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.INACTIVE);
    isrs.add(isr5);
    InformationSystemRelease isr6 = new InformationSystemRelease();
    isr6.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.PLANNED);
    isrs.add(isr6);
    InformationSystemRelease isr7 = new InformationSystemRelease();
    isr7.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.PLANNED);
    isrs.add(isr7);
    InformationSystemRelease isr8 = new InformationSystemRelease();
    isr8.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.PLANNED);
    isrs.add(isr8);
    InformationSystemRelease isr9 = new InformationSystemRelease();
    isr9.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.PLANNED);
    isrs.add(isr9);
    InformationSystemRelease isr0 = new InformationSystemRelease();
    isr0.setTypeOfStatus(InformationSystemRelease.TypeOfStatus.PLANNED);
    isrs.add(isr0);

    // expected map
    Map<String, Integer> statusMap = Maps.newLinkedHashMap();
    statusMap.put(InformationSystemRelease.TypeOfStatus.CURRENT.toString(), Integer.valueOf(3));
    statusMap.put(InformationSystemRelease.TypeOfStatus.PLANNED.toString(), Integer.valueOf(5));
    statusMap.put(InformationSystemRelease.TypeOfStatus.TARGET.toString(), Integer.valueOf(0));
    statusMap.put(InformationSystemRelease.TypeOfStatus.INACTIVE.toString(), Integer.valueOf(2));

    assertEquals(statusMap, dashboardServiceImpl.getIsrStatusMap(isrs));

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DashboardServiceImpl#getTechnicalComponentsStatusMap(java.util.List)}.
   */
  @Test
  public void testGetTechnicalComponentsStatusMap() {
    List<TechnicalComponentRelease> tcrs = new LinkedList<TechnicalComponentRelease>();
    TechnicalComponentRelease tcr1 = new TechnicalComponentRelease();
    tcr1.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.CURRENT);
    tcrs.add(tcr1);
    TechnicalComponentRelease tcr2 = new TechnicalComponentRelease();
    tcr2.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.CURRENT);
    tcrs.add(tcr2);
    TechnicalComponentRelease tcr3 = new TechnicalComponentRelease();
    tcr3.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.CURRENT);
    tcrs.add(tcr3);
    TechnicalComponentRelease tcr4 = new TechnicalComponentRelease();
    tcr4.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.INACTIVE);
    tcrs.add(tcr4);
    TechnicalComponentRelease tcr5 = new TechnicalComponentRelease();
    tcr5.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.INACTIVE);
    tcrs.add(tcr5);
    TechnicalComponentRelease tcr6 = new TechnicalComponentRelease();
    tcr6.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.PLANNED);
    tcrs.add(tcr6);
    TechnicalComponentRelease tcr7 = new TechnicalComponentRelease();
    tcr7.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.PLANNED);
    tcrs.add(tcr7);
    TechnicalComponentRelease tcr8 = new TechnicalComponentRelease();
    tcr8.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.PLANNED);
    tcrs.add(tcr8);
    TechnicalComponentRelease tcr9 = new TechnicalComponentRelease();
    tcr9.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.PLANNED);
    tcrs.add(tcr9);
    TechnicalComponentRelease tcr0 = new TechnicalComponentRelease();
    tcr0.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.PLANNED);
    tcrs.add(tcr0);
    TechnicalComponentRelease tcr11 = new TechnicalComponentRelease();
    tcr11.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.UNDEFINED);
    tcrs.add(tcr11);

    // expected map
    Map<String, Integer> statusMap = Maps.newLinkedHashMap();
    statusMap.put(TechnicalComponentRelease.TypeOfStatus.CURRENT.toString(), Integer.valueOf(3));
    statusMap.put(TechnicalComponentRelease.TypeOfStatus.PLANNED.toString(), Integer.valueOf(5));
    statusMap.put(TechnicalComponentRelease.TypeOfStatus.TARGET.toString(), Integer.valueOf(0));
    statusMap.put(TechnicalComponentRelease.TypeOfStatus.INACTIVE.toString(), Integer.valueOf(2));
    statusMap.put(TechnicalComponentRelease.TypeOfStatus.UNDEFINED.toString(), Integer.valueOf(1));

    assertEquals(statusMap, dashboardServiceImpl.getTechnicalComponentsStatusMap(tcrs));

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DashboardServiceImpl#getIsrSealStateMap(java.util.List)}.
   */
  @Test
  public void testGetIsrSealStateMap() {
    List<InformationSystemRelease> isrs = new LinkedList<InformationSystemRelease>();
    InformationSystemRelease isr1 = new InformationSystemRelease();
    isr1.setSealState(SealState.INVALID);
    isrs.add(isr1);
    InformationSystemRelease isr2 = new InformationSystemRelease();
    isr2.setSealState(SealState.INVALID);
    isrs.add(isr2);
    InformationSystemRelease isr3 = new InformationSystemRelease();
    isr3.setSealState(SealState.INVALID);
    isrs.add(isr3);
    InformationSystemRelease isr4 = new InformationSystemRelease();
    isr4.setSealState(SealState.NOT_AVAILABLE);
    isrs.add(isr4);
    InformationSystemRelease isr5 = new InformationSystemRelease();
    isr5.setSealState(SealState.NOT_AVAILABLE);
    isrs.add(isr5);
    InformationSystemRelease isr6 = new InformationSystemRelease();
    isr6.setSealState(SealState.VALID);
    isrs.add(isr6);
    InformationSystemRelease isr7 = new InformationSystemRelease();
    isr7.setSealState(SealState.VALID);
    isrs.add(isr7);
    InformationSystemRelease isr8 = new InformationSystemRelease();
    isr8.setSealState(SealState.VALID);
    isrs.add(isr8);
    InformationSystemRelease isr9 = new InformationSystemRelease();
    isr9.setSealState(SealState.VALID);
    isrs.add(isr9);
    InformationSystemRelease isr0 = new InformationSystemRelease();
    isr0.setSealState(SealState.OUTDATED);
    isrs.add(isr0);

    // expected map
    Map<String, Integer> statusMap = Maps.newLinkedHashMap();
    statusMap.put(SealState.VALID.toString(), Integer.valueOf(4));
    statusMap.put(SealState.OUTDATED.toString(), Integer.valueOf(1));
    statusMap.put(SealState.INVALID.toString(), Integer.valueOf(3));
    statusMap.put(SealState.NOT_AVAILABLE.toString(), Integer.valueOf(2));

    assertEquals(statusMap, dashboardServiceImpl.getIsrSealStateMap(isrs));

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DashboardServiceImpl#getTopUsedIsr(java.util.List)}.
   */
  @SuppressWarnings({ "boxing", "unchecked" })
  @Test
  public void testGetTopUsedIsr() {
    List<InformationSystemRelease> isrs = new LinkedList<InformationSystemRelease>();

    InformationSystemRelease isr1 = MockTestHelper.createNiceMock(InformationSystemRelease.class);
    List<InformationSystemRelease> retList1 = MockTestHelper.createNiceMock(List.class);
    MockTestHelper.expect(retList1.size()).andReturn(5);
    MockTestHelper.expect(isr1.getInterfacedInformationSystemReleases()).andReturn(retList1);
    MockTestHelper.replay(isr1, retList1);
    isrs.add(isr1);

    InformationSystemRelease isr2 = MockTestHelper.createNiceMock(InformationSystemRelease.class);
    List<InformationSystemRelease> retList2 = MockTestHelper.createNiceMock(List.class);
    MockTestHelper.expect(retList2.size()).andReturn(Integer.valueOf(3));
    MockTestHelper.expect(isr2.getInterfacedInformationSystemReleases()).andReturn(retList2);
    MockTestHelper.replay(isr2, retList2);
    isrs.add(isr2);

    InformationSystemRelease isr3 = MockTestHelper.createNiceMock(InformationSystemRelease.class);
    List<InformationSystemRelease> retList3 = MockTestHelper.createNiceMock(List.class);
    MockTestHelper.expect(retList3.size()).andReturn(Integer.valueOf(10));
    MockTestHelper.expect(isr3.getInterfacedInformationSystemReleases()).andReturn(retList3);
    MockTestHelper.replay(isr3, retList3);
    isrs.add(isr3);

    InformationSystemRelease isr4 = MockTestHelper.createNiceMock(InformationSystemRelease.class);
    List<InformationSystemRelease> retList4 = MockTestHelper.createNiceMock(List.class);
    MockTestHelper.expect(retList4.size()).andReturn(Integer.valueOf(1));
    MockTestHelper.expect(isr4.getInterfacedInformationSystemReleases()).andReturn(retList4);
    MockTestHelper.replay(isr4, retList4);
    isrs.add(isr4);

    Map<InformationSystemRelease, Integer> res = dashboardServiceImpl.getTopUsedIsr(isrs);

    // the generation of a sortedMap via the MapValueComparator hinders a simple equality-check
    int[] expectedVals = { 10, 5, 3, 1 };
    Iterator<Integer> it = res.values().iterator();
    int i = 0;
    while (it.hasNext()) {
      assertEquals(expectedVals[i++], it.next().intValue());
    }

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DashboardServiceImpl#getTopUsedTcr(java.util.List)}.
   */
  @SuppressWarnings({ "unchecked", "boxing" })
  @Test
  public void testGetTopUsedTcr() {
    List<TechnicalComponentRelease> tcrs = new LinkedList<TechnicalComponentRelease>();

    TechnicalComponentRelease tcr1 = MockTestHelper.createNiceMock(TechnicalComponentRelease.class);
    Set<InformationSystemRelease> retList1 = MockTestHelper.createNiceMock(Set.class);
    MockTestHelper.expect(retList1.size()).andReturn(15);
    MockTestHelper.expect(tcr1.getInformationSystemReleases()).andReturn(retList1);
    MockTestHelper.replay(tcr1, retList1);
    tcrs.add(tcr1);

    TechnicalComponentRelease tcr2 = MockTestHelper.createNiceMock(TechnicalComponentRelease.class);
    Set<InformationSystemRelease> retList2 = MockTestHelper.createNiceMock(Set.class);
    MockTestHelper.expect(retList2.size()).andReturn(Integer.valueOf(3));
    MockTestHelper.expect(tcr2.getInformationSystemReleases()).andReturn(retList2);
    MockTestHelper.replay(tcr2, retList2);
    tcrs.add(tcr2);

    TechnicalComponentRelease tcr3 = MockTestHelper.createNiceMock(TechnicalComponentRelease.class);
    Set<InformationSystemRelease> retList3 = MockTestHelper.createNiceMock(Set.class);
    MockTestHelper.expect(retList3.size()).andReturn(Integer.valueOf(10));
    MockTestHelper.expect(tcr3.getInformationSystemReleases()).andReturn(retList3);
    MockTestHelper.replay(tcr3, retList3);
    tcrs.add(tcr3);

    TechnicalComponentRelease tcr4 = MockTestHelper.createNiceMock(TechnicalComponentRelease.class);
    Set<InformationSystemRelease> retList4 = MockTestHelper.createNiceMock(Set.class);
    MockTestHelper.expect(retList4.size()).andReturn(Integer.valueOf(0));
    MockTestHelper.expect(tcr4.getInformationSystemReleases()).andReturn(retList4);
    MockTestHelper.replay(tcr4, retList4);
    tcrs.add(tcr4);

    Map<TechnicalComponentRelease, Integer> res = dashboardServiceImpl.getTopUsedTcr(tcrs);

    // the generation of a sortedMap via the MapValueComparator hinders a simple equality-check
    int[] expectedVals = { 15, 10, 3, 0 };
    Iterator<Integer> it = res.values().iterator();
    int i = 0;
    while (it.hasNext()) {
      assertEquals(expectedVals[i++], it.next().intValue());
    }

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DashboardServiceImpl#getSingleDimensionAttributes(java.util.List)}.
   */
  @Test
  public void testGetSingleDimensionAttributes() {
    List<BBAttribute> inputList = new ArrayList<BBAttribute>();
    BBAttribute attr1 = new BBAttribute(Integer.valueOf(15), BBAttribute.FIXED_ATTRIBUTE_DATETYPE, null, null);
    attr1.setIsMultiValue(true);
    inputList.add(attr1);
    BBAttribute attr2 = new BBAttribute(Integer.valueOf(15), BBAttribute.FIXED_ATTRIBUTE_ENUM, null, null);
    attr2.setIsMultiValue(false);
    inputList.add(attr2);
    BBAttribute attr3 = new BBAttribute(Integer.valueOf(15), BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE, null, null);
    attr3.setIsMultiValue(false);
    inputList.add(attr3);
    BBAttribute attr4 = new BBAttribute(Integer.valueOf(15), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, null, null);
    attr4.setIsMultiValue(true);
    inputList.add(attr4);

    List<BBAttribute> expList = new ArrayList<BBAttribute>();
    expList.add(attr2);
    expList.add(attr3);

    assertEquals(expList, dashboardServiceImpl.getSingleDimensionAttributes(inputList));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DashboardServiceImpl#getTopUsedTcrByAdMap(de.iteratec.iteraplan.model.DashboardElementLists)}.
   */
  @Test
  public void testGetTopUsedTcrByAdMap() {
    List<TechnicalComponentRelease> tcrList = new ArrayList<TechnicalComponentRelease>();
    List<ArchitecturalDomain> adList = new ArrayList<ArchitecturalDomain>();
    TechnicalComponentRelease tcr1 = new TechnicalComponentRelease();
    TechnicalComponentRelease tcr2 = new TechnicalComponentRelease();
    TechnicalComponentRelease tcr3 = new TechnicalComponentRelease();
    TechnicalComponentRelease tcr4 = new TechnicalComponentRelease();
    ArchitecturalDomain ad1 = new ArchitecturalDomain();
    ArchitecturalDomain ad2 = new ArchitecturalDomain();
    ad2.setName(AbstractHierarchicalEntity.TOP_LEVEL_NAME);
    ArchitecturalDomain ad3 = new ArchitecturalDomain();
    ArchitecturalDomain ad4 = new ArchitecturalDomain();

    tcr2.addArchitecturalDomain(ad1);
    tcr3.addArchitecturalDomain(ad3);
    tcr4.addArchitecturalDomain(ad3);
    tcr4.addArchitecturalDomain(ad4);

    assert (ad3.getTechnicalComponentReleases().size() == 2);

    tcr1.addInformationSystemRelease(new InformationSystemRelease());
    tcr3.addInformationSystemRelease(new InformationSystemRelease());
    tcr3.addInformationSystemRelease(new InformationSystemRelease());
    tcr2.addInformationSystemRelease(new InformationSystemRelease());
    tcr2.addInformationSystemRelease(new InformationSystemRelease());
    tcr2.addInformationSystemRelease(new InformationSystemRelease());
    tcr4.addInformationSystemRelease(new InformationSystemRelease());
    tcr4.addInformationSystemRelease(new InformationSystemRelease());
    tcr4.addInformationSystemRelease(new InformationSystemRelease());
    tcr4.addInformationSystemRelease(new InformationSystemRelease());

    tcrList.add(tcr1);
    tcrList.add(tcr2);
    tcrList.add(tcr3);
    tcrList.add(tcr4);
    adList.add(ad1);
    adList.add(ad2);
    adList.add(ad3);
    adList.add(ad4);

    DashboardElementLists elements = new DashboardElementLists();
    elements.setTcrList(tcrList);
    elements.setAdList(adList);

    Map<ArchitecturalDomain, Map<TechnicalComponentRelease, Integer>> res = dashboardServiceImpl.getTopUsedTcrByAdMap(elements);

    // top level name not allowed
    assertFalse(res.containsKey(ad2));

    // the generation of a sortedMap via the MapValueComparator hinders a simple equality-check
    int[] expectedVals = { 1, 3, 4, 2, 4 };
    Iterator<Map<TechnicalComponentRelease, Integer>> it = res.values().iterator();
    int i = 0;
    while (it.hasNext()) {
      if (i != 2) {
        assertEquals(expectedVals[i++], it.next().values().iterator().next().intValue());
      }
      else {
        // the iterator for ad3 has to be stored since it holds 2 values, tcr3 and tcr4
        Iterator<Integer> intIter = it.next().values().iterator();
        assertEquals(expectedVals[i++], intIter.next().intValue());
        assertEquals(expectedVals[i++], intIter.next().intValue());
      }
    }
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DashboardServiceImpl#getValueMap(java.util.List, java.util.List)}.
   * There is a variety of cases not yet covered by this test, e.g. a number-type attribute or assigned values
   */
  @SuppressWarnings("boxing")
  @Test
  public void testGetValueMapWithoutNumberAttributes() {
    List<BBAttribute> bbaList = new LinkedList<BBAttribute>();
    BBAttribute attr1 = new BBAttribute(Integer.valueOf(1), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, "one", null);
    BBAttribute attr2 = new BBAttribute(Integer.valueOf(2), BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE, "two", null);
    bbaList.add(attr1);
    bbaList.add(attr2);

    assertTrue(dashboardServiceImpl.getValueMap(bbaList, null).size() == 0);

    List<InformationSystemInterface> bbList = Lists.newLinkedList();

    InformationSystemInterface isi1 = new InformationSystemInterface();
    isi1.setId(1);
    bbList.add(isi1);
    InformationSystemInterface isi2 = new InformationSystemInterface();
    isi2.setId(2);
    bbList.add(isi2);

    MockTestHelper.expect(attributeTypeServiceM.isNumberAT(Integer.valueOf(1))).andReturn(Boolean.FALSE);
    MockTestHelper.expect(attributeTypeServiceM.isNumberAT(Integer.valueOf(2))).andReturn(Boolean.FALSE);
    MockTestHelper.expect(attributeTypeServiceM.getAttributeTypeByName("one")).andReturn(null);
    MockTestHelper.expect(attributeTypeServiceM.getAttributeTypeByName("two")).andReturn(null);
    MockTestHelper.replay(attributeValueServiceM, attributeTypeServiceM);

    //expected map
    Map<String, Map<String, List<Integer>>> expectedMap = new LinkedHashMap<String, Map<String, List<Integer>>>();
    Map<String, List<Integer>> map1 = new LinkedHashMap<String, List<Integer>>();
    map1.put("not assigned", Lists.newArrayList(isi1.getId(), isi2.getId()));
    Map<String, List<Integer>> map2 = new LinkedHashMap<String, List<Integer>>();
    map2.put("not assigned", Lists.newArrayList(isi1.getId(), isi2.getId()));
    expectedMap.put("one", map1);
    expectedMap.put("two", map2);

    assertEquals(expectedMap, dashboardServiceImpl.getValueMap(bbaList, bbList));

  }

}
