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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.persistence.dao.DateIntervalDAO;


/**
 * DateIntervalService implementation.
 */
@Service("dateIntervalService")
public class DateIntervalServiceImpl implements DateIntervalService {

  private DateIntervalDAO dateIntervalDAO;

  public void setDateIntervalDAO(DateIntervalDAO dateIntervalDAO) {
    this.dateIntervalDAO = dateIntervalDAO;
  }

  /**{@inheritDoc}**/
  public DateInterval saveOrUpdate(DateInterval dateInterval) {
    return dateIntervalDAO.merge(dateInterval);
  }

  public Set<DateInterval> findDateIntervalsByDateATs(Set<Integer> dateATids) {
    Set<DateInterval> intervals = new HashSet<DateInterval>();
    for (Integer id : dateATids) {
      //
      List<DateInterval> resultStart = findDateIntervalsStartWith(id);
      intervals.addAll(resultStart);
      //
      List<DateInterval> resultEnd = findDateIntervalsEndWith(id);
      intervals.addAll(resultEnd);
    }
    return intervals;
  }

  public void deleteDateIntervasByDateAT(Integer idDateAT) {
    List<DateInterval> intervalsStart = findDateIntervalsStartWith(idDateAT);
    for (DateInterval di : intervalsStart) {
      dateIntervalDAO.delete(di);
    }
    List<DateInterval> intervalsEnd = findDateIntervalsEndWith(idDateAT);
    for (DateInterval di : intervalsEnd) {
      dateIntervalDAO.delete(di);
    }
  }

  private List<DateInterval> findDateIntervalsStartWith(Integer idDateAT) {
    DetachedCriteria criteriaStart = DetachedCriteria.forClass(DateInterval.class);
    criteriaStart.add(Restrictions.eq("startDate.id", idDateAT));
    return dateIntervalDAO.findByCriteria(criteriaStart);
  }

  private List<DateInterval> findDateIntervalsEndWith(Integer idDateAT) {
    DetachedCriteria criteriaEnd = DetachedCriteria.forClass(DateInterval.class);
    criteriaEnd.add(Restrictions.eq("endDate.id", idDateAT));
    return dateIntervalDAO.findByCriteria(criteriaEnd);
  }

  /**{@inheritDoc}**/
  public DateInterval findDateIntervalById(Integer dateIntervalId) {
    return dateIntervalDAO.loadObjectById(dateIntervalId);
  }

  /**{@inheritDoc}**/
  public DateInterval findDateIntervalByName(String name) {
    return dateIntervalDAO.getDateIntervalByName(name);
  }

  /**{@inheritDoc}**/
  public boolean existsDateIntervalByName(Integer id, String name) {
    return dateIntervalDAO.doesObjectWithDifferentIdExist(id, name);
  }

  /**{@inheritDoc}**/
  public List<DateInterval> findAllDateIntervals() {
    DetachedCriteria criteriaDateInterval = DetachedCriteria.forClass(DateInterval.class);
    return dateIntervalDAO.findByCriteria(criteriaDateInterval);
  }

  /**{@inheritDoc}**/
  public void deleteEntity(DateInterval di) {
    dateIntervalDAO.delete(di);
  }

}
