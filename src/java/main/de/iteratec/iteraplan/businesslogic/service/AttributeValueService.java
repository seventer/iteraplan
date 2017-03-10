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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.Collection;
import java.util.List;

import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;


/**
 * Service interface for {@link AttributeValue}s.
 */
public interface AttributeValueService extends Service {

  /**
   * Returns the sorted list of all AttributeValues for the given AttributeType id.
   * 
   * @param attributeTypeId the id of the attribute type, for which all attribute values should be returned
   * @return List of AttributeValue, sorted according to the comparator defined for the attribute
   *         values
   * @throws de.iteratec.iteraplan.common.error.IteraplanBusinessException if the current user does not have the specified permission on the attribute type group
   */
  List<? extends AttributeValue> getAllAVs(Integer attributeTypeId);

  /**
   * Returns the AV values as a list of Strings for the given attribute type id. The Strings are
   * localized, if the attribute is of type NumberAT.
   * 
   * @param attributeTypeId the id of the attribute type for which the value strings should be returned.
   * @return List of (localized) attribute value string representations.
   * @throws de.iteratec.iteraplan.common.error.IteraplanBusinessException if the current user does not have the specified permission on the attribute type group
   */
  List<String> getAllAVStrings(Integer attributeTypeId);

  /**
   * Returns the AV values as a list of Strings for a given attribute type id and a given list of
   * BuildingBlocks. The Strings are localized, if the attribute is of type NumberAT. The method is
   * analogical to getAllAVStrings with the difference that in this method only the Strings of @param
   * buildingBlocks are returned, and not the Strings of all corresponding elements in the database.
   * 
   * @param attributeTypeId the id of the attribute type for which the value strings should be returned.
   * @param buildingBlocks the list of building blocks
   * @return List of (localized) attribute value string representations.
   * @throws de.iteratec.iteraplan.common.error.IteraplanBusinessException if the current user does not have the specified permission on the attribute type group
   */
  List<String> getAVStringsForBuildingBlocks(Integer attributeTypeId, List<BuildingBlock> buildingBlocks);

  /**
   * Returns a HashBucketMap with BuildingBlock ids as keys and connected AttributeValues as values.
   * Only AttributeValues that belong to the AttributeType identified by the given id are included.
   * 
   * @param buildingBlockIds
   * @param attributeTypeId
   * @return HashBucketMap of Integer, (List of) AttributeValue.
   */
  HashBucketMap<Integer, AttributeValue> getBuildingBlockIdsToConnectedAttributeValues(List<Integer> buildingBlockIds, Integer attributeTypeId);

  /**
   * Deletes orphaned attribute values, i.e. attribute values that are not referenced by an
   * attribute value assignment, of type number, text and date. The remaining attribute types of
   * type enumeration and responsibility do not need to be handled because their values are fixed,
   * i.e. no new values will be created if an assignment to some building block is created.
   * <p>
   * Orphaned attribute value assignments are also removed.
   */
  void removeOrphanedAttributeValuesAndAssignments();

  /**
   * Persists the specified {@code attributeValue}. 
   * 
   * @param attributeValue the attribute value to persist
   */
  AttributeValue saveOrUpdate(AttributeValue attributeValue);

  /**
   * Reloads the specified collection of the attribute values and associates them with 
   * the current Hibernate Session. 
   * 
   * @param <T> the attribute value type
   * @param attributeValues the collection of the attribute values to reload
   * @return the reloaded attribute values
   */
  <T extends AttributeValue> List<T> reload(Collection<T> attributeValues);

  /**
   * Reloads the specified attribute value and associates it with the current Hibernate Session. 
   * If the specified {@code attributeValue} equals {@code null}, this method returns {@code null}.
   * 
   * @param <T> the attribute value type
   * @param attributeValue the attribute value to reload
   * @return the reloaded attribute value or {@code null}
   */
  <T extends AttributeValue> T reload(T attributeValue);

  /**
   * Sets the new attribute value for the specified {@code buildingBlock} and {@code attributeType}. This method must 
   * be used only for the scalar value attributes. Supported attribute types are:
   * <ul>
   * <li> {@link de.iteratec.iteraplan.model.attribute.TextAV}
   * <li> {@link de.iteratec.iteraplan.model.attribute.NumberAV}
   * <li> {@link de.iteratec.iteraplan.model.attribute.DateAV}
   * </ul>
   * 
   * <p>If the {@code newAttributeValue} equals {@code null}, the attribute value will be removed.
   * 
   * @param <T> the attribute value type
   * @param buildingBlock the building block to set the value for
   * @param newAttributeValue the new attribute value to set
   * @param attributeType the attribute type
   */
  <T extends AttributeValue> void setValue(BuildingBlock buildingBlock, T newAttributeValue, AttributeType attributeType);

  /**
   * Sets the new attribute values for the specified {@code buildingBlock} and {@code attributeTypeId}. This method must 
   * be used only for the reference value attributes. Supported attribute types are:
   * <ul>
   * <li> {@link de.iteratec.iteraplan.model.attribute.EnumAV}
   * <li> {@link de.iteratec.iteraplan.model.attribute.ResponsibilityAV}
   * </ul>
   * 
   * <p>If the {@code buildingBlock} contains other attribute values, as specified in {@code newAttributeValues}, those 
   * values will be removed. If the {@code newAttributeValues} will be {@code empty}, all values will be removed.
   * 
   * @param <T> the attribute value type
   * @param buildingBlock the building block to set the value for
   * @param newAttributeValues the new attribute values to set. Should be not {@code null}.
   * @param attributeTypeId the attribute type id
   */
  <T extends AttributeValue> void setReferenceValues(BuildingBlock buildingBlock, Collection<T> newAttributeValues, Integer attributeTypeId);

  /**
   * Loads the {@link AttributeValue} for the specified {@code id} and {@code clazz}.
   * 
   * @param <AV> the {@link AttributeValue} type 
   * @param id the attribute value id
   * @param clazz the attribute value class
   * @return the loaded attribute value or {@code null}, if the {@code id} equals {@code null} or the
   *    entity could not be found
   */
  <AV extends AttributeValue> AV loadObjectById(Integer id, Class<AV> clazz);

  /**
   * Saves or updates the attribute values associated to the specified {@code buildingBlock}.
   * 
   * @param buildingBlock the building block to update attribute values for
   */
  void saveOrUpdateAttributeValues(BuildingBlock buildingBlock);

  /**
   * Deletes the given attribute value after dereferencing the connected
   * attribute value assignments.
   * Should only be used for attribute values of types with an explicitely defined set
   * of values: Responsibility and Enumeration attributes
   * Removing the attribute value from its type has to be done by the caller of this method.
   * @param entity
   *            The {@link AttributeValue} to delete
   */
  void deleteEntity(AttributeValue entity);
}
