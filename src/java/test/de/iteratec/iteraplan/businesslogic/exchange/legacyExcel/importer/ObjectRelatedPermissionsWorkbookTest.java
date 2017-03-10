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
/**
 * 
 */
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.output.NullOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog.Level;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Test for {@link ObjectRelatedPermissionsWorkbook} testing if the data specified in
 * Excel file {@value ObjectRelatedPermissionsWorkbookTest#EXCEL_FILE_NAME}.
 * 
 * @author agu 
 *
 */
public class ObjectRelatedPermissionsWorkbookTest {
  /** Excel file name, to be tested. */
  private static final String              EXCEL_FILE_NAME = "objectrelatedpermissions.xls";
  /** Excel file name, to be tested. */
  private static final String              EXCEL_FILE_NAME_WITH_NAME = "objectrelatedpermissionswithname.xls";
  /** Class to be tested. */
  private ObjectRelatedPermissionsWorkbook workbook;

  /**
   * Creates an instance of the {@link ObjectRelatedPermissionsWorkbook}.
   */
  @Before
  public void setUp() {
    PrintWriter stream = new PrintWriter(new NullOutputStream());
    ProcessingLog userLog = new ProcessingLog(Level.DEBUG, stream);
    workbook = new ObjectRelatedPermissionsWorkbook(userLog);
  }

  /**
   * Removes the processing log from the {@link ObjectRelatedPermissionsWorkbook}.
   */
  @After
  public void tearDown() {
    ImportWorkbook.removeProcessingLog();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ObjectRelatedPermissionsWorkbook#ObjectRelatedPermissionsWorkbook(de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog)}.
   */
  @Test
  public void testObjectRelatedPermissionsWorkbook() {
    assertNotNull(workbook);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ObjectRelatedPermissionsWorkbook#doImport(java.io.InputStream)}.
   */
  @Test
  public void testDoImport() {
    InputStream excelInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(EXCEL_FILE_NAME);
    List<ObjectRelatedPermissionsData> permissions = workbook.doImport(excelInputStream);
    assertNotNull(permissions);
    assertEquals(26, permissions.size());

    checkImportedBuildingBlocks("bd", 2, getOfType(TypeOfBuildingBlock.BUSINESSDOMAIN, permissions));
    checkImportedBuildingBlocks("bp", 2, getOfType(TypeOfBuildingBlock.BUSINESSPROCESS, permissions));
    checkImportedBuildingBlocks("bf", 2, getOfType(TypeOfBuildingBlock.BUSINESSFUNCTION, permissions));
    checkImportedBuildingBlocks("p", 2, getOfType(TypeOfBuildingBlock.PRODUCT, permissions));
    checkImportedBuildingBlocks("bu", 2, getOfType(TypeOfBuildingBlock.BUSINESSUNIT, permissions));
    checkImportedBuildingBlocks("bo", 2, getOfType(TypeOfBuildingBlock.BUSINESSOBJECT, permissions));
    checkImportedBuildingBlocks("isd", 2, getOfType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, permissions));
    checkImportedBuildingBlocks("isr", 2, getOfType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, permissions));
    checkImportedBuildingBlocks("isi", 2, getOfType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, permissions));
    checkImportedBuildingBlocks("ad", 2, getOfType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, permissions));
    checkImportedBuildingBlocks("tcr", 2, getOfType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, permissions));
    checkImportedBuildingBlocks("ie", 2, getOfType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, permissions));
    checkImportedBuildingBlocks("pr", 2, getOfType(TypeOfBuildingBlock.PROJECT, permissions));
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ObjectRelatedPermissionsWorkbook#doImport(java.io.InputStream)}.
   */
  @Test
  public void testDoImportWithName() {
    InputStream excelInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(EXCEL_FILE_NAME_WITH_NAME);
    List<ObjectRelatedPermissionsData> permissions = workbook.doImport(excelInputStream);
    assertNotNull(permissions);
    assertEquals(52, permissions.size());

    checkImportedBuildingBlocksWithName("bd", 4, getOfType(TypeOfBuildingBlock.BUSINESSDOMAIN, permissions));
    checkImportedBuildingBlocksWithName("bp", 4, getOfType(TypeOfBuildingBlock.BUSINESSPROCESS, permissions));
    checkImportedBuildingBlocksWithName("bf", 4, getOfType(TypeOfBuildingBlock.BUSINESSFUNCTION, permissions));
    checkImportedBuildingBlocksWithName("p", 4, getOfType(TypeOfBuildingBlock.PRODUCT, permissions));
    checkImportedBuildingBlocksWithName("bu", 4, getOfType(TypeOfBuildingBlock.BUSINESSUNIT, permissions));
    checkImportedBuildingBlocksWithName("bo", 4, getOfType(TypeOfBuildingBlock.BUSINESSOBJECT, permissions));
    checkImportedBuildingBlocksWithName("isd", 4, getOfType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, permissions));
    checkImportedBuildingBlocksWithName("isr", 4, getOfType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, permissions));
    checkImportedBuildingBlocksWithName("isi", 4, getOfType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, permissions));
    checkImportedBuildingBlocksWithName("ad", 4, getOfType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, permissions));
    checkImportedBuildingBlocksWithName("tcr", 4, getOfType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, permissions));
    checkImportedBuildingBlocksWithName("ie", 4, getOfType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, permissions));
    checkImportedBuildingBlocksWithName("pr", 4, getOfType(TypeOfBuildingBlock.PROJECT, permissions));
  }
  
  /**
   * Checks that every sheet contains 2 entries in the form:<br />
   * Id | User<br />
   * 1  | prefix_user1<br />
   * 2  | prefix_user2
   * 
   * <p>Where prefix is the building block abbrevation.
   * 
   * 
   * @param prefix
   * @param permissions
   */
  private void checkImportedBuildingBlocks(String prefix, int size, List<ObjectRelatedPermissionsData> permissions) {
    assertEquals(size, permissions.size());

    Set<String> ids = Sets.newHashSet("1", "2");

    for (ObjectRelatedPermissionsData permission : permissions) {
      assertThat(ids, JUnitMatchers.hasItem(permission.getId().getAttributeValue()));

      String users = createUsers(prefix, permission.getId().getAttributeValue());
      assertEquals(users, permission.getUsers().getAttributeValue());
    }
  }

  /**
   * Checks that every sheet contains 2 entries in the form:<br />
   * Id | User<br />
   * 1  | prefix_user1<br />
   * 2  | prefix_user2
   * 
   * <p>Where prefix is the building block abbrevation.
   * 
   * 
   * @param prefix
   * @param businessDomains
   */
  private void checkImportedBuildingBlocksWithName(String prefix, int size, List<ObjectRelatedPermissionsData> permissions) {
    assertEquals(size, permissions.size());

    for (int i = 0; i < permissions.size(); i++) {
      ObjectRelatedPermissionsData permission = permissions.get(i);
      String users = createUsers(prefix, String.valueOf(i+1));
      assertEquals(users, permission.getUsers().getAttributeValue());
    }
  }

  /**
   * Creates the users in the form of "{prefix}_user{id}1" and "{prefix}_user{id}2" 
   * 
   * @param prefix the building block abbreviation
   * @param id the building block id 
   * @return the created user for specified {@code prefix} and {@code id}
   */
  private String createUsers(String prefix, String id) {
    String user1 = MessageFormat.format("{0}_user{1}{2}", prefix, id, Integer.valueOf(1));
    String user2 = MessageFormat.format("{0}_user{1}{2}", prefix, id, Integer.valueOf(2));

    return user1 + "; " + user2;
  }

  /**
   * Returns all object permissions from the specified list of {@code permissions} having the specified
   * {@code type}.
   *  
   * @param type the building block type
   * @param permissions the list of all imported permissions
   * @return all object permissions having the specified {@code type}
   */
  private List<ObjectRelatedPermissionsData> getOfType(TypeOfBuildingBlock type, List<ObjectRelatedPermissionsData> permissions) {
    final List<ObjectRelatedPermissionsData> result = Lists.newArrayList();

    for (ObjectRelatedPermissionsData permission : permissions) {
      if (permission.getTypeOfBuildingBlock() == type) {
        result.add(permission);
      }
    }

    return result;
  }
}
