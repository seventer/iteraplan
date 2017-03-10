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

import java.util.List;

import org.springframework.validation.Errors;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Implements {@link BasePermissionCellComponentModel}. This one holds functional permissions for a role.
 */
public class FuncPermissionCellComponentModel extends BasePermissionCellComponentModel {

  private static final long    serialVersionUID = -721302801598332412L;

  /** the functional permission */
  private PermissionFunctional permissionFunctional;

  /**
   * @param componentMode The {@link ComponentMode} of this component model.
   * @param aggregated if this is true, all aggregated permissions will be shown. The Table is also not editable.
   */
  protected FuncPermissionCellComponentModel(ComponentMode componentMode, boolean aggregated) {
    super(componentMode, aggregated);
  }

  /**{@inheritDoc}**/
  public void initializeFrom(Role source) {
    setValue(Boolean.valueOf(isAggregated() && source.getPermissionsFunctionalAggregated().contains(permissionFunctional)
        || source.getPermissionsFunctional().contains(permissionFunctional)));
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

    PermissionFunctional permissionFunctionalReload = reload(permissionFunctional);
    if (permissionFunctionalReload == null) {
      return;
    }

    if (target.getPermissionsFunctional().contains(permissionFunctionalReload)) {
      if (!getValue().booleanValue()) {
        target.removePermissionFunctionalTwoWay(permissionFunctionalReload);
      }
    }
    else {
      if (getValue().booleanValue()) {
        target.addPermissionFunctionalTwoWay(permissionFunctionalReload);
      }
    }
  }

  /**{@inheritDoc}**/
  public void validate(Errors errors) {
    // nothing to do here
  }

  private PermissionFunctional reload(PermissionFunctional pf) {
    List<PermissionFunctional> reloadList = SpringServiceFactory.getRoleService().reloadFunctionalPermissions(Lists.newArrayList(pf));
    if (!reloadList.isEmpty()) {
      return reloadList.get(0);
    }
    return null;
  }

  public PermissionFunctional getPermissionFunctional() {
    return permissionFunctional;
  }

  public void setPermissionFunctional(PermissionFunctional permissionFunctional) {
    this.permissionFunctional = permissionFunctional;
  }

}
