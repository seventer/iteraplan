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
package de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.EcoreServiceForTabReporting;
import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.Iteraplan2EMFHelper;
import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.TabReportingEcoreData;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;


public class XmiServiceForTabReporting {
  private static final Logger                                    LOGGER               = Logger.getIteraplanLogger(XmiServiceForTabReporting.class);
  private static final Map<Class<?>, TypeOfFunctionalPermission> CLASS_TO_PERMISSIONS = getClassToPermsMap();
  private static final String                                    RUNTIME_PERIOD       = "RuntimePeriod";

  private EcoreServiceForTabReporting                            ecoreService;
  private SessionFactory                                         sessionFactory;

  private static Map<Object, Object> getSaveOptions() {
    Map<Object, Object> result = Maps.newHashMap();
    result.put(XMIResource.OPTION_EXTENDED_META_DATA, ExtendedMetaData.INSTANCE);
    result.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
    return result;
  }

  public XmiServiceForTabReporting(EcoreServiceForTabReporting ecoreService, SessionFactory sessionFactory) {
    this.ecoreService = Preconditions.checkNotNull(ecoreService);
    this.sessionFactory = Preconditions.checkNotNull(sessionFactory);
  }

  public void generateXmlExportFor(Collection<? extends BuildingBlock> buildingBlocks, OutputStream stream) {
    if (buildingBlocks.isEmpty()) {
      return;
    }

    String uri = buildingBlocks.iterator().next().getClass().getSimpleName() + "_ExportForTabReporting.xml";
    TabReportingEcoreData tabReportingEcoreData = ecoreService.getExtendedEPackge();
    Resource xmiResource = getXmiResource(uri, tabReportingEcoreData);
    xmiResource.setURI(URI.createURI(""));
    EObject exportFor = export(buildingBlocks, tabReportingEcoreData);
    xmiResource.getContents().add(exportFor);
    try {
      xmiResource.save(stream, getSaveOptions());
    } catch (IOException e) {
      throw new IteraplanTechnicalException(e);
    }
  }

  public void generateTotalXmlExport(OutputStream stream) {
    String uri = "iteraplanXMI.xml";
    TabReportingEcoreData tabReportingEcoreData = ecoreService.getExtendedEPackge();
    Resource xmiResource = getXmiResource(uri, tabReportingEcoreData);
    xmiResource.setURI(URI.createURI(""));
    EObject eContainer = exportAll(tabReportingEcoreData);
    xmiResource.getContents().add(eContainer);
    try {
      xmiResource.save(stream, getSaveOptions());
    } catch (IOException e) {
      throw new IteraplanTechnicalException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private EObject exportAll(TabReportingEcoreData tabEcoreData) {
    List<BuildingBlock> buildingBlocks = Lists.newArrayList();
    EPackage model = tabEcoreData.getModelPackage();
    Session session = sessionFactory.getCurrentSession();
    for (EClassifier ec : model.getEClassifiers()) {
      if (ec instanceof EClass) {
        EClass eClass = (EClass) ec;
        String name = eClass.getName();
        if (!eClass.isAbstract() && !RUNTIME_PERIOD.equals(name) && !Iteraplan2EMFHelper.CLASS_NAME_TABULARREPORTING.equals(name)) {
          Query query = session.createQuery("select xx from " + name + " xx");
          Iterator<Object> iterator = query.iterate();
          while (iterator.hasNext()) {
            Object entity = iterator.next();
            if (entity instanceof BuildingBlock) {
              buildingBlocks.add((BuildingBlock) entity);
            }
            else {
              LOGGER.error(entity.getClass().getName());
            }
          }

        }
        else {
          LOGGER.debug("Ignored " + name);
        }
      }
    }

    return export(buildingBlocks, tabEcoreData);
  }

  private Resource getXmiResource(String uri, TabReportingEcoreData tabReportingEcoreData) {
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xml", new EcoreResourceFactoryImpl());
    ResourceSet rs = new ResourceSetImpl();

    EPackage extendedModel = tabReportingEcoreData.getModelPackage();
    rs.getPackageRegistry().put(extendedModel.getNsURI(), extendedModel);

    return rs.createResource(URI.createURI(uri));
  }

  private EObject export(Collection<? extends BuildingBlock> buildingBlocks, TabReportingEcoreData tabReportingEcoreData) {
    Map<Object, EObject> objectToEObject = Maps.newLinkedHashMap();
    for (BuildingBlock bb : buildingBlocks) {
      generateEObjectFor(bb, tabReportingEcoreData, objectToEObject);
    }

    for (BuildingBlock bb : buildingBlocks) {
      setEReferencesFor(bb, tabReportingEcoreData, objectToEObject);
    }

    return getEContainerObject(objectToEObject, tabReportingEcoreData.getModelPackage());
  }

  private EObject getEContainerObject(Map<Object, EObject> objects, EPackage modelPackage) {
    Preconditions.checkNotNull(objects);
    Preconditions.checkNotNull(modelPackage);
    Collection<EObject> eObjects = Lists.newArrayList(objects.values());

    EObject eContainer = modelPackage.getEFactoryInstance().create(
        (EClass) modelPackage.getEClassifier(Iteraplan2EMFHelper.CLASS_NAME_TABULARREPORTING));
    eContainer.eSet(eContainer.eClass().getEStructuralFeature(Iteraplan2EMFHelper.EREFERENCE_NAME), eObjects);

    return eContainer;
  }

  private void setEReferencesFor(BuildingBlock bb, TabReportingEcoreData tabReportingEcoreData, Map<Object, EObject> objectToEObject) {
    EObject eBB = objectToEObject.get(bb);
    for (EReference eReference : eBB.eClass().getEAllReferences()) {
      Object value = getReferenceValue(eBB, eReference, bb, tabReportingEcoreData, objectToEObject);
      if (value != null) {
        eBB.eSet(eReference, value);
      }
    }
  }

  private EObject generateEObjectFor(Object bb, TabReportingEcoreData tabReportingEcoreData, Map<Object, EObject> objectToEObject) {
    if (!userHasRights(bb.getClass())) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }

    EPackage extendedModel = tabReportingEcoreData.getModelPackage();
    EClass eClassifier = (EClass) extendedModel.getEClassifier(bb.getClass().getSimpleName());
    EObject eBB = extendedModel.getEFactoryInstance().create(eClassifier);
    for (EAttribute eAtt : eBB.eClass().getEAllAttributes()) {
      Object attributeValue = getAttributeValue(eBB, eAtt, bb, tabReportingEcoreData);
      if (attributeValue != null) {
        eBB.eSet(eAtt, attributeValue);
      }
    }
    objectToEObject.put(bb, eBB);

    return eBB;
  }

  private Object getAttributeValue(EObject eBB, EAttribute eAttribute, Object bb, TabReportingEcoreData tabReportingEcoreData) {
    boolean bbExtended = isExtended(eAttribute, tabReportingEcoreData);

    if (bbExtended) {
      return getValueForExtendedAttribute(eBB, eAttribute, bb, tabReportingEcoreData);
    }
    else {
      return getStandardAttributeValue(eAttribute, bb);
    }
  }

  private Object getReferenceValue(EObject eBB, EReference esf, Object bb, TabReportingEcoreData tabReportingEcoreData,
                                   Map<Object, EObject> objectToEObject) {
    boolean bbExtended = isExtended(esf, tabReportingEcoreData);

    if (bbExtended) {
      return getValueForExtendedReference(eBB, esf, (BuildingBlock) bb);
    }
    else {
      return getStandardReferenceValue(eBB, esf, bb, tabReportingEcoreData, objectToEObject);
    }
  }

  private Object getValueForExtendedReference(EObject eBB, EReference eRef, BuildingBlock bb) {
    if (eBB == null || eRef == null || bb == null || eRef.getEContainingClass() != eBB.eClass()) {
      return null;
    }

    if (eRef.getName().equals("owningUserEntities") && userHasRights(User.class)) {
      for (UserEntity ue : bb.getOwningUserEntities()) {
        if (ue instanceof User) {
          addEValueToEList(eBB, eRef, ((User) ue).getLoginName());
        }
        else {
          addEValueToEList(eBB, eRef, ((UserGroup) ue).getName());
        }
      }
    }

    return null;
  }

  @SuppressWarnings({ "rawtypes" })
  private Object getStandardReferenceValue(EObject eBB, EReference eRef, Object bb, TabReportingEcoreData tabReportingEcoreData,
                                           Map<Object, EObject> objectToEObject) {
    Object referencedValue = getReferencedValue(eRef, bb);
    if (referencedValue == null) {
      return null;
    }

    EReference eOpposite = eRef.getEOpposite();
    if (eRef.getUpperBound() == 1) {
      if (!objectToEObject.containsKey(referencedValue)) {
        if (userHasRights(referencedValue.getClass())) {
          generateEObjectFor(referencedValue, tabReportingEcoreData, objectToEObject);
        }
        else {
          return null;
        }
      }

      EObject referencedEObject = objectToEObject.get(referencedValue);
      if (eOpposite != null && eOpposite.getUpperBound() == 1) {
        referencedEObject.eSet(eOpposite, eBB);
      }
      else if (eOpposite != null) {
        addEValueToEList(referencedEObject, eOpposite, eBB);
      }

      return referencedEObject;
    }
    else {
      //TODO remove this if block
      if (!(referencedValue instanceof Iterable)) {
        LOGGER.error("ESF: " + eRef.getEContainingClass().getName() + "." + eRef.getName() + " behaves strange");
        referencedValue = Lists.newArrayList(referencedValue);
      }
      for (Object val : (Iterable) referencedValue) {
        if (val instanceof BusinessMapping) {
          manageBusinessMapping((BusinessMapping) val, tabReportingEcoreData, objectToEObject);
        }
        else if (val instanceof Transport) {
          manageTransport((Transport) val, tabReportingEcoreData, objectToEObject);
        }
        else {
          if (!objectToEObject.containsKey(val)) {
            if (userHasRights(val.getClass())) {
              generateEObjectFor(val, tabReportingEcoreData, objectToEObject);
            }
            else {
              return null;
            }
          }

          addEValueToEList(eBB, eRef, objectToEObject.get(val));
          if (eOpposite != null && eOpposite.getUpperBound() == 1) {
            objectToEObject.get(val).eSet(eOpposite, eBB);
          }
          else if (eOpposite != null) {
            addEValueToEList(objectToEObject.get(val), eOpposite, eBB);
          }
        }
      }
    }

    return null;
  }

  private Object getReferencedValue(EReference eRef, Object bb) {
    try {
      Method method = findMethodForStandardAttribute(eRef.getName(), bb);
      if (method == null) {
        LOGGER.warn("The method for the reference {0} not found", eRef.getName());
        return null;
      }

      return method.invoke(bb);
    } catch (IllegalArgumentException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (IllegalAccessException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (InvocationTargetException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  private void manageTransport(Transport tp, TabReportingEcoreData tabReportingEcoreData, Map<Object, EObject> objectToEObject) {
    if (!userHasRights(BusinessObject.class) || !userHasRights(InformationSystemInterface.class)) {
      return;
    }

    if (!objectToEObject.containsKey(tp)) {
      generateEObjectFor(tp, tabReportingEcoreData, objectToEObject);
    }

    BusinessObject businessObject = tp.getBusinessObject();
    if (!objectToEObject.containsKey(businessObject)) {
      generateEObjectFor(businessObject, tabReportingEcoreData, objectToEObject);
    }

    EObject tpEObject = objectToEObject.get(tp);
    EObject boEObject = objectToEObject.get(businessObject);
    tpEObject.eSet(tpEObject.eClass().getEStructuralFeature("businessObject"), boEObject);
    addEValueToEList(boEObject, boEObject.eClass().getEStructuralFeature("transports"), tpEObject);

    InformationSystemInterface isi = tp.getInformationSystemInterface();
    if (!objectToEObject.containsKey(isi)) {
      generateEObjectFor(isi, tabReportingEcoreData, objectToEObject);
    }

    EObject isiEObject = objectToEObject.get(isi);
    tpEObject.eSet(tpEObject.eClass().getEStructuralFeature("informationSystemInterface"), isiEObject);
    addEValueToEList(isiEObject, isiEObject.eClass().getEStructuralFeature("transports"), tpEObject);
  }

  private void manageBusinessMapping(BusinessMapping bm, TabReportingEcoreData tabReportingEcoreData, Map<Object, EObject> objectToEObject) {
    InformationSystemRelease isr = bm.getInformationSystemRelease();

    if (!objectToEObject.containsKey(bm)) {
      generateEObjectFor(bm, tabReportingEcoreData, objectToEObject);
      EObject eBm = objectToEObject.get(bm);

      BusinessProcess businessProcess = bm.getBusinessProcess();
      if (userHasRights(BusinessProcess.class) && businessProcess != null) {
        if (!objectToEObject.containsKey(businessProcess)) {
          generateEObjectFor(businessProcess, tabReportingEcoreData, objectToEObject);
        }
        eBm.eSet(eBm.eClass().getEStructuralFeature("businessProcess"), objectToEObject.get(businessProcess));
      }

      if (userHasRights(InformationSystemRelease.class) && isr != null) {
        if (!objectToEObject.containsKey(isr)) {
          generateEObjectFor(isr, tabReportingEcoreData, objectToEObject);
        }
        eBm.eSet(eBm.eClass().getEStructuralFeature("informationSystemRelease"), objectToEObject.get(isr));
      }

      BusinessUnit businessUnit = bm.getBusinessUnit();
      if (userHasRights(BusinessUnit.class) && businessUnit != null) {
        if (!objectToEObject.containsKey(businessUnit)) {
          generateEObjectFor(businessUnit, tabReportingEcoreData, objectToEObject);
        }
        eBm.eSet(eBm.eClass().getEStructuralFeature("businessUnit"), objectToEObject.get(businessUnit));
      }

      Product product = bm.getProduct();
      if (userHasRights(Product.class) && product != null) {
        if (!objectToEObject.containsKey(product)) {
          generateEObjectFor(product, tabReportingEcoreData, objectToEObject);
        }
        eBm.eSet(eBm.eClass().getEStructuralFeature("product"), objectToEObject.get(product));
      }
    }

  }

  private Object getStandardAttributeValue(EAttribute eAtt, Object bb) {
    Method method = findMethodForStandardAttribute(eAtt.getName(), bb);
    if (method == null) {
      LOGGER.warn("The method for the attribute {0} for class {1} not found", eAtt.getName(), bb.getClass());
      return null;
    }

    Object result = invokeMethod(bb, method);

    if (!(eAtt.getEType() instanceof EEnum)) {
      if (eAtt.isID()) {
        return eAtt.getEContainingClass().getName() + "_" + ((Integer) result).intValue();
      }
      return result;
    }

    if (result == null) {
      return null;
    }

    EEnum eEnum = (EEnum) eAtt.getEType();
    for (EEnumLiteral eLiteral : eEnum.getELiterals()) {
      if (result.toString().equals(eLiteral.getLiteral())) {
        return eLiteral;
      }
    }

    return null;
  }

  private Object invokeMethod(Object bb, Method method) {
    try {
      return method.invoke(bb);
    } catch (IllegalArgumentException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (IllegalAccessException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (InvocationTargetException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  private Method findMethodForStandardAttribute(String name, Object bb) {
    String keyGet = "get" + capitalize(name);
    String keyIs = "is" + capitalize(name);

    Method[] methodsOfBBClass = bb.getClass().getMethods();
    for (Method m : methodsOfBBClass) {
      if (m.getName().equals(keyGet) || m.getName().equals(keyIs)) {
        return m;
      }
    }

    return null;
  }

  private Object getValueForExtendedAttribute(EObject eBB, EAttribute eAtt, Object ob, TabReportingEcoreData tabReportingEcoreData) {
    BuildingBlock bb = (BuildingBlock) ob;

    AttributeType attributeType = tabReportingEcoreData.getAttributeTypeForFeature(eAtt);
    if (attributeType != null) {
      for (AttributeValueAssignment ava : bb.getAttributeValueAssignments()) {
        AttributeValue attributeValue = ava.getAttributeValue();

        if (attributeValue.getAbstractAttributeType().equals(attributeType)) {
          if (attributeValue instanceof NumberAV) {
            NumberAV nAV = (NumberAV) attributeValue;
            return nAV.getValue();
          }
          if (attributeValue instanceof DateAV) {
            DateAV dAV = (DateAV) attributeValue;
            return dAV.getValue();
          }
          if (attributeValue instanceof TextAV) {
            TextAV tAV = (TextAV) attributeValue;
            return tAV.getValue();
          }
          if (attributeValue instanceof ResponsibilityAV) {
            ResponsibilityAV rAV = (ResponsibilityAV) attributeValue;
            if (eAtt.getUpperBound() == 1) {
              return rAV.getUserEntity().getIdentityString();
            }
            else {
              addEValueToEList(eBB, eAtt, rAV.getUserEntity().getIdentityString());
            }
          }

          if (attributeValue instanceof EnumAV) {
            EnumAV eAV = (EnumAV) attributeValue;
            EnumAT attributeType2 = eAV.getAttributeType();
            for (EEnumLiteral eLiteral : tabReportingEcoreData.getEEnumForAttributeType(attributeType2).getELiterals()) {
              if (eLiteral.getLiteral().equals(eAV.getValue())) {
                if (eAtt.getUpperBound() == 1) {
                  return eLiteral;
                }
                else {
                  addEValueToEList(eBB, eAtt, eLiteral);
                }
              }
            }
          }
        }
      }
    }
    else if (eAtt.getName().equals("owningUserEntities") && bb.getOwningUserEntities() != null) {
      EList<Object> eUsers = new UniqueEList<Object>();
      for (UserEntity ue : bb.getOwningUserEntities()) {
        eUsers.add(ue.getIdentityString());
      }
      eBB.eSet(eAtt, eUsers);
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  private void addEValueToEList(EObject eObject, EStructuralFeature esf, Object eValue) {
    if (eObject == null || esf == null || esf.getUpperBound() != EStructuralFeature.UNBOUNDED_MULTIPLICITY
        || !esf.getEContainingClass().equals(eObject.eClass())) {
      return;
    }

    List<Object> eList = (List<Object>) eObject.eGet(esf);
    if (eList == null) {
      return;
    }

    eList.add(eValue);
  }

  private boolean userHasRights(Class<?> clazz) {
    TypeOfFunctionalPermission permission = CLASS_TO_PERMISSIONS.get(clazz);
    if (permission != null) {
      return UserContext.getCurrentPerms().userHasFunctionalPermission(permission);
    }
    //since we have functional permissions for all BuildingBlock classes except AbstractAssociation subclasses,
    //one can expect, that the return true will only be used when the rights for one of thes classes is checked
    return true;
  }

  private static Map<Class<?>, TypeOfFunctionalPermission> getClassToPermsMap() {
    Map<Class<?>, TypeOfFunctionalPermission> result = Maps.newHashMap();

    for (Entry<TypeOfFunctionalPermission, Class<?>> entry : TypeOfFunctionalPermission.PERMISSION_TO_CLASS_MAP.entrySet()) {
      result.put(entry.getValue(), entry.getKey());
    }

    return result;
  }

  private String capitalize(String s) {
    return StringUtils.capitalize(s);
  }

  public void saveExtendedEPackge(OutputStream outStream) {
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    ResourceSet rs = new ResourceSetImpl();
    Resource r = rs.createResource(URI.createURI("IteraplanModelForTabularReporting_extended.ecore"));

    TabReportingEcoreData tabReportingEcoreData = ecoreService.getExtendedEPackge();
    EPackage modelPackage = tabReportingEcoreData.getModelPackage();
    modelPackage.setNsURI(modelPackage.getNsURI().replaceAll(".ecore", "_extended.ecore"));

    r.setURI(URI.createURI(""));
    r.getContents().add(modelPackage);
    rs.getPackageRegistry().put(modelPackage.getNsURI(), modelPackage);
    rs.getPackageRegistry().put(modelPackage.getName(), modelPackage);
    try {
      r.save(outStream, getSaveOptions());
    } catch (IOException e) {
      throw new IteraplanTechnicalException(e);
    }
  }

  private boolean isExtended(EStructuralFeature esf, TabReportingEcoreData tabReportingEcoreData) {
    if (esf.getName().equals("owningUserEntities")) {
      return true;
    }
    return tabReportingEcoreData.containsStructuralFeature(esf);
  }
}
