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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.User;


/**
 * Contains data about the changes between a revision of a certain BB and its predecessor (if any)
 * @author rge
 */
public abstract class HistoryBBChangeset {

  private DateTime                                       timestamp;

  private String                                         author;
  private User                                           revisionAuthor;
  private Integer                                        bbID;
  private TypeOfBuildingBlock                            bbType;
  // Is this the very first known revision of this entity, without anything before it
  private boolean                                        initialChangeset;

  private String                                         nameFrom;
  private String                                         nameTo;
  private String                                         descriptionFrom;
  private String                                         descriptionTo;

  // This is for internal use only. When accessing externally, get a List version of this Map via
  // getAttributeGroupChangesets()
  // Maps ATG IDs to corresponding ATG changesets, so all attribute changes can be stored by their
  // respective ATG
  private Map<Integer, HistoryBBAttributeGroupChangeset> attributeChangesetByAtgMap = new HashMap<Integer, HistoryBBAttributeGroupChangeset>();

  private List<String>                                   changedTimeseriesNames     = Lists.newArrayList();

  /**
   * Constructs a changeset for one building block with ID {@code bbId}, of type {@code tobb}.
   * Author and timestamp are optional for a changeset.
   * @param bbId ID of the building block which is reflected by this changeset
   * @param tobb type of the building block which is reflected by this changeset
   * @param author A string representation of the user who created this changeset. If null, an empty string will be used.
   * @param timestamp Timestamp for the point in time which this changeset reflects. If null, the current time will be used.
   */
  public HistoryBBChangeset(Integer bbId, TypeOfBuildingBlock tobb, String author, DateTime timestamp) {
    this.bbID = bbId;
    this.bbType = tobb;
    this.initialChangeset = false;

    if (author != null) {
      this.author = author;
    }
    else {
      this.author = "";
    }

    if (timestamp != null) {
      this.timestamp = timestamp;
    }
    else {
      this.timestamp = new DateTime();
    }

  }

  /**
   * Returns the name of a BuildingBlock, or "" if the BB is null
   */
  protected <T extends BuildingBlock> String getNameOfBBOrEmpty(T bb) {
    return (bb == null ? "" : bb.getNonHierarchicalName());
  }

  public boolean isNameChanged() {
    return !StringUtils.equals(nameFrom, nameTo);
  }

  public boolean isDescriptionChanged() {
    return !StringUtils.equals(descriptionFrom, descriptionTo);
  }

  public String getNameFrom() {
    return nameFrom;
  }

  public String getNameTo() {
    return nameTo;
  }

  public String getDescriptionFrom() {
    return descriptionFrom;
  }

  public String getDescriptionTo() {
    return descriptionTo;
  }

  public User getRevisionAuthor() {
    return revisionAuthor;
  }

  public void setRevisionAuthor(User revisionAuthor) {
    this.revisionAuthor = revisionAuthor;
  }

  public DateTime getTimestamp() {
    return timestamp;
  }

  public String getTimestampString() {
    return DateUtils.formatAsStringToLong(timestamp.toDate(), UserContext.getCurrentLocale());
  }

  public String getAuthor() {
    return author;
  }

  public Integer getBbID() {
    return bbID;
  }

  /**
   * May be override by sub-classes if they have additional basic properties.
   */
  public boolean isBasicPropertiesChanged() {
    return isNameChanged() || isDescriptionChanged();
  }

  /**
   *  Means that there is no FROM, this is the first version and nothing came before it (as far as History is logged)
   */
  public boolean isInitialChangeset() {
    return initialChangeset;
  }

  public void setInitialChangeset(boolean isInitial) {
    this.initialChangeset = isInitial;
  }

  /**
   * Test data may not have an author
   */
  public boolean isHasAuthor() {
    return !StringUtils.isEmpty(author);
  }

  public Map<Integer, HistoryBBAttributeGroupChangeset> getAttributeChangesetByAtgMap() {
    return attributeChangesetByAtgMap;
  }

  public List<HistoryBBAttributeGroupChangeset> getAttributeGroupChangesets() {
    return new ArrayList<HistoryBBAttributeGroupChangeset>(attributeChangesetByAtgMap.values());
  }

  public List<String> getChangedTimeseriesNames() {
    return changedTimeseriesNames;
  }

  public void setChangedTimeseriesNames(List<String> changedTimeseriesNames) {
    this.changedTimeseriesNames = changedTimeseriesNames;
  }

  public boolean isAttributesChanged() {
    return !attributeChangesetByAtgMap.isEmpty();
  }

  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return bbType;
  }

  public String getTypeOfBuildingBlockAsString() {
    return bbType.getValue();
  }

  /**
   * Helper function for JSP logic to discover the concrete type. Implementation that choose to return {@code true} must
   * also implement the methods getParentElementLabelKey() and getChildElementsLabelKey() as they will be called by JSPs.
   * @return true if the concrete class is a subclass of {@link HierarchicalEntityChangeset}, or implements its methods.
   */
  public abstract boolean isHierarchicalType();

  /**
   * Should be overridden by sub-classes to actually check for relation changes.
   * @return true if there are any changes to the BB's relations. Default implementation always return false.
   */
  public boolean isRelationsChanged() {
    return false;
  }

  public void setNameFrom(String nameFrom) {
    this.nameFrom = nameFrom;
  }

  public void setNameTo(String nameTo) {
    this.nameTo = nameTo;
  }

  public void setDescriptionFrom(String descriptionFrom) {
    this.descriptionFrom = descriptionFrom;
  }

  public void setDescriptionTo(String descriptionTo) {
    this.descriptionTo = descriptionTo;
  }

  public boolean hasNoticeableChanges() {
    return isRelationsChanged() || isAttributesChanged() || isBasicPropertiesChanged() || isInitialChangeset() || isDescriptionChanged()
        || isNameChanged();
  }
}