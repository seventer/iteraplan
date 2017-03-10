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
package de.iteratec.iteraplan.model;

import java.util.Set;

import de.iteratec.iteraplan.model.interfaces.IdEntity;


/**
 * Interface for entities that may be part of a {@code BusinessMapping}.
 */
public interface BusinessMappingEntity extends IdEntity {

  /**
   * Adds a {@code BusinessMapping}. Updates both sides of the association.
   * @param bMapping
   *          The {@link BusinessMapping} to add
   */
  void addBusinessMapping(BusinessMapping bMapping);

  /**
   * Adds each element of the given set of {@code BusinessMapping}s. Updates both sides of the
   * association. Neither the set nor an element in the set must be {@code null}.
   * @param bMappings
   *          Set of {@link BusinessMapping}s to add.
   */
  void addBusinessMappings(Set<BusinessMapping> bMappings);

  /**
   * Removes all {@code BusinessMapping}s. Updates both sides of the association.
   */
  void removeBusinessMappings();

  /**
   * Returns the {@link BusinessMapping}s of this entity.
   * @return Set of {@link BusinessMapping}.
   */
  Set<BusinessMapping> getBusinessMappings();

  /**
   * Sets the {@link BusinessMapping}s.
   * @param bMappings
   *          Set of {@link BusinessMapping}s to set.
   */
  void setBusinessMappings(Set<BusinessMapping> bMappings);

}