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
package de.iteratec.iteraplan.businesslogic.service.diffs;

import java.util.Collection;
import java.util.List;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.diffs.model.HierarchicalEntityChangeset;
import de.iteratec.iteraplan.diffs.model.InformationSystemReleaseChangeset;
import de.iteratec.iteraplan.diffs.model.ReleaseSequenceTypeChangeset;
import de.iteratec.iteraplan.diffs.model.UsageTypeChangeset;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.interfaces.UsageEntity;


public final class RelationDiffer {

  private static final Logger LOGGER = Logger.getIteraplanLogger(RelationDiffer.class);

  private RelationDiffer() {
    // prevent instantiation
  }

  public static void addHierarchyDiffs(HierarchicalEntityChangeset changeset, AbstractHierarchicalEntity<? extends BuildingBlock> fromBb,
                                       AbstractHierarchicalEntity<? extends BuildingBlock> toBb) {

    // Compare Parents
    String toBBParentName = getNameOfBBOrEmpty(toBb.getParentElement());
    String fromBBParentName = getNameOfBBOrEmpty(fromBb.getParentElement());
    if (!toBBParentName.equals(fromBBParentName)) {

      LOGGER.debug(" Parent changed from " + fromBBParentName + " to " + toBBParentName);
      changeset.setParentFrom(fromBb.getParentElement());
      changeset.setParentTo(toBb.getParentElement());
    }

    // Loop through all Children of FROM, check if no longer still exist in TO: Removed
    // NOTE: This uses .equals, which considers the ID+Name+Description, so if any of those
    // changed,
    // that child is considered Removed+Added [CONFIRMED, not necessarily a bug]
    for (BuildingBlock curFromBbChild : fromBb.getChildrenAsList()) {

      List<? extends BuildingBlock> toBbChildrenList = toBb.getChildrenAsList();
      if (!toBbChildrenList.contains(curFromBbChild)) {

        LOGGER.debug(curFromBbChild.getNonHierarchicalName() + " was removed");
        changeset.getChildrenRemoved().add(curFromBbChild);
      }
    }

    // Loop through all Children of TO, check if they didn't exist in FROM: Add
    for (BuildingBlock curToBbChild : toBb.getChildrenAsList()) {

      List<? extends BuildingBlock> fromBbChildrenList = fromBb.getChildrenAsList();
      if (!fromBbChildrenList.contains(curToBbChild)) {

        LOGGER.debug(curToBbChild.getNonHierarchicalName() + " was added");
        changeset.getChildrenAdded().add(curToBbChild);
      }
    }

  }

  public static void addIsrCompositionDiffs(InformationSystemReleaseChangeset changeset, InformationSystemRelease fromBb,
                                            InformationSystemRelease toBb) {
    // Compare Parents
    String toBBParentName = getNameOfBBOrEmpty(toBb.getParentElement());
    String fromBBParentName = getNameOfBBOrEmpty(fromBb.getParentElement());
    if (!toBBParentName.equals(fromBBParentName)) {

      LOGGER.debug(" Parent changed from " + fromBBParentName + " to " + toBBParentName);
      changeset.setParentFrom(fromBb.getParentElement());
      changeset.setParentTo(toBb.getParentElement());
    }

    // Loop through all Children of FROM, check if no longer still exist in TO: Removed
    // NOTE: This uses .equals, which considers the ID+Name+Description, so if any of those
    // changed,
    // that child is considered Removed+Added [CONFIRMED, not necessarily a bug]
    for (InformationSystemRelease curFromBbChild : fromBb.getChildrenAsList()) {

      List<? extends BuildingBlock> toBbChildrenList = toBb.getChildrenAsList();
      if (!toBbChildrenList.contains(curFromBbChild)) {

        LOGGER.debug(curFromBbChild.getNonHierarchicalName() + " was removed");
        changeset.getChildrenRemoved().add(curFromBbChild);
      }
    }

    // Loop through all Children of TO, check if they didn't exist in FROM: Add
    for (InformationSystemRelease curToBbChild : toBb.getChildrenAsList()) {

      List<? extends BuildingBlock> fromBbChildrenList = fromBb.getChildrenAsList();
      if (!fromBbChildrenList.contains(curToBbChild)) {

        LOGGER.debug(curToBbChild.getNonHierarchicalName() + " was added");
        changeset.getChildrenAdded().add(curToBbChild);
      }
    }
  }

  /**
   * Returns the name of a BuildingBlock, or "" if the BB is null
   */
  private static <T extends BuildingBlock> String getNameOfBBOrEmpty(T bb) {
    return (bb == null ? "" : bb.getNonHierarchicalName());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <T extends Sequence> void addReleaseSequenceDiffs(ReleaseSequenceTypeChangeset<T> changeset, T fromBb, T toBb) {
    RelationDiffer.addChange(toBb.getPredecessors(), fromBb.getPredecessors(), changeset.getPredecessorsAdded());
    RelationDiffer.addChange(fromBb.getPredecessors(), toBb.getPredecessors(), changeset.getPredecessorsRemoved());

    RelationDiffer.addChange(toBb.getSuccessors(), fromBb.getSuccessors(), changeset.getSuccessorsAdded());
    RelationDiffer.addChange(fromBb.getSuccessors(), toBb.getSuccessors(), changeset.getSuccessorsRemoved());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <T extends UsageEntity> void addUsageDiffs(UsageTypeChangeset<T> changeset, T fromBb, T toBb) {
    RelationDiffer.addChange(toBb.getBaseComponents(), fromBb.getBaseComponents(), changeset.getBaseComponentsAdded());
    RelationDiffer.addChange(fromBb.getBaseComponents(), toBb.getBaseComponents(), changeset.getBaseComponentsRemoved());

    RelationDiffer.addChange(toBb.getParentComponents(), fromBb.getParentComponents(), changeset.getParentComponentsAdded());
    RelationDiffer.addChange(fromBb.getParentComponents(), toBb.getParentComponents(), changeset.getParentComponentsRemoved());
  }

  public static <T> void addChange(Collection<T> from, Collection<T> to, Collection<T> add) {
    for (T c : from) {
      if (!to.contains(c)) {
        add.add(c);
      }
    }
  }

}
