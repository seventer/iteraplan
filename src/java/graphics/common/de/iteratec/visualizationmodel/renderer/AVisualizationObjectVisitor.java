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
package de.iteratec.visualizationmodel.renderer;

import de.iteratec.visualizationmodel.ALabeledVisualizationObject;
import de.iteratec.visualizationmodel.APrimitiveSymbol;
import de.iteratec.visualizationmodel.ASymbol;
import de.iteratec.visualizationmodel.BaseMapSymbol;
import de.iteratec.visualizationmodel.CompositeSymbol;
import de.iteratec.visualizationmodel.Ellipse;
import de.iteratec.visualizationmodel.EllipseArc;
import de.iteratec.visualizationmodel.Polygon;
import de.iteratec.visualizationmodel.Polyline;
import de.iteratec.visualizationmodel.Rectangle;
import de.iteratec.visualizationmodel.Text;


public abstract class AVisualizationObjectVisitor {

  /**
   * Visiting method for a composite.
   * 
   * @param composite The composite to be visited.
   */
  public void visit(CompositeSymbol composite) {
    visit((ASymbol) composite);
  }

  /**
   * Visiting method for a base map.
   * 
   * @param baseMap The {@link BaseMapSymbol} to be visited.
   */
  public void visit(BaseMapSymbol baseMap) {
    visit((CompositeSymbol) baseMap);
  }

  /**
   * Visiting method for an ellipse.
   * 
   * @param elli The ellipse to be visited.
   */
  public void visit(Ellipse elli) {
    visit((APrimitiveSymbol) elli);
  }

  /**
   * Visiting method for an ellipse arc.
   * 
   * @param ellarc The ellipse arc to be visited.
   */
  public void visit(EllipseArc ellarc) {
    visit((APrimitiveSymbol) ellarc);
  }

  /**
   * Visiting method for a polygon.
   * 
   * @param poly The polygon to be visited.
   */
  public void visit(Polygon poly) {
    visit((APrimitiveSymbol) poly);
  }

  /**
   * Visiting method for a polyline.
   * 
   * @param poly The polyline to be used.
   */
  public void visit(Polyline poly) {
    visit((APrimitiveSymbol) poly);
  }

  /**
   * Visiting method for a rectangle.
   * 
   * @param rect The rectangle to be visited.
   */
  public void visit(Rectangle rect) {
    visit((APrimitiveSymbol) rect);
  }

  /**
   * Visiting method for a text.
   * 
   * @param text The text to be visited.
   */
  public void visit(Text text) {
    visit((APrimitiveSymbol) text);
  }

  public void visit(ASymbol symbol) {
    //Basic hook for implementing visits on arbitrary symbols
  }

  public void visit(APrimitiveSymbol primi) {
    visit((ASymbol) primi);
  }

  public void visit(ALabeledVisualizationObject labeledPrimi) {
    visit((APrimitiveSymbol) labeledPrimi);
  }

  /**
   * Visiting method called, whenever a primitive and all its children (texts) are completely visited.
   * 
   * @param p The primitive which is completely visited.
   */
  public void endSymbol(ASymbol p) {
    //Basic hook for implementing end of visits on arbitrary symbols
  }

  @SuppressWarnings("cast")
  public void endPrimitive(APrimitiveSymbol primi) {
    endSymbol((ASymbol) primi);
  }
}
