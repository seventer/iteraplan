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
package de.iteratec.iteraplan.persistence.dao;

import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * DAO interface for {@link InformationSystemRelease}s.
 */
public interface InformationSystemReleaseDAO extends DAOTemplate<InformationSystemRelease, Integer> {

  /**
   * Checks if the information system release with the specified {@code name} and {@code version} exists.
   * 
   * @param name the release name
   * @param version the release version
   * @return {@code true} if such information system release exists, {@code false} otherwise
   * @throws IllegalArgumentException if the name equals {@code null}
   */
  boolean doesReleaseExist(String name, String version);

  /**
   * Returns a list of {@link InformationSystemRelease}s.
   * <ul>
   * <li>An optional list of information systems may be excluded.</li>
   * <li>It may be specified if inactive information systems should be included in the resulting
   * list.</li>
   * </ul>
   * 
   * @param toExclude
   *          The list of information systems that should be excluded from the result. Set to null
   *          if not needed.
   * @param showInactive
   *          If false, all information systems with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<InformationSystemRelease> filter(final List<InformationSystemRelease> toExclude, final boolean showInactive);

  /**
   * Returns a list of {@link InformationSystemRelease}s.
   * <ul>
   * <li>An optional list of information system ids may be excluded.</li>
   * <li>It may be specified if inactive information systems should be included in the resulting
   * list.</li>
   * </ul>
   * 
   * @param toExclude
   *          The list of information systems ids that should be excluded from the result. Set to null
   *          if not needed.
   * @param showInactive
   *          If false, all information systems with status INACTIVE will be filtered out.
   * @return See method description.
   */
  List<InformationSystemRelease> filterWithIds(final Set<Integer> toExclude, boolean showInactive);

  /**
   * Returns all Ipureleases that match the filter and that do have connections.
   * @param showInactive
   *          If set to false, only Ipureleases with active connections are returned.
   * 
   * @return List of {@link InformationSystemRelease}s.
   */
  List<InformationSystemRelease> getInformationSystemReleasesWithConnections(final boolean showInactive);

  /**
   * Gets all information system releases that are not contained in another. This means
   * getElementOfIpurelease() returns null.
   * 
   * @return The list of all outermost information system releases. Not null.
   */
  List<InformationSystemRelease> getOutermostInformationSystemReleases();

  boolean isDuplicateInformationSystem(String name, Integer identifier);

  /**
   * Returns all Informationsystems that have either a successor or a predecessor.
   * 
   * @param showInactive
   *          If set to false, only Informationsystems with active connections are returned.
   * @return List of {@link InformationSystemRelease}s.
   */
  List<InformationSystemRelease> getReleasesWithSuccessors(final boolean showInactive);

}