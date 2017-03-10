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
package de.iteratec.iteraplan.persistence.elasticeam.metamodel;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.DefaultSpringApplicationContext;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.MixinTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.MMetamodelComparator.MMChange;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.MMetamodelComparator.MMChangeKind;


/**
 *
 */
public class IteraplanMetamodelDifferentialWriter {

  private final IteraplanMapping mapping;

  public IteraplanMetamodelDifferentialWriter(IteraplanMapping mapping) {
    this.mapping = mapping;
  }

  public void write(Collection<MMChange<?>> diffs) {
    IteraplanDiffClassifier diffClassifier = new IteraplanDiffClassifier(this.mapping);
    Multimap<TypeOfDiff, MMChange<?>> classifiedDiffs = diffClassifier.classifyDiffElements(diffs);
    write(classifiedDiffs);
  }

  @SuppressWarnings("unchecked")
  public Collection<MMChange<?>> write(Multimap<TypeOfDiff, MMChange<?>> classifiedDiffs) {
    List<MMChange<?>> appliedDiffs = Lists.newArrayList();

    //Pass 2
    if (TypeOfDiff.IGNORED.compareTo(TypeOfDiff.ADD_ENUM) < 0) {
      for (MMChange<?> change : classifiedDiffs.get(TypeOfDiff.ADD_ENUM)) {
        updateEnum((MMChange<EnumerationExpression>) change);
        appliedDiffs.add(change);
      }
    }

    //Pass 3
    if (TypeOfDiff.IGNORED.compareTo(TypeOfDiff.ADD_ENUM_LITERAL) < 0) {
      for (MMChange<?> change : classifiedDiffs.get(TypeOfDiff.ADD_ENUM_LITERAL)) {
        updateEnumLiteral((MMChange<EnumerationLiteralExpression>) change);
        appliedDiffs.add(change);
      }
    }

    //Pass 4
    if (TypeOfDiff.IGNORED.compareTo(TypeOfDiff.ADD_PROPERTY) < 0) {
      for (MMChange<?> change : classifiedDiffs.get(TypeOfDiff.ADD_PROPERTY)) {
        updateProperty((MMChange<PropertyExpression<?>>) change);
        appliedDiffs.add(change);
      }
    }

    //Pass 5
    if (TypeOfDiff.IGNORED.compareTo(TypeOfDiff.REMOVE_PROPERTY) < 0) {
      for (MMChange<?> change : classifiedDiffs.get(TypeOfDiff.REMOVE_PROPERTY)) {
        updateProperty((MMChange<PropertyExpression<?>>) change);
        appliedDiffs.add(change);
      }
    }

    //Pass 6
    if (TypeOfDiff.IGNORED.compareTo(TypeOfDiff.REMOVE_ENUM_LITERAL) < 0) {
      for (MMChange<?> change : classifiedDiffs.get(TypeOfDiff.REMOVE_ENUM_LITERAL)) {
        updateEnumLiteral((MMChange<EnumerationLiteralExpression>) change);
        appliedDiffs.add(change);
      }
    }

    //Pass 7
    if (TypeOfDiff.IGNORED.compareTo(TypeOfDiff.REMOVE_ENUM) < 0) {
      for (MMChange<?> change : classifiedDiffs.get(TypeOfDiff.REMOVE_ENUM)) {
        updateEnum((MMChange<EnumerationExpression>) change);
        appliedDiffs.add(change);
      }
    }

    return appliedDiffs;
  }

  private void updateProperty(MMChange<PropertyExpression<?>> change) {
    AttributeTypeService attributeTypeService = (AttributeTypeService) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
        "attributeTypeService");
    AttributeTypeGroupService attributeTypeGroupService = (AttributeTypeGroupService) DefaultSpringApplicationContext.getSpringApplicationContext()
        .getBean("attributeTypeGroupService");
    PropertyExpression<?> property = change.getAffectedElement();
    AttributeType attributeType = attributeTypeService.getAttributeTypeByName(change.getAffectedElement().getPersistentName());

    if (change.getChangeKind() == MMChangeKind.ADD) {
      if (attributeType == null && property instanceof PrimitivePropertyExpression) {
        PrimitiveTypeExpression pType = ((PrimitivePropertyExpression) property).getType();
        if (BigDecimal.class.equals(pType.getEncapsulatedType())) {
          attributeType = new NumberAT();
        }
        else if (String.class.equals(pType.getEncapsulatedType())) {
          attributeType = new TextAT();
        }
        else if (Date.class.equals(pType.getEncapsulatedType())) {
          attributeType = new DateAT();
        }
        attributeType.setName(property.getPersistentName());
        attributeType.setDescription(property.getDescription());
        AttributeTypeGroup atg = attributeTypeGroupService.getStandardAttributeTypeGroup();
        atg.addAttributeTypeTwoWay(attributeType, 0);
        attributeTypeService.saveOrUpdate(attributeType);
      }
      else if (attributeType instanceof EnumAT && (property.getUpperBound() > 1 || property.getUpperBound() == EAttribute.UNBOUNDED_MULTIPLICITY)) {
        ((EnumAT) attributeType).setMultiassignmenttype(true);
      }
      attributeType.setMandatory(property.getLowerBound() == 1);
      BuildingBlockType bbt = getBBTFor(property.getHolder());
      if (bbt != null) {
        attributeType.addBuildingBlockTypeTwoWay(bbt);
      }
      else if (property.getHolder() instanceof MixinTypeExpression) {
        String name = property.getHolder().getName().replaceFirst("<<Mixin>> ", "");
        AttributeTypeGroup atg = attributeTypeGroupService.getAttributeTypeGroupByName(name);
        if (atg != null && !atg.equals(attributeType.getAttributeTypeGroup())) {
          attributeType.getAttributeTypeGroup().removeAttributeType(attributeType);
          atg.addAttributeTypeTwoWay(attributeType);
        }
      }
      attributeTypeService.saveOrUpdate(attributeType);
    }
    else if (change.getChangeKind() == MMChangeKind.DELETE) {
      BuildingBlockType bbt = getBBTFor(property.getHolder());
      attributeType.removeBuildingBlockTypeTwoWay(bbt);
      if (!(attributeType instanceof EnumAT) && attributeType.getBuildingBlockTypeIds().isEmpty()) {
        attributeTypeService.deleteEntity(attributeType);
      }
    }
  }

  private void updateEnumLiteral(MMChange<EnumerationLiteralExpression> change) {
    AttributeValueService attributeValueService = (AttributeValueService) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
        "attributeValueService");
    AttributeTypeService attributeTypeService = (AttributeTypeService) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
        "attributeTypeService");
    if (change.getChangeKind() == MMChangeKind.ADD) {
      EnumAT enumAT = this.mapping.getAdditionalEnumerationExpressions().get(change.getAffectedElement().getOwner());
      EnumAV enumAV = new EnumAV();
      enumAV.setAttributeTypeTwoWay(enumAT);
      enumAV.setDescription(change.getAffectedElement().getDescription());
      enumAV.setName(change.getAffectedElement().getPersistentName());
      attributeValueService.saveOrUpdate(enumAV);
      attributeTypeService.merge(enumAT);
    }
    else if (change.getChangeKind() == MMChangeKind.DELETE) {
      //TODO mba: remove this hack..
      EnumAV enumAV = this.mapping.getAdditionalEnumerationLiterals().get(change.getAffectedElement());
      enumAV.getAttributeType().getAttributeValues().remove(enumAV);
      SessionFactory factory = (SessionFactory) DefaultSpringApplicationContext.getSpringApplicationContext().getBean("sessionFactory");
      FlushMode before = factory.getCurrentSession().getFlushMode();
      factory.getCurrentSession().setFlushMode(FlushMode.AUTO);
      Session session = factory.getCurrentSession();
      Transaction transaction = session.beginTransaction();
      attributeValueService.deleteEntity(enumAV);
      transaction.commit();
      factory.getCurrentSession().setFlushMode(before);
    }
  }

  private void updateEnum(MMChange<EnumerationExpression> change) {
    AttributeTypeService attributeTypeService = (AttributeTypeService) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
        "attributeTypeService");
    AttributeValueService attributeValueService = (AttributeValueService) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
        "attributeValueService");
    if (change.getChangeKind() == MMChangeKind.ADD) {
      EnumAT newEnumAT = new EnumAT();
      newEnumAT.setName(change.getAffectedElement().getPersistentName().replace(EnumAT.class.getCanonicalName() + ".", ""));
      newEnumAT.setDescription(change.getAffectedElement().getDescription());
      AttributeTypeGroupDAO dao = (AttributeTypeGroupDAO) DefaultSpringApplicationContext.getSpringApplicationContext().getBean(
          "attributeTypeGroupDAO");
      AttributeTypeGroup atg = dao.getStandardAttributeTypeGroup();
      newEnumAT.setAttributeTypeGroup(atg);
      atg.addAttributeType(newEnumAT, 0);
      attributeTypeService.saveOrUpdate(newEnumAT);
      for (EnumerationLiteralExpression literal : change.getAffectedElement().getLiterals()) {
        EnumAV literalAV = new EnumAV();
        literalAV.setName(literal.getPersistentName());
        literalAV.setDescription(literal.getDescription());
        newEnumAT.addAttribueValueTwoWay(literalAV);
        attributeValueService.saveOrUpdate(literalAV);
      }
      attributeTypeService.saveOrUpdate(newEnumAT);
    }
    else if (change.getChangeKind() == MMChangeKind.DELETE) {
      EnumAT enumAT2Delete = (EnumAT) attributeTypeService.getAttributeTypeByName(change.getAffectedElement().getPersistentName()
          .replace(EnumAT.class.getCanonicalName() + ".", ""));
      attributeTypeService.deleteEntity(enumAT2Delete);
    }
  }

  protected BuildingBlockType getBBTFor(UniversalTypeExpression type) {
    HbMappedClass hbClass = this.mapping.getSubstantialTypes().get(type);
    if (hbClass == null) {
      hbClass = this.mapping.getRelationshipTypes().get(type);
    }
    if (hbClass == null) {
      return null;
    }

    if (hbClass.hasReleaseClass()) {
      hbClass = hbClass.getReleaseClass();
    }
    Class<?> mappedClass = hbClass.getMappedClass();
    if (mappedClass == null || !BuildingBlock.class.isAssignableFrom(mappedClass)) {
      return null;
    }
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.typeOfBuildingBlockForClass(mappedClass);
    BuildingBlockTypeDAO dao = (BuildingBlockTypeDAO) DefaultSpringApplicationContext.getSpringApplicationContext().getBean("buildingBlockTypeDAO");
    return dao.getBuildingBlockTypeByType(tobb);
  }
}
