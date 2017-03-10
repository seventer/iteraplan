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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.Line;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import de.iteratec.iteraplan.businesslogic.service.TimeseriesService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.visualizationmodel.jfreechart.AxisFactory;
import de.iteratec.visualizationmodel.jfreechart.ChartFactory;
import de.iteratec.visualizationmodel.jfreechart.JFreeChartSymbol;


/**
 *
 */
public class JFreeChartLineGraphicCreator {

  private static final Logger LOGGER         = Logger.getIteraplanLogger(JFreeChartLineGraphicCreator.class);

  public static final int     DEFAULT_HEIGHT = 1000;
  public static final int     DEFAULT_WIDTH  = 1500;

  private String              title;
  private Date                fromDate;
  private Date                toDate;

  private DateAxis            xAxis;
  private Axis                yAxis;
  private XYDataset           dataset;
  private boolean             emptyDataset   = false;

  private float               xResolution;
  private float               yResolution;

  private TimeseriesService   timeseriesService;

  /**
   * @return timeseriesService the timeseriesService
   */
  public TimeseriesService getTimeseriesService() {
    return timeseriesService;
  }

  public void setTimeseriesService(TimeseriesService timeseriesService) {
    this.timeseriesService = timeseriesService;
  }

  public JFreeChartSymbol generateJFreeChartSymbol(List<Timeseries> timeseriesList, AttributeType attributeType, String startDateString,
                                                   String endDateString) {
    initData(startDateString, endDateString);
    createAxes(attributeType);
    createDataset(timeseriesList, attributeType);
    return createChart();
  }

  public void initData(String startDateString, String endDateString) {
    setFromDate(getDateFromString(startDateString));
    if (endDateString.isEmpty()) {
      setToDate(new Date());
    }
    else {
      setToDate(getDateFromString(endDateString));
    }
    setxResolution(DEFAULT_WIDTH);
    setyResolution(DEFAULT_HEIGHT);
    title = MessageAccess.getString("graphicalExport.line.Title", UserContext.getCurrentLocale());
  }

  /**
   * 
   */
  private void createDataset(List<Timeseries> timeseriesList, AttributeType attributeType) {

    XYSeriesCollection xysc = new XYSeriesCollection();

    if (!timeseriesList.isEmpty()) {
      for (Timeseries timeseries : timeseriesList) {
        String name = timeseries.getBuildingBlock().getNonHierarchicalName();
        XYSeries xys = new XYSeries(name);
        List<TimeseriesEntry> entries = timeseries.getEntries();
        orderTimeseriesByTime(entries);
        int firstIdx = getIndexFirstElementToDraw(entries);
        int lastIdx = getIndexLastElementToDraw(entries);
        int beforeIdx = getIndexLastElementBeforeFromDate(entries);
        if (firstIdx > -1) {
          if (beforeIdx > -1) {
            xys.add(fromDate.getTime(), toDoubleValue(attributeType, entries.get(beforeIdx).getValue()));
          }
          if (firstIdx != lastIdx) {
            for (int i = firstIdx; i <= lastIdx; i++) {
              TimeseriesEntry entry = entries.get(i);
              xys.add(entry.getDate().getTime(), toDoubleValue(attributeType, entry.getValue()));
            }
          }
          else {
            xys.add(entries.get(firstIdx).getDate().getTime(), toDoubleValue(attributeType, entries.get(firstIdx).getValue()));
          }
          /**
           *Index is set in 'getIndexFirstElementToDraw(entries);' to -1 if there are no entries in the timeseries
           *to prevent an IndexOutOfBoundsException a simple range check is enough.
           */
          if (lastIdx >= 0 && lastIdx <= entries.size()) {
            //Füge zur XYSeries das Datum hinzu und den wert des Eintags an der Stelle(lastidx)
            xys.add(toDate.getTime(), toDoubleValue(attributeType, entries.get(lastIdx).getValue()));
          }
        }
        xysc.addSeries(xys);
        LOGGER.debug("Added data series for {0}", name);
      }
    }
    dataset = xysc;

    int seriesCount = dataset.getSeriesCount();

    if (seriesCount == 0) {
      dataset = createEmptyDataset();
      emptyDataset = true;
    }
    else {
      emptyDataset = false;
    }

  }

  /**
   * @param entries
   * @return index of the last element to add to the diagram
   */
  private int getIndexLastElementToDraw(List<TimeseriesEntry> entries) {
    int idx = -1;
    for (TimeseriesEntry entry : entries) {
      if (entry.getDate().before(toDate)) {
        idx = entries.indexOf(entry);
      }
    }
    return idx;
  }

  /**
   * @param entries
   * @return index of the first element to add to the diagram
   */
  private int getIndexFirstElementToDraw(List<TimeseriesEntry> entries) {
    for (TimeseriesEntry entry : entries) {
      if (entry.getDate().after(fromDate)) {
        return entries.indexOf(entry);
      }
    }
    return -1;
  }

  private int getIndexLastElementBeforeFromDate(List<TimeseriesEntry> entries) {
    int idx = -1;
    for (TimeseriesEntry entry : entries) {
      Date date = entry.getDate();
      if (date.before(fromDate)) {
        int idxDate = entries.indexOf(entry);
        if (idx < 0) {
          idx = idxDate;
        }
        else {
          if (date.after(entries.get(idx).getDate())) {
            idx = idxDate;
          }
        }
      }
    }
    return idx;
  }

  /**
   * @param entries
   */
  private void orderTimeseriesByTime(List<TimeseriesEntry> entries) {
    Collections.sort(entries, new Comparator<TimeseriesEntry>() {
      public int compare(TimeseriesEntry entry1, TimeseriesEntry entry2) {
        return entry1.getDate().compareTo(entry2.getDate());
      }
    });
  }

  /**
   * @param attributeType
   * @param value
   * @return a Double value for the given String value
   */
  private Double toDoubleValue(AttributeType attributeType, String value) {
    if (attributeType instanceof NumberAT) {
      return Double.valueOf(value);
    }
    else {
      if (attributeType instanceof EnumAT) {
        EnumAT enumAt = (EnumAT) attributeType;
        Map<String, Double> map = mapStringValuesToDouble(enumAt.getSortedAttributeValues());
        return map.get(value);
      }
    }
    return null;
  }

  /**
   * @param sortedEnumAVs
   * @return a map between Double and String values
   */
  private Map<String, Double> mapStringValuesToDouble(List<EnumAV> sortedEnumAVs) {
    Map<String, Double> stringToDouble = new HashMap<String, Double>();
    int i = 0;
    for (EnumAV enumAv : sortedEnumAVs) {
      stringToDouble.put(enumAv.getName(), Double.valueOf(i));
      i++;
    }
    return stringToDouble;
  }

  private XYDataset createEmptyDataset() {
    emptyDataset = true;

    XYSeriesCollection xysc = new XYSeriesCollection();
    XYSeries xys = new XYSeries("");
    xys.add(fromDate.getTime(), null);
    xys.add(toDate.getTime(), null);
    xysc.addSeries(xys);
    return xysc;
  }

  /**
   * 
   */
  private void createAxes(AttributeType attributeType) {
    AxisFactory af = new AxisFactory();

    // Time axis (xAxis)
    xAxis = af.createDateAxis(MessageAccess.getString("graphicalExport.line.xAxisLabel", UserContext.getCurrentLocale()));
    xAxis.setRange(fromDate, toDate);
    xAxis.setMinimumDate(fromDate);
    xAxis.setMaximumDate(toDate);
    xAxis.setMinorTickCount(5);
    xAxis.setAutoRange(true);

    // Value axis (yAxis)
    if (attributeType instanceof EnumAT) {
      // yAxis for enumerations

      EnumAT enumAT = (EnumAT) attributeType;
      List<EnumAV> attributeValues = enumAT.getSortedAttributeValues();

      String[] enumArray = new String[attributeValues.size()];

      int i = 0;
      for (EnumAV value : attributeValues) {
        enumArray[i] = value.getName();
        i++;
      }

      yAxis = new SymbolAxis(attributeType.getName(), enumArray);

    }
    else {
      // yAxis for numerics 
      yAxis = new NumberAxis(attributeType.getName());
    }
  }

  /**
   * Creates the chart and applies some styling.
   * @return the JFreeChart wrapped in an iteratec API symbol.
   */
  private JFreeChartSymbol createChart() {
    try {
      ChartFactory cf = new ChartFactory();
      cf.setTitle(title);
      cf.setxAxis(xAxis);
      cf.setyAxis(yAxis);
      JFreeChart chart = cf.createXYStepChart(dataset);
      LegendTitle legend = chart.getLegend();
      legend.setPosition(RectangleEdge.RIGHT);
      legend.setVisible(!emptyDataset); // don't show a legend, if the dataset is empty
      XYPlot xyplot = (XYPlot) chart.getPlot();
      xyplot.setBackgroundAlpha(0.4f);
      JFreeChartSymbol jfcs = new JFreeChartSymbol(chart);
      jfcs.setWidth(xResolution);
      jfcs.setHeight(yResolution);
      return jfcs;
    } catch (Exception e) {
      throw new IteraplanBusinessException(e);
    }
  }

  private static Date getDateFromString(String dateAsString) {
    return DateUtils.parseAsDate(dateAsString, UserContext.getCurrentLocale());
  }

  /**
   * @return fromDate the fromDate
   */
  public Date getFromDate() {
    return fromDate;
  }

  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  /**
   * @return toDate the toDate
   */
  public Date getToDate() {
    return toDate;
  }

  public void setToDate(Date toDate) {
    Date currentDate = new Date();
    if (toDate.after(currentDate)) {
      this.toDate = currentDate;
    }
    else {
      this.toDate = toDate;
    }
  }

  /**
   * @return xResolution the xResolution
   */
  public float getxResolution() {
    return xResolution;
  }

  public void setxResolution(float xResolution) {
    this.xResolution = xResolution;
  }

  /**
   * @return yResolution the yResolution
   */
  public float getyResolution() {
    return yResolution;
  }

  public void setyResolution(float yResolution) {
    this.yResolution = yResolution;
  }

}
