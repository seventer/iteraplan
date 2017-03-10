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

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;


/**
 * This tag extends the LinkToElementTag. It creates an a-href link that, when activated, forwards
 * the user to the management page of a supported object. For more information see
 * {@link LinkToElementTag}.
 * <p>
 * This Tag has two use-cases:
 * </p>
 * <ul>
 * <li>When you provide a link-parameter, this link will be be outputted and surround the contents.</li>
 * <li>When you provide no link-parameter, you can provide all the parameters of the
 * LinkToElementTag and a new link will be created for you.</li>
 * </ul>
 * Please note, that either name or link is required.
 */
public class HtmlLinkToElementTag extends LinkToElementTag {

  private String              isLinked;

  // Style for a-hrefs
  private static final String LINK_STYLE_HTML = "htmlink";

  private String              link;

  @Override
  public int doStartTag() throws JspException {

    if ("true".equals(isLinked)) {

      TagUtils.write(pageContext, "<a href=\"");

      // when a link is provided, output it
      if (!StringUtils.isEmpty(link)) {
        TagUtils.write(pageContext, link);
      }
      // otherwise create a link through superclass, if at least the name is set
      else if (getName() != null) {
        super.setType("html");
        super.doStartTag();
      }
      // if neither the link nor the name is set, the resulting link will be empty

      TagUtils.write(pageContext, String.format("\" class=\"%s\" onclick=\"return false;\" >", LINK_STYLE_HTML));
    }

    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException {
    if ("true".equals(isLinked)) {
      TagUtils.write(pageContext, "</a>");
    }
    return EVAL_PAGE;
  }

  public String getIsLinked() {
    return isLinked;
  }

  public void setIsLinked(String isLinked) {
    this.isLinked = isLinked;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }
}