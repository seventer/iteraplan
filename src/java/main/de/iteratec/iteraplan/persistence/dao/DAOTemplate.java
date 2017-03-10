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
package de.iteratec.iteraplan.persistence.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;


/**
 * This interface provides a generic way for handling domain objects.
 */
public interface DAOTemplate<E, T extends Serializable> {

  /**
   * Deletes the given entity from the database.
   * 
   * @param entity The entity to delete.
   */
  void delete(E entity);

  /**
   * Deletes the given entities from the database.
   * <br><br>
   * <b>Caution:</b>
   * <ul>
   * <li>Use only after thorough consideration. It is highly possible that deletion with this method
   * instead of bulk-updates with HQL queries will cause performance issues for larger entity-sets.</li>
   * <li>Any steps to be considered before deleting an entity have to be taken care of by the caller
   * of this method.</li>
   * </ul>
   * @param entities
   *            The entities to be deleted
   */
  void deleteAll(Collection<? extends E> entities);

  /**
   * Clears the hibernate session: {@link org.hibernate.Session#clear()}
   */
  void clearSession();

  /**
   * Checks if an object with the given name already exists
   * @param identifier id of the object (could be null)
   * @param name the name of object
   * @return true if object exists, false if object does not exist
   */
  boolean doesObjectWithDifferentIdExist(T identifier, String name);

  /**
   * Executes the given named query which takes no parameters.
   * 
   * @param namedQuery The named query
   * @return The list of results of the query
   */
  List<E> executeNamedQuery(String namedQuery);

  /**
   * Executes the given named query which takes one parameter key and value.
   * 
   * @param namedQuery The named query.
   * @param key The key of the parameter in the query.
   * @param value The specific value of the parameter to be passed to the query.
   * @return The list of results of the query.
   */
  List<E> executeNamedQuery(String namedQuery, String key, Object value);

  /**
   * Executes the given named query which takes parameter keys and values.
   * 
   * @param namedQuery The named query.
   * @param keys The keys of the parameters in the query.
   * @param values The specific values of the parameters to be passed to the query.
   * @return The list of results of the query.
   */
  List<E> executeNamedQuery(String namedQuery, String[] keys, Object[] values);

  /**
   * Executes the specified {@link DetachedCriteria} and returns the results.
   * 
   * @param criteria the criteria to execute
   * @return the result entities
   */
  List<E> findByCriteria(DetachedCriteria criteria);

  /**
   * Loads and returns the first entity in the database according to an ordering that must be
   * defined by the implementors of this interface.
   * 
   * @return The loaded entity or {@code null} if it does not exist.
   */
  E getFirstElement();

  List<E> loadElementList(String orderByProperty);

  List<E> loadElementListWithIds(Set<T> identifiers);

  List<E> loadFilteredElementList(String property, Set<T> toExclude);

  E loadObjectById(T id);

  E loadObjectById(T id, String... associations);

  E loadObjectByIdIfExists(T id);

  /**
   * Persists the given entity. Note: The entity is either saved or updated depending on the value
   * of its identifier property.
   * 
   * @param entity The entity to be saved or updated.
   * @return The saved or updated entity.
   */
  E saveOrUpdate(E entity);

  /**
   * Persists the given collection of entities. Note: The entities is either saved or updated depending on the value
   * of its identifier property.
   * 
   * @param entities The entities to be saved or updated.
   * @return The persisted entities.
   */
  Collection<E> saveOrUpdate(Collection<E> entities);

  List<E> getSubscribedElements();

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
   * Searches for a String in the Attributes of a Class (and joins them if necessary).
   * 
   * @param searchTerm
   *          The user-provided search term.
   * @param attribute
   *          That attribute(s) (of kind String) that shall be compared to 'searchTerm' in a
   *          dot-separated Format. For Example to
   *          check if 'searchTerm' is in the Name of an InformationSystemRelease's
   *          InformationSystem the String must be "informationSystem.name". The order of the
   *          Attributes here is reflected to an ORDER BY-Statement.
   * @return the result list
   */
  List<E> findBySearchTerm(String searchTerm, final String... attribute);

  /**
   * Searches for a String in the Attributes of a Class (and joins them if necessary).
   * 
   * @param searchTerm
   *          The user-provided search term.
   * @param showInactive whether to show or hide inactive Buildingblocks. Currently only applicable for ISR!
   * @param attribute
   *          That attribute(s) (of kind String) that shall be compared to 'searchTerm' in a
   *          dot-separated Format. For Example to
   *          check if 'searchTerm' is in the Name of an InformationSystemRelease's
   *          InformationSystem the String must be "informationSystem.name". The order of the
   *          Attributes here is reflected to an ORDER BY-Statement.
   * @return the result list
   */
  List<E> findBySearchTerm(String searchTerm, boolean showInactive, final String... attribute);

  /**
   * Searches the building blocks by their exact names. The letter case (lower and upper) will be ignored.
   * 
   * @param names the set of the exact entity names
   * @return the list of found entities, or {@code empty} list, if no entities will be found
   */
  List<E> findByNames(Set<String> names);
}