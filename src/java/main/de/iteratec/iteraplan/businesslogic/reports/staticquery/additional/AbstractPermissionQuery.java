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
package de.iteratec.iteraplan.businesslogic.reports.staticquery.additional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.Configuration;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Parameter;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ParameterMultipleOption;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn.DataType;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultRow;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.StaticQuery;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.persistence.dao.PermissionQueryDAO;


/**
 * Abstract base class for permission queries.
 */
public abstract class AbstractPermissionQuery implements StaticQuery {

  /**
   * The name of the permission query. 
   * <p>
   * This name is both the key to the internationalized name of the 
   * query and the key to the name of the Spring Bean configured for 
   * the query.
   */
  private final String       name;

  /**
   * The configuration of the permission query.
   */
  private Configuration      configuration;

  /**
   * The result of the permission query.
   */
  private Result             result;

  /**
   * The reference to the concrete implementation of the DAO interface 
   * for permission queries. Injected by Spring.
   */
  private PermissionQueryDAO permissionQueryDAO;

  /**
   * Constructor.
   * 
   * @param name
   *    The name of the permission query.
   */
  public AbstractPermissionQuery(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * @see de.iteratec.iteraplan.businesslogic.reports.staticquery.StaticQuery#execute(java.util.Map)
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
    result = executeQuery(parameters);

    // Sort the resulting rows, if applicable.
    if (result.getRowComparator() != null) {
      Collections.sort(result.getRows(), result.getRowComparator());
    }

    return result;
  }

  /**
   * This method is implemented in the abstract base class to avoid having to 
   * initialize the configuration data more than one time. For permission queries 
   * that define parameters though, the configuration has to be reinitialized 
   * since a permission query is a Spring Bean and thus a Singleton. Otherwise 
   * each user would be presented with the same parameter values some user entered 
   * before.
   */
  public final Configuration getConfiguration() {

    if (configuration == null) {
      throw new IllegalStateException("The configuration of this permission query has not been initialized.");
    }

    return configuration;
  }

  /**
   * The {@link StaticQuery#initializeConfiguration()} method is implemented in
   * this abstract class especially for permission queries, that don't have a state,
   * i.e. they don't define parameters and parameter values. If a permission query
   * depends on some parameters, this method may be overriden in the implementation
   * to provide that information.
   * <p>
   * Additionally, subclasses may define a validator for a particular parameter by 
   * overriding this method.
   */
  public Configuration initializeConfiguration() {

    configuration = new Configuration();
    configuration.setName(name);

    return configuration;
  }

  public void setPermissionQueryDAO(PermissionQueryDAO permissionQueryDAO) {
    this.permissionQueryDAO = permissionQueryDAO;
  }

  /**
   * Method to allow subclasses to additionally configure the result of a 
   * permission query. Currently, this is useful for defining a comparator.
   */
  protected void configureResult() {

    result = new Result();
    result.setName(name);
    result.setColumns(configureColumns());
  }

  protected abstract List<Role> getListOfRoles(String value);

  protected abstract String getParameterName();

  /**
   * Method which performs the actual query. Must be overriden by subclasses.
   * Subclasses with add
   * @param parameters
   *    The parameters of the consistency check.
   *    
   * @return
   *    The result of the consistency check.
   */
  public Result executeQuery(Map<String, Parameter> parameters) {
    final String parameterName = getParameterName();
    if (!parameters.containsKey(parameterName)) {
      throw new IllegalArgumentException("A permission query parameter is not available.");
    }

    // Get the object for the parameter with given name.
    Parameter parameter = parameters.get(parameterName);
    List<Role> roles = getListOfRoles(parameter.getValue());

    Set<Role> set = new HashSet<Role>(roles);
    for (Role role : roles) {
      set.addAll(role.getElementOfRolesAggregated());
    }
    List<Role> list = new ArrayList<Role>(set);
    Collections.sort(list);
    for (Role role : list) {
      List<Object> row = new ArrayList<Object>();
      row.add(role);
      row.add(role.getDescription());

      ResultRow resultRow = new ResultRow();
      resultRow.setElements(row);

      result.getRows().add(resultRow);
    }
    return result;
  }

  /**
   * Configures the meta information of the column header and data of the result 
   * table of a permission query.
   * 
   * @return
   *    A list of {@link ResultColumn}s.
   */
  protected List<ResultColumn> configureColumns() {

    List<ResultColumn> columns = new ArrayList<ResultColumn>();

    columns.add(new ResultColumn("global.name", DataType.OBJECT, "roleName", Boolean.TRUE, Boolean.FALSE));
    columns.add(new ResultColumn("global.description"));

    return columns;
  }

  protected Configuration addOptionsToResult(List<Object> options) {
    ParameterMultipleOption parameter = new ParameterMultipleOption();
    parameter.setOptions(options);
    parameter.setAccessor("value");
    parameter.setLocalized(Boolean.TRUE);

    Map<String, Parameter> parameters = new HashMap<String, Parameter>();
    parameters.put(getParameterName(), parameter);

    // Add parameter to the configuration.
    configuration.setParameters(parameters);
    return configuration;
  }

  protected PermissionQueryDAO getPermissionQueryDAO() {
    return permissionQueryDAO;
  }
}