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
package de.iteratec.iteraplan.elasticeam.model.compare;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


/**
 * Represents the result of computing the differences between two models
 * on the basis of a given {@link MatchResult}.
 */
public interface DiffBuilderResult {

  /**
   * List of warn messages which occured while calculating diffs.
   * @return The warnings which occurred, or empty list if there were none.
   */
  List<String> getWarnings();

  /**
   * Returns the matches from which this diff result was computed.
   * @return
   *    The {@link MatchResult} from which this diff result was computed.
   */
  MatchResult getMatchResult();

  /**
   * Returns the set of (both single sided and two sided) diffs.
   * @param type
   *    The universal type the diffs of whose instances are to be retrieved.
   * @return
   *    Set of {@link BaseDiff}s. Not null but may be empty.
   */
  Set<BaseDiff> getDiffsByType(UniversalTypeExpression type);

  /**
   * Returns the set of left sided diffs. By convention, the left sided diffs are
   * the ones to be deleted, since the left model is the one containing the
   * existing data.
   * @param type
   *    The universal type the diffs of whose instances are to be retrieved.
   * @return
   *    Set of {@link LeftSidedDiff}s. Not <b>null</b> but may be empty.
   */
  Set<LeftSidedDiff> getLeftSidedDiffsByType(UniversalTypeExpression type);

  /**
   * Returns the set of right sided diffs. By convention, the right sided diffs represent
   * new instances to be added, since they contain expressions found
   * only in the right model, which is the one containing the updated data.
   * @param type
   *    The universal type the diffs of whose instances are to be retrieved.
   * @return
   *    Set of {@link RightSidedDiff}s. Not <b>null</b> but may be empty.
   */
  Set<RightSidedDiff> getRightSidedDiffsByType(UniversalTypeExpression type);

  /**
   * Returns the set of two-sided diffs. These diffs enclose expressions which 
   * were found in both the left (original) and right (updated) models.
   * @param type
   *    The universal type the diffs of whose instances are to be retrieved.
   * @return
   *    Set of {@link TwoSidedDiff}s. Not <b>null</b> but may be empty.
   */
  Set<TwoSidedDiff> getTwoSidedDiffsByType(UniversalTypeExpression type);

  /**
   * Returns a mapping from universal types to all of their diffs.
   * @return
   *    Map from {@link UniversalTypeExpression}s to all of their {@link BaseDiff}s.
   */
  Map<UniversalTypeExpression, Set<BaseDiff>> getDiffs();

  /**
   * Returns a mapping from universal types to all of their left sided diffs.
   * @return
   *    Map from {@link UniversalTypeExpression}s to all of their {@link LeftSidedDiff}s.
   */
  Map<UniversalTypeExpression, Set<LeftSidedDiff>> getLeftSidedDiffs();

  /**
   * Returns a mapping from universal types to all of their right sided diffs.
   * @return
   *    Map from {@link UniversalTypeExpression}s to all of their {@link RightSidedDiff}s.
   */
  Map<UniversalTypeExpression, Set<RightSidedDiff>> getRightSidedDiffs();

  /**
   * Returns a mapping from universal types to all of their two sided diffs.
   * @return
   *    Map from {@link UniversalTypeExpression}s to all of their {@link TwoSidedDiff}s.
   */
  Map<UniversalTypeExpression, Set<TwoSidedDiff>> getTwoSidedDiffs();

}
