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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.elasticmi.metamodel.read.REnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.model.attribute.NumberAT;


/**
 * Creates initial configuration of color variables in a viewpoint configuration.
 * Covers following cases:
 * Discrete coloring on the basis of enumeration properties.
 * Continuous coloring on the basis of decimal properties.
 * Requires information from both elasticMI and iteraplan classic. The information must
 * be provided upon calling the corresponding methods.
 */
public final class ViewpointColorConfiguration {

  private static final String DECORATION_MODE            = "decorationMode";

  private static final String DECORATION_MODE_DISCRETE   = "discrete";
  private static final String DECORATION_MODE_CONTINUOUS = "continuous";

  //FIXME Those should probably end up in the cont.color vbb variable...
  private static final String MIN_VALUE                  = "minValue";
  private static final String MAX_VALUE                  = "maxValue";

  /**
   * decoration mode -> discrete
   * available colors -> JSON array of Strings hex rgb value
   * selected colors -> JSON map literal name -> color
   * orderedLiterals -> JSON array of the literals. ordering is provided by the metamodel.
   * 
   * @param enumeration
   * @param isTypeOfStatus
   * @param uiAvailableColors
   * @return
   *    The complete color configuration for an enumeration color variable.
   */
  public Map<String, String> createEnumColorConfig(RNominalEnumerationExpression enumeration, boolean isTypeOfStatus, List<String> uiAvailableColors) {
    Map<String, String> colorConfigMap = Maps.newHashMap();
    colorConfigMap.put(DECORATION_MODE, DECORATION_MODE_DISCRETE);

    Gson gson = new Gson();

    List<String> availableColors = Lists.newArrayList(uiAvailableColors);
    availableColors.add(Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);
    colorConfigMap.put("availableColors", gson.toJson(availableColors));

    Map<String, String> literal2color = Maps.newHashMap();
    List<String> orderedLiterals = Lists.newArrayList();
    for (REnumerationLiteralExpression literal : enumeration.getLiterals()) {
      //escaping of names, use localized names?
      literal2color.put(literal.getPersistentName(), Integer.toHexString(literal.getDefaultColor().getRGB()).substring(2));
      orderedLiterals.add(literal.getPersistentName());
    }
    if (!isTypeOfStatus) {
      literal2color.put(MessageAccess.getString(DimensionOptionsBean.DEFAULT_VALUE), Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);
      orderedLiterals.add(MessageAccess.getString(DimensionOptionsBean.DEFAULT_VALUE));
    }
    String serializedLiteralMappings = gson.toJson(literal2color);
    colorConfigMap.put("selectedColors", serializedLiteralMappings);
    colorConfigMap.put("orderedLiterals", gson.toJson(orderedLiterals));

    return colorConfigMap;
  }

  public Map<String, String> createDecimalColorConfig(RPropertyExpression property, NumberAT numberAt) {
    Map<String, String> colorConfigMap = Maps.newHashMap();
    colorConfigMap.put(DECORATION_MODE, DECORATION_MODE_CONTINUOUS);
    colorConfigMap.put(MIN_VALUE, numberAt.getMinValue() == null ? "" : numberAt.getMinValue().toString());
    colorConfigMap.put(MAX_VALUE, numberAt.getMaxValue() == null ? "" : numberAt.getMaxValue().toString());
    return colorConfigMap;
  }
}
