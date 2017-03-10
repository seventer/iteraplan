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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;


/**
 * This class defines the view for all tabular reporting sites (massupdate, tabular- and graphical
 * reporting). The constructor fills all available and visible columns depending on the
 * TypeOfBuildingBlock. Attributes must be added apart. On tabular reporting site, the user can
 * modify the view an add / delete column entries.
 */
public class ViewConfiguration implements Serializable {

  /** Serialization version. */
  private static final long              serialVersionUID        = 2906531854641474014L;
  private static final String            GLOBAL_NAMEVERSION      = "global.nameversion";
  private static final String            GLOBAL_DESCRIPTION      = "global.description";
  private static final String            DESCRIPTION             = "description";

  private Locale                         locale;
  /** List of visible elements. Every entry contains informations for one column. */
  private List<ColumnEntry>              visibleColumns          = new ArrayList<ColumnEntry>();
  /** List of available and not visible elements */
  private List<ColumnEntry>              availableColumns        = new ArrayList<ColumnEntry>();
  /** List of available and not visible elements */
  private List<ColumnEntry>              addedColumns            = new ArrayList<ColumnEntry>();
  /** All elements (visible and available) */
  private final Map<String, ColumnEntry> allColumns              = new HashMap<String, ColumnEntry>();

  private boolean                        showTechnicalAttributes = false;

  /**
   * Constructor with no initialization of available and visible columns. Resulting elemtent can
   * only be used as dummy to avoid null pointer exceptions.
   * 
   * @param locale
   */
  public ViewConfiguration(Locale locale) {
    this.locale = locale;
  }

  /**
   * Constructor fills available and visible column elements depending on the TypeOfBuildingBlock
   * and the locale.
   * 
   * @param type
   *          For which type should the configuration be loaded
   * @param locale
   *          Translate Column entries in a given language.
   */
  public ViewConfiguration(TypeOfBuildingBlock type, Locale locale) {
    this.locale = locale;
    switch (type) {
      case INFORMATIONSYSTEMINTERFACE:
        getInterfaceConfig();
        break;
      case INFORMATIONSYSTEMRELEASE:
        getInformationSystemConfig();
        break;
      case PROJECT:
        getProjectConfig();
        break;
      case TECHNICALCOMPONENTRELEASE:
        getTechnicalComponentConfig();
        break;
      default:
        getSimpleConfig();
        break;
    }
  }

  /******************************************************/
  /** Methods for different configurations ******/
  private void getInformationSystemConfig() {
    addColumnEntryComplete(GLOBAL_NAMEVERSION, "hierarchicalName", COLUMN_TYPE.INHERITED, true);
    addColumnEntryComplete(GLOBAL_DESCRIPTION, DESCRIPTION, COLUMN_TYPE.DESCRIPTION, true);
    addColumnEntryComplete("global.from", "runtimePeriodNullSafe.start", COLUMN_TYPE.DATE, true);
    addColumnEntryComplete("global.to", "runtimePeriodNullSafe.end", COLUMN_TYPE.DATE, true);
    addColumnEntryComplete("global.type_of_status", "typeOfStatus", COLUMN_TYPE.TYPE_OF_STATUS, true);
    addColumnEntryComplete("global.parent", "parent", COLUMN_TYPE.NAME, false);
    addColumnEntryComplete("global.child", "children", COLUMN_TYPE.LIST, false);
    addColumnEntryComplete("global.predecessor", "predecessors", COLUMN_TYPE.LIST, false);
    addColumnEntryComplete("global.successor", "successors", COLUMN_TYPE.LIST, false);
    addColumnEntryComplete("global.uses", "baseComponents", COLUMN_TYPE.LIST, false);
    addColumnEntryComplete("global.usedBy", "parentComponents", COLUMN_TYPE.LIST, false);

    addColumnEntryComplete("global.lastModificationUser", "lastModificationUser", COLUMN_TYPE.NAME, false);
    addColumnEntryComplete("global.lastModificationTime", "lastModificationTime", COLUMN_TYPE.DATE, false);

    addColumnEntryComplete("seal", "lastSeal", COLUMN_TYPE.SEAL, false);

    //extra columns with technical attributes
    addColumnEntryComplete("csv.informationSystem.id", "informationSystemId", COLUMN_TYPE.DESCRIPTION, false, true);
    addColumnEntryComplete("csv.informationSystem.name", "informationSystemName", COLUMN_TYPE.DESCRIPTION, false, true);
    addColumnEntryComplete("csv.informationSystemRelease.id", "id", COLUMN_TYPE.DESCRIPTION, false, true);

    //addColumnEntryComplete("csv.informationSystemRelease.name", "name", COLUMN_TYPE.DESCRIPTION, false, true);

    addColumnEntryComplete("csv.parent.informationSystem.id", "parentInformationSystemId", COLUMN_TYPE.DESCRIPTION, false, true);
    addColumnEntryComplete("csv.parent.informationSystem.name", "parentInformationSystemName", COLUMN_TYPE.DESCRIPTION, false, true);
    addColumnEntryComplete("csv.informationSystemRelease.version", "version", COLUMN_TYPE.DESCRIPTION, false, true);
    addColumnEntryComplete("csv.parent.informationSystemRelease.version", "parentInformationSystemReleaseVersion", COLUMN_TYPE.DESCRIPTION, false,
        true);
    addColumnEntryComplete("csv.parent.informationSystemRelease.id", "parentInformationSystemReleaseId", COLUMN_TYPE.DESCRIPTION, false, true);

  }

  private void getSimpleConfig() {
    addColumnEntryComplete(GLOBAL_NAMEVERSION, "hierarchicalName", COLUMN_TYPE.INHERITED, true);
    addColumnEntryComplete(GLOBAL_DESCRIPTION, DESCRIPTION, COLUMN_TYPE.DESCRIPTION, true);
  }

  private void getProjectConfig() {
    addColumnEntryComplete(GLOBAL_NAMEVERSION, "hierarchicalName", COLUMN_TYPE.INHERITED, true);
    addColumnEntryComplete(GLOBAL_DESCRIPTION, DESCRIPTION, COLUMN_TYPE.DESCRIPTION, true);
    addColumnEntryComplete("global.from", "runtimePeriodNullSafe.start", COLUMN_TYPE.DATE, true);
    addColumnEntryComplete("global.to", "runtimePeriodNullSafe.end", COLUMN_TYPE.DATE, true);
    addColumnEntryComplete("global.parent", "parent", COLUMN_TYPE.NAME, false);
    addColumnEntryComplete("global.child", "children", COLUMN_TYPE.LIST, false);
  }

  private void getTechnicalComponentConfig() {
    addColumnEntryComplete(GLOBAL_NAMEVERSION, "releaseName", COLUMN_TYPE.NAME, true);
    addColumnEntryComplete(GLOBAL_DESCRIPTION, DESCRIPTION, COLUMN_TYPE.DESCRIPTION, true);
    addColumnEntryComplete("global.from", "runtimePeriodNullSafe.start", COLUMN_TYPE.DATE, true);
    addColumnEntryComplete("global.to", "runtimePeriodNullSafe.end", COLUMN_TYPE.DATE, true);
    addColumnEntryComplete("global.type_of_status", "typeOfStatus", COLUMN_TYPE.TYPE_OF_STATUS, true);
    addColumnEntryComplete("global.predecessor", "predecessors", COLUMN_TYPE.LIST, false);
    addColumnEntryComplete("global.successor", "successors", COLUMN_TYPE.LIST, false);
    addColumnEntryComplete("global.uses", "baseComponents", COLUMN_TYPE.LIST, false);
    addColumnEntryComplete("global.usedBy", "parentComponents", COLUMN_TYPE.LIST, false);
    addColumnEntryComplete("global.available_for_interfaces", "availableForInterfaces", COLUMN_TYPE.AVAILABLE_FOR_INTERFACES, false);
  }

  private void getInterfaceConfig() {
    addColumnEntryComplete("interface.singular", "name", COLUMN_TYPE.NAME, true);
    addColumnEntryComplete("reporting.result.interface.releaseA", "informationSystemReleaseA.hierarchicalName", COLUMN_TYPE.CONNECTION, true);
    addColumnEntryComplete("global.direction", "direction", COLUMN_TYPE.DIRECTION, true);
    addColumnEntryComplete("reporting.result.interface.releaseB", "informationSystemReleaseB.hierarchicalName", COLUMN_TYPE.CONNECTION, true);
    addColumnEntryComplete("reporting.result.interface.description", DESCRIPTION, COLUMN_TYPE.DESCRIPTION, true);
  }

  /** End Methods for different configurations **/
  /******************************************************/

  /**
   * Adds all attributes to the list of available entries. They are not added to the list of visible
   * elements.
   */
  public void addAttributeTypesToView(List<AttributeType> attributes) {
    for (AttributeType attType : attributes) {
      if (UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(attType.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ)) {
        addColumnEntryComplete(attType.getName(), attType.getId().toString(), COLUMN_TYPE.ATTRIBUTE, false);
      }
    }
  }

  /**
   * Opposite of deleteColumnEntry function. Adds an entry to the list of visible elements and
   * removes it from the list of available elements.
   * 
   * @param head
   *          Head key.
   */
  public void addColumnEntry(String head) {
    ColumnEntry entry = allColumns.get(head);
    if (availableColumns.contains(entry)) {
      visibleColumns.add(entry);
      addedColumns.add(entry);
      availableColumns.remove(entry);
    }
  }

  /**
   * Adds a Column Entry to the view. If selected is set to true, the value will be displayes at
   * start. Otherwise it will be visible in the available elements.
   * 
   * @param head
   *          Language key for table header. Is also used as unique hash key for storing the entry.
   * @param field
   *          Field in component model.
   * @param type
   *          The output is formated depending on the type. @see
   *          de.iteratec.iteraplan.presentation.dialogs
   *          .reporting.memorybeans.ColumnEntry.COLUMN_TYPE;
   * @param selected
   *          If (selected==true) the entry is displayed.
   */
  private void addColumnEntryComplete(String head, String field, ColumnEntry.COLUMN_TYPE type, boolean selected) {
    ColumnEntry entry = new ColumnEntry(field, type, head);
    if (selected) {
      visibleColumns.add(entry);
    }
    else {
      this.availableColumns.add(entry);
    }
    allColumns.put(head, entry);
  }

  private void addColumnEntryComplete(String head, String field, ColumnEntry.COLUMN_TYPE type, boolean selected, boolean technicalAttribute) {
    ColumnEntry entry = new ColumnEntry(field, type, head);
    entry.setTechnicalAttribute(technicalAttribute);
    if (selected) {
      visibleColumns.add(entry);
    }
    else {
      this.availableColumns.add(entry);
    }
    allColumns.put(head, entry);
  }

  /**
   * Deletes an entry from the list of visible elements and adds it to the list of available
   * elements.
   * 
   * @param head
   *          Head key.
   */
  public void deleteColumnEntry(String head) {
    ColumnEntry entry = allColumns.get(head);
    int index = visibleColumns.indexOf(entry);
    if (index == -1) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ELEMENT_DOES_NOT_EXIST_IN_VIEW);
    }
    availableColumns.add(entry);
    addedColumns.remove(entry);
    visibleColumns.remove(entry);
  }

  /**
   * Shift element to the left.
   * 
   * @param head
   *          Head key.
   */
  public void moveColumnEntryLeft(String head) {
    ColumnEntry entry = allColumns.get(head);
    int index = visibleColumns.indexOf(entry);
    if (index == -1) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ELEMENT_DOES_NOT_EXIST_IN_VIEW);
    }
    // First elements can't be shifted to the left
    if (index > 0) {
      Collections.swap(visibleColumns, index, index - 1);
    }
  }

  /**
   * Shift element to the right.
   * 
   * @param head
   *          Head key.
   */
  public void moveColumnEntryRight(String head) {
    ColumnEntry entry = allColumns.get(head);
    int index = visibleColumns.indexOf(entry);
    if (index == -1) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ELEMENT_DOES_NOT_EXIST_IN_VIEW);
    }
    // Last element can't be shifted to the right
    if (index < (visibleColumns.size() - 1)) {
      Collections.swap(visibleColumns, index, index + 1);
    }
  }

  /*********************************************/
  /******** Generated getter / setter **********/
  public List<ColumnEntry> getAvailableColumns() {
    return availableColumns;
  }

  public void setAvailableColumns(List<ColumnEntry> availableColumns) {
    this.availableColumns = availableColumns;
  }

  public List<ColumnEntry> getVisibleColumns() {
    return visibleColumns;
  }

  public void setVisibleColumns(List<ColumnEntry> visibleColumns) {
    this.visibleColumns = visibleColumns;
  }

  public List<ColumnEntry> getAddedColumns() {
    return addedColumns;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  /**
   * @return showTechnicalAttributes the showTechnicalAttributes
   */
  public boolean isShowTechnicalAttributes() {
    return showTechnicalAttributes;
  }

  public void setShowTechnicalAttributes(boolean showTechnicalAttributes) {
    this.showTechnicalAttributes = showTechnicalAttributes;
  }

}
