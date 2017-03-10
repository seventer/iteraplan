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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.xml.query.ClusterOptionsXML;
import de.iteratec.iteraplan.model.xml.query.ColumnEntryXML;
import de.iteratec.iteraplan.model.xml.query.InformationFlowOptionsXML;
import de.iteratec.iteraplan.model.xml.query.LineOptionsXML;
import de.iteratec.iteraplan.model.xml.query.MasterplanOptionsXML;
import de.iteratec.iteraplan.model.xml.query.PieBarOptionsXML;
import de.iteratec.iteraplan.model.xml.query.PortfolioOptionsXML;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.QueryResultXML;
import de.iteratec.iteraplan.model.xml.query.TabularOptionsXML;
import de.iteratec.iteraplan.model.xml.query.VbbOptionsXML;


/**
 * XML root dto for standard queries. Serves as root class for queries (un)marshalled to/from XML
 * files.
 */
@XmlRootElement(name = "report")
@XmlType(name = "report", propOrder = { "informationFlowOptions", "masterplanOptions", "portfolioOptions", "clusterOptions", "pieBarOptions",
    "vbbOptions", "tabularOptions", "lineOptions", "queryResults", "queryForms", "selectedResultIds", "version", "visibleColumns" })
public class ReportXML implements SerializedQuery<ManageReportMemoryBean> {

  private static final Logger LOGGER = Logger.getIteraplanLogger(ReportXML.class);

  @XmlEnum(String.class)
  public enum ReportTypeXML {
    GENERIC, PORTFOLIO, MASTERPLAN, INFORMATIONFLOW, CLUSTER, PIEBAR, VBB, COMPOSITE, LINE;
  }

  private final List<QueryResultXML> queryResults           = Lists.newArrayList();
  /** {@link #getQueryForms()} */
  @Deprecated
  private List<QueryFormXML>         queryForms             = new ArrayList<QueryFormXML>();
  /** {@link #getSelectedResultIds()} */
  @Deprecated
  private List<Integer>              selectedResultIds      = new ArrayList<Integer>();
  /** {@link #getReportType()} */
  private ReportTypeXML              reportType             = ReportTypeXML.GENERIC;
  /** {@link #getPortfolioOptions()} */
  private PortfolioOptionsXML        portfolioOptions       = null;
  /** {@link #getMasterplanOptions()} */
  private MasterplanOptionsXML       masterplanOptions      = null;
  /** {@link #getClusterOptions()} */
  private ClusterOptionsXML          clusterOptions         = null;
  /** {@link #getPieBarOptions()} */
  private PieBarOptionsXML           pieBarOptions          = null;
  /** {@link #getVbbOptions()} */
  private VbbOptionsXML              vbbOptions             = null;
  /** {@link #getLineOptions()} */
  private LineOptionsXML              lineOptions             = null;
  /** {@link #getVisibleColumns()} */
  private List<ColumnEntryXML>       visibleColumns         = new ArrayList<ColumnEntryXML>();

  private boolean                    reportUpdated          = false;

  /** {@link #getInformatioFlowOptions()} */
  private InformationFlowOptionsXML  informationFlowOptions = null;

  private TabularOptionsXML          tabularOptions         = null;

  /**
   * The queryforms of the saved query. queryForms[0] contains the main query form. The remaining
   * forms optionally contain query extensions.
   * For compatibility with older saved queries 
   * 
   * @return The queryForms
   */
  @XmlElementWrapper(name = "queryResults")
  @XmlElement(name = "queryResult")
  public List<QueryResultXML> getQueryResults() {
    return queryResults;
  }

  /**
   * The queryforms of the saved query. queryForms[0] contains the main query form. The remaining
   * forms optionally contain query extensions.
   * For compatibility with older saved queries 
   * 
   * @return The queryForms
   */
  @XmlElementWrapper(name = "queryForms")
  @XmlElement(name = "queryForm")
  @Deprecated
  public List<QueryFormXML> getQueryForms() {
    return queryForms;
  }

  /**
   * The BBs of the query result that have to be selected according to the saved query. Note: only
   * buildingBlocks that still exist will be selected!
   * For compatibility with older saved queries
   * @return The selected BBs
   */
  @XmlElementWrapper(name = "selectedResultIds")
  @XmlElement(name = "id")
  @Deprecated
  public List<Integer> getSelectedResultIds() {
    return selectedResultIds;
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

  /**
   * Custom options of the portfolio diagram. Only needed when persisting portfolio diagram
   * configurations
   * 
   * @return The portfolio diagram options
   */
  @XmlElement
  public PortfolioOptionsXML getPortfolioOptions() {
    return portfolioOptions;
  }

  /**
   * Custom options of the masterplan diagram. Only needed when persisting masterplan diagram
   * configurations
   * 
   * @return The masterplan diagram options
   */
  @XmlElement
  public MasterplanOptionsXML getMasterplanOptions() {
    return masterplanOptions;
  }

  /**
   * Custom options of the inforamtionFlow diagram. Only needed when persisting inforamtionFlow
   * diagram configurations
   * 
   * @return The inforamtionFlow diagram options
   */
  @XmlElement
  public InformationFlowOptionsXML getInformationFlowOptions() {
    return informationFlowOptions;
  }

  /**
   * Custom options of tabular reports. Only needed when persisting tabular report configurations
   * 
   * @return The tabular options
   */
  @XmlElement
  public TabularOptionsXML getTabularOptions() {
    return tabularOptions;
  }

  public void setTabularOptions(TabularOptionsXML tabularOptions) {
    this.tabularOptions = tabularOptions;
  }

  /**
   * @return lineOptions the lineOptions
   */
  @XmlElement
  public LineOptionsXML getLineOptions() {
    return lineOptions;
  }

  public void setLineOptions(LineOptionsXML lineOptions) {
    this.lineOptions = lineOptions;
  }

  @XmlElement
  public String getVersion() {
    return IteraplanProperties.getProperties().getBuildVersion();
  }

  public void setPortfolioOptions(PortfolioOptionsXML portfolioOptions) {
    this.portfolioOptions = portfolioOptions;
  }

  public void setMasterplanOptions(MasterplanOptionsXML masterplanOptions) {
    this.masterplanOptions = masterplanOptions;
  }

  public void setInformationFlowOptions(InformationFlowOptionsXML informationFlow) {
    this.informationFlowOptions = informationFlow;
  }

  @Deprecated
  public void setQueryForms(List<QueryFormXML> queryForms) {
    this.queryForms = queryForms;
  }

  @Deprecated
  public void setSelectedResultIds(List<Integer> selectedResultIds) {
    this.selectedResultIds = selectedResultIds;
  }

  public ClusterOptionsXML getClusterOptions() {
    return clusterOptions;
  }

  public void setClusterOptions(ClusterOptionsXML clusterOptions) {
    this.clusterOptions = clusterOptions;
  }

  public PieBarOptionsXML getPieBarOptions() {
    return pieBarOptions;
  }

  public void setPieBarOptions(PieBarOptionsXML pieBarOptions) {
    this.pieBarOptions = pieBarOptions;
  }

  /**
   * Custom options of the VBB configuration. Only needed when persisting VBB
   * diagram configurations
   * 
   * @return The VBB diagram options
   */
  @XmlElement
  public VbbOptionsXML getVbbOptions() {
    return vbbOptions;
  }

  public void setVbbOptions(VbbOptionsXML vbbOptions) {
    this.vbbOptions = vbbOptions;
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
    // the reportType does not have to be validated as enums are validated against the schema

    //    if ((queryForms == null || queryForms.isEmpty()) && (queryResults == null || queryResults.isEmpty())) {
    //      logError("No queryForms found");
    //    }
    for (QueryFormXML queryFormXML : queryForms) {
      queryFormXML.validate(locale);
    }
    for (QueryResultXML queryResultXML : queryResults) {
      queryResultXML.validate(locale);
    }

    switch (reportType) {
      case PORTFOLIO:
        if (portfolioOptions == null) {
          logError("Portfolio report does not have any options assigned");
        }
        portfolioOptions.validate(locale);
        break;
      case MASTERPLAN:
        if (masterplanOptions == null) {
          logError("Masterplan report does not have any options assigned");
        }
        masterplanOptions.validate(locale);
        break;
      case INFORMATIONFLOW:
        if (informationFlowOptions == null) {
          logError("InformationFlow report does not have any options assigned");
        }
        informationFlowOptions.validate(locale);
        break;
      case CLUSTER:
        if (clusterOptions == null) {
          logError("Cluster report does not have any options assigned");
        }
        clusterOptions.validate(locale);
        break;
      case PIEBAR:
        if (pieBarOptions == null) {
          logError("PieBar report does not have any options assigned");
        }
        pieBarOptions.validate(locale);
        break;
      case VBB:
        if (vbbOptions == null) {
          logError("VBB report does not have any options assigned");
        }
        vbbOptions.validate(locale);
        break;
      case LINE:
        if (lineOptions == null) {
          logError("Line report does not have any options assigned");
        }
        lineOptions.validate(locale);
        break;
      default:
    }
    // validate tabularOptions if set
    if (tabularOptions != null) {
      tabularOptions.validate(locale);
    }
  }

  public void initFrom(ManageReportMemoryBean memBean, Locale locale) {
    for (QueryResult queryResult : memBean.getQueryResults().values()) {
      QueryResultXML queryResultXML = new QueryResultXML();
      queryResultXML.initFrom(queryResult, locale);
      this.queryResults.add(queryResultXML);
    }
    if (memBean.getGraphicalOptions() != null) {
      if (ReportType.PORTFOLIO.equals(memBean.getReportType())) {
        // up to here attributes of the portfolio grafik are referenced by their ID.
        // Transform userdefined attributes to their name here as the integer ID might change
        initPortfolioOptions(memBean, locale);
        this.reportType = ReportTypeXML.PORTFOLIO;
      }
      // otherwise check if this memBean configures the masterplan diagram
      else if (ReportType.MASTERPLAN.equals(memBean.getReportType())) {
        this.reportType = ReportTypeXML.MASTERPLAN;
        initMasterplanOptions(memBean, locale);
      }
      else if (ReportType.CLUSTER.equals(memBean.getReportType())) {
        this.reportType = ReportTypeXML.CLUSTER;
        initClusterOptions(memBean, locale);
      }
      else if (ReportType.VBBCLUSTER.equals(memBean.getReportType()) || ReportType.TIMELINE.equals(memBean.getReportType())) {
        this.reportType = ReportTypeXML.VBB;
        initVbbOptions(memBean, locale);
      }
      else if (ReportType.PIE.equals(memBean.getReportType()) || ReportType.BAR.equals(memBean.getReportType())) {
        this.reportType = ReportTypeXML.PIEBAR;
        initPieBarOptions(memBean, locale);
      }
      // otherwise check if this memBean configures the informationFlow diagram
      else if (ReportType.INFORMATIONFLOW.equals(memBean.getReportType())) {
        this.reportType = ReportTypeXML.INFORMATIONFLOW;
        initInformationFlowOptions(memBean, locale);
      }
      else if (ReportType.LINE.equals(memBean.getReportType())) {
        this.reportType = ReportTypeXML.LINE;
        initLineOptions(memBean, locale);
      }
    }
    // if this is a tabular report
    if (ReportType.TABVIEW.equals(memBean.getReportType())) {
      initTabularOptions(memBean, locale);
    }

    // view configuration
    initColumnEntries(memBean, locale);
  }

  private void initTabularOptions(ManageReportMemoryBean memBean, Locale locale) {
    tabularOptions = new TabularOptionsXML();
    tabularOptions.initFrom(memBean.getTabularOptions(), locale);

  }

  /**
   * Initialises the XML bean of the portfolio options.
   * 
   * @param memBean
   *          The current report membean
   * @param locale
   *          The current user's locale
   */
  private void initPortfolioOptions(ManageReportMemoryBean memBean, Locale locale) {
    this.portfolioOptions = new PortfolioOptionsXML();
    portfolioOptions.initFrom(GraphicalOptionsGetter.getPortfolioOptions(memBean), locale);
  }

  /**
   * Initialises the XML bean of the masterplan options.
   * 
   * @param memBean
   *          The current report membean
   * @param locale
   *          The current user's locale
   */
  private void initMasterplanOptions(ManageReportMemoryBean memBean, Locale locale) {
    this.masterplanOptions = new MasterplanOptionsXML();
    masterplanOptions.initFrom(GraphicalOptionsGetter.getMasterplanOptions(memBean), locale);
  }

  private void initClusterOptions(ManageReportMemoryBean memBean, Locale locale) {
    this.clusterOptions = new ClusterOptionsXML();
    clusterOptions.initFrom(GraphicalOptionsGetter.getClusterOptions(memBean), locale);
  }

  private void initVbbOptions(ManageReportMemoryBean memBean, Locale locale) {
    this.vbbOptions = new VbbOptionsXML();
    vbbOptions.initFrom(GraphicalOptionsGetter.getVbbOptions(memBean), locale);
  }

  private void initPieBarOptions(ManageReportMemoryBean memBean, Locale locale) {
    this.pieBarOptions = new PieBarOptionsXML();
    pieBarOptions.initFrom(GraphicalOptionsGetter.getPieBarOptions(memBean), locale);
  }
  
  private void initLineOptions(ManageReportMemoryBean memBean, Locale locale) {
    this.lineOptions = new LineOptionsXML();
    lineOptions.initFrom(GraphicalOptionsGetter.getLineOptions(memBean), locale);
  }

  private void initColumnEntries(ManageReportMemoryBean memBean, Locale locale) {
    this.visibleColumns = new ArrayList<ColumnEntryXML>();
    if (memBean.getViewConfiguration() != null && memBean.getViewConfiguration().getVisibleColumns() != null) {
      List<ColumnEntry> configVisibleColumns = memBean.getViewConfiguration().getVisibleColumns();
      for (ColumnEntry configColumnEntry : configVisibleColumns) {
        ColumnEntryXML columnEntryXML = new ColumnEntryXML();
        columnEntryXML.initFrom(configColumnEntry, locale);
        this.visibleColumns.add(columnEntryXML);
      }
    }
  }

  /**
   * Initialises the information flow options XML bean.
   * @param memBean
   *          The current report membean
   * @param locale
   *          The current user's locale
   */
  private void initInformationFlowOptions(ManageReportMemoryBean memBean, Locale locale) {
    this.informationFlowOptions = new InformationFlowOptionsXML();
    informationFlowOptions.initFrom(GraphicalOptionsGetter.getInformationFlowOptions(memBean), locale);
  }

  private void logError(String message) {
    LOGGER.error("Error during validation of query definition: {0}", message);
    throw new IteraplanTechnicalException(IteraplanErrorMessages.ILLEGAL_XML_FILE_DATA);
  }

  @XmlElementWrapper(name = "visibleColumns")
  @XmlElement(name = "visibleColumn")
  public List<ColumnEntryXML> getVisibleColumns() {
    return visibleColumns;
  }

  public void setVisibleColumns(List<ColumnEntryXML> visibleColumns) {
    this.visibleColumns = visibleColumns;
  }

  public Type<?> getQueryResultType() {
    if (queryResults != null && !queryResults.isEmpty()) {
      return queryResults.get(0).getQueryForms().get(0).getType();
    }
    else if (queryForms != null && !queryForms.isEmpty()) {
      // for compatibility with older saved queries
      return queryForms.get(0).getType();
    }
    else {
      return null;
    }
  }

}
