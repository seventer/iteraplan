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
package de.iteratec.iteraplan.model.xml.query;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;


public class ValidationHelperTest {

  @Test
  public final void testValidateColors() {
    List<String> colorAttributeValuesTestList = ImmutableList.of("test value 1", "test value 2", "test value 3", "test value 4");
    List<String> selectedColorsTestList = ImmutableList.of("#000000", "#AA0000", "#00AA00", "#0000AA");

    String errorMsgTest = ValidationHelper.validateColors(colorAttributeValuesTestList, selectedColorsTestList);
    assertEquals("There should be no error message.", null, errorMsgTest);

    errorMsgTest = ValidationHelper.validateColors(colorAttributeValuesTestList, null);
    assertEquals("Either the color attribute values or the selected colors are not set", errorMsgTest);

    errorMsgTest = ValidationHelper.validateColors(null, selectedColorsTestList);
    assertEquals("Either the color attribute values or the selected colors are not set", errorMsgTest);

    selectedColorsTestList = ImmutableList.of("#000000");
    errorMsgTest = ValidationHelper.validateColors(colorAttributeValuesTestList, selectedColorsTestList);
    assertEquals("The number of color attributes and colors do not match. Received 4 attribute values and 1 colors", errorMsgTest);
  }

  @Test
  public final void testValidateLineTypes() {
    List<String> lineTypeAttributeValuesTestList = ImmutableList.of("test value 1", "test value 2", "test value 3", "test value 4");
    List<String> selectedLineTypesTestList = ImmutableList.of("line type 1", "line type 2", "line type 3", "line type 4");

    String errorMsgTest = ValidationHelper.validateLineTypes(lineTypeAttributeValuesTestList, selectedLineTypesTestList);
    assertEquals("There should be no error message.", null, errorMsgTest);

    errorMsgTest = ValidationHelper.validateLineTypes(lineTypeAttributeValuesTestList, null);
    assertEquals("Either the line type attribute values or the selected line types are not set", errorMsgTest);

    errorMsgTest = ValidationHelper.validateLineTypes(null, selectedLineTypesTestList);
    assertEquals("Either the line type attribute values or the selected line types are not set", errorMsgTest);

    selectedLineTypesTestList = ImmutableList.of("line type");
    errorMsgTest = ValidationHelper.validateLineTypes(lineTypeAttributeValuesTestList, selectedLineTypesTestList);
    assertEquals("The number of line type attributes and line types do not match. Received 4 attribute values and 1 line types", errorMsgTest);
  }

}
