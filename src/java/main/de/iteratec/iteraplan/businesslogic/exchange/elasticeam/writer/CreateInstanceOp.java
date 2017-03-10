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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Creates an iteraplan instance from a {@link RightSidedDiff}.
 */
public class CreateInstanceOp extends IteraplanChangeOperation {

  private static final Logger               LOGGER = Logger.getIteraplanLogger(CreateInstanceOp.class);

  private final BuildingBlockServiceLocator bbServiceLocator;

  public CreateInstanceOp(IteraplanMapping mapping, BiMap<Object, UniversalModelExpression> instanceMapping,
      BuildingBlockServiceLocator bbServiceLocator) {
    super(mapping, instanceMapping);
    this.bbServiceLocator = bbServiceLocator;
  }

  protected Logger getLogger() {
    return LOGGER;
  }

  /**
   * Creates an iteraplan instance from a {@link RightSidedDiff}.
   */
  protected void handleRightSidedDiff(RightSidedDiff rightSidedDiff) {
    UniversalModelExpression instanceExpression = rightSidedDiff.getExpression();
    LOGGER.debug("Instance to be created: {0}", instanceExpression);

    if (!handleInformationFlowCase(rightSidedDiff) && !handleInterfaceCase(rightSidedDiff)
        && !isInvalidBusinessMapping(rightSidedDiff.getType(), rightSidedDiff.getExpression())) {
      HbMappedClass hbClass = getHbClass(rightSidedDiff.getType());
      BuildingBlock created = createIteraplanInstance(hbClass, instanceExpression);

      // the parent is already set to the virtual element by the BuildingBlockFactory create method
      // setDefaultParentForHierarchicalEntity(created);
      putInstanceForExpression(instanceExpression, created);
    }
  }

  private boolean handleInformationFlowCase(RightSidedDiff rightSidedDiff) {
    if (!rightSidedDiff.getType().getPersistentName().equals("InformationFlow")) {
      return false;
    }
    else {
      UniversalModelExpression instanceExpression = rightSidedDiff.getExpression();
      BuildingBlock transport = createTransportIfApplicable(instanceExpression, rightSidedDiff.getType());
      if (transport != null) {
        putInstanceForExpression(instanceExpression, transport);
      }
      return true;
    }
  }

  private boolean handleInterfaceCase(RightSidedDiff rightSidedDiff) {
    if (!rightSidedDiff.getType().getPersistentName().equals("InformationSystemInterface")) {
      return false;
    }
    else {
      UniversalModelExpression instanceExpression = rightSidedDiff.getExpression();
      RelationshipEndExpression toInformationFlow = rightSidedDiff.getType().findRelationshipEndByName("informationFlows");
      Collection<UniversalModelExpression> connectedInformationFlows = instanceExpression.getConnecteds(toInformationFlow);
      if (connectedInformationFlows != null && !connectedInformationFlows.isEmpty()) {
        InformationSystemInterface isi = BuildingBlockFactory.createInformationSystemInterface();
        putInstanceForExpression(instanceExpression, isi);
        logInfo("Created new InformationSystemInterface.");
      }
      else {
        logWarning("Interface \"{0}\" does not have any InformationFlow assigned. Ignoring.",
            instanceExpression.getValue(MixinTypeNamed.NAME_PROPERTY));
      }
      return true;
    }
  }

  private BuildingBlock createTransportIfApplicable(UniversalModelExpression instanceExpression, UniversalTypeExpression typeExpression) {
    RelationshipEndExpression toBO = typeExpression.findRelationshipEndByName("businessObject");
    UniversalModelExpression boExpression = instanceExpression.getConnected(toBO);

    if (boExpression == null) {
      LOGGER.debug("No Business Object => no Transport created.");
      return null;
    }
    else {
      LOGGER.debug("Transport for \"{0}\" with Business Object \"{1}\" created.", instanceExpression,
          boExpression.getValue(MixinTypeNamed.NAME_PROPERTY));
      return BuildingBlockFactory.createTransport();
    }
  }

  /**
   * Creates an iteraplan entity instance.
   * @param hbClass
   *          {@link HbMappedClass} for the class of the entity to create
   * @param sourceModelExpression
   *          {@link UniversalModelExpression} describing the entity to create
   * @return the iteraplan entity instance
   */
  private BuildingBlock createIteraplanInstance(HbMappedClass hbClass, UniversalModelExpression sourceModelExpression) {
    Class<?> clazz = hbClass.getMappedClass();
    try {
      Method factoryConstructor = BuildingBlockFactory.class.getMethod("create" + clazz.getSimpleName());
      BuildingBlock bb = (BuildingBlock) factoryConstructor.invoke(null);

      if (hbClass.isReleaseClass()) {
        //in case of TCRs or ISRs, we need to check for TCs and ISs
        String[] versionedName = sourceModelExpression.getValue(MixinTypeNamed.NAME_PROPERTY).toString().split("#");
        Set<String> names = Sets.newHashSet(versionedName[0].trim());

        if (TechnicalComponentRelease.class.equals(hbClass.getMappedClass())) {
          addBaseToTCR(bb, names);
        }
        else if (InformationSystemRelease.class.equals(hbClass.getMappedClass())) {
          addBaseToISR(bb, names);
        }
        else {
          throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, hbClass);
        }
      }
      logInfo("Created new {0}.", clazz.getSimpleName());
      return bb;
    } catch (SecurityException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    } catch (NoSuchMethodException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    } catch (IllegalArgumentException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    } catch (IllegalAccessException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    } catch (InvocationTargetException e) {
      LOGGER.error(e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
    }
  }

  private void addBaseToTCR(BuildingBlock bb, Set<String> names) {
    TechnicalComponentRelease release = (TechnicalComponentRelease) bb;
    TechnicalComponentService service = bbServiceLocator.getTcService();

    List<TechnicalComponent> list = service.findByNames(names);

    TechnicalComponent base = null;
    if (list.isEmpty()) {
      base = BuildingBlockFactory.createTechnicalComponent();
    }
    else {
      base = list.get(0);
    }
    base.addRelease(release); // adds it two way
  }

  private void addBaseToISR(BuildingBlock bb, Set<String> names) {
    InformationSystemRelease release = (InformationSystemRelease) bb;
    InformationSystemService service = bbServiceLocator.getInformationSystemService();

    List<InformationSystem> list = service.findByNames(names);

    InformationSystem base = null;
    if (list.isEmpty()) {
      base = BuildingBlockFactory.createInformationSystem();
    }
    else {
      base = list.get(0);
    }
    base.addRelease(release); //adds it two way
  }

}
