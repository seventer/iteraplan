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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.common.util.StringUtil;
import de.iteratec.iteraplan.model.interfaces.StatusEntity;
import de.iteratec.iteraplan.model.interfaces.UsageEntity;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * A {@code TechnicalComponentRelease} contains specific information concerning the technical
 * realisation of {@link InformationSystemRelease}s or {@link InformationSystemInterface}s. The
 * standardisation is part of the IT architecture management. This results in a catalogue of
 * standardised Technical Components, also called blueprint. During the documentation of the as-is
 * situation non-standard compliant Technical Components could also be captured.
 */
@Entity
@Audited
@Indexed(index = "index.TechnicalComponentRelease")
public class TechnicalComponentRelease extends BuildingBlock implements Release, RuntimePeriodDelegate, Sequence<TechnicalComponentRelease>,
    StatusEntity, UsageEntity<TechnicalComponentRelease> {

  private static final long serialVersionUID = -2524870120049771684L;

  /**
   * Enumeration class for possible statuses that a technical component release release can have.
   * The string passed into the constructor is the key for an internationalized value.
   * <p>
   * The enum type is made available to Hibernate via the
   * {@link de.iteratec.iteraplan.persistence.util.StringEnumUserType}. See that class's JavaDoc for
   * more information.
   * </p>
   */
  public enum TypeOfStatus {
    CURRENT("typeOfStatus_current"), PLANNED("typeOfStatus_planned"), TARGET("typeOfStatus_target"), INACTIVE("typeOfStatus_inactive"), UNDEFINED(
        "typeOfStatus_undefined");

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

  private TypeOfStatus                    typeOfStatus                      = TypeOfStatus.CURRENT;

  @IndexedEmbedded
  private TechnicalComponent              technicalComponent;
  private Set<ArchitecturalDomain>        architecturalDomains              = hashSet();
  // private Set<InfrastructureElement> infrastructureElements = hashSet();
  private Set<Tcr2IeAssociation>          infrastructureElementAssociations = hashSet();
  private Set<InformationSystemRelease>   informationSystemReleases         = hashSet();

  private Set<InformationSystemInterface> informationSystemInterfaces       = hashSet();

  private Set<TechnicalComponentRelease>  successors                        = hashSet();
  private Set<TechnicalComponentRelease>  predecessors                      = hashSet();

  private Set<TechnicalComponentRelease>  baseComponents                    = hashSet();
  private Set<TechnicalComponentRelease>  parentComponents                  = hashSet();

  public TechnicalComponentRelease() {
    // No-arg constructor.
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE;
  }

  /**
   * Adds an {@code ArchitecturalDomain}. Updates both sides of the association.
   */
  public void addArchitecturalDomain(ArchitecturalDomain ad) {
    Preconditions.checkNotNull(ad);
    architecturalDomains.add(ad);
    ad.getTechnicalComponentReleases().add(this);
  }

  /**
   * Adds each element of the given set of {@code ArchitecturalDomain}s. Updates both sides of the
   * association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addArchitecturalDomains(Collection<ArchitecturalDomain> set) {
    Preconditions.checkContentsNotNull(set);
    for (ArchitecturalDomain ad : set) {
      architecturalDomains.add(ad);
      ad.getTechnicalComponentReleases().add(this);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void addBaseComponent(TechnicalComponentRelease rel) {
    HierarchyHelper.addBaseComponent(this, rel, IteraplanErrorMessages.TECHNICALCOMPONENT_BASECOMPONENT_CYCLE);
  }

  /**
   * {@inheritDoc}
   */
  public void addBaseComponents(Collection<TechnicalComponentRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (TechnicalComponentRelease rel : set) {
      HierarchyHelper.addBaseComponent(this, rel, IteraplanErrorMessages.TECHNICALCOMPONENT_BASECOMPONENT_CYCLE);
    }
  }

  /**
   * Adds each element of the given set of {@link InformationSystemRelease}s. Updates both sides of
   * the association. Neither the set nor an element in the set must be {@code null}.
   */
  public void addInformationSystemReleases(Collection<InformationSystemRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (InformationSystemRelease isr : set) {
      informationSystemReleases.add(isr);
      isr.getTechnicalComponentReleases().add(this);
    }
  }

  /**
   * Adds the given {@link InformationSystemRelease}. Updates both sides of the association. The
   * given element must not be {@code null}.
   */
  public void addInformationSystemRelease(InformationSystemRelease isr) {
    Preconditions.checkNotNull(isr);
    informationSystemReleases.add(isr);
    isr.getTechnicalComponentReleases().add(this);
  }

  /**
   * {@inheritDoc}
   */
  public void addParentComponent(TechnicalComponentRelease tcr) {
    HierarchyHelper.addParentComponent(this, tcr, IteraplanErrorMessages.TECHNICALCOMPONENT_BASECOMPONENT_CYCLE);
  }

  /**
   * {@inheritDoc}
   */
  public void addParentComponents(Collection<TechnicalComponentRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (TechnicalComponentRelease rel : set) {
      HierarchyHelper.addParentComponent(this, rel, IteraplanErrorMessages.TECHNICALCOMPONENT_BASECOMPONENT_CYCLE);
    }
  }

  /**
   * Adds a predecessor. Updates both sides of the association.
   */
  public void addPredecessor(TechnicalComponentRelease tcr) {
    HierarchyHelper.addPredecessor(this, tcr, IteraplanErrorMessages.TECHNICALCOMPONENT_PREDECESSOR_CYCLE);
  }

  /**
   * Adds each element of the given set as a predecessor. Updates both sides of the association.
   * Neither the set nor an element in the set must be {@code null}.
   */
  public void addPredecessors(Collection<TechnicalComponentRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (TechnicalComponentRelease tcr : set) {
      HierarchyHelper.addPredecessor(this, tcr, IteraplanErrorMessages.TECHNICALCOMPONENT_PREDECESSOR_CYCLE);
    }
  }

  /**
   * Adds a successor. Updates both sides of the association.
   */
  public void addSuccessor(TechnicalComponentRelease tcr) {
    HierarchyHelper.addSuccessor(this, tcr, IteraplanErrorMessages.TECHNICALCOMPONENT_PREDECESSOR_CYCLE);
  }

  /**
   * Adds each element of the given set as a successor. Updates both sides of the association.
   * Neither the set nor an element in the set must be {@code null}.
   */
  public void addSuccessors(Collection<TechnicalComponentRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (TechnicalComponentRelease tcr : set) {
      HierarchyHelper.addSuccessor(this, tcr, IteraplanErrorMessages.TECHNICALCOMPONENT_PREDECESSOR_CYCLE);
    }
  }

  /**
   * Returns all connected Connections in a sorted List for the presentation tier.
   * 
   * @return List of {@link InformationSystemInterface};
   */
  public List<InformationSystemInterface> getAllConnectionsSorted() {
    List<InformationSystemInterface> sortedResult = new ArrayList<InformationSystemInterface>(getInformationSystemInterfaces());
    Collections.sort(sortedResult);
    return sortedResult;
  }

  public Set<ArchitecturalDomain> getArchitecturalDomains() {
    return this.architecturalDomains;
  }

  public Set<TechnicalComponentRelease> getBaseComponents() {
    return this.baseComponents;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public String getIdentityString() {
    return getReleaseName();
  }

  public Set<InformationSystemInterface> getInformationSystemInterfaces() {
    return this.informationSystemInterfaces;
  }

  public Set<InformationSystemRelease> getInformationSystemReleases() {
    return this.informationSystemReleases;
  }

  public Set<InfrastructureElement> getInfrastructureElements() {
    HashSet<InfrastructureElement> result = new HashSet<InfrastructureElement>();
    for (Tcr2IeAssociation ieAssoc : infrastructureElementAssociations) {
      result.add(ieAssoc.getInfrastructureElement());
    }
    return result;
  }

  public Set<Tcr2IeAssociation> getInfrastructureElementAssociations() {
    return this.infrastructureElementAssociations;
  }

  public Set<TechnicalComponentRelease> getParentComponents() {
    return this.parentComponents;
  }

  public Set<TechnicalComponentRelease> getPredecessors() {
    return predecessors;
  }

  public String getReleaseName() {
    if (getTechnicalComponent() == null) {
      return "";
    }
    return GeneralHelper.makeReleaseName(technicalComponent.getName(), getVersion());
  }

  public RuntimePeriod getRuntimePeriod() {
    return runtimePeriod;
  }

  public RuntimePeriod getRuntimePeriodNullSafe() {
    return runtimePeriod == null ? new RuntimePeriod() : runtimePeriod;
  }

  public Set<TechnicalComponentRelease> getSuccessors() {
    return successors;
  }

  public TechnicalComponent getTechnicalComponent() {
    return technicalComponent;
  }

  public String getName() {
    return this.getReleaseName();
  }

  public String getNameWithoutVersion() {
    return technicalComponent.getName();
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

  public boolean isAvailableForInterfaces() {
    return technicalComponent.isAvailableForInterfaces();
  }

  /**
   * Remove all {@link ArchitecturalDomain}s. Updates both sides of the association.
   */
  public void removeArchitecturalDomains() {
    for (ArchitecturalDomain ad : architecturalDomains) {
      ad.getTechnicalComponentReleases().remove(this);
    }
    architecturalDomains.clear();
  }

  /**
   * {@inheritDoc}
   */
  public void removeBaseComponents() {
    for (TechnicalComponentRelease rel : baseComponents) {
      rel.getParentComponents().remove(this);
    }
    baseComponents.clear();
  }

  /**
   * Removes all {@link InformationSystemInterface}s. Updates both sides of the association.
   */
  public void removeInformationSystemInterfaces() {
    for (InformationSystemInterface isi : informationSystemInterfaces) {
      isi.getTechnicalComponentReleases().remove(this);
    }
    informationSystemInterfaces.clear();
  }

  /**
   * Remove all {@link InformationSystemRelease}s. Updates both sides of the association.
   */
  public void removeInformationSystemReleases() {
    for (InformationSystemRelease isr : informationSystemReleases) {
      isr.getTechnicalComponentReleases().remove(this);
    }
    informationSystemReleases.clear();
  }

  /**
   * Remove all {@link InfrastructureElement}s. Updates both sides of the association.
   */
  public void removeInfrastructureElements() {
    for (Tcr2IeAssociation ieAssoc : infrastructureElementAssociations) {
      ieAssoc.getInfrastructureElement().getTechnicalComponentReleaseAssociations().remove(ieAssoc);
    }
    infrastructureElementAssociations.clear();
  }

  /**
   * {@inheritDoc}
   */
  public void removeParentComponents() {
    for (TechnicalComponentRelease rel : parentComponents) {
      rel.getBaseComponents().remove(this);
    }
    parentComponents.clear();
  }

  /**
   * Removes all predecessors. Updates both sides of the association.
   */
  public void removePredecessors() {
    for (TechnicalComponentRelease rel : predecessors) {
      rel.getSuccessors().remove(this);
    }
    predecessors.clear();
  }

  /**
   * Removes all successors. Updates both sides of the association.
   */
  public void removeSuccessors() {
    for (TechnicalComponentRelease rel : successors) {
      rel.getPredecessors().remove(this);
    }
    successors.clear();
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

  public void setArchitecturalDomains(Set<ArchitecturalDomain> set) {
    this.architecturalDomains = set;
  }

  public void setBaseComponents(Set<TechnicalComponentRelease> set) {
    this.baseComponents = set;
  }

  public void setDescription(String description) {
    this.description = StringUtil.removeIllegalXMLChars(description);
  }

  public void setInformationSystemInterfaces(Set<InformationSystemInterface> set) {
    this.informationSystemInterfaces = set;
  }

  public void setInformationSystemReleases(Set<InformationSystemRelease> set) {
    this.informationSystemReleases = set;
  }

  /**
   * Compatibility method for setting the associations to Infrastructure Elements.
   * <p><b>Use {@link #setInfrastructureElementAssociations(Set)} instead.</b></p>
   * This method does not create fully valid association objects and <b>must not</b> be used by productive code. It is acceptable for tests.
   */
  @Deprecated
  public void setInfrastructureElements(Set<InfrastructureElement> set) {
    this.infrastructureElementAssociations = new HashSet<Tcr2IeAssociation>();
    for (InfrastructureElement infrastructureElement : set) {
      this.infrastructureElementAssociations.add(new Tcr2IeAssociation(this, infrastructureElement));
    }
  }

  public void setInfrastructureElementAssociations(Set<Tcr2IeAssociation> set) {
    this.infrastructureElementAssociations = set;
  }

  public void setParentComponents(Set<TechnicalComponentRelease> set) {
    this.parentComponents = set;
  }

  public void setPredecessors(Set<TechnicalComponentRelease> set) {
    this.predecessors = set;
  }

  public void setRuntimePeriod(RuntimePeriod runtimePeriod) {
    this.runtimePeriod = runtimePeriod;
  }

  public void setRuntimePeriodNullSafe(RuntimePeriod period) {
    runtimePeriod = period.isUnbounded() ? null : period;
  }

  public void setSuccessors(Set<TechnicalComponentRelease> set) {
    this.successors = set;
  }

  public void setTechnicalComponent(TechnicalComponent tc) {
    this.technicalComponent = tc;
  }

  public void setTypeOfStatus(TypeOfStatus typeOfStatus) {
    this.typeOfStatus = typeOfStatus;
  }

  public void setVersion(String version) {
    this.version = StringUtils.trim(StringUtil.removeIllegalXMLChars(version));
  }

  @Override
  public void validate() {
    super.validate();

    if (technicalComponent.getName() == null || technicalComponent.getName().equals("")) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NAME_CANNOT_BE_EMPTY);
    }
  }

  public void connectTcr2IeAssociations(Set<Tcr2IeAssociation> associations) {
    Set<Tcr2IeAssociation> existingAssociations = this.getInfrastructureElementAssociations();

    for (Tcr2IeAssociation tcrAss : Sets.difference(existingAssociations, associations).immutableCopy()) {
      tcrAss.disconnect();
    }

    for (Tcr2IeAssociation assoc : Sets.difference(associations, existingAssociations).immutableCopy()) {
      assoc.connect();
    }
  }

}