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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.MockTestHelper;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.user.DataSource;
import de.iteratec.iteraplan.persistence.RoutingDataSource;
import de.iteratec.iteraplan.persistence.dao.DataSourceDAO;


/**
 *
 */
public class DataSourceServiceImplTest {

  private DataSourceDAO         dataSourceDAOm;
  private RoutingDataSource     routingDataSourceM;

  private DataSourceServiceImpl dataSourceServiceImpl = new DataSourceServiceImpl();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() {
    dataSourceDAOm = MockTestHelper.createNiceMock(DataSourceDAO.class);
    routingDataSourceM = MockTestHelper.createNiceMock(RoutingDataSource.class);
    
    dataSourceServiceImpl.setDataSourceDAO(dataSourceDAOm);
    dataSourceServiceImpl.setRoutingDataSource(routingDataSourceM);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DataSourceServiceImpl#validateKey(java.lang.String)}.
   */
  @Test(expected = IteraplanTechnicalException.class)
  public void testValidateKeyEmptyKey() {
    dataSourceServiceImpl.validateKey("");
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DataSourceServiceImpl#validateKey(java.lang.String)}.
   */
  @Test
  public void testValidateKeyMasterdata() {
    assertNull(dataSourceServiceImpl.validateKey(Constants.MASTER_DATA_SOURCE));
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DataSourceServiceImpl#validateKey(java.lang.String)}.
   */
  @Test(expected = IteraplanTechnicalException.class)
  public void testValidateKeyEmptySources() {
    String inputKey = "key";
    List<DataSource> dataSources = new ArrayList<DataSource>();
    MockTestHelper.expect(dataSourceDAOm.getDataSourceByKey(inputKey)).andReturn(dataSources).anyTimes();
    MockTestHelper.replay(dataSourceDAOm);
    
    dataSourceServiceImpl.validateKey(inputKey);
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DataSourceServiceImpl#validateKey(java.lang.String)}.
   */
  @Test(expected = IteraplanTechnicalException.class)
  public void testValidateKeyExpiredKey() {
    String inputKey = "key";
    
    List<DataSource> dataSources = new ArrayList<DataSource>();
    DataSource source = new DataSource();
    source.setExpiryDate(new Date(System.currentTimeMillis()-1000));
    dataSources.add(source);
    
    MockTestHelper.expect(dataSourceDAOm.getDataSourceByKey(inputKey)).andReturn(dataSources).anyTimes();
    MockTestHelper.replay(dataSourceDAOm);
    
    dataSourceServiceImpl.validateKey(inputKey);
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DataSourceServiceImpl#validateKey(java.lang.String)}.
   */
  @Test
  public void testValidateKey() {
    String inputKey = "key";
    
    List<DataSource> dataSources = new ArrayList<DataSource>();
    DataSource source = new DataSource();
    dataSources.add(source);
    
    MockTestHelper.expect(dataSourceDAOm.getDataSourceByKey(inputKey)).andReturn(dataSources).anyTimes();
    MockTestHelper.replay(dataSourceDAOm);
    
    assertEquals(source, dataSourceServiceImpl.validateKey(inputKey));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.DataSourceServiceImpl#initializeDBwithKey(java.lang.String)}.
   */
  @SuppressWarnings("boxing")
  @Test
  public void testInitializeDBwithKey() {
    String inputKey = "key";
    
    List<DataSource> dataSources = new ArrayList<DataSource>();
    DataSource source = new DataSource();
    dataSources.add(source);   
    MockTestHelper.expect(dataSourceDAOm.getDataSourceByKey(inputKey)).andReturn(dataSources).anyTimes();
    MockTestHelper.expect(routingDataSourceM.isKeyContained(inputKey)).andReturn(Boolean.FALSE);
    MockTestHelper.replay(dataSourceDAOm, routingDataSourceM);
    
    dataSourceServiceImpl.initializeDBwithKey(inputKey);
  }

}
