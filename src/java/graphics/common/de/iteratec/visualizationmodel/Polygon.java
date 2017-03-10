/*
 * Copyright 2011 Christian M. Schweda & iteratec
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iteratec.visualizationmodel;

import java.util.List;

import de.iteratec.visualizationmodel.renderer.AVisualizationObjectVisitor;


/**
 * A polygon defined by a list of points. Warning: No x and y coordinates here,
 * the points are defined in absolute coordinates.
 * 
 * @author Christian M. Schweda
 * 
 * @version 6.0
 */
public class Polygon extends ALabeledVisualizationObject {

  private List<Point> points;

  /**
   * Set the points comprising the polygon.
   * 
   * @param newPoints the list of points
   */
  public void setPoints(List<Point> newPoints) {
    points = newPoints;
  }

  /**
   * Get the points the polygon is comprised of.
   * 
   * @return the list of points
   */
  public List<Point> getPoints() {
    return points;
  }

  @Override
  public float getWidth() {
    float minX = Float.MAX_VALUE;
    float maxX = -Float.MAX_VALUE;
    for (Point p : points) {
      minX = Math.min(minX, p.getX());
      maxX = Math.max(maxX, p.getX());
    }

    return maxX - minX;
  }

  @Override
  public float getHeight() {
    float minY = Float.MAX_VALUE;
    float maxY = -Float.MAX_VALUE;
    for (Point p : points) {
      minY = Math.min(minY, p.getY());
      maxY = Math.max(maxY, p.getY());
    }

    return maxY - minY;
  }

  @Override
  public void setYpos(float newYpos) {
    if (points != null) {
      float currentYpos = getYpos();
      float delta = newYpos - currentYpos;
      for (Point point : points) {
        point.setY(delta + point.getY());
      }
    }
    super.setYpos(newYpos);
  }

  @Override
  public void setXpos(float newXpos) {
    if (points != null) {
      float currentXpos = getXpos();
      float delta = newXpos - currentXpos;
      for (Point point : points) {
        point.setX(delta + point.getX());
      }
    }
    super.setXpos(newXpos);
  }

  // This needs to override the superclass with the same method to use 
  // visitor.visit(Polygon) instead of a more general one
  @Override
  public void visit(AVisualizationObjectVisitor visitor) {
    visitor.visit(this);
    if (getText() != null) {
      getText().visit(visitor);
    }
    visitor.endPrimitive(this);
  }
}