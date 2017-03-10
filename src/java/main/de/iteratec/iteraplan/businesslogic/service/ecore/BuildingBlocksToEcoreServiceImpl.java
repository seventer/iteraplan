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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.xmi.exporter.ecore.Iteraplan2EMFHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFMetamodel;
import de.iteratec.iteraplan.model.AbstractAssociation;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


public class BuildingBlocksToEcoreServiceImpl implements BuildingBlocksToEcoreService {

  private static final Logger        LOGGER = Logger.getLogger(BuildingBlocksToEcoreService.class);

  private final BuildingBlockTypeDAO buildingBlockTypeDAO;

  public BuildingBlocksToEcoreServiceImpl(BuildingBlockTypeDAO buildingBlockTypeDAO) {
    this.buildingBlockTypeDAO = buildingBlockTypeDAO;
  }

  /* (non-Javadoc)
   * @see de.iteratec.iteraplan.businesslogic.service.ecore.BuildingBlocksToEcoreService#getEPackage()
   */
  public MappedEPackage createEPackage() {
    MappedEPackage result = new MappedEPackage(Iteraplan2EMFHelper.getBasicIteraplanEPackage());
    List<BuildingBlockType> bbts = this.buildingBlockTypeDAO.loadElementList("id");
    for (BuildingBlockType bbt : bbts) {
      annotateType(result, bbt);
      if (!UserContext.getCurrentPerms().getUserHasBbTypeFunctionalPermission(bbt.getTypeOfBuildingBlock().getValue())) {
        continue;
      }

      for (AttributeType at : bbt.getAttributeTypes()) {
        if (!UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ)) {
          continue;
        }

        if (at instanceof EnumAT) {
          List<EnumAV> enumAVs = ((EnumAT) at).getSortedAttributeValues();
          result.createNewEAttribute((EnumAT) at, enumAVs, bbt);
        }
        else {
          List<String> avStrings = Lists.newLinkedList();
          if (!(at instanceof TextAT)) {
            for (AttributeValue av : at.getAllAttributeValues()) {
              avStrings.add(av.getLocalizedValueString(UserContext.getCurrentLocale()));
            }
          }
          result.createNewEAttribute(at, avStrings, bbt);
        }
      }
    }
    return result;
  }

  private static void annotateType(MappedEPackage ePackage, BuildingBlockType bbt) {
    if (AbstractAssociation.class.isAssignableFrom(bbt.getTypeOfBuildingBlock().getAssociatedClass())) {
      EMFMetamodel.annotateRelationshipType(ePackage.getEClass(bbt));
    }
    else if (BusinessMapping.class.isAssignableFrom(bbt.getTypeOfBuildingBlock().getAssociatedClass())) {
      EMFMetamodel.annotateRelationshipType(ePackage.getEClass(bbt));
    }
    else if (ePackage.getEClass(bbt) != null) {
      EMFMetamodel.annotateSubstantialType(ePackage.getEClass(bbt));
    }
  }

  /* (non-Javadoc)
   * @see de.iteratec.iteraplan.businesslogic.service.ecore.BuildingBlocksToEcoreService#convertToEObjects(java.util.Collection)
   */
  public Collection<EObject> convertToEObjects(Collection<BuildingBlock> buildingBlocks) {
    Map<BuildingBlock, EObject> eObjects = Maps.newHashMap();
    if (buildingBlocks == null || buildingBlocks.isEmpty()) {
      return Collections.emptyList();
    }

    MappedEPackage mPackage = createEPackage();
    for (BuildingBlock bb : buildingBlocks) {
      EClass eClass = mPackage.getEClass(bb.getBuildingBlockType());
      EObject instance = EcoreUtil.create(eClass);
      for (EAttribute eAttribute : eClass.getEAllAttributes()) {
        setEAttribute(instance, bb, eAttribute, mPackage);
      }
      eObjects.put(bb, instance);
    }

    for (BuildingBlock bb : buildingBlocks) {
      EClass eClass = mPackage.getEClass(bb.getBuildingBlockType());
      EObject instance = eObjects.get(bb);
      for (EReference eReference : eClass.getEAllReferences()) {
        Object opposite = null;
        try {
          opposite = PropertyUtils.getSimpleProperty(bb, eReference.getName());
        } catch (IllegalAccessException iae) {
          LOGGER.error("Could not access reference value.", iae);
        } catch (InvocationTargetException ite) {
          LOGGER.error("Could not access reference value.", ite);
        } catch (NoSuchMethodException nsme) {
          LOGGER.error("Could not access reference value.", nsme);
        }
        setEStructuralFeature(instance, eReference, opposite, eObjects);
      }
    }

    return Collections.unmodifiableCollection(eObjects.values());
  }

  private static void setEAttribute(EObject instance, BuildingBlock bb, EAttribute eAtt, MappedEPackage mPackage) {
    Object value = null;
    if (mPackage.isExtended(eAtt)) {
      Collection<Object> values = new LinkedList<Object>();
      for (AttributeValueAssignment assignment : bb.getAssignmentsForId(mPackage.getAttributeType(eAtt).getId())) {
        values.add(assignment.getAttributeValue().getValue());
      }
      value = values;
    }
    else {
      try {
        value = PropertyUtils.getSimpleProperty(bb, eAtt.getName());
      } catch (IllegalAccessException iae) {
        LOGGER.error("Could not access attribute value.", iae);
      } catch (InvocationTargetException ite) {
        LOGGER.error("Could not access attribute value.", ite);
      } catch (NoSuchMethodException nsme) {
        LOGGER.error("Could not access attribute value.", nsme);
      }
    }
    if (eAtt.getEType() instanceof EEnum) {
      Map<String, EEnumLiteral> litMap = Maps.newHashMap();
      for (EEnumLiteral eLit : ((EEnum) eAtt.getEType()).getELiterals()) {
        litMap.put(eLit.getLiteral(), eLit);
      }
      setEStructuralFeature(instance, eAtt, value, litMap);
    }
    else {
      setEStructuralFeature(instance, eAtt, value);
    }
  }

  private static void setEStructuralFeature(EObject instance, EStructuralFeature feature, Object value, Map<?, ?> translation) {
    if (value == null || (value instanceof Collection && ((Collection<?>) value).isEmpty())) {
      return;
    }
    if (value instanceof Collection) {
      Collection<Object> values = Lists.newLinkedList();
      for (Object v : (Collection<?>) value) {
        if (translation.containsKey(v)) {
          values.add(translation.get(v));
        }
      }
      setEStructuralFeature(instance, feature, values);
    }
    else {
      setEStructuralFeature(instance, feature, translation.get(value));
    }
  }

  private static void setEStructuralFeature(EObject instance, EStructuralFeature feature, Object value) {
    if (value == null || (value instanceof Collection && ((Collection<?>) value).isEmpty())) {
      return;
    }

    if (value instanceof Collection) {
      if (feature.isMany()) {
        instance.eSet(feature, value);
      }
      else {
        instance.eSet(feature, ((Collection<?>) value).iterator().next());
      }
    }
    else {
      if (feature.isMany()) {
        instance.eSet(feature, Collections.singleton(value));
      }
      else {
        instance.eSet(feature, value);
      }
    }
  }
}
