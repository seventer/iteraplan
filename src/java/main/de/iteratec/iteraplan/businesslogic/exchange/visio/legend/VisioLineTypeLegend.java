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

import java.awt.Color;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.LineDimension;
import de.iteratec.iteraplan.businesslogic.exchange.visio.VisioDimensionExport;
import de.iteratec.visio.model.Shape;
import de.iteratec.visio.model.exceptions.MasterNotFoundException;


public class VisioLineTypeLegend extends VisioAttributeLegend<Integer> {

  private static final String VISIO_SHAPE_NAME_INDEX_FIELD = "Color-Index-Field";
  private static final String VISIO_SHAPE_NAME_LINE_HEADER = "Color-Header";
  private static final double LINE_LENGTH                  = 0.3;
  private static final double LINE_WIDTH                   = 0.08;

  @Override
  protected void createLegendEntry(String shapeName, String key, String label) throws MasterNotFoundException {
    double binXColor = getPinX() + 0.25; // distance shape edge
    Shape legendEntry = getVisioLegendContainer().createNewInnerShape(VISIO_SHAPE_NAME_INDEX_FIELD);
    legendEntry.setFieldValue(label);
    legendEntry.setPosition(getPinX(), getPinY() - getLegendEntryHeight());

    LineDimension lineDim = (LineDimension) getDim();
    List<String> values = lineDim.getValues();
    Integer lineType;
    if (values.contains(key)) {
      lineType = lineDim.getValue(key);
    }
    else {
      lineType = lineDim.getDefaultValue();
    }
    legendEntry = getVisioLegendContainer().createNewInnerShape(shapeName);
    legendEntry.setSize(LINE_LENGTH, LINE_WIDTH);
    legendEntry.setLinePattern(VisioDimensionExport.getVisioLinePattern(lineType.intValue()));
    legendEntry.setFillForegroundColor(Color.BLACK);
    legendEntry.setPosition(binXColor, (getPinY() - getLegendEntryHeight() / 2));
    legendEntry.setCharSize(TXT_SIZE_11_PTS);
  }

  @Override
  protected String getHeaderShapeName() {
    return VISIO_SHAPE_NAME_LINE_HEADER;
  }
}
