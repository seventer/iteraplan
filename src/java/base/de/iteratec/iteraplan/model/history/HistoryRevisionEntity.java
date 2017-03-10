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
package de.iteratec.iteraplan.model.history;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.joda.time.DateTime;

import de.iteratec.iteraplan.model.interfaces.IdEntity;

/**
 * This is the revision entity that gets created by the Listener whenever a transaction is committed.
 * The default is extended to add a user name.
 * @author rge
 */
@RevisionEntity(HistoryRevisionListener.class)
public class HistoryRevisionEntity implements IdEntity, Serializable {

  private static final long serialVersionUID = 4784291080873331691L;

  @RevisionNumber
  private Integer id;

  @RevisionTimestamp
  private long timestamp;

  private String username;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Transient
  public Date getRevisionDate() {
    return new Date(timestamp);
  }

  @Transient
  public DateTime getRevisionDateTime() {
    return new DateTime(timestamp);
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String toString() {
    return "IteraHistoryRevisionEntity(id = " + getId() + ", revisionDate = " + DateFormat.getDateTimeInstance().format(getRevisionDate())
        + ", user = " + getUsername() + ")";
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
        appendSuper(super.hashCode()).
        append(id).
        append(timestamp).
        append(username).
        toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof HistoryRevisionEntity)) {
      return false;
    }
    final HistoryRevisionEntity other = (HistoryRevisionEntity) obj;

    return new EqualsBuilder().
        appendSuper(super.equals(obj)).
        append(id, other.id).
        append(timestamp, other.timestamp).
        append(username, other.username).
        isEquals();
  }
}
