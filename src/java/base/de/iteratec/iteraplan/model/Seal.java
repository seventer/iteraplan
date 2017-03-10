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
package de.iteratec.iteraplan.model;

import java.util.Date;

import javax.persistence.Entity;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.envers.Audited;

import de.iteratec.iteraplan.model.interfaces.IdEntity;


/**
 * A seal stamp for marking the {@link BuildingBlock} as verified. The seal is valid until the next
 * modification of the {@link BuildingBlock} where this seal belongs to.
 */
@Entity
@Audited
public class Seal implements IdEntity, Comparable<Seal> {
  private static final long serialVersionUID = 5343508035859500746L;

  private Integer           id;
  private BuildingBlock     bb;
  private Date              date;
  private String            user;
  private String            comment;

  /**
   * Default constructor
   */
  public Seal() {
    super();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Returns the creation date.
   * 
   * @return the creation date
   */
  public Date getDate() {
    return date;
  }

  /**
   * Sets the creation date.
   * 
   * @param date the creation date
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Returns the user login name, who created this seal.
   * 
   * @return the user, who created this seal
   */
  public String getUser() {
    return user;
  }

  /**
   * Sets the user, who created this seal.
   * 
   * @param user the user login name
   */
  public void setUser(String user) {
    this.user = user;
  }

  public BuildingBlock getBb() {
    return bb;
  }

  public void setBb(BuildingBlock bb) {
    this.bb = bb;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Seal)) {
      return false;
    }
    final Seal other = (Seal) obj;

    EqualsBuilder builder = new EqualsBuilder();
    builder.append(getId(), other.getId());
    builder.append(getDate(), other.getDate());
    builder.append(getUser(), other.getUser());
    builder.append(getBb(), other.getBb());
    builder.append(getComment(), other.getComment());

    return builder.isEquals();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();
    builder.append(getId());
    builder.append(getDate());
    builder.append(getUser());
    builder.append(getBb());
    builder.append(getComment());

    return builder.toHashCode();
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    builder.append("id", id);
    builder.append("date", date);
    builder.append("user", user);
    builder.append("isr", bb);
    builder.append("comment", comment);

    return builder.toString();
  }

  /** {@inheritDoc} */
  public int compareTo(Seal o) {
    CompareToBuilder builder = new CompareToBuilder();
    builder.append(getDate(), o.getDate());

    return builder.toComparison();
  }
}
