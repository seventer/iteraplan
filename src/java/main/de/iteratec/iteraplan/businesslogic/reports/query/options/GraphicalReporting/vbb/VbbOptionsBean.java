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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.vbb;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.MixedColorCodingDecorator.ContinuousColorCodingDecorator;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.RecursiveCluster;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


public class VbbOptionsBean extends GraphicalExportBaseOptions {
  /** Serialization version */
  private static final long                                 serialVersionUID      = 7905352527704407646L;

  private static final Map<ReportType, Map<String, String>> DEFAULT_CONFIG_VALUES = Maps.newHashMap();
  private static final Map<ReportType, List<String>>        QUERY_NAMES           = Maps.newHashMap();
  static {
    Map<String, String> clusterDefault = Maps.newHashMap();
    clusterDefault.put(RecursiveCluster.CLASS_OUTER + "." + RecursiveCluster.VV_FILL_COLOR, RecursiveCluster.DEFAULT_OUTER_FILL_COLOR);
    clusterDefault.put(RecursiveCluster.CLASS_OUTER + "." + ContinuousColorCodingDecorator.VV_MIN_COLOR,
        ContinuousColorCodingDecorator.DEFAULT_MIN_COLOR);
    clusterDefault.put(RecursiveCluster.CLASS_OUTER + "." + ContinuousColorCodingDecorator.VV_MAX_COLOR,
        ContinuousColorCodingDecorator.DEFAULT_MAX_COLOR);
    clusterDefault.put(RecursiveCluster.CLASS_OUTER + "." + ContinuousColorCodingDecorator.VV_UNDEFINED_COLOR, "#"
        + Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);
    clusterDefault.put(RecursiveCluster.CLASS_OUTER + "." + ContinuousColorCodingDecorator.VV_OUT_OF_BOUNDS_COLOR,
        ContinuousColorCodingDecorator.DEFAULT_OUT_OF_BOUNDS_COLOR);

    clusterDefault.put(RecursiveCluster.CLASS_INNER + "." + RecursiveCluster.VV_FILL_COLOR, RecursiveCluster.DEFAULT_INNER_FILL_COLOR);
    clusterDefault.put(RecursiveCluster.CLASS_INNER + "." + ContinuousColorCodingDecorator.VV_MIN_COLOR,
        ContinuousColorCodingDecorator.DEFAULT_MIN_COLOR);
    clusterDefault.put(RecursiveCluster.CLASS_INNER + "." + ContinuousColorCodingDecorator.VV_MAX_COLOR,
        ContinuousColorCodingDecorator.DEFAULT_MAX_COLOR);
    clusterDefault.put(RecursiveCluster.CLASS_INNER + "." + ContinuousColorCodingDecorator.VV_UNDEFINED_COLOR, "#"
        + Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);
    clusterDefault.put(RecursiveCluster.CLASS_INNER + "." + ContinuousColorCodingDecorator.VV_OUT_OF_BOUNDS_COLOR,
        ContinuousColorCodingDecorator.DEFAULT_OUT_OF_BOUNDS_COLOR);

    clusterDefault.put("showAllInner", "false");

    DEFAULT_CONFIG_VALUES.put(ReportType.VBBCLUSTER, clusterDefault);

    QUERY_NAMES.put(ReportType.VBBCLUSTER, ImmutableList.of(RecursiveCluster.CLASS_OUTER, RecursiveCluster.CLASS_INNER));
  }

  private boolean                                           iteraQlEnabled;

  private Map<String, String>                               vpConfigMap           = Maps.newHashMap();

  private final ReportType                                  reportType;

  public VbbOptionsBean(ReportType reportType) {
    super();

    getColorOptionsBean().setAvailableColors(SpringGuiFactory.getInstance().getVbbClusterColors());
    setSelectedGraphicFormat(Constants.REPORTS_EXPORT_GRAPHICAL_SVG);
    setAvailableGraphicFormats(ExportOption.getGraphicalExportOptions(false));

    if (DEFAULT_CONFIG_VALUES.containsKey(reportType)) {
      vpConfigMap = Maps.newHashMap(DEFAULT_CONFIG_VALUES.get(reportType));

      String filterHint = MessageAccess.getString("reports.selectAll") + " " + MessageAccess.getString("graphicalExport.pieBar.bar.assignedElements");
      vpConfigMap.put("outer.filterHint", filterHint);
      vpConfigMap.put("inner.filterHint", filterHint);
    }

    this.reportType = reportType;
  }

  public Map<String, String> getViewpointConfigMap() {
    return this.vpConfigMap;
  }

  public void setViewpointConfigMap(Map<String, String> vpConfigMap) {
    this.vpConfigMap = vpConfigMap;
  }

  public boolean isIteraQlEnabled() {
    return iteraQlEnabled;
  }

  public void setIteraQlEnabled(boolean iteraQlEnabled) {
    this.iteraQlEnabled = iteraQlEnabled;
  }

  /**{@inheritDoc}**/
  @Override
  public List<String> getQueryResultNames() {
    return QUERY_NAMES.get(reportType);
  }
}
