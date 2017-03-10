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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Helper class including the most important methods for the "MS Project Export"
 * 
 * @author rrs
 */
final class MsProjectHelper {

  /** empty private constructor */
  private MsProjectHelper() {
    // hide constructor
  }

  /**
   * This method starts the task creation
   * 
   * @param parent
   *          project file will be used as parent
   * @param block
   *          draft of task
   * @param file
   *          needed by later methods
   * @param request
   *          needed by later methods
   * @return data filled task instance
   */
  static Task buildingBlockToRootTask(ProjectFile parent, BuildingBlock block, ProjectFile file, HttpServletRequest request) {
    return initTask(parent.addTask(), block, file.getCalendar(), request);
  }

  /**
   * This method starts the task creation
   * 
   * @param parent
   *          task will be used as parent
   * @param block
   *          draft of task
   * @param file
   *          needed by later methods
   * @param request
   *          needed by later methods
   * @return data filled task instance
   */
  static Task buildingBlockToTask(Task parent, BuildingBlock block, ProjectFile file, HttpServletRequest request) {
    return initTask(parent.addTask(), block, file.getCalendar(), request);
  }

  /**
   * Decides how the task will be initialized
   * 
   * @param task
   *          The task to be initialized
   * @param block
   *          The data containing draft
   * @param calendar
   *          The project calendar contains the working days
   * @param request
   *          The request is used for building the URL
   * @return Returns initialized task
   */
  static Task initTask(Task task, BuildingBlock block, ProjectCalendar calendar, HttpServletRequest request) {

    if (block instanceof TechnicalComponentRelease) {
      initTechnicalComponentTask(task, (TechnicalComponentRelease) block, calendar, request);
    }
    else if (block instanceof InformationSystemRelease) {
      initInformationSystemTask(task, (InformationSystemRelease) block, calendar, request);
    }
    else if (block instanceof Project) {
      initProjectTask(task, (Project) block, calendar, request);
    }

    return task;
  }

  /**
   * Initializes the technical component task
   * 
   * @param task
   *          The task to be initialized
   * @param block
   *          The data containing draft
   * @param calendar
   *          The project calendar contains the working days
   * @param request
   *          The request is used for building the URL
   */
  static void initTechnicalComponentTask(Task task, TechnicalComponentRelease release, ProjectCalendar calendar, HttpServletRequest request) {

    String url = URLBuilder.getEntityURL(release, URLBuilder.getApplicationURL(request));
    task.setName(release.getName());
    task.setNotes(getStatus(release.getTypeOfStatus().toString()) + " " + url + release.getDescription());
    RuntimePeriod runtime = release.getRuntimePeriodNullSafe();
    setTaskDate(task, runtime, calendar);

  }

  /**
   * Initializes the project task
   * 
   * @param task
   *          The task to be initialized
   * @param block
   *          The data containing draft
   * @param calendar
   *          The project calendar contains the working days
   * @param request
   *          The request is used for building the URL
   */
  static void initProjectTask(Task task, Project project, ProjectCalendar calendar, HttpServletRequest request) {

    String url = URLBuilder.getEntityURL(project, URLBuilder.getApplicationURL(request));
    task.setName(project.getNonHierarchicalName());
    task.setNotes(url + project.getDescription());
    RuntimePeriod runtime = project.getRuntimePeriod();
    setTaskDate(task, runtime, calendar);

  }

  /**
   * Initializes the information system task
   * 
   * @param task
   *          The task to be initialized
   * @param block
   *          The data containing draft
   * @param calendar
   *          The project calendar contains the working days
   * @param request
   *          The request is used for building the URL
   */
  static void initInformationSystemTask(Task task, InformationSystemRelease release, ProjectCalendar calendar, HttpServletRequest request) {

    String url = URLBuilder.getEntityURL(release, URLBuilder.getApplicationURL(request));
    task.setName(release.getNonHierarchicalName());
    task.setNotes(getStatus(release.getTypeOfStatus().toString()) + " " + url + release.getDescription());
    RuntimePeriod runtime = release.getRuntimePeriod();
    setTaskDate(task, runtime, calendar);
  }

  /**
   * Sets date and duration of the task
   * 
   * @param task
   *          The task to be set
   * @param runtime
   *          The object including date data
   * @param calendar
   *          The project calendar contains the working days
   */
  static void setTaskDate(Task task, RuntimePeriod runtime, ProjectCalendar calendar) {

    int zero = 0;
    Date start, end;

    if (runtime != null) {

      if (runtime.getStart() != null) {
        start = new Date(runtime.getStart().getTime());
      }
      else {
        // set start to end if end exists
        if (runtime.getEnd() != null) {
          start = new Date(runtime.getEnd().getTime());
        } // no start and no end date set
        else {
          start = new Date();
        }
      }

      if (runtime.getEnd() != null) {
        end = new Date(runtime.getEnd().getTime());
      }
      else {
        // set end to start if start exists
        if (runtime.getStart() != null) {
          end = new Date(runtime.getStart().getTime());
        } // no start and no end date set
        else {
          end = new Date();
        }
      }
      task.setDuration(calendar.getDuration(start, end));
    }
    else { // no start and no end date set
      start = new Date();
      end = new Date();
      task.setDuration(Duration.getInstance(zero, TimeUnit.DAYS));
    }

    task.setStart(start);
    task.setEarlyStart(start);
    task.setLateStart(start);

    task.setFinish(end);
    task.setEarlyFinish(end);
    task.setLateFinish(end);

    task.setConstraintDate(start);
    task.setConstraintType(ConstraintType.MUST_START_ON);
  }

  /**
   * Returns the typeOfStatus as a string for task description
   * 
   * @param typeOfStatus
   *          The current status of the building block
   * @return string for description
   */
  static String getStatus(String typeOfStatus) {

    // Message access uses english application resources as default
    // if nothing is set.
    return MessageAccess.getStringOrNull(typeOfStatus, null);
  }

}
