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
package de.iteratec.iteraplan.presentation.dialog.MassUpdate.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model for mass update operations on {@link BusinessMapping}s.
 */
public class BusinessMappingCmMu implements Serializable {

  /** Serialization version. */
  private static final long     serialVersionUID        = 8041810797263191397L;
  public static final String    PATH_TO_BUSINESSPROCESS = "BusinessProcess";
  public static final String    PATH_TO_BUSINESSUNIT    = "BusinessUnit";
  public static final String    PATH_TO_PRODUCT         = "Product";

  private BusinessMapping       bm;

  private List<BusinessProcess> processes               = new ArrayList<BusinessProcess>(0);
  private List<BusinessUnit>    units                   = new ArrayList<BusinessUnit>(0);
  private List<Product>         products                = new ArrayList<Product>(0);

  private BusinessProcess       selectedProcess;
  private Integer               selectedProcessID;

  private BusinessUnit          selectedUnit;
  private Integer               selectedUnitID;

  private Product               selectedProduct;
  private Integer               selectedProductID;

  private ComponentMode         componentMode;
  private String                htmlId;

  public BusinessMappingCmMu(BusinessMapping bm, ComponentMode mode) {

    this.bm = bm;
    this.componentMode = mode;
    this.htmlId = bm.getNameForHtmlId();
  }

  public BusinessMapping getEncapsulatedBusinessMappings() {
    return bm;
  }

  public List<BusinessProcess> getAvailableBusinessProcess() {
    return processes;
  }

  public BusinessProcess getSelectedBusinessProcess() {
    if (selectedProcess == null) {
      setSelectedBusinessProcess();
    }
    return selectedProcess;
  }

  public Integer getSelectedBusinessProcessId() {
    return selectedProcessID;
  }

  public List<BusinessUnit> getAvailableBusinessUnit() {
    return units;
  }

  public BusinessUnit getSelectedBusinessUnit() {
    if (selectedUnit == null) {
      setSelectedBusinessUnit();
    }
    return selectedUnit;
  }

  public Integer getSelectedBusinessUnitId() {
    return selectedUnitID;
  }

  public List<Product> getAvailableProduct() {
    return products;
  }

  public Product getSelectedProduct() {
    if (selectedProduct == null) {
      setSelectedProduct();
    }
    return selectedProduct;
  }

  public Integer getSelectedProductId() {
    return selectedProductID;
  }

  public void setEncapsulatedBusinessMapping(BusinessMapping bm) {
    this.bm = bm;
  }

  public void setSelectedBusinessProcessId(Integer selectedBpId) {
    this.selectedProcessID = selectedBpId;
  }

  public void setSelectedBusinessUnitId(Integer id) {
    this.selectedUnitID = id;
  }

  public void setSelectedProductId(Integer selectedProductId) {
    this.selectedProductID = selectedProductId;
  }

  public ComponentMode getComponentMode() {
    return componentMode;
  }

  public String getHtmlId() {
    return htmlId;
  }

  /**
   * Returns a hashcode for this element taking the ids of the associated BusinessProcess, Product
   * and Organizational unit as the calculation base
   * 
   * @return The hashcode
   */
  public Integer getCustomHashCode() {

    int result = 17;
    result = 37 * result + "bpro".hashCode() * 15 + maskNull(this.selectedProcessID);
    result = 37 * result + "orgu".hashCode() * 109 + maskNull(this.selectedUnitID);
    result = 37 * result + "prod".hashCode() * 107 + maskNull(this.selectedProductID);

    return Integer.valueOf(result);
  }

  /**
   * Initializes the component model with lists of all available {@link BusinessProcess}es,
   * {@link BusinessUnit}s and {@link Product}s.
   * 
   * @param bpList The list of available business processes.
   * @param ouList The list of available organizational units.
   * @param prList The list of available products.
   */
  public void initializeFrom(List<BusinessProcess> bpList, List<BusinessUnit> ouList, List<Product> prList) {

    this.processes = bpList;
    this.units = ouList;
    this.products = prList;

    // Store the currently associated elements of the Business Mapping.

    this.selectedProcess = bm.getBusinessProcess();
    this.selectedUnit = bm.getBusinessUnit();
    this.selectedProduct = bm.getProduct();

    this.selectedProcessID = selectedProcess.getId();
    this.selectedUnitID = selectedUnit.getId();
    this.selectedProductID = selectedProduct.getId();
  }

  /**
   * Updates the lists of available {@link BusinessProcess}es, {@link BusinessUnit}s and
   * {@link Product}s.
   * 
   * @param bpList The list of available business processes.
   * @param ouList The list of available organizational units.
   * @param prList The list of available products.
   */
  public void update(List<BusinessProcess> bpList, List<BusinessUnit> ouList, List<Product> prList) {

    this.processes = bpList;
    this.units = ouList;
    this.products = prList;

    setSelectedBusinessProcess();
    setSelectedBusinessUnit();
    setSelectedProduct();
  }

  public void configure(BusinessMapping target) {
    // Nothing to do. 
  }

  private int maskNull(Integer id) {
    return id == null ? -1 : id.intValue();
  }

  /**
   * Sets the reference to the currently selected business process according to the ID transmitted
   * in the request and stored in the field {@link #selectedProcessID}.
   */
  private void setSelectedBusinessProcess() {

    for (BusinessProcess process : processes) {
      if (process.getId().equals(selectedProcessID)) {
        this.selectedProcess = process;
        break;
      }
    }
  }

  /**
   * Sets the reference to the currently selected organizational unit according to the ID
   * transmitted in the request and stored in the field {@link #selectedUnitID}.
   */
  private void setSelectedBusinessUnit() {

    for (BusinessUnit unit : units) {
      if (unit.getId().equals(selectedUnitID)) {
        this.selectedUnit = unit;
        break;
      }
    }
  }

  /**
   * Sets the reference to the currently selected product according to the ID transmitted in the
   * request and stored in the field {@link #selectedProductID}.
   */
  private void setSelectedProduct() {

    for (Product product : products) {
      if (product.getId().equals(selectedProductID)) {
        this.selectedProduct = product;
        break;
      }
    }
  }

}