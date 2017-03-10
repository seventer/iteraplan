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
package de.iteratec.iteraplan.presentation.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmailModel {

  public static final String ADDED     = "added";
  public static final String REMOVED   = "removed";
  public static final String CHANGED   = "changed";

  private String name;

  private String link;

  private String user;

  private Date time;

  private String type;

  private String applicationLink;

  private List<Change> changes = new ArrayList<Change>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getApplicationLink() {
    return applicationLink;
  }

  public void setApplicationLink(String applicationLink) {
    this.applicationLink = applicationLink;
  }

  public void setChanges(List<Change> changes) {
    this.changes = changes;
  }

  public List<Change> getChanges() {
    return changes;
  }

  public static class Change {

    private String name;

    private String type;

    private Object value;

    private String link;

    private Object from;

    private String fromLink;

    private Object to;

    private String toLink;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public Object getValue() {
      return value;
    }

    public void setValue(Object value) {
      this.value = value;
    }

    public String getLink() {
      return link;
    }

    public void setLink(String link) {
      this.link = link;
    }

    public Object getFrom() {
      return from;
    }

    public void setFrom(Object from) {
      this.from = from;
    }

    public String getFromLink() {
      return fromLink;
    }

    public void setFromLink(String fromLink) {
      this.fromLink = fromLink;
    }

    public Object getTo() {
      return to;
    }

    public void setTo(Object to) {
      this.to = to;
    }

    public String getToLink() {
      return toLink;
    }

    public void setToLink(String toLink) {
      this.toLink = toLink;
    }
  }
}
