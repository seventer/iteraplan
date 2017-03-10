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

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.diffs.model.InformationSystemReleaseChangeset;
import de.iteratec.iteraplan.diffs.model.InterfaceChangeset;
import de.iteratec.iteraplan.diffs.model.RuntimePeriodCarryingChangeset;
import de.iteratec.iteraplan.diffs.model.TechnicalComponentReleaseChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.RuntimePeriodDelegate;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;

public final class CorePropertyDiffer {

  private static final Logger LOGGER = Logger.getIteraplanLogger(CorePropertyDiffer.class);

  private CorePropertyDiffer() {
    // prevent instantiation
  }

  public static void addNameDescriptionDiffs(HistoryBBChangeset changeset, BuildingBlock fromBb, BuildingBlock toBb) {

    if (!toBb.getNonHierarchicalName().equals(fromBb.getNonHierarchicalName())) {
      LOGGER.debug(" Name changed from " + fromBb.getNonHierarchicalName() + " to " + toBb.getNonHierarchicalName());
      changeset.setNameFrom(fromBb.getNonHierarchicalName());
      changeset.setNameTo(toBb.getNonHierarchicalName());
    }

    if (!toBb.getDescription().equals(fromBb.getDescription())) {
      changeset.setDescriptionFrom(fromBb.getDescription());
      changeset.setDescriptionTo(toBb.getDescription());
    }
  }

  public static void addNameDescriptionDiffs(TechnicalComponentReleaseChangeset changeset, TechnicalComponentRelease fromBb, TechnicalComponentRelease toBb) {

    if (!toBb.getReleaseName().equals(fromBb.getReleaseName())) {
      LOGGER.debug(" Name changed from " + fromBb.getReleaseName() + " to " + toBb.getReleaseName());
      changeset.setNameFrom(fromBb.getReleaseName());
      changeset.setNameTo(toBb.getReleaseName());
    }

    if (!toBb.getDescription().equals(fromBb.getDescription())) {
      changeset.setDescriptionFrom(fromBb.getDescription());
      changeset.setDescriptionTo(toBb.getDescription());
    }
  }

  public static void addNameDescriptionDiffs(InterfaceChangeset changeset, InformationSystemInterface fromBb, InformationSystemInterface toBb) {
    // Name change?
    // ISIs can't use the same code to compare their names, because getNonHierarchicalName()/getIdentityString() does not retrieve their name
    // All other BBTypes have their name compare code inside the HistoryBBChangeset
    if (!toBb.getName().equals(fromBb.getName())) {
      changeset.setNameFrom(fromBb.getName());
      changeset.setNameTo(toBb.getName());
    }
    if (!toBb.getDescription().equals(fromBb.getDescription())) {
      changeset.setDescriptionFrom(fromBb.getDescription());
      changeset.setDescriptionTo(toBb.getDescription());
    }
  }

  public static void addRuntimeDiffs(RuntimePeriodCarryingChangeset changeset, RuntimePeriodDelegate fromBb, RuntimePeriodDelegate toBb) {
    if (fromBb.getRuntimePeriodNullSafe().getStart() == null) {
      if (toBb.getRuntimePeriodNullSafe().getStart() != null) {
        changeset.setRuntimeStartAdded(toBb.getRuntimePeriod().getStart());
      }
    }
    else {
      if (toBb.getRuntimePeriodNullSafe().getStart() == null) {
        changeset.setRuntimeStartRemoved(fromBb.getRuntimePeriodNullSafe().getStart());
      }
      else if (fromBb.getRuntimePeriodNullSafe().getStart().getTime() != toBb.getRuntimePeriodNullSafe().getStart().getTime()) {
        changeset.setRuntimeStartRemoved(fromBb.getRuntimePeriodNullSafe().getStart());
        changeset.setRuntimeStartAdded(toBb.getRuntimePeriodNullSafe().getStart());
      }
    }

    if (fromBb.getRuntimePeriodNullSafe().getEnd() == null) {
      if (toBb.getRuntimePeriodNullSafe().getEnd() != null) {
        changeset.setRuntimeEndAdded(toBb.getRuntimePeriodNullSafe().getEnd());
      }
    }
    else {
      if (toBb.getRuntimePeriodNullSafe().getEnd() == null) {
        changeset.setRuntimeEndRemoved(fromBb.getRuntimePeriodNullSafe().getEnd());
      }
      else if (fromBb.getRuntimePeriodNullSafe().getEnd().getTime() !=toBb.getRuntimePeriodNullSafe().getEnd().getTime()) {
        changeset.setRuntimeEndRemoved(fromBb.getRuntimePeriodNullSafe().getEnd());
        changeset.setRuntimeEndAdded(toBb.getRuntimePeriodNullSafe().getEnd());
      }
    }
  }

  public static void addStatusDiff(InformationSystemReleaseChangeset changeset, InformationSystemRelease fromBb, InformationSystemRelease toBb) {
    if (!fromBb.getTypeOfStatus().equals(toBb.getTypeOfStatus())) {
      changeset.setStatusTo(toBb.getTypeOfStatus().getValue());
      changeset.setStatusFrom(fromBb.getTypeOfStatus().getValue());
    }
  }

  public static void addStatusDiff(TechnicalComponentReleaseChangeset changeset, TechnicalComponentRelease fromBb, TechnicalComponentRelease toBb) {
    if (!fromBb.getTypeOfStatus().equals(toBb.getTypeOfStatus())) {
      changeset.setStatusTo(toBb.getTypeOfStatus().getValue());
      changeset.setStatusFrom(fromBb.getTypeOfStatus().getValue());
    }
  }

  @SuppressWarnings("boxing")
  public static void addAvailableForInterfacesDiff(TechnicalComponentReleaseChangeset changeset, TechnicalComponentRelease fromBb, TechnicalComponentRelease toBb) {
    if (fromBb.getTechnicalComponent().isAvailableForInterfaces() != toBb.getTechnicalComponent().isAvailableForInterfaces()) {
      changeset.setAvailableForInterfacesTo(toBb.getTechnicalComponent().isAvailableForInterfaces());
      changeset.setAvailableForInterfacesFrom(fromBb.getTechnicalComponent().isAvailableForInterfaces());
    }
  }

  public static void addDirectionDiff(InterfaceChangeset changeset, InformationSystemInterface fromBb, InformationSystemInterface toBb) {
    if (!fromBb.getDirection().equals(toBb.getDirection())) {
      changeset.setDirectionFrom(fromBb.getDirection());
      changeset.setDirectionTo(toBb.getDirection());
    }
  }

  public static void addConnectedIsrDiffs(InterfaceChangeset changeset, InformationSystemInterface fromBb, InformationSystemInterface toBb) {
    if (!fromBb.getInformationSystemReleaseA().equals(toBb.getInformationSystemReleaseA())) {
      changeset.setRemovedInformationSystemA(fromBb.getInformationSystemReleaseA());
      changeset.setAddedInformationSystemA(toBb.getInformationSystemReleaseA());
    }

    if (!fromBb.getInformationSystemReleaseB().equals(toBb.getInformationSystemReleaseB())) {
      changeset.setRemovedInformationSystemB(fromBb.getInformationSystemReleaseB());
      changeset.setAddedInformationSystemB(toBb.getInformationSystemReleaseB());
    }
  }

}
