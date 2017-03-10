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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.ExcelTemplateGeneratorService;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.MetamodelLoader;


/**
 * Tests the generation of the Excel templates.
 */
public class ExcelTemplateGeneratorServiceImplTest extends BaseTransactionalTestSupport {

  @Autowired
  private MetamodelLoader  metamodelLoader;
  @Autowired
  private TestDataHelper2 testDataHelper;
  @Autowired
  private ExcelTemplateGeneratorService excelTemplateGeneratorService;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.ExcelTemplateGeneratorServiceImpl#generateTemplateExcel2003(de.iteratec.iteraplan.elasticeam.metamodel.Metamodel, de.iteratec.iteraplan.elasticeam.metamodel.MetamodelContext)}.
   * @throws IOException if an exception occurs
   */
  @Test
  public void testGenerateTemplateExcel2003() throws IOException {
    Metamodel metamodelExpression = metamodelLoader.loadConceptualMetamodel();

    WorkbookContext workbookContext = excelTemplateGeneratorService.generateTemplateExcel2003(metamodelExpression);

    File tempFile = File.createTempFile("iteraplanExcelTemplate", "xls");
    ExcelExportTestUtils.persistWorkbook(workbookContext.getWb(), tempFile);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.ExcelTemplateGeneratorServiceImpl#generateTemplateExcel2007(de.iteratec.iteraplan.elasticeam.metamodel.Metamodel, de.iteratec.iteraplan.elasticeam.metamodel.MetamodelContext)}.
   * @throws IOException if an exception occurs
   */
  @Test
  public void testGenerateTemplateExcel2007() throws IOException {
    Metamodel metamodelExpression = metamodelLoader.loadConceptualMetamodel();

    WorkbookContext workbookContext = excelTemplateGeneratorService.generateTemplateExcel2007(metamodelExpression);

    File tempFile = File.createTempFile("iteraplanExcelTemplate", "xlsx");
    ExcelExportTestUtils.persistWorkbook(workbookContext.getWb(), tempFile);
  }

}
