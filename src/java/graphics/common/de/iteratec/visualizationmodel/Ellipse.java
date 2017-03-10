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
 * An ellipse defined by midpoint, x radius and y radius.
 *
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public class Ellipse extends ALabeledVisualizationObject {

	private float radiusX;
	private float radiusY;
	
	
	/**
	 * Sets the new radius in the x-axis.
	 * 
	 * @param newRadiusX the new radius
	 */
	public void setRadiusX(float newRadiusX) {
		radiusX = newRadiusX;
	}

	/**
	 * Sets the new radius in the y-axis.
	 * 
	 * @param newRadiusY the new radius along the y-axis
	 */
	public void setRadiusY(float newRadiusY) {
		radiusY = newRadiusY;
	}

	/**
	 * Get the radius in the x-axis.
	 * 
	 * @return the radius in the y-axis
	 */
	public float getRadiusX() {
		return radiusX;
	}

	/**
	 * Get the radius in the y-axis.
	 * 
	 * @return the radius in the y-axis
	 */
	public float getRadiusY() {
		return radiusY;
	}
}