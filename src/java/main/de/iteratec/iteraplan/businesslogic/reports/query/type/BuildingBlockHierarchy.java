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
package de.iteratec.iteraplan.businesslogic.reports.query.type;

import java.util.List;

import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * Represents a hierarchy over building blocks.
 * 
 * This is a kind of directed graph, with a number of root elements (retrievable via
 * {@linkplain #getToplevelElements}) and traversable via {@link #getParent(BuildingBlock)}
 * and {@link #getChildren(BuildingBlock)}.
 */
public interface BuildingBlockHierarchy<T extends BuildingBlock> {
  /**
   * The toplevel elements for the hierarchy.
   * 
   * @return All elements that do not have a parent. Not null. Empty iff hierarchy is empty.
   * @throws IteraplanTechnicalException If the database has not been initialized with the Initial Data
   */
  List<T> getToplevelElements() throws IteraplanTechnicalException;

  /**
   * The parent for the given element.
   * 
   * @param element Element to find the parent of. Type has to match hierachy. Must not be null.
   * @return The element's parent. Can be null if element is toplevel.
   * 
   * @throws ClassCastException if parameter does not match.
   * @throws NullPointerException if parameter is null.
   */
  T getParent(T element);

  /**
   * The children of the given element.
   * 
   * @param element Element to find the parent of. Type has to match hierachy. Must not be null.
   * @return The element's children. Can be null or empty if element if leaf.
   * @throws ClassCastException if parameter does not match.
   * @throws NullPointerException if parameter is null.
   */
  List<T> getChildren(T element);
}