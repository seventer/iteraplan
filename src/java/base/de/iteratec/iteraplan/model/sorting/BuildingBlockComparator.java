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
package de.iteratec.iteraplan.model.sorting;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * Implementation of {@link Comparator} that compares a list of {@link BuildingBlock}s. The list 
 * is sorted according to the concatenation of the building blocks' internationalized type and the 
 * building blocks' identity string.
 */
public class BuildingBlockComparator implements Comparator<BuildingBlock>, Serializable {

  private static final long serialVersionUID = -4043263083307057724L;
  private Locale            locale           = null;

  public BuildingBlockComparator() {
    this.locale = UserContext.getCurrentLocale();
  }

  public int compare(BuildingBlock o1, BuildingBlock o2) {
    String typeO1 = MessageAccess.getStringOrNull(o1.getBuildingBlockType().getName(), locale);
    String typeO2 = MessageAccess.getStringOrNull(o2.getBuildingBlockType().getName(), locale);

    String o1Identifier = typeO1 + "\u0001" + o1.getIdentityString();
    String o2Identifier = typeO2 + "\u0001" + o2.getIdentityString();

    return o1Identifier.compareToIgnoreCase(o2Identifier);
  }
}
