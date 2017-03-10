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
package de.iteratec.iteraplan.businesslogic.reports.query.postprocessing;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.reports.query.node.Comparator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Operation;
import de.iteratec.iteraplan.businesslogic.reports.query.node.OperationNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.PropertyLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.attribute.BBAttribute;


public class IncludeConnectedInformationSystemReleasesStrategyTest extends TestCase {

  @Test
  public void testOption() throws Exception {

    Date now = DateUtils.parseAsDate("01.01.2010", Locale.GERMAN);
    Date later = DateUtils.parseAsDate("01.01.2011", Locale.GERMAN);

    InformationSystemRelease r1 = new InformationSystemRelease();
    r1.setVersion("r1");
    r1.setId(Integer.valueOf(1));
    r1.setTypeOfStatus(TypeOfStatus.CURRENT);

    InformationSystemRelease r2 = new InformationSystemRelease();
    r2.setVersion("r2");
    r2.setId(Integer.valueOf(2));
    r2.setTypeOfStatus(TypeOfStatus.PLANNED);

    InformationSystemRelease r3 = new InformationSystemRelease();
    r3.setVersion("r3");
    r3.setId(Integer.valueOf(3));
    r3.setTypeOfStatus(TypeOfStatus.CURRENT);
    r3.setRuntimePeriod(new RuntimePeriod(now, null));

    InformationSystemInterface con1 = new InformationSystemInterface();
    con1.setId(Integer.valueOf(670));
    con1.connect(r1, r2);

    InformationSystemInterface con2 = new InformationSystemInterface();
    con1.setId(Integer.valueOf(671));
    con2.connect(r3, r1);

    Set<InformationSystemRelease> ipuSet = new HashSet<InformationSystemRelease>();
    ipuSet.add(r1);

    IncludeConnectedInformationSystemReleasesStrategy strategy = new IncludeConnectedInformationSystemReleasesStrategy(Integer.valueOf(0));
    OperationNode rootNode = new OperationNode(Operation.AND);

    // add query for current status
    InformationSystemReleaseTypeQu type = InformationSystemReleaseTypeQu.getInstance();
    String typeOfStatusProperty = type.getTypeOfStatusProperty();
    rootNode.addChild(new PropertyLeafNode(type, null, typeOfStatusProperty, Comparator.LIKE, InformationSystemRelease.TypeOfStatus.CURRENT,
        BBAttribute.FIXED_ATTRIBUTE_TYPE));

    // add queries for current timespan
    rootNode.addChild(new PropertyLeafNode(type, null, type.getEndDateProperty(), Comparator.GEQ, now, BBAttribute.FIXED_ATTRIBUTE_DATETYPE));
    rootNode.addChild(new PropertyLeafNode(type, null, type.getStartDateProperty(), Comparator.LEQ, now, BBAttribute.FIXED_ATTRIBUTE_DATETYPE));

    // test without stateOption -> all conected releases expected
    Set<InformationSystemRelease> result = strategy.process(ipuSet, rootNode);

    assertTrue(result.contains(r1));
    assertTrue(result.contains(r2));
    assertTrue(result.contains(r3));

    OptionConsiderStateAndDate option = strategy.getAdditionalOptions().get(0);
    option.setSelected(true);

    // test with option -> r1 and r3 is expected due to their state
    result = strategy.process(ipuSet, rootNode);

    assertTrue(result.contains(r1));
    assertFalse(result.contains(r2));
    assertTrue(result.contains(r3));

    r3.setRuntimePeriod(new RuntimePeriod(later, null));
    // test with option -> r3 is not expected due to its date
    result = strategy.process(ipuSet, rootNode);
    assertTrue(result.contains(r1));
    assertFalse(result.contains(r2));
    assertFalse(result.contains(r3));
  }
}
