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
package de.iteratec.iteraplan.businesslogic.exchange.nettoExport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import au.com.bytecode.opencsv.CSVReader;
import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ViewConfiguration;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.presentation.dialog.GuiTableState;
import de.iteratec.iteraplan.presentation.memory.ColumnDefinition;


/**
 * Test for the NettoCSV Export
 */
public class NettoCSVTransformerTest extends BaseTransactionalTestSupport {

  @Autowired
  private TestDataHelper2            testDataHelper;

  private NumberAT                   numberAT;
  private List<BuildingBlock>        sourceList;
  private InformationSystemRelease[] isArray;

  /**{@inheritDoc}**/
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());

    prepareTestData();

  }

  @Test
  /**
   * Testcase creates 4 Informationsystemreleases with Text, Date and Numeric Attributes, which are transformed in CSV
   * The CSV output stream will be parsed again as CSV and the values are asserted
   */
  public void testCSVTransformerForSpreadsheetReport() {

    NettoTransformer csvTransformer = NettoCSVTransformer.newInstance(createSimpleSpreadsheetReportTableStructure());

    assertNotNull("Can't create netto transformer for overview page table structure for csv", csvTransformer);

    //Create output stream
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    csvTransformer.transform(sourceList, out, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

    //assert that output stream
    InputStream in = new ByteArrayInputStream(out.toByteArray());
    CSVReader reader;
    try {

      //BOMInputStream is necessary, because of leading BOM in Stream
      //Otherwise assertion would fail
      BOMInputStream bOMInputStream = new BOMInputStream(in);
      ByteOrderMark bom = bOMInputStream.getBOM();
      String charsetName = bom == null ? "UTF-8" : bom.getCharsetName();

      reader = new CSVReader(new InputStreamReader(new BufferedInputStream(bOMInputStream), charsetName), ';');

      List<String[]> allLines = reader.readAll();

      int index = 0;
      SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

      for (String[] line : allLines) {

        String name = line[0];
        String desc = line[1];
        String start = line[2];
        String end = line[3];
        String statusString = line[4];
        String numString = line[5];

        //get the status enum first
        //we onlny need planned and current in this test
        TypeOfStatus status = null;

        if ("Plan".equals(statusString)) {
          status = TypeOfStatus.PLANNED;
        }
        else if ("Ist".equals(statusString)) {
          status = TypeOfStatus.CURRENT;
        }

        if (index == 0) {
          //assert the headers of CSV file
          assertEquals("Name und Version", name.trim());
          assertEquals("Beschreibung", desc);
          assertEquals("von", start);
          assertEquals("bis", end);
          assertEquals("Status", statusString);
          assertEquals("Complexity", numString);
        }
        else {

          //format the number
          NumberFormat nf = new DecimalFormat("0.00");
          String number = nf.format(Double.valueOf(numString));

          assertEquals(isArray[index - 1].getName(), name);
          assertEquals(isArray[index - 1].getDescription(), desc);
          assertEquals(df.format(isArray[index - 1].getRuntimePeriod().getStart()), start);
          assertEquals(df.format(isArray[index - 1].getRuntimePeriod().getEnd()), end);
          assertEquals(isArray[index - 1].getTypeOfStatus(), status);
          assertEquals(isArray[index - 1].getAttributeValue(numberAT.getName(), Locale.GERMAN), number);
        }

        index++;

      }

      reader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testCSVTransformerForOverviewPage() {

    NettoTransformer csvTransformer = NettoCSVTransformer.newInstance(createSimpleOverviewPageTableStructure());

    assertNotNull("Can't create netto transformer for overview page table structure for csv", csvTransformer);

    //Create output stream
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    csvTransformer.transform(sourceList, out, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

    //assert that output stream
    InputStream in = new ByteArrayInputStream(out.toByteArray());
    CSVReader reader;
    try {

      //BOMInputStream is necessary, because of leading BOM in Stream
      //Otherwise assertion would fail
      BOMInputStream bOMInputStream = new BOMInputStream(in);
      ByteOrderMark bom = bOMInputStream.getBOM();
      String charsetName = bom == null ? "UTF-8" : bom.getCharsetName();

      reader = new CSVReader(new InputStreamReader(new BufferedInputStream(bOMInputStream), charsetName), ';');

      List<String[]> allLines = reader.readAll();

      int index = 0;
      SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

      for (String[] line : allLines) {

        String name = line[0];
        String desc = line[1];
        String start = line[2];
        String end = line[3];
        String status = line[4];

        if (index == 0) {
          //assert the headers of CSV file
          assertEquals("Name und Version", name.trim());
          assertEquals("Beschreibung", desc);
          assertEquals("von", start);
          assertEquals("bis", end);
          assertEquals("Status", status);
        }
        else {
          assertEquals(isArray[index - 1].getName(), name);
          assertEquals(isArray[index - 1].getDescription(), desc);
          assertEquals(df.format(isArray[index - 1].getRuntimePeriod().getStart()), start);
          assertEquals(df.format(isArray[index - 1].getRuntimePeriod().getEnd()), end);
          assertEquals(isArray[index - 1].getTypeOfStatus().toString(), status);
        }

        index++;

      }

      reader.close();

    } catch (IOException e) {
      e.printStackTrace();
      fail("Fail due to IO Exception");
    }

  }

  /**
   * Creates the Table Structure for SpreadSheet test in this class
   * @return the SpreadsheetReportTableStructure
   */
  private TableStructure createSimpleSpreadsheetReportTableStructure() {
    List<ColumnEntry> cols = new ArrayList<ColumnEntry>();
    cols.add(new ColumnEntry("hierarchicalName", COLUMN_TYPE.INHERITED, "global.nameversion"));
    cols.add(new ColumnEntry("description", COLUMN_TYPE.DESCRIPTION, "global.description"));
    cols.add(new ColumnEntry("runtimePeriod.start", COLUMN_TYPE.DATE, "global.from"));
    cols.add(new ColumnEntry("runtimePeriod.end", COLUMN_TYPE.DATE, "global.to"));
    cols.add(new ColumnEntry("typeOfStatus", COLUMN_TYPE.TYPE_OF_STATUS, "global.type_of_status"));
    cols.add(new ColumnEntry(numberAT.getId().toString(), COLUMN_TYPE.ATTRIBUTE, numberAT.getName()));

    ViewConfiguration viewConfiguration = new ViewConfiguration(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, Locale.GERMAN);
    viewConfiguration.setVisibleColumns(cols);

    return new SpreadsheetReportTableStructure(viewConfiguration);
  }

  /**
   * Creates the Table Structure for OverviewPage test in this class
   * @return the OverviewPageTableStructure
   */
  private TableStructure createSimpleOverviewPageTableStructure() {
    List<ColumnDefinition> cols = new ArrayList<ColumnDefinition>();
    cols.add(new ColumnDefinition("global.nameversion", "hierarchicalName", "", true));
    cols.add(new ColumnDefinition("global.description", "description", "", true));
    cols.add(new ColumnDefinition("global.from", "runtimePeriod.start", "", true));
    cols.add(new ColumnDefinition("global.to", "runtimePeriod.end", "", true));
    cols.add(new ColumnDefinition("global.type_of_status", "typeOfStatus", "", true));

    GuiTableState tableState = new GuiTableState();
    tableState.addColumnEntries(cols);
    return new OverviewPageTableStructure(tableState);
  }

  private void prepareTestData() {

    /*
     * Create a Numeric Attribute
     */
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("test", "test description");
    numberAT = testDataHelper.createNumberAttributeType("Complexity", "description", atg);
    testDataHelper.getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE).addAttributeTypeTwoWay(numberAT);

    NumberAV numberAV1;
    NumberAV numberAV2;

    numberAV1 = testDataHelper.createNumberAV(BigDecimal.ONE, numberAT);
    numberAV2 = testDataHelper.createNumberAV(BigDecimal.TEN, numberAT);

    sourceList = new ArrayList<BuildingBlock>();

    InformationSystem is = testDataHelper.createInformationSystem("Unit-Test Information-System");
    isArray = new InformationSystemRelease[4];

    isArray[0] = testDataHelper.createInformationSystemRelease(is, "1.0", "Description", "10.10.2015", "10.10.2020", TypeOfStatus.PLANNED);
    isArray[1] = testDataHelper.createInformationSystemRelease(is, "2.0", "Description", "11.09.2016", "10.10.2020", TypeOfStatus.CURRENT);
    isArray[2] = testDataHelper.createInformationSystemRelease(is, "3.0", "Description", "12.08.2017", "10.10.2020", TypeOfStatus.PLANNED);
    isArray[3] = testDataHelper.createInformationSystemRelease(is, "4.0", "Description", "13.07.2018", "10.10.2020", TypeOfStatus.CURRENT);

    testDataHelper.createAVA(isArray[0], numberAV1);
    testDataHelper.createAVA(isArray[1], numberAV2);
    testDataHelper.createAVA(isArray[2], numberAV1);
    testDataHelper.createAVA(isArray[3], numberAV2);

    sourceList.add(isArray[0]);
    sourceList.add(isArray[1]);
    sourceList.add(isArray[2]);
    sourceList.add(isArray[3]);

  }

}
