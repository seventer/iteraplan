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

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.SingleBarOptionsBean;


@XmlType(name = "singleBarColorDimension", propOrder = { "label", "childrenColorAttributeValues", "childrenSelectedColors", "useColorRange" })
public class SingleBarColorOptionsXML extends AbstractXMLElement<SingleBarOptionsBean> {

  private int          colorAttribute;
  private boolean      selected                     = true;
  private List<String> childrenColorAttributeValues = new ArrayList<String>();
  private List<String> childrenSelectedColors       = new ArrayList<String>();
  private String       label;
  private boolean      useColorRange                = false;

  /**
   * Empty constructor needed for JAXB. DO NOT USE WITHIN THE APPLICATION!
   */
  public SingleBarColorOptionsXML() {
    // empty constructor needed for JAXB. DO NOT USE
  }

  @XmlAttribute(required = true)
  public boolean isSelected() {
    return selected;
  }

  @XmlAttribute(required = true)
  public int getColorAttribute() {
    return colorAttribute;
  }

  @XmlElementWrapper(name = "childrenColorAttributeValues")
  @XmlElement(name = "childValue")
  public List<String> getChildrenColorAttributeValues() {
    return childrenColorAttributeValues;
  }

  @XmlElementWrapper(name = "childrenSelectedColors")
  @XmlElement(name = "childColor")
  public List<String> getChildrenSelectedColors() {
    return childrenSelectedColors;
  }

  @XmlElement
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public void setColorAttribute(int colorAttribute) {
    this.colorAttribute = colorAttribute;
  }

  public void setChildrenColorAttributeValues(List<String> colorAttributeValues) {
    this.childrenColorAttributeValues = colorAttributeValues;
  }

  public void setChildrenSelectedColors(List<String> selectedColors) {
    this.childrenSelectedColors = selectedColors;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.model.xml.query.QueryXMLElement#initFrom(java.lang.Object,
   * java.util.Locale)
   */
  public void initFrom(SingleBarOptionsBean dimension, Locale locale) {
    copyStringList(this.childrenColorAttributeValues, dimension.getColorOptions().getAttributeValues());
    copyStringList(this.childrenSelectedColors, dimension.getColorOptions().getSelectedColors());
    this.colorAttribute = dimension.getColorOptions().getDimensionAttributeId().intValue();
    this.useColorRange = dimension.getColorOptions().isUseColorRange();
    this.label = dimension.getLabel();
    this.selected = dimension.isSelected();
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
  public void update(SingleBarOptionsBean queryElement, Locale locale) {
    // not needed as initialisation of the clusterGraphic is done in
    // de.iteratec.iteraplan.businesslogic.service.InitFormHelperServiceImpl#initPieBarDiagramForm
  }

  public void validate(Locale locale) {
    boolean attributeValuesNotSet = childrenColorAttributeValues == null && childrenSelectedColors != null;
    boolean selectedColorsNotSet = childrenColorAttributeValues != null && childrenSelectedColors == null;
    if ((colorAttribute != -1) && (attributeValuesNotSet || selectedColorsNotSet)) {
      logError("Either the color attribute values or the selected colors are not set");
    }
  }

  public boolean isUseColorRange() {
    return useColorRange;
  }

  public void setUseColorRange(boolean useColorRange) {
    this.useColorRange = useColorRange;
  }
}