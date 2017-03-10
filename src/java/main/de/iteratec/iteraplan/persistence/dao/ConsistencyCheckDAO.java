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
import java.util.Map;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.persistence.util.Pair;


/**
 * DAO interface for consistency checks.
 */
public interface ConsistencyCheckDAO {

  /**
   * diverse aliases used in queries for accessing result maps, e.g. in @link
   * getTcReleasesUsingOtherTcrNotReleased()
   */
  String ISR_ALIAS            = "isr";
  String ISR_REF_ALIAS        = "referencedIsr";
  String BB_ALIAS             = "buildingBlock";
  String TCR_ALIAS            = "tcr";
  String TCR_BASE_ALIAS       = "baseTcr";
  String INFRASTR_ASSOC_ALIAS = "infrStrAssoc";
  String INFRASTR_ELEM_ALIAS  = "infrStrElemViaTCR";
  String BO_ALIAS             = "businessObject";
  String ISR_ASSOC_ALIAS      = "isrAssoc";
  String TRANS_ALIAS          = "transport";
  String ISI_ALIAS            = "interface";
  String ISR_A_ALIAS          = "informationSystem_A";
  String ISR_B_ALIAS          = "informationSystem_B";
  String ISI_A_ALIAS          = "interface_A";
  String ISI_B_ALIAS          = "interface_B";

  /**
   * Returns all (or of the given type) {@link BuildingBlock}s that are associated with one or more
   * {@link de.iteratec.iteraplan.model.attribute.AttributeValueAssignment}s, that are out of the
   * range defined in the corresponding number attribute types.
   * 
   * @param type
   *          The type, for which the building blocks shall be returned. If null, all building
   *          blocks are returned.
   * @return See method description.
   */
  List<BuildingBlock> getBuildingBlocksWithAttributeValueAssigmentsOutOfRange(TypeOfBuildingBlock type);

  /**
   * Returns all {@link InformationSystemInterface}s that connect two
   * {@link InformationSystemRelease}s that are not simultaneously active, as defined by their start
   * and end dates.
   * 
   * @return See method description.
   */
  List<InformationSystemInterface> getConnectionsWithUnsynchronizedInformationSystemReleases();

  /**
   * Returns all currently active {@link InformationSystemRelease}s, as defined by their start and
   * end dates, that don't have their status set to CURRENT.
   * 
   * @return See method description.
   */
  List<InformationSystemRelease> getInformationSystemReleasesActiveWithoutStatusCurrent();

  /**
   * Returns all {@link InformationSystemRelease}s that are not active anymore, as defined by their
   * start and end dates, and that don't have their status set to INACTIVE.
   * 
   * @return See method description.
   */
  List<InformationSystemRelease> getInformationSystemReleasesInactiveWithoutStatusInactive();

  /**
   * @return Returns all {@link InformationSystemRelease}s with status PLANNED, that are not part of
   *         any {@code Project}s.
   */
  List<InformationSystemRelease> getInformationSystemReleasesPlannedWithoutAssociatedProjects();

  /**
   * @return Returns all {@link InformationSystemRelease}s with status CURRENT, TARGET or INACTIVE,
   *         that are part of {@code Project}s.
   */
  List<InformationSystemRelease> getInformationSystemReleasesWithoutStatusPlannedButAssociatedToProjects();

  /**
   * Returns all {@link InformationSystemRelease}s that have a parent.
   * 
   * @return See method description.
   */
  List<InformationSystemRelease> getInformationSystemReleasesWithParents();

  /**
   * Returns all {@link InformationSystemRelease}s that have their status set to CURRENT or
   * INACTIVE, but have not yet been launched, as defined by their start and end dates.
   * 
   * @return See method description.
   */
  List<InformationSystemRelease> getInformationSystemReleasesWithStatusCurrentOrInactiveButNotYetLaunched();

  /**
   * Returns a list of object arrays of {@link de.iteratec.iteraplan.model.attribute.NumberAT}s and
   * {@link de.iteratec.iteraplan.model.attribute.NumberAV}s for the building block with the given
   * ID.
   * 
   * @param id
   *          The ID of the building block for which number attribute types and values shall be
   *          returned.
   * @return See method description.
   */
  List<Object[]> getNumberAttributeTypeAndValueForBuildingBlockID(Integer id);

  /**
   * Returns all {@link InformationSystemRelease}s for the information system with the given ID,
   * that have their status set to CURRENT.
   * 
   * @param id
   *          The ID of the information system for which the releases shall be returned.
   * @return See method description.
   */
  List<InformationSystemRelease> getReleasesWithStatusCurrentForInformationSystemID(Integer id);

  /**
   * @return All releases of the {@code TechnicalComponent} with the given ID and status CURRENT.
   */
  List<TechnicalComponentRelease> getReleasesWithStatusCurrentForTcID(Integer id);

  /**
   * Returns all currently active {@link TechnicalComponentRelease}s, as defined by their start and
   * end dates, that don't have their status set to CURRENT.
   * 
   * @return See method description.
   */
  List<TechnicalComponentRelease> getTcReleasesActiveWithoutStatusCurrent();

  /**
   * Returns all {@link TechnicalComponentRelease}s that are not active anymore, as defined by their
   * start and end dates, and that don't have their status set to INACTIVE or UNDEFINED.
   * 
   * @return See method description.
   */
  List<TechnicalComponentRelease> getTcReleasesInactiveWithoutStatusInactive();

  /**
   * Returns all {@link TechnicalComponentRelease}s that have their status set to CURRENT or
   * INACTIVE, but have not yet been launched, as defined by their start and end dates.
   * 
   * @return See method description.
   */
  List<TechnicalComponentRelease> getTcReleasesWithStatusCurrentOrInactiveButNotYetLaunched();

  /**
   * Returns all {@link TechnicalComponentRelease}s that have their status set to UNDEFINED.
   * 
   * @return See method description.
   */
  List<TechnicalComponentRelease> getTcReleasesWithStatusUndefined();

  /**
   * Returns all {@link TechnicalComponentRelease}s (@link TCR) which are using other, unreleased
   * Technical Components (@link TCR_BASE).
   * 
   * @return A list of mappings of the form @link TCR -> the technical component releases in
   *         question and @link TCR_BASE -> the base component of tcr which is not yet released.
   */
  List<Map<String, TechnicalComponentRelease>> getTcReleasesUsingOtherTcrNotReleased();

  /**
   * @return Returns a list of Object arrays. The first entry is an array of
   *         {@link InformationSystemRelease}s, the second is an array of
   *         {@link de.iteratec.iteraplan.model.Project}s. The projects' runtime start at an earlier
   *         date than their associated information systems' runtime.
   */
  List<Object[]> getUnsynchronizedProjectsWithInformationSystemReleases();

  /**
   * Returns a list of object arrays of {@link TechnicalComponentRelease}s and
   * {@link InformationSystemRelease}s, whereas the {@link TechnicalComponentRelease}s are not
   * continuously active throughout the entire productivity time span of their associated
   * information system releases, as defined by their start and end dates.
   * 
   * @return See method description.
   */
  List<Object[]> getUnsynchronizedTcAndIsReleases();

  /**
   * Returns a list of Information System Interfaces which connect active and inactive Information
   * Systems.
   * 
   * @return See method description.
   */
  List<InformationSystemInterface> getInformationSystemInterfacesConnectingCurrentAndNonCurrentInformationSystemReleases();

  /**
   * This consistency check checks if there are releases of information systems that are connected
   * with an interface, but there is any given time in which either is not active, while the other
   * is.
   * 
   * @return See method description.
   */
  List<InformationSystemInterface> getInformationSystemInterfacesConnectingCurrentAndNonCurrentInformationSystemReleases2();

  /**
   * Returns a list of Technical Component Releases sharing no Architectural Domain with their Base
   * Components
   * 
   * @return See method description.
   */
  List<Object[]> getTechnicalComponentReleasesSharingNoArchitecturalDomainWithTheirChildren();

  /**
   * Returns a list of Technical Component Releases sharing no Architectural Domain with their
   * Successors
   * 
   * @return See method description.
   */
  List<Object[]> getTechnicalComponentReleasesSharingNoArchitecturalDomainWithTheirSuccessors();

  /**
   * Returns the list of Information Systems which are not directly connected to Infrastructure
   * Elements, but only through an indirection over Technical Components
   * 
   * @return See method description.
   */
  List<Map<String, BuildingBlock>> getIsrConnectedToInfrastrElemViaTcrButNotDirectly();

  /**
   * Returns the list of Interfaces which are not directly connected to Information Systems, but
   * only through an indirection over Business Objects
   * 
   * @return See method description.
   */
  List<Map<String, BuildingBlock>> getIsiConnectedToInformationSystemViaBusinessObjectButNotDirectly();

  /**
   * Returns the list of Building Blocks which have been recently updated in the last
   * <code>days</code>.
   * 
   * @param type
   *          the type of building block
   * @param days
   *          The number of days into the past that shall be considered
   * @param recentlyUpdated
   *          updated vs. not updated
   * @return See method description.
   */
  List<BuildingBlock> getBuildingBlocksRecentlyUpdated(TypeOfBuildingBlock type, int days, boolean recentlyUpdated);

  /**
   * Returns the list of Building Blocks which don't have any association to other building blocks.
   * 
   * @param type
   *          the type of building block
   * @return See method description.
   */
  List<BuildingBlock> getBuildingBlocksWithNoAssociations(TypeOfBuildingBlock type);

  /**
   * Returns the list of Business Objects which are used in an information system 'X', but not
   * transported to other systems through interfaces of system 'X' .
   * 
   * @return See method description.
   */
  List<Pair<BusinessObject, InformationSystemRelease>> getBoUsedByInformationSystemButNotTransported();
}
