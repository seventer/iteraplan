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
package de.iteratec.iteraplan.businesslogic.exchange.dimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.LineDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class StatusLinePatternTest extends TestCase {

  public void testInitialization() {

    LineDimension dimension = initializeDimension();
    StatusAdapter adapter = (StatusAdapter) dimension.getAdapter();
    assertFalse(adapter.hasUnspecificValue());

    List<String> values = adapter.getValues();
    assertEquals(values.size(), 4);
    assertEquals("Ist", values.get(0));
    assertEquals("Plan", values.get(1));
    assertEquals("Soll", values.get(2));
    assertEquals("Auﬂer Betrieb", values.get(3));

    assertEquals(dimension.getName(), MessageAccess.getStringOrNull("global.type_of_status", Locale.GERMAN));
  }

  public void testMappingValues() {
    LineDimension dimension = initializeDimension();

    assertEquals(dimension.getValue("Ist"), Integer.valueOf(0));
    assertEquals(dimension.getValue("Plan"), Integer.valueOf(1));
    assertEquals(dimension.getValue("Soll"), Integer.valueOf(2));
    assertEquals(dimension.getValue("Auﬂer Betrieb"), Integer.valueOf(3));
  }

  public void testInformationSystemMapping() {
    LineDimension dimension = initializeDimension();

    InformationSystemRelease system = new InformationSystemRelease();
    system.setTypeOfStatus(TypeOfStatus.CURRENT);
    assertEquals(dimension.getValue(system), Integer.valueOf(0));
    system.setTypeOfStatus(TypeOfStatus.PLANNED);
    assertEquals(dimension.getValue(system), Integer.valueOf(1));
    system.setTypeOfStatus(TypeOfStatus.TARGET);
    assertEquals(dimension.getValue(system), Integer.valueOf(2));
    system.setTypeOfStatus(TypeOfStatus.INACTIVE);
    assertEquals(dimension.getValue(system), Integer.valueOf(3));

  }

  private LineDimension initializeDimension() {
    StatusAdapter adapter = new StatusAdapter(Locale.GERMAN, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    LineDimension dimension = new LineDimension(adapter);
    dimension.init(getLineList(4));
    return dimension;
  }

  private List<Integer> getLineList(int size) {
    List<Integer> result = new ArrayList<Integer>(size);
    int counter = 0;
    while (counter < size) {
      result.add(Integer.valueOf(counter));
      counter++;
    }
    return result;
  }
}
