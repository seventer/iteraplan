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
package de.iteratec.iteraplan.setup;

/**
 * This class holds information about saved diagrams. Needed in both OSS and enterprise version
 * 
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 */
public final class SavedDiagramConstants {

  public static final String XML_FILE_LOCATION_URI               = "/";
  public static final String XML_FILE_EXTENSION                  = ".xml";

  /** Landscape Query 1 */
  public static final String LANDSCAPE_QUERY_ONE_FILENAME        = "landscapeQueryOne";
  /** Information Flow Query 1 */
  public static final String INFORMATION_FLOW_QUERY_ONE_FILENAME = "informationFlowQueryOne";
  /** Portfolio Query 1 */
  public static final String PORTFOLIO_QUERY_ONE_FILENAME        = "portfolioQueryOne";
  /** Masterplan Query 1 */
  public static final String MASTERPLAN_QUERY_ONE_FILENAME       = "masterplanQueryOne";
  /** Cluster Query 1 */
  public static final String CLUSTER_QUERY_ONE_FILENAME          = "clusterQueryOne";

  public static final String LANDSCAPE_QUERY_ONE_COLOR           = "Komplexität";
  public static final String LANDSCAPE_QUERY_ONE_LINE_TYPE       = "Systemgröße";

  /** empty private constructor */
  private SavedDiagramConstants() {
    // hide constructor
  }

  public static String getLandscapeQueryOneURI() {
    return XML_FILE_LOCATION_URI + LANDSCAPE_QUERY_ONE_FILENAME + XML_FILE_EXTENSION;
  }

  public static String getInformationFlowQueryOneURI() {
    return XML_FILE_LOCATION_URI + INFORMATION_FLOW_QUERY_ONE_FILENAME + XML_FILE_EXTENSION;
  }

  public static String getPortfolioQueryOneURI() {
    return XML_FILE_LOCATION_URI + PORTFOLIO_QUERY_ONE_FILENAME + XML_FILE_EXTENSION;
  }

  public static String getMasterplanQueryOneURI() {
    return XML_FILE_LOCATION_URI + MASTERPLAN_QUERY_ONE_FILENAME + XML_FILE_EXTENSION;
  }

  public static String getClusterQueryOneURI() {
    return XML_FILE_LOCATION_URI + CLUSTER_QUERY_ONE_FILENAME + XML_FILE_EXTENSION;
  }
}
