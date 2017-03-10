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
package de.iteratec.iteraplan.model.ApplicationLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;

import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.Seal;
import de.iteratec.iteraplan.model.SealState;



/**
 * JUnit test for the {@link InformationSystemRelease} class. 
 * Other JUnit tests are implemented in {@link InformationSystemReleaseTest} class.
 */
public class InformationSystemRelease2Test {
  
  private static final String STANDARD_START_DATE_2005    = "1.1.2005";
  private static final String STANDARD_END_DATE_2008      = "31.12.2008";
  private static final String STANDARD_END_DATE_2020      = "31.12.2020";
  private static final String DATE_PATTERN                = "dd.MM.yyyy";
  
  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getTypeOfBuildingBlock()} The test
   * method has only meaning for the code coverage.
   */
  @Test
  public void testGetTypeOfBuildingBlock() {
    assertEquals("informationSystemRelease.singular", new InformationSystemRelease().getTypeOfBuildingBlock().toString());
  }

  @Test
  public void testGetSealState() {
    InformationSystemRelease isr = new InformationSystemRelease();
    assertEquals(SealState.NOT_AVAILABLE, isr.getSealState());
  }
  
  @Test
  public void testGetSealStateOutdated() {
    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setSealState(SealState.VALID);
    
    int expirationInMonths = IteraplanProperties.getIntProperty(IteraplanProperties.SEAL_EXPIRATION_DAYS);
    Seal seal = new Seal();
    seal.setDate(new DateTime().minusMonths(expirationInMonths+1).toDate());
    isr.addSeal(seal);
    
    assertEquals(SealState.OUTDATED, isr.getSealState());
  }
  
  @Test
  public void testGetSealStateValid() {
    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setSealState(SealState.VALID);
    assertEquals(SealState.VALID, isr.getSealState());
  }
  
  @Test
  public void testGetLastSeal() {
    InformationSystemRelease isr = new InformationSystemRelease();
    
    Seal seal1 = createSeal(new DateTime().minusHours(1));
    isr.addSeal(seal1);
    isr.addSeal(createSeal(new DateTime().minusHours(3)));
    isr.addSeal(createSeal(new DateTime().minusHours(2)));
    
    Seal lastSeal = isr.getLastSeal();
    assertNotNull(lastSeal);
    assertEquals(seal1, lastSeal);
  }

  /**
   * Creates the new {@link Seal} instance with the specified date.
   * 
   * @param isr
   * @param minusMonths
   */
  private Seal createSeal(DateTime minusMonths) {
    Seal seal = new Seal();
    seal.setDate(minusMonths.toDate());
    
    return seal;
  }
  
  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeEndsAt()}
   * The method tests if the runtimeEndsAt() returns the correct end date of the period.
   */
  @Test
  public void testRuntimeEndsAtCaseNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    // date
    Date expected = null;
    Date actual = classUnderTest.runtimeEndsAt();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeEndsAt()}
   * The method tests if the runtimeEndsAt() returns the correct end date of the period.
   */
  @Test
  public void testRuntimeEndsAtCaseNotNull() throws ParseException {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2008);
    RuntimePeriod period = new RuntimePeriod(start, end);

    classUnderTest.setRuntimePeriod(period);
    Date actual = classUnderTest.runtimeEndsAt();
    assertEquals(end, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeOverlapsPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeOverlapsPeriod() correctly returns true if no period is set.
   */
  @Test
  public void testRuntimeOverlapsPeriodCaseNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    // dates
    RuntimePeriod period = new RuntimePeriod(null, null);

    boolean actual = classUnderTest.runtimeOverlapsPeriod(period);
    Boolean expected = Boolean.TRUE;
    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeOverlapsPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeOverlapsPeriod() returns boolean value if the overlap of the
   * periods is different as null.
   */
  @Test
  public void testRuntimeOverlapsPeriodFirstCaseNotNull() throws ParseException {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date thisStart = format.parse(STANDARD_START_DATE_2005);
    Date thisEnd = format.parse(STANDARD_END_DATE_2008);
    RuntimePeriod thisPeriod = new RuntimePeriod(thisStart, thisEnd);

    Date otherStart = format.parse("01.01.2008");
    Date otherEnd = format.parse(STANDARD_END_DATE_2020);
    RuntimePeriod otherPeriod = new RuntimePeriod(otherStart, otherEnd);

    classUnderTest.setRuntimePeriod(thisPeriod);

    boolean actual = classUnderTest.runtimeOverlapsPeriod(otherPeriod);
    Boolean expected = Boolean.TRUE;

    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeOverlapsPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeOverlapsPeriod() returns boolean value if the overlap of the
   * periods is different as null.
   */
  @Test
  public void testRuntimeOverlapsPeriodSecCaseNotNull() throws ParseException {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date thisStart = format.parse(STANDARD_START_DATE_2005);
    Date thisEnd = format.parse(STANDARD_END_DATE_2008);
    RuntimePeriod thisPeriod = new RuntimePeriod(thisStart, thisEnd);

    Date otherStart = format.parse("01.01.2009");
    Date otherEnd = format.parse(STANDARD_END_DATE_2020);
    RuntimePeriod otherPeriod = new RuntimePeriod(otherStart, otherEnd);

    classUnderTest.setRuntimePeriod(thisPeriod);

    boolean actual = classUnderTest.runtimeOverlapsPeriod(otherPeriod);
    Boolean expected = Boolean.FALSE;

    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeStartsAt()}
   * The method tests if the runtimeStartsAt() returns the correct start date of the period.
   */
  @Test
  public void testRuntimeStartsAtCaseNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    // date
    Date expected = null;
    Date actual = classUnderTest.runtimeStartsAt();
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeStartsAt()}
   * The method tests if the runtimeStartsAt() returns the correct start date of the period.
   */
  @Test
  public void testRuntimeStartsAtCaseNotNull() throws ParseException {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse(STANDARD_START_DATE_2005);
    Date end = format.parse(STANDARD_END_DATE_2008);
    RuntimePeriod period = new RuntimePeriod(start, end);

    classUnderTest.setRuntimePeriod(period);
    Date actual = classUnderTest.runtimeStartsAt();
    assertEquals(start, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeWithinPeriod() correctly returns true if no period is set.
   */
  @Test
  public void testRuntimeWithinPeriodCaseNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    // dates
    RuntimePeriod period = new RuntimePeriod(null, null);

    boolean actual = classUnderTest.runtimeWithinPeriod(period);
    Boolean expected = Boolean.TRUE;
    assertEquals(expected, Boolean.valueOf(actual));
  }

  private boolean actualTestRuntimeWithinPeriod(String start1, String end1, String start2, String end2) throws ParseException {
    // dates
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date thisStart = format.parse(start1);
    Date thisEnd = format.parse(end1);
    RuntimePeriod thisPeriod = new RuntimePeriod(thisStart, thisEnd);

    Date otherStart = format.parse(start2);
    Date otherEnd = format.parse(end2);
    RuntimePeriod otherPeriod = new RuntimePeriod(otherStart, otherEnd);

    classUnderTest.setRuntimePeriod(thisPeriod);

    return classUnderTest.runtimeWithinPeriod(otherPeriod);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeWithinPeriod() returns boolean value if the period is different
   * as null.
   */
  @Test
  public void testRuntimeWithinPeriodFirstCaseNotNull() throws ParseException {

    boolean actual = actualTestRuntimeWithinPeriod("06.03.2003", "30.10.2004", "06.03.2005", "30.10.2015");
    Boolean expected = Boolean.FALSE;

    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeWithinPeriod() returns boolean value if the period is different
   * as null.
   */
  @Test
  public void testRuntimeWithinPeriodSecCaseNotNull() throws ParseException {

    boolean actual = actualTestRuntimeWithinPeriod(STANDARD_START_DATE_2005, STANDARD_END_DATE_2008, STANDARD_START_DATE_2005, STANDARD_END_DATE_2020);
    Boolean expected = Boolean.TRUE;

    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#runtimeWithinPeriod(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the runtimeWithinPeriod() returns boolean value if the period is different
   * as null.
   */
  @Test
  public void testRuntimeWithinPeriodThirdCaseNotNull() throws ParseException {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date thisStart = format.parse("01.01.2003");
    Date thisEnd = format.parse(STANDARD_END_DATE_2008);
    RuntimePeriod thisPeriod = new RuntimePeriod(thisStart, thisEnd);

    Date otherStart = format.parse(STANDARD_START_DATE_2005);
    Date otherEnd = format.parse(STANDARD_END_DATE_2020);
    RuntimePeriod otherPeriod = new RuntimePeriod(otherStart, otherEnd);

    classUnderTest.setRuntimePeriod(thisPeriod);

    boolean actual = classUnderTest.runtimeWithinPeriod(otherPeriod);
    Boolean expected = Boolean.FALSE;

    assertEquals(expected, Boolean.valueOf(actual));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setRuntimePeriodNullSafe(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the setRuntimePeriodNullSafe(RuntimePeriod period) correctly sets
   * RuntimePeriod.
   */
  @Test
  public void testSetRuntimePeriodNullSafeCaseNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    // dates
    RuntimePeriod period = new RuntimePeriod(null, null);

    classUnderTest.setRuntimePeriodNullSafe(period);
    RuntimePeriod actual = classUnderTest.getRuntimePeriod();
    RuntimePeriod expected = null;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#setRuntimePeriodNullSafe(de.iteratec.iteraplan.model.RuntimePeriod)}
   * The method tests if the setRuntimePeriodNullSafe(RuntimePeriod period) correctly sets
   * RuntimePeriod.
   */
  @Test
  public void testSetRuntimePeriodNullSafeCaseNotNull() throws ParseException {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse("06.03.2005");
    Date end = format.parse("30.10.2015");

    RuntimePeriod expected = new RuntimePeriod(start, end);

    classUnderTest.setRuntimePeriodNullSafe(expected);
    RuntimePeriod actual = classUnderTest.getRuntimePeriod();
    assertEquals(expected, actual);
  }
  
  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getRuntimePeriodNullSafe()} The
   * method has meaning only for the code coverage.
   */
  @Test
  public void testGetRuntimePeriodNullSafeCaseNull() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    // dates
    RuntimePeriod expected = new RuntimePeriod();
    RuntimePeriod actual = classUnderTest.getRuntimePeriodNullSafe();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease#getRuntimePeriodNullSafe()}. The
   * method has meaning only for the code coverage.
   */
  @Test
  public void testGetRuntimePeriodNullSafeCaseNotNull() throws ParseException {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();

    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(DATE_PATTERN);
    Date start = format.parse("06.03.2005");
    Date end = format.parse("30.10.2015");
    RuntimePeriod expected = new RuntimePeriod(start, end);
    classUnderTest.setRuntimePeriodNullSafe(expected);

    RuntimePeriod actual = classUnderTest.getRuntimePeriodNullSafe();
    assertEquals(expected, actual);
  }
  

  /**
   * Test method for {@link de.iteratec.iteraplan.model.InformationSystemRelease#validate()} The
   * method tests if the validate method validates correctly. For that test case the log4jrootLogger
   * must be set on DEBUG, stdout. The flag is in the
   * /iteraplan/WebContent/Web-Inf/classes/log4j.properties
   */
  @Test
  public void testValidateSecondCaseNoException() {
    InformationSystemRelease isr = setUpIsr();
    isr.validate();

    isr.addBusinessMappings(createBusinessMapping("testBU", "testBP", "testP"));
    isr.validate();
  }
  
  private InformationSystemRelease setUpIsr() {
    InformationSystemRelease classUnderTest = new InformationSystemRelease();
    classUnderTest.setId(Integer.valueOf(15));

    InformationSystem is = new InformationSystem();
    is.setName("testInfoSys");
    is.addRelease(classUnderTest);
    classUnderTest.setInformationSystem(is);

    InformationSystemRelease parent = new InformationSystemRelease();
    parent.setId(Integer.valueOf(20));
    classUnderTest.setParent(parent);
    return classUnderTest;
  }

  private Set<BusinessMapping> createBusinessMapping(String buName, String bpName, String pName) {
    Set<BusinessMapping> bmSet = hashSet();

    BusinessUnit bu = null;
    BusinessProcess bp = null;
    Product p = null;

    if (buName != null) {
      bu = new BusinessUnit();
      bu.setName(buName);
      bu.setId(Integer.valueOf(55));
    }

    if (bpName != null) {
      bp = new BusinessProcess();
      bp.setName(bpName);
      bp.setId(Integer.valueOf(65));
    }

    if (pName != null) {
      p = new Product();
      p.setName(pName);
      p.setId(Integer.valueOf(70));
    }

    BusinessMapping first = new BusinessMapping();
    first.setBusinessUnit(bu);
    first.setBusinessProcess(bp);
    first.setProduct(p);
    bmSet.add(first);
    return bmSet;
  }
}
