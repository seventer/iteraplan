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
package de.iteratec.iteraplan.businesslogic.exchange.msproject;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import de.iteratec.iteraplan.businesslogic.exchange.msproject.MsProjectExporterBase.ExportType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * Factory class for returning the needed exporter for response generator
 * 
 * @author rrs
 */
public final class MsProjectExporterFactory {

  /** empty private constructor */
  private MsProjectExporterFactory() {
    // hide constructor
  }

  /**
   * @param results
   *          query results
   * @param request
   *          request for url resolution
   * @param type
   *          building block type
   * @param exportType
   *          specifies the type of export, e.g. with subordinated building blocks or without
   * @return the MS project file
   */

  public static MsProjectExport getExport(List<? extends BuildingBlock> results, HttpServletRequest request, Type<?> type, ExportType exportType) {

    if (type instanceof InformationSystemReleaseTypeQu) {
      return new MsProjectInformationSystemExport(results, request, exportType);
    }
    else if (type instanceof TechnicalComponentReleaseTypeQu) {
      return new MsProjectTechnicalComponentExport(results, request, exportType);
    }
    else if (type instanceof ProjectQueryType) {
      return new MsProjectProjectExport(results, request, exportType);
    }
    return null;
  }

}
