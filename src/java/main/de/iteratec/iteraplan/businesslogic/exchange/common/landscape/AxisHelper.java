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
package de.iteratec.iteraplan.businesslogic.exchange.common.landscape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.AxisElement;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.ContentElement;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BuildingBlockHierarchy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.HierarchicalType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.common.util.HashBucketMatrix;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.interfaces.IdentityStringEntity;


/**
 * Converts building blocks to a AxisElement hierarchy.
 * 
 * @see AxisElement
 */
public class AxisHelper<CE extends ContentElement<?>> {

  private static final Logger                                        LOGGER              = Logger.getIteraplanLogger(AxisHelper.class);

  /** The generated AxisElement hierarchy. */
  private final List<AxisElement<CE>>                                axisElementTree;

  /** The maximum level (depth) of the generated tree. */
  private int                                                        maxLevel;
  /** Maps id to BuildingBlock. Contains only BuildingBlocks that were passed to this class. */
  private final Map<Integer, IdentityEntity>                         idToBb              = CollectionUtils.hashMap();
  /** AxisElements with a level lower than topLevel are omitted. */
  private int                                                        topLevel            = 1;
  /** AxisElements with a level higher than bottomLevel are merged with their parent. */
  private int                                                        bottomLevel         = 2;
  /** Stores BuildingBlocks that were omitted due to specified topLevel. */
  // private HashSet<A> omittedBbs = new HashSet<A>();
  /**
   * Maps a IdentityStringEntity to a list of AxisElements (can be more than one due to N:M
   * hierarchy).
   */
  private final HashBucketMap<IdentityStringEntity, AxisElement<CE>> bbToAxisElementList = new HashBucketMap<IdentityStringEntity, AxisElement<CE>>();

  /**
   * @param resultElements
   *          The elements that were selected for the side axis.
   * @param topLevel
   *          The top most level that is to be displayed in the axis. Only relevant for hierarchical
   *          elements.
   * @param bottomLevel
   *          The bottom most level that is to be displayed in the axis. Only relevant for
   *          hierarchical elements.
   * @param elementType
   *          The type of the building blocks on the axis. If null attributes are assumed.
   * @param unspecifiedElement
   *          if not null, this element will be added as last of this axis
   */
  public AxisHelper(final List<IdentityEntity> resultElements, int topLevel, int bottomLevel, Type<?> elementType, IdentityEntity unspecifiedElement) {
    LOGGER.debug("Creating AxisHelper for elementType: {0}", elementType);
    if (resultElements.isEmpty()) {
      LOGGER.error("Trying to create axis without elements.");
      throw new IteraplanBusinessException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    final List<IdentityEntity> elements = new ArrayList<IdentityEntity>(resultElements);
    elements.remove(unspecifiedElement);

    this.axisElementTree = new ArrayList<AxisElement<CE>>();
    this.topLevel = topLevel;
    this.bottomLevel = bottomLevel;

    if (elementType instanceof HierarchicalType<?>) {
      // if we have a hierarchy, we just use the first - TODO should be user's choice
      final HierarchicalType<?> hierarchicalType = (HierarchicalType<?>) elementType;
      final BuildingBlockHierarchy<?> hierarchy = hierarchicalType.getHierarchies().get(0);
      // prepare id map
      for (IdentityEntity element : elements) {
        idToBb.put(element.getId(), element);
      }
      createTree(hierarchy, unspecifiedElement);
    }
    else {
      createList(elements);
    }
    if (unspecifiedElement != null) {
      final AxisElement<CE> predecessor = axisElementTree.isEmpty() ? null : axisElementTree.get(axisElementTree.size() - 1);
      final AxisElement<CE> unspecifiedAxisElement = createAxisElement(unspecifiedElement, null, topLevel, true, predecessor);

      bbToAxisElementList.add(unspecifiedElement, unspecifiedAxisElement);
      axisElementTree.add(unspecifiedAxisElement);
    }
  }

  private void createList(List<IdentityEntity> resultElements) {
    AxisElement<CE> predecessor = null;
    for (IdentityEntity entity : resultElements) {
      final AxisElement<CE> newAxisElement = createAxisElement(entity, null, 1, true, predecessor);
      bbToAxisElementList.add(entity, newAxisElement);
      axisElementTree.add(newAxisElement);
      predecessor = newAxisElement;
    }
  }

  /**
   * This method converts the BuildingBlocks to AxisElements and creates a tree represented by the
   * list axisElementTree. After this method completes, the elements in the tree will have an
   * incorrect level, iff top levels had to be cut off due to topLevel setting. This is corrected
   * when calling the mergeBottomAxisElements method, which needs these levels to see which elements
   * need to be merged together, depending on the bottomLevel setting.
   * 
   * @param hierarchy
   * @throws de.iteratec.iteraplan.common.error.IteraplanException
   *           If the generated tree is empty, an exception will be thrown.
   */
  private void createTree(BuildingBlockHierarchy<?> hierarchy, IdentityEntity unspecifiedElement) {
    deepSearch(null, hierarchy, new HashMap<Integer, AxisElement<CE>>());
    removeTopLevelDuplicates();
    if (axisElementTree.isEmpty() && unspecifiedElement == null) {
      // the axis has no elements!
      LOGGER.error("Trying to create axis without elements.");
      throw new IteraplanBusinessException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  /**
   * Creates a tree of AxisElement for the BuildingBlock contained in the given AxisElement.
   * AxisElements, that are above the topLevel are not part of the generated tree. After this method
   * is done, the given AxisElement will be the root node of the tree.
   * 
   * @param axisElement
   *          The current element for which to do the deep search.
   * @param hierarchy
   * @param levelPredecessors
   *          Maps from a level (Integer) to the current predecessor on that level. Needed when
   *          levels are omitted due to topLevel settings.
   * @throws de.iteratec.iteraplan.common.error.IteraplanTechnicalException
   *           If the database is not online, or if it has not been initialized.
   */
  private void deepSearch(AxisElement<CE> axisElement, BuildingBlockHierarchy<?> hierarchy, Map<Integer, AxisElement<CE>> levelPredecessors) {
    final List<IdentityEntity> currentElements = getCurrentElements(axisElement, hierarchy);
    if (!currentElements.isEmpty()) {

      AxisElement<CE> predecessor = null;
      for (IdentityEntity childBb : currentElements) {
        if (childBb == null) {
          continue;
        }

        final boolean isPartOfResultSet = idToBb.get(childBb.getId()) != null;
        final int level = getLevel(axisElement);

        final AxisElement<CE> childAxisElement = createAxisElement(childBb, axisElement, level, isPartOfResultSet, predecessor);
        deepSearch(childAxisElement, hierarchy, levelPredecessors);
        if (isNeitherPlaceholderNorResult(childAxisElement)) {
          continue;
        }
        // if below or equal top level, add to map
        if (level >= topLevel) {
          bbToAxisElementList.add(childBb, childAxisElement);
        }
        // otherwise add to omitted list.
        else {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("omitted element: " + childBb.getIdentityString());
          }
          // omittedBbs.add(childBb);
        }
        // if top level, add the element to the top level list as well.
        if (level == topLevel) {
          addToTopLevelList(levelPredecessors, level, childAxisElement);
        }

        predecessor = childAxisElement;
        levelPredecessors.put(Integer.valueOf(level), childAxisElement);
      }
    }
  }

  private void addToTopLevelList(Map<Integer, AxisElement<CE>> levelPredecessors, int level, AxisElement<CE> childAxisElement) {
    childAxisElement.setParent(null);
    childAxisElement.setPredecessor(levelPredecessors.get(Integer.valueOf(level)));
    axisElementTree.add(childAxisElement);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Adding element to top level: " + childAxisElement);
    }
  }

  private boolean isNeitherPlaceholderNorResult(AxisElement<CE> childAxisElement) {
    if (!childAxisElement.isMarkedAsVisible() && !childAxisElement.isInResultSet()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Skipping '{0}' because it's neither placeholder, nor result.", childAxisElement.getElement().getIdentityString());
      }
      final AxisElement<CE> parent = childAxisElement.getParent();
      if (parent != null) {
        parent.getChildren().remove(childAxisElement);
      }
      return true;
    }
    return false;
  }

  private int getLevel(AxisElement<CE> axisElement) {
    if (axisElement == null) {
      return 1;
    }
    else {
      return axisElement.getLevel() + 1;
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private List<IdentityEntity> getCurrentElements(AxisElement<CE> axisElement, BuildingBlockHierarchy hierarchy) {
    if (axisElement == null) {
      return hierarchy.getToplevelElements();
    }
    else {
      return hierarchy.getChildren((BuildingBlock) axisElement.getElement());
    }
  }

  /**
   * Looks for top level elements that are duplicate (due to N:M hierarchy) and throws duplicates
   * and all their children (also duplicates) away.
   */
  private void removeTopLevelDuplicates() {
    final HashSet<IdentityStringEntity> bbs = new HashSet<IdentityStringEntity>();

    AxisElement<CE> predecessor = null;
    for (final Iterator<AxisElement<CE>> it = axisElementTree.iterator(); it.hasNext();) {
      final AxisElement<CE> axisEl = it.next();
      // set predecessor to the last element not removed
      axisEl.setPredecessor(predecessor);

      if (bbs.contains(axisEl.getElement())) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Removing duplicate: " + axisEl.getName());
        }

        final List<AxisElement<CE>> bbToAxisListElement = bbToAxisElementList.get(axisEl.getElement());
        if (bbToAxisListElement != null) {
          bbToAxisListElement.remove(axisEl);
        }
        it.remove();

        for (AxisElement<CE> child : axisEl.getAllChildren()) {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing duplicate: " + child.getName());
          }

          final List<AxisElement<CE>> childElement = bbToAxisElementList.get(child.getElement());
          if (childElement != null) {
            childElement.remove(child);
          }
        }
      }
      else {
        bbs.add(axisEl.getElement());
        predecessor = axisEl;
      }
    }
  }

  private AxisElement<CE> createAxisElement(IdentityEntity entity, AxisElement<CE> superBean, int level, boolean isInResultSet,
                                            AxisElement<CE> predecessor) {
    return new AxisElement<CE>(entity, superBean, level, isInResultSet, predecessor);
  }

  /**
   * Due to the removal of levels which are lower than the given topLevel, the levels of the
   * elements in the tree must be updated. This method subtracts the topLevel value from all level
   * values and calculates the max level.
   * 
   * @param treeOfAxisElements
   */
  private void calculateCorrectLevels(Collection<AxisElement<CE>> treeOfAxisElements) {
    for (AxisElement<CE> element : treeOfAxisElements) {
      element.setLevel(element.getLevel() - (topLevel - 1));
      if (element.getLevel() > maxLevel) {
        maxLevel = element.getLevel();
      }
      calculateCorrectLevels(element.getChildren());
    }
  }

  /**
   * AxisElements with a level lower than topLevel are already ignored while creating the tree. This
   * method should be called after the ContentElements have been connected to the AxisElements in
   * the axisElementTree. It will merge the AxisElements that have a level higher than bottomLevel.
   * These AxisElements are removed and their associated content elements are taken over to their
   * respective parent. Finally the correct level for all AxisElements as well as the maxLevel will
   * be calculated.
   * 
   * @param contentElements
   *          The contentElement map, which needs to be updated when merging levels.
   */
  public void mergeBottomAxisElements(HashBucketMatrix<AxisElement<CE>, AxisElement<CE>, CE> contentElements) {
    LOGGER.debug("****************************Merging bottom levels...");
    doMerge(axisElementTree, contentElements);
    calculateCorrectLevels(axisElementTree);
  }

  @SuppressWarnings("unchecked")
  private void doMerge(Collection<AxisElement<CE>> treeOfAxisElements, HashBucketMatrix<AxisElement<CE>, AxisElement<CE>, CE> contentElements) {
    if (treeOfAxisElements == null || treeOfAxisElements.isEmpty()) {
      return;
    }
    for (AxisElement<CE> el : treeOfAxisElements) {
      doMerge(el.getChildren(), contentElements);
      final AxisElement<CE> elParent = el.getParent();
      if (el.getLevel() > bottomLevel && elParent != null) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("See if {0} and {1} need merging...", el.getName(), elParent.getName());
        }
        for (final Iterator<CE> elContentIt = el.getContentElements().iterator(); elContentIt.hasNext();) {
          final CE cEl = elContentIt.next();
          if (isMergeNeeded(contentElements, elParent, elContentIt, cEl)) {
            // parent does not have the same content element -> add content element to parent
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("merging content element {0} with axis element {1}", cEl.getName(), elParent.getName());
            }
            contentElements.getBucketNotNull(cEl.getSideAxisRef(), cEl.getTopAxisRef()).remove(cEl);
            elContentIt.remove();
            addElementToParent(contentElements, elParent, cEl);
          }
        }
        el.setContentElements(new ArrayList<CE>());
      }
      cutOffBottom(el);
    }
  }

  /**
   * cuts off the element's children if they're below bottomLevel
   * @param el
   *          content element
   */
  private void cutOffBottom(AxisElement<CE> el) {
    if (el.getLevel() == bottomLevel) {
      for (AxisElement<CE> child : el.getChildren()) {
        child.setParent(null);
      }
      el.setChildren(new ArrayList<AxisElement<CE>>());
    }
  }

  @SuppressWarnings("unchecked")
  private boolean isMergeNeeded(HashBucketMatrix<AxisElement<CE>, AxisElement<CE>, CE> contentElements, AxisElement<CE> elParent,
                                Iterator<CE> elContentIt, CE cEl) {
    for (CE cElParent : elParent.getContentElements()) {
      // if parent already has same content element -> do not merge, but throw away.
      final AxisElement<?> axis = hasTheSameContentElement(elParent, cEl, cElParent);
      if (axis != null) {
        contentElements.getBucketNotNull(cEl.getSideAxisRef(), cEl.getTopAxisRef()).remove(cEl);
        axis.getContentElements().remove(cEl);
        elContentIt.remove();
        return false;
      }
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  private void addElementToParent(HashBucketMatrix<AxisElement<CE>, AxisElement<CE>, CE> contentElements, AxisElement<CE> elParent, CE cEl) {
    if (elParent.equals(cEl.getTopAxisRef().getParent())) {
      cEl.setTopAxisRef(elParent);
    }
    else if (elParent.equals(cEl.getSideAxisRef().getParent())) {
      cEl.setSideAxisRef(elParent);
    }
    contentElements.add(cEl.getSideAxisRef(), cEl.getTopAxisRef(), cEl);
    elParent.getContentElements().add(cEl);
  }

  /**
   * Checks if the content elements are equal, taking into account their associated {@link AxisElement}s
   * @param elParent
   *          associated {@link AxisElement}
   * @param cEl1
   *          first content element
   * @param cEl2
   *          second content element
   * @return the common {@link AxisElement} if the content elements are the same, null otherwise
   */
  private AxisElement<?> hasTheSameContentElement(AxisElement<CE> elParent, CE cEl1, CE cEl2) {
    if (elParent.equals(cEl1.getTopAxisRef().getParent()) && cEl1.getSideAxisRef().equals(cEl2.getSideAxisRef())
        && cEl1.getElement().equals(cEl2.getElement())) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Throwing away content element " + cEl1.getName());
      }
      return cEl1.getSideAxisRef();
    }
    else if (elParent.equals(cEl1.getSideAxisRef().getParent()) && cEl1.getTopAxisRef().equals(cEl2.getTopAxisRef())
        && cEl1.getElement().equals(cEl2.getElement())) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Throwing away content element " + cEl1.getName());
      }
      return cEl1.getTopAxisRef();
    }
    return null;
  }

  public HashBucketMap<IdentityStringEntity, AxisElement<CE>> getBbToAxisElementList() {
    return bbToAxisElementList;
  }

  public int getMaxLevel() {
    return maxLevel;
  }

  public List<AxisElement<CE>> getAxisElementTree() {
    return new ArrayList<AxisElement<CE>>(axisElementTree);
  }
}
