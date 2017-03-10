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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;


/**
 * Tests the service methods of the {@link Isr2BoAssociationService} interface.
 * 
 * @author sip
 */
public class Isr2BoAssociationServiceTest extends BaseTransactionalTestSupport {

  private static final String             TEST_IS_NAME            = "testIS";
  private static final String             VERSION_1_0             = "1.0";
  private static final String             TEST_BO_NAME            = "testBO";
  private static final String             TEST_ATG_NAME           = "testATG";
  private static final String             TEST_ENUMAT_NAME        = "testEnumAt";
  private static final String             TEST_NUMBERAT_NAME      = "testNumberAt";
  private static final String             TES_DESCRIPTION         = "an entity for testing";
  private static final String             TES_DESCRIPTION_UPDATED = "updated entity";

  @Autowired
  private Isr2BoAssociationService        isr2BoAssociationService;
  @Autowired
  private InformationSystemReleaseService informationSystemReleaseService;
  @Autowired
  private BusinessObjectService           businessObjectService;
  @Autowired
  private AttributeTypeService            attributeTypeService;
  @Autowired
  private TestDataHelper2                 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#deleteEntity(InformationSystemRelease)}
   */
  @Test
  public void testDeleteIsrWithAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);
    Isr2BoAssociation assoc = testDataHelper.addBusinessObjectToInformationSystem(isr, bo);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    informationSystemReleaseService.deleteEntity(isr);

    commit();
    beginTransaction();

    assertNull(informationSystemReleaseService.loadObjectByIdIfExists(isr.getId()));
    assertNull(isr2BoAssociationService.loadObjectByIdIfExists(assoc.getId()));
    assertEquals(bo, businessObjectService.loadObjectByIdIfExists(bo.getId()));

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectService#deleteEntity(BusinessObject)}
   */
  @Test
  public void testDeleteBoWithAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);
    Isr2BoAssociation assoc = testDataHelper.addBusinessObjectToInformationSystem(isr, bo);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    businessObjectService.deleteEntity(bo);

    commit();
    beginTransaction();

    assertEquals(isr, informationSystemReleaseService.loadObjectByIdIfExists(isr.getId()));
    assertNull(isr2BoAssociationService.loadObjectByIdIfExists(assoc.getId()));
    assertNull(businessObjectService.loadObjectByIdIfExists(bo.getId()));

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#saveOrUpdate(InformationSystemRelease)}
   */
  @Test
  public void testCreateIsrWithAssociation() {
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    InformationSystem is = BuildingBlockFactory.createInformationSystem();
    is.setName(TEST_IS_NAME);

    InformationSystemRelease isr = BuildingBlockFactory.createInformationSystemRelease();
    isr.setInformationSystem(is);
    isr.setVersion(VERSION_1_0);

    Isr2BoAssociation assoc = BuildingBlockFactory.createIsr2BoAssociation(isr, bo);
    assoc.connect();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    informationSystemReleaseService.saveOrUpdate(isr);

    commit();
    beginTransaction();

    InformationSystemRelease reloadedIsr = informationSystemReleaseService.loadObjectById(isr.getId());
    Set<Isr2BoAssociation> associations = reloadedIsr.getBusinessObjectAssociations();
    assertEquals(1, associations.size());
    Isr2BoAssociation association = associations.iterator().next();
    assertEquals(reloadedIsr, association.getInformationSystemRelease());
    assertEquals(bo, association.getBusinessObject());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectService#saveOrUpdate(BusinessObject)}
   */
  @Test
  public void testCreateBoWithAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    BusinessObject bo = BuildingBlockFactory.createBusinessObject();
    bo.setName(TEST_BO_NAME);

    Isr2BoAssociation assoc = BuildingBlockFactory.createIsr2BoAssociation(isr, bo);
    assoc.connect();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    businessObjectService.saveOrUpdate(bo);

    commit();
    beginTransaction();

    BusinessObject reloadedBo = businessObjectService.loadObjectById(bo.getId());
    Set<Isr2BoAssociation> associations = reloadedBo.getInformationSystemReleaseAssociations();
    assertEquals(1, associations.size());
    Isr2BoAssociation association = associations.iterator().next();
    assertEquals(isr, association.getInformationSystemRelease());
    assertEquals(reloadedBo, association.getBusinessObject());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#saveOrUpdate(InformationSystemRelease)}
   */
  @Test
  public void testUpdateIsrAddAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    InformationSystemRelease updateIsr = informationSystemReleaseService.loadObjectById(isr.getId());
    updateIsr.setDescription(TES_DESCRIPTION_UPDATED);

    Isr2BoAssociation assoc = BuildingBlockFactory.createIsr2BoAssociation(updateIsr, bo);
    assoc.connect();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    informationSystemReleaseService.saveOrUpdate(updateIsr);

    commit();
    beginTransaction();

    InformationSystemRelease reloadedIsr = informationSystemReleaseService.loadObjectById(updateIsr.getId());
    Set<Isr2BoAssociation> associations = reloadedIsr.getBusinessObjectAssociations();
    assertEquals(1, associations.size());
    Isr2BoAssociation association = associations.iterator().next();
    assertEquals(reloadedIsr, association.getInformationSystemRelease());
    assertEquals(bo, association.getBusinessObject());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectService#saveOrUpdate(BusinessObject)}
   */
  @Test
  public void testUpdateBoAddAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    BusinessObject updateBo = businessObjectService.loadObjectById(bo.getId());
    updateBo.setDescription(TES_DESCRIPTION_UPDATED);

    Isr2BoAssociation assoc = BuildingBlockFactory.createIsr2BoAssociation(isr, updateBo);
    assoc.connect();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    businessObjectService.saveOrUpdate(updateBo);

    commit();
    beginTransaction();

    BusinessObject reloadedBo = businessObjectService.loadObjectById(updateBo.getId());
    Set<Isr2BoAssociation> associations = reloadedBo.getInformationSystemReleaseAssociations();
    assertEquals(1, associations.size());
    Isr2BoAssociation association = associations.iterator().next();
    assertEquals(isr, association.getInformationSystemRelease());
    assertEquals(reloadedBo, association.getBusinessObject());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#saveOrUpdate(InformationSystemRelease)}
   */
  @Test
  public void testUpdateIsrRemoveAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);
    Isr2BoAssociation assoc = testDataHelper.addBusinessObjectToInformationSystem(isr, bo);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    InformationSystemRelease updateIsr = informationSystemReleaseService.loadObjectById(isr.getId());
    updateIsr.setDescription(TES_DESCRIPTION_UPDATED);
    updateIsr.getBusinessObjectAssociations().clear();
    informationSystemReleaseService.saveOrUpdate(updateIsr);

    commit();
    beginTransaction();

    assertEquals(updateIsr, informationSystemReleaseService.loadObjectByIdIfExists(updateIsr.getId()));
    assertNull(isr2BoAssociationService.loadObjectByIdIfExists(assoc.getId()));
    assertEquals(bo, businessObjectService.loadObjectByIdIfExists(bo.getId()));

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectService#saveOrUpdate(BusinessObject)}
   */
  @Test
  public void testUpdateBoRemoveAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);
    Isr2BoAssociation assoc = testDataHelper.addBusinessObjectToInformationSystem(isr, bo);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    BusinessObject updateBo = businessObjectService.loadObjectById(bo.getId());
    updateBo.setDescription(TES_DESCRIPTION_UPDATED);
    updateBo.getInformationSystemReleaseAssociations().clear();
    businessObjectService.saveOrUpdate(updateBo);

    commit();
    beginTransaction();

    assertEquals(isr, informationSystemReleaseService.loadObjectByIdIfExists(isr.getId()));
    assertNull(isr2BoAssociationService.loadObjectByIdIfExists(assoc.getId()));
    assertEquals(updateBo, businessObjectService.loadObjectByIdIfExists(updateBo.getId()));

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#saveOrUpdate(InformationSystemRelease)}
   */
  @Test
  public void testUpdateIsrAddAvaToAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);
    testDataHelper.addBusinessObjectToInformationSystem(isr, bo);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    InformationSystemRelease updateIsr = informationSystemReleaseService.loadObjectById(isr.getId());
    updateIsr.setDescription(TES_DESCRIPTION_UPDATED);
    Set<Isr2BoAssociation> businessObjectAssociations = updateIsr.getBusinessObjectAssociations();
    assertEquals(1, businessObjectAssociations.size());
    Isr2BoAssociation assoc = businessObjectAssociations.iterator().next();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    informationSystemReleaseService.saveOrUpdate(updateIsr);

    commit();
    beginTransaction();

    InformationSystemRelease reloadedIsr = informationSystemReleaseService.loadObjectById(updateIsr.getId());
    Set<Isr2BoAssociation> associations = reloadedIsr.getBusinessObjectAssociations();
    assertEquals(1, associations.size());
    Isr2BoAssociation association = associations.iterator().next();
    assertEquals(reloadedIsr, association.getInformationSystemRelease());
    assertEquals(bo, association.getBusinessObject());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectService#saveOrUpdate(BusinessObject)}
   */
  @Test
  public void testUpdateBoAddAvaToAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);
    testDataHelper.addBusinessObjectToInformationSystem(isr, bo);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    BusinessObject updateBo = businessObjectService.loadObjectById(bo.getId());
    updateBo.setDescription(TES_DESCRIPTION_UPDATED);
    Set<Isr2BoAssociation> informationSystemReleaseAssociations = updateBo.getInformationSystemReleaseAssociations();
    assertEquals(1, informationSystemReleaseAssociations.size());
    Isr2BoAssociation assoc = informationSystemReleaseAssociations.iterator().next();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    businessObjectService.saveOrUpdate(updateBo);

    commit();
    beginTransaction();

    BusinessObject reloadedBo = businessObjectService.loadObjectById(updateBo.getId());
    Set<Isr2BoAssociation> associations = reloadedBo.getInformationSystemReleaseAssociations();
    assertEquals(1, associations.size());
    Isr2BoAssociation association = associations.iterator().next();
    assertEquals(isr, association.getInformationSystemRelease());
    assertEquals(reloadedBo, association.getBusinessObject());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#saveOrUpdate(InformationSystemRelease)}
   */
  @Test
  public void testUpdateIsrRemoveAvaFromAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);
    Isr2BoAssociation assoc = testDataHelper.addBusinessObjectToInformationSystem(isr, bo);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    InformationSystemRelease updateIsr = informationSystemReleaseService.loadObjectById(isr.getId());
    updateIsr.setDescription(TES_DESCRIPTION_UPDATED);
    for (Isr2BoAssociation updateAssoc : updateIsr.getBusinessObjectAssociations()) {
      updateAssoc.getAttributeValueAssignments().clear();
    }
    informationSystemReleaseService.saveOrUpdate(updateIsr);

    commit();
    beginTransaction();

    InformationSystemRelease reloadIsr = informationSystemReleaseService.loadObjectByIdIfExists(updateIsr.getId());
    assertEquals(updateIsr, reloadIsr);
    assertEquals(1, reloadIsr.getBusinessObjectAssociations().size());
    assertEquals(bo, businessObjectService.loadObjectByIdIfExists(bo.getId()));

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessObjectService#saveOrUpdate(BusinessObject)}
   */
  @Test
  public void testUpdateBoRemoveAvaFromAssociation() {
    InformationSystem is = testDataHelper.createInformationSystem(TEST_IS_NAME);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, VERSION_1_0);
    BusinessObject bo = testDataHelper.createBusinessObject(TEST_BO_NAME, TES_DESCRIPTION);
    Isr2BoAssociation assoc = testDataHelper.addBusinessObjectToInformationSystem(isr, bo);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    BusinessObject updateBo = businessObjectService.loadObjectById(bo.getId());
    updateBo.setDescription(TES_DESCRIPTION_UPDATED);
    for (Isr2BoAssociation updateAssoc : updateBo.getInformationSystemReleaseAssociations()) {
      updateAssoc.getAttributeValueAssignments().clear();
    }
    businessObjectService.saveOrUpdate(updateBo);

    commit();
    beginTransaction();

    BusinessObject reloadBo = businessObjectService.loadObjectByIdIfExists(updateBo.getId());
    assertEquals(isr, informationSystemReleaseService.loadObjectByIdIfExists(isr.getId()));
    assertEquals(1, reloadBo.getInformationSystemReleaseAssociations().size());
    assertEquals(updateBo, reloadBo);

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }
}
