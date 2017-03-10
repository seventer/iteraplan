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
package de.iteratec.turm.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class that takes a plain-text user supplied password, and applies a one-way message digest
 * algorithm to generate an encrypted password that will be compared against an already encrypted
 * password record, most likely held in a database. This class will typically be used by a servlet
 * or struts action class that needs to enforce programmatic security.
 * 
 * This class was copied from the iteraplan project.
 */
public final class PasswordEncryption {
  public static final String        MESSAGEDIGEST_SHA = "SHA";
  public static final String        MESSAGEDIGEST_MD2 = "MD2";
  public static final String        MESSAGEDIGEST_MD5 = "MD5"; // default

  public static final byte          BYTE_MASK         = 0x0f;

  private static PasswordEncryption instance;

  /** Private modifier to prevent instantiation */
  private PasswordEncryption() {
    // no instantiation
  }

  /**
   * Encrypts the supplied plaintext password with the default message digest MD5 algorithm.
   * 
   * @throws UnsupportedEncodingException 
   * @throws NoSuchAlgorithmException 
   */
  public synchronized String getEncryptedPassword(String plaintext) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    return getEncryptedPassword(plaintext, MESSAGEDIGEST_MD5);
  }

  /**
   * Encrypts the supplied plaintext password with the supplied algorithm.
   */
  public synchronized String getEncryptedPassword(String plaintext, String algorithm)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest md = null;
    md = MessageDigest.getInstance(algorithm);
    md.update(plaintext.getBytes("UTF-8"));

    return bytesToHexString(md.digest());
  }

  /**
   * Utilises the Singleton pattern as there is no need to create separate instances
   */
  public static synchronized PasswordEncryption getInstance() {
    if (instance == null) {
      instance = new PasswordEncryption();
    }
    return instance;
  }

  /**
   * SessionConstants required to perform hex-encoding of a byte array.
   */
  // Checkstyle message: 'HEXARRAY' entspricht nicht dem Muster
  // '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$|logger'.
  // and perhaps change this declaration to begin of class.
  private static final char[] HEXARRAY = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
      'B', 'C', 'D', 'E', 'F'         };

  /**
   * Converts a byte[] array into a Hex string
   * 
   * @param inByteArray
   * @return string
   */
  public static String bytesToHexString(byte[] inByteArray) {
    return bytesToHexString(inByteArray, 0, inByteArray.length);
  }

  /**
   * Converts the given byte array into a hex string representation.
   * 
   * @param inByteArray
   * @param offset
   * @param len
   * @return a hex String representation of the byte array.
   */
  public static String bytesToHexString(byte[] inByteArray, int offset, int len) {
    if (inByteArray == null) {
      return null;
    }
    int position;
    StringBuffer returnBuffer = new StringBuffer();

    for (position = offset; position < len; position++) {
      returnBuffer.append(HEXARRAY[((inByteArray[position] >> 4) & BYTE_MASK)]);
      returnBuffer.append(HEXARRAY[(inByteArray[position] & BYTE_MASK)]);
    }

    return returnBuffer.toString();
  }

}