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

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.SealState;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TypeOfAttribute;


/**
 * A {@link ColumnStructure} for columns in the spreadsheet report tables (wraps a {@link ColumnEntry}). 
 * This class should produce the same result like the output section in "/WebContent/jsp/commonReporting/resultPages/ResultPageTile.jsp".
 */
public class SpreadsheetReportColumnStructure extends AbstractColumnStructure {

  private final ColumnEntry columnEntry;

  private final COLUMN_TYPE colType;
  private final String      beanPath;

  public SpreadsheetReportColumnStructure(ColumnEntry columnEntry) {
    this.columnEntry = columnEntry;

    //    this.dateFormat = new SimpleDateFormat(dateFormatPattern, UserContext.getCurrentLocale());
    this.colType = ColumnEntry.COLUMN_TYPE.get(columnEntry.getType());
    this.beanPath = getBeanPath();
  }

  /**{@inheritDoc}**/
  @Override
  public String resolveValue(Object bean) {

    // Use same logic like in ResultPageTile.jsp:

    BeanWrapperImpl wrapper = new BeanWrapperImpl(false);
    wrapper.setWrappedInstance(bean);
    Object resolvedObject = wrapper.getPropertyValue(beanPath);

    switch (colType) {

      case ATTRIBUTE:
        return resolveAttributeValue(resolvedObject);

      case LIST:
        if (resolvedObject instanceof Iterable<?>) {
          List<String> valuesList = Lists.newArrayList();
          for (Object obj : (Iterable<?>) resolvedObject) {
            valuesList.add(String.valueOf(obj));
          }
          if (valuesList.size() > 0) {
            return Joiner.on(MULTIVALUE_SEPARATOR).join(valuesList);
          }
        }
        break;

      case SEAL:
        if (resolvedObject instanceof SealState) {
          SealState sealState = (SealState) resolvedObject;
          return MessageAccess.getString(sealState.toString());
        }
        break;

      case AVAILABLE_FOR_INTERFACES:
        if (resolvedObject instanceof Boolean) {
          boolean availableForInterfaces = ((Boolean) resolvedObject).booleanValue();
          if (availableForInterfaces) {
            return MessageAccess.getString("global.yes");
          }
          else {
            return MessageAccess.getString("global.no");
          }
        }
        break;

      case DIRECTION:
        if (resolvedObject instanceof String) {
          Direction directionForValue = Direction.getDirectionForValue(String.valueOf(resolvedObject));
          if (directionForValue != null) {
            return SHOW_DIRECTION_ARROWS ? directionForValue.toString() : directionForValue.name();
          }
        }
        return StringUtils.defaultString(String.valueOf(resolvedObject), NOT_FOUND);

      case DATE:
        if (resolvedObject instanceof Date) {
          return getDateFormat().format((Date) resolvedObject);
        }
        break;

      case TYPE_OF_STATUS:
        if (resolvedObject instanceof de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus) {
          de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus statusISR = (de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus) resolvedObject;
          return MessageAccess.getString(statusISR.toString());
        }
        else if (resolvedObject instanceof de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus) {
          de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus statusTCR = (de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus) resolvedObject;
          return MessageAccess.getString(statusTCR.toString());
        }
        break;

      default:
        if (resolvedObject != null) {
          return String.valueOf(resolvedObject);
        }
        break;
    }
    return NOT_FOUND;
  }

  private String resolveAttributeValue(Object resolvedObject) {
    if (resolvedObject instanceof Iterable<?>) {
      List<String> valuesList = Lists.newArrayList();
      for (Object avaObj : (Iterable<?>) resolvedObject) {
        if (avaObj instanceof AttributeValueAssignment) {
          AttributeValueAssignment ava = (AttributeValueAssignment) avaObj;
          try {
            AttributeValue attributeValue = ava.getAttributeValue();
            AttributeType attributeType = attributeValue.getAbstractAttributeType();
            TypeOfAttribute typeOfAttribute = attributeType.getTypeOfAttribute();
            Integer atIdEntity = attributeType.getId();
            Integer atIdCol = Integer.valueOf((columnEntry.getField()));
            if (atIdCol.equals(atIdEntity)) {
              switch (typeOfAttribute) {
                case DATE:
                  if (attributeValue instanceof DateAV) {
                    Object valueObj = ((DateAV) attributeValue).getValue();
                    valuesList.add(getDateFormat().format(valueObj));
                  }
                  else {
                    valuesList.add(String.valueOf(attributeValue.getValue()));
                  }
                  break;
                case RESPONSIBILITY:
                  if (attributeValue instanceof ResponsibilityAV) {
                    valuesList.add(((ResponsibilityAV) attributeValue).getName());
                  }
                  else {
                    valuesList.add(String.valueOf(attributeValue.getValue()));
                  }
                  break;
                default:
                  valuesList.add(String.valueOf(attributeValue.getValue()));
                  break;
              }
            }
          } catch (Exception e) {
            // nothing
          }
        }
      }
      if (valuesList.size() > 0) {
        return Joiner.on(MULTIVALUE_SEPARATOR).join(valuesList);
      }
    }
    return NOT_FOUND;
  }

  private String getBeanPath() {
    if (colType.equals(ColumnEntry.COLUMN_TYPE.ATTRIBUTE)) {
      return "attributeValueAssignments";
    }
    else if (colType.equals(ColumnEntry.COLUMN_TYPE.SEAL)) {
      return "sealState.value";
    }
    else {
      return columnEntry.getField();
    }
  }

  /**{@inheritDoc}**/
  @Override
  public String getColumnHeader() {
    if (colType.equals(ColumnEntry.COLUMN_TYPE.ATTRIBUTE)) {
      return columnEntry.getHead();
    }
    else {
      return MessageAccess.getString(columnEntry.getHead());
    }
  }
}
