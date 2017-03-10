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
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;


/**
 * Tests the service methods of the {@link Tcr2IeAssociationService} interface.
 * 
 * @author sip
 */
public class Tcr2IeAssociationServiceTest extends BaseTransactionalTestSupport {

  @Autowired
  private Tcr2IeAssociationService         tcr2IeAssociationService;
  @Autowired
  private TechnicalComponentReleaseService technicalComponentReleaseService;
  @Autowired
  private InfrastructureElementService     infrastructureElementService;
  @Autowired
  private AttributeTypeService             attributeTypeService;
  @Autowired
  private TestDataHelper2                  testDataHelper;

  private static final String              TEST_IS_NAME            = "testIS";
  private static final String              VERSION_1_0             = "1.0";
  private static final String              TEST_BO_NAME            = "testBO";
  private static final String              TEST_ATG_NAME           = "testATG";
  private static final String              TEST_ENUMAT_NAME        = "testEnumAt";
  private static final String              TEST_NUMBERAT_NAME      = "testNumberAt";
  private static final String              TES_DESCRIPTION         = "an entity for testing";
  private static final String              TES_DESCRIPTION_UPDATED = "updated entity";

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService#deleteEntity(TechnicalComponentRelease)}
   */
  @Test
  public void testDeleteTcrWithAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);
    Tcr2IeAssociation assoc = testDataHelper.addTcrToIe(ie, tcr);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    technicalComponentReleaseService.deleteEntity(tcr);

    commit();
    beginTransaction();

    assertNull(technicalComponentReleaseService.loadObjectByIdIfExists(tcr.getId()));
    assertNull(tcr2IeAssociationService.loadObjectByIdIfExists(assoc.getId()));
    assertEquals(ie, infrastructureElementService.loadObjectByIdIfExists(ie.getId()));

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService#deleteEntity(InfrastructureElement)}
   */
  @Test
  public void testDeleteIeWithAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);
    Tcr2IeAssociation assoc = testDataHelper.addTcrToIe(ie, tcr);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    infrastructureElementService.deleteEntity(ie);

    commit();
    beginTransaction();

    assertEquals(tcr, technicalComponentReleaseService.loadObjectByIdIfExists(tcr.getId()));
    assertNull(tcr2IeAssociationService.loadObjectByIdIfExists(assoc.getId()));
    assertNull(infrastructureElementService.loadObjectByIdIfExists(ie.getId()));

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService#saveOrUpdate(TechnicalComponentRelease)}
   */
  @Test
  public void testCreateTcrWithAssociation() {
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    TechnicalComponent tc = BuildingBlockFactory.createTechnicalComponent();
    tc.setName(TEST_IS_NAME);

    TechnicalComponentRelease tcr = BuildingBlockFactory.createTechnicalComponentRelease();
    tcr.setTechnicalComponent(tc);
    tcr.setVersion(VERSION_1_0);

    Tcr2IeAssociation assoc = BuildingBlockFactory.createTcr2IeAssociation(tcr, ie);
    assoc.connect();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    technicalComponentReleaseService.saveOrUpdate(tcr);

    commit();
    beginTransaction();

    TechnicalComponentRelease reloadedIsr = technicalComponentReleaseService.loadObjectById(tcr.getId());
    Set<Tcr2IeAssociation> associations = reloadedIsr.getInfrastructureElementAssociations();
    assertEquals(1, associations.size());
    Tcr2IeAssociation association = associations.iterator().next();
    assertEquals(reloadedIsr, association.getTechnicalComponentRelease());
    assertEquals(ie, association.getInfrastructureElement());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService#saveOrUpdate(InfrastructureElement)}
   */
  @Test
  public void testCreateIeWithAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    InfrastructureElement ie = BuildingBlockFactory.createInfrastructureElement();
    ie.setName(TEST_BO_NAME);

    Tcr2IeAssociation assoc = BuildingBlockFactory.createTcr2IeAssociation(tcr, ie);
    assoc.connect();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    infrastructureElementService.saveOrUpdate(ie);

    commit();
    beginTransaction();

    InfrastructureElement reloadedBo = infrastructureElementService.loadObjectById(ie.getId());
    Set<Tcr2IeAssociation> associations = reloadedBo.getTechnicalComponentReleaseAssociations();
    assertEquals(1, associations.size());
    Tcr2IeAssociation association = associations.iterator().next();
    assertEquals(tcr, association.getTechnicalComponentRelease());
    assertEquals(reloadedBo, association.getInfrastructureElement());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService#saveOrUpdate(TechnicalComponentRelease)}
   */
  @Test
  public void testUpdateTcrAddAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    TechnicalComponentRelease updateTcr = technicalComponentReleaseService.loadObjectById(tcr.getId());
    updateTcr.setDescription(TES_DESCRIPTION_UPDATED);

    Tcr2IeAssociation assoc = BuildingBlockFactory.createTcr2IeAssociation(updateTcr, ie);
    assoc.connect();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    technicalComponentReleaseService.saveOrUpdate(updateTcr);

    commit();
    beginTransaction();

    TechnicalComponentRelease reloadedIsr = technicalComponentReleaseService.loadObjectById(updateTcr.getId());
    Set<Tcr2IeAssociation> associations = reloadedIsr.getInfrastructureElementAssociations();
    assertEquals(1, associations.size());
    Tcr2IeAssociation association = associations.iterator().next();
    assertEquals(reloadedIsr, association.getTechnicalComponentRelease());
    assertEquals(ie, association.getInfrastructureElement());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService#saveOrUpdate(InfrastructureElement)}
   */
  @Test
  public void testUpdateIeAddAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    InfrastructureElement updateIe = infrastructureElementService.loadObjectById(ie.getId());
    updateIe.setDescription(TES_DESCRIPTION_UPDATED);

    Tcr2IeAssociation assoc = BuildingBlockFactory.createTcr2IeAssociation(tcr, updateIe);
    assoc.connect();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    infrastructureElementService.saveOrUpdate(updateIe);

    commit();
    beginTransaction();

    InfrastructureElement reloadedBo = infrastructureElementService.loadObjectById(updateIe.getId());
    Set<Tcr2IeAssociation> associations = reloadedBo.getTechnicalComponentReleaseAssociations();
    assertEquals(1, associations.size());
    Tcr2IeAssociation association = associations.iterator().next();
    assertEquals(tcr, association.getTechnicalComponentRelease());
    assertEquals(reloadedBo, association.getInfrastructureElement());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService#saveOrUpdate(TechnicalComponentRelease)}
   */
  @Test
  public void testUpdateTcrRemoveAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);
    Tcr2IeAssociation assoc = testDataHelper.addTcrToIe(ie, tcr);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    TechnicalComponentRelease updateTcr = technicalComponentReleaseService.loadObjectById(tcr.getId());
    updateTcr.setDescription(TES_DESCRIPTION_UPDATED);
    updateTcr.getInfrastructureElementAssociations().clear();
    technicalComponentReleaseService.saveOrUpdate(updateTcr);

    commit();
    beginTransaction();

    assertEquals(updateTcr, technicalComponentReleaseService.loadObjectByIdIfExists(updateTcr.getId()));
    assertNull(tcr2IeAssociationService.loadObjectByIdIfExists(assoc.getId()));
    assertEquals(ie, infrastructureElementService.loadObjectByIdIfExists(ie.getId()));

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService#saveOrUpdate(InfrastructureElement)}
   */
  @Test
  public void testUpdateIeRemoveAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);
    Tcr2IeAssociation assoc = testDataHelper.addTcrToIe(ie, tcr);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    InfrastructureElement updateIe = infrastructureElementService.loadObjectById(ie.getId());
    updateIe.setDescription(TES_DESCRIPTION_UPDATED);
    updateIe.getTechnicalComponentReleaseAssociations().clear();
    infrastructureElementService.saveOrUpdate(updateIe);

    commit();
    beginTransaction();

    assertEquals(tcr, technicalComponentReleaseService.loadObjectByIdIfExists(tcr.getId()));
    assertNull(tcr2IeAssociationService.loadObjectByIdIfExists(assoc.getId()));
    assertEquals(updateIe, infrastructureElementService.loadObjectByIdIfExists(updateIe.getId()));

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService#saveOrUpdate(TechnicalComponentRelease)}
   */
  @Test
  public void testUpdateTcrAddAvaToAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);
    testDataHelper.addTcrToIe(ie, tcr);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    TechnicalComponentRelease updateTcr = technicalComponentReleaseService.loadObjectById(tcr.getId());
    updateTcr.setDescription(TES_DESCRIPTION_UPDATED);
    Set<Tcr2IeAssociation> infrastructureElementAssociations = updateTcr.getInfrastructureElementAssociations();
    assertEquals(1, infrastructureElementAssociations.size());
    Tcr2IeAssociation assoc = infrastructureElementAssociations.iterator().next();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    technicalComponentReleaseService.saveOrUpdate(updateTcr);

    commit();
    beginTransaction();

    TechnicalComponentRelease reloadedIsr = technicalComponentReleaseService.loadObjectById(updateTcr.getId());
    Set<Tcr2IeAssociation> associations = reloadedIsr.getInfrastructureElementAssociations();
    assertEquals(1, associations.size());
    Tcr2IeAssociation association = associations.iterator().next();
    assertEquals(reloadedIsr, association.getTechnicalComponentRelease());
    assertEquals(ie, association.getInfrastructureElement());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService#saveOrUpdate(InfrastructureElement)}
   */
  @Test
  public void testUpdateIeAddAvaToAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);
    testDataHelper.addTcrToIe(ie, tcr);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);

    commit();
    beginTransaction();

    InfrastructureElement updateIe = infrastructureElementService.loadObjectById(ie.getId());
    updateIe.setDescription(TES_DESCRIPTION_UPDATED);
    Set<Tcr2IeAssociation> technicalComponentReleaseAssociations = updateIe.getTechnicalComponentReleaseAssociations();
    assertEquals(1, technicalComponentReleaseAssociations.size());
    Tcr2IeAssociation assoc = technicalComponentReleaseAssociations.iterator().next();

    NumberAV numberAV = new NumberAV();
    numberAV.setAttributeTypeTwoWay(numberAT);
    numberAV.setValue(new BigDecimal(123));

    AttributeValueAssignment avaNum = new AttributeValueAssignment();
    avaNum.addReferences(numberAV, assoc);

    AttributeValueAssignment avaEnum = new AttributeValueAssignment();
    avaEnum.addReferences(enumAV, assoc);

    infrastructureElementService.saveOrUpdate(updateIe);

    commit();
    beginTransaction();

    InfrastructureElement reloadedBo = infrastructureElementService.loadObjectById(updateIe.getId());
    Set<Tcr2IeAssociation> associations = reloadedBo.getTechnicalComponentReleaseAssociations();
    assertEquals(1, associations.size());
    Tcr2IeAssociation association = associations.iterator().next();
    assertEquals(tcr, association.getTechnicalComponentRelease());
    assertEquals(reloadedBo, association.getInfrastructureElement());
    assertEquals(Lists.newArrayList(numberAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(numberAT));
    assertEquals(Lists.newArrayList(enumAV), association.getAttributeTypeToAttributeValues().getBucketNotNull(enumAT));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService#saveOrUpdate(TechnicalComponentRelease)}
   */
  @Test
  public void testUpdateTcrRemoveAvaFromAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);
    Tcr2IeAssociation assoc = testDataHelper.addTcrToIe(ie, tcr);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    TechnicalComponentRelease updateTcr = technicalComponentReleaseService.loadObjectById(tcr.getId());
    updateTcr.setDescription(TES_DESCRIPTION_UPDATED);
    for (Tcr2IeAssociation updateAssoc : updateTcr.getInfrastructureElementAssociations()) {
      updateAssoc.getAttributeValueAssignments().clear();
    }
    technicalComponentReleaseService.saveOrUpdate(updateTcr);

    commit();
    beginTransaction();

    TechnicalComponentRelease reloadTcr = technicalComponentReleaseService.loadObjectByIdIfExists(updateTcr.getId());
    assertEquals(updateTcr, reloadTcr);
    assertEquals(1, reloadTcr.getInfrastructureElementAssociations().size());
    assertEquals(ie, infrastructureElementService.loadObjectByIdIfExists(ie.getId()));

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService#saveOrUpdate(InfrastructureElement)}
   */
  @Test
  public void testUpdateIeRemoveAvaFromAssociation() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent(TEST_IS_NAME, true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION_1_0, true);
    InfrastructureElement ie = testDataHelper.createInfrastructureElement(TEST_BO_NAME, TES_DESCRIPTION);
    Tcr2IeAssociation assoc = testDataHelper.addTcrToIe(ie, tcr);

    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup(TEST_ATG_NAME, TES_DESCRIPTION);

    NumberAT numberAT = testDataHelper.createNumberAttributeType(TEST_NUMBERAT_NAME, TES_DESCRIPTION, atg);
    NumberAV numberAV = testDataHelper.createNumberAV(new BigDecimal(123), numberAT);
    testDataHelper.createAVA(assoc, numberAV);

    EnumAT enumAT = testDataHelper.createEnumAttributeType(TEST_ENUMAT_NAME, TES_DESCRIPTION, Boolean.FALSE, atg);
    EnumAV enumAV = testDataHelper.createEnumAV("enumVal", TES_DESCRIPTION, enumAT);
    testDataHelper.createAVA(assoc, enumAV);

    commit();
    beginTransaction();

    InfrastructureElement updateIe = infrastructureElementService.loadObjectById(ie.getId());
    updateIe.setDescription(TES_DESCRIPTION_UPDATED);
    for (Tcr2IeAssociation updateAssoc : updateIe.getTechnicalComponentReleaseAssociations()) {
      updateAssoc.getAttributeValueAssignments().clear();
    }
    infrastructureElementService.saveOrUpdate(updateIe);

    commit();
    beginTransaction();

    InfrastructureElement reloadIe = infrastructureElementService.loadObjectByIdIfExists(updateIe.getId());
    assertEquals(tcr, technicalComponentReleaseService.loadObjectByIdIfExists(tcr.getId()));
    assertEquals(1, reloadIe.getTechnicalComponentReleaseAssociations().size());
    assertEquals(updateIe, reloadIe);

    assertTrue(attributeTypeService.loadObjectById(numberAT.getId()).getAllAttributeValues().isEmpty());
    Collection<? extends AttributeValue> allEnumValues = attributeTypeService.loadObjectById(enumAT.getId()).getAllAttributeValues();
    assertEquals(1, allEnumValues.size());
    assertTrue(allEnumValues.contains(enumAV));
  }
}
