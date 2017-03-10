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
package de.iteratec.iteraplan.presentation.dialog;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.support.MutableSortDefinition;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.presentation.memory.ColumnDefinition;


/**
 *  Columns and sort definition for overview pages.
 */
public class GuiTableState implements Serializable {

  private static final long      serialVersionUID           = -3243676314237738568L;

  /** List of available and not visible elements */
  private List<ColumnDefinition> availableColumnDefinitions = new LinkedList<ColumnDefinition>();

  /** List of visible elements. */
  private List<ColumnDefinition> visibleColumnDefinitions   = new LinkedList<ColumnDefinition>();

  private MutableSortDefinition  sortDefinition;

  public void addColumnEntry(ColumnDefinition columnDefinition) {

    if (columnDefinition.isVisibleOnInit()) {
      if (!visibleColumnDefinitions.contains(columnDefinition)) {
        visibleColumnDefinitions.add(columnDefinition);
      }
    }
    else {
      if (!availableColumnDefinitions.contains(columnDefinition)) {
        availableColumnDefinitions.add(columnDefinition);
      }
    }

  }

  public void addColumnEntries(List<ColumnDefinition> columnDefinitionsList) {
    for (ColumnDefinition columnDefinition : columnDefinitionsList) {
      addColumnEntry(columnDefinition);
    }
  }

  public void addColumn(int index) {
    if (index < 0 || index >= getAvailableColumnDefinitions().size()) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ELEMENT_DOES_NOT_EXIST_IN_VIEW);
    }

    ColumnDefinition columnProps = getAvailableColumnDefinitions().get(index);
    getVisibleColumnDefinitions().add(getVisibleColumnDefinitions().size(), columnProps);
    getAvailableColumnDefinitions().remove(index);
  }

  public void removeColumn(int index) {
    if (index < 0 || index >= getVisibleColumnDefinitions().size()) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ELEMENT_DOES_NOT_EXIST_IN_VIEW);
    }

    ColumnDefinition columnProps = visibleColumnDefinitions.get(index);
    getAvailableColumnDefinitions().add(columnProps);
    getVisibleColumnDefinitions().remove(index);
  }

  public void moveColumnLeft(int index) {
    if (index < 0 || index >= getVisibleColumnDefinitions().size()) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ELEMENT_DOES_NOT_EXIST_IN_VIEW);
    }

    if (index > 0) {
      getVisibleColumnDefinitions().add(index - 1, getVisibleColumnDefinitions().remove(index));
    }
  }

  public void moveColumnRight(int index) {
    if (index < 0 || index >= getVisibleColumnDefinitions().size()) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ELEMENT_DOES_NOT_EXIST_IN_VIEW);
    }

    if (index < getVisibleColumnDefinitions().size() - 1) {
      visibleColumnDefinitions.add(index + 1, visibleColumnDefinitions.remove(index));
    }
  }

  public List<ColumnDefinition> getAvailableColumnDefinitions() {
    return availableColumnDefinitions;
  }

  public void setAvailableColumnDefinitions(List<ColumnDefinition> availableColumnDefinitions) {
    this.availableColumnDefinitions = availableColumnDefinitions;
  }

  public List<ColumnDefinition> getVisibleColumnDefinitions() {
    return visibleColumnDefinitions;
  }

  public void setVisibleColumnDefinitions(List<ColumnDefinition> visibleColumnDefinitions) {
    this.visibleColumnDefinitions = visibleColumnDefinitions;
  }

  public MutableSortDefinition getSortDefinition() {
    return sortDefinition;
  }

  public void setSortDefinition(MutableSortDefinition sortDefinition) {
    this.sortDefinition = sortDefinition;
  }
}
