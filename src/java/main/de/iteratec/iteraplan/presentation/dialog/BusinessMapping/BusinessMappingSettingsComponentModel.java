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

import java.util.List;

import org.springframework.validation.Errors;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;


/**
 * The component model for the settings part in business mappings.
 */
public class BusinessMappingSettingsComponentModel extends AbstractComponentModelBase<BusinessMappingSettingsComponentModel> {
  private static final long   serialVersionUID   = 7372837720915270472L;

  /** Type for the first building block. */
  private String              firstType          = Constants.BB_PRODUCT_PLURAL;
  /** Type for the columns. */
  private String              columnType         = Constants.BB_BUSINESSPROCESS_PLURAL;
  /** Type for the rows. */
  private String              rowType            = Constants.BB_BUSINESSUNIT_PLURAL;
  /** Type for the content. */
  private String              contentType        = Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL;

  /** A list of building blocks of the first type. */
  private List<BuildingBlock> bbListForSelection = Lists.newArrayList();
  /** Id of the building block, the user has selected.*/
  private Integer             selectedBbId;

  /**
   * @param componentMode
   */
  protected BusinessMappingSettingsComponentModel(ComponentMode componentMode) {
    super(componentMode);
  }

  public String getFirstType() {
    return firstType;
  }

  public void setFirstType(String firstType) {
    this.firstType = firstType;
  }

  public String getColumnType() {
    return columnType;
  }

  public void setColumnType(String columnType) {
    this.columnType = columnType;
  }

  public String getRowType() {
    return rowType;
  }

  public void setRowType(String rowType) {
    this.rowType = rowType;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public List<BuildingBlock> getBbListForSelection() {
    return bbListForSelection;
  }

  public void setBbListForSelection(List<BuildingBlock> bbListForSelection) {
    this.bbListForSelection = bbListForSelection;
  }

  public Integer getSelectedBbId() {
    return selectedBbId;
  }

  public void setSelectedBbId(Integer selectedBbId) {
    this.selectedBbId = selectedBbId;
  }

  /**
   * {@inheritDoc}
   */
  public void initializeFrom(BusinessMappingSettingsComponentModel source) {
    this.firstType = source.firstType;
    this.columnType = source.columnType;
    this.rowType = source.rowType;
    this.contentType = source.contentType;
    this.bbListForSelection = source.bbListForSelection;
    this.selectedBbId = source.selectedBbId;
  }

  /**
   * {@inheritDoc}
   */
  public void update() {
    // nothing to do jet
  }

  /**
   * {@inheritDoc}
   */
  public void configure(BusinessMappingSettingsComponentModel target) {
    target.firstType = this.firstType;
    target.columnType = this.columnType;
    target.rowType = this.rowType;
    target.contentType = this.contentType;
    target.bbListForSelection = this.bbListForSelection;
    target.selectedBbId = this.selectedBbId;
  }

  /**
   * {@inheritDoc}
   */
  public void validate(Errors errors) {
    // nothing to do jet
  }

}
