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
package de.iteratec.iteraplan.common;

import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

import org.junit.Test;

import de.iteratec.iteraplan.model.AbstractAssociation;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;


/**
 * Test cases for helper methods in {@link GeneralHelper}.
 */
public class GeneralHelperTest {

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessObjectInfos()} The
   * method tests if the getBusinessObjectInfos() returns empty string if the BusinessObject set is
   * empty too.
   */
  @Test
  public void testMakeConcatenatedNameStringForBbCollectionEmpty() {
    String expected = " ";
    assertEquals(expected, GeneralHelper.makeConcatenatedNameStringForBbCollection(null));
    assertEquals(expected, GeneralHelper.makeConcatenatedNameStringForBbCollection(new ArrayList<InformationSystemRelease>()));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getBusinessObjectInfos()} The
   * method tests if the getBusinessObjectInfos() returns correct String with all the
   * BusinessObjectsInfo.
   */
  @Test
  public void testMakeConcatenatedNameStringForBbCollectionNotEmpty() {
    Set<BusinessObject> set = hashSet();
    // BusinessObject
    BusinessObject firstPr = new BusinessObject();
    firstPr.setId(Integer.valueOf(45));
    firstPr.setName("first Product");
    firstPr.setDescription("test first Product");
    set.add(firstPr);

    BusinessObject secPr = new BusinessObject();
    secPr.setId(Integer.valueOf(50));
    secPr.setName("second Product");
    secPr.setDescription("test sec Product");
    set.add(secPr);

    BusinessObject thirdPr = new BusinessObject();
    thirdPr.setId(Integer.valueOf(55));
    thirdPr.setName("third Product");
    thirdPr.setDescription("testthird Product");
    set.add(thirdPr);

    BusinessObject fourthPr = new BusinessObject();
    fourthPr.setId(Integer.valueOf(65));
    fourthPr.setName("fourth Product");
    fourthPr.setDescription("test fourth Product");
    set.add(fourthPr);

    String actual = GeneralHelper.makeConcatenatedNameStringForBbCollection(set);

    assertTrue(actual.contains("first Product"));
    assertTrue(actual.contains("second Product"));
    assertTrue(actual.contains("third Product"));
    assertTrue(actual.contains("fourth Product"));

    assertFalse(actual.contains("fifth Product"));
    assertFalse(actual.contains("sixth Product"));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.common.GeneralHelper#makeConcatenatedNameStringForAssociationCollection} The
   * method tests if the makeConcatenatedNameStringForAssociationCollection() returns empty string if the BusinessObjectAssociations set is
   * empty too.
   */
  @Test
  public void testMakeConcatenatedNameStringForAssociationCollectionEmpty() {
    String expected = " ";
    assertEquals(expected, GeneralHelper.makeConcatenatedNameStringForAssociationCollection(null, true, false, false));
    assertEquals(expected, GeneralHelper.makeConcatenatedNameStringForAssociationCollection(null, true, true, false));
    assertEquals(expected, GeneralHelper.makeConcatenatedNameStringForAssociationCollection(null, true, true, true));

    assertEquals(expected,
 GeneralHelper.makeConcatenatedNameStringForAssociationCollection(
        new ArrayList<AbstractAssociation<? extends BuildingBlock, ? extends BuildingBlock>>(), false, true, false));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.common.GeneralHelper#makeConcatenatedNameStringForAssociationCollection} The
   * method tests if the makeConcatenatedNameStringForAssociationCollection() returns correct String with all the
   * BusinessObjectAssociationsInfo.
   */
  @Test
  public void testMakeConcatenatedNameStringForAssociationCollectionNotEmpty() {
    Set<BusinessObject> setBo = hashSet();
    
    // BusinessObject
    BusinessObject firstPr = new BusinessObject();
    firstPr.setId(Integer.valueOf(45));
    firstPr.setName("first Product");
    firstPr.setDescription("test first Product");
    setBo.add(firstPr);

    BusinessObject secPr = new BusinessObject();
    secPr.setId(Integer.valueOf(50));
    secPr.setName("second Product");
    secPr.setDescription("test sec Product");
    setBo.add(secPr);

    BusinessObject thirdPr = new BusinessObject();
    thirdPr.setId(Integer.valueOf(55));
    thirdPr.setName("third Product");
    thirdPr.setDescription("testthird Product");
    setBo.add(thirdPr);


    //AttributeTypes
    TextAT textAt = new TextAT();
    textAt.setName("text Attribute");

    NumberAT numberAt = new NumberAT();
    numberAt.setName("number Attribute");

    //AttributeValues
    TextAV textAv = new TextAV(textAt, "Text Value");
    NumberAV numberAv = new NumberAV(numberAt, new BigDecimal(1234));

    //Attributes
    AttributeValueAssignment textAttribute = new AttributeValueAssignment();
    textAttribute.setAttributeValue(textAv);
    Set<AttributeValueAssignment> textAttributeSet = hashSet();
    textAttributeSet.add(textAttribute);
    
    AttributeValueAssignment numberAttribute = new AttributeValueAssignment();
    numberAttribute.setAttributeValue(numberAv);
    Set<AttributeValueAssignment> numberAttributeSet = hashSet();
    numberAttributeSet.add(numberAttribute);

    Set<Isr2BoAssociation> assSet = hashSet();
    //Associations
    Isr2BoAssociation firstAss = new Isr2BoAssociation(new InformationSystemRelease(), firstPr);
    firstAss.setAttributeValueAssignments(textAttributeSet);
    assSet.add(firstAss);

    Isr2BoAssociation secondAss = new Isr2BoAssociation(new InformationSystemRelease(), secPr);
    secondAss.setAttributeValueAssignments(numberAttributeSet);
    assSet.add(secondAss);

    Isr2BoAssociation thirdAss = new Isr2BoAssociation(new InformationSystemRelease(), thirdPr);
    assSet.add(thirdAss);

    String actual = GeneralHelper.makeConcatenatedNameStringForAssociationCollection(assSet, false, false, false);
    assertTrue(actual.contains("first Product"));
    assertTrue(actual.contains("second Product"));
    assertTrue(actual.contains("third Product"));

    assertFalse(actual.contains("fifth Product"));
    assertFalse(actual.contains("sixth Product"));


    actual = GeneralHelper.makeConcatenatedNameStringForAssociationCollection(assSet, true, false, false);
    assertFalse(actual.contains("first Product"));
    assertFalse(actual.contains("Text Value"));

    actual = GeneralHelper.makeConcatenatedNameStringForAssociationCollection(assSet, false, true, false);
    assertTrue(actual.contains("first Product (Text Value)"));
    assertFalse(actual.contains("text Attribute"));

    actual = GeneralHelper.makeConcatenatedNameStringForAssociationCollection(assSet, false, true, true);
    assertTrue(actual.contains("(text Attribute: Text Value)"));

    actual = GeneralHelper.makeConcatenatedNameStringForAssociationCollection(assSet, false, false, true);
    assertFalse(actual.contains("Text Value"));
    assertFalse(actual.contains("text Attribute:"));
  }

}
