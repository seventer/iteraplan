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
/**
 * 
 */
package de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.strategy;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.BusinessMappingItems;


/**
 * Test class for the {@link BusinessUnitMappingStrategy}.
 *
 */
public class BusinessUnitMappingStrategyTest {

  private final BusinessMappingStrategy mappingStrategy = BusinessMappingStrategyFactory.getStrategyFor(BusinessUnit.class);

  /**
   * Creates a mock for the {@link BuildingBlockTypeService} and sets it in {@link BuildingBlockFactory}.
   */
  @Before
  public void setUp() {
    final BuildingBlockTypeService bbTypeService = EasyMock.createMock(BuildingBlockTypeService.class);
    EasyMock.expect(bbTypeService.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSMAPPING)).andReturn(null).anyTimes();
    EasyMock.replay(bbTypeService);

    BuildingBlockFactory.setBbTypeService(bbTypeService);

    BuildingBlockServiceLocator bbServiceLocator = EasyMock.createMock("BuildingBlockServiceLocator", BuildingBlockServiceLocator.class);
    EasyMock.replay(bbServiceLocator);
    BuildingBlockFactory.setBbServiceLocator(bbServiceLocator);
  }

  /**
   * Resets the {@link BuildingBlockFactory}. 
   */
  @After
  public void tearDown() {
    BuildingBlockFactory.setBbTypeService(null);
    BuildingBlockFactory.setBbServiceLocator(null);
  }

  @Test
  public void testCreateBusinessMappingsWithEmptyItems() {
    final BusinessMappingItems bmi = new BusinessMappingItems();
    final List<BusinessMapping> mappings = mappingStrategy.createBusinessMappings(bmi);
    Assert.assertTrue(mappings.isEmpty());
  }

  @Test
  public void testCreateBusinessMappings() {
    final InformationSystemRelease isr1 = new InformationSystemRelease();
    final InformationSystemRelease isr2 = new InformationSystemRelease();
    final BusinessProcess bp1 = new BusinessProcess();
    final Product product = new Product();

    final List<InformationSystemRelease> isrs = Lists.newArrayList(isr1, isr2);
    final List<BusinessProcess> bps = Lists.newArrayList(bp1);
    final List<BusinessUnit> bus = Lists.newArrayList();
    final List<Product> products = Lists.newArrayList(product);
    final BusinessMappingItems bmi = new BusinessMappingItems(isrs, bps, bus, products);

    final List<BusinessMapping> mappings = mappingStrategy.createBusinessMappings(bmi);
    Assert.assertEquals(2, mappings.size());
    final BusinessMapping businessMapping1 = mappings.get(0);
    final BusinessMapping businessMapping2 = mappings.get(1);

    Assert.assertEquals(isr1, businessMapping1.getInformationSystemRelease());
    Assert.assertEquals(bp1, businessMapping1.getBusinessProcess());
    Assert.assertEquals(product, businessMapping1.getProduct());

    Assert.assertEquals(isr2, businessMapping2.getInformationSystemRelease());
    Assert.assertEquals(bp1, businessMapping2.getBusinessProcess());
    Assert.assertEquals(product, businessMapping2.getProduct());
  }

  @Test
  public void testCreateBusinessMappingsWithTopLevelNames() {
    final BusinessProcess bp1 = new BusinessProcess();
    bp1.setName(BusinessProcess.TOP_LEVEL_NAME);
    final Product product = new Product();
    product.setName(Product.TOP_LEVEL_NAME);

    final List<InformationSystemRelease> isrs = Lists.newArrayList();
    final List<BusinessProcess> bps = Lists.newArrayList(bp1);
    final List<BusinessUnit> bus = Lists.newArrayList();
    final List<Product> products = Lists.newArrayList(product);
    final BusinessMappingItems bmi = new BusinessMappingItems(isrs, bps, bus, products);

    final List<BusinessMapping> mappings = mappingStrategy.createBusinessMappings(bmi);
    Assert.assertTrue(mappings.isEmpty());
  }

  @Test
  public void testCreateBusinessMappingsWithTopLevelNamesAndWithISR() {
    final InformationSystemRelease isr1 = new InformationSystemRelease();
    final InformationSystemRelease isr2 = new InformationSystemRelease();
    final BusinessProcess bp1 = new BusinessProcess();
    bp1.setName(BusinessProcess.TOP_LEVEL_NAME);
    final Product product = new Product();
    product.setName(Product.TOP_LEVEL_NAME);

    final List<InformationSystemRelease> isrs = Lists.newArrayList(isr1, isr2);
    final List<BusinessProcess> bps = Lists.newArrayList(bp1);
    final List<BusinessUnit> bus = Lists.newArrayList();
    final List<Product> products = Lists.newArrayList(product);
    final BusinessMappingItems bmi = new BusinessMappingItems(isrs, bps, bus, products);

    final List<BusinessMapping> mappings = mappingStrategy.createBusinessMappings(bmi);
    Assert.assertFalse(mappings.isEmpty());
    Assert.assertEquals(2, mappings.size());
  }

  @Test(expected = IteraplanBusinessException.class)
  public void testValidateWithEmptyItems() {
    final BusinessMappingItems bmi = new BusinessMappingItems();
    mappingStrategy.validate(bmi);
  }

  @Test
  public void testValidate() {
    final InformationSystemRelease isr1 = new InformationSystemRelease();
    final InformationSystemRelease isr2 = new InformationSystemRelease();
    final BusinessProcess bp1 = new BusinessProcess();
    final Product product = new Product();

    final List<InformationSystemRelease> isrs = Lists.newArrayList(isr1, isr2);
    final List<BusinessProcess> bps = Lists.newArrayList(bp1);
    final List<BusinessUnit> bus = Lists.newArrayList();
    final List<Product> products = Lists.newArrayList(product);
    final BusinessMappingItems bmi = new BusinessMappingItems(isrs, bps, bus, products);

    mappingStrategy.validate(bmi);
  }

  @Test
  public void testAddOwningEntity() {
    final BusinessMapping businessMapping = new BusinessMapping();
    final BusinessUnit bu = new BusinessUnit();
    mappingStrategy.addOwningEntity(businessMapping, bu);

    Assert.assertEquals(bu, businessMapping.getBusinessUnit());
    Assert.assertThat(bu.getBusinessMappings(), JUnitMatchers.hasItem(businessMapping));
  }

  @Test(expected = ClassCastException.class)
  public void testAddOwningEntityForInvalidBB() {
    final BusinessMapping businessMapping = new BusinessMapping();
    final Product product = new Product();
    mappingStrategy.addOwningEntity(businessMapping, product);
  }

  @Test
  public void testDoesMappingExist() {
    final InformationSystemRelease isr1 = new InformationSystemRelease();
    isr1.setId(Integer.valueOf(1));
    final InformationSystemRelease isr2 = new InformationSystemRelease();
    isr2.setId(Integer.valueOf(1));
    final BusinessProcess bp1 = new BusinessProcess();
    bp1.setId(Integer.valueOf(1));
    final Product product = new Product();
    product.setId(Integer.valueOf(1));

    final BusinessMapping bm1 = new BusinessMapping();
    bm1.setInformationSystemRelease(isr1);
    bm1.setBusinessProcess(bp1);
    bm1.setProduct(product);

    final BusinessMapping bm2 = new BusinessMapping();
    bm2.setInformationSystemRelease(isr2);
    bm2.setBusinessProcess(bp1);
    bm2.setProduct(product);

    final List<BusinessMapping> existingMappings = Lists.newArrayList(bm1, bm2);

    final BusinessMapping bmToCheck = new BusinessMapping();
    bmToCheck.setInformationSystemRelease(isr2);
    bmToCheck.setBusinessProcess(bp1);
    bmToCheck.setProduct(product);

    Assert.assertTrue(mappingStrategy.doesMappingExist(existingMappings, bmToCheck));
  }

  @Test
  public void testDoesMappingExistFalse() {
    final InformationSystemRelease isr1 = new InformationSystemRelease();
    isr1.setId(Integer.valueOf(1));
    final InformationSystemRelease isr2 = new InformationSystemRelease();
    isr2.setId(Integer.valueOf(1));
    final BusinessProcess bp1 = new BusinessProcess();
    bp1.setId(Integer.valueOf(1));
    final Product product = new Product();
    product.setId(Integer.valueOf(1));

    final BusinessMapping bm1 = new BusinessMapping();
    bm1.setInformationSystemRelease(isr1);
    bm1.setBusinessProcess(bp1);
    bm1.setProduct(product);

    final BusinessMapping bm2 = new BusinessMapping();
    bm2.setInformationSystemRelease(isr2);
    bm2.setBusinessProcess(bp1);
    bm2.setProduct(product);

    final List<BusinessMapping> existingMappings = Lists.newArrayList(bm1, bm2);

    final BusinessMapping bmToCheck = new BusinessMapping();
    final InformationSystemRelease isr3 = new InformationSystemRelease();
    bmToCheck.setInformationSystemRelease(isr3);
    bmToCheck.setBusinessProcess(bp1);
    bmToCheck.setProduct(product);

    Assert.assertFalse(mappingStrategy.doesMappingExist(existingMappings, bmToCheck));
  }

}
