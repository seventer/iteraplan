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
package de.iteratec.iteraplan.model.xml.query;

import java.util.Locale;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TimeFunction;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Line.LineOptionsBean;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;

/**
 *
 */
@XmlType(name = "lineOptions", propOrder = { "selectedBbType", "timeSpan", "selectedKeyAttributeType",
    "selectedGraphicsFormat", "useNamesLegend", "showSavedQueryInfo" })
public class LineOptionsXML extends AbstractXMLElement<LineOptionsBean> {
  
  private static final Logger LOGGER                   = Logger.getIteraplanLogger(LineOptionsXML.class);

  private String              selectedBbType;
  private QTimespanDataXML    timeSpan                 = new QTimespanDataXML();
  private int                 dialogStep               = 1;
  private String              selectedGraphicsFormat;
  private boolean             useNamesLegend           = true;
  private boolean             showSavedQueryInfo;
  private int                 selectedKeyAttributeType = -1;
  
  /**
   * Empty constructor needed for JAXB. DO NOT USE WITHIN THE APPLICATION!
   */
  public LineOptionsXML() {
    // empty constructor needed for JAXB. DO NOT USE
  }

  /**
   * @return selectedBbType the selectedBbType
   */
  @XmlElement
  public String getSelectedBbType() {
    return selectedBbType;
  }

  public void setSelectedBbType(String selectedBbType) {
    this.selectedBbType = selectedBbType;
  }

  /**
   * @return timeSpan the timeSpan
   */
  @XmlElement
  public QTimespanDataXML getTimeSpan() {
    return timeSpan;
  }

  public void setTimeSpan(QTimespanDataXML timeSpan) {
    this.timeSpan = timeSpan;
  }

  /**
   * @return dialogStep the dialogStep
   */
  @XmlAttribute(required = true)
  public int getDialogStep() {
    return dialogStep;
  }

  public void setDialogStep(int dialogStep) {
    this.dialogStep = dialogStep;
  }

  /**
   * @return selectedGraphicsFormat the selectedGraphicsFormat
   */
  @XmlElement
  public String getSelectedGraphicsFormat() {
    return selectedGraphicsFormat;
  }

  public void setSelectedGraphicsFormat(String selectedGraphicsFormat) {
    this.selectedGraphicsFormat = selectedGraphicsFormat;
  }

  /**
   * @return useNamesLegend the useNamesLegend
   */
  @XmlElement
  public boolean isUseNamesLegend() {
    return useNamesLegend;
  }

  public void setUseNamesLegend(boolean useNamesLegend) {
    this.useNamesLegend = useNamesLegend;
  }

  /**
   * @return showSavedQueryInfo the showSavedQueryInfo
   */
  @XmlElement
  public boolean isShowSavedQueryInfo() {
    return showSavedQueryInfo;
  }

  public void setShowSavedQueryInfo(boolean showSavedQueryInfo) {
    this.showSavedQueryInfo = showSavedQueryInfo;
  }

  /**
   * @return selectedKeyAttributeType the selectedKeyAttributeType
   */
  @XmlAttribute(required = true)
  public int getSelectedKeyAttributeType() {
    return selectedKeyAttributeType;
  }

  public void setSelectedKeyAttributeType(int selectedKeyAttributeType) {
    this.selectedKeyAttributeType = selectedKeyAttributeType;
  }

  /**{@inheritDoc}**/
  public void validate(Locale locale) {
    if (dialogStep != 1 && dialogStep != 2) {
      logError(dialogStep + " is not a valid dialog step");
    }
  }

  /**{@inheritDoc}**/
  public void initFrom(LineOptionsBean lineOptions, Locale locale) {
    this.dialogStep = lineOptions.getDialogStep();
    this.timeSpan.setStartDate(lineOptions.getStartDateString(), locale, lineOptions.getStartTimeFunction());
    this.timeSpan.setEndDate(lineOptions.getEndDateString(), locale, lineOptions.getEndTimeFunction());
    this.selectedBbType = lineOptions.getSelectedBbType();
    this.selectedGraphicsFormat = lineOptions.getSelectedGraphicFormat();
    this.selectedKeyAttributeType = lineOptions.getSelectedKeyAttributeTypeId();
    this.showSavedQueryInfo = lineOptions.isShowSavedQueryInfo();
    this.useNamesLegend = lineOptions.isUseNamesLegend();
  }

  /**{@inheritDoc}**/
  public void update(LineOptionsBean lineOptions, Locale locale) {
    if (locale == null) {
      LOGGER.error("Locale not set!");
      throw new IteraplanTechnicalException();
    }
    lineOptions.setSelectedGraphicFormat(selectedGraphicsFormat);
    lineOptions.setUseNamesLegend(useNamesLegend);
    lineOptions.setShowSavedQueryInfo(showSavedQueryInfo);
    lineOptions.setStartDateString(DateUtils.formatAsString(this.timeSpan.calculateStartDate(), locale));
    lineOptions.setStartTimeFunction(TimeFunction.ABSOLUTE);
    lineOptions.setEndDateString(DateUtils.formatAsString(this.timeSpan.calculateEndDate(), locale));
    lineOptions.setEndTimeFunction(TimeFunction.ABSOLUTE);
    lineOptions.setDialogStep(this.dialogStep);
  }
}
