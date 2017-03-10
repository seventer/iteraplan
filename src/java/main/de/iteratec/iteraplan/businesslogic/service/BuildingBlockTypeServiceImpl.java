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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.notifications.NotificationService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.collections.EntityToIdFunction;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.presentation.email.BuildingBlockTypeModelBuilder;
import de.iteratec.iteraplan.presentation.email.EmailModel;


/**
 * The default implementation of the {@link BuildingBlockTypeService}.
 */
public class BuildingBlockTypeServiceImpl extends AbstractService implements BuildingBlockTypeService {

  private BuildingBlockTypeDAO buildingBlockTypeDAO;
  private NotificationService  notificationService;

  /** {@inheritDoc} */
  public BuildingBlockType loadObjectById(Integer id) {
    return buildingBlockTypeDAO.loadObjectById(id);
  }

  /** {@inheritDoc} */
  public BuildingBlockType getBuildingBlockTypeByType(TypeOfBuildingBlock typeOfBuildingBlock) {
    return buildingBlockTypeDAO.getBuildingBlockTypeByType(typeOfBuildingBlock);
  }

  /** {@inheritDoc} */
  public List<BuildingBlockType> getAvailableBuildingBlockTypesForAttributeType(Integer attributeTypeId) {
    return buildingBlockTypeDAO.getAvailableBuildingBlockTypesForAttributeType(attributeTypeId);
  }

  /** {@inheritDoc} */
  public List<BuildingBlockType> getBuildingBlockTypesEligibleForAttributes() {
    List<BuildingBlockType> list = buildingBlockTypeDAO.getBuildingBlockTypesEligibleForAttributes();
    Collections.sort(list);

    return list;
  }

  public void setBuildingBlockTypeDAO(BuildingBlockTypeDAO buildingBlockTypeDAO) {
    this.buildingBlockTypeDAO = buildingBlockTypeDAO;
  }

  public void setNotificationService(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  /** {@inheritDoc} */
  public Integer subscribe(Integer id, boolean subscribe) {
    BuildingBlockType bbt = buildingBlockTypeDAO.loadObjectById(id);

    boolean subscribePerm = UserContext.getCurrentUserContext().getPerms().isUserHasFuncPermSubscription();

    if (!subscribePerm) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }

    User user = UserContext.getCurrentUserContext().getUser();

    String key;
    if (subscribe) {
      bbt.getSubscribedUsers().add(user);
      key = "generic.subscribed";
    }
    else {
      bbt.getSubscribedUsers().remove(user);
      key = "generic.unsubscribed";
    }
    buildingBlockTypeDAO.saveOrUpdate(bbt);

    BuildingBlockTypeModelBuilder modelBuilder = new BuildingBlockTypeModelBuilder(bbt);
    Map<User, EmailModel> mailModel = new HashMap<User, EmailModel>();
    mailModel.put(user, modelBuilder.createModel());
    notificationService.sendEmail(ImmutableList.of(user), key, mailModel);

    return Integer.valueOf(bbt.getSubscribedUsers().size());
  }

  /** {@inheritDoc} */
  public List<BuildingBlockType> reload(Collection<BuildingBlockType> entities) {
    if ((entities == null) || entities.isEmpty()) {
      return new ArrayList<BuildingBlockType>(0);
    }

    EntityToIdFunction<BuildingBlockType, Integer> toIdFunction = new EntityToIdFunction<BuildingBlockType, Integer>();
    Iterable<Integer> transform = Iterables.transform(entities, toIdFunction);

    return buildingBlockTypeDAO.loadElementListWithIds(Sets.newHashSet(transform));
  }

  /** {@inheritDoc} */
  public BuildingBlockType saveOrUpdate(BuildingBlockType bbt) {
    return buildingBlockTypeDAO.saveOrUpdate(bbt);
  }

  /**{@inheritDoc}**/
  public Collection<BuildingBlockType> getSubscribedElements() {
    return this.getDao().getSubscribedElements();
  }

  protected DAOTemplate<BuildingBlockType, Integer> getDao() {
    return buildingBlockTypeDAO;
  }

  /**{@inheritDoc}**/
  public List<BuildingBlockType> getAllBuildingBlockTypesForDisplay() {
    List<BuildingBlockType> result = Lists.newArrayList();

    for (String tobbString : Constants.ALL_TYPES_FOR_DISPLAY) {
      result.add(getBuildingBlockTypeByType(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(tobbString)));
    }

    return result;
  }
}
