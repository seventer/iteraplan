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

import java.util.List;

import de.iteratec.iteraplan.model.BusinessObject;


/**
 * Service interface for {@link BusinessObject}s.
 */
public interface BusinessObjectService extends HierarchicalBuildingBlockService<BusinessObject, Integer> {

  /**
   * Returns a list of valid generalisations of the business object with the given ID. Valid
   * generalisations are defined as all elements that are no direct or indirect specialisations of
   * the given element. Furthermore, the list is sorted by the hierarchical names. The business
   * object with the given ID is not included.
   * 
   * @param id the ID of the element for which valid generalisations shall be returned. May be {@code null}.
   * @return A list of valid generalisations of the business object with the given ID. If the ID is
   *         {@code null} all elements are returned.
   */
  List<BusinessObject> getAvailableGeneralisations(Integer id);

  /**
   * @return The most general {@code BusinessObject}s.
   */
  List<BusinessObject> getBusinessObjectsWithoutGeneralisation();

  /**
   * @param id the ID of the element for which all available specialisations shall be returned.
   * @param elementsToExclude the list of elements that should be excluded from the result. Set to
   *    {@code null} if not needed.
   * @param includeRoot if {@code true}, the top-level element is included in the results.
   * @return A list of all available specialisations.
   */
  List<BusinessObject> getAvailableSpecialisations(Integer id, List<BusinessObject> elementsToExclude, boolean includeRoot);
}
