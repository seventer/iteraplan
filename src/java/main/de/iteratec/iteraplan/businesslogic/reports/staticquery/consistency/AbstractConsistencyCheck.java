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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.Configuration;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Domain;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Parameter;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Parameter.Validator;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ParameterMultipleOption;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ParameterSingleOption;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn.DataType;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultRow;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.StaticQuery;
import de.iteratec.iteraplan.businesslogic.service.ConsistencyCheckService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.persistence.dao.ConsistencyCheckDAO;


/**
 * Abstract base class for consistency checks.
 */
public abstract class AbstractConsistencyCheck implements StaticQuery {

  /**
   * parameters, used by some concrete consistency checks
   */
  public static final String      DAYS_PARAMETER          = "days";
  public static final String      TYPE_PARAMETER          = "type";
  public static final String      RECENT_UPDATE_PARAMETER = "updated";

  /**
   * The name of the consistency check.
   * <p>
   * This name is both the key to the internationalized name of the check and the key to the name of
   * the Spring Bean configured for the consistency check.
   */
  private final String            name;

  /**
   * The domain the consistency check belongs to.
   */
  private final Domain            domain;

  /**
   * The configuration of the consistency check.
   */
  private Configuration           configuration;

  /**
   * The result of the consistency check.
   */
  private Result                  result;

  /**
   * The reference to the concrete implementation of the DAO interface for consistency checks.
   * Injected by Spring.
   */
  private ConsistencyCheckDAO     consistencyCheckDAO;

  private ConsistencyCheckService consistencyCheckService;

  /**
   * Constructor.
   *
   * @param name
   *          The name of this consistency check.
   * @param domain
   *          The domain this consistency check belongs to.
   */
  public AbstractConsistencyCheck(String name, Domain domain) {
    this.name = name;
    this.domain = domain;
  }

  /**
   * Returns the result of a consistency check. The implementation of this method performs the
   * following operations:
   * <ul>
   * <li>Validates the parameters in the given map, if a validator is defined.</li>
   * <li>Calls the {@link #configureResult()}-method.</li>
   * <li>Calls the {@link #executeCheck(Map)}-method to execute the concrete consistency check.</li>
   * <li>Calls the {@link #formatDates()}-method on results of type {@link DataType#DATE}</li>
   * <li>Sorts the rows of the result, if a comparator is defined.</li>
   * </ul>
   */
  public Result execute(Map<String, Parameter> parameters) {

    // Validate the parameters, if applicable.
    for (Parameter parameter : parameters.values()) {
      if (parameter.getValidator() != null) {
        parameter.getValidator().validate(parameter.getValue());
      }
    }

    // Configure the result object.
    configureResult();
    result.setParameters(parameters);

    // Delegate the work to the concrete consistency check.
    result = executeCheck(parameters);

    // Format the result, if applicable.
    formatDates();

    // Sort the resulting rows, if applicable.
    if (result != null && result.getRowComparator() != null) {
      Collections.sort(result.getRows(), result.getRowComparator());
    }

    return result;
  }

  /**
   * Template method which performs the actual consistency check. Must be overriden by subclasses.
   *
   * @param parameters
   *          The parameters of the consistency check.
   * @return The result of the consistency check.
   */
  public abstract Result executeCheck(Map<String, Parameter> parameters);

  /**
   * This method is implemented in the abstract base class to avoid having to initialize the
   * configuration data more than one time. For consistency checks that define parameters though,
   * the configuration has to be reinitialized since a consistency check is a Spring Bean and thus a
   * Singleton. Otherwise each user would be presented with the same parameter values some user
   * entered before.
   */
  public final Configuration getConfiguration() {

    if (configuration == null) {
      throw new IllegalStateException("The configuration of this consistency check has not been initialized.");
    }

    return configuration;
  }

  /**
   * The {@link StaticQuery#initializeConfiguration()} method is implemented in this abstract class
   * especially for consistency checks, that don't have a state, i.e. they don't define parameters
   * and parameter values. If a consistency check depends on some parameters, this method may be
   * overriden in the implementation to provide that information.
   * <p>
   * Additionally, subclasses may define a validator for a particular parameter by overriding this
   * method.
   */
  public Configuration initializeConfiguration() {

    configuration = new Configuration();
    configuration.setName(name);
    configuration.setDomain(domain);

    return configuration;
  }

  public void setConsistencyCheckDAO(ConsistencyCheckDAO consistencyCheckDAO) {
    this.consistencyCheckDAO = consistencyCheckDAO;
  }

  public void setConsistencyCheckService(ConsistencyCheckService consistencyCheckService) {
    this.consistencyCheckService = consistencyCheckService;
  }

  /**
   * Configures the meta information of the column header and data of the result table of a
   * consistency check.
   *
   * @return A list of {@link ResultColumn}s.
   */
  protected abstract List<ResultColumn> configureColumns();

  /**
   * Method to allow subclasses to additionally configure the result of a consistency check. If the
   * rows of the result of some consistency check shall be sorted in a particular order, this method
   * should be overridden to define a comparator.
   */
  protected void configureResult() {

    result = new Result();
    result.setName(name);
    result.setDomain(domain);
    result.setColumns(configureColumns());
  }

  /**
   * Formats the values with data type {@link DataType#DATE} contained in the {@link Result} object
   * of this consistency check according to the locale defined in the user context.
   */
  protected final void formatDates() {
    if (result != null) {
      for (int i = 0; i < result.getColumns().size(); i++) {
        ResultColumn column = result.getColumns().get(i);
        if (column.getType() == DataType.DATE) {
          for (ResultRow row : result.getRows()) {

            // Get the elements corresponding to the column with the data type DATE.
            Object value = row.getElements().get(i);

            Date date;
            if (value != null) {
              if (value instanceof String) {
                date = DateUtils.parseAsDate((String) value, UserContext.getCurrentLocale());
              }
              else if (value instanceof Date) {
                date = (Date) value;
              }
              else {
                date = DateUtils.parseAsDate(value.toString(), UserContext.getCurrentLocale());
              }

              row.getElements().set(i, DateUtils.formatAsString(date, UserContext.getCurrentLocale()));
            }
          }
        }
      }
    }
  }

  protected void addRow2Result(List<Object> row) {

    ResultRow resultRow = new ResultRow();
    resultRow.setElements(row);

    List<ResultRow> allRows = result.getRows();

    boolean duplicate = false;

    // check for duplicates
    for (ResultRow existingRow : allRows) {
      if (existingRow.hasSameContentAs(resultRow)) {
        duplicate = true;
        break;
      }
    }

    // if not duplicate
    if (!duplicate) {
      allRows.add(resultRow);
    }
  }

  protected List<ResultColumn> doConfigureColumns(String columnName) {

    List<ResultColumn> columns = new ArrayList<ResultColumn>();
    columns.add(new ResultColumn(columnName, DataType.OBJECT, "identityString", Boolean.TRUE, Boolean.FALSE));
    columns.add(new ResultColumn(Constants.TIMESPAN_PRODUCTIVE_FROM, DataType.DATE));
    columns.add(new ResultColumn(Constants.TIMESPAN_PRODUCTIVE_TO, DataType.DATE));
    columns.add(new ResultColumn(Constants.ATTRIBUTE_TYPEOFSTATUS, DataType.OBJECT, "value", Boolean.FALSE, Boolean.TRUE));

    return columns;
  }

  /**
   * Adds the building block type as parameter to map.
   *
   * @param parameters
   *          Map of all parameters
   * @param options
   *          The options for the building block parameter
   */
  protected void addBuildingBlockTypeParameter(Map<String, Parameter> parameters, List<Object> options) {

    ParameterMultipleOption parameter = new ParameterMultipleOption();
    parameter.setOptions(options);
    parameter.setAccessor("value");
    parameter.setLocalized(Boolean.TRUE);
    parameter.setFirstOption("check.buildingblocktype.all");
    parameter.setValue("check.buildingblocktype.all");

    parameters.put(TYPE_PARAMETER, parameter);
  }

  /**
   * Returns the parameter/flag whether we are looking for recently actualised elements (true) or
   * 'older' elements (false)
   *
   * @return See method description.
   */
  protected Parameter getRecentlyActualisedParameter() {

    ParameterMultipleOption parameter = new ParameterMultipleOption();
    parameter.setOptions(Arrays.asList(new Object[] { new ParameterObjectForRecentUpdate(Boolean.FALSE) }));
    parameter.setAccessor("value");
    parameter.setLocalized(Boolean.TRUE);
    parameter.setFirstOption("check.recentlyUpdated");
    parameter.setValue("check.recentlyUpdated");

    return parameter;
  }

  /**
   * Retrieves the parameter for days.
   */
  protected Parameter getDaysParameter() {

    ParameterSingleOption param = new ParameterSingleOption();
    param.setValue("1");
    param.setValidator(new DaysValidator());
    return param;
  }

  /**
   * Configures the consistency check for a building block.
   *
   * @param additionalParameters
   *          Any parameters beyond the building block type to pass dynamically to config. Must not
   *          be null.
   * @param excludeAssociationTypes If set to true, building block types which serve as associations are suppressed.
   * @return ready configuration
   */
  protected Configuration doInitializeConfigurationBuildingBlocks(Map<String, Parameter> additionalParameters, boolean excludeAssociationTypes) {

    /**
     * Create a parameter with multiple entries. Get all building block types that may be associated
     * with attributes.
     */
    List<Object> options = new ArrayList<Object>();
    options.addAll(consistencyCheckService.getTypeOfBuildingBlocksEligibleForAttributes());
    if (excludeAssociationTypes) {
      options.remove(TypeOfBuildingBlock.TCR2IEASSOCIATION);
      options.remove(TypeOfBuildingBlock.ISR2BOASSOCIATION);
      options.remove(TypeOfBuildingBlock.BUSINESSMAPPING);
    }
    /**
     * copy all additional parameters
     */
    Map<String, Parameter> parameters = new HashMap<String, Parameter>(additionalParameters);
    addBuildingBlockTypeParameter(parameters, options);

    /**
     * Add environment and parameter to the configuration.
     */
    configuration = new Configuration();
    configuration.setName(name);
    configuration.setDomain(domain);
    configuration.setParameters(parameters);

    return configuration;
  }

  protected Result getResult() {
    return result;
  }

  protected ConsistencyCheckDAO getConsistencyCheckDAO() {
    return consistencyCheckDAO;
  }

  protected void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  protected String getName() {
    return name;
  }

  protected Domain getDomain() {
    return domain;
  }

  private static final class DaysValidator extends Validator {

    /** Serialization version. */
    private static final long serialVersionUID = 5405020091664958605L;

    @Override
    public void validate(String value) {
      int days = 0;
      
      try {
        days = Integer.parseInt(value);
      } catch (NumberFormatException ex) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.INCORRECT_INTEGER_FORMAT, ex);
      }
      
      // we request a positive number
      if (days < 0) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.INCORRECT_INTEGER_FORMAT);
      }
      
    }
  }

}