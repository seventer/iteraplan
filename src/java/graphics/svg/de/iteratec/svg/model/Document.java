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
package de.iteratec.svg.model;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import de.iteratec.svg.model.impl.MasterShapeProperties;
import de.iteratec.svg.styling.SvgCssStyling;


public interface Document {

  /**
   * The lowest layer on which shapes can be attached into the document.The constant is also used
   * for the internal layers of a shape.
   */
  int MIN_LAYER     = 0;

  /**
   * The highest layer on which shapes can be attached in into the document.The constant is also
   * used for the internal layers of a shape.
   */
  int MAX_LAYER     = 10;

  /**
   * The default shape layer. If no other layer is specified the default layer is taken for a
   * shape.The setting also applies for the internal layers of a shape.
   */
  int DEFAULT_LAYER = 5;

  /**
   * Retrieves a master from the document stencils by its unique name.
   * 
   * @param uniqueName
   *          The unique name of the master in the SVG document
   * @return The SVG model class representing the master.
   * @throws MasterNotFoundException
   *           Iff the uniqueName provided does not identify a master in the document.
   */
  Node getMaster(String uniqueName) throws MasterNotFoundException;

  /**
   * Saves the document to the given file.
   * 
   * @param outputFile
   *          The file to write to.
   * @throws IOException
   *           Iff the file can not be written.
   */
  void save(File outputFile) throws IOException, SvgExportException;

  /**
   * Enables the use of CSS styling for the shapes. If enabled the properties of the shapes will be
   * stored as generalized CSS classes. Otherwise the properties of the shapes will be stored as in
   * the style attribute of each shape.The default option is enabled, unless no CSS can be read from
   * the document template.
   * 
   * @param status
   */
  void setShapeCSSStylingEnabled(boolean status);

  /**
   * Retrieves the current global (for this document) state of the shape CSS styling.
   * 
   * @return true if global css styling is on and false otherwise.
   */
  boolean isShapeCSSStylingEnabled();

  /**
   * Enables the use of CSS styling for the texts. If enabled the properties of the shapes will be
   * stored as generalized CSS classes. Otherwise the properties of the shapes will be stored as in
   * the style attribute of each shape. The default option is enabled, unless no CSS can be read
   * from the document template.
   * 
   * @param status
   */
  void setTextCSSStylingEnabled(boolean status);

  /**
   * Retrieves the current global (for this document) state of the text CSS styling.
   * 
   * @return true if global text css styling is on and false otherwise.
   */
  boolean isTextCSSStylingEnabled();

  /**
   * Adds {@code shape} to the document at the given layer
   * @param layer
   *          layer the shape is to be added to
   * @param shape
   *          shape to be added
   */
  void addShapeToDocument(int layer, BasicShape shape);

  /**
   * Creates a new shape based on the master shape with the given name.
   * 
   * @param masterName
   *          The name of the master shape in the SVG template used.
   * @return A new shape based on this master shape.
   * @throws SvgExportException
   *           Iff no matching master can be found or CSS problems occur.
   */
  Shape createNewShape(String masterName) throws SvgExportException;

  /**
   * Creates a new shape based on the master shape with the given name.
   * 
   * @param masterName
   *          The name of the master shape in the SVG template used.
   * @param layer
   *          The layer in document that this shape should belong to.
   * @return A new shape based on this master shape.
   * @throws SvgExportException
   *           Iff no matching master can be found or CSS problems occur.
   */
  Shape createNewShape(String masterName, int layer) throws SvgExportException;

  /**
   * Creates a new basic shape with the specified type/master.
   * 
   * @param masterName
   *          The name of the shape master. Must be one of those in SVG_BASIC_SHAPES
   * @return A fully initialized new shape of the specified kind.
   * @throws SvgExportException
   */
  BasicShape createNewBasicShape(String masterName) throws SvgExportException;

  /**
   * Creates a new basic shape with the specified type/master.
   * 
   * @param masterName
   *          The name of the shape master. Must be one of those in SVG_BASIC_SHAPES
   * @param layer
   *          The layer in document that this shape should belong to.
   * @return A fully initialized new shape of the specified kind.
   * @throws SvgExportException
   */
  BasicShape createNewBasicShape(String masterName, int layer) throws SvgExportException;

  /**
   * Sets the dimensions of the page.
   * 
   * @param width
   *          The new width of the page
   * @param height
   *          The new height of the page
   */
  void setPageSize(double width, double height);

  /**
   * Optionally adds a view box to the root element of the SVG document. For more information on the
   * <b>viewBox</b> attribute, see the SVG Specification.
   * 
   * @param startX
   *          The start x-coordinate of the view box.
   * @param startY
   *          The start y-coordinate of the view box.
   * @param endX
   *          The end x-coordinate of the view box.
   * @param endY
   *          The end y-coordinate of the view box.
   */
  void setViewBox(double startX, double startY, double endX, double endY);

  /**
   * The width of the page.
   * 
   * @return The width of the page.
   * @throws IllegalStateException
   *           iff the page has no width attached or it can not be parsed
   */
  double getPageWidth();

  /**
   * The height of the page.
   * 
   * @return The height of the page.
   * @throws IllegalStateException
   *           iff the page has no height attached or it can not be parsed
   */
  double getPageHeight();

  /**
   * {@code shapeIdCount} is incremented each time a new shape is created and used
   * to create unique ids for each shape.
   * @return {@code shapeIdCount}
   */
  int getShapeIdCount();

  /**
   * Manually increments {@code shapeIdCount} by 1, see also {@link #getShapeIdCount()}
   */
  void incShapeIdCount();

  /**
   * Manually sets {@code shapeIdCount} to {@code count}, see also {@link #getShapeIdCount()}.
   * Used by composite graphic creation.
   * @param count
   *          value to set
   */
  void setShapeIdCount(int count);

  /**
   * Calculates the bounding box of the shape. This is the smallest rectangle all shapes on the page
   * fit into. All the restrictions documented on Shape.getBoundingBox() apply. Shapes for which no
   * bounding box can be calculated (i.e. for which the method throws an IllegalStateException) are
   * ignored -- they are just assumed not to have a bounding box. If no shape has a bounding box,
   * the method throws an exception itself. TODO this could be generic for ShapeContainer, which
   * would change the semantics for the Shape class, though
   * 
   * @return the bounding box of the underlying shape in SVG's coordinate space. Can not be null.
   * @throws IllegalStateException
   *           iff there is no shape on the page or non of the shapes has a bounding box.
   */
  Rectangle2D getBoundingBox() throws SvgExportException;

  /**
   * Adjusts the page size so it fits the existing shapes. The correctness of this function depends
   * on the correctness of getBoundingBox(). Additionally only the size will be adjusted, shapes
   * with negative coordinates will cause the page to be of the right size but not yet in the right
   * position. TODO add moving of shapes if necessary
   * 
   * @param margin
   *          The distance to leave as a margin on each side in inches
   * @param increaseOnly
   *          Iff set the page will never be made smaller, only bigger
   */
  void autoAdjustPageSize(double margin, boolean increaseOnly) throws SvgExportException;

  /**
   * Retrieves a list with the names of all currently defined CSS style classes.
   * 
   * @return The list with class names.
   */

  List<SvgCssStyling> getCSSStyleClasses();

  /**
   * Creates a new CSS Class and adds it to the list of available classes.
   * 
   * @param uniqueName
   *          - The name of the class.
   * @param properties
   *          - The map with the properties to be defined for this class.
   * @throws SvgExportException
   *           - iff a CSS class with this name already exists.
   */
  void createNewCSSClass(String uniqueName, Map<String, String> properties) throws SvgExportException;

  /**
   * This method has to be called after all properties of the document have been set and all shapes
   * have been added in order to apply those to the DOM structure. If a write or save method is
   * called without the document being finalized an exception will be thrown. Also, any changes to
   * the document done after finalizing will no longer take effect on the resulting output.
   * 
   * @throws SvgExportException
   *           Iff an error occurs during the DOM generation.
   */
  void finalizeDocument() throws SvgExportException;

  /**
   * @return true, if {@link #finalizeDocument()} has been evoked on this document
   */
  boolean isFinalized();

  /**
   * @return the DOM document
   */
  org.w3c.dom.Document getDocument();

  MasterShapeProperties getMasterShapeProperties(String masterName);

}