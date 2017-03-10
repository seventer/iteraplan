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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProductMassUpdateType;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.presentation.dialog.Product.ProductComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;


/**
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 */
public class MassUpdateProductComponentModel extends ProductComponentModel implements MassUpdateComponentModel<Product> {

  /** Serialization version. */
  private static final long serialVersionUID                   = -8514847854999459730L;
  private boolean           namePropertyAvailable              = false;
  private boolean           descriptionPropertyAvailable       = false;
  private boolean           consistsOfAssociationAvailable     = false;

  private boolean           businessDomainAssociationAvailable = false;
  private boolean           parentAssociationAvailable         = false;

  public MassUpdateProductComponentModel() {
    super(ComponentMode.EDIT);

    super.getConsistsOfModel().updateHeaderAndFields(new String[] { NAME_LABEL }, new String[] { "name" });

    String[] header = new String[] { "global.name" };
    String[] fields = new String[] { "hierarchicalName" };
    super.getBusinessDomainModel().updateHeaderAndFields(header, fields);
    //super.getElementOfModel().updateHeaderAndFields(header, fields);
  }

  public Map<String, String> getComponentModelAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();
    assignment.put(ProductMassUpdateType.PROPERTY_NAME, "nameComponentModel");
    assignment.put(ProductMassUpdateType.PROPERTY_DESCRIPTION, "descriptionComponentModel");
    assignment.put(ProductMassUpdateType.ASSOCIATION_CHILDREN, "consistsOfComponentModel");
    assignment.put(ProductMassUpdateType.ASSOCIATION_PARENT, "parentComponentModel");
    assignment.put(ProductMassUpdateType.ASSOCIATION_BUSINESSDOMAIN, "businessDomainComponentModel");

    return assignment;
  }

  /**
   * @return Returns a map of getter names, which are used to gain access to the component model by reflection.
   *         See MassUpdateFrontendService.
   */
  public Map<String, String> getAssociationGetMethodAssignment() {
    Map<String, String> assignment = new HashMap<String, String>();

    assignment.put(ProductMassUpdateType.ASSOCIATION_BUSINESSDOMAIN, "getBusinessDomainModel");
    assignment.put(ProductMassUpdateType.ASSOCIATION_PARENT, "getElementOfModel");

    return assignment;
  }

  public void initializeFrom(Product buildingBlock, List<String> properties, List<String> associations) {
    MassUpdateType type = ProductMassUpdateType.getInstance();

    for (String property : properties) {
      if (type.isPropertyIdEqual(property, ProductMassUpdateType.PROPERTY_NAME)) {
        namePropertyAvailable = true;
        super.getNameModel().initializeFrom(buildingBlock);
      }
      if (type.isPropertyIdEqual(property, ProductMassUpdateType.PROPERTY_DESCRIPTION)) {
        descriptionPropertyAvailable = true;
        super.getDescriptionModel().initializeFrom(buildingBlock);
      }
    }

    for (String association : associations) {
      if (association.equals(ProductMassUpdateType.ASSOCIATION_CHILDREN)) {
        consistsOfAssociationAvailable = true;
        super.getConsistsOfModel().initializeFrom(buildingBlock);
      }
      if (association.equals(ProductMassUpdateType.ASSOCIATION_BUSINESSDOMAIN)) {
        businessDomainAssociationAvailable = true;
      }
      if (association.equals(ProductMassUpdateType.ASSOCIATION_PARENT)) {
        parentAssociationAvailable = true;
        super.getElementOfModel().initializeFrom(buildingBlock);
      }
    }
    super.getBusinessDomainModel().initializeFrom(buildingBlock);
  }

  @Override
  public void update() throws IteraplanException {
    if (namePropertyAvailable) {
      getNameComponentModel().update();
    }
    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().update();
    }
    if (consistsOfAssociationAvailable) {
      getConsistsOfComponentModel().update();
    }
    if (businessDomainAssociationAvailable) {
      getBusinessDomainComponentModel().update();
    }
    if (parentAssociationAvailable) {
      getParentComponentModel().update();
    }
  }

  /**
   * Returns the component model for both name and version of the Product
   * 
   * @return The name component model
   */
  public ComponentModel<Product> getNameComponentModel() {
    if (namePropertyAvailable) {
      return super.getNameModel();
    }
    return null;
  }

  /**
   * Returns the component model for the description of the Product
   * 
   * @return The description component model
   */
  public ComponentModel<Product> getDescriptionComponentModel() {
    if (descriptionPropertyAvailable) {
      return super.getDescriptionModel();
    }
    return null;
  }

  /**
   * Returns the component model for the consistsOf association of the Product
   * 
   * @return The description component model
   */
  public ComponentModel<Product> getConsistsOfComponentModel() {
    if (consistsOfAssociationAvailable) {
      return super.getConsistsOfModel();
    }
    return null;
  }

  /**
   * Returns the component model for the BusinessDomain association of the Product
   * 
   * @return The businessDomain component model
   */
  public ComponentModel<Product> getBusinessDomainComponentModel() {
    if (businessDomainAssociationAvailable) {
      return super.getBusinessDomainModel();
    }
    return null;
  }

  /**
   * Returns the component model for the parent of the Product
   * 
   * @return The parent component model
   */
  public ComponentModel<Product> getParentComponentModel() {
    if (parentAssociationAvailable) {
      return super.getElementOfModel();
    }
    return null;
  }

  @Override
  public void configure(Product target) throws IteraplanException {
    if (namePropertyAvailable) {
      getNameComponentModel().configure(target);
    }
    if (descriptionPropertyAvailable) {
      getDescriptionComponentModel().configure(target);
    }
    if (businessDomainAssociationAvailable) {
      getBusinessDomainComponentModel().configure(target);
    }
    if (parentAssociationAvailable) {
      getParentComponentModel().configure(target);
    }
  }
}
