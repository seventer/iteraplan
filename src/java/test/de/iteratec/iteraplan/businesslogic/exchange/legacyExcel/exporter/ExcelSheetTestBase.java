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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.After;
import org.junit.Before;

import de.iteratec.iteraplan.TestAsSuperUser;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.InformationSystemRelease;

/**
 * This class serves as a parent class for the individual sheet tests for different types of {@link de.iteratec.iteraplan.model.BuildingBlock}s.
 * Its main objective is to provide String constants and common helper methods that are used by several sheet tests. 
 * For combination of sheets into a report see {@link ExcelReportTestBase}.
 */
public abstract class ExcelSheetTestBase {
  private Date                lastModDate            = new Date();
  private int                 currRowIndex;

  static final String SYSTEM_USER            = "system";
  static final String TEST_USER              = "testUser";
  static final String TEST_DESCRIPTION       = "testDescription";
  static final String TEST_URL               = "testURL";
  static final String BP_PLURAL_MESSAGE_KEY  = "businessProcess.plural";
  static final String BU_PLURAL_MESSAGE_KEY  = "businessUnit.plural";
  static final String BD_PLURAL_MESSAGE_KEY  = "businessDomain.plural";
  static final String ISR_PLURAL_MESSAGE_KEY = "informationSystemRelease.plural";
  static final String DATE_FORMAT_PATTERN    = "dd-MMM-yyyy";
  static final String ISI_TRANSPORT          = "global.direction";
  
  @Before
  public void setUp() throws Exception {
    this.currRowIndex = 0;
    TestAsSuperUser.createSuperUserInContext();
  }

  @After
  public void tearDown() throws Exception {
    TestAsSuperUser.clearUserContext();
  }

  protected Set<InformationSystemRelease> createISRs(ExporterDataUtil exporterDataUtil, String namePrefix, int startId, int... versions) {
    return exporterDataUtil.createISRs(startId + 1, namePrefix + " - InformationSystemRelease", versions);
  }
  
  protected List<String> createBasicHierarchicalEntityContent(String url, AbstractHierarchicalEntity<?> entity) {
    List<String> content = new ArrayList<String>();
    content.add(String.format(url, entity.getId(), entity.getId()));
    content.add(entity.getName());
    content.add(entity.getHierarchicalName());
    content.add(entity.getDescription());
    return content;
  }

  /**
   * @param sheet
   * @return the next row which is not <code>null</code>
   */
  protected Row retrieveNextRow(Sheet sheet) {
    Row row = null;
    int index = this.currRowIndex;
    while (row == null) {
      row = sheet.getRow(index);
      index++;
    }
    this.currRowIndex = index;
    return row;
  }

  /**
   * Verifies a single line of content
   * 
   * @param row
   * @param expContent
   */
  protected void verifyRowContent(Row row, Object... expContent) {
    Iterator<Cell> iter = row.cellIterator();
    for (Object element : expContent) {
      if (iter.hasNext()) {
        String nextElement = iter.next().toString();
        assertEquals(element, nextElement);
      }
      else {
        fail("Received more entries than expected");
      }
    }
  } 
  
  protected String getExtBMHeaderName(String isr, String bu, String product, String bm) {
    return bm + "(" + product + " / " + bu + " / " + isr + ")";
  }
  
  protected Date getLastModDate() {
    return this.lastModDate;
  }
}
