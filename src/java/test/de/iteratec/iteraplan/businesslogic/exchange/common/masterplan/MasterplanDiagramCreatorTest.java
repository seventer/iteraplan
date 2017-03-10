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
package de.iteratec.iteraplan.businesslogic.exchange.common.masterplan;

import org.junit.Test;


//TODO reenable test for the new masterplan creator
public class MasterplanDiagramCreatorTest //extends BaseTransactionalTestSupport {
{
  @Test
  public void emptyTest() {
    //Nothing here
  }

  //private MasterplanOptionsBean                 masterplanOptions;
  //
  //  private final List<InformationSystemRelease>  isrList       = Lists.newArrayList();
  //  private final List<TechnicalComponentRelease> tcrList       = Lists.newArrayList();
  //  private final List<Project>                   projList      = Lists.newArrayList();
  //  private final List<AttributeValue>            enumAVs       = Lists.newArrayList();
  //  private final List<AttributeValue>            numberAVs     = Lists.newArrayList();
  //
  //  // string literals for testing, @see AvoidDuplicateLiterals in FindBugs
  //  private static final String                   ENUM_AT       = "EnumAT";
  //  private static final String                   NUMBER_AT     = "NumberAT";
  //  private static final String                   DATE_06062003 = "06.06.2003";
  //  private static final String                   DATE_06062004 = "06.06.2004";
  //  private static final String                   DATE_06062005 = "06.06.2005";
  //  private static final String                   DATE_06062006 = "06.06.2006";
  //  private static final String                   DATE_06062007 = "06.06.2007";
  //  private static final String                   DATE_06012018 = "06.01.2018";
  //  private static final String                   DATE_06012019 = "06.01.2019";
  //  private static final String                   DATE_06012020 = "06.01.2020";
  //  private static final String                   DATE_06012021 = "06.01.2021";
  //  private static final String                   DATE_06012022 = "06.01.2022";
  //
  //  @Autowired
  //  private GeneralBuildingBlockService           bbService;
  //  @Autowired
  //  private AttributeTypeService                  atService;
  //  @Autowired
  //  private ProjectDAO                            projectDAO;
  //  @Autowired
  //  private BuildingBlockTypeService              bbtService;
  //  @Autowired
  //  private InitFormHelperService                 initFormHelperService;
  //  @Autowired
  //  private BuildingBlockServiceLocator           buildingBlockServiceLocator;
  //  @Autowired
  //  private TestDataHelper2                       testDataHelper;
  //
  //  @Before
  //  public void setUp() {
  //    super.setUp();
  //    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  //
  //    UserContext.getCurrentUserContext().setLocale(Locale.GERMAN);
  //    masterplanOptions = new MasterplanOptionsBean();
  //    createBuildingBlocks();
  //    createAttributes();
  //    setAssociations();
  //    assignAttributeValues();
  //
  //    commit();
  //  }
  //
  //  /**
  //   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagramCreator#createMasterplanDiagram()}.
  //   */
  //  @Test
  //  public final void testCreateMasterplanDiagramIS() {
  //    ManageReportMemoryBean memBean = initFormHelperService.getInitializedReportMemBean(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue());
  //    memBean.setGraphicalOptions(masterplanOptions);
  //
  //    masterplanOptions.setSortMethod("hierarchical");
  //    masterplanOptions.setStartDateString("10.12.2005");
  //    masterplanOptions.setEndDateString("1.1.2020");
  //    masterplanOptions.setSelectedRelatedType("graphicalReport.informationSystemRelease.predecessors");
  //    //TODO remake with row types 
  //    //   List<ColumnEntry> availableColumns = memBean.getViewConfiguration().getAvailableColumns();
  //    //    masterplanOptions.setAvailableCustomColumns(availableColumns);
  //    //    masterplanOptions.setSelectedCustomColumns(Lists.newArrayList(availableColumns.subList(availableColumns.size() - 2, availableColumns.size())));
  //
  //    beginTransaction();
  //
  //    // to have one element (first in the isrList) which isn't selected main building block, but appears in the related elements
  //    List<InformationSystemRelease> mainBbs = isrList.subList(1, isrList.size());
  //
  //    MasterplanDiagramCreator creator = new MasterplanDiagramCreator(buildingBlockServiceLocator, bbService, masterplanOptions, mainBbs, atService);
  //    assertNotNull(creator);
  //    MasterplanDiagram diagram = creator.createMasterplanDiagram();
  //    assertNotNull(diagram);
  //
  //    assertEquals("Header:", "Informationssystem", diagram.getBbHeader());
  //    assertEquals("Related Types Header:", "Vorgänger", diagram.getRelatedTypeHeader());
  //    assertEquals("Timespan:", 170, diagram.getTimenspanLength());
  //    assertEquals("Content Row Number:", 14, diagram.getMasterplanContentHeight());
  //    assertEquals("Custom Column Headers:", Lists.newArrayList(ENUM_AT, NUMBER_AT), diagram.getCustomHeaders());
  //
  //    assertDiagramRowsCaseIS(diagram);
  //  }
  //
  //  private void assertDiagramRowsCaseIS(MasterplanDiagram diagram) {
  //    String nameIs1 = "IS 1 # 1.0";
  //    String nameIs2 = "IS 2 # 1.0";
  //    String nameIs3 = "IS 3 # 1.0";
  //    List<String> expectedRowNames = Lists.newArrayList(nameIs2, nameIs1, nameIs3, nameIs1, nameIs2, "IS 4 # 1.0", nameIs1, nameIs2, nameIs3,
  //        "IS 5 # 1.0", nameIs1, nameIs2, nameIs3, "IS 4 # 1.0");
  //    boolean[] expectedSecondOrderRow = { false, true, false, true, true, false, true, true, true, false, true, true, true, true };
  //
  //    String av12345 = "av1, av2, av3, av4, av5";
  //    String av2345 = "av2, av3, av4, av5";
  //    List<String> expectedFirstCustomColumn = Lists.newArrayList(av2345, av12345, "av3, av4, av5", av12345, av2345, "av4, av5", av12345, av2345,
  //        "av3, av4, av5", "av5", av12345, av2345, "av3, av4, av5", "av4, av5");
  //
  //    String lit100 = "1,00";
  //    String lit1600 = "16,00";
  //    List<String> expectedSecondCustomColumn = Lists.newArrayList(lit1600, lit100, "49,00", lit100, lit1600, "100,00", lit100, lit1600, "49,00",
  //        "9,00", lit100, lit1600, "49,00", "100,00");
  //    List<String> expectedStartDates = Lists.newArrayList(DATE_06062004, DATE_06062003, DATE_06062005, DATE_06062003, DATE_06062004, DATE_06062006,
  //        DATE_06062003, DATE_06062004, DATE_06062005, DATE_06062007, DATE_06062003, DATE_06062004, DATE_06062005, DATE_06062006);
  //    List<String> expectedEndDates = Lists.newArrayList(DATE_06012019, DATE_06012018, DATE_06012020, DATE_06012018, DATE_06012019, DATE_06012021,
  //        DATE_06012018, DATE_06012019, DATE_06012020, DATE_06012022, DATE_06012018, DATE_06012019, DATE_06012020, DATE_06012021);
  //
  //    for (int i = 0; i < diagram.getRows().size(); i++) {
  //      MasterplanDiagramRow row = diagram.getRows().get(i);
  //      assertEquals("Row Name:", expectedRowNames.get(i), row.getRowName());
  //      assertEquals("second order row expected: " + expectedSecondOrderRow[i], expectedSecondOrderRow[i], row.isSecondOrderRow());
  //      assertEquals("Start Date:", expectedStartDates.get(i), row.getBeginDate());
  //      assertEquals("End Date:", expectedEndDates.get(i), row.getEndDate());
  //
  //      Map<String, String> expectedCustColumns = Maps.newHashMap();
  //      expectedCustColumns.put(ENUM_AT, expectedFirstCustomColumn.get(i));
  //      expectedCustColumns.put(NUMBER_AT, expectedSecondCustomColumn.get(i));
  //      assertEquals("Custom Column Values:", expectedCustColumns, row.getCustomColumnContents());
  //    }
  //  }
  //
  //  /**
  //   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagramCreator#createMasterplanDiagram()}.
  //   */
  //  @Test
  //  public final void testCreateMasterplanDiagramTC() {
  //    ManageReportMemoryBean memBean = initFormHelperService.getInitializedReportMemBean(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE.getValue());
  //    memBean.setGraphicalOptions(masterplanOptions);
  //
  //    masterplanOptions.setSelectedBbType(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL);
  //    masterplanOptions.setSortMethod("hierarchical");
  //    masterplanOptions.setStartDateString("10.12.2005");
  //    masterplanOptions.setEndDateString("1.1.2020");
  //    masterplanOptions.setSelectedRelatedType(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL);
  //    //TODO remake with row types
  //    //    List<ColumnEntry> availableColumns = memBean.getViewConfiguration().getAvailableColumns();
  //    //    masterplanOptions.setAvailableCustomColumns(availableColumns);
  //    //    masterplanOptions.setSelectedCustomColumns(Lists.newArrayList(availableColumns.subList(availableColumns.size() - 2, availableColumns.size())));
  //
  //    beginTransaction();
  //    MasterplanDiagramCreator creator = new MasterplanDiagramCreator(buildingBlockServiceLocator, bbService, masterplanOptions, tcrList, atService);
  //    assertNotNull(creator);
  //    MasterplanDiagram diagram = creator.createMasterplanDiagram();
  //    assertNotNull(diagram);
  //
  //    assertEquals("Header:", "Technischer Baustein", diagram.getBbHeader());
  //    assertEquals("Related Types Header:", "Informationssysteme", diagram.getRelatedTypeHeader());
  //    assertEquals("Timespan:", 170, diagram.getTimenspanLength());
  //    assertEquals("Content Row Number:", 10, diagram.getMasterplanContentHeight());
  //    assertEquals("Custom Column Headers:", Lists.newArrayList(ENUM_AT, NUMBER_AT), diagram.getCustomHeaders());
  //
  //    assertDiagramRowsCaseTC(diagram);
  //  }
  //
  //  private void assertDiagramRowsCaseTC(MasterplanDiagram diagram) {
  //    List<String> expectedRowNames = Lists.newArrayList("TC 1 # 1.0", "IS 1 # 1.0", "TC 2 # 1.0", "IS 2 # 1.0", "TC 3 # 1.0", "IS 3 # 1.0",
  //        "TC 4 # 1.0", "IS 4 # 1.0", "TC 5 # 1.0", "IS 5 # 1.0");
  //    boolean[] expectedSecondOrderRow = { false, true, false, true, false, true, false, true, false, true };
  //    List<String> expectedStartDates = Lists.newArrayList(DATE_06062003, DATE_06062003, DATE_06062004, DATE_06062004, DATE_06062005, DATE_06062005,
  //        DATE_06062006, DATE_06062006, DATE_06062007, DATE_06062007);
  //    List<String> expectedEndDates = Lists.newArrayList(DATE_06012018, DATE_06012018, DATE_06012019, DATE_06012019, DATE_06012020, DATE_06012020,
  //        DATE_06012021, DATE_06012021, DATE_06012022, DATE_06012022);
  //
  //    for (int i = 0; i < diagram.getRows().size(); i++) {
  //      MasterplanDiagramRow row = diagram.getRows().get(i);
  //      assertEquals("Row Name:", expectedRowNames.get(i), row.getRowName());
  //      assertEquals("second order row expected: " + expectedSecondOrderRow[i], expectedSecondOrderRow[i], row.isSecondOrderRow());
  //      assertEquals("Start Date:", expectedStartDates.get(i), row.getBeginDate());
  //      assertEquals("End Date:", expectedEndDates.get(i), row.getEndDate());
  //
  //      Map<String, String> expectedCustColumns = Maps.newHashMap();
  //      expectedCustColumns.put(ENUM_AT, "");
  //      expectedCustColumns.put(NUMBER_AT, "");
  //      assertEquals("Custom Column Values:", expectedCustColumns, row.getCustomColumnContents());
  //    }
  //  }
  //
  //  /**
  //   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagramCreator#createMasterplanDiagram()}.
  //   */
  //  @Test
  //  public final void testCreateMasterplanDiagramProj() {
  //    ManageReportMemoryBean memBean = initFormHelperService.getInitializedReportMemBean(TypeOfBuildingBlock.PROJECT.getValue());
  //    memBean.setGraphicalOptions(masterplanOptions);
  //
  //    masterplanOptions.setSelectedBbType(Constants.BB_PROJECT_PLURAL);
  //    masterplanOptions.setSortMethod("hierarchical");
  //    masterplanOptions.setStartDateString("10.12.2005");
  //    masterplanOptions.setEndDateString("1.1.2020");
  //    masterplanOptions.setSelectedRelatedType(Constants.REPORTS_EXPORT_SELECT_RELATION);
  //
  //    beginTransaction();
  //    MasterplanDiagramCreator creator = new MasterplanDiagramCreator(buildingBlockServiceLocator, bbService, masterplanOptions, projList, atService);
  //    assertNotNull(creator);
  //    MasterplanDiagram diagram = creator.createMasterplanDiagram();
  //    assertNotNull(diagram);
  //
  //    assertEquals("Header:", "Projekt", diagram.getBbHeader());
  //    assertNull("Related Types Header:", diagram.getRelatedTypeHeader());
  //    assertEquals("Timespan:", 170, diagram.getTimenspanLength());
  //    assertEquals("Content Row Number:", 5, diagram.getMasterplanContentHeight());
  //    assertEquals("Custom Column Headers:", new ArrayList<String>(), diagram.getCustomHeaders());
  //
  //    assertDiagramRowsCaseProj(diagram);
  //  }
  //
  //  private void assertDiagramRowsCaseProj(MasterplanDiagram diagram) {
  //    List<String> expectedRowNames = Lists.newArrayList("Proj 1", "Proj 2", "Proj 3", "Proj 4", "Proj 5");
  //    List<String> expectedStartDates = Lists.newArrayList(DATE_06062003, DATE_06062004, DATE_06062005, DATE_06062006, DATE_06062007);
  //    List<String> expectedEndDates = Lists.newArrayList(DATE_06012018, DATE_06012019, DATE_06012020, DATE_06012021, DATE_06012022);
  //
  //    for (int i = 0; i < diagram.getRows().size(); i++) {
  //      MasterplanDiagramRow row = diagram.getRows().get(i);
  //      assertEquals("Row Name:", expectedRowNames.get(i), row.getRowName());
  //      assertFalse("second order row expected: false", row.isSecondOrderRow());
  //      assertEquals("Start Date:", expectedStartDates.get(i), row.getBeginDate());
  //      assertEquals("End Date:", expectedEndDates.get(i), row.getEndDate());
  //
  //      Map<String, String> expectedCustColumns = Maps.newHashMap();
  //      assertEquals("Custom Column Values:", expectedCustColumns, row.getCustomColumnContents());
  //    }
  //  }
  //
  //  private void createBuildingBlocks() {
  //    isrList.clear();
  //    for (int i = 1; i <= 5; i++) {
  //      InformationSystem is = testDataHelper.createInformationSystem("IS " + i);
  //      isrList.add(testDataHelper.createInformationSystemRelease(is, "1.0"));
  //    }
  //
  //    tcrList.clear();
  //    for (int i = 1; i <= 5; i++) {
  //      TechnicalComponent tc = testDataHelper.createTechnicalComponent("TC " + i, true, true);
  //      tcrList.add(testDataHelper.createTCRelease(tc, "1.0", true));
  //    }
  //
  //    Project rootProj = projectDAO.getFirstElement();
  //    projList.clear();
  //    for (int i = 1; i <= 5; i++) {
  //      Project proj = testDataHelper.createProject("Proj " + i, "Proj " + i + " Description");
  //      proj.addParent(rootProj);
  //      projList.add(proj);
  //    }
  //  }
  //
  //  private void createAttributes() {
  //    Set<BuildingBlockType> bbtSet = Sets.newHashSet(bbtService.getBuildingBlockTypesEligibleForAttributes());
  //
  //    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("test", "test description");
  //    NumberAT numberAT = testDataHelper.createNumberAttributeType(NUMBER_AT, "Number AT description", atg);
  //    numberAT.setBuildingBlockTypes(bbtSet);
  //    EnumAT enumAT = testDataHelper.createEnumAttributeType(ENUM_AT, "Enum AT description", Boolean.TRUE, atg);
  //    enumAT.setBuildingBlockTypes(bbtSet);
  //
  //    for (int i = 1; i <= 5; i++) {
  //      enumAVs.add(testDataHelper.createEnumAV("av" + i, "enum AV description " + i, enumAT));
  //    }
  //
  //    for (int i = 1; i <= 10; i++) {
  //      numberAVs.add(testDataHelper.createNumberAV(new BigDecimal(i * i), numberAT));
  //    }
  //  }
  //
  //  private void setAssociations() {
  //    int count = 0;
  //    for (InformationSystemRelease isr : isrList) {
  //      for (int i = count + 1; i < isrList.size(); i++) {
  //        isrList.get(i).addPredecessor(isr);
  //      }
  //      isr.addTechnicalComponentRelease(tcrList.get(count % tcrList.size()));
  //      isr.addProject(projList.get(count % tcrList.size()));
  //      count++;
  //    }
  //  }
  //
  //  private void assignAttributeValues() {
  //    int shift = calculateShift(numberAVs);
  //
  //    Calendar startDate = Calendar.getInstance(Locale.GERMANY);
  //    Calendar endDate = Calendar.getInstance(Locale.GERMANY);
  //    startDate.set(2003, 5, 6);
  //    endDate.set(2018, 0, 6);
  //
  //    int count = 0;
  //    for (InformationSystemRelease isr : isrList) {
  //      for (int i = count; i < enumAVs.size(); i++) {
  //        testDataHelper.createAVA(isr, enumAVs.get(i));
  //      }
  //      testDataHelper.createAVA(isr, numberAVs.get((count * shift) % numberAVs.size()));
  //      isr.setRuntimePeriod(new RuntimePeriod(startDate.getTime(), endDate.getTime()));
  //
  //      startDate.set(Calendar.YEAR, startDate.get(Calendar.YEAR) + 1);
  //      endDate.set(Calendar.YEAR, endDate.get(Calendar.YEAR) + 1);
  //      count++;
  //    }
  //
  //    startDate.set(2003, 5, 6);
  //    endDate.set(2018, 0, 6);
  //    for (TechnicalComponentRelease tcr : tcrList) {
  //      tcr.setRuntimePeriod(new RuntimePeriod(startDate.getTime(), endDate.getTime()));
  //
  //      startDate.set(Calendar.YEAR, startDate.get(Calendar.YEAR) + 1);
  //      endDate.set(Calendar.YEAR, endDate.get(Calendar.YEAR) + 1);
  //    }
  //
  //    startDate.set(2003, 5, 6);
  //    endDate.set(2018, 0, 6);
  //    for (Project proj : projList) {
  //      proj.setRuntimePeriod(new RuntimePeriod(startDate.getTime(), endDate.getTime()));
  //
  //      startDate.set(Calendar.YEAR, startDate.get(Calendar.YEAR) + 1);
  //      endDate.set(Calendar.YEAR, endDate.get(Calendar.YEAR) + 1);
  //    }
  //  }
  //
  //  private int calculateShift(List<?> listToShift) {
  //    int[] possibleShifts = { 2, 3, 5, 7 };
  //    for (int shift : possibleShifts) {
  //      if (listToShift.size() % shift != 0) {
  //        return shift;
  //      }
  //    }
  //    return 11;
  //  }
}
