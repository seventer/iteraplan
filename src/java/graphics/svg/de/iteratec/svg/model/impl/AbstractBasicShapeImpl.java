/*
 * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2004 - 2014 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact iteratec GmbH headquarters at Inselkammerstr. 4
 * 82008 Munich - Unterhaching, Germany, or at email address info@iteratec.de.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "iteraplan" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by iteraplan".
 */
package de.iteratec.svg.model.impl;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.MasterNotFoundException;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.styling.SvgBaseStyling;
import de.iteratec.svg.styling.SvgCssStyling;


public abstract class AbstractBasicShapeImpl implements BasicShape {

  private static final Logger                              LOGGER        = Logger.getIteraplanLogger(AbstractBasicShapeImpl.class);

  private List<String>                                     shapeStyles;
  private boolean                                          shapeCSSStylingEnabled;

  private SvgBaseStyling                                   defaultStyle;

  private final String                                     masterName;
  private final DocumentImpl                               document;
  private final String                                     shapeId;

  private double                                           rotationAngle;
  // private List<AbstractBasicShapeImpl> innerShapes;
  private final Map<Integer, List<AbstractBasicShapeImpl>> innerShapes;
  private AbstractBasicShapeImpl                           parentShape;

  private double                                           pinX;
  private double                                           pinY;
  private double                                           width;
  private double                                           height;

  private boolean                                          basicShape;

  private String                                           xLinkURL;

  private int                                              globalLayer   = Document.DEFAULT_LAYER;
  private int                                              internalLayer = Document.DEFAULT_LAYER;

  public AbstractBasicShapeImpl(String masterShapeName, String shapeId, DocumentImpl document) {

    this.shapeId = shapeId;
    this.masterName = masterShapeName;
    this.document = document;
    this.innerShapes = new HashMap<Integer, List<AbstractBasicShapeImpl>>();
    this.parentShape = null;
    this.rotationAngle = 0;
    this.shapeCSSStylingEnabled = true;
    shapeStyles = new ArrayList<String>();
    defaultStyle = new SvgBaseStyling();
    this.xLinkURL = "";

    // initialize the coordinates and sizes as the basic shapes don't provide them automatically
    this.pinX = 0;
    this.pinY = 0;
    this.width = 0;
    this.height = 0;

  }

  public DocumentImpl getDocument() {
    return this.document;
  }

  public Rectangle2D getBoundingBox() {

    // TODO
    /*
     * The current method is actually rather inaccurate, as it doesen't take the shapes' rotations
     * into consideration. A possible improvement is to use sine/cosine functions for all shapes
     * from the very top one (recursive addition of the angles) and then estimate where the end
     * points of this shape are. Implementation through accessing the parent shapes?
     */

    Rectangle2D bbox = new Rectangle2D.Double(this.getPinX(), this.getPinY(), this.getPinX() + this.getWidth(), this.getPinY() + this.getHeight());

    for (int innerLayer = Document.MIN_LAYER; innerLayer <= Document.MAX_LAYER; innerLayer++) {
      Integer layerKey = Integer.valueOf(innerLayer);
      List<AbstractBasicShapeImpl> innerLayerShapes = innerShapes.get(layerKey);
      if (innerLayerShapes != null) {
        for (AbstractBasicShapeImpl innerShape : innerLayerShapes) {
          bbox.createUnion(innerShape.getBoundingBox());
        }
      }
    }
    return bbox;
  }

  public Shape createNewInnerShape(String name) throws SvgExportException {
    return createNewInnerShape(name, Document.DEFAULT_LAYER);
  }

  public Shape createNewInnerShape(String name, int layer) throws SvgExportException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Creating new inner shape with master " + name + "in shape with id " + this.shapeId);
    }

    if (document.getMasterNameToMasterShapeMap().get(name) == null) {
      LOGGER.error("No shape with the master " + name + "is defined.");
      throw new MasterNotFoundException("The master shape with the unique name " + name + " does not exist.");
    }

    String innerShapeId = name + document.getShapeIdCount();
    document.incShapeIdCount();
    ShapeImpl newShape = new ShapeImpl(name, innerShapeId, document);
    newShape.setParentShape(this);
    document.applyMasterProperties(newShape);

    Integer layerKey = Integer.valueOf(layer);
    if (innerShapes.get(layerKey) == null) {
      innerShapes.put(layerKey, new LinkedList<AbstractBasicShapeImpl>());
    }

    innerShapes.get(layerKey).add(newShape);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Successfully created new inner shape with Id: " + newShape.getID());
    }

    return newShape;
  }

  public BasicShape createNewBasicInnerShape(String shapeType) throws SvgExportException {
    return createNewBasicInnerShape(shapeType, Document.DEFAULT_LAYER);
  }

  public BasicShape createNewBasicInnerShape(String shapeType, int layer) throws SvgExportException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Creating new basic inner shape with type " + shapeType + "in shape with Id: " + this.shapeId);
    }

    AbstractBasicShapeImpl newShape = null;
    String newShapeId = shapeType + document.getShapeIdCount();
    document.incShapeIdCount();

    if (shapeType.equalsIgnoreCase(SVG_BASIC_SHAPES.CIRCLE)) {
      newShape = new CircleShape(newShapeId, document);
    }
    else if (shapeType.equalsIgnoreCase(SVG_BASIC_SHAPES.PATH)) {
      newShape = new PathShape(newShapeId, document);
    }
    else if (shapeType.equalsIgnoreCase(SVG_BASIC_SHAPES.RECTANGLE)) {
      newShape = new RectangleShape(newShapeId, document);
    }
    else if (shapeType.equalsIgnoreCase(SVG_BASIC_SHAPES.TEXT)) {
      newShape = new TextShape(newShapeId, document);
    }
    else {
      LOGGER.error("The basic shape type " + shapeType + "is undefined.");
      throw new SvgExportException("The basic shape type " + shapeType + " is not defined.");
    }

    newShape.setParentShape(this);

    Integer layerKey = Integer.valueOf(layer);
    if (innerShapes.get(layerKey) == null) {
      innerShapes.put(layerKey, new LinkedList<AbstractBasicShapeImpl>());
    }

    innerShapes.get(layerKey).add(newShape);

    LOGGER.debug("Successfully created new basic inner shape with Id: " + newShape.getID());
    return newShape;
  }

  protected abstract void applySettingsToDom(Node domShape);

  public double getHeight() {
    return height;
  }

  public String getID() {
    return shapeId;
  }

  public String getMasterName() {
    return masterName;
  }

  public double getPinX() {
    return pinX;
  }

  public double getPinY() {
    return pinY;
  }

  public double getAbsolutePinX() {
    double xCor = this.getPinX();
    AbstractBasicShapeImpl parent = this.getParentShape();
    while (parent != null) {
      xCor = xCor + parent.getPinX();
      parent = parent.getParentShape();
    }
    return xCor;
  }

  public double getAbsolutePinY() {
    double yCor = this.getPinY();
    AbstractBasicShapeImpl parent = this.getParentShape();
    while (parent != null) {
      yCor = yCor + parent.getPinY();
      parent = parent.getParentShape();
    }
    return yCor;
  }

  public double getWidth() throws IllegalStateException {
    return width;
  }

  public void setAngle(double angle) {
    rotationAngle = angle;
  }

  public double getAngle() {
    return rotationAngle;
  }

  public void setPosition(double x, double y) {
    pinX = x;
    pinY = y;
  }

  public void setSize(double width, double height) {
    this.width = width;
    this.height = height;
  }

  public void addCSSClass(String uniqueClassName) throws SvgExportException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Adding CSS class " + uniqueClassName + " to shape classes.");
    }

    boolean found = false;
    for (SvgCssStyling styleclass : document.getCSSStyleClasses()) {
      if (styleclass.getCssClassName().matches(".*" + uniqueClassName + ".*")) {
        if (!shapeStyles.contains(styleclass.getCssClassName())) {
          shapeStyles.add(styleclass.getCssClassName());
        }
        found = true;
        break;
      }
    }
    if (!found) {
      LOGGER.error("The CSS class with the name " + uniqueClassName + " is undefined.");
      throw new SvgExportException("The CSS class with the name " + uniqueClassName + " is undefined.");
    }

  }

  public List<String> getShapeCSSClassNames() {
    return shapeStyles;
  }

  public boolean isCSSStylingEnabled() {
    return shapeCSSStylingEnabled;
  }

  public void setShapeCSSEnabled(boolean status) {
    shapeCSSStylingEnabled = status;
  }

  protected AbstractBasicShapeImpl getParentShape() {
    return this.parentShape;
  }

  protected void setParentShape(AbstractBasicShapeImpl parentShape) {
    this.parentShape = parentShape;
  }

  /**
   * The method flattens all internal layers of a certain sub level of a shape. In the resulting
   * list, the shapes are ordered increasingly with respect to their layer.
   * 
   * @return A flattened list of all internal shapes on the current level.
   */
  protected List<AbstractBasicShapeImpl> getInnerShapes() {
    List<AbstractBasicShapeImpl> flattenedInnerShapes = new LinkedList<AbstractBasicShapeImpl>();
    for (int layer = Document.MIN_LAYER; layer <= Document.MAX_LAYER; layer++) {
      Integer layerKey = Integer.valueOf(layer);
      if (innerShapes.get(layerKey) != null) {
        flattenedInnerShapes.addAll(innerShapes.get(layerKey));
      }
    }
    return flattenedInnerShapes;
  }

  public void setShapeStyles(List<String> shapeStyles) {
    this.shapeStyles = shapeStyles;
  }

  public SvgBaseStyling getDefaultStyle() {
    return defaultStyle;
  }

  public void setDefaultStyle(SvgBaseStyling defaultStyle) {
    this.defaultStyle = defaultStyle;
  }

  public boolean isBasicShape() {
    return this.basicShape;
  }

  protected String buildTransformationString() {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Building transformation string for shape with Id: " + this.getID());
    }

    adjustShapePositionForTransformation();

    // create the resulting transformation string
    StringBuffer result = new StringBuffer(30);

    result.append("matrix(1,0,0,1,");
    // Insert new X coordinate
    result.append(getPinX());
    // Adjust
    result.append(',');
    // Insert the new Y coordinate
    result.append(getPinY());
    // Close the matrix
    result.append(')');
    // Insert rotation
    if (getAngle() != 0) {
      result.append(" rotate(");
      result.append(getAngle());
      result.append(')');
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Successfully created transformation string for shape with Id " + this.getID());
    }
    return result.toString();
  }

  protected void adjustShapePositionForTransformation() {
    /*
     * If we're not at the first level (we deal with an inner shape) we adjust the shape size and
     * position according to those of its parent. We don't explicitly need a recursion (if we are
     * deeper than the first sub-level) as the order of shape creation implies that we have already
     * applied the scalings of the parent of this parent to this parent (and so on to the top of the
     * tree).
     */
    double parentScaleX = 1;
    double parentScaleY = 1;

    if (getParentShape() != null) {

      if (!getParentShape().isBasicShape()) {
        ShapeImpl parent = (ShapeImpl) getParentShape();
        if (parent.isScaleInnerShapesWithShape()) {
          parentScaleX = getParentShape().getWidth() / document.getMasterShapePropertiesMap().get(getParentShape().getMasterName()).getShapeWidth();

          parentScaleY = getParentShape().getHeight() / document.getMasterShapePropertiesMap().get(getParentShape().getMasterName()).getShapeHeight();
        }
      }

      setPosition(getPinX() * parentScaleX + getParentShape().getPinX(), getPinY() * parentScaleY + getParentShape().getPinY());

    }

    // Scale the shape accordingly to the scaling of the parent if selected
    if (!isBasicShape() && getParentShape() != null && !getParentShape().isBasicShape()) {
      ShapeImpl parent = (ShapeImpl) getParentShape();
      if (parent.isScaleInnerShapesWithShape()) {
        setSize(getWidth() * parentScaleX, getHeight() * parentScaleY);
      }
    }
  }

  public void setXLink(String url) {
    this.xLinkURL = url;
  }

  public String getXLink() {
    return this.xLinkURL;
  }

  public void setShapeLayer(int layer) throws SvgExportException {
    document.validateLayer(layer);
    AbstractBasicShapeImpl parent = this;
    int oldLayer = getShapeLayer();
    while (parent.getParentShape() != null) {
      parent.globalLayer = layer;
      parent = parent.getParentShape();
    }
    document.updateShapeLayer(parent, oldLayer);
  }

  public int getShapeLayer() {
    AbstractBasicShapeImpl parent = this;
    int layer = this.globalLayer;
    while (parent != null) {
      layer = parent.globalLayer;
      parent = parent.getParentShape();
    }
    return layer;
  }

  public void setInternalLayer(int layer) throws SvgExportException {
    document.validateLayer(layer);
    if (this.getParentShape() != null) {
      this.getParentShape().updateInternalLayer(this, layer);
    }
    this.internalLayer = layer;
  }

  public int getInternalLayer() {
    return this.internalLayer;
  }

  protected void updateInternalLayer(AbstractBasicShapeImpl shape, int newLayer) {
    this.innerShapes.get(Integer.valueOf(shape.getInternalLayer())).remove(shape);
    Integer newLayerKey = Integer.valueOf(newLayer);
    if (this.innerShapes.get(newLayerKey) == null) {
      this.innerShapes.put(newLayerKey, new LinkedList<AbstractBasicShapeImpl>());
    }
    this.innerShapes.get(newLayerKey).add(shape);
  }

  public void setBasicShape(boolean basicShape) {
    this.basicShape = basicShape;
  }

  public void setGlobalLayer(int globalLayer) {
    this.globalLayer = globalLayer;
  }

  public int getGlobalLayer() {
    return globalLayer;
  }

}
