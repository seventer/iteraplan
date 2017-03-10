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
package de.iteratec.iteraplan.presentation.problemreports;

import java.io.PrintWriter;

import com.google.common.base.Joiner;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;


/**
 * {@link ProblemReportPart} for information about the user's permissions. User name is not included.
 */
final class PermissionProblemReportPart extends AbstractProblemReportPart {

  private PermissionProblemReportPart(String filename) {
    super(filename);
  }

  static PermissionProblemReportPart generatePermissionReport(String filename) {
    PermissionProblemReportPart reportPart = new PermissionProblemReportPart(filename);

    UserContext currentUserContext = UserContext.getCurrentUserContext();
    Permissions perms = currentUserContext.getPerms();

    PrintWriter permWriter = reportPart.getWriter();
    permWriter.println("User Datasource: " + currentUserContext.getDataSource());
    permWriter.println("User Locale: " + currentUserContext.getLocale());
    permWriter.println("User Roles: " + Joiner.on(",").join(currentUserContext.getRoles()));
    permWriter.println("User Is Administrator: " + perms.isUserIsAdministrator());
    permWriter.println("-");
    permWriter.println("Functional (=read) permissions on Building Blocks:");
    permWriter.println("  AD: " + perms.getUserHasFuncPermAD());
    permWriter.println("  BD: " + perms.getUserHasFuncPermBD());
    permWriter.println("  BF: " + perms.getUserHasFuncPermBF());
    permWriter.println("  BM: " + perms.getUserHasFuncPermBM());
    permWriter.println("  BO: " + perms.getUserHasFuncPermBO());
    permWriter.println("  BP: " + perms.getUserHasFuncPermBP());
    permWriter.println("  BU: " + perms.getUserHasFuncPermBU());
    permWriter.println("  IE: " + perms.getUserHasFuncPermIE());
    permWriter.println("  INT: " + perms.getUserHasFuncPermINT());
    permWriter.println("  IS: " + perms.getUserHasFuncPermIS());
    permWriter.println("  ISD: " + perms.getUserHasFuncPermISD());
    permWriter.println("  PROD: " + perms.getUserHasFuncPermPROD());
    permWriter.println("  PROJ: " + perms.getUserHasFuncPermPROJ());
    permWriter.println("  TC: " + perms.getUserHasFuncPermTC());
    permWriter.println("-");
    permWriter.println("Functional permissions:");
    permWriter.println("  AttributeGroups: " + perms.getUserHasFuncPermAttributeGroups());
    permWriter.println("  Attributes: " + perms.getUserHasFuncPermAttributes());
    permWriter.println("  AuditLog: " + perms.getUserHasFuncPermAuditLog());
    permWriter.println("  Configuration: " + perms.isUserHasFuncPermConfiguration());
    permWriter.println("  ConsistencyCheck: " + perms.getUserHasFuncPermConsistencyCheck());
    permWriter.println("  Dashboard: " + perms.getUserHasFuncPermDashboard());
    permWriter.println("  Datasources: " + perms.isUserHasFuncPermDatasources());
    permWriter.println("  ExcelImport: " + perms.isUserHasFuncPermExcelImport());
    permWriter.println("  GrantInstancePerm: " + perms.getUserHasFuncPermGrantInstancePerm());
    permWriter.println("  GraphReporting: " + perms.getUserHasFuncPermGraphReporting());
    permWriter.println("  GraphReportingCreate: " + perms.getUserHasFuncPermGraphReportingCreate());
    permWriter.println("  GraphReportingFull: " + perms.getUserHasFuncPermGraphReportingFull());
    permWriter.println("  InstancePerms: " + perms.getUserHasFuncPermInstancePerms());
    permWriter.println("  IteraQl: " + perms.isUserHasFuncPermIteraQl());
    permWriter.println("  MassUpdate: " + perms.getUserHasFuncPermMassUpdate());
    permWriter.println("  Overview: " + perms.getUserHasFuncPermOverview());
    permWriter.println("  Roles: " + perms.getUserHasFuncPermRoles());
    permWriter.println("  Seal: " + perms.isUserHasFuncPermSeal());
    permWriter.println("  Search: " + perms.getUserHasFuncPermSearch());
    permWriter.println("  ShowSubscribers: " + perms.isUserHasFuncPermShowSubscribers());
    permWriter.println("  Subscription: " + perms.isUserHasFuncPermSubscription());
    permWriter.println("  SuccessorReport: " + perms.getUserHasFuncPermSuccessorReport());
    permWriter.println("  SupportingQuery: " + perms.getUserHasFuncPermSupportingQuery());
    permWriter.println("  TabReporting: " + perms.getUserHasFuncPermTabReporting());
    permWriter.println("  TabReportingCreate: " + perms.getUserHasFuncPermTabReportingCreate());
    permWriter.println("  TabReportingFull: " + perms.getUserHasFuncPermTabReportingFull());
    permWriter.println("  Templates: " + perms.getUserHasFuncPermTemplates());
    permWriter.println("  UserGroups: " + perms.getUserHasFuncPermUserGroups());
    permWriter.println("  Users: " + perms.getUserHasFuncPermUsers());
    permWriter.println("  ViewHistory: " + perms.getUserHasFuncPermViewHistory());
    permWriter.println("  XmiDeserialization: " + perms.isUserHasFuncPermXmiDeserialization());
    permWriter.println("  XmiSerialization: " + perms.isUserHasFuncPermXmiSerialization());

    permWriter.close();
    return reportPart;
  }

  /**{@inheritDoc}**/
  @Override
  public String getReportPartIdentifier() {
    return "permission";
  }
}
