/*
 * Copyright 2011 Christian M. Schweda & iteratec
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.recommendation;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.VBBUtil;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.MixedOrPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;


/**
 * The recommender determines possible bindings for the variables specified in an abstract viewmodel.
 * Partial bindings are considered by the recommender to determine only completing, non-contradicting
 * recommendations.
 */
public class Recommender {

  private Metamodel                                                   abstractViewmodel;
  private Metamodel                                                   informationModel;

  private static ListMultimap<DataTypeExpression, DataTypeExpression> admissibleSubtypes;

  static {
    admissibleSubtypes = LinkedListMultimap.create();
    admissibleSubtypes.put(BuiltinPrimitiveType.STRING, BuiltinPrimitiveType.DECIMAL);
    admissibleSubtypes.put(BuiltinPrimitiveType.STRING, BuiltinPrimitiveType.INTEGER);
  }

  /**
   * Default constructor.
   * @param abstractViewmodel the abstract viewmodel, whose variables should be bound.
   * @param informationModel the information model, whose elements should be used to bind the variables.
   */
  public Recommender(Metamodel abstractViewmodel, Metamodel informationModel) {
    this.abstractViewmodel = abstractViewmodel;
    this.informationModel = informationModel;
  }

  private static void addRecommendation(ViewpointConfigurationRecommendation recommendation, NamedExpression avmElement, NamedExpression candidate) {
    //TODO Compute priority here
    int priority = 0;
    if (candidate != null && candidate.getPersistentName() != null) {
      String pName = candidate.getPersistentName();

      for (int i = 0; i < pName.length(); i++) {
        if (pName.charAt(i) == '(' || pName.charAt(i) == '/' || pName.charAt(i) == '[') {
          priority--;
        }
      }
    }
    recommendation.addRecommendation(avmElement, candidate, priority);
  }

  private static void addRecommendations(ViewpointConfigurationRecommendation recommendation, ViewpointConfiguration configuration) {
    for (Entry<NamedExpression, NamedExpression> avmElement : configuration.entrySet()) {
      addRecommendation(recommendation, avmElement.getKey(), avmElement.getValue());
    }
  }

  private static boolean isAdmissibleSubtype(DataTypeExpression avmDataType, DataTypeExpression candidateDataType) {
    return avmDataType.equals(candidateDataType) || admissibleSubtypes.containsEntry(avmDataType, candidateDataType);
  }

  /**
   * Checks for exact matching bounds of a candidate- and a avmClass-EStructuralFeature.
   * @param avmClassFeature
   * @param candidate
   * @return true, when the structural has covariantly matching bounds
   */
  private static boolean hasMatchingBounds(FeatureExpression<?> avmClassFeature, FeatureExpression<?> candidate) {
    if (avmClassFeature.getLowerBound() > candidate.getLowerBound()) {
      return false;
    }
    if (avmClassFeature.getUpperBound() != FeatureExpression.UNLIMITED && avmClassFeature.getUpperBound() < candidate.getUpperBound()) {
      return false;
    }
    return true;
  }

  private static boolean isMatchingAttribute(PropertyExpression<? extends DataTypeExpression> avmAttribute,
                                             PropertyExpression<? extends DataTypeExpression> candidateAttribute) {

    if (!hasMatchingBounds(avmAttribute, candidateAttribute)) {
      return false;
    }
    if (avmAttribute instanceof MixedOrPropertyExpression) {
      MixedOrPropertyExpression mixedOrProperty = (MixedOrPropertyExpression) avmAttribute;
      if (mixedOrProperty.isMatchEnumerations() && candidateAttribute instanceof EnumerationPropertyExpression) {
        return true;
      }
      for (DataTypeExpression admissibleType : mixedOrProperty.getTypes()) {
        if (isAdmissibleSubtype(admissibleType, candidateAttribute.getType())) {
          return true;
        }
      }
      return false;
    }
    else if (avmAttribute instanceof EnumerationPropertyExpression && avmAttribute.getType() == null) {
      return candidateAttribute instanceof EnumerationPropertyExpression;
    }
    else {
      return isAdmissibleSubtype(avmAttribute.getType(), candidateAttribute.getType());
    }
  }

  private static boolean isMatchingNonSelfReference(RelationshipEndExpression avmReference, ViewpointConfiguration config) {
    RelationshipEndExpression candidateReference = (RelationshipEndExpression) config.getMappingFor(avmReference);

    boolean matching = hasMatchingBounds(avmReference, candidateReference);
    matching &= !candidateReference.getType().equals(candidateReference.getOrigin());

    if (config.getMappingFor(avmReference.getType()) != null) {
      matching &= config.getMappingFor(avmReference.getType()).equals(candidateReference.getType());
      matching &= config.getMappingFor(avmReference.getHolder()).equals(candidateReference.getHolder());
    }

    return matching;
  }

  private static List<PropertyExpression<? extends DataTypeExpression>> findMatchingAttributes(PropertyExpression<? extends DataTypeExpression> avmAttribute,
                                                                                               UniversalTypeExpression candidateClass) {
    List<PropertyExpression<? extends DataTypeExpression>> attributes = new LinkedList<PropertyExpression<? extends DataTypeExpression>>();

    for (PropertyExpression<? extends DataTypeExpression> candidateAttribute : candidateClass.getProperties()) {
      if (isMatchingAttribute(avmAttribute, candidateAttribute)) {
        attributes.add(candidateAttribute);
      }
    }

    return attributes;
  }

  private static List<RelationshipEndExpression> findMatchingSelfReferences(RelationshipEndExpression avmSelfReference,
                                                                            UniversalTypeExpression candidateClass) {
    List<RelationshipEndExpression> selfReferences = new LinkedList<RelationshipEndExpression>();

    for (RelationshipEndExpression candidateReference : candidateClass.getRelationshipEnds()) {
      if (candidateReference.getType().equals(candidateClass) && hasMatchingBounds(avmSelfReference, candidateReference)) {
        selfReferences.add(candidateReference);
      }
    }

    return selfReferences;
  }

  private static List<RelationshipEndExpression> findMatchingNonSelfReferences(RelationshipEndExpression avmReference,
                                                                               UniversalTypeExpression candidateClass, ViewpointConfiguration config) {
    List<RelationshipEndExpression> nonSelfReferences = new LinkedList<RelationshipEndExpression>();

    for (RelationshipEndExpression candidateReference : candidateClass.getRelationshipEnds()) {
      ViewpointConfiguration innerConfig = new ViewpointConfiguration(config);
      innerConfig.setMappingFor(avmReference, candidateReference);

      if (isMatchingNonSelfReference(avmReference, innerConfig)) {
        nonSelfReferences.add(candidateReference);
      }
    }

    return nonSelfReferences;
  }

  private List<UniversalTypeExpression> findCandidateClasses(UniversalTypeExpression avmClass) {
    List<UniversalTypeExpression> candidates = new LinkedList<UniversalTypeExpression>();

    // Iterate over all information model classes
    for (UniversalTypeExpression candidateClass : this.informationModel.getUniversalTypes()) {

      boolean isCandidate = !(avmClass instanceof SubstantialTypeExpression) || candidateClass instanceof SubstantialTypeExpression;

      // Check whether at least one possible mapping exists for each mandatory AVM attribute in the current candidate
      for (PropertyExpression<? extends DataTypeExpression> avmAttribute : avmClass.getProperties()) {
        if (!VBBUtil.isOptional(avmAttribute)) {
          isCandidate &= (findMatchingAttributes(avmAttribute, candidateClass).size() > 0);
        }
      }

      // Check whether at least one possible mapping exists for each mandatory AVM self-reference in the current candidate
      for (RelationshipEndExpression avmReference : avmClass.getRelationshipEnds()) {
        // is self-reference and not optional
        if (avmReference.getType().equals(avmClass) && !VBBUtil.isOptional(avmReference)) {
          isCandidate &= (findMatchingSelfReferences(avmReference, candidateClass).size() > 0);
        }
      }

      if (isCandidate) {
        candidates.add(candidateClass);
      }
    }

    return candidates;
  }

  private Deque<RelationshipEndExpression> collectNonSelfReferences() {
    LinkedList<RelationshipEndExpression> nonSelfReferences = new LinkedList<RelationshipEndExpression>();

    // Collect non-self references
    for (UniversalTypeExpression avmClass : this.abstractViewmodel.getUniversalTypes()) {
      for (RelationshipEndExpression avmReference : avmClass.getRelationshipEnds()) {
        if (!avmReference.getType().equals(avmClass)) {
          nonSelfReferences.add(avmReference);
        }
      }
    }

    return nonSelfReferences;
  }

  private void collectAttributesAndSelfReferences(ViewpointConfigurationRecommendation recommendation, ViewpointConfiguration externalConfig,
                                                  UniversalTypeExpression avmClass) {
    UniversalTypeExpression mappedClass = (UniversalTypeExpression) externalConfig.getMappingFor(avmClass);

    // Add recommendations for the missing attributes
    for (PropertyExpression<? extends DataTypeExpression> avmAttribute : avmClass.getProperties()) {
      //recommendations for optional properties are given even if the avmAttribute is already mapped
      if (!externalConfig.hasValueFor(avmAttribute) || avmAttribute.getLowerBound() == 0) {
        for (PropertyExpression<? extends DataTypeExpression> candidateAtt : findMatchingAttributes(avmAttribute, mappedClass)) {
          addRecommendation(recommendation, avmAttribute, candidateAtt);
        }
      }
    }

    // Add recommendations for the missing self-references
    for (RelationshipEndExpression avmReference : avmClass.getRelationshipEnds()) {
      //recommendations for optional self-relationships are given even if the avmReference is already mapped
      if ((!externalConfig.hasValueFor(avmReference) || avmReference.getLowerBound() == 0) && avmReference.getType().equals(avmClass)) {
        for (RelationshipEndExpression candidateRef : findMatchingSelfReferences(avmReference, mappedClass)) {
          addRecommendation(recommendation, avmReference, candidateRef);
        }
      }
    }
  }

  private void collectRecommendations(ViewpointConfigurationRecommendation recommendation, Deque<UniversalTypeExpression> remainingAvmClasses,
                                      ViewpointConfiguration externalConfig, ViewpointConfiguration candidateConfig,
                                      Map<UniversalTypeExpression, List<UniversalTypeExpression>> candidates) {
    if (remainingAvmClasses.isEmpty()) {
      // If all avmClasses + attributes + self-references already have mappings in the provided configuration, 
      // thus, the list of classes to process is empty, collect recommendations for the references between those 
      // classes
      collectRemainingReferences(recommendation, collectNonSelfReferences(), externalConfig, candidateConfig);
    }
    else {
      UniversalTypeExpression avmClass = remainingAvmClasses.removeFirst();
      if (externalConfig.hasValueFor(avmClass)) {
        // If the externally provided configuration already contains a mapping for the current avmClass, 
        // add recommendations for the missing attributes and self-references
        collectAttributesAndSelfReferences(recommendation, externalConfig, avmClass);
        collectRecommendations(recommendation, new LinkedList<UniversalTypeExpression>(remainingAvmClasses), externalConfig, candidateConfig,
            candidates);
      }
      else {
        // Collect recommendations for the provided mapping extended by a mapping of each candidate to the 
        // current avmClass
        for (UniversalTypeExpression candidateClass : candidates.get(avmClass)) {
          // Extend the provided configuration with the current candidate-class/avm-class mapping and collect 
          // recommendations for this scenario
          ViewpointConfiguration innerCandidateConfig = new ViewpointConfiguration(candidateConfig);
          innerCandidateConfig.setMappingFor(avmClass, candidateClass);

          collectRecommendations(recommendation, new LinkedList<UniversalTypeExpression>(remainingAvmClasses), externalConfig, innerCandidateConfig,
              candidates);
        }
      }
    }
  }

  private void collectRemainingReferences(ViewpointConfigurationRecommendation recommendation, Deque<RelationshipEndExpression> nonSelfReferences,
                                          ViewpointConfiguration externalConfig, ViewpointConfiguration candidateConfig) {
    if (nonSelfReferences.isEmpty()) {
      // If all non-self references already have mappings in the provided configuration, thus, all mappings are 
      // provided, add this configuration to the recommendations.
      addRecommendations(recommendation, candidateConfig);
    }
    else {
      RelationshipEndExpression avmReference = nonSelfReferences.removeFirst();
      //recommendations for optional relationships are given even if the avmReference is already mapped
      if (!externalConfig.hasValueFor(avmReference) || avmReference.getLowerBound() == 0) {
        UniversalTypeExpression candidateClass = (UniversalTypeExpression) candidateConfig.getMappingFor(avmReference.getHolder());
        for (RelationshipEndExpression candidateReference : findMatchingNonSelfReferences(avmReference, candidateClass, candidateConfig)) {
          // Extend the provided configuration with the current candidate-reference/avm-reference mapping and 
          // collect recommendations for this scenario
          ViewpointConfiguration innerCandidateConfig = new ViewpointConfiguration(candidateConfig);
          innerCandidateConfig.setMappingFor(avmReference, candidateReference);

          collectRemainingReferences(recommendation, nonSelfReferences, externalConfig, innerCandidateConfig);
        }
      }
      else {
        collectRemainingReferences(recommendation, nonSelfReferences, externalConfig, candidateConfig);
      }
    }
  }

  /**
   * Determines all recommended configurations that match the supplied partial configuration
   * @param config the partial configuration to match
   * @return the corresponding recommendations
   */
  public ViewpointConfigurationRecommendation recommend(ViewpointConfiguration config) {
    Map<UniversalTypeExpression, List<UniversalTypeExpression>> candidates = new HashMap<UniversalTypeExpression, List<UniversalTypeExpression>>();
    for (UniversalTypeExpression avmClass : this.abstractViewmodel.getUniversalTypes()) {
      candidates.put(avmClass, findCandidateClasses(avmClass));
    }

    // Create a recommendation based on the current configuration
    ViewpointConfigurationRecommendation recommendation = new ViewpointConfigurationRecommendation(config);
    collectRecommendations(recommendation, new LinkedList<UniversalTypeExpression>(candidates.keySet()), config, config, candidates);

    return recommendation;
  }
}
