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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.ArchitecturalDomainExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.BusinessDomainExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.BusinessFunctionExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.BusinessObjectExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.BusinessProcessExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.BusinessUnitExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.ExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.InformationSystemDomainExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.InformationSystemInterfaceExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.InformationSystemReleaseExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.InfrastructureElementExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.ProductExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.ProjectExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.TechnicalComponentReleaseExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO.SuccessionContainer;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * This class provides an implementation of {@link ExcelExportService}.
 */
public class ExcelExportServiceImpl implements ExcelExportService {

  private BuildingBlockTypeDAO buildingBlockTypeDao;
  private AttributeTypeDAO     attributeTypeDao;

  /** {@inheritDoc} */
  public <T extends BuildingBlock> ExportWorkbook getBuildingBlockReport(Locale locale, ExcelAdditionalQueryData queryData, List<T> allBbs,
                                                                         TypeOfBuildingBlock tob, String serverUrl, TemplateType templateType) {
    return getBuildingBlockReport(locale, queryData, allBbs, tob, serverUrl, templateType, "");
  }

  /** {@inheritDoc} */
  public <T extends BuildingBlock> ExportWorkbook getBuildingBlockReport(Locale locale, ExcelAdditionalQueryData queryData, List<T> allBbs,
                                                                         TypeOfBuildingBlock tob, String serverUrl, TemplateType templateType,
                                                                         String templateFileName) {
    ExportWorkbook context = new ExportWorkbook(locale, templateType, templateFileName);
    ExcelReport report = createExcelReportForType(context, queryData, new HashSet<T>(allBbs), tob, serverUrl);

    report.createReport();
    context.adjustDefaultSettingsSheet();
    return context;
  }

  /**
   * Factory method that creates an appropriate Excel report for the building block elements that
   * are passed in.
   * 
   * @param context
   *          The Excel workbook context to work with.
   * @param queryData
   *          some additional information of the query to include in the report
   * @param allBbs
   *          a set of same-type building blocks which shall be reported
   * @param tob
   *          The building block type descriptor that reflects the contents of allBbs. This value
   *          MUST correspond to the (class) type of the list elements.
   * @param serverURL
   *          the server url to be used for creation of links within the current report
   * @return an instance of concrete subtype of {@link ExcelReport}, according to the
   *         <code>tob</code> parameter.
   */
  @SuppressWarnings("unchecked")
  private ExcelReport createExcelReportForType(ExportWorkbook context, ExcelAdditionalQueryData queryData, Set<? extends BuildingBlock> allBbs,
                                               TypeOfBuildingBlock tob, String serverURL) {

    switch (tob) {
      case ARCHITECTURALDOMAIN:
        return new ArchitecturalDomainExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<ArchitecturalDomain>) allBbs, queryData,
            serverURL);
      case BUSINESSDOMAIN:
        return new BusinessDomainExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<BusinessDomain>) allBbs, queryData, serverURL);
      case BUSINESSFUNCTION:
        return new BusinessFunctionExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<BusinessFunction>) allBbs, queryData, serverURL);
      case BUSINESSOBJECT:
        return new BusinessObjectExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<BusinessObject>) allBbs, queryData, serverURL);
      case BUSINESSPROCESS:
        return new BusinessProcessExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<BusinessProcess>) allBbs, queryData, serverURL);
      case BUSINESSUNIT:
        return new BusinessUnitExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<BusinessUnit>) allBbs, queryData, serverURL);
      case INFORMATIONSYSTEMDOMAIN:
        return new InformationSystemDomainExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<InformationSystemDomain>) allBbs,
            queryData, serverURL);
      case INFORMATIONSYSTEMINTERFACE:
        return new InformationSystemInterfaceExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<InformationSystemInterface>) allBbs,
            queryData, serverURL);
      case INFORMATIONSYSTEMRELEASE:
        return new InformationSystemReleaseExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<InformationSystemRelease>) allBbs,
            queryData, serverURL);
      case INFRASTRUCTUREELEMENT:
        return new InfrastructureElementExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<InfrastructureElement>) allBbs, queryData,
            serverURL);
      case PRODUCT:
        return new ProductExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<Product>) allBbs, queryData, serverURL);
      case PROJECT:
        return new ProjectExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<Project>) allBbs, queryData, serverURL);
      case TECHNICALCOMPONENTRELEASE:
        return new TechnicalComponentReleaseExcelReport(context, buildingBlockTypeDao, attributeTypeDao, (Set<TechnicalComponentRelease>) allBbs,
            queryData, serverURL);
      default:
        // unsupported type of building block
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  public ExportWorkbook getReleaseSuccessionReport(Locale locale, List<SuccessionContainer<? extends Sequence<?>>> releaseSuccession,
                                                   TypeOfBuildingBlock releaseType, String serverURL, TemplateType templateType) {
    List<BuildingBlock> releases = new ArrayList<BuildingBlock>();
    for (SuccessionContainer<? extends Sequence> succContainer : releaseSuccession) {
      releases.add((BuildingBlock) succContainer.getRelease());
    }

    return getBuildingBlockReport(locale, null, releases, releaseType, serverURL, templateType);
  }

  public void setAttributeTypeDAO(AttributeTypeDAO attributeTypeDao) {
    this.attributeTypeDao = attributeTypeDao;
  }

  public void setBuildingBlockTypeDAO(BuildingBlockTypeDAO buildingBlockTypeDao) {
    this.buildingBlockTypeDao = buildingBlockTypeDao;
  }
}
