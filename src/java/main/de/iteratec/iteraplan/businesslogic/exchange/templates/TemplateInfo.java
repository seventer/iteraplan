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
package de.iteratec.iteraplan.businesslogic.exchange.templates;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Holds information about a template file: the file name, and whether it should be deletable or not
 */
public class TemplateInfo implements Comparable<TemplateInfo>, Serializable {

  /** Serialization version */
  private static final long serialVersionUID = -6540676128924294641L;

  private String            name;
  private boolean           deletable;

  public TemplateInfo(String templateName) {
    this.name = templateName;
    this.deletable = true;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setDeletable(boolean deletable) {
    this.deletable = deletable;
  }

  public boolean isDeletable() {
    return deletable;
  }

  public int compareTo(TemplateInfo other) {
    if (other.isDeletable() == this.deletable) {
      return this.name.compareTo(other.getName());
    }
    else {
      // non deletable objects will always be "less than" deletable ones.
      return this.isDeletable() ? 1 : -1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }

    if (!o.getClass().equals(this.getClass())) {
      return false;
    }

    TemplateInfo other = (TemplateInfo) o;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(this.deletable, other.isDeletable());
    builder.append(this.name, other.getName());

    return builder.isEquals();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();
    builder.append(this.deletable);
    builder.append(this.name);
    return builder.toHashCode();
  }
}