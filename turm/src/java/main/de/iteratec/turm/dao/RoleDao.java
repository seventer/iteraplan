/*
 * iTURM is a User and Roles Management web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.turm.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.ResultSetExtractor;

import de.iteratec.turm.common.Logger;
import de.iteratec.turm.dao.common.OlVersionCheck;
import de.iteratec.turm.dao.common.UpdateStatement;
import de.iteratec.turm.exceptions.DaoException;
import de.iteratec.turm.model.Role;
import de.iteratec.turm.model.RoleContainer;
import de.iteratec.turm.model.User;


/**
 * Class for reading, creating and updating role data in the database.
 */
public class RoleDao extends BaseDao {

  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(RoleDao.class);

  private UserDao             userDao;

  /**
   * Delete an existing role in the database.
   * 
   * @param roleId The id of the role to delete.
   * @throws DaoException If the role could not be deleted.
   */
  public void deleteRole(Number roleId) throws DaoException {
    List<UpdateStatement> queries = new ArrayList<UpdateStatement>();
    // role <-> user
    queries.add(new UpdateStatement("delete from im_user_has_role where im_role_id=?", roleId));
    // role
    queries.add(new UpdateStatement("delete from im_role where id=?", roleId));
    executeUpdates(queries, null);
  }

  /**
   * Create a new role in the database.
   * 
   * @param newRole The Java Bean that contains all relevant information for
   *                creating the role.
   * @return The id of the newly created role.
   * @throws DaoException If the role could not be created.
   */
  public Number createNewRole(RoleContainer newRole) throws DaoException {
    List<User> users = null;
    if (newRole.getUserIds() == null || newRole.getUserIds().length == 0) {
      users = new ArrayList<User>();
    }
    else {
      users = userDao.loadUsersByIdStrings(newRole.getUserIds());
    }
    Number newRoleId = getNextId();
    List<UpdateStatement> insertQueries = new ArrayList<UpdateStatement>();

    insertQueries.add(new UpdateStatement("insert into im_role(id, olVersion, roleName, description) values(?, ?, ?, ?)",
        newRoleId, BigDecimal.valueOf(0), newRole.getRoleName(), newRole.getDescription()));
    for (User user : users) {
      insertQueries.add(new UpdateStatement("insert into im_user_has_role(im_user_id, im_role_id) values (?,?)",
          user.getId(), newRoleId));
    }
    executeUpdates(insertQueries, null);
    return newRoleId;
  }

  /**
   * Load an existing role from the database.
   * 
   * @param roleId The id of the role to load.
   * @return The role.
   * @throws DaoException If the role could not be loaded.
   */
  public Role loadRoleById(Number roleId) throws DaoException {
    List<Number> roleIds = new ArrayList<Number>();
    roleIds.add(roleId);
    List<Role> roles = loadRolesByIds(roleIds);
    if (roles.isEmpty()) {
      throw new DaoException("error.roleNotFound");
    }
    return roles.get(0);
  }

  /**
   * Load all existing roles from the database.
   * 
   * @return A list of all exisiting roles.
   * @throws DaoException If the roles could not be loaded.
   */
  public List<Role> loadAllRoles() throws DaoException {
    return loadRolesByIds(null);
  }

  /**
   * Loads a list of roles from the database.
   * 
   * The roles that are returned are also filled with the associated users.
   * 
   * @param roleIdsAsStrings An array that contains the ids of the roles to load as Strings.
   * @return The roles.
   * @throws DaoException If the roles could not be loaded.
   * @see #loadRolesByIds(List)
   */
  public List<Role> loadRolesByIdStrings(String[] roleIdsAsStrings) throws DaoException {
    List<Number> roleIds = new ArrayList<Number>();
    for (int i = 0; i < roleIdsAsStrings.length; i++) {
      roleIds.add(Long.valueOf(roleIdsAsStrings[i]));
    }
    return loadRolesByIds(roleIds);
  }

  /**
   * Loads a list of roles from the database.
   * 
   * The roles that are returned are also filled with the associated users.
   * 
   * @param roleIds A list of ids of roles that are to be loaded.
   * @return The roles.
   * @throws DaoException If the roles could not be loaded.
   */
  public List<Role> loadRolesByIds(List<Number> roleIds) throws DaoException {
    List<Number> roleIdsToLoad = roleIds;
    if (roleIdsToLoad == null) {
      roleIdsToLoad = new ArrayList<Number>();
    }
    // This query will also return the associated users. However, it will
    // also return duplicate roles, so we have to group the relevant
    // information later on.
    StringBuilder query = new StringBuilder("select r.id as roleId, r.olVersion as olVersion, r.roleName as roleName, r.description as description, u.id as userId, u.olVersion as userOlVersion, u.loginName as loginName, u.firstName as firstName, u.lastName as lastName "
        + "from im_role r left outer join im_user_has_role ur on r.id = ur.im_role_id left outer join im_user u on ur.im_user_id=u.id");
    for (int i = 0; i < roleIdsToLoad.size(); i++) {
      if (i <= 0) {
        query.append(" where");
      }
      else {
        query.append(" or");
      }
      query.append(" r.id=?");
    }
    query.append(" order by lower(r.roleName), lower(u.loginName)");

    // helper object to transform JDBC results into roles with their attached users
    ResultSetExtractor<List<Role>> roleWithUsersExtractor = new ResultSetExtractor<List<Role>>() {

      public List<Role> extractData(ResultSet rs) throws SQLException, DataAccessException {
        // use LinkedHashMap in order to preserve the insertion order - the database has sorted results already
        Map<Integer, Role> roleMap = new LinkedHashMap<Integer, Role>();
        roleMap.put(Integer.valueOf(0), null);

        while (rs.next()) {
          // be careful as roleId == NULL means id == 0 in Java
          Integer id = rs.getInt("roleId");
          Role role = roleMap.get(id);

          // if we didn't process this role yet, add it to the list.
          if (role == null) {
            Number olVersion = rs.getInt("olVersion");
            String roleName = rs.getString("roleName");
            String description = rs.getString("description");
            List<User> users = new ArrayList<User>();
            role = new Role(id, olVersion, roleName, description, users);
            roleMap.put(id, role);
          }

          // get the associated user and add it to the role.
          Integer userId = rs.getInt("userId");
          // be careful as userId == NULL means userId == 0 in Java
          if (userId != null && userId > 0) {
            Number userOlVersion = rs.getInt("userOlVersion");
            String loginName = rs.getString("loginName");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            User user = new User(userId, userOlVersion, loginName, firstName, lastName, new ArrayList<Role>());
            role.getUsers().add(user);
          }
        }

        roleMap.remove(Integer.valueOf(0));
        return new ArrayList<Role>(roleMap.values());
      }
    };

    // execute the query and return the transformed List
    return executeQuery(query.toString(), roleWithUsersExtractor, roleIdsToLoad.toArray());
  }

  /**
   * Update an existing role in the database.
   * 
   * @param roleToUpdate The Java Bean that contains the role information the is to
   *                     to be saved in the database.
   * @throws DaoException If the roel could not be updated.
   */
  public void updateRole(RoleContainer roleToUpdate) throws DaoException {
    List<User> users = null;
    if (roleToUpdate.getUserIds() == null || roleToUpdate.getUserIds().length == 0) {
      users = new ArrayList<User>();
    }
    else {
      users = userDao.loadUsersByIdStrings(roleToUpdate.getUserIds());
    }

    List<UpdateStatement> updateQueries = new ArrayList<UpdateStatement>();
    updateQueries.add(new UpdateStatement("delete from im_user_has_role where im_role_id=?", roleToUpdate.getId()));
    updateQueries.add(new UpdateStatement("update im_role set olVersion=olVersion+1, roleName=?, description=? where id=?",
        roleToUpdate.getRoleName(), roleToUpdate.getDescription(), roleToUpdate.getId()));
    for (User user : users) {
      updateQueries.add(new UpdateStatement("insert into im_user_has_role(im_user_id, im_role_id) values (?,?)",
          user.getId(), roleToUpdate.getId()));
    }
    OlVersionCheck olVersionCheck = new OlVersionCheck("select r.olVersion as olVersion from im_role r where r.id = ?", roleToUpdate.getId(), roleToUpdate.getOlVersion());

    try {
      executeUpdates(updateQueries, olVersionCheck);
    } catch (OptimisticLockingFailureException e) {
      throw new DaoException("error.roleHasBeenEdited", e);
    }
  }

  public void setUserDao(UserDao userDao) {
    this.userDao = userDao;
  }

}
