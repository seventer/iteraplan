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
package de.iteratec.iteraplan.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;


public class AbstractHierarchicalEntityTest extends BaseTransactionalTestSupport {

  @Autowired
  private InfrastructureElementService infrastructureElementService;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }
  /**
   * Test method for {@link de.iteratec.iteraplan.model.AbstractHierarchicalEntity#addParent()}. The
   * method tests if addParent behaves correctly when called from within an iteration over children
   * of an element.
   */
  @SuppressWarnings("boxing")
  @Test
  public void testGetAttributeStringForSearchIndexing() {

    // BusinessDomain extends AbstractHierarchicalEntity
    AbstractHierarchicalEntity<BusinessDomain> classUnderTest = new BusinessDomain();
    classUnderTest.setId(42);

    BusinessDomain root = new BusinessDomain();
    root.setName("-");
    root.setId(1);

    BusinessDomain child1 = new BusinessDomain();
    child1.setId(2);

    BusinessDomain child2 = new BusinessDomain();
    child2.setId(3);

    child1.addParent((BusinessDomain) classUnderTest);
    child2.addParent((BusinessDomain) classUnderTest);

    assertEquals(2, classUnderTest.getChildren().size());

    // bring child elements to the top of the hierarchy
    // this is done in all updateFromWorkingCopy methods in the Service classes
    // if we don't use a new list to copy the children into, a concurrent modification exception
    // would be thrown
    for (BusinessDomain child : new ArrayList<BusinessDomain>(classUnderTest.getChildren())) {
      child.addParent(root);
    }
    // the above iteration already removes the children from classUnderTest, as it sets the parent
    // of these children to a different element!
    assertEquals(0, classUnderTest.getChildren().size());
    classUnderTest.getChildren().clear();

    // the two children should have root as their parent set
    assertEquals(root, child1.getParent());
    assertEquals(root, child2.getParent());
    // and root should have these two elements set as its children
    assertEquals(2, root.getChildren().size());
    // order should be the same as we added it, as we use array lists for parent/child relations
    assertEquals(child1, root.getChildren().get(0));
  }

  /**
   * Test attempting to introduce null-entries in an Hierarchical Element's children-list
   */
  @Test
  public void testChildrenListCaseNullEntries() {
    InfrastructureElement ie1 = testDataHelper.createInfrastructureElement("IE 1", "IE 1 description");
    InfrastructureElement ie11 = testDataHelper.createInfrastructureElement("IE 11", "IE 11 description");
    InfrastructureElement ie12 = testDataHelper.createInfrastructureElement("IE 12", "IE 12 description");

    ie1.setChildren(new ArrayList<InfrastructureElement>());
    ie1.getChildren().add(null);
    ie1.getChildren().add(ie11);
    ie1.getChildren().add(ie12);
    ie1.getChildren().add(ie11); // adding double entry

    infrastructureElementService.saveOrUpdate(ie1);

    commit();

    beginTransaction();
    ie1 = infrastructureElementService.loadObjectById(ie1.getId());

    assertEquals("Wrong amount of children for getChildren(): " + ie1.getChildren(), 2, ie1.getChildren().size());
    assertEquals("Wrong amount of children for getChildrenAsList(): " + ie1.getChildrenAsList(), 2, ie1.getChildrenAsList().size());
    assertEquals("Wrong amount of children for getChildrenAsSet(): " + ie1.getChildrenAsSet(), 2, ie1.getChildrenAsSet().size());
  }

}
