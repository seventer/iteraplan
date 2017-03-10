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

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Task;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * The base class for all ms project file exporters
 * 
 * @author rrs
 */
public abstract class MsProjectExporterBase implements MsProjectExport {

  public enum ExportType {
    XML_WITHOUT_SUBORDINATED_BLOCKS, XML_WITH_SUBORDINATED_BLOCKS, MPX_WITHOUT_SUBORDINATED_BLOCKS, MPX_WITH_SUBORDINATED_BLOCKS
  }

  private ExportType                    type;
  private List<? extends BuildingBlock> results;
  private ProjectFile                   file;
  private HttpServletRequest            request;

  MsProjectExporterBase(List<? extends BuildingBlock> results, HttpServletRequest request, ExportType type) {
    this.results = results;
    this.type = type;
    this.request = request;
    initProjectFile();
  }

  /**
   * Initializes the member project file and sets the default configuration
   */
  private void initProjectFile() {

    this.file = new ProjectFile();
    file.setAutoTaskID(true);
    file.setAutoTaskUniqueID(true);
    file.setAutoResourceID(true);
    file.setAutoResourceUniqueID(true);
    file.setAutoOutlineLevel(true);
    file.setAutoOutlineNumber(true);
    file.setAutoWBS(true);
    file.setAutoCalendarUniqueID(true);
    file.addDefaultBaseCalendar();

  }

  /**
   * Finds the earliest task.start for setting the project.start
   */
  void initProjectFileHeader() {

    ProjectHeader header = file.getProjectHeader();
    List<Task> tasks = file.getAllTasks();
    Iterator<Task> it = tasks.iterator();
    long earliest = Long.MAX_VALUE;

    while (it.hasNext()) {

      Task task = it.next();

      if (task.getStart().getTime() < earliest) {
        earliest = task.getStart().getTime();
        header.setStartDate(task.getStart());
      }
    }
  }

  protected List<? extends BuildingBlock> getResults() {
    return results;
  }

  protected ProjectFile getFile() {
    return file;
  }

  protected HttpServletRequest getRequest() {
    return request;
  }

  protected ExportType getType() {
    return type;
  }
}
