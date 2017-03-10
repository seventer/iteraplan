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
package de.iteratec.iteraplan.businesslogic.service.ecore;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.Iteraplan2EMFHelper;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.MultiassignementType;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


@SuppressWarnings("PMD.TooManyMethods")
public class MappedEPackage implements EPackage {

  private EPackage                                                    wrapped;
  private BiMap<EStructuralFeature, AttributeTypeInBuildingBlockType> eSFtoAT;
  private BiMap<EnumAT, EEnum>                                        atToEEnum;
  private BiMap<BuildingBlockType, EClass>                            bbToEClass;
  private ListMultimap<AttributeTypeInBuildingBlockType, String>      atToAVStrings;
  private static Map<Class<? extends AttributeType>, EDataType>       dataTypeMapping;

  static {
    dataTypeMapping = Maps.newHashMap();
    dataTypeMapping.put(TextAT.class, EcorePackage.eINSTANCE.getEString());
    dataTypeMapping.put(ResponsibilityAT.class, EcorePackage.eINSTANCE.getEString());
    dataTypeMapping.put(NumberAT.class, EcorePackage.eINSTANCE.getEBigDecimal());
    dataTypeMapping.put(DateAT.class, EcorePackage.eINSTANCE.getEDate());
  }

  static final class AttributeTypeInBuildingBlockType {
    private AttributeType     at;
    private BuildingBlockType bt;

    protected AttributeTypeInBuildingBlockType(AttributeType at, BuildingBlockType bt) {
      this.at = at;
      this.bt = bt;
    }

    @Override
    public int hashCode() {
      return at.hashCode() ^ bt.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof AttributeTypeInBuildingBlockType) {
        AttributeTypeInBuildingBlockType casted = (AttributeTypeInBuildingBlockType) obj;
        return casted.at.equals(this.at) && casted.bt.equals(this.bt);
      }
      else {
        return false;
      }
    }

    public AttributeType getAt() {
      return at;
    }

    public BuildingBlockType getBt() {
      return bt;
    }
  }

  public MappedEPackage(EPackage wrapped) {
    this.eSFtoAT = HashBiMap.create();
    this.atToEEnum = HashBiMap.create();
    this.bbToEClass = HashBiMap.create();
    this.atToAVStrings = LinkedListMultimap.create();
    this.wrapped = wrapped;
  }

  public MappedEPackage createCopy(Predicate<ENamedElement> filter) {
    return new MappingEPackageCopier().copy(this, filter);
  }

  public MappedEPackage createCopy() {
    Predicate<ENamedElement> filter = Predicates.alwaysTrue();
    return new MappingEPackageCopier().copy(this, filter);
  }

  public List<String> getAttributeValueStrings(EStructuralFeature eStructuralFeature) {
    List<String> result = Lists.newLinkedList();
    if (this.eSFtoAT.containsKey(eStructuralFeature)) {
      AttributeTypeInBuildingBlockType atInBt = this.eSFtoAT.get(eStructuralFeature);
      if (this.atToAVStrings.containsKey(atInBt)) {
        result.addAll(this.atToAVStrings.get(atInBt));
      }
      if (this.atToEEnum.containsKey(atInBt.getAt())) {
        for (EEnumLiteral eLit : this.atToEEnum.get(atInBt.getAt()).getELiterals()) {
          result.add(eLit.getName());
        }
      }
    }

    return result;
  }

  public boolean isExtended(EStructuralFeature eStructuralFeature) {
    return this.eSFtoAT.containsKey(eStructuralFeature);
  }

  public AttributeType getAttributeType(EStructuralFeature eStructuralFeature) {
    return this.eSFtoAT.get(eStructuralFeature) == null ? null : this.eSFtoAT.get(eStructuralFeature).getAt();
  }

  public EStructuralFeature getEStructuralFeature(AttributeType attributeType, BuildingBlockType buildingBlockType) {
    return this.eSFtoAT.inverse().get(new AttributeTypeInBuildingBlockType(attributeType, buildingBlockType));
  }

  public EnumAT getEnumerationAttributeType(EEnum eEnum) {
    return this.atToEEnum.inverse().get(eEnum);
  }

  public EEnum getEEnum(EnumAT enumAT) {
    return this.atToEEnum.get(enumAT);
  }

  final void createNewEAttribute(EnumAT at, List<EnumAV> avs, BuildingBlockType bt) {
    AttributeTypeInBuildingBlockType atInBt = new AttributeTypeInBuildingBlockType(at, bt);
    createEAttribute(atInBt).setEType(forceGetEEnum(at, avs));
  }

  final void createNewEAttribute(AttributeType at, List<String> avRanges, BuildingBlockType bt) {
    AttributeTypeInBuildingBlockType atInBt = new AttributeTypeInBuildingBlockType(at, bt);
    createEAttribute(atInBt).setEType(dataTypeMapping.get(at.getClass()));
    this.atToAVStrings.putAll(atInBt, avRanges);
  }

  private EAttribute createEAttribute(AttributeTypeInBuildingBlockType atInBt) {
    EAttribute eAtt = EcoreFactory.eINSTANCE.createEAttribute();
    Iteraplan2EMFHelper.setName(eAtt, atInBt.getAt().getName());
    eAtt.setLowerBound(atInBt.getAt().isMandatory() ? 1 : 0);
    eAtt.setUnique(false);

    if (atInBt.getAt() instanceof MultiassignementType) {
      eAtt.setUpperBound(((MultiassignementType) atInBt.getAt()).isMultiassignmenttype() ? EStructuralFeature.UNBOUNDED_MULTIPLICITY : 1);
    }
    else {
      eAtt.setUpperBound(1);
    }
    getEClass(atInBt.getBt()).getEStructuralFeatures().add(eAtt);
    this.eSFtoAT.put(eAtt, atInBt);
    return eAtt;
  }

  private EEnum forceGetEEnum(EnumAT enumAT, List<EnumAV> avs) {
    if (getEEnum(enumAT) == null) {
      EEnum newEEnum = EcoreFactory.eINSTANCE.createEEnum();
      Iteraplan2EMFHelper.setName(newEEnum, enumAT.getName());

      EEnumLiteral undefined = EcoreFactory.eINSTANCE.createEEnumLiteral();
      undefined.setValue(0);
      undefined.setLiteral("-");
      undefined.setName("UNDEFINED");
      newEEnum.getELiterals().add(undefined);
      int count = 1;
      for (EnumAV av : avs) {
        if (av != null) {
          EEnumLiteral eLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
          Iteraplan2EMFHelper.setName(eLiteral, av.getName());
          eLiteral.setValue(count);
          eLiteral.setLiteral(av.getValue());
          newEEnum.getELiterals().add(eLiteral);
          count++;
        }
      }
      this.wrapped.getEClassifiers().add(newEEnum);
      this.atToEEnum.put(enumAT, newEEnum);
    }
    return getEEnum(enumAT);
  }

  public EClass getEClass(BuildingBlockType buildingBlockType) {
    if (!this.bbToEClass.containsKey(buildingBlockType)) {
      EClassifier eClassifier = this.wrapped.getEClassifier(buildingBlockType.getTypeOfBuildingBlock().getAssociatedClass().getSimpleName());
      if (eClassifier instanceof EClass) {
        this.bbToEClass.put(buildingBlockType, (EClass) eClassifier);
      }
    }
    return this.bbToEClass.get(buildingBlockType);
  }

  /*private static final void addSerializationAnnotation(ENamedElement eNamedElement) {
    EcoreUtil.setAnnotation(eNamedElement, ExtendedMetaData.ANNOTATION_URI, "name", eNamedElement.getName().replaceAll("[^\\w]", "_"));
  }*/

  /**
   * @see org.eclipse.emf.common.notify.Notifier#eAdapters()
   */
  public EList<Adapter> eAdapters() {
    return wrapped.eAdapters();
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eClass()
   */
  public EClass eClass() {
    return wrapped.eClass();
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eResource()
   */
  public Resource eResource() {
    return wrapped.eResource();
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eContainer()
   */
  public EObject eContainer() {
    return wrapped.eContainer();
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eContainingFeature()
   */
  public EStructuralFeature eContainingFeature() {
    return wrapped.eContainingFeature();
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eContainmentFeature()
   */
  public EReference eContainmentFeature() {
    return wrapped.eContainmentFeature();
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eContents()
   */
  public EList<EObject> eContents() {
    return wrapped.eContents();
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eAllContents()
   */
  public TreeIterator<EObject> eAllContents() {
    return wrapped.eAllContents();
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eCrossReferences()
   */
  public EList<EObject> eCrossReferences() {
    return wrapped.eCrossReferences();
  }

  /**
   * @see org.eclipse.emf.common.notify.Notifier#eDeliver()
   */
  public boolean eDeliver() {
    return wrapped.eDeliver();
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eIsProxy()
   */
  public boolean eIsProxy() {
    return wrapped.eIsProxy();
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eGet(org.eclipse.emf.ecore.EStructuralFeature)
   */
  public Object eGet(EStructuralFeature feature) {
    return wrapped.eGet(feature);
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eGet(org.eclipse.emf.ecore.EStructuralFeature, boolean)
   */
  public Object eGet(EStructuralFeature feature, boolean resolve) {
    return wrapped.eGet(feature, resolve);
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eSet(org.eclipse.emf.ecore.EStructuralFeature, java.lang.Object)
   */
  public void eSet(EStructuralFeature feature, Object newValue) {
    wrapped.eSet(feature, newValue);
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)
   */
  public boolean eIsSet(EStructuralFeature feature) {
    return wrapped.eIsSet(feature);
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eInvoke(org.eclipse.emf.ecore.EOperation, org.eclipse.emf.common.util.EList)
   */
  public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException {
    return wrapped.eInvoke(operation, arguments);
  }

  /**
   * @see org.eclipse.emf.common.notify.Notifier#eNotify(org.eclipse.emf.common.notify.Notification)
   */
  public void eNotify(Notification arg0) {
    wrapped.eNotify(arg0);
  }

  /**
   * @see org.eclipse.emf.common.notify.Notifier#eSetDeliver(boolean)
   */
  public void eSetDeliver(boolean arg0) {
    wrapped.eSetDeliver(arg0);
  }

  /**
   * @see org.eclipse.emf.ecore.EModelElement#getEAnnotations()
   */
  public EList<EAnnotation> getEAnnotations() {
    return wrapped.getEAnnotations();
  }

  /**
   * @see org.eclipse.emf.ecore.ENamedElement#getName()
   */
  public String getName() {
    return wrapped.getName();
  }

  /**
   * @see org.eclipse.emf.ecore.ENamedElement#setName(java.lang.String)
   */
  public void setName(String value) {
    wrapped.setName(value);
  }

  /**
   * @see org.eclipse.emf.ecore.EModelElement#getEAnnotation(java.lang.String)
   */
  public EAnnotation getEAnnotation(String source) {
    return wrapped.getEAnnotation(source);
  }

  /**
   * @see org.eclipse.emf.ecore.EPackage#getNsURI()
   */
  public String getNsURI() {
    return wrapped.getNsURI();
  }

  /**
   * @see org.eclipse.emf.ecore.EPackage#setNsURI(java.lang.String)
   */
  public void setNsURI(String value) {
    wrapped.setNsURI(value);
  }

  /**
   * @see org.eclipse.emf.ecore.EPackage#getNsPrefix()
   */
  public String getNsPrefix() {
    return wrapped.getNsPrefix();
  }

  /**
   * @see org.eclipse.emf.ecore.EPackage#setNsPrefix(java.lang.String)
   */
  public void setNsPrefix(String value) {
    wrapped.setNsPrefix(value);
  }

  /**
   * @see org.eclipse.emf.ecore.EPackage#getEFactoryInstance()
   */
  public EFactory getEFactoryInstance() {
    return wrapped.getEFactoryInstance();
  }

  /**
   * @see org.eclipse.emf.ecore.EPackage#setEFactoryInstance(org.eclipse.emf.ecore.EFactory)
   */
  public void setEFactoryInstance(EFactory value) {
    wrapped.setEFactoryInstance(value);
  }

  /**
   * @see org.eclipse.emf.ecore.EPackage#getEClassifiers()
   */
  public EList<EClassifier> getEClassifiers() {
    return wrapped.getEClassifiers();
  }

  /**
   * @see org.eclipse.emf.ecore.EPackage#getESubpackages()
   */
  public EList<EPackage> getESubpackages() {
    return wrapped.getESubpackages();
  }

  /**
   * @see org.eclipse.emf.ecore.EPackage#getESuperPackage()
   */
  public EPackage getESuperPackage() {
    return wrapped.getESuperPackage();
  }

  /**
   * @see org.eclipse.emf.ecore.EPackage#getEClassifier(java.lang.String)
   */
  public EClassifier getEClassifier(String name) {
    return wrapped.getEClassifier(name);
  }

  /**
   * @see org.eclipse.emf.ecore.EObject#eUnset(org.eclipse.emf.ecore.EStructuralFeature)
   */
  public void eUnset(EStructuralFeature feature) {
    wrapped.eUnset(feature);
  }

  private static class MappingEPackageCopier {
    private BiMap<ENamedElement, ENamedElement> orig2copy;

    MappingEPackageCopier() {
      this.orig2copy = HashBiMap.create();
    }

    MappedEPackage copy(MappedEPackage oldEPackage, Predicate<ENamedElement> filter) {
      EPackage newEPackage = EcoreFactory.eINSTANCE.createEPackage();
      MappedEPackage result = new MappedEPackage(newEPackage);
      newEPackage.setName(oldEPackage.getName());
      newEPackage.setNsPrefix(oldEPackage.getNsPrefix());
      newEPackage.setNsURI(oldEPackage.getNsURI());

      copyAllEDataTypes(oldEPackage, result, filter);

      for (EClassifier oldEClassifier : Collections2.filter(oldEPackage.getEClassifiers(), eClassAnd(filter))) {
        EClass oldEClass = (EClass) oldEClassifier;
        EClass newEClass = copyEClassifier(oldEClass);
        newEPackage.getEClassifiers().add(newEClass);
        result.bbToEClass.put(oldEPackage.bbToEClass.inverse().get(oldEClass), newEClass);
        newEClass.getEStructuralFeatures().clear();
        for (EAttribute oldEAttribute : Collections2.filter(oldEClass.getEAttributes(), filter)) {
          EAttribute newEAttribute = copyEStructuralFeature(oldEAttribute);
          if (newEAttribute != null) {
            if (oldEPackage.eSFtoAT.containsKey(oldEAttribute)) {
              result.eSFtoAT.put(newEAttribute, oldEPackage.eSFtoAT.get(oldEAttribute));
            }
            newEClass.getEStructuralFeatures().add(newEAttribute);
          }
        }
      }
      for (EClassifier oldEClassifier : Collections2.filter(oldEPackage.getEClassifiers(), eClassAnd(filter))) {
        EClass oldEClass = (EClass) oldEClassifier;
        EClass newEClass = (EClass) this.orig2copy.get(oldEClass);
        for (EClass oldSuperEClass : Collections2.filter(oldEClass.getESuperTypes(), filter)) {
          newEClass.getESuperTypes().add((EClass) this.orig2copy.get(oldSuperEClass));
        }
        for (EReference oldEReference : Collections2.filter(oldEClass.getEReferences(), filter)) {
          EReference newEReference = copyEStructuralFeature(oldEReference);
          if (newEReference != null) {
            if (oldEPackage.eSFtoAT.containsKey(oldEReference)) {
              result.eSFtoAT.put(newEReference, oldEPackage.eSFtoAT.get(oldEReference));
            }
            newEClass.getEStructuralFeatures().add(newEReference);
          }
        }
      }
      result.atToAVStrings.putAll(oldEPackage.atToAVStrings);

      return result;
    }

    private void copyAllEDataTypes(MappedEPackage oldEPackage, MappedEPackage result, Predicate<ENamedElement> filter) {
      for (EClassifier oldEClassifier : Collections2.filter(oldEPackage.getEClassifiers(), eDataTypeAnd(filter))) {
        EClassifier newEClassifier = copyEClassifier(oldEClassifier);
        result.getEFactoryInstance().getEPackage().getEClassifiers().add(newEClassifier);
        if (oldEClassifier instanceof EEnum) {
          EEnum oldEEnum = (EEnum) oldEClassifier;
          if (oldEPackage.atToEEnum.containsValue(oldEClassifier)) {
            result.atToEEnum.put(oldEPackage.atToEEnum.inverse().get(oldEEnum), (EEnum) newEClassifier);
          }
        }
      }
    }

    private <T extends EStructuralFeature> T copyEStructuralFeature(T oldEStructuralFeature) {
      T newEStructuralFeature = EcoreUtil.copy(oldEStructuralFeature);
      if (this.orig2copy.containsKey(oldEStructuralFeature.getEType())) {
        newEStructuralFeature.setEType((EClassifier) this.orig2copy.get(oldEStructuralFeature.getEType()));
      }
      else if (oldEStructuralFeature.getEType() instanceof EDataType) {
        newEStructuralFeature.setEType(oldEStructuralFeature.getEType());
      }
      else {
        return null;
      }
      if (oldEStructuralFeature instanceof EReference) {
        EReference oldEReference = (EReference) oldEStructuralFeature;
        if (this.orig2copy.containsKey(oldEReference.getEOpposite())) {
          ((EReference) newEStructuralFeature).setEOpposite((EReference) this.orig2copy.get(oldEReference.getEOpposite()));
        }
      }

      this.orig2copy.put(oldEStructuralFeature, newEStructuralFeature);
      return newEStructuralFeature;
    }

    private <T extends EClassifier> T copyEClassifier(T oldEClassifier) {
      T newEClassifier = EcoreUtil.copy(oldEClassifier);
      if (oldEClassifier instanceof EEnum) {
        ((EEnum) newEClassifier).getELiterals().clear();
        for (EEnumLiteral oldELiteral : ((EEnum) oldEClassifier).getELiterals()) {
          EEnumLiteral newELiteral = EcoreUtil.copy(oldELiteral);
          this.orig2copy.put(oldELiteral, newELiteral);
        }
      }
      this.orig2copy.put(oldEClassifier, newEClassifier);
      return newEClassifier;
    }

    private static Predicate<ENamedElement> eDataTypeAnd(Predicate<ENamedElement> filter) {
      return Predicates.and(new Predicate<ENamedElement>() {
        public boolean apply(ENamedElement arg0) {
          return arg0 instanceof EDataType;
        }
      }, filter);
    }

    private static Predicate<ENamedElement> eClassAnd(Predicate<ENamedElement> filter) {
      return Predicates.and(new Predicate<ENamedElement>() {
        public boolean apply(ENamedElement arg0) {
          return arg0 instanceof EClass;
        }
      }, filter);
    }
  }
}
