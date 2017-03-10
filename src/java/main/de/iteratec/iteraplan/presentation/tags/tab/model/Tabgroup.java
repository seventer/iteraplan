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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * @author Jürgen Lind (iteratec GmbH)
 */
public class Tabgroup implements Serializable {

  /** Serialization version. */
  private static final long      serialVersionUID = 8989171234643844077L;
  private String                 id;
  private final List<Tab>        tabs;
  private final Map<String, Tab> tabMap;
  private Tab                    currentTab;

  public Tabgroup() {
    this.tabs = new ArrayList<Tab>();
    this.tabMap = new HashMap<String, Tab>();
  }

  public Tabgroup(String id) {
    this.tabs = new ArrayList<Tab>();
    this.tabMap = new HashMap<String, Tab>();
    this.id = id;
  }

  /**
   * @return 
   *    The value of the name field.
   */
  public String getId() {
    return this.id;
  }

  /**
   * @param id 
   *    The new value for the name field.
   */
  public void setId(String id) {
    this.id = id;
  }

  public void addTab(Tab t) {

    if (!this.tabMap.containsKey(t.getId())) {
      this.tabs.add(t);
      this.tabMap.put(t.getId(), t);

      if (this.currentTab == null) {
        this.currentTab = t;
      }
    }
  }

  public void removeTab(Tab t) {

    if (this.tabMap.containsKey(t.getId())) {
      tabMap.remove(t.getId());
      tabs.remove(t);

      // if the current tab is removed, it is set
      // to the first remaining tab in the list
      if (currentTab.equals(t)) {
        currentTab = tabs.get(0);
      }
    }
  }

  public List<Tab> getTabs() {
    return this.tabs;
  }

  public int getTabCount() {
    return this.tabs.size();
  }

  /**
   * @return 
   *    The value of the currentTab field.
   */
  public Tab getCurrentTab() {
    return this.currentTab;
  }

  /**
   * @param tabId 
   *    The new value for the currentTab field.
   */
  public void setCurrentTabById(String tabId) {
    this.currentTab = this.tabMap.get(tabId);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}