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
import java.util.Iterator;
import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.RangeValue;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.IteraplanValidationUtils;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;


/**
 * Component Model for managing the range list for number attribute types. It defines possible
 * actions for the component model. The actions are read from the action property and evaluated in
 * the update method.
 */
public class RangeValuesComponentModel extends AbstractComponentModelBase<NumberAT> {

  /** Serialization version. */
  private static final long      serialVersionUID    = 1457250351570858788L;

  private static final Logger    LOGGER              = Logger.getIteraplanLogger(RangeValuesComponentModel.class);

  private static final String    ACTION_NEW          = "new";
  private static final String    ACTION_DELETE       = "delete";

  private static final int       MAX_RANGE_VALUE     = Constants.MAX_RANGELIST_SIZE;

  /** List of component model parts. Each part manages a single enum attribute value. */
  private final List<RangeValueComponentModel> rangeValueParts         = new ArrayList<RangeValueComponentModel>();

  private List<String>           rangeValuesAsString = new ArrayList<String>();

  /** Name string of a new enum attribute value that should be added. */
  private String                 valueToAdd;

  /** The GUI action to carry out within update(). */
  private String                 action;

  /**
   * The position of the selected list element. The first element has position 1, the second 2, ...
   * This unusual index start is necessary as the web framework converts a null/empty value for an
   * Integer into the value 0. The index must therefore be shifted.
   */
  private Integer                selectedPosition;

  public RangeValuesComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
  }

  public void initializeFrom(NumberAT source) {
    List<RangeValue> connectedElements = new ArrayList<RangeValue>(source.getRangeValues());
    List<String> connectedElementsAsString = source.getRangeValuesAsString(UserContext.getCurrentLocale());
    Collections.sort(connectedElements);
    for (RangeValue connectedElement : connectedElements) {
      RangeValueComponentModel model = new RangeValueComponentModel(getComponentMode());
      model.initializeFrom(connectedElement);
      rangeValueParts.add(model);
    }
    this.rangeValuesAsString = connectedElementsAsString;
  }

  public void update() {
    if (selectedPosition != null && selectedPosition.intValue() > 0) {
      int index = selectedPosition.intValue() - 1;
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Action '" + getAction() + "' was called for EnumAV on position " + selectedPosition.intValue());
      }
      if (ACTION_DELETE.equals(getAction())) {
        rangeValueParts.remove(index);
      }
    }
    // If the action string is empty, it is assumed that the save button was pressed. In that case,
    // an attribute that was entered in the textfield but not added via the appropriate icon
    // should be saved as well. This behavior is consistent with the behavior in other parts of
    // iteraplan, for example when selecting items from a drop down and pressing 'save' instead of
    // adding the element. Note that an attribute must NOT be saved if no name was entered.
    else if ((ACTION_NEW.equals(getAction()) || "".equals(getAction())) && !("".equals(valueToAdd)) && (valueToAdd != null)) {
      processNotNull();

      // throw an exception if the name already exists in the component model
      if (valueAlreadyExists()) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.ATTRVAL_EXISTS);
      }
      RangeValue range = new RangeValue();
      range.setValue(BigDecimalConverter.parse(valueToAdd, UserContext.getCurrentLocale()));
      
      RangeValueComponentModel rangeValueModel = new RangeValueComponentModel(getComponentMode());
      rangeValueModel.initializeFrom(range);
      rangeValueParts.add(rangeValueModel);

      // reset input fields after update
      valueToAdd = null;
    }

    // reset input fields after update
    action = null;
    selectedPosition = null;
  }

  private void processNotNull() {
    for (Iterator<RangeValueComponentModel> it = rangeValueParts.iterator(); it.hasNext(); ) {
      RangeValueComponentModel range = it.next();
      range.update();
      if (range.rangeValue == null) {
        it.remove();
      }
    }
  }

  /**
   * Checks if an EnumAV name the user tries to add already exists
   * 
   * @return <code>true</code> if the name already exists
   */
  private boolean valueAlreadyExists() {
    for (RangeValueComponentModel valuePart : rangeValueParts) {
      if (valuePart.rangeValue.getValue().compareTo(BigDecimalConverter.parse(valueToAdd, UserContext.getCurrentLocale())) == 0) {
        return true;
      }
    }
    return false;
  }

  public void configure(NumberAT target) {
    if (getComponentMode() == ComponentMode.CREATE) {
      for (RangeValueComponentModel rangeValue : rangeValueParts) {
        RangeValue copy = rangeValue.rangeValue.getCopy();
        target.addRangeValueTwoWay(copy);
      }
      
      return;
    }
    
    List<RangeValue> rangeValues = Lists.newArrayList();
    for (RangeValueComponentModel range : rangeValueParts){
      rangeValues.add(range.rangeValue);
    }
    
    List<RangeValue> reloadedRangeValues = SpringServiceFactory.getAttributeTypeService().reloadRangeValues(rangeValues);
    List<RangeValue> existingRangeValues = getExistingRangeValues(target, reloadedRangeValues);
    SetView<RangeValue> removedValues = Sets.difference(Sets.newHashSet(target.getRangeValues()), Sets.newHashSet(existingRangeValues));
    for (RangeValue rangeValue : removedValues) {
      target.removeRangeValueTwoWays(rangeValue.getId());
    }
    target.getRangeValues().removeAll(removedValues);
    
    rangeValues = Lists.newArrayList();
    for (RangeValueComponentModel range : rangeValueParts){
      rangeValues.add(range.rangeValue);
    }
    
    target.addRangeValuesTwoWay(rangeValues);
  }
  
  private List<RangeValue> getExistingRangeValues(NumberAT target, List<RangeValue> reloadEntities) {
    final Iterable<RangeValue> existingValues = Iterables.filter(target.getRangeValues(), Predicates.in(reloadEntities));
    return Lists.newArrayList(existingValues);
  }

  public void validate(Errors errors) {
    Object[] params = IteraplanValidationUtils.getLocalizedArgsWithSpanTags("global.name", "errorInline");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "valueToAdd", "errors.required", params);
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public List<RangeValueComponentModel> getRangeValues() {
    return rangeValueParts;
  }

  public String getValueToAdd() {
    return valueToAdd;
  }

  public void setValueToAdd(String valueToAdd) {
    this.valueToAdd = valueToAdd;
  }

  public Integer getSelectedPosition() {
    return selectedPosition;
  }

  public void setSelectedPosition(Integer selectedPosition) {
    this.selectedPosition = selectedPosition;
  }

  public int getMaxRangeValue() {
    return MAX_RANGE_VALUE;
  }

  public List<String> getRangeValuesAsString() {
    return rangeValuesAsString;
  }
  
  static class RangeValueComponentModel extends AbstractComponentModelBase<RangeValue> {
    
    private static final long serialVersionUID = -1447480716213868237L;

    protected RangeValueComponentModel(ComponentMode componentMode) {
      super(componentMode);
    }

    private RangeValue rangeValue;
    
    private String valueAsString;

    /* (non-Javadoc)
     * @see de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel#initializeFrom(java.lang.Object)
     */
    public void initializeFrom(RangeValue source) {
      this.rangeValue = source;
      this.valueAsString = BigDecimalConverter.format(source.getValue(), true, UserContext.getCurrentLocale());
    }

    /* (non-Javadoc)
     * @see de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel#update()
     */
    public void update() {
      if (valueAsString != null && valueAsString.trim().length() > 0) {
        rangeValue.setValue(BigDecimalConverter.parse(valueAsString, true, UserContext.getCurrentLocale()));
      }
      else {
        rangeValue = null;
      }
      if (rangeValue != null) {
        valueAsString = BigDecimalConverter.format(rangeValue.getValue(), true, UserContext.getCurrentLocale());
      }
      else {
        valueAsString = "";
      }
    }

    /* (non-Javadoc)
     * @see de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel#configure(java.lang.Object)
     */
    public void configure(RangeValue target) {
      if (valueAsString != null && valueAsString.trim().length() > 0) {
        target.setValue(BigDecimalConverter.parse(valueAsString, true, UserContext.getCurrentLocale()));
      }
    }

    /* (non-Javadoc)
     * @see de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel#validate(org.springframework.validation.Errors)
     */
    public void validate(Errors errors) {
      // Nothing to be done
    }

    public void setValueAsString(String valueAsString) {
      this.valueAsString = valueAsString;
    }

    public String getValueAsString() {
      return valueAsString;
    }
    
  }
}
