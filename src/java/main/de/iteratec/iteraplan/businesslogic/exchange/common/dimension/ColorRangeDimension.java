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
package de.iteratec.iteraplan.businesslogic.exchange.common.dimension;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;


public class ColorRangeDimension extends ColorDimension {

  private static final Logger LOGGER            = Logger.getIteraplanLogger(ColorRangeDimension.class);

  private Map<String, String> legendKeyPrefixes = Maps.newHashMap();
  private List<String>        valuesForLegend   = Lists.newArrayList();

  public ColorRangeDimension(DimensionAdapter<?> adapter) {
    super(adapter);
  }

  public void initValues(List<String> values, ColorGenerator colorGenerator) {
    Map<String, Color> mapping = Maps.newHashMap();
    for (String valueStr : values) {
      BigDecimal bigDecimalValue = BigDecimalConverter.parse(valueStr, getAdapter().getLocale());
      mapping.put(valueStr, colorGenerator.generateColor(bigDecimalValue));
    }
    valuesForLegend.add(values.get(0));
    valuesForLegend.add(values.get(values.size() - 1));
    super.setMapping(mapping);
  }

  @Override
  public void init(List<Color> values) {
    LOGGER.error("Function init(List<Color> values) should not be used for SizeDimension.");
    throw new IteraplanTechnicalException();
  }

  /**{@inheritDoc}**/
  @Override
  public List<String> getValues() {
    return valuesForLegend;
  }

  public String getLegendPrefixKeyFor(String value) {
    return legendKeyPrefixes.get(value);
  }

  public void setLegendKeyPrefixes(String lowerBound, String upperBound) {
    this.legendKeyPrefixes.put(valuesForLegend.get(0), lowerBound);
    this.legendKeyPrefixes.put(valuesForLegend.get(1), upperBound);
  }
}
