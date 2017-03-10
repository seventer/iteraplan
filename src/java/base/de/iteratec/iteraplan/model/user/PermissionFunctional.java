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
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


/**
 * This class implements a functional permission in iteraplan. It is defined by a type and contains
 * a set of roles which have been granted this functional permission.
 */
@Entity
public class PermissionFunctional implements Comparable<PermissionFunctional>, IdentityEntity, Serializable {

  private static final long                                                 serialVersionUID = 8427565566169134454L;

  private Integer                                                           id;

  // This class needs no optimistic locking because instances won't be updated.

  private TypeOfFunctionalPermission                                        typeOfFunctionalPermission;

  public static final ImmutableMap<TypeOfFunctionalPermission, Set<Dialog>> MAP_PERMISSION_TO_DIALOG;

  static {
    Map<TypeOfFunctionalPermission, Set<Dialog>> mapping = Maps.newHashMap();

    mapping.put(TypeOfFunctionalPermission.TABULAR_REPORTING, Sets.newHashSet(Dialog.TABULAR_REPORTING));
    mapping.put(TypeOfFunctionalPermission.GRAPHICAL_REPORTING,
        Sets.newHashSet(Dialog.GRAPHICAL_REPORTING, Dialog.CUSTOM_DASHBOARD_INSTANCES_OVERVIEW, Dialog.SAVED_QUERIES));
    mapping.put(TypeOfFunctionalPermission.SUCCESSORREPORT, Sets.newHashSet(Dialog.SUCCESSOR_REPORTS));
    mapping.put(TypeOfFunctionalPermission.CONSISTENCY_CHECK, Sets.newHashSet(Dialog.CONSISTENCY_CHECK));
    mapping.put(TypeOfFunctionalPermission.SEARCH, Sets.newHashSet(Dialog.SEARCH));
    mapping.put(TypeOfFunctionalPermission.MASSUPDATE, Sets.newHashSet(Dialog.MASS_UPDATE));
    mapping.put(TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION, Sets.newHashSet(Dialog.OBJECT_RELATED_PERMISSION));
    mapping.put(TypeOfFunctionalPermission.SUPPORTING_QUERY, Sets.newHashSet(Dialog.SUPPORTING_QUERY));
    mapping.put(TypeOfFunctionalPermission.DOWNLOAD_AUDIT_LOG, Sets.newHashSet(Dialog.MISCELLANEOUS));
    mapping.put(TypeOfFunctionalPermission.CONFIGURATION, Sets.newHashSet(Dialog.CONFIGURATION));
    mapping.put(TypeOfFunctionalPermission.XMIDESERIALIZATION, Sets.newHashSet(Dialog.XMIDESERIALIZATION));
    mapping.put(TypeOfFunctionalPermission.XMISERIALIZATION, Sets.newHashSet(Dialog.XMISERIALIZATION));
    mapping.put(TypeOfFunctionalPermission.EXCELIMPORT, Sets.newHashSet(Dialog.EXCELIMPORT, Dialog.IMPORT));
    mapping.put(TypeOfFunctionalPermission.TEMPLATES, Sets.newHashSet(Dialog.TEMPLATES));
    mapping.put(TypeOfFunctionalPermission.SUBSCRIPTION, Sets.newHashSet(Dialog.SUBSCRIPTION));
    mapping.put(TypeOfFunctionalPermission.DASHBOARD, Sets.newHashSet(Dialog.DASHBOARD));
    mapping.put(TypeOfFunctionalPermission.ITERAQL, Sets.newHashSet(Dialog.ITERAQL));
    mapping.put(TypeOfFunctionalPermission.OVERVIEW, Sets.newHashSet(Dialog.OVERVIEW));

    MAP_PERMISSION_TO_DIALOG = ImmutableMap.copyOf(mapping);
  }

  private Set<Role>                                                         roles            = new HashSet<Role>();

  public PermissionFunctional() {
    super();
  }

  public PermissionFunctional(TypeOfFunctionalPermission typeOfFunctionalPermission) {
    super();
    this.typeOfFunctionalPermission = typeOfFunctionalPermission;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public TypeOfFunctionalPermission getTypeOfFunctionalPermission() {
    return typeOfFunctionalPermission;
  }

  public void setTypeOfFunctionalPermission(TypeOfFunctionalPermission typeOfFunctionalPermission) {
    this.typeOfFunctionalPermission = typeOfFunctionalPermission;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public int compareTo(PermissionFunctional other) {
    Locale currentLocale = UserContext.getCurrentLocale();
    String nameThis = MessageAccess.getString(this.getTypeOfFunctionalPermission().toString(), currentLocale);
    String nameOther = MessageAccess.getString(other.getTypeOfFunctionalPermission().toString(), currentLocale);
    return nameThis.compareToIgnoreCase(nameOther);
  }

  public String getIdentityString() {
    return getTypeOfFunctionalPermission().toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }

    final PermissionFunctional other = (PermissionFunctional) obj;
    return Objects.equal(typeOfFunctionalPermission, other.getTypeOfFunctionalPermission());
  }

  @Override
  public int hashCode() {
    int prime = 29;
    int result = 1;

    result = prime * result + (typeOfFunctionalPermission != null ? typeOfFunctionalPermission.hashCode() : 0);

    return result;
  }

  @Override
  public String toString() {
    return String.valueOf(getTypeOfFunctionalPermission());
  }

}