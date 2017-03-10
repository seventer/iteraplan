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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class TechnicalComponentReleaseChangeset extends HistoryBBChangeset implements RuntimePeriodCarryingChangeset, ReleaseSequenceTypeChangeset<TechnicalComponentRelease>, UsageTypeChangeset<TechnicalComponentRelease> {

  private String                          statusTo;
  private String                          statusFrom;

  private Boolean                         availableForInterfacesTo;
  private Boolean                         availableForInterfacesFrom;

  private DateTime                        runtimeStartTo;
  private DateTime                        runtimeStartFrom;

  private DateTime                        runtimeEndTo;
  private DateTime                        runtimeEndFrom;

  private List<TechnicalComponentRelease> predecessorsAdded                = new ArrayList<TechnicalComponentRelease>();
  private List<TechnicalComponentRelease> predecessorsRemoved              = new ArrayList<TechnicalComponentRelease>();

  private List<TechnicalComponentRelease> successorsAdded                  = new ArrayList<TechnicalComponentRelease>();
  private List<TechnicalComponentRelease> successorsRemoved                = new ArrayList<TechnicalComponentRelease>();

  private List<TechnicalComponentRelease> baseComponentsAdded              = new ArrayList<TechnicalComponentRelease>();
  private List<TechnicalComponentRelease> baseComponentsRemoved            = new ArrayList<TechnicalComponentRelease>();

  private List<TechnicalComponentRelease> parentComponentsAdded            = new ArrayList<TechnicalComponentRelease>();
  private List<TechnicalComponentRelease> parentComponentsRemoved          = new ArrayList<TechnicalComponentRelease>();

  private List<ArchitecturalDomain>       architecturalDomainsAdded        = new ArrayList<ArchitecturalDomain>();
  private List<ArchitecturalDomain>       architecturalDomainsRemoved      = new ArrayList<ArchitecturalDomain>();

  private List<InfrastructureElement>     infrastructureElementsAdded      = new ArrayList<InfrastructureElement>();
  private List<InfrastructureElement>     infrastructureElementsRemoved    = new ArrayList<InfrastructureElement>();

  private List<InformationSystemRelease>  informationSystemReleasesAdded   = new ArrayList<InformationSystemRelease>();
  private List<InformationSystemRelease>  informationSystemReleasesRemoved = new ArrayList<InformationSystemRelease>();

  private List<InformationSystemInterface> interfacesAdded                  = new ArrayList<InformationSystemInterface>();
  private List<InformationSystemInterface> interfacesRemoved                = new ArrayList<InformationSystemInterface>();

  /**
   * Default constructor.
   * @param bbId
   * @param tobb
   * @param author
   * @param timestamp
   */
  public TechnicalComponentReleaseChangeset(Integer bbId, TypeOfBuildingBlock tobb, String author, DateTime timestamp) {
    super(bbId, tobb, author, timestamp);
  }

  @Override
  public boolean isRelationsChanged() {
    return super.isRelationsChanged()
        || ! Objects.equal(getArchitecturalDomainsAdded(), getArchitecturalDomainsRemoved())
        || ! Objects.equal(getBaseComponentsAdded(), getBaseComponentsRemoved())
        || ! Objects.equal(getParentComponentsAdded(), getParentComponentsRemoved())
        || ! Objects.equal(getInformationSystemReleasesAdded(), getInformationSystemReleasesRemoved())
        || ! Objects.equal(getInfrastructureElementsAdded(), getInfrastructureElementsRemoved())
        || ! Objects.equal(getPredecessorsAdded(), getPredecessorsRemoved())
 || !Objects.equal(getSuccessorsAdded(), getSuccessorsRemoved())
        || !Objects.equal(getInterfacesAdded(), getInterfacesRemoved());
  }

  @Override
  public boolean isHierarchicalType() {
    return false;
  }

  public List<TechnicalComponentRelease> getPredecessorsAdded() {
    return predecessorsAdded;
  }

  public void setPredecessorsAdded(List<TechnicalComponentRelease> addedPredecessors) {
    this.predecessorsAdded = addedPredecessors;
  }

  public List<TechnicalComponentRelease> getPredecessorsRemoved() {
    return predecessorsRemoved;
  }

  public void setPredecessorsRemoved(List<TechnicalComponentRelease> removedPredecessors) {
    this.predecessorsRemoved = removedPredecessors;
  }

  public void setSuccessorsRemoved(List<TechnicalComponentRelease> successorsRemoved) {
    this.successorsRemoved = successorsRemoved;
  }

  public List<TechnicalComponentRelease> getSuccessorsRemoved() {
    return successorsRemoved;
  }

  public void setSuccessorsAdded(List<TechnicalComponentRelease> successorsAdded) {
    this.successorsAdded = successorsAdded;
  }

  public List<TechnicalComponentRelease> getSuccessorsAdded() {
    return successorsAdded;
  }

  public List<TechnicalComponentRelease> getBaseComponentsAdded() {
    return baseComponentsAdded;
  }

  public void setBaseComponentsAdded(List<TechnicalComponentRelease> addedBaseComponents) {
    this.baseComponentsAdded = addedBaseComponents;
  }

  public List<TechnicalComponentRelease> getBaseComponentsRemoved() {
    return baseComponentsRemoved;
  }

  public void setBaseComponentsRemoved(List<TechnicalComponentRelease> removedBaseComponents) {
    this.baseComponentsRemoved = removedBaseComponents;
  }

  public void setParentComponentsRemoved(List<TechnicalComponentRelease> parentComponentsRemoved) {
    this.parentComponentsRemoved = parentComponentsRemoved;
  }

  public List<TechnicalComponentRelease> getParentComponentsRemoved() {
    return parentComponentsRemoved;
  }

  public void setParentComponentsAdded(List<TechnicalComponentRelease> parentComponentsAdded) {
    this.parentComponentsAdded = parentComponentsAdded;
  }

  public List<TechnicalComponentRelease> getParentComponentsAdded() {
    return parentComponentsAdded;
  }

  public List<ArchitecturalDomain> getArchitecturalDomainsAdded() {
    return architecturalDomainsAdded;
  }

  public void setArchitecturalDomainsAdded(List<ArchitecturalDomain> addedArchitecturalDomains) {
    this.architecturalDomainsAdded = addedArchitecturalDomains;
  }

  public List<ArchitecturalDomain> getArchitecturalDomainsRemoved() {
    return architecturalDomainsRemoved;
  }

  public void setArchitecturalDomainsRemoved(List<ArchitecturalDomain> removedArchitecturalDomains) {
    this.architecturalDomainsRemoved = removedArchitecturalDomains;
  }

  public List<InfrastructureElement> getInfrastructureElementsAdded() {
    return infrastructureElementsAdded;
  }

  public void setInfrastructureElementsAdded(List<InfrastructureElement> addedInfrastructureElements) {
    this.infrastructureElementsAdded = addedInfrastructureElements;
  }

  public List<InfrastructureElement> getInfrastructureElementsRemoved() {
    return infrastructureElementsRemoved;
  }

  public void setInfrastructureElementsRemoved(List<InfrastructureElement> removedInfrastructureElements) {
    this.infrastructureElementsRemoved = removedInfrastructureElements;
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

  public List<InformationSystemInterface> getInterfacesAdded() {
    return interfacesAdded;
  }

  public void setInterfacesAdded(List<InformationSystemInterface> addedInformationSystemInterfaces) {
    this.interfacesAdded = addedInformationSystemInterfaces;
  }

  public List<InformationSystemInterface> getInterfacesRemoved() {
    return interfacesRemoved;
  }

  public void setInterfacesRemoved(List<InformationSystemInterface> removedInformationSystemInterfaces) {
    this.interfacesRemoved = removedInformationSystemInterfaces;
  }

  @Override
  public boolean isBasicPropertiesChanged() {
    return super.isBasicPropertiesChanged()
        || !StringUtils.equals(getStatusTo(), getStatusFrom())
        || !Objects.equal(getRuntimeStartAdded(), getRuntimeStartRemoved())
        || !Objects.equal(getRuntimeEndAdded(), getRuntimeEndRemoved())
        || !Objects.equal(getAvailableForInterfacesTo(), getAvailableForInterfacesFrom());
  }

  public Boolean getAvailableForInterfacesTo() {
    return availableForInterfacesTo;
  }

  public void setAvailableForInterfacesTo(Boolean availableForInterfacesTo) {
    this.availableForInterfacesTo = availableForInterfacesTo;
  }

  public Boolean getAvailableForInterfacesFrom() {
    return availableForInterfacesFrom;
  }

  public void setAvailableForInterfacesFrom(Boolean availableForInterfacesFrom) {
    this.availableForInterfacesFrom = availableForInterfacesFrom;
  }

  /**
   * @return The message key for the new status.
   */
  public String getStatusTo() {
    return statusTo;
  }

  public void setStatusTo(String statusTo) {
    this.statusTo = statusTo;
  }

  /**
   * @return The message key for the old status.
   */
  public String getStatusFrom() {
    return statusFrom;
  }

  public void setStatusFrom(String statusFrom) {
    this.statusFrom = statusFrom;
  }

  public DateTime getRuntimeStartAdded() {
    return runtimeStartTo;
  }

  public void setRuntimeStartAdded(Date runtimeStartTo) {
    this.runtimeStartTo = new DateTime(runtimeStartTo);
  }

  public void setRuntimeStartAdded(DateTime runtimeStartTo) {
    this.runtimeStartTo = runtimeStartTo;
  }

  public DateTime getRuntimeStartRemoved() {
    return runtimeStartFrom;
  }

  public void setRuntimeStartRemoved(Date runtimeStartFrom) {
    this.runtimeStartFrom = new DateTime(runtimeStartFrom);
  }

  public void setRuntimeStartRemoved(DateTime runtimeStartFrom) {
    this.runtimeStartFrom = runtimeStartFrom;
  }

  public DateTime getRuntimeEndAdded() {
    return runtimeEndTo;
  }

  public void setRuntimeEndAdded(Date runtimeEndTo) {
    this.runtimeEndTo = new DateTime(runtimeEndTo);
  }

  public void setRuntimeEndAdded(DateTime runtimeEndTo) {
    this.runtimeEndTo = runtimeEndTo;
  }

  public DateTime getRuntimeEndRemoved() {
    return runtimeEndFrom;
  }

  public void setRuntimeEndRemoved(Date runtimeEndFrom) {
    this.runtimeEndFrom = new DateTime(runtimeEndFrom);
  }

  public void setRuntimeEndRemoved(DateTime runtimeEndFrom) {
    this.runtimeEndFrom = runtimeEndFrom;
  }

  public boolean isRuntimeHasChanged() {
    return getRuntimeStartAdded() != null || getRuntimeStartRemoved() != null
        || getRuntimeEndAdded() != null || getRuntimeEndRemoved() != null;
  }

  public String getRuntimeStartRemovedAsString() {
    return DateUtils.formatAsStringNotNull(getRuntimeStartRemoved());
  }

  public String getRuntimeStartAddedAsString() {
    return DateUtils.formatAsStringNotNull(getRuntimeStartAdded());
  }

  public String getRuntimeEndRemovedAsString() {
    return DateUtils.formatAsStringNotNull(getRuntimeEndRemoved());
  }

  public String getRuntimeEndAddedAsString() {
    return DateUtils.formatAsStringNotNull(getRuntimeEndAdded());
  }

  public boolean hasNoticeableChanges() {
    return super.hasNoticeableChanges() || isRuntimeHasChanged();
  }

}