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

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.RangeValue;
import de.iteratec.iteraplan.model.attribute.TypeOfAttribute;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.AttributeValueAssignmentDAO;
import de.iteratec.iteraplan.persistence.dao.AttributeValueDAO;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.RangeValueDAO;
import de.iteratec.iteraplan.persistence.dao.TimeseriesDAO;


/**
 * Implementation of the service interface {@link AttributeTypeService}.
 */
@Service("attributeTypeService")
@Repository
public class AttributeTypeServiceImpl extends AbstractEntityService<AttributeType, Integer> implements AttributeTypeService {

  private static final Logger         LOGGER = Logger.getIteraplanLogger(AttributeTypeServiceImpl.class);

  private AttributeTypeDAO            attributeTypeDAO;
  private AttributeValueDAO           attributeValueDAO;
  private AttributeValueAssignmentDAO attributeValueAssignmentDAO;
  private RangeValueDAO               rangeValueDAO;
  private DateIntervalService         dateIntervalService;
  private TimeseriesDAO               timeseriesDAO;

  public void assureReadPermission(Set<Integer> ids) {
    // Check permission.
    UserContext.getCurrentPerms().assureAnyFunctionalPermission(
        new TypeOfFunctionalPermission[] { TypeOfFunctionalPermission.TABULAR_REPORTING, TypeOfFunctionalPermission.GRAPHICAL_REPORTING,
            TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION, TypeOfFunctionalPermission.MASSUPDATE });

    for (Integer id : ids) {
      AttributeType at = attributeTypeDAO.loadObjectById(id);
      UserContext.getCurrentPerms().assureAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ);
    }
  }

  @Override
  public void deleteEntity(AttributeType at) {
    LOGGER.debug("Deleting attribute type with ID: {0}", at.getId());

    // Check permission.
    UserContext.getCurrentUserContext().getPerms().assureFunctionalPermission(TypeOfFunctionalPermission.ATTRIBUTETYPE);

    AttributeType reloadedAT = attributeTypeDAO.loadObjectById(at.getId());

    if (reloadedAT != null) {
      deleteRelatedAVAs(reloadedAT.getAllAttributeValues());

      // delete orphan DateInterval
      if (reloadedAT.getTypeOfAttribute().equals(TypeOfAttribute.DATE)) {
        dateIntervalService.deleteDateIntervasByDateAT(reloadedAT.getId());
      }
      timeseriesDAO.deleteTimeseriesByAttributeTypeId(reloadedAT.getId());

      // Using deleteAll instead of a more efficient HQL query because our history functionality
      // wouldn't work otherwise (as Hibernate Envers can't see changes by HQL queries)
      attributeValueDAO.deleteAll(reloadedAT.getAllAttributeValues());

      // Delete the attribute type and associated attribute values (mapped with cascade).
      attributeTypeDAO.delete(reloadedAT);
    }
  }

  private void deleteRelatedAVAs(Collection<? extends AttributeValue> attributeValues) {
    Collection<AttributeValueAssignment> avasToDelete = Sets.newHashSet();
    for (AttributeValue av : attributeValues) {
      avasToDelete.addAll(av.getAttributeValueAssignments());
      av.getAttributeValueAssignments().clear();
      //      av.getAttributeValueAssignments().size();
    }
    for (AttributeValueAssignment ava : avasToDelete) {
      ava.removeReferences();
    }
    // Using deleteAll instead of a more efficient HQL query because our history functionality
    // wouldn't work otherwise (as Hibernate Envers can't see changes by HQL queries)
    attributeValueAssignmentDAO.deleteAll(avasToDelete);

    //    Integer n = attributeValueAssignmentDAO.deleteAssignmentsByAttributeType(at);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Number of deleted attribute value assignments: " + avasToDelete.size());
    }
  }

  public AttributeType getAttributeTypeByName(final String name) {
    return attributeTypeDAO.getAttributeTypeByName(name);
  }

  public List<AttributeType> getAttributeTypesFiltered(List<AttributeType> elementsToExclude) {
    Set<Integer> set = null;
    if (elementsToExclude != null) {
      set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);
    }

    return attributeTypeDAO.loadFilteredElementList("name", set);
  }

  public List<DateAT> getAllDateAT() {
    List<DateAT> dates = new ArrayList<DateAT>();
    List<AttributeType> types = attributeTypeDAO.loadElementList(null);
    for (AttributeType type : types) {
      if (type instanceof DateAT) {
        dates.add((DateAT) type);
      }
    }
    return dates;
  }

  public List<AttributeType> getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock type, boolean enforceReadPermissions) {
    return attributeTypeDAO.getAttributeTypesForTypeOfBuildingBlock(type, enforceReadPermissions);
  }

  public void setAttributeTypeDAO(AttributeTypeDAO attributeTypeDAO) {
    this.attributeTypeDAO = attributeTypeDAO;
  }

  public void setAttributeValueAssignmentDAO(AttributeValueAssignmentDAO attributeValueAssignmentDAO) {
    this.attributeValueAssignmentDAO = attributeValueAssignmentDAO;
  }

  public void setAttributeValueDAO(AttributeValueDAO attributeValueDAO) {
    this.attributeValueDAO = attributeValueDAO;
  }

  public void setRangeValueDAO(RangeValueDAO rangeValueDAO) {
    this.rangeValueDAO = rangeValueDAO;
  }

  public void setDateIntervalService(DateIntervalService dateIntervalService) {
    this.dateIntervalService = dateIntervalService;
  }

  public void setTimeseriesDAO(TimeseriesDAO timeseriesDAO) {
    this.timeseriesDAO = timeseriesDAO;
  }

  public void checkForMultipleAssignments(EnumAT enumAT) {
    if (!enumAT.isMultiassignmenttype() && attributeValueDAO.checkForBuildingBlocksWithMoreThanOneEnumAVs(enumAT.getId())) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.USED_MULTIVALUE_ATTRIBUTE);
    }
  }

  @Override
  protected DAOTemplate<AttributeType, Integer> getDao() {
    return attributeTypeDAO;
  }

  /** {@inheritDoc} */
  public List<AttributeType> getAttributeBySearch(AttributeType attributeType) {
    Preconditions.checkNotNull(attributeType);
    return this.attributeTypeDAO.findBySearchTerm(attributeType.getName(), "name");
  }

  @Override
  public AttributeType saveOrUpdate(AttributeType entity) {
    if (TypeOfAttribute.ENUM == entity.getTypeOfAttribute()) {
      checkForMultipleAssignments((EnumAT) entity);
    }

    return super.saveOrUpdate(entity);
  }

  /** {@inheritDoc} */
  public <AT extends AttributeType> AT loadObjectById(Integer attributeId, Class<AT> clazz) {
    return attributeTypeDAO.loadObjectById(attributeId, clazz);
  }

  /** {@inheritDoc} */
  public List<RangeValue> reloadRangeValues(Collection<RangeValue> identifiers) {
    return rangeValueDAO.reload(identifiers);
  }

  /** {@inheritDoc} */
  public boolean isNumberAT(Integer attributeTypeId) {
    if (attributeTypeId.intValue() > 0) {
      AttributeType at = attributeTypeDAO.loadObjectById(attributeTypeId);
      if (at instanceof NumberAT) {
        return true;
      }
    }
    return false;
  }

}
