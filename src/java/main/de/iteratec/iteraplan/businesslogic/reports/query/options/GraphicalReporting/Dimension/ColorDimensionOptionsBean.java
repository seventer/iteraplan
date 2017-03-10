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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DefaultColorAttributeValue;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


public class ColorDimensionOptionsBean extends DimensionOptionsBean implements Serializable {

  public static final Logger              LOGGER              = Logger.getIteraplanLogger(ColorDimensionOptionsBean.class);

  /** Serialization version. */
  private static final long               serialVersionUID    = -9140186996879407617L;

  public static final String              LOWER_BOUND_VALUE   = "global.lowerbound.short";
  public static final String              UPPER_BOUND_VALUE   = "global.upperbound.short";

  private String                          defaultColor        = SpringGuiFactory.getInstance().getDefaultColor();
  private List<String>                    availableColors     = Lists.newArrayList();
  private Map<String, NameToColorWrapper> valueToColorMap     = Maps.newLinkedHashMap();

  /** if set to true, the color range box will be rendered. This should be done for numberATs. */
  private boolean                         colorRangeAvailable = false;

  /** if set to true, a color will be used. (only for numberAT) */
  private boolean                         useColorRange       = false;

  @Override
  public void switchToGenerationMode() {
    this.setCurrentMode(DimensionOptionsMode.GENERATION);

    if (isCustomAttribute(getDimensionAttributeId().intValue())) {
      return;
    }

    if (!valueToColorMap.isEmpty()) {
      defaultColor = valueToColorMap.get(DEFAULT_VALUE).getColor();
      valueToColorMap.remove(DEFAULT_VALUE);
    }
  }

  @Override
  public void switchToPresentationMode() {
    this.setCurrentMode(DimensionOptionsMode.PRESENTATION);

    if (isCustomAttribute(getDimensionAttributeId().intValue())) {
      return;
    }

    if (!valueToColorMap.isEmpty()) {
      valueToColorMap.put(DEFAULT_VALUE, new NameToColorWrapper(DEFAULT_VALUE, defaultColor));
      defaultColor = SpringGuiFactory.getInstance().getDefaultColor();
    }
  }

  @Override
  public void refresh(List<String> dimensionAttributeValues) {
    // Error checking
    if (!DimensionOptionsMode.PRESENTATION.equals(this.getCurrentMode())) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    // Remove the unspecified colour from the list of available colours to avoid its usage as a
    // colour for a defined by default.
    availableColors.remove(defaultColor);

    // Create the colour - attribute value mapping
    setAttributeValues(dimensionAttributeValues);

    // Add unspecified colour and option
    if (!isCustomAttribute(getDimensionAttributeId().intValue()) && !valueToColorMap.isEmpty()) {
      availableColors.add(defaultColor);
    }
  }

  @Override
  public void matchValuesFromSavedQuery(List<String> savedValues, List<String> savedColours) {
    if (savedValues == null || savedColours == null || savedValues.size() != savedColours.size()) {
      LOGGER.debug("Saved attribute values and saved selected colours don't match. Using standard mapping.");
      return;
    }

    Set<String> attributeValues = new LinkedHashSet<String>(getAttributeValues());

    for (int i = 0; i < savedValues.size(); i++) {
      String value = savedValues.get(i);
      if (attributeValues.contains(value)) {
        setSelectedColor(value, savedColours.get(i));
      }
    }
  }

  public String getDefaultColor() {
    return defaultColor;
  }

  public void setDefaultColor(String defaultColor) {
    this.defaultColor = defaultColor;
  }

  /**
   * @return Returns the availableColors.
   */
  public List<String> getAvailableColors() {
    return availableColors;
  }

  /**
   * @param availableColors
   *          The availableColors to set.
   */
  public void setAvailableColors(List<String> availableColors) {
    this.availableColors = availableColors;
  }

  /**
   * @return Returns the colorAttributeValues.
   */
  @Override
  public List<String> getAttributeValues() {
    return new ArrayList<String>(valueToColorMap.keySet());
  }

  /**
   * @param colorAttributeValues
   *          The colorAttributeValues to set.
   */
  @Override
  protected void setAttributeValues(List<String> colorAttributeValues) {
    List<String> attributeValues = (colorAttributeValues != null ? colorAttributeValues : new ArrayList<String>());
    int numAvailableColors = getAvailableColors().size();
    int dimensionAttributeId = getDimensionAttributeId().intValue();

    resetValueToColorMap();
    if (!isUseColorRange()) {

      AttributeValueService attributeValueService = SpringServiceFactory.getAttributeValueService(); // NOPMD
      List<? extends AttributeValue> avList = Lists.newArrayList();

      if (dimensionAttributeId > 0) {
        avList = attributeValueService.getAllAVs(getDimensionAttributeId());
      }

      for (int i = 0; i < attributeValues.size(); i++) {
        String av = attributeValues.get(i);

        // either fetch the color or iterate through the available colors
        String colorValue = calculateColor(av, avList, getAvailableColors().get(i % numAvailableColors));

        this.valueToColorMap.put(escapeAVKey(av), new NameToColorWrapper(av, colorValue));
      }

    }
    else if (attributeValues.size() != 0) {
      this.valueToColorMap.put(LOWER_BOUND_VALUE, new NameToColorWrapper(LOWER_BOUND_VALUE, getAvailableColors().get(0)));
      this.valueToColorMap.put(UPPER_BOUND_VALUE, new NameToColorWrapper(UPPER_BOUND_VALUE, getAvailableColors().get(1)));
    }

    // Add unspecified colour and option
    if (!isCustomAttribute(dimensionAttributeId) && (dimensionAttributeId > 0 || !attributeValues.isEmpty())) {
      valueToColorMap.put(DEFAULT_VALUE, new NameToColorWrapper(DEFAULT_VALUE, defaultColor));
    }
  }

  /**
   * Escapes the attribute values name to be able to use it as a key in the valueToColorMap.<br />
   * Currently escapes single quotes, opening and closing square brackets as well as the EL-prefix <code>${</code>.<br />
   * Refer to <a href="http://docs.oracle.com/javaee/1.4/tutorial/doc/JSPIntro7.html#wp71088">http://docs.oracle.com/javaee/1.4/tutorial/doc/JSPIntro7.html#wp71088</a>
   * for further chars and char-sequences that may be escaped
   * <ul>
   * <li>escaping <code>'</code> avoids premature end of string</li>
   * <li>escaping <code>[</code> and <code>]</code> avoids premature end of map access</li>
   * <li>escaping <code>${</code> avoids the string being interpreted as EL-Expression</li>
   * </ul>
   * 
   * @param value the string to escape
   * @return the escaped string
   */
  private String escapeAVKey(String value) {
    String result = value.replace("\\", "\\\\"); //escape escape-prefix
    result = result.replace("'", "\\&#39;");
    result = result.replace("[", "\\&#91;");
    result = result.replace("]", "\\&#93;");
    result = result.replace("${", "\\&#36;\\&#123;");
    return result;
  }

  /**
   * TODO check if this method is necessary
   * Determines which color to use for a given attribute value name
   * @param avString
   *          The value string of the attribute value
   * @param avList
   *          List containing the actual AttributeValues
   * @param fallBackColor
   *          Color to use if no default color exists 
   * @return The color to use for the given attribute value name
   */
  private String calculateColor(String avString, List<? extends AttributeValue> avList, String fallBackColor) {
    for (AttributeValue value : avList) {
      // look for the right attribute value
      if (value.getValueString().equals(avString)) {
        if (value instanceof DefaultColorAttributeValue) {
          return ((DefaultColorAttributeValue) value).getDefaultColorHex();
        }
        else {
          break;
        }
      }
    }

    return fallBackColor;
  }

  public List<String> getSelectedColors() {
    Collection<String> result = Collections2.transform(valueToColorMap.values(), new Function<NameToColorWrapper, String>() {
      public String apply(NameToColorWrapper input) {
        return input.getColor();
      }
    });
    return new ArrayList<String>(result);
  }

  /**
   * resets the value to color mapping
   */
  public void resetValueToColorMap() {
    this.valueToColorMap = Maps.newLinkedHashMap();
  }

  /**
   * @return The value to color mapping
   */
  public Map<String, NameToColorWrapper> getValueToColorMap() {
    return valueToColorMap;
  }

  /**
   * Sets the colour on a given value
   * 
   * @param attributeValue
   *          The attribute value the Colour belongs to
   * @param colour
   *          The Colour to set
   */
  public void setSelectedColor(String attributeValue, String colour) {
    valueToColorMap.put(escapeAVKey(attributeValue), new NameToColorWrapper(attributeValue, colour));
  }

  public boolean isUseColorRange() {
    return useColorRange;
  }

  public void setUseColorRange(boolean useColorRange) {
    if (this.useColorRange != useColorRange) {
      setToRefresh(true);
    }
    this.useColorRange = useColorRange;
  }

  public String getLowerBoundMessageKey() {
    return LOWER_BOUND_VALUE;
  }

  public String getUpperBoundMessageKey() {
    return UPPER_BOUND_VALUE;
  }

  public boolean isColorRangeAvailable() {
    return colorRangeAvailable;
  }

  public void setColorRangeAvailable(boolean colorRangeAvailable) {
    this.colorRangeAvailable = colorRangeAvailable;
    if (!colorRangeAvailable) {
      setUseColorRange(false);
    }
  }

  /**
   * Wraps an unexcaped name of an attribute value and the corresponding color
   */
  public static final class NameToColorWrapper implements Serializable {
    private static final long serialVersionUID = 1L;

    private String            name;
    private String            color;

    public NameToColorWrapper(String name, String color) {
      this.name = name;
      this.color = color;
    }

    public String getName() {
      return name;
    }

    public String getColor() {
      return color;
    }

    public void setColor(String color) {
      this.color = color;
    }

    @Override
    public String toString() {
      return color;
    }
  }
}
