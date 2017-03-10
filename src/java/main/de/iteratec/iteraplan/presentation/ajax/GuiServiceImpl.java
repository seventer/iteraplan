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

import static de.iteratec.iteraplan.presentation.SessionConstants.GUI_CONTEXT;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import de.iteratec.iteraplan.presentation.GuiContext;


/**
 * This class offers Ajax remote method calls to save the selected tab
 */
public class GuiServiceImpl implements GuiService {

  public GuiContext getGuiContext() {
    // get Session
    WebContext ctx = WebContextFactory.get();
    HttpSession session = ctx.getSession(false);
    if (session == null) {
      // no session yet, i.e. user is not logged in -> ignore
      return null;
    }

    // get GuiContext
    GuiContext guiContext = (GuiContext) session.getAttribute(GUI_CONTEXT);
    return (guiContext != null) ? guiContext : GuiContext.getCurrentGuiContext();
  }

  /**
   * updates the selected tab of the dialog in the GuiContext
   * 
   * @param tabName
   *          name of the tab
   */
  public void setSelectedTab(String tabName) {

    GuiContext guiContext = getGuiContext();

    // get correct Map for dialog name
    Map<String, Map<String, String>> selectedTabMap = guiContext.getSelectedTabMap();
    Map<String, String> dialogTab;

    // if no map exists so far for this dialog, create one
    if (selectedTabMap.get(guiContext.getActiveDialogName()) == null) {
      dialogTab = new HashMap<String, String>();
      selectedTabMap.put(guiContext.getActiveDialogName(), dialogTab);
    }

    // set for this dialog / flow page the selected tab
    dialogTab = selectedTabMap.get(guiContext.getActiveDialogName());
    String key = guiContext.getDialogNameForTabIdentification();
    dialogTab.put(key, tabName);

  }

  /**
   * updates the status of the attribute type group in the GuiContext
   * 
   * @param atgName
   *          name of the attribute type group
   */
  public void setAttributeGroupStatus(String atgName) {

    GuiContext guiContext = getGuiContext();

    // get correct Map for dialog name
    Map<String, SetMultimap<String, String>> openedATGMap = guiContext.getOpenedATGMap();

    // if no map exists so far for this dialog, create one
    if (!openedATGMap.containsKey(guiContext.getActiveDialogName())) {
      SetMultimap<String, String> newEmptyMap = HashMultimap.create();
      openedATGMap.put(guiContext.getActiveDialogName(), newEmptyMap);
    }
    // set for this dialog / flow page the selected tab
    SetMultimap<String, String> atgTab = openedATGMap.get(guiContext.getActiveDialogName());

    String key = guiContext.getDialogNameForTabIdentification();
    Set<String> openedATGs = atgTab.get(key);
    // if attribute type group block is already open, swap its state
    if (openedATGs.contains(atgName)) {
      openedATGs.remove(atgName);
    }
    else {
      openedATGs.add(atgName);
    }
  }

  /**
   * updates the status of a menu section in the GuiContext
   * 
   * @param menuIndex
   *          index of the menu sections starting with 0
   * @return true if changed to expanded, otherwise false
   */
  public boolean setMenuStatus(int menuIndex) {

    // get Session
    WebContext ctx = WebContextFactory.get();
    HttpSession session = ctx.getSession(false);
    if (session == null) {
      // no session yet, i.e. user is not logged in -> ignore
      return false;
    }

    // get GuiContext
    GuiContext guiContext = (GuiContext) session.getAttribute(GUI_CONTEXT);
    if (guiContext == null) {
      guiContext = GuiContext.getCurrentGuiContext();
    }
    // inverse boolean value of menu status
    Boolean[] menuStatus = guiContext.getExpandedMenuStatus();
    if (menuIndex < menuStatus.length) {
      checkForNull(menuStatus);
      if (menuStatus[menuIndex].booleanValue()) {
        menuStatus[menuIndex] = Boolean.FALSE;
      }
      else {
        menuStatus[menuIndex] = Boolean.TRUE;
      }
      guiContext.setExpandedMenuStatus(menuStatus);
      session.setAttribute(GUI_CONTEXT, guiContext);
    }
    return menuStatus[menuIndex].booleanValue();
  }

  /**
   * Check the menuStatus if it contains any null-values. This might happen when changing the
   * locale. In this case context actions and open elements are open, watched element is closed.
   * 
   * @param menuStatus
   */
  private void checkForNull(Boolean[] menuStatus) {
    for (int i = 0; i < menuStatus.length; i++) {
      if (menuStatus[i] == null) {
        menuStatus[i] = (i == 2) ? Boolean.FALSE : Boolean.TRUE;
      }
    }

  }
}
