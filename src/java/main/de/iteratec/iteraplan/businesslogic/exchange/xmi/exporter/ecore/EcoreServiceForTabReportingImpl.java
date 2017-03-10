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
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * The service for creating the ecore model for the tabular reporting. This service uses the predefined 
 * {@code EcoreServiceForTabReporting#LOCATION_OF_ECORE} ecore model, then the enabled attribute types are added to each 
 * building block.
 */
public final class EcoreServiceForTabReportingImpl implements EcoreServiceForTabReporting {
  private static final Logger        LOGGER = Logger.getIteraplanLogger(EcoreServiceForTabReportingImpl.class);

  private final BuildingBlockTypeDAO buildingBlockTypeDAO;
  private final AttributeTypeDAO     attributeTypeDAO;

  public EcoreServiceForTabReportingImpl(BuildingBlockTypeDAO buildingBlockTypeDAO, AttributeTypeDAO attributeTypeDAO) {
    this.buildingBlockTypeDAO = buildingBlockTypeDAO;
    this.attributeTypeDAO = attributeTypeDAO;
  }

  /** {@inheritDoc} */
  public TabReportingEcoreData getExtendedEPackge() {
    EPackage modelPackage = Iteraplan2EMFHelper.getBasicIteraplanEPackage();
    TabReportingEcoreData tabReportingEcoreData = createAdditionalEStructuralFeatures(modelPackage);

    return tabReportingEcoreData;
  }

  private TabReportingEcoreData createAdditionalEStructuralFeatures(EPackage modelPackage) {
    TabReportingEcoreData data = new TabReportingEcoreData(modelPackage);
    Multimap<Class<? extends AttributeType>, AttributeType> fetchAttributeTypes = fetchAttributeTypes();

    for (Class<? extends AttributeType> attributeTypeClass : fetchAttributeTypes.keySet()) {
      if (attributeTypeClass.equals(EnumAT.class)) {
        handleEnumATs(fetchAttributeTypes, data);
      }
      else {
        handleSimpleATs(fetchAttributeTypes.get(attributeTypeClass), data);
      }
    }

    return data;
  }

  private void handleSimpleATs(Collection<AttributeType> attributeTypesOfClass, TabReportingEcoreData data) {
    for (AttributeType attributeType : attributeTypesOfClass) {
      addNewEAttributeToEClasses(attributeType, data);
    }
  }

  private void handleEnumATs(Multimap<Class<? extends AttributeType>, AttributeType> attributeTypes, TabReportingEcoreData data) {
    Collection<AttributeType> enumAttributeTypes = attributeTypes.get(EnumAT.class);

    for (AttributeType at : enumAttributeTypes) {
      EnumAT enumAT = (EnumAT) at;
      EEnum newEEnum = generateNewEEnumFor(enumAT);
      data.getModelPackage().getEClassifiers().add(newEEnum);

      List<BuildingBlockType> buildingBlockTypesForAttributeType = buildingBlockTypeDAO.getConnectedBuildingBlockTypesForAttributeType(at.getId());
      for (BuildingBlockType bbt : buildingBlockTypesForAttributeType) {
        EAttribute newAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        Iteraplan2EMFHelper.setName(newAttribute, enumAT.getName());
        newAttribute.setEType(newEEnum);
        newAttribute.setLowerBound(enumAT.isMandatory() ? 1 : 0);
        newAttribute.setUpperBound(enumAT.isMultiassignmenttype() ? EStructuralFeature.UNBOUNDED_MULTIPLICITY : 1);
        newAttribute.setUnique(false);

        EClass eClass = (EClass) data.getModelPackage().getEClassifier(bbt.getTypeOfBuildingBlock().getAssociatedClass().getSimpleName());
        if (eClass != null) {
          eClass.getEStructuralFeatures().add(newAttribute);
        }
        else {
          LOGGER.error("Encountered a class '" + bbt.getTypeOfBuildingBlock()
              + "' which is not present in the current ECore meta-model! This is most probably a bug.");
        }

        data.addStructuralFeature(newAttribute, at);
        data.addEnum(newEEnum, at);
      }
    }
  }

  private static EEnum generateNewEEnumFor(EnumAT enumAT) {
    EEnum newEEnum = EcoreFactory.eINSTANCE.createEEnum();
    Iteraplan2EMFHelper.setName(newEEnum, enumAT.getName());

    EEnumLiteral undefined = EcoreFactory.eINSTANCE.createEEnumLiteral();
    undefined.setValue(0);
    undefined.setLiteral("-");
    undefined.setName("UNDEFINED");
    newEEnum.getELiterals().add(undefined);

    int count = 1;
    for (EnumAV av : enumAT.getSortedAttributeValues()) {
      if (av != null) {
        EEnumLiteral eLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
        eLiteral.setName(av.getName());
        eLiteral.setValue(count);
        eLiteral.setLiteral(av.getValue());
        newEEnum.getELiterals().add(eLiteral);
        count++;
      }
    }

    return newEEnum;
  }

  private void addNewEAttributeToEClasses(AttributeType attributeType, TabReportingEcoreData data) {
    if (attributeType == null) {
      return;
    }

    Integer atId = attributeType.getId();
    for (BuildingBlockType bbt : buildingBlockTypeDAO.getConnectedBuildingBlockTypesForAttributeType(atId)) {
      String associatedClassName = bbt.getTypeOfBuildingBlock().getAssociatedClass().getSimpleName();
      EClass containingEClass = (EClass) data.getModelPackage().getEClassifier(associatedClassName);
      EAttribute eatt = generateEAttForAT(attributeType);
      if (containingEClass != null) {
        containingEClass.getEStructuralFeatures().add(eatt);
      }
      else {
        LOGGER.error("Encountered a class '" + bbt.getTypeOfBuildingBlock()
            + "' which is not present in the current ECore meta-model! This is most probably a bug.");
      }

      data.addStructuralFeature(eatt, attributeType);
    }
  }

  private EAttribute generateEAttForAT(AttributeType attributeType) {
    EAttribute eAtt = EcoreFactory.eINSTANCE.createEAttribute();
    Iteraplan2EMFHelper.setName(eAtt, attributeType.getName());
    Class<?> attrbiteTypeClass = attributeType.getClass();

    if (attrbiteTypeClass.equals(TextAT.class) || attrbiteTypeClass.equals(ResponsibilityAT.class)) {
      eAtt.setEType(EcoreFactory.eINSTANCE.getEcorePackage().getEString());
    }
    if (attrbiteTypeClass.equals(NumberAT.class)) {
      eAtt.setEType(EcoreFactory.eINSTANCE.getEcorePackage().getEBigDecimal());
    }
    if (attrbiteTypeClass.equals(DateAT.class)) {
      eAtt.setEType(EcoreFactory.eINSTANCE.getEcorePackage().getEDate());
    }

    eAtt.setLowerBound(attributeType.isMandatory() ? 1 : 0);
    eAtt.setUpperBound(1);
    if (attributeType instanceof ResponsibilityAT) {
      eAtt.setUpperBound(((ResponsibilityAT) attributeType).isMultiassignmenttype() ? EStructuralFeature.UNBOUNDED_MULTIPLICITY : 1);
    }
    if (attributeType instanceof EnumAT) {
      eAtt.setUpperBound(((EnumAT) attributeType).isMultiassignmenttype() ? EStructuralFeature.UNBOUNDED_MULTIPLICITY : 1);
    }
    eAtt.setUnique(false);
    return eAtt;
  }

  private Multimap<Class<? extends AttributeType>, AttributeType> fetchAttributeTypes() {
    Multimap<Class<? extends AttributeType>, AttributeType> attributeTypes = ArrayListMultimap.create();
    for (AttributeType at : attributeTypeDAO.loadFilteredElementList("name", null)) {
      attributeTypes.put(at.getClass(), at);
    }

    return attributeTypes;
  }
}