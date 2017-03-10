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
package de.iteratec.iteraplan.presentation.dialog.timeseries;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.webflow.execution.FlowExecutionContext;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.TimeseriesService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.dialog.common.CanHaveTimeseriesBaseMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.BuildingBlockComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.timeseries.TimeseriesAttributeComponentModel;


/**
 * This class contains tests making sure the necessary methods for synchronizing timeseries data with building block attributes
 * are called when using the public "create mem bean", "update" and "save component model" methods of {@link TimeseriesCMBaseFrontendService}s.
 */
public class TimeseriesCMBaseFrontendServiceTest {
  private TimeseriesCMBaseFrontendServiceMockImpl timeseriesCMBaseFrontendService;

  private static final Integer                    ENUM_AT_ID = Integer.valueOf(1);
  private static final Integer                    NUM_AT_ID  = Integer.valueOf(2);
  private static final Integer                    TEST_BB_ID = Integer.valueOf(42);

  private EnumAT                                  enumAT;
  private NumberAT                                numAT;
  private BuildingBlock                           testBB;
  private Timeseries                              enumTimeseries;
  private Timeseries                              numTimeseries;

  @SuppressWarnings({ "deprecation" })
  @Before
  public void onSetUp() {
    UserContext.setCurrentUserContext(new UserContext("test", Collections.<Role> emptySet(), Locale.ENGLISH, new User()));
    timeseriesCMBaseFrontendService = new TimeseriesCMBaseFrontendServiceMockImpl();

    AttributeTypeGroup atg = new AttributeTypeGroup();

    enumAT = new EnumAT();
    enumAT.setId(ENUM_AT_ID);
    enumAT.setTimeseries(true);
    enumAT.setName("Enum AT");
    atg.addAttributeTypeTwoWay(enumAT);

    numAT = new NumberAT();
    numAT.setId(NUM_AT_ID);
    numAT.setTimeseries(true);
    numAT.setName("Number AT");
    atg.addAttributeTypeTwoWay(numAT);

    BuildingBlockType ieBBT = new BuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, true);
    ieBBT.addAttributeTypeTwoWay(enumAT);
    ieBBT.addAttributeTypeTwoWay(numAT);

    testBB = new InfrastructureElement();
    testBB.setId(TEST_BB_ID);
    testBB.setBuildingBlockType(ieBBT);
    ((InfrastructureElement) testBB).setName("Test IE");

    enumTimeseries = new Timeseries();
    enumTimeseries.setAttribute(enumAT);
    enumTimeseries.setBuildingBlock(testBB);

    numTimeseries = new Timeseries();
    numTimeseries.setId(Integer.valueOf(101));
    numTimeseries.setAttribute(numAT);
    numTimeseries.setBuildingBlock(testBB);
    numTimeseries.addEntry(new TimeseriesEntry(new Date(0), "42"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService#getCreateMemBean(org.springframework.webflow.execution.FlowExecutionContext)}.
   */
  @Test
  public void testGetCreateMemBean() {
    TimeseriesService timeseriesServiceMock = getTimeseriesServiceMockForInitializingTests();
    timeseriesCMBaseFrontendService.setTimeseriesService(timeseriesServiceMock);

    // test call
    CanHaveTimeseriesMemBeanMockImpl memBean = timeseriesCMBaseFrontendService.getCreateMemBean(null);

    // assertions
    List<String> expectedFrontendServiceCalls = Lists.newArrayList("createNewMemBean", "createNewModelObject", "createComponentModel(CREATE)",
        "initializeTimeseriesComponentModels");
    List<String> expectedCmCalls = Lists.newArrayList("initializeFrom");

    assertEquals(expectedFrontendServiceCalls, timeseriesCMBaseFrontendService.calls);
    assertEquals(expectedCmCalls, memBean.getComponentModel().calls);

    EasyMock.verify(timeseriesServiceMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService#getCreateMemBeanWithDataFromId(java.lang.Integer)}.
   */
  @Test
  public void testGetCreateMemBeanWithDataFromId() {
    TimeseriesService timeseriesServiceMock = getTimeseriesServiceMockForInitializingTests();
    timeseriesCMBaseFrontendService.setTimeseriesService(timeseriesServiceMock);

    // test call
    CanHaveTimeseriesMemBeanMockImpl memBean = timeseriesCMBaseFrontendService.getCreateMemBeanWithDataFromId(Integer.valueOf(42));

    // assertions
    List<String> expectedFrontendServiceCalls = Lists.newArrayList("createNewMemBean", "getModelObjectById(" + TEST_BB_ID + ")",
        "prepareBuildingBlockForCopy", "createComponentModel(CREATE)", "initializeTimeseriesComponentModels");
    List<String> expectedCmCalls = Lists.newArrayList("initializeFrom");

    assertEquals(expectedFrontendServiceCalls, timeseriesCMBaseFrontendService.calls);
    assertEquals(expectedCmCalls, memBean.getComponentModel().calls);

    EasyMock.verify(timeseriesServiceMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService#getEditMemBean(de.iteratec.iteraplan.presentation.dialog.common.CanHaveTimeseriesBaseMemBean, java.lang.Integer, org.springframework.webflow.execution.RequestContext, org.springframework.webflow.execution.FlowExecutionContext)}.
   */
  @Test
  public void testGetEditMemBean() {
    TimeseriesService timeseriesServiceMock = getTimeseriesServiceMockForInitializingTests();
    timeseriesCMBaseFrontendService.setTimeseriesService(timeseriesServiceMock);

    CanHaveTimeseriesMemBeanMockImpl memBean = new CanHaveTimeseriesMemBeanMockImpl();
    // test call
    memBean = timeseriesCMBaseFrontendService.getEditMemBean(memBean, TEST_BB_ID, null, null);

    // assertions
    List<String> expectedFrontendServiceCalls = Lists.newArrayList("getModelObjectById(" + TEST_BB_ID + ")", "createComponentModel(EDIT)",
        "initializeTimeseriesComponentModels", "enterEditMode");
    List<String> expectedCmCalls = Lists.newArrayList("setEntity", "initializeFrom");

    assertEquals(expectedFrontendServiceCalls, timeseriesCMBaseFrontendService.calls);
    assertEquals(expectedCmCalls, memBean.getComponentModel().calls);

    EasyMock.verify(timeseriesServiceMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService#getMemBean(java.lang.Integer)}.
   */
  @Test
  public void testGetMemBean() {
    TimeseriesService timeseriesServiceMock = getTimeseriesServiceMockForInitializingTests();
    timeseriesCMBaseFrontendService.setTimeseriesService(timeseriesServiceMock);

    // test call
    CanHaveTimeseriesMemBeanMockImpl memBean = timeseriesCMBaseFrontendService.getMemBean(TEST_BB_ID);

    // assertions
    List<String> expectedFrontendServiceCalls = Lists.newArrayList("createNewMemBean", "getModelObjectById(" + TEST_BB_ID + ")",
        "createComponentModel(READ)", "initializeTimeseriesComponentModels");
    List<String> expectedCmCalls = Lists.newArrayList("initializeFrom");

    assertEquals(expectedFrontendServiceCalls, timeseriesCMBaseFrontendService.calls);
    assertEquals(expectedCmCalls, memBean.getComponentModel().calls);

    EasyMock.verify(timeseriesServiceMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService#updateComponentModel(de.iteratec.iteraplan.presentation.dialog.common.CanHaveTimeseriesBaseMemBean, org.springframework.webflow.execution.RequestContext, org.springframework.webflow.execution.FlowExecutionContext)}.
   */
  @Test
  public void testUpdateComponentModel() {
    CanHaveTimeseriesMemBeanMockImpl memBean = setUpMemBean();

    TimeseriesAttributeComponentModel timeseriesCmMock1 = getTimeseriesCmUpdateMock();
    memBean.getTimeseriesComponentModels().put(ENUM_AT_ID, timeseriesCmMock1);

    TimeseriesAttributeComponentModel timeseriesCmMock2 = getTimeseriesCmUpdateMock();
    memBean.getTimeseriesComponentModels().put(NUM_AT_ID, timeseriesCmMock2);

    timeseriesCMBaseFrontendService.setTimeseriesUpdateService(new TimeseriesUpdateFrontendService());

    // test call
    timeseriesCMBaseFrontendService.updateComponentModel(memBean, null, null);

    // assertions
    List<String> expectedBbCmCalls = Lists.newArrayList("setEntity", "update"); // "setEntity" is from setUpMemBean

    assertEquals(expectedBbCmCalls, memBean.getComponentModel().calls);
    assertEquals(2, memBean.getTimeseriesComponentModels().size());

    EasyMock.verify(timeseriesCmMock1, timeseriesCmMock2);
  }

  /**
   * @return A Mock for {@link TimeseriesAttributeComponentModel} which expects an update call.
   */
  private TimeseriesAttributeComponentModel getTimeseriesCmUpdateMock() {
    TimeseriesAttributeComponentModel timeseriesCmMock = EasyMock.createMock(TimeseriesAttributeComponentModel.class);
    timeseriesCmMock.update();
    EasyMock.expectLastCall().once();
    EasyMock.expect(timeseriesCmMock.getLatestEntryValue()).andReturn(null);
    EasyMock.replay(timeseriesCmMock);
    return timeseriesCmMock;
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService#saveComponentModel(de.iteratec.iteraplan.presentation.dialog.common.CanHaveTimeseriesBaseMemBean, java.lang.Integer, org.springframework.webflow.execution.RequestContext, org.springframework.webflow.execution.FlowExecutionContext)}.
   */
  @Test
  public void testSaveComponentModel() {
    CanHaveTimeseriesMemBeanMockImpl memBean = setUpMemBean();

    TimeseriesAttributeComponentModel timeseriesCmMock1 = getTimeseriesCmConfigureEnumMock();
    memBean.getTimeseriesComponentModels().put(ENUM_AT_ID, timeseriesCmMock1);

    TimeseriesAttributeComponentModel timeseriesCmMock2 = getTimeseriesCmConfigureNumMock();
    memBean.getTimeseriesComponentModels().put(NUM_AT_ID, timeseriesCmMock2);

    TimeseriesService timeseriesServiceMock = getTimeseriesServiceMockForSavingTests();
    timeseriesCMBaseFrontendService.setTimeseriesService(timeseriesServiceMock);

    BuildingBlockService<BuildingBlock, Integer> serviceMock = getBbServiceSaveMock();
    timeseriesCMBaseFrontendService.setService(serviceMock);

    // test call
    timeseriesCMBaseFrontendService.saveComponentModel(memBean, TEST_BB_ID, null, null);

    // assertions
    List<String> expectedFrontendServiceCalls = Lists.newArrayList("validateOnSave", "saveTimeseriesComponentModels", "leaveEditMode");
    List<String> expectedCmCalls = Lists.newArrayList("setEntity", "getEntity", "update", "configure"); // "setEntity" is from setUpMemBean

    assertEquals(expectedFrontendServiceCalls, timeseriesCMBaseFrontendService.calls);
    assertEquals(expectedCmCalls, memBean.getComponentModel().calls);

    EasyMock.verify(serviceMock, timeseriesServiceMock, timeseriesCmMock1, timeseriesCmMock2);
  }

  /**
   * @return A Mock for {@link TimeseriesAttributeComponentModel} which expects a configure call for {@link #enumTimeseries}
   *         and in response adds an entry to the timeseries for testing the case of entries being added to the timeseries. 
   */
  private TimeseriesAttributeComponentModel getTimeseriesCmConfigureEnumMock() {
    TimeseriesAttributeComponentModel timeseriesCmMock = EasyMock.createMock(TimeseriesAttributeComponentModel.class);
    timeseriesCmMock.configure(EasyMock.eq(enumTimeseries));
    EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
      public Object answer() {
        Timeseries timeseries = (Timeseries) EasyMock.getCurrentArguments()[0];
        timeseries.addEntry(new TimeseriesEntry(new Date(0), "42"));
        enumTimeseries.addEntry(new TimeseriesEntry(new Date(0), "42")); // needed for an expect of a timeseries service mock, see getTimeseriesServiceMockForSavingTests()
        return null;
      }
    });
    EasyMock.replay(timeseriesCmMock);
    return timeseriesCmMock;
  }

  /**
   * @return A Mock for {@link TimeseriesAttributeComponentModel} which expects a configure call for {@link #enumTimeseries}
   *         and in response removes the single entry of the timeseries for testing the case of deleting a timeseries
   *         when all its entries are removed. 
   */
  private TimeseriesAttributeComponentModel getTimeseriesCmConfigureNumMock() {
    TimeseriesAttributeComponentModel timeseriesCmMock = EasyMock.createMock(TimeseriesAttributeComponentModel.class);
    timeseriesCmMock.configure(EasyMock.eq(numTimeseries));
    EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
      public Object answer() {
        Timeseries timeseries = (Timeseries) EasyMock.getCurrentArguments()[0];
        timeseries.removeEntry(new Date(0));
        return null;
      }
    });
    EasyMock.replay(timeseriesCmMock);
    return timeseriesCmMock;
  }

  /**
   * @return A Mock for {@link BuildingBlockService} expecting the merge and saveOrUpdate calls which should happen
   *         when a component model for a previously existing entity (edit mode) is saved.
   */
  private BuildingBlockService<BuildingBlock, Integer> getBbServiceSaveMock() {
    @SuppressWarnings("unchecked")
    BuildingBlockService<BuildingBlock, Integer> serviceMock = EasyMock.createMock(BuildingBlockService.class);
    EasyMock.expect(serviceMock.merge(testBB)).andReturn(testBB);
    EasyMock.expect(serviceMock.saveOrUpdate(testBB)).andReturn(testBB);
    EasyMock.replay(serviceMock);
    return serviceMock;
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.presentation.dialog.timeseries.TimeseriesCMBaseFrontendService#saveNewComponentModel(de.iteratec.iteraplan.presentation.dialog.common.CanHaveTimeseriesBaseMemBean, org.springframework.webflow.execution.RequestContext, org.springframework.webflow.execution.FlowExecutionContext)}.
   */
  @Test
  public void testSaveNewComponentModel() {
    CanHaveTimeseriesMemBeanMockImpl memBean = setUpMemBean();

    TimeseriesAttributeComponentModel timeseriesCmMock1 = getTimeseriesCmConfigureEnumMock();
    memBean.getTimeseriesComponentModels().put(ENUM_AT_ID, timeseriesCmMock1);

    TimeseriesAttributeComponentModel timeseriesCmMock2 = getTimeseriesCmConfigureNumMock();
    memBean.getTimeseriesComponentModels().put(NUM_AT_ID, timeseriesCmMock2);

    TimeseriesService timeseriesServiceMock = getTimeseriesServiceMockForSavingTests();
    timeseriesCMBaseFrontendService.setTimeseriesService(timeseriesServiceMock);

    BuildingBlockService<BuildingBlock, Integer> serviceMock = getBbServiceSaveNewMock();
    timeseriesCMBaseFrontendService.setService(serviceMock);

    // test call
    timeseriesCMBaseFrontendService.saveNewComponentModel(memBean, null, null);

    // assertions
    List<String> expectedFrontendServiceCalls = Lists.newArrayList("createNewModelObject", "validateOnSave", "saveTimeseriesComponentModels",
        "leaveEditMode");
    List<String> expectedCmCalls = Lists.newArrayList("setEntity", "update", "configure", "setEntity"); // first "setEntity" is from setUpMemBean

    assertEquals(expectedFrontendServiceCalls, timeseriesCMBaseFrontendService.calls);
    assertEquals(expectedCmCalls, memBean.getComponentModel().calls);
    EasyMock.verify(serviceMock, timeseriesServiceMock);
  }

  /**
   * @return A Mock for {@link BuildingBlockService} expecting only the saveOrUpdate call which should happen
   *         when a component model for a newly created entity is saved.
   */
  private BuildingBlockService<BuildingBlock, Integer> getBbServiceSaveNewMock() {
    @SuppressWarnings("unchecked")
    BuildingBlockService<BuildingBlock, Integer> serviceMock = EasyMock.createMock(BuildingBlockService.class);
    EasyMock.expect(serviceMock.saveOrUpdate(testBB)).andReturn(testBB);
    EasyMock.replay(serviceMock);
    return serviceMock;
  }

  /**
   * @return A memory bean with a building block component model for {@link #testBB}.
   */
  private CanHaveTimeseriesMemBeanMockImpl setUpMemBean() {
    CanHaveTimeseriesMemBeanMockImpl memBean = new CanHaveTimeseriesMemBeanMockImpl();
    BBComponentModelMockImpl bbCM = new BBComponentModelMockImpl();
    bbCM.setEntity(testBB);
    memBean.setComponentModel(bbCM);
    return memBean;
  }

  /**
   * Creates a mock for TimeseriesService which expects the calls made in
   * method "initializeTimeseriesComponentModels" of {@link TimeseriesCMBaseFrontendService}.
   */
  private TimeseriesService getTimeseriesServiceMockForInitializingTests() {
    TimeseriesService timeseriesServiceMock = EasyMock.createMock(TimeseriesService.class);
    EasyMock.expect(timeseriesServiceMock.loadTimeseriesByBuildingBlockAndAttributeType(testBB, enumAT)).andReturn(null);
    EasyMock.expect(timeseriesServiceMock.loadTimeseriesByBuildingBlockAndAttributeType(testBB, numAT)).andReturn(null);
    EasyMock.replay(timeseriesServiceMock);
    return timeseriesServiceMock;
  }

  /**
   * Creates a mock for TimeseriesService which expects the calls made in
   * method "saveTimeseriesComponentModels" of {@link TimeseriesCMBaseFrontendService}.
   */
  private TimeseriesService getTimeseriesServiceMockForSavingTests() {
    TimeseriesService timeseriesServiceMock = EasyMock.createMock(TimeseriesService.class);

    EasyMock.expect(timeseriesServiceMock.loadTimeseriesByBuildingBlockAndAttributeType(testBB, enumAT)).andReturn(null);
    EasyMock.expect(timeseriesServiceMock.updateBuildingBlockAttribute(EasyMock.eq(enumTimeseries), EasyMock.eq(testBB))).andReturn(testBB);
    EasyMock.expect(timeseriesServiceMock.saveOrUpdateWithoutBbUpdate(EasyMock.eq(enumTimeseries))).andReturn(null); // we can return null here since the return value is not used

    EasyMock.expect(timeseriesServiceMock.loadTimeseriesByBuildingBlockAndAttributeType(testBB, numAT)).andReturn(numTimeseries);
    EasyMock.expect(timeseriesServiceMock.updateBuildingBlockAttribute(numTimeseries, testBB)).andReturn(testBB);
    timeseriesServiceMock.deleteTimeseries(numTimeseries);
    EasyMock.expectLastCall().once();

    EasyMock.replay(timeseriesServiceMock);
    return timeseriesServiceMock;
  }

  private class TimeseriesCMBaseFrontendServiceMockImpl extends TimeseriesCMBaseFrontendService<BuildingBlock, CanHaveTimeseriesMemBeanMockImpl> {
    private List<String>                                 calls = Lists.newArrayList();
    private BuildingBlockService<BuildingBlock, Integer> bbService;

    @Override
    public BuildingBlockService<BuildingBlock, Integer> getService() {
      return bbService;
    }

    public void setService(BuildingBlockService<BuildingBlock, Integer> service) {
      this.bbService = service;
    }

    @Override
    protected BuildingBlock getModelObjectById(Integer id) {
      calls.add("getModelObjectById(" + id + ")");
      return testBB;
    }

    @Override
    protected BuildingBlock createNewModelObject() {
      calls.add("createNewModelObject");
      return testBB;
    }

    @Override
    protected CanHaveTimeseriesMemBeanMockImpl createNewMemBean() {
      calls.add("createNewMemBean");
      return new CanHaveTimeseriesMemBeanMockImpl();
    }

    @Override
    protected BBComponentModelMockImpl createComponentModel(CanHaveTimeseriesMemBeanMockImpl memBean, ComponentMode componentMode) {
      calls.add("createComponentModel(" + componentMode.name() + ")");
      BBComponentModelMockImpl cm = new BBComponentModelMockImpl();
      memBean.setComponentModel(cm);
      return cm;
    }

    @Override
    protected void prepareBuildingBlockForCopy(BuildingBlock buildingBlock) {
      calls.add("prepareBuildingBlockForCopy");
    }

    @Override
    protected void initializeTimeseriesComponentModels(BuildingBlock bb, CanHaveTimeseriesMemBeanMockImpl memBean, ComponentMode componentMode) {
      calls.add("initializeTimeseriesComponentModels");
      super.initializeTimeseriesComponentModels(bb, memBean, componentMode);
    }

    @Override
    protected void saveTimeseriesComponentModels(BuildingBlock bb, CanHaveTimeseriesMemBeanMockImpl memBean) {
      calls.add("saveTimeseriesComponentModels");
      super.saveTimeseriesComponentModels(bb, memBean);
    }

    @Override
    public void enterEditMode(FlowExecutionContext flowContext) {
      calls.add("enterEditMode");
    }

    @Override
    public void leaveEditMode(FlowExecutionContext flowContext) {
      calls.add("leaveEditMode");
    }

    public <L extends Entity> void validateOnSave(L newEntity, BuildingBlockService<BuildingBlock, Integer> service) {
      calls.add("validateOnSave");
    }

    @Override
    protected String getFlowId() {
      return "test flow";
    }

  }

  @SuppressWarnings("serial")
  private class BBComponentModelMockImpl extends BuildingBlockComponentModel<BuildingBlock> {
    private List<String> calls = Lists.newArrayList();

    public BBComponentModelMockImpl() {
      super(null);
    }

    @Override
    public void initializeFrom(BuildingBlock source) {
      calls.add("initializeFrom");
    }

    @Override
    public void update() {
      calls.add("update");
    }

    @Override
    public void configure(BuildingBlock target) {
      calls.add("configure");
    }

    public void validate(Errors errors) {
      calls.add("validate");
    }

    @Override
    public void setEntity(BuildingBlock entity) {
      calls.add("setEntity");
      super.setEntity(entity);
    }

    @Override
    public BuildingBlock getEntity() {
      calls.add("getEntity");
      return super.getEntity();
    }

    @Override
    public Type<? extends BuildingBlock> getManagedType() {
      return null;
    }
  }

  @SuppressWarnings("serial")
  private class CanHaveTimeseriesMemBeanMockImpl extends CanHaveTimeseriesBaseMemBean<BuildingBlock, BBComponentModelMockImpl> {
    private List<String>             calls = Lists.newArrayList();
    private BBComponentModelMockImpl cm;

    public BBComponentModelMockImpl getComponentModel() {
      calls.add("getComponentModel");
      return cm;
    }

    public void setComponentModel(BBComponentModelMockImpl componentModel) {
      calls.add("setComponentModel");
      this.cm = componentModel;
    }

  }
}
