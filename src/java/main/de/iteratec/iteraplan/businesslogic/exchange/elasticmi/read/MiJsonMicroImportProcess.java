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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess.CheckPoint;
import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcessMessages;
import de.iteratec.iteraplan.businesslogic.exchange.common.SimpleMessage;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model.MiIteraplanDiffWriter;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.diff.model.CreateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.DeleteDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.MergeStrategy;
import de.iteratec.iteraplan.elasticmi.diff.model.ModelDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.UpdateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.CUDMergeStrategy;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.DeleteDiffImpl;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ElasticMiModelMergeable;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ModelDiffImpl;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ModelMatcherImpl;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ModelMergeable;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.PartialModelDiffTransformer;
import de.iteratec.iteraplan.elasticmi.dynamic.DynamicMetamodel;
import de.iteratec.iteraplan.elasticmi.dynamic.DynamicMetamodelImpl;
import de.iteratec.iteraplan.elasticmi.dynamic.FilterPredicate;
import de.iteratec.iteraplan.elasticmi.io.mapper.json.JsonSingleOEMapper;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.messages.MiMessageAccess;
import de.iteratec.iteraplan.elasticmi.messages.merge.CreateDiffMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.DeleteDiffMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.UpdateDiffMessage;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.IntegerAtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.partial.BasePartialExportMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.partial.b.BPartialExportMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.mask.MaskingMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.mask.MetamodelWithPermissionsMaskUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelWithPermissionsCopier;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ModelUtil;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiAccessLevel;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiTypePermission;
import de.iteratec.iteraplan.elasticmi.validate.model.ModelValidator;


/**
 * ImportProcess for Json's with an single ObjectExpression.
 * This ImportProcess do not extends the MiImportProcess or ImportProcess because this ImportProcess special variation and has his own methods.
 */
public class MiJsonMicroImportProcess implements JsonMicroImportProcess {

  private static final Logger               LOGGER                            = Logger.getIteraplanLogger(MiJsonMicroImportProcess.class);

  private static final String               VALIDATE_ERROR_MESSAGE_KEY        = "json.import.exception.notValid";
  private static final String               DELETE_EXCEPTION_MESSAGE_KEY      = "json.delete.exception";
  private static final String               DELETE_NOT_FOUND_MESSAGE_KEY      = "json.delete.exception.elementNotFound";
  private static final String               DELETE_NO_PERMISSIONS_MESSAGE_KEY = "json.delete.exception.noPermissions";
  private static final String               CREATE_EXCEPTION_MESSAGE_KEY      = "json.create.exception";
  private static final String               CREATE_NO_PERMISSIONS_MESSAGE_KEY = "json.create.exception.noPermissions";
  private static final String               UPDATE_EXCEPTION_MESSAGE_KEY      = "json.update.exception";
  private static final String               UPDATE_NOT_FOUND_MESSAGE_KEY      = "json.update.exception.elementNotFound";
  private static final String               UPDATE_NO_PERMISSIONS_MESSAGE_KEY = "json.update.exception.noPermissions";

  private static final String               FILTERED_PROP_NAME                = "id";

  private final BuildingBlockServiceLocator bbServiceLocator;
  private final AttributeValueService       attributeValueService;

  private final MiImportProcessMessages     importProcessMessages             = new MiImportProcessMessages();
  private final ImportDatabaseDataAccess    databaseAccess;

  private MaskingMetamodel                  maskingMetamodel;

  private BigInteger                        idValue;
  private RStructuredTypeExpression         mainType;
  private JsonObject                        json                              = null;

  private Model                             modelToImport;
  private final Model                       preImportModel;

  private boolean                           success                           = true;

  /**
   * Default constructor.
   * @param bbServiceLocator
   * @param attributeValueService
   * @param loadTaskFactory
   */
  public MiJsonMicroImportProcess(BuildingBlockServiceLocator bbServiceLocator, AttributeValueService attributeValueService,
      IteraplanMiLoadTaskFactory loadTaskFactory) {

    this.bbServiceLocator = bbServiceLocator;
    this.attributeValueService = attributeValueService;
    this.databaseAccess = new ImportDatabaseDataAccess(loadTaskFactory);
    this.preImportModel = ModelUtil.copy(databaseAccess.getRMetamodel(), databaseAccess.getModel());
  }

  /**
   * Set id, baseType and parse the inputStream to an JsonObject
   * @param id
   * @param baseType
   * @param in
   */
  private void init(BigInteger id, RStructuredTypeExpression baseType, InputStream in) {
    this.idValue = id;
    this.mainType = baseType;
    this.json = readJsonFromInputStream(in);
  }

  /**
   * Parse the inputStream to an JsonObject.
   * If the inputStream is null, null is returned and the {@link #importProcessMessages} get an error message.
   * 
   * @param inputStream
   * @return JsonObject
   */
  private JsonObject readJsonFromInputStream(InputStream inputStream) {
    if (inputStream == null) {
      LOGGER.error("The input stream to read the json from was null.");
      return null;
    }

    try {
      JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
      return new JsonParser().parse(reader).getAsJsonObject();
    } catch (UnsupportedEncodingException e) {
      LOGGER.error("Error reading json", e);
    } catch (IllegalStateException e) {
      LOGGER.error("Error reading json", e);
    }
    return null;
  }

  @Override
  public BigInteger create(RStructuredTypeExpression baseType, InputStream in) {
    init(null, baseType, in);

    if (!isJsonValid()) {
      return null;
    }

    // check user permission
    if (!hasPermission(ElasticMiAccessLevel.CREATE)) {
      getImportProcessMessages().onMessage(new LocalizedIteraplanMessage(Severity.WARNING, CREATE_NO_PERMISSIONS_MESSAGE_KEY, mainType, idValue));
      return null;
    }

    // create an id for the new oe
    this.idValue = getCanonicModel().getIdProvider().getId();

    BigInteger createdID = executeImport();
    if (hasFailed()) {
      importProcessMessages.onMessage(new LocalizedIteraplanMessage(Severity.ERROR, CREATE_EXCEPTION_MESSAGE_KEY));
      return null;
    }

    return createdID;
  }

  private boolean hasFailed() {
    return !success || !importProcessMessages.getMessages(Severity.ERROR).isEmpty();
  }

  @Override
  public void update(BigInteger id, RStructuredTypeExpression baseType, InputStream in) {
    init(id, baseType, in);

    if (!isJsonValid()) {
      return;
    }

    if (!hasPermission(ElasticMiAccessLevel.UPDATE)) {
      getImportProcessMessages().onMessage(new LocalizedIteraplanMessage(Severity.WARNING, UPDATE_NO_PERMISSIONS_MESSAGE_KEY, idValue));
      return;
    }

    ObjectExpression oeToUpdate = getCanonicModel().findById(mainType, idValue);
    if (oeToUpdate == null) {
      importProcessMessages.onMessage(new LocalizedIteraplanMessage(Severity.ERROR, UPDATE_NOT_FOUND_MESSAGE_KEY));
      return;
    }

    executeImport();
    if (hasFailed()) {
      importProcessMessages.onMessage(new LocalizedIteraplanMessage(Severity.ERROR, UPDATE_EXCEPTION_MESSAGE_KEY));
    }
  }

  private boolean isJsonValid() {
    if (json == null || importProcessMessages.getMessages(Severity.ERROR).size() > 0) {
      importProcessMessages.onMessage(new LocalizedIteraplanMessage(Severity.ERROR, VALIDATE_ERROR_MESSAGE_KEY));
      return false;
    }
    return true;
  }

  private boolean hasPermission(ElasticMiAccessLevel accessLevel) {
    ElasticMiTypePermission permission = new ElasticMiTypePermission(mainType.getPersistentName(), accessLevel);
    return ElasticMiContext.getCurrentContext().getAccessController().has(permission);
  }

  @Override
  public void delete(BigInteger id, RStructuredTypeExpression baseType) {
    this.idValue = id;
    this.mainType = baseType;

    if (!hasPermission(ElasticMiAccessLevel.DELETE)) {
      getImportProcessMessages().onMessage(new LocalizedIteraplanMessage(Severity.WARNING, DELETE_NO_PERMISSIONS_MESSAGE_KEY, mainType, idValue));
      return;
    }

    ObjectExpression oeToDelete = getCanonicModel().findById(mainType, idValue);
    if (oeToDelete == null) {
      importProcessMessages.onMessage(new LocalizedIteraplanMessage(Severity.ERROR, DELETE_NOT_FOUND_MESSAGE_KEY));
      // The element that should be deleted, could not be found. Cancel the operation.
      return;
    }

    ModelDiff modelDiff = new SimpleModelDeleteDiff(mainType, oeToDelete);

    // merger into the Database
    mergeModelIntoDb(modelDiff);
    if (hasFailed()) {
      getImportProcessMessages().onMessage(new LocalizedIteraplanMessage(Severity.ERROR, DELETE_EXCEPTION_MESSAGE_KEY, mainType, idValue));
    }
  }

  /**
   * Execute the import process.
   * @return The ID of the merged object expression, null if merge wasn't successful
   */
  private BigInteger executeImport() {

    creatImportModelAndValidate();
    dryRun();
    if (success) {
      return mergeModelIntoDb();
    }
    else {
      return null;
    }
  }

  /**
   * Create the model to Import and validate it.
   */
  public void creatImportModelAndValidate() {

    // check if it is an valid json
    if (!validate()) {
      importProcessMessages.onMessage(new LocalizedIteraplanMessage(Severity.ERROR, VALIDATE_ERROR_MESSAGE_KEY));
      success = false;
      return;
    }

    // read the json and create a model
    modelToImport = loadModelFromJson();

    // validate the generated model
    ModelValidator preMergeValidator = IteraplanModelValidators.getPreMergeModelValidator(getImportMetamodel());
    boolean modelIsValid = preMergeValidator.validate(modelToImport, getImportProcessMessages());

    modelIsValid &= !getImportProcessMessages().hasErrors();
    if (!modelIsValid) {
      importProcessMessages.onMessage(new LocalizedIteraplanMessage(Severity.ERROR, VALIDATE_ERROR_MESSAGE_KEY));
      success = false;
    }
  }

  /**
   * Create the model from the json
   * @return model to import
   */
  private Model loadModelFromJson() {
    JsonSingleOEMapper soeMapper = new JsonSingleOEMapper((BasePartialExportMetamodel) getImportMetamodel(), maskingMetamodel, idValue);
    return modelToImport = soeMapper.read(json, getCanonicModel().getIdProvider());
  }

  /**
   * Check if the json is valid.
   * The JsonObject must contain an result and the result must be a JsonArray. 
   * @return true if the Json is valid, otherwise false
   */
  private boolean validate() {
    JsonElement result = json.get("result");
    if (result == null || !result.isJsonArray()) {
      getImportProcessMessages().onMessage(new LocalizedIteraplanMessage(Severity.ERROR, "json.import.exception.notValid"));
      return false;
    }
    return true;
  }

  private void dryRun() {

    Model modelCopy = ModelUtil.copy(getCanonicMetamodel(), getCanonicModel());

    updateMaskingMetamodelBase(); // necessary to respect imported attributes
    ModelDiff modelDiff = new ModelDiffImpl(getImportMetamodel(), modelCopy, getModelToImport(), new ModelMatcherImpl(), getImportProcessMessages());

    modelDiff = prepareDiff(modelDiff);
    modelDiff.merge(getMergeStrategy(new ElasticMiModelMergeable(modelCopy)));

    ModelValidator postMergeValidator = IteraplanModelValidators.getPostMergeModelValidator(getCanonicMetamodel());
    success = postMergeValidator.validate(modelCopy, getImportProcessMessages()) && success; // success on the right, since validation should be done in any case

    if (success) {
      importProcessMessages.clear();
    }
  }

  /**
   * Create first the ModelDiff and merge this into the database.
   * @return ID of the merged building block
   */
  private BigInteger mergeModelIntoDb() {
    databaseAccess.loadMetamodel();
    ModelDiff modelDiff = new ModelDiffImpl(getImportMetamodel(), getCanonicModel(), getModelToImport(), new ModelMatcherImpl(),
        getImportProcessMessages());
    modelDiff = prepareDiff(modelDiff);

    mergeModelIntoDb(modelDiff);
    if (modelDiff.getCreateDiffs().size() == 1) {
      return modelDiff.getCreateDiffs().iterator().next().getIdAfterMerge();
    }
    else {
      return idValue;
    }
  }

  /**
   * Merges the passed ModelDiff into the Database.
   * @param modelDiff
   */
  private void mergeModelIntoDb(ModelDiff modelDiff) {

    MiIteraplanDiffWriter diffWriter = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator,
        databaseAccess.getMetamodelMapping(), databaseAccess.getInstanceMapping(), getImportProcessMessages());

    success = diffWriter.writeDifferences(getMergeStrategy(diffWriter)) && success; // success on the right, since validation should be done in any case

    if (success) {
      databaseAccess.loadModelWithContext();
      getImportProcessMessages().onMessage(
          new SimpleMessage(Severity.INFO, MiMessageAccess.getString("de.iteratec.iteraplan.elasticmi.excelimport.mergeHeadline")));
      addImportResult(CheckPoint.MODEL_WRITE, getCanonicModel());
    }
  }

  private Model getModelToImport() {
    return modelToImport;
  }

  /**
   * Rebase masking metamodel with current permissions-restricted metamodel
   */
  private void updateMaskingMetamodelBase() {
    RMetamodel rMetamodel = PojoRMetamodelWithPermissionsCopier.copyRMetamodel(databaseAccess.getRMetamodel(), ElasticMiContext.getCurrentContext()
        .getAccessController());
    if (maskingMetamodel == null) {
      maskingMetamodel = MetamodelWithPermissionsMaskUtil.mask(rMetamodel);
    }
    else {
      maskingMetamodel.reBase(rMetamodel);
    }
  }

  /**
   * Transforms the given ModelDiff from a partial to a total reference frame
   */
  private ModelDiff prepareDiff(ModelDiff modelDiff) {
    //note: no need to update masking metamodel here,
    //since partial metamodel does not support metamodel changes
    BasePartialExportMetamodel partialMetamodel = (BPartialExportMetamodel) getImportMetamodel();
    return (new PartialModelDiffTransformer()).transformToCanonic(partialMetamodel, modelDiff, getImportProcessMessages());
  }

  /**
   * Create an return the partialExportMetamodel
   * @return BPartialExportMetamodel
   */
  private RMetamodel getImportMetamodel() {

    // create the MaskingMetamodel
    RMetamodel rMetamodel = PojoRMetamodelWithPermissionsCopier.copyRMetamodel(getCanonicMetamodel(), ElasticMiContext.getCurrentContext()
        .getAccessController());
    if (maskingMetamodel == null) {
      maskingMetamodel = MetamodelWithPermissionsMaskUtil.mask(rMetamodel);
    }
    else {
      maskingMetamodel.reBase(rMetamodel);
    }

    // create the filter predicate
    DynamicMetamodel dMetamodel = new DynamicMetamodelImpl();
    RPropertyExpression filterProp = mainType.findPropertyByPersistentName(FILTERED_PROP_NAME);
    FilterPredicate pred = dMetamodel.createSimplePredicate(filterProp,
        AtomicDataType.INTEGER.type().findComparisonOperator(IntegerAtomicDataType.EQUALS_PERSISTENT_NAME), idValue);

    RStructuredTypeExpression filteredMainType = dMetamodel.createFilteredStructuredType(mainType, pred);

    return new BPartialExportMetamodel(maskingMetamodel, filteredMainType);
  }

  private final MergeStrategy getMergeStrategy(ModelMergeable mergeable) {
    return new CUDMergeStrategy(mergeable, new Date(), getCanonicModel(), getImportProcessMessages());
  }

  /**{@inheritDoc}**/
  @Override
  public ImportProcessMessages getImportProcessMessages() {
    return importProcessMessages;
  }

  private Model getCanonicModel() {
    return databaseAccess.getModel();
  }

  private RMetamodel getCanonicMetamodel() {
    return databaseAccess.getRMetamodel();
  }

  private void addImportResult(CheckPoint modelCompare, Model mergedModel) {
    ModelDiff diff = new ModelDiffImpl(getCanonicMetamodel(), preImportModel, mergedModel, new ModelMatcherImpl(), MessageListener.NOOP_LISTENER);
    for (CreateDiff create : diff.getCreateDiffs()) {
      getImportProcessMessages().onMessage(new CreateDiffMessage(create));
    }
    for (UpdateDiff update : diff.getUpdateDiffs()) {
      getImportProcessMessages().onMessage(new UpdateDiffMessage(update));
    }
    for (DeleteDiff delete : diff.getDeleteDiffs()) {
      getImportProcessMessages().onMessage(new DeleteDiffMessage(delete));
    }
    importProcessMessages.finalizeCheckPoint(modelCompare);
  }

  private static final class SimpleModelDeleteDiff implements ModelDiff {

    private final DeleteDiff deleteDiff;

    public SimpleModelDeleteDiff(RStructuredTypeExpression type, ObjectExpression oe) {
      deleteDiff = new DeleteDiffImpl(type, oe);
    }

    @Override
    public Set<CreateDiff> getCreateDiffs() {
      return Collections.emptySet();
    }

    @Override
    public Set<CreateDiff> getCreateDiffsForType(RStructuredTypeExpression structuredType) {
      return Collections.emptySet();
    }

    @Override
    public Set<UpdateDiff> getUpdateDiffs() {
      return Collections.emptySet();
    }

    @Override
    public Set<UpdateDiff> getUpdateDiffsForType(RStructuredTypeExpression structuredType) {
      return Collections.emptySet();
    }

    @Override
    public Set<DeleteDiff> getDeleteDiffs() {
      return Sets.newHashSet(deleteDiff);
    }

    @Override
    public Set<DeleteDiff> getDeleteDiffsForType(RStructuredTypeExpression structuredType) {
      if (deleteDiff.getStructuredType().getCanonicBase().equals(structuredType)) {
        return Sets.newHashSet(deleteDiff);
      }
      else {
        return Collections.emptySet();
      }
    }

    /**{@inheritDoc}**/
    @Override
    public void merge(MergeStrategy strategy) {
      // nop
    }
  }
}