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

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.model.BuildingBlock;

/**
 * A class for storing a {@link BuildingBlock} and its associated attributes.
 */
public class BuildingBlockAttributes {

  private BuildingBlock       buildingBlock;
  /** Attribute header names associated with the content. */
  private Map<String, CellValueHolder> attributes;

  public BuildingBlockAttributes(BuildingBlock buildingBlock, Map<String, CellValueHolder> attributes) {
    this.buildingBlock = buildingBlock;
    this.attributes = attributes;
  }
  
  public BuildingBlock getBuildingBlock() {
    return buildingBlock;
  }
  
  /**
   * Returns the attribute header names associated with the content values.
   * 
   * @return the attribute header names associated with the content values.
   */
  public Map<String, CellValueHolder> getAttributes() {
    return attributes;
  }
  
  /**
   * Returns the set containing the all available problem markers in attributes.
   */
  public Set<ProblemMarker> getAvailableMarkers() {
    final Set<ProblemMarker> result = Sets.newHashSet();
    
    for (CellValueHolder cellValueHolder : attributes.values()) {
      result.add(cellValueHolder.getProblemMarker());
    }
    
    return result; 
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Attributes on BBE ");
    sb.append(buildingBlock.getNonHierarchicalName());
    sb.append(": ");
    for (Map.Entry<String, CellValueHolder> atEntry : this.attributes.entrySet()) {
      sb.append(atEntry.getKey());
      sb.append("=");
      sb.append(atEntry.getValue().toString());
      sb.append(", ");
    }
    return sb.toString();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof BuildingBlockAttributes)) {
      return false;
    }
    final BuildingBlockAttributes other = (BuildingBlockAttributes) obj;
    
    return new EqualsBuilder().append(buildingBlock, other.buildingBlock).append(attributes, other.attributes).isEquals();
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(buildingBlock).append(attributes).toHashCode();
  }
}