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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


public class ClusterSecondOrderBean implements Serializable {
  private static final long         serialVersionUID                = -7199235522546873572L;

  public static final String        BUILDING_BLOCK_BEAN             = "buildingBlockBean";
  public static final String        ATTRIBUTE_BEAN                  = "attributeBean";
  public static final boolean       ATTRIBUTE_BEAN_DEFAULT_SELECTED = false;

  private String                    name;

  private String                    representedType;

  private String                    beanType                        = BUILDING_BLOCK_BEAN;

  private List<BBAttribute>         availableAttributes;
  private ColorDimensionOptionsBean colorOptions                    = new ColorDimensionOptionsBean();

  private boolean                   selected                        = true;

  private List<String>              availableBbShapes               = new ArrayList<String>();
  private String                    selectedBbShape                 = Constants.REPORTS_EXPORT_CLUSTER_BB_SHAPE_RECTANGLE;

  public ClusterSecondOrderBean(String name, String type, List<BBAttribute> availableAttributes, String beanType) {

    this.name = name;
    this.representedType = type;
    this.beanType = beanType;
    this.availableAttributes = availableAttributes;

    List<String> availableColors = SpringGuiFactory.getInstance().getClusterColors();
    List<String> availableColorsToSet = new ArrayList<String>();
    for (String color : availableColors) {
      availableColorsToSet.add(color);
    }
    this.colorOptions.setAvailableColors(availableColorsToSet);

    availableBbShapes.add(Constants.REPORTS_EXPORT_CLUSTER_BB_SHAPE_RECTANGLE);
    availableBbShapes.add(Constants.REPORTS_EXPORT_CLUSTER_BB_SHAPE_ROUNDED);
    availableBbShapes.add(Constants.REPORTS_EXPORT_CLUSTER_BB_SHAPE_ARROW);

    this.selected = (this.beanType.equals(ATTRIBUTE_BEAN) ? ATTRIBUTE_BEAN_DEFAULT_SELECTED : true);
  }

  public List<BBAttribute> getAvailableAttributes() {
    return availableAttributes;
  }

  public String getRepresentedType() {
    return representedType;
  }

  public void setRepresentedType(String representedBbType) {
    this.representedType = representedBbType;
  }

  public void setAvailableAttributes(List<BBAttribute> availableAttributes) {
    this.availableAttributes = availableAttributes;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getSelectedBbShape() {
    return selectedBbShape;
  }

  public void setSelectedBbShape(String selectedBbShape) {
    this.selectedBbShape = selectedBbShape;
  }

  public List<String> getAvailableBbShapes() {
    return availableBbShapes;
  }

  /**
   * The name of the building block represented in this dimension.
   * 
   * @return The BB name.
   */
  public String getName() {
    return this.name;
  }

  public void setColorOptions(ColorDimensionOptionsBean colorOptions) {
    this.colorOptions = colorOptions;
  }

  public ColorDimensionOptionsBean getColorOptions() {
    return this.colorOptions;
  }

  public String getBeanType() {
    return beanType;
  }

  public void setBeanType(String beanType) {
    this.beanType = beanType;
  }
}
