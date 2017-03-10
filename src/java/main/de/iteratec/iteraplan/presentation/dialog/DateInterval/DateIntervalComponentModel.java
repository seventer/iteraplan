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
package de.iteratec.iteraplan.presentation.dialog.DateInterval;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.ElementNameComponentModel;

/**
 *
 */
public class DateIntervalComponentModel extends AbstractComponentModelBase<DateInterval> {

  private static final long serialVersionUID = -996813462521516620L;
  protected static final String                                                       NAME_LABEL        = "global.name";
  protected static final String                                                       NAME_FIELD        = "name";
  private ElementNameComponentModel<DateInterval>                           nameModel;
  
  private DateInterval dateInterval;
  
  private String selectedStartDate = "";
  private String selectedEndDate = "";
  
  public DateIntervalComponentModel() {
    this(ComponentMode.READ);
  }

  protected DateIntervalComponentModel(ComponentMode componentMode) {
    super(componentMode);
    
    nameModel = new NameCM(componentMode, NAME_FIELD, NAME_LABEL);
  }
  
  public final ElementNameComponentModel<DateInterval> getNameModel() {
    if (nameModel == null) {
      nameModel = new NameElementNameComponentModel(getComponentMode(), "name", NAME_LABEL);
    }
    return nameModel;
  }

  /**{@inheritDoc}**/
  public void initializeFrom(DateInterval source) {
    // TODO to complete
    nameModel.initializeFrom(source);
    if (source.getStartDate() != null) {
      this.selectedStartDate = source.getStartDate().getName();
    }
    if (source.getEndDate() != null) {
      this.selectedEndDate = source.getEndDate().getName();
    }
    this.dateInterval = source;
  }

  /**{@inheritDoc}**/
  public void update() {
    // TODO Auto-generated method stub
    
  }

  /**{@inheritDoc}**/
  public void configure(DateInterval target) {
    // TODO Auto-generated method stub
    
  }

  /**{@inheritDoc}**/
  public void validate(Errors errors) {
    // TODO Auto-generated method stub
    
  }

  public DateInterval getDateInterval() {
    return dateInterval;
  }

  public void setDateInterval(DateInterval dateInterval) {
    this.dateInterval = dateInterval;
  }

  public String getSelectedStartDate() {
    return selectedStartDate;
  }

  public void setSelectedStartDate(String selectedStartDate) {
    this.selectedStartDate = selectedStartDate;
  }

  public String getSelectedEndDate() {
    return selectedEndDate;
  }

  public void setSelectedEndDate(String selectedEndDate) {
    this.selectedEndDate = selectedEndDate;
  }

  private static final class NameElementNameComponentModel extends ElementNameComponentModel<DateInterval> {
    /** Serialization version. */
    private static final long serialVersionUID = -2168507012040915810L;

    public NameElementNameComponentModel(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(DateInterval target, String stringToSet) {
      target.setName(stringToSet);
    }

    @Override
    public String getStringFromElement(DateInterval source) {
      return source.getName();
    }
  }
  
  private static final class NameCM extends ElementNameComponentModel<DateInterval> {
    /** Serialization version. */
    private static final long serialVersionUID = -6636705090058480880L;

    public NameCM(ComponentMode componentMode, String htmlId, String labelKey) {
      super(componentMode, htmlId, labelKey);
    }

    @Override
    public void setStringForElement(DateInterval target, String stringToSet) {
      target.setName(stringToSet);
    }

    @Override
    public String getStringFromElement(DateInterval source) {
//      if (DateInterval.TOP_LEVEL_NAME.equals(source.getName())) {
//        setVirtualElementSelected(true);
//      }

      return source.getName();
    }
  }
}
