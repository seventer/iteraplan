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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.poi.ss.usermodel.Cell;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.model.BuildingBlock;

/**
 * Holds the {@link BuildingBlock}, its {@code id} and other building block properties like
 * name and description. This entity also holds the {@link ProblemMarker} and comments,
 * associated with each imported field.
 * 
 * <p>By default each field has ProblemMarker#NONE marker and {@code empty} comments.
 */
public class BuildingBlockHolder {
  private BuildingBlock     buildingBlock;
  private String            buildingBlockId;
  private BuildingBlock     clone;

  private Cell              buildingBlockNameCell;
  private ProblemMarker     nameMarker = ProblemMarker.NONE;
  private StringBuilder     nameComment = new StringBuilder();

  private Cell              buildingBlockDescriptionCell;
  private ProblemMarker     descriptionMarker = ProblemMarker.NONE;
  private StringBuilder     descriptionComment = new StringBuilder();

  private Map<BuildingBlockProperty, CellValueHolder> propertyCells = Maps.newHashMap();


  public BuildingBlockHolder(BuildingBlock buildingBlock) {
    this.buildingBlock = buildingBlock;
  }

  public BuildingBlockHolder(BuildingBlock buildingBlock, Map<BuildingBlockProperty, CellValueHolder> propertyCells) {
    this(buildingBlock);
    this.propertyCells = Maps.newHashMap(propertyCells);
  }

  public BuildingBlockHolder(BuildingBlock buildingBlock, Cell buildingBlockNameCell, Cell buildingBlockDescriptionCell) {
    this(buildingBlock);
    this.buildingBlockNameCell = buildingBlockNameCell;
    this.buildingBlockDescriptionCell = buildingBlockDescriptionCell;
  }

  public BuildingBlockHolder(BuildingBlock buildingBlock, Cell buildingBlockNameCell, Cell buildingBlockDescriptionCell, String customId) {
    this(buildingBlock, buildingBlockNameCell, buildingBlockDescriptionCell);
    this.buildingBlockId = customId;
  }

  public BuildingBlock getBuildingBlock() {
    return buildingBlock;
  }

  public Map<BuildingBlockProperty, CellValueHolder> getPropertyCells() {
    return propertyCells;
  }

  public void putPropertyCell(BuildingBlockProperty property, CellValueHolder cellValueHolder) {
    propertyCells.put(property, cellValueHolder);
  }

  public CellValueHolder getPropertyCell(BuildingBlockProperty property) {
    return propertyCells.get(property);
  }

  /**
   * Returns a custom-defined building block ID. May be {@code null} or {@code empty}.
   */
  public String getBuildingBlockId() {
    return buildingBlockId;
  }

  public Cell getBuildingBlockCell() {
    return buildingBlockNameCell;
  }

  public Cell getDescriptionCell() {
    return buildingBlockDescriptionCell;
  }

  public ProblemMarker getDescriptionMarker() {
    return descriptionMarker;
  }

  public String getDescriptionComment() {
    return descriptionComment.toString();
  }
  
  public BuildingBlock getClone() {
    return clone;
  }

  public void setClone(BuildingBlock clone) {
    this.clone = clone;
  }

  public void addNameProblem(ProblemMarker marker, String problemComment) {
    if (this.nameMarker.compareTo(marker) < 0) {
      // only change the marker if it becomes more severe
      this.nameMarker = marker;
    }
    if (this.nameComment.length() > 0) {
      this.nameComment.append('\n');
    }
    this.nameComment.append(problemComment);
  }

  public ProblemMarker getNameMarker() {
    return nameMarker;
  }

  public String getNameComment() {
    return nameComment.toString();
  }

  public void addDescriptionProblem(ProblemMarker marker, String problemComment) {
    if (this.descriptionMarker.compareTo(marker) < 0) {
      // only change the marker if it becomes more severe
      this.descriptionMarker = marker;
    }
    if (this.descriptionComment.length() > 0) {
      this.descriptionComment.append('\n');
    }
    this.descriptionComment.append(problemComment);
  }

  /**
   * Returns the set containing the all available problem markers in properties.
   */
  public Set<ProblemMarker> getAvailableMarkers() {
    final Set<ProblemMarker> result = Sets.newHashSet();

    for (CellValueHolder cellValueHolder : propertyCells.values()) {
      result.add(cellValueHolder.getProblemMarker());
    }

    result.add(nameMarker);
    result.add(descriptionMarker);

    return result;
  }

  @Override
  public String toString() {
    return "BBE " + (buildingBlock != null ? buildingBlock.getNonHierarchicalName() : "(null)") + " from ["
        + ExcelImportUtilities.getCellRef(buildingBlockNameCell) + "]";
  }

  @Override
  public int hashCode() {
    final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
    hashCodeBuilder.append(buildingBlock);
    hashCodeBuilder.append(buildingBlockNameCell);
    hashCodeBuilder.append(buildingBlockId);
    hashCodeBuilder.append(buildingBlockDescriptionCell);
    hashCodeBuilder.append(nameMarker);
    hashCodeBuilder.append(descriptionMarker);

    return hashCodeBuilder.toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof BuildingBlockHolder)) {
      return false;
    }
    final BuildingBlockHolder other = (BuildingBlockHolder) obj;

    return new EqualsBuilder().append(buildingBlock, other.buildingBlock).append(buildingBlockNameCell, other.buildingBlockNameCell)
        .append(buildingBlockId, other.buildingBlockId).append(buildingBlockDescriptionCell, other.buildingBlockDescriptionCell)
        .append(nameMarker, other.nameMarker).append(descriptionMarker, other.descriptionMarker).isEquals();
  }

}