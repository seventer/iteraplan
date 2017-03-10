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
package de.iteratec.iteraplan.businesslogic.exchange.visio.landscape;

import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.LineDimension;
import de.iteratec.iteraplan.businesslogic.exchange.visio.VisioDimensionExport;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.visio.model.Shape;


/**
 * Assigns linepattern to a shape based on the attribute values of a building block.
 * 
 * @see de.iteratec.iteraplan.businesslogic.exchange.visio.landscape.ShapeConfigurator
 */
public class ShapeConfiguratorLinePattern extends ShapeConfigurator<LineDimension> {

  /**
   * Creates a new shape configurator. The attribute values are mapped to integers which represent
   * the line pattern id within Visio. If multiple matches occur for a multi-valued EnumAt, the
   * first match wins.
   */
  public ShapeConfiguratorLinePattern(LineDimension dimension) {
    super(dimension);
  }

  @Override
  public <T extends BuildingBlock> void configureShape(Shape shape, T bb) {
    int line = VisioDimensionExport.getVisioLinePattern(getDimension().getValue(bb).intValue());
    shape.setLinePattern(line);
  }

  @Override
  public void configureShape(Shape shape, String attributeValue) {
    int line = VisioDimensionExport.getVisioLinePattern(getDimension().getValue(attributeValue).intValue());
    shape.setLinePattern(line);
  }

  @Override
  public boolean affectsFill() {
    return false;
  }

  @Override
  public boolean affectsLine() {
    return true;
  }
}
