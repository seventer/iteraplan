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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.SealState;


/**
 * A {@link DimensionAdapter} implementation for the {@link SealState} enumeration.
 */
public class SealStateAdapter extends DimensionAdapter<InformationSystemRelease> {

  /** Contains the mappings for {@link SealState} and its translated label */
  private final Map<SealState, String> languageMappings;

  /**
   * Creates a new instance of this class. 
   * 
   * @param locale the currently selected locale
   */
  public SealStateAdapter(Locale locale) {
    super(locale);
    languageMappings = StringEnumReflectionHelper.getLanguageSpecificMappingValues(SealState.class, getLocale());
    setValues(StringEnumReflectionHelper.getLanguageSpecificEnumValues(SealState.class, getLocale()));
  }

  /** {@inheritDoc} */
  @Override
  protected String getResultForObject(InformationSystemRelease isr) {
    return languageMappings.get(isr.getSealState());
  }

  /** {@inheritDoc} */
  @Override
  protected List<String> getMultipleResultsForObject(InformationSystemRelease isr) {
    return Lists.newArrayList(getResultForObject(isr));
  }

  /** {@inheritDoc} */
  @Override
  String getResultForValue(String value) {
    return value;
  }

  /** {@inheritDoc} */
  @Override
  String getName() {
    return MessageAccess.getStringOrNull("seal", getLocale());
  }

  /** {@inheritDoc} */
  @Override
  boolean hasUnspecificValue() {
    return false;
  }

}
