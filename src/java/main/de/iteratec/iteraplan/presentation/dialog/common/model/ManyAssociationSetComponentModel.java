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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.sorting.BuildingBlockSortHelper;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model for a N-association of a source type 'F' to a target type 'T' where the
 * association is modeled as a set, i.e. with no inherent ordering.
 */
public abstract class ManyAssociationSetComponentModel<F extends IdEntity, T extends Comparable<? super T> & IdEntity> extends
AbstractComponentModelBase<F> {

  private static final long   serialVersionUID   = 7651191013370770880L;

  private static final Logger LOGGER             = Logger.getIteraplanLogger(ManyAssociationSetComponentModel.class);

  private F                   managedElement;

  private List<T>             connectedElements  = new ArrayList<T>();

  private List<T>             availableElements  = new ArrayList<T>();

  private Integer             elementIdToAdd     = null;

  private Integer             elementIdToRemove  = null;

  private Integer[]           elementIdsToAdd    = new Integer[0];

  private Integer[]           elementIdsToRemove = new Integer[0];

  private final String        availableElementsLabel;

  private final String        tableHeaderKey;

  private String[]            columnHeaderKeys;

  private String[]            connectedElementsFields;

  protected final T           dummyForPresentation;

  private Boolean[]           lookupLablesMode;

  private Boolean             lookupAvailableLablesMode;

  private String[]            availableElementsPresentationGroupKeys;

  /**
   * @param componentMode The component mode for this component.
   * @param htmlId The html id the JSP should use for this component. This facilitates
   *               the writing of automated tests.
   * @param tableHeaderKey The I18N key the JSP should use as a heading for this component.
   * @param columnHeaderKeys The I18N keys the JSP should use as the headings for the columns
   *                         of the connected elements. The number of keys corresponds to the
   *                         number of columns.
   * @param connectedElementsFields The fields of the connected elements that can be displayed
   *                                by the JSP. The fields correspond to the columnHeaderKeys.
   *                                For example: If the first columnHeaderKey is 'global.name', the
   *                                first connectedElementsField could be 'name'.
   * @param availableElementsLabel The field of the available elements that should be used for
   *                               display. For example: 'name'.
   * @param dummyForPresentation An empty instance of the class on the other side of the association.
   *                             This dummy is added to the available list of elements to represent
   *                             no selected available element.
   */
  public ManyAssociationSetComponentModel(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
                                          String[] connectedElementsFields, String availableElementsLabel, T dummyForPresentation) {
    super(componentMode, htmlId);
    this.tableHeaderKey = tableHeaderKey;
    this.columnHeaderKeys = (columnHeaderKeys == null) ? null : columnHeaderKeys.clone();
    this.connectedElementsFields = (connectedElementsFields == null) ? null : connectedElementsFields.clone();
    this.availableElementsLabel = availableElementsLabel;
    this.dummyForPresentation = dummyForPresentation;
    checkHeaderAndFields();
  }

  private void checkHeaderAndFields() {

    if (columnHeaderKeys == null ^ connectedElementsFields == null) {
      throw new IllegalStateException("Both the fields 'columnHeaderKeys' and 'connectedElementsFields' must either null or not null.");
    }

    if (columnHeaderKeys != null && connectedElementsFields != null && columnHeaderKeys.length != connectedElementsFields.length) {
      throw new IllegalStateException("The fields 'columnHeaderKeys' and 'connectedElementsFields' must have the same length.");
    }
  }

  /**
   * @param componentMode The component mode for this component.
   * @param htmlId The html id the JSP should use for this component. This facilitates
   *               the writing of automated tests.
   * @param tableHeaderKey The I18N key the JSP should use as a heading for this component.
   * @param columnHeaderKeys The I18N keys the JSP should use as the headings for the columns
   *                         of the connected elements. The number of keys corresponds to the
   *                         number of columns.
   * @param connectedElementsFields The fields of the connected elements that can be displayed
   *                                by the JSP. The fields correspond to the columnHeaderKeys.
   *                                For example: If the first columnHeaderKey is 'global.name', the
   *                                first connectedElementsField could be 'name'.
   * @param availableElementsLabel The field of the available elements that should be used for
   *                               display. For example: 'name'.
   * @param dummyForPresentation An empty instance of the class on the other side of the association.
   *                             This dummy is added to the available list of elements to represent
   *                             no selected available element.
   * @param lookupLablesMode This boolean list defines, which of the connectedElementsFields are
   *                         I18N keys that have to be resolved. For example, if the
   *                         connectedElementsFields are: ['name', 'typeofstatus'] and the
   *                         lookupLablesMode are: [false, true], then the field value of name can be
   *                         displayed while the field value of typeofstatus is an I18N key that has
   *                         to be resolved. Set to null if not needed.
   * @param lookupAvailableLablesMode If true, then the availableElementsLabel points to a field which
   *                                  is an I18N key that has to be resolved. Set to false if not needed.
   * @param availableElementsPresentationGroupKeys A list of I18N keys. If this list is set, the method
   *                                               {{@link #getAvailableElements()} is expected to return
   *                                               a list of lists which correspond to the I18N keys. With
   *                                               this mechanism, the available elements can be grouped.
   *                                               Set to null if not needed.
   */
  public ManyAssociationSetComponentModel(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
                                          String[] connectedElementsFields, String availableElementsLabel, T dummyForPresentation, Boolean[] lookupLablesMode,
                                          Boolean lookupAvailableLablesMode, String[] availableElementsPresentationGroupKeys) {
    this(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);

    this.lookupLablesMode = (lookupLablesMode == null) ? null : lookupLablesMode.clone();
    this.lookupAvailableLablesMode = lookupAvailableLablesMode;
    this.availableElementsPresentationGroupKeys = (availableElementsPresentationGroupKeys == null) ? null : availableElementsPresentationGroupKeys
        .clone();

    boolean connectedElementsFieldsNull = this.lookupLablesMode != null && this.connectedElementsFields == null;
    boolean lookupLablesModeNull = this.lookupLablesMode == null && this.connectedElementsFields != null;
    boolean bothNotNull = this.lookupLablesMode != null && this.connectedElementsFields != null;
    if (connectedElementsFieldsNull || lookupLablesModeNull || (bothNotNull && this.connectedElementsFields.length != this.lookupLablesMode.length)) {
      throw new IllegalArgumentException("lookUpLablesMode and connectedElementsFields must have the same number of entries.");
    }
  }

  /**
   * This method allows to adjust:
   * <ul>
   *    <li>The set of resource bundle keys used as headers for connected elements.</li>
   *    <li>The set of fields used to render the content of connected elements.</li>
   * </ul>
   * <p>
   * Note: Primarily needed in the mass update component models.
   * 
   * @param header
   *    The resource bundle keys that are used as headers.
   * @param fields
   *    The fields of the connected elements.
   */
  public void updateHeaderAndFields(String[] header, String[] fields) {

    this.columnHeaderKeys = (header == null) ? null : header.clone();
    this.connectedElementsFields = (fields == null) ? null : fields.clone();
    checkHeaderAndFields();
  }

  /**
   * Returns a list of all connected elements for the given source element.
   * 
   * @param source
   *    The element for which connected elemented should be retrieved.
   * @return
   *    A list of connected elements.
   */
  protected abstract Set<T> getConnectedElements(F source);

  /**
   * Connects the given set of elements to the given target element.
   * 
   * @param target
   *    The target element for which connected elements should be set.
   * @param toConnect
   *    A set of elements that should be connected to the target element.
   */
  protected abstract void setConnectedElements(F target, Set<T> toConnect);

  /**
   * Returns a list of available elements for a source element that has an unordered
   * association to elements of type 'T'. The source instance is specified by its id.
   * 
   * @param id
   *    The id of the source element for which a list of available elements should be created.
   * @param connected
   *    The list of already connected elements. These elements never occur in the list
   *    of available elements.
   * @return
   *    List of available elements.
   */
  protected abstract List<T> getAvailableElements(Integer id, List<T> connected);

  /**
   * @return null, if the Comparable implementation of the objects should be used or
   *         an instance of Comparator which is to be used for sorting.
   */
  protected Comparator<T> comparatorForSorting() {
    return null;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void sortElements(List<T> elements) {
    if (elements.isEmpty()) {
      return;
    }

    Comparator<T> comparator = comparatorForSorting();
    if (comparator == null) {
      if (BuildingBlock.class.isAssignableFrom(elements.get(0).getClass())) {
        BuildingBlockSortHelper.sortByDefault((List) elements);
      }
      else {
        Collections.sort(elements);
      }
    }
    else {
      Collections.sort(elements, comparator);
    }
  }

  public void initializeFrom(F source) {
    this.managedElement = source;
    this.connectedElements = Lists.newArrayList(getConnectedElements(source));

    sortElements(connectedElements);

    if (getComponentMode() != ComponentMode.READ) {
      this.availableElements = getAvailableElements(source.getId(), this.connectedElements);

      // if there are any elements available at all, sort them
      if (availableElements.size() > 0) {
        sortElements(availableElements);
      }
    }
  }

  public void update() {
    processElementIdToAdd();
    elementIdToAdd = null;

    processElementIdToRemove();
    elementIdToRemove = null;

    processElementIdsToAdd();
    elementIdsToAdd = new Integer[0];

    processElementIdsToRemove();
    elementIdsToRemove = new Integer[0];

    if (availableElements.size() > 0) {
      sortElements(availableElements);
    }

    if (connectedElements.size() > 0) {
      sortElements(connectedElements);
    }
  }

  protected void processElementIdToRemove() {
    if (elementIdToRemove == null || elementIdToRemove.intValue() <= 0) {
      return;
    }

    for (Iterator<T> it = connectedElements.iterator(); it.hasNext();) {
      T connected = it.next();
      Integer connectedId = connected.getId();
      if (elementIdToRemove.equals(connectedId)) {
        LOGGER.debug("Removing element with ID {0}", elementIdToRemove);

        it.remove();
        availableElements.add(connected);
        break;
      }
    }
  }

  protected void processElementIdToAdd() {
    if (elementIdToAdd == null || elementIdToAdd.intValue() <= 0) {
      return;
    }

    for (Iterator<T> it = availableElements.iterator(); it.hasNext();) {
      T available = it.next();
      Integer availableId = available.getId();
      if (elementIdToAdd.equals(availableId)) {
        LOGGER.debug("Adding element with ID {0}", elementIdToAdd);

        it.remove();
        connectedElements.add(available);
        break;
      }
    }
  }

  protected void processElementIdsToRemove() {
    if (elementIdsToRemove == null || elementIdsToRemove.length == 0) {
      return;
    }

    for (Iterator<T> it = connectedElements.iterator(); it.hasNext();) {
      T connected = it.next();
      Integer connectedId = connected.getId();
      for (Integer id : this.elementIdsToRemove) {
        if (id.equals(connectedId)) {
          LOGGER.debug("Removing element with ID {0}", id);

          it.remove();
          availableElements.add(connected);
          break;
        }
      }
    }
  }

  protected void processElementIdsToAdd() {
    if (elementIdsToAdd == null || elementIdsToAdd.length == 0) {
      return;
    }

    for (Iterator<T> it = availableElements.iterator(); it.hasNext();) {
      T available = it.next();
      Integer availableId = available.getId();
      for (Integer id : this.elementIdsToAdd) {
        if (id.equals(availableId)) {
          LOGGER.debug("Adding element with ID {0}", id);

          it.remove();
          connectedElements.add(available);
          break;
        }
      }
    }
  }

  public void configure(F target) {
    setConnectedElements(target, new HashSet<T>(connectedElements));
  }

  public void validate(Errors errors) {
    // do nothing
  }

  public List<T> getConnectedElements() {
    return connectedElements;
  }

  protected List<T> getAvailableElements() {
    return availableElements;
  }

  public List<T> getAvailableElementsPresentation() {
    List<T> availableElementsPresentation = new ArrayList<T>();
    availableElementsPresentation.add(dummyForPresentation);
    availableElementsPresentation.addAll(getAvailableElements());
    return availableElementsPresentation;
  }

  public Integer[] getElementIdsToAdd() {
    return copy(elementIdsToAdd);
  }

  public void setElementIdsToAdd(Integer[] elementIdsToAdd) {
    this.elementIdsToAdd = copy(elementIdsToAdd);
  }

  public Integer[] getElementIdsToRemove() {
    return copy(elementIdsToRemove);
  }

  public void setElementIdsToRemove(Integer[] elementIdsToRemove) {
    this.elementIdsToRemove = copy(elementIdsToRemove);
  }

  public Integer getElementIdToAdd() {
    return elementIdToAdd;
  }

  public void setElementIdToAdd(Integer elementIdToAdd) {
    this.elementIdToAdd = elementIdToAdd;
  }

  public Integer getElementIdToRemove() {
    return elementIdToRemove;
  }

  public void setElementIdToRemove(Integer elementIdToRemove) {
    this.elementIdToRemove = elementIdToRemove;
  }

  public String getAvailableElementsLabel() {
    return availableElementsLabel;
  }

  public String[] getColumnHeaderKeys() {
    if (columnHeaderKeys == null) {
      return null;
    }
    else { // copy
      return columnHeaderKeys.clone();
    }
  }

  public String[] getConnectedElementsFields() {
    if (connectedElementsFields == null) {
      return null;
    }
    else { // copy
      return connectedElementsFields.clone();
    }
  }

  public String getTableHeaderKey() {
    return tableHeaderKey;
  }

  public Boolean[] getLookupLablesMode() {
    if (lookupLablesMode == null) {
      return null;
    }
    else { // copy
      return lookupLablesMode.clone();
    }
  }

  public String[] getAvailableElementsPresentationGroupKeys() {
    if (availableElementsPresentationGroupKeys == null) {
      return null;
    }
    else { // copy
      return availableElementsPresentationGroupKeys.clone();
    }
  }

  public Boolean getLookupAvailableLablesMode() {
    return lookupAvailableLablesMode;
  }

  protected F getManagedElement() {
    return managedElement;
  }

  protected void setAvailableElements(List<T> availableElements) {
    this.availableElements = availableElements;
  }

  public ManyAssociationSetComponentModel<F, T> build() {
    return this;
  }
  
  public boolean isDynamicallyLoaded(){
    return false;
  }

  @SuppressWarnings("unchecked")
  private static <T> T[] copy(T[] original) {
    T[] copy = (T[]) Array.newInstance(original.getClass().getComponentType(), original.length);
    System.arraycopy(original, 0, copy, 0, original.length);
    return copy;
  }

}
