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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.variable;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointConfiguration;


public class ContinuousColoringVariable {

  protected static final String VC_OUT_OF_BOUNDS_COLOR = "outOfBoundsColor";
  protected static final String VC_MIN_COLOR           = "minColor";
  protected static final String VC_MAX_COLOR           = "maxColor";
  protected static final String VC_MIN_VALUE           = "maxValue";
  protected static final String VC_MAX_VALUE           = "minValue";

  protected static final String VV_CONTINUOUS          = "continuous";

  private String                outOfBoundsColor;
  private String                minColor;
  private String                maxColor;
  private String                minValue;
  private String                maxValue;

  public String getName() {
    return VV_CONTINUOUS;
  }

  //Note: no recommendations for now

  public void configureFrom(ViewpointConfiguration vpConfig) {
    this.outOfBoundsColor = vpConfig.get(VC_OUT_OF_BOUNDS_COLOR);
    this.minColor = vpConfig.get(VC_MIN_COLOR);
    this.maxColor = vpConfig.get(VC_MAX_COLOR);
    this.minValue = vpConfig.get(VC_MIN_VALUE);
    this.maxValue = vpConfig.get(VC_MAX_VALUE);
  }

  public String getOutOfBoundsColor() {
    return outOfBoundsColor;
  }

  public String getMinColor() {
    return minColor;
  }

  public String getMaxColor() {
    return maxColor;
  }

  public String getMinValue() {
    return minValue;
  }

  public String getMaxValue() {
    return maxValue;
  }

}
