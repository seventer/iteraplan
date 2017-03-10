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
package de.iteratec.iteraplan.businesslogic.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;


/**
 * Integration test for the {@link AttributeTypeServiceImpl} service.
 */
public class AttributeTypeServiceIntegrationTest extends BaseTransactionalTestSupport {
  private BusinessDomain        businessDomain1;
  private BusinessDomain        businessDomain2;

  @Autowired
  private AttributeTypeService  attributeTypeService;
  @Autowired
  private AttributeValueService attributeValueService;
  @Autowired
  private AttributeTypeGroupDAO attributeTypeGroupDAO;
  @Autowired
  private BusinessDomainService businessDomainService;
  @Autowired
  private TestDataHelper2       testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());

    businessDomain1 = testDataHelper.createBusinessDomain("business domain I", "business domain description");
    businessDomain2 = testDataHelper.createBusinessDomain("business domain II", "business domain description");

    EnumAT enumAt = createEnumAttribute("enum");

    testDataHelper.createAVA(businessDomain1, getAttributeValueByName("A", enumAt.getAttributeValues()));
    testDataHelper.createAVA(businessDomain2, getAttributeValueByName("B", enumAt.getAttributeValues()));

    commit();
    beginTransaction();
  }

  private EnumAT createEnumAttribute(String identifier) {
    final AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    EnumAT at = testDataHelper.createEnumAttributeType(identifier, identifier, Boolean.FALSE, atgStandard);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(at);

    testDataHelper.createEnumAV("A", "A desc", at);
    testDataHelper.createEnumAV("B", "B desc", at);
    testDataHelper.createEnumAV("C", "C desc", at);
    testDataHelper.createEnumAV("D", "C desc", at);
    testDataHelper.createEnumAV("E", "C desc", at);

    return at;
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeTypeServiceImpl#saveOrUpdate(de.iteratec.iteraplan.model.attribute.AttributeType)}.
   */
  @Test
  public void testSaveOrUpdate() {
    assertFalse(businessDomain1.getAttributeValueAssignments().isEmpty());
    assertFalse(businessDomain2.getAttributeValueAssignments().isEmpty());

    EnumAT att = (EnumAT) attributeTypeService.getAttributeTypeByName("enum");
    EnumAV av1 = getAttributeValueByName("A", att.getAttributeValues());
    att.getAttributeValues().remove(av1);
    attributeValueService.deleteEntity(av1);
    attributeTypeService.saveOrUpdate(att);
    commit();

    beginTransaction();
    businessDomain1 = businessDomainService.loadObjectById(businessDomain1.getId());
    assertTrue(businessDomain1.getAttributeValueAssignments().isEmpty());

    att = (EnumAT) attributeTypeService.getAttributeTypeByName("enum");
    EnumAV av2 = getAttributeValueByName("B", att.getAttributeValues());
    att.getAttributeValues().remove(av2);
    attributeValueService.deleteEntity(av2);
    attributeTypeService.saveOrUpdate(att);
    commit();

    beginTransaction();
    businessDomain2 = businessDomainService.loadObjectById(businessDomain2.getId());
    assertTrue(businessDomain2.getAttributeValueAssignments().isEmpty());
  }

  private EnumAV getAttributeValueByName(String name, Collection<EnumAV> attValues) {
    for (EnumAV av : attValues) {
      if (av.getName().equals(name)) {
        return av;
      }
    }
    return null;
  }
}
