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
package de.iteratec.iteraplan.model.user;

import java.io.Serializable;

import javax.persistence.Entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


@Entity
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
public class PermissionAttrTypeGroup implements Comparable<PermissionAttrTypeGroup>, IdentityEntity, Serializable {

  private static final long  serialVersionUID  = 7189856153433913750L;
  public static final String NO_PERMISSION_KEY = "manageRoles.noPermissions";
  public static final String READ_KEY          = "manageRoles.readPermission";
  public static final String READ_WRITE_KEY    = "manageRoles.readWritePermission";

  private Integer            id;

  private Integer            olVersion;

  private Boolean            readPermission    = Boolean.FALSE;

  private Boolean            writePermission   = Boolean.FALSE;

  private Role               role;

  private AttributeTypeGroup attrTypeGroup;

  public PermissionAttrTypeGroup() {
    // nothing to do
  }

  public PermissionAttrTypeGroup(Integer id, AttributeTypeGroup attrTypeGroup) {
    this(id, attrTypeGroup, null);
  }

  public PermissionAttrTypeGroup(Integer id, AttributeTypeGroup attrTypeGroup, Role role) {
    this.id = id;
    this.attrTypeGroup = attrTypeGroup;
    this.role = role;
  }

  public PermissionAttrTypeGroup(Integer id, Role role) {
    this(id, null, role);
  }

  public int compareTo(PermissionAttrTypeGroup other) {
    return this.getAttributeTypeGroupName().compareToIgnoreCase(other.getAttributeTypeGroupName());
  }

  public String getAttributeTypeGroupName() {
    if (this.getAttrTypeGroup() != null) {
      return this.getAttrTypeGroup().getName();
    }
    return "";
  }

  public AttributeTypeGroup getAttrTypeGroup() {
    return attrTypeGroup;
  }

  public Integer getId() {
    return id;
  }

  public String getIdentityString() {
    return getId().toString();
  }

  public Integer getOlVersion() {
    return olVersion;
  }

  public String getPermissionKey() {
    if (this.readPermission.booleanValue() && this.writePermission.booleanValue()) {
      return READ_WRITE_KEY;
    }
    else if ((this.readPermission.booleanValue())) {
      return READ_KEY;
    }
    else {
      return NO_PERMISSION_KEY;
    }
  }

  public Boolean isReadPermission() {
    return readPermission;
  }

  public Role getRole() {
    return role;
  }

  public String getRoleName() {
    if (this.getRole() != null) {
      return this.getRole().getRoleName();
    }
    return "";
  }

  public Boolean isWritePermission() {
    return writePermission;
  }

  /**
   * Provides a hashCode that covers all relevant properties.
   * 
   * @return A unique hash string.
   */
  public String privateHashString() {
    StringBuffer code = new StringBuffer();
    code.append(isReadPermission());
    code.append('\u0001');
    code.append(isWritePermission());
    code.append('\u0001');
    code.append(getRole().getId());
    code.append('\u0001');
    code.append(getAttrTypeGroup().getId());
    return code.toString();
  }

  public void resetPermissions() {
    this.setReadPermission(Boolean.FALSE);
    this.setWritePermission(Boolean.FALSE);
  }

  public void setAttrTypeGroup(AttributeTypeGroup attrTypeGroup) {
    this.attrTypeGroup = attrTypeGroup;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  public void setPermissionKey(String key) {
    if (READ_WRITE_KEY.equals(key)) {
      this.setReadPermission(Boolean.TRUE);
      this.setWritePermission(Boolean.TRUE);
    }
    else if (READ_KEY.equals(key)) {
      this.setReadPermission(Boolean.TRUE);
      this.setWritePermission(Boolean.FALSE);
    }
    else {
      this.setReadPermission(Boolean.FALSE);
      this.setWritePermission(Boolean.FALSE);
    }
  }

  public void setReadPermission(Boolean readPermission) {
    this.readPermission = readPermission;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public void setWritePermission(Boolean writePermission) {
    this.writePermission = writePermission;
    if (writePermission.booleanValue() && (readPermission == null || !readPermission.booleanValue())) {
      setReadPermission(Boolean.TRUE);
    }
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    if (id == null) {
      result = super.hashCode();
    }
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PermissionAttrTypeGroup)) {
      return false;
    }
    final PermissionAttrTypeGroup other = (PermissionAttrTypeGroup) obj;
    if (id == null) {
      if (other.getId() != null) {
        return false;
      }
    }
    else if (!id.equals(other.getId())) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    builder.append("id", getId());
    builder.append("atg", getAttributeTypeGroupName());
    builder.append("role", getRoleName());
    builder.append("readPerm", isReadPermission());
    builder.append("writePerm", isWritePermission());

    return builder.toString();
  }
}
