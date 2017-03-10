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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilderResult;
import de.iteratec.iteraplan.elasticeam.model.compare.LeftSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchResult;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.impl.DiffBuilderImpl.BaseDiffImpl;


public class DiffBuilderResultImpl implements DiffBuilderResult {

  private final MatchResult                                 matchResult;
  private final List<String>                                warnings = Lists.newArrayList();
  private final Map<UniversalTypeExpression, Set<BaseDiff>> diffs;

  protected DiffBuilderResultImpl(MatchResult matchResult) {
    this.matchResult = matchResult;
    this.diffs = new HashMap<UniversalTypeExpression, Set<BaseDiff>>();
  }

  protected void addDiff(BaseDiffImpl diff) {
    UniversalTypeExpression actualType = matchResult.getMetamodel().findUniversalTypeByPersistentName(diff.getType().getPersistentName());
    if (actualType == null) {
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "The universal type " + diff.getType().getPersistentName()
          + " does not belong to the metamodel of this diff builder.");
    }

    if (diffs.get(actualType) == null) {
      diffs.put(actualType, new HashSet<BaseDiff>());
    }
    diff.setOwner(this);
    diffs.get(actualType).add(diff);
  }

  /**{@inheritDoc}**/
  public MatchResult getMatchResult() {
    return this.matchResult;
  }

  /**{@inheritDoc}**/
  public Set<BaseDiff> getDiffsByType(UniversalTypeExpression type) {
    if (diffs.get(type) != null) {
      return diffs.get(type);
    }
    return new HashSet<BaseDiff>();
  }

  /**{@inheritDoc}**/
  public Set<LeftSidedDiff> getLeftSidedDiffsByType(UniversalTypeExpression type) {

    Set<LeftSidedDiff> result = Sets.newHashSet();
    for (BaseDiff diff : getDiffsByType(type)) {
      if (diff instanceof LeftSidedDiff) {
        result.add((LeftSidedDiff) diff);
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  public Set<RightSidedDiff> getRightSidedDiffsByType(UniversalTypeExpression type) {

    Set<RightSidedDiff> result = Sets.newHashSet();
    for (BaseDiff diff : getDiffsByType(type)) {
      if (diff instanceof RightSidedDiff) {
        result.add((RightSidedDiff) diff);
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  public Set<TwoSidedDiff> getTwoSidedDiffsByType(UniversalTypeExpression type) {

    Set<TwoSidedDiff> result = Sets.newHashSet();
    for (BaseDiff diff : getDiffsByType(type)) {
      if (diff instanceof TwoSidedDiff) {
        result.add((TwoSidedDiff) diff);
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  public Map<UniversalTypeExpression, Set<BaseDiff>> getDiffs() {
    return diffs;
  }

  /**{@inheritDoc}**/
  public Map<UniversalTypeExpression, Set<LeftSidedDiff>> getLeftSidedDiffs() {
    Map<UniversalTypeExpression, Set<LeftSidedDiff>> result = new HashMap<UniversalTypeExpression, Set<LeftSidedDiff>>();
    for (UniversalTypeExpression type : diffs.keySet()) {
      result.put(type, getLeftSidedDiffsByType(type));
    }
    return result;
  }

  /**{@inheritDoc}**/
  public Map<UniversalTypeExpression, Set<RightSidedDiff>> getRightSidedDiffs() {
    Map<UniversalTypeExpression, Set<RightSidedDiff>> result = new HashMap<UniversalTypeExpression, Set<RightSidedDiff>>();
    for (UniversalTypeExpression type : diffs.keySet()) {
      result.put(type, getRightSidedDiffsByType(type));
    }
    return result;
  }

  /**{@inheritDoc}**/
  public Map<UniversalTypeExpression, Set<TwoSidedDiff>> getTwoSidedDiffs() {
    Map<UniversalTypeExpression, Set<TwoSidedDiff>> result = new HashMap<UniversalTypeExpression, Set<TwoSidedDiff>>();
    for (UniversalTypeExpression type : diffs.keySet()) {
      result.put(type, getTwoSidedDiffsByType(type));
    }
    return result;
  }

  /**{@inheritDoc}**/
  public List<String> getWarnings() {
    return warnings;
  }

  public void addWarning(String message) {
    warnings.add(message);
  }

}
