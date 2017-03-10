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

import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


/**
 * This class represents the assignment of an arbitrary attribute value to an arbitrary building
 * block.
 * <p>
 * Table name: ava.
 */
@Entity
@Audited
@Indexed(index = "index.AttributeValueAssignment")
public class AttributeValueAssignment implements IdentityEntity, Serializable {

  private static final long serialVersionUID = 5287868502287186113L;

  /** {@link #getId()} */
  @DocumentId
  private Integer           id;

  /** {@link #getOlVersion()} */
  private Integer           olVersion;

  /** {@link #getBuildingBlock()} */
  @ContainedIn
  private BuildingBlock     buildingBlock;

  /** {@link #getAttributeValue()} */
  @IndexedEmbedded
  private AttributeValue    attributeValue;

  public AttributeValueAssignment() {
    super();
  }

  public AttributeValueAssignment(BuildingBlock bb, AttributeValue av) {
    this.buildingBlock = bb;
    this.attributeValue = av;
  }

  /**
   * @return
   *    The unique identifier of the building block.
   */
  public Integer getId() {
    return id;
  }

  /**
   * @return
   *    The optimistic locking version.
   */
  public Integer getOlVersion() {
    return olVersion;
  }

  /**
   * @return
   *    The {@link BuildingBlock} that has been assigned the {@link #getAttributeValue() attribute value}.
   */
  public BuildingBlock getBuildingBlock() {
    return buildingBlock;
  }

  /**
   * @return
   *    The {@link AttributeValue} that has been assigned to the {@link #getBuildingBlock() building block}.
   */
  public AttributeValue getAttributeValue() {
    return attributeValue;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  public void setBuildingBlock(BuildingBlock buildingBlock) {
    this.buildingBlock = buildingBlock;
  }

  public void setAttributeValue(AttributeValue attributeValue) {
    this.attributeValue = attributeValue;
  }

  /**
   * Creates the references to both the given {@link AttributeValue} and {@link BuildingBlock}
   * for this attribute value assignment.
   * <p>
   * This method updates only this side of the association to the attribute value. This is
   * sufficient if the Java object model must not be accurate, that is, if the assignment is
   * immediately saved to the database and won't be needed further. In this case this method is
   * highly preferred over setting both sides of the association, because trying to set both
   * sides results in a database join over each and every subclass of {@link BuildingBlock}. In
   * some situations this causes an error with MySQL.
   * <p>
   * On the other hand this method updates both sides of the association to the building block.
   * 
   * @param av
   *    The attribute value to add.
   * @param bb
   *    The building block to add.
   * 
   * @throws IllegalArgumentException
   *    If the passed-in attribute value and/or building block is {@code null}.
   */
  public void addReferences(AttributeValue av, BuildingBlock bb) {

    if (av == null || bb == null) {
      throw new IllegalArgumentException("Neither the attribute value and the building block must be null.");
    }

    // Set the attribute value.
    setAttributeValue(av);

    // Set the building block.
    bb.getAttributeValueAssignments().add(this);
    setBuildingBlock(bb);
  }

  /**
   * Removes the references to both the {@link AttributeValue} and the {@link BuildingBlock}
   * from this attribute value assignment.
   * <p>
   * This method updates only this side of the association to the attribute value. This is
   * sufficient if the assignment must not be processed further but shall be immediately saved
   * to the database. In this case this method is highly preferred over removing both sides of
   * the association.
   * <p>
   * On the other hand this method updates both sides of the association to the building block.
   */
  public void removeReferences() {

    if (attributeValue != null && buildingBlock != null) {
      setAttributeValue(null);

      buildingBlock.getAttributeValueAssignments().remove(this);
      setBuildingBlock(null);
    }
  }

  public String getIdentityString() {
    return id.toString();
  }

  @Override
  public String toString() {

    StringBuilder result = new StringBuilder(70);
    result.append("AttributeValueAssignment ID: ").append(id);
    if (buildingBlock != null) {
      result.append("\tBuildingBlock ID: ").append(buildingBlock.getId());
    }
    if (attributeValue != null) {
      result.append("\tAttributeValue ID: ").append(attributeValue.getId());
    }

    return result.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof AttributeValueAssignment)) {
      return false;
    }
    final AttributeValueAssignment other = (AttributeValueAssignment) obj;

    EqualsBuilder equalsBuilder = new EqualsBuilder();
    equalsBuilder.append(getId(), other.getId());

    return equalsBuilder.isEquals();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();

    if (getId() == null) {
      hashCodeBuilder.appendSuper(super.hashCode());
    }
    else {
      hashCodeBuilder.append(getId());
    }

    return hashCodeBuilder.toHashCode();
  }
}
