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
package de.iteratec.iteraplan.businesslogic.exchange.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages.ErrorLevel;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMetamodelLoader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.ModelLoader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.validator.IgnoreMandatoryATNotSetFilter;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.validator.IteraplanModelValidator;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.writer.IteraplanModelDiffWriter;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.writer.IteraplanModelDiffWriter.IteraplanDiffWriterResult;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.ModelFactory;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilder;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilder.DiffMode;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilderResult;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffWriterResult;
import de.iteratec.iteraplan.elasticeam.model.compare.LeftSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchResult;
import de.iteratec.iteraplan.elasticeam.model.compare.Matcher;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.impl.DiffBuilderImpl;
import de.iteratec.iteraplan.elasticeam.model.compare.impl.MatcherImpl;
import de.iteratec.iteraplan.elasticeam.model.merge.ModelMerger;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelConsistencyViolation;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelValidator;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelValidatorResult;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.IteraplanDiffClassifier;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.IteraplanMetamodelDifferentialWriter;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.MMetamodelComparator;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.MMetamodelComparator.MMChange;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.TypeOfDiff;


public abstract class EamImportProcess extends ImportProcess {

  private final IteraplanMetamodelLoader          metamodelLoader;
  private final ModelLoader                       modelLoader;

  private final EamImportProcessMessages          messages = new EamImportProcessMessages(new ResultMessages());

  private IteraplanMapping                        mapping;

  private Model                                   baseModel;
  private BiMap<Object, UniversalModelExpression> instanceMapping;
  private Multimap<TypeOfDiff, MMChange<?>>       classifiedMetamodelDiffs;
  //  private Model                             baseModel;
  //  private BiMap<Object, ObjectExpression>   instanceMapping;
  //  private MetamodelWriter                   metamodelWriter;

  private DiffMode                                diffMode = DiffMode.ADDITIVE;

  private UserContext                             originalUserContext;

  protected EamImportProcess(IteraplanMetamodelLoader metamodelLoader, ModelLoader modelLoader, BuildingBlockServiceLocator bbServiceLocator,
      AttributeValueService avService) {
    super(bbServiceLocator, avService);
    this.metamodelLoader = metamodelLoader;
    this.modelLoader = modelLoader;

    loadModelAndMetamodelFromDb(true);
  }

  public EamImportProcessMessages getImportProcessMessages() {
    return this.messages;
  }

  private void loadModelAndMetamodelFromDb(boolean loadModel) {
    // FIXME Ugly hack necessary because for writing into the DB the IteraplanMapping is needed
    // After complete switch to the elastic core use only elasticeamService, avoiding the need for UserContext-switches here
    try {
      assureSupervisorRole();
      mapping = metamodelLoader.loadConceptualMetamodelMapping();
      if (loadModel) {
        baseModel = ModelFactory.INSTANCE.createModel(mapping.getMetamodel());
        instanceMapping = modelLoader.load(baseModel, mapping);
      }
    } finally {
      restoreOriginalRolePermissions();
    }
  }

  /**
   * Creates a temporary user context with full permissions, if all permissions are not already granted.
   * NOTE: Be sure to *really* call {@link #restoreOriginalRolePermissions()} after the
   *    permission-sensitive work is completed
   */
  private void assureSupervisorRole() {
    // check whether the currently logged in user has supervisor privileges already
    boolean isSupervisor = false;
    for (Role role : UserContext.getCurrentUserContext().getRoles()) {
      if (role.isSupervisor()) {
        isSupervisor = true;
        break;
      }
    }
    // if not, create a temporary user context which is capable of loading the entire metamodel
    if (!isSupervisor) {
      originalUserContext = UserContext.getCurrentUserContext();

      User user = new User();
      user.setLoginName("ElasticeamLoader");

      Role superRole = new Role();
      superRole.setRoleName(Role.SUPERVISOR_ROLE_NAME);

      Set<Role> roles = new HashSet<Role>();
      roles.add(superRole);

      UserContext userContext = new UserContext("ElasticLoader", roles, originalUserContext.getLocale(), user);
      userContext.setDataSource(originalUserContext.getDataSource());
      UserContext.setCurrentUserContext(userContext);
    }
  }

  /**
   * Restores the original user-context of the current user, if if has been replaced by
   * {@link #assureSupervisorRole()} before
   */
  private void restoreOriginalRolePermissions() {
    if (originalUserContext != null) {
      UserContext.detachCurrentUserContext();
      UserContext.setCurrentUserContext(originalUserContext);
      originalUserContext = null;
    }
  }

  @Override
  public boolean compareMetamodel() {
    getCurrentCheckList().pending(CheckPoint.METAMODEL_COMPARE);
    ResultMessages result = new ResultMessages();

    List<MMChange<?>> diffs = MMetamodelComparator.diff(mapping.getMetamodel(), getMetamodelToImport());

    IteraplanDiffClassifier classifier = new IteraplanDiffClassifier(mapping);
    classifiedMetamodelDiffs = classifier.classifyDiffElements(diffs);

    Collection<MMChange<?>> unapplicableDiffs = IteraplanDiffClassifier.getUnapplicableDiffs(classifiedMetamodelDiffs);
    if (!unapplicableDiffs.isEmpty()) {
      result.addMessage(ErrorLevel.ERROR, "Unapplicable Metamodel changes detected:");
      result.addMessages(ErrorLevel.ERROR, MMetamodelComparator.getDiffInfoStrings(unapplicableDiffs));
      getImportProcessMessages().setWrappedResultMessages(result);
      return false;
    }

    // record checkpoint as completed
    getCurrentCheckList().done(CheckPoint.METAMODEL_COMPARE);

    Collection<MMChange<?>> diffsToApply = IteraplanDiffClassifier.getDiffsToApply(classifiedMetamodelDiffs);
    if (diffsToApply.isEmpty()) {
      result.addMessage(ErrorLevel.INFO, "No Metamodel changes necessary.");
      // skip the next step implicitly
      writeMetamodel();
      return true;
    }

    result.addMessage(ErrorLevel.INFO, "There are changes to the current Metamodel necessary to proceed:");
    result.addMessages(ErrorLevel.INFO, MMetamodelComparator.getDiffInfoStrings(diffsToApply));

    getImportProcessMessages().setWrappedResultMessages(result);
    return true;
  }

  @Override
  public boolean writeMetamodel() {
    getCurrentCheckList().pending(CheckPoint.METAMODEL_MERGE);

    ResultMessages result = new ResultMessages();

    IteraplanMetamodelDifferentialWriter metamodelWriter = new IteraplanMetamodelDifferentialWriter(mapping);
    Collection<MMChange<?>> appliedDiffs = metamodelWriter.write(classifiedMetamodelDiffs);
    if (appliedDiffs.isEmpty()) {
      result.addMessage(ErrorLevel.INFO, "No Metamodel changes had to be applied.");
      // mark checkpoint as done and proceed directly
      getCurrentCheckList().done(CheckPoint.METAMODEL_MERGE);
      getImportProcessMessages().setWrappedResultMessages(result);
      return true;
    }

    loadModelAndMetamodelFromDb(false);
    result.addMessage(ErrorLevel.INFO, "Following changes were applied to the current Metamodel:");
    result.addMessages(ErrorLevel.INFO, MMetamodelComparator.getDiffInfoStrings(appliedDiffs));

    getCurrentCheckList().done(CheckPoint.METAMODEL_MERGE);
    getImportProcessMessages().setWrappedResultMessages(result);
    return true;
  }

  @Override
  public boolean dryRun() {
    getCurrentCheckList().pending(CheckPoint.MODEL_COMPARE);
    ResultMessages result = new ResultMessages();
    Metamodel metamodel = mapping.getMetamodel();

    Model workingModel = ModelFactory.INSTANCE.createModel(metamodel);

    // FIXME ugly hack, see #loadModelAndMetamodelFromDb
    try {
      assureSupervisorRole();
      modelLoader.load(workingModel, mapping);
    } finally {
      restoreOriginalRolePermissions();
    }

    DiffBuilderResult diffs = matchAndDiff(workingModel, getModelToImport(), result);

    ModelMerger modelMerger = new ModelMerger(workingModel, metamodel, diffs);
    DiffWriterResult writeResult = modelMerger.writeDifferences();
    result.addMessages(ErrorLevel.ERROR, writeResult.getErrors());

    ModelValidatorResult validatorResult = validateModel(metamodel, modelMerger.getMergedModel(), true, true, false, true);

    if (!validatorResult.getViolations().isEmpty()) {
      for (ModelConsistencyViolation violation : validatorResult.getViolations()) {
        result.addMessage(ErrorLevel.ERROR, violation.getInfoString());
      }
    }

    if (result.getErrorLevel() != ErrorLevel.ERROR) {
      result.addMessage(ErrorLevel.INFO, "When importing, following changes will be applied:");
      List<String> infoMessages = getBaseDiffInfoStrings(writeResult.getAppliedDiffs());
      result.addMessages(ErrorLevel.INFO, infoMessages);

      // mark checkpoint as done
      getCurrentCheckList().done(CheckPoint.MODEL_COMPARE);
    }
    getImportProcessMessages().setWrappedResultMessages(result);
    return result.getErrorLevel() != ErrorLevel.ERROR;
  }

  @Override
  public boolean mergeModelIntoDb() {
    getCurrentCheckList().pending(CheckPoint.MODEL_WRITE);
    ResultMessages result = new ResultMessages();

    loadModelAndMetamodelFromDb(true);

    DiffBuilderResult diffs = matchAndDiff(baseModel, getModelToImport(), result);

    IteraplanModelDiffWriter writer = new IteraplanModelDiffWriter(diffs, mapping, instanceMapping, getAttributeValueService(),
        getBuildingBlockServiceLocator());

    IteraplanDiffWriterResult writerResult = writer.writeDifferences();
    List<String> infoMessages = getBaseDiffInfoStrings(writerResult.getAppliedDiffs());
    result.addMessage(ErrorLevel.INFO, "Following changes were applied:");
    result.addMessages(ErrorLevel.INFO, infoMessages);
    result.addMessages(ErrorLevel.WARNING, writerResult.getWarnings());
    result.addMessages(ErrorLevel.ERROR, writerResult.getErrors());

    if (result.getErrorLevel() != ErrorLevel.ERROR) {
      // mark checkpoint as done only if no errors were logged previously
      getCurrentCheckList().done(CheckPoint.MODEL_WRITE);
    }

    getImportProcessMessages().setWrappedResultMessages(result);
    return result.getErrorLevel() != ErrorLevel.ERROR;
  }

  protected List<String> getMessagesFromValidatorResult(ModelValidatorResult validatorResult) {
    List<String> result = Lists.newArrayList();
    if (!validatorResult.getViolations().isEmpty()) {
      for (ModelConsistencyViolation violation : validatorResult.getViolations()) {
        result.add(violation.getInfoString());
      }
    }
    return result;
  }

  protected List<String> getBaseDiffInfoStrings(List<BaseDiff> diffs) {
    Map<UniversalTypeExpression, Set<BaseDiff>> added = Maps.newHashMap();
    Map<UniversalTypeExpression, Set<BaseDiff>> changed = Maps.newHashMap();
    Map<UniversalTypeExpression, Set<BaseDiff>> removed = Maps.newHashMap();

    for (BaseDiff diff : diffs) {
      if (diff instanceof RightSidedDiff) {
        putDiff(added, diff);
      }
      if (diff instanceof TwoSidedDiff) {
        putDiff(changed, diff);
      }
      if (diff instanceof LeftSidedDiff) {
        putDiff(removed, diff);
      }
    }

    List<String> infoMessages = Lists.newArrayList();
    for (Map.Entry<UniversalTypeExpression, Set<BaseDiff>> entry : added.entrySet()) {
      infoMessages.add(entry.getKey().getPersistentName() + "s added: " + entry.getValue().size());
    }
    for (Map.Entry<UniversalTypeExpression, Set<BaseDiff>> entry : changed.entrySet()) {
      infoMessages.add(entry.getKey().getPersistentName() + "s changed: " + entry.getValue().size());
    }
    for (Map.Entry<UniversalTypeExpression, Set<BaseDiff>> entry : removed.entrySet()) {
      infoMessages.add(entry.getKey().getPersistentName() + "s removed: " + entry.getValue().size());
    }
    if (infoMessages.isEmpty()) {
      infoMessages.add("No changes.");
    }
    return infoMessages;
  }

  private void putDiff(Map<UniversalTypeExpression, Set<BaseDiff>> map, BaseDiff diff) {
    if (!map.containsKey(diff.getType())) {
      map.put(diff.getType(), new HashSet<BaseDiff>());
    }
    map.get(diff.getType()).add(diff);
  }

  public DiffMode getCurrentDiffMode() {
    return diffMode;
  }

  public void setDiffMode(DiffMode diffMode) {
    this.diffMode = diffMode;
  }

  protected ModelValidatorResult validateModel(Metamodel metamodel, Model model, boolean ignoreNullId, boolean ignoreIdUniqueness,
                                               boolean isPossiblePartialModel, boolean checkInterfaceConsistency) {
    ModelValidator validator = new IteraplanModelValidator(metamodel);
    validator.setIgnoreNullId(ignoreNullId);
    validator.setIgnoreIdUniqueness(ignoreIdUniqueness);
    validator.setCheckInterfaceConsistency(checkInterfaceConsistency);
    if (diffMode == DiffMode.ADDITIVE && isPossiblePartialModel) {
      validator.setIgnoreLowerCardinalityBound(true);
    }
    ModelValidatorResult validatorResult = validator.validate(model);

    return new IgnoreMandatoryATNotSetFilter(validatorResult, mapping);
  }

  private DiffBuilderResult matchAndDiff(Model targetModel, Model sourceModel, ResultMessages result) {
    Matcher matcher = new MatcherImpl(getMetamodelToImport(), MatcherImpl.IDCOMPARATOR);
    MatchResult matchResult = matcher.match(targetModel, sourceModel);

    DiffBuilder diffBuilder = new DiffBuilderImpl(matchResult);
    diffBuilder.setMode(getCurrentDiffMode());
    DiffBuilderResult diffs = diffBuilder.computeDifferences();
    result.addMessages(ErrorLevel.WARNING, diffs.getWarnings());
    return diffs;
  }

  protected abstract Metamodel getMetamodelToImport();

  protected abstract Model getModelToImport();

}
