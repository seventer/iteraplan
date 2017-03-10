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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.envers.Audited;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;


/**
 * Represents an attribute of a Building Block. This can either be a fixed attribute (like name), or
 * a user defined, dynamic attribute.
 */
@Entity
@Audited
public class BBAttribute implements Serializable {

  private static final long                         serialVersionUID                      = -1208932840497178916L;
  public static final String                        BLANK_ATTRIBUTE_TYPE                  = "blank";
  public static final String                        FIXED_ATTRIBUTE_TYPE                  = "fixed";
  public static final String                        FIXED_ATTRIBUTE_DATETYPE              = "fixedDate";
  public static final String                        FIXED_ATTRIBUTE_SET                   = "fixedSet";
  public static final String                        FIXED_ATTRIBUTE_ENUM                  = "fixedEnum";
  public static final String                        USERDEF_ENUM_ATTRIBUTE_TYPE           = "userdefEnum";
  public static final String                        USERDEF_NUMBER_ATTRIBUTE_TYPE         = "userdefNumber";
  public static final String                        USERDEF_DATE_ATTRIBUTE_TYPE           = "userdefDate";
  public static final String                        USERDEF_TEXT_ATTRIBUTE_TYPE           = "userdefText";
  public static final String                        USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE = "userdefResponsibility";

  public static final Integer                       UNDEFINED_ID_VALUE                    = Integer.valueOf(-1);

  /** database id (only for userdef attributes, otherwise it's -1) */
  private Integer                                   attributeTypeId;
  /** the name. this is either the userdef attribute name or, if it is a fixed attribute, the key to localise the name. */
  private final String                              name;
  /** either blank, fixed or userdef (enum, number, text) */
  private final String                              type;
  /** the name of the property (only for fixed attributes) */
  private final String                              dbName;
  /** indicates if this is a multivalue attribute (only for userdef attributes) */
  private boolean                                   multiValue;
  /** indicates if this is a timeseries attribute (only for userdef attributes) */
  private boolean                                   timeseries;
  /** does the currently logged in user have right permissions fir the attribute type */
  private boolean                                   hasWritePermissions;

  /** for enum type attributes: the enumeration class */
  private Class<?>                                  enumClass;

  /** map between id and name of the attribute. It's used later to get the name of attribute by id */
  private static Map<Integer, String>               attributeMap                          = new HashMap<Integer, String>();

  /** maps the db-name of fixed attributes to an Integer-ID, used in constructor */
  private static final Map<String, Integer>         DB_NAME_TO_ID                         = CollectionUtils.hashMap();
  static {
    DB_NAME_TO_ID.put("name", Integer.valueOf(-2));
    DB_NAME_TO_ID.put("informationSystem.name", Integer.valueOf(-3));
    DB_NAME_TO_ID.put("technicalComponent.name", Integer.valueOf(-3));
    DB_NAME_TO_ID.put("description", Integer.valueOf(-4));
    DB_NAME_TO_ID.put("lastModificationUser", Integer.valueOf(-5));
    DB_NAME_TO_ID.put("lastModificationTime", Integer.valueOf(-6));
    DB_NAME_TO_ID.put("version", Integer.valueOf(-7));
    DB_NAME_TO_ID.put("interfaceDirection", Integer.valueOf(-9));
    DB_NAME_TO_ID.put("subscribedUsers", Integer.valueOf(-10));
    DB_NAME_TO_ID.put("seal", Integer.valueOf(-11));
    DB_NAME_TO_ID.put("technicalComponent.availableForInterfaces", Integer.valueOf(-12));
  }

  /** contains the String-IDs of all user-defined attribute types and blank attribute type */
  private static final Set<String>                  USERDEF_ATTRIBUTE_TYPE_STRING_IDS     = CollectionUtils.hashSet();
  static {
    USERDEF_ATTRIBUTE_TYPE_STRING_IDS.add(BLANK_ATTRIBUTE_TYPE);
    USERDEF_ATTRIBUTE_TYPE_STRING_IDS.add(USERDEF_DATE_ATTRIBUTE_TYPE);
    USERDEF_ATTRIBUTE_TYPE_STRING_IDS.add(USERDEF_ENUM_ATTRIBUTE_TYPE);
    USERDEF_ATTRIBUTE_TYPE_STRING_IDS.add(USERDEF_NUMBER_ATTRIBUTE_TYPE);
    USERDEF_ATTRIBUTE_TYPE_STRING_IDS.add(USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE);
    USERDEF_ATTRIBUTE_TYPE_STRING_IDS.add(USERDEF_TEXT_ATTRIBUTE_TYPE);
  }

  /** contains the String-IDs of all fixed attribute types */
  private static final Set<String>                  FIXED_ATTRIBUTE_TYPE_STRING_IDS       = CollectionUtils.hashSet();
  static {
    FIXED_ATTRIBUTE_TYPE_STRING_IDS.add(FIXED_ATTRIBUTE_DATETYPE);
    FIXED_ATTRIBUTE_TYPE_STRING_IDS.add(FIXED_ATTRIBUTE_SET);
    FIXED_ATTRIBUTE_TYPE_STRING_IDS.add(FIXED_ATTRIBUTE_TYPE);
    FIXED_ATTRIBUTE_TYPE_STRING_IDS.add(FIXED_ATTRIBUTE_ENUM);
  }

  /** maps the String-ID of user-defined attribute types to the corresponding TypeOfAttribute */
  private static final Map<String, TypeOfAttribute> STRING_ID_TO_TYPE_OF_ATTRIBUTE        = CollectionUtils.hashMap();
  static {
    STRING_ID_TO_TYPE_OF_ATTRIBUTE.put(USERDEF_DATE_ATTRIBUTE_TYPE, TypeOfAttribute.DATE);
    STRING_ID_TO_TYPE_OF_ATTRIBUTE.put(USERDEF_ENUM_ATTRIBUTE_TYPE, TypeOfAttribute.ENUM);
    STRING_ID_TO_TYPE_OF_ATTRIBUTE.put(USERDEF_NUMBER_ATTRIBUTE_TYPE, TypeOfAttribute.NUMBER);
    STRING_ID_TO_TYPE_OF_ATTRIBUTE.put(USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE, TypeOfAttribute.RESPONSIBILITY);
    STRING_ID_TO_TYPE_OF_ATTRIBUTE.put(USERDEF_TEXT_ATTRIBUTE_TYPE, TypeOfAttribute.TEXT);
  }

  public BBAttribute(Integer attributeTypeId, String type, String name, String dbName) {
    if ((attributeTypeId == null) || (attributeTypeId.intValue() < 0)) {
      // Set id for any fixed attributes, e.g name, description
      if ((type != null) && type.startsWith("fixed")) {
        if (DB_NAME_TO_ID.containsKey(dbName)) {
          this.attributeTypeId = DB_NAME_TO_ID.get(dbName);
        }
      }
      else {
        this.attributeTypeId = UNDEFINED_ID_VALUE;
      }
    }
    else {
      this.attributeTypeId = attributeTypeId;
    }
    this.type = type;
    this.name = name;
    this.dbName = dbName;
    attributeMap.put(this.attributeTypeId, name);
  }

  public String getStringId() {
    if ((type == null) || (name == null)) {
      return "";
    }

    StringBuffer stringId = new StringBuffer();
    stringId.append(type);
    stringId.append('_');
    stringId.append(dbName);
    stringId.append('_');
    stringId.append(attributeTypeId);

    return stringId.toString();
  }

  /**
   * Appends the Strings 'type' + 'dbName' + 'Attribute name'. Used when serializing an attribute
   * within a query configuration. Integer id's might change, hence the string representation of the
   * attribute is used as identifier.
   * 
   * @return The identification String that is based on the name of the attribute
   */
  public String getStringIdName() {
    if ((type == null) || (name == null)) {
      return "";
    }

    StringBuffer stringId = new StringBuffer();
    stringId.append(type);
    stringId.append('_');
    stringId.append(dbName);
    stringId.append('_');
    stringId.append(name);

    return stringId.toString();
  }

  public static String getDbNameByStringId(String stringId) {
    if ((stringId == null) || stringId.equals("")) {
      return null;
    }
    int index1 = stringId.indexOf('_');
    int index2 = stringId.lastIndexOf('_');
    String res = null;
    try {
      res = stringId.substring(index1 + 1, index2);
    } catch (IndexOutOfBoundsException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }
    return res;
  }

  public static String getTypeByStringId(String stringId) {
    if (StringUtils.isEmpty(stringId)) {
      return null;
    }

    int index = stringId.indexOf('_');
    try {
      return stringId.substring(0, index);
    } catch (IndexOutOfBoundsException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }
  }

  public static Integer getIdByStringId(String stringId) {
    if ((stringId == null) || stringId.equals("")) {
      return null;
    }
    int index = stringId.lastIndexOf('_');
    Integer res = null;
    try {
      String resString = stringId.substring(index + 1, stringId.length());
      res = Integer.valueOf(Integer.parseInt(resString));
    } catch (IndexOutOfBoundsException ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }
    return res;
  }

  /**
   * Returns the attributed name of an id that was generated by {@link #getStringIdName()}
   * 
   * @param stringIdName
   *          The string id including the name of the attribute
   * @return The name of the attribute
   */
  public static String getNameByStringIdName(String stringIdName) {
    if ((stringIdName == null) || stringIdName.equals("")) {
      return null;
    }
    String res = null;
    try {
      // first underline
      int index = stringIdName.indexOf('_');
      // second underline
      String tmp = stringIdName.substring(index + 1);
      index = tmp.indexOf('_');
      if (index < 0) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
      res = tmp.substring(index + 1);
    } catch (IndexOutOfBoundsException ex) {

      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, ex);
    }
    return res;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public Integer getId() {
    return attributeTypeId;
  }

  public String getDbName() {
    return dbName;
  }

  public boolean isMultiValue() {
    return multiValue;
  }

  public void setIsMultiValue(boolean multiValue) {
    this.multiValue = multiValue;
  }

  public boolean isTimeseries() {
    return timeseries;
  }

  public void setIsTimeseries(boolean timeseries) {
    this.timeseries = timeseries;
  }

  public boolean isHasWritePermissions() {
    return hasWritePermissions;
  }

  public void setHasWritePermissions(boolean hasWritePermissions) {
    this.hasWritePermissions = hasWritePermissions;
  }

  public Class<?> getEnumClass() {
    return enumClass;
  }

  public void setEnumClass(Class<?> enumClass) {
    this.enumClass = enumClass;
  }

  public static boolean isStringIdNameValid(String stringIdName, boolean includeFixed) {
    String localType = getTypeByStringId(stringIdName);
    if (StringUtils.isEmpty(localType)
        || !(USERDEF_ATTRIBUTE_TYPE_STRING_IDS.contains(localType) || (includeFixed && FIXED_ATTRIBUTE_TYPE_STRING_IDS.contains(localType)))) {
      return false;
    }
    return true;
  }

  public static boolean isTypeIn(String attributeName, String... types) {
    if (StringUtils.isEmpty(attributeName) || (types == null)) {
      return false;
    }
    String attrType = getTypeByStringId(attributeName);
    boolean ret = false;
    for (String type : types) {
      if (!isTypeValid(type)) {
        return false;
      }
      if (attrType.equals(type)) {
        ret = true;
      }
    }
    return ret;
  }

  private static boolean isTypeValid(String localType) {
    if (!StringUtils.isEmpty(localType)
        && (USERDEF_ATTRIBUTE_TYPE_STRING_IDS.contains(localType) || (FIXED_ATTRIBUTE_TYPE_STRING_IDS.contains(localType) && !FIXED_ATTRIBUTE_SET
            .equals(localType)))) {
      return true;
    }
    return false;
  }

  public static String getAttributeNameById(Integer id) {
    return attributeMap.get(id);
  }

  public static TypeOfAttribute getTypeOfAttribute(String type) {
    if (STRING_ID_TO_TYPE_OF_ATTRIBUTE.containsKey(type)) {
      return STRING_ID_TO_TYPE_OF_ATTRIBUTE.get(type);
    }
    else {
      return null;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof BBAttribute)) {
      return false;
    }

    final BBAttribute other = (BBAttribute) obj;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(attributeTypeId, other.attributeTypeId);
    builder.append(type, other.type);
    builder.append(name, other.name);
    builder.append(dbName, other.dbName);

    return builder.isEquals();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();
    builder.append(attributeTypeId);
    builder.append(type);
    builder.append(name);
    builder.append(dbName);

    return builder.toHashCode();
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    builder.append("name", name);
    builder.append("type", type);

    return builder.toString();
  }
}
