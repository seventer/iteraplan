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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import de.iteratec.iteraplan.businesslogic.exchange.common.SimpleMessage;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.AttributeTypeReader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.impl.ExcelAttributeTypeReader;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.io.ExportInfo;
import de.iteratec.iteraplan.elasticmi.io.exception.ElasticMiMalformedExternalRepresentationException;
import de.iteratec.iteraplan.elasticmi.io.mapper.xls.XlsModelMapper;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.messages.MiMessageAccess;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.validate.model.ModelValidator;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.ImportStrategy;


public final class MiExcelImportProcess extends MiImportProcess {

  private static final Logger LOGGER        = Logger.getIteraplanLogger(MiExcelImportProcess.class);

  private final Workbook      workbook;

  private Model               modelToImport = null;
  private ExportInfo          exportInfo    = null;

  public MiExcelImportProcess(BuildingBlockServiceLocator bbServiceLocator, BuildingBlockTypeService bbTypeService,
      AttributeTypeGroupService atgService, AttributeTypeService atService, AttributeValueService avService, ImportStrategy importStrategy,
      IteraplanMiLoadTaskFactory loadTaskFactory, InputStream in) {
    super(bbServiceLocator, avService, atgService, atService, bbTypeService, importStrategy, loadTaskFactory);
    this.workbook = readWorkbook(in);
  }

  /**{@inheritDoc}**/
  @Override
  public boolean importAndCheckFile() {
    getCurrentCheckList().pending(CheckPoint.FILE_CHECK);
    validate();

    loadModelFromResource();

    ModelValidator preMergeValidator = IteraplanModelValidators.getPreMergeModelValidator(getImportMetamodel());
    boolean modelIsValid = preMergeValidator.validate(modelToImport, getImportProcessMessages());

    finalizeMessages();
    modelIsValid &= !getImportProcessMessages().hasErrors();
    if (modelIsValid) {
      getImportProcessMessages().onMessage(
          new SimpleMessage(Severity.INFO, MiMessageAccess.getString("de.iteratec.iteraplan.elasticmi.excelimport.fileCorrect")));
      getCurrentCheckList().done(CheckPoint.FILE_CHECK);
    }
    return modelIsValid;
  }

  @Override
  protected ExportInfo getExportInfo() {
    if (this.exportInfo == null) {
      this.exportInfo = XlsModelMapper.getWorkbookInfo(workbook);
    }
    return this.exportInfo;
  }

  protected void loadModelFromResource() {
    String buildVersion = IteraplanProperties.getProperties().getBuildVersion();
    try {
      XlsModelMapper modelMapper = new XlsModelMapper(getImportMetamodel(), getMaskingMetamodel(), getImportProcessMessages(), null, buildVersion);
      this.modelToImport = modelMapper.read(workbook, getCanonicModel().getIdProvider());
    } catch (ElasticMiMalformedExternalRepresentationException e) {
      throw new IteraplanTechnicalException(e);
    }
  }

  private Workbook readWorkbook(InputStream in) {
    Workbook result = null;
    try {
      result = WorkbookFactory.create(in);
    } catch (InvalidFormatException e) {
      String msg = MessageFormat.format("Error reading excel workbook: InvalidFormatException {0}", e.getMessage());
      LOGGER.error(msg);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, msg);
    } catch (IOException e) {
      String msg = MessageFormat.format("Error reading excel workbook: IOException {0}", e.getMessage());
      LOGGER.error(msg);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, msg);
    } finally {
      IOUtils.closeQuietly(in);
    }
    return result;
  }

  @Override
  protected Model getModelToImport() {
    if (this.modelToImport == null) {
      loadModelFromResource();
    }
    return this.modelToImport;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean isPartial() {
    return getExportInfo().isPartial();
  }

  /**{@inheritDoc}**/
  @Override
  public String getFilteredTypeName() {
    IteraQlQuery query = getQuery();
    if (query != null) {
      return query.getLeft().getCanonicBase().getName();
    }
    return null;
  }

  /**{@inheritDoc}**/
  @Override
  public String getFilteredTypePersistentName() {
    IteraQlQuery query = getQuery();
    if (query != null) {
      return query.getLeft().getPersistentName();
    }
    return null;
  }

  /**{@inheritDoc}**/
  @Override
  public String getExtendedFilter() {
    String mainType = getExportInfo().getHiddenMainType();
    int beginIndex = mainType.indexOf('[');
    int endIndex = mainType.indexOf(']');
    if (beginIndex > -1 && endIndex > -1) {
      return getExportInfo().getHiddenMainType().substring(beginIndex + 1, endIndex);
    }
    return null;
  }

  /**{@inheritDoc}**/
  @Override
  public String getExportTimestamp() {
    return DateUtils.formatAsStringToLong(getExportInfo().getHiddenExportTime(), ElasticMiContext.getCurrentContext().getCurrentLocale());
  }

  private IteraQlQuery getQuery() {
    ExportInfo info = getExportInfo();
    RMetamodel rMetamodel = ElasticMiContext.getCurrentContext().getContextMetamodel();
    if (info.isPartial()) {
      return IteraQl2Compiler.compile(rMetamodel, info.getHiddenMainType() + ";");
    }
    return null;
  }

  /**{@inheritDoc}**/
  @Override
  protected AttributeTypeReader getAttributeTypeReader() {
    return new ExcelAttributeTypeReader(workbook, getImportProcessMessages());
  }
}
