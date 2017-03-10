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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.svg.SvgGraphicWriter;
import de.iteratec.svg.model.BasicShape;
import de.iteratec.svg.model.BasicShape.SVG_BASIC_SHAPES;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.MasterNotFoundException;
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.styling.SvgBaseStyling;
import de.iteratec.svg.styling.SvgCssStyling;


@SuppressWarnings("PMD.TooManyMethods")
public class DocumentImpl implements Document {

  private static final Logger                  LOGGER                         = Logger.getIteraplanLogger(DocumentImpl.class);

  private final org.w3c.dom.Document           document;
  private final org.w3c.dom.Node               documentRoot;
  private org.w3c.dom.Node                     cssStyleNode;

  private final Map<Integer, List<BasicShape>> generatedShapes;

  private Map<String, Node>                    mastersByName;

  private Map<String, MasterShapeProperties>   masterProperties;

  private List<String>                         masterNames;

  private int                                  shapeIdCount;

  private double                               pageWidth;

  private double                               pageHeight;

  private boolean                              addViewBox                     = false;

  private double                               viewBoxStartX;
  private double                               viewBoxStartY;
  private double                               viewBoxEndX;
  private double                               viewBoxEndY;

  private boolean                              cssShapeStyilingEnabled        = true;

  private boolean                              cssTextStylingEnabled          = true;

  private boolean                              documentFinalized              = false;

  private List<SvgCssStyling>                  cssStyles;

  private Map<String, SvgCssStyling>           cssNameToStyle;

  private static final String                  SVG_IDENTIFIER                 = "svg";

  private static final String                  SVG_NODE_BASIC                 = "g";
  private static final String                  SVG_NODE_PATTERN               = "pattern";

  private static final String                  ATTRIBUTE_NAME_ID              = "id";
  private static final String                  ATTRIBUTE_NAME_ORIGINAL_HEIGHT = "oheight";
  private static final String                  ATTRIBUTE_NAME_ORIGINAL_WIDTH  = "owidth";
  private static final String                  ATTRIBUTE_NAME_ORIGINAL_X      = "ox";
  private static final String                  ATTRIBUTE_NAME_ORIGINAL_Y      = "oy";
  private static final String                  ATTRIBUTE_NAME_STYLE           = "style";
  private static final String                  ATTRIBUTE_NAME_CLASS           = "class";

  protected static final String                ATTRIBUTE_VALUE_SHAPE_PROPS    = "shapeProperties";
  protected static final String                ATTRIBUTE_VALUE_TEXT_PROPS     = "textProperties";
  protected static final String                ATTRIBUTE_VALUE_TEXT_FIELD     = "textField";
  protected static final String                ATTRIBUTE_VALUE_IMAGE_FIELD    = "imageField";
  protected static final String                ATTRIBUTE_VALUE_TRANSFORM_NODE = "transformNode";

  public DocumentImpl(InputStream inputStream) throws IOException, SvgExportException {

    LOGGER.debug("Entered svg document constructor. \n Parsing document from template file.");

    // Load the document from the input stream
    String parser = XMLResourceDescriptor.getXMLParserClassName();
    SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
    String uri = SVG_IDENTIFIER;

    document = f.createDocument(uri, inputStream);

    documentRoot = document.getDocumentElement();

    generatedShapes = CollectionUtils.hashMap();
    generatedShapes.put(Integer.valueOf(Document.DEFAULT_LAYER), new LinkedList<BasicShape>());

    // Initialize the ID counter
    this.shapeIdCount = 0;

    LOGGER.debug("Template file successfully parsed. Reading document properties.");

    // Read the page properties
    readPageProperties();

    // Load the predefined CSS style sheet
    loadCSSStyles();

    // Load the master shapes
    loadMasterShapes();

    LOGGER.debug("Svg document initialization completed successfully.");

  }

  public void autoAdjustPageSize(double margin, boolean increaseOnly) throws SvgExportException {

    Rectangle2D contentBound = getBoundingBox();
    if (increaseOnly) {
      if (pageHeight < contentBound.getHeight() + contentBound.getMinX()) {
        pageHeight = contentBound.getHeight() + contentBound.getMinX() + margin;
      }
      if (pageWidth < contentBound.getWidth() + contentBound.getMinX()) {
        pageWidth = contentBound.getWidth() + contentBound.getMinX() + margin;
      }
    }
    else {
      pageHeight = contentBound.getHeight() + contentBound.getMinX() + margin;
      pageWidth = contentBound.getWidth() + contentBound.getMinX() + margin;
    }
  }

  public Shape createNewShape(String masterName) throws SvgExportException {
    return createNewShape(masterName, Document.DEFAULT_LAYER);
  }

  public Shape createNewShape(String masterName, int layer) throws SvgExportException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Creating a new shape with master: " + masterName);
    }

    validateLayer(layer);

    // Assure Master exists
    if (mastersByName.get(masterName) == null) {

      LOGGER.error("No master with name " + masterName + " is to be found in the document.");

      throw new MasterNotFoundException("The Master " + masterName + " does not exist.");
    }

    // Generate shapeId
    String shapeId = masterName + shapeIdCount;
    shapeIdCount++;

    // Create the shape
    ShapeImpl newShape = new ShapeImpl(masterName, shapeId, this);
    applyMasterProperties(newShape);

    addShapeToDocument(layer, newShape);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Successfulli created new shape with master " + masterName + " and Id " + newShape.getID());
    }

    return newShape;
  }

  public void addShapeToDocument(int layer, BasicShape shape) {
    Integer layerKey = Integer.valueOf(layer);

    if (generatedShapes.get(layerKey) == null) {
      generatedShapes.put(layerKey, new LinkedList<BasicShape>());
    }

    generatedShapes.get(layerKey).add(shape);
  }

  public BasicShape createNewBasicShape(String shapeType) throws SvgExportException {
    return createNewBasicShape(shapeType, Document.DEFAULT_LAYER);
  }

  public BasicShape createNewBasicShape(String shapeType, int layer) throws SvgExportException {

    validateLayer(layer);

    String shapeId = shapeType + shapeIdCount;
    shapeIdCount++;

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Creating new basic shape of type " + shapeType + " with Id: " + shapeId);
    }

    AbstractBasicShapeImpl newShape;
    if (shapeType.equalsIgnoreCase(SVG_BASIC_SHAPES.CIRCLE)) {
      newShape = new CircleShape(shapeId, this);
    }
    else if (shapeType.equalsIgnoreCase(SVG_BASIC_SHAPES.PATH)) {
      newShape = new PathShape(shapeId, this);
    }
    else if (shapeType.equalsIgnoreCase(SVG_BASIC_SHAPES.RECTANGLE)) {
      newShape = new RectangleShape(shapeId, this);
    }
    else if (shapeType.equalsIgnoreCase(SVG_BASIC_SHAPES.TEXT)) {
      newShape = new TextShape(shapeId, this);
    }
    else if (shapeType.equalsIgnoreCase(SVG_BASIC_SHAPES.PATTERN)) {
      newShape = new PatternShape(shapeId, this);
    }
    else {
      shapeIdCount--;

      LOGGER.error("Unknown shape type: " + shapeType);

      throw new SvgExportException("The basic shape type " + shapeType + " is not defined.");
    }

    addShapeToDocument(layer, newShape);
    return newShape;
  }

  public Rectangle2D getBoundingBox() throws SvgExportException {

    if (generatedShapes.size() == 0) {
      return new Rectangle2D.Double(0, 0, 0, 0);
    }
    Rectangle2D bbox = new Rectangle2D.Double(0, 0, 0, 0);
    for (Integer layerKey : generatedShapes.keySet()) {
      List<BasicShape> layerShapes = generatedShapes.get(layerKey);
      if (layerShapes != null) {
        for (BasicShape shape : layerShapes) {
          bbox.createUnion(shape.getBoundingBox());
        }
      }
    }
    return bbox;
  }

  public Node getMaster(String uniqueName) throws MasterNotFoundException {
    Node master = mastersByName.get(uniqueName);
    if (master == null) {

      LOGGER.error("Master with the unique name " + uniqueName + " could not be found.");

      throw new MasterNotFoundException("The master shape with name: " + uniqueName + " does not exist.");
    }
    return master;
  }

  public double getPageHeight() {
    return pageHeight;
  }

  public double getPageWidth() {
    return pageWidth;
  }

  public void save(File outputFile) throws IOException, SvgExportException {
    FileOutputStream outStream = new FileOutputStream(outputFile);
    try {
      SvgGraphicWriter.writeToSvg(this, outStream);
    } finally {
      if (outStream != null) {
        outStream.close();
      }
    }
  }

  public void setPageSize(double width, double height) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Setting page width to " + width + " and page height to " + height);
    }
    this.pageHeight = height;
    this.pageWidth = width;
  }

  public void setViewBox(double startX, double startY, double endX, double endY) {
    this.viewBoxStartX = startX;
    this.viewBoxStartY = startY;
    this.viewBoxEndX = endX;
    this.viewBoxEndY = endY;

    this.addViewBox = true;
  }

  public void setShapeCSSStylingEnabled(boolean status) {
    this.cssShapeStyilingEnabled = status;
  }

  public boolean isShapeCSSStylingEnabled() {
    return this.cssShapeStyilingEnabled;
  }

  public void setTextCSSStylingEnabled(boolean status) {
    this.cssTextStylingEnabled = status;
  }

  public boolean isTextCSSStylingEnabled() {
    return this.cssTextStylingEnabled;
  }

  public List<SvgCssStyling> getCSSStyleClasses() {
    return cssStyles;
  }

  public void createNewCSSClass(String uniqueName, Map<String, String> properties) throws SvgExportException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Trying to create new CSS class with name: " + uniqueName);
    }

    // Check if the class already exists
    if (cssNameToStyle.get(uniqueName) != null) {

      LOGGER.error("A CSS class with the unique name " + uniqueName + " is already defined for this document.");

      throw new SvgExportException("A CSS Class with the name " + uniqueName + " already exists.");
    }

    SvgCssStyling newStyle = new SvgCssStyling(uniqueName);
    for (Map.Entry<String, String> entry : properties.entrySet()) {
      newStyle.setAttribute(entry.getKey(), entry.getValue());
    }

    cssStyles.add(newStyle);
    cssNameToStyle.put(uniqueName, newStyle);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Successfully created new CSS class: " + uniqueName);
    }

  }

  public void finalizeDocument() throws SvgExportException {

    LOGGER.debug("Trying to finalize the document.");
    try {
      prepareForExport();
    } catch (Exception e) {

      LOGGER.error("Failed to transfer object data to the DOM tree. Document finalization failed.");
      throw new SvgExportException("An internal error has occured during DOM generation.", e);
    }

    this.documentFinalized = true;
  }

  protected void updateShapeLayer(AbstractBasicShapeImpl shape, int oldLayer) {

    generatedShapes.get(Integer.valueOf(oldLayer)).remove(shape);

    addShapeToDocument(shape.getGlobalLayer(), shape);
  }

  protected void validateLayer(int layer) throws SvgExportException {
    if (layer < Document.MIN_LAYER || layer > Document.MAX_LAYER) {
      LOGGER.error("Unsupported layer: " + layer + " .Only numbers between " + Document.MIN_LAYER + " and " + Document.MAX_LAYER + " are allowed.");
      throw new SvgExportException("Unsupported layer: " + layer + " .Only numbers between " + Document.MIN_LAYER + " and " + Document.MAX_LAYER
          + " are allowed.");
    }
  }

  // Private methods
  private void loadMasterShapes() throws SvgExportException {

    LOGGER.debug("Loading master shapes from the template file.");

    mastersByName = new HashMap<String, Node>();
    masterNames = new ArrayList<String>();
    masterProperties = new HashMap<String, MasterShapeProperties>();

    List<Node> masters = SvgDomUtils.loadMasterShapes(documentRoot);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("A total of " + masters.size() + " master shapes habe been found.");
    }

    for (Node master : masters) {
      String name = SvgDomUtils.getValueForAttribute(ATTRIBUTE_NAME_ID, master);

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Setting reference to master: " + name);
      }

      mastersByName.put(name, master);
      masterNames.add(name);
      readMasterShapeProperties(name);
    }

    LOGGER.debug("Done loading masters from template. Creating masters for the basic shape types.");

    createMastersForBasicShapes();

    // As we don't want to export the master shapes with the
    // resulting document and we already have copied them
    // we remove them from the document

    LOGGER.debug("Removing original master nodes from the DOM tree.");

    for (Node master : masters) {
      documentRoot.removeChild(master);
    }

    LOGGER.debug("Master shape loading successfully completed.");

  }

  private void createMastersForBasicShapes() {

    LOGGER.debug("Creating masters for basic shapes.");

    // Circle
    createMasterForBasicShape(SvgDomUtils.createNodeWithName(SVG_NODE_BASIC, documentRoot, document), SVG_BASIC_SHAPES.CIRCLE, "circle");
    // Rectangle
    createMasterForBasicShape(SvgDomUtils.createNodeWithName(SVG_NODE_BASIC, documentRoot, document), SVG_BASIC_SHAPES.RECTANGLE, "rect");
    // Path
    createMasterForBasicShape(SvgDomUtils.createNodeWithName(SVG_NODE_BASIC, documentRoot, document), SVG_BASIC_SHAPES.PATH, "path");
    // Text
    createMasterForBasicShape(SvgDomUtils.createNodeWithName(SVG_NODE_BASIC, documentRoot, document), SVG_BASIC_SHAPES.TEXT, "text");
    // Pattern
    createMasterForBasicShape(SvgDomUtils.createNodeWithName(SVG_NODE_PATTERN, documentRoot, document), SVG_BASIC_SHAPES.PATTERN, "g");

    LOGGER.debug("Successfully created masters for basic shapes.");
  }

  private void createMasterForBasicShape(Node basicMaster, String basicShapeName, String nodeName) {
    SvgDomUtils.setOrCreateAttributeForNode(basicMaster, ATTRIBUTE_NAME_ID, basicShapeName);

    Node basicTransform = SvgDomUtils.createNodeWithName(SVG_NODE_BASIC, basicMaster, document);
    SvgDomUtils.setOrCreateAttributeForNode(basicTransform, ATTRIBUTE_NAME_ID, ATTRIBUTE_VALUE_TRANSFORM_NODE);

    Node shapeProperties = SvgDomUtils.createNodeWithName(nodeName, basicTransform, document);
    SvgDomUtils.setOrCreateAttributeForNode(shapeProperties, ATTRIBUTE_NAME_ID, ATTRIBUTE_VALUE_SHAPE_PROPS);

    mastersByName.put(basicShapeName, basicMaster);
    masterNames.add(basicShapeName);
    documentRoot.removeChild(basicMaster);
  }

  private void readMasterShapeProperties(String masterShapeName) throws SvgExportException {

    // See Class MasterShapeProperties for more.
    MasterShapeProperties props = new MasterShapeProperties();

    // Read the styling properties of the shape
    loadMasterShapeStylingFromDOMShape(props, masterShapeName);

    // Read the styling properties of the text content
    loadMasterShapeTextStylingFromDOMShape(props, masterShapeName);

    // Read shape dimensions
    loadShapeDimensionsFromDOMShape(props, masterShapeName);

    // Store the properties
    masterProperties.put(masterShapeName, props);

  }

  private void loadCSSStyles() throws SvgExportException {

    LOGGER.debug("Start loading CSS style definitions from template.");

    cssStyles = new ArrayList<SvgCssStyling>();
    cssNameToStyle = new HashMap<String, SvgCssStyling>();

    cssStyleNode = SvgDomUtils.getCSSStylingNode(documentRoot);

    if (cssStyleNode != null) {

      LOGGER.debug("CSS styling node found in document.");

      String cssClassesString = cssStyleNode.getTextContent();
      cssStyleNode.setTextContent("");

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Trying to decode CSS style definitons from template. The CSS string is: " + cssClassesString);
      }

      List<String> classStrings = SvgCssStyling.decodeCSSClassesFromString(cssClassesString);

      for (String cssClass : classStrings) {

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Splitting CSS class name from definition for string: " + cssClass);
        }

        String[] particles = cssClass.split("\\Q{\\E");

        if (particles.length < 2) {

          LOGGER.error("CSS style definition is too short.");

          throw new SvgExportException("CSS Style definition is too short to represent a valid style.");
        }

        SvgCssStyling st = SvgCssStyling.createCssClassFromString(cssClass);
        cssStyles.add(st);
        cssNameToStyle.put(st.getCssClassName(), st);

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Successfully decoded CSS class definition: " + cssClass);
        }

      }

      LOGGER.debug("Successfully loaded CSS classes from template.");

    }
    else {

      LOGGER.debug("No CSS data was found in the template. CSS support is set to disabled.");
      cssShapeStyilingEnabled = false;
      cssTextStylingEnabled = false;
    }
  }

  protected Map<String, Node> getMasterNameToMasterShapeMap() {
    return mastersByName;
  }

  protected Map<String, MasterShapeProperties> getMasterShapePropertiesMap() {
    return masterProperties;
  }

  public int getShapeIdCount() {
    return shapeIdCount;
  }

  public void incShapeIdCount() {
    shapeIdCount++;
  }

  public void setShapeIdCount(int count) {
    this.shapeIdCount = count;
  }

  protected void applyMasterProperties(ShapeImpl shape) {

    // Retrieve props
    MasterShapeProperties props = masterProperties.get(shape.getMasterName());

    // Apply positioning properties
    shape.setPosition(props.getShapeX(), props.getShapeY());
    shape.setSize(props.getShapeWidth(), props.getShapeHeight());

    // Apply shape styling properties
    shape.setDefaultStyle(props.getDefaultStyle().deepCopy());
    List<String> classes = new ArrayList<String>();
    for (String s : props.getShapeStyles()) {
      classes.add(s);
    }
    shape.setShapeStyles(classes);

    // Apply text styling properties
    shape.setDefaultTextStyle(props.getDefaultTextStyle().deepCopy());
    classes = new ArrayList<String>();
    for (String s : props.getTextStyles()) {
      classes.add(s);
    }
    shape.setTextStyles(classes);

  }

  /**
   * This method creates DOM shapes from the internally-generated Bean-like shape containers and
   * attaches them to the DOM Document
   */
  private void prepareForExport() {

    LOGGER.debug("Transfering object data to the DOM tree.");

    // Apply page properties to the DOM
    SvgDomUtils.setOrCreateAttributeForNode(documentRoot, "height", Double.toString(pageHeight));
    SvgDomUtils.setOrCreateAttributeForNode(documentRoot, "width", Double.toString(pageWidth));

    // Apply viewBox to the DOM
    if (this.addViewBox) {
      StringBuffer buffer = new StringBuffer();
      buffer.append(Double.toString(this.viewBoxStartX));
      buffer.append(' ');
      buffer.append(Double.toString(this.viewBoxStartY));
      buffer.append(' ');
      buffer.append(Double.toString(this.viewBoxEndX));
      buffer.append(' ');
      buffer.append(Double.toString(this.viewBoxEndY));

      SvgDomUtils.setOrCreateAttributeForNode(documentRoot, "viewBox", buffer.toString());
    }

    LOGGER.debug("Successfully inserted document dimensions into the DOM tree.");

    // Apply CSS styling if enabled
    insertCSSStylesInDocument();

    LOGGER.debug("Transfering shape settings to the DOM tree.");

    // For each shape transform the bean properties into DOM code.
    // Then attach to the root or to the parent shape
    for (int currentLayer = Document.MIN_LAYER; currentLayer <= Document.MAX_LAYER; currentLayer++) {

      Integer layerKey = Integer.valueOf(currentLayer);
      List<BasicShape> layerShapes = generatedShapes.get(layerKey);

      if (layerShapes != null) {
        for (BasicShape shape : layerShapes) {
          applyShapeSettings(shape);
        }
      }

    }
    LOGGER.debug("Successfully transfered shape properties to the DOM shapes.");
  }

  private void applyShapeSettings(BasicShape shape) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Transfering settings for shape with Id: " + shape.getID());
    }

    AbstractBasicShapeImpl abstractBasicShape = (AbstractBasicShapeImpl) shape;

    Node domShape = mastersByName.get(abstractBasicShape.getMasterName()).cloneNode(true);

    // If basic -> apply basic shape specifics
    abstractBasicShape.applySettingsToDom(domShape);
    // Apply all to the basic shape
    applyCommonShapeSettingsToDom(abstractBasicShape, domShape);

    // If advanced -> apply those too
    if (!abstractBasicShape.isBasicShape()) {
      ShapeImpl advShape = (ShapeImpl) abstractBasicShape;
      applyAdvancedShapeSettingsToDOM(advShape, domShape);
    }

    // Change the shape properties id to be conform with the svg specification
    SvgDomUtils.setOrCreateAttributeForNode(SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_SHAPE_PROPS), ATTRIBUTE_NAME_ID,
        ATTRIBUTE_VALUE_SHAPE_PROPS + shapeIdCount++);

    if (abstractBasicShape.getParentShape() == null) {
      manageXLinksAndAppendToDom(abstractBasicShape, documentRoot, domShape);
    }
    else {
      // If the text is not to be resized with the shape it also has to appear on top of all inner shapes.
      Element parentNode = ((AbstractDocument) document).getChildElementById(documentRoot, abstractBasicShape.getParentShape().getID());
      Node textNode = SvgDomUtils.getNodeWithId(parentNode, ATTRIBUTE_VALUE_TEXT_PROPS);
      if (!abstractBasicShape.getParentShape().isBasicShape() && textNode != null) {
        ShapeImpl advParentShape = (ShapeImpl) abstractBasicShape.getParentShape();
        if (!advParentShape.isResizeTextWithShape()) {
          parentNode.insertBefore(domShape, textNode.getParentNode());
        }
        else {
          manageXLinksAndAppendToDom(abstractBasicShape, parentNode, domShape);
        }
      }
      else {
        manageXLinksAndAppendToDom(abstractBasicShape, parentNode, domShape);
      }
    }

    // Take care of the child shapes.
    for (AbstractBasicShapeImpl childShape : abstractBasicShape.getInnerShapes()) {
      applyShapeSettings(childShape);
    }
    adjustIdsForTextElements(domShape);
  }

  /**
   * Checks if the shape to be inserted is marked as a xlink reference. If this is the case, a
   * corresponding xlink node is added. After that the shape is inserted into the document.
   * 
   * @param shape
   *          The shape that holds information about the linking.
   * @param qualifiedParent
   *          The parent of the selected shape.
   * @param domShape
   *          The shape to insert.
   */
  private void manageXLinksAndAppendToDom(AbstractBasicShapeImpl shape, Node qualifiedParent, Node domShape) {

    if (shape.getXLink() == null || shape.getXLink().length() == 0) {
      qualifiedParent.appendChild(domShape);
    }
    else {
      Element xLinkElement = document.createElement("a");

      SvgDomUtils.setOrCreateAttributeForNode(xLinkElement, "xlink:href", shape.getXLink());
      SvgDomUtils.setOrCreateAttributeForNode(xLinkElement, "xlink:show", "new");

      qualifiedParent.appendChild(xLinkElement);
      xLinkElement.appendChild(domShape);
    }
  }

  private void adjustIdsForTextElements(Node domShape) {
    Node textNode = SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_TEXT_PROPS);
    // Change the element id's to be conform with the svg specification:
    if (textNode != null) {
      SvgDomUtils.setOrCreateAttributeForNode(textNode, ATTRIBUTE_NAME_ID, ATTRIBUTE_VALUE_TEXT_PROPS + shapeIdCount++);
      Node textField = SvgDomUtils.getNodeWithId(textNode, ATTRIBUTE_VALUE_TEXT_FIELD);
      if (textField != null) {
        SvgDomUtils.setOrCreateAttributeForNode(textField, ATTRIBUTE_NAME_ID, ATTRIBUTE_VALUE_TEXT_FIELD + shapeIdCount++);
      }
    }
  }

  private void applyCommonShapeSettingsToDom(AbstractBasicShapeImpl shape, Node domShape) {

    LOGGER.debug("Applying general shape settings to DOM.");

    // Apply the ID:
    SvgDomUtils.setOrCreateAttributeForNode(domShape, ATTRIBUTE_NAME_ID, shape.getID());

    // Apply shape size, position and rotation
    Node transformNode = SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_TRANSFORM_NODE);
    String transformationString = shape.buildTransformationString();
    SvgDomUtils.setOrCreateAttributeForNode(transformNode, "transform", transformationString);
    // Change the element id to be conform with the svg specification
    SvgDomUtils.setOrCreateAttributeForNode(transformNode, ATTRIBUTE_NAME_ID, ATTRIBUTE_VALUE_TRANSFORM_NODE + shapeIdCount++);

    // Apply shape styling properties
    String shapeStyles = shape.getDefaultStyle().generateStylingString();
    String shapeCSSClasses = SvgCssStyling.buildCSSClassesString(shape.getShapeCSSClassNames());

    if (shape.isCSSStylingEnabled()) {
      SvgDomUtils.setOrCreateAttributeForNode(SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_SHAPE_PROPS), ATTRIBUTE_NAME_STYLE, "");
      SvgDomUtils
          .setOrCreateAttributeForNode(SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_SHAPE_PROPS), ATTRIBUTE_NAME_CLASS, shapeCSSClasses);
    }
    else {
      SvgDomUtils.setOrCreateAttributeForNode(SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_SHAPE_PROPS), ATTRIBUTE_NAME_STYLE, shapeStyles);
      SvgDomUtils.setOrCreateAttributeForNode(SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_SHAPE_PROPS), ATTRIBUTE_NAME_CLASS, "");
    }

    LOGGER.debug("Successfully applied shape id, position and styling.");

  }

  private void applyAdvancedShapeSettingsToDOM(Shape shape, Node domShape) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Applying shape properties for advanved shape with Id: " + shape.getID());
    }

    applyTextShapeSettingsToDOM(shape, domShape);
    applyImageShapeSettingsToDOM(shape, domShape);

    LOGGER.debug("Advanced shape properties successfully transfered to DOM.");
  }

  private void applyTextShapeSettingsToDOM(Shape shape, Node domShape) {
    // Set the text
    if (!shape.isBoundTextInShape()) {
      // No textBox simulation
      Node textNode = SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_TEXT_FIELD);
      if (textNode != null) {
        textNode.setTextContent(shape.getTextFieldValue());
      }
    }
    else {
      // TODO see if more of the text thing can be left aside...
      Node textRootNode = SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_TEXT_PROPS);
      if (textRootNode != null) {
        double fontSize = shape.getDefaultTextStyle().getDoubleAttributeValue(SvgBaseStyling.FONT_SIZE);
        List<String> resultLines = AdvancedTextHelper.buildTextBoxLines(shape.getTextFieldValue(), fontSize,
            AdvancedTextHelper.POINT_TO_UNIT_CONSTANT, shape.getWidth());

        double shapeCenterY;
        double xCor = 0;

        // Calculate coordinates
        if (!shape.isResizeTextWithShape()) {
          shapeCenterY = shape.getHeight() * 0.5;
          xCor = shape.getWidth() * shape.getTextXPlacement();
          shape.setTextPosition(shape.getPinX(), shape.getPinY());
        }
        else {
          shapeCenterY = masterProperties.get(shape.getMasterName()).getShapeHeight() / 2;
          xCor = masterProperties.get(shape.getMasterName()).getShapeWidth() / 2;
        }
        double numberOfLines = resultLines.size();
        double lineHeight = fontSize;// * AdvancedTextHelper.POINT_TO_UNIT_CONSTANT;
        double lineSpacing = shape.getLineSpacing();// * AdvancedTextHelper.POINT_TO_UNIT_CONSTANT;
        double firstLineY = shapeCenterY + lineHeight / 2 - (lineHeight * (numberOfLines - 1) + lineSpacing * (numberOfLines - 1)) / 2;

        // Create the new "textLines"
        double yCor = 0;
        Node tspanNode = SvgDomUtils.getNodeWithId(textRootNode, ATTRIBUTE_VALUE_TEXT_FIELD);
        Node textLine;
        int count = 0;

        for (String line : resultLines) {
          if (line.length() != 0) {
            yCor = firstLineY + count * (lineHeight + lineSpacing);

            textLine = tspanNode.cloneNode(true);
            textRootNode.appendChild(textLine);
            textLine.setTextContent(line);

            SvgDomUtils.setOrCreateAttributeForNode(textLine, ATTRIBUTE_NAME_ID, "tspan" + shapeIdCount++);
            SvgDomUtils.setOrCreateAttributeForNode(textLine, "x", Double.toString(xCor));
            SvgDomUtils.setOrCreateAttributeForNode(textLine, "y", Double.toString(yCor));
            count++;
          }
        }
        // Set the coordinates of the textRootNode
        SvgDomUtils.setOrCreateAttributeForNode(textRootNode, "x", Double.toString(shape.getWidth() / 2));
        SvgDomUtils.setOrCreateAttributeForNode(textRootNode, "y", Double.toString(firstLineY));
        textRootNode.removeChild(tspanNode);
      }
    }

    applyTextStylingProperties(shape, domShape);
  }

  private void applyImageShapeSettingsToDOM(Shape shape, Node domShape) {
    Node imageNode = SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_IMAGE_FIELD);
    if (imageNode != null) {
      SvgDomUtils.setOrCreateAttributeForNode(imageNode, "xlink:href", shape.getImageLink());
      SvgDomUtils.setOrCreateAttributeForNode(imageNode, "width", Double.toString(shape.getImageWidth()));
      SvgDomUtils.setOrCreateAttributeForNode(imageNode, "height", Double.toString(shape.getImageHeight()));
    }
  }

  private void applyTextStylingProperties(Shape shape, Node domShape) {
    Node textPropsNode = SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_TEXT_PROPS);

    String textStyles = shape.getDefaultTextStyle().generateStylingString();
    String textClasses = SvgCssStyling.buildCSSClassesString(shape.getShapeTextCSSClassNames());
    if (textPropsNode != null) {
      if (shape.isCSSTextStylingEnabled()) {
        SvgDomUtils.setOrCreateAttributeForNode(textPropsNode, ATTRIBUTE_NAME_CLASS, textClasses);
        SvgDomUtils.setOrCreateAttributeForNode(textPropsNode, ATTRIBUTE_NAME_STYLE, "");
      }
      else {
        SvgDomUtils.setOrCreateAttributeForNode(textPropsNode, ATTRIBUTE_NAME_STYLE, textStyles);
        SvgDomUtils.setOrCreateAttributeForNode(textPropsNode, ATTRIBUTE_NAME_CLASS, "");
      }
    }
    if (!shape.isResizeTextWithShape()) {
      applyTextResizingSettingsToDom(shape, domShape);
    }
  }

  private void applyTextResizingSettingsToDom(Shape shape, Node domShape) {
    // Apply text resizing setting for advanced shapes

    Node textRootNode = SvgDomUtils.getNodeWithId(domShape, ATTRIBUTE_VALUE_TEXT_PROPS);
    if (textRootNode != null) {
      Element textTransformElement = SvgDomUtils.createNodeWithName(SVG_NODE_BASIC, domShape, document);
      textTransformElement.appendChild(textRootNode);
      String translateString = "translate(" + (shape.getTextPinX()) + "," + (shape.getTextPinY()) + ")";
      String transformString = translateString + "rotate(" + shape.getTextAngle() + ")";
      SvgDomUtils.setOrCreateAttributeForNode(textTransformElement, "transform", transformString);
    }
  }

  private void readPageProperties() {

    LOGGER.debug("Reading page properties.");

    String heightStr = SvgDomUtils.getValueForAttribute("height", documentRoot);
    String widthStr = SvgDomUtils.getValueForAttribute("width", documentRoot);

    if (heightStr == null || widthStr == null) {
      pageHeight = 297;
      pageWidth = 210;

      LOGGER.debug("The page dimensions were not defined in the template. Using default page dimensions.");
      return;
    }

    try {
      pageHeight = Double.parseDouble(heightStr);
      pageWidth = Double.parseDouble(widthStr);
    } catch (Exception e) {
      // Set default values for page height and width -> DIN A4 Standard
      pageHeight = 297;
      pageWidth = 210;
      LOGGER.debug("The page dimensions in the template were not nummeric. Using default page dimensions.");

    }

    LOGGER.debug("Page properties loaded successfully. ");

  }

  private void loadMasterShapeStylingFromDOMShape(MasterShapeProperties masterProps, String masterShapeName) throws SvgExportException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Loading master shape style properties for master " + masterShapeName);
    }

    Node shapePropsNode = SvgDomUtils.getNodeWithId(mastersByName.get(masterShapeName), ATTRIBUTE_VALUE_SHAPE_PROPS);

    if (shapePropsNode == null) {
      LOGGER.error("Shape proeprties node is undefined for master shape " + masterShapeName);
      throw new SvgExportException("Illegal MasterShape state detected: A master without shapeProperties Node");
    }

    // Load the style node from the DOM
    String styleContent = SvgDomUtils.getValueForAttribute(ATTRIBUTE_NAME_STYLE, shapePropsNode);
    if (styleContent != null) {
      SvgBaseStyling styling = new SvgBaseStyling();
      styling.addAttributes(styleContent);
      masterProps.setDefaultStyle(styling);
    }

    // Load the CSS props form the DOM
    String classesStr = SvgDomUtils.getValueForAttribute(ATTRIBUTE_NAME_CLASS, shapePropsNode);
    List<String> shapeStyleClasses = new ArrayList<String>();

    if (classesStr != null) {
      String[] classes = classesStr.split("\\s");
      for (String classe : classes) {
        if (cssNameToStyle.get(classe.trim()) != null) {
          shapeStyleClasses.add(cssNameToStyle.get(classe.trim()).getCssClassName());
        }
      }
    }
    masterProps.setShapeStyles(shapeStyleClasses);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Shape properties for master " + masterShapeName + "loaded successfully.");
    }
  }

  private void loadMasterShapeTextStylingFromDOMShape(MasterShapeProperties masterProps, String masterShapeName) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Loading text styling properties for master shape: " + masterShapeName);
    }

    Node textPropsNode = SvgDomUtils.getNodeWithId(mastersByName.get(masterShapeName), ATTRIBUTE_VALUE_TEXT_PROPS);
    if (textPropsNode != null) {

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Text properties node found in master: " + masterShapeName + ". Loading text styles attribute.");
      }

      // Load the style node from the DOM
      String styleContent = SvgDomUtils.getValueForAttribute(ATTRIBUTE_NAME_STYLE, textPropsNode);
      if (styleContent != null) {
        SvgBaseStyling textStyling = new SvgBaseStyling();
        textStyling.addAttributes(styleContent);
        masterProps.setDefaultTextStyle(textStyling);

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Text styles attribute successfully loaded for master: " + masterShapeName);
        }
      }
      else {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("No text styles attribute was found for the master: " + masterShapeName);
        }
      }

      // Load CSS props from the DOM
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Loading CSS class references for the text properties of master: " + masterShapeName);
      }

      String classesStr = SvgDomUtils.getValueForAttribute(ATTRIBUTE_NAME_CLASS, textPropsNode);
      List<String> shapeTextClasses = new ArrayList<String>();

      if (classesStr != null) {
        String[] classes = classesStr.split("\\s");
        for (String classe : classes) {
          if (cssNameToStyle.get(classe.trim()) != null) {
            shapeTextClasses.add(cssNameToStyle.get(classe.trim()).getCssClassName());
          }
        }

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("CSS class references for the text properties of master " + masterShapeName + "loaded successfully.");
        }
      }
      else {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("No CSS class references were found for the text properties of master: " + masterShapeName);
        }
      }
      masterProps.setTextStyles(shapeTextClasses);
    }
    else {

      LOGGER.debug("No text properties node found in the DOM tree. Text properties loading completed.");
    }
  }

  private void loadShapeDimensionsFromDOMShape(MasterShapeProperties masterProps, String masterShapeName) throws SvgExportException {

    /*
     * The original shape position and size are stored in the transformation node under the
     * attributes oheight, owidth, ox and oy. We need this, as for some shapes it is not possible
     * (or too complicated) to simply read the dimensions from the main shape ( for example in the
     * case of a circle we will have to explicitly know the shape is a circle and than calculate the
     * dimensions).
     */

    LOGGER.debug("Loading predefined master shape dimensions from the DOM tree.");

    Node transformNode = SvgDomUtils.getNodeWithId(mastersByName.get(masterShapeName), ATTRIBUTE_VALUE_TRANSFORM_NODE);

    if (transformNode == null) {

      LOGGER.error("No transformation node has beed found for the master: " + masterShapeName);

      throw new SvgExportException("There is no transformNode defined for the MasterShape: " + masterShapeName);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Loading size and position parameters for master " + masterShapeName);
    }

    String height = SvgDomUtils.getValueForAttribute(ATTRIBUTE_NAME_ORIGINAL_HEIGHT, transformNode);
    String width = SvgDomUtils.getValueForAttribute(ATTRIBUTE_NAME_ORIGINAL_WIDTH, transformNode);
    String x = SvgDomUtils.getValueForAttribute(ATTRIBUTE_NAME_ORIGINAL_X, transformNode);
    String y = SvgDomUtils.getValueForAttribute(ATTRIBUTE_NAME_ORIGINAL_Y, transformNode);

    if (height == null || width == null || x == null || y == null) {

      LOGGER.error("The size of position parameters of the master " + masterShapeName + " have not beed found.");

      throw new SvgExportException("The size and position parameters of the MasterShape " + masterShapeName
          + "are not defined in the transform Node.");
    }

    try {
      masterProps.setShapeHeight(Double.parseDouble(SvgBaseStyling.decodeSizeAttribute(height)));
      masterProps.setShapeWidth(Double.parseDouble(SvgBaseStyling.decodeSizeAttribute(width)));
      masterProps.setShapeX(Double.parseDouble(SvgBaseStyling.decodeSizeAttribute(x)));
      masterProps.setShapeY(Double.parseDouble(SvgBaseStyling.decodeSizeAttribute(y)));
    } catch (NumberFormatException ex) {

      LOGGER
          .error("The size and position parameters of the master " + masterShapeName + " could not be parsed. Maybe their values are not nummeric.");

      throw new SvgExportException("The size and position parameters of the master shape " + masterShapeName
          + " could not be parsed. Maybe the values are not nummeric.", ex);
    }

    LOGGER.debug("Removind size and position attributes from the DOM tree.");

    // TODO use a SvgDomUtils method that is secured against null pointers
    transformNode.getAttributes().removeNamedItem(ATTRIBUTE_NAME_ORIGINAL_X);
    transformNode.getAttributes().removeNamedItem(ATTRIBUTE_NAME_ORIGINAL_Y);
    transformNode.getAttributes().removeNamedItem(ATTRIBUTE_NAME_ORIGINAL_WIDTH);
    transformNode.getAttributes().removeNamedItem(ATTRIBUTE_NAME_ORIGINAL_HEIGHT);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Successfully loaded size and position properties from the DOM tree for master: " + masterShapeName);
    }

  }

  private void insertCSSStylesInDocument() {

    LOGGER.debug("Inserting CDATA section containing the CSS style definitions into the DOM tree.");

    if (cssStyleNode == null) {
      return;
    }

    StringBuffer buffer = new StringBuffer();
    for (SvgCssStyling st : cssStyles) {
      buffer.append(st.generateCSSClassString());
      buffer.append('\n');
    }
    CDATASection cdata = document.createCDATASection(buffer.toString());
    cssStyleNode.appendChild(cdata);

    LOGGER.debug("Successfully inserted CSS style definitons into the DOM tree.");

  }

  public MasterShapeProperties getMasterShapeProperties(String masterName) {
    if (getMasterShapePropertiesMap().containsKey(masterName)) {
      return getMasterShapePropertiesMap().get(masterName);
    }
    else {
      return null;
    }
  }

  public boolean isFinalized() {
    return documentFinalized;
  }

  public org.w3c.dom.Document getDocument() {
    return document;
  }
}
