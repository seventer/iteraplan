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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TimeFunction;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;


@XmlType(name = "masterplanOptions", propOrder = { "colorAttributeId", "colorAttributeValues", "selectedBbType", "selectedColors", "timeSpan",
    "selectedGraphicFormat", "useColorRange", "useNamesLegend", "showSavedQueryInfo", "selectedRelatedType", "useOnlyNeighbouringRelatedElements",
    "customColumns", "level0Options", "level1Options", "level2Options" })
public class MasterplanOptionsXML extends AbstractXMLElement<MasterplanOptionsBean> {

  private static final Logger LOGGER = Logger.getIteraplanLogger(MasterplanOptionsXML.class);

  @XmlEnum(String.class)
  public enum SortMethodXML {
    HIERARCHICAL, NON_HIERARCHICAL;

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      if (NON_HIERARCHICAL.equals(this)) {
        return "nonHierarchical";
      }
      else {
        return "hierarchical";
      }
    }

    public static SortMethodXML get(String sortMethod) {
      if ("nonHierarchical".equals(sortMethod)) {
        return NON_HIERARCHICAL;
      }
      else if ("hierarchical".equals(sortMethod)) {
        return HIERARCHICAL;
      }
      LOGGER.error("'" + sortMethod + "' is not a valid sort method!");
      throw new IteraplanTechnicalException();
    }
  }

  //generic masterplan parameters
  private int                         dialogStep                         = 1;
  private QTimespanDataXML            timeSpan                           = new QTimespanDataXML();
  private String                      selectedGraphicFormat              = null;
  private boolean                     useNamesLegend                     = true;
  private boolean                     showSavedQueryInfo;

  private MasterplanRowTypeOptionsXML level0Options                      = null;
  private MasterplanRowTypeOptionsXML level1Options                      = null;
  private MasterplanRowTypeOptionsXML level2Options                      = null;

  //compatibility parameters
  private SortMethodXML               sortMethod                         = null;
  private Integer                     colorAttributeId                   = Integer.valueOf(-1);
  private List<String>                colorAttributeValues               = new ArrayList<String>();
  private List<String>                selectedColors                     = new ArrayList<String>();
  private String                      selectedBbType;
  private String                      selectedRelatedType                = Constants.REPORTS_EXPORT_SELECT_RELATION;
  private boolean                     useOnlyNeighbouringRelatedElements = true;
  private List<ColumnEntryXML>        customColumns                      = new ArrayList<ColumnEntryXML>();
  private boolean                     useColorRange                      = false;

  @XmlElement(name = "level0Options")
  public MasterplanRowTypeOptionsXML getLevel0Options() {
    return level0Options;
  }

  public void setLevel0Options(MasterplanRowTypeOptionsXML level0Options) {
    this.level0Options = level0Options;
  }

  @XmlElement(name = "level1Options")
  public MasterplanRowTypeOptionsXML getLevel1Options() {
    return level1Options;
  }

  public void setLevel1Options(MasterplanRowTypeOptionsXML level1Options) {
    this.level1Options = level1Options;
  }

  @XmlElement(name = "level2Options")
  public MasterplanRowTypeOptionsXML getLevel2Options() {
    return level2Options;
  }

  public void setLevel2Options(MasterplanRowTypeOptionsXML level2Options) {
    this.level2Options = level2Options;
  }

  /**
   * Empty constructor needed for JAXB. DO NOT USE WITHIN THE APPLICATION!
   */
  public MasterplanOptionsXML() {
    // empty constructor needed for JAXB. DO NOT USE
  }

  @XmlElement
  public String getSelectedRelatedType() {
    return selectedRelatedType;
  }

  @XmlElement
  public boolean isUseOnlyNeighbouringRelatedElements() {
    return useOnlyNeighbouringRelatedElements;
  }

  @XmlElementWrapper(name = "customColumns")
  @XmlElement(name = "customColumn")
  public List<ColumnEntryXML> getCustomColumns() {
    return customColumns;
  }

  @XmlAttribute(required = true)
  public int getDialogStep() {
    return dialogStep;
  }

  @XmlAttribute(required = true)
  public SortMethodXML getSortMethod() {
    return sortMethod;
  }

  @XmlElement
  public QTimespanDataXML getTimeSpan() {
    return timeSpan;
  }

  @XmlElement
  public String getSelectedBbType() {
    return this.selectedBbType;
  }

  public void setDialogStep(int dialogStep) {
    this.dialogStep = dialogStep;
  }

  public void setSortMethod(SortMethodXML sortMethod) {
    this.sortMethod = sortMethod;
  }

  public void setSelectedGraphicFormat(String selectedGraphicFormat) {
    this.selectedGraphicFormat = selectedGraphicFormat;
  }

  public void setUseNamesLegend(boolean useNamesLegend) {
    this.useNamesLegend = useNamesLegend;
  }

  public void setTimeSpan(QTimespanDataXML timeSpan) {
    this.timeSpan = timeSpan;
  }

  public void setSelectedBbType(String type) {
    this.selectedBbType = type;
  }

  public void setSelectedRelatedType(String relatedType) {
    this.selectedRelatedType = relatedType;
  }

  public void setUseOnlyNeighbouringRelatedElements(boolean useOnlyNeighbouringRelatedElements) {
    this.useOnlyNeighbouringRelatedElements = useOnlyNeighbouringRelatedElements;
  }

  public void setCustomColumnsAttributeIds(List<ColumnEntryXML> columns) {
    this.customColumns = columns;
  }

  @XmlElement
  public Integer getColorAttributeId() {
    return colorAttributeId;
  }

  @XmlElementWrapper(name = "colorAttributeValues")
  @XmlElement(name = "value")
  public List<String> getColorAttributeValues() {
    return colorAttributeValues;
  }

  @XmlElementWrapper(name = "selectedColors")
  @XmlElement(name = "color")
  public List<String> getSelectedColors() {
    return selectedColors;
  }

  @XmlElement
  public String getSelectedGraphicFormat() {
    return this.selectedGraphicFormat;
  }

  @XmlElement
  public boolean isUseNamesLegend() {
    return useNamesLegend;
  }

  @XmlElement
  public boolean isShowSavedQueryInfo() {
    return showSavedQueryInfo;
  }

  public void setShowSavedQueryInfo(boolean showSavedQueryInfo) {
    this.showSavedQueryInfo = showSavedQueryInfo;
  }

  public void setColorAttributeId(Integer colorAttributeId) {
    this.colorAttributeId = colorAttributeId;
  }

  public void setColorAttributeValues(List<String> colorAttributeValues) {
    this.colorAttributeValues = colorAttributeValues;
  }

  public void setSelectedColors(List<String> selectedColors) {
    this.selectedColors = selectedColors;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object,
   * java.util.Locale)
   */
  public void initFrom(MasterplanOptionsBean queryElement, Locale locale) {
    this.dialogStep = queryElement.getDialogStep();
    this.selectedGraphicFormat = queryElement.getSelectedGraphicFormat();
    this.useNamesLegend = queryElement.isUseNamesLegend();
    this.showSavedQueryInfo = queryElement.isShowSavedQueryInfo();
    this.timeSpan.setStartDate(queryElement.getStartDateString(), locale, queryElement.getStartTimeFunction());
    this.timeSpan.setEndDate(queryElement.getEndDateString(), locale, queryElement.getEndTimeFunction());
    this.level0Options = new MasterplanRowTypeOptionsXML();
    this.level0Options.initFrom(queryElement.getLevel0Options(), locale);
    if (queryElement.getLevel1Options() != null) {
      this.level1Options = new MasterplanRowTypeOptionsXML();
      this.level1Options.initFrom(queryElement.getLevel1Options(), locale);
      if (queryElement.getLevel2Options() != null) {
        this.level2Options = new MasterplanRowTypeOptionsXML();
        this.level2Options.initFrom(queryElement.getLevel2Options(), locale);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object,
   * java.util.Locale)
   */
  public void update(MasterplanOptionsBean queryElement, Locale locale) {
    if (locale == null) {
      LOGGER.error("Locale not set!");
      throw new IteraplanTechnicalException();
    }
    queryElement.setSelectedGraphicFormat(selectedGraphicFormat);
    queryElement.setUseNamesLegend(useNamesLegend);
    queryElement.setShowSavedQueryInfo(showSavedQueryInfo);
    queryElement.setStartDateString(DateUtils.formatAsString(this.timeSpan.calculateStartDate(), locale));
    // queryElement.setStartTimeFunction(timeSpan.getStartTimeFunction().convert());
    queryElement.setStartTimeFunction(TimeFunction.ABSOLUTE);
    queryElement.setEndDateString(DateUtils.formatAsString(this.timeSpan.calculateEndDate(), locale));
    // queryElement.setEndTimeFunction(timeSpan.getEndTimeFunction().convert());
    queryElement.setEndTimeFunction(TimeFunction.ABSOLUTE);
    queryElement.setDialogStep(this.dialogStep);
  }

  public void validate(Locale locale) {
    if (dialogStep != 1 && dialogStep != 2) {
      logError(dialogStep + " is not a valid dialog step");
    }
    if (timeSpan != null) {
      timeSpan.validate(locale);
    }

    if (colorAttributeId != null) {
      String errorMsg = ValidationHelper.validateColors(colorAttributeValues, selectedColors);
      if (errorMsg != null) {
        logError(errorMsg);
      }
    }
    // the sortMethod does not have to be validated as enums are validated against the schema!
  }

  public boolean isUseColorRange() {
    return useColorRange;
  }

  public void setUseColorRange(boolean useColorRange) {
    this.useColorRange = useColorRange;
  }
}
