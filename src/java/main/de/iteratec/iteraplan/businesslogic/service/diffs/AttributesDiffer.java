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
package de.iteratec.iteraplan.businesslogic.service.diffs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.ObjectNotFoundException;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.diffs.model.HistoryBBAttributeGroupChangeset;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;

public final class AttributesDiffer {

  // Unfortunate workaround since looking up ATGs from ATs doesn't work in Envers yet
  private static final String                            DUMMY_ATG_NAME                     = "Attributes";
  @SuppressWarnings("boxing")
  private static final Integer                           DUMMY_ATG_ID                       = 1;

  private static final Logger LOGGER = Logger.getIteraplanLogger(AttributesDiffer.class);

  private final HistoryBBChangeset changeset;
  private final BuildingBlock fromBb;
  private final BuildingBlock toBb;

  public static void addAttributeDiffs(HistoryBBChangeset changeset, BuildingBlock fromBb, BuildingBlock toBb) {
    AttributesDiffer differ = new AttributesDiffer(changeset, fromBb, toBb);
    differ.deriveAttributesDiffs();
  }

  private AttributesDiffer(HistoryBBChangeset changeset, BuildingBlock fromBb, BuildingBlock toBb) {
    this.changeset = changeset;
    this.fromBb = fromBb;
    this.toBb = toBb;
  }

  private void deriveAttributesDiffs() {
    LOGGER.debug(" >Attributes");

    // Attributes (Blocker-Issue: Insert/Update=false Issue, see
    // http://community.jboss.org/thread/155216)
    // Effects: Enum, Respon, ATG

    Set<AttributeValueAssignment> toAVAs = toBb.getAttributeValueAssignments();
    Set<AttributeValueAssignment> fromAVAs = fromBb.getAttributeValueAssignments();

    findAttrModificationsAndAdds(fromAVAs, toAVAs);

    findAttrRemovals(fromAVAs, toAVAs);

  }

  private void findAttrModificationsAndAdds(Set<AttributeValueAssignment> fromAVAs, Set<AttributeValueAssignment> toAVAs) {
    //
    // Compare New Attributes to Old: Add, Change
    //
    for (AttributeValueAssignment toAVA : toAVAs) {
      AttributeValue toAttValue = toAVA.getAttributeValue();

      if (toAttValue == null) {
        LOGGER.debug(" Error[New2Old]: AV was null, skipping");
        continue;
      }
      AttributeType toAttType = toAttValue.getAbstractAttributeType();

      if (toAttType == null) {
        LOGGER.debug(" Error[New2Old]: AT was null, skipping");
        continue;
      }

      try {
        LOGGER.debug(" [New2Old]Attribute `{0} {1}`: {2}", toAttType.getName(), toAttType.getId(), toAttValue.getValueString());
        AttributeTypeGroup atGroup = toAttType.getAttributeTypeGroup();

        // Check if this attribute type existed in the last revision
        Collection<AttributeValue> fromAttValues = findAVByATIdInAVAs(toAttType.getId(), fromAVAs);

        // TODO Once looking up the ATG works, use it rather than the DUMMY_ATG data
        Integer atgId = ( atGroup != null ? atGroup.getId() : DUMMY_ATG_ID );
        String atgName = ( atGroup != null ? atGroup.getName() : DUMMY_ATG_NAME );

        if (fromAttValues.isEmpty()) {
          LOGGER.debug("  Added! Not found in previous rev");
          addAttributeChangeEntry(atgId, atgName, toAttType.getId(), toAttType.getName(), "", toAttValue.getValueString());

        } else {
          if (toAttType instanceof EnumAT) {
            compareEnumAVs(fromAttValues, toAttValue, (EnumAT) toAttType, atGroup);

          }
          else if (toAttType instanceof ResponsibilityAT) {
            compareRespAVs(fromAttValues, toAttValue, (ResponsibilityAT) toAttType, atGroup);

          }
          else if (toAttType instanceof NumberAT) {
            compareNumberAVs(fromAttValues, toAttValue, (NumberAT) toAttType, atGroup);
          }
          else {
            AttributeValue fromAttValue = fromAttValues.iterator().next();

            // Changed?
            if (!fromAttValue.getValueString().equals(toAttValue.getValueString())) {

              LOGGER.debug("  Change! From: " + fromAttValue.getValueString() + " to: " + toAttValue.getValueString());
              addAttributeChangeEntry(atgId, atgName, toAttType.getId(), toAttType.getName(), fromAttValue.getValueString(),
                  toAttValue.getValueString());
            }
          }
        }
      } catch (ObjectNotFoundException e) {
        LOGGER.debug("  [New2Old]: AT not found, skipping");
      }
    }
  }

  private void findAttrRemovals(Set<AttributeValueAssignment> fromAVAs, Set<AttributeValueAssignment> toAVAs) {
    //
    // Compare Old Attributes to New: Remove
    //
    for (AttributeValueAssignment fromAVA : fromAVAs) {
      AttributeValue fromAttValue = fromAVA.getAttributeValue();

      if (fromAttValue == null) {
        LOGGER.debug(" Error[Old2New]: AV was null, skipping");
        continue;
      }

      AttributeType fromAttType = fromAttValue.getAbstractAttributeType();

      if (fromAttType == null) {
        LOGGER.debug(" Error[Old2New]: AT was null, skipping");
        continue;
      }
      try {
        LOGGER.debug(" [Old2New]Attribute `{0} {1}: {2}", fromAttType.getName(), fromAttType.getId(), fromAttValue.getValueString());

        // TODO Once looking up the ATG works, use it rather than the DUMMY_ATG data
        AttributeTypeGroup atGroup = fromAttType.getAttributeTypeGroup();

        // Check if this attribute type existed in the last revision
        Collection<AttributeValue> toAttValues = findAVByATIdInAVAs(fromAttType.getId(), toAVAs);
        Integer atgId = ( atGroup != null ? atGroup.getId() : DUMMY_ATG_ID );
        String atgName = ( atGroup != null ? atGroup.getName() : DUMMY_ATG_NAME );
        if (toAttValues.isEmpty()) {
          LOGGER.debug("  Removed! Not found in current rev");
          addAttributeChangeEntry(atgId, atgName, fromAttType.getId(),
              fromAttType.getName(), fromAttValue.getValueString(), "");

        } else {
          if ((fromAttType instanceof EnumAT && ((EnumAT) fromAttType).isMultiassignmenttype())
              || (fromAttType instanceof ResponsibilityAT && ((ResponsibilityAT) fromAttType).isMultiassignmenttype())) {
            boolean found = false;

            for (AttributeValue attValue : toAttValues) {
              // Changed?
              if (attValue.getValueString().equals(fromAttValue.getValueString())) {
                found = true;
                break;
              }
            }

            if (!found) {
              LOGGER.debug("  Removed! Not found in current rev");
              addAttributeChangeEntry(atgId, atgName, fromAttType.getId(),
                  fromAttType.getName(), fromAttValue.getValueString(), "");
            }
          }
        }

      } catch (ObjectNotFoundException e) {
        LOGGER.debug("  [Old2New]: AT not found, skipping");
      }
    }
  }

  private void compareNumberAVs(Collection<AttributeValue> fromAttValues, AttributeValue toAttValue, NumberAT toAttType,
                                AttributeTypeGroup atGroup) {
    // we cannot cast to NumberAV directly, because Envers gives us some proxied AttributeValue objects which cannot be downcasted
    AttributeValue fromAttValue = fromAttValues.iterator().next();

    // therefore we can only cast the method return values according to NumberAV method signatures
    BigDecimal fromValue = (BigDecimal) fromAttValue.getValue();
    String fromValueString = BigDecimalConverter.format(fromValue, true, Locale.ENGLISH);
    BigDecimal toValue = (BigDecimal) toAttValue.getValue();
    String toValueString = BigDecimalConverter.format(toValue, true, Locale.ENGLISH);
    // Changed?
    if (!fromValueString.equals(toValueString)) {
      Integer atgId = ( atGroup != null ? atGroup.getId() : DUMMY_ATG_ID );
      String atgName = ( atGroup != null ? atGroup.getName() : DUMMY_ATG_NAME );

      LOGGER.debug("  Change! From: " + fromAttValue.getValueString() + " to: " + toAttValue.getValueString());
      addAttributeChangeEntry(atgId, atgName, toAttType.getId(), toAttType.getName(), fromAttValue.getValueString(),
          toAttValue.getValueString());
    }
  }

  private void compareRespAVs(Collection<AttributeValue> fromAttValues, AttributeValue toAttValue, ResponsibilityAT toAttType,
                              AttributeTypeGroup atGroup) {
    Integer atgId = ( atGroup != null ? atGroup.getId() : DUMMY_ATG_ID );
    String atgName = ( atGroup != null ? atGroup.getName() : DUMMY_ATG_NAME );
    if (!toAttType.isMultiassignmenttype()) {
      AttributeValue fromAttValue = fromAttValues.iterator().next();

      // Changed?
      if (!fromAttValue.getValueString().equals(toAttValue.getValueString())) {
        LOGGER.debug("  Change! From: " + fromAttValue.getValueString() + " to: " + toAttValue.getValueString());
        addAttributeChangeEntry(atgId, atgName, toAttType.getId(), toAttType.getName(), fromAttValue.getValueString(),
            toAttValue.getValueString());
      }
    }
    else {
      boolean found = false;
      for (AttributeValue attValue : fromAttValues) {
        // Changed?
        if (attValue.getValueString().equals(toAttValue.getValueString())) {
          found = true;
          break;
        }
      }
      if (!found) {
        addAttributeChangeEntry(atgId, atgName, toAttType.getId(), toAttType.getName(), "", toAttValue.getValueString());
      }
    }
  }

  private void compareEnumAVs(Collection<AttributeValue> fromAttValues, AttributeValue toAttValue, EnumAT toAttType, AttributeTypeGroup atGroup) {
    Integer atgId = ( atGroup != null ? atGroup.getId() : DUMMY_ATG_ID );
    String atgName = ( atGroup != null ? atGroup.getName() : DUMMY_ATG_NAME );
    if (!toAttType.isMultiassignmenttype()) {
      AttributeValue fromAttValue = fromAttValues.iterator().next();

      // Changed?
      if (!fromAttValue.getValueString().equals(toAttValue.getValueString())) {
        LOGGER.debug("  Change! From: " + fromAttValue.getValueString() + " to: " + toAttValue.getValueString());
        addAttributeChangeEntry(atgId, atgName, toAttType.getId(), toAttType.getName(), fromAttValue.getValueString(),
            toAttValue.getValueString());
      }
    }
    else {
      boolean found = false;
      for (AttributeValue attValue : fromAttValues) {
        // Changed?
        if (attValue.getValueString().equals(toAttValue.getValueString())) {
          found = true;
          break;
        }
      }
      if (!found) {
        addAttributeChangeEntry(atgId, atgName, toAttType.getId(), toAttType.getName(), "", toAttValue.getValueString());
      }
    }
  }

  /**
   * Returns the AttributeValue by a given AttributeType ID, found by looking through all ATs in the given set of AVAs
   */
  private Collection<AttributeValue> findAVByATIdInAVAs(Integer idAT, Set<AttributeValueAssignment> theAVAs) {
    Collection<AttributeValue> result = new ArrayList<AttributeValue>();

    for (AttributeValueAssignment curAVA : theAVAs) {
      AttributeValue curAttValue = curAVA.getAttributeValue();

      if (curAttValue == null) {
        LOGGER.error(" Error[findAVByATIdInAVAs]: AV was null, skipping");
        continue;
      }

      AttributeType curAttType = curAttValue.getAbstractAttributeType();

      if (curAttType == null) {
        LOGGER.error(" Error[findAVByATIdInAVAs]: AT was null, skipping");
        continue;
      }

      try {
        // Check if it's the AT we are seeking
        if (curAttType.getId().equals(idAT)) {
          result.add(curAttValue);
        }
      } catch (org.hibernate.ObjectNotFoundException e) {
        LOGGER.debug("  [findAVByATIdInAVAs]: AT not found, skipping", e);
      }
    }

    return result;
  }

  /**
   * Adds Attribute change info to appropriate ATGChangeset, creating it if necessary.
   */
  private void addAttributeChangeEntry(Integer atgId, String atgName, Integer atId, String attributeName, String oldValue, String newValue) {
    Map<Integer, HistoryBBAttributeGroupChangeset> attributeChanges = changeset.getAttributeChangesetByAtgMap();
    if (attributeChanges.containsKey(atgId)) {
      attributeChanges.get(atgId).addChangedAttribute(atId, attributeName, oldValue, newValue);
    }
    else {
      HistoryBBAttributeGroupChangeset newATGChangeset = new HistoryBBAttributeGroupChangeset(atgName);
      newATGChangeset.addChangedAttribute(atId, attributeName, oldValue, newValue);
      attributeChanges.put(atgId, newATGChangeset);
    }
  }

}
