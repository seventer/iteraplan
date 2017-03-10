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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Represents imported entry for the object related permissions. It contains the 
 * building block id (see {@link #getId()}), type (see {@link #getTypeOfBuildingBlock()})
 * and a set of users, having exclusive read/write permissions on this object.
 * 
 * @author agu
 *
 */
public class ObjectRelatedPermissionsData {

  /** Building block id. */
  private CellValueHolder     id;
  /** Building block name. */
  private CellValueHolder     name;
  /** Building block type. */
  private TypeOfBuildingBlock typeOfBuildingBlock;
  /** The users having exclusiv permissions. */
  private CellValueHolder     users;

  public ObjectRelatedPermissionsData(CellValueHolder id, TypeOfBuildingBlock typeOfBuildingBlock, CellValueHolder users) {
    this.id = id;
    this.typeOfBuildingBlock = typeOfBuildingBlock;
    this.users = users;
  }
  
  public ObjectRelatedPermissionsData(CellValueHolder id, CellValueHolder name, TypeOfBuildingBlock typeOfBuildingBlock, CellValueHolder users) {
    this(id, typeOfBuildingBlock, users);
    this.name = name;
  }

  /**
   * Returns the building block id.
   * 
   * @return the building block id
   */
  public CellValueHolder getId() {
    return id;
  }
  
  /**
   * Returns the building block name.
   * 
   * @return the building block name
   */
  public CellValueHolder getName() {
    return name;
  }

  /**
   * Returns building block type.
   * 
   * @return the building block type.
   */
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return typeOfBuildingBlock;
  }

  /**
   * Returns the users having exclusive read/write permissions.
   * 
   * @return the users having exclusive read/write permissions
   */
  public CellValueHolder getUsers() {
    return users;
  }

  @Override
  public String toString() {
    ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    toStringBuilder.append("id", getId().getCellRef());
    toStringBuilder.append("type", getTypeOfBuildingBlock());
    toStringBuilder.append("users", getUsers().getCellRef());

    return toStringBuilder.toString();
  }
}
