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

import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.reports.query.node.AbstractLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * Interface for textual queries.
 */
public interface QueryDAO {

  /**
   * Evaluates a query expression tree in a recursive manner.
   * <ul>
   * <li>The query tree can consist of OperationNodes, ExtensionNodes and LeafNodes. All node types
   * can be used as the root node.</li>
   * <li>ExtensionNodes are not allowed more than once for every path from the root to the leaves.</li>
   * <li>An ExtensionNode result type must match the result type of the whole query.</li>
   * <li>ExtensionNodes must have a child node, which can be an OperationNode or a LeafNode.</li>
   * <li>A LeafNode is always a leaf of the query tree (not an inner node).</li>
   * <li>If a LeafNode does not have an ExtensionNode as direct or indirect father, its result type
   * has to match the result type of the whole query.</li>
   * <li>If a LeafNode has an ExtensionNode as direct or indirect father, its result type has to
   * match the leaf type of the extension stored in the ExtensionNode.</li>
   * <li>OperationNodes can be used within the whole query tree, but only as inner nodes (not as
   * leaf nodes).</li>
   * </ul>
   * 
   * @param rootNode
   *          The root node form where the evaluation shall be started from.
   * @return The set of building blocks that match the query.
   */
  Set<BuildingBlock> evaluateQueryTree(Node rootNode);

  List<String> getAttributeValuesForFixedAttribute(Type<?> type, String attrName);

  Set<BuildingBlock> getResultSetForAbstractLeafNodeUsingCriteria(AbstractLeafNode node);

  List<String> getSetAttribute(Type<?> type, String attrName);
}
