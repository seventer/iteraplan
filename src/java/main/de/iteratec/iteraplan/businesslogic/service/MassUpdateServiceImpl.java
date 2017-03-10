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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.common.MassUpdateHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.BusinessMappingCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.InformationSystemReleaseCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttribute;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttributeConfig;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttributeItem;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateLine;


/**
 * A service for updating and deleting multiple Entities at the same time. 
 */
public class MassUpdateServiceImpl implements MassUpdateService {

  private static final Logger                                                       LOGGER       = Logger
                                                                                                     .getIteraplanLogger(MassUpdateServiceImpl.class);

  private AttributeTypeService                                                      attributeTypeService;
  private AttributeValueService                                                     attributeValueService;
  private DateIntervalService                                                       dateIntervalService;

  // BuildingBlock services needed for update
  private Map<TypeOfBuildingBlock, EntityService<? extends BuildingBlock, Integer>> bbtToService = CollectionUtils.hashMap();

  /** {@inheritDoc} */
  public void initAttributes(String selectedAttributeId, List<MassUpdateLine<? extends BuildingBlock>> lines,
                             List<MassUpdateAttributeConfig> massUpdateAttributeConfig, int attributeIndex) {

    MassUpdateHelper massUpdateHelper = new MassUpdateHelper(attributeValueService);

    // init the attribute config (header keys, ...)
    AttributeType attributeType = attributeTypeService.loadObjectById(BBAttribute.getIdByStringId(selectedAttributeId));
    MassUpdateAttributeConfig cfg = getInitializedAttributeConfig(selectedAttributeId, attributeIndex, attributeType);
    massUpdateAttributeConfig.add(cfg);

    // set values for each line
    for (MassUpdateLine<? extends BuildingBlock> line : lines) {
      MassUpdateAttribute attribute = new MassUpdateAttribute(attributeIndex);
      attribute.setType(cfg.getType());
      attribute.setMassUpdateAttributeItem(new MassUpdateAttributeItem(line.getBuildingBlockToUpdate(), cfg.getAttributeTypeId()));

      if (BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE.equals(cfg.getType())) {
        attribute.setAttributeValues(cfg.getAttributeValues());
        massUpdateHelper.updateEnumAvSelectionForMassUpdateItem(attribute.getMassUpdateAttributeItem(), cfg.getAttributeTypeId(), false);
      }
      else if (BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE.equals(cfg.getType())) {
        attribute.setAttributeValues(cfg.getAttributeValues());
        massUpdateHelper.updateResponsibilityAvSelectionForMassUpdateItem(attribute.getMassUpdateAttributeItem(), cfg.getAttributeTypeId(), false);
      }
      else {
        if (BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE.equals(cfg.getType())) {
          attribute.setAttributeValues(new ArrayList<AttributeValue>());
          attribute.setMultilineInput(((TextAT) attributeType).isMultiline());
        }
        else if (BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE.equals(cfg.getType())) {
          attribute.setAttributeValues(new ArrayList<AttributeValue>());
        }
        else if (BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE.equals(cfg.getType())) {
          attribute.setAttributeValues(new ArrayList<AttributeValue>());
        }
        else {
          throw new IllegalArgumentException("Massupdate for " + cfg.getType() + " not implemented!");
        }
        massUpdateHelper.updateNumberOrTextOrDateAvFieldsForMassUpdateItem(attribute.getMassUpdateAttributeItem(), cfg.getAttributeTypeId(), false);
      }
      line.addAttribute(selectedAttributeId, attribute);
    }
  }

  @SuppressWarnings("unchecked")
  private MassUpdateAttributeConfig getInitializedAttributeConfig(String selectedAttributeId, int attributeIndex, AttributeType attributeType) {
    String attributeTypeString = BBAttribute.getTypeByStringId(selectedAttributeId);
    Integer attributeId = BBAttribute.getIdByStringId(selectedAttributeId);

    MassUpdateAttributeConfig cfg = new MassUpdateAttributeConfig();
    cfg.setHeaderKey(attributeType.getName());
    cfg.setPosition(attributeIndex);
    cfg.setType(attributeTypeString);
    cfg.setAttributeTypeId(attributeId);

    // stuff to be done only once
    if (BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE.equals(cfg.getType())) {
      // the attribute is an enum type. get the possible values from the db once
      cfg.setAttributeValues((List<AttributeValue>) attributeValueService.getAllAVs(attributeId));
    }
    if (BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE.equals(cfg.getType())) {
      // the attribute is an responsibility type. get the possible values from the db once
      cfg.setAttributeValues((List<AttributeValue>) attributeValueService.getAllAVs(attributeId));
    }
    if (BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE.equals(cfg.getType())) {
      TextAT at = (TextAT) attributeType;
      if (at.isMultiline()) {
        cfg.setMultiline(true);
      }
    }
    return cfg;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T extends BuildingBlock> T initComponentModel(MassUpdateComponentModel<T> componentModel, T buildingBlock, List<String> properties,
                                                        List<String> associations) {
    EntityService<BuildingBlock, Integer> entityService = getEntityService(buildingBlock);
    T loadedEntity = (T) entityService.loadObjectById(buildingBlock.getId());

    componentModel.initializeFrom(loadedEntity, properties, associations);

    return loadedEntity;
  }

  @SuppressWarnings("unchecked")
  private <T extends BuildingBlock> EntityService<BuildingBlock, Integer> getEntityService(T buildingBlock) {
    TypeOfBuildingBlock tobb = buildingBlock.getTypeOfBuildingBlock();
    return (EntityService<BuildingBlock, Integer>) bbtToService.get(tobb);
  }

  public void setArchitecturalDomainService(ArchitecturalDomainService architecturalDomainService) {
    bbtToService.put(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, architecturalDomainService);
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  public void setAttributeValueService(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  public void setDateIntervalService(DateIntervalService dateIntervalService) {
    this.dateIntervalService = dateIntervalService;
  }

  public void setBusinessFunctionService(BusinessFunctionService businessFunctionService) {
    bbtToService.put(TypeOfBuildingBlock.BUSINESSFUNCTION, businessFunctionService);
  }

  public void setBusinessObjectService(BusinessObjectService businessObjectService) {
    bbtToService.put(TypeOfBuildingBlock.BUSINESSOBJECT, businessObjectService);
  }

  public void setBusinessProcessService(BusinessProcessService businessProcessService) {
    bbtToService.put(TypeOfBuildingBlock.BUSINESSPROCESS, businessProcessService);
  }

  public void setBusinessMappingService(BusinessMappingService businessMappingService) {
    bbtToService.put(TypeOfBuildingBlock.BUSINESSMAPPING, businessMappingService);
  }

  public void setTechnicalComponentReleaseService(TechnicalComponentReleaseService service) {
    bbtToService.put(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, service);
  }

  public void setInfrastructureElementService(InfrastructureElementService service) {
    bbtToService.put(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, service);
  }

  public void setInformationSystemInterfaceService(InformationSystemInterfaceService service) {
    bbtToService.put(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, service);
  }

  public void setBusinessDomainService(BusinessDomainService businessDomainService) {
    bbtToService.put(TypeOfBuildingBlock.BUSINESSDOMAIN, businessDomainService);
  }

  public void setInformationSystemDomainService(InformationSystemDomainService service) {
    bbtToService.put(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, service);
  }

  public void setInformationSystemReleaseService(InformationSystemReleaseService service) {
    bbtToService.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, service);
  }

  public void setProjectService(ProjectService projectService) {
    bbtToService.put(TypeOfBuildingBlock.PROJECT, projectService);
  }

  public void setBusinessUnitService(BusinessUnitService businessUnitService) {
    bbtToService.put(TypeOfBuildingBlock.BUSINESSUNIT, businessUnitService);
  }

  public void setProductService(ProductService productService) {
    bbtToService.put(TypeOfBuildingBlock.PRODUCT, productService);
  }

  /** {@inheritDoc} */
  public <T extends BuildingBlock> boolean updateAttributes(MassUpdateLine<T> line) {
    BuildingBlock buildingBlockToUpdate = line.getBuildingBlockToUpdate();
    EntityService<BuildingBlock, Integer> entityService = getEntityService(buildingBlockToUpdate);
    BuildingBlock buildingBlock = entityService.loadObjectById(buildingBlockToUpdate.getId());
    MassUpdateHelper massUpdateHelper = new MassUpdateHelper(attributeValueService);

    validateDateIntervalPairs(line, buildingBlock);

    boolean updated = false;
    for (MassUpdateAttribute attribute : line.getAttributes()) {
      final MassUpdateAttributeItem massUpdateAttributeItem = attribute.getMassUpdateAttributeItem();
      final Integer attributeId = massUpdateAttributeItem.getAttributeId();

      if (BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE.equals(attribute.getType())) {
        this.executeMassUpdateOnNumberAttributes(buildingBlock, massUpdateAttributeItem);
        // update the dto to convert the number to the correct locale
        massUpdateHelper.updateNumberOrTextOrDateAvFieldsForMassUpdateItem(massUpdateAttributeItem, attributeId, false);
        updated = true;
      }
      else if (BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE.equals(attribute.getType())) {
        this.executeMassUpdateOnTextAttributes(buildingBlock, massUpdateAttributeItem);
        // update the dto to convert the number to the correct locale
        massUpdateHelper.updateNumberOrTextOrDateAvFieldsForMassUpdateItem(massUpdateAttributeItem, attributeId, false);
        updated = true;
      }
      else if (BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE.equals(attribute.getType())) {
        this.executeMassUpdateOnEnumAttributes(buildingBlock, massUpdateAttributeItem);
        // update the dto to convert the number to the correct locale
        massUpdateHelper.updateEnumAvSelectionForMassUpdateItem(massUpdateAttributeItem, attributeId, false);
        updated = true;
      }
      else if (BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE.equals(attribute.getType())) {
        this.executeMassUpdateOnDateAttributes(buildingBlock, massUpdateAttributeItem);
        // update the dto to convert the date to the correct locale
        massUpdateHelper.updateNumberOrTextOrDateAvFieldsForMassUpdateItem(massUpdateAttributeItem, attributeId, false);
        updated = true;
      }
      else if (BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE.equals(attribute.getType())) {
        this.executeMassUpdateOnResponsibilityAttributes(buildingBlock, massUpdateAttributeItem);
        // update the dto
        massUpdateHelper.updateResponsibilityAvSelectionForMassUpdateItem(massUpdateAttributeItem, attributeId, false);
        updated = true;
      }
    }

    line.setBuildingBlockToUpdate(buildingBlock);
    return updated;
  }

  /**
   * Validates a whole line in the massupdate, to check if a whole Date Interval is modified
   * does not write any changes
   * @param line
   */
  private <T extends BuildingBlock> void validateDateIntervalPairs(MassUpdateLine<T> line, BuildingBlock buildingBlock) {

    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.MASSUPDATE);
    //Integer in map is the Id for an DateAT
    Map<Integer, MassUpdateAttributeItem> massUpdateAttributes = new HashMap<Integer, MassUpdateAttributeItem>();
    for (MassUpdateAttribute attribute : line.getAttributes()) {
      if (BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE.equals(attribute.getType())) {
        massUpdateAttributes.put(attributeTypeService.loadObjectById(attribute.getMassUpdateAttributeItem().getAttributeId(), DateAT.class).getId(),
            attribute.getMassUpdateAttributeItem());
      }
    }
    if (massUpdateAttributes.isEmpty()) {
      return;
    }
    //get all the date intervals that contain the given dateATs
    Set<DateInterval> dateIntervals = dateIntervalService.findDateIntervalsByDateATs(massUpdateAttributes.keySet());

    //For every found DateInterval that contains at least one DateAT check if the DateInterval is valid
    for (DateInterval dateInterval : dateIntervals) {
      /**
      * A complete "DateInterval" (two DateAVs that define a part of a DateInterval for a BB) is going to be updated, check if the
      * startDate is equal to / or before the enddate
      * 
      */
      if (massUpdateAttributes.get(dateInterval.getStartDate().getId()) != null && massUpdateAttributes.get(dateInterval.getEndDate().getId()) != null) {
        String startDate = massUpdateAttributes.get(dateInterval.getStartDate().getId()).getNewAttributeValue();
        String endDate = massUpdateAttributes.get(dateInterval.getEndDate().getId()).getNewAttributeValue();
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
          Date newStartDate = DateUtils.parseAsDate(startDate, UserContext.getCurrentLocale());
          Date newEndDate = DateUtils.parseAsDate(endDate, UserContext.getCurrentLocale());
          if (newStartDate != null && newEndDate != null && newStartDate.after(newEndDate)) {
            throw new IteraplanBusinessException(IteraplanErrorMessages.DATE_INTERVAL_END_DATE_BEFORE_START_DATE);
          }
        }
      }
      /**
       * A single date of a DateInterval is going to be updated
       */
      else {
        String date;
        Date newDate;
        if (massUpdateAttributes.get(dateInterval.getStartDate().getId()).getNewAttributeValue() != null) {
          date = massUpdateAttributes.get(dateInterval.getStartDate().getId()).getNewAttributeValue();
          newDate = DateUtils.parseAsDate(date, UserContext.getCurrentLocale());
          if (((DateAV) buildingBlock.getAttributeTypeToAttributeValues().getBucketNotNull(dateInterval.getEndDate()).iterator().next()).getValue().before(
              newDate)) {
            throw new IteraplanBusinessException(IteraplanErrorMessages.DATE_INTERVAL_START_DATE_BEFORE_END_DATE);
          }
        }
        else {
          date = massUpdateAttributes.get(dateInterval.getEndDate().getId()).getNewAttributeValue();
          newDate = DateUtils.parseAsDate(date, UserContext.getCurrentLocale());
          if (((DateAV) buildingBlock.getAttributeTypeToAttributeValues().getBucketNotNull(dateInterval.getEndDate()).iterator().next()).getValue().after(
              newDate)) {
            throw new IteraplanBusinessException(IteraplanErrorMessages.DATE_INTERVAL_END_DATE_BEFORE_START_DATE);
          }
        }
      }
    }
  }

  /** {@inheritDoc} */
  public <T extends BuildingBlock> void updateBusinessMappingLine(MassUpdateLine<T> line) {
    final InformationSystemRelease isRelease = (InformationSystemRelease) line.getBuildingBlockToUpdate();
    final InformationSystemReleaseService isrService = (InformationSystemReleaseService) bbtToService
        .get(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    final InformationSystemRelease dbInstance = isrService.merge(isRelease);

    int numberOfChanges = 0;
    InformationSystemReleaseCmMu isReleaseComponentModel = (InformationSystemReleaseCmMu) line.getComponentModel();
    Set<Integer> hashCodes = new HashSet<Integer>();

    for (BusinessMappingCmMu componentModel : isReleaseComponentModel.getBusinessMappingComponentModels()) {
      BusinessMapping dbBizMapping = dbInstance.getBusinessMapping(componentModel.getEncapsulatedBusinessMappings().getId());
      boolean changes = checkForChanges(componentModel, dbBizMapping);

      // check for duplicates
      Integer hash = componentModel.getCustomHashCode();
      if (hashCodes.contains(hash)) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_ENTRY_BM);
      }
      hashCodes.add(hash);

      if (changes) {
        saveChanges(componentModel, dbBizMapping);
        numberOfChanges++;
      }
    }

    line.setBuildingBlockToUpdate(dbInstance);
    if (numberOfChanges > 0) {
      line.getMassUpdateResult().setWasExecuted(true);
    }
    else {
      line.getMassUpdateResult().setWasExecuted(false);
    }
  }

  private boolean checkForChanges(BusinessMappingCmMu componentModel, BusinessMapping dbBizMapping) {
    boolean changes = false;
    if (!dbBizMapping.getBusinessProcess().getId().equals(componentModel.getSelectedBusinessProcessId())) {
      BusinessProcessService businessProcessService = (BusinessProcessService) bbtToService.get(TypeOfBuildingBlock.BUSINESSPROCESS);
      dbBizMapping.setBusinessProcess(businessProcessService.loadObjectById(componentModel.getSelectedBusinessProcessId()));
      changes = true;
    }
    if (!dbBizMapping.getBusinessUnit().getId().equals(componentModel.getSelectedBusinessUnitId())) {
      BusinessUnitService businessUnitService = (BusinessUnitService) bbtToService.get(TypeOfBuildingBlock.BUSINESSUNIT);
      dbBizMapping.setBusinessUnit(businessUnitService.loadObjectById(componentModel.getSelectedBusinessUnitId()));
      changes = true;
    }
    if (!dbBizMapping.getProduct().getId().equals(componentModel.getSelectedProductId())) {
      ProductService productService = (ProductService) bbtToService.get(TypeOfBuildingBlock.PRODUCT);
      dbBizMapping.setProduct(productService.loadObjectById(componentModel.getSelectedProductId()));
      changes = true;
    }
    return changes;
  }

  private void saveChanges(BusinessMappingCmMu componentModel, BusinessMapping dbBizMapping) {
    if (LOGGER.isDebugEnabled()) {
      StringBuilder sb = new StringBuilder();
      sb.append("\nMassupdating business mapping:\n ");
      sb.append("   old BP ID: ");
      sb.append(componentModel.getEncapsulatedBusinessMappings().getBusinessProcess().getId());
      sb.append("\n");
      sb.append("   new BP ID: ").append(dbBizMapping.getBusinessProcess().getId()).append("\n");
      sb.append("   old BU ID: ");
      sb.append(componentModel.getEncapsulatedBusinessMappings().getBusinessUnit().getId());
      sb.append("\n");
      sb.append("   new BU ID: ").append(dbBizMapping.getBusinessUnit().getId()).append("\n");
      sb.append("   old PR ID: ");
      sb.append(componentModel.getEncapsulatedBusinessMappings().getProduct().getId());
      sb.append("\n");
      sb.append("   new PR ID: ").append(dbBizMapping.getProduct().getId());
      sb.append("\n");
      LOGGER.debug(sb.toString());
    }

    final InformationSystemReleaseService isrService = (InformationSystemReleaseService) bbtToService
        .get(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    isrService.saveOrUpdate(dbBizMapping.getInformationSystemRelease());
    componentModel.setEncapsulatedBusinessMapping(dbBizMapping);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T extends BuildingBlock> void updateLine(MassUpdateLine<T> line) {
    T buildingBlock = line.getBuildingBlockToUpdate();

    EntityService<BuildingBlock, Integer> entityService = getEntityService(buildingBlock);
    final T mergedBuildingBlock = (T) entityService.merge(buildingBlock);

    UserContext.getCurrentPerms().assureBbUpdatePermission(mergedBuildingBlock);

    line.getComponentModel().configure(mergedBuildingBlock);
    BuildingBlock updatedBuildingBlock = updateBuildingBlock(mergedBuildingBlock);
    line.setBuildingBlockToUpdate(updatedBuildingBlock);
  }

  /** {@inheritDoc} */
  public void deleteBuildingBlock(BuildingBlock buildingBlock) {
    TypeOfBuildingBlock tobb = buildingBlock.getTypeOfBuildingBlock();
    if (!bbtToService.containsKey(tobb)) {
      LOGGER.error("Could not delete building block " + buildingBlock);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    @SuppressWarnings("unchecked")
    EntityService<BuildingBlock, Integer> service = (EntityService<BuildingBlock, Integer>) bbtToService.get(tobb);

    //eagerly loaded in order to avoid LazyInitException when executing bulk delete
    BuildingBlock loadedBB = service.loadObjectByIdIfExists(buildingBlock.getId());

    if (loadedBB != null) {
      service.deleteEntity(loadedBB);
    }
  }

  /** {@inheritDoc} */
  public void subscribeBuildingBlock(BuildingBlock buildingBlock, boolean subscribe) {
    TypeOfBuildingBlock tobb = buildingBlock.getTypeOfBuildingBlock();
    if (!bbtToService.containsKey(tobb) || TypeOfBuildingBlock.BUSINESSMAPPING.equals(tobb)) {
      LOGGER.error("Could not subscribe building block " + buildingBlock);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    SubscribeService service = (SubscribeService) bbtToService.get(tobb);
    service.subscribe(buildingBlock.getId(), subscribe);
  }

  /**
   * Performs an update of a {@link DateAV}.
   *
   * @param buildingBlock The {@link BuildingBlock} the attribute value is assigned to.
   * @param dto The data transfer object for mass updates on attribute types.
   */
  private void executeMassUpdateOnDateAttributes(BuildingBlock buildingBlock, MassUpdateAttributeItem dto) {
    LOGGER.debug("Performing mass update on date attribute value.");
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.MASSUPDATE);

    DateAT at = attributeTypeService.loadObjectById(dto.getAttributeId(), DateAT.class);
    UserContext.getCurrentPerms().assureAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ_WRITE);

    // Add new attribute value.
    String newValue = dto.getNewAttributeValue();
    if (StringUtils.isNotEmpty(newValue)) {
      DateAV av = new DateAV();
      Date newDate = DateUtils.parseAsDate(newValue, UserContext.getCurrentLocale());
      if (newDate != null) {
        av.setValue(newDate);
        av.setAttributeType(at); // Setting this side of the association suffices.
      }

      attributeValueService.setValue(buildingBlock, av, at);
    }
    else {
      attributeValueService.setValue(buildingBlock, null, at);
    }

    attributeValueService.saveOrUpdateAttributeValues(buildingBlock);
    EntityService<BuildingBlock, Integer> entityService = getEntityService(buildingBlock);
    entityService.saveOrUpdate(buildingBlock);
  }

  /**
   * Performs an update of a {@link EnumAV}.
   *
   * @param buildingBlock
   *          The {@link BuildingBlock} the attribute value is assigned to.
   * @param dto
   *          The data transfer object for mass updates on attribute types.
   */
  private void executeMassUpdateOnEnumAttributes(BuildingBlock buildingBlock, MassUpdateAttributeItem dto) {
    LOGGER.debug("Performing mass update on enumeration attribute value.");
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.MASSUPDATE);

    EnumAT at = attributeTypeService.loadObjectById(dto.getAttributeId(), EnumAT.class);
    List<Integer> selectedIDs = dto.getSelectedAttributeValueIds();

    // Check permission for the attribute type group.
    UserContext.getCurrentPerms().assureAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ_WRITE);

    // Check if only single values are updated for single valued attribute types.
    if (!at.isMultiassignmenttype() && (selectedIDs.size() >= 2)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ILLEGAL_MULTIVALUE_ATTRIBUTE_ASSIGNMENT);
    }

    List<AttributeValue> avs = Lists.newArrayList();
    for (Integer id : selectedIDs) {
      EnumAV av = attributeValueService.loadObjectById(id, EnumAV.class);
      avs.add(av);
    }

    attributeValueService.setReferenceValues(buildingBlock, avs, at.getId());
    attributeValueService.saveOrUpdateAttributeValues(buildingBlock);
    EntityService<BuildingBlock, Integer> entityService = getEntityService(buildingBlock);
    entityService.saveOrUpdate(buildingBlock);
  }

  /**
   * Performs an update of a {@link NumberAV}.
   *
   * @param buildingBlock
   *          The {@link BuildingBlock} the attribute value is assigned to.
   * @param dto
   *          The data transfer object for mass updates on attribute types.
   */
  private void executeMassUpdateOnNumberAttributes(BuildingBlock buildingBlock, MassUpdateAttributeItem dto) {
    LOGGER.debug("Performing mass update on text attribute value.");
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.MASSUPDATE);

    NumberAT at = attributeTypeService.loadObjectById(dto.getAttributeId(), NumberAT.class);

    // Check permission for the attribute type group.
    UserContext.getCurrentPerms().assureAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ_WRITE);

    // Add new attribute value.
    String newValue = dto.getNewAttributeValue();
    if (StringUtils.isNotEmpty(newValue)) {
      NumberAV av = new NumberAV();
      BigDecimal newNumber = BigDecimalConverter.parse(newValue, true, UserContext.getCurrentLocale());

      av.setValue(newNumber);
      av.setAttributeType(at); // Setting this side of the association suffices.

      attributeValueService.setValue(buildingBlock, av, at);
    }
    else {
      attributeValueService.setValue(buildingBlock, null, at);
    }

    attributeValueService.saveOrUpdateAttributeValues(buildingBlock);
    EntityService<BuildingBlock, Integer> entityService = getEntityService(buildingBlock);
    entityService.saveOrUpdate(buildingBlock);
  }


  /**
   * Performs an update of a {@link de.iteratec.iteraplan.model.attribute.ResponsibilityAV}.
   *
   * @param buildingBlock
   *          The {@link BuildingBlock} the text attribute value is assigned to.
   * @param dto
   *          The data transfer object for mass updates on attribute types.
   */
  private void executeMassUpdateOnResponsibilityAttributes(BuildingBlock buildingBlock, MassUpdateAttributeItem dto) {
    LOGGER.debug("Performing mass update on enumeration attribute value.");
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.MASSUPDATE);

    ResponsibilityAT at = attributeTypeService.loadObjectById(dto.getAttributeId(), ResponsibilityAT.class);
    List<Integer> selectedIDs = dto.getSelectedAttributeValueIds();

    // Check permission for the attribute type group.
    UserContext.getCurrentPerms().assureAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ_WRITE);

    // Check if only single values are updated for single valued attribute types.

    if (!at.isMultiassignmenttype() && (selectedIDs.size() >= 2)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ILLEGAL_MULTIVALUE_ATTRIBUTE_ASSIGNMENT);
    }

    List<AttributeValue> avs = Lists.newArrayList();
    for (Integer id : selectedIDs) {
      ResponsibilityAV av = attributeValueService.loadObjectById(id, ResponsibilityAV.class);
      avs.add(av);
    }

    attributeValueService.setReferenceValues(buildingBlock, avs, at.getId());
    attributeValueService.saveOrUpdateAttributeValues(buildingBlock);
    EntityService<BuildingBlock, Integer> entityService = getEntityService(buildingBlock);
    entityService.saveOrUpdate(buildingBlock);
  }

  /**
   * Performs an update of a {@link TextAV}.
   *
   * @param buildingBlock
   *          The {@link BuildingBlock} the text attribute value is assigned to.
   * @param dto
   *          The data transfer object for mass updates on attribute types.
   */
  private void executeMassUpdateOnTextAttributes(BuildingBlock buildingBlock, MassUpdateAttributeItem dto) {
    LOGGER.debug("Performing mass update on text attribute value.");
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.MASSUPDATE);

    TextAT at = attributeTypeService.loadObjectById(dto.getAttributeId(), TextAT.class);

    // Check permission for the attribute type group.
    UserContext.getCurrentPerms().assureAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ_WRITE);

    // Add new attribute value.
    String newValue = dto.getNewAttributeValue();
    if (StringUtils.isNotEmpty(newValue)) {
      TextAV av = new TextAV();
      av.setValue(newValue);
      av.setAttributeType(at); // Setting this side of the association suffices.

      attributeValueService.setValue(buildingBlock, av, at);
    }
    else {
      attributeValueService.setValue(buildingBlock, null, at);
    }

    attributeValueService.saveOrUpdateAttributeValues(buildingBlock);
    EntityService<BuildingBlock, Integer> entityService = getEntityService(buildingBlock);
    entityService.saveOrUpdate(buildingBlock);
  }

  /**
   * Calls the update method of the service responsible for managing the buildingBlock instance that
   * is passed in as parameter. IMPORTANT: This updates Properties and Associations, but ignores the
   * attributes.
   *
   * @param buildingBlock
   *          The {@link BuildingBlock} to update. Represents a dbInstance that has been updated by
   *          some component model during the mass update
   * @return The updated and persisted entity
   */
  @SuppressWarnings("unchecked")
  private BuildingBlock updateBuildingBlock(BuildingBlock buildingBlock) {
    TypeOfBuildingBlock tobb = buildingBlock.getTypeOfBuildingBlock();

    if (!bbtToService.containsKey(tobb)) {
      throw new IllegalArgumentException("MassUpdateLine for generic BuildingBlock not available. MassUpdateLine for " + buildingBlock.getClass()
          + " has to be configured in MassUpdateServiceImpl.class");
    }

    EntityService<BuildingBlock, Integer> service = (EntityService<BuildingBlock, Integer>) bbtToService.get(tobb);
    return service.saveOrUpdate(buildingBlock);
  }
}
