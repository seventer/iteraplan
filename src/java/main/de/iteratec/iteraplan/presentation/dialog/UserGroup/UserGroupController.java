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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import de.iteratec.iteraplan.businesslogic.service.UserGroupService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.presentation.dialog.GuiSearchController;
import de.iteratec.iteraplan.presentation.memory.SearchDialogMemory;


/**
 * This Controller handles all simple standard actions in the User Group context, that are not
 * handled by Spring WebFlow. This contains mainly actions about the user group search.
 */
@Controller
public class UserGroupController extends GuiSearchController<UserGroupDialogMemory> {

  @Autowired
  private UserGroupService userGroupService;

  @Autowired
  public UserGroupController(UserGroupService userGroupService) {
    this.userGroupService = userGroupService;
  }

  @Override
  protected String getDialogName() {
    return Dialog.USER_GROUP.getDialogName();
  }

  @Override
  protected String getBaseViewMapping() {
    return "usergroup";
  }

  @Override
  protected TypeOfBuildingBlock getTob() {
    // no building block controller
    return null;
  }

  /**{@inheritDoc}**/
  @Override
  protected void initHierarchicalTopElement(SearchDialogMemory searchDialogMemory) {
    searchDialogMemory.setHierarchicalTopElement(null); // no hierarchy => set top element to null     
  }

  @Override
  protected UserGroupDialogMemory getDialogMemory() {
    return new UserGroupDialogMemory();
  }

  /**
   * This method executes the search for specific user groups
   * 
   * @param dialogMemory
   *          contains search parameters
   * @param model
   *          Spring model
   * @return view ID of the result page
   */
  @Override
  public PagedListHolder<?> searchAll(UserGroupDialogMemory dialogMemory, ModelMap model) {

    List<UserGroup> userGroupList = userGroupService.getUserGroupsBySearch(dialogMemory.toUserGroup());
    PagedListHolder<UserGroup> results = new PagedListHolder<UserGroup>(userGroupList);

    return results;
  }

}