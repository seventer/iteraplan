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
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class InformationSystemReleaseChangeset extends HistoryBBChangeset implements
RuntimePeriodCarryingChangeset, ReleaseSequenceTypeChangeset<InformationSystemRelease>, UsageTypeChangeset<InformationSystemRelease> {

  private String                          statusTo;
  private String                          statusFrom;

  private String                          versionFrom;
  private String                          versionTo;

  private DateTime                        runtimeStartAdded;
  private DateTime                        runtimeStartRemoved;

  private DateTime                        runtimeEndAdded;
  private DateTime                        runtimeEndRemoved;

  private InformationSystemRelease        parentTo;
  private InformationSystemRelease        parentFrom;

  private List<InformationSystemRelease>  childrenAdded                     = new ArrayList<InformationSystemRelease>();
  private List<InformationSystemRelease>  childrenRemoved                   = new ArrayList<InformationSystemRelease>();

  private List<InformationSystemRelease>  predecessorsAdded                 = new ArrayList<InformationSystemRelease>();
  private List<InformationSystemRelease>  predecessorsRemoved               = new ArrayList<InformationSystemRelease>();

  private List<InformationSystemRelease>  successorsAdded                   = new ArrayList<InformationSystemRelease>();
  private List<InformationSystemRelease>  successorsRemoved                 = new ArrayList<InformationSystemRelease>();

  private List<InformationSystemRelease>  baseComponentsAdded               = new ArrayList<InformationSystemRelease>();
  private List<InformationSystemRelease>  baseComponentsRemoved             = new ArrayList<InformationSystemRelease>();

  private List<InformationSystemRelease>  parentComponentsAdded             = new ArrayList<InformationSystemRelease>();
  private List<InformationSystemRelease>  parentComponentsRemoved           = new ArrayList<InformationSystemRelease>();

  private List<InformationSystemDomain>   informationSystemDomainsAdded     = new ArrayList<InformationSystemDomain>();
  private List<InformationSystemDomain>   informationSystemDomainsRemoved   = new ArrayList<InformationSystemDomain>();

  private List<TechnicalComponentRelease> technicalComponentReleasesAdded   = new ArrayList<TechnicalComponentRelease>();
  private List<TechnicalComponentRelease> technicalComponentReleasesRemoved = new ArrayList<TechnicalComponentRelease>();

  private List<InfrastructureElement>     infrastructureElementsAdded       = new ArrayList<InfrastructureElement>();
  private List<InfrastructureElement>     infrastructureElementsRemoved     = new ArrayList<InfrastructureElement>();

  private List<Project>                   projectsAdded                     = new ArrayList<Project>();
  private List<Project>                   projectsRemoved                   = new ArrayList<Project>();

  private List<BusinessObject>            businessObjectsAdded              = new ArrayList<BusinessObject>();
  private List<BusinessObject>            businessObjectsRemoved            = new ArrayList<BusinessObject>();

  private List<BusinessFunction>          businessFunctionsAdded            = new ArrayList<BusinessFunction>();
  private List<BusinessFunction>          businessFunctionsRemoved          = new ArrayList<BusinessFunction>();

  private List<BusinessMapping>           businessMappingsAdded             = new ArrayList<BusinessMapping>();
  private List<BusinessMapping>           businessMappingsRemoved           = new ArrayList<BusinessMapping>();

  private List<InformationSystemInterface> interfaceAdded                    = new ArrayList<InformationSystemInterface>();
  private List<InformationSystemInterface> interfaceRemoved                  = new ArrayList<InformationSystemInterface>();

  /**
   * Default constructor.
   * @param bbId
   * @param tobb
   * @param author
   * @param timestamp
   */
  public InformationSystemReleaseChangeset(Integer bbId, TypeOfBuildingBlock tobb, String author, DateTime timestamp) {
    super(bbId, tobb, author, timestamp);
  }

  @Override
  public boolean isRelationsChanged() {
    return super.isRelationsChanged()
        || ! Objects.equal(getBaseComponentsAdded(), getBaseComponentsRemoved())
        || ! Objects.equal(getParentComponentsAdded(), getParentComponentsRemoved())
        || ! Objects.equal(getBusinessFunctionsAdded(), getBusinessFunctionsRemoved())
        || ! Objects.equal(getBusinessMappingsAdded(), getBusinessMappingsRemoved())
        || ! Objects.equal(getBusinessObjectsAdded(), getBusinessObjectsRemoved())
        || ! Objects.equal(getInformationSystemDomainsAdded(), getInformationSystemDomainsRemoved())
        || ! Objects.equal(getInfrastructureElementsAdded(), getInfrastructureElementsRemoved())
        || ! Objects.equal(getPredecessorsAdded(), getPredecessorsRemoved())
        || ! Objects.equal(getSuccessorsAdded(), getSuccessorsRemoved())
        || ! Objects.equal(getProjectsAdded(), getProjectsRemoved())
        || !Objects.equal(getTechnicalComponentReleasesAdded(), getTechnicalComponentReleasesRemoved())
        || !Objects.equal(getInterfaceAdded(), getInterfaceRemoved());
  }

  @Override
  public boolean isHierarchicalType() {
    return true;
  }

  public String getParentElementLabelKey() {
    String bbtStr = getTypeOfBuildingBlock().toString();
    return bbtStr.replace(".singular", ".parent");
  }

  public String getChildElementsLabelKey() {
    String bbtStr = getTypeOfBuildingBlock().toString();
    return bbtStr.replace(".singular", ".children");
  }

  public String getVersionFrom() {
    return versionFrom;
  }

  public void setVersionFrom(String versionFrom) {
    this.versionFrom = versionFrom;
  }

  public String getVersionTo() {
    return versionTo;
  }

  public void setVersionTo(String versionTo) {
    this.versionTo = versionTo;
  }

  public boolean isChildrenChanged() {
    return !(getChildrenAdded().isEmpty() && getChildrenRemoved().isEmpty());
  }

  /**
   * @return if parent changed
   * 
   * parentTo/parentFrom might actually be null (in special case of an ISR parent)
   */
  public boolean isParentChanged() {
    return !StringUtils.equals(getParentFromName(), getParentToName());
  }

  public InformationSystemRelease getParentFrom() {
    return parentFrom;
  }

  public InformationSystemRelease getParentTo() {
    return parentTo;
  }

  public void setParentFrom(InformationSystemRelease parentFrom) {
    this.parentFrom = parentFrom;
  }

  public void setParentTo(InformationSystemRelease parentTo) {
    this.parentTo = parentTo;
  }

  /**
   * @return parent's name or "" if none
   */
  public String getParentFromName() {
    return getNameOfBBOrEmpty(parentFrom);
  }

  /**
   * @return parent's name or "" if none
   */
  public String getParentToName() {
    return getNameOfBBOrEmpty(parentTo);
  }

  public List<InformationSystemRelease> getChildrenAdded() {
    return childrenAdded;
  }

  public void setChildrenAdded(List<InformationSystemRelease> childrenAdded) {
    this.childrenAdded = childrenAdded;
  }

  public List<InformationSystemRelease> getChildrenRemoved() {
    return childrenRemoved;
  }

  public void setChildrenRemoved(List<InformationSystemRelease> childrenRemoved) {
    this.childrenRemoved = childrenRemoved;
  }

  public List<String> getChildrenAddedNames() {
    List<String> childrenNames = new ArrayList<String>();
    for (BuildingBlock curChild : childrenAdded) {
      childrenNames.add(curChild.getNonHierarchicalName());
    }

    return childrenNames;
  }

  public List<String> getChildrenRemovedNames() {
    List<String> childrenNames = new ArrayList<String>();
    for (BuildingBlock curChild : childrenRemoved) {
      childrenNames.add(curChild.getNonHierarchicalName());
    }

    return childrenNames;
  }

  public List<InformationSystemRelease> getPredecessorsAdded() {
    return predecessorsAdded;
  }

  public void setPredecessorsAdded(List<InformationSystemRelease> addedPredecessors) {
    this.predecessorsAdded = addedPredecessors;
  }

  public List<InformationSystemRelease> getPredecessorsRemoved() {
    return predecessorsRemoved;
  }

  public void setPredecessorsRemoved(List<InformationSystemRelease> removedPredecessors) {
    this.predecessorsRemoved = removedPredecessors;
  }

  public List<InformationSystemRelease> getSuccessorsAdded() {
    return successorsAdded;
  }

  public void setSuccessorsAdded(List<InformationSystemRelease> successorsAdded) {
    this.successorsAdded = successorsAdded;
  }

  public List<InformationSystemRelease> getSuccessorsRemoved() {
    return successorsRemoved;
  }

  public void setSuccessorsRemoved(List<InformationSystemRelease> successorsRemoved) {
    this.successorsRemoved = successorsRemoved;
  }

  public List<InformationSystemRelease> getBaseComponentsAdded() {
    return baseComponentsAdded;
  }

  public void setBaseComponentsAdded(List<InformationSystemRelease> addedBaseComponents) {
    this.baseComponentsAdded = addedBaseComponents;
  }

  public List<InformationSystemRelease> getBaseComponentsRemoved() {
    return baseComponentsRemoved;
  }

  public void setBaseComponentsRemoved(List<InformationSystemRelease> removedBaseComponents) {
    this.baseComponentsRemoved = removedBaseComponents;
  }

  public List<InformationSystemRelease> getParentComponentsAdded() {
    return parentComponentsAdded;
  }

  public void setParentComponentsAdded(List<InformationSystemRelease> parentComponentsAdded) {
    this.parentComponentsAdded = parentComponentsAdded;
  }

  public List<InformationSystemRelease> getParentComponentsRemoved() {
    return parentComponentsRemoved;
  }

  public void setParentComponentsRemoved(List<InformationSystemRelease> parentComponentsRemoved) {
    this.parentComponentsRemoved = parentComponentsRemoved;
  }

  public List<InformationSystemDomain> getInformationSystemDomainsAdded() {
    return informationSystemDomainsAdded;
  }

  public void setInformationSystemDomainsAdded(List<InformationSystemDomain> addedInformationSystemDomains) {
    this.informationSystemDomainsAdded = addedInformationSystemDomains;
  }

  public List<InformationSystemDomain> getInformationSystemDomainsRemoved() {
    return informationSystemDomainsRemoved;
  }

  public void setInformationSystemDomainsRemoved(List<InformationSystemDomain> removedInformationSystemDomains) {
    this.informationSystemDomainsRemoved = removedInformationSystemDomains;
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

  public List<Project> getProjectsAdded() {
    return projectsAdded;
  }

  public void setProjectsAdded(List<Project> addedProjects) {
    this.projectsAdded = addedProjects;
  }

  public List<Project> getProjectsRemoved() {
    return projectsRemoved;
  }

  public void setProjectsRemoved(List<Project> removedProjects) {
    this.projectsRemoved = removedProjects;
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

  public List<BusinessFunction> getBusinessFunctionsAdded() {
    return businessFunctionsAdded;
  }

  public void setBusinessFunctionsAdded(List<BusinessFunction> addedBusinessFunctions) {
    this.businessFunctionsAdded = addedBusinessFunctions;
  }

  public List<BusinessFunction> getBusinessFunctionsRemoved() {
    return businessFunctionsRemoved;
  }

  public void setBusinessFunctionsRemoved(List<BusinessFunction> removedBusinessFunctions) {
    this.businessFunctionsRemoved = removedBusinessFunctions;
  }

  public List<BusinessMapping> getBusinessMappingsAdded() {
    return businessMappingsAdded;
  }

  public void setBusinessMappingsAdded(List<BusinessMapping> addedBusinessMappings) {
    this.businessMappingsAdded = addedBusinessMappings;
  }

  public List<BusinessMapping> getBusinessMappingsRemoved() {
    return businessMappingsRemoved;
  }

  public void setBusinessMappingsRemoved(List<BusinessMapping> removedBusinessMappings) {
    this.businessMappingsRemoved = removedBusinessMappings;
  }

  @Override
  public boolean isBasicPropertiesChanged() {
    return super.isBasicPropertiesChanged()
        || !StringUtils.equals(getStatusTo(), getStatusFrom())
        || !Objects.equal(getRuntimeStartAdded(), getRuntimeStartRemoved())
        || !Objects.equal(getRuntimeEndAdded(), getRuntimeEndRemoved());
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
    return runtimeStartAdded;
  }

  public void setRuntimeStartAdded(DateTime runtimeStartAdded) {
    this.runtimeStartAdded = runtimeStartAdded;
  }

  public void setRuntimeStartAdded(Date runtimeStartAdded) {
    this.runtimeStartAdded = new DateTime(runtimeStartAdded);
  }

  public DateTime getRuntimeStartRemoved() {
    return runtimeStartRemoved;
  }

  public void setRuntimeStartRemoved(DateTime runtimeStartRemoved) {
    this.runtimeStartRemoved = runtimeStartRemoved;
  }

  public void setRuntimeStartRemoved(Date runtimeStartRemoved) {
    this.runtimeStartRemoved = new DateTime(runtimeStartRemoved);
  }

  public DateTime getRuntimeEndAdded() {
    return runtimeEndAdded;
  }

  public void setRuntimeEndAdded(DateTime runtimeEndAdded) {
    this.runtimeEndAdded = runtimeEndAdded;
  }

  public void setRuntimeEndAdded(Date runtimeEndAdded) {
    this.runtimeEndAdded = new DateTime(runtimeEndAdded);
  }

  public DateTime getRuntimeEndRemoved() {
    return runtimeEndRemoved;
  }

  public void setRuntimeEndRemoved(DateTime runtimeEndRemoved) {
    this.runtimeEndRemoved = runtimeEndRemoved;
  }

  public void setRuntimeEndRemoved(Date runtimeEndRemoved) {
    this.runtimeEndRemoved = new DateTime(runtimeEndRemoved);
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

  public List<InformationSystemInterface> getInterfaceRemoved() {
    return interfaceRemoved;
  }

  public void setInterfaceRemoved(List<InformationSystemInterface> interfaceRemoved) {
    this.interfaceRemoved = interfaceRemoved;
  }


  public List<InformationSystemInterface> getInterfaceAdded() {
    return interfaceAdded;
  }

  public void setInterfaceAdded(List<InformationSystemInterface> interfaceAdded) {
    this.interfaceAdded = interfaceAdded;
  }

  public boolean hasNoticeableChanges() {
    return super.hasNoticeableChanges() || isChildrenChanged() || isParentChanged() || isRuntimeHasChanged();
  }

}
