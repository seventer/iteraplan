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
import java.util.Set;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.HashBucketMatrix;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.persistence.dao.BusinessMappingDAO;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;


/**
 * Implementation of the service interface {@link BusinessMappingService}.
 */
public class BusinessMappingServiceImpl extends AbstractBuildingBlockService<BusinessMapping> implements BusinessMappingService {

  private BusinessMappingDAO businessMappingDAO;

  /** {@inheritDoc} */
  public List<BusinessMapping> getBusinessMappingsWithNoFunctions(BuildingBlock entity) {
    if (entity instanceof InformationSystemRelease) {
      return businessMappingDAO.getBusinessMappingsConnectedToISR(entity.getId());
    }
    else if (entity instanceof BusinessUnit) {
      return businessMappingDAO.getBusinessMappingsConnectedToBU(entity.getId());
    }
    else if (entity instanceof BusinessProcess) {
      return businessMappingDAO.getBusinessMappingsConnectedToBP(entity.getId());
    }
    else if (entity instanceof Product) {
      return businessMappingDAO.getBusinessMappingsConnectedToProduct(entity.getId());
    }
    else {
      throw new IllegalArgumentException("Unsupported type in business mapping");
    }

  }

  /**
   * {@inheritDoc}
   */
  public BusinessMapping getBusinessMappingByRelatedBuildingBlockIds(final Integer prodId, final Integer buId, final Integer bpId, final Integer isrId) {
    return businessMappingDAO.getBusinessMappingConnectedToProductAndBUAndBPAndISR(prodId, buId, bpId, isrId);
  }

  public void setBusinessMappingDAO(BusinessMappingDAO dao) {
    this.businessMappingDAO = dao;
  }

  /** {@inheritDoc} */
  @Override
  protected DAOTemplate<BusinessMapping, Integer> getDao() {
    return businessMappingDAO;
  }

  /** {@inheritDoc} */
  public List<BusinessMapping> findByNames(Set<String> names) {
    throw new IllegalStateException("Business Mappings do not have the name!");
  }

  /** {@inheritDoc} */
  public int deleteOrphanedBusinessMappings() {
    int deleteOrphanedBusinessMappings = businessMappingDAO.deleteOrphanedBusinessMappings();
    getAttributeValueService().removeOrphanedAttributeValuesAndAssignments();
    return deleteOrphanedBusinessMappings;
  }

  /** {@inheritDoc} */
  public HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock> getTabelData(TypeOfBuildingBlock tobbForElement, Integer elementId,
                                                                                    TypeOfBuildingBlock tobbForX, TypeOfBuildingBlock tobbForY) {
    TypeOfBuildingBlock tobbForResult = checkTobbsAndReturnLast(tobbForElement, tobbForX, tobbForY);
    HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock> tableData = new HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock>();
    for (BusinessMapping bm : this.loadElementList()) {
      BuildingBlock first = getBBFromBMForTobb(bm, tobbForElement);
      if (first != null && first.getId().equals(elementId)) {
        BuildingBlock x = getBBFromBMForTobb(bm, tobbForX);
        BuildingBlock y = getBBFromBMForTobb(bm, tobbForY);
        BuildingBlock res = getBBFromBMForTobb(bm, tobbForResult);
        if (x != null && y != null && res != null) {
          tableData.add(x, y, res);
        }
      }
    }
    return tableData;
  }

  /**
   * Checks if the three types of building blocks form a valid business mapping. The last building block type will be determined, and returned.
   * @param tobbForElement
   * @param tobbForRow
   * @param tobbForColumn
   * @return the fourth building block type (for content).
   */
  private TypeOfBuildingBlock checkTobbsAndReturnLast(TypeOfBuildingBlock tobbForElement, TypeOfBuildingBlock tobbForRow,
                                                      TypeOfBuildingBlock tobbForColumn) {
    List<TypeOfBuildingBlock> tobbNeeded = Lists.newArrayList(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, TypeOfBuildingBlock.PRODUCT,
        TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.BUSINESSPROCESS);
    tobbNeeded.remove(tobbForElement);
    tobbNeeded.remove(tobbForRow);
    tobbNeeded.remove(tobbForColumn);
    if (tobbNeeded.size() == 1) {
      return tobbNeeded.get(0);
    }
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

  /**
   * Return the building block connected in the business mapping, which is from the given type.
   * @param bm business mapping, containing the wanted building block.
   * @param tobb type of the building block.
   * @return building block.
   */
  private BuildingBlock getBBFromBMForTobb(BusinessMapping bm, TypeOfBuildingBlock tobb) {
    switch (tobb) {
      case INFORMATIONSYSTEMRELEASE:
        return bm.getInformationSystemRelease();
      case PRODUCT:
        return bm.getProduct();
      case BUSINESSUNIT:
        return bm.getBusinessUnit();
      case BUSINESSPROCESS:
        return bm.getBusinessProcess();
      default:
        return null;
    }
  }

  @Override
  protected void checkDelete(BusinessMapping buildingBlock) {
    super.checkDelete(buildingBlock);
    checkDeletePermissionsCurrentPerms(buildingBlock);
  }

  @Override
  protected void onAfterDelete(BusinessMapping buildingBlock) {
    getAttributeValueService().removeOrphanedAttributeValuesAndAssignments();
  }
}