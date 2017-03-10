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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateInterval;


/**
 * Unit test class for DateIntervalDAO.
 */
public class DateIntervalDAOTest extends BaseTransactionalTestSupport {

  private static final String TEST_DESCRIPTION = "testDescription";
  @Autowired
  private DateIntervalDAO dateIntervalDAO;
  @Autowired
  private TestDataHelper2 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }
  
  @Test
  public void readDateIntervals() {
    List<DateInterval> list =  dateIntervalDAO.loadElementList(null);
    assertNotNull(list);
  }
  
  @Test
  public void testCascadeByDeleteDateAT() {
    AttributeTypeGroup group = testDataHelper.getDefaultATG();
    DateAT at1 = testDataHelper.createDateAttributeType("name1", TEST_DESCRIPTION, group);
    DateAT at2 = testDataHelper.createDateAttributeType("name2", TEST_DESCRIPTION, group);
    commit();
    
    DateInterval di = new DateInterval();
    beginTransaction();
    
    di.setName("a test user name");
    di.setDefaultColorHex("#123456");
    di.setStartDate(at1);
    di.setEndDate(at2);
    DateInterval cDI = dateIntervalDAO.saveOrUpdate(di);
    commit();
    
    beginTransaction();
    DateInterval justInserted =  dateIntervalDAO.loadObjectById(cDI.getId());
    assertNotNull(justInserted);
    
    // This part of the test is more related to the AttributeTypeServiceImpl than the DAO itself...
    // It is important here to use the reference of DateAT from the loaded object to avoid a NonUniqueObjectException in Hibernate.
    testDataHelper.deleteAttributeType(justInserted.getStartDate());
    commit();
    
    DateInterval justDeleted = null;
    List<DateInterval> list =  dateIntervalDAO.loadElementList(null);
    for (DateInterval element : list) {
      if (element.getId().equals(cDI.getId())) {
        justDeleted = element;
      }
    }
    assertNull(justDeleted);
  }
}
