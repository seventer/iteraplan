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
package de.iteratec.iteraplan.elasticeam.metamodel.emf;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.iteratec.iteraplan.elasticeam.emfimpl.EMFEnumerationLiteral;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFMetamodel;
import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.exception.MetamodelException;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;


public final class EPackageConverter {

  private EPackageConverter() {
    //Nothing here
  }

  public static Mapping<Metamodel> deriveMapping(Metamodel metamodel, boolean useLocalNames) {
    return new ExportRun(metamodel, useLocalNames);
  }

  public static EPackage convert(Metamodel metamodel, boolean useLocalNames) {
    return deriveMapping(metamodel, useLocalNames).getEPackage();
  }

  public static Mapping<EMFMetamodel> deriveMapping(EPackage ePackage, boolean useLocalNames) {
    return new ImportRun(ePackage, useLocalNames);
  }

  public static Metamodel convert(EPackage ePackage, boolean useLocalNames) {
    return deriveMapping(ePackage, useLocalNames).getMetamodel();
  }

  private static final class ImportRun extends Mapping<EMFMetamodel> {
    private final boolean bestEffort;

    ImportRun(EPackage ePackage, boolean useLocalNames) {
      super(ePackage, useLocalNames);
      this.bestEffort = false;
      convertEEnums(ePackage);
      convertEClasses(ePackage);

      for (EClassifier eClassifier : ePackage.getEClassifiers()) {
        if (eClassifier instanceof EClass) {
          try {
            convertRelationships((EClass) eClassifier);
          } catch (MetamodelException se) {
            if (!this.bestEffort) {
              throw se;
            }
          }
        }
      }
    }

    private void convertEClasses(EPackage ePkg) {
      for (EClassifier eClassifier : ePkg.getEClassifiers()) {
        String persistentName = getPersistentName(eClassifier);
        if (eClassifier instanceof EClass && persistentName != null) {
          try {
            if (isRelationshipType(eClassifier)) {
              convertUniversalTypeProperties((EClass) eClassifier, this.getMetamodel().createRelationshipType(persistentName));
            }
            else {
              convertUniversalTypeProperties((EClass) eClassifier, this.getMetamodel().createSubstantialType(persistentName));
            }
          } catch (MetamodelException se) {
            if (!this.bestEffort) {
              throw se;
            }
          }
        }
      }
    }

    private void convertEEnums(EPackage ePkg) {
      for (EClassifier eClassifier : ePkg.getEClassifiers()) {
        if (eClassifier instanceof EEnum) {
          try {
            convertEnum((EEnum) eClassifier);
          } catch (MetamodelException se) {
            if (!this.bestEffort) {
              throw se;
            }
          }
        }
      }
    }

    @Override
    protected EMFMetamodel init(EPackage theEPackage) {
      return new EMFMetamodel(getEPackage().getName());
    }

    @Override
    protected EPackage init(EMFMetamodel theMetamodel) {
      throw new UnsupportedOperationException();
    }

    private void convertRelationships(EClass eClass) {
      UniversalTypeExpression sourceType = getUniversalType(eClass);
      for (EReference eReference : eClass.getEReferences()) {
        UniversalTypeExpression targetType = getUniversalType(eReference.getEReferenceType());
        EReference eOpposite = eReference.getEOpposite();
        if (eOpposite != null) {
          String source2targetPersistentName = getPersistentName(eReference);
          String source2targetName = eReference.getName();
          String target2sourcePersistentName = getPersistentName(eOpposite);
          String target2sourceName = eOpposite.getName();
          String relationshipTypeName = getRelationshipTypeName(eReference) == null ? (getRelationshipTypeName(eOpposite) == null ? "("
              + source2targetPersistentName + "," + target2sourcePersistentName + ")" : getRelationshipTypeName(eOpposite))
              : getRelationshipTypeName(eReference);

          RelationshipExpression relationship = getMetamodel().createRelationship(relationshipTypeName, sourceType, eReference.getName(),
              eReference.getLowerBound(), eReference.getUpperBound(), targetType, eOpposite.getName(), eOpposite.getLowerBound(),
              eOpposite.getUpperBound());
          relationship.findRelationshipEndByName(source2targetPersistentName).setName(source2targetName);
          relationship.findRelationshipEndByName(source2targetPersistentName).setAbbreviation(getAbbreviation(eReference));
          relationship.findRelationshipEndByName(source2targetPersistentName).setDescription(getDescription(eReference));
          relationship.findRelationshipEndByName(target2sourcePersistentName).setName(target2sourceName);
          relationship.findRelationshipEndByName(target2sourcePersistentName).setAbbreviation(getAbbreviation(eOpposite));
          relationship.findRelationshipEndByName(target2sourcePersistentName).setDescription(getDescription(eOpposite));
        }
      }
    }

    private void convertUniversalTypeProperties(EClass eClass, UniversalTypeExpression universalTypeExpression) {
      try {
        setBasicAttributes(universalTypeExpression, eClass);
        add(eClass, universalTypeExpression);
        for (EAttribute eAttribute : eClass.getEAttributes()) {
          try {
            convertProperty(universalTypeExpression, eAttribute);
          } catch (MetamodelException se) {
            if (!this.bestEffort) {
              throw se;
            }
          }
        }
      } catch (MetamodelException se) {
        if (!this.bestEffort) {
          throw se;
        }
      }
    }

    private void convertProperty(UniversalTypeExpression universalType, EAttribute eAttribute) {
      if (UniversalTypeExpression.ID_PROPERTY.getPersistentName().equals(getPersistentName(eAttribute))) {
        return;
      }
      if (universalType instanceof SubstantialTypeExpression
          && MixinTypeNamed.NAME_PROPERTY.getPersistentName().equals(getPersistentName(eAttribute))) {
        return;
      }
      if (universalType instanceof SubstantialTypeExpression
          && MixinTypeNamed.DESCRIPTION_PROPERTY.getPersistentName().equals(getPersistentName(eAttribute))) {
        return;
      }
      PropertyExpression<?> result = null;
      if (eAttribute.getEAttributeType() instanceof EEnum) {
        EnumerationExpression enumeration = getEnumeration((EEnum) eAttribute.getEAttributeType());
        result = this.getMetamodel().createProperty(universalType, getPersistentName(eAttribute), eAttribute.getLowerBound(),
            eAttribute.getUpperBound(), enumeration);
      }
      else {
        PrimitiveTypeExpression primitiveType = getOrCreatePrimitiveType(eAttribute.getEAttributeType());
        result = this.getMetamodel().createProperty(universalType, getPersistentName(eAttribute), eAttribute.getLowerBound(),
            eAttribute.getUpperBound(), primitiveType);
      }
      setBasicAttributes(result, eAttribute);
    }

    private PrimitiveTypeExpression getOrCreatePrimitiveType(EDataType dataType) {
      DataTypeExpression dT = getMetamodel().findDataTypeByPersistentName(dataType.getInstanceClassName());
      if (dT instanceof PrimitiveTypeExpression) {
        return (PrimitiveTypeExpression) dT;
      }

      PrimitiveTypeExpression result = getPrimitiveType(dataType);
      if (result != null) {
        return result;
      }

      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "No primitive type defined for eDataType " + dataType);
    }

    private void convertEnum(EEnum eEnum) {
      try {
        EnumerationExpression enumeration = this.getMetamodel().createEnumeration(getPersistentName(eEnum));
        setBasicAttributes(enumeration, eEnum);
        add(eEnum, enumeration);
        for (EEnumLiteral eLiteral : eEnum.getELiterals()) {
          if (!EMFEnumerationLiteral.NOT_SPECIFIED.equals(eLiteral.getName())) {
            try {
              EnumerationLiteralExpression literal = this.getMetamodel().createEnumerationLiteral(enumeration, getPersistentName(eLiteral), null);
              setBasicAttributes(literal, eLiteral);
            } catch (MetamodelException se) {
              if (!this.bestEffort) {
                throw se;
              }
            }
          }
        }
      } catch (MetamodelException se) {
        if (!this.bestEffort) {
          throw se;
        }
      }
    }

    private void setBasicAttributes(NamedExpression namedExpression, ENamedElement eNamedElement) {
      namedExpression.setName(eNamedElement.getName());
      namedExpression.setDescription(getDescription(eNamedElement));
      namedExpression.setAbbreviation(getAbbreviation(eNamedElement));
    }
  }

  private static final class ExportRun extends Mapping<Metamodel> {
    ExportRun(Metamodel metamodel, boolean useLocalNames) {
      super(metamodel, useLocalNames);
      for (EnumerationExpression enumeration : metamodel.getEnumerationTypes()) {
        convertEnum(enumeration);
      }
      for (UniversalTypeExpression universalType : metamodel.getUniversalTypes()) {
        convertUniversalType(universalType);
      }
      for (RelationshipExpression relationship : metamodel.getRelationships()) {
        convertRelationship(relationship);
      }
    }

    @Override
    protected Metamodel init(EPackage theEPackage) {
      throw new UnsupportedOperationException();
    }

    @Override
    protected EPackage init(Metamodel theMetamodel) {
      EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
      setName(pkg, getMetamodel().getName(), getMetamodel().getName());
      pkg.setNsPrefix("iteraplan");
      pkg.setNsURI("urn:iteraplan");
      return pkg;
    }

    private void convertUniversalType(UniversalTypeExpression universalType) {
      EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      setBasicAttributes(eClass, universalType);
      annotateEClass(eClass, universalType);
      add(universalType, eClass);
      addProperties(universalType);
      this.getEPackage().getEClassifiers().add(eClass);
    }

    private void convertRelationship(RelationshipExpression relationship) {
      EClass end0 = getEClass(relationship.getRelationshipEnds().get(0).getHolder());
      EClass end1 = getEClass(relationship.getRelationshipEnds().get(1).getHolder());

      EReference ref0t1 = EcoreFactory.eINSTANCE.createEReference();
      setBasicAttributes(ref0t1, relationship.getRelationshipEnds().get(0));
      setCardinalityConstraints(ref0t1, relationship.getRelationshipEnds().get(0));
      ref0t1.setEType(end1);
      end0.getEStructuralFeatures().add(ref0t1);
      EReference ref1t0 = EcoreFactory.eINSTANCE.createEReference();
      setBasicAttributes(ref1t0, relationship.getRelationshipEnds().get(1));
      setCardinalityConstraints(ref1t0, relationship.getRelationshipEnds().get(1));
      ref1t0.setEType(end0);
      end1.getEStructuralFeatures().add(ref1t0);
      ref0t1.setEOpposite(ref1t0);
    }

    private void addProperties(UniversalTypeExpression universalType) {
      for (PropertyExpression<?> property : universalType.getProperties()) {
        EDataType dataType = getEDataType(property);
        if (dataType != null) {
          EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
          setBasicAttributes(eAttribute, property);
          setCardinalityConstraints(eAttribute, property);
          eAttribute.setEType(dataType);
          eAttribute.setID(UniversalTypeExpression.ID_PROPERTY.equals(property));
          getEClass(universalType).getEStructuralFeatures().add(eAttribute);
        }
      }
    }

    private static void setCardinalityConstraints(EStructuralFeature eStructuralFeature, FeatureExpression<?> cardinalityConstrainedFeature) {
      eStructuralFeature.setLowerBound(cardinalityConstrainedFeature.getLowerBound());
      eStructuralFeature.setUpperBound(cardinalityConstrainedFeature.getUpperBound());
    }

    private EDataType getEDataType(PropertyExpression<?> property) {
      if (PrimitivePropertyExpression.class.isInstance(property)) {
        for (EClassifier candidate : EcorePackage.eINSTANCE.getEClassifiers()) {
          if (EDataType.class.isInstance(candidate)
              && ((EDataType) candidate).getInstanceClass().equals(((PrimitivePropertyExpression) property).getType().getEncapsulatedType())) {
            return (EDataType) candidate;
          }
        }

        EDataType builtinType = getPrimitiveTypes().inverse().get(property.getType());
        if (builtinType == null) {
          builtinType = EcoreFactory.eINSTANCE.createEDataType();
          builtinType.setName(property.getType().getPersistentName());
          setBasicAttributes(builtinType, property.getType());
          builtinType.setInstanceClass(((PrimitiveTypeExpression) property.getType()).getEncapsulatedType());
          this.getEPackage().getEClassifiers().add(builtinType);
          add(builtinType, (PrimitiveTypeExpression) property.getType());
        }
        return builtinType;
      }
      else if (EnumerationPropertyExpression.class.isInstance(property)) {
        return getEEnum(((EnumerationPropertyExpression) property).getType());
      }
      // Handle here
      return null;
    }

    private void convertEnum(EnumerationExpression enumeration) {
      EEnum eEnum = EcoreFactory.eINSTANCE.createEEnum();
      setBasicAttributes(eEnum, enumeration);
      add(enumeration, eEnum);
      EMFMetamodel.addNullLiteral(eEnum);
      int count = 1;
      for (EnumerationLiteralExpression literal : enumeration.getLiterals()) {
        EEnumLiteral eLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
        setBasicAttributes(eLiteral, literal);
        eLiteral.setValue(count++);
        eEnum.getELiterals().add(eLiteral);
      }
      this.getEPackage().getEClassifiers().add(eEnum);
    }

    private void setBasicAttributes(ENamedElement eNamedElement, NamedExpression namedExpression) {
      setName(eNamedElement, namedExpression.getName(), namedExpression.getPersistentName());
      if (namedExpression.getDescription() != null && !namedExpression.getDescription().isEmpty()) {
        EcoreUtil.setAnnotation(eNamedElement, METAMODELANNOTATIONURI, DESCRIPTIONKEY, namedExpression.getDescription());
      }
      if (namedExpression.getAbbreviation() != null && !namedExpression.getAbbreviation().isEmpty()) {
        EcoreUtil.setAnnotation(eNamedElement, METAMODELANNOTATIONURI, ABBREVIATIONKEY, namedExpression.getAbbreviation());
      }
    }
  }
}
