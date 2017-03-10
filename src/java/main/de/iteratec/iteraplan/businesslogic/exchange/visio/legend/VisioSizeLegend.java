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
package de.iteratec.iteraplan.businesslogic.exchange.visio.legend;

import java.util.List;

import de.iteratec.iteraplan.common.util.InchConverter;
import de.iteratec.visio.model.Shape;
import de.iteratec.visio.model.exceptions.MasterNotFoundException;


public class VisioSizeLegend extends VisioAttributeLegend<Double> {

  private static final String VISIO_SHAPE_NAME_SIZE_HEADER           = "Size-Header";
  private static final String VISIO_SHAPE_NAME_SIZE_FIELD            = "Size-Index-Field";
  private static final String VISIO_SHAPE_NAME_SIZE_UNDEFINED_ENTITY = "SizeUndefinedEntity";

  @Override
  protected void createLegendEntry(String shapeName, String key, String label) throws MasterNotFoundException {
    // The height of the size entities (in inches):
    double seHight = 0.25;

    Shape shape = getVisioLegendContainer().createNewInnerShape(VISIO_SHAPE_NAME_SIZE_FIELD);
    shape.setFieldValue(key);
    shape.setCharSize(TXT_SIZE_11_PTS);
    double height = shape.getHeight();
    shape.setPosition(0, getPinY() - height);

    double weight;
    List<String> values = getDim().getValues();
    if (values.contains(key)) {
      shape = getVisioLegendContainer().createNewInnerShape(shapeName);
      weight = getDim().getValue(key).doubleValue();
    }
    else {
      shape = getVisioLegendContainer().createNewInnerShape(VISIO_SHAPE_NAME_SIZE_UNDEFINED_ENTITY);
      weight = 0.4;
    }
    double seLength = InchConverter.mmToInches(30) * (weight * 0.6 + 0.5);

    shape.setPosition(getPinX() + getLabelOffset(), getPinY() - height / 2);
    shape.setSize(seLength, seHight);

    // The following has to be added so that the graphic can be correctly displayed with
    // VisioViewer
    shape.setFirstVertexOfGeometry(0, 0);
    shape.setLineEnd(2, 0, seHight);
    shape.setFirstVertexOfGeometry(0, seHight / 2);
    shape.setLineEnd(3, seLength, seHight / 2);
    shape.setFirstVertexOfGeometry(seLength, seHight);
    shape.setLineEnd(4, seLength, 0);
  }

  @Override
  protected double calculateLabelOffset(Shape headerShape) {
    return 0.05 * headerShape.getWidth();
  }

  @Override
  protected double calculatePinX(Shape headerShape) {
    return headerShape.getWidth() / 4;
  }

  @Override
  protected String getHeaderShapeName() {
    return VISIO_SHAPE_NAME_SIZE_HEADER;
  }
}
