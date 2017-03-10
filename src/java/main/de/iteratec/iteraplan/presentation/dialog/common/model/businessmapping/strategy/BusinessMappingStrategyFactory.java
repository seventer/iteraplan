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
/**
 * 
 */
package de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;

/**
 * A factory for creating {@link BusinessMappingStrategy} instances.  
 * 
 * <p>Currently supported strategies are for the following classes:
 * <ul>
 * <li> {@link InformationSystemRelease}
 * <li> {@link BusinessUnit}
 * <li> {@link BusinessProcess}
 * <li> {@link Product}
 * </ul>
 *
 */
public final class BusinessMappingStrategyFactory<T extends BuildingBlock> {
  private static final ImmutableMap<Class<?>, BusinessMappingStrategy> STRATEGIES = initialize();
  
  private BusinessMappingStrategyFactory() {  /* do nothing*/  }
  
  /**
   * Returns the mapping strategy for the specified class {@code type}. 
   * 
   * @param <T> the {@link BuildingBlock} type
   * @param type the class type
   * @return the mapping strategy
   * @throws IteraplanTechnicalException if the mapping strategy for the specified type is not supported
   */
  public static <T extends BuildingBlock> BusinessMappingStrategy getStrategyFor(Class<T> type) {
    if (STRATEGIES.containsKey(type)) {
      return STRATEGIES.get(type);
    }
    
    throw new IteraplanTechnicalException();
  }

  /**
   * Initializes all supported mapping strategies.
   * 
   * @return the immutable map of supported mapping strategies 
   */
  private static ImmutableMap<Class<?>, BusinessMappingStrategy> initialize() {
    final Builder<Class<?>, BusinessMappingStrategy> builder = ImmutableMap.builder();
    
    builder.put(InformationSystemRelease.class, new InformationSystemReleaseMappingStrategy());
    builder.put(BusinessUnit.class, new BusinessUnitMappingStrategy());
    builder.put(BusinessProcess.class, new BusinessProcessMappingStrategy());
    builder.put(Product.class, new ProductMappingStrategy());
    
    return builder.build();
  }
}
