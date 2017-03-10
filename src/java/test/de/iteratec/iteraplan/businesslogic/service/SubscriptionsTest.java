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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.TestAsSuperUser;
import de.iteratec.iteraplan.businesslogic.service.notifications.NotificationService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.dao.ArchitecturalDomainDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO;


/**
 * JUnit Test for {@link de.iteratec.iteraplan.businesslogic.service.AbstractBuildingBlockService#subscribe(java.io.Serializable, boolean)}.
 */
public class SubscriptionsTest extends TestAsSuperUser {

  private NotificationService                   notificationServiceMock;

  private ArchitecturalDomainServiceImpl        architecturalDomainService;
  private ArchitecturalDomainDAO                architecturalDomainDAOMock;
  private ArchitecturalDomain                   architecturalDomain;

  private InformationSystemInterfaceServiceImpl informationSystemInterfaceService;
  private InformationSystemInterfaceDAO         informationSystemInterfaceDAOMock;
  private InformationSystemInterface            informationSystemInterface;

  @Before
  public void setUp() {
    notificationServiceMock = createNiceMock(NotificationService.class);
    AttributeValueService attributeValueServiceMock = createNiceMock(AttributeValueService.class);

    architecturalDomainService = new ArchitecturalDomainServiceImpl();
    architecturalDomainDAOMock = createNiceMock(ArchitecturalDomainDAO.class);

    architecturalDomain = new ArchitecturalDomain();
    architecturalDomain.setName("AD");
    architecturalDomain.setId(Integer.valueOf(1));
    architecturalDomain.setParent(new ArchitecturalDomain());

    architecturalDomainService.setArchitecturalDomainDAO(architecturalDomainDAOMock);
    architecturalDomainService.setNotificationService(notificationServiceMock);
    architecturalDomainService.setAttributeValueService(attributeValueServiceMock);

    informationSystemInterfaceService = new InformationSystemInterfaceServiceImpl();
    informationSystemInterfaceDAOMock = createNiceMock(InformationSystemInterfaceDAO.class);

    informationSystemInterface = new InformationSystemInterface();
    informationSystemInterface.setName("ISI");
    informationSystemInterface.setId(Integer.valueOf(1));
    informationSystemInterface.setInformationSystemReleaseA(new InformationSystemRelease());
    informationSystemInterface.setInformationSystemReleaseB(new InformationSystemRelease());

    informationSystemInterfaceService.setInformationSystemInterfaceDAO(informationSystemInterfaceDAOMock);
    informationSystemInterfaceService.setNotificationService(notificationServiceMock);
    informationSystemInterfaceService.setAttributeValueService(attributeValueServiceMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AbstractBuildingBlockService#subscribe(java.io.Serializable, boolean)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSubscribeArchitecturalDomain() {
    Integer id = Integer.valueOf(123);
    expect(architecturalDomainDAOMock.loadObjectById(id)).andReturn(architecturalDomain).anyTimes();
    expect(architecturalDomainDAOMock.saveOrUpdate(architecturalDomain)).andReturn(architecturalDomain).anyTimes();
    replay(architecturalDomainDAOMock);

    notificationServiceMock.sendEmail(EasyMock.anyObject(Collection.class), EasyMock.eq("generic.subscribed"),
        EasyMock.anyObject(Map.class));
    replay(notificationServiceMock);

    Set<User> subscribers = architecturalDomain.getSubscribedUsers();
    assertEquals(Integer.valueOf(1), architecturalDomainService.subscribe(id, true));
    assertEquals(1, subscribers.size());
    assertTrue(subscribers.contains(UserContext.getCurrentUserContext().getUser()));
    verify(notificationServiceMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AbstractBuildingBlockService#subscribe(java.io.Serializable, boolean)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUnsubscribeArchitecturalDomain() {
    Integer id = Integer.valueOf(123);
    expect(architecturalDomainDAOMock.loadObjectById(id)).andReturn(architecturalDomain).anyTimes();
    expect(architecturalDomainDAOMock.saveOrUpdate(architecturalDomain)).andReturn(architecturalDomain).anyTimes();
    replay(architecturalDomainDAOMock);

    notificationServiceMock.sendEmail(EasyMock.anyObject(Collection.class), EasyMock.eq("generic.unsubscribed"),
        EasyMock.anyObject(Map.class));
    replay(notificationServiceMock);

    Set<User> subscribers = architecturalDomain.getSubscribedUsers();
    subscribers.add(UserContext.getCurrentUserContext().getUser());
    assertEquals(Integer.valueOf(0), architecturalDomainService.subscribe(id, false));
    assertEquals(0, subscribers.size());
    assertFalse(subscribers.contains(UserContext.getCurrentUserContext().getUser()));
    verify(notificationServiceMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AbstractBuildingBlockService#subscribe(java.io.Serializable, boolean)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSubscribeInformationSystemInterface() {
    Integer id = Integer.valueOf(123);
    expect(informationSystemInterfaceDAOMock.loadObjectById(id)).andReturn(informationSystemInterface).anyTimes();
    expect(informationSystemInterfaceDAOMock.saveOrUpdate(informationSystemInterface)).andReturn(informationSystemInterface).anyTimes();
    replay(informationSystemInterfaceDAOMock);

    notificationServiceMock.sendEmail(EasyMock.anyObject(Collection.class), EasyMock.eq("generic.subscribed"), EasyMock.anyObject(Map.class));
    replay(notificationServiceMock);

    Set<User> subscribers = informationSystemInterface.getSubscribedUsers();
    assertEquals(Integer.valueOf(1), informationSystemInterfaceService.subscribe(id, true));
    assertEquals(1, subscribers.size());
    assertTrue(subscribers.contains(UserContext.getCurrentUserContext().getUser()));
    verify(notificationServiceMock);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.AbstractBuildingBlockService#subscribe(java.io.Serializable, boolean)}.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUnsubscribeInformationSystemInterface() {
    Integer id = Integer.valueOf(123);
    expect(informationSystemInterfaceDAOMock.loadObjectById(id)).andReturn(informationSystemInterface).anyTimes();
    expect(informationSystemInterfaceDAOMock.saveOrUpdate(informationSystemInterface)).andReturn(informationSystemInterface).anyTimes();
    replay(informationSystemInterfaceDAOMock);

    notificationServiceMock.sendEmail(EasyMock.anyObject(Collection.class), EasyMock.eq("generic.unsubscribed"), EasyMock.anyObject(Map.class));
    replay(notificationServiceMock);

    Set<User> subscribers = informationSystemInterface.getSubscribedUsers();
    subscribers.add(UserContext.getCurrentUserContext().getUser());
    assertEquals(Integer.valueOf(0), informationSystemInterfaceService.subscribe(id, false));
    assertEquals(0, subscribers.size());
    assertFalse(subscribers.contains(UserContext.getCurrentUserContext().getUser()));
    verify(notificationServiceMock);
  }
}
