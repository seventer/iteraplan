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

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingItems;


/**
 * {@link BusinessMappingStrategy} implementation for the {@link InformationSystemRelease}.
 *
 */
public class InformationSystemReleaseMappingStrategy implements BusinessMappingStrategy {

  /** {@inheritDoc} */
  public List<BusinessMapping> createBusinessMappings(BusinessMappingItems bmi) {
    final List<BusinessMapping> result = Lists.newArrayList();

    for (BusinessProcess bp : bmi.getBusinessProcesses()) {
      for (BusinessUnit bu : bmi.getBusinessUnits()) {
        for (Product pr : bmi.getProducts()) {
          BusinessMapping businessMapping = BuildingBlockFactory.createBusinessMapping();
          businessMapping.setBusinessProcess(bp);
          businessMapping.setBusinessUnit(bu);
          businessMapping.setProduct(pr);
          
          if (!hasTopLevelElementsOnly(businessMapping)) {
            result.add(businessMapping);
          }
        }
      }
    }

    return result;
  }

  private boolean hasTopLevelElementsOnly(BusinessMapping bm) {
    final String bpName = bm.getBusinessProcess().getName();
    final String buName = bm.getBusinessUnit().getName();
    final String pName = bm.getProduct().getName();
    
    return BusinessProcess.TOP_LEVEL_NAME.equals(bpName) && BusinessUnit.TOP_LEVEL_NAME.equals(buName) && Product.TOP_LEVEL_NAME.equals(pName);
  }
  
  /** {@inheritDoc} */
  public void validate(BusinessMappingItems bmi) {
    if (bmi.getBusinessProcesses().isEmpty() || bmi.getBusinessUnits().isEmpty() || bmi.getProducts().isEmpty()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CANNOT_ADD_INVALID_BUSINESS_MAPPINGS);
    }
  }
  
  /** {@inheritDoc} */
  public <T extends BuildingBlock> void addOwningEntity(BusinessMapping businessMapping, T source) {
    if (source.getId() != null) {
      // test needed when saving new business mapping, because of the NullPointer- and LazyInitException
      businessMapping.addInformationSystemRelease(SpringServiceFactory.getInformationSystemReleaseService().loadObjectById(source.getId()));
    }
    else {
      businessMapping.addInformationSystemRelease((InformationSystemRelease) source);
    }
  }
  
  /** {@inheritDoc} */
  public boolean doesMappingExist(List<BusinessMapping> existingMappings, BusinessMapping bmToCheck) {
    for (BusinessMapping existingMapping : existingMappings) {
      if (equalsIds(existingMapping, bmToCheck)) {
        return true;
      }
    }
    
    return false;
  }
  
  public boolean equalsIds(BusinessMapping existingBm, BusinessMapping newBm) {
    final BusinessProcess bp = newBm.getBusinessProcess();
    final Product product = newBm.getProduct();    
    final BusinessUnit bu = newBm.getBusinessUnit();
    
    boolean isAnyParameterNull = bp.getId() == null || bu.getId() == null || product.getId() == null;
    boolean isAnyReferenceNull = existingBm.getBusinessProcess() == null || existingBm.getBusinessUnit() == null || existingBm.getProduct() == null;
    
    if (isAnyParameterNull || isAnyReferenceNull) {
      return false;
    }
    
    return bp.getId().equals(existingBm.getBusinessProcess().getId()) && bu.getId().equals(existingBm.getBusinessUnit().getId()) && product.getId().equals(existingBm.getProduct().getId());
  }
}
