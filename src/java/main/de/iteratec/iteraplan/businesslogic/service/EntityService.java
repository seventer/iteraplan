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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.model.interfaces.ValidatableEntity;


/**
 * This interface provides basic functionality available to all transaction bound service classes.
 * 
 * @param <E>
 *          The type parameter for the concrete entity.
 * @param <T>
 *          The type parameter for the entity's identifier.
 */
public interface EntityService<E extends Entity, T extends Serializable> extends Service {

  /**
   * Deletes the respective entity. Implementing classes must take care that relevant
   * associations are deleted as well.
   * 
   * @param buildingBlock the entity to be deleted.
   */
  void deleteEntity(E buildingBlock);

  /**
   * Checks whether an object exists with the given name and a different than the given identifier.
   * The identifier is used to distinguish between save and update operations. If no id is provided,
   * the method just checks whether an object with the given name exists.
   * 
   * @param identifier
   *          The object's identifier. It can be {@code null}, in this case only the name is checked
   * @param name
   *          The object's name.
   * @return {@code True}, if an object with the given name and a different identifier exists;
   *         otherwise {@code false}. 
   *         If identifier is {@code null}: {@code True}, if an object with the given name exists;
   *         otherwise {@code false}.
   */
  boolean doesObjectWithDifferentIdExist(T identifier, String name);

  /**
   * @return Returns the first element of a collection of entities. By default, the first element is
   *         obtained by ordering available elements by name and returning the top-most.
   */
  E getFirstElement();

  /**
   * Loads and returns all instances of the entity from the database and orders them by a property.
   * 
   * @param orderByProperty
   *          The property of the entity which shall be used for ordering the result - may not be
   *          {@code null}.
   * @return See method description.
   */
  List<E> loadElementList(String orderByProperty);

  /**
   * Loads and returns all instances of the entity from the database without defined ordering.
   * 
   * @return See method description.
   */
  List<E> loadElementList();

  /**
   * Loads and returns a list of all instances of the entity. The list is filtered by the specified
   * filter which applies to the specifed property. Furthermore, all instances that match the given
   * set of identifiers are excluded from the list. The list is sorted by the specified property.
   * @param toExclude
   *          The set of identifiers that are excluded from the result list. If {@code null}, no
   *          instances will be excluded.
   * 
   * @return See method description.
   */
  List<E> loadFilteredElementList(Set<T> toExclude);

  /**
   * Returns the instance of the entity with the given identifier. If the entity does not exist, an
   * exception is thrown.
   * 
   * @param id the ID of the entity to load
   * @return The entity. An exception is thrown if the entity does not exist. If the passed-in ID is
   *         {@code null}, {@code null} is returned.
   */
  E loadObjectById(T id);

  /**
   * Loads and returns the instance with the given identifier from the database. Additionally,
   * associations that are specified via their names in the variable argument list will be eagerly
   * fetched. If the entity does not exist, an exception is thrown.
   * 
   * @param id
   *          The ID of the entity to load.
   * @param associations
   *          The list of names of the associations that shall be eagerly fetched.
   * @return The loaded entity.
   */
  E loadObjectById(T id, String... associations);

  /**
   * Return the instance of the entity with the given identifier. If the entity does not exist,
   * {@code null} is returned.
   * 
   * @param id
   *          The ID of the entity.
   * @return The entity. {@code null} is returned if the entity does not exist.
   */
  E loadObjectByIdIfExists(T id);

  /**
   * Reloads all instances in the given {@code Collection} and returns them as a {@code List}.
   * 
   * @return The list of reloaded entities or an empty list if the argument is {@code null} or
   *         empty.
   */
  List<E> reload(Collection<E> col);

  /**
   * Saves or updates the given instance of an entity with the given session instance. After 
   * that the given entity will be validated (see {@link ValidatableEntity#validate()}).
   * 
   * @param entity the instance to save or update.
   * @return the saved or updated entity
   */
  E saveOrUpdate(E entity);

  /**
   * Copy the state of the given object onto the persistent object with the same identifier. 
   * <p>Similar to saveOrUpdate, but never associates the given object with the current Hibernate Session. 
   * In case of a new entity, the state will be copied over as well.
   * 
   * @param entity the object to merge with the corresponding persistence instance
   * @return the updated, registered persistent instance
   * @throws org.springframework.dao.DataAccessException in case of Hibernate errors
   */
  E merge(E entity);

  /**
   * Clears the hibernate session: {@link Session#clear()}
   */
  void clearSession();
}