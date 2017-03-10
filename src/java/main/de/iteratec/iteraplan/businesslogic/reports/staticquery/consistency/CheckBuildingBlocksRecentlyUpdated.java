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
package de.iteratec.iteraplan.businesslogic.reports.staticquery.consistency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.Configuration;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Domain;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Parameter;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ParameterMultipleOption;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn.DataType;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class CheckBuildingBlocksRecentlyUpdated extends BuildingBlockConsistencyCheck {

  public CheckBuildingBlocksRecentlyUpdated() {
    super("check_general_bbRecentlyUpdated", Domain.GENERAL);
  }

  /**
   * set up header labels of result table
   */
  @Override
  protected List<ResultColumn> configureColumns() {

    List<ResultColumn> columns = new ArrayList<ResultColumn>();

    columns.add(new ResultColumn("check.column.buildingblock.type", DataType.OBJECT, "name", Boolean.FALSE, Boolean.TRUE));
    columns.add(new ResultColumn("check.column.buildingblock.name", DataType.OBJECT, "identityString", Boolean.TRUE, Boolean.FALSE));
    columns.add(new ResultColumn("global.lastModificationTime", DataType.DATE));

    return columns;
  }

  @SuppressWarnings("boxing")
  @Override
  public Result executeCheck(Map<String, Parameter> parameters) {

    TypeOfBuildingBlock type = getTypeOfBbFromParameters(parameters);

    Parameter dayParameter = getConfiguration().getParameters().get(DAYS_PARAMETER);
    Integer days = Integer.valueOf(dayParameter.getValue());

    ParameterMultipleOption recentUpdateParameter = (ParameterMultipleOption) parameters.get(RECENT_UPDATE_PARAMETER);
    String updateValue = recentUpdateParameter.getValue();
    Boolean recentUpdates = ParameterObjectForRecentUpdate.getBooleanFromValue(updateValue);

    List<BuildingBlock> list = getConsistencyCheckDAO().getBuildingBlocksRecentlyUpdated(type, days, recentUpdates);

    for (BuildingBlock buildingBlock : list) {
      List<Object> row = new ArrayList<Object>();

      // add building block, its name, and its last modification date to each row
      row.add(buildingBlock.getBuildingBlockType());
      row.add(buildingBlock);
      row.add(buildingBlock.getLastModificationTime());

      addRow2Result(row);
    }
    return getResult();
  }

  /**
   * Apply specific config for this consistency check. Adds the additional parameter 'days'
   * dynamically.
   */
  @Override
  public Configuration initializeConfiguration() {

    Map<String, Parameter> additionalParameters = new HashMap<String, Parameter>();
    additionalParameters.put(DAYS_PARAMETER, this.getDaysParameter());
    additionalParameters.put(RECENT_UPDATE_PARAMETER, this.getRecentlyActualisedParameter());

    return doInitializeConfigurationBuildingBlocks(additionalParameters, false);
  }
}
