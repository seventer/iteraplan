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
package de.iteratec.iteraplan.presentation.dialog.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.ElementNameComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetReadOnlyComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.RoutingDatasourceComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


/**
 * GUI model for {@link User}s.
 */
public class UserComponentModel extends AbstractComponentModelBase<User> {

  /** Serialization version. */
  private static final long                                                   serialVersionUID            = -8716314754356344319L;
  // user constants
  private static final String                                                 LOGIN_NAME_LABEL            = "manageUser.loginName";
  private static final String                                                 FIRST_NAME_LABEL            = "manageUser.firstName";
  private static final String                                                 LAST_NAME_LABEL             = "manageUser.lastName";
  private static final String                                                 EMAIL_LABEL                 = "manageUser.email";

  // user group constants
  private static final String                                                 NAME_LABEL_KEY              = "global.name";
  private static final String                                                 DESCRIPTION_LABEL_KEY       = "global.description";
  private static final String                                                 GROUP_NAME_PROPERTY         = "name";
  private static final String                                                 GROUP_DESCRIPTION_PROPERTY  = "description";
  private static final String                                                 USER_GROUP_TABLE_HEADER_KEY = "manageUser.assignedUserGroups";

  private final StringComponentModel<User>                                    loginNameModel;

  private final StringComponentModel<User>                                    firstNameModel;

  private final StringComponentModel<User>                                    lastNameModel;

  private final StringComponentModel<User>                                    emailModel;

  private final ManyAssociationSetComponentModel<User, UserGroup>             userGroupModel;

  private final ManyAssociationSetReadOnlyComponentModel<User, UserGroup>     aggregatedUserGroupModel;

  private final ManyAssociationSetReadOnlyComponentModel<User, BuildingBlock> aggregatedBuildingBlockModel;

  private final RoutingDatasourceComponentModel                               routingDatasourceModel;

  private String                                                              lastModificationUser        = "";

  private Date                                                                lastModificationTime        = null;

  private String                                                              selectedLoginName;

  private User                                                                user;

  public UserComponentModel(ComponentMode componentMode) {
    super(componentMode);
    setHtmlId("user");

    loginNameModel = new LoginNameCM(componentMode, "loginName", LOGIN_NAME_LABEL);
    firstNameModel = new FirstNameCM(componentMode, "firstName", FIRST_NAME_LABEL, true);
    lastNameModel = new LastNameCM(componentMode, "lastName", LAST_NAME_LABEL, true);
    emailModel = new EmailCM(componentMode, "email", EMAIL_LABEL, false);

    userGroupModel = new UserGroupCM(componentMode, "userGroups", USER_GROUP_TABLE_HEADER_KEY,
        new String[] { NAME_LABEL_KEY, DESCRIPTION_LABEL_KEY }, new String[] { GROUP_NAME_PROPERTY, GROUP_DESCRIPTION_PROPERTY },
        GROUP_NAME_PROPERTY, new UserGroup());

    aggregatedUserGroupModel = new AggregatedUserGroupCM(componentMode, "aggregatedUserGroups", "manageUser.assignedUserGroupsAggregated",
        new String[] { "global.name", "global.description" }, new String[] { "name", "description" }, null);

    aggregatedBuildingBlockModel = new AggregatedBuildingBlockCM(componentMode, "aggregatedBuildingBlocks",
        "objectRelatedPermissions.hasExplicitPermissions.aggregated", new String[] { "global.bbtype", "global.name" }, new String[] {
            "buildingBlockType.typeOfBuildingBlock.value", "identityString" }, new Boolean[] { Boolean.TRUE, Boolean.FALSE });

    routingDatasourceModel = new RoutingDatasourceComponentModel(componentMode);
  }

  public void initializeFrom(User source) {
    selectedLoginName = source.getLoginName();

    loginNameModel.initializeFrom(source);
    firstNameModel.initializeFrom(source);
    lastNameModel.initializeFrom(source);
    emailModel.initializeFrom(source);
    userGroupModel.initializeFrom(source);
    aggregatedUserGroupModel.initializeFrom(source);
    aggregatedBuildingBlockModel.initializeFrom(source);
    routingDatasourceModel.initializeFrom(source);
    lastModificationTime = source.getLastModificationTime();
    lastModificationUser = source.getLastModificationUser();
  }

  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      loginNameModel.update();
      firstNameModel.update();
      lastNameModel.update();
      emailModel.update();
      userGroupModel.update();
    }
  }

  public void configure(User target) {
    loginNameModel.configure(target);
    firstNameModel.configure(target);
    lastNameModel.configure(target);
    emailModel.configure(target);
    userGroupModel.configure(target);
    routingDatasourceModel.configure(target);
  }

  public void validate(Errors errors) {
    // check for non-empty names
    errors.pushNestedPath("loginNameModel");
    loginNameModel.validate(errors, "current", new String[] { "manageUser.loginName" });
    errors.popNestedPath();
    errors.pushNestedPath("firstNameModel");
    firstNameModel.validate(errors, "current", new String[] { "manageUser.firstName" });
    errors.popNestedPath();
    errors.pushNestedPath("lastNameModel");
    lastNameModel.validate(errors, "current", new String[] { "manageUser.lastName" });
    errors.popNestedPath();
  }

  public StringComponentModel<User> getFirstNameModel() {
    return firstNameModel;
  }

  public StringComponentModel<User> getLastNameModel() {
    return lastNameModel;
  }

  public StringComponentModel<User> getLoginNameModel() {
    return loginNameModel;
  }

  public StringComponentModel<User> getEmailModel() {
    return emailModel;
  }

  public ManyAssociationSetComponentModel<User, UserGroup> getUserGroupModel() {
    return userGroupModel;
  }

  public ManyAssociationSetReadOnlyComponentModel<User, UserGroup> getAggregatedUserGroupModel() {
    return aggregatedUserGroupModel;
  }

  public ManyAssociationSetReadOnlyComponentModel<User, BuildingBlock> getAggregatedBuildingBlockModel() {
    return aggregatedBuildingBlockModel;
  }

  public Date getLastModificationTime() {
    return lastModificationTime;
  }

  public String getLastModificationUser() {
    return lastModificationUser;
  }

  public User getLastModificationUserByLoginName() {
    return SpringServiceFactory.getUserService().getUserByLoginIfExists(getLastModificationUser());
  }

  public RoutingDatasourceComponentModel getRoutingDatasourceModel() {
    return routingDatasourceModel;
  }

  public boolean isLoggedInUserSelected() {

    // In case a new user is created
    if (selectedLoginName == null) {
      return false;
    }
    else {
      return selectedLoginName.equalsIgnoreCase(UserContext.getCurrentUserContext().getLoginName());
    }
  }

  private static final class LoginNameCM extends ElementNameComponentModel<User> {
    /** Serialization version. */
    private static final long serialVersionUID = 3781360385580921399L;

    public LoginNameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(User target, String stringToSet) {
      target.setLoginName(stringToSet);
    }

    @Override
    public String getStringFromElement(User source) {
      return source.getLoginName();
    }
  }

  private static final class FirstNameCM extends StringComponentModel<User> {
    /** Serialization version. */
    private static final long serialVersionUID = 9068870047960911810L;

    public FirstNameCM(ComponentMode componentMode, String htmlId, String labelKey, boolean mandatory) {
      super(componentMode, htmlId, labelKey, mandatory);
    }

    @Override
    public void setStringForElement(User target, String stringToSet) {
      target.setFirstName(stringToSet);
    }

    @Override
    public String getStringFromElement(User source) {
      return source.getFirstName();
    }
  }

  private static final class LastNameCM extends StringComponentModel<User> {
    /** Serialization version. */
    private static final long serialVersionUID = 1830753953894159729L;

    public LastNameCM(ComponentMode componentMode, String htmlId, String labelKey, boolean mandatory) {
      super(componentMode, htmlId, labelKey, mandatory);
    }

    @Override
    public void setStringForElement(User target, String stringToSet) {
      target.setLastName(stringToSet);
    }

    @Override
    public String getStringFromElement(User source) {
      return source.getLastName();
    }
  }

  private static final class EmailCM extends StringComponentModel<User> {
    /** Serialization version. */
    private static final long serialVersionUID = -4237457738920080510L;

    public EmailCM(ComponentMode componentMode, String htmlId, String labelKey, boolean mandatory) {
      super(componentMode, htmlId, labelKey, mandatory);
    }

    @Override
    public void setStringForElement(User target, String stringToSet) {
      target.setEmail(stringToSet);
    }

    @Override
    public String getStringFromElement(User source) {
      return source.getEmail();
    }
  }

  private static final class UserGroupCM extends ManyAssociationSetComponentModel<User, UserGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = 1691773617656020224L;

    public UserGroupCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, UserGroup dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected List<UserGroup> getAvailableElements(Integer id, List<UserGroup> connected) {
      return SpringServiceFactory.getUserGroupService().getUserGroupsFiltered(null, connected);
    }

    @Override
    protected Set<UserGroup> getConnectedElements(User source) {
      return source.getParentUserGroups();
    }

    @Override
    protected void setConnectedElements(User target, Set<UserGroup> referenceObjects) {
      List<UserGroup> reloadedUserGroups = SpringServiceFactory.getUserGroupService().reload(referenceObjects);

      target.removeParentUserGroups();
      target.addParentUserGroups(reloadedUserGroups);
    }
  }

  private static final class AggregatedUserGroupCM extends ManyAssociationSetReadOnlyComponentModel<User, UserGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = 2928659817480153952L;

    public AggregatedUserGroupCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, Boolean[] lookupLablesMode) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, lookupLablesMode);
    }

    @Override
    protected List<UserGroup> getConnectedElementsToDisplay(User source) {
      List<UserGroup> ugs = new ArrayList<UserGroup>(source.getUserGroupHierarchy());
      Collections.sort(ugs);
      return ugs;
    }
  }

  private static final class AggregatedBuildingBlockCM extends ManyAssociationSetReadOnlyComponentModel<User, BuildingBlock> {
    /** Serialization version. */
    private static final long serialVersionUID = -5991115174486616447L;

    public AggregatedBuildingBlockCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, Boolean[] lookupLablesMode) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, lookupLablesMode);
    }

    @Override
    protected List<BuildingBlock> getConnectedElementsToDisplay(User source) {
      return SpringServiceFactory.getUserEntityService().loadOwnedBuildingBlocks(source, true);
    }
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

}