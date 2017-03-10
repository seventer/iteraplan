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

/**
 * A utility which computes the differences between two models.
 */
public interface DiffBuilder {

  /**
   * Triggers the computation of the differences between two models.
   * @return
   *    A result, which contains all differences and supporting data structures
   *    obtained from the comparison of the two models.
   */
  DiffBuilderResult computeDifferences();

  /**
   * Sets the ruleset diffs are computed with. Standard is {@link DiffMode#ADDITIVE ADDITIVE}.
   * @param mode
   *          The {@link DiffMode} representing the differ ruleset
   */
  void setMode(DiffMode mode);

  /**
   * Modes representing a ruleset for a {@link DiffBuilder}
   */
  public enum DiffMode {
    /**
     * Only additive or changing diffs will be created:
     * adding of instances, related objects or property values;
     * changing of a related object to another one in single value relationships,
     * changing of a property value for single value properties.
     */
    ADDITIVE,
    /**
     * Property values and related objects can be removed from building blocks,
     * but no removal of building blocks should be possible.
     */
    V_PARTIAL,
    /**
     * All changes are applied, including all deletions.
     */
    STRICT;
  }
}
