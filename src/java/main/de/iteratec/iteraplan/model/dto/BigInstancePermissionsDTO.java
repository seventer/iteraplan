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
package de.iteratec.iteraplan.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;


public class BigInstancePermissionsDTO implements Serializable {

  /** Serialization version. */
  private static final long   serialVersionUID = -8508142974899487147L;
  private UserEntity          currentUserEntity;
  /** All UserEntities */
  private List<UserEntity>    availableUserEntities;
  /** All BuildingBlocks that the currentUserEntity owns */
  private List<BuildingBlock> associatedBuildingBlocks;
  /** All Types that can be queried */
  private List<Type<?>>       availableQueryTypes;

  public List<BuildingBlock> getAssociatedBuildingBlocks() {
    return associatedBuildingBlocks;
  }

  public void setAssociatedBuildingBlocks(List<BuildingBlock> associatedBuildingBlocks) {
    this.associatedBuildingBlocks = associatedBuildingBlocks;
  }

  public UserEntity getCurrentUserEntity() {
    return currentUserEntity;
  }

  public void setCurrentUserEntity(UserEntity currentUserEntity) {
    this.currentUserEntity = currentUserEntity;
  }

  public List<UserEntity> getAvailableUserEntities() {
    return availableUserEntities;
  }

  @SuppressWarnings("unchecked")
  public List<UserEntity>[] getAvailableUserEntitiesForPresentation() {
    List<UserEntity> users = new ArrayList<UserEntity>();
    List<UserEntity> userGroups = new ArrayList<UserEntity>();
    for (Iterator<UserEntity> it = availableUserEntities.iterator(); it.hasNext();) {
      UserEntity userEntity = it.next();
      if (userEntity instanceof User) {
        users.add(userEntity);
      }
      else if (userEntity instanceof UserGroup) {
        userGroups.add(userEntity);
      }
    }
    return new List[] { users, userGroups };
  }

  public void setAvailableUserEntities(List<UserEntity> availableUserEntities) {
    this.availableUserEntities = availableUserEntities;
  }

  public List<Type<?>> getAvailableQueryTypes() {
    return availableQueryTypes;
  }

  public void setAvailableQueryTypes(List<Type<?>> availableQueryTypes) {
    this.availableQueryTypes = availableQueryTypes;
  }

}
