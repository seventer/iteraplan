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
package de.iteratec.iteraplan.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;


/**
 * Holds user related information for the duration of the session.
 * 
 * <p>Contains the currently logged in user, the roles associated with the user and other user specific
 * information. This object is initialized in {@link de.iteratec.springframework.security.LoginInitializationSuccessHandler}
 * which is called when entering or resetting iteraplan. Besides storing user specific preferences,
 * this object is used by business methods to determine whether the user has the rights to call the
 * method. The class itself holds UserContext instances for each thread in a ThreadLocal variable.
 * The instance is stored in the HttpSession and set into the ThreadLocal variable by the
 * IteraplanController at the beginning of every request.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class UserContext implements Serializable {

  private static final long                     serialVersionUID    = 3802267306338516151L;
  private static final Logger                   LOGGER              = Logger.getIteraplanLogger(UserContext.class);

  /**
   * The UserContext instance for each thread is held in the following ThreadLocal. The instances
   * are set and removed in the IteraplanCommand class.
   */
  private static final ThreadLocal<UserContext> CURRENT_USERCONTEXT = new ThreadLocal<UserContext>();

  /** The login name of the currently logged in user. */
  private String                                loginName           = null;

  /** The roles the user has. */
  private Set<Role>                             roles               = null;

  /** Contains the permission information for the user. */
  private Permissions                           perms               = null;

  /** Specifies whether the user wants to see inactive elements. */
  private boolean                               showInactiveStatus  = IteraplanProperties
                                                                        .getBooleanProperty(IteraplanProperties.CONFIG_SHOW_INACTIVE);

  /** Specifies which DB the user wants to work in. */
  private String                                dataSource          = Constants.MASTER_DATA_SOURCE;

  /** Specified the default choice, how many search results shall be displayed to the user */
  private short                                 defaultResultCount;

  private short                                 currentPageSize;

  /** The Locale of the user. */
  private Locale                                locale              = null;

  /** The id of the current HttpSession. Used for auditing purposes. */
  private String                                sessionId           = null;

  /** The user object associated with the currently logged in user. Can be null. */
  private User                                  user                = null;

  /**
   * Constructor. No parameter must be {@code null} or an empty string.
   * 
   * @param loginName
   *          The login name of the user.
   * @param roles
   *          The set of roles assigned to the user.
   * @param locale
   *          The current locale.
   * @param user
   *          The user object.
   */
  public UserContext(String loginName, Set<Role> roles, Locale locale, User user) {
    if (StringUtils.isEmpty(loginName) || (roles == null) || (locale == null)) {
      throw new IllegalArgumentException("Login Name, roles and locale must not be null or assigned an empty string.");
    }

    this.loginName = loginName;
    this.roles = roles;
    setLocale(locale);
    this.user = user;
    if ((user != null) && StringUtils.isNotEmpty(user.getDataSource())) {
      this.dataSource = user.getDataSource();
    }
    this.perms = new Permissions(this.user, this.roles);

    try {
      int preferredResultCount = IteraplanProperties.getIntProperty(IteraplanProperties.PROP_SEARCH_RESULT_COUNT);
      if ((preferredResultCount > 0) && (preferredResultCount <= Short.MAX_VALUE)) {
        this.defaultResultCount = (short) preferredResultCount;
        this.currentPageSize = defaultResultCount;
      }
    } catch (NumberFormatException nfe) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.PROPERTY_MISSING, nfe);
    }
  }

  public short getCurrentPageSize() {
    return currentPageSize;
  }

  public void setCurrentPageSize(short currentPageSize) {
    this.currentPageSize = currentPageSize;
  }

  public String getDataSource() {
    return dataSource;
  }

  public Locale getLocale() {
    return locale;
  }

  public String getLoginName() {
    return loginName;
  }

  public Permissions getPerms() {
    return perms;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public String getSessionId() {
    return sessionId;
  }

  public User getUser() {
    return user;
  }

  public List<String> getAvailableLocales() {
    return Constants.LOCALES;
  }

  /**
   * Returns the showInactiveStatus. If this is true, Information Systems and Catalog Items which
   * have the status inactive are not shown to the user.
   * 
   * @return the showInactiveStatus.
   */
  public boolean isShowInactiveStatus() {
    return showInactiveStatus;
  }

  public boolean isWorksWithMaster() {
    return Constants.MASTER_DATA_SOURCE.equals(this.dataSource);
  }

  public void setDataSource(String dbType) {
    this.dataSource = dbType;
  }

  public void setDefaultResultCount(short defaultResultCount) {
    this.defaultResultCount = defaultResultCount;
  }

  public short getDefaultResultCount() {
    return defaultResultCount;
  }

  public void setLocale(Locale locale) {
    // only set the requested locale if it's contained in the supported languages. Prevents problems caused
    // by the difference between the locale set and the local "shown" (i.e. English if unsupported).
    // Substring is needed so that the country-part of the locale-string is ignored.
    boolean acceptLocale = locale != null && Constants.LOCALES.contains(locale.getLanguage());
    this.locale = acceptLocale ? locale : (new Locale("en"));
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public void setShowInactiveStatus(boolean showInactiveStatus) {
    this.showInactiveStatus = showInactiveStatus;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(35);
    sb.append("\nUser details:");
    sb.append("\n-------------");
    sb.append("\n- Login name: ").append(loginName);
    sb.append("\n- Has been assigned the roles: ");
    for (Iterator<Role> it = roles.iterator(); it.hasNext();) {
      Role role = it.next();
      sb.append(role.getRoleName());
      if (it.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append("\n- Connects to data source: ").append(dataSource);
    sb.append("\n- Uses locale: ").append(getCurrentLocale());

    return sb.toString();
  }

  public String toCSVString() {

    StringBuilder sb = new StringBuilder(30);
    sb.append(";" + loginName + ";");
    for (Role role : roles) {
      sb.append(role.getRoleName() + " ");
    }
    sb.append(";" + dataSource);
    sb.append(";" + locale);

    return sb.toString();
  }

  /**
   * Returns the Locale of the UserContext instance for the current thread.
   * 
   * @return The Locale of the UserContext instance for the current thread.
   */
  public static Locale getCurrentLocale() {
    UserContext currentUserContext = getCurrentUserContext();
    if (currentUserContext != null) {
      return currentUserContext.getLocale();
    }
    return null;
  }

  /**
   * This method is a shortcut for getCurrentUserContext().getPerms().
   * 
   * @return The Permissions for the UserContext object for the current thread.
   */
  public static Permissions getCurrentPerms() {
    UserContext currentUserContext = getCurrentUserContext();
    if (currentUserContext != null) {
      return getCurrentUserContext().getPerms();
    }
    return null;
  }

  /**
   * Returns the UserContext instance for the current thread.
   * 
   * @return The UserContext instance for the current thread.
   */
  public static UserContext getCurrentUserContext() {
    return CURRENT_USERCONTEXT.get();
  }

  /**
   * Sets the given UserContext instance into a {@link ThreadLocal}. This allows all methods running
   * in the same thread to retrieve this instance.
   * 
   * @param userContext
   *          the new userContext
   */
  public static void setCurrentUserContext(UserContext userContext) {
    CURRENT_USERCONTEXT.set(userContext);
  }

  /**
   * Detaches/ removes the current User Context instance from this thread. After invoking this
   * method, {@link #getCurrentUserContext()} will return null. Therefore, be sure to store the
   * context in the user's session.
   */
  public static void detachCurrentUserContext() {
    CURRENT_USERCONTEXT.remove();
  }

  /**
   * Returns the currently active DataSource set within the currentUsercontext. If the current
   * Datasource cannot be resolved, returns an empty string for the Datasource and writes a log
   * message. Make sure not to call this method from within a thread.
   * 
   * @return the currently active DataSource
   */
  public static String getActiveDatasource() {
    UserContext uc = getCurrentUserContext();
    if ((uc != null) && !StringUtils.isEmpty(uc.getDataSource())) {
      return uc.getDataSource();
    }
    else {
      LOGGER.error("COULD_NOT_RESOLVE_ACTIVE_DATASOURCE; Using only ID for Index file!");
      return "";
    }
  }

  /**
   * Contains all permission relevant informations for the user.
   */
  @SuppressWarnings("PMD.BooleanGetMethodName")
  public static final class Permissions implements Serializable {

    private static final long                                                 serialVersionUID                      = 7100227981967716631L;

    /** The id of the current user. */
    private Integer                                                           userId                                = null;

    /** Contains all buidling block types (TypeOfBuildingBlock) the user has update permissions for. */
    private final Set<TypeOfBuildingBlock>                                    updateBbtPermissions                  = new HashSet<TypeOfBuildingBlock>();

    /** Contains all buidling block types (TypeOfBuildingBlock) the user has create permissions for. */
    private final Set<TypeOfBuildingBlock>                                    createBbtPermissions                  = new HashSet<TypeOfBuildingBlock>();

    /** Contains all buidling block types (TypeOfBuildingBlock) the user has delete permissions for. */
    private final Set<TypeOfBuildingBlock>                                    deleteBbtPermissions                  = new HashSet<TypeOfBuildingBlock>();

    /** Contains all functional permissions (TypeOfFunctionalPermission) the user has. */
    private final Set<TypeOfFunctionalPermission>                             functionalPerms                       = new HashSet<TypeOfFunctionalPermission>();

    /** Contains the ids of all (aggregated) roles the user has. */
    private final Set<Integer>                                                roleIds                               = new HashSet<Integer>();

    /** Contains the ids of all (aggregated) user groups the user is in. */
    private final Set<Integer>                                                userGroupIds                          = new HashSet<Integer>();

    /** Contains the names of all menus that the user can see (GuiContext constants). */
    private final Set<String>                                                 visibleMenus                          = new HashSet<String>();

    /** True if the user is the system administrator. */
    private boolean                                                           userIsAdministrator                   = false;

    private static final Set<TypeOfFunctionalPermission>                      FUNCTIONAL_PERM_TYPES_MENU_EADATA     = ImmutableSet
                                                                                                                        .of(TypeOfFunctionalPermission.OVERVIEW,
                                                                                                                            TypeOfFunctionalPermission.SEARCH,

                                                                                                                            TypeOfFunctionalPermission.BUSINESSDOMAIN,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSPROCESS,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSUNIT,
                                                                                                                            TypeOfFunctionalPermission.PRODUCT,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSFUNCTION,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSOBJECT,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSMAPPING,

                                                                                                                            TypeOfFunctionalPermission.INFORMATIONSYSTEMDOMAIN,
                                                                                                                            TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE,
                                                                                                                            TypeOfFunctionalPermission.INFORMATIONSYSTEMINTERFACE,

                                                                                                                            TypeOfFunctionalPermission.ARCHITECTURALDOMAIN,
                                                                                                                            TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES,
                                                                                                                            TypeOfFunctionalPermission.INFRASTRUCTUREELEMENT,

                                                                                                                            TypeOfFunctionalPermission.PROJECT);

    private static final Set<TypeOfFunctionalPermission>                      FUNCTIONAL_PERM_TYPES_MENU_REPORTS    = ImmutableSet
                                                                                                                        .of(TypeOfFunctionalPermission.TABULAR_REPORTING,
                                                                                                                            TypeOfFunctionalPermission.ITERAQL,
                                                                                                                            TypeOfFunctionalPermission.SUCCESSORREPORT,
                                                                                                                            TypeOfFunctionalPermission.CONSISTENCY_CHECK,
                                                                                                                            TypeOfFunctionalPermission.SUBSCRIPTION);

    private static final Set<TypeOfFunctionalPermission>                      FUNCTIONAL_PERM_TYPES_MENU_VISUAL     = ImmutableSet
                                                                                                                        .of(TypeOfFunctionalPermission.DASHBOARD,
                                                                                                                            TypeOfFunctionalPermission.GRAPHICAL_REPORTING);

    private static final Set<TypeOfFunctionalPermission>                      FUNCTIONAL_PERM_TYPES_MENU_MASS       = ImmutableSet
                                                                                                                        .of(TypeOfFunctionalPermission.MASSUPDATE,
                                                                                                                            TypeOfFunctionalPermission.XMISERIALIZATION,
                                                                                                                            TypeOfFunctionalPermission.XMIDESERIALIZATION,
                                                                                                                            TypeOfFunctionalPermission.EXCELIMPORT);

    private static final Set<TypeOfFunctionalPermission>                      FUNCTIONAL_PERM_TYPES_MENU_GOVERNANCE = ImmutableSet
                                                                                                                        .of(TypeOfFunctionalPermission.USER,
                                                                                                                            TypeOfFunctionalPermission.USERGROUP,
                                                                                                                            TypeOfFunctionalPermission.ROLE,
                                                                                                                            TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION,
                                                                                                                            TypeOfFunctionalPermission.SUPPORTING_QUERY,
                                                                                                                            TypeOfFunctionalPermission.CONSISTENCY_CHECK);

    private static final Set<TypeOfFunctionalPermission>                      FUNCTIONAL_PERM_TYPES_MENU_ADMIN      = ImmutableSet
                                                                                                                        .of(TypeOfFunctionalPermission.CONFIGURATION,
                                                                                                                            TypeOfFunctionalPermission.ATTRIBUTETYPEGROUP,
                                                                                                                            TypeOfFunctionalPermission.ATTRIBUTETYPE);

    private static final Map<TypeOfBuildingBlock, TypeOfFunctionalPermission> FUNCTIONAL_PERMS_FOR_BUILDING_BLOCKS  = new ImmutableMap.Builder<TypeOfBuildingBlock, TypeOfFunctionalPermission>()
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.ARCHITECTURALDOMAIN,
                                                                                                                            TypeOfFunctionalPermission.ARCHITECTURALDOMAIN)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.BUSINESSFUNCTION,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSFUNCTION)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.BUSINESSOBJECT,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSOBJECT)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.BUSINESSPROCESS,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSPROCESS)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.BUSINESSMAPPING,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSMAPPING)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE,
                                                                                                                            TypeOfFunctionalPermission.INFORMATIONSYSTEMINTERFACE)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.TRANSPORT,
                                                                                                                            TypeOfFunctionalPermission.INFORMATIONSYSTEMINTERFACE)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.INFRASTRUCTUREELEMENT,
                                                                                                                            TypeOfFunctionalPermission.INFRASTRUCTUREELEMENT)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.BUSINESSDOMAIN,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSDOMAIN)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.INFORMATIONSYSTEM,
                                                                                                                            TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE,
                                                                                                                            TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN,
                                                                                                                            TypeOfFunctionalPermission.INFORMATIONSYSTEMDOMAIN)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.PROJECT,
                                                                                                                            TypeOfFunctionalPermission.PROJECT)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.BUSINESSUNIT,
                                                                                                                            TypeOfFunctionalPermission.BUSINESSUNIT)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.PRODUCT,
                                                                                                                            TypeOfFunctionalPermission.PRODUCT)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.TECHNICALCOMPONENT,
                                                                                                                            TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES)
                                                                                                                        .put(
                                                                                                                            TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE,
                                                                                                                            TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES)
                                                                                                                        .build();

    /**
     * Constructor
     * 
     * @param user
     *          The User object, if it exists. Can be null.
     * @param roles
     *          The roles the user has. Not null.
     */
    public Permissions(final UserEntity user, final Set<Role> roles) {
      for (Role role : roles) {
        if (role.getRoleName().equals(Role.SUPERVISOR_ROLE_NAME)) {
          this.userIsAdministrator = true;
        }
        initPermissionsBbType(role);
        initPermissionsFunctional(role);
        this.roleIds.add(role.getId());
      }
      if (user != null) {
        this.userGroupIds.addAll(user.getUserGroupHierarchyAsIDs());
        this.userId = user.getId();
      }
      initVisibleMenusAndReadableBbTypes();
    }

    /**
     * @param tofps
     *          The TypeOfFunctionalPermissions to check. The user only has to have one of them.
     * @throws IteraplanBusinessException
     *           If the user does not have any of the given functional permissions.
     */
    public void assureAnyFunctionalPermission(final TypeOfFunctionalPermission[] tofps) {
      for (TypeOfFunctionalPermission permission : tofps) {
        if (userHasFunctionalPermission(permission)) {
          return;
        }

      }
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }

    /**
     * Assures that the current user has the specified permission on the given attribute type group
     * and throws an exception if not.
     * 
     * @param atg
     *          The attribute type group to check whether the current user has the permission on.
     * @param perm
     *          defines the permission to be checked
     * @throws IteraplanBusinessException
     *           thrown if the current user has not the specified permission on the attribute type
     *           group
     */
    public void assureAttrTypeGroupPermission(final AttributeTypeGroup atg, AttributeTypeGroupPermissionEnum perm) {
      if (!userHasAttrTypeGroupPermission(atg, perm)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
      }
    }

    /**
     * If a user wants to update a building block, the following permissions are needed:
     * <ul>
     * <li>A functional permission for accessing the management page for the building block type.</li>
     * <li>The update permission for the building block type.</li>
     * <li>If it exists, an exclusive write permission for the instance.</li>
     * </ul>
     * This method checks all three permissions.
     * 
     * @param buildingBlock
     *          The building block to check.
     * @throws IteraplanBusinessException
     *           If the user does not have any of the above permissions for the given building block
     *           instance.
     */
    public void assureBbUpdatePermission(final BuildingBlock buildingBlock) {
      TypeOfBuildingBlock tobb = buildingBlock.getBuildingBlockType().getTypeOfBuildingBlock();
      TypeOfFunctionalPermission tofp = getFunctionalPermissionForBuildingBlock(tobb);
      if (!userHasFunctionalPermission(tofp)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
      }
      if (!userHasBbTypeUpdatePermission(tobb)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
      }
      if (!userHasBbInstanceWritePermission(buildingBlock)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
      }
    }

    /**
     * If a user wants to delete a building block, the following permissions are needed:
     * <ul>
     * <li>A functional permission for accessing the management page for the building block type.</li>
     * <li>The delete permission for the building block type.</li>
     * <li>If it exists, an exclusive write permission for the instance.</li>
     * </ul>
     * This method checks all three permissions.
     * 
     * @param buildingBlock
     *          The building block to check.
     * @throws IteraplanBusinessException
     *           If the user does not have any of the above permissions for the given building block
     *           instance.
     */
    public void assureBbDeletePermission(final BuildingBlock buildingBlock) {
      TypeOfBuildingBlock tobb = buildingBlock.getBuildingBlockType().getTypeOfBuildingBlock();
      TypeOfFunctionalPermission tofp = getFunctionalPermissionForBuildingBlock(tobb);
      if (!userHasFunctionalPermission(tofp)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
      }
      if (!userHasBbTypeDeletePermission(tobb)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
      }
      if (!userHasBbInstanceWritePermission(buildingBlock)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
      }
    }

    // The following methods are the main methods for checking for permissions.

    /**
     * @param tofp
     *          The TypeOfFunctionalPermission to check.
     * @throws IteraplanBusinessException
     *           If the user does not have the given functional permission.
     */
    public void assureFunctionalPermission(final TypeOfFunctionalPermission tofp) {
      if (!userHasFunctionalPermission(tofp)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
      }
    }

    /**
     * @param tobb
     *          The {@link TypeOfBuildingBlock} to check.
     * @throws IteraplanBusinessException
     *           If the user does not have the given functional permission.
     */
    public void assureFunctionalPermission(final TypeOfBuildingBlock tobb) {
      this.assureFunctionalPermission(this.getFunctionalPermissionForBuildingBlock(tobb));
    }

    /**
     * @param tobbAsString
     *          The Building Block Type to query, given as a String.
     * @return True if the user has update permission for the given building block type.
     */
    public boolean getUserHasBbTypeUpdatePermission(String tobbAsString) {
      TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(tobbAsString);

      return userHasBbTypeUpdatePermission(tobb);
    }

    /**
     * @param tobbAsString
     *          The Building Block Type to query, given as a String.
     * @return True if the user has create permission for the given building block type.
     */
    public boolean getUserHasBbTypeCreatePermission(String tobbAsString) {
      TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(tobbAsString);

      return userHasBbTypeCreatePermission(tobb);
    }

    /**
     * @param tobbAsString
     *          The Building Block Type to query, given as a String.
     * @return True if the user has delete permission for the given building block type.
     */
    public boolean getUserHasBbTypeDeletePermission(String tobbAsString) {
      TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(tobbAsString);

      return userHasBbTypeDeletePermission(tobb);
    }

    public boolean getUserHasBbTypeFunctionalPermission(String tobbAsString) {
      TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(tobbAsString);

      if (tobb.equals(TypeOfBuildingBlock.BUSINESSMAPPING)) {
        return true;
      }

      return userHasFunctionalPermission(getFunctionalPermissionForBuildingBlock(tobb));
    }

    public boolean getUserHasDialogPermission(String dialog) {
      return (dialog != null) && visibleMenus.contains(dialog);
    }

    // The following are convenience methods for the business logic.

    public boolean getUserHasFuncPermAttributeGroups() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.ATTRIBUTETYPEGROUP);
    }

    public boolean getUserHasFuncPermAttributes() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.ATTRIBUTETYPE);
    }

    public boolean getUserHasFuncPermGrantInstancePerm() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.GRANT_EXPLICIT_PERMISSION);
    }

    public boolean getUserHasFuncPermInstancePerms() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION);
    }

    public boolean getUserHasFuncPermRoles() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.ROLE);
    }

    public boolean getUserHasFuncPermTabReporting() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.TABULAR_REPORTING);
    }

    public boolean getUserHasFuncPermTabReportingCreate() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.TABREPORT_CREATE)
          || userHasFunctionalPermission(TypeOfFunctionalPermission.TABREPORT_FULL);
    }

    public boolean getUserHasFuncPermTabReportingFull() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.TABREPORT_FULL);
    }

    public boolean getUserHasFuncPermGraphReporting() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.GRAPHICAL_REPORTING);
    }

    public boolean getUserHasFuncPermGraphReportingCreate() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.GRAPHREPORT_CREATE)
          || userHasFunctionalPermission(TypeOfFunctionalPermission.GRAPHREPORT_FULL);
    }

    public boolean getUserHasFuncPermGraphReportingFull() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.GRAPHREPORT_FULL);
    }

    public boolean getUserHasFuncPermSupportingQuery() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.SUPPORTING_QUERY);
    }

    public boolean getUserHasFuncPermSearch() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.SEARCH);
    }

    public boolean getUserHasFuncPermTemplates() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.TEMPLATES);
    }

    // the following methods are used by the dashboard to check the permission for the respective building block type
    public boolean getUserHasFuncPermBD() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.BUSINESSDOMAIN);
    }

    public boolean getUserHasFuncPermBP() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.BUSINESSPROCESS);
    }

    public boolean getUserHasFuncPermBM() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.BUSINESSMAPPING);
    }

    public boolean getUserHasFuncPermBF() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.BUSINESSFUNCTION);
    }

    public boolean getUserHasFuncPermPROD() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.PRODUCT);
    }

    public boolean getUserHasFuncPermBU() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.BUSINESSUNIT);
    }

    public boolean getUserHasFuncPermBO() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.BUSINESSOBJECT);
    }

    public boolean getUserHasFuncPermISD() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.INFORMATIONSYSTEMDOMAIN);
    }

    public boolean getUserHasFuncPermIS() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE);
    }

    public boolean getUserHasFuncPermINT() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.INFORMATIONSYSTEMINTERFACE);
    }

    public boolean getUserHasFuncPermAD() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.ARCHITECTURALDOMAIN);
    }

    public boolean getUserHasFuncPermTC() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES);
    }

    public boolean getUserHasFuncPermIE() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.INFRASTRUCTUREELEMENT);
    }

    public boolean getUserHasFuncPermPROJ() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.PROJECT);
    }

    public boolean getUserHasFuncPermMassUpdate() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.MASSUPDATE);
    }

    public boolean getUserHasFuncPermSuccessorReport() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.SUCCESSORREPORT);
    }

    public boolean getUserHasFuncPermConsistencyCheck() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.CONSISTENCY_CHECK);
    }

    public boolean getUserHasFuncPermUserGroups() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.USERGROUP);
    }

    public boolean isUserHasFuncPermDatasources() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.CHANGE_DATASOURCE);
    }

    public boolean isUserHasFuncPermConfiguration() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.CONFIGURATION);
    }

    public boolean getUserHasFuncPermViewHistory() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.VIEW_HISTORY);
    }

    // the following are convenience methods for the GUI

    public boolean getUserHasFuncPermUsers() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.USER);
    }

    public boolean getUserHasFuncPermAuditLog() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.DOWNLOAD_AUDIT_LOG);
    }

    public boolean isUserHasFuncPermXmiDeserialization() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.XMIDESERIALIZATION);
    }

    public boolean isUserHasFuncPermXmiSerialization() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.XMISERIALIZATION);
    }

    public boolean isUserHasFuncPermExcelImport() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.EXCELIMPORT);
    }

    /**
     * @return true if user has permission for any of the menu links contained in eadata.
     */
    public boolean getUserHasPermissionsForMenuEadata() {
      return Iterables.any(FUNCTIONAL_PERM_TYPES_MENU_EADATA, new FunctionalPermissionPredicate());
    }

    /**
     * @return true if user has permission for any of the menu links contained in reports.
     */
    public boolean getUserHasPermissionsForMenuReports() {
      return Iterables.any(FUNCTIONAL_PERM_TYPES_MENU_REPORTS, new FunctionalPermissionPredicate());
    }

    /**
     * @return true if user has permission for any of the menu links contained in visualization.
     */
    public boolean getUserHasPermissionsForMenuVisual() {
      return Iterables.any(FUNCTIONAL_PERM_TYPES_MENU_VISUAL, new FunctionalPermissionPredicate());
    }

    /**
     * @return true if user has permission for any of the menu links contained in mass data.
     */
    public boolean getUserHasPermissionsForMenuMass() {
      return Iterables.any(FUNCTIONAL_PERM_TYPES_MENU_MASS, new FunctionalPermissionPredicate());
    }

    /**
     * @return true if user has permission for any of the menu links contained in governance.
     */
    public boolean getUserHasPermissionsForMenuGovernance() {
      return Iterables.any(FUNCTIONAL_PERM_TYPES_MENU_GOVERNANCE, new FunctionalPermissionPredicate());
    }

    /**
     * @return true if user has permission for any of the menu links contained in administration.
     */
    public boolean getUserHasPermissionsForMenuAdmin() {
      return Iterables.any(FUNCTIONAL_PERM_TYPES_MENU_ADMIN, new FunctionalPermissionPredicate());
    }

    public boolean isUserAllowedToCreateConnections() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.INFORMATIONSYSTEMINTERFACE)
          && userHasBbTypeCreatePermission(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);
    }

    public boolean isUserHasFuncPermSubscription() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.SUBSCRIPTION);
    }

    public boolean isUserHasFuncPermShowSubscribers() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.SHOW_SUBSCRIBERS);
    }

    public boolean isUserHasFuncPermViewHistory() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.VIEW_HISTORY);
    }

    public boolean getUserHasFuncPermDashboard() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.DASHBOARD);
    }

    public boolean getUserHasFuncPermOverview() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.OVERVIEW);
    }

    public boolean isUserHasFuncPermIteraQl() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.ITERAQL);
    }

    public boolean isUserHasFuncPermSeal() {
      return userHasFunctionalPermission(TypeOfFunctionalPermission.SEAL);
    }

    /**
     * is used by JSPs
     * 
     * @return the boolean value of the member flag specifying whether the current user is the
     *         superuser
     */
    public boolean isUserIsAdministrator() {
      return userIsAdministrator;
    }

    /**
     * Checks whether the current user has a permission on an attribute type group. Returns false,
     * if any of the parameter is null.
     * 
     * @param atg
     *          the AttributeTypeGroup
     * @param atgPerm
     *          the permission to be checked against the attribute type group
     * @return true if the user has the specified permission on the attribute type group, false if
     *         not.
     */
    public boolean userHasAttrTypeGroupPermission(final AttributeTypeGroup atg, AttributeTypeGroupPermissionEnum atgPerm) {
      if ((atg == null) || (atgPerm == null)) {
        return false;
      }
      // superuser is allowed to do anything!
      if (this.userIsAdministrator) {
        return true;
      }

      // check for read permissions which we have to check in any case:
      final Set<Integer> neededRoleIds4read = Collections.unmodifiableSet(atg.getRoleIdsWithReadPermissionNotAggregated());
      boolean userHasReadPerms = userHasAttrTypeGroupReadPerm(neededRoleIds4read);

      // So, which permissions was requested to be checked?
      if (atgPerm.equals(AttributeTypeGroupPermissionEnum.READ)) {
        // ok, just READ perms, we are done:
        return userHasReadPerms;
      }

      if (atgPerm.equals(AttributeTypeGroupPermissionEnum.READ_WRITE)) {
        // We have to check whether the user has the permission to change the atg
        // read perms are a prerequisite
        if (!userHasReadPerms) {
          return false;
        }
        else {
          // get the required roles for write perms
          final Set<Integer> neededRoleIds4write = Collections.unmodifiableSet(atg.getRoleIdsWithWritePermissionNotAggregated());
          return userHasAttrTypeGroupWritePerm(neededRoleIds4read, neededRoleIds4write);
        }
      }

      // unknown/undefined AttributeTypeGroupPermissionEnum
      return false;
    }

    private boolean userHasAttrTypeGroupWritePerm(final Set<Integer> neededRoleIds4read, final Set<Integer> neededRoleIds4write) {
      if (neededRoleIds4write.isEmpty()) {
        // Ok, attention please!
        // special case: if nobody has write permissions, but some have a read permission:
        // this does _not_ imply that everyone has write permissions.
        // if there are no role-specific read or write permissions defined, anybody is
        // allowed to do everything on the given atg
        return neededRoleIds4read.isEmpty();
      }
      else {
        // build the intersection of the user's roles and the required roles to write
        Set<Integer> usersRoles = new HashSet<Integer>(this.roleIds);
        usersRoles.retainAll(neededRoleIds4write);

        return !usersRoles.isEmpty();
      }
    }

    private boolean userHasAttrTypeGroupReadPerm(final Set<Integer> neededRoleIds4read) {
      if (neededRoleIds4read.isEmpty()) {
        // there are no specific read permissions, therefore each user is allowed to read it
        return true;
      }
      else {
        // build the intersection of the users roles and the required roles to read the atg
        Set<Integer> usersRoles = new HashSet<Integer>(this.roleIds);
        usersRoles.retainAll(neededRoleIds4read);

        return !usersRoles.isEmpty();
      }
    }

    /**
     * Main method for checking the users permissions for editing and deleting building block
     * instances. All other (convenience) methods should use this method.
     * 
     * @param buildingBlock
     *          The building block to query
     * @return True if the building block either does not belong to any user or user group, the user
     *         is in one of the owning user groups or is one of the owning users or the user is the
     *         system administrator. Returns false when the building block is null.
     */
    public boolean userHasBbInstanceWritePermission(final BuildingBlock buildingBlock) {
      if (buildingBlock == null) {
        return false;
      }
      return userIsPartOfOwningUserEntityIds(buildingBlock.getOwningUserEntityIds());
    }

    /**
     * Main method for checking the users update permissions for building block types. All other
     * (convenience) methods should use this method.
     * 
     * @param tobb
     *          The Building Block Type to query.
     * @return True if the user has update permission for the given building block type.
     */
    public boolean userHasBbTypeUpdatePermission(final TypeOfBuildingBlock tobb) {
      if (this.userIsAdministrator) {
        return true;
      }
      if ((tobb != null) && updateBbtPermissions.contains(tobb)) {
        return true;
      }
      return false;
    }

    /**
     * Main method for checking the users create permissions for building block types. All other
     * (convenience) methods should use this method.
     * 
     * @param tobb
     *          The Building Block Type to query.
     * @return True if the user has create permission for the given building block type.
     */
    public boolean userHasBbTypeCreatePermission(final TypeOfBuildingBlock tobb) {
      if (this.userIsAdministrator) {
        return true;
      }
      if ((tobb != null) && createBbtPermissions.contains(tobb)) {
        return true;
      }
      return false;
    }

    /**
     * Main method for checking the users delete permissions for building block types. All other
     * (convenience) methods should use this method.
     * 
     * @param tobb
     *          The Building Block Type to query.
     * @return True if the user has delete permission for the given building block type.
     */
    public boolean userHasBbTypeDeletePermission(final TypeOfBuildingBlock tobb) {
      if (this.userIsAdministrator) {
        return true;
      }
      if ((tobb != null) && deleteBbtPermissions.contains(tobb)) {
        return true;
      }
      return false;
    }

    /**
     * Main method for checking the user's functional permissions. All other (convenience) methods
     * should use this method.
     * 
     * @param tofp
     *          The functional permission to query.
     * @return true if the user has the functional permission.
     */
    public boolean userHasFunctionalPermission(final TypeOfFunctionalPermission tofp) {
      if (this.userIsAdministrator) {
        return true;
      }
      if ((tofp != null) && functionalPerms.contains(tofp)) {
        return true;
      }
      return false;
    }

    /**
     * Main method for checking the users permissions for editing and deleting building block
     * instances. All other (convenience) methods should use this method.
     * 
     * @param owningUserEntityIds
     *          The set of UserEntity Ids that own a certain building block.
     * @return True if the user is the system administrator, the owningUserEntityIds is empty
     *         (nobody owns the building block), or the user is in one of the owning user groups or
     *         is one of the owning users. Returns false when owningUserEntityIds is null.
     */
    public boolean userIsPartOfOwningUserEntityIds(Set<Integer> owningUserEntityIds) {
      if (owningUserEntityIds == null) {
        return false;
      }
      if (this.userIsAdministrator) {
        return true;
      }
      // if nobody is in the owning user entity set, everybody is considered to be owner.
      if (owningUserEntityIds.isEmpty()) {
        return true;
      }
      Set<Integer> hasUserGroupIds = new HashSet<Integer>(this.userGroupIds);
      hasUserGroupIds.retainAll(owningUserEntityIds);
      boolean userIsInOwningUserGroup = !hasUserGroupIds.isEmpty();
      boolean userOwns = owningUserEntityIds.contains(userId);
      return userIsInOwningUserGroup || userOwns;
    }

    /**
     * @param tobb
     *          A building block type
     * @return The TypeOfFunctionalPermission needed for accessing the management page for the given
     *         building block type.
     */
    public TypeOfFunctionalPermission getFunctionalPermissionForBuildingBlock(TypeOfBuildingBlock tobb) {
      if (FUNCTIONAL_PERMS_FOR_BUILDING_BLOCKS.containsKey(tobb)) {
        return FUNCTIONAL_PERMS_FOR_BUILDING_BLOCKS.get(tobb);
      }
      else {
        return null;
      }
    }

    private void initPermissionsBbType(Role role) {
      for (BuildingBlockType bbt : role.getBbtForPermissionType(EditPermissionType.UPDATE)) {
        updateBbtPermissions.add(bbt.getTypeOfBuildingBlock());
      }
      for (BuildingBlockType bbt : role.getBbtForPermissionType(EditPermissionType.CREATE)) {
        createBbtPermissions.add(bbt.getTypeOfBuildingBlock());
      }
      for (BuildingBlockType bbt : role.getBbtForPermissionType(EditPermissionType.DELETE)) {
        deleteBbtPermissions.add(bbt.getTypeOfBuildingBlock());
      }
    }

    private void initPermissionsFunctional(Role role) {
      for (PermissionFunctional pf : role.getPermissionsFunctional()) {
        functionalPerms.add(pf.getTypeOfFunctionalPermission());
      }
    }

    private void initVisibleMenusAndReadableBbTypes() {

      Set<TypeOfFunctionalPermission> permissionsWithMappedClasses = TypeOfFunctionalPermission.getAllPermissionsMappedToClasses();

      for (TypeOfFunctionalPermission perm : permissionsWithMappedClasses) {

        if (userHasFunctionalPermission(perm)) {
          Dialog dialog = Dialog.getDialogForClass(perm.getClassForPermission());
          if (dialog != null) {
            visibleMenus.add(dialog.getDialogName());
          }
        }
      }

      for (Entry<TypeOfFunctionalPermission, Set<Dialog>> entry : PermissionFunctional.MAP_PERMISSION_TO_DIALOG.entrySet()) {
        if (userHasFunctionalPermission(entry.getKey())) {
          for (Dialog dialog : entry.getValue()) {
            visibleMenus.add(dialog.getDialogName());
          }
        }
      }
    }

    /**
     * This predicate class determines a true or false value for each {@link TypeOfFunctionalPermission}-input,
     * depending on whether or not the user has this type of permission
     */
    private final class FunctionalPermissionPredicate implements Predicate<TypeOfFunctionalPermission> {
      public boolean apply(TypeOfFunctionalPermission input) {
        return userHasFunctionalPermission(input);
      }
    }

  }

}
