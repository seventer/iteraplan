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
package de.iteratec.iteraplan.businesslogic.exchange.common.portfolio;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import de.iteratec.iteraplan.model.BuildingBlock;

/**
 *
 */
public class Tile implements Serializable {

  private static final long serialVersionUID = 8791273703426862516L;
  
  private List<BuildingBlock> buildingBlocks = new LinkedList<BuildingBlock>();
  private double width;
  private double height;
  private double xPos;
  private double yPos;
  /**
   * @return buildingBlocks the buildingBlocks
   */
  public List<BuildingBlock> getBuildingBlocks() {
    return buildingBlocks;
  }
  public void setBuildingBlocks(List<BuildingBlock> buildingBlocks) {
    this.buildingBlocks = buildingBlocks;
  }
  /**
   * @return width the width
   */
  public double getWidth() {
    return width;
  }
  public void setWidth(double width) {
    this.width = width;
  }
  /**
   * @return height the height
   */
  public double getHeight() {
    return height;
  }
  public void setHeight(double height) {
    this.height = height;
  }
  /**
   * @return xPos the xPos
   */
  public double getxPos() {
    return xPos;
  }
  public void setxPos(double xPos) {
    this.xPos = xPos;
  }
  /**
   * @return yPos the yPos
   */
  public double getyPos() {
    return yPos;
  }
  public void setyPos(double yPos) {
    this.yPos = yPos;
  }
  
}
