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

import java.io.Serializable;
import java.util.List;

import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;


/**
 * The service interface for all entities of type {@link AbstractHierarchicalEntity}.
 * 
 * @param <E> The type parameter for the concrete hierarchical building block.
 * @param <T> The type parameter for hierachical building block's identifier.
 */
public interface HierarchicalBuildingBlockService<E extends AbstractHierarchicalEntity<E>, T extends Serializable> extends BuildingBlockService<E, T> {

  /**
   * Returns a list of valid children of the given source entity. Valid children are defined as all
   * elements that are no direct or indirect parents of the given element, including the top-level
   * element. Furthermore, the list may be limited by an optional filter string which applies to the
   * hierarchical name as well as an optional list of entities that may be excluded from the list.
   * 
   * @param source The entity for which valid children shall be returned.
   * @param elementsToExclude The list of entities that should be excluded from the result. Set to
   *            {@code null} if not needed.
   * @return See method description.
   */
  List<E> getAvailableChildren(E source, List<E> elementsToExclude);

  /**
   * Returns a list of valid parents of the entity with the given ID. Valid parents are defined as
   * all elements that are no direct or indirect children of the given element. The entity with the
   * given ID is not included. The list is sorted by the hierarchical names.
   * 
   * @param id The ID of the element for which valid parents shall be returned. May be {@code null}.
   * @return A list of valid parents of the entity with the given ID. If the ID is {@code null} all
   *         elements are returned.
   */
  List<E> getAvailableParents(T id);

  /**
   * Returns a list of entities.
   * <ul>
   * <li>An optional list of entities may be excluded from the results.</li>
   * <li>It may be specified if the top-level element shall be included in the results.</li>
   * </ul>
   * @param elementsToExclude The list of elements that should be excluded from the result. Set to
   *            {@code null} if not needed.
   * @param includeRoot If {@code true}, the top-level element is included in the results.
   * 
   * @return A list of entities.
   */
  List<E> getEntitiesFiltered(List<E> elementsToExclude, boolean includeRoot);

  /**
   * Retrieves all building blocks matching the search criteria contained in the given
   * entity
   * 
   * @param entity a partially filled entity, whose properties will be used as search criteria
   * @return a list of entities which match the specified search criteria.
   */
  List<E> getEntityResultsBySearch(E entity);

  /**
   * Moves a {@link AbstractHierarchicalEntity} element from one place to another. The the element
   * will be added to the new place as sibling before or after a destination lement. The destination
   * can be inside the same parent or to another parent.
   * 
   * @param sourceId  The ID of the element to move.
   * @param destId  The ID of the destination element.
   * @param insertAfter  If true, the element will be added after the destination element. Otherwise before.
   * @return  True, if the reordering has been successful.
   */
  boolean saveReorderMove(Integer sourceId, Integer destId, boolean insertAfter);
}
