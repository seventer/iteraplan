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
package de.iteratec.iteraplan.presentation;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;


/**
 * {@link LdapUserDetailsImpl} additionally containing the users email address retrieved from the directory
 * 
 * @see IteraplanLdapUserDetailsMapper
 */
public class IteraplanLdapUserDetails extends LdapUserDetailsImpl {
  private static final long   serialVersionUID = 1L;

  private static final Logger LOGGER           = Logger.getIteraplanLogger(IteraplanLdapUserDetails.class);

  private String              firstName;
  private String              lastName;
  private String              mail;

  /**
   * @return firstName the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * @return lastName the lastName
   */
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * @return mail the mail
   */
  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  /**{@inheritDoc}**/
  @Override
  @SuppressWarnings("PMD.UselessOverridingMethod")
  // equality remains the same as for LdapUserDetailsImpl
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /**{@inheritDoc}**/
  @Override
  @SuppressWarnings("PMD.UselessOverridingMethod")
  // hashcode remains the same as for LdapUserDetailsImpl
  public int hashCode() {
    return super.hashCode();
  }

  public static class Essence extends LdapUserDetailsImpl.Essence {
    //default constructor
    public Essence() {
      super();
    }

    public Essence(IteraplanLdapUserDetails copyMe) {
      super(copyMe);
      LOGGER.debug("Copying user attributes from old UserDetails..");
      setFirstName(copyMe.getFirstName());
      setLastName(copyMe.getLastName());
      setMail(copyMe.getMail());
    }

    public Essence(DirContextOperations ctx) {
      super(ctx);
      LOGGER.debug("Reading user attributes from context..");
      IteraplanProperties iteraplanProperties = IteraplanProperties.getProperties();
      try {
        String firstnameProp = iteraplanProperties.getProperty(IteraplanProperties.LDAP_FIELDNAME_FIRSTNAME);
        if (ctx.attributeExists(firstnameProp)) {
          setFirstName(ctx.getStringAttribute(firstnameProp));
        }
        else {
          LOGGER.info("Attribute {0} could not be read from the LDAP directory, check correctness of property {1}", firstnameProp, IteraplanProperties.LDAP_FIELDNAME_FIRSTNAME);
        }
      } catch (IteraplanTechnicalException ex) {
        LOGGER.warn("Property {0} missing in application configuration. User details cannot be extracted from the LDAP directory", IteraplanProperties.LDAP_FIELDNAME_FIRSTNAME);
      }

      try {
        String lastnameProp = iteraplanProperties.getProperty(IteraplanProperties.LDAP_FIELDNAME_LASTNAME);
        if (ctx.attributeExists(lastnameProp)) {
          setLastName(ctx.getStringAttribute(lastnameProp));

        }
        else {
          LOGGER.info("Attribute {0} could not be read from the LDAP directory, check correctness of property {1}", lastnameProp, IteraplanProperties.LDAP_FIELDNAME_LASTNAME);
        }
      } catch (IteraplanTechnicalException ex) {
        LOGGER.warn("Property {0} missing in application configuration. User details cannot be extracted from the LDAP directory", IteraplanProperties.LDAP_FIELDNAME_LASTNAME);
      }

      try {
        String emailProp = iteraplanProperties.getProperty(IteraplanProperties.LDAP_FIELDNAME_EMAIL);
        if (ctx.attributeExists(emailProp)) {
          setMail(ctx.getStringAttribute(emailProp));
        }
        else {
          LOGGER.info("Attribute {0} could not be read from the LDAP directory, check correctness of property {1}", emailProp, IteraplanProperties.LDAP_FIELDNAME_EMAIL);
        }
      } catch (IteraplanTechnicalException ex) {
        LOGGER.warn("Property {0} missing in application configuration. User details cannot be extracted from the LDAP directory", IteraplanProperties.LDAP_FIELDNAME_EMAIL);
      }
    }

    /**{@inheritDoc}**/
    @Override
    protected LdapUserDetailsImpl createTarget() {
      return new IteraplanLdapUserDetails();
    }

    public final void setFirstName(String firstName) {
      // explicitly declare the casted user details to avoid pmd warning complaining about parameter reassignment
      IteraplanLdapUserDetails details = (IteraplanLdapUserDetails) instance;
      LOGGER.debug("Setting firstName \"{0}\".", firstName);
      details.firstName = firstName;
    }

    public final void setLastName(String lastName) {
      // explicitly declare the casted user details to avoid pmd warning complaining about parameter reassignment
      IteraplanLdapUserDetails details = (IteraplanLdapUserDetails) instance;
      LOGGER.debug("Setting lastName \"{0}\".", lastName);
      details.lastName = lastName;
    }

    public final void setMail(String mail) {
      // explicitly declare the casted user details to avoid pmd warning complaining about parameter reassignment
      IteraplanLdapUserDetails details = (IteraplanLdapUserDetails) instance;
      LOGGER.debug("Setting email \"{0}\".", mail);
      details.mail = mail;
    }
  }
}
