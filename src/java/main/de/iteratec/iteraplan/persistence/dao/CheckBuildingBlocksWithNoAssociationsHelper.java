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
package de.iteratec.iteraplan.persistence.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.google.common.collect.ImmutableList;

import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Helper class for the consistency check 'CheckBuildingBlocksWithNoAssociations' encapsulating its
 * main functionality.
 * 
 * @author est
 */
public final class CheckBuildingBlocksWithNoAssociationsHelper {

  private static final String                             CHILDREN            = "children";
  private static final String                             BD_PLURAL_NAME      = "businessDomains";
  private static final String                             ISR_PLURAL_NAME     = "informationSystemReleases";
  private static final String                             BM_PLURAL_NAME      = "businessMappings";

  private static final Map<TypeOfBuildingBlock, String[]> BBT_TO_ASSOCIATIONS = CollectionUtils.hashMap();
  static {
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, new String[] { "technicalComponentReleases", CHILDREN });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.BUSINESSFUNCTION, new String[] { "businessObjects", BD_PLURAL_NAME, CHILDREN });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.BUSINESSDOMAIN, new String[] { "businessFunctions", "businessProcesses", "businessObjects",
      "businessUnits", "products", CHILDREN });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.BUSINESSOBJECT, new String[] { "informationSystemReleaseAssociations", "businessFunctions",
      BD_PLURAL_NAME, "transports", CHILDREN, "specialisations" });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.BUSINESSPROCESS, new String[] { BM_PLURAL_NAME, BD_PLURAL_NAME, CHILDREN });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.BUSINESSUNIT, new String[] { BM_PLURAL_NAME, BD_PLURAL_NAME, CHILDREN });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, new String[] { ISR_PLURAL_NAME, CHILDREN });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, new String[] { "transports", "technicalComponentReleases" });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, new String[] { "interfacesReleaseA", "interfacesReleaseB",
      "informationSystemDomains", "technicalComponentReleases", "infrastructureElements", "businessObjectAssociations", "projects", BM_PLURAL_NAME,
      "successors", "predecessors", CHILDREN, "baseComponents", "parentComponents" });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, new String[] { ISR_PLURAL_NAME, "technicalComponentReleaseAssociations",
      CHILDREN });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, new String[] { "architecturalDomains",
      "infrastructureElementAssociations", ISR_PLURAL_NAME, "informationSystemInterfaces", "successors", "predecessors", "baseComponents",
      "parentComponents" });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.PRODUCT, new String[] { BM_PLURAL_NAME, BD_PLURAL_NAME, CHILDREN });
    BBT_TO_ASSOCIATIONS.put(TypeOfBuildingBlock.PROJECT, new String[] { ISR_PLURAL_NAME, CHILDREN });
  }

  private CheckBuildingBlocksWithNoAssociationsHelper() {
    // don't instantiate, has only static methods
  }

  /**
   * Returns the result set for the specific building block type
   * 
   * @param session
   *          The Hibernate session
   * @param type
   *          The type of building block
   * @return See method description
   */
  @SuppressWarnings("unchecked")
  public static List<BuildingBlock> getResultsForType(Session session, TypeOfBuildingBlock type) {
    Class<? extends BuildingBlock> bbClass = type.getAssociatedClass();
    Criteria c = session.createCriteria(bbClass, ConsistencyCheckDAO.BB_ALIAS);
    configureCriteriaRestrictions(c, type);
    return c.list();
  }

  /**
   * Configures the criteria restrictions
   * 
   * @param c
   *          Criteria
   * @param type
   *          The type of building block
   */
  public static void configureCriteriaRestrictions(Criteria c, TypeOfBuildingBlock type) {
    String[] associations = getAssociations(type);
    if (associations == null) {
      /**
       * exclude element from result list
       */
      c.add(Restrictions.isNull(ConsistencyCheckDAO.BB_ALIAS + ".id"));
    }
    else {
      /**
       * add restrictions for all associations and properties
       */
      String[] properties = new String[] {}; // getProperties(type);
      addRestrictionForAssociations(c, ConsistencyCheckDAO.BB_ALIAS, associations, properties);
    }
  }

  /**
   * Retrieves the set of association names for the corresponding building block
   * 
   * @param type
   *          The type of building block
   * @return its association names
   */
  public static String[] getAssociations(TypeOfBuildingBlock type) {
    if (type == null || !BBT_TO_ASSOCIATIONS.containsKey(type)) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    else {
      return BBT_TO_ASSOCIATIONS.get(type);
    }
  }

  /**
   * Get properties of a type.
   * 
   * @param type
   *          The type of building block
   * @return See method description
   */
  public static String[] getProperties(TypeOfBuildingBlock type) {
    List<TypeOfBuildingBlock> typesWithParentProperties = ImmutableList.of(TypeOfBuildingBlock.ARCHITECTURALDOMAIN,
        TypeOfBuildingBlock.BUSINESSFUNCTION, TypeOfBuildingBlock.BUSINESSDOMAIN, TypeOfBuildingBlock.BUSINESSPROCESS,
        TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE,
        TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, TypeOfBuildingBlock.PRODUCT, TypeOfBuildingBlock.PROJECT);

    if (TypeOfBuildingBlock.BUSINESSOBJECT.equals(type)) {
      return new String[] { "generalisation", "parent" };
    }
    else if (typesWithParentProperties.contains(type)) {
      return new String[] { "parent" };
    }
    else if (TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE.equals(type)) {
      return new String[] { "informationSystemReleaseA", "informationSystemReleaseB" };
    }
    else if (TypeOfBuildingBlock.BUSINESSMAPPING.equals(type)) {
      return new String[] { "informationSystemRelease", "businessFunction", "businessUnit", "product" };
    }
    return new String[] {};
  }

  /**
   * Helper method to add restrictions imposing that all associations of a building block either do
   * not exist (null) or are empty.
   * 
   * @param c
   *          the Criteria object
   * @param alias
   *          the alias of the building block
   * @param associations
   *          the set of associations
   * @param properties
   *          the set of properties
   */
  public static void addRestrictionForAssociations(Criteria c, String alias, String[] associations, String[] properties) {
    for (String association : Arrays.asList(associations)) {
      //path to retrieve associated element
      String associationPath = alias + "." + association;
      //add restriction of this specific association
      c.add(Restrictions.or(Restrictions.isNull(associationPath), Restrictions.isEmpty(associationPath)));
      //mind that several c.add() produced from this iteration are implicitly combined by a logical AND
    }

    for (String property : properties) {
      //add restriction of this specific property
      c.add(Restrictions.or(Restrictions.isNull(property), Restrictions.isEmpty(property)));
    }

  }
}
