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
package de.iteratec.iteraplan.elasticeam.emfimpl;

import java.awt.Color;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.exception.MetamodelException;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeaturePermissions;
import de.iteratec.iteraplan.elasticeam.metamodel.MixedOrPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.MixinTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression.NameChangeEvent;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.TypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypePermissions;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.util.ElasticeamContextUtil;
import de.iteratec.iteraplan.elasticeam.util.I18nMap;
import de.iteratec.iteraplan.model.user.Role;


@SuppressWarnings("PMD.TooManyMethods")
public class EMFMetamodel implements EditableMetamodel, Observer {

  private static final String ANNOTATIONURI    = "urn:IM2L";
  private static final String DISCRIMINATORKEY = "type";

  /**
   * Annotate an {@link EDataType} to flag as {@link EnumerationExpression}
   * 
   * @param eDataType
   *    the {@link EDataType} to annotate
   */
  public static void annotateEnumerationType(EDataType eDataType) {
    EcoreUtil.setAnnotation(eDataType, ANNOTATIONURI, DISCRIMINATORKEY, EnumerationExpression.class.getSimpleName());
  }

  /**
   * Annotate an {@link EDataType} to flag as {@link PrimitiveTypeExpression}
   * 
   * @param eDataType
   *    the {@link EDataType} to annotate
   */
  public static void annotatePrimitiveType(EDataType eDataType) {
    EcoreUtil.setAnnotation(eDataType, ANNOTATIONURI, DISCRIMINATORKEY, PrimitiveTypeExpression.class.getSimpleName());
  }

  /**
   * Annotate an {@link EClass} to flag as {@link RelationshipTypeExpression}
   * 
   * @param eClass
   *    the {@link EClass} to annotate
   */
  public static void annotateRelationshipType(EClass eClass) {
    EcoreUtil.setAnnotation(eClass, ANNOTATIONURI, DISCRIMINATORKEY, RelationshipTypeExpression.class.getSimpleName());
  }

  /**
   * Annotate an {@link EClass} to flag as {@link SubstantialTypeExpression}
   * 
   * @param eClass
   *    the {@link EClass} to annotate
   */
  public static void annotateSubstantialType(EClass eClass) {
    EcoreUtil.setAnnotation(eClass, ANNOTATIONURI, DISCRIMINATORKEY, SubstantialTypeExpression.class.getSimpleName());
  }

  /**
   * Checks the annotations of an {@link EDataType} to check whether it is representing an {@link EnumerationExpression}
   * 
   * @param eDataType
   *    the {@link EDataType} to check
   * @return
   *    true, if the {@link EDataType} is representing an {@link EnumerationExpression}
   */
  public static boolean isEnumerationType(EDataType eDataType) {
    return EnumerationExpression.class.getSimpleName().equals(EcoreUtil.getAnnotation(eDataType, ANNOTATIONURI, DISCRIMINATORKEY));
  }

  /**
   * Checks the annotations of an {@link EDataType} to check whether it is representing a {@link PrimitiveTypeExpression}
   * 
   * @param eDataType
   *    the {@link EDataType} to check
   * @return
   *    true, if the {@link EDataType} is representing a {@link PrimitiveTypeExpression}
   */
  public static boolean isPrimitiveType(EDataType eDataType) {
    return PrimitiveTypeExpression.class.getSimpleName().equals(EcoreUtil.getAnnotation(eDataType, ANNOTATIONURI, DISCRIMINATORKEY));
  }

  /**
   * Checks the {@link EClass}' annotations to check whether it is representing an {@link RelationshipTypeExpression} or not (e.g. {@link SubstantialTypeExpression})
   * @param eClass
   *    the {@link EClass} to check
   * @return
   *    true, if the {@link EClass} is representing a {@link RelationshipTypeExpression}
   */
  public static boolean isRelationshipType(EClass eClass) {
    return RelationshipTypeExpression.class.getSimpleName().equals(EcoreUtil.getAnnotation(eClass, ANNOTATIONURI, DISCRIMINATORKEY));
  }

  /**
   * Checks the {@link EClass}' annotations to check whether it is representing an {@link SubstantialTypeExpression} or not (e.g. {@link RelationshipTypeExpression})
   * @param eClass
   *    the {@link EClass} to check
   * @return
   *    true, if the {@link EClass} is representing a {@link SubstantialTypeExpression}
   */
  public final static boolean isSubstantialType(EClass eClass) {
    return SubstantialTypeExpression.class.getSimpleName().equals(EcoreUtil.getAnnotation(eClass, ANNOTATIONURI, DISCRIMINATORKEY));
  }

  private EPackage                                                              wrapped;
  private final BiMap<EAttribute, BiMap<EClass, PropertyExpression<?>>>         properties;
  private final BiMap<EDataType, PrimitiveTypeExpression>                       primitiveTypes;

  private final BiMap<EEnum, EnumerationExpression>                             enumerations;
  private final BiMap<EEnumLiteral, BiMap<EEnum, EnumerationLiteralExpression>> literals;

  private final BiMap<EReference, BiMap<EClass, RelationshipEndExpression>>     relationshipEnds;

  private final BiMap<EClass, UniversalTypeExpression>                          universalTypes;

  private final I18nMap<TypeExpression>                                         types;

  private final Multimap<RelationshipExpression, RelationshipEndExpression>     relationship2relationshipEnds;

  private final Multimap<EnumerationExpression, EnumerationPropertyExpression>  enumeration2properties;

  private final Multimap<PrimitiveTypeExpression, PrimitivePropertyExpression>  primitiveType2properties;

  private final ListMultimap<Role, TypeExpression>                              readableTypes;

  private final ListMultimap<Role, FeatureExpression<?>>                        readableFeatures;

  private boolean                                                               disableAccessControl = false;

  /**
   * Default constructor.
   */
  public EMFMetamodel(String persistentName) {
    this();
    this.wrapped = EcoreFactory.eINSTANCE.createEPackage();
    this.wrapped.setName(persistentName);
    addDefaultPrimitiveTypes();
  }

  /**
   * Creates a metamodel which does not not perform access control. 
   */
  public EMFMetamodel(String persistentName, boolean disableAccessControl) {
    this(persistentName);
    this.disableAccessControl = disableAccessControl;
  }

  private EMFMetamodel() {
    this.properties = HashBiMap.create();
    this.primitiveTypes = HashBiMap.create();
    this.enumerations = HashBiMap.create();
    this.literals = HashBiMap.create();
    this.relationshipEnds = HashBiMap.create();
    this.universalTypes = HashBiMap.create();
    this.enumeration2properties = ArrayListMultimap.create();
    this.relationship2relationshipEnds = ArrayListMultimap.create();
    this.primitiveType2properties = ArrayListMultimap.create();
    this.types = I18nMap.create();
    this.readableFeatures = ArrayListMultimap.create();
    this.readableTypes = ArrayListMultimap.create();
  }

  /**{@inheritDoc}**/
  public void addMixin(SubstantialTypeExpression substantialType, MixinTypeExpression mixin) {
    for (PropertyExpression<?> pe : mixin.getProperties()) {
      if (pe instanceof EnumerationPropertyExpression) {
        EMFEnumerationProperty prop = createPropertyImpl(this.universalTypes.inverse().get(substantialType), pe.getPersistentName(),
            pe.getLowerBound(), pe.getUpperBound(), this.enumerations.inverse().get(((EnumerationPropertyExpression) pe).getType()), mixin);
        //TODO
        //NamedImpl.copy(pe, prop);

        prop.addObserver(substantialType);
        prop.setName(pe.getPersistentName());
      }
      if (pe instanceof PrimitivePropertyExpression) {
        EMFPrimitiveProperty prop = createPropertyImpl(this.universalTypes.inverse().get(substantialType), pe.getPersistentName(),
            pe.getLowerBound(), pe.getUpperBound(), getEDataType(((PrimitivePropertyExpression) pe).getType().getEncapsulatedType()), mixin);
        prop.addObserver(substantialType);
        prop.setName(pe.getPersistentName());
        //TODO
        //NamedImpl.copy(pe, prop);
      }
    }
    for (Entry<RelationshipEndExpression, RelationshipEndExpression> entry : mixin.getRelationshipEndPairs().entrySet()) {
      // needs to be changed, in case of mixins representing relationships connecting different types 
      createRelationship(mixin.getPersistentName() + "_" + substantialType.getPersistentName(), substantialType, entry.getKey().getPersistentName(),
          entry.getKey().getLowerBound(), entry.getKey().getUpperBound(), substantialType, entry.getValue().getPersistentName(), entry.getValue()
              .getLowerBound(), entry.getValue().getUpperBound());
    }
  }

  /**{@inheritDoc}**/
  public final EnumerationExpression createEnumeration(String persistentName) {
    if (this.wrapped.getEClassifier(persistentName) != null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot create enumeration with non unique persistent name " + persistentName
          + ".");
    }
    EEnum eEnum = EcoreFactory.eINSTANCE.createEEnum();
    eEnum.setName(persistentName);
    annotateEnumerationType(eEnum);
    this.wrapped.getEClassifiers().add(eEnum);
    EMFEnumeration enumeration = new EMFEnumeration(eEnum, this);
    this.enumerations.put(eEnum, enumeration);
    addNullLiteral(eEnum);
    enumeration.addObserver(this);
    return enumeration;
  }

  /**
   * After creation of new EEnum (for a new EMFEnumeration), create a default literal that represents "null"/"unspecified"
   * @param eEnum
   */
  public static final void addNullLiteral(EEnum eEnum) {
    EEnumLiteral nullLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
    nullLiteral.setValue(0);
    nullLiteral.setName(EMFEnumerationLiteral.NOT_SPECIFIED);
    nullLiteral.setLiteral(EMFEnumerationLiteral.NOT_SPECIFIED);
    eEnum.getELiterals().add(nullLiteral);
  }

  /**{@inheritDoc}**/
  public final EnumerationLiteralExpression createEnumerationLiteral(EnumerationExpression enumeration, String persistentName,
                                                                     Color defaultLiteralColor) {
    EEnum eEnum = this.enumerations.inverse().get(enumeration);
    if (eEnum == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot create enumeration literal for non-canonic enumeration expression.");
    }
    if (eEnum.getEEnumLiteral(persistentName) != null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot create enumeration literal with non unique persistent name "
          + persistentName + ".");
    }
    EEnumLiteral eEnumLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
    eEnumLiteral.setName(persistentName);
    eEnumLiteral.setValue(eEnum.getELiterals().size());
    eEnum.getELiterals().add(eEnumLiteral);
    EMFEnumerationLiteral literal = new EMFEnumerationLiteral(eEnumLiteral, this, defaultLiteralColor);
    BiMap<EEnum, EnumerationLiteralExpression> value = HashBiMap.create();
    value.put(eEnum, literal);
    this.literals.put(eEnumLiteral, value);
    return literal;
  }

  public final PropertyExpression<?> createProperty(UniversalTypeExpression owner, String persistentName, int lowerBound, int upperBound,
                                                    DataTypeExpression type) {
    if (PrimitiveTypeExpression.class.isInstance(type)) {
      return createProperty(owner, persistentName, lowerBound, upperBound, (PrimitiveTypeExpression) type);
    }
    else if (EnumerationExpression.class.isInstance(type)) {
      return createProperty(owner, persistentName, lowerBound, upperBound, (EnumerationExpression) type);
    }
    else {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Unknown data type expression: " + type);
    }
  }

  /**{@inheritDoc}**/
  public final EnumerationPropertyExpression createProperty(UniversalTypeExpression owner, String persistentName, int lowerBound, int upperBound,
                                                            EnumerationExpression type) {
    EClass eClass = this.universalTypes.inverse().get(owner);
    if (eClass == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add canonic property to non-canonic universal type.");
    }
    if (eClass.getEStructuralFeature(persistentName) != null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add property with duplicate persistent name " + persistentName + ".");
    }
    EEnum eEnum = this.enumerations.inverse().get(type);
    //    if (eEnum == null) {
    //      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add canonic property to non-canonic type.");
    //    }

    EMFEnumerationProperty property = createPropertyImpl(eClass, persistentName, lowerBound, upperBound, eEnum);
    property.addObserver(owner);
    property.setName(persistentName);

    return property;
  }

  /**{@inheritDoc}**/
  public final PrimitivePropertyExpression createProperty(UniversalTypeExpression owner, String persistentName, int lowerBound, int upperBound,
                                                          PrimitiveTypeExpression type) {
    EClass eClass = this.universalTypes.inverse().get(owner);
    if (eClass == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add canonic property to non-canonic universal type.");
    }
    if (eClass.getEStructuralFeature(persistentName) != null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add property with duplicate persistent name " + persistentName + ".");
    }

    EDataType dataType = getEDataType(type.getEncapsulatedType());

    if (dataType == null || !this.primitiveTypes.containsKey(dataType)) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR,
          "Can not create a property whose primitive type does not exist in the metamodel.");
    }

    EMFPrimitiveProperty property = createPropertyImpl(eClass, persistentName, lowerBound, upperBound, dataType);
    property.addObserver(owner);
    property.setName(persistentName);

    return property;
  }

  /**{@inheritDoc}**/
  public MixedOrPropertyExpression createMixedOrProperty(UniversalTypeExpression owner, String persistentName, int lowerBound, int upperBound,
                                                         Set<PrimitiveTypeExpression> admissibleDataTypes, boolean matchEnumerations) {
    EClass eClass = this.universalTypes.inverse().get(owner);
    if (eClass == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add canonic property to non-canonic universal type.");
    }
    if (eClass.getEStructuralFeature(persistentName) != null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add property with duplicate persistent name " + persistentName + ".");
    }

    if (admissibleDataTypes == null || admissibleDataTypes.isEmpty()) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "A mixed OR property must have at least one primitive type.");
    }

    EDataType mainEDataType = null;
    Set<EDataType> allEDataTypes = Sets.newHashSet();
    for (PrimitiveTypeExpression type : admissibleDataTypes) {
      EDataType dataType = getEDataType(type.getEncapsulatedType());

      if (dataType == null || !this.primitiveTypes.containsKey(dataType)) {
        throw new MetamodelException(MetamodelException.GENERAL_ERROR,
            "Can not create a property whose primitive type does not exist in the metamodel.");
      }
      if (mainEDataType == null) {
        mainEDataType = dataType;
      }
      allEDataTypes.add(dataType);
    }

    EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    //EcoreUtil.setAnnotation(eAttribute, ANNOTATIONURI, DISCRIMINATORKEY, "mixedOrProperty");
    eAttribute.setName(persistentName);
    eAttribute.setLowerBound(lowerBound);
    eAttribute.setUpperBound(upperBound);
    eAttribute.setEType(mainEDataType);
    eClass.getEStructuralFeatures().add(eAttribute);

    EMFMixedOrProperty result = new EMFMixedOrProperty(eAttribute, this, allEDataTypes, matchEnumerations);

    BiMap<EClass, PropertyExpression<?>> value = HashBiMap.create();
    value.put(eClass, result);
    this.properties.put(eAttribute, value);

    for (PrimitiveTypeExpression primitiveType : result.getTypes()) {
      this.primitiveType2properties.put(primitiveType, result);
    }

    result.addObserver(owner);
    result.setName(persistentName);

    return result;
  }

  /**{@inheritDoc}**/
  public final RelationshipExpression createRelationship(String persistentName, UniversalTypeExpression holder0, String end0Name, int end0lower,
                                                         int end0upper, UniversalTypeExpression holder1, String end1Name, int end1lower, int end1upper) {
    return createRelationship(persistentName, holder0, end0Name, end0lower, end0upper, holder1, end1Name, end1lower, end1upper, true);
  }

  /**{@inheritDoc}**/
  public RelationshipExpression createRelationship(String persistentName, UniversalTypeExpression type, String end0Name, int end0lower,
                                                   int end0upper, String end1Name, int end1lower, int end1upper, boolean acycic) {
    return createRelationship(persistentName, type, end0Name, end0lower, end0upper, type, end1Name, end1lower, end1upper, acycic);
  }

  final RelationshipExpression createRelationship(String persistentName, UniversalTypeExpression holder0, String end0Name, int end0lower,
                                                  int end0upper, UniversalTypeExpression holder1, String end1Name, int end1lower, int end1upper,
                                                  boolean acyclic) {
    EClass eClass0 = this.universalTypes.inverse().get(holder0);
    if (eClass0 == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add canonic relationship to non-canonic universal type.");
    }
    EClass eClass1 = this.universalTypes.inverse().get(holder1);
    if (eClass1 == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add canonic relationship to non-canonic universal type.");
    }
    if (isRelationshipType(eClass0) && isRelationshipType(eClass1)) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add relationship between two relationship types.");
    }
    if (eClass0.getEStructuralFeature(end0Name) != null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add relationship end with duplicate persistent name " + end0Name + ".");
    }
    if (eClass1.getEStructuralFeature(end1Name) != null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot add relationship end with duplicate persistent name " + end1Name + ".");
    }
    EReference eReference0 = EcoreFactory.eINSTANCE.createEReference();
    eReference0.setName(end0Name);
    eReference0.setLowerBound(end0lower);
    eReference0.setUpperBound(end0upper);
    eReference0.setEType(eClass1);
    eClass0.getEStructuralFeatures().add(eReference0);
    EReference eReference1 = EcoreFactory.eINSTANCE.createEReference();
    eReference1.setName(end1Name);
    eReference1.setLowerBound(end1lower);
    eReference1.setUpperBound(end1upper);
    eReference1.setEType(eClass0);
    eClass1.getEStructuralFeatures().add(eReference1);
    eReference0.setEOpposite(eReference1);
    //note: the emf opposite is not reflexive and has thus to be explicitly set in both directions
    eReference1.setEOpposite(eReference0);
    EMFRelationshipEnd relationshipEnd0 = new EMFRelationshipEnd(eReference0, this);
    BiMap<EClass, RelationshipEndExpression> value0 = HashBiMap.create();
    value0.put(eClass0, relationshipEnd0);
    this.relationshipEnds.put(eReference0, value0);
    EMFRelationshipEnd relationshipEnd1 = new EMFRelationshipEnd(eReference1, this);
    BiMap<EClass, RelationshipEndExpression> value1 = HashBiMap.create();
    value1.put(eClass1, relationshipEnd1);
    this.relationshipEnds.put(eReference1, value1);
    EMFRelationship result = new EMFRelationship(persistentName, eReference0, eReference1, this, acyclic);
    this.relationship2relationshipEnds.put(result, relationshipEnd0);
    this.relationship2relationshipEnds.put(result, relationshipEnd1);

    relationshipEnd0.addObserver(holder0);
    relationshipEnd0.setName(end0Name);

    relationshipEnd1.addObserver(holder1);
    relationshipEnd1.setName(end1Name);

    return result;
  }

  /**{@inheritDoc}**/
  public final RelationshipTypeExpression createRelationshipType(String persistentName) {
    if (this.wrapped.getEClassifier(persistentName) != null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot create relationship type with duplicate persistent name "
          + persistentName + ".");
    }
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName(persistentName);
    annotateRelationshipType(eClass);
    this.wrapped.getEClassifiers().add(eClass);
    EMFRelationshipType result = new EMFRelationshipType(eClass, this);
    this.universalTypes.put(eClass, result);

    EMFPrimitiveProperty idProp = createIDPropertyImpl(eClass);
    idProp.addObserver(result);
    idProp.setName(UniversalTypeExpression.ID_PROPERTY.getPersistentName());

    result.addObserver(this);
    result.setName(persistentName);

    return result;
  }

  /**{@inheritDoc}**/
  public final SubstantialTypeExpression createSubstantialType(String persistentName) {
    if (this.wrapped.getEClassifier(persistentName) != null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot create substantial type with duplicate persistent name "
          + persistentName + ".");
    }
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName(persistentName);
    annotateSubstantialType(eClass);
    this.wrapped.getEClassifiers().add(eClass);
    EMFSubstantialType result = new EMFSubstantialType(eClass, this);
    this.universalTypes.put(eClass, result);

    EMFPrimitiveProperty idProp = createIDPropertyImpl(eClass);
    idProp.addObserver(result);
    idProp.setName(UniversalTypeExpression.ID_PROPERTY.getPersistentName());

    addMixin(result, MixinTypeNamed.INSTANCE);
    result.addObserver(this);
    return result;
  }

  /**{@inheritDoc}**/
  public final void deleteEnumeration(EnumerationExpression enumeration) {
    EEnum eEnum = this.enumerations.inverse().remove(enumeration);
    if (eEnum == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot delete non-canonic enumeration.");
    }
    Set<EAttribute> eAtts2Delete = Sets.newHashSet();
    Set<EnumerationPropertyExpression> propertiesToDelete = Sets.newHashSet();
    for (EnumerationPropertyExpression property : this.enumeration2properties.removeAll(enumeration)) {
      eAtts2Delete.addAll(findAllConnectedEAttributes(property));
      propertiesToDelete.add(property);
    }
    for (EnumerationPropertyExpression property : propertiesToDelete) {
      ((EMFUniversalType) property.getHolder()).removeFeature(property);
    }
    for (EAttribute eAtt : eAtts2Delete) {
      eAtt.getEContainingClass().getEStructuralFeatures().remove(eAtt);
      this.properties.remove(eAtt);
    }
    for (EEnumLiteral eEnumLiteral : eEnum.getELiterals()) {
      this.literals.inverse().remove(eEnumLiteral);
    }
    this.wrapped.getEClassifiers().remove(eEnum);
  }

  /**{@inheritDoc}**/
  public final void deleteEnumerationLiteral(EnumerationLiteralExpression literal) {
    List<EEnumLiteral> eEnumLiterals2Delete = Lists.newArrayList();
    for (Entry<EEnumLiteral, BiMap<EEnum, EnumerationLiteralExpression>> entry : this.literals.entrySet()) {
      Entry<EEnum, EnumerationLiteralExpression> eEnum2Literal = entry.getValue().entrySet().iterator().next();
      if (literal.equals(eEnum2Literal.getValue())) {
        eEnumLiterals2Delete.add(entry.getKey());
      }
    }
    if (eEnumLiterals2Delete.isEmpty()) {
      return;
    }
    for (EEnumLiteral eEnumLiteral : eEnumLiterals2Delete) {
      eEnumLiteral.getEEnum().getELiterals().remove(eEnumLiteral);
    }
    if (literal instanceof EMFEnumeration) {
      ((EMFEnumeration) literal).removeLiteral(literal);
    }
  }

  /**{@inheritDoc}**/
  public final void deleteProperty(PropertyExpression<?> property) {
    canDeleteFeature(property);
    Set<EAttribute> eAtts2Delete = findAllConnectedEAttributes(property);
    if (eAtts2Delete.isEmpty()) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot delete non-canonic property.");
    }
    ((EMFUniversalType) property.getHolder()).removeFeature(property);
    if (property instanceof EnumerationPropertyExpression) {
      EnumerationExpression enumeration = this.enumerations.get(eAtts2Delete.iterator().next().getEType());
      this.enumeration2properties.remove(enumeration, property);
    }
    else if (property instanceof PrimitivePropertyExpression) {
      PrimitiveTypeExpression primitiveType = this.primitiveTypes.get(eAtts2Delete.iterator().next().getEType());
      this.primitiveType2properties.remove(primitiveType, property);
    }
    for (EAttribute toDelete : eAtts2Delete) {
      this.properties.remove(toDelete);
      toDelete.getEContainingClass().getEStructuralFeatures().remove(toDelete);
    }
  }

  /**{@inheritDoc}**/
  public final void deleteRelationship(RelationshipExpression relationship) {
    for (RelationshipEndExpression relEnd : relationship.getRelationshipEnds()) {
      canDeleteFeature(relEnd);
    }
    if (!this.relationship2relationshipEnds.containsKey(relationship)) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot delete non-canonic relationship.");
    }
    for (RelationshipEndExpression relationshipEnd : this.relationship2relationshipEnds.removeAll(relationship)) {
      if (EMFRelationshipEnd.class.isInstance(relationshipEnd)) {
        ((EMFUniversalType) relationshipEnd.getHolder()).removeFeature(relationshipEnd);
      }
      Set<EReference> eRefs2Delete = findAllConnectedEReferences(relationshipEnd);
      for (EReference eReference : eRefs2Delete) {
        eReference.getEContainingClass().getEStructuralFeatures().remove(eReference);
        this.relationshipEnds.remove(eReference);
      }
      if (EMFRelationshipEnd.class.isInstance(relationshipEnd)) {
        ((EMFRelationshipEnd) relationshipEnd).deleteObserver(relationshipEnd.getType());
      }
    }
  }

  /**{@inheritDoc}**/
  public final void deleteRelationshipType(RelationshipTypeExpression relationshipType) {
    for (RelationshipEndExpression relEnd : relationshipType.getRelationshipEnds()) {
      deleteRelationship(relEnd.getRelationship());
    }
    EClass toDelete = this.universalTypes.inverse().remove(relationshipType);
    if (toDelete == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot delete non-canonic relationship type.");
    }
    for (EAttribute att2delete : toDelete.getEAttributes()) {
      this.properties.inverse().remove(att2delete);
    }
    for (EReference ref2delete : toDelete.getEReferences()) {
      this.relationshipEnds.inverse().remove(ref2delete);
      EReference oppositeRef2delete = ref2delete.getEOpposite();
      if (oppositeRef2delete != null) {
        oppositeRef2delete.getEContainingClass().getEStructuralFeatures().remove(oppositeRef2delete);
        this.relationshipEnds.inverse().remove(oppositeRef2delete);
      }
    }
    this.wrapped.getEClassifiers().remove(toDelete);
    this.types.remove(relationshipType);
  }

  /**{@inheritDoc}**/
  public final void deleteSubstantialType(SubstantialTypeExpression substantialType) {
    EClass toDelete = this.universalTypes.inverse().remove(substantialType);
    if (toDelete == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot delete non-canonic substantial type.");
    }
    for (EAttribute att2delete : toDelete.getEAttributes()) {
      this.properties.inverse().remove(att2delete);
    }
    for (EReference ref2delete : toDelete.getEReferences()) {
      this.relationshipEnds.inverse().remove(ref2delete);
      EReference oppositeRef2delete = ref2delete.getEOpposite();
      if (oppositeRef2delete != null) {
        oppositeRef2delete.getEContainingClass().getEStructuralFeatures().remove(oppositeRef2delete);
        this.relationshipEnds.inverse().remove(oppositeRef2delete);
      }
    }
    this.wrapped.getEClassifiers().remove(toDelete);
    this.types.remove(substantialType);
  }

  /**
   * Searches for the {@link PropertyExpression} that is holding (wrapping) the {@link EAttribute};
   * if not present, a new {@link PropertyExpression} is being instantiated before returning it
   * @param eAttribute
   *    the {@link EAttribute} to search for
   * @return
   *    the {@link PropertyExpression} wrapping the {@link EAttribute}
   */
  public final PropertyExpression<?> encapsulate(EAttribute eAttribute) {
    if (!this.properties.containsKey(eAttribute)) {
      BiMap<EClass, PropertyExpression<?>> value = HashBiMap.create();
      if (eAttribute.getEType() instanceof EEnum) {
        value.put(eAttribute.getEContainingClass(), new EMFEnumerationProperty(eAttribute, this));
      }
      else {
        value.put(eAttribute.getEContainingClass(), new EMFPrimitiveProperty(eAttribute, this));
      }
      this.properties.put(eAttribute, value);
    }
    return filterFeature(this.properties.get(eAttribute).get(eAttribute.getEContainingClass()));
  }

  /**
   * Searches for the {@link UniversalTypeExpression} that is holding (wrapping) the {@link EClass};
   * if not present, a new {@link UniversalTypeExpression} is being instantiated before returning it
   * @param eClass
   *    the {@link EClass} to search for
   * @return
   *    the {@link UniversalTypeExpression} wrapping the {@link EClass}
   */
  public final UniversalTypeExpression encapsulate(EClass eClass) {
    if (!this.universalTypes.containsKey(eClass)) {
      if (isSubstantialType(eClass)) {
        this.universalTypes.forcePut(eClass, new EMFSubstantialType(eClass, this));
      }
      if (isRelationshipType(eClass)) {
        this.universalTypes.put(eClass, new EMFRelationshipType(eClass, this));
      }
    }
    return this.universalTypes.get(eClass);
  }

  /**
   * Searches for the {@link EnumerationExpression} that is holding (wrapping) the {@link EEnum};
   * if not present, a new {@link EnumerationExpression} is being instantiated before returning it
   * @param eEnum
   *    the {@link EEnum} to search for
   * @return
   *    the {@link EnumerationExpression} wrapping the {@link EEnum}
   */
  public final EnumerationExpression encapsulate(EEnum eEnum) {
    if (!this.enumerations.containsKey(eEnum)) {
      this.enumerations.forcePut(eEnum, new EMFEnumeration(eEnum, this));
    }
    return this.enumerations.get(eEnum);
  }

  /**
   * Searches for the {@link EnumerationLiteralExpression} that is holding (wrapping) the {@link EEnumLiteral};
   * if not present, a new {@link EnumerationLiteralExpression} is being instantiated before returning it
   * @param eEnumLiteral
   *    the {@link EEnumLiteral} to search for
   * @return
   *    the {@link EnumerationLiteralExpression} wrapping the {@link EEnumLiteral}
   */
  public final EnumerationLiteralExpression encapsulate(EEnumLiteral eEnumLiteral) {
    if (!this.literals.containsKey(eEnumLiteral)) {
      BiMap<EEnum, EnumerationLiteralExpression> value = HashBiMap.create();
      //TODO is fine? do we need to keep some extra information, or store the color data in the eEnumLiteral, e.g. as an annotation?
      value.put(eEnumLiteral.getEEnum(), new EMFEnumerationLiteral(eEnumLiteral, this, null));
      this.literals.put(eEnumLiteral, value);
    }
    return this.literals.get(eEnumLiteral).get(eEnumLiteral.getEEnum());
  }

  /**
   * Searches for the {@link RelationshipEndExpression} that is holding (wrapping) the {@link EReference};
   * if not present, a new {@link RelationshipEndExpression} is being instantiated before returning it
   * @param eReference
   *    the {@link EReference} to search for
   * @return
   *    the {@link RelationshipEndExpression} wrapping the {@link EReference}
   */
  public final RelationshipEndExpression encapsulate(EReference eReference) {
    if (!this.relationshipEnds.containsKey(eReference)) {
      BiMap<EClass, RelationshipEndExpression> value = HashBiMap.create();
      value.put(eReference.getEContainingClass(), new EMFRelationshipEnd(eReference, this));
      this.relationshipEnds.put(eReference, value);
    }
    return filterFeature(this.relationshipEnds.get(eReference).get(eReference.getEContainingClass()));
  }

  /**
   * Searches for the {@link FeatureExpression} that is holding (wrapping) the {@link EStructuralFeature};
   * if not present, a new {@link FeatureExpression} is being instantiated before returning it
   * @param eFeature
   *    the {@link EStructuralFeature} to search for
   * @return
   *    the {@link FeatureExpression} wrapping the {@link EStructuralFeature}
   */
  public final FeatureExpression<?> encapsulate(EStructuralFeature eFeature) {
    if (EReference.class.isInstance(eFeature)) {
      return encapsulate((EReference) eFeature);
    }
    else if (EAttribute.class.isInstance(eFeature)) {
      return encapsulate((EAttribute) eFeature);
    }
    else {
      return null;
    }
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    else {
      EMFMetamodel otherMetamodel = (EMFMetamodel) obj;
      return wrapped.getName().equals(otherMetamodel.wrapped.getName()) && wrapped.getNsPrefix().equals(otherMetamodel.wrapped.getNsPrefix())
          && wrapped.getNsURI().equals(otherMetamodel.wrapped.getNsURI());
    }
  }

  /**{@inheritDoc}**/
  public DataTypeExpression findDataTypeByName(String name) {
    TypeExpression result = findTypeByName(name);
    if (DataTypeExpression.class.isInstance(result)) {
      return (DataTypeExpression) result;
    }
    return null;
  }

  /**{@inheritDoc}**/
  public DataTypeExpression findDataTypeByPersistentName(String persistentName) {
    TypeExpression dataType = findTypeByPersistentName(persistentName);
    if (DataTypeExpression.class.isInstance(dataType)) {
      return (DataTypeExpression) dataType;
    }
    return null;
  }

  /**{@inheritDoc}**/
  public final TypeExpression findTypeByName(ElasticeamContext ctx, String name) {
    return findTypeByName(name);
  }

  /**{@inheritDoc}**/
  public final TypeExpression findTypeByName(String name) {
    return filterType(this.types.get(ElasticeamContextUtil.getCurrentContext().getLocale(), name));
  }

  /**{@inheritDoc}**/
  public final TypeExpression findTypeByPersistentName(String persistentName) {
    TypeExpression result = null;
    EClassifier eClassifier = this.wrapped.getEClassifier(persistentName);
    if (eClassifier instanceof EEnum) {
      result = encapsulate((EEnum) eClassifier);
    }
    else if (eClassifier instanceof EDataType) {
      //Note: This statement must be after the EEnum check, since the EEnum is a subclass of EDataType
      result = encapsulate((EDataType) eClassifier);
    }
    else if (eClassifier instanceof EClass) {
      result = encapsulate((EClass) eClassifier);
    }
    return filterType(result);
  }

  /**{@inheritDoc}**/
  public UniversalTypeExpression findUniversalTypeByName(String name) {
    TypeExpression result = findTypeByName(name);
    if (result instanceof UniversalTypeExpression) {
      return (UniversalTypeExpression) result;
    }
    else {
      return null;
    }
  }

  /**{@inheritDoc}**/
  public UniversalTypeExpression findUniversalTypeByPersistentName(String persistentName) {
    TypeExpression candidateType = findTypeByPersistentName(persistentName);
    if (candidateType instanceof UniversalTypeExpression) {
      return (UniversalTypeExpression) candidateType;
    }
    else {
      return null;
    }
  }

  public final List<DataTypeExpression> getDataTypes() {
    List<DataTypeExpression> result = Lists.newArrayList();
    result.addAll(getPrimitiveTypes());
    result.addAll(getEnumerationTypes());
    return result;
  }

  public final List<DataTypeExpression> getDataTypes(ElasticeamContext ctx) {
    return getDataTypes();
  }

  /**{@inheritDoc}**/
  public final List<EnumerationExpression> getEnumerationTypes() {
    List<EnumerationExpression> result = Lists.newArrayList();
    for (EClassifier eClassifier : getEPackage().getEClassifiers()) {
      if (eClassifier instanceof EDataType && isEnumerationType((EDataType) eClassifier)) {
        result.add(encapsulate((EEnum) eClassifier));
      }
    }
    return filterTypes(result);
  }

  /**{@inheritDoc}**/
  public final List<EnumerationExpression> getEnumerationTypes(ElasticeamContext ctx) {
    return getEnumerationTypes();
  }

  public EPackage getEPackage() {
    return this.wrapped;
  }

  /**{@inheritDoc}**/
  public final String getName() {
    return this.wrapped.getName();
  }

  public final List<PrimitiveTypeExpression> getPrimitiveTypes() {
    List<PrimitiveTypeExpression> result = Lists.newArrayList();
    for (EClassifier eClassifier : getEPackage().getEClassifiers()) {
      if (eClassifier instanceof EDataType && isPrimitiveType((EDataType) eClassifier)) {
        result.add(encapsulate((EDataType) eClassifier));
      }
    }
    return filterTypes(result);
  }

  public final List<PrimitiveTypeExpression> getPrimitiveTypes(ElasticeamContext ctx) {
    return getPrimitiveTypes();
  }

  /**{@inheritDoc}**/
  public List<RelationshipExpression> getRelationships() {
    return filterRelationships(Lists.newLinkedList(this.relationship2relationshipEnds.keySet()));
  }

  /**{@inheritDoc}**/
  public List<RelationshipExpression> getRelationships(ElasticeamContext ctx) {
    return getRelationships();
  }

  /**{@inheritDoc}**/
  public final List<RelationshipTypeExpression> getRelationshipTypes() {
    List<RelationshipTypeExpression> result = Lists.newArrayList();
    for (EClassifier eClassifier : getEPackage().getEClassifiers()) {
      if (eClassifier instanceof EClass && isRelationshipType((EClass) eClassifier)) {
        result.add((RelationshipTypeExpression) encapsulate((EClass) eClassifier));
      }
    }
    return filterTypes(result);
  }

  /**{@inheritDoc}**/
  public final List<RelationshipTypeExpression> getRelationshipTypes(ElasticeamContext ctx) {
    return getRelationshipTypes();
  }

  /**{@inheritDoc}**/
  public final List<SubstantialTypeExpression> getSubstantialTypes() {
    List<SubstantialTypeExpression> result = Lists.newArrayList();
    for (EClassifier eClassifier : getEPackage().getEClassifiers()) {
      if (eClassifier instanceof EClass && isSubstantialType((EClass) eClassifier)) {
        result.add((SubstantialTypeExpression) encapsulate((EClass) eClassifier));
      }
    }
    return filterTypes(result);
  }

  /**{@inheritDoc}**/
  public final List<SubstantialTypeExpression> getSubstantialTypes(ElasticeamContext ctx) {
    return getSubstantialTypes();
  }

  /**{@inheritDoc}**/
  public final List<TypeExpression> getTypes() {
    List<TypeExpression> result = Lists.newArrayList();
    for (EClassifier eClassifier : getEPackage().getEClassifiers()) {
      if (eClassifier instanceof EClass) {
        result.add(encapsulate((EClass) eClassifier));
      }
      else if (eClassifier instanceof EEnum) {
        result.add(encapsulate((EEnum) eClassifier));
      }
      else if (eClassifier instanceof EDataType) {
        result.add(encapsulate((EDataType) eClassifier));
      }
    }
    return filterTypes(result);
  }

  /**{@inheritDoc}**/
  public final List<TypeExpression> getTypes(ElasticeamContext ctx) {
    return getTypes();
  }

  public final List<UniversalTypeExpression> getUniversalTypes() {
    List<UniversalTypeExpression> result = Lists.newArrayList();
    result.addAll(getSubstantialTypes());
    result.addAll(getRelationshipTypes());
    return result;
  }

  public final List<UniversalTypeExpression> getUniversalTypes(ElasticeamContext ctx) {
    return getUniversalTypes();
  }

  /**{@inheritDoc}**/
  public final void grantPermission(PropertyExpression<?> on, Role to, FeaturePermissions type) {
    if (type == FeaturePermissions.READ) {
      this.readableFeatures.put(to, on);
    }
  }

  /**{@inheritDoc}**/
  public final void grantPermission(UniversalTypeExpression on, Role to, UniversalTypePermissions type) {
    if (type == UniversalTypePermissions.READ) {
      this.readableTypes.put(to, on);
    }
  }

  @Override
  public final int hashCode() {
    return (getClass().hashCode() + wrapped.getName() + wrapped.getNsURI() + wrapped.getNsPrefix()).hashCode();
  }

  public final PrimitiveTypeExpression initPrimitiveType(BuiltinPrimitiveType primitiveType) {
    if (primitiveType == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "A primitive type must have its encapsulated class specified.");
    }
    for (EDataType existing : primitiveTypes.keySet()) {
      if (primitiveType.getEncapsulatedType().equals(existing.getInstanceClass())) {
        throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Cannot create a primitive type for class "
            + primitiveType.getEncapsulatedType().getName() + ". A primitive type for this class is already defied.");
      }
    }

    EDataType dataType = EcoreFactory.eINSTANCE.createEDataType();
    dataType.setName(primitiveType.getPersistentName());
    dataType.setInstanceClass(primitiveType.getEncapsulatedType());

    annotatePrimitiveType(dataType);

    this.wrapped.getEClassifiers().add(dataType);
    this.primitiveTypes.put(dataType, primitiveType);

    return primitiveType;
  }

  /**{@inheritDoc}**/
  public void removeMixin(SubstantialTypeExpression substantialType, MixinTypeExpression mixin) {
    // TODO Auto-generated method stub

  }

  /**
   * Get the {@link EAttribute} that has been instantiated to represent the {@link PropertyExpression}
   * 
   * @param property
   *    the {@link PropertyExpression} to search for
   * @param eClass
   *    the {@link EClass} representing the porperty's holder ({@link UniversalTypeExpression})
   * @return
   *    the {@link EAttribute} representing the {@link PropertyExpression}
   */
  public final EAttribute unwrap(PropertyExpression<?> property, EClass eClass) {
    EAttribute result = null;
    for (Entry<EAttribute, BiMap<EClass, PropertyExpression<?>>> entrySet : this.properties.entrySet()) {
      Entry<EClass, PropertyExpression<?>> entry = entrySet.getValue().entrySet().iterator().next();
      if (eClass.getName().equals(entry.getKey().getName()) && property.equals(entry.getValue())) {
        result = entrySet.getKey();
        break;
      }
    }
    if (result == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Could not find mapped EAttribute for PropertyExpression '"
          + property.getPersistentName() + "' in EClass '" + eClass.getName() + "'");
    }
    return result;
  }

  /**
   * Get the {@link EReference} that has been instantiated to represent the {@link RelationshipEndExpression}
   * 
   * @param relationshipEnd
   *    the {@link RelationshipEndExpression} to search for
   * @param eClass
   *    the {@link EClass} representing the relationshipEnd's holder ({@link UniversalTypeExpression})
   * @return
   *    the {@link EReference} representing the {@link RelationshipEndExpression}
   */
  public final EReference unwrap(RelationshipEndExpression relationshipEnd, EClass eClass) {
    EReference result = null;
    for (Entry<EReference, BiMap<EClass, RelationshipEndExpression>> entrySet : this.relationshipEnds.entrySet()) {
      Entry<EClass, RelationshipEndExpression> entry = entrySet.getValue().entrySet().iterator().next();
      if (eClass.getName().equals(entry.getKey().getName()) && relationshipEnd.equals(entry.getValue())) {
        result = entrySet.getKey();
        break;
      }
    }
    if (result == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Could not find mapped EReference for RelationshipEndExpression '"
          + relationshipEnd.getPersistentName() + "' in EClass '" + eClass.getName() + "'");
    }
    return result;
  }

  /**{@inheritDoc}**/
  public final void update(Observable arg0, Object arg1) {
    if (arg0 instanceof TypeExpression && arg1 instanceof NameChangeEvent) {
      this.types.set(((NameChangeEvent) arg1).getLocale(), ((NameChangeEvent) arg1).getName(), (TypeExpression) arg0);
    }
  }

  private void addDefaultPrimitiveTypes() {
    for (PrimitiveTypeExpression builtinType : BuiltinPrimitiveType.BUILTIN_PRIMITIVE_TYPES) {
      initPrimitiveType((BuiltinPrimitiveType) builtinType);
    }
  }

  private void canDeleteFeature(FeatureExpression<?> feature) {
    if (!(feature.getHolder() != null && feature.getHolder() == feature.getOrigin())) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Can not delete builtin feature " + feature);
    }
  }

  private EMFPrimitiveProperty createIDPropertyImpl(EClass eClass) {
    EMFPrimitiveProperty idPoperty = createPropertyImpl(eClass, "id", 1, 1, getEDataType(BuiltinPrimitiveType.INTEGER.getEncapsulatedType()),
        new MixinTypeExpression[] { null });
    idPoperty.getWrapped().setID(true);
    return idPoperty;
  }

  private EMFPrimitiveProperty createPropertyImpl(EClass eClass, String persistentName, int lowerBound, int upperBound, EDataType eDataType,
                                                  MixinTypeExpression... mixin) {
    EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    eAttribute.setName(persistentName);
    eAttribute.setLowerBound(lowerBound);
    eAttribute.setUpperBound(upperBound);
    eAttribute.setEType(eDataType);
    eClass.getEStructuralFeatures().add(eAttribute);
    EMFPrimitiveProperty result = (mixin == null || mixin.length == 0) ? new EMFPrimitiveProperty(eAttribute, this) : new EMFPrimitiveProperty(
        eAttribute, mixin[0], this);
    BiMap<EClass, PropertyExpression<?>> value = HashBiMap.create();
    value.put(eClass, result);
    this.properties.put(eAttribute, value);
    this.primitiveType2properties.put(this.primitiveTypes.get(eDataType), result);
    return result;
  }

  private EMFEnumerationProperty createPropertyImpl(EClass eClass, String persistentName, int lowerBound, int upperBound, EEnum eEnum,
                                                    MixinTypeExpression... mixin) {
    EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    eAttribute.setName(persistentName);
    eAttribute.setLowerBound(lowerBound);
    eAttribute.setUpperBound(upperBound == FeatureExpression.UNLIMITED ? EStructuralFeature.UNBOUNDED_MULTIPLICITY : upperBound);
    eAttribute.setEType(eEnum);
    eClass.getEStructuralFeatures().add(eAttribute);
    EMFEnumerationProperty result = (mixin == null || mixin.length == 0) ? new EMFEnumerationProperty(eAttribute, this) : new EMFEnumerationProperty(
        eAttribute, mixin[0], this);
    BiMap<EClass, PropertyExpression<?>> value = HashBiMap.create();
    value.put(eClass, result);
    this.properties.put(eAttribute, value);
    this.enumeration2properties.put(this.enumerations.get(eEnum), result);

    return result;
  }

  private <T extends TypeExpression> T filterType(T type) {
    if (disableAccessControl) {
      return type;
    }
    if (ElasticeamContextUtil.getCurrentContext().isSupervisor()) {
      return type;
    }
    if (type instanceof DataTypeExpression) {
      return type;
    }

    for (Role role : ElasticeamContextUtil.getCurrentContext().getRoles()) {
      if (this.readableTypes.get(role).contains(type)) {
        if (type instanceof EMFRelationshipType) {
          EMFRelationshipType rte = (EMFRelationshipType) type;
          for (EReference eRef : rte.getWrapped().getEAllReferences()) {
            if (eRef.getLowerBound() > 0 && rte.findRelationshipEndByPersistentName(eRef.getName()) == null) {
              return null;
            }
          }
        }
        return type;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private <T extends TypeExpression> List<T> filterTypes(List<T> candidateTypes) {
    if (disableAccessControl) {
      return candidateTypes;
    }
    if (ElasticeamContextUtil.getCurrentContext().isSupervisor()) {
      return candidateTypes;
    }
    Set<T> result = Sets.newLinkedHashSet();
    for (Role role : ElasticeamContextUtil.getCurrentContext().getRoles()) {
      if (this.readableTypes.containsKey(role)) {
        result.addAll(ListUtils.intersection(this.readableTypes.get(role), candidateTypes));
      }
    }
    for (T type : candidateTypes) {
      if (type instanceof DataTypeExpression) {
        result.add(type);
      }
    }
    Set<RelationshipTypeExpression> invisibleRTEs = Sets.newHashSet();
    for (T type : result) {
      if (type instanceof EMFRelationshipType) {
        EMFRelationshipType rte = (EMFRelationshipType) type;
        for (EReference eRef : rte.getWrapped().getEAllReferences()) {
          if (eRef.getLowerBound() > 0 && rte.findRelationshipEndByPersistentName(eRef.getName()) == null) {
            // required relEnd is invisible for current user => rte is invisible, too
            invisibleRTEs.add(rte);
          }
        }
      }
    }
    result.removeAll(invisibleRTEs);

    return Lists.newArrayList(result);
  }

  /**
   * returns a {@link Set} of {@link EAttribute}s that are wrapped by {@link PropertyExpression}s 
   * that are equal to the provided {@link PropertyExpression}
   */
  private Set<EAttribute> findAllConnectedEAttributes(PropertyExpression<?> property) {
    Set<EAttribute> eAttributes = Sets.newHashSet();
    for (Entry<EAttribute, BiMap<EClass, PropertyExpression<?>>> entrySet : this.properties.entrySet()) {
      Entry<EClass, PropertyExpression<?>> entry = entrySet.getValue().entrySet().iterator().next();
      if (property.equals(entry.getValue())) {
        eAttributes.add(entrySet.getKey());
      }
    }
    return eAttributes;
  }

  /**
   * returns a {@link Set} of {@link EReference}s that are wrapped by {@link RelationshipEndExpression}s 
   * that are equal to the provided {@link RelationshipEndExpression}
   */
  private Set<EReference> findAllConnectedEReferences(RelationshipEndExpression relationshipEnd) {
    Set<EReference> eReferences = Sets.newHashSet();
    for (Entry<EReference, BiMap<EClass, RelationshipEndExpression>> entrySet : this.relationshipEnds.entrySet()) {
      Entry<EClass, RelationshipEndExpression> entry = entrySet.getValue().entrySet().iterator().next();
      if (relationshipEnd.equals(entry.getValue())) {
        eReferences.add(entrySet.getKey());
      }
    }
    return eReferences;
  }

  private EDataType getEDataType(Class<?> type) {
    EDataType eDataType = null;
    for (EClassifier eClassifier : EcorePackage.eINSTANCE.getEClassifiers()) {
      if (eClassifier instanceof EDataType && type.equals(((EDataType) eClassifier).getInstanceClass())) {
        eDataType = (EDataType) eClassifier;
      }
    }
    for (EClassifier eClassifier : this.wrapped.getEClassifiers()) {
      if (eClassifier instanceof EDataType && type.equals(((EDataType) eClassifier).getInstanceClass())) {
        eDataType = (EDataType) eClassifier;
      }
    }
    return eDataType;
  }

  /**
   * Searches for the {@link PrimitiveTypeExpression} that is holding (wrapping) the {@link EDataType};
   * if not present, a new {@link PrimitiveTypeExpression} is being instantiated before returning it
   * @param eDataType
   *    the {@link EDataType} to search for
   * @return
   *    the {@link PrimitiveTypeExpression} wrapping the {@link EDataType}
   */
  final PrimitiveTypeExpression encapsulate(EDataType eDataType) {
    if (!this.primitiveTypes.containsKey(eDataType)) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Missing primitive type for eDataType " + eDataType);
    }
    return this.primitiveTypes.get(eDataType);
  }

  /**
   * Get the {@link RelationshipExpression} that is connected to the {@link RelationshipEndExpression}
   * 
   * @param relationshipEnd
   *    the {@link RelationshipEndExpression} to check
   * @return
   *    the {@link RelationshipExpression} that is connected to the {@link RelationshipEndExpression}
   */
  final RelationshipExpression getRelationship(RelationshipEndExpression relationshipEnd) {
    for (Entry<RelationshipExpression, RelationshipEndExpression> entry : relationship2relationshipEnds.entries()) {
      if (entry.getValue().equals(relationshipEnd)) {
        return entry.getKey();
      }
    }
    return null;
  }

  final List<RelationshipExpression> filterRelationships(List<RelationshipExpression> relationships) {
    if (disableAccessControl) {
      return relationships;
    }
    if (ElasticeamContextUtil.getCurrentContext().isSupervisor()) {
      return relationships;
    }
    List<RelationshipExpression> result = Lists.newArrayList();
    for (RelationshipExpression rel : relationships) {
      if (filterRelationship(rel) != null) {
        result.add(rel);
      }
    }
    return result;
  }

  final RelationshipExpression filterRelationship(RelationshipExpression rel) {
    List<RelationshipEndExpression> relEnds = rel.getRelationshipEnds();
    if (relEnds.size() == filterFeatures(relEnds).size()) {
      return rel;
    }
    return null;
  }

  final <F extends FeatureExpression<?>> List<F> filterFeatures(List<F> features) {
    if (disableAccessControl) {
      return features;
    }
    if (ElasticeamContextUtil.getCurrentContext().isSupervisor()) {
      return features;
    }
    Set<F> result = Sets.newHashSet();
    // FIXME change once we have feature-level permissions
    for (F feature : features) {
      if (feature instanceof PropertyExpression && filterType(feature.getHolder()) != null) {
        result.add(feature);
      }
    }
    //    for (Role role : ElasticeamContextUtil.getCurrentContext().getRoles()) {
    //      if (this.readableFeatures.containsKey(role)) {
    //        result.addAll(ListUtils.intersection(this.readableFeatures.get(role), features));
    //      }
    //    }
    for (F feature : features) {
      if (feature instanceof RelationshipEndExpression && filterType(((RelationshipEndExpression) feature).getType()) != null) {
        result.add(feature);
      }
    }
    return Lists.newArrayList(result);
  }

  final <F extends FeatureExpression<?>> F filterFeature(F feature) {
    if (disableAccessControl) {
      return feature;
    }
    if (ElasticeamContextUtil.getCurrentContext().isSupervisor()) {
      return feature;
    }
    if (feature instanceof RelationshipEndExpression && filterType(((RelationshipEndExpression) feature).getType()) != null) {
      return feature;
    }
    // FIXME change once we have feature-level permissions
    if (feature instanceof PropertyExpression && filterType(feature.getHolder()) != null) {
      return feature;
    }
    //    for (Role role : ElasticeamContextUtil.getCurrentContext().getRoles()) {
    //      if (this.readableFeatures.get(role).contains(feature)) {
    //        return feature;
    //      }
    //    }
    return null;
  }

}
