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
/**
 * 
 */
package de.iteratec.iteraplan.presentation;

import static de.iteratec.iteraplan.presentation.SessionConstants.GUI_CONTEXT;
import static de.iteratec.iteraplan.presentation.SessionConstants.LOGGED_IN_USER_LOGIN;
import static de.iteratec.iteraplan.presentation.SessionConstants.LOGGED_IN_USER_ROLES;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.DataSourceService;
import de.iteratec.iteraplan.businesslogic.service.ElasticMiContextAndStakeholderManagerInitializationService;
import de.iteratec.iteraplan.businesslogic.service.ElasticeamService;
import de.iteratec.iteraplan.businesslogic.service.RoleService;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.springframework.security.UserContextInitializationService;


/**
 * Default {@link UserContextInitializationService} implementation for initializing user context and setting it in session.
 */
public class UserContextInitializationServiceImpl implements UserContextInitializationService {
  private static final Logger                                        LOGGER               = Logger
                                                                                              .getIteraplanLogger(UserContextInitializationServiceImpl.class);
  private static final Logger                                        LOGIN_LOGGER         = Logger.getIteraplanLogger("auditing.logins");
  /** initialize menu status, only watched elements closed by default */
  private static final Boolean[]                                     EXPANDED_MENU_STATUS = { Boolean.TRUE, Boolean.TRUE, Boolean.FALSE };
  private DataSourceService                                          dataSourceService;
  /** Role service for getting all existing roles. */
  private RoleService                                                roleService;
  /** User service for getting user entity. */
  private UserService                                                userService;
  /** Transaction manager for executing DB calls in transaction. */
  private PlatformTransactionManager                                 transactionManager;

  private ElasticMiContextAndStakeholderManagerInitializationService elasticMiContextAndStakeholderManagerInitializationService;

  private ElasticeamService                                          elasticService;

  /** {@inheritDoc} */
  public String initializeUserContext(HttpServletRequest req, Authentication authentication) {
    try {
      final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
      final TransactionCallback<String> transactionCallback = new StoreContextCallback(req, authentication);

      return transactionTemplate.execute(transactionCallback);
    } catch (TransactionException e) {
      LOGGER.error("Data source is not available", e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.LOGIN_DB_DATASOURCE_NOT_AVAILABLE, e);
    }
  }

  /**
   * Creates and stores a new instance of {@link GuiContext} into the session.
   * 
   * @param req the current request
   */
  private GuiContext createGuiContext(final HttpServletRequest req) {
    final HttpSession session = req.getSession();
    final GuiContext guiContext = new GuiContext();

    guiContext.setExpandedMenuStatus(EXPANDED_MENU_STATUS);
    session.setAttribute(GUI_CONTEXT, guiContext);
    GuiContext.setCurrentGuiContext(guiContext);
    LOGGER.debug("Created initial GuiContext.");

    return guiContext;
  }

  /**
   * Stores the user's login name and the user's roles to the session. The stored information is
   * used in {@link de.iteratec.iteraplan.presentation.dialogs.Start.InitCommand} to create the
   * {@link UserContext} object.
   * 
   * Retrieves all information about the logged-in user from the iteraplan database, creates the
   * {@link UserContext} filled with the respective values of the logged-in user and stores the
   * context in the global memory. Also checks, if the user's password has expired. If the user
   * context already exists, nothing is done.
   * 
   * @return An error message key or null if everything was successful.
   */
  private String createAndStoreUserContext(HttpServletRequest req, Authentication authentication) {
    HttpSession session = req.getSession();

    String userLogin = StringUtils.trim(authentication.getName());
    session.setAttribute(LOGGED_IN_USER_LOGIN, userLogin);

    // Make sure that the MASTER data source is used upon login. The user's context stored in the
    // thread local of the UserContext is not null, if the user has already logged into iteraplan
    // previously and the server has not been restarted since. Note that though the session has
    // been invalidated on logout (see Spring Security configuration), but the UserContext is still
    // there.
    // Thus the reference to the data source that the user connects to must be explicitly set to
    // the MASTER data source. Otherwise the data source stored in the context is used, but the
    // according database does usually not contain all the data necessary for a successful login
    // (e.g. the role for the demo access to iteraplan).
    if (UserContext.getCurrentUserContext() != null) {
      LOGGER.info("Point the user to the MASTER data source.");
      UserContext.getCurrentUserContext().setDataSource(Constants.MASTER_DATA_SOURCE);
    }

    final Set<String> userRoles = getUserRoles(authentication);
    session.setAttribute(LOGGED_IN_USER_ROLES, userRoles);

    User user = userService.getUserByLoginIfExists(userLogin);
    final Set<Role> roles = loadRoles(userRoles);
    Locale locale = RequestContextUtils.getLocale(req);

    // Create and store user context.
    UserContext userContext = new UserContext(userLogin, roles, locale, user);
    UserContext.setCurrentUserContext(userContext);
    session.setAttribute(SessionConstants.USER_CONTEXT, userContext);
    LOGGER.debug("User context created and stored in user's session.");

    LOGIN_LOGGER.info(userContext.toCSVString());

    if (user == null) {
      user = userService.createUser(userLogin);

      // Create and store user context.
      // the new user can only be created after the user context is set (above)
      // as the update of an entity triggers the LastModificationInterceptor, which
      // expects an already set user context
      userContext = new UserContext(userLogin, roles, locale, user);
      UserContext.detachCurrentUserContext();
      UserContext.setCurrentUserContext(userContext);
      session.setAttribute(SessionConstants.USER_CONTEXT, userContext);
    }

    readLdapData(authentication.getPrincipal(), user);

    if (roles != null && !roles.isEmpty() && !(roles.containsAll(user.getRoles()) && user.getRoles().containsAll(roles))) {
      user.clearRoles();
      for (Role role : roles) {
        user.addRoleTwoWay(role);
      }
      userService.saveOrUpdate(user);
    }

    final String errorMessageKey = createDataSource(userContext);
    if (errorMessageKey != null) {
      return errorMessageKey;
    }

    LOGGER.info("User has logged in.");

    // notify ElasticeamService (bean), that the metamodel and model for the 'new' datasource needs to be loaded
    elasticService.initOrReload();

    //Create elasticMiContext
    ElasticMiContext elasticMiContext = elasticMiContextAndStakeholderManagerInitializationService.initializeMiContextAndStakeholderManager(
        userLogin, userContext.getDataSource());
    session.setAttribute(SessionConstants.ELASTIC_MI_CONTEXT, elasticMiContext);

    return null;
  }

  private void readLdapData(Object principal, User user) {
    // try to gather user fields of user if we authenticate against an ldap
    // @see IteraplanLdapUserDetailsMapper
    String firstName = null;
    String lastName = null;
    String email = null;
    if (principal instanceof IteraplanLdapUserDetails) {
      LOGGER.debug("Found IteraplanLdapUserDetails");
      firstName = ((IteraplanLdapUserDetails) principal).getFirstName();
      lastName = ((IteraplanLdapUserDetails) principal).getLastName();
      email = ((IteraplanLdapUserDetails) principal).getMail();
    }

    boolean modified = false;
    if (firstName != null && !firstName.equals(user.getFirstName())) {
      LOGGER.debug("Setting user firstName to \"{0}\".", firstName);
      user.setFirstName(firstName);
      modified = true;
    }
    if (lastName != null && !lastName.equals(user.getLastName())) {
      LOGGER.debug("Setting user lastName to \"{0}\".", lastName);
      user.setLastName(lastName);
      modified = true;
    }
    if (email != null && !email.equals(user.getEmail())) {
      LOGGER.debug("Setting user email to \"{0}\".", email);
      user.setEmail(email);
      modified = true;
    }
    if (modified) {
      userService.saveOrUpdate(user);
    }
  }

  private Set<String> getUserRoles(Authentication authentication) {
    final Set<String> grantedAuthorities = getGrantedAuthorities(authentication);
    final List<Role> allRoles = roleService.getAllRolesFiltered();

    Set<String> userRoles = Sets.newHashSet();
    for (Role role : allRoles) {
      if (grantedAuthorities.contains(role.getRoleName())) {
        userRoles.add(role.getRoleName());
        LOGGER.debug("User has role {0}", role.getRoleName());
      }
    }

    return userRoles;
  }

  private Set<String> getGrantedAuthorities(Authentication authentication) {
    final Set<String> grantedAuthorities = Sets.newHashSet();
    for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
      grantedAuthorities.add(grantedAuthority.getAuthority());
    }

    return grantedAuthorities;
  }

  /**
   * Test, if the initial data set is available. Because we are not yet logged into the
   * application we have to deal with errors via transitions in the dialog machine and
   * display them differently than from within the application.
   * 
   * @return error message or {@code null}
   */
  private String checkDatabaseAccess() {
    try {
      if (roleService.getRoleByName(Role.SUPERVISOR_ROLE_NAME) == null) {
        return "LOGIN_DB_DATA_NOT_AVAILABLE";
      }
    } catch (DataAccessException ex) {
      LOGGER.fatal("Exception: The iteraplan database schema is not accessible.", ex);
      return "LOGIN_DB_SCHEMA_NOT_AVAILABLE";
    }

    return null;
  }

  /**
   * Returns the set of {@link Role}s from the database that are associated with the user stored in
   * the {@link SessionConstants#LOGGED_IN_USER_ROLES} constant. Note that aggregated roles are
   * added to the set as well.
   * 
   * @param userRoles the current user's session
   * @return freshly loaded user roles
   */
  Set<Role> loadRoles(Set<String> userRoles) {

    // Find directly assigned roles.
    Set<Role> roles = Sets.newHashSet();
    for (String role : userRoles) {
      roles.add(roleService.getRoleByName(role));
    }

    Set<Role> rolesAggregated = new HashSet<Role>();
    rolesAggregated.addAll(roles);

    // Find aggregated roles and add to final set
    for (Role role : roles) {
      rolesAggregated.addAll(role.getConsistsOfRolesAggregated());

    }

    return rolesAggregated;

  }

  /**
   * Creates a new BasicDataSource for the currently logged-in user.
   * 
   * <p>Here the data source has to point explicitly to the MASTER data source, because only
   * that database contains the relevant list of data sources. Thus, without changing
   * the data source, the initializeDBwithKey()-method (see below) uses the currently assigned
   * data source.
   * This must not happen, because the data accessed in that method only exists in the MASTER database.
   * 
   * @param userContext the user context that shall be initialized with the user data source.
   * @return <code>null</code> if successful, otherwise an error message key.
   */
  private String createDataSource(UserContext userContext) {
    String key = userContext.getDataSource();
    userContext.setDataSource(Constants.MASTER_DATA_SOURCE);

    try {
      dataSourceService.initializeDBwithKey(key);
    } catch (IteraplanTechnicalException ex) {
      LOGGER.error("Exception: Validation of data source.", ex);
      return IteraplanErrorMessages.getErrorMsgKey(ex.getErrorCode());
    } finally {
      // Reset the key to the data source stored in the user context.
      userContext.setDataSource(key);
    }

    return null;
  }

  private final class StoreContextCallback implements TransactionCallback<String> {
    /** Current request. */
    private final HttpServletRequest req;
    /** Current user authentication. */
    private final Authentication     authentication;

    StoreContextCallback(HttpServletRequest req, Authentication authentication) {
      this.req = req;
      this.authentication = authentication;
    }

    public String doInTransaction(TransactionStatus status) {
      final String databaseAccessErrorKey = checkDatabaseAccess();
      if (databaseAccessErrorKey != null) {
        return databaseAccessErrorKey;
      }

      createGuiContext(req);

      final String errorMessageKey = createAndStoreUserContext(req, authentication);
      if (errorMessageKey != null) {
        return errorMessageKey;
      }

      return null;
    }
  }

  public void setRoleService(RoleService roleService) {
    this.roleService = roleService;
  }

  public void setDataSourceService(DataSourceService dataSourceService) {
    this.dataSourceService = dataSourceService;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public void setTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  public void setElasticService(ElasticeamService elasticService) {
    this.elasticService = elasticService;
  }

  public void setElasticMiContextAndStakeholderManagerInitializationService(ElasticMiContextAndStakeholderManagerInitializationService elasticMiContextAndStakeholderManagerInitializationService) {
    this.elasticMiContextAndStakeholderManagerInitializationService = elasticMiContextAndStakeholderManagerInitializationService;
  }

}
