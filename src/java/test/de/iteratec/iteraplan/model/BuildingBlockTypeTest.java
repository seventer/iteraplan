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
package de.iteratec.iteraplan.model;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.TextAT;


/**
 * @author mma
 */
public class BuildingBlockTypeTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    // not yet used
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    // not yet used
  }

  private BuildingBlockType classUnderTest;

  /**
   * Test method for constructor
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#BuildingBlockType()}
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#getTypeOfBuildingBlock()}
   */
  @Test
  public void testBuildingBlockTypeOfBuildingBlock() {
    classUnderTest = new BuildingBlockType(TypeOfBuildingBlock.DUMMY);

    TypeOfBuildingBlock expected = TypeOfBuildingBlock.DUMMY;
    TypeOfBuildingBlock actual = classUnderTest.getTypeOfBuildingBlock();
    assertEquals(expected, actual);
  }

  /**
   * Test method for constructor
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#BuildingBlockType(de.iteratec.iteraplan.model.TypeOfBuildingBlock)}
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#setTypeOfBuildingBlock(de.iteratec.iteraplan.model.TypeOfBuildingBlock)}
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#setAvailableForAttributes(boolean)}
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#isAvailableForAttributes()}
   */
  @Test
  public void testBuildingBlockTypeTypeTypeOfBuildingBlockBoolean() {
    classUnderTest = new BuildingBlockType(TypeOfBuildingBlock.DUMMY, true);

    TypeOfBuildingBlock expectedType = TypeOfBuildingBlock.DUMMY;
    TypeOfBuildingBlock actualType = classUnderTest.getTypeOfBuildingBlock();

    boolean actualAvailableForAttribute = classUnderTest.isAvailableForAttributes();

    assertEquals(expectedType, actualType);
    assertTrue(actualAvailableForAttribute);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#getId()}
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#setId(java.lang.Integer)}
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#getOlVersion()}
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#setOlVersion(java.lang.Integer)} The
   * method has only meaning for the code coverage.
   */
  @Test
  public void testIdAndOlVersion() {
    classUnderTest = new BuildingBlockType();

    classUnderTest.setId(Integer.valueOf(50));
    classUnderTest.setOlVersion(Integer.valueOf(60));

    Integer expectedId = Integer.valueOf(50);
    Integer expectedOlVersion = Integer.valueOf(60);

    Integer actualId = classUnderTest.getId();
    Integer actualOlVersion = classUnderTest.getOlVersion();

    assertEquals(expectedId, actualId);
    assertEquals(expectedOlVersion, actualOlVersion);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#getAttributeTypes()}
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#setAttributeTypes(java.util.Set)} The
   * method has only meaning for the code coverage.
   */
  @Test
  public void testAttributeTypes() {
    classUnderTest = new BuildingBlockType();
    assertNotNull(classUnderTest.getAttributeTypes());

    Set<AttributeType> expected = hashSet();
    expected.add(new TextAT());

    classUnderTest.setAttributeTypes(expected);
    Set<AttributeType> actual = classUnderTest.getAttributeTypes();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#addAttributeTypeOneWay(de.iteratec.iteraplan.model.attribute.AttributeType)}
   * The method tests if the addAttributeTypeOneWay(AttributeType at) adds correctly AttributeType
   * in the attributeTypes.
   */
  @Test
  public void testAddAttributeTypeOneWay() {
    classUnderTest = new BuildingBlockType();
    assertNotNull(classUnderTest.getAttributeTypes());

    Set<AttributeType> expected = hashSet();
    TextAT firstEl = new TextAT();
    expected.add(firstEl);

    classUnderTest.addAttributeTypeOneWay(firstEl);

    Set<AttributeType> actual = classUnderTest.getAttributeTypes();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#addAttributeTypeTwoWay(de.iteratec.iteraplan.model.attribute.AttributeType)}
   * The method tests if the addAttributeTypeTwoWay(AttributeType at) adds correctly AttributeType
   * in the attributeTypes and the other side of the association is correctly set.
   */
  @Test
  public void testAddAttributeTypeTwoWay() {
    classUnderTest = new BuildingBlockType();
    assertNotNull(classUnderTest.getAttributeTypes());

    Set<AttributeType> expectedFirstSide = hashSet();
    TextAT firstEl = new TextAT();
    expectedFirstSide.add(firstEl);

    Set<BuildingBlockType> expectedSecSide = hashSet();
    expectedSecSide.add(classUnderTest);

    classUnderTest.addAttributeTypeTwoWay(firstEl);

    // test the first side of the association
    Set<AttributeType> actualFirstSide = classUnderTest.getAttributeTypes();
    assertEquals(expectedFirstSide, actualFirstSide);

    // test the second side of the association
    for (AttributeType at : classUnderTest.getAttributeTypes()) {
      Set<BuildingBlockType> actualSecSide = at.getBuildingBlockTypes();
      assertEquals(expectedSecSide, actualSecSide);
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#removeAttributeTypeOneWay(de.iteratec.iteraplan.model.attribute.AttributeType)}
   * The method tests if the removeAttributeTypeOneWay(AttributeType at) removes correctly
   * AttributeType from the attributeTypes.
   */
  @Test
  public void testRemoveAttributeTypeOneWay() {
    classUnderTest = new BuildingBlockType();
    assertNotNull(classUnderTest.getAttributeTypes());
    TextAT firstEl = new TextAT();

    Set<AttributeType> expected = hashSet();
    Set<AttributeType> initSet = hashSet();
    initSet.add(firstEl);

    classUnderTest.addAttributeTypeOneWay(firstEl);
    classUnderTest.removeAttributeTypeOneWay(firstEl);

    Set<AttributeType> actual = classUnderTest.getAttributeTypes();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#getAttributeTypesAsList()}
   * The method tests if the getAttributeTypesAsList() returns correctly the set attributeTypes as a
   * list.
   */
  @Test
  public void testGetAttributeTypesAsList() {
    classUnderTest = new BuildingBlockType();
    assertNotNull(classUnderTest.getAttributeTypes());

    TextAT firstEl = new TextAT();
    firstEl.setName("1");
    firstEl.setId(Integer.valueOf(60));
    TextAT secEl = new TextAT();
    secEl.setName("2");
    secEl.setId(Integer.valueOf(0));
    TextAT thirdEl = new TextAT();
    thirdEl.setName("3");
    thirdEl.setId(Integer.valueOf(40));

    List<AttributeType> expected = arrayList();
    expected.add(thirdEl);
    expected.add(firstEl);
    expected.add(secEl);

    Collections.sort(expected);

    Set<AttributeType> initSet = hashSet();
    initSet.add(firstEl);
    initSet.add(thirdEl);
    initSet.add(secEl);

    classUnderTest.setAttributeTypes(initSet);
    List<AttributeType> actual = classUnderTest.getAttributeTypesAsList();

    assertEquals(expected, actual);
    // assertEquals(expected.size(), actual.size());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#getName()}
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#getIdentityString()} The method has only
   * meaning for the test coverage.
   */
  @Test
  public void testGetNameAndGetIdentityString() {
    classUnderTest = new BuildingBlockType();
    classUnderTest.setTypeOfBuildingBlock(TypeOfBuildingBlock.ARCHITECTURALDOMAIN);

    String expected = "architecturalDomain.singular";
    String firstActual = classUnderTest.getName();
    String secActual = classUnderTest.getIdentityString();

    assertEquals(expected, firstActual);
    assertEquals(expected, secActual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#compareTo(de.iteratec.iteraplan.model.BuildingBlockType)}
   * The method tests if the compareTo() returns 0 correctly if the both object equals are.
   */
  @Test
  public void testCompareToEquals() {
    classUnderTest = new BuildingBlockType(TypeOfBuildingBlock.DUMMY);
    BuildingBlockType other = new BuildingBlockType(TypeOfBuildingBlock.DUMMY);

    int expected = 0;
    int actual = classUnderTest.compareTo(other);

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#compareTo(de.iteratec.iteraplan.model.BuildingBlockType)}
   * The method tests if the compareTo() returns positive value correctly if the this object bigger
   * is.
   */
  @Test
  public void testCompareToBigger() {
    classUnderTest = new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN);
    BuildingBlockType other = new BuildingBlockType(TypeOfBuildingBlock.DUMMY);

    int actual = classUnderTest.compareTo(other);

    assertTrue(actual > 0);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BuildingBlockType#compareTo(de.iteratec.iteraplan.model.BuildingBlockType)}
   * The method tests if the compareTo() returns negative value correctly if the this object smaller
   * is.
   */
  @Test
  public void testCompareToSmaller() {
    classUnderTest = new BuildingBlockType(TypeOfBuildingBlock.DUMMY);
    BuildingBlockType other = new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN);

    int actual = classUnderTest.compareTo(other);

    assertTrue(actual < 0);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#equals(java.lang.Object)}
   * The method tests if the equals() returns true correctly if the both object equals are.
   */
  @Test
  public void testEqualsFirstTrue() {
    classUnderTest = new BuildingBlockType(TypeOfBuildingBlock.DUMMY);

    Boolean expected = Boolean.TRUE;
    Boolean actual = Boolean.valueOf(classUnderTest.equals(classUnderTest));
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#equals(java.lang.Object)}
   * The method tests if the equals() returns true correctly.
   */
  @Test
  public void testEqualsSecTrue() {
    classUnderTest = new BuildingBlockType(TypeOfBuildingBlock.DUMMY);
    BuildingBlockType other = new BuildingBlockType(TypeOfBuildingBlock.DUMMY);

    Boolean expected = Boolean.TRUE;
    Boolean actual = Boolean.valueOf(classUnderTest.equals(other));
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#equals(java.lang.Object)}
   * The method tests if the equals() returns false correctly.
   */
  @Test
  public void testEqualsFirstFalse() {
    classUnderTest = new BuildingBlockType(TypeOfBuildingBlock.DUMMY);
    BuildingBlockType other = null;

    Boolean expected = Boolean.FALSE;
    Boolean actual = Boolean.valueOf(classUnderTest.equals(other));
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#equals(java.lang.Object)}
   * The method tests if the equals() returns false correctly.
   */
  @Test
  public void testEqualsSecFalse() {
    classUnderTest = new BuildingBlockType(TypeOfBuildingBlock.DUMMY);
    BusinessDomain other = new BusinessDomain();

    Boolean expected = Boolean.FALSE;
    Boolean actual = Boolean.valueOf(classUnderTest.equals(other));
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#equals(java.lang.Object)}
   * The method tests if the equals() returns false correctly.
   */
  @Test
  public void testEqualsThirdFalse() {
    classUnderTest = new BuildingBlockType(null);
    BuildingBlockType other = new BuildingBlockType(null);

    Boolean expected = Boolean.FALSE;
    Boolean actual = Boolean.valueOf(classUnderTest.equals(other));
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#hashCode()} The method
   * test if the hashCode() returns correct hashCode. The method has meaning for the code coverage.
   */
  @Test
  public void testHashCodeCaseWithID() {
    classUnderTest = new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN);

    int expected = (new BuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN)).hashCode();
    int actual = classUnderTest.hashCode();

    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BuildingBlockType#hashCode()} The method
   * test if the hashCode() returns correct hashCode. The method has meaning for the code coverage.
   */
  @Test
  public void testHashCodeCaseWithoutID() {
    classUnderTest = new BuildingBlockType();

    int expected = new BuildingBlockType().hashCode();
    int actual = classUnderTest.hashCode();

    assertEquals(expected, actual);
  }

}
