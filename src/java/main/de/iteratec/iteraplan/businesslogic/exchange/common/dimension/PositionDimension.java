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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;


public class PositionDimension extends Dimension<Double> {

  protected static final Double DEFAULT_POSITION = Double.valueOf(-1);

  public PositionDimension(DimensionAdapter<?> adapter) {
    super(adapter);
  }

  @Override
  public Double getDefaultValue() {
    return DEFAULT_POSITION;
  }

  public void initNotNumberAt(List<String> values) {
    super.setMapping(calculateValueToWeightMap(values));
  }

  public void initNumberAt(NumberAT numberAt, List<NumberAV> numberAvs) {
    super.setMapping(getValuesToWeightForNumberAvs(numberAt, numberAvs));
  }

  private Map<String, Double> getValuesToWeightForNumberAvs(NumberAT numberAt, List<NumberAV> numberAvs) {
    if (numberAvs == null || numberAvs.isEmpty()) {
      return new HashMap<String, Double>();
    }
    List<BigDecimal> decimals = new ArrayList<BigDecimal>();
    for (NumberAV numberAv : numberAvs) {
      decimals.add(numberAv.getValue());
    }

    BigDecimal upperBound = numberAt.calculateUpperBoundForNumberAt(decimals);
    BigDecimal lowerBound = numberAt.calculateLowerBoundForNumberAt(decimals);

    Map<String, Double> map = new HashMap<String, Double>();
    if (upperBound != null && lowerBound != null) {
      if (lowerBound.equals(upperBound)) {
        for (NumberAV value : numberAvs) {
          map.put(value.getLocalizedValueString(getAdapter().getLocale()), Double.valueOf(0.5));
        }
      }
      else {
        double max = upperBound.doubleValue();
        double min = lowerBound.doubleValue();
        double difference = max - min;
        for (NumberAV value : numberAvs) {
          map.put(value.getLocalizedValueString(getAdapter().getLocale()), Double.valueOf(1 - ((max - value.getValue().doubleValue()) / (difference))));
        }
      }
    }

    return map;
  }

}