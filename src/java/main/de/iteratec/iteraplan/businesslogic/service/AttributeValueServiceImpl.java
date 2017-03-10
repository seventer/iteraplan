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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.common.collections.EntityToIdFunction;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.attribute.TypeOfAttribute;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.AttributeValueDAO;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;


/**
 * Implementation of the service interface {@link AttributeValueService}.
 */
public class AttributeValueServiceImpl extends AbstractService implements AttributeValueService {

  private static final Logger     LOGGER = Logger.getIteraplanLogger(AttributeValueServiceImpl.class);

  private AttributeValueDAO       attributeValueDAO;
  private AttributeTypeDAO        attributeTypeDAO;
  private GeneralBuildingBlockDAO generalBuildingBlockDAO;

  /** {@inheritDoc} */
  public List<? extends AttributeValue> getAllAVs(Integer attributeTypeId) {
    AttributeType at = attributeTypeDAO.loadObjectById(attributeTypeId);
    // if the chosen attribute type is not visible to the current user, return no values. This
    // should never happen.
    Permissions perms = UserContext.getCurrentUserContext().getPerms();
    perms.assureAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ);
    if (at instanceof EnumAT) {
      return ((EnumAT) at).getSortedAttributeValues();
    }
    else if (at instanceof NumberAT) {
      List<NumberAV> values = Lists.newArrayList(((NumberAT) at).getAttributeValues());
      TypeOfAttribute.NUMBER.sort(values);
      return values;
    }
    else if (at instanceof TextAT) {
      List<TextAV> values = Lists.newArrayList(((TextAT) at).getAttributeValues());
      TypeOfAttribute.TEXT.sort(values);
      return values;
    }
    else if (at instanceof DateAT) {
      List<DateAV> values = Lists.newArrayList(((DateAT) at).getAttributeValues());
      TypeOfAttribute.DATE.sort(values);
      return values;
    }
    else if (at instanceof ResponsibilityAT) {
      return ((ResponsibilityAT) at).getSortedAttributeValues();
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  /** {@inheritDoc} */
  public List<String> getAllAVStrings(Integer attributeTypeId) {
    LOGGER.debug("getAllAVStrings() trying to get value Strings for AttributeType with id {1}", attributeTypeId);
    UserContext.getCurrentPerms().assureAnyFunctionalPermission(
        new TypeOfFunctionalPermission[] { TypeOfFunctionalPermission.TABULAR_REPORTING, TypeOfFunctionalPermission.GRAPHICAL_REPORTING,
            TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION, TypeOfFunctionalPermission.MASSUPDATE, TypeOfFunctionalPermission.DASHBOARD });

    AttributeType at = attributeTypeDAO.loadObjectById(attributeTypeId);
    // if the chosen attribute type is not visible to the current user, return no values. This
    // should never happen.
    Permissions perms = UserContext.getCurrentUserContext().getPerms();
    perms.assureAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ);

    if (at instanceof EnumAT) {
      return getAllEnumAVStrings(attributeTypeId);
    }
    else if (at instanceof NumberAT) {
      return getAllNumberAVStrings(attributeTypeId);
    }
    else if (at instanceof TextAT) {
      return getAllTextAVStrings(attributeTypeId);
    }
    else if (at instanceof DateAT) {
      return getAllDateAVStrings(attributeTypeId);
    }
    else if (at instanceof ResponsibilityAT) {
      return getAllResponsibilityAVStrings(attributeTypeId);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  /**
   * Returns all DateAV values as a List of Strings for the given attribute type id. The list is
   * free of duplicates.
   * 
   * @param attributeTypeId
   *          The id of the attribute type, for which all date attribute value strings should be
   *          returned.
   * @return List of sorted date attribute values in their string representation. No duplicates.
   */
  private List<String> getAllDateAVStrings(Integer attributeTypeId) {
    DateAT dateAt = attributeTypeDAO.loadObjectById(attributeTypeId, DateAT.class);
    List<DateAV> values = Lists.newArrayList(dateAt.getAttributeValues());
    TypeOfAttribute.DATE.sort(values);
    Set<String> avValues = new TreeSet<String>();

    for (DateAV dav : values) {
      avValues.add(dav.getLocalizedValueString(UserContext.getCurrentLocale()));
    }

    return new ArrayList<String>(avValues);
  }

  /**
   * Returns all EnumAV names as a List of Strings for the given attribute type id. The list is free
   * of duplicates.
   * 
   * @param attributeTypeId
   *          The id of the attribute type, for which all enum attribute value strings should be
   *          returned.
   * @return List of sorted enum attribute values in their string representation.
   */
  private List<String> getAllEnumAVStrings(Integer attributeTypeId) {
    EnumAT enumAT = attributeTypeDAO.loadObjectById(attributeTypeId, EnumAT.class);
    List<EnumAV> values = enumAT.getSortedAttributeValues();
    List<String> avValues = new ArrayList<String>();

    for (EnumAV eav : values) {
      avValues.add(eav.getValueString());
    }

    return avValues;
  }

  /**
   * Returns all NumberAV values as a List of localized Strings for the given attribute type id. The
   * list is free of duplicates.
   * 
   * @param attributeTypeId
   *          The id of the attribute type, for which all number attribute value strings should be
   *          returned.
   * @return List of sorted localized number attribute values in their string representation,
   *         according to the locale of the current user. No duplicates.
   */
  private List<String> getAllNumberAVStrings(Integer attributeTypeId) {
    Set<String> avValues = new TreeSet<String>();
    NumberAT numberAT = attributeTypeDAO.loadObjectById(attributeTypeId, NumberAT.class);
    List<NumberAV> values = Lists.newArrayList(numberAT.getAttributeValues());
    TypeOfAttribute.NUMBER.sort(values);

    for (NumberAV nav : values) {
      avValues.add(nav.getLocalizedValueString(UserContext.getCurrentLocale()));
    }

    return new ArrayList<String>(avValues);
  }

  private List<String> getAllResponsibilityAVStrings(Integer attributeTypeId) {
    List<String> avValues = new ArrayList<String>();
    ResponsibilityAT respAT = attributeTypeDAO.loadObjectById(attributeTypeId, ResponsibilityAT.class);
    List<ResponsibilityAV> values = respAT.getSortedAttributeValues();

    for (ResponsibilityAV eav : values) {
      avValues.add(eav.getValueString());
    }

    return avValues;
  }

  /**
   * Returns all TextAV values as a List of Strings for the given attribute type id. The list is
   * free of duplicates.
   * 
   * @param attributeTypeId
   *          The id of the attribute type, for which all text attribute value strings should be
   *          returned.
   * @return List of sorted text attribute values in their string representation. No duplicates.
   */
  private List<String> getAllTextAVStrings(Integer attributeTypeId) {
    Set<String> avValues = new TreeSet<String>();
    TextAT textAT = attributeTypeDAO.loadObjectById(attributeTypeId, TextAT.class);
    List<TextAV> values = Lists.newArrayList(textAT.getAttributeValues());
    TypeOfAttribute.TEXT.sort(values);
    for (TextAV tav : values) {
      avValues.add(tav.getValueString());
    }
    return new ArrayList<String>(avValues);
  }

  /** {@inheritDoc} */
  public List<String> getAVStringsForBuildingBlocks(Integer attributeTypeId, List<BuildingBlock> buildingBlocks) {
    LOGGER.debug("getAllAVStrings() trying to get value Strings for AttributeType with id " + attributeTypeId.intValue());
    UserContext.getCurrentPerms().assureAnyFunctionalPermission(new TypeOfFunctionalPermission[] { TypeOfFunctionalPermission.GRAPHICAL_REPORTING });

    AttributeType at = attributeTypeDAO.loadObjectById(attributeTypeId);
    // if the chosen attribute type is not visible to the current user, return no values. This should never happen.
    Permissions perms = UserContext.getCurrentUserContext().getPerms();
    perms.assureAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ);

    // create a list of the AttributeValues of the buildingBlocks
    List<AttributeValue> allBlocksValues = Lists.newArrayList();
    for (BuildingBlock buildingBlock : buildingBlocks) {
      for (AttributeValueAssignment ava : buildingBlock.getAssignmentsForId(attributeTypeId)) {
        allBlocksValues.add(ava.getAttributeValue());
      }
    }

    // perform conversion from AttributeType to concrete types:
    if (at instanceof NumberAT) {
      TypeOfAttribute.NUMBER.filterAndSort(allBlocksValues);
    }
    else if (at instanceof TextAT) {
      TypeOfAttribute.TEXT.sort(allBlocksValues);
    }
    else if (at instanceof DateAT) {
      TypeOfAttribute.DATE.sort(allBlocksValues);
    }
    return getAttributeStringValues(allBlocksValues);
  }

  /**
   * Returns the String-List of the values for the AttributeType. The Strings are Localized.
   * 
   * @param attributeValues
   * @return List of the (sorted) string values of the given building blocks
   */
  private List<String> getAttributeStringValues(List<AttributeValue> attributeValues) {
    Set<String> values = Sets.newLinkedHashSet();
    for (AttributeValue av : attributeValues) {
      values.add(av.getLocalizedValueString(UserContext.getCurrentLocale()));
    }

    return Lists.newArrayList(values);
  }

  /**
   * TODO agu I have the feeling, that we don't need this method
   */
  public HashBucketMap<Integer, AttributeValue> getBuildingBlockIdsToConnectedAttributeValues(List<Integer> buildingBlockIds, Integer id) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.MASSUPDATE);

    HashBucketMap<Integer, AttributeValue> map = new HashBucketMap<Integer, AttributeValue>();
    AttributeType at = attributeTypeDAO.loadObjectById(id);
    for (Integer bbId : buildingBlockIds) {
      BuildingBlock bb = generalBuildingBlockDAO.loadObjectById(bbId);
      map.addAll(bbId, bb.getConnectedAttributeValues(at));
    }
    return map;
  }

  /** {@inheritDoc} */
  public void removeOrphanedAttributeValuesAndAssignments() {
    // Remove orphaned attribute values.
    Integer deletedDateAttributeValues = attributeValueDAO.deleteOrphanedDateAttributeValues();
    Integer deletedNumberAttributeValues = attributeValueDAO.deleteOrphanedNumberAttributeValues();
    Integer deletedTextAttributeValues = attributeValueDAO.deleteOrphanedTextAttributeValues();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Number of deleted attribute values per type: Date " + deletedDateAttributeValues + " - Number " + deletedNumberAttributeValues
          + " - Text " + deletedTextAttributeValues);
    }
  }

  /** {@inheritDoc} */
  public AttributeValue saveOrUpdate(AttributeValue attributeValue) {
    return attributeValueDAO.saveOrUpdate(attributeValue);
  }

  public void setAttributeTypeDAO(AttributeTypeDAO attributeTypeDAO) {
    this.attributeTypeDAO = attributeTypeDAO;
  }

  public void setAttributeValueDAO(AttributeValueDAO attributeValueDAO) {
    this.attributeValueDAO = attributeValueDAO;
  }

  public void setGeneralBuildingBlockDAO(GeneralBuildingBlockDAO generalBuildingBlockDAO) {
    this.generalBuildingBlockDAO = generalBuildingBlockDAO;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T extends AttributeValue> List<T> reload(Collection<T> attributeValues) {
    if ((attributeValues == null) || attributeValues.isEmpty()) {
      return Lists.newArrayList();
    }

    EntityToIdFunction<T, Integer> toIdFunction = new EntityToIdFunction<T, Integer>();
    Iterable<Integer> transform = Iterables.transform(attributeValues, toIdFunction);

    List<AttributeValue> loadElementListWithIds = attributeValueDAO.loadElementListWithIds(Sets.newHashSet(transform));
    List<T> result = Lists.newArrayList();
    for (AttributeValue attributeValue : loadElementListWithIds) {
      result.add((T) attributeValue);
    }

    return result;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T extends AttributeValue> T reload(T attributeValue) {
    if (attributeValue == null) {
      return null;
    }

    return (T) attributeValueDAO.loadObjectById(attributeValue.getId(), attributeValue.getClass());
  }

  /** {@inheritDoc} */
  public <T extends AttributeValue> void setReferenceValues(BuildingBlock target, Collection<T> newAttributeValues, Integer attributeTypeId) {
    //reload to associate with Hibernate session
    List<T> reloadedAV = Lists.newArrayList();
    for (T t : newAttributeValues) {
      T reload = reload(t);
      if (reload != null) {
        reloadedAV.add(reload);
      }
    }

    Set<AttributeValueAssignment> assignedAvasToRemove = Sets.newHashSet();
    Set<AttributeValueAssignment> assignedAvas = target.getAssignmentsForId(attributeTypeId);
    for (AttributeValueAssignment ava : assignedAvas) {
      if (!reloadedAV.contains(ava.getAttributeValue())) {
        assignedAvasToRemove.add(ava);
      }
    }
    target.removeAttributeValueAssignments(assignedAvasToRemove);

    for (T attributeValue : reloadedAV) {
      AttributeValueAssignment ava = findOrCreateAva(target, attributeValue, attributeTypeId);
      ava.addReferences(attributeValue, target);
    }
  }

  /** {@inheritDoc} */
  public <T extends AttributeValue> void setValue(BuildingBlock target, T newAttributeValue, AttributeType attributeType) {
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(attributeType);

    if (newAttributeValue != null) {
      AttributeValueAssignment ava = findOrCreateAva(target, attributeType.getId());

      if (ava.getId() == null) {
        addAttributeType(newAttributeValue, attributeType);
        ava.addReferences(newAttributeValue, target);
      }
      else {
        if (newAttributeValue instanceof NumberAV) {
          NumberAV av = (NumberAV) ava.getAttributeValue();
          NumberAV newAv = (NumberAV) newAttributeValue;
          av.setValue(newAv.getValue());
        }
        else if (newAttributeValue instanceof DateAV) {
          DateAV av = (DateAV) ava.getAttributeValue();
          DateAV newAv = (DateAV) newAttributeValue;
          av.setValue(newAv.getValue());
        }
        else if (newAttributeValue instanceof TextAV) {
          TextAV av = (TextAV) ava.getAttributeValue();
          TextAV newAv = (TextAV) newAttributeValue;
          av.setValue(newAv.getValue());
        }
        else {
          throw new IllegalArgumentException(String.format("The attribute value '%s' is not supported", newAttributeValue));
        }
      }
    }
    else {
      target.removeAttributeValueAssignmentsForAttributeType(attributeType.getId());
    }
  }

  private <T extends AttributeValue> void addAttributeType(T newAttributeValue, AttributeType attributeType) {
    if (newAttributeValue.getAbstractAttributeType() != null) {
      return;
    }

    if (newAttributeValue instanceof NumberAV) {
      ((NumberAV) newAttributeValue).setAttributeTypeTwoWay((NumberAT) attributeType);
    }
    else if (newAttributeValue instanceof DateAV) {
      ((DateAV) newAttributeValue).setAttributeTypeTwoWay((DateAT) attributeType);
    }
    else if (newAttributeValue instanceof TextAV) {
      ((TextAV) newAttributeValue).setAttributeTypeTwoWay((TextAT) attributeType);
    }
    else {
      throw new IllegalArgumentException(String.format("The attribute value '%s' is not supported", newAttributeValue));
    }
  }

  private AttributeValueAssignment findOrCreateAva(BuildingBlock buildingBlock, Integer attributeTypeId) {
    AttributeValueAssignment ava = buildingBlock.getAssignmentForId(attributeTypeId);
    return ava != null ? ava : new AttributeValueAssignment();
  }

  private AttributeValueAssignment findOrCreateAva(BuildingBlock buildingBlock, AttributeValue attributeValue, Integer attributeTypeId) {
    AttributeValueAssignment ava = buildingBlock.getAssignmentForId(attributeTypeId, attributeValue);
    return ava != null ? ava : new AttributeValueAssignment();
  }

  /** {@inheritDoc} */
  public <AV extends AttributeValue> AV loadObjectById(Integer attributeId, Class<AV> clazz) {
    return attributeValueDAO.loadObjectById(attributeId, clazz);
  }

  /** {@inheritDoc} */
  public void saveOrUpdateAttributeValues(BuildingBlock buildingBlock) {
    Collection<List<AttributeValue>> values = buildingBlock.getAttributeTypeToAttributeValues().values();

    for (List<AttributeValue> attributeValueList : values) {
      attributeValueDAO.saveOrUpdate(attributeValueList);
    }
  }

  /**{@inheritDoc}**/
  public void deleteEntity(AttributeValue entity) {
    Set<AttributeValueAssignment> attributeValueAssignments = ImmutableSet.copyOf(entity.getAttributeValueAssignments());
    for (AttributeValueAssignment ava : attributeValueAssignments) {
      ava.removeReferences();
    }
    attributeValueDAO.delete(entity);
  }
}
