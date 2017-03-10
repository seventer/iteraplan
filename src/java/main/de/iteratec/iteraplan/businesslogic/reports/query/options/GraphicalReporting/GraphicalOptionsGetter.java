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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Line.LineOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.PortfolioOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.vbb.VbbOptionsBean;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Provides methods to get the graphical options bean from a ManageReportMemoryBean casted to the
 * subclass you want to have, including the necessary checks if it is valid to.
 */
public final class GraphicalOptionsGetter {

  private static final Logger LOGGER = Logger.getIteraplanLogger(GraphicalOptionsGetter.class);

  private GraphicalOptionsGetter() {
    // utility class
  }

  public static PortfolioOptionsBean getPortfolioOptions(ManageReportMemoryBean memBean) {
    IGraphicalExportBaseOptions options = memBean.getGraphicalOptions();
    if (options == null) {
      PortfolioOptionsBean portfolioOptionsBean = new PortfolioOptionsBean();
      memBean.setGraphicalOptions(portfolioOptionsBean);
      return portfolioOptionsBean;
    }
    if (options instanceof PortfolioOptionsBean) {
      return (PortfolioOptionsBean) options;
    }
    else {
      LOGGER.error("getPortfolioOptions: Wrong options bean: {0}", options.getClass().getName());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public static MasterplanOptionsBean getMasterplanOptions(ManageReportMemoryBean memBean) {
    IGraphicalExportBaseOptions options = memBean.getGraphicalOptions();
    if (options == null) {
      MasterplanOptionsBean masterplanOptionsBean = new MasterplanOptionsBean();
      memBean.setGraphicalOptions(masterplanOptionsBean);
      return masterplanOptionsBean;
    }
    if (options instanceof MasterplanOptionsBean) {
      return (MasterplanOptionsBean) options;
    }
    else {
      LOGGER.error("getMasterplanOptions: Wrong options bean: {0}", options.getClass().getName());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public static InformationFlowOptionsBean getInformationFlowOptions(ManageReportMemoryBean memBean) {
    IGraphicalExportBaseOptions options = memBean.getGraphicalOptions();
    if (options == null) {
      InformationFlowOptionsBean informationFlowOptions = new InformationFlowOptionsBean();
      memBean.setGraphicalOptions(informationFlowOptions);
      return informationFlowOptions;
    }
    if (options instanceof InformationFlowOptionsBean) {
      return (InformationFlowOptionsBean) options;
    }
    else {
      LOGGER.error("getInformationFlowOptions: Wrong options bean: {0}", options.getClass().getName());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public static ClusterOptionsBean getClusterOptions(ManageReportMemoryBean memBean) {
    IGraphicalExportBaseOptions options = memBean.getGraphicalOptions();
    if (options == null) {
      ClusterOptionsBean clusterOptions = new ClusterOptionsBean();
      memBean.setGraphicalOptions(clusterOptions);
      return clusterOptions;
    }
    if (options instanceof ClusterOptionsBean) {
      return (ClusterOptionsBean) options;
    }
    else {
      LOGGER.error("getClusterOptions: Wrong options bean: {0}", options.getClass().getName());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public static VbbOptionsBean getVbbOptions(ManageReportMemoryBean memBean) {
    IGraphicalExportBaseOptions options = memBean.getGraphicalOptions();
    if (options == null) {
      VbbOptionsBean vbbOptions = new VbbOptionsBean(memBean.getReportType());
      memBean.setGraphicalOptions(vbbOptions);
      return vbbOptions;
    }
    if (options instanceof VbbOptionsBean) {
      return (VbbOptionsBean) options;
    }
    else {
      LOGGER.error("getVbbOptions: Wrong options bean: {0}", options.getClass().getName());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public static LineOptionsBean getLineOptions(ManageReportMemoryBean memBean) {
    IGraphicalExportBaseOptions options = memBean.getGraphicalOptions();
    if (options == null) {
      LineOptionsBean lineOptions = new LineOptionsBean();
      memBean.setGraphicalOptions(lineOptions);
      return lineOptions;
    }
    if (options instanceof LineOptionsBean) {
      return (LineOptionsBean) options;
    }
    else {
      LOGGER.error("getLineOptions: Wrong options bean: {0}", options.getClass().getName());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }
  
  public static PieBarDiagramOptionsBean getPieBarOptions(ManageReportMemoryBean memBean) {
    IGraphicalExportBaseOptions options = memBean.getGraphicalOptions();
    if (options == null) {
      PieBarDiagramOptionsBean pieBarOptions = new PieBarDiagramOptionsBean();
      memBean.setGraphicalOptions(pieBarOptions);
      return pieBarOptions;
    }
    if (options instanceof PieBarDiagramOptionsBean) {
      return (PieBarDiagramOptionsBean) options;
    }
    else {
      LOGGER.error("getPieBarOptions: Wrong options bean: {0}", options.getClass().getName());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

}
