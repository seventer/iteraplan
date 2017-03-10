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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.notifications.NotificationService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.email.AbstractModelBuilder;
import de.iteratec.iteraplan.presentation.email.EmailModel;
import de.iteratec.iteraplan.presentation.email.ModelBuilderFactory;


/**
 * Abstract service class for all entities of type {@link BuildingBlock}.
 *
 * @param <E>
 *          The type parameter for the concrete building block.
 */
public abstract class AbstractBuildingBlockService<E extends BuildingBlock> extends AbstractEntityService<E, Integer> implements
    BuildingBlockService<E, Integer> {

  private AttributeValueService       attributeValueService;
  private GeneralBuildingBlockService generalBuildingBlockService;
  private NotificationService         notificationService;
  private TimeseriesService           timeseriesService;

  public void setTimeseriesService(TimeseriesService timeseriesService) {
    this.timeseriesService = timeseriesService;
  }

  public void setGeneralBuildingBlockService(GeneralBuildingBlockService generalBuildingBlockService) {
    this.generalBuildingBlockService = generalBuildingBlockService;
  }

  public void setAttributeValueService(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  public void setNotificationService(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  /** {@inheritDoc} */
  public Integer subscribe(Integer id, boolean subscribe) {
    final E entity = this.loadObjectById(id);

    boolean funcPerm = UserContext.getCurrentUserContext().getPerms()
        .getUserHasBbTypeFunctionalPermission(entity.getTypeOfBuildingBlock().getValue());
    boolean subscribePerm = UserContext.getCurrentUserContext().getPerms().isUserHasFuncPermSubscription();

    // no subscription possible without read permission
    // consistent with UI
    if (!(subscribePerm && funcPerm)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }

    User user = UserContext.getCurrentUserContext().getUser();
    AbstractModelBuilder modelBuilder = ModelBuilderFactory.createModelBuilder(entity, null, null);

    String key;
    if (subscribe) {
      entity.getSubscribedUsers().add(user);
      key = "generic.subscribed";
    }
    else {
      entity.getSubscribedUsers().remove(user);
      key = "generic.unsubscribed";
    }

    // save directly through DAO to avoid permission check (user can subscribe an element
    // even without create or update permissions)
    this.getDao().saveOrUpdate(entity);
    entity.validate();

    Map<User, EmailModel> models = new HashMap<User, EmailModel>();
    models.put(user, modelBuilder.createModel());
    notificationService.sendEmail(ImmutableList.of(user), key, models);
    return Integer.valueOf(entity.getSubscribedUsers().size());
  }

  /**
   * {@inheritDoc}
   * Firstly it checks if the building block id is not {@code null}.
   * Then reloads the given building block and then calls, in this order:
   * <ul>
   *    <li>{@link #checkDelete(BuildingBlock)}</li>
   *    <li>{@link #onBeforeDelete(BuildingBlock)}</li>
   *    <li>super: {@link AbstractEntityService#deleteEntity(de.iteratec.iteraplan.model.interfaces.Entity)}</li>
   *    <li>{@link #onAfterDelete(BuildingBlock)}</li>
   * </ul>
   */
  @Override
  public final void deleteEntity(E buildingBlock) {
    Integer bbId = buildingBlock.getId();
    if (bbId == null) {
      throw new IllegalArgumentException("An ID equal to null is not allowed for deletion.");
    }

    E reloadedBuildingBlock = loadObjectById(buildingBlock.getId());
    checkDelete(reloadedBuildingBlock);
    E buildingBlockToDelete = onBeforeDelete(reloadedBuildingBlock);
    super.deleteEntity(buildingBlockToDelete);
    onAfterDelete(buildingBlockToDelete);
  }

  /**
   * Checks if the given building block can be deleted.
   * If something goes wrong, the exception will be thrown.
   * 
   * @param buildingBlock the building block to delete
   * @throws de.iteratec.iteraplan.common.error.IteraplanBusinessException - If the user does not have the permissions to delete
   *    the given building block instance.
   */
  protected void checkDelete(E buildingBlock) {
    // override to add checks
  }

  /**
   * Is called in {@link #deleteEntity(BuildingBlock)} right before the actual deletion.
   * @param buildingBlock
   * @return the building block with the "before delete" actions performed on it
   */
  protected E onBeforeDelete(E buildingBlock) {
    // override to add actions to be performed before deletion
    return buildingBlock;
  }

  /**
   * Is called in {@link #deleteEntity(BuildingBlock)} right after the actual deletion.
   * @param buildingBlock
   */
  protected void onAfterDelete(E buildingBlock) {
    // override to add actions to be performed after deletion
  }

  /**
   * Gathers all descendants of the given building block (in case it is a {@link HierarchicalEntity})
   * together with the building block itself.
   * Then calls {@link TimeseriesService#deleteTimeseriesByBuildingBlocks(Collection)}
   * to clean up timeseries which would be orphaned after the building blocks is deleted.
   * This is necessary because deleting a hierarchical entity causes the deletion of all
   * descendants as well.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void deleteRelatedTimeseries(E buildingBlock) {
    Set<E> willBeDeleted = Sets.newHashSet();
    if (buildingBlock instanceof HierarchicalEntity<?>) {
      HierarchicalEntity hierarchicalEntity = (HierarchicalEntity) buildingBlock;
      hierarchicalEntity.getDescendants(hierarchicalEntity, willBeDeleted);
    }
    willBeDeleted.add(buildingBlock);
    timeseriesService.deleteTimeseriesByBuildingBlocks(willBeDeleted);
  }

  /**
   * Returns the instance of the {@link AttributeValueService}. Used to save or delete
   * the attribute values.
   * 
   * @return the instance of the attribute value service
   */
  protected AttributeValueService getAttributeValueService() {
    return attributeValueService;
  }

  protected GeneralBuildingBlockService getGeneralBuildingBlockService() {
    return generalBuildingBlockService;
  }

  protected void checkDeletePermissionsCurrentPerms(E buildingBlock) {
    UserContext.getCurrentPerms().assureBbDeletePermission(buildingBlock);
  }

  /** {@inheritDoc} */
  public List<E> findByNames(Set<String> names) {
    return getDao().findByNames(names);
  }

  /**{@inheritDoc}**/
  public Collection<E> getSubscribedElements() {
    return this.getDao().getSubscribedElements();
  }

  /**{@inheritDoc}**/
  @Override
  public final E saveOrUpdate(E entity) {
    return saveOrUpdate(entity, true);
  }

  /**{@inheritDoc}**/
  public E saveOrUpdate(E entity, boolean cleanup) {
    return super.saveOrUpdate(entity);
  }
}