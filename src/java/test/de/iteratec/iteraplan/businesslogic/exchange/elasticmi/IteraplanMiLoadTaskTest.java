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

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMetamodelLoader;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTask;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WClassExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WEnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WFeatureExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WNamedExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WRelationshipTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WUniversalTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WValueTypeExpression;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


/**
 *
 */
public class IteraplanMiLoadTaskTest extends BaseTransactionalTestSupport {
  private static final Logger      LOGGER = Logger.getIteraplanLogger(IteraplanMiLoadTaskTest.class);

  @Autowired
  private ElasticMiLoadTaskFactory factory;

  @Autowired
  private IteraplanMetamodelLoader eamLoader;

  @Autowired
  private TestDataHelper2          testDataHelper;

  private WMetamodel               miMetamodel;
  private Metamodel                eamMetamodel;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    BuildingBlockType isr = testDataHelper.getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    BuildingBlockType ie = testDataHelper.getBuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT);
    BuildingBlockType bm = testDataHelper.getBuildingBlockType(TypeOfBuildingBlock.BUSINESSMAPPING);

    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    AttributeTypeGroup testgroup = testDataHelper.createAttributeTypeGroup("testgroup", "testgroup");
    DateAT date = testDataHelper.createDateAttributeType("date", "date", testgroup);
    date.addBuildingBlockTypeTwoWay(isr);
    EnumAT singleEnum = testDataHelper.createEnumAttributeType("enum", "enum", Boolean.FALSE, testgroup);
    singleEnum.addBuildingBlockTypeTwoWay(isr);
    EnumAT multiEnum = testDataHelper.createEnumAttributeType("multEnum", "multEnum", Boolean.TRUE, testgroup);
    multiEnum.addBuildingBlockTypeTwoWay(isr);
    NumberAT number = testDataHelper.createNumberAttributeType("number", "number", testgroup);
    number.addBuildingBlockTypesTwoWay(Sets.newHashSet(isr, ie, bm));
    ResponsibilityAT singleResp = testDataHelper.createResponsibilityAttributeType("resp", "resp", Boolean.FALSE, testgroup);
    singleResp.addBuildingBlockTypeTwoWay(isr);
    ResponsibilityAT multResp = testDataHelper.createResponsibilityAttributeType("multResp", "multResp", Boolean.TRUE, testgroup);
    multResp.addBuildingBlockTypeTwoWay(isr);
    TextAT text = testDataHelper.createTextAttributeType("text", "text", false, testgroup);
    text.addBuildingBlockTypeTwoWay(isr);
    TextAT richtext = testDataHelper.createTextAttributeType("richtext", "richtext", true, testgroup);
    richtext.addBuildingBlockTypeTwoWay(isr);
    ElasticMiLoadTask task = factory.create("MASTER");
    Assert.assertNotNull(task);
    miMetamodel = task.loadWMetamodel();
    Assert.assertNotNull(miMetamodel);
    eamMetamodel = eamLoader.loadConceptualMetamodel();
    Assert.assertNotNull(eamMetamodel);
  }

  @Test
  public void testDiversify() {
    checkUniversalTypes();
    checkDataTypes();
  }

  private void checkUniversalTypes() {
    LOGGER.info("checking UTEs");
    Assert.assertEquals("count of UTEs was not equal", eamMetamodel.getUniversalTypes().size(), miMetamodel.getUniversalTypes().size());

    for (UniversalTypeExpression ute : eamMetamodel.getUniversalTypes()) {
      LOGGER.info("checking UTE " + ute.getPersistentName());
      WUniversalTypeExpression wute = miMetamodel.findUniversalTypeByPersistentName(ute.getPersistentName());
      Assert.assertNotNull("UTE " + ute.getPersistentName() + " was not found in miMetamodel", wute);
      if (ute.getMetaType().equals(SubstantialTypeExpression.class)) {
        Assert.assertEquals(WClassExpression.class, wute.getMetaType());
      }
      else {
        Assert.assertEquals(WRelationshipTypeExpression.class, wute.getMetaType());
      }
      checkLocalization(ute, wute);
      checkFeatures(ute, wute);
    }
  }

  private void checkFeatures(UniversalTypeExpression ute, WUniversalTypeExpression wute) {
    LOGGER.info("checking features of " + ute.getPersistentName());
    Assert.assertEquals("count of features of " + ute.getPersistentName() + " was not equal", ute.getFeatures().size(), wute.getAllFeatures().size());
    for (FeatureExpression<?> feature : ute.getFeatures()) {
      WFeatureExpression<?> wfeature = wute.findFeatureByPersistentName(feature.getPersistentName());
      Assert.assertNotNull("feature " + feature.getPersistentName() + " was not found in miMetamodel", wfeature);
      checkFeature(feature, wfeature);
    }
  }

  private void checkDataTypes() {
    for (DataTypeExpression eamType : eamMetamodel.getDataTypes()) {
      WValueTypeExpression<?> miType = null;
      String eamPersistentName = eamType.getPersistentName();
      if ("de.iteratec.iteraplan.model.RuntimePeriod".equals(eamPersistentName)) {
        miType = miMetamodel.findValueTypeByPersistentName("Duration");
      }
      else if ("java.math.BigDecimal".equals(eamPersistentName)) {
        miType = miMetamodel.findValueTypeByPersistentName("decimal");
      }
      else if ("java.math.BigInteger".equals(eamPersistentName)) {
        miType = miMetamodel.findValueTypeByPersistentName("integer");
      }
      else if ("java.lang.Boolean".equals(eamPersistentName)) {
        miType = miMetamodel.findValueTypeByPersistentName("boolean");
      }
      else if ("java.util.Date".equals(eamPersistentName)) {
        miType = miMetamodel.findValueTypeByPersistentName("date");
      }
      else if ("java.lang.String".equals(eamPersistentName)) {
        miType = miMetamodel.findValueTypeByPersistentName("string");
      }
      else {
        miType = miMetamodel.findValueTypeByPersistentName(eamPersistentName);
      }
      Assert.assertNotNull(miType);
      checkType(eamType, miType, null);
      checkLocalization(eamType, miType);

      if (eamType instanceof EnumerationExpression) {
        List<WEnumerationLiteralExpression> miLiterals = ((WNominalEnumerationExpression) miType).getLiterals();
        List<EnumerationLiteralExpression> eamLiterals = ((EnumerationExpression) eamType).getLiterals();
        Assert.assertEquals(eamLiterals.size(), miLiterals.size());
        for (int i = 0; i < eamLiterals.size(); i++) {
          Assert.assertEquals(eamLiterals.get(i).getPersistentName(), miLiterals.get(i).getPersistentName());
        }
        checkLocalization(eamType, miType);
      }
    }
  }

  private void checkFeature(FeatureExpression<?> feature, WFeatureExpression<?> wfeature) {
    //last modification properties falsely have lower bound 0 in eam
    if (!feature.getPersistentName().contains("lastModification")) {
      Assert.assertEquals("incorrect lower bound " + feature.getHolder() + "." + feature, feature.getLowerBound(), wfeature.getLowerBound());
    }
    Assert.assertEquals("incorrect upper bound " + feature.getHolder() + "." + feature, feature.getUpperBound(), wfeature.getUpperBound());
    checkType(feature.getType(), wfeature.getType(), feature.getPersistentName());
    checkLocalization(feature, wfeature);
  }

  private void checkType(Object eamType, Object miType, String featureName) {
    //feature name for the dateTime
    if (eamType instanceof UniversalTypeExpression) {
      Assert.assertTrue(miType instanceof WUniversalTypeExpression);
      Assert.assertTrue(((WUniversalTypeExpression) miType).getPersistentName().equals(((WUniversalTypeExpression) miType).getPersistentName()));
    }
    else if (eamType instanceof EnumerationExpression) {
      Assert.assertTrue(miType instanceof WNominalEnumerationExpression);
      Assert.assertTrue(((WNominalEnumerationExpression) miType).getPersistentName().equals(
          ((WNominalEnumerationExpression) miType).getPersistentName()));
    }
    else if (eamType.equals(BuiltinPrimitiveType.BOOLEAN)) {
      Assert.assertEquals(AtomicDataType.BOOLEAN.type(), miType);
    }
    else if (eamType.equals(BuiltinPrimitiveType.DATE) && featureName != null && featureName.contains("lastModification")) {
      Assert.assertEquals(AtomicDataType.DATE_TIME.type(), miType);
    }
    else if (eamType.equals(BuiltinPrimitiveType.DATE) && (featureName == null || (featureName != null && !featureName.contains("lastModification")))) {
      Assert.assertEquals(AtomicDataType.DATE.type(), miType);
    }
    else if (eamType.equals(BuiltinPrimitiveType.DECIMAL)) {
      Assert.assertEquals(AtomicDataType.DECIMAL.type(), miType);
    }
    else if (eamType.equals(BuiltinPrimitiveType.DURATION)) {
      Assert.assertEquals(AtomicDataType.DURATION.type(), miType);
    }
    else if (eamType.equals(BuiltinPrimitiveType.INTEGER)) {
      Assert.assertEquals(AtomicDataType.INTEGER.type(), miType);
    }
    else if (eamType.equals(BuiltinPrimitiveType.STRING)) {
      Assert.assertTrue(miType.equals(AtomicDataType.STRING.type()) || miType.equals(AtomicDataType.RICH_TEXT.type()));
    }
  }

  private void checkLocalization(NamedExpression eamNamed, WNamedExpression miNamed) {
    for (String locString : Constants.LOCALES) {
      Locale locale = new Locale(locString);
      if (!eamNamed.getName(locale).equals(miNamed.getName(locale))) {
        LOGGER.debug("name of " + eamNamed + "(" + eamNamed.getName(locale) + ") not equal to " + miNamed + "(" + miNamed.getName(locale)
            + ") for locale " + locale);
      }
      if (!eamNamed.getDescription(locale).equals(miNamed.getDescription(locale))) {
        LOGGER.debug("description of " + eamNamed + "(" + eamNamed.getDescription(locale) + ") not equal to " + miNamed + "("
            + miNamed.getDescription(locale) + ") for locale " + locale);
      }
      if (!eamNamed.getAbbreviation(locale).equals(miNamed.getAbbreviation(locale))) {
        LOGGER.debug("abbreviation of " + eamNamed + "(" + eamNamed.getAbbreviation(locale) + ") not equal to " + miNamed + "("
            + miNamed.getAbbreviation(locale) + ") for locale " + locale);
      }
    }
  }
}
