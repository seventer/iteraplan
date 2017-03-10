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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.interfaces.IdEntity;


/**
 * Tag for checking write permissions for a building block instance.
 * 
 * This tag expects the user context and the building block instance to check
 * and invokes the UserContext method 'userHasBbInstanceWritePermission' on the 
 * building block instance. The result is stored in the specified page-scoped 
 * bean and contains either true or false.
 */
public class CheckBbInstancePermission2 extends AbstractCheckPermissionTag {
  @Override
  @SuppressWarnings("unchecked")
  public int doStartTag() throws JspException {
    // Look up the user context.
    UserContext uc = (UserContext) pageContext.findAttribute(getUserContext());
    if (uc == null) {
      throw new JspException("Could not find bean: " + getUserContext());
    }

    // Look up the connected user entities.
    Collection<? extends IdEntity> connectedUserEntities = (Collection<? extends IdEntity>) TagUtils.lookup(pageContext, getName(), getProperty(), null);
    if (connectedUserEntities == null) {
      throw new JspException("Bean " + getName() + " with property " + getProperty() + " was not found.");
    }

    HashSet<Integer> connectedUserEntityIds = new HashSet<Integer>(Arrays.asList(GeneralHelper.createIdArrayFromIdEntities(connectedUserEntities)));

    Boolean permissionGranted = Boolean.valueOf(uc.getPerms().userIsPartOfOwningUserEntityIds(connectedUserEntityIds));

    pageContext.setAttribute(getResult(), permissionGranted, PageContext.PAGE_SCOPE);
    return super.doStartTag();
  }
}
