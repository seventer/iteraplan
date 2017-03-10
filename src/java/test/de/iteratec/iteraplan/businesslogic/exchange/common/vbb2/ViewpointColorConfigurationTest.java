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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WClassExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WTypeGroup;
import de.iteratec.iteraplan.elasticmi.metamodel.write.impl.WMetamodelImpl;
import de.iteratec.iteraplan.model.attribute.NumberAT;


public class ViewpointColorConfigurationTest {

  private static final String HEALTH  = "health";
  private static final String GOOD    = "Good";
  private static final String AVERAGE = "Average";
  private static final String BAD     = "Bad";

  private RMetamodel createRMetamodel() {
    WMetamodel wMetamodel = new WMetamodelImpl();
    WNominalEnumerationExpression enumeration = wMetamodel.createEnumeration("health", true);
    enumeration.createEnumerationLiteral(GOOD, Color.decode("#AFCEA8"));
    enumeration.createEnumerationLiteral(AVERAGE, Color.decode("#F6DF95"));
    enumeration.createEnumerationLiteral(BAD, Color.decode("#AFCEA8"));

    WTypeGroup wtg = wMetamodel.createTypeGroup("tg");
    WClassExpression wClass = wMetamodel.createClassExpression("classE", wtg);
    wClass.createProperty("decimalP", 0, 1, false, false, false, true, AtomicDataType.DECIMAL.type());

    return PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
  }

  @Test
  public void testEnumColorConfig() {
    //setup
    RMetamodel metmaodel = createRMetamodel();
    RNominalEnumerationExpression health = (RNominalEnumerationExpression) metmaodel.findValueTypeByPersistentName(HEALTH);
    List<String> availableColors = Lists.newArrayList("AFCEA8", "F6DF95");

    //object under test
    Map<String, String> colorConfigMap = new ViewpointColorConfiguration().createEnumColorConfig(health, false, availableColors);

    //assertions
    assertNotNull(colorConfigMap);
    assertTrue(colorConfigMap.get("orderedLiterals").contains("[\"Good\",\"Average\",\"Bad\","));
    assertEquals("discrete", colorConfigMap.get("decorationMode"));
    assertEquals("[\"AFCEA8\",\"F6DF95\",\"d3cfd1\"]", colorConfigMap.get("availableColors"));
    String selectedColors = colorConfigMap.get("selectedColors");
    assertTrue(selectedColors.contains("\"unspecified\":\"d3cfd1\"") || selectedColors.contains("\"unspezifiziert\":\"d3cfd1\""));
    assertTrue(selectedColors.contains("\"Good\":\"" + "AFCEA8\"".toLowerCase()));
    assertTrue(selectedColors.contains("\"Average\":\"" + "F6DF95\"".toLowerCase()));
    assertTrue(selectedColors.contains("\"Bad\":\"" + "AFCEA8\"".toLowerCase()));
  }

  @Test
  public void testTypeOfStatusColorConfig() {
    //setup
    RMetamodel metmaodel = createRMetamodel();
    RNominalEnumerationExpression health = (RNominalEnumerationExpression) metmaodel.findValueTypeByPersistentName(HEALTH);
    List<String> availableColors = Lists.newArrayList("AFCEA8", "F6DF95");

    //object under test
    Map<String, String> colorConfigMap = new ViewpointColorConfiguration().createEnumColorConfig(health, true, availableColors);

    //assertions
    assertNotNull(colorConfigMap);
    assertEquals("[\"Good\",\"Average\",\"Bad\"]", colorConfigMap.get("orderedLiterals"));
    assertEquals("discrete", colorConfigMap.get("decorationMode"));
    assertEquals("[\"AFCEA8\",\"F6DF95\",\"d3cfd1\"]", colorConfigMap.get("availableColors"));
    String selectedColors = colorConfigMap.get("selectedColors");
    assertTrue(selectedColors.contains("\"Good\":\"" + "AFCEA8\"".toLowerCase()));
    assertTrue(selectedColors.contains("\"Average\":\"" + "F6DF95\"".toLowerCase()));
    assertTrue(selectedColors.contains("\"Bad\":\"" + "AFCEA8\"".toLowerCase()));
  }

  @Test
  public void testDecimalColorConfig() {
    //setup
    RMetamodel rMetamodel = createRMetamodel();
    RStructuredTypeExpression type = rMetamodel.findStructuredTypeByPersistentName("classE");
    RPropertyExpression property = type.findPropertyByPersistentName("decimalP");

    NumberAT numberAt = new NumberAT();
    numberAt.setName("decimalP");
    numberAt.setMinValue(BigDecimal.ZERO);
    numberAt.setMaxValue(BigDecimal.TEN);

    //object under test
    Map<String, String> colorConfigMap = new ViewpointColorConfiguration().createDecimalColorConfig(property, numberAt);

    //assertions
    assertNotNull(colorConfigMap);
    assertEquals("continuous", colorConfigMap.get("decorationMode"));
    assertEquals("0", colorConfigMap.get("minValue"));
    assertEquals("10", colorConfigMap.get("maxValue"));
  }

}
