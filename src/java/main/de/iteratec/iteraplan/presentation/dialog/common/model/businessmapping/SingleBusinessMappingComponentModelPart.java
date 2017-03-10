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
package de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.service.BusinessProcessService;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.ProductService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessMappingEntity;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.AttributesComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.strategy.BusinessMappingStrategy;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.strategy.BusinessMappingStrategyFactory;


public class SingleBusinessMappingComponentModelPart implements Serializable {
  /** Serialization version. */
  private static final long                         serialVersionUID = 42L;

  private final ComponentMode                       componentMode;
  private final BusinessMapping                     bizMapping;
  private final String                              htmlId;

  private Integer                                   selectedBpId;
  private Integer                                   selectedBusinessUnitId;
  private Integer                                   selectedProductId;
  private Integer                                   selectedInformationSystemRelId;
  private BusinessMappingItems                      bmi;

  private transient BusinessProcessService          businessProcessService;
  private transient BusinessUnitService             businessUnitService;
  private transient InformationSystemReleaseService informationSystemReleaseService;
  private transient ProductService                  productService;

  /** Component Model for BuildingBlock attributeValueAssignments references (incl. AttributeValues). */
  private final AttributesComponentModel            attributeModel;

  public SingleBusinessMappingComponentModelPart(BusinessMapping bizMapping, ComponentMode componentMode) {
    initServices();
    this.bizMapping = bizMapping;
    this.componentMode = componentMode;
    this.htmlId = bizMapping.getNameForHtmlId();

    // display all attributes that are assigned to business mappings
    attributeModel = new AttributeAttributesComponentModel(componentMode, this.htmlId);
  }

  private void initServices() {
    businessProcessService = SpringServiceFactory.getBusinessProcessService();
    businessUnitService = SpringServiceFactory.getBusinessUnitService();
    informationSystemReleaseService = SpringServiceFactory.getInformationSystemReleaseService();
    productService = SpringServiceFactory.getProductService();
  }

  /**
   * Implement readObject() to allow the deserialisation to init the transient field.
   * 
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private void readObject(final ObjectInputStream is) throws IOException, ClassNotFoundException {
    if (is != null) {
      is.defaultReadObject();
    }
    initServices();
  }

  public void initializeFrom(BusinessMappingItems bmiSorted) {
    this.bmi = bmiSorted;
    this.selectedInformationSystemRelId = bizMapping.getInformationSystemRelease().getId();
    this.selectedBpId = getBusinessProcessIdSafe(bizMapping);
    this.selectedBusinessUnitId = getBusinessUnitIdSafe(bizMapping);
    this.selectedProductId = getProductIdSafe(bizMapping);

    this.attributeModel.initializeFrom(bizMapping);
  }

  /**
   * Returns the id for the {@link BusinessProcess} contained in specified {@link BusinessMapping}.
   * If the tuple element equals {@code null}, the root element id will be returned and the set for 
   * the specified {@link BusinessMapping}. 
   * 
   * <p>Developer note: this method is required to make the {@link BusinessMapping} more fault tolerant. 
   * 
   * @param bm the business mapping
   * @return the business process id
   */
  private Integer getBusinessProcessIdSafe(BusinessMapping bm) {
    if (bm.getBusinessProcess() != null) {
      return bm.getBusinessProcess().getId();
    }

    BusinessProcess rootBp = businessProcessService.getFirstElement();
    bm.addBusinessProcess(rootBp);
    return rootBp.getId();
  }

  /**
   * Returns the id for the {@link BusinessUnit} contained in specified {@link BusinessMapping}.
   * If the tuple element equals {@code null}, the root element id will be returned and the set for 
   * the specified {@link BusinessMapping}. 
   * 
   * <p>Developer note: this method is required to make the {@link BusinessMapping} more fault tolerant. 
   * 
   * @param bm the business mapping
   * @return the business unit id
   */
  private Integer getBusinessUnitIdSafe(BusinessMapping bm) {
    if (bm.getBusinessUnit() != null) {
      return bm.getBusinessUnit().getId();
    }

    BusinessUnit rootBu = businessUnitService.getFirstElement();
    bm.addBusinessUnit(rootBu);
    return rootBu.getId();
  }

  /**
   * Returns the id for the {@link Product} contained in specified {@link BusinessMapping}.
   * If the tuple element equals {@code null}, the root element id will be returned and the set for 
   * the specified {@link BusinessMapping}. 
   * 
   * <p>Developer note: this method is required to make the {@link BusinessMapping} more fault tolerant. 
   * 
   * @param bm the business mapping
   * @return the product id
   */
  private Integer getProductIdSafe(BusinessMapping bm) {
    if (bm.getProduct() != null) {
      return bm.getProduct().getId();
    }

    Product rootProduct = productService.getFirstElement();
    bm.addProduct(rootProduct);
    return rootProduct.getId();
  }

  public void update(BusinessMappingItems bmiAll) {
    this.bmi = bmiAll;

    attributeModel.update();
  }

  /**
   * Configure a fresh target Information system release with the business mapping and attribute
   * value assignment information of this cluster.
   * 
   * @param target The fresh business mapping participant instance to configure.
   */
  public void configure(BuildingBlock target) {
    BusinessMapping businessMapping;
    if (getComponentMode() == ComponentMode.CREATE) {
      businessMapping = BuildingBlockFactory.createBusinessMapping();
    }
    else {
      businessMapping = reloadBusinessMapping();
    }

    final BusinessMappingStrategy strategy = BusinessMappingStrategyFactory.getStrategyFor(target.getClass());
    strategy.addOwningEntity(businessMapping, target);

    if (target instanceof BusinessMappingEntity && !(target instanceof BusinessFunction)) {
      final BusinessMappingEntity targetBMEntity = (BusinessMappingEntity) target;
      targetBMEntity.addBusinessMapping(businessMapping);
    }

    if (!(target instanceof InformationSystemRelease)) {
      InformationSystemRelease isr = informationSystemReleaseService.loadObjectById(selectedInformationSystemRelId);
      businessMapping.setInformationSystemRelease(isr);
    }

    if (!(target instanceof BusinessProcess)) {
      BusinessProcess bp = businessProcessService.loadObjectById(selectedBpId);
      businessMapping.setBusinessProcess(bp);
    }

    if (!(target instanceof BusinessUnit)) {
      BusinessUnit bu = businessUnitService.loadObjectById(selectedBusinessUnitId);
      businessMapping.setBusinessUnit(bu);
    }

    if (!(target instanceof Product)) {
      Product prod = productService.loadObjectById(selectedProductId);
      businessMapping.setProduct(prod);
    }

    attributeModel.configure(businessMapping);
  }

  /**
   * Reloads the existing {@link BusinessMapping} or returns the {@code bizMapping} if new.
   * 
   * @return the possibly reloaded business mapping
   */
  private BusinessMapping reloadBusinessMapping() {
    BusinessMapping reloadedBm = null;
    if (bizMapping.getId() != null) {
      reloadedBm = SpringServiceFactory.getBusinessMappingService().loadObjectById(bizMapping.getId());
    }
    else {
      reloadedBm = bizMapping;
    }
    return reloadedBm;
  }

  public BusinessMapping getMapping() {
    return bizMapping;
  }

  public ComponentMode getComponentMode() {
    return componentMode;
  }

  public AttributesComponentModel getAttributeModel() {
    return attributeModel;
  }

  public List<InformationSystemRelease> getAvailableInformationSystemReleases() {
    return bmi.getInformationSystemReleases();
  }

  public Integer getSelectedInformationSystemRelId() {
    return selectedInformationSystemRelId;
  }

  public void setSelectedInformationSystemRelId(Integer selectedInformationSystemRelId) {
    this.selectedInformationSystemRelId = selectedInformationSystemRelId;
  }

  public List<BusinessProcess> getAvailableBps() {
    return bmi.getBusinessProcesses();
  }

  public List<BusinessUnit> getAvailableBusinessUnits() {
    return bmi.getBusinessUnits();
  }

  public List<Product> getAvailableProducts() {
    return bmi.getProducts();
  }

  public Integer getSelectedBpId() {
    return selectedBpId;
  }

  public void setSelectedBpId(Integer selectedBpId) {
    this.selectedBpId = selectedBpId;
  }

  public Integer getSelectedBusinessUnitId() {
    return selectedBusinessUnitId;
  }

  public void setSelectedBusinessUnitId(Integer id) {
    this.selectedBusinessUnitId = id;
  }

  public Integer getSelectedProductId() {
    return selectedProductId;
  }

  public void setSelectedProductId(Integer selectedProductId) {
    this.selectedProductId = selectedProductId;
  }

  public String getHtmlId() {
    return htmlId;
  }

  private static final class AttributeAttributesComponentModel extends AttributesComponentModel {

    /** Serialization version. */
    private static final long serialVersionUID = 1028251768126230198L;

    public AttributeAttributesComponentModel(ComponentMode componentMode, String htmlId) {
      super(componentMode, htmlId);
    }

    @Override
    public boolean showATG(AttributeTypeGroup atg) {
      return true;
    }
  }

}
