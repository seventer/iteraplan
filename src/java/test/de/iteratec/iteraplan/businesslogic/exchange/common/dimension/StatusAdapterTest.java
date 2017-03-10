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

import org.junit.Test;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * JUnit test for the {@link StatusAdapter} class.
 */
public class StatusAdapterTest {

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter#getResultForValue(java.lang.String)}.
   */
  @Test
  public void testGetResultForValueString() {
    StatusAdapter statusAdapter = new StatusAdapter(Locale.ENGLISH, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    assertEquals("test", statusAdapter.getResultForValue("test"));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter#getName()}.
   */
  @Test
  public void testGetName() {
    StatusAdapter statusAdapter = new StatusAdapter(Locale.ENGLISH, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    assertEquals(MessageAccess.getString(Constants.ATTRIBUTE_TYPEOFSTATUS, Locale.ENGLISH), statusAdapter.getName());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter#hasUnspecificValue()}.
   */
  @Test
  public void testHasUnspecificValue() {
    StatusAdapter statusAdapter = new StatusAdapter(Locale.ENGLISH, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    assertFalse(statusAdapter.hasUnspecificValue());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter#StatusAdapter(java.util.Locale, de.iteratec.iteraplan.model.TypeOfBuildingBlock)}.
   */
  @Test
  public void testStatusAdapterIsr() {
    StatusAdapter statusAdapter = new StatusAdapter(Locale.ENGLISH, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

    List<String> statusValues = Lists.newArrayList();
    for (TypeOfStatus typeOfState : TypeOfStatus.values()) {
      statusValues.add(MessageAccess.getString(typeOfState.getValue(), Locale.ENGLISH));
    }

    assertEquals(statusValues, statusAdapter.getValues());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter#StatusAdapter(java.util.Locale, de.iteratec.iteraplan.model.TypeOfBuildingBlock)}.
   */
  @Test
  public void testStatusAdapterTcr() {
    StatusAdapter statusAdapter = new StatusAdapter(Locale.ENGLISH, TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);

    List<String> statusValues = Lists.newArrayList();
    for (TechnicalComponentRelease.TypeOfStatus typeOfState : TechnicalComponentRelease.TypeOfStatus.values()) {
      statusValues.add(MessageAccess.getString(typeOfState.getValue(), Locale.ENGLISH));
    }

    assertEquals(statusValues, statusAdapter.getValues());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter#getResultForObject(de.iteratec.iteraplan.model.interfaces.StatusEntity)}.
   */
  @Test
  public void testGetResultForObjectStatusEntityIsr() {
    StatusAdapter statusAdapter = new StatusAdapter(Locale.ENGLISH, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setTypeOfStatus(TypeOfStatus.PLANNED);

    assertEquals(MessageAccess.getString(TypeOfStatus.PLANNED.getValue(), Locale.ENGLISH), statusAdapter.getResultForObject(isr));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter#getResultForObject(de.iteratec.iteraplan.model.interfaces.StatusEntity)}.
   */
  @Test
  public void testGetResultForObjectStatusEntityTcr() {
    StatusAdapter statusAdapter = new StatusAdapter(Locale.ENGLISH, TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    TechnicalComponentRelease tcr = new TechnicalComponentRelease();
    tcr.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.PLANNED);

    assertEquals(MessageAccess.getString(TechnicalComponentRelease.TypeOfStatus.PLANNED.getValue(), Locale.ENGLISH), statusAdapter.getResultForObject(tcr));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter#getMultipleResultsForObject(de.iteratec.iteraplan.model.interfaces.StatusEntity)}.
   */
  @Test
  public void testGetMultipleResultsForObjectStatusEntityIsr() {
    StatusAdapter statusAdapter = new StatusAdapter(Locale.ENGLISH, TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setTypeOfStatus(TypeOfStatus.PLANNED);

    assertEquals(Lists.newArrayList(MessageAccess.getString(TypeOfStatus.PLANNED.getValue(), Locale.ENGLISH)), statusAdapter.getMultipleResultsForObject(isr));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.StatusAdapter#getMultipleResultsForObject(de.iteratec.iteraplan.model.interfaces.StatusEntity)}.
   */
  @Test
  public void testGetMultipleResultsForObjectStatusEntityTcr() {
    StatusAdapter statusAdapter = new StatusAdapter(Locale.ENGLISH, TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    TechnicalComponentRelease tcr = new TechnicalComponentRelease();
    tcr.setTypeOfStatus(TechnicalComponentRelease.TypeOfStatus.PLANNED);

    assertEquals(Lists.newArrayList(MessageAccess.getString(TechnicalComponentRelease.TypeOfStatus.PLANNED.getValue(), Locale.ENGLISH)),
        statusAdapter.getMultipleResultsForObject(tcr));
  }

}
