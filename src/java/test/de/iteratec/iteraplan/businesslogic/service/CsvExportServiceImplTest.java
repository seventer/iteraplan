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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.iteratec.iteraplan.MockTestDataFactory;
import de.iteratec.iteraplan.MockTestHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;


/**
 *
 */
public class CsvExportServiceImplTest {

  private AttributeTypeDAO     attributeTypeDAOm;
  private CsvExportServiceImpl csvExportServiceImpl;

  @Before
  public void setUp() {
    attributeTypeDAOm = MockTestHelper.createNiceMock(AttributeTypeDAO.class);
    csvExportServiceImpl = new CsvExportServiceImpl(attributeTypeDAOm);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.CsvExportServiceImpl#createCsvExport(java.util.List)}.
   */
  @Test
  public void testCreateCsvExport() {
    MockTestDataFactory mtdf = MockTestDataFactory.getInstance();
    mtdf.createUserContext();

    // setting only some required values
    List<InformationSystemRelease> isrList = new ArrayList<InformationSystemRelease>();
    InformationSystemRelease isr = mtdf.getInformationSystemReleaseTestData();
    InformationSystem is = mtdf.getInformationSystemTestData();
    is.setId(Integer.valueOf(2));
    // will be searched for 
    String funnyString = "mumbleblitz!";
    isr.setDescription("this is my description and I hope it's in the csv: " + funnyString);

    isr.setId(Integer.valueOf(5));
    isr.setInformationSystem(is);
    Date twoDaysAgo = new Date(System.currentTimeMillis() - 172800000);
    Date yesterday = new Date(System.currentTimeMillis() - 8640000);
    isr.setRuntimePeriod(new RuntimePeriod(twoDaysAgo, yesterday));
    isrList.add(isr);

    // creating the AttributeType-list supposedly activated for ISRs 
    List<AttributeType> atList = new ArrayList<AttributeType>();
    // since iteraplan.properties isn't further specified
    List<String> propertyNames = generateNameList();
    for (String name : propertyNames) {
      AttributeType at = new NumberAT();
      atList.add(at);
      MockTestHelper.expect(attributeTypeDAOm.getAttributeTypeByName(name)).andReturn(at);
    }
    MockTestHelper.expect(attributeTypeDAOm.getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, true))
        .andReturn(atList);
    MockTestHelper.replay(attributeTypeDAOm);

    String res = csvExportServiceImpl.createCsvExport(isrList);
    // the method creates an entire new csv-file, assertEquals(String, String) is madness.
    // Instead the result is searched for certain substrings set before, i.e. funnyString and dates  
    assertTrue(res.contains("InformationSystemRelease.version."));
    
    if (IteraplanProperties.getProperties().propertyIsSetToTrue(IteraplanProperties.PROP_CSV_EXPORT_DESCRIPTION)) {
      assertTrue(res.contains(funnyString));
    }
    if (IteraplanProperties.getProperties().propertyIsSetToTrue(IteraplanProperties.PROP_CSV_EXPORT_TIMESPAN)) {
      assertTrue(res.contains(DateUtils.formatAsString(yesterday, UserContext.getCurrentLocale())));
    }

    // NOTE: the output opens correctly when stored as .csv
  }

  /**
   * Creates a list containing the names for attributes stored in iteraplan.properties retrieved
   * by {@link de.iteratec.iteraplan.businesslogic.service.CsvExportServiceImpl#getAttributeTypesToLog()}
   * so that the mock can be properly prepared.
   *  
   * @return List with attribute names
   */
  private List<String> generateNameList() {
    List<String> res = new ArrayList<String>();

    IteraplanProperties properties = IteraplanProperties.getProperties();
    Collection<Object> keys = properties.getAllPropertyKeys();
    List<String> attrKeys = new ArrayList<String>();

    for (Object obj : keys) {
      String key = (String) obj;
      if (key.startsWith(IteraplanProperties.PREFIX_CSV_ATTR)) {
        attrKeys.add(key);
      }
    }
    Collections.sort(attrKeys);

    for (String attrKey : attrKeys) {
      String name = properties.getProperty(attrKey);
      res.add(name);
    }

    return res;
  }
}
