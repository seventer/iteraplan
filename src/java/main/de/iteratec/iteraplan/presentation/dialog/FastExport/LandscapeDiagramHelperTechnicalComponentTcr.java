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

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Helper for creating a fast export landscape diagram from a technical component release (start
 * element). Content type: technical component releases. Column type: information system releases.
 * Row type: architectural domains.
 * 
 * @author est
 */
public class LandscapeDiagramHelperTechnicalComponentTcr extends LandscapeDiagramHelper<TechnicalComponentRelease> {

  /**
   * @param startElement
   *          The technical component release to start from.
   */
  public LandscapeDiagramHelperTechnicalComponentTcr(TechnicalComponentRelease startElement) {

    super(startElement, TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE,
        TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
  }

  /**
   * filter out content: only start element as technical component
   */
  @Override
  protected List<BuildingBlock> filterContentElements() {

    List<BuildingBlock> filteredContent = new ArrayList<BuildingBlock>();
    filteredContent.add(getStartElement());
    return filteredContent;

  }

  @Override
  protected List<? extends BuildingBlock> filterColumnElements() {

    List<BuildingBlock> filteredList = new ArrayList<BuildingBlock>();

    if (TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.equals(getColumnTypeOfBb())) {
      filteredList.addAll(getStartElement().getInformationSystemReleases());

      return filteredList;
    }
    else {
      throw new IteraplanBusinessException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

  @Override
  protected List<? extends BuildingBlock> filterRowElements() {

    List<BuildingBlock> filteredList = new ArrayList<BuildingBlock>();

    if (TypeOfBuildingBlock.ARCHITECTURALDOMAIN.equals(getRowTypeOfBb())) {
      filteredList.addAll(getStartElement().getArchitecturalDomains());

      return filteredList;
    }
    else {
      throw new IteraplanBusinessException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }

  }

}
