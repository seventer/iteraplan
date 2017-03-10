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
package de.iteratec.iteraplan.businesslogic.reports.query.type;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.query.type.SimpleAssociation.AssociationCardinality;
import de.iteratec.iteraplan.common.Constants;


/**
 * Extends the class {@link BusinessMappingType} with functionality for mass updates.
 */
public class BusinessMappingTypeMu extends BusinessMappingType implements MassUpdateType {

  /** Serialization version. */
  private static final long                  serialVersionUID = -8179155297503398950L;
  private static final BusinessMappingTypeMu INSTANCE         = new BusinessMappingTypeMu();

  public static MassUpdateType getInstance() {
    return INSTANCE;
  }

  /* (non-Javadoc)
   * @see de.iteratec.iteraplan.businesslogic.reports.query.type.Type#initAssociations()
   */
  @Override
  protected void initAssociations() {
    super
        .addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_BUSINESSPROCESS, Constants.BB_BUSINESSPROCESS, AssociationCardinality.TO_ONE, 0));
    super.addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_BUSINESSUNIT, Constants.BB_BUSINESSUNIT, AssociationCardinality.TO_ONE, 1));
    super.addMassUpdateAssociation(new SimpleAssociation(ASSOCIATION_PRODUCT, Constants.BB_PRODUCT, AssociationCardinality.TO_ONE, 2));
  }

  /**
   * Takes a list of localization ids belonging to the associations of business mapping 
   * and returns the keys that are not included in the provided list
   * @param associations The keys of the associations
   * @return The localization keys not contained in the provided list 
   */
  public static List<String> getMissingAssociationKeys(List<String> associations) {
    List<String> ret = new ArrayList<String>();
    if (!isSelected(associations, ASSOCIATION_BUSINESSPROCESS)) {
      ret.add(Constants.BB_BUSINESSPROCESS);
    }
    if (!isSelected(associations, ASSOCIATION_BUSINESSUNIT)) {
      ret.add(Constants.BB_BUSINESSUNIT);
    }
    if (!isSelected(associations, ASSOCIATION_PRODUCT)) {
      ret.add(Constants.BB_PRODUCT);
    }
    return ret;
  }

  /**
   * Adds the path in compareString to the list in result if the path is not contained in list
   * @param list The list with the selected paths
   * @param compareString The path to check for
   */
  private static boolean isSelected(List<String> list, String compareString) {
    for (String item : list) {
      if (item.equals(compareString)) {
        return true;
      }
    }
    return false;
  }

}
