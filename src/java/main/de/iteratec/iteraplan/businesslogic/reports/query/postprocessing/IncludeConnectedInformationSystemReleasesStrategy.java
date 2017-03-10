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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.node.OperationNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.PropertyLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.BaseDateUtils;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * Adds all IS releases to the result set which have an interface to one or more IS releases in the
 * result set.
 */
public class IncludeConnectedInformationSystemReleasesStrategy extends AbstractPostprocessingStrategy<InformationSystemRelease> {

  /** Serialization version. */
  private static final long                serialVersionUID = -5902714067899755717L;
  private static final Logger              LOGGER           = Logger.getIteraplanLogger(IncludeConnectedInformationSystemReleasesStrategy.class);
  private final OptionConsiderStateAndDate stateOption      = new OptionConsiderStateAndDate();

  public IncludeConnectedInformationSystemReleasesStrategy(Integer orderNumber) {
    super(Constants.POSTPROCESSINGSTRATEGY_ADD_INTERFACED_ISR, orderNumber, new String[] { Constants.REPORTS_EXPORT_HTML,
        Constants.REPORTS_EXPORT_EXCEL_2007, Constants.REPORTS_EXPORT_EXCEL_2003, Constants.REPORTS_EXPORT_XMI,
        Constants.REPORTS_EXPORT_MSPROJECT_MSPDI, Constants.REPORTS_EXPORT_MSPROJECT_MPX, Constants.REPORTS_EXPORT_MSPROJECT_MSPDI_INCLUDING_SUBS,
        Constants.REPORTS_EXPORT_MSPROJECT_MPX_INCLUDING_SUBS, Constants.REPORTS_EXPORT_CSV, Constants.REPORTS_EXPORT_TABVIEW,
        Constants.REPORTS_EXPORT_GRAPHICAL_INFORMATIONFLOW, Constants.REPORTS_EXPORT_GRAPHICAL_PORTFOLIO,
        Constants.REPORTS_EXPORT_GRAPHICAL_MASTERPLAN, Constants.REPORTS_EXPORT_GRAPHICAL_LANDSCAPE, Constants.REPORTS_EXPORT_GRAPHICAL_CLUSTER,
        Constants.REPORTS_EXPORT_GRAPHICAL_BAR, Constants.REPORTS_EXPORT_GRAPHICAL_PIE });
  }

  @Override
  public Set<InformationSystemRelease> process(Set<InformationSystemRelease> isReleases, Node queryNode) {

    LOGGER.debug("Entering: process() of IncludeConnectedInformationSystemReleasesStrategy");

    HashMap<Integer, InformationSystemRelease> results = new HashMap<Integer, InformationSystemRelease>();

    for (InformationSystemRelease isr : isReleases) {
      if (results.get(isr.getId()) == null) {
        results.put(isr.getId(), isr);
      }

      for (InformationSystemInterface iface : isr.getAllConnections()) {

        addIsrToResultMap(results, iface.getInformationSystemReleaseA(), queryNode);
        addIsrToResultMap(results, iface.getInformationSystemReleaseB(), queryNode);
      }
    }

    LOGGER.debug("Leaving: process() of IncludeConnectedInformationSystemReleasesStrategy");

    return new HashSet<InformationSystemRelease>(results.values());
  }

  /**
   * Adds the given {@code isr} to the Map {@code idToIsrMap}, if not already contained
   * and state option is selected that matches state and date.
   * @param idToIsrMap
   *          Map<Integer, InformationSystemRelease> to put {@code isr} into
   * @param isr
   *          InformationSystemRelease to add to the map 
   * @param queryNode
   *          Node to check if state and date match
   */
  private void addIsrToResultMap(Map<Integer, InformationSystemRelease> idToIsrMap, InformationSystemRelease isr, Node queryNode) {
    if ((idToIsrMap.get(isr.getId()) == null) && (!stateOption.isSelected() || matchesStateAndDate(isr, queryNode))) {
      idToIsrMap.put(isr.getId(), isr);
    }
  }

  @Override
  public List<OptionConsiderStateAndDate> getAdditionalOptions() {
    ArrayList<OptionConsiderStateAndDate> options = new ArrayList<OptionConsiderStateAndDate>();
    options.add(this.stateOption);
    return options;
  }

  /**
   * Matches the state and the productive timespan of the given {@link InformationSystemRelease}
   * with all selected states and the productive timespan in the given {@code Node}.
   * 
   * @param rel
   *          The {@link InformationSystemRelease} to be matched against the {@code Node}.
   * @param node
   *          The {@code Node} to match the {@link InformationSystemRelease} against.
   * @return True, if the {@link InformationSystemRelease} matches the data passed in the given
   *         {@code Node}.
   */
  private boolean matchesStateAndDate(InformationSystemRelease rel, Node node) {

    List<TypeOfStatus> states = getSelectedStates(node);
    if (!states.contains(rel.getTypeOfStatus())) {
      return false;
    }

    SortedSet<Date> startDates = getNodeDates(node, InformationSystemReleaseTypeQu.PROPERTY_STARTDATE);
    SortedSet<Date> endDates = getNodeDates(node, InformationSystemReleaseTypeQu.PROPERTY_ENDDATE);

    Date startDate = startDates.size() == 0 ? BaseDateUtils.MIN_DATE.toDate() : startDates.first();
    Date endDate = endDates.size() == 0 ? BaseDateUtils.MAX_DATE.toDate() : endDates.last();

    RuntimePeriod period = new RuntimePeriod(startDate, endDate);

    return rel.runtimeOverlapsPeriod(period);
  }

  /**
   * Returns selected states in the given {@code Node} and all its child nodes.
   * 
   * @param node
   *          The node to be checked.
   * @return A list of statuses.
   */
  private List<TypeOfStatus> getSelectedStates(Node node) {
    ArrayList<TypeOfStatus> states = new ArrayList<TypeOfStatus>();
    if (node instanceof PropertyLeafNode) {
      PropertyLeafNode pln = (PropertyLeafNode) node;
      if (InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS.equals(pln.getPropertyName())) {
        states.add((TypeOfStatus) pln.getPattern());
      }
    }
    else if (node instanceof OperationNode) {
      OperationNode on = (OperationNode) node;
      List<Node> children = on.getChildren();
      for (Node child : children) {
        states.addAll(getSelectedStates(child));
      }
    }

    return states;
  }

  private SortedSet<Date> getNodeDates(Node node, String propertyName) {
    SortedSet<Date> nodeDates = new TreeSet<Date>();
    if (node instanceof PropertyLeafNode) {
      PropertyLeafNode pln = (PropertyLeafNode) node;
      if (propertyName.equals(pln.getPropertyName()) && !pln.getPattern().equals("null")) {
        nodeDates.add((Date) pln.getPattern());
      }
    }
    else if (node instanceof OperationNode) {
      OperationNode on = (OperationNode) node;
      List<Node> children = on.getChildren();
      for (Node child : children) {
        nodeDates.addAll(getNodeDates(child, propertyName));
      }
    }

    return nodeDates;
  }

}