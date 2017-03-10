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
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * Operation to persist an iteraplan instance
 */
public class SaveOrUpdateOperation extends IteraplanChangeOperation {

  private static final Logger               LOGGER = Logger.getIteraplanLogger(SaveOrUpdateOperation.class);
  private final BuildingBlockServiceLocator bbServiceLocator;

  public SaveOrUpdateOperation(IteraplanMapping mapping, BiMap<Object, UniversalModelExpression> instanceMapping,
      BuildingBlockServiceLocator bbServiceLocator) {
    super(mapping, instanceMapping);
    this.bbServiceLocator = bbServiceLocator;
  }

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

  /**
   * Persists the instance created from the given {@link RightSidedDiff}.
   */
  @Override
  protected void handleRightSidedDiff(RightSidedDiff rightSidedDiff) {
    persist(rightSidedDiff.getType(), rightSidedDiff.getExpression());
  }

  /**
   * Persists the instance affected by the given {@link TwoSidedDiff}.
   */
  @Override
  protected void handleTwoSidedDiff(TwoSidedDiff twoSidedDiff) {
    persist(twoSidedDiff.getType(), twoSidedDiff.getLeftExpression());
  }

  private void persist(UniversalTypeExpression type, UniversalModelExpression universalModelExpression) {
    Object instanceToPersist = getInstanceForExpression(universalModelExpression);
    if (instanceToPersist != null) {
      handleSeals(instanceToPersist);

      HbMappedClass hbClass = getHbClass(type);
      if (hbClass.isReleaseClass()) {
        Object baseInstance = getReleaseBase(hbClass, instanceToPersist);
        saveOrUpdate(baseInstance);
      }

      saveOrUpdate(instanceToPersist);
    }
    else {
      LOGGER.debug("No instance found for the given expression. Not persisting.");
    }
  }

  private void handleSeals(Object instanceToPersist) {
    if (instanceToPersist instanceof InformationSystemRelease) {
      ((InformationSystemRelease) instanceToPersist).breakSeal();
    }
    else if (instanceToPersist instanceof InformationSystemInterface) {
      ((InformationSystemInterface) instanceToPersist).getInformationSystemReleaseA().breakSeal();
      ((InformationSystemInterface) instanceToPersist).getInformationSystemReleaseB().breakSeal();
    }
  }

  private void saveOrUpdate(Object instanceToPersist) {
    if (instanceToPersist != null) {
      if (instanceToPersist instanceof BuildingBlock) {
        LOGGER.debug("Persisting \"{0}\"...", instanceToPersist.getClass().getSimpleName());

        // getting the right BB-Service
        BuildingBlockService<BuildingBlock, Integer> instanceBBService = bbServiceLocator.getService(((BuildingBlock) instanceToPersist)
            .getTypeOfBuildingBlock());

        instanceBBService.saveOrUpdate((BuildingBlock) instanceToPersist, false);

        logInfo("{0} \"{1}\" persisted.", instanceToPersist.getClass().getSimpleName(), instanceToPersist);
      }
      else {
        logWarning("Can't persist object of class \"{0}\". Ignoring.", instanceToPersist.getClass().getSimpleName());
      }
    }
    else {
      LOGGER.debug("No instance found for the given expression. Not persisting.");
    }
  }

}
