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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanMap;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorGenerator;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.InnerVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.impl.util.VisualVariableHelper;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.impl.util.VisualVariableHelper.VisualVariable;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.legend.ColorLegend;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.legend.ColorLegendEntry;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.legend.ColorLegendInfo;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.visualizationmodel.APlanarSymbol;
import de.iteratec.visualizationmodel.Color;


public class MixedColorCodingDecorator extends ADecoratorBase<PrimitivePropertyExpression> {

  public static final String VV_COLORING_OPTIONAL       = "optional_coloring";
  public static final String VV_DECORATION_MODE         = "decorationMode";

  public static final String DECORATION_MODE_DISCRETE   = "discrete";
  public static final String DECORATION_MODE_CONTINUOUS = "continuous";

  private ADecoratorBase<?>  subDecorator;

  //visual variables
  private EClass             visualVariableClass        = null;

  //one of: discrete, continuous
  private String             decorationMode             = "";

  protected MixedColorCodingDecorator(InnerVBB<? extends APlanarSymbol> decoratedVbb) {
    super(decoratedVbb);
  }

  /**{@inheritDoc}**/
  public void computeAbstractViewmodel(EditableMetamodel abstractViewModel, ViewpointConfiguration vpConfig, String prefix) {
    getDecoratedVBB().computeAbstractViewmodel(abstractViewModel, vpConfig, prefix);
    setDecoratedClass(abstractViewModel.findUniversalTypeByPersistentName(prefix));
    setColorAttribute(abstractViewModel.createMixedOrProperty(getDecoratedClass(), ATTRIBUTE_COLOR, 0, 1,
        Sets.newHashSet(BuiltinPrimitiveType.DECIMAL), true));
  }

  /**{@inheritDoc}**/
  @Override
  protected Color getColorForObject(UniversalModelExpression instance, Model model, ViewpointConfiguration config) {
    return subDecorator.getColorForObject(instance, model, config);
  }

  /**{@inheritDoc}**/
  @Override
  protected void initialize(Model model, ViewpointConfiguration config) {
    subDecorator.initialize(model, config);
  }

  /**{@inheritDoc}**/
  public EClass getEVisualVariableClass(EPackage visualVariableEPackage, String prefix) {
    this.visualVariableClass = this.getDecoratedVBB().getEVisualVariableClass(visualVariableEPackage, prefix);
    VisualVariableHelper.addAllVisualVariables(getClass(), this.visualVariableClass);
    VisualVariableHelper.addAllVisualVariables(ContinuousColorCodingDecorator.class, this.visualVariableClass);
    VisualVariableHelper.addAllVisualVariables(DiscreteColorCodingDecorator.class, this.visualVariableClass);
    return this.visualVariableClass;
  }

  /**{@inheritDoc}**/
  public void setVisualVariables(EObject visualVariables) {
    BeanMap bm = new BeanMap(this);
    for (EAttribute att : this.visualVariableClass.getEAllAttributes()) {
      VisualVariableHelper.setVisualVariableValue(bm, visualVariables, att);
    }
    if (DECORATION_MODE_DISCRETE.equals(this.decorationMode)) {
      subDecorator = new DiscreteColorCodingDecorator(getDecoratedVBB());
    }
    else if (DECORATION_MODE_CONTINUOUS.equals(this.decorationMode)) {
      subDecorator = new ContinuousColorCodingDecorator(getDecoratedVBB());
    }
    BeanMap subBeanMap = new BeanMap(subDecorator);
    for (EAttribute att : this.visualVariableClass.getEAllAttributes()) {
      VisualVariableHelper.setVisualVariableValue(subBeanMap, visualVariables, att);
    }
    this.getDecoratedVBB().setVisualVariables(visualVariables);
  }

  /**
   * @return legend the legend
   */
  @Override
  public final ColorLegend getLegend() {
    if (subDecorator == null) {
      return null;
    }
    return subDecorator.getLegend();
  }

  @VisualVariable
  public String getDecorationMode() {
    return decorationMode;
  }

  public void setDecorationMode(String decorationMode) {
    this.decorationMode = decorationMode;
  }

  public class ContinuousColorCodingDecorator extends ADecoratorBase<PrimitivePropertyExpression> {

    public static final String  VV_MIN_COLOR                = "minColor";
    public static final String  VV_MAX_COLOR                = "maxColor";
    public static final String  VV_UNDEFINED_COLOR          = "undefinedColor";
    public static final String  VV_OUT_OF_BOUNDS_COLOR      = "outOfBoundsColor";
    public static final String  VV_MIN_VALUE                = "minValue";
    public static final String  VV_MAX_VALUE                = "maxValue";

    public static final String  DEFAULT_MIN_COLOR           = "#FFFFFF";
    public static final String  DEFAULT_MAX_COLOR           = "#A91B8E";
    public static final String  DEFAULT_OUT_OF_BOUNDS_COLOR = "#FF0000";

    private static final String LOWERBOUND                  = "global.lowerbound.short";
    private static final String UPPERBOUND                  = "global.upperbound.short";

    // visual variables
    private Color               minColor;
    private Color               maxColor;
    private Color               undefinedColor;
    private Color               outOfBoundsColor;
    private BigDecimal          minValue;
    private BigDecimal          maxValue;

    private ColorGenerator      colorGenerator;

    protected ContinuousColorCodingDecorator(InnerVBB<? extends APlanarSymbol> decoratedVbb) {
      super(decoratedVbb);
    }

    @Override
    protected void initialize(Model model, ViewpointConfiguration config) {
      String lowerBoundColor = this.minColor.toString().substring(1);
      String upperBoundColor = this.maxColor.toString().substring(1);
      Iterable<BigDecimal> values = initBounds(model, config);
      determineRangeBounds((Set<BigDecimal>) values);
      this.colorGenerator = new ColorGenerator(lowerBoundColor, upperBoundColor, true, values);
      this.colorGenerator.setDefaultColor(this.undefinedColor.toString().substring(1));
      this.colorGenerator.setOutOfBoundsColor(this.outOfBoundsColor.toString().substring(1));
      this.colorGenerator.setLowerBound(minValue.floatValue());
      this.colorGenerator.setUpperBound(maxValue.floatValue());
      fillLegend();
    }

    private Iterable<BigDecimal> initBounds(Model model, ViewpointConfiguration config) {
      SubstantialTypeExpression clazz = (SubstantialTypeExpression) config.getMappingFor(MixedColorCodingDecorator.this.getDecoratedClass());
      PropertyExpression<?> colorAtt = (PropertyExpression<?>) config.getMappingFor(MixedColorCodingDecorator.this.getColorAttribute());
      setColorLegend(new ColorLegend(new ColorLegendInfo(colorAtt.getName(), "")));

      Set<BigDecimal> values = new HashSet<BigDecimal>();
      Collection<InstanceExpression> instances = model.findAll(clazz);
      for (InstanceExpression instance : instances) {
        BigDecimal attributeValue = (BigDecimal) model.getValue(instance, colorAtt);
        if (attributeValue != null) {
          values.add(attributeValue);
        }
      }
      return values;
    }

    /**
     * Entries of the legend are colored by using the mean of the range.
     */
    private void fillLegend() {
      if (maxColor != null && minColor != null) {
        java.awt.Color color = colorGenerator.generateColor(minValue);
        ColorLegend legend = super.getLegend();
        legend.addEntry(new ColorLegendEntry(null, MessageAccess.getStringOrNull(LOWERBOUND) + ": " + minValue.toString(), new Color(color.getRed(),
            color.getGreen(), color.getBlue())));

        color = colorGenerator.generateColor(maxValue);
        legend.addEntry(new ColorLegendEntry(null, MessageAccess.getStringOrNull(UPPERBOUND) + ": " + maxValue.toString(), new Color(color.getRed(),
            color.getGreen(), color.getBlue())));

        java.awt.Color defaultColor = colorGenerator.getDefaultColor();
        legend.addEntry(new ColorLegendEntry(null, MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE), new Color(
            defaultColor.getRed(), defaultColor.getGreen(), defaultColor.getBlue())));
        legend.addEntry(new ColorLegendEntry(null, MessageAccess.getStringOrNull("global.outofbounds.short"), new Color(outOfBoundsColor.getRed(),
            outOfBoundsColor.getGreen(), outOfBoundsColor.getBlue())));
      }
    }

    private void determineRangeBounds(Set<BigDecimal> values) {
      if (values.isEmpty()) {
        return;
      }
      boolean updateLowerBound = (minValue == null);
      boolean updateUpperBound = (maxValue == null);
      for (BigDecimal value : values) {
        if (minValue == null && value != null) {
          minValue = value;
        }
        if (maxValue == null && value != null) {
          maxValue = value;
        }
        if (value != null) {
          if (updateUpperBound) {
            maxValue = maxValue.max(value);
          }
          if (updateLowerBound) {
            minValue = minValue.min(value);
          }
        }
      }
    }

    @Override
    protected Color getColorForObject(UniversalModelExpression instance, Model model, ViewpointConfiguration config) {
      PropertyExpression<?> colorAttributeMapping = (PropertyExpression<?>) config.getMappingFor(MixedColorCodingDecorator.this.getColorAttribute());
      if (instance == null) {
        return null;
      }
      BigDecimal attributeValue = (BigDecimal) instance.getValue(colorAttributeMapping);

      java.awt.Color awtColor = this.colorGenerator.generateColor(attributeValue);
      return new Color(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
    }

    @VisualVariable
    public Color getMinColor() {
      return minColor;
    }

    public void setMinColor(Color minColor) {
      this.minColor = minColor;
    }

    @VisualVariable
    public Color getMaxColor() {
      return maxColor;
    }

    public void setMaxColor(Color maxColor) {
      this.maxColor = maxColor;
    }

    @VisualVariable
    public Color getUndefinedColor() {
      return undefinedColor;
    }

    public void setUndefinedColor(Color undefinedColor) {
      this.undefinedColor = undefinedColor;
    }

    @VisualVariable
    public Color getOutOfBoundsColor() {
      return outOfBoundsColor;
    }

    public void setOutOfBoundsColor(Color outOfBoundsColor) {
      this.outOfBoundsColor = outOfBoundsColor;
    }

    @VisualVariable
    public BigDecimal getMinValue() {
      return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
      this.minValue = minValue;
    }

    @VisualVariable
    public BigDecimal getMaxValue() {
      return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
      this.maxValue = maxValue;
    }

    /**{@inheritDoc}**/
    public void computeAbstractViewmodel(EditableMetamodel abstractViewModel, ViewpointConfiguration vpConfig, String prefix) {
      throw new UnsupportedOperationException("Not supported in this subclass!");
    }

    /**{@inheritDoc}**/
    public EClass getEVisualVariableClass(EPackage visualVariableEPackage, String prefix) {
      throw new UnsupportedOperationException("Not supported in this subclass!");
    }

    /**{@inheritDoc}**/
    public void setVisualVariables(EObject visualVariables) {
      throw new UnsupportedOperationException("Not supported in this subclass!");
    }

  }

  public class DiscreteColorCodingDecorator extends ADecoratorBase<EnumerationPropertyExpression> {

    public static final String  VV_COLOR_MAPPING = "colorMapping";

    //The config which comes in from an external source
    private Map<String, String> colorMappingConfig;
    //The actual mapping, created during initialization and respecting the literals of the enumeration
    private Map<String, Color>  colorMapping;

    protected DiscreteColorCodingDecorator(InnerVBB<? extends APlanarSymbol> decoratedVbb) {
      super(decoratedVbb);
    }

    /**
     * Automatically builds a color mapping using the relevant attribute's possible values and the available colors.
     * @param config
     *          {@link ViewpointConfiguration} with the relevant attribute
     */
    @Override
    protected void initialize(Model model, ViewpointConfiguration config) {
      EnumerationExpression enumeration = null;
      EnumerationPropertyExpression colorProperty = (EnumerationPropertyExpression) config.getMappingFor(MixedColorCodingDecorator.this
          .getColorAttribute());
      if (colorProperty != null) {
        colorMapping = Maps.newHashMap();
        setColorLegend(new ColorLegend(new ColorLegendInfo(colorProperty.getName(), "")));
        enumeration = colorProperty.getType();
      }
      else {
        setColorLegend(new ColorLegend(new ColorLegendInfo("", "")));
      }
      if (enumeration != null) {
        ColorLegend legend = super.getLegend();
        for (EnumerationLiteralExpression literal : enumeration.getLiterals()) {
          Color color = new Color("#" + colorMappingConfig.get(literal.getPersistentName()));
          colorMapping.put(literal.getPersistentName(), color);
          ColorLegendEntry colorLegendEntry = new ColorLegendEntry(null, literal.getName(), color);
          legend.addEntry(colorLegendEntry);
        }
        if (colorMappingConfig.containsKey(MessageAccess.getString(DimensionOptionsBean.DEFAULT_VALUE))) {
          Color color = new Color("#" + colorMappingConfig.get(MessageAccess.getString(DimensionOptionsBean.DEFAULT_VALUE)));
          String unspecifiedString = MessageAccess.getString(DimensionOptionsBean.DEFAULT_VALUE);
          colorMapping.put(unspecifiedString, color);
          legend.addEntry(new ColorLegendEntry(null, unspecifiedString, colorMapping.get(unspecifiedString)));
        }
      }
    }

    /**
     * Returns the color the given Object should be colored in. 
     * @param object
     *          the Object to determine a color for
     * @param config
     *          ViewpointConfiguration containing the relevant attribute definition
     * @return the Color to set
     */
    @Override
    protected Color getColorForObject(UniversalModelExpression object, Model model, ViewpointConfiguration config) {
      PropertyExpression<?> colorAttributeMapping = (PropertyExpression<?>) config.getMappingFor(MixedColorCodingDecorator.this.getColorAttribute());
      if (object == null) {
        return null;
      }
      EnumerationLiteralExpression value = (EnumerationLiteralExpression) object.getValue(colorAttributeMapping);
      if (value == null) {
        return colorMapping.get(MessageAccess.getString(DimensionOptionsBean.DEFAULT_VALUE));
      }
      else {
        return colorMapping.get(value.getPersistentName());
      }
    }

    @VisualVariable
    public String getColorMapping() {
      if (this.colorMapping == null) {
        return null;
      }
      Map<String, String> stringMapping = Maps.newHashMap();
      for (Entry<String, Color> entry : colorMapping.entrySet()) {
        stringMapping.put(entry.getKey(), entry.getValue().toString());
      }
      Gson gson = new Gson();
      return gson.toJson(stringMapping);
    }

    @SuppressWarnings("unchecked")
    public void setColorMapping(String colorMapping) {
      this.colorMappingConfig = (new Gson()).fromJson(colorMapping, Map.class);
    }

    /**{@inheritDoc}**/
    public void computeAbstractViewmodel(EditableMetamodel abstractViewModel, ViewpointConfiguration vpConfig, String prefix) {
      throw new UnsupportedOperationException("Not supported in this subclass!");
    }

    /**{@inheritDoc}**/
    public EClass getEVisualVariableClass(EPackage visualVariableEPackage, String prefix) {
      throw new UnsupportedOperationException("Not supported in this subclass!");
    }

    /**{@inheritDoc}**/
    public void setVisualVariables(EObject visualVariables) {
      throw new UnsupportedOperationException("Not supported in this subclass!");
    }
  }

}
