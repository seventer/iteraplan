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
package de.iteratec.iteraplan.businesslogic.service.legacyExcel;

import static de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ImportWorkbook.getProcessingLog;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.ExcelConstants;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.BuildingBlockHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.CellValueHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ExcelImportUtilities;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData.BuildingBlockAttributes;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData.BuildingBlockRelations;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeDataWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ObjectRelatedPermissionsData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ObjectRelatedPermissionsWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog.Level;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BusinessMappingService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TransportInfo;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.TypeOfAttribute;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.user.User;


/**
 * Allows importing specially formatted Excel files (workbooks) into the database
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveImports" })
public class ExcelImportServiceImpl implements ExcelImportService {

  private static final Logger               LOGGER                = Logger.getIteraplanLogger(ExcelImportServiceImpl.class);
  private static final Logger               FRONTEND_LOG          = Logger.getIteraplanLogger("ExcelImportProcessingLog");
  /** List of all single-value attribute types: Date, Number and Text */
  private static final Set<TypeOfAttribute> SINGLE_VALUE_ATS      = Sets.newEnumSet(Sets.newHashSet(TypeOfAttribute.DATE, TypeOfAttribute.NUMBER,
                                                                      TypeOfAttribute.TEXT), TypeOfAttribute.class);

  private final Pattern                     businessObjectPattern = Pattern.compile("(<->|->|<-|-)\\s(.+)");

  private AttributeTypeService              attributeTypeService;
  private AttributeValueService             attributeValueService;

  private UserService                       userService;
  private BuildingBlockServiceLocator       buildingBlockServiceLocator;

  /** {@inheritDoc} */
  public LandscapeData readLandscapeData(InputStream is, PrintWriter logStream) {
    ProcessingLog userLog = new ProcessingLog(getLogLevel(), logStream);
    LandscapeDataWorkbook importer = new LandscapeDataWorkbook(userLog);

    try {
      LandscapeData landscapeData = importer.doImport(is);

      return landscapeData;
    } catch (Exception e) {
      getProcessingLog().error("Import failed with exception: ", e);
      LandscapeDataWorkbook.removeProcessingLog();
      // rethrow as IteraplanException to cancel the transaction and redirect to an appropriate error display page
      throw new IteraplanBusinessException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  /** {@inheritDoc} */
  public void importLandscapeData(LandscapeData landscapeData) {
    try {
      Map<String, AttributeType> allAttributeTypes = loadAllAttributeTypes();

      importContentToDaos(landscapeData.getBuildingBlocks());
      importAttributes(landscapeData.getAttributes(), allAttributeTypes);
      importRelations(landscapeData.getRelations(), landscapeData.getLocale(), allAttributeTypes);
      getProcessingLog().info("Import completed. The changed are now being commited to the database...");
    } catch (Exception e) {
      getProcessingLog().error("Import failed with exception: ", e);
      // rethrow as IteraplanException to cancel the transaction and redirect to an appropriate error display page
      throw new IteraplanBusinessException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } finally {
      LandscapeDataWorkbook.removeProcessingLog();
    }
  }

  /** {@inheritDoc} */
  public void importObjectRelatedPermissions(InputStream is, PrintWriter logWriter) {
    ProcessingLog userLog = new ProcessingLog(getLogLevel(), logWriter);
    ObjectRelatedPermissionsWorkbook importer = new ObjectRelatedPermissionsWorkbook(userLog);

    try {
      List<ObjectRelatedPermissionsData> permissionsData = importer.doImport(is);

      userLog.info("Saving object related permissions");
      for (ObjectRelatedPermissionsData objectUserPermissions : permissionsData) {
        CellValueHolder id = objectUserPermissions.getId();
        CellValueHolder name = objectUserPermissions.getName();
        TypeOfBuildingBlock type = objectUserPermissions.getTypeOfBuildingBlock();

        Integer parseId = parseId(id);
        String parseName = name.getAttributeValue();
        BuildingBlock bb = getDbCopy(parseId, parseName, type);
        if (bb != null) {
          LOGGER.debug("BB: {0} Type: {1}", bb.getNonHierarchicalName(), type);

          if (StringUtils.equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME, bb.getNonHierarchicalName())) {
            userLog.warn("[{0}] The building block {1} with ID {2} is virtual. It is not allowed to assign object "
                + "related permissions for such elements", id.getCellRef(), bb.getNonHierarchicalName(), bb.getId());
            continue;
          }

          Set<User> users = this.loadOrCreateUsers(objectUserPermissions.getUsers());
          if (users != null) {
            bb.addOwningUserEntities(users);
            getServiceFor(bb.getTypeOfBuildingBlock()).saveOrUpdate(bb);
            String bbType = MessageAccess.getString(type.toString());
            userLog.info("[{0}] The object related permissions for building block {1} of type {2} for users {3} were saved", id.getCellRef(),
                bb.getNonHierarchicalName(), bbType, users);
          }
        }
        else {
          String bbType = MessageAccess.getString(type.toString());
          userLog
              .warn("[{0}] The building block with ID {1} and type {2} was not found in database", id.getCellRef(), id.getAttributeValue(), bbType);
        }
      }
    } catch (Exception e) {
      userLog.error("Import failed with exception: ", e);
      // rethrow as IteraplanException to cancel the transaction and redirect to an appropriate error display page
      throw new IteraplanBusinessException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } finally {
      LandscapeDataWorkbook.removeProcessingLog();
    }
  }

  private Map<String, AttributeType> loadAllAttributeTypes() {
    List<AttributeType> allAttributeTypes = attributeTypeService.loadElementList();
    Map<String, AttributeType> attributesMap = Maps.newHashMap();
    for (AttributeType attributeType : allAttributeTypes) {
      attributesMap.put(attributeType.getName(), attributeType);
    }

    return attributesMap;
  }

  void importContentToDaos(Collection<BuildingBlockHolder> buildingBlocks) {
    LOGGER.debug("Saving building blocks to DB");

    for (BuildingBlockHolder buildingBlockHolder : buildingBlocks) {
      BuildingBlock buildingBlock = buildingBlockHolder.getBuildingBlock();
      String buildingBlockCellCoords = ExcelImportUtilities.getCellRef(buildingBlockHolder.getBuildingBlockCell());
      LOGGER.debug("Cell [{0}] BB: {1} Type: {2}", buildingBlockCellCoords, buildingBlock.getNonHierarchicalName(),
          buildingBlock.getTypeOfBuildingBlock());

      if (isRelationBuildingBlock(buildingBlockHolder)) {
        continue;
      }

      try {
        saveBuildingBlock(buildingBlockHolder);
      } catch (IteraplanBusinessException e) {
        String bbType = MessageAccess.getString(buildingBlock.getTypeOfBuildingBlock().toString());
        LOGGER.warn("Saving building block " + buildingBlock.getNonHierarchicalName() + " from cell [" + 0 + "] failed", e);
        getProcessingLog().warn("Cell [{0}]  Building block {1} of type {2} could not be saved, row {3} is ignored. Error was: {4}",
            buildingBlockCellCoords, buildingBlock.getNonHierarchicalName(), bbType,
            ExcelImportUtilities.getCellRow(buildingBlockHolder.getBuildingBlockCell()), e.getMessage());
      }
    }
  }

  protected void saveBuildingBlock(BuildingBlockHolder buildingBlockHolder) {
    BuildingBlock buildingBlock = buildingBlockHolder.getBuildingBlock();
    if (buildingBlock instanceof InformationSystemRelease) {
      saveIsr(buildingBlockHolder);
      return;
    }
    else if (buildingBlock instanceof TechnicalComponentRelease) {
      saveTcr(buildingBlockHolder);
      return;
    }
    // else: it's a non-release building block and we can process it generically

    TypeOfBuildingBlock typeOfBuildingBlock = buildingBlock.getTypeOfBuildingBlock();
    BuildingBlockService<BuildingBlock, Integer> service = getServiceFor(typeOfBuildingBlock);

    if (service == null) {
      LOGGER.error("Can't save Building Block from cell [{0}]: Unknown Building Block Type, skipping",
          ExcelImportUtilities.getCellRef(buildingBlockHolder.getBuildingBlockCell()));
      return;
    }

    addParentToHierarchicalEntity(buildingBlock, service);
    BuildingBlock savedEntity = service.saveOrUpdate(buildingBlock);
    // store the potentially modified DB ID into the building block object for later reference
    buildingBlock.setId(savedEntity.getId());
  }

  /**
   * Adds the parent to new hierarchical entities if required. The parent will be the root element of the specified building
   * block.
   * 
   * @param buildingBlock the building block to add the parent for
   * @param service the building block service for getting the root element
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void addParentToHierarchicalEntity(BuildingBlock buildingBlock, BuildingBlockService<BuildingBlock, Integer> service) {
    if (buildingBlock instanceof AbstractHierarchicalEntity<?>) {
      AbstractHierarchicalEntity hEntity = (AbstractHierarchicalEntity) buildingBlock;
      HierarchicalEntity<?> parent = hEntity.getParent();

      if (parent == null && !hEntity.isTopLevelElement()) {
        BuildingBlock root = service.getFirstElement();
        hEntity.addParent((HierarchicalEntity) root);
      }
    }
  }

  /**
   * Returns {@code true} if the specified {@code buildingBlock} is a relation building block.
   * The relation building blocks will be imported in {@link #importRelations(Collection, Locale)}.
   * 
   * @param buildingBlock the building block to check
   * @return {@code true} if the specified {@code buildingBlock} is a relation building block
   */
  private boolean isRelationBuildingBlock(BuildingBlockHolder buildingBlock) {
    if (buildingBlock.getBuildingBlock() instanceof BusinessMapping || buildingBlock.getBuildingBlock() instanceof InformationSystemInterface) {
      LOGGER
          .debug("Programming error: There should be no BM/ISI in the ImportContent/CoreAttributes, since they can't be created without their relations");

      return true;
    }

    return false;
  }

  private void importRelations(Collection<BuildingBlockRelations> buildingBlockRelSet, Locale locale, Map<String, AttributeType> allAttributeTypes) {
    for (BuildingBlockRelations buildingBlockRel : buildingBlockRelSet) {
      Map<String, CellValueHolder> entries = buildingBlockRel.getContent();
      importSubscribedUsers(buildingBlockRel.getBuildingBlock(), entries, locale);

      BuildingBlock buildingBlock = handleSpecialRelations(locale, buildingBlockRel, allAttributeTypes);
      if (buildingBlock == null) {
        continue;
      }

      // All non-ISI/BMs can have their names printed in this way
      getProcessingLog().info("Saving relations from row {0} for: {1}", buildingBlockRel.getRowNum(), buildingBlock.getNonHierarchicalName());

      try {
        saveRelations(locale, buildingBlockRel, buildingBlock, allAttributeTypes);
      } catch (IteraplanBusinessException e) {
        String bbType = MessageAccess.getString(buildingBlock.getTypeOfBuildingBlock().toString());
        LOGGER.warn("saving relations for building block " + buildingBlock.getNonHierarchicalName() + " from row " + buildingBlockRel.getRowNum()
            + " failed", e);
        getProcessingLog().warn("Row {0}  Relations for building block {1} of type {2} could not be saved, this row. Error was: {4}",
            buildingBlockRel.getRowNum(), buildingBlock.getNonHierarchicalName(), bbType, e.getMessage());
      }
    }
  }

  private void saveRelations(Locale locale, BuildingBlockRelations buildingBlockRel, BuildingBlock buildingBlock,
                             Map<String, AttributeType> allAttributeTypes) {
    Map<String, CellValueHolder> entries = buildingBlockRel.getContent();

    if (buildingBlock instanceof ArchitecturalDomain) {
      createBuildingBlockRelations((ArchitecturalDomain) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof BusinessDomain) {
      createBuildingBlockRelations((BusinessDomain) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof BusinessFunction) {
      createBuildingBlockRelations((BusinessFunction) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof BusinessObject) {
      createBuildingBlockRelations((BusinessObject) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof BusinessProcess) {
      createBuildingBlockRelations((BusinessProcess) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof BusinessUnit) {
      createBuildingBlockRelations((BusinessUnit) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof InformationSystemDomain) {
      createBuildingBlockRelations((InformationSystemDomain) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof InformationSystemRelease) {
      createBuildingBlockRelations((InformationSystemRelease) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof InformationSystemInterface) {
      Map<String, CellValueHolder> attributes = buildingBlockRel.getAttributes();
      createBuildingBlockRelationsISI((InformationSystemInterface) buildingBlock, entries, attributes, locale, allAttributeTypes);
    }
    else if (buildingBlock instanceof InfrastructureElement) {
      createBuildingBlockRelations((InfrastructureElement) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof Product) {
      createBuildingBlockRelations((Product) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof Project) {
      createBuildingBlockRelations((Project) buildingBlock, entries, locale);
    }
    else if (buildingBlock instanceof TechnicalComponentRelease) {
      createBuildingBlockRelations((TechnicalComponentRelease) buildingBlock, entries, locale);
    }
    else {
      LOGGER.error("Can't update BB relations: Unknown BBT {0}, skipping", buildingBlock.getBuildingBlockType());
    }
  }

  /**
   * Imports the subscribed users for the specified {@code buildingBlock}.
   * 
   * @param buildingBlock the building block to subcribe users for
   * @param relations the relation field values containing field names associated with the value
   * @param locale the import locale
   */
  private void importSubscribedUsers(BuildingBlock buildingBlock, Map<String, CellValueHolder> relations, Locale locale) {
    CellValueHolder subscribedUserCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.SUBSCRIBED_USERS, locale));
    String subscribedUsersContent = "";
    if (subscribedUserCellValueHolder == null) {
      return;
    }
    subscribedUsersContent = subscribedUserCellValueHolder.getAttributeValue();

    String subscribedUserNames = StringUtils.defaultString(subscribedUsersContent);
    String[] subscribedUsersLogins = ExcelImportUtilities.getSplittedArray(subscribedUserNames, ExcelSheet.IN_LINE_SEPARATOR.trim());

    Set<String> validLoginNames = Sets.newHashSet();
    for (String login : subscribedUsersLogins) {
      if (StringUtils.isNotEmpty(login)) {
        validLoginNames.add(login);
      }
    }

    Set<User> subscribedUsers = this.loadOrCreateUsers(validLoginNames);
    buildingBlock.getSubscribedUsers().clear();
    buildingBlock.getSubscribedUsers().addAll(subscribedUsers);
  }

  /**
   * Checks if the given BuildingBlock needs special treatment and creates the special Relations, if necessary.
   * @param locale
   *          Locale
   * @param buildingBlockRel
   *          the given BuildingBlockRelation
   * @return null, if special treatment was necessary, a dbCopy of the BuildingBlock for further treatment otherwise
   */
  private BuildingBlock handleSpecialRelations(Locale locale, BuildingBlockRelations buildingBlockRel, Map<String, AttributeType> allAttributeTypes) {
    BuildingBlock buildingBlock = buildingBlockRel.getBuildingBlock();
    Map<String, CellValueHolder> entries = buildingBlockRel.getContent();
    Map<String, CellValueHolder> attributes = buildingBlockRel.getAttributes();

    // ISI/BM are special relationship BBs (and their "name" cannot at this stage be printed so easily)
    if (buildingBlock instanceof InformationSystemInterface) {
      createBuildingBlockRelationsISI((InformationSystemInterface) buildingBlock, entries, attributes, locale, allAttributeTypes);
      return null;
    }
    else if (buildingBlock instanceof BusinessMapping) {
      createBuildingBlockRelationsBM(entries, attributes, locale, allAttributeTypes);
      return null;
    }

    // BuildingBlock not imported?
    BuildingBlock dbCopy = getDbCopyById(buildingBlock.getId(), buildingBlock.getTypeOfBuildingBlock());
    if (dbCopy == null) {
      getProcessingLog().debug("Element {0} wasn't imported; Neither will its relations be.", buildingBlock.getNonHierarchicalName());
    }
    return dbCopy;
  }

  /**
   * Loads the users with the login names specified in {@code userLoginNames} set. If the user does not exist, it will be created (see
   * {@link UserService#createUser(String)}).
   * 
   * @param userLoginNames
   *          the set containing user login names
   * @return the set of user entities
   */
  private Set<User> loadOrCreateUsers(Set<String> userLoginNames) {
    Set<User> result = Sets.newHashSet();

    for (String loginName : userLoginNames) {
      User user = userService.getUserByLoginIfExists(loginName);

      if (user == null) {
        getProcessingLog().info("The user {0} does not exist. Creating new one", loginName);
        user = userService.createUser(loginName);
      }

      result.add(user);
    }

    return result;
  }

  /**
   * Loads the users with the login names specified in {@code userLoginNames} set. If the user does not exist, it will be created (see
   * {@link UserService#createUser(String)}).
   * 
   * @param userLoginNames
   *          the set containing user login names
   * @return the set of user entities
   */
  private Set<User> loadOrCreateUsers(CellValueHolder users) {
    String usersContent = users.getAttributeValue();
    if (StringUtils.isBlank(usersContent)) {
      getProcessingLog().error("[{0}] The users are not specified, skipping", users.getCellRef());
      return null;
    }
    String[] splittedArray = ExcelImportUtilities.getSplittedArray(usersContent, ExcelSheet.IN_LINE_SEPARATOR.trim());

    Set<String> userLoginNames = Sets.newHashSet(splittedArray);
    Set<User> result = Sets.newHashSet();

    for (String loginName : userLoginNames) {
      User user = userService.getUserByLoginIfExists(loginName);

      if (user == null) {
        getProcessingLog().info("[{0}] The user {1} does not exist. Creating new one", users.getCellRef(), loginName);
        user = userService.createUser(loginName);
      }

      result.add(user);
    }

    return result;
  }

  private void importAttributes(Collection<BuildingBlockAttributes> buildingBlockAttSet, Map<String, AttributeType> allAttributeTypes) {
    for (BuildingBlockAttributes buildingBlockAtt : buildingBlockAttSet) {
      BuildingBlock buildingBlock = buildingBlockAtt.getBuildingBlock();
      try {
        importAttributesForBB(buildingBlock, buildingBlockAtt.getAttributes(), allAttributeTypes);
      } catch (IteraplanBusinessException e) {
        String bbType = MessageAccess.getString(buildingBlock.getTypeOfBuildingBlock().toString());
        LOGGER.warn("saving attributes for building block " + buildingBlock.getNonHierarchicalName() + " from row " + buildingBlockAtt.getRowNum()
            + " failed", e);
        getProcessingLog().warn("Row {0}  Error saving attributes for building block {0} of type {1}; skipping this row!",
            buildingBlockAtt.getRowNum(), buildingBlock.getNonHierarchicalName(), bbType);
      }
    }
  }

  /**
   * Imports attributes into the database for a given BB
   * 
   * @param bb
   *          For most BBs, this should just be the BB that was read in from the ImportContent with its proper Database ID filled in (The Database
   *          copy of which will be automatically retrieved by Id; if it can't be found by ID, it will just be skipped), ie: for those BBTs, this
   *          parameter does not need to be the Database/WorkingCopy. However, for ISI/BM, it must already be the Database/WorkingCopy.
   * @param entries
   *          Mappings of Attribute Name + Group -> Attribute Value
   */
  private void importAttributesForBB(BuildingBlock bb, Map<String, CellValueHolder> entries, Map<String, AttributeType> allAttributeTypes) {
    if (bb.getId() == null) {
      return;
    }

    CellValueHolder anyCell = Iterables.getFirst(entries.values(), null);
    String rowNum = (anyCell == null ? "empty" : ExcelImportUtilities.getCellRow(anyCell.getOriginCell()));
    getProcessingLog().info("saving attributes from row [{0}] for: {1}", rowNum, bb.getNonHierarchicalName());
    LOGGER.debug("{0} has these attributes: {1}", bb.getNonHierarchicalName(), entries);

    for (Entry<String, CellValueHolder> entry : entries.entrySet()) {
      final String name = getAttributeName(entry.getKey());
      final CellValueHolder cellValueHolder = entry.getValue();
      final AttributeType attrType = allAttributeTypes.get(name);

      if (attrType != null && isAttributeTypeEnabledForBB(bb, attrType)) {
        AttributeTypeGroup atg = attrType.getAttributeTypeGroup();
        if (!UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE)) {
          getProcessingLog().error("You have no permission to edit attributes in the group {0}. The attribute {1} in cell [{2}] will not be saved.",
              atg.getName(), attrType.getName(), cellValueHolder.getCellRef());
          continue;
        }

        try {
          Set<AttributeValue> avSet = ExcelImportUtilities.createAttributeValue(attrType, entry.getValue());

          if (SINGLE_VALUE_ATS.contains(attrType.getTypeOfAttribute())) {
            if (!avSet.isEmpty()) {
              AttributeValue firstAV = Iterables.get(avSet, 0);

              // This is a hack to avoid ObjectDeletedExceptions
              // Usually this should be fixed in another place, but the according changes could have
              // undesired side-effects throughout the rest of iteraplan (where attribute value unsetting
              // seems to work just fine)
              // a local change here is the least invasive change, and since this code is scheduled
              // to be retired anyway, there is basically no technical debt.
              if (firstAV == null) {
                AttributeValueAssignment ava = bb.getAssignmentForId(attrType.getId());
                if (ava != null) {
                  ava.getAttributeValue().getAttributeValueAssignments().remove(ava);
                  ava.removeReferences();
                }
              }
              // end of hack

              attributeValueService.setValue(bb, firstAV, attrType);
            }
          }
          else {

            // This is a hack to avoid ObjectDeletedExceptions
            // Usually this should be fixed in another place, but the according changes could have
            // undesired side-effects throughout the rest of iteraplan (where attribute value unsetting
            // seems to work just fine)
            // a local change here is the least invasive change, and since this code is scheduled
            // to be retired anyway, there is basically no technical debt.
            if (avSet.isEmpty()) {
              Set<AttributeValueAssignment> avas = Sets.newHashSet(bb.getAssignmentsForId(attrType.getId()));
              for (AttributeValueAssignment ava : avas) {
                ava.getAttributeValue().getAttributeValueAssignments().remove(ava);
                ava.removeReferences();
              }
            }
            // end of hack

            attributeValueService.setReferenceValues(bb, avSet, attrType.getId());
          }
        } catch (IllegalArgumentException ex) {
          getProcessingLog().warn("Cell [{0}]  Ignoring value \"{1}\" for attribute type \"{2}\" due to error.", cellValueHolder.getCellRef(),
              cellValueHolder.getAttributeValue(), attrType.getName());
        }
      }
      else {
        getProcessingLog()
            .warn(
                "The attribute {0} does not exist in the database or is not supported by {1} building block. The value in cell [{2}] thus cannot be imported",
                entry.getKey(), bb, ExcelImportUtilities.getCellRef(entry.getValue().getOriginCell()));
      }
    }
    attributeValueService.saveOrUpdateAttributeValues(bb);
  }

  private boolean isAttributeTypeEnabledForBB(BuildingBlock bb, final AttributeType attrType) {
    for (BuildingBlockType bbt : attrType.getBuildingBlockTypes()) {
      if (bbt.getTypeOfBuildingBlock().equals(bb.getTypeOfBuildingBlock())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the attribute name from the specified attribute header name, read from Excel file. The header names can contain the groups, specified
   * between {@code '('} and {@code ')'}. For example {@code 'Complexity (Default Attribute Group)'}.
   * 
   * @param attrHeadName the attribute header name
   * @return the attribute name
   */
  private String getAttributeName(String attrHeadName) {
    String groupSeparator = "(";
    if (attrHeadName.contains(groupSeparator)) {
      String fullName[] = ExcelImportUtilities.getSplittedArray(attrHeadName, "(");
      return fullName[0].trim();
    }

    return attrHeadName.trim();
  }

  private void saveTcr(BuildingBlockHolder tcrHolder) {
    BuildingBlock bb = tcrHolder.getBuildingBlock();
    if (!(bb instanceof TechnicalComponentRelease)) {
      throw new IllegalArgumentException("Can only process Technical Component Releases");
    }

    TechnicalComponentRelease tcr = (TechnicalComponentRelease) bb;
    TechnicalComponent newTc = tcr.getTechnicalComponent();

    boolean doesTCExist = getTechnicalComponentReleaseService().isDuplicateTechnicalComponent(newTc.getName(), newTc.getId());
    if (doesTCExist) {
      String tcrNameCellRef = ExcelImportUtilities.getCellRef(tcrHolder.getBuildingBlockCell());
      TechnicalComponent tc = (TechnicalComponent) getBuildingBlockByName(newTc.getTypeOfBuildingBlock(), newTc.getName(), tcrNameCellRef);
      newTc.getReleases().clear();
      tc.addRelease(tcr);
    }

    if (tcr.getId() == null) {
      try {
        getTechnicalComponentReleaseService().validateDuplicate(tcr.getTechnicalComponent(), tcr);
      } catch (IteraplanBusinessException e) {
        int errorCode = e.getErrorCode();
        if (errorCode == IteraplanErrorMessages.DUPLICATE_RELEASE) {
          getProcessingLog().error("The Technical Component Release {0} already exists in DB. Skipping", tcr.getNonHierarchicalName());
          return;
        }
        else {
          throw e;
        }
      }
    }

    getTechnicalComponentReleaseService().saveOrUpdate(tcr);
  }

  private void saveIsr(BuildingBlockHolder isrHolder) {
    BuildingBlock bb = isrHolder.getBuildingBlock();
    if (!(bb instanceof InformationSystemRelease)) {
      throw new IllegalArgumentException("Can only process Information System Releases");
    }

    InformationSystemRelease isr = (InformationSystemRelease) bb;
    final InformationSystem newIs = isr.getInformationSystem();
    final boolean doesISExist = getInformationSystemReleaseService().isDuplicateInformationSystem(newIs.getName(), newIs.getId());

    if (doesISExist) {
      String isrNameCellRef = ExcelImportUtilities.getCellRef(isrHolder.getBuildingBlockCell());
      final InformationSystem is = (InformationSystem) getBuildingBlockByName(newIs.getTypeOfBuildingBlock(), newIs.getName(), isrNameCellRef);
      newIs.getReleases().clear();
      is.addRelease(isr);
    }

    if (isr.getId() == null) {
      try {
        getInformationSystemReleaseService().validateDuplicate(isr.getInformationSystem(), isr);
      } catch (IteraplanBusinessException e) {
        int errorCode = e.getErrorCode();
        if (errorCode == IteraplanErrorMessages.DUPLICATE_RELEASE) {
          getProcessingLog().error("The Information System Release {0} already exists in DB. Skipping", isr.getNonHierarchicalName());
          return;
        }
        else {
          throw e;
        }
      }
    }

    getInformationSystemReleaseService().saveOrUpdate(isr);
  }

  /**
   * Returns Building Block by the specified {@code id} and {@code type}. If the specified {@code id} equals {@code null} or the building block will
   * be not found, {@code null} will be returned.
   * 
   * @param id
   *          the building block id
   * @param type
   *          the building block type
   * @return the building block instance or {@code null}, if such building block does not exist
   */
  private BuildingBlock getDbCopyById(Integer id, TypeOfBuildingBlock type) {
    if (id == null) {
      return null;
    }

    return getServiceFor(type).loadObjectByIdIfExists(id);
  }

  /**
   * Returns Building Block by the specified {@code name}, {@code id} and {@code type}.
   * The Building Block is first searched for by {@code name}, then by {@code id}. If inconsistencies
   * are detected, e.g. the retrieved Building Block has not the specified {@code id}, {@code null}
   * is returned. If both name and id are {@code null} or the building block cannot be found,
   * {@code null} will be returned.
   * 
   * @param id
   *          the building block id
   * @param name
   *          the name of the building block
   * @param type
   *          the building block type
   * @return the building block instance or {@code null}, if such building block does not exist
   */
  private BuildingBlock getDbCopy(Integer id, String name, TypeOfBuildingBlock type) {
    String bbName = StringUtils.defaultIfBlank(name, null);
    if (id == null && bbName == null) {
      return null;
    }

    if (bbName != null && getServiceFor(type).doesObjectWithDifferentIdExist(null, bbName)) {
      BuildingBlock bb = getServiceFor(type).findByNames(Sets.newHashSet(bbName)).get(0);
      if (id == null || bb.getId().equals(id)) {
        return bb;
      }
      else {
        String bbType = MessageAccess.getString(type.toString());
        getProcessingLog().warn("ID {0} and name {1} do not match for type {2}, skipping", id, bbName, bbType);
        return null;
      }
    }
    else if (id != null) {
      BuildingBlock bb = getServiceFor(type).loadObjectByIdIfExists(id);

      if (bb != null && type.equals(bb.getTypeOfBuildingBlock())) {
        return bb;
      }
      else {
        return null;
      }
    }

    return null;
  }

  private Integer parseId(CellValueHolder id) {
    try {
      return Integer.valueOf((int) Double.parseDouble(id.getAttributeValue()));

    } catch (NumberFormatException e) {
      getProcessingLog().debug("[{0}] Could not understand Id {1}", id.getCellRef(), id.getAttributeValue());
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends HierarchicalEntity<T>> void setParentRelation(AbstractHierarchicalEntity<T> bb, CellValueHolder hierarchyNameCellValueHolder) {
    if (hierarchyNameCellValueHolder != null) {
      String hierarchyName = hierarchyNameCellValueHolder.getAttributeValue();
      String hierarchyCellRef = ExcelImportUtilities.getCellRef(hierarchyNameCellValueHolder.getOriginCell());
      String parentNameFromHierarchicalName = getParentNameFromHierarchicalName(hierarchyName, bb.getNonHierarchicalName(), hierarchyCellRef);
      BuildingBlock parentNew = getBuildingBlockByName(bb.getTypeOfBuildingBlock(), parentNameFromHierarchicalName, hierarchyCellRef);

      if (parentNew != null && !Objects.equal(parentNew, bb.getParent())) {
        bb.addParent((T) parentNew);
      }
    }
  }

  void createBuildingBlockRelations(BusinessFunction bf, Map<String, CellValueHolder> relations, Locale locale) {
    // Parent
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_BUSINESSFUNCTION_PLURAL, locale));
    setParentRelation(bf, hierarchyNameCellValueHolder);

    // Import BusinessDomains
    CellValueHolder bdCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSDOMAIN_PLURAL, locale));
    if (bdCellValueHolder != null) {
      Set<BusinessDomain> bds = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSDOMAIN, bdCellValueHolder);
      bf.removeBusinessDomains();
      bf.addBusinessDomains(bds);
    }

    CellValueHolder boCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSOBJECT_PLURAL, locale));
    if (boCellValueHolder != null) {
      Set<BusinessObject> bos = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSOBJECT, boCellValueHolder);
      bf.removeBusinessObjects();
      bf.addBusinessObjects(bos);
    }

    getServiceFor(TypeOfBuildingBlock.BUSINESSFUNCTION).saveOrUpdate(bf);
  }

  void createBuildingBlockRelations(ArchitecturalDomain ad, Map<String, CellValueHolder> relations, Locale locale) {
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_ARCHITECTURALDOMAIN_PLURAL, locale));
    setParentRelation(ad, hierarchyNameCellValueHolder);

    CellValueHolder tcrCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, locale));
    if (tcrCellValueHolder != null) {
      Set<TechnicalComponentRelease> tcrs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, tcrCellValueHolder);
      ad.removeTechnicalComponentReleases();
      ad.addTechnicalComponentReleases(tcrs);
    }

    getServiceFor(TypeOfBuildingBlock.ARCHITECTURALDOMAIN).saveOrUpdate(ad);
  }

  /**
   * Gets the header key to retrieve the BM string
   * 
   * @param bmLayout
   *          The 3 BBTypes, in order, used to build the header key
   */
  private String getBusinessMappingHeaderKey(TypeOfBuildingBlock[] bmLayout, Locale locale) {

    assert bmLayout.length == 3;

    String bm = MessageAccess.getStringOrNull(Constants.BB_BUSINESSMAPPING_PLURAL, locale);
    StringBuilder strBuilder = new StringBuilder(bm);
    strBuilder.append(ExcelSheet.UNIT_OPENER);

    for (int i = 0; i < 3; i++) {
      switch (bmLayout[i]) {
        case BUSINESSPROCESS:
          strBuilder.append(MessageAccess.getStringOrNull(Constants.BB_BUSINESSPROCESS, locale));
          break;
        case BUSINESSUNIT:
          strBuilder.append(MessageAccess.getStringOrNull(Constants.BB_BUSINESSUNIT, locale));
          break;
        case PRODUCT:
          strBuilder.append(MessageAccess.getStringOrNull(Constants.BB_PRODUCT, locale));
          break;
        case INFORMATIONSYSTEMRELEASE:
          strBuilder.append(MessageAccess.getStringOrNull(Constants.BB_INFORMATIONSYSTEMRELEASE, locale));
          break;
        default:
          throw new IteraplanTechnicalException();
      }

      // No UnitSeparator after the 3rd BBT Name
      if (i < 2) {
        strBuilder.append(ExcelSheet.UNIT_SEPARATOR);
      }
    }

    strBuilder.append(ExcelSheet.UNIT_CLOSER);
    return strBuilder.toString();
  }

  /**
   * Validates, checks for duplicates, and finally saves a BM.
   * 
   * @return The businessMapping, if it was either created or if it already existed; Else: null
   */
  private BusinessMapping saveBusinessMapping(InformationSystemRelease isr, BusinessProcess bp, BusinessUnit bu, Product product) {
    String happyName = "Business Mapping [" + isr + "][" + product + "][" + bu + "][" + bp + "]";
    getProcessingLog().debug("Saving {0}", happyName);

    if ((product == null) || (bu == null) || (bp == null) || (isr == null)) {
      getProcessingLog().error(happyName + " contains missing elements! Not importing.");
      return null;
    }

    if (product.isTopLevelElement() && bu.isTopLevelElement() && bp.isTopLevelElement()) {
      getProcessingLog().error(happyName + " consists of only top level elements! Not importing.");
      return null;
    }

    // Confirm not duplicate
    BusinessMapping oldBm = getBusinessMappingService().getBusinessMappingByRelatedBuildingBlockIds(product.getId(), bu.getId(), bp.getId(),
        isr.getId());
    if (oldBm != null) {
      getProcessingLog().debug("Such a BusinessMapping already exists, not saving again.");
      return oldBm;
    }

    // Save
    BusinessMapping bm = BuildingBlockFactory.createBusinessMapping();
    bm.addProduct(product);
    bm.addBusinessUnit(bu);
    bm.addBusinessProcess(bp);
    bm.addInformationSystemRelease(isr);
    getBusinessMappingService().saveOrUpdate(bm);

    return bm;

  }

  @SuppressWarnings("PMD.ExcessiveMethodLength")
  void createBuildingBlockRelations(InformationSystemRelease isr, Map<String, CellValueHolder> relations, Locale locale) {
    // Parent
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, locale));
    // cannot use setParentRelation(isr, hierarchyNameCellValueHolder) here, because of type incompatibility
    if (hierarchyNameCellValueHolder != null) {
      String hierarchyName = hierarchyNameCellValueHolder.getAttributeValue();
      String hierarchyCellRef = ExcelImportUtilities.getCellRef(hierarchyNameCellValueHolder.getOriginCell());
      String parentNameFromHierarchicalName = getParentNameFromHierarchicalName(hierarchyName, isr.getNonHierarchicalName(), hierarchyCellRef);
      BuildingBlock parentNew = getBuildingBlockByName(isr.getTypeOfBuildingBlock(), parentNameFromHierarchicalName,
          ExcelImportUtilities.getCellRef(hierarchyNameCellValueHolder.getOriginCell()));
      if (parentNew != null) {
        isr.addParent((InformationSystemRelease) parentNew);
      }
    }

    CellValueHolder predecessorsCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.ASSOC_PREDECESSORS, locale));
    if (predecessorsCellValueHolder != null) {
      Set<InformationSystemRelease> preds = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, predecessorsCellValueHolder);
      isr.removePredecessors();
      isr.addPredecessors(preds);
    }

    CellValueHolder successorsCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.ASSOC_SUCCESSORS, locale));
    if (successorsCellValueHolder != null) {
      Set<InformationSystemRelease> succs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, successorsCellValueHolder);
      isr.removeSuccessors();
      isr.addSuccessors(succs);
    }

    CellValueHolder bfCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSFUNCTION_PLURAL, locale));
    if (bfCellValueHolder != null) {
      Set<BusinessFunction> bfs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSFUNCTION, bfCellValueHolder);
      isr.removeBusinessFunctions();
      isr.addBusinessFunctions(bfs);
    }

    // Interfaces (not imported, just warn user)
    // Reason: We can't uniquely identify an Interface by its "name", since multiple ISIs with the same 2 ISRs can exist
    CellValueHolder interfacesCellValueHolder = relations.get(MessageAccess.getStringOrNull(
        "reporting.excel.header.informationSystemRelease.interfacesTo", locale));
    if (interfacesCellValueHolder != null) {
      String interfaces = interfacesCellValueHolder.getAttributeValue();
      String ifaceCellRef = ExcelImportUtilities.getCellRef(interfacesCellValueHolder.getOriginCell());
      if (!StringUtils.isEmpty(interfaces)) {
        getProcessingLog()
            .warn(
                "{0} has interfaces specified in cell [{1}] in the InformationSystemRelease sheet. These were not imported. Only Interfaces listed in the Interfaces sheet are imported.",
                isr.getNonHierarchicalName(), ifaceCellRef);
      }
    }

    CellValueHolder boCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSOBJECT_PLURAL, locale));
    if (boCellValueHolder != null) {
      Set<BusinessObject> bos = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSOBJECT, boCellValueHolder);
      Set<Isr2BoAssociation> associations = Sets.newHashSetWithExpectedSize(bos.size());
      Set<Isr2BoAssociation> existentAssociations = isr.getBusinessObjectAssociations();
      for (BusinessObject bo : bos) {
        Isr2BoAssociation assoc = BuildingBlockFactory.createIsr2BoAssociation(isr, bo);
        // try to find out whether that association exists already, and if yes, use the existing object
        for (Isr2BoAssociation connectedAssoc : existentAssociations) {
          if (assoc.equals(connectedAssoc)) {
            assoc = connectedAssoc;
            break;
          }
        }
        associations.add(assoc);
      }
      buildingBlockServiceLocator.getIsr2BoAssociationService().saveAssociations(associations);
      isr.connectIsr2BoAssociations(associations);
    }

    // Business Mapping
    TypeOfBuildingBlock[] orderOfBMContent = { TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.PRODUCT, TypeOfBuildingBlock.BUSINESSUNIT };
    CellValueHolder allBMCellValueHolder = relations.get(getBusinessMappingHeaderKey(orderOfBMContent, locale));
    if (allBMCellValueHolder != null) {
      String allBMs = allBMCellValueHolder.getAttributeValue();
      String bmCellRef = ExcelImportUtilities.getCellRef(allBMCellValueHolder.getOriginCell());
      if (StringUtils.isNotEmpty(allBMs)) {
        getProcessingLog()
            .warn(
                "Information System {0} has business mappings specified in cell [{1}]. These were not imported. Only Business Mappings listed in the Business Mappings sheet are imported.",
                isr.getNonHierarchicalName(), bmCellRef);
      }
    }

    CellValueHolder isdCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL, locale));
    if (isdCellValueHolder != null) {
      Set<InformationSystemDomain> isds = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, isdCellValueHolder);
      isr.removeInformationSystemDomains();
      isr.addInformationSystemDomains(isds);
    }

    CellValueHolder ieCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INFRASTRUCTUREELEMENT_PLURAL, locale));
    if (ieCellValueHolder != null) {
      Set<InfrastructureElement> ies = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, ieCellValueHolder);
      isr.removeInfrastructureElements();
      isr.addInfrastructureElements(ies);
    }

    CellValueHolder projectsCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_PROJECT_PLURAL, locale));
    if (projectsCellValueHolder != null) {
      Set<Project> projs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.PROJECT, projectsCellValueHolder);
      isr.removeProjects();
      isr.addProjects(projs);
    }

    CellValueHolder tcrCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, locale));
    if (tcrCellValueHolder != null) {
      Set<TechnicalComponentRelease> tcrs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, tcrCellValueHolder);
      isr.removeTechnicalComponentReleases();
      isr.addTechnicalComponentReleases(tcrs);
    }

    // Uses ISs
    CellValueHolder isCellValueHolder = relations.get(MessageAccess.getStringOrNull(
        ExcelConstants.HEADER_INFORMATIONSYSTEMRELEASE_SHEET_BASECOMPONENTS_COLUMN, locale));
    if (isCellValueHolder != null) {
      Set<InformationSystemRelease> iss = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, isCellValueHolder);
      isr.removeBaseComponents();
      isr.addBaseComponents(iss);
    }

    getInformationSystemReleaseService().saveOrUpdate(isr);
  }

  /**
   * Get the header text for the key of BBT (plural)
   */
  private String getHierarchyHeaderFor(String constantBbtKey, Locale locale) {
    return MessageAccess.getStringOrNull(constantBbtKey, locale) + ' ' + MessageAccess.getStringOrNull("global.hierarchical", locale);
  }

  void createBuildingBlockRelations(BusinessDomain bd, Map<String, CellValueHolder> relations, Locale locale) {
    // Parent
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_BUSINESSDOMAIN_PLURAL, locale));
    setParentRelation(bd, hierarchyNameCellValueHolder);

    CellValueHolder bfCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSFUNCTION_PLURAL, locale));
    if (bfCellValueHolder != null) {
      Set<BusinessFunction> bfs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSFUNCTION, bfCellValueHolder);
      bd.removeBusinessFunctions();
      bd.addBusinessFunctions(bfs);
    }

    CellValueHolder boCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSOBJECT_PLURAL, locale));
    if (boCellValueHolder != null) {
      Set<BusinessObject> bos = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSOBJECT, boCellValueHolder);
      bd.removeBusinessObjects();
      bd.addBusinessObjects(bos);
    }

    CellValueHolder bpCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSPROCESS_PLURAL, locale));
    if (bpCellValueHolder != null) {
      Set<BusinessProcess> bps = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSPROCESS, bpCellValueHolder);
      bd.removeBusinessProcesses();
      bd.addBusinessProcesses(bps);
    }

    CellValueHolder productsCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_PRODUCT_PLURAL, locale));
    if (productsCellValueHolder != null) {
      Set<Product> prods = loadBuildingBlocksAsSet(TypeOfBuildingBlock.PRODUCT, productsCellValueHolder);
      bd.removeProducts();
      bd.addProducts(prods);
    }

    CellValueHolder buCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSUNIT_PLURAL, locale));
    if (buCellValueHolder != null) {
      Set<BusinessUnit> bus = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSUNIT, buCellValueHolder);
      bd.removeBusinessUnits();
      bd.addBusinessUnits(bus);
    }

    getServiceFor(TypeOfBuildingBlock.BUSINESSDOMAIN).saveOrUpdate(bd);
  }

  void createBuildingBlockRelations(BusinessObject bo, Map<String, CellValueHolder> relations, Locale locale) {
    // Parent
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_BUSINESSOBJECT_PLURAL, locale));
    setParentRelation(bo, hierarchyNameCellValueHolder);

    CellValueHolder specializationsCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.ASSOC_SPECIALISATION, locale));
    if (specializationsCellValueHolder != null) {
      Set<BusinessObject> specials = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSOBJECT, specializationsCellValueHolder);
      bo.removeSpecialisationRelations();
      bo.addSpecialisations(specials);
    }

    CellValueHolder bdCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSDOMAIN_PLURAL, locale));
    if (bdCellValueHolder != null) {
      Set<BusinessDomain> bds = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSDOMAIN, bdCellValueHolder);
      bo.removeBusinessDomainRelations();
      bo.addBusinessDomains(bds);
    }

    CellValueHolder bfCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSFUNCTION_PLURAL, locale));
    if (bfCellValueHolder != null) {
      Set<BusinessFunction> bfs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSFUNCTION, bfCellValueHolder);
      bo.removeBusinessFunctionRelations();
      bo.addBusinessFunctions(bfs);
    }

    CellValueHolder isrCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, locale));
    if (isrCellValueHolder != null) {
      Set<InformationSystemRelease> isrs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, isrCellValueHolder);
      Set<Isr2BoAssociation> associations = Sets.newHashSetWithExpectedSize(isrs.size());
      Set<Isr2BoAssociation> existentAssociations = bo.getInformationSystemReleaseAssociations();
      for (InformationSystemRelease isr : isrs) {
        Isr2BoAssociation assoc = BuildingBlockFactory.createIsr2BoAssociation(isr, bo);
        // try to find out whether that association exists already, and if yes, use the existing object
        for (Isr2BoAssociation connectedAssoc : existentAssociations) {
          if (assoc.equals(connectedAssoc)) {
            assoc = connectedAssoc;
            break;
          }
        }
        buildingBlockServiceLocator.getIsr2BoAssociationService().saveAssociations(associations);
        associations.add(assoc);
      }
      bo.connectIsr2BoAssociations(associations);
    }

    getServiceFor(TypeOfBuildingBlock.BUSINESSOBJECT).saveOrUpdate(bo);

  }

  void createBuildingBlockRelations(BusinessProcess bp, Map<String, CellValueHolder> relations, Locale locale) {
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_BUSINESSPROCESS_PLURAL, locale));
    setParentRelation(bp, hierarchyNameCellValueHolder);

    CellValueHolder bdCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSDOMAIN_PLURAL, locale));
    if (bdCellValueHolder != null) {
      Set<BusinessDomain> bds = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSDOMAIN, bdCellValueHolder);
      bp.removeBusinessDomainRelations();
      bp.addBusinessDomains(bds);
    }

    // Business Mapping
    TypeOfBuildingBlock[] orderOfBmContent = { TypeOfBuildingBlock.PRODUCT, TypeOfBuildingBlock.BUSINESSUNIT,
        TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE };
    CellValueHolder allBMCellValueHolder = relations.get(getBusinessMappingHeaderKey(orderOfBmContent, locale));
    if (allBMCellValueHolder != null) {
      String allBMs = allBMCellValueHolder.getAttributeValue();
      String bmCellRef = ExcelImportUtilities.getCellRef(allBMCellValueHolder.getOriginCell());
      if (!StringUtils.isEmpty(allBMs)) {
        getProcessingLog()
            .warn(
                "Business Process {0} has business mappings specified in cell [{1}]. These were not imported. Only Business Mappings listed in the Business Mappings sheet are imported.",
                bp.getNonHierarchicalName(), bmCellRef);
      }
    }

    // save updated relations
    getServiceFor(TypeOfBuildingBlock.BUSINESSPROCESS).saveOrUpdate(bp);
  }

  void createBuildingBlockRelations(BusinessUnit bu, Map<String, CellValueHolder> relations, Locale locale) {
    // Parent
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_BUSINESSUNIT_PLURAL, locale));
    setParentRelation(bu, hierarchyNameCellValueHolder);

    CellValueHolder bdCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSDOMAIN_PLURAL, locale));
    if (bdCellValueHolder != null) {
      Set<BusinessDomain> bds = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSDOMAIN, bdCellValueHolder);
      bu.removeBusinessDomainRelations();
      bu.addBusinessDomains(bds);
    }

    TypeOfBuildingBlock[] orderOfBmContent = { TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.PRODUCT,
        TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE };
    CellValueHolder allBMCellValueHolder = relations.get(getBusinessMappingHeaderKey(orderOfBmContent, locale));
    if (allBMCellValueHolder != null) {
      String allBMs = allBMCellValueHolder.getAttributeValue();
      String bmCellRef = ExcelImportUtilities.getCellRef(allBMCellValueHolder.getOriginCell());
      if (StringUtils.isNotEmpty(allBMs)) {
        getProcessingLog()
            .warn(
                "Business Unit {0} has business mappings specified in cell [{1}]. These were not imported. Only Business Mappings listed in the Business Mappings sheet are imported.",
                bu.getNonHierarchicalName(), bmCellRef);
      }
    }

    getServiceFor(TypeOfBuildingBlock.BUSINESSUNIT).saveOrUpdate(bu);
  }

  void createBuildingBlockRelations(InformationSystemDomain isd, Map<String, CellValueHolder> relations, Locale locale) {
    // Parent
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL, locale));
    setParentRelation(isd, hierarchyNameCellValueHolder);

    CellValueHolder isrCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, locale));
    if (isrCellValueHolder != null) {
      Set<InformationSystemRelease> isrs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, isrCellValueHolder);
      isd.removeInformationSystemReleases();
      isd.addInformationSystemReleases(isrs);
    }

    getServiceFor(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN).saveOrUpdate(isd);
  }

  void createBuildingBlockRelationsBM(Map<String, CellValueHolder> relations, Map<String, CellValueHolder> attributes, Locale locale,
                                      Map<String, AttributeType> allAttributeTypes) {
    // Look up and retrieve the current version of the related BBs
    CellValueHolder bpHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSPROCESS_PLURAL, locale));
    String bpName = (bpHolder != null ? bpHolder.getAttributeValue() : "");
    String bpCellRef = bpHolder != null ? ExcelImportUtilities.getCellRef(bpHolder.getOriginCell()) : "";

    CellValueHolder productHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_PRODUCT_PLURAL, locale));
    String productName = (productHolder != null ? productHolder.getAttributeValue() : "");
    String productCellRef = productHolder != null ? ExcelImportUtilities.getCellRef(productHolder.getOriginCell()) : "";

    CellValueHolder buHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSUNIT_PLURAL, locale));
    String buName = (buHolder != null ? buHolder.getAttributeValue() : "");
    String buCellRef = buHolder != null ? ExcelImportUtilities.getCellRef(buHolder.getOriginCell()) : "";

    CellValueHolder isrHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, locale));
    String isrName = (isrHolder != null ? isrHolder.getAttributeValue() : "");
    String isrCellRef = isrHolder != null ? ExcelImportUtilities.getCellRef(isrHolder.getOriginCell()) : "";

    String bmRowNum = (isrHolder != null ? ExcelImportUtilities.getCellRow(isrHolder.getOriginCell()) : "");

    String happyName = "Business Mapping [" + bpName + "][" + productName + "][" + buName + "][" + isrName + "] from row [" + bmRowNum + "]";
    getProcessingLog().debug("Preparing to save {0}", happyName);

    BusinessProcess bp = (BusinessProcess) getBuildingBlockByNameOrTop(TypeOfBuildingBlock.BUSINESSPROCESS, bpName, bpCellRef);
    Product product = (Product) getBuildingBlockByNameOrTop(TypeOfBuildingBlock.PRODUCT, productName, productCellRef);
    BusinessUnit bu = (BusinessUnit) getBuildingBlockByNameOrTop(TypeOfBuildingBlock.BUSINESSUNIT, buName, buCellRef);
    InformationSystemRelease isr = (InformationSystemRelease) getBuildingBlockByName(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, isrName,
        isrCellRef);

    // All elements must be found (or at least replaced with Top Elements)
    if ((bp == null) || (product == null) || (bu == null) || (isr == null)) {
      getProcessingLog().error("Could not find related all BuildingBlocks for {0}. Not importing.", happyName);
      return;
    }

    // load biz mapping if it exists already
    BusinessMapping bm = getBusinessMappingService()
        .getBusinessMappingByRelatedBuildingBlockIds(product.getId(), bu.getId(), bp.getId(), isr.getId());
    if (bm != null) {
      getProcessingLog().debug("{0} exists already, not saving again. Will try updating it.", happyName);
    }
    else {
      bm = saveBusinessMapping(isr, bp, bu, product);
    }

    if (bm == null) {
      getProcessingLog().warn("Could not create {0}. Giving up on this element.", happyName);
      return;
    }

    // Import Attributes
    getBusinessMappingService().saveOrUpdate(bm);
    importAttributesForBB(bm, attributes, allAttributeTypes);
  }

  void createBuildingBlockRelationsISI(InformationSystemInterface isi, Map<String, CellValueHolder> relations,
                                       Map<String, CellValueHolder> attributes, Locale locale, Map<String, AttributeType> allAttributeTypes) {
    CellValueHolder relAHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INTERFACE_INFORMATIONSYSTEMRELEASE_A, locale));
    String strReleaseA = relAHolder != null ? relAHolder.getAttributeValue() : "";
    String releaseACellRef = relAHolder != null ? ExcelImportUtilities.getCellRef(relAHolder.getOriginCell()) : "";

    CellValueHolder relBHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INTERFACE_INFORMATIONSYSTEMRELEASE_B, locale));
    String strReleaseB = relBHolder != null ? relBHolder.getAttributeValue() : "";
    String releaseBCellRef = relBHolder != null ? ExcelImportUtilities.getCellRef(relBHolder.getOriginCell()) : "";

    String isiRowNum = (relAHolder != null ? ExcelImportUtilities.getCellRow(relAHolder.getOriginCell()) : "");

    String interfaceDescription = "Interface '" + isi.getName() + "' [" + strReleaseA + "] " + isi.getInterfaceDirection() + " [" + strReleaseB
        + "] from row " + isiRowNum;
    getProcessingLog().info("Saving {0}", interfaceDescription);

    InformationSystemRelease releaseA = (InformationSystemRelease) getBuildingBlockByName(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, strReleaseA,
        releaseACellRef);
    InformationSystemRelease releaseB = (InformationSystemRelease) getBuildingBlockByName(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, strReleaseB,
        releaseBCellRef);

    if ((releaseA == null) || (releaseB == null)) {
      getProcessingLog().error("Could not find related BuildingBlocks for {0}. Not importing.", interfaceDescription);
      return;
    }

    if (isi.getId() == null) {
      getProcessingLog().debug(" inserting {0}", interfaceDescription);
    }
    else {
      getProcessingLog().debug(" updating {0}", interfaceDescription);
    }
    isi.connect(releaseA, releaseB);

    // Import TechnicalComponentReleases
    CellValueHolder tcrCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, locale));
    if (tcrCellValueHolder != null) {
      Set<TechnicalComponentRelease> tcrSet = loadBuildingBlocksAsSet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, tcrCellValueHolder);
      isi.removeTechnicalComponentReleases();
      isi.addTechnicalComponentReleases(tcrSet);
    }

    CellValueHolder boNamesCellValueHolder = relations.get(MessageAccess.getStringOrNull("interface.transport.businessObjects", locale));
    saveTransports(boNamesCellValueHolder, isi);

    getInformationSystemInterfaceService().saveOrUpdate(isi);
    importAttributesForBB(isi, attributes, allAttributeTypes);
  }

  private void saveTransports(CellValueHolder boNamesWithDirections, InformationSystemInterface isi) {
    if (boNamesWithDirections == null) {
      return;
    }
    String boNamesStr = boNamesWithDirections.getAttributeValue();
    if (StringUtils.isBlank(boNamesStr)) {
      return;
    }

    String[] boNamesParsed = ExcelImportUtilities.getSplittedArray(boNamesStr, ExcelSheet.IN_LINE_SEPARATOR.trim());
    String boNamesCellRef = ExcelImportUtilities.getCellRef(boNamesWithDirections.getOriginCell());

    for (String boNameWithDirection : Sets.newHashSet(boNamesParsed)) {
      if (StringUtils.isBlank(boNameWithDirection)) {
        continue;
      }

      Matcher matcher = businessObjectPattern.matcher(boNameWithDirection);
      if (matcher.find()) {
        final String direction = matcher.group(1);
        final String name = matcher.group(2);

        if (StringUtils.isNotBlank(name)) {
          saveTransport(direction, name, boNamesCellRef, isi);
        }
        else {
          getProcessingLog().warn("A Business Object name in cell [{0}] is blank: {1}", boNamesCellRef, boNameWithDirection);
        }
      }
      else {
        getProcessingLog()
            .warn("Could not understand the Business Object direction and name in cell [{0}]: {1}", boNamesCellRef, boNameWithDirection);
      }
    }
  }

  private void saveTransport(String direction, String boName, String boCellCoordinates, InformationSystemInterface isi) {
    BusinessObject bo = (BusinessObject) getBuildingBlockByName(TypeOfBuildingBlock.BUSINESSOBJECT, boName, boCellCoordinates);
    if (bo != null) {
      LOGGER.debug("Related BB found: " + bo.getNonHierarchicalName());

      Transport t = createOrLoadTransport(isi, bo);
      setBusinessObjectDirection(t, direction);
    }
    else {
      getProcessingLog().warn("Related business object {0} from cell [{1}] not found, ignoring", boName, boCellCoordinates);
    }
  }

  private Transport createOrLoadTransport(InformationSystemInterface isi, BusinessObject bo) {
    final Transport existingTransport = getExistingTransport(isi, bo);
    if (existingTransport != null) {
      return existingTransport;
    }

    final Transport newTransport = BuildingBlockFactory.createTransport();
    isi.addTransport(newTransport);
    newTransport.addBusinessObject(bo);

    return newTransport;
  }

  /**
   * Returns existing {@link Transport} in {@code isi} with the specified {@code bo}. If such business
   * object does not exist, {@code null} will be returned.
   * 
   * @param isi the information system interface
   * @param bo the business object
   * @return the existing transport or {@code null}
   */
  private Transport getExistingTransport(InformationSystemInterface isi, BusinessObject bo) {
    Set<Transport> transports = isi.getTransports();
    for (Transport transport : transports) {
      if (bo.equals(transport.getBusinessObject())) {
        return transport;
      }
    }

    return null;
  }

  /**
   * Sets the direction of the specified transport {@code t}. If the direction is known, it
   * will be set. Otherwise the direction will be set as "no direction".
   * 
   * @param t the transport to set the direction for
   * @param direction the direction. The direction can be one of the following values: '-', '->', "<-" or '<->'
   */
  private void setBusinessObjectDirection(Transport t, String direction) {
    TransportInfo transportInfo = TransportInfo.getByTextRepresentation(direction);
    switch (transportInfo) {
      case NO_DIRECTION:
        t.setDirection(Direction.NO_DIRECTION);
        break;
      case FIRST_TO_SECOND:
        t.setDirection(Direction.FIRST_TO_SECOND);
        break;
      case SECOND_TO_FIRST:
        t.setDirection(Direction.SECOND_TO_FIRST);
        break;
      case BOTH_DIRECTIONS:
        t.setDirection(Direction.BOTH_DIRECTIONS);
        break;
      default:
        LOGGER.warn("Could not understand the direction of the business object, probably a programming error");
        break;
    }
  }

  void createBuildingBlockRelations(InfrastructureElement ie, Map<String, CellValueHolder> relations, Locale locale) {
    // Parent
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_INFRASTRUCTUREELEMENT_PLURAL, locale));
    setParentRelation(ie, hierarchyNameCellValueHolder);

    CellValueHolder tcrNamesCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL, locale));
    if (tcrNamesCellValueHolder != null) {
      Set<TechnicalComponentRelease> tcrSet = loadBuildingBlocksAsSet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, tcrNamesCellValueHolder);
      Set<Tcr2IeAssociation> associations = Sets.newHashSetWithExpectedSize(tcrSet.size());
      Set<Tcr2IeAssociation> existentAssociations = ie.getTechnicalComponentReleaseAssociations();
      for (TechnicalComponentRelease tcr : tcrSet) {
        Tcr2IeAssociation assoc = BuildingBlockFactory.createTcr2IeAssociation(tcr, ie);
        // try to find out whether that association exists already, and if yes, use the existing object
        for (Tcr2IeAssociation connectedAssoc : existentAssociations) {
          assoc = connectedAssoc;
          break;
        }
        buildingBlockServiceLocator.getTcr2IeAssociationService().saveAssociations(associations);
        associations.add(assoc);
      }
      ie.connectTcr2IeAssociations(associations);
    }

    CellValueHolder isrCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, locale));
    if (isrCellValueHolder != null) {
      Set<InformationSystemRelease> isrs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, isrCellValueHolder);
      ie.removeInformationSystemReleases();
      ie.addInformationSystemReleases(isrs);
    }

    getServiceFor(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT).saveOrUpdate(ie);
  }

  void createBuildingBlockRelations(Product product, Map<String, CellValueHolder> relations, Locale locale) {
    // Parent
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_PRODUCT_PLURAL, locale));
    setParentRelation(product, hierarchyNameCellValueHolder);

    CellValueHolder bdCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_BUSINESSDOMAIN_PLURAL, locale));
    if (bdCellValueHolder != null) {
      Set<BusinessDomain> bds = loadBuildingBlocksAsSet(TypeOfBuildingBlock.BUSINESSDOMAIN, bdCellValueHolder);
      product.removeBusinessDomainRelations();
      product.addBusinessDomains(bds);
    }

    TypeOfBuildingBlock[] orderOfBmContent = { TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.BUSINESSUNIT,
        TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE };
    CellValueHolder allBMCellValueHolder = relations.get(getBusinessMappingHeaderKey(orderOfBmContent, locale));
    if (allBMCellValueHolder != null) {
      String allBMs = allBMCellValueHolder.getAttributeValue();
      String bmCellRef = ExcelImportUtilities.getCellRef(allBMCellValueHolder.getOriginCell());
      if (StringUtils.isNotEmpty(allBMs)) {
        getProcessingLog()
            .warn(
                "Product {0} has business mappings specified in cell [{1}]. These were not imported. Only Business Mappings listed in the Business Mappings sheet are imported.",
                product.getNonHierarchicalName(), bmCellRef);
      }
    }

    getServiceFor(TypeOfBuildingBlock.PRODUCT).saveOrUpdate(product);
  }

  void createBuildingBlockRelations(Project project, Map<String, CellValueHolder> relations, Locale locale) {
    // Parent
    CellValueHolder hierarchyNameCellValueHolder = relations.get(getHierarchyHeaderFor(Constants.BB_PROJECT_PLURAL, locale));
    setParentRelation(project, hierarchyNameCellValueHolder);

    CellValueHolder isrCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, locale));
    if (isrCellValueHolder != null) {
      Set<InformationSystemRelease> isrs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, isrCellValueHolder);
      project.removeInformationSystemReleases();
      project.addInformationSystemReleases(isrs);
    }

    getServiceFor(TypeOfBuildingBlock.PROJECT).saveOrUpdate(project);
  }

  /**
   * Returns the Parent name by passing a hierarchical name, or null if not found.
   * 
   * @param nameHierarchy
   * @param nameNonHierarchy
   *          If passed (not null), checks that this is the name contained in the other parameter, returning null on mismatch.
   */
  String getParentNameFromHierarchicalName(String nameHierarchy, String nameNonHierarchy, String cellCoordinates) {
    if (nameHierarchy == null) {
      return null;
    }

    // If there is at least one :, there is a parent
    final String hierarchySeparator = Constants.HIERARCHYSEP.trim();

    if (nameHierarchy.contains(hierarchySeparator)) {
      // Only verify the name if this parameter was passed
      if (nameNonHierarchy != null) {
        String nameNonHierarchyFound = StringUtils.substringAfterLast(nameHierarchy, hierarchySeparator);
        nameNonHierarchyFound = StringUtils.strip(nameNonHierarchyFound);

        // Check if the Name part of the Hierarchy is the one we expected
        if (!nameNonHierarchyFound.equalsIgnoreCase(nameNonHierarchy)) {
          getProcessingLog().warn(
              "Hierarchical Name \"{0}\" in cell [{1}] does not contain the name of the BuildingBlock it belongs to ({2}). Not setting parent!",
              nameHierarchy, cellCoordinates, nameNonHierarchy);
          return null;
        }
      }
      String allBeforeName = StringUtils.substringBeforeLast(nameHierarchy, hierarchySeparator);
      allBeforeName = StringUtils.strip(allBeforeName);

      // Strip grandparents+ if necessary
      if (allBeforeName.contains(hierarchySeparator)) {
        return StringUtils.substringAfterLast(allBeforeName, Constants.HIERARCHYSEP).trim();
      }
      return allBeforeName;
    }
    return null;
  }

  void createBuildingBlockRelations(TechnicalComponentRelease tcr, Map<String, CellValueHolder> relations, Locale locale) {
    CellValueHolder ieNamesCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INFRASTRUCTUREELEMENT_PLURAL, locale));
    if (ieNamesCellValueHolder != null) {
      Set<InfrastructureElement> ieSet = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, ieNamesCellValueHolder);
      Set<Tcr2IeAssociation> associations = Sets.newHashSetWithExpectedSize(ieSet.size());
      Set<Tcr2IeAssociation> existentAssociations = tcr.getInfrastructureElementAssociations();
      for (InfrastructureElement ie : ieSet) {
        Tcr2IeAssociation assoc = BuildingBlockFactory.createTcr2IeAssociation(tcr, ie);
        // try to find out whether that association exists already, and if yes, use the existing object
        for (Tcr2IeAssociation connectedAssoc : existentAssociations) {
          assoc = connectedAssoc;
          break;
        }
        buildingBlockServiceLocator.getTcr2IeAssociationService().saveAssociations(associations);
        associations.add(assoc);
      }
      tcr.connectTcr2IeAssociations(associations);
    }

    // Interfaces (not imported, just warn user)
    // Reason: We can't uniquely identify an Interface by its "name", since multiple ISIs with the same 2 ISRs can exist
    CellValueHolder interfacesCellValueHolder = relations.get(MessageAccess.getStringOrNull(
        ExcelConstants.HEADER_TECHNICALCOMPONENTRELEASE_SHEET_INTERFACES_COLUMN, locale));
    if (interfacesCellValueHolder != null) {
      String interfaces = interfacesCellValueHolder.getAttributeValue();
      String cellRef = ExcelImportUtilities.getCellRef(interfacesCellValueHolder.getOriginCell());
      if (!StringUtils.isEmpty(interfaces)) {
        getProcessingLog()
            .warn(
                "Technical Component {0} has interfaces specified in cell [{1}]. These were not imported. Only interfaces listed in the Interface sheet are imported.",
                tcr.getNonHierarchicalName(), cellRef);
      }
    }

    CellValueHolder isrCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, locale));
    if (isrCellValueHolder != null) {
      Set<InformationSystemRelease> isrs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, isrCellValueHolder);
      tcr.removeInformationSystemReleases();
      tcr.addInformationSystemReleases(isrs);
    }

    // import ArchitecturalDomains
    CellValueHolder adCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.BB_ARCHITECTURALDOMAIN_PLURAL, locale));
    if (adCellValueHolder != null) {
      Set<ArchitecturalDomain> ads = loadBuildingBlocksAsSet(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, adCellValueHolder);
      tcr.removeArchitecturalDomains();
      tcr.addArchitecturalDomains(ads);
    }

    CellValueHolder tcrNamesCellValueHolder = relations.get(MessageAccess.getStringOrNull(
        ExcelConstants.HEADER_TECHNICALCOMPONENTRELEASE_SHEET_BASECOMPONENTS_COLUMN, locale));
    if (tcrNamesCellValueHolder != null) {
      Set<TechnicalComponentRelease> usedTcrs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, tcrNamesCellValueHolder);
      tcr.removeBaseComponents();
      tcr.addBaseComponents(usedTcrs);
    }

    CellValueHolder predecessorsCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.ASSOC_PREDECESSORS, locale));
    if (predecessorsCellValueHolder != null) {
      Set<TechnicalComponentRelease> preds = loadBuildingBlocksAsSet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, predecessorsCellValueHolder);
      tcr.removePredecessors();
      tcr.addPredecessors(preds);
    }

    CellValueHolder successorsCellValueHolder = relations.get(MessageAccess.getStringOrNull(Constants.ASSOC_SUCCESSORS, locale));
    if (successorsCellValueHolder != null) {
      Set<TechnicalComponentRelease> succs = loadBuildingBlocksAsSet(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, successorsCellValueHolder);
      tcr.removeSuccessors();
      tcr.addSuccessors(succs);
    }

    getTechnicalComponentReleaseService().saveOrUpdate(tcr);
  }

  /**
   * Retrieves the building block objects, described by a list of building block names, from the database. Building block names that cannot be found
   * in the DB will be silently ignored; only a log message is written.
   * 
   * @param <T> the element type of the resulting set. This MUST correspond to the enum type passed in as <code>tob</code>.
   * @param tob the Building Block type of the listed building blocks.
   * @param buildingBlockList a semicolon-separated list of building block names.
   * @param cellCoordinates Excel cell coordinates of the cell where the building block list was read from, for logging purposes
   * @return the corresponding building block objects, as many as could be found in the DB.
   */
  @SuppressWarnings("unchecked")
  private <T extends BuildingBlock> Set<T> loadBuildingBlocksAsSet(TypeOfBuildingBlock tob, String buildingBlockList, String cellCoordinates) {
    if (StringUtils.isBlank(buildingBlockList)) {
      return Collections.emptySet();
    }

    Set<String> allNames = Sets.newHashSet(ExcelImportUtilities.getSplittedArray(buildingBlockList, ExcelSheet.IN_LINE_SEPARATOR.trim()));
    final Set<String> nonHierarchicalNames = getNonHierarchicalNames(allNames);
    List<BuildingBlock> loadedBbList = getServiceFor(tob).findByNames(nonHierarchicalNames);
    Set<T> foundBuildingBlocks = Sets.newHashSet((Collection<? extends T>) loadedBbList);

    checkFoundResults(tob, foundBuildingBlocks, nonHierarchicalNames, cellCoordinates);
    return foundBuildingBlocks;
  }

  /**
   * Like {@link #loadBuildingBlocksAsSet(TypeOfBuildingBlock, String, String)}, but extracts the cell value internally
   * @param tob the Building Block type of the listed building blocks.
   * @param buildingBlockList cell value holder for the list of BB names
   * @return the corresponding building block objects, as many as could be found in the DB.
   */
  private <T extends BuildingBlock> Set<T> loadBuildingBlocksAsSet(TypeOfBuildingBlock tob, CellValueHolder buildingBlockList) {
    String cellText = buildingBlockList.getAttributeValue();
    String cellRef = ExcelImportUtilities.getCellRef(buildingBlockList.getOriginCell());
    return loadBuildingBlocksAsSet(tob, cellText, cellRef);
  }

  /**
   * Checks if all building blocks are found. For missing building blocks a warning will be written to
   * the processing log.
   * 
   * @param tob the type of building block
   * @param foundBuildingBlocks the found building blocks
   * @param nonHierarchicalNames the set of non-hierarchical building block names
   * @param cellCoordinates Excel cell coordinates of the cell where the building block list was read from, for logging purposes
   */
  private void checkFoundResults(TypeOfBuildingBlock tob, Set<? extends BuildingBlock> foundBuildingBlocks, final Set<String> nonHierarchicalNames,
                                 String cellCoordinates) {
    Set<String> allNamesLower = convertToLowerCase(nonHierarchicalNames);
    Set<String> buildingBlockNames = getBuildingBlockNames(foundBuildingBlocks);
    for (String searchName : allNamesLower) {
      if (!buildingBlockNames.contains(searchName)) {
        String bbType = MessageAccess.getString(tob.toString());
        getProcessingLog().warn("Cell [{0}]: Related building block {1} (type {2}) not found, ignoring", cellCoordinates, searchName, bbType);
      }
    }
  }

  /**
   * Returns the non-hierarchical building block names.
   * 
   * @param buildingBlocks the building blocks to get the names for
   * @return the set containing lower-case non-hierarchical names
   */
  private Set<String> getBuildingBlockNames(Set<? extends BuildingBlock> buildingBlocks) {
    Set<String> names = Sets.newHashSet();
    for (BuildingBlock buildingBlock : buildingBlocks) {
      names.add(StringUtils.lowerCase(buildingBlock.getNonHierarchicalName()));
    }

    return names;
  }

  private Set<String> convertToLowerCase(Set<String> names) {
    Set<String> result = Sets.newHashSet();
    for (String string : names) {
      result.add(string.toLowerCase());
    }

    return result;
  }

  private Set<String> getNonHierarchicalNames(Set<String> names) {
    Set<String> nonHierarchicalNames = Sets.newHashSet();
    for (String name : names) {
      if (name.indexOf(Constants.HIERARCHYSEP) > -1) {
        String nameNonHierarchy = name.substring(name.lastIndexOf(Constants.HIERARCHYSEP) + Constants.HIERARCHYSEP.length());
        nonHierarchicalNames.add(nameNonHierarchy);
      }
      else {
        nonHierarchicalNames.add(name);
      }
    }

    return nonHierarchicalNames;
  }

  /**
   * Returns the building block service instance that is responsible for the given {@link TypeOfBuildingBlock}.
   * 
   * @param tob
   *          One of the user-visible building block types. Internal types, such as business mappings, transports and attributable relation types are
   *          not supported; null will be returned.
   * @return a service instance that can deal with the requested building block type, or <code>null</code> if <code>tob</code> is not supported.
   */
  private BuildingBlockService<BuildingBlock, Integer> getServiceFor(TypeOfBuildingBlock tob) {
    return buildingBlockServiceLocator.getService(tob);
  }

  /**
   * Works just like getBuildingBlockByName, except that it will try to return the top-level element if the desired element can't be found. Might
   * still return null if that can't be found either.
   */
  private BuildingBlock getBuildingBlockByNameOrTop(TypeOfBuildingBlock type, String name, String cellCoordinates) {
    BuildingBlock result = getBuildingBlockByName(type, name, cellCoordinates);
    if (result == null) {
      String bbType = MessageAccess.getString(type.toString());
      getProcessingLog().warn("Cell [{0}]: Element of type {1} with name {2} not found. Using Virtual Element instead.", cellCoordinates, bbType,
          name);
      return getBuildingBlockByName(type, AbstractHierarchicalEntity.TOP_LEVEL_NAME, "none");
    }

    return result;
  }

  /**
   * Get a BuildingBlock by its Non-Hierarchical Name. Returns null if a BuildingBlock with exactly this name can't be found. (Hierarchical names are
   * automatically converted to be Non-Hierarchical)
   * 
   * @param type
   * @param name
   *          Name of BB. For ISRs/TCRs, in format: "BaseName # Version"
   * @return a BB with exactly this name, else null
   */
  protected BuildingBlock getBuildingBlockByName(TypeOfBuildingBlock type, String name, String cellCoordinates) {
    if (name == null) {
      return null;
    }

    Set<BuildingBlock> foundBuildingBlocks = loadBuildingBlocksAsSet(type, name, cellCoordinates);
    return Iterables.getFirst(foundBuildingBlocks, null);
  }

  private Level getLogLevel() {
    if (FRONTEND_LOG.isDebugEnabled()) {
      return Level.DEBUG;
    }
    if (FRONTEND_LOG.isInfoEnabled()) {
      return Level.INFO;
    }
    if (FRONTEND_LOG.isWarnEnabled()) {
      return Level.WARN;
    }
    return Level.ERROR;
  }

  @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
  private InformationSystemInterfaceService getInformationSystemInterfaceService() {
    // the compiler does not allow to cast directly, so we have to make this intermediate step
    BuildingBlockService<?, ?> x = getServiceFor(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);
    return (InformationSystemInterfaceService) x;
  }

  @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
  private InformationSystemReleaseService getInformationSystemReleaseService() {
    // the compiler does not allow to cast directly, so we have to make this intermediate step
    BuildingBlockService<?, ?> x = getServiceFor(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    return (InformationSystemReleaseService) x;
  }

  @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
  private TechnicalComponentReleaseService getTechnicalComponentReleaseService() {
    // the compiler does not allow to cast directly, so we have to make this intermediate step
    BuildingBlockService<?, ?> x = getServiceFor(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
    return (TechnicalComponentReleaseService) x;
  }

  @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
  private BusinessMappingService getBusinessMappingService() {
    // the compiler does not allow to cast directly, so we have to make this intermediate step
    BuildingBlockService<?, ?> x = getServiceFor(TypeOfBuildingBlock.BUSINESSMAPPING);
    return (BusinessMappingService) x;
  }

  public void setAttributeValueService(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public void setBuildingBlockServiceLocator(BuildingBlockServiceLocator buildingBlockServiceLocator) {
    this.buildingBlockServiceLocator = buildingBlockServiceLocator;
  }

}
