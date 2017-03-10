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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import java.io.Serializable;
import java.util.List;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;


/**
 * This interface provides methods to deal with the GUI data from a textual query regarding the
 * selected attributes, properties and associations to be used in a mass update It should be
 * implemented by JSP backing beans that hold the GUI data. It is designed to hold that data in an
 * instance of {@code Map}. Either the attribute name or the key according to the localization file
 * serve as key of the Map
 */
public interface MassUpdateData extends Serializable {

  /**
   * Returns the the list of properties that were selected by the user to be part of a massupdate
   * 
   * @return The list of property keys refering to properties that take part in the mass update
   */
  List<String> getSelectedPropertiesList();

  List<String> getSelectedAssociationsList();

  List<String> getSelectedAttributesList();

  /**
   * Sets the the list of properties that were selected by the user to be part of a massupdate
   * 
   * @param selectedProperties
   */
  void setSelectedPropertiesList(List<String> selectedProperties);

  void setSelectedAssociationsList(List<String> selectedAssociations);

  void setSelectedAttributesList(List<String> sSelectedAttributes);

  /**
   * Returns if a given attribute, property or association has been selected on the gui.
   * 
   * @param key
   *          The status as a string.
   * @return The value of the status as specified on the GUI.
   */
  Boolean getPropertySelected(String key);

  Boolean getAssociationSelected(String key);

  Boolean getAttributeSelected(String key);

  /**
   * Sets the status and its boolean value as specified on the GUI.
   * 
   * @param key
   *          The status as a string.
   * @param value
   *          The value of the status as specified on the GUI.
   */
  void setPropertySelected(String key, Boolean value);

  void setAssociationSelected(String key, Boolean value);

  void setAttributeSelected(String key, Boolean value);

  /**
   * Validates the user input.
   * 
   * @throws IteraplanBusinessException
   *           If validation could not be performed successfully, the exception should contain an
   *           error key declared in {@code IteraplanErrorMessages}.
   */
  void validate() throws IteraplanBusinessException;
}
