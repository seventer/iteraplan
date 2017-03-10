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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.poi.ss.usermodel.Cell;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.User;


/**
 * Represents the landscape data consisting of building blocks, attributes and relations.
 * The order of the added elements stays always the same.
 * 
 * @author agu
 *
 */
public class LandscapeData implements Serializable {
  /** Serialization version. */
  private static final long                         serialVersionUID = -7427080277022157893L;

  private static final Logger                       LOGGER           = Logger.getIteraplanLogger(LandscapeData.class);

  private final List<BuildingBlockHolder>           buildingBlocks   = Lists.newArrayList();
  private final List<BuildingBlockAttributes>       attributes       = Lists.newArrayList();
  private final List<BuildingBlockRelations>        relations        = Lists.newArrayList();
  private final Map<TypeOfBuildingBlock, Set<User>> users            = Maps.newHashMap();

  private Locale locale;
  
  public void addBuildingBlock(BuildingBlockHolder buildingBlock) {
    buildingBlocks.add(buildingBlock);
  }

  /**
   * Returns list of all building blocks, read out of the Excel file.
   * 
   * @return list of all building blocks
   */
  public List<BuildingBlockHolder> getBuildingBlocks() {
    return buildingBlocks;
  }

  public void addAttributes(BuildingBlock buildingBlock, Map<String, Cell> attributesMap) {
    LOGGER.debug("putting in {0}   {1}", buildingBlock.getNonHierarchicalName(), attributesMap);

    final Map<String, CellValueHolder> attributeNameToValue = Maps.newHashMap();
    for (Entry<String, Cell> cellEntry : attributesMap.entrySet()) {
      final CellValueHolder attribute = new CellValueHolder(cellEntry.getValue());
      attributeNameToValue.put(cellEntry.getKey(), attribute);
    }

    BuildingBlockAttributes bbAttributes = new BuildingBlockAttributes(buildingBlock, attributeNameToValue);
    attributes.add(bbAttributes);

  }

  /**
   * Returns list of all building blocks attributes, read out of the Excel file.
   * 
   * @return list of all building blocks attributes
   */
  public List<BuildingBlockAttributes> getAttributes() {
    return attributes;
  }

  public void addRelation(BuildingBlock buildingBlock, Map<String, Cell> content, Map<String, Cell> atts) {
    LOGGER.debug("putting in {0} {1}", buildingBlock.getNonHierarchicalName(), atts);

    final Map<String, CellValueHolder> relationTypeToTargetsMap = Maps.newHashMap();

    for (Entry<String, Cell> cellEntry : content.entrySet()) {
      CellValueHolder attribute = new CellValueHolder(cellEntry.getValue());
      relationTypeToTargetsMap.put(cellEntry.getKey(), attribute);
    }

    LOGGER.debug("putting relations in {0} {1}", buildingBlock.getNonHierarchicalName(), relationTypeToTargetsMap);

    final Map<String, CellValueHolder> relAttributeNameToValue = Maps.newHashMap();
    for (Entry<String, Cell> cellEntry : atts.entrySet()) {
      CellValueHolder attribute = new CellValueHolder(cellEntry.getValue());
      relAttributeNameToValue.put(cellEntry.getKey(), attribute);
    }

    relations.add(new BuildingBlockRelations(buildingBlock, relationTypeToTargetsMap, relAttributeNameToValue));
  }

  /**
   * Returns list of all building blocks relations, read out of the Excel file.
   * 
   * @return list of all building blocks relations
   */
  public List<BuildingBlockRelations> getRelations() {
    return relations;
  }

  public Map<TypeOfBuildingBlock, Set<User>> getUsers() {
    return users;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  /**
   * A class for storing {@link BuildingBlock} and its associated attributes.
   * 
   * @author agu
   *
   */
  public static class BuildingBlockAttributes implements Serializable {
    /** Serialization version. */
    private static final long                  serialVersionUID = 7066726302265246785L;
    private final BuildingBlock                buildingBlock;
    /** Attribute header names associated with the content. */
    private final Map<String, CellValueHolder> attributes;

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
     * @return The number of the excel sheet row for these building block attributes as String
     */
    public String getRowNum() {
      for (CellValueHolder cell : attributes.values()) {
        if (cell != null && cell.getOriginCell() != null) {
          return ExcelImportUtilities.getCellRow(cell.getOriginCell());
        }
      }
      return "undef";
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

  /**
   * A class for storing relation {@link BuildingBlock}s and its associated content and attributes.
   * 
   * @author agu
   *
   */
  public static class BuildingBlockRelations implements Serializable {
    /** Serialization version. */
    private static final long                  serialVersionUID = -7257350125302752794L;
    private final BuildingBlock                buildingBlock;
    /** Content header names associated with the content. */
    private final Map<String, CellValueHolder> content;
    /** Attribute header names associated with the content. */
    private final Map<String, CellValueHolder> attributes;

    public BuildingBlockRelations(BuildingBlock buildingBlock, Map<String, CellValueHolder> content, Map<String, CellValueHolder> attributes) {
      this.buildingBlock = buildingBlock;
      this.content = content;
      this.attributes = attributes;
    }

    public BuildingBlock getBuildingBlock() {
      return buildingBlock;
    }

    /**
     * Returns the building block header names associated with the content values.
     * 
     * @return the building block header names associated with the content values
     */
    public Map<String, CellValueHolder> getContent() {
      return content;
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
     * @return The number of the excel sheet row for these building block relations as String
     */
    public String getRowNum() {
      Collection<CellValueHolder> cells = new HashSet<CellValueHolder>(content.values());
      cells.addAll(attributes.values());
      for (CellValueHolder cell : cells) {
        if (cell != null && cell.getOriginCell() != null) {
          return ExcelImportUtilities.getCellRow(cell.getOriginCell());
        }
      }
      return "undef";
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof BuildingBlockRelations)) {
        return false;
      }
      final BuildingBlockRelations other = (BuildingBlockRelations) obj;

      return new EqualsBuilder().append(buildingBlock, other.buildingBlock).append(content, other.content).append(attributes, other.attributes)
          .isEquals();
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder().append(buildingBlock).append(content).append(attributes).toHashCode();
    }
  }
}
