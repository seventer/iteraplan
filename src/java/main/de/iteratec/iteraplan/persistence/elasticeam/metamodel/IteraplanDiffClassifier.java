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
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.MMetamodelComparator.MMChange;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.MMetamodelComparator.MMChangeKind;


/**
 *
 */
public class IteraplanDiffClassifier {

  public static final Logger LOGGER = Logger.getIteraplanLogger(IteraplanDiffClassifier.class);

  private IteraplanMapping   mapping;

  /**
   * Default constructor.
   */
  public IteraplanDiffClassifier(IteraplanMapping mapping) {
    this.mapping = mapping;
  }

  public Multimap<TypeOfDiff, MMChange<?>> classifyDiffElements(Collection<MMChange<?>> diffs) {
    Multimap<TypeOfDiff, MMChange<?>> result = ArrayListMultimap.create();
    for (MMChange<?> diff : diffs) {
      if (diff.getAffectedElement() instanceof UniversalTypeExpression) {
        classifyUniversalTypeChanges(result, diff);
      }
      if (diff.getAffectedElement() instanceof RelationshipEndExpression) {
        classifyRelationshipEndChanges(result, diff);
      }
      if (diff.getAffectedElement() instanceof EnumerationExpression) {
        classifyEnumerationChange(result, diff);
      }
      if (diff.getAffectedElement() instanceof EnumerationLiteralExpression) {
        classifyLiteralChange(result, diff);
      }
      if (diff.getAffectedElement() instanceof PropertyExpression) {
        classifyPropertyChange(result, diff);
      }
    }
    return result;
  }

  private void classifyUniversalTypeChanges(Multimap<TypeOfDiff, MMChange<?>> result, MMChange<?> diff) {
    if (diff.getChangeKind() == MMChangeKind.ADD) {
      result.put(TypeOfDiff.UNKNOWN, diff);
    }
    else {
      result.put(TypeOfDiff.IGNORED, diff);
    }
  }

  private void classifyRelationshipEndChanges(Multimap<TypeOfDiff, MMChange<?>> result, MMChange<?> diff) {
    if (diff.getChangeKind() == MMChangeKind.ADD || diff.getChangeKind() == MMChangeKind.CHANGE) {
      result.put(TypeOfDiff.UNKNOWN, diff);
    }
    else {
      result.put(TypeOfDiff.IGNORED, diff);
    }
  }

  private void classifyEnumerationChange(Multimap<TypeOfDiff, MMChange<?>> result, MMChange<?> diff) {
    if (diff.getChangeKind() == MMChangeKind.ADD) {
      result.put(TypeOfDiff.ADD_ENUM, diff);
    }
    if (diff.getChangeKind() == MMChangeKind.DELETE) {
      if (this.mapping.getAdditionalEnumerationExpressions().containsKey(diff.getAffectedElement())) {
        result.put(TypeOfDiff.REMOVE_ENUM, diff);
      }
      else {
        result.put(TypeOfDiff.UNKNOWN, diff);
      }
    }
  }

  private void classifyLiteralChange(Multimap<TypeOfDiff, MMChange<?>> result, MMChange<?> diff) {
    EnumerationLiteralExpression affectedElement = (EnumerationLiteralExpression) diff.getAffectedElement();
    if (this.mapping.getAdditionalEnumerationExpressions().containsKey(affectedElement.getOwner())) {
      if (diff.getChangeKind() == MMChangeKind.ADD) {
        result.put(TypeOfDiff.ADD_ENUM_LITERAL, diff);
      }
      if (diff.getChangeKind() == MMChangeKind.DELETE) {
        result.put(TypeOfDiff.REMOVE_ENUM_LITERAL, diff);
      }
    }
    else {
      result.put(TypeOfDiff.UNKNOWN, diff);
    }
  }

  private void classifyPropertyChange(Multimap<TypeOfDiff, MMChange<?>> result, MMChange<?> diff) {
    if (diff.getChangeKind() == MMChangeKind.ADD) {
      result.put(TypeOfDiff.ADD_PROPERTY, diff);
    }
    if (diff.getChangeKind() == MMChangeKind.CHANGE) {
      result.put(TypeOfDiff.UNKNOWN, diff);
    }
    if (diff.getChangeKind() == MMChangeKind.DELETE && this.mapping.isDerivedFromAT((PropertyExpression) diff.getAffectedElement())) {
      result.put(TypeOfDiff.REMOVE_PROPERTY, diff);
    }
  }

  public static Collection<MMChange<?>> getDiffsToApply(Multimap<TypeOfDiff, MMChange<?>> classifiedDiffs) {
    List<MMChange<?>> result = Lists.newArrayList();
    for (Entry<TypeOfDiff, Collection<MMChange<?>>> entry : classifiedDiffs.asMap().entrySet()) {

      if (TypeOfDiff.IGNORED.compareTo(entry.getKey()) < 0) {
        result.addAll(entry.getValue());
      }
    }
    return result;
  }

  public static Collection<MMChange<?>> getUnapplicableDiffs(Multimap<TypeOfDiff, MMChange<?>> classifiedDiffs) {
    LOGGER.info("Getting unapplicable metamodel differences.");

    Collection<MMChange<?>> unapplicableDiffs = classifiedDiffs.get(TypeOfDiff.UNKNOWN);
    if (unapplicableDiffs == null) {
      unapplicableDiffs = Sets.newHashSet();
    }

    LOGGER.info("{0} unapplicable differences.", Integer.valueOf(unapplicableDiffs.size()));
    if (LOGGER.isDebugEnabled()) {
      for (MMChange<?> diff : unapplicableDiffs) {
        LOGGER.debug("- {0}", diff);
      }
    }
    return unapplicableDiffs;
  }

}
