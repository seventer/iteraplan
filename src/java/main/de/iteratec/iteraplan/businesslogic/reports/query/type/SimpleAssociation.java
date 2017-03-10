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
package de.iteratec.iteraplan.businesslogic.reports.query.type;

import java.io.Serializable;


/**
 * A class holding meta information about associations that can be updated in massupdate mode. 
 * 
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 *
 */
public class SimpleAssociation implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = -8960559426861634171L;

  /**
   * Cardinatlity enum. Determines the cardinatlity of the opposite side of an association.
   * Needed to decide how to display and mange the association on the gui (i.e. which tile to
   * use)
   * 
   * @author Gunnar Giesinger, iteratec GmbH, 2007
   *
   */
  public enum AssociationCardinality {
    /** use the jsp/MassUpdate/tiles/OneAssociationComponentComboboxView.jsp to display the association **/
    TO_ONE("toOneAssociation"),

    /** use the jsp/MassUpdate/tiles/ManyAssociationSetComponentComboboxView.jsp to display the association **/
    TO_MANY_SET("toManyAssocationSet"),

    /** use the jsp/MassUpdate/tiles/ManyAssociationListComponentView.jsp to display the association **/
    TO_MANY_LIST("toManyAssocationList"),

    /** if a custom tile shall be used to display the association in the mass update dialogue
     * the tile has to be named according to the following naming convetion
     * Custom[BuildingBlockClassName]_[getterMethodForComponentModel].jsp
     * Example: CustomConnection_associatedCatalogItemComponentModel.jsp 
     **/
    CUSTOM("custom");

    private final String cardinality;

    private AssociationCardinality(String cardinality) {
      this.cardinality = cardinality;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return cardinality;
    }
  }

  /** The name of the association. Should be set to the name of the property of the BuildingBlock 
   * that holds the Set, List, Object, ... of the association**/
  private final String                 name;
  /** The localized description String of the association**/
  private final String                 namePresentationKey;
  /** Cardinality of the association. See {@link AssociationCardinality}**/
  private final AssociationCardinality cardinality;
  /** Position of the object if placed in a list. Used to fix the order after which associations are displayed
   * in columns on the gui**/
  private int                          position = 0;

  public SimpleAssociation(String name, String namePresentationKey, AssociationCardinality cardinality, int position) {
    this.name = name;
    this.namePresentationKey = namePresentationKey;
    this.cardinality = cardinality;
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public String getNamePresentationKey() {
    return namePresentationKey;
  }

  public AssociationCardinality getCardinality() {
    return cardinality;
  }

  public String getCardinalityString() {
    return cardinality.toString();
  }

  public int getPosition() {
    return position;
  }

}
