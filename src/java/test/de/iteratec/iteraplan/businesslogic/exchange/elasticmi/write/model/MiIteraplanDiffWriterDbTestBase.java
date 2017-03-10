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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyInit;
import de.iteratec.iteraplan.elasticmi.exception.ElasticMiException;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.elasticmi.util.NamedUtil;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.user.User;


public abstract class MiIteraplanDiffWriterDbTestBase extends BaseTransactionalTestSupport {

  protected static final String         TEST_ISD_NAME = "ISD1";

  @Autowired
  protected ElasticMiLoadTaskFactory    loadTaskFactory;

  @Autowired
  protected AttributeTypeService        attributeTypeService;

  @Autowired
  protected AttributeValueService       attributeValueService;

  @Autowired
  protected BuildingBlockServiceLocator bbServiceLocator;

  @Autowired
  protected TestDataHelper2             helper;

  @Autowired
  protected UserService                 userService;

  @Before
  public void onSetUp() {
    UserContext.setCurrentUserContext(helper.createUserContext());
  }

  @Override
  @After
  public void onTearDown() {
    UserContext.detachCurrentUserContext();
    rollback();
  }

  protected BuildingBlock findBb(TypeOfBuildingBlock type, String name) {
    List<BuildingBlock> bbs = bbServiceLocator.getService(type).findByNames(Sets.newHashSet(name));
    return bbs.isEmpty() ? null : bbs.iterator().next();
  }

  protected IteraplanMiLoadTask loadDbAndCreateTask() {
    createInitialDatabase();
    return (IteraplanMiLoadTask) loadTaskFactory.create("MASTER");
  }

  private void createInitialDatabase() {
    AttributeTypeGroup defaultGroup = helper.createAttributeTypeGroup("ATG", "Default Attribute Type Group Description");

    // Enum AT
    EnumAT enumAT = helper.createEnumAttributeType("enumAT", "enumAT description", Boolean.TRUE, defaultGroup);
    helper.assignAttributeTypeToAllAvailableBuildingBlockTypes(enumAT);
    EnumAV enumAV1 = helper.createEnumAV("enumAV1", "enumAV1-descr", enumAT);
    EnumAV enumAV2 = helper.createEnumAV("enumAV2", "enumAV2-descr", enumAT);
    EnumAV enumAV3 = helper.createEnumAV("enumAV3", "enumAV3-descr", enumAT);

    // RespAT
    ResponsibilityAT respAT = helper.createResponsibilityAttributeType("respAT", "respAT description", Boolean.TRUE, defaultGroup);
    helper.assignAttributeTypeToAllAvailableBuildingBlockTypes(respAT);
    User user1 = new User();
    user1.setLoginName("user1");
    user1.setFirstName("user1f");
    user1.setLastName("user1l");
    userService.saveOrUpdate(user1);
    User user2 = new User();
    user2.setLoginName("user2");
    user2.setFirstName("user2f");
    user2.setLastName("user2l");
    userService.saveOrUpdate(user2);
    ResponsibilityAV respAV1 = helper.createResponsibilityAV(respAT, user1).iterator().next();
    ResponsibilityAV respAV2 = helper.createResponsibilityAV(respAT, user2).iterator().next();

    // Number AT
    NumberAT numberAT = helper.createNumberAttributeType("numberAT", "numberAT description", defaultGroup);
    helper.assignAttributeTypeToAllAvailableBuildingBlockTypes(numberAT);

    // Date AT
    DateAT dateAT = helper.createDateAttributeType("dateAT", "dateAT description", defaultGroup);
    helper.assignAttributeTypeToAllAvailableBuildingBlockTypes(dateAT);

    // Text AT
    TextAT textAT = helper.createTextAttributeType("textAT", "textAT description", true, defaultGroup);
    helper.assignAttributeTypeToAllAvailableBuildingBlockTypes(textAT);

    //Building blocks
    BusinessProcess bp1 = helper.createBusinessProcess("BP1", "BP1", true);
    helper.createAVA(bp1, enumAV1);
    BusinessProcess bp2 = helper.createBusinessProcess("BP2", "BP2", true);
    helper.createAVA(bp2, enumAV2);
    BusinessProcess bp3 = helper.createBusinessProcess("BP3", "BP3", true);
    helper.createAVA(bp3, enumAV3);
    bp2.addParent(bp1);
    bp3.addParent(bp1);

    BusinessDomain bd1 = helper.createBusinessDomain("BD1", "BD1", true);
    BusinessDomain bd2 = helper.createBusinessDomain("BD2", "BD2", true);
    bd1.addBusinessProcesses(Sets.newHashSet(bp1, bp2));
    bd2.addBusinessProcesses(Sets.newHashSet(bp2, bp3));

    BusinessUnit bu1 = helper.createBusinessUnit("BU1", "BU1", true);
    AttributeValue av = new NumberAV(numberAT, BigDecimal.valueOf(10));
    attributeValueService.saveOrUpdate(av);
    helper.createAVA(bu1, av);
    BusinessUnit bu2 = helper.createBusinessUnit("BU2", "BU2", true);
    av = new NumberAV(numberAT, BigDecimal.valueOf(20));
    attributeValueService.saveOrUpdate(av);
    helper.createAVA(bu2, av);
    bbServiceLocator.getBuService().saveOrUpdate(bu1);

    BusinessObject bo1 = helper.createBusinessObject("BO1", "BO1", true);
    av = new DateAV(dateAT, parseDate("10.10.2013"));
    attributeValueService.saveOrUpdate(av);
    helper.createAVA(bo1, av);
    BusinessObject bo2 = helper.createBusinessObject("BO2", "BO2", true);
    av = new DateAV(dateAT, parseDate("10.12.2013"));
    attributeValueService.saveOrUpdate(av);
    helper.createAVA(bo2, av);

    InformationSystemRelease isr1 = helper.createInformationSystemRelease(helper.createInformationSystem("IS1"), "1");
    helper.createAVA(isr1, respAV1);
    InformationSystemRelease isr2 = helper.createInformationSystemRelease(helper.createInformationSystem("IS2"), "1");
    helper.createAVA(isr2, respAV2);
    isr2.addParent(isr1);

    InformationSystemInterface isi1 = helper.createInformationSystemInterface(isr1, isr2, null, "ISI1");
    helper.createTransport(bo1, isi1, Direction.BOTH_DIRECTIONS);
    helper.createTransport(bo2, isi1, Direction.NO_DIRECTION);
    isi1.setName("IS1-IS2");
    isi1.setDirection("->");

    Product prod1 = helper.createProduct("PROD1", "PROD1", true);
    av = new TextAV(textAT, "some text");
    attributeValueService.saveOrUpdate(av);
    helper.createAVA(prod1, av);

    helper.createBusinessMapping(isr1, bp2, bu1, prod1, true).setName("BM1");
    helper.createBusinessMapping(isr1, bp3, bu1, prod1, true).setName("BM2");
    helper.createBusinessMapping(isr2, bp1, bu2, prod1, true).setName("BM3");

    helper.addBusinessObjectToInformationSystem(isr2, bo1);

    helper.createInformationSystemDomain(TEST_ISD_NAME, "ISD1 description");

    commit();
    beginTransaction();
  }

  protected static Date parseDate(String dateString) {
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    try {
      return dateFormat.parse(dateString);
    } catch (ParseException e) {
      throw new ElasticMiException(ElasticMiException.GENERAL_ERROR, "Failed to parse date");
    }
  }

  protected static RStructuredTypeExpression type(RMetamodel mm, String name) {
    return mm.findStructuredTypeByPersistentName(name);
  }

  protected static ObjectExpression object(RStructuredTypeExpression type, Model model, String name) {
    RPropertyExpression nameP = type.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    for (ObjectExpression oe : type.apply(model).getMany()) {
      if (NamedUtil.areSame(name, nameP.apply(oe).isOne() ? nameP.apply(oe).getOne().asString() : "")) {
        return oe;
      }
    }
    return null;
  }

  protected static ElasticValue<ValueExpression> evVe(Object... objs) {
    return ElasticValue.many(ValueExpression.create(Sets.newHashSet(objs)));
  }

  protected static RRelationshipEndExpression relEnd(RStructuredTypeExpression type, String name) {
    return type.findRelationshipEndByPersistentName(name);
  }

  protected static RPropertyExpression property(RStructuredTypeExpression type, String name) {
    return type.findPropertyByPersistentName(name);
  }

  protected static PropertyInit propInit(RStructuredTypeExpression type, String relEndName, ValueExpression ve) {
    return new PropertyInit(property(type, relEndName), ElasticValue.one(ve));
  }

  protected static PropertyInit propInit(RStructuredTypeExpression type, String relEndName, Collection<ValueExpression> ves) {
    return new PropertyInit(property(type, relEndName), ElasticValue.many(ves));
  }

}
