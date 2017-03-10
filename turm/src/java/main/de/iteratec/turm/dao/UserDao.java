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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
import de.iteratec.turm.exceptions.TurmException;
import de.iteratec.turm.exceptions.UserInputException;
import de.iteratec.turm.model.Role;
import de.iteratec.turm.model.User;
import de.iteratec.turm.model.UserContainer;


/**
 * Class for reading, creating and updating user data in the database.
 */
public class UserDao extends BaseDao {

  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(UserDao.class);

  private RoleDao             roleDao;

  /**
   * Delete an existing user in the database.
   * 
   * @param id The id of the user to delete.
   * @throws DaoException If the user could not be deleted.
   */
  public void deleteUser(Number id) throws DaoException {
    List<UpdateStatement> statements = new ArrayList<UpdateStatement>();
    // user <-> roles table
    statements.add(new UpdateStatement("delete from im_user_has_role where im_user_id=?", id ));
    // user table
    statements.add(new UpdateStatement("delete from im_user where id=?", id));
    executeUpdates(statements, null);
  }

  /**
   * Create a new user in the database.
   * 
   * @param newUser The Java Bean that contains the data of the user that is to
   *                be saved in the database.
   * @throws TurmException If the user could not be saved.
   */
  public void createNewUser(UserContainer newUser) throws TurmException {
    List<Role> roles = null;
    if (newUser.getRoleIds() == null || newUser.getRoleIds().length == 0) {
      roles = new ArrayList<Role>();
    }
    else {
      roles = roleDao.loadRolesByIdStrings(newUser.getRoleIds());
    }
    Number newUserId = getNextId();
    List<UpdateStatement> inserts = new ArrayList<UpdateStatement>();

    // create the insert statement for the user.
    inserts.add(new UpdateStatement( "insert into im_user(id, olVersion, loginName, firstName, lastName, password, lastPasswordChange) values(?, ?, ?, ?, ?, ?, ?)",
        newUserId, BigDecimal.valueOf(0), newUser.getLoginName(), newUser.getFirstName(), newUser.getLastName(),
        newUser.getNewPasswordEncrypted(), new Date(System.currentTimeMillis())));
    // create the insert statement for his roles.
    for (Role role : roles) {
      inserts.add(new UpdateStatement("insert into im_user_has_role(im_user_id, im_role_id) values (?,?)",
          newUserId, role.getId()));
    }
    executeUpdates(inserts, null);
  }

  /**
   * Update an existing user in the database.
   * 
   * @param editUser The Java Bean that contains the user data that is to be
   *                 updated in the database.
   * @throws DaoException If the user could not be updated.
   */
  public void updateUser(UserContainer editUser) throws DaoException {
    List<Role> roles = null;
    if (editUser.getRoleIds() == null || editUser.getRoleIds().length == 0) {
      roles = new ArrayList<Role>();
    }
    else {
      roles = roleDao.loadRolesByIdStrings(editUser.getRoleIds());
    }
    List<UpdateStatement> updateQueries = new ArrayList<UpdateStatement>();
    // delete the association to all roles
    updateQueries.add(new UpdateStatement("delete from im_user_has_role where im_user_id=?", editUser.getId()));
    // create the update statement for the user itself.
    updateQueries.add(new UpdateStatement("update im_user set olVersion=olVersion+1, loginName=?, firstName=?, lastName=? where id=?",
        editUser.getLoginName(), editUser.getFirstName(), editUser.getLastName(), editUser.getId()));

    // insert new associations for the new roles.
    for (Role role : roles) {
      updateQueries.add(new UpdateStatement(
          "insert into im_user_has_role(im_user_id, im_role_id) values (?,?)", editUser.getId(), role.getId()));
    }
    OlVersionCheck olVersionCheck = new OlVersionCheck("select u.olVersion as olVersion from im_user u where u.id = ?", editUser.getId(), editUser.getOlVersion());
    try {
      executeUpdates(updateQueries, olVersionCheck);
    } catch (OptimisticLockingFailureException e) {
      throw new DaoException("error.userHasBeenEdited", e);
    }
  }

  /**
   * Load an existing user from the database by id.
   * 
   * @param id The id of the user.
   * @return The user.
   * @throws DaoException If the user could not be loaded.
   */
  public User loadUserById(Number id) throws DaoException {
    List<Number> userIds = new ArrayList<Number>();
    userIds.add(id);
    List<User> users = loadUsersByIds(userIds);
    if (users.isEmpty()) {
      throw new DaoException("error.userNotFound");
    }
    return users.get(0);
  }

  /**
   * Load all existing users from the database.
   * 
   * The users that are returned are also filled with the associated roles.
   * 
   * @return A list of all users.
   * @throws DaoException If the users could not be loaded.
   */
  public List<User> loadAllUsers() throws DaoException {
    return loadUsersByIds(null);
  }

  /**
   * Load a list of users by id.
   * 
   * The users that are returned are also filled with the associated roles.
   * 
   * @param userIdsAsStrings An array that contains the id of the users to load
   *                         as Strings.
   * @return A list of users that have the given ids.
   * @throws DaoException If the users could not be loaded.
   * @see #loadUsersByIds(List)
   */
  public List<User> loadUsersByIdStrings(String[] userIdsAsStrings) throws DaoException {
    ArrayList<Number> userIds = new ArrayList<Number>();
    for (String id : userIdsAsStrings) {
      userIds.add(Long.valueOf(id));
    }
    return loadUsersByIds(userIds);
  }

  /**
   * Load a list of users by id.
   * 
   * The users that are returned are also filled with the associated roles.
   * 
   * @param userIds A list of ids of users that are to be loaded from the database.
   * @return A list of users that have the given ids.
   * @throws DaoException If the users could not be loaded.
   */
  public List<User> loadUsersByIds(List<Number> userIds) throws DaoException {
    List<Number> userIdsToLoad = userIds;
    if (userIdsToLoad == null) {
      userIdsToLoad = new ArrayList<Number>();
    }
    // This query will also return the associated roles. However, it will
    // also return duplicate users, so we have to group the relevant
    // information later on.
    StringBuilder query = new StringBuilder("select u.id as userId, u.olVersion as olVersion, u.loginName as loginName, u.firstName as firstName, u.lastName as lastName, r.id as roleId, r.olVersion as roleOlVersion, r.roleName as roleName, r.description as roleDescription "
        + "from im_user u left outer join im_user_has_role ur on u.id=ur.im_user_id left outer join im_role r on ur.im_role_id=r.id");
    for (int i = 0; i < userIdsToLoad.size(); i++) {
      if (i <= 0) {
        query.append(" where");
      }
      else {
        query.append(" or");
      }
      query.append(" u.id = ?");
    }
    query.append(" order by lower(u.loginName), lower(r.roleName)");

    // helper object to transform JDBC results into users with their attached roles
    ResultSetExtractor<List<User>> userWithRolesExtractor = new ResultSetExtractor<List<User>>() {

      public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        // use LinkedHashMap in order to preserve the insertion order - the database has sorted results already
        Map<Integer, User> userMap = new LinkedHashMap<Integer, User>();
        userMap.put(Integer.valueOf(0), null);

        while (rs.next()) {
          // be careful as userId == NULL means id == 0 in Java
          Integer id = rs.getInt("userId");
          User user = userMap.get(id);

          // if we didn't process the user yet, add it to the list.
          if (user == null) {
            Number olVersion = rs.getInt("olVersion");
            String loginName = rs.getString("loginName");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            List<Role> roles = new ArrayList<Role>();
            user = new User(id, olVersion, loginName, firstName, lastName, roles);
            userMap.put(id, user);
          }

          // collect the associated role and add it to the user.
          Integer roleId = rs.getInt("roleId");
          // be careful as roleId == NULL means roleId == 0 in Java
          if (roleId != null && roleId > 0) {
            Integer roleOlVersion = rs.getInt("roleOlVersion");
            String roleName = rs.getString("roleName");
            String roleDescription = rs.getString("roleDescription");
            Role role = new Role(roleId, roleOlVersion, roleName, roleDescription, new ArrayList<User>());
            user.getRoles().add(role);
          }
        }

        userMap.remove(Integer.valueOf(0));
        return new ArrayList<User>(userMap.values());
      }

    };

    // execute the query and return the transformed List
    return executeQuery(query.toString(), userWithRolesExtractor, userIdsToLoad.toArray());
  }

  /**
   * Updates the password of an existing user.
   * 
   * @param loginName The login name of the user whos password is to be changed.
   * @param oldPasswordEncrypted The current password (encrypted)
   * @param newPasswordEncrypted The new password (encrypted)
   * @throws TurmException If the password could not be updated in the database.
   */
  public void updateUserPassword(String loginName, String oldPasswordEncrypted,
                                 String newPasswordEncrypted) throws TurmException {
    UpdateStatement updateStatement = new UpdateStatement("update im_user set password=?, lastPasswordChange=? where loginName like ? and upper(password) like upper(?)",
        newPasswordEncrypted, new Date(System.currentTimeMillis()), loginName, oldPasswordEncrypted);

    List<UpdateStatement> l = Arrays.asList(updateStatement);
    int count = executeUpdates(l, null);
    if (count < 1) {
      throw new UserInputException("error.badCredentials");
    }
    else if (count > 1) {
      throw new TurmException("error.internalError");
    }
  }

  public void resetUserPassword(String loginName,
                                String newPasswordEncrypted) throws TurmException {
    UpdateStatement updateStatement = new UpdateStatement("update im_user set password=?, lastPasswordChange=? where loginName like ? ",
        newPasswordEncrypted, new Date(System.currentTimeMillis()), loginName);

    List<UpdateStatement> l = Arrays.asList(updateStatement);
    int count = executeUpdates(l, null);
    if (count < 1) {
      throw new UserInputException("error.badCredentials");
    }
    else if (count > 1) {
      throw new TurmException("error.internalError");
    }
  }

  public void setRoleDao(RoleDao roleDao) {
    this.roleDao = roleDao;
  }

}
