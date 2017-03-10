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
import java.util.Collection;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.BooleanComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;


public class ResponsibilityAttributeTypeComponentModel extends AbstractComponentModelBase<AttributeType> {

  /** Serialization version. */
  private static final long                                  serialVersionUID          = 5431249110244105488L;

  private static final String                                MULTIASSIGNMENTTYPE_LABEL = "manageAttributes.responsibilityAT.multiassignmenttype";

  private final Collection<ComponentModel<ResponsibilityAT>> attributeModels           = new ArrayList<ComponentModel<ResponsibilityAT>>();

  private BooleanComponentModel<ResponsibilityAT>            multiAssignmentTypeModel;

  private ResponsibilityAttributeValuesComponentModel        responsibilityAttributeValuesModel;

  public ResponsibilityAttributeTypeComponentModel(ComponentMode componentMode) {
    super(componentMode);
    attributeModels.add(getMultiAssignmentTypeModel());
    attributeModels.add(getResponsibilityAttributeValuesModel());
  }

  public void configure(AttributeType attributeType) throws IteraplanException {
    Preconditions.checkArgument(attributeType instanceof ResponsibilityAT);
    ResponsibilityAT responsibilityAT = (ResponsibilityAT) attributeType;
    for (ComponentModel<ResponsibilityAT> model : attributeModels) {
      model.configure(responsibilityAT);
    }
  }

  public void initializeFrom(AttributeType attributeType) throws IteraplanException {
    Preconditions.checkArgument(attributeType instanceof ResponsibilityAT);
    ResponsibilityAT responsibilityAT = (ResponsibilityAT) attributeType;
    for (ComponentModel<ResponsibilityAT> model : attributeModels) {
      model.initializeFrom(responsibilityAT);
    }
  }

  public void update() throws IteraplanException {
    if (getComponentMode() != ComponentMode.READ) {
      for (ComponentModel<ResponsibilityAT> model : attributeModels) {
        model.update();
      }
    }
  }

  final BooleanComponentModel<ResponsibilityAT> getMultiAssignmentTypeModel() {
    if (multiAssignmentTypeModel == null) {
      multiAssignmentTypeModel = new MultiAssignmentTypeBooleanComponentModel(getComponentMode(), "multiassignmenttype", MULTIASSIGNMENTTYPE_LABEL);
    }
    return multiAssignmentTypeModel;
  }

  final ResponsibilityAttributeValuesComponentModel getResponsibilityAttributeValuesModel() {
    if (responsibilityAttributeValuesModel == null) {
      responsibilityAttributeValuesModel = new ResponsibilityAttributeValuesComponentModel(getComponentMode(), "responsibilityAttributeValues");
    }
    return responsibilityAttributeValuesModel;
  }

  public void validate(Errors errors) {
    // Nothing to do
  }

  private static final class MultiAssignmentTypeBooleanComponentModel extends BooleanComponentModel<ResponsibilityAT> {

    /** Serialization version. */
    private static final long serialVersionUID = -1009420692417277666L;

    public MultiAssignmentTypeBooleanComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public Boolean getBooleanFromElement(ResponsibilityAT source) {
      return Boolean.valueOf(source.isMultiassignmenttype());
    }

    @Override
    public void setBooleanForElement(ResponsibilityAT target, Boolean booleanToSet) {
      target.setMultiassignmenttype(booleanToSet.booleanValue());
    }
  }

}
