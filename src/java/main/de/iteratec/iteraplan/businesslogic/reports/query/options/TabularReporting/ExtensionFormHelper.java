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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;


/**
 * Helper class for moving {@link IPresentationExtension}s between a list and a map.
 */
public class ExtensionFormHelper {

  private InitFormHelperService initFormHelperService;

  public void setInitFormHelperService(InitFormHelperService initFormHelperService) {
    this.initFormHelperService = initFormHelperService;
  }

  /**
   * Adds the ReportExtension specified by the given Extension identifier String to the given List
   * of DynamicQeryFormData instances and adapts the Map of available ReportExtensions.
   * 
   * @param extension
   *          The name of the extension to add as defined in the appropriate Type instance.
   * @param queryForms
   *          List of DynamicQueryFormData.
   * @param availableReportExtensions
   *          Map that maps extension names to Extension instances (String, Extension).
   */
  public void addReportExtension(String extension, List<DynamicQueryFormData<?>> queryForms,
                                 Map<String, IPresentationExtension> availableReportExtensions) {
    if (extension == null) {
      return;
    }
    IPresentationExtension ext = availableReportExtensions.get(extension);
    if (ext == null) {
      return;
    }
    DynamicQueryFormData<?> form = initFormHelperService.getReportForm(ext);
    if (form != null) {
      queryForms.add(form);
      availableReportExtensions.remove(extension);
    }
  }

  // TODO too specific... refactor.
  /**
   * Removes the ReportExtension specified by the given Extension identifier String from the given
   * List of DynamicQeryFormData instances and adapts the Map of available ReportExtensions.
   * 
   * @param queryForms
   *          List of DynamicQueryFormData.
   * @param availableReportExtensions
   *          Map that maps extension names to Extension instances.
   * @param extensionId
   *          The id of the extension to remove.
   */
  public void removeExportExtension(List<DynamicQueryFormData<?>> queryForms, Map<String, IPresentationExtension> availableReportExtensions,
                                    Integer extensionId) {
    if (extensionId != null && extensionId.intValue() < queryForms.size()) {
      DynamicQueryFormData<?> form = queryForms.get(extensionId.intValue());
      IPresentationExtension ext = form.getExtension();
      availableReportExtensions.put(ext.getName(), ext);
      queryForms.remove(extensionId.intValue());
    }
  }

}
