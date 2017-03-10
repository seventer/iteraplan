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
 * A business process is a sequence of logical coherent activities, contributing to the added value
 * chain of the company, having a defined start and end, generally repeatable and normally customer
 * oriented. The class inherits equals() and hashCode() from {@link AbstractHierarchicalEntity}.
 */
@Entity
@Audited
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
@Indexed(index = "index.BusinessProcess")
public class BusinessProcess extends AbstractHierarchicalEntity<BusinessProcess> implements BusinessMappingEntity {

  private static final long    serialVersionUID = -123880085707896488L;
  private Set<BusinessDomain>  businessDomains  = Sets.newHashSet();
  private Set<BusinessMapping> businessMappings = Sets.newHashSet();

  public BusinessProcess() {
    // No-arg constructor.
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.BUSINESSPROCESS;
  }

  /**
   * Adds a set of {@link BusinessDomain}s. Updates both sides of the association.
   */
  public void addBusinessDomains(Collection<BusinessDomain> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessDomain bd : set) {
      businessDomains.add(bd);
      bd.getBusinessProcesses().add(this);
    }
  }

  @Override
  public int compareTo(BuildingBlock other) {
    return HierarchyHelper.compareToForOrderedHierarchy(this, (BusinessProcess) other);
  }

  public Set<BusinessDomain> getBusinessDomains() {
    return businessDomains;
  }

  @Override
  public String getI18NKey() {
    return "businessProcess.virtualElement";
  }

  /**
   * Clears all Business Domain associations. Updates both sides of the associations.
   */
  public void removeBusinessDomainRelations() {
    for (BusinessDomain bd : businessDomains) {
      bd.getBusinessObjects().remove(this);
    }
    businessDomains.clear();
  }

  public void setBusinessDomains(Set<BusinessDomain> businessDomains) {
    this.businessDomains = businessDomains;
  }

  /**{@inheritDoc}**/
  public void removeBusinessMappings() {
    for (BusinessMapping bm : businessMappings) {
      bm.setBusinessProcess(null);
    }
    businessMappings.clear();
  }

  /**{@inheritDoc}**/
  public void addBusinessMappings(Set<BusinessMapping> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessMapping bm : set) {
      addBusinessMapping(bm);
    }
  }

  /**{@inheritDoc}**/
  public void addBusinessMapping(BusinessMapping bMapping) {
    Preconditions.checkNotNull(bMapping);
    this.businessMappings.add(bMapping);
    bMapping.setBusinessProcess(this);
  }

  /**{@inheritDoc}**/
  public Set<BusinessMapping> getBusinessMappings() {
    return this.businessMappings;
  }

  /**{@inheritDoc}**/
  public void setBusinessMappings(Set<BusinessMapping> bMappings) {
    this.businessMappings = bMappings;
  }
}