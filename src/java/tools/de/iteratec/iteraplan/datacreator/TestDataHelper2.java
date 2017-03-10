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
package de.iteratec.iteraplan.datacreator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueAssignmentService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.businesslogic.service.BusinessFunctionService;
import de.iteratec.iteraplan.businesslogic.service.BusinessMappingService;
import de.iteratec.iteraplan.businesslogic.service.BusinessObjectService;
import de.iteratec.iteraplan.businesslogic.service.BusinessProcessService;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.DateIntervalService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.businesslogic.service.ProductService;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.businesslogic.service.RoleService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentService;
import de.iteratec.iteraplan.businesslogic.service.TimeseriesService;
import de.iteratec.iteraplan.businesslogic.service.TransportService;
import de.iteratec.iteraplan.businesslogic.service.UserGroupService;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.RangeValue;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.persistence.dao.PermissionAttrTypeGroupDAO;


/**
 * Creates test data for the database. To create the necessary tables, run the schema-export task in
 * the buil.xml file.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveImports" })
@Service
public final class TestDataHelper2 {

  @Autowired
  private BusinessProcessService            businessProcessService;
  @Autowired
  private TechnicalComponentReleaseService  tcrService;
  @Autowired
  private BusinessDomainService             businessDomainService;
  @Autowired
  private InformationSystemReleaseService   isrService;
  @Autowired
  private BusinessUnitService               businessUnitService;
  @Autowired
  private ProjectService                    projectService;
  @Autowired
  private BusinessObjectService             businessObjectService;
  @Autowired
  private BuildingBlockTypeService          buildingBlockTypeService;
  @Autowired
  private AttributeValueAssignmentService   avaService;
  @Autowired
  private InfrastructureElementService      infrastructureElementService;
  @Autowired
  private AttributeTypeService              attributeTypeService;
  @Autowired
  private AttributeTypeGroupService         atgService;
  @Autowired
  private ArchitecturalDomainService        architecturalDomainService;
  @Autowired
  private InformationSystemDomainService    informationSystemDomainService;
  @Autowired
  private RoleService                       roleService;
  @Autowired
  private UserService                       userService;
  @Autowired
  private ProductService                    productService;
  @Autowired
  private InformationSystemInterfaceService isiService;
  @Autowired
  private PermissionAttrTypeGroupDAO        permissionAttrTypeGroupDAO;
  @Autowired
  private BusinessFunctionService           businessFunctionService;
  @Autowired
  private BusinessMappingService            businessMappingService;
  @Autowired
  private AttributeValueService             attributeValueService;
  @Autowired
  private TechnicalComponentService         technicalComponentService;
  @Autowired
  private TransportService                  transportService;
  @Autowired
  private UserGroupService                  userGroupService;
  @Autowired
  private TimeseriesService                 timeseriesService;
  @Autowired
  private DateIntervalService               dateIntervalService;

  private static final String               DEFAULT_COLOR = "00FF00";

  public void addADToTCRelease(TechnicalComponentRelease tcr, ArchitecturalDomain ad) {
    tcr.addArchitecturalDomain(ad);
    tcrService.saveOrUpdate(tcr);
  }

  public void addBaseComponentToTCRelease(TechnicalComponentRelease tcr, TechnicalComponentRelease base) {
    tcr.addBaseComponent(base);
    tcrService.saveOrUpdate(tcr);
  }

  public void addBaseComponentToISRelease(InformationSystemRelease isr, InformationSystemRelease base) {
    isr.addBaseComponent(base);
    isrService.saveOrUpdate(isr);
  }

  public void addBusinessFunctionsToBusinessDomain(BusinessDomain fd, Set<BusinessFunction> bfSet) {
    fd.addBusinessFunctions(bfSet);
    businessDomainService.saveOrUpdate(fd);
  }

  public void addBusinessObjectsToBusinessDomain(BusinessDomain fd, Set<BusinessObject> boSet) {
    fd.addBusinessObjects(boSet);
    businessDomainService.saveOrUpdate(fd);
  }

  public void addBusinessUnitsToBusinessDomain(BusinessDomain bd, Set<BusinessUnit> buSet) {
    bd.addBusinessUnits(buSet);
    businessDomainService.saveOrUpdate(bd);
  }

  public void addBusinessDomainsToBusinessUnit(BusinessUnit bu, Set<BusinessDomain> bdSet) {
    bu.addBusinessDomains(bdSet);
    businessUnitService.saveOrUpdate(bu);
  }

  public Isr2BoAssociation addBusinessObjectToInformationSystem(InformationSystemRelease isr, BusinessObject bo) {
    Isr2BoAssociation assoc = BuildingBlockFactory.createIsr2BoAssociation(isr, bo);
    assoc.connect();
    isrService.saveOrUpdate(isr);
    return assoc;
  }

  public void addBusinessProcessesToBusinessDomain(BusinessDomain fd, Set<BusinessProcess> bpSet) {
    fd.addBusinessProcesses(bpSet);
    businessDomainService.saveOrUpdate(fd);
  }

  public void addChildToIsr(InformationSystemRelease parent, InformationSystemRelease child) {
    parent.addChild(child);
    isrService.saveOrUpdate(parent);
  }

  public void addElementOf(BusinessObject parent, BusinessObject child) {
    child.addParent(parent);
    businessObjectService.saveOrUpdate(parent);
  }

  public void addElementOf(Project parent, Project child) {
    child.addParent(parent);
    projectService.saveOrUpdate(parent);
  }

  public void addIeToIsr(InformationSystemRelease isr, InfrastructureElement ie) {
    isr.addInfrastructureElement(ie);
    isrService.saveOrUpdate(isr);
  }

  public void addBfToIsr(InformationSystemRelease isr, BusinessFunction bf) {
    isr.addBusinessFunction(bf);
    isrService.saveOrUpdate(isr);
  }

  public void addIsrToIsd(InformationSystemRelease isr, InformationSystemDomain isd) {
    isr.addInformationSystemDomain(isd);
    isrService.saveOrUpdate(isr);
  }

  public void addIsrToProject(InformationSystemRelease isr, Project proj) {
    isr.addProject(proj);
    isrService.saveOrUpdate(isr);
  }

  public void addSuccessorToIsr(InformationSystemRelease isr, InformationSystemRelease successor) {
    isr.addSuccessor(successor);
    isrService.saveOrUpdate(isr);
  }

  public void addSuccessorToTCRelease(TechnicalComponentRelease tcr, TechnicalComponentRelease succ) {
    tcr.addSuccessor(succ);
    tcrService.saveOrUpdate(tcr);
  }

  public void addTcrToIsr(InformationSystemRelease isr, TechnicalComponentRelease tcr) {
    isr.addTechnicalComponentRelease(tcr);
    isrService.saveOrUpdate(isr);
  }

  public Tcr2IeAssociation addTcrToIe(InfrastructureElement ie, TechnicalComponentRelease tcr) {
    Tcr2IeAssociation assoc = BuildingBlockFactory.createTcr2IeAssociation(tcr, ie);
    assoc.connect();
    infrastructureElementService.saveOrUpdate(ie);
    return assoc;
  }

  public void addIeToTcr(TechnicalComponentRelease tcr, InfrastructureElement ie) {
    Tcr2IeAssociation assoc = BuildingBlockFactory.createTcr2IeAssociation(tcr, ie);
    assoc.connect();
    tcrService.saveOrUpdate(tcr);
  }

  public void addSubscription(User user, BuildingBlock bb) {
    bb.getSubscribedUsers().add(user);
    user.getSubscribedBuildingBlocks().add(bb);
    userService.saveOrUpdate(user);
  }

  public void addSubscription(User user, BuildingBlockType bbt) {
    bbt.getSubscribedUsers().add(user);
    user.getSubscribedBuildingBlockTypes().add(bbt);
    userService.saveOrUpdate(user);
  }

  /**
   * Activates the given {@link AttributeType} for all available {@link BuildingBlockType}s except
   * {@code BusinessMapping}s (see Ticket #1095).
   */
  public void assignAttributeTypeToAllAvailableBuildingBlockTypes(AttributeType at) {
    List<BuildingBlockType> bbts = buildingBlockTypeService.getAvailableBuildingBlockTypesForAttributeType(at.getId());

    for (BuildingBlockType bbt : bbts) {
      if (bbt.getTypeOfBuildingBlock() != TypeOfBuildingBlock.BUSINESSMAPPING) {
        at.addBuildingBlockTypeTwoWay(bbt);
      }
    }

    attributeTypeService.saveOrUpdate(at);
  }

  public void assignAttributeTypeToBuildingBlockType(AttributeType at, BuildingBlockType bbt) {
    at.addBuildingBlockTypeTwoWay(bbt);
    attributeTypeService.saveOrUpdate(at);
  }

  public boolean containsAttributeTypeWithSameID(Set<AttributeType> atSet, AttributeType attributeType) {
    for (AttributeType at : atSet) {
      if (at.getId().equals(attributeType.getId())) {
        return true;
      }
    }
    return false;
  }

  public ArchitecturalDomain createArchitecturalDomain(String name, String desc) {
    return createArchitecturalDomain(name, desc, true);
  }

  public ArchitecturalDomain createArchitecturalDomain(String name, String desc, boolean persist) {
    ArchitecturalDomain domain = BuildingBlockFactory.createArchitecturalDomain();
    domain.setName(name);
    domain.setDescription(desc);

    if (persist) {
      domain = architecturalDomainService.saveOrUpdate(domain);
    }

    return domain;
  }

  public AttributeTypeGroup createAttributeTypeGroup(String name, String description, Boolean isToplevelATG) {
    AttributeTypeGroup atg = new AttributeTypeGroup();
    atg.setName(name);
    atg.setDescription(description);
    atg.setToplevelATG(isToplevelATG);
    atg.setPosition(Integer.valueOf(atgService.getMaxATGPositionNumber().intValue() + 1));
    atgService.saveOrUpdate(atg);
    return atg;
  }

  public AttributeTypeGroup createAttributeTypeGroup(String name, String description) {
    return createAttributeTypeGroup(name, description, Boolean.FALSE);
  }

  public PermissionAttrTypeGroup createAttrTypeGroupPermission(Role adminRole, AttributeTypeGroup atg1, Boolean read, Boolean write) {
    PermissionAttrTypeGroup patg = new PermissionAttrTypeGroup();
    patg.setRole(adminRole);
    patg.setAttrTypeGroup(atg1);
    patg.setReadPermission(read);
    patg.setWritePermission(write);

    permissionAttrTypeGroupDAO.saveOrUpdate(patg);

    return patg;
  }

  /**
   * Assigns the given attribute value to the given building block.
   */
  public void createAVA(BuildingBlock bb, AttributeValue av) {
    AttributeValueAssignment ava = new AttributeValueAssignment();

    if (av instanceof DateAV) {
      DateAV dav = (DateAV) av;
      ava.addReferences(dav, bb);
      avaService.saveOrUpdate(ava);
    }
    else if (av instanceof EnumAV) {
      EnumAV eav = (EnumAV) av;
      ava.addReferences(eav, bb);
      avaService.saveOrUpdate(ava);
    }
    else if (av instanceof NumberAV) {
      NumberAV nav = (NumberAV) av;
      ava.addReferences(nav, bb);
      avaService.saveOrUpdate(ava);
    }
    else if (av instanceof ResponsibilityAV) {
      ResponsibilityAV rav = (ResponsibilityAV) av;
      ava.addReferences(rav, bb);
      avaService.saveOrUpdate(ava);
    }
    else if (av instanceof TextAV) {
      TextAV tav = (TextAV) av;
      ava.addReferences(tav, bb);
      avaService.saveOrUpdate(ava);
    }
    else {
      throw new AssertionError();
    }
  }

  public BusinessDomain createBusinessDomain(String name, String desc) {
    return createBusinessDomain(name, desc, true);
  }

  public BusinessDomain createBusinessDomain(String name, String desc, boolean persist) {
    BusinessDomain fd = BuildingBlockFactory.createBusinessDomain();
    fd.setName(name);
    fd.setDescription(desc);

    if (persist) {
      fd = businessDomainService.saveOrUpdate(fd);
    }

    return fd;
  }

  /**
   * Creates and returns a {@link BusinessFunction} with the given name and description.
   * 
   * @param name
   *          The function's name.
   * @param desc
   *          The function's description. May be {@code null}.
   * @return See method description.
   */
  public BusinessFunction createBusinessFunction(String name, String desc) {
    return createBusinessFunction(name, desc, true);
  }

  public BusinessFunction createBusinessFunction(String name, String desc, boolean persist) {
    BusinessFunction bf = BuildingBlockFactory.createBusinessFunction();
    bf.setName(name);
    bf.setDescription(desc);

    if (persist) {
      bf = businessFunctionService.saveOrUpdate(bf);
    }

    return bf;
  }

  public BusinessMapping createBusinessMapping(InformationSystemRelease release, BusinessProcess process, BusinessUnit unit, Product product) {
    return createBusinessMapping(release, process, unit, product, true);
  }

  public BusinessMapping createBusinessMapping(InformationSystemRelease release, BusinessProcess process, BusinessUnit unit, Product product,
                                               boolean persist) {
    BusinessProcess tempProcess = process;
    BusinessUnit tempUnit = unit;
    Product tempProduct = product;

    BusinessMapping bm = BuildingBlockFactory.createBusinessMapping();

    bm.addInformationSystemRelease(release);

    if (tempProcess == null) {
      tempProcess = businessProcessService.getFirstElement();
    }
    bm.addBusinessProcess(tempProcess);

    if (tempUnit == null) {
      tempUnit = businessUnitService.getFirstElement();
    }
    bm.addBusinessUnit(tempUnit);

    if (tempProduct == null) {
      tempProduct = productService.getFirstElement();
    }
    bm.addProduct(tempProduct);

    if (persist) {
      bm = businessMappingService.saveOrUpdate(bm);
    }

    return bm;
  }

  public BusinessMapping createBusinessMappingToProcess(InformationSystemRelease isr, BusinessProcess bp) {
    return createBusinessMappingToProcess(isr, bp, true);
  }

  public BusinessMapping createBusinessMappingToProcess(InformationSystemRelease isr, BusinessProcess bp, boolean persist) {
    BusinessUnit rootBU = businessUnitService.getFirstElement();
    Product rootProduct = productService.getFirstElement();
    BusinessMapping bpc = BuildingBlockFactory.createBusinessMapping();
    bpc.addInformationSystemRelease(isr);
    bpc.addBusinessProcess(bp);
    bpc.addBusinessUnit(rootBU);
    bpc.addProduct(rootProduct);

    if (persist) {
      bpc = businessMappingService.saveOrUpdate(bpc);
    }

    return bpc;
  }

  public BusinessObject createBusinessObject(String name, String desc) {
    return createBusinessObject(name, desc, true);
  }

  public BusinessObject createBusinessObject(String name, String desc, boolean persist) {
    BusinessObject bo = BuildingBlockFactory.createBusinessObject();
    bo.setName(name);
    bo.setDescription(desc);

    if (persist) {
      bo = businessObjectService.saveOrUpdate(bo);
    }

    return bo;
  }

  public BusinessProcess createBusinessProcess(String name, String desc) {
    return createBusinessProcess(name, desc, true);
  }

  public BusinessProcess createBusinessProcess(String name, String desc, boolean persist) {
    BusinessProcess bp = BuildingBlockFactory.createBusinessProcess();
    bp.setName(name);
    bp.setDescription(desc);

    if (persist) {
      bp = businessProcessService.saveOrUpdate(bp);
    }

    return bp;
  }

  public BusinessUnit createBusinessUnit(String name, String desc) {
    return createBusinessUnit(name, desc, true);
  }

  public BusinessUnit createBusinessUnit(String name, String desc, boolean persist) {
    BusinessUnit bu = BuildingBlockFactory.createBusinessUnit();
    bu.setName(name);
    bu.setDescription(desc);

    if (persist) {
      bu = businessUnitService.saveOrUpdate(bu);
    }

    return bu;
  }

  public DateAT createDateAttributeType(String name, String description, AttributeTypeGroup group) {
    DateAT attribute = new DateAT();
    attribute.setName(name);
    attribute.setDescription(description);
    attribute.setAttributeTypeGroupTwoWay(group);

    attributeTypeService.saveOrUpdate(attribute);

    return attribute;
  }

  public void deleteAttributeType(AttributeType at) {
    attributeTypeService.deleteEntity(at);
  }

  public DateAV createDateAV(Date value, DateAT at) {
    DateAV av = new DateAV();
    if (value != null) {
      av.setValue(value);
      av.setAttributeType(at);
      at.addAttributeValue(av);
      attributeValueService.saveOrUpdate(av);
    }
    return av;
  }

  /**
   * Creates an attribute of type {@link EnumAT}.
   * 
   * @param name
   *          The attribute's name.
   * @param description
   *          The attribute's description.
   * @param isMultiValue
   *          {@code True}, if the attribute shall support multiple values. {@code False} otherwise.
   * @param group
   *          The {@link AttributeTypeGroup} the attribute shall belong to.
   * @return An attribute of type {@link EnumAT}.
   */
  public EnumAT createEnumAttributeType(String name, String description, Boolean isMultiValue, AttributeTypeGroup group) {
    EnumAT attribute = new EnumAT();
    attribute.setName(name);
    attribute.setDescription(description);
    attribute.setMultiassignmenttype(isMultiValue.booleanValue());
    attribute.setAttributeTypeGroupTwoWay(group);

    attributeTypeService.saveOrUpdate(attribute);

    return attribute;
  }

  public EnumAV createEnumAV(String name, String description, String color, EnumAT at) {
    EnumAV av = new EnumAV();
    av.setName(name);
    av.setDefaultColorHex(color);
    av.setDescription(description);
    at.addAttribueValueTwoWay(av);

    attributeValueService.saveOrUpdate(av);

    return av;
  }

  public EnumAV createEnumAV(String name, String description, EnumAT at) {

    return createEnumAV(name, description, DEFAULT_COLOR, at);
  }

  public RangeValue createRangeValue(BigDecimal value, NumberAT at) {
    RangeValue rv = new RangeValue();
    rv.setValue(value);
    rv.setAttributeTypeTwoWay(at);

    attributeTypeService.saveOrUpdate(at);

    return rv;
  }

  /**
   * Creates and saves an information system.
   * 
   * @param name
   *          The name of the information system.
   * @return See method description.
   */
  public InformationSystem createInformationSystem(String name) {
    InformationSystem is = BuildingBlockFactory.createInformationSystem();
    is.setName(name);

    return SpringServiceFactory.getInformationSystemService().saveOrUpdate(is);
  }

  public InformationSystemDomain createInformationSystemDomain(String name, String desc) {
    return creatInformationSystemDomain(name, desc, true);
  }

  public InformationSystemDomain creatInformationSystemDomain(String name, String desc, boolean persist) {
    InformationSystemDomain domain = BuildingBlockFactory.createInformationSystemDomain();
    domain.setName(name);
    domain.setDescription(desc);

    if (persist) {
      domain = informationSystemDomainService.saveOrUpdate(domain);
    }

    return domain;
  }

  public InformationSystemInterface createInformationSystemInterfaceWithNameDirection(String name, String direction, String descr,
                                                                                      InformationSystemRelease a, InformationSystemRelease b,
                                                                                      TechnicalComponentRelease tcr) {

    InformationSystemInterface connection = BuildingBlockFactory.createInformationSystemInterface();
    connection.setName(name);
    connection.setDirection(direction);
    connection.setDescription(descr);
    connection.connect(a, b);

    if (tcr != null) {
      connection.addTechnicalComponentRelease(tcr);
    }

    return isiService.saveOrUpdate(connection);
  }

  public InformationSystemInterface createInformationSystemInterface(InformationSystemRelease a, InformationSystemRelease b,
                                                                     TechnicalComponentRelease tcr, String desc) {

    InformationSystemInterface connection = BuildingBlockFactory.createInformationSystemInterface();
    connection.setDescription(desc);
    connection.connect(a, b);

    if (tcr != null) {
      connection.addTechnicalComponentRelease(tcr);
    }

    return isiService.saveOrUpdate(connection);
  }

  public InformationSystemInterface createInformationSystemInterfaceWithDirection(InformationSystemRelease a, InformationSystemRelease b,
                                                                                  Direction direction, TechnicalComponentRelease tcr, String descr) {
    InformationSystemInterface connection = BuildingBlockFactory.createInformationSystemInterface();
    connection.setInterfaceDirection(direction);
    connection.setDescription(descr);
    connection.connect(a, b);

    if (tcr != null) {
      connection.addTechnicalComponentRelease(tcr);
    }

    return isiService.saveOrUpdate(connection);
  }

  public InformationSystemRelease createInformationSystemRelease(InformationSystem is, String version) {
    return createInformationSystemRelease(is, version, null, null, null, null);
  }

  public InformationSystemRelease createInformationSystemRelease(InformationSystem is, String version, InformationSystemRelease... children) {
    InformationSystemRelease isr = BuildingBlockFactory.createInformationSystemRelease();
    is.addRelease(isr);
    isr.setVersion(version);

    for (InformationSystemRelease child : Sets.newHashSet(children)) {
      isr.addChild(child);
      child.addParent(isr);
    }

    return isrService.saveOrUpdate(isr);
  }

  public InformationSystemRelease createInformationSystemRelease(InformationSystem is, String version, String description, String startDate,
                                                                 String endDate, InformationSystemRelease.TypeOfStatus status) {

    InformationSystemRelease isr = BuildingBlockFactory.createInformationSystemRelease();
    is.addRelease(isr);
    isr.setVersion(version);

    if (description != null) {
      isr.setDescription(description);
    }

    if (status != null) {
      isr.setTypeOfStatus(status);
    }

    Date start = DateUtils.parseAsDate(startDate, Locale.GERMAN);
    Date end = DateUtils.parseAsDate(endDate, Locale.GERMAN);
    RuntimePeriod period = new RuntimePeriod(start, end);
    isr.setRuntimePeriod(period);

    return isrService.saveOrUpdate(isr);
  }

  public InfrastructureElement createInfrastructureElement(String name, String desc) {
    InfrastructureElement ie = BuildingBlockFactory.createInfrastructureElement();
    ie.setName(name);
    ie.setDescription(desc);

    return infrastructureElementService.saveOrUpdate(ie);
  }

  /**
   * Creates an attribute of type {@link NumberAT}.
   * 
   * @param name
   *          The attribute's name.
   * @param description
   *          The attribute's description.
   * @param group
   *          The {@link AttributeTypeGroup} the attribute shall belong to.
   * @return An attribute of type {@link NumberAT}.
   */
  public NumberAT createNumberAttributeType(String name, String description, AttributeTypeGroup group) {
    NumberAT attribute = new NumberAT();
    attribute.setName(name);
    attribute.setDescription(description);
    attribute.setAttributeTypeGroupTwoWay(group);

    attributeTypeService.saveOrUpdate(attribute);

    return attribute;
  }

  public NumberAV createNumberAV(BigDecimal value, NumberAT at) {
    NumberAV av = new NumberAV();
    av.setValue(value);
    at.addAttribueValueTwoWay(av);

    attributeValueService.saveOrUpdate(av);

    return av;
  }

  public Product createProduct(String name, String description) {
    return createProduct(name, description, true);
  }

  public Product createProduct(String name, String description, boolean persist) {
    Product prod = BuildingBlockFactory.createProduct();
    prod.setName(name);
    prod.setDescription(description);

    if (persist) {
      prod = productService.saveOrUpdate(prod);
    }

    return prod;
  }

  public Project createProject(String name, String desc) {
    return createProject(name, desc, "", "", true);
  }

  public Project createProject(String name, String desc, boolean persist) {
    return createProject(name, desc, "", "", persist);
  }

  public Project createProject(String name, String desc, Date startDate, Date endDate) {
    return createProject(name, desc, startDate, endDate, true);
  }

  public Project createProject(String name, String desc, Date startDate, Date endDate, boolean persist) {
    Project task = BuildingBlockFactory.createProject();
    task.setName(name);
    task.setDescription(desc);

    RuntimePeriod period = new RuntimePeriod(startDate, endDate);
    task.setRuntimePeriod(period);

    if (persist) {
      task = projectService.saveOrUpdate(task);
    }

    return task;
  }

  public Project createProject(String name, String desc, String startDate, String endDate) {
    return createProject(name, desc, startDate, endDate, true);
  }

  public Project createProject(String name, String desc, String startDate, String endDate, boolean persist) {
    Date start = DateUtils.parseAsDate(startDate, Locale.GERMAN);
    Date end = DateUtils.parseAsDate(endDate, Locale.GERMAN);

    return createProject(name, desc, start, end, persist);
  }

  /**
   * Creates an attribute of type {@link ResponsibilityAT}.
   * 
   * @param name
   *          The attribute's name.
   * @param description
   *          The attribute's description.
   * @param isMultiValue
   *          {@code True}, if the attribute shall support multiple values. {@code False} otherwise.
   * @param group
   *          The {@link AttributeTypeGroup} the attribute shall belong to.
   * @return An attribute of type {@link ResponsibilityAT}.
   */
  public ResponsibilityAT createResponsibilityAttributeType(String name, String description, Boolean isMultiValue, AttributeTypeGroup group) {
    ResponsibilityAT attribute = new ResponsibilityAT();
    attribute.setName(name);
    attribute.setDescription(description);
    attribute.setMultiassignmenttype(isMultiValue.booleanValue());
    attribute.setAttributeTypeGroupTwoWay(group);

    attributeTypeService.saveOrUpdate(attribute);

    return attribute;
  }

  public List<ResponsibilityAV> createResponsibilityAV(ResponsibilityAT at, UserEntity... entities) {
    return createResponsibilityAV(at, Arrays.asList(entities));
  }

  public List<ResponsibilityAV> createResponsibilityAV(ResponsibilityAT at, Collection<UserEntity> entities) {
    List<ResponsibilityAV> elementsToAdd = new ArrayList<ResponsibilityAV>();
    for (UserEntity entity : entities) {
      ResponsibilityAV av = new ResponsibilityAV();
      av.setDefaultColorHex("00ff00");
      av.setUserEntity(entity);
      elementsToAdd.add(av);
      at.addAttributeValuesTwoWay(Lists.newArrayList(av));
      attributeValueService.saveOrUpdate(av);
    }

    return elementsToAdd;
  }

  /**
   * Create a Role
   * 
   * @param roleName
   * @return role
   */
  public Role createRole(String roleName) {
    Role role = new Role();
    role.setRoleName(roleName);
    roleService.saveOrUpdate(role);
    return role;
  }

  public Role createRole(String roleName, List<TypeOfBuildingBlock> typeOfBuildingBlock, List<TypeOfFunctionalPermission> typeOfFunctionalPermission,
                         List<Role> consistsOfRoles) {
    Role role = new Role();
    role.setRoleName(roleName);

    for (TypeOfBuildingBlock tobb : typeOfBuildingBlock) {
      BuildingBlockType bbt = getBuildingBlockType(tobb);
      new Role2BbtPermission(role, bbt, EditPermissionType.UPDATE).connect();
      new Role2BbtPermission(role, bbt, EditPermissionType.CREATE).connect();
      new Role2BbtPermission(role, bbt, EditPermissionType.DELETE).connect();
    }

    for (TypeOfFunctionalPermission tofp : typeOfFunctionalPermission) {
      PermissionFunctional permFunc = roleService.getPermissionFunctionalByType(tofp);
      role.addPermissionFunctionalTwoWay(permFunc);
    }

    for (Role r : consistsOfRoles) {
      role.addConsistsOfRoleTwoWay(r);
    }

    return roleService.saveOrUpdate(role);
  }

  public Role createRole(String roleName, List<TypeOfBuildingBlock> typeOfBuildingBlock, List<TypeOfFunctionalPermission> typeOfFunctionalPermission) {
    return createRole(roleName, typeOfBuildingBlock, typeOfFunctionalPermission, Lists.<Role> newArrayList());
  }

  public Role createRole(String roleName, Role... roles) {
    return createRole(roleName, Lists.<TypeOfBuildingBlock> newArrayList(), Lists.<TypeOfFunctionalPermission> newArrayList(),
        Lists.newArrayList(roles));
  }

  public TechnicalComponentRelease createTCRelease(TechnicalComponent tc, String version, boolean persist) {
    return createTCRelease(tc, version, null, null, null, null, persist);
  }

  public TechnicalComponentRelease createTCRelease(TechnicalComponent tc, String version, String description, String startDate, String endDate,
                                                   TechnicalComponentRelease.TypeOfStatus status, boolean persist) {

    TechnicalComponentRelease rel = BuildingBlockFactory.createTechnicalComponentRelease();

    tc.addRelease(rel);
    rel.setVersion(version);
    rel.setDescription(description);

    if (status != null) {
      rel.setTypeOfStatus(status);
    }

    Date start = DateUtils.parseAsDate(startDate, Locale.GERMAN);
    Date end = DateUtils.parseAsDate(endDate, Locale.GERMAN);
    RuntimePeriod period = new RuntimePeriod(start, end);
    rel.setRuntimePeriod(period);

    if (persist) {
      rel = tcrService.saveOrUpdate(rel);
    }

    return rel;
  }

  public TechnicalComponent createTechnicalComponent(String name, boolean availableForConnections, boolean persist) {
    TechnicalComponent ci = BuildingBlockFactory.createTechnicalComponent();
    ci.setName(name);
    ci.setAvailableForInterfaces(availableForConnections);

    if (persist) {
      ci = technicalComponentService.saveOrUpdate(ci);
    }

    return ci;
  }

  /**
   * Creates an attribute of type {@link TextAT}.
   * 
   * @param name
   *          The attribute's name.
   * @param description
   *          The attribute's description.
   * @param isMultiLine
   *          {@code True}, if the attribute shall support multiple multiple lines of text. {@code
   *          False} otherwise.
   * @param group
   *          The {@link AttributeTypeGroup} the attribute shall belong to.
   * @return An attribute of type {@link TextAT}.
   */
  public TextAT createTextAttributeType(String name, String description, boolean isMultiLine, AttributeTypeGroup group) {
    TextAT attribute = new TextAT();
    attribute.setName(name);
    attribute.setDescription(description);
    attribute.setMultiline(isMultiLine);
    attribute.setAttributeTypeGroupTwoWay(group);

    return (TextAT) attributeTypeService.saveOrUpdate(attribute);
  }

  public TextAV createTextAV(String value, TextAT at) {
    TextAV av = new TextAV();
    av.setValue(value);
    av.setAttributeTypeTwoWay(at);

    return (TextAV) attributeValueService.saveOrUpdate(av);
  }

  /**
   * Creates a Transport
   */
  public Transport createTransport(BusinessObject bo, InformationSystemInterface c, Direction direction) {
    Transport transport = BuildingBlockFactory.createTransport();
    c.addTransport(transport);
    transport.addBusinessObject(bo);
    transport.setDirection(direction);

    return transportService.saveOrUpdate(transport);
  }

  public Timeseries createTimeseries(BuildingBlock bd, AttributeType at, TimeseriesEntry... entries) {
    Timeseries testSeries = new Timeseries();
    testSeries.setAttribute(at);
    testSeries.setBuildingBlock(bd);
    for (TimeseriesEntry entry : entries) {
      testSeries.addEntry(entry);
    }
    return timeseriesService.saveOrUpdateWithBbUpdate(testSeries);
  }

  public DateInterval createDateInterval(String name, String defaultColorHex, DateAT startDateAT, DateAT endDateAT) {
    DateInterval dateInterval = new DateInterval();
    dateInterval.setName(name);
    dateInterval.setStartDate(startDateAT);
    dateInterval.setEndDate(endDateAT);
    dateInterval.setDefaultColorHex(defaultColorHex);
    return dateIntervalService.saveOrUpdate(dateInterval);
  }

  /**
   * Creates a User
   * 
   * @param loginName
   * @param firstName
   * @param lastName
   * @param dataSource
   * @return user
   */
  public User createUser(String loginName, String firstName, String lastName, String dataSource) {
    User user = new User();
    user.setLoginName(loginName.toLowerCase());
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setDataSource(dataSource);

    return userService.saveOrUpdate(user);
  }

  /**
   * Creates a User with loginName = firstName = lastName = name, and MASTER datasource
   * 
   * @param name
   * @return user
   */
  public User createUser(String name) {
    return createUser(name, name, name, "MASTER");
  }

  /**
   * @return User context with user admin and role admin.
   * @throws de.iteratec.iteraplan.common.error.IteraplanException
   */
  public UserContext createUserContext() {
    User user = getEmptyUser();
    user.setLoginName("system");
    Role supervisorRole = roleService.getSupervisorRole();
    Set<Role> roles = new HashSet<Role>();
    roles.add(supervisorRole);
    return new UserContext(user.getLoginName(), roles, new Locale("de"), userService.getUserByLoginIfExists(user.getLoginName()));
  }

  public UserGroup createUserGroup(String ugName, String ugDescription, Set<UserEntity> users, Set<BuildingBlock> bbs) {
    UserGroup ug = new UserGroup();
    ug.setName(ugName);
    ug.setDescription(ugDescription);

    for (UserEntity user : users) {
      ug.addUserEntity(user);
    }

    for (BuildingBlock block : bbs) {
      ug.addOwnedBuildingBlock(block);
    }

    return userGroupService.saveOrUpdate(ug);
  }

  public UserGroup createUserGroup(String ugName, String ugDescription, UserEntity... users) {
    return createUserGroup(ugName, ugDescription, Sets.newHashSet(users), Sets.<BuildingBlock> newHashSet());
  }

  public UserGroup createUserGroup(String ugName, UserEntity... users) {
    return createUserGroup(ugName, "", users);
  }

  public BuildingBlockType getBuildingBlockType(TypeOfBuildingBlock tobb) {
    return buildingBlockTypeService.getBuildingBlockTypeByType(tobb);
  }

  public List<BuildingBlockType> getAllBuildingBlockTypes(Iterable<TypeOfBuildingBlock> tobbs) {
    List<BuildingBlockType> list = Lists.newArrayList();
    for (TypeOfBuildingBlock tobb : tobbs) {
      list.add(getBuildingBlockType(tobb));
    }
    return list;
  }

  public List<TypeOfBuildingBlock> getAllTypeofbuildingblock() {
    List<TypeOfBuildingBlock> result = new ArrayList<TypeOfBuildingBlock>();
    result.add(TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
    result.add(TypeOfBuildingBlock.BUSINESSOBJECT);
    result.add(TypeOfBuildingBlock.BUSINESSPROCESS);
    result.add(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);
    result.add(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT);
    result.add(TypeOfBuildingBlock.INFORMATIONSYSTEM);
    result.add(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN);
    result.add(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    result.add(TypeOfBuildingBlock.PROJECT);
    result.add(TypeOfBuildingBlock.BUSINESSUNIT);
    result.add(TypeOfBuildingBlock.PRODUCT);
    result.add(TypeOfBuildingBlock.TECHNICALCOMPONENT);
    result.add(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    result.add(TypeOfBuildingBlock.TRANSPORT);
    return result;
  }

  public Role getEmptyRole() {
    Role role = new Role();
    role.setRoleName("");
    role.setConsistsOfRoles(new HashSet<Role>());
    role.setElementOfRoles(new HashSet<Role>());
    role.setPermissionsAttrTypeGroup(new HashSet<PermissionAttrTypeGroup>());
    role.setPermissionsBbt(new HashSet<Role2BbtPermission>());
    role.setPermissionsFunctional(new HashSet<PermissionFunctional>());
    return role;
  }

  public User getEmptyUser() {
    User user = new User();
    user.setLoginName("guest");
    user.setFirstName("");
    user.setLastName("");
    user.setDataSource("MASTER");
    user.setId(Integer.valueOf(0));
    user.setParentUserGroups(new HashSet<UserGroup>());
    return user;
  }

  public AttributeTypeGroup getDefaultATG() {
    return atgService.getStandardAttributeTypeGroup();
  }

}