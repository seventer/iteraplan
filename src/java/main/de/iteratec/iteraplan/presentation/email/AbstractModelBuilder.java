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

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;


public abstract class AbstractModelBuilder {

  protected BuildingBlock  bb;
  private String applicationBaseUri;

  public AbstractModelBuilder(BuildingBlock bb, String applicationBaseUri) {
    super();
    this.bb = bb;
    this.applicationBaseUri = applicationBaseUri;
  }

  protected String createLink(BuildingBlock b) {
    if (StringUtils.isNotBlank(applicationBaseUri)) {
      return URLBuilder.getEntityURL(b, applicationBaseUri);
    }
    else {
      return "";
    }
  }

  public EmailModel createModel() {
    EmailModel model = new EmailModel();
    model.setName(bb.getIdentityString());
    model.setLink(this.createLink(bb));
    model.setUser(UserContext.getCurrentUserContext().getLoginName());
    model.setTime(new Date());
    if (StringUtils.isNotBlank(applicationBaseUri)) {
      model.setApplicationLink(applicationBaseUri);
    }
    return model;
  }

  protected void addChange(List<EmailModel.Change> changes, String name, String type, List<? extends BuildingBlock> elements) {
    for (BuildingBlock childAdded : elements) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName(name);
      change.setType(type);
      change.setValue(SubscriptionsUtil.extractName(childAdded));
      change.setLink(this.createLink(childAdded));
      changes.add(change);
    }
  }
}
