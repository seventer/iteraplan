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
package de.iteratec.iteraplan.presentation.dialog.MassUpdate.ComponentModel;

import junit.framework.TestCase;

import org.junit.Test;

import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.BusinessMappingCmMu;


public class BusinessMappingTest extends TestCase {

  @Test
  public void testGetCustomHashCode() {
    BusinessMapping bizMapping = new BusinessMapping() {
      @Override
      public String getNameForHtmlId() {
        return "";
      }
    };
    BusinessMappingCmMu componentModel1 = new BusinessMappingCmMu(bizMapping, null);
    BusinessMappingCmMu componentModel2 = new BusinessMappingCmMu(bizMapping, null);

    assertEquals(componentModel1.getCustomHashCode(), componentModel1.getCustomHashCode());

    setIds(componentModel1, componentModel2, -1, -1, 1, -1, -1, 1);
    assertEquals(componentModel1.getCustomHashCode(), componentModel1.getCustomHashCode());

    setIds(componentModel1, componentModel2, 1, 1, 1, 1, 1, 1);
    assertEquals(componentModel1.getCustomHashCode(), componentModel1.getCustomHashCode());

    setIds(componentModel1, componentModel2, 1, 1, 1, 1, 1, 2);
    assertEquals(componentModel1.getCustomHashCode(), componentModel1.getCustomHashCode());

    setIds(componentModel1, componentModel2, 2, 1, 1, 1, 1, 2);
    assertEquals(componentModel1.getCustomHashCode(), componentModel1.getCustomHashCode());

    setIds(componentModel1, componentModel2, 1, 1, 1, 1, 2, 2);
    assertEquals(componentModel1.getCustomHashCode(), componentModel1.getCustomHashCode());

  }

  private void setIds(BusinessMappingCmMu componentModel1, BusinessMappingCmMu componentModel2, int i, int j, int k, int l, int m, int n) {
    if (i == -1) {
      componentModel1.setSelectedBusinessProcessId(null);
    }
    else {
      componentModel1.setSelectedBusinessProcessId(Integer.valueOf(i));
    }
    if (j == -1) {
      componentModel2.setSelectedBusinessProcessId(null);
    }
    else {
      componentModel2.setSelectedBusinessProcessId(Integer.valueOf(j));
    }
    if (k == -1) {
      componentModel1.setSelectedBusinessUnitId(null);
    }
    else {
      componentModel1.setSelectedBusinessUnitId(Integer.valueOf(k));
    }
    if (l == -1) {
      componentModel2.setSelectedBusinessUnitId(null);
    }
    else {
      componentModel2.setSelectedBusinessUnitId(Integer.valueOf(l));
    }
    if (m == -1) {
      componentModel1.setSelectedProductId(null);
    }
    else {
      componentModel1.setSelectedProductId(Integer.valueOf(m));
    }
    if (n == -1) {
      componentModel2.setSelectedProductId(null);
    }
    else {
      componentModel2.setSelectedProductId(Integer.valueOf(n));
    }

  }

}
