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
import de.iteratec.iteraplan.common.util.StringUtil;


/**
 * @author Tobias Dietl (iteratec GmbH)
 * @version $Id: $
 */
public class StringUtilTest extends TestCase {

  private static final Logger LOGGER = Logger.getIteraplanLogger(StringUtil.class);

  public void testRemoveNewLines() {
    String testWin = "ersteZeile\r\nzweiteZeile";
    String replacedWin = StringUtil.removeNewLines(testWin);
    LOGGER.debug("Windows: '" + testWin + "' -> '" + replacedWin + "'");
    assertEquals("ersteZeile zweiteZeile", replacedWin);

    String testUnix = "ersteZeile\nzweiteZeile";
    String replacedUnix = StringUtil.removeNewLines(testUnix);
    LOGGER.debug("Unix: '" + testUnix + "' -> '" + replacedUnix + "'");
    assertEquals("ersteZeile zweiteZeile", replacedUnix);

    String testUnix2 = "ersteZeile\r\rdritteZeile";
    String replacedUnix2 = StringUtil.removeNewLines(testUnix2);
    LOGGER.debug("Unix2: '" + testUnix2 + "' -> '" + replacedUnix2 + "'");
    assertEquals("ersteZeile  dritteZeile", replacedUnix2);
  }

  public void testRemoveIllegalXMLChars() {
    String legal1 = "this is a test";

    String illegal1 = legal1 + '\u0001';
    assertEquals(legal1, StringUtil.removeIllegalXMLChars(illegal1));

    // \t \r and \n are legal and should not be removed
    String legal3 = legal1 + '\t';
    assertFalse(legal1.equals(StringUtil.removeIllegalXMLChars(legal3)));
    assertEquals(legal1 + '\t', StringUtil.removeIllegalXMLChars(legal3));

    String legal4 = legal1 + '\r';
    assertFalse(legal1.equals(StringUtil.removeIllegalXMLChars(legal4)));
    assertEquals(legal1 + '\r', StringUtil.removeIllegalXMLChars(legal4));

    String legal5 = legal1 + '\n';
    assertFalse(legal1.equals(StringUtil.removeIllegalXMLChars(legal5)));
    assertEquals(legal1 + '\n', StringUtil.removeIllegalXMLChars(legal5));

    // test removing inside of string
    String illegal6 = legal1 + '\u0013' + legal1;
    assertTrue((legal1 + legal1).equals(StringUtil.removeIllegalXMLChars(illegal6)));
  }

  public void testRemovePathFromFileURIString() {
    String nochange = "this is a test";
    assertEquals(nochange, StringUtil.removePathFromFileURIString(nochange));

    String changed = "Unresolved reference 'BuildingBlockType_2'. (file:///C:/Programme/apache-tomcat-6.0.26/bin/iteraplanSerialization.xmi, 4, 220)";
    assertEquals("Unresolved reference 'BuildingBlockType_2'. (iteraplanSerialization.xmi, 4, 220)", StringUtil.removePathFromFileURIString(changed));

    String changed2 = "Unresolved reference 'BuildingBlockType_2'. (file:C:/Programme/apache-tomcat-6.0.26/bin/iteraplanSerialization.xmi, 4, 220)";
    assertEquals("Unresolved reference 'BuildingBlockType_2'. (iteraplanSerialization.xmi, 4, 220)", StringUtil.removePathFromFileURIString(changed2));

    // should not throw an exception
    StringUtil.removePathFromFileURIString(null);
  }
}
