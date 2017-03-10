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
import java.util.Set;

import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * The service interface for all entities of type {@link BuildingBlock}.
 * 
 * @param <E> the type parameter for the concrete building block.
 * @param <T> the type parameter for building block's identifier.
 */
public interface BuildingBlockService<E extends BuildingBlock, T extends Serializable> extends EntityService<E, T>, SubscribeService<E> {

  /**
   * Searches the building blocks by their exact names. The letter case (lower and upper) will be ignored.
   * Hierarchical entities will be searched by non hierarchical name. The {@link de.iteratec.iteraplan.model.InformationSystemRelease}s
   * and {@link de.iteratec.iteraplan.model.TechnicalComponentRelease}s will be searched by their release names
   * (see {@link de.iteratec.iteraplan.model.Release#getReleaseName()}).
   * 
   * @param names the set of the exact building block names
   * @return the list of found building blocks, or {@code empty} list, if no building blocks will be found
   */
  List<E> findByNames(Set<String> names);

  /**
   * Saves or updates the given instance of an entity with the given session instance. After 
   * that the given entity will be validated (see {@link de.iteratec.iteraplan.model.interfaces.ValidatableEntity#validate()}).
   * 
   * @param entity the instance to save or update.
   * @param cleanup whether to execute necessary cleanup operations
   * @return the saved or updated entity
   */
  E saveOrUpdate(E entity, boolean cleanup);
}
