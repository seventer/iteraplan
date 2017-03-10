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

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 *
 */
public class NeighborhoodDiagramCreatorTest extends BaseTransactionalTestSupport {

  private static final Logger               LOGGER = Logger.getIteraplanLogger(NeighborhoodDiagramCreatorTest.class);

  private static InformationSystemRelease[] informationSystems;
  @Autowired
  private TestDataHelper2                   testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    informationSystems = NeighborDiagramDataCreator.createInformationSystems(testDataHelper);
  }

  @Test
  public void testNeighborhoodDiagram() {
    diagramInformationTest(informationSystems[0], 3);
    diagramInformationTest(informationSystems[7], 5);
    diagramInformationTest(informationSystems[10], 6);
    diagramInformationTest(informationSystems[11], 4);

  }

  private void diagramInformationTest(InformationSystemRelease isr, int connections) {
    NeighborhoodDiagramCreator creator = new NeighborhoodDiagramCreator(isr);
    NeighborhoodDiagram diagram = creator.createNeighborhoodDiagram();
    LOGGER.debug("Checking content");
    assertEquals("Count of expected connected Informationsystems differs. ", connections, diagram.getConnectedInformationSystems().size());
    LOGGER.debug("Checking placement");
    distanceCheck(diagram);
  }

  /**
   * Checks if the objects don't
   * @param diagram
   */
  private void distanceCheck(NeighborhoodDiagram diagram) {
    for (SpatialInfromationSystemWrapper sIsrA : diagram.getConnectedInformationSystems()) {
      for (SpatialInfromationSystemWrapper sIsrB : diagram.getConnectedInformationSystems()) {
        if (sIsrA != sIsrB
            && !(Math.abs(sIsrA.getCoordinate().getX() - sIsrB.getCoordinate().getX()) > NeighborhoodDiagram.BLOCK_WIDTH || Math.abs(sIsrA
                .getCoordinate().getY() - sIsrB.getCoordinate().getY()) > NeighborhoodDiagram.BLOCK_HEIGHT)) {
          Assert.fail("The created Blocks are to close to each other: ISR-1-Coords(" + sIsrA.getCoordinate().getX() + ","
              + sIsrA.getCoordinate().getY() + ") ISR-2-Coords(" + sIsrB.getCoordinate().getX() + "," + sIsrB.getCoordinate().getY()
              + "), Minimum Distance should be X: " + NeighborhoodDiagram.BLOCK_WIDTH + " or Y: " + NeighborhoodDiagram.BLOCK_HEIGHT);
        }
      }
    }
    for (SpatialInfromationSystemWrapper sIsrB : diagram.getConnectedInformationSystems()) {
      if (!(Math.abs(diagram.getObjectOfInterest().getCoordinate().getX() - sIsrB.getCoordinate().getX()) > NeighborhoodDiagram.BLOCK_WIDTH || Math
          .abs(diagram.getObjectOfInterest().getCoordinate().getY() - sIsrB.getCoordinate().getY()) > NeighborhoodDiagram.BLOCK_HEIGHT)) {
        Assert.fail("The created Blocks are to close to each other: ISR-1-Coords(" + diagram.getObjectOfInterest().getCoordinate().getX() + ","
            + diagram.getObjectOfInterest().getCoordinate().getY() + ") ISR-2-Coords(" + sIsrB.getCoordinate().getX() + ","
            + sIsrB.getCoordinate().getY() + "), Minimum Distance should be X: " + NeighborhoodDiagram.BLOCK_HEIGHT + " or Y: "
            + NeighborhoodDiagram.BLOCK_HEIGHT);
      }
    }
  }
}
