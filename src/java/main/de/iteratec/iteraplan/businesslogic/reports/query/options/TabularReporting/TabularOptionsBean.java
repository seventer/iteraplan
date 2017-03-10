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

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;


/**
 * Container for the parameters of the Cluster diagram.
 */
public class TabularOptionsBean implements Serializable {

  /** Serialization version. */
  private static final long  serialVersionUID               = 7351923044445399717L;

  private String             resultFormat;
  /** The template name for the {@link de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator.ResultFormat}. */
  private String             resultFormatTemplate;
  /** Holds the available template names for the currently selected result format */
  private List<ExportOption> availableResultFormatTemplates = Lists.newArrayList();

  public String getResultFormat() {
    return resultFormat;
  }

  public void setResultFormat(String resultFormat) {
    this.resultFormat = resultFormat;
  }

  /**
   * Returns the template name for the {@link #getResultFormat()}. Some formats 
   * do not have any templates.
   * 
   * @return the template name 
   */
  public String getResultFormatTemplate() {
    return resultFormatTemplate;
  }

  /**
   * Sets the template name for the {@link #getResultFormat()}.
   * 
   * @param resultFormatTemplate the template name
   */
  public void setResultFormatTemplate(String resultFormatTemplate) {
    this.resultFormatTemplate = resultFormatTemplate;
  }

  public List<ExportOption> getAvailableResultFormatTemplates() {
    return availableResultFormatTemplates;
  }

  public void setAvailableResultFormatTemplates(List<ExportOption> availableResultFormatTemplates) {
    this.availableResultFormatTemplates = availableResultFormatTemplates;
  }

}
