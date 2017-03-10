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
package de.iteratec.iteraplan.presentation.dialog.AttributeTypeGroup;

import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getAttributeTypeGroupService;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.dto.PermissionAttrTypeGroupDTO;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.dialog.Role.PermissionAttributeTypeGroupComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.BooleanComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ElementNameComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationListComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetReadOnlyComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


/**
 * Main component model for the attribute type group management page.
 */
public class AttributeTypeGroupComponentModel extends AbstractComponentModelBase<AttributeTypeGroup> {

  /** Serialization version. */
  private static final long                                                                     serialVersionUID                           = -7287306194456247279L;

  private static final Logger                                                                   LOGGER                                     = Logger
                                                                                                                                               .getIteraplanLogger(AttributeTypeGroupComponentModel.class);

  private static final String                                                                   NAME_LABEL_KEY                             = Constants.ATTRIBUTE_NAME;
  private static final String                                                                   DESCRIPTION_LABEL_KEY                      = Constants.ATTRIBUTE_DESCRIPTION;
  private static final String                                                                   NAME_PROPERTY_KEY                          = Constants.ATTRIBUTE_NAME;
  private static final String                                                                   DESCRIPTION_PROPERTY_KEY                   = Constants.ATTRIBUTE_DESCRIPTION;
  private static final String                                                                   NAME_PROPERTY                              = "name";
  private static final String                                                                   DESCRIPTION_PROPERTY                       = "description";
  private static final String                                                                   TOPLEVELATG_LABEL_KEY                      = "atg.toplevelATG";
  private static final String                                                                   PERMISSION_ATG_AGGREGATED_TABLE_HEADER_KEY = "atg.permissionsRole.aggregated";

  /** CM for the managed AttributeTypeGroup instance itself. */
  private AttributeTypeGoupSelectionComponentModel                                              chooseAttributeTypeGroupComponentModel;

  /** CM for the AttributeTypeGroup name property. */
  private ElementNameComponentModel<AttributeTypeGroup>                                         nameModel;

  /** CM for the AttributeTypeGroup description property. */
  private StringComponentModel<AttributeTypeGroup>                                              descriptionModel;

  private BooleanComponentModel<AttributeTypeGroup>                                             toplevelATGModel;

  /** CM for the attribute types contained in the group. */
  private ManyAssociationListComponentModel<AttributeTypeGroup, AttributeType>                  containedAttributeTypesModel;

  /** CM for the associated roles. */
  private PermissionAttributeTypeGroupComponentModel<AttributeTypeGroup>                        permissionRolesModel;

  /** CM for aggregated permissions for attribute type groups (read-only). */
  private ManyAssociationSetReadOnlyComponentModel<AttributeTypeGroup, PermissionAttrTypeGroup> aggregatedPermissionAtgModel;

  /** @see AttributeTypeGroup#getLastModificationUser() */
  private String                                                                                lastModificationUser                       = "";

  /** @see AttributeTypeGroup#getLastModificationTime() */
  private Date                                                                                  lastModificationTime                       = null;

  private AttributeTypeGroup                                                                    attributeTypeGroup;
  
  
  public AttributeTypeGroupComponentModel(ComponentMode componentMode) {
    super(componentMode);

  }

  public void initializeFrom(AttributeTypeGroup source) {
    getChooseAttributeTypeGroupComponentModel().initializeFrom(source);
    getNameModel().initializeFrom(source);
    getDescriptionModel().initializeFrom(source);
    getToplevelATGModel().initializeFrom(source);
    getContainedAttributeTypesModel().initializeFrom(source);
    getPermissionRolesModel().initializeFrom(source);
    getAggregatedPermissionAtgModel().initializeFrom(source);
    this.lastModificationTime = source.getLastModificationTime();
    this.lastModificationUser = source.getLastModificationUser();
  }

  public void update() {
    getNameModel().update();
    getDescriptionModel().update();
    getToplevelATGModel().update();
    getContainedAttributeTypesModel().update();
    getPermissionRolesModel().update();
  }

  public void configure(AttributeTypeGroup target) {
    getNameModel().configure(target);
    getDescriptionModel().configure(target);
    getToplevelATGModel().configure(target);
    getContainedAttributeTypesModel().configure(target);
    getPermissionRolesModel().configure(target);
  }

  public void validate(Errors errors) {
    errors.pushNestedPath("nameModel");
    nameModel.validate(errors);
    errors.popNestedPath();

    errors.pushNestedPath("descriptionModel");
    descriptionModel.validateDescription(errors);
    errors.popNestedPath();
  }

  public final StringComponentModel<AttributeTypeGroup> getDescriptionModel() {
    if (descriptionModel == null) {
      descriptionModel = new DescriptionStringComponentModel(getComponentMode(), "description", DESCRIPTION_LABEL_KEY);
    }
    return descriptionModel;
  }

  public final BooleanComponentModel<AttributeTypeGroup> getToplevelATGModel() {
    if (toplevelATGModel == null) {
      toplevelATGModel = new ToplevelATGBooleanComponentModel(getComponentMode(), "ToplevelATG", TOPLEVELATG_LABEL_KEY);
    }
    return toplevelATGModel;
  }

  public final ElementNameComponentModel<AttributeTypeGroup> getNameModel() {
    if (nameModel == null) {
      nameModel = new NameElementNameComponentModel(getComponentMode(), "name", NAME_LABEL_KEY);
    }
    return nameModel;
  }

  public final PermissionAttributeTypeGroupComponentModel<AttributeTypeGroup> getPermissionRolesModel() {
    if (permissionRolesModel == null) {
      permissionRolesModel = new PermissionRolesPermissionAttributeTypeGroupCM(getComponentMode(), false);
    }
    return permissionRolesModel;
  }

  public final ManyAssociationListComponentModel<AttributeTypeGroup, AttributeType> getContainedAttributeTypesModel() {
    if (containedAttributeTypesModel == null) {
      containedAttributeTypesModel = new ContainedAttributeTypesCM(getComponentMode(), "containedAts", "atg.containedAttributes", new String[] {
          NAME_PROPERTY_KEY, DESCRIPTION_PROPERTY_KEY }, new String[] { NAME_PROPERTY, DESCRIPTION_PROPERTY }, NAME_PROPERTY, new TextAT());
    }
    return containedAttributeTypesModel;
  }

  public final ManyAssociationSetReadOnlyComponentModel<AttributeTypeGroup, PermissionAttrTypeGroup> getAggregatedPermissionAtgModel() {
    if (aggregatedPermissionAtgModel == null) {
      aggregatedPermissionAtgModel = new AggregatedPermissionAtgManyAssociationSetReadOnlyCM(getComponentMode(), "aggregatedPermissionAtgs",
          PERMISSION_ATG_AGGREGATED_TABLE_HEADER_KEY, new String[] { NAME_LABEL_KEY, "manageRoles.setPermissions" }, new String[] { "role.roleName",
              "permissionKey" }, new Boolean[] { Boolean.FALSE, Boolean.TRUE });
    }
    return aggregatedPermissionAtgModel;
  }

  public AttributeTypeGoupSelectionComponentModel getChooseAttributeTypeGroupComponentModel() {
    if (chooseAttributeTypeGroupComponentModel == null) {
      chooseAttributeTypeGroupComponentModel = new AttributeTypeGoupSelectionComponentModel(getComponentMode(), "atgSelection");
    }
    return chooseAttributeTypeGroupComponentModel;
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
  
  public void sortEverything() {

    containedAttributeTypesModel.sort();
  }

  private static final class DescriptionStringComponentModel extends StringComponentModel<AttributeTypeGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = 3035563798317259151L;

    public DescriptionStringComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(AttributeTypeGroup element) {
      return element.getDescription();
    }

    @Override
    public void setStringForElement(AttributeTypeGroup element, String stringToSet) {
      element.setDescription(stringToSet);
    }
  }

  private static final class ToplevelATGBooleanComponentModel extends BooleanComponentModel<AttributeTypeGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = 2602261368445870416L;

    public ToplevelATGBooleanComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public Boolean getBooleanFromElement(AttributeTypeGroup source) {
      return source.isToplevelATG();
    }

    @Override
    public void setBooleanForElement(AttributeTypeGroup target, Boolean booleanToSet) {
      target.setToplevelATG(booleanToSet);
    }
  }

  private static final class NameElementNameComponentModel extends ElementNameComponentModel<AttributeTypeGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = 5870727486848602205L;

    public NameElementNameComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(AttributeTypeGroup element) {
      if (element != null && AttributeTypeGroup.STANDARD_ATG_NAME.equals(element.getName())) {
        setDefaultAttributeGroupSelected(true);
      }
      
      return element.getName();
    }

    @Override
    public void setStringForElement(AttributeTypeGroup element, String stringToSet) {
      // prevent setting of a different name, if name was "[Default Attribute Group]"
      if (isDefaultAttributeGroupSelected()) {
        return;
      }
      
      element.setName(stringToSet);
    }
  }

  private static final class PermissionRolesPermissionAttributeTypeGroupCM extends PermissionAttributeTypeGroupComponentModel<AttributeTypeGroup> {
    /** Serialization version. */
    private static final long serialVersionUID = 7420032573225137603L;

    public PermissionRolesPermissionAttributeTypeGroupCM(ComponentMode componentMode, boolean manageFromRoleSide) {
      super(componentMode, manageFromRoleSide);
    }

    @Override
    protected List<PermissionAttrTypeGroupDTO> getAvailableElements(Integer id, List<PermissionAttrTypeGroupDTO> connected) {
      return getAttributeTypeGroupService().getPermissionsForRoles(id, connected);
    }

    @Override
    protected Set<PermissionAttrTypeGroupDTO> getConnectedElements(AttributeTypeGroup source) {
      Set<PermissionAttrTypeGroupDTO> set = Sets.newHashSet();
      for (PermissionAttrTypeGroup elem : source.getPermissionsRole()) {
        if (elem != null && elem.getRole() != null) {
          // create DTO with unique ID (use ID of role)
          PermissionAttrTypeGroupDTO dto = new PermissionAttrTypeGroupDTO(elem.getRole().getId(), elem);
          set.add(dto);
        }
      }
      return set;
    }

    @Override
    protected void setConnectedElements(AttributeTypeGroup target, Set<PermissionAttrTypeGroupDTO> toConnect) {
      Set<PermissionAttrTypeGroup> set = Sets.newHashSet();
      for (PermissionAttrTypeGroupDTO dto : toConnect) {
        set.add(dto.getPermission());
      }

      target.removePermissions();
      List<PermissionAttrTypeGroup> existingPermissions = SpringServiceFactory.getAttributeTypeGroupService().reloadPermissionAttrTypeGroups(set);
      for (PermissionAttrTypeGroup permission : existingPermissions) {
        for (PermissionAttrTypeGroup addedPerm : set) {
          if (permission.equals(addedPerm)) {
            permission.setReadPermission(addedPerm.isReadPermission());
            permission.setWritePermission(addedPerm.isWritePermission());
            break;
          }
        }
      }
      target.addPermissions(existingPermissions);

      ImmutableSet<PermissionAttrTypeGroup> newPermissions = Sets.difference(set, Sets.newHashSet(existingPermissions)).immutableCopy();
      target.addPermissions(newPermissions);
    }
  }

  private static final class ContainedAttributeTypesCM extends ManyAssociationListComponentModel<AttributeTypeGroup, AttributeType> {
    /** Serialization version. */
    private static final long serialVersionUID = 821138468764915486L;

    public ContainedAttributeTypesCM(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
        String[] connectedElementsFields, String availableElementsLabel, AttributeType dummyForPresentation) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
    }

    @Override
    protected List<AttributeType> getAvailableElements(AttributeTypeGroup source, List<AttributeType> connected) {
      return SpringServiceFactory.getAttributeTypeService().getAttributeTypesFiltered(connected);
    }

    @Override
    protected List<AttributeType> getConnectedElements(AttributeTypeGroup source) {
      return source.getAttributeTypes();
    }

    /**
     * The removed {@link AttributeType} elements will be added to Standard Group. The new one will be removed from 
     * the old one and assigned to {@code target} group. 
     * 
     */
    @Override
    protected void setConnectedElements(AttributeTypeGroup atg, List<AttributeType> referenceObjects) {
      List<AttributeType> reloadedATs = SpringServiceFactory.getAttributeTypeService().reload(referenceObjects);
      AttributeTypeGroup standardAtg = SpringServiceFactory.getAttributeTypeGroupService().getStandardAttributeTypeGroup();

      List<AttributeType> oldATs = Lists.newArrayList(atg.getAttributeTypes());
      for (AttributeType at : oldATs) {
        at.removeAttributeTypeGroupTwoWay();
        if (!reloadedATs.contains(at)) {
          standardAtg.addAttributeTypeTwoWay(at);
        }
      }

      for (AttributeType at : reloadedATs) {
        if (at.getAttributeTypeGroup() != null) {
          at.removeAttributeTypeGroupTwoWay();
        }
        atg.addAttributeTypeTwoWay(at);
      }
    }

    @Override
    protected boolean isElementRemovable() {
      setErrorCode(IteraplanErrorMessages.CANNOT_REMOVE_ELEMENT_FROM_TYPEGROUP);

      boolean isNotNull = getSourceElement() != null;
      String name = getSourceElement().getName();
      return isNotNull && !StringUtils.equals(name, AttributeTypeGroup.STANDARD_ATG_NAME);
    }
  }

  private static final class AggregatedPermissionAtgManyAssociationSetReadOnlyCM extends
      ManyAssociationSetReadOnlyComponentModel<AttributeTypeGroup, PermissionAttrTypeGroup> {

    /** Serialization version. */
    private static final long serialVersionUID = 6603774352529476164L;

    public AggregatedPermissionAtgManyAssociationSetReadOnlyCM(ComponentMode componentMode, String htmlId, String tableHeaderKey,
        String[] columnHeaderKeys, String[] connectedElementsFields, Boolean[] lookupLablesMode) {
      super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, lookupLablesMode);
    }

    @Override
    protected List<PermissionAttrTypeGroup> getConnectedElementsToDisplay(AttributeTypeGroup source) {
      LOGGER.debug("Entering getConnectedElementsToDisplay()");

      Map<Integer, PermissionAttrTypeGroup> map = createPermissionsMap(source);
      // for each directly connected role, create a PermissionAttTypeGroup for all
      // parent roles, which in fact aggregate this permission.
      for (PermissionAttrTypeGroup patg : source.getPermissionsRole()) {
        if (patg == null) {
          LOGGER.error("PermissionAttrTypeGroup is null!");
          continue;
        }

        Role patgRole = patg.getRole();
        if (patgRole == null) {
          LOGGER.error("PermissionAttrTypeGroup.getRole is null for ATG: " + patg.getAttributeTypeGroupName()
              + ". Try to edit this ATG with superuser to establish a clean state!");
          continue;
        }

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug(" - PermissionAttrTypeGroup with ID: " + patg.getId());
          LOGGER.debug(" - - Role " + patg.getRoleName() + "with ID: " + patg.getRole().getId());
          LOGGER.debug(" - - AttributeTypeGroup " + patg.getAttributeTypeGroupName() + "with ID: " + patg.getAttrTypeGroup().getId());
        }

        addAggregatedPermissions(map, patg, patgRole);
      }

      List<PermissionAttrTypeGroup> result = Lists.newArrayList(map.values());
      Collections.sort(result);

      return result;
    }

    private Map<Integer, PermissionAttrTypeGroup> createPermissionsMap(AttributeTypeGroup source) {
      Map<Integer, PermissionAttrTypeGroup> map = new HashMap<Integer, PermissionAttrTypeGroup>();
      for (PermissionAttrTypeGroup patg : source.getPermissionsRole()) {
        if (patg != null && patg.getRole() != null) {
          map.put(patg.getRole().getId(), patg);
        }

      }

      return map;
    }

    private void addAggregatedPermissions(Map<Integer, PermissionAttrTypeGroup> map, PermissionAttrTypeGroup patg, Role patgRole) {
      for (Role role : patgRole.getElementOfRolesAggregated()) {
        PermissionAttrTypeGroup visitedPatg = map.get(role.getId());
        if (visitedPatg == null) {
          // create a permission for display purposes only
          visitedPatg = new PermissionAttrTypeGroup();
          visitedPatg.setRole(role);
          visitedPatg.setReadPermission(patg.isReadPermission());
          visitedPatg.setWritePermission(patg.isWritePermission());
          map.put(role.getId(), visitedPatg);
          continue;
        }
        if (patg.isReadPermission().booleanValue()) {
          visitedPatg.setReadPermission(Boolean.TRUE);
        }
        if (patg.isWritePermission().booleanValue()) {
          visitedPatg.setWritePermission(Boolean.TRUE);
        }
        map.put(role.getId(), visitedPatg);
      }
    }
  }

  public AttributeTypeGroup getAttributeTypeGroup() {
    return attributeTypeGroup;
  }

  public void setAttributeTypeGroup(AttributeTypeGroup attributeTypeGroup) {
    this.attributeTypeGroup = attributeTypeGroup;
  }

}
