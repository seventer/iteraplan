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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.query.QueryTreeGenerator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Property;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.BBAttributeComparator;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.MultiassignementType;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;
import de.iteratec.iteraplan.model.attribute.TypeOfAttribute;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.sorting.BuildingBlockSortHelper;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.QueryDAO;


/**
 * A service for querying the {@link BuildingBlock} instances and retrieving the attributes, 
 * required for filtering.
 */
public class QueryServiceImpl implements QueryService {

  private AttributeValueService       attributeValueService;
  private AttributeTypeDAO            attributeTypeDAO;
  private GeneralBuildingBlockService generalBuildingBlockService;
  private QueryDAO                    queryDAO;
  private AttributeTypeService        attributeTypeService;
  private TimeseriesService           timeseriesService;

  private static final Set<String>    USERDEF_ATTRIBUTE_TYPES = ImmutableSet.of(BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE,
                                                                  BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE,
                                                                  BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE,
                                                                  BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE);

  /** {@inheritDoc} */
  public int[] determineLevels(List<? extends BuildingBlock> elements) {
    int[] result = new int[] { 1, 1 };
    // reload all elements
    List<BuildingBlock> refreshedElements = generalBuildingBlockService.refreshBuildingBlocks(elements);
    if (refreshedElements.isEmpty() || !(refreshedElements.get(0) instanceof HierarchicalEntity)) {
      // undefined or no hierarchy. return only one level.
      return result;
    }

    // find levels
    int topLevel = 1000;
    int bottomLevel = 1;
    for (BuildingBlock bb : refreshedElements) {
      HierarchicalEntity<?> hbb = (HierarchicalEntity<?>) bb;
      int level = hbb.getLevel();
      if (topLevel > level) {
        topLevel = level;
      }
      if (bottomLevel < level) {
        bottomLevel = level;
      }
    }
    result[0] = topLevel;
    result[1] = bottomLevel;
    return result;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T extends BuildingBlock> List<T> evaluateQueryTree(final Node rootNode, TimeseriesQuery tsQuery,
                                                             final List<AbstractPostprocessingStrategy<T>> postprocessingStrategies) {

    UserContext.getCurrentPerms().assureAnyFunctionalPermission(
        new TypeOfFunctionalPermission[] { TypeOfFunctionalPermission.TABULAR_REPORTING, TypeOfFunctionalPermission.GRAPHICAL_REPORTING,
            TypeOfFunctionalPermission.ELEMENT_SPECIFIC_PERMISSION, TypeOfFunctionalPermission.MASSUPDATE });

    Set<T> resultSet = (Set<T>) queryDAO.evaluateQueryTree(rootNode);

    // filter by timeseries criterion
    if (tsQuery != null && resultSet.size() > 0 && tsQuery.isValid()) {
      QPart qpart = tsQuery.getPart();
      String stringID = qpart.getChosenAttributeStringId();
      BBAttribute attribute = tsQuery.getBBAttributeByStringId(stringID);
      AttributeType at = attributeTypeService.loadObjectById(attribute.getId());
      Map<? extends BuildingBlock, Timeseries> timeseries = timeseriesService.loadTimeseriesForBuildingBlocks(resultSet, at);

      for (Iterator<? extends BuildingBlock> it = resultSet.iterator(); it.hasNext();) {
        BuildingBlock bb = it.next();
        Timeseries ts = timeseries.get(bb);
        if (!tsQuery.evaluate(at.getTypeOfAttribute(), ts)) {
          it.remove();
        }
      }
    }

    if (postprocessingStrategies != null) {
      for (AbstractPostprocessingStrategy<T> strategy : postprocessingStrategies) {
        resultSet = strategy.process(resultSet, rootNode);
      }
    }

    List<T> resultList = new ArrayList<T>(resultSet);
    BuildingBlockSortHelper.sortByDefault(resultList);

    return resultList;
  }

  /** {@inheritDoc} */
  public List<String> getAttributeValuesForAttribute(Type<?> type, BBAttribute bbAttribute) {
    List<String> attributeValues = new ArrayList<String>();
    if (USERDEF_ATTRIBUTE_TYPES.contains(bbAttribute.getType())) {
      attributeValues = attributeValueService.getAllAVStrings(bbAttribute.getId());
    }
    else if (BBAttribute.FIXED_ATTRIBUTE_ENUM.equals(bbAttribute.getType())) {
      for (Object d : bbAttribute.getEnumClass().getEnumConstants()) {
        attributeValues.add(d.toString());
      }
    }
    else if ((BBAttribute.FIXED_ATTRIBUTE_TYPE.equals(bbAttribute.getType()) && !(bbAttribute.getDbName().equalsIgnoreCase("description")))) {
      // for the attribute "description" the attributeValuesList should be empty
      // because the drop down list is too narrow and it is difficult to evaluate descriptions with
      // line breaks and links
      attributeValues = queryDAO.getAttributeValuesForFixedAttribute(type, bbAttribute.getDbName());
    }
    else if ((BBAttribute.FIXED_ATTRIBUTE_SET).equals(bbAttribute.getType())) {
      attributeValues = queryDAO.getSetAttribute(type, bbAttribute.getDbName());
    }

    return attributeValues;
  }

  /** {@inheritDoc} */
  public List<BBAttribute> getFixedAndUserdefAttributesForBBType(Type<?> type) {
    List<BBAttribute> bbAttributes = getUserdefAttributesForBBType(type);
    addFixedAttributeTypes(type, bbAttributes);

    return bbAttributes;
  }

  /** {@inheritDoc} */
  public List<BBAttribute> getUserdefAttributesForBBType(final Type<?> bbType, final AttributeTypeGroupPermissionEnum permission) {
    List<BBAttribute> userdefAttrs = getUserdefAttributesForBBType(bbType);

    // Iterate over the list an filter out these attributes the current user
    // has no sufficient permissions on
    for (Iterator<BBAttribute> iter = userdefAttrs.iterator(); iter.hasNext();) {
      // find out the AttributeTypeGroup of the BBAttribute
      BBAttribute anAttribute = iter.next();
      AttributeType attrType = attributeTypeDAO.loadObjectById(anAttribute.getId());
      AttributeTypeGroup attrTypeGroup = attrType.getAttributeTypeGroup();

      // check whether the current user has sufficient permissions
      if (!UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(attrTypeGroup, permission)) {
        // user has not sufficient permissions, kick out the attribute from the list!
        iter.remove();
      }
    }

    return userdefAttrs;
  }

  public void setAttributeTypeDAO(AttributeTypeDAO attributeTypeDAO) {
    this.attributeTypeDAO = attributeTypeDAO;
  }

  public void setAttributeValueService(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  public void setGeneralBuildingBlockService(GeneralBuildingBlockService generalBuildingBlockService) {
    this.generalBuildingBlockService = generalBuildingBlockService;
  }

  public void setQueryDAO(QueryDAO queryDAO) {
    this.queryDAO = queryDAO;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  /**
   * @return timeseriesService the timeseriesService
   */
  public TimeseriesService getTimeseriesService() {
    return timeseriesService;
  }

  public void setTimeseriesService(TimeseriesService timeseriesService) {
    this.timeseriesService = timeseriesService;
  }

  /**
   * Adds all 'fixed properties' of the given Type to the List of BBAttributes
   * 
   * @param type
   *          the type of the building block whose fixed attributes will be added to the given list
   * @param bbAttributes
   *          a List of BBAttribute objects, this List will be modified by the method
   */
  private void addFixedAttributeTypes(final Type<?> type, List<BBAttribute> bbAttributes) {
    int index = 0;
    for (Property prop : type.getProperties()) {
      if (prop.getNameDB().equals("lastModificationTime")) {
        bbAttributes.add(index++, new BBAttribute(null, BBAttribute.FIXED_ATTRIBUTE_DATETYPE, prop.getNamePresentationKey(), prop.getNameDB()));
      }
      else if (prop.getNameDB().equals("subscribedUsers")) {
        bbAttributes.add(index++, new BBAttribute(null, BBAttribute.FIXED_ATTRIBUTE_SET, prop.getNamePresentationKey(), prop.getNameDB()));
      }
      else if (prop.getNameDB().toLowerCase().contains("direction")) {
        BBAttribute attribute = new BBAttribute(null, BBAttribute.FIXED_ATTRIBUTE_ENUM, prop.getNamePresentationKey(), prop.getNameDB());
        attribute.setEnumClass(Direction.class);
        bbAttributes.add(index++, attribute);
      }
      else {
        bbAttributes.add(index++, new BBAttribute(null, BBAttribute.FIXED_ATTRIBUTE_TYPE, prop.getNamePresentationKey(), prop.getNameDB()));
      }
    }
  }

  /** {@inheritDoc} */
  public List<BBAttribute> convertToBBAttributes(final List<AttributeType> attributeTypes) {
    List<BBAttribute> bbAttributes = new ArrayList<BBAttribute>();
    for (AttributeType at : attributeTypes) {
      BBAttribute bbAttr = null;
      if (at instanceof EnumAT) {
        bbAttr = new BBAttribute(at.getId(), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, at.getName(), null);
      }
      else if (at instanceof NumberAT) {
        bbAttr = new BBAttribute(at.getId(), BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE, at.getName(), null);
      }
      else if (at instanceof TextAT) {
        bbAttr = new BBAttribute(at.getId(), BBAttribute.USERDEF_TEXT_ATTRIBUTE_TYPE, at.getName(), null);
      }
      else if (at instanceof DateAT) {
        bbAttr = new BBAttribute(at.getId(), BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE, at.getName(), null);
      }
      else if (at instanceof ResponsibilityAT) {
        bbAttr = new BBAttribute(at.getId(), BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE, at.getName(), null);
      }
      else {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
      if (at instanceof MultiassignementType) {
        // Only elements of this interface can be assigned to multiple values:
        bbAttr.setIsMultiValue(((MultiassignementType) at).isMultiassignmenttype());
      }
      if (at instanceof TimeseriesType) {
        bbAttr.setIsTimeseries(((TimeseriesType) at).isTimeseries());
      }

      UserContext uc = UserContext.getCurrentUserContext();
      if (uc.getPerms().userHasAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ_WRITE)) {
        bbAttr.setHasWritePermissions(true);
      }

      bbAttributes.add(bbAttr);
    }
    Collections.sort(bbAttributes, new BBAttributeComparator());

    return bbAttributes;
  }

  /** {@inheritDoc} */
  public List<BBAttribute> getUserdefAttributesForBBType(Type<?> type) {
    TypeOfBuildingBlock typeOfBB = TypeOfBuildingBlock.fromPropertyString(type.getTypeNamePresentationKey());
    TypeOfBuildingBlock typeOfBb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(typeOfBB.toString());
    List<AttributeType> attributes = attributeTypeDAO.getAttributeTypesForTypeOfBuildingBlock(typeOfBb, true);

    List<AttributeType> permittedAttributes = filterAttributeListByPermission(attributes);

    return convertToBBAttributes(permittedAttributes);
  }

  /** {@inheritDoc} */
  public List<BBAttribute> getBBAttributesForGraphicalExport(TypeOfBuildingBlock type) {
    TypeOfBuildingBlock newType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(type.toString());

    List<AttributeType> allAttributeTypes = attributeTypeDAO.getAttributeTypesForTypeOfBuildingBlock(newType, true);
    ArrayList<AttributeType> attributes = new ArrayList<AttributeType>();
    Set<TypeOfAttribute> requiredTypes = Sets.newHashSet(TypeOfAttribute.ENUM, TypeOfAttribute.RESPONSIBILITY, TypeOfAttribute.NUMBER);
    for (AttributeType attributeType : allAttributeTypes) {
      if (requiredTypes.contains(attributeType.getTypeOfAttribute())) {
        attributes.add(attributeType);
      }
    }

    List<AttributeType> permittedAttributes = filterAttributeListByPermission(attributes);
    List<BBAttribute> bbAttributes = convertToBBAttributes(permittedAttributes);

    if ((newType == TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE) || (newType == TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE)) {
      bbAttributes.add(0, new BBAttribute(Integer.valueOf(0), BBAttribute.FIXED_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_TYPEOFSTATUS,
          Constants.ATTRIBUTE_TYPEOFSTATUS));
    }

    if (newType == TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE) {
      bbAttributes.add(1, new BBAttribute(Integer.valueOf(-11), BBAttribute.FIXED_ATTRIBUTE_TYPE, "seal", "seal"));
    }

    return bbAttributes;
  }

  private static List<AttributeType> filterAttributeListByPermission(List<AttributeType> attributes) {
    List<AttributeType> permittedAttributes = Lists.newArrayList();

    for (AttributeType attributeType : attributes) {
      AttributeTypeGroup attributeTypeGroup = attributeType.getAttributeTypeGroup();
      if (UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(attributeTypeGroup, AttributeTypeGroupPermissionEnum.READ)) {
        permittedAttributes.add(attributeType);
      }
    }

    return permittedAttributes;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public <T extends BuildingBlock> List<AbstractPostprocessingStrategy<T>> disposeOfWildcard(List<AbstractPostprocessingStrategy<? extends BuildingBlock>> list,
                                                                                             Class<T> returnType) {

    List<AbstractPostprocessingStrategy<T>> result = new ArrayList<AbstractPostprocessingStrategy<T>>();
    for (AbstractPostprocessingStrategy<? extends BuildingBlock> element : list) {
      result.add((AbstractPostprocessingStrategy<T>) element);
    }

    return result;
  }

  /** {@inheritDoc} */
  public <E extends ManageReportMemoryBean> void requestEntityList(E memBean, ReportType expectedReportType) {
    checkReportTypeHelper(memBean, expectedReportType);
    QueryResult queryResult = memBean.getQueryResult();

    if (queryResult == null) {
      memBean.setLoadedFromSavedQuery(false);
      return;
    }

    // Generate the tree of nodes.
    QueryTreeGenerator qtg = new QueryTreeGenerator(UserContext.getCurrentLocale(), attributeTypeService);
    Node node = qtg.generateQueryTree(queryResult.getQueryForms());

    Type<?> reportType = memBean.getReportResultType();

    List<AbstractPostprocessingStrategy<? extends BuildingBlock>> strategies = queryResult.getSelectedPostProcessingStrategies();

    // Add a post-processing strategy in case an ordered hierarchy has been queried.
    if (reportType.isOrderedHierarchy()) {
      // remove virtual top level elements
      strategies.add(reportType.getOrderedHierarchyRemoveRootElementStrategy());
    }
    // Due to the inconsistent usage of generics the list must be processed to dispose of the
    // wildcard.
    List<AbstractPostprocessingStrategy<BuildingBlock>> postProcStrategies = disposeOfWildcard(strategies, BuildingBlock.class);

    List<BuildingBlock> results = evaluateQueryTree(node, queryResult.getTimeseriesQuery(), postProcStrategies);
    memBean.setResults(results);
    boolean resultsAreLoadedFromFile = (memBean.isLoadedFromSavedQuery() && (queryResult.getSelectedResultIds().length > 0));

    if (!resultsAreLoadedFromFile) {
      queryResult.setSelectedResultIds(GeneralHelper.createIdArrayFromIdEntities(results));
    }
    memBean.setLoadedFromSavedQuery(false);
  }

  /**
   * Helper method for checking the correct report type.
   * If the mem bean's report type is not equal to the key, a technical exception is thrown.
   * 
   * @param memBean The backing memory bean.
   * @param key The corresponding key of the application ressources (e.g. Constants.REPORTS_EXPORT_GRAPHICAL_LANDSCAPE)
   */
  private <E extends ManageReportMemoryBean> void checkReportTypeHelper(E memBean, ReportType key) {
    String reportType = memBean.getReportType().getValue();
    if (!reportType.equals(key.getValue())) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

}