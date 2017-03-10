/*
 * iTURM is a User and Roles Management web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.turm.functionalTests.role;

import static de.iteratec.turm.functionalTests.role.Constants.ALL_INTIAL_ROLES;
import static de.iteratec.turm.functionalTests.role.Constants.BUTTON_ADD_USER;
import static de.iteratec.turm.functionalTests.role.Constants.BUTTON_REMOVE_USER;
import static de.iteratec.turm.functionalTests.role.Constants.BUTTON_ROLE_CREATEUPDATE;
import static de.iteratec.turm.functionalTests.role.Constants.TEXT_ROLE_NAME;
import static de.iteratec.turm.functionalTests.user.Constants.ALL_INTIAL_USERS;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import de.iteratec.turm.functionalTests.TurmSession.DialogExpectation;
import de.iteratec.turm.functionalTests.TurmTestBase;


public class RoleTest extends TurmTestBase {

  @Test
  public void testAssertRolePage() throws MalformedURLException, IOException {
    login();
    clickRoleTab();
    session.assertNoError();
    // roles
    assertRoleCount(ALL_INTIAL_ROLES.length);
    session.assertTextStrings(ALL_INTIAL_ROLES);
    // available users
    assertAvailableUserCount(ALL_INTIAL_USERS.length);
    session.assertTextStrings(ALL_INTIAL_USERS);
    logout();
  }

  @Test
  public void testUpdateRole() throws MalformedURLException, IOException {
    login();
    clickRoleTab();

    // click edit
    session.clickElementById("button_edit_role_7");

    // verify
    session.assertTextFieldContentsById(TEXT_ROLE_NAME, "iteraplan_Supervisor");
    session.assertSelectOptionExistsById("option_role_addedUser_1");
    session.assertXPath("count(//node()[starts-with(@id, 'option_role_availableUser_')])="
        + (ALL_INTIAL_USERS.length - 1));

    // role name empty
    session.setTextFieldById(TEXT_ROLE_NAME, "");
    session.clickButtonById(BUTTON_ROLE_CREATEUPDATE);
    session.assertText("Der Rollenname darf nicht leer sein.", false);

    // login name exists. also check that once set fields remain set.
    session.setTextFieldById(TEXT_ROLE_NAME, ALL_INTIAL_ROLES[0]);
    session.clickOptionById("option_role_addedUser_1");
    session.clickButtonById(BUTTON_REMOVE_USER);
    session.clickOptionById("option_role_availableUser_1");
    session.clickButtonById(BUTTON_ADD_USER);
    session.clickButtonById(BUTTON_ROLE_CREATEUPDATE);
    session.assertText("Die Rolle "+ALL_INTIAL_ROLES[0]+" existiert bereits.", true);
    session.assertTextFieldContentsById(TEXT_ROLE_NAME, ALL_INTIAL_ROLES[0]);
    session.assertSelectOptionExistsByIdAndText("option_role_addedUser_1", "fmu");
    session.assertElementIdDoesNotExist("option_role_addedUser_2");

    // change data and save
    session.setTextFieldById(TEXT_ROLE_NAME, "Neue Rolle");
    session.clickButtonById(BUTTON_ROLE_CREATEUPDATE);

    // verify
    session.assertNoError();
    session.assertText("Rolle \"Neue Rolle\" wurde erfolgreich geändert.", false);
    ArrayList<String> allRoleNames = copyArrayToList(ALL_INTIAL_ROLES);
    allRoleNames.add("Neue Rolle");
    allRoleNames.remove("iteraplan_Supervisor");
    Collections.sort(allRoleNames, String.CASE_INSENSITIVE_ORDER);
    session.assertTextStrings(allRoleNames.toArray());
    session.assertXPath("count(id('role_table_7')//tr)=2");
    session.assertXPath("contains(id('role_table_7')//tr[2],'fmu')");

    // change again, but cancel
    session.clickElementById("button_edit_role_7");
    session.setTextFieldById(TEXT_ROLE_NAME, "Andere Rolle");
    session.clickElementById("button_cancelCreateUpdateRole");
    session.assertNoText("Anderer Login", false);

    // revert changes
    session.clickElementById("button_edit_role_7");
    session.setTextFieldById(TEXT_ROLE_NAME, "iteraplan_Supervisor");
    session.clickOptionById("option_role_addedUser_1");
    session.clickButtonById(BUTTON_REMOVE_USER);
    session.clickOptionById("option_role_availableUser_4");
    session.clickButtonById(BUTTON_ADD_USER);
    session.clickButtonById(BUTTON_ROLE_CREATEUPDATE);
    session.assertNoError();

    logout();
  }

  private void clickRoleTab() throws IOException {
    session.clickElementById("link_role_tab");
  }

  @Test
  public void testCreateDeleteRole() throws MalformedURLException, IOException {
    login();
    clickRoleTab();
    session.assertNoError();

    // role name empty
    session.clickButtonById(BUTTON_ROLE_CREATEUPDATE);
    session.assertText("Der Rollenname darf nicht leer sein.", false);

    // role name already exists
    session.setTextFieldById(TEXT_ROLE_NAME, ALL_INTIAL_ROLES[0]);
    session.clickOptionById("option_role_availableUser_1");
    session.clickButtonById(BUTTON_ADD_USER);
    session.clickButtonById(BUTTON_ROLE_CREATEUPDATE);
    session.assertText("Die Rolle "+ALL_INTIAL_ROLES[0]+" existiert bereits.", true);
    session.assertSelectOptionExistsByIdAndText("option_role_addedUser_1", "fmu");

    // change and save
    session.setTextFieldById(TEXT_ROLE_NAME, "Neue Rolle");
    session.clickButtonById(BUTTON_ROLE_CREATEUPDATE);

    // verify
    session.assertNoError();
    session.assertText("Rolle \"Neue Rolle\" wurde erfolgreich angelegt.", false);
    ArrayList<String> allRoleNames = copyArrayToList(ALL_INTIAL_ROLES);
    allRoleNames.add("Neue Rolle");
    Collections.sort(allRoleNames, String.CASE_INSENSITIVE_ORDER);
    session.assertTextStrings(allRoleNames.toArray());
    session.assertXPath("count(id('role_table_8')//tr)=2");
    session.assertXPath("contains(id('role_table_8')//tr[2],'fmu')");

    // delete
    session.expectDialogs(new DialogExpectation[] { new DialogExpectation(
        "Rolle Neue Rolle wirklich löschen?", true, "true") });
    session.clickElementById("button_delete_role_8");
    session.assertText("Rolle wurde erfolgreich gelöscht.", false);
    session.assertNoText("Neue Rolle", false);

    logout();
  }

  private void assertRoleCount(int count) {
    session.assertXPath("count(//node()[starts-with(@id,'role_table_')])=" + count);
  }

  private void assertAvailableUserCount(int count) {
    session.assertXPath("count(//node()[starts-with(@id,'option_role_availableUser_')])=" + count);
  }

  private ArrayList<String> copyArrayToList(String[] array) {
    ArrayList<String> list = new ArrayList<String>();
    for (int i = 0; i < array.length; i++) {
      String string = array[i];
      list.add(string);
    }
    return list;
  }
}
