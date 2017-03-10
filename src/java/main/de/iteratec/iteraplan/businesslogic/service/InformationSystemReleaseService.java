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

import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * Service interface for {@link InformationSystemRelease}s.
 */
public interface InformationSystemReleaseService extends BuildingBlockService<InformationSystemRelease, Integer>, ReleaseService {

  /**
   * Returns a list of available parent {@link InformationSystemRelease}s for the information system
   * with the given ID.
   * <ul>
   * <li>The resulting list is sorted by the hierarchical name.</li>
   * <li>It may be specified if inactive information systems should be included in the resulting
   * list.</li>
   * </ul>
   * 
   * @param id The ID of the currently selected information system domain.
   * @param showInactive If false, all information systems with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<InformationSystemRelease> getAvailableParentReleases(Integer id, boolean showInactive);

  /**
   * Returns a list of {@link InformationSystemRelease}s.
   * <ul>
   * <li>The list is sorted by the contained information systems' hierachical names.</li>
   * <li>The list may be limited by an optional filter string which is applied to the hierarchical
   * name.</li>
   * <li>The information system with the given ID, if specified, is included even if it doesn't
   * match the filter condition.</li>
   * <li>An optional list of information systems may be excluded from the result.</li>
   * <li>It may be specified if inactive information systems should be included in the resulting
   * list.</li>
   * </ul>
   * @param elementsToExclude The list of information systems that should be excluded from the result. Set to null
   *          if not needed.
   * @param showInactive If false, all information systems with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<InformationSystemRelease> getInformationSystemsFiltered(List<InformationSystemRelease> elementsToExclude, boolean showInactive);

  /**
   * Returns all information system releases that are not contained in another information system
   * release. This effectively means that {@link InformationSystemRelease#getParent()} returns null
   * for each returned entity.
   * 
   * @return See method description.
   */
  List<InformationSystemRelease> getOutermostInformationSystemReleases();

  /**
   * Returns a list of potential predecessors for the {@link InformationSystemRelease} with the
   * given ID.
   * <ul>
   * <li>Potential predecessors are all information systems that are not successors of the given
   * information system.</li>
   * <li>The resulting list is sorted by the hierarchical name.</li>
   * <li>An optional list of information systems may be excluded from the result.</li>
   * <li>It may be specified if inactive information systems should be included in the resulting
   * list.</li>
   * </ul>
   * 
   * @param id The ID of the information system for which predecessors should be retrieved. If null
   *          or less than 0, all matching information systems are returned. If greater than 0, all
   *          matching information systems that are not successors of the given information system
   *          are returned.
   * @param elementsToExclude The list of information systems that should be excluded from the result. Set to null
   *          if not needed.
   * @param showInactive If false, all information systems with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<InformationSystemRelease> getValidPredecessors(Integer id, List<InformationSystemRelease> elementsToExclude, boolean showInactive);

  /**
   * Returns a list of potential successors for the {@link InformationSystemRelease} with the
   * given ID.
   * <ul>
   * <li>Potential successors are all information systems that are not predecessors of the given
   * information system.</li>
   * <li>The resulting list is sorted by the hierarchical name.</li>
   * <li>An optional list of information systems may be excluded from the result.</li>
   * <li>It may be specified if inactive information systems should be included in the resulting
   * list.</li>
   * </ul>
   * 
   * @param id The ID of the information system for which successors should be retrieved. If null
   *          or less than 0, all matching information systems are returned. If greater than 0, all
   *          matching information systems that are not predecessors of the given information system
   *          are returned.
   * @param elementsToExclude The list of information systems that should be excluded from the result. Set to null
   *          if not needed.
   * @param showInactive If false, all information systems with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<InformationSystemRelease> getValidSuccessors(Integer id, List<InformationSystemRelease> elementsToExclude, boolean showInactive);

  /**
   * Checks whether an {@link de.iteratec.iteraplan.model.InformationSystem} exists that is
   * considered to be a duplicate of the information system with the given identifier and name. A
   * duplicate is defined as an information system that has got the given name but not the given
   * identifier. The identifier is used to distinguish between save and update operations.
   * 
   * @return {@code True}, if an information system with the given identifier and name exists;
   *         otherwise {@code false} is returned.
   * @throws IllegalArgumentException If the supplied name is {@code null}.
   */
  boolean isDuplicateInformationSystem(String name, Integer identifier);

  /**
   * Returns a list of valid children of the given source entity. Valid children are defined as all
   * elements that are no direct or indirect parents of the given element, including the top-level
   * element. Furthermore, the list may be limited by an optional filter string which applies to the
   * hierarchical name as well as an optional list of entities that may be excluded from the list.
   * 
   * @param id The id of the entity for which valid children shall be returned.
   * @param elementsToExclude The list of entities that should be excluded from the result. Set to {@code null} if not needed.
   * @return See method description.
   */
  List<InformationSystemRelease> getAvailableChildren(Integer id, List<InformationSystemRelease> elementsToExclude, boolean showInactive);

  List<InformationSystemRelease> getInformationSystemReleasesBySearch(InformationSystem informationSystem, boolean showInactive);

  /**
   * Must be called when creating a new InformationSystemRelease
   * 
   * @param informationSystem
   * @param informationSystemRelease
   */
  void validateDuplicate(InformationSystem informationSystem, InformationSystemRelease informationSystemRelease);

  /**
   * Returns a list of {@link InformationSystemRelease}s that may be potentially used by the
   * information system release with the given ID.
   * <ul>
   * <li>Potential ISRs are all technical ISs that are not used by this ISR and,
   * to avoid cyclic references, that don't use this ISR.</li>
   * <li>The list is sorted by the contained ISRs' names and versions.</li>
   * <li>The list may be limited by an optional filter string which is applied to the ISRs'
   * names and versions.</li>
   * <li>An optional list of ISRs may be excluded from the result.</li>
   * <li>It may be specified if inactive ISRs should be included in the resulting list.</li>
   * </ul>
   * 
   * @param id The ID of the ISR for which used technical ISRs should be retrieved. If null
   *          or less than 0, all matching ISRs are returned. If greater than 0, all
   *          matching ISRs that are not used by the given ISR are returned.
   * @param toExclude The list of ISRs that should be excluded from the result. Set to null if not needed.
   * @param showInactive If false, all ISRs with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<InformationSystemRelease> validBaseComponents(Integer id, List<InformationSystemRelease> toExclude, boolean showInactive);

  /**
   * Returns a list of {@link InformationSystemRelease}s that may potentially use the
   * information system release with the given ID.
   * <ul>
   * <li>Potential ISRs are all technical ISs that don't use this ISR and,
   * to avoid cyclic references, that are not used by this ISR.</li>
   * <li>The list is sorted by the contained ISRs' names and versions.</li>
   * <li>The list may be limited by an optional filter string which is applied to the ISRs'
   * names and versions.</li>
   * <li>An optional list of ISRs may be excluded from the result.</li>
   * <li>It may be specified if inactive ISRs should be included in the resulting list.</li>
   * </ul>
   * 
   * @param id The ID of the ISR for which the using technical ISRs should be retrieved. If null
   *          or less than 0, all matching ISRs are returned. If greater than 0, all
   *          matching ISRs that are not used by the given ISR are returned.
   * @param toExclude The list of ISRs that should be excluded from the result. Set to null if not needed.
   * @param showInactive If false, all ISRs with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<InformationSystemRelease> validParentComponents(Integer id, List<InformationSystemRelease> toExclude, boolean showInactive);

  /**
   * Loads and returns all instances of the entity from the database without defined ordering.
   * 
   * @param showInactive include inactive information system releases
   * @return See method description.
   */
  List<InformationSystemRelease> loadElementList(boolean showInactive);
}
