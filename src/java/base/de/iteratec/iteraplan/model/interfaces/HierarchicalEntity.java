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
package de.iteratec.iteraplan.model.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * This interface must be implemented by all domain objects that define a parent/child relationship.
 */
public interface HierarchicalEntity<T extends HierarchicalEntity<T>> extends IdEntity {

  String HIERARCHICAL_NAME_SEPARATOR = " : ";

  /**
   * @return Returns a list of children of the domain object. This list might be ordered in a way
   *         specific to the particular domain object.
   */
  List<T> getChildrenAsList();

  /**
   * @return Returns a set of children of the domain object.
   */
  Set<T> getChildrenAsSet();

  /**
   * @return Returns a collection of children of the domain object. The collection might represent a
   *         set or a list.
   */
  Collection<T> getChildren();

  /**
   * Recursively finds all children of the given {@link HierarchicalEntity}. The given entity
   * itself is not contained in the resulting set.
   * 
   * @param entity
   *          The entity for which all descendants should be found.
   * @param set
   *          The set of all descendants found so far. Acts as an output parameter.
   */
  void getDescendants(T entity, Set<T> set);

  /**
   * @return Returns the hierarchical name of the domain object. The hierarchical name consists of
   *         the object's simple name plus all the parent's names.
   */
  String getHierarchicalName();

  /**
   * @return Returns the non-hierarchical or simple name of the domain object.
   */
  String getNonHierarchicalName();

  /**
   * @return Returns the distance from the root of the hierarchy of the domain object.
   */
  int getLevel();

  /**
   * @return Returns the parent element of the domain object.
   */
  T getParentElement();

  /**
   * Returns the position of a child element for ordered children
   * 
   * @param child
   *          Child element
   * @return Position in List
   */
  int findChildPos(T child);

  void removeParent();

  void addParent(T parentToAdd);

  /**
   * Indicates if this element is a top level/virtual element.
   * 
   * @return {@code true}, if this element is a top level/virtual element
   */
  boolean isTopLevelElement();
}