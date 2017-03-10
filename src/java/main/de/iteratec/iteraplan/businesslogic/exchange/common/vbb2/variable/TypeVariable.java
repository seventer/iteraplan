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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.variable;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation.FlatVariableRecommendation;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation.PrimaryRecommendation;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation.Priority;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation.StructuredVariableRecommendation;
import de.iteratec.iteraplan.elasticmi.dynamic.DynamicMetamodel;
import de.iteratec.iteraplan.elasticmi.dynamic.DynamicMetamodelImpl;
import de.iteratec.iteraplan.elasticmi.exception.ElasticMiException;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression.OriginalWType;


/**
 * A {@link TypeVariable} holds a {@link RStructuredTypeExpression} (or short a type) as value, and recommends a list of types as possible values.
 * A {@link TypeVariable} is constructed as a part of a variable tree, 
 * and then configured from a {@link ViewpointConfiguration} and/or given its boundary value, that is another adjacent type.
 * If the {@link TypeVariable} is configured, it has a (single) value and a one-element recommendation for that value.
 * If the {@link TypeVariable} is not set, it has no value and a (typically) multi-value recommendation, depending on the boundary.
 * <p>
 * Without a boundary type, the variable recommends all types (excluding relationship types) and their objectified types.
 * With a boundary type, the variable recommends (one hop) all directly adjacent types (including the objectified types), 
 * or (two hops) types adjacent to an directly adjacent type, or (two and a half) via an relationship type and another intermediate type, excluding itself.
 * <p>
 * A {@link TypeVariable} has sub-variables for the self-relationship and the coloring, both depending on its value.
 * <p>
 * When this {@link TypeVariable} is configured with a value, it notifies its parent via a callback object, and its sub-variables.
 */
public class TypeVariable {

  private final String                     name;
  private final RMetamodel                 metamodel;

  private final TypeVariableParentCallback parentCallback;

  private final SelfRelationshipVariable   selfRelationshipNesting;
  private final TypeColoringVariable       coloringConfiguration;

  private RStructuredTypeExpression        value;

  private RStructuredTypeExpression        boundaryOppositeType;

  public TypeVariable(String name, RMetamodel metamodel, TypeVariableParentCallback parentCallback, String defaultStaticColor) {
    this.name = name;
    this.metamodel = metamodel;
    this.parentCallback = parentCallback;
    this.selfRelationshipNesting = new SelfRelationshipVariable(metamodel, name + "2" + name);
    this.coloringConfiguration = new TypeColoringVariable(defaultStaticColor);
  }

  private void notifyParentAndChildren() {
    parentCallback.typeChanged(value);
    selfRelationshipNesting.setBondaryHolderType(value);
    coloringConfiguration.setBondaryHolderType(value);
  }

  /**
   * Configures the value of this variable from an entry with the key "inner" or "outer", depending on its name, if present.
   * The value in the configuration must be a valid persistent name of a {@link RStructuredTypeExpression} in the metamodel of this variable.
   * If the recommendation is invalid, or if no entry with its key is present, the value is not set.
   * <p>
   * If the value is set, this variable notifies its parent (via callback) and its sub-variables.
   * <p>
   * Note that this method accepts persistent names of types that would never be recommended. Using these types, for example filtered types,
   * is not checked and leads to undefined behavior. 
   * <p>
   * Configures the sub-variables.
   * 
   * @param vpConfig  {@link ViewpointConfiguration} object to configure the variable tree
   */
  public void configureFrom(ViewpointConfiguration vpConfig) {
    String valueString = vpConfig.get(getName());
    if (valueString != null) {
      try {
        this.value = IteraQl2Compiler.compile(metamodel, valueString).getLeft();
        notifyParentAndChildren();
        this.coloringConfiguration.configureFrom(vpConfig.withPrefix(getName()));
        this.selfRelationshipNesting.configureFrom(vpConfig.withPrefix(getName()));
      } catch (ElasticMiException e) {
        //NOOP
      }
    }
  }

  /**
   * Appends a recommendation of type names for the key "inner" or "outer", depending on its name.
   * 
   * If value is set: flat recommendation with one element and high priority.
   * 
   * If value is not set, and the boundary is not set:
   * structured recommendation with a primary recommendation for each non-relationship type in the metamodel with high priority,
   * with secondary recommendations for each non-technical property as objectified type.
   * 
   * If value is not set and boundary is set:
   * flat (!) recommendation with two parts:
   * first part: all types other than the boundary type, connected by one, two or two-and-a-half hops, sorted by (localized) name, case-insensitive.
   * second part: all objectified types for the properties of the boundary type, sorted by (localized) name, case-insensitive.
   * 
   * If value is not set and boundary is an objectified type:
   * flat recommendation with one element, which is the base type of the (objectified type in the) boundary.
   * 
   * Given boundary type B, connected by...
   * one hop: all types of B/r for all relationship ends /r.
   * two hops: all types of B/r/s, where B/r may or may not be a relationship type.
   * two-and-a-half hops: all types of B/r/s/t, where B/r is a relationship type, or B/r/s is a relationship type. 
   *  
   * Example with the best-practice metamodel: 
   * BD one hop: BP, BU, Prod, BO, BF
   * BD two hops: (BO-) IS
   * BD two-and-a-half hps: (BP/BU/Prod - BM -) IS, (BO - IFlow - ) IS, (BO - IFlow -) IF
   * 
   * Experimental:
   * 1hop = connected(B) 
   * 2hop = connected(1hop)
   * 2 1/2hop = connected(connected( 1hop filter isRelType )) union connected(2hop filter isRelType)
   * 
   * result := (1hop union 2hop union 21/2 hop) filterNot isrelType \ B
   * 
   * 
   * @param vpRecommendation Result object to be filled in with the recommendation for the key of this variable and its sub-variables. Entries with other keys are not modified.
   */
  public void appendRecommendations(ViewpointRecommendation vpRecommendation) {
    if (value != null) {
      appendValue(vpRecommendation);
    }
    else if (boundaryOppositeType != null) {
      appendBoundOpposite(vpRecommendation);
    }
    else {
      appendUnbound(vpRecommendation);
    }
  }

  private void appendUnbound(ViewpointRecommendation vpRecommendation) {
    for (RStructuredTypeExpression type : VariableUtils.sortByPersistentName(metamodel.getStructuredTypes())) {
      if (!OriginalWType.RELATIONSHIP.equals(type.getOriginalWType())) {
        StructuredVariableRecommendation typeRec = vpRecommendation.addStructured(getName());
        PrimaryRecommendation primaryRec = typeRec.append(type.getPersistentName(), type.getName(), Priority.HIGH);
        DynamicMetamodel dynamicMM = new DynamicMetamodelImpl();
        for (RPropertyExpression property : VariableUtils.sortByPersistentName(type.getAllProperties())) {
          if (!property.isTechnical()) {
            RStructuredTypeExpression objectifiedType = dynamicMM.createObjectifyingStructuredType(type, property);
            primaryRec.append(objectifiedType.getPersistentName(), objectifiedType.getName());
          }
        }
      }
    }
  }

  private void appendBoundOpposite(ViewpointRecommendation vpRecommendation) {
    Set<RStructuredTypeExpression> canonicTypesToRecommend = Sets.newHashSet();
    for (RRelationshipEndExpression relEnd0 : this.boundaryOppositeType.getAllRelationshipEnds()) {
      if (!relEnd0.isSelfRelationship() && !OriginalWType.RELATIONSHIP.equals(relEnd0.getType().getOriginalWType())) {
        //non-self non-rte hop0
        canonicTypesToRecommend.add(relEnd0.getType());
        for (RRelationshipEndExpression relEnd1 : relEnd0.getType().getAllRelationshipEnds()) {
          if (!relEnd1.getOpposite().equals(relEnd0) && !OriginalWType.RELATIONSHIP.equals(relEnd1.getType().getOriginalWType())) {
            //second non-relationship hop
            canonicTypesToRecommend.add(relEnd1.getType());
          }
        }
      }
      else if (OriginalWType.RELATIONSHIP.equals(relEnd0.getType().getOriginalWType())) {
        for (RRelationshipEndExpression relEnd1 : relEnd0.getType().getAllRelationshipEnds()) {
          if (!relEnd1.getOpposite().equals(relEnd0)) {
            //not opposite of previous
            canonicTypesToRecommend.add(relEnd1.getType());
            //and one more hop
            for (RRelationshipEndExpression relEnd2 : relEnd1.getType().getAllRelationshipEnds()) {
              if (!relEnd2.isSelfRelationship() && !OriginalWType.RELATIONSHIP.equals(relEnd2.getType().getOriginalWType())) {
                canonicTypesToRecommend.add(relEnd2.getType());
              }
            }
          }
        }
      }
    }
    FlatVariableRecommendation typeRecommendaton = vpRecommendation.addFlat(getName());
    List<RStructuredTypeExpression> sortedCanonicTypes = VariableUtils.sortByPersistentName(Lists.newArrayList(canonicTypesToRecommend));
    for (RStructuredTypeExpression relatedType : sortedCanonicTypes) {
      typeRecommendaton.append(relatedType.getPersistentName(), relatedType.getName(), Priority.HIGH);
    }
    DynamicMetamodel dynamicMetamodel = new DynamicMetamodelImpl();
    List<RStructuredTypeExpression> objetifiedTypes = Lists.newArrayList();
    for (RPropertyExpression property : this.boundaryOppositeType.getAllProperties()) {
      if (!property.isTechnical()) {
        RStructuredTypeExpression objectifiedType = dynamicMetamodel.createObjectifyingStructuredType(boundaryOppositeType, property);
        objetifiedTypes.add(objectifiedType);
      }
    }
    objetifiedTypes = VariableUtils.sortByPersistentName(objetifiedTypes);
    for (RStructuredTypeExpression objectifiedType : objetifiedTypes) {
      typeRecommendaton.append(objectifiedType.getPersistentName(), objectifiedType.getName(), Priority.HIGH); //???LOW?
    }
  }

  private void appendValue(ViewpointRecommendation vpRecommendation) {
    vpRecommendation.addFlat(getName()).append(value.getPersistentName(), value.getName(), Priority.HIGH);
  }

  protected void setBondaryOppositeType(RStructuredTypeExpression oppositeType) {
    this.boundaryOppositeType = oppositeType;
  }

  public RStructuredTypeExpression getValue() {
    return value;
  }

  public String getName() {
    return name;
  }

  /**
   * Callback for type variables to notify their parent
   * when they have been set.
   */
  protected interface TypeVariableParentCallback {
    void typeChanged(RStructuredTypeExpression type);
  }

}
