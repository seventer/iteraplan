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
package de.iteratec.iteraplan.presentation.dialog;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.BusinessMappingService;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.dialog.BusinessUnit.BusinessUnitController;
import de.iteratec.iteraplan.presentation.dialog.InformationSystem.BusinessMappingController;
import de.iteratec.iteraplan.presentation.dialog.InformationSystem.InformationSystemController;


/**
 *
 */
public final class JsonApiTestHelper {

  public static final int                        BUSINESSUNIT_VIRTUAL_ROOT_ID       = 39;
  private static final int                       INFORMATIONSYSTEM_ID_OFFSET        = 40;
  private static final int                       INFORMATIONSYSTEMRELEASE_ID_OFFSET = 60;
  private static final int                       BUSINESSUNIT_ID_OFFSET             = 80;
  private static final int                       BUSINESSMAPPING_ID_OFFSET          = 100;

  private int                                    freshId                            = 150;

  private static final String                    INFORMTATIONSYSTEM_NAME            = "InformationSystem";
  private static final String                    BUSINESSUNIT_NAME                  = "BusinessUnit";
  private static final String                    STANDARD_DESCRIPTION               = "Some_Description";
  private static final String                    STANDARD_VERSION                   = "Some_Version";

  private InformationSystemController            informationSystemController;

  private BusinessUnitController                 businessUnitController;

  private BusinessMappingController              businessMappingController;

  private Map<Integer, InformationSystem>        informationsystems                 = Maps.newHashMap();
  private Map<Integer, InformationSystemRelease> informationsystemreleases          = Maps.newHashMap();
  private Map<Integer, BusinessUnit>             businessUnits                      = Maps.newHashMap();
  private Map<Integer, BusinessMapping>          businessMappings                   = Maps.newHashMap();

  public final Calendar                          modificationDate                   = Calendar.getInstance();

  private BuildingBlockTypeService               oldBBTypeService;
  private BuildingBlockServiceLocator            oldBBServiceLocator;
  private UserContext                            oldContext;

  private AttributeTypeGroup                     atgroup1;
  private AttributeTypeGroup                     atgroup2;

  private User                                   alice;
  private final boolean                          limitedPermissions;

  public JsonApiTestHelper(boolean limitedPermissions) {
    this.limitedPermissions = limitedPermissions;
  }

  public void resetTestData() {
    informationsystems.clear();
    informationsystemreleases.clear();
    businessUnits.clear();
    businessMappings.clear();

    initBBFactory();

    createAttributeTypes();

    initUserContext();

    createInformationSystems();

    createBusinessUnits();

    createBusinessMappings();

    setupISController();

    setupBUController();

    setupBMController();
  }

  /**
   * initializes the UserContext for testing purposes with a single user with a two roles, each connected with one of the AttributeTypeGroups created in createAttributeTypes.
   */
  private void initUserContext() {
    cleanupUserContext();

    alice = new User();
    alice.setFirstName("Alice");
    alice.setId(getFreshId());
    alice.setLoginName("alice");

    Role role = new Role();
    role.setId(getFreshId());
    role.setRoleName("atgroup1");
    PermissionAttrTypeGroup patg1 = new PermissionAttrTypeGroup(getFreshId(), atgroup1);
    patg1.setReadPermission(Boolean.TRUE);
    role.setPermissionsAttrTypeGroup(Sets.newHashSet(patg1));
    patg1.setRole(role);
    atgroup1.addPermissionTwoWay(patg1);
    role.addPermissionFunctionalTwoWay(new PermissionFunctional(TypeOfFunctionalPermission.BUSINESSUNIT));
    role.addPermissionFunctionalTwoWay(new PermissionFunctional(TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE));
    role.addPermissionFunctionalTwoWay(new PermissionFunctional(TypeOfFunctionalPermission.BUSINESSMAPPING));
    Set<Role> roles = Sets.newHashSet(role);

    //have to create the role and associate it with patg1 even if permissions should be limited, since default permission is false. see ITERAPLAN-1858 for details.
    Role role2 = new Role();
    role2.setRoleName("atgroup2");
    PermissionAttrTypeGroup patg2 = new PermissionAttrTypeGroup(getFreshId(), atgroup2);
    patg2.setReadPermission(Boolean.TRUE);
    role2.setPermissionsAttrTypeGroup(Sets.newHashSet(patg2));
    patg2.setRole(role2);
    atgroup2.addPermissionTwoWay(patg2);
    if (!limitedPermissions) {
      roles.add(role2);
    }

    UserContext contextMock = new UserContext("test", roles, new Locale("de"), alice);

    UserContext.setCurrentUserContext(contextMock);

  }

  /**
   * creates and initializes two AttributeTypeGroups. One named "atgroup1", containing only a single TextAT. The other named "atgroup2", containing one of each possible ATs (Text, Number, Responsibility, Enum, Date).
   * All AttributeTypes are enabled for BusinessUnits, InformationSystemReleases and InformationSystems.  
   */
  private void createAttributeTypes() {
    atgroup1 = new AttributeTypeGroup();
    atgroup1.setId(Integer.valueOf(28));
    atgroup2 = new AttributeTypeGroup();
    atgroup2.setId(Integer.valueOf(29));

    atgroup1.setName("atgroup1");
    atgroup2.setName("atgroup2");

    atgroup1.setPosition(Integer.valueOf(0));
    atgroup2.setPosition(Integer.valueOf(1));

    Set<BuildingBlockType> bbtypes = Sets.newHashSet();
    bbtypes.add(BuildingBlockFactory.getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEM));
    bbtypes.add(BuildingBlockFactory.getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    bbtypes.add(BuildingBlockFactory.getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSUNIT));

    TextAT singleTextAT = new TextAT();
    singleTextAT.setId(Integer.valueOf(30));
    singleTextAT.setName(atgroup1.getName() + ".someText");
    atgroup1.addAttributeTypeTwoWay(singleTextAT);
    singleTextAT.addBuildingBlockTypesTwoWay(bbtypes);

    DateAT someDateAT = new DateAT();
    someDateAT.setId(Integer.valueOf(31));
    someDateAT.setName(atgroup2.getName() + ".someDate");
    atgroup2.addAttributeTypeTwoWay(someDateAT);
    someDateAT.addBuildingBlockTypesTwoWay(bbtypes);

    EnumAT someEnumAT = new EnumAT();
    someEnumAT.setId(Integer.valueOf(32));
    someEnumAT.setName(atgroup2.getName() + ".someEnum");
    EnumAV value1 = new EnumAV();
    value1.setAttributeTypeTwoWay(someEnumAT);
    value1.setName("enumValue1");
    EnumAV value2 = new EnumAV();
    value2.setAttributeTypeTwoWay(someEnumAT);
    value2.setName("enumValue2");
    atgroup2.addAttributeTypeTwoWay(someEnumAT);
    someEnumAT.addBuildingBlockTypesTwoWay(bbtypes);

    NumberAT someNumberAT = new NumberAT();
    someNumberAT.setId(Integer.valueOf(33));
    someNumberAT.setName(atgroup2.getName() + ".someNumber");
    atgroup2.addAttributeTypeTwoWay(someNumberAT);
    someNumberAT.addBuildingBlockTypesTwoWay(bbtypes);

    ResponsibilityAT someResAT = new ResponsibilityAT();
    someResAT.setId(Integer.valueOf(34));
    someResAT.setName(atgroup2.getName() + ".someResponsibility");
    atgroup2.addAttributeTypeTwoWay(someResAT);
    someResAT.addBuildingBlockTypesTwoWay(bbtypes);

    TextAT someTextAT = new TextAT();
    someTextAT.setId(Integer.valueOf(35));
    someTextAT.setName(atgroup2.getName() + ".someType");
    atgroup2.addAttributeTypeTwoWay(someTextAT);
    someTextAT.addBuildingBlockTypesTwoWay(bbtypes);
  }

  /**
   * creates a number of instances of BusinessUnit and stores them with their ID as key in the map of BusinessUnits.
   * BU0 will be parent of BU2
   */
  private void createBusinessUnits() {
    for (int i = 0; i < 3; i++) {
      BusinessUnit bu = BuildingBlockFactory.createBusinessUnit();
      Integer id = getBusinessUnitId(i);
      bu.setId(id);
      bu.setLastModificationTime(modificationDate.getTime());
      bu.setName(getBusinessUnitName(i));

      if (i == 2) {
        bu.setParent(businessUnits.get(getBusinessUnitId(0)));
      }

      bu.setDescription(getDescription(i));

      setAttributes(bu);

      businessUnits.put(id, bu);
    }
  }

  /**
   * sets default values for all attribute types for the given BuildingBlock.
   * @param bb
   */
  private void setAttributes(BuildingBlock bb) {
    Set<AttributeValueAssignment> avas = Sets.newHashSet();

    avas.addAll(getAttributeValueAssignments(atgroup1, bb));
    avas.addAll(getAttributeValueAssignments(atgroup2, bb));

    bb.setAttributeValueAssignments(avas);
  }

  private Set<AttributeValueAssignment> getAttributeValueAssignments(AttributeTypeGroup atgroup, BuildingBlock bb) {
    Set<AttributeValueAssignment> avas = Sets.newHashSet();

    for (AttributeType at : atgroup.getAttributeTypes()) {
      AttributeValue av = null;
      if (at instanceof DateAT) {
        av = new DateAV((DateAT) at, modificationDate.getTime());
      }
      if (at instanceof EnumAT) {
        av = new EnumAV();
        ((EnumAV) av).setAttributeTypeTwoWay((EnumAT) at);
        ((EnumAV) av).setName("enumValue1");

      }
      if (at instanceof NumberAT) {
        av = new NumberAV((NumberAT) at, BigDecimal.valueOf(10l));
      }
      if (at instanceof ResponsibilityAT) {
        av = new ResponsibilityAV((ResponsibilityAT) at);

        ((ResponsibilityAV) av).setUserEntity(alice);
      }
      if (at instanceof TextAT) {
        av = new TextAV((TextAT) at, "someText");
      }
      //      bu.setAttributeValueAssignments(attributeValueAssignments);
      av.setId(getFreshId());
      AttributeValueAssignment ava = new AttributeValueAssignment(bb, av);
      ava.setId(getFreshId());

      avas.add(ava);
    }

    return avas;

  }

  /**
   * creates a number of business mappings such that:
   * IS0 and BU0 are connected by two BM's (number 0 and 1)
   * IS1 and BU1 are connected by one BM   (number 2)
   * IS0 and BU1 are connected by one BM   (number 3)
   * Is2 and BU2 are not connected
   */
  private void createBusinessMappings() {
    BusinessMapping bm = BuildingBlockFactory.createBusinessMapping();
    Integer id = getBusinessMappingId(0);
    bm.setId(id);
    bm.addInformationSystemRelease(informationsystemreleases.get(getInformationSystemReleaseId(0)));
    bm.addBusinessUnit(businessUnits.get(getBusinessUnitId(0)));
    businessMappings.put(id, bm);

    bm = BuildingBlockFactory.createBusinessMapping();
    id = getBusinessMappingId(1);
    bm.setId(id);
    bm.addInformationSystemRelease(informationsystemreleases.get(getInformationSystemReleaseId(0)));
    bm.addBusinessUnit(businessUnits.get(getBusinessUnitId(0)));
    businessMappings.put(id, bm);

    bm = BuildingBlockFactory.createBusinessMapping();
    id = getBusinessMappingId(2);
    bm.setId(id);
    bm.addInformationSystemRelease(informationsystemreleases.get(getInformationSystemReleaseId(0)));
    bm.addBusinessUnit(businessUnits.get(getBusinessUnitId(1)));
    businessMappings.put(id, bm);

    bm = BuildingBlockFactory.createBusinessMapping();
    id = getBusinessMappingId(3);
    bm.setId(id);
    bm.addInformationSystemRelease(informationsystemreleases.get(getInformationSystemReleaseId(1)));
    bm.addBusinessUnit(businessUnits.get(getBusinessUnitId(1)));
    businessMappings.put(id, bm);

  }

  /**
   * creates a number of instances of InformationSystem and stores them with their ID as key in the map of InformationSystems. Additionally, each InformationSystem gets a single InformationSystemRelease, which is also stored in the according map.
   * ISR 0 will be the parent of ISR1
   */
  private void createInformationSystems() {
    for (int i = 0; i < 3; i++) {
      InformationSystem is = BuildingBlockFactory.createInformationSystem();
      Integer id = Integer.valueOf(INFORMATIONSYSTEM_ID_OFFSET + i);
      is.setId(id);
      is.setLastModificationTime(modificationDate.getTime());
      is.setName(getInformationSystemName(i));
      informationsystems.put(id, is);
      InformationSystemRelease isr = BuildingBlockFactory.createInformationSystemRelease();

      isr.setDescription("isrdescription" + i);
      Integer isrId = getInformationSystemReleaseId(i);
      isr.setId(isrId);
      isr.setTypeOfStatus(TypeOfStatus.CURRENT);
      isr.setLastModificationTime(modificationDate.getTime());

      isr.setDescription(getDescription(i));

      isr.setVersion(getInformationSystemReleaseVersion(i));

      if (i == 1) {
        isr.setParent(informationsystemreleases.get(getInformationSystemReleaseId(0)));
      }

      is.addRelease(isr);

      setAttributes(isr);
      informationsystemreleases.put(isrId, isr);
    }
  }

  /**
   * initialises the {@code BuildingBlockFactory} such that it uses services created with EasyMock instead of trying to access a database in order to ensure testing is independent of database status.
   */
  @SuppressWarnings("PMD.AvoidCatchingNPE")
  private void initBBFactory() {
    //setup the type service
    BuildingBlockTypeService typeService = createMock(BuildingBlockTypeService.class);
    expect(typeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEM)).andReturn(
        new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEM)).anyTimes();

    expect(typeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)).andReturn(
        new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)).anyTimes();

    expect(typeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSUNIT)).andReturn(
        new BuildingBlockType(TypeOfBuildingBlock.BUSINESSUNIT)).anyTimes();

    expect(typeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSMAPPING)).andReturn(
        new BuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING)).anyTimes();

    BuildingBlockServiceLocator locatormock = createMock(BuildingBlockServiceLocator.class);

    BusinessUnitService buservicemock = createMock(BusinessUnitService.class);

    expect(locatormock.getBuService()).andReturn(buservicemock).anyTimes();

    BusinessUnit virtualRoot = new BusinessUnit();

    virtualRoot.setId(Integer.valueOf(39));

    virtualRoot.setParent(null);

    expect(buservicemock.getFirstElement()).andReturn(virtualRoot).anyTimes();

    //store the old services in order to be able to restore them after testing
    //a nullpointerexception when trying to get the service indicates that spring is not available, thus the services need not be reset
    try {
      oldBBServiceLocator = BuildingBlockFactory.getBbServiceLocator();
    } catch (NullPointerException ex) {
      oldBBServiceLocator = null;
    }
    try {
      oldBBTypeService = BuildingBlockFactory.getBbTypeService();
    } catch (NullPointerException ex) {
      oldBBTypeService = null;
    }
    BuildingBlockFactory.setBbServiceLocator(locatormock);
    BuildingBlockFactory.setBbTypeService(typeService);

    replay(typeService);
    replay(locatormock);
    replay(buservicemock);
  }

  private void setupISController() {
    List<InformationSystemRelease> isrs = Lists.newArrayList(informationsystemreleases.values());
    Collections.sort(isrs, new BuildingBlockComparator());
    InformationSystemReleaseService mockISRService = createMock(InformationSystemReleaseService.class);
    for (int i = 0; i < 3; i++) {
      expect(mockISRService.loadObjectById(getInformationSystemReleaseId(i))).andReturn(
          informationsystemreleases.get(getInformationSystemReleaseId(i))).anyTimes();
    }
    expect(mockISRService.loadElementList(false)).andReturn(isrs).anyTimes();

    replay(mockISRService);

    informationSystemController = new InformationSystemController(mockISRService);

  }

  private void setupBUController() {
    List<BusinessUnit> bus = Lists.newArrayList(businessUnits.values());

    BusinessUnitService mockBUService = createMock(BusinessUnitService.class);
    for (int i = 0; i < 3; i++) {
      expect(mockBUService.loadObjectById(getBusinessUnitId(i))).andReturn(businessUnits.get(getBusinessUnitId(i))).anyTimes();
    }
    expect(mockBUService.loadElementList()).andReturn(bus).anyTimes();

    replay(mockBUService);

    businessUnitController = new BusinessUnitController(mockBUService);

  }

  private void setupBMController() {
    List<BusinessMapping> bms = Lists.newArrayList(businessMappings.values());

    BusinessMappingService mockBMService = createMock(BusinessMappingService.class);
    expect(mockBMService.loadElementList()).andReturn(bms).anyTimes();
    replay(mockBMService);
    businessMappingController = new BusinessMappingController(mockBMService);
  }

  public InformationSystemController getInformationSystemController() {
    if (informationSystemController == null) {
      resetTestData();
    }
    return informationSystemController;
  }

  public BusinessUnitController getBusinessUnitController() {
    if (businessUnitController == null) {
      resetTestData();
    }
    return businessUnitController;
  }

  public BusinessMappingController getBusinessMappingController() {
    if (businessMappingController == null) {
      resetTestData();
    }
    return businessMappingController;
  }

  /**
   * 
   * @param response
   * @param resultClass
   * @return The content of the response, assuming it contains JSON information, in an Object as given by the resultClass. Note that this method might crash if it is given a resultClass that does not match the top level JSON Objects type.
   */
  public <T> T readJson(MockHttpServletResponse response, Class<T> resultClass) {
    try {
      ObjectMapper om = new ObjectMapper();

      String content = response.getContentAsString();

      return om.readValue(content, resultClass);

    } catch (UnsupportedEncodingException ex) {
      return null;
    } catch (IOException ex) {
      return null;
    }

  }

  public String getInformationSystemName(int number) {
    return INFORMTATIONSYSTEM_NAME + number;
  }

  public String getInformationSystemNameWithVersion(int number) {
    return INFORMTATIONSYSTEM_NAME + number + " # " + getInformationSystemReleaseVersion(number);
  }

  public Integer getInformationSystemReleaseId(int number) {
    return Integer.valueOf(INFORMATIONSYSTEMRELEASE_ID_OFFSET + number);
  }

  public Integer getBusinessUnitId(int number) {
    return Integer.valueOf(BUSINESSUNIT_ID_OFFSET + number);
  }

  public String getBusinessUnitName(int number) {
    return BUSINESSUNIT_NAME + number;
  }

  public String getDescription(int number) {
    return STANDARD_DESCRIPTION + number;
  }

  public String getInformationSystemReleaseVersion(int number) {
    return STANDARD_VERSION + number;
  }

  public Integer getBusinessMappingId(int number) {
    return Integer.valueOf(BUSINESSMAPPING_ID_OFFSET + number);
  }

  /**
   * restores the configuration of the BuildingBlockFactory.
   * Also resets the private fields. 
   */
  public void cleanup() {
    freshId = 150;

    informationSystemController = null;
    businessUnitController = null;
    businessMappingController = null;
    if (oldBBServiceLocator != null) {
      BuildingBlockFactory.setBbServiceLocator(oldBBServiceLocator);
    }
    if (oldBBTypeService != null) {
      BuildingBlockFactory.setBbTypeService(oldBBTypeService);
    }
    cleanupUserContext();
  }

  private void cleanupUserContext() {
    if (oldContext != null) {
      UserContext.setCurrentUserContext(oldContext);
    }
    alice = null;
  }

  private Integer getFreshId() {
    freshId++;
    return Integer.valueOf(freshId);
  }

  public AttributeTypeGroup getATGroup1() {
    return atgroup1;
  }

  public AttributeTypeGroup getATGroup2() {
    return atgroup2;
  }

  public void validateAttributeGroup1(Map<String, Object> attributes, AttributeTypeGroup atgroup) {
    Assert.assertEquals(2, attributes.size());
    Assert.assertEquals(atgroup.getName(), attributes.get("name"));

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> attrs = (List<Map<String, Object>>) attributes.get("attributes");
    Assert.assertEquals(atgroup.getAttributeTypes().size(), attrs.size());

    //build up a map identifying the attribute maps by their name
    Map<String, Map<String, Object>> attrsByName = Maps.newHashMap();
    for (Map<String, Object> attrMap : attrs) {
      Assert.assertNotNull(attrMap.get("attributeName"));
      attrsByName.put((String) attrMap.get("attributeName"), attrMap);
    }

    for (AttributeType at : atgroup.getAttributeTypes()) {
      Map<String, Object> attr = attrsByName.get(at.getName());

      Assert.assertNotNull(attr);

      String attrType = (String) attr.get("attributeType");
      Object value = attr.get("value");
      //      Assert.assertNotNull(value);

      if (at instanceof TextAT) {
        Assert.assertEquals("TEXT", attrType);
        Assert.assertEquals("someText", value);

      }
      if (at instanceof NumberAT) {
        Assert.assertEquals("NUMBER", attrType);
        Assert.assertEquals(Integer.valueOf(10), value);
      }
      if (at instanceof DateAT) {
        Assert.assertEquals("DATE", attrType);
        try {
          String time = new ObjectMapper().writeValueAsString(modificationDate.getTime());
          Assert.assertEquals(time, value.toString());
        } catch (IOException ex) {
          throw new AssertionFailedError();
        }

      }
      if (at instanceof EnumAT) {
        Assert.assertEquals("ENUM", attrType);
        Assert.assertEquals("enumValue1", value);
      }
      if (at instanceof ResponsibilityAT) {
        Assert.assertEquals("RESPONSIBILITY", attrType);
        Assert.assertEquals(alice.getLoginName(), value);
      }

    }
  }
}
