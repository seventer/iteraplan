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
package de.iteratec.iteraplan.persistence.dao;

import java.util.List;

import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;


/**
 * This class provides access to persisted {@link AttributeTypeGroup}s.
 */
public interface AttributeTypeGroupDAO extends DAOTemplate<AttributeTypeGroup, Integer> {

  /**
   * Returns a list of all {@code AttributeTypeGroup}s sorted by their name.
   * 
   * @return a list of all {@code AttributeTypeGroup}s sorted by their name
   */
  List<AttributeTypeGroup> getAllAttributeTypeGroups();

  /**
   * Returns the maximum position number of an AttributeTypeGroup.
   * 
   * @return The largest position number found. If no position number can be acquired, 0 is
   *         returned.
   */
  Integer getMaxATGPositionNumber();

  /**
   * Returns the minimum position number of an AttributeTypeGroup.
   * 
   * @return The smallest position number found. If no position number can be acquired, 0 is
   *         returned.
   */
  Integer getMinATGPositionNumber();

  /**
   * Returns the standard attribute type group.
   * 
   * @return The standard attribute type group having the name {@link AttributeTypeGroup#STANDARD_ATG_NAME}.
   */
  AttributeTypeGroup getStandardAttributeTypeGroup();

  /**
   * Loads an {@link AttributeTypeGroup} by its position.
   * 
   * @param position The position 
   * @return The attribute type group with the given position.
   */
  AttributeTypeGroup getAttributeTypeGroupByPosition(Integer position);

  /**
   * Loads an {@link AttributeTypeGroup} by its name.
   * @param name The name of the {@link AttributeTypeGroup} to load.
   * @return the attribute type group with the given name.
   */
  AttributeTypeGroup getAttributeTypeGroupByName(String name);

}
