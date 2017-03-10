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
package de.iteratec.iteraplan.presentation.dialog.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.sorting.BuildingBlockSortHelper;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model for a 1:N-association of a source type 'T' to a target type 'F', where the
 * association is modeled as an ordered list.
 */
public abstract class ManyAssociationListComponentModel<F extends IdentityEntity, T extends IdentityEntity & Comparable<? super T>> extends
AbstractComponentModelBase<F> {

  private static final long   serialVersionUID   = -2902919277577335071L;

  private static final Logger LOGGER             = Logger.getIteraplanLogger(ManyAssociationListComponentModel.class);

  private static final String ACTION_REMOVE      = "remove";
  private static final String ACTION_MOVE_TOP    = "moveTop";
  private static final String ACTION_MOVE_UP     = "moveUp";
  private static final String ACTION_MOVE_DOWN   = "moveDown";
  private static final String ACTION_MOVE_BOTTOM = "moveBottom";

  public static final String  SORTORDER_NONE     = "none";
  public static final String  SORTORDER_ASC      = "asc";
  public static final String  SORTORDER_DESC     = "desc";

  private String              sortOrder          = SORTORDER_ASC;

  /** List of all connected elements for the association (n-side). */
  private List<T>             connectedElements  = new ArrayList<T>();

  /** List of unconnected elements that are still available for the association. */
  private List<T>             availableElements  = new ArrayList<T>();

  /**
   * The position of the selected list element. The first element has position 1, the second 2, ...
   * This unusual index start is necessary as the web framework converts a null/empty value for an
   * Integer into the value 0. The index must therefore be shifted.
   */
  private Integer             selectedPosition;

  /**
   * The id of an element to add to the list of connected elements.
   * If null or <= 0, no element should be added.
   */
  private Integer             elementIdToAdd     = null;

  /**
   * The name of the action to carry out. All possible position- and remove-actions are
   * defined by the static final ACTION_... Strings.
   */
  private String              action;

  /** The filter condition. If null or empty, no filter should be applied. */
  private String              filter             = null;

  /** Flag to indicate that the filter was activated in the last request. */
  private boolean             filterWasActivated = false;

  /** The managed source element (1-side) */
  private F                   sourceElement;

  /** Property key for the header of the table. */
  private final String        tableHeaderKey;

  /** Property keys for the columns of the table. */
  private String[]            columnHeaderKeys;

  /** The names of the properties of all connected instances of 'T' to show (e.g. {"name", "description"}. */
  private String[]            connectedElementsFields;

  /** Name of a property of 'T' that uniquely represents every available element (e.g. name). */
  private final String        availableElementsLabel;

  /** Dummy element used as the first entry in the list of available elements. */
  protected final T             dummyForPresentation;

  /** {@link #getErrorCode()} */
  private int                 errorCode          = IteraplanErrorMessages.CANNOT_REMOVE_ELEMENT_FROM_HIERARCHY;

  /**
   * @param componentMode The component mode to set.
   * @param htmlId The HTML id that can be used for identifying this component in functional tests.
   * @param tableHeaderKey The I18N key that describes this component.
   * @param columnHeaderKeys The I18N keys that describe the fields of connected elements.
   *                         The array must have the same length as {@link #connectedElementsFields}
   * @param connectedElementsFields The fields that of the connected elements to be shown.
   *                                The array must have the same length as {@link #columnHeaderKeys}
   * @param availableElementsLabel The field that is used for displaying the list of available elements.
   * @param dummyForPresentation An empty instance of the managed entities.
   */
  public ManyAssociationListComponentModel(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
                                           String[] connectedElementsFields, String availableElementsLabel, T dummyForPresentation) {
    super(componentMode, htmlId);
    this.tableHeaderKey = tableHeaderKey;
    this.columnHeaderKeys = (columnHeaderKeys == null) ? null : columnHeaderKeys.clone();
    this.connectedElementsFields = (connectedElementsFields == null) ? null : connectedElementsFields.clone();
    this.availableElementsLabel = availableElementsLabel;
    this.dummyForPresentation = dummyForPresentation;
    if ((null == this.columnHeaderKeys) || (null == this.connectedElementsFields)
        || (this.columnHeaderKeys.length != this.connectedElementsFields.length)) {
      throw new IllegalArgumentException("columnHeaderKeys and connectedElementsFields must have the same number of entries.");
    }
  }

  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }

  public String getSortOrder() {
    return sortOrder;
  }

  public String getAction() {
    return action;
  }

  public String getAvailableElementsLabel() {
    return availableElementsLabel;
  }

  public List<T> getAvailableElementsPresentation() {
    List<T> availableElementsPresentation = new ArrayList<T>();
    availableElementsPresentation.add(dummyForPresentation);
    availableElementsPresentation.addAll(getAvailableElements());
    return availableElementsPresentation;
  }

  public String[] getColumnHeaderKeys() {
    if (columnHeaderKeys == null) {
      return null;
    }
    else { // copy
      return columnHeaderKeys.clone();
    }
  }

  public List<T> getConnectedElements() {
    return connectedElements;
  }

  public String[] getConnectedElementsFields() {
    if (connectedElementsFields == null) {
      return null;
    }
    else { // copy
      return connectedElementsFields.clone();
    }
  }

  public Integer getElementIdToAdd() {
    return elementIdToAdd;
  }

  /**
   * @return
   *    The currently {@link #setErrorCode(int) set} error code.
   */
  public int getErrorCode() {
    return errorCode;
  }

  public String getFilter() {
    return filter;
  }

  public Integer getSelectedPosition() {
    return selectedPosition;
  }

  public F getSourceElement() {
    return sourceElement;
  }

  public String getTableHeaderKey() {
    return tableHeaderKey;
  }

  public boolean isFilterWasActivated() {
    return filterWasActivated;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public void setElementIdToAdd(Integer elementIdToAdd) {
    this.elementIdToAdd = elementIdToAdd;
  }

  /**
   * Specify an error code that will be thrown (wrapped inside an exception) from methods of this
   * component model. Arbitrary error codes may be set, but the code has an initial default value
   * of {@link IteraplanErrorMessages#CANNOT_REMOVE_ELEMENT_FROM_HIERARCHY}. A method which throws
   * an exception with an error code describes this fact in its documentation.
   * 
   * @param errorCode
   *    The error code to set.
   */
  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public void setFilterWasActivated(boolean filterWasActivated) {
    this.filterWasActivated = filterWasActivated;
  }

  public void setSelectedPosition(Integer selectedPosition) {
    this.selectedPosition = selectedPosition;
  }

  public void initializeFrom(F source) {
    this.sourceElement = source;
    this.connectedElements = new ArrayList<T>(getConnectedElements(source));
    if (getComponentMode() != ComponentMode.READ) {
      this.availableElements = getAvailableElements(sourceElement, this.connectedElements);
    }
    sortElements(availableElements);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void sortElements(List list) {
    if (list.isEmpty()) {
      return;
    }
    else if (list.get(0) instanceof BuildingBlock) {
      BuildingBlockSortHelper.sortByDefault(list);
    }
    else {
      Collections.sort(list);
    }
  }

  public void update() {
    if (!filterWasActivated) {
      filter = "";
    }
    filterWasActivated = false;

    this.processElementIdToAdd();
    elementIdToAdd = null;

    if (selectedPosition != null && selectedPosition.intValue() > 0) {
      int index = selectedPosition.intValue() - 1;
      LOGGER.debug("Action {0} was called for list element on position {1}", action, selectedPosition);
      T element = connectedElements.get(index);
      if (ACTION_MOVE_TOP.equals(action)) {
        connectedElements.remove(index);
        connectedElements.add(0, element);
      }
      else if (ACTION_MOVE_UP.equals(action)) {
        if (index > 0) {
          connectedElements.remove(index);
          connectedElements.add(index - 1, element);
        }
      }
      else if (ACTION_MOVE_DOWN.equals(action)) {
        if (index < connectedElements.size() - 1) {
          connectedElements.remove(index);
          connectedElements.add(index + 1, element);
        }
      }
      else if (ACTION_MOVE_BOTTOM.equals(action)) {
        connectedElements.remove(index);
        connectedElements.add(element);
      }
      else if (ACTION_REMOVE.equals(action)) {
        if (isElementRemovable()) {
          connectedElements.remove(index);
        }
        else {
          throw new IteraplanBusinessException(getErrorCode(), element.getIdentityString());
        }
      }
    }

    sortElements(availableElements);

    // reset input fields after update:
    action = null;
    selectedPosition = null;
  }

  protected void processElementIdToAdd() {
    if (elementIdToAdd != null && elementIdToAdd.intValue() > 0) {
      for (Iterator<T> it = availableElements.iterator(); it.hasNext();) {
        T available = it.next();
        Integer availableId = available.getId();
        if (elementIdToAdd.equals(availableId)) {
          it.remove();
          connectedElements.add(available);
          break;
        }
      }
    }
  }

  //sorts the connectedElements list in ascending or descending order
  public void sort() {

    //A generic comparator is created in order to compare different hierarchical entities by their names.
    Comparator<T> comparator = new Comparator<T>() {

      //toString() delivers the name of the hierarchical entity
      public int compare(T o1, T o2) {
        if ("asc".equals(sortOrder)) {
          return o1.toString().compareToIgnoreCase(o2.toString());
        }
        else if ("desc".equals(sortOrder)) {
          return o2.toString().compareToIgnoreCase(o1.toString());
        }
        else {
          throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
        }
      }
    };

    if (comparator != null) {
      Collections.sort(connectedElements, comparator);
    }
  }

  public void configure(F target) {
    setConnectedElements(target, connectedElements);
  }

  public void validate(Errors errors) {
    // do nothing
  }
  
  public boolean isDynamicallyLoaded(){
    return false;
  }

  /**
   * Allows subclasses of component models to adjust the set of fields that will be dispayed in the
   * corresponding tiles. Needed for mass update component models.
   * @param newColumnHeaderKeys The I18N keys that describe the fields of connected elements.
   * @param newConnectedElementsFields The fields that of the connected elements to be shown.
   */
  public void updateHeaderAndFields(String[] newColumnHeaderKeys, String[] newConnectedElementsFields) {
    this.columnHeaderKeys = (newColumnHeaderKeys == null) ? null : newColumnHeaderKeys.clone();
    this.connectedElementsFields = (newConnectedElementsFields == null) ? null : newConnectedElementsFields.clone();
    if ((null == this.columnHeaderKeys) || (null == this.connectedElementsFields)
        || (this.columnHeaderKeys.length != this.connectedElementsFields.length)) {
      throw new IllegalArgumentException("columnHeaderKeys and connectedElementsFields must have the same number of entries.");
    }
  }

  protected List<T> getAvailableElements() {
    return availableElements;
  }

  /**
   * Returns the List of available elements for a source that has an ordered association to elements of type 'T'.
   * The source instance is specified by its id. The List can be filtered with currentFilter.
   * 
   * @param source The managed element, for which the available list should be created.
   * @param alreadyConnectedElements The list of already connected elements.
   *                                 These elements never occur in the list of available elements.
   * @return List of available elements of type 'T'.
   */
  protected abstract List<T> getAvailableElements(F source, List<T> alreadyConnectedElements);

  /**
   * Returns the list of all connected elements for the given source.
   * 
   * @param source The instance of type 'F' that has an ordered association to elements of type 'T'
   * @return List of connected elements of type 'T'.
   */
  protected abstract List<T> getConnectedElements(F source);

  /**
   * @return
   *    Returns {@code true} if an element on the many-side may be removed from the association.
   *    For example, this might not be the case if the element is already located on the topmost
   *    level of a hierarchy. If the element is not removable, an exception with the error code
   *    defined by {@link #getErrorCode()} is thrown.
   */
  protected abstract boolean isElementRemovable();

  /**
   * Sets the given list of reference objects (type 'T') for the target of type 'F'
   * 
   * @param target The target object to configure.
   * @param referenceObjects List of reference objects, ordered.
   */
  protected abstract void setConnectedElements(F target, List<T> referenceObjects);

}