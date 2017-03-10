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
package de.iteratec.iteraplan.presentation.dialog.common.model.timeseries;

import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.util.TimeseriesHelper;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model for Timeseries EnumAT attribute values.
 */
public class TimeseriesEnumSingleValueComponentModel extends TimeseriesAttributeValueComponentModel {
  private static final long serialVersionUID       = -8473787675453833990L;

  private String            attributeValueAsString = "";
  private List<String>      availableValuesAsStrings;

  protected TimeseriesEnumSingleValueComponentModel(EnumAT at, ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
    initAvailableValues(at);
  }

  private void initAvailableValues(EnumAT at) {
    List<EnumAV> enumAVs = at.getSortedAttributeValues();
    availableValuesAsStrings = Lists.newArrayList(Lists.transform(enumAVs, TimeseriesHelper.AV_TO_NAME_FUNCTION));
    availableValuesAsStrings.add(0, "");
  }

  /**
   * @return The attribute value as String for presentation
   */
  @Override
  public String getAttributeValueAsString() {
    return attributeValueAsString;
  }

  @Override
  public void setAttributeValueAsString(String value) {
    this.attributeValueAsString = value;
  }

  public List<String> getAvailableValuesAsStrings() {
    return availableValuesAsStrings;
  }

  @Override
  public boolean check() {
    if (isEmpty()) {
      error(IteraplanErrorMessages.TIMESERIES_ENTRY_EMPTY_VALUE);
    }
    return getErrorMessages().isEmpty();
  }

  @Override
  String getNormalizedValue() {
    return getAttributeValueAsString();
  }
}