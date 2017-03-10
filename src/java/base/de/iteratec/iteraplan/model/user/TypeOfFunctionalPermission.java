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
package de.iteratec.iteraplan.model.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
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


/**
 * Persistent enumeration class for types of functional permissions. The string passed into the
 * constructor is the key for an internationalized value. The key for the description of these
 * permissions is not stored in the database, but it can be retrieved by concatenating the key with
 * '.description'.
 */
public enum TypeOfFunctionalPermission {

  ARCHITECTURALDOMAIN("role.permission.ArchitecturalDomain"), ATTRIBUTETYPE("role.permission.AttributeType"), ATTRIBUTETYPEGROUP(
      "role.permission.AttributeTypeGroup"), BUSINESSDOMAIN("role.permission.BusinessDomain"), BUSINESSFUNCTION("role.permission.BusinessFunction"), BUSINESSOBJECT(
      "role.permission.BusinessObject"), BUSINESSPROCESS("role.permission.BusinessProcess"), BUSINESSUNIT("role.permission.BusinessUnit"), CHANGE_DATASOURCE(
      "role.permission.ChangeDataSource"), CONSISTENCY_CHECK("role.permission.ConsistencyCheck"), CONFIGURATION("role.permission.Configuration"), DOWNLOAD_AUDIT_LOG(
      "role.permission.DownloadAuditLog"), ELEMENT_SPECIFIC_PERMISSION("role.permission.ElementSpecificPermission"), EXCELIMPORT(
      "role.permission.ExcelImport"), GRANT_EXPLICIT_PERMISSION("role.permission.GrantExplicitPermission"), GRAPHICAL_REPORTING(
      "role.permission.GraphicalReporting"), INFORMATIONSYSTEMDOMAIN("role.permission.InformationSystemDomain"), INFORMATIONSYSTEMINTERFACE(
      "role.permission.InformationSystemInterface"), INFORMATIONSYSTEMRELEASE("role.permission.InformationSystemRelease"), SUCCESSORREPORT(
      "role.permission.SuccessorReport"), INFRASTRUCTUREELEMENT("role.permission.InfrastructureElement"), MASSUPDATE("role.permission.MassUpdate"), PRODUCT(
      "role.permission.Product"), PROJECT("role.permission.Project"), ROLE("role.permission.Role"), SEARCH("role.permission.Search"), SUPPORTING_QUERY(
      "role.permission.SupportingQuery"), TABULAR_REPORTING("role.permission.TabularReporting"), TECHNICALCOMPONENTRELEASES(
      "role.permission.TechnicalComponentReleases"), USER("role.permission.User"), USERGROUP("role.permission.UserGroup"), XMIDESERIALIZATION(
      "role.permission.XmiDeserialization"), XMISERIALIZATION("role.permission.XmiSerialization"), SUBSCRIPTION("role.permission.Subscription"), SHOW_SUBSCRIBERS(
      "role.permission.ShowSubscribers"), DASHBOARD("role.permission.Dashboard"), TABREPORT_CREATE("role.permission.TabularReportingCreate"), TABREPORT_FULL(
      "role.permission.TabularReportingFull"), GRAPHREPORT_CREATE("role.permission.GraphicalReportingCreate"), GRAPHREPORT_FULL(
      "role.permission.GraphicalReportingFull"), ITERAQL("role.permission.IteraQl"), SEAL("role.permission.Seal"), BUSINESSMAPPING(
      "role.permission.BusinessMapping"), VIEW_HISTORY("role.permission.ViewHistory"), OVERVIEW("role.permission.Overview"), TEMPLATES(
      "role.permission.Templates"), REST("role.permission.REST"),
  // Dummy-Constant for the presentation tier.
  DUMMY("");

  private static final Logger                                   LOGGER = Logger.getIteraplanLogger(TypeOfFunctionalPermission.class);

  // All, except the DUMMY constant.
  public static final List<TypeOfFunctionalPermission>          ALL;

  public static final Map<TypeOfFunctionalPermission, Class<?>> PERMISSION_TO_CLASS_MAP;

  static {
    // ///////////////// IMPORTANT ///////////////////////////////////
    // When a new functional permission is added, do only add it to the end of the ALL array
    // Otherwise, database IDs will be mixed up and require a tedious migration script. You have
    // been warned!
    //
    // However, when changing this array, you ALWAYS have to create a migration script which updates
    // the
    // perm_functional table for older databases. Even changing the strings in enum values requires
    // you to create a migration script.
    // //////////////////////////
    ALL = Collections.unmodifiableList(Arrays.asList(new TypeOfFunctionalPermission[] { USERGROUP, USER, ARCHITECTURALDOMAIN, ATTRIBUTETYPE,
      ATTRIBUTETYPEGROUP, BUSINESSFUNCTION, BUSINESSOBJECT, BUSINESSPROCESS, INFRASTRUCTUREELEMENT, INFORMATIONSYSTEMINTERFACE, DOWNLOAD_AUDIT_LOG,
      BUSINESSDOMAIN, GRANT_EXPLICIT_PERMISSION, ELEMENT_SPECIFIC_PERMISSION, INFORMATIONSYSTEMRELEASE, CONFIGURATION, INFORMATIONSYSTEMDOMAIN,
      PROJECT, MASSUPDATE, BUSINESSUNIT, PRODUCT, CONSISTENCY_CHECK, SEARCH, TABULAR_REPORTING, GRAPHICAL_REPORTING, SUCCESSORREPORT, ROLE,
      SUPPORTING_QUERY, TECHNICALCOMPONENTRELEASES, CHANGE_DATASOURCE, XMISERIALIZATION, XMIDESERIALIZATION, EXCELIMPORT, SUBSCRIPTION,
      SHOW_SUBSCRIBERS, DASHBOARD, TABREPORT_CREATE, TABREPORT_FULL, GRAPHREPORT_CREATE, GRAPHREPORT_FULL, ITERAQL, SEAL, BUSINESSMAPPING,
      VIEW_HISTORY, OVERVIEW, TEMPLATES, REST }));
    // ////////////////// IMPORTANT: See note above! /////////////////

    Map<TypeOfFunctionalPermission, Class<?>> classPermissions = new HashMap<TypeOfFunctionalPermission, Class<?>>();
    classPermissions.put(ARCHITECTURALDOMAIN, ArchitecturalDomain.class);
    classPermissions.put(BUSINESSDOMAIN, BusinessDomain.class);
    classPermissions.put(BUSINESSFUNCTION, BusinessFunction.class);
    classPermissions.put(BUSINESSOBJECT, BusinessObject.class);
    classPermissions.put(BUSINESSPROCESS, BusinessProcess.class);
    classPermissions.put(BUSINESSMAPPING, BusinessMapping.class);
    classPermissions.put(BUSINESSUNIT, BusinessUnit.class);
    classPermissions.put(INFORMATIONSYSTEMDOMAIN, InformationSystemDomain.class);
    classPermissions.put(INFORMATIONSYSTEMINTERFACE, InformationSystemInterface.class);
    classPermissions.put(INFORMATIONSYSTEMRELEASE, InformationSystemRelease.class);
    classPermissions.put(INFRASTRUCTUREELEMENT, InfrastructureElement.class);
    classPermissions.put(PRODUCT, Product.class);
    classPermissions.put(PROJECT, Project.class);
    classPermissions.put(TECHNICALCOMPONENTRELEASES, TechnicalComponentRelease.class);
    classPermissions.put(USER, User.class);
    classPermissions.put(USERGROUP, UserGroup.class);
    classPermissions.put(ATTRIBUTETYPE, AttributeType.class);
    classPermissions.put(ATTRIBUTETYPEGROUP, AttributeTypeGroup.class);
    classPermissions.put(ROLE, Role.class);
    PERMISSION_TO_CLASS_MAP = Collections.unmodifiableMap(classPermissions);
  }

  private final String                                          typeOfFunctionalPermission;

  private TypeOfFunctionalPermission(String typeOfFunctionalPermission) {
    this.typeOfFunctionalPermission = typeOfFunctionalPermission;
  }

  /**
   * Delegates to <tt>toString()</tt>. The presentation tier uses JSP Expression Language to access
   * application data stored in JavaBean components. Thus a JavaBean-style getter-method has to be
   * provided to display the value of an Enum instance on the GUI.
   * 
   * @return See method description.
   */
  public String getValue() {
    return this.toString();
  }

  /**
   * Returns the current string value stored in the Enum.
   * <p>
   * Required for correct reflection behaviour (see <tt>StringEnumReflectionHelper</tt>).
   * 
   * @return See method description.
   */
  @Override
  public String toString() {
    return this.typeOfFunctionalPermission;
  }

  /**
   * Returns the elements of this Enum class, except DUMMY.
   * 
   * @return See method description.
   */
  public static List<TypeOfFunctionalPermission> getEnumConstants() {

    // The list that is returned for Arrays.asList cannot be modified,
    // thus it is wrapped in another ArrayList.
    List<TypeOfFunctionalPermission> list = new ArrayList<TypeOfFunctionalPermission>(Arrays.asList(TypeOfFunctionalPermission.class
        .getEnumConstants()));
    list.remove(TypeOfFunctionalPermission.DUMMY);
    return list;
  }

  /**
   * Returns the Enum instance for the specified string value.
   * 
   * @param value
   *          The string value for which the Enum instance shall be returned.
   * @return See method description.
   */
  public static TypeOfFunctionalPermission getTypeOfFunctionalPermissionByString(String value) {
    String name = StringEnumReflectionHelper.getNameFromValue(TypeOfFunctionalPermission.class, value);
    try {
      return Enum.valueOf(TypeOfFunctionalPermission.class, name);
    } catch (IllegalArgumentException ex) {
      LOGGER.error("This enum has no constant with the specified name " + value);
    }
    return null;
  }

  public static Set<TypeOfFunctionalPermission> getAllPermissionsMappedToClasses() {
    return PERMISSION_TO_CLASS_MAP.keySet();
  }

  public Class<?> getClassForPermission() {
    return PERMISSION_TO_CLASS_MAP.get(this);
  }
}
