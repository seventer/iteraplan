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
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.interfaces.StatusEntity;


/**
 * A {@link DimensionAdapter} implementation for handling the {@link de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus} and 
 * {@link de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus} enumerations, used in {@link InformationSystemRelease} and 
 * {@link TechnicalComponentRelease} entities.
 */
public class StatusAdapter extends DimensionAdapter<StatusEntity> {

  private Map<InformationSystemRelease.TypeOfStatus, String>  languageMappingIsr = Maps.newHashMap();
  private Map<TechnicalComponentRelease.TypeOfStatus, String> languageMappingTcr = Maps.newHashMap();

  public StatusAdapter(Locale locale, TypeOfBuildingBlock type) {
    super(locale);
    init(type);
  }

  private void init(TypeOfBuildingBlock type) {
    if (TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.equals(type)) {
      languageMappingIsr = StringEnumReflectionHelper.getLanguageSpecificMappingValues(InformationSystemRelease.TypeOfStatus.class, getLocale());
      super.setValues(StringEnumReflectionHelper.getLanguageSpecificEnumValues(InformationSystemRelease.TypeOfStatus.class, getLocale()));
    }
    else {
      languageMappingTcr = StringEnumReflectionHelper.getLanguageSpecificMappingValues(TechnicalComponentRelease.TypeOfStatus.class, getLocale());
      super.setValues(StringEnumReflectionHelper.getLanguageSpecificEnumValues(TechnicalComponentRelease.TypeOfStatus.class, getLocale()));
    }
  }

  /** {@inheritDoc} */
  @Override
  public String getResultForValue(String value) {
    return value;
  }

  /** {@inheritDoc} */
  @Override
  protected String getResultForObject(StatusEntity statusEntity) {
    if (statusEntity instanceof InformationSystemRelease) {
      InformationSystemRelease.TypeOfStatus status = ((InformationSystemRelease) statusEntity).getTypeOfStatus();
      return this.getLanguageValue(status, null);
    }
    else {
      TechnicalComponentRelease.TypeOfStatus status = ((TechnicalComponentRelease) statusEntity).getTypeOfStatus();
      return this.getLanguageValue(null, status);
    }
  }

  private String getLanguageValue(InformationSystemRelease.TypeOfStatus isStatus, TechnicalComponentRelease.TypeOfStatus tcStatus) {
    if (isStatus != null) {
      return this.languageMappingIsr.get(isStatus);
    }
    else {
      return this.languageMappingTcr.get(tcStatus);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected List<String> getMultipleResultsForObject(StatusEntity informationSystem) {
    return Lists.newArrayList(getResultForObject(informationSystem));
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasUnspecificValue() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return MessageAccess.getStringOrNull(Constants.ATTRIBUTE_TYPEOFSTATUS, getLocale());
  }

}
