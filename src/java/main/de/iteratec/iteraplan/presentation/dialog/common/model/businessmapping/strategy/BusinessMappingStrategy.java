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
package de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.strategy;

import java.util.List;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingItems;


/**
 * A strategy for creating {@link BusinessMapping}s, validating them and performing updates and 
 * checks. 
 * 
 * @since 20.01.2011
 * @version 2.8
 *
 */
public interface BusinessMappingStrategy {  

  /**
   * Returns the list of the newly created business mappings. Depending on the implementation, 
   * the business mappings will be created from different business mapping items 
   * ({@link de.iteratec.iteraplan.model.InformationSystemRelease}s, {@link de.iteratec.iteraplan.model.BusinessProcess}'s, 
   * {@link de.iteratec.iteraplan.model.BusinessUnit}s and/or {@link de.iteratec.iteraplan.model.Product}s).
   * 
   * @param bmi the business mapping items
   * @return the list of the newly created business mappings
   */
  List<BusinessMapping> createBusinessMappings(BusinessMappingItems bmi);

  /**
   * Validates the specified business mapping items. 
   * 
   * @param bmi the business mapping items
   * @throws de.iteratec.iteraplan.common.error.IteraplanBusinessException if the items are in invalid state
   */
  void validate(BusinessMappingItems bmi);

  /**
   * Attaches the specified business mapping to the owning entity. 
   * 
   * @param <T> the {@link BuildingBlock} type
   * @param businessMapping the business mapping
   * @param owningEntity the owning entity
   */
  <T extends BuildingBlock> void addOwningEntity(BusinessMapping businessMapping, T owningEntity);
  
  /**
   * Returns {@code true} if the specified business mapping {@code bmToCheck} already exists in the 
   * specified {@code existingMappings} list. Otherwise returns {@code false}.
   * 
   * @param existingMappings the list of existing business mappings
   * @param bmToCheck the business mapping to check for existance
   * @return {@code true} if the specified business mapping {@code bmToCheck} already exists, {@code false} otherwise
   */
  boolean doesMappingExist(List<BusinessMapping> existingMappings, BusinessMapping bmToCheck);

}