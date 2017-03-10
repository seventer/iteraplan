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
import java.awt.geom.Point2D;
import java.util.Random;

import de.iteratec.layoutgraph.LayoutEdge;
import de.iteratec.layoutgraph.LayoutGraph;
import de.iteratec.layoutgraph.LayoutNode;
import de.iteratec.layoutgraph.layout.distance.DistanceOperation;
import de.iteratec.layoutgraph.layout.distance.DistanceOperation.DistResult;
import de.iteratec.layoutgraph.layout.distance.EuclideanDistance;
import de.iteratec.layoutgraph.layout.distance.RectangleDistance;


public class StandardSpringForceLayout extends AbstractNodeLayout {

  /**
   * Parameters for configuring the layout algorithm. This is used to configure the layout instead
   * of a long list of separate parameters. It also provides default values for all parameters which
   * allows providing only those parameters explicitly that have to be changed. The members provide
   * their own descriptions on what they do.
   */
  public static final class Config {
    /**
     * The operation used to determine the distance between two nodes. Normally a standard Euclidian
     * distance is used, i.e. the length of the direct line between the two centers of the nodes. By
     * replacing this operation different topologies can be used or the size of the nodes can be
     * included into the distance calculation.
     */
    private DistanceOperation distanceOperation    = new EuclideanDistance();

    /**
     * The original strength of repulsion. This value determines how strong the nodes repel each
     * other in the first iteration. In each further iteration this will be decreased by using the
     * repulsioSlowdown value until the repulsionStopFactor is reached. This way the layout
     * algorithm starts off with strong movements and settles down over time.
     * 
     * @see #repulsionSlowdown
     * @see #repulsionStopFactor
     */
    private double            repulsionStartFactor = 100;

    /**
     * The slowdown for each iteration. See the description of repulsionStartFactor for details. It
     * has to be more than 1, although it typically is only slightly higher.
     * 
     * @see #repulsionStartFactor
     * @see #repulsionStopFactor
     */
    private double            repulsionSlowdown    = 1.01;

    /**
     * The repulsion value at which the algorithm stops. See the description of repulsionStartFactor
     * for details.
     * 
     * @see #repulsionStartFactor
     * @see #repulsionSlowdown
     */
    private double            repulsionStopFactor  = 40;

    /**
     * The length of the springs. This determines the target distance between two nodes that are
     * connected by an edge. If the distance is any shorter, additional repulsing forces will be
     * generated, if it is any longer, contracting forces will be generated. Note that this length
     * is usually not reached exactly since the repulsing forces between two nodes exist even if
     * they are connected.
     * 
     * @see #springElasticity
     */
    private double            springLength         = 200;

    /**
     * Determines the elasticity of the springs. This is the constant used in the application of
     * Hooke's Law: the higher this value, the harder the virtual spring. It has to be less than 1.
     * 
     * @see #springLength
     */
    private double            springElasticity     = 0.05;

    /**
     * The attraction to the center in horizontal direction. This determines how much the nodes are
     * drawn to the center parallel to the X axis. The distinction between the two axes is made to
     * allow creating graphs that are meant for display within non-square boundaries: changing the
     * ratio between this value and the centerAttractionY causes the layout to be stretched in one
     * direction or the other.
     * 
     * @see #centerAttractionY
     */
    private double            centerAttractionX    = 0.3;

    /**
     * The attraction to the center in vertical direction. This determines how much the nodes are
     * drawn to the center parallel to the Y axis.
     * 
     * @see #centerAttractionX
     */
    private double            centerAttractionY    = 0.3;

    /** empty private constructor for utility class */
    private Config() {
      // hide constructor
    }
  }

  private Config              config;

  private static final double NORMING_CONSTANT = 30;
  private static final double BORDER_FACTOR    = 4;

  public StandardSpringForceLayout(LayoutGraph graph, Config config) {
    super(graph);
    this.config = config;
  }

  public StandardSpringForceLayout(LayoutGraph graph) {
    super(graph);
  }

  public void initialize() {

    // Create an initial distribution of the nodes.
    double spreadFactor = 120;
    double radius = spreadFactor * Math.sqrt(getGraph().getNodes().size()) * 2.2;
    double step = 360.0 / (getGraph().getNodes().size() + 1);
    double angle = 0;
    for (LayoutNode node : getGraph().getNodes()) {
      node.setX(Math.sin(angle) * radius);
      node.setY(Math.cos(angle) * radius);
      angle = angle + step;
    }

    // If no configuration has been provided use the standard one
    if (config == null) {
      double factor = estimateFactor();
      config = new StandardSpringForceLayout.Config();
      config.repulsionStartFactor = 100 * factor;
      config.repulsionStopFactor = 40 * factor;
      config.repulsionSlowdown = 1.01;
      config.springLength = 200 * factor;
      config.centerAttractionX = 0.3;
      // The square root of 2 is the aspect ratio of a An sheet, applying this to
      // the centric vertical force should give a landscape diagram
      config.centerAttractionY = 0.3 * Math.sqrt(2);
      // a rectangular distance function is to be used
      config.distanceOperation = new RectangleDistance();
      config.springElasticity = 0;
    }
  }

  public void doLayout() {

    double repulsionFactor = config.repulsionStartFactor;

    while (repulsionFactor > config.repulsionStopFactor) {

      for (LayoutNode node : getGraph().getNodes()) {
        if (node.getParent() != null) {
          continue;
        }
        double nodeX = node.getX();
        double nodeY = node.getY();
        Point2D.Double force = new Point2D.Double(0, 0);

        // node-node repulsion
        force = calculateRepulsion(repulsionFactor, node, force);

        // edges are springs
        force = calculateEdgeForce(node, force);

        // everything is dragged towards the center
        double newForceX = -config.centerAttractionX * nodeX;
        double newForceY = -config.centerAttractionY * nodeY;
        force.setLocation(force.x + newForceX, force.y + newForceY);

        // We add a small random force for each node, to prevent nodes
        // from keeping a certain pseudo-balanced position.
        nodeX += force.x + addRandomForce();
        nodeY += force.y + addRandomForce();

        // We apply the positions to the node
        if (node.getParent() == null) {
          node.setX(nodeX);
          node.setY(nodeY);
        }
      }
      repulsionFactor /= config.repulsionSlowdown;
    }

    estimateGraphDimensions();

  }

  private Point2D.Double calculateEdgeForce(LayoutNode node, Point2D.Double startForce) {
    Point2D.Double force = new Point2D.Double(startForce.getX(), startForce.getY());
    for (LayoutEdge edge : getGraph().getEdges()) {
      LayoutNode otherNode = null;
      if (edge.getStartNode().getRepresentedId().equals(node.getRepresentedId())) {
        otherNode = edge.getEndNode();
      }
      if (edge.getEndNode().getRepresentedId().equals(node.getRepresentedId())) {
        otherNode = edge.getStartNode();
      }
      if (otherNode == null || otherNode.getParent() != null) {
        continue;
      }
      DistResult distResult = config.distanceOperation.distance(node, otherNode);
      if (distResult.getDist() == 0) {
        distResult.setDist(0.05);
      }
      // vaguely similar to Hooke's law:
      double rep = config.springElasticity * (distResult.getDist() - config.springLength) / distResult.getDist();
      double newForceX = distResult.getDx() * rep;
      double newForceY = distResult.getDy() * rep;
      force.setLocation(force.x + newForceX, force.y + newForceY);
    }
    return force;
  }

  private Point2D.Double calculateRepulsion(double repulsionFactor, LayoutNode node, Point2D.Double startForce) {
    Point2D.Double force = new Point2D.Double(startForce.getX(), startForce.getY());
    for (LayoutNode otherNode : getGraph().getNodes()) {
      if (otherNode.getParent() != null) {
        continue;
      }
      DistResult distResult = config.distanceOperation.distance(node, otherNode);
      if (distResult.getDist() == 0) {
        distResult.setDist(0.05);
      }

      // something like Coulomb's law:
      double rep = -(repulsionFactor / distResult.getDist()) * (repulsionFactor / distResult.getDist());
      double newForceX = rep * distResult.getDx();
      double newForceY = rep * distResult.getDy();
      force.setLocation(force.x + newForceX, force.y + newForceY);
    }
    return force;
  }

  private void estimateGraphDimensions() {

    double minX = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double minY = Double.MAX_VALUE;
    double maxY = Double.MIN_VALUE;

    // Estimate the dimensions of the graph.
    for (LayoutNode node : getGraph().getNodes()) {
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
        new Dimension((int) Math.round(maxX - minX + 4 * NORMING_CONSTANT), (int) Math.round(maxY - minY + 2 * BORDER_FACTOR * NORMING_CONSTANT)));

    for (LayoutNode node : getGraph().getNodes()) {
      node.setX(node.getX() - minX + BORDER_FACTOR * NORMING_CONSTANT);
      node.setY(node.getY() - minY + BORDER_FACTOR * NORMING_CONSTANT);
    }

  }

  private double addRandomForce() {
    Random sGen = new Random();
    int sign = sGen.nextInt() % 2;
    double value = sGen.nextDouble();
    value = 7.0 / (23 + (value % 23));
    if (sign == 0) {
      value = -value;
    }
    return value;
  }

  private double estimateFactor() {
    double minWidth = Double.MAX_VALUE;

    for (LayoutNode node : getGraph().getNodes()) {
      if (node.getParent() == null && node.getWidth() < minWidth) {
        minWidth = node.getWidth();
      }
    }

    return minWidth / NORMING_CONSTANT;
  }

}
