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
package de.iteratec.iteraplan.persistence.elasticeam.model.diff;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.common.DefaultSpringApplicationContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.emf.EObjectConverter;
import de.iteratec.iteraplan.elasticeam.metamodel.emf.EPackageConverter;
import de.iteratec.iteraplan.elasticeam.metamodel.emf.Mapping;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.diff.AbstractModelElementChange;
import de.iteratec.iteraplan.elasticeam.model.diff.AbstractModelElementChange.TypeOfModelElementChange;
import de.iteratec.iteraplan.elasticeam.model.diff.ModelElementChangeFactory;
import de.iteratec.iteraplan.elasticeam.model.diff.ModelWriter;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;


/**
 * Calculate classified {@link AbstractModelElementChange}s describing the differences between two {@link Model}s
 * and can apply these changes to the base model.
 */
public class EMFModelWriter extends ModelWriter {

  private static final Logger                                             LOGGER = Logger.getIteraplanLogger(EMFModelWriter.class);

  private IteraplanMapping                                                mapping;
  private BiMap<Object, UniversalModelExpression>                         instanceMapping;
  private Map<TypeOfModelElementChange, List<AbstractModelElementChange>> changes;
  private Session                                                         session;
  private Transaction                                                     transaction;

  public EMFModelWriter(IteraplanMapping mapping, Model model, BiMap<Object, UniversalModelExpression> instanceMapping, Model modified) {
    super(model, modified, mapping.getMetamodel());
    this.mapping = mapping;
    this.instanceMapping = instanceMapping;
    compareAndClassifyChanges();
  }

  public EMFModelWriter(Model base, Model modified, Metamodel metamodel) {
    super(base, modified, metamodel);
    compareAndClassifyChanges();
  }

  /**{@inheritDoc}**/
  @Override
  protected final void compareAndClassifyChanges() {
    changes = initChangeMap();
    List<DiffElement> differences = getDifferences(getBaseModel(), getModifiedModel(), getMetamodel(), eContainerClass());
    for (DiffElement diff : differences) {
      LOGGER.info("Creating AbstractModelElementChange for difference \"{0}\"...", diff);
      AbstractModelElementChange change = null;
      if (instanceMapping == null) {
        change = ModelElementChangeFactory.INSTANCE.createModelElementChange(diff, getBaseModel(), getModifiedModel(), getMetamodel());
      }
      else {
        change = EMFModelElementChangeFactory.INSTANCE.createModelElementChange(diff, mapping, getBaseModel(), instanceMapping, getModifiedModel());
      }
      LOGGER.info("Change \"{0}\" created and classified as \"{1}\"", change, change.getTypeOfModelDifference());

      changes.get(change.getTypeOfModelDifference()).add(change);
      LOGGER.debug(diff.getKind() + "(" + diff.getClass().getSimpleName() + ") => " + change.getTypeOfModelDifference().name() + "("
          + change.getClass().getSimpleName() + ")");
    }
  }

  protected static List<DiffElement> getDifferences(Model base, Model modified, Metamodel metamodel, EClass eClass) {
    LOGGER.info("Calculating model differences...");
    Mapping<Metamodel> metamodelMapping = EPackageConverter.deriveMapping(metamodel, false);
    EObject baseContainer = getEContainer(eClass, base, metamodelMapping, "model");
    EObject modifiedContainer = getEContainer(eClass, modified, metamodelMapping, "model");
    Map<String, Object> options = Maps.newHashMap();
    try {
      MatchModel matchModel = MatchService.doContentMatch(modifiedContainer, baseContainer, options);
      DiffModel diffModel = DiffService.doDiff(matchModel);
      List<DiffElement> differences = diffModel.getDifferences();
      LOGGER.info("{0} differences found.", Integer.valueOf(differences.size()));
      return differences;
    } catch (InterruptedException e) {
      LOGGER.error(e);
      throw new ModelException(ModelException.GENERAL_ERROR, "Could not compare resources " + baseContainer + " and " + modifiedContainer);
    }
  }

  protected static EObject getEContainer(EClass eContainerClass, Model model, Mapping<Metamodel> metamodelMapping, String resource) {
    metamodelMapping.getEPackage().getEClassifiers().add(eContainerClass);
    EObject eContainer = eContainerClass.getEPackage().getEFactoryInstance().create(eContainerClass);
    eContainer.eSet(eContainerClass.getEStructuralFeature("id"), resource);
    eContainer.eSet(eContainerClass.getEStructuralFeature("contains"), EObjectConverter.export(metamodelMapping, model));
    metamodelMapping.getEPackage().getEClassifiers().remove(eContainerClass);
    return eContainer;
  }

  private EClass eContainerClass() {
    EReference containmentReference = EcoreFactory.eINSTANCE.createEReference();
    containmentReference.setName("contains");
    containmentReference.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
    containmentReference.setUnique(true);
    containmentReference.setEType(EcoreFactory.eINSTANCE.createEObject().eClass());
    containmentReference.setContainment(true);

    EAttribute id = EcoreFactory.eINSTANCE.createEAttribute();
    id.setName("id");
    id.setID(true);
    id.setLowerBound(1);
    id.setUpperBound(1);
    id.setEType(EcorePackage.eINSTANCE.getEString());

    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Container");

    eClass.getEStructuralFeatures().add(id);
    eClass.getEStructuralFeatures().add(containmentReference);

    return eClass;
  }

  /**{@inheritDoc}**/
  @Override
  public Map<TypeOfModelElementChange, List<AbstractModelElementChange>> getChanges() {
    return changes;
  }

  /**{@inheritDoc}**/
  @Override
  protected void validateCompatibility() {
    //TODO implement more specific compatibility checks;
  }

  /**{@inheritDoc}**/
  @Override
  public Map<TypeOfModelElementChange, List<AbstractModelElementChange>> applyChanges(Set<TypeOfModelElementChange> ignoredTypes,
                                                                                      Set<AbstractModelElementChange> ignoredChanges) {
    beginTransation();
    Map<TypeOfModelElementChange, List<AbstractModelElementChange>> applied = super.applyChanges(ignoredTypes, ignoredChanges);

    LOGGER.info("Persisting changes-related entities...");
    for (TypeOfModelElementChange type : TypeOfModelElementChange.ALL) {
      LOGGER.info("Handling changes of type \"{0}\"...", type);
      for (AbstractModelElementChange change : applied.get(type)) {
        LOGGER.info("Change \"{0}\"...", change);
        Object entity = null;
        if (change instanceof EMFInstanceDeletion) {
          entity = getEntityToDelete(change);
          if (entity != null) {
            session.delete(entity);
            LOGGER.info("Entity \"{0}\" deleted.", entity);
          }
        }
        else if (!(change instanceof TechnicalEMFModelElementChange)) {
          entity = getEntityToPersist(change);
          if (entity != null) {
            session.saveOrUpdate(entity);
            LOGGER.info("Entity \"{0}\" persisted.", entity);
          }
        }
      }
    }
    setDefaultParents(applied.get(TypeOfModelElementChange.ADD_INSTANCE));
    // Commit automatically done when returning to the frontend. Not necessary here, then?
    //    commit();
    return applied;
  }

  private Object getEntityToDelete(AbstractModelElementChange change) {
    Object toPersist;
    LOGGER.debug("Getting entity to delete...");
    EMFInstanceDeletion deletion = (EMFInstanceDeletion) change;
    toPersist = instanceMapping.inverse().get(deletion.getDeletedModelExpression());
    LOGGER.debug("Entity to delete: {0}", toPersist);
    return toPersist;
  }

  private Object getEntityToPersist(AbstractModelElementChange change) {
    Object toPersist = null;

    LOGGER.debug("Getting entity to persist...");
    if (change instanceof EMFInstanceExpressionCreation) {
      EMFInstanceExpressionCreation newInstance = (EMFInstanceExpressionCreation) change;
      toPersist = instanceMapping.inverse().get(newInstance.getNewCreatedModelExpression());
      LOGGER.debug("Entity to persist: {0}", toPersist);
    }
    else if (change instanceof EMFLinkExpressionCreation) {
      EMFLinkExpressionCreation newRelationshipInstance = (EMFLinkExpressionCreation) change;
      toPersist = instanceMapping.inverse().get(newRelationshipInstance.getNewCreatedModelExpression());
      LOGGER.debug("Entity to persist: {0}", toPersist);
    }
    else if (change instanceof EMFPropertyValueChange) {
      EMFPropertyValueChange valChange = (EMFPropertyValueChange) change;
      toPersist = instanceMapping.inverse().get(valChange.getModelExpressionInCurrentModel());
      LOGGER.debug("Entity to persist: {0}", toPersist);
    }
    else if (change instanceof EMFLinkChange) {
      EMFLinkChange linkChange = (EMFLinkChange) change;
      toPersist = instanceMapping.inverse().get(linkChange.getSource());
      LOGGER.debug("Entity to persist: {0}", toPersist);
    }
    else {
      LOGGER.error("Unhandled " + TypeOfModelElementChange.class.getSimpleName() + " detected " + change + "("
          + change.getTypeOfModelDifference().name() + ")");
    }
    return toPersist;
  }

  /**
   * In case of persisting changes to the iteraplan db, a new transaction should be created before applying any changes
   */
  private void beginTransation() {
    if (session == null && transaction == null) {
      //TODO remove context.getBean(...)
      SessionFactory sessionFactory = (SessionFactory) DefaultSpringApplicationContext.getSpringApplicationContext().getBean("sessionFactory");
      this.session = sessionFactory.getCurrentSession();
      session.setFlushMode(FlushMode.COMMIT);
      if (!session.isOpen()) {
        session = sessionFactory.openSession();
      }
      this.transaction = this.session.beginTransaction();
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR,
          "Cannot begin new transaction while there is another active one alive");
    }

  }

  //  /**
  //   * After changes have been applied, changes need to be written to iteraplan db
  //   */
  //  private void commit() {
  //    //TODO make sure transaction won't be committed before this method (currently AttributeValueService.saveOrUpdate(attributeValue) actually commits transaction
  //    if (session != null && (session.isOpen() || session.isDirty())) {
  //      session.flush();
  //    }
  //    if (transaction != null && !transaction.wasCommitted()) {
  //      transaction.commit();
  //    }
  //    AttributeValueService service = (AttributeValueService) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
  //        "attributeValueService");
  //    service.removeOrphanedAttributeValuesAndAssignments();
  //  }

  /**
   * Set parent of each new created {@link AbstractHierarchicalEntity} to its virtual element if its parent has not been set yet.
   * 
   * @param newInstanceChanges
   *    A {@link List} with all changes of type {@link TypeOfModelElementChange#ADD_INSTANCE}
   */
  private void setDefaultParents(List<AbstractModelElementChange> newInstanceChanges) {
    if (!newInstanceChanges.isEmpty()) {
      LOGGER.info("Setting default parents for HierarchicalEntities if necessary...");
      Map<Class<?>, AbstractHierarchicalEntity<?>> virtualElements = Maps.newHashMap();
      try {
        Method addParent = AbstractHierarchicalEntity.class.getMethod("addParent", HierarchicalEntity.class);
        for (AbstractModelElementChange change : newInstanceChanges) {
          if (change instanceof EMFInstanceExpressionCreation) {
            EMFInstanceExpressionCreation newInstanceChange = (EMFInstanceExpressionCreation) change;
            BuildingBlock bb = (BuildingBlock) instanceMapping.inverse().get(newInstanceChange.getNewCreatedModelExpression());
            if (bb instanceof AbstractHierarchicalEntity<?>) {
              setDefaultParentForHierarchicalEntity(bb, virtualElements, addParent);
              session.save(bb);
            }
          }
          else {
            LOGGER.error("EMFModelWriter cannoth persist change: " + change);
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
      } catch (SecurityException e) {
        LOGGER.error(e);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      } catch (NoSuchMethodException e) {
        LOGGER.error(e);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      }
      LOGGER.info("Setting of default parents done.");
    }

  }

  private void setDefaultParentForHierarchicalEntity(BuildingBlock bb, Map<Class<?>, AbstractHierarchicalEntity<?>> virtualElements, Method addParent)
      throws IllegalAccessException, InvocationTargetException {
    LOGGER.info("Checking \"{0}\" for its parent...", bb);
    AbstractHierarchicalEntity<?> hierarchicalEntity = (AbstractHierarchicalEntity<?>) bb;
    if (hierarchicalEntity.getParent() == null) {
      LOGGER.info("\"{0}\" has no parent. Setting virtual element as parent...", bb);
      if (!virtualElements.containsKey(hierarchicalEntity.getClass())) {
        LOGGER.info("Finding virtual element for building block type \"{0}\"...", bb.getClass().getSimpleName());
        String serviceName = bb.getClass().getSimpleName().substring(0, 1).toLowerCase() + bb.getClass().getSimpleName().substring(1) + "Service";
        BuildingBlockService<?, ?> service = (BuildingBlockService<?, ?>) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
            serviceName);
        Set<String> names = Sets.newHashSet(AbstractHierarchicalEntity.TOP_LEVEL_NAME);
        AbstractHierarchicalEntity<?> virtualElement = (AbstractHierarchicalEntity<?>) service.findByNames(names).get(0);
        virtualElements.put(bb.getClass(), virtualElement);
      }
      AbstractHierarchicalEntity<?> virtualElement = virtualElements.get(hierarchicalEntity.getClass());
      addParent.invoke(bb, virtualElement);
      LOGGER.info("Parent of \"{0}\" set to \"{1} ({2})\"", bb, virtualElement, virtualElement.getClass().getSimpleName());
    }
    else {
      LOGGER.info("Parent for \"{0}\" is set already.", bb);
    }
  }

  private static Map<TypeOfModelElementChange, List<AbstractModelElementChange>> initChangeMap() {
    Map<TypeOfModelElementChange, List<AbstractModelElementChange>> changeMap = Maps.newLinkedHashMap();
    for (TypeOfModelElementChange typeOfChange : TypeOfModelElementChange.ALL) {
      List<AbstractModelElementChange> emptyList = Lists.newArrayList();
      changeMap.put(typeOfChange, emptyList);
    }
    return changeMap;
  }

}
