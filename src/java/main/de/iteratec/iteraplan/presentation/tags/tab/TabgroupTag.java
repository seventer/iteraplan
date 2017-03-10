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
package de.iteratec.iteraplan.presentation.tags.tab;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.tags.TagUtils;
import de.iteratec.iteraplan.presentation.tags.tab.model.Tab;
import de.iteratec.iteraplan.presentation.tags.tab.model.Tabgroup;


/**
 * This Tag will first set a Tabgroup called "currentTabgroup" in the pagecontext. This Tabgroup
 * will then be filled with the tabs lying within the tab. The end of this Tag will output all the
 * tabs and their contents to the page.
 */
public class TabgroupTag extends TagSupport {

  private static final long   serialVersionUID               = -526556523358013135L;

  private static final Logger LOGGER                         = Logger.getIteraplanLogger(TabgroupTag.class);

  private static final int    DEFAULT_MAX_TAB_COUNT_PER_LINE = 5;

  public static final String  TABPREFIX                      = "tab_";
  public static final String  CONTPREFIX                     = "cont_";

  private String              tabgroupid;

  private int                 maxTabCountPerLine             = DEFAULT_MAX_TAB_COUNT_PER_LINE;

  public TabgroupTag() {
    super();
  }

  /**
   * Get the tabgroup with the set id and set it into the page context.
   * 
   * @see javax.servlet.jsp.tagext.Tag#doStartTag()
   */
  @Override
  public int doStartTag() throws JspException {

    // EL expressions must be evaluated in doStartTag()
    // and not in attribute setter methods, because servlet
    // containers can reuse tags, and if an attribute takes a
    // string literal, the setter method might not be called
    // every time the tag is encountered.
    evaluateExpressions();

    // the current state of the tab group should be retrieved here (identified by getId()) if the
    // last state of tabs will be saved in the future
    this.pageContext.setAttribute("currentTabgroup", new Tabgroup(getId()));

    return EVAL_PAGE;
  }

  /**
   * Render everything for displaying the tabs and the currently active JSP.
   * 
   * @see javax.servlet.jsp.tagext.Tag#doEndTag()
   */
  @Override
  public int doEndTag() throws JspException {
    JspWriter w = this.pageContext.getOut();

    Tabgroup currentTabgroup = (Tabgroup) this.pageContext.getAttribute("currentTabgroup");

    GuiContext guiContext = GuiContext.getCurrentGuiContext();
    String selectedTab = guiContext.getSelectedTab();

    // if tab info was available, set currentTab info
    if (selectedTab != null) {
      currentTabgroup.setCurrentTabById(selectedTab);
    }

    try {
      // write all tabs
      w.print("<div id=\"");
      w.print(TABPREFIX);
      w.print(currentTabgroup.getId());
      w.println("\">");
      w.println("<ul class=\"nav nav-tabs\">");
      for (Tab t : currentTabgroup.getTabs()) {
        // if the tab is the current tab, use a different style
        Tab currentTab = getCurrentOrDefaultTab(currentTabgroup);
        boolean isCurrentTab = t.getId().equals(currentTab.getId());
        renderTab(w, currentTabgroup, t, isCurrentTab);
      }
      w.println("</ul>");
      w.println("</div>");

      // write all the content of the tabs
      w.print("<div id=\"");
      w.print(CONTPREFIX);
      w.print(currentTabgroup.getId());
      w.println("\">");
      for (Tab t : currentTabgroup.getTabs()) {
        Tab currentTab = getCurrentOrDefaultTab(currentTabgroup);
        boolean isCurrentTab = t.getId().equals(currentTab.getId());
        renderTabContents(w, t, isCurrentTab);
      }
      w.println("</div>");

    } catch (IOException e) {
      LOGGER.error(e);
    }

    return EVAL_PAGE;
  }

  /**
   * Helper method. Returns the current tab, if present, or otherwise the default tab, which is by
   * convention the first tab.
   * 
   * @param currentTabgroup
   *          The currently active tab group.
   * @return See method description.
   */
  private Tab getCurrentOrDefaultTab(Tabgroup currentTabgroup) {

    // try with current tab
    Tab currentTab = currentTabgroup.getCurrentTab();

    // check - no current tab?
    // Not: This is the case when switching from VIEW mode to EDIT mode with the visualisation tab
    // selected.
    if (currentTab == null) {
      // use default tab (i.e. first tab)
      currentTab = currentTabgroup.getTabs().get(0);
    }
    return currentTab;

  }

  @Override
  public String getId() {
    return this.tabgroupid;
  }

  @Override
  public void setId(String tabgroupid) {
    this.tabgroupid = tabgroupid;
  }

  public void setMaxTabCountPerLine(int maxTabCountPerLine) {
    this.maxTabCountPerLine = maxTabCountPerLine;
  }

  public int getMaxTabCountPerLine() {
    return maxTabCountPerLine;
  }

  protected void evaluateExpressions() throws JspException {
    setId(TagUtils.evalString("id", getId(), this, pageContext));
  }

  @SuppressWarnings("boxing")
  private void renderTab(JspWriter writer, Tabgroup group, Tab t, boolean isCurrent) throws IOException, JspException {

    if (!t.isInactive()) {

      int indexToShow = group.getTabs().indexOf(t) + 1;

      String tabStyleCurrent = (isCurrent ? "active " : "") + "link";

      writer.println(String.format("<li id=\"%s%s\" class=\"tab tab%d %s\" >", TABPREFIX, t.getId(), indexToShow, tabStyleCurrent));

      String attrOnClick = "";
      if (t.isClickable()) {
        attrOnClick = String.format("onclick=\"newSwitchTab('%s','tab%d');\" ", group.getId(), indexToShow);
      }

      writer.println(String.format("<a %s>", attrOnClick));

      TagUtils.insertMessage(this.pageContext, t.getText(), t.getTextKey());

      writer.println("</a></li>");
    }
  }

  private void renderTabContents(JspWriter w, Tab t, boolean isCurrent) throws JspException, IOException {

    // Use the CSS classes for switching the visibility of the tabContent
    // This is required for the JS function newSwitchTab() and the printview css. 
    String showContentCssClass = "visible";
    if (!isCurrent) {
      showContentCssClass = "hidden";
    }

    w.print("<div id=\"");
    w.print(t.getId());
    w.print(String.format("\" class=\"tabcontent %s\" ", showContentCssClass));
    w.println(" >");
    TagUtils.insertTile(this.pageContext, t.getPage());
    w.println("</div>");
  }

}