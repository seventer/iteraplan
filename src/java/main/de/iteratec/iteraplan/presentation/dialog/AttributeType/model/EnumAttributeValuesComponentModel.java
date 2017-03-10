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
package de.iteratec.iteraplan.presentation.dialog.AttributeType.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.IteraplanValidationUtils;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;


/**
 * Component Model for managing the value list for enum attribute types. It defines possible actions
 * for the component model. The actions are read from the action property and evaluated in the
 * update method.
 */
public class EnumAttributeValuesComponentModel extends AbstractComponentModelBase<EnumAT> {

  /** Serialization version. */
  private static final long                                serialVersionUID    = 1948073160907598925L;
  public static final String                               SORTORDER_NONE      = "none";
  public static final String                               SORTORDER_ASC       = "asc";
  public static final String                               SORTORDER_DESC      = "desc";

  private static final Logger                              LOGGER              = Logger.getIteraplanLogger(EnumAttributeValuesComponentModel.class);

  private static final String                              ACTION_NEW          = "new";
  private static final String                              ACTION_COPY         = "copy";
  private static final String                              ACTION_DELETE       = "delete";
  private static final String                              ACTION_MOVE_TOP     = "moveTop";
  private static final String                              ACTION_MOVE_UP      = "moveUp";
  private static final String                              ACTION_MOVE_DOWN    = "moveDown";
  private static final String                              ACTION_MOVE_BOTTOM  = "moveBottom";

  /** List of component model parts. Each part manages a single enum attribute value. */
  private final List<EnumAttributeValueComponentModelPart> valueParts          = new ArrayList<EnumAttributeValueComponentModelPart>();

  /** List of available colors for attributes */
  private List<String>                                     availableColors     = Lists.newArrayList();

  /** Name string of a new enum attribute value that should be added. */
  private String                                           nameToAdd;

  /** Description string of a new enum attribute value that should be added. */
  private String                                           descriptionToAdd;

  /** Description string of a new enum attribute value that should be added. */
  private String                                           colorToAdd;

  private String                                           sortOrder           = SORTORDER_NONE;

  private int                                              availableColorIndex = 0;

  /**
   * The position of the selected list element. The first element has position 1, the second 2, ...
   * This unusual index start is necessary as the web framework converts a null/empty value for an
   * Integer into the value 0. The index must therefore be shifted.
   */
  private Integer                                          selectedPosition;

  /** The GUI action to carry out within update(). */
  private String                                           action;

  public EnumAttributeValuesComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
  }

  public void initializeFrom(EnumAT source) {
    availableColors = SpringGuiFactory.getInstance().getAttributeColors();

    List<EnumAV> connectedElements = source.getSortedAttributeValues();
    for (EnumAV connectedElement : connectedElements) {
      EnumAttributeValueComponentModelPart elementPart = new EnumAttributeValueComponentModelPart();
      elementPart.initializeFrom(connectedElement);
      valueParts.add(elementPart);
    }
  }

  public void update() {
    for (EnumAttributeValueComponentModelPart elementPart : valueParts) {
      elementPart.update();
    }

    // throw an exception if there are duplicate names in the component model (checked case-insensitively)
    checkForExistingDuplicates();

    if (selectedPosition != null && selectedPosition.intValue() > 0) {
      actionOnSelectedPosition();
    }
    // If the action string is empty, it is assumed that the save button was pressed. In that case,
    // an attribute that was entered in the textfield but not added via the appropriate icon
    // should be saved as well. This behaviour is consistent with the behaviour in other parts of
    // iteraplan, for example when selecting items from a drop down and pressing 'save' instead of
    // adding the element. Note that an attribute must NOT be saved if no name was entered.
    else if ((ACTION_NEW.equals(action) || "".equals(action)) && !("".equals(nameToAdd))) {
      // throw an exception if the new name already exists in the component model
      if (nameAlreadyExists()) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.ATTRVAL_EXISTS);
      }
      EnumAV newEAV = new EnumAV();
      newEAV.setName(nameToAdd);
      newEAV.setDescription(descriptionToAdd);
      newEAV.setDefaultColorHex(colorToAdd);
      EnumAttributeValueComponentModelPart newPart = new EnumAttributeValueComponentModelPart();
      newPart.initializeFrom(newEAV);
      valueParts.add(newPart);
      // shift the index
      availableColorIndex = (availableColorIndex + 1) % availableColors.size();

      // reset input fields after update
      nameToAdd = null;
      descriptionToAdd = null;
      colorToAdd = null;
    }
    else if (!sortOrder.equals(SORTORDER_NONE)) {
      // Depending on the sortOrder a comparator will be created or sorting will be skipped
      sort();
    }

    // reset input fields after update
    action = null;
    selectedPosition = null;
  }

  private void checkForExistingDuplicates() {
    Set<String> uniqueNames = Sets.newHashSet();
    for (EnumAttributeValueComponentModelPart vp : valueParts) {
      if (!uniqueNames.add(vp.getName().toLowerCase())) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.ATTRVAL_EXISTS);
      }
    }
  }

  /**
   * 
   */
  private void actionOnSelectedPosition() {
    int index = selectedPosition.intValue() - 1;
    LOGGER.debug("Action '{0}' was called for EnumAV on position {1}", action, selectedPosition);
    EnumAttributeValueComponentModelPart elementPart;
    elementPart = valueParts.get(index);
    if (ACTION_MOVE_TOP.equals(action)) {
      valueParts.remove(index);
      valueParts.add(0, elementPart);
    }
    else if (ACTION_MOVE_UP.equals(action)) {
      if (index > 0) {
        valueParts.remove(index);
        valueParts.add(index - 1, elementPart);
      }
    }
    else if (ACTION_MOVE_DOWN.equals(action)) {
      if (index < valueParts.size() - 1) {
        valueParts.remove(index);
        valueParts.add(index + 1, elementPart);
      }
    }
    else if (ACTION_MOVE_BOTTOM.equals(action)) {
      valueParts.remove(index);
      valueParts.add(elementPart);
    }
    else if (ACTION_DELETE.equals(action)) {
      valueParts.remove(index);
    }
    else if (ACTION_COPY.equals(action)) {
      EnumAV copy = new EnumAV();
      copy.setName(elementPart.getName() + " *");
      copy.setDescription(elementPart.getDescription());
      copy.setDefaultColorHex(elementPart.getDefaultColor());
      EnumAttributeValueComponentModelPart copyPart = new EnumAttributeValueComponentModelPart();
      copyPart.initializeFrom(copy);
      valueParts.add(index + 1, copyPart);
    }

  }

  /**
   * Checks if an EnumAV name the user tries to add already exists
   * 
   * @return <code>true</code> if the name already exists
   */
  private boolean nameAlreadyExists() {
    for (EnumAttributeValueComponentModelPart valuePart : valueParts) {
      if (valuePart.getName().equalsIgnoreCase(nameToAdd)) {
        return true;
      }
    }
    return false;
  }

  public void configure(EnumAT target) {
    List<EnumAV> actual = getActualAttributeValues(target);
    deleteRemovedAttributeValues(target, actual);

    // Set EnumAV-positions according to ordering in actual list
    int pos = 0;
    for (EnumAV av : actual) {
      av.setPosition(pos++);
    }

    for (EnumAttributeValueComponentModelPart part : valueParts) {
      part.configure(target);
    }
  }

  /**
   * Find out the removed values and delete them. 
   * 
   * @param enumAt the enum attribute type
   * @param actualValues the actually configured attribute values
   */
  private void deleteRemovedAttributeValues(EnumAT enumAt, List<EnumAV> actualValues) {
    SetView<EnumAV> difference = Sets.difference(enumAt.getAttributeValues(), Sets.newHashSet(actualValues));
    AttributeValueService attributeValueService = SpringServiceFactory.getAttributeValueService();
    for (EnumAV enumAV : Sets.newHashSet(difference)) {
      enumAV.getAttributeType().getAttributeValues().remove(enumAV);
      attributeValueService.deleteEntity(enumAV);
    }
  }

  /**
   * Returns the actually configured enum attribute values, preserving the order of values.
   * 
   * @param enumAt the enum attribute type, required for adding new values to
   * @return the actually configured enum attribute values
   */
  private List<EnumAV> getActualAttributeValues(EnumAT enumAt) {
    List<EnumAV> actual = Lists.newArrayList();
    for (EnumAttributeValueComponentModelPart part : valueParts) {
      EnumAV enumAv = part.getEnumAv();
      if (enumAv.getId() != null) {
        actual.add(SpringServiceFactory.getAttributeValueService().reload(enumAv));
      }
      else {
        enumAv.setAttributeTypeTwoWay(enumAt);
        actual.add(enumAv);
      }
    }

    return actual;
  }

  public void sort() {
    Comparator<EnumAttributeValueComponentModelPart> comparator = EnumAttributeValueComponentModelPart.comparatorForSorting(sortOrder);
    if (comparator != null) {
      Collections.sort(valueParts, comparator);
    }
    // Change the sortOrder back to neutral or otherwise it will be always sorted
    sortOrder = SORTORDER_NONE;
  }

  public void sort(String order) {
    this.sortOrder = order;
    sort();
  }

  public void validate(Errors errors) {
    Object[] params = IteraplanValidationUtils.getLocalizedArgsWithSpanTags("global.name", "errorInline");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nameToAdd", "errors.required", params);
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Integer getSelectedPosition() {
    return selectedPosition;
  }

  public void setSelectedPosition(Integer selectedPosition) {
    this.selectedPosition = selectedPosition;
  }

  public List<EnumAttributeValueComponentModelPart> getValueParts() {
    return valueParts;
  }

  public String getNameToAdd() {
    return nameToAdd;
  }

  public void setNameToAdd(String nameToAdd) {
    this.nameToAdd = nameToAdd;
  }

  public String getColorToAdd() {
    return colorToAdd;
  }

  public void setColorToAdd(String colorToAdd) {
    this.colorToAdd = colorToAdd;
  }

  public String getDescriptionToAdd() {
    return descriptionToAdd;
  }

  public void setDescriptionToAdd(String descriptionToAdd) {
    this.descriptionToAdd = descriptionToAdd;
  }

  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }

  public String getSortOrder() {
    return sortOrder;
  }

  public int getAvailableColorIndex() {
    return availableColorIndex;
  }

  public void setAvailableColorIndex(int availableColorIndex) {
    this.availableColorIndex = availableColorIndex;
  }

  public List<String> getAvailableColors() {
    return availableColors;
  }

  public void setAvailableColors(List<String> availableColors) {
    this.availableColors = availableColors;
  }

}
