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
package de.iteratec.iteraplan.presentation.dialog.common.model.permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.sorting.UserEntityIntComparator;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;


public class UserEntityCM extends ManyAssociationSetComponentModel<BuildingBlock, UserEntity> {

  /** Serialization version. */
  private static final long serialVersionUID             = 4838289388965649004L;

  private List<UserEntity>  aggregatedOwningUserEntities = new ArrayList<UserEntity>();
  private boolean           imminentLockOut              = false;

  /**
   * @param componentMode
   * @param htmlId
   */
  public UserEntityCM(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId, "instancePermission.title", new String[] { "global.type", "instancePermission.name",
        "instancePermission.description" }, new String[] { "type", "identityString", "descriptiveString" }, "identityString", null, new Boolean[] {
        Boolean.TRUE, Boolean.FALSE, Boolean.FALSE }, Boolean.FALSE, new String[] { "global.user", "global.usergroups" });
  }

  @Override
  protected void setConnectedElements(BuildingBlock target, Set<UserEntity> referenceObjects) {
    if (!target.getOwningUserEntities().equals(referenceObjects)) {
      target.setOwningUserEntities(referenceObjects);
    }
  }

  @Override
  protected List<UserEntity> getAvailableElements(Integer id, List<UserEntity> connected) {
    Set<Integer> connectedRefIds = GeneralHelper.createIdSetFromIdEntities(connected);
    List<UserEntity> allUserEntitiesFiltered = SpringServiceFactory.getUserService().getAllUserEntitiesFiltered(connectedRefIds);
    
    //initiliaze UserGroup.members because of lazy init
    for (UserEntity userEntity : allUserEntitiesFiltered) {
      if (userEntity instanceof UserGroup) {
        Hibernate.initialize(((UserGroup) userEntity).getMembers());
      }
    }
    
    return allUserEntitiesFiltered;
  }

  @Override
  protected Set<UserEntity> getConnectedElements(BuildingBlock source) {
    return source.getOwningUserEntities();
  }

  @Override
  public void initializeFrom(BuildingBlock source) {
    super.initializeFrom(source);
    this.aggregatedOwningUserEntities = SpringServiceFactory.getUserService().getAggregatedOwningUserEntities(getConnectedElements());
  }

  @Override
  public void update() {
    super.update();
    // sort list with UserEntityComparator
    Collections.sort(getConnectedElements(), new UserEntityIntComparator());
    // check if the user is locking himself out.
    InformationSystem dummy = new InformationSystem();
    dummy.setOwningUserEntities(new HashSet<UserEntity>(getConnectedElements()));
    setImminentLockOut(!UserContext.getCurrentUserContext().getPerms().userHasBbInstanceWritePermission(dummy));
  }

  /**
   * Returns an array of lists with available UserEntities:
   * <ol>
   *   <li>All available users.</li>
   *   <li>All available user groups.</li>
   * </ol>
   * @return List[User, UserGroup] 
   */
  @SuppressWarnings("unchecked")
  public List<UserEntity>[] getAvailableElementsPresentationGrouped() {
    List<UserEntity> users = new ArrayList<UserEntity>();
    List<UserEntity> userGroups = new ArrayList<UserEntity>();
    for (Iterator<UserEntity> it = this.getAvailableElements().iterator(); it.hasNext();) {
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

  public List<UserEntity> getAggregatedOwningUserEntities() {
    return aggregatedOwningUserEntities;
  }

  public boolean isImminentLockOut() {
    return imminentLockOut;
  }

  public void setImminentLockOut(boolean imminentLockOut) {
    this.imminentLockOut = imminentLockOut;
  }

}
