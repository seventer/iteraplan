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

import java.util.List;

import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.Dimension;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.visio.model.Shape;


/**
 * A shape configurator is an object that can take a shape and change its appearance based on
 * iteraplan data. The basic idea is that during a Visio export certain aspects of a Shape instance
 * such as color or line style should be controlled by the data using a mapping that the user
 * provides. This mapping is encapsulated in these objects, which are part of the interface between
 * the UI and the Visio export. ShapeConfigurator use @see
 * de.iteratec.iteraplan.businesslogic.exchange.visio.dimension.Dimension to map values.
 */
public abstract class ShapeConfigurator<T extends Dimension<?>> {
  private final T dimension;

  public ShapeConfigurator(T newDimension) {
    this.dimension = newDimension;
  }

  /**
   * Gets the label for the configurator. This is used to label legends which show the mappings
   * between the objects and the results.
   * 
   * @return The label to use in legends. Not null.
   */
  public String getLabel() {
    return dimension.getName();
  }

  public T getDimension() {
    return this.dimension;
  }

  public List<String> getValues() {
    return this.dimension.getValues();
  }

  public boolean hasUnspecificValue() {
    return this.dimension.hasUnspecificValue();
  }

  /**
   * Takes a shape and changes the appearance according to configuration.
   * 
   * @param shape
   *          The shape to change settings upon. Must not be null.
   * @param data
   *          The data object used to configure the shape. Must not be null. Must match type of
   *          configurator.
   */
  public abstract <E extends BuildingBlock> void configureShape(Shape shape, E data);

  /**
   * Takes a shape and changes the appearance according to configuration.
   * 
   * @param shape
   *          The shape to change settings upon. Must not be null.
   * @param data
   *          The string used to configure the shape. Must not be null.
   */
  public abstract <E extends BuildingBlock> void configureShape(Shape shape, String data);

  /**
   * True if the configuration changes the fill of a shape. This is used to determine how to create
   * samples for legends.
   * 
   * @return True if the configuration changes the fill of a shape.
   */
  public abstract boolean affectsFill();

  /**
   * True if the configuration changes the line of a shape. This is used to determine how to create
   * samples for legends.
   * 
   * @return True if the configuration changes the line of a shape.
   */
  public abstract boolean affectsLine();
}
