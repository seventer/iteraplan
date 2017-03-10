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
package de.iteratec.iteraplan.model.ApplicationLandscape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;


public class Isr2BoAssociationTest {

  private InformationSystemRelease isr1;
  private InformationSystemRelease isr2;
  private BusinessObject           bo1;
  private BusinessObject           bo2;

  private Isr2BoAssociation        association;

  @SuppressWarnings("boxing")
  @Before
  public void setUp() throws Exception {
    InformationSystem is = new InformationSystem();
    is.setName("MyInfoSystem");
    isr1 = new InformationSystemRelease();
    isr2 = new InformationSystemRelease();
    isr1.setId(34);
    isr1.setId(36);
    isr1.setInformationSystem(is);
    isr2.setInformationSystem(is);

    bo1 = new BusinessObject();
    bo2 = new BusinessObject();
    bo1.setId(64);
    bo2.setId(67);
    bo1.setName("MyBusObject");
    bo2.setName("MyBusObject");

  }

  @Test
  public void testGetTypeOfBuildingBlock() {
    association = new Isr2BoAssociation(isr1, bo1);
    assertEquals(TypeOfBuildingBlock.ISR2BOASSOCIATION, association.getTypeOfBuildingBlock());

  }

  @SuppressWarnings("boxing")
  @Test
  public void testIsr2BoAssociation() {
    association = new Isr2BoAssociation();
    association.setBusinessObject(bo2);
    association.setInformationSystemRelease(isr1);
    association.setId(3456);

    Isr2BoAssociation assoc2 = new Isr2BoAssociation(isr1, bo2);
    assoc2.setId(23234);
    assertEquals("Two association objects should be 'equal' even with different IDs", assoc2, association);
  }

  /**
   * Tests proper instantiation as well as correct insertion behaviour at the connected objects.
   * Test method for {@link Isr2BoAssociation#connect()} and {@link Isr2BoAssociation#disconnect()}
   */
  @Test
  public void testConnectDisconnect() {
    association = new Isr2BoAssociation(isr1, bo1);
    association.setId(Integer.valueOf(1));
    assertFalse("Instantiation of the association class should leave connected building block untouched", isr1.getBusinessObjectAssociations()
        .contains(association));

    int setSizePreAdd = isr1.getBusinessObjectAssociations().size();
    int ieSetSizePreAdd = bo1.getInformationSystemReleaseAssociations().size();
    association.connect();
    assertTrue("Instantiation of the association class should leave connected building block untouched", isr1.getBusinessObjectAssociations()
        .contains(association));
    assertTrue("Instantiation of the association class should leave connected building block untouched", bo1
        .getInformationSystemReleaseAssociations().contains(association));
    assertEquals("set should grow by one", setSizePreAdd + 1, isr1.getBusinessObjectAssociations().size());
    assertEquals("set should grow by one", ieSetSizePreAdd + 1, bo1.getInformationSystemReleaseAssociations().size());

    // re-connecting should be a no-op
    association.connect();
    assertEquals("set should not grow after repeated adds", setSizePreAdd + 1, isr1.getBusinessObjectAssociations().size());
    assertEquals("set should not grow after repeated adds", ieSetSizePreAdd + 1, bo1.getInformationSystemReleaseAssociations().size());

    association.disconnect();
    assertEquals("disconnect should reduce the set size", setSizePreAdd, isr1.getBusinessObjectAssociations().size());
    assertEquals("disconnect should reduce the set size", ieSetSizePreAdd, bo1.getInformationSystemReleaseAssociations().size());
  }

  /**
   * Test method for {@link Isr2BoAssociation#connect()}
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConnectNull() {
    association = new Isr2BoAssociation();
    association.connect();
  }

  /**
   * Test method for {@link Isr2BoAssociation#disconnect()}
   */
  @Test(expected = IllegalArgumentException.class)
  public void testDisconnectNull() {
    association = new Isr2BoAssociation();
    association.disconnect();
  }

  @SuppressWarnings("boxing")
  @Test
  public void testHashCode() {
    association = new Isr2BoAssociation(isr2, bo1);
    Isr2BoAssociation assoc2 = new Isr2BoAssociation(isr2, bo2);
    Isr2BoAssociation assoc3 = new Isr2BoAssociation(isr2, bo2);

    assertEquals(assoc2.hashCode(), assoc3.hashCode());
    assertNotSame("associations with different ends should have different hashcodes", association.hashCode(), assoc2.hashCode());
    assertNotSame("associations with different ends should have different hashcodes", association.hashCode(), assoc3.hashCode());

    createAttributesOnAssociations(assoc2, assoc3);
    assertEquals("even with different attributes, these association objects should have same hashCodes", assoc2.hashCode(), assoc3.hashCode());

  }

  @Test
  public void testEqualsObject() {
    association = new Isr2BoAssociation(isr2, bo1);
    Isr2BoAssociation assoc2 = new Isr2BoAssociation(isr2, bo2);
    Isr2BoAssociation assoc3 = new Isr2BoAssociation(isr2, bo2);

    assertEquals("equal ends on different objects means they should be equal", assoc2, assoc3);
    Assert.assertNotSame("Should be equal, but not the same objects", assoc2, assoc3);

    assertFalse("associations with different ends are never equal", association.equals(assoc2));
    assertFalse("associations with different ends are never equal", association.equals(assoc3));

    createAttributesOnAssociations(assoc2, assoc3);

    assertEquals("even with different attributes, these association objects should be considered equal", assoc2, assoc3);

  }

  private void createAttributesOnAssociations(Isr2BoAssociation assoc2, Isr2BoAssociation assoc3) {
    // let's construct two associations with different attribute value assignments and see how they
    // behave under equals
    EnumAT enumAt = new EnumAT();
    enumAt.setMultiassignmenttype(true);
    EnumAV enum1 = new EnumAV();
    enum1.setName("foo1");
    enum1.setAttributeTypeTwoWay(enumAt);
    EnumAV enum2 = new EnumAV();
    enum2.setName("foo2");
    enum2.setAttributeTypeTwoWay(enumAt);
    EnumAV enum3 = new EnumAV();
    enum3.setName("foo3");
    enum3.setAttributeTypeTwoWay(enumAt);

    AttributeValueAssignment enum1ava1 = new AttributeValueAssignment(assoc2, enum1);
    AttributeValueAssignment enum2ava1 = new AttributeValueAssignment(assoc2, enum2);
    Set<AttributeValueAssignment> avas1 = new HashSet<AttributeValueAssignment>();
    avas1.add(enum1ava1);
    avas1.add(enum2ava1);
    assoc2.setAttributeValueAssignments(avas1);

    AttributeValueAssignment enum2ava2 = new AttributeValueAssignment(assoc3, enum2);
    AttributeValueAssignment enum3ava2 = new AttributeValueAssignment(assoc3, enum3);
    Set<AttributeValueAssignment> avas2 = new HashSet<AttributeValueAssignment>();
    assoc2.setAttributeValueAssignments(avas1);
    avas2.add(enum2ava2);
    avas2.add(enum3ava2);
    assoc3.setAttributeValueAssignments(avas2);
  }

}
