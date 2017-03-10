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
package de.iteratec.iteraplan.presentation.dialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.presentation.dialog.common.MemBean;


public final class MemBeanSerializationTestHelper {

  private static final Logger LOGGER = Logger.getIteraplanLogger(MemBeanSerializationTestHelper.class);

  /** empty private constructor */
  private MemBeanSerializationTestHelper() {
    // hide constructor
  }

  /**
   * Checks that the provided MemBean is actually serializable, by serializing and deserializing the
   * object
   * 
   * @param <T>
   *          actual type of the memBean
   * @param originalMemBean
   *          the memBean under test
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public static <T> void testSerializeDeserialize(T originalMemBean) {
    T deserializedMembean;
    Class<? extends T> orgMemBeanClass = (Class<? extends T>) originalMemBean.getClass();

    // serialize
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream oos;
    try {
      oos = new ObjectOutputStream(out);
      oos.writeObject(originalMemBean);
      oos.close();
    } catch (IOException e) {
      LOGGER.error(e);
    }

    // deserialize
    InputStream in = new ByteArrayInputStream(out.toByteArray());
    ObjectInputStream ois;
    try {
      ois = new ObjectInputStream(in);
      Object o = ois.readObject();

      assertEquals("Deserialized Object is not of the same class", orgMemBeanClass, o.getClass());

      deserializedMembean = orgMemBeanClass.cast(o);
      assertEquals("Deserialized Object is not of the same class", originalMemBean.getClass(), deserializedMembean.getClass());
    } catch (ClassNotFoundException e) {
      LOGGER.error(e);
    } catch (IOException e) {
      LOGGER.error(e);
    }

  }

  public static void testComponentModelPresence(MemBean<?, ?> memBean) {
    assertNotNull("MemBean must not have a null component model after initialization", memBean.getComponentModel());

  }

}
