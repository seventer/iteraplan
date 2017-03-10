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
package de.iteratec.iteraplan.businesslogic.exchange.msproject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * The concrete exporter is responsible for creating technicalComponentTasks
 * 
 * @author rrs
 */
public class MsProjectTechnicalComponentExport extends MsProjectExporterBase {

  public MsProjectTechnicalComponentExport(List<? extends BuildingBlock> results, HttpServletRequest request, ExportType type) {
    super(results, request, type);
  }

  /**
   * Creates ms project file from query results
   * 
   * @return - returns the project file filled with data
   */
  public ProjectFile createMsProjectFile() {

    // Creating Tasks

    Map<String, Task> tasks = new TreeMap<String, Task>();

    for (BuildingBlock block : getResults()) {
      TechnicalComponentRelease technicalComponent = (TechnicalComponentRelease) block;
      Task task = MsProjectHelper.buildingBlockToRootTask(getFile(), technicalComponent, this.getFile(), getRequest());
      tasks.put(task.getName(), task);
    }

    // Adding Predecessors

    for (BuildingBlock block : getResults()) {
      TechnicalComponentRelease actual = (TechnicalComponentRelease) block;
      Set<TechnicalComponentRelease> predecessors = actual.getPredecessors();
      Task actualT = tasks.get(actual.getNonHierarchicalName());

      for (TechnicalComponentRelease tc : predecessors) {
        Task parentT = tasks.get(tc.getNonHierarchicalName());
        if (parentT != null) {
          actualT.addPredecessor(parentT);
        }
      }
    }
    initProjectFileHeader();
    return getFile();
  }
}
