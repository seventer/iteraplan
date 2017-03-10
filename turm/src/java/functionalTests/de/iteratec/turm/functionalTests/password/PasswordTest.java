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
package de.iteratec.turm.functionalTests.password;

import static de.iteratec.turm.functionalTests.user.Constants.BUTTON_ADD_ROLE;
import static de.iteratec.turm.functionalTests.user.Constants.BUTTON_REMOVE_ROLE;
import static de.iteratec.turm.functionalTests.user.Constants.BUTTON_USER_CREATEUPDATE;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.iteratec.turm.functionalTests.TurmTestBase;

public class PasswordTest extends TurmTestBase {

  @Before
  public void setUp() throws MalformedURLException, IOException {
    // give "iteraplan_Supervisor" role to user "reader"
    login();
    session.clickElementById("button_edit_user_4");
    session.clickElementById("option_user_availableRole_5");
    session.clickButtonById(BUTTON_ADD_ROLE);
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    logout();
  }

  @After
  public void tearDown() throws MalformedURLException, IOException {
    // revert role assignment
    login();
    session.clickElementById("button_edit_user_4");
    session.clickElementById("option_user_addedRole_3");
    session.clickButtonById(BUTTON_REMOVE_ROLE);
    session.clickButtonById(BUTTON_USER_CREATEUPDATE);
    logout();

  }

  @Test
  public void testPasswordChange() throws MalformedURLException, IOException {
    session.goToPasswordPage();
    session.assertText("Bitte geben Sie Ihren Login, Ihr altes Passwort und ein neues Passwort ein.", false);

    // password too short
    session.setTextFieldById("text_password", "pass");
    session.setTextFieldById("text_passwordRepeated", "pass");
    session.clickButtonById("button_submit");
    session.assertText("Das Passwort ist zu kurz", false);

    // password not safe
    session.setTextFieldById("text_password", "password");
    session.setTextFieldById("text_passwordRepeated", "password");
    session.clickButtonById("button_submit");
    session.assertText("Das Passwort ist nicht sicher", false);

    // passwords don't match
    session.setTextFieldById("text_password", "Passw0rt");
    session.setTextFieldById("text_passwordRepeated", "Passw1rt");
    session.clickButtonById("button_submit");
    session.assertText("Die eingegebenen Passwörter stimmen nicht überein.", false);

    // login not correct
    session.setTextFieldById("text_login_name", "mmu1");
    session.setTextFieldById("text_oldPassword", "Reader123");
    session.setTextFieldById("text_password", "Reader1234");
    session.setTextFieldById("text_passwordRepeated", "Reader1234");
    session.clickButtonById("button_submit");
    session.assertText("Der angegebene Loginname oder das Passwort ist falsch.", false);
    session.assertTextFieldContentsById("text_login_name", "mmu1");

    // old password not correct
    session.setTextFieldById("text_login_name", "mmu");
    session.setTextFieldById("text_oldPassword", "Reader1234");
    session.setTextFieldById("text_password", "Reader123");
    session.setTextFieldById("text_passwordRepeated", "Reader123");
    session.clickButtonById("button_submit");
    session.assertText("Der angegebene Loginname oder das Passwort ist falsch.", false);

    // password not changed
    session.setTextFieldById("text_login_name", "mmu");
    session.setTextFieldById("text_oldPassword", "Reader123");
    session.setTextFieldById("text_password", "Reader123");
    session.setTextFieldById("text_passwordRepeated", "Reader123");
    session.clickButtonById("button_submit");
    session.assertText("Das neue Passwort ist das gleiche wie das alte Passwort. Bitte geben Sie ein neues Passwort ein.", false);

    // save changes
    session.setTextFieldById("text_login_name", "mmu");
    session.setTextFieldById("text_oldPassword", "Reader123");
    session.setTextFieldById("text_password", "Reader1234");
    session.setTextFieldById("text_passwordRepeated", "Reader1234");
    session.clickButtonById("button_submit");

    // assert old password is no longer valid
    session.assertNoError();
    session.login("mmu", "Reader123", false);
    session.assertText("Der angegebene Loginname oder das Passwort ist falsch.", false);

    // assert new password is valid
    session.login("mmu", "Reader1234");
    logout();

    // revert changes
    session.goToPasswordPage();
    session.setTextFieldById("text_login_name", "mmu");
    session.setTextFieldById("text_oldPassword", "Reader1234");
    session.setTextFieldById("text_password", "Reader123");
    session.setTextFieldById("text_passwordRepeated", "Reader123");
    session.clickButtonById("button_submit");
    session.assertNoError();
  }

}
