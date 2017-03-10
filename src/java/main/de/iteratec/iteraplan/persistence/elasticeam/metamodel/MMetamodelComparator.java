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
package de.iteratec.iteraplan.persistence.elasticeam.metamodel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.TypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


/**
 *
 */
public final class MMetamodelComparator {

  public static enum MMChangeKind {
    ADD, DELETE, CHANGE
  }

  public static class MMChange<N extends NamedExpression> {

    private final N            affectedElement;
    private final MMChangeKind changeKind;

    protected MMChange(N affectedElement, MMChangeKind changeKind) {
      this.affectedElement = affectedElement;
      this.changeKind = changeKind;
    }

    public final N getAffectedElement() {
      return this.affectedElement;
    }

    public final MMChangeKind getChangeKind() {
      return this.changeKind;
    }

    public String toString() {
      return changeKind.name() + ": " + affectedElement;
    }
  }

  private static class MMMatch<T extends NamedExpression> {
    private T left;
    private T right;

    MMMatch(T left, T right) {
      this.left = left;
      this.right = right;
    }

    MMChange<T> getMMChange() {
      if (this.left == null) {
        return new MMChange<T>(this.right, MMChangeKind.ADD);
      }
      if (this.right == null) {
        return new MMChange<T>(this.left, MMChangeKind.DELETE);
      }

      if (this.left instanceof FeatureExpression) {
        FeatureExpression<?> leftFeature = (FeatureExpression<?>) this.left;
        FeatureExpression<?> rightFeature = (FeatureExpression<?>) this.right;
        if (!leftFeature.getType().getMetaType().equals(rightFeature.getType().getMetaType())
            || !leftFeature.getType().getPersistentName().equals(rightFeature.getType().getPersistentName())) {
          return new MMChange<T>(this.left, MMChangeKind.CHANGE);
        }
      }

      return null;
    }
  }

  private MMetamodelComparator() {
    //hide constructor
  }

  public static List<MMChange<?>> diff(Metamodel oldMM, Metamodel newMM) {
    List<MMChange<?>> result = Lists.newArrayList();

    for (MMMatch<?> match : computeMatches(oldMM, newMM)) {
      MMChange<?> change = match.getMMChange();
      if (change != null) {
        result.add(change);
      }
    }

    //TODO workaround for ITERAPLAN-1781: if a customer wants to import an excel containing only a limited number of sheets, this would cause omitted attributes to be removed from the metamodel. 
    // Since the DiffBuilder uses additive mode, this should be applied to the metamodel diff builder, too, for consistency reasons.
    // It would be nicer however, if instead of applying a filter after calculating the results, the MMetamodelComparator could be called in additive mode, too.
    // Also note that with the currently applied filter, new attributes can be added, even if this functionality is not desired in principle. 

    //filter the changes such that only types ADD and CHANGE remain.
    Collection<MMChange<?>> filtered = Collections2.filter(result, new Predicate<MMChange<?>>() {
      public boolean apply(MMChange<?> input) {
        switch (input.getChangeKind()) {
          case ADD:
            return true;
          case CHANGE:
            return true;
          case DELETE:
            return false;
          default:
            return false;
        }
      }
    });

    result = Lists.newArrayList();
    result.addAll(filtered);

    return result;
  }

  private static Collection<MMMatch<?>> computeMatches(Metamodel oldMM, Metamodel newMM) {
    Collection<MMMatch<?>> matches = Lists.newLinkedList();
    Collection<TypeExpression> matchedNewTypes = Sets.newHashSet();
    Map<TypeExpression, TypeExpression> matchedTypes = Maps.newHashMap();

    for (TypeExpression oldType : oldMM.getTypes()) {
      TypeExpression newType = newMM.findTypeByPersistentName(oldType.getPersistentName());
      if (newType == null) {
        matches.add(new MMMatch<NamedExpression>(oldType, null));
      }
      else if (oldType.getMetaType().equals(newType.getMetaType())) {
        matches.add(new MMMatch<TypeExpression>(oldType, newType));
        matchedTypes.put(oldType, newType);
        matchedNewTypes.add(newType);
      }
      else {
        matches.add(new MMMatch<NamedExpression>(oldType, null));
      }
    }
    for (TypeExpression newType : newMM.getTypes()) {
      if (!matchedNewTypes.contains(newType)) {
        matches.add(new MMMatch<TypeExpression>(null, newType));
      }
    }

    for (Entry<TypeExpression, TypeExpression> matchedType : matchedTypes.entrySet()) {
      if (matchedType.getKey() instanceof EnumerationExpression) {
        matchEnums(matchedType, matches);
      }
      else if (matchedType.getKey() instanceof UniversalTypeExpression) {
        matchUniversalTypes(matchedType, matches);
      }
    }

    return matches;
  }

  private static void matchEnums(Entry<TypeExpression, TypeExpression> enumMatch, Collection<MMMatch<?>> matches) {
    Collection<EnumerationLiteralExpression> matchedNewLiterals = Sets.newHashSet();
    for (EnumerationLiteralExpression oldLiteral : ((EnumerationExpression) enumMatch.getKey()).getLiterals()) {
      EnumerationLiteralExpression newLiteral = ((EnumerationExpression) enumMatch.getValue()).findLiteralByPersistentName(oldLiteral
          .getPersistentName());
      matches.add(new MMMatch<EnumerationLiteralExpression>(oldLiteral, newLiteral));
      if (newLiteral != null) {
        matchedNewLiterals.add(newLiteral);
      }
    }
    for (EnumerationLiteralExpression newLiteral : ((EnumerationExpression) enumMatch.getValue()).getLiterals()) {
      if (!matchedNewLiterals.contains(newLiteral)) {
        matches.add(new MMMatch<EnumerationLiteralExpression>(null, newLiteral));
      }
    }
  }

  private static void matchUniversalTypes(Entry<TypeExpression, TypeExpression> universalTypeMatch, Collection<MMMatch<?>> matches) {
    Collection<FeatureExpression<?>> matchedNewFeatures = Sets.newHashSet();
    for (FeatureExpression<?> oldFeature : ((UniversalTypeExpression) universalTypeMatch.getKey()).getFeatures()) {
      FeatureExpression<?> newFeature = ((UniversalTypeExpression) universalTypeMatch.getValue()).findFeatureByPersistentName(oldFeature
          .getPersistentName());
      if (newFeature == null) {
        matches.add(new MMMatch<NamedExpression>(oldFeature, null));
      }
      else if (oldFeature.getMetaType().equals(newFeature.getMetaType())) {
        matches.add(new MMMatch<NamedExpression>(oldFeature, newFeature));
        matchedNewFeatures.add(newFeature);
      }
      else {
        matches.add(new MMMatch<NamedExpression>(oldFeature, null));
      }
    }
    for (FeatureExpression<?> newFeature : ((UniversalTypeExpression) universalTypeMatch.getValue()).getFeatures()) {
      if (!matchedNewFeatures.contains(newFeature)) {
        matches.add(new MMMatch<NamedExpression>(null, newFeature));
      }
    }
  }

  public static List<String> getDiffInfoStrings(Collection<MMChange<? extends NamedExpression>> diffs) {
    List<String> unapplicableInfos = Lists.newArrayList();

    for (MMChange<?> diff : diffs) {
      String message = getInfoStringForDiff(diff);
      unapplicableInfos.add(message);
    }

    return unapplicableInfos;
  }

  public static String getInfoStringForDiff(MMChange<? extends NamedExpression> diff) {
    NamedExpression element = diff.getAffectedElement();

    MMChangeKind changeKind = diff.getChangeKind();
    String additionalInfo = "";

    StringBuilder builder = new StringBuilder();
    builder.append(changeKind.name());
    if (element instanceof EnumerationExpression) {
      builder.append(": Enumeration Attribute");
    }
    else if (element instanceof FeatureExpression) {
      String parentName = ((FeatureExpression<?>) element).getHolder().getName();
      builder.append(getChangeKindConnector(changeKind)).append(parentName).append(":");
      additionalInfo = getAdditionalInfoForFeatureExpressions((FeatureExpression<?>) element);
    }
    else if (element instanceof TypeExpression) {
      builder.append(": Type");
    }
    builder.append(" \"").append(element.getName()).append("\"").append(additionalInfo);

    return builder.toString();
  }

  private static String getAdditionalInfoForFeatureExpressions(FeatureExpression<?> element) {
    StringBuilder additionalInfo = new StringBuilder();
    additionalInfo.append(" [").append(element.getLowerBound()).append("..");

    int upperBound = element.getUpperBound();
    if (upperBound == PropertyExpression.UNLIMITED) {
      additionalInfo.append("*");
    }
    else {
      additionalInfo.append(upperBound);
    }

    additionalInfo.append("]");

    return additionalInfo.toString();
  }

  private static String getChangeKindConnector(MMChangeKind changeKind) {
    switch (changeKind) {
      case ADD:
        return " to ";
      case DELETE:
        return " from ";
      case CHANGE:
        return " on ";
      default:
        return " ";
    }
  }
}
