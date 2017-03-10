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
package de.iteratec.iteraplan.elasticeam.derived;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.operator.join.JoinedRelationshipEnd;
import de.iteratec.iteraplan.elasticeam.operator.objectify.ObjectifyingSubstantialType;


/**
 * A factory to create derived metamodels.
 */
public final class DerivedMetamodelFactory {
  private static final Logger LOGGER = Logger.getIteraplanLogger(DerivedMetamodelFactory.class);

  private DerivedMetamodelFactory() {
    //Nothing here
  }

  /**
   * Creates a new derived metamodel which encloses the provided metamodel.
   * @param metamodel
   *    The metamodel to use as base for the new derived metamodel.
   * @return
   *    The new derived metamodel.
   */
  public static Metamodel deriveMetamodel(Metamodel metamodel) {
    long time = -System.currentTimeMillis();
    DerivedMetamodel result = new DerivedMetamodel(metamodel);
    initObjectify(result, metamodel);
    initJoin(result, metamodel);
    //initPower(result, metamodel);

    time += System.currentTimeMillis();
    LOGGER.info("Derived metamodel in {0} ms.", Long.valueOf(time));
    return result;
  }

  /*private static void initPower(DerivedMetamodel dmm, Metamodel old) {
    for (SubstantialTypeExpression ste : old.getSubstantialTypes()) {
      PowerSubstantialType pst = new PowerSubstantialType(ste);
      RelationshipEndExpression pst2ste = pst.getRelationshipEnds().get(0);
      dmm.add(pst2ste.getRelationship());
      dmm.add(pst);
      dmm.add(ste, pst2ste.getRelationship().getOppositeEndFor(pst2ste));
    }
  }*/

  private static void initObjectify(DerivedMetamodel dmm, Metamodel old) {
    for (SubstantialTypeExpression ste : old.getSubstantialTypes()) {
      for (PropertyExpression<?> property : ste.getProperties()) {
        ObjectifyingSubstantialType ost = new ObjectifyingSubstantialType(property);
        RelationshipEndExpression ost2ste = ost.getRelationshipEnds().get(0);
        dmm.add(ost2ste.getRelationship());
        dmm.add(ost);
        dmm.add(ste, ost2ste.getRelationship().getOppositeEndFor(ost2ste));
      }
    }
  }

  private static void initJoin(DerivedMetamodel dmm, Metamodel old) {
    for (SubstantialTypeExpression ste : old.getSubstantialTypes()) {
      Collection<List<RelationshipEndExpression>> paths = Lists.newLinkedList();
      collectPaths(ste, 2, paths);
      for (List<RelationshipEndExpression> path : paths) {
        RelationshipEndExpression joinedRelationshipEnd = JoinedRelationshipEnd.join(path);
        dmm.add(ste, joinedRelationshipEnd);
        dmm.add((SubstantialTypeExpression) joinedRelationshipEnd.getType(),
            joinedRelationshipEnd.getRelationship().getOppositeEndFor(joinedRelationshipEnd));
      }
    }
  }

  @SuppressWarnings("boxing")
  private static void collectPaths(SubstantialTypeExpression startClass, int length, Collection<List<RelationshipEndExpression>> result) {
    Multimap<Integer, List<RelationshipEndExpression>> pathsAndPrefixes = collectPathCandidates(startClass, length);
    for (Entry<Integer, List<RelationshipEndExpression>> pathCandidate : pathsAndPrefixes.entries()) {
      if (!endsInRelationshipType(pathCandidate.getValue()) && !isSelfReferentialPath(startClass, pathCandidate.getValue())) {
        result.add(pathCandidate.getValue());
      }
      else if (pathCandidate.getKey() == length) {
        for (List<RelationshipEndExpression> expandedPathCandidate : expandByOneStep(pathCandidate.getValue())) {
          if (!endsInRelationshipType(expandedPathCandidate) && !isSelfReferentialPath(startClass, pathCandidate.getValue())) {
            result.add(expandedPathCandidate);
          }
        }
      }
    }
  }

  private static Multimap<Integer, List<RelationshipEndExpression>> collectPathCandidates(SubstantialTypeExpression startClass, int length) {
    Multimap<Integer, List<RelationshipEndExpression>> pathsAndPrefixes = HashMultimap.create();
    for (RelationshipEndExpression relationshipEnd : startClass.getRelationshipEnds()) {
      pathsAndPrefixes.putAll(Integer.valueOf(2), expandByOneStep(Collections.singletonList(relationshipEnd)));
    }
    for (int i = 3; i <= length; i++) {
      for (List<RelationshipEndExpression> prefix : pathsAndPrefixes.get(Integer.valueOf(i - 1))) {
        pathsAndPrefixes.putAll(Integer.valueOf(i), expandByOneStep(prefix));
      }
    }
    return pathsAndPrefixes;
  }

  private static Collection<List<RelationshipEndExpression>> expandByOneStep(List<RelationshipEndExpression> prefix) {
    RelationshipEndExpression lastReference = prefix.get(prefix.size() - 1);
    Collection<List<RelationshipEndExpression>> newPrefixes = Lists.newLinkedList();
    for (RelationshipEndExpression candidate : lastReference.getType().getRelationshipEnds()) {
      if (!candidate.getRelationship().getOppositeEndFor(candidate).equals(lastReference)) {
        List<RelationshipEndExpression> newPath = Lists.newLinkedList(prefix);
        newPath.add(candidate);
        newPrefixes.add(newPath);
      }
    }
    return newPrefixes;
  }

  private static boolean isSelfReferentialPath(SubstantialTypeExpression startClass, List<RelationshipEndExpression> path) {
    RelationshipEndExpression lastReference = path.get(path.size() - 1);
    return startClass.equals(lastReference.getType());
  }

  private static boolean endsInRelationshipType(List<RelationshipEndExpression> path) {
    RelationshipEndExpression lastReference = path.get(path.size() - 1);
    return lastReference.getType() instanceof RelationshipTypeExpression;
  }
}
