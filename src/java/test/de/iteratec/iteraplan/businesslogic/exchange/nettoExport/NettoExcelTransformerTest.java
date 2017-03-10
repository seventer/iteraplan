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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ViewConfiguration;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.GuiTableState;
import de.iteratec.iteraplan.presentation.memory.ColumnDefinition;


/**
 * @author gph
 */
public class NettoExcelTransformerTest extends BaseTransactionalTestSupport {

  @Autowired
  private TestDataHelper2 testDataHelper;

  /**{@inheritDoc}**/
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.nettoExport.NettoExcelTransformer#transform(java.util.List, java.io.OutputStream, de.iteratec.iteraplan.model.TypeOfBuildingBlock)}.
   */
  @Test
  public void testTransform() {
    NettoTransformer inst2007op = NettoExcelTransformer.newInstance(createSimpleOverviewPageTableStructure(),
        NettoExcelTransformer.ExcelVersion.EXCEL_VERSION_2007);
    NettoTransformer inst2003sr = NettoExcelTransformer.newInstance(createSimpleSpreadsheetReportTableStructure(),
        NettoExcelTransformer.ExcelVersion.EXCEL_VERSION_2003);

    assertNotNull("Can't create netto transformer for overview page table structure with excel 2007", inst2007op);
    assertNotNull("Can't create netto transformer for spreadsheet report table structure with excel 2003", inst2003sr);

    List<BuildingBlock> sourceList = new ArrayList<BuildingBlock>();
    String firstInfstrElemName = "Infrastructure Element for UnitTest";
    String lastInfstrElemDesc = "Last comment";
    sourceList.add(testDataHelper.createInfrastructureElement(firstInfstrElemName, "Some comment"));
    sourceList.add(testDataHelper.createInfrastructureElement("Another Infrastructure Element", "Some more comment"));
    sourceList.add(testDataHelper.createInfrastructureElement("Yet another Infrastructure Element", "Even more comment"));
    sourceList.add(testDataHelper.createInfrastructureElement("Last Infrastructure Element", lastInfstrElemDesc));

    ByteArrayOutputStream bufferA = new ByteArrayOutputStream();
    ByteArrayOutputStream bufferB = new ByteArrayOutputStream();
    inst2007op.transform(sourceList, bufferA, TypeOfBuildingBlock.INFRASTRUCTUREELEMENT);
    inst2003sr.transform(sourceList, bufferB, TypeOfBuildingBlock.INFRASTRUCTUREELEMENT);

    InputStream in2007 = new ByteArrayInputStream(bufferA.toByteArray());
    InputStream in2003 = new ByteArrayInputStream(bufferB.toByteArray());

    // Excel version
    try {
      assertTrue("Generated excel file is not version 2007.", POIXMLDocument.hasOOXMLHeader(in2007));
      assertTrue("Generated excel file is not version 2003.", POIFSFileSystem.hasPOIFSHeader(in2003));
    } catch (IOException e1) {
      fail("Can't read excel header from buffers.");
    }

    Workbook workbook2007 = null;
    Workbook workbook2003 = null;
    try {
      workbook2007 = WorkbookFactory.create(in2007);
      workbook2003 = WorkbookFactory.create(in2003);
    } catch (Exception e) {
      fail("Can't open generated excel workbook.");
    }

    assertNotNull("Could not create excel workbook instance from generated output (excel 2007).", workbook2007);
    assertNotNull("Could not create excel workbook instance from generated output (excel 2003).", workbook2003);

    assertSame("Number of sheets is not equal 1  (excel 2007).", Integer.valueOf(workbook2007.getNumberOfSheets()), Integer.valueOf(1));
    assertSame("Number of sheets is not equal 1  (excel 2003).", Integer.valueOf(workbook2003.getNumberOfSheets()), Integer.valueOf(1));

    String stringCellValue2007 = null;
    String stringCellValue2003 = null;
    String stringCellValueDesc2007 = null;
    String stringCellValueDesc2003 = null;
    Sheet sheet2007;
    Sheet sheet2003;
    Row firstDataRow2007;
    Row firstDataRow2003;
    Row lastDataRow2007;
    Row lastDataRow2003;
    Cell nameCell2007;
    Cell nameCell2003;
    Cell descCell2007;
    Cell descCell2003;

    try {
      sheet2007 = workbook2007.getSheetAt(0);
      sheet2003 = workbook2003.getSheetAt(0);

      firstDataRow2007 = sheet2007.getRow(1);
      firstDataRow2003 = sheet2003.getRow(1);

      lastDataRow2007 = sheet2007.getRow(4);
      lastDataRow2003 = sheet2003.getRow(4);

      nameCell2007 = firstDataRow2007.getCell(0);
      nameCell2003 = firstDataRow2003.getCell(0);

      descCell2007 = lastDataRow2007.getCell(1);
      descCell2003 = lastDataRow2003.getCell(1);

      stringCellValue2007 = nameCell2007.getStringCellValue();
      stringCellValue2003 = nameCell2003.getStringCellValue();

      stringCellValueDesc2007 = descCell2007.getStringCellValue();
      stringCellValueDesc2003 = descCell2003.getStringCellValue();
    } catch (Exception e) {
      fail("Wrong structure inside workbook/sheet/row.");
    }

    assertEquals("String in generated excel 2007 workbook does not match the string from the first element in the List of BuildingBlocks.",
        stringCellValue2007, firstInfstrElemName);
    assertEquals("String in generated excel 2003 workbook does not match the string from the first element in the List of BuildingBlocks.",
        stringCellValue2003, firstInfstrElemName);

    assertEquals("String in generated excel 2007 workbook does not match the string from the last element in the List of BuildingBlocks.",
        stringCellValueDesc2007, lastInfstrElemDesc);
    assertEquals("String in generated excel 2003 workbook does not match the string from the last element in the List of BuildingBlocks.",
        stringCellValueDesc2003, lastInfstrElemDesc);
  }

  private TableStructure createSimpleOverviewPageTableStructure() {
    List<ColumnDefinition> cols = new ArrayList<ColumnDefinition>();
    cols.add(new ColumnDefinition("infrastructureElement.singular", "name", "", true));
    cols.add(new ColumnDefinition("global.description", "description", "", true));

    GuiTableState tableState = new GuiTableState();
    tableState.addColumnEntries(cols);
    return new OverviewPageTableStructure(tableState);
  }

  private TableStructure createSimpleSpreadsheetReportTableStructure() {
    List<ColumnEntry> cols = new ArrayList<ColumnEntry>();
    cols.add(new ColumnEntry("hierarchicalName", COLUMN_TYPE.INHERITED, "global.nameversion"));
    cols.add(new ColumnEntry("description", COLUMN_TYPE.DESCRIPTION, "global.description"));

    ViewConfiguration viewConfiguration = new ViewConfiguration(TypeOfBuildingBlock.BUSINESSPROCESS, Locale.US);
    viewConfiguration.setVisibleColumns(cols);
    return new SpreadsheetReportTableStructure(viewConfiguration);
  }
}
