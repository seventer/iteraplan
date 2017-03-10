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
package de.iteratec.iteraplan.presentation.dialog.BusinessMapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BusinessMappingService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessMappingEntity;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.sorting.NonHierarchicalEntityComparator;
import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;
import de.iteratec.iteraplan.presentation.dialog.CMBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.BusinessMapping.BusinessMappingTableComponentModel.CellComponentModel;
import de.iteratec.iteraplan.presentation.dialog.BusinessMapping.BusinessMappingTableComponentModel.MultiCellComponentModel;
import de.iteratec.iteraplan.presentation.dialog.BusinessMapping.BusinessMappingTableComponentModel.RowList;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * This Service Implementation offers actions used by Spring Webflow to create and delete
 * business mappings i a table view.
 */
@Service("businessMappingFrontendService")
public class BusinessMappingFrontendServiceImpl extends CMBaseFrontendServiceImpl<BusinessMappingMemBean, BusinessMappingService> implements
    BuildingBlockFrontendService<BusinessMappingMemBean> {

  @Autowired
  private BusinessMappingService      businessMappingService;

  @Autowired
  private BuildingBlockServiceLocator buildingBlockServiceLocator;

  @Autowired
  private InitFormHelperService       initFormHelperService;

  /**
   * {@inheritDoc}
   */
  @Override
  public BusinessMappingService getService() {
    return businessMappingService;
  }

  public void setService(BusinessMappingService businessMappingService) {
    this.businessMappingService = businessMappingService;
  }

  public void setBuildingBlockServiceLocator(BuildingBlockServiceLocator buildingBlockServiceLocator) {
    this.buildingBlockServiceLocator = buildingBlockServiceLocator;
  }

  public InitFormHelperService getInitFormHelperService() {
    return initFormHelperService;
  }

  public void setInitFormHelperService(InitFormHelperService initFormHelperService) {
    this.initFormHelperService = initFormHelperService;
  }

  /**
   * Returns the instance of the business mapping with the given identifier. If the entity does not exist, an
   * exception is thrown.
   * @param id the ID of the business mapping to load
   * @return The business mapping. An exception is thrown if the entity does not exist. If the passed-in ID is
   *         {@code null}, {@code null} is returned.
   */
  protected BusinessMapping getModelObjectById(Integer id) {
    BusinessMapping businessMapping = businessMappingService.loadObjectById(id);
    if (businessMapping == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ);
    }
    return businessMapping;
  }

  /**
   * there is no delete option for business mappings. This will throw an exception.
   */
  public boolean deleteEntity(Integer currentId, RequestContext context, FlowExecutionContext flowContext) {
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

  /**
   * method is not available. This will throw an exception.
   * the {@link BusinessMappingFrontendServiceImpl#getInitialMemBean()} method will provide a new mem bean.
   */
  public BusinessMappingMemBean getMemBean(Integer currentId) {
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

  /**
   * Retruns a new mem bean with a new component model in read mode.
   * The settings are set to default.
   * The table will not be initialized.
   * @return mem bean
   */
  public BusinessMappingMemBean getInitialMemBean() {
    BusinessMappingMemBean memBean = new BusinessMappingMemBean();
    BusinessMappingComponentModel cm = new BusinessMappingComponentModel();
    memBean.setComponentModel(cm);

    BusinessMappingSettingsComponentModel settings = cm.getSettings();
    settings.setBbListForSelection(getBbs(settings.getFirstType()));

    return memBean;
  }

  /**
   * Retruns the old mem bean with a new component model in read mode. 
   * The settings and the table are taken over from the old component model.
   * The table will be reloaded.
   * @param memBean
   * @param entityId
   * @param context
   * @param flowContext
   * @return mem bean
   */
  public BusinessMappingMemBean getMemBeanWithOldCmData(BusinessMappingMemBean memBean, Integer entityId, RequestContext context,
                                                        FlowExecutionContext flowContext) {
    BusinessMappingComponentModel oldCm = memBean.getComponentModel();
    oldCm.getTable().setBusinessMappingTableData(loadTableData(oldCm.getSettings()));
    BusinessMappingComponentModel newCm = new BusinessMappingComponentModel(ComponentMode.READ);
    newCm.initializeFrom(oldCm);
    memBean.setComponentModel(newCm);
    return memBean;
  }

  /**
   * Retruns the old mem bean with a new component model in edit mode.
   * The settings and the table are taken over from the old component model.
   * The table will be reloaded.
   * @param memBean
   * @param entityId
   * @param context
   * @param flowContext
   * @return mem bean
   */
  public BusinessMappingMemBean getEditMemBean(BusinessMappingMemBean memBean, Integer entityId, RequestContext context,
                                               FlowExecutionContext flowContext) {
    BusinessMappingComponentModel oldCm = memBean.getComponentModel();
    oldCm.getTable().setBusinessMappingTableData(loadTableData(oldCm.getSettings()));
    BusinessMappingComponentModel newCm = new BusinessMappingComponentModel(ComponentMode.EDIT);
    newCm.initializeFrom(oldCm);
    memBean.setComponentModel(newCm);
    return memBean;
  }

  /**
   * there is no create option for business mappings. This will throw an exception.
   */
  public BusinessMappingMemBean getCreateMemBean(FlowExecutionContext flowContext) {
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

  /**
   * {@inheritDoc}
   */
  public boolean saveComponentModel(BusinessMappingMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    BusinessMappingComponentModel cm = memBean.getComponentModel();
    cm.update();

    boolean createPermission = UserContext.getCurrentUserContext().getPerms().userHasBbTypeCreatePermission(TypeOfBuildingBlock.BUSINESSMAPPING);
    boolean deletePermission = UserContext.getCurrentUserContext().getPerms().userHasBbTypeDeletePermission(TypeOfBuildingBlock.BUSINESSMAPPING);

    BusinessMappingTable oldBmt = cm.getTable().getBusinessMappingTableData();
    BusinessMappingTable newBmt = cm.getTable().createNewBusinessMappingTable();

    BuildingBlockService<BuildingBlock, Integer> firstElementService = buildingBlockServiceLocator.getService(TypeOfBuildingBlock
        .getTypeOfBuildingBlockByString(cm.getSettings().getFirstType()));
    BuildingBlockService<BuildingBlock, Integer> rowElementService = buildingBlockServiceLocator.getService(TypeOfBuildingBlock
        .getTypeOfBuildingBlockByString(cm.getSettings().getRowType()));
    BuildingBlockService<BuildingBlock, Integer> columnElementService = buildingBlockServiceLocator.getService(TypeOfBuildingBlock
        .getTypeOfBuildingBlockByString(cm.getSettings().getColumnType()));
    BuildingBlockService<BuildingBlock, Integer> contentElementService = buildingBlockServiceLocator.getService(TypeOfBuildingBlock
        .getTypeOfBuildingBlockByString(cm.getSettings().getContentType()));

    BuildingBlock firstBB = oldBmt.getFirstBuildingBlock();
    firstBB = firstElementService.merge(firstBB);

    for (BuildingBlock rowBB : rowElementService.reload(cm.getTable().getBbPageForRow())) {
      for (BuildingBlock columnBB : columnElementService.reload(cm.getTable().getBbPageForColumn())) {
        List<BuildingBlock> oldContent = oldBmt.getTableData().getBucketNotNull(rowBB, columnBB);
        List<BuildingBlock> newContent = newBmt.getTableData().getBucketNotNull(rowBB, columnBB);

        Set<BuildingBlock> toCreate = Sets.newHashSet(newContent);
        toCreate.removeAll(oldContent);

        if (!createPermission && !toCreate.isEmpty()) {
          throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
        }

        for (BuildingBlock contentBB : contentElementService.reload(toCreate)) {
          BusinessMapping bm = createBM(firstBB, rowBB, columnBB, contentBB);
          addBusinessMapping(firstBB, bm);
        }

        Set<BuildingBlock> toDelete = Sets.newHashSet(oldContent);
        toDelete.removeAll(newContent);

        if (!deletePermission && !toDelete.isEmpty()) {
          throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
        }

        for (BuildingBlock contentBB : contentElementService.reload(toDelete)) {
          BusinessMapping bm = deleteBM(firstBB, rowBB, columnBB, contentBB);
          removeBusinessMapping(firstBB, bm);
        }
      }
    }

    firstElementService.saveOrUpdate(firstBB);
    leaveEditMode(flowContext);
    return true;
  }

  /**
   * Creates a business mapping with four building blocks. Also checks, if this is a possible business mapping.
   * If not, an exception will be thrown.
   * @param bbs building blocks which should form a business mapping.
   * @throws IteraplanTechnicalException if the building blocks will not form a valid business mapping.
   */
  private BusinessMapping createBM(BuildingBlock... bbs) {
    List<BuildingBlock> bmElements = Lists.newArrayList(bbs);
    BusinessMapping bm = BuildingBlockFactory.createBusinessMapping();
    bm.setInformationSystemRelease((InformationSystemRelease) findBBWithType(bmElements, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    bm.setProduct((Product) findBBWithType(bmElements, TypeOfBuildingBlock.PRODUCT));
    bm.setBusinessUnit((BusinessUnit) findBBWithType(bmElements, TypeOfBuildingBlock.BUSINESSUNIT));
    bm.setBusinessProcess((BusinessProcess) findBBWithType(bmElements, TypeOfBuildingBlock.BUSINESSPROCESS));
    return bm;
  }

  /**
   * Deletes a business mapping with four building blocks. Also checks, if this is a possible business mapping.
   * If not, an exception will be thrown.
   * @param bbs building blocks which should form a business mapping.
   * @throws IteraplanTechnicalException if the building blocks will not form a valid business mapping.
   */
  private BusinessMapping deleteBM(BuildingBlock... bbs) {
    List<BuildingBlock> bmElements = Lists.newArrayList(bbs);
    Integer prodId = findBBWithType(bmElements, TypeOfBuildingBlock.PRODUCT).getId();
    Integer buId = findBBWithType(bmElements, TypeOfBuildingBlock.BUSINESSUNIT).getId();
    Integer bpId = findBBWithType(bmElements, TypeOfBuildingBlock.BUSINESSPROCESS).getId();
    Integer isrId = findBBWithType(bmElements, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE).getId();
    BusinessMapping bm = businessMappingService.getBusinessMappingByRelatedBuildingBlockIds(prodId, buId, bpId, isrId);
    bm.removeBusinessProcess();
    bm.removeBusinessUnit();
    bm.removeInformationSystemRelease();
    bm.removeProduct();
    return bm;
  }

  /**
   * adds a business mapping to the building block
   * @param bb building block
   * @param bm business mapping
   */
  private void addBusinessMapping(BuildingBlock bb, BusinessMapping bm) {
    if (bb instanceof BusinessMappingEntity) {
      ((BusinessMappingEntity) bb).addBusinessMapping(bm);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
  }

  /**
   * removes a business mapping from the building block
   * @param bb building block
   * @param bm business mapping
   */
  private void removeBusinessMapping(BuildingBlock bb, BusinessMapping bm) {
    if (bb instanceof Product) {
      ((Product) bb).getBusinessMappings().remove(bm);
    }
    else if (bb instanceof BusinessUnit) {
      ((BusinessUnit) bb).getBusinessMappings().remove(bm);
    }
    else if (bb instanceof BusinessProcess) {
      ((BusinessProcess) bb).getBusinessMappings().remove(bm);
    }
    else if (bb instanceof InformationSystemRelease) {
      ((InformationSystemRelease) bb).getBusinessMappings().remove(bm);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }
  }

  /**
   * Searches in a list of building blocks the building block with the specific type. The building block
   * will be removed from the list.
   * @param bmElements list of building blocks, where to search.
   * @param tobb type of building block to search.
   * @return the building block, found with the type.
   * @throws IteraplanTechnicalException if the list contains no building block with the specific type.
   */
  private BuildingBlock findBBWithType(List<BuildingBlock> bmElements, TypeOfBuildingBlock tobb) {
    for (BuildingBlock bb : bmElements) {
      if (bb.getTypeOfBuildingBlock() == tobb) {
        bmElements.remove(bb);
        return bb;
      }
    }
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

  /**
   * This method will move the content of row and column updates to the cell components.
   * @param memBean
   * @param context
   * @param flowContext
   */
  public void rowColumnUpdate(BusinessMappingMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    BusinessMappingComponentModel cm = memBean.getComponentModel();
    BusinessMappingTableComponentModel table = cm.getTable();
    cm.update();

    List<RowList> rowLists = table.getCellComponentsRows();

    MultiCellComponentModel allUpdater = table.getAllUpdateComponent();
    List<Integer> elementIdsToConnect = getIds(allUpdater);
    for (RowList rl : rowLists) {
      for (CellComponentModel cell : rl.getCellComponents()) {
        updateCell(elementIdsToConnect, cell);
      }
    }

    List<MultiCellComponentModel> rowUpdater = table.getRowUpdateComponents();
    int rowCount = rowUpdater.size();
    for (int i = 0; i < rowCount; i++) {
      elementIdsToConnect = getIds(rowUpdater.get(i));
      for (CellComponentModel cell : rowLists.get(i).getCellComponents()) {
        updateCell(elementIdsToConnect, cell);
      }
    }

    List<MultiCellComponentModel> columnUpdater = table.getColumnUpdateComponents();
    int columnCount = columnUpdater.size();
    for (int i = 0; i < columnCount; i++) {
      elementIdsToConnect = getIds(columnUpdater.get(i));
      for (RowList rl : rowLists) {
        CellComponentModel cell = rl.getCellComponents().get(i);
        updateCell(elementIdsToConnect, cell);
      }
    }
  }

  /**
   * Converts the connected elements in the component model to an array of id's.
   * @param componentModel the component model containing the connected elements.
   * @return list with the id's.
   */
  private List<Integer> getIds(MultiCellComponentModel componentModel) {
    List<BuildingBlock> elementsToConnect = componentModel.getConnectedElements();
    List<Integer> elementIdsToConnect = Lists.newArrayList();
    for (BuildingBlock bb : elementsToConnect) {
      elementIdsToConnect.add(bb.getId());
    }
    return elementIdsToConnect;
  }

  /**
   * adds a list of element id's to the cell component model.
   * @param elementIdsToConnect element id's which should be added to the cell component model.
   * @param cell the cell component model.
   */
  private void updateCell(List<Integer> elementIdsToConnect, CellComponentModel cell) {
    Integer[] idArray = new Integer[elementIdsToConnect.size()];
    idArray = elementIdsToConnect.toArray(idArray);
    cell.setElementIdsToAdd(idArray);
    cell.update();
  }

  /**
   * there is no save option for new business mappings. This will throw an exception.
   */
  public Integer saveNewComponentModel(BusinessMappingMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

  public BusinessMappingMemBean changeSettings(BusinessMappingMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    BusinessMappingComponentModel cm = memBean.getComponentModel();
    BusinessMappingSettingsComponentModel settings = cm.getSettings();
    settings.setBbListForSelection(getBbs(settings.getFirstType()));
    cm.setTable(null);
    return memBean;
  }

  public BusinessMappingMemBean sendBusinessMappingsRequest(BusinessMappingMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    BusinessMappingComponentModel cm = memBean.getComponentModel();

    BusinessMappingTableComponentModel table = new BusinessMappingTableComponentModel(cm.getComponentMode());
    cm.setTable(table);

    return memBean;
  }

  private List<BuildingBlock> getBbs(String tobb) {
    List<BuildingBlock> elementList = buildingBlockServiceLocator.getService(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(tobb))
        .loadElementList();
    Collections.sort(elementList, new NonHierarchicalEntityComparator<BuildingBlock>());
    return elementList;
  }

  /**
   * Creates a business mapping table with connected mappings, with the informations in the component model.
   * @param cm component model containing the informations about the table.
   * @return new business mapping table with data.
   */
  private BusinessMappingTable loadTableData(BusinessMappingSettingsComponentModel settings) {
    BusinessMappingTable bmt = new BusinessMappingTable();

    String firstType = settings.getFirstType();
    String selectedColumn = settings.getColumnType();
    String selectedRow = settings.getRowType();
    String selectedContent = settings.getContentType();
    Integer bbId = settings.getSelectedBbId();

    TypeOfBuildingBlock tobbForFirst = TypeOfBuildingBlock.fromPropertyString(firstType);
    TypeOfBuildingBlock tobbForRow = TypeOfBuildingBlock.fromPropertyString(selectedRow);
    TypeOfBuildingBlock tobbForColumn = TypeOfBuildingBlock.fromPropertyString(selectedColumn);
    bmt.setTableData(businessMappingService.getTabelData(tobbForFirst, bbId, tobbForRow, tobbForColumn));

    bmt.setFirstBuildingBlock(buildingBlockServiceLocator.getService(tobbForFirst).loadObjectById(bbId));
    bmt.setBbForRow(this.getBbs(selectedRow));
    bmt.setBbForColumn(this.getBbs(selectedColumn));
    bmt.setBbForContent(this.getBbs(selectedContent));

    return bmt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getFlowId() {
    return Dialog.BUSINESS_MAPPING.getFlowId();
  }

}
