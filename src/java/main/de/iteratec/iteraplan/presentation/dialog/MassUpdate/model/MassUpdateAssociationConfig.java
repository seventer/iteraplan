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
package de.iteratec.iteraplan.presentation.dialog.MassUpdate.model;

import de.iteratec.iteraplan.businesslogic.reports.query.type.SimpleAssociation.AssociationCardinality;


public class MassUpdateAssociationConfig extends MassUpdateConfig {

  /** Serialization version. */
  private static final long serialVersionUID = 8708201893059784223L;

  private String            cardinality;

  /**
   * The name of the getter method that is used to get the component model representing associations.
   * Used for accessing this component model by reflection in MassUpdateFortendService.
   */
  private String            methodNameOfGetModelOfAssociationMethod;

  /**
   * Indicates if the standard values for this attribute shall be set. Defaults to false. Will be
   * set to true when clicking on the Button in the GUI. True for at most one
   * MassUpdateAssociationConfig at any time.
   **/
  private boolean           setStandardValue;

  public String getCardinality() {
    return cardinality;
  }

  public void setCardinality(String cardinality) {
    this.cardinality = cardinality;
  }

  public boolean isToManyAssociationSet() {
    return AssociationCardinality.TO_MANY_SET.toString().equals(cardinality);
  }

  public boolean isToManyAssociationList() {
    return AssociationCardinality.TO_MANY_LIST.toString().equals(cardinality);
  }

  public boolean isToOneAssociation() {
    return AssociationCardinality.TO_ONE.toString().equals(cardinality);
  }

  public boolean isCustomAssociation() {
    return AssociationCardinality.CUSTOM.toString().equals(cardinality);
  }

  public void setMethodNameOfGetModelOfAssociationMethod(String pathToManyAssociationSetComponentModel) {
    this.methodNameOfGetModelOfAssociationMethod = pathToManyAssociationSetComponentModel;
  }

  public String getMethodNameOfGetModelOfAssociationMethod() {
    return methodNameOfGetModelOfAssociationMethod;
  }

  public void setSetStandardValue(boolean setStandardValue) {
    this.setStandardValue = setStandardValue;
  }

  public boolean isSetStandardValue() {
    return setStandardValue;
  }

}
