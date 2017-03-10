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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Locale;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.visualizationmodel.APlanarSymbol;
import de.iteratec.visualizationmodel.APrimitiveSymbol;
import de.iteratec.visualizationmodel.ASymbol;
import de.iteratec.visualizationmodel.BaseMapSymbol;
import de.iteratec.visualizationmodel.EllipseArc;
import de.iteratec.visualizationmodel.Point;
import de.iteratec.visualizationmodel.Polygon;
import de.iteratec.visualizationmodel.Polyline;
import de.iteratec.visualizationmodel.Text;


/**
 * Rendering visitor for creating a svg-String from a set of primitives.
 * 
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public class SVGRenderingVisitor extends ARenderingVisitor<String, ST> {

  /**
   * 
   */
  private static final String CONTENTS                  = "contents";
  private static final String STRINGTEMPLATES_FILE      = "SVGTemplates.stg";
  private static final char   STRINGTEMPLATES_DELIMITER = '$';

  // Will be added to the calculated height of the image to hold the action bar
  private static final int    SIDE_MARGIN               = 70;
  private static final int    TOP_MARGIN                = 105;
  private static final int    BOTTOM_MARGIN             = 70;

  private static final Logger LOGGER                    = Logger.getLogger(SVGRenderingVisitor.class);

  private final STGroup       templates;
  private final ST            maintemplate;

  /**
   * Default constructor.
   * 
   * @throws InitializationException Thrown, when stringTemplate-files containing the svg-template are not available.
   */
  public SVGRenderingVisitor() throws InitializationException {
    templates = new STGroupFile(STRINGTEMPLATES_FILE, STRINGTEMPLATES_DELIMITER, STRINGTEMPLATES_DELIMITER);
    maintemplate = templates.getInstanceOf("Maintemplate");

    try {
      super.addAttributeSerializer(new PropertyDescriptor("lineStyle", APlanarSymbol.class), new LineStyleSerializer());
      super.addAttributeSerializer(new PropertyDescriptor("points", Polygon.class), new PolygonPointsSerializer());
      super.addAttributeSerializer(new PropertyDescriptor("points", Polyline.class), new PolylinePointsSerializer());
      super.addAttributeSerializer(new PropertyDescriptor("textStyle", Text.class), new TextStyleSerializer());
      super.addAttributeSerializer(new PropertyDescriptor("horizontalAlignment", Text.class), new HorizontalAlignmentSerializer());
      super.addAttributeSerializer(new PropertyDescriptor("fillColor", APlanarSymbol.class), new FillColorSerializer());
    } catch (IntrospectionException ie) {
      LOGGER.error("Error while introspecting a class: {0}", ie.getMessage(), ie);
      throw new InitializationException(ie);
    }
  }

  @Override
  protected void setAttribute(ST template, String attributeName, Object value) {
    if (value != null) {
      try {
        // Capitalize first letter of attribute name
        String cAttributeName = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);

        // Prevents warnings for attributes that get serialized by being visited.
        if (!(value instanceof APlanarSymbol)) {
          template.add(cAttributeName, value);
        }
      } catch (IllegalArgumentException iae) {
        LOGGER.warn("Can't serialize attribute '{0}' of '{1}'", attributeName, template.getName());
      }
    }
  }

  private ST getInstance(Class<?> clazz) {
    return templates.getInstanceOf(clazz.getSimpleName());
  }

  @Override
  public void visit(APrimitiveSymbol primi) {
    Class<? extends ASymbol> clazz = primi.getClass();
    if (clazz != Text.class) {
      ST group = this.templates.getInstanceOf("Group");
      group.add("Link", primi.getLink());
      group.add("LinkTarget", primi.getLinkTarget());
      maintemplate.add(CONTENTS, group.render());
    }
    ST elementTemplate = getInstance(clazz);
    setAllAttributes(primi, elementTemplate);
    maintemplate.add(CONTENTS, elementTemplate.render());
  }

  @Override
  public void endPrimitive(APrimitiveSymbol primi) {
    Class<? extends ASymbol> clazz = primi.getClass();
    if (clazz != Text.class) {
      ST group = this.templates.getInstanceOf("Endgroup");
      group.add("Link", primi.getLink());
      maintemplate.add(CONTENTS, group.render());
    }
  }

  @Override
  public String getResult() {
    return maintemplate.render();
  }

  // Converts the arc into a string containing the path definition
  @SuppressWarnings("boxing")
  private String ellipseArcToPath(EllipseArc a) {
    Point startpoint = new Point();
    Point endpoint = new Point();
    StringBuffer result = new StringBuffer();
    // See http://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes
    boolean arc = a.getEndAngle() - a.getStartAngle() > 180;
    boolean sweep = a.getEndAngle() - a.getStartAngle() > 0;
    startpoint.setX((int) (a.getRadiusX() * Math.cos(a.getStartAngle()) + a.getXpos()));
    startpoint.setY((int) (a.getRadiusY() * Math.sin(a.getStartAngle()) + a.getYpos()));
    endpoint.setX((int) (a.getRadiusX() * Math.cos(a.getEndAngle()) + a.getXpos()));
    endpoint.setY((int) (a.getRadiusY() * Math.sin(a.getEndAngle()) + a.getYpos()));

    result.append(String.format(Locale.US, "M %f %f ", startpoint.getX(), startpoint.getY()));
    result.append("A ");
    result.append(a.getRadiusX());
    result.append(' ');
    result.append(a.getRadiusY());
    result.append(' ');
    result.append((arc ? 1 : 0));
    result.append(' ');
    result.append((sweep ? 1 : 0));
    result.append(' ');
    result.append(endpoint.getX());
    result.append(' ');
    result.append(endpoint.getY());
    return result.toString();
  }

  @Override
  public void visit(EllipseArc arc) {
    ST elementTemplate = getInstance(arc.getClass());
    setAllAttributes(arc, elementTemplate);
    elementTemplate.add("Points", ellipseArcToPath(arc));
    maintemplate.add(CONTENTS, elementTemplate.render());
  }

  @Override
  public void visit(BaseMapSymbol baseMap) {
    super.visit(baseMap);
    maintemplate.add(BaseMapSymbol.TITLE, baseMap.getTitle());

    //handle naked export
    if (!baseMap.isNaked()) {
      maintemplate.add(BaseMapSymbol.GENERATED, baseMap.getGeneratedInformation());
      maintemplate.add(BaseMapSymbol.DESCRIPTION, baseMap.getDescription());
    }

    //handle scaling setting (custom width and height)
    if (baseMap.getCustomWidth() != null) {
      Double width = baseMap.getCustomWidth();
      double changeInPercent = (width.doubleValue() * 100) / (baseMap.getWidth() + SIDE_MARGIN * 2);
      double newHeight = ((baseMap.getHeight() + TOP_MARGIN + BOTTOM_MARGIN) * changeInPercent) / 100;

      this.maintemplate.remove("Width");
      this.maintemplate.remove("Height");

      this.maintemplate.add("Width", new Double(width.doubleValue() + SIDE_MARGIN * 2));
      this.maintemplate.add("Height", new Double(newHeight));
    }
    else if (baseMap.getCustomHeight() != null) {
      Double height = baseMap.getCustomHeight();
      double changeInPercent = (height.doubleValue() * 100) / (baseMap.getHeight() + TOP_MARGIN + BOTTOM_MARGIN);
      double newWidth = ((baseMap.getWidth() + SIDE_MARGIN * 2) * changeInPercent) / 100;

      this.maintemplate.remove("Width");
      this.maintemplate.remove("Height");

      this.maintemplate.add("Width", new Double(newWidth));
      this.maintemplate.add("Height", new Double(height.doubleValue() + TOP_MARGIN + BOTTOM_MARGIN));
    }

    if (baseMap.isNaked()) {
      this.maintemplate.add("Naked", "none");
    }
    else {
      this.maintemplate.add("Naked", "block");
    }
  }

  /**
   * Sets the dimensions of the final visualization.
   * 
   * @param width the visualization's width
   * @param height the visualization's height
   */
  @SuppressWarnings("boxing")
  public final void setDimensions(int width, int height) {

    this.maintemplate.add("Width", width + SIDE_MARGIN * 2);
    this.maintemplate.add("Height", height + TOP_MARGIN + BOTTOM_MARGIN);
    this.maintemplate.add("ViewBoxWidth", width + SIDE_MARGIN * 2);
    this.maintemplate.add("ViewBoxHeight", height + TOP_MARGIN + BOTTOM_MARGIN);
    this.maintemplate.add("contentEndX", width + SIDE_MARGIN);
    this.maintemplate.add("contentEndY", height + TOP_MARGIN);
  }
}