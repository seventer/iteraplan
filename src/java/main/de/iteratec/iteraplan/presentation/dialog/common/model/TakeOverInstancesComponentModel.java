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
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * ComponentModel for copying associations on an instance-by-instance level.
 * <p>
 * T is the target and source type, U is the type of the referenced objects.
 */
public abstract class TakeOverInstancesComponentModel<T extends BuildingBlock, U extends BuildingBlock> extends AbstractComponentModelBase<T> {

  private static final long    serialVersionUID   = -4037705366798631272L;

  private static List<Integer> exclude            = new ArrayList<Integer>(1);

  static {
    exclude.add(Integer.valueOf(-1));
  }

  private T                    sourceInstance;

  private List<U>              references         = null;

  private Integer[]            selectedReferences = new Integer[] {};

  /** The key of a property to use as label for the boolean choice. */
  private final String         labelKey;

  private final boolean        initiallySelected;

  private final String[]       columnHeaderKeys;

  private final String[]       connectedElementsFields;

  /**
   * Constructor.
   * 
   * @param componentMode
   *          The component mode for this component.
   * @param htmlId
   *          The HTML ID to be used by the JSP for this component.
   * @param labelKey
   *          The I18N key to be used by the JSP should as a label for this component.
   * @param columnHeaderKeys
   *          The I18N keys to be used by the JSP as the headings of the columns of connected
   *          elements. The number of keys corresponds to the number of columns.
   * @param connectedElementsFields
   *          The fields of the connected elements that are to be displayed by the JSP. The fields
   *          correspond to the parameter {@code #columnHeaderKeys}. For example: If the first
   *          header key is 'global.name', the first field could be 'name'.
   * @param initiallySelected
   *          If true, all managed elements will be initially selected when the component is
   *          rendered for the first time. After that, the selection may be changed.
   */
  protected TakeOverInstancesComponentModel(ComponentMode componentMode, String htmlId, String labelKey, String[] columnHeaderKeys,
      String[] connectedElementsFields, boolean initiallySelected) {

    super(componentMode, htmlId);
    this.labelKey = labelKey;
    this.columnHeaderKeys = (columnHeaderKeys == null) ? null : columnHeaderKeys.clone();
    this.connectedElementsFields = (connectedElementsFields == null) ? null : connectedElementsFields.clone();
    this.initiallySelected = initiallySelected;
  }

  protected abstract List<U> getReferencedObjects(T source);

  protected abstract void getSelectedObjects(T target, List<U> referencedInstance);

  @SuppressWarnings("unchecked")
  public void initializeFrom(T source) {

    sourceInstance = source;
    references = getReferencedObjects(source);
    selectedReferences = new Integer[references.size()];

    for (int i = 0; i < references.size(); i++) {
      if (initiallySelected) {
        selectedReferences[i] = references.get(i).getId();
      }
      else {
        selectedReferences[i] = Integer.valueOf(-1);
      }
    }

    // To avoid LazyInitExceptions, these Values have to be reloaded eagerly
    if (references.size() > 0 && references.get(0) instanceof InformationSystemInterface) {
      List<InformationSystemInterface> fresh = new ArrayList<InformationSystemInterface>();
      for (InformationSystemInterface i : (List<InformationSystemInterface>) references) {
        fresh.add(SpringServiceFactory.getInformationSystemInterfaceService().loadObjectById(i.getId(),
            new String[] { "transports", "technicalComponentReleases" }));
      }
      references = (List<U>) fresh;
    }
  }

  public void update() {
    // Nothing to do.
  }

  public void configure(T target) {

    List<Integer> selected = getSelectedReferencesFiltered();
    List<U> list = new ArrayList<U>(selected.size());

    for (U entry : references) {
      if (selected.contains(entry.getId())) {
        list.add(entry);
      }
    }

    getSelectedObjects(target, list);
  }

  public String getLabelKey() {
    return labelKey;
  }

  public List<U> getReferences() {
    return references;
  }

  public Integer[] getSelectedReferences() {
    return selectedReferences.clone();
  }

  /**
   * Delete the element with value -1, because it is only used to call the getter/setter during
   * every request.
   * 
   * @return Ids from selected elements
   */
  private List<Integer> getSelectedReferencesFiltered() {
    List<Integer> result = new ArrayList<Integer>(Arrays.asList(selectedReferences));
    result.removeAll(exclude);
    return result;
  }

  public void setSelectedReferences(Integer[] selectedReferencesArray) {

    selectedReferences = selectedReferencesArray == null ? null : selectedReferencesArray.clone();
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

  public T getSourceInstance() {
    return sourceInstance;
  }

  public void validate(Errors errors) {
    // nothing to do (yet)
  }
}
