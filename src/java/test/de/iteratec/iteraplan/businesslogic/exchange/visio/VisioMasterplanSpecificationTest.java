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
package de.iteratec.iteraplan.businesslogic.exchange.visio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagram;
import de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.MasterplanDiagramCreator;
import de.iteratec.iteraplan.businesslogic.exchange.svg.DataCreator;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.TimelineFeature;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.DateIntervalService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.visio.model.Document;


@RunWith(SpringJUnit4ClassRunner.class)
@IfProfileValue(name = "junit.build", value = "nightly.only")
public class VisioMasterplanSpecificationTest extends BaseTransactionalTestSupport {

  private static final List<String>   ALL_DATE_INTERVALS  = Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION,
                                                              DataCreator.DI_INTRO_TO_DECISION);

  private static final List<String>   ALL_ATTRIBUTE_NAMES = Lists
                                                              .newArrayList(DataCreator.AT_COSTS, DataCreator.AT_DECISION_DATE,
                                                                  DataCreator.AT_EVALUATION_DATE, DataCreator.AT_HEALTH,
                                                                  DataCreator.AT_INTRODUCTION_DATE);

  private static final String         TCR_SUCCESSORS_KEY  = "graphicalReport.technicalComponentRelease.successors";
  private static final String         ISR_USED_ISR_KEY    = "graphicalReport.informationSystemRelease.baseComponents";

  private static final String         DIR_BUILD           = "build";
  private static final String         DIR_GENERATED       = "generated";
  private static final String         DIR_JUNITOUT        = "junitout";
  private static final String         RESULTS_PATH        = DIR_BUILD + File.separator + DIR_GENERATED + File.separator + DIR_JUNITOUT;
  private static final List<String>   availableColors     = Lists.newArrayList("AFCEA8", "F6DF95", "D79DAD", "88AED9", "ACA1C8", "D3DD93", "E8BB9E");

  private static final String         TIMESPAN_BEGIN      = "15.12.2010";
  private static final String         TIMESPAN_END        = "01.06.2014";

  @Autowired
  private AttributeTypeService        attributeTypeService;
  @Autowired
  private AttributeValueService       attributeValueService;
  @Autowired
  private BuildingBlockServiceLocator buildingBlockServiceLocator;
  @Autowired
  private DateIntervalService         dateIntervalService;
  @Autowired
  private TestDataHelper2             testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    (new DataCreator(testDataHelper)).createData();
    createOutputDir();
  }

  @After
  public void onTearDown() {
    super.onTearDown();
    UserContext.detachCurrentUserContext();
  }

  @Test
  public void testISRMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();
    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_INFORMATIONSYSTEMRELEASE, false, DataCreator.AT_HEALTH, false, false,
        new ArrayList<String>(), Lists.newArrayList(DataCreator.AT_COSTS));

    writeDiagramToFile(masterplanOptions, getAllInformationSystems(), "masterplan_1level_ISR");
  }

  @Test
  public void testTCRMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();
    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_TECHNICALCOMPONENTRELEASE, false, DataCreator.AT_HEALTH, false, false,
        new ArrayList<String>(), Lists.newArrayList(DataCreator.AT_COSTS));

    writeDiagramToFile(masterplanOptions, getAllTechinicalComponents(), "masterplan_1level_TCR");
  }

  @Test
  public void testProjectMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();
    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_PROJECT, false, DataCreator.AT_HEALTH, false, false, new ArrayList<String>(),
        Lists.newArrayList(DataCreator.AT_COSTS));

    writeDiagramToFile(masterplanOptions, getAllProjects(), "masterplan_1level_PROJ");
  }

  @Test
  public void testProjectIsrMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_PROJECT, false, DataCreator.AT_HEALTH, false, false, new ArrayList<String>(),
        Lists.newArrayList(DataCreator.AT_COSTS));

    configureLevelOptions(masterplanOptions, 1, Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.BB_INFORMATIONSYSTEMRELEASE, false,
        DataCreator.AT_COSTS, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION),
        Lists.newArrayList(DataCreator.AT_HEALTH));

    writeDiagramToFile(masterplanOptions, getAllProjects(), "masterplan_2level_PROJ_ISR");
  }

  @Test
  public void testIsrProjectMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_INFORMATIONSYSTEMRELEASE, false, DataCreator.AT_HEALTH, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_DECISION), Lists.newArrayList(DataCreator.AT_COSTS));

    configureLevelOptions(masterplanOptions, 1, Constants.BB_PROJECT_PLURAL, Constants.BB_PROJECT, false, DataCreator.AT_COSTS, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION), new ArrayList<String>());

    writeDiagramToFile(masterplanOptions, getAllInformationSystems(), "masterplan_2level_ISR_PROJ");
  }

  @Test
  public void testIsrTcrMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_INFORMATIONSYSTEMRELEASE, true, DataCreator.AT_HEALTH, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION),
        Lists.newArrayList(DataCreator.AT_COSTS));

    configureLevelOptions(masterplanOptions, 1, Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, Constants.BB_TECHNICALCOMPONENTRELEASE, false,
        DataCreator.AT_COSTS, false, true,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION), new ArrayList<String>());

    writeDiagramToFile(masterplanOptions, getAllInformationSystems(), "masterplan_2level_ISR_TCR");
  }

  @Test
  public void testTcrIsrMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_TECHNICALCOMPONENTRELEASE, false, DataCreator.AT_HEALTH, false, false,
        new ArrayList<String>(), Lists.newArrayList(DataCreator.AT_COSTS));

    configureLevelOptions(masterplanOptions, 1, Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.BB_INFORMATIONSYSTEMRELEASE, false,
        DataCreator.AT_COSTS, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION),
        Lists.newArrayList(DataCreator.AT_HEALTH));

    writeDiagramToFile(masterplanOptions, getAllTechinicalComponents(), "masterplan_2level_TCR_ISR");
  }

  @Test
  public void testProductIsrTcrMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_PRODUCT, false, DataCreator.AT_HEALTH, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_DECISION), Lists.newArrayList(DataCreator.AT_COSTS));

    configureLevelOptions(masterplanOptions, 1, Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.BB_INFORMATIONSYSTEMRELEASE, true,
        DataCreator.AT_COSTS, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION), new ArrayList<String>());

    configureLevelOptions(masterplanOptions, 2, Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, Constants.BB_TECHNICALCOMPONENTRELEASE, false,
        DataCreator.AT_COSTS, false, true,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION), new ArrayList<String>());

    writeDiagramToFile(masterplanOptions, getAllProducts(), "masterplan_3level_PROD_ISR_TCR");
  }

  @Test
  public void testProjectIsrTcrMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_PROJECT, false, DataCreator.AT_HEALTH, false, false, new ArrayList<String>(),
        Lists.newArrayList(DataCreator.AT_COSTS));

    configureLevelOptions(masterplanOptions, 1, Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.BB_INFORMATIONSYSTEMRELEASE, false,
        DataCreator.AT_COSTS, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION),
        Lists.newArrayList(DataCreator.AT_HEALTH));

    configureLevelOptions(masterplanOptions, 2, Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, Constants.BB_TECHNICALCOMPONENTRELEASE, false,
        DataCreator.AT_COSTS, false, true,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION), new ArrayList<String>());

    writeDiagramToFile(masterplanOptions, getAllProjects(), "masterplan_3level_PROJ_ISR_TCR");
  }

  @Test
  public void testAdTcrClassicMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_ARCHITECTURALDOMAIN, false, null, false, true, new ArrayList<String>(),
        Lists.newArrayList(DataCreator.AT_COSTS));

    configureLevelOptions(masterplanOptions, 1, Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, Constants.BB_TECHNICALCOMPONENTRELEASE, false,
        DataCreator.AT_HEALTH, false, false, new ArrayList<String>(), Lists.newArrayList(DataCreator.AT_COSTS));

    writeDiagramToFile(masterplanOptions, getAllArchitecturalDomains(), "masterplan_2level_AD_TCR_classic");
  }

  @Test
  public void testAdTcrWithDateIntervalsMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_ARCHITECTURALDOMAIN, false, DataCreator.AT_COSTS, false, true,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION),
        Lists.newArrayList(DataCreator.AT_COSTS));

    configureLevelOptions(masterplanOptions, 1, Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, Constants.BB_TECHNICALCOMPONENTRELEASE, false,
        DataCreator.AT_HEALTH, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION, DataCreator.DI_INTRO_TO_DECISION),
        Lists.newArrayList(DataCreator.AT_COSTS));

    writeDiagramToFile(masterplanOptions, getAllArchitecturalDomains(), "masterplan_2level_AD_TCR_withDateIntervals");
  }

  @Test
  public void testIeIsrBuMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_INFRASTRUCTUREELEMENT, false, DataCreator.AT_HEALTH, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_DECISION), Lists.newArrayList(DataCreator.AT_COSTS));

    configureLevelOptions(masterplanOptions, 1, Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.BB_INFORMATIONSYSTEMRELEASE, true,
        DataCreator.AT_COSTS, true, false, Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION),
        Lists.newArrayList(DataCreator.AT_COSTS, DataCreator.AT_HEALTH));

    configureLevelOptions(masterplanOptions, 2, Constants.BB_BUSINESSUNIT_PLURAL, Constants.BB_BUSINESSUNIT, false, DataCreator.AT_COSTS, false,
        true, ALL_DATE_INTERVALS, Lists.newArrayList(DataCreator.AT_HEALTH));

    writeDiagramToFile(masterplanOptions, getAllInfrastructureElements(), "masterplan_3level_IE_ISR_BU");
  }

  @Test
  public void testBfIsrUsedIsrMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_BUSINESSFUNCTION, false, DataCreator.AT_COSTS, false, true, ALL_DATE_INTERVALS,
        new ArrayList<String>());

    configureLevelOptions(masterplanOptions, 1, Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.BB_INFORMATIONSYSTEMRELEASE, true,
        DataCreator.AT_HEALTH, false, false, ALL_DATE_INTERVALS, new ArrayList<String>());

    configureLevelOptions(masterplanOptions, 2, ISR_USED_ISR_KEY, Constants.BB_INFORMATIONSYSTEMRELEASE, true, DataCreator.AT_HEALTH, true, false,
        ALL_DATE_INTERVALS, new ArrayList<String>());

    writeDiagramToFile(masterplanOptions, getAllBusinessFunctions(), "masterplan_3level_BF_ISR_UsedISR");
  }

  @Test
  public void testBfIsrProjectMasterplan() {
    MasterplanOptionsBean masterplanOptions = initOptions();

    configureLevelOptions(masterplanOptions, 0, "", Constants.BB_BUSINESSFUNCTION, false, DataCreator.AT_COSTS, false, true, ALL_DATE_INTERVALS,
        new ArrayList<String>());

    configureLevelOptions(masterplanOptions, 1, Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.BB_INFORMATIONSYSTEMRELEASE, true,
        DataCreator.AT_HEALTH, false, false, ALL_DATE_INTERVALS, new ArrayList<String>());

    configureLevelOptions(masterplanOptions, 2, Constants.BB_PROJECT_PLURAL, Constants.BB_PROJECT, true, DataCreator.AT_HEALTH, true, false,
        ALL_DATE_INTERVALS, new ArrayList<String>());

    writeDiagramToFile(masterplanOptions, getAllBusinessFunctions(), "masterplan_3level_BF_ISR_PROJ");
  }

  @Test
  public void testTcrSuccessroTcrClosureIsr() {
    MasterplanOptionsBean options = initOptions();

    configureLevelOptions(options, 0, "", Constants.BB_TECHNICALCOMPONENTRELEASE, false, DataCreator.AT_COSTS, false, false,
        Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL, DataCreator.DI_EVAL_TO_DECISION), Lists.newArrayList(DataCreator.AT_HEALTH));

    configureLevelOptions(options, 1, TCR_SUCCESSORS_KEY, Constants.BB_TECHNICALCOMPONENTRELEASE, false, DataCreator.AT_HEALTH, true, false,
        ALL_DATE_INTERVALS, Lists.newArrayList(DataCreator.AT_COSTS));

    configureLevelOptions(options, 2, Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.BB_INFORMATIONSYSTEMRELEASE, true,
        DataCreator.AT_HEALTH, true, false, Lists.newArrayList(DataCreator.DI_INTRO_TO_EVAL), Lists.newArrayList(DataCreator.AT_COSTS));

    writeDiagramToFile(options, getAllTechinicalComponents(), "masterplan_3level_TCR_SuccessroTCR_Closure_ISR");
  }

  @Test
  public void testAdOnly() {
    MasterplanOptionsBean options = initOptions();

    configureLevelOptions(options, 0, "", Constants.BB_ARCHITECTURALDOMAIN, false, null, false, true, new ArrayList<String>(),
        new ArrayList<String>());

    writeDiagramToFile(options, getAllArchitecturalDomains(), "masterplan_1level_Ad_only");
  }

  private void configureLevelOptions(MasterplanOptionsBean masterplanOptions, int level, String relationToBb, String selectedBbType,
                                     boolean hierarchicalNames, String colorAtName, boolean closure, boolean disableRuntimePeriod,
                                     List<String> timelineFeatureNames, List<String> customColumnNames) {
    MasterplanRowTypeOptions levelOpts = new MasterplanRowTypeOptions(relationToBb, selectedBbType, level, getAvailableDateIntervals(selectedBbType),
        new ArrayList<BBAttribute>(), getAvailableCustomColumns(ALL_ATTRIBUTE_NAMES));
    levelOpts.setHierarchicalSort(hierarchicalNames);
    levelOpts.setCanBuildClosure(true);
    levelOpts.setBuildClosure(closure);

    if (disableRuntimePeriod) {
      levelOpts.removeTimeLineByPosition(0);
    }

    for (String timelineFeatureName : timelineFeatureNames) {
      levelOpts.addTimeline(getTimelineFeatureByName(levelOpts, timelineFeatureName));
    }

    if (colorAtName != null && !colorAtName.isEmpty()) {
      initColorOptions(levelOpts.getColorOptions(), colorAtName);
    }

    List<ColumnEntry> availableCustomCols = levelOpts.getAvailableCustomColumns();
    List<ColumnEntry> selectedCustomCols = Lists.newArrayList();
    for (String colName : customColumnNames) {
      for (ColumnEntry available : availableCustomCols) {
        if (available.getHead().equals(colName)) {
          selectedCustomCols.add(available);
        }
      }
    }
    levelOpts.setSelectedCustomColumns(selectedCustomCols);

    if (level == 0) {
      masterplanOptions.setLevel0Options(levelOpts);
    }
    else if (level == 1) {
      masterplanOptions.setLevel1Options(levelOpts);
    }
    else {
      masterplanOptions.setLevel2Options(levelOpts);
    }
  }

  private void initColorOptions(ColorDimensionOptionsBean colorOptions, String attributeName) {
    colorOptions.setAvailableColors(availableColors);
    Integer atId = attributeTypeService.getAttributeTypeByName(attributeName).getId();
    colorOptions.setDimensionAttributeId(atId);
    colorOptions.refreshDimensionOptions(attributeValueService.getAllAVStrings(atId));
    colorOptions.switchToGenerationMode();
  }

  private List<BuildingBlock> getAllInformationSystems() {
    List<BuildingBlock> isrs = Lists.newArrayList();
    isrs.addAll(buildingBlockServiceLocator.getIsrService().loadElementList());
    return isrs;
  }

  private List<BuildingBlock> getAllTechinicalComponents() {
    List<BuildingBlock> tcrs = Lists.newArrayList();
    tcrs.addAll(buildingBlockServiceLocator.getTcrService().loadElementList());
    return tcrs;
  }

  private List<BuildingBlock> getAllArchitecturalDomains() {
    List<BuildingBlock> ads = Lists.newArrayList();
    ads.addAll(buildingBlockServiceLocator.getAdService().loadElementList());
    return ads;
  }

  private List<BuildingBlock> getAllProjects() {
    List<BuildingBlock> projects = Lists.newArrayList();
    projects.addAll(buildingBlockServiceLocator.getProjectService().loadElementList());
    return projects;
  }

  private List<BuildingBlock> getAllProducts() {
    List<BuildingBlock> products = Lists.newArrayList();
    products.addAll(buildingBlockServiceLocator.getProductService().loadElementList());
    return products;
  }

  private List<BuildingBlock> getAllInfrastructureElements() {
    List<BuildingBlock> ies = Lists.newArrayList();
    ies.addAll(buildingBlockServiceLocator.getIeService().loadElementList());
    return ies;
  }

  private List<BuildingBlock> getAllBusinessFunctions() {
    List<BuildingBlock> bfs = Lists.newArrayList();
    bfs.addAll(buildingBlockServiceLocator.getBfService().loadElementList());
    return bfs;
  }

  private List<ColumnEntry> getAvailableCustomColumns(List<String> attributeNames) {
    List<ColumnEntry> result = Lists.newArrayList();
    for (String atName : attributeNames) {
      result.add(getColumnEntry(atName));
    }
    return result;
  }

  private ColumnEntry getColumnEntry(String attributeName) {
    return new ColumnEntry(attributeTypeService.getAttributeTypeByName(attributeName).getId().toString(), COLUMN_TYPE.ATTRIBUTE, attributeName);
  }

  private TimelineFeature getTimelineFeatureByName(MasterplanRowTypeOptions options, String timelineFeatureName) {
    for (TimelineFeature feature : options.getAvailableTimeLines()) {
      if (feature.getName().equals(timelineFeatureName)) {
        return feature;
      }
    }
    return null;
  }

  private List<DateInterval> getAvailableDateIntervals(String forBbType) {
    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(forBbType);
    List<AttributeType> aTypes = attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(bbType, false);
    Set<Integer> dateATids = new HashSet<Integer>();
    for (AttributeType dat : aTypes) {
      if (dat instanceof DateAT) {
        dateATids.add(dat.getId());
      }
    }
    return Lists.newArrayList(dateIntervalService.findDateIntervalsByDateATs(dateATids));
  }

  private MasterplanOptionsBean initOptions() {
    MasterplanOptionsBean options = new MasterplanOptionsBean();
    options.setSelectedGraphicFormat(Constants.REPORTS_EXPORT_GRAPHICAL_VISIO);
    options.setStartDateString(getDateString(TIMESPAN_BEGIN));
    options.setEndDateString(getDateString(TIMESPAN_END));

    return options;
  }

  /**
   * Writes a masterplan diagram. The diagram is described through the provided options bean
   * and the list of selected building blocks.
   * @param masterplanOptions
   *    The masterplan options bean which describes the diagram.
   * @param selectedLevel0Bbs
   *    The selected list of level 0 (base) building blocks.
   * @param destinationFile
   *    The name of the name of the file to write to. no context and no extensions - they are added automatically
   * @return
   *    true if the diagram was successfully written. false otherwise
   */
  private boolean writeDiagramToFile(MasterplanOptionsBean masterplanOptions, List<BuildingBlock> selectedLevel0Bbs, String fileName) {
    MasterplanDiagramCreator diagramCreator = new MasterplanDiagramCreator(masterplanOptions, buildingBlockServiceLocator, attributeTypeService,
        selectedLevel0Bbs);
    MasterplanDiagram diagram = diagramCreator.createMasterplanDiagram();

    VisioMasterplanExport visioExport = new VisioMasterplanExport(diagram, masterplanOptions, attributeTypeService, attributeValueService);
    Document document = visioExport.createDiagram();

    File destinationFile = getDestinationFile(fileName, masterplanOptions.getSelectedGraphicFormat());
    try {

      if (Constants.REPORTS_EXPORT_GRAPHICAL_VISIO.equals(masterplanOptions.getSelectedGraphicFormat())) {
        document.save(destinationFile);
      }
      else {
        throw new UnsupportedOperationException();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private File getDestinationFile(String fileName, String exportFormat) {
    String extension = "";
    if (Constants.REPORTS_EXPORT_GRAPHICAL_VISIO.equals(exportFormat)) {
      extension = ".vdx";
    }
    return new File(RESULTS_PATH + File.separator + fileName + extension);
  }

  private static String getDateString(String sourceString) {
    Date date = DataCreator.parseDate(sourceString);
    return DateUtils.formatAsString(date, UserContext.getCurrentLocale());
  }

  private static void createOutputDir() {
    File resultDir = new File(RESULTS_PATH);
    if (!resultDir.exists()) {
      boolean success = resultDir.mkdirs();
      if (!success) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
    }
  }
}
