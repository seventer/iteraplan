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

import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.ModelFactory;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.random.RandomModelLoader;


/**
 *
 */
public class RandomModelTest {
  private Metamodel metamodel;
  private Model     model;
  private int       size;

  @Before
  public void setUp() {
    this.metamodel = new MetamodelCreator().getMetamodel();
    this.model = ModelFactory.INSTANCE.createModel(metamodel);
    this.size = new Random().nextInt(100) + 2;
    new RandomModelLoader().load(metamodel, model, this.size);
  }

  @Test
  public void countTests() {
    for (UniversalTypeExpression type : this.metamodel.getUniversalTypes()) {
      Assert.assertEquals(size, this.model.findAll(type).size());
    }
  }

  @Test
  public void cardTests() {
    for (UniversalTypeExpression type : this.metamodel.getUniversalTypes()) {
      for (UniversalModelExpression instance : this.model.findAll(type)) {
        cardTest(instance, type);
      }
    }
  }

  private void cardTest(UniversalModelExpression instance, UniversalTypeExpression type) {
    for (PropertyExpression<?> prop : type.getProperties()) {
      if (prop.getUpperBound() == 1) {
        Assert.assertTrue(prop.getLowerBound() == 0 || instance.getValue(prop) != null);
      }
      else {
        Assert.assertTrue(prop.getLowerBound() == 0 || !instance.getValues(prop).isEmpty());
      }
    }
    for (RelationshipEndExpression rend : type.getRelationshipEnds()) {
      if (rend.getUpperBound() == 1) {
        Assert.assertTrue(rend.getLowerBound() == 0 || instance.getConnected(rend) != null);
      }
      else {
        Assert.assertTrue(rend.getLowerBound() == 0 || !instance.getConnecteds(rend).isEmpty());
      }
    }
  }
}
