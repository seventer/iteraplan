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
package de.iteratec.iteraplan.businesslogic.service.legacyExcel;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.BuildingBlockHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.CellValueHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ImportWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog;
import de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.businesslogic.service.BusinessFunctionService;
import de.iteratec.iteraplan.businesslogic.service.BusinessObjectService;
import de.iteratec.iteraplan.businesslogic.service.BusinessProcessService;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemService;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.businesslogic.service.Isr2BoAssociationService;
import de.iteratec.iteraplan.businesslogic.service.ProductService;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentService;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;


@SuppressWarnings("PMD.SingularField")
public class ExcelImportServiceImplTest {
  /** Excel file name, to be tested. */
  private static final String              PERMISSIONS_EXCEL_FILE           = "objectrelatedpermissionswithbd.xls";
  /** Excel file name, to be tested. */
  private static final String              PERMISSIONS_EXCEL_FILE_WITH_NAME = "objectrelatedpermissionswithbdandname.xls";

  private static final String              NEW_SUFFIX                       = "new";

  private ArchitecturalDomainService       architecturalDomainServiceMock;
  private BusinessDomainService            businessDomainServiceMock;
  private BusinessFunctionService          businessFunctionServiceMock;
  private BusinessObjectService            businessObjectServiceMock;
  private InfrastructureElementService     infrastructureElementServiceMock;
  private InformationSystemReleaseService  informationSystemReleaseServiceMock;
  private InformationSystemService         informationSystemServiceMock;
  private Isr2BoAssociationService         isr2BoAssociationServiceMock;
  private InformationSystemDomainService   informationSystemDomainServiceMock;
  private TechnicalComponentReleaseService technicalComponentReleaseServiceMock;
  private TechnicalComponentService        technicalComponentServiceMock;
  private BusinessProcessService           businessProcessServiceMock;
  private ProductService                   productServiceMock;
  private ProjectService                   projectServiceMock;
  private BusinessUnitService              businessUnitServiceMock;
  private UserService                      userService;
  private BuildingBlockServiceLocator      bbServiceLocator;

  private BuildingBlockTypeService         buildingBlockTypeService;
  /** Only needed in order to inject the Processing Logger */

  private ExcelImportServiceImpl           excelImportService;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() {
    excelImportService = new ExcelImportServiceImpl();

    User user = new User();
    user.setDataSource("MASTER");
    user.setLoginName("system");
    UserContext.setCurrentUserContext(new UserContext("system", new HashSet<Role>(), Locale.getDefault(), user));

    architecturalDomainServiceMock = EasyMock.createMock("ArchitecturalDomainService", ArchitecturalDomainService.class);
    businessDomainServiceMock = EasyMock.createMock("BusinessDomainService", BusinessDomainService.class);
    businessFunctionServiceMock = EasyMock.createMock(BusinessFunctionService.class);
    businessProcessServiceMock = EasyMock.createMock(BusinessProcessService.class);
    businessObjectServiceMock = EasyMock.createMock(BusinessObjectService.class);
    businessUnitServiceMock = EasyMock.createMock(BusinessUnitService.class);
    productServiceMock = EasyMock.createMock(ProductService.class);
    projectServiceMock = EasyMock.createMock(ProjectService.class);
    infrastructureElementServiceMock = EasyMock.createMock(InfrastructureElementService.class);
    informationSystemServiceMock = EasyMock.createMock(InformationSystemService.class);
    informationSystemReleaseServiceMock = EasyMock.createMock(InformationSystemReleaseService.class);
    isr2BoAssociationServiceMock = EasyMock.createMock("isr2BoAssociationServiceMock", Isr2BoAssociationService.class);
    informationSystemDomainServiceMock = EasyMock.createMock(InformationSystemDomainService.class);
    technicalComponentReleaseServiceMock = EasyMock.createMock(TechnicalComponentReleaseService.class);
    technicalComponentServiceMock = EasyMock.createMock(TechnicalComponentService.class);
    buildingBlockTypeService = EasyMock.createMock(BuildingBlockTypeService.class);
    userService = EasyMock.createMock("UserService", UserService.class);
    bbServiceLocator = EasyMock.createMock("BuildingBlockServiceLocator", BuildingBlockServiceLocator.class);

    excelImportService.setUserService(userService);
    excelImportService.setBuildingBlockServiceLocator(bbServiceLocator);

    BuildingBlockFactory.setBbTypeService(buildingBlockTypeService);
    BuildingBlockFactory.setBbServiceLocator(bbServiceLocator);

    // create logger and inject into ThreadLocal, so that all classes can access it
    ProcessingLog logger = EasyMock.createMockBuilder(ProcessingLog.class).addMockedMethods("warn", "debug", "error").createNiceMock();
    @SuppressWarnings("unused")
    ImportWorkbook wb = new ImportWorkbook(logger);

    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.ARCHITECTURALDOMAIN))
        .andReturn((BuildingBlockService) architecturalDomainServiceMock).anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.BUSINESSDOMAIN)).andReturn((BuildingBlockService) businessDomainServiceMock)
        .anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.BUSINESSFUNCTION)).andReturn((BuildingBlockService) businessFunctionServiceMock)
        .anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.BUSINESSOBJECT)).andReturn((BuildingBlockService) businessObjectServiceMock)
        .anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.BUSINESSPROCESS)).andReturn((BuildingBlockService) businessProcessServiceMock)
        .anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.BUSINESSUNIT)).andReturn((BuildingBlockService) businessUnitServiceMock)
        .anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.PRODUCT)).andReturn((BuildingBlockService) productServiceMock).anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.PROJECT)).andReturn((BuildingBlockService) projectServiceMock).anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT))
        .andReturn((BuildingBlockService) infrastructureElementServiceMock).anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN))
        .andReturn((BuildingBlockService) informationSystemDomainServiceMock).anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE))
        .andReturn((BuildingBlockService) informationSystemReleaseServiceMock).anyTimes();

    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.ISR2BOASSOCIATION))
        .andReturn((BuildingBlockService) isr2BoAssociationServiceMock).anyTimes();
    EasyMock.expect(bbServiceLocator.getIsr2BoAssociationService()).andReturn(isr2BoAssociationServiceMock).anyTimes();

    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE))
        .andReturn((BuildingBlockService) technicalComponentReleaseServiceMock).anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.INFORMATIONSYSTEM))
        .andReturn((BuildingBlockService) informationSystemServiceMock).anyTimes();
    EasyMock.expect(bbServiceLocator.getService(TypeOfBuildingBlock.TECHNICALCOMPONENT))
        .andReturn((BuildingBlockService) technicalComponentServiceMock).anyTimes();
  }

  @After
  public void tearDown() {
    ImportWorkbook.removeProcessingLog();
    BuildingBlockFactory.setBbTypeService(null);
    BuildingBlockFactory.setBbServiceLocator(null);
  }

  /**
   * All the building blocks passed in here carry an ID already, i.e. they will trigger an update.
   */
  @SuppressWarnings("boxing")
  @Test
  public void testImportContentToDaosWithUpdates() {
    // prepare test data
    LandscapeData testContent = new LandscapeData();

    // Data from import
    ArchitecturalDomain ad1new = createSimpleArchitecturalDomain();
    ArchitecturalDomain rootAd = new ArchitecturalDomain();
    BusinessDomain bd1new = createSimpleBusinessDomain();
    BusinessDomain rootBd = new BusinessDomain();
    BusinessFunction bf1new = createSimpleBusinessFunction();
    BusinessFunction rootBf = new BusinessFunction();
    BusinessObject bo1new = createSimpleBusinessObject();
    BusinessObject rootBo = new BusinessObject();
    InformationSystemRelease isr1new = createSimpleInformationSystemRelease();
    TechnicalComponentRelease tcr1new = createSimpleTechnicalComponentRelease();
    ad1new.setName(ad1new.getNonHierarchicalName() + NEW_SUFFIX);
    bd1new.setName(bd1new.getNonHierarchicalName() + NEW_SUFFIX);
    bf1new.setName(bf1new.getNonHierarchicalName() + NEW_SUFFIX);
    bo1new.setName(bo1new.getNonHierarchicalName() + NEW_SUFFIX);
    isr1new.setVersion(isr1new.getVersion() + NEW_SUFFIX);
    tcr1new.setVersion(tcr1new.getVersion() + NEW_SUFFIX);

    final BuildingBlockHolder adHolder = new BuildingBlockHolder(ad1new, null, null);
    final BuildingBlockHolder bdHolder = new BuildingBlockHolder(bd1new, null, null);
    final BuildingBlockHolder bfHolder = new BuildingBlockHolder(bf1new, null, null);
    final BuildingBlockHolder boHolder = new BuildingBlockHolder(bo1new, null, null);
    final BuildingBlockHolder isrHolder = new BuildingBlockHolder(isr1new, null, null);
    final BuildingBlockHolder tcrHolder = new BuildingBlockHolder(tcr1new, null, null);

    testContent.addBuildingBlock(adHolder);
    testContent.addBuildingBlock(bdHolder);
    testContent.addBuildingBlock(bfHolder);
    testContent.addBuildingBlock(boHolder);
    testContent.addBuildingBlock(isrHolder);
    testContent.addBuildingBlock(tcrHolder);

    // set expectations
    EasyMock.expect(architecturalDomainServiceMock.saveOrUpdate(ad1new)).andReturn(ad1new);
    EasyMock.expect(architecturalDomainServiceMock.getFirstElement()).andReturn(rootAd);
    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(bd1new)).andReturn(bd1new);
    EasyMock.expect(businessDomainServiceMock.getFirstElement()).andReturn(rootBd);
    EasyMock.expect(businessFunctionServiceMock.saveOrUpdate(bf1new)).andReturn(bf1new);
    EasyMock.expect(businessFunctionServiceMock.getFirstElement()).andReturn(rootBf);
    EasyMock.expect(businessObjectServiceMock.saveOrUpdate(bo1new)).andReturn(bo1new);
    EasyMock.expect(businessObjectServiceMock.getFirstElement()).andReturn(rootBo);

    // ISR
    EasyMock.expect(informationSystemReleaseServiceMock.isDuplicateInformationSystem("My Test IS", 84)).andReturn(Boolean.FALSE);
    EasyMock.expect(informationSystemReleaseServiceMock.saveOrUpdate(isr1new)).andReturn(isr1new);

    // TCR
    EasyMock.expect(technicalComponentReleaseServiceMock.isDuplicateTechnicalComponent("My Test TC", 26)).andReturn(Boolean.FALSE);
    EasyMock.expect(technicalComponentReleaseServiceMock.saveOrUpdate(tcr1new)).andReturn(tcr1new).anyTimes();

    // and now test it
    EasyMock.replay(architecturalDomainServiceMock, businessDomainServiceMock, businessFunctionServiceMock, businessObjectServiceMock);
    EasyMock.replay(informationSystemServiceMock, informationSystemReleaseServiceMock, technicalComponentServiceMock,
        technicalComponentReleaseServiceMock);
    EasyMock.replay(bbServiceLocator);
    excelImportService.importContentToDaos(testContent.getBuildingBlocks());
    EasyMock.verify(architecturalDomainServiceMock, businessDomainServiceMock, businessFunctionServiceMock, businessObjectServiceMock);
    EasyMock.verify(informationSystemServiceMock, informationSystemReleaseServiceMock, technicalComponentServiceMock,
        technicalComponentReleaseServiceMock);
  }

  /**
   * All the building blocks passed in here carry no ID, i.e. they will trigger an insert.
   */
  @Test
  public void testImportContentToDaosWithInserts() {
    LandscapeData testContent = new LandscapeData();

    // Data from insert
    ArchitecturalDomain ad1 = createSimpleArchitecturalDomain();
    BusinessDomain bd1 = createSimpleBusinessDomain();
    BusinessFunction bf1 = createSimpleBusinessFunction();
    BusinessObject bo1 = createSimpleBusinessObject();
    // Tests insert of the ISR/TCR, no change to IS/TC
    InformationSystemRelease isr1 = createSimpleInformationSystemRelease();
    TechnicalComponentRelease tcr1 = createSimpleTechnicalComponentRelease();

    final BuildingBlockHolder adHolder = new BuildingBlockHolder(ad1, null, null);
    final BuildingBlockHolder bdHolder = new BuildingBlockHolder(bd1, null, null);
    final BuildingBlockHolder bfHolder = new BuildingBlockHolder(bf1, null, null);
    final BuildingBlockHolder boHolder = new BuildingBlockHolder(bo1, null, null);
    final BuildingBlockHolder isrHolder = new BuildingBlockHolder(isr1, null, null);
    final BuildingBlockHolder tcrHolder = new BuildingBlockHolder(tcr1, null, null);

    testContent.addBuildingBlock(adHolder);
    testContent.addBuildingBlock(bdHolder);
    testContent.addBuildingBlock(bfHolder);
    testContent.addBuildingBlock(boHolder);
    testContent.addBuildingBlock(isrHolder);
    testContent.addBuildingBlock(tcrHolder);

    // for generating blank ISR/TCRs (three times, because we need two copies here also)
    EasyMock.replay(buildingBlockTypeService);

    setExpectations(ad1, bd1, bf1, bo1);
    setIsrExpectations(isr1);
    setTcrExpectations(tcr1);

    // and now test it
    EasyMock.replay(architecturalDomainServiceMock, businessDomainServiceMock, businessFunctionServiceMock, businessObjectServiceMock);
    EasyMock.replay(informationSystemServiceMock, informationSystemReleaseServiceMock, technicalComponentServiceMock,
        technicalComponentReleaseServiceMock);
    EasyMock.replay(bbServiceLocator);
    excelImportService.importContentToDaos(testContent.getBuildingBlocks());
    EasyMock.verify(architecturalDomainServiceMock, businessDomainServiceMock, businessFunctionServiceMock, businessObjectServiceMock);
    EasyMock.verify(informationSystemServiceMock, informationSystemReleaseServiceMock, technicalComponentServiceMock,
        technicalComponentReleaseServiceMock);
    EasyMock.verify(buildingBlockTypeService);
  }

  @SuppressWarnings("boxing")
  private void setTcrExpectations(TechnicalComponentRelease tcr1) {
    EasyMock.expect(technicalComponentReleaseServiceMock.isDuplicateTechnicalComponent("My Test TC", 26)).andReturn(Boolean.TRUE);
    TechnicalComponent existingTc = createSimpleTechnicalComponent();
    EasyMock.expect(technicalComponentServiceMock.findByNames(Sets.newHashSet("My Test TC"))).andReturn(Lists.newArrayList(existingTc));

    EasyMock.expect(technicalComponentReleaseServiceMock.saveOrUpdate(tcr1)).andReturn(tcr1).anyTimes();
  }

  @SuppressWarnings("boxing")
  private void setIsrExpectations(InformationSystemRelease isr1) {
    EasyMock.expect(informationSystemReleaseServiceMock.isDuplicateInformationSystem("My Test IS", 84)).andReturn(Boolean.TRUE);
    InformationSystem existingIs = createSimpleInformationSystem();
    EasyMock.expect(informationSystemServiceMock.findByNames(Sets.newHashSet("My Test IS"))).andReturn(Lists.newArrayList(existingIs));

    EasyMock.expect(informationSystemReleaseServiceMock.saveOrUpdate(isr1)).andReturn(isr1);
  }

  private void setExpectations(ArchitecturalDomain ad1, BusinessDomain bd1, BusinessFunction bf1, BusinessObject bo1) {
    ArchitecturalDomain rootAd = new ArchitecturalDomain();
    BusinessDomain rootBd = new BusinessDomain();
    BusinessFunction rootBf = new BusinessFunction();
    BusinessObject rootBo = new BusinessObject();

    EasyMock.expect(architecturalDomainServiceMock.getFirstElement()).andReturn(rootAd);
    EasyMock.expect(architecturalDomainServiceMock.saveOrUpdate(ad1)).andReturn(ad1);

    EasyMock.expect(businessDomainServiceMock.getFirstElement()).andReturn(rootBd);
    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(bd1)).andReturn(bd1);

    EasyMock.expect(businessFunctionServiceMock.getFirstElement()).andReturn(rootBf);
    EasyMock.expect(businessFunctionServiceMock.saveOrUpdate(bf1)).andReturn(bf1);

    EasyMock.expect(businessObjectServiceMock.getFirstElement()).andReturn(rootBo);
    EasyMock.expect(businessObjectServiceMock.saveOrUpdate(bo1)).andReturn(bo1);
  }

  @SuppressWarnings("deprecation")
  private InformationSystemRelease createSimpleInformationSystemRelease() {
    InformationSystem is = new InformationSystem();
    is.setName("My Test IS");
    is.setId(Integer.valueOf(84));
    InformationSystemRelease isr = new InformationSystemRelease();
    is.addRelease(isr);
    isr.setVersion("42a");
    isr.setId(Integer.valueOf(354));
    isr.setBuildingBlockType(createSimpleBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));

    return isr;
  }

  private InformationSystem createSimpleInformationSystem() {
    InformationSystem is = new InformationSystem();
    is.setName("My Test IS");
    is.setId(Integer.valueOf(15));

    return is;
  }

  @SuppressWarnings("deprecation")
  private TechnicalComponentRelease createSimpleTechnicalComponentRelease() {
    TechnicalComponent tc = new TechnicalComponent();
    tc.setName("My Test TC");
    tc.setId(Integer.valueOf(26));
    TechnicalComponentRelease tcr = new TechnicalComponentRelease();
    tc.addRelease(tcr);
    tcr.setVersion("26a");
    tcr.setId(Integer.valueOf(266));
    tcr.setBuildingBlockType(createSimpleBuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));

    return tcr;
  }

  private TechnicalComponent createSimpleTechnicalComponent() {
    TechnicalComponent tc = new TechnicalComponent();
    tc.setName("My Test TC");
    tc.setId(Integer.valueOf(11));

    return tc;
  }

  @Test
  public void testCreateBuildingBlockRelationsBusinessFunction() {
    BusinessFunction bf = createSimpleBusinessFunction();

    // test with an empty relations mapping
    EasyMock.expect(businessFunctionServiceMock.saveOrUpdate(bf)).andReturn(bf);
    EasyMock.replay(businessFunctionServiceMock, businessDomainServiceMock, businessObjectServiceMock, bbServiceLocator);
    Map<String, CellValueHolder> bfRelations = new HashMap<String, CellValueHolder>();
    excelImportService.createBuildingBlockRelations(bf, bfRelations, Locale.getDefault());
    Assert.assertTrue("import should not change relation set", bf.getBusinessDomains().isEmpty());
    Assert.assertTrue("import should not change relation set", bf.getBusinessObjects().isEmpty());
    EasyMock.verify(businessFunctionServiceMock, businessDomainServiceMock, businessObjectServiceMock, bbServiceLocator);

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell1 = row.createCell(0);
    final Cell cell2 = row.createCell(1);

    // prepare one Business domain and one business object that exists already in the "DB"
    EasyMock.reset(businessFunctionServiceMock, businessDomainServiceMock, businessObjectServiceMock);
    BusinessDomain bd1 = createSimpleBusinessDomain();
    cell2.setCellValue(bd1.getName());
    CellValueHolder cellValueHolder2 = new CellValueHolder(cell2);
    bfRelations.put(MessageAccess.getStringOrNull("businessDomain.plural"), cellValueHolder2);
    BusinessObject bo1 = createSimpleBusinessObject();
    cell1.setCellValue(bo1.getName());
    CellValueHolder cellValueHolder1 = new CellValueHolder(cell1);
    bfRelations.put(MessageAccess.getStringOrNull("businessObject.plural"), cellValueHolder1);
    // set service expectations
    EasyMock.expect(businessDomainServiceMock.findByNames(Sets.newHashSet(bd1.getName()))).andReturn(Arrays.asList(new BusinessDomain[] { bd1 }));
    EasyMock.expect(businessObjectServiceMock.findByNames(Sets.newHashSet(bo1.getName()))).andReturn(Arrays.asList(new BusinessObject[] { bo1 }));
    EasyMock.expect(businessFunctionServiceMock.saveOrUpdate(bf)).andReturn(bf);

    // and test
    EasyMock.replay(businessFunctionServiceMock, businessDomainServiceMock, businessObjectServiceMock);
    excelImportService.createBuildingBlockRelations(bf, bfRelations, Locale.getDefault());
    EasyMock.verify(businessFunctionServiceMock, businessDomainServiceMock, businessObjectServiceMock);
    Assert.assertTrue("import should add bd1", bf.getBusinessDomains().contains(bd1));
    Assert.assertTrue("import should add bo1", bf.getBusinessObjects().contains(bo1));

    // next test; reset and clean-up
    EasyMock.reset(businessFunctionServiceMock, businessDomainServiceMock, businessObjectServiceMock);
    bf.setBusinessDomains(new HashSet<BusinessDomain>());
    bf.setBusinessObjects(new HashSet<BusinessObject>());
    // feed in a relation for a non-existent TCR
    EasyMock.expect(businessDomainServiceMock.findByNames(Sets.newHashSet(bd1.getName()))).andReturn(new ArrayList<BusinessDomain>());
    EasyMock.expect(businessObjectServiceMock.findByNames(Sets.newHashSet(bo1.getName()))).andReturn(new ArrayList<BusinessObject>());
    EasyMock.expect(businessFunctionServiceMock.saveOrUpdate(bf)).andReturn(bf);

    // and test
    EasyMock.replay(businessFunctionServiceMock, businessDomainServiceMock, businessObjectServiceMock);
    excelImportService.createBuildingBlockRelations(bf, bfRelations, Locale.getDefault());
    EasyMock.verify(businessFunctionServiceMock, businessDomainServiceMock, businessObjectServiceMock);
    Assert.assertTrue("import should not add bd1", bf.getBusinessDomains().isEmpty());
    Assert.assertTrue("import should not not bo1", bf.getBusinessObjects().isEmpty());
  }

  @SuppressWarnings("deprecation")
  private BusinessObject createSimpleBusinessObject() {
    BusinessObject bo1 = new BusinessObject();
    bo1.setId(Integer.valueOf(5435));
    bo1.setName("My Biz Object");
    bo1.setBuildingBlockType(createSimpleBuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT));
    return bo1;
  }

  @SuppressWarnings("deprecation")
  private BusinessDomain createSimpleBusinessDomain() {
    BusinessDomain bd1 = new BusinessDomain();
    bd1.setId(Integer.valueOf(6541));
    bd1.setName("My Biz Domain1");
    bd1.setBuildingBlockType(createSimpleBuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN));
    return bd1;
  }

  @SuppressWarnings("deprecation")
  private BusinessFunction createSimpleBusinessFunction() {
    BusinessFunction bf = new BusinessFunction();
    bf.setId(Integer.valueOf(168));
    bf.setName("BizFunc1");
    bf.setDescription("desc");
    bf.setBuildingBlockType(createSimpleBuildingBlockType(TypeOfBuildingBlock.BUSINESSFUNCTION));
    return bf;
  }

  @Test
  public void testCreateBuildingBlockRelationsWithEmptyMap() {
    ArchitecturalDomain ad = createSimpleArchitecturalDomain();

    // test with an empty relations mapping
    EasyMock.expect(architecturalDomainServiceMock.saveOrUpdate(ad)).andReturn(ad);
    EasyMock.replay(architecturalDomainServiceMock, technicalComponentReleaseServiceMock, bbServiceLocator);
    Map<String, CellValueHolder> adRelations = new HashMap<String, CellValueHolder>();
    excelImportService.createBuildingBlockRelations(ad, adRelations, Locale.getDefault());
    Assert.assertTrue("import should not change relation set", ad.getTechnicalComponentReleases().isEmpty());
    EasyMock.verify(architecturalDomainServiceMock, technicalComponentReleaseServiceMock, bbServiceLocator);
  }

  @Test
  public void testCreateBuildingBlockRelationsArchitecturalDomain() {
    ArchitecturalDomain ad = createSimpleArchitecturalDomain();
    Map<String, CellValueHolder> adRelations = new HashMap<String, CellValueHolder>();

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell = row.createCell(0);

    // prepare one related TCR that exists already in the DB
    TechnicalComponentRelease tcr1 = new TechnicalComponentRelease();
    TechnicalComponent tc1 = new TechnicalComponent();
    tcr1.setTechnicalComponent(tc1);
    tcr1.setVersion("1.1");
    tcr1.setId(Integer.valueOf(34131));
    tc1.setName("TC1");
    tc1.setId(Integer.valueOf(646));
    String tcrName = tcr1.getNonHierarchicalName();
    cell.setCellValue(tcrName);

    CellValueHolder cellValueHolder = new CellValueHolder(cell);
    adRelations.put(MessageAccess.getStringOrNull("technicalComponentRelease.plural"), cellValueHolder);
    // set service expectations
    EasyMock.expect(technicalComponentReleaseServiceMock.findByNames(Sets.newHashSet(tcrName))).andReturn(Lists.newArrayList(tcr1));
    EasyMock.expect(architecturalDomainServiceMock.saveOrUpdate(ad)).andReturn(ad);

    // and test
    EasyMock.replay(architecturalDomainServiceMock, technicalComponentReleaseServiceMock, bbServiceLocator);
    excelImportService.createBuildingBlockRelations(ad, adRelations, Locale.getDefault());
    EasyMock.verify(architecturalDomainServiceMock, technicalComponentReleaseServiceMock);
    Assert.assertTrue("import should add tcr1", ad.getTechnicalComponentReleases().contains(tcr1));

    // next test; reset and clean-up
    EasyMock.reset(architecturalDomainServiceMock, technicalComponentReleaseServiceMock);
    ad.setTechnicalComponentReleases(new HashSet<TechnicalComponentRelease>());
    // feed in a relation for a non-existent TCR
    EasyMock.expect(technicalComponentReleaseServiceMock.findByNames(Sets.newHashSet(tcrName))).andReturn(new ArrayList<TechnicalComponentRelease>());
    EasyMock.expect(architecturalDomainServiceMock.saveOrUpdate(ad)).andReturn(ad);

    // and test
    EasyMock.replay(architecturalDomainServiceMock, technicalComponentReleaseServiceMock);
    excelImportService.createBuildingBlockRelations(ad, adRelations, Locale.getDefault());
    EasyMock.verify(architecturalDomainServiceMock, technicalComponentReleaseServiceMock);
    Assert.assertTrue("import should not add tcr1", ad.getTechnicalComponentReleases().isEmpty());
  }

  @Test
  public void testGetParentNameFromHierarchialName() {
    String result;

    result = excelImportService.getParentNameFromHierarchicalName("0 : 1 : 22 : 333", "333", "");
    Assert.assertEquals("22", result);

    result = excelImportService.getParentNameFromHierarchicalName("xxx : yyy : zzz", "zzz", "");
    Assert.assertEquals("yyy", result);

    result = excelImportService.getParentNameFromHierarchicalName("00 : 11", "11", "");
    Assert.assertEquals("00", result);

    result = excelImportService.getParentNameFromHierarchicalName("00 : 11", null, "");
    Assert.assertEquals("00", result);

    result = excelImportService.getParentNameFromHierarchicalName("00 : 11", "22", "");
    Assert.assertNull(result);

    result = excelImportService.getParentNameFromHierarchicalName("11", null, "");
    Assert.assertNull(result);
  }

  @SuppressWarnings("deprecation")
  private ArchitecturalDomain createSimpleArchitecturalDomain() {
    ArchitecturalDomain ad = new ArchitecturalDomain();
    ad.setId(Integer.valueOf(125));
    ad.setDescription("desc");
    ad.setName("myAD1");
    ad.setBuildingBlockType(createSimpleBuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN));
    return ad;
  }

  private BuildingBlockType createSimpleBuildingBlockType(TypeOfBuildingBlock bbType) {
    return new BuildingBlockType(bbType);
  }

  /**
   * Tests the method {@link ExcelImportService#importObjectRelatedPermissions(java.io.InputStream, java.io.PrintStream)}.
   * 
   * <p>Only the {@link BusinessDomain} objects are tested, because the functionality for other building
   * block types is equivalent.
   */
  @Test
  public void testImportObjectRelatedPermissionsData() {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    InputStream excelInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PERMISSIONS_EXCEL_FILE);

    BusinessDomain businessDomain1 = new BusinessDomain();
    BusinessDomain businessDomain2 = new BusinessDomain();
    BusinessDomain businessDomain4 = new BusinessDomain();
    businessDomain4.setName(AbstractHierarchicalEntity.TOP_LEVEL_NAME);

    EasyMock.expect(businessDomainServiceMock.loadObjectByIdIfExists(Integer.valueOf(1))).andReturn(businessDomain1);
    EasyMock.expect(businessDomainServiceMock.loadObjectByIdIfExists(Integer.valueOf(2))).andReturn(businessDomain2);
    EasyMock.expect(businessDomainServiceMock.loadObjectByIdIfExists(Integer.valueOf(3))).andReturn(null);
    EasyMock.expect(businessDomainServiceMock.loadObjectByIdIfExists(Integer.valueOf(4))).andReturn(businessDomain4);
    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(businessDomain1)).andReturn(businessDomain1);
    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(businessDomain2)).andReturn(businessDomain2);

    User user11 = createSampleUser("bd_user11");
    User user12 = createSampleUser("bd_user12");
    User user21 = createSampleUser("bd_user21");
    User user22 = createSampleUser("bd_user22");
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user11")).andReturn(user11);
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user12")).andReturn(null);
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user21")).andReturn(user21);
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user22")).andReturn(null);
    EasyMock.expect(userService.createUser("bd_user12")).andReturn(user12);
    EasyMock.expect(userService.createUser("bd_user22")).andReturn(user22);

    EasyMock.replay(businessDomainServiceMock, userService, bbServiceLocator);

    excelImportService.importObjectRelatedPermissions(excelInputStream, logWriter);
    Assert.assertEquals(Sets.newHashSet(user11, user12), businessDomain1.getOwningUserEntities());
    Assert.assertEquals(Sets.newHashSet(user21, user22), businessDomain2.getOwningUserEntities());

    EasyMock.verify(businessDomainServiceMock, userService, bbServiceLocator);
  }

  /**
   * Tests the method {@link ExcelImportService#importObjectRelatedPermissions(java.io.InputStream, java.io.PrintStream)}.
   * 
   * <p>Only the {@link BusinessDomain} objects are tested, because the functionality for other building
   * block types is equivalent.
   */
  @SuppressWarnings("boxing")
  @Test
  public void testImportObjectRelatedPermissionsDataWithName() {
    PrintWriter logWriter = new PrintWriter(new NullOutputStream());
    InputStream excelInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PERMISSIONS_EXCEL_FILE_WITH_NAME);

    BusinessDomain businessDomain1 = new BusinessDomain();
    BusinessDomain businessDomain2 = new BusinessDomain();
    BusinessDomain businessDomain3 = new BusinessDomain();
    BusinessDomain businessDomain4 = new BusinessDomain();
    BusinessDomain businessDomain5 = new BusinessDomain();

    businessDomain1.setId(Integer.valueOf(1));
    businessDomain2.setId(Integer.valueOf(2));
    businessDomain3.setId(Integer.valueOf(3));
    businessDomain4.setId(Integer.valueOf(4));
    businessDomain5.setId(Integer.valueOf(5));

    businessDomain2.setName("Test1");
    businessDomain3.setName("Test2");
    businessDomain4.setName("Test3");
    businessDomain5.setName("Test4");

    EasyMock.expect(businessDomainServiceMock.loadObjectByIdIfExists(Integer.valueOf(1))).andReturn(businessDomain1);
    EasyMock.expect(businessDomainServiceMock.doesObjectWithDifferentIdExist(null, "Test1")).andReturn(Boolean.TRUE);
    EasyMock.expect(businessDomainServiceMock.findByNames(Sets.newHashSet("Test1"))).andReturn(Lists.newArrayList(businessDomain2));
    EasyMock.expect(businessDomainServiceMock.doesObjectWithDifferentIdExist(null, "Test2")).andReturn(Boolean.TRUE);
    EasyMock.expect(businessDomainServiceMock.findByNames(Sets.newHashSet("Test2"))).andReturn(Lists.newArrayList(businessDomain3));
    EasyMock.expect(businessDomainServiceMock.doesObjectWithDifferentIdExist(null, "Test3")).andReturn(Boolean.TRUE);
    EasyMock.expect(businessDomainServiceMock.findByNames(Sets.newHashSet("Test3"))).andReturn(Lists.newArrayList(businessDomain4));
    EasyMock.expect(businessDomainServiceMock.doesObjectWithDifferentIdExist(null, "Test4")).andReturn(Boolean.TRUE);
    EasyMock.expect(businessDomainServiceMock.findByNames(Sets.newHashSet("Test4"))).andReturn(Lists.newArrayList(businessDomain5));

    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(businessDomain1)).andReturn(businessDomain1);
    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(businessDomain2)).andReturn(businessDomain2);
    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(businessDomain3)).andReturn(businessDomain3);
    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(businessDomain4)).andReturn(businessDomain4);

    User user11 = createSampleUser("bd_user11");
    User user12 = createSampleUser("bd_user12");
    User user21 = createSampleUser("bd_user21");
    User user22 = createSampleUser("bd_user22");
    User user31 = createSampleUser("bd_user31");
    User user32 = createSampleUser("bd_user12");
    User user41 = createSampleUser("bd_user41");
    User user42 = createSampleUser("bd_user22");
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user11")).andReturn(user11);
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user12")).andReturn(null);
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user21")).andReturn(user21);
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user22")).andReturn(null);
    EasyMock.expect(userService.createUser("bd_user12")).andReturn(user12);
    EasyMock.expect(userService.createUser("bd_user22")).andReturn(user22);
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user31")).andReturn(user31);
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user32")).andReturn(null);
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user41")).andReturn(user41);
    EasyMock.expect(userService.getUserByLoginIfExists("bd_user42")).andReturn(null);
    EasyMock.expect(userService.createUser("bd_user32")).andReturn(user32);
    EasyMock.expect(userService.createUser("bd_user42")).andReturn(user42);

    EasyMock.replay(businessDomainServiceMock, userService, bbServiceLocator);

    excelImportService.importObjectRelatedPermissions(excelInputStream, logWriter);
    Assert.assertEquals(Sets.newHashSet(user11, user12), businessDomain1.getOwningUserEntities());
    Assert.assertEquals(Sets.newHashSet(user21, user22), businessDomain2.getOwningUserEntities());
    Assert.assertEquals(Sets.newHashSet(user31, user32), businessDomain3.getOwningUserEntities());
    Assert.assertEquals(Sets.newHashSet(user41, user42), businessDomain4.getOwningUserEntities());
    Assert.assertTrue(businessDomain5.getOwningUserEntities().isEmpty());

    EasyMock.verify(businessDomainServiceMock, userService, bbServiceLocator);
  }

  /**
   * Creates sample user with the specified {@code loginName}.
   * 
   * @param loginName the user login name
   * @return the newly created user
   */
  private User createSampleUser(String loginName) {
    User user = new User();
    user.setLoginName(loginName);
    user.setFirstName(loginName);
    user.setLastName(loginName);

    return user;
  }

  @SuppressWarnings("boxing")
  @Test
  public void testSetParentRelation() {
    BusinessDomain childBd = createSimpleBusinessDomain();
    Map<String, CellValueHolder> bdRelations = new HashMap<String, CellValueHolder>();
    childBd.setName("BD2");

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell0 = row.createCell(0); //hierarchy

    BusinessDomain parentBd1 = new BusinessDomain();
    parentBd1.setName("BD1 : BD2");
    parentBd1.setId(413301);
    String parentName = parentBd1.getNonHierarchicalName();
    cell0.setCellValue(parentName);
    CellValueHolder cellValueHolder0 = new CellValueHolder(cell0);
    bdRelations.put("Fachliche Domänen hierarchisch", cellValueHolder0);

    EasyMock.expect(businessDomainServiceMock.findByNames(Sets.newHashSet("BD1"))).andReturn(Lists.newArrayList(parentBd1));
    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(childBd)).andReturn(childBd).anyTimes();

    EasyMock.replay(businessDomainServiceMock, bbServiceLocator);
    excelImportService.createBuildingBlockRelations(childBd, bdRelations, Locale.getDefault());
    EasyMock.verify(businessDomainServiceMock);

    Assert.assertEquals(parentBd1, childBd.getParent());
  }

  @Test
  @SuppressWarnings("PMD.ExcessiveMethodLength")
  public void testCreateBuildingBlockRelationsBusinessDomain() {
    BusinessDomain bd = createSimpleBusinessDomain();
    Map<String, CellValueHolder> bdRelations = new HashMap<String, CellValueHolder>();

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell0 = row.createCell(0); //bo
    final Cell cell1 = row.createCell(1); //bf
    final Cell cell2 = row.createCell(2); //bproc
    final Cell cell3 = row.createCell(3); //prod
    final Cell cell4 = row.createCell(4); //bu

    BusinessObject bo1 = new BusinessObject();
    bo1.setName("BO1");
    bo1.setId(Integer.valueOf(34101));
    String boName = bo1.getNonHierarchicalName();
    cell0.setCellValue(boName);
    CellValueHolder cellValueHolder0 = new CellValueHolder(cell0);
    bdRelations.put(MessageAccess.getStringOrNull("businessObject.plural"), cellValueHolder0);
    // set service expectations
    EasyMock.expect(businessObjectServiceMock.findByNames(Sets.newHashSet(boName))).andReturn(Lists.newArrayList(bo1));

    BusinessFunction bf1 = new BusinessFunction();
    bf1.setName("BF1");
    bf1.setId(Integer.valueOf(4101));
    String bfName = bf1.getNonHierarchicalName();
    cell1.setCellValue(bfName);
    CellValueHolder cellValueHolder1 = new CellValueHolder(cell1);
    bdRelations.put(MessageAccess.getStringOrNull("global.business_functions"), cellValueHolder1);
    // set service expectations
    EasyMock.expect(businessFunctionServiceMock.findByNames(Sets.newHashSet(bfName))).andReturn(Lists.newArrayList(bf1));

    BusinessProcess bproc1 = new BusinessProcess();
    bproc1.setName("BPROC1");
    bproc1.setId(Integer.valueOf(4131));
    String bprocName = bproc1.getNonHierarchicalName();
    cell2.setCellValue(bprocName);
    CellValueHolder cellValueHolder2 = new CellValueHolder(cell2);
    bdRelations.put(MessageAccess.getStringOrNull("businessProcess.plural"), cellValueHolder2);
    // set service expectations
    EasyMock.expect(businessProcessServiceMock.findByNames(Sets.newHashSet(bprocName))).andReturn(Lists.newArrayList(bproc1));

    Product prod1 = new Product();
    prod1.setName("PROD1");
    prod1.setId(Integer.valueOf(422131));
    String prodName = prod1.getNonHierarchicalName();
    cell3.setCellValue(prodName);
    CellValueHolder cellValueHolder3 = new CellValueHolder(cell3);
    bdRelations.put(MessageAccess.getStringOrNull("global.products"), cellValueHolder3);
    // set service expectations
    EasyMock.expect(productServiceMock.findByNames(Sets.newHashSet(prodName))).andReturn(Lists.newArrayList(prod1));

    BusinessUnit bu1 = new BusinessUnit();
    bu1.setName("BU1");
    String buName = bu1.getName();
    cell4.setCellValue(buName);
    CellValueHolder cellValueHolder4 = new CellValueHolder(cell4);
    bdRelations.put(MessageAccess.getStringOrNull("businessUnit.plural"), cellValueHolder4);
    EasyMock.expect(businessUnitServiceMock.findByNames(Sets.newHashSet(buName))).andReturn(Lists.newArrayList(bu1));

    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(bd)).andReturn(bd).anyTimes();
    // and test
    EasyMock.replay(businessDomainServiceMock, businessUnitServiceMock, productServiceMock, businessProcessServiceMock, businessFunctionServiceMock,
        businessObjectServiceMock, bbServiceLocator);
    excelImportService.createBuildingBlockRelations(bd, bdRelations, Locale.getDefault());
    EasyMock.verify(businessDomainServiceMock, businessUnitServiceMock, productServiceMock, businessObjectServiceMock, businessProcessServiceMock,
        businessFunctionServiceMock);
    Assert.assertTrue(bd.getBusinessObjects().contains(bo1));
    Assert.assertTrue(bd.getBusinessFunctions().contains(bf1));
    Assert.assertTrue(bd.getBusinessProcesses().contains(bproc1));
    Assert.assertTrue(bd.getProducts().contains(prod1));
    Assert.assertTrue(bd.getBusinessUnits().contains(bu1));

    // next test; reset and clean-up
    EasyMock.reset(businessDomainServiceMock, businessUnitServiceMock, productServiceMock, businessObjectServiceMock, businessProcessServiceMock,
        businessFunctionServiceMock);
    bd.setBusinessObjects(new HashSet<BusinessObject>());
    bd.setBusinessFunctions(new HashSet<BusinessFunction>());
    bd.setBusinessProcesses(new HashSet<BusinessProcess>());
    bd.setBusinessUnits(new HashSet<BusinessUnit>());
    bd.setProducts(new HashSet<Product>());
    // feed in a relation for a non-existent BO
    EasyMock.expect(businessObjectServiceMock.findByNames(Sets.newHashSet(boName))).andReturn(new ArrayList<BusinessObject>());
    EasyMock.expect(businessFunctionServiceMock.findByNames(Sets.newHashSet(bfName))).andReturn(new ArrayList<BusinessFunction>());
    EasyMock.expect(businessProcessServiceMock.findByNames(Sets.newHashSet(bprocName))).andReturn(new ArrayList<BusinessProcess>());
    EasyMock.expect(productServiceMock.findByNames(Sets.newHashSet(prodName))).andReturn(new ArrayList<Product>());
    EasyMock.expect(businessUnitServiceMock.findByNames(Sets.newHashSet(buName))).andReturn(new ArrayList<BusinessUnit>());

    EasyMock.expect(businessDomainServiceMock.saveOrUpdate(bd)).andReturn(bd).anyTimes();
    // and test
    EasyMock.replay(businessDomainServiceMock, businessUnitServiceMock, productServiceMock, businessObjectServiceMock, businessFunctionServiceMock,
        businessProcessServiceMock);
    excelImportService.createBuildingBlockRelations(bd, bdRelations, Locale.getDefault());
    EasyMock.verify(businessDomainServiceMock, businessUnitServiceMock, productServiceMock, businessObjectServiceMock, businessFunctionServiceMock,
        businessProcessServiceMock);
    Assert.assertTrue(bd.getBusinessObjects().isEmpty());
    Assert.assertTrue(bd.getBusinessFunctions().isEmpty());
    Assert.assertTrue(bd.getBusinessProcesses().isEmpty());
    Assert.assertTrue(bd.getProducts().isEmpty());
    Assert.assertTrue(bd.getBusinessUnits().isEmpty());
  }

  @Test
  public void testCreateBuildingBlockRelationsBusinessObject() {
    BusinessObject bo = createSimpleBusinessObject();
    Map<String, CellValueHolder> boRelations = new HashMap<String, CellValueHolder>();

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell0 = row.createCell(0); //bo
    final Cell cell1 = row.createCell(1); //bf
    final Cell cell2 = row.createCell(2); //bd
    final Cell cell3 = row.createCell(3); //isr

    BusinessObject bo1 = new BusinessObject();
    bo1.setName("BO1");
    bo1.setId(Integer.valueOf(34101));
    String boName = bo1.getNonHierarchicalName();
    cell0.setCellValue(boName);
    CellValueHolder cellValueHolder0 = new CellValueHolder(cell0);
    boRelations.put(MessageAccess.getStringOrNull("global.specialisation"), cellValueHolder0);
    // set service expectations
    EasyMock.expect(businessObjectServiceMock.findByNames(Sets.newHashSet(boName))).andReturn(Lists.newArrayList(bo1));

    BusinessFunction bf1 = new BusinessFunction();
    bf1.setName("BF1");
    bf1.setId(Integer.valueOf(4101));
    String bfName = bf1.getNonHierarchicalName();
    cell1.setCellValue(bfName);
    CellValueHolder cellValueHolder1 = new CellValueHolder(cell1);
    boRelations.put(MessageAccess.getStringOrNull("global.business_functions"), cellValueHolder1);
    // set service expectations
    EasyMock.expect(businessFunctionServiceMock.findByNames(Sets.newHashSet(bfName))).andReturn(Lists.newArrayList(bf1));

    BusinessDomain bd1 = new BusinessDomain();
    bd1.setName("BD1");
    bd1.setId(Integer.valueOf(413301));
    String bdName = bd1.getNonHierarchicalName();
    cell2.setCellValue(bdName);
    CellValueHolder cellValueHolder2 = new CellValueHolder(cell2);
    boRelations.put(MessageAccess.getStringOrNull("businessDomain.plural"), cellValueHolder2);
    // set service expectations
    EasyMock.expect(businessDomainServiceMock.findByNames(Sets.newHashSet(bdName))).andReturn(Lists.newArrayList(bd1));

    InformationSystemRelease isr1 = new InformationSystemRelease();
    InformationSystem is1 = new InformationSystem();
    isr1.setInformationSystem(is1);
    isr1.setVersion("1.2");
    is1.setName("IS1");
    String isrName = isr1.getNonHierarchicalName();
    cell3.setCellValue(isrName);
    CellValueHolder cellValueHolder3 = new CellValueHolder(cell3);
    boRelations.put(MessageAccess.getStringOrNull("informationSystemRelease.plural"), cellValueHolder3);
    // set service expectations
    EasyMock.expect(informationSystemReleaseServiceMock.findByNames(Sets.newHashSet(isrName))).andReturn(Lists.newArrayList(isr1));

    EasyMock.expect(businessObjectServiceMock.saveOrUpdate(bo)).andReturn(bo).anyTimes();
    // and test
    EasyMock.replay(businessObjectServiceMock, businessFunctionServiceMock, businessDomainServiceMock, informationSystemReleaseServiceMock,
        bbServiceLocator);
    excelImportService.createBuildingBlockRelations(bo, boRelations, Locale.getDefault());
    EasyMock.verify(businessObjectServiceMock, businessFunctionServiceMock, businessDomainServiceMock, informationSystemReleaseServiceMock);
    Assert.assertTrue(bo.getSpecialisations().contains(bo1));
    Assert.assertTrue(bo.getBusinessFunctions().contains(bf1));
    Assert.assertTrue(bo.getBusinessDomains().contains(bd1));
    Assert.assertTrue(bo.getInformationSystemReleases().contains(isr1));

    // next test; reset and clean-up
    EasyMock.reset(businessObjectServiceMock, businessFunctionServiceMock, businessDomainServiceMock, informationSystemReleaseServiceMock);
    bo.setSpecialisations(new HashSet<BusinessObject>());
    bo.setBusinessFunctions(new HashSet<BusinessFunction>());
    bo.setBusinessDomains(new HashSet<BusinessDomain>());
    bo.setInformationSystemReleases(new HashSet<InformationSystemRelease>());
    // feed in a relation for a non-existent BO
    EasyMock.expect(businessObjectServiceMock.findByNames(Sets.newHashSet(boName))).andReturn(new ArrayList<BusinessObject>());
    EasyMock.expect(businessFunctionServiceMock.findByNames(Sets.newHashSet(bfName))).andReturn(new ArrayList<BusinessFunction>());
    EasyMock.expect(businessDomainServiceMock.findByNames(Sets.newHashSet(bdName))).andReturn(new ArrayList<BusinessDomain>());
    EasyMock.expect(informationSystemReleaseServiceMock.findByNames(Sets.newHashSet(isrName))).andReturn(new ArrayList<InformationSystemRelease>());

    EasyMock.expect(businessObjectServiceMock.saveOrUpdate(bo)).andReturn(bo).anyTimes();
    // and test
    EasyMock.replay(businessObjectServiceMock, businessFunctionServiceMock, businessDomainServiceMock, informationSystemReleaseServiceMock);
    excelImportService.createBuildingBlockRelations(bo, boRelations, Locale.getDefault());
    EasyMock.verify(businessObjectServiceMock, businessFunctionServiceMock, businessDomainServiceMock, informationSystemReleaseServiceMock);
    Assert.assertTrue(bo.getSpecialisations().isEmpty());
    Assert.assertTrue(bo.getBusinessDomains().isEmpty());
    Assert.assertTrue(bo.getBusinessFunctions().isEmpty());
    Assert.assertTrue(bo.getInformationSystemReleases().isEmpty());
  }

  @Test
  @SuppressWarnings("PMD.ExcessiveMethodLength")
  public void testCreateBuildingBlockRelationsInformationSystemRelease() {
    InformationSystemRelease isr = createSimpleInformationSystemRelease();
    Map<String, CellValueHolder> isrRelations = new HashMap<String, CellValueHolder>();

    final HSSFWorkbook workbook = new HSSFWorkbook();
    final HSSFSheet sheet = workbook.createSheet();
    final HSSFRow row = sheet.createRow(0);
    final Cell cell0 = row.createCell(0); //preced
    final Cell cell1 = row.createCell(1); //success
    final Cell cell3 = row.createCell(3); //bo
    final Cell cell4 = row.createCell(4); //isd
    final Cell cell5 = row.createCell(5); //ie
    final Cell cell6 = row.createCell(6); //proj
    final Cell cell7 = row.createCell(7); //tcr
    final Cell cell8 = row.createCell(8); //is

    InformationSystemRelease isrPred = new InformationSystemRelease();
    InformationSystem isPred = new InformationSystem();
    isrPred.setInformationSystem(isPred);
    isrPred.setVersion("1.2");
    isPred.setName("IS_pred");
    String isrName1 = isrPred.getNonHierarchicalName();
    cell0.setCellValue(isrName1);
    CellValueHolder cellValueHolder0 = new CellValueHolder(cell0);
    isrRelations.put(MessageAccess.getStringOrNull("global.predecessors"), cellValueHolder0);
    // set service expectations
    EasyMock.expect(informationSystemReleaseServiceMock.findByNames(Sets.newHashSet(isrName1))).andReturn(Lists.newArrayList(isrPred));

    InformationSystemRelease isrSucc = new InformationSystemRelease();
    InformationSystem isSucc = new InformationSystem();
    isrSucc.setInformationSystem(isSucc);
    isrSucc.setVersion("2.3");
    isSucc.setName("IS_succ");
    String isrName2 = isrSucc.getNonHierarchicalName();
    cell1.setCellValue(isrName2);
    CellValueHolder cellValueHolder1 = new CellValueHolder(cell1);
    isrRelations.put(MessageAccess.getStringOrNull("global.successors"), cellValueHolder1);
    // set service expectations
    EasyMock.expect(informationSystemReleaseServiceMock.findByNames(Sets.newHashSet(isrName2))).andReturn(Lists.newArrayList(isrSucc));

    BusinessObject bo1 = new BusinessObject();
    bo1.setName("BO1");
    bo1.setId(Integer.valueOf(34101));
    String boName = bo1.getNonHierarchicalName();
    cell3.setCellValue(boName);
    CellValueHolder cellValueHolder3 = new CellValueHolder(cell3);
    isrRelations.put(MessageAccess.getStringOrNull("businessObject.plural"), cellValueHolder3);
    // set service expectations
    EasyMock.expect(businessObjectServiceMock.findByNames(Sets.newHashSet(boName))).andReturn(Lists.newArrayList(bo1));

    InformationSystemDomain isd1 = new InformationSystemDomain();
    isd1.setName("ISD1");
    isd1.setId(Integer.valueOf(334101));
    String isdName = isd1.getNonHierarchicalName();
    cell4.setCellValue(isdName);
    CellValueHolder cellValueHolder4 = new CellValueHolder(cell4);
    isrRelations.put(MessageAccess.getStringOrNull("informationSystemDomain.plural"), cellValueHolder4);
    // set service expectations
    EasyMock.expect(informationSystemDomainServiceMock.findByNames(Sets.newHashSet(isdName))).andReturn(Lists.newArrayList(isd1));

    InfrastructureElement ie1 = new InfrastructureElement();
    ie1.setName("IE1");
    ie1.setId(Integer.valueOf(34101));
    String ieName = ie1.getNonHierarchicalName();
    cell5.setCellValue(ieName);
    CellValueHolder cellValueHolder5 = new CellValueHolder(cell5);
    isrRelations.put(MessageAccess.getStringOrNull("infrastructureElement.plural"), cellValueHolder5);
    // set service expectations
    EasyMock.expect(infrastructureElementServiceMock.findByNames(Sets.newHashSet(ieName))).andReturn(Lists.newArrayList(ie1));

    Project proj1 = new Project();
    proj1.setName("PROJ1");
    proj1.setId(Integer.valueOf(34101));
    String projName = proj1.getNonHierarchicalName();
    cell6.setCellValue(projName);
    CellValueHolder cellValueHolder6 = new CellValueHolder(cell6);
    isrRelations.put(MessageAccess.getStringOrNull("project.plural"), cellValueHolder6);
    // set service expectations
    EasyMock.expect(projectServiceMock.findByNames(Sets.newHashSet(projName))).andReturn(Lists.newArrayList(proj1));

    TechnicalComponentRelease tcr1 = new TechnicalComponentRelease();
    TechnicalComponent tc1 = new TechnicalComponent();
    tcr1.setTechnicalComponent(tc1);
    tcr1.setVersion("1.1");
    tcr1.setId(Integer.valueOf(34131));
    tc1.setName("TC1");
    tc1.setId(Integer.valueOf(646));
    String tcrName = tcr1.getNonHierarchicalName();
    cell7.setCellValue(tcrName);
    CellValueHolder cellValueHolder7 = new CellValueHolder(cell7);
    isrRelations.put(MessageAccess.getStringOrNull("technicalComponentRelease.plural"), cellValueHolder7);
    // set service expectations
    EasyMock.expect(technicalComponentReleaseServiceMock.findByNames(Sets.newHashSet(tcrName))).andReturn(Lists.newArrayList(tcr1));

    InformationSystemRelease isrBase = new InformationSystemRelease();
    InformationSystem isBase = new InformationSystem();
    isrBase.setInformationSystem(isBase);
    isrBase.setVersion("1.23");
    isBase.setName("IS_base");
    String isrBaseName = isrBase.getNonHierarchicalName();
    cell8.setCellValue(isrBaseName);
    CellValueHolder cellValueHolder8 = new CellValueHolder(cell8);
    isrRelations.put(MessageAccess.getStringOrNull("reporting.excel.header.informationSystemRelease.baseComponents"), cellValueHolder8);
    // set service expectations
    EasyMock.expect(informationSystemReleaseServiceMock.findByNames(Sets.newHashSet(isrBaseName))).andReturn(Lists.newArrayList(isrBase));

    EasyMock.expect(informationSystemReleaseServiceMock.saveOrUpdate(isr)).andReturn(isr).anyTimes();
    // and test
    EasyMock.replay(informationSystemReleaseServiceMock, businessObjectServiceMock, informationSystemDomainServiceMock,
        infrastructureElementServiceMock, projectServiceMock, technicalComponentReleaseServiceMock, informationSystemServiceMock, bbServiceLocator);
    excelImportService.createBuildingBlockRelations(isr, isrRelations, Locale.getDefault());
    EasyMock.verify(informationSystemReleaseServiceMock, businessObjectServiceMock, informationSystemDomainServiceMock,
        infrastructureElementServiceMock, projectServiceMock, technicalComponentReleaseServiceMock, informationSystemServiceMock);
    Assert.assertTrue(isr.getPredecessors().contains(isrPred));
    Assert.assertTrue(isr.getSuccessors().contains(isrSucc));
    Assert.assertTrue(isr.getBusinessObjects().contains(bo1));
    Assert.assertTrue(isr.getInformationSystemDomains().contains(isd1));
    Assert.assertTrue(isr.getInfrastructureElements().contains(ie1));
    Assert.assertTrue(isr.getProjects().contains(proj1));
    Assert.assertTrue(isr.getTechnicalComponentReleases().contains(tcr1));
    Assert.assertTrue(isr.getBaseComponents().contains(isrBase));

    // next test; reset and clean-up
    EasyMock.reset(informationSystemReleaseServiceMock, businessObjectServiceMock, informationSystemDomainServiceMock,
        infrastructureElementServiceMock, projectServiceMock, technicalComponentReleaseServiceMock, informationSystemServiceMock);
    isr.setPredecessors(new HashSet<InformationSystemRelease>());
    isr.setSuccessors(new HashSet<InformationSystemRelease>());
    isr.setBusinessObjects(new HashSet<BusinessObject>());
    isr.setInformationSystemDomains(new HashSet<InformationSystemDomain>());
    isr.setInfrastructureElements(new HashSet<InfrastructureElement>());
    isr.setProjects(new HashSet<Project>());
    isr.setTechnicalComponentReleases(new HashSet<TechnicalComponentRelease>());
    isr.setBaseComponents(new HashSet<InformationSystemRelease>());
    // feed in a relation for a non-existent BO
    EasyMock.expect(informationSystemReleaseServiceMock.findByNames(Sets.newHashSet(isrName1))).andReturn(new ArrayList<InformationSystemRelease>());
    EasyMock.expect(informationSystemReleaseServiceMock.findByNames(Sets.newHashSet(isrName2))).andReturn(new ArrayList<InformationSystemRelease>());
    EasyMock.expect(businessObjectServiceMock.findByNames(Sets.newHashSet(boName))).andReturn(new ArrayList<BusinessObject>());
    EasyMock.expect(informationSystemDomainServiceMock.findByNames(Sets.newHashSet(isdName))).andReturn(new ArrayList<InformationSystemDomain>());
    EasyMock.expect(infrastructureElementServiceMock.findByNames(Sets.newHashSet(ieName))).andReturn(new ArrayList<InfrastructureElement>());
    EasyMock.expect(projectServiceMock.findByNames(Sets.newHashSet(projName))).andReturn(new ArrayList<Project>());
    EasyMock.expect(technicalComponentReleaseServiceMock.findByNames(Sets.newHashSet(tcrName))).andReturn(new ArrayList<TechnicalComponentRelease>());
    EasyMock.expect(informationSystemReleaseServiceMock.findByNames(Sets.newHashSet(isrBaseName))).andReturn(
        new ArrayList<InformationSystemRelease>());

    EasyMock.expect(informationSystemReleaseServiceMock.saveOrUpdate(isr)).andReturn(isr).anyTimes();
    // and test
    EasyMock.replay(informationSystemReleaseServiceMock, businessObjectServiceMock, informationSystemDomainServiceMock,
        infrastructureElementServiceMock, projectServiceMock, technicalComponentReleaseServiceMock, informationSystemServiceMock);
    excelImportService.createBuildingBlockRelations(isr, isrRelations, Locale.getDefault());
    EasyMock.verify(informationSystemReleaseServiceMock, businessObjectServiceMock, informationSystemDomainServiceMock,
        infrastructureElementServiceMock, projectServiceMock, technicalComponentReleaseServiceMock, informationSystemServiceMock);
    Assert.assertTrue(isr.getPredecessors().isEmpty());
    Assert.assertTrue(isr.getSuccessors().isEmpty());
    Assert.assertTrue(isr.getBusinessObjects().isEmpty());
    Assert.assertTrue(isr.getInformationSystemDomains().isEmpty());
    Assert.assertTrue(isr.getInfrastructureElements().isEmpty());
    Assert.assertTrue(isr.getProjects().isEmpty());
    Assert.assertTrue(isr.getTechnicalComponentReleases().isEmpty());
    Assert.assertTrue(isr.getBaseComponents().isEmpty());

  }
}
