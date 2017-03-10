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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.Sequence;


public abstract class ReleaseSheetImporterTestBase<T extends BuildingBlock & Sequence<T>> {

  private ReleaseBuildingBlockSheetImporter<T> classUnderTest;

  @Before
  public void setUp() throws Exception {
    classUnderTest = getSheetImporterInstance();
  }

  protected abstract ReleaseBuildingBlockSheetImporter<T> getSheetImporterInstance();

  @Test
  public void testGetNameAndRelease() {
    String isrNameWithVersion = "foo 123 # Rel5";
    assertEquals("foo 123", classUnderTest.getNameMain(isrNameWithVersion));
    assertEquals("Rel5", classUnderTest.getNameRelease(isrNameWithVersion));

    String isrNameUnversioned = "barf,()";
    assertEquals("barf,()", classUnderTest.getNameMain(isrNameUnversioned));
    assertEquals("", classUnderTest.getNameRelease(isrNameUnversioned));

    String isrNameHashMark = "isr[34] # ";
    assertEquals("isr[34]", classUnderTest.getNameMain(isrNameHashMark));
    assertEquals("", classUnderTest.getNameRelease(isrNameHashMark));

    String isrNameSpaces = "an ISR_name   with lots of      space     #     even in version field!!        ";
    assertEquals("an ISR_name   with lots of      space", classUnderTest.getNameMain(isrNameSpaces));
    assertEquals("even in version field!!", classUnderTest.getNameRelease(isrNameSpaces));

    String isrNameManyHashes = "  isr_ #34$! # ver#3.5";
    assertEquals("isr_", classUnderTest.getNameMain(isrNameManyHashes));
    assertEquals("34$! # ver#3.5", classUnderTest.getNameRelease(isrNameManyHashes));

  }

}
