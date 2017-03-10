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
package de.iteratec.layoutgraph.layout;

import java.awt.Dimension;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.layoutgraph.LayoutEdge;
import de.iteratec.layoutgraph.LayoutGraph;
import de.iteratec.layoutgraph.LayoutNode;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;


/**
 * Common class for the jung-based node layouts.
 */
public abstract class JungBasedNodeLayout extends AbstractNodeLayout {

  private Graph<LayoutNode, LayoutEdge> jungGraph;
  private boolean                       layoutInitialized;

  private static final int              NORMING_CONSTANT = 40;

  public JungBasedNodeLayout(LayoutGraph graph) {
    super(graph);
    this.layoutInitialized = false;
  }

  public void doLayout() {
    if (!isLayoutInitialized()) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public void initialize() {
    setJungGraph(new DirectedSparseGraph<LayoutNode, LayoutEdge>());
    for (LayoutNode node : getGraph().getNodes()) {
      getJungGraph().addVertex(node);
    }

    for (LayoutEdge edge : getGraph().getEdges()) {
      getJungGraph().addEdge(edge, edge.getStartNode(), edge.getEndNode());
    }

  }

  protected void estimateGraphDimensions(AbstractLayout<LayoutNode, LayoutEdge> layout) {
    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double maxY = Double.MIN_VALUE;

    for (LayoutNode node : getGraph().getNodes()) {
      node.setX(layout.getX(node));
      node.setY(layout.getY(node));

      if (node.getX() + node.getWidth() > maxX) {
        maxX = node.getX() + node.getWidth();
      }
      if (node.getX() < minX) {
        minX = node.getX();
      }
      if (node.getY() + node.getHeight() > maxY) {
        maxY = node.getY() + node.getHeight();
      }
      if (node.getY() < minY) {
        minY = node.getY();
      }
    }

    getGraph().setGraphDimension(
        new Dimension((int) Math.round((maxX - minX) * Math.sqrt(2)) + 4 * NORMING_CONSTANT, (int) Math.round(maxY - minY) + 4 * NORMING_CONSTANT));

    for (LayoutNode node : getGraph().getNodes()) {
      // Move all coordinates to a base starting at 0,0 plus an offset to leave some space for the
      // edge routing
      node.setX(node.getX() - minX + 2 * NORMING_CONSTANT);
      node.setY(node.getY() - minY + 2 * NORMING_CONSTANT);

      // Apply an aspect ratio of 1:sqrt(2) as the layout creates a circle - shaped representation
      node.setX(node.getX() * Math.sqrt(2));
    }
  }

  protected void setLayoutInitialized(boolean layoutInitialized) {
    this.layoutInitialized = layoutInitialized;
  }

  protected boolean isLayoutInitialized() {
    return layoutInitialized;
  }

  protected void setJungGraph(Graph<LayoutNode, LayoutEdge> jungGraph) {
    this.jungGraph = jungGraph;
  }

  protected Graph<LayoutNode, LayoutEdge> getJungGraph() {
    return jungGraph;
  }

}
