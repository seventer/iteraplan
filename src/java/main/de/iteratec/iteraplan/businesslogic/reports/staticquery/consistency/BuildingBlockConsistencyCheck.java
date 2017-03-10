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

import java.util.Collections;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.Configuration;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Domain;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Parameter;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ParameterMultipleOption;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result.RowComparator;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultRow;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Applies specific set up steps for building blocks. Overrides the two methods configureResult()
 * and compare(ResultRow row1, ResultRow row2).
 */
public abstract class BuildingBlockConsistencyCheck extends AbstractConsistencyCheck {

  /**
   * Delegate construction to thus of superclass.
   * 
   * @param name
   *          The name of the consistency check
   * @param domain
   *          Its domain, one of the three categories (business, it, technical) it fits best into
   */
  public BuildingBlockConsistencyCheck(String name, Domain domain) {
    super(name, domain);
  }

  /**
   * Apply config for building blocks.
   */
  @Override
  public Configuration initializeConfiguration() {
    Map<String, Parameter> parameters = Collections.emptyMap();
    return doInitializeConfigurationBuildingBlocks(parameters, false);
  }

  /**
   * Apply config for building blocks, among others for sorting different building block types in
   * the result list.
   */
  @Override
  protected void configureResult() {
    super.configureResult();

    getResult().setRowComparator(new BuildingBlockRowComparator());
  }

  /**
   * Retrieves the type of building block from the parameters
   * 
   * @param parameters
   *          Map of all parameters
   * @return the type of building block
   */
  protected TypeOfBuildingBlock getTypeOfBbFromParameters(Map<String, Parameter> parameters) {

    if (!parameters.containsKey(TYPE_PARAMETER)) {
      throw new IllegalArgumentException("A consistency check parameter is not available.");
    }

    /***
     * Get the object for the parameter named 'type'.
     */
    Parameter parameter = parameters.get(TYPE_PARAMETER);

    /***
     * Get the value of the parameter and the first option in the list.
     */
    String value = parameter.getValue();
    String firstOption = ((ParameterMultipleOption) parameter).getFirstOption();

    TypeOfBuildingBlock type = null;

    /***
     * Assuming the user selected a particular building block type.
     */
    if (!value.equalsIgnoreCase(firstOption)) {
      type = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(value);
    }

    return type;
  }

  private static final class BuildingBlockRowComparator extends RowComparator {

    /** Serialization version. */
    private static final long serialVersionUID = 2126118750208074757L;

    @Override
    public int compare(ResultRow row1, ResultRow row2) {
      BuildingBlockType type1 = (BuildingBlockType) row1.getElements().get(0);
      BuildingBlockType type2 = (BuildingBlockType) row2.getElements().get(0);
      return type1.compareTo(type2);
    }
  }
}
