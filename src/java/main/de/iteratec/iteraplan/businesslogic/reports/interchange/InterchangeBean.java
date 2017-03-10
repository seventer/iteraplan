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
package de.iteratec.iteraplan.businesslogic.reports.interchange;

import net.jawr.web.util.StringUtils;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class InterchangeBean {

  public static final String     IS_LANDSCAPE_CONTENT = "graphicalExport.landscape.is.content";
  public static final String     IS_LANDSCAPE_X_AXIS  = "graphicalExport.landscape.is.xaxis";
  public static final String     IS_LANDSCAPE_Y_AXIS  = "graphicalExport.landscape.is.yaxis";
  public static final String     TC_LANDSCAPE_CONTENT = "graphicalExport.landscape.tc.content";
  public static final String     TC_LANDSCAPE_X_AXIS  = "graphicalExport.landscape.tc.xaxis";
  public static final String     TC_LANDSCAPE_Y_AXIS  = "graphicalExport.landscape.tc.yaxis";

  private Boolean                isSealSelected       = Boolean.FALSE;
  private Boolean                isStatusSelected     = Boolean.FALSE;
  private String                 selectedStatusValue  = StringUtils.EMPTY;
  private String                 selectedSealValue    = StringUtils.EMPTY;
  private Integer[]              selectedIds;
  private TypeOfBuildingBlock    typeOfBuildingBlock;
  private InterchangeDestination interchangeDestination;

  public Integer[] getSelectedIds() {
    return selectedIds.clone();
  }

  public void setSelectedIds(Integer[] ids) {
    this.selectedIds = ids.clone();
  }

  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return typeOfBuildingBlock;
  }

  public void setTypeOfBuildingBlock(TypeOfBuildingBlock typeOfBuildingBlock) {
    this.typeOfBuildingBlock = typeOfBuildingBlock;
  }

  public InterchangeDestination getInterchangeDestination() {
    return interchangeDestination;
  }

  public void setInterchangeDestination(InterchangeDestination interchangeDestination) {
    this.interchangeDestination = interchangeDestination;
  }

  public Boolean getIsSealSelected() {
    return isSealSelected;
  }

  public void setIsSealSelected(Boolean isSealSelected) {
    this.isSealSelected = isSealSelected;
  }

  public Boolean getIsStatusSelected() {
    return isStatusSelected;
  }

  public void setIsStatusSelected(Boolean isStatusSelected) {
    this.isStatusSelected = isStatusSelected;
  }

  public String getSelectedStatusValue() {
    return selectedStatusValue;
  }

  public void setSelectedStatusValue(String selectedStatusValue) {
    this.selectedStatusValue = selectedStatusValue;
  }

  public String getSelectedSealValue() {
    return selectedSealValue;
  }

  public void setSelectedSealValue(String selectedSealValue) {
    this.selectedSealValue = selectedSealValue;
  }

}