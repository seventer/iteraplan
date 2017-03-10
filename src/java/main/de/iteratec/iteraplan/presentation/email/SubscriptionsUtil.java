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
package de.iteratec.iteraplan.presentation.email;

import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockUtil;

public final class SubscriptionsUtil {

  private SubscriptionsUtil() {
    // Nothing to comment besides the fact that there is nothing to comment. SO meta.
  }

  /**
   * Returns a copy of the building block if the notification option is activated
   * @param entity the building block to copy
   * @return a copy of the building block that contains all relevant information used for generating the email content
   */
  public static BuildingBlock prepareForSubscriptions(BuildingBlock entity) {
    if (!IteraplanProperties.getBooleanProperty(IteraplanProperties.NOTIFICATION_ACTIVATED)) {
      return entity;
    } else {
      return BuildingBlockUtil.clone(entity);
    }
  }

  public static boolean isEmailNecessary(BuildingBlock clone, BuildingBlock original, EmailModel model) {
    return (clone == null) || (original != null && !model.getChanges().isEmpty());
  }

  public static String extractName(BuildingBlock bb) {
    return bb.getIdentityString();
  }

}
