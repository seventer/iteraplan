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
package de.iteratec.turm.functionalTests.user;

import static de.iteratec.turm.functionalTests.role.Constants.ALL_INTIAL_ROLES;
import static de.iteratec.turm.functionalTests.user.Constants.ALL_INTIAL_USERS;
import static de.iteratec.turm.functionalTests.user.Constants.BUTTON_ADD_ROLE;
import static de.iteratec.turm.functionalTests.user.Constants.BUTTON_REMOVE_ROLE;
import static de.iteratec.turm.functionalTests.user.Constants.BUTTON_USER_CREATEUPDATE;
import static de.iteratec.turm.functionalTests.user.Constants.TEXT_USER_FIRSTNAME;
import static de.iteratec.turm.functionalTests.user.Constants.TEXT_USER_LASTNAME;
import static de.iteratec.turm.functionalTests.user.Constants.TEXT_USER_LOGIN;
import static de.iteratec.turm.functionalTests.user.Constants.TEXT_USER_PASSWORD;
import static de.iteratec.turm.functionalTests.user.Constants.TEXT_USER_PASSWORDREPEAT;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import de.iteratec.turm.functionalTests.TurmSession.DialogExpectation;
import de.iteratec.turm.functionalTests.TurmTestBase;


public class UserTest extends TurmTestBase {

  @Test
  public void testAssertUserPage() throws MalformedURLException, IOException {
    login();
    session.assertNoError();
    // users
    assertUserCount(ALL_INTIAL_USERS.length);
    session.assertTextStrings(ALL_INTIAL_USERS);
    // available roles
    assertAvailableRoleCount(ALL_INTIAL_ROLES.length);
    session.assertTextStrings(ALL_INTIAL_ROLES);
    logout();
  }

  @Test
  public void testUpdateUser() throws MalformedURLException, IOException {
    login();

    // click edit
    session.clickElementById("button_edit_user_3");

    // verify
    session.assertTextFieldContentsById("textfield_user_loginName", "jdo");
    session.assertTextFieldContentsById("textfield_user_firstName", "James");
    session.assertTextFieldContentsById("textfield_user_lastName", "Donald");
    session.assertSelectOptionExistsById("option_user_addedRole_1");
    session.assertXPath("count(//node()[starts-with(@id, 'option_user_availableRole_')])="+(ALL_INTIAL_ROLES.length-1));

    // login name empty
    session.setTextFieldById(TEXT_USER_LOGIN, "");
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    session.assertText("Der Login darf nicht leer sein", false);


    // login name exists. also check that once set fields remain set.
    session.setTextFieldById(TEXT_USER_LOGIN, "fmu");
    session.setTextFieldById(TEXT_USER_FIRSTNAME, "Neuer Vorame");
    session.setTextFieldById(TEXT_USER_LASTNAME, "Neuer Nachame");
    session.clickOptionById("option_user_addedRole_1");
    session.clickButtonById(BUTTON_REMOVE_ROLE);
    session.clickOptionById("option_user_availableRole_1");
    session.clickButtonById(BUTTON_ADD_ROLE);
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    session.assertText("Der Login .* existiert bereits", true);
    session.assertTextFieldContentsById(TEXT_USER_LOGIN, "fmu");
    session.assertTextFieldContentsById(TEXT_USER_FIRSTNAME, "Neuer Vorame");
    session.assertTextFieldContentsById(TEXT_USER_LASTNAME, "Neuer Nachame");
    session.assertSelectOptionExistsByIdAndText("option_user_addedRole_1", "Administrator iteraplan");
    session.assertElementIdDoesNotExist("option_user_addedRole_2");

    // change data and save
    session.setTextFieldById(TEXT_USER_LOGIN, "Neuer Login");
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);

    // verify
    session.assertNoError();
    session.assertText("Benutzer mit Login \"Neuer Login\" wurde erfolgreich geändert.", false);
    ArrayList<String> allLoginNames = copyArrayToList(ALL_INTIAL_USERS);
    allLoginNames.add("Neuer Login");
    allLoginNames.remove("jdo");
    Collections.sort(allLoginNames, String.CASE_INSENSITIVE_ORDER);
    session.assertTextStrings(allLoginNames.toArray());
    session.assertTextStrings(new String[] { "Neuer Login", "Neuer Vorame Neuer Nachame",
    " Administrator iteraplan" });
    session.assertXPath("count(id('user_table_4')//tr)=3");

    // change again, but cancel
    session.clickElementById("button_edit_user_4");
    session.setTextFieldById(TEXT_USER_LOGIN, "Anderer Login");
    session.clickElementById("button_cancelCreateUpdateUser");
    session.assertNoText("Anderer Login", false);

    // revert changes
    session.clickElementById("button_edit_user_4");
    session.setTextFieldById(TEXT_USER_LOGIN, "jdo");
    session.setTextFieldById(TEXT_USER_FIRSTNAME, "James");
    session.setTextFieldById(TEXT_USER_LASTNAME, "Donald");
    session.clickOptionById("option_user_addedRole_1");
    session.clickButtonById(BUTTON_REMOVE_ROLE);
    session.clickOptionById("option_user_availableRole_4");
    session.clickButtonById(BUTTON_ADD_ROLE);
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    session.assertNoError();

    logout();
  }

  @Test
  public void testCreateDeleteUser() throws MalformedURLException, IOException {
    login();
    session.assertNoError();

    // login name empty
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    session.assertText("Der Login darf nicht leer sein", false);

    // first name empty
    session.setTextFieldById(TEXT_USER_LOGIN, "Neuer Login Name");
    session.setTextFieldById(TEXT_USER_FIRSTNAME, "");
    session.setTextFieldById(TEXT_USER_LASTNAME, "Neuer NachName");
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    session.assertText("Der Vorname darf nicht leer sein.", false);

    // last name empty
    session.setTextFieldById(TEXT_USER_LOGIN, "Neuer Login Name");
    session.setTextFieldById(TEXT_USER_FIRSTNAME, "Neuer VorName");
    session.setTextFieldById(TEXT_USER_LASTNAME, "");
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    session.assertText("Der Nachname darf nicht leer sein.", false);

    // password too short
    session.setTextFieldById(TEXT_USER_LOGIN, "Neuer Login Name");
    session.setTextFieldById(TEXT_USER_FIRSTNAME, "Neuer VorName");
    session.setTextFieldById(TEXT_USER_LASTNAME, "Neuer NachName");
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    session.assertText("Das Passwort ist zu kurz", false);
    session.assertTextFieldContentsById(TEXT_USER_LOGIN, "Neuer Login Name");

    // password not safe
    session.setTextFieldById(TEXT_USER_FIRSTNAME, "Neuer Vorame");
    session.setTextFieldById(TEXT_USER_LASTNAME, "Neuer Nachame");
    session.setTextFieldById(TEXT_USER_PASSWORD, "password");
    session.setTextFieldById(TEXT_USER_PASSWORDREPEAT, "");
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    session.assertText("Das Passwort ist nicht sicher", false);
    session.assertTextFieldContentsById(TEXT_USER_LOGIN, "Neuer Login Name");
    session.assertTextFieldContentsById(TEXT_USER_FIRSTNAME, "Neuer Vorame");
    session.assertTextFieldContentsById(TEXT_USER_LASTNAME, "Neuer Nachame");
    session.assertTextFieldContentsById(TEXT_USER_PASSWORD, "");
    session.assertTextFieldContentsById(TEXT_USER_PASSWORDREPEAT, "");

    // repeated password not the same
    session.setTextFieldById(TEXT_USER_PASSWORD, "Passw0rt");
    session.setTextFieldById(TEXT_USER_PASSWORDREPEAT, "");
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    session.assertText("Die eingegebenen Passwörter stimmen nicht überein", false);
    session.assertTextFieldContentsById(TEXT_USER_LOGIN, "Neuer Login Name");
    session.assertTextFieldContentsById(TEXT_USER_FIRSTNAME, "Neuer Vorame");
    session.assertTextFieldContentsById(TEXT_USER_LASTNAME, "Neuer Nachame");
    session.assertTextFieldContentsById(TEXT_USER_PASSWORD, "");
    session.assertTextFieldContentsById(TEXT_USER_PASSWORDREPEAT, "");

    // login name already exists
    session.setTextFieldById(TEXT_USER_PASSWORD, "Passw0rt");
    session.setTextFieldById(TEXT_USER_PASSWORDREPEAT, "Passw0rt");
    session.setTextFieldById(TEXT_USER_LOGIN, ALL_INTIAL_USERS[0]);
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    session.assertText("Der Login .* existiert bereits", true);

    // add roles and create
    session.setTextFieldById(TEXT_USER_LOGIN, "Neuer Login Name");
    session.setTextFieldById(TEXT_USER_PASSWORD, "Passw0rt");
    session.setTextFieldById(TEXT_USER_PASSWORDREPEAT, "Passw0rt");
    session.clickOptionById("option_user_availableRole_7");
    session.clickButtonById(BUTTON_ADD_ROLE);
    session.clickOptionById("option_user_availableRole_1");
    session.clickButtonById(BUTTON_ADD_ROLE);
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);

    // verify
    session.assertNoError();
    session.assertText("Benutzer mit Login \"Neuer Login Name\" wurde erfolgreich angelegt", false);
    ArrayList<String> allLoginNames = copyArrayToList(ALL_INTIAL_USERS);
    allLoginNames.add("Neuer Login Name");
    Collections.sort(allLoginNames, String.CASE_INSENSITIVE_ORDER);
    session.assertTextStrings(allLoginNames.toArray());
    session.assertTextStrings(new String[] { "Neuer Login Name", "Neuer Vorame Neuer Nachame",
        " Administrator iteraplan", "iteraplan_Supervisor" });
    session.assertXPath("count(id('user_table_5')//tr)=4");

    // delete
    session.expectDialogs(new DialogExpectation[] { new DialogExpectation(
        "Benutzer Neuer Login Name wirklich löschen?", true, "true") });
    session.clickElementById("button_delete_user_5");
    session.assertText("Benutzer wurde erfolgreich gelöscht.", false);
    session.assertNoText("Neuer Login Name", false);
    session.assertNoText("Neuer Vorame", false);
    session.assertNoText("Neuer Nachame", false);

    logout();
  }



  private void assertUserCount(int count) {
    session.assertXPath("count(//node()[starts-with(@id,'user_table_')])=" + count);
  }

  private void assertAvailableRoleCount(int count) {
    session.assertXPath("count(//node()[starts-with(@id,'option_user_availableRole_')])=" + count);
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
