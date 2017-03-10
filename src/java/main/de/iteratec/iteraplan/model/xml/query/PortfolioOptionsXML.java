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

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.PortfolioOptionsBean;


@XmlType(name = "portfolioOptions", propOrder = { "colorAttributeId", "colorAttributeValues", "scalingEnabled", "selectedBbType", "selectedColors",
    "sizeAttributeId", "XAxisAttributeId", "YAxisAttributeId", "selectedGraphicsFormat", "useColorRange", "useNamesLegend", "showSavedQueryInfo" })
public class PortfolioOptionsXML extends AbstractXMLElement<PortfolioOptionsBean> {

  private int          dialogStep           = 1;
  private Integer      xAxisAttributeId     = null;
  private Integer      yAxisAttributeId     = null;
  private Integer      sizeAttributeId      = Integer.valueOf(-1);
  private Integer      colorAttributeId     = Integer.valueOf(-1);
  private List<String> colorAttributeValues = new ArrayList<String>();
  private List<String> selectedColors       = new ArrayList<String>();
  private boolean      scalingEnabled       = false;
  private boolean      useNamesLegend       = true;
  private boolean      showSavedQueryInfo;
  private boolean      useColorRange        = false;

  private String       selectedBbType;
  private String       selectedGraphicsFormat;

  @XmlAttribute(required = true)
  public int getDialogStep() {
    return dialogStep;
  }

  @XmlElement
  public Integer getXAxisAttributeId() {
    return xAxisAttributeId;
  }

  @XmlElement
  public Integer getYAxisAttributeId() {
    return yAxisAttributeId;
  }

  @XmlElement
  public Integer getSizeAttributeId() {
    return sizeAttributeId;
  }

  @XmlElement
  public Integer getColorAttributeId() {
    return colorAttributeId;
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

  @XmlElement
  public String getSelectedBbType() {
    return this.selectedBbType;
  }

  public void setDialogStep(int dialogStep) {
    this.dialogStep = dialogStep;
  }

  public void setXAxisAttributeId(Integer axisAttributeId) {
    xAxisAttributeId = axisAttributeId;
  }

  public void setYAxisAttributeId(Integer axisAttributeId) {
    yAxisAttributeId = axisAttributeId;
  }

  public void setSizeAttributeId(Integer sizeAttributeId) {
    this.sizeAttributeId = sizeAttributeId;
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

  public void setSelectedBbType(String type) {
    this.selectedBbType = type;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object,
   * java.util.Locale)
   */
  public void initFrom(PortfolioOptionsBean portfolioOptions, Locale locale) {
    this.dialogStep = portfolioOptions.getDialogStep();
    copyStringList(this.getColorAttributeValues(), portfolioOptions.getColorOptionsBean().getAttributeValues());
    copyStringList(this.getSelectedColors(), portfolioOptions.getColorOptionsBean().getSelectedColors());
    this.selectedBbType = portfolioOptions.getSelectedBbType();
    this.scalingEnabled = portfolioOptions.isScalingEnabled();
    this.selectedGraphicsFormat = portfolioOptions.getSelectedGraphicFormat();
    this.useNamesLegend = portfolioOptions.isUseNamesLegend();
    this.showSavedQueryInfo = portfolioOptions.isShowSavedQueryInfo();
    this.xAxisAttributeId = portfolioOptions.getXAxisAttributeId();
    this.yAxisAttributeId = portfolioOptions.getYAxisAttributeId();
    this.sizeAttributeId = portfolioOptions.getSizeAttributeId();
    this.colorAttributeId = portfolioOptions.getColorOptionsBean().getDimensionAttributeId();
    this.useColorRange = portfolioOptions.getColorOptionsBean().isUseColorRange();
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
  public void update(PortfolioOptionsBean queryElement, Locale locale) {
    // not needed as initialisation of the portfolio options bean is done in
    // de.iteratec.iteraplan.businesslogic.service.InitFormHelperServiceImpl#initPortfolioDiagramForm
    // due to the fact that calls to AttributeValueService are needed to copy the data from the XML
    // file
    // to the PortfolioOptionsBean
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
    if (sizeAttributeId == null) {
      logError("The ID of the chosen size attribute is null");
    }
    if (xAxisAttributeId == null) {
      logError("The ID of the chosen x axis attribute is null");
    }
    if (yAxisAttributeId == null) {
      logError("The ID of the chosen y axis attribute is null");
    }
  }

  public void setScalingState(boolean scalingEnabled) {
    this.scalingEnabled = scalingEnabled;
  }

  @XmlElement
  public boolean isScalingEnabled() {
    return this.scalingEnabled;
  }

  public void setSelectedGraphicsFormat(String selectedGraphicsFormat) {
    this.selectedGraphicsFormat = selectedGraphicsFormat;
  }

  @XmlElement
  public String getSelectedGraphicsFormat() {
    return this.selectedGraphicsFormat;
  }

  @XmlElement
  public boolean isUseNamesLegend() {
    return useNamesLegend;
  }

  public void setUseNamesLegend(boolean useNamesLegend) {
    this.useNamesLegend = useNamesLegend;
  }

  @XmlElement
  public boolean isShowSavedQueryInfo() {
    return showSavedQueryInfo;
  }

  public void setShowSavedQueryInfo(boolean showSavedQueryInfo) {
    this.showSavedQueryInfo = showSavedQueryInfo;
  }

  public boolean isUseColorRange() {
    return useColorRange;
  }

  public void setUseColorRange(boolean useColorRange) {
    this.useColorRange = useColorRange;
  }
}
