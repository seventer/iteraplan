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
package de.iteratec.iteraplan.businesslogic.exchange.common.neighbor;

import java.awt.Color;
import java.util.List;
import java.util.Map;


/**
 * Contains the general configuration information for the created visualization export
 */
public class NeighborhoodDiagram {

  /**
   * Constants that are used in the graphic generation for Visio and SVG
   */
  public static final int                     BLOCK_WIDTH               = 120;
  public static final int                     BLOCK_HEIGHT              = 40;
  public static final int                     BLOCK_DISTANCE            = 180;
  public static final int                     COLOR_LEGEND_WIDTH        = 200;
  public static final int                     NAME_LEGEND_ENTRY_HEIGHT  = 20;
  public static final int                       MARGIN_NAME_LEGEND       = 5;
  public static final int                       OOI_LINE_WIDTH           = 3;

  public static final double                    DEFAUL_CONTENT_SIZE      = 500.0;
  public static final double                    LEGEND_ENTRY_HEIGHT      = 32.0;
  public static final double                    ROOT_MARGIN              = 200;
  public static final double                    SIMPLE_MARGIN            = ROOT_MARGIN / 2;
  public static final double                    DEFAULT_LEGEND_MARGIN    = 20.0;

  public static final String                    DEFAULT_FONT_SIZE        = "11";
  public static final String                    DEFAULT_FONT             = "Arial";
  public static final String                    MESSAGE_TYPE_OF_STATUS   = "global.type_of_status";
  public static final String                    TITLE                    = "graphicalExport.neighborhood.title";

  public static final Color                   STATUS_CURRENT_COLOR      = new Color(175, 206, 168);
  public static final Color                   STATUS_PLANNED_COLOR      = new Color(246, 223, 149);
  public static final Color                   STATUS_TARGET_COLOR       = new Color(215, 157, 173);
  public static final Color                   STATUS_INACTIVE_COLOR     = new Color(136, 174, 217);

  private SpatialInfromationSystemWrapper      objectOfInterest;
  private List<SpatialInfromationSystemWrapper> connectedInformationSystems;
  private Map<Color, String>                  colorLegend;
  private double                              sideLength                   = 500;
  private double                              totalHeight;
  private double                              totalWidth;

  public String toHexString(Color colour, boolean withHash) {
    String hexColour = Integer.toHexString(colour.getRGB() & 0xffffff);
    if (hexColour.length() < 6) {
      hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
    }
    if (withHash) {
      return "#" + hexColour;
    }
    else {
      return hexColour;
    }
  }

  public SpatialInfromationSystemWrapper getObjectOfInterest() {
    return objectOfInterest;
  }

  public void setObjectOfInterest(SpatialInfromationSystemWrapper objectOfInterest) {
    this.objectOfInterest = objectOfInterest;
  }

  public List<SpatialInfromationSystemWrapper> getConnectedInformationSystems() {
    return connectedInformationSystems;
  }

  public void setConnectedInformationSystems(List<SpatialInfromationSystemWrapper> connectedInformationSystems) {
    this.connectedInformationSystems = connectedInformationSystems;
  }

  public Map<Color, String> getColorLegend() {
    return colorLegend;
  }

  public void setColorLegend(Map<Color, String> colorLegend) {
    this.colorLegend = colorLegend;
  }


  public double getSideLength() {
    return sideLength;
  }

  public void setSideLength(double sideLength) {
    this.sideLength = sideLength;
  }

  public double getTotalHeight() {
    return totalHeight;
  }

  public void setTotalHeight(double totalHeight) {
    this.totalHeight = totalHeight;
  }

  public double getTotalWidth() {
    return totalWidth;
  }

  public void setTotalWidth(double totalWidth) {
    this.totalWidth = totalWidth;
  }


}
