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
package de.iteratec.iteraplan.businesslogic.exchange.common.masterplan;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.EnumAT;


public class MasterplanCustomColumnHelperTest extends BaseTransactionalTestSupport {
  private static final String  ATTRIBUTE_NAME = "EnumAT";
  private final Locale         locale         = Locale.ENGLISH;
  private String[]             lastModificationString;
  private List<BuildingBlock>  buildingBlocks;

  @Autowired
  private AttributeTypeService attributeTypeService;
  @Autowired
  private TestDataHelper2      testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());

    createBuildingBlocks();
    createAttributeAndAttributeValueAssignments();
  }

  @Test
  public final void testProjectAttributeColumn() {
    ColumnEntry column = new ColumnEntry(attributeTypeService.getAttributeTypeByName(ATTRIBUTE_NAME).getId().toString(), COLUMN_TYPE.ATTRIBUTE,
        ATTRIBUTE_NAME);
    List<String> actualValues = projectColumn(column);

    String[] expected = new String[12];
    expected[0] = "";
    expected[1] = "";
    expected[2] = "";
    expected[3] = "av1";
    expected[4] = "";
    expected[5] = "";
    expected[6] = "";
    expected[7] = "av2";
    expected[8] = "";
    expected[9] = "";
    expected[10] = "";
    expected[11] = "av3";

    assertValuesAre(actualValues, expected);
  }

  @Test
  public final void testProjectNameColumn() {
    ColumnEntry column = new ColumnEntry("parent", COLUMN_TYPE.NAME, "global.parent");
    List<String> actualValues = projectColumn(column);

    String[] expected = new String[12];
    expected[0] = "";
    expected[1] = "IS0 # ver0";
    expected[2] = "";
    expected[3] = "";
    expected[4] = "";
    expected[5] = "IS4 # ver4";
    expected[6] = "";
    expected[7] = "";
    expected[8] = "";
    expected[9] = "IS8 # ver8";
    expected[10] = "";
    expected[11] = "";

    assertValuesAre(actualValues, expected);
  }

  @Test
  public final void testProjectListColumn() {
    ColumnEntry column = new ColumnEntry("predecessors", COLUMN_TYPE.LIST, "global.predecessor");
    List<String> actualValues = projectColumn(column);

    String[] expected = new String[12];
    expected[0] = "";
    expected[1] = "";
    expected[2] = "IS0 # ver0";
    expected[3] = "";
    expected[4] = "";
    expected[5] = "";
    expected[6] = "IS4 # ver4";
    expected[7] = "";
    expected[8] = "";
    expected[9] = "";
    expected[10] = "IS8 # ver8";
    expected[11] = "";

    assertValuesAre(actualValues, expected);
  }

  @Test
  public final void testProjectDateColumn() {
    ColumnEntry column = new ColumnEntry("lastModificationTime", COLUMN_TYPE.DATE, "global.lastModificationTime");
    List<String> actualValues = projectColumn(column);

    assertValuesAre(actualValues, lastModificationString);
  }

  private List<String> projectColumn(ColumnEntry entry) {
    List<ColumnEntry> customCols = new ArrayList<ColumnEntry>();
    customCols.add(entry);
    MasterplanCustomColumnHelper columnHelper = new MasterplanCustomColumnHelper(Sets.newHashSet(buildingBlocks), customCols, attributeTypeService,
        locale);
    Map<ColumnEntry, Map<BuildingBlock, String>> result = columnHelper.projectColums();
    return getResultValues(result, entry);
  }

  private void assertValuesAre(List<String> actual, String[] expected) {
    for (int i = 0; i < 12; i++) {
      assertEquals(expected[i], actual.get(i));
    }
  }

  private List<String> getResultValues(Map<ColumnEntry, Map<BuildingBlock, String>> result, ColumnEntry key) {
    List<String> vals = new ArrayList<String>();
    Map<BuildingBlock, String> tmp = result.get(key);
    for (int i = 0; i < 12; i++) {
      vals.add(tmp.get(buildingBlocks.get(i)));
    }
    return vals;
  }

  private void createAttributeAndAttributeValueAssignments() {
    //Create ATG and a 
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("masterplanColumnTestATG", "descr");
    EnumAT attribute = testDataHelper.createEnumAttributeType(ATTRIBUTE_NAME, "description", Boolean.TRUE, atg);
    attribute.setBuildingBlockTypes(CollectionUtils.hashSet(buildingBlocks.get(0).getBuildingBlockType()));

    //Create attribute values
    AttributeValue val1 = testDataHelper.createEnumAV("av1", "desc1", attribute);
    AttributeValue val2 = testDataHelper.createEnumAV("av2", "desc2", attribute);
    AttributeValue val3 = testDataHelper.createEnumAV("av3", "desc3", attribute);

    //Create assignments
    testDataHelper.createAVA(buildingBlocks.get(3), val1);
    testDataHelper.createAVA(buildingBlocks.get(7), val2);
    testDataHelper.createAVA(buildingBlocks.get(11), val3);
  }

  private void createBuildingBlocks() {
    DateFormat format = DateFormat.getDateInstance(3, locale);

    buildingBlocks = new ArrayList<BuildingBlock>();
    lastModificationString = new String[12];
    for (int i = 0; i < 12; i++) {

      InformationSystem is = testDataHelper.createInformationSystem("IS" + i);
      InformationSystemRelease rel = testDataHelper.createInformationSystemRelease(is, "ver" + i);
      //store the last modification date for each release
      lastModificationString[i] = format.format(rel.getLastModificationTime());

      if (i % 4 == 1) {
        rel.setParent((InformationSystemRelease) buildingBlocks.get(i - 1));
      }
      else if (i % 4 == 2) {
        Set<InformationSystemRelease> predecessors = new HashSet<InformationSystemRelease>();
        predecessors.add((InformationSystemRelease) buildingBlocks.get(i - 2));
        rel.setPredecessors(predecessors);
      }
      //create the attribute values afterwards
      buildingBlocks.add(rel);
    }

  }
}
