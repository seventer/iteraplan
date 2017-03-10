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

import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.common.util.StringUtil;


/**
 * A {@code TechnicalComponent} represents a container for {@code TechnicalComponentRelease}s. It
 * specifies properties common to all releases of a given {@code TechnicalComponent}.
 */
@Entity
@Audited
@Indexed(index = "index.TechnicalComponent")
public class TechnicalComponent extends BuildingBlock {

  private static final long              serialVersionUID = -2365366321513697936L;

  @Field(store = Store.YES)
  private String                         name;

  private boolean                        availableForInterfaces;

  @ContainedIn
  private Set<TechnicalComponentRelease> releases         = hashSet();

  public TechnicalComponent() {
    // No-arg constructor.
  }

  @Override
  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return TypeOfBuildingBlock.TECHNICALCOMPONENT;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = StringUtils.trim(StringUtil.removeIllegalXMLChars(name));
  }

  public boolean isAvailableForInterfaces() {
    return this.availableForInterfaces;
  }

  public void setAvailableForInterfaces(boolean value) {
    this.availableForInterfaces = value;
  }

  public Set<TechnicalComponentRelease> getReleases() {
    return this.releases;
  }

  public void setReleases(Set<TechnicalComponentRelease> set) {
    this.releases = set;
  }

  /**
   * Adds a {@code TechnicalComponentRelease}. Updates both sides of the association.
   */
  public void addRelease(TechnicalComponentRelease rel) {
    Preconditions.checkNotNull(rel);
    releases.add(rel);
    rel.setTechnicalComponent(this);
  }

  /**
   * Removes an {@link TechnicalComponentRelease}. Updates both sides of the association.
   */
  public void removeRelease(TechnicalComponentRelease rel) {
    Preconditions.checkNotNull(rel);
    releases.remove(rel);
    rel.setTechnicalComponent(null);
  }

  public String getIdentityString() {
    return getName();
  }

  @Override
  /**
   * TechnicalComponents have no description, see {@link TechnicalComponentRelease}
   */
  public String getDescription() {
    return "";
  }

}