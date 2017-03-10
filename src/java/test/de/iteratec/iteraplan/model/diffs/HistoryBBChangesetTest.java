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
package de.iteratec.iteraplan.model.diffs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.service.diffs.BBChangesetFactory;
import de.iteratec.iteraplan.diffs.model.BusinessFunctionChangeset;
import de.iteratec.iteraplan.diffs.model.BusinessUnitChangeset;
import de.iteratec.iteraplan.diffs.model.HistoryBBAttributeGroupChangeset;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.diffs.model.InformationSystemReleaseChangeset;
import de.iteratec.iteraplan.diffs.model.InterfaceChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;

/**
 * Tests the HistoryBBChangeset class
 */
@SuppressWarnings("boxing")
public class HistoryBBChangesetTest {

  BusinessUnit               buA;
  BusinessUnit               buB;

  BusinessFunction           bfA;
  BusinessFunction           bfB;

  InformationSystem          isA;
  InformationSystem          isB;
  InformationSystemRelease   isrA;
  InformationSystemRelease   isrB;

  InformationSystemRelease   isr2;
  InformationSystemRelease   isr3;
  InformationSystemRelease   isr4;

  InformationSystemInterface isiA;
  InformationSystemInterface isiB;

  TechnicalComponent         tcA;
  TechnicalComponent         tcB;
  TechnicalComponentRelease  tcrA;
  TechnicalComponentRelease  tcrB;

  @Before
  public void setUp() throws Exception {

    createBusinessUnits();

    createInformationsystemReleases();

    createInterfaces();

    createBusinessFunctions();
  }

  private void createBusinessUnits() {
    // AbstractHierarchicalEntity / BU
    //  ver A
    buA = new BusinessUnit();
    buA.setName("bbTestBU");
    buA.setId(1);
    buA.setDescription("A");
    // child A
    BusinessUnit buAchild = new BusinessUnit();
    buAchild.setName("buA child");
    buAchild.setId(101);
    buAchild.addParent(buA);

    //  ver B
    buB = new BusinessUnit();
    buB.setName("bb Test BU Updated");
    buB.setId(1);
    buB.setDescription("B");
    // child B
    BusinessUnit buBchild = new BusinessUnit();
    buBchild.setName("buB child");
    buBchild.setId(102);
    buBchild.addParent(buB);
    // parent
    BusinessUnit buBparent = new BusinessUnit();
    buBparent.setName("buB Parent");
    buBparent.setId(103);
    buB.addParent(buBparent);

    associateAVAs(buA, buB);
  }

  private void createBusinessFunctions() {
    //  ver A
    bfA = new BusinessFunction();
    bfA.setName("bbTestBF");
    bfA.setId(13);
    bfA.setDescription("A");

    // rev B
    bfB = new BusinessFunction();
    bfB.setName("bb Test BF Updated");
    bfB.setId(13);
    bfB.setDescription("B");

    associateAVAs(buA, buB);
  }

  private void createInformationsystemReleases() {
    // ISR /////////////
    // ver A
    isA = new InformationSystem();
    isA.setId(2);
    isA.setName("IS A");

    isrA = new InformationSystemRelease();
    isrA.setId(3);
    isrA.setVersion("ISR A");
    isrA.setDescription("ISR A");
    isA.addRelease(isrA);

    // ver B
    isB = new InformationSystem();
    isB.setId(2);
    isB.setName("IS B");
    isrB = new InformationSystemRelease();
    isrB.setId(3);
    isrB.setVersion("ISR B");
    isrB.setDescription("ISR B");
    isA.addRelease(isrB);

    // ISR2
    isr2 = new InformationSystemRelease();
    isr2.setId(32);
    isr2.setVersion("ISR2");
    isA.addRelease(isr2);

    // ISR3
    isr3 = new InformationSystemRelease();
    isr3.setId(33);
    isr3.setVersion("ISR3");
    isA.addRelease(isr3);

    // ISR4
    isr4 = new InformationSystemRelease();
    isr4.setId(34);
    isr4.setVersion("ISR4");
    isA.addRelease(isr4);
  }

  private void createInterfaces() {
    // ISI /////////////
    // ver A
    isiA = new InformationSystemInterface();
    isiA.setId(4);
    isiA.setDescription("ISI A");
    isiA.addReleaseA(isrA);
    isiA.addReleaseB(isr2);

    // ver B
    isiB = new InformationSystemInterface();
    isiB.setId(4);
    isiB.setDescription("ISI B");
    isiB.addReleaseA(isr3);
    isiB.addReleaseB(isr4);

    // TCR /////////////
    // ver A
    tcA = new TechnicalComponent();
    tcA.setId(6);
    tcA.setName("TC A");

    tcrA = new TechnicalComponentRelease();
    tcrA.setId(7);
    tcrA.setDescription("TCR A");
    tcA.addRelease(tcrA);

    // ver B
    tcB = new TechnicalComponent();
    tcB.setId(6);
    tcB.setName("TC B");

    tcrB = new TechnicalComponentRelease();
    tcrB.setId(7);
    tcrB.setDescription("TCR B");
    tcB.addRelease(tcrB);
  }

  /**
   * Creates 4 Attribute cases between two BUs: Added, Deleted, Changed, Equal
   */
  private void associateAVAs(BuildingBlock bblockA, BuildingBlock bblockB) {
    // bblockA only Attribute
    // AT
    TextAT at1 = new TextAT();
    at1.setId(1);
    at1.setName("AT1");

    // ATG
    AttributeTypeGroup atg = new AttributeTypeGroup();
    atg.addAttributeTypeTwoWay(at1);

    // AV
    TextAV av1 = new TextAV();
    av1.setValue("AV1");
    av1.setAttributeTypeTwoWay(at1);

    // AVA
    AttributeValueAssignment avaA1 = new AttributeValueAssignment();
    avaA1.setAttributeValue(av1);
    Set<AttributeValueAssignment> setAvaA = new HashSet<AttributeValueAssignment>();
    setAvaA.add(avaA1);

    //
    // bblockB only Attribute
    // AT
    TextAT at2 = new TextAT();
    at2.setId(2);
    at2.setName("AT2");
    // ATG
    atg.addAttributeTypeTwoWay(at2);

    // AV
    TextAV av2 = new TextAV();
    av2.setValue("AV2");
    av2.setAttributeTypeTwoWay(at2);

    // AVA
    AttributeValueAssignment avaB2 = new AttributeValueAssignment();
    avaB2.setAttributeValue(av2);
    Set<AttributeValueAssignment> setAvaB = new HashSet<AttributeValueAssignment>();
    setAvaB.add(avaB2);

    //
    // buA+B both Attribute (Change)
    // AV
    TextAV av3a = new TextAV();
    av3a.setValue("AV3a");
    TextAV av3b = new TextAV();
    av3b.setValue("AV3b");

    // AT
    TextAT at3 = new TextAT();
    at3.setId(3);
    at3.setName("AT3");
    av3a.setAttributeTypeTwoWay(at3);
    av3b.setAttributeTypeTwoWay(at3);
    // ATG - Use the same
    atg.addAttributeTypeTwoWay(at3);

    // AVA A
    AttributeValueAssignment avaA3 = new AttributeValueAssignment();
    avaA3.setAttributeValue(av3a);
    setAvaA.add(avaA3);
    // AVA B
    AttributeValueAssignment avaB3 = new AttributeValueAssignment();
    avaB3.setAttributeValue(av3b);
    setAvaB.add(avaB3);

    //
    // buA+B both Attribute (No Change)
    // AV
    TextAV av4a = new TextAV();
    av4a.setValue("AV4x");
    TextAV av4b = new TextAV();
    av4b.setValue("AV4x");

    // AT
    TextAT at4 = new TextAT();
    at4.setId(4);
    at4.setName("AT4");
    av4a.setAttributeTypeTwoWay(at4);
    av4b.setAttributeTypeTwoWay(at4);

    // ATG - Use the same
    atg.addAttributeTypeTwoWay(at4);

    // AVA A
    AttributeValueAssignment avaA4 = new AttributeValueAssignment();
    avaA4.setAttributeValue(av4a);
    setAvaA.add(avaA4);
    // AVA B
    AttributeValueAssignment avaB4 = new AttributeValueAssignment();
    avaB4.setAttributeValue(av4b);
    setAvaB.add(avaB4);

    // Associate AVAs with BUs
    bblockA.setAttributeValueAssignments(setAvaA);
    bblockB.setAttributeValueAssignments(setAvaB);
  }

  @Test
  public void changesetInitialWithAuthor() {
    // with author
    HistoryBBChangeset changeset = BBChangesetFactory.createChangeset(null, buA, "bob", new DateTime());

    Assert.assertTrue(changeset.isInitialChangeset());
    Assert.assertTrue(changeset.isHasAuthor());
    Assert.assertEquals("bob", changeset.getAuthor());
  }

  @Test
  public void changesetInitialWithoutAuthor() {
    // without author
    HistoryBBChangeset changeset = BBChangesetFactory.createChangeset(null, buA, "", new DateTime());

    Assert.assertFalse(changeset.isHasAuthor());
  }

  @Test
  public void changesetAbstractHierarchicalEntity() {
    BusinessUnitChangeset changeset = (BusinessUnitChangeset) BBChangesetFactory.createChangeset(buA, buB, "bob", new DateTime());

    Assert.assertFalse(changeset.isInitialChangeset());
    Assert.assertTrue(changeset.isHasAuthor());
    Assert.assertEquals("bob", changeset.getAuthor());

    Assert.assertTrue(changeset.isBasicPropertiesChanged());
    Assert.assertTrue(changeset.isNameChanged());
    Assert.assertEquals("bbTestBU", changeset.getNameFrom());
    Assert.assertEquals("bb Test BU Updated", changeset.getNameTo());

    Assert.assertTrue(changeset.isDescriptionChanged());
    Assert.assertEquals("A", changeset.getDescriptionFrom());
    Assert.assertEquals("B", changeset.getDescriptionTo());

    // Hierarchy
    // parent
    Assert.assertTrue(changeset.isChildrenChanged());
    Assert.assertTrue(changeset.isParentChanged());
    Assert.assertEquals("", changeset.getParentFromName());
    Assert.assertEquals("buB Parent", changeset.getParentToName());
    // children
    List<BuildingBlock> cAdded = changeset.getChildrenAdded();
    Assert.assertEquals("buB child", cAdded.get(0).getNonHierarchicalName());
    Assert.assertEquals("buB child", changeset.getChildrenAddedNames().get(0));
    List<BuildingBlock> cRemoved = changeset.getChildrenRemoved();
    Assert.assertEquals("buA child", cRemoved.get(0).getNonHierarchicalName());
    Assert.assertEquals("buA child", changeset.getChildrenRemovedNames().get(0));

    // Attributes
    List<HistoryBBAttributeGroupChangeset> atgChangesets = changeset.getAttributeGroupChangesets();
    Assert.assertEquals(1, atgChangesets.size());
    HistoryBBAttributeGroupChangeset atgChangeset = atgChangesets.get(0);

    List<String[]> changedAttributes = atgChangeset.getChangedAttributes();
    Assert.assertEquals(3, changedAttributes.size());

    String[] delAtt = changedAttributes.get(0);
    Assert.assertEquals("AT1", delAtt[0]);
    Assert.assertEquals("AV1", delAtt[1]);
    Assert.assertEquals("", delAtt[2]);

    String[] addAtt = changedAttributes.get(1);
    Assert.assertEquals("AT2", addAtt[0]);
    Assert.assertEquals("", addAtt[1]);
    Assert.assertEquals("AV2", addAtt[2]);

    String[] modAtt = changedAttributes.get(2);
    Assert.assertEquals("AT3", modAtt[0]);
    Assert.assertEquals("AV3a", modAtt[1]);
    Assert.assertEquals("AV3b", modAtt[2]);
  }

  @Test
  public void changesetISR() {
    HistoryBBChangeset changeset = BBChangesetFactory.createChangeset(isrA, isrB, "", new DateTime());

    Assert.assertFalse(changeset.isHasAuthor());
    Assert.assertTrue(changeset.isDescriptionChanged());

    Assert.assertTrue(changeset.isNameChanged());
    Assert.assertEquals("IS A # ISR A", changeset.getNameFrom());
    Assert.assertEquals("IS A # ISR B", changeset.getNameTo());

  }

  @Test
  public void changesetISI() {
    InterfaceChangeset changeset = (InterfaceChangeset) BBChangesetFactory.createChangeset(isiA, isiB, "", new DateTime());

    Assert.assertFalse(changeset.isHasAuthor());

    Assert.assertTrue(changeset.isDescriptionChanged());
    Assert.assertEquals("ISI A", changeset.getDescriptionFrom());
    Assert.assertEquals("ISI B", changeset.getDescriptionTo());

    Assert.assertTrue(changeset.isIsrAChanged());
    Assert.assertEquals("IS A # ISR A", changeset.getRemovedInformationSystemA().getNonHierarchicalName());
    Assert.assertEquals("IS A # ISR3", changeset.getAddedInformationSystemA().getNonHierarchicalName());

    Assert.assertTrue(changeset.isIsrBChanged());
    Assert.assertEquals("IS A # ISR2", changeset.getRemovedInformationSystemB().getNonHierarchicalName());
    Assert.assertEquals("IS A # ISR4", changeset.getAddedInformationSystemB().getNonHierarchicalName());
  }

  @Test
  public void changesetTCR() {
    HistoryBBChangeset changeset = BBChangesetFactory.createChangeset(tcrA, tcrB, "", new DateTime());

    Assert.assertTrue(changeset.isDescriptionChanged());
    Assert.assertEquals("TCR A", changeset.getDescriptionFrom());
    Assert.assertEquals("TCR B", changeset.getDescriptionTo());
  }

  @Test
  public void correctHierarchyMessageKeys() {
    InformationSystemReleaseChangeset isrChangeset = (InformationSystemReleaseChangeset) BBChangesetFactory.createChangeset(isrA, isrB, "", new DateTime());
    Assert.assertEquals("informationSystemRelease.parent", isrChangeset.getParentElementLabelKey());

    BusinessFunctionChangeset bfChangeset = (BusinessFunctionChangeset) BBChangesetFactory.createChangeset(bfA, bfB, "", new DateTime());
    Assert.assertEquals("businessFunction.parent", bfChangeset.getParentElementLabelKey());
  }
}
