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
package de.iteratec.iteraplan.presentation.dialog.timeseries;

import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.presentation.dialog.common.CanHaveTimeseriesBaseMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.model.BuildingBlockComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.AttributeTypeComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.AttributeTypeGroupComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.EnumAttributeTypeSingleComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.NumberAttributeTypeComponentModelPart;
import de.iteratec.iteraplan.presentation.dialog.common.model.timeseries.TimeseriesAttributeComponentModel;


@Service("timeseriesUpdateFrontendService")
public class TimeseriesUpdateFrontendService {

  private static final Logger LOGGER = Logger.getIteraplanLogger(TimeseriesUpdateFrontendService.class);

  public boolean updateTimeseriesComponentModels(CanHaveTimeseriesBaseMemBean<?, ?> memBean) {
    for (Entry<Integer, TimeseriesAttributeComponentModel> cmEntry : memBean.getTimeseriesComponentModels().entrySet()) {
      cmEntry.getValue().update();
      updateAttributeTypeCm(cmEntry.getKey(), cmEntry.getValue().getLatestEntryValue(), memBean.getComponentModel());
    }
    return true;
  }

  /**
   * Sets the given value to the component model of the attribute type with the given id.
   */
  private void updateAttributeTypeCm(Integer attributeId, String valueToSet, BuildingBlockComponentModel<?> bbCm) {
    AttributeTypeComponentModelPart attributeCm = findComponentModelForAttributeId(attributeId, bbCm);
    String checkedValueToSet = valueToSet == null ? "" : valueToSet;

    if (attributeCm instanceof NumberAttributeTypeComponentModelPart) {
      ((NumberAttributeTypeComponentModelPart) attributeCm).setAttributeValueAsString(checkedValueToSet);
    }
    else if (attributeCm instanceof EnumAttributeTypeSingleComponentModelPart) {
      Integer idToSet = null;
      if (!checkedValueToSet.isEmpty()) {
        idToSet = findEnumAvWithName((EnumAT) attributeCm.getAttributeType(), checkedValueToSet).getId();
      }
      ((EnumAttributeTypeSingleComponentModelPart) attributeCm).setAvIdToSet(idToSet);
    }
  }

  private AttributeTypeComponentModelPart findComponentModelForAttributeId(Integer attributeId, BuildingBlockComponentModel<?> bbCm) {
    for (AttributeTypeGroupComponentModelPart atgCm : bbCm.getAttributeModel().getAtgParts()) {
      for (AttributeTypeComponentModelPart atCm : atgCm.getAtParts()) {
        if (attributeId.equals(atCm.getAttributeType().getId())) {
          return atCm;
        }
      }
    }
    return null;
  }

  private EnumAV findEnumAvWithName(EnumAT enumAT, String valueToSet) {
    for (EnumAV enumAV : enumAT.getAttributeValues()) {
      if (valueToSet.equals(enumAV.getName())) {
        return enumAV;
      }
    }
    LOGGER.error("No enum literal \"{0}\" found in attribute type \"{1}\".", valueToSet, enumAT);
    throw new IteraplanBusinessException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
  }

}
