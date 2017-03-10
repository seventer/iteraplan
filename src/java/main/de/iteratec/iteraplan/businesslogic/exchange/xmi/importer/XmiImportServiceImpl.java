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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.importer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.EntityBeanIntrospectionServiceBean;
import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.EntityClassStore;
import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.Iteraplan2EMFHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.StringUtil;
import de.iteratec.iteraplan.model.AbstractAssociation;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.sorting.HierarchyLevelComparator;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.User;


/**
 * Imports the iteraplan Entities from the specified XMI file.
 */
public class XmiImportServiceImpl implements XmiImportService {
  private static final Logger                                 LOGGER                = Logger.getIteraplanLogger(XmiImportServiceImpl.class);
  private static final String                                 IGNORE_CASE_OLVERSION = "olVersion";
  private static final Map<String, Class<? extends IdEntity>> NAME_CLASS_MAPPINGS   = EntityClassStore.getNameToClassMappings();
  private static final Set<String>                            INITIALDATA_ROLES     = ImmutableSet.of("iteraplan_Supervisor", "MainUser");

  private final SessionHelper                                 sessionHelper;
  private final EntityBeanIntrospectionServiceBean            introspectionBean;
  private final XmiSavedQueriesHelper                         savedQueriesHelper;
  private final ThreadLocal<ImportContext>                    localContext          = new ThreadLocal<ImportContext>();

  public XmiImportServiceImpl(SessionHelper sessionHelper, EntityBeanIntrospectionServiceBean introspectionBean,
      XmiSavedQueriesHelper savedQuerieHelper) {
    this.sessionHelper = sessionHelper;
    this.introspectionBean = introspectionBean;
    this.savedQueriesHelper = savedQuerieHelper;

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
  }

  /** {@inheritDoc} */
  public void importXmi(InputStream xmiInputStream) {
    List<EObject> xmiObjects = initXmiDeserialization(xmiInputStream);

    ImportContext context = new ImportContext();
    localContext.set(context);

    try {
      Map<IdEntity, EObject> createObjects = createObjects(xmiObjects);
      getEntityToEObjectMap().putAll(createObjects);
      importEObjects(createObjects);
    } catch (Exception e) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ILLEGAL_XMI_FILE_DATA, e, StringUtil.removePathFromFileURIString(e.getMessage()));
    } finally {
      localContext.remove();
    }
  }

  /** {@inheritDoc} */
  public void importInitialXmi(InputStream xmiInputStream, boolean importAttributeTypes) {
    List<EObject> xmiObjects = initXmiDeserialization(xmiInputStream);

    ImportContext context = new ImportContext();
    localContext.set(context);

    try {
      Map<IdEntity, EObject> createObjects = filterInitialData(createObjects(xmiObjects), importAttributeTypes);
      getEntityToEObjectMap().putAll(createObjects);
      importEObjects(createObjects);
    } catch (Exception e) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ILLEGAL_XMI_FILE_DATA, e, StringUtil.removePathFromFileURIString(e.getMessage()));
    } finally {
      localContext.remove();
    }
  }

  private void importEObjects(Map<IdEntity, EObject> createObjects) {
    final Map<EObject, IdEntity> eObjectsMap = getEObjectsMap();
    persistCoreObjects(createObjects.values(), eObjectsMap);

    Set<String> relationClassNames = Sets.newHashSet("BusinessMapping", "Tcr2IeAssociation", "Isr2BoAssociation");

    Predicate<EObject> relationsClassNamePredicate = new EObjectClassNamePredicate(relationClassNames);
    Iterable<EObject> relationsFiltered = Iterables.filter(createObjects.values(), relationsClassNamePredicate);
    List<EObject> relations = Lists.newArrayList(relationsFiltered);

    Predicate<EObject> nonRelationsClassNamePredicate = Predicates.not(relationsClassNamePredicate);
    Iterable<EObject> nonRelationsFiltered = Iterables.filter(createObjects.values(), nonRelationsClassNamePredicate);
    List<EObject> nonRelations = Lists.newArrayList(nonRelationsFiltered);

    final Map<IdEntity, EObject> objectToEObject = getEntityToEObjectMap();
    persistBuildingBlocks(objectToEObject, eObjectsMap, nonRelations);

    setAllReferences(objectToEObject);

    List<EObject> persistentObjectsRel = EntityClassStore.getPersistentObjects(relations);
    List<IdEntity> convertRel = EntityClassStore.convert(persistentObjectsRel, eObjectsMap);
    persistAssociationsWithoutAVA(convertRel);

    List<EObject> persistentObjects2 = EntityClassStore.getPersistentObjects(nonRelations);
    List<IdEntity> convert = EntityClassStore.convert(persistentObjects2, eObjectsMap);
    persistOrderedObjects(convert);

    savedQueriesHelper.update(objectToEObject);
  }

  /**
   * Filters the specified map to leave only the initial data.
   * 
   * @param entityToEObjectMap the map containing the entities associated with the {@link EObject} instances
   * @param importAttributeTypes decides whether basic attribute types will be imported or not
   * @return the initial data entities
   */
  private Map<IdEntity, EObject> filterInitialData(Map<IdEntity, EObject> entityToEObjectMap, boolean importAttributeTypes) {
    Map<IdEntity, EObject> result = Maps.newHashMap();
    List<Class<? extends IdEntity>> coreClasses = EntityClassStore.getCoreClasses();

    for (Entry<IdEntity, EObject> entry : entityToEObjectMap.entrySet()) {
      if (coreClasses.contains(entry.getKey().getClass())) {
        filterForCoreClasses(entry, result, importAttributeTypes);
      }
      else {
        filterForNonCoreClasses(entry, result, importAttributeTypes);
      }
    }

    return result;
  }

  private void filterForNonCoreClasses(Entry<IdEntity, EObject> entry, Map<IdEntity, EObject> result, boolean importAttributeTypes) {
    IdEntity entity = entry.getKey();
    Class<? extends IdEntity> entityClass = entity.getClass();

    if (AbstractHierarchicalEntity.class.isAssignableFrom(entityClass)) {
      AbstractHierarchicalEntity<?> hEntity = (AbstractHierarchicalEntity<?>) entity;
      if (hEntity.isTopLevelElement()) {
        result.put(entity, entry.getValue());
      }
    }
    else if (AttributeTypeGroup.class.equals(entityClass)) {
      AttributeTypeGroup atg = (AttributeTypeGroup) entity;
      if (importAttributeTypes || StringUtils.equals(atg.getName(), "[Default Attribute Group]")) {
        result.put(entity, entry.getValue());
      }
    }
    else if (Role2BbtPermission.class.equals(entityClass)) {
      // add role-to-permission link only if the role has been included for import
      if (isPermissionLinkedToInitialRole(entry.getValue())) {
        result.put(entity, entry.getValue());
      }

    }
    else if (importAttributeTypes && AttributeType.class.isAssignableFrom(entityClass)) {
      result.put(entity, entry.getValue());
    }
  }

  /**
   * Inspects the passed Role2BbtPermission EObject for whether it is linked to a role that is part of the initial data, or not.
   */
  private boolean isPermissionLinkedToInitialRole(EObject eRole2Bbt) {
    for (EStructuralFeature esf : eRole2Bbt.eClass().getEAllReferences()) {
      if (!esf.getName().equalsIgnoreCase("role")) {
        continue;
      }
      EObject referencedEnd = (EObject) eRole2Bbt.eGet(esf);
      for (EAttribute attr : referencedEnd.eClass().getEAllAttributes()) {
        if (!attr.getName().equalsIgnoreCase("roleName")) {
          continue;
        }
        Object roleName = referencedEnd.eGet(attr);
        if (INITIALDATA_ROLES.contains(roleName)) {
          return true;
        }
      }

    }
    return false;
  }

  private void filterForCoreClasses(Entry<IdEntity, EObject> entry, Map<IdEntity, EObject> result, boolean importAttributeTypes) {
    IdEntity entity = entry.getKey();
    Class<? extends IdEntity> entityClass = entity.getClass();

    if (SavedQuery.class.isAssignableFrom(entityClass)) {
      // skip import, as saved queries don't work without any building blocks
      return;
    }

    if (User.class.isAssignableFrom(entityClass)) {
      User user = (User) entity;
      if (StringUtils.equals(user.getLoginName(), "system")) {
        result.put(entity, entry.getValue());
      }
    }
    else if (PermissionAttrTypeGroup.class.isAssignableFrom(entityClass)) {
      if (importAttributeTypes) {
        /*
         * don't import PermissionAttrTypeGroups when not importing attributes for initial data
         * because they can refer to not imported AttributeTypeGroups and cause an error then.
         * The Default Attribute Group, which is imported, does not have any associated
         * Permissions at the time of this implementation.
         * FIXME Should this change, these permissions won't be imported, if importAttributeTypes is false
         */
        result.put(entity, entry.getValue());
      }
    }
    else if (Role.class.isAssignableFrom(entityClass)) {
      Role role = (Role) entity;
      if (INITIALDATA_ROLES.contains(role.getRoleName())) {
        result.put(entity, entry.getValue());
      }
    }
    else {
      result.put(entity, entry.getValue());
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void persistBuildingBlocks(final Map<IdEntity, EObject> objectToEObject, final Map<EObject, IdEntity> eObjectsMap,
                                     List<EObject> nonRelations) {
    setBuildingBlockRelations(objectToEObject);
    Multimap<Class<? extends IdEntity>, EObject> persistentObjects = EntityClassStore.groupEObjects(nonRelations);
    List<Class<? extends BuildingBlock>> persistOrder = EntityClassStore.buildingBlockClasses();
    for (Class<? extends IdEntity> entityClass : persistOrder) {
      Collection<EObject> collection = persistentObjects.get(entityClass);
      if (collection == null) {
        continue;
      }

      List<IdEntity> entities = EntityClassStore.convert(collection, eObjectsMap);

      if (AbstractHierarchicalEntity.class.isAssignableFrom(entityClass)) {
        List<AbstractHierarchicalEntity<?>> buildingBlocks = XmiHelper.castList(entities);
        Collections.sort(buildingBlocks, new HierarchyLevelComparator());
        persistOrderedObjects(buildingBlocks);
      }
      else {
        List<BuildingBlock> buildingBlocks = XmiHelper.castList(entities);
        Collections.sort(buildingBlocks);
        persistOrderedObjects(buildingBlocks);
      }
    }
  }

  private void persistCoreObjects(Collection<EObject> eObjects, Map<EObject, IdEntity> eObjectsMap) {
    List<EObject> coreEObjects = EntityClassStore.getCoreEntities(eObjects);
    List<IdEntity> coreEntities = EntityClassStore.convert(coreEObjects, eObjectsMap);
    persistOrderedObjects(coreEntities);
  }

  private Map<IdEntity, EObject> createObjects(List<EObject> eObjects) {
    Map<IdEntity, EObject> result = Maps.newHashMap();
    for (EObject eObject : eObjects) {
      IdEntity entity = createObject(eObject);

      if (entity != null) {
        result.put(entity, eObject);
      }
    }

    return result;
  }

  private IdEntity createObject(EObject eObject) {
    String className = eObject.eClass().getName();
    if (!NAME_CLASS_MAPPINGS.containsKey(className) || className.equals(RuntimePeriod.class.getSimpleName())) {
      return null;
    }

    Class<? extends IdEntity> entityClass = NAME_CLASS_MAPPINGS.get(className);
    IdEntity entity = XmiHelper.getNewObject(entityClass);
    setEAttributes(entity, eObject);

    if (entity != null) {
      return useExistingInstanceFromDb(eObject, entity);
    }

    return null;
  }

  private void setBuildingBlockRelations(Map<IdEntity, EObject> objectToEObject) {
    for (Entry<IdEntity, EObject> entry : objectToEObject.entrySet()) {
      IdEntity entity = entry.getKey();
      EObject eObject = entry.getValue();

      if (entity instanceof BuildingBlock) {
        setReferences(entity, eObject, Sets.newHashSet("buildingBlockType"));

        if (entity instanceof AbstractHierarchicalEntity<?>) {
          setReferences(entity, eObject, Sets.newHashSet("parent"));
        }

        BuildingBlock bb = (BuildingBlock) entity;
        if (bb.getTypeOfBuildingBlock() == TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE) {
          setReferences(entity, eObject, Sets.newHashSet("informationSystem"));
        }
        else if (bb.getTypeOfBuildingBlock() == TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE) {
          setReferences(entity, eObject, Sets.newHashSet("technicalComponent"));
        }
        else if (bb.getTypeOfBuildingBlock() == TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE) {
          setReferences(entity, eObject, Sets.newHashSet("informationSystemReleaseA", "informationSystemReleaseB"));
        }
        else if (bb.getTypeOfBuildingBlock() == TypeOfBuildingBlock.TRANSPORT) {
          setReferences(entity, eObject, Sets.newHashSet("businessObject", "informationSystemInterface"));
        }
      }
    }
  }

  private IdEntity useExistingInstanceFromDb(EObject eObject, IdEntity entity) {
    IdEntity instanceInDb = sessionHelper.getExistingInstanceFromDB(entity.getClass(), entity.getId());
    if (instanceInDb != null) {
      setEAttributes(instanceInDb, eObject);
      return instanceInDb;
    }
    else {
      entity.setId(null);
      return entity;
    }
  }

  /**
   * Method to set all Attributes for the created Object
   * 
   * @param entity the entity instance
   * @param eObject the corresponding EObject
   */
  private void setEAttributes(IdEntity entity, EObject eObject) {
    for (EAttribute eAtt : eObject.eClass().getEAllAttributes()) {
      if (eAtt.getName().equals(IGNORE_CASE_OLVERSION)) {
        continue;
      }

      if (eAtt.isID()) {
        Integer eId = XmiHelper.getEId(eObject);
        if (entity.getId() == null && eId != null) {
          entity.setId(eId);
        }
      }
      else {
        Object value = determineEAttValue(entity, eObject, eAtt);
        if (value != null) {
          XmiHelper.invokeWriteMethod(entity, value, eAtt, sessionHelper);
        }
      }
    }
  }

  private Object determineEAttValue(IdEntity entity, EObject eObject, EAttribute eAtt) {
    if (eAtt.isMany()) {
      return getManyValue(entity, eObject, eAtt);
    }
    else if (eAtt.getEType() instanceof EEnum) {
      Method writeMethod = XmiHelper.getWriteMethod(eAtt, entity.getClass());
      return enumObjectForEEnum(eObject.eGet(eAtt), writeMethod);
    }
    else {
      return objectForEObject(eObject.eGet(eAtt));
    }
  }

  private void setAllReferences(Map<IdEntity, EObject> objectToEObjectMap) {
    for (Entry<IdEntity, EObject> entry : objectToEObjectMap.entrySet()) {
      setReferences(entry.getKey(), entry.getValue());
    }
  }

  /**
   * sets all References of the object and updates the referents
   * 
   * @param instance
   */
  private void setReferences(IdEntity instance, EObject eInstance) {
    LOGGER.debug("Setting references on {0} instance", instance.getClass());
    for (EReference esf : eInstance.eClass().getEAllReferences()) {
      setReferenceValue(instance, eInstance, esf);
    }
  }

  private void setReferences(IdEntity instance, EObject eInstance, Set<String> referenceNames) {
    for (EReference esf : eInstance.eClass().getEAllReferences()) {
      if (referenceNames.contains(esf.getName())) {
        setReferenceValue(instance, eInstance, esf);
      }
    }
  }

  private void setReferenceValue(IdEntity entity, EObject eInstance, EReference esf) {
    Object value = null;
    if (esf.isMany()) {
      // value will be null!;
      // @see getManyValue
      value = getManyValue(entity, eInstance, esf);
    }
    else {
      if (esf.getEType() instanceof EEnum) {
        Method writeMethod = XmiHelper.getWriteMethod(esf, entity.getClass());
        value = enumObjectForEEnum(eInstance.eGet(esf), writeMethod);
      }
      else if (esf.getEType().getName().equals(RuntimePeriod.class.getSimpleName())) {
        value = createRuntimePeriodInstance((EObject) eInstance.eGet(esf));
      }
      else {
        value = objectForEObject(eInstance.eGet(esf));
      }
    }

    if (value != null && !(value instanceof EObject)) {
      XmiHelper.invokeWriteMethod(entity, value, esf, sessionHelper);
    }
  }

  private Object createRuntimePeriodInstance(EObject eInstance) {
    if (eInstance == null) {
      return null;
    }

    Date start = (Date) eInstance.eGet(eInstance.eClass().getEStructuralFeature("start"));
    Date end = (Date) eInstance.eGet(eInstance.eClass().getEStructuralFeature("end"));

    return new RuntimePeriod(start, end);
  }

  /**
   * Method to get a Java.lang.Object-value for the given EMF-value
   * 
   * @param eObject the given EMF-Value
   * @return the corresponding Java.lang.Object-value
   */
  private Object objectForEObject(Object eObject) {
    if (eObject == null) {
      return null;
    }

    if (getEObjectsMap().containsKey(eObject)) {
      return getEObjectsMap().get(eObject);
    }

    return eObject;
  }

  /**
   * Method to get an Enumeration Value
   * 
   * @param object the EEnumLiteral value
   * @param writeMethod the writeMethod for the corresponding enumeration-attribute
   * @return the enum-value for the given EEnumLiteral value
   */
  private Object enumObjectForEEnum(Object object, Method writeMethod) {
    EEnumLiteral eEnumLiteral = (EEnumLiteral) object;
    try {
      String name = writeMethod.getParameterTypes()[0].getName();
      Class<?> enumClass = Class.forName(name);
      return enumClass.getEnumConstants()[eEnumLiteral.getValue()];
    } catch (ClassNotFoundException e) {
      LOGGER.error(e);
      return null;
    }
  }

  /**
   * Method to get the many-value to the corresponding many-valued EstructuralFeature
   * 
   * @param instance the object where the given esf will be set
   * @param eObject the corresponding EObject
   * @param esf the actual EStructuralFeature
   * @return null
   */
  @SuppressWarnings("unchecked")
  private Object getManyValue(IdEntity instance, EObject eObject, EStructuralFeature esf) {
    Collection<Object> resultSet = (Collection<Object>) XmiHelper.invokeReadMethod(instance, esf);
    for (EObject o : (EList<EObject>) eObject.eGet(esf)) {
      Object entity = objectForEObject(o);

      if (entity instanceof EObject) {
        continue;
      }

      //TODO AGU This is a hack to prevent duplicate AttributeValue instances
      if (entity instanceof AttributeValue) {
        AttributeValue newAv = (AttributeValue) entity;
        setReferences(newAv, o);

        handleAttributeValue(resultSet, entity, newAv);
      }
      else if (!resultSet.contains(entity)) {
        resultSet.add(entity);
      }
    }

    return null;
  }

  private void handleAttributeValue(Collection<Object> resultSet, Object entity, AttributeValue newAv) {
    if (resultSet instanceof List) {
      AttributeValue existingAv = null;
      for (Object object : resultSet) {
        AttributeValue av = (AttributeValue) object;
        if (Objects.equal(av.getValue(), newAv.getValue())) {
          existingAv = av;
        }
      }

      if (existingAv == null) {
        resultSet.add(entity);
      }

    }
    else {
      resultSet.add(entity);
    }
  }

  /**
   * Initializes all necessary fields after object construction
   * 
   * @param local
   *          decides whether the local-application-context or the default-application-context is
   *          used
   */
  private List<EObject> initXmiDeserialization(InputStream xmiInputStream) {
    ResourceSet resourceSet = createResourceSet();
    URI uri = URI.createURI("iteraplanData.xmi");
    Resource resource = resourceSet.createResource(uri);

    List<EObject> loadXmiObjects = null;
    try {
      resource.load(xmiInputStream, null);
      if (resource.getContents().isEmpty()) {
        // there is nothing to process
        return Collections.emptyList();
      }

      loadXmiObjects = loadXmiObjects(resource);
      LOGGER.info(loadXmiObjects.size() + " EObjects found");
    } catch (IOException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ILLEGAL_XMI_FILE_DATA, e, StringUtil.removePathFromFileURIString(e.getMessage()));
    }

    validate(loadXmiObjects);

    return loadXmiObjects;
  }

  private void validate(List<EObject> eObjects) {
    List<String> errors = Lists.newArrayList();
    for (EObject eObject : eObjects) {
      errors.addAll(XmiHelper.validateObject(eObject));
    }

    if (!errors.isEmpty()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ILLEGAL_XMI_FILE_DATA, errors);
    }
  }

  @SuppressWarnings("unchecked")
  private List<EObject> loadXmiObjects(Resource xmiResource) {
    EObject firstResource = xmiResource.getContents().get(0);
    String firstResourceClassName = firstResource.eClass().getName();

    if (xmiResource.getContents().size() == 1 && firstResourceClassName.equals(Iteraplan2EMFHelper.CLASS_NAME)) {
      EStructuralFeature eStructuralFeature = firstResource.eClass().getEStructuralFeature(Iteraplan2EMFHelper.EREFERENCE_NAME);
      Collection<EObject> eObjectsFromResource = (Collection<EObject>) firstResource.eGet(eStructuralFeature);

      return Lists.newArrayList(eObjectsFromResource);
    }
    else {
      return Lists.newArrayList(xmiResource.getContents());
    }
  }

  /**
   * persists all created objects; changing the order can lead to exceptions on commit!!
   */
  private void persistOrderedObjects(List<? extends IdEntity> entities) {
    LOGGER.debug("Persisted objects: {0}", Integer.valueOf(entities.size()));

    for (IdEntity entity : entities) {
      sessionHelper.save(entity);
    }
  }

  /**
   * persists the associations, with the left and right side, without {@link AttributeValueAssignment}.
   * This is separated because of null constraints. (AVA has a null constraint on the BB side, so the
   * associations must exist, but there is also a null constraint for the left and right side of
   * associations, so the must be saved with this two relations.)
   */
  private void persistAssociationsWithoutAVA(List<? extends IdEntity> entitys) {
    LOGGER.debug("Persisted associations: {0}", Integer.valueOf(entitys.size()));

    for (IdEntity entity : entitys) {
      if (entity instanceof AbstractAssociation) {
        AbstractAssociation<?, ?> association = (AbstractAssociation<?, ?>) entity;
        Set<AttributeValueAssignment> avas = Sets.newHashSet(association.getAttributeValueAssignments());
        association.getAttributeValueAssignments().clear();
        sessionHelper.save(association);
        association.getAttributeValueAssignments().addAll(avas);
      }
    }
  }

  /**
   * @return the IdEntity-to-EObject view on the current {@link ImportContext}
   */
  private Map<IdEntity, EObject> getEntityToEObjectMap() {
    return localContext.get().getEntityToEObject();
  }

  /**
   * @return the EObject-toIdEntity view on the current {@link ImportContext}
   */
  private Map<EObject, IdEntity> getEObjectsMap() {
    return localContext.get().getEObjectsMap();
  }

  /**
   * Creates a new {@link ResourceSet} and registers the namespace URIs in its
   * package registry.
   * 
   * @return a newly created {@link ResourceSet}
   */
  private ResourceSet createResourceSet() {
    ResourceSet resourceSet = new ResourceSetImpl();

    for (EPackage ePackage : introspectionBean.getEPackages()) {
      resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
    }

    return resourceSet;
  }

}
