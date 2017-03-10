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
package de.iteratec.iteraplan.elasticeam.model.merge;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilderResult;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffPart;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffWriter;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffWriterResult;
import de.iteratec.iteraplan.elasticeam.model.compare.LeftSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchingPair;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;


public class ModelMerger implements DiffWriter {

  private static final Logger                                     LOGGER          = Logger.getIteraplanLogger(ModelMerger.class);

  private final Model                                             targetModel;
  private final Metamodel                                         targetMetamodel;
  private final DiffBuilderResult                                 diffs;
  private MergeResult                                             diffWriterResult;

  private Map<UniversalModelExpression, UniversalModelExpression> diffToTargetMap = Maps.newHashMap();

  public ModelMerger(Model targetModel, Metamodel targetMetamodel, DiffBuilderResult diffs) {
    this.targetModel = targetModel;
    this.targetMetamodel = targetMetamodel;
    this.diffs = diffs;
    for (Map.Entry<UniversalTypeExpression, Set<MatchingPair>> entry : diffs.getMatchResult().getMatches().entrySet()) {
      for (MatchingPair pair : entry.getValue()) {
        diffToTargetMap.put(pair.getRightExpression(), pair.getLeftExpression());
      }
    }
    this.diffWriterResult = new MergeResult();
  }

  public DiffWriterResult writeDifferences() {
    LOGGER.info("Writing Diffs...");

    createSubstantialTypes(diffs.getRightSidedDiffs());
    createRelationshipTypes(diffs.getRightSidedDiffs());
    addRelationships(diffs.getRightSidedDiffs());
    updateElements(diffs.getTwoSidedDiffs());
    deleteRelationshipTypes(diffs.getLeftSidedDiffs());
    deleteSubstantialTypes(diffs.getLeftSidedDiffs());

    LOGGER.info("All Diffs written.");
    return diffWriterResult;
  }

  public Model getMergedModel() {
    return targetModel;
  }

  private void createSubstantialTypes(Map<UniversalTypeExpression, Set<RightSidedDiff>> rightSidedDiffs) {
    LOGGER.info("Step 1: create substantial types...");

    for (Map.Entry<UniversalTypeExpression, Set<RightSidedDiff>> entry : rightSidedDiffs.entrySet()) {
      if (entry.getKey() instanceof SubstantialTypeExpression) {
        LOGGER.info(entry.getKey().toString());
        for (RightSidedDiff diff : entry.getValue()) {
          UniversalModelExpression sourceExpression = diff.getExpression();

          LOGGER.info(" - adding {0}..." + sourceExpression);

          InstanceExpression targetInstance = targetModel.create((SubstantialTypeExpression) entry.getKey());
          for (PropertyExpression<?> property : entry.getKey().getProperties()) {
            setPropertyValue(targetInstance, property, sourceExpression.getValue(property));
          }
          diffToTargetMap.put(sourceExpression, targetInstance);
        }
      }
    }
  }

  private void createRelationshipTypes(Map<UniversalTypeExpression, Set<RightSidedDiff>> rightSidedDiffs) {
    LOGGER.info("Step 2: create relationship types...");

    for (Map.Entry<UniversalTypeExpression, Set<RightSidedDiff>> entry : rightSidedDiffs.entrySet()) {
      if (entry.getKey() instanceof RelationshipTypeExpression) {
        LOGGER.info(entry.getKey().toString());
        for (RightSidedDiff diff : entry.getValue()) {
          UniversalModelExpression sourceExpression = diff.getExpression();

          LOGGER.info(" - adding {0}..." + sourceExpression);

          LinkExpression targetLink = targetModel.create((RelationshipTypeExpression) entry.getKey());
          for (RelationshipEndExpression relEnd : entry.getKey().getRelationshipEnds()) {
            setRelationshipEnds(targetLink, relEnd, sourceExpression.getConnecteds(relEnd));
          }
          for (PropertyExpression<?> property : entry.getKey().getProperties()) {
            setPropertyValue(targetLink, property, sourceExpression.getValue(property));
          }
          diffToTargetMap.put(sourceExpression, targetLink);
          diffWriterResult.addApplied(diff);
        }
      }
    }
  }

  private void addRelationships(Map<UniversalTypeExpression, Set<RightSidedDiff>> rightSidedDiffs) {
    LOGGER.info("Step 3: add relationships for substantial types...");

    for (Map.Entry<UniversalTypeExpression, Set<RightSidedDiff>> entry : rightSidedDiffs.entrySet()) {
      if (entry.getKey() instanceof SubstantialTypeExpression) {
        LOGGER.info(entry.getKey().toString());
        for (RightSidedDiff diff : entry.getValue()) {
          LOGGER.info(" - adding relationships for {0}..." + diff.getExpression());

          for (RelationshipEndExpression relEnd : entry.getKey().getRelationshipEnds()) {
            if (relEnd.getType() instanceof SubstantialTypeExpression) {
              // Relationships to LinkInstances already handled from the other side in step 2
              setRelationshipEnds(diffToTargetMap.get(diff.getExpression()), relEnd, diff.getExpression().getConnecteds(relEnd));
            }
          }
          diffWriterResult.addApplied(diff);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void updateElements(Map<UniversalTypeExpression, Set<TwoSidedDiff>> twoSidedDiffs) {
    LOGGER.info("Step 4: update universal types...");

    for (Map.Entry<UniversalTypeExpression, Set<TwoSidedDiff>> entry : twoSidedDiffs.entrySet()) {
      LOGGER.info(entry.getKey().toString());
      for (TwoSidedDiff diff : entry.getValue()) {
        UniversalModelExpression targetExpression = diff.getLeftExpression();

        LOGGER.info(" - updating {0}..." + targetExpression);

        for (DiffPart diffPart : diff.getDiffParts()) {
          if (diffPart.getFeature() instanceof PropertyExpression) {
            PropertyExpression<?> property = (PropertyExpression<?>) diffPart.getFeature();
            setPropertyValue(targetExpression, property, diffPart.getRightValue());
          }
          else if (diffPart.getFeature() instanceof RelationshipEndExpression) {
            RelationshipEndExpression relEnd = (RelationshipEndExpression) diffPart.getFeature();
            if (relEnd.getUpperBound() == 1) {
              setRelationshipEnd(targetExpression, relEnd, (UniversalModelExpression) diffPart.getRightValue());
            }
            else {
              setRelationshipEnds(targetExpression, relEnd, (Collection<UniversalModelExpression>) diffPart.getRightValue());
            }
          }
        }
        diffWriterResult.addApplied(diff);
      }
    }
  }

  private void deleteRelationshipTypes(Map<UniversalTypeExpression, Set<LeftSidedDiff>> leftSidedDiffs) {
    LOGGER.info("Step 5: delete relationship types...");

    for (Map.Entry<UniversalTypeExpression, Set<LeftSidedDiff>> entry : leftSidedDiffs.entrySet()) {
      if (entry.getKey() instanceof RelationshipTypeExpression) {
        LOGGER.info(entry.getKey().toString());
        for (LeftSidedDiff diff : entry.getValue()) {
          LOGGER.info(" - deleting {0}..." + diff.getExpression());

          if (targetModel.canDelete(entry.getKey())) {
            targetModel.delete((LinkExpression) diff.getExpression());
          }
          diffWriterResult.addApplied(diff);
        }
      }
    }
  }

  private void deleteSubstantialTypes(Map<UniversalTypeExpression, Set<LeftSidedDiff>> leftSidedDiffs) {
    LOGGER.info("Step 6: delete substantial types...");

    for (Map.Entry<UniversalTypeExpression, Set<LeftSidedDiff>> entry : leftSidedDiffs.entrySet()) {
      if (entry.getKey() instanceof SubstantialTypeExpression) {
        LOGGER.info(entry.getKey().toString());
        for (LeftSidedDiff diff : entry.getValue()) {
          LOGGER.info(" - deleting {0}..." + diff.getExpression());

          if (targetModel.canDelete(entry.getKey())) {
            targetModel.delete((InstanceExpression) diff.getExpression());
          }
          diffWriterResult.addApplied(diff);
        }
      }
    }
  }

  private void setPropertyValue(UniversalModelExpression targetExpression, PropertyExpression<?> property, Object value) {
    LOGGER.info("Setting value for \"{0}\" of \"{1}\" to \"{2}\".", property, targetExpression, value);

    PropertyExpression<?> targetProperty = targetMetamodel.findUniversalTypeByPersistentName(property.getHolder().getPersistentName())
        .findPropertyByPersistentName(property.getPersistentName());

    Object newValue = convertPropertyValue(targetProperty, value);
    targetExpression.setValue(targetProperty, newValue);
  }

  @SuppressWarnings("unchecked")
  private Object convertPropertyValue(PropertyExpression<?> property, Object value) {
    if (value == null) {
      return property.getUpperBound() == 1 ? null : new LinkedList<Object>();
    }

    if (property instanceof EnumerationPropertyExpression) {
      EnumerationExpression sourceEnumType = (EnumerationExpression) property.getType();
      EnumerationExpression targetEnumType = (EnumerationExpression) targetMetamodel.findDataTypeByPersistentName(sourceEnumType.getPersistentName());

      if (property.getUpperBound() == 1) {
        return targetEnumType.findLiteralByPersistentName(((EnumerationLiteralExpression) value).getPersistentName());
      }
      else if (value instanceof Collection) {
        Collection<EnumerationLiteralExpression> resultValues = Sets.newHashSet();
        Collection<EnumerationLiteralExpression> sourceValues = (Collection<EnumerationLiteralExpression>) value;
        for (EnumerationLiteralExpression sourceValue : sourceValues) {
          resultValues.add(targetEnumType.findLiteralByPersistentName(sourceValue.getPersistentName()));
        }
        return resultValues;
      }
    }

    return value;
  }

  private void setRelationshipEnd(UniversalModelExpression targetExpression, RelationshipEndExpression relEnd,
                                  UniversalModelExpression sourceConnected) {
    LOGGER.info("Setting {0} of \"{1}\" to \"{2}\"...", relEnd.getPersistentName(), targetExpression, sourceConnected);

    RelationshipEndExpression targetRelEnd = targetMetamodel.findUniversalTypeByPersistentName(relEnd.getHolder().getPersistentName())
        .findRelationshipEndByPersistentName(relEnd.getPersistentName());

    UniversalModelExpression mappedSourceConnected = sourceConnected;
    if (diffToTargetMap.containsKey(sourceConnected)) {
      mappedSourceConnected = diffToTargetMap.get(sourceConnected);
    }
    targetExpression.connect(targetRelEnd, mappedSourceConnected);
  }

  private void setRelationshipEnds(UniversalModelExpression targetExpression, RelationshipEndExpression relEnd,
                                   Collection<UniversalModelExpression> sourceConnecteds) {
    LOGGER.info("Setting {0} of \"{1}\" to \"{2}\"...", relEnd.getPersistentName(), targetExpression, sourceConnecteds);

    RelationshipEndExpression targetRelEnd = targetMetamodel.findUniversalTypeByPersistentName(relEnd.getHolder().getPersistentName())
        .findRelationshipEndByPersistentName(relEnd.getPersistentName());

    Collection<UniversalModelExpression> targetConnecteds = targetExpression.getConnecteds(targetRelEnd);
    Collection<UniversalModelExpression> mappedSourceConnecteds = Sets.newHashSet();
    for (UniversalModelExpression connected : sourceConnecteds) {
      UniversalModelExpression mapped = connected;
      if (diffToTargetMap.containsKey(connected)) {
        mapped = diffToTargetMap.get(connected);
      }
      mappedSourceConnecteds.add(mapped);
    }
    targetExpression.disconnect(targetRelEnd, targetConnecteds);
    targetExpression.connect(targetRelEnd, mappedSourceConnecteds);
  }

  private static class MergeResult implements DiffWriterResult {

    private List<String>   errorMessages = Lists.newArrayList();
    private List<BaseDiff> appliedDiffs  = Lists.newArrayList();

    public List<String> getErrors() {
      return errorMessages;
    }

    public void addError(String errorMessage) {
      this.errorMessages.add(errorMessage);
    }

    public List<BaseDiff> getAppliedDiffs() {
      return appliedDiffs;
    }

    public void addApplied(BaseDiff appliedDiff) {
      this.appliedDiffs.add(appliedDiff);
    }

  }

}
