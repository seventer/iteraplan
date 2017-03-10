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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports.ArchitecturalDomainExcelReport;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;


/**
 * This class tests the creation of excel reports 
 * for {@link de.iteratec.iteraplan.model.ArchitecturalDomain}s.
 * 
 * @see ArchitecturalDomainSheetTest
 */
public class ArchitecturalDomainReportTest extends ExcelReportTestBase {

  /**
   * Tests the contents of a report for architectural domain in Excel 2003 format
   */
  @Test
  public void testADReport2003() {
    testReportCreationWithPermissions(TemplateType.EXCEL_2003);
  }

  /**
   * Tests the contents of a report for architectural domain in Excel 2007 format
   */
  @Test
  public void testADReport2007() {
    testReportCreationWithPermissions(TemplateType.EXCEL_2007);
  }

  /** Create the report for the given template type once with full permissions and once without permission
   * 
   */
  private void testReportCreationWithPermissions(TemplateType templateType) {

    List<TypeOfFunctionalPermission> tofps = new ArrayList<TypeOfFunctionalPermission>();

    tofps.add(TypeOfFunctionalPermission.ARCHITECTURALDOMAIN);
    tofps.add(TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES);
    UserContext.setCurrentUserContext(this.createUserContext(tofps));

    List<AttributeType> attrTypes = new ArrayList<AttributeType>();

    this.addExpectedCall(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, attrTypes);
    this.addExpectedCall(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, attrTypes);
    this.addExpectedCall();
    EasyMock.replay(this.getBbtDAOMock());
    EasyMock.replay(this.getAttrTypeDAOMock());

    ExportWorkbook context = new ExportWorkbook(Locale.GERMAN, templateType);
    Set<ArchitecturalDomain> allADs = new HashSet<ArchitecturalDomain>();

    ArchitecturalDomainExcelReport report = new ArchitecturalDomainExcelReport(context, this.getBbtDAOMock(), this.getAttrTypeDAOMock(), allADs,
        null, TEST_SERVER_URL);
    context = report.createReport();

    this.verifySheets(context, 3, DEFAULT_SHEET_KEY, AD_SHEET_KEY, TCR_SHEET_KEY);

    tofps.remove(TypeOfFunctionalPermission.ARCHITECTURALDOMAIN);
    UserContext.setCurrentUserContext(this.createUserContext(tofps));
    try {
      new ArchitecturalDomainExcelReport(context, this.getBbtDAOMock(), this.getAttrTypeDAOMock(), allADs, null, TEST_SERVER_URL);
      fail("Created an architectural report without having a permission");
    } catch (IteraplanBusinessException e) {
      // expected due to missing permission
    }
    EasyMock.verify(this.getAttrTypeDAOMock());

    EasyMock.reset(this.getAttrTypeDAOMock());
    EasyMock.reset(this.getBbtDAOMock());

  }
}
