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

import de.iteratec.layoutgraph.LayoutEdge;
import de.iteratec.layoutgraph.LayoutGraph;
import de.iteratec.layoutgraph.LayoutNode;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;


public class CircleNodeLayout extends JungBasedNodeLayout {

  private CircleLayout<LayoutNode, LayoutEdge> circleLayout;

  public CircleNodeLayout(LayoutGraph graph) {
    super(graph);
  }

  public void initialize() {
    super.initialize();

    double sumWidth = getSumWidth();
    double radius = sumWidth / 2;

    circleLayout = new CircleLayout<LayoutNode, LayoutEdge>(getJungGraph());
    circleLayout.setSize(new Dimension((int) Math.round(2.2 * radius), (int) Math.round(2.2 * radius)));
    circleLayout.setRadius(radius);

    this.setLayoutInitialized(true);
  }

  public void doLayout() {
    super.doLayout();

    circleLayout.initialize();

    estimateGraphDimensions(circleLayout);
  }

  private double getSumWidth() {
    double result = 0;

    for (LayoutNode node : getGraph().getNodes()) {
      result = result + node.getWidth();
    }

    return result;
  }

}
