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
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Helper for creating a fast export landscape diagram from a Project (start element). Content type:
 * information system releases. Column type: business units. Row type: Project.
 * 
 * @author est
 */
public class LandscapeDiagramHelperProjectIsr extends LandscapeDiagramHelper<Project> {

  /**
   * @param startElement
   *          The information system release to start from.
   */
  public LandscapeDiagramHelperProjectIsr(Project startElement, TypeOfBuildingBlock columnType, TypeOfBuildingBlock rowType) {

    super(startElement, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, columnType, rowType);
  }

  /**
   * filters the column elements
   */
  protected List<BuildingBlock> filterColumnElements() {

    // Here, the filtered column elements are collected
    List<BuildingBlock> filteredList = new ArrayList<BuildingBlock>();

    // Loop through all Business Units
    for (BuildingBlock columnElement : getColumnElements()) {
      Set<BusinessMapping> mappings = getBusinessMappings(columnElement);

      // Loop through all their mappings
      for (BusinessMapping mapping : mappings) {
        InformationSystemRelease content = (InformationSystemRelease) mapping.getAssociatedElement(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

        // If the current Mapping's ISR's list of Project contains the one the user selected, add
        // the current BU to the output
        if (content != null && content.getProjects().contains(getStartElement())) {
          filteredList.add(columnElement);
        }
      }
    }
    return filteredList;
  }

  /**
   * filters the row elements
   */
  protected List<BuildingBlock> filterRowElements() {

    // Only start element shown
    List<BuildingBlock> filteredList = new ArrayList<BuildingBlock>();

    filteredList.add(getStartElement());
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
