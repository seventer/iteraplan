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
package de.iteratec.iteraplan.businesslogic.exchange.common.neighbor;

import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;


/**
 *
 */
public final class NeighborDiagramDataCreator {

  private static final String[]             VERSIONS    = { "0.9", "1.0", "1.1", "2.0" };
  private static final String[]             LABELS      = { "IS1", "IS2", "IS3", "IS4", "IS5", "IS6", "IS7", "IS8", "IS9", "IS10", "IS11", "IS12",
      "IS13"                                           };
  private static final String               DESCRIPTION = "Description";
  @Autowired
  private static TestDataHelper2 testDataHelper;

  private NeighborDiagramDataCreator() {

  }

  public static InformationSystemRelease[] createInformationSystems(TestDataHelper2 dataHelper) {
    testDataHelper = dataHelper;
    InformationSystemRelease[] informationSystems = new InformationSystemRelease[13];
    informationSystems[0] = createInformationSystem(LABELS[0], VERSIONS[0], TypeOfStatus.CURRENT);
    informationSystems[1] = createInformationSystem(LABELS[1], VERSIONS[0], TypeOfStatus.CURRENT);
    informationSystems[2] = createInformationSystem(LABELS[2], VERSIONS[0], TypeOfStatus.CURRENT);
    informationSystems[3] = createInformationSystem(LABELS[3], VERSIONS[0], TypeOfStatus.CURRENT);
    informationSystems[4] = createInformationSystem(LABELS[4], VERSIONS[0], TypeOfStatus.CURRENT);
    informationSystems[5] = createInformationSystem(LABELS[5], VERSIONS[0], TypeOfStatus.CURRENT);
    informationSystems[6] = createInformationSystem(LABELS[6], VERSIONS[1], TypeOfStatus.PLANNED);
    informationSystems[7] = createInformationSystem(LABELS[7], VERSIONS[1], TypeOfStatus.PLANNED);
    informationSystems[8] = createInformationSystem(LABELS[8], VERSIONS[1], TypeOfStatus.PLANNED);
    informationSystems[9] = createInformationSystem(LABELS[9], VERSIONS[1], TypeOfStatus.TARGET);
    informationSystems[10] = createInformationSystem(LABELS[10], VERSIONS[1], TypeOfStatus.TARGET);
    informationSystems[11] = createInformationSystem(LABELS[11], VERSIONS[1], TypeOfStatus.INACTIVE);
    informationSystems[12] = createInformationSystem(LABELS[12], VERSIONS[2], TypeOfStatus.INACTIVE);
    /**
     * OOI:
     * TypeOfStatus:CURRENT
     * 3 Connected ISR with status Current
     * 4 Other connected ISR
     */
    createInformationSystemInterface(informationSystems[0], informationSystems[1]);
    createInformationSystemInterface(informationSystems[0], informationSystems[2]);
    createInformationSystemInterface(informationSystems[0], informationSystems[3]);
    createInformationSystemInterface(informationSystems[0], informationSystems[6]);
    createInformationSystemInterface(informationSystems[0], informationSystems[8]);
    createInformationSystemInterface(informationSystems[0], informationSystems[9]);
    createInformationSystemInterface(informationSystems[0], informationSystems[12]);
    /**
     * OOI:
     * TypeOfStatus:PLANNED
     * 3 Connected ISR with status Current
     * 2 Connected ISR with status PLANNED
     * 2 Other connected ISR
     */
    createInformationSystemInterface(informationSystems[7], informationSystems[1]);
    createInformationSystemInterface(informationSystems[7], informationSystems[2]);
    createInformationSystemInterface(informationSystems[7], informationSystems[3]);
    createInformationSystemInterface(informationSystems[7], informationSystems[6]);
    createInformationSystemInterface(informationSystems[7], informationSystems[8]);
    createInformationSystemInterface(informationSystems[7], informationSystems[9]);
    createInformationSystemInterface(informationSystems[7], informationSystems[12]);

    /**
     * OOI:
     * TypeOfStatus:PLANNED
     * 3 Connected ISR with status Current
     * 2 Connected ISR with status PLANNED
     * 1 Connected ISR with status TARGET
     * 1 Other connected ISR
     */
    createInformationSystemInterface(informationSystems[10], informationSystems[1]);
    createInformationSystemInterface(informationSystems[10], informationSystems[2]);
    createInformationSystemInterface(informationSystems[10], informationSystems[3]);
    createInformationSystemInterface(informationSystems[10], informationSystems[6]);
    createInformationSystemInterface(informationSystems[10], informationSystems[8]);
    createInformationSystemInterface(informationSystems[10], informationSystems[9]);
    createInformationSystemInterface(informationSystems[10], informationSystems[12]);
    /**
     * OOI:
     * TypeOfStatus:PLANNED
     * 3 Connected ISR with status Current
     * 1 Connected ISR with status INACTIVE
     * 3 Other connected ISR
     */
    createInformationSystemInterface(informationSystems[11], informationSystems[1]);
    createInformationSystemInterface(informationSystems[11], informationSystems[2]);
    createInformationSystemInterface(informationSystems[11], informationSystems[3]);
    createInformationSystemInterface(informationSystems[11], informationSystems[6]);
    createInformationSystemInterface(informationSystems[11], informationSystems[8]);
    createInformationSystemInterface(informationSystems[11], informationSystems[9]);
    createInformationSystemInterface(informationSystems[11], informationSystems[12]);
    return informationSystems;

  }

  private static void createInformationSystemInterface(InformationSystemRelease isr1, InformationSystemRelease isr2) {
    testDataHelper.createInformationSystemInterface(isr1, isr2, null, DESCRIPTION);
  }

  private static InformationSystemRelease createInformationSystem(String name, String version, TypeOfStatus status) {
    InformationSystem is1 = testDataHelper.createInformationSystem(name);
    return testDataHelper.createInformationSystemRelease(is1, version, DESCRIPTION, "01.01.2001", "01.01.2002", status);
  }

}
