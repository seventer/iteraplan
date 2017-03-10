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

import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.TimelineFeature;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


@XmlType(name = "masterplanRowTypeOptions", propOrder = { "selectedBbType", "relationToBbType", "timelineFeatures", "useDefaultColoring",
    "colorAttributeId", "colorAttributeValues", "selectedColors", "useColorRange", "hierarchicalSort", "buildClosure", "customColumns" })
public class MasterplanRowTypeOptionsXML extends AbstractXMLElement<MasterplanRowTypeOptions> {

  private String                   selectedBbType       = Constants.REPORTS_EXPORT_SELECT_RELATION;
  private String                   relationToBbType     = "";
  private List<TimelineFeatureXML> timelineFeatures     = Lists.newArrayList();
  private boolean                  useDefaultColoring   = false;
  private Integer                  colorAttributeId     = Integer.valueOf(-1);
  private List<String>             colorAttributeValues = Lists.newArrayList();
  private List<String>             selectedColors       = Lists.newArrayList();
  private boolean                  useColorRange        = false;
  private boolean                  hierarchicalSort     = false;
  private boolean                  buildClosure         = false;
  private List<ColumnEntryXML>     customColumns        = Lists.newArrayList();

  @XmlElement(name = "selectedBbType", required = true)
  public String getSelectedBbType() {
    return selectedBbType;
  }

  public void setSelectedBbType(String selectedBbType) {
    this.selectedBbType = selectedBbType;
  }

  @XmlElement(name = "relationToBbType", required = true)
  public String getRelationToBbType() {
    return relationToBbType;
  }

  public void setRelationToBbType(String relationToBbType) {
    this.relationToBbType = relationToBbType;
  }

  @XmlElementWrapper(name = "timelineFeatures")
  @XmlElement(name = "timelineFeature")
  public List<TimelineFeatureXML> getTimelineFeatures() {
    return timelineFeatures;
  }

  public void setTimelineFeatures(List<TimelineFeatureXML> timelineFeatures) {
    this.timelineFeatures = timelineFeatures;
  }

  @XmlElement(name = "useDefaultColoring")
  public boolean isUseDefaultColoring() {
    return useDefaultColoring;
  }

  public void setUseDefaultColoring(boolean useDefaultColoring) {
    this.useDefaultColoring = useDefaultColoring;
  }

  @XmlElement(name = "colorAttributeId")
  public Integer getColorAttributeId() {
    return colorAttributeId;
  }

  public void setColorAttributeId(Integer colorAttributeId) {
    this.colorAttributeId = colorAttributeId;
  }

  @XmlElementWrapper(name = "colorAttributeValues")
  @XmlElement(name = "value")
  public List<String> getColorAttributeValues() {
    return colorAttributeValues;
  }

  public void setColorAttributeValues(List<String> colorAttributeValues) {
    this.colorAttributeValues = colorAttributeValues;
  }

  @XmlElementWrapper(name = "selectedColors")
  @XmlElement(name = "color")
  public List<String> getSelectedColors() {
    return selectedColors;
  }

  public void setSelectedColors(List<String> selectedColors) {
    this.selectedColors = selectedColors;
  }

  @XmlElement(name = "useColorRange")
  public boolean isUseColorRange() {
    return useColorRange;
  }

  public void setUseColorRange(boolean useColorRange) {
    this.useColorRange = useColorRange;
  }

  @XmlElement(name = "hierarchicalSort")
  public boolean isHierarchicalSort() {
    return hierarchicalSort;
  }

  public void setHierarchicalSort(boolean hierarchicalSort) {
    this.hierarchicalSort = hierarchicalSort;
  }

  @XmlElement(name = "buildClosure")
  public boolean isBuildClosure() {
    return buildClosure;
  }

  public void setBuildClosure(boolean buildClosure) {
    this.buildClosure = buildClosure;
  }

  @XmlElementWrapper(name = "customColumns")
  @XmlElement(name = "customColumn")
  public List<ColumnEntryXML> getCustomColumns() {
    return customColumns;
  }

  public void setCustomColumns(List<ColumnEntryXML> customColumns) {
    this.customColumns = customColumns;
  }

  /**{@inheritDoc}**/
  public void initFrom(MasterplanRowTypeOptions queryElement, Locale locale) {
    this.selectedBbType = queryElement.getSelectedBbType();
    this.relationToBbType = queryElement.getRelationToBbType();
    if (!queryElement.getTimelineFeatures().isEmpty()) {
      this.timelineFeatures = Lists.newArrayList();
      for (TimelineFeature feature : queryElement.getTimelineFeatures()) {
        TimelineFeatureXML featureXml = new TimelineFeatureXML();
        featureXml.initFrom(feature, locale);
        timelineFeatures.add(featureXml);
      }
    }
    this.useDefaultColoring = queryElement.isUseDefaultColoring();
    this.colorAttributeId = queryElement.getColorOptions().getDimensionAttributeId();
    if (queryElement.getColorOptions().getAttributeValues() != null) {
      this.colorAttributeValues = Lists.newArrayList(queryElement.getColorOptions().getAttributeValues());
    }
    if (queryElement.getColorOptions().getSelectedColors() != null) {
      this.selectedColors = Lists.newArrayList(queryElement.getColorOptions().getSelectedColors());
    }
    this.useColorRange = queryElement.getColorOptions().isUseColorRange();
    this.hierarchicalSort = queryElement.isHierarchicalSort();
    this.buildClosure = queryElement.isBuildClosure();
    if (!queryElement.getSelectedCustomColumns().isEmpty()) {
      this.customColumns = Lists.newArrayList();
      for (ColumnEntry column : queryElement.getSelectedCustomColumns()) {
        ColumnEntryXML columnXml = new ColumnEntryXML();
        columnXml.initFrom(column, locale);
        this.customColumns.add(columnXml);
      }
    }
  }

  /**{@inheritDoc}**/
  public void update(MasterplanRowTypeOptions queryElement, Locale locale) {
    //can not be done here, since it requires the services
    throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
  }

  /**{@inheritDoc}**/
  public void validate(Locale locale) {
    // nothing to do here
  }

}
