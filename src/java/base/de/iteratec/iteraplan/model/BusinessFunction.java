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

import java.util.Collection;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * A business function may be associated with information system releases, organizational units,
 * business processes as well as business objects. It can be grouped into business domains.
 * Functions itself are hierarchically ordered, i.e. a business function has got exactly one parent
 * (at least the virtual top level element) and may have n children.
 * <p>
 * Table name: bfunc
 */
@Entity
@Audited
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
@Indexed(index = "index.BusinessFunction")
public class BusinessFunction extends AbstractHierarchicalEntity<BusinessFunction> {

  private static final long    serialVersionUID = -3078169747235213228L;
  private Set<BusinessObject>  businessObjects  = Sets.newHashSet();
  private Set<BusinessDomain>  businessDomains  = Sets.newHashSet();
  private Set<InformationSystemRelease> informationSystems = Sets.newHashSet();

  public BusinessFunction() {
    // No-arg constructor.
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.BUSINESSFUNCTION;
  }

  public Set<BusinessObject> getBusinessObjects() {
    return businessObjects;
  }

  public Set<BusinessDomain> getBusinessDomains() {
    return businessDomains;
  }

  public void setBusinessObjects(Set<BusinessObject> businessObjects) {
    this.businessObjects = businessObjects;
  }

  public void setBusinessDomains(Set<BusinessDomain> businessDomains) {
    this.businessDomains = businessDomains;
  }

  /**
   * Adds each element of the given set of {@link BusinessObject}s. Updates both sides of the
   * association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addBusinessObjects(Collection<BusinessObject> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessObject bo : set) {
      businessObjects.add(bo);
      bo.getBusinessFunctions().add(this);
    }
  }

  /**
   * Adds each element of the given set of {@link BusinessDomain}s. Updates both sides of the
   * association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addBusinessDomains(Collection<BusinessDomain> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessDomain fd : set) {
      businessDomains.add(fd);
      fd.getBusinessFunctions().add(this);
    }
  }

  /**
   * Adds each element of the given set of {@link InformationSystemRelease}s. Updates both sides of the
   * association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addInformationSystems(Collection<InformationSystemRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (InformationSystemRelease isr : set) {
      informationSystems.add(isr);
      isr.getBusinessFunctions().add(this);
    }
  }

  /**
   * Clears all business objects.
   */
  public void removeBusinessObjects() {
    for (BusinessObject bo : businessObjects) {
      bo.getBusinessFunctions().remove(this);
    }
    businessObjects.clear();
  }

  /**
   * Clears all business domains.
   */
  public void removeBusinessDomains() {
    for (BusinessDomain fd : businessDomains) {
      fd.getBusinessFunctions().remove(this);
    }
    businessDomains.clear();
  }

  /**
   * Clears all information systems.
   */
  public void removeInformationSystems() {
    for (InformationSystemRelease isr : informationSystems) {
      isr.getBusinessFunctions().remove(this);
    }
    informationSystems.clear();
  }

  /**
   * Clears all associations.
   */
  public void removeRelations() {
    for (BusinessObject bo : businessObjects) {
      bo.getBusinessFunctions().remove(this);
    }
    businessObjects.clear();

    for (BusinessDomain fd : businessDomains) {
      fd.getBusinessFunctions().remove(this);
    }
    businessDomains.clear();
    for (InformationSystemRelease isr : informationSystems) {
      isr.getBusinessFunctions().remove(this);
    }
    informationSystems.clear();
  }

  @Override
  public int compareTo(BuildingBlock o) {
    return HierarchyHelper.compareToForOrderedHierarchy(this, (BusinessFunction) o);
  }

  @Override
  public String getI18NKey() {
    return "businessFunction.virtualElement.description";
  }

  /**
   * @return informationSystems the informationSystems
   */
  public Set<InformationSystemRelease> getInformationSystems() {
    return informationSystems;
  }

  public void setInformationSystems(Set<InformationSystemRelease> informationSystems) {
    this.informationSystems = informationSystems;
  }
}