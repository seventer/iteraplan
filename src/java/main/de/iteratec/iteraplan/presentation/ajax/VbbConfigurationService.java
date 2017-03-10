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
package de.iteratec.iteraplan.presentation.ajax;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;


/**
 *
 */
public interface VbbConfigurationService {

  Map<String, Collection<Tag>> recommend(Map<String, String> parameters, String viewpointName);

  Map<String, String> recommendColorMapping(String holders, String attributeName);

  class Tag implements Comparable<Tag> {
    private String    id;
    private String    name;
    private int       prio;
    private List<Tag> children;
    private boolean   isRoot;

    public Tag() {
      this.children = Lists.newLinkedList();
    }

    public Tag(String id, String name, int prio, boolean isRoot) {
      this();
      this.id = id;
      this.name = name;
      this.isRoot = isRoot;
      this.prio = prio;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getPrio() {
      return prio;
    }

    public void setPrio(int prio) {
      this.prio = prio;
    }

    protected void addChild(Tag tag) {
      this.children.add(tag);
    }

    public void setChildren(List<Tag> children) {
      this.children.clear();
      this.children.addAll(children);
    }

    public List<Tag> getChildren() {
      return Collections.unmodifiableList(children);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Tag o) {
      if (this.prio < o.prio) {
        return 1;
      }
      else if (this.prio > o.prio) {
        return -1;
      }
      else if (this.name == null) {
        return o.name == null ? 0 : 1;
      }
      else if (this.name.startsWith(o.name)) {
        return 1;
      }
      else if (o.name.startsWith(this.name)) {
        return -1;
      }
      else {
        return this.name.compareTo(o.name);
      }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
      return obj instanceof Tag && compareTo((Tag) obj) == 0;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return this.name == null ? this.prio : this.name.hashCode() ^ this.prio;
    }

    public boolean isRoot() {
      return isRoot;
    }

    public void setRoot(boolean isRoot) {
      this.isRoot = isRoot;
    }
  }
}
