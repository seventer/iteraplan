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

import org.joda.time.DateTime;

import com.google.common.base.Objects;

import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class ProjectChangeset extends HierarchicalEntityChangeset implements RuntimePeriodCarryingChangeset {

  private DateTime                       runtimeStartAdded;
  private DateTime                       runtimeStartRemoved;

  private DateTime                       runtimeEndAdded;
  private DateTime                       runtimeEndRemoved;

  private List<InformationSystemRelease> informationSystemReleasesAdded   = new ArrayList<InformationSystemRelease>();
  private List<InformationSystemRelease> informationSystemReleasesRemoved = new ArrayList<InformationSystemRelease>();

  /**
   * Default constructor.
   * @param bbId
   * @param tobb
   * @param author
   * @param timestamp
   */
  public ProjectChangeset(Integer bbId, TypeOfBuildingBlock tobb, String author, DateTime timestamp) {
    super(bbId, tobb, author, timestamp);
  }

  @Override
  public boolean isBasicPropertiesChanged() {
    return super.isBasicPropertiesChanged()
        || !Objects.equal(getRuntimeStartAdded(), getRuntimeStartRemoved())
        || !Objects.equal(getRuntimeEndAdded(), getRuntimeEndRemoved());
  }

  @Override
  public boolean isRelationsChanged() {
    return super.isRelationsChanged()
        || ! Objects.equal(getInformationSystemReleasesAdded(), getInformationSystemReleasesRemoved());
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

  public DateTime getRuntimeStartAdded() {
    return runtimeStartAdded;
  }

  public void setRuntimeStartAdded(Date runtimeStartAdded) {
    this.runtimeStartAdded = new DateTime(runtimeStartAdded);
  }

  public void setRuntimeStartAdded(DateTime runtimeStartAdded) {
    this.runtimeStartAdded = runtimeStartAdded;
  }

  public DateTime getRuntimeStartRemoved() {
    return runtimeStartRemoved;
  }

  public void setRuntimeStartRemoved(Date runtimeStartRemoved) {
    this.runtimeStartRemoved = new DateTime(runtimeStartRemoved);
  }

  public void setRuntimeStartRemoved(DateTime runtimeStartRemoved) {
    this.runtimeStartRemoved = runtimeStartRemoved;
  }

  public DateTime getRuntimeEndAdded() {
    return runtimeEndAdded;
  }

  public void setRuntimeEndAdded(Date runtimeEndAdded) {
    this.runtimeEndAdded = new DateTime(runtimeEndAdded);
  }

  public void setRuntimeEndAdded(DateTime runtimeEndAdded) {
    this.runtimeEndAdded = runtimeEndAdded;
  }

  public DateTime getRuntimeEndRemoved() {
    return runtimeEndRemoved;
  }

  public void setRuntimeEndRemoved(Date runtimeEndRemoved) {
    this.runtimeEndRemoved = new DateTime(runtimeEndRemoved);
  }

  public void setRuntimeEndRemoved(DateTime runtimeEndRemoved) {
    this.runtimeEndRemoved = runtimeEndRemoved;
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

}
