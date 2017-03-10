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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.EntityService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.HashBucketMatrix;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModelDL;

/**
 * The component model for the table part in business mappings.
 */
public class BusinessMappingTableComponentModel extends AbstractComponentModelBase<BusinessMappingTableComponentModel> {
  private static final long             serialVersionUID         = 1044493843359945353L;

  /** Number of columns displayed on one page */
  private int                           columnsPerPage           = Constants.BUSINESS_MAPPING_TABLE_MAX_COLUMNS;
  /** Number of rows displayed on one page */
  private int                           rowsPerPage              = Constants.BUSINESS_MAPPING_TABLE_MAX_ROWS;

  /** The column page number, which is displayed (starts with 0) */
  private int                           actualColumnPage         = 0;
  /** The row page number, which is displayed (starts with 0)*/
  private int                           actualRowPage            = 0;

  /** Contains the informations for the table. */
  private BusinessMappingTable          businessMappingTableData = new BusinessMappingTable();
  /** Matrix with row and column bb's as index, to get a component Model for each cell. */
  private List<RowList>                 cellComponentsRows       = null;

  /** is row and column update enabled. */
  private boolean                       rowColumnUpdateEnabled   = false;
  /** The all update component Model */
  private MultiCellComponentModel       allUpdateComponent       = null;
  /** List with row update component Models */
  private List<MultiCellComponentModel> rowUpdateComponents      = null;
  /** List with column update component Models */
  private List<MultiCellComponentModel> columnUpdateComponents   = null;

  /**
   * @param componentMode
   */
  protected BusinessMappingTableComponentModel(ComponentMode componentMode) {
    super(componentMode);
  }

  public BusinessMappingTable getBusinessMappingTableData() {
    return businessMappingTableData;
  }

  public void setBusinessMappingTableData(BusinessMappingTable businessMappingTableData) {
    this.businessMappingTableData = businessMappingTableData;
  }

  public List<RowList> getCellComponentsRows() {
    return cellComponentsRows;
  }

  public void setCellComponentsRows(List<RowList> cellComponentsRows) {
    this.cellComponentsRows = cellComponentsRows;
  }

  public boolean isRowColumnUpdateEnabled() {
    return rowColumnUpdateEnabled;
  }

  public void setRowColumnUpdateEnabled(boolean rowColumnUpdateEnabled) {
    this.rowColumnUpdateEnabled = rowColumnUpdateEnabled;
  }

  public MultiCellComponentModel getAllUpdateComponent() {
    return allUpdateComponent;
  }

  public void setAllUpdateComponent(MultiCellComponentModel allUpdateComponent) {
    this.allUpdateComponent = allUpdateComponent;
  }

  public List<MultiCellComponentModel> getRowUpdateComponents() {
    return rowUpdateComponents;
  }

  public void setRowUpdateComponents(List<MultiCellComponentModel> rowUpdateComponents) {
    this.rowUpdateComponents = rowUpdateComponents;
  }

  public List<MultiCellComponentModel> getColumnUpdateComponents() {
    return columnUpdateComponents;
  }

  public void setColumnUpdateComponents(List<MultiCellComponentModel> columnUpdateComponents) {
    this.columnUpdateComponents = columnUpdateComponents;
  }

  public int getColumnsPerPage() {
    return columnsPerPage;
  }

  public void setColumnsPerPage(int columnsPerPage) {
    if (columnsPerPage <= 0) {
      this.columnsPerPage = 1;
    }
    else {
      this.columnsPerPage = columnsPerPage;
    }
    setActualColumnPage(getActualColumnPage());
  }

  public int getRowsPerPage() {
    return rowsPerPage;
  }

  public void setRowsPerPage(int rowsPerPage) {
    if (rowsPerPage <= 0) {
      this.rowsPerPage = 1;
    }
    else {
      this.rowsPerPage = rowsPerPage;
    }
    setActualRowPage(getActualRowPage());
  }

  public int getActualColumnPage() {
    return actualColumnPage;
  }

  public void setActualColumnPage(int actualColumnPage) {
    if (actualColumnPage < 0) {
      this.actualColumnPage = 0;
    }
    else if (actualColumnPage >= getNumberOfColumnPages()) {
      this.actualColumnPage = getNumberOfColumnPages();
    }
    else {
      this.actualColumnPage = actualColumnPage;
    }
  }

  public int getActualRowPage() {
    return actualRowPage;
  }

  public void setActualRowPage(int actualRowPage) {
    if (actualRowPage < 0) {
      this.actualRowPage = 0;
    }
    else if (actualRowPage >= getNumberOfRowPages()) {
      this.actualRowPage = getNumberOfRowPages();
    }
    else {
      this.actualRowPage = actualRowPage;
    }
  }

  public int getNumberOfColumnPages() {
    return (businessMappingTableData.getBbForColumn().size() - 1) / getColumnsPerPage();
  }

  public int getNumberOfRowPages() {
    return (businessMappingTableData.getBbForRow().size() - 1) / getRowsPerPage();
  }

  public List<BuildingBlock> getBbPageForColumn() {
    return getPage(businessMappingTableData.getBbForColumn(), actualColumnPage, columnsPerPage);
  }

  public List<BuildingBlock> getBbPageForRow() {
    return getPage(businessMappingTableData.getBbForRow(), actualRowPage, rowsPerPage);
  }

  /**
   * {@inheritDoc}
   */
  public void initializeFrom(BusinessMappingTableComponentModel source) {
    this.businessMappingTableData = source.getBusinessMappingTableData();
    this.columnsPerPage = source.columnsPerPage;
    this.rowsPerPage = source.rowsPerPage;
    this.actualColumnPage = source.actualColumnPage;
    this.actualRowPage = source.actualRowPage;
    laodActualPage();
  }

  /**
   * This method must be called after changing the actual page numbers.
   * It will create new components for this page.
   */
  public void laodActualPage() {
    List<BuildingBlock> bbForColumn = getBbPageForColumn();
    List<BuildingBlock> bbForRow = getBbPageForRow();

    loadPage(bbForRow, bbForColumn);
  }

  /**
   * returns a sublist from a list.
   * @param list
   * @param page number of the page
   * @param pageSize size of the page
   * @return sublist
   */
  private <T> List<T> getPage(List<T> list, int page, int pageSize) {
    List<T> result = Lists.newArrayList();
    int start = Math.max(page * pageSize, 0);
    int stop = Math.min(start + pageSize, list.size());
    for (int i = start; i < stop; i++) {
      result.add(list.get(i));
    }
    return result;
  }

  /**
   * Creates the component models for the table. Cells are created for each combination of (row, column),
   * the row and column updater for each element in row and column and the all update component model.
   * @param bbForRow building blocks for row, which should be used for the component models.
   * @param bbForColumn building blocks for column, which should be used for the component models.
   */
  private void loadPage(List<BuildingBlock> bbForRow, List<BuildingBlock> bbForColumn) {
    // Cell components
    cellComponentsRows = Lists.newArrayList();
    for (BuildingBlock rowBB : bbForRow) {
      List<CellComponentModel> cellComponents = Lists.newArrayList();
      for (BuildingBlock columnBB : bbForColumn) {
        CellComponentModel cell = new CellComponentModel(getComponentMode(), rowBB, columnBB, businessMappingTableData.getBbForContent().get(0));
        cell.initializeFrom(businessMappingTableData);
        cellComponents.add(cell);
      }
      cellComponentsRows.add(new RowList(cellComponents));
    }

    // All update component
    allUpdateComponent = new MultiCellComponentModel(getComponentMode(), businessMappingTableData.getBbForContent().get(0));
    allUpdateComponent.initializeFrom(businessMappingTableData);

    // Row update components
    rowUpdateComponents = Lists.newArrayList();
    for (int i = bbForRow.size(); i > 0; i--) {
      MultiCellComponentModel rowUpdateCM = new MultiCellComponentModel(getComponentMode(), businessMappingTableData.getBbForContent().get(0));
      rowUpdateCM.initializeFrom(businessMappingTableData);
      rowUpdateComponents.add(rowUpdateCM);
    }

    // Column update components
    columnUpdateComponents = Lists.newArrayList();
    for (int i = bbForColumn.size(); i > 0; i--) {
      MultiCellComponentModel columnUpdateCM = new MultiCellComponentModel(getComponentMode(), businessMappingTableData.getBbForContent().get(0));
      columnUpdateCM.initializeFrom(businessMappingTableData);
      columnUpdateComponents.add(columnUpdateCM);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void update() {
    for (RowList row : cellComponentsRows) {
      for (CellComponentModel cellComponent : row.getCellComponents()) {
        cellComponent.update();
      }
    }
    allUpdateComponent.update();
    for (MultiCellComponentModel rowUpdater : rowUpdateComponents) {
      rowUpdater.update();
    }
    for (MultiCellComponentModel columnUpdater : columnUpdateComponents) {
      columnUpdater.update();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void configure(BusinessMappingTableComponentModel target) {
    target.businessMappingTableData = createNewBusinessMappingTable();
    target.columnsPerPage = this.columnsPerPage;
    target.rowsPerPage = this.rowsPerPage;
    target.actualColumnPage = this.actualColumnPage;
    target.actualRowPage = this.actualRowPage;
    target.laodActualPage();
  }

  /**
   * Creates a new business mapping table with the data from component models.
   * This is not a full table as the table for initialization. This one contains only
   * the data for the actual page.
   * @return new business mapping table.
   */
  public BusinessMappingTable createNewBusinessMappingTable() {
    BusinessMappingTable bmt = new BusinessMappingTable();
    bmt.setFirstBuildingBlock(getBusinessMappingTableData().getFirstBuildingBlock());
    bmt.setBbForRow(getBusinessMappingTableData().getBbForRow());
    bmt.setBbForColumn(getBusinessMappingTableData().getBbForColumn());
    bmt.setBbForContent(getBusinessMappingTableData().getBbForContent());
    bmt.setTableData(new HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock>());
    for (RowList row : cellComponentsRows) {
      for (CellComponentModel cellComponent : row.getCellComponents()) {
        cellComponent.configure(bmt);
      }
    }
    allUpdateComponent.configure(bmt);
    for (MultiCellComponentModel rowUpdater : rowUpdateComponents) {
      rowUpdater.configure(bmt);
    }
    for (MultiCellComponentModel columnUpdater : columnUpdateComponents) {
      columnUpdater.configure(bmt);
    }
    return bmt;
  }

  /**
   * {@inheritDoc}
   */
  public void validate(Errors errors) {
    for (RowList row : cellComponentsRows) {
      for (CellComponentModel cellComponent : row.getCellComponents()) {
        cellComponent.validate(errors);
      }
    }
    allUpdateComponent.validate(errors);
    for (MultiCellComponentModel rowUpdater : rowUpdateComponents) {
      rowUpdater.validate(errors);
    }
    for (MultiCellComponentModel columnUpdater : columnUpdateComponents) {
      columnUpdater.validate(errors);
    }
  }

  /**
   * contains a row of cell component models.
   */
  public static class RowList implements Serializable {
    private static final long        serialVersionUID = 7842844651370394768L;
    private List<CellComponentModel> cellComponents;

    public RowList(List<CellComponentModel> cellComponents) {
      this.cellComponents = cellComponents;
    }

    public List<CellComponentModel> getCellComponents() {
      return this.cellComponents;
    }
  }

  /**
   * A cell component model represents one cell in the table. It knows the building block for his row and column dimension.
   */
  public static class CellComponentModel extends ManyAssociationSetComponentModelDL<BusinessMappingTable, BuildingBlock> {
    private static final long   serialVersionUID = 566034868351235228L;

    /** the building block in the row dimension. */
    private final BuildingBlock rowElement;
    /** the building block in the column dimension. */
    private final BuildingBlock columnElement;

    public CellComponentModel(ComponentMode componentMode, BuildingBlock rowElement, BuildingBlock columnElement, BuildingBlock dummy) {
      super(componentMode, "bm", null, new String[] { "global.name" }, new String[] { "name" }, "hierarchicalName", dummy);
      this.rowElement = rowElement;
      this.columnElement = columnElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<BuildingBlock> getConnectedElements(BusinessMappingTable source) {
      return Sets.newLinkedHashSet(source.getTableData().getBucketNotNull(rowElement, columnElement));
    }
    
    @Override
    protected BuildingBlockService<BuildingBlock, Integer> getService() {
      return SpringServiceFactory.getBuildingBlockServiceLocator().getService(getTypeOfBuildingBlock());
    }
    
    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return dummyForPresentation.getTypeOfBuildingBlock();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setConnectedElements(BusinessMappingTable target, Set<BuildingBlock> toConnect) {
      // this is an empty map since configure(), now fill it with the componentModel data
      HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock> newTable = target.getTableData();

      for (BuildingBlock bb : toConnect) {
        newTable.add(rowElement, columnElement, bb);
      }
    }

  }

  /**
   * The multi cell component model holds values for adding one or more values to all cell components in
   * a row or column. This component model will not configure anything. Other classes should move the
   * connected elements of this component model to the cell component models.
   */
  public static class MultiCellComponentModel extends ManyAssociationSetComponentModelDL<BusinessMappingTable, BuildingBlock> {
    private static final long serialVersionUID = 566034868351235228L;

    public MultiCellComponentModel(ComponentMode componentMode, BuildingBlock dummy) {
      super(componentMode, "bm", null, new String[] { "global.name" }, new String[] { "name" }, "hierarchicalName", dummy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<BuildingBlock> getConnectedElements(BusinessMappingTable source) {
      // the update should be an empty list at the beginning
      return Sets.newLinkedHashSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setConnectedElements(BusinessMappingTable target, Set<BuildingBlock> toConnect) {
      // there is a special button, which will move the elementsToConnect in cells, so here is nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<BuildingBlock> getAvailableElements(Integer id, List<BuildingBlock> connected) {
      return Lists.newArrayList(getManagedElement().getBbForContent());
    }

    /**{@inheritDoc}**/
    @Override
    protected EntityService<BuildingBlock, Integer> getService() {
      return SpringServiceFactory.getBuildingBlockServiceLocator().getService(getTypeOfBuildingBlock());
    }

    /**{@inheritDoc}**/
    @Override
    public TypeOfBuildingBlock getTypeOfBuildingBlock() {
      return dummyForPresentation.getTypeOfBuildingBlock();
    }
  }

}
