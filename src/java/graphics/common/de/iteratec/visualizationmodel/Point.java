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
 * Class representing a point from the visualization model.
 * 
 * @author Christian M. Schweda
 *
 * @version 5.0
 */
public class Point {

	/**
	 * Attribute containing the x value of this point.
	 */
	private float x;
	
	/**
	 * Attribute containing the y value of this point.
	 */
	private float y;
	
	/**
	 * Default constructor for a point instance at (0,0).
	 */
	public Point() {
		//creates point at (0,0)
	}
	
	/**
	 * Constructor for a point instance a (x,y).
	 * 
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 */
	public Point(float x, float y) {
		this();
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Method for creating a point instance from the given string.
	 * 
	 * @param str The string, which should be converted to a point value.
	 * 
	 * @return The corresponding point value.
	 */
	public static final Point create(String str) {
		String[] temp = str.split(";");
		return new Point(Float.parseFloat(temp[0]), Float.parseFloat(temp[1]));
	}
	
	/**
	 * Method for converting a point instance to a string.
	 * 
	 * @param point The point value to be converted.
	 * 
	 * @return A string representing the provided point.
	 */
	public static final String toString(Point point) {
		StringBuffer sb = new StringBuffer();
		sb.append(Float.toString(point.getX()));
		sb.append(';');
		sb.append(Float.toString(point.getY()));
		return sb.toString();
	}
	
	/**
	 * Method for reading the x-coordinate of the point.
	 * 
	 * @return The x-coordinate of the point.
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Method for setting the x-coordinate of the point.
	 * 
	 * @param x The new x-coordinate of the point.
	 */
	public void setX(float x) {
		this.x = x;
	}
	
	/**
	 * Method for reading the y-coordinate of the point.
	 * 
	 * @return The y-coordinate of the point.
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Method for setting the y-coordinate of the point.
	 * 
	 * @param y The new y-coordinate of the point.
	 */
	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Point) {
			return this.x == ((Point) o).x && this.y == ((Point) o).y;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Float.floatToIntBits(this.x) & Float.floatToIntBits(this.y);
	}
	
	@Override
	public String toString() {
		return Float.toString(getX()) + "," + Float.toString(getY());
	}
	
	/**
	 * Scalar multiplication on points.
	 * 
	 * @param old the point to be multiplied
	 * @param factor the scalar-factor
	 * 
	 * @return the new point
	 */
	public static final Point times(Point old, float factor) {
		return new Point(old.getX() * factor, old.getY() * factor);
	}
	
	/**
	 * component-wise addition on points.
	 * 
	 * @param p0 point 1
	 * @param p1 point 2
	 * 
	 * @return the sum of above points
	 */
	public static final Point add(Point p0, Point p1) {
		return new Point(p0.getX() + p1.getX(), p0.getY() + p1.getY());
	}
}