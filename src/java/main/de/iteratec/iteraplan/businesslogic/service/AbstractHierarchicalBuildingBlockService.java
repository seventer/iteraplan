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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * Abstract service class for all entities of type {@link AbstractBuildingBlockService}.
 * 
 * @param <E>
 *          The type parameter for the concrete hierarchical building block.
 */
public abstract class AbstractHierarchicalBuildingBlockService<E extends AbstractHierarchicalEntity<E>> extends AbstractBuildingBlockService<E>
    implements HierarchicalBuildingBlockService<E, Integer> {

  private static final Logger LOGGER = Logger.getIteraplanLogger(AbstractHierarchicalBuildingBlockService.class);

  /** {@inheritDoc} */
  public List<E> getAvailableChildren(E source, List<E> elementsToExclude) {
    Set<Integer> set = new HashSet<Integer>();
    Integer id = source.getId();

    // The ID is null if and only if a new element is created.
    if (id != null) {
      // Add the ID of the current element.
      set.add(source.getId());

      // Add the IDs of elements to exclude.
      if (elementsToExclude != null && elementsToExclude.size() > 0) {
        set.addAll(GeneralHelper.createIdSetFromIdEntities(elementsToExclude));
      }

      // Add the IDs of the element's parents (top-level element included).
      E parent = source.getParentElement();
      while (parent != null) {
        set.add(parent.getId());
        parent = parent.getParentElement();
      }
    }
    else {
      // Add just the ID of the top-level element.
      set.add(getFirstElement().getId());
      // Add the IDs of elements to exclude.
      if (elementsToExclude != null && elementsToExclude.size() > 0) {
        set.addAll(GeneralHelper.createIdSetFromIdEntities(elementsToExclude));
      }
    }

    List<E> list = loadFilteredElementList(set);
    Collections.sort(list, new HierarchicalEntityCachingComparator<E>());

    return list;
  }

  /** {@inheritDoc} */
  public List<E> getAvailableParents(Integer id) {
    List<E> list = loadElementList();

    if (id != null) {
      E item = loadObjectById(id);
      list.remove(item);
      HierarchyHelper.removeCycleElementsFromElementOfList(list, item);
    }

    Collections.sort(list, new HierarchicalEntityCachingComparator<E>());

    return list;
  }

  /** {@inheritDoc} */
  public List<E> getEntitiesFiltered(List<E> elementsToExclude, boolean includeRoot) {
    Set<Integer> set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);
    List<E> list = loadFilteredElementList(set);

    // If the root is not be included, remove it from the list.
    if (!includeRoot) {
      E root = null;
      for (E e : list) {
        if (e.isTopLevelElement()) {
          root = e;
          break;
        }
      }

      list.remove(root);
    }

    return list;
  }

  private void checkForCircularParentChildReleations(E entity) {
    final List<Integer> visitedElements = Lists.newArrayList();
    E parentElement = entity.getParentElement();

    if (parentElement != null) {
      visitedElements.add(entity.getParentElement().getId());
    }

    for (E child : entity.getChildrenAsList()) {
      final E root = loadObjectById(child.getId());
      checkIfChildrenContain(root, visitedElements);
    }
  }

  private void checkIfChildrenContain(E root, List<Integer> visitedElements) {
    for (E child : root.getChildrenAsList()) {
      E loadedChild = loadObjectById(child.getId());
      if (visitedElements.contains(loadedChild.getId())) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.CIRCULAR_PARENT_CHILD_CONNECTIONS);
      }

      visitedElements.add(loadedChild.getId());
      if (loadedChild.getChildrenAsList().size() > 0) {
        checkIfChildrenContain(loadedChild, visitedElements);
      }
    }
  }

  private void checkParentChildElementsNotEqual(E entity) {
    E parentElement = entity.getParentElement();

    if (parentElement == null) {
      return;
    }

    List<E> children = entity.getChildrenAsList();
    for (E child : children) {
      if (child.getId().equals(parentElement.getId())) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.PARENT_CAN_NOT_BE_CHILD_ELEMENT);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public E saveOrUpdate(E entity, boolean cleanup) {

    boolean funcPerm = UserContext.getCurrentUserContext().getPerms()
        .getUserHasBbTypeFunctionalPermission(entity.getTypeOfBuildingBlock().getValue());
    boolean createPerm = UserContext.getCurrentUserContext().getPerms().getUserHasBbTypeCreatePermission(entity.getTypeOfBuildingBlock().getValue());
    boolean updatePerm = UserContext.getCurrentUserContext().getPerms().getUserHasBbTypeUpdatePermission(entity.getTypeOfBuildingBlock().getValue());

    if (funcPerm && (createPerm || updatePerm)) {
      purgeChildrenFromNullAndDoubleEntries(entity);
      checkParentChildElementsNotEqual(entity);
      checkForCircularParentChildReleations(entity);

      return super.saveOrUpdate(entity, cleanup);
    }
    else {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }

  }

  /**
   * Removes null-entries and duplicate entries from the entity's children-list.
   * Necessary to avoid database-corruption (not consecutively enumerated POS column).
   * @param entity
   */
  private void purgeChildrenFromNullAndDoubleEntries(E entity) {
    Set<E> children = entity.getChildrenAsSet();
    if (children.size() != entity.getChildren().size() || children.contains(null)) {
      if (children.removeAll(Collections.singletonList(null))) {
        LOGGER.warn("Null-entry in children-list of \"{0}\" (Type: {1}, ID: {2}) found! Cleaning up.", entity.getName(), entity.getClass().getName(),
            entity.getId());
      }
      entity.getChildren().clear();
      entity.getChildren().addAll(children);
    }
  }

  @Override
  protected void checkDelete(E bb) {
    super.checkDelete(bb);

    // top-level element must not be deleted
    Integer bbId = bb.getId();
    E root = getDao().getFirstElement();
    if (bbId.equals(root.getId())) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CANNOT_DELETE_VIRTUAL_ELEMENT, new Object[] { root.getNonHierarchicalName() });
    }
  }

  @Override
  protected E onBeforeDelete(E buildingBlock) {
    super.onBeforeDelete(buildingBlock);
    deleteRelatedTimeseries(buildingBlock);
    return buildingBlock;
  }

  /** {@inheritDoc} */
  public List<E> getEntityResultsBySearch(E entity) {
    return getDao().findBySearchTerm(entity.getName(), "name");
  }

  /**{@inheritDoc}**/
  public boolean saveReorderMove(final Integer sourceId, final Integer destId, boolean insertAfter) {
    E sourceEntity = getDao().loadObjectById(sourceId);
    E destEntity = getDao().loadObjectById(destId);

    if (sourceEntity == null || destEntity == null) {
      LOGGER.error("Can't read source and/or destination element for reordering."); // TODO  throw new IteraplanBusinessException();
      return false;
    }

    // get all children of destination entity's parent, including the destination entity itself (sorted by position)
    E destParent = destEntity.getParent();
    List<E> allDestChildren = destParent.getChildrenAsList();
    // TODO verify if there is no need to use a comparator because hibernate sorts by position by default
    // using the comparator leads to NPEs if the BB is newly created
    //    Collections.sort(allDestChildren, new ByPositionComparator());

    // get all children of source entity's parent, and remove the source entity from this collection.
    E sourceParent = sourceEntity.getParent();
    sourceEntity.removeFromParent(sourceParent);

    int destIndex = Iterables.indexOf(allDestChildren, new Predicate<E>() {
      public boolean apply(E input) {
        return input.getId().equals(destId);
      }
    });

    if (destIndex < 0) {
      LOGGER.error("Can't determine index of destination for reordering."); // TODO  throw new IteraplanBusinessException();
      return false;
    }

    int insertAt = destIndex + (insertAfter ? 1 : 0);
    LOGGER.debug("Insert <" + sourceEntity.getNonHierarchicalName() + "> after <" + destEntity.getNonHierarchicalName() + "> at index: " + insertAt);
    if (insertAt >= allDestChildren.size()) {
      allDestChildren.add(sourceEntity);
    }
    else {
      allDestChildren.add(insertAt, sourceEntity);
    }
    sourceEntity.setParent(destParent);

    saveOrUpdate(sourceParent);
    if (!sourceParent.equals(destParent)) {
      saveOrUpdate(destParent);
    }

    return true;
  }
  //
  //  private static class ByPositionComparator implements Comparator<AbstractHierarchicalEntity<?>>, Serializable {
  //    private static final long serialVersionUID = 8946480332109275325L;
  //
  //    /**{@inheritDoc}**/
  //    public int compare(AbstractHierarchicalEntity<?> o1, AbstractHierarchicalEntity<?> o2) {
  //      return o1.getPosition().compareTo(o2.getPosition());
  //    }
  //  }
}
