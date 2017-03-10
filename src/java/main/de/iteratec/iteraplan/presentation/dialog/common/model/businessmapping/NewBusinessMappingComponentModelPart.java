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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.IndexedIdEntityList;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * @param <T>
 *          the building block type from which the business mapping is seen. Currently,
 *          {@link InformationSystemRelease}, {@link Product}, {@link BusinessProcess} and
 *          {@link BusinessUnit} are accepted types.
 * @param <C>
 *          the building block type which is used for clustering the business mapping. Must be
 *          different from <code>T</code>! Currently, {@link InformationSystemRelease},
 *          {@link Product}, {@link BusinessProcess} and {@link BusinessUnit} are accepted types.
 */
public class NewBusinessMappingComponentModelPart<T extends BuildingBlock, C extends BuildingBlock> implements Serializable {

  /** Serialization version. */
  private static final long                             serialVersionUID       = 5461750440861461798L;
  private static final String                           ACTION_MAPPINGS_RESET  = "resetMappings";
  private static final String                           ACTION_MAPPINGS_ADD    = "addMappings";

  /** Back-Reference to the parent component model. */
  private final BusinessMappingsComponentModel<T, C>    existingMappingsModel;

  private final Class<? extends BuildingBlock>          owningBuildingBlockType;

  /**
   * The component mode for this component model.
   */
  private final ComponentMode                           componentMode;

  /** A HTML ID that can be used for functional testing. May be null. */
  private String                                        htmlId;

  /**
   * TOP_LEVEL_NAME is stored here also so AbstractHierarchicalEntity doesn't have to be imported
   * from the JSP
   */
  private String                                        topLevelName           = AbstractHierarchicalEntity.TOP_LEVEL_NAME;

  /** The GUI action to carry out within update(). */
  private String                                        action                 = null;

  private IndexedIdEntityList<InformationSystemRelease> connectedIsrs          = new IndexedIdEntityList<InformationSystemRelease>();
  private IndexedIdEntityList<InformationSystemRelease> availableIsrs          = new IndexedIdEntityList<InformationSystemRelease>();
  private Integer[]                                     isrToAdd               = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
  private Integer[]                                     isrForAdd              = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;

  private IndexedIdEntityList<BusinessProcess>          connectedBps           = new IndexedIdEntityList<BusinessProcess>();
  private IndexedIdEntityList<BusinessProcess>          availableBps           = new IndexedIdEntityList<BusinessProcess>();
  private Integer[]                                     bpToAdd                = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
  private Integer[]                                     bpForAdd               = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;

  private IndexedIdEntityList<BusinessUnit>             connectedBusinessUnits = new IndexedIdEntityList<BusinessUnit>();
  private IndexedIdEntityList<BusinessUnit>             availableBusinessUnits = new IndexedIdEntityList<BusinessUnit>();
  private Integer[]                                     buToAdd                = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
  private Integer[]                                     buForAdd               = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;

  private IndexedIdEntityList<Product>                  connectedProducts      = new IndexedIdEntityList<Product>();
  private IndexedIdEntityList<Product>                  availableProducts      = new IndexedIdEntityList<Product>();
  private Integer[]                                     prToAdd                = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
  private Integer[]                                     prForAdd               = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;

  NewBusinessMappingComponentModelPart(ComponentMode componentMode, String htmlId, BusinessMappingsComponentModel<T, C> existingMappingsModel,
      Class<? extends BuildingBlock> owningBuildingBlockType) {
    this.componentMode = componentMode;
    this.htmlId = htmlId;
    this.existingMappingsModel = existingMappingsModel;
    this.owningBuildingBlockType = owningBuildingBlockType;
  }

  public void initializeFrom(BusinessMappingItems bmiSorted) {
    availableIsrs = new IndexedIdEntityList<InformationSystemRelease>(bmiSorted.getInformationSystemReleases());
    availableBps = new IndexedIdEntityList<BusinessProcess>(bmiSorted.getBusinessProcesses());
    availableBusinessUnits = new IndexedIdEntityList<BusinessUnit>(bmiSorted.getBusinessUnits());
    availableProducts = new IndexedIdEntityList<Product>(bmiSorted.getProducts());
  }

  public void update(BusinessMappingItems bmiAll) {
    resetLists(bmiAll);
    moveItemsToConnectedList();

    if (action == null) {
      return;
    }

    if (ACTION_MAPPINGS_ADD.equals(action)) {
      addBusinessMapping(bmiAll);
    }
    else if (ACTION_MAPPINGS_RESET.equals(action)) {
      resetLists(bmiAll);
    }

    removeConnectedElements();
    resetAction();
  }

  private void moveItemsToConnectedList() {
    addIsrs();
    addBps();
    addBus();
    addPrs();
  }

  private void addBusinessMapping(BusinessMappingItems bmiAll) {
    final BusinessMappingItems bmi = new BusinessMappingItems(connectedIsrs, connectedBps, connectedBusinessUnits, connectedProducts);
    final List<BusinessMapping> alreadyExistingMappings = existingMappingsModel.updateClusterPartsWithNewBusinessMapping(bmi);
    resetLists(bmiAll);

    // if some of the business mappings were already assigned before, re-add them 
    // to the selected list and throw an exception
    for (BusinessMapping bizMapping : alreadyExistingMappings) {
      if (!owningBuildingBlockType.isAssignableFrom(InformationSystemRelease.class)) {
        InformationSystemRelease isr = bizMapping.getInformationSystemRelease();
        availableIsrs.removeWithKey(isr.getId());
        connectedIsrs.addWithKey(isr, isr.getId());
      }

      if (!owningBuildingBlockType.isAssignableFrom(BusinessProcess.class)) {
        BusinessProcess process = bizMapping.getBusinessProcess();
        availableBps.removeWithKey(process.getId());
        connectedBps.addWithKey(process, process.getId());
      }

      if (!owningBuildingBlockType.isAssignableFrom(BusinessUnit.class)) {
        BusinessUnit bu = bizMapping.getBusinessUnit();
        availableBusinessUnits.removeWithKey(bu.getId());
        connectedBusinessUnits.addWithKey(bu, bu.getId());
      }

      if (!owningBuildingBlockType.isAssignableFrom(Product.class)) {
        Product product = bizMapping.getProduct();
        availableProducts.removeWithKey(product.getId());
        connectedProducts.addWithKey(product, product.getId());
      }
    }

    if (!alreadyExistingMappings.isEmpty()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_BUSINESS_MAPPINGS_ONADD);
    }
  }

  /**
   * Reset action in order to have it executed only once.
   */
  private void resetAction() {
    action = null;
  }

  /**
   * Remove all connected elements from the available elements lists, so that no element appears
   * on both sides. this is necessary because these lists have been initialized with data from
   * the database and it is not known inherently which elements are already connected. This
   * method performs its work only on the selected element and thus removes only THIS element.
   * even if there are more connected elements, the newly initialized list will eventually
   * contain all elements BUT the selected one. 
   * 
   */
  private void removeConnectedElements() {
    for (InformationSystemRelease isr : connectedIsrs) {
      availableIsrs.removeWithKey(isr.getId());
    }

    for (BusinessProcess bp : connectedBps) {
      availableBps.removeWithKey(bp.getId());
    }

    for (BusinessUnit bu : connectedBusinessUnits) {
      availableBusinessUnits.removeWithKey(bu.getId());
    }

    for (Product p : connectedProducts) {
      availableProducts.removeWithKey(p.getId());
    }
  }

  /**
   * Moves selected information system releases from the available list to the connected list. Selected systems are
   * identified by the ID array that the browser sent. That array is cleared after systems were moved.
   */
  private void addIsrs() {
    for (Integer id : this.isrToAdd) {
      InformationSystemRelease isr = this.availableIsrs.getWithKey(id);
      availableIsrs.removeWithKey(isr.getId());
      connectedIsrs.addWithKey(isr, isr.getId());
    }

    isrToAdd = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
  }

  /**
   * Moves selected processes from the available list to the connected list. Selected processes are
   * identified by the ID array that the browser sent. That array is cleared after processes were moved.
   */
  private void addBps() {
    for (Integer id : this.bpToAdd) {
      BusinessProcess bp = this.availableBps.getWithKey(id);
      availableBps.removeWithKey(bp.getId());
      connectedBps.addWithKey(bp, bp.getId());
    }

    bpToAdd = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
  }

  /**
   * Moves selected business units from the available list to the connected list. Selected business units are
   * identified by the ID array that the browser sent. That array is cleared after business units were moved.
   */
  private void addBus() {
    for (Integer id : this.buToAdd) {
      BusinessUnit bu = this.availableBusinessUnits.getWithKey(id);
      availableBusinessUnits.removeWithKey(bu.getId());
      connectedBusinessUnits.addWithKey(bu, bu.getId());
    }

    buToAdd = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
  }

  /**
   * Moves selected products from the available list to the connected list. Selected products are
   * identified by the ID array that the browser sent. That array is cleared after products were moved.
   */
  private void addPrs() {
    for (Integer id : this.prToAdd) {
      Product pr = this.availableProducts.getWithKey(id);
      availableProducts.removeWithKey(pr.getId());
      connectedProducts.addWithKey(pr, pr.getId());
    }

    prToAdd = ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
  }

  /**
   * Clears the list of connected BUs, BPs and Products, and re-initializes the available-lists 
   * with the provided arguments 
   */
  private void resetLists(BusinessMappingItems bmi) {
    availableIsrs = new IndexedIdEntityList<InformationSystemRelease>(bmi.getInformationSystemReleases());
    connectedIsrs.clear();

    availableBps = new IndexedIdEntityList<BusinessProcess>(bmi.getBusinessProcesses());
    connectedBps.clear();

    availableBusinessUnits = new IndexedIdEntityList<BusinessUnit>(bmi.getBusinessUnits());
    connectedBusinessUnits.clear();

    availableProducts = new IndexedIdEntityList<Product>(bmi.getProducts());
    connectedProducts.clear();
  }

  public ComponentMode getComponentMode() {
    return componentMode;
  }

  public String getTopLevelName() {
    return topLevelName;
  }

  public String getHtmlId() {
    return htmlId;
  }

  public List<InformationSystemRelease> getAvailableIsrs() {
    return availableIsrs;
  }

  public List<InformationSystemRelease> getConnectedIsrs() {
    return connectedIsrs;
  }

  public List<BusinessProcess> getAvailableBps() {
    return availableBps;
  }

  public List<BusinessProcess> getConnectedBps() {
    return connectedBps;
  }

  public List<BusinessUnit> getAvailableBusinessUnits() {
    return availableBusinessUnits;
  }

  public List<Product> getAvailableProducts() {
    return availableProducts;
  }

  public List<BusinessUnit> getConnectedBusinessUnits() {
    return connectedBusinessUnits;
  }

  public List<Product> getConnectedProducts() {
    return connectedProducts;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  // NEW

  public Integer[] getIsrToAdd() {
    return copy(isrToAdd);
  }

  public Integer[] getBpToAdd() {
    return copy(bpToAdd);
  }

  public Integer[] getBuToAdd() {
    return copy(buToAdd);
  }

  public Integer[] getPrToAdd() {
    return copy(prToAdd);
  }

  public Integer[] getIsrForAdd() {
    return copy(isrForAdd);
  }

  public Integer[] getBpForAdd() {
    return copy(bpForAdd);
  }

  public Integer[] getBuForAdd() {
    return copy(buForAdd);
  }

  public Integer[] getPrForAdd() {
    return copy(prForAdd);
  }

  public void setIsrToAdd(Integer[] isrToAdd) {
    this.isrToAdd = copy(isrToAdd);
  }

  public void setIsrForAdd(Integer[] isrForAdd) {
    this.isrForAdd = copy(isrForAdd);
  }

  public void setBpToAdd(Integer[] bpToAdd) {
    this.bpToAdd = copy(bpToAdd);
  }

  public void setBuToAdd(Integer[] buToAdd) {
    this.buToAdd = copy(buToAdd);
  }

  public void setPrToAdd(Integer[] prToAdd) {
    this.prToAdd = copy(prToAdd);
  }

  public void setBpForAdd(Integer[] bpForAdd) {
    this.bpForAdd = copy(bpForAdd);
  }

  public void setBuForAdd(Integer[] buForAdd) {
    this.buForAdd = copy(buForAdd);
  }

  public void setPrForAdd(Integer[] prForAdd) {
    this.prForAdd = copy(prForAdd);
  }

  private static Integer[] copy(Integer[] original) {
    return (Integer[]) ArrayUtils.clone(original);
  }
}
