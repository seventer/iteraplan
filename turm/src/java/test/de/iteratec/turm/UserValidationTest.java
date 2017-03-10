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
package de.iteratec.turm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.iteratec.turm.exceptions.TurmException;
import de.iteratec.turm.exceptions.UserInputException;
import de.iteratec.turm.model.UserContainer;

public class UserValidationTest {

  @Test
  public void testUserContainerValidation() {
    UserContainer uc = new UserContainer();

    uc.setLoginName("");
    uc.setFirstName("bbb");
    uc.setLastName("ccc");
    // test empty login name
    try {
      uc.validateBasicFields();
      fail();
    } catch(UserInputException e) {
      assertEquals("error.loginMustNotBeEmpty", e.getMessage());
    }

    uc.setLoginName("aaa");
    uc.setFirstName("");
    uc.setLastName("ccc");
    // test empty first name
    try {
      uc.validateBasicFields();
      fail();
    } catch(UserInputException e) {
      assertEquals("error.firstNameMustNotBeEmpty", e.getMessage());
    }

    uc.setLoginName("aaa");
    uc.setFirstName("bbb");
    uc.setLastName("");
    // test empty last name
    try {
      uc.validateBasicFields();
      fail();
    } catch(UserInputException e) {
      assertEquals("error.lastNameMustNotBeEmpty", e.getMessage());
    }

    uc.setLoginName("aaa");
    uc.setFirstName("bbb");
    uc.setLastName("ccc");
    try {
      uc.validateBasicFields();
    } catch (UserInputException e) {
      fail("Validation should have passed.");
    }

    // test too short password
    uc.setPassword("");
    uc.setPasswordRepeat("");
    try {
      uc.validatePassword();
      fail();
    } catch (UserInputException e) {
      assertEquals("error.passwordTooShort", e.getMessage());
    }

    // test insecure password
    uc.setPassword("password");
    uc.setPasswordRepeat("password");
    try {
      uc.validatePassword();
      fail();
    } catch (UserInputException e) {
      assertEquals("error.insecurePassword", e.getMessage());
    }

    // test password mismatch
    uc.setPassword("Passw0rt");
    uc.setPasswordRepeat("Passw1rt");
    try {
      uc.validatePassword();
      fail();
    } catch (UserInputException e) {
      assertEquals("error.passwordsDoNotMatch", e.getMessage());
    }

    uc.setPassword("Passw0rt");
    uc.setPasswordRepeat("Passw0rt");
    try {
      uc.validateAll();
    } catch (TurmException e) {
      fail("Validation should have passed.");
    }
  }

}
