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

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.sorting.BuildingBlockSortHelper;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * This component manages a N:1 relation. By default it is configured such that the 1-side may be
 * null. If this is not true for your case, set the {@link ManyToOneComponentModel#nullable}
 * flag through the constructor in order to change that behavior. The type parameter F represents the
 * N side of the relation, while the type parameter T represents the 1 side of the relation. The
 * latter is managed by this component. 
 */
public abstract class ManyToOneComponentModel<F extends IdEntity, T extends IdEntity & Comparable<? super T>> extends AbstractComponentModelBase<F> {

  private static final long serialVersionUID   = 2139226074311724719L;
  /** This configuration flag indicates whether the 1-side may be null. */
  private final boolean     nullable;
  /** The key of the label text to show. */
  private final String      labelKey;
  /** The dummy object to show for nullable N:1-associations. */
  private final T           dummyForPresentation;
  private T                 connectedElement;
  private Integer           connectedElementId = null;
  private List<T>           availableElements  = new ArrayList<T>();

  /**
   * Constructor.
   * 
   * @param componentMode
   *          The component mode.
   * @param htmlId
   *          The HTML ID. May be null.
   * @param labelKey
   *          The I18N key the will be used by the JSP for labeling the connected element.
   * @param nullable
   *          If true, an empty element will be shown in the list of available elements. Otherwise,
   *          no empty element will be shown.
   */
  @SuppressWarnings("unchecked")
  public ManyToOneComponentModel(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
    super(componentMode, htmlId);
    this.labelKey = labelKey;
    this.nullable = nullable;
    if (this.nullable) {
      ParameterizedType t = (ParameterizedType) getClass().getGenericSuperclass();
      Class<T> c = (Class<T>) t.getActualTypeArguments()[1];
      try {
        this.dummyForPresentation = c.newInstance();
      } catch (InstantiationException e) {
        throw new IllegalArgumentException("Could not instantiate dummy of class " + c + " for presentation.", e);
      } catch (IllegalAccessException e) {
        throw new IllegalArgumentException("Could not instantiate dummy of class " + c + " for presentation.", e);
      }
    }
    else {
      this.dummyForPresentation = null;
    }
  }

  /**
   * Returns the element that is connected to the managed element.
   * 
   * @param source
   *          The managed element.
   * @return See method description.
   */
  protected abstract T getConnectedElement(F source);

  /**
   * Return a list of all elements that can be connected to the managed element.
   * 
   * @param id
   *          The ID of the currently managed element. Only relevant for parent/child relations. Set
   *          to null if not applicable.
   * @return See method description.
   */
  protected abstract List<T> getAvailableElements(Integer id);
  
  public abstract TypeOfBuildingBlock getTypeOfBuildingBlock();

  /**
   * Set the connected element for the managed element.
   * 
   * @param target
   *          The managed element.
   * @param element
   *          The element that is to be connected to the managed element.
   */
  protected abstract void setConnectedElement(F target, T element);

  public void initializeFrom(F source) {
    this.connectedElement = getConnectedElement(source);
    if (connectedElement != null) {
      this.connectedElementId = connectedElement.getId();
    }
    if (getComponentMode() != ComponentMode.READ) {
      this.availableElements = getAvailableElements(source.getId());
      sortElements(this.availableElements);
    }
  }

  public void update() {
    if (connectedElementId != null && connectedElementId.intValue() > 0) {
      for (T element : availableElements) {
        Integer elementId = element.getId();
        if (connectedElementId.equals(elementId)) {
          connectedElement = element;
          break;
        }
      }
    }
    else {
      connectedElement = null;
    }
  }

  public void configure(F target) {
    setConnectedElement(target, connectedElement);
  }

  public void validate(Errors errors) {
    if (!this.nullable && connectedElement == null) {
      errors.rejectValue("connectedElement", "errors.required");
    }
  }

  public Integer getConnectedElementId() {
    return connectedElementId;
  }

  public void setConnectedElementId(Integer referenceId) {
    this.connectedElementId = referenceId;
  }

  public List<T> getAvailableElements() {
    return availableElements;
  }

  public Object getConnectedElement() {
    return connectedElement;
  }

  protected void setConnectedElement(T connectedElement) {
    this.connectedElement = connectedElement;
  }
  
  public boolean isDynamicallyLoaded(){
    return false;
  }

  // TODO this method should become obsolete when elements are selected differently.
  // it adds the connectedElement to the list if it is not contained and adds an
  // empty element at the beginning of the list, if this association is nullable.
  /**
   * @return The list of available elements. The currently connected element is always contained in
   *         the list, even if it was filtered out. If the association is nullable, an empty element
   *         is added to the list.
   */
  public List<T> getAvailableElementsPresentation() {
    List<T> presentationList = new ArrayList<T>(availableElements);
    if (connectedElementId != null && connectedElementId.intValue() > 0) {
      boolean addConnectedToList = true;
      for (T element : presentationList) {
        Integer elementId = element.getId();
        if (connectedElementId.equals(elementId)) {
          addConnectedToList = false;
          break;
        }
      }
      if (addConnectedToList) {
        presentationList.add(connectedElement);
        sortElements(presentationList);
      }
    }
    if (nullable) {
      presentationList.add(0, dummyForPresentation);
    }
    return presentationList;
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

  public String getLabelKey() {
    return labelKey;
  }

  public boolean isNullable() {
    return nullable;
  }

}
