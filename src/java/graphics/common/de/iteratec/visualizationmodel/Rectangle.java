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
 * A Rectangle defined by width, height, corner round value and x,y coordinates.
 * A non-rounded rectangle is simply a rectangle with roundAmount 0.
 * 
 * @author Christian M. Schweda
 * 
 * @version 6.0
 */
public class Rectangle extends ALabeledVisualizationObject {

	private float roundAmount;

	/**
	 * Set the corner round radius.
	 * 
	 * @param newRound the corner radius of the rectangle
	 */
	public void setRoundAmount(float newRound) {
		roundAmount = newRound;
	}

	/**
	 * Get the corner round radius.
	 * 
	 * @return the corner radius of the rectangle
	 */
	public float getRoundAmount() {
		return roundAmount;
	}
}
