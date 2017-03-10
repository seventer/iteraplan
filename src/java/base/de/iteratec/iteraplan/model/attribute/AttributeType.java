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
package de.iteratec.iteraplan.model.attribute;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.google.common.collect.ImmutableSet;

import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.interfaces.Entity;


/**
 * The abstract model class for attribute types. <b>Note: this class has a natural ordering that is
 * inconsistent with equals.</b>
 */
@javax.persistence.Entity
@Audited
public abstract class AttributeType implements Comparable<AttributeType>, Entity, Serializable {

  private static final long       serialVersionUID      = -4953457772265576455L;

  /**
   * Blacklist for AttributeType names that cause a conflict with elasticMi.
   * You get the set by iterating over all defined features of all structured types.
   * The reason for a static set of names is, that the user may not see all types at runtime
   * and hence not all forbidden defined featues can be determined from the ElasticMi metamodel.
   */
  public static final Set<String> BLACKLIST_FOR_AT_NAME = ImmutableSet.of("architecturalDomains", "availableForInterfaces", "baseComponents",
                                                            "businessDomains", "businessFunctions", "businessMappings", "businessObject",
                                                            "businessObjectAssociations", "businessObjects", "businessProcess", "businessProcesses",
                                                            "businessUnit", "businessUnits", "children", "description", "direction",
                                                            "generalisation", "id", "informationFlows", "informationFlows1", "informationFlows2",
                                                            "informationSystemDomains", "informationSystemInterface", "informationSystemInterfaces",
                                                            "informationSystemRelease", "informationSystemRelease1", "informationSystemRelease2",
                                                            "informationSystemReleaseAssociations", "informationSystemReleases",
                                                            "informationSystems", "infrastructureElement", "infrastructureElementAssociations",
                                                            "infrastructureElements", "interfaceDirection", "iteraplan_InformationSystemInterfaceID",
                                                            "lastModificationTime", "lastModificationUser", "name", "parent", "parentComponents",
                                                            "position", "predecessors", "product", "products", "projects", "runtimePeriod",
                                                            "specialisations", "successors", "technicalComponentRelease",
                                                            "technicalComponentReleaseAssociations", "technicalComponentReleases", "typeOfStatus");

  private Integer                 id;
  private Integer                 olVersion;
  private String                  name;
  private String                  description;
  private boolean                 mandatory;
  private AttributeTypeGroup      attributeTypeGroup;
  private Set<BuildingBlockType>  buildingBlockTypes    = new HashSet<BuildingBlockType>();
  private String                  lastModificationUser;
  private Date                    lastModificationTime;
  private Integer                 position;

  public AttributeType() {
    super();
  }

  /**
   * Returns the {@link AttributeType} id.
   */
  public Integer getId() {
    return id;
  }

  public Integer getOlVersion() {
    return olVersion;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public boolean isMandatory() {
    return mandatory;
  }

  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public AttributeTypeGroup getAttributeTypeGroup() {
    return attributeTypeGroup;
  }

  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public Set<BuildingBlockType> getBuildingBlockTypes() {
    return buildingBlockTypes;
  }

  public String getLastModificationUser() {
    return lastModificationUser;
  }

  public Date getLastModificationTime() {
    return lastModificationTime;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  public void setName(String name) {
    this.name = StringUtils.trim(name);
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }

  public void setAttributeTypeGroup(AttributeTypeGroup attributeTypeGroup) {
    this.attributeTypeGroup = attributeTypeGroup;
  }

  public void setBuildingBlockTypes(Set<BuildingBlockType> buildingBlockTypes) {
    this.buildingBlockTypes = buildingBlockTypes;
  }

  public void setLastModificationUser(String lastModificationUser) {
    this.lastModificationUser = lastModificationUser;
  }

  public void setLastModificationTime(Date lastModificationTime) {
    this.lastModificationTime = lastModificationTime;
  }

  @AuditMappedBy(mappedBy = "abstractAttributeType")
  public abstract Collection<? extends AttributeValue> getAllAttributeValues();

  /**
   * Adds this attribute type to the given attribute type group, setting a reference on both ends.
   * It is inserted into the group's AT list at the given position (0-based index).
   * 
   * @param atg
   *          The attribute type grouped that <code>this</code> shall be added to.
   * @param position
   *          The position in the group's list, where <code>this</code> shall be inserted. A value
   *          below zero will have it added to the end of the list.
   */
  public void setAttributeTypeGroupTwoWay(AttributeTypeGroup atg, int position) {
    if (position < 0) {
      setAttributeTypeGroupTwoWay(atg);
      return;
    }

    if (atg != null) {
      atg.addAttributeType(this, position);
    }
    if (this.getAttributeTypeGroup() != null) {
      this.getAttributeTypeGroup().removeAttributeType(this);
    }
    this.setAttributeTypeGroup(atg);
  }

  /**
   * Adds this attribute type to the given attribute type group, setting a reference on both ends.
   * It is inserted at the end of the group's AT list.
   * 
   * @param atg
   *          The attribute type grouped that <code>this</code> shall be added to.
   */
  public void setAttributeTypeGroupTwoWay(AttributeTypeGroup atg) {
    int currentLastPosition = 0;
    if (atg != null) {
      if (atg.getAttributeTypes().contains(this) && this.getAttributeTypeGroup() != null && this.getAttributeTypeGroup().equals(atg)) {
        // if atg and at are already consistent, and no special position for the at given, nothing needs to be done
        return;
      }
      currentLastPosition = atg.getAttributeTypes().size();
    }

    setAttributeTypeGroupTwoWay(atg, currentLastPosition);
  }

  public void addBuildingBlockTypeOneWay(BuildingBlockType bbt) {
    if (this.getBuildingBlockTypes() == null) {
      this.setBuildingBlockTypes(new HashSet<BuildingBlockType>());
    }
    this.getBuildingBlockTypes().add(bbt);
  }

  public void addBuildingBlockTypesTwoWay(Set<BuildingBlockType> bbts) {
    for (BuildingBlockType bbt : bbts) {
      bbt.addAttributeTypeTwoWay(this);
    }
  }

  public void addBuildingBlockTypeTwoWay(BuildingBlockType bbt) {
    this.addBuildingBlockTypeOneWay(bbt);
    bbt.addAttributeTypeOneWay(this);
  }

  public void removeAllBuildingBlockTypesTwoWay() {
    if (this.getBuildingBlockTypes() != null) {
      for (BuildingBlockType bbt : getBuildingBlockTypes()) {
        bbt.removeAttributeTypeOneWay(this);
      }
    }
    this.setBuildingBlockTypes(new HashSet<BuildingBlockType>());
  }

  /**
   * Removes the currently set attribute type group from this attribute type. Additionally, removes
   * the reference to this attribute type from the AT group.
   * 
   * @return The position of this attribute type in the list at the attribute type group. It's a
   *         0-based index. If <code>this</code> was not part of an attribute type group yet, -1 is
   *         returned.
   */
  public int removeAttributeTypeGroupTwoWay() {
    int index = -1;
    if (this.getAttributeTypeGroup() != null) {
      index = this.getAttributeTypeGroup().removeAttributeType(this);
    }
    this.setAttributeTypeGroup(null);
    return index;
  }

  public void removeBuildingBlockTypeTwoWay(BuildingBlockType bbt) {
    if (this.getBuildingBlockTypes() != null) {
      this.getBuildingBlockTypes().remove(bbt);
    }
    bbt.removeAttributeTypeOneWay(this);
  }

  public int compareTo(AttributeType other) {
    if ((this.getAttributeTypeGroup() != null) && (other.getAttributeTypeGroup() != null)) {
      if (this.getAttributeTypeGroup().equals(other.getAttributeTypeGroup())) {
        if ((this.getPosition() != null) && (other.getPosition() != null)) {
          return this.getPosition().compareTo(other.getPosition());
        }
      }
      else {
        return this.getAttributeTypeGroup().compareTo(other.getAttributeTypeGroup());
      }
    }
    if ((getName() != null) && (other.getName() != null)) {
      return getName().compareTo(other.getName());
    }
    else {
      return 1;
    }
  }

  public Set<Integer> getBuildingBlockTypeIds() {
    Set<Integer> ids = new HashSet<Integer>();
    for (BuildingBlockType bbt : this.getBuildingBlockTypes()) {
      ids.add(bbt.getId());
    }
    return ids;
  }

  public String getIdentityString() {
    return getName();
  }

  /**
   * Returns a canonical name of this AttributeType, suitable for use as part of a HTML ID.
   * 
   * @return The name as a String, free of non-word characters.
   */
  public String getNameForHtmlId() {
    return this.getName().replaceAll("\\W", "");
  }

  /**
   * @return Returns a String that contains of both the AttributeType and the AttributeTypeGroup
   *         name. If the AttributeType name is <code>null</code>, <code>null</code> is returned.
   */
  public String getNameWithGroup() {
    // the dummy element on the GUI should not cause a null pointer exception
    if (name == null) {
      return null;
    }
    return buildNameWithGroup();
  }

  /**
   * @return Returns a String that contains of both the AttributeType and the AttributeTypeGroup
   *         name. The returned string is never null.
   */
  public String getNameWithGroupForExport() {
    return buildNameWithGroup();
  }

  /**
   * @return Returns the {@link TypeOfAttribute} of the concrete subtype of this abstract class.
   */
  public abstract TypeOfAttribute getTypeOfAttribute();

  /**
   * @return Returns the {@link Class type} of the concrete {@link AttributeValue} associated with
   *         each subtype of this abstract class.
   */
  public abstract Class<? extends AttributeValue> getClassOfAttributeValue();

  public void validate() {
    // Delegate validation to concrete sub-classes.
  }

  /**
   * Creates a String representation of the AttributeType name together with the AttributeTypeGroup
   * name.
   */
  private String buildNameWithGroup() {
    StringBuilder buf = new StringBuilder();
    buf.append(name);
    if (attributeTypeGroup != null) {
      buf.append(" (");
      buf.append(attributeTypeGroup.getName());
      buf.append(')');
    }
    return buf.toString();
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public Integer getPosition() {
    return position;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    // this is wrong for Object.hashCode() calculates a different hash value for every instance
    // this results in even objects with the same id (and thus equal by the equals method) having different hash values
    // int result = super.hashCode();
    int result = 1;
    if (id == null) {
      result = super.hashCode();
    }
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + (mandatory ? 1231 : 1237);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((olVersion == null) ? 0 : olVersion.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AttributeType other = (AttributeType) obj;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(description, other.description);
    builder.append(id, other.id);
    builder.append(mandatory, other.mandatory);
    builder.append(name, other.name);
    builder.append(olVersion, other.olVersion);
    return builder.isEquals();
  }

  // This override was put in place for the alphabetical sorting of attributes.
  // The sort() method in ManyAssociationListComponentModel uses toString() to compare the names of
  // the Attributes.
  // Without the override the class name (i.e. "EnumAT") is returned instead of the attribute name,
  // hence
  // the sorting is not done by attribute name.
  @Override
  public String toString() {
    return name;
  }

}
