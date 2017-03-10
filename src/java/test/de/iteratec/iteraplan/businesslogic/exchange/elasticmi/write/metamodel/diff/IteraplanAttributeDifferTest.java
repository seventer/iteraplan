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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.AttributeExtractorTest;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.VirtualAttributeType;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.TextAT;


public class IteraplanAttributeDifferTest extends AttributeExtractorTest {

  @Test
  public void testAttributeDiff() {
    initiateValues();
    List<VirtualAttributeType> result = new ArrayList<VirtualAttributeType>();
    fillVATTestList(result);

    IteraplanAttributeDiffer iAttributeDiffer = new MockedIteraplanAttributeDifferBuilder() // 
        .withAttribute("text", getTextAttributeType()) // 
        .withAttribute("number", getNumericAttributeType()) // 
        .withAttribute("enum1", getEnumerationAttributeType()) // 
        .withAttribute("enum2", null) // 
        .withAttribute("resp", getResponsibilityAttributeType()) // 
        .withBBT(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, getTechnicalComponent()) // 
        .withBBT(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, getInformationSystem()) // 
        .withBBT(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, getTechnicalComponent()) // 
        .withBBT(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, getInformationSystem()) // 
        .build(); //

    List<AtsApplicableChange> diffs = iAttributeDiffer.findDifferences(result);
    assertEquals(9, diffs.size());
  }

  @Test
  public void testAttributeDiffInvalidAT() {
    initiateValues();
    List<VirtualAttributeType> result = new ArrayList<VirtualAttributeType>();

    VirtualAttributeType vat = new VirtualAttributeType(TextAT.class, "invalid", false, true);
    vat.addAssociatedToBB(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    result.add(vat);

    IteraplanAttributeDiffer iAttributeDiffer = new MockedIteraplanAttributeDifferBuilder() // 
        .withAttribute("invalid", getNumericAttributeType()) // 
        .withBBT(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, getInformationSystem()) // 
        .build(); //

    try {
      iAttributeDiffer.findDifferences(result);
      fail();
    } catch (IteraplanBusinessException e) {
      assertEquals(IteraplanErrorMessages.GENERAL_IMPORT_ERROR, e.getErrorCode());
    }
  }

  @Test
  public void testAttributeDiffExistingLiteral() {
    initiateValues();
    List<VirtualAttributeType> result = new ArrayList<VirtualAttributeType>();

    VirtualAttributeType vat = new VirtualAttributeType(EnumAT.class, "enumWithExistingLiteral", false, true);
    vat.addAssociatedToBB(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    vat.addEnumAV("eins");
    result.add(vat);

    IteraplanAttributeDiffer iAttributeDiffer = new MockedIteraplanAttributeDifferBuilder() // 
        .withAttribute("enumWithExistingLiteral", getEnumerationAttributeType()) // 
        .withBBT(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, getInformationSystem()) // 
        .build(); //

    List<AtsApplicableChange> diffs = iAttributeDiffer.findDifferences(result);
    assertEquals(0, diffs.size());
  }

  @Test
  public void testEnumLiteralExists() {
    EnumAT enumAT = new EnumAT();
    EnumAV av1 = new EnumAV();
    av1.setName("TestAV");
    enumAT.addAttribueValueTwoWay(av1);

    IteraplanAttributeDiffer iAttributeDiffer = new MockedIteraplanAttributeDifferBuilder() // 
        .withAttribute("enumWithExistingLiteral", getNumericAttributeType()) // 
        .withBBT(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, getInformationSystem()) // 
        .build(); //

    assertTrue(iAttributeDiffer.enumLiteralExists(enumAT, "TestAV").isPresent()); // check for case-sensitive duplicate
    assertTrue(iAttributeDiffer.enumLiteralExists(enumAT, "testav").isPresent()); // check for case-insensitive duplicate
    assertFalse(iAttributeDiffer.enumLiteralExists(enumAT, "Another AV").isPresent());
  }

  @Test
  public void testForbiddenAttributeTypeName() {
    IteraplanAttributeDiffer iAttributeDiffer = new MockedIteraplanAttributeDifferBuilder().build();
    assertTrue(iAttributeDiffer.isForbiddenAttributeTypeName("id"));
    assertTrue(iAttributeDiffer.isForbiddenAttributeTypeName("NAME"));
    assertTrue(iAttributeDiffer.isForbiddenAttributeTypeName("dEsCRipTioN"));
    assertFalse(iAttributeDiffer.isForbiddenAttributeTypeName("something else"));
  }
}
