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
package de.iteratec.iteraplan.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;


/**
 * List all currently implemented dialog pages. For every dialog page it stores an internal dialog
 * name (identifier) and, if applicable, the corresponding flow ID. The Web flow definition with
 * that ID belongs to that dialog.
 */
public enum Dialog {

  ATTRIBUTE_TYPE("AttributeType", "attributetype/start"), ATTRIBUTE_TYPE_GROUP("AttributeTypeGroup", "attributetypegroup/start"), ARCHITECHTURAL_DOMAIN(
      "ArchitecturalDomain", "architecturaldomain/start"), BUSINESS_DOMAIN("BusinessDomain", "businessdomain/start"), BUSINESS_FUNCTION(
      "BusinessFunction", "businessfunction/start"), BUSINESS_OBJECT("BusinessObject", "businessobject/start"), BUSINESS_PROCESS("BusinessProcess",
      "businessprocess/start"), BUSINESS_UNIT("BusinessUnit", "businessunit/start"), BUSINESS_MAPPING("BusinessMapping", "businessmapping/start"), DASHBOARD(
      "Dashboard", "dashboard"), EXCELIMPORT("ExcelImport", "excelimport"), CONFIGURATION("Configuration", "configuration"), SUBSCRIPTION(
      "Subscription", "subscription"), CONSISTENCY_CHECK("ConsistencyCheck", "consistencycheck"), GRAPHICAL_REPORTING("GraphicalReporting",
      "graphicalreporting"), GRAPHICAL_REPORTING_CLUSTER("GraphicalReporting", "graphicalreporting/clusterdiagram/start"), GRAPHICAL_REPORTING_INFORMATIONFLOW(
      "GraphicalReporting", "graphicalreporting/informationflowdiagram/start"), GRAPHICAL_REPORTING_LANDSCAPE("GraphicalReporting",
      "graphicalreporting/landscapediagram/start"), GRAPHICAL_REPORTING_MASTERPLAN("GraphicalReporting", "graphicalreporting/masterplandiagram/start"), GRAPHICAL_REPORTING_PORTFOLIO(
      "GraphicalReporting", "graphicalreporting/portfoliodiagram/start"), GRAPHICAL_REPORTING_COMPOSITE("GraphicalReporting",
      "graphicalreporting/compositediagram/start"), GRAPHICAL_REPORTING_PIEBAR("GraphicalReporting", "graphicalreporting/piebardiagram/start"), GRAPHICAL_REPORTING_VBBCLUSTER(
      "GraphicalReporting", "graphicalreporting/vbbclusterdiagram/start"), GRAPHICAL_REPORTING_TIMELINE("GraphicalReporting",
      "graphicalreporting/timelinediagram/start"), GRAPHICAL_REPORTING_LINE("GraphicalReporting", "graphicalreporting/linediagram/start"), GRAPHICAL_REPORTING_MATRIX(
      "GraphicalReporting", "graphicalreporting/matrixdiagram/start"), HELP("Help", "help/show"), IMPORT("Import", "import/start"), ITERAQL(
      "IteraQl", "iteraql"), INFORMATION_SYSTEM("InformationSystem", "informationsystem/start"), INFORMATION_SYSTEM_DOMAIN("InformationSystemDomain",
      "informationsystemdomain/start"), SUCCESSOR_REPORTS("SuccessorReport", "successorReport"), INFRASTRUCTURE_ELEMENT("InfrastructureElement",
      "infrastructureelement/start"), INTERFACE("Interface", "interface/start"), MISCELLANEOUS("Miscellaneous", "miscellaneous"), MASS_UPDATE(
      "MassUpdate", "massupdate/start"), OBJECT_RELATED_PERMISSION("ObjectRelatedPermission", "objectrelatedpermission/start"), OVERVIEW("Overview",
      "overview"), PRODUCT("Product", "product/start"), PROJECT("Project", "project/start"), RESTART("Restart", "restart/start"), ROLE("Role",
      "role/start"), SAVED_QUERIES("SavedQueries", "savedqueries"), SEARCH("Search", "search"), SUPPORTING_QUERY("SupportingQuery", "supportingquery"), TABULAR_REPORTING(
      "TabularReporting", "tabularreporting/start"), TECHNICAL_COMPONENT("TechnicalComponent", "technicalcomponent/start"), TEMPLATES("Templates",
      "templates"), USER("User", "user/start"), USER_GROUP("UserGroup", "usergroup/start"), XMIDESERIALIZATION("XmiDeserialization",
      "xmideserialization"), XMISERIALIZATION("XmiSerialization", "xmiserialization"), CUSTOM_DASHBOARD_INSTANCES_OVERVIEW(
      "CustomDashboardInstancesOverview", "customdashboardinstancesoverview"), CUSTOM_DASHBOARD_INSTANCE("CustomDashboardInstance",
      "customdashboardinstance"), DATE_INTERVAL("DateInterval", "dateinterval/start");

  private static final Map<Class<?>, Dialog> CLASS_TO_DIALOG_MAP = CollectionUtils.hashMap();

  static {
    CLASS_TO_DIALOG_MAP.put(AttributeType.class, ATTRIBUTE_TYPE);
    CLASS_TO_DIALOG_MAP.put(AttributeTypeGroup.class, ATTRIBUTE_TYPE_GROUP);
    CLASS_TO_DIALOG_MAP.put(ArchitecturalDomain.class, ARCHITECHTURAL_DOMAIN);
    CLASS_TO_DIALOG_MAP.put(BusinessDomain.class, BUSINESS_DOMAIN);
    CLASS_TO_DIALOG_MAP.put(BusinessFunction.class, BUSINESS_FUNCTION);
    CLASS_TO_DIALOG_MAP.put(BusinessObject.class, BUSINESS_OBJECT);
    CLASS_TO_DIALOG_MAP.put(BusinessProcess.class, BUSINESS_PROCESS);
    CLASS_TO_DIALOG_MAP.put(BusinessMapping.class, BUSINESS_MAPPING);
    CLASS_TO_DIALOG_MAP.put(BusinessUnit.class, BUSINESS_UNIT);
    CLASS_TO_DIALOG_MAP.put(DateAT.class, ATTRIBUTE_TYPE);
    CLASS_TO_DIALOG_MAP.put(EnumAT.class, ATTRIBUTE_TYPE);
    CLASS_TO_DIALOG_MAP.put(InformationSystemRelease.class, INFORMATION_SYSTEM);
    CLASS_TO_DIALOG_MAP.put(InformationSystemInterface.class, INTERFACE);
    CLASS_TO_DIALOG_MAP.put(InformationSystemDomain.class, INFORMATION_SYSTEM_DOMAIN);
    CLASS_TO_DIALOG_MAP.put(InfrastructureElement.class, INFRASTRUCTURE_ELEMENT);
    CLASS_TO_DIALOG_MAP.put(NumberAT.class, ATTRIBUTE_TYPE);
    CLASS_TO_DIALOG_MAP.put(Product.class, PRODUCT);
    CLASS_TO_DIALOG_MAP.put(Project.class, PROJECT);
    CLASS_TO_DIALOG_MAP.put(ResponsibilityAT.class, ATTRIBUTE_TYPE);
    CLASS_TO_DIALOG_MAP.put(Role.class, ROLE);
    CLASS_TO_DIALOG_MAP.put(TechnicalComponentRelease.class, TECHNICAL_COMPONENT);
    CLASS_TO_DIALOG_MAP.put(TextAT.class, ATTRIBUTE_TYPE);
    CLASS_TO_DIALOG_MAP.put(User.class, USER);
    CLASS_TO_DIALOG_MAP.put(UserGroup.class, USER_GROUP);
    CLASS_TO_DIALOG_MAP.put(UserEntity.class, OBJECT_RELATED_PERMISSION);
    CLASS_TO_DIALOG_MAP.put(DateInterval.class, DATE_INTERVAL);
  }

  private final String                       dialogName;
  private final String                       flowId;

  private Dialog(String dialogName, String flowId) {
    this.dialogName = dialogName;
    this.flowId = flowId;
  }

  public String getDialogName() {
    return dialogName;
  }

  public String getFlowId() {
    return flowId;
  }

  public static List<Dialog> getBbElementDialogs() {
    return new ArrayList<Dialog>(Arrays.asList(new Dialog[] { INFORMATION_SYSTEM, BUSINESS_DOMAIN, BUSINESS_PROCESS, BUSINESS_FUNCTION, PRODUCT,
        BUSINESS_UNIT, BUSINESS_MAPPING, BUSINESS_OBJECT, INFORMATION_SYSTEM_DOMAIN, INTERFACE, ARCHITECHTURAL_DOMAIN, TECHNICAL_COMPONENT,
        INFRASTRUCTURE_ELEMENT, PROJECT }));
  }

  // Flows that only should only have one instance open, regardless of the id
  public static List<Dialog> getSingleFlows() {
    return new ArrayList<Dialog>(Arrays.asList(new Dialog[] { TABULAR_REPORTING, GRAPHICAL_REPORTING_CLUSTER, GRAPHICAL_REPORTING_INFORMATIONFLOW,
        GRAPHICAL_REPORTING_LANDSCAPE, GRAPHICAL_REPORTING_MASTERPLAN, GRAPHICAL_REPORTING_PORTFOLIO, GRAPHICAL_REPORTING_COMPOSITE,
        GRAPHICAL_REPORTING_PIEBAR, GRAPHICAL_REPORTING_VBBCLUSTER, GRAPHICAL_REPORTING_MATRIX, MASS_UPDATE, BUSINESS_MAPPING, IMPORT }));
  }

  /**
   * Returns the Dialog name corresponding to the passed class. May be null, if there is no dialog
   * for that class
   * 
   * @param clazz
   *          one of the iteraplan model classes in {@link de.iteratec.iteraplan.model}.
   * @return a dialog name String or null.
   */
  public static String dialogNameForClass(Class<?> clazz) {
    Dialog dialog = getDialogForClass(clazz);
    return dialog == null ? null : dialog.getDialogName();
  }

  /**
   * Returns the Flow ID of dialog corresponding to the passed class. May be null, if there is no
   * dialog for that class
   * 
   * @param clazz
   *          one of the iteraplan model classes in {@link de.iteratec.iteraplan.model}.
   * @return a Flow ID String or null.
   */
  public static String flowIdForClass(Class<?> clazz) {
    Dialog dialog = getDialogForClass(clazz);
    return dialog == null ? null : dialog.getFlowId();
  }

  public static String getDialogNameForFlowId(String flowId) {
    if (flowId == null) {
      return null;
    }

    for (Dialog aDialog : Dialog.values()) {
      if (aDialog.getFlowId().equals(flowId)) {
        return aDialog.getDialogName();
      }
    }

    return null;
  }

  /**
   * Returns the Dialog description for the passed class. May be null, if there is no dialog for
   * that class
   * 
   * @param clazz
   *          one of the iteraplan model classes in {@link de.iteratec.iteraplan.model}.
   * @return a {@link Dialog} instance or null.
   */
  public static Dialog getDialogForClass(Class<?> clazz) {
    return CLASS_TO_DIALOG_MAP.get(clazz);
  }
}
