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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.Project;
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
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;


/**
 * Integration test for the {@link AttributeValueServiceImpl} service.
 * 
 * @author agu
 *
 */
public class AttributeValueServiceIntegrationTest extends BaseTransactionalTestSupport {

  private Project               project;
  private TextAT                textAtId;
  private DateAT                dateAtId;
  private NumberAT              numberAtId;
  private Integer               enumAtId;
  private Integer               enumMultiAtId;
  private Integer               respAtId;
  private Integer               respMultiAtId;
  @Autowired
  private AttributeValueService attributeValueService;
  @Autowired
  private AttributeTypeGroupDAO attributeTypeGroupDAO;
  @Autowired
  private ProjectService        projectService;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    
    project = testDataHelper.createProject("project", "project description");
    
    User user1 = testDataHelper.createUser("user1", "user1", "user1", "MASTER");
    User user2 = testDataHelper.createUser("user2", "user2", "user2", "MASTER");
    User user3 = testDataHelper.createUser("user3", "user3", "user3", "MASTER");
    
    UserGroup group1 = testDataHelper.createUserGroup("group1", "descr", Sets.newHashSet((UserEntity) user1), Collections.<BuildingBlock> emptySet());
    UserGroup group2 = testDataHelper.createUserGroup("group2", "descr", Sets.newHashSet((UserEntity) user2), Collections.<BuildingBlock> emptySet());
    UserGroup group3 = testDataHelper.createUserGroup("group3", "descr", Sets.newHashSet((UserEntity) user3), Collections.<BuildingBlock> emptySet());
    
    textAtId = createTextAttribute(project, "text1");
    dateAtId = createDateAttribute(project, "date1", new Date());
    numberAtId = createNumberAttribute(project, "date1", BigDecimal.valueOf(20));
    enumAtId = createEnumAttribute(project, "enum").getId();
    enumMultiAtId = createEnumMultiAttribute(project, "enumMulti").getId();
    respAtId = createRespAttribute(project, "resp", Lists.<UserEntity> newArrayList(user1, user2, user3)).getId();
    respMultiAtId = createRespMultiAttribute(project, "respMulti", Lists.<UserEntity> newArrayList(group1, group2, group3)).getId();
    
    commit();
    beginTransaction();
    project = projectService.loadObjectById(project.getId());
  }


  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueServiceImpl#setReferenceValues(de.iteratec.iteraplan.model.BuildingBlock, java.util.Collection, java.lang.Integer)}.
   */
  @Test
  public void testSetReferenceValuesToRemove() {
    assertEquals(Sets.newHashSet("A"), getEnumNames(project.getAssignmentsForId(enumAtId)));
    assertEquals(Sets.newHashSet("A", "E"), getEnumNames(project.getAssignmentsForId(enumMultiAtId)));
    assertEquals(Sets.newHashSet("user1"), getEnumNames(project.getAssignmentsForId(respAtId)));
    assertEquals(Sets.newHashSet("group1", "group2"), getEnumNames(project.getAssignmentsForId(respMultiAtId)));

    attributeValueService.setReferenceValues(project, Collections.<AttributeValue> emptyList(), enumAtId);
    attributeValueService.setReferenceValues(project, Collections.<AttributeValue> emptyList(), enumMultiAtId);
    attributeValueService.setReferenceValues(project, Collections.<AttributeValue> emptyList(), respAtId);
    attributeValueService.setReferenceValues(project, Collections.<AttributeValue> emptyList(), respMultiAtId);

    assertEquals(Sets.newHashSet(), getEnumNames(project.getAssignmentsForId(enumAtId)));
    assertEquals(Sets.newHashSet(), getEnumNames(project.getAssignmentsForId(enumMultiAtId)));
    assertEquals(Sets.newHashSet(), getEnumNames(project.getAssignmentsForId(respAtId)));
    assertEquals(Sets.newHashSet(), getEnumNames(project.getAssignmentsForId(respMultiAtId)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueServiceImpl#setReferenceValues(de.iteratec.iteraplan.model.BuildingBlock, java.util.Collection, java.lang.Integer)}.
   */
  @Test
  public void testSetReferenceValues() {
    assertEquals(Sets.newHashSet("A"), getEnumNames(project.getAssignmentsForId(enumAtId)));
    assertEquals(Sets.newHashSet("A", "E"), getEnumNames(project.getAssignmentsForId(enumMultiAtId)));
    assertEquals(Sets.newHashSet("user1"), getEnumNames(project.getAssignmentsForId(respAtId)));
    assertEquals(Sets.newHashSet("group1", "group2"), getEnumNames(project.getAssignmentsForId(respMultiAtId)));

    Set<AttributeValue> newEnumValues = getEnumValues(Sets.newHashSet("E"), enumAtId);
    Set<AttributeValue> newEnumMultiValues = getEnumValues(Sets.newHashSet("D", "E", "C"), enumMultiAtId);
    Set<AttributeValue> newRespValues = getEnumValues(Sets.newHashSet("user3"), respAtId);
    Set<AttributeValue> newRespMultiValues = getEnumValues(Sets.newHashSet("group2", "group3"), respMultiAtId);

    attributeValueService.setReferenceValues(project, newEnumValues, enumAtId);
    attributeValueService.setReferenceValues(project, newEnumMultiValues, enumMultiAtId);
    attributeValueService.setReferenceValues(project, newRespValues, respAtId);
    attributeValueService.setReferenceValues(project, newRespMultiValues, respMultiAtId);

    assertEquals(Sets.newHashSet("E"), getEnumNames(project.getAssignmentsForId(enumAtId)));
    assertEquals(Sets.newHashSet("C", "D", "E"), getEnumNames(project.getAssignmentsForId(enumMultiAtId)));
    assertEquals(Sets.newHashSet("user3"), getEnumNames(project.getAssignmentsForId(respAtId)));
    assertEquals(Sets.newHashSet("group2", "group3"), getEnumNames(project.getAssignmentsForId(respMultiAtId)));
  }

  private Set<AttributeValue> getEnumValues(Set<String> enumNames, Integer attributeTypeId) {
    List<? extends AttributeValue> allAVs = attributeValueService.getAllAVs(attributeTypeId);
    Set<AttributeValue> newEnumValues = Sets.newHashSet();

    for (AttributeValue attributeValue : allAVs) {
      if (attributeValue instanceof EnumAV) {
        EnumAV enumAv = (EnumAV) attributeValue;
        if (enumNames.contains(enumAv.getValue())) {
          newEnumValues.add(enumAv);
        }
      }
      else {
        ResponsibilityAV respAv = (ResponsibilityAV) attributeValue;
        if (enumNames.contains(respAv.getName())) {
          newEnumValues.add(respAv);
        }
      }
    }

    return newEnumValues;
  }

  private Set<String> getEnumNames(Set<AttributeValueAssignment> enumAvas) {
    Set<String> result = Sets.newHashSet();

    for (AttributeValueAssignment ava : enumAvas) {
      result.add(ava.getAttributeValue().getValueString());
    }

    return result;
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueServiceImpl#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test
  public void testSetValueToRemove() {
    assertNotNull(project.getAssignmentForId(textAtId.getId()));
    assertNotNull(project.getAssignmentForId(dateAtId.getId()));
    assertNotNull(project.getAssignmentForId(numberAtId.getId()));

    attributeValueService.setValue(project, null, textAtId);
    attributeValueService.setValue(project, null, dateAtId);
    attributeValueService.setValue(project, null, numberAtId);

    assertNull(project.getAssignmentForId(textAtId.getId()));
    assertNull(project.getAssignmentForId(dateAtId.getId()));
    assertNull(project.getAssignmentForId(numberAtId.getId()));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueServiceImpl#setValue(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.model.attribute.AttributeValue, java.lang.Integer)}.
   */
  @Test
  public void testSetValue() {
    assertNotNull(project.getAssignmentForId(textAtId.getId()));
    assertNotNull(project.getAssignmentForId(dateAtId.getId()));
    assertNotNull(project.getAssignmentForId(numberAtId.getId()));

    TextAV newTextAV = new TextAV();
    newTextAV.setValue("new value");
    DateAV newDateAV = new DateAV();
    Date newDate = new Date();
    newDateAV.setValue(newDate);
    NumberAV newNumberAV = new NumberAV();
    newNumberAV.setValue(BigDecimal.valueOf(30));

    attributeValueService.setValue(project, newTextAV, textAtId);
    attributeValueService.setValue(project, newDateAV, dateAtId);
    attributeValueService.setValue(project, newNumberAV, numberAtId);

    assertEquals("new value", project.getAssignmentForId(textAtId.getId()).getAttributeValue().getValue());
    assertEquals(newDate, project.getAssignmentForId(dateAtId.getId()).getAttributeValue().getValue());
    assertEquals(BigDecimal.valueOf(30), project.getAssignmentForId(numberAtId.getId()).getAttributeValue().getValue());
  }

  private TextAT createTextAttribute(Project buildingBlock, String identifier) {
    final AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    final TextAT textAT1 = testDataHelper.createTextAttributeType("TextAT" + identifier, "TextAT description" + identifier, true, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(textAT1);
    TextAV textAV = testDataHelper.createTextAV("Text Value" + identifier, textAT1);

    testDataHelper.createAVA(buildingBlock, textAV);

    return textAT1;
  }

  private DateAT createDateAttribute(Project buildingBlock, String identifier, Date date) {
    final AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    final DateAT dateAT1 = testDataHelper.createDateAttributeType("DateAT" + identifier, "DateAT description" + identifier, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(dateAT1);
    DateAV dateAV = testDataHelper.createDateAV(date, dateAT1);

    if (dateAV.getValue() != null) {
      testDataHelper.createAVA(buildingBlock, dateAV);
    }
    return dateAT1;
  }

  private NumberAT createNumberAttribute(Project buildingBlock, String identifier, BigDecimal number) {
    final AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    final NumberAT at = testDataHelper.createNumberAttributeType("NumberAT" + identifier, "NumberAT description" + identifier, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(at);
    NumberAV numberAV = testDataHelper.createNumberAV(number, at);

    testDataHelper.createAVA(buildingBlock, numberAV);

    return at;
  }

  private EnumAT createEnumAttribute(Project buildingBlock, String identifier) {
    final AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    EnumAT at = testDataHelper.createEnumAttributeType(identifier, identifier, Boolean.FALSE, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(at);

    EnumAV enumAV1 = testDataHelper.createEnumAV("A", "A desc", at);
    testDataHelper.createEnumAV("B", "B desc", at);
    testDataHelper.createEnumAV("C", "C desc", at);
    testDataHelper.createEnumAV("D", "C desc", at);
    testDataHelper.createEnumAV("E", "C desc", at);

    testDataHelper.createAVA(buildingBlock, enumAV1);

    return at;
  }

  private EnumAT createEnumMultiAttribute(Project buildingBlock, String identifier) {
    final AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    EnumAT at = testDataHelper.createEnumAttributeType(identifier, identifier, Boolean.TRUE, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(at);

    EnumAV enumAV1 = testDataHelper.createEnumAV("A", "A desc", at);
    testDataHelper.createEnumAV("B", "B desc", at);
    testDataHelper.createEnumAV("C", "C desc", at);
    testDataHelper.createEnumAV("D", "C desc", at);
    EnumAV enumAV2 = testDataHelper.createEnumAV("E", "C desc", at);

    testDataHelper.createAVA(buildingBlock, enumAV1);
    testDataHelper.createAVA(buildingBlock, enumAV2);

    return at;
  }

  private ResponsibilityAT createRespAttribute(Project buildingBlock, String identifier, List<UserEntity> userEntities) {
    final AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    ResponsibilityAT at = testDataHelper.createResponsibilityAttributeType(identifier, identifier, Boolean.FALSE, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(at);

    List<ResponsibilityAV> responsibilities = testDataHelper.createResponsibilityAV(at, userEntities);

    testDataHelper.createAVA(buildingBlock, responsibilities.get(0));

    return at;
  }

  private ResponsibilityAT createRespMultiAttribute(Project buildingBlock, String identifier, List<UserEntity> userEntities) {
    final AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    ResponsibilityAT at = testDataHelper.createResponsibilityAttributeType(identifier, identifier, Boolean.TRUE, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(at);

    List<ResponsibilityAV> responsibilities = testDataHelper.createResponsibilityAV(at, userEntities);

    testDataHelper.createAVA(buildingBlock, responsibilities.get(0));
    testDataHelper.createAVA(buildingBlock, responsibilities.get(1));

    return at;
  }

}
