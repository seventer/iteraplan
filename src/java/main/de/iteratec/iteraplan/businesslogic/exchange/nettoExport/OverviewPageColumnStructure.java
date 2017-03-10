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
package de.iteratec.iteraplan.businesslogic.exchange.nettoExport;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.MultiassignementType;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.presentation.memory.ColumnDefinition;


/**
 * A {@link ColumnStructure} for columns in the overview page tables (wraps a {@link ColumnDefinition}). 
 * This class should produce the same result like the output section in "/WebContent/jsp/common/Results.jsp".
 */
public class OverviewPageColumnStructure extends AbstractColumnStructure {
  private ColumnDefinition columnDefinition;

  public OverviewPageColumnStructure(ColumnDefinition columnDefinition) {
    this.columnDefinition = columnDefinition;
  }

  /**{@inheritDoc}**/
  @Override
  public Object resolveValue(Object bean) {

    // Same logic like in Results.jsp here:

    BeanWrapperImpl wrapper = new BeanWrapperImpl(false);
    wrapper.setWrappedInstance(bean);
    Object resolvedObject = wrapper.getPropertyValue(columnDefinition.getBeanPropertyPath());

    AttributeType attrType = columnDefinition.getAttributeType();
    boolean multiValueType = (attrType instanceof MultiassignementType) && ((MultiassignementType) attrType).isMultiassignmenttype();

    if (attrType != null) {

      if (resolvedObject instanceof List<?>) {
        List<?> allValues = (List<?>) resolvedObject;

        if (multiValueType) {
          List<String> attributeValues = Lists.newArrayList();
          for (Object attrObj : allValues) {
            if (attrObj instanceof DateAV) {
              Date dateValue = ((DateAV) attrObj).getValue();
              attributeValues.add(dateValue.toString());
            }
            else if (attrObj instanceof ResponsibilityAV) {
              attributeValues.add(((ResponsibilityAV) attrObj).getName());
            }
            else {
              attributeValues.add(((AttributeValue) attrObj).getValueString());
            }
          }

          return Joiner.on(MULTIVALUE_SEPARATOR).join(attributeValues);
        }
        else {
          if (allValues == null || allValues.size() == 0) {
            return NOT_FOUND;
          }

          Object singleValue = allValues.get(0);
          if (singleValue instanceof ResponsibilityAV) {
            return ((ResponsibilityAV) singleValue).getName();
          }
          else {
            return ((AttributeValue) singleValue).getValue();
          }
        }
      }

    }
    else {
      if ("direction".equals(columnDefinition.getModelPath()) && (resolvedObject instanceof String)) {
        Direction directionForValue = Direction.getDirectionForValue(String.valueOf(resolvedObject));
        if (directionForValue != null) {
          return SHOW_DIRECTION_ARROWS ? directionForValue.toString() : directionForValue.name();
        }
      }

      if (columnDefinition.isInternationalized()) {
        return MessageAccess.getString(String.valueOf(resolvedObject));
      }

      return String.valueOf(resolvedObject);
    }

    return StringUtils.defaultString(String.valueOf(resolvedObject), NOT_FOUND);
  }

  /**{@inheritDoc}**/
  @Override
  public String getColumnHeader() {
    if (columnDefinition.getAttributeType() == null) {
      String tableHeaderKey = columnDefinition.getTableHeaderKey();
      return MessageAccess.getString(tableHeaderKey);
    }
    else {
      return columnDefinition.getTableHeaderKey();
    }
  }
}
