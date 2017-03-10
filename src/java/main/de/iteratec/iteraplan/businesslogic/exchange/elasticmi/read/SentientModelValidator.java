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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read;

import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.validate.model.AModelValidator;


/**
 * Abstract class for validators which, because of needing
 * specific types in a metmaodel, should be capable
 * of detecting whether they actually should validate,
 * or skip.
 */
abstract class SentientModelValidator extends AModelValidator {

  private final boolean sentientMode;

  public SentientModelValidator(RMetamodel rMetamodel) {
    super(rMetamodel);
    this.sentientMode = init(rMetamodel);
  }

  protected final boolean init(RMetamodel rMetamodel) {
    if (canValidate(rMetamodel)) {
      initInternal(rMetamodel);
      return false;
    }
    return true;
  }

  /**
   * @return
   *    true if the validator should be sentient, i.e. can not validate on the basis
   *    of the provided metamodel.
   */
  protected final boolean isSentientMode() {
    return this.sentientMode;
  }

  protected abstract boolean canValidate(RMetamodel rMetamodel);

  protected abstract void initInternal(RMetamodel rMetamodel);

}
