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
package de.iteratec.iteraplan.presentation.dialog;

import java.io.Serializable;


/**
 *  Parameters for table actions like sorting, add column, (re)move column 
 */
public class TableActionParams implements Serializable {

  private static final long serialVersionUID = -6255933432199823893L;

  /** Trigger column sort on column (definition) with thid id. */
  private Integer           colSortIndex;

  /** Add column (definition) with this id to visible columns. */
  private Integer           colToAdd;

  /** Move column with this id. */
  private Integer           colMoveIndex;

  /** Direction to move the column. */
  private String            colMoveDirection;

  /** Remove column (definition) with this id from visible columns */
  private Integer           colRemoveIndex;

  /** Show tree view if true. Otherwise show normal list view. */
  private Boolean           showTreeView;

  /** Reset sort order to default if true. */
  private Boolean           sortByPosition;

  /** In tree view, load all children of this partial loaded node. */
  private Integer           loadAllFrom;

  public TableActionParams(Integer colSortIndex, Integer colToAdd, Integer colMoveIndex, String colMoveDirection, Integer colRemoveIndex,
      Boolean showTreeView, Boolean sortByPosition, Integer loadAllFrom) {
    this.colSortIndex = colSortIndex;
    this.colToAdd = colToAdd;
    this.colMoveIndex = colMoveIndex;
    this.colMoveDirection = colMoveDirection;
    this.colRemoveIndex = colRemoveIndex;
    this.showTreeView = showTreeView;
    this.sortByPosition = sortByPosition;
    this.loadAllFrom = loadAllFrom;
  }

  public Integer getColSortIndex() {
    return colSortIndex;
  }

  public void setColSortIndex(Integer colSortIndex) {
    this.colSortIndex = colSortIndex;
  }

  public Integer getColToAdd() {
    return colToAdd;
  }

  public void setColToAdd(Integer colToAdd) {
    this.colToAdd = colToAdd;
  }

  public Integer getColMoveIndex() {
    return colMoveIndex;
  }

  public void setColMoveIndex(Integer colMoveIndex) {
    this.colMoveIndex = colMoveIndex;
  }

  public String getColMoveDirection() {
    return colMoveDirection;
  }

  public void setColMoveDirection(String colMoveDirection) {
    this.colMoveDirection = colMoveDirection;
  }

  public Integer getColRemoveIndex() {
    return colRemoveIndex;
  }

  public void setColRemoveIndex(Integer colRemoveIndex) {
    this.colRemoveIndex = colRemoveIndex;
  }

  public Boolean getShowTreeView() {
    return showTreeView;
  }

  public void setShowTreeView(Boolean showTreeView) {
    this.showTreeView = showTreeView;
  }

  public Boolean getSortByPosition() {
    return sortByPosition;
  }

  public void setSortByPosition(Boolean sortByPosition) {
    this.sortByPosition = sortByPosition;
  }

  public Integer getLoadAllFrom() {
    return loadAllFrom;
  }

  public void setLoadAllFrom(Integer loadAllFrom) {
    this.loadAllFrom = loadAllFrom;
  }
}