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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * This class checks permissions for a general type (Extension, Association, etc.) based on a String
 * representation of the type. The class consists of an inner abstract class
 * {@link PermissionHelper.PermissionCheckCommand} and a Map object <code>map</code>. Each String is
 * mapped to its respective code block inside the {@link #hasPermissionFor(String)} method (see
 * {@link #createPermissionCommands()}): The selection which code to run is performed via the map
 * (depending on the input string). This is a viable solution for emulating a badly-missing
 * <code>switch</code>-statement for Strings. Unfortunately, such a switch-statement does not yet
 * exist (hopefully it will in Java 7).
 */
public final class PermissionHelper {

  private static final Logger                           LOGGER                                 = Logger.getIteraplanLogger(PermissionHelper.class);

  /**
   * emulates switch-Statement for Strings
   */
  private static Map<String, PermissionCheckCommand>    permissionMap                          = new HashMap<String, PermissionCheckCommand>();

  private static final Map<TypeOfBuildingBlock, String> BB_TO_PERMISSION_MAP                   = CollectionUtils.hashMap();
  // FIXME: replace these internal strings with an enum type. this can then be used consistently in
  // all users
  public static final String                            ASSOCIATION_BUSINESSFUNCTION           = "businessFunction";
  public static final String                            ASSOCIATION_BUSINESSFUNCTIONS          = "businessFunctions";
  public static final String                            ASSOCIATION_BUSINESSDOMAIN             = "businessDomain";
  public static final String                            ASSOCIATION_BUSINESSDOMAINS            = "businessDomains";
  public static final String                            ASSOCIATION_INFORMATIONSYSTEMRELEASE   = "informationSystemRelease";
  public static final String                            ASSOCIATION_INFORMATIONSYSTEMRELEASES  = "informationSystemReleases";
  public static final String                            ASSOCIATION_BUSINESSOBJECT             = "businessObject";
  public static final String                            ASSOCIATION_BUSINESSOBJECTS            = "businessObjects";
  public static final String                            ASSOCIATION_BUSINESSPROCESS            = "businessProcess";
  public static final String                            ASSOCIATION_BUSINESSPROCESSES          = "businessProcesses";
  public static final String                            ASSOCIATION_INFORMATIONSYSTEM          = "informationSystemRelease";
  public static final String                            ASSOCIATION_INFORMATIONSYSTEMS         = "informationSystemReleases";
  public static final String                            ASSOCIATION_INFORMATIONSYSTEMINTERFACE = "informationSystemInterface";
  public static final String                            ASSOCIATION_BUSINESSMAPPING            = "businessMapping";
  public static final String                            ASSOCIATION_BUSINESSMAPPINGS           = "businessMappings";
  public static final String                            ASSOCIATION_INFORMATIONSYSTEMDOMAIN    = "informationSystemDomain";
  public static final String                            ASSOCIATION_INFORMATIONSYSTEMDOMAINS   = "informationSystemDomains";
  public static final String                            ASSOCIATION_BUSINESSUNIT               = "businessUnit";
  public static final String                            ASSOCIATION_BUSINESSUNITS              = "businessUnits";
  public static final String                            ASSOCIATION_INFRASTRUCTUREELEMENT      = "infrastructureElement";
  public static final String                            ASSOCIATION_INFRASTRUCTUREELEMENTS     = "infrastructureElements";
  public static final String                            ASSOCIATION_PROJECT                    = "project";
  public static final String                            ASSOCIATION_PROJECTS                   = "projects";
  public static final String                            ASSOCIATION_ARCHITECTURALDOMAIN        = "architecturalDomain";
  public static final String                            ASSOCIATION_ARCHITECTURALDOMAINS       = "architecturalDomains";
  public static final String                            ASSOCIATION_PRODUCT                    = "product";
  public static final String                            ASSOCIATION_PRODUCTS                   = "products";
  public static final String                            ASSOCIATION_TECHNICALCOMPONENTRELEASE  = "technicalComponentRelease";
  public static final String                            ASSOCIATION_TECHNICALCOMPONENTRELEASES = "technicalComponentReleases";

  /* initialize bBToPermissionMap */
  static {
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, PermissionHelper.ASSOCIATION_ARCHITECTURALDOMAIN);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.BUSINESSDOMAIN, PermissionHelper.ASSOCIATION_BUSINESSDOMAIN);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.BUSINESSFUNCTION, PermissionHelper.ASSOCIATION_BUSINESSFUNCTION);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.BUSINESSMAPPING, PermissionHelper.ASSOCIATION_BUSINESSMAPPING);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.BUSINESSOBJECT, PermissionHelper.ASSOCIATION_BUSINESSOBJECT);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.BUSINESSPROCESS, PermissionHelper.ASSOCIATION_BUSINESSPROCESS);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.BUSINESSUNIT, PermissionHelper.ASSOCIATION_BUSINESSUNIT);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.INFORMATIONSYSTEM, PermissionHelper.ASSOCIATION_INFORMATIONSYSTEM);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, PermissionHelper.ASSOCIATION_INFORMATIONSYSTEMDOMAIN);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, PermissionHelper.ASSOCIATION_INFORMATIONSYSTEMRELEASE);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, PermissionHelper.ASSOCIATION_INFORMATIONSYSTEMINTERFACE);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, PermissionHelper.ASSOCIATION_INFRASTRUCTUREELEMENT);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.PRODUCT, PermissionHelper.ASSOCIATION_PRODUCT);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.PROJECT, PermissionHelper.ASSOCIATION_PROJECT);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.TECHNICALCOMPONENT, PermissionHelper.ASSOCIATION_TECHNICALCOMPONENTRELEASE);
    BB_TO_PERMISSION_MAP.put(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, PermissionHelper.ASSOCIATION_TECHNICALCOMPONENTRELEASE);

    createPermissionCommands();

  }

  public static String getAssociatedPermission(TypeOfBuildingBlock tob) {
    return BB_TO_PERMISSION_MAP.get(tob);
  }

  /**
   * defines method with code block to perform (body of switch-statement)
   */
  private abstract static class PermissionCheckCommand {
    public abstract boolean hasCompositePermission();

    private boolean hasAtomicPermission(String condition) {

      if (UserContext.getCurrentPerms() != null) {
        return UserContext.getCurrentPerms().getUserHasBbTypeFunctionalPermission(condition);
      }
      // if no Permissions object exist, we assume that the user has NO permission, i.e. is not
      // authorized
      else {
        return false;
      }
    }

    // Currently not needed, but nevertheless viable
    // public boolean permissionAnd(String condition1, String condition2) {
    // return (hasAtomicPermission(condition1) && hasAtomicPermission(condition2));
    // }

    public boolean permissionOr(String condition1, String condition2) {
      return (hasAtomicPermission(condition1) || hasAtomicPermission(condition2));
    }

  }

  private PermissionHelper() {
    // only static methods
  }

  /**
   * realizes the mapping for switch-Statement
   */
  private static void createPermissionCommands() {

    createCommandsForExtensions();
    createCommandsForJoins();
    createCommandsForPlurals();
    createCommandsForAssociations();

  }

  private static void createCommandsForAssociations() {
    /* check all associations: */

    addPermissionCheckCommand(ASSOCIATION_BUSINESSFUNCTION, new String[] { Constants.BB_BUSINESSFUNCTION, Constants.BB_BUSINESSFUNCTION_PLURAL },
        null);
    addPermissionCheckCommand(ASSOCIATION_BUSINESSFUNCTIONS, new String[] { Constants.BB_BUSINESSFUNCTION, Constants.BB_BUSINESSFUNCTION_PLURAL },
        null);

    addPermissionCheckCommand(ASSOCIATION_BUSINESSDOMAIN, new String[] { Constants.BB_BUSINESSDOMAIN, Constants.BB_BUSINESSDOMAIN_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_BUSINESSDOMAINS, new String[] { Constants.BB_BUSINESSDOMAIN, Constants.BB_BUSINESSDOMAIN_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_INFORMATIONSYSTEMRELEASE, new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE,
        Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_INFORMATIONSYSTEMRELEASES, new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE,
        Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_BUSINESSOBJECT, new String[] { Constants.BB_BUSINESSOBJECT, Constants.BB_BUSINESSOBJECT_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_BUSINESSOBJECTS, new String[] { Constants.BB_BUSINESSOBJECT, Constants.BB_BUSINESSOBJECT_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_BUSINESSPROCESS, new String[] { Constants.BB_BUSINESSPROCESS, Constants.BB_BUSINESSPROCESS_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_BUSINESSPROCESSES, new String[] { Constants.BB_BUSINESSPROCESS, Constants.BB_BUSINESSPROCESS_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_INFORMATIONSYSTEM, new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE,
        Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_INFORMATIONSYSTEMS, new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE,
        Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_INFORMATIONSYSTEMINTERFACE, new String[] { Constants.BB_INFORMATIONSYSTEMINTERFACE,
        Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_BUSINESSMAPPING, new String[] { Constants.BB_BUSINESSMAPPING, Constants.BB_BUSINESSMAPPING_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_BUSINESSMAPPINGS, new String[] { Constants.BB_BUSINESSMAPPING, Constants.BB_BUSINESSMAPPING_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_INFORMATIONSYSTEMDOMAIN, new String[] { Constants.BB_INFORMATIONSYSTEMDOMAIN,
        Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_INFORMATIONSYSTEMDOMAINS, new String[] { Constants.BB_INFORMATIONSYSTEMDOMAIN,
        Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_BUSINESSUNIT, new String[] { Constants.BB_BUSINESSUNIT, Constants.BB_BUSINESSUNIT_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_BUSINESSUNITS, new String[] { Constants.BB_BUSINESSUNIT, Constants.BB_BUSINESSUNIT_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_INFRASTRUCTUREELEMENT, new String[] { Constants.BB_INFRASTRUCTUREELEMENT,
        Constants.BB_INFRASTRUCTUREELEMENT_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_INFRASTRUCTUREELEMENTS, new String[] { Constants.BB_INFRASTRUCTUREELEMENT,
        Constants.BB_INFRASTRUCTUREELEMENT_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_PROJECT, new String[] { Constants.BB_PROJECT, Constants.BB_PROJECT_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_PROJECTS, new String[] { Constants.BB_PROJECT, Constants.BB_PROJECT_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_ARCHITECTURALDOMAIN, new String[] { Constants.BB_ARCHITECTURALDOMAIN,
        Constants.BB_ARCHITECTURALDOMAIN_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_ARCHITECTURALDOMAINS, new String[] { Constants.BB_ARCHITECTURALDOMAIN,
        Constants.BB_ARCHITECTURALDOMAIN_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_PRODUCT, new String[] { Constants.BB_PRODUCT, Constants.BB_PRODUCT_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_PRODUCTS, new String[] { Constants.BB_PRODUCT, Constants.BB_PRODUCT_PLURAL }, null);

    addPermissionCheckCommand(ASSOCIATION_TECHNICALCOMPONENTRELEASE, new String[] { Constants.BB_TECHNICALCOMPONENTRELEASE,
        Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL }, null);
    addPermissionCheckCommand(ASSOCIATION_TECHNICALCOMPONENTRELEASES, new String[] { Constants.BB_TECHNICALCOMPONENTRELEASE,
        Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL }, null);

  }

  private static void createCommandsForPlurals() {
    /* plural checks */

    addPermissionCheckCommand(Constants.BB_PROJECT_PLURAL, new String[] { Constants.BB_PROJECT_PLURAL, Constants.BB_PROJECT_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL,
        Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL, new String[] { Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL,
        Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, new String[] { Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL,
        Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_INFRASTRUCTUREELEMENT_PLURAL, new String[] { Constants.BB_INFRASTRUCTUREELEMENT_PLURAL,
        Constants.BB_INFRASTRUCTUREELEMENT_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_ARCHITECTURALDOMAIN_PLURAL, new String[] { Constants.BB_ARCHITECTURALDOMAIN_PLURAL,
        Constants.BB_ARCHITECTURALDOMAIN_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL, new String[] { Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL,
        Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_BUSINESSOBJECT_PLURAL, new String[] { Constants.BB_BUSINESSOBJECT_PLURAL,
        Constants.BB_BUSINESSOBJECT_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_BUSINESSMAPPING_PLURAL, new String[] { Constants.BB_BUSINESSMAPPING_PLURAL,
        Constants.BB_BUSINESSMAPPING_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_BUSINESSFUNCTION_PLURAL, new String[] { Constants.BB_BUSINESSFUNCTION_PLURAL,
        Constants.BB_BUSINESSFUNCTION_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_BUSINESSUNIT_PLURAL, new String[] { Constants.BB_BUSINESSUNIT_PLURAL, Constants.BB_BUSINESSUNIT_PLURAL },
        null);

    addPermissionCheckCommand(Constants.BB_BUSINESSPROCESS_PLURAL, new String[] { Constants.BB_BUSINESSPROCESS_PLURAL,
        Constants.BB_BUSINESSPROCESS_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_BUSINESSDOMAIN_PLURAL, new String[] { Constants.BB_BUSINESSDOMAIN_PLURAL,
        Constants.BB_BUSINESSDOMAIN_PLURAL }, null);

    addPermissionCheckCommand(Constants.BB_PRODUCT_PLURAL, new String[] { Constants.BB_PRODUCT_PLURAL, Constants.BB_PRODUCT_PLURAL }, null);

  }

  private static void createCommandsForJoins() {

    /* join */
    addPermissionCheckCommand(Constants.EXTENSION_ISR_AD_VIA_TCR, new String[] { Constants.BB_TECHNICALCOMPONENTRELEASE,
        Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL }, new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE,
        Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL });

    /* join */
    addPermissionCheckCommand(Constants.EXTENSION_ISR_ISR_VIA_ISI, new String[] { Constants.BB_INFORMATIONSYSTEMINTERFACE,
        Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL }, new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE,
        Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL });

    /* join */
    addPermissionCheckCommand(Constants.EXTENSION_ISR_BO_VIA_ISI, new String[] { Constants.BB_BUSINESSOBJECT, Constants.BB_BUSINESSOBJECT_PLURAL },
        new String[] { Constants.BB_INFORMATIONSYSTEMINTERFACE, Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL });

    /* join */
    addPermissionCheckCommand(Constants.EXTENSION_ISI_BO, new String[] { Constants.BB_BUSINESSOBJECT, Constants.BB_BUSINESSOBJECT_PLURAL },
        new String[] { Constants.BB_INFORMATIONSYSTEMINTERFACE, Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL });

    /* join */
    addPermissionCheckCommand(Constants.EXTENSION_ISI_ISR, new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE,
        Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL }, new String[] { Constants.BB_INFORMATIONSYSTEMINTERFACE,
        Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL });
  }

  private static void createCommandsForExtensions() {
    addPermissionCheckCommand(Constants.EXTENSION_TCR, new String[] { Constants.BB_TECHNICALCOMPONENTRELEASE,
        Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_TCR_ISI, new String[] { Constants.BB_TECHNICALCOMPONENTRELEASE,
        Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_ISR_ISI, new String[] { Constants.BB_INFORMATIONSYSTEMINTERFACE,
        Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL }, new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE,
        Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL });

    addPermissionCheckCommand(Constants.EXTENSION_PROJ, new String[] { Constants.BB_PROJECT, Constants.BB_PROJECT_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_ISD, new String[] { Constants.BB_INFORMATIONSYSTEMDOMAIN,
        Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_ISR, new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE,
        Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_IE, new String[] { Constants.BB_INFRASTRUCTUREELEMENT, Constants.BB_INFRASTRUCTUREELEMENT_PLURAL },
        null);

    addPermissionCheckCommand(Constants.EXTENSION_AD, new String[] { Constants.BB_ARCHITECTURALDOMAIN, Constants.BB_ARCHITECTURALDOMAIN_PLURAL },
        null);

    addPermissionCheckCommand(Constants.EXTENSION_BD, new String[] { Constants.BB_BUSINESSDOMAIN, Constants.BB_BUSINESSDOMAIN_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_BO, new String[] { Constants.BB_BUSINESSOBJECT, Constants.BB_BUSINESSOBJECT_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_BP, new String[] { Constants.BB_BUSINESSPROCESS, Constants.BB_BUSINESSPROCESS_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_BU, new String[] { Constants.BB_BUSINESSUNIT, Constants.BB_BUSINESSUNIT_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_PRODUCT, new String[] { Constants.BB_PRODUCT, Constants.BB_PRODUCT_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_BUSINESSFUNCTION, new String[] { Constants.BB_BUSINESSFUNCTION,
        Constants.BB_BUSINESSFUNCTION_PLURAL }, null);

    addPermissionCheckCommand(Constants.EXTENSION_ISR2BOASSOCIATION, new String[] { Constants.ASSOC_INFORMATIONSYSTEMRELEASE_TO_BUSINESSOBJECT,
        Constants.ASSOC_INFORMATIONSYSTEMRELEASE_TO_BUSINESSOBJECT }, null);

    addPermissionCheckCommand(Constants.EXTENSION_BM, new String[] { Constants.BB_BUSINESSMAPPING, Constants.BB_BUSINESSMAPPING_PLURAL }, null);
  }

  /**
   * Adds PermissionCheckCommand with composite condition <code>(conditions[0] || conditions[1])
   * && (joinConditions[0] || joinConditions[1])</code> to the permissionMap
   * 
   * @param elementToCheck
   * @param conditions
   *          two element permission checks connected via or
   * @param joinConditions
   *          two elements permission checks connected via or; connected via and to conditions
   */
  private static void addPermissionCheckCommand(final String elementToCheck, final String[] conditions, final String[] joinConditions) {

    if (conditions != null && conditions.length > 1) {

      permissionMap.put(elementToCheck.toLowerCase(), new PermissionCheckCommand() {

        @Override
        public boolean hasCompositePermission() {

          boolean hasFirstPermissions = permissionOr(conditions[0], conditions[1]);

          if (joinConditions == null || joinConditions.length <= 1) {
            return hasFirstPermissions;
          }
          else {
            return (hasFirstPermissions && permissionOr(joinConditions[0], joinConditions[1]));
          }

        } // hasCompositePermission()

      }); // map.put

    } // if

  }

  /**
   * check permission for a general type (Extension, Association, etc.)
   * 
   * @param nameKey
   *          Key for the permission. Is lower cased for check
   * @return true - permission granted, false permission denied
   */
  public static boolean hasPermissionFor(String nameKey) {
    if (nameKey == null) {
      LOGGER.warn("Called hasPermissionFor with null, returning false!");
      return false;
    }

    String searchKey = normalizeKey(nameKey);

    // perform permission check according to mapping with the normalized key
    PermissionCheckCommand command = permissionMap.get(searchKey);
    if (command != null) {
      return command.hasCompositePermission();
    }
    else {
      LOGGER.warn(searchKey + " can't be found in the permissionMap, returning false");
      return false;
    }
  }
  
  public static boolean permissionCommandExists(String nameKey) {
    if (nameKey == null) {
      LOGGER.warn("Called permissionCommandExists with null, returning false!");
      return false;
    }

    String searchKey = normalizeKey(nameKey);

    // perform permission check according to mapping with the normalized key
    PermissionCheckCommand command = permissionMap.get(searchKey);
    return (command != null);
  }

  /**
   * Some permissions are not actually contained in the map: child, children, parent, predecessors,
   * successors, generalisation, specialisations, parentcomponents and basecomponents are available,
   * if the permission for the BB itself is available
   * 
   * @param nameKey
   * @return the normalized key, additionally converted to lowercase.
   */
  private static String normalizeKey(String nameKey) {
    Set<String> suffixes = new ImmutableSet.Builder<String>().add(".child").add(".children").add(".parent").add(".predecessors").add(".successors")
        .add(".generalisation").add(".specialisations").add(".parentcomponents").add(".basecomponents").build();

    String normKey = nameKey.toLowerCase();
    if (Iterables.any(suffixes, new StringEndsWithPredicate(normKey))) {
      normKey = normKey.substring(0, normKey.lastIndexOf('.'));
    }
    return normKey;
  }

  /**
   * This predicate class determines a true or false value for each input-String,
   * depending on whether or not the reference-String ends with it.
   */
  private static class StringEndsWithPredicate implements Predicate<String> {
    private String reference;

    public StringEndsWithPredicate(String referenceString) {
      super();
      this.reference = referenceString;
    }

    public boolean apply(String input) {
      return reference.endsWith(input);
    }
  }
}
