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
package de.iteratec.iteraplan.persistence.util;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;


/**
 * Parameterized type for mapping a Java 5 Enum with string values.
 * <p>
 * This allows you to avoid the need to define a concrete UserType instance for every enum 
 * that you have. Contrary to <a>http://www.hibernate.org/272.html</a> this class allows for 
 * the value in the database to be different from the Enum constant name.
 * <p>
 * This implementation requires the Enum class to overwrite the methode <tt>public String 
 * toString()</tt>, which should return the string value stored in the Enum instance. 
 * Failing to do so, results in unpredictable behaviour. 
 * <p>
 * For safety reasons and in order to enforce this convention, the Enum class could implement 
 * an interface exposing some method for the same purpose. This would bind the implementation
 * of the Enum classes to a specific interface though (see referred website for details).
 * <p>
 * Usage:
 * <ul>
 *   <li>Example enum</li>
 *   <pre>
 *     public enum GenderEnum {
 *       
 *       MALE("M"),
 *       FEMALE("F");
 *
 *       private final String state;
 *          
 *       GenderEnum(final String state) { this.state = state; }
 *
 *       public String toString() { return this.state; }
 *       
 *     }
 *   </pre>
 *   <li>Create a new typedef for each enum, giving it a unique type name.</li>
 *   <pre>
 *     &lt;typedef name="gender" class='StringEnumUserType'&gt;
 *       &lt;param name="enum"&gt;com.example.Gender&lt;/param&gt;
 *     &lt;/typedef&gt;
 *   </pre>
 *   <li>Reference this type name in the property tag.</li>
 *   <pre>
 *     &lt;class ...&gt;
 *       &lt;property name='gender' type='gender'/&gt;
 *     &lt;/class&gt;
 *   </pre>
 * </ul>
 * 
 * @author refer to <a>http://www.hibernate.org/273.html</a>
 *  
 * @param <T>
 */
public class StringEnumUserType<T extends Enum<T>> implements EnhancedUserType, ParameterizedType {

  /**
   * Enum class for this particular user type.
   */
  private Class<T> enumClass;

  /**
   * Value to use if null.
   */
  private String   defaultValue;

  /** 
   * Creates a new instance of StringEnumUserType 
   */
  public StringEnumUserType() {
    // default constructor
  }

  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return cached;
  }

  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @SuppressWarnings("unchecked")
  public Serializable disassemble(Object value) throws HibernateException {
    return (Enum<T>) value;
  }

  public boolean equals(Object x, Object y) throws HibernateException {
    return x == y;//NOPMD since this explicit equal is intended
  }

  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  public boolean isMutable() {
    return false;
  }

  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
    String value = rs.getString(names[0]);
    if (value == null) {
      value = getDefaultValue();
      //no value provided
      if (value == null) {
        return null;
      }
    }
    String name = StringEnumReflectionHelper.getNameFromValue(enumClass, value);
    Object res = rs.wasNull() ? null : Enum.valueOf(enumClass, name);

    return res;
  }

  @SuppressWarnings("unchecked")
  public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, Types.VARCHAR);
    }
    else {
      st.setString(index, (value instanceof String ? value.toString() : ((T) value).toString()));
    }
  }

  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }

  public Class<T> returnedClass() {
    return enumClass;
  }

  @SuppressWarnings("unchecked")
  public void setParameterValues(Properties parameters) {
    String enumClassName = parameters.getProperty("enum");
    try {
      //Validates the class but does not eliminate the cast
      enumClass = (Class<T>) Class.forName(enumClassName).asSubclass(Enum.class);
    } catch (ClassNotFoundException ex) {
      throw new HibernateException("Enum class " + enumClassName + " not found", ex);
    }

    setDefaultValue(parameters.getProperty("default"));
  }

  public int[] sqlTypes() {
    return new int[] { Types.VARCHAR };
  }

  public Object fromXMLString(String xmlValue) {
    String name = StringEnumReflectionHelper.getNameFromValue(enumClass, xmlValue);
    return Enum.valueOf(enumClass, name);
  }

  @SuppressWarnings("unchecked")
  public String objectToSQLString(Object value) {
    return '\'' + ((T) value).toString() + '\'';
  }

  @SuppressWarnings("unchecked")
  public String toXMLString(Object value) {
    return ((T) value).toString();
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
}
