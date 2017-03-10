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
package de.iteratec.iteraplan.businesslogic.exchange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateInfo;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateLocatorService;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateLocatorServiceImpl;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateLocatorServiceImpl.Caching;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Test class for the {@link TemplateLocatorServiceImpl} class.
 * 
 * @author agu
 *
 */
public class TemplateLocatorServiceImplTest {

  private static final String TEMPLATES_DIR       = "templates";
  private static final String EXCEL_WORKBOOK_XLS  = "ExcelWorkbook.xls";
  private static final String EXCEL_WORKBOOK2_XLS = "ExcelWorkbook2.xls";

  @Test(expected = IteraplanTechnicalException.class)
  public void testCreateServiceWithNonExistingDir() {
    TemplateLocatorService service = new TemplateLocatorServiceImpl("nonExistingDir");
    service.getTemplateInfos(TemplateType.EXCEL_2003);
  }

  @Test(expected = IteraplanTechnicalException.class)
  public void testCreateServiceWithFileAsClasspathDir() {
    // Since the current implementation internally adds a sub-directory according to the TemplateType
    // to the given base path, this here results in the same as the "non existing dir" case.
    TemplateLocatorService service = new TemplateLocatorServiceImpl("templates/legacyExcel/textFile.txt");
    service.getTemplateInfos(TemplateType.EXCEL_2003);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.TemplateLocatorServiceImpl#getTemplateInfos()}.
   */
  @Test
  public void testGetTemplateNamesExcel() {
    TemplateLocatorService templateLocatorService = new TemplateLocatorServiceImpl(TEMPLATES_DIR);
    Set<TemplateInfo> templateInfos = templateLocatorService.getTemplateInfos(TemplateType.EXCEL_2003);

    assertNotNull(templateInfos);

    Set<TemplateInfo> expected = createExpectedTemplateInfos(Lists.newArrayList(EXCEL_WORKBOOK_XLS, EXCEL_WORKBOOK2_XLS));
    assertEquals(expected, templateInfos);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.TemplateLocatorServiceImpl#getTemplateInfos()}.
   */
  @Test
  public void testGetTemplateNamesInfoflow() {
    TemplateLocatorService templateLocatorService = new TemplateLocatorServiceImpl(TEMPLATES_DIR);
    Set<TemplateInfo> templateInfos = templateLocatorService.getTemplateInfos(TemplateType.INFOFLOW);

    assertNotNull(templateInfos);

    Set<TemplateInfo> expected = createExpectedTemplateInfos(Sets.newHashSet("VisioInformationFlowTemplate.vdx"));
    assertEquals(expected, templateInfos);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.TemplateLocatorServiceImpl#getFile(java.lang.String)}.
   */
  @Test
  public void testGetFile() {
    TemplateLocatorService templateLocatorService = new TemplateLocatorServiceImpl(TEMPLATES_DIR);

    File templateFile = templateLocatorService.getFile(TemplateType.EXCEL_2003, EXCEL_WORKBOOK_XLS);
    assertNotNull(templateFile);
    assertEquals(EXCEL_WORKBOOK_XLS, templateFile.getName());

    File templateFile2 = templateLocatorService.getFile(TemplateType.EXCEL_2003, EXCEL_WORKBOOK2_XLS);
    assertNotNull(templateFile2);
    assertEquals(EXCEL_WORKBOOK2_XLS, templateFile2.getName());

    File templateFileNonExisting = templateLocatorService.getFile(TemplateType.EXCEL_2003, "nonExisting");
    Assert.assertNull(templateFileNonExisting);
  }

  @Test
  public void testCaching() throws IOException {
    TemplateLocatorService templateLocatorService = new TemplateLocatorServiceImpl(TEMPLATES_DIR, Caching.ENABLED);

    File templateFile = templateLocatorService.getFile(TemplateType.EXCEL_2003, EXCEL_WORKBOOK_XLS);
    File dir = templateFile.getParentFile();
    File tempFile = new File(dir, "tempfile.xls");
    tempFile.createNewFile();

    try {
      assertNotNull(templateFile);
      assertEquals(EXCEL_WORKBOOK_XLS, templateFile.getName());

      File templateFile2 = templateLocatorService.getFile(TemplateType.EXCEL_2003, EXCEL_WORKBOOK2_XLS);
      assertNotNull(templateFile2);
      assertEquals(EXCEL_WORKBOOK2_XLS, templateFile2.getName());

      File templateFileNonExisting = templateLocatorService.getFile(TemplateType.EXCEL_2003, "tempfile.xls");
      Assert.assertNull(templateFileNonExisting);
    } finally {
      FileUtils.deleteQuietly(tempFile);
    }
  }

  private Set<TemplateInfo> createExpectedTemplateInfos(Collection<String> expectedNames) {
    Set<TemplateInfo> expected = Sets.newTreeSet();
    for (String name : expectedNames) {
      TemplateInfo info = new TemplateInfo(name);
      if (EXCEL_WORKBOOK_XLS.equals(name)) {
        info.setDeletable(false);
      }
      expected.add(info);
    }
    return expected;
  }

}
