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
package de.iteratec.iteraplan.businesslogic.exchange.common.masterplan;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;


/**
 * A helper class to create a mapping from a selected set of properties of building blocks to their respective
 * values for a given set of building blocks. Currently used exclusively for the custom columns of the masterplan
 * diagram.
 */
public class MasterplanCustomColumnHelper {

  private final Set<? extends BuildingBlock>                 entities;
  private final List<ColumnEntry>                            columns;
  private final AttributeTypeService                         attributeTypeService;
  private final Locale                                       locale;

  private final Map<ColumnEntry, Map<BuildingBlock, String>> projectionResults = new HashMap<ColumnEntry, Map<BuildingBlock, String>>();

  public MasterplanCustomColumnHelper(Set<? extends BuildingBlock> entities, List<ColumnEntry> columns, AttributeTypeService attributeTypeService,
      Locale locale) {
    this.entities = entities;
    this.columns = columns;
    this.attributeTypeService = attributeTypeService;
    this.locale = locale;
  }

  public Map<ColumnEntry, Map<BuildingBlock, String>> projectColums() {
    for (ColumnEntry column : columns) {
      projectionResults.put(column, new HashMap<BuildingBlock, String>());
      projectColumn(column);
    }

    return projectionResults;
  }

  private void projectColumn(ColumnEntry column) {
    if (column.getEnumType().equals(COLUMN_TYPE.NAME)) {
      projectNameColumn(column);
    }
    else if (column.getEnumType().equals(COLUMN_TYPE.DATE)) {
      projectDateColumn(column);
    }
    else if (column.getEnumType().equals(COLUMN_TYPE.LIST)) {
      projectListColumn(column);
    }
    else if (column.getEnumType().equals(COLUMN_TYPE.ATTRIBUTE)) {
      projectAttributeColumn(column);
    }
    else if (column.getEnumType().equals(COLUMN_TYPE.SEAL)) {
      projectSealColumn(column);
    }
    else if (column.getEnumType().equals(COLUMN_TYPE.AVAILABLE_FOR_INTERFACES)) {
      projectAvailableForInterfacesColumn(column);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  private void projectNameColumn(ColumnEntry column) {
    String methodName = formatFieldToGetter(column.getField());

    for (BuildingBlock bb : entities) {
      Object value = invokeMethodNoArguments(bb, methodName);
      //it is assumed that the result of a name column has a toString implementation
      if (value == null) {
        this.projectionResults.get(column).put(bb, "");
      }
      else {
        this.projectionResults.get(column).put(bb, value.toString());
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void projectListColumn(ColumnEntry column) {
    String methodName = formatFieldToGetter(column.getField());

    for (BuildingBlock bb : entities) {
      Object value = invokeMethodNoArguments(bb, methodName);
      //it is assumed that the result of a name column has a toString implementation
      StringBuilder valueString = new StringBuilder();
      if (value == null) {
        this.projectionResults.get(column).put(bb, valueString.toString());
      }
      else {
        //it is further assumed that the 'list' is a collection
        Collection<BuildingBlock> resultCollection = (Collection<BuildingBlock>) value;
        boolean first = true;
        for (BuildingBlock listBb : resultCollection) {
          if (!first) {
            valueString.append(", ");
          }
          valueString.append(listBb.toString());
          first = false;
        }
        this.projectionResults.get(column).put(bb, valueString.toString());
      }
    }
  }

  private void projectDateColumn(ColumnEntry column) {
    String methodName = formatFieldToGetter(column.getField());

    for (BuildingBlock bb : entities) {
      Object value = invokeMethodNoArguments(bb, methodName);
      //it is assumed that the result of a 'date' column is a java.util.Date instance
      if (value == null) {
        this.projectionResults.get(column).put(bb, "");
      }
      else {
        Date date = (Date) value;
        DateFormat format = DateFormat.getDateInstance(3, locale);
        this.projectionResults.get(column).put(bb, format.format(date));
      }
    }
  }

  private void projectAttributeColumn(ColumnEntry column) {
    AttributeType attributeType = attributeTypeService.loadObjectById(Integer.valueOf(column.getField()));

    for (BuildingBlock bb : entities) {
      List<AttributeValue> values = bb.getAttributeTypeToAttributeValues().get(attributeType);
      StringBuilder stringValues = new StringBuilder();
      if (values != null) {
        boolean initial = true;
        for (AttributeValue val : values) {
          if (!initial) {
            stringValues.append(", ");
          }
          stringValues.append(val.getLocalizedValueString(locale));
          initial = false;
        }
      }
      this.projectionResults.get(column).put(bb, stringValues.toString());
    }
  }

  private void projectSealColumn(ColumnEntry column) {
    for (BuildingBlock bb : entities) {
      if (bb.getTypeOfBuildingBlock() == TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE) {
        InformationSystemRelease isr = (InformationSystemRelease) bb;
        String stateValue = isr.getSealState().getValue();
        this.projectionResults.get(column).put(bb, MessageAccess.getString(stateValue, locale));
      }
    }
  }

  private void projectAvailableForInterfacesColumn(ColumnEntry column) {
    for (BuildingBlock bb : entities) {
      if (bb.getTypeOfBuildingBlock() == TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE) {
        TechnicalComponentRelease tcr = (TechnicalComponentRelease) bb;
        String stateValue = "global." + tcr.isAvailableForInterfaces();
        this.projectionResults.get(column).put(bb, MessageAccess.getString(stateValue, locale));
      }
    }
  }

  /**
   * Transforms the filed name to a getter method. Note that the is-convention for boolean
   * methods is omitted here, since no boolean methods are supposed to be called through
   * this class.
   * @param field   
   *    The name of the field, e.g. 'name'
   * @return
   *    The assumed name of the getter method for this property, e.g. 'getName';
   */
  private static String formatFieldToGetter(String field) {
    return "get" + field.substring(0, 1).toUpperCase() + field.substring(1, field.length());
  }

  /**
   * Invokes a method without arguments, i.e. a getter.
   * @param onInstance
   *    The instance on which the method is to be invoked.
   * @param methodName
   *    The name of the method.
   * @return
   *    The return value of the method.
   */
  private static Object invokeMethodNoArguments(Object onInstance, String methodName) {
    Object result = null;

    try {
      Method method = onInstance.getClass().getMethod(methodName, new Class<?>[] {});
      result = method.invoke(onInstance, new Object[] {});
      
    } catch (NoSuchMethodException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (IllegalArgumentException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (IllegalAccessException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (InvocationTargetException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
    return result;
  }

}
