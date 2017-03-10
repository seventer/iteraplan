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
package de.iteratec.iteraplan.model.BusinessLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class BusinessMappingTest {

  private static final String UPDATE_BOTH_SIDES_ERROR_MSG = "Fail to update both sides of the association";
  private BusinessMapping     businessMapping             = null;

  /**
   * Test method for : {@link de.iteratec.iteraplan.model.BusinessMapping#getTypeOfBuildingBlock()}
   * The method has only meaning for the code coverage.
   */
  @Test
  public void testGetTypeOfBuildingBlock() {
    businessMapping = new BusinessMapping();

    assertEquals("businessMapping.singular", businessMapping.getTypeOfBuildingBlock().toString());
  }

  /**
   * Test method for :
   * {@link de.iteratec.iteraplan.model.BusinessMapping#getAssociatedElement(de.iteratec.iteraplan.model.TypeOfBuildingBlock)}
   * Test method for (setter and getter):
   * {@link de.iteratec.iteraplan.model.BusinessMapping#setInformationSystemRelease()}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#getInformationSystemRelease()}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#setBusinessProcess()}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#getBusinessProcess()}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#setBusinessUnit()}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#getBusinessUnit()}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#setBusinessUnit()}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#getBusinessUnit()} The method has only
   * meaning for the code coverage.
   */
  @Test
  public void testAssociatedElement() {
    businessMapping = new BusinessMapping();

    // informationSystemRelease
    InformationSystemRelease isr = new InformationSystemRelease();
    businessMapping.setInformationSystemRelease(isr);

    BuildingBlock isrActual = businessMapping.getAssociatedElement(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

    BuildingBlock isrExpected = businessMapping.getInformationSystemRelease();

    assertEquals(isrExpected, isrActual);

    // businessProcess
    BusinessProcess bp = new BusinessProcess();
    businessMapping.setBusinessProcess(bp);

    BuildingBlock bpActual = businessMapping.getAssociatedElement(TypeOfBuildingBlock.BUSINESSPROCESS);

    BuildingBlock bpExpected = businessMapping.getBusinessProcess();

    assertEquals(bpExpected, bpActual);

    // businessUnit
    BusinessUnit bu = new BusinessUnit();
    businessMapping.setBusinessUnit(bu);

    BuildingBlock buActual = businessMapping.getAssociatedElement(TypeOfBuildingBlock.BUSINESSUNIT);

    BuildingBlock buExpected = businessMapping.getBusinessUnit();

    assertEquals(buExpected, buActual);

    // product
    Product p = new Product();
    businessMapping.setProduct(p);

    BuildingBlock bprActual = businessMapping.getAssociatedElement(TypeOfBuildingBlock.PRODUCT);

    BuildingBlock bprExpected = businessMapping.getProduct();

    assertEquals(bprExpected, bprActual);

    // default
    BuildingBlock actual = businessMapping.getAssociatedElement(TypeOfBuildingBlock.DUMMY);
    BuildingBlock expected = null;
    assertEquals(expected, actual);

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#addInformationSystemRelease(InformationSystemRelease)}
   * The method tests if the addInformationSystemRelease throws correctly IllegalArgumentException if
   * the user tries to add null as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddInformationSystemReleaseCaseNull() {

    businessMapping = new BusinessMapping();
    businessMapping.addInformationSystemRelease(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#addInformationSystemRelease(InformationSystemRelease)}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#removeInformationSystemRelease(InformationSystemRelease)}
   * The method tests if the addInformationSystemRelease adds properly InformationSystemRelease and
   * if the removeInformationSystemRelease removes properly InformationSystemRelease.
   */
  @Test
  public void testAddInformationSystemReleaseCaseNotNull() {
    businessMapping = new BusinessMapping();

    // expected set from the second side of the association
    InformationSystemRelease expectedISR = new InformationSystemRelease();
    Set<BusinessMapping> expectedBMs = hashSet();
    expectedBMs.add(businessMapping);

    businessMapping.addInformationSystemRelease(expectedISR);
    InformationSystemRelease actualISR = businessMapping.getInformationSystemRelease();
    Set<BusinessMapping> actualBMs = actualISR.getBusinessMappings();

    // test for the first side of the association
    assertEquals(expectedISR, actualISR);

    // test for the second side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBMs, actualBMs);

    // test after remove
    businessMapping.removeInformationSystemRelease();
    assertEquals(businessMapping.getInformationSystemRelease(), null);

  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#addBusinessProcess(BusinessProcess)} The
   * method tests if the addBusinessProcess throws correctly IllegalArgumentException if the user tries
   * to add null as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddBusinessProcessCaseNull() {

    businessMapping = new BusinessMapping();
    businessMapping.addBusinessProcess(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#addBusinessProcess(BusinessProcess)}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#removeBusinessProcess(BusinessProcess)}The
   * method tests if the addBusinessProcess adds properly BusinessProcess and if the
   * removeBusinessProcess removes properly BusinessProcess.
   */
  @Test
  public void testAddBusinessProcessCaseNotNull() {
    businessMapping = new BusinessMapping();

    // expected set from the second side of the association
    BusinessProcess expectedBP = new BusinessProcess();
    Set<BusinessMapping> expectedBMs = hashSet();
    expectedBMs.add(businessMapping);

    businessMapping.addBusinessProcess(expectedBP);

    BusinessProcess actualBP = businessMapping.getBusinessProcess();
    Set<BusinessMapping> actualBMs = actualBP.getBusinessMappings();

    // test for the first side of the association
    assertEquals(expectedBP, actualBP);

    // test for the second side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBMs, actualBMs);

    // test after remove
    businessMapping.removeBusinessProcess();
    assertEquals(businessMapping.getBusinessProcess(), null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#addBusinessUnit(BusinessUnit)} The method
   * tests if the addBusinessUnit throws correctly IllegalArgumentException if the user tries to add
   * null as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddBusinessUnitCaseNull() {

    businessMapping = new BusinessMapping();
    businessMapping.addBusinessUnit(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#addBusinessUnit(BusinessUnit)}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#removeBusinessUnit(BusinessUnit)} The method
   * tests if the addBusinessUnit adds properly BusinessUnit and if the removeBusinessUnit removes
   * properly BusinessUnit.
   */
  @Test
  public void testAddBusinessUnitCaseNotNull() {
    businessMapping = new BusinessMapping();

    // expected set from the second side of the association
    BusinessUnit expectedBU = new BusinessUnit();
    Set<BusinessMapping> expectedBMs = hashSet();
    expectedBMs.add(businessMapping);

    businessMapping.addBusinessUnit(expectedBU);

    BusinessUnit actualBU = businessMapping.getBusinessUnit();
    Set<BusinessMapping> actualBMs = actualBU.getBusinessMappings();

    // test for the first side of the association
    assertEquals(expectedBU, actualBU);

    // test for the second side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBMs, actualBMs);

    // test after remove
    businessMapping.removeBusinessUnit();
    assertEquals(businessMapping.getBusinessUnit(), null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#addBusinessProduct(BusinessProduct)} The
   * method tests if the addProduct throws correctly IllegalArgumentException if the user tries to add
   * null as element.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddBusinessProductCaseNull() {

    businessMapping = new BusinessMapping();
    businessMapping.addProduct(null);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#addBusinessProduct(BusinessProduct)}
   * {@link de.iteratec.iteraplan.model.BusinessMapping#removeBusinessProduct(BusinessProduct)} The
   * method tests if the addProduct adds properly Product and if the removeProduct removes properly
   * Product.
   */
  @Test
  public void testAddBusinessProductCaseNotNull() {
    businessMapping = new BusinessMapping();

    // expected set from the second side of the association
    Product expectedP = new Product();
    Set<BusinessMapping> expectedBMs = hashSet();
    expectedBMs.add(businessMapping);

    businessMapping.addProduct(expectedP);

    Product actualP = businessMapping.getProduct();
    Set<BusinessMapping> actualBMs = actualP.getBusinessMappings();

    // test for the first side of the association
    assertEquals(expectedP, actualP);

    // test for the second side of the association
    assertEquals(UPDATE_BOTH_SIDES_ERROR_MSG, expectedBMs, actualBMs);

    // test after remove
    businessMapping.removeProduct();
    assertEquals(businessMapping.getProduct(), null);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessMapping#getIdentityString()} The
   * method tests if the getIdentityString() returns correct string.
   */
  @Test
  public void testGetIdentityString() {
    businessMapping = new BusinessMapping();

    // informationSystem
    InformationSystem is = new InformationSystem();
    is.setName("testInfoSys");

    InformationSystemRelease firstISR = new InformationSystemRelease();
    firstISR.setId(Integer.valueOf(15));
    firstISR.setVersion("1");
    firstISR.setInformationSystem(is);

    InformationSystemRelease secISR = new InformationSystemRelease();
    secISR.setVersion("2");
    secISR.setInformationSystem(is);
    secISR.setParent(firstISR);

    InformationSystemRelease thirdISR = new InformationSystemRelease();
    thirdISR.setId(Integer.valueOf(20));
    thirdISR.setVersion("3");
    thirdISR.setInformationSystem(is);
    thirdISR.setParent(secISR);

    // businessProcess
    BusinessProcess top = new BusinessProcess();
    top.setId(Integer.valueOf(99999));
    top.setName("top");

    BusinessProcess root = new BusinessProcess();
    root.setId(Integer.valueOf(9999));
    root.setName("root");
    root.setParent(top);

    BusinessProcess parentBP = new BusinessProcess();
    parentBP.setId(Integer.valueOf(1111));
    parentBP.setName("parent");
    parentBP.setParent(root);

    BusinessProcess bp = new BusinessProcess();
    bp.setId(Integer.valueOf(15));
    parentBP.setName("bp");
    bp.setParent(parentBP);


    // businessUnit
    BusinessUnit bu = new BusinessUnit();

    // product
    Product p = new Product();
    String expected = "testInfoSys # 1 : testInfoSys # 2 : testInfoSys # 3 / root : bp : null /  / ";

    businessMapping.setInformationSystemRelease(thirdISR);
    businessMapping.setBusinessProcess(bp);
    businessMapping.setBusinessUnit(bu);
    businessMapping.setProduct(p);
    String actual = businessMapping.getIdentityString();

    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessMapping#getIdentityString()} The
   * method tests if the getIdentityString() returns correct string.
   */
  @Test
  public void testGetIdentityStringCaseNull() {
    // BuildingBlockFactory
    businessMapping = new BusinessMapping();

    // informationSystem
    InformationSystem is = new InformationSystem();
    is.setName("testInfoSys");

    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setVersion("1");
    isr.setInformationSystem(is);

    businessMapping.setInformationSystemRelease(isr);
    String expected = "? : testInfoSys # 1 / null / null / null";

    String actual = businessMapping.getIdentityString();

    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#EqualsIds (Integer, Integer, Integer) } The
   * method tests if the equalsIds returns true correctly if bProcess, bUnit and product are set and
   * their ids are set too.
   */
  @Test
  public void testEqualsIdsCaseNotNull() {
    businessMapping = new BusinessMapping();

    BusinessProcess bp = new BusinessProcess();
    bp.setId(Integer.valueOf(1));

    BusinessUnit bu = new BusinessUnit();
    bu.setId(Integer.valueOf(2));

    Product p = new Product();
    p.setId(Integer.valueOf(3));

    businessMapping.setBusinessProcess(bp);
    businessMapping.setBusinessUnit(bu);
    businessMapping.setProduct(p);

    assertTrue(businessMapping.equalsIds(bp.getId(), bu.getId(), p.getId()));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#EqualsIds (Integer, Integer, Integer) } The
   * method tests if the equalsIds returns false correctly if some of the elements : bProcess, bUnit
   * or product are set or their ids are not set.
   */
  @Test
  public void testEqualsIdsCaseNull() {
    businessMapping = new BusinessMapping();

    BusinessProcess bp = new BusinessProcess();
    bp.setId(Integer.valueOf(1));

    BusinessUnit bu = new BusinessUnit();
    bu.setId(Integer.valueOf(2));

    Product p = new Product();

    businessMapping.setBusinessProcess(bp);
    businessMapping.setBusinessUnit(bu);
    businessMapping.setProduct(p);

    assertFalse(businessMapping.equalsIds(bp.getId(), bu.getId(), p.getId()));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.BusinessMapping#EqualsIds (Integer, Integer, Integer) } The
   * method tests if the equalsIds returns false correctly if bProcess, bUnit and product are set
   * and their ids are set, but the element ids are different from the method ids.
   */
  @Test
  public void testEqualsIdsCaseNotNullNotEqual() {
    businessMapping = new BusinessMapping();

    BusinessProcess bp = new BusinessProcess();
    bp.setId(Integer.valueOf(1));

    BusinessUnit bu = new BusinessUnit();
    bu.setId(Integer.valueOf(2));

    Product p = new Product();
    bu.setId(Integer.valueOf(3));

    businessMapping.setBusinessProcess(bp);
    businessMapping.setBusinessUnit(bu);
    businessMapping.setProduct(p);

    assertFalse(businessMapping.equalsIds(bp.getId(), bu.getId(), Integer.valueOf(5)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessMapping#getNameForHtmlId()  } The
   * method tests if the getNameForHtmlId() returns correct string.
   */
  //  @Test
  public void testGetNameForHtmlId() {
    businessMapping = new BusinessMapping();

    InformationSystem is = new InformationSystem();
    is.setName("\\testInfoSys");

    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setVersion("5");
    isr.setInformationSystem(is);

    BusinessProcess bp = new BusinessProcess();
    bp.setName("bprocess ");

    BusinessUnit bu = new BusinessUnit();
    bu.setName("bunit.");

    Product p = new Product();
    p.setName("product!");

    businessMapping.setInformationSystemRelease(isr);
    businessMapping.setBusinessProcess(bp);
    businessMapping.setBusinessUnit(bu);
    businessMapping.setProduct(p);

    String expected = "testInfoSys5:bprocess:bunit:product";
    String actual = businessMapping.getNameForHtmlId();

    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessMapping#validate ()  } The method
   * tests if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateCaseException() {
    businessMapping = new BusinessMapping();

    BusinessProcess bp = new BusinessProcess();
    bp.setName("-");
    BusinessUnit bu = new BusinessUnit();
    bu.setName("-");
    Product p = new Product();
    p.setName("-");

    businessMapping.setBusinessProcess(bp);
    businessMapping.setBusinessUnit(bu);
    businessMapping.setProduct(p);

    businessMapping.validate();

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessMapping#validate ()  } The method
   * tests if the validate method throws exception correctly.
   */
  @Test(expected = IteraplanBusinessException.class)
  public void testValidateCaseExceptionWithNullEntities() {
    businessMapping = new BusinessMapping();

    businessMapping.validate();

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessMapping#validate ()  } The method
   * tests if the validate method validates correctly. For that test case the log4jrootLogger must
   * be set on DEBUG, stdout. The flag is in the
   * /iteraplan/WebContent/Web-Inf/classes/log4j.properties
   */
  @Test
  public void testValidateCaseNoneException() {
    businessMapping = new BusinessMapping();

    InformationSystem is = new InformationSystem();
    is.setName("testIS");
    InformationSystemRelease isr = new InformationSystemRelease();
    is.addRelease(isr);
    BusinessProcess bp = new BusinessProcess();
    bp.setName("testBP");
    BusinessUnit bu = new BusinessUnit();
    bu.setName("testBU");
    Product p = new Product();
    p.setName("testProd");

    businessMapping.setInformationSystemRelease(isr);
    businessMapping.setBusinessProcess(bp);
    businessMapping.setBusinessUnit(bu);
    businessMapping.setProduct(p);

    businessMapping.validate();
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessMapping#hasTopLevelElementsOnly() }
   * The method tests if the hasTopLevelElementsOnly() returns false correctly if if all the object
   * do not have the same TOP_LEVEL_NAME
   */
  @Test
  public void testHasTopLevelElementsOnlyCaseFalse() {
    businessMapping = new BusinessMapping();

    BusinessProcess bp = new BusinessProcess();
    bp.setName("bprocess");

    BusinessUnit bu = new BusinessUnit();
    bu.setName("bunit");

    Product p = new Product();
    bu.setName("product");

    businessMapping.setBusinessProcess(bp);
    businessMapping.setBusinessUnit(bu);
    businessMapping.setProduct(p);

    assertFalse(businessMapping.hasTopLevelElementsOnly());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.BusinessMapping#hasTopLevelElementsOnly() }
   * The method tests if the hasTopLevelElementsOnly() returns true correctly if all the object have
   * the same TOP_LEVEL_NAME
   */
  @Test
  public void testHasTopLevelElementsOnlyCaseTrue() {
    businessMapping = new BusinessMapping();

    BusinessProcess bp = new BusinessProcess();
    bp.setName("-");

    BusinessUnit bu = new BusinessUnit();
    bu.setName("-");

    Product p = new Product();
    p.setName("-");

    businessMapping.setBusinessProcess(bp);
    businessMapping.setBusinessUnit(bu);
    businessMapping.setProduct(p);

    assertTrue(businessMapping.hasTopLevelElementsOnly());
  }

}
