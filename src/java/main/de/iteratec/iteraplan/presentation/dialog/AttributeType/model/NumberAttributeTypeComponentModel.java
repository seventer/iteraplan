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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.BigDecimalComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.BooleanComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.StringComponentModel;


public class NumberAttributeTypeComponentModel extends AbstractComponentModelBase<AttributeType> implements TimeseriesTypeComponentModel {

  /** Serialization version. */
  private static final long                          serialVersionUID    = 1119001410891962420L;
  private static final String                        LOWER_BOUND_LABEL   = "manageAttributes.numberAT.lowerbound";
  private static final String                        UPPER_BOUND_LABEL   = "manageAttributes.numberAT.upperbound";
  private static final String                        UNIFORM_RANGE_LABEL = "manageAttributes.numberAT.uniformrange";
  private static final String                        UNIT_LABEL          = "manageAttributes.numberAT.unit";

  private BigDecimalComponentModel<NumberAT>         lowerBoundModel;
  private BooleanComponentModel<NumberAT>            rangeUniformyDistributedModel;
  private RangeValuesComponentModel                  rangesModel;
  private BigDecimalComponentModel<NumberAT>         upperBoundModel;
  private StringComponentModel<NumberAT>             unitModel;
  private BooleanComponentModel<TimeseriesType>      timeseriesModel;

  private final Collection<ComponentModel<NumberAT>> attributeModels     = new ArrayList<ComponentModel<NumberAT>>();

  public NumberAttributeTypeComponentModel(ComponentMode componentMode) {
    super(componentMode);
    attributeModels.add(getLowerBoundModel());
    attributeModels.add(getUpperBoundModel());
    attributeModels.add(getUnitModel());
    attributeModels.add(getRangeUniformyDistributedModel());
    attributeModels.add(getRangesModel());
  }

  public void configure(AttributeType attributeType) {
    Preconditions.checkArgument(attributeType instanceof NumberAT);
    NumberAT numberAT = (NumberAT) attributeType;
    for (ComponentModel<NumberAT> model : attributeModels) {
      model.configure(numberAT);
    }
    getTimeseriesModel().configure(numberAT);
  }

  public void initializeFrom(AttributeType attributeType) {
    Preconditions.checkArgument(attributeType instanceof NumberAT);
    NumberAT numberAT = (NumberAT) attributeType;
    for (ComponentModel<NumberAT> model : attributeModels) {
      model.initializeFrom(numberAT);
    }
    getTimeseriesModel().initializeFrom(numberAT);
  }

  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      for (ComponentModel<NumberAT> model : attributeModels) {
        model.update();
      }
    }
  }

  public void validate(Errors errors) {
    errors.pushNestedPath("lowerBoundModel");
    lowerBoundModel.validate(errors);
    errors.popNestedPath();

    errors.pushNestedPath("upperBoundModel");
    upperBoundModel.validate(errors);
    errors.popNestedPath();
  }

  final BigDecimalComponentModel<NumberAT> getLowerBoundModel() {
    if (lowerBoundModel == null) {
      lowerBoundModel = new LowerBoundBigDecimalComponentModel(getComponentMode(), "lowerBound", LOWER_BOUND_LABEL);
    }
    return lowerBoundModel;
  }

  final BigDecimalComponentModel<NumberAT> getUpperBoundModel() {
    if (upperBoundModel == null) {
      upperBoundModel = new UpperBoundBigDecimalComponentModel(getComponentMode(), "upperBound", UPPER_BOUND_LABEL);
    }
    return upperBoundModel;
  }

  final StringComponentModel<NumberAT> getUnitModel() {
    if (unitModel == null) {
      unitModel = new UnitStringComponentModel(getComponentMode(), "unit", UNIT_LABEL);
    }
    return unitModel;
  }

  final BooleanComponentModel<NumberAT> getRangeUniformyDistributedModel() {
    if (rangeUniformyDistributedModel == null) {
      rangeUniformyDistributedModel = new RangeUniformyDistributedBooleanComponentModel(getComponentMode(), "rangeUniformyDistributed",
          UNIFORM_RANGE_LABEL);
    }
    return rangeUniformyDistributedModel;
  }

  final RangeValuesComponentModel getRangesModel() {
    if (rangesModel == null) {
      rangesModel = new RangeValuesComponentModel(getComponentMode(), "rangeValues");
    }
    return rangesModel;
  }

  public BooleanComponentModel<TimeseriesType> getTimeseriesModel() {
    if (timeseriesModel == null) {
      // timeseries flag is only editable for newly created attribute types
      ComponentMode componentMode = getComponentMode() != ComponentMode.CREATE ? ComponentMode.READ : getComponentMode();
      timeseriesModel = new TimeseriesBooleanComponentModel(componentMode, "timeseries", TimeseriesBooleanComponentModel.TIMESERIES_LABEL);
    }
    return timeseriesModel;
  }

  private static final class LowerBoundBigDecimalComponentModel extends BigDecimalComponentModel<NumberAT> {

    /** Serialization version. */
    private static final long serialVersionUID = 8398869401186411528L;

    public LowerBoundBigDecimalComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    protected BigDecimal getBigDecimalFromElement(NumberAT source) {
      return source.getMinValue();
    }

    @Override
    protected void setBigDecimalForElement(NumberAT target, BigDecimal value) {
      target.setMinValue(value);
    }
  }

  private static final class UpperBoundBigDecimalComponentModel extends BigDecimalComponentModel<NumberAT> {

    /** Serialization version. */
    private static final long serialVersionUID = 1187774878974997542L;

    public UpperBoundBigDecimalComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    protected BigDecimal getBigDecimalFromElement(NumberAT source) {
      return source.getMaxValue();
    }

    @Override
    protected void setBigDecimalForElement(NumberAT target, BigDecimal value) {
      target.setMaxValue(value);
    }
  }

  private static final class UnitStringComponentModel extends StringComponentModel<NumberAT> {

    /** Serialization version. */
    private static final long serialVersionUID = -7592436631826117467L;

    public UnitStringComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public String getStringFromElement(NumberAT element) {
      return element.getUnit();
    }

    @Override
    public void setStringForElement(NumberAT element, String stringToSet) {
      element.setUnit(stringToSet);
    }
  }

  private static final class RangeUniformyDistributedBooleanComponentModel extends BooleanComponentModel<NumberAT> {

    /** Serialization version. */
    private static final long serialVersionUID = 5259493299438848923L;

    public RangeUniformyDistributedBooleanComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public Boolean getBooleanFromElement(NumberAT source) {
      return Boolean.valueOf(source.isRangeUniformyDistributed());
    }

    @Override
    public void setBooleanForElement(NumberAT target, Boolean booleanToSet) {
      boolean value = org.apache.commons.lang.BooleanUtils.toBoolean(booleanToSet);
      target.setRangeUniformyDistributed(value);
    }
  }

}
