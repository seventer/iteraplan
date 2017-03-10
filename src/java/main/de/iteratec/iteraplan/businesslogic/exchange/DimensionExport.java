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
package de.iteratec.iteraplan.businesslogic.exchange;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Iterables;

import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeRangeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorGenerator;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorRangeDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.Dimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.DimensionAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.EmptyAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.LineDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.NumberAttributeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.PositionDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SealStateAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SizeDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.portfolio.BubbleSpace;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.IPortfolioOptions;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.collections.StringToBigDecimalFunction;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;


/**
 * Core functionality of all graphical exports.ö
 */
public abstract class DimensionExport extends GraphicsExport {

  private final AttributeValueService attributeValueService;
  private final AttributeTypeService  attributeTypeService;

  private ColorDimension              colorDimension;

  private Locale                      locale;

  public DimensionExport(AttributeTypeService attributeTypeService, AttributeValueService attributeValueService) {
    this.attributeTypeService = attributeTypeService;
    this.attributeValueService = attributeValueService;
    this.locale = UserContext.getCurrentLocale();
  }

  /**
   * Creates a color definition depending on the colorOptions
   * 
   * @param colorOptions
   *          Specific configurations
   * @param bbType
   * @return color Dimension object for colorOptions
   */
  protected ColorDimension createColorDimension(ColorDimensionOptionsBean colorOptions, TypeOfBuildingBlock bbType) {
    if (colorOptions.isUseColorRange()) {
      return createColorRangeDimension(colorOptions, bbType);
    }
    Integer selectedColorAttribute = colorOptions.getDimensionAttributeId();
    DimensionAdapter<?> adapter = this.createAdapter(selectedColorAttribute, bbType);
    ColorDimension dimension = new ColorDimension(adapter);
    dimension.setDefaultValue(colorOptions.getDefaultColor());

    dimension.init(convertStringToColor(colorOptions.getSelectedColors()));

    return dimension;
  }

  /**
   * Creates a color range definition depending on the colorOptions
   * 
   * @param colorOptions
   *          Specific configurations
   * @param bbType
   * @return color Dimension object for colorOptions
   */
  protected ColorRangeDimension createColorRangeDimension(ColorDimensionOptionsBean colorOptions, TypeOfBuildingBlock bbType) {
    Integer selectedColorAttribute = colorOptions.getDimensionAttributeId();
    AttributeType atDefault = attributeTypeService.loadObjectById(selectedColorAttribute);
    List<String> stringValues = attributeValueService.getAllAVStrings(selectedColorAttribute);

    NumberAttributeAdapter adapter = new NumberAttributeAdapter(locale);
    adapter.init(atDefault, stringValues);

    ColorRangeDimension dimension = new ColorRangeDimension(adapter);
    dimension.setDefaultValue(colorOptions.getDefaultColor());

    if (!stringValues.isEmpty()) {

      String lowerBoundColor = colorOptions.getValueToColorMap().get(ColorDimensionOptionsBean.LOWER_BOUND_VALUE).getColor();
      String upperBoundColor = colorOptions.getValueToColorMap().get(ColorDimensionOptionsBean.UPPER_BOUND_VALUE).getColor();
      Iterable<BigDecimal> values = Iterables.transform(stringValues, new StringToBigDecimalFunction(locale));
      ColorGenerator cg = new ColorGenerator(lowerBoundColor, upperBoundColor, false, values);

      dimension.initValues(adapter.getValues(), cg);
      dimension.setLegendKeyPrefixes(ColorDimensionOptionsBean.LOWER_BOUND_VALUE, ColorDimensionOptionsBean.UPPER_BOUND_VALUE);
    }

    return dimension;
  }

  /**
   * Creates a line definition depending on the selection.
   * @param selection
   * @param values
   * @param bbType
   * @return line Dimension object for selection
   */
  public LineDimension createLineDimension(Integer selection, List<Integer> values, TypeOfBuildingBlock bbType) {
    DimensionAdapter<?> adapter = this.createAdapter(selection, bbType);
    LineDimension dimension = new LineDimension(adapter);

    if (selection.intValue() != GraphicalExportBaseOptions.NOTHING_SELECTED) {
      dimension.init(values);
    }

    return dimension;
  }

  /**
   * Creates a line definition depending on the lineOptions
   * 
   * @param lineOptions
   *          Specific configurations
   * @param bbType
   * @return line Dimension object for colorOptions
   */
  public LineDimension createLineDimension(LineDimensionOptionsBean lineOptions, TypeOfBuildingBlock bbType) {
    DimensionAdapter<?> adapter = this.createAdapter(lineOptions.getDimensionAttributeId(), bbType);
    LineDimension dimension = new LineDimension(adapter);

    if (lineOptions.getDimensionAttributeId().intValue() != GraphicalExportBaseOptions.NOTHING_SELECTED) {
      if (lineOptions.getDimensionAttributeId().intValue() != GraphicalExportBaseOptions.STATUS_SELECTED) {
        dimension.setDefaultValue(lineOptions.getDefaultLineType());
      }

      dimension.init(createIntegerList(lineOptions.getSelectedLineTypes()));
    }
    return dimension;
  }

  /**
   * Retrieves an adapter.
   * 
   * @param selection
   *          The id of the attribute.
   * @param bbType
   *          The type of building block for which the adapter is to be created. Only relevant for a
   *          status adapter, otherwise can be left as null.
   * @return The initialized adapter.
   */
  protected DimensionAdapter<?> createAdapter(Integer selection, TypeOfBuildingBlock bbType) {
    DimensionAdapter<?> adapter;
    switch (selection.intValue()) {
      case GraphicalExportBaseOptions.NOTHING_SELECTED:
        adapter = new EmptyAdapter(locale);
        break;
      case GraphicalExportBaseOptions.STATUS_SELECTED:
        adapter = new StatusAdapter(locale, bbType);
        break;
      case GraphicalExportBaseOptions.SEAL_SELECTED:
        adapter = new SealStateAdapter(locale);
        break;
      default:
        AttributeType atDefault = attributeTypeService.loadObjectById(selection);
        List<String> stringValues = attributeValueService.getAllAVStrings(selection);
        if (atDefault instanceof NumberAT) {
          adapter = new AttributeRangeAdapter(locale);
        }
        else {
          adapter = new AttributeAdapter(locale);
        }
        ((AttributeAdapter) adapter).init(atDefault, stringValues);
        break;
    }
    return adapter;
  }

  private List<Color> convertStringToColor(Collection<String> colorStr) {
    List<Color> result = new ArrayList<Color>();
    for (String color : colorStr) {
      result.add(Color.decode("#" + color));
    }
    return result;
  }

  /**
   * Retrieves the name of the {@link AttributeType} specifying a dimension.
   * 
   * @param dim
   *          An initialized instance of {@link Dimension}.
   * @return The name of the {@link AttributeType}.
   */
  protected String getFieldValueFromDimension(Dimension<?> dim) {
    DimensionAdapter<?> adapter = dim.getAdapter();
    if (adapter instanceof AttributeAdapter) {
      AttributeType attrType = ((AttributeAdapter) adapter).getAttributeType();
      if (attrType != null) {
        return attrType.getName();
      }
    }
    else {
      return dim.getName();
    }
    return "";
  }

  /**
   * Retrieves the name of the {@link AttributeType} specifying this dimension together with its
   * {@link de.iteratec.iteraplan.model.attribute.AttributeTypeGroup AttributeTypeGroup}.
   * 
   * @param dim
   *          An initialized instance of {@link Dimension}.
   * @return The name of the {@link AttributeType} together with its
   *          {@link de.iteratec.iteraplan.model.attribute.AttributeTypeGroup AttributeTypeGroup}.
   */
  protected String getFullFieldValueFromDimension(Dimension<?> dim) {
    DimensionAdapter<?> adapter = dim.getAdapter();
    if (adapter instanceof AttributeAdapter) {
      AttributeType attrType = ((AttributeAdapter) adapter).getAttributeType();
      if (attrType != null) {
        return ((AttributeAdapter) adapter).getAttributeType().getNameWithGroup();
      }
    }
    else {
      return dim.getName();
    }

    return "";
  }

  protected BubbleSpace createBubbleSpace(IPortfolioOptions portfolioOptions, List<BuildingBlock> buildingBlocks) {
    BubbleSpace bubbleSpace = new BubbleSpace();
    /*
     * Scaling should be used only for the axis dimensions, not for the size and color ones (if the
     * scaling method is used for sizes, all bubbles come out uniformly sized and the size legend is
     * incorrectly displayed).
     */
    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(portfolioOptions.getSelectedBbType());
    bubbleSpace.setXDimension(createPositionDimensionWithScaling(portfolioOptions.getXAxisAttributeId(), portfolioOptions.isScalingEnabled(),
        buildingBlocks, bbType));
    bubbleSpace.setYDimension(createPositionDimensionWithScaling(portfolioOptions.getYAxisAttributeId(), portfolioOptions.isScalingEnabled(),
        buildingBlocks, bbType));

    bubbleSpace.setSizeDimension(createSizeDimension(portfolioOptions.getSizeAttributeId(), bbType));
    bubbleSpace.setColorDimension(createColorDimension(portfolioOptions.getColorOptionsBean(), bbType));

    return bubbleSpace;

  }

  /**
   * Portfolio Export method. For number attributes show in the legend only the lowest, highest and
   * three intermediate values. Returns a List of number Strings that contains at most five
   * elements.
   * 
   * @param numberAt
   *          The NumberAT instance for which the legend values should be calculated. Existing upper
   *          and lower bounds are used.
   * @param values
   *          List of BigDecimal.
   * @return List of String representations of up to five numbers to be shown in the legend
   */
  protected List<String> getNumberValuesForLegend(NumberAT numberAt, List<String> values, IPortfolioOptions portfolioOptions) {
    List<BigDecimal> bigDList = new ArrayList<BigDecimal>();
    for (String string : values) {
      BigDecimal value = BigDecimalConverter.parse(string, locale);
      bigDList.add(value);
    }
    BigDecimal value0BigD = numberAt.calculateLowerBoundForNumberAt(bigDList);
    BigDecimal valueNBigD = numberAt.calculateUpperBoundForNumberAt(bigDList);

    if (!portfolioOptions.isScalingEnabled()) {
      return createResultListWithoutScaling(value0BigD, valueNBigD);
    }
    else {
      return createResultListWithScaling(bigDList, value0BigD, valueNBigD);
    }
  }

  private List<String> createResultListWithScaling(List<BigDecimal> bigDList, BigDecimal value0BigD, BigDecimal valueNBigD) {
    /*
     * If scaling is enabled, the null has to be excluded from the axis, and the minimal and
     * maximal values of the current bubbles have to be taken:
     */
    List<String> resultList = new ArrayList<String>();
    if (value0BigD != null && valueNBigD != null) {
      double min = bigDList.get(0).doubleValue();
      double max = min;
      // Determine the current minimum
      for (BigDecimal bigDecimal : bigDList) {
        double tmp = bigDecimal.doubleValue();
        min = Math.min(tmp, min);
      }
      // Determine the current maximum
      for (BigDecimal bigDecimal : bigDList) {
        double tmp = bigDecimal.doubleValue();
        max = Math.max(tmp, max);
      }
      // Calculate the values that should appear on the axis
      double difference = max - min;
      for (int i = 0; i < 5; i++) {
        BigDecimal elem = new BigDecimal(min + i * difference / 4);
        resultList.add(BigDecimalConverter.format(elem, true, locale));
      }
    }

    return resultList;
  }

  private List<String> createResultListWithoutScaling(BigDecimal value0BigD, BigDecimal valueNBigD) {
    List<String> resultList = new ArrayList<String>();
    if (value0BigD != null && valueNBigD != null) {
      if (value0BigD.equals(valueNBigD)) {
        resultList.add(BigDecimalConverter.format(value0BigD, true, locale));
      }
      else {
        double difference = valueNBigD.doubleValue() - value0BigD.doubleValue();
        resultList.add(BigDecimalConverter.format(value0BigD, true, locale));
        for (int i = 1; i < 4; i++) {
          BigDecimal elem = new BigDecimal(value0BigD.doubleValue() + i * (difference / 4));
          resultList.add(BigDecimalConverter.format(elem, true, locale));
        }
        resultList.add(BigDecimalConverter.format(valueNBigD, true, locale));
      }
    }

    return resultList;
  }

  /*
   * If scaling is enabled, only the values (respective to attributeTupeId) of the buldingBlocks
   * that are to be drawn on the diagram are taken. This is important because the set of the values
   * of the currently to be drawn elements might consist of only some of all the values, in which
   * case, the axis labels should be arranged to this (smaller) interval, and not the one from 0 to
   * the biggest value (in this dimension). This method should not be used for the size and color
   * dimensions.
   */
  @SuppressWarnings("unchecked")
  private PositionDimension createPositionDimensionWithScaling(Integer attributeTypeId, boolean scaling, List<BuildingBlock> buildingBlocks,
                                                               TypeOfBuildingBlock bbType) {
    DimensionAdapter<?> adapter;
    PositionDimension dimension;
    boolean isNumberAt = false;
    switch (attributeTypeId.intValue()) {
      case GraphicalExportBaseOptions.NOTHING_SELECTED:
        List<Double> mapping = new ArrayList<Double>();
        adapter = new EmptyAdapter(locale);
        dimension = new PositionDimension(adapter);
        dimension.init(mapping);
        break;
      case GraphicalExportBaseOptions.STATUS_SELECTED:
        adapter = new StatusAdapter(locale, bbType);
        dimension = new PositionDimension(adapter);
        dimension.initNotNumberAt(adapter.getValues());
        break;
      case GraphicalExportBaseOptions.SEAL_SELECTED:
        adapter = new SealStateAdapter(locale);
        dimension = new PositionDimension(adapter);
        dimension.initNotNumberAt(adapter.getValues());
        break;
      default:
        AttributeType atDefault = attributeTypeService.loadObjectById(attributeTypeId);
        isNumberAt = atDefault instanceof NumberAT;
        List<String> stringValues;
        if (scaling) {
          stringValues = attributeValueService.getAVStringsForBuildingBlocks(attributeTypeId, buildingBlocks);
        }
        else {
          stringValues = attributeValueService.getAllAVStrings(attributeTypeId);
        }
        AttributeAdapter attrAdapter = new AttributeAdapter(locale);
        attrAdapter.init(atDefault, stringValues);
        if (isNumberAt) {
          List<NumberAV> numberValues = (List<NumberAV>) attributeValueService.getAllAVs(attributeTypeId);
          NumberAT numberAt = (NumberAT) atDefault;
          dimension = new PositionDimension(attrAdapter);
          dimension.initNumberAt(numberAt, numberValues);
        }
        else {
          dimension = new PositionDimension(attrAdapter);
          dimension.initNotNumberAt(stringValues);
        }
        break;
    }

    return dimension;
  }

  /*
   * If scaling is disabled all elements (with corresponding attributeTypeId) are taken. In this
   * case the axis is mapped from 0 to the biggest value available in the database (respecting the
   * attributeTypeId). This is to be considered only if the dimension is an axis dimension. In the
   * other cases (as for example size dimension) there is no difference, whether scaling is enabled
   * or disabled. All other dimensions use this method.
   */
  private SizeDimension createSizeDimension(Integer attributeTypeId, TypeOfBuildingBlock bbType) {
    DimensionAdapter<?> adapter = this.createAdapter(attributeTypeId, bbType);
    SizeDimension dimension = new SizeDimension(adapter);
    List<String> mapping = new ArrayList<String>();

    if (attributeTypeId.intValue() != GraphicalExportBaseOptions.NOTHING_SELECTED) {
      mapping = dimension.getValues();
    }
    dimension.initValues(mapping);

    return dimension;
  }

  protected List<Integer> createIntegerList(List<String> input) {
    List<Integer> result = new ArrayList<Integer>();
    if (input != null) {
      for (String value : input) {
        result.add(Integer.valueOf(value));
      }
    }
    return result;
  }

  protected String getBuildingBlockHierarchicalName(BuildingBlock block) {
    String bubbleName = "";
    if (block instanceof HierarchicalEntity<?>) {
      HierarchicalEntity<?> hierarchicalEntity = (HierarchicalEntity<?>) block;
      bubbleName = hierarchicalEntity.getHierarchicalName();
    }
    else {
      bubbleName = block.getIdentityString();
    }
    return bubbleName;
  }

  protected String getBuildingBlockNonHierarchicalName(BuildingBlock block) {
    String bubbleName = "";
    if (block instanceof HierarchicalEntity<?>) {
      HierarchicalEntity<?> hierarchicalEntity = (HierarchicalEntity<?>) block;
      bubbleName = hierarchicalEntity.getNonHierarchicalName();
    }
    else {
      bubbleName = block.getIdentityString();
    }
    return bubbleName;
  }

  /**
   * Retrieves the height of the corresponding legend in number of entries (rows) including the
   * header.
   * 
   * @param dimension
   *          The dimension for which the legend is to be created.
   * @return The number of rows in the legend.
   */
  protected int getLegendEntryCountForDimension(DimensionOptionsBean dimension) {
    int legendEntryCount = 0;

    if (dimension != null && dimension.getDimensionAttributeId().intValue() != GraphicalExportBaseOptions.NOTHING_SELECTED) {
      legendEntryCount = dimension.getAttributeValues().size() + 1;

      // Add unspecified value
      if (!(dimension.getDimensionAttributeId().intValue() == GraphicalExportBaseOptions.STATUS_SELECTED) && legendEntryCount > 0) {
        legendEntryCount++;
      }
    }

    return legendEntryCount;
  }

  protected ColorDimension getColorDimension() {
    return this.colorDimension;
  }

  protected void setColorDimension(ColorDimension colorDim) {
    this.colorDimension = colorDim;
  }

  protected AttributeTypeService getAttributeTypeService() {
    return attributeTypeService;
  }

  protected AttributeValueService getAttributeValueService() {
    return attributeValueService;
  }

  protected Locale getLocale() {
    return locale;
  }

  protected void setLocale(Locale locale) {
    this.locale = locale;
  }

}
