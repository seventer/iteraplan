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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class InterfaceChangeset extends HistoryBBChangeset {

  private InformationSystemRelease        addedInformationSystemA;
  private InformationSystemRelease        removedInformationSystemA;

  private InformationSystemRelease        addedInformationSystemB;
  private InformationSystemRelease        removedInformationSystemB;

  private String                          directionTo;
  private String                          directionFrom;

  private List<TechnicalComponentRelease> technicalComponentReleasesAdded   = new ArrayList<TechnicalComponentRelease>();
  private List<TechnicalComponentRelease> technicalComponentReleasesRemoved = new ArrayList<TechnicalComponentRelease>();

  private List<Transport>                 transportsAdded                   = new ArrayList<Transport>();
  private List<Transport>                 transportsRemoved                 = new ArrayList<Transport>();

  /**
   * Default constructor.
   * @param bbId
   * @param tobb
   * @param author
   * @param timestamp
   */
  public InterfaceChangeset(Integer bbId, TypeOfBuildingBlock tobb, String author, DateTime timestamp) {
    super(bbId, tobb, author, timestamp);
  }

  @Override
  public boolean isBasicPropertiesChanged() {
    return super.isBasicPropertiesChanged()
        || isIsrAChanged()
        || isIsrBChanged()
        || !StringUtils.equals(getDirectionTo(), getDirectionFrom());
  }

  @Override
  public boolean isRelationsChanged() {
    return super.isRelationsChanged()
        || ! Objects.equal(getTechnicalComponentReleasesAdded(), getTechnicalComponentReleasesRemoved())
        || ! Objects.equal(getTransportsAdded(), getTransportsRemoved());
  }

  @Override
  public boolean isHierarchicalType() {
    return false;
  }

  public boolean isIsrAChanged() {
    // These are only set if they changed
    return (removedInformationSystemA != null && addedInformationSystemA != null);
  }

  public boolean isIsrBChanged() {
    // These are only set if they changed
    return (removedInformationSystemB != null && addedInformationSystemB != null);
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

  public InformationSystemRelease getAddedInformationSystemA() {
    return addedInformationSystemA;
  }

  public void setAddedInformationSystemA(InformationSystemRelease addedInformationSystemA) {
    this.addedInformationSystemA = addedInformationSystemA;
  }

  public InformationSystemRelease getRemovedInformationSystemA() {
    return removedInformationSystemA;
  }

  public void setRemovedInformationSystemA(InformationSystemRelease removedInformationSystemA) {
    this.removedInformationSystemA = removedInformationSystemA;
  }

  public InformationSystemRelease getAddedInformationSystemB() {
    return addedInformationSystemB;
  }

  public void setAddedInformationSystemB(InformationSystemRelease addedInformationSystemB) {
    this.addedInformationSystemB = addedInformationSystemB;
  }

  public InformationSystemRelease getRemovedInformationSystemB() {
    return removedInformationSystemB;
  }

  public void setRemovedInformationSystemB(InformationSystemRelease removedInformationSystemB) {
    this.removedInformationSystemB = removedInformationSystemB;
  }

  public String getDirectionTo() {
    return directionTo;
  }

  public void setDirectionTo(String directionTo) {
    this.directionTo = directionTo;
  }

  public String getDirectionFrom() {
    return directionFrom;
  }

  public void setDirectionFrom(String directionFrom) {
    this.directionFrom = directionFrom;
  }

  public List<Transport> getTransportsAdded() {
    return transportsAdded;
  }

  public void setTransportsAdded(List<Transport> addedTransports) {
    this.transportsAdded = addedTransports;
  }

  public List<Transport> getTransportsRemoved() {
    return transportsRemoved;
  }

  public void setTransportsRemoved(List<Transport> removedTransports) {
    this.transportsRemoved = removedTransports;
  }

  public boolean hasNoticeableChanges() {
    return super.hasNoticeableChanges() || isIsrAChanged() || isIsrBChanged();
  }
}
