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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.BarsOrderMethod;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.SingleBarOptionsBean;
import de.iteratec.iteraplan.common.util.CollectionUtils;


@XmlType(name = "pieBarOption", propOrder = { "selectedBbType", "selectedModus", "diagramKeyType", "valuesSource", "diagramValuesType",
    "colorAttributeId", "colorAssociation", "colorAttributeValues", "selectedColors", "colorOptions", "associationTopLevel",
    "associationBottomLevel", "showEmptyBars", "showSegmentLabels", "showBarSizeLabels", "barsOrderMethod", "selectedGraphicsFormat",
    "useColorRange", "useNamesLegend", "showSavedQueryInfo" })
public class PieBarOptionsXML extends AbstractXMLElement<PieBarDiagramOptionsBean> {

  private String                         selectedBbType;
  private String                         selectedModus;
  private String                         diagramKeyType;
  private String                         valuesSource;
  private String                         diagramsValuesType;

  private String                         selectedKeyAssociation   = "";
  private int                            selectedKeyAttributeType = -1;

  private String                         colorAssociation         = "";
  private int                            colorAttributeId         = -1;
  private List<String>                   colorAttributeValues     = CollectionUtils.arrayList();
  private List<String>                   selectedColors           = CollectionUtils.arrayList();
  private List<SingleBarColorOptionsXML> colorOptions             = CollectionUtils.arrayList();

  private int                            associationTopLevel      = 1;
  private int                            associationBottomLevel   = 3;

  private boolean                        showEmptyBars            = true;
  private boolean                        showSegmentLabels        = false;
  private boolean                        showBarSizeLabels        = false;
  private String                         barsOrderMethod          = BarsOrderMethod.DEFAULT.getValue();

  private String                         selectedGraphicsFormat;
  private boolean                        useNamesLegend           = true;
  private boolean                        showSavedQueryInfo;
  private boolean                        useColorRange;

  private int                            dialogStep               = 1;

  /**
   * Empty constructor needed for JAXB. DO NOT USE WITHIN THE APPLICATION!
   */
  public PieBarOptionsXML() {
    // empty constructor needed for JAXB. DO NOT USE
  }

  @XmlAttribute(required = true)
  public int getDialogStep() {
    return dialogStep;
  }

  public void setDialogStep(int dialogStep) {
    this.dialogStep = dialogStep;
  }

  @XmlElement
  public String getSelectedBbType() {
    return this.selectedBbType;
  }

  @XmlElement
  public String getSelectedModus() {
    return selectedModus;
  }

  @XmlElement
  public String getDiagramKeyType() {
    return diagramKeyType;
  }

  @XmlElement
  public String getValuesSource() {
    return valuesSource;
  }

  @XmlElement
  public String getDiagramValuesType() {
    return diagramsValuesType;
  }

  @XmlAttribute(required = true)
  public int getSelectedKeyAttributeType() {
    return selectedKeyAttributeType;
  }

  @XmlAttribute(required = true)
  public String getSelectedKeyAssociation() {
    return selectedKeyAssociation;
  }

  @XmlElement
  public String getSelectedGraphicsFormat() {
    return selectedGraphicsFormat;
  }

  @XmlElement
  public boolean isUseNamesLegend() {
    return useNamesLegend;
  }

  public void setShowSavedQueryInfo(boolean showSavedQueryInfo) {
    this.showSavedQueryInfo = showSavedQueryInfo;
  }

  public boolean isShowSavedQueryInfo() {
    return showSavedQueryInfo;
  }

  @XmlElement(required = true)
  public int getColorAttributeId() {
    return colorAttributeId;
  }

  @XmlElement(required = true)
  public String getColorAssociation() {
    return colorAssociation;
  }

  @XmlElementWrapper(name = "colorAttributeValues")
  @XmlElement(name = "value")
  public List<String> getColorAttributeValues() {
    return this.colorAttributeValues;
  }

  @XmlElementWrapper(name = "selectedColors")
  @XmlElement(name = "color")
  public List<String> getSelectedColors() {
    return this.selectedColors;
  }

  @XmlElementWrapper(name = "colorOptions")
  @XmlElement(name = "option")
  public List<SingleBarColorOptionsXML> getColorOptions() {
    return colorOptions;
  }

  @XmlElement
  public int getAssociationBottomLevel() {
    return associationBottomLevel;
  }

  @XmlElement
  public int getAssociationTopLevel() {
    return associationTopLevel;
  }

  @XmlElement
  public boolean isShowEmptyBars() {
    return showEmptyBars;
  }

  @XmlElement
  public boolean isShowSegmentLabels() {
    return showSegmentLabels;
  }

  @XmlElement
  public boolean isShowBarSizeLabels() {
    return showBarSizeLabels;
  }

  @XmlElement
  public String getBarsOrderMethod() {
    return barsOrderMethod;
  }

  public void setSelectedKeyAttributeType(int selectedKeyAttributeType) {
    this.selectedKeyAttributeType = selectedKeyAttributeType;
  }

  public void setSelectedKeyAssociation(String selectedKeyAssociation) {
    this.selectedKeyAssociation = selectedKeyAssociation;
  }

  public void setSelectedModus(String selectedModus) {
    this.selectedModus = selectedModus;
  }

  public void setDiagramKeyType(String diagramKeyType) {
    this.diagramKeyType = diagramKeyType;
  }

  public void setValuesSource(String valuesSource) {
    this.valuesSource = valuesSource;
  }

  public void setDiagramValuesType(String diagramValuesType) {
    this.diagramsValuesType = diagramValuesType;
  }

  public void setSelectedBbType(String selectedBbType) {
    this.selectedBbType = selectedBbType;
  }

  public void setSelectedGraphicsFormat(String selectedGraphicsFormat) {
    this.selectedGraphicsFormat = selectedGraphicsFormat;
  }

  public void setUseNamesLegend(boolean useNamesLegend) {
    this.useNamesLegend = useNamesLegend;
  }

  public void setColorAttributeId(int colorAttribute) {
    this.colorAttributeId = colorAttribute;
  }

  public void setColorAssociation(String colorAssociation) {
    this.colorAssociation = colorAssociation;
  }

  public void setColorAttributeValues(List<String> colorAttributeValues) {
    this.colorAttributeValues = colorAttributeValues;
  }

  public void setSelectedColors(List<String> selectedColors) {
    this.selectedColors = selectedColors;
  }

  public void setColorOptions(List<SingleBarColorOptionsXML> colorOptions) {
    this.colorOptions = colorOptions;
  }

  public void setAssociationBottomLevel(int associationBottomLevel) {
    this.associationBottomLevel = associationBottomLevel;
  }

  public void setAssociationTopLevel(int associationTopLevel) {
    this.associationTopLevel = associationTopLevel;
  }

  public void setShowEmptyBars(boolean showEmptyBars) {
    this.showEmptyBars = showEmptyBars;
  }

  public void setShowSegmentLabels(boolean showSegmentLabels) {
    this.showSegmentLabels = showSegmentLabels;
  }

  public void setShowBarSizeLabels(boolean showBarSizeLabels) {
    this.showBarSizeLabels = showBarSizeLabels;
  }

  public void setBarsOrderMethod(String barsOrderMethod) {
    this.barsOrderMethod = barsOrderMethod;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object,
   * java.util.Locale)
   */
  public void initFrom(PieBarDiagramOptionsBean options, Locale locale) {
    this.dialogStep = options.getDialogStep();
    this.selectedBbType = options.getSelectedBbType();
    this.selectedModus = options.getDiagramType().getValue();
    this.diagramKeyType = options.getDiagramKeyType().getValue();
    this.valuesSource = options.getValuesSource().getValue();
    this.diagramsValuesType = options.getDiagramValuesType().getValue();
    this.selectedKeyAttributeType = options.getSelectedKeyAttributeTypeId();
    this.selectedKeyAssociation = options.getSelectedKeyAssociation();
    this.colorAssociation = options.getSelectedAssociation();
    this.colorAttributeId = options.getColorOptionsBean().getDimensionAttributeId().intValue();
    this.colorAttributeValues = options.getColorOptionsBean().getAttributeValues();
    this.selectedColors = options.getColorOptionsBean().getSelectedColors();
    this.useColorRange = options.getColorOptionsBean().isUseColorRange();

    for (SingleBarOptionsBean singleBarOptions : options.getBarsMap().values()) {
      this.colorOptions.add(initColorDimension(singleBarOptions, locale));
    }

    this.associationBottomLevel = options.getSelectedBottomLevel();
    this.associationTopLevel = options.getSelectedTopLevel();

    this.showEmptyBars = options.isShowEmptyBars();
    this.showSegmentLabels = options.isShowSegmentLabels();
    this.showBarSizeLabels = options.isShowBarSizeLabels();
    this.barsOrderMethod = options.getBarsOrderMethod().getValue();

    this.selectedGraphicsFormat = options.getSelectedGraphicFormat();
    this.useNamesLegend = options.isUseNamesLegend();
    this.showSavedQueryInfo = options.isShowSavedQueryInfo();
  }

  private SingleBarColorOptionsXML initColorDimension(SingleBarOptionsBean dimension, Locale locale) {
    final SingleBarColorOptionsXML xmlDimension = new SingleBarColorOptionsXML();
    xmlDimension.initFrom(dimension, locale);
    return xmlDimension;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object,
   * java.util.Locale)
   */
  public void update(PieBarDiagramOptionsBean queryElement, Locale locale) {
    // not needed as initialisation of the pie or bar Graphic is done in
    // de.iteratec.iteraplan.businesslogic.service.InitFormHelperServiceImpl#initPieBarDiagramForm
  }

  public void validate(Locale locale) {
    if (dialogStep != 1 && dialogStep != 2) {
      logError(dialogStep + " is not a valid dialog step");
    }
  }

  public boolean isUseColorRange() {
    return useColorRange;
  }

  public void setUseColorRange(boolean useColorRange) {
    this.useColorRange = useColorRange;
  }

}
