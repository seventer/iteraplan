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
package de.iteratec.iteraplan.presentation.memory;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.support.PagedListHolder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.presentation.dialog.GuiSearchController.SearchStrategy;
import de.iteratec.iteraplan.presentation.memory.EntityTreeNode.EntityNodeIterator;


/**
 * GUI Helper class for tree view (data structure).
 */
public class TreeViewHelper implements Serializable {

  private static final long serialVersionUID          = 3039874491088273783L;

  private static final int  PARTIAL_LOADED_ELEMENTS   = IteraplanProperties.getIntProperty(IteraplanProperties.TREE_VIEW_PARTIAL_INITIAL_ELEMENTS);
  private static final int  PARTIAL_LOADING_THRESHOLD = IteraplanProperties
                                                          .getIntProperty(IteraplanProperties.TREE_VIEW_PARTIAL_ACTIVATION_THRESHOLD);

  /**
   * Functional Helper to get a (comparable) value from an object, using a (Spring) property path.
   */
  private static class PropertyResolver implements Function<AbstractHierarchicalEntity<?>, Comparable<?>>, Serializable {

    private static final long         serialVersionUID = -278952064912521724L;

    private transient BeanWrapperImpl beanWrapper;
    private final String              property;

    PropertyResolver(String property) {
      this.property = property;
    }

    public Comparable<?> apply(AbstractHierarchicalEntity<?> input) {

      try {
        BeanWrapperImpl wrapper = getBeanWrapper();
        wrapper.setWrappedInstance(input);
        return (Comparable<?>) wrapper.getPropertyValue(property);
      } catch (Exception e) {
        return null;
      }
    }

    private BeanWrapperImpl getBeanWrapper() {
      if (this.beanWrapper == null) {
        this.beanWrapper = new BeanWrapperImpl(false);
      }
      return this.beanWrapper;
    }
  }

  /** Default ordering by internal position.  */
  private static Ordering<AbstractHierarchicalEntity<?>> defaultOrdering   = Ordering.natural().nullsFirst()
                                                                               .onResultOf(new Function<AbstractHierarchicalEntity<?>, Integer>() {

                                                                                 public Integer apply(AbstractHierarchicalEntity<?> input) {
                                                                                   return input.getPosition();
                                                                                 }
                                                                               });

  private SearchDialogMemory                             dialogMemory;

  private Ordering<AbstractHierarchicalEntity<?>>        ordering;

  /** Enable reordering by position in treeview. */
  private boolean                                        reorderingEnabled = false;

  private Map<Integer, EntityTreeNode>                   idToNodeMap;

  /** Node level. (0 = root level) */
  private int                                            currentTreeLevel;

  /** Max node level. (0 = root level) */
  private int                                            maxTreeLevel;

  private Set<Integer>                                   resultIdSet;

  private EntityTreeNode                                 rootNode;

  public TreeViewHelper() {
    this.ordering = defaultOrdering;
  }

  /**
   * Create / rebuild tree structure.
   */
  public void buildTreeModel(PagedListHolder<?> resultList, HierarchicalEntity<?> rootElement, SearchDialogMemory searchDialogMemory) {
    this.dialogMemory = searchDialogMemory;

    if (rootNode == null) {
      rootNode = new EntityTreeNode(rootElement);
    }
    else {
      rootNode.setEntity((AbstractHierarchicalEntity<?>) rootElement);
    }

    // create a set of all result ids
    resultIdSet = Sets.newHashSet();
    for (Object result : resultList.getSource()) {
      if (result instanceof IdEntity) {
        Integer id = ((IdEntity) result).getId();
        resultIdSet.add(id);
      }
    }
    resultIdSet.remove(rootElement.getId());

    // create a map of ids to nodes, to enable caching and reusing node elements in order to maintain their state between requests
    idToNodeMap = Maps.newHashMap();
    idToNodeMap.put(rootNode.getId(), rootNode);
    EntityNodeIterator nodeIterator = new EntityNodeIterator(rootNode);
    nodeIterator.addPredicate(new Predicate<EntityTreeNode>() {
      public boolean apply(EntityTreeNode input) {
        return !input.isPartialPlaceholder();
      }
    });
    while (nodeIterator.hasNext()) {
      EntityTreeNode reusableNode = nodeIterator.next();
      idToNodeMap.put(reusableNode.getId(), reusableNode);
    }

    currentTreeLevel = 0;
    maxTreeLevel = 0;

    // sorting issues
    String sortByProperty = resultList.getSort().getProperty();
    if (sortByProperty != null) {
      ordering = Ordering.natural().nullsFirst().onResultOf(new PropertyResolver(sortByProperty));
    }
    else {
      ordering = defaultOrdering;
    }

    // build tree structure of EntityTreeNodes
    recurseBuildTree(rootNode);
  }

  /**
   * Helper method to recursively build up the tree structure.
   * @param node  The root node.
   */
  private boolean recurseBuildTree(EntityTreeNode node) {
    HierarchicalEntity<?> entity = node.getEntity();

    boolean includedNodesOnLevel = false;

    node.resetChildren();
    node.resetUnloadedChildren();
    int siblingIndex = 0;

    this.currentTreeLevel++;
    this.maxTreeLevel = Math.max(maxTreeLevel, currentTreeLevel - 1);

    @SuppressWarnings("unchecked")
    List<AbstractHierarchicalEntity<?>> entityChildren = (List<AbstractHierarchicalEntity<?>>) entity.getChildrenAsList();
    Collections.sort(entityChildren, dialogMemory.getTableState().getSortDefinition().isAscending() ? ordering : ordering.reverse());

    // process children
    // check partial loading conditions on each node
    boolean partialLoading = entityChildren.size() > PARTIAL_LOADING_THRESHOLD;
    for (HierarchicalEntity<?> child : entityChildren) {

      AbstractHierarchicalEntity<?> childEntity = (AbstractHierarchicalEntity<?>) child;
      Integer childId = child.getId();

      // Create or reuse node-wrapper for entity
      EntityTreeNode childNode;
      if (idToNodeMap.containsKey(childId)) {
        childNode = idToNodeMap.remove(childId);
        childNode.setEntity(childEntity);
        childNode.setTreeLevel(currentTreeLevel);
      }
      else {
        childNode = new EntityTreeNode(child, currentTreeLevel);
      }

      childNode.setParent(node);
      calculateNodeDisplay(childNode);

      // check if element is contained in search results and thus contained in the view
      boolean validResult = resultIdSet.contains(childId);
      childNode.setValidResult(validResult);

      // recursively process subtree and check, if it contains valid results
      boolean includedChildren = recurseBuildTree(childNode);

      // element is included, if it has included children OR is a valid result itself
      boolean included = includedChildren || validResult;
      includedNodesOnLevel |= included;

      if (included) {
        childNode.setSiblingIndex(siblingIndex);

        if (partialLoading && siblingIndex >= PARTIAL_LOADED_ELEMENTS && !node.isLoadAll()) {
          calculateSubtreeDisplay(childNode);
          node.addUnloadedChild(childNode);
        }
        else {
          node.add(childNode);
        }

        this.idToNodeMap.put(childId, childNode);
        ++siblingIndex;
      }
    }

    if (node.hasUnloadedChildren()) {
      node.add(EntityTreeNode.placeholderFor(node, node.getUnloadedChildren().get(0)));
    }

    this.currentTreeLevel--;

    // pass "included"-information to recursive call
    return includedNodesOnLevel;
  }

  private void calculateNodeDisplay(EntityTreeNode node) {
    EntityTreeNode parent = node.getParent();
    node.setDisplay(!parent.isCollapsed() && parent.isDisplay());
  }

  private void calculateSubtreeDisplay(EntityTreeNode node) {
    for (EntityTreeNode etn : node) {
      calculateNodeDisplay(etn);
    }
  }

  /**
   * Called (per Ajax) to expand all nodes.
   */
  public void expandAll() {
    for (EntityTreeNode etn : rootNode) {
      etn.setCollapsed(false);
      etn.setDisplay(true);
    }
  }

  /**
   * Called (per Ajax) to collapse all nodes.
   */
  public void collapseAll() {
    for (EntityTreeNode etn : rootNode) {
      etn.setCollapsed(true);
      etn.setDisplay(etn.getTreeLevel() == 1);
    }
  }

  /**
   * Called (per Ajax) to collapse/expand one node
   * @param nodeId  The id of the node/entity.
   * @param collapse  true to collapse, false to expand
   */
  public void setCollapseNode(Integer nodeId, boolean collapse) {
    EntityTreeNode etn = idToNodeMap.get(nodeId);
    if (etn != null) {
      etn.setCollapsed(collapse);
    }

    Iterator<EntityTreeNode> iterator = etn.iterator();
    while (iterator.hasNext()) {
      EntityTreeNode node = iterator.next();
      node.setDisplay(!collapse);

      // don't show children of collapsed nodes; skip:
      if (!collapse && node.isCollapsed()) {
        EntityTreeNode.skipSubtree(node, iterator);
      }
    }
  }

  public void loadAllFrom(Integer loadAllId) {
    EntityTreeNode etn = idToNodeMap.get(loadAllId);
    if (etn != null) {
      etn.setLoadAll(true);

      if (etn.hasUnloadedChildren()) {
        List<EntityTreeNode> unloadedChildren = etn.getUnloadedChildren();
        for (EntityTreeNode unloadedEtn : unloadedChildren) {
          unloadedEtn.setCollapsed(true);
          etn.add(unloadedEtn);
        }
        etn.resetUnloadedChildren();
      }
    }
  }

  public boolean isOrderedByPosition() {
    String sortDef = dialogMemory.getTableState().getSortDefinition().getProperty();
    return sortDef != null && "position".equals(sortDef);
  }

  public Iterator<EntityTreeNode> getSortedNodes() {
    return rootNode.iterator();
  }

  public Map<Integer, EntityTreeNode> getIdToNodeMap() {
    return idToNodeMap;
  }

  public int getMaxTreeLevel() {
    return maxTreeLevel;
  }

  public int getResultCount() {
    return (resultIdSet == null) ? 0 : resultIdSet.size();
  }

  public boolean isReorderingEnabled() {
    return reorderingEnabled;
  }

  public void setReorderingEnabled(boolean reorderingEnabled) {
    this.reorderingEnabled = reorderingEnabled;
  }

  public boolean isReorderingPossible() {
    return isOrderedByPosition() && dialogMemory.getActiveSearchStrategy().equals(SearchStrategy.SEARCH_ALL);
  }
}
