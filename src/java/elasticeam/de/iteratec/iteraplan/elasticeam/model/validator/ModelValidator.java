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
package de.iteratec.iteraplan.elasticeam.model.validator;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.StringInputValidator;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 * Validates a model against a given metamodel.
 */
public class ModelValidator {

  private final Metamodel metamodel;

  private boolean         ignoreNullId;
  private boolean         ignoreLowerCardinalityBound;
  private boolean         ignoreIdUniqueness;
  private boolean         checkInterfaceConsistency;

  public ModelValidator(Metamodel metamodel) {
    this.metamodel = metamodel;
  }

  /**
   * Returns whether IDs must have a value for the model to pass the validator, or not.
   * @return true if IDs are allowed to not have a value, false if not.
   */
  public boolean isIgnoreNullId() {
    return ignoreNullId;
  }

  /**
   * Sets whether IDs must have a value for the model to pass the validator, or not.
   * @param ignoreNullId
   *          true if IDs are allowed to not have a value, false if not.
   */
  public void setIgnoreNullId(boolean ignoreNullId) {
    this.ignoreNullId = ignoreNullId;
  }

  /**
   * Returns whether IDs must be unique within the model to pass the validator, or not.
   * @return true if IDs don't need to be unique, false if not.
   */
  public boolean isIgnoreIdUniqueness() {
    return ignoreIdUniqueness;
  }

  /**
   * Sets whether IDs must be unique within the model to pass the validator, or not.
   * @param ignoreIdUniqueness
   *          true if IDs don't need to be unique, false if not.
   */
  public void setIgnoreIdUniqueness(boolean ignoreIdUniqueness) {
    this.ignoreIdUniqueness = ignoreIdUniqueness;
  }

  /**
   * Returns whether lower cardinality bounds will be checked or not.
   * @return true if lower cardinality bounds will be ignored.
   */
  public boolean isIgnoreLowerCardinalityBound() {
    return ignoreLowerCardinalityBound;
  }

  /**
   * Sets whether lower cardinality bounds will be checked or not.
   * @param ignoreLowerCardinalityBound
   *          true if lower cardinality bounds will be ignored.
   */
  public void setIgnoreLowerCardinalityBound(boolean ignoreLowerCardinalityBound) {
    this.ignoreLowerCardinalityBound = ignoreLowerCardinalityBound;
  }

  /**
   * Returns whether the interfaces will be checked on consistency (regarding the connected information flows)
   * @return true, if the interfaces will be checked, false else
   */
  public boolean isCheckInterfaceConsistency() {
    return checkInterfaceConsistency;
  }

  /**
   * Sets whether the validator checks for inconsistent information flows connected to an interface
   * @param checkInterfaceConsistency
   */
  public void setCheckInterfaceConsistency(boolean checkInterfaceConsistency) {
    this.checkInterfaceConsistency = checkInterfaceConsistency;
  }

  /**
   * Validates the given model against the metamodel with which the validator was initialized.
   * @param model
   *    The model to validate.
   * @return
   *    The result of the validation, containing instances of {@link ModelConsistencyViolation}
   *    which represent all inconsistencies found..
   */
  public ModelValidatorResult validate(Model model) {
    ModelValidatorResult result = new ModelValidatorResultImpl(metamodel, model);

    validateCardinalityConstraints(model, result);

    if (!ignoreIdUniqueness) {
      validateIdUniqueness(model, result);
    }
    validateInstanceNameUniqueness(model, result);

    vaidateNoCyclesOverAcyclicRelationships(model, result);

    validateUniversalTypeExpressionNamesConstraints(model, result);

    return result;
  }

  protected Metamodel getMetamodel() {
    return this.metamodel;
  }

  private void validateCardinalityConstraints(Model model, ModelValidatorResult result) {
    for (UniversalTypeExpression type : metamodel.getUniversalTypes()) {
      for (UniversalModelExpression expression : model.findAll(type)) {
        for (PropertyExpression<?> property : type.getProperties()) {
          int numberOfValues = expression.getValues(property).size();
          if (isPropertyCardinalityViolation(property, numberOfValues)) {
            result.addViolation(new CardinalityConstraintViolation(property, expression, numberOfValues));
          }
        }
        for (RelationshipEndExpression relEnd : type.getRelationshipEnds()) {
          int numberOfValues = expression.getConnecteds(relEnd).size();
          if (isRelationshipCardinalityViolation(relEnd, numberOfValues)) {
            result.addViolation(new CardinalityConstraintViolation(relEnd, expression, numberOfValues));
          }
        }
      }
    }
  }

  private boolean isPropertyCardinalityViolation(PropertyExpression<?> property, int numberOfValues) {
    boolean wrongNumberOfValues = numberOfValues > property.getUpperBound();
    if (!ignoreLowerCardinalityBound) {
      wrongNumberOfValues |= numberOfValues < property.getLowerBound();
    }
    boolean emptyIdSpecialCase = ignoreNullId && MixinTypeNamed.ID_PROPERTY.equals(property) && numberOfValues == 0;
    return wrongNumberOfValues && !emptyIdSpecialCase;
  }

  private boolean isRelationshipCardinalityViolation(RelationshipEndExpression relEnd, int numberOfValues) {
    boolean wrongNumberOfValues = numberOfValues > relEnd.getUpperBound();
    if (!ignoreLowerCardinalityBound) {
      wrongNumberOfValues |= numberOfValues < relEnd.getLowerBound();
    }
    return wrongNumberOfValues;
  }

  private void validateIdUniqueness(Model model, ModelValidatorResult result) {
    Map<BigInteger, Set<UniversalModelExpression>> idToUme = Maps.newHashMap();
    PropertyExpression<?> idProperty = UniversalTypeExpression.ID_PROPERTY;

    for (UniversalTypeExpression type : metamodel.getUniversalTypes()) {
      for (UniversalModelExpression expression : model.findAll(type)) {
        BigInteger id = (BigInteger) expression.getValue(idProperty);
        if (id != null) {
          if (idToUme.get(id) == null) {
            idToUme.put(id, new HashSet<UniversalModelExpression>());
          }
          idToUme.get(id).add(expression);
        }
      }
    }

    for (Entry<BigInteger, Set<UniversalModelExpression>> entry : idToUme.entrySet()) {
      if (entry.getValue().size() > 1) {
        result.addViolation(new UniquenessConstraintViolation(idProperty, entry.getValue(), entry.getKey()));
      }
    }
  }

  private void validateInstanceNameUniqueness(Model model, ModelValidatorResult result) {
    for (SubstantialTypeExpression type : metamodel.getSubstantialTypes()) {
      Map<String, Set<UniversalModelExpression>> nameToUme = Maps.newHashMap();
      PropertyExpression<?> nameProperty = type.findPropertyByPersistentName(MixinTypeNamed.NAME_PROPERTY.getPersistentName());

      for (UniversalModelExpression expression : model.findAll(type)) {
        String name = (String) expression.getValue(nameProperty);
        if (name != null) {
          name = name.toLowerCase(); // make sure to validate name property uniqueness case-insensitive
          if (nameToUme.get(name) == null) {
            nameToUme.put(name, new HashSet<UniversalModelExpression>());
          }
          nameToUme.get(name).add(expression);
        }
      }

      for (Entry<String, Set<UniversalModelExpression>> entry : nameToUme.entrySet()) {
        if (entry.getValue().size() > 1) {
          result.addViolation(new UniquenessConstraintViolation(nameProperty, entry.getValue(), entry.getKey()));
        }
      }
    }
  }

  private void vaidateNoCyclesOverAcyclicRelationships(Model model, ModelValidatorResult result) {
    Map<UniversalTypeExpression, Set<RelationshipExpression>> relationshipsToCheck = Maps.newHashMap();

    for (UniversalTypeExpression type : metamodel.getUniversalTypes()) {
      for (RelationshipEndExpression relEnd : type.getRelationshipEnds()) {
        if (relEnd.getRelationship().isAcyclic() && relEnd.getHolder().equals(relEnd.getType())) {
          if (relationshipsToCheck.get(type) == null) {
            relationshipsToCheck.put(type, new HashSet<RelationshipExpression>());
          }
          relationshipsToCheck.get(type).add(relEnd.getRelationship());
        }
      }
    }

    for (Entry<UniversalTypeExpression, Set<RelationshipExpression>> entry : relationshipsToCheck.entrySet()) {
      Collection<UniversalModelExpression> expressions = model.findAll(entry.getKey());
      for (RelationshipExpression relationship : entry.getValue()) {
        RelationshipEndExpression relationshipEnd = relationship.getRelationshipEnds().get(0);
        Tarjan tarjanSCC = new Tarjan(expressions, relationshipEnd);
        Set<Set<UniversalModelExpression>> stronglyConnectedComponents = tarjanSCC.run();

        Set<UniversalModelExpression> stronglyConnectedUMEs = Sets.newHashSet();
        for (Set<UniversalModelExpression> component : stronglyConnectedComponents) {
          stronglyConnectedUMEs.addAll(component);
          result.addViolation(new CycleOverAcyclicRelationshipConstraintViolation(relationship, component));
        }

        // check the UMEs which are not part of strongly connected components for one-element cycles
        // (meaning the UME is connected to itself)
        Set<UniversalModelExpression> remaining = Sets.newHashSet(expressions);
        remaining.removeAll(stronglyConnectedUMEs);
        for (UniversalModelExpression ume : remaining) {
          Collection<UniversalModelExpression> connecteds = ume.getConnecteds(relationshipEnd);
          if (connecteds.contains(ume)) {
            result.addViolation(new CycleOverAcyclicRelationshipConstraintViolation(relationship, ImmutableSet.of(ume)));
          }
        }
      }
    }
  }

  /**
   * Validates the names of the {@link UniversalTypeExpression} when a excel import is performed
   * doesn't check if a name is null or "" because the validateCardinalityConstraints Method already does that.
   * @param model
   * @param result
   */
  private void validateUniversalTypeExpressionNamesConstraints(Model model, ModelValidatorResult result) {
    for (UniversalTypeExpression type : metamodel.getUniversalTypes()) {
      if (type instanceof SubstantialTypeExpression) {
        for (UniversalModelExpression expression : model.findAll(type)) {
          String universalTypeExpressionName = (String) expression.getValue(MixinTypeNamed.NAME_PROPERTY);
          if (universalTypeExpressionName != null) {
            if (type.getPersistentName().equals(Constants.BB_INFORMATIONSYSTEMRELEASE_INITIALCAP)) {
              if (!StringInputValidator.isValidInformationSystemName(universalTypeExpressionName)) {
                result.addViolation(new UniversalTypeExpressionNameConstraintViolation(type.ID_PROPERTY, type, universalTypeExpressionName));
              }
            }
            else {
              if (!StringInputValidator.isValidBuildingBlockName(universalTypeExpressionName)) {
                result.addViolation(new UniversalTypeExpressionNameConstraintViolation(type.ID_PROPERTY, type, universalTypeExpressionName));
              }
            }
          }
        }
      }
    }
  }

  static private class Tarjan {
    private final RelationshipEndExpression              edgeType;
    private final Set<UniversalModelExpression>          toVisitNodes;

    private final Stack<UniversalModelExpression>        stack;
    private int                                          maxdfs;
    private final Map<UniversalModelExpression, Integer> nodeDfs;
    private final Map<UniversalModelExpression, Integer> lowLink;

    private final Set<Set<UniversalModelExpression>>     connectedComponents;

    Tarjan(Collection<UniversalModelExpression> nodes, RelationshipEndExpression edge) {
      this.edgeType = edge;
      this.toVisitNodes = Sets.newHashSet(nodes);

      this.stack = new Stack<UniversalModelExpression>();
      this.maxdfs = 0;
      this.nodeDfs = Maps.newHashMap();
      this.lowLink = Maps.newHashMap();
      this.connectedComponents = Sets.newHashSet();
    }

    Set<Set<UniversalModelExpression>> run() {
      while (!toVisitNodes.isEmpty()) {
        tarjan(toVisitNodes.iterator().next());
      }
      return connectedComponents;
    }

    @SuppressWarnings("boxing")
    void tarjan(UniversalModelExpression node) {
      nodeDfs.put(node, maxdfs);
      lowLink.put(node, maxdfs);
      maxdfs++;
      stack.push(node);
      toVisitNodes.remove(node);
      for (UniversalModelExpression connectedNode : node.getConnecteds(edgeType)) {
        if (toVisitNodes.contains(connectedNode)) {
          tarjan(connectedNode);
          lowLink.put(node, Math.min(lowLink.get(node), lowLink.get(connectedNode)));
        }
        else if (stack.contains(connectedNode)) {
          lowLink.put(node, Math.min(lowLink.get(node), nodeDfs.get(connectedNode)));
        }
      }
      if (lowLink.get(node).intValue() == nodeDfs.get(node).intValue()) {
        Set<UniversalModelExpression> elementsOfConnectedComponent = Sets.newHashSet();
        UniversalModelExpression cNode;
        do {
          cNode = stack.pop();
          elementsOfConnectedComponent.add(cNode);
        } while (!cNode.equals(node));
        if (elementsOfConnectedComponent.size() > 1) {
          connectedComponents.add(elementsOfConnectedComponent);
        }
      }
    }
  }

}
