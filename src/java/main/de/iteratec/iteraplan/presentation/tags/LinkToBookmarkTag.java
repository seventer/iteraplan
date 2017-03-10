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
package de.iteratec.iteraplan.presentation.tags;

import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.flow.FlowEntry;


/**
 * This tag creates a WebFlow URL for a building block, that is possible to be added to bookmarks
 * for Firefox or IE
 */
public class LinkToBookmarkTag extends TagSupport {

  private static final long serialVersionUID = -619195030388664573L;

  private String parameter;

  @Override
  public int doStartTag() throws JspException {

    GuiContext guiCtx = GuiContext.getCurrentGuiContext();
    String dialogName = guiCtx.getActiveDialogName();
    Collection<FlowEntry> flowEntries = guiCtx.getAllFlowsForDialogName(dialogName);

    FlowEntry entry = null;

    // get the current flowEntry
    for (FlowEntry singleEntry : flowEntries) {

      if (singleEntry.getKey().equalsIgnoreCase(guiCtx.getActiveDialog())) {
        // get Id of the building block
        entry = singleEntry;
        break;
      }
    }
    if (entry == null) { // still null?
      // don't create a link at all
      return SKIP_BODY;
    }

    if (parameter.equalsIgnoreCase("url")) {
      TagUtils.write(pageContext, URLBuilder.getAbsoluteURLforFlow(pageContext, dialogName, entry.getEntityId()));
    }
    else if (parameter.equalsIgnoreCase("title")) {
      TagUtils.write(pageContext, "iteraplan - " + entry.getLabel());
    }
    else if (parameter.equalsIgnoreCase("id")) {
      TagUtils.write(pageContext, entry.getEntityId().toString());
    }

    // continue processing this page
    return SKIP_BODY;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter(String parameter) {
    this.parameter = parameter;
  }
}