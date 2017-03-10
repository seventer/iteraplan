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
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;


@XmlType(name = "informationFlowOptions", propOrder = { "selectedGraphicFormat", "useColorRange", "useNamesLegend", "showSavedQueryInfo",
    "selectedNodeLayout", "colorAttributeId", "colorAttributeValues", "selectedColors", "lineTypeAttributeId", "lineAttributeValues",
    "selectedLineTypes", "edgeCaptionType", "edgeAttributeId", "showIsBusinessObjects", "showIsBaseComponents", "selectedTemplateName" })
public class InformationFlowOptionsXML extends AbstractXMLElement<InformationFlowOptionsBean> {

  private int          dialogStep            = 1;

  private String       selectedGraphicFormat = null;
  private String       selectedNodeLayout    = null;

  private boolean      useNamesLegend        = true;
  private boolean      showSavedQueryInfo;
  private boolean      useColorRange         = false;

  private Integer      colorAttributeId      = Integer.valueOf(-1);
  private List<String> colorAttributeValues  = new ArrayList<String>();
  private List<String> selectedColors        = new ArrayList<String>();

  private Integer      lineTypeAttributeId   = Integer.valueOf(-1);
  private List<String> lineAttributeValues   = new ArrayList<String>();
  private List<String> selectedLineTypes     = new ArrayList<String>();

  /**
   * Saves the selected edge caption types (see constants in {@link InformationFlowOptionsBean}) as int.
   * Each selected type gets its own digit (using the decimal system).
   * This is done to ensure compatibility to older saved queries, where only one caption type was saved as int. 
   */
  private int          edgeCaptionType       = 1;
  private int          edgeAttributeId       = -1;

  private boolean      showIsBusinessObjects = true;
  private boolean      showIsBaseComponents  = true;

  private String       selectedTemplateName;

  @XmlAttribute(required = true)
  public int getDialogStep() {
    return dialogStep;
  }

  public void setSelectedTemplateName(String selectedTemplateName) {
    this.selectedTemplateName = selectedTemplateName;
  }

  @XmlElement
  public String getSelectedTemplateName() {
    return selectedTemplateName;
  }

  @XmlElement
  public boolean isShowIsBusinessObjects() {
    return showIsBusinessObjects;
  }

  @XmlElement
  public boolean isShowIsBaseComponents() {
    return this.showIsBaseComponents;
  }

  @XmlElement
  public String getSelectedGraphicFormat() {
    return selectedGraphicFormat;
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

  @XmlElement
  public String getSelectedNodeLayout() {
    return this.selectedNodeLayout;
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
  public Integer getLineTypeAttributeId() {
    return lineTypeAttributeId;
  }

  @XmlElementWrapper(name = "lineAttributeValues")
  @XmlElement(name = "value")
  public List<String> getLineAttributeValues() {
    return lineAttributeValues;
  }

  @XmlElementWrapper(name = "selectedLineTypes")
  @XmlElement(name = "lineType")
  public List<String> getSelectedLineTypes() {
    return selectedLineTypes;
  }

  @XmlElement
  public int getEdgeCaptionType() {
    return this.edgeCaptionType;
  }

  @XmlElement
  public int getEdgeAttributeId() {
    return this.edgeAttributeId;
  }

  public void setShowIsBusinessObjects(boolean showIsBusinessObjects) {
    this.showIsBusinessObjects = showIsBusinessObjects;
  }

  public void setShowIsBaseComponents(boolean showIsBaseComponents) {
    this.showIsBaseComponents = showIsBaseComponents;
  }

  public void setDialogStep(int dialogStep) {
    this.dialogStep = dialogStep;
  }

  public void setSelectedGraphicFormat(String selectedGraphicFormat) {
    this.selectedGraphicFormat = selectedGraphicFormat;
  }

  public void setUseNamesLegend(boolean useNamesLegend) {
    this.useNamesLegend = useNamesLegend;
  }

  public void setSelectedNodeLayout(String selectedNodeLayout) {
    this.selectedNodeLayout = selectedNodeLayout;
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

  public void setLineTypeAttributeId(Integer lineTypeAttributeId) {
    this.lineTypeAttributeId = lineTypeAttributeId;
  }

  public void setLineAttributeValues(List<String> lineAttributeValues) {
    this.lineAttributeValues = lineAttributeValues;
  }

  public void setSelectedLineTypes(List<String> selectedLineTypes) {
    this.selectedLineTypes = selectedLineTypes;
  }

  public void setEdgeCaptionType(int edgeCaptionType) {
    this.edgeCaptionType = edgeCaptionType;
  }

  public void setEdgeAttributeId(int edegAttributeId) {
    this.edgeAttributeId = edegAttributeId;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object,
   * java.util.Locale)
   */
  public void initFrom(InformationFlowOptionsBean queryElement, Locale locale) {
    this.dialogStep = queryElement.getDialogStep();
    // quadrant values
    copyStringList(this.colorAttributeValues, queryElement.getColorOptionsBean().getAttributeValues());
    copyStringList(this.selectedColors, queryElement.getColorOptionsBean().getSelectedColors());
    copyStringList(this.lineAttributeValues, queryElement.getLineOptionsBean().getAttributeValues());
    copyStringList(this.selectedLineTypes, queryElement.getLineOptionsBean().getSelectedLineTypes());

    this.colorAttributeId = queryElement.getColorOptionsBean().getDimensionAttributeId();
    this.lineTypeAttributeId = queryElement.getLineOptionsBean().getDimensionAttributeId();

    this.selectedGraphicFormat = queryElement.getSelectedGraphicFormat();
    this.useNamesLegend = queryElement.isUseNamesLegend();
    this.useColorRange = queryElement.getColorOptionsBean().isUseColorRange();
    this.showSavedQueryInfo = queryElement.isShowSavedQueryInfo();
    this.selectedNodeLayout = queryElement.getSelectedNodeLayout();
    setEdgeCaptions(queryElement);
    this.showIsBusinessObjects = queryElement.isShowIsBusinessObjects();
    this.showIsBaseComponents = queryElement.isShowIsBaseComponents();
    this.selectedTemplateName = queryElement.getSelectedTemplateName();
  }

  /**
   * {@link #edgeCaptionType}
   */
  private void setEdgeCaptions(InformationFlowOptionsBean queryElement) {
    this.edgeCaptionType = 0;
    for (int selectionType : queryElement.getSelectionType()) {
      this.edgeCaptionType = this.edgeCaptionType * 10 + selectionType;
      if (selectionType == InformationFlowOptionsBean.LINE_DESCR_ATTRIBUTES) {
        edgeAttributeId = queryElement.getLineCaptionSelectedAttributeId().intValue();
      }
    }
  }

  /**
   * Creates an array with the selected edge caption types from the saved {@link #edgeCaptionType} int.
   */
  public int[] getEdgeCaptionTypes() {
    Set<Integer> captionTypesSet = Sets.newHashSet();
    int combinedCaptionType = this.edgeCaptionType;
    while (combinedCaptionType >= 1) {
      captionTypesSet.add(Integer.valueOf(combinedCaptionType % 10));
      combinedCaptionType = (int) Math.floor(combinedCaptionType / 10.0);
    }
    if (this.edgeAttributeId != -1) {
      captionTypesSet.add(Integer.valueOf(0));
    }

    int[] edgeCaptionTypes = new int[captionTypesSet.size()];
    int count = 0;
    for (Integer captionType : captionTypesSet) {
      edgeCaptionTypes[count++] = captionType.intValue();
    }

    return edgeCaptionTypes;
  }

  /**
   * Copies the values of one String list to another
   * 
   * @param dest
   *          The destination list
   * @param src
   *          The source list
   */
  private void copyStringList(List<String> dest, List<String> src) {
    if (src != null) {
      for (String str : src) {
        dest.add(str);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#update(java.lang.Object,
   * java.util.Locale)
   */
  public void update(InformationFlowOptionsBean queryElement, Locale locale) {
    // not needed as initialisation of the informationFlow options bean is done in
    // de.iteratec.iteraplan.businesslogic.service.InitFormHelperServiceImpl#initInformationFlowDiagramForm
    // due to the fact that calls to AttributeValueService are needed to copy the data from the XML
    // file
    // to the InformationFlowOptionsBean
  }

  /**
   * {@inheritDoc}
   */
  public void validate(Locale locale) {
    if (dialogStep != 1 && dialogStep != 2) {
      logError(dialogStep + " is not a valid dialog step");
    }
    if (colorAttributeId != null) {
      String errorMsg = ValidationHelper.validateColors(colorAttributeValues, selectedColors);
      if (errorMsg != null) {
        logError(errorMsg);
      }
    }

    if (lineTypeAttributeId != null) {
      String errorMsg = ValidationHelper.validateLineTypes(lineAttributeValues, selectedLineTypes);
      if (errorMsg != null) {
        logError(errorMsg);
      }
    }
  }

  public boolean isUseColorRange() {
    return useColorRange;
  }

  public void setUseColorRange(boolean useColorRange) {
    this.useColorRange = useColorRange;
  }
}