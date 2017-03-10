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
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.MetamodelExport;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedProperty;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffPart;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;


/**
 * Sets the relationships of an iteraplan instance according to a {@link de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff Diff}.
 */
public class SetRelationshipOp extends IteraplanChangeOperation {

  private static final Logger                                           LOGGER = Logger.getIteraplanLogger(SetRelationshipOp.class);

  private final BuildingBlockServiceLocator                             bbServiceLocator;
  private final Map<UniversalModelExpression, UniversalModelExpression> rightToLeftMap;

  public SetRelationshipOp(IteraplanMapping mapping, BiMap<Object, UniversalModelExpression> instanceMapping,
      BuildingBlockServiceLocator bbServiceLocator, Map<UniversalModelExpression, UniversalModelExpression> rightToLeftMap) {
    super(mapping, instanceMapping);
    this.bbServiceLocator = bbServiceLocator;
    this.rightToLeftMap = rightToLeftMap;
  }

  protected Logger getLogger() {
    return LOGGER;
  }

  /**
  * Sets the relationships from a {@link RightSidedDiff}.
  */
  protected void handleRightSidedDiff(RightSidedDiff diff) {
    LOGGER.debug("Setting relationships for \"{0}\"...", diff.getExpression());

    UniversalModelExpression instanceExpression = diff.getExpression();
    Object instance = getInstanceForExpression(instanceExpression);

    LOGGER.debug("Instance: {0}", instance);

    if (isInformationflowCase(diff)) {
      new InformationFlowRelationsOp().handleRightSidedDiff(diff);
    }
    else if (instance != null) {
      HbMappedClass hbClass = getHbClass(diff.getType());

      for (RelationshipEndExpression relationshipEnd : diff.getType().getRelationshipEnds()) {
        LOGGER.debug("Handling relationshipEnd: {0}", relationshipEnd.toString());

        if (!isToIgnore(diff.getType(), relationshipEnd)) {
          handleRelationshipOfNewInstance(instance, hbClass, instanceExpression, relationshipEnd);
        }
      }

      checkVirtualElements(instance);
      checkInterfaceEnds(instance, instanceExpression, diff.getType());
      checkTcAvailableForInterfaceCase(instance);
    }
    else {
      LOGGER.debug("No instance found for expression \"{0}\". Ignoring.", instanceExpression);
    }

    LOGGER.debug("All relationships set.");
  }

  private void handleRelationshipOfNewInstance(Object instance, HbMappedClass hbClass, UniversalModelExpression instanceExpression,
                                               RelationshipEndExpression relationshipEnd) {
    LOGGER.debug("called handleRelationshipOfNewInstance");
    HbMappedProperty hbRelationship = findHbMappedProperty(hbClass, relationshipEnd);

    LOGGER.debug("hbRelationShip: {0}", hbRelationship);

    if (hbRelationship != null) {
      Object owningInstance = findOwningInstance(hbClass, hbRelationship, instance);

      LOGGER.debug("Owning Instance: {0}", owningInstance);

      if (relationshipEnd.getUpperBound() > 1) {
        LOGGER.debug("upper bound > 1");
        Set<Object> toAdd = getInstanceSetForExpressions(instanceExpression.getConnecteds(relationshipEnd));
        if (!toAdd.isEmpty()) {
          Set<Object> toRemove = Sets.newHashSet();
          updateRelationshipCollection(owningInstance, hbRelationship, toRemove, toAdd);
        }
        else {
          LOGGER.debug("Nothing to add");
        }
      }
      else {
        UniversalModelExpression modelExpressionToSet = instanceExpression.getConnected(relationshipEnd);
        LOGGER.debug("upperBound <= 1");
        if (modelExpressionToSet != null) {
          Object newTarget = getInstanceForExpression(modelExpressionToSet);
          LOGGER.debug("newTarget: {0}", newTarget);
          updateRelationship(owningInstance, hbRelationship, newTarget);
        }
      }
    }
  }

  /**
   * Changes the relationships based on a {@link TwoSidedDiff}.
   */
  protected void handleTwoSidedDiff(TwoSidedDiff diff) {
    LOGGER.debug("Setting relationships for \"{0}\"...", diff.getLeftExpression());

    if (!isInvalidBusinessMapping(diff.getType(), diff.getRightExpression())) {
      UniversalModelExpression instanceExpression = diff.getLeftExpression();
      Object instance = getInstanceForExpression(instanceExpression);
      if (isInformationflowCase(diff)) {
        new InformationFlowRelationsOp().handleTwoSidedDiff(diff);
      }
      else {
        HbMappedClass hbClass = getHbClass(diff.getType());
        for (DiffPart diffPart : diff.getDiffParts()) {
          FeatureExpression<?> feature = diffPart.getFeature();
          if (feature instanceof RelationshipEndExpression) {
            LOGGER.debug("Updating relationship \"{0}\" of \"{1}\"...", feature, instanceExpression);
            RelationshipEndExpression relationshipEnd = (RelationshipEndExpression) feature;
            if (!isToIgnore(diff.getType(), relationshipEnd)) {
              handleDiffPart(instance, hbClass, diffPart, relationshipEnd);
            }
          }
        }
        checkVirtualElements(instance);
        checkTcAvailableForInterfaceCase(instance);
      }
      LOGGER.debug("All relationships set.");
    }
  }

  @SuppressWarnings("unchecked")
  private void handleDiffPart(Object instance, HbMappedClass hbClass, DiffPart diffPart, RelationshipEndExpression relationshipEnd) {
    HbMappedProperty hbRelationship = findHbMappedProperty(hbClass, relationshipEnd);
    if (hbRelationship != null) {
      Object owningInstance = findOwningInstance(hbClass, hbRelationship, instance);

      if (relationshipEnd.getUpperBound() > 1) {
        Set<Object> leftValue = getInstanceSetForExpressions((Collection<UniversalModelExpression>) diffPart.getLeftValue());
        Set<Object> rightValue = getInstanceSetForExpressions((Collection<UniversalModelExpression>) diffPart.getRightValue());
        Set<Object> toRemove = Sets.newHashSet(leftValue);
        toRemove.removeAll(rightValue);
        Set<Object> toAdd = Sets.newHashSet(rightValue);
        toAdd.removeAll(leftValue);
        updateRelationshipCollection(owningInstance, hbRelationship, toRemove, toAdd);
      }
      else {
        Object newTarget = getInstanceForExpression((UniversalModelExpression) diffPart.getRightValue());
        updateRelationship(owningInstance, hbRelationship, newTarget);

        if (instance instanceof AbstractHierarchicalEntity && relationshipEnd.getPersistentName().equals("parent") && diffPart.getLeftValue() == null) {
          BuildingBlockService<BuildingBlock, Integer> service = bbServiceLocator.getService(((BuildingBlock) instance).getTypeOfBuildingBlock());
          AbstractHierarchicalEntity<?> firstElement = (AbstractHierarchicalEntity<?>) service.getFirstElement();
          firstElement.getChildren().remove(instance);
          service.saveOrUpdate(firstElement);
        }
      }
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void updateRelationshipCollection(Object instance, HbMappedProperty hbRelationship, Set<Object> toRemove, Set<Object> toAdd) {
    LOGGER.debug("Updating relationship \"{0}\" of \"{1}\"...", hbRelationship.getName(), instance);
    Set<Object> validObjectsToAdd = extractApplicableTargets(instance, toAdd);
    LOGGER.debug("called updateRelationshipCollection");
    try {
      Object connected = hbRelationship.getGetMethod().invoke(instance);
      if (connected instanceof Collection) {
        Collection collection = (Collection) connected;

        collection.removeAll(toRemove);
        collection.addAll(validObjectsToAdd);
      }
      logInfo("\"{0}\" of {1} \"{2}\" set to {3}.", hbRelationship.getName(), instance.getClass().getSimpleName(), instance, connected);
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

  private Set<Object> extractApplicableTargets(Object instance, Set<Object> toAdd) {
    Set<Object> result = Sets.newHashSet();
    for (Object candidate : toAdd) {
      if (isApplicableTarget(instance, candidate)) {
        result.add(candidate);
      }
    }
    return result;
  }

  private void updateRelationship(Object instance, HbMappedProperty hbRelationship, Object newTarget) {
    LOGGER.debug("Updating relationship \"{0}\" of \"{1}\"...", hbRelationship.getName(), instance);
    if (isApplicableTarget(instance, newTarget)) {
      try {
        hbRelationship.getSetMethod().invoke(instance, newTarget);
        logInfo("\"{0}\" of {1} \"{2}\" set to \"{3}\".", hbRelationship.getName(), instance.getClass().getSimpleName(), instance, newTarget);
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
  }

  private boolean isApplicableTarget(Object instance, Object newTarget) {
    if (instance instanceof TechnicalComponentRelease && newTarget instanceof InformationSystemInterface) {
      return ((TechnicalComponentRelease) instance).isAvailableForInterfaces();
    }
    else if (instance instanceof InformationSystemInterface && newTarget instanceof TechnicalComponentRelease) {
      return ((TechnicalComponentRelease) newTarget).isAvailableForInterfaces();
    }
    return true;
  }

  private Set<Object> getInstanceSetForExpressions(Collection<UniversalModelExpression> expressions) {
    if (expressions == null || expressions.isEmpty()) {
      return Sets.newHashSet();
    }

    Set<Object> instances = Sets.newHashSet();
    for (UniversalModelExpression expression : expressions) {
      Object instance = getInstanceForExpression(expression);
      if (instance != null) {
        instances.add(instance);
      }
    }

    return instances;
  }

  protected Object getInstanceForExpression(UniversalModelExpression modelExpression) {
    if (modelExpression == null) {
      return null;
    }
    UniversalModelExpression matchedExpression = rightToLeftMap.get(modelExpression);
    if (matchedExpression == null) {
      matchedExpression = modelExpression;
    }

    return super.getInstanceForExpression(matchedExpression);
  }

  /**
   * Determines whether the given relationship update should be ignored. Used for example to ignore
   * relationship updates already handled by special cases. 
   */
  private boolean isToIgnore(UniversalTypeExpression typeExpression, RelationshipEndExpression relationshipEnd) {
    if (typeExpression.getPersistentName().equals("InformationSystem")) {
      // Information flow relations are already handled in #handleInformationFlow
      return relationshipEnd.getPersistentName().equals(MetamodelExport.INFORMATION_FLOW_IS1_OPP)
          || relationshipEnd.getPersistentName().equals(MetamodelExport.INFORMATION_FLOW_IS2_OPP);
    }
    if (typeExpression.getPersistentName().equals("BusinessObject")) {
      // Information flow relations are already handled in #handleInformationFlow
      return relationshipEnd.getPersistentName().equals("informationFlows");
    }
    if (typeExpression.getPersistentName().equals("InformationSystemInterface")) {
      // Information flow relations are already handled in #handleInformationFlow
      return relationshipEnd.getPersistentName().equals("informationFlows");
    }
    return false;
  }

  private void checkVirtualElements(Object bb) {
    LOGGER.debug("Checking whether there are null values assigned in relations of \"{0}\" where virtual elements should be...", bb);
    if (bb instanceof AbstractHierarchicalEntity) {
      checkParent((AbstractHierarchicalEntity<?>) bb);
    }
    if (bb instanceof BusinessMapping) {
      checkBMForNullelements((BusinessMapping) bb);
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends HierarchicalEntity<T>> void checkParent(AbstractHierarchicalEntity<T> hierarchicalEntity) {

    LOGGER.debug("checkParent of {0}: ", hierarchicalEntity);

    T parent = hierarchicalEntity.getParent();
    T virtualRootElement = (T) bbServiceLocator.getService(hierarchicalEntity.getTypeOfBuildingBlock()).getFirstElement();
    if (parent == null) {
      LOGGER.debug("parent is null - calling addParent");
      hierarchicalEntity.addParent(virtualRootElement);
    }
    else if (AbstractHierarchicalEntity.TOP_LEVEL_NAME.equals(parent.getNonHierarchicalName()) && !parent.getChildren().contains(hierarchicalEntity)) {
      LOGGER.debug("Adding \"{0}\" to the children list of its virtual element.", hierarchicalEntity);
      parent.getChildren().add((T) hierarchicalEntity);
      bbServiceLocator.getService(hierarchicalEntity.getTypeOfBuildingBlock()).saveOrUpdate((BuildingBlock) parent);
    }
    else if (!AbstractHierarchicalEntity.TOP_LEVEL_NAME.equals(parent.getNonHierarchicalName())
        && virtualRootElement.getChildren().contains(hierarchicalEntity)) {
      LOGGER.debug("Entity \"{0}\" has already a parent different from the virtual root element. Cleaning up the root element.", hierarchicalEntity);
      virtualRootElement.getChildren().remove(hierarchicalEntity);
      bbServiceLocator.getService(hierarchicalEntity.getTypeOfBuildingBlock()).saveOrUpdate((BuildingBlock) virtualRootElement);
    }
  }

  private void checkBMForNullelements(BusinessMapping bm) {
    // only "normal" business mappings have to be checked
    if (bm.getProduct() == null) {
      LOGGER.debug("Setting virtual element as product in business mapping \"{0}\".", bm);
      bbServiceLocator.getProductService().getFirstElement().addBusinessMapping(bm);
    }
    if (bm.getBusinessProcess() == null) {
      LOGGER.debug("Setting virtual element as business process in business mapping \"{0}\".", bm);
      bbServiceLocator.getBpService().getFirstElement().addBusinessMapping(bm);
    }
    if (bm.getBusinessUnit() == null) {
      LOGGER.debug("Setting virtual element as business unit in business mapping \"{0}\".", bm);
      bbServiceLocator.getBuService().getFirstElement().addBusinessMapping(bm);
    }
  }

  private void checkInterfaceEnds(Object instance, UniversalModelExpression interfaceExpression, UniversalTypeExpression type) {
    if (instance instanceof InformationSystemInterface) {
      InformationSystemInterface isi = (InformationSystemInterface) instance;
      LOGGER.debug("Check whether the interface has its information system releases set, and if not, correct this.");
      if (isi.getInformationSystemReleaseA() == null || isi.getInformationSystemReleaseB() == null) {
        RelationshipEndExpression toInfoFlow = type.findRelationshipEndByName("informationFlows");
        UniversalModelExpression infoFlowExpression = interfaceExpression.getConnecteds(toInfoFlow).iterator().next();

        RelationshipEndExpression toIsrA = toInfoFlow.getType().findRelationshipEndByName(MetamodelExport.INFORMATION_FLOW_IS1);
        RelationshipEndExpression toIsrB = toInfoFlow.getType().findRelationshipEndByName(MetamodelExport.INFORMATION_FLOW_IS2);
        UniversalModelExpression isrAExpression = infoFlowExpression.getConnected(toIsrA);
        UniversalModelExpression isrBExpression = infoFlowExpression.getConnected(toIsrB);

        isi.addReleaseA((InformationSystemRelease) getInstanceForExpression(isrAExpression));
        isi.addReleaseB((InformationSystemRelease) getInstanceForExpression(isrBExpression));
        LOGGER.debug("Interface ends for \"{0}\" set.", isi);
      }
      else {
        LOGGER.debug("Interface ends are ok.");
      }
    }
  }

  private void checkTcAvailableForInterfaceCase(Object instance) {
    if (instance instanceof TechnicalComponentRelease && !((TechnicalComponentRelease) instance).isAvailableForInterfaces()) {
      Set<TechnicalComponentRelease> releases = ((TechnicalComponentRelease) instance).getTechnicalComponent().getReleases();
      for (TechnicalComponentRelease tcr : releases) {
        if (!tcr.getInformationSystemInterfaces().isEmpty()) {
          logWarning("Technical Component Release \"{0}\" is not available for interfaces, but has interfaces assigned. Removing them.", tcr);
          tcr.removeInformationSystemInterfaces();
        }
      }
    }
  }

  private class InformationFlowRelationsOp {

    /**
     * Sets the relationships for an InformationFlow from a {@link RightSidedDiff}.
     */
    public void handleRightSidedDiff(RightSidedDiff diff) {
      if (!isInformationflowCase(diff)) {
        logWarning("Trying to handle diff for type \"{0}\" as InformationFlow. Ignoring.", diff.getType());
        return;
      }

      UniversalTypeExpression typeExpression = diff.getType();
      UniversalModelExpression infoFlowExpression = diff.getExpression();

      RelationshipEndExpression toBO = typeExpression.findRelationshipEndByName("businessObject");
      UniversalModelExpression boExpression = infoFlowExpression.getConnected(toBO);

      InformationSystemInterface isi = getInterfaceForInfoFlow(infoFlowExpression, typeExpression);
      setInterfaceEndsFromModelExpression(infoFlowExpression, typeExpression, isi);
      //      if (isi.getId() == null) {
      //        bbServiceLocator.getIsiService().saveOrUpdate(isi);
      //      }

      Object infoFlow = getInstanceForExpression(infoFlowExpression);
      Transport transport = null;
      if (infoFlow != null) {
        transport = (Transport) infoFlow;
      }
      else if (boExpression != null) {
        transport = createNewTransportWithDirection(infoFlowExpression, typeExpression);
      }

      if (transport != null && boExpression != null) {
        transport.disconnect();
        BusinessObject bo = (BusinessObject) getInstanceForExpression(boExpression);
        transport.setBusinessObject(bo);
        transport.setInformationSystemInterface(isi);
        transport.connect();
        logInfo("BusinessObject \"{0}\" to InformationSystemInterface \"{1}\" added.", bo, isi);
      }
    }

    /**
     * Changes the relationships for an InformationFlow based on a {@link TwoSidedDiff}.
     */
    public void handleTwoSidedDiff(TwoSidedDiff diff) {
      if (!isInformationflowCase(diff)) {
        logWarning("Trying to handle diff for type \"{0}\" as InformationFlow. Ignoring.", diff.getType());
        return;
      }

      for (DiffPart part : diff.getDiffParts()) {
        if (part.getFeature() instanceof RelationshipEndExpression) {
          if (part.getFeature().getPersistentName().equals(MetamodelExport.INFORMATION_FLOW_ISI)) {
            applyInterfaceRelationChange(diff, part);
          }
          else if (part.getFeature().getPersistentName().equals("businessObject")) {
            applyBusinessObjectChange(diff, part);
          }
          else if (part.getFeature().getPersistentName().equals(MetamodelExport.INFORMATION_FLOW_IS1)) {
            applyIS1Change(diff, part);
          }
          else if (part.getFeature().getPersistentName().equals(MetamodelExport.INFORMATION_FLOW_IS2)) {
            applyIS2Change(diff, part);
          }
        }
      }
    }

    private InformationSystemInterface getInterfaceForInfoFlow(UniversalModelExpression infoFlowExpression, UniversalTypeExpression typeExpression) {
      InformationSystemInterface isi = null;

      RelationshipEndExpression toInterface = typeExpression.findRelationshipEndByName("informationSystemInterface");
      UniversalModelExpression interfaceExpression = infoFlowExpression.getConnected(toInterface);

      PropertyExpression<?> isiIdExpression = typeExpression.findPropertyByPersistentName(MetamodelExport.INFORMATION_FLOW_ISI_ID);
      Integer isiID = (Integer) checkValue(infoFlowExpression.getValue(isiIdExpression));

      if (interfaceExpression != null) {
        isi = (InformationSystemInterface) getInstanceForExpression(interfaceExpression);
      }
      else if (isiID != null) {
        isi = bbServiceLocator.getIsiService().loadObjectByIdIfExists(isiID);
      }
      if (isi == null) {
        isi = createNewInterface();
        // the information flow direction becomes the interface direction
        Direction infoFlowDirection = getDirection(infoFlowExpression, typeExpression);
        isi.setInterfaceDirection(infoFlowDirection);
        LOGGER.debug("Direction of new interface set to \"{0}\".", infoFlowDirection);
      }
      return isi;
    }

    private boolean setInterfaceEndsFromModelExpression(UniversalModelExpression infoFlowExpression, UniversalTypeExpression typeExpression,
                                                        InformationSystemInterface isi) {
      RelationshipEndExpression toISR1 = typeExpression.findRelationshipEndByName(MetamodelExport.INFORMATION_FLOW_IS1);
      RelationshipEndExpression toISR2 = typeExpression.findRelationshipEndByName(MetamodelExport.INFORMATION_FLOW_IS2);
      UniversalModelExpression isr1Expression = infoFlowExpression.getConnected(toISR1);
      UniversalModelExpression isr2Expression = infoFlowExpression.getConnected(toISR2);

      InformationSystemRelease isr1New = isi.getInformationSystemReleaseA();
      InformationSystemRelease isr2New = isi.getInformationSystemReleaseB();

      if (isr1Expression != null) {
        isr1New = (InformationSystemRelease) getInstanceForExpression(isr1Expression);
      }
      if (isr2Expression != null) {
        isr2New = (InformationSystemRelease) getInstanceForExpression(isr2Expression);
      }

      if (!setInterfaceEnds(isi, isr1New, isr2New)) {
        logWarning(
            "One or more InformationSystems of InformationSystemInterface \"{0}\" for InformationFlow \"{1}\" could not be set. Ignoring change.",
            isi, infoFlowExpression);
        return false;
      }
      else {
        return true;
      }
    }

    private boolean setInterfaceEnds(InformationSystemInterface isi, InformationSystemRelease isr1New, InformationSystemRelease isr2New) {
      if (isr1New != null && isr2New != null) {
        InformationSystemRelease oldIsrA = isi.getInformationSystemReleaseA();
        if (oldIsrA != null) {
          oldIsrA.getInterfacesReleaseA().remove(isi);
        }
        InformationSystemRelease oldIsrB = isi.getInformationSystemReleaseB();
        if (oldIsrB != null) {
          oldIsrB.getInterfacesReleaseB().remove(isi);
        }
        isi.connect(isr1New, isr2New);
        logInfo("InformationSystemInterface \"{0}\" connected.", isi);
        return true;
      }
      else {
        return false;
      }
    }

    private InformationSystemInterface createNewInterface() {
      LOGGER.debug("Creating new InformationSystemInterface...");
      InformationSystemInterface isi = BuildingBlockFactory.createInformationSystemInterface();
      isi.setInterfaceDirection(Direction.NO_DIRECTION);
      LOGGER.debug("Created new InformationSystemInterface.");
      return isi;
    }

    private Transport createNewTransportWithDirection(UniversalModelExpression infoFlowExpression, UniversalTypeExpression typeExpression) {
      LOGGER.debug("Creating new transport with direction...");
      Transport transport;
      transport = BuildingBlockFactory.createTransport();
      Direction direction = getDirection(infoFlowExpression, typeExpression);
      transport.setDirection(direction);
      LOGGER.debug("Transport with direction \"{0}\" created.", direction);
      return transport;
    }

    private Direction getDirection(UniversalModelExpression infoFlowExpression, UniversalTypeExpression typeExpression) {
      PropertyExpression<?> directionProp = typeExpression.findPropertyByPersistentName("direction");
      Direction direction = (Direction) checkValue(infoFlowExpression.getValue(directionProp));
      if (direction == null) {
        direction = Direction.NO_DIRECTION;
        logWarning("No direction for InformationFlow \"{0}\" given. Using no_direction.", infoFlowExpression);
      }
      return direction;
    }

    private void applyInterfaceRelationChange(TwoSidedDiff diff, DiffPart part) {
      LOGGER.debug("Changing InformationSystemInterface of InformationFlow \"{0}\" from \"{1}\" to \"{2}\"...", diff.getLeftExpression(),
          part.getLeftValue(), part.getRightValue());
      Object infoFlow = getInstanceForExpression(diff.getLeftExpression());
      if (infoFlow != null) {
        Transport transport = (Transport) infoFlow;
        InformationSystemInterface isi;
        if (part.getRightValue() != null) {
          UniversalModelExpression interfaceExpression = (UniversalModelExpression) part.getRightValue();
          isi = (InformationSystemInterface) getInstanceForExpression(interfaceExpression);
        }
        else {
          isi = createNewInterface();
          if (!setInterfaceEndsFromModelExpression(diff.getRightExpression(), diff.getType(), isi)) {
            return;
          }
        }
        transport.disconnect();
        transport.setInformationSystemInterface(isi);
        transport.connect();
      }
    }

    private void applyBusinessObjectChange(TwoSidedDiff diff, DiffPart part) {
      LOGGER.debug("Changing BusinessObject of InformationFlow \"{0}\" from \"{1}\" to \"{2}\"...", diff.getLeftExpression(), part.getLeftValue(),
          part.getRightValue());
      Object infoFlow = getInstanceForExpression(diff.getLeftExpression());
      Object rightValue = part.getRightValue();
      if (infoFlow != null) {
        Transport transport = (Transport) infoFlow;
        if (rightValue != null) {
          BusinessObject newBO = (BusinessObject) getInstanceForExpression((UniversalModelExpression) rightValue);
          transport.disconnect();
          transport.setBusinessObject(newBO);
          transport.connect();
        }
        else {
          transport.disconnect();
          bbServiceLocator.getTransportService().deleteEntity(transport);
          removeInstanceMapping(diff.getLeftExpression(), infoFlow);
        }
      }
      else {
        BusinessObject newBO = (BusinessObject) getInstanceForExpression((UniversalModelExpression) rightValue);
        InformationSystemInterface isi = getInterfaceForInfoFlow(diff.getLeftExpression(), diff.getType());
        Transport transport = BuildingBlockFactory.createTransport();
        transport.setInformationSystemInterface(isi);
        transport.setBusinessObject(newBO);
        transport.connect();
        transport.setDirection(getDirection(diff.getRightExpression(), diff.getType()));
      }
    }

    private void applyIS1Change(TwoSidedDiff diff, DiffPart part) {
      InformationSystemInterface isi = getInterfaceForInfoFlow(diff.getLeftExpression(), diff.getType());
      InformationSystemRelease isr1New = getISRFromDiffPart(part);
      InformationSystemRelease isr2 = isi.getInformationSystemReleaseB();
      if (!setInterfaceEnds(isi, isr1New, isr2)) {
        logWarning(
            "One or more InformationSystems of InformationSystemInterface \"{0}\" for InformationFlow \"{1}\" could not be set. Ignoring change.",
            isi, diff.getLeftExpression());
      }
    }

    private void applyIS2Change(TwoSidedDiff diff, DiffPart part) {
      InformationSystemInterface isi = getInterfaceForInfoFlow(diff.getLeftExpression(), diff.getType());
      InformationSystemRelease isr1 = isi.getInformationSystemReleaseA();
      InformationSystemRelease isr2New = getISRFromDiffPart(part);
      if (!setInterfaceEnds(isi, isr1, isr2New)) {
        logWarning(
            "One or more InformationSystems of InformationSystemInterface \"{0}\" for InformationFlow \"{1}\" could not be set. Ignoring change.",
            isi, diff.getLeftExpression());
      }
    }

    private InformationSystemRelease getISRFromDiffPart(DiffPart part) {
      Object rightValue = part.getRightValue();
      InformationSystemRelease isr = null;
      if (rightValue != null) {
        isr = (InformationSystemRelease) getInstanceForExpression((UniversalModelExpression) rightValue);
      }
      return isr;
    }

  }
}
