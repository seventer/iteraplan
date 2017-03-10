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
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * Architectural domains are used to structure technical components within the blueprint.
 */
@Entity
@Audited
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
@Indexed(index = "index.ArchitecturalDomain")
public class ArchitecturalDomain extends AbstractHierarchicalEntity<ArchitecturalDomain> {

  private static final long              serialVersionUID           = -5167222727502000485L;
  private Set<TechnicalComponentRelease> technicalComponentReleases = hashSet();

  public ArchitecturalDomain() {
    // No-arg constructor.
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.ARCHITECTURALDOMAIN;
  }

  public Set<TechnicalComponentRelease> getTechnicalComponentReleases() {
    return this.technicalComponentReleases;
  }

  public void setTechnicalComponentReleases(Set<TechnicalComponentRelease> set) {
    this.technicalComponentReleases = set;
  }

  /**
   * Adds a set of {@link TechnicalComponent}s. Updates both sides of the association.
   */
  public void addTechnicalComponentReleases(Collection<TechnicalComponentRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (TechnicalComponentRelease tcr : set) {
      technicalComponentReleases.add(tcr);
      tcr.getArchitecturalDomains().add(this);
    }
  }

  /**
   * Removes all {@link TechnicalComponentRelease}s. Updates both sides of the association.
   */
  public void removeTechnicalComponentReleases() {
    for (TechnicalComponentRelease elem : technicalComponentReleases) {
      elem.getArchitecturalDomains().remove(this);
    }
    technicalComponentReleases.clear();
  }

  @Override
  public int compareTo(BuildingBlock other) {
    return HierarchyHelper.compareToForOrderedHierarchy(this, (ArchitecturalDomain) other);
  }

  @Override
  public String getI18NKey() {
    return "architecturalDomain.virtualElement";
  }
}