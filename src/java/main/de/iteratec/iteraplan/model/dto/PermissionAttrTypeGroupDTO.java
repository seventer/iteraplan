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
package de.iteratec.iteraplan.model.dto;

import java.io.Serializable;

import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;


/**
 * This DTO encapsulates the model class {@link PermissionAttrTypeGroup}. It is used to provide
 * unique IDs and reference them through the presentation layer to add / remove permissions for 
 * attribute type groups or roles. 
 * 
 * In earlier versions of iteraplan the IDs were negative IDs of a {@link de.iteratec.iteraplan.model.attribute.AttributeTypeGroup} 
 * or {@link de.iteratec.iteraplan.model.user.Role} to ensure that no ID already in use was chosen. 
 * Because the current Spring configuration provides only for READ-WRITE sessions (that is, sessions 
 * get always flushed) this led to the situation that negative IDs were tried to store in the database.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
public class PermissionAttrTypeGroupDTO implements Comparable<PermissionAttrTypeGroupDTO>, IdEntity, Serializable {

  /** Serialization version. */
  private static final long       serialVersionUID = 7997570579074731458L;

  private Integer                 id;

  private PermissionAttrTypeGroup permission;

  public PermissionAttrTypeGroupDTO() {
    // do nothing
  }

  public PermissionAttrTypeGroupDTO(Integer id, PermissionAttrTypeGroup permission) {
    this.id = id;
    this.permission = permission;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public PermissionAttrTypeGroup getPermission() {
    return permission;
  }

  public void setPermission(PermissionAttrTypeGroup permission) {
    this.permission = permission;
  }

  /**
   * Impose total ordering of the elements of this class according 
   * to the name of the encapsulated {@link de.iteratec.iteraplan.model.attribute.AttributeTypeGroup}.
   */
  public int compareTo(PermissionAttrTypeGroupDTO other) {
    String s1 = this.getPermission().getAttributeTypeGroupName();
    String s2 = other.getPermission().getAttributeTypeGroupName();
    return s1.compareToIgnoreCase(s2);
  }
}
