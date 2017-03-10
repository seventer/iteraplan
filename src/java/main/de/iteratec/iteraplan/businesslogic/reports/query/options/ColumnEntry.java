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
package de.iteratec.iteraplan.businesslogic.reports.query.options;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * ColumnEntry contains all attributes to describe a single column. This class is used in the
 * ViewConfiguration class. Equals and HashCode are needed, because the view class operates with
 * collection search functions. Keep in mind to update these functions when updating this class.
 * Since the definition won't change, this class is immutable.
 */
public class ColumnEntry implements Serializable {
  /** Serialization version. */
  private static final long serialVersionUID   = 7272189827995104619L;
  private final String      field;
  private final String      head;
  private final COLUMN_TYPE type;
  private boolean           technicalAttribute = false;

  public ColumnEntry(final String field, final COLUMN_TYPE type, final String head) {
    this.field = field;
    this.type = type;
    this.head = head;
  }

  public ColumnEntry(final String field, final COLUMN_TYPE type, final String head, boolean technicalAttribute) {
    this(field, type, head);
    this.technicalAttribute = technicalAttribute;
  }

  public ColumnEntry(final ColumnEntry entry) {
    this.field = entry.field;
    this.type = entry.type;
    this.head = entry.head;
    this.technicalAttribute = entry.technicalAttribute;
  }

  public String getField() {
    return field;
  }

  public String getType() {
    return type.toString();
  }

  public COLUMN_TYPE getEnumType() {
    return type;
  }

  public String getHead() {
    return head;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ColumnEntry)) {
      return false;
    }
    ColumnEntry entry = (ColumnEntry) obj;
    boolean result = entry.getField().equals(field) && entry.getEnumType().equals(type);
    if (!COLUMN_TYPE.ATTRIBUTE.equals(entry.getEnumType())) {
      result = result && entry.getHead().equals(head);
    }
    return result;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + field.hashCode();
    result = 31 * result + type.ordinal();
    if (!COLUMN_TYPE.ATTRIBUTE.equals(type)) {
      result = 31 * result + head.hashCode();
    }
    return result;
  }

  @Override
  public String toString() {
    return "ColumnEntry " + head + ": Field " + field + ", Type: " + type;
  }

  /**
   * This enumeration is used in the frontend to distinguish different views for different value
   * types.
   */
  public enum COLUMN_TYPE {

    DATE("date"), DESCRIPTION("description"), INHERITED("name_inherited"), TYPE_OF_STATUS("typeofstatus"), NAME("name"), CONNECTION("connection"), LIST(
        "list"), ATTRIBUTE("attribute"), DIRECTION("direction"), SEAL("seal"), AVAILABLE_FOR_INTERFACES("availableForInterfaces");

    private String                                value;

    private static final Map<String, COLUMN_TYPE> LOOKUP = new HashMap<String, COLUMN_TYPE>();

    static {
      for (COLUMN_TYPE s : EnumSet.allOf(COLUMN_TYPE.class)) {
        LOOKUP.put(s.toString(), s);
      }
    }

    private COLUMN_TYPE(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }

    public static COLUMN_TYPE get(String value) {
      return LOOKUP.get(value);
    }
  }

  /**
   * @return technicalAttribute the technicalAttribute
   */
  public boolean isTechnicalAttribute() {
    return technicalAttribute;
  }

  public void setTechnicalAttribute(boolean technicalAttribute) {
    this.technicalAttribute = technicalAttribute;
  }

}
