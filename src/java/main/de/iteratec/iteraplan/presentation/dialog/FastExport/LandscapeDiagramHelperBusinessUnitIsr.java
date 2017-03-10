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
package de.iteratec.iteraplan.presentation.dialog.FastExport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Helper for creating a fast export landscape diagram from a BusinessUnit (start element). Content
 * type: information system releases. Column type: BusinessUnits. Row type: business units.
 * 
 * @author est
 */
public class LandscapeDiagramHelperBusinessUnitIsr extends LandscapeDiagramHelper<BusinessUnit> {

  /**
   * @param startElement
   *          The information system release to start from.
   */
  public LandscapeDiagramHelperBusinessUnitIsr(BusinessUnit startElement, TypeOfBuildingBlock columnType, TypeOfBuildingBlock rowType) {

    super(startElement, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, columnType, rowType);
  }

  /**
   * filters the column elements
   */
  protected List<BuildingBlock> filterColumnElements() {
    return filterColumnElementsByBusinessMapping();
  }

  /**
   * filters the row elements
   */
  protected List<BuildingBlock> filterRowElements() {

    // Here, the filtered column elements are collected
    List<BuildingBlock> filteredList = new ArrayList<BuildingBlock>();

    for (BuildingBlock rowElement : getRowElements()) { // Loop through all row Elements (ex. BU)

      // Projects don't have Business Mappings directly (go via ISRs)
      if (getRowTypeOfBb().equals(TypeOfBuildingBlock.PROJECT)) {

        Project curProject = (Project) rowElement;
        // Include only the Projects who have ISRs, who have Mappings, who then have the
        // startElement
        Set<InformationSystemRelease> curProjectISRs = curProject.getInformationSystemReleases();

        for (InformationSystemRelease currentISR : curProjectISRs) {
          Set<BusinessMapping> mappings = currentISR.getBusinessMappings();
          // add the current row to the filter list if it has a connection to the start element
          if (mappingsIncludeStartElement(mappings)) {
            filteredList.add(rowElement);
          }
        }
      }
      else {

        Set<BusinessMapping> mappings = getBusinessMappings(rowElement); // Mapping
        // add the current row to the filter list if it has a connection to the start element
        if (mappingsIncludeStartElement(mappings)) {
          filteredList.add(rowElement);
        }
      }
    }
    return filteredList;
  }

  /**
   * adopt all content elements unfiltered
   */
  @Override
  protected List<BuildingBlock> filterContentElements() {
    return this.getContentElements();
  }

}
