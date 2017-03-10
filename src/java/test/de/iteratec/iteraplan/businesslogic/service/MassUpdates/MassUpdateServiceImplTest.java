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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.MockTestDataFactory;
import de.iteratec.iteraplan.MockTestHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemDomainTypeMu;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BusinessProcessService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.MassUpdateServiceImpl;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.BusinessMappingCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.InformationSystemDomainCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.InformationSystemReleaseCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttribute;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttributeConfig;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttributeItem;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateLine;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateResult;


public class MassUpdateServiceImplTest {

  private MassUpdateServiceImpl          massUpdateService;
  private AttributeTypeService           attributeTypeServiceMock;
  private AttributeValueService          attributeValueServiceMock;
  private InformationSystemDomainService isdServiceMock;
  private ProjectService                 projectService;

  @Before
  public void setUp() throws Exception {
    massUpdateService = new MassUpdateServiceImpl();

    attributeTypeServiceMock = MockTestHelper.createMock(AttributeTypeService.class);
    attributeValueServiceMock = MockTestHelper.createMock(AttributeValueService.class);
    isdServiceMock = MockTestHelper.createMock(InformationSystemDomainService.class);
    projectService = MockTestHelper.createMock(ProjectService.class);

    massUpdateService.setAttributeTypeService(attributeTypeServiceMock);
    massUpdateService.setAttributeValueService(attributeValueServiceMock);
    massUpdateService.setInformationSystemDomainService(isdServiceMock);
    massUpdateService.setProjectService(projectService);
  }

  /**
   * Tests the {@link MassUpdateServiceImpl#initAttributes(String, List, List, int)} Method. Creates
   * some test objects that are passed in as parameters and checks if the attributes on the line are
   * initialized correctly
   */
  @Test
  public void testInitAttributes() {
    // set up the test data
    MockTestDataFactory mtdf = MockTestDataFactory.getInstance();
    AttributeType attributeType = mtdf.generateTestObject(TextAT.class);
    attributeType.setId(Integer.valueOf(10));

    Project project1 = mtdf.getProjectTestData();
    project1.setId(Integer.valueOf(1));
    Project project2 = mtdf.getProjectTestData();
    project2.setId(Integer.valueOf(2));

    MassUpdateLine<Project> line1 = new MassUpdateLine<Project>();
    line1.setBuildingBlockToUpdate(project1);
    MassUpdateLine<Project> line2 = new MassUpdateLine<Project>();
    line2.setBuildingBlockToUpdate(project2);
    List<MassUpdateLine<? extends BuildingBlock>> lines = new ArrayList<MassUpdateLine<? extends BuildingBlock>>();
    lines.add(line1);
    lines.add(line2);

    // the attribute type id
    String selectedAttributeId = BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE + "_null_10";

    List<MassUpdateAttributeConfig> massUpdateAttributeConfig = new ArrayList<MassUpdateAttributeConfig>();
    int attributeIndex = 0;

    MockTestHelper.expect(attributeTypeServiceMock.loadObjectById(Integer.valueOf(10))).andReturn(attributeType);

    HashBucketMap<Integer, AttributeValue> bbIdToAVs = new HashBucketMap<Integer, AttributeValue>();

    AttributeValue attributeValue1 = mtdf.generateTestObject(TextAV.class);
    List<Integer> ids = new ArrayList<Integer>();
    ids.add(project1.getId());
    bbIdToAVs.add(project1.getId(), attributeValue1);
    MockTestHelper.expect(attributeValueServiceMock.getBuildingBlockIdsToConnectedAttributeValues(ids, Integer.valueOf(10))).andReturn(bbIdToAVs);

    AttributeValue attributeValue2 = mtdf.generateTestObject(TextAV.class);
    ids = new ArrayList<Integer>();
    ids.add(project2.getId());
    bbIdToAVs.add(project2.getId(), attributeValue2);
    MockTestHelper.expect(attributeValueServiceMock.getBuildingBlockIdsToConnectedAttributeValues(ids, Integer.valueOf(10))).andReturn(bbIdToAVs);

    MockTestHelper.replay(attributeTypeServiceMock, attributeValueServiceMock);

    // END set up the test data

    // Tests
    massUpdateService.initAttributes(selectedAttributeId, lines, massUpdateAttributeConfig, attributeIndex);

    assertEquals(1, massUpdateAttributeConfig.size());
    for (MassUpdateAttributeConfig config : massUpdateAttributeConfig) {
      assertEquals(attributeType.getId(), config.getAttributeTypeId());
      assertNull(config.getAttributeValues());
    }

    int i = 0;
    for (MassUpdateLine<? extends BuildingBlock> line : lines) {
      assertEquals(1, line.getAttributes().size());
      assertEquals(Integer.valueOf(10), line.getAttributes().get(0).getAttributeTypeId());
      if (i == 0) {
        // line 0
        assertEquals(attributeValue1.getValueString(), line.getAttributes().get(0).getMassUpdateAttributeItem().getNewAttributeValue());
      }
      else {
        assertEquals(attributeValue2.getValueString(), line.getAttributes().get(0).getMassUpdateAttributeItem().getNewAttributeValue());
      }
      i++;
    }

    MockTestHelper.verify(attributeTypeServiceMock, attributeValueServiceMock);
  }

  @Test
  public void testInitComponentModel() {
    MockTestDataFactory mtdf = MockTestDataFactory.getInstance();
    mtdf.createUserContext();

    InformationSystemDomain isd = mtdf.getInformationSystemDomainTestData();
    InformationSystemDomainCmMu componentModel = new InformationSystemDomainCmMu();
    List<String> properties = new ArrayList<String>();
    List<String> associations = new ArrayList<String>();
    properties.add(InformationSystemDomainTypeMu.PROPERTY_NAME);

    MockTestHelper.expect(isdServiceMock.loadObjectById(isd.getId())).andReturn(isd);
    MockTestHelper.replay(isdServiceMock);

    massUpdateService.initComponentModel(componentModel, isd, properties, associations);

    assertEquals(isd.getName(), componentModel.getNameModel().getCurrent());
    assertNull(componentModel.getDescriptionComponentModel());

    MockTestHelper.verify(isdServiceMock);
  }

  @Test
  public void testUpdateAttributes() {
    MockTestDataFactory mtdf = MockTestDataFactory.getInstance();
    mtdf.createUserContext();

    // Generate the mock object for the text attribute type.
    TextAT textAT = mtdf.generateTestObject(TextAT.class);
    textAT.setId(Integer.valueOf(10));

    // Generate the mock object for the attribute type group.
    AttributeTypeGroup atGroup = mtdf.generateTestObject(AttributeTypeGroup.class);
    textAT.setAttributeTypeGroup(atGroup);

    // Generate the mock object for the text attribute value.
    TextAV textAV = mtdf.generateTestObject(TextAV.class);

    // Generate the mock object for the task.
    Project task = mtdf.getProjectTestData();
    task.setId(Integer.valueOf(1));

    MassUpdateLine<Project> line = new MassUpdateLine<Project>();
    line.setBuildingBlockToUpdate(task);

    MassUpdateAttribute attribute = new MassUpdateAttribute(0);
    attribute.setType(BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE);
    attribute.setMassUpdateAttributeItem(new MassUpdateAttributeItem(task, textAT.getId()));
    attribute.getMassUpdateAttributeItem().setNewAttributeValue("NEWVALUE");

    String selectedAttributeId = BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE + "_null_10";
    line.addAttribute(selectedAttributeId, attribute);

    // Record expected behaviour.
    MockTestHelper.expect(projectService.loadObjectById(task.getId())).andReturn(task);
    MockTestHelper.expect(attributeTypeServiceMock.loadObjectById(textAT.getId(), TextAT.class)).andReturn(textAT);
    MockTestHelper.expect(projectService.saveOrUpdate(task)).andReturn(task);

    attributeValueServiceMock.setValue(task, textAV, textAT);
    attributeValueServiceMock.saveOrUpdateAttributeValues(task);

    HashBucketMap<Integer, AttributeValue> map = new HashBucketMap<Integer, AttributeValue>();
    List<Integer> identifiers = new ArrayList<Integer>();
    identifiers.add(task.getId());
    map.add(task.getId(), textAV);
    MockTestHelper.expect(attributeValueServiceMock.getBuildingBlockIdsToConnectedAttributeValues(identifiers, Integer.valueOf(10))).andReturn(map);

    // Switch to replay state.
    MockTestHelper.replay(projectService, attributeTypeServiceMock, attributeValueServiceMock);

    massUpdateService.updateAttributes(line);

    assertEquals(textAV.getValue(), line.getAttributes().get(0).getMassUpdateAttributeItem().getNewAttributeValue());

    // Verify behaviour: Check if the expected method calls occur.
    MockTestHelper.verify(projectService, attributeTypeServiceMock, attributeValueServiceMock);
  }

  @Test
  public void testUpdateBusinessMappingLineNoChanges() {
    MockTestDataFactory mtdf = MockTestDataFactory.getInstance();
    mtdf.createUserContext();
    
    // acceptable for testing purposes
    @SuppressWarnings("unchecked")
    MassUpdateLine<InformationSystemRelease> line = MockTestHelper.createNiceMock(MassUpdateLine.class);
    MassUpdateComponentModel<InformationSystemRelease> muCm = MockTestHelper.createNiceMock(InformationSystemReleaseCmMu.class);
    
    // mocking and setting various returns to prepared objects
    List<BusinessMappingCmMu> bmcmmuList = new LinkedList<BusinessMappingCmMu>();
    BusinessMappingCmMu bmcmmu = MockTestHelper.createMock(BusinessMappingCmMu.class);
    
    InformationSystemReleaseCmMu isrcmuCast = (InformationSystemReleaseCmMu)muCm;
    MockTestHelper.expect(isrcmuCast.getBusinessMappingComponentModels()).andReturn(bmcmmuList);
    MockTestHelper.replay(isrcmuCast);
    
    InformationSystemRelease isr = MockTestHelper.createNiceMock(InformationSystemRelease.class);
    MockTestHelper.expect(line.getBuildingBlockToUpdate()).andReturn(isr);
    MockTestHelper.expect(line.getComponentModel()).andReturn(muCm);
    
    InformationSystemReleaseService isrService = MockTestHelper.createMock(InformationSystemReleaseService.class);
    massUpdateService.setInformationSystemReleaseService(isrService);
    
    // since merge() copies the state, the same object is returned to avoid interaction with the dao
    MockTestHelper.expect(isrService.merge(isr)).andReturn(isr); 
    MockTestHelper.replay(isrService);
    
    BusinessMapping bm = MockTestHelper.createNiceMock(BusinessMapping.class);
    MockTestHelper.expect(isr.getBusinessMapping(Integer.valueOf(5))).andReturn(bm);
    MockTestHelper.replay(isr);
    MockTestHelper.expect(bm.getId()).andReturn(Integer.valueOf(5));
    
    // mocking the various components of the BusinessMapping
    BusinessProcess bp = mtdf.getBusinessProcessTestData(); bp.setId(Integer.valueOf(20));
    BusinessUnit businessUnit = mtdf.getBusinessUnitTestData(); businessUnit.setId(Integer.valueOf(21));
    Product product = mtdf.getProductTestData(); product.setId(Integer.valueOf(22));
    MockTestHelper.expect(bm.getBusinessProcess()).andReturn(bp);
    MockTestHelper.expect(bm.getBusinessUnit()).andReturn(businessUnit);
    MockTestHelper.expect(bm.getProduct()).andReturn(product);
    MockTestHelper.replay(bm);
    
    // mocking the BusinessMappingCmMu's behavior, since the "no change"-scenario is being tested the ids are identical
    MockTestHelper.expect(bmcmmu.getSelectedBusinessProcessId()).andReturn(Integer.valueOf(20));
    MockTestHelper.expect(bmcmmu.getSelectedBusinessUnitId()).andReturn(Integer.valueOf(21));
    MockTestHelper.expect(bmcmmu.getSelectedProductId()).andReturn(Integer.valueOf(22));
    MockTestHelper.expect(bmcmmu.getEncapsulatedBusinessMappings()).andReturn(bm);
    MockTestHelper.expect(bmcmmu.getCustomHashCode()).andReturn(Integer.valueOf(bmcmmu.hashCode()));
    MockTestHelper.replay(bmcmmu);
    bmcmmuList.add(bmcmmu);

    MassUpdateResult muRes = MockTestHelper.createMock(MassUpdateResult.class);
    MockTestHelper.expect(line.getMassUpdateResult()).andReturn(muRes);
    MockTestHelper.replay(line);
    
    massUpdateService.updateBusinessMappingLine(line);
  }

  @Test
  public void testUpdateBusinessMappingLineWithChanges() {
    MockTestDataFactory mtdf = MockTestDataFactory.getInstance();
    mtdf.createUserContext();
    
    // acceptable for testing purposes
    @SuppressWarnings("unchecked")
    MassUpdateLine<InformationSystemRelease> line = MockTestHelper.createNiceMock(MassUpdateLine.class);
    MassUpdateComponentModel<InformationSystemRelease> componentModel = MockTestHelper.createNiceMock(InformationSystemReleaseCmMu.class);
    
    // mocking and setting various returns to prepared objects
    List<BusinessMappingCmMu> bmcmuList = new LinkedList<BusinessMappingCmMu>();
    BusinessMappingCmMu bmcmmu = MockTestHelper.createNiceMock(BusinessMappingCmMu.class);
    
    InformationSystemReleaseCmMu isrcmuCast = (InformationSystemReleaseCmMu)componentModel;
    MockTestHelper.expect(isrcmuCast.getBusinessMappingComponentModels()).andReturn(bmcmuList);
    MockTestHelper.replay(isrcmuCast);
    
    InformationSystemRelease isr = MockTestHelper.createNiceMock(InformationSystemRelease.class);
    MockTestHelper.expect(line.getBuildingBlockToUpdate()).andReturn(isr);
    MockTestHelper.expect(line.getComponentModel()).andReturn(componentModel);
    
    InformationSystemReleaseService isrService = MockTestHelper.createNiceMock(InformationSystemReleaseService.class);
    massUpdateService.setInformationSystemReleaseService(isrService);
    // bpService needed since the BusínessProcess will be the changed component
    BusinessProcessService bpService = MockTestHelper.createMock(BusinessProcessService.class);
    massUpdateService.setBusinessProcessService(bpService);
    
    // since merge() copies the state, the same object is returned to avoid interaction with the dao
    MockTestHelper.expect(isrService.merge(isr)).andReturn(isr); 
    MockTestHelper.replay(isrService);
    
    BusinessMapping bm = MockTestHelper.createNiceMock(BusinessMapping.class);
    MockTestHelper.expect(isr.getBusinessMapping(Integer.valueOf(5))).andReturn(bm);
    MockTestHelper.replay(isr);
    MockTestHelper.expect(bm.getId()).andReturn(Integer.valueOf(5));
    
    // mocking the various components of the BusinessMapping
    BusinessProcess bp = mtdf.getBusinessProcessTestData(); bp.setId(Integer.valueOf(201));
    BusinessUnit businessUnit = mtdf.getBusinessUnitTestData(); businessUnit.setId(Integer.valueOf(21));
    Product product = mtdf.getProductTestData(); product.setId(Integer.valueOf(22));
    MockTestHelper.expect(bm.getBusinessProcess()).andReturn(bp);
    MockTestHelper.expect(bm.getBusinessUnit()).andReturn(businessUnit);
    MockTestHelper.expect(bm.getProduct()).andReturn(product);
    MockTestHelper.expect(bm.getInformationSystemRelease()).andReturn(isr);
    MockTestHelper.replay(bm);
    
    // mocking the BusinessMappingCmMu's behavior, BusinessProcessId is different
    MockTestHelper.expect(bmcmmu.getSelectedBusinessProcessId()).andReturn(Integer.valueOf(20)).anyTimes();
    MockTestHelper.expect(bmcmmu.getSelectedBusinessUnitId()).andReturn(Integer.valueOf(21));
    MockTestHelper.expect(bmcmmu.getSelectedProductId()).andReturn(Integer.valueOf(22));
    MockTestHelper.expect(bmcmmu.getEncapsulatedBusinessMappings()).andReturn(bm);
    MockTestHelper.expect(bmcmmu.getCustomHashCode()).andReturn(Integer.valueOf(bmcmmu.hashCode()));
    MockTestHelper.expect(bpService.loadObjectById(Integer.valueOf(20))).andReturn(mtdf.getBusinessProcessTestData());
    MockTestHelper.replay(bmcmmu);
    MockTestHelper.replay(bpService);
    bmcmuList.add(bmcmmu);

    MassUpdateResult muRes = MockTestHelper.createMock(MassUpdateResult.class);
    MockTestHelper.expect(line.getMassUpdateResult()).andReturn(muRes);
    MockTestHelper.replay(line);
    
    massUpdateService.updateBusinessMappingLine(line);
  }
  
}
