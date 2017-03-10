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

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.BooleanComponentModel;


public class EnumAttributeTypeComponentModel extends AbstractComponentModelBase<AttributeType> implements TimeseriesTypeComponentModel {

  /** Serialization version. */
  private static final long                     serialVersionUID = -1244495173730977812L;

  protected static final String                 MULTIVALUE_LABEL = "manageAttributes.multiplevalues";

  private BooleanComponentModel<EnumAT>         multiValueModel;
  private BooleanComponentModel<TimeseriesType> timeseriesModel;
  private EnumAttributeValuesComponentModel     enumAttributeValueModel;

  public EnumAttributeTypeComponentModel(ComponentMode componentMode) {
    super(componentMode);
  }

  public void configure(AttributeType attributeType) {
    Preconditions.checkArgument(attributeType instanceof EnumAT);
    EnumAT enumAT = (EnumAT) attributeType;
    getEnumAttributeValuesModel().configure(enumAT);
    getMultivalueModel().configure(enumAT);
    getTimeseriesModel().configure(enumAT);
  }

  public void initializeFrom(AttributeType attributeType) {
    Preconditions.checkArgument(attributeType instanceof EnumAT);
    EnumAT enumAT = (EnumAT) attributeType;

    getEnumAttributeValuesModel().initializeFrom(enumAT);
    getMultivalueModel().initializeFrom(enumAT);
    getTimeseriesModel().initializeFrom(enumAT);
  }

  public void update() {
    if (getComponentMode() != ComponentMode.READ) {
      getEnumAttributeValuesModel().update();
      getMultivalueModel().update();
    }
  }

  public void validate(Errors errors) {
    if (getMultivalueModel().getCurrent().booleanValue() && getTimeseriesModel().getCurrent().booleanValue()) {
      errors.rejectValue("timeseriesModel.current", "ILLEGAL_MULTIVALUE_TIMESERIES");
    }
  }

  public BooleanComponentModel<EnumAT> getMultivalueModel() {
    if (multiValueModel == null) {
      multiValueModel = new MultivalueBooleanComponentModel(getComponentMode(), "multivalue", MULTIVALUE_LABEL);
    }
    return multiValueModel;
  }

  public BooleanComponentModel<TimeseriesType> getTimeseriesModel() {
    if (timeseriesModel == null) {
      // timeseries flag is only editable for newly created attribute types
      ComponentMode componentMode = getComponentMode() != ComponentMode.CREATE ? ComponentMode.READ : getComponentMode();
      timeseriesModel = new TimeseriesBooleanComponentModel(componentMode, "timeseries", TimeseriesBooleanComponentModel.TIMESERIES_LABEL);
    }
    return timeseriesModel;
  }

  public EnumAttributeValuesComponentModel getEnumAttributeValuesModel() {
    if (enumAttributeValueModel == null) {
      enumAttributeValueModel = new EnumAttributeValuesComponentModel(getComponentMode(), "enumAttributeValues");
    }
    return enumAttributeValueModel;
  }

  private static final class MultivalueBooleanComponentModel extends BooleanComponentModel<EnumAT> {
    /** Serialization version. */
    private static final long serialVersionUID = -2950445758204907259L;

    public MultivalueBooleanComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public Boolean getBooleanFromElement(EnumAT source) {
      return Boolean.valueOf(source.isMultiassignmenttype());
    }

    @Override
    public void setBooleanForElement(EnumAT target, Boolean booleanToSet) {
      target.setMultiassignmenttype(booleanToSet.booleanValue());
    }
  }

}
