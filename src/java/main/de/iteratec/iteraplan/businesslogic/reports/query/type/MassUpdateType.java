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
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Interface for accessing Meta Information of Types used for Mass Update
 * 
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 */
public interface MassUpdateType extends Serializable {

  /**
   * Return the {@link TypeOfBuildingBlock} the MassUpdateType represents
   * 
   * @return The TypeOfBuildingBlock
   */
  TypeOfBuildingBlock getTypeOfBuildingBlock();

  /**
   * Returns all properties of the BuildingBlock represented by the MassUpdateType that will be
   * updateable in mass update mode
   * 
   * @return The properties that can be updated
   */
  List<Property> getProperties();

  /**
   * Returns the I18N key for the underlying BuildingBlock. Wrapper for
   * {@link de.iteratec.iteraplan.businesslogic.reports.query.type.Type#getTimespanPresentationKey()}
   * 
   * @return I18N key for the BuildingBlock
   */
  String getTypeNamePresentationKey();

  /**
   * Returns the list of all MassUpdate associations of the underlying business type. I.e. all
   * associations that will be updateable in mass update mode
   * 
   * @return The list of MetaInformation of updateable associations. See {@link SimpleAssociation}
   */
  Set<SimpleAssociation> getMassUpdateAssociations();

  /**
   * Tests if the given property ID (i.e. the representation of the property having "." replaced by
   * "_") belongs to the given nameDB
   * 
   * @param propertyId
   *          The id
   * @param nameDB
   *          The database property name
   * @return True if the strings are corresponding
   */
  boolean isPropertyIdEqual(String propertyId, String nameDB);

  /**
   * Returns a property specified by its nameDB
   * 
   * @param nameDB
   *          The db name of the property
   * @return The property, <code>null</code> if the property is not registered
   */
  Property getProperty(String nameDB);

  /**
   * Returns the propert based on a given ID (i.e. the key replaced "." by "_")
   * 
   * @param propertyId
   *          The id of the property without "."
   * @return The property instance
   */
  Property getPropertyFromId(String propertyId);

  /**
   * Returns a simple association having the given ID
   * 
   * @param associationId
   *          The ID of the association
   * @return The association instance (contains meta information about an association of the
   *         underlying BuildingBlock)
   */
  SimpleAssociation getSimpleAssociationFromId(String associationId);

}
