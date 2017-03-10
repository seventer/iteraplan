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
import javax.xml.bind.annotation.XmlType;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterSecondOrderBean;
import de.iteratec.iteraplan.common.util.CollectionUtils;


@XmlType(name = "clusterOption", propOrder = { "availableLevels", "colorAttribute", "colorAttributeValues", "colorOptions", "lowerLevel",
    "selectedBbType", "selectedColors", "selectedModus", "selectedAttributeValues", "typeOrder", "upperLevel", "selectedGraphicsFormat",
    "useColorRange", "useNamesLegend", "showSavedQueryInfo", "swimLanes" })
public class ClusterOptionsXML extends AbstractXMLElement<ClusterOptionsBean> {

  private int                   dialogStep              = 1;
  private int                   selectedAttributeType   = -1;

  private List<String>          selectedAttributeValues = CollectionUtils.arrayList();
  private int                   colorAttribute          = -1;
  private List<String>          colorAttributeValues    = CollectionUtils.arrayList();
  private List<String>          selectedColors          = CollectionUtils.arrayList();
  private List<ColorOptionsXML> colorOptions            = CollectionUtils.arrayList();
  private List<String>          typeOrder;
  private String                selectedModus;
  private String                selectedBbType;

  private boolean               swimLanes;
  private boolean               useNamesLegend          = true;
  private boolean               showSavedQueryInfo;
  private boolean               useColorRange           = false;

  private int                   upperLevel;
  private int                   lowerLevel;
  private int                   availableLevels;

  private String                selectedGraphicsFormat;

  /**
   * Empty constructor needed for JAXB. DO NOT USE WITHIN THE APPLICATION!
   */
  public ClusterOptionsXML() {
    // empty constructor needed for JAXB. DO NOT USE
  }

  @XmlAttribute(required = true)
  public int getDialogStep() {
    return dialogStep;
  }

  public void setDialogStep(int dialogStep) {
    this.dialogStep = dialogStep;
  }

  @XmlAttribute(required = true)
  public int getColorAttribute() {
    return colorAttribute;
  }

  @XmlElement
  public String getSelectedBbType() {
    return this.selectedBbType;
  }

  @XmlElement
  public boolean isSwimLanes() {
    return this.swimLanes;
  }

  @XmlElementWrapper(name = "typeOrder")
  @XmlElement(name = "type")
  public List<String> getTypeOrder() {
    return typeOrder;
  }

  @XmlElementWrapper(name = "colorAttributeValues")
  @XmlElement(name = "value")
  public List<String> getColorAttributeValues() {
    return this.colorAttributeValues;
  }

  @XmlElementWrapper(name = "colorOptions")
  @XmlElement(name = "option")
  public List<ColorOptionsXML> getColorOptions() {
    return colorOptions;
  }

  @XmlElementWrapper(name = "selectedColors")
  @XmlElement(name = "color")
  public List<String> getSelectedColors() {
    return this.selectedColors;
  }

  @XmlElement
  public String getSelectedModus() {
    return selectedModus;
  }

  @XmlAttribute(required = true)
  public int getSelectedAttributeType() {
    return selectedAttributeType;
  }

  @XmlElementWrapper(name = "selectedAttributeValues")
  @XmlElement(name = "attributeValue")
  public List<String> getSelectedAttributeValues() {
    return selectedAttributeValues;
  }

  @XmlElement
  public int getUpperLevel() {
    return upperLevel;
  }

  @XmlElement
  public int getLowerLevel() {
    return lowerLevel;
  }

  @XmlElement
  public int getAvailableLevels() {
    return availableLevels;
  }

  @XmlElement
  public String getSelectedGraphicsFormat() {
    return selectedGraphicsFormat;
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

  public void setAvailableLevels(int availableLevels) {
    this.availableLevels = availableLevels;
  }

  public void setLowerLevel(int lowerLevel) {
    this.lowerLevel = lowerLevel;
  }

  public void setUpperLevel(int upperLevel) {
    this.upperLevel = upperLevel;
  }

  public void setSwimLanes(boolean swimLanes) {
    this.swimLanes = swimLanes;
  }

  public void setSelectedAttributeType(int selectedAttributeType) {
    this.selectedAttributeType = selectedAttributeType;
  }

  public void setSelectedAttributeValues(List<String> selectedAttributeValues) {
    this.selectedAttributeValues = selectedAttributeValues;
  }

  public void setColorOptions(List<ColorOptionsXML> colorOptions) {
    this.colorOptions = colorOptions;
  }

  public void setSelectedModus(String selectedModus) {
    this.selectedModus = selectedModus;
  }

  public void setSelectedBbType(String selectedBbType) {
    this.selectedBbType = selectedBbType;
  }

  public void setColorAttribute(int colorAttribute) {
    this.colorAttribute = colorAttribute;
  }

  public void setTypeOrder(List<String> typeOrder) {
    this.typeOrder = typeOrder;
  }

  public void setColorAttributeValues(List<String> colorAttributeValues) {
    this.colorAttributeValues = colorAttributeValues;
  }

  public void setSelectedColors(List<String> selectedColors) {
    this.selectedColors = selectedColors;
  }

  public void setSelectedGraphicsFormat(String selectedGraphicsFormat) {
    this.selectedGraphicsFormat = selectedGraphicsFormat;
  }

  public void setUseNamesLegend(boolean useNamesLegend) {
    this.useNamesLegend = useNamesLegend;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object,
   * java.util.Locale)
   */
  public void initFrom(ClusterOptionsBean queryElement, Locale locale) {
    this.dialogStep = queryElement.getDialogStep();
    this.selectedModus = queryElement.getSelectedClusterMode();
    this.selectedBbType = queryElement.getSelectedBbType();
    this.selectedAttributeType = queryElement.getSelectedAttributeType();
    if (queryElement.getColorOptionsBean().getAttributeValues().equals(queryElement.getSelectedAttributeValues())) {
      this.selectedAttributeValues = null;
    }
    else {
      this.selectedAttributeValues = queryElement.getSelectedAttributeValues();
    }
    this.colorAttribute = queryElement.getColorOptionsBean().getDimensionAttributeId().intValue();
    this.colorAttributeValues = queryElement.getColorOptionsBean().getAttributeValues();
    this.selectedColors = queryElement.getColorOptionsBean().getSelectedColors();
    this.useColorRange = queryElement.getColorOptionsBean().isUseColorRange();
    this.upperLevel = queryElement.getSelectedHierarchicalUpperLevel();
    this.lowerLevel = queryElement.getSelectedHierarchicalLowerLevel();
    this.availableLevels = queryElement.getAvailableHierarchicalLevels();
    this.selectedGraphicsFormat = queryElement.getSelectedGraphicFormat();
    this.useNamesLegend = queryElement.isUseNamesLegend();
    this.showSavedQueryInfo = queryElement.isShowSavedQueryInfo();
    this.swimLanes = queryElement.isSwimlaneContent();

    final List<String> outputOrder = new ArrayList<String>(queryElement.getTypeOrder().size());
    for (String type : queryElement.getTypeOrder()) {
      outputOrder.add(type);
    }
    this.typeOrder = outputOrder;

    for (ClusterSecondOrderBean dimension : queryElement.getSecondOrderBeans()) {
      this.colorOptions.add(initColorDimension(dimension, locale));
    }
  }

  private ColorOptionsXML initColorDimension(ClusterSecondOrderBean dimension, Locale locale) {
    final ColorOptionsXML xmlDimension = new ColorOptionsXML();
    xmlDimension.initFrom(dimension, locale);
    return xmlDimension;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object,
   * java.util.Locale)
   */
  public void update(ClusterOptionsBean queryElement, Locale locale) {
    // not needed as initialisation of the clusterGraphic is done in
    // de.iteratec.iteraplan.businesslogic.service.InitFormHelperServiceImpl#initClusterDiagramForm
  }

  public void validate(Locale locale) {
    if (dialogStep != 1 && dialogStep != 2) {
      logError(dialogStep + " is not a valid dialog step");
    }

    if (colorAttribute > 0) {
      final String errorMsg = ValidationHelper.validateColors(colorAttributeValues, selectedColors);
      if (errorMsg != null) {
        logError(errorMsg);
      }
    }
    for (ColorOptionsXML dimension : colorOptions) {
      dimension.validate(locale);
    }
  }

  public boolean isUseColorRange() {
    return useColorRange;
  }

  public void setUseColorRange(boolean useColorRange) {
    this.useColorRange = useColorRange;
  }

}
