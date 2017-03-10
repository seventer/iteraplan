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
package de.iteratec.turm;

import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import de.iteratec.turm.common.TurmProperties;
import de.iteratec.turm.dao.RoleDao;
import de.iteratec.turm.dao.UserDao;
import de.iteratec.turm.exceptions.DaoException;
import de.iteratec.turm.exceptions.TurmException;
import de.iteratec.turm.model.Role;
import de.iteratec.turm.model.RoleContainer;
import de.iteratec.turm.model.User;
import de.iteratec.turm.model.UserContainer;


/**
 * This class creates some sample data which can be used for testing.
 * 
 * All existing data will be erased from the database.
 */
public class CreateTestData {

  public static final String PROP_USER            = "de.iteratec.turm.test.databaseUser";
  public static final String PROP_PASSWORD        = "de.iteratec.turm.test.databasePassword";
  public static final String PROP_URL             = "de.iteratec.turm.test.databaseUrl";
  public static final String PROP_JDBCDRIVERCLASS = "de.iteratec.turm.test.jdbcDriverClass";

  private static RoleDao roleDao;
  private static UserDao userDao;

  /**
   * The main class.
   * 
   * Sets up a simple datasource, deletes all existing data from the database
   * and inserts the test data.
   * 
   * @param args not used.
   * @throws ClassNotFoundException
   * @throws NamingException
   * @throws TurmException
   */
  public static void main(String[] args) throws NamingException, TurmException {
    setUpJndiDataSouce();
    ConfigurableApplicationContext applicationCtx = ApplicationContextUtil.getApplicationContext();
    roleDao = applicationCtx.getBean(RoleDao.class);
    userDao = applicationCtx.getBean(UserDao.class);
    deleteTestData();
    createTestData();
  }

  /**
   * Create the test data.
   * 
   * @throws TurmException
   */
  private static void createTestData() throws TurmException {
    // create roles
    RoleContainer rc = new RoleContainer();
    rc.setRoleName("Administrator iteraplan");
    Number adminRoleId = roleDao.createNewRole(rc);
    rc.setRoleName("Bebauungsplaner Informationssysteme");
    Number isPlanerRoleId = roleDao.createNewRole(rc);
    rc.setRoleName("Bebauungsplaner Prozesse");
    Number processPlanerRoleId = roleDao.createNewRole(rc);
    rc.setRoleName("CEO/CIO/Stratege");
    roleDao.createNewRole(rc);
    rc.setRoleName("Informationssystem-Verantwortlicher");
    Number isRoleId = roleDao.createNewRole(rc);
    rc.setRoleName("IT-Architekt");
    Number architectRoleId = roleDao.createNewRole(rc);
    rc.setRoleName("iteraplan_Supervisor");
    Number superVisorRoleId = roleDao.createNewRole(rc);

    // create users
    UserContainer uc = null;
    uc = new UserContainer(null, null, "hme", "Hans", "Meyer", "admin", "admin",
        new String[] { adminRoleId.toString() });
    userDao.createNewUser(uc);
    uc = new UserContainer(null, null, "jdo", "James", "Donald", "reader", "reader",
        new String[] { isRoleId.toString() });
    userDao.createNewUser(uc);
    uc = new UserContainer(null, null, "fmu", "Franz", "Mustermann", "manager", "manager",
        new String[] { architectRoleId.toString() });
    userDao.createNewUser(uc);
    uc = new UserContainer(null, null, "mmu", "Markus", "Müller", "Reader123", "Reader123",
        new String[] { isPlanerRoleId.toString(), processPlanerRoleId.toString() });
    userDao.createNewUser(uc);
    uc = new UserContainer(null, null, "system", "-", "-", "password", "password",
        new String[] { superVisorRoleId.toString() });
    userDao.createNewUser(uc);
  }

  /**
   * Delete all test data from the database.
   * 
   * @throws DaoException
   */
  private static void deleteTestData() throws DaoException {
    List<User> users = userDao.loadAllUsers();
    for (Iterator<User> it = users.iterator(); it.hasNext();) {
      User user = it.next();
      userDao.deleteUser(user.getId());
    }
    List<Role> allRoles = roleDao.loadAllRoles();
    for (Iterator<Role> it = allRoles.iterator(); it.hasNext();) {
      Role role = it.next();
      roleDao.deleteRole(role.getId());
    }
  }

  /**
   * Set up a simple data source factory which can be used for testing purposes.
   * 
   * @throws ClassNotFoundException
   * @throws NamingException
   */
  private static void setUpJndiDataSouce() throws NamingException {
    // get database configuration using system properties
    String url = System.getProperty(CreateTestData.PROP_URL);
    String user = System.getProperty(CreateTestData.PROP_USER);
    String password = System.getProperty(CreateTestData.PROP_PASSWORD);
    String jdbcDriverClass = System.getProperty(CreateTestData.PROP_JDBCDRIVERCLASS);
    if (url == null || user == null || password == null || jdbcDriverClass == null) {
      throw new IllegalStateException("Not all necessary system properties have been set: " + "\n"
          + CreateTestData.PROP_URL + ": " + url + "\n" + CreateTestData.PROP_USER
          + ": " + user + "\n" + CreateTestData.PROP_PASSWORD + ": " + password + "\n"
          + CreateTestData.PROP_JDBCDRIVERCLASS + ": " + jdbcDriverClass);
    }
    // Bind Test Data Source to a simple JNDI provider
    System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "de.iteratec.turm.DummyFactory");
    Context ctx = (new DummyFactory()).getInitialContext(null);
    DriverManagerDataSource ds = new SingleConnectionDataSource(url, user, password, true);
    ds.setDriverClassName(jdbcDriverClass);
    String datasource = TurmProperties.getProperties().getProperty(TurmProperties.TURM_DATASOURCE);
    ctx.bind(datasource, ds);
  }

}
