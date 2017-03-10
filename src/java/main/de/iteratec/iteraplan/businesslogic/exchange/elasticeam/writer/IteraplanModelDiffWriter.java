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
package de.iteratec.iteraplan.businesslogic.exchange.elasticeam.writer;

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

import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages.ErrorLevel;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeHierarchic;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilderResult;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffWriter;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffWriterResult;
import de.iteratec.iteraplan.elasticeam.model.compare.LeftSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchingPair;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;


/**
 * Writes diffs from a {@link DiffBuilderResult} into the database.
 */
public class IteraplanModelDiffWriter implements DiffWriter {

  private static final Logger                                           LOGGER        = Logger.getIteraplanLogger(IteraplanModelDiffWriter.class);

  private final DiffBuilderResult                                       diffs;

  private final IteraplanMapping                                        mapping;
  private final BiMap<Object, UniversalModelExpression>                 instanceMapping;
  private final AttributeValueService                                   avService;
  private final BuildingBlockServiceLocator                             bbServiceLocator;
  private final Map<UniversalModelExpression, UniversalModelExpression> rightToLeftExpressionMap;

  private final IteraplanDiffWriterResult                               diffWriterResult;
  private final Set<BaseDiff>                                           diffsToIgnore = Sets.newHashSet();

  public IteraplanModelDiffWriter(DiffBuilderResult diffs, IteraplanMapping mapping, BiMap<Object, UniversalModelExpression> instanceMapping,
      AttributeValueService avService, BuildingBlockServiceLocator bbServiceLocator) {
    this.diffs = diffs;
    this.mapping = mapping;
    this.instanceMapping = instanceMapping;
    this.avService = avService;
    this.bbServiceLocator = bbServiceLocator;
    this.rightToLeftExpressionMap = Maps.newHashMap();
    for (Map.Entry<UniversalTypeExpression, Set<MatchingPair>> matches : diffs.getMatchResult().getMatches().entrySet()) {
      for (MatchingPair pair : matches.getValue()) {
        rightToLeftExpressionMap.put(pair.getRightExpression(), pair.getLeftExpression());
      }
    }

    this.diffWriterResult = new IteraplanDiffWriterResult();
  }

  public IteraplanDiffWriterResult writeDifferences() {
    LOGGER.info("Writing Diffs...");

    createSubstantialTypes(diffs.getRightSidedDiffs());
    createRelationshipTypes(diffs.getRightSidedDiffs());
    addRelationships(diffs.getRightSidedDiffs());
    updateElements(diffs.getTwoSidedDiffs());
    deleteRelationshipTypes(diffs.getLeftSidedDiffs());
    deleteSubstantialTypes(diffs.getLeftSidedDiffs());

    //cleanup has been skipped during diff application, execute it now
    bbServiceLocator.getAllBBService().performCleanup();

    LOGGER.info("All Diffs written.");
    return diffWriterResult;
  }

  private void createSubstantialTypes(Map<UniversalTypeExpression, Set<RightSidedDiff>> diffsToWrite) {
    LOGGER.info("Step 1: create substantial types...");

    Entry<UniversalTypeExpression, Set<RightSidedDiff>> interfacesToCreate = null;
    for (Map.Entry<UniversalTypeExpression, Set<RightSidedDiff>> entry : diffsToWrite.entrySet()) {
      if (entry.getKey() instanceof SubstantialTypeExpression) {
        if (entry.getKey().getPersistentName().equals("InformationSystemInterface")) {
          interfacesToCreate = entry;
        }
        else {
          // InformationSystemInterfaces will be handled with the InformationFlow RTE in step 2
          LOGGER.info(entry.getKey().toString());
          for (RightSidedDiff diff : getSortedDiffs(entry.getValue(), entry.getKey())) {
            createInstanceWithoutRelations(diff);
          }
        }
      }
    }
    if (interfacesToCreate != null) {
      LOGGER.info(interfacesToCreate.getKey().toString());
      for (RightSidedDiff diff : getSortedDiffs(interfacesToCreate.getValue(), interfacesToCreate.getKey())) {
        createInstanceWithRelations(diff);
      }
    }

    LOGGER.info("Step 1 done.");
  }

  private void createRelationshipTypes(Map<UniversalTypeExpression, Set<RightSidedDiff>> diffsToWrite) {
    LOGGER.info("Step 2: create relationship types...");

    for (Map.Entry<UniversalTypeExpression, Set<RightSidedDiff>> entry : diffsToWrite.entrySet()) {
      if (entry.getKey() instanceof RelationshipTypeExpression) {
        LOGGER.info(entry.getKey().toString());
        for (RightSidedDiff diff : getSortedDiffs(entry.getValue(), entry.getKey())) {
          createInstanceWithRelations(diff);
          addAppliedDiff(diff);
        }
      }
    }

    LOGGER.info("Step 2 done.");
  }

  private void addRelationships(Map<UniversalTypeExpression, Set<RightSidedDiff>> diffsToWrite) {
    LOGGER.info("Step 3: add relationships for substantial types...");

    for (Map.Entry<UniversalTypeExpression, Set<RightSidedDiff>> entry : diffsToWrite.entrySet()) {
      if (entry.getKey() instanceof SubstantialTypeExpression) {
        LOGGER.info(entry.getKey().toString());
        for (RightSidedDiff diff : getSortedDiffs(entry.getValue(), entry.getKey())) {
          LOGGER.info("- add relationships for {0}", diff.getExpression());
          setRelations(diff);
          addAppliedDiff(diff);
        }
      }
    }

    LOGGER.info("Step 3 done.");
  }

  private void updateElements(Map<UniversalTypeExpression, Set<TwoSidedDiff>> diffsToWrite) {
    LOGGER.info("Step 4: update universal types...");

    for (Map.Entry<UniversalTypeExpression, Set<TwoSidedDiff>> entry : diffsToWrite.entrySet()) {
      LOGGER.info(entry.getKey().toString());
      for (TwoSidedDiff diff : getSortedDiffs(entry.getValue(), entry.getKey())) {
        LOGGER.info("- apply changes to {0}", diff.getLeftExpression());
        setBuiltinProperties(diff);
        setRelations(diff);
        setAttributeValues(diff);
        saveOrUpdate(diff);
        addAppliedDiff(diff);
      }
    }

    LOGGER.info("Step 4 done.");
  }

  private void deleteRelationshipTypes(Map<UniversalTypeExpression, Set<LeftSidedDiff>> diffsToWrite) {
    LOGGER.info("Step 5: delete relationship types...");

    for (Map.Entry<UniversalTypeExpression, Set<LeftSidedDiff>> entry : diffsToWrite.entrySet()) {
      if (entry.getKey() instanceof RelationshipTypeExpression) {
        LOGGER.info(entry.getKey().toString());
        for (LeftSidedDiff diff : getSortedDiffs(entry.getValue(), entry.getKey())) {
          LOGGER.info("- remove {0}", diff.getExpression());
          deleteInstance(diff);
        }
      }
    }

    LOGGER.info("Step 5 done.");
  }

  private void deleteSubstantialTypes(Map<UniversalTypeExpression, Set<LeftSidedDiff>> diffsToWrite) {
    LOGGER.info("Step 6: delete substantial types...");

    for (Map.Entry<UniversalTypeExpression, Set<LeftSidedDiff>> entry : diffsToWrite.entrySet()) {
      if (entry.getKey() instanceof SubstantialTypeExpression) {
        LOGGER.info(entry.getKey().toString());
        for (LeftSidedDiff diff : getSortedDiffs(entry.getValue(), entry.getKey())) {
          LOGGER.info("- remove {0}", diff.getExpression());
          deleteInstance(diff);
        }
      }
    }

    LOGGER.info("Step 6 done.");
  }

  /**
   * Diffs need to be sorted by hierarchical depth because of ITERAPLAN-1663
   * @param diffSet
   *          Set of {@link BaseDiff}s to be sorted
   * @param type
   *          {@link UniversalTypeExpression} the diffs refer to
   * @return List of diffs sorted by hierarchical depth, see {@link HierarchicalDiffComparator}
   */
  private <T extends BaseDiff> List<T> getSortedDiffs(Set<T> diffSet, UniversalTypeExpression type) {
    List<T> sortedDiffs = Lists.newArrayList(diffSet);
    Collections.sort(sortedDiffs, new HierarchicalDiffComparator(type));
    return sortedDiffs;
  }

  private void createInstanceWithoutRelations(RightSidedDiff diff) {
    LOGGER.info("- add {0}", diff.getExpression());
    createInstance(diff);
    setBuiltinProperties(diff);
    saveOrUpdate(diff);
    setAttributeValues(diff);
  }

  private void createInstanceWithRelations(RightSidedDiff diff) {
    LOGGER.info("- add {0}", diff.getExpression());
    createInstance(diff);
    setRelations(diff);
    setBuiltinProperties(diff);
    saveOrUpdate(diff);
    setAttributeValues(diff);
  }

  private void createInstance(RightSidedDiff diff) {
    IteraplanChangeOperation createInstanceOp = new CreateInstanceOp(mapping, instanceMapping, bbServiceLocator);
    handleOp(createInstanceOp, diff);
  }

  private void setRelations(BaseDiff diff) {
    IteraplanChangeOperation setRelationsOp = new SetRelationshipOp(mapping, instanceMapping, bbServiceLocator, rightToLeftExpressionMap);
    handleOp(setRelationsOp, diff);
  }

  private void setBuiltinProperties(BaseDiff diff) {
    IteraplanChangeOperation setBuiltinPropsOp = new SetBuiltinPropertiesOp(mapping, instanceMapping, bbServiceLocator.getIsiService());
    handleOp(setBuiltinPropsOp, diff);
  }

  private void saveOrUpdate(BaseDiff diff) {
    IteraplanChangeOperation saveOrUpdateOp = new SaveOrUpdateOperation(mapping, instanceMapping, bbServiceLocator);
    handleOp(saveOrUpdateOp, diff);
  }

  private void setAttributeValues(BaseDiff diff) {
    IteraplanChangeOperation saveAttributeValuesOp = new SetAttributeValueOp(mapping, instanceMapping, avService);
    handleOp(saveAttributeValuesOp, diff);
  }

  private void deleteInstance(LeftSidedDiff diff) {
    IteraplanChangeOperation deleteInstanceOp = new DeleteInstanceOp(mapping, instanceMapping, bbServiceLocator);
    handleOp(deleteInstanceOp, diff);
  }

  private void handleOp(IteraplanChangeOperation operation, BaseDiff diff) {
    if (!diffsToIgnore.contains(diff)) {
      operation.execute(diff);
      ResultMessages result = operation.getResult();
      diffWriterResult.addMessages(result);
      if (result.getErrorLevel() == ErrorLevel.ERROR) {
        diffsToIgnore.add(diff);
      }
    }
  }

  private void addAppliedDiff(BaseDiff diff) {
    if (!diffsToIgnore.contains(diff)) {
      diffWriterResult.addAppliedDiff(diff);
    }
  }

  /**
   * Sorts by hierarchical depth. Deepest elements first.
   */
  private static class HierarchicalDiffComparator implements Comparator<BaseDiff> {

    private final RelationshipEndExpression parentREE;

    /**
     * Default constructor.
     * @param type
     */
    public HierarchicalDiffComparator(UniversalTypeExpression type) {
      parentREE = type.findRelationshipEndByPersistentName(MixinTypeHierarchic.PARENT.getPersistentName());
    }

    /**{@inheritDoc}**/
    public int compare(BaseDiff o1, BaseDiff o2) {
      UniversalModelExpression exp1 = null, exp2 = null;
      if (o1 instanceof RightSidedDiff) {
        exp1 = ((RightSidedDiff) o1).getExpression();
      }
      else if (o1 instanceof LeftSidedDiff) {
        exp1 = ((LeftSidedDiff) o1).getExpression();
      }
      else if (o1 instanceof TwoSidedDiff) {
        exp1 = ((TwoSidedDiff) o1).getRightExpression();
      }

      if (o2 instanceof RightSidedDiff) {
        exp2 = ((RightSidedDiff) o2).getExpression();
      }
      else if (o2 instanceof LeftSidedDiff) {
        exp2 = ((LeftSidedDiff) o2).getExpression();
      }
      else if (o2 instanceof TwoSidedDiff) {
        exp2 = ((TwoSidedDiff) o2).getRightExpression();
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
    private int calculateHierarchicalDepth(UniversalModelExpression expression) {
      if (expression == null || parentREE == null) {
        return 0;
      }

      UniversalModelExpression parent = expression.getConnected(parentREE);
      int depth = 0;
      while (parent != null) {
        depth++;
        parent = parent.getConnected(parentREE);
      }
      return depth;
    }
  }

  public static class IteraplanDiffWriterResult extends ResultMessages implements DiffWriterResult {
    private static final long    serialVersionUID = 1298398379332503630L;

    private final List<BaseDiff> appliedDiffs;

    public IteraplanDiffWriterResult() {
      super();
      this.appliedDiffs = Lists.newArrayList();
    }

    /**
     * Adds a {@link BaseDiff} to the result indicating it was successfully applied
     * @param diff
     */
    public void addAppliedDiff(BaseDiff diff) {
      this.appliedDiffs.add(diff);
    }

    public List<BaseDiff> getAppliedDiffs() {
      return appliedDiffs;
    }
  }

}
