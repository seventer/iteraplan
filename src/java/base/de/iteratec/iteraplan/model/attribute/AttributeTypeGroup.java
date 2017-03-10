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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.AuditMappedBy;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;


/**
 * <b>Note: this class has a natural ordering that is inconsistent with equals.</b>
 */
@javax.persistence.Entity
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
public class AttributeTypeGroup implements Comparable<AttributeTypeGroup>, Entity, Serializable {

  private static final long            serialVersionUID         = -5607034138216908011L;

  public static final String           STANDARD_ATG_NAME        = "[Default Attribute Group]";

  public static final String           STANDARD_ATG_DESCRIPTION = "In dieser Merkmalsgruppe befinden sich alle Merkmale, die nicht explizit einer anderen Merkmalsgruppe zugeordnet worden sind. Der Name und die Beschreibung dieser Gruppe kann nicht verändert werden.\n\n"
                                                                    + "This Attribute Group contains all Attributes that are not explicitly assigned to another Attribute Group. The name and description of this group cannot be changed.";

  private Integer                      id;

  private Integer                      olVersion;

  private String                       name;

  private String                       description;

  private Boolean                      toplevelATG;

  /**
   * The position that is persisted. The absolute position must be calculated by evaluating the
   * positions of all other AttributeTypeGroups.
   * 
   * @see #compareTo(AttributeTypeGroup)
   */
  private Integer                      position;

  /** List of attribute types attached to this group. Never null. */
  private List<AttributeType>          attributeTypes           = new ArrayList<AttributeType>();

  private Set<PermissionAttrTypeGroup> permissionsRole          = new HashSet<PermissionAttrTypeGroup>();

  private String                       lastModificationUser;

  private Date                         lastModificationTime;

  /**
   * Constructor.
   */
  public AttributeTypeGroup() {
    super();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getOlVersion() {
    return olVersion;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = StringUtils.trim(name);
  }

  public String getDescription() {
    if (STANDARD_ATG_NAME.equals(this.name) && UserContext.getCurrentLocale() != null) {
      Locale locale = UserContext.getCurrentLocale();
      String i18nDescription = MessageAccess.getStringOrNull("atg.standardAtg.description", locale);
      return i18nDescription != null ? i18nDescription : this.description;
    }
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean isToplevelATG() {
    return toplevelATG;
  }

  public void setToplevelATG(Boolean isToplevelATG) {
    this.toplevelATG = isToplevelATG;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  @AuditMappedBy(mappedBy = "attributeTypeGroup", positionMappedBy = "position")
  public List<AttributeType> getAttributeTypes() {
    return attributeTypes;
  }

  public void setAttributeTypes(List<AttributeType> attributeTypes) {
    if (attributeTypes == null) {
      throw new IllegalArgumentException("Attribute types may not be null");
    }
    this.attributeTypes = attributeTypes;
  }

  /**
   * Adds the given attribute type to this attribute type group, inserting to the list at the given
   * index. The attribute type remains unmodified.
   * 
   * @param at
   *          The attribute type to add.
   * @param index
   *          The list index, where the attribute type shall be inserted. If below zero, it will be
   *          appended to the end of the list.
   */
  public void addAttributeType(AttributeType at, int index) {
    List<AttributeType> attrTypeList = this.getAttributeTypes();
    attrTypeList.remove(at);

    if (index < 0 || index >= attrTypeList.size()) {
      attrTypeList.add(at);
    }
    else {
      attrTypeList.add(index, at);
    }
  }

  /**
   * Adds the given attribute type to this attribute type group, inserting to the list at the given
   * index. References are created on both ends.
   * 
   * @param at
   *          The attribute type to add.
   * @param index
   *          The list index, where the attribute type shall be inserted. If below zero, it will be
   *          appended to the end of the list.
   */
  public void addAttributeTypeTwoWay(AttributeType at, int index) {
    if (index < 0 || index >= this.getAttributeTypes().size()) {
      addAttributeTypeTwoWay(at);
    }
    else {
      this.getAttributeTypes().add(index, at);
      at.setAttributeTypeGroup(this);
    }
  }

  /**
   * Adds the given attribute type to this attribute type group, appending it to the end of the
   * list. References are created on both ends.
   * 
   * @param at
   *          The attribute type to add.
   */
  public void addAttributeTypeTwoWay(AttributeType at) {
    this.getAttributeTypes().add(at);
    at.setAttributeTypeGroup(this);
  }

  /**
   * Removes the given attribute type from this AT group. Additionally, clears the back-reference to
   * this AT group in the attribute type object.
   * 
   * @param at
   *          The attribute type to be removed from this AT group.
   * @return The position of the attribute type in this group before deletion. It's a 0-based index.
   *         If it was not part of this AT group yet, -1 is returned.
   */
  public int removeAttributeType(AttributeType at) {
    int index = -1;
    if (getAttributeTypes() != null) {
      index = this.getAttributeTypes().indexOf(at);
      this.getAttributeTypes().remove(at);
    }
    return index;
  }

  public int compareTo(AttributeTypeGroup other) {
    return this.getPosition().compareTo(other.getPosition());
  }

  public Set<PermissionAttrTypeGroup> getPermissionsRole() {
    return permissionsRole;
  }

  public void setPermissionsRole(Set<PermissionAttrTypeGroup> permissionsRole) {
    this.permissionsRole = permissionsRole;
  }

  public void addPermissionTwoWay(PermissionAttrTypeGroup patg) {
    if (permissionsRole == null) {
      permissionsRole = new HashSet<PermissionAttrTypeGroup>();
    }
    permissionsRole.add(patg);
    patg.getRole().addPermissionsAttrTypeGroupTwoWay(patg);
    patg.setAttrTypeGroup(this);
  }

  public void addPermissions(Collection<PermissionAttrTypeGroup> patg) {
    for (PermissionAttrTypeGroup permissionAttrTypeGroup : patg) {
      addPermissionTwoWay(permissionAttrTypeGroup);
    }
  }

  public void removePermissions() {
    Set<PermissionAttrTypeGroup> permissions = new HashSet<PermissionAttrTypeGroup>(permissionsRole);
    for (PermissionAttrTypeGroup permissionAttrTypeGroup : permissions) {
      permissionAttrTypeGroup.getRole().removePermissionsAttrTypeGroup(permissionAttrTypeGroup);
    }
    permissionsRole.clear();
  }

  /**
   * @return A set of Roles which have a read permission for this attribute type group. The set only
   *         includes roles that are directly assigned to this group. It does <b>not</b> contain
   *         roles, which are a higher up the role hierarchy and aggregate these permissions.
   *         Therefore the returned roles do not represent the complete set of roles which have a
   *         read permission for this attribute type group.
   */
  public Set<Integer> getRoleIdsWithReadPermissionNotAggregated() {
    Set<Integer> result = new HashSet<Integer>();
    if (getPermissionsRole() == null) {
      return result;
    }
    for (Iterator<PermissionAttrTypeGroup> it = getPermissionsRole().iterator(); it.hasNext();) {
      PermissionAttrTypeGroup patg = it.next();
      if (patg != null && patg.getRole() != null && patg.isReadPermission().booleanValue()) {
        result.add(patg.getRole().getId());
      }
    }
    return result;
  }

  /**
   * @return A set of Roles which have a write permission for this attribute type group. The set
   *         only includes roles that are directly assigned to this group. It does <b>not</b>
   *         contain roles, which are a higher up the role hierarchy and aggregate these
   *         permissions. Therefore the returned roles do not represent the complete set of roles
   *         which have a write permission for this attribute type group.
   */
  public Set<Integer> getRoleIdsWithWritePermissionNotAggregated() {
    Set<Integer> result = new HashSet<Integer>();
    if (getPermissionsRole() == null) {
      return result;
    }
    for (Iterator<PermissionAttrTypeGroup> it = getPermissionsRole().iterator(); it.hasNext();) {
      PermissionAttrTypeGroup patg = it.next();
      if (patg.isWritePermission().booleanValue() && patg.getRole() != null) {
        result.add(patg.getRole().getId());
      }
    }
    return result;
  }

  public String getLastModificationUser() {
    return this.lastModificationUser;
  }

  public void setLastModificationUser(String user) {
    this.lastModificationUser = user;
  }

  public Date getLastModificationTime() {
    return lastModificationTime;
  }

  public void setLastModificationTime(Date lastModificationTime) {
    this.lastModificationTime = lastModificationTime;
  }

  public String getIdentityString() {
    return getName();
  }

  /**
   * Returns a canonical name of this AttributeTypeGroup, suitable for use as part of a HTML ID.
   * 
   * @return The name as a String, free of non-word characters.
   */
  public String getNameForHtmlId() {
    return this.getName().replaceAll("\\W", "");
  }

  /**
   * Validates this object for business and technical constraints.
   * 
   * @throws IteraplanTechnicalException
   */
  public void validate() throws IteraplanTechnicalException {
    for (Iterator<PermissionAttrTypeGroup> it = getPermissionsRole().iterator(); it.hasNext();) {
      PermissionAttrTypeGroup patg = it.next();
      if (!patg.isReadPermission().booleanValue() && patg.isWritePermission().booleanValue()) {
        // PermissionAttributetypegroup must be either read or read-write.
        // Just write is not allowed.
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
    }

  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + ((attributeTypes == null) ? 0 : attributeTypes.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((toplevelATG == null) ? 0 : toplevelATG.hashCode());
    result = prime * result + ((olVersion == null) ? 0 : olVersion.hashCode());
    result = prime * result + ((permissionsRole == null) ? 0 : permissionsRole.hashCode());
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
    if (getClass() != obj.getClass()) {
      return false;
    }
    AttributeTypeGroup other = (AttributeTypeGroup) obj;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(attributeTypes, other.attributeTypes);
    builder.append(description, other.description);
    builder.append(id, other.id);
    builder.append(toplevelATG, other.toplevelATG);
    builder.append(name, other.name);
    builder.append(olVersion, other.olVersion);
    builder.append(permissionsRole, other.permissionsRole);
    return builder.isEquals();
  }

}
