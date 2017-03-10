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
package de.iteratec.iteraplan.businesslogic.exchange.common.dimension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Locale;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Seal;
import de.iteratec.iteraplan.model.SealState;


/**
 * JUnit test for the {@link SealStateAdapter} class.
 */
public class SealStateAdapterTest {

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SealStateAdapter#getResultForValue(java.lang.String)}.
   */
  @Test
  public void testGetResultForValueString() {
    SealStateAdapter sealStateAdapter = new SealStateAdapter(Locale.ENGLISH);
    assertEquals("Outdated", sealStateAdapter.getResultForValue("Outdated"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SealStateAdapter#getName()}.
   */
  @Test
  public void testGetName() {
    SealStateAdapter sealStateAdapter = new SealStateAdapter(Locale.ENGLISH);
    assertEquals(MessageAccess.getString("seal", Locale.ENGLISH), sealStateAdapter.getName());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SealStateAdapter#hasUnspecificValue()}.
   */
  @Test
  public void testHasUnspecificValue() {
    SealStateAdapter sealStateAdapter = new SealStateAdapter(Locale.ENGLISH);
    assertFalse(sealStateAdapter.hasUnspecificValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SealStateAdapter#SealStateAdapter(java.util.Locale)}.
   */
  @Test
  public void testSealStateAdapter() {
    SealStateAdapter sealStateAdapter = new SealStateAdapter(Locale.ENGLISH);

    List<String> sealStateValues = Lists.newArrayList();
    SealState[] values = SealState.values();
    for (SealState sealState : values) {
      sealStateValues.add(MessageAccess.getString(sealState.getValue(), Locale.ENGLISH));
    }

    assertEquals(sealStateValues, sealStateAdapter.getValues());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SealStateAdapter#getResultForObject(de.iteratec.iteraplan.model.InformationSystemRelease)}.
   */
  @Test
  public void testGetResultForObjectStateNotAvailable() {
    SealStateAdapter sealStateAdapter = new SealStateAdapter(Locale.ENGLISH);

    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setSealState(SealState.NOT_AVAILABLE);

    assertEquals(MessageAccess.getString("seal.notavailable", Locale.ENGLISH), sealStateAdapter.getResultForObject(isr));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SealStateAdapter#getResultForObject(de.iteratec.iteraplan.model.InformationSystemRelease)}.
   */
  @Test
  public void testGetResultForObjectStateValid() {
    SealStateAdapter sealStateAdapter = new SealStateAdapter(Locale.ENGLISH);

    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setSealState(SealState.VALID);
    Seal seal = new Seal();
    seal.setDate(new LocalDateTime().minusDays(1).toDateTime().toDate());
    isr.addSeal(seal);

    assertEquals(MessageAccess.getString("seal.valid", Locale.ENGLISH), sealStateAdapter.getResultForObject(isr));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.SealStateAdapter#getMultipleResultsForObject(de.iteratec.iteraplan.model.InformationSystemRelease)}.
   */
  @Test
  public void testGetMultipleResultsForObjectInformationSystemRelease() {
    SealStateAdapter sealStateAdapter = new SealStateAdapter(Locale.ENGLISH);

    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setSealState(SealState.VALID);
    Seal seal = new Seal();
    seal.setDate(new LocalDateTime().minusDays(1).toDateTime().toDate());
    isr.addSeal(seal);

    assertEquals(Lists.newArrayList(MessageAccess.getString("seal.valid", Locale.ENGLISH)), sealStateAdapter.getMultipleResultsForObject(isr));
  }

}
