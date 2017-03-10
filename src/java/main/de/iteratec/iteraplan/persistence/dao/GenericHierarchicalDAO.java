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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import de.iteratec.hibernate.criterion.IteraplanLikeExpression;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;


/**
 * Abstract DAO class for all entities of type {@link AbstractHierarchicalEntity}.
 * 
 * @param <E>
 *          The type parameter for the concrete hierarchical building block.
 * @param <T>
 *          The type parameter for the hierachical building block's identifier.
 */
public abstract class GenericHierarchicalDAO<E extends AbstractHierarchicalEntity<E>, T extends Serializable> extends GenericBaseDAO<E, T> implements
    HierarchicalDAOTemplate<E, T> {

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public E getFirstElement() {
    HibernateCallback<E> callback = new HibernateCallback<E>() {
      public E doInHibernate(Session session) {
        Criteria c = session.createCriteria(getPersistentClass());
        //FIXME we may use eq here instead of like
        c.add(new IteraplanLikeExpression(getNameAttribute(), AbstractHierarchicalEntity.TOP_LEVEL_NAME, false));
        Object result = c.uniqueResult();
        if (result == null) {
          throw new IllegalStateException("The hierarchy's top-level element does not exist.");
        }

        return (E) result;
      }
    };

    return getHibernateTemplate().execute(callback);
  }

  @Override
  protected String getNameAttribute() {
    return "name";
  }

}