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
 * Like the polygon, but not closed. Also comprised of absolute points.
 * 
 * @author Christian M. Schweda
 * 
 * @version 6.0
 */
public class Polyline extends APlanarSymbol {

	private List<Point> points;

	/**
	 * Set the line end points.
	 * 
	 * @param newSegments the list of points for the polyline.
	 */
	public void setPoints(List<Point> newSegments) {
		points = newSegments;
	}

	/**
	 * Get the line end points.
	 * 
	 * @return the list of points for the polyline.
	 */
	public List<Point> getPoints() {
		return points;
	}

	@Override
	public void visit(AVisualizationObjectVisitor visitor) {
		visitor.visit(this);
		visitor.endPrimitive(this);
	}
}