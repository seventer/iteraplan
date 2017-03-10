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
package de.iteratec.iteraplan.model.queries;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.iteratec.iteraplan.common.Constants;


public enum ReportType {
  INFORMATIONFLOW(Constants.REPORTS_EXPORT_GRAPHICAL_INFORMATIONFLOW, Constants.GRAPHICAL_EXPORT_INFORMATIONFLOW_DIAGRAM_TITLE,
      "graphicalreporting/informationflowdiagram"), PORTFOLIO(Constants.REPORTS_EXPORT_GRAPHICAL_PORTFOLIO,
      Constants.GRAPHICAL_EXPORT_PORTFOLIO_DIAGRAM_TITLE, "graphicalreporting/portfoliodiagram"), CLUSTER(Constants.REPORTS_EXPORT_GRAPHICAL_CLUSTER,
      Constants.GRAPHICAL_EXPORT_CLUSTER_DIAGRAM_TITLE, "graphicalreporting/clusterdiagram"), MASTERPLAN(
      Constants.REPORTS_EXPORT_GRAPHICAL_MASTERPLAN, Constants.GRAPHICAL_EXPORT_MASTERPLAN_DIAGRAM_TITLE, "graphicalreporting/masterplandiagram"), LANDSCAPE(
      Constants.REPORTS_EXPORT_GRAPHICAL_LANDSCAPE, Constants.GRAPHICAL_EXPORT_LANDSCAPE_DIAGRAM_TITLE, "graphicalreporting/landscapediagram"), COMPOSITE(
      Constants.REPORTS_EXPORT_GRAPHICAL_COMPOSITE, Constants.GRAPHICAL_EXPORT_COMPOSITE_DIAGRAM_TITLE, "graphicalreporting/compositediagram"), PIE(
      Constants.REPORTS_EXPORT_GRAPHICAL_PIE, Constants.GRAPHICAL_EXPORT_PIE_DIAGRAM_TITLE, "graphicalreporting/piebardiagram"), BAR(
      Constants.REPORTS_EXPORT_GRAPHICAL_BAR, Constants.GRAPHICAL_EXPORT_BAR_DIAGRAM_TITLE, "graphicalreporting/piebardiagram"), VBBCLUSTER(
      Constants.REPORTS_EXPORT_GRAPHICAL_VBB_CLUSTER, Constants.GRAPHICAL_EXPORT_VBBCLUSTER_DIAGRAM_TITLE, "graphicalreporting/vbbclusterdiagram"), TIMELINE(
      Constants.REPORTS_EXPORT_GRAPHICAL_TIMELINE, Constants.GRAPHICAL_EXPORT_TIMELINE_DIAGRAM_TITLE, "graphicalreporting/timelinediagram"), LINE(
      Constants.REPORTS_EXPORT_GRAPHICAL_LINE, Constants.GRAPHICAL_EXPORT_LINE_DIAGRAM_TITLE, "graphicalreporting/linediagram"), MATRIX(
      Constants.REPORTS_EXPORT_GRAPHICAL_MATRIX, "graphicalreporting/matrixdiagram"), NEIGHBORHOOD(Constants.REPORTS_EXPORT_GRAPHICAL_NEIGHBORHOOD), TABVIEW(
      Constants.REPORTS_EXPORT_TABVIEW),

  // the following fields are duplicated from TypeOfBuildingBlockType-enum
  // theirs insertion is required to unchange the logic of hibernate, also the application logic.
  ARCHITECTURALDOMAIN("architecturalDomain.singular", true), BUSINESSDOMAIN("businessDomain.singular", true), BUSINESSFUNCTION(
      "global.business_function", true), BUSINESSOBJECT("businessObject.singular", true), BUSINESSPROCESS("businessProcess.singular", true), BUSINESSMAPPING(
      "businessMapping.singular", true), BUSINESSUNIT("businessUnit.singular", true), INFORMATIONSYSTEM("informationSystem.singular"), INFORMATIONSYSTEMRELEASE(
      "informationSystemRelease.singular", true), INFORMATIONSYSTEMDOMAIN("informationSystemDomain.singular", true), INFORMATIONSYSTEMINTERFACE(
      "interface.singular", true), INFRASTRUCTUREELEMENT("infrastructureElement.singular", true), PRODUCT("global.product", true), PROJECT(
      "project.singular", true), TECHNICALCOMPONENT("technicalComponent.singular"), TECHNICALCOMPONENTRELEASE("technicalComponentRelease.singular",
      true), TRANSPORT("global.transport"), TCR2IEASSOCIATION("technicalComponentRelease.association.infrastructureElement"), ISR2BOASSOCIATION(
      "informationSystemRelease.association.businessObject"),
  // Dummy for the presentation tier
  DUMMY(""),

  // the 2 following fields are used interchangeable with the 2 same named constants in Constants class.
  //REPORTS_EXPORT_CLUSTER_MODE_ATTRIBUTE("graphicalExport.cluster.mode.attributes"),
  HTML("reports_exportHTML");

  /**
   *  Collection of all report-types which are diagrams
   *  This is mainly used for the Dashboard Templates as choice for report types.
   */
  public static final List<ReportType> GRAPHICAL_REPORTING_TYPES = ImmutableList.of(INFORMATIONFLOW, PORTFOLIO, CLUSTER, MASTERPLAN, LANDSCAPE, PIE,
                                                                     BAR, VBBCLUSTER, TIMELINE, LINE, MATRIX);

  public static final List<ReportType> TABLUAR_REPORTING_TYPES   = ImmutableList.of(ARCHITECTURALDOMAIN, BUSINESSDOMAIN, BUSINESSFUNCTION,
                                                                     BUSINESSOBJECT, BUSINESSPROCESS, BUSINESSMAPPING, BUSINESSUNIT,
                                                                     INFORMATIONSYSTEM, INFORMATIONSYSTEMRELEASE, INFORMATIONSYSTEMDOMAIN,
                                                                     INFORMATIONSYSTEMINTERFACE, INFRASTRUCTUREELEMENT, PRODUCT, PROJECT,
                                                                     TECHNICALCOMPONENT, TECHNICALCOMPONENTRELEASE);

  private final String                 stringValue;

  private final String                 titleProperty;

  private final String                 flowMapping;

  /**
   * Enum constructor
   */
  ReportType(String stringValue) {
    this.stringValue = stringValue;
    this.titleProperty = "";
    this.flowMapping = "";
  }

  /**
   * extended Constructor
   */
  ReportType(String stringValue, String titleProperty) {
    this.stringValue = stringValue;
    this.titleProperty = titleProperty;
    this.flowMapping = "";
  }

  ReportType(String stringValue, String titleProperty, String flowMapping) {
    this.stringValue = stringValue;
    this.titleProperty = titleProperty;
    this.flowMapping = flowMapping;
  }

  ReportType(String stringValue, boolean reportType) {
    this(stringValue, reportType ? "global.report.tabular" : "", reportType ? "tabularreporting" : "");
  }

  public String getValue() {
    return stringValue;
  }

  public String getTitleProperty() {
    return titleProperty;
  }

  public String getFlowMapping() {
    return flowMapping;
  }

  public boolean isGraphicalReport() {
    return GRAPHICAL_REPORTING_TYPES.contains(this);
  }

  public boolean isTabularReport() {
    return TABLUAR_REPORTING_TYPES.contains(this);
  }

  @Override
  public String toString() {
    return this.getValue();
  }

  public static ReportType fromValue(String stringValue) {
    if (stringValue != null) {
      for (ReportType t : values()) {
        if (stringValue.equalsIgnoreCase(t.stringValue)) {
          return t;
        }
      }
    }
    return null;
  }
}
