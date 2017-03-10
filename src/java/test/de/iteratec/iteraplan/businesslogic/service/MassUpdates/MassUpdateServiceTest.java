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
package de.iteratec.iteraplan.businesslogic.service.MassUpdates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeMu;
import de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainService;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.businesslogic.service.BusinessFunctionService;
import de.iteratec.iteraplan.businesslogic.service.BusinessObjectService;
import de.iteratec.iteraplan.businesslogic.service.BusinessProcessService;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemService;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.businesslogic.service.MassUpdateService;
import de.iteratec.iteraplan.businesslogic.service.ProductService;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.presentation.dialog.InformationSystem.model.InformationSystemReleaseNameComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.InformationSystemReleaseCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttribute;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttributeItem;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateLine;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


/**
 * Test class for mass update service methods.
 */
public class MassUpdateServiceTest extends BaseTransactionalTestSupport {

  private static final Logger               LOGGER                     = Logger.getIteraplanLogger(MassUpdateServiceTest.class);

  @Autowired
  private MassUpdateService                 massUpdateService;
  @Autowired
  private AttributeTypeDAO                  attributeTypeDAO;
  @Autowired
  private AttributeTypeGroupDAO             attributeTypeGroupDAO;
  @Autowired
  private InformationSystemReleaseService   informationSystemReleaseService;
  @Autowired
  private InformationSystemService          informationSystemService;
  @Autowired
  private ArchitecturalDomainService        architecturalDomainService;
  @Autowired
  private BusinessDomainService             businessDomainService;
  @Autowired
  private BusinessFunctionService           businessFunctionService;
  @Autowired
  private BusinessObjectService             businessObjectService;
  @Autowired
  private BusinessProcessService            businessProcessService;
  @Autowired
  private BusinessUnitService               businessUnitService;
  @Autowired
  private TechnicalComponentReleaseService  technicalComponentReleaseService;
  @Autowired
  private TechnicalComponentService         technicalComponentService;
  @Autowired
  private InfrastructureElementService      infrastructureElementService;
  @Autowired
  private InformationSystemInterfaceService informationSystemInterfaceService;
  @Autowired
  private InformationSystemDomainService    informationSystemDomainService;
  @Autowired
  private ProjectService                    projectService;
  @Autowired
  private ProductService                    productService;
  @Autowired
  private TestDataHelper2                   testDataHelper;

  private static final String               ENUM_AT_NAME_A             = "EnumAT A";
  private static final String               TEST_DESCRIPTION           = "testDesription";

  private static final String               STANDARD_START_DATE_2005   = "1.1.2005";
  private static final String               STANDARD_END_DATE_2005     = "31.12.2005";

  private static final String               INCORRECT_AVA_NR_ERROR_MSG = "Incorrect number of AVAs!";

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testExecuteAttributeMassUpdateMVANewValueCorrect() {

    arrangeIpureleaseEnumAvMultiValueDatabase();
    commit();
    beginTransaction();

    // multi value attribute and new value:
    InformationSystemRelease isr = informationSystemReleaseService.getInformationSystemsFiltered(null, true).get(0);
    LOGGER.debug("use Ipurelease for test: " + isr.getHierarchicalName());

    AttributeType attributeType = attributeTypeDAO.getAttributeTypeByName("EnumAT B");
    List<EnumAV> enumAVs = ((EnumAT) attributeType).getSortedAttributeValues();
    String[] selectedAttributeValueStringIds = new String[] { enumAVs.get(0).getId().toString(), enumAVs.get(1).getId().toString(),
        enumAVs.get(2).getId().toString() };

    MassUpdateAttributeItem mui = new MassUpdateAttributeItem(isr, attributeType.getId());
    mui.setSelectedAttributeValueStringIds(selectedAttributeValueStringIds);
    MassUpdateLine<InformationSystemRelease> line = new MassUpdateLine<InformationSystemRelease>();
    line.setBuildingBlockToUpdate(isr);
    MassUpdateAttribute attribute = new MassUpdateAttribute(0);
    attribute.setMassUpdateAttributeItem(mui);
    attribute.setType(BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE);
    line.addAttribute("id", attribute);
    massUpdateService.updateAttributes(line);
    commit();

    beginTransaction();
    isr = informationSystemReleaseService.getInformationSystemsFiltered(null, true).get(0);
    assertEquals(INCORRECT_AVA_NR_ERROR_MSG, 3, isr.getAttributeValueAssignments().size());
    commit();
  }

  @Test
  public void testExecuteAttributeMassUpdateMVANoNewValueCorrect() {
    // arrange:
    arrangeIpureleaseEnumAvMultiValueDatabase();
    commit();
    beginTransaction();

    // act and assert:

    // multi value attribute and no new value:
    InformationSystemRelease instance = informationSystemReleaseService.getInformationSystemsFiltered(null, true).get(0);
    LOGGER.debug("use Ipurelease for test: " + instance.getHierarchicalName());
    AttributeType attributeType = attributeTypeDAO.getAttributeTypeByName("EnumAT B");
    String newAttributeValue = null;
    List<EnumAV> enumAVs = ((EnumAT) attributeType).getSortedAttributeValues();
    String[] selectedAttributeValueStringIds = new String[] { enumAVs.get(1).getId().toString(), enumAVs.get(2).getId().toString(),
        enumAVs.get(3).getId().toString() };

    MassUpdateAttributeItem mui = new MassUpdateAttributeItem(instance, attributeType.getId());
    mui.setNewAttributeValue(newAttributeValue);
    // mui.setNewAttributeValueSelected(true);
    mui.setSelectedAttributeValueStringIds(selectedAttributeValueStringIds);
    MassUpdateLine<InformationSystemRelease> line = new MassUpdateLine<InformationSystemRelease>();
    line.setBuildingBlockToUpdate(instance);
    MassUpdateAttribute attribute = new MassUpdateAttribute(0);
    attribute.setMassUpdateAttributeItem(mui);
    attribute.setType(BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE);
    line.addAttribute("id", attribute);
    massUpdateService.updateAttributes(line);
    commit();

    beginTransaction();
    instance = informationSystemReleaseService.getInformationSystemsFiltered(null, true).get(0);
    assertEquals(INCORRECT_AVA_NR_ERROR_MSG, 3, instance.getAttributeValueAssignments().size());
    commit();

  }

  @Test
  public void testExecuteAttributeMassUpdateSVAIncorrect() {
    // arrange:
    arrangeIpureleaseEnumAvSingleValueDatabase();
    commit();
    beginTransaction();

    // act and assert:

    // single value attribute and new value:
    InformationSystemRelease instance = informationSystemReleaseService.getInformationSystemsFiltered(null, true).get(0);
    LOGGER.debug("use Ipurelease for test: " + instance.getHierarchicalName());
    Integer attributeTypeId = attributeTypeDAO.getAttributeTypeByName(ENUM_AT_NAME_A).getId();
    String[] selectedAttributeValueStringIds = new String[] { "2", "3" };

    MassUpdateAttributeItem mui = new MassUpdateAttributeItem(instance, attributeTypeId);
    mui.setSelectedAttributeValueStringIds(selectedAttributeValueStringIds);
    MassUpdateLine<InformationSystemRelease> line = new MassUpdateLine<InformationSystemRelease>();
    line.setBuildingBlockToUpdate(instance);
    MassUpdateAttribute attribute = new MassUpdateAttribute(0);
    attribute.setMassUpdateAttributeItem(mui);
    attribute.setType(BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE);
    line.addAttribute("id", attribute);

    try {
      massUpdateService.updateAttributes(line);
      fail("Illegal attempt to assign more than one attribute value for a single value attribute!");
    } catch (IteraplanBusinessException ex) {
      assertEquals(ex.getErrorCode(), IteraplanErrorMessages.ILLEGAL_MULTIVALUE_ATTRIBUTE_ASSIGNMENT);
    }

  }

  @Test
  public void testExecuteAttributeMassUpdateSVANewValueCorrect() {
    // arrange:
    arrangeIpureleaseEnumAvSingleValueDatabase();
    commit();
    beginTransaction();

    // act and assert:

    // single value attribute and new value:
    InformationSystemRelease instance = informationSystemReleaseService.getInformationSystemsFiltered(null, true).get(0);
    LOGGER.debug("using Ipurelease for test: " + instance.getHierarchicalName());
    AttributeType attributeType = attributeTypeDAO.getAttributeTypeByName(ENUM_AT_NAME_A);

    String[] selectedAttributeValueStringIds = new String[0];

    MassUpdateAttributeItem mui = new MassUpdateAttributeItem(instance, attributeType.getId());
    mui.setSelectedAttributeValueStringIds(selectedAttributeValueStringIds);
    MassUpdateLine<InformationSystemRelease> line = new MassUpdateLine<InformationSystemRelease>();
    line.setBuildingBlockToUpdate(instance);
    MassUpdateAttribute attribute = new MassUpdateAttribute(0);
    attribute.setMassUpdateAttributeItem(mui);
    attribute.setType(BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE);
    line.addAttribute("id", attribute);
    massUpdateService.updateAttributes(line);
    commit();

    beginTransaction();
    instance = informationSystemReleaseService.getInformationSystemsFiltered(null, true).get(0);
    assertEquals(INCORRECT_AVA_NR_ERROR_MSG, 0, instance.getAttributeValueAssignments().size());
    commit();
  }

  @Test
  public void testExecuteAttributeMassUpdateSVANoNewValueCorrect() {
    // arrange:
    arrangeIpureleaseEnumAvSingleValueDatabase();
    commit();
    beginTransaction();

    // act and assert:

    // single value attribute and no new value:
    InformationSystemRelease instance = informationSystemReleaseService.getInformationSystemsFiltered(null, true).get(0);
    LOGGER.debug("using Ipurelease for test: " + instance.getHierarchicalName());
    AttributeType attributeType = attributeTypeDAO.getAttributeTypeByName(ENUM_AT_NAME_A);
    String newAttributeValue = null;
    Integer selectedEnumAvId = Integer.valueOf(2);
    String[] selectedAttributeValueStringIds = new String[] { selectedEnumAvId.toString() };

    MassUpdateAttributeItem mui = new MassUpdateAttributeItem(instance, attributeType.getId());
    mui.setNewAttributeValue(newAttributeValue);
    // mui.setNewAttributeValueSelected(true);
    mui.setSelectedAttributeValueStringIds(selectedAttributeValueStringIds);
    MassUpdateLine<InformationSystemRelease> line = new MassUpdateLine<InformationSystemRelease>();
    line.setBuildingBlockToUpdate(instance);
    MassUpdateAttribute attribute = new MassUpdateAttribute(0);
    attribute.setMassUpdateAttributeItem(mui);
    attribute.setType(BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE);
    line.addAttribute("id", attribute);
    massUpdateService.updateAttributes(line);
    commit();

    beginTransaction();
    instance = informationSystemReleaseService.getInformationSystemsFiltered(null, true).get(0);
    List<AttributeValueAssignment> avas = new ArrayList<AttributeValueAssignment>(instance.getAttributeValueAssignments());
    assertEquals(INCORRECT_AVA_NR_ERROR_MSG, 1, avas.size());
    assertEquals("AttributeValue has an incorrect value!", selectedEnumAvId, avas.get(0).getAttributeValue().getId());
    commit();
  }

  private void arrangeIpureleaseEnumAvMultiValueDatabase() {

    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);

    AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    EnumAT bEnum = testDataHelper
        .createEnumAttributeType("EnumAT B", "Description Attribute B\r\nCR and LF\r\nMultivalue", Boolean.TRUE, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(bEnum);
    EnumAV avB1 = testDataHelper.createEnumAV("AV B 1", "AV B 1", bEnum);
    EnumAV avB2 = testDataHelper.createEnumAV("AV B 2", "AV B 2", bEnum);
    testDataHelper.createEnumAV("AV B 3", "AV B 3", bEnum);
    testDataHelper.createEnumAV("AV B 4", "AV B 4", bEnum);
    testDataHelper.createAVA(i1, avB1);
    testDataHelper.createAVA(i1, avB2);
  }

  private void arrangeIpureleaseEnumAvSingleValueDatabase() {

    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);

    AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    EnumAT aEnum = testDataHelper.createEnumAttributeType(ENUM_AT_NAME_A, "Description Attribute A", Boolean.FALSE, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(aEnum);
    EnumAV avA1 = testDataHelper.createEnumAV("AV A 1", "AV A 1", aEnum);
    testDataHelper.createEnumAV("AV A 2", "AV A 2", aEnum);
    testDataHelper.createAVA(i1, avA1);

    commit();
  }

  /**
   * Create two different ipureleases, persist them, run a massupdate over the ipureleases and check
   * whether the data has changed on the db
   */
  @Test
  public void testUpdateProperties() {
    InformationSystem is1 = testDataHelper.createInformationSystem("IS");
    InformationSystem is2 = testDataHelper.createInformationSystem("IS2");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is1, "v1.1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is2, "v2.2", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);

    Integer id1 = isr1.getId();
    Integer id2 = isr2.getId();

    commit();
    beginTransaction();

    List<String> properties = new ArrayList<String>();
    properties.add(InformationSystemReleaseTypeMu.PROPERTY_NAME.replace(".", "_"));
    properties.add(InformationSystemReleaseTypeMu.PROPERTY_DESCRIPTION.replace(".", "_"));
    List<String> associations = new ArrayList<String>();

    // line 1
    MassUpdateLine<InformationSystemRelease> line1 = new MassUpdateLine<InformationSystemRelease>();
    line1.setBuildingBlockToUpdate(isr1);
    MassUpdateComponentModel<InformationSystemRelease> componentModel1 = new InformationSystemReleaseCmMu();
    componentModel1.initializeFrom(isr1, properties, associations);
    line1.setComponentModel(componentModel1);

    // line 2
    MassUpdateLine<InformationSystemRelease> line2 = new MassUpdateLine<InformationSystemRelease>();
    line2.setBuildingBlockToUpdate(isr2);
    MassUpdateComponentModel<InformationSystemRelease> componentModel2 = new InformationSystemReleaseCmMu();
    componentModel2.initializeFrom(isr2, properties, associations);
    line2.setComponentModel(componentModel2);

    // set the new data that would come from the gui
    InformationSystemReleaseCmMu model = (InformationSystemReleaseCmMu) line1.getComponentModel();
    InformationSystemReleaseNameComponentModel nameComponentModel = (InformationSystemReleaseNameComponentModel) model.getNameComponentModel();
    StringComponentModel<InformationSystemRelease> descriptionComponentModel = (StringComponentModel<InformationSystemRelease>) model
        .getDescriptionComponentModel();

    nameComponentModel.getElementName().setCurrent("NewName");
    nameComponentModel.getReleaseName().setCurrent("NewVersion");
    descriptionComponentModel.setCurrent("NewDescription");

    model = (InformationSystemReleaseCmMu) line2.getComponentModel();
    nameComponentModel = (InformationSystemReleaseNameComponentModel) model.getNameComponentModel();
    descriptionComponentModel = (StringComponentModel<InformationSystemRelease>) model.getDescriptionComponentModel();

    nameComponentModel.getElementName().setCurrent("NewName2");
    nameComponentModel.getReleaseName().setCurrent("NewVersion2");
    descriptionComponentModel.setCurrent("NewDescription2");

    line1.getComponentModel().update();
    line2.getComponentModel().update();

    // run the update
    massUpdateService.updateLine(line1);
    massUpdateService.updateLine(line2);

    commit();
    beginTransaction();

    // check if the ipurleases are correctly updated on the database
    InformationSystemRelease release = informationSystemReleaseService.loadObjectById(id1);
    assertEquals("NewName", release.getInformationSystem().getName());
    assertEquals("NewVersion", release.getVersion());
    assertEquals("NewDescription", release.getDescription());

    release = informationSystemReleaseService.loadObjectById(id2);
    assertEquals("NewName2", release.getInformationSystem().getName());
    assertEquals("NewVersion2", release.getVersion());
    assertEquals("NewDescription2", release.getDescription());
  }

  /**
   * Creates an {@link InformationSystemRelease} and an {@link InformationSystemDomain} and saves
   * both. Start a new transaction and set the association between the two using mass update
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateAssociations() {
    InformationSystem ipu = testDataHelper.createInformationSystem("IPU");
    InformationSystemRelease ipurelease1 = testDataHelper.createInformationSystemRelease(ipu, "v1.1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);

    Integer id1 = ipurelease1.getId();

    InformationSystemDomain isd = testDataHelper.createInformationSystemDomain("DomainName", "DomainDesc");

    Integer domainId = isd.getId();

    commit();
    beginTransaction();

    List<String> properties = new ArrayList<String>();
    List<String> associations = new ArrayList<String>();
    associations.add(InformationSystemReleaseTypeMu.ASSOCIATION_INFORMATIONSYSTEMDOMAINS);

    // line 1
    MassUpdateLine<InformationSystemRelease> line1 = new MassUpdateLine<InformationSystemRelease>();
    line1.setBuildingBlockToUpdate(ipurelease1);
    MassUpdateComponentModel<InformationSystemRelease> componentModel1 = new InformationSystemReleaseCmMu();
    componentModel1.initializeFrom(ipurelease1, properties, associations);
    line1.setComponentModel(componentModel1);

    InformationSystemReleaseCmMu model = (InformationSystemReleaseCmMu) line1.getComponentModel();
    ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemDomain> domains = (ManyAssociationSetComponentModel<InformationSystemRelease, InformationSystemDomain>) model
        .getInformationSystemDomainComponentModel();
    domains.setElementIdToAdd(domainId);

    model.update();

    massUpdateService.updateLine(line1);

    commit();

    beginTransaction();
    InformationSystemRelease release = informationSystemReleaseService.loadObjectById(id1);
    assertEquals(1, release.getInformationSystemDomains().size());
    assertEquals(domainId, release.getInformationSystemDomains().iterator().next().getId());
  }

  /**
   * creates several {@link InformationSystemRelease} and deletes them with {@code testDeleteInformationSystems}
   */
  @Test
  public void testDeleteInformationSystems() {
    InformationSystem is1 = testDataHelper.createInformationSystem("IS1");
    InformationSystem is2 = testDataHelper.createInformationSystem("IS2");
    InformationSystemRelease isr11 = testDataHelper.createInformationSystemRelease(is1, "1.0");
    InformationSystemRelease isr12 = testDataHelper.createInformationSystemRelease(is1, "2.0");
    InformationSystemRelease isr21 = testDataHelper.createInformationSystemRelease(is2, "1.0");
    isr21.setParent(isr12);

    TechnicalComponent tc = testDataHelper.createTechnicalComponent("TC", true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, "1.0", true);

    InformationSystemInterface isi1 = testDataHelper.createInformationSystemInterface(isr11, isr21, tcr, "ISI1 description");
    InformationSystemInterface isi2 = testDataHelper.createInformationSystemInterface(isr12, isr21, tcr, "ISI2 description");

    commit();
    beginTransaction();

    // deletion of an ISR should delete all interfaces which relate to it, too
    massUpdateService.deleteBuildingBlock(isr11);
    assertNull(informationSystemReleaseService.loadObjectByIdIfExists(isr11.getId()));
    assertNull(informationSystemInterfaceService.loadObjectByIdIfExists(isi1.getId()));

    massUpdateService.deleteBuildingBlock(isi2);
    assertNull(informationSystemInterfaceService.loadObjectByIdIfExists(isi2.getId()));
    commit();
    beginTransaction();

    // deletion of an ISR should delete its children, too
    // also deletion of the only release of an IS should delete this IS, too
    massUpdateService.deleteBuildingBlock(isr12);
    assertNull(informationSystemReleaseService.loadObjectByIdIfExists(isr12.getId()));
    assertNull(informationSystemReleaseService.loadObjectByIdIfExists(isr21.getId()));
    assertNull(informationSystemService.loadObjectByIdIfExists(is1.getId()));
    assertNull(informationSystemService.loadObjectByIdIfExists(is2.getId()));

    // deletion of only release of a TC should delete this TC, too
    massUpdateService.deleteBuildingBlock(tcr);
    assertNull(technicalComponentReleaseService.loadObjectByIdIfExists(tcr.getId()));
    assertNull(technicalComponentService.loadObjectByIdIfExists(tc.getId()));

    // should do nothing, since isr21 was deleted before as child of isr12
    massUpdateService.deleteBuildingBlock(isr21);

    commit();
  }

  /**
   * creates {@link ArchitecturalDomain} and deletes it with {@code testDeleteArchitecturalDomain}
   */
  @Test
  public void testDeleteArchitecturalDomain() {
    ArchitecturalDomain ad = testDataHelper.createArchitecturalDomain("AD", "AD description");

    massUpdateService.deleteBuildingBlock(ad);
    assertNull(architecturalDomainService.loadObjectByIdIfExists(ad.getId()));

    commit();
  }

  /**
   * creates {@link BusinessDomain} and deletes it with {@code testDeleteBusinessDomain}
   */
  @Test
  public void testDeleteBusinessDomain() {
    BusinessDomain bd = testDataHelper.createBusinessDomain("BD", "BD description");

    massUpdateService.deleteBuildingBlock(bd);
    assertNull(businessDomainService.loadObjectByIdIfExists(bd.getId()));

    commit();
  }

  /**
   * creates {@link BusinessFunction} and deletes it with {@code testDeleteBusinessFunction}
   */
  @Test
  public void testDeleteBusinessFunction() {
    BusinessFunction bf = testDataHelper.createBusinessFunction("BF", "BF description");

    massUpdateService.deleteBuildingBlock(bf);
    assertNull(businessFunctionService.loadObjectByIdIfExists(bf.getId()));

    commit();
  }

  /**
   * creates {@link BusinessObject} and deletes it with {@code testDeleteBusinessObject}
   */
  @Test
  public void testDeleteBusinessObject() {
    BusinessObject bo = testDataHelper.createBusinessObject("BO", "BO description");

    massUpdateService.deleteBuildingBlock(bo);
    assertNull(businessObjectService.loadObjectByIdIfExists(bo.getId()));

    commit();
  }

  /**
   * creates {@link BusinessProcess} and deletes it with {@code testDeleteBusinessProcess}
   */
  @Test
  public void testDeleteBusinessProcess() {
    BusinessProcess bp = testDataHelper.createBusinessProcess("BP", "BP description");
    bp.addParent(businessProcessService.getFirstElement());

    massUpdateService.deleteBuildingBlock(bp);
    assertNull(businessProcessService.loadObjectByIdIfExists(bp.getId()));

    commit();
  }

  /**
   * creates {@link BusinessUnit} and deletes it with {@code testDeleteBusinessUnit}
   */
  @Test
  public void testDeleteBusinessUnit() {
    BusinessUnit bu = testDataHelper.createBusinessUnit("BU", "BU description");
    bu.addParent(businessUnitService.getFirstElement());

    massUpdateService.deleteBuildingBlock(bu);
    assertNull(businessUnitService.loadObjectByIdIfExists(bu.getId()));

    commit();
  }

  /**
   * creates {@link InfrastructureElement} and deletes it with {@code testDeleteInfrastructureElement}
   */
  @Test
  public void testDeleteInfrastructureElement() {
    InfrastructureElement ie = testDataHelper.createInfrastructureElement("IE", "IE description");

    massUpdateService.deleteBuildingBlock(ie);
    assertNull(infrastructureElementService.loadObjectByIdIfExists(ie.getId()));

    commit();
  }

  /**
   * creates {@link InformationSystemDomain} and deletes it with {@code testDeleteInformationSystemDomain}
   */
  @Test
  public void testDeleteInformationSystemDomain() {
    InformationSystemDomain isd = testDataHelper.createInformationSystemDomain("ISD", "ISD description");

    massUpdateService.deleteBuildingBlock(isd);
    assertNull(informationSystemDomainService.loadObjectByIdIfExists(isd.getId()));

    commit();
  }

  /**
   * creates {@link Product} and deletes it with {@code testDeleteProduct}
   */
  @Test
  public void testDeleteProduct() {
    Product product = testDataHelper.createProduct("Prod", "Product description");
    product.addParent(productService.getFirstElement());

    massUpdateService.deleteBuildingBlock(product);
    assertNull(productService.loadObjectByIdIfExists(product.getId()));

    commit();
  }

  /**
   * creates {@link Project} and deletes it with {@code testDeleteProject}
   */
  @Test
  public void testDeleteProject() {
    Project project = testDataHelper.createProject("Proj", "Project description");

    massUpdateService.deleteBuildingBlock(project);
    assertNull(projectService.loadObjectByIdIfExists(project.getId()));

    commit();
  }

  /**
   * Creates some ISR with the ISI between them and then tries to delete ISR. There was a bug #2380, where the ISI could not be 
   * deleted, because of inconsistent ISR state.
   */
  @Test
  public void testDeleteISRWithISI() {
    InformationSystem is1 = testDataHelper.createInformationSystem("IS1");
    InformationSystem is2 = testDataHelper.createInformationSystem("IS2");
    InformationSystemRelease isr11 = testDataHelper.createInformationSystemRelease(is1, "1.0");
    InformationSystemRelease isr12 = testDataHelper.createInformationSystemRelease(is1, "2.0");
    InformationSystemRelease isr21 = testDataHelper.createInformationSystemRelease(is2, "1.0");
    isr21.setParent(isr12);

    TechnicalComponent tc = testDataHelper.createTechnicalComponent("TC", true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, "1.0", true);

    InformationSystemInterface isi1 = testDataHelper.createInformationSystemInterface(isr11, isr21, tcr, "ISI1 description");
    InformationSystemInterface isi2 = testDataHelper.createInformationSystemInterface(isr12, isr21, tcr, "ISI2 description");

    commit();
    beginTransaction();

    massUpdateService.deleteBuildingBlock(isr11);
    massUpdateService.deleteBuildingBlock(isr12);
    massUpdateService.deleteBuildingBlock(isr21);

    commit();
    assertNull(informationSystemReleaseService.loadObjectByIdIfExists(isr11.getId()));
    assertNull(informationSystemReleaseService.loadObjectByIdIfExists(isr12.getId()));
    assertNull(informationSystemReleaseService.loadObjectByIdIfExists(isr21.getId()));
    assertNull(informationSystemInterfaceService.loadObjectByIdIfExists(isi1.getId()));
    assertNull(informationSystemInterfaceService.loadObjectByIdIfExists(isi2.getId()));
  }

}
