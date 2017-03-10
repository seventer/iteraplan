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
package de.iteratec.iteraplan.businesslogic.reports.query.postprocessing;

import java.util.HashSet;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * This post processing strategy converts a result set so that it finally contains only the top
 * level Information Systems. This is done by the following steps:
 * <ul>
 * <li>For all Information Systems in the result set, the top-level Information Systems are found.
 * This new set will be processed, the original result set is discarded.
 * <li>The new set is then processed top-down. That means that all sub-Information Systems of the
 * top-level Information Systems will be processed.
 * <li>For each Information System A, its Interfaces are evaluated. If the Interface connects to an
 * Information System B, whose top-level parent is also in the set being processed, the Interface is
 * "copyied" to the corresponding top-level Information System.
 * <li>Interfaces that are copied to the top-level are merged with existing ones. Merging is done by
 * comparing the transported Business Objects and their direction and adjusting them as necessary.
 * So if an Interface between top-level A and top-level B already exists, their Business Objects and
 * Transports will be merged.
 * </ul>
 * Please note that the instances in the result set are heavily modified and do not reflect the
 * instances in the database anymore. Also: only the basic fields of the involved instances are
 * copied. All other relations will be lost.
 */
public class HideSubInformationSystemReleasesStrategy extends AbstractPostprocessingStrategy<InformationSystemRelease> {

  /** Serialization version. */
  private static final long   serialVersionUID = 4711739641536332768L;
  private static final Logger LOGGER           = Logger.getIteraplanLogger(HideSubInformationSystemReleasesStrategy.class);

  public HideSubInformationSystemReleasesStrategy(Integer orderNumber) {
    super(Constants.POSTPROCESSINGSTRATEGY_HIDE_CHILDREN, orderNumber, new String[] { Constants.REPORTS_EXPORT_HTML,
        Constants.REPORTS_EXPORT_EXCEL_2007, Constants.REPORTS_EXPORT_EXCEL_2003, Constants.REPORTS_EXPORT_XMI,
        Constants.REPORTS_EXPORT_MSPROJECT_MSPDI, Constants.REPORTS_EXPORT_MSPROJECT_MPX, Constants.REPORTS_EXPORT_MSPROJECT_MSPDI_INCLUDING_SUBS,
        Constants.REPORTS_EXPORT_MSPROJECT_MPX_INCLUDING_SUBS, Constants.REPORTS_EXPORT_CSV, Constants.REPORTS_EXPORT_TABVIEW,
        Constants.REPORTS_EXPORT_GRAPHICAL_PORTFOLIO, Constants.REPORTS_EXPORT_GRAPHICAL_MASTERPLAN, Constants.REPORTS_EXPORT_GRAPHICAL_CLUSTER,
        Constants.REPORTS_EXPORT_GRAPHICAL_LANDSCAPE, Constants.REPORTS_EXPORT_GRAPHICAL_BAR, Constants.REPORTS_EXPORT_GRAPHICAL_PIE });
  }

  @Override
  public Set<InformationSystemRelease> process(Set<InformationSystemRelease> isReleases, Node queryNode) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Calling post processing strategy HideSubInformationSystemReleasesStrategy with " + isReleases.size() + " IS releases");
    }
    Set<InformationSystemRelease> results = new HashSet<InformationSystemRelease>();
    for (InformationSystemRelease isr : isReleases) {
      isr = isr.getPrimeFather();
      results.add(isr);
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("returning " + results.size() + " IS releases");
    }

    return results;
  }

}
