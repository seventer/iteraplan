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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.List;

import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Service interface for {@link TechnicalComponentRelease}s.
 */
public interface TechnicalComponentReleaseService extends BuildingBlockService<TechnicalComponentRelease, Integer>, ReleaseService {

  /**
   * Returns a list of {@link TechnicalComponentRelease}s that may be used for interfaces between
   * two information system releases.
   * <ul>
   * <li>The list is sorted by the names of the releases.</li>
   * <li>An optional list of releases may be excluded from the result.</li>
   * <li>It may be specified if inactive releases should be included in the resulting list.</li>
   * </ul>
   * 
   * @param toExclude The list of releases that should be excluded from the result. Set to null if not needed.
   * @param showInactive If false, all releases with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<TechnicalComponentRelease> eligibleForConnections(List<TechnicalComponentRelease> toExclude, boolean showInactive);

  /**
   * Returns a list of {@link TechnicalComponentRelease}s.
   * <ul>
   * <li>The list is sorted by the contained technical components' names and versions.</li>
   * <li>The list may be limited by an optional filter string which is applied to the technical components'
   * names and versions.</li>
   * <li>The technical component with the given ID, if specified, is included even if it doesn't match the
   * filter condition.</li>
   * <li>An optional list of technical components may be excluded from the result.</li>
   * <li>It may be specified if inactive technical components should be included in the resulting list.</li>
   * </ul>
   * @param toExclude The list of technical component releases that should be excluded from the result. Set to null if not needed.
   * @param showInactive If false, all technical component releases with status INACTIVE will be filtered out.
   * 
   * @return See method description.
   */
  List<TechnicalComponentRelease> filter(List<TechnicalComponentRelease> toExclude, boolean showInactive);

  /**
   * Checks whether an {@link de.iteratec.iteraplan.model.TechnicalComponent} exists that is
   * considered to be a duplicate of the technical component with the given identifier and name. A
   * duplicate is defined as a technical component that has got the given name but not the given
   * identifier. The identifier is used to distinguish between save and update operations.
   * 
   * @return {@code True}, if a technical component with the given identifier and name exists; otherwise {@code false} is returned.
   * @throws IllegalArgumentException If the supplied name is {@code null}.
   */
  boolean isDuplicateTechnicalComponent(String name, Integer identifier);

  /**
   * Returns a list of {@link TechnicalComponentRelease}s that may be potentially used by the
   * technical component release with the given ID.
   * <ul>
   * <li>Potential technical components are all technical components that are not used by this technical component and,
   * to avoid cyclic references, that don't use this technical component.</li>
   * <li>The list is sorted by the contained technical components' names and versions.</li>
   * <li>The list may be limited by an optional filter string which is applied to the technical components'
   * names and versions.</li>
   * <li>An optional list of technical components may be excluded from the result.</li>
   * <li>It may be specified if inactive technical components should be included in the resulting list.</li>
   * </ul>
   * 
   * @param id
   *          The ID of the technical component release for which used technical component releases should be retrieved. If null
   *          or less than 0, all matching technical component releases are returned. If greater than 0, all
   *          matching technical component releases that are not used by the given technical component release are returned.
   * @param toExclude The list of technical component releases that should be excluded from the result. Set to null if not needed.
   * @param showInactive If false, all technical component releases with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<TechnicalComponentRelease> validBaseComponents(Integer id, List<TechnicalComponentRelease> toExclude, boolean showInactive);

  /**
   * Returns a list of potential predecessors for the {@link TechnicalComponentRelease} with the
   * given ID.
   * <ul>
   * <li>Potential predecessors are all technical components that are not successors of the given technical component.</li>
   * <li>The list is sorted by the contained technical components' names and versions.</li>
   * <li>The list may be limited by an optional filter string which is applied to the technical components'
   * names and versions.</li>
   * <li>An optional list of technical components may be excluded from the result.</li>
   * <li>It may be specified if inactive technical components should be included in the resulting list.</li>
   * </ul>
   * 
   * @param id The ID of the technical component for which predecessors should be retrieved. If null or less
   *          than 0, all matching technical components are returned. If greater than 0, all matching
   *          technical components that are not successors of the given technical component are returned.
   * @param toExclude The list of technical components that should be excluded from the result. Set to null if not needed.
   * @param showInactive If false, all technical components with status INACTIVE will be filtered out.
   * @return See method signature.
   */
  List<TechnicalComponentRelease> validPredecessors(Integer id, List<TechnicalComponentRelease> toExclude, boolean showInactive);

  List<TechnicalComponentRelease> getTechnicalComponentReleasesBySearch(TechnicalComponent technicalComponent, boolean showInactive);

  /**
   * Must be called when creating a new TechnicalComponentRelease
   * 
   * @param technicalComponent
   * @param technicalComponentRelease
   */
  void validateDuplicate(TechnicalComponent technicalComponent, TechnicalComponentRelease technicalComponentRelease);

  /**
   * Loads and returns all instances of the entity from the database without defined ordering.
   * 
   * @param showInactive include inactive technical component releases
   * @return See method description.
   */
  List<TechnicalComponentRelease> loadElementList(boolean showInactive);
}