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
package de.iteratec.iteraplan.businesslogic.exchange.common.piebar;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesType;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;


/**
 * Class holding information about a single pie or bar, like values and according sizes 
 */
public class PieBar {

  private static final Logger      LOGGER          = Logger.getIteraplanLogger(PieBar.class);

  private String                   label           = "";
  private final ValuesType         type;

  private final Map<String, Count> valuesToSize    = new LinkedHashMap<String, Count>();
  private int                      totalSize;
  private int                      realSize;
  private boolean                  multiValueATBar = false;

  public PieBar(ValuesType type, List<String> values) {
    LOGGER.debug("initializing PieBar");
    this.type = type;
    for (String value : values) {
      valuesToSize.put(value, new Count());
    }
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ValuesType getType() {
    return type;
  }

  public Map<String, Integer> getValuesToSizeMap() {
    Map<String, Integer> resultMap = new LinkedHashMap<String, Integer>();
    for (Map.Entry<String, Count> entry : valuesToSize.entrySet()) {
      resultMap.put(entry.getKey(), Integer.valueOf(entry.getValue().getCount()));
    }
    return resultMap;
  }

  public int getTotalSize() {
    return totalSize;
  }

  public int getRealSize() {
    return realSize;
  }

  public void add(List<String> values) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("adding values to PieBar: {0}", values.toString());
    }
    List<String> toIncrease = CollectionUtils.arrayList();
    switch (type) {
      case MAINTAINED:
        if (values.isEmpty()) {
          toIncrease.add(DimensionOptionsBean.DEFAULT_VALUE);
        }
        else {
          toIncrease.add("graphicalReport.specified");
        }
        break;
      case VALUES:
        if (values.isEmpty()) {
          toIncrease.add(DimensionOptionsBean.DEFAULT_VALUE);
        }
        for (String value : values) {
          toIncrease.add(value);
        }
        break;
      case COUNT:
        if (values.isEmpty()) {
          toIncrease.add(DimensionOptionsBean.DEFAULT_VALUE);
        }
        else {
          toIncrease.add(String.valueOf(values.size()));
        }
        break;
      default:
        LOGGER.error("Invalid ValuesType: {0}", type.name());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
    addToMap(toIncrease);
  }

  private void addToMap(List<String> toIncrease) {
    for (String key : toIncrease) {
      if (!valuesToSize.containsKey(key)) {
        LOGGER.error("expected value not found: {0}", key);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
      }

      valuesToSize.get(key).inc();
      totalSize++;
    }
    realSize++;
  }

  public void setMultiValueATBar(boolean isMultiValueATBar) {
    this.multiValueATBar = isMultiValueATBar;
  }

  public boolean isMultiValueATBar() {
    return multiValueATBar;
  }

  private static class Count {
    private int cnt = 0;

    public int getCount() {
      return cnt;
    }

    public void inc() {
      this.cnt++;
    }
  }

}
