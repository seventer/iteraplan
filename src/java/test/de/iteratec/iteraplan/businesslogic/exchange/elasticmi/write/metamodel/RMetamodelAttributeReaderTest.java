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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.impl.RMetamodelAttributeTypeReader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.VirtualAttributeType;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WClassExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WFeatureExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WTypeGroup;
import de.iteratec.iteraplan.elasticmi.metamodel.write.impl.WMetamodelImpl;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


public class RMetamodelAttributeReaderTest {

  @Test
  public void testReadAttributes() {
    RMetamodel metamodel = getTestMetamodel();
    RMetamodelAttributeTypeReader reader = new RMetamodelAttributeTypeReader(metamodel, MessageListener.NOOP_LISTENER);
    List<VirtualAttributeType> vats = reader.readVirtualAttributes();

    assertEquals(7, vats.size());

    VirtualAttributeType enumAT = null;
    VirtualAttributeType decimalAT = null;
    VirtualAttributeType integerAT = null;
    VirtualAttributeType stringAT = null;
    VirtualAttributeType richTextAT = null;
    VirtualAttributeType dateAT = null;
    VirtualAttributeType dateTimeAT = null;

    for (VirtualAttributeType vat : vats) {
      if ("enumProp".equals(vat.getAtName())) {
        enumAT = vat;
      }
      else if ("decimalProp".equals(vat.getAtName())) {
        decimalAT = vat;
      }
      else if ("integerProp".equals(vat.getAtName())) {
        integerAT = vat;
      }
      else if ("stringProp".equals(vat.getAtName())) {
        stringAT = vat;
      }
      else if ("richTextProp".equals(vat.getAtName())) {
        richTextAT = vat;
      }
      else if ("dateProp".equals(vat.getAtName())) {
        dateAT = vat;
      }
      else if ("dateTimeProp".equals(vat.getAtName())) {
        dateTimeAT = vat;
      }
    }

    assertNotNull(enumAT);
    assertNotNull(decimalAT);
    assertNotNull(integerAT);
    assertNotNull(stringAT);
    assertNotNull(richTextAT);
    assertNotNull(dateAT);
    assertNotNull(dateTimeAT);

    assertTrue(enumAT.getAssociatedToBB().contains(TypeOfBuildingBlock.BUSINESSPROCESS));
    assertFalse(enumAT.isMandatory());
    assertFalse(enumAT.isMultivalue());
    assertEquals(EnumAT.class, enumAT.getAtType());
    assertTrue(enumAT.getEnumAV().contains("literal1"));
    assertTrue(enumAT.getEnumAV().contains("literal2"));
    assertTrue(enumAT.getEnumAV().contains("literal3"));

    assertTrue(decimalAT.getAssociatedToBB().contains(TypeOfBuildingBlock.BUSINESSPROCESS));
    assertTrue(decimalAT.getAssociatedToBB().contains(TypeOfBuildingBlock.BUSINESSUNIT));
    assertTrue(decimalAT.isMandatory());
    assertTrue(decimalAT.isMultivalue());
    assertEquals(NumberAT.class, decimalAT.getAtType());

    assertTrue(integerAT.getAssociatedToBB().contains(TypeOfBuildingBlock.BUSINESSPROCESS));
    assertTrue(integerAT.isMandatory());
    assertFalse(integerAT.isMultivalue());
    assertEquals(NumberAT.class, integerAT.getAtType());

    assertTrue(stringAT.getAssociatedToBB().contains(TypeOfBuildingBlock.BUSINESSPROCESS));
    assertFalse(stringAT.isMandatory());
    assertTrue(stringAT.isMultivalue());
    assertEquals(TextAT.class, stringAT.getAtType());

    assertTrue(richTextAT.getAssociatedToBB().contains(TypeOfBuildingBlock.BUSINESSPROCESS));
    assertFalse(richTextAT.isMandatory());
    assertFalse(richTextAT.isMultivalue());
    assertEquals(TextAT.class, richTextAT.getAtType());

    assertTrue(dateAT.getAssociatedToBB().contains(TypeOfBuildingBlock.BUSINESSPROCESS));
    assertFalse(dateAT.isMandatory());
    assertFalse(dateAT.isMultivalue());
    assertEquals(DateAT.class, dateAT.getAtType());

    assertTrue(dateTimeAT.getAssociatedToBB().contains(TypeOfBuildingBlock.BUSINESSPROCESS));
    assertTrue(dateTimeAT.isMandatory());
    assertFalse(dateTimeAT.isMultivalue());
    assertEquals(DateAT.class, dateTimeAT.getAtType());
  }

  public RMetamodel getTestMetamodel() {
    WMetamodelImpl wMM = new WMetamodelImpl();
    WNominalEnumerationExpression wNEE = wMM.createEnumeration("ENUM-de.iteratec.iteraplan.model.attribute.EnumAT-ENUM", false);
    wNEE.createEnumerationLiteral("literal1", null);
    wNEE.createEnumerationLiteral("literal2", null);
    wNEE.createEnumerationLiteral("literal3", null);
    WTypeGroup tg = wMM.createTypeGroup("DEFAULT TG");
    WClassExpression bp = wMM.createClassExpression("BusinessProcess", tg);
    bp.createProperty("enumProp", 0, 1, false, false, false, true, wNEE);
    bp.createProperty("decimalProp", 1, WFeatureExpression.UNLIMITED, false, false, false, true, AtomicDataType.DECIMAL.type());
    bp.createProperty("integerProp", 1, 1, false, false, false, true, AtomicDataType.INTEGER.type());
    bp.createProperty("stringProp", 0, WFeatureExpression.UNLIMITED, false, false, false, true, AtomicDataType.STRING.type());
    bp.createProperty("richTextProp", 0, 1, false, false, false, true, AtomicDataType.RICH_TEXT.type());
    bp.createProperty("dateProp", 0, 1, false, false, false, false, AtomicDataType.DATE.type());
    bp.createProperty("dateTimeProp", 1, 1, false, false, false, true, AtomicDataType.DATE_TIME.type());

    WClassExpression bu = wMM.createClassExpression("BusinessUnit", tg);
    bu.createProperty("decimalProp", 1, WFeatureExpression.UNLIMITED, false, false, false, true, AtomicDataType.DECIMAL.type());

    return PojoRMetamodelCopyUtil.rMetamodelFor(wMM);
  }
}
