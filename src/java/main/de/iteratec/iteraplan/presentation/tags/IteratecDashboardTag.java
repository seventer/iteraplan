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

import de.iteratec.iteraplan.businesslogic.service.wiki.IteraplanSyntaxParserService;
import de.iteratec.iteraplan.common.Logger;


/**
 * This tag provides basically the same functionality as the IteratecWriteTag,
 * but it convert by default wiki notations into HTML.
 * Furthermore it convert the iteratec Syntax also in HTML.
 * This Class/Tag provide the basic function for an dashboard view.
 */
public class IteratecDashboardTag extends IteratecWriteTag {

  /** Serialization version. */
  private static final long   serialVersionUID = 8479261772774685726L;
  
  private static final Logger LOGGER           = Logger.getIteraplanLogger(IteratecDashboardTag.class);

  /** Reference ID to an additional savedQuery / tabular Report */
  private String              refIdProperty    = null;

  public String getRefIdProperty() {
    return refIdProperty;
  }

  public void setRefIdProperty(String refIdProperty) {
    this.refIdProperty = refIdProperty;
    try {
      this.refIdProperty = TagUtils.evalString("refIdProperty", refIdProperty, this, pageContext);
    } catch (JspException e) {
      LOGGER.error(e);
    }
  }

  /**{@inheritDoc}**/
  @Override
  public int doStartTagInternal() throws JspException {
    // Look up the requested bean (if necessary)
    if (TagUtils.lookup(pageContext, getName(), getScope()) == null) {
      return SKIP_BODY; // Nothing to output
    }

    // Look up the requested property value
    Object value = TagUtils.lookup(pageContext, getName(), getProperty(), getScope());
    if (value == null) {
      return SKIP_BODY; // Nothing to output
    }

    // Convert value to the String with some formatting
    String output = formatValue(value);

    IteraplanSyntaxParserService syntaxParserService = getRequestContext().getWebApplicationContext().getBean(IteraplanSyntaxParserService.class);

    if (getRefIdProperty() == null) {
      output = syntaxParserService.convertIteraplanSyntax(output, getUserAgent());
    }
    else {
      Object refIdObject = TagUtils.lookup(pageContext, getName(), getRefIdProperty(), getScope());
      if(refIdObject != null){
        String refId = formatValue(refIdObject);
        output = syntaxParserService.convertIteraplanSyntax(output, getUserAgent(), new Integer(refId));
      }
    }

    if (isBreaksAndSpaces()) {
      output = TagUtils.breaksAndSpaces(output);
    }

    TagUtils.write(pageContext, output);

    // Continue processing this page
    return SKIP_BODY;
  }

}
