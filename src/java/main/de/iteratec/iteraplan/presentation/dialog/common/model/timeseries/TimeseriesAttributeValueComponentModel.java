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

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public abstract class TimeseriesAttributeValueComponentModel extends AbstractTimeseriesCMBase<TimeseriesEntry> {
  private static final long serialVersionUID = -1649714828528503316L;

  protected TimeseriesAttributeValueComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
  }

  /**
   * @return The attribute value as String for presentation
   */
  public abstract String getAttributeValueAsString();

  public abstract void setAttributeValueAsString(String value);

  /**
   * @return True if the model does not contain a value
   */
  public boolean isEmpty() {
    return getAttributeValueAsString().isEmpty();
  }

  public void initializeFrom(TimeseriesEntry source) {
    if (source != null) {
      setValueAsStringFromSource(source);
    }
    else {
      setAttributeValueAsString("");
    }
  }

  /**
   * Called to set the attribute string value from TimeseriesEntry source, if source != null
   * @param source
   *          The source TimeseriesEntry. Must not be null.
   */
  protected void setValueAsStringFromSource(TimeseriesEntry source) {
    setAttributeValueAsString(source.getValue());
  }

  public void validate(Errors errors) {
    if (isEmpty()) {
      errors.rejectValue("attributeValueAsString", "TIMESERIES_ENTRY_EMPTY_VALUE");
    }
  }

  public void update() {
    clearErrors();
    // else nothing to do. Work is done by data binding alone
  }

  public void configure(TimeseriesEntry target) {
    // nothing to do. Timeseries entries are immutable and have to be created with the right values by a containing component model
  }

  /**
   * @return The normalized value used to save in the timeseries entry. Does not contain any possible modifications for presentation
   *         as {@link #getAttributeValueAsString()} would return.
   */
  abstract String getNormalizedValue();
}
