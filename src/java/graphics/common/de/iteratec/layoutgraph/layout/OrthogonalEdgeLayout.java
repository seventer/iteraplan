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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.iteratec.layoutgraph.DockingPointsManager;
import de.iteratec.layoutgraph.GridPointManager;
import de.iteratec.layoutgraph.LayoutEdge;
import de.iteratec.layoutgraph.LayoutEdgeCoordinate;
import de.iteratec.layoutgraph.LayoutGraph;
import de.iteratec.layoutgraph.LayoutNode;
import de.iteratec.layoutgraph.LayoutNode.NodeSide;


/**
 * This Class represents an orthogonal edge layout.<br><br><br>
 * 
 * 
 *  The routing is realized using the Mikami-Tabuchi algorithm (see description {@link de.iteratec.layoutgraph.layout.OrthogonalEdgeLayout.OrthogonalEdgeMikamiTabuchiAlgorithm} for 
 *  details)<br><br>
 *  
 *  Further it uses:<br>
 *  -   the {@link GridPointManager} class to discretize the layout object, in particular to translate the 
 *      nodes of the layoutgraph to a list of grid-points and to identify intersections of layout objects<br>
 *  -   the {@link DockingPointsManager} class to perform a pre- and post-processing of the graph nodes' docking
 *      points.    <br>
 *      
 *      
 * 
 * @author Andreas Umbreit, iteratec GmbH
 */
public class OrthogonalEdgeLayout extends AbstractEdgeLayout {

  private List<LayoutEdge>     edges;

  private GridPointManager     gridPointManager;

  private DockingPointsManager dockingPointsManager;

  public static final int      HORIZONTAL           = 0;
  public static final int      VERTICAL             = 1;

  public static final int      DIMENSIONS           = 2;
  private static final double  SMALL_VALUE          = 0.5;

  private static final int     DIMENSION_CORRECTION = 200;
  private static final double  DISTANCE_TOLERANCE   = 8;

  /**   
   *   Algorithm to route an edge through a grid with blocked points (-> {@link GridPointManager})
   *   which is as well updated.<br><br>
   *    
   *   The algorithm follows the following idea:<br>
   *    From the edges start*) and end*)- point trial lines are drawns in all 4 directions (if possible) until
   *    they hit some blocking object (another node of the graph)<br><br>
   *    
   *    Then repeatedly from every branching point (i.e. gridpoint) along these trial lines new trial lines orthogonal to the preceeding one
   *    are drawn. The number of branchings that have been performed from the original starting point to a trial line defines its level.<br><br>
   *    
   *    This is repeated until (besides an iteration limit) a trial line that roots from the start point
   *    intersects with a trial line that roots from the end point.<br><br>
   *    
   *    The branching points that each lead from the start- and end-point to the trial lines that intersect 
   *    and the intersection point build then the edge-path's points.<br><br><br><br>
   *    
   *   
   *    
   *   *): in this implementation pre-processed edges are used, therefore the routing 
   *   takes place between the two "central" points of the edge that are already set
   *    (if the edge is preprocessed by the dockingPoint-precprocessing, 4 path points are set,
   *     therefore routing will take place between the second and the third point):
   */
  private static class OrthogonalEdgeMikamiTabuchiAlgorithm {

    public static final int  FORWARD         = 0;
    public static final int  BACKWARD        = 1;
    private static final int MAX_ITER        = 8;
    private static final int VISIT_INDICATOR = 1;

    private LayoutEdge       edge;

    /**
     *  routing takes place between the two central set points of the edge<br>
     *   routeStartIndex is the index in the edges path points of the start point
     */
    private int              routeStartIndex;

    /**
     *  routing takes place between the two central set points of the edge<br>
     *   routeEndndex is the index in the edges path points of the end point
     */
    private int              routeEndIndex;

    private GridPointManager gridPointManager;

    /**
     * Objects of this class represent a trial line according to the Mikami-Tabuchi Algorithm<br><br>
     * 
     * A trial line is identified by a source point, a dimension ( VERTICAL/ HORIZONTAL),<br>
     *    it's bounds (i.e. the max. distances)  <br>
     *    and its level in the Mikami-Tabuchi algorithm.<br><br>
     *    
     * Use the initialize method to set bounds and  create branch points.
     *     
     * 
     */
    private class TrialLine {

      /**
       *  A trial lines source point is the point from where to left and to the right (top and bottom) branch points are created.
       */
      private LayoutEdgeCoordinate       sourcePoint;

      /**
       *  The level of a trial line is the number of corners (i.e. preceding trial lines) 
       *  of the path from the source coordinate to the start point of the current lines.<br>
       *   A trial line of level 0 starts directly in a source, a trial line of level one start at a gridpoint of
       *   a trial line of level 0, etc. ...
       *   
        *  
       */
      private int                        level;

      private int                        dimension;
      private TrialLine                  predecessor;

      /**
       *  The lowerDistance is the distance (according to the trial line's dimension) of the first trial lines branch point
       *  to the source point
       */
      private double                     lowerDistance;
      /**
       *  The  upperDistance is the distance (according to the trial line's dimension) of the last trial lines branch point
       *  to the source point
       */
      private double                     upperDistance;

      /**
       *  The branchPoints of a trial lines are the points that may be used as source points of further trial lines of higher level.
       */
      private List<LayoutEdgeCoordinate> branchPoints;

      private class PointUniformDistanceComparator implements Comparator<LayoutEdgeCoordinate> {
        private LayoutEdgeCoordinate rootPoint;

        public PointUniformDistanceComparator(LayoutEdgeCoordinate rootPoint) {
          this.rootPoint = rootPoint;
        }

        /**{@inheritDoc}**/
        public int compare(LayoutEdgeCoordinate o1, LayoutEdgeCoordinate o2) {
          return (int) (rootDist(o1) - rootDist(o2));
        }

        private double rootDist(LayoutEdgeCoordinate o) {
          return LayoutEdgeCoordinate.uniformDistance(o, rootPoint);
        }

      }

      public TrialLine(LayoutEdgeCoordinate sourcePoint, int dimension, int level, TrialLine predecessor) {
        this.sourcePoint = sourcePoint.clone();
        this.dimension = dimension;
        this.level = level;
        this.predecessor = predecessor;
        this.branchPoints = new ArrayList<LayoutEdgeCoordinate>();
        this.lowerDistance = 0;
        this.upperDistance = 0;
      }

      public LayoutEdgeCoordinate getSourcePoint() {
        return sourcePoint.clone();
      }

      public LayoutEdgeCoordinate getStartPoint() {
        LayoutEdgeCoordinate startPoint = sourcePoint.clone();
        double activeDimStartValue = startPoint.getCoord(dimension) - lowerDistance;
        startPoint.setCoord(dimension, activeDimStartValue);
        return startPoint;
      }

      public LayoutEdgeCoordinate getEndPoint() {
        LayoutEdgeCoordinate endPoint = sourcePoint.clone();
        double activeDimEndValue = endPoint.getCoord(dimension) + upperDistance;
        endPoint.setCoord(dimension, activeDimEndValue);
        return endPoint;
      }

      public int getDimension() {
        return this.dimension;
      }

      public TrialLine getPredecessor() {
        return this.predecessor;
      }

      public double getLowerDistance() {
        return this.lowerDistance;
      }

      public double getUpperDistance() {
        return this.upperDistance;
      }

      /**
       * 
       * @return  the bounds of the trial line's dimension as Interval
       */
      public double[] getActiveDimRange() {
        double lowerBound = this.sourcePoint.getCoord(this.dimension) - this.lowerDistance;
        double upperBound = this.sourcePoint.getCoord(this.dimension) + this.upperDistance;
        return new double[] { lowerBound, upperBound };
      }

      public int getLevel() {
        return this.level;
      }

      /**
       * Sets the bounds of a trial line and creates branch points along the line.
       * 
       * @param distanceBounds
       *          The distances from the starting point in both directions, i.e.<br>
       *              distanceBounds[0] : positive distance of branch point on trial line left-most (top-most) from the start point<br>
       *              distanceBounds[1] : positive distance of branch point on trial line right-most (bottom-most) from the start point
       *          for a horizontal (vertical) trial line    
       *           
       * @param visitedPoints
       *          The HashMap the generated branchPoints have to be logged in to prevent duplicate
       *          trial lines.
       */
      public void initialize(double[] distanceBounds) {
        double[] bounds = distanceBounds.clone(); // due to the PMD msg ArrayIsStoredDirectly

        this.lowerDistance = bounds[0];
        this.upperDistance = bounds[1];

        // create the branchingPoints
        gridPointManager.discretizeLineSegment(getStartPoint(), getEndPoint(), branchPoints, dimension);

        // due to performance issues reorder the branchPoints according to their source distance
        PointUniformDistanceComparator sourceDistanceComparator = new PointUniformDistanceComparator(sourcePoint);
        Collections.sort(branchPoints, sourceDistanceComparator);

        for (LayoutEdgeCoordinate branchPointTemp : branchPoints) {
          gridPointManager.increaseVisits(branchPointTemp);
        }

      }

      /**
       *  set the trial lines distance of the first point to the source point
       * @param lowerDistance > 0
       */
      public void setLowerDistance(double lowerDistance) {
        this.lowerDistance = lowerDistance;
      }

      /**
       *  set the trial lines distance of the last point to the source point
       * @param upperDistance > 0
       */
      public void setUpperDistance(double upperDistance) {
        this.upperDistance = upperDistance;
      }

      public List<LayoutEdgeCoordinate> getBranchPoints() {
        return this.branchPoints;
      }

    }

    /**
     * Objects of this class are used to represent an intersection between two trial lines.
     */
    private static class TrialLineIntersection {

      private LayoutEdgeCoordinate intersectionPoint;
      private TrialLine            testLine;
      private TrialLine            intersectionLine;

      public TrialLineIntersection(LayoutEdgeCoordinate intersectionPoint, TrialLine testLine, TrialLine intersectionLine) {
        this.intersectionLine = intersectionLine;
        this.testLine = testLine;
        this.intersectionPoint = intersectionPoint.clone();
      }

      public TrialLine getIntersectingLine() {
        return this.intersectionLine;
      }

      public TrialLine getTestLine() {
        return this.testLine;
      }

      public LayoutEdgeCoordinate getIntersectionPoint() {
        return this.intersectionPoint;
      }
    }

    public OrthogonalEdgeMikamiTabuchiAlgorithm(LayoutEdge edge, GridPointManager gridPointManager) {
      this.edge = edge;
      this.gridPointManager = gridPointManager;
    }

    /**
     * Used for determining the correct edge path once an intersection is found
     * 
     * @param trialLine
     *          The one trial line
     * @param intersection
     *          The Intersection Object containing the other trial line and the intersection point
     * @param edge
     *          The edge to be modified
     * @param direction ( FORWARD or BACKWARD)
     *          indicates, if the one trial line belongs to the start- or to the endNode
     * 
     */
    private void backtrack(TrialLine trialLine, TrialLineIntersection intersection, int direction) {

      TrialLine sLine;
      TrialLine tLine;
      if (direction == FORWARD) {
        sLine = trialLine;
        tLine = intersection.getIntersectingLine();
      }
      else {
        tLine = trialLine;
        sLine = intersection.getIntersectingLine();
      }
      edge.addEdgePathPoint(intersection.getIntersectionPoint(), routeStartIndex + 1);
      // as a point has been added the roudEndPoint's index has been shifted
      routeEndIndex++;

      while (!(sLine.getPredecessor() == null)) {
        edge.addEdgePathPoint(sLine.getSourcePoint(), routeStartIndex + 1);
        routeEndIndex++;
        sLine = sLine.getPredecessor();
      }
      while (!(tLine.getPredecessor() == null)) {
        edge.addEdgePathPoint(tLine.getSourcePoint(), routeEndIndex - 1);
        routeEndIndex++;
        tLine = tLine.getPredecessor();
      }

    }

    /** 
     *  Creates the initial (i.e. level 0 ) trial lines starting from a source Point
     *  further cuts off the trial lines in accordance with the nodeSide
     * 
     * @param sourcePoint
     * @param nodeSide
     * @return list of trial lines
     */
    private List<TrialLine> createInitialTrialLines(LayoutEdgeCoordinate sourcePoint, NodeSide nodeSide) {
      List<TrialLine> intialTrialLines = new ArrayList<TrialLine>();

      // Create horizontal and vertical 0-level trial lines, determine their bounds and initiliaze
      TrialLine horizontalTrialLine = new TrialLine(sourcePoint, HORIZONTAL, 0, null);
      double[] horizontalBounds = determineTrialLineBounds(horizontalTrialLine, DISTANCE_TOLERANCE);
      horizontalTrialLine.initialize(horizontalBounds);

      TrialLine verticalTrialLine = new TrialLine(sourcePoint, VERTICAL, 0, null);
      double[] verticalBounds = determineTrialLineBounds(verticalTrialLine, DISTANCE_TOLERANCE);
      verticalTrialLine.initialize(verticalBounds);

      // Pay regard to the node side and cut off the trial lines
      // e.g. if the nodeSide is north, the trialLine cannot search bottom-directed 
      // it's not set 0 to make it distinguishable from the intial value 0
      switch (nodeSide) {
        case NORTH:
          verticalTrialLine.setUpperDistance(SMALL_VALUE);
          break;
        case EAST:
          horizontalTrialLine.setLowerDistance(SMALL_VALUE);
          break;
        case SOUTH:
          verticalTrialLine.setLowerDistance(SMALL_VALUE);
          break;
        case WEST:
          horizontalTrialLine.setUpperDistance(SMALL_VALUE);
          break;
        default:
      }

      intialTrialLines.add(horizontalTrialLine);
      intialTrialLines.add(verticalTrialLine);

      return intialTrialLines;
    }

    /**
     *    Creates a new trial line from a given branching Point on a preceeding trial line
     *    
     * @param predecessor
     * @param branchPoint
     * @return trialLine with branchPoint as source
     */
    private TrialLine createNewTrialLine(TrialLine predecessor, LayoutEdgeCoordinate branchPoint) {
      // new trial line is orthogonal on predecessor:
      int dimension = predecessor.getDimension() == HORIZONTAL ? VERTICAL : HORIZONTAL;
      // and of one greater level :
      int level = predecessor.getLevel() + 1;

      TrialLine newTrialLine = new TrialLine(branchPoint, dimension, level, predecessor);
      newTrialLine.initialize(determineTrialLineBounds(newTrialLine, DISTANCE_TOLERANCE));

      return newTrialLine;

    }

    /**
     * Determine the bounds (i.e. distances left and right (top and bottom) of the source point that do not
     *  intersect with a blocking object) of a trial line in the routing environment.
     *    
     * 
     * @param trialLine
     *          The trial line for which the borders have to be determined.
     * @param tolerance
     *          The distance to non-crossable structures a line may not undercut.
     * @return The two distances to the source of the trial line
     */
    private double[] determineTrialLineBounds(TrialLine trialLine, double tolerance) {
      int activeDimension = trialLine.getDimension();
      int inactiveDimension = (activeDimension == HORIZONTAL) ? VERTICAL : HORIZONTAL;
      LayoutEdgeCoordinate sourcePoint = trialLine.getSourcePoint();
      double[] limits = new double[] { gridPointManager.getWidthLimit(), gridPointManager.getHeightLimit() };

      // determine start try values for the upper and lower distance bounds
      // either they're are already set by the TrialLine,
      // if not for the source's distances to the LayoutGraph borders are used
      double lowerDistanceCandidate = (trialLine.getLowerDistance() != 0) ? trialLine.getLowerDistance() : +sourcePoint.getCoord(activeDimension)
          - GridPointManager.GLOBAL_STEP_SIZE;

      double upperDistanceCandidate = (trialLine.getUpperDistance() != 0) ? trialLine.getUpperDistance() : limits[activeDimension]
          - sourcePoint.getCoord(activeDimension) - GridPointManager.GLOBAL_STEP_SIZE;
      List<LayoutEdgeCoordinate> forbidden = gridPointManager.getForbiddenPoints();
      // for every forbidden grid point check whether it's too close, if so, decrease distances
      for (LayoutEdgeCoordinate currentForbiddenPoint : forbidden) {

        double activeDimDistanceToSource = currentForbiddenPoint.getCoord(activeDimension) - sourcePoint.getCoord(activeDimension);
        double distanceToTrialLine = Math.abs(currentForbiddenPoint.getCoord(inactiveDimension) - sourcePoint.getCoord(inactiveDimension));

        boolean tooClose = distanceToTrialLine < tolerance;
        if (tooClose) {
          // if activeDimDistanceToSource negative -> lower distance might be adapted, otherwise upper distance
          if ((activeDimDistanceToSource < -SMALL_VALUE) && (-activeDimDistanceToSource < lowerDistanceCandidate)) {
            lowerDistanceCandidate = -activeDimDistanceToSource;
          }
          if ((activeDimDistanceToSource > SMALL_VALUE) && (activeDimDistanceToSource < upperDistanceCandidate)) {
            upperDistanceCandidate = activeDimDistanceToSource;
          }
        }
      }
      // distances are diminished by SMALL_VALUE to get actual NON-forbidden point
      return new double[] { lowerDistanceCandidate - SMALL_VALUE, upperDistanceCandidate - SMALL_VALUE };
    }

    /**
     *  Determines an intersection between two lists of trial lines
     *    (not treated equally, see descriptions of overloaded methods for details) 
     *         
     * @param testLines
     * @param intersectionCandidates
     * @return the TrialLineIntersection if existent
     */
    private TrialLineIntersection findTrialLineIntersection(List<TrialLine> testLines, List<TrialLine> intersectionCandidates) {
      TrialLineIntersection intersection = null;

      for (TrialLine currentTestLine : testLines) {
        intersection = findTrialLineIntersection(currentTestLine, intersectionCandidates);
        if (intersection != null) {
          return intersection;
        }
      }

      return null;
    }

    /**
     * Determines an intersection between a trial line and a list of trial lines
     * 
     * 
     * @param testLine
     *          The trial line that is test for intersecting with other lines.
     * @param intersectionCandidates
     *          A List of trial lines to be tested for an intersection with the testLine.
     * @return The Intersection Object containing one crossing trial line in the intersectionCandidates and the intersection
     *         point. <code>null</code> If no intersection could be found or all intersections already in LinePoints list
     */
    private TrialLineIntersection findTrialLineIntersection(TrialLine testLine, List<TrialLine> intersectionCandidates) {
      int activeLevel = testLine.getLevel();
      int activeDimension = testLine.getDimension();
      int inactiveDimension = activeDimension == HORIZONTAL ? VERTICAL : HORIZONTAL;

      int startIndex = intersectionCandidates.size() - 1;

      // Determine last intersectionCandidate that is at least two levels below the testLine-level
      //  trial lines of lower testLevel cannot intersect with the testLine anymore as there already
      //  exist parallel trial lines of higher level that are closer to the testing Lines
      while (intersectionCandidates.get(startIndex).getLevel() >= activeLevel - 2 && startIndex > 0) {
        startIndex--;
      }

      for (int i = startIndex; i < intersectionCandidates.size(); i++) {
        TrialLine currentIntersectionCandidate = intersectionCandidates.get(i);
        if (currentIntersectionCandidate.getDimension() == inactiveDimension) {
          TrialLineIntersection intersection = findTrialLineIntersection(testLine, currentIntersectionCandidate);
          if (intersection != null) {
            return intersection;
          }
        }
      }
      return null;
    }

    /**
     * Determines an intersection between a trial line and an intersectionCandidate of different active dimension<br><br>
     *   
     *  !NOTE: if the to lines are parallel null is returned even they might intersect<br><br>
     *  
     *   Further, if the point is already in the LinePoints, it's not suitable as intersection as this caused overlapping lines
     * @param testLine
     *          The trial line that is test for intersecting with other lines.
     * @param intersectionCandidate
     *          The  trial lines to be tested for an intersection with the testLine.
     * @return The Intersection Object containing one crossing trial line in the intersectionCandidates and the intersection
     *         point. <code>null</code> If no intersection could be found or lines parallel or intersectingPoint already in pointList.
     */
    private TrialLineIntersection findTrialLineIntersection(TrialLine testLine, TrialLine intersectionCandidate) {

      if (testLine.getDimension() == intersectionCandidate.getDimension()) {
        // in this implementation calculating the intersection of parallel lines is not needed
        return null;
      }

      int activeDimension = testLine.getDimension();
      int inactiveDimension = activeDimension == HORIZONTAL ? VERTICAL : HORIZONTAL;

      boolean activeDimIntersect;
      boolean inactiveDimIntersect;

      // the two trial lines intersect if and only if
      //    - the testCandidate has an active dim value in the bounds of the testLine -> activeDimIntersect
      //    - the testLine has an inactive dim value in the bounds of the intersectionCandidate -> inactiveDimIntersect
      // exactly this is now checked: 
      double testLineInactiveDimValue = testLine.getSourcePoint().getCoord(inactiveDimension);
      double intersectionCandidateInactiveDimValue = intersectionCandidate.getSourcePoint().getCoord(activeDimension);
      double[] testLineRange = testLine.getActiveDimRange();
      double[] intersectionCandidateRange = intersectionCandidate.getActiveDimRange();
      activeDimIntersect = (testLineRange[0] <= intersectionCandidateInactiveDimValue && intersectionCandidateInactiveDimValue <= testLineRange[1]);
      inactiveDimIntersect = (intersectionCandidateRange[0] <= testLineInactiveDimValue && testLineInactiveDimValue <= intersectionCandidateRange[1]);

      if (activeDimIntersect && inactiveDimIntersect) {
        LayoutEdgeCoordinate intersectionPoint = new LayoutEdgeCoordinate();

        // the intersections point's coordinates are each line's INactive dimension values
        intersectionPoint.setCoord(activeDimension, intersectionCandidateInactiveDimValue);
        intersectionPoint.setCoord(inactiveDimension, testLineInactiveDimValue);

        return new TrialLineIntersection(intersectionPoint, testLine, intersectionCandidate);

      }
      return null;
    }

    /**
     * 
     * @param direction
     * @param activeLines
     * @param inactiveLines
     * @return true if an intersection point has been found (i.e. search successful), false if not
     */
    private boolean tryAllActiveBranches(int direction, List<TrialLine> activeLines, List<TrialLine> inactiveLines) {

      for (int activeLineIndex = getStartIndexOfMaxlevelLines(activeLines); activeLineIndex < activeLines.size(); activeLineIndex++) {

        TrialLine currentActiveTrialLine = activeLines.get(activeLineIndex);

        for (LayoutEdgeCoordinate currentBranchPoint : currentActiveTrialLine.getBranchPoints()) {

          boolean foundIntersection = tryNewTrialLine(direction, activeLines, inactiveLines, currentActiveTrialLine, currentBranchPoint);
          if (foundIntersection) {
            return true;
          }
        }
      }
      return false;
    }

    private boolean tryNewTrialLine(int direction, List<TrialLine> activeLines, List<TrialLine> inactiveLines, TrialLine activeTrialLine,
                                    LayoutEdgeCoordinate branchPoint) {

      if (!gridPointManager.isAlreadyLinePoint(branchPoint) && gridPointManager.getNumberOfVisits(branchPoint) <= VISIT_INDICATOR) {

        TrialLine newTrialLine = createNewTrialLine(activeTrialLine, branchPoint);
        activeLines.add(newTrialLine);

        TrialLineIntersection intersection = findTrialLineIntersection(newTrialLine, inactiveLines);
        if (!(intersection == null) && !gridPointManager.isAlreadyLinePoint(intersection.getIntersectionPoint())) {
          backtrack(newTrialLine, intersection, direction);
          return true;
        }
      }
      return false;
    }

    /**
     * The Mikami-Tabuchi Routing takes place in this method
     * 
     * 
     */
    public void routeEdge() {

      /*
       * 
       * edge routing takes place between the to central points of the edge.
       * 
       * in case of dockingPoint-preprocessed edges this is between the second and second-last points of the edge,
       * as the first and the last segment are used to establish a padding between several edges
       * sharing a node's side (see initialization)
      */
      routeStartIndex = (int) Math.floor((edge.getEdgePath().size() - 1) / (double) 2);
      routeEndIndex = (int) Math.floor((edge.getEdgePath().size() + 1) / (double) 2);
      LayoutEdgeCoordinate routeStartPoint = edge.getEdgePath().get(routeStartIndex).clone();
      LayoutEdgeCoordinate routeEndPoint = edge.getEdgePath().get(routeEndIndex).clone();

      // If start and end point are at same level try a direct routing:
      boolean directHorizontalEdgePossible = routeStartPoint.getX() - routeEndPoint.getX() == 0
          && gridPointManager.countMappedPointsInSegment(routeStartPoint, routeEndPoint) == 0;
      boolean directVerticalEdgePossible = routeStartPoint.getY() - routeEndPoint.getY() == 0
          && gridPointManager.countMappedPointsInSegment(routeStartPoint, routeEndPoint) == 0;

      if (directHorizontalEdgePossible || directVerticalEdgePossible) {
        gridPointManager.discretizeEdgePathToLinePoints(edge);
        return;
      }

      // if a direct line segment is not possible, the Mikami-Tabuchi algorithm is started:

      List<TrialLine> sLines = createInitialTrialLines(routeStartPoint, edge.getStartDock().getSide());
      List<TrialLine> tLines = createInitialTrialLines(routeEndPoint, edge.getEndDock().getSide());

      // Initial try if the 0-level trial lines are intersecting, if so do a (forward) backtrack and create the edge
      TrialLineIntersection intersection = findTrialLineIntersection(sLines, tLines);
      if (intersection != null && !gridPointManager.isAlreadyLinePoint(intersection.getIntersectionPoint())) {
        backtrack(intersection.getTestLine(), intersection, FORWARD);
        gridPointManager.discretizeEdgePathToLinePoints(edge);
        return;
      }

      List<TrialLine> activeLines = tLines;
      List<TrialLine> inactiveLines = sLines;
      int direction = BACKWARD;
      for (int iterCount = 0; iterCount < MAX_ITER; iterCount++) {
        if (direction == FORWARD) {
          activeLines = tLines;
          inactiveLines = sLines;
          direction = BACKWARD;
        }
        else {
          activeLines = sLines;
          inactiveLines = tLines;
          direction = FORWARD;
        }

        boolean foundIntersection = tryAllActiveBranches(direction, activeLines, inactiveLines);
        if (foundIntersection) {
          gridPointManager.discretizeEdgePathToLinePoints(edge);
          return;
        }
      }

    }

    /**
     *  Searches for the first index of a trial line of maximal level
     *  in a given list with sorted ascending by levels 
     *  
     * @param  trialLinesSortedByLevels List of trial lines sorted by level in ascending order
     * @return list-index of trial line of maximal level
     */
    private int getStartIndexOfMaxlevelLines(List<TrialLine> trialLinesSortedByLevels) {
      int index = trialLinesSortedByLevels.size() - 1;
      int maxLevel = trialLinesSortedByLevels.get(index).getLevel(); // as the assumption is that levels are ascending
      while (index > 0 && trialLinesSortedByLevels.get(index - 1).getLevel() == maxLevel) {
        index--;
      }
      return index;
    }
  }

  public OrthogonalEdgeLayout(LayoutGraph graph) {
    super(graph);
  }

  /**
   *  Initialization of the OrthogonalEdgeLayouter:<br>
   *    - create a GridPointManager instance and translate graph nodes into their gridpoints<br>
   *    - create the empty edges<br>
   *    - create a DockingPointsManager instance and perform pre-processing of edges with respect to their docking points<br>
   */
  public void initialize() {

    Set<LayoutNode> graphNodes = getLayoutGraph().getNodes();

    double widthLimit = getLayoutGraph().getGraphDimension().getWidth() + DIMENSION_CORRECTION;
    double heightLimit = getLayoutGraph().getGraphDimension().getHeight() + DIMENSION_CORRECTION;

    gridPointManager = new GridPointManager(widthLimit, heightLimit);
    gridPointManager.addForbiddenPointsFromNodes(graphNodes);

    edges = new ArrayList<LayoutEdge>(getLayoutGraph().getEdges());
    //  edges are sorted ascending by distance of their ending points
    IsLongerComparator isLongerComparator = new IsLongerComparator(getLayoutGraph());
    Collections.sort(edges, isLongerComparator);

    dockingPointsManager = new DockingPointsManager(gridPointManager);

    /*
     *  pre-processing of docking points:
     *   - initialization
     *   - set up first segments of edges 
     */

    // initialize the docking points of each graph node, i.e.
    // determine all possible docking points along the graphNodes' boundaries
    for (LayoutNode currentLayoutNode : graphNodes) {
      currentLayoutNode.initializeDockingPoints();
    }

    // initialize the docking points for each edge 
    // and do the edge preprocessing
    dockingPointsManager.initializeDockingPoints(edges);

    // block the preprocessed edges:
    gridPointManager.addForbiddenPoints(edges);

  }

  /**
   * Routes every edge of the layoutGraph with help of the MikamiTabuchiAlgorithm<br><br>
   * 
   * Also performs the post-processing of each edge (improvement of docking-points).
   */
  public void doLayout() {

    // individually route each edge
    for (int i = 0; i < edges.size(); i++) {
      LayoutEdge currentEdge = edges.get(i);
      gridPointManager.clearVisitedPoints();

      OrthogonalEdgeMikamiTabuchiAlgorithm mikamiTabuchiAlgorithm = new OrthogonalEdgeMikamiTabuchiAlgorithm(currentEdge, gridPointManager);

      mikamiTabuchiAlgorithm.routeEdge();

    }

    dockingPointsManager.postprocessDockings(edges);

  }

}
