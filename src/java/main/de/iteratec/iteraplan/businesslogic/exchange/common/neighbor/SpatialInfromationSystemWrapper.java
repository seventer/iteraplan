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
package de.iteratec.iteraplan.businesslogic.exchange.common.neighbor;

import java.awt.Color;

import org.apache.lucene.spatial.geometry.shape.Point2D;

import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 *Needed for neighborhood diagram, wraps coordinates, ISR and abbreviation of name, for drawing of the diagram
 */
public class SpatialInfromationSystemWrapper {

  private final InformationSystemRelease informationSystemRelease;
  private Point2D                        coordinate;
  private String                         nameAbbreviation;

  public SpatialInfromationSystemWrapper(InformationSystemRelease informationSystemRelease, Integer x, Integer y) {
    this.informationSystemRelease = informationSystemRelease;
    this.coordinate = new Point2D(x.intValue(), y.intValue());
  }

  public SpatialInfromationSystemWrapper(InformationSystemRelease informationSystemRelease, Point2D cooridante) {
    this.informationSystemRelease = informationSystemRelease;
    this.coordinate = cooridante;
  }

  /**
   * @return informationSystemRelease the informationSystemRelease
   */
  public InformationSystemRelease getInformationSystemRelease() {
    return informationSystemRelease;
  }

  /**
   * @return coordinate the coordinate
   */
  public Point2D getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Point2D coordinate) {
    this.coordinate = coordinate;
  }

  public Color getColorForStatus() {
    if (InformationSystemRelease.TypeOfStatus.CURRENT == informationSystemRelease.getTypeOfStatus()) {
      return NeighborhoodDiagram.STATUS_CURRENT_COLOR;
    }
    else if (InformationSystemRelease.TypeOfStatus.TARGET == informationSystemRelease.getTypeOfStatus()) {
      return NeighborhoodDiagram.STATUS_TARGET_COLOR;
    }
    else if (InformationSystemRelease.TypeOfStatus.PLANNED == informationSystemRelease.getTypeOfStatus()) {
      return NeighborhoodDiagram.STATUS_PLANNED_COLOR;
    }
    else if (InformationSystemRelease.TypeOfStatus.INACTIVE == informationSystemRelease.getTypeOfStatus()) {
      return NeighborhoodDiagram.STATUS_INACTIVE_COLOR;
    }
    return null;
  }

  public boolean hasNameAbbreviation() {
    if (this.nameAbbreviation != null) {
      return true;
    }
    return false;
  }

  /**
   * @return nameAbbreviation the nameAbbreviation
   */
  public String getNameAbbreviation() {
    return nameAbbreviation;
  }

  public void setNameAbbreviation(String nameAbbreviation) {
    this.nameAbbreviation = nameAbbreviation;
  }

}
