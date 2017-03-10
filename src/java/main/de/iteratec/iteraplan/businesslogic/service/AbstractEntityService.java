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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.orm.ObjectRetrievalFailureException;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.collections.EntityToIdFunction;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;


/**
 * Abstract service class for all entities of type {@link Entity}. Implements the service interface
 * {@link EntityService} and forwards the implemented methods to the {@link DAOTemplate} interface.
 * 
 * @param <E>
 *          The type parameter for the concrete entity.
 * @param <T>
 *          The type parameter for the entity's identifier.
 */
public abstract class AbstractEntityService<E extends Entity, T extends Serializable> extends AbstractService implements EntityService<E, T> {

  private static final Logger LOGGER = Logger.getIteraplanLogger(AbstractEntityService.class);

  /** {@inheritDoc} */
  public void clearSession() {
    getDao().clearSession();
  }

  /** {@inheritDoc} */
  public void deleteEntity(E buildingBlock) {
    getDao().delete(buildingBlock);
  }

  /** {@inheritDoc} */
  public boolean doesObjectWithDifferentIdExist(T identifier, String name) {
    return getDao().doesObjectWithDifferentIdExist(identifier, name);
  }

  /** {@inheritDoc} */
  public E getFirstElement() {
    return getDao().getFirstElement();
  }

  /** {@inheritDoc} */
  public List<E> loadElementList(String orderByProperty) {
    Preconditions.checkNotNull(orderByProperty);
    return getDao().loadElementList(orderByProperty);
  }

  /** {@inheritDoc} */
  public List<E> loadElementList() {
    return getDao().loadElementList(null);
  }

  /** {@inheritDoc} */
  public List<E> loadFilteredElementList(Set<T> toExclude) {
    return getDao().loadFilteredElementList(null, toExclude);
  }

  /**{@inheritDoc} */
  public E loadObjectById(T id) {
    if (id == null) {
      LOGGER.error("Tried to load an object with id 'null'");
    }

    try {
      return getDao().loadObjectById(id);
    } catch (ObjectRetrievalFailureException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ, e);
    }
  }

  /** 
   * {@inheritDoc} 
   */
  public E loadObjectById(T id, String... associations) {
    return getDao().loadObjectById(id, associations);
  }

  /** {@inheritDoc} */
  public E loadObjectByIdIfExists(T id) {
    return getDao().loadObjectByIdIfExists(id);
  }

  /** {@inheritDoc} */
  public List<E> reload(Collection<E> identifiers) {
    if ((identifiers == null) || identifiers.isEmpty()) {
      return new ArrayList<E>(0);
    }

    EntityToIdFunction<E, T> toIdFunction = new EntityToIdFunction<E, T>();
    Iterable<T> transform = Iterables.transform(identifiers, toIdFunction);

    List<E> loadElementListWithIds = getDao().loadElementListWithIds(Sets.newHashSet(transform));
    Collections.sort(loadElementListWithIds, Ordering.explicit(Lists.newArrayList(identifiers)));

    return loadElementListWithIds;
  }

  /** {@inheritDoc} */
  public E saveOrUpdate(E entity) {
    LOGGER.debug("Saving/Updating the entity: {0}", entity);

    E savedEntity = getDao().saveOrUpdate(entity);
    savedEntity.validate();

    return savedEntity;
  }

  /**
   * This method must be implemented by concrete services in order to provide the concrete DAO used
   * in the methods above.
   * 
   * @return The concrete implementation of a {@link DAOTemplate}.
   */
  protected abstract DAOTemplate<E, T> getDao();

  /**
   * {@inheritDoc}
   * 
   * @see de.iteratec.iteraplan.persistence.dao.GenericBaseDAO#merge(Object)
   */
  public E merge(E entity) {
    return getDao().merge(entity);
  }
}
