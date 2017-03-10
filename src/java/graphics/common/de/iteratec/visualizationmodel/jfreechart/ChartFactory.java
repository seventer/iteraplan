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
package de.iteratec.visualizationmodel.jfreechart;

import java.awt.BasicStroke;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;

/**
 *
 */
public class ChartFactory {
  
  private static final ChartTheme THEME       = new IteraChartTheme();

  private String                  title       = "title";
  private Axis                    xAxis;
  private Axis                    yAxis;
  private PlotOrientation         orientation = PlotOrientation.VERTICAL;
  private boolean                 showLegend = true;
  private boolean                 showTooltips = true;
  private boolean                 showUrls = true;

  public JFreeChart createXYStepChart(XYDataset dataset) {
    XYToolTipGenerator toolTipGenerator = !showTooltips ? null : new StandardXYToolTipGenerator();
    XYURLGenerator urlGenerator = !showUrls ? null : new StandardXYURLGenerator();
    XYItemRenderer renderer = new XYStepRenderer(toolTipGenerator, urlGenerator);
    
    XYPlot plot = new XYPlot(dataset, (ValueAxis) xAxis, (ValueAxis) yAxis, null);
    plot.setRenderer(renderer);
    plot.setOrientation(orientation);
    plot.setDomainCrosshairVisible(false);
    plot.setRangeCrosshairVisible(false);
    plot.getRenderer().setSeriesStroke(0, new BasicStroke(5.0f));
    JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, showLegend);
    THEME.apply(chart);
    return chart;
  }

  public ChartFactory setTitle(String title) {
    this.title = title;
    return this;
  }

  public ChartFactory setOrientation(PlotOrientation orientation) {
    this.orientation = orientation;
    return this;
  }

  public ChartFactory setShowLegend(boolean showLegend) {
    this.showLegend = showLegend;
    return this;
  }

  public ChartFactory setShowTooltips(boolean showTooltips) {
    this.showTooltips = showTooltips;
    return this;
  }

  public ChartFactory setShowUrls(boolean showUrls) {
    this.showUrls = showUrls;
    return this;
  }

  public ChartFactory setxAxis(Axis xAxis) {
    this.xAxis = xAxis;
    return this;
  }

  public ChartFactory setyAxis(Axis yAxis) {
    this.yAxis = yAxis;
    return this;
  }
}
