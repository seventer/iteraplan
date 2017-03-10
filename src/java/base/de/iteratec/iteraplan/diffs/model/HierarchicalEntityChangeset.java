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
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;

public abstract class HierarchicalEntityChangeset extends HistoryBBChangeset {

  private BuildingBlock                                  parentFrom;
  private BuildingBlock                                  parentTo;

  private List<BuildingBlock>   childrenAdded            = Lists.newArrayList();
  private List<BuildingBlock>   childrenRemoved          = Lists.newArrayList();

  /**
   * Default constructor.
   * @param bbId
   * @param tobb
   * @param author
   * @param timestamp
   */
  public HierarchicalEntityChangeset(Integer bbId, TypeOfBuildingBlock tobb, String author, DateTime timestamp) {
    super(bbId, tobb, author, timestamp);
  }

  @Override
  public boolean isHierarchicalType() {
    return true;
  }

  public boolean isChildrenChanged() {
    return ! Objects.equal(getChildrenAdded(), getChildrenRemoved());
  }

  /**
   * @return if parent changed
   */
  public boolean isParentChanged() {
    return !StringUtils.equals(getParentFromName(), getParentToName());
  }

  public List<BuildingBlock> getChildrenAdded() {
    return childrenAdded;
  }

  public List<BuildingBlock> getChildrenRemoved() {
    return childrenRemoved;
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

  /**
   * Might be null, even if parent changed
   */
  public BuildingBlock getParentFrom() {
    return parentFrom;
  }

  public void setParentFrom(BuildingBlock parentFrom) {
    this.parentFrom = parentFrom;
  }

  public void setParentTo(BuildingBlock parentTo) {
    this.parentTo = parentTo;
  }

  /**
   * Might be null, even if parent changed
   */
  public BuildingBlock getParentTo() {
    return parentTo;
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

  public String getParentElementLabelKey() {
    // handle the two exceptions where internal identifiers have not yet been unified
    if (getTypeOfBuildingBlock().equals(TypeOfBuildingBlock.BUSINESSFUNCTION)) {
      return "businessFunction.parent";
    }
    if (getTypeOfBuildingBlock().equals(TypeOfBuildingBlock.PRODUCT)) {
      return "product.parent";
    }

    String bbtStr = getTypeOfBuildingBlock().toString();
    return bbtStr.replace(".singular", ".parent");
  }

  public String getChildElementsLabelKey() {
    // handle the two exceptions where internal identifiers have not yet been unified
    if (getTypeOfBuildingBlock().equals(TypeOfBuildingBlock.BUSINESSFUNCTION)) {
      return "businessFunction.children";
    }
    if (getTypeOfBuildingBlock().equals(TypeOfBuildingBlock.PRODUCT)) {
      return "product.children";
    }

    String bbtStr = getTypeOfBuildingBlock().toString();
    return bbtStr.replace(".singular", ".children");
  }

  public boolean hasNoticeableChanges() {
    return super.hasNoticeableChanges() || isChildrenChanged() || isParentChanged();
  }
}
