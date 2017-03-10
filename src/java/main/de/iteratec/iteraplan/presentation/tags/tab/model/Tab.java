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
package de.iteratec.iteraplan.presentation.tags.tab.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * @author Jürgen Lind (iteratec GmbH)
 * @version $Id: Tab.java 23455 2014-08-19 14:42:27Z gph $
 */
public class Tab implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 2448805644392038771L;
  private String            id;
  private String            text;
  private String            textKey;
  private String            page;
  private boolean           clickable;
  private boolean           inactive;

  public Tab(String id, String text, String textKey, String page, boolean clickable, boolean inactive) {
    this.id = id;
    this.text = text;
    this.textKey = textKey;
    this.page = page;
    this.clickable = clickable;
    this.inactive = inactive;
  }

  /**
   * @return The value of the name field
   */
  public String getId() {
    return this.id;
  }

  /**
   * @param id
   *          The new value for the name field
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return The value of the page field
   */
  public String getPage() {
    return this.page;
  }

  /**
   * @param page
   *          The new value for the page field
   */
  public void setPage(String page) {
    this.page = page;
  }

  /**
   * @return The value of the text field
   */
  public String getText() {
    return this.text;
  }

  /**
   * @param text
   *          The new value for the text field
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * @return The value of the textKey field
   */
  public String getTextKey() {
    return this.textKey;
  }

  /**
   * @param textKey
   *          The new value for the textKey field
   */
  public void setTextKey(String textKey) {
    this.textKey = textKey;
  }

  public boolean isClickable() {
    return clickable;
  }

  public void setClickable(boolean clickable) {
    this.clickable = clickable;
  }

  public boolean isInactive() {
    return inactive;
  }

  public void setInactive(boolean inactive) {
    this.inactive = inactive;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }

    if ((null == obj) || (obj.getClass() != this.getClass())) {
      return false;
    }

    Tab other = (Tab) obj;

    return (id == null ? other.id == null : id.equals(other.id));
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {

    return (id != null ? id.hashCode() : 0);
  }

}