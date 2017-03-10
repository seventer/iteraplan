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

import java.io.InputStream;
import java.util.Date;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.AttributeTypeReader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.impl.RMetamodelAttributeTypeReader;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.exception.ElasticMiIoException;
import de.iteratec.iteraplan.elasticmi.io.ExportInfo;
import de.iteratec.iteraplan.elasticmi.io.mapper.bundle.Bundle;
import de.iteratec.iteraplan.elasticmi.io.mapper.emf.EmfModelMapper;
import de.iteratec.iteraplan.elasticmi.io.mapper.emf.bundle.EmfBundle;
import de.iteratec.iteraplan.elasticmi.io.mapper.emf.bundle.EmfBundleMapper;
import de.iteratec.iteraplan.elasticmi.io.mapper.metamodel.emf.EmfMetamodelMapper;
import de.iteratec.iteraplan.elasticmi.messages.LocalizedMiMessage;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.validate.model.ModelValidator;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.ImportStrategy;


/**
 *
 */
public class MiEmfImportProcess extends MiImportProcess {

  private final EmfBundle     bundle;
  private RMetamodel          loadedMetamodel;
  private Model               loadedModel;
  private AttributeTypeReader aReader;

  public MiEmfImportProcess(BuildingBlockServiceLocator bbServiceLocator, BuildingBlockTypeService bbTypeService,
      AttributeTypeGroupService atgService, AttributeTypeService atService, AttributeValueService avService, ImportStrategy importStrategy,
      IteraplanMiLoadTaskFactory loadTaskFactory, InputStream in) {
    super(bbServiceLocator, avService, atgService, atService, bbTypeService, importStrategy, loadTaskFactory);
    try {
      this.bundle = EmfBundle.fromZip(in);
    } catch (ElasticMiIoException e) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.IMPORT_FILE_CORRUPT, e);
    }
  }

  /**{@inheritDoc}**/
  @Override
  protected Model getModelToImport() {
    if (loadedModel == null) {
      loadModelFromResource();
    }
    return loadedModel;
  }

  @Override
  protected void loadModelFromResource() {
    this.loadedModel = (new EmfModelMapper(getImportMetamodel())).read(bundle.getModelRepresentation(), ElasticMiContext.getCurrentContext()
        .getContextModel().getIdProvider());
  }

  /**{@inheritDoc}**/
  @Override
  protected ExportInfo getExportInfo() {
    ExportInfo eInfo = new ExportInfo();
    if (getExportTimestamp() != null && !getExportTimestamp().trim().isEmpty()) {
      Date timestamp = (Date) AtomicDataType.DATE_TIME.type().fromObject(getExportTimestamp());
      eInfo.setVisibleExportTime(timestamp);
      eInfo.setHiddenExportTime(timestamp);

    }
    return eInfo;
  }

  /**{@inheritDoc}**/
  @Override
  protected AttributeTypeReader getAttributeTypeReader() {
    if (aReader == null) {
      aReader = new RMetamodelAttributeTypeReader(loadedMetamodel, getImportProcessMessages());
    }
    return aReader;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean importAndCheckFile() {

    getCurrentCheckList().pending(CheckPoint.FILE_CHECK);
    validate();

    getImportMetamodel(); // to make sure MaskingMM is setUp correctly;

    EmfBundleMapper bMapper = new EmfBundleMapper(IteraplanProperties.getProperties().getBuildVersion());
    Bundle<RMetamodel, Model> importedBundle = bMapper.read(bundle);
    loadedMetamodel = importedBundle.getMetamodelRepresentation();
    loadedModel = importedBundle.getModelRepresentation();

    ModelValidator preMergeValidator = IteraplanModelValidators.getPreMergeModelValidator(getImportMetamodel());
    boolean modelIsValid = preMergeValidator.validate(loadedModel, getImportProcessMessages());

    finalizeMessages();
    if (modelIsValid) {
      getImportProcessMessages().onMessage(new LocalizedMiMessage(Severity.INFO, "de.iteratec.iteraplan.elasticmi.excelimport.fileCorrect"));
      getCurrentCheckList().done(CheckPoint.FILE_CHECK);
    }
    return modelIsValid;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean isPartial() {
    return false;
  }

  /**{@inheritDoc}**/
  @Override
  public String getFilteredTypeName() {
    return null;
  }

  /**{@inheritDoc}**/
  @Override
  public String getFilteredTypePersistentName() {
    return null;
  }

  /**{@inheritDoc}**/
  @Override
  public String getExtendedFilter() {
    return null;
  }

  /**{@inheritDoc}**/
  @Override
  public String getExportTimestamp() {
    return EmfMetamodelMapper.getTimestamp(bundle.getMetamodelRepresentation());
  }

}
