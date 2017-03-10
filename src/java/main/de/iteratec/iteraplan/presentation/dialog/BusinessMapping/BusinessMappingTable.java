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

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.util.HashBucketMatrix;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdEntity;


/**
 * This Table contains all informations needed to draw a table of business mappings.
 * It contains the building block, which is connected by all business mappings on this
 * table, and all building blocks for column, rows and content. There is also a matrix
 * with all business mappings connected in the table.
 */
public class BusinessMappingTable implements Serializable, IdEntity {
  private static final long                                             serialVersionUID   = -2349869874624733688L;

  /** The building block to which it goes in the table. */
  private BuildingBlock                                                 firstBuildingBlock = null;
  /** A list of all building blocks in the column dimension. */
  private List<BuildingBlock>                                           bbForColumn        = Lists.newArrayList();
  /** A list of all building blocks in the row dimension. */
  private List<BuildingBlock>                                           bbForRow           = Lists.newArrayList();
  /** A list of all building blocks in the content dimension. */
  private List<BuildingBlock>                                           bbForContent       = Lists.newArrayList();
  /** A matrix, containing the connected business mappings. This matrix will return a list with the content for a specific row and column. */
  private HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock> tableData;

  /**
   * returns the building block to which it goes in the table.
   * @return building block
   */
  public BuildingBlock getFirstBuildingBlock() {
    return firstBuildingBlock;
  }

  /**
   * sets the building block to which it goes in the table.
   */
  public void setFirstBuildingBlock(BuildingBlock firstBuildingBlock) {
    this.firstBuildingBlock = firstBuildingBlock;
  }

  /**
   * returns a list of all building blocks in the column dimension.
   * @return list of building blocks
   */
  public List<BuildingBlock> getBbForColumn() {
    return bbForColumn;
  }

  /**
   * sets a list of all building blocks in the column dimension.
   */
  public void setBbForColumn(List<BuildingBlock> bbForColumn) {
    this.bbForColumn = bbForColumn;
  }

  /**
   * returns a list of all building blocks in the row dimension.
   * @return list of building blocks
   */
  public List<BuildingBlock> getBbForRow() {
    return bbForRow;
  }

  /**
   * sets a list of all building blocks in the row dimension.
   */
  public void setBbForRow(List<BuildingBlock> bbForRow) {
    this.bbForRow = bbForRow;
  }

  /**
   * returns a list of all building blocks in the content dimension.
   * @return list of building blocks
   */
  public List<BuildingBlock> getBbForContent() {
    return bbForContent;
  }

  /**
   * sets a list of all building blocks in the content dimension.
   */
  public void setBbForContent(List<BuildingBlock> bbForContent) {
    this.bbForContent = bbForContent;
  }

  /**
   * Returns a matrix, containing the connected business mappings.
   * This matrix will return a list with the content for a specific row and column.
   * @return matrix of building blocks
   */
  public HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock> getTableData() {
    return tableData;
  }

  /**
   * Sets a matrix, containing the connected business mappings.
   */
  public void setTableData(HashBucketMatrix<BuildingBlock, BuildingBlock, BuildingBlock> tableData) {
    this.tableData = tableData;
  }

  /**
   * {@inheritDoc}
   */
  public Integer getId() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void setId(Integer id) {
    // this table holds a matrix of business mappings, and has no id
  }

}
