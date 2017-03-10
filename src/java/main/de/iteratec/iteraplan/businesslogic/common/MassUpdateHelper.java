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
package de.iteratec.iteraplan.businesslogic.common;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttributeItem;


/**
 * Helper class for bulk updates (mass updates).
 */
public class MassUpdateHelper {

  private static final Logger         LOGGER = Logger.getIteraplanLogger(MassUpdateHelper.class);

  private final AttributeValueService attributeValueService;

  public MassUpdateHelper(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  /**
   * Helper method. Returns a Map with BuildingBlock ids as keys and Lists of AttributeValues as
   * values.
   * 
   * @param massUpdateItem
   *          The MassUpdateAttributeItem holding the
   *          {@link de.iteratec.iteraplan.model.BuildingBlock} to get the values for
   * @param attributeTypeId
   *          id of the AttributeType for which the connected AttributeValues should be returned.
   * @param executeMode
   *          Iff set to true and mass update was not successful for the current
   *          MassUpdateAttributeItem, the MassUpdateAttributeItem will be skipped.
   * @return HashBucketMap (Integer, ArrayList of AttributeValue).
   */
  private HashBucketMap<Integer, AttributeValue> getBuildingBlockIdToAttributeValue(MassUpdateAttributeItem massUpdateItem, Integer attributeTypeId,
                                                                                    boolean executeMode) {
    if (executeMode && !massUpdateItem.isWasSuccessful()) {
      return new HashBucketMap<Integer, AttributeValue>();
    }

    List<Integer> id = new ArrayList<Integer>();
    id.add(massUpdateItem.getBuildingBlockToUpdate().getId());

    HashBucketMap<Integer, AttributeValue> bbIdToAVs = attributeValueService.getBuildingBlockIdsToConnectedAttributeValues(id, attributeTypeId);

    return bbIdToAVs;

  }

  private String[] getAvIdArray(List<AttributeValue> avs) {

    String[] avIdArray = new String[avs.size()];
    List<String> attributeValueStringIds = new ArrayList<String>();

    // set the selected values on the dto
    for (AttributeValue av : avs) {
      attributeValueStringIds.add(av.getId().toString());
    }

    return attributeValueStringIds.toArray(avIdArray);
  }

  /**
   * Updates a given enum massUpdateItem for enum types. Retrieves the selected enum values and
   * stores them in massUpdateItem.
   * 
   * @param massUpdateItem
   *          The dto
   * @param attributeTypeId
   *          The current attributetype ID
   * @param executeMode
   *          The execute mode
   */
  public void updateEnumAvSelectionForMassUpdateItem(MassUpdateAttributeItem massUpdateItem, Integer attributeTypeId, boolean executeMode) {
    // a HashBucketMap consisting of <BuildingBlockID, all possible enum values>
    HashBucketMap<Integer, AttributeValue> bbIdToAVs = getBuildingBlockIdToAttributeValue(massUpdateItem, attributeTypeId, executeMode);

    if (executeMode && !massUpdateItem.isWasSuccessful()) {
      return;
    }
    // get the enum values from the bucket
    List<AttributeValue> avs = bbIdToAVs.getBucketNotNull(massUpdateItem.getBuildingBlockToUpdate().getId());

    massUpdateItem.setSelectedAttributeValueStringIds(getAvIdArray(avs));
    massUpdateItem.setNewAttributeValue(null);
  }

  /**
   * Updates a given massUpdateItem for text and number types. Retrieves the value of the attribute
   * and stores it in the massUpdateItem
   * 
   * @param massUpdateItem
   *          The massUpdateItem
   * @param attributeTypeId
   *          The attributeTypeId for which the value shall be held in the massUpdateItem
   * @param executeMode
   *          The execute Mode
   */
  public void updateNumberOrTextOrDateAvFieldsForMassUpdateItem(MassUpdateAttributeItem massUpdateItem, Integer attributeTypeId, boolean executeMode) {

    // get a map <BuildingBlockId, associtated AttributeValue for given attributeType>
    HashBucketMap<Integer, AttributeValue> bbIdToAVs = getBuildingBlockIdToAttributeValue(massUpdateItem, attributeTypeId, executeMode);

    if (executeMode && !massUpdateItem.isWasSuccessful()) {
      return;
    }
    // get the attribute values from the Bucket (in this case it will be at most one as we're not
    // dealing with
    // an enum)
    List<AttributeValue> avs = bbIdToAVs.getBucketNotNull(massUpdateItem.getBuildingBlockToUpdate().getId());
    if (avs.size() > 1) {
      LOGGER.error("Found more than one assigned AttributeValue for the AttributeType with id '" + attributeTypeId
          + "'. A maximum of one AttributeValue is logically allowed.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    else if (avs.size() == 1) {
      AttributeValue av = avs.get(0);
      // set the localized number value of the attribute
      massUpdateItem.setNewAttributeValue(av.getLocalizedValueString(UserContext.getCurrentLocale()));
    }
    else {
      massUpdateItem.setNewAttributeValue(null);
    }
  }

  /**
   * Updates a given responsibility massUpdateItem for responsibility types. Retrieves the selected
   * responsibility values and stores them in massUpdateItem.
   * 
   * @param massUpdateItem
   *          The dto
   * @param attributeTypeId
   *          The current attributetype ID
   * @param executeMode
   *          The execute mode
   */
  public void updateResponsibilityAvSelectionForMassUpdateItem(MassUpdateAttributeItem massUpdateItem, Integer attributeTypeId, boolean executeMode) {

    // a HashBucketMap consisting of <BuildingBlockID, all possible responsibility values>
    HashBucketMap<Integer, AttributeValue> bbIdToAVs = getBuildingBlockIdToAttributeValue(massUpdateItem, attributeTypeId, executeMode);

    if (executeMode && !massUpdateItem.isWasSuccessful()) {
      return;
    }
    // get the responsibility values from the bucket
    List<AttributeValue> avs = bbIdToAVs.getBucketNotNull(massUpdateItem.getBuildingBlockToUpdate().getId());

    massUpdateItem.setSelectedAttributeValueStringIds(getAvIdArray(avs));
    massUpdateItem.setNewAttributeValue(null);

  }

}
