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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.ElasticMiIteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model.CreateOrUpdateDiffHandler.CreateOrUpdateDiff;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticmi.diff.model.Connect;
import de.iteratec.iteraplan.elasticmi.diff.model.CreateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.DeleteDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.Disconnect;
import de.iteratec.iteraplan.elasticmi.diff.model.MergeStrategy;
import de.iteratec.iteraplan.elasticmi.diff.model.ModelDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.ObjectDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyChange;
import de.iteratec.iteraplan.elasticmi.diff.model.UpdateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ModelMergeable;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression.OriginalWType;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.elasticmi.util.NamedUtil;
import de.iteratec.iteraplan.elasticmi.util.StructuredTypeSortUtil;
import de.iteratec.iteraplan.model.BuildingBlock;


public class MiIteraplanDiffWriter extends ModelMergeable {

  private static final Logger               LOGGER        = Logger.getIteraplanLogger(MiIteraplanDiffWriter.class);

  private final ModelDiff                   modelDiff;

  private final BuildingBlockServiceLocator bbServiceLocator;

  private final RMetamodel                  rMetamodel;

  private final Set<ObjectDiff>             diffsToIgnore = Sets.newHashSet();

  private final AttributeValueSetter        attributeValueSetter;
  private final BuiltinPropertySetter       builtinPropertySetter;
  private final InstanceCreator             instanceCreator;
  private final InstanceDeleter             instanceDeleter;
  private final RelationshipSetter          relationshipSetter;
  private final SaveOrUpdater               saveOrUpdater;
  private final LastModificationUpdater     lastModUpdater;
  private final DiffApplyMemory             applications;

  private boolean                           writeSuccess  = true;

  public MiIteraplanDiffWriter(ModelDiff modelDiff, AttributeValueService avService, BuildingBlockServiceLocator bbServiceLocator,
      ElasticMiIteraplanMapping metamodelMapping, BiMap<Object, ObjectExpression> instanceMapping, MessageListener listener) {
    this.modelDiff = modelDiff;
    this.rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(metamodelMapping.getMetamodel());
    this.bbServiceLocator = bbServiceLocator;
    Map<ObjectExpression, ObjectExpression> rightToLeftExpressionMap = Maps.newHashMap();
    for (RStructuredTypeExpression type : rMetamodel.getStructuredTypes()) {
      for (UpdateDiff diff : modelDiff.getUpdateDiffsForType(type)) {
        rightToLeftExpressionMap.put(diff.getRightObjectExpression(), diff.getLeftObjectExpression());
      }
    }
    Map<BigInteger, BuildingBlock> id2bbMap = Maps.newHashMap();
    long start = -System.currentTimeMillis();
    for (Entry<Object, ObjectExpression> entry : instanceMapping.entrySet()) {
      if (entry.getKey() instanceof BuildingBlock) {
        BuildingBlock bb = (BuildingBlock) entry.getKey();
        bb = bbServiceLocator.getService(bb.getTypeOfBuildingBlock()).loadObjectById(bb.getId());
        id2bbMap.put(entry.getValue().getId(), bb);
      }
    }
    LOGGER.debug("BBs reloaded in {0} ms.", start + System.currentTimeMillis());
    this.attributeValueSetter = new AttributeValueSetter(metamodelMapping, id2bbMap, avService, listener);
    this.builtinPropertySetter = new BuiltinPropertySetter(metamodelMapping, id2bbMap, this.bbServiceLocator, listener);
    this.instanceCreator = new InstanceCreator(metamodelMapping, id2bbMap, this.bbServiceLocator, listener);
    this.instanceDeleter = new InstanceDeleter(metamodelMapping, id2bbMap, this.bbServiceLocator, listener);
    this.relationshipSetter = new RelationshipSetter(metamodelMapping, id2bbMap, this.bbServiceLocator, rightToLeftExpressionMap, listener);
    this.saveOrUpdater = new SaveOrUpdater(metamodelMapping, id2bbMap, this.bbServiceLocator, listener);
    this.lastModUpdater = new LastModificationUpdater(metamodelMapping, id2bbMap, listener);
    this.applications = new DiffApplyMemory();
  }

  public boolean writeDifferences(MergeStrategy strategy) {
    LOGGER.info("Writing Diffs...");

    this.relationshipSetter.setMergeStrategy(strategy);
    this.attributeValueSetter.setMergeStrategy(strategy);
    this.builtinPropertySetter.setMergeStrategy(strategy);

    //Explicitly put ISI and IF last for create, and first for delete
    List<RStructuredTypeExpression> orderedForCreate = StructuredTypeSortUtil.orderExistentially(rMetamodel.getStructuredTypes());
    orderedForCreate.remove(rMetamodel.findStructuredTypeByPersistentName("InformationFlow"));
    orderedForCreate.remove(rMetamodel.findStructuredTypeByPersistentName("InformationSystemInterface"));
    orderedForCreate.add(rMetamodel.findStructuredTypeByPersistentName("InformationSystemInterface"));
    orderedForCreate.add(rMetamodel.findStructuredTypeByPersistentName("InformationFlow"));

    LOGGER.info("Step 1: create structured types...");
    for (RStructuredTypeExpression type : orderedForCreate) {
      createObjectExpressions(modelDiff.getCreateDiffsForType(type), strategy);
    }
    LOGGER.info("Step 1 done.");

    LOGGER.info("Step 2: add relationships for sortal types...");
    for (RStructuredTypeExpression type : orderedForCreate) {
      //Note: ordering does not matter here
      connectInstances(type, modelDiff.getCreateDiffsForType(type), strategy);
    }
    LOGGER.info("Step 2 done.");

    LOGGER.info("Step 4: update universal types...");
    for (RStructuredTypeExpression type : orderedForCreate) {
      updateExistingObjectExpressions(type, modelDiff.getUpdateDiffsForType(type), strategy);
    }
    LOGGER.info("Step 4 done.");

    LOGGER.info("Step 5: delete structured types...");
    List<RStructuredTypeExpression> orderedForDelete = Lists.reverse(orderedForCreate);
    for (RStructuredTypeExpression type : orderedForDelete) {
      deleteObjectExpressions(type, modelDiff.getDeleteDiffsForType(type), strategy);
    }
    LOGGER.info("Step 5 done.");

    //cleanup has been skipped during diff application, execute it now
    bbServiceLocator.getAllBBService().performCleanup();

    LOGGER.info("All Diffs written.");

    return writeSuccess;
  }

  private void createObjectExpressions(Set<CreateDiff> createDiffs, MergeStrategy mergeStrategy) {
    for (CreateDiff diff : createDiffs) {
      mergeStrategy.applyCreate(diff);
    }
  }

  private void connectInstances(RStructuredTypeExpression type, Set<CreateDiff> createDiffs, MergeStrategy strategy) {
    LOGGER.info(type.getPersistentName());
    if (!OriginalWType.RELATIONSHIP.equals(type.getOriginalWType())) {
      for (CreateDiff diff : getSortedDiffs(createDiffs, type)) {
        LOGGER.info("- add relationships for {0}", diff.getObjectExpression());
        strategy.applyConnects(diff);
      }
    }
  }

  private void updateExistingObjectExpressions(RStructuredTypeExpression type, Set<UpdateDiff> updateDiffs, MergeStrategy strategy) {
    LOGGER.info(type.getPersistentName());
    for (UpdateDiff diff : getSortedDiffs(updateDiffs, type)) {
      strategy.apply(diff);
    }
  }

  private void deleteObjectExpressions(RStructuredTypeExpression type, Set<DeleteDiff> deleteDiffs, MergeStrategy strategy) {
    for (DeleteDiff diff : getSortedDiffs(deleteDiffs, type)) {
      strategy.apply(diff);
    }
  }

  private void createInstanceWithoutRelations(CreateDiff diff) {
    LOGGER.info("- add {0}", diff.getObjectExpression());
    createInstance(diff);
    CreateOrUpdateDiff diffToHandle = new CreateOrUpdateDiff(diff);
    handle(diffToHandle, builtinPropertySetter);
    handle(diffToHandle, saveOrUpdater);
    handle(diffToHandle, attributeValueSetter);
  }

  private void createInstanceWithRelations(CreateDiff diff) {
    LOGGER.info("- add {0}", diff.getObjectExpression());
    createInstance(diff);
    CreateOrUpdateDiff diffToHandle = new CreateOrUpdateDiff(diff);
    handle(diffToHandle, relationshipSetter);
    handle(diffToHandle, builtinPropertySetter);
    handle(diffToHandle, saveOrUpdater);
    handle(diffToHandle, attributeValueSetter);
  }

  private boolean handle(CreateOrUpdateDiff diff, CreateOrUpdateDiffHandler handler) {
    boolean success = false;
    if (!diffsToIgnore.contains(diff.isLeft() ? diff.getLeft() : diff.getRight())) {
      success = handler.handleDiff(diff);
      if (!success) {
        diffsToIgnore.add(diff.isLeft() ? diff.getLeft() : diff.getRight());
      }
    }
    return success;
  }

  private void createInstance(CreateDiff diff) {
    if (!diffsToIgnore.contains(diff)) {
      boolean succes = instanceCreator.handleCreateDiff(diff);
      if (!succes) {
        diffsToIgnore.add(diff);
      }
    }
  }

  private void deleteInstance(DeleteDiff diff) {
    if (!diffsToIgnore.contains(diff)) {
      boolean succes = instanceDeleter.handleDeleteDiff(diff);
      if (!succes) {
        diffsToIgnore.add(diff);
      }
    }
  }

  /**
   * Diffs need to be sorted by hierarchical depth because of ITERAPLAN-1663
   * @param diffSet
   *          Set of {@link ObjectDiff}s to be sorted
   * @param type
   *          {@link RStructuredTypeExpression} the diffs refer to
   * @return List of diffs sorted by hierarchical depth, see {@link HierarchicalDiffComparator}
   */
  private static <T extends ObjectDiff> List<T> getSortedDiffs(Set<T> diffSet, RStructuredTypeExpression type) {
    List<T> sortedDiffs = Lists.newArrayList(diffSet);
    Collections.sort(sortedDiffs, new HierarchicalDiffComparator(type));
    return sortedDiffs;
  }

  /**{@inheritDoc}**/
  @Override
  protected boolean apply(CreateDiff diff) {
    RStructuredTypeExpression type = diff.getStructuredType();
    LOGGER.info(type.getPersistentName());
    if (NamedUtil.areSame("InformationSystemInterface", type.getPersistentName())) {
      createInstanceWithRelations(diff);
    }
    else if (OriginalWType.RELATIONSHIP.equals(type.getOriginalWType())) {
      createInstanceWithRelations(diff);
    }
    else {
      createInstanceWithoutRelations(diff);
    }
    return true;
  }

  /**{@inheritDoc}**/
  @Override
  protected boolean apply(CreateDiff diff, Connect connect) {
    if (!applications.hasRelationshipChangesApplied(diff)) {
      applications.registerRelationshipChanges(diff);
      return handle(new CreateOrUpdateDiff(diff), relationshipSetter);
    }
    return false;
  }

  /**{@inheritDoc}**/
  @Override
  protected boolean apply(UpdateDiff diff, PropertyChange change) {
    if (change.isActualChange() && !applications.hasPropertyChangesApplied(diff)) {
      CreateOrUpdateDiff diffToHandle = new CreateOrUpdateDiff(diff);
      boolean applied = handle(diffToHandle, builtinPropertySetter);
      applied |= handle(diffToHandle, attributeValueSetter);
      if (applied) {
        // necessary to explicitely update the last modification info, because in this case
        // no saveOrUpdate is called which, via interceptor, would cause the update otherwise
        lastModUpdater.handleDiff(diff);
      }
      applications.registerPropertyChanges(diff);
      return applied;
    }
    else {
      return false;
    }
  }

  /**{@inheritDoc}**/
  @Override
  protected boolean apply(UpdateDiff diff, Connect connect) {
    if (connect.getObjectExpressions().isSome() && !applications.hasRelationshipChangesApplied(diff)) {
      applications.registerRelationshipChanges(diff);
      // currently no need for lastModUpdater as RelationshipSetter internally performs saveOrUpdate
      return handle(new CreateOrUpdateDiff(diff), relationshipSetter);
    }
    else {
      return false;
    }
  }

  /**{@inheritDoc}**/
  @Override
  protected boolean apply(UpdateDiff diff, Disconnect disconnect) {
    if (disconnect.getObjectExpressions().isSome() && !applications.hasRelationshipChangesApplied(diff)) {
      applications.registerRelationshipChanges(diff);
      // currently no need for lastModUpdater as RelationshipSetter internally performs saveOrUpdate
      return handle(new CreateOrUpdateDiff(diff), relationshipSetter);
    }
    else {
      return false;
    }
  }

  /**{@inheritDoc}**/
  @Override
  protected boolean apply(DeleteDiff delete) {
    deleteInstance(delete);
    return true;
  }

  /**
   * Remembers which diffs had their connects or disconnects or property changes applied already.
   * Used to avoid multiple applications of the same changes which could be caused by the difference
   * in structure between the diffs and the diffWriter methods:<br>
   * For example the {@link MiIteraplanDiffWriter#apply(UpdateDiff, Connect)} will be called for every
   * single Connect in the UpdateDiff, but the {@link RelationshipSetter} used by the diff writer
   * will apply every single connect and disconnect with each of these calls.
   */
  private static class DiffApplyMemory {
    Set<ObjectDiff> registeredRelationshipChanges = Sets.newHashSet();
    Set<UpdateDiff> registeredPropertyChanges     = Sets.newHashSet();

    public boolean hasRelationshipChangesApplied(UpdateDiff update) {
      return registeredRelationshipChanges.contains(update);
    }

    public boolean hasRelationshipChangesApplied(CreateDiff create) {
      return registeredRelationshipChanges.contains(create);
    }

    public boolean hasPropertyChangesApplied(UpdateDiff update) {
      return registeredPropertyChanges.contains(update);
    }

    public void registerRelationshipChanges(UpdateDiff update) {
      registeredRelationshipChanges.add(update);
    }

    public void registerRelationshipChanges(CreateDiff create) {
      registeredRelationshipChanges.add(create);
    }

    public void registerPropertyChanges(UpdateDiff update) {
      registeredPropertyChanges.add(update);
    }
  }

  /**
   * Sorts by hierarchical depth. Deepest elements first.
   */
  private static class HierarchicalDiffComparator implements Comparator<ObjectDiff>, Serializable {

    private static final long                serialVersionUID = 844238688442007781L;

    private final RRelationshipEndExpression parentREE;

    /**
     * Default constructor.
     * @param type
     */
    public HierarchicalDiffComparator(RStructuredTypeExpression type) {
      parentREE = type.findRelationshipEndByPersistentName(ElasticMiConstants.PERSISTENT_NAME_HIERARCHY_PARENT);
    }

    /**{@inheritDoc}**/
    public int compare(ObjectDiff o1, ObjectDiff o2) {
      ObjectExpression exp1 = null;
      ObjectExpression exp2 = null;
      if (o1 instanceof CreateDiff) {
        exp1 = ((CreateDiff) o1).getObjectExpression();
      }
      else if (o1 instanceof DeleteDiff) {
        exp1 = ((DeleteDiff) o1).getObjectExpression();
      }
      else if (o1 instanceof UpdateDiff) {
        exp1 = ((UpdateDiff) o1).getRightObjectExpression();
      }

      if (o2 instanceof CreateDiff) {
        exp2 = ((CreateDiff) o2).getObjectExpression();
      }
      else if (o2 instanceof DeleteDiff) {
        exp2 = ((DeleteDiff) o2).getObjectExpression();
      }
      else if (o2 instanceof UpdateDiff) {
        exp2 = ((UpdateDiff) o2).getRightObjectExpression();
      }

      if (exp1 == null || exp2 == null) {
        // this shouldn't happen
        LOGGER.error("At least one of the two diffs to be compared doesn't reference a model object.");
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }

      int depthExp1 = calculateHierarchicalDepth(exp1);
      int depthExp2 = calculateHierarchicalDepth(exp2);

      return depthExp2 - depthExp1;
    }

    /**
     * @param expression
     * @return depth of hierarchy
     */
    private int calculateHierarchicalDepth(ObjectExpression expression) {
      if (expression == null || parentREE == null) {
        return 0;
      }

      ElasticValue<ObjectExpression> parents = parentREE.apply(expression);
      int depth = 0;
      while (parents.isOne()) {
        depth++;
        parents = parentREE.apply(parents.getOne());
      }
      return depth;
    }
  }
}
