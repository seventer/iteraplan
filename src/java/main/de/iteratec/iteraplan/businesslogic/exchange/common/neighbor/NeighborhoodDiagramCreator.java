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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.spatial.geometry.shape.Point2D;

import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 *
 */
public class NeighborhoodDiagramCreator {

  private static final Double      OUTER_ANGULAR_SUM = new Double(360.0);

  private InformationSystemRelease objectOfInterst;
  private NeighborhoodDiagram      neighborhoodDiagram;

  public NeighborhoodDiagramCreator(InformationSystemRelease objectOfInterest) {
    this.objectOfInterst = objectOfInterest;
  }

  public NeighborhoodDiagram createNeighborhoodDiagram() {
    neighborhoodDiagram = new NeighborhoodDiagram();
    generateSpatialInformatioSystemReleases(getStatusFilteredConnectedIS());
    generateObjectOfInterestPosition();
    generateLegends();
    return neighborhoodDiagram;
  }

  /**
   * 
   *    All information systems, that have at least one direct relationship (over Interfaces) to the objectOfInterst . 
   *    
   *    The information systems are selected according to the following criteria:
   *    <ul>
   *    <li>if the objectOfInterst status is "current" only current IS are displayed</li>
   *    <li>if the objectOfInterst status is "planned" only planned and current IS are displayed</li>
   *    <li>if the objectOfInterst status is "target" only target and planned and current IS are displayed</li>
   *    <li>if the objectOfInterst status is "inactive" only inactive and current IS are displayed</li>
   *    </ul>
   * @return filtered Set of InformationSystemReleases
   */
  private List<InformationSystemRelease> getStatusFilteredConnectedIS() {
    List<InformationSystemRelease> resultSet = new ArrayList<InformationSystemRelease>();
    for (InformationSystemInterface isi : objectOfInterst.getAllConnections()) {
      InformationSystemRelease connectedISR;
      if (objectOfInterst.equals(isi.getInformationSystemReleaseA())) {
        connectedISR = isi.getInformationSystemReleaseB();
      }
      else {
        connectedISR = isi.getInformationSystemReleaseA();
      }
      if (connectedISR != null && !connectedISR.equals(objectOfInterst) && !resultSet.contains(connectedISR)) {
        if (InformationSystemRelease.TypeOfStatus.CURRENT.equals(objectOfInterst.getTypeOfStatus())
            && InformationSystemRelease.TypeOfStatus.CURRENT.equals(connectedISR.getTypeOfStatus())) {
          resultSet.add(connectedISR);
        }
        else if (InformationSystemRelease.TypeOfStatus.PLANNED.equals(objectOfInterst.getTypeOfStatus())
            && (InformationSystemRelease.TypeOfStatus.CURRENT.equals(connectedISR.getTypeOfStatus()) || InformationSystemRelease.TypeOfStatus.PLANNED
                .equals(connectedISR.getTypeOfStatus()))) {
          resultSet.add(connectedISR);
        }
        else if (InformationSystemRelease.TypeOfStatus.TARGET.equals(objectOfInterst.getTypeOfStatus())
            && (InformationSystemRelease.TypeOfStatus.CURRENT.equals(connectedISR.getTypeOfStatus())
                || InformationSystemRelease.TypeOfStatus.PLANNED.equals(connectedISR.getTypeOfStatus()) || InformationSystemRelease.TypeOfStatus.TARGET
                  .equals(connectedISR.getTypeOfStatus()))) {
          resultSet.add(connectedISR);
        }
        else if (InformationSystemRelease.TypeOfStatus.INACTIVE.equals(objectOfInterst.getTypeOfStatus())
            && (InformationSystemRelease.TypeOfStatus.CURRENT.equals(connectedISR.getTypeOfStatus()) || InformationSystemRelease.TypeOfStatus.INACTIVE
                .equals(connectedISR.getTypeOfStatus()))) {
          resultSet.add(connectedISR);
        }
      }
    }

    return resultSet;
  }

  private void generateSpatialInformatioSystemReleases(List<InformationSystemRelease> informationSystemReleases) {
    List<SpatialInfromationSystemWrapper> spatialInfromationSystemWrappers = new ArrayList<SpatialInfromationSystemWrapper>();
    //If conntent is empty return empty list
    if (informationSystemReleases.size() == 0) {
      neighborhoodDiagram.setConnectedInformationSystems(spatialInfromationSystemWrappers);
    }
    else if (informationSystemReleases.size() == 1) {
      spatialInfromationSystemWrappers.add(new SpatialInfromationSystemWrapper(informationSystemReleases.iterator().next(), 0,
          NeighborhoodDiagram.BLOCK_DISTANCE));
    }
    else if (informationSystemReleases.size() == 2) {
      int count = 0;
      for (InformationSystemRelease isr : informationSystemReleases) {
        spatialInfromationSystemWrappers.add(new SpatialInfromationSystemWrapper(isr, 0, count * NeighborhoodDiagram.BLOCK_DISTANCE));
        count++;
      }
    }
    else {

      Double azimuth = OUTER_ANGULAR_SUM / informationSystemReleases.size();
      int counter = 0;
      Point2D nextPoint = new Point2D(0, 0);
      for (InformationSystemRelease isr : informationSystemReleases) {
        spatialInfromationSystemWrappers.add(new SpatialInfromationSystemWrapper(isr, nextPoint));
        nextPoint = calculateNextPoint(azimuth * counter, nextPoint);
        counter++;
      }

      morphToPositiveValue(spatialInfromationSystemWrappers);
    }
    neighborhoodDiagram.setConnectedInformationSystems(spatialInfromationSystemWrappers);

  }

  /**
   * Calculated positions can have a negative value, this method morphs them all to a positive value
   * @param spatialInfromationSystemWrappers
   */
  private void morphToPositiveValue(List<SpatialInfromationSystemWrapper> spatialInfromationSystemWrappers) {
    double minXValue = 0.0;
    for (SpatialInfromationSystemWrapper isr : spatialInfromationSystemWrappers) {
      if (isr.getCoordinate().getX() < minXValue) {
        minXValue = isr.getCoordinate().getX();
      }
    }
    minXValue = Math.abs(minXValue);
    for (SpatialInfromationSystemWrapper isr : spatialInfromationSystemWrappers) {
      isr.getCoordinate().setX(isr.getCoordinate().getX() + minXValue);
    }
  }

  /**
   * Place the shapes in a circle
   * @param azimuth
   * @param currentPoint
   * @return
   *    Returns the next point on the border edge of the circle.
   */
  private Point2D calculateNextPoint(Double azimuth, Point2D currentPoint) {
    double xGradient;
    double yGradient;

    xGradient = Math.cos(Math.toRadians(azimuth)) * NeighborhoodDiagram.BLOCK_DISTANCE;
    yGradient = Math.sin(Math.toRadians(azimuth)) * NeighborhoodDiagram.BLOCK_DISTANCE;

    Point2D directionalVektor = new Point2D(xGradient, yGradient);
    directionalVektor.add(currentPoint);
    return directionalVektor;
  }

  private void generateObjectOfInterestPosition() {
    double minXValue = 0;
    double maxXValue = 0;
    double minYValue = 0;
    double maxYValue = 0;
    for (SpatialInfromationSystemWrapper isr : neighborhoodDiagram.getConnectedInformationSystems()) {
      minXValue = Math.min(minXValue, isr.getCoordinate().getX());
      maxXValue = Math.max(maxXValue, isr.getCoordinate().getX());
      minYValue = Math.min(minYValue, isr.getCoordinate().getY());
      maxYValue = Math.max(maxYValue, isr.getCoordinate().getY());
    }

    centeringPositions(maxXValue, maxYValue);

    neighborhoodDiagram.setObjectOfInterest(new SpatialInfromationSystemWrapper(objectOfInterst, new Point2D(neighborhoodDiagram.getSideLength() / 2,
        neighborhoodDiagram.getSideLength() / 2)));
  }

  /**
   * Centering all shapes
   * @param maxXValue
   * @param maxYValue
   */
  private void centeringPositions(double maxXValue, double maxYValue) {
    if (neighborhoodDiagram.getSideLength() < Math.max(maxXValue + NeighborhoodDiagram.ROOT_MARGIN, maxYValue + NeighborhoodDiagram.ROOT_MARGIN)) {
      neighborhoodDiagram.setSideLength(maxXValue + NeighborhoodDiagram.ROOT_MARGIN);
    }

    double centeringVectorX = (neighborhoodDiagram.getSideLength() - maxXValue) / 2;
    double centeringVectorY = (neighborhoodDiagram.getSideLength() - maxYValue) / 2;
    for (SpatialInfromationSystemWrapper isr : neighborhoodDiagram.getConnectedInformationSystems()) {
      isr.getCoordinate().set(isr.getCoordinate().getX() + centeringVectorX, isr.getCoordinate().getY() + centeringVectorY);
    }
  }

  private void generateLegends() {
    neighborhoodDiagram.setColorLegend(new HashMap<Color, String>());
    for (SpatialInfromationSystemWrapper spatialInfromationSystemWrapper : neighborhoodDiagram.getConnectedInformationSystems()) {
      if (neighborhoodDiagram.getColorLegend().containsKey(spatialInfromationSystemWrapper.getColorForStatus())) {
        neighborhoodDiagram.getColorLegend().put(spatialInfromationSystemWrapper.getColorForStatus(),
            spatialInfromationSystemWrapper.getInformationSystemRelease().getTypeOfStatus().name());
      }
    }
  }

}
