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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.interfaces.GeneralisationSpecialisationEntity;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * This class represents the domain object 'Business Object'. A business object is an aligned
 * business term for abstract or concrete objects which are business relevant, i.e. have a strong
 * relation to the company's business. Examples of business objects are 'Customer', 'Product' or
 * 'Contract'.
 */
@Entity
@Audited
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
@Indexed(index = "index.BusinessObject")
public class BusinessObject extends AbstractHierarchicalEntity<BusinessObject> implements GeneralisationSpecialisationEntity<BusinessObject> {

  private static final long               serialVersionUID                        = 4832580675540779459L;
  private Set<Isr2BoAssociation>          informationSystemReleaseAssociations    = hashSet();
  private Set<BusinessFunction>           businessFunctions                       = hashSet();
  private Set<BusinessDomain>             businessDomains                         = hashSet();
  private Set<Transport>                  transports                              = hashSet();
  private Set<InformationSystemInterface> informationSystemInterfaces             = hashSet();
  private Set<InformationSystemRelease>   informationSystemReleasesConnectedToIsi = hashSet();

  private BusinessObject                  generalisation                          = null;
  private Set<BusinessObject>             specialisations                         = hashSet();

  public BusinessObject() {
    // No-arg constructor.
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.BUSINESSOBJECT;
  }

  /**
   * Adds a set of {@link BusinessDomain}s. Updates both sides of the association.
   */
  public void addBusinessDomains(Collection<BusinessDomain> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessDomain bd : set) {
      businessDomains.add(bd);
      bd.getBusinessObjects().add(this);
    }
  }

  /**
   * Adds a set of {@link BusinessFunction}s. Updates both sides of the association.
   */
  public void addBusinessFunctions(Collection<BusinessFunction> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessFunction bf : set) {
      businessFunctions.add(bf);
      bf.getBusinessObjects().add(this);
    }
  }

  /**
   * Adds a set of {@link BusinessObject}s. Updates both sides of the association.
   */
  public void addSpecialisations(Set<BusinessObject> set) {
    Preconditions.checkContentsNotNull(set);
    for (BusinessObject bo : set) {
      if (bo.getGeneralisation() != null) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.ELEMENT_GENERALISATION_ALREADY_EXISTS);
      }
      bo.addGeneralisation(this);
    }
    for (BusinessObject bo : set) {
      this.specialisations.add(bo);
    }
  }

  /**
   * Adds a generalisation. Updates both sides of the association.
   */
  public void addGeneralisation(BusinessObject bo) {
    Preconditions.checkNotNull(bo);
    removeGeneralisation();
    setGeneralisation(bo);
    bo.getSpecialisations().add(this);
  }

  /**
   * Adds a {@link Transport}. Updates both sides of the association.
   */
  public void addTransport(Transport t) {
    Preconditions.checkNotNull(t);
    transports.clear();
    transports.add(t);
    t.setBusinessObject(this);
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

  /**
   * Returns all connected Connections in a sorted List for the presentation tier.
   * 
   * @return List of {@link InformationSystemInterface};
   */
  public List<InformationSystemRelease> getAllConnectedInformationsSystemReleasesToIsiSorted() {
    List<InformationSystemRelease> sortedResult = new ArrayList<InformationSystemRelease>(getInformationSystemsConnectedToIsi());
    Collections.sort(sortedResult, new HierarchicalEntityCachingComparator<InformationSystemRelease>());
    return sortedResult;

  }

  /**
   * Compares hierarchical entities according to the standard implementation for ordered
   * hierarchies.
   */
  @Override
  public int compareTo(BuildingBlock other) {
    return HierarchyHelper.compareToForOrderedHierarchy(this, (BusinessObject) other);
  }

  public Set<BusinessDomain> getBusinessDomains() {
    return businessDomains;
  }

  public Set<BusinessFunction> getBusinessFunctions() {
    return businessFunctions;
  }

  public BusinessObject getGeneralisation() {
    return generalisation;
  }

  @Override
  public String getI18NKey() {
    return "businessObject.virtualElement";
  }

  public Set<InformationSystemRelease> getInformationSystemReleases() {
    Set<InformationSystemRelease> result = new HashSet<InformationSystemRelease>(informationSystemReleaseAssociations.size());
    for (Isr2BoAssociation isrAssociation : informationSystemReleaseAssociations) {
      result.add(isrAssociation.getInformationSystemRelease());
    }
    return result;
  }

  public Set<Isr2BoAssociation> getInformationSystemReleaseAssociations() {
    return informationSystemReleaseAssociations;
  }

  public Set<BusinessObject> getSpecialisations() {
    return specialisations;
  }

  public Set<Transport> getTransports() {
    return transports;
  }

  public Set<InformationSystemInterface> getInformationSystemInterfaces() {

    List<Transport> transportsCopy = new ArrayList<Transport>(transports);

    for (Transport tc : transportsCopy) {
      this.informationSystemInterfaces.add(tc.getInformationSystemInterface());
    }
    return this.informationSystemInterfaces;
  }

  public Set<InformationSystemRelease> getInformationSystemsConnectedToIsi() {

    List<Transport> transportsCopy = new ArrayList<Transport>(transports);
    InformationSystemInterface isi;

    for (Transport tc : transportsCopy) {
      isi = tc.getInformationSystemInterface();
      this.informationSystemReleasesConnectedToIsi.add(isi.getInformationSystemReleaseA());
      this.informationSystemReleasesConnectedToIsi.add(isi.getInformationSystemReleaseB());
    }
    return this.informationSystemReleasesConnectedToIsi;
  }

  /**
   * Clears all {@link BusinessDomain} associations. Updates both sides of the associations.
   */
  public void removeBusinessDomainRelations() {
    for (BusinessDomain bd : businessDomains) {
      bd.getBusinessObjects().remove(this);
    }
    businessDomains.clear();
  }

  /**
   * Clears all {@link BusinessFunction} associations. Updates both sides of the associations.
   */
  public void removeBusinessFunctionRelations() {
    for (BusinessFunction bf : businessFunctions) {
      bf.getBusinessObjects().remove(this);
    }
    businessFunctions.clear();
  }

  /**
   * Clears all Specialisation associations. Updates both sides of the associations.
   */
  public void removeSpecialisationRelations() {
    for (BusinessObject bo : specialisations) {
      bo.setGeneralisation(null);
    }
    specialisations.clear();
  }

  /**
   * Removes the generalisation. Updates both sides of the association.
   */
  public void removeGeneralisation() {
    if (generalisation != null) {
      generalisation.getSpecialisations().remove(this);
      generalisation = null;
    }
  }

  /**
   * Removes all information systems. Updates both sides of the association.
   */
  public void removeInformationSystemReleaseAssociations() {
    for (Isr2BoAssociation isrAssoc : informationSystemReleaseAssociations) {
      isrAssoc.getInformationSystemRelease().getBusinessObjectAssociations().remove(isrAssoc);
    }
    informationSystemReleaseAssociations.clear();
  }

  /**
   * Removes all Children, and Specialisations
   */
  @Override
  public void removeAllChildren() {
    super.removeAllChildren();
    removeSpecialisationRelations();
  }

  public void setBusinessDomains(Set<BusinessDomain> businessDomains) {
    this.businessDomains = businessDomains;
  }

  public void setBusinessFunctions(Set<BusinessFunction> businessFunctions) {
    this.businessFunctions = businessFunctions;
  }

  public void setGeneralisation(BusinessObject generalisation) {
    BusinessObject oldGeneralisation = this.generalisation;
    this.generalisation = generalisation;
    if (HierarchyHelper.hasGeneralisationCycle(this)) {
      this.generalisation = oldGeneralisation;
      throw new IteraplanBusinessException(IteraplanErrorMessages.GENERALISATION_HIERARCHY_CYCLE_BO);
    }
  }

  /**
   * Compatibility method for setting the associations to Technical Component Releases.
   * <p><b>Use {@link #setInformationSystemReleaseAssociations(Set)} instead.</b></p>
   * This method does not create fully valid association objects and <b>must not</b> be used by productive code. It is acceptable for tests.
   */
  @Deprecated
  public void setInformationSystemReleases(Set<InformationSystemRelease> set) {
    this.informationSystemReleaseAssociations = new HashSet<Isr2BoAssociation>(set.size());
    for (InformationSystemRelease isr : set) {
      this.informationSystemReleaseAssociations.add(new Isr2BoAssociation(isr, this));
    }
  }

  public void setInformationSystemReleaseAssociations(Set<Isr2BoAssociation> set) {
    this.informationSystemReleaseAssociations = set;
  }

  public void setSpecialisations(Set<BusinessObject> specialisations) {
    this.specialisations = specialisations;
  }

  public void setTransports(Set<Transport> transports) {
    this.transports = transports;
  }

  @Override
  public void validate() {

    super.validate();

    // Check whether there are cycles in the generalisation hierarchy.
    if (HierarchyHelper.hasGeneralisationCycle(this)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.GENERALISATION_HIERARCHY_CYCLE_BO);
    }
  }

  public void connectIsr2BoAssociations(Set<Isr2BoAssociation> associations) {
    Set<Isr2BoAssociation> existingAssociations = this.getInformationSystemReleaseAssociations();

    for (Isr2BoAssociation isrAss : Sets.difference(existingAssociations, associations).immutableCopy()) {
      isrAss.disconnect();
    }

    for (Isr2BoAssociation assoc : Sets.difference(associations, existingAssociations).immutableCopy()) {
      assoc.connect();
    }
  }

}