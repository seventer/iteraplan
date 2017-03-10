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

import java.util.Locale;

import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.user.UserEntity;


/**
 * The model class for {@link AttributeValue}s of type {@link ResponsibilityAT}.
 */
@Entity
@Audited
public class ResponsibilityAV extends AttributeValue implements DefaultColorAttributeValue {
  private static final long   serialVersionUID = -1652328332681939101L;

  private static final Logger LOGGER           = Logger.getIteraplanLogger(ResponsibilityAV.class);

  private UserEntity          userEntity;

  private ResponsibilityAT    attributeType;

  private String              defaultColorHex;

  public ResponsibilityAV() {
    super();
  }

  public ResponsibilityAV(ResponsibilityAT attributeType) {
    super();
    this.attributeType = attributeType;
  }

  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public UserEntity getUserEntity() {
    return userEntity;
  }

  public ResponsibilityAT getAttributeType() {
    return attributeType;
  }

  public void setUserEntity(UserEntity userEntity) {
    this.userEntity = userEntity;
  }

  public void setAttributeType(ResponsibilityAT attributeType) {
    this.attributeType = attributeType;
  }

  public void addAttributeTypeTwoWay(ResponsibilityAT at) {
    if (at != null) {
      at.getAttributeValues().add(this);
      setAttributeType(at);
    }
  }

  /**
   * Returns the name or login name (depending on the concrete subtype) of the user entity managed
   * by this attribute value.
   * 
   * @return See method description.
   */
  public String getName() {
    if (this.userEntity == null) {
      return null;
    }

    return this.userEntity.getIdentityString();
  }

  @Override
  public AttributeType getAbstractAttributeType() {
    return this.getAttributeType();
  }

  @Override
  public AttributeValue getCopy() {

    return this;
  }

  @Override
  public String getValueString() {
    return this.getName();
  }

  @Override
  public String getLocalizedValueString(Locale locale) {
    return this.getName();
  }

  @Override
  public Object getValue() {
    return userEntity;
  }

  public String getDefaultColorHex() {
    return defaultColorHex;
  }

  public void setDefaultColorHex(String defaultColorHex) {
    this.defaultColorHex = defaultColorHex;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
    hashCodeBuilder.appendSuper(super.hashCode());
    hashCodeBuilder.append(userEntity);
    return hashCodeBuilder.toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof ResponsibilityAV)) {
      return false;
    }
    ResponsibilityAV other = (ResponsibilityAV) obj;
    EqualsBuilder equalsBuilder = new EqualsBuilder();
    equalsBuilder.append(this.userEntity, other.userEntity);
    return equalsBuilder.isEquals();
  }

  /** Responsibility attribute are sorted by the explicitly stored order */
  public int compareTo(AttributeValue o) {
    if (!(o instanceof ResponsibilityAV)) {
      LOGGER.error("Trying to compare ResponsibilityAV \"{0}\" with \"{1}\", which is not a ResponsibilityAV.", this, o);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    ResponsibilityAV r = ((ResponsibilityAV) o);
    if (this.getName() == null) {
      return -1;
    }
    return this.getName().compareTo(r.getName());
  }
}