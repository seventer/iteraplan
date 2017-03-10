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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff.AssociateAttributeTypeWithTypeOfBuildingBlockChange;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff.AtsApplicableChange;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff.CreateAttributeTypeChange;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff.CreateEnumAvChange;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.impl.ExcelAttributeTypeReader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.VirtualAttributeType;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.permission.User;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


@ContextConfiguration({ "/applicationContext.xml", "/applicationContext-dao.xml", "/applicationContext-datasource.xml",
    "/applicationContext-ext.xml", "/applicationContext-gui.xml", "/applicationContext-staticquery.xml", "/applicationContext-test.xml",
    "/applicationContext-rest.xml" })
@ActiveProfiles("de.iteratec.iteraplan.testing")
public class AttributeExtractorTest extends AbstractJUnit4SpringContextTests {

  protected AttributeTypeService attributeTypeService;

  private static final String    VALID_EXCEL_SHEET_PATH   = "src/java/test/excel/testFiles/elasticExcel/attributeDifferenceExcelImport.xls";
  //TODO cleanup
  //  private static final String  INVALID_VERSION_EXCEL_SHEET_PATH = "src/java/test/excel/testFiles/elasticExcel/invalidVersionExcelImport.xls";
  private static final int       NEW_VALID_ATTRIBUTE_SIZE = 5;

  //GIVEN
  private Workbook               validWorkbook;
  //  private Workbook             invalidVersionWorkbook;

  private Set<AttributeType>     existingAT               = new HashSet<AttributeType>();
  private EnumAT                 enumerationAttributeType2;

  private AttributeType          numericAttributeType;
  private AttributeType          textAttributeType;
  private EnumAT                 enumerationAttributeType;
  private ResponsibilityAT       responsibilityAttributeType;

  private BuildingBlockType      informationSystem;
  private BuildingBlockType      technicalComponent;

  @Before
  public void setUp() {
    StakeholderManagerImpl mgr = new StakeholderManagerImpl();
    User user = mgr.createUser("testUser");
    ElasticMiContext ctx = new ElasticMiContext(user, "MASTER", new ElasticMiExecution(mgr, ElasticMiLoadTaskFactory.EMPTY_TASK_FACTORY));
    ElasticMiContext.setCurrentContext(ctx);
    validWorkbook = ExcelUtils.openExcelFile(VALID_EXCEL_SHEET_PATH);
    //    invalidVersionWorkbook = ExcelUtils.openExcelFile(INVALID_VERSION_EXCEL_SHEET_PATH);

  }

  protected void initiateValues() {
    setInformationSystem(bbtBuilder(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    setTechnicalComponent(bbtBuilder(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));

    setTextAttributeType(new TextAT());
    getTextAttributeType().setName("text");
    getTextAttributeType().setMandatory(false);
    getTextAttributeType().addBuildingBlockTypeTwoWay(getInformationSystem());
    getTextAttributeType().addBuildingBlockTypeTwoWay(getTechnicalComponent());

    setNumericAttributeType(new NumberAT());
    getNumericAttributeType().setName("number");
    getNumericAttributeType().setMandatory(false);
    getNumericAttributeType().addBuildingBlockTypeTwoWay(getInformationSystem());
    getNumericAttributeType().addBuildingBlockTypeTwoWay(getTechnicalComponent());

    AttributeType dateAttributeType = new DateAT();
    dateAttributeType.setName("date");
    dateAttributeType.setMandatory(false);
    dateAttributeType.addBuildingBlockTypeTwoWay(getInformationSystem());
    dateAttributeType.addBuildingBlockTypeTwoWay(getTechnicalComponent());

    EnumAV enumAV = new EnumAV();
    enumAV.setName("eins");
    setEnumerationAttributeType(new EnumAT());
    getEnumerationAttributeType().setName("enum");
    getEnumerationAttributeType().setMandatory(false);
    getEnumerationAttributeType().addBuildingBlockTypeTwoWay(getInformationSystem());
    getEnumerationAttributeType().addAttribueValueTwoWay(enumAV);

    enumerationAttributeType2 = new EnumAT();
    enumerationAttributeType2.setName("enum2");
    enumerationAttributeType2.setMandatory(false);
    enumerationAttributeType2.setMultiassignmenttype(false);

    setResponsibilityAttributeType(new ResponsibilityAT());
    getResponsibilityAttributeType().setName("resp");
    getResponsibilityAttributeType().setMandatory(false);
    getResponsibilityAttributeType().setMultiassignmenttype(true);

    existingAT.add(getNumericAttributeType());
    existingAT.add(dateAttributeType);
    existingAT.add(getEnumerationAttributeType());
    existingAT.add(getTextAttributeType());
    existingAT.add(getResponsibilityAttributeType());
  }

  @Test
  public void testAttributeRead() {
    //Tests if attributes are correctly found
    ExcelAttributeTypeReader reader = new ExcelAttributeTypeReader(validWorkbook, MessageListener.NOOP_LISTENER);
    List<VirtualAttributeType> result = reader.readVirtualAttributes();
    assertTrue(result.contains(new VirtualAttributeType(TextAT.class, "text", false, false)));
    assertTrue(result.contains(new VirtualAttributeType(TextAT.class, "text2", false, false)));
    assertTrue(result.contains(new VirtualAttributeType(NumberAT.class, "number", false, false)));
    assertTrue(result.contains(new VirtualAttributeType(EnumAT.class, "enum", false, false)));
    assertTrue(result.contains(new VirtualAttributeType(DateAT.class, "date", false, false)));
    //Test if no more attributes than the one before were found
    assertEquals(NEW_VALID_ATTRIBUTE_SIZE, result.size());
    //Tests if invalid attributes are found that should not be found e.g. permissions are not sufficient
  }

  @Test
  public void testAttributeWrite() {
    initiateValues();
    List<AtsApplicableChange> diffs = new ArrayList<AtsApplicableChange>();
    fillDiffList(diffs);
    attributeTypeService = EasyMock.createMock(AttributeTypeService.class);

    EasyMock.expect(attributeTypeService.saveOrUpdate(enumerationAttributeType2)).andReturn(null);

    EasyMock.expect(attributeTypeService.getAttributeTypeByName("enum2")).andReturn(enumerationAttributeType2);
    EasyMock.expect(attributeTypeService.saveOrUpdate(enumerationAttributeType2)).andReturn(null);
    EasyMock.expect(attributeTypeService.getAttributeTypeByName("enum2")).andReturn(enumerationAttributeType2);
    EasyMock.expect(attributeTypeService.saveOrUpdate(enumerationAttributeType2)).andReturn(null);

    EasyMock.expect(attributeTypeService.getAttributeTypeByName("enum2")).andReturn(enumerationAttributeType2);
    EasyMock.expect(attributeTypeService.saveOrUpdate(enumerationAttributeType2)).andReturn(null);
    EasyMock.replay(attributeTypeService);

    AttributeTypeWriter attributeTypeWriter = new AttributeTypeWriter(attributeTypeService);
    attributeTypeWriter.write(diffs, MessageListener.LOG_LISTENER);
    EasyMock.verify(attributeTypeService);
  }

  private void fillDiffList(List<AtsApplicableChange> diffs) {
    diffs.add(new CreateAttributeTypeChange(new VirtualAttributeType(EnumAT.class, "enum2", false, false), new AttributeTypeGroup()));
    diffs.add(new CreateEnumAvChange("eins", "enum2"));
    diffs.add(new CreateEnumAvChange("zwei", "enum2"));
    diffs.add(new AssociateAttributeTypeWithTypeOfBuildingBlockChange("enum2", getTechnicalComponent()));
  }

  protected void fillVATTestList(List<VirtualAttributeType> vatList) {
    VirtualAttributeType vat1 = new VirtualAttributeType(TextAT.class, "text", false, false);
    vat1.addAssociatedToBB(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    vat1.addAssociatedToBB(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);

    VirtualAttributeType vat2 = new VirtualAttributeType(NumberAT.class, "number", true, false);
    vat2.addAssociatedToBB(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    vat2.addAssociatedToBB(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);

    VirtualAttributeType vat3 = new VirtualAttributeType(EnumAT.class, "enum1", false, true);
    vat3.addAssociatedToBB(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    vat3.addAssociatedToBB(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    vat3.addEnumAV("enum1");
    vat3.addEnumAV("enum2");

    VirtualAttributeType vat4 = new VirtualAttributeType(EnumAT.class, "enum2", false, false);
    vat4.addAssociatedToBB(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    vat4.addAssociatedToBB(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    vat4.addEnumAV("enum1");
    vat4.addEnumAV("enum2");

    VirtualAttributeType vat5 = new VirtualAttributeType(TextAT.class, "resp", false, true);
    vat5.addAssociatedToBB(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

    vatList.add(vat1);
    vatList.add(vat2);
    vatList.add(vat3);
    vatList.add(vat4);
    vatList.add(vat5);
  }

  private BuildingBlockType bbtBuilder(TypeOfBuildingBlock tobb) {
    return new BuildingBlockType(tobb);
  }

  public AttributeType getTextAttributeType() {
    return textAttributeType;
  }

  public void setTextAttributeType(AttributeType textAttributeType) {
    this.textAttributeType = textAttributeType;
  }

  public AttributeType getNumericAttributeType() {
    return numericAttributeType;
  }

  public void setNumericAttributeType(AttributeType numericAttributeType) {
    this.numericAttributeType = numericAttributeType;
  }

  public EnumAT getEnumerationAttributeType() {
    return enumerationAttributeType;
  }

  public void setEnumerationAttributeType(EnumAT enumerationAttributeType) {
    this.enumerationAttributeType = enumerationAttributeType;
  }

  public ResponsibilityAT getResponsibilityAttributeType() {
    return responsibilityAttributeType;
  }

  public void setResponsibilityAttributeType(ResponsibilityAT responsibilityAttributeType) {
    this.responsibilityAttributeType = responsibilityAttributeType;
  }

  public BuildingBlockType getTechnicalComponent() {
    return technicalComponent;
  }

  public void setTechnicalComponent(BuildingBlockType technicalComponent) {
    this.technicalComponent = technicalComponent;
  }

  public BuildingBlockType getInformationSystem() {
    return informationSystem;
  }

  public void setInformationSystem(BuildingBlockType informationSystem) {
    this.informationSystem = informationSystem;
  }
}
