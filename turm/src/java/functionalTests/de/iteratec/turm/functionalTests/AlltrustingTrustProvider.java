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
package de.iteratec.turm.functionalTests;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;


public final class AlltrustingTrustProvider extends java.security.Provider {

  private static final long serialVersionUID = -952054781384252475L;

  private final static String NAME = "TrustAll";

  public AlltrustingTrustProvider() {
    super(NAME, 0.17, null);

    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      public Void run() {
        put("TrustManagerFactory." + TrustManagerFactoryImpl.getAlgorithm(),
            TrustManagerFactoryImpl.class.getName());
        return null;
      }
    });
  }

  public static void install() {
    if (Security.getProvider(NAME) == null) {
      Security.insertProviderAt(new AlltrustingTrustProvider(), 2);
      Security.setProperty("ssl.TrustManagerFactory.algorithm", TrustManagerFactoryImpl
          .getAlgorithm());
    }
  }

  public final static class TrustManagerFactoryImpl extends TrustManagerFactorySpi {

    public static String getAlgorithm() {
      return "XTrust509";
    }

    protected void engineInit(KeyStore keystore) throws KeyStoreException {
      // nothing to do
    }

    protected void engineInit(ManagerFactoryParameters mgrparams)
        throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException();
    }

    protected TrustManager[] engineGetTrustManagers() {
      return new TrustManager[] { new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
          // nothing to do
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
          // nothing to do
        }
      } };
    }
  }
}