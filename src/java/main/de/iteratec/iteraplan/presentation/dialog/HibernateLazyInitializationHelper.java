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
package de.iteratec.iteraplan.presentation.dialog;

import java.util.List;

import org.hibernate.Hibernate;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry.COLUMN_TYPE;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.UsageEntity;

/**
 * A helper class to initialize the lazy collections used in {@link BuildingBlock} entities. This is 
 * needed in tabular reports, since the collections must be initialized before rendering the page.
 * 
 * @see ReportBaseFrontendServiceImpl
 */
final class HibernateLazyInitializationHelper {
  
  /** Prevents instances of this class. */
  private HibernateLazyInitializationHelper() {
    // nothing to do
  }
  
  /**
   * Initializes the collections for the specified list of building blocks. To improve the performance, some 
   * of the collections will be initialized only if the concerned column is contained in the {@code visibleColums} 
   * list.
   * 
   * @param buildingBlocks the list of building blocks
   * @param visibleColumns the list of visible columns, configured in tabular reports
   */
  public static void initialize(List<? extends BuildingBlock> buildingBlocks, List<ColumnEntry> visibleColumns) {
    initSuccessorsAndPredecessors(buildingBlocks);
    initParentAndChildren(buildingBlocks);
    initBaseAndParentElements(buildingBlocks);

    if (containsVisibleColumnWithType(visibleColumns, ColumnEntry.COLUMN_TYPE.ATTRIBUTE)) {
      initAttributes(buildingBlocks);
    }

    if (containsVisibleColumnWithType(visibleColumns, ColumnEntry.COLUMN_TYPE.SEAL)) {
      initSeals(buildingBlocks);
    }
  }
  
  private static void initAttributes(List<? extends BuildingBlock> results) {
    for (BuildingBlock bb : results) {
      Hibernate.initialize(bb.getAttributeValueAssignments());
    }
  }

  private static void initSuccessorsAndPredecessors(List<? extends BuildingBlock> results) {
    for (BuildingBlock bb : results) {
      if (bb instanceof Sequence<?>) {
        Sequence<?> tmp = (Sequence<?>) bb;
        Hibernate.initialize(tmp.getPredecessors());
        Hibernate.initialize(tmp.getSuccessors());
      }
    }
  }

  private static void initParentAndChildren(List<? extends BuildingBlock> results) {
    for (BuildingBlock bb : results) {
      if (bb instanceof HierarchicalEntity<?>) {
        HierarchicalEntity<?> tmp = (HierarchicalEntity<?>) bb;
        Hibernate.initialize(tmp.getParentElement());
        Hibernate.initialize(tmp.getChildren());
      }
    }
  }

  private static void initBaseAndParentElements(List<? extends BuildingBlock> results) {
    for (BuildingBlock bb : results) {
      if (bb instanceof UsageEntity<?>) {
        UsageEntity<?> tmp = (UsageEntity<?>) bb;
        Hibernate.initialize(tmp.getBaseComponents());
        Hibernate.initialize(tmp.getParentComponents());
      }
    }
  }

  /**
   * Initializes the {@link InformationSystemRelease#getSeals()} list for all information systems in 
   * the specified {@code results} list.
   * 
   * @param results the list containing the results
   */
  private static void initSeals(List<? extends BuildingBlock> results) {
    for (BuildingBlock bb : results) {
      if (bb instanceof InformationSystemRelease) {
        InformationSystemRelease isr = (InformationSystemRelease) bb;
        Hibernate.initialize(isr.getSeals());
      }
    }
  }

  /**
   * Returns {@code true} if the specified list of visible column entries contains the entry with the specified {@link COLUMN_TYPE}.
   * 
   * @param visibleColumns the visible columns 
   * @param columnType the column type to look for
   * @return a flag indicating if the visible column contain column with the specified type
   */
  private static boolean containsVisibleColumnWithType(List<ColumnEntry> visibleColumns, COLUMN_TYPE columnType) {
    for (ColumnEntry columnEntry : visibleColumns) {
      if (columnEntry.getEnumType() == columnType) {
        return true;
      }
    }

    return false;
  }
}
