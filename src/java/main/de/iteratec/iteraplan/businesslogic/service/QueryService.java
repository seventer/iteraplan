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

import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.queries.ReportType;

/**
 * A service for querying the {@link BuildingBlock} instances and retrieving the attributes,
 * required for filtering.
 */
public interface QueryService {

  /**
   * Creates a list of {@link BBAttribute} for a building block type. The BBAttribute are used to
   * represent either a fixed attribute (like name and description) or a user defined attribute.
   * 
   * @param type the type of the building block to get the attributes for
   * @return a List of BBAttribute objects which is never null
   */
  List<BBAttribute> getFixedAndUserdefAttributesForBBType(final Type<?> type);

  List<BBAttribute> getUserdefAttributesForBBType(final Type<?> bbType, final AttributeTypeGroupPermissionEnum permission);

  /**
   * Returns a List of all 'user defined attributes' for the given type of the building block.
   * 
   * @param type the building block of which we want to get its user-defined attributes
   * @return a List of BBAttribute objects, never null
   */
  List<BBAttribute> getUserdefAttributesForBBType(Type<?> type);

  /**
   * Returns a List of Strings that represents the possible values for the given BBAttribute and
   * Type (only needed for fixed BBAttributes).
   * 
   * @param type
   * @param attribute
   * @return List of String.
   */
  List<String> getAttributeValuesForAttribute(final Type<?> type, final BBAttribute attribute);

  /**
   * Returns all building block instances that match the conditions defined in the given query tree.
   * 
   * @param rootNode The root of the query tree to evaluate.
   * @param tsQuery A {@link TimeseriesQuery} to execute after evaluation of the rootNode query
   * @param postprocessingStrategies List of AbstractPostprocessingStrategy.
   * @return List of result building block elements.
   */
  <T extends BuildingBlock> List<T> evaluateQueryTree(final Node rootNode, TimeseriesQuery tsQuery,
      final List<AbstractPostprocessingStrategy<T>> postprocessingStrategies);

  /**
   * Finds the top and bottom level of a list of hierarchical building blocks.
   * 
   * @param elements The hierarchical buildingblocks for which the top and bottom levels are to be found.
   * @return an int[] containing the top and the bottom level in that order. If the given
   *         axisElements list is empty of the elements are not instances of HierarchicalEntity, the
   *         result will contain [1, 1].
   */
  int[] determineLevels(List<? extends BuildingBlock> elements);

  List<BBAttribute> getBBAttributesForGraphicalExport(TypeOfBuildingBlock type);

  /**
   * Creates a List of BBAttributes from the given List of AttributeTypes.
   * 
   * @param attributeTypes a List of AttributeType objects
   * @return a new List of BBAttribute objects, which may be empty, but never null
   */
  List<BBAttribute> convertToBBAttributes(List<AttributeType> attributeTypes);

  /**
   * Convenience method to cast the generics wildcard away from a type. For example, turns
   * {@code List<AbstractPostprocessingStrategy<? extends BuildingBlock>>} into
   * {@code List<AbstractPostprocessingStrategy<BuildingBlock>>}. That makes it easier to handle.
   * 
   * The list contents are not processed in any way.
   * @return An identical list with an easier to handle type.
   */
  <T extends BuildingBlock> List<AbstractPostprocessingStrategy<T>> disposeOfWildcard(List<AbstractPostprocessingStrategy<? extends BuildingBlock>> list,
      Class<T> returnType);

  <E extends ManageReportMemoryBean> void requestEntityList(E memBean, ReportType expectedReportType);
}