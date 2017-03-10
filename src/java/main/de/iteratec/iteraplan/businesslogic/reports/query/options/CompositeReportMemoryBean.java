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
package de.iteratec.iteraplan.businesslogic.reports.query.options;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Composite.CompositeDiagramOptionsBean;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;


/**
 * Holds information for the composite diagram graphical report 
 */
public class CompositeReportMemoryBean extends ReportMemBean {

  /** Serialization version */
  private static final long                  serialVersionUID          = 8894299589385070477L;

  private final Map<String, String>          typeToMessageKey          = ImmutableMap.of(ReportType.BAR.getValue(), "graphicalExport.barDiagram",
                                                                           ReportType.PIE.getValue(), "graphicalExport.pieDiagram");

  private final List<ManageReportMemoryBean> graphicPartMemBeans       = CollectionUtils.arrayList();
  private List<SavedQuery>                   availablePartSavedQueries = CollectionUtils.arrayList();

  private boolean                            checkAllBox               = false;

  private CompositeDiagramOptionsBean        compositeOptions          = new CompositeDiagramOptionsBean();

  public List<ManageReportMemoryBean> getGraphicPartMemBeans() {
    return graphicPartMemBeans;
  }

  @edu.umd.cs.findbugs.annotations.SuppressWarnings("NP_NULL_ON_SOME_PATH")
  public void addGraphicPartMemBean(Integer savedQueryId, ManageReportMemoryBean partMemBean) {
    if (partMemBean != null && (ReportType.PIE.equals(partMemBean.getReportType())) || ReportType.BAR.equals(partMemBean.getReportType())) {
      graphicPartMemBeans.add(partMemBean);
    }
  }

  public void clearPartReports() {
    graphicPartMemBeans.clear();
    compositeOptions.getSelectedSavedQueryIds().clear();
    compositeOptions.setSelectionChanged(true);
  }

  public void setAvailablePartQueries(List<SavedQuery> availableSavedQueries) {
    this.availablePartSavedQueries = availableSavedQueries;
  }

  public List<SavedQuery> getAvailablePartQueries() {
    return availablePartSavedQueries;
  }

  public void setCompositeOptions(CompositeDiagramOptionsBean options) {
    this.compositeOptions = options;
  }

  public CompositeDiagramOptionsBean getCompositeOptions() {
    return compositeOptions;
  }

  /**
   * @return The {@link SavedQuery SavedQueries} of the reports selected as parts for
   *         the composite report in order of selection
   */
  public List<SavedQuery> getSelectedPartQueries() {
    List<SavedQuery> selectedQueries = CollectionUtils.arrayList();
    for (Integer id : compositeOptions.getSelectedSavedQueryIds()) {
      for (SavedQuery query : availablePartSavedQueries) {
        if (id.equals(query.getId())) {
          selectedQueries.add(query);
          break;
        }
      }
    }
    return selectedQueries;
  }

  /**
   * @return The {@link SavedQuery SavedQueries} of the reports not selected
   *         as parts for the composite report
   */
  public List<SavedQuery> getNotSelectedPartQueries() {
    List<SavedQuery> notSelectedQueries = CollectionUtils.arrayList();
    for (SavedQuery query : availablePartSavedQueries) {
      if (!compositeOptions.getSelectedSavedQueryIds().contains(query.getId())) {
        notSelectedQueries.add(query);
      }
    }
    return notSelectedQueries;
  }

  public void validateSelectedPartQueryIds() {
    if (!compositeOptions.getSelectedSavedQueryIds().isEmpty()) {
      Iterator<Integer> selectedIdsIter = compositeOptions.getSelectedSavedQueryIds().iterator();
      for (Integer id = selectedIdsIter.next(); selectedIdsIter.hasNext(); id = selectedIdsIter.next()) {
        boolean found = false;
        for (SavedQuery query : availablePartSavedQueries) {
          if (id.equals(query.getId())) {
            found = true;
            break;
          }
        }
        if (!found) {
          selectedIdsIter.remove();
          compositeOptions.setSelectionChanged(true);
        }
      }
    }
  }

  public void setCheckAllBox(boolean checkAllBox) {
    this.checkAllBox = checkAllBox;
  }

  public boolean isCheckAllBox() {
    return checkAllBox;
  }

  public Map<String, String> getTypeToMessageKey() {
    return typeToMessageKey;
  }

}
