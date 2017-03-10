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
package de.iteratec.elasticeam;

import java.math.BigDecimal;

import de.iteratec.iteraplan.elasticeam.emfimpl.EMFMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;


public final class MetamodelCreator {

  public static String[] getSubstantialTypeNames() {
    return new String[] { "IS", "BP", "BU" };
  }

  public static String[] getEnumerationNames() {
    return new String[] { "healthE", "responsibilityE" };
  }

  public static String[] getEnumerationLiteralValues() {
    return new String[] { "good", "medium", "bad" };
  }

  private EMFMetamodel metamodel;

  public Metamodel getMetamodel() {
    return this.metamodel;
  }

  public EMFMetamodel getMetamodelImpl() {
    return this.metamodel;
  }

  public MetamodelCreator() {
    this(false);
  }

  public MetamodelCreator(boolean disableAccessControl) {
    this.metamodel = new EMFMetamodel("testMetamodel", disableAccessControl);
    createEnumerations();
    createTypesAndProperties();
    createRelationships();
  }

  private void createEnumerations() {
    for (String enumerationName : getEnumerationNames()) {
      EnumerationExpression enumeration = this.metamodel.createEnumeration(enumerationName);
      for (String enumerationLiteralValue : getEnumerationLiteralValues()) {
        this.metamodel.createEnumerationLiteral(enumeration, enumerationLiteralValue, null).setName(enumerationLiteralValue);
      }
      enumeration.setName(enumerationName);
    }
  }

  private void createTypesAndProperties() {
    for (String name : getSubstantialTypeNames()) {
      SubstantialTypeExpression substantialType = this.metamodel.createSubstantialType(name);
      substantialType.setName(name);
      addProperties(substantialType);
      addHierarchy(substantialType);
    }
  }

  private void addProperties(SubstantialTypeExpression type) {
    addResponsibility(type);
    addCosts(type);

    if (type.getPersistentName().equals(getSubstantialTypeNames()[0])) {
      addVersion(type);
      addStartDate(type);
      addHealth(type);
    }
  }

  private void addHierarchy(SubstantialTypeExpression type) {
    this.metamodel.createRelationship("Hierarchy", type, "parent", 0, 1, type, "children", 0, FeatureExpression.UNLIMITED);
  }

  private void createRelationships() {
    // create the business mapping (between IS, BP and BU), all are related
    // to the mapping with a 1 to * cardinality.
    SubstantialTypeExpression isType = (SubstantialTypeExpression) metamodel.findTypeByPersistentName("IS");
    SubstantialTypeExpression bpType = (SubstantialTypeExpression) metamodel.findTypeByPersistentName("BP");
    SubstantialTypeExpression buType = (SubstantialTypeExpression) metamodel.findTypeByPersistentName("BU");
    RelationshipTypeExpression bmRelation = metamodel.createRelationshipType("BM");
    addVersion(bmRelation);
    addResponsibility(bmRelation);
    addName(bmRelation);

    this.metamodel.createRelationship("isBM", isType, "business mapping", 0, FeatureExpression.UNLIMITED, bmRelation, "information system", 1, 1);
    this.metamodel.createRelationship("bpBM", bpType, "business mapping", 0, FeatureExpression.UNLIMITED, bmRelation, "business process", 1, 1);
    this.metamodel.createRelationship("buBM", buType, "business mapping", 0, FeatureExpression.UNLIMITED, bmRelation, "business unit", 1, 1);
  }

  protected void addName(UniversalTypeExpression type) {
    this.metamodel.createProperty(type, "name", 0, 1, BuiltinPrimitiveType.STRING).setName("name");
  }

  protected void addVersion(UniversalTypeExpression type) {
    this.metamodel.createProperty(type, "version", 0, 1, BuiltinPrimitiveType.INTEGER).setName("version");
  }

  protected void addStartDate(UniversalTypeExpression type) {
    this.metamodel.createProperty(type, "startDate", 0, 1, BuiltinPrimitiveType.DATE).setName("startDate");
  }

  protected void addCosts(UniversalTypeExpression type) {
    PrimitiveTypeExpression decimalType = (PrimitiveTypeExpression) getMetamodel().findTypeByPersistentName(BigDecimal.class.getName());
    this.metamodel.createProperty(type, "costs", 1, 1, decimalType).setName("costs");
  }

  protected void addHealth(UniversalTypeExpression type) {
    EnumerationExpression healthEnum = (EnumerationExpression) metamodel.findTypeByPersistentName("healthE");
    this.metamodel.createProperty(type, "health", 0, 1, healthEnum).setName("health");
  }

  protected void addResponsibility(UniversalTypeExpression type) {
    EnumerationExpression respEnum = (EnumerationExpression) metamodel.findTypeByPersistentName("responsibilityE");
    this.metamodel.createProperty(type, "responsibility", 0, Integer.MAX_VALUE, respEnum).setName("responsibility");
  }
}
