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
package de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.HashBucketMatrix;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


/**
 * An element used as axis label for a landscape diagram.
 * 
 * @param <CE>
 *          The type of the {@link ContentElement}s attached to the axis.
 */
public class AxisElement<CE extends ContentElement<?>> extends Element {

  private IdentityEntity                                                            element;

  /** If null, the element is a top level element. */
  private AxisElement<CE>                                                           parent;

  /**
   * List of AxisElement. Should never be null, may be empty.
   */
  private List<AxisElement<CE>>                                                     children        = new ArrayList<AxisElement<CE>>();

  /**
   * The predecessor always has the same parent as this. <code>
   *   this.parent = this.predecessor.parent
   * </code> This applies to having no parent
   * (null value), too.
   */
  private AxisElement<CE>                                                           predecessor;

  /**
   * List of ContentElement. Should never be null. May be empty.
   */
  private List<CE>                                                                  contentElements = new ArrayList<CE>();

  /**
   * Maps a cell to the elements of its "cell group": For every element of the side axis and every
   * element of the top axis this map provides a list with all content elements that are lying in
   * the cell or (transitively through other cells) being related to the elements of this cell.
   */
  private HashBucketMatrix<AxisElement<?>, AxisElement<?>, List<ContentElement<?>>> contentElementGroups;
  /**
   * Maps a cell to the number of different elements in its cell group. This map is used to decrease
   * the number of estimations of the cell groups. Each time a cell group is being computed, all of
   * its cells are being put into the map, so that every further element from this cell or a related
   * cell then doesen't compute the group again, but just takes the result from the map.
   */
  private HashBucketMatrix<AxisElement<?>, AxisElement<?>, Integer>                 contentElementGroupHeight;

  private boolean                                                                   markedAsVisible = false;

  private boolean                                                                   inResultSet     = false;

  /**
   * Stores the maximal number of content elements in a group or related cells over all groups of
   * related cells that are referenced in this axis element.
   */
  private int                                                                       contentHeight;

  /**
   * @param element
   * @param parent
   * @param level
   * @param isInResultSet
   * @param predecessor
   */
  public AxisElement(IdentityEntity element, AxisElement<CE> parent, int level, boolean isInResultSet, AxisElement<CE> predecessor) {
    super(element.getIdentityString());
    this.element = element;
    this.parent = parent;
    if (parent != null) {
      parent.getChildren().add(this);
    }
    this.setLevel(level);
    this.inResultSet = isInResultSet;
    this.markedAsVisible = isInResultSet;
    if (isInResultSet) {
      markPathToRoot(this.parent);
    }
    this.predecessor = predecessor;
    this.contentElementGroups = new HashBucketMatrix<AxisElement<?>, AxisElement<?>, List<ContentElement<?>>>();
    this.contentElementGroupHeight = new HashBucketMatrix<AxisElement<?>, AxisElement<?>, Integer>();
  }

  private void markPathToRoot(AxisElement<CE> bean) {
    if (bean == null || bean.isMarkedAsVisible()) {
      return;
    }
    bean.setMarkedAsVisible(true);
    markPathToRoot(bean.getParent());
  }

  public AxisElement<CE> getPredecessor() {
    return this.predecessor;
  }

  /**
   * Determines the width or height in the result. This is measured in units of the matching content
   * element sizes, i.e. if the axis is vertical then a value of one means "one content shape
   * tall", otherwise it means "one content shape wide".
   * 
   * @return The size of this element measured in content shapes.
   */
  public int getDisplayLength() {
    int length = getChildrenDisplayLength() + getDirectContentDisplayLength();
    // we always want to see the element, so we return at least 1
    if (length > 0) {
      return length;
    }
    else {
      return 1;
    }
  }

  /**
   * This method determines how much space should be assigned for adding content directly related to
   * this element. The width calculated is either one (if content exists) or zero (if no content
   * exists).
   * 
   * @return The width or height required for content directly assigned to this element.
   */
  private int getDirectContentDisplayLength() {
    if (!getContentElements().isEmpty()) {
      return Math.max(contentHeight, 1);
    }
    else {
      return 0;
    }
  }

  public int getUniqueContentElementCount() {
    int count = 1;
    Set<IdentityEntity> set = CollectionUtils.hashSet();
    for (Iterator<CE> it = contentElements.iterator(); it.hasNext();) {
      CE el = it.next();
      set.add(el.getElement());
    }
    if (set.size() > 0) {
      count = set.size();
    }
    return count;
  }

  public HashBucketMatrix<AxisElement<?>, AxisElement<?>, List<ContentElement<?>>> getContentElementGroups() {
    return this.contentElementGroups;
  }

  /**
   * This method initializes the content element groups for a given axis element. This provides
   * later access to the height (in number of content elements) of every axis element, which is
   * needed for the postprocess scaling of the contents and axes as well as for the later
   * positioning of the elements.
   */
  public void createContentElementGroups(boolean vertical, boolean scaleDownContentElements, boolean spanContentBetweenCells) {

    for (ContentElement<?> contentElement : contentElements) {
      // Check if the element belongs to a group whose division factor has already been computed.
      if (contentElementGroupHeight.getBucket(contentElement.getSideAxisRef(), contentElement.getTopAxisRef()) == null) {

        // Locate the element of the other axis which uniquely identifies the cell of the current content element
        AxisElement<?> cellIdentifier = getCellIdentifier(vertical, contentElement);

        //Fetch the cells which build the connected component with regard to the current cell.
        Set<AxisElement<?>> affectedCells = null;
        if (spanContentBetweenCells) {
          //If the content spans between cells the cell group really is the connected component.

          // Retrieve the nonempty cells (for this row/column) of the other axis
          List<AxisElement<?>> checkedElementsList = new ArrayList<AxisElement<?>>();

          // Retrieve all cells that belong to the same cell group
          affectedCells = retrieveCellGroup(cellIdentifier, checkedElementsList, vertical);
        }
        else {
          //If each cell is to be treated independently, the connected component (or cell group)
          //consists of the current cell only.
          affectedCells = new HashSet<AxisElement<?>>();
          affectedCells.add(cellIdentifier);
        }

        // Create buckets
        /*
         * We use the following two buckets to save ourselves a lot of computing. It is important to
         * note, that the getInternalLevel() and getContentElementGroupForElement() methods in the
         * ContentElement class depend on the the "contentElementGroups" bucket. It must therefore
         * be verified that this method has been called for every content element of the
         * LandscapeDiagram before calling any of the methods mentioned above. If this is not the
         * case a NullPointerException might occur, as the needed mapping has not yet been computed
         * at the point of querying.
         */
        addBucket(vertical, affectedCells);
      }
    }

    adjustMappings(scaleDownContentElements);
  }

  private void addBucket(boolean vertical, Set<AxisElement<?>> affectedCells) {
    int cellCount = calculateCellCount(vertical, affectedCells);
    List<ContentElement<?>> contElem = createBucket(vertical, affectedCells);
    for (AxisElement<?> axEl : affectedCells) {
      if (vertical) {
        contentElementGroupHeight.add(axEl, this, Integer.valueOf(cellCount));
        contentElementGroups.add(axEl, this, contElem);
      }
      else {
        contentElementGroupHeight.add(this, axEl, Integer.valueOf(cellCount));
        contentElementGroups.add(this, axEl, contElem);
      }

    }
    this.contentHeight = Math.max(contentHeight, cellCount);
  }

  /**
   * Calculates the number of (different) cells
   * @param vertical
   *          ordering of cells
   * @param affectedCells
   * @return number of cells
   */
  private int calculateCellCount(boolean vertical, Set<AxisElement<?>> affectedCells) {
    Set<IdentityEntity> resultSet = CollectionUtils.hashSet();

    for (ContentElement<?> ce : contentElements) {
      if (vertical) {
        if (affectedCells.contains(ce.getSideAxisRef())) {
          resultSet.add(ce.getElement());
        }
      }
      else {
        if (affectedCells.contains(ce.getTopAxisRef())) {
          resultSet.add(ce.getElement());
        }
      }
    }
    if (resultSet.size() > 0) {
      return resultSet.size();
    }
    return 1;
  }

  private AxisElement<?> getCellIdentifier(boolean vertical, ContentElement<?> contentElement) {
    AxisElement<?> cellIdentifier;
    if (vertical) {
      cellIdentifier = contentElement.getSideAxisRef();
    }
    else {
      cellIdentifier = contentElement.getTopAxisRef();
    }
    return cellIdentifier;
  }

  private List<ContentElement<?>> createBucket(boolean vertical, Set<AxisElement<?>> affectedCells) {
    List<ContentElement<?>> contElem = CollectionUtils.arrayList();
    for (ContentElement<?> ce : contentElements) {
      if (vertical) {
        if (affectedCells.contains(ce.getSideAxisRef()) && !contElem.contains(ce)) {
          contElem.add(ce);
        }
      }
      else {
        if (affectedCells.contains(ce.getTopAxisRef()) && !contElem.contains(ce)) {
          contElem.add(ce);
        }
      }
    }
    return contElem;
  }

  private void adjustMappings(boolean scaleDownContentElements) {
    // If the downscale for content elements is disabled, we set all element groups to be of the
    // same basic height, which is the maximal height over all groups. We need this as in this case
    // the content element height is the metric for determining the row/column height/width and as
    // such must be the same for all elements.
    if (!scaleDownContentElements) {
      for (ContentElement<?> contentElement : contentElements) {
        contentElementGroupHeight.remove(contentElement.getSideAxisRef(), contentElement.getTopAxisRef());
        contentElementGroupHeight.add(contentElement.getSideAxisRef(), contentElement.getTopAxisRef(), Integer.valueOf(contentHeight));
      }
    }
    else {
      // In the case of scaling down the contents we use the axis elements as metric. Therefore an
      // axis element has a unit height of 1, as being then returned by the method
      // getDirectContentDisplayLength()
      contentHeight = 1;
    }
  }

  /**
   * Retrieves the height of the group of the content element. If the scaling of content elements is
   * disabled, the global maximal number of content elements for the axis element will be returned.
   * 
   * @param contentElement
   *          The content element whose group is of importance
   * @return The number of elemenents in the gruop of the content element or the maximal number of
   *         elements per group over all groups for this axis element if content scaling is
   *         disabled.
   */
  public int getUniqueContentElementCount(ContentElement<?> contentElement) {
    return contentElementGroupHeight.getBucket(contentElement.getSideAxisRef(), contentElement.getTopAxisRef()).get(0).intValue();
  }

  private Set<AxisElement<?>> retrieveCellGroup(AxisElement<?> cellIdentifier, List<AxisElement<?>> checkedCellsList, boolean vertical) {

    Set<AxisElement<?>> resultSet = new HashSet<AxisElement<?>>();

    // Mark cell as visited if not visited already
    if (checkedCellsList.contains(cellIdentifier)) {
      return new HashSet<AxisElement<?>>();
    }
    else {
      checkedCellsList.add(cellIdentifier);
    }

    // Locate all elements in cell
    List<ContentElement<?>> cellContents = createCellContentList(cellIdentifier, vertical);

    // Build a list of cells to visit
    List<AxisElement<?>> cellsToCheck = getCellsToCheck(checkedCellsList, vertical, cellContents);

    resultSet.add(cellIdentifier);

    for (AxisElement<?> axEl : cellsToCheck) {
      Set<AxisElement<?>> tmp = retrieveCellGroup(axEl, checkedCellsList, vertical);
      for (AxisElement<?> entity : tmp) {
        if (!resultSet.contains(entity)) {
          resultSet.add(entity);
        }
      }
    }

    return resultSet;
  }

  /**
   * @param checkedCellsList
   *          already checked cells
   * @param vertical
   *          cell ordering
   * @param cellContents
   *          list of content elements
   * @return cells which need checking
   */
  private List<AxisElement<?>> getCellsToCheck(List<AxisElement<?>> checkedCellsList, boolean vertical, List<ContentElement<?>> cellContents) {
    List<AxisElement<?>> cellsToCheck = new ArrayList<AxisElement<?>>();
    for (ContentElement<?> ce : cellContents) {
      for (ContentElement<?> checker : contentElements) {
        if (checker.getElement().getIdentityString().equals(ce.getElement().getIdentityString())) {
          AxisElement<?> sideAxisRef = checker.getSideAxisRef();
          AxisElement<?> topAxisRef = checker.getTopAxisRef();

          if (vertical && !cellsToCheck.contains(sideAxisRef) && !checkedCellsList.contains(sideAxisRef)) {
            cellsToCheck.add(sideAxisRef);
          }
          else if (!cellsToCheck.contains(topAxisRef) && !checkedCellsList.contains(topAxisRef)) {
            cellsToCheck.add(topAxisRef);
          }
        }
      }
    }
    return cellsToCheck;
  }

  private List<ContentElement<?>> createCellContentList(AxisElement<?> cellIdentifier, boolean vertical) {
    List<ContentElement<?>> cellContents = new ArrayList<ContentElement<?>>();
    for (ContentElement<?> ce : contentElements) {
      if (vertical) {
        if (ce.getSideAxisRef().getElement().getIdentityString().equals(cellIdentifier.getElement().getIdentityString())) {
          cellContents.add(ce);
        }
      }
      else {
        if (ce.getTopAxisRef().getElement().getIdentityString().equals(cellIdentifier.getElement().getIdentityString())) {
          cellContents.add(ce);
        }
      }
    }
    return cellContents;
  }

  public int getDisplayStartPosition() {
    if (getParent() == null && getPredecessor() == null) {
      return 0;
    }
    if (getParent() != null && getPredecessor() == null) {
      return getParent().getDisplayStartPosition();
    }
    return getPredecessor().getDisplayStartPosition() + getPredecessor().getDisplayLength();
  }

  public List<AxisElement<CE>> getChildren() {
    return children;
  }

  public List<AxisElement<CE>> getAllChildren() {
    List<AxisElement<CE>> result = new ArrayList<AxisElement<CE>>();
    childrenFetcher(getChildren(), result);
    return result;
  }

  private void childrenFetcher(List<AxisElement<CE>> childrenToProcess, List<AxisElement<CE>> result) {
    if (childrenToProcess == null) {
      return;
    }
    result.addAll(childrenToProcess);
    for (Iterator<AxisElement<CE>> it = childrenToProcess.iterator(); it.hasNext();) {
      AxisElement<CE> child = it.next();
      childrenFetcher(child.getChildren(), result);
    }
  }

  public void setChildren(List<AxisElement<CE>> children) {
    this.children = children;
  }

  public boolean isMarkedAsVisible() {
    return markedAsVisible;
  }

  public void setMarkedAsVisible(boolean markedAsVisible) {
    this.markedAsVisible = markedAsVisible;
  }

  public AxisElement<CE> getParent() {
    return parent;
  }

  public IdentityEntity getElement() {
    return element;
  }

  public boolean isInResultSet() {
    return inResultSet;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("'" + this.element.getIdentityString());
    sb.append("\nLevel: " + getLevel());
    sb.append("\nDisplay start: " + getDisplayStartPosition());
    sb.append("\nDisplay length: " + getDisplayLength());
    sb.append("\nUnique Content Els: " + getUniqueContentElementCount());
    sb.append("\nisInResultSet: " + inResultSet);
    if (this.predecessor != null) {
      sb.append("\nPredecessor: " + this.predecessor.getElement().getIdentityString() + " (" + this.predecessor.getLevel() + ") "
          + this.predecessor.hashCode());
    }
    else {
      sb.append("\nPredecessor: null");
    }
    sb.append("\nContent Elements: ");
    for (Iterator<CE> it = getContentElements().iterator(); it.hasNext();) {
      CE el = it.next();
      sb.append(el.getName());
      if (it.hasNext()) {
        sb.append(", ");
      }
    }
    return sb.toString();
  }

  public void setParent(AxisElement<CE> parent) {
    this.parent = parent;
  }

  public List<CE> getContentElements() {
    return contentElements;
  }

  public void setContentElements(List<CE> contentElements) {
    this.contentElements = contentElements;
  }

  public void addContentElement(CE el) {
    this.contentElements.add(el);
  }

  public int getChildrenDisplayLength() {
    int length = 0;
    for (Iterator<AxisElement<CE>> it = getChildren().iterator(); it.hasNext();) {
      AxisElement<CE> child = it.next();
      length += child.getDisplayLength();
    }
    return length;
  }

  public int getTailStart() {
    return getDisplayStartPosition() + getChildrenDisplayLength();
  }

  public int getDisplayEndPosition() {
    return getDisplayStartPosition() + getDisplayLength();
  }

  public void setPredecessor(AxisElement<CE> predecessor) {
    this.predecessor = predecessor;
  }

  public int getTotalLevel() {
    int length = 1;
    int maxChildLevel = 0;
    for (Iterator<AxisElement<CE>> it = getChildren().iterator(); it.hasNext();) {
      AxisElement<CE> child = it.next();
      int childTotal = child.getTotalLevel();
      if (childTotal > maxChildLevel) {
        maxChildLevel = childTotal;
      }
    }
    return length + maxChildLevel;
  }
}
