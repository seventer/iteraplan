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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.iteratec.iteraplan.presentation.tags.TagUtils;
import de.iteratec.iteraplan.presentation.tags.tab.model.Tab;
import de.iteratec.iteraplan.presentation.tags.tab.model.Tabgroup;


/**
 * This Tag is used within a Tabgroup Tag. The postcondition is, that the Tabgroup Tag sets an
 * attribute "currentTabgroup" in the pageContext in its StartTag. This tab is then added to the
 * tabgroup.
 */
public class TabTag extends TagSupport {

  private String  id;

  private String  text;

  private String  textKey;

  private String  page;

  private String  showTab;

  private boolean clickable;
  private boolean inactive;

  public TabTag() {
    super();
    // set default values, if nothing was passed in  
    clickable = true;
    inactive = false;
  }

  /**
   * @return The value of the id field.
   */
  @Override
  public String getId() {
    return this.id;
  }

  /**
   * @param id
   *          The new value for the id field.
   */
  @Override
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return The value of the text field.
   */
  public String getText() {
    return this.text;
  }

  /**
   * @param text
   *          The new value for the text field.
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * @return The value of the textKey field.
   */
  public String getTextKey() {
    return this.textKey;
  }

  /**
   * @param textKey
   *          The new value for the textKey field.
   */
  public void setTextKey(String textKey) {
    this.textKey = textKey;
  }

  /**
   * @return The value of the page field.
   */
  public String getPage() {
    return this.page;
  }

  /**
   * @param page
   *          The new value for the page field.
   */
  public void setPage(String page) {
    this.page = page;
  }

  /**
   * @return The value of the showTab field.
   */
  public String getShowTab() {
    return showTab;
  }

  /**
   * @param showTab
   *          The new value for the showTab field.
   */
  public void setShowTab(String showTab) {
    this.showTab = showTab;
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

  /**
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

    Tabgroup tg = (Tabgroup) this.pageContext.getAttribute("currentTabgroup");
    Tab t = new Tab(this.id, this.text, this.textKey, this.page, this.clickable, this.inactive);

    // the 'showTab' attribute is optional, i.e. if it is null, the
    // default value of 'true' is assumed.
    if (showTab == null || (Boolean.parseBoolean(showTab))) {
      tg.addTab(t);
    }
    else {
      tg.removeTab(t);
    }

    return SKIP_BODY;
  }

  /**
   * @see javax.servlet.jsp.tagext.Tag#doEndTag()
   */
  @Override
  public int doEndTag() throws JspException {
    return SKIP_BODY;
  }

  private void evaluateExpressions() throws JspException {
    this.showTab = TagUtils.evalString("showTab", this.showTab, this, pageContext);
  }

}