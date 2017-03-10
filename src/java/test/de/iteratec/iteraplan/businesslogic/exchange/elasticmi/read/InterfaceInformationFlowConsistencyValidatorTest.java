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
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.impl.ModelImpl;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;


/**
 * JUnit elasticMI model test case for {@link InterfaceInformationFlowConsistencyValidator}
 */
public class InterfaceInformationFlowConsistencyValidatorTest extends ValidatorTestBaseWithElasticMiContext {

  private Model                                        model;
  private InterfaceInformationFlowConsistencyValidator validator;

  private RStructuredTypeExpression                    informationSystemInterface;
  private RStructuredTypeExpression                    informationFlow;
  private RStructuredTypeExpression                    informationSystem;
  private RRelationshipEndExpression                   infoFlowToIsr1;
  private RRelationshipEndExpression                   infoFlowToIsr2;
  private RRelationshipEndExpression                   isiToInfoFlow;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    super.onSetUp();
    RMetamodel metamodel = DataProvider.getMetamodelForInterfaceInformationFlowConsistencyValidator();
    model = new ModelImpl();
    validator = new InterfaceInformationFlowConsistencyValidator(metamodel);

    informationSystemInterface = metamodel.findStructuredTypeByPersistentName("InformationSystemInterface");
    informationFlow = metamodel.findStructuredTypeByPersistentName("InformationFlow");
    informationSystem = metamodel.findStructuredTypeByPersistentName("InformationSystem");

    isiToInfoFlow = informationSystemInterface.findRelationshipEndByPersistentName("informationFlows");
    infoFlowToIsr1 = informationFlow.findRelationshipEndByPersistentName("informationSystemRelease1");
    infoFlowToIsr2 = informationFlow.findRelationshipEndByPersistentName("informationSystemRelease2");
  }

  /**
   * Test the correct setup.
   */
  @Test
  public void testSetUp() {
    assertNotNull(informationSystemInterface);
    assertNotNull(informationFlow);
    assertNotNull(informationSystem);
    assertNotNull(infoFlowToIsr1);
    assertNotNull(infoFlowToIsr2);
    assertNotNull(isiToInfoFlow);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.InterfaceInformationFlowConsistencyValidator#validate(de.iteratec.iteraplan.elasticmi.model.Model, de.iteratec.iteraplan.elasticmi.messages.MessageListener)}.
   */
  @Test
  public void testValidate() {
    ObjectExpression isi = informationSystemInterface.create(model);
    setNameValue(informationSystem, isi, "Test Information System");
    ObjectExpression isrA1 = informationSystem.create(model);
    setNameValue(informationSystem, isrA1, "Test Information System A1");
    ObjectExpression isrA2 = informationSystem.create(model);
    setNameValue(informationSystem, isrA2, "Test Information System A2");
    ObjectExpression isrB1 = informationSystem.create(model);
    setNameValue(informationSystem, isrB1, "Test Information System B1");
    ObjectExpression isrB2 = informationSystem.create(model);
    setNameValue(informationSystem, isrB2, "Test Information System");

    ObjectExpression infoFlow1 = informationFlow.create(model);
    ObjectExpression infoFlow2 = informationFlow.create(model);
    ObjectExpression infoFlow3 = informationFlow.create(model);
    ObjectExpression infoFlow4 = informationFlow.create(model);
    ObjectExpression infoFlow5 = informationFlow.create(model);

    // test equal IF with just one connected ISR
    infoFlowToIsr1.connect(model, ElasticValue.one(infoFlow1), ElasticValue.one(isrA1));
    infoFlowToIsr2.connect(model, ElasticValue.one(infoFlow1), ElasticValue.one(isrA2));
    infoFlowToIsr1.connect(model, ElasticValue.one(infoFlow2), ElasticValue.one(isrA1));
    isiToInfoFlow.connect(model, ElasticValue.one(isi), ElasticValue.one(infoFlow1));
    isiToInfoFlow.connect(model, ElasticValue.one(isi), ElasticValue.one(infoFlow2));
    assertFalse(validator.validate(model, MessageListener.LOG_LISTENER));

    // test equal ISR in IF
    infoFlowToIsr2.connect(model, ElasticValue.one(infoFlow2), ElasticValue.one(isrA2));
    assertTrue(validator.validate(model, MessageListener.LOG_LISTENER));

    // test equals ISR in IF, but different order
    isiToInfoFlow.disconnect(model, ElasticValue.one(isi), ElasticValue.one(infoFlow2));
    infoFlowToIsr1.connect(model, ElasticValue.one(infoFlow3), ElasticValue.one(isrA2));
    infoFlowToIsr2.connect(model, ElasticValue.one(infoFlow3), ElasticValue.one(isrA1));
    isiToInfoFlow.connect(model, ElasticValue.one(isi), ElasticValue.one(infoFlow3));
    assertFalse(validator.validate(model, MessageListener.LOG_LISTENER));

    // test partially different ISR in IF
    isiToInfoFlow.disconnect(model, ElasticValue.one(isi), ElasticValue.one(infoFlow3));
    infoFlowToIsr1.connect(model, ElasticValue.one(infoFlow4), ElasticValue.one(isrA1));
    infoFlowToIsr2.connect(model, ElasticValue.one(infoFlow4), ElasticValue.one(isrB1));
    isiToInfoFlow.connect(model, ElasticValue.one(isi), ElasticValue.one(infoFlow4));
    assertFalse(validator.validate(model, MessageListener.LOG_LISTENER));

    // test completely different ISR in IF
    isiToInfoFlow.disconnect(model, ElasticValue.one(isi), ElasticValue.one(infoFlow4));
    infoFlowToIsr1.connect(model, ElasticValue.one(infoFlow5), ElasticValue.one(isrB1));
    infoFlowToIsr2.connect(model, ElasticValue.one(infoFlow5), ElasticValue.one(isrB2));
    isiToInfoFlow.connect(model, ElasticValue.one(isi), ElasticValue.one(infoFlow5));
    assertFalse(validator.validate(model, MessageListener.LOG_LISTENER));
  }

}
