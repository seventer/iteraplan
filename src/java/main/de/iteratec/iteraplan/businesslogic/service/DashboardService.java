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
import java.util.Map;

import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.DashboardElementLists;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.BBAttribute;

/**
 * Service for calculating various properties and attributes, required to show dashboard graphics.
 */
public interface DashboardService {

  /**
   * @param elements
   * @return ordered Map containing a shorthand for each BBT and the number of elements of this type
   */
  Map<String, Integer> getNumberOfElementsMap(DashboardElementLists elements);

  /**
   * 
   * @return a filled DashboardElementLists object that contains loaded building block lists
   */
  DashboardElementLists getElementLists();

  /**
   * @param isrs the list of information systems
   * @return ordered Map containing the ISR status and the number of elements for each of the according status type
   */
  Map<String, Integer> getIsrStatusMap(List<InformationSystemRelease> isrs);
  
  /**
   * @param isrs the list of information systems
   * @return ordered Map containing the ISR status and the number of elements for each of the according status type
   */
  Map<String, Integer> getIsrSealStateMap(List<InformationSystemRelease> isrs);

  /**
   * @param tcrs the list of technical components
   * @return ordered Map containing the technical components status and the number of elements for each of the according status type
   */
  Map<String, Integer> getTechnicalComponentsStatusMap(List<TechnicalComponentRelease> tcrs);

  /**
   * @param tcrs the list of technical components
   * @return sorted Map containing technical components and the number of times they are used by isr, sorted by the number
   */
  Map<TechnicalComponentRelease, Integer> getTopUsedTcr(List<TechnicalComponentRelease> tcrs);

  /**
   * @param elements
   * @return clustered map of architectural domains containing ordered Maps that contain the TechnicalComponents ordered by the number of times the TCR are used by ISR
   */
  Map<ArchitecturalDomain, Map<TechnicalComponentRelease, Integer>> getTopUsedTcrByAdMap(DashboardElementLists elements);

  /**
   * @param isrs the list of information systems
   * @return sorted Map containing information systems and the number of interfaces they contain, sorted by the number
   */
  Map<InformationSystemRelease, Integer> getTopUsedIsr(List<InformationSystemRelease> isrs);

  List<BBAttribute> getSingleDimensionAttributes(List<BBAttribute> attributes);

  Map<String, Map<String, List<Integer>>> getValueMap(List<BBAttribute> bbAttribute, List<? extends BuildingBlock> bbBlist);
  
}
