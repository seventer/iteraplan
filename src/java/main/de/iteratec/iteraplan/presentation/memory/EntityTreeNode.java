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
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;


/**
 * Wrapper (bean) class for {@link HierarchicalEntity} objects for the GUI.
 */
public class EntityTreeNode implements Iterable<EntityTreeNode>, Serializable {

  private static final long serialVersionUID = -2035512650999940148L;

  /** Iterator for subtrees of a node element (excluding the node itself). */
  static class EntityNodeIterator implements Iterator<EntityTreeNode> {

    private final Deque<Iterator<EntityTreeNode>> iteratorStack     = Lists.newLinkedList();
    private EntityTreeNode                        nextNode;
    private boolean                               nextNodeDelivered = true;
    private Set<Predicate<EntityTreeNode>>        predicates        = Sets.newHashSet();

    public EntityNodeIterator(EntityTreeNode rootNode) {
      this.iteratorStack.offer(rootNode.getChildren().iterator());
    }

    public void addPredicate(Predicate<EntityTreeNode> etnPredicate) {
      predicates.add(etnPredicate);
    }

    /**{@inheritDoc}**/
    public boolean hasNext() {
      loadNextNode();
      return nextNode != null;
    }

    /**{@inheritDoc}**/
    public EntityTreeNode next() {
      loadNextNode();
      nextNodeDelivered = true;
      return nextNode;
    }

    private void loadNextNode() {
      if (nextNodeDelivered) {
        findNextDisplayNode();
        nextNodeDelivered = false;
      }
    }

    private void findNextDisplayNode() {
      Iterator<EntityTreeNode> iterator;

      while (!iteratorStack.isEmpty()) {
        iterator = iteratorStack.peek();
        if (iterator.hasNext()) {
          EntityTreeNode nextNodeCandidate = iterator.next();
          if (!nextNodeCandidate.isLeaf()) {
            // push child iterator onto the stack
            iteratorStack.push(nextNodeCandidate.getChildren().iterator());
          }
          if (fulfillsAllPredicates(nextNodeCandidate)) {
            nextNode = nextNodeCandidate;
            return;
          }
        }
        else {
          iteratorStack.pop();
        }
      }

      // no more elements
      nextNode = null;
    }

    private boolean fulfillsAllPredicates(EntityTreeNode etn) {
      boolean fulfilled = true;
      for (Predicate<EntityTreeNode> predicate : predicates) {
        fulfilled &= predicate.apply(etn);
      }
      return fulfilled;
    }

    /**{@inheritDoc}**/
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /** Create placeholder stub ("... load x more") */
  static EntityTreeNode placeholderFor(EntityTreeNode parentNode, EntityTreeNode renderLikeNode) {
    EntityTreeNode placeholderNode = new EntityTreeNode(renderLikeNode.getEntity());
    placeholderNode.setTreeLevel(renderLikeNode.getTreeLevel());
    placeholderNode.setParent(parentNode);
    placeholderNode.setSiblingIndex(-1);
    placeholderNode.setPartialPlaceholder(true);
    placeholderNode.setCollapsed(true);
    placeholderNode.setDisplay(parentNode.isDisplay() && !parentNode.isCollapsed());
    placeholderNode.setValidResult(false);
    return placeholderNode;
  }

  /** Entity which is wrapped by this node element. */
  private AbstractHierarchicalEntity<?> entity;

  /** Id of the wrapped entity. */
  private final Integer                 id;

  /** This node's child nodes. */
  private List<EntityTreeNode>          children;

  /** This node's parent node. */
  private EntityTreeNode                parent;

  /** The tree level (root element = 0).  */
  private int                           treeLevel;

  /** Flag to indicate if this node is displayed collapsed (true) or expanded (false).  */
  private boolean                       collapsed          = false;

  /** Flag to indicate how CSS "display" property should be set.  */
  private boolean                       display            = true;

  /** Flag to indicate if this node's entity is among the valid search results.  */
  private boolean                       validResult        = false;

  private int                           siblingIndex;

  private boolean                       partialPlaceholder = false;

  private List<EntityTreeNode>          unloadedChildren;

  private boolean                       loadAll            = false;

  EntityTreeNode(HierarchicalEntity<?> entity) {
    this(entity, 0);
  }

  EntityTreeNode(HierarchicalEntity<?> entity, int level) {
    //    Preconditions.checkState(entity instanceof AbstractHierarchicalEntity<?>);

    this.entity = (AbstractHierarchicalEntity<?>) entity;
    this.treeLevel = level;
    this.id = entity.getId();
  }

  public Integer getId() {
    return id;
  }

  public AbstractHierarchicalEntity<?> getEntity() {
    return entity;
  }

  void setEntity(AbstractHierarchicalEntity<?> entity) {
    this.entity = entity;
  }

  public List<EntityTreeNode> getChildren() {
    if (children == null) {
      children = Lists.newArrayList();
    }
    return children;
  }

  EntityTreeNode getChildBySiblingIndex(int lkpSiblingIndex) {
    Preconditions.checkElementIndex(lkpSiblingIndex, children.size());
    return children.get(lkpSiblingIndex);
  }

  void resetChildren() {
    children = Lists.newArrayList();
  }

  public EntityTreeNode getParent() {
    return parent;
  }

  void setParent(EntityTreeNode parentNode) {
    parent = parentNode;
  }

  void add(EntityTreeNode childNode) {
    getChildren().add(childNode);
  }

  void addUnloadedChild(EntityTreeNode childNode) {
    if (unloadedChildren == null) {
      unloadedChildren = Lists.newArrayList();
    }
    unloadedChildren.add(childNode);
  }

  void resetUnloadedChildren() {
    unloadedChildren = null;
  }

  boolean hasUnloadedChildren() {
    return unloadedChildren != null && unloadedChildren.size() > 0;
  }

  List<EntityTreeNode> getUnloadedChildren() {
    return unloadedChildren;
  }

  public int getNumberOfUnloadedChildren() {
    if (unloadedChildren != null) {
      return unloadedChildren.size();
    }
    else {
      return 0;
    }
  }

  public int getTreeLevel() {
    return treeLevel;
  }

  void setTreeLevel(int treeLevel) {
    this.treeLevel = treeLevel;
  }

  public boolean isCollapsed() {
    return collapsed;
  }

  void setCollapsed(boolean collapsed) {
    this.collapsed = collapsed;
  }

  public boolean isDisplay() {
    return display;
  }

  void setDisplay(boolean display) {
    this.display = display;
  }

  public boolean isValidResult() {
    return validResult;
  }

  void setValidResult(boolean validResult) {
    this.validResult = validResult;
  }

  public boolean isLeaf() {
    return children == null || children.size() == 0;
  }

  public int getSiblingIndex() {
    return siblingIndex;
  }

  void setSiblingIndex(int siblingIndex) {
    this.siblingIndex = siblingIndex;
  }

  public boolean isFirst() {
    return siblingIndex == 0;
  }

  public boolean isLast() {
    return siblingIndex == getParent().getChildren().size() - 1;
  }

  public boolean isPartialPlaceholder() {
    return partialPlaceholder;
  }

  void setPartialPlaceholder(boolean partialPlaceholder) {
    this.partialPlaceholder = partialPlaceholder;
  }

  public boolean isLoadAll() {
    return loadAll;
  }

  public void setLoadAll(boolean loadAll) {
    this.loadAll = loadAll;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof EntityTreeNode)) {
      return false;
    }
    EntityTreeNode other = (EntityTreeNode) obj;
    return new EqualsBuilder().append(getId(), other.getId()).isEquals();
  }

  /**{@inheritDoc}**/
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getId()).hashCode();
  }

  /**{@inheritDoc}**/
  @Override
  public String toString() {
    if (isPartialPlaceholder()) {
      return "STUB at position of: "
          + (new ToStringBuilder(this).append("id", entity.getId()).append("hierarchical name", entity.getHierarchicalName()).toString());
    }
    else {
      return new ToStringBuilder(this).append("id", entity.getId()).append("hierarchical name", entity.getHierarchicalName()).toString();
    }
  }

  /**{@inheritDoc}**/
  public Iterator<EntityTreeNode> iterator() {
    return Iterators.peekingIterator(new EntityNodeIterator(this));
  }

  public static void skipSubtree(EntityTreeNode currentNode, Iterator<EntityTreeNode> iterator) {
    Preconditions.checkState(iterator instanceof PeekingIterator<?>,
        "Skipping subtree requires an Iterator of type com.google.common.collect.PeekingIterator<EntityTreeNode> !");
    PeekingIterator<EntityTreeNode> peekingIterator = (PeekingIterator<EntityTreeNode>) iterator;
    int currentNodeLevel = currentNode.getTreeLevel();
    while (peekingIterator.hasNext() && peekingIterator.peek() != null && peekingIterator.peek().getTreeLevel() > currentNodeLevel) {
      peekingIterator.next();
    }
  }
}