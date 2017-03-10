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

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserGroup;


public class IteratecWriteTagTest {

  @Before
  public void setUp() {
    User user = new User();
    user.setLoginName("guest");
    user.setFirstName("");
    user.setLastName("");
    user.setDataSource("MASTER");
    user.setId(Integer.valueOf(0));
    user.setParentUserGroups(new HashSet<UserGroup>());

    Set<Role> roles = Sets.newHashSet();
    UserContext userContext = new UserContext(user.getLoginName(), roles, Locale.ENGLISH, user);

    UserContext.setCurrentUserContext(userContext);
  }

  @After
  public void tearDown() {
    UserContext.setCurrentUserContext(null);
  }

  @Test
  public void decorateFileLinks() {
    String original = "<a href=\"file:\\\\\\Filesrv1-muc\\\\allgemein\\\\test.txt\">test-text</a>";
    String expected = "<a onclick=\"javascript:return alert(&#39;Directly opening a file in your web "
        + "browser is not allowed due to security concerns. Please right click on the link to copy the link "
        + "address. &#39;);\" href=\"file:\\\\\\Filesrv1-muc\\\\allgemein\\\\test.txt\">test-text</a>";

    UserContext.getCurrentUserContext().setLocale(new Locale("en"));
    String decorated = TagUtils.decorateFileLinks(original);
    assertEquals(expected, decorated);
  }
}
