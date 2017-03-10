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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.impl.ModelImpl;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;


/**
 * JUnit elasticMI model test case for {@link BusinessMappingCardinalityValidator}
 */
public class BusinessMappingCardinalityValidatorTest extends ValidatorTestBaseWithElasticMiContext {

  private Model                               model;
  private BusinessMappingCardinalityValidator validator;

  private RStructuredTypeExpression           isr;
  private RStructuredTypeExpression           bm;
  private RStructuredTypeExpression           bp;
  private RStructuredTypeExpression           bu;
  private RStructuredTypeExpression           prod;

  private RRelationshipEndExpression          bm2isr;
  private RRelationshipEndExpression          bm2bp;
  private RRelationshipEndExpression          bm2bu;
  private RRelationshipEndExpression          bm2prod;

  private RPropertyExpression                 isrNameProp;
  private RPropertyExpression                 bpNameProp;
  private RPropertyExpression                 buNameProp;
  private RPropertyExpression                 prodNameProp;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    super.onSetUp();
    RMetamodel metamodel = DataProvider.getMetamodelWithBM();
    model = new ModelImpl();
    validator = new BusinessMappingCardinalityValidator(metamodel);

    isr = metamodel.findStructuredTypeByPersistentName("InformationSystem");
    bm = metamodel.findStructuredTypeByPersistentName("BusinessMapping");
    bp = metamodel.findStructuredTypeByPersistentName("BusinessProcess");
    bu = metamodel.findStructuredTypeByPersistentName("BusinessUnit");
    prod = metamodel.findStructuredTypeByPersistentName("BusinessProcess");

    bm2isr = bm.findRelationshipEndByPersistentName("informationSystemRelease");
    bm2bp = bm.findRelationshipEndByPersistentName("businessProcess");
    bm2bu = bm.findRelationshipEndByPersistentName("businessUnit");
    bm2prod = bm.findRelationshipEndByPersistentName("businessProcess");

    isrNameProp = isr.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    bpNameProp = bp.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    buNameProp = bu.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    prodNameProp = prod.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
  }

  /**
   * Test the correct setup.
   */
  @Test
  public void testSetUp() {
    assertNotNull(isr);
    assertNotNull(bm);
    assertNotNull(bp);
    assertNotNull(bu);
    assertNotNull(prod);
    assertNotNull(bm2isr);
    assertNotNull(bm2bp);
    assertNotNull(bm2bu);
    assertNotNull(bm2prod);
    assertNotNull(isrNameProp);
    assertNotNull(bpNameProp);
    assertNotNull(buNameProp);
    assertNotNull(prodNameProp);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.BusinessMappingCardinalityValidator#validate(de.iteratec.iteraplan.elasticmi.model.Model, de.iteratec.iteraplan.elasticmi.messages.MessageListener)}.
   */
  @Test
  public void testValidate() {

    ObjectExpression bm1 = bm.create(model);
    ObjectExpression isr1 = isr.create(model);
    setNameValue(isr, isr1, "Info Sys A");
    ObjectExpression bp1 = bp.create(model);
    setNameValue(bp, bp1, "Business Process A");
    ObjectExpression bu1 = bu.create(model);
    setNameValue(bu, bu1, "Busniess Unit A");
    ObjectExpression prod1 = prod.create(model);
    setNameValue(prod, prod1, "Product A");
    assertNotNull(isr1);
    assertNotNull(bm1);
    assertNotNull(bp1);
    assertNotNull(bu1);
    assertNotNull(prod1);

    bm2isr.connect(model, ElasticValue.one(bm1), ElasticValue.one(isr1));
    bm2bp.connect(model, ElasticValue.one(bm1), ElasticValue.one(bp1));
    bm2bu.connect(model, ElasticValue.one(bm1), ElasticValue.one(bu1));
    bm2prod.connect(model, ElasticValue.one(bm1), ElasticValue.one(prod1));
    assertTrue(validator.validate(model, MessageListener.LOG_LISTENER));

    // Only one is set
    model = new ModelImpl();
    ObjectExpression isr2 = isr.create(model);
    setNameValue(isr, isr2, "Info Sys B");
    ObjectExpression bm2 = bm.create(model);
    ObjectExpression prod2 = prod.create(model);
    setNameValue(prod, prod2, "Product B");
    bm2isr.connect(model, ElasticValue.one(bm2), ElasticValue.one(isr2));
    bm2prod.connect(model, ElasticValue.one(bm2), ElasticValue.one(prod2));
    assertTrue(validator.validate(model, MessageListener.LOG_LISTENER));

    // None set
    model = new ModelImpl();
    ObjectExpression bm3 = bm.create(model);
    ObjectExpression isr3 = isr.create(model);
    setNameValue(isr, isr3, "Info Sys C");
    bm2isr.connect(model, ElasticValue.one(bm3), ElasticValue.one(isr3));
    assertFalse(validator.validate(model, MessageListener.LOG_LISTENER));
  }

}
