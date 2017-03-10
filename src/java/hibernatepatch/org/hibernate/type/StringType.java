/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA

 */
package org.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;


/**
 * A type that maps between {@link java.sql.Types#VARCHAR VARCHAR} and {@link String}
 * 
 * @author Gavin King
 * @author Steve Ebersole
 */

/**
 * Patched version of StringType. <br>
 * <br>
 * Empty Strings are replaced by EMPTY_STRING_REPLACEMENT for use in oracle databases. This
 * encoding/decoding is necessary to work around the fact that oracle stores an empty String as null
 * when using varchar(2). <br>
 * <br>
 * Make sure that this version of StringType is first in your classpath!
 */
public class StringType extends AbstractSingleColumnStandardBasicType<String> implements
DiscriminatorType<String> {

  public static final StringType INSTANCE                 = new StringType();

  /** [Patch] The replacement for empty strings in the database. */
  private static final String    EMPTY_STRING_REPLACEMENT = "\u00AB nU1l \u00BB";

  public StringType() {
    super(VarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE);
  }

  public String getName() {
    return "string";
  }

  @Override
  protected boolean registerUnderJavaType() {
    return true;
  }

  public String objectToSQLString(String value, Dialect dialect) throws Exception { // NOPMD original code, ignore
    return '\'' + encodeEmptyString(value) + '\'';
  }

  public String stringToObject(String xml) throws Exception { // NOPMD original code, ignore
    return xml;
  }

  @Override
  public String toString(String value) {

    return value;
  }

  // Everything below here is part of the Patch

  public static void isPatched() {
    // If this method exists, iteraplan will not show a warning
  }

  @Override
  public Object get(ResultSet rs, String name) throws HibernateException, SQLException {
    return decodeEmptyString(nullSafeGet(rs, name));
  }

  @Override
  protected String nullSafeGet(ResultSet rs, String name, WrapperOptions options) throws SQLException {
    return decodeEmptyString(sqlTypeDescriptor.getExtractor( javaTypeDescriptor ).extract( rs, name, options ));
  }

  @Override
  protected void nullSafeSet(PreparedStatement st, Object value, int index, WrapperOptions options) throws SQLException {
    sqlTypeDescriptor.getBinder( javaTypeDescriptor ).bind( st, encodeEmptyString((String) value), index, options );
  }

  @Override
  public void set(PreparedStatement st, String value, int index) throws HibernateException, SQLException {
    nullSafeSet(st, encodeEmptyString(value), index);
  }

  private static final String decodeEmptyString(String s) {
    if (EMPTY_STRING_REPLACEMENT.equals(s)) {
      return "";
    }
    return s;
  }

  private static final String encodeEmptyString(String s) {
    if ("".equals(s)) {
      return EMPTY_STRING_REPLACEMENT;
    }
    return s;
  }

}
