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
package de.iteratec.iteraplan.businesslogic.exchange.elasticeam.writer;

import com.google.common.collect.BiMap;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.LeftSidedDiff;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 *
 */
public class DeleteInstanceOp extends IteraplanChangeOperation {

  private static final Logger               LOGGER = Logger.getIteraplanLogger(DeleteInstanceOp.class);

  private final BuildingBlockServiceLocator bbServiceLocator;

  public DeleteInstanceOp(IteraplanMapping mapping, BiMap<Object, UniversalModelExpression> instanceMapping,
      BuildingBlockServiceLocator bbServiceLocator) {
    super(mapping, instanceMapping);
    this.bbServiceLocator = bbServiceLocator;
  }

  protected Logger getLogger() {
    return LOGGER;
  }

  /**
   * Delete an instance given by the {@link LeftSidedDiff}.
   */
  protected void handleLeftSidedDiff(LeftSidedDiff leftSidedDiff) {
    LOGGER.debug("Deleting \"{0}\"...", leftSidedDiff.getExpression());
    Object instance = getInstanceForExpression(leftSidedDiff.getExpression());
    if (instance instanceof BuildingBlock) {
      deleteObject((BuildingBlock) instance);
    }
    else {
      logWarning("{0} \"{1}\" is not a BuildingBlock and will not be deleted.", instance.getClass().getSimpleName(), instance);
    }
  }

  private void deleteObject(BuildingBlock instance) {
    bbServiceLocator.getService(instance.getTypeOfBuildingBlock()).deleteEntity(instance);
    logInfo("{0} \"{1}\" deleted.", instance.getClass().getSimpleName(), instance);
  }
}
