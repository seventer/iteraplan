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
package de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans;

import de.iteratec.iteraplan.common.util.IteraplanProperties;


public class LandscapeDiagram {
  private final String title;
  private Axis         topAxis                     = null;
  private Axis         sideAxis                    = null;
  private Content      content                     = null;
  private boolean      columnAxisScalesWithContent = false;
  private boolean      scaleDownContentElements    = false;
  private boolean      useNamesLegend              = true;

  /**
   * @param title
   * @param topAxis
   * @param sideAxis
   * @param content
   * @param columnAxisScalesWithContent
   */
  public LandscapeDiagram(String title, Axis topAxis, Axis sideAxis, Content content, boolean columnAxisScalesWithContent) {
    super();
    this.title = title;
    this.topAxis = topAxis;
    this.sideAxis = sideAxis;
    this.content = content;
    this.columnAxisScalesWithContent = columnAxisScalesWithContent;
  }

  /**
   * Retrieves (from the properties) whether the content elements of the landscape diagram are to be scaled down.
   * @return true, if and only if the property is set to true, or no value is set.
   */
  public static boolean isScaleDownLandscapeContentElements() {

    String scaleDownStr = IteraplanProperties.getProperties().getProperty(IteraplanProperties.EXPORT_GRAPHICAL_LANDSCAPE_SCALE_DOWN_GRAPHIC_ELEMENTS);

    if (scaleDownStr != null && !scaleDownStr.matches("//s*")
        && (scaleDownStr.matches(Boolean.toString(true)) || scaleDownStr.matches(Boolean.toString(false)))) {
      Boolean scaleDownBool = Boolean.valueOf(scaleDownStr);

      return scaleDownBool.booleanValue();
    }

    //default is true
    return true;
  }

  public Content getContent() {
    return content;
  }

  public String getTitle() {
    return title;
  }

  public Axis getSideAxis() {
    return sideAxis;
  }

  public Axis getTopAxis() {
    return topAxis;
  }

  public boolean isContentScalesVertically() {
    return columnAxisScalesWithContent;
  }

  public boolean isScaleDownContentElements() {
    return scaleDownContentElements;
  }

  public void setScaleDownContentElements(boolean scaleDownContentElements) {
    this.scaleDownContentElements = scaleDownContentElements;
  }

  public boolean isUseNamesLegend() {
    return useNamesLegend;
  }

  public void setUseNamesLegend(boolean useNamesLegend) {
    this.useNamesLegend = useNamesLegend;
  }
}