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
package de.iteratec.iteraplan.model.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.interfaces.GeneralisationSpecialisationEntity;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.UsageEntity;
import de.iteratec.iteraplan.model.user.Role;


/**
 * Helper methods for hierarchical entities. These methods revolve around the interfaces
 * HierarchicalCoreEntity, SpecialisationEntity, UsageEntity and HierarchicalEntityNM.
 */
public final class HierarchyHelper {

  private HierarchyHelper() {
    // no instance needed. Only providing static methods
  }

  private static final Logger LOGGER = Logger.getIteraplanLogger(HierarchyHelper.class);

  /**
   * Implementation of compareTo() for ordered hierarchical entities. Compares two hierarchical
   * entities. If they have the same father, the ordering is determined by the position in the
   * fathers list. If they have different fathers, the ordering is determined by the first common
   * father node on the way to the root node. The root node has no father and is smaller than all
   * other hierarchical entities.
   * 
   * @param <T>
   *          Some ordered HierarchicalEntity type.
   * @param thisInstance
   *          The first instance to compare.
   * @param otherInstance
   *          The second instance to compare.
   * @return -1, if thisInstance is smaller compared to otherInstance, 0, if thisInstance is equal
   *         to otherInstance +1, if thisInstance is greater compared to otherInstance.
   */
  public static <T extends HierarchicalEntity<T>> int compareToForOrderedHierarchy(T thisInstance, T otherInstance) {
    if (thisInstance.equals(otherInstance)) {
      return 0;
    }
    else if (thisInstance.getParentElement() == null) {
      return -1;
    }
    else if (otherInstance.getParentElement() == null) {
      return +1;
    }
    else if (thisInstance.getParentElement().equals(otherInstance.getParentElement())) {
      T commonAncestor = thisInstance.getParentElement();
      return indexOfChildInConsistsOfList(commonAncestor, thisInstance) - indexOfChildInConsistsOfList(commonAncestor, otherInstance);
    }
    else {
      List<T> currentHierarchy = createHierarchyList(thisInstance);
      List<T> otherHierarchy = createHierarchyList(otherInstance);

      int indexOfCommonAncestor = getIndexOfCommonAncestor(currentHierarchy, otherHierarchy);

      if (currentHierarchy.size() == indexOfCommonAncestor + 1) {
        return -1;
      }
      else if (otherHierarchy.size() == indexOfCommonAncestor + 1) {
        return +1;
      }
      else {
        T commonAncestor = currentHierarchy.get(indexOfCommonAncestor);
        return indexOfChildInConsistsOfList(commonAncestor, currentHierarchy.get(indexOfCommonAncestor + 1))
            - indexOfChildInConsistsOfList(commonAncestor, otherHierarchy.get(indexOfCommonAncestor + 1));

      }
    }
  }

  private static <T> int getIndexOfCommonAncestor(List<T> currentHierarchy, List<T> otherHierarchy) {
    int minHierarchySize = Math.min(currentHierarchy.size(), otherHierarchy.size());

    int indexOfCommonAncestor = 0;
    for (int i = 0; i < minHierarchySize; i++) {
      T currentAncestor = currentHierarchy.get(i);
      T otherAncestor = otherHierarchy.get(i);
      if (currentAncestor.equals(otherAncestor)) {
        indexOfCommonAncestor = i;
      }
      else {
        break;
      }
    }
    return indexOfCommonAncestor;
  }

  public static <T extends HierarchicalEntity<T>> List<T> createHierarchyList(T instance) {
    List<T> hierarchyList = new ArrayList<T>();
    hierarchyList.add(instance);
    T parent = instance.getParentElement();
    while (parent != null) {
      hierarchyList.add(0, parent);
      parent = parent.getParentElement();
    }
    return hierarchyList;
  }

  /**
   * Returns all super entities in the 1:n hierarchy for the given HierarchicalCoreEntity.
   * 
   * @param entity
   * @return Set of HierarchicalCoreEntity instances.
   */
  public static <T extends HierarchicalEntity<T>> Set<T> getSuperEntities(T entity) {
    Set<T> result = new HashSet<T>();
    T parent = entity.getParentElement();
    while (parent != null) {
      result.add(parent);
      parent = parent.getParentElement();
    }
    return result;
  }

  /**
   * @param role the role to consider
   * @return True if the direct sub-entities of the given entity occur somewhere in the set of
   *         super-entities in the hierarchy.
   */
  public static boolean hasAggregationCycleNM(Role role) {
    if (role == null || role.getConsistsOfRoles().isEmpty() || role.getElementOfRoles().isEmpty()) {
      return false;
    }

    Set<Role> superEntitiesComplete = getSuperEntities(role, new HashSet<Role>());
    Set<Role> subEntities = role.getConsistsOfRoles();
    for (Role superEntity : superEntitiesComplete) {
      for (Role subEntity : subEntities) {
        if (superEntity.getId().equals(subEntity.getId())) {
          return true;
        }
      }
    }

    return false;
  }

  private static Set<Role> getSuperEntities(Role entity, Set<Role> currentSet) {
    currentSet.addAll(entity.getElementOfRoles());
    for (Iterator<Role> it = entity.getElementOfRoles().iterator(); it.hasNext();) {
      Role superEntity = it.next();
      getSuperEntities(superEntity, currentSet);
    }
    return currentSet;
  }

  /**
   * Checks if the current HierarchicalCoreEntity contains an element-of hierarchy cycles.
   * 
   * @param current
   *          The HierarchicalCoreEntity for which the element-of hierarchy should be checked.
   * @return true, if an element-of hierarchy cycle has been detected.
   */
  public static boolean hasElementOfCycle(HierarchicalEntity<?> current) {
    if (current == null || current.getId() == null) {
      return false;
    }
    HierarchicalEntity<?> parent = current.getParentElement();
    while (parent != null) {
      // if the id is not set yet, we can't be sure if this is a cycle or not, so we are optimistic
      // and return false
      if (parent.getId() == null) {
        return false;
      }
      if (parent.getId().equals(current.getId())) {
        return true;
      }
      parent = parent.getParentElement();
    }
    return false;
  }

  /**
   * Checks if the current SpecialisationEntity contains a generalisation hierarchy cycle.
   * 
   * @param current
   *          The SpecialisationEntity for which the generalisation hierarchy should be checked.
   * @return true, if an generalisation hierarchy cycle has been detected.
   */
  public static <T extends GeneralisationSpecialisationEntity<T>> boolean hasGeneralisationCycle(T current) {
    if (current == null || current.getId() == null) {
      return false;
    }
    T parent = current.getGeneralisation();
    while (parent != null) {
      if (current.getId().equals(parent.getId())) {
        return true;
      }
      parent = parent.getGeneralisation();
    }
    return false;
  }

  /**
   * Checks if the current Sequence contains a predecessor hierarchy cycle.
   * 
   * @param current
   *          The {@link Sequence} for which the predecessor hierarchy should be checked.
   * @throws IteraplanBusinessException if an predecessorCycle is detected
   */
  public static <T extends Sequence<T>> boolean hasPredecessorCycle(T current) {
    if (current == null) {
      return false;
    }
    if (current.getId() == null) {
      // An ID is required. Create a fictitious ID (-1).
      current.setId(Integer.valueOf(-1));
    }
    try {
      if (isIdContainedInPredecessors(current.getPredecessors(), current.getId())) {
        LOGGER.error("Predecessor Cycle detected for BB: {0}", current.getIdentityString());
        return true;
      }
      return false;
    } finally {
      if (Integer.valueOf(-1).equals(current.getId())) {
        //To avoid problems, set back again fictitious ID (-1).
        current.setId(null);
      }
    }
  }

  /**
   * Checks if the current Sequence contains a successor hierarchy cycle.
   * 
   * @param current
   *          The {@link Sequence} for which the successor hierarchy should be checked.
   * @throws IteraplanBusinessException if an predecessorCycle is detected
   */
  public static <T extends Sequence<T>> boolean hasSuccessorCycle(T current) {
    if (current == null) {
      return false;
    }
    if (current.getId() == null) {
      // An ID is required. Create a fictitious ID (-1).
      current.setId(Integer.valueOf(-1));
    }
    try{
      if (isIdContainedInSuccessors(current.getSuccessors(), current.getId())) {
        LOGGER.error("Successor Cycle detected for BB: {0}", current.getIdentityString());
        return true;
      }
      return false;
    } finally {
      if (Integer.valueOf(-1).equals(current.getId())) {
        //To avoid problems, set back again fictitious ID (-1).
        current.setId(null);
      }
    }
  }

  /**
   * Recursively checks all predecessors if they contain an element with the given id.
   * 
   * @param predecessors
   *          Set of predecessors to check
   * @param id
   *          id to check for
   * @return true if the id is contained within the predecessor hierarchy
   */
  public static <T extends Sequence<T>> boolean isIdContainedInPredecessors(Set<T> predecessors, Integer id) {
    for (T predecessor : predecessors) {
      if (id.equals(predecessor.getId())) {
        return true;
      }
      if (isIdContainedInPredecessors(predecessor.getPredecessors(), id)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Recursively checks all predecessors if they contain an element with the given id.
   * 
   * @param successors
   *          Set of predecessors to check
   * @param id
   *          id to check for
   * @return true if the id is contained within the predecessor hierarchy
   */
  public static <T extends Sequence<T>> boolean isIdContainedInSuccessors(Set<T> successors, Integer id) {
    for (T successor : successors) {
      if (id.equals(successor.getId())) {
        return true;
      }
      if (isIdContainedInSuccessors(successor.getSuccessors(), id)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the current UsageEntity contains a parent component hierarchy cycle.
   * 
   * @param current
   *          The UsageEntity for which the parent component hierarchy should be checked.
   * @return true, if an parent component hierarchy cycle has been detected.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static boolean hasParentComponentCycle(UsageEntity current) {
    if (current == null || current.getId() == null) {
      return false;
    }
    return isIdContainedInParentComponents(current.getParentComponents(), current.getId());
  }

  /**
   * Checks if the current UsageEntity contains a base component hierarchy cycle.
   * 
   * @param current
   *          The UsageEntity for which the base component hierarchy should be checked.
   * @return true, if an base component hierarchy cycle has been detected.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static boolean hasBaseComponentCycle(UsageEntity current) {
    if (current == null || current.getId() == null) {
      return false;
    }
    return isIdContainedInBaseComponents(current.getBaseComponents(), current.getId());
  }

  /**
   * Recursively checks all base components if they contain an element with the given id.
   * 
   * @param parentComponents
   *          Set of base components to check
   * @param id
   *          id to check for
   * @return true if the id is contained within the base component hierarchy
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static boolean isIdContainedInParentComponents(Set<UsageEntity> parentComponents, Integer id) {
    for (UsageEntity parentComponent : parentComponents) {
      if (id.equals(parentComponent.getId())) {
        return true;
      }
      if (isIdContainedInParentComponents(parentComponent.getParentComponents(), id)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Recursively checks all base components if they contain an element with the given id.
   * 
   * @param baseComponents
   *          Set of base components to check
   * @param id
   *          id to check for
   * @return true if the id is contained within the base component hierarchy
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static boolean isIdContainedInBaseComponents(Set<UsageEntity> baseComponents, Integer id) {
    for (UsageEntity baseComponent : baseComponents) {
      if (id.equals(baseComponent.getId())) {
        return true;
      }
      if (isIdContainedInBaseComponents(baseComponent.getBaseComponents(), id)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Builds a hierarchical name for the given HierarchicalEntity.
   * 
   * @param entity
   *          The entity for which the hierarchical name should be created.
   * @param detailedName
   *          Iff true, the full hierarchical name is returned. Else, a short version is returned.
   * @return The hierarchical name where each part is separated by an appropriate separator symbol.
   */
  public static String makeHierarchicalName(HierarchicalEntity<?> entity, boolean detailedName) {
    List<String> stringsToConcatenate = Lists.newArrayList(entity.getNonHierarchicalName());

    if (entity.getId() != null) {
      HierarchicalEntity<?> parent = entity.getParentElement();
      while (parent != null) {
        final HierarchicalEntity<?> parentElement = parent.getParentElement();
        if (parentElement != null || detailedName) {
          stringsToConcatenate.add(parent.getNonHierarchicalName());
        }
        parent = parentElement;
      }
    }
    else if (!"".equals(entity.getNonHierarchicalName())) {
      stringsToConcatenate.add("?");
    }
    Collections.reverse(stringsToConcatenate);

    return GeneralHelper.makeConcatenatedStringWithSeparator(stringsToConcatenate, Constants.HIERARCHYSEP);
  }

  /**
   * Removes all those elements from the given List of proposed element-of HierarchicalCoreEntities
   * that would lead to a cycle when used as element-of HierarchicalCoreEntity of the given current
   * HierarchicalCoreEntity.
   * 
   * @param parents
   *          The List of proposed element-of HierarchicalCoreEntities.
   * @param current
   *          The HierarchicalCoreEntity for which the elements in the given List should be
   *          cycle-checked.
   */
  public static <T extends HierarchicalEntity<T>> void removeCycleElementsFromElementOfList(List<T> parents, T current) {
    Set<T> children = current.getChildrenAsSet();
    if (children != null && !children.isEmpty()) {
      parents.removeAll(children);
      for (T newCurrent : children) {
        removeCycleElementsFromElementOfList(parents, newCurrent);
      }
    }
  }

  /**
   * Removes all those elements from the given List of proposed generalisation
   * SpecialisationEntities that would lead to a cycle when used as generalisation
   * SpecialisationEntity of the given current SpecialisationEntity.
   * 
   * @param generalisations
   *          The List of proposed generalisation SpecialisationEntities.
   * @param current
   *          The SpecialisationEntity for which the elements in the given List should be
   *          cycle-checked.
   */
  public static <T extends GeneralisationSpecialisationEntity<T>> void removeCycleElementsFromGeneralisationList(List<T> generalisations, T current) {
    Set<T> specialisations = current.getSpecialisations();
    if (specialisations != null && !specialisations.isEmpty()) {
      generalisations.removeAll(specialisations);
      for (Iterator<T> it = specialisations.iterator(); it.hasNext();) {
        T newCurrent = it.next();
        removeCycleElementsFromGeneralisationList(generalisations, newCurrent);
      }
    }
  }

  /**
   * Adds a base component. Updates both sides of the association.
   * @param target
   *          {@link UsageEntity} to add the base component to
   * @param baseComponent
   *          base component to add
   * @param errorCode
   *          errorCode for exception in case of cyle
   */
  public static <T extends UsageEntity<T>> void addBaseComponent(T target, T baseComponent, int errorCode) {
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(baseComponent);

    final boolean addedToBaseComponents = target.getBaseComponents().add(baseComponent);
    final boolean addedToParentComponents = baseComponent.getParentComponents().add(target);
    // perform cycle check to validate that the hierarchy is still in a consistent state
    // this is explicitly done _after_ the new parent/base relation has been set
    if (HierarchyHelper.hasParentComponentCycle(target)) {
      LOGGER.error("Can't set base component of " + target.getIdentityString() + " to " + baseComponent.getIdentityString());
      // revert to the old, consistent state
      if (addedToBaseComponents) {
        target.getBaseComponents().remove(baseComponent);
      }
      if (addedToParentComponents) {
        baseComponent.getParentComponents().remove(target);
      }
      throw new IteraplanBusinessException(errorCode);
    }
  }

  /**
   * Adds a parent component. Updates both sides of the association.
   * @param target
   *          {@link UsageEntity} to add the parent component to
   * @param parentComponent
   *          parent component to add
   * @param errorCode
   *          errorCode for exception in case of cyle
   */
  public static <T extends UsageEntity<T>> void addParentComponent(T target, T parentComponent, int errorCode) {
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(parentComponent);

    final boolean addedToParentComponents = target.getParentComponents().add(parentComponent);
    final boolean addedToBaseComponents = parentComponent.getBaseComponents().add(target);
    // perform cycle check to validate that the hierarchy is still in a consistent state
    // this is explicitly done _after_ the new parent/base relation has been set
    if (HierarchyHelper.hasBaseComponentCycle(target)) {
      LOGGER.error("Can't set parent component of " + target.getIdentityString() + " to " + parentComponent.getIdentityString());
      // revert to the old, consistent state
      if (addedToParentComponents) {
        target.getParentComponents().remove(parentComponent);
      }
      if (addedToBaseComponents) {
        parentComponent.getBaseComponents().remove(target);
      }
      throw new IteraplanBusinessException(errorCode);
    }
  }

  /**
   * Adds a predecessor. Updates both sides of the association.
   * @param target
   *          {@link Sequence} to add the predecessor to
   * @param predecessor
   *          predecessor to add
   * @param errorCode
   *          errorCode for exception in case of cyle
   */
  public static <T extends Sequence<T>> void addPredecessor(T target, T predecessor, int errorCode) {
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(predecessor);

    final boolean addedToPredecessors = target.getPredecessors().add(predecessor);
    final boolean addedToSuccessors = predecessor.getSuccessors().add(target);
    // perform cycle check to validate that the hierarchy is still in a consistent state
    // this is explicitly done _after_ the new predecessor/successor relation has been set
    if (HierarchyHelper.hasSuccessorCycle(target)) {
      LOGGER.error("Can't set predecessor " + target.getIdentityString() + " to " + predecessor.getIdentityString());
      // revert to the old, consistent state
      if (addedToPredecessors) {
        target.getPredecessors().remove(predecessor);
      }
      if (addedToSuccessors) {
        predecessor.getSuccessors().remove(target);
      }
      throw new IteraplanBusinessException(errorCode);
    }
  }

  /**
   * Adds a successor. Updates both sides of the association.
   * @param target
   *          {@link Sequence} to add the successor to
   * @param successor
   *          successor to add
   * @param errorCode
   *          errorCode for exception in case of cyle
   */
  public static <T extends Sequence<T>> void addSuccessor(T target, T successor, int errorCode) {
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(successor);

    final boolean addedToSuccessors = target.getSuccessors().add(successor);
    final boolean addedToPredecessors = successor.getPredecessors().add(target);
    // perform cycle check to validate that the hierarchy is still in a consistent state
    // this is explicitly done _after_ the new predecessor/successor relation has been set
    if (HierarchyHelper.hasPredecessorCycle(target)) {
      LOGGER.error("Can't set successor " + target.getIdentityString() + " to " + successor.getIdentityString());
      // revert to the old, consistent state
      if (addedToSuccessors) {
        target.getSuccessors().remove(successor);
      }
      if (addedToPredecessors) {
        successor.getPredecessors().remove(target);
      }
      throw new IteraplanBusinessException(errorCode);
    }
  }

  /**
   * Helper method to make comparisons easier. Determines the index of the given child element in
   * the parent elements list of consists-of elements.
   * 
   * @param <T>
   *          Some ordered HierarchicalEntity type.
   * @param parent
   *          The supposed parent element
   * @param child
   *          The assumed child element.
   * @return Iff the child can not be found of the parent's consists of list is null, -1 is
   *         returned. Else, the index of the child in the parent's list is returned.
   */
  private static <T extends HierarchicalEntity<T>> int indexOfChildInConsistsOfList(T parent, T child) {
    if (parent.getChildrenAsList() == null) {
      return -1;
    }
    return parent.getChildrenAsList().indexOf(child);
  }
}