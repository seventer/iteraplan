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
package de.iteratec.iteraplan.diffs.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class BusinessFunctionChangeset extends HierarchicalEntityChangeset {

  private List<BusinessObject>   businessObjectsAdded   = new ArrayList<BusinessObject>();
  private List<BusinessObject>   businessObjectsRemoved = new ArrayList<BusinessObject>();

  private List<BusinessDomain>   businessDomainsAdded   = new ArrayList<BusinessDomain>();
  private List<BusinessDomain>   businessDomainsRemoved = new ArrayList<BusinessDomain>();

  private List<InformationSystemRelease> informationSystemAdded   = new ArrayList<InformationSystemRelease>();
  private List<InformationSystemRelease> informationSystemRemoved = new ArrayList<InformationSystemRelease>();
  /**
   * Default constructor.
   * @param bbId
   * @param tobb
   * @param author
   * @param timestamp
   */
  public BusinessFunctionChangeset(Integer bbId, TypeOfBuildingBlock tobb, String author, DateTime timestamp) {
    super(bbId, tobb, author, timestamp);
  }

  @Override
  public boolean isRelationsChanged() {
    return super.isRelationsChanged()
        || ! Objects.equal(getBusinessDomainsAdded(), getBusinessDomainsRemoved())
        || !Objects.equal(getBusinessObjectsAdded(), getBusinessObjectsRemoved())
        || !Objects.equal(getInformationSystemAdded(), getInformationSystemRemoved());
  }

  public List<BusinessObject> getBusinessObjectsAdded() {
    return businessObjectsAdded;
  }

  public void setBusinessObjectsAdded(List<BusinessObject> addedBusinessObjects) {
    this.businessObjectsAdded = addedBusinessObjects;
  }

  public List<BusinessObject> getBusinessObjectsRemoved() {
    return businessObjectsRemoved;
  }

  public void setBusinessObjectsRemoved(List<BusinessObject> removedBusinessObjects) {
    this.businessObjectsRemoved = removedBusinessObjects;
  }

  public List<BusinessDomain> getBusinessDomainsAdded() {
    return businessDomainsAdded;
  }

  public void setBusinessDomainsAdded(List<BusinessDomain> addedBusinessDomains) {
    this.businessDomainsAdded = addedBusinessDomains;
  }

  public List<BusinessDomain> getBusinessDomainsRemoved() {
    return businessDomainsRemoved;
  }

  public void setBusinessDomainsRemoved(List<BusinessDomain> removedBusinessDomains) {
    this.businessDomainsRemoved = removedBusinessDomains;
  }

  /**
   * @return informationSystemRemoved the informationSystemRemoved
   */
  public List<InformationSystemRelease> getInformationSystemRemoved() {
    return informationSystemRemoved;
  }

  public void setInformationSystemRemoved(List<InformationSystemRelease> informationSystemRemoved) {
    this.informationSystemRemoved = informationSystemRemoved;
  }

  /**
   * @return informationSystemAdded the informationSystemAdded
   */
  public List<InformationSystemRelease> getInformationSystemAdded() {
    return informationSystemAdded;
  }

  public void setInformationSystemAdded(List<InformationSystemRelease> informationSystemAdded) {
    this.informationSystemAdded = informationSystemAdded;
  }
}
