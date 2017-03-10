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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.interfaces.UsageEntity;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * All infrastructure elements taken together form the deployment infrastructure. It consists of
 * logical hardware and network units which are used to operate information systems as well as
 * technical components. A single infrastructure element may be modelled as an ordered hierarchy of
 * infrastructure elements.
 */
@Entity
@Audited
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
@Indexed(index = "index.InfrastructureElement")
public class InfrastructureElement extends AbstractHierarchicalEntity<InfrastructureElement> implements UsageEntity<InfrastructureElement> {

  private static final long             serialVersionUID                      = 5620674499718345900L;
  private Set<InformationSystemRelease> informationSystemReleases             = hashSet();
  private Set<Tcr2IeAssociation>        technicalComponentReleaseAssociations = hashSet();

  private Set<InfrastructureElement>    baseComponents                        = hashSet();
  private Set<InfrastructureElement>    parentComponents                      = hashSet();

  public InfrastructureElement() {
    // No-arg constructor.
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.INFRASTRUCTUREELEMENT;
  }

  /**
   * Adds a set of {@link InformationSystemRelease}s. Updates both sides of the association.
   */
  public void addInformationSystemReleases(Set<InformationSystemRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (InformationSystemRelease isr : set) {
      informationSystemReleases.add(isr);
      isr.getInfrastructureElements().add(this);
    }
  }

  @Override
  public int compareTo(BuildingBlock other) {
    return HierarchyHelper.compareToForOrderedHierarchy(this, (InfrastructureElement) other);
  }

  @Override
  public String getI18NKey() {
    return "infrastructureElement.virtualElement";
  }

  public Set<InformationSystemRelease> getInformationSystemReleases() {
    return informationSystemReleases;
  }

  public Set<TechnicalComponentRelease> getTechnicalComponentReleases() {
    Set<TechnicalComponentRelease> result = new HashSet<TechnicalComponentRelease>(technicalComponentReleaseAssociations.size());
    for (Tcr2IeAssociation tcrAssociation : technicalComponentReleaseAssociations) {
      result.add(tcrAssociation.getTechnicalComponentRelease());
    }
    return result;
  }

  public Set<Tcr2IeAssociation> getTechnicalComponentReleaseAssociations() {
    return technicalComponentReleaseAssociations;
  }

  /**
   * Removes all {@link InformationSystemRelease}s. Updates both sides of the association.
   */
  public void removeInformationSystemReleases() {
    for (InformationSystemRelease isr : informationSystemReleases) {
      isr.getInfrastructureElements().remove(this);
    }
    informationSystemReleases.clear();
  }

  /**
   * Removes all {@link TechnicalComponentRelease}s. Updates both sides of the association.
   */
  public void removeTechnicalComponentsReleaseAssociations() {
    for (Tcr2IeAssociation tcrAss : technicalComponentReleaseAssociations) {
      tcrAss.getTechnicalComponentRelease().getInfrastructureElementAssociations().remove(tcrAss);
    }
    technicalComponentReleaseAssociations.clear();
  }

  public void setInformationSystemReleases(Set<InformationSystemRelease> set) {
    this.informationSystemReleases = set;
  }

  /**
   * Compatibility method for setting the associations to Technical Component Releases.
   * <p><b>Use {@link #setTechnicalComponentReleaseAssociations(Set)} instead.</b></p>
   * This method does not create fully valid association objects and <b>must not</b> be used by productive code. It is acceptable for tests.
   */
  @Deprecated
  public void setTechnicalComponentReleases(Set<TechnicalComponentRelease> set) {
    this.technicalComponentReleaseAssociations = new HashSet<Tcr2IeAssociation>(set.size());
    for (TechnicalComponentRelease tcr : set) {
      this.technicalComponentReleaseAssociations.add(new Tcr2IeAssociation(tcr, this));
    }
  }

  public void setTechnicalComponentReleaseAssociations(Set<Tcr2IeAssociation> set) {
    this.technicalComponentReleaseAssociations = set;
  }

  public void connectTcr2IeAssociations(Set<Tcr2IeAssociation> associations) {
    Set<Tcr2IeAssociation> existingAssociations = this.getTechnicalComponentReleaseAssociations();

    for (Tcr2IeAssociation tcrAss : Sets.difference(existingAssociations, associations).immutableCopy()) {
      tcrAss.disconnect();
    }

    for (Tcr2IeAssociation assoc : Sets.difference(associations, existingAssociations).immutableCopy()) {
      assoc.connect();
    }
  }

  /**{@inheritDoc}**/
  public void addBaseComponent(InfrastructureElement rel) {
    HierarchyHelper.addBaseComponent(this, rel, IteraplanErrorMessages.IE_BASECOMPONENT_CYCLE);
  }

  /**{@inheritDoc}**/
  public void addBaseComponents(Collection<InfrastructureElement> set) {
    Preconditions.checkContentsNotNull(set);
    for (InfrastructureElement rel : set) {
      HierarchyHelper.addBaseComponent(this, rel, IteraplanErrorMessages.IE_BASECOMPONENT_CYCLE);
    }
  }

  /**{@inheritDoc}**/
  public Set<InfrastructureElement> getBaseComponents() {
    return this.baseComponents;
  }

  /**{@inheritDoc}**/
  public void removeBaseComponents() {
    for (InfrastructureElement rel : baseComponents) {
      rel.getParentComponents().remove(this);
    }
    baseComponents.clear();
  }

  /**{@inheritDoc}**/
  public void addParentComponent(InfrastructureElement rel) {
    HierarchyHelper.addParentComponent(this, rel, IteraplanErrorMessages.IE_BASECOMPONENT_CYCLE);
  }

  /**{@inheritDoc}**/
  public void addParentComponents(Collection<InfrastructureElement> set) {
    Preconditions.checkContentsNotNull(set);
    for (InfrastructureElement rel : set) {
      HierarchyHelper.addParentComponent(this, rel, IteraplanErrorMessages.IE_BASECOMPONENT_CYCLE);
    }
  }

  /**{@inheritDoc}**/
  public Set<InfrastructureElement> getParentComponents() {
    return this.parentComponents;
  }

  /**{@inheritDoc}**/
  public void removeParentComponents() {
    for (InfrastructureElement rel : parentComponents) {
      rel.getBaseComponents().remove(this);
    }
    parentComponents.clear();
  }

  public void setBaseComponents(Set<InfrastructureElement> baseComponents) {
    this.baseComponents = baseComponents;
  }

  public void setParentComponents(Set<InfrastructureElement> parentComponents) {
    this.parentComponents = parentComponents;
  }

}