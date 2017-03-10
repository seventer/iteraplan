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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMetamodelLoader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.ModelLoader;
import de.iteratec.iteraplan.businesslogic.exchange.svg.DataCreator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.ModelFactory;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTask;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.metamodel.common.base.DateInterval;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


public class MiModelLoaderTest extends BaseTransactionalTestSupport {
  @Autowired
  private ElasticMiLoadTaskFactory                    factory;

  @Autowired
  private IteraplanMetamodelLoader                    eamMetamodelLoader;

  @Autowired
  private ModelLoader                                 eamLoader;

  @Autowired
  private TestDataHelper2                             testDataHelper;

  @Autowired
  private BuildingBlockServiceLocator                 locator;

  private Metamodel                                   eamMetamodel;
  private RMetamodel                                  miMetamodel;
  private Model                                       eamModel;
  private de.iteratec.iteraplan.elasticmi.model.Model miModel;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    DataCreator creator = new DataCreator(testDataHelper);
    creator.createData();

    Iterator<InformationSystemRelease> isrIterator = locator.getIsrService().loadElementList().iterator();
    Iterator<TechnicalComponentRelease> tcrIterator = locator.getTcrService().loadElementList().iterator();
    Iterator<BusinessObject> boIterator = locator.getBoService().loadElementList().iterator();

    //create interfaces with and without transport to check creation of InformationFlows
    testDataHelper.createInformationSystemInterface(isrIterator.next(), isrIterator.next(), tcrIterator.next(), "an interface");

    InformationSystemInterface isi = testDataHelper.createInformationSystemInterface(isrIterator.next(), isrIterator.next(), tcrIterator.next(),
        "another interface");
    testDataHelper.createTransport(boIterator.next(), isi, Direction.BOTH_DIRECTIONS);

    ElasticMiLoadTask task = factory.create("MASTER");
    Assert.assertNotNull(task);
    WMetamodel wMetamodel = task.loadWMetamodel();
    Assert.assertNotNull(wMetamodel);
    IteraplanMapping mapping = eamMetamodelLoader.loadConceptualMetamodelMapping();
    Assert.assertNotNull(mapping);
    eamMetamodel = mapping.getMetamodel();
    Assert.assertNotNull(eamMetamodel);

    eamModel = ModelFactory.INSTANCE.createModel(eamMetamodel);
    eamLoader.load(eamModel, mapping);

    miMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Assert.assertNotNull(miMetamodel);
    miModel = task.loadModel(wMetamodel, miMetamodel);
    Assert.assertNotNull(miModel);
  }

  @Test
  public void testDiversify() {
    for (UniversalTypeExpression ute : eamMetamodel.getUniversalTypes()) {
      RStructuredTypeExpression rste = miMetamodel.findStructuredTypeByPersistentName(ute.getPersistentName());
      Assert.assertNotNull(rste);
      Assert.assertEquals("number of instances for ute " + ute.getPersistentName() + " not equal", eamModel.findAll(ute).size(), rste.apply(miModel)
          .getSize());
      for (UniversalModelExpression ume : eamModel.findAll(ute)) {
        BigInteger id = (BigInteger) ume.getValue(UniversalTypeExpression.ID_PROPERTY);
        ObjectExpression oe = miModel.findById(rste, id);
        Assert.assertNotNull("could not find OE of type " + rste + " with id " + id, oe);
        for (PropertyExpression<?> prop : ute.getProperties()) {
          RPropertyExpression rprop = rste.findPropertyByPersistentName(prop.getPersistentName());
          Collection<Object> eamValues = ume.getValues(prop);
          ElasticValue<ValueExpression> miValues = rprop.apply(oe);
          Assert.assertEquals("count of values for " + prop.getPersistentName() + " not equal", eamValues.size(), miValues.getSize());
          for (Object eamValue : eamValues) {
            checkValueContained(rprop, miValues, eamValue);
          }
        }
        for (RelationshipEndExpression ree : ute.getRelationshipEnds()) {
          RRelationshipEndExpression rree = rste.findRelationshipEndByPersistentName(ree.getPersistentName());
          Collection<UniversalModelExpression> eamConnecteds = ume.getConnecteds(ree);
          ElasticValue<ObjectExpression> miConnecteds = rree.apply(oe);
          Assert.assertEquals("count of connecteds for " + ute + "." + ree.getPersistentName() + " not equal", eamConnecteds.size(),
              miConnecteds.getSize());
          for (UniversalModelExpression eamConnected : eamConnecteds) {
            checkConnected(miConnecteds, eamConnected);
          }
        }
      }
    }
  }

  private void checkValueContained(RPropertyExpression rprop, ElasticValue<ValueExpression> miValues, Object eamValue) {
    boolean contained = false;
    for (ValueExpression miValue : miValues.getMany()) {
      contained |= checkValue(rprop, miValue, eamValue);
    }
    Assert.assertTrue("value not contained: " + rprop + " " + eamValue, contained);
  }

  private void checkConnected(ElasticValue<ObjectExpression> miConnecteds, UniversalModelExpression eamConnected) {
    boolean contained = false;
    for (ObjectExpression miConnected : miConnecteds.getMany()) {
      contained |= miConnected.getId().equals(eamConnected.getValue(UniversalTypeExpression.ID_PROPERTY));
    }
    Assert.assertTrue(contained);
  }

  private boolean checkValue(RPropertyExpression rprop, ValueExpression miValue, Object eamValue) {
    boolean result = false;
    if (rprop.getType().equals(AtomicDataType.DURATION.type())) {
      Assert.assertTrue(eamValue instanceof RuntimePeriod);
      DateInterval duration = miValue.asDuration();
      RuntimePeriod period = (RuntimePeriod) eamValue;
      result = checkRuntimePeriod(period, duration);
    }
    else if (rprop.getType() instanceof RNominalEnumerationExpression) {
      Assert.assertTrue(eamValue instanceof EnumerationLiteralExpression);
      result = miValue.asEnumerationLiteral().getPersistentName().equals(((EnumerationLiteralExpression) eamValue).getPersistentName());
    }
    else {
      Object normalizedEamValue = rprop.getType().normalize(ElasticValue.one(ValueExpression.create(eamValue))).getOne().getValue();
      result = miValue.getValue().equals(normalizedEamValue);
    }
    return result;
  }

  private static boolean checkRuntimePeriod(RuntimePeriod period, DateInterval duration) {
    boolean result = false;
    if (period.getStart() == null) {
      result = duration.getStart() == null;
    }
    else {
      result = period.getStart().equals(duration.getStart());
    }
    if (period.getEnd() == null) {
      result &= duration.getEnd() == null;
    }
    else {
      result &= period.getEnd().equals(duration.getEnd());
    }
    return result;
  }
}
