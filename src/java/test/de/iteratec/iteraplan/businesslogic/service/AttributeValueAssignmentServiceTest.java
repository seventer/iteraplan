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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.persistence.dao.AttributeValueAssignmentDAO;


/**
 * Class for testing the service methods of the {@link AttributeValueAssignmentService} interface.
 * 
 * @author sip
 */
public class AttributeValueAssignmentServiceTest {

  private AttributeValueAssignmentServiceImpl attributeValueAssignmentService;
  private AttributeValueAssignmentDAO         attributeValueAssignmentDAOMock;

  private AttributeValueAssignment            ava1;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    attributeValueAssignmentService = new AttributeValueAssignmentServiceImpl();

    attributeValueAssignmentDAOMock = createNiceMock(AttributeValueAssignmentDAO.class);
    attributeValueAssignmentService.setAttributeValueAssignmentDAO(attributeValueAssignmentDAOMock);

    BusinessProcess bb = new BusinessProcess();
    bb.setId(Integer.valueOf(23234));
    bb.setName("Building Block under test");

    DateAT at = new DateAT();
    at.setName("testDateAT");
    at.setId(Integer.valueOf(826));

    DateAV av1 = new DateAV(at, new Date(1234));
    av1.setId(Integer.valueOf(8991));
    av1.setAttributeTypeTwoWay(at);
    DateAV av2 = new DateAV(at, new Date(3234223));
    av2.setId(Integer.valueOf(8992));
    av2.setAttributeTypeTwoWay(at);

    ava1 = new AttributeValueAssignment(bb, av1);
    ava1.setId(Integer.valueOf(9993));
    av1.setAttributeValueAssignments(Sets.newHashSet(ava1));
    bb.setAttributeValueAssignments(Sets.newHashSet(ava1));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AttributeValueAssignmentService#saveOrUpdate(de.iteratec.iteraplan.model.attribute.AttributeValueAssignment)}.
   */
  @Test
  public void testSaveOrUpdate() {
    expect(attributeValueAssignmentDAOMock.saveOrUpdate(ava1)).andReturn(ava1);
    replay(attributeValueAssignmentDAOMock);
    assertEquals(ava1, attributeValueAssignmentService.saveOrUpdate(ava1));
    verify(attributeValueAssignmentDAOMock);
  }
}
