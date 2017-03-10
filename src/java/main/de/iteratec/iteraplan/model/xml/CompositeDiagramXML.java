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
package de.iteratec.iteraplan.model.xml;

import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import de.iteratec.iteraplan.businesslogic.reports.query.options.CompositeReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Composite.CompositeDiagramOptionsBean;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.xml.ReportXML.ReportTypeXML;


/**
 * XML root dto for standard queries. Serves as root class for queries (un)marshalled to/from XML
 * files.
 */
@XmlRootElement(name = "compositeDiagram")
@XmlType(name = "compositeDiagram", propOrder = { "graphicFormat", "partReports", "showSavedQueryInfo", "version" })
public class CompositeDiagramXML implements SerializedQuery<CompositeReportMemoryBean> {

  private List<Integer> partReports        = CollectionUtils.arrayList();

  /** {@link #getReportType()} */
  private ReportTypeXML reportType         = ReportTypeXML.COMPOSITE;
  private String        graphicFormat      = Constants.REPORTS_EXPORT_GRAPHICAL_SVG;
  private boolean       showSavedQueryInfo = false;

  private boolean       reportUpdated      = false;

  /**
   * The report parts of the composite diagrams.
   * 
   * @return The list of part report
   */
  @XmlElementWrapper(name = "partReports")
  @XmlElement(name = "partReportId")
  public List<Integer> getPartReports() {
    return partReports;
  }

  public void setPartReports(List<Integer> idList) {
    this.partReports = idList;
  }

  @XmlElement(name = "graphicFormat")
  public String getGraphicFormat() {
    return graphicFormat;
  }

  public void setGraphicFormat(String graphicFormat) {
    this.graphicFormat = graphicFormat;
  }

  public void setShowSavedQueryInfo(boolean showSavedQueryInfo) {
    this.showSavedQueryInfo = showSavedQueryInfo;
  }

  @XmlElement
  public boolean isShowSavedQueryInfo() {
    return showSavedQueryInfo;
  }

  /**
   * The type of the report. Usually set to {@link ReportTypeXML#GENERIC}. If the saved report holds
   * a diagram, set to the according type (see {@link ReportTypeXML}).
   * 
   * @return The report type
   */
  @XmlAttribute(required = true)
  public ReportTypeXML getReportType() {
    return reportType;
  }

  public void setReportType(ReportTypeXML reportType) {
    this.reportType = reportType;
  }

  @XmlElement
  public String getVersion() {
    return IteraplanProperties.getProperties().getBuildVersion();
  }

  @XmlTransient
  public boolean isReportUpdated() {
    return reportUpdated;
  }

  public void setReportUpdated(boolean reportUpdated) {
    this.reportUpdated = reportUpdated;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.SerializedQuery#validate(java.util.Locale)
   */
  public void validate(Locale locale) {
    //nothing to do
  }

  public void initFrom(CompositeReportMemoryBean memBean, Locale locale) {
    CompositeDiagramOptionsBean compositeOptions = memBean.getCompositeOptions();

    this.graphicFormat = compositeOptions.getSelectedGraphicFormat();
    this.showSavedQueryInfo = compositeOptions.isShowSavedQueryInfo();

    this.partReports = CollectionUtils.arrayList();
    for (Integer partReportSavedQueryId : compositeOptions.getSelectedSavedQueryIds()) {
      partReports.add(partReportSavedQueryId);
    }
  }

}
