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
package de.iteratec.iteraplan.model.attribute;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.interfaces.ValidatableEntity;


@Entity
public class Timeseries implements IdentityEntity, ValidatableEntity {
  private static final long          serialVersionUID = -8936630591256820819L;

  private Integer                    id;
  private Integer                    olVersion;
  private BuildingBlock              buildingBlock;
  private AttributeType              attribute;
  // using HashMap instead of Map because Map is not explicitly serializable
  private TreeMap<LocalDate, String> values           = Maps.newTreeMap(Ordering.natural());

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getOlVersion() {
    return olVersion;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  public String getIdentityString() {
    return id.toString();
  }

  public BuildingBlock getBuildingBlock() {
    return buildingBlock;
  }

  public void setBuildingBlock(BuildingBlock buildingBlock) {
    this.buildingBlock = buildingBlock;
  }

  public AttributeType getAttribute() {
    return attribute;
  }

  public void setAttribute(AttributeType attribute) {
    if (attribute instanceof TimeseriesType && ((TimeseriesType) attribute).isTimeseries()) {
      this.attribute = attribute;
    }
    else {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NO_TIMESERIES_ATTRIBUTE, attribute);
    }
  }

  /**
   * Returns a list of the entries in this Timeseries, ordered by date.
   * Changes to the list will not be reflected in the timeseries and vice versa.
   * @return a List of {@link TimeseriesEntry}. Can not be null.
   */
  public List<TimeseriesEntry> getEntries() {
    List<TimeseriesEntry> result = Lists.newArrayList();
    for (Map.Entry<LocalDate, String> entry : values.entrySet()) {
      result.add(new TimeseriesEntry(entry.getKey().toDate(), entry.getValue()));
    }
    return result;
  }

  /**
   * Sets the entries of the timeseries. Discards all old entries.
   * @param entries
   *          Timeseries entries to set
   */
  public void setEntries(List<TimeseriesEntry> entries) {
    this.values.clear();
    addEntries(entries);
  }

  public void addEntries(List<TimeseriesEntry> entries) {
    for (TimeseriesEntry entry : entries) {
      addEntry(entry);
    }
  }

  public void addEntry(TimeseriesEntry entry) {
    String value = getCheckedValue(entry.getValue());
    this.values.put(new LocalDate(entry.getDate()), value);
  }

  /**
   * In case of a number attribute, this method returns the given string value after
   * making sure it is formatted in English locale. This is necessary for later parsing.
   * Does not modify the given value if the attribute type is not a number attribute type.
   * @param value
   *          String value to check
   * @return The valid value
   */
  private String getCheckedValue(String value) {
    if (attribute instanceof NumberAT) {
      BigDecimal numberValue = BigDecimalConverter.parse(value, UserContext.getCurrentLocale());
      return BigDecimalConverter.format(numberValue, true, Locale.ENGLISH);
    }
    else {
      return value;
    }
  }

  public void removeEntry(Date dateToRemove) {
    values.remove(new LocalDate(dateToRemove));
  }

  public TimeseriesEntry getLatestEntry() {
    if (values.isEmpty()) {
      return null;
    }

    LocalDate latestDate = new LocalDate(0);
    for (LocalDate date : values.keySet()) {
      if (latestDate.isBefore(date)) {
        latestDate = date;
      }
    }
    return new TimeseriesEntry(latestDate.toDate(), values.get(latestDate));
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder(111, 39);
    builder.append(id).append(buildingBlock).append(attribute).append(values);
    return builder.toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    Timeseries other = (Timeseries) obj;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(this.id, other.id);
    builder.append(this.buildingBlock, other.buildingBlock);
    builder.append(this.attribute, other.attribute);
    builder.append(this.values, other.values);
    return builder.isEquals();
  }

  @Override
  public String toString() {
    return "{" + id + "; BB: " + buildingBlock + "; AT: " + attribute + "; Values: " + getEntries() + "}";
  }

  public void validate() {
    LocalDate now = LocalDate.now();
    for (LocalDate entryDate : values.keySet()) {
      if (now.isBefore(entryDate)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.TIMESERIES_INVALID_FUTURE_DATE);
      }
    }
  }

  // used by hibernate
  @SuppressWarnings("unused")
  private String getSeries() {
    return TimeseriesSerializer.serialize(getEntries());
  }

  // used by hibernate
  @SuppressWarnings("unused")
  private void setSeries(String serialized) {
    setEntries(TimeseriesSerializer.deserialize(serialized));
  }

  public static Collection<String> getValuesBetweenTimespan(Date start, Date end, Timeseries series) {
    Collection<String> result = Sets.newHashSet();

    String prevValue = null;
    if (series != null) {
      for (TimeseriesEntry entry : series.getEntries()) {
        //because we are sorted by date we can skip if we passed the specified timespan
        if (end != null && entry.getDate().after(end)) {
          break;
        }
        if (start == null || entry.getDate().compareTo(start) > 0) {
          if (result.size() == 0) {
            result.add(prevValue);
          }
          result.add(entry.getValue());
        }
        else if (entry.getDate().compareTo(start) == 0) {
          result.add(entry.getValue());
        }
        else {
          //cache the current entry to be able to add it as the first entry if we enter the specified timespan
          prevValue = entry.getValue();
        }
      }
    }

    if (result.isEmpty()) {
      result.add(prevValue);
    }

    return result;
  }

  public static class TimeseriesEntry implements Comparable<TimeseriesEntry> {
    private final Date   date;
    private final String value;

    public TimeseriesEntry(Date date, String value) {
      assert (date != null);
      assert (value != null);
      this.date = date;
      this.value = value;
    }

    public Date getDate() {
      return date;
    }

    public String getValue() {
      return value;
    }

    public int compareTo(TimeseriesEntry o) {
      return this.date.compareTo(o.getDate());
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      else if (this == obj) {
        return true;
      }
      else if (!getClass().equals(obj.getClass())) {
        return false;
      }
      else {
        TimeseriesEntry other = (TimeseriesEntry) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(this.date, other.date).append(this.value, other.value);
        return builder.isEquals();
      }
    }

    @Override
    public int hashCode() {
      HashCodeBuilder builder = new HashCodeBuilder(117, 39);
      builder.append(this.date).append(this.value);
      return builder.toHashCode();
    }

    @Override
    public String toString() {
      return "[" + DateFormat.getDateInstance().format(date) + ": \"" + value + "\"]";
    }
  }

  public static final class TimeseriesSerializer {
    private TimeseriesSerializer() {
      // private constructor to avoid instantiation
    }

    public static String serialize(List<TimeseriesEntry> timeseries) {
      Gson gson = createGson();
      Type typeOfSrc = getType();
      return gson.toJson(timeseries, typeOfSrc);
    }

    public static List<TimeseriesEntry> deserialize(String serialized) {
      Gson gson = createGson();
      Type typeOfSrc = getType();
      return gson.fromJson(serialized, typeOfSrc);
    }

    private static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.setDateFormat("yyyy-MM-dd");
      return builder.create();
    }

    private static Type getType() {
      Type typeOfSrc = new TypeToken<List<TimeseriesEntry>>() {
        // nothing to do here
      }.getType();
      return typeOfSrc;
    }
  }
}
