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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.legend;

import de.iteratec.visualizationmodel.APlanarSymbol;
import de.iteratec.visualizationmodel.Color;


/**
 * Class representing an entry in a legend
 */
public class ColorLegendEntry implements Comparable<ColorLegendEntry> {
  /** Symbol shown in the first column of the legend for this entry */
  private APlanarSymbol entrySymbol;
  /** Text shown next to the symbol explaining the meaning */
  private final String  entryLabel;
  /** property value this legend entry is based on */
  private final Color   propertyValue;

  public ColorLegendEntry(APlanarSymbol entrySymbol, String entryLabel, Color propertyValue) {
    this.entrySymbol = entrySymbol;
    this.entryLabel = entryLabel;
    this.propertyValue = propertyValue;
  }

  public APlanarSymbol getEntrySymbol() {
    return entrySymbol;
  }

  public void setEntrySymbol(APlanarSymbol entrySymbol) {
    this.entrySymbol = entrySymbol;
  }

  public String getEntryLabel() {
    return entryLabel;
  }

  public Object getPropertyValue() {
    return propertyValue;
  }

  /**{@inheritDoc}**/
  public int compareTo(ColorLegendEntry o) {
    try {
      return Double.valueOf(entryLabel).compareTo(Double.valueOf(o.getEntryLabel()));
    } catch (NumberFormatException nfe) {
      if ("null".equals(entryLabel)) {
        return -1;
      }
      else {
        return 1;
      }
    }
  }

  /**
   * {@inheritDoc}*
   * Auto generated hash method
   */
  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + ((entryLabel == null) ? 0 : entryLabel.hashCode());
    result = prime * result + ((entrySymbol == null) ? 0 : entrySymbol.hashCode());
    result = prime * result + ((propertyValue == null) ? 0 : propertyValue.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}*
   * 
   * Auto generated euqals method
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ColorLegendEntry other = (ColorLegendEntry) obj;
    if (entryLabel == null) {
      if (other.entryLabel != null) {
        return false;
      }
    }
    else if (!entryLabel.equals(other.entryLabel)) {
      return false;
    }
    if (entrySymbol == null) {
      if (other.entrySymbol != null) {
        return false;
      }
    }
    else if (!entrySymbol.equals(other.entrySymbol)) {
      return false;
    }
    if (propertyValue == null) {
      if (other.propertyValue != null) {
        return false;
      }
    }
    else if (!propertyValue.equals(other.propertyValue)) {
      return false;
    }
    return true;
  }

}
