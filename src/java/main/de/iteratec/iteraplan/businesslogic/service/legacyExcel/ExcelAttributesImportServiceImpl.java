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

import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.AttData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.AttributeData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.AttributeWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.CellValueHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.DateAttData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.EnumAttData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.EnumAttributeValueData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ExcelImportUtilities;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.NumberAttData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog.Level;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ResponsibilityAttData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.TextAttData;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.UserGroupService;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.RangeValue;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserGroup;


/**
 * A default implementation of {@link ExcelAttributesImportService} for importing the 
 * attribute definitions from Excel files.
 */
public class ExcelAttributesImportServiceImpl implements ExcelAttributesImportService {

  private static final Logger       LOGGER = Logger.getIteraplanLogger(ExcelAttributesImportServiceImpl.class);

  private AttributeTypeGroupService attributeTypeGroupService;
  private BuildingBlockTypeService  buildingBlockTypeService;
  private AttributeTypeService      attributeTypeService;
  private AttributeValueService     attributeValueService;
  private UserService               userService;
  private UserGroupService          userGroupService;

  /** {@inheritDoc} */
  public void importAttributes(InputStream is, PrintWriter logWriter) {
    ProcessingLog userLog = new ProcessingLog(Level.DEBUG, logWriter);
    AttributeWorkbook importer = new AttributeWorkbook(userLog);

    try {
      AttributeData attributeData = importer.doImport(is);
      importEnumerationAttributes(userLog, attributeData.getEnumData());
      importResponsibilityAttributes(userLog, attributeData.getResponsibilityData());
      importNumberAttributes(userLog, attributeData.getNumberData());
      importDateAttributes(userLog, attributeData.getDateData());
      importTextAttributes(userLog, attributeData.getTextData());

    } catch (Exception e) {
      userLog.error("Import failed with exception: ", e);
      throw new IteraplanBusinessException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } finally {
      AttributeWorkbook.removeProcessingLog();
    }
  }

  /**
   * imports all enumeration attributes.
   * @param userLog the processing log for saving import messages
   * @param imEnumAttData list of attribute data, to import
   */
  private void importEnumerationAttributes(ProcessingLog userLog, List<EnumAttData> imEnumAttData) {
    userLog.info("Saving Enumeration Attributes");
    checkForDuplicate(imEnumAttData, userLog);

    for (EnumAttData enumAttData : imEnumAttData) {
      EnumAT enumAt = createOrLoadAt(enumAttData.getName(), enumAttData.getOldName(), userLog, EnumAT.class);
      if (enumAt == null) {
        continue;
      }
      userLog.info("[{0}] Saving attribute: {1}", enumAttData.getName().getCellRef(), enumAt);

      enumAt.setDescription(enumAttData.getDescription().getAttributeValue());
      enumAt.setMandatory(parseBoolean(enumAttData.getMandatory()));
      enumAt.setMultiassignmenttype(parseBoolean(enumAttData.getMultiple()));
      setGroup(enumAttData, enumAt, userLog);
      setActivatedTypes(enumAttData, enumAt, userLog);
      setEnumValues(enumAttData, enumAt, userLog);
      attributeTypeService.saveOrUpdate(enumAt);
    }
  }

  /**
   * imports all responsibility attributes.
   * @param userLog the processing log for saving import messages
   * @param imResponsibilityAttData list of attribute data, to import
   */
  private void importResponsibilityAttributes(ProcessingLog userLog, List<ResponsibilityAttData> imResponsibilityAttData) {
    userLog.info("Saving Responsibility Attributes");
    checkForDuplicate(imResponsibilityAttData, userLog);

    for (ResponsibilityAttData responsibilityAttData : imResponsibilityAttData) {
      ResponsibilityAT responsibilityAt = createOrLoadAt(responsibilityAttData.getName(), responsibilityAttData.getOldName(), userLog,
          ResponsibilityAT.class);
      if (responsibilityAt == null) {
        continue;
      }
      userLog.info("[{0}] Saving attribute: {1}", responsibilityAttData.getName().getCellRef(), responsibilityAt);

      responsibilityAt.setDescription(responsibilityAttData.getDescription().getAttributeValue());
      responsibilityAt.setMandatory(parseBoolean(responsibilityAttData.getMandatory()));
      responsibilityAt.setMultiassignmenttype(parseBoolean(responsibilityAttData.getMultiple()));
      setGroup(responsibilityAttData, responsibilityAt, userLog);
      setActivatedTypes(responsibilityAttData, responsibilityAt, userLog);
      setUsers(responsibilityAttData, responsibilityAt, userLog);
      setUserGroups(responsibilityAttData, responsibilityAt, userLog);
      attributeTypeService.saveOrUpdate(responsibilityAt);
    }
  }

  /**
   * imports all number attributes.
   * @param userLog the processing log for saving import messages
   * @param imNumberAttData list of attribute data, to import
   */
  private void importNumberAttributes(ProcessingLog userLog, List<NumberAttData> imNumberAttData) {
    userLog.info("Saving Number Attributes");
    checkForDuplicate(imNumberAttData, userLog);

    for (NumberAttData numberAttData : imNumberAttData) {
      NumberAT numberAt = createOrLoadAt(numberAttData.getName(), numberAttData.getOldName(), userLog, NumberAT.class);
      if (numberAt == null) {
        continue;
      }
      userLog.info("[{0}] Saving attribute: {1}", numberAttData.getName().getCellRef(), numberAt);

      numberAt.setDescription(numberAttData.getDescription().getAttributeValue());
      numberAt.setMandatory(parseBoolean(numberAttData.getMandatory()));
      numberAt.setUnit(numberAttData.getUnit().getAttributeValue());
      setGroup(numberAttData, numberAt, userLog);
      setBounds(numberAttData, numberAt, userLog);
      setRange(numberAttData, numberAt, userLog);
      setActivatedTypes(numberAttData, numberAt, userLog);
      attributeTypeService.saveOrUpdate(numberAt);
    }
  }

  /**
   * imports all date attributes.
   * @param userLog the processing log for saving import messages
   * @param enumData list of attribute data, to import
   */
  private void importDateAttributes(ProcessingLog userLog, List<DateAttData> imDateAttData) {
    userLog.info("Saving Date Attributes");
    checkForDuplicate(imDateAttData, userLog);

    for (DateAttData dateAttData : imDateAttData) {
      DateAT dateAt = createOrLoadAt(dateAttData.getName(), dateAttData.getOldName(), userLog, DateAT.class);
      if (dateAt == null) {
        continue;
      }
      userLog.info("[{0}] Saving attribute: {1}", dateAttData.getName().getCellRef(), dateAt);

      dateAt.setDescription(dateAttData.getDescription().getAttributeValue());
      dateAt.setMandatory(parseBoolean(dateAttData.getMandatory()));
      setGroup(dateAttData, dateAt, userLog);
      setActivatedTypes(dateAttData, dateAt, userLog);
      attributeTypeService.saveOrUpdate(dateAt);
    }
  }

  /**
   * imports all text attributes.
   * @param userLog the processing log for saving import messages
   * @param enumData list of attribute data, to import
   */
  private void importTextAttributes(ProcessingLog userLog, List<TextAttData> imTextAttData) {
    userLog.info("Saving Text Attributes");
    checkForDuplicate(imTextAttData, userLog);

    for (TextAttData textAttData : imTextAttData) {
      TextAT textAt = createOrLoadAt(textAttData.getName(), textAttData.getOldName(), userLog, TextAT.class);
      if (textAt == null) {
        continue;
      }
      userLog.info("[{0}] Saving attribute: {1}", textAttData.getName().getCellRef(), textAt);

      textAt.setDescription(textAttData.getDescription().getAttributeValue());
      textAt.setMandatory(parseBoolean(textAttData.getMandatory()));
      textAt.setMultiline(parseBoolean(textAttData.getMultiline()));
      setGroup(textAttData, textAt, userLog);
      setActivatedTypes(textAttData, textAt, userLog);
      attributeTypeService.saveOrUpdate(textAt);
    }
  }

  /**
   * This method will add, update or rename the enum values for the attribute. If a value can't be added, an error will be logged.
   * @param enumAttData contains informations about the new values.
   * @param enumAt the attribute, which should be updated.
   * @param userLog the processing log for saving import messages
   */
  private void setEnumValues(EnumAttData enumAttData, EnumAT enumAt, ProcessingLog userLog) {

    // logger output
    if (enumAttData.getValues().size() > 0) {
      List<String> valueStrings = Lists.newArrayList();
      for (EnumAttributeValueData valueData : enumAttData.getValues()) {
        CellValueHolder name = valueData.getName();
        if (valueStrings.contains(name.getAttributeValue())) {
          userLog.warn("[{0}] This attribute value is twice: {1}", name.getCellRef(), name.getAttributeValue());
        }
        valueStrings.add(name.getAttributeValue());
      }
      String allValuesString = listToString(valueStrings);
      userLog.info("saving values: {0}", allValuesString);
    }

    Set<EnumAV> actualValues = enumAt.getAttributeValues();
    Map<String, EnumAV> actualValuesMap = Maps.newHashMap();
    for (EnumAV enumAV : actualValues) {
      actualValuesMap.put(enumAV.getName(), enumAV);
    }

    for (EnumAttributeValueData valueData : enumAttData.getValues()) {
      EnumAV enumAv = createOrFindEnumAv(actualValuesMap, valueData.getName(), valueData.getOldName(), userLog);
      if (enumAv == null) {
        continue;
      }
      enumAv.setDescription(valueData.getDescription().getAttributeValue());
      if (enumAv.getAttributeType() == null) {
        enumAv.setAttributeTypeTwoWay(enumAt);
      }
      attributeValueService.saveOrUpdate(enumAv);
    }
  }

  /**
   * This method will add or update the responsibility values (only users) for the attribute. If a value can't be added, an error will be logged.
   * @param responsibilityAttData contains informations about the new values.
   * @param responsibilityAt the attribute, which should be updated.
   * @param userLog the processing log for saving import messages
   */
  private void setUsers(ResponsibilityAttData responsibilityAttData, ResponsibilityAT responsibilityAt, ProcessingLog userLog) {
    String cellRef = responsibilityAttData.getUsers().getCellRef();

    // logger output
    List<String> users = toList(responsibilityAttData.getUsers());
    if (users.size() > 0) {
      String allValuesString = listToString(users);
      userLog.info("[{0}] saving user values: {1}", cellRef, allValuesString);
    }

    Set<String> connectedUsers = Sets.newHashSet();
    for (ResponsibilityAV av : responsibilityAt.getAttributeValues()) {
      if (av.getUserEntity() instanceof User) {
        connectedUsers.add(av.getName());
      }
    }

    for (String user : users) {
      if (connectedUsers.contains(user)) {
        userLog.info("[{0}] Value with user \"{1}\" already exists. Nothing to do.", cellRef, user);
        continue;
      }
      ResponsibilityAV responsibilityAV = createResponsibilityUserAv(user, cellRef, userLog);
      connectedUsers.add(user);
      responsibilityAV.addAttributeTypeTwoWay(responsibilityAt);
      attributeValueService.saveOrUpdate(responsibilityAV);
    }
  }

  /**
   * This method will add or update the responsibility values (only user groups) for the attribute. If a value can't be added, an error will be logged.
   * @param responsibilityAttData contains informations about the new values.
   * @param responsibilityAt the attribute, which should be updated.
   * @param userLog the processing log for saving import messages
   */
  private void setUserGroups(ResponsibilityAttData responsibilityAttData, ResponsibilityAT responsibilityAt, ProcessingLog userLog) {
    String cellRef = responsibilityAttData.getUserGroups().getCellRef();

    // logger output
    List<String> userGroups = toList(responsibilityAttData.getUserGroups());
    if (userGroups.size() > 0) {
      String allValuesString = listToString(userGroups);
      userLog.info("[{0}] saving user group values: {1}", cellRef, allValuesString);
    }

    Set<String> connectedUserGroups = Sets.newHashSet();
    for (ResponsibilityAV av : responsibilityAt.getAttributeValues()) {
      if (av.getUserEntity() instanceof UserGroup) {
        connectedUserGroups.add(av.getName());
      }
    }

    for (String userGroup : userGroups) {
      if (connectedUserGroups.contains(userGroup)) {
        userLog.info("[{0}] Value with user \"{1}\" already exists. Nothing to do.", cellRef, userGroup);
        continue;
      }
      ResponsibilityAV responsibilityAV = createResponsibilityUserGroupAv(userGroup);
      if (responsibilityAV == null) {
        userLog.warn("[{0}] User group with name \"{1}\" not Found. Cant import.", cellRef, userGroup);
        continue;
      }
      connectedUserGroups.add(userGroup);
      responsibilityAV.addAttributeTypeTwoWay(responsibilityAt);
      attributeValueService.saveOrUpdate(responsibilityAV);
    }

  }

  /**
   * Creates a string with the values in the list, and removes the brackets.
   * @param strings the list to convert
   * @return one string representing the list
   */
  private String listToString(List<?> strings) {
    String listString = strings.toString();
    listString = listString.substring(1, listString.length() - 1);
    return listString;
  }

  /**
   * This method will set the activated building block types for the attribute. Building block types which are invalid or not
   * available for attributes will be ignored, and an error message will be logged.
   * @param attData contains informations about the new values.
   * @param at the attribute, which should be updated.
   * @param userLog the processing log for saving import messages
   */
  private void setActivatedTypes(AttData attData, AttributeType at, ProcessingLog userLog) {
    Set<BuildingBlockType> bbts = new HashSet<BuildingBlockType>();
    String cellRef = attData.getActive().getCellRef();
    for (String bbt : toSet(attData.getActive())) {
      BuildingBlockType bbtype = getBBType(bbt, cellRef, userLog);
      if (bbtype == null) {
        continue;
      }
      bbts.add(bbtype);
    }
    at.removeAllBuildingBlockTypesTwoWay();
    at.addBuildingBlockTypesTwoWay(bbts);
    attributeTypeService.saveOrUpdate(at);
  }

  /**
   * Returns the Building Block Type for the String.
   * @param bbt Building Block Type as String.
   * @param cellRef 
   * @param userLog the processing log for saving import messages
   * @return Building Block Type
   */
  private BuildingBlockType getBBType(String bbt, String cellRef, ProcessingLog userLog) {
    TypeOfBuildingBlock tobb;
    try {
      tobb = TypeOfBuildingBlock.valueOf(bbt.toUpperCase());
    } catch (IllegalArgumentException e) {
      userLog.error("[{0}] A BuildingBlockType for the value \"{1}\", does not exist.", cellRef, bbt);
      LOGGER.debug(e);
      return null;
    }

    if (tobb.equals(TypeOfBuildingBlock.DUMMY)) {
      userLog.error("[{0}] A BuildingBlockType for the value \"{1}\", does not exist.", cellRef, bbt);
      return null;
    }

    BuildingBlockType bbtype = buildingBlockTypeService.getBuildingBlockTypeByType(tobb);
    if (!bbtype.isAvailableForAttributes()) {
      userLog.error("[{0}] The BuildingBlockType \"{1}\", is not available for Attributes.", cellRef, bbtype.getName());
      return null;
    }

    return bbtype;
  }

  /**
   * This method returns a boolean for the string.
   * @param bool String to be parsed.
   * @return boolean
   */
  private boolean parseBoolean(CellValueHolder bool) {
    return (bool.getAttributeValue().equalsIgnoreCase("YES"));
  }

  /**
   * This method will set the Group for the attribute. If this field is empty, the group will be set to default.
   * @param attData contains informations about the new values.
   * @param at the attribute, which should be updated.
   * @param userLog the processing log for saving import messages
   */
  private void setGroup(AttData attData, AttributeType at, ProcessingLog userLog) {
    String group = attData.getGroupName().getAttributeValue();
    String cellRef = attData.getGroupName().getCellRef();

    if (StringUtils.isBlank(group)) {
      setStandardGroup(at);
      userLog.info("[{0}] Group field is blank. Set to default group.", cellRef);
    }
    else {
      List<AttributeTypeGroup> allGroups = attributeTypeGroupService.loadElementList();
      AttributeTypeGroup foundGroup = null;
      for (AttributeTypeGroup attributeTypeGroup : allGroups) {
        if (attributeTypeGroup.getName().equalsIgnoreCase(group)) {
          foundGroup = attributeTypeGroup;
        }
      }

      if (foundGroup != null) {
        at.removeAttributeTypeGroupTwoWay();
        at.setAttributeTypeGroupTwoWay(foundGroup);
      }
      else {
        setStandardGroup(at);
        userLog.warn("[{0}] Group not found \"{1}\". Set to default group.", cellRef, group);
      }
    }
  }

  /**
   * This method sets the StandardAttributeTypeGroup for the attribute
   * @param at the attribute, which should be updated.
   */
  private void setStandardGroup(AttributeType at) {
    AttributeTypeGroup standardAttributeTypeGroup = attributeTypeGroupService.getStandardAttributeTypeGroup();
    at.setAttributeTypeGroupTwoWay(standardAttributeTypeGroup);
  }

  /**
   * This method sets the bounds for NumberATs
   * @param numberAttData contains informations about the new values.
   * @param numberAt the attribute, which should be updated.
   * @param userLog the processing log for saving import messages
   */
  private void setBounds(NumberAttData numberAttData, NumberAT numberAt, ProcessingLog userLog) {
    CellValueHolder lowerBound = numberAttData.getLowerBound();
    CellValueHolder upperBound = numberAttData.getUpperBound();

    String low = lowerBound.getAttributeValue();
    BigDecimal lower = null;
    if (StringUtils.isNotBlank(low)) {
      try {
        lower = new BigDecimal(low);
      } catch (NumberFormatException e) {
        userLog.error("[{0}] Invalid formalt for lower bound: {1}", lowerBound.getCellRef(), low);
        LOGGER.debug(e);
      }
    }
    String up = upperBound.getAttributeValue();
    BigDecimal upper = null;
    if (StringUtils.isNotBlank(up)) {
      try {
        upper = new BigDecimal(up);
      } catch (NumberFormatException e) {
        userLog.error("[{0}] Invalid formalt for upper bound: {1}", upperBound.getCellRef(), up);
        LOGGER.debug(e);
      }
    }

    if (lower != null && upper != null && lower.compareTo(upper) > 0) {
      userLog.error("[{0}] The upper bound ({1}) is smaller than the lower bound ({2}). Cant set values", lowerBound.getCellRef(), upper, lower);
      lower = null;
      upper = null;
    }

    numberAt.setMinValue(lower);
    numberAt.setMaxValue(upper);
  }

  /**
  * This method sets RangeUniformyDistributed and User defined ranges, if range not uniform distributed.
  * @param numberAttData contains informations about the new values.
  * @param numberAt the attribute, which should be updated.
  * @param userLog the processing log for saving import messages
  */
  private void setRange(NumberAttData numberAttData, NumberAT numberAt, ProcessingLog userLog) {
    boolean uniform = parseBoolean(numberAttData.getUniform());
    numberAt.setRangeUniformyDistributed(uniform);

    if (!uniform) {
      Set<BigDecimal> userRangesToAdd = getBigDecimalRanges(numberAttData, userLog);
      for (RangeValue rv : Sets.newHashSet(numberAt.getRangeValues())) {
        BigDecimal value = rv.getValue();
        if (userRangesToAdd.contains(value)) {
          userRangesToAdd.remove(value);
        }
        else {
          numberAt.removeRangeValueTwoWays(rv.getId());
        }
      }

      for (BigDecimal range : userRangesToAdd) {
        RangeValue value = new RangeValue(numberAt, range);
        value.setAttributeTypeTwoWay(numberAt);
      }
    }
    else if (!numberAttData.getUserRanges().getAttributeValue().isEmpty()) {
      userLog.info("[{0}] The attribute is range uniform distributed. User defined ranges will be ignored.", numberAttData.getUniform().getCellRef());
    }

    attributeTypeService.saveOrUpdate(numberAt);
  }

  /**
   * Creates a set of BigDecimals with range values, and chose correct values from the attData for this list.
   * @param numberAttData contains informations about the new values.
   * @param userLog the processing log for saving import messages
   * @return Set with all correct values as bigDecimal
   */
  private Set<BigDecimal> getBigDecimalRanges(NumberAttData numberAttData, ProcessingLog userLog) {
    Set<BigDecimal> userRangesToAdd = Sets.newHashSet();
    String celLRef = numberAttData.getUserRanges().getCellRef();
    for (String rangeStr : toList(numberAttData.getUserRanges())) {

      BigDecimal value;
      try {
        value = new BigDecimal(rangeStr).setScale(2);
      } catch (NumberFormatException e) {
        userLog.error("[{0}] Invalid format for range value: {1}", celLRef, rangeStr);
        LOGGER.debug(e);
        continue;
      }

      if (userRangesToAdd.contains(value)) {
        userLog.warn("[{0}] Range value is twice: {1}", celLRef, value.toString());
      }

      if (userRangesToAdd.size() >= Constants.MAX_RANGELIST_SIZE) {
        userLog.error("[{0}] Cant save more than {1} value ranges. Skipping range with value \"{2}\".", celLRef,
            Integer.valueOf(Constants.MAX_RANGELIST_SIZE), value.toString());
        continue;
      }

      userRangesToAdd.add(value);
    }
    return userRangesToAdd;
  }

  private void checkForDuplicate(List<? extends AttData> attData, ProcessingLog userLog) {
    Set<String> dublicateCheck = Sets.newHashSet();
    for (AttData ad : attData) {
      String name = ad.getName().getAttributeValue();
      if (!dublicateCheck.add(name)) {
        userLog.warn("[{0}] This attribute is twice: {1}", ad.getName().getCellRef(), name);
      }
    }
  }

  /**
   * Loads the Entity by oldName (or name, if oldName is Blank). If the entity could not be
   * found, a new one will be created. The returned Entity will have a name. If any error
   * occurs, this method returns null.
   * @param name Name, or new Name of the entity
   * @param oldName Old name. (if blank the method behaves like oldName=name)
   * @param userLog the processing log for saving import messages
   * @return the loaded or created (and updated) entity
   */
  @SuppressWarnings("unchecked")
  private <T extends AttributeType> T createOrLoadAt(CellValueHolder name, CellValueHolder oldName, ProcessingLog userLog, Class<T> atClass) {
    String nameValue = name.getAttributeValue();
    String oldNameValue = oldName.getAttributeValue();
    String nameCellRef = name.getCellRef();
    String oldNameCellRef = oldName.getCellRef();

    if (StringUtils.isNotBlank(oldNameValue)) {
      AttributeType at = attributeTypeService.getAttributeTypeByName(oldNameValue);

      if (at == null) {
        userLog.error("[{0}] {2} attribute for name \"{1}\" not Found. Cant update.", nameCellRef, oldNameValue, atClass.getSimpleName());
        return null;
      }

      else if (!(at.getClass().isAssignableFrom(atClass))) {
        userLog.error("[{0}] \"{1}\" is not a {2} attribute. Cant update.", oldNameCellRef, oldNameValue, atClass.getSimpleName());
        return null;
      }

      if (attributeTypeService.doesObjectWithDifferentIdExist(null, nameValue)) {
        userLog.error("[{0}] {2} attribute for name \"{1}\" already exists. Cant update.", nameCellRef, nameValue, atClass.getSimpleName());
        return null;
      }
      else {
        at.setName(nameValue);
        userLog.info("[{0}] Rename {3} attribute from \"{1}\" to \"{2}\".", nameCellRef, oldNameValue, nameValue, atClass.getSimpleName());
        return (T) at;
      }
    }

    else {
      AttributeType at = attributeTypeService.getAttributeTypeByName(nameValue);
      if (at == null) {
        userLog.info("[{0}] {2} attribute for name \"{1}\" not Found. Creating new {2} attribute.", nameCellRef, nameValue, atClass.getSimpleName());
        T newAttributeType = createNewAttributeTypeInstance(userLog, atClass);
        if (newAttributeType == null) {
          return null;
        }
        newAttributeType.setName(nameValue);
        return newAttributeType;
      }
      else if (!(at.getClass().isAssignableFrom(atClass))) {
        userLog.error("[{0}] \"{1}\" is not a {2} attribute. Cant update.", nameCellRef, nameValue, atClass.getSimpleName());
        return null;
      }
      else {
        return (T) at;
      }
    }
  }

  /**
   * Creates a new AttributeType instance with the type of the class.
   * @param userLog the processing log for saving import messages
   * @param atClass type for the attribute type
   * @return new AttributeType, or null
   */
  private <T> T createNewAttributeTypeInstance(ProcessingLog userLog, Class<T> atClass) {
    T newAttributeType;
    try {
      newAttributeType = atClass.newInstance();
    } catch (InstantiationException e) {
      userLog.error(e.getMessage());
      return null;
    } catch (IllegalAccessException e) {
      userLog.error(e.getMessage());
      return null;
    }
    return newAttributeType;
  }

  /**
   * Loads the Entity by oldName (or name, if oldName is Blank). If the entity could not be
   * found, a new one will be created. The returned Entity will have a name. If any error
   * occurs, this method returns null.
   * @param name Name, or new Name of the entity
   * @param oldName Old name. (if blank the method behaves like oldName=name)
   * @param userLog the processing log for saving import messages
   * @return the loaded or created (and updated) entity
   */
  private EnumAV createOrFindEnumAv(Map<String, EnumAV> actualValuesMap, CellValueHolder name, CellValueHolder oldName, ProcessingLog userLog) {
    EnumAV enumAV;
    String nameValue = name.getAttributeValue();
    String oldNameValue = oldName.getAttributeValue();
    String nameCellRef = name.getCellRef();
    String oldNameCellRef = oldName.getCellRef();

    if (StringUtils.isNotBlank(oldNameValue)) {
      enumAV = actualValuesMap.get(oldNameValue);

      if (enumAV == null) {
        userLog.error("[{0}] Enum value for name \"{1}\" not Found. Cant update.", oldNameCellRef, oldNameValue);
        return null;
      }
      else if (actualValuesMap.containsKey(nameValue)) {
        userLog.error("[{0}] Enum value for name \"{1}\" already exists. Cant update.", nameCellRef, nameValue);
        return null;
      }
      else {
        enumAV.setName(nameValue);
        actualValuesMap.remove(oldNameValue);
        userLog.info("[{0}] Rename enum value from \"{1}\" to \"{2}\".", nameCellRef, oldNameValue, nameValue);
      }
    }

    else {
      enumAV = actualValuesMap.get(nameValue);
      if (enumAV == null) {
        userLog.info("[{0}] Enum value for name \"{1}\" not Found. Creating new enum value.", nameCellRef, nameValue);
        enumAV = new EnumAV();
        enumAV.setName(nameValue);
      }
    }

    actualValuesMap.put(nameValue, enumAV);
    return enumAV;
  }

  /**
   * Creates a new responsibility user value, if not existing. Also creates a new user, if not existing.
   * @param user User name
   * @param userLog the processing log for saving import messages
   * @return new responsibility value, with user
   */
  private ResponsibilityAV createResponsibilityUserAv(String user, String cellRef, ProcessingLog userLog) {
    User userEntity;
    if (userService.doesObjectWithDifferentIdExist(null, user)) {
      userLog.info("[{0}] Responsibility value with user \"{1}\" not Found. Creating new responsibility value.", cellRef, user);
      userEntity = userService.getUserByLoginIfExists(user);
    }
    else {
      userLog.info("[{0}] User with name \"{1}\" not Found. Creating new user.", cellRef, user);
      userEntity = new User();
      userEntity.setFirstName(user);
      userEntity.setLastName(user);
      userEntity.setLoginName(user);
      userService.saveOrUpdate(userEntity);
    }

    ResponsibilityAV responsibilityAV = new ResponsibilityAV();
    responsibilityAV.setUserEntity(userEntity);
    return responsibilityAV;
  }

  /**
   * Creates a new responsibility user group value, or returns null, if the user group not exists.
   * @param userGroup User name
   * @return new responsibility value, with user
   */
  private ResponsibilityAV createResponsibilityUserGroupAv(String userGroup) {
    UserGroup userGroupEntity;
    userGroupEntity = findUserGroup(userGroup);
    if (userGroupEntity == null) {
      return null;
    }
    ResponsibilityAV responsibilityAV = new ResponsibilityAV();
    responsibilityAV.setUserEntity(userGroupEntity);
    return responsibilityAV;
  }

  /**
   * Searches for a user group with the given name.
   * @param userGroup name of the user group
   * @return the user group, if exists, else null
   */
  private UserGroup findUserGroup(String userGroup) {
    List<UserGroup> allUserGroups = userGroupService.loadElementList();
    for (UserGroup ug : allUserGroups) {
      if (ug.getName().equals(userGroup)) {
        return ug;
      }
    }
    return null;
  }

  /**
   * creates a new List with string, splitted by ';'
   * @param cellValueHolder
   * @return new List
   */
  public static List<String> toList(CellValueHolder cellValueHolder) {
    String content = cellValueHolder.getAttributeValue();
    String[] splittedArray = ExcelImportUtilities.getSplittedArray(content, ExcelSheet.IN_LINE_SEPARATOR.trim());
    List<String> strListFiltered = Lists.newArrayList();
    for (String str : splittedArray) {
      if (StringUtils.isNotBlank(str)) {
        strListFiltered.add(str);
      }
    }
    return strListFiltered;
  }

  /**
   * creates a new Set with string, splitted by ';'
   * @param cellValueHolder
   * @return new Set
   */
  public static Set<String> toSet(CellValueHolder cellValueHolder) {
    return Sets.newHashSet(toList(cellValueHolder));
  }

  public void setAttributeTypeGroupService(AttributeTypeGroupService attributeTypeGroupService) {
    this.attributeTypeGroupService = attributeTypeGroupService;
  }

  public void setBuildingBlockTypeService(BuildingBlockTypeService buildingBlockTypeService) {
    this.buildingBlockTypeService = buildingBlockTypeService;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  public void setAttributeValueService(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public void setUserGroupService(UserGroupService userGroupService) {
    this.userGroupService = userGroupService;
  }
}
