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
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation.Priority;
import de.iteratec.iteraplan.elasticmi.dynamic.DynamicMetamodel;
import de.iteratec.iteraplan.elasticmi.dynamic.DynamicMetamodelImpl;
import de.iteratec.iteraplan.elasticmi.exception.ElasticMiException;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression.OriginalWType;


/**
 * A {@link RelationshipVariable} holds a {@link RRelationshipEndExpression} (or short a relationship end) as value, 
 * and recommends a list of relationsship ends as possible values. 
 * Relationship ends can be simple and direct, or joined and via an intermediate type.
 * A {@link RelationshipVariable} is constructed as a part of a variable tree, 
 * and then configured from a {@link ViewpointConfiguration} and/or given one or two boundary values, that is one or both adjacent type.
 * If the {@link RelationshipVariable} is configured, it has a (single) value and a one-element recommendation for that value.
 * If the {@link RelationshipVariable} is not set, it has no value and a (typically) multi-value recommendation, depending on the boundaries.
 * <p>
 * Without a boundary type, or with only one boundary type, the variable makes no recommendation, that is an empty one.
 * Only with both boundary types, the variable recommends all relationship ends, starting at the outer type and ending at the inner type.
 * The relationship ends can reach the inner type directly (one hop), or via an intermediate type (two hops) 
 * or (two and a half) via an relationship type and another intermediate type.
 * Note that the joined relationship ends may visit a type more than one time, as in IS/parent/usedComponents.
 * <p>
 * A {@link RelationshipVariable} has no sub-variables.
 * <p>
 * In the context of a Nesting Cluster Diagram, a {@link RelationshipVariable} does not notfiy its parent nor sub-variables.
 * It does not need to notify its parent, because its siblings, the adjaced type variables, are always set when this {@link RelationshipVariable} is set.
 * <p>
 * The {@link RelationshipVariable} holds a second {@link RRelationshipEndExpression} that is the opposite of the its main value.
 * For both relationship ends, this variable adds a recommendation with the key "outer.outer2inner" and "inner.inner2outer", respectively.
 * 
 */
public class RelationshipVariable {

  protected static final String      NAME          = "outer.outer2inner";
  protected static final String      NAME_OPPOSITE = "inner.inner2outer";

  private final RMetamodel           metamodel;

  private RRelationshipEndExpression value;

  private RStructuredTypeExpression  boundaryOuterType;
  private RStructuredTypeExpression  boundaryInnerType;

  public RelationshipVariable(RMetamodel metamodel) {
    this.metamodel = metamodel;
  }

  /** 
   * configures the value of this variable from an entry with the key "outer.outer2inner". 
   * Note that an entry for the opposite direction "inner.inner2outer" is ignored, and in practice there is no such entry.
   * If there is no entry, or if the value is empty or not a valid persistent name of a relationship end of the outer type,
   * no value is set.
   * <p>
   * Note that this method accepts persistent names of relationship ends that would never be recommended. 
   * Using these relationship ends, for example filtered relationship ends, is not checked and leads to undefined behavior. 
   * @param vpConfig
   */
  public void configureFrom(ViewpointConfiguration vpConfig) {
    String valueString = vpConfig.get(NAME);
    if (valueString != null && boundaryOuterType != null) {
      String iteraQlString = boundaryOuterType.getPersistentName() + valueString;
      try {
        RRelationshipEndExpression relEnd = IteraQl2Compiler.compile(metamodel, iteraQlString).getRight();
        this.value = relEnd;
      } catch (ElasticMiException e) {
        //NOOP  debug log message
      }
    }
  }

  /**
   * Recommends possible values for the relationship end, depending on the adjacent boundary types.
   * <br>Cases:
   * <br>
   * No boundary type or only one boundary type set: no recommendation.
   * <br>
   * Both boundary types set, and outer is an objectified type: isValueOf
   * <br>
   * Both boundary types set, and inner is an objectified type: opposite of isValueOf, that is "valueOf(...)
   * <br>
   * Both boundary types set, both canonic:
   * 
   * one hop: all relationship ends r of outer type O so that O/r has inner type I
   * two hops: all relationship ends r/s, where r is a relationship of outer type O, and O/r/s has inner type I
   * Note that both r and s may be self-relationships
   * two-and-a-half hops: all relationship ends r/s/t, where r is a relationship of outer type O, and O/r/s/t has inner type I,
   * if O/r or O/r/s has a relationship type.
   * 
   * <p>
   * The relationship ends are sorted by number of hops and then by localized name.
   * 
   * <p>
   * Reconsider the concept of "2 1/2 hops" when relationship types will have self-relationships and or relationship types connect relationship types (chaining).
   * 
   * @param vpRecommendation
   *    The vpRecommendation to append to.
   */
  public void appendRecommendations(ViewpointRecommendation vpRecommendation) {
    if (boundaryOuterType == null || boundaryInnerType == null) {
      //recommend only if both boundary types are set
      return;
    }
    Set<RRelationshipEndExpression> relEnds = Sets.newHashSet();
    if (!boundaryOuterType.isCanonic()) {
      //if outer not canonic -> only relEnds leading to the inner type
      //note that this should, by configuration,
      //only happen when the inner is the canonic base of the outer
      //and the outer is an objectified property
      relEnds.addAll(getRecommendationForSyntheticOuter());
    }
    else if (!boundaryInnerType.isCanonic()) {
      //symmetric case as above for the inner type
      relEnds.addAll(getRecommendationForSyntheticInner());
    }
    else {
      //both outer and inner are canonic -> 2.5 hop full metamodel recommendation
      relEnds.addAll(getFullCanonicRelationshipEndRecommendation());
    }

    //sort recommendations
    List<RRelationshipEndExpression> sortedOuter2InnerRecommendations = VariableUtils.sortByPersistentName(Lists.newArrayList(relEnds));
    List<RRelationshipEndExpression> sortedInner2OuterRecommendations = Lists.newArrayList();
    for (RRelationshipEndExpression relEnd : sortedOuter2InnerRecommendations) {
      sortedInner2OuterRecommendations.add(relEnd.getOpposite());
    }
    sortedInner2OuterRecommendations = VariableUtils.sortByPersistentName(sortedInner2OuterRecommendations);

    //append recommendations
    FlatVariableRecommendation o2iRecommendation = vpRecommendation.addFlat(NAME);
    for (RRelationshipEndExpression relEnd : sortedOuter2InnerRecommendations) {
      o2iRecommendation.append(relEnd.getPersistentName(), relEnd.getName(), Priority.HIGH);
    }
    FlatVariableRecommendation i2oRecommendation = vpRecommendation.addFlat(NAME_OPPOSITE);
    for (RRelationshipEndExpression relEnd : sortedInner2OuterRecommendations) {
      i2oRecommendation.append(relEnd.getPersistentName(), relEnd.getName(), Priority.HIGH);
    }
  }

  private Set<RRelationshipEndExpression> getFullCanonicRelationshipEndRecommendation() {
    Set<RRelationshipEndExpression> recommendations = Sets.newHashSet();
    DynamicMetamodel dynamicMetamodel = new DynamicMetamodelImpl();
    for (RRelationshipEndExpression relEnd0 : boundaryOuterType.getAllRelationshipEnds()) {
      if (relEnd0.getType().equals(boundaryInnerType)) {
        //direct single step (1 hop)
        //(assumed not to be a self-relationship, because of appropriate type recommendation)
        recommendations.add(relEnd0);
        //potential second and third hops building a slef relationship
        for (RRelationshipEndExpression relEnd1 : relEnd0.getType().getAllRelationshipEnds()) {
          if (relEnd1.isSelfRelationship()) {
            //do another hop over self relationships (2 hops)
            recommendations.add(dynamicMetamodel.createJoinedRelationshipEnd(boundaryOuterType, relEnd0, relEnd1));
          }
          else if (OriginalWType.RELATIONSHIP.equals(relEnd1.getType().getOriginalWType())) {
            //do another hop over a relationship type (2.5 hops)
            for (RRelationshipEndExpression relEnd2 : relEnd1.getType().getAllRelationshipEnds()) {
              if (!relEnd2.equals(relEnd1.getOpposite()) && relEnd2.getType().equals(boundaryInnerType)) {
                RRelationshipEndExpression innerJoin = dynamicMetamodel.createJoinedRelationshipEnd(relEnd0.getType(), relEnd1, relEnd2);
                recommendations.add(dynamicMetamodel.createJoinedRelationshipEnd(boundaryOuterType, relEnd0, innerJoin));
              }
            }
          }
        }
      }
      else {
        //do another hop (2+ hops)
        for (RRelationshipEndExpression relEnd1 : relEnd0.getType().getAllRelationshipEnds()) {
          if (relEnd1.getType().equals(boundaryInnerType)) {
            //append /canonic/canonic (2 hops)
            recommendations.add(dynamicMetamodel.createJoinedRelationshipEnd(boundaryOuterType, relEnd0, relEnd1));
          }
          else if (OriginalWType.RELATIONSHIP.equals(relEnd1.getType().getOriginalWType())) {
            //append /canonic/toRTE/fromRTE (2.5 hops)
            for (RRelationshipEndExpression relEnd2 : relEnd1.getType().getAllRelationshipEnds()) {
              if (!relEnd2.equals(relEnd1.getOpposite()) && relEnd2.getType().equals(boundaryInnerType)) {
                RRelationshipEndExpression innerJoin = dynamicMetamodel.createJoinedRelationshipEnd(relEnd0.getType(), relEnd1, relEnd2);
                recommendations.add(dynamicMetamodel.createJoinedRelationshipEnd(boundaryOuterType, relEnd0, innerJoin));
              }
            }
          }
        }
      }
    }
    return recommendations;
  }

  private Set<RRelationshipEndExpression> getRecommendationForSyntheticOuter() {
    Set<RRelationshipEndExpression> relEnds = Sets.newHashSet();
    for (RRelationshipEndExpression relEnd : boundaryOuterType.getAllRelationshipEnds()) {
      if (relEnd.getType().equals(boundaryInnerType)) {
        // ? if outer is an objectified type, for example obj(IS@costs), then it has only one relationship end towards its original holder typ: "/isValueOf".
        // so this if is not needed
        // caution: this may change with more synthetic types in the future.
        relEnds.add(relEnd);
      }
    }
    return relEnds;
  }

  private Set<RRelationshipEndExpression> getRecommendationForSyntheticInner() {
    Set<RRelationshipEndExpression> relEnds = Sets.newHashSet();
    for (RRelationshipEndExpression relEnd : boundaryInnerType.getAllRelationshipEnds()) {
      if (relEnd.getType().equals(boundaryOuterType)) {
        // see above: if inner is an objectified type, inner has only one relationhip end, isValueOf, and its opposite, "valueOf(...)"
        // is what we need.
        // no if needed.
        // caution as above

        //add the opposite, since we are processing the inner type
        //and the relEnd is the inner2outer
        relEnds.add(relEnd.getOpposite());
      }
    }
    return relEnds;
  }

  protected void setBoundaryOuterType(RStructuredTypeExpression outerType) {
    this.boundaryOuterType = outerType;
  }

  protected void setBoundaryInnerType(RStructuredTypeExpression innerType) {
    this.boundaryInnerType = innerType;
  }

  public String getName() {
    return NAME;
  }

  public RRelationshipEndExpression getValue() {
    return value;
  }

}
