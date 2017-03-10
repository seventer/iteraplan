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
package de.iteratec.iteraplan.common;

import junit.framework.TestCase;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * @author Tobias Dietl (iteratec GmbH)
 * @version $Id: $
 */
public class CycleCheckTest extends TestCase {

  public void testGeneralisationCycleCheckNo() {
    // arrange:
    BusinessObject a = new BusinessObject();
    a.setId(Integer.valueOf(1));
    BusinessObject b = new BusinessObject();
    b.setId(Integer.valueOf(2));
    BusinessObject c = new BusinessObject();
    c.setId(Integer.valueOf(3));

    b.addGeneralisation(a);
    c.addGeneralisation(b);

    // act & assert:
    boolean cycle = HierarchyHelper.hasGeneralisationCycle(b);
    assertFalse(cycle);
  }

  public void testGeneralisationCycleCheckYes() {
    // arrange:
    BusinessObject a = new BusinessObject();
    a.setId(Integer.valueOf(1));
    BusinessObject b = new BusinessObject();
    b.setId(Integer.valueOf(2));
    BusinessObject c = new BusinessObject();
    c.setId(Integer.valueOf(3));

    b.addGeneralisation(a);
    c.addGeneralisation(b);
    try {
      a.addGeneralisation(c);
      fail("A cycle should have been detected and an according exception thrown.");
    } catch (IteraplanBusinessException e) {
      // everything okay, this should happen
      assertEquals(IteraplanErrorMessages.GENERALISATION_HIERARCHY_CYCLE_BO, e.getErrorCode());
    }

    // act & assert:
    boolean cycle = HierarchyHelper.hasGeneralisationCycle(c);
    assertFalse(cycle);
  }

  public void testElementOfCycleCheckNo() {
    // arrange:
    BusinessObject a = new BusinessObject();
    a.setId(Integer.valueOf(1));
    BusinessObject b = new BusinessObject();
    b.setId(Integer.valueOf(2));
    BusinessObject c = new BusinessObject();
    c.setId(Integer.valueOf(3));

    b.addParent(a);
    c.addParent(a);

    // act & assert:
    boolean cycle;
    cycle = HierarchyHelper.hasElementOfCycle(b);
    assertFalse(cycle);
    cycle = HierarchyHelper.hasElementOfCycle(c);
    assertFalse(cycle);
  }

  public void testElementOfCycleCheckYes() {
    // arrange:
    BusinessObject a = new BusinessObject();
    a.setId(Integer.valueOf(1));
    BusinessObject b = new BusinessObject();
    b.setId(Integer.valueOf(2));
    BusinessObject c = new BusinessObject();
    c.setId(Integer.valueOf(3));

    try {
      b.addParent(a);
      c.addParent(b);
      // this will throw an ELEMENT_OF_HIERARCHY_CYCLE exception.
      a.addParent(c);
      fail("A cycle should have been detected and an according exception thrown.");
    } catch (IteraplanBusinessException e) {
      // we catch the exception here and leave the object in an inconsistent state to check for the
      // cycle again
      assertEquals(IteraplanErrorMessages.ELEMENT_OF_HIERARCHY_CYCLE, e.getErrorCode());
    }

    // the resulting hierarchy should have no cycle anymore
    boolean cycle = HierarchyHelper.hasElementOfCycle(a);
    assertFalse(cycle);
  }
}
