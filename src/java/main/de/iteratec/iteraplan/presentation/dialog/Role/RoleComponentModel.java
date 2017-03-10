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
package de.iteratec.iteraplan.presentation.dialog.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.dto.PermissionAttrTypeGroupDTO;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.dialog.Role.model.PermissionMatrixComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.ElementNameComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetReadOnlyComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


/**
 * GUI model for the Role management page.
 */
public class RoleComponentModel extends AbstractComponentModelBase<Role> {

  /** Serialization version. */
  private static final long                                                       serialVersionUID                            = 1L;
  // Role constants
  private static final String                                                     NAME_LABEL_KEY                              = "global.name";
  private static final String                                                     DESCRIPTION_LABEL_KEY                       = "global.description";

  private static final String                                                     SUB_ROLE_TABLE_HEADER_KEY                   = "manageRoles.assigned_roles";
  private static final String                                                     ROLE_NAME_PROPERTY                          = "roleName";
  private static final String                                                     ROLE_DESCRIPTION_PROPERTY                   = "description";

  private static final String                                                     FUNC_PERMISSION_HEADER_KEY                  = "manageRoles.permissionsFunctional";
  private static final String                                                     FUNC_PERMISSION_NAME_PROPERTY               = "typeOfFunctionalPermission.value";

  private static final String                                                     BB_TYPE_TABLE_HEADER_KEY                    = "manageRoles.permissionsBbType";

  private static final String                                                     SUB_ROLE_AGGREGATED_TABLE_HEADER_KEY        = "manageRoles.assigned_roles.aggregated";
  private static final String                                                     FUNC_PERMISSION_AGGREGATED_TABLE_HEADER_KEY = "manageRoles.permissionsFunctional.aggregated";
  private static final String                                                     BB_TYPE_AGGREGATED_TABLE_HEADER_KEY         = "manageRoles.permissionsBbType.aggregated";
  private static final String                                                     PERMISSION_ATG_AGGREGATED_TABLE_HEADER_KEY  = "manageRoles.permissionsAttrGroup.aggregated";

  /** The id of the managed role. */
  private Integer                                                                 managedId;

  /** CM for Role name property. */
  private ElementNameComponentModel<Role>                                         nameModel;

  /** CM for Role description property. */
  private StringComponentModel<Role>                                              descriptionModel;

  /** CM for associated sub-Roles. */
  private ManyAssociationSetComponentModel<Role, Role>                            subRoleModel;

  /** CM for associated {@link PermissionFunctional}s. */
  private ManyAssociationSetComponentModel<Role, PermissionFunctional>            functionalPermissionsModel;

  /** CM for associated {@link BuildingBlockType}s. */
  private PermissionMatrixComponentModel                                          bbTypeModel;

  /** CM for associated {@link PermissionAttrTypeGroup}s. */
  private PermissionAttributeTypeGroupComponentModel<Role>                        permissionAtgModel;

  /** CM for aggregated sub-roles (read-only). */
  private ManyAssociationSetReadOnlyComponentModel<Role, Role>                    aggregatedSubRoleModel;

  /** CM for aggregated functional permissions (read-only). */
  private ManyAssociationSetReadOnlyComponentModel<Role, PermissionFunctional>    aggregatedFunctionalPermissionModel;

  /** CM for aggregated functional permissions (read-only). */
  private PermissionMatrixComponentModel                                          aggregatedBbTypeModel;

  /** CM for aggregated permissions for attribute type groups (read-only). */
  private ManyAssociationSetReadOnlyComponentModel<Role, PermissionAttrTypeGroup> aggregatedPermissionAtgModel;

  /** @see Role#getLastModificationUser() */
  private String                                                                  lastModificationUser                        = "";

  /** @see Role#getLastModificationTime() */
  private Date                                                                    lastModificationTime                        = null;

  private Role                                                                    role;

  /**
   * Initialization is not done here but in initializeFrom(Object), as the model instance may not be
   * known at this point.
   */
  public RoleComponentModel(ComponentMode componentMode) {
    super(componentMode);
    setHtmlId("role");

  }

  public void initializeFrom(Role roleParam) {
    this.managedId = roleParam.getId();
    getNameModel().initializeFrom(roleParam);
    getDescriptionModel().initializeFrom(roleParam);
    getSubRoleModel().initializeFrom(roleParam);
    getFunctionalPermissionsModel().initializeFrom(roleParam);
    getBbTypeModel().initializeFrom(roleParam);
    getPermissionAtgModel().initializeFrom(roleParam);
    getAggregatedSubRoleModel().initializeFrom(roleParam);
    getAggregatedFunctionalPermissionModel().initializeFrom(roleParam);
    getAggregatedBbTypeModel().initializeFrom(roleParam);
    getAggregatedPermissionAtgModel().initializeFrom(roleParam);
    this.lastModificationTime = roleParam.getLastModificationTime();
    this.lastModificationUser = roleParam.getLastModificationUser();
  }

  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      getNameModel().update();
      getDescriptionModel().update();
      getSubRoleModel().update();
      getFunctionalPermissionsModel().update();
      getBbTypeModel().update();
      getPermissionAtgModel().update();
    }
  }

  public void configure(Role roleParam) {
    getNameModel().configure(roleParam);
    getDescriptionModel().configure(roleParam);
    getSubRoleModel().configure(roleParam);
    getFunctionalPermissionsModel().configure(roleParam);
    getBbTypeModel().configure(roleParam);
    getPermissionAtgModel().configure(roleParam);
  }

  public void validate(Errors errors) {
    // check for non-empty name
    errors.pushNestedPath("nameModel");
    getNameModel().validate(errors);
    errors.popNestedPath();

    errors.pushNestedPath("descriptionModel");
    descriptionModel.validateDescription(errors);
    errors.popNestedPath();
  }

  public final StringComponentModel<Role> getDescriptionModel() {
    if (descriptionModel == null) {
      descriptionModel = new DescriptionCM(getComponentMode(), "description", DESCRIPTION_LABEL_KEY, false);
    }
    return descriptionModel;
  }

  public final StringComponentModel<Role> getNameModel() {
    if (nameModel == null) {
      nameModel = new NameElementNameCM(getComponentMode(), "name", NAME_LABEL_KEY);
    }
    return nameModel;
  }

  public final ManyAssociationSetComponentModel<Role, Role> getSubRoleModel() {
    if (subRoleModel == null) {
      subRoleModel = new SubRoleCM(getComponentMode(), "subRoles", SUB_ROLE_TABLE_HEADER_KEY, new String[] { NAME_LABEL_KEY, DESCRIPTION_LABEL_KEY },
          new String[] { ROLE_NAME_PROPERTY, ROLE_DESCRIPTION_PROPERTY }, ROLE_NAME_PROPERTY, new Role());
    }
    return subRoleModel;
  }

  public final ManyAssociationSetComponentModel<Role, PermissionFunctional> getFunctionalPermissionsModel() {
    if (functionalPermissionsModel == null) {
      functionalPermissionsModel = new FunctionalPermissionsCM(getComponentMode(), "functionalPermissions", FUNC_PERMISSION_HEADER_KEY,
          new String[] { NAME_LABEL_KEY }, new String[] { FUNC_PERMISSION_NAME_PROPERTY }, FUNC_PERMISSION_NAME_PROPERTY, new PermissionFunctional(
              TypeOfFunctionalPermission.DUMMY), new Boolean[] { Boolean.TRUE }, Boolean.TRUE, null);
    }
    return functionalPermissionsModel;
  }

  public final PermissionMatrixComponentModel getBbTypeModel() {
    if (bbTypeModel == null) {
      bbTypeModel = new BbTypeCM(getComponentMode(), "bbTypes", BB_TYPE_TABLE_HEADER_KEY, new String[] { "manageRoles.permission.read",
          "manageRoles.permission.update", "manageRoles.permission.create", "manageRoles.permission.delete" });
    }
    return bbTypeModel;
  }

  public final PermissionAttributeTypeGroupComponentModel<Role> getPermissionAtgModel() {
    if (permissionAtgModel == null) {
      permissionAtgModel = new PermissionAtgPermissionAttributeTypeGroupCM(getComponentMode(), true);
    }
    return permissionAtgModel;
  }

  public final ManyAssociationSetReadOnlyComponentModel<Role, Role> getAggregatedSubRoleModel() {
    if (aggregatedSubRoleModel == null) {
      aggregatedSubRoleModel = new AggregatedSubRoleCM(getComponentMode(), "aggregatedRoles", SUB_ROLE_AGGREGATED_TABLE_HEADER_KEY, new String[] {
          NAME_LABEL_KEY, DESCRIPTION_LABEL_KEY }, new String[] { ROLE_NAME_PROPERTY, ROLE_DESCRIPTION_PROPERTY }, null);
    }
    return aggregatedSubRoleModel;
  }

  public final ManyAssociationSetReadOnlyComponentModel<Role, PermissionFunctional> getAggregatedFunctionalPermissionModel() {
    if (aggregatedFunctionalPermissionModel == null) {
      aggregatedFunctionalPermissionModel = new AggregatedFunctionalPermissionCM(getComponentMode(), "aggregatedFunctionalPermissions",
          FUNC_PERMISSION_AGGREGATED_TABLE_HEADER_KEY, new String[] { NAME_LABEL_KEY }, new String[] { FUNC_PERMISSION_NAME_PROPERTY },
          new Boolean[] { Boolean.TRUE });
    }
    return aggregatedFunctionalPermissionModel;
  }

  public final PermissionMatrixComponentModel getAggregatedBbTypeModel() {
    if (aggregatedBbTypeModel == null) {
      aggregatedBbTypeModel = new AggregatedBbTypeCM(getComponentMode(), "aggregatedBbType", BB_TYPE_AGGREGATED_TABLE_HEADER_KEY, new String[] {
          "manageRoles.permission.read", "manageRoles.permission.update", "manageRoles.permission.create", "manageRoles.permission.delete" });
    }
    return aggregatedBbTypeModel;
  }

  public final ManyAssociationSetReadOnlyComponentModel<Role, PermissionAttrTypeGroup> getAggregatedPermissionAtgModel() {
    if (aggregatedPermissionAtgModel == null) {
      aggregatedPermissionAtgModel = new AggregatedPermissionAtgCM(getComponentMode(), "aggregatedPermissionAtgs",
          PERMISSION_ATG_AGGREGATED_TABLE_HEADER_KEY, new String[] { NAME_LABEL_KEY, "manageRoles.setPermissions" }, new String[] {
              "attrTypeGroup.name", "permissionKey" }, new Boolean[] { Boolean.FALSE, Boolean.TRUE });
    }
    return aggregatedPermissionAtgModel;
  }

  public Integer getManagedId() {
    return managedId;
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

  private static final class SubRoleCM extends ManyAssociationSetComponentModel<Role, Role> {
    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    public SubRoleCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys, String[] connectedElementsFields,
        String availableElementsLabel, Role dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected List<Role> getAvailableElements(Integer id, List<Role> connected) {
      return SpringServiceFactory.getRoleService().getAvailableRoles(id, connected);
    }

    @Override
    protected Set<Role> getConnectedElements(Role source) {
      return source.getConsistsOfRoles();
    }

    @Override
    protected void setConnectedElements(Role target, Set<Role> referenceObjects) {
      final List<Role> reloadedSubRoles = SpringServiceFactory.getRoleService().reload(referenceObjects);

      target.removeConsistsOfRoles();
      target.addConsistsOfRoles(reloadedSubRoles);
    }
  }

  private static final class FunctionalPermissionsCM extends ManyAssociationSetComponentModel<Role, PermissionFunctional> {
    /** Serialization version. */
    private static final long serialVersionUID = -2666953215976050934L;

    public FunctionalPermissionsCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, PermissionFunctional dummyForPresentation, Boolean[] lookupLablesMode,
        Boolean lookupAvailableLablesMode, String[] availableElementsPresentationGroupKeys) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation,
          lookupLablesMode, lookupAvailableLablesMode, availableElementsPresentationGroupKeys);
    }

    @Override
    protected List<PermissionFunctional> getAvailableElements(Integer id, List<PermissionFunctional> connected) {
      List<PermissionFunctional> availableFunctionalPermissions = Lists.newArrayList(SpringServiceFactory.getRoleService()
          .getAvailableFunctionalPermissions(connected));
      removeBuildingBlockPermissions(availableFunctionalPermissions);
      return availableFunctionalPermissions;
    }

    @Override
    protected Set<PermissionFunctional> getConnectedElements(Role source) {
      Set<PermissionFunctional> permissionsFunctional = Sets.newHashSet(source.getPermissionsFunctional());
      removeBuildingBlockPermissions(permissionsFunctional);
      return permissionsFunctional;
    }

    private void removeBuildingBlockPermissions(Iterable<PermissionFunctional> permissionsFunctional) {
      for (Iterator<PermissionFunctional> it = permissionsFunctional.iterator(); it.hasNext();) {
        PermissionFunctional perm = it.next();
        if (TypeOfBuildingBlock.typeOfBuildingBlockForClass(perm.getTypeOfFunctionalPermission().getClassForPermission()) != null) {
          it.remove();
        }
      }
    }

    @Override
    protected void setConnectedElements(Role target, Set<PermissionFunctional> referenceObjects) {
      List<PermissionFunctional> reloadFPs = SpringServiceFactory.getRoleService().reloadFunctionalPermissions(referenceObjects);

      target.removePermissionFunctionals();
      target.addPermissionFunctionals(reloadFPs);
    }
  }

  private static final class BbTypeCM extends PermissionMatrixComponentModel {

    /** Serialization version. */
    private static final long serialVersionUID = 1069717821439104059L;

    public BbTypeCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, false);
    }

    /**{@inheritDoc}**/
    @Override
    public List<BuildingBlockType> getAvailableBuildingBlockTypes() {
      return SpringServiceFactory.getRoleService().getAvailableBuildingBlockTypes(null);
    }

    /**{@inheritDoc}**/
    @Override
    public List<PermissionFunctional> getAvailablePermissionFunctional() {
      return SpringServiceFactory.getRoleService().getAvailableFunctionalPermissions(null);
    }

  }

  private static final class PermissionAtgPermissionAttributeTypeGroupCM extends PermissionAttributeTypeGroupComponentModel<Role> {
    /** Serialization version. */
    private static final long serialVersionUID = -6592443614053512009L;

    public PermissionAtgPermissionAttributeTypeGroupCM(ComponentMode componentMode, boolean manageFromRoleSide) {
      super(componentMode, manageFromRoleSide);
    }

    @Override
    protected List<PermissionAttrTypeGroupDTO> getAvailableElements(Integer id, List<PermissionAttrTypeGroupDTO> connected) {
      return SpringServiceFactory.getRoleService().getPermissionsForAttributeTypeGroups(connected);
    }

    @Override
    protected Set<PermissionAttrTypeGroupDTO> getConnectedElements(Role source) {
      Set<PermissionAttrTypeGroupDTO> set = new HashSet<PermissionAttrTypeGroupDTO>();
      for (PermissionAttrTypeGroup elem : source.getPermissionsAttrTypeGroup()) {
        if (elem != null && elem.getAttrTypeGroup() != null) {
          // create DTO with unique ID (use ID of attribute type group)
          PermissionAttrTypeGroupDTO dto = new PermissionAttrTypeGroupDTO(elem.getAttrTypeGroup().getId(), elem);
          set.add(dto);
        }
      }

      return set;
    }

    @Override
    protected void setConnectedElements(Role target, Set<PermissionAttrTypeGroupDTO> toConnect) {
      Set<PermissionAttrTypeGroup> set = new HashSet<PermissionAttrTypeGroup>();
      for (PermissionAttrTypeGroupDTO elem : toConnect) {
        //Due to the LazyInitException we need to load and set explixitly the role permissions 
        AttributeTypeGroup atg = SpringServiceFactory.getAttributeTypeGroupService().loadObjectByIdIfExists(
            elem.getPermission().getAttrTypeGroup().getId());
        elem.getPermission().getAttrTypeGroup().setPermissionsRole(atg.getPermissionsRole());

        set.add(elem.getPermission());
      }

      target.removePermissionsAttrTypeGroups();
      List<PermissionAttrTypeGroup> existingPermissions = SpringServiceFactory.getAttributeTypeGroupService().reloadPermissionAttrTypeGroups(set);
      target.addPermissionsAttrTypeGroups(existingPermissions);

      ImmutableSet<PermissionAttrTypeGroup> newPermissions = Sets.difference(set, Sets.newHashSet(existingPermissions)).immutableCopy();

      target.addPermissionsAttrTypeGroups(newPermissions);
    }
  }

  private static final class AggregatedSubRoleCM extends ManyAssociationSetReadOnlyComponentModel<Role, Role> {
    /** Serialization version. */
    private static final long serialVersionUID = -6587221564300830224L;

    public AggregatedSubRoleCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, Boolean[] lookupLablesMode) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, lookupLablesMode);
    }

    @Override
    protected List<Role> getConnectedElementsToDisplay(Role source) {
      ArrayList<Role> result = new ArrayList<Role>(source.getConsistsOfRolesAggregated());
      Collections.sort(result);
      return result;
    }
  }

  private static final class DescriptionCM extends StringComponentModel<Role> {
    /** Serialization version. */
    private static final long serialVersionUID = -1592968347617917019L;

    public DescriptionCM(ComponentMode componentMode, String htmlId, String labelKey, boolean mandatory) {
      super(componentMode, htmlId, labelKey, mandatory);
    }

    @Override
    public void setStringForElement(Role target, String stringToSet) {
      target.setDescription(stringToSet);
    }

    @Override
    public String getStringFromElement(Role source) {
      return source.getDescription();
    }
  }

  private static final class AggregatedFunctionalPermissionCM extends ManyAssociationSetReadOnlyComponentModel<Role, PermissionFunctional> {
    /** Serialization version. */
    private static final long serialVersionUID = 3410301659334870193L;

    public AggregatedFunctionalPermissionCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, Boolean[] lookupLablesMode) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, lookupLablesMode);
    }

    @Override
    protected List<PermissionFunctional> getConnectedElementsToDisplay(Role source) {
      List<PermissionFunctional> result = new ArrayList<PermissionFunctional>(source.getPermissionsFunctionalAggregated());
      removeBuildingBlockPermissions(result);
      Collections.sort(result);
      return result;
    }

    private void removeBuildingBlockPermissions(Iterable<PermissionFunctional> permissionsFunctional) {
      for (Iterator<PermissionFunctional> it = permissionsFunctional.iterator(); it.hasNext();) {
        PermissionFunctional perm = it.next();
        if (TypeOfBuildingBlock.typeOfBuildingBlockForClass(perm.getTypeOfFunctionalPermission().getClassForPermission()) != null) {
          it.remove();
        }
      }
    }
  }

  private static final class NameElementNameCM extends ElementNameComponentModel<Role> {
    /** Serialization version. */
    private static final long serialVersionUID = 7473619816199565924L;

    public NameElementNameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(Role target, String stringToSet) {
      target.setRoleName(stringToSet);
    }

    @Override
    public String getStringFromElement(Role source) {
      return source.getRoleName();
    }
  }

  private static final class AggregatedBbTypeCM extends PermissionMatrixComponentModel {
    /** Serialization version. */
    private static final long serialVersionUID = 4068174656828346870L;

    public AggregatedBbTypeCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, true);
    }

    /**{@inheritDoc}**/
    @Override
    public List<BuildingBlockType> getAvailableBuildingBlockTypes() {
      return SpringServiceFactory.getRoleService().getAvailableBuildingBlockTypes(null);
    }

    /**{@inheritDoc}**/
    @Override
    public List<PermissionFunctional> getAvailablePermissionFunctional() {
      return SpringServiceFactory.getRoleService().getAvailableFunctionalPermissions(null);
    }
  }

  private static final class AggregatedPermissionAtgCM extends ManyAssociationSetReadOnlyComponentModel<Role, PermissionAttrTypeGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = 4303285946562663786L;

    public AggregatedPermissionAtgCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, Boolean[] lookupLablesMode) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, lookupLablesMode);
    }

    @Override
    protected List<PermissionAttrTypeGroup> getConnectedElementsToDisplay(Role source) {
      Set<PermissionAttrTypeGroup> permissionAtgAggr = source.getPermissionsAttrTypeGroupAggregated();
      Map<Integer, PermissionAttrTypeGroup> map = new HashMap<Integer, PermissionAttrTypeGroup>();
      for (PermissionAttrTypeGroup patg : permissionAtgAggr) {
        PermissionAttrTypeGroup tmpPatg = map.get(patg.getAttrTypeGroup().getId());
        if (tmpPatg == null) {
          tmpPatg = new PermissionAttrTypeGroup();
          tmpPatg.setReadPermission(patg.isReadPermission());
          tmpPatg.setWritePermission(patg.isWritePermission());
          tmpPatg.setAttrTypeGroup(patg.getAttrTypeGroup());
          map.put(patg.getAttrTypeGroup().getId(), tmpPatg);
          continue;
        }
        if (patg.isReadPermission().booleanValue()) {
          tmpPatg.setReadPermission(Boolean.TRUE);
        }
        if (patg.isWritePermission().booleanValue()) {
          tmpPatg.setWritePermission(Boolean.TRUE);
        }
        map.put(patg.getAttrTypeGroup().getId(), tmpPatg);
      }
      List<PermissionAttrTypeGroup> result = new ArrayList<PermissionAttrTypeGroup>(map.values());
      Collections.sort(result);
      return result;
    }
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

}
