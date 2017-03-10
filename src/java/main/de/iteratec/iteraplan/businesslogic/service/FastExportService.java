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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelper;


/**
 * This interface should provide access to functionality that is hiding the preparation of a fast
 * export from the actual export service in means of simulating the frontend logic of the ordinary
 * graphical export.
 */
public interface FastExportService {

  RuntimePeriod getEncompassingRuntimePeriod(List<BuildingBlock> elementsToBeEncompassedInTimespan);

  List<BuildingBlock> retrieveBuildingBlockListForMasterplanFastExport(BuildingBlock element, MasterplanOptionsBean bean, String diagramVariant);
  
  List<InformationSystemRelease> retrieveRelatedIsForInformationFlowFastExport(BuildingBlock startElement);

  BuildingBlock getStartElement(Integer id, String bbType);

  List<InformationSystemRelease> getInformationFlowReleases(BuildingBlock startElement);

  String SORT_HIERARCHICAL = "hierarchical";

  enum DiagramVariant {

    MASTERPLAN_HIERARCHY("Hierarchy"), // Similar to: Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL
    MASTERPLAN_PROJECTS("Projects"), // ": Constants.BB_PROJECT_PLURAL
    MASTERPLAN_TECHNICAL_COMPONENTS("TechnicalComponents"), // ":
    // Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL
    LANDSCAPE_BY_BUSINESSUNITS("BusinessLandscapeByBusinessUnits"), LANDSCAPE_BY_BUSINESSPROCESSES("BusinessLandscapeByBusinessProcesses"), LANDSCAPE_BY_PRODUCTS(
        "BusinessLandscapeByProducts"), LANDSCAPE_BY_BUSINESSFUNCTIONS("BusinessLandscapeByBusinessFunctions"), LANDSCAPE_BY_PROJECTS(
        "BusinessLandscapeByProjects"), LANDSCAPE_TECHNICAL("TechnicalLandscape"), UNKNOWN("Unknown");

    private final String variantName;

    DiagramVariant(String variantName) {
      this.variantName = variantName;
    }

    public String getName() {
      return this.variantName;
    }

    public static DiagramVariant fromName(String name) {
      for (DiagramVariant variant : DiagramVariant.values()) {
        if (variant.getName().equals(name)) {
          return variant;
        }
      }
      return UNKNOWN;
    }

  }

  InformationFlowOptionsBean retrieveInformationFlowOptionsForFastExport();

  MasterplanOptionsBean initMasterplanOptionsForFastExport(String serverUrl);
  
  void configureMasterplanOptionsForFastExport(MasterplanOptionsBean options, RuntimePeriod encompassingTimespan);
  
  LandscapeOptionsBean retrieveLandscapeOptionsForFastExport();

  LandscapeDiagramHelper<? extends BuildingBlock> retrieveLandscapeHelper(String diagramVariant, BuildingBlock element);

}
