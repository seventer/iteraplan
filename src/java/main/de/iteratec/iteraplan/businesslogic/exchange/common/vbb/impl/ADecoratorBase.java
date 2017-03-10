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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.InnerVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.VBBUtil;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.AbstractVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.VisualVariableHelper.VisualVariable;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.legend.ColorLegend;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.visualizationmodel.APlanarSymbol;
import de.iteratec.visualizationmodel.Color;


abstract class ADecoratorBase<P extends PropertyExpression<?>> implements InnerVBB<APlanarSymbol> {

  public static final String                      ATTRIBUTE_COLOR = VBBUtil.PREFIX4OPTIONAL + "coloring";

  // decorated VBB
  private final InnerVBB<? extends APlanarSymbol> decoratedVbb;

  private ColorLegend                             colorLegend;

  private boolean                                 initialized     = false;

  //abstract viewmodel objects
  private P                                       colorAttribute;
  private UniversalTypeExpression                 decoratedClass;

  protected ADecoratorBase(InnerVBB<? extends APlanarSymbol> decoratedVbb) {
    this.decoratedVbb = decoratedVbb;
  }

  /**
   * @return the baseUrl used for creating links at symbols in the resulting visualization
   */
  @VisualVariable
  public final String getBaseUrl() {
    if (this.decoratedVbb instanceof AbstractVBB) {
      return ((AbstractVBB) this.decoratedVbb).getBaseUrl();
    }
    return null;
  }

  /**
   * Sets the baseUrl used for creating links at symbols in the resulting visualization.
   * @param baseUrl the baseUrl for links.
   */
  public final void setBaseUrl(String baseUrl) {
    if (this.decoratedVbb instanceof AbstractVBB) {
      ((AbstractVBB) this.decoratedVbb).setBaseUrl(baseUrl);
    }
  }

  /**
   * @return legend the legend
   */
  public ColorLegend getLegend() {
    return colorLegend;
  }

  /**{@inheritDoc}**/
  public final APlanarSymbol transform(UniversalModelExpression instance, Model model, ViewpointConfiguration config) {
    APlanarSymbol symbol = getDecoratedVBB().transform(instance, model, config);

    if (config.hasValueFor(getColorAttribute())) { // If optional colorAttribute is set
      if (!isInitialized()) {
        initialize(model, config);
        setInitialized(true);
      }

      if (symbol != null) {
        Color colorForObject = getColorForObject(instance, model, config);
        if (colorForObject != null) {
          symbol.setFillColor(colorForObject);
        }
      }
    }
    return symbol;
  }

  protected abstract Color getColorForObject(UniversalModelExpression instance, Model model, ViewpointConfiguration config);

  protected abstract void initialize(Model model, ViewpointConfiguration config);

  protected final InnerVBB<? extends APlanarSymbol> getDecoratedVBB() {
    return this.decoratedVbb;
  }

  protected final void setDecoratedClass(UniversalTypeExpression decoratedClass) {
    this.decoratedClass = decoratedClass;
  }

  protected final UniversalTypeExpression getDecoratedClass() {
    return this.decoratedClass;
  }

  protected final P getColorAttribute() {
    return this.colorAttribute;
  }

  protected final void setColorAttribute(P colorAttribute) {
    this.colorAttribute = colorAttribute;
  }

  protected final boolean isInitialized() {
    return this.initialized;
  }

  protected final void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

  protected final void setColorLegend(ColorLegend colorLegend) {
    this.colorLegend = colorLegend;
  }

}
