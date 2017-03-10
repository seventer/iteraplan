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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.TypeOfAttribute;


/**
 *
 */
public class TimeseriesQuery implements Serializable {
  private static final long          serialVersionUID         = 1L;

  private Quantor                    quantor                  = Quantor.EXISTS;
  private QTimespanData              timespan                 = new QTimespanData(UserContext.getCurrentLocale());
  private QPart                      qPart                    = new QPart();
  private Map<String, List<NamedId>> availableAttributeValues = new HashMap<String, List<NamedId>>();
  private final List<BBAttribute>    availableAttributes;

  public enum Quantor {
    ALL("reports.timeseries.all") {
      @Override
      public boolean evaluate(Predicate<String> predicate, Collection<String> values) {
        boolean result = true;

        for (String value : values) {
          if (!predicate.apply(value)) {
            result = false;
            break;
          }
        }

        return result;
      }
    },
    EXISTS("reports.timeseries.exists") {
      @Override
      public boolean evaluate(Predicate<String> predicate, Collection<String> values) {
        boolean result = false;

        for (String value : values) {
          if (predicate.apply(value)) {
            result = true;
            break;
          }
        }

        return result;
      }
    };

    private final String name;

    /**
     * Default constructor.
     */
    private Quantor(String name) {
      this.name = name;
    }

    /**
     * @return name the name
     */
    public String getName() {
      return name;
    }

    public abstract boolean evaluate(Predicate<String> predicate, Collection<String> values);
  }

  public TimeseriesQuery(List<BBAttribute> availableAttributes) {
    this.availableAttributes = availableAttributes;
  }

  /**
   * @return availableAttributes the availableAttributes
   */
  public List<BBAttribute> getAvailableAttributes() {
    return availableAttributes;
  }

  /**
   * @return availableAttributeValues the availableAttributeValues
   */
  public Map<String, List<NamedId>> getAvailableAttributeValues() {
    return availableAttributeValues;
  }

  public void setAvailableAttributeValues(Map<String, List<NamedId>> availableAttributeValues) {
    this.availableAttributeValues = availableAttributeValues;
  }

  /**
   * @return qPart the qPart
   */
  public QPart getPart() {
    return qPart;
  }

  public void setPart(QPart qPart) {
    this.qPart = qPart;
  }

  /**
   * @return timespan the timespan
   */
  public QTimespanData getTimespan() {
    return timespan;
  }

  public void setTimespan(QTimespanData timespan) {
    this.timespan = timespan;
  }

  /**
   * @return quantor the quantor
   */
  public Quantor getQuantor() {
    return quantor;
  }

  public void setQuantor(Quantor quantor) {
    this.quantor = quantor;
  }

  public List<Quantor> getQuantors() {
    return Lists.newArrayList(Quantor.ALL, Quantor.EXISTS);
  }

  public boolean evaluate(TypeOfAttribute toa, Timeseries ts) {
    String value;
    if (qPart.getFreeTextCriteriaSelected().booleanValue()) {
      value = qPart.getFreeTextCriteria();
    }
    else {
      value = qPart.getExistingCriteria();
    }
    Date startDate = timespan.getStartDate();
    Date endDate = timespan.getEndDate();

    Collection<String> values = Timeseries.getValuesBetweenTimespan(startDate, endDate, ts);

    //create predicate according to the specified operator and evaluate
    Predicate<String> predicate = TimeseriesQueryPredicateFactory.createPredicate(toa, getPart().getChosenOperationId(), value);
    return quantor.evaluate(predicate, values);
  }

  public boolean isValid() {
    return (qPart.getChosenAttributeStringId() != null && !BBAttribute.UNDEFINED_ID_VALUE.equals(BBAttribute.getIdByStringId(qPart
        .getChosenAttributeStringId())));
  }

  public BBAttribute getBBAttributeByStringId(String id) {
    BBAttribute result = null;

    for (BBAttribute attr : availableAttributes) {
      if (attr.getStringId().equals(id)) {
        result = attr;
      }
    }

    return result;
  }
}
