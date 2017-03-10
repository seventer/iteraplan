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
package de.iteratec.elasticeam.operator;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.elasticeam.MetamodelCreator;
import de.iteratec.elasticeam.ModelCreator;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.BindingSet;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.operator.power.PowerSubstantialType;


public class PowerOperatorTest {

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

  private SubstantialTypeExpression typeIS() {
    return (SubstantialTypeExpression) getMetamodel().findTypeByName("IS");
  }

  private SubstantialTypeExpression getPowerType() {
    return new PowerSubstantialType(typeIS());
  }

  @Test
  public void testCreatePowerType() {
    getPowerType();
  }

  @Test
  public void testPowerTypeInstance() {
    SubstantialTypeExpression powerType = getPowerType();
    Collection<InstanceExpression> instances = getModel().findAll(powerType);
    assertEquals(1, instances.size());
  }

  @Test
  public void testEvaluatePowerType() {
    SubstantialTypeExpression powerType = getPowerType();
    Collection<InstanceExpression> instances = getModel().findAll(powerType);

    RelationshipEndExpression instancesRelEnd = powerType.findRelationshipEndByPersistentName("isContainer");

    assertEquals(getModel().findAll(typeIS()).size(), instances.iterator().next().getConnecteds(instancesRelEnd).size());

    BindingSet mapping = getModel().findAll(instancesRelEnd);
    assertEquals(1, mapping.getAllFromElements().size());
    assertEquals(getModel().findAll(typeIS()).size(), mapping.getAllToElements().size());

    for (UniversalModelExpression toElement : mapping.getAllToElements()) {
      assertEquals(1, mapping.getFromBindings(toElement).size());
    }

    //opposite rel end
    RelationshipEndExpression oppositeEnd = null;
    for (RelationshipEndExpression relEnd : instancesRelEnd.getRelationship().getRelationshipEnds()) {
      if (!relEnd.equals(instancesRelEnd)) {
        oppositeEnd = relEnd;
      }
    }

    BindingSet inverseMapping = getModel().findAll(oppositeEnd);
    assertEquals(1, inverseMapping.getAllToElements().size());
    assertEquals(getModel().findAll(typeIS()).size(), inverseMapping.getAllFromElements().size());
    for (UniversalModelExpression fromElement : inverseMapping.getAllFromElements()) {
      assertEquals(1, inverseMapping.getToBindings(fromElement).size());
    }

    UniversalModelExpression singleIs = getModel().findAll(typeIS()).iterator().next();
    Collection<UniversalModelExpression> powerTypes = singleIs.getConnecteds(oppositeEnd);
    assertEquals(1, powerTypes.size());

  }

}
