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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import de.iteratec.iteraplan.model.AbstractAssociation;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessMappingEntity;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.Seal;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.RangeValue;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.queries.CustomDashboardInstance;
import de.iteratec.iteraplan.model.queries.CustomDashboardTemplate;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.queries.SavedQueryEntity;
import de.iteratec.iteraplan.model.user.DataSource;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;


/**
 * Class which contains all entity classes for the iteraplan ecore model
 * 
 * @author mba
 */
public final class EntityClassStore {

  private static final Collection<Class<? extends IdEntity>>  ENTITY_CLASSES      = initializeClasses();
  private static final Map<String, Class<? extends IdEntity>> NAME_CLASS_MAPPINGS = initNameToClassMappings(ENTITY_CLASSES);

  private EntityClassStore() {
    // prevents the instances of this class
  }

  /**
   * Returns the collection of all classes, which can be imported. The returned list is
   * immutable.
   * 
   * @return the collection of all classes, which can be imported
   */
  public static Collection<Class<? extends IdEntity>> getEntityClasses() {
    return ENTITY_CLASSES;
  }

  /**
   * Returns the map containing the class names associated with the actual classes,
   * taken from the {@link #getEntityClasses()} method. The returned map is immutable.
   * 
   * @return the map containing the class names associated with the actual classes
   */
  public static Map<String, Class<? extends IdEntity>> getNameToClassMappings() {
    return NAME_CLASS_MAPPINGS;
  }

  /**
   * Returns the collection of all classes, which can be imported. The returned list is
   * immutable.
   * 
   * @return the collection of all classes, which can be imported
   */
  private static Collection<Class<? extends IdEntity>> initializeClasses() {
    Builder<Class<? extends IdEntity>> builder = ImmutableList.builder();

    builder.add(BuildingBlockType.class);

    builder.add(DataSource.class);
    builder.add(PermissionAttrTypeGroup.class);
    builder.add(PermissionFunctional.class);
    builder.add(Role.class);
    builder.add(Role2BbtPermission.class);
    builder.add(User.class);
    builder.add(UserEntity.class);
    builder.add(UserGroup.class);

    builder.add(AttributeType.class);
    builder.add(AttributeTypeGroup.class);
    builder.add(AttributeValue.class);
    builder.add(AttributeValueAssignment.class);
    builder.add(DateAT.class);
    builder.add(DateAV.class);
    builder.add(EnumAT.class);
    builder.add(EnumAV.class);
    builder.add(NumberAT.class);
    builder.add(NumberAV.class);
    builder.add(RangeValue.class);
    builder.add(ResponsibilityAT.class);
    builder.add(ResponsibilityAV.class);
    builder.add(TextAT.class);
    builder.add(TextAV.class);

    builder.add(DateInterval.class);

    builder.add(BuildingBlock.class);
    builder.add(AbstractAssociation.class);
    builder.add(BusinessMappingEntity.class);
    builder.add(AbstractHierarchicalEntity.class);
    builder.add(ArchitecturalDomain.class);
    builder.add(BusinessDomain.class);
    builder.add(BusinessFunction.class);
    builder.add(BusinessMapping.class);
    builder.add(BusinessObject.class);
    builder.add(BusinessProcess.class);
    builder.add(BusinessUnit.class);
    builder.add(InformationSystem.class);
    builder.add(InformationSystemDomain.class);
    builder.add(InformationSystemInterface.class);
    builder.add(InformationSystemRelease.class);
    builder.add(InfrastructureElement.class);
    builder.add(Isr2BoAssociation.class);
    builder.add(Product.class);
    builder.add(Project.class);
    builder.add(Tcr2IeAssociation.class);
    builder.add(TechnicalComponent.class);
    builder.add(TechnicalComponentRelease.class);
    builder.add(Transport.class);
    builder.add(Seal.class);

    builder.add(SavedQueryEntity.class);
    builder.add(SavedQuery.class);

    builder.add(HierarchicalEntity.class);

    //    builder.add(HistoryRevisionEntity.class);
    builder.add(CustomDashboardInstance.class);
    builder.add(CustomDashboardTemplate.class);

    return builder.build();
  }

  private static Map<String, Class<? extends IdEntity>> initNameToClassMappings(Collection<Class<? extends IdEntity>> classes) {
    com.google.common.collect.ImmutableMap.Builder<String, Class<? extends IdEntity>> builder = ImmutableMap.builder();

    for (Class<? extends IdEntity> clazz : classes) {
      builder.put(clazz.getSimpleName(), clazz);
    }

    return builder.build();
  }

  public static List<EObject> getPersistentObjects(List<EObject> eObjects) {
    Multimap<Class<? extends IdEntity>, EObject> orderedObjects = groupEObjects(eObjects);
    LinkedList<EObject> objects = Lists.newLinkedList();

    objects.addAll(orderedObjects.get(AttributeTypeGroup.class));

    objects.addAll(orderedObjects.get(NumberAT.class));
    objects.addAll(orderedObjects.get(DateAT.class));
    objects.addAll(orderedObjects.get(TextAT.class));
    objects.addAll(orderedObjects.get(ResponsibilityAT.class));
    objects.addAll(orderedObjects.get(EnumAT.class));

    objects.addAll(orderedObjects.get(NumberAV.class));
    objects.addAll(orderedObjects.get(DateAV.class));
    objects.addAll(orderedObjects.get(TextAV.class));
    objects.addAll(orderedObjects.get(EnumAV.class));
    objects.addAll(orderedObjects.get(ResponsibilityAV.class));
    objects.addAll(orderedObjects.get(RangeValue.class));

    objects.addAll(orderedObjects.get(DateInterval.class));

    objects.addAll(orderedObjects.get(BusinessObject.class));
    objects.addAll(orderedObjects.get(BusinessProcess.class));
    objects.addAll(orderedObjects.get(Product.class));
    objects.addAll(orderedObjects.get(BusinessDomain.class));
    objects.addAll(orderedObjects.get(BusinessFunction.class));
    objects.addAll(orderedObjects.get(BusinessUnit.class));
    objects.addAll(orderedObjects.get(InformationSystemDomain.class));
    objects.addAll(orderedObjects.get(Project.class));
    objects.addAll(orderedObjects.get(ArchitecturalDomain.class));
    objects.addAll(orderedObjects.get(TechnicalComponent.class));
    objects.addAll(orderedObjects.get(InformationSystem.class));
    objects.addAll(orderedObjects.get(InformationSystemRelease.class));
    objects.addAll(orderedObjects.get(InformationSystemInterface.class));
    objects.addAll(orderedObjects.get(TechnicalComponentRelease.class));
    objects.addAll(orderedObjects.get(InfrastructureElement.class));
    objects.addAll(orderedObjects.get(Transport.class));
    objects.addAll(orderedObjects.get(BusinessMapping.class));
    objects.addAll(orderedObjects.get(Tcr2IeAssociation.class));
    objects.addAll(orderedObjects.get(Isr2BoAssociation.class));

    objects.addAll(orderedObjects.get(CustomDashboardInstance.class));
    objects.addAll(orderedObjects.get(CustomDashboardTemplate.class));

    return objects;
  }

  @SuppressWarnings("unchecked")
  public static List<Class<? extends IdEntity>> getCoreClasses() {
    List<Class<? extends IdEntity>> coreClasses = Lists.newArrayList(BuildingBlockType.class, DataSource.class, PermissionFunctional.class,
        Role.class, PermissionAttrTypeGroup.class, UserGroup.class, User.class, SavedQuery.class);

    return coreClasses;
  }

  public static List<EObject> getCoreEntities(Collection<EObject> eObjects) {
    Multimap<Class<? extends IdEntity>, EObject> orderedObjects = groupEObjects(eObjects);

    LinkedList<EObject> objects = Lists.newLinkedList();
    for (Class<? extends IdEntity> class1 : getCoreClasses()) {
      objects.addAll(orderedObjects.get(class1));
    }

    return objects;
  }

  /**
   * Returns all {@link BuildingBlock} classes ordered by their persistent order.
   * 
   * @return all {@link BuildingBlock} classes
   */
  public static List<Class<? extends BuildingBlock>> buildingBlockClasses() {
    List<Class<? extends BuildingBlock>> objects = Lists.newArrayList();

    objects.add(BusinessObject.class);
    objects.add(BusinessProcess.class);
    objects.add(Product.class);
    objects.add(BusinessDomain.class);
    objects.add(BusinessFunction.class);
    objects.add(BusinessUnit.class);
    objects.add(InformationSystemDomain.class);
    objects.add(Project.class);
    objects.add(ArchitecturalDomain.class);
    objects.add(TechnicalComponent.class);
    objects.add(InformationSystem.class);
    objects.add(InformationSystemRelease.class);
    objects.add(InformationSystemInterface.class);
    objects.add(TechnicalComponentRelease.class);
    objects.add(InfrastructureElement.class);
    objects.add(Transport.class);
    objects.add(BusinessMapping.class);
    objects.add(Tcr2IeAssociation.class);
    objects.add(Isr2BoAssociation.class);

    return objects;
  }

  public static Multimap<Class<? extends IdEntity>, EObject> groupEObjects(Collection<EObject> eObjects) {
    Map<String, Class<? extends IdEntity>> nameToClassMappings = EntityClassStore.getNameToClassMappings();
    Multimap<Class<? extends IdEntity>, EObject> orderedObjects = LinkedHashMultimap.create();

    for (EObject eObject : eObjects) {
      Class<? extends IdEntity> clazz = nameToClassMappings.get(eObject.eClass().getName());
      orderedObjects.put(clazz, eObject);
    }

    return orderedObjects;
  }

  public static List<IdEntity> convert(Collection<EObject> eObjects, Map<EObject, IdEntity> eObjectsMap) {
    List<IdEntity> result = Lists.newArrayList();

    for (EObject eObject : eObjects) {
      IdEntity entity = eObjectsMap.get(eObject);
      if (entity != null) {
        result.add(entity);
      }
    }

    return result;
  }
}
