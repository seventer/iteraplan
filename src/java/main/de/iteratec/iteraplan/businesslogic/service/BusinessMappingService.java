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

import de.iteratec.iteraplan.common.util.HashBucketMatrix;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Service interface for {@code BusinessMapping}s.
 */
public interface BusinessMappingService extends BuildingBlockService<BusinessMapping, Integer> {

  /**
   * @return Returns a list of {@link BusinessMapping}s of the given {@link BuildingBlock} that do not
   *         contain a reference to any business function, i.e. that reference is {@code null}.
   */
  List<BusinessMapping> getBusinessMappingsWithNoFunctions(BuildingBlock entity);

  /**
   * Returns the business mapping, which connects the building blocks given as id's.
   * @param prodId id from product.
   * @param buId id from business unit.
   * @param bpId id from business process.
   * @param isrId id from information system release.
   * @return business mapping.
   */
  BusinessMapping getBusinessMappingByRelatedBuildingBlockIds(final Integer prodId, final Integer buId, final Integer bpId, final Integer isrId);

  /**
   * Delete all invalid business mappings.
   * 
   * @return the number of deleted business mappings
   */
  int deleteOrphanedBusinessMappings();

  /**
   * creates a table with all business mappings for one building block.
   * @param tobbForElement the type of the building block, connected with all business mappings in the table.
   * @param elementId id of the building block.
   * @param tobbForRow type of building block, which represents the row (first index in matrix)
   * @param tobbForColumn type of building block, which represents the column (second index in matrix)
   * @return a hashBucketMatrix with building blocks of the type tobbForRow as row index, and building blocks of the type tobbForColumn as column index.
   *         the result of the matrix, with these indexes will return a list with building blocks, having a business mapping together with the first 
   *         element building block, the row building block and column building block.
   */
  HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock> getTabelData(TypeOfBuildingBlock tobbForElement, Integer elementId,
                                                                             TypeOfBuildingBlock tobbForRow, TypeOfBuildingBlock tobbForColumn);
}
