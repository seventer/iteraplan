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

import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class InfrastructureElementChangeset extends HierarchicalEntityChangeset implements UsageTypeChangeset<InfrastructureElement> {

  private List<InformationSystemRelease>  informationSystemReleasesAdded    = Lists.newArrayList();
  private List<InformationSystemRelease>  informationSystemReleasesRemoved  = Lists.newArrayList();

  private List<TechnicalComponentRelease> technicalComponentReleasesAdded   = Lists.newArrayList();
  private List<TechnicalComponentRelease> technicalComponentReleasesRemoved = Lists.newArrayList();
  private List<InfrastructureElement>     baseComponentsAdded               = Lists.newArrayList();
  private List<InfrastructureElement>     baseComponentsRemoved             = Lists.newArrayList();
  private List<InfrastructureElement>     parentComponentsAdded             = Lists.newArrayList();
  private List<InfrastructureElement>     parentComponentsRemoved           = Lists.newArrayList();

  /**
   * Default constructor.
   * @param bbId
   * @param tobb
   * @param author
   * @param timestamp
   */
  public InfrastructureElementChangeset(Integer bbId, TypeOfBuildingBlock tobb, String author, DateTime timestamp) {
    super(bbId, tobb, author, timestamp);
  }

  @Override
  public boolean isRelationsChanged() {
    return super.isRelationsChanged() || !Objects.equal(getInformationSystemReleasesAdded(), getInformationSystemReleasesRemoved())
        || !Objects.equal(getTechnicalComponentReleasesAdded(), getTechnicalComponentReleasesRemoved())
        || !Objects.equal(getBaseComponentsAdded(), getBaseComponentsRemoved())
        || !Objects.equal(getParentComponentsAdded(), getParentComponentsRemoved());
  }

  public List<InformationSystemRelease> getInformationSystemReleasesAdded() {
    return informationSystemReleasesAdded;
  }

  public void setInformationSystemReleasesAdded(List<InformationSystemRelease> addedInformationSystemReleases) {
    this.informationSystemReleasesAdded = addedInformationSystemReleases;
  }

  public List<InformationSystemRelease> getInformationSystemReleasesRemoved() {
    return informationSystemReleasesRemoved;
  }

  public void setInformationSystemReleasesRemoved(List<InformationSystemRelease> removedInformationSystemReleases) {
    this.informationSystemReleasesRemoved = removedInformationSystemReleases;
  }

  public List<TechnicalComponentRelease> getTechnicalComponentReleasesAdded() {
    return technicalComponentReleasesAdded;
  }

  public void setTechnicalComponentReleasesAdded(List<TechnicalComponentRelease> addedTechnicalComponentReleases) {
    this.technicalComponentReleasesAdded = addedTechnicalComponentReleases;
  }

  public List<TechnicalComponentRelease> getTechnicalComponentReleasesRemoved() {
    return technicalComponentReleasesRemoved;
  }

  public void setTechnicalComponentReleasesRemoved(List<TechnicalComponentRelease> removedTechnicalComponentReleases) {
    this.technicalComponentReleasesRemoved = removedTechnicalComponentReleases;
  }

  public List<InfrastructureElement> getBaseComponentsAdded() {
    return baseComponentsAdded;
  }

  public void setBaseComponentsAdded(List<InfrastructureElement> addedBaseComponents) {
    this.baseComponentsAdded = addedBaseComponents;
  }

  public List<InfrastructureElement> getBaseComponentsRemoved() {
    return baseComponentsRemoved;
  }

  public void setBaseComponentsRemoved(List<InfrastructureElement> removedBaseComponents) {
    this.baseComponentsRemoved = removedBaseComponents;
  }

  public List<InfrastructureElement> getParentComponentsAdded() {
    return parentComponentsAdded;
  }

  public void setParentComponentsAdded(List<InfrastructureElement> parentComponentsAdded) {
    this.parentComponentsAdded = parentComponentsAdded;
  }

  public List<InfrastructureElement> getParentComponentsRemoved() {
    return parentComponentsRemoved;
  }

  public void setParentComponentsRemoved(List<InfrastructureElement> parentComponentsRemoved) {
    this.parentComponentsRemoved = parentComponentsRemoved;
  }

}
