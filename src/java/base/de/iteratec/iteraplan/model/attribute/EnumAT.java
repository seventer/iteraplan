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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.attribute.util.TimeseriesHelper;


/**
 * The model class for enumeration attribute types.
 */
@Entity
@Audited
public class EnumAT extends AttributeType implements MultiassignementType, TimeseriesType {

  private static final long serialVersionUID = -8116758520336549446L;

  private boolean           multiassignmenttype;
  private boolean           timeseries;

  // this is a set for it to work with hibernate envers for our history functionality
  private Set<EnumAV>       attributeValues  = Sets.newHashSet();

  public EnumAT() {
    super();
  }

  public boolean isMultiassignmenttype() {
    return multiassignmenttype;
  }

  @Override
  public Collection<? extends AttributeValue> getAllAttributeValues() {
    return getAttributeValues();
  }

  @AuditMappedBy(mappedBy = "attributeType")
  public Set<EnumAV> getAttributeValues() {
    return attributeValues;
  }

  public List<EnumAV> getSortedAttributeValues() {
    return TypeOfAttribute.ENUM.sort(Lists.newArrayList(attributeValues));
  }

  public void setMultiassignmenttype(boolean multivalue) {
    this.multiassignmenttype = multivalue;
  }

  public void setAttributeValues(Set<EnumAV> attributeValues) {
    this.attributeValues = attributeValues;
  }

  /**
   * Sets relation between this attribute type and the given attribute value on both sides.
   * Also initializes the position of the attribute value to be inserted after the existing values.
   * @param av the attribute value to add
   */
  public void addAttribueValueTwoWay(EnumAV av) {
    av.setPosition(getHighestPosition() + 1);
    attributeValues.add(av);
    av.setAttributeType(this);
  }

  /**
   * Sets relation between this attribute type and the given attribute values on both sides.
   * Also initializes the positions of the attribute values to be inserted after the existing values.
   * @param elementsToAdd the attribute values to add
   */
  public void addAttributeValuesTwoWay(List<EnumAV> elementsToAdd) {
    int lastPos = getHighestPosition() + 1;
    for (EnumAV attributeValue : elementsToAdd) {
      attributeValue.setAttributeType(this);
      attributeValue.setPosition(lastPos++);
    }
    attributeValues.addAll(elementsToAdd);
  }

  private int getHighestPosition() {
    int max = -1;
    for (EnumAV av : attributeValues) {
      max = Math.max(max, av.getPosition());
    }
    return max;
  }

  public void removeAttributeValuesTwoWay() {
    for (EnumAV av : attributeValues) {
      av.setAttributeType(null);
    }
    attributeValues.clear();
  }

  /**
   * Retrieves the {@link EnumAV} from the attribute values associated with this enumeration
   * attribute type specified by the given ID. If no such value exists, {@code null} is returned.
   * 
   * @param id
   *          The ID of the attribute value to be returned.
   * @return See method description.
   */
  public EnumAV getAttributeValueById(Integer id) {
    for (EnumAV av : this.getAttributeValues()) {
      if (id.equals(av.getId())) {
        return av;
      }
    }
    return null;
  }

  @Override
  public Class<? extends AttributeValue> getClassOfAttributeValue() {
    return EnumAV.class;
  }

  @Override
  public TypeOfAttribute getTypeOfAttribute() {
    return TypeOfAttribute.ENUM;
  }

  @Override
  public void validate() {
    super.validate();

    // no duplicate attribute value names are allowed
    checkForDuplicates();

    // no empty names allowed
    checkForInvalidNames();

    checkTimeseriesConditions();
  }

  /**
   * Checks, if the given list of attribute values of type {@link EnumAV} contains elements with
   * duplicate names.
   * 
   * @throws de.iteratec.iterplan.common.IteraplanBusinessException
   *           If two attribute values in the list have the same name.
   */
  private void checkForDuplicates() {
    if (attributeValues == null || attributeValues.size() < 2) {
      return;
    }

    Set<String> names = new HashSet<String>();
    for (EnumAV av : attributeValues) {
      if (names.contains(av.getName())) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.ATTRVAL_EXISTS);
      }
      names.add(av.getName());
    }
  }

  /**
   * Checks, if the given list of attribute values of type {@link EnumAV} contains elements with no
   * names or names that only consist of whitespaces.
   * 
   * @throws de.iteratec.iterplan.common.IteraplanBusinessException
   *           If an attribute value with no name or an empty name is found.
   */
  private void checkForInvalidNames() {
    for (EnumAV eav : attributeValues) {
      String eavName = eav.getName();
      if (StringUtils.isBlank(eavName)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.INVALID_ATTRIBUTE_VALUE_NAME);
      }
    }
  }

  private void checkTimeseriesConditions() {
    if (timeseries) {
      if (multiassignmenttype) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.ILLEGAL_MULTIVALUE_TIMESERIES);
      }
      TimeseriesHelper.validateAssignedBuildingBlockTypes(this);
    }
  }

  public boolean isTimeseries() {
    return timeseries;
  }

  public void setTimeseries(boolean isTimeseries) {
    this.timeseries = isTimeseries;
  }
}
