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
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.Domain;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Parameter;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn.DataType;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultRow;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.persistence.dao.TechnicalComponentDAO;


/**
 * This consistency check checks if there is more than one release of a catalog item that have their
 * status set to CURRENT.
 */
public class CheckMoreThanOneReleaseOfTcWithStatusCurrent extends AbstractConsistencyCheck {

  private TechnicalComponentDAO technicalComponentDAO;

  public CheckMoreThanOneReleaseOfTcWithStatusCurrent() {
    super("check_technical_tcrMoreThanOneReleaseWithStatusCurrent", Domain.TECHNICAL);
  }

  @Override
  public Result executeCheck(Map<String, Parameter> parameters) {

    for (TechnicalComponent item : technicalComponentDAO.loadElementList(null)) {

      List<TechnicalComponentRelease> releases = getConsistencyCheckDAO().getReleasesWithStatusCurrentForTcID(item.getId());

      // If there's more than one release with the status CURRENT.
      if (releases.size() > 1) {

        for (TechnicalComponentRelease release : releases) {
          List<Object> row = new ArrayList<Object>();
          row.add(release);

          ResultRow resultRow = new ResultRow();
          resultRow.setElements(row);

          getResult().getRows().add(resultRow);
        }
      }
    }

    return getResult();
  }

  @Override
  protected List<ResultColumn> configureColumns() {

    List<ResultColumn> columns = new ArrayList<ResultColumn>();

    columns.add(new ResultColumn(Constants.BB_TECHNICALCOMPONENTRELEASE, DataType.OBJECT, "identityString", Boolean.TRUE, Boolean.FALSE));

    return columns;
  }

  public void setTechnicalComponentDAO(TechnicalComponentDAO dao) {
    this.technicalComponentDAO = dao;
  }

}