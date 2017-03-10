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

import java.util.Date;

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
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.model.RuntimePeriod;


public abstract class Mapping<T extends Metamodel> {
  public static final String                              METAMODELANNOTATIONURI        = "urn:metamodelAnnotation";
  public static final String                              DESCRIPTIONKEY                = "description";
  public static final String                              ABBREVIATIONKEY               = "abbreviation";
  public static final String                              UNIVERSALTYPEDISCRIMINATOR    = "type";
  public static final String                              ESCAPEDNAMEKEY                = "name";
  public static final String                              RELATIONSHIPTYPENAME          = "relationshipName";

  public static final String                              RELATIONSHIPTYPEDISCRIMINATOR = RelationshipTypeExpression.class.getSimpleName()
                                                                                            .replaceAll("Expression", "");
  private final T                                         metamodel;
  protected EPackage                                      ePackage;
  private final BiMap<EClass, SubstantialTypeExpression>  substantialTypes              = HashBiMap.create();
  private final BiMap<EClass, RelationshipTypeExpression> relationshipTypes             = HashBiMap.create();
  private final BiMap<EEnum, EnumerationExpression>       enumerations                  = HashBiMap.create();
  private final BiMap<EDataType, PrimitiveTypeExpression> primitiveTypes                = HashBiMap.create();

  private final boolean                                   useLocalNames;

  protected Mapping(T metamodel, boolean useLocalNames) {
    this.metamodel = metamodel;
    this.useLocalNames = useLocalNames;
    this.ePackage = init(metamodel);
    this.ePackage.setEFactoryInstance(new IteraplanFactory());
  }

  protected Mapping(EPackage ePackage, boolean useLocalNames) {
    this.ePackage = ePackage;
    this.useLocalNames = useLocalNames;
    this.ePackage.setEFactoryInstance(new IteraplanFactory());
    this.metamodel = init(ePackage);
  }

  protected abstract EPackage init(T theMetamodel);

  protected abstract T init(EPackage theEPackage);

  protected final void add(EClass eClass, UniversalTypeExpression universalType) {
    if (universalType instanceof SubstantialTypeExpression) {
      this.substantialTypes.put(eClass, (SubstantialTypeExpression) universalType);
    }
    else if (universalType instanceof RelationshipTypeExpression) {
      this.relationshipTypes.put(eClass, (RelationshipTypeExpression) universalType);
    }
  }

  protected final void add(UniversalTypeExpression universalType, EClass eClass) {
    if (universalType instanceof SubstantialTypeExpression) {
      this.substantialTypes.put(eClass, (SubstantialTypeExpression) universalType);
    }
    else if (universalType instanceof RelationshipTypeExpression) {
      this.relationshipTypes.put(eClass, (RelationshipTypeExpression) universalType);
    }
  }

  protected final void add(EEnum eEnum, EnumerationExpression enumeration) {
    this.enumerations.put(eEnum, enumeration);
  }

  protected final void add(EnumerationExpression enumeration, EEnum eEnum) {
    this.enumerations.put(eEnum, enumeration);
  }

  public final UniversalTypeExpression getUniversalType(EClass eClass) {
    if (this.substantialTypes.containsKey(eClass)) {
      return this.substantialTypes.get(eClass);
    }
    return this.relationshipTypes.get(eClass);
  }

  public final EClass getEClass(UniversalTypeExpression universalType) {
    if (this.substantialTypes.inverse().containsKey(universalType)) {
      return this.substantialTypes.inverse().get(universalType);
    }
    return this.relationshipTypes.inverse().get(universalType);
  }

  public final SubstantialTypeExpression getSubstantialType(EClass eClass) {
    return this.substantialTypes.get(eClass);
  }

  public final EClass getEClass(SubstantialTypeExpression substantialType) {
    return this.substantialTypes.inverse().get(substantialType);
  }

  public final RelationshipTypeExpression getRelationshipType(EClass eClass) {
    return this.relationshipTypes.get(eClass);
  }

  public final EClass getEClass(RelationshipTypeExpression relationshipType) {
    return this.relationshipTypes.inverse().get(relationshipType);
  }

  public final EnumerationExpression getEnumeration(EEnum eEnum) {
    return this.enumerations.get(eEnum);
  }

  public final EEnum getEEnum(EnumerationExpression enumeration) {
    return this.enumerations.inverse().get(enumeration);
  }

  protected final PrimitiveTypeExpression getPrimitiveType(EDataType dataType) {
    return primitiveTypes.get(dataType);
  }

  protected final void add(EDataType dataType, PrimitiveTypeExpression primitiveType) {
    this.primitiveTypes.put(dataType, primitiveType);
  }

  protected final BiMap<EDataType, PrimitiveTypeExpression> getPrimitiveTypes() {
    return primitiveTypes;
  }

  private final String getName(NamedExpression namedExpression) {
    return this.useLocalNames ? namedExpression.getName() : namedExpression.getPersistentName();
  }

  public final EnumerationLiteralExpression getLiteral(EEnum eEnum, EEnumLiteral value) {
    if (this.enumerations.containsKey(eEnum)) {
      EnumerationExpression enumeration = this.enumerations.get(eEnum);
      for (EnumerationLiteralExpression literal : enumeration.getLiterals()) {
        if (this.useLocalNames) {
          if (literal.getName().equals(value.getName())) {
            return literal;
          }
        }
        else {
          if (literal.getPersistentName().equals(value.getName())) {
            return literal;
          }
        }
      }
      return null;
    }
    else {
      return null;
    }
  }

  public final EAttribute getEAttribute(UniversalTypeExpression universalType, PropertyExpression<?> property) {
    if (this.substantialTypes.inverse().containsKey(universalType)) {
      EClass eClass = this.substantialTypes.inverse().get(universalType);
      EStructuralFeature feature = eClass.getEStructuralFeature(getName(property));
      if (feature instanceof EAttribute) {
        return (EAttribute) feature;
      }
    }
    if (this.relationshipTypes.inverse().containsKey(universalType)) {
      EClass eClass = this.relationshipTypes.inverse().get(universalType);
      EStructuralFeature feature = eClass.getEStructuralFeature(getName(property));
      if (feature instanceof EAttribute) {
        return (EAttribute) feature;
      }
    }
    return null;
  }

  public final PropertyExpression<?> getProperty(EClass eClass, EAttribute eAttribute) {
    if (this.substantialTypes.containsKey(eClass)) {
      if (this.useLocalNames) {
        return this.substantialTypes.get(eClass).findPropertyByName(eAttribute.getName());
      }
      else {
        return this.substantialTypes.get(eClass).findPropertyByPersistentName(eAttribute.getName());
      }
    }
    return null;
  }

  public final EReference getEReference(UniversalTypeExpression universalType, RelationshipEndExpression relationshipEnd) {
    if (this.substantialTypes.inverse().containsKey(universalType)) {
      EClass eClass = this.substantialTypes.inverse().get(universalType);
      EStructuralFeature feature = eClass.getEStructuralFeature(getName(relationshipEnd));
      if (feature instanceof EReference) {
        return (EReference) feature;
      }
    }
    if (this.relationshipTypes.inverse().containsKey(universalType)) {
      EClass eClass = this.relationshipTypes.inverse().get(universalType);
      EStructuralFeature feature = eClass.getEStructuralFeature(getName(relationshipEnd));
      if (feature instanceof EReference) {
        return (EReference) feature;
      }
    }
    return null;
  }

  protected static void annotateEReference(EReference eReference, String relationshipTypePersistentName) {
    EcoreUtil.setAnnotation(eReference, METAMODELANNOTATIONURI, RELATIONSHIPTYPENAME, relationshipTypePersistentName);
  }

  protected static void annotateEClass(EClass eClass, UniversalTypeExpression universalType) {
    if (RelationshipTypeExpression.class.isInstance(universalType)) {
      EcoreUtil.setAnnotation(eClass, METAMODELANNOTATIONURI, UNIVERSALTYPEDISCRIMINATOR, RELATIONSHIPTYPEDISCRIMINATOR);
    }
  }

  protected static String getDescription(ENamedElement eNamedElement) {
    return EcoreUtil.getAnnotation(eNamedElement, METAMODELANNOTATIONURI, DESCRIPTIONKEY);
  }

  protected static String getAbbreviation(ENamedElement eNamedElement) {
    return EcoreUtil.getAnnotation(eNamedElement, METAMODELANNOTATIONURI, ABBREVIATIONKEY);
  }

  protected static String getUniversalTypeDiscriminator(ENamedElement eNamedElement) {
    return EcoreUtil.getAnnotation(eNamedElement, METAMODELANNOTATIONURI, UNIVERSALTYPEDISCRIMINATOR);
  }

  protected void setName(ENamedElement eNamedElement, String name, String persistentName) {
    eNamedElement.setName(this.useLocalNames ? name : persistentName);
    EcoreUtil.setAnnotation(eNamedElement, ExtendedMetaData.ANNOTATION_URI, ESCAPEDNAMEKEY, escape(persistentName));
  }

  private static String escape(String persistentName) {
    StringBuffer escapedName = new StringBuffer();
    boolean isFirst = true;
    for (int i = 0; i < persistentName.length(); i++) {
      char c = persistentName.charAt(i);
      if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z')) {
        escapedName.append(c);
      }
      else if (isFirst) {
        addEscaped(c, escapedName);
      }
      else if (c == '-' || c == '.' || ('0' <= c && c <= '9')) {
        escapedName.append(c);
      }
      else {
        addEscaped(c, escapedName);
      }
      isFirst = false;
    }
    return escapedName.toString();
  }

  private static void addEscaped(char c, StringBuffer buffer) {
    buffer.append('_');
    buffer.append((int) c);
    buffer.append('_');
  }

  protected static String getPersistentName(ENamedElement eNamedElement) {
    String escapedName = EcoreUtil.getAnnotation(eNamedElement, ExtendedMetaData.ANNOTATION_URI, ESCAPEDNAMEKEY);
    if (escapedName == null) {
      return null;
    }
    String[] fragments = startsEscaped(escapedName) ? escapedName.substring(1).split("_") : escapedName.split("_");
    boolean stringMode = !startsEscaped(escapedName);
    StringBuffer name = new StringBuffer();
    for (String fragment : fragments) {
      if (stringMode) {
        name.append(fragment);
      }
      else {
        name.append((char) Integer.parseInt(fragment));
      }
      stringMode = !stringMode;
    }
    return name.toString();
  }

  protected static boolean startsEscaped(String string) {
    if (string == null || string.isEmpty()) {
      return false;
    }
    return string.charAt(0) == '_';
  }

  protected static String getRelationshipTypeName(EReference eReference) {
    return EcoreUtil.getAnnotation(eReference, METAMODELANNOTATIONURI, RELATIONSHIPTYPENAME);
  }

  protected static boolean isRelationshipType(EClassifier eClass) {
    return RELATIONSHIPTYPEDISCRIMINATOR.equals(getUniversalTypeDiscriminator(eClass));
  }

  public T getMetamodel() {
    return metamodel;
  }

  public final EPackage getEPackage() {
    return ePackage;
  }

  private static class IteraplanFactory extends EFactoryImpl {
    /**{@inheritDoc}**/
    @Override
    public Object createFromString(EDataType eDataType, String stringValue) {
      if (RuntimePeriod.class.equals(eDataType.getInstanceClass())) {
        String[] tmp = stringValue.split("~");
        if (tmp.length == 0) {
          return new RuntimePeriod(null, null);
        }
        else if (tmp.length == 1) {
          return new RuntimePeriod(parseDate(tmp[0]), null);
        }
        else {
          return new RuntimePeriod(parseDate(tmp[0]), parseDate(tmp[1]));
        }
      }
      else {
        return super.createFromString(eDataType, stringValue);
      }
    }

    private static Date parseDate(String longString) {
      if (longString == null || "".equals(longString)) {
        return null;
      }
      else {
        return new Date(Long.parseLong(longString));
      }
    }

    /**{@inheritDoc}**/
    @Override
    public String convertToString(EDataType eDataType, Object objectValue) {
      if (RuntimePeriod.class.equals(eDataType.getInstanceClass())) {
        RuntimePeriod rp = (RuntimePeriod) objectValue;
        return (rp.getStart() == null ? "" : Long.toString(rp.getStart().getTime())) + "~"
            + (rp.getEnd() == null ? "" : Long.toString(rp.getEnd().getTime()));
      }
      else {
        return super.convertToString(eDataType, objectValue);
      }
    }
  }
}
