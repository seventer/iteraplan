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
package de.iteratec.iteraplan.model.attribute.util;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.collections.BBTLocalizedNameFunction;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;


public final class TimeseriesHelper {

  private static final Set<TypeOfBuildingBlock> INVALID_TOBBS                    = ImmutableSet.of(TypeOfBuildingBlock.BUSINESSMAPPING,
                                                                                     TypeOfBuildingBlock.TCR2IEASSOCIATION,
                                                                                     TypeOfBuildingBlock.ISR2BOASSOCIATION,
                                                                                     TypeOfBuildingBlock.TRANSPORT);

  public static final Predicate<BBAttribute>    BBATTRIBUTE_TIMESERIES_PREDICATE = new Predicate<BBAttribute>() {

                                                                                   public boolean apply(BBAttribute input) {
                                                                                     return input.isTimeseries()
                                                                                         || input.getType().equals(BBAttribute.BLANK_ATTRIBUTE_TYPE);
                                                                                   }
                                                                                 };

  public static final Function<EnumAV, String>  AV_TO_NAME_FUNCTION              = new Function<EnumAV, String>() {
                                                                                   public String apply(EnumAV input) {
                                                                                     return input.getName();
                                                                                   }
                                                                                 };

  private TimeseriesHelper() {
    // private constructor to avoid instantiation
  }

  /**
   * Checks whether the given attribute type has invalid building block types assigned, if it is a timeseries type.
   * If yes, returns a set of the invalidly assigned types.
   * If the given attribute is not a timeseries, always returns an empty set.
   * @param at
   *          Attribute type to check
   * @return Set of the invalidly assigned building block types, empty set if there are none
   */
  public static Set<BuildingBlockType> getInvalidBuildingBlockTypes(AttributeType at) {
    if (!(at instanceof TimeseriesType) || !((TimeseriesType) at).isTimeseries()) {
      return Collections.emptySet();
    }

    Set<BuildingBlockType> invalidTypeNames = Sets.newHashSet();
    for (BuildingBlockType bbt : at.getBuildingBlockTypes()) {
      if (INVALID_TOBBS.contains(bbt.getTypeOfBuildingBlock())) {
        invalidTypeNames.add(bbt);
      }
    }
    return invalidTypeNames;
  }

  /**
   * Checks whether the given attribute type has invalid building block types assigned, if it is a timeseries type.
   * If yes, throws an appropriate {@link IteraplanBusinessException}.
   * Does nothing if the given attribute type is not a timeseries.
   * @param at
   *          Attribute type to check
   */
  public static void validateAssignedBuildingBlockTypes(AttributeType at) {
    Set<BuildingBlockType> invalidTypes = getInvalidBuildingBlockTypes(at);
    if (!invalidTypes.isEmpty()) {
      String invalidTypeNames = "";
      if (invalidTypes.size() == 1) {
        invalidTypeNames = new BBTLocalizedNameFunction().apply(invalidTypes.iterator().next());
      }
      else {
        BuildingBlockType last = invalidTypes.iterator().next();
        Set<BuildingBlockType> rest = Sets.newHashSet(invalidTypes);
        rest.remove(last);
        invalidTypeNames = GeneralHelper.makeConcatenatedNameStringForBbtCollection(rest);
        invalidTypeNames += " " + MessageAccess.getString("global.and") + " " + new BBTLocalizedNameFunction().apply(last);
      }
      throw new IteraplanBusinessException(IteraplanErrorMessages.TIMESERIES_ILLEGAL_ASSOC_BBTS_ASSIGNED, invalidTypeNames);
    }
  }
}
