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
package de.iteratec.iteraplan.common.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;


/**
 * A multipurpose class consisting of an id, a name, a description 
 * and a map that can contain further data.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
public class NamedId implements Comparable<NamedId>, Serializable {

  private static final long   serialVersionUID = 4369957915330906692L;

  private Integer             id;

  private String              name;

  private String              description;

  private Map<Object, Object> misc             = null;

  /**
   * Default constructor
   */
  public NamedId() {
    // nothing to do
  }

  /**
   * Short constructor. Leaves the map null.
   * 
   * @param id
   * @param name
   * @param description
   */
  public NamedId(Integer id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  /**
   * Full constructor
   * 
   * @param id
   * @param name
   * @param description
   * @param map
   */
  public NamedId(Integer id, String name, String description, Map<Object, Object> map) {
    this(id, name, description);
    this.misc = map;
  }

  /**
   * @return Returns the id.
   */
  public Integer getId() {
    return id;
  }

  /**
   * @param id The id to set.
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name to set.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return Returns the description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description The description to set.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public Map<Object, Object> getMisc() {
    return misc;
  }

  public void setMisc(Map<Object, Object> misc) {
    this.misc = misc;
  }

  public int compareTo(NamedId other) {
    return this.getName().compareToIgnoreCase(other.getName());
  }

  public String toString() {
    StringBuffer sb = new StringBuffer(30);
    sb.append("Id: ");
    sb.append(this.id);
    sb.append(", Name: ");
    sb.append(this.name);
    sb.append(", Description: ");
    sb.append(this.description);
    if (this.misc != null) {
      for (Iterator<Map.Entry<Object, Object>> it = this.misc.entrySet().iterator(); it.hasNext();) {
        Map.Entry<Object, Object> entry = it.next();
        sb.append(", ");
        sb.append(entry.getKey());
        sb.append(": ");
        sb.append(entry.getValue());
      }
    }
    return sb.toString();
  }
}
