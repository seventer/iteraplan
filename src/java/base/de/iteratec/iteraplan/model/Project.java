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

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * This class represents the domain object 'Project'. A project or program is generally
 * characterised by the singleness of its conditions (as a whole), e.g. goals, time, financial,
 * resource and other constraints, differentiations from other projects and project specific
 * organisations.
 */
@Entity
@Audited
@Indexed(index = "index.Project")
@edu.umd.cs.findbugs.annotations.SuppressWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS")
public class Project extends AbstractHierarchicalEntity<Project> implements RuntimePeriodDelegate {

  private static final long             serialVersionUID          = 7603693990851840397L;

  @IndexedEmbedded(targetElement = RuntimePeriod.class)
  private RuntimePeriod                 runtimePeriod;

  private Set<InformationSystemRelease> informationSystemReleases = hashSet();

  public Project() {
    // No-arg constructor.
  }

  public void addInformationSystemRelease(InformationSystemRelease isr) {
    isr.addProject(this);
    this.informationSystemReleases.add(isr);
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.PROJECT;
  }

  /**
   * Adds a set of {@link InformationSystemRelease}s. Updates both sides of the association.
   */
  public void addInformationSystemReleases(Set<InformationSystemRelease> set) {
    Preconditions.checkContentsNotNull(set);
    for (InformationSystemRelease isr : set) {
      informationSystemReleases.add(isr);
      isr.getProjects().add(this);
    }
  }

  @Override
  public int compareTo(BuildingBlock other) {
    return HierarchyHelper.compareToForOrderedHierarchy(this, (Project) other);
  }

  @Override
  public String getI18NKey() {
    return "project.virtualElement";
  }

  public Set<InformationSystemRelease> getInformationSystemReleases() {
    return informationSystemReleases;
  }

  public RuntimePeriod getRuntimePeriod() {
    return runtimePeriod;
  }

  public RuntimePeriod getRuntimePeriodNullSafe() {
    return runtimePeriod == null ? new RuntimePeriod() : runtimePeriod;
  }

  /**
   * Clears all associations. Updates both sides of the associations.
   */
  public void removeRelations() {
    removeInformationSystemReleases();
  }

  /**
   * Clears all information system releases. Updates both sides of the associations.
   */
  public void removeInformationSystemReleases() {
    for (InformationSystemRelease isr : informationSystemReleases) {
      isr.getProjects().remove(this);
    }
    informationSystemReleases.clear();
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

  public void setInformationSystemReleases(Set<InformationSystemRelease> set) {
    this.informationSystemReleases = set;
  }

  public void setRuntimePeriod(RuntimePeriod runtimePeriod) {
    this.runtimePeriod = runtimePeriod;
  }

  public void setRuntimePeriodNullSafe(RuntimePeriod period) {
    runtimePeriod = period.isUnbounded() ? null : period;
  }

}