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

import de.iteratec.visualizationmodel.renderer.AVisualizationObjectVisitor;

public abstract class ASymbol extends AVisualizationObject{

	/**
	 * Sets the width.
	 * 
	 * @param width the width to set
	 */
	public abstract void setWidth(float width);

	/**
	 * Sets the height.
	 * 
	 * @param height the height to set
	 */
	public abstract void setHeight(float height);

	/**
	 * Sets the xpos.
	 * 
	 * @param newXpos the xPos to set
	 */
	public abstract void setXpos(float newXpos);

	/**
	 * Sets the ypos.
	 * 
	 * @param newYpos the yPos to set
	 */
	public abstract void setYpos(float newYpos);

	/**
	 * Gets the xpos.
	 * 
	 * @return the primitive's xPos
	 */
	public abstract float getXpos();

	/**
	 * Gets the ypos.
	 * 
	 * @return the primitive's yPos
	 */
	public abstract float getYpos();
	
	/**
	 * Gets the width.
	 * 
	 * @return the primitive's width
	 */
	public abstract float getWidth();

	/**
	 * Gets the height.
	 * 
	 * @return the primitive's height
	 */
	public abstract float getHeight();

	/**
	 * Needed for the visitor pattern.
	 * 
	 * @param visitor the visitor vising this primitive
	 */
	public abstract void visit(AVisualizationObjectVisitor visitor);
}