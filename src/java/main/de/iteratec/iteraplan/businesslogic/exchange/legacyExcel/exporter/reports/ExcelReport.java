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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports;

import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ArchitecturalDomainExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.BusinessDomainExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.BusinessFunctionExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.BusinessMappingExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.BusinessObjectExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.BusinessProcessExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.BusinessUnitExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.InformationSystemDomainExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.InformationSystemInterfaceExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.InformationSystemReleaseExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.InfrastructureElementExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ProductExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ProjectExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.TechnicalComponentReleaseExcelSheet;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * This class contains general features for all subclasses that represent excel reports for
 * different types of {@link de.iteratec.iteraplan.model.BuildingBlock}s.<br/>
 * Additionally this class deals with all required permission issues, so all subclasses are only
 * responsible for combination of right excel sheets.
 */
public abstract class ExcelReport {

  protected static final Logger    LOGGER = Logger.getIteraplanLogger(ExcelReport.class);

  private ExportWorkbook           context;
  private BuildingBlockTypeDAO     bbtDao;
  private AttributeTypeDAO         attributeTypeDAO;
  private ExcelAdditionalQueryData queryData;
  private TypeOfBuildingBlock      currentBBType;
  private String                   serverURL;

  /**
   * Stores the given parameters in class members for further processing. Also checks if the current
   * user has permission to create the current excel report. If the check fails an
   * {@link de.iteratec.iteraplan.common.error.IteraplanBusinessException IteraplanBusinessException} is thrown.
   * 
   * @param context
   *          the context used to store the created excel report
   * @param attributeTypeDAO
   *          required for handling of corresponding attribute types
   * @param queryData
   *          additional data for excel report
   * @param bbType
   *          the type of the current {@link de.iteratec.iteraplan.model.BuildingBlock} used for
   *          differentiation of sheets' contents
   * @param serverURL
   *          the server url to be used for creation of links within the current sheet, must not be
   *          <code>null</code>.
   * @throws de.iteratec.iteraplan.common.error.IteraplanBusinessException
   *           if the current user has no permission to create this excel report
   */
  @SuppressWarnings("hiding")
  protected void init(ExportWorkbook context, BuildingBlockTypeDAO bbtDao, AttributeTypeDAO attributeTypeDAO, ExcelAdditionalQueryData queryData,
                      TypeOfBuildingBlock bbType, String serverURL) {
    UserContext.getCurrentPerms().assureFunctionalPermission(bbType);
    if (serverURL == null) {
      LOGGER.info("Missing server URL for creating of an excel report. No hyperlinks will be added.");
    }
    this.context = context;
    this.bbtDao = bbtDao;
    this.attributeTypeDAO = attributeTypeDAO;
    this.queryData = queryData;
    this.currentBBType = bbType;
    this.serverURL = serverURL;
  }

  /**
   * @return the current context
   */
  protected ExportWorkbook getContext() {
    return this.context;
  }

  /**
   * @return the corresponding {@link AttributeTypeDAO}-instance
   */
  protected AttributeTypeDAO getAttributeTypeDAO() {
    return this.attributeTypeDAO;
  }

  /**
   * @return the corresponding additional query data
   */
  protected ExcelAdditionalQueryData getQueryData() {
    return this.queryData;
  }

  /**
   * Creates a new sheet for {@link TechnicalComponentRelease}s. Before creation a test for required
   * permission is made. No sheet is added if the test fails.
   * 
   * @param allTCRs
   *          the contents to be added
   */
  protected void addTCRSheet(Set<TechnicalComponentRelease> allTCRs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE)) {
      TechnicalComponentReleaseExcelSheet tcrSheet = new TechnicalComponentReleaseExcelSheet(allTCRs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, true), this.getQueryData(),
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE), this.serverURL);
      tcrSheet.createSheet();
      LOGGER.debug("Added technical components' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted technical components' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link InformationSystemRelease}s. Before creation a test for required
   * permission is made. No sheet is added if the test fails.
   * 
   * @param allISRs
   *          the elements to be added
   */
  protected void addISRSheet(Set<InformationSystemRelease> allISRs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)) {
      //add information system release list
      List<AttributeType> activatedAttributeTypesForTobb = this.getAttributeTypeDAO().getAttributeTypesForTypeOfBuildingBlock(
          TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, true);
      BuildingBlockType buildingBlockTypeByType = bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
      InformationSystemReleaseExcelSheet isrSheet = new InformationSystemReleaseExcelSheet(allISRs, this.getContext(),
          activatedAttributeTypesForTobb, this.getQueryData(), buildingBlockTypeByType, this.serverURL);
      isrSheet.createSheet();
      LOGGER.debug("Added information system releases' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted information system releases' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link InformationSystemDomain}s. Before creation a test for required
   * permission is made. No sheet is added if the test fails.
   * 
   * @param allISDs
   *          the elements to be added
   */
  protected void addISDSheet(Set<InformationSystemDomain> allISDs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN)) {
      InformationSystemDomainExcelSheet isdSheet = new InformationSystemDomainExcelSheet(allISDs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN), this.serverURL);
      isdSheet.createSheet();
      LOGGER.debug("Added information system domains' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted information system domains' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link ArchitecturalDomain}s. Before creation a test for required
   * permission is made. No sheet is added if the test fails.
   * 
   * @param allADs
   *          the elements to be added
   */
  protected void addADSheet(Set<ArchitecturalDomain> allADs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.ARCHITECTURALDOMAIN)) {
      ArchitecturalDomainExcelSheet adSheet = new ArchitecturalDomainExcelSheet(allADs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN), this.serverURL);
      adSheet.createSheet();
      LOGGER.debug("Added architectural domains' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted architectural domains' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link InfrastructureElement}s. Before creation a test for required
   * permission is made. No sheet is added if the test fails.
   * 
   * @param allIEs
   *          the elements to be added
   */
  protected void addIESheet(Set<InfrastructureElement> allIEs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT)) {
      InfrastructureElementExcelSheet ieSheet = new InfrastructureElementExcelSheet(allIEs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT), this.serverURL);
      ieSheet.createSheet();
      LOGGER.debug("Added infrastructure elements' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted infrastructure elements' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link InformationSystemInterface}s. Before creation a test for
   * required permission is made. No sheet is added if the test fails.
   * 
   * @param allISIs
   *          the elements to be added
   */
  protected void addISISheet(Set<InformationSystemInterface> allISIs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE)) {
      InformationSystemInterfaceExcelSheet isiSheet = new InformationSystemInterfaceExcelSheet(allISIs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE), this.serverURL);
      isiSheet.createSheet();
      LOGGER.debug("Added information system interfaces' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted information system interfaces' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link Project}s. Before creation a test for required permission is
   * made. No sheet is added if the test fails.
   * 
   * @param allProjects
   *          the elements to be added
   */
  protected void addProjectSheet(Set<Project> allProjects) {
    if (this.checkUserPermission(TypeOfBuildingBlock.PROJECT)) {
      ProjectExcelSheet projectSheet = new ProjectExcelSheet(allProjects, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.PROJECT, true), this.getQueryData(),
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.PROJECT), this.serverURL);
      projectSheet.createSheet();
      LOGGER.debug("Added projects' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted projects' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link Product}s. Before creation a test for required permission is
   * made. No sheet is added if the test fails.
   * 
   * @param allProducts
   *          the elements to be added
   */
  protected void addProductSheet(Set<Product> allProducts) {
    if (this.checkUserPermission(TypeOfBuildingBlock.PRODUCT)) {
      ProductExcelSheet productSheet = new ProductExcelSheet(allProducts, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.PRODUCT, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.PRODUCT), this.serverURL);
      productSheet.createSheet();
      LOGGER.debug("Added products' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted products' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link BusinessFunction}s. Before creation a test for required
   * permission is made. No sheet is added if the test fails.
   * 
   * @param allBFs
   *          the elements to be added
   */
  protected void addBFSheet(Set<BusinessFunction> allBFs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.BUSINESSFUNCTION)) {
      BusinessFunctionExcelSheet bfSheet = new BusinessFunctionExcelSheet(allBFs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.BUSINESSFUNCTION, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSFUNCTION), this.serverURL);
      bfSheet.createSheet();
      LOGGER.debug("Added business functions' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted business functions' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link BusinessUnit}s. Before creation a test for required permission
   * is made. No sheet is added if the test fails.
   * 
   * @param allBUs
   *          the elements to be added
   */
  protected void addBUSheet(Set<BusinessUnit> allBUs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.BUSINESSUNIT)) {
      BusinessUnitExcelSheet buSheet = new BusinessUnitExcelSheet(allBUs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.BUSINESSUNIT, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSUNIT), this.serverURL);
      buSheet.createSheet();
      LOGGER.debug("Added business units' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted business units' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link BusinessObject}s. Before creation a test for required permission
   * is made. No sheet is added if the test fails.
   * 
   * @param allBOs
   *          the elements to be added
   */
  protected void addBOSheet(Set<BusinessObject> allBOs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.BUSINESSOBJECT)) {
      BusinessObjectExcelSheet boSheet = new BusinessObjectExcelSheet(allBOs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.BUSINESSOBJECT, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSOBJECT), this.serverURL);
      boSheet.createSheet();
      LOGGER.debug("Added business objects' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted business objects' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link BusinessDomain}s. Before creation a test for required permission
   * is made. No sheet is added if the test fails.
   * 
   * @param allBDs
   *          the elements to be added
   */
  protected void addBDSheet(Set<BusinessDomain> allBDs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.BUSINESSDOMAIN)) {
      BusinessDomainExcelSheet bdSheet = new BusinessDomainExcelSheet(allBDs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.BUSINESSDOMAIN, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSDOMAIN), this.serverURL);
      bdSheet.createSheet();
      LOGGER.debug("Added business domains' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted business domains' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link BusinessProcess}s. Before creation a test for required
   * permission is made. No sheet is added if the test fails.
   * 
   * @param allBPs
   *          the elements to be added
   */
  protected void addBPSheet(Set<BusinessProcess> allBPs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.BUSINESSPROCESS)) {
      BusinessProcessExcelSheet bpSheet = new BusinessProcessExcelSheet(allBPs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.BUSINESSPROCESS, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS), this.serverURL);
      bpSheet.createSheet();
      LOGGER.debug("Added business processes' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted business processes' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Creates a new sheet for {@link BusinessMapping}s. Before creation a test for required
   * permission is made. No sheet is added if the test fails.
   * 
   * @param allBMs
   *          the elements to be added
   */
  protected void addBMSheet(Set<BusinessMapping> allBMs) {
    if (this.checkUserPermission(TypeOfBuildingBlock.BUSINESSMAPPING)) {
      BusinessMappingExcelSheet bmSheet = new BusinessMappingExcelSheet(allBMs, this.getContext(), this.getAttributeTypeDAO()
          .getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.BUSINESSMAPPING, true), null,
          bbtDao.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSMAPPING), this.serverURL);
      bmSheet.createSheet();
      LOGGER.debug("Added business mappings' sheet for {0}-report", this.currentBBType);
    }
    else {
      LOGGER.debug("Omitted business mappings' sheet for {0}-report (user does not have the required permission)", this.currentBBType);
    }
  }

  /**
   * Checks if the current user has a permission for the given <code>bbType</code>.
   * 
   * @param bbType
   *          the {@link TypeOfBuildingBlock} in question
   * @return <code>true</code> if the current user has the permission, <code>false</code> otherwise
   */
  private boolean checkUserPermission(TypeOfBuildingBlock bbType) {
    return UserContext.getCurrentPerms().getUserHasBbTypeFunctionalPermission(bbType.toString());
  }

  /**
   * This method must be implemented by all subclasses and is supposed to fill all required excel
   * contents.
   * 
   * @return a {@link ExportWorkbook} with all expected information
   */
  public abstract ExportWorkbook createReport();

}
