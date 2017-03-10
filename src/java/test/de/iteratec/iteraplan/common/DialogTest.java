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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.Transport;


public class DialogTest {

  @Test
  public void testDialogNameForClass() {
    // most building blocks/ model classes have a corresponding dialog; some, however, don't
    // and the lookup logic should return null

    String res1 = Dialog.dialogNameForClass(BusinessUnit.class);
    assertNotNull("returned dialog name should not be null", res1);

  }

  @Test
  public void testFlowIdForClass() {
    // most building blocks/ model classes have a corresponding dialog; some, however, don't
    // and the lookup logic should return null

    String res1 = Dialog.dialogNameForClass(BusinessUnit.class);
    assertNotNull("returned flow ID should not be null", res1);

  }

  @Test
  public void testGetDialogForClass() {
    Dialog res1 = Dialog.getDialogForClass(Transport.class);
    assertNull("the should be no dialog for Transport/ null expected", res1);

    Dialog res2 = Dialog.getDialogForClass(Project.class);
    assertNotNull("the must be dialog for Project", res2);
  }

}
