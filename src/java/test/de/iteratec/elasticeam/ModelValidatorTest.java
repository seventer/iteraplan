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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.validator.CardinalityConstraintViolation;
import de.iteratec.iteraplan.elasticeam.model.validator.CycleOverAcyclicRelationshipConstraintViolation;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelValidator;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelValidatorResult;
import de.iteratec.iteraplan.elasticeam.model.validator.UniquenessConstraintViolation;
import de.iteratec.iteraplan.elasticeam.model.validator.UniversalTypeExpressionNameConstraintViolation;


public class ModelValidatorTest {

  private MetamodelCreator metamodelCreator;

  @Before
  public void setUp() {
    this.metamodelCreator = new MetamodelCreator();
  }

  private Metamodel getMetamodel() {
    return this.metamodelCreator.getMetamodel();
  }

  private Model getModel() {
    return ModelCreator.createModel(getMetamodel());
  }

  private ModelValidatorResult validateModel(Metamodel metamodel, Model model) {
    ModelValidator modelValidator = new ModelValidator(metamodel);
    return modelValidator.validate(model);
  }

  @Test
  public void testBaseModel() {
    ModelValidatorResult result = validateModel(getMetamodel(), getModel());
    assertEquals(0, result.getViolations().size());
  }

  @Test
  public void testDuplicateId() {
    Metamodel metamodel = getMetamodel();
    SubstantialTypeExpression isType = (SubstantialTypeExpression) metamodel.findUniversalTypeByPersistentName("IS");
    Model model = getModel();

    InstanceExpression is1 = model.findByName(isType, "is1");
    InstanceExpression is2 = model.findByName(isType, "is2");
    is2.setValue(UniversalTypeExpression.ID_PROPERTY, is1.getValue(UniversalTypeExpression.ID_PROPERTY));

    ModelValidatorResult result = validateModel(metamodel, model);

    assertEquals(1, result.getViolations().size());
    assertTrue(UniquenessConstraintViolation.class.isInstance(result.getViolations().iterator().next()));

    UniquenessConstraintViolation violation = (UniquenessConstraintViolation) result.getViolations().iterator().next();
    assertEquals(UniversalTypeExpression.ID_PROPERTY, violation.getFeature());
    assertEquals(2, violation.getViolatingExpressions().size());

    Set<String> violatingInstanceNames = Sets.newHashSet();
    for (UniversalModelExpression violatingExpression : violation.getViolatingExpressions()) {
      violatingInstanceNames.add((String) violatingExpression.getValue(MixinTypeNamed.NAME_PROPERTY));
    }
    assertTrue(violatingInstanceNames.contains("is1"));
    assertTrue(violatingInstanceNames.contains("is2"));

    assertEquals(is1.getValue(UniversalTypeExpression.ID_PROPERTY), violation.getViolatingValue());
  }

  @Test
  public void testMissingId() {
    Metamodel metamodel = getMetamodel();
    SubstantialTypeExpression isType = (SubstantialTypeExpression) metamodel.findUniversalTypeByPersistentName("IS");
    Model model = getModel();

    InstanceExpression is1 = model.findByName(isType, "is1");
    is1.setValue(UniversalTypeExpression.ID_PROPERTY, null);

    ModelValidatorResult result = validateModel(metamodel, model);

    assertEquals(1, result.getViolations().size());
    assertTrue(CardinalityConstraintViolation.class.isInstance(result.getViolations().iterator().next()));

    CardinalityConstraintViolation violation = (CardinalityConstraintViolation) result.getViolations().iterator().next();

    assertEquals(UniversalTypeExpression.ID_PROPERTY, violation.getFeature());
    assertEquals(is1.getValue(MixinTypeNamed.NAME_PROPERTY), violation.getViolatingExpression().getValue(MixinTypeNamed.NAME_PROPERTY));
  }

  @Test
  public void testDuplicateName() {
    Metamodel metamodel = getMetamodel();
    SubstantialTypeExpression isType = (SubstantialTypeExpression) metamodel.findUniversalTypeByPersistentName("IS");
    Model model = getModel();

    InstanceExpression is1 = model.findByName(isType, "is1");
    InstanceExpression is2 = model.findByName(isType, "is2");
    is2.setValue(MixinTypeNamed.NAME_PROPERTY, "is1");

    ModelValidatorResult result = validateModel(metamodel, model);

    assertEquals(1, result.getViolations().size());
    assertTrue(UniquenessConstraintViolation.class.isInstance(result.getViolations().iterator().next()));

    UniquenessConstraintViolation violation = (UniquenessConstraintViolation) result.getViolations().iterator().next();
    assertEquals(MixinTypeNamed.NAME_PROPERTY, violation.getFeature());
    assertEquals(2, violation.getViolatingExpressions().size());

    Set<BigInteger> violatingInstanceIds = Sets.newHashSet();
    for (UniversalModelExpression violatingExpression : violation.getViolatingExpressions()) {
      violatingInstanceIds.add((BigInteger) violatingExpression.getValue(UniversalTypeExpression.ID_PROPERTY));
    }
    assertTrue(violatingInstanceIds.contains(is1.getValue(UniversalTypeExpression.ID_PROPERTY)));
    assertTrue(violatingInstanceIds.contains(is2.getValue(UniversalTypeExpression.ID_PROPERTY)));

    assertEquals(is1.getValue(MixinTypeNamed.NAME_PROPERTY), violation.getViolatingValue());
  }

  @Test
  public void testMissingName() {
    Metamodel metamodel = getMetamodel();
    SubstantialTypeExpression isType = (SubstantialTypeExpression) metamodel.findUniversalTypeByPersistentName("IS");
    Model model = getModel();

    InstanceExpression is1 = model.findByName(isType, "is1");
    is1.setValue(MixinTypeNamed.NAME_PROPERTY, null);

    ModelValidatorResult result = validateModel(metamodel, model);

    assertEquals(1, result.getViolations().size());
    assertTrue(CardinalityConstraintViolation.class.isInstance(result.getViolations().iterator().next()));

    CardinalityConstraintViolation violation = (CardinalityConstraintViolation) result.getViolations().iterator().next();

    assertEquals(MixinTypeNamed.NAME_PROPERTY, violation.getFeature());
    assertEquals(is1.getValue(UniversalTypeExpression.ID_PROPERTY), violation.getViolatingExpression().getValue(UniversalTypeExpression.ID_PROPERTY));
  }

  @Test
  public void testCyclicHierarchy() {
    Metamodel metamodel = getMetamodel();
    SubstantialTypeExpression isType = (SubstantialTypeExpression) metamodel.findUniversalTypeByPersistentName("IS");
    RelationshipEndExpression children = isType.findRelationshipEndByPersistentName("children");
    Model model = getModel();

    InstanceExpression is1 = model.findByName(isType, "is1");
    InstanceExpression is4 = model.findByName(isType, "is4");

    model.link(is4, children, is1);

    ModelValidatorResult result = validateModel(metamodel, model);

    assertEquals(1, result.getViolations().size());
    assertTrue(CycleOverAcyclicRelationshipConstraintViolation.class.isInstance(result.getViolations().iterator().next()));

    CycleOverAcyclicRelationshipConstraintViolation violation = (CycleOverAcyclicRelationshipConstraintViolation) result.getViolations().iterator()
        .next();

    assertEquals(children.getRelationship(), violation.getRelationship());

    InstanceExpression is2 = model.findByName(isType, "is2");

    assertTrue(violation.getExpressions().contains(is1));
    assertTrue(violation.getExpressions().contains(is2));
    assertTrue(violation.getExpressions().contains(is4));

  }

  @Test
  public void testOneElementCyclicHierarchy() {
    Metamodel metamodel = getMetamodel();
    SubstantialTypeExpression isType = (SubstantialTypeExpression) metamodel.findUniversalTypeByPersistentName("IS");
    RelationshipEndExpression children = isType.findRelationshipEndByPersistentName("children");
    Model model = getModel();

    InstanceExpression is1 = model.findByName(isType, "is1");

    model.link(is1, children, is1);

    ModelValidatorResult result = validateModel(metamodel, model);

    assertEquals(1, result.getViolations().size());
    CycleOverAcyclicRelationshipConstraintViolation violation = (CycleOverAcyclicRelationshipConstraintViolation) result.getViolations().iterator()
        .next();

    assertTrue(CycleOverAcyclicRelationshipConstraintViolation.class.isInstance(violation));

    assertEquals(children.getRelationship(), violation.getRelationship());
    assertTrue(violation.getExpressions().contains(is1));
  }

  @Test
  public void testValidateUniversalTypeNamesConstraints() {
    Metamodel metamodel = getMetamodel();
    Model model = getModel();
    SubstantialTypeExpression isType = (SubstantialTypeExpression) metamodel.findUniversalTypeByName("IS");
    InstanceExpression is1 = model.findByName(isType, "is1");
    InstanceExpression is4 = model.findByName(isType, "is4");
    model.setValue(is1, MixinTypeNamed.NAME_PROPERTY, "valid#name");
    model.setValue(is4, MixinTypeNamed.NAME_PROPERTY, "invalid:name");
    ModelValidatorResult result = validateModel(metamodel, model);
    assertEquals("Expected exactly one violation", 1, result.getViolations().size());
    assertSame("The Type of violation is not the expected UniversalTypeExpressionNameConstraintViolation", true,
        UniversalTypeExpressionNameConstraintViolation.class.isInstance(result.getViolations().iterator().next()));
    UniversalTypeExpressionNameConstraintViolation violation = (UniversalTypeExpressionNameConstraintViolation) result.getViolations().iterator()
        .next();
    assertSame("The persistent name of the SubstantialTypeExpression in the violation isn't the expected one", true,
        isType.getPersistentName() == violation.getType().getPersistentName());
  }
}
