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
package de.iteratec.iteraplan.common.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.InformationSystemDomain;


/**
 * @author Tobias Dietl (iteratec GmbH)
 * @version $Id: $
 */
public class VBStyleCollectionTest extends TestCase {

  private static final Logger LOGGER                  = Logger.getIteraplanLogger(VBStyleCollectionTest.class);

  private static final String TEST_ISD_NAME_1         = "id1";
  private static final String TEST_ISD_NAME_2         = "id2";
  private static final String TEST_ISD_NAME_3         = "id3";

  private static final String WRONG_ELEMENT_ERROR_MSG = "wrong element found!";

  public void testCollectionConstructor() {
    // arrange:
    List<InformationSystemDomain> identityEntities = new ArrayList<InformationSystemDomain>();
    InformationSystemDomain id1 = new InformationSystemDomain();
    id1.setId(Integer.valueOf(1));
    id1.setName(TEST_ISD_NAME_1);
    identityEntities.add(id1);
    InformationSystemDomain id2 = new InformationSystemDomain();
    id2.setId(Integer.valueOf(2));
    id2.setName(TEST_ISD_NAME_2);
    identityEntities.add(id2);

    // act and assert:
    VBStyleCollection<Integer, InformationSystemDomain> vbc = new IndexedIdEntityList<InformationSystemDomain>(identityEntities);
    LOGGER.debug(vbc.toString());
    assertEquals("wrong number of elements!", 2, vbc.size());
    assertEquals(WRONG_ELEMENT_ERROR_MSG, TEST_ISD_NAME_1, vbc.getWithKey(Integer.valueOf(1)).getName());
    assertEquals(WRONG_ELEMENT_ERROR_MSG, TEST_ISD_NAME_2, vbc.getWithKey(Integer.valueOf(2)).getName());
    assertNull("null expected!", vbc.getWithKey(Integer.valueOf(3)));
  }

  @SuppressWarnings("boxing")
  public void testVBStyleCollectionRemove() {

    VBStyleCollection<Integer, InformationSystemDomain> original = new VBStyleCollection<Integer, InformationSystemDomain>();

    InformationSystemDomain id1 = new InformationSystemDomain();
    id1.setId(1);
    id1.setName(TEST_ISD_NAME_1);
    original.addWithKey(id1, 1000);

    InformationSystemDomain id2 = new InformationSystemDomain();
    id2.setId(2);
    id2.setName(TEST_ISD_NAME_2);
    original.addWithKey(id2, 2000);

    InformationSystemDomain id3 = new InformationSystemDomain();
    id3.setId(3);
    id3.setName(TEST_ISD_NAME_3);
    original.addWithKey(id3, 3000);

    VBStyleCollection<Integer, InformationSystemDomain> copy = new VBStyleCollection<Integer, InformationSystemDomain>(original);

    LOGGER.debug("old VBStyleCollection before: " + original.toString());
    original.remove(1);
    original.sort();
    copy.sort();
    LOGGER.debug("old VBStyleCollection after: " + original.toString());

    assertEquals("wrong number of elements!", 2, original.size());
    assertEquals(WRONG_ELEMENT_ERROR_MSG, TEST_ISD_NAME_1, original.get(0).getName());
    assertEquals(WRONG_ELEMENT_ERROR_MSG, TEST_ISD_NAME_3, original.get(1).getName());

    LOGGER.debug("new VBStyleCollection: " + copy.toString());

    assertEquals("wrong number of elements!", 3, copy.size());
    assertEquals(WRONG_ELEMENT_ERROR_MSG, TEST_ISD_NAME_1, copy.get(0).getName());
    assertEquals(WRONG_ELEMENT_ERROR_MSG, TEST_ISD_NAME_2, copy.get(1).getName());
    assertEquals(WRONG_ELEMENT_ERROR_MSG, TEST_ISD_NAME_3, copy.get(2).getName());
  }

}
