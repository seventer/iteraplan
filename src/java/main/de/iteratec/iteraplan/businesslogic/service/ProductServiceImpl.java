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

import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.ProductDAO;


/**
 * Implementation of the service interface {@link ProductService}.
 */
public class ProductServiceImpl extends AbstractHierarchicalBuildingBlockService<Product> implements ProductService {

  private ProductDAO             productDAO;
  private BusinessMappingService businessMappingService;

  @Override
  protected void checkDelete(Product buildingBlock) {
    super.checkDelete(buildingBlock);
    checkDeletePermissionsCurrentPerms(buildingBlock);
  }

  @Override
  protected void onAfterDelete(Product buildingBlock) {
    getAttributeValueService().removeOrphanedAttributeValuesAndAssignments();
  }

  /** {@inheritDoc} */
  @Override
  protected DAOTemplate<Product, Integer> getDao() {
    return productDAO;
  }

  public void setBusinessMappingService(BusinessMappingService businessMappingService) {
    this.businessMappingService = businessMappingService;
  }

  public void setProductDAO(ProductDAO productDAO) {
    this.productDAO = productDAO;
  }

  /**
   * Checks, if the given product contains duplicate business mappings.
   * 
   * @param product The product to check.
   * @throws IteraplanBusinessException If the product contains duplicate business mappings.
   */
  private void checkForDuplicateBusinessMapping(Product product) {
    final Set<String> set = Sets.newHashSet();
    StringBuilder sb = new StringBuilder(20);

    for (BusinessMapping mapping : product.getBusinessMappings()) {

      sb.append("[");
      sb.append(mapping.getInformationSystemRelease().getName());
      sb.append(" , ");
      sb.append(mapping.getBusinessProcess().getName());
      sb.append(" , ");
      sb.append(mapping.getBusinessUnit().getName());
      sb.append("]");

      if (!set.add(sb.toString())) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_BUSINESS_MAPPINGS_ONEDIT, sb.toString());
      }

      sb.setLength(0);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Product saveOrUpdate(Product entity, boolean cleanup) {
    checkForDuplicateBusinessMapping(entity);

    for (BusinessMapping businessMapping : entity.getBusinessMappings()) {
      getAttributeValueService().saveOrUpdateAttributeValues(businessMapping);
      businessMappingService.saveOrUpdate(businessMapping);
    }
    getAttributeValueService().saveOrUpdateAttributeValues(entity);
    Product savedProduct = super.saveOrUpdate(entity, cleanup);

    if (cleanup) {
      businessMappingService.deleteOrphanedBusinessMappings();
    }

    return savedProduct;
  }
}