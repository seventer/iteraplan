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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Composite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportOptionsWithOrderedList;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.CollectionUtils;


/**
 * Container for the parameters of the Composite diagrams.
 */
public class CompositeDiagramOptionsBean extends GraphicalExportOptionsWithOrderedList implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID      = 7192842769695222841L;

  private static final int  NONE                  = -1;

  private List<Integer>     selectedSavedQueryIds = CollectionUtils.arrayList();
  private boolean           selectionChanged      = true;

  public CompositeDiagramOptionsBean() {
    super();
    setSelectedGraphicFormat(Constants.REPORTS_EXPORT_GRAPHICAL_SVG);
    setAvailableGraphicFormats(ExportOption.getGraphicalExportOptions(false));
  }

  public List<Integer> getSelectedSavedQueryIds() {
    return selectedSavedQueryIds;
  }

  public void setSelectedSavedQueryIds(List<Integer> ids) {
    if (!selectedSavedQueryIds.equals(ids)) {
      selectionChanged = true;
      if (ids == null) {
        this.selectedSavedQueryIds.clear();
      }
      else {
        this.selectedSavedQueryIds = ids;
      }
    }
  }

  public void setSelectionChanged(boolean selectionChanged) {
    this.selectionChanged = selectionChanged;
  }

  public boolean isSelectionChanged() {
    return selectionChanged;
  }

  @Override
  public void refreshOrder() {
    if (getMovedItem() != NONE) {
      setSelectionChanged(true);
      switch (getMoveType()) {
        case TOP:
          selectedSavedQueryIds.add(0, selectedSavedQueryIds.remove(getMovedItem()));
          break;
        case UP:
          if (getMovedItem() > 0) {
            selectedSavedQueryIds.add(getMovedItem() - 1, selectedSavedQueryIds.remove(getMovedItem()));
          }
          break;
        case DOWN:
          if (getMovedItem() < (selectedSavedQueryIds.size() - 1)) {
            selectedSavedQueryIds.add(getMovedItem() + 1, selectedSavedQueryIds.remove(getMovedItem()));
          }
          break;
        case BOTTOM:
          final int position = selectedSavedQueryIds.size() - 1;
          selectedSavedQueryIds.add(position, selectedSavedQueryIds.remove(getMovedItem()));
          break;
        default:
          // Nothing to do.
          break;
      }
      // reset after performing movement
      setMovedItem(NONE);
      setMove(Movement.HOLD_POSITION.toInteger());
    }
  }

  public List<String> getQueryResultNames() {
    // composite diagram doesn't need queries on its own
    return new ArrayList<String>();
  }

}