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
package de.iteratec.iteraplan.presentation.dialog.UserGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.ElementNameComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetReadOnlyComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


/**
 * GUI model for the User management page.
 */
public class UserGroupComponentModel extends AbstractComponentModelBase<UserGroup> {

  /** Serialization version. */
  private static final long                                                        serialVersionUID                  = -6065932412433099372L;
  // user constants
  private static final String                                                      NAME_LABEL_KEY                    = "global.name";
  private static final String                                                      DESCRIPTION_LABEL_KEY             = "global.description";

  private static final String                                                      SUPER_USER_GROUP_TABLE_HEADER_KEY = "manageUserGroup.subUserGroups";
  private static final String                                                      GROUP_NAME_PROPERTY               = "name";
  private static final String                                                      GROUP_DESCRIPTION_PROPERTY        = "description";

  private static final String                                                      USER_TABLE_HEADER_KEY             = "manageUserGroup.users";
  private static final String                                                      USER_LOGINNAME_LABEL_KEY          = "manageUser.loginName";
  private static final String                                                      USER_LOGINNAME_PROPERTY           = "loginName";
  private static final String                                                      USER_FIRSTNAME_LABEL_KEY          = "manageUser.firstName";
  private static final String                                                      USER_FIRSTNAME_PROPERTY           = "firstName";
  private static final String                                                      USER_LASTNAME_LABEL_KEY           = "manageUser.lastName";
  private static final String                                                      USER_LASTNAME_PROPERTY            = "lastName";

  private final StringComponentModel<UserGroup>                                    nameModel;

  private final StringComponentModel<UserGroup>                                    descriptionModel;

  private final ManyAssociationSetComponentModel<UserGroup, UserGroup>             subUserGroupModel;

  private final ManyAssociationSetComponentModel<UserGroup, User>                  userModel;

  private final ManyAssociationSetReadOnlyComponentModel<UserGroup, UserGroup>     aggregatedSuperUserGroupModel;

  private final ManyAssociationSetReadOnlyComponentModel<UserGroup, BuildingBlock> aggregatedBuildingBlockModel;

  /**
   * @see UserGroup#getLastModificationUser()
   */
  private String                                                                   lastModificationUser              = "";

  /**
   * @see UserGroup#getLastModificationTime()
   */
  private Date                                                                     lastModificationTime              = null;

  private UserGroup                                                                userGroup;

  /**
   * Initialization is not done here but in initializeFrom(Object), as the model instance may not be
   * known at this point.
   */
  public UserGroupComponentModel(ComponentMode componentMode) {
    super(componentMode);
    setHtmlId("usergroup");

    nameModel = new NameCM(componentMode, "name", NAME_LABEL_KEY);
    descriptionModel = new DescriptionCM(componentMode, "description", DESCRIPTION_LABEL_KEY, false);
    subUserGroupModel = new SubUserGroupCM(componentMode, "subUserGroups", SUPER_USER_GROUP_TABLE_HEADER_KEY, new String[] { NAME_LABEL_KEY,
        DESCRIPTION_LABEL_KEY }, new String[] { GROUP_NAME_PROPERTY, GROUP_DESCRIPTION_PROPERTY }, GROUP_NAME_PROPERTY, new UserGroup());

    userModel = new UserCM(componentMode, "users", USER_TABLE_HEADER_KEY, new String[] { USER_LOGINNAME_LABEL_KEY, USER_FIRSTNAME_LABEL_KEY,
        USER_LASTNAME_LABEL_KEY }, new String[] { USER_LOGINNAME_PROPERTY, USER_FIRSTNAME_PROPERTY, USER_LASTNAME_PROPERTY },
        USER_LOGINNAME_PROPERTY, new User());

    aggregatedSuperUserGroupModel = new AggregatedSuperUserGroupCM(componentMode, "aggregatedSuperUserGroups",
        "manageUserGroup.superUserGroups.aggregated", new String[] { NAME_LABEL_KEY, DESCRIPTION_LABEL_KEY }, new String[] { GROUP_NAME_PROPERTY,
            GROUP_DESCRIPTION_PROPERTY }, null);

    aggregatedBuildingBlockModel = new AggregatedBuildingBlockCM(componentMode, "aggregatedBuildingBlocks",
        "objectRelatedPermissions.hasExplicitPermissions.aggregated", new String[] { "global.bbtype", NAME_LABEL_KEY }, new String[] {
            "buildingBlockType.typeOfBuildingBlock.value", "identityString" }, new Boolean[] { Boolean.TRUE, Boolean.FALSE });
  }

  public void initializeFrom(UserGroup source) {
    nameModel.initializeFrom(source);
    descriptionModel.initializeFrom(source);
    subUserGroupModel.initializeFrom(source);
    userModel.initializeFrom(source);
    aggregatedSuperUserGroupModel.initializeFrom(source);
    aggregatedBuildingBlockModel.initializeFrom(source);
    this.lastModificationTime = source.getLastModificationTime();
    this.lastModificationUser = source.getLastModificationUser();
  }

  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      nameModel.update();
      descriptionModel.update();
      subUserGroupModel.update();
      userModel.update();
    }
  }

  public void configure(UserGroup target) {
    nameModel.configure(target);
    descriptionModel.configure(target);
    subUserGroupModel.configure(target);
    userModel.configure(target);
  }

  public void validate(Errors errors) {
    // check for non-empty name
    errors.pushNestedPath("nameModel");
    nameModel.validate(errors);
    errors.popNestedPath();

    errors.pushNestedPath("descriptionModel");
    descriptionModel.validateDescription(errors);
    errors.popNestedPath();
  }

  public StringComponentModel<UserGroup> getDescriptionModel() {
    return descriptionModel;
  }

  public StringComponentModel<UserGroup> getNameModel() {
    return nameModel;
  }

  public ManyAssociationSetComponentModel<UserGroup, User> getUserModel() {
    return userModel;
  }

  public ManyAssociationSetReadOnlyComponentModel<UserGroup, BuildingBlock> getAggregatedBuildingBlockModel() {
    return aggregatedBuildingBlockModel;
  }

  public ManyAssociationSetReadOnlyComponentModel<UserGroup, UserGroup> getAggregatedSuperUserGroupModel() {
    return aggregatedSuperUserGroupModel;
  }

  public ManyAssociationSetComponentModel<UserGroup, UserGroup> getSubUserGroupModel() {
    return subUserGroupModel;
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

  private static final class NameCM extends ElementNameComponentModel<UserGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = -3993868406521480691L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(UserGroup target, String stringToSet) {
      target.setName(stringToSet);
    }

    @Override
    public String getStringFromElement(UserGroup source) {
      return source.getName();
    }
  }

  private static final class DescriptionCM extends StringComponentModel<UserGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = -1052742891305677062L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey, boolean mandatory) {
      super(componentMode, htmlId, labelKey, mandatory);
    }

    @Override
    public void setStringForElement(UserGroup target, String stringToSet) {
      target.setDescription(stringToSet);
    }

    @Override
    public String getStringFromElement(UserGroup source) {
      return source.getDescription();
    }
  }

  private static final class SubUserGroupCM extends ManyAssociationSetComponentModel<UserGroup, UserGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = 1347859654761061286L;

    public SubUserGroupCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, UserGroup dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected List<UserGroup> getAvailableElements(Integer id, List<UserGroup> connected) {
      List<UserGroup> availableUserGroupsToTransferPermissions = SpringServiceFactory.getUserGroupService()
          .getAvailableUserGroupsToTransferPermissions(id, connected);

      //initiliaze UserGroup.members because of lazy init
      for (UserGroup userGroup : availableUserGroupsToTransferPermissions) {
        Hibernate.initialize(userGroup.getMembers());
      }

      return availableUserGroupsToTransferPermissions;
    }

    @Override
    protected Set<UserGroup> getConnectedElements(UserGroup source) {
      return source.findUserGroupsInUserGroup();
    }

    @Override
    protected void setConnectedElements(UserGroup target, Set<UserGroup> toConnect) {
      Set<UserGroup> userGroupsInGroup = target.findUserGroupsInUserGroup();
      target.getMembers().removeAll(userGroupsInGroup);
      target.getMembers().addAll(toConnect);
    }
  }

  private static final class UserCM extends ManyAssociationSetComponentModel<UserGroup, User> {
    /** Serialization version. */
    private static final long serialVersionUID = 277680677996837321L;

    public UserCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, User dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected List<User> getAvailableElements(Integer id, List<User> connected) {
      return SpringServiceFactory.getUserService().getUsersFiltered(null, connected);
    }

    @Override
    protected Set<User> getConnectedElements(UserGroup source) {
      return source.findUsersInUserGroup();
    }

    @Override
    protected void setConnectedElements(UserGroup target, Set<User> toConnect) {
      Set<User> usersInGroup = target.findUsersInUserGroup();
      target.getMembers().removeAll(usersInGroup);
      target.getMembers().addAll(toConnect);
    }
  }

  private static final class AggregatedSuperUserGroupCM extends ManyAssociationSetReadOnlyComponentModel<UserGroup, UserGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = 1332227567401431688L;

    public AggregatedSuperUserGroupCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, Boolean[] lookupLablesMode) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, lookupLablesMode);
    }

    @Override
    protected List<UserGroup> getConnectedElementsToDisplay(UserGroup source) {
      List<UserGroup> ugs = new ArrayList<UserGroup>(source.getUserGroupHierarchy());
      Collections.sort(ugs);
      return ugs;
    }
  }

  private static final class AggregatedBuildingBlockCM extends ManyAssociationSetReadOnlyComponentModel<UserGroup, BuildingBlock> {
    /** Serialization version. */
    private static final long serialVersionUID = -2059620569163136814L;

    public AggregatedBuildingBlockCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, Boolean[] lookupLablesMode) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, lookupLablesMode);
    }

    @Override
    protected List<BuildingBlock> getConnectedElementsToDisplay(UserGroup source) {
      return SpringServiceFactory.getUserEntityService().loadOwnedBuildingBlocks(source, true);
    }
  }

  public UserGroup getUserGroup() {
    return userGroup;
  }

  public void setUserGroup(UserGroup userGroup) {
    this.userGroup = userGroup;
  }

}
