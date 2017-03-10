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
package de.iteratec.iteraplan.persistence.dao;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * @author mma
 */
public class InfrastructureElementDAOImplTest extends BaseTransactionalTestSupport {
  private static final String          TEST_DESCRIPTION         = "testDescription";
  @Autowired
  private InfrastructureElementDAOImpl infrastructureElementDAO;
  @Autowired
  private TestDataHelper2              testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InfrastructureElementDAOImpl#onBeforeDelete(de.iteratec.iteraplan.model.InfrastructureElement)}
   */
  @Test
  public void testOnBeforeDelete() {
    InfrastructureElement entity = testDataHelper.createInfrastructureElement("testAD", "desc");
    InfrastructureElement parent = testDataHelper.createInfrastructureElement("parentAD", TEST_DESCRIPTION);

    TechnicalComponent tc = testDataHelper.createTechnicalComponent("testTC", true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, "Beta", true);

    InformationSystem is = testDataHelper.createInformationSystem("testIS");
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, "Beta");

    InfrastructureElement firstChild = testDataHelper.createInfrastructureElement("firstChildAD", TEST_DESCRIPTION);
    InfrastructureElement secChild = testDataHelper.createInfrastructureElement("secChildAD", TEST_DESCRIPTION);
    InfrastructureElement thirdChild = testDataHelper.createInfrastructureElement("thirdChildAD", TEST_DESCRIPTION);
    commit();

    Set<InformationSystemRelease> setISRs = hashSet();
    setISRs.add(isr);

    beginTransaction();
    testDataHelper.addTcrToIe(firstChild, tcr);
    firstChild.setInformationSystemReleases(setISRs);
    testDataHelper.addTcrToIe(secChild, tcr);
    secChild.setInformationSystemReleases(setISRs);
    testDataHelper.addTcrToIe(thirdChild, tcr);
    thirdChild.setInformationSystemReleases(setISRs);
    commit();

    beginTransaction();
    thirdChild.addParent(secChild);
    secChild.addParent(firstChild);
    commit();

    List<InfrastructureElement> children = arrayList();
    children.add(firstChild);

    beginTransaction();
    new Tcr2IeAssociation(tcr, entity).connect();
    entity.addInformationSystemReleases(setISRs);
    entity.addParent(parent);
    entity.setChildren(children);
    commit();
    beginTransaction();

    infrastructureElementDAO.onBeforeDelete(entity);
    commit();
    beginTransaction();

    InfrastructureElement actualParent = entity.getParent();
    List<InfrastructureElement> expectedChildren = arrayList();
    List<InfrastructureElement> actualChildren = entity.getChildren();

    // after the delete operation the entity should not have a parent.
    assertNull(actualParent);
    // after the delete operation the entity should not have any children.
    assertEquals(expectedChildren, actualChildren);


    Set<TechnicalComponentRelease> expectedTCRs = Sets.newHashSet(tcr); // the tcr2IeAssociations will not be deleted
    Set<InformationSystemRelease> expectedISRs = hashSet();
    Set<TechnicalComponentRelease> actualTCRs = entity.getTechnicalComponentReleases();

    // after the delete operation the entity should not have any association with
    // any TechnicalComponentReleases
    Assert.assertEquals(expectedTCRs, actualTCRs);
    // after the delete operation the entity should not have a parent.
    Assert.assertNull(actualParent);
    // after the delete operation the entities children should not have any association with
    // technicalComponentReleases or with informationSystemReleases.
    Assert.assertEquals(expectedTCRs, firstChild.getTechnicalComponentReleases());
    Assert.assertEquals(expectedTCRs, secChild.getTechnicalComponentReleases());
    Assert.assertEquals(expectedTCRs, thirdChild.getTechnicalComponentReleases());

    Assert.assertEquals(expectedISRs, firstChild.getInformationSystemReleases());
    Assert.assertEquals(expectedISRs, secChild.getInformationSystemReleases());
    Assert.assertEquals(expectedISRs, thirdChild.getInformationSystemReleases());
  }

}
