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
package de.iteratec.iteraplan.elasticeam.model.compare.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilder;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilderResult;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffPart;
import de.iteratec.iteraplan.elasticeam.model.compare.LeftSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchResult;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchingPair;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.SingleSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;


public class DiffBuilderImpl implements DiffBuilder {

  private static final Logger      LOGGER             = Logger.getIteraplanLogger(DiffBuilderImpl.class);

  private static final Set<String> IGNORED_PROPERTIES = Sets.newHashSet("lastModificationTime", "lastModificationUser");

  private final MatchResult        matchResult;
  private DiffMode                 mode               = DiffMode.ADDITIVE;

  public DiffBuilderImpl(MatchResult matchResult) {
    this.matchResult = matchResult;
  }

  public void setMode(DiffMode mode) {
    this.mode = mode;
  }

  /**{@inheritDoc}**/
  public DiffBuilderResult computeDifferences() {
    DiffBuilderResultImpl result = new DiffBuilderResultImpl(matchResult);

    processLeftUnmatcheds(result);
    processRightUnmatcheds(result);
    processMatches(result);

    return result;
  }

  private void processLeftUnmatcheds(DiffBuilderResultImpl result) {
    if (mode != DiffMode.ADDITIVE) {
      for (Entry<UniversalTypeExpression, Set<UniversalModelExpression>> leftUnmatched : this.matchResult.getLeftUnmatched().entrySet()) {
        UniversalTypeExpression universalType = leftUnmatched.getKey();

        for (UniversalModelExpression ume : leftUnmatched.getValue()) {
          if (isInstanceToDelete(ume, universalType)) {
            result.addDiff(new LeftSidedDiffImpl(universalType, ume));
          }
        }
      }
    }
  }

  private boolean isInstanceToDelete(UniversalModelExpression ume, UniversalTypeExpression universalType) {
    if (mode == DiffMode.STRICT) {
      return true;
    }
    else if (mode == DiffMode.ADDITIVE || universalType instanceof SubstantialTypeExpression) {
      return false;
    }
    else if (mode == DiffMode.V_PARTIAL) {
      for (RelationshipEndExpression re : universalType.getRelationshipEnds()) {
        Collection<UniversalModelExpression> connecteds = ume.getConnecteds(re);
        for (UniversalModelExpression connected : connecteds) {
          /*
           * If one of the connected elements is not in the right model, this means
           * we don't touch the relationship object.
           * TODO Can the relationship object be invalidated by relationship-removing
           * two-sided diffs of other elements?
           */
          if (matchResult.getLeftUnmatchedForType(re.getType()).contains(connected)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private void processRightUnmatcheds(DiffBuilderResultImpl result) {
    for (Entry<UniversalTypeExpression, Set<UniversalModelExpression>> rightUnmatched : this.matchResult.getRightUnmatched().entrySet()) {
      for (UniversalModelExpression ume : rightUnmatched.getValue()) {
        result.addDiff(new RightSidedDiffImpl(rightUnmatched.getKey(), ume));
      }
    }
  }

  private void processMatches(DiffBuilderResultImpl result) {
    //Prepare instance map
    BiMap<UniversalModelExpression, UniversalModelExpression> left2right = HashBiMap.create();
    for (Entry<UniversalTypeExpression, Set<MatchingPair>> matched : matchResult.getMatches().entrySet()) {
      for (MatchingPair pair : matched.getValue()) {
        left2right.put(pair.getLeftExpression(), pair.getRightExpression());
      }
    }

    // process matches
    for (Entry<UniversalTypeExpression, Set<MatchingPair>> matched : matchResult.getMatches().entrySet()) {
      for (MatchingPair pair : matched.getValue()) {
        TwoSidedDiffImpl diff = new TwoSidedDiffImpl(matched.getKey(), pair.getLeftExpression(), pair.getRightExpression());
        for (PropertyExpression<?> property : matched.getKey().getProperties()) {
          diffProperty(property, pair.getLeftExpression(), pair.getRightExpression(), diff);
        }
        for (RelationshipEndExpression re : matched.getKey().getRelationshipEnds()) {
          diffRelationshipEnd(re, pair, left2right, diff, result);
        }
        if (!diff.getDiffParts().isEmpty()) {
          result.addDiff(diff);
        }
      }
    }
  }

  private void diffProperty(PropertyExpression<?> pe, UniversalModelExpression left, UniversalModelExpression right, TwoSidedDiffImpl result) {
    if (IGNORED_PROPERTIES.contains(pe.getPersistentName())) {
      return;
    }
    if (pe.getUpperBound() > 1) {
      Collection<Object> leftValues = left.getValues(pe);
      Collection<Object> rightValues = fixPropertyRightValuesForDiffMode(leftValues, right.getValues(pe));

      boolean areEqual = leftValues.size() == rightValues.size();
      for (Object leftValue : leftValues) {
        boolean foundMatch = false;
        for (Object rightValue : rightValues) {
          foundMatch |= isEqual(leftValue, rightValue);
        }
        areEqual &= foundMatch;
      }
      if (!areEqual) {
        result.addDiffPart(pe, leftValues, rightValues);
      }
    }
    else if (isPropertyValueToUpdate(left.getValue(pe), right.getValue(pe))) {
      result.addDiffPart(pe, left.getValue(pe), right.getValue(pe));
    }
  }

  private boolean isPropertyValueToUpdate(Object leftValue, Object rightValue) {
    if (mode == DiffMode.ADDITIVE && rightValue == null) {
      return false;
    }
    else {
      return !isEqual(leftValue, rightValue);
    }
  }

  private Collection<Object> fixPropertyRightValuesForDiffMode(Collection<Object> leftValues, Collection<Object> rightValues) {
    Collection<Object> newRightValues = copyCollection(rightValues);
    if (mode == DiffMode.ADDITIVE) {
      List<Object> valuesToAddToRightSide = Lists.newArrayList(leftValues);
      valuesToAddToRightSide.removeAll(newRightValues);
      if (!valuesToAddToRightSide.isEmpty()) {
        newRightValues.addAll(valuesToAddToRightSide);
      }
    }
    return newRightValues;
  }

  private void diffRelationshipEnd(RelationshipEndExpression re, MatchingPair pair,
                                   BiMap<UniversalModelExpression, UniversalModelExpression> left2right, TwoSidedDiffImpl diff,
                                   DiffBuilderResultImpl result) {
    if (re.getUpperBound() > 1) {
      Collection<UniversalModelExpression> leftValues = pair.getLeftExpression().getConnecteds(re);
      Collection<UniversalModelExpression> rightValues = fixRelationshipRightValuesForDiffMode(pair.getRightExpression().getConnecteds(re),
          leftValues, re, left2right);

      // Match needed for correct compare, see #isEqual(Object, Object)
      Collection<UniversalModelExpression> rightValuesToCheck = matchUMEs(rightValues, left2right.inverse());
      boolean areEqual = leftValues.size() == rightValues.size();
      for (UniversalModelExpression leftValue : leftValues) {
        // Check whether for each connected value in the left model we have a match in the right model.
        // Ignores ordering.
        boolean foundMatch = false;
        for (UniversalModelExpression rightValue : rightValuesToCheck) {
          foundMatch |= isEqual(leftValue, rightValue);
        }
        areEqual &= foundMatch;
      }
      if (!areEqual) {
        diff.addDiffPart(re, leftValues, rightValues);
      }
    }
    else {
      if (isToOneRelationToBeUpdated(pair, re, left2right, result)) {
        diff.addDiffPart(re, pair.getLeftExpression().getConnected(re), pair.getRightExpression().getConnected(re));
      }
    }
  }

  @SuppressWarnings("PMD.MissingBreakInSwitch")
  private boolean isToOneRelationToBeUpdated(MatchingPair pair, RelationshipEndExpression re,
                                             BiMap<UniversalModelExpression, UniversalModelExpression> left2right, DiffBuilderResultImpl result) {
    UniversalModelExpression rightConnected = pair.getRightExpression().getConnected(re);
    UniversalModelExpression leftConnected = pair.getLeftExpression().getConnected(re);

    switch (mode) {
      case ADDITIVE:
        // Changed due to issue ITERAPLAN-1306 to enable change of hierarchy (to root) by excel import.
        // Now, if the connected element is null on the right side, this will be accepted as a change
        // even in ADDITIVE mode
      case V_PARTIAL:
        if (matchResult.getLeftUnmatchedForType(re.getType()).contains(leftConnected)) {
          // Changes in relationship assignment will only be applied if the originally
          // assigned element is present in the right model, too
          String warn = "Change of \"{0}\" in \"{1}\" not applied, because formerly assigned element \"{2}\" is not present in the target model.";
          result.addWarning(MessageFormat.format(warn, re.getName(), pair.getLeftExpression(), leftConnected));
          return false;
        }
        //$FALL-THROUGH$
      case STRICT:
        UniversalModelExpression rightMatchedToLeft = left2right.inverse().get(rightConnected);
        if (rightMatchedToLeft == null) {
          rightMatchedToLeft = rightConnected;
        }
        return !isEqual(leftConnected, rightMatchedToLeft);
      default:
        LOGGER.error("Unsupported diff mode \"{0}\".", mode);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  /**
   * Modifies the "rightValues" collection of assigned elements by adding from "leftValues" and/or
   * removing elements according to the rules of the active {@link DiffMode}.
   */
  private Collection<UniversalModelExpression> fixRelationshipRightValuesForDiffMode(Collection<UniversalModelExpression> rightValues,
                                                                                     Collection<UniversalModelExpression> leftValues,
                                                                                     RelationshipEndExpression re,
                                                                                     BiMap<UniversalModelExpression, UniversalModelExpression> left2right) {

    List<UniversalModelExpression> droppedLefts = Lists.newArrayList(leftValues);
    droppedLefts.removeAll(matchUMEs(rightValues, left2right.inverse()));
    RelationshipEndExpression opposite = re.getRelationship().getOppositeEndFor(re);
    boolean manyToManyCase = opposite.getUpperBound() > 1;

    Collection<UniversalModelExpression> rightValuesToBeApplied = copyCollection(rightValues);
    switch (mode) {
      case ADDITIVE:
        if (manyToManyCase) {
          // In additive mode we don't drop assignments in many-to-many relations, hence
          // the adding of the elements which were marked as dropped
          rightValuesToBeApplied.addAll(droppedLefts);
          break;
        }
        // If upper bound of the opposite = 1 we need to consider changed references on the "to one" side of the
        // relation which result in a removal of references on the "to many" side.
        // That's why in that case we create the diff as if in v_partial mode
        //$FALL-THROUGH$
      case V_PARTIAL:
        if (!manyToManyCase) {
          // Check, whether one of the right values to be applied results from an illegal change.
          // Changing an assignment in a to-one relation if the originally assigned element is not
          // in the right side model is an illegal change.
          for (Iterator<UniversalModelExpression> i = rightValuesToBeApplied.iterator(); i.hasNext();) {
            UniversalModelExpression leftUME = left2right.inverse().get(i.next());
            if (leftUME != null && matchResult.getLeftUnmatchedForType(opposite.getType()).contains(leftUME.getConnected(opposite))) {
              i.remove();
            }
          }
        }
        // Assignments can only be removed in v_partial mode if both originally connected elements
        // are present in the right side model.
        for (UniversalModelExpression leftUME : droppedLefts) {
          if (matchResult.getLeftUnmatchedForType(re.getType()).contains(leftUME)) {
            // originally connected element not found in right model => retain assignment
            rightValuesToBeApplied.add(leftUME);
          }
        }
        break;
      case STRICT:
        // nothing to do, just use the assignments as they are in the right side model
        break;
      default:
        LOGGER.error("Unsupported diff mode \"{0}\".", mode);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    return rightValuesToBeApplied;
  }

  @SuppressWarnings("unchecked")
  private <T> Collection<T> copyCollection(Collection<T> collection) {
    Class<?> collectionClass = collection.getClass();
    Collection<T> copy;
    try {
      copy = (Collection<T>) collectionClass.newInstance();
      copy.addAll(collection);
      return copy;
    } catch (InstantiationException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    } catch (IllegalAccessException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    }
  }

  /**
   * Returns {@link UniversalModelExpression}s matching, according to the given map,
   * those expressions in the given collection.
   * If an expression is not found in the map, the original one is used.
   */
  private Collection<UniversalModelExpression> matchUMEs(Collection<UniversalModelExpression> collection,
                                                         Map<UniversalModelExpression, UniversalModelExpression> matchingElements) {
    List<UniversalModelExpression> matchedList = Lists.newArrayList();
    for (UniversalModelExpression ume : collection) {
      if (matchingElements.containsKey(ume)) {
        matchedList.add(matchingElements.get(ume));
      }
      else {
        matchedList.add(ume);
      }
    }
    return matchedList;
  }

  /**
   * Compares two objects for equality.
   * In case {@link UniversalModelExpression}s are compared, be sure both expressions
   * are either from the right or from the left model, else always false will be returned,
   * even for matching elements of left and right model, since UMEs are compared with '=='.
   * @param o1
   * @param o2
   * @return true if o1 is equal/a match for o2, false otherwise. null is equal to null.
   */
  private static boolean isEqual(Object o1, Object o2) {
    if (o1 == null) {
      return o2 == null;
    }
    if (o2 == null) {
      return false;
    }
    if (o1 instanceof BigDecimal && o2 instanceof BigDecimal) {
      return ((BigDecimal) o1).compareTo((BigDecimal) o2) == 0;
    }
    if (o1 instanceof Date && o2 instanceof Date) {
      return ((Date) o1).compareTo((Date) o2) == 0;
    }
    if (o1 instanceof String && o2 instanceof String) {
      //Normalization of whitespace characters for Excel 2003/2007 compatibility
      return ((String) o1).trim().replace("\r\n", "\n").equals(((String) o2).replace("\r\n", "\n").trim());
    }
    return o1.equals(o2);
  }

  static class DiffPartImpl implements DiffPart {
    private final FeatureExpression<?> feature;
    private final Object               leftValue;
    private final Object               rightValue;

    DiffPartImpl(FeatureExpression<?> feature, Object leftValue, Object rightValue) {
      this.feature = feature;
      this.leftValue = leftValue;
      this.rightValue = rightValue;
    }

    /**{@inheritDoc}**/
    public FeatureExpression<?> getFeature() {
      return feature;
    }

    /**{@inheritDoc}**/
    public Object getLeftValue() {
      return leftValue;
    }

    /**{@inheritDoc}**/
    public Object getRightValue() {
      return rightValue;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof DiffPart)) {
        return false;
      }
      DiffPart otherPart = (DiffPart) obj;
      boolean leftValsMatch = false;
      if ((leftValue == null && otherPart.getLeftValue() == null)
          || (leftValue != null && otherPart.getLeftValue() != null && leftValue.equals(otherPart.getLeftValue()))) {
        leftValsMatch = true;
      }
      boolean rightValsMatch = false;
      if ((rightValue == null && otherPart.getRightValue() == null)
          || (rightValue != null && otherPart.getRightValue() != null && rightValue.equals(otherPart.getRightValue()))) {
        rightValsMatch = true;
      }
      return feature.equals(otherPart.getFeature()) && leftValsMatch && rightValsMatch;
    }

    @Override
    public int hashCode() {
      int base = feature.hashCode();
      if (leftValue == null) {
        base = base ^ "leftValNull".hashCode();
      }
      else {
        base = base ^ leftValue.hashCode();
      }

      if (rightValue == null) {
        base = base ^ "rightValNull".hashCode();
      }
      else {
        base = base ^ rightValue.hashCode();
      }
      return base;
    }

  }

  class TwoSidedDiffImpl extends BaseDiffImpl implements TwoSidedDiff {
    private final UniversalModelExpression leftExpression;
    private final UniversalModelExpression rightExpression;
    private final Set<DiffPart>            diffParts;

    TwoSidedDiffImpl(UniversalTypeExpression type, UniversalModelExpression leftExpression, UniversalModelExpression rightExpression) {
      super(type);
      this.leftExpression = leftExpression;
      this.rightExpression = rightExpression;
      this.diffParts = Sets.newHashSet();
    }

    void addDiffPart(FeatureExpression<?> feature, Object leftValue, Object rightValue) {
      FeatureExpression<?> actualFeature = getType().findFeatureByPersistentName(feature.getPersistentName());
      if (actualFeature == null) {
        throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "The feature " + feature.getPersistentName()
            + " is undefiend for the universal type " + getType().getPersistentName());
      }
      diffParts.add(new DiffPartImpl(feature, leftValue, rightValue));
    }

    /**{@inheritDoc}**/
    public UniversalModelExpression getLeftExpression() {
      return leftExpression;
    }

    /**{@inheritDoc}**/
    public UniversalModelExpression getRightExpression() {
      return rightExpression;
    }

    /**{@inheritDoc}**/
    public Set<DiffPart> getDiffParts() {
      return diffParts;
    }

  }

  private class SingleSidedDiffImpl extends BaseDiffImpl implements SingleSidedDiff {

    private final UniversalModelExpression expression;

    SingleSidedDiffImpl(UniversalTypeExpression type, UniversalModelExpression expression) {
      super(type);
      this.expression = expression;
    }

    /**{@inheritDoc}**/
    public UniversalModelExpression getExpression() {
      return expression;
    }
  }

  class LeftSidedDiffImpl extends SingleSidedDiffImpl implements LeftSidedDiff {
    LeftSidedDiffImpl(UniversalTypeExpression type, UniversalModelExpression expression) {
      super(type, expression);
    }
  }

  class RightSidedDiffImpl extends SingleSidedDiffImpl implements RightSidedDiff {
    RightSidedDiffImpl(UniversalTypeExpression type, UniversalModelExpression expression) {
      super(type, expression);
    }
  }

  abstract class BaseDiffImpl implements BaseDiff {

    private final UniversalTypeExpression type;
    private DiffBuilderResult             owner;

    BaseDiffImpl(UniversalTypeExpression type) {
      this.type = type;
    }

    void setOwner(DiffBuilderResultImpl owner) {
      this.owner = owner;
    }

    /**{@inheritDoc}**/
    public DiffBuilderResult getOwner() {
      return owner;
    }

    /**{@inheritDoc}**/
    public UniversalTypeExpression getType() {
      return type;
    }
  }

}
