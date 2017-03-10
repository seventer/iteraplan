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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.common.util.StringUtil;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.StatusEntity;
import de.iteratec.iteraplan.model.interfaces.UsageEntity;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;
import de.iteratec.iteraplan.model.sorting.OrderedHierarchicalEntityCachingComparator;


/**
 * An {@code InformationSystemRelease} is a piece of software or a software package for related
 * business functions with clear logical and technical boundaries. It is supported completely or
 * chiefly by IT.
 */
@Entity
@Audited
@SuppressWarnings("PMD.TooManyMethods")
@Indexed(index = "index.InformationSystemRelease")
public class InformationSystemRelease extends BuildingBlock implements HierarchicalEntity<InformationSystemRelease>, Release, RuntimePeriodDelegate,
    Sequence<InformationSystemRelease>, StatusEntity, UsageEntity<InformationSystemRelease>, BusinessMappingEntity {

  private static final long   serialVersionUID = 7144720635020674969L;
  private static final Logger LOGGER           = Logger.getIteraplanLogger(InformationSystemRelease.class);

  /**
   * Enumeration class for possible statuses that an information system can have. The string passed
   * into the constructor is the key for an internationalized value.
   * <p>
   * The enum type is made available to Hibernate via the <tt>StringEnumUserType</tt> class. See
   * this class's JavaDoc for more information.
   */
  public enum TypeOfStatus {
    CURRENT("typeOfStatus_current"), PLANNED("typeOfStatus_planned"), TARGET("typeOfStatus_target"), INACTIVE("typeOfStatus_inactive");

    private final String typeOfStatus;

    TypeOfStatus(String typeOfStatus) {
      this.typeOfStatus = typeOfStatus;
    }

    /**
     * Delegates to <tt>toString()</tt>. The presentation tier uses JSP Expression Language to
     * access application data stored in JavaBean components. Thus a JavaBean-style getter-method
     * has to be provided to display the value of an Enum instance on the GUI.
     */
    public String getValue() {
      return toString();
    }

    /**
     * Returns the current string value stored in the Enum.
     * <p>
     * Required for correct reflection behaviour (see <tt>StringEnumReflectionHelper</tt>).
     * 
     * @return String value.
     */
    @Override
    public String toString() {
      return typeOfStatus;
    }

    /**
     * @see #values()
     * @return List of all {@link TypeOfStatus} as strings
     */
    public static List<String> stringValues() {
      List<String> values = new ArrayList<String>();
      for (TypeOfStatus status : values()) {
        values.add(status.toString());
      }
      return values;
    }
  }

  @Field(store = Store.YES)
  private String                          description;
  @Field(index = Index.UN_TOKENIZED, store = Store.YES)
  private String                          version;
  @IndexedEmbedded(targetElement = RuntimePeriod.class)
  private RuntimePeriod                   runtimePeriod;

  private TypeOfStatus                    typeOfStatus                     = TypeOfStatus.CURRENT;

  @IndexedEmbedded
  private InformationSystem               informationSystem                = null;

  /** {@link #getInterfacesReleaseA()} */
  private Set<InformationSystemInterface> interfacesReleaseA               = Sets.newHashSet();

  /** {@link #getInterfacesReleaseB()} */
  private Set<InformationSystemInterface> interfacesReleaseB               = Sets.newHashSet();
  private Set<InformationSystemDomain>    informationSystemDomains         = Sets.newHashSet();
  private Set<TechnicalComponentRelease>  technicalComponentReleases       = Sets.newHashSet();
  private Set<InfrastructureElement>      infrastructureElements           = Sets.newHashSet();
  private Set<Isr2BoAssociation>          businessObjectAssociations       = Sets.newHashSet();
  private Set<Project>                    projects                         = Sets.newHashSet();
  private Set<BusinessFunction>           businessFunctions                = Sets.newHashSet();

  // business objects connected to and transported via IS interfaces of the ISR
  private final Set<BusinessObject>       businessObjectsTransportedByIsis = Sets.newHashSet();

  private Set<BusinessMapping>            businessMappings                 = Sets.newHashSet();

  private Set<InformationSystemRelease>   successors                       = Sets.newHashSet();
  private Set<InformationSystemRelease>   predecessors                     = Sets.newHashSet();

  private InformationSystemRelease        parent                           = null;
  private Set<InformationSystemRelease>   children                         = Sets.newHashSet();

  private Set<InformationSystemRelease>   baseComponents                   = Sets.newHashSet();

  private Set<InformationSystemRelease>   parentComponents                 = Sets.newHashSet();

  public InformationSystemRelease() {
    // No-arg constructor.
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;
  }

  /**{@inheritDoc}**/
  public void addBusinessMapping(BusinessMapping bm) {
    Preconditions.checkNotNull(bm);
    businessMappings.add(bm);
    bm.setInformationSystemRelease(this);
  }

  /**{@inheritDoc}**/
  public void addBusinessMappings(Set<BusinessMapping> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessMapping bm : set) {
      addBusinessMapping(bm);
    }
  }

  /**
   * Adds a child. Updates both sides of the association.
   */
  public void addChild(InformationSystemRelease isr) {
    Preconditions.checkNotNull(isr);
    children.add(isr);
    isr.setParent(this);
  }

  /**
   * Adds the specified collection of children. Updates both sides of the association.
   * 
   * @param childrenToAdd the children to add
   */
  public void addChildren(Collection<InformationSystemRelease> childrenToAdd) {
    Preconditions.checkNotNull(childrenToAdd);

    for (InformationSystemRelease child : childrenToAdd) {
      getChildren().add(child);
      child.addParent(this);
    }
  }

  /**
   * Adds an {@code InformationSystemDomain}s. Updates both sides of the association.
   */
  public void addInformationSystemDomain(InformationSystemDomain isd) {
    Preconditions.checkNotNull(isd);
    informationSystemDomains.add(isd);
    isd.getInformationSystemReleases().add(this);
  }

  /**
   * Adds a set of {@code InformationSystemDomain}s. Updates both sides of the association.
   */
  public void addInformationSystemDomains(Collection<InformationSystemDomain> set) {
    Preconditions.checkContentsNotNull(set);
    for (InformationSystemDomain isd : set) {
      informationSystemDomains.add(isd);
      isd.getInformationSystemReleases().add(this);
    }
  }

  /**
   * Adds a {@code InfrastructureElement}. Updates both sides of the association.
   */
  public void addInfrastructureElement(InfrastructureElement ie) {
    Preconditions.checkNotNull(ie);
    infrastructureElements.add(ie);
    ie.getInformationSystemReleases().add(this);
  }

  /**
   * Adds each element of the given set of {@code InfrastructureElement}s. Updates both sides of the
   * association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addInfrastructureElements(Collection<InfrastructureElement> set) {
    Preconditions.checkContentsNotNull(set);
    for (InfrastructureElement ie : set) {
      infrastructureElements.add(ie);
      ie.getInformationSystemReleases().add(this);
    }
  }

  /**
   * Adds a parent. Updates both sides of the association.
   */
  public void addParent(InformationSystemRelease isr) {
    Preconditions.checkNotNull(isr);
    removeParent();
    setParent(isr);
    isr.getChildren().add(this);
  }

  /**
   * Adds a predecessor. Updates both sides of the association.
   */
  public void addPredecessor(InformationSystemRelease isr) {
    HierarchyHelper.addPredecessor(this, isr, IteraplanErrorMessages.ISR_PREDECESSOR_CYCLE);
  }

  /**
   * Adds each element of the given set as a predecessor. Updates both sides of the association.
   * Neither the set nor an element in the set must be {@code null}.
   */
  public void addPredecessors(Collection<InformationSystemRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (InformationSystemRelease isr : set) {
      HierarchyHelper.addPredecessor(this, isr, IteraplanErrorMessages.ISR_PREDECESSOR_CYCLE);
    }
  }

  /**
   * Adds a {@link Project}. Updates both sides of the association.
   */
  public void addProject(Project p) {
    Preconditions.checkNotNull(p);
    projects.add(p);
    p.getInformationSystemReleases().add(this);
  }

  /**
   * Adds each element of the given set of {@link Project}s. Updates both sides of the association.
   * Neither the set nor an element in the set must be {@code null}.
   */
  public void addProjects(Collection<Project> set) {
    Preconditions.checkContentsNotNull(set);
    for (Project p : set) {
      addProject(p);
    }
  }

  /**
   * Adds a successor. Updates both sides of the association.
   */
  public void addSuccessor(InformationSystemRelease isr) {
    HierarchyHelper.addSuccessor(this, isr, IteraplanErrorMessages.ISR_PREDECESSOR_CYCLE);
  }

  /**
   * Adds each element of the given set as a successor. Updates both sides of the association.
   * Neither the set nor an element in the set must be {@code null}.
   */
  public void addSuccessors(Collection<InformationSystemRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (InformationSystemRelease isr : set) {
      HierarchyHelper.addSuccessor(this, isr, IteraplanErrorMessages.ISR_PREDECESSOR_CYCLE);
    }
  }

  /**
   * Adds a {@code TechnicalComponentRelease}. Updates both sides of the association.
   */
  public void addTechnicalComponentRelease(TechnicalComponentRelease rel) {
    Preconditions.checkNotNull(rel);
    technicalComponentReleases.add(rel);
    rel.getInformationSystemReleases().add(this);
  }

  /**
   * Adds each element of the given set of {@code TechnicalComponentRelease}s. Updates both sides of
   * the association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addTechnicalComponentReleases(Collection<TechnicalComponentRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (TechnicalComponentRelease rel : set) {
      technicalComponentReleases.add(rel);
      rel.getInformationSystemReleases().add(this);
    }
  }

  public void addBusinessFunction(BusinessFunction bf) {
    Preconditions.checkNotNull(bf);
    businessFunctions.add(bf);
    bf.getInformationSystems().add(this);
  }

  /**
   * Adds each element of the given set of {@code BusinessFunction}s. Updates both sides of
   * the association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addBusinessFunctions(Collection<BusinessFunction> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessFunction rel : set) {
      businessFunctions.add(rel);
      rel.getInformationSystems().add(this);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void addBaseComponent(InformationSystemRelease rel) {
    HierarchyHelper.addBaseComponent(this, rel, IteraplanErrorMessages.ISR_BASECOMPONENT_CYCLE);
  }

  /**
   * {@inheritDoc}
   */
  public void addBaseComponents(Collection<InformationSystemRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (InformationSystemRelease rel : set) {
      HierarchyHelper.addBaseComponent(this, rel, IteraplanErrorMessages.ISR_BASECOMPONENT_CYCLE);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void addParentComponent(InformationSystemRelease rel) {
    HierarchyHelper.addParentComponent(this, rel, IteraplanErrorMessages.ISR_BASECOMPONENT_CYCLE);
  }

  /**
   * {@inheritDoc}
   */
  public void addParentComponents(Collection<InformationSystemRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (InformationSystemRelease rel : set) {
      HierarchyHelper.addParentComponent(this, rel, IteraplanErrorMessages.ISR_BASECOMPONENT_CYCLE);
    }
  }

  /**
   * Creates an enriched version string composed of the version of this release and the given
   * identifier separated by the symbol '-'. The identifier is used to increase the chance for a
   * unique version string. This method is currently used for creating unique version strings for
   * copies of an information system release. Note that trailing white spaces are removed.
   * 
   * @param identifier
   *          The string to add to the generated version string. If null, no suffix is used for the
   *          created version string.
   * @return The enriched version string.
   */
  public String createVersionWithSuffix(String identifier) {
    String suffix = identifier == null ? "" : identifier;
    String v = version == null ? "" : version;

    String enrichedVersion = String.format("%s - %s", v, suffix);

    return enrichedVersion.trim();
  }

  public Set<InformationSystemInterface> getAllConnections() {
    Set<InformationSystemInterface> connections = new HashSet<InformationSystemInterface>();
    Set<InformationSystemInterface> setA = getInterfacesReleaseA();
    Set<InformationSystemInterface> setB = getInterfacesReleaseB();
    if (setA != null) {
      connections.addAll(setA);
    }
    if (setB != null) {
      connections.addAll(setB);
    }

    return connections;
  }

  /**
   * Returns all connected Connections in a sorted List for the presentation tier.
   * 
   * @return List of {@link InformationSystemInterface};
   */
  public List<InformationSystemInterface> getAllConnectionsSorted() {
    List<InformationSystemInterface> allAttachedInterfaces = new ArrayList<InformationSystemInterface>(getAllConnections());
    for (InformationSystemInterface isi : allAttachedInterfaces) {
      isi.setReferenceRelease(this);
    }

    // Sort by the hierarchical name of the information system release connected to this release.
    Collections.sort(allAttachedInterfaces, new Comparator<InformationSystemInterface>() {
      public int compare(InformationSystemInterface c1, InformationSystemInterface c2) {
        return c1.getOtherRelease().getHierarchicalName().compareToIgnoreCase(c2.getOtherRelease().getHierarchicalName());
      }
    });

    return allAttachedInterfaces;
  }

  public Set<BusinessFunction> getBusinessFunctions() {
    return businessFunctions;
  }

  /**
   * Returns the business mapping with the specified id
   * 
   * @param id
   *          The id of the business mapping to return.
   * @return The business mapping, or <code>null</code> if not found
   */
  public BusinessMapping getBusinessMapping(Integer id) {
    for (BusinessMapping bm : this.businessMappings) {
      if (bm.getId().equals(id)) {
        return bm;
      }
    }
    return null;
  }

  public Set<BusinessMapping> getBusinessMappings() {
    return businessMappings;
  }

  public List<BusinessMapping> getBusinessMappingsAsList() {
    List<BusinessMapping> list = new ArrayList<BusinessMapping>(getBusinessMappings());
    Collections.sort(list);
    return list;
  }

  public Set<BusinessObject> getBusinessObjects() {
    HashSet<BusinessObject> result = new HashSet<BusinessObject>();
    for (Isr2BoAssociation boAssoc : businessObjectAssociations) {
      result.add(boAssoc.getBusinessObject());
    }
    return result;
  }

  public Set<Isr2BoAssociation> getBusinessObjectAssociations() {
    return this.businessObjectAssociations;
  }

  public Set<InformationSystemRelease> getChildren() {
    return children;
  }

  public List<InformationSystemRelease> getChildrenAsList() {
    List<InformationSystemRelease> list = new ArrayList<InformationSystemRelease>(getChildren());
    Collections.sort(list, new HierarchicalEntityCachingComparator<InformationSystemRelease>());
    return list;
  }

  public Set<InformationSystemRelease> getChildrenAsSet() {
    return getChildren();
  }

  public void getDescendants(InformationSystemRelease entity, Set<InformationSystemRelease> set) {
    for (InformationSystemRelease child : entity.getChildren()) {
      set.add(child);
      getDescendants(child, set);
    }
  }

  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Returns a list of all child releases of this release. Note that this release is also included
   * in the resulting list.
   * 
   * @return See method description.
   */
  public List<InformationSystemRelease> getDirectAndIndirectChildren() {
    List<InformationSystemRelease> result = Lists.newArrayList();
    List<InformationSystemRelease> list = Lists.newArrayList(children);

    while (list.size() > 0) {
      InformationSystemRelease child = list.remove(0);
      list.addAll(child.getChildren());
      result.add(child);
    }

    // Add this release.
    result.add(0, this);

    return result;
  }

  @Override
  public String getHierarchicalName() {
    return HierarchyHelper.makeHierarchicalName(this, true);
  }

  public String getIdentityString() {
    return getHierarchicalName();
  }

  public String getHierarchicalNameIfDifferent() {
    String hierarchicalName = this.getHierarchicalName();
    if (this.getReleaseName().equals(hierarchicalName)) {
      return StringUtils.EMPTY;
    }
    else {
      return hierarchicalName;
    }
  }

  public InformationSystem getInformationSystem() {
    return informationSystem;
  }

  public Set<InformationSystemDomain> getInformationSystemDomains() {
    return informationSystemDomains;
  }

  public Set<InfrastructureElement> getInfrastructureElements() {
    return infrastructureElements;
  }

  /**
   * @return Returns a sorted list of {@code InformationSystemRelease}s that interface *this*.
   */
  public List<InformationSystemRelease> getInterfacedInformationSystemReleases() {
    List<InformationSystemRelease> neighbors = new ArrayList<InformationSystemRelease>();
    Set<InformationSystemInterface> interfaces = this.getAllConnections();
    for (InformationSystemInterface c : interfaces) {
      InformationSystemRelease isrA = c.getInformationSystemReleaseA();
      InformationSystemRelease isrB = c.getInformationSystemReleaseB();
      if (isrA == this) {
        neighbors.add(isrB);
      }
      else {
        neighbors.add(isrA);
      }
    }

    Collections.sort(neighbors, new HierarchicalEntityCachingComparator<InformationSystemRelease>());

    return neighbors;
  }

  /**
   * @return Returns a sorted list of {@code BusinessObject}s that are connected with interfaces of
   *         this {@code InformationsSystemRelease}
   */
  public List<BusinessObject> getBusinessObjectsTransportedByIsisSorted() {
    List<BusinessObject> sortedResult = new ArrayList<BusinessObject>(getBusinessObjectsTransportedByIsis());
    Collections.sort(sortedResult, new OrderedHierarchicalEntityCachingComparator<BusinessObject>());

    return sortedResult;
  }

  /**
   * @return Returns a set of {@code BusinessObject}s that are connected with interfaces of this
   *         {@code InformationsSystemRelease}
   */
  public Set<BusinessObject> getBusinessObjectsTransportedByIsis() {
    Set<InformationSystemInterface> interfaces = this.getAllConnections();

    for (InformationSystemInterface isi : interfaces) {

      Set<Transport> transports = isi.getTransports();
      for (Transport transport : transports) {
        this.businessObjectsTransportedByIsis.add(transport.getBusinessObject());
      }

    }
    return this.businessObjectsTransportedByIsis;
  }

  /**
   * Returns the set of {@link InformationSystemInterface}s where this release is the release at end
   * A of each of those interfaces.
   */
  public Set<InformationSystemInterface> getInterfacesReleaseA() {
    return interfacesReleaseA;
  }

  /**
   * Returns the set of {@link InformationSystemInterface}s where this release is the release at end
   * B of each of those interfaces.
   */
  public Set<InformationSystemInterface> getInterfacesReleaseB() {
    return interfacesReleaseB;
  }

  public int getLevel() {
    InformationSystemRelease release = this;
    int level = 1;
    while (release.getParent() != null) {
      level++;
      release = release.getParent();
    }
    return level;
  }

  @Override
  public String getNonHierarchicalName() {
    return getReleaseName();
  }

  public InformationSystemRelease getParent() {
    return parent;
  }

  public InformationSystemRelease getParentElement() {
    return parent;
  }

  public Set<InformationSystemRelease> getPredecessors() {
    return predecessors;
  }

  public InformationSystemRelease getPrimeFather() {

    InformationSystemRelease isr = this;
    while (isr.getParentElement() != null) {
      isr = isr.getParent();
    }
    return isr;
  }

  public Set<Project> getProjects() {
    return projects;
  }

  public String getName() {
    return this.getReleaseName();
  }

  public String getReleaseName() {
    if (informationSystem == null) {
      return StringUtils.EMPTY;
    }

    return GeneralHelper.makeReleaseName(informationSystem.getName(), version);
  }

  public String getNameWithoutVersion() {
    return informationSystem.getName();
  }

  public RuntimePeriod getRuntimePeriod() {
    return runtimePeriod;
  }

  public RuntimePeriod getRuntimePeriodNullSafe() {
    return runtimePeriod == null ? new RuntimePeriod() : runtimePeriod;
  }

  public Set<InformationSystemRelease> getSuccessors() {
    return successors;
  }

  public Set<TechnicalComponentRelease> getTechnicalComponentReleases() {
    return technicalComponentReleases;
  }

  public TypeOfStatus getTypeOfStatus() {
    return typeOfStatus;
  }

  public String getTypeOfStatusAsString() {
    return typeOfStatus.toString();
  }

  public String getVersion() {
    return version;
  }

  /**{@inheritDoc}**/
  public void removeBusinessMappings() {
    for (BusinessMapping bm : businessMappings) {
      bm.setInformationSystemRelease(null);
    }
    businessMappings.clear();
  }

  /**
   * Removes all {@code BusinessObject}s. Updates both sides of the association.
   */
  public void removeBusinessObjectAssociations() {
    for (Isr2BoAssociation boAssoc : businessObjectAssociations) {
      boAssoc.getBusinessObject().getInformationSystemReleaseAssociations().remove(boAssoc);
    }
    businessObjectAssociations.clear();
  }

  /**
   * Removes all {@code InformationSystemDomain}s. Updates both sides of the association.
   */
  public void removeInformationSystemDomains() {
    for (InformationSystemDomain isd : informationSystemDomains) {
      isd.getInformationSystemReleases().remove(this);
    }
    informationSystemDomains.clear();
  }

  /**
   * {@inheritDoc}
   */
  public void removeBaseComponents() {
    for (InformationSystemRelease rel : baseComponents) {
      rel.getParentComponents().remove(this);
    }
    baseComponents.clear();
  }

  /**
   * {@inheritDoc}
   */
  public void removeParentComponents() {
    for (InformationSystemRelease rel : parentComponents) {
      rel.getBaseComponents().remove(this);
    }
    parentComponents.clear();
  }

  /**
   * Removes all {@code InfrastructureElement}s. Updates both sides of the association.
   */
  public void removeInfrastructureElements() {
    for (InfrastructureElement ie : infrastructureElements) {
      ie.getInformationSystemReleases().remove(this);
    }
    infrastructureElements.clear();
  }

  /**
   * Removes the parent. Updates both sides of the association.
   */
  public void removeParent() {
    if (parent != null) {
      parent.getChildren().remove(this);
      setParent(null);
    }
  }

  /**
   * Removes all predecessors. Updates both sides of the association.
   */
  public void removePredecessors() {
    for (InformationSystemRelease isr : predecessors) {
      isr.getSuccessors().remove(this);
    }
    predecessors.clear();
  }

  /**
   * Removes all associated {@link Project}s. Updates both sides of the association.
   */
  public void removeProjects() {
    for (Project p : projects) {
      p.getInformationSystemReleases().remove(this);
    }
    projects.clear();
  }

  /**
   * Removes all successors. Updates both sides of the association.
   */
  public void removeSuccessors() {
    for (InformationSystemRelease isr : successors) {
      isr.getPredecessors().remove(this);
    }
    successors.clear();
  }

  /**
   * Remove all {@link TechnicalComponentRelease}s. Updates both sides of the association.
   */
  public void removeTechnicalComponentReleases() {
    for (TechnicalComponentRelease rel : technicalComponentReleases) {
      rel.getInformationSystemReleases().remove(this);
    }
    technicalComponentReleases.clear();
  }

  /**
   * Remove all {@link BusinessFunction}s. Updates both sides of the association.
   */
  public void removeBusinessFunctions() {
    for (BusinessFunction rel : businessFunctions) {
      rel.getInformationSystems().remove(this);
    }
    businessFunctions.clear();
  }

  /**
   * Removes the associations of the interfaces from the other {@link InformationSystemRelease} side.
   * This set will be left unchanged, because otherwise the interfaces cannot be deleted after then.
   */
  public void removeInterfacesReleaseA() {
    Set<InformationSystemInterface> tmp = removeDuplicateInterfaces(interfacesReleaseA);

    for (InformationSystemInterface isi : tmp) {
      isi.getInformationSystemReleaseB().getInterfacesReleaseA().remove(isi);
      isi.getInformationSystemReleaseB().getInterfacesReleaseB().remove(isi);
    }
  }

  /**
   * Removes the associations of the interfaces from the other {@link InformationSystemRelease} side.
   * This set will be left unchanged, because otherwise the interfaces cannot be deleted after then.
   */
  public void removeInterfacesReleaseB() {
    for (InformationSystemInterface isi : interfacesReleaseB) {
      isi.getInformationSystemReleaseA().getInterfacesReleaseA().remove(isi);
      isi.getInformationSystemReleaseA().getInterfacesReleaseB().remove(isi);
    }
  }

  public Date runtimeEndsAt() {
    return runtimePeriod == null ? null : runtimePeriod.getEnd();
  }

  public boolean runtimeOverlapsPeriod(RuntimePeriod period) {
    return runtimePeriod == null ? true : runtimePeriod.overlapsPeriod(period);
  }

  public Date runtimeStartsAt() {
    return runtimePeriod == null ? null : runtimePeriod.getStart();
  }

  public boolean runtimeWithinPeriod(RuntimePeriod period) {
    return runtimePeriod == null ? true : runtimePeriod.withinPeriod(period);
  }

  public void setBusinessFunctions(Set<BusinessFunction> businessFunctions) {
    this.businessFunctions = businessFunctions;
  }

  public void setBusinessMappings(Set<BusinessMapping> set) {
    this.businessMappings = set;
  }

  /**
   * Compatibility method for setting the associations to Business Objects.
   * <p><b>Use {@link #setBusinessObjectAssociations(Set)} instead.</b></p>
   * This method does not create fully valid association objects and <b>must not</b> be used by productive code. It is acceptable for tests.
   */
  @Deprecated
  public void setBusinessObjects(Set<BusinessObject> set) {
    this.businessObjectAssociations = new HashSet<Isr2BoAssociation>();
    for (BusinessObject businessObject : set) {
      this.businessObjectAssociations.add(new Isr2BoAssociation(this, businessObject));
    }
  }

  public void setBusinessObjectAssociations(Set<Isr2BoAssociation> set) {
    this.businessObjectAssociations = set;
  }

  public void setChildren(Set<InformationSystemRelease> children) {
    this.children = children;
  }

  public void setDescription(String description) {
    this.description = StringUtil.removeIllegalXMLChars(description);
  }

  public void setInformationSystem(InformationSystem is) {
    this.informationSystem = is;
  }

  public void setInformationSystemDomains(Set<InformationSystemDomain> set) {
    this.informationSystemDomains = set;
  }

  public void setInfrastructureElements(Set<InfrastructureElement> set) {
    this.infrastructureElements = set;
  }

  public void setInterfacesReleaseA(Set<InformationSystemInterface> set) {
    this.interfacesReleaseA = set;
  }

  public void setInterfacesReleaseB(Set<InformationSystemInterface> set) {
    this.interfacesReleaseB = set;
  }

  public void setParent(InformationSystemRelease parent) {
    InformationSystemRelease oldParent = this.parent;
    this.parent = parent;
    // perform cycle check to validate that the hierarchy is still in a consistent state
    // this is explicitly done _after_ the new parent relation has been set
    if (HierarchyHelper.hasElementOfCycle(this) || this.equals(parent)) {
      LOGGER.error("Can't set parent of " + this.getName() + " to " + parent.getNonHierarchicalName());
      this.parent = oldParent;
      throw new IteraplanBusinessException(IteraplanErrorMessages.ELEMENT_OF_HIERARCHY_CYCLE);
    }
  }

  public void setPredecessors(Set<InformationSystemRelease> set) {
    this.predecessors = set;
  }

  public void setProjects(Set<Project> projects) {
    this.projects = projects;
  }

  public void setRuntimePeriod(RuntimePeriod runtimePeriod) {
    this.runtimePeriod = runtimePeriod;
  }

  public void setRuntimePeriodNullSafe(RuntimePeriod period) {
    runtimePeriod = period.isUnbounded() ? null : period;
  }

  public void setSuccessors(Set<InformationSystemRelease> set) {
    this.successors = set;
  }

  public void setTechnicalComponentReleases(Set<TechnicalComponentRelease> set) {
    this.technicalComponentReleases = set;
  }

  public void setTypeOfStatus(TypeOfStatus typeOfStatus) {
    this.typeOfStatus = typeOfStatus;
  }

  public void setVersion(String version) {
    this.version = StringUtils.trim(StringUtil.removeIllegalXMLChars(version));
  }

  public Set<InformationSystemRelease> getBaseComponents() {
    return baseComponents;
  }

  public void setBaseComponents(Set<InformationSystemRelease> baseComponents) {
    this.baseComponents = baseComponents;
  }

  public Set<InformationSystemRelease> getParentComponents() {
    return parentComponents;
  }

  public void setParentComponents(Set<InformationSystemRelease> parentComponents) {
    this.parentComponents = parentComponents;
  }

  @Override
  public void validate() {
    super.validate();

    if (informationSystem.getName() == null || informationSystem.getName().equals("")) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NAME_CANNOT_BE_EMPTY);
    }

    // Check for invalid parent/child hierarchy.
    if (HierarchyHelper.hasElementOfCycle(this)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ELEMENT_OF_HIERARCHY_CYCLE);
    }

    // Check for invalid business supports.
    for (BusinessMapping bizMapping : businessMappings) {
      bizMapping.validate();
    }
  }

  public int findChildPos(InformationSystemRelease c) {
    // Not used in this class, because we have no order!
    return 0;
  }

  //Needed in order to prevent ConcurrentModificationException, if there are more copies of an interface
  //in the set
  private Set<InformationSystemInterface> removeDuplicateInterfaces(Set<InformationSystemInterface> informationSystemInterfaces) {
    Set<InformationSystemInterface> uniqueIsi = new HashSet<InformationSystemInterface>();

    for (InformationSystemInterface tmp : informationSystemInterfaces) {
      uniqueIsi.add(tmp);
    }

    return uniqueIsi;
  }

  /**
   * Removes all the children. Updates both sides of association.
   */
  public void removeChildren() {
    for (InformationSystemRelease child : getChildren()) {
      child.setParent(null);
    }
    getChildren().clear();
  }

  /** {@inheritDoc} */
  public boolean isTopLevelElement() {
    return false;
  }

  public void connectIsr2BoAssociations(Set<Isr2BoAssociation> associations) {
    Set<Isr2BoAssociation> existingAssociations = this.getBusinessObjectAssociations();

    for (Isr2BoAssociation isrAss : Sets.difference(existingAssociations, associations).immutableCopy()) {
      isrAss.disconnect();
    }

    for (Isr2BoAssociation assoc : Sets.difference(associations, existingAssociations).immutableCopy()) {
      assoc.connect();
    }
  }

  /*
   * For technical attributes
   */
  public String getInformationSystemId() {
    return informationSystem != null ? String.valueOf(informationSystem.getId()) : null;
  }

  public String getInformationSystemName() {
    return informationSystem != null ? informationSystem.getName() : null;
  }

  public String getParentInformationSystemId() {

    if (parent != null && parent.getInformationSystem() != null) {
      return String.valueOf(parent.getInformationSystem().getId());
    }
    else {
      return null;
    }

  }

  public String getParentInformationSystemName() {

    if (parent != null && parent.getInformationSystem() != null) {
      return parent.getInformationSystem().getName();
    }
    else {
      return null;
    }

  }

  public String getParentInformationSystemReleaseVersion() {
    return parent != null ? parent.getVersion() : null;
  }

  public String getParentInformationSystemReleaseId() {
    return parent != null ? String.valueOf(parent.getId()) : null;
  }
}