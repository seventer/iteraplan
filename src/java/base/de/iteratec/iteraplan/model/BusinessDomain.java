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
package de.iteratec.iteraplan.model;

import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;

import java.util.Collection;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * A business domain is used to group business functions, business processes, business objects as
 * well as products. Domains itself are hierarchically ordered, i.e. a business domain has got
 * exactly one parent (at least the virtual top level element) and may have n children.The class
 * inherits equals() and hashCode() from {@link AbstractHierarchicalEntity}.
 */
@Entity
@Audited
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
@Indexed(index = "index.BusinessDomain")
public class BusinessDomain extends AbstractHierarchicalEntity<BusinessDomain> {

  private static final long     serialVersionUID  = -6329711408765560023L;
  private Set<BusinessFunction> businessFunctions = hashSet();
  private Set<BusinessProcess>  businessProcesses = hashSet();
  private Set<BusinessObject>   businessObjects   = hashSet();
  private Set<Product>          products          = hashSet();
  private Set<BusinessUnit>     businessUnits     = hashSet();

  public BusinessDomain() {
    // No-arg constructor.
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.BUSINESSDOMAIN;
  }

  public Set<BusinessFunction> getBusinessFunctions() {
    return businessFunctions;
  }

  public Set<BusinessProcess> getBusinessProcesses() {
    return businessProcesses;
  }

  public Set<BusinessObject> getBusinessObjects() {
    return businessObjects;
  }

  public Set<Product> getProducts() {
    return products;
  }

  public Set<BusinessUnit> getBusinessUnits() {
    return businessUnits;
  }

  public void setBusinessFunctions(Set<BusinessFunction> businessFunctions) {
    this.businessFunctions = businessFunctions;
  }

  public void setBusinessProcesses(Set<BusinessProcess> businessProcesses) {
    this.businessProcesses = businessProcesses;
  }

  public void setBusinessObjects(Set<BusinessObject> businessObjects) {
    this.businessObjects = businessObjects;
  }

  public void setProducts(Set<Product> products) {
    this.products = products;
  }

  public void setBusinessUnits(Set<BusinessUnit> businessUnits) {
    this.businessUnits = businessUnits;
  }

  /**
   * Adds each element of the given set of {@link BusinessFunction}s. Updates both sides of the
   * association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addBusinessFunctions(Collection<BusinessFunction> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessFunction bf : set) {
      businessFunctions.add(bf);
      bf.getBusinessDomains().add(this);
    }
  }

  /**
   * Adds each element of the given set of {@link BusinessProcess}s. Updates both sides of the
   * association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addBusinessProcesses(Collection<BusinessProcess> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessProcess bp : set) {
      businessProcesses.add(bp);
      bp.getBusinessDomains().add(this);
    }
  }

  /**
   * Adds each element of the given set of {@link BusinessObject}s. Updates both sides of the
   * association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addBusinessObjects(Collection<BusinessObject> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessObject bo : set) {
      businessObjects.add(bo);
      bo.getBusinessDomains().add(this);
    }
  }

  /**
   * Adds each element of the given set of {@link Product}s. Updates both sides of the association.
   * Neither the set nor an element in the set must be {@code null}.
   */
  public void addProducts(Collection<Product> set) {
    Preconditions.checkContentsNotNull(set);
    for (Product p : set) {
      products.add(p);
      p.getBusinessDomains().add(this);
    }
  }

  /**
   * Adds each element of the given set of {@link BusinessUnit}s. Updates both sides of the
   * association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addBusinessUnits(Collection<BusinessUnit> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessUnit bu : set) {
      businessUnits.add(bu);
      bu.getBusinessDomains().add(this);
    }
  }

  /**
   * Clears the associated business functions.
   */
  public void removeBusinessFunctions() {
    for (BusinessFunction elem : businessFunctions) {
      elem.getBusinessDomains().remove(this);
    }
    businessFunctions.clear();
  }

  /**
   * Clears the associated business processes.
   */
  public void removeBusinessProcesses() {
    for (BusinessProcess elem : businessProcesses) {
      elem.getBusinessDomains().remove(this);
    }
    businessProcesses.clear();
  }

  /**
   * Clears the associated business objects.
   */
  public void removeBusinessObjects() {
    for (BusinessObject elem : businessObjects) {
      elem.getBusinessDomains().remove(this);
    }
    businessObjects.clear();
  }

  /**
   * Clears the associated products.
   */
  public void removeProducts() {
    for (Product elem : products) {
      elem.getBusinessDomains().remove(this);
    }
    products.clear();
  }

  /**
   * Clears the associated business units.
   */
  public void removeBusinessUnits() {
    for (BusinessUnit bu : businessUnits) {
      bu.getBusinessDomains().remove(this);
    }
    businessUnits.clear();
  }

  /**
   * Clears all associations.
   */
  public void removeRelations() {
    // Clears the associated business functions.
    for (BusinessFunction elem : businessFunctions) {
      elem.getBusinessDomains().remove(this);
    }
    businessFunctions.clear();

    // Clears the associated business processes.
    for (BusinessProcess elem : businessProcesses) {
      elem.getBusinessDomains().remove(this);
    }
    businessProcesses.clear();

    // Clears the associated business objects.
    for (BusinessObject elem : businessObjects) {
      elem.getBusinessDomains().remove(this);
    }
    businessObjects.clear();

    // Clears the associated products.
    for (Product elem : products) {
      elem.getBusinessDomains().remove(this);
    }
    products.clear();

    // Clears the associated business units.
    for (BusinessUnit bu : businessUnits) {
      bu.getBusinessDomains().remove(this);
    }
    businessUnits.clear();
  }

  @Override
  public int compareTo(BuildingBlock o) {
    return HierarchyHelper.compareToForOrderedHierarchy(this, (BusinessDomain) o);
  }

  @Override
  public String getI18NKey() {
    return "businessDomain.virtualElement";
  }

}