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
package de.iteratec.visualizationmodel.renderer.impl.svg;

import de.iteratec.visualizationmodel.ASymbol;
import de.iteratec.visualizationmodel.BaseMapSymbol;
import de.iteratec.visualizationmodel.CompositeSymbol;
import de.iteratec.visualizationmodel.renderer.AVisualizationObjectVisitor;


/**
 * Auxiliary visitor for determining the total size of the visualization to be rendered.
 * 
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public class SizeVisitor extends AVisualizationObjectVisitor {

  private int xMin;
  private int xMax;
  private int yMin;
  private int yMax;

  /**
   * Default constructor for a size visitor.
   */
  public SizeVisitor() {
    this.xMax = Integer.MIN_VALUE;
    this.yMax = Integer.MIN_VALUE;
    this.xMin = Integer.MAX_VALUE;
    this.yMin = Integer.MAX_VALUE;
  }

  @Override
  public void visit(ASymbol primi) {
    float xminus = primi.getXpos() - primi.getWidth() / 2;
    if (xminus < this.xMin) {
      this.xMin = (int) xminus;
    }
    float yminus = primi.getYpos() - primi.getHeight() / 2;
    if (yminus < this.yMin) {
      this.yMin = (int) yminus;
    }
    float maxx = primi.getXpos() + primi.getWidth() / 2;
    if (maxx > this.xMax) {
      this.xMax = (int) maxx;
    }
    float maxy = primi.getYpos() + primi.getHeight() / 2;
    if (maxy > this.yMax) {
      this.yMax = (int) maxy;
    }
  }

  /**
   * Visiting method for a base map.
   * 
   * @param baseMap The {@link BaseMapSymbol} to be visited.
   */
  @Override
  public void visit(BaseMapSymbol baseMap) {
    visit((CompositeSymbol) baseMap);
  }

  /*@Override
  public void visit(Polygon poly) {
  	for (Point p:poly.getPoints()) {
  		if (p.getX() < this.xMin) {
  			this.xMin = (int)p.getX() - 1;
  		}
  		if (p.getX() > this.xMax) {
  			this.xMax = (int)p.getX() + 1;
  		}
  		if (p.getY() < this.yMin) {
  			this.yMin = (int)p.getY() - 1;
  		}
  		if (p.getX() > this.yMax) {
  			this.yMax = (int)p.getY() + 1;
  		}
  	}
  }
  
  @Override
  public void visit(Polyline poly) {
  	for (Point p:poly.getPoints()) {
  		if (p.getX() < this.xMin) {
  			this.xMin = (int)p.getX() - 1;
  		}
  		if (p.getX() > this.xMax) {
  			this.xMax = (int)p.getX() + 1;
  		}
  		if (p.getY() < this.yMin) {
  			this.yMin = (int)p.getY() - 1;
  		}
  		if (p.getX() > this.yMax) {
  			this.yMax = (int)p.getY() + 1;
  		}
  	}
  }*/

  /**
   * Method for accessing the total width of the visualization.
   * 
   * @return The visualization's width.
   */
  public int getWidth() {
    return this.xMax - this.xMin;
  }

  /**
   * Method for accessing the total height of the visualization.
   * 
   * @return The visualization's height.
   */
  public int getHeight() {
    return this.yMax - this.yMin;
  }

  /**
   * @return the minimum x-value used by the primitives
   */
  public int getXMin() {
    return this.xMin;
  }

  /**
   * @return the minimum y-value used by the primitives
   */
  public int getYMin() {
    return this.yMin;
  }
}