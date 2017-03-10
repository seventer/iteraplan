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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.excelimport;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;

import de.iteratec.iteraplan.businesslogic.exchange.common.EamImportProcess;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages.ErrorLevel;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMetamodelLoader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.ModelLoader;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelValidatorResult;


public class ExcelImportProcess extends EamImportProcess {

  private final ExcelImporter excelImporter;

  public ExcelImportProcess(InputStream in, IteraplanMetamodelLoader metamodelLoader, ModelLoader modelLoader,
      BuildingBlockServiceLocator bbServiceLocator, AttributeValueService avService) {
    super(metamodelLoader, modelLoader, bbServiceLocator, avService);

    Workbook workbook = ExcelUtils.openExcelFile(in);
    excelImporter = new ExcelImporter(workbook);
  }

  public boolean importAndCheckFile() {
    getCurrentCheckList().pending(CheckPoint.FILE_CHECK);
    ResultMessages result = new ResultMessages();
    if (!excelImporter.importExcel()) {
      result.addMessages(ErrorLevel.ERROR, excelImporter.getErrorMessages());
    }

    ModelValidatorResult validatorResult = validateModel(getMetamodelToImport(), getModelToImport(), true, false, true, false);
    if (!validatorResult.getViolations().isEmpty()) {
      result.addMessages(ErrorLevel.ERROR, getMessagesFromValidatorResult(validatorResult));
    }

    if (!result.getErrors().isEmpty()) {
      getImportProcessMessages().setWrappedResultMessages(result);
      return false;
    }

    result.addMessage(ErrorLevel.INFO, "Excel file correct and internally consistent.");
    getCurrentCheckList().done(CheckPoint.FILE_CHECK);
    getImportProcessMessages().setWrappedResultMessages(result);
    return true;
  }

  /**{@inheritDoc}**/
  @Override
  protected Metamodel getMetamodelToImport() {
    return excelImporter.getMetamodel();
  }

  /**{@inheritDoc}**/
  @Override
  protected Model getModelToImport() {
    return excelImporter.getModel();
  }

  /**{@inheritDoc}**/
  @Override
  public boolean isPartial() {
    return false;
  }
  
  /**{@inheritDoc}**/
  @Override
  public String getFilteredTypeName(){
    return null;
  }
  
  /**{@inheritDoc}**/
  @Override
  public String getFilteredTypePersistentName(){
    return null;
  }

  /**{@inheritDoc}**/
  @Override
  public String getExtendedFilter(){
    return null;
  }
  
  /**{@inheritDoc}**/
  @Override
  public String getExportTimestamp(){
    return null;
  }
}
