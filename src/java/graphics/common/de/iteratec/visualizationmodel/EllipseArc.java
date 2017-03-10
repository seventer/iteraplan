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

/**
 * A partial ellipse, defined by a center point, an x radius, an y radius and a start and end angle.
 * 
 * @author Christian M. Schweda
 * 
 * @version 6.0
 */
public class EllipseArc extends ALabeledVisualizationObject {

  private float radiusX;
  private float radiusY;
  private float startAngle;
  private float endAngle;

  /**
   * Sets the start angle for the ellipse arc starting with 0 in northern direction.
   * 
   * @param newStartAngle the start angle for the ellipse arc
   */
  public void setStartAngle(float newStartAngle) {
    startAngle = newStartAngle;
  }

  /**
   * Sets the end angle for the ellipse arc starting with 0 in northern direction.
   * 
   * @param newEndAngle the end angle for the ellipse arc
   */
  public void setEndAngle(float newEndAngle) {
    endAngle = newEndAngle;
  }

  /**
   * Sets the radius along the x-axis.
   * 
   * @param newXRadius the radius in x-axis direction
   */
  public void setRadiusX(float newXRadius) {
    radiusX = newXRadius;
  }

  /**
   * Sets the radius along the y-axis.
   * 
   * @param newYRadius the radius in y-axis direction
   */
  public void setRadiusY(float newYRadius) {
    radiusY = newYRadius;
  }

  /**
   * Gets the start angle.
   * 
   * @return the start angle
   */
  public float getStartAngle() {
    return startAngle;
  }

  /**
   * Gets the end angle.
   * 
   * @return the end angle
   */
  public float getEndAngle() {
    return endAngle;
  }

  /**
   * Gets the radius x.
   * 
   * @return the radius in x-axis direction
   */
  public float getRadiusX() {
    return radiusX;
  }

  /**
   * Gets the radius y.
   * 
   * @return the radius in y-axis direction
   */
  public float getRadiusY() {
    return radiusY;
  }
}