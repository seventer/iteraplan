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

import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * Models a reference in the domain graph from one type to another.
 * 
 * This is used by Type instances to model references from itself
 * to another type. The path is described
 * by looking at a property to navigate by and the returned type. The return
 * value is distinguished for either being a set or a single item.
 */
public class TypeWithJoinProperty implements Serializable {

  /** Serialization version. */
  private static final long             serialVersionUID = 8411972374102499483L;

  /**
   * The name of the property to use to retrieve the associated object.
   * 
   * Never null. Never empty.
   */
  private String                        joinPropertyName;

  /**
   * Flag if the target is a set.
   * 
   * This is true iff there are multiple objects returned by the join
   * property (i.e. the return value is a Set).
   */
  private boolean                       targetReturnsMultipleEntities;

  /**
   * The result type.
   * 
   * The type of the return value, either directly (if {@link #targetReturnsMultipleEntities}
   * is false) or as part of a Set (otherwise).
   */
  private Type<? extends BuildingBlock> type;

  /**
   * Creates a new join definition.
   * 
   * @param joinPropertyName        The name of the property to follow in the object graph. Not null. Not empty.
   * @param typeToJoin              The type that is the result of the navigation. Not null.
   * @param returnsMultipleEntities True if the result of the navigation is not an instance 
   *                                but a Set of entities of the result type.
   */
  public TypeWithJoinProperty(String joinPropertyName, Type<? extends BuildingBlock> typeToJoin, boolean returnsMultipleEntities) {
    if ((joinPropertyName == null) || (joinPropertyName.length() <= 0) || (typeToJoin == null)) {
      throw new IllegalArgumentException("TypeWithJoinProperty cannot be initialized with null values.");
    }
    this.joinPropertyName = joinPropertyName;
    this.type = typeToJoin;
    this.targetReturnsMultipleEntities = returnsMultipleEntities;
  }

  /**
   * Returns the target type.
   * 
   * @return The type returned by the property -- either as instance or as set.
   */
  public Type<? extends BuildingBlock> getType() {
    return this.type;
  }

  /**
   * Gets the name of the property establishing the association.
   * 
   * @return The name of the property establishing the association. Not null. Not empty.
   */
  public String getAssociationName() {
    return this.joinPropertyName;
  }

  /**
   * Flag if the association returns a set or single item.
   * 
   * @return true iff the return value of the property is a Set, false iff only a single item is retrieved.
   */
  public boolean returnsMultipleEntities() {
    return this.targetReturnsMultipleEntities;
  }
}
