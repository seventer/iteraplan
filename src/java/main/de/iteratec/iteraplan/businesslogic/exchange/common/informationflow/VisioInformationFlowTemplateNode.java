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
package de.iteratec.iteraplan.businesslogic.exchange.common.informationflow;

import java.util.Set;

import com.google.common.collect.Sets;


/**
 * Holds relevant information of a node of a information flow layout template for Visio.
 */
public class VisioInformationFlowTemplateNode {

  /** ID of the building block */
  private Integer            id;
  /** X position of the Visio shape in the template */
  private double             pinX;
  /** Y position of the Visio shape in the template */
  private double             pinY;
  /** Building block ID of this element's parent (null if not existing)*/
  private Integer            parentId;
  /** Building block IDs of this element's children (empty if not existing)*/
  private final Set<Integer> childrenIds = Sets.newHashSet();

  public VisioInformationFlowTemplateNode(Integer id, double pinX, double pinY) {
    this.id = id;
    this.pinX = pinX;
    this.pinY = pinY;
  }

  /**
   * @return {@link #id}
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets {@link #id}.
   * @param id
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return {@link #pinX}
   */
  public double getPinX() {
    return pinX;
  }

  /**
   * Sets {@link #pinX}.
   * @param pinX
   */
  public void setPinX(double pinX) {
    this.pinX = pinX;
  }

  /**
   * @return {@link #pinY}
   */
  public double getPinY() {
    return pinY;
  }

  /**
   * Sets {@link #pinY}.
   * @param pinY
   */
  public void setPinY(double pinY) {
    this.pinY = pinY;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public Set<Integer> getChildrenIds() {
    return childrenIds;
  }

  public void addChildrenId(Integer childId) {
    childrenIds.add(childId);
  }

}
