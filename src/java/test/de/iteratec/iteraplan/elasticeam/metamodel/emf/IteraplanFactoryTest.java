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
package de.iteratec.iteraplan.elasticeam.metamodel.emf;

import static junit.framework.Assert.assertEquals;

import java.util.Date;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.impl.EcoreFactoryImpl;
import org.junit.Test;

import de.iteratec.elasticeam.MetamodelCreator;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * Tests for the factory class set in {@link Mapping} and used for conversions during xmi
 * serialization and deserialization.
 * {@link Mapping} is the base class for {@link EPackageConverter}'s ExportRun and ImportRun.
 */
public class IteraplanFactoryTest {

  /**
   * Tests the creation of runtime periods from their string representations in xmi files.
   * Related to ITERAPLAN-2052
   */
  @Test
  public void testRuntimePeriodCreateFromString() {
    EFactory factory = getFactoryToTest();
    EDataType runtimeEDataType = createRuntimePeriodDataType();

    Long dateStartLong = Long.valueOf(1388400000000l);
    Long dateEndLong = Long.valueOf(1388444400000l);

    assertRuntimePeriodFromString(factory, runtimeEDataType, dateStartLong, dateEndLong);
    assertRuntimePeriodFromString(factory, runtimeEDataType, null, dateEndLong);
    assertRuntimePeriodFromString(factory, runtimeEDataType, dateStartLong, null);
    assertRuntimePeriodFromString(factory, runtimeEDataType, null, null);
  }

  private void assertRuntimePeriodFromString(EFactory factory, EDataType runtimeEDataType, Long dateStartLong, Long dateEndLong) {
    String dateStartString = "";
    Date dateStart = null;
    String dateEndString = "";
    Date dateEnd = null;

    if (dateStartLong != null) {
      dateStartString = dateStartLong.toString();
      dateStart = new Date(dateStartLong.longValue());
    }
    if (dateEndLong != null) {
      dateEndString = dateEndLong.toString();
      dateEnd = new Date(dateEndLong.longValue());
    }
    Object runtimePeriod = factory.createFromString(runtimeEDataType, dateStartString + "~" + dateEndString);
    assertEquals(new RuntimePeriod(dateStart, dateEnd), runtimePeriod);
  }

  /**
   * Tests the serialization of runtime periods into their string representations for xmi files.
   * Related to ITERAPLAN-2052
   */
  @Test
  public void testRuntimePeriodConvertToString() {
    EFactory factory = getFactoryToTest();
    EDataType runtimeEDataType = createRuntimePeriodDataType();

    Long dateStartLong = Long.valueOf(1388400000000l);
    Long dateEndLong = Long.valueOf(1388444400000l);

    assertRuntimePeriodToString(factory, runtimeEDataType, dateStartLong, dateEndLong);
    assertRuntimePeriodToString(factory, runtimeEDataType, null, dateEndLong);
    assertRuntimePeriodToString(factory, runtimeEDataType, dateStartLong, null);
    assertRuntimePeriodToString(factory, runtimeEDataType, null, null);
  }

  private void assertRuntimePeriodToString(EFactory factory, EDataType runtimeEDataType, Long dateStartLong, Long dateEndLong) {
    String dateStartString = "";
    Date dateStart = null;
    String dateEndString = "";
    Date dateEnd = null;

    if (dateStartLong != null) {
      dateStart = DateUtils.toDateTimeAtStartOfDay(new Date(dateStartLong.longValue())).toDate();
      dateStartString = Long.toString(dateStart.getTime());
    }
    if (dateEndLong != null) {
      dateEnd = DateUtils.toDateTimeAtStartOfDay(new Date(dateEndLong.longValue())).toDate();
      dateEndString = Long.toString(dateEnd.getTime());
    }
    String runtimePeriodString = factory.convertToString(runtimeEDataType, new RuntimePeriod(dateStart, dateEnd));
    assertEquals(dateStartString + "~" + dateEndString, runtimePeriodString);
  }

  /**
   * This method returns the factory used in the metamodel export and import processes
   * to convert to and from emf stuff.
   */
  private EFactory getFactoryToTest() {
    MetamodelCreator creator = new MetamodelCreator();
    Metamodel metamodel = creator.getMetamodel();
    Mapping<Metamodel> mapping = EPackageConverter.deriveMapping(metamodel, false);
    return mapping.getEPackage().getEFactoryInstance();
  }

  /**
   * This method creates a dummy {@link EDataType} representing a runtime period
   */
  private EDataType createRuntimePeriodDataType() {
    EDataType runtimeEDataType = EcoreFactoryImpl.init().createEDataType();
    runtimeEDataType.setInstanceClass(RuntimePeriod.class);
    return runtimeEDataType;
  }

}
