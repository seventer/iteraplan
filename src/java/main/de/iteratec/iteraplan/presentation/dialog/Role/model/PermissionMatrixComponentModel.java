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
package de.iteratec.iteraplan.presentation.dialog.Role.model;

import java.util.Collections;
import java.util.List;

import org.springframework.validation.Errors;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;


/**
 * Represents a matrix of checkBoxes, where permissions can be set.
 */
public abstract class PermissionMatrixComponentModel extends AbstractComponentModelBase<Role> {

  private static final long                       serialVersionUID = 8204154246979181458L;

  /** The header key for the matrix-table */
  private final String                            tableHeaderKey;

  /** The subheader keys for each column. For exampel: read, update, create, delete */
  private final List<String>                      columnHeaderKeys;

  /** The row components */
  private final List<PermissionRowComponentModel> rows             = Lists.newArrayList();

  /** if this is true, all aggregated permissions will be shown. The Table is also not editable. */
  private final boolean                           aggregated;

  /**
   * @param componentMode The {@link ComponentMode} of this component model.
   * @param htmlId The HTML ID of this component model.
   * @param tableHeaderKey The header key for the matrix-table
   * @param columnHeaderKeys The subheader keys for each column. For exampel: read, update, create, delete
   * @param aggregated if this is true, all aggregated permissions will be shown. The Table is also not editable.
   */
  public PermissionMatrixComponentModel(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
      boolean aggregated) {
    super(componentMode, htmlId);
    this.tableHeaderKey = tableHeaderKey;
    this.columnHeaderKeys = Lists.newArrayList(columnHeaderKeys);
    this.aggregated = aggregated;
  }

  /** {@inheritDoc} **/
  public void initializeFrom(Role source) {
    List<BuildingBlockType> bbts = Lists.newArrayList(getAvailableBuildingBlockTypes());
    Collections.sort(bbts);

    for (BuildingBlockType bbt : bbts) {
      PermissionRowComponentModel cm = new PermissionRowComponentModel(getComponentMode(), bbt.getTypeOfBuildingBlock().getValue());

      PermissionFunctional permissionFunctionalForBbt = getPermissionFunctionalForBbt(bbt);
      if (permissionFunctionalForBbt != null) {
        FuncPermissionCellComponentModel fpcm = new FuncPermissionCellComponentModel(getComponentMode(), isAggregated());
        fpcm.setPermissionFunctional(permissionFunctionalForBbt);
        cm.addCellComponentModel(fpcm);
      }

      BbtPermissionCellComponentModel bbtUpdateCm = new BbtPermissionCellComponentModel(getComponentMode(), isAggregated());
      bbtUpdateCm.setBuildingBlockType(bbt);
      bbtUpdateCm.setEditPermissionType(EditPermissionType.UPDATE);
      cm.addCellComponentModel(bbtUpdateCm);

      BbtPermissionCellComponentModel bbtCreateCm = new BbtPermissionCellComponentModel(getComponentMode(), isAggregated());
      bbtCreateCm.setBuildingBlockType(bbt);
      bbtCreateCm.setEditPermissionType(EditPermissionType.CREATE);
      cm.addCellComponentModel(bbtCreateCm);

      BbtPermissionCellComponentModel bbtDeleteCm = new BbtPermissionCellComponentModel(getComponentMode(), isAggregated());
      bbtDeleteCm.setBuildingBlockType(bbt);
      bbtDeleteCm.setEditPermissionType(EditPermissionType.DELETE);
      cm.addCellComponentModel(bbtDeleteCm);

      if (!bbt.getTypeOfBuildingBlock().equals(TypeOfBuildingBlock.BUSINESSMAPPING)) {
        cm.setFirstRequiredForOthers(true);
      }

      cm.initializeFrom(source);
      addRow(cm);
    }
  }

  /**
   * Searches for a Functional Permission, which is the read permission for the given building block type.
   * @param bbt building block type for searching a corresponding functional permission
   * @return functional permission if found, else null
   */
  private PermissionFunctional getPermissionFunctionalForBbt(BuildingBlockType bbt) {
    Class<? extends BuildingBlock> cls = bbt.getTypeOfBuildingBlock().getAssociatedClass();
    for (PermissionFunctional pf : getAvailablePermissionFunctional()) {
      if (cls.equals(pf.getTypeOfFunctionalPermission().getClassForPermission())) {
        return pf;
      }
    }
    return null;
  }

  /** {@inheritDoc} **/
  public void update() {
    for (PermissionRowComponentModel row : rows) {
      row.update();
    }
  }

  /** {@inheritDoc} **/
  public void configure(Role target) {
    for (PermissionRowComponentModel row : rows) {
      row.configure(target);
    }
  }

  /** {@inheritDoc} **/
  public void validate(Errors errors) {
    for (PermissionRowComponentModel row : rows) {
      row.validate(errors);
    }
  }

  /**
   * Returns a list of {@link BuildingBlockType}. This list will decide, which permissions are shown. For every building block
   * type, all {@link EditPermissionType}s are rendered, plus one corresponding {@link PermissionFunctional} if
   * one could be found in {@link #getAvailablePermissionFunctional()}.
   * @return a list of {@link BuildingBlockType}s.
   */
  public abstract List<BuildingBlockType> getAvailableBuildingBlockTypes();

  /**
   * Returns a list, containing {@link PermissionFunctional}s. These permissions could be rendered, if a corresponding
   * {@link BuildingBlockType} exists in {@link #getAvailableBuildingBlockTypes()}.
   * @return a list of {@link PermissionFunctional}s
   */
  public abstract List<PermissionFunctional> getAvailablePermissionFunctional();

  public List<PermissionRowComponentModel> getRows() {
    return rows;
  }

  public void addRow(PermissionRowComponentModel cm) {
    rows.add(cm);
  }

  public String getTableHeaderKey() {
    return tableHeaderKey;
  }

  public List<String> getColumnHeaderKeys() {
    return columnHeaderKeys;
  }

  public boolean isAggregated() {
    return aggregated;
  }

}
