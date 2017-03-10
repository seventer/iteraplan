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
package de.iteratec.iteraplan.persistence.dao;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.util.SqlHqlStringUtils;


/**
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 *
 */
public class RoleDAOImpl extends GenericBaseDAO<Role, Integer> implements RoleDAO {

  /** {@inheritDoc} */
  public Role getRoleByName(final String name) {
    if (name == null) {
      return null;
    }

    HibernateCallback<Role> callback = new HibernateCallback<Role>() {
      public Role doInHibernate(Session session) {
        Query q = getSession().getNamedQuery("getRolesByName");
        q.setString("roleName", SqlHqlStringUtils.escapeSqlLikeOperatorWildcards(name, getSession()));
        return (Role) q.uniqueResult();
      }
    };
    return getHibernateTemplate().execute(callback);
  }

  /** {@inheritDoc} */
  public Role getSupervisorRole() {
    Role role = getRoleByName(Role.SUPERVISOR_ROLE_NAME);
    if (role == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.DATABASE_NOT_INITIALIZED);
    }

    return role;
  }

  /** {@inheritDoc} */
  public List<Role> getAllRolesFiltered() {
    return executeNamedQuery("getRolesByName", "roleName", "%");
  }

  /** {@inheritDoc} */
  public PermissionFunctional getPermissionFunctionalByType(TypeOfFunctionalPermission type) {
    return getFunctionalPermissionByString(type.toString());
  }

  /** {@inheritDoc} */
  public PermissionFunctional getFunctionalPermissionByString(final String permission) {
    HibernateCallback<PermissionFunctional> callback = new FunctionalPermissionByStringCallback(permission);
    return getHibernateTemplate().execute(callback);

  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    return "roleName";
  }

  /** {@inheritDoc} */
  @Override
  protected void onBeforeDelete(Role entity) {
    super.onBeforeDelete(entity);
    removeAllElementOfRoles(entity);
    removeAllPermissionFunctional(entity);
    entity.removePermissionsBbt();
    entity.removePermissionsAttrTypeGroups();
  }

  private void removeAllElementOfRoles(Role entity) {
    for (Iterator<Role> it = entity.getElementOfRoles().iterator(); it.hasNext();) {
      Role elementOfRole = it.next();
      elementOfRole.getConsistsOfRoles().remove(entity);
      it.remove();
    }
  }

  private void removeAllPermissionFunctional(Role entity) {
    for (Iterator<PermissionFunctional> it = entity.getPermissionsFunctional().iterator(); it.hasNext();) {
      PermissionFunctional permFunc = it.next();
      permFunc.getRoles().remove(entity);
      it.remove();
    }
  }

  private static final class FunctionalPermissionByStringCallback implements HibernateCallback<PermissionFunctional> {
    private final String permission;

    public FunctionalPermissionByStringCallback(String permission) {
      this.permission = permission;
    }

    @SuppressWarnings("unchecked")
    public PermissionFunctional doInHibernate(Session session) {
      Query q = session.getNamedQuery("getPermissionFunctionalByType");
      q.setString("tofp", SqlHqlStringUtils.escapeSqlLikeOperatorWildcards(permission, session));
      List<PermissionFunctional> list = q.list();
      if (list.size() != 1) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
      return list.get(0);
    }
  }

}
