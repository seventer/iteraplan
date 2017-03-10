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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.MockTestDataFactory;
import de.iteratec.iteraplan.TestAsSuperUser;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.service.FastExportService.DiagramVariant;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;
import de.iteratec.iteraplan.presentation.dialog.FastExport.MasterplanDiagramHelper;
import de.iteratec.iteraplan.presentation.dialog.FastExport.MasterplanDiagramHelperInformationSystem;


public class FastExportServiceImplTest {
  private BusinessProcessService           businessProcessServiceMock;
  private BusinessUnitService              businessUnitServiceMock;
  private InfrastructureElementService     infrastructureElementServiceMock;
  private InformationSystemDomainService   informationSystemDomainServiceMock;
  private InformationSystemReleaseService  informationSystemReleaseServiceMock;
  private ProductService                   productServiceMock;
  private ProjectService                   projectServiceMock;
  private TechnicalComponentReleaseService technicalComponentReleaseServiceMock;

  private final FastExportServiceImpl      fastExportServiceImpl  = new FastExportServiceImpl();
  private final MockTestDataFactory        mtdf                   = MockTestDataFactory.getInstance();

  // for date adjustments
  private static final int                 HALF_YEAR_IN_MONTHS    = 6;
  private static final int                 QUARTER_YEAR_IN_MONTHS = 3;

  @Before
  public void setUp() {
    TestAsSuperUser.createSuperUserInContext();

    SpringGuiFactory.getInstance().setInformationFlowColors(Lists.newArrayList("AFCEA8", "F6DF95", "D79DAD", "88AED9", "ACA1C8", "D3DD93", "E8BB9E"));
    SpringGuiFactory.getInstance().setMasterplanColors(Lists.newArrayList("AFCEA8", "F6DF95", "D79DAD", "88AED9", "ACA1C8", "D3DD93", "E8BB9E"));

    businessProcessServiceMock = EasyMock.createNiceMock(BusinessProcessService.class);
    businessUnitServiceMock = EasyMock.createNiceMock(BusinessUnitService.class);
    infrastructureElementServiceMock = EasyMock.createNiceMock(InfrastructureElementService.class);
    informationSystemDomainServiceMock = EasyMock.createNiceMock(InformationSystemDomainService.class);
    informationSystemReleaseServiceMock = EasyMock.createNiceMock(InformationSystemReleaseService.class);
    productServiceMock = EasyMock.createNiceMock(ProductService.class);
    projectServiceMock = EasyMock.createNiceMock(ProjectService.class);
    technicalComponentReleaseServiceMock = EasyMock.createNiceMock(TechnicalComponentReleaseService.class);

    fastExportServiceImpl.setBusinessProcessService(businessProcessServiceMock);
    fastExportServiceImpl.setBusinessUnitService(businessUnitServiceMock);
    fastExportServiceImpl.setInfrastructureElementService(infrastructureElementServiceMock);
    fastExportServiceImpl.setInformationSystemDomainService(informationSystemDomainServiceMock);
    fastExportServiceImpl.setInformationSystemReleaseService(informationSystemReleaseServiceMock);
    fastExportServiceImpl.setProductService(productServiceMock);
    fastExportServiceImpl.setProjectService(projectServiceMock);
    fastExportServiceImpl.setTechnicalComponentReleaseService(technicalComponentReleaseServiceMock);

  }

  @After
  public void tearDown() {
    TestAsSuperUser.clearUserContext();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#retrieveInformationFlowOptionsForFastExport()}.
   */
  //@Test TODO temporary removed for bug investigation
  public void testRetrieveInformationFlowOptionsForFastExport() {
    InformationFlowOptionsBean res = fastExportServiceImpl.retrieveInformationFlowOptionsForFastExport();

    assertEquals(Integer.valueOf(GraphicalExportBaseOptions.STATUS_SELECTED), res.getColorOptionsBean().getDimensionAttributeId());
    assertEquals(Integer.valueOf(GraphicalExportBaseOptions.NOTHING_SELECTED), res.getLineOptionsBean().getDimensionAttributeId());
    assertEquals(res.getColorOptionsBean().getAvailableColors().subList(0, 4), res.getColorOptionsBean().getSelectedColors());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#retrieveRelatedIsForInformationFlowFastExport(de.iteratec.iteraplan.model.BuildingBlock)}.
   */
  @Test
  public void testRetrieveRelatedIsForInformationFlowFastExport() {
    InformationSystemRelease isrTop = new InformationSystemRelease();
    InformationSystemRelease isr1 = new InformationSystemRelease();
    InformationSystemRelease isr2 = new InformationSystemRelease();
    InformationSystemRelease isr3 = new InformationSystemRelease();
    InformationSystemRelease isr4 = new InformationSystemRelease();
    isrTop.setId(Integer.valueOf(51));
    isr1.setId(Integer.valueOf(63));
    isr2.setId(Integer.valueOf(72));
    isr3.setId(Integer.valueOf(86));
    isr4.setId(Integer.valueOf(34));

    InformationSystemInterface isi1 = new InformationSystemInterface();
    InformationSystemInterface isi2 = new InformationSystemInterface();
    InformationSystemInterface isi3 = new InformationSystemInterface();
    InformationSystemInterface isi4 = new InformationSystemInterface();
    isi1.connect(isrTop, isr1);
    isi2.connect(isrTop, isr2);
    isi3.connect(isr3, isrTop);
    isi4.connect(isr4, isrTop);

    List<InformationSystemRelease> res = fastExportServiceImpl.retrieveRelatedIsForInformationFlowFastExport(isrTop);
    assertSame(Integer.valueOf(res.size()), Integer.valueOf(5));
    assertTrue(res.contains(isr1));
    assertTrue(res.contains(isr2));
    assertTrue(res.contains(isr3));
    assertTrue(res.contains(isr4));
    assertTrue(res.contains(isrTop));

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#retrieveRelatedIsForInformationFlowFastExportFromMultiIs(java.util.Set)}.
   */
  @Test
  public void testRetrieveRelatedIsForInformationFlowFastExportFromMultiIs() {
    InformationSystemRelease isrTop = new InformationSystemRelease();
    InformationSystemRelease isr1 = new InformationSystemRelease();
    InformationSystemRelease isr2 = new InformationSystemRelease();
    InformationSystemRelease isr3 = new InformationSystemRelease();
    InformationSystemRelease isr4 = new InformationSystemRelease();
    isrTop.setId(Integer.valueOf(51));
    isr1.setId(Integer.valueOf(63));
    isr2.setId(Integer.valueOf(72));
    isr3.setId(Integer.valueOf(86));
    isr4.setId(Integer.valueOf(34));

    InformationSystemInterface isi1 = new InformationSystemInterface();
    InformationSystemInterface isi2 = new InformationSystemInterface();
    InformationSystemInterface isi3 = new InformationSystemInterface();
    InformationSystemInterface isi4 = new InformationSystemInterface();
    isi1.connect(isrTop, isr1);
    isi2.connect(isrTop, isr2);
    isi3.connect(isr3, isrTop);
    isi4.connect(isr4, isrTop);

    InformationSystemRelease isrTop2 = new InformationSystemRelease();
    InformationSystemRelease isr12 = new InformationSystemRelease();
    InformationSystemRelease isr22 = new InformationSystemRelease();
    InformationSystemRelease isr32 = new InformationSystemRelease();
    InformationSystemRelease isr42 = new InformationSystemRelease();
    isrTop2.setId(Integer.valueOf(512));
    isr12.setId(Integer.valueOf(632));
    isr22.setId(Integer.valueOf(722));
    isr32.setId(Integer.valueOf(862));
    isr42.setId(Integer.valueOf(342));

    InformationSystemInterface isi12 = new InformationSystemInterface();
    InformationSystemInterface isi22 = new InformationSystemInterface();
    InformationSystemInterface isi32 = new InformationSystemInterface();
    InformationSystemInterface isi42 = new InformationSystemInterface();
    isi12.connect(isrTop2, isr12);
    isi22.connect(isrTop2, isr22);
    isi32.connect(isr32, isrTop2);
    isi42.connect(isr42, isrTop2);

    List<InformationSystemRelease> res = fastExportServiceImpl.retrieveRelatedIsForInformationFlowFastExportFromMultiIs(Sets.newHashSet(isrTop,
        isrTop2));
    assertSame(Integer.valueOf(res.size()), Integer.valueOf(10));
    assertTrue(res.contains(isr1));
    assertTrue(res.contains(isr2));
    assertTrue(res.contains(isr3));
    assertTrue(res.contains(isr4));
    assertTrue(res.contains(isrTop));
    assertTrue(res.contains(isr12));
    assertTrue(res.contains(isr22));
    assertTrue(res.contains(isr32));
    assertTrue(res.contains(isr42));
    assertTrue(res.contains(isrTop2));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#getEncompassingRuntimePeriod(java.util.List)}.
   */
  @Test
  public void testGetEncompassingRuntimePeriodStartEnd() {
    InformationSystemRelease isr1 = mtdf.getInformationSystemReleaseTestData();
    InformationSystemRelease isr2 = mtdf.getInformationSystemReleaseTestData();

    Date now = new Date();
    Date earliest = DateUtils.addMonths(now, -HALF_YEAR_IN_MONTHS);
    Date latest = DateUtils.addMonths(now, HALF_YEAR_IN_MONTHS);
    Date someStart1 = DateUtils.addMonths(now, -QUARTER_YEAR_IN_MONTHS);
    Date someEnd1 = DateUtils.addMonths(now, QUARTER_YEAR_IN_MONTHS);

    RuntimePeriod period1 = new RuntimePeriod(someStart1, latest);
    isr1.setRuntimePeriod(period1);
    RuntimePeriod period2 = new RuntimePeriod(earliest, someEnd1);
    isr2.setRuntimePeriod(period2);

    List<BuildingBlock> bbList = new ArrayList<BuildingBlock>();
    bbList.add(isr2);
    bbList.add(isr1);

    RuntimePeriod res = fastExportServiceImpl.getEncompassingRuntimePeriod(bbList);
    RuntimePeriod exp = new RuntimePeriod(earliest, latest);
    assertEquals(exp, res);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#getEncompassingRuntimePeriod(java.util.List)}.
   */
  @Test
  public void testGetEncompassingRuntimePeriodWithStartNoEnd() {
    InformationSystemRelease isr1 = mtdf.getInformationSystemReleaseTestData();
    InformationSystemRelease isr2 = mtdf.getInformationSystemReleaseTestData();

    Date now = new Date();
    Date earliest = DateUtils.addMonths(now, -HALF_YEAR_IN_MONTHS);
    Date someStart1 = DateUtils.addMonths(now, -QUARTER_YEAR_IN_MONTHS);

    RuntimePeriod period1 = new RuntimePeriod(someStart1, null);
    isr1.setRuntimePeriod(period1);
    RuntimePeriod period2 = new RuntimePeriod(earliest, null);
    isr2.setRuntimePeriod(period2);

    Calendar yearAfterEarliest = Calendar.getInstance();
    yearAfterEarliest.setTime(earliest);
    yearAfterEarliest.roll(Calendar.YEAR, 1);

    List<BuildingBlock> bbList = new ArrayList<BuildingBlock>();
    bbList.add(isr2);
    bbList.add(isr1);

    RuntimePeriod res = fastExportServiceImpl.getEncompassingRuntimePeriod(bbList);
    RuntimePeriod exp = new RuntimePeriod(earliest, yearAfterEarliest.getTime());
    assertEquals(exp, res);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#getEncompassingRuntimePeriod(java.util.List)}.
   */
  @Test
  public void testGetEncompassingRuntimePeriodNoStartWithEnd() {
    InformationSystemRelease isr1 = mtdf.getInformationSystemReleaseTestData();
    InformationSystemRelease isr2 = mtdf.getInformationSystemReleaseTestData();

    Date now = new Date();
    Date latest = DateUtils.addMonths(now, HALF_YEAR_IN_MONTHS);
    Date someEnd1 = DateUtils.addMonths(now, QUARTER_YEAR_IN_MONTHS);

    RuntimePeriod period1 = new RuntimePeriod(null, latest);
    isr1.setRuntimePeriod(period1);
    RuntimePeriod period2 = new RuntimePeriod(null, someEnd1);
    isr2.setRuntimePeriod(period2);

    Calendar yearBeforeLatest = Calendar.getInstance();
    yearBeforeLatest.setTime(latest);
    yearBeforeLatest.roll(Calendar.YEAR, -1);

    List<BuildingBlock> bbList = new ArrayList<BuildingBlock>();
    bbList.add(isr2);
    bbList.add(isr1);

    RuntimePeriod res = fastExportServiceImpl.getEncompassingRuntimePeriod(bbList);
    RuntimePeriod exp = new RuntimePeriod(yearBeforeLatest.getTime(), latest);
    assertEquals(exp, res);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#getEncompassingRuntimePeriod(java.util.List)}.
   */
  @Test
  public void testGetEncompassingRuntimePeriodNoStartNoEnd() {
    InformationSystemRelease isr1 = mtdf.getInformationSystemReleaseTestData();
    InformationSystemRelease isr2 = mtdf.getInformationSystemReleaseTestData();

    RuntimePeriod period1 = new RuntimePeriod();
    isr1.setRuntimePeriod(period1);
    RuntimePeriod period2 = new RuntimePeriod();
    isr2.setRuntimePeriod(period2);

    // pretty much copy-paste from original method
    Calendar calendar = Calendar.getInstance();
    calendar.roll(Calendar.YEAR, false);
    calendar.roll(Calendar.MONTH, HALF_YEAR_IN_MONTHS);
    Date start = calendar.getTime();
    calendar.roll(Calendar.YEAR, 1);
    Date end = calendar.getTime();

    List<BuildingBlock> bbList = new ArrayList<BuildingBlock>();
    bbList.add(isr2);
    bbList.add(isr1);

    RuntimePeriod res = fastExportServiceImpl.getEncompassingRuntimePeriod(bbList);
    RuntimePeriod exp = new RuntimePeriod(start, end);
    assertEquals(exp, res);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#initMasterplanOptionsForFastExport(java.lang.String)}.
   */
  @Test
  public void testInitMasterplanOptionsForFastExport() {
    mtdf.createUserContext();
    String url = "myUrl";
    MasterplanOptionsBean res = fastExportServiceImpl.initMasterplanOptionsForFastExport(url);

    assertEquals(Integer.valueOf(GraphicalExportBaseOptions.NOTHING_SELECTED), res.getColorOptionsBean().getDimensionAttributeId());
    assertEquals(url, res.getServerUrl());
    assertTrue(res.isNakedExport());
    assertEquals(Lists.newArrayList(), res.getColorOptionsBean().getSelectedColors());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#configureMasterplanOptionsForFastExport(de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean, de.iteratec.iteraplan.model.RuntimePeriod)}.
   */
  @Test
  public void testConfigureMasterplanOptionsForFastExport() {
    MasterplanOptionsBean bean = new MasterplanOptionsBean();
    bean.setLevel0Options(new MasterplanRowTypeOptions("", TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue(), 0, new ArrayList<DateInterval>(),
        new ArrayList<BBAttribute>(), new ArrayList<ColumnEntry>()));
    bean.getLevel0Options().setHierarchicalSort(true);

    Date now = new Date();
    Date start = DateUtils.addMonths(now, -HALF_YEAR_IN_MONTHS);
    Date end = DateUtils.addMonths(now, HALF_YEAR_IN_MONTHS);
    RuntimePeriod period = new RuntimePeriod(start, end);

    fastExportServiceImpl.configureMasterplanOptionsForFastExport(bean, period);
    assertTrue(bean.getLevel0Options().isHierarchicalSort());
    assertEquals(DateFormatUtils.format(start, "MM/dd/yyyy"), bean.getStartDateString());
    assertEquals(DateFormatUtils.format(end, "MM/dd/yyyy"), bean.getEndDateString());

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#retrieveBuildingBlockListForMasterplanFastExport(de.iteratec.iteraplan.model.BuildingBlock, de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean, java.lang.String)}.
   */
  @SuppressWarnings("deprecation")
  //@Test TODO temporary removed for bug investigation
  public void testRetrieveBuildingBlockListForMasterplanFastExport() {
    BuildingBlockType bbt = new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    BuildingBlock bb = mtdf.getInformationSystemReleaseTestData();
    bb.setBuildingBlockType(bbt);
    MasterplanOptionsBean bean = new MasterplanOptionsBean();
    String variant = "Hierarchy";
    DiagramVariant diagVar = DiagramVariant.MASTERPLAN_HIERARCHY;

    MasterplanDiagramHelper helper = new MasterplanDiagramHelperInformationSystem();
    assertEquals(helper.determineResults(bb, diagVar, bean),
        fastExportServiceImpl.retrieveBuildingBlockListForMasterplanFastExport(bb, bean, variant));

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.FastExportServiceImpl#getStartElement(java.lang.Integer, java.lang.String)}.
   */
  @Test
  public void testGetStartElement() {
    BuildingBlock bb;
    String[] vals = { Constants.BB_INFRASTRUCTUREELEMENT_INITIALCAP, Constants.BB_INFORMATIONSYSTEMDOMAIN_INITIALCAP,
        Constants.BB_INFORMATIONSYSTEMRELEASE_INITIALCAP, Constants.BB_PROJECT_INITIALCAP, Constants.BB_PRODUCT_INITIALCAP,
        Constants.BB_BUSINESSPROCESS_INITIALCAP, Constants.BB_BUSINESSUNIT_INITIALCAP, Constants.BB_TECHNICALCOMPONENTRELEASE_INITIALCAP };

    BuildingBlock[] blocks = { new InfrastructureElement(), new InformationSystemDomain(), new InformationSystemRelease(), new Project(),
        new Product(), new BusinessProcess(), new BusinessUnit(), new TechnicalComponentRelease() };

    for (int i = 0; i < vals.length; i++) {
      blocks[i].setId(Integer.valueOf(i));
      setServiceExpect(TypeOfBuildingBlock.fromInitialCapString(vals[i]), blocks);
      bb = fastExportServiceImpl.getStartElement(Integer.valueOf(i), vals[i]);
      assertEquals(blocks[i], bb);
    }

  }

  /**
   * Sets the expectations and returns for the right service mock.
   * 
   */
  private void setServiceExpect(TypeOfBuildingBlock tob, BuildingBlock[] blocks) {
    EasyMock.reset(infrastructureElementServiceMock, informationSystemDomainServiceMock, informationSystemReleaseServiceMock, productServiceMock,
        projectServiceMock, businessProcessServiceMock, businessUnitServiceMock, technicalComponentReleaseServiceMock);
    switch (tob) {
      case INFRASTRUCTUREELEMENT:
        EasyMock.expect(infrastructureElementServiceMock.loadObjectById(Integer.valueOf(0))).andReturn((InfrastructureElement) blocks[0]);
        break;
      case INFORMATIONSYSTEMDOMAIN:
        EasyMock.expect(informationSystemDomainServiceMock.loadObjectById(Integer.valueOf(1))).andReturn((InformationSystemDomain) blocks[1]);
        break;
      case INFORMATIONSYSTEMRELEASE:
        EasyMock.expect(informationSystemReleaseServiceMock.loadObjectById(Integer.valueOf(2))).andReturn((InformationSystemRelease) blocks[2]);
        break;
      case PROJECT:
        EasyMock.expect(projectServiceMock.loadObjectById(Integer.valueOf(3))).andReturn((Project) blocks[3]);
        break;
      case PRODUCT:
        EasyMock.expect(productServiceMock.loadObjectById(Integer.valueOf(4))).andReturn((Product) blocks[4]);
        break;
      case BUSINESSPROCESS:
        EasyMock.expect(businessProcessServiceMock.loadObjectById(Integer.valueOf(5))).andReturn((BusinessProcess) blocks[5]);
        break;
      case BUSINESSUNIT:
        EasyMock.expect(businessUnitServiceMock.loadObjectById(Integer.valueOf(6))).andReturn((BusinessUnit) blocks[6]);
        break;
      case TECHNICALCOMPONENTRELEASE:
        EasyMock.expect(technicalComponentReleaseServiceMock.loadObjectById(Integer.valueOf(7))).andReturn((TechnicalComponentRelease) blocks[7]);
        break;
      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
    EasyMock.replay(infrastructureElementServiceMock, informationSystemDomainServiceMock, informationSystemReleaseServiceMock, productServiceMock,
        projectServiceMock, businessProcessServiceMock, businessUnitServiceMock, technicalComponentReleaseServiceMock);
  }
}
