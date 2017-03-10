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
package de.iteratec.iteraplan.presentation.dialog.Role.model;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public class BbtPermissionCellComponentModel extends BasePermissionCellComponentModel {

  private static final long  serialVersionUID = -721302801598332412L;

  /** The building block for the permission */
  private BuildingBlockType  buildingBlockType;

  /** The type of the permission */
  private EditPermissionType editPermissionType;

  /**
   * @param componentMode The {@link ComponentMode} of this component model.
   * @param aggregated if this is true, all aggregated permissions will be shown. The Table is also not editable.
   */
  protected BbtPermissionCellComponentModel(ComponentMode componentMode, boolean aggregated) {
    super(componentMode, aggregated);
  }

  /**{@inheritDoc}**/
  public void initializeFrom(Role source) {
    setValue(Boolean.valueOf(isAggregated() ? hasAggregatedPermission(source) : getThisPermission(source) != null));
  }

  /**{@inheritDoc}**/
  public void update() {
    // nothing to do here
  }

  /**{@inheritDoc}**/
  public void configure(Role target) {
    if (isAggregated()) {
      return;
    }

    Role2BbtPermission perm = getThisPermission(target);
    if (perm != null) {
      if (!getValue().booleanValue()) {
        // Remove Permission
        perm.disconnect();
      }
    }
    else {
      if (getValue().booleanValue()) {
        // Add Permission
        perm = new Role2BbtPermission();
        perm.setRole(target);
        perm.setBbt(SpringServiceFactory.getBuildingBlockTypeService().loadObjectById(buildingBlockType.getId()));
        perm.setType(editPermissionType);
        perm.connect();
      }
    }
  }

  /**{@inheritDoc}**/
  public void validate(Errors errors) {
    // nothing to do here
  }

  /**
   * Searches in the {@link Role}, if there is an permission for the same {@link BuildingBlockType} and {@link EditPermissionType}.
   * @param r {@link Role}
   * @return The {@link Role2BbtPermission} if found, else null.
   */
  private Role2BbtPermission getThisPermission(Role r) {
    for (Role2BbtPermission perm : r.getPermissionsBbt()) {
      if (perm.getBbt().equals(buildingBlockType) && perm.getType().equals(editPermissionType)) {
        return perm;
      }
    }
    return null;
  }

  /**
   * Checks weather this permission is granted directly or by aggregated {@link Role}s.
   * @param r {@link Role}
   * @return true, if an permission is granted, else false.
   */
  private boolean hasAggregatedPermission(Role r) {
    for (BuildingBlockType bbt : r.getBbtForPermissionTypeAggregated(editPermissionType)) {
      if (bbt.equals(buildingBlockType)) {
        return true;
      }
    }
    return false;
  }

  public BuildingBlockType getBuildingBlockType() {
    return buildingBlockType;
  }

  public void setBuildingBlockType(BuildingBlockType buildingBlockType) {
    this.buildingBlockType = buildingBlockType;
  }

  public EditPermissionType getEditPermissionType() {
    return editPermissionType;
  }

  public void setEditPermissionType(EditPermissionType editPermissionType) {
    this.editPermissionType = editPermissionType;
  }

}
