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

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.MockTestDataFactory;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.DiagramKeyType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.DiagramType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.PortfolioOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeMu;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.dto.LandscapeDiagramConfigDTO;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.SvgExportException;


/**
 *
 */
public class SvgExportServiceImplTest {

  private static final Logger         LOGGER               = Logger.getIteraplanLogger(SvgExportServiceImplTest.class);

  // services need for mocking
  private AttributeTypeDAO            attributeTypeDAOm;
  private GeneralBuildingBlockService generalBuildingBlockServiceM;
  private AttributeTypeService        attributeTypeServiceM;
  private AttributeValueService       attributeValueServiceM;

  private final SvgExportServiceImpl  svgExportServiceImpl = new SvgExportServiceImpl();
  private final MockTestDataFactory   mtdf                 = MockTestDataFactory.getInstance();
  private File                        outputFile;

  @Before
  public void setUp() {
    // services forwarded in different occasions, set in the test-object and mocked just in case
    GeneralBuildingBlockDAO generalBuildingBlockDAOm;
    BuildingBlockServiceLocator buildingBlockServiceLocatorM;

    attributeTypeDAOm = EasyMock.createNiceMock(AttributeTypeDAO.class);
    generalBuildingBlockServiceM = EasyMock.createNiceMock(GeneralBuildingBlockService.class);
    generalBuildingBlockDAOm = EasyMock.createNiceMock(GeneralBuildingBlockDAO.class);
    attributeTypeServiceM = EasyMock.createNiceMock(AttributeTypeService.class);
    attributeValueServiceM = EasyMock.createNiceMock(AttributeValueService.class);
    buildingBlockServiceLocatorM = EasyMock.createNiceMock(BuildingBlockServiceLocator.class);

    svgExportServiceImpl.setAttributeTypeDAO(attributeTypeDAOm);
    svgExportServiceImpl.setGeneralBuildingBlockService(generalBuildingBlockServiceM);
    svgExportServiceImpl.setGeneralBuildingBlockDAO(generalBuildingBlockDAOm);
    svgExportServiceImpl.setAttributeTypeService(attributeTypeServiceM);
    svgExportServiceImpl.setAttributeValueService(attributeValueServiceM);
    svgExportServiceImpl.setBuildingBlockServiceLocator(buildingBlockServiceLocatorM);

    mtdf.createUserContext();

    try {
      outputFile = File.createTempFile("svg_export", ".svg");
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
    outputFile.deleteOnExit();
  }

  /**
  * Test method for {@link de.iteratec.iteraplan.businesslogic.service.SvgExportServiceImpl#generateSvgInformationFlowExport(java.util.List, boolean, de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean)}.
  */
  @Test(expected = IteraplanBusinessException.class)
  public void testGenerateSvgInformationFlowExportSizeConstraint() {
    @SuppressWarnings("unchecked")
    List<InformationSystemRelease> isrList = EasyMock.createNiceMock(List.class);

    EasyMock.expect(Integer.valueOf(isrList.size())).andReturn(Integer.valueOf(600));
    EasyMock.replay(isrList);

    svgExportServiceImpl.generateSvgInformationFlowExport(isrList, null, null, false, null);

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.SvgExportServiceImpl#generateSvgInformationFlowExport(java.util.List, boolean, de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean)}.
   * 
   * Only tests with ppConnectionMerging set to false, since to small to break;
   */
  @Test
  public void testGenerateSvgInformationFlowExport() {
    List<InformationSystemRelease> isrList = getTestIsrList();

    // prepares the InformationFlowOptionsBean
    InformationFlowOptionsBean infoBean = EasyMock.createNiceMock(InformationFlowOptionsBean.class);
    ColorDimensionOptionsBean colorBean = new ColorDimensionOptionsBean();
    colorBean.setDefaultColor("1A301D");
    LineDimensionOptionsBean lineBean = new LineDimensionOptionsBean();

    // set expectations for various calls 
    EasyMock.expect(infoBean.getLineCaptionSelectedAttributeId()).andReturn(Integer.valueOf(-1)).anyTimes();
    EasyMock.expect(infoBean.getSelectedNodeLayout()).andReturn(Constants.REPORTS_EXPORT_INFORMATIONFLOW_LAYOUT_STANDARD).anyTimes();
    EasyMock.expect(infoBean.getColorOptionsBean()).andReturn(colorBean).anyTimes();
    EasyMock.expect(infoBean.getLineOptionsBean()).andReturn(lineBean).anyTimes();
    EasyMock.expect(infoBean.getSelectionType()).andReturn(new int[] { InformationFlowOptionsBean.LINE_DESCR_BUSINESS_OBJECTS }).anyTimes();
    EasyMock.expect(generalBuildingBlockServiceM.refreshBuildingBlocks(isrList)).andReturn(new ArrayList<BuildingBlock>(isrList));
    EasyMock.replay(generalBuildingBlockServiceM, infoBean);

    Document res = svgExportServiceImpl.generateSvgInformationFlowExport(isrList, null, null, false, infoBean);
    EasyMock.verify(generalBuildingBlockServiceM, infoBean);

    // saving the file
    try {
      res.save(outputFile);
    } catch (IOException e1) {
      LOGGER.error(e1.getMessage(), e1);
    } catch (SvgExportException e1) {
      LOGGER.error(e1.getMessage(), e1);
    }

    assertTrue(svgFileContains(isrList.get(0).getInformationSystem().getName()));
    assertTrue(svgFileContains(isrList.get(1).getInformationSystem().getName()));
    assertTrue(res.isFinalized());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.SvgExportServiceImpl#generateSvgPortfolioExport(java.util.List, de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.IPortfolioOptions)}.
   */
  @Test
  public void testGenerateSvgPortfolioExport() {
    List<InformationSystemRelease> isrList = getTestIsrList();

    PortfolioOptionsBean optBean = new PortfolioOptionsBean();
    ColorDimensionOptionsBean colorBean = optBean.getColorOptionsBean();
    colorBean.setDefaultColor("1A301D");

    EasyMock.expect(generalBuildingBlockServiceM.refreshBuildingBlocks(isrList)).andReturn(new ArrayList<BuildingBlock>(isrList));
    EasyMock.replay(generalBuildingBlockServiceM);
    EasyMock.expect(attributeTypeServiceM.isNumberAT(optBean.getYAxisAttributeId())).andReturn(false);
    EasyMock.replay(attributeTypeServiceM);
    Document res = svgExportServiceImpl.generateSvgPortfolioExport(isrList, optBean);
    EasyMock.verify(generalBuildingBlockServiceM);

    // saving the file
    try {
      res.save(outputFile);
    } catch (IOException e1) {
      LOGGER.error(e1.getMessage(), e1);
    } catch (SvgExportException e1) {
      LOGGER.error(e1.getMessage(), e1);
    }

    assertTrue(svgFileContains(isrList.get(0).getInformationSystem().getName()));
    assertTrue(svgFileContains(isrList.get(1).getInformationSystem().getName()));
    assertTrue(res.isFinalized());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.SvgExportServiceImpl#generateSvgLandscapeDiagramExport(de.iteratec.iteraplan.model.dto.LandscapeDiagramConfigDTO, de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.ILandscapeOptions)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGenerateSvgLandscapeDiagramExport() {
    List<InformationSystemRelease> isrList = getTestIsrList();

    LandscapeDiagramConfigDTO config = new LandscapeDiagramConfigDTO();
    config.setContentBbs(isrList);
    // redirects to status attribute
    config.setColumnAttributeId(Integer.valueOf(0));
    // redirects to custom attribute below
    config.setRowAttributeId(Integer.valueOf(1));
    config.setContentType(new InformationSystemReleaseTypeMu()); //no idea why InformationSystemReleaseTypeQu fails

    AttributeType at = getConnectedTestAT(isrList);
    EasyMock.expect(attributeTypeDAOm.loadObjectById(Integer.valueOf(1), AttributeType.class)).andReturn(at).anyTimes();

    LandscapeOptionsBean optBean = new LandscapeOptionsBean();
    optBean.getColorOptionsBean().setDefaultColor("1A301D");

    EasyMock.expect(attributeTypeDAOm.loadObjectById(Integer.valueOf(0), AttributeType.class)).andReturn(null).anyTimes();
    EasyMock.expect(generalBuildingBlockServiceM.refreshBuildingBlocks(isrList)).andReturn(new ArrayList<BuildingBlock>(isrList));
    EasyMock.replay(generalBuildingBlockServiceM, attributeTypeDAOm);
    Document res = svgExportServiceImpl.generateSvgLandscapeDiagramExport(config, optBean);
    EasyMock.verify(generalBuildingBlockServiceM, attributeTypeDAOm);

    // saving the file
    try {
      res.save(outputFile);
    } catch (IOException e1) {
      LOGGER.error(e1.getMessage(), e1);
    } catch (SvgExportException e1) {
      LOGGER.error(e1.getMessage(), e1);
    }

    assertTrue(svgFileContains(at.getName()));
    List<AttributeValue> enumAVs = Lists.newArrayList(at.getAllAttributeValues());
    Collections.sort(enumAVs);
    assertTrue(svgFileContains(enumAVs.get(0).getIdentityString()));
    assertTrue(svgFileContains(enumAVs.get(1).getIdentityString()));
    assertTrue(svgFileContains(isrList.get(0).getInformationSystem().getName()));
    assertTrue(svgFileContains(isrList.get(1).getInformationSystem().getName()));
    assertTrue(res.isFinalized());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.SvgExportServiceImpl#generateSvgMasterplanExport(java.util.List, de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.IMasterplanOptions)}.
   */
  @Test
  public void testGenerateSvgMasterplanExport() {
    List<InformationSystemRelease> isrList = getTestIsrList();

    MasterplanOptionsBean optBean = new MasterplanOptionsBean();
    MasterplanRowTypeOptions level0Opts = new MasterplanRowTypeOptions("", optBean.getSelectedBbType(), 0, new ArrayList<DateInterval>(),
        new ArrayList<BBAttribute>(), new ArrayList<ColumnEntry>());
    optBean.setLevel0Options(level0Opts);
    ColorDimensionOptionsBean colorBean = level0Opts.getColorOptions();
    colorBean.setDefaultColor("1A301D");
    level0Opts.setHierarchicalSort(true);

    EasyMock.expect(generalBuildingBlockServiceM.refreshBuildingBlocks(isrList)).andReturn(new ArrayList<BuildingBlock>(isrList));
    EasyMock.replay(generalBuildingBlockServiceM);
    Document res = svgExportServiceImpl.generateSvgMasterplanExport(isrList, optBean);
    EasyMock.verify(generalBuildingBlockServiceM);

    // saving the file
    try {
      res.save(outputFile);
    } catch (IOException e1) {
      LOGGER.error(e1.getMessage(), e1);
    } catch (SvgExportException e1) {
      LOGGER.error(e1.getMessage(), e1);
    }

    // start and enddate of the ISRs
    String expStart = de.iteratec.iteraplan.common.util.DateUtils.formatAsString(isrList.get(0).getRuntimePeriod().getStart(),
        UserContext.getCurrentLocale());
    String expEnd = de.iteratec.iteraplan.common.util.DateUtils.formatAsString(isrList.get(1).getRuntimePeriod().getEnd(),
        UserContext.getCurrentLocale());

    assertTrue(svgFileContains(isrList.get(0).getInformationSystem().getName()));
    assertTrue(svgFileContains(isrList.get(1).getInformationSystem().getName()));
    assertTrue(svgFileContains(expStart));
    assertTrue(svgFileContains(expEnd));
    assertTrue(res.isFinalized());

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.SvgExportServiceImpl#generateSvgPieBarDiagramExport(java.util.List, de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGenerateSvgBarDiagramExport() {
    List<InformationSystemRelease> isrList = getTestIsrList();

    PieBarDiagramOptionsBean optBean = new PieBarDiagramOptionsBean();
    ColorDimensionOptionsBean colorBean = optBean.getColorOptionsBean();
    colorBean.setDefaultColor("1A301D");
    colorBean.setAvailableColors(Lists.newArrayList("1A301D"));

    optBean.setDiagramType(DiagramType.BAR);
    optBean.setSelectedKeyAssociation("informationSystemRelease.singular");
    optBean.setDiagramKeyType(DiagramKeyType.ATTRIBUTE_VALUES);
    optBean.setSelectedKeyAttributeTypeId(10);
    optBean.setNumberOfSelectedElements(isrList.size());

    AttributeType at = getConnectedTestAT(isrList);

    EasyMock.expect(attributeTypeServiceM.loadObjectById(Integer.valueOf(10))).andReturn(at).anyTimes();
    EasyMock.expect(attributeValueServiceM.getAllAVStrings(Integer.valueOf(10))).andReturn(Lists.newArrayList("Val1", "Val2"));

    EasyMock.expect(generalBuildingBlockServiceM.refreshBuildingBlocks(isrList)).andReturn(new ArrayList<BuildingBlock>(isrList)).anyTimes();
    EasyMock.replay(generalBuildingBlockServiceM, attributeTypeServiceM, attributeValueServiceM);
    Document res = svgExportServiceImpl.generateSvgPieBarDiagramExport(isrList, optBean);
    EasyMock.verify(generalBuildingBlockServiceM, attributeTypeServiceM, attributeValueServiceM);

    // saving the file
    try {
      res.save(outputFile);
    } catch (IOException e1) {
      LOGGER.error(e1.getMessage(), e1);
    } catch (SvgExportException e1) {
      LOGGER.error(e1.getMessage(), e1);
    }

    String expTitle = isrList.size() + " " + MessageAccess.getStringOrNull(optBean.getSelectedBbType(), UserContext.getCurrentLocale());

    List<AttributeValue> avList = Lists.newArrayList(at.getAllAttributeValues());
    Collections.sort(avList);
    assertTrue(svgFileContains(avList.get(0).getIdentityString()));
    assertTrue(svgFileContains(avList.get(1).getIdentityString()));
    assertTrue(svgFileContains(expTitle));
    assertTrue(res.isFinalized());
  }

  /**
   * Creates a AttributeType which shows up in the ISRs AttributeValueAssignments. ID is set to 10.
   * 
   * @param isrList the list to be connected to. Should be the list provided by getTestIsrList(), or at least have size 2.
   * @return connected AttributeType
   */
  private AttributeType getConnectedTestAT(List<InformationSystemRelease> isrList) {
    assert isrList.size() == 2;

    AttributeType at = new EnumAT();
    at.setName("myAttr");
    at.setId(Integer.valueOf(10));

    EnumAV av1 = new EnumAV();
    av1.setName("Val1");
    av1.setAttributeType((EnumAT) at);
    av1.setPosition(0);
    AttributeValueAssignment ava1 = new AttributeValueAssignment();
    // sets both ways in the AttributeValueAssignment
    ava1.setAttributeValue(av1);
    ava1.setBuildingBlock(isrList.get(0));
    Set<AttributeValueAssignment> avaContainer1 = Sets.newHashSet(ava1);
    // sets both ways in the AV and in the ISR
    av1.setAttributeValueAssignments(avaContainer1);
    isrList.get(0).setAttributeValueAssignments(avaContainer1);

    // see above
    EnumAV av2 = new EnumAV();
    av2.setName("Val2");
    av2.setAttributeType((EnumAT) at);
    av2.setPosition(1);
    AttributeValueAssignment ava2 = new AttributeValueAssignment();
    ava2.setAttributeValue(av2);
    ava2.setBuildingBlock(isrList.get(1));
    Set<AttributeValueAssignment> avaContainer2 = Sets.newHashSet(ava2);
    av2.setAttributeValueAssignments(avaContainer2);
    isrList.get(1).setAttributeValueAssignments(avaContainer2);

    ((EnumAT) at).setAttributeValues(Sets.newHashSet(av1, av2));

    return at;
  }

  /**
   * The ISRs contained have an Id, an IS and a RuntimePeriod.
   * 
   * @return list of ISRs for testing purposes
   */
  private List<InformationSystemRelease> getTestIsrList() {
    InformationSystemRelease isr1 = mtdf.getInformationSystemReleaseTestData();
    InformationSystemRelease isr2 = mtdf.getInformationSystemReleaseTestData();
    isr1.setId(Integer.valueOf(50));
    isr1.setInformationSystem(mtdf.getInformationSystem());
    isr2.setId(Integer.valueOf(51));
    isr2.setInformationSystem(mtdf.getInformationSystem());

    Date now = new Date();
    Date startDate = DateUtils.addDays(now, -5);
    Date endDate = DateUtils.addDays(now, 5);
    isr1.setRuntimePeriod(new RuntimePeriod(startDate, endDate));
    isr2.setRuntimePeriod(new RuntimePeriod(startDate, endDate));

    return Lists.newArrayList(isr1, isr2);
  }

  /**
   * @param doc Document containing the export
   * @param exp String being looked for
   * 
   * @return whether the string is contained 
   */
  private boolean svgFileContains(String exp) {

    // initiating the BufferedReader
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(outputFile));
    } catch (FileNotFoundException e) {
      LOGGER.error(e.getMessage(), e);
    }

    // reading the file
    try {
      String line;
      line = br.readLine();
      while (line != null) {
        if (line.contains(exp)) {
          return true;
        }
        line = br.readLine();
      }
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(br);
    }

    return false;
  }

}
