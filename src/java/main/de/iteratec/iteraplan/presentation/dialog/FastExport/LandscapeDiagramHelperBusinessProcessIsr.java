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
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Helper for creating a fast export landscape diagram from a product (start element). Content type:
 * information system releases. Column type: products. Row type: business units.
 * 
 * @author est
 */
public class LandscapeDiagramHelperBusinessProcessIsr extends LandscapeDiagramHelper<BusinessProcess> {

  /**
   * @param startElement
   *          The information system release to start from.
   */
  public LandscapeDiagramHelperBusinessProcessIsr(BusinessProcess startElement, TypeOfBuildingBlock columnType, TypeOfBuildingBlock rowType) {

    super(startElement, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, columnType, rowType);
  }

  /**
   * filters the column elements
   */
  protected List<BuildingBlock> filterColumnElements() {

    // Here it will always just be the one the user selected
    List<BuildingBlock> filteredList = new ArrayList<BuildingBlock>();
    filteredList.add(getStartElement());
    return filteredList;
  }

  /**
   * filters the row elements
   */
  protected List<BuildingBlock> filterRowElements() {

    // Here, the filtered column elements are collected
    List<BuildingBlock> filteredList = new ArrayList<BuildingBlock>();
    if (getRowTypeOfBb().equals(TypeOfBuildingBlock.BUSINESSFUNCTION)) {

      Set<BusinessMapping> mappings = getBusinessMappings(getStartElement());

      // Loop through all mappings from startElement
      for (BusinessMapping mapping : mappings) {

        // Get the current Mapping's ISR
        InformationSystemRelease isrContent = (InformationSystemRelease) mapping.getAssociatedElement(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

        if (isrContent != null) {

          // Get the list of the current ISR's BusinessFunctions
          Set<BusinessFunction> currentBusinessFunctions = isrContent.getBusinessFunctions();

          // Add all these BFs to the result
          for (BusinessFunction currentBF : currentBusinessFunctions) {
            filteredList.add(currentBF);
          }
        }
      }
    }
    else { // Not Business Function

      for (BuildingBlock rowElement : getRowElements()) { // Loop through all row Elements (ex. BU)

        Set<BusinessMapping> mappings = getBusinessMappings(rowElement); // Mapping
        // add the current axis element to the filter list if it has a connection to the start
        // element
        if (mappingsIncludeStartElement(mappings)) {
          filteredList.add(rowElement); // Include current row Element if its businessMap contains

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
