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

import org.w3c.dom.Node;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.svg.model.BasicShape;


public class CircleShape extends AbstractBasicShapeImpl {

  private static final Logger LOGGER = Logger.getIteraplanLogger(CircleShape.class);

  private double              radius;

  public CircleShape(String shapeId, DocumentImpl document) {
    super(BasicShape.SVG_BASIC_SHAPES.CIRCLE, shapeId, document);
    this.setBasicShape(true);
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public double getWidth() {
    return 2 * radius;
  }

  public double getHeight() {
    return 2 * radius;
  }

  @Override
  protected void applySettingsToDom(Node domShape) {

    LOGGER.debug("Allpying specific shape settings for CircleShape to the DOM tree.");

    Node shapePropsNode = SvgDomUtils.getNodeWithId(domShape, DocumentImpl.ATTRIBUTE_VALUE_SHAPE_PROPS);

    if (this.getParentShape() != null) {
      this.setPosition(this.getPinX() - this.getParentShape().getPinX(), this.getPinY() - this.getParentShape().getPinY());
    }

    SvgDomUtils.setOrCreateAttributeForNode(shapePropsNode, "cx", Double.toString(0));
    SvgDomUtils.setOrCreateAttributeForNode(shapePropsNode, "cy", Double.toString(0));
    SvgDomUtils.setOrCreateAttributeForNode(shapePropsNode, "r", Double.toString(radius));

  }

}
