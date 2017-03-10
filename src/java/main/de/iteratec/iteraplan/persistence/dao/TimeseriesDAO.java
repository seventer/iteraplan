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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import de.iteratec.iteraplan.model.attribute.Timeseries;


public class TimeseriesDAO extends GenericBaseDAO<Timeseries, Integer> {

  public List<Timeseries> loadByBuildingBlockAndAttributeType(Integer bbId, Integer atId) {
    String[] parameters = new String[] { "bbID", "atID" };
    Integer[] parameterValues = new Integer[] { bbId, atId };
    List<Timeseries> resultList = executeNamedQuery("getTimseriesForBuildingBlockAndAttributeType", parameters, parameterValues);
    if (resultList == null || resultList.isEmpty()) {
      return Collections.emptyList();
    }
    return resultList;
  }

  /**
   * Deletes all Timeseries related to the given attribute type id.
   * @param atId
   *          The attribute type id
   * @return The number of deleted timeseries objects
   */
  public Integer deleteTimeseriesByAttributeTypeId(final Integer atId) {
    return updateWithNamedQuery("deleteByAttributeType", "id", atId);
  }

  /**
   * Deletes all Timeseries related to the given building block ids.
   * @param bbIds
   *          The collection of building block ids for which the timeseries should be deleted
   * @return The number of deleted timeseries objects
   */
  public Integer deleteTimeseriesByBuildingBlockIds(final Collection<Integer> bbIds) {
    return updateWithNamedQuery("deleteByBuildingBlocks", "ids", bbIds);
  }

  private Integer updateWithNamedQuery(final String queryName, final String parameterName, final Object parameter) {
    HibernateCallback<Integer> callback = new HibernateCallback<Integer>() {
      public Integer doInHibernate(Session session) {
        Query queryObject = session.getNamedQuery(queryName);
        if (parameter instanceof Collection) {
          queryObject.setParameterList(parameterName, (Collection) parameter);
        }
        else {
          queryObject.setParameter(parameterName, parameter);
        }
        return Integer.valueOf(queryObject.executeUpdate());
      }
    };

    return getHibernateTemplate().execute(callback);
  }

  @Override
  protected String getNameAttribute() {
    throw new UnsupportedOperationException("This operation is not supported for this type.");
  }

  /**
   * Loads all Timeseries for the BuildingBlocks with the specified Ids for the AttributeType with the specified id
   * 
   * @param bbIds a collection containing the ids of the BuildingBlocks in question
   * @param atId the id of the attribute type to load the timeseries for
   * @return the list of {@link Timeseries} for the BuildingBlocks with the specified Ids
   */
  public List<Timeseries> loadForBuildingBlocks(Collection<Integer> bbIds, Integer atId) {
    String[] parameters = new String[] { "bbIDs", "atID" };
    Object[] parameterValues = new Object[] { bbIds, atId };
    List<Timeseries> resultList = executeNamedQuery("getTimeseriesForBuildingBlocks", parameters, parameterValues);
    if (resultList == null || resultList.isEmpty()) {
      return Collections.emptyList();
    }
    return resultList;
  }
}
