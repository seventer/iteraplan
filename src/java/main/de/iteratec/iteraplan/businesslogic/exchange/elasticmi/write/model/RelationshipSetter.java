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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.ElasticMiIteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.HbMappedClass;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.HbMappedProperty;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.WMetamodelExport;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticmi.diff.model.Connect;
import de.iteratec.iteraplan.elasticmi.diff.model.Disconnect;
import de.iteratec.iteraplan.elasticmi.diff.model.ObjectDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.RelationshipEndDiff;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression.OriginalWType;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;


/**
 * Sets the relationships of an iteraplan instance according to a {@link ObjectDiff}.
 */
public class RelationshipSetter extends AIteraplanInstanceWriter implements CreateOrUpdateDiffHandler {

  private static final Logger                           LOGGER = Logger.getIteraplanLogger(RelationshipSetter.class);

  private final BuildingBlockServiceLocator             bbServiceLocator;
  private final Map<ObjectExpression, ObjectExpression> rightToLeftMap;

  public RelationshipSetter(ElasticMiIteraplanMapping mapping, Map<BigInteger, BuildingBlock> id2bbMap, BuildingBlockServiceLocator bbServiceLocator,
      Map<ObjectExpression, ObjectExpression> rightToLeftMap, MessageListener listener) {
    super(mapping, id2bbMap, listener);
    this.bbServiceLocator = bbServiceLocator;
    this.rightToLeftMap = rightToLeftMap;
  }

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

  public boolean handleDiff(CreateOrUpdateDiff diff) {
    LOGGER.debug("Setting relationships for \"{0}\"...", diff);

    ObjectExpression objectExpression = diff.isLeft() ? diff.getLeft().getObjectExpression() : diff.getRight().getLeftObjectExpression();
    BuildingBlock buildingBlock = getInstanceForExpression(objectExpression);

    if (buildingBlock != null) {
      LOGGER.debug("Instance: {0}", buildingBlock);
      HbMappedClass hbClass = getHbClass(diff.getStructuredType());

      //special handling for interface ends of information flow
      setInterfaceEndsByTransport(buildingBlock, diff);
      //generic case
      for (RRelationshipEndExpression relationshipEnd : diff.getStructuredType().getAllRelationshipEnds()) {
        LOGGER.debug("Handling relationshipEnd: {0}", relationshipEnd.toString());
        if (!OriginalWType.RELATIONSHIP.equals(relationshipEnd.getType().getOriginalWType())) {
          setRelationship(relationshipEnd, hbClass, buildingBlock, getConnect(diff, relationshipEnd), getDisconnect(diff, relationshipEnd));
        }
      }

      checkVirtualElements(buildingBlock);
      checkInterfaceEnds(buildingBlock, objectExpression, diff.getStructuredType());
      checkTcAvailableForInterfaceCase(buildingBlock);
      if (isBusinessMappingInvalid(buildingBlock)) {
        removeInstanceMapping(objectExpression, buildingBlock);
        bbServiceLocator.getService(buildingBlock.getTypeOfBuildingBlock()).deleteEntity(buildingBlock);
      }
    }
    else {
      LOGGER.debug("No instance found for expression \"{0}\". Ignoring.", objectExpression);
    }

    LOGGER.debug("All relationships set.");
    return true;
  }

  private void setRelationship(RRelationshipEndExpression relEnd, HbMappedClass hbClass, BuildingBlock buildingBlock, Connect connect,
                               Disconnect disconnect) {
    LOGGER.debug("called handleRelationshipOfNewInstance");
    HbMappedProperty hbRelationship = findHbMappedProperty(hbClass, relEnd);

    LOGGER.debug("hbRelationShip: {0}", hbRelationship);

    if (hbRelationship != null && !(connect.getObjectExpressions().isNone() && disconnect.getObjectExpressions().isNone())) {
      BuildingBlock owningInstance = (BuildingBlock) findOwningInstance(hbClass, hbRelationship, buildingBlock);

      LOGGER.debug("Owning Instance: {0}", owningInstance);

      ElasticValue<BuildingBlock> toAdd = getBuildingBlocks(connect);
      ElasticValue<BuildingBlock> toRemove = getBuildingBlocks(disconnect);

      if (relEnd.getUpperBound() == 1) {
        LOGGER.debug("upper bound = 1");
        updateManyToOneRelation(owningInstance, hbRelationship, toAdd, toRemove, relEnd);
      }
      else {
        LOGGER.debug("upper bound > 1");
        updateManyRelation(owningInstance, hbRelationship, toAdd, toRemove, true);
      }
    }
  }

  private boolean isBusinessMappingInvalid(BuildingBlock bb) {
    if (!(bb instanceof BusinessMapping)) {
      return false;
    }
    BusinessMapping bm = (BusinessMapping) bb;
    try {
      bm.validate();
    } catch (IteraplanBusinessException e) {
      return true;
    }
    return false;
  }

  private void updateManyToOneRelation(BuildingBlock instance, HbMappedProperty hbRelationship, ElasticValue<BuildingBlock> toAdd,
                                       ElasticValue<BuildingBlock> toRemove, RRelationshipEndExpression relEnd) {
    LOGGER.debug("Updating relationship \"{0}\" of \"{1}\"...", hbRelationship.getName(), instance);
    try {
      if (relEnd.isDefining()) {
        setExistentialRelation(instance, hbRelationship, toAdd, toRemove);
      }
      else {
        setNonexistentialRelation(instance, hbRelationship, toAdd, toRemove);
      }
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

  private void setExistentialRelation(BuildingBlock instance, HbMappedProperty hbRelationship, ElasticValue<BuildingBlock> toAdd,
                                      ElasticValue<BuildingBlock> toRemove) throws IllegalAccessException, InvocationTargetException {
    if (toAdd.isOne() && isApplicableTarget(instance, toAdd.getOne())) {
      hbRelationship.getSetMethod().invoke(instance, toAdd.getOne());
    }
    else if (toRemove.isOne()) {
      removeManyToOneRelation(instance, hbRelationship, toRemove);
    }
  }

  private void setNonexistentialRelation(BuildingBlock instance, HbMappedProperty hbRelationship, ElasticValue<BuildingBlock> toAdd,
                                         ElasticValue<BuildingBlock> toRemove) throws IllegalAccessException, InvocationTargetException {
    if (toAdd.isOne() && isApplicableTarget(instance, toAdd.getOne())) {
      hbRelationship.getSetMethod().invoke(instance, toAdd.getOne());
      updateManyRelation(toAdd.getOne(), hbRelationship.getOpposite(), ElasticValue.one(instance), ElasticValue.<BuildingBlock> none(), false);
      if (toRemove.isOne()) {
        updateManyRelation(toRemove.getOne(), hbRelationship.getOpposite(), ElasticValue.<BuildingBlock> none(), ElasticValue.one(instance), false);
      }
    }
    else if (toRemove.isOne()) {
      removeManyToOneRelation(instance, hbRelationship, toRemove);
    }
  }

  private void removeManyToOneRelation(BuildingBlock instance, HbMappedProperty hbRelationship, ElasticValue<BuildingBlock> toRemove)
      throws IllegalAccessException, InvocationTargetException {
    hbRelationship.getSetMethod().invoke(instance, (Object) null);
    updateManyRelation(toRemove.getOne(), hbRelationship.getOpposite(), ElasticValue.<BuildingBlock> none(), ElasticValue.one(instance), false);
  }

  @SuppressWarnings("unchecked")
  private void updateManyRelation(BuildingBlock instance, HbMappedProperty hbRelationship, ElasticValue<BuildingBlock> toAdd,
                                  ElasticValue<BuildingBlock> toRemove, boolean callOpposite) {
    LOGGER.debug("Updating relationship \"{0}\" of \"{1}\"...", hbRelationship.getName(), instance);
    Set<BuildingBlock> validObjectsToAdd = extractApplicableTargets(instance, toAdd);
    LOGGER.debug("called updateRelationshipCollection");
    try {
      Object connected = hbRelationship.getGetMethod().invoke(instance);
      if (connected instanceof Collection) {
        Collection<BuildingBlock> collection = (Collection<BuildingBlock>) connected;
        //add only new elements
        removeElementsByIdOrHashCode(validObjectsToAdd, collection);
        //Remove elements by ID since the BuildingBlock hashCode is kaputt.
        removeElementsByIdOrHashCode(collection, toRemove.getMany());
        collection.addAll(validObjectsToAdd);
        if (callOpposite) {
          for (BuildingBlock bb : toAdd.getMany()) {
            updateManyRelation(bb, hbRelationship.getOpposite(), ElasticValue.one(instance), ElasticValue.<BuildingBlock> none(), false);
          }
          for (BuildingBlock bb : toRemove.getMany()) {
            updateManyRelation(bb, hbRelationship.getOpposite(), ElasticValue.<BuildingBlock> none(), ElasticValue.one(instance), false);
          }
        }
      }
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

  private static <C extends Collection<B>, B extends BuildingBlock> void removeElementsByIdOrHashCode(C fromCollection, C buildingBlocks) {
    Set<B> temp = Sets.newHashSet(fromCollection);
    fromCollection.clear();
    for (B bb : buildingBlocks) {
      removeElementByIdOrHashCode(temp, bb);
    }
    fromCollection.addAll(temp);
  }

  private static <C extends Collection<B>, B extends BuildingBlock> void removeElementByIdOrHashCode(C fromCollection, B buildingBlock) {
    //assuming exactly one element with a given ID exists
    B toRemove = null;
    for (B candidate : fromCollection) {
      if (candidate.getId() != null && candidate.getId().equals(buildingBlock.getId()) || candidate.equals(buildingBlock)) {
        toRemove = candidate;
        break;
      }
    }
    fromCollection.remove(toRemove);
  }

  private void checkVirtualElements(BuildingBlock bb) {
    LOGGER.debug("Checking whether there are null values assigned in relations of \"{0}\" where virtual elements should be...", bb);
    if (bb instanceof AbstractHierarchicalEntity) {
      checkParent((AbstractHierarchicalEntity<?>) bb);
    }
    if (bb instanceof BusinessMapping) {
      checkBMForNullelements((BusinessMapping) bb);
    }
  }

  private void checkInterfaceEnds(BuildingBlock instance, ObjectExpression interfaceOE, RStructuredTypeExpression interfaceType) {
    if (instance instanceof InformationSystemInterface) {
      InformationSystemInterface isi = (InformationSystemInterface) instance;
      LOGGER.debug("Check whether the interface has its information system releases set, and if not, correct this.");
      if (isi.getInformationSystemReleaseA() == null || isi.getInformationSystemReleaseB() == null) {
        RRelationshipEndExpression toInfoFlow = interfaceType.findRelationshipEndByPersistentName("informationFlows");
        //assuming there is at least one info flow, otherwise validation would fail.
        ObjectExpression infoFlowExpression = toInfoFlow.apply(interfaceOE).getMany().iterator().next();

        RRelationshipEndExpression toIsrA = toInfoFlow.getType().findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS1);
        RRelationshipEndExpression toIsrB = toInfoFlow.getType().findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS2);
        ObjectExpression isrAExpression = toIsrA.apply(infoFlowExpression).getOne();
        ObjectExpression isrBExpression = toIsrB.apply(infoFlowExpression).getOne();

        isi.addReleaseA((InformationSystemRelease) getInstanceForExpression(isrAExpression));
        isi.addReleaseB((InformationSystemRelease) getInstanceForExpression(isrBExpression));
        LOGGER.debug("Interface ends for \"{0}\" set.", isi);
      }
      else {
        LOGGER.debug("Interface ends are ok.");
      }
    }
  }

  private void setInterfaceEndsByTransport(BuildingBlock buildingBlock, CreateOrUpdateDiff diff) {
    if (!(buildingBlock instanceof Transport)) {
      return;
    }
    RStructuredTypeExpression infoFlowType = diff.getStructuredType();
    RRelationshipEndExpression toIsiRelEnd = infoFlowType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_ISI);
    ObjectExpression interfaceOE = toIsiRelEnd
        .apply(diff.isLeft() ? diff.getLeft().getObjectExpression() : diff.getRight().getLeftObjectExpression()).getOne();
    InformationSystemInterface bbIsi = (InformationSystemInterface) getInstanceForExpression(interfaceOE);

    HbMappedClass hbIsiClass = getHbClass(toIsiRelEnd.getType());

    RRelationshipEndExpression toIsr1End = infoFlowType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS1);
    RRelationshipEndExpression toIsr2End = infoFlowType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS2);
    Connect isr1EndConnect = getConnect(diff, toIsr1End);
    Disconnect isr1EndDisconnect = getDisconnect(diff, toIsr1End);
    Connect isr2EndConnect = getConnect(diff, toIsr2End);
    Disconnect isr2EndDisconnect = getDisconnect(diff, toIsr2End);

    //Only allow updates here. Deletion of interfaces must be done by an explicit interface delete change
    //since an implicit one (over information flows) can not be correctly recognized (interfaces can
    //exist without infoFlows and the deletion of all info flows does not imply the deletion of the interface).
    //Unsetting of the defining ends of the interface through the infoFlows does not imply deletion
    //of the interface either, because such a disconnect is produced by the deletion of the
    //infoFlow itself.
    if (isr1EndConnect.getObjectExpressions().isSome()) {
      setRelationship(toIsr1End, hbIsiClass, bbIsi, isr1EndConnect, isr1EndDisconnect);
    }
    if (isr2EndConnect.getObjectExpressions().isSome()) {
      setRelationship(toIsr2End, hbIsiClass, bbIsi, isr2EndConnect, isr2EndDisconnect);
    }
  }

  private void checkTcAvailableForInterfaceCase(BuildingBlock instance) {
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

  private boolean isApplicableTarget(BuildingBlock instance, BuildingBlock newTarget) {
    if (instance instanceof TechnicalComponentRelease && newTarget instanceof InformationSystemInterface) {
      return ((TechnicalComponentRelease) instance).isAvailableForInterfaces();
    }
    else if (instance instanceof InformationSystemInterface && newTarget instanceof TechnicalComponentRelease) {
      return ((TechnicalComponentRelease) newTarget).isAvailableForInterfaces();
    }
    return true;
  }

  private Set<BuildingBlock> extractApplicableTargets(BuildingBlock instance, ElasticValue<BuildingBlock> toAdd) {
    Set<BuildingBlock> result = Sets.newHashSet();
    for (BuildingBlock candidate : toAdd.getMany()) {
      if (isApplicableTarget(instance, candidate)) {
        result.add(candidate);
      }
    }
    return result;
  }

  private Connect getConnect(CreateOrUpdateDiff diff, RRelationshipEndExpression relEnd) {
    if (diff.isLeft()) {
      return diff.getLeft().getConnect(relEnd);
    }
    else {
      return getMergeStrategy().filterConnect(diff.getRight(), diff.getRight().getConnect(relEnd));
    }
  }

  private Disconnect getDisconnect(CreateOrUpdateDiff diff, RRelationshipEndExpression relEnd) {
    if (diff.isLeft()) {
      return Disconnect.emptyDisconnect(relEnd);
    }
    else {
      return getMergeStrategy().filterDisconnect(diff.getRight(), diff.getRight().getDisconnect(relEnd));
    }
  }

  /**
   * Returns the building blocks matching the ObjectExpressions
   * referenced in the given {@link Connect} or {@link Disconnect} object.
   * @param reeDiff
   *        {@link Connect} or {@link Disconnect} for which the building blocks need to be determined
   * @return ElasticValue of building blocks
   */
  private ElasticValue<BuildingBlock> getBuildingBlocks(RelationshipEndDiff<?> reeDiff) {
    return ElasticValue.many(getInstanceSetForExpressions(reeDiff.getObjectExpressions().getMany()));
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

  private Set<BuildingBlock> getInstanceSetForExpressions(Collection<ObjectExpression> expressions) {
    if (expressions == null || expressions.isEmpty()) {
      return Sets.newHashSet();
    }

    Set<BuildingBlock> instances = Sets.newHashSet();
    for (ObjectExpression expression : expressions) {
      BuildingBlock instance = getInstanceForExpression(expression);
      if (instance != null) {
        instances.add(instance);
      }
    }
    return instances;
  }

  @Override
  protected BuildingBlock getInstanceForExpression(ObjectExpression objectExpression) {
    if (objectExpression == null) {
      return null;
    }
    ObjectExpression matchedExpression = rightToLeftMap.get(objectExpression);
    if (matchedExpression == null) {
      matchedExpression = objectExpression;
    }
    return super.getInstanceForExpression(matchedExpression);
  }
}
