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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.Configuration;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Domain;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Parameter;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn.DataType;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultRow;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;


/**
 * Checks if there are {@link Project}s whose runtime starts at an earlier date than their
 * associated {@link InformationSystemRelease}s' runtime.
 */
public class CheckProjectsUnsynchronizedWithAssociatedInformationSystemReleases extends AbstractConsistencyCheck {

  public CheckProjectsUnsynchronizedWithAssociatedInformationSystemReleases() {
    super("check_it_projectsUnsynchronizedWithAssociatedInformationSystemReleases", Domain.IT);
  }

  public Result executeCheck(Map<String, Parameter> parameters) {

    if (!parameters.containsKey(DAYS_PARAMETER)) {
      throw new IllegalArgumentException("A consistency check parameter is not available.");
    }

    /* Get the object for the parameter named 'days'. */
    Parameter parameter = parameters.get(DAYS_PARAMETER);

    /* Get the value of the parameter. */
    int days = Integer.parseInt(parameter.getValue());

    List<Object[]> listObjects = getConsistencyCheckDAO().getUnsynchronizedProjectsWithInformationSystemReleases();

    for (Object[] objects : listObjects) {
      Project p = (Project) objects[0];
      Date projectEndDate = p.runtimeEndsAt();
      DateTime projectEndDateTime = new LocalDate(projectEndDate).toDateTimeAtStartOfDay();

      InformationSystemRelease release = (InformationSystemRelease) objects[1];
      Date releaseStartDate = release.runtimeStartsAt();
      DateTime releaseStartDateTime = new LocalDate(releaseStartDate).toDateTimeAtStartOfDay();

      /* when the startDate or endDate is not set, skip this ISR/Proj */
      if (releaseStartDate == null || projectEndDate == null) {
        continue;
      }

      int gapDays = Days.daysBetween(releaseStartDateTime, projectEndDateTime).getDays();

      /**
       * if the day-param is positive, we are looking for ISR that start at least x days before the
       * ProjEnd
       */
      if (days >= 0) {
        if (gapDays >= days) {
          addToResult(p, release);
        }
      }
      /**
       * if the day-param is negative, we are looking for ISR that start at least x days after the
       * ProjEnd
       */
      else {
        if (gapDays <= days) {
          addToResult(p, release);
        }
      }
    }

    return getResult();
  }

  /**
   * Adds the Project and ISR to the result
   * 
   * @param p
   *          the Project
   * @param release
   *          the ISR
   */
  private void addToResult(Project p, InformationSystemRelease release) {
    List<Object> row = new ArrayList<Object>();
    row.add(p);
    row.add(release);
    row.add(p.runtimeEndsAt());
    row.add(release.runtimeStartsAt());

    ResultRow resultRow = new ResultRow();
    resultRow.setElements(row);

    getResult().getRows().add(resultRow);

  }

  public Configuration initializeConfiguration() {

    Map<String, Parameter> parameters = new HashMap<String, Parameter>();
    parameters.put(DAYS_PARAMETER, this.getDaysParameter());

    /* Add environment and parameter to the configuration. */
    setConfiguration(new Configuration());
    getConfiguration().setName(getName());
    getConfiguration().setDomain(getDomain());
    getConfiguration().setParameters(parameters);

    return getConfiguration();
  }

  @Override
  protected List<ResultColumn> configureColumns() {

    List<ResultColumn> columns = new ArrayList<ResultColumn>();

    columns.add(new ResultColumn(Constants.BB_PROJECT, DataType.OBJECT, "nonHierarchicalName", Boolean.TRUE, Boolean.FALSE));
    columns.add(new ResultColumn(Constants.BB_INFORMATIONSYSTEMRELEASE, DataType.OBJECT, "nonHierarchicalName", Boolean.TRUE, Boolean.FALSE));
    columns.add(new ResultColumn("check.columns.project.enddate", DataType.DATE));
    columns.add(new ResultColumn("check.column.informationsystemrelease.startdate", DataType.DATE));

    return columns;
  }

}