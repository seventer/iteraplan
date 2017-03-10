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

import java.util.Date;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess;
import de.iteratec.iteraplan.businesslogic.exchange.common.SimpleMessage;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.AttributeTypeImporter;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.AttributeTypeReader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model.MiIteraplanDiffWriter;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.diff.model.CreateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.DeleteDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.MergeStrategy;
import de.iteratec.iteraplan.elasticmi.diff.model.ModelDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.UpdateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.AdditiveMergeStrategy;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.CUDMergeStrategy;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ElasticMiModelMergeable;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ModelDiffImpl;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ModelMatcherImpl;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ModelMergeable;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.PartialModelDiffTransformer;
import de.iteratec.iteraplan.elasticmi.io.ExportInfo;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.messages.MiMessageAccess;
import de.iteratec.iteraplan.elasticmi.messages.merge.CreateDiffMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.DeleteDiffMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.UpdateDiffMessage;
import de.iteratec.iteraplan.elasticmi.metamodel.common.RWAtomicDataTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.partial.BasePartialExportMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.partial.a.APartialExportMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.mask.MaskingMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.mask.MetamodelWithPermissionsMaskUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelWithPermissionsCopier;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ModelUtil;
import de.iteratec.iteraplan.elasticmi.validate.model.ModelValidator;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.ImportStrategy;


public abstract class MiImportProcess extends ImportProcess {

  private static final String             MSG_PARTIAL_MISSING_MAIN_TYPE = "de.iteratec.iteraplan.elasticmi.import.partial.missing.main.type";

  private final ImportStrategy            importStrategy;
  private final MiImportProcessMessages   importProcessMessages         = new MiImportProcessMessages();
  private final ImportDatabaseDataAccess  databaseAccess;

  private final AttributeTypeGroupService attributeTypeGroupService;
  private final AttributeTypeService      attributeTypeService;
  private final BuildingBlockTypeService  buildingBlockTypeService;

  private final Model                     preImportModel;

  private MaskingMetamodel                maskingMetamodel;

  protected MiImportProcess(BuildingBlockServiceLocator bbServiceLocator, AttributeValueService avService,
      AttributeTypeGroupService attributeTypeGroupService, AttributeTypeService attributeTypeService,
      BuildingBlockTypeService buildingBlockTypeService, ImportStrategy importStrategy, IteraplanMiLoadTaskFactory loadTaskFactory) {
    super(bbServiceLocator, avService);
    this.importStrategy = importStrategy;
    this.databaseAccess = new ImportDatabaseDataAccess(loadTaskFactory);
    this.attributeTypeGroupService = attributeTypeGroupService;
    this.attributeTypeService = attributeTypeService;
    this.buildingBlockTypeService = buildingBlockTypeService;
    this.preImportModel = ModelUtil.copy(databaseAccess.getRMetamodel(), databaseAccess.getModel());
  }

  protected void setMaskingMetamodel(MaskingMetamodel maskingMetamodel) {
    this.maskingMetamodel = maskingMetamodel;
  }

  protected MaskingMetamodel getMaskingMetamodel() {
    return this.maskingMetamodel;
  }

  /**
   * @return
   *    The model to import.
   */
  protected abstract Model getModelToImport();

  /**
   * Reloads the model from the excel workbook.
   * A reload is necessary after metamodel changes have been imported,
   * so that the external model has all features of the canonic metamodel.
   */
  protected abstract void loadModelFromResource();

  /**
   * Getter for the filtered (in terms of permissions and partiality) RMetamodel
   * 
   * @return
   *    The RMetamodel that is used during the import 
   */
  protected RMetamodel getImportMetamodel() {
    ExportInfo info = getExportInfo();
    RMetamodel rMetamodel = PojoRMetamodelWithPermissionsCopier.copyRMetamodel(getCanonicMetamodel(), ElasticMiContext.getCurrentContext()
        .getAccessController());
    if (getMaskingMetamodel() == null) {
      setMaskingMetamodel(MetamodelWithPermissionsMaskUtil.mask(rMetamodel));
    }
    else {
      getMaskingMetamodel().reBase(rMetamodel);
    }
    if (info.isPartial()) {
      try {
        IteraQlQuery query = IteraQl2Compiler.compile(getMaskingMetamodel(), info.getHiddenMainType() + ";");
        return new APartialExportMetamodel(getMaskingMetamodel(), query.getLeft());
      } catch (IteraQl2Exception e) {
        getImportProcessMessages().onMessage(new LocalizedIteraplanMessage(Severity.ERROR, MSG_PARTIAL_MISSING_MAIN_TYPE, info.getHiddenMainType()));
        return getMaskingMetamodel();
      }
    }
    else {
      return getMaskingMetamodel();
    }
  }

  protected abstract ExportInfo getExportInfo();

  protected abstract AttributeTypeReader getAttributeTypeReader();

  protected RMetamodel getCanonicMetamodel() {
    return getDatabaseDataAccess().getRMetamodel();
  }

  protected Model getCanonicModel() {
    return getDatabaseDataAccess().getModel();
  }

  protected ImportDatabaseDataAccess getDatabaseDataAccess() {
    return this.databaseAccess;
  }

  protected void validate() {
    ExportInfo info = getExportInfo();
    if (!info.isTimeValid()) {
      if (info.getHiddenExportTime() == null) {
        getImportProcessMessages().onMessage(new SimpleMessage(Severity.INFO, "No export timestamp detected. Using current system time."));
        info.setHiddenExportTime(new Date());
      }
      else {
        Date hidden = info.getHiddenExportTime();
        Date visible = info.getVisibleExportTime();
        RWAtomicDataTypeExpression<?> dateTimeType = AtomicDataType.DATE_TIME.type();
        String hiddenTimeString = dateTimeType.toString(hidden);
        String visibleTimeString = dateTimeType.toString(visible);
        getImportProcessMessages().onMessage(
            new SimpleMessage(Severity.WARNING, "Given export timestamp of \"" + visibleTimeString
                + "\" does not match the saved export timestamp of \"" + hiddenTimeString + "\". Using saved export time."));
      }
    }
    if (!info.isTypeValid()) {
      String hidden = info.getHiddenMainType();
      String visible = info.getVisibleMainType();
      if (info.isPartial()) {
        getImportProcessMessages().onMessage(
            new SimpleMessage(Severity.WARNING, "Given main type \"" + visible + "\" doesn't match detected main type \"" + hidden
                + "\". Using the latter."));
      }
      else {
        getImportProcessMessages().onMessage(
            new SimpleMessage(Severity.WARNING, "Main type \"" + visible + "\" given, but no main type detected. Performing full import."));
      }
    }
  }

  /**{@inheritDoc}**/
  @Override
  public final boolean compareMetamodel() {
    getCurrentCheckList().pending(CheckPoint.METAMODEL_COMPARE);
    AttributeTypeImporter atImporter = createATImporter();
    boolean diffSuccess = atImporter.diff();
    finalizeMessages();
    if (diffSuccess) {
      getCurrentCheckList().done(CheckPoint.METAMODEL_COMPARE);
    }
    return diffSuccess;
  }

  /**{@inheritDoc}**/
  @Override
  public final boolean writeMetamodel() {
    getCurrentCheckList().pending(CheckPoint.METAMODEL_MERGE);
    AttributeTypeImporter atImporter = createATImporter();
    boolean mergeSuccess = atImporter.merge();
    finalizeMessages();
    if (mergeSuccess) {
      getCurrentCheckList().done(CheckPoint.METAMODEL_MERGE);
      if (atImporter.wereChangesApplied()) {
        databaseAccess.loadAllWithoutContext();
        updateMaskingMetamodelBase(); // necessary to respect imported attributes
        loadModelFromResource();
      }
    }
    return mergeSuccess;
  }

  /**{@inheritDoc}**/
  @Override
  public final boolean dryRun() {
    getCurrentCheckList().pending(CheckPoint.MODEL_COMPARE);

    Model modelCopy = ModelUtil.copy(getCanonicMetamodel(), getCanonicModel());

    ModelDiff modelDiff = calculateModelDiff(modelCopy);

    modelDiff.merge(getMergeStrategy(importStrategy, new ElasticMiModelMergeable(modelCopy)));

    ModelValidator postMergeValidator = IteraplanModelValidators.getPostMergeModelValidator(getCanonicMetamodel());
    boolean modelIsValid = postMergeValidator.validate(modelCopy, getImportProcessMessages());

    if (modelIsValid) {
      SimpleMessage changeMessage = new SimpleMessage(Severity.INFO,
          MiMessageAccess.getString("de.iteratec.iteraplan.elasticmi.excelimport.diffHeadline"));
      addImportResult(changeMessage, modelCopy);
      getCurrentCheckList().done(CheckPoint.MODEL_COMPARE);
    }

    return modelIsValid;
  }

  private ModelDiff calculateModelDiff(Model model) {
    ModelDiffImpl modelDiff = new ModelDiffImpl(getImportMetamodel(), model, getModelToImport(), new ModelMatcherImpl(), getImportProcessMessages());
    if (getExportInfo().isPartial()) {
      //note: no need to update masking metamodel here,
      //since partial metamodel does not support metamodel changes
      BasePartialExportMetamodel partialMetamodel = (APartialExportMetamodel) getImportMetamodel();
      return (new PartialModelDiffTransformer()).transformToCanonic(partialMetamodel, modelDiff, getImportProcessMessages());
    }
    else {
      return modelDiff;
    }
  }

  /**
   * Rebase masking metamodel with current permissions-restricted metamodel
   */
  private void updateMaskingMetamodelBase() {
    getMaskingMetamodel().reBase(
        PojoRMetamodelWithPermissionsCopier.copyRMetamodel(getDatabaseDataAccess().getRMetamodel(), ElasticMiContext.getCurrentContext()
            .getAccessController()));
  }

  /**{@inheritDoc}**/
  @Override
  public final boolean mergeModelIntoDb() {
    getCurrentCheckList().pending(CheckPoint.MODEL_WRITE);

    // reload metamodel-mapping to avoid LazyInitExceptions when accessing attribute values during writing
    getDatabaseDataAccess().loadMetamodel();

    //Note: no need to update masking metamodel, since no metamodel changes are expected after the dry run
    ModelDiff modelDiff = calculateModelDiff(getCanonicModel());
    MiIteraplanDiffWriter diffWriter = new MiIteraplanDiffWriter(modelDiff, getAttributeValueService(), getBuildingBlockServiceLocator(),
        getDatabaseDataAccess().getMetamodelMapping(), getDatabaseDataAccess().getInstanceMapping(), getImportProcessMessages());

    boolean writeSucceded = diffWriter.writeDifferences(getMergeStrategy(importStrategy, diffWriter));

    if (writeSucceded) {
      getDatabaseDataAccess().loadModelWithContext();
      SimpleMessage changeMessage = new SimpleMessage(Severity.INFO,
          MiMessageAccess.getString("de.iteratec.iteraplan.elasticmi.excelimport.mergeHeadline"));
      addImportResult(changeMessage, getCanonicModel());
      getCurrentCheckList().done(CheckPoint.MODEL_WRITE);
    }

    return writeSucceded;
  }

  /**{@inheritDoc}**/
  @Override
  public MiImportProcessMessages getImportProcessMessages() {
    return importProcessMessages;
  }

  private final MergeStrategy getMergeStrategy(ImportStrategy strategy, ModelMergeable mergeable) {
    switch (strategy) {
      case ADDITIVE:
        return new AdditiveMergeStrategy(mergeable, getModelToImport(), this.getCanonicModel(), getImportProcessMessages());
      case CUD:
        Date timestamp = getExportInfo().getHiddenExportTime();
        return new CUDMergeStrategy(mergeable, timestamp, getCanonicModel(), getImportProcessMessages());
      default:
        // should not happen
        throw new UnsupportedOperationException("Import strategy \"" + strategy.getText() + "\" not supported.");
    }
  }

  private AttributeTypeImporter createATImporter() {
    return new AttributeTypeImporter(getAttributeTypeReader(), getImportProcessMessages(), attributeTypeGroupService, attributeTypeService,
        buildingBlockTypeService);
  }

  private void addImportResult(Message changeMessage, Model mergedModel) {
    ModelDiff diff = new ModelDiffImpl(getCanonicMetamodel(), preImportModel, mergedModel, new ModelMatcherImpl(), MessageListener.NOOP_LISTENER);
    Set<CreateDiff> creates = diff.getCreateDiffs();
    Set<UpdateDiff> updates = diff.getUpdateDiffs();
    Set<DeleteDiff> deletes = diff.getDeleteDiffs();
    if (!(creates.isEmpty() && updates.isEmpty() && deletes.isEmpty())) {
      getImportProcessMessages().onMessage(changeMessage);
      for (CreateDiff create : creates) {
        getImportProcessMessages().onMessage(new CreateDiffMessage(create));
      }
      for (UpdateDiff update : updates) {
        getImportProcessMessages().onMessage(new UpdateDiffMessage(update));
      }
      for (DeleteDiff delete : deletes) {
        getImportProcessMessages().onMessage(new DeleteDiffMessage(delete));
      }
    }
    finalizeMessages();
  }

  protected final void finalizeMessages() {
    importProcessMessages.finalizeCheckPoint(getCurrentCheckList().getPending());
  }
}