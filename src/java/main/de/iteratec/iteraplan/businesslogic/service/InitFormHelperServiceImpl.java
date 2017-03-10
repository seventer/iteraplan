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

import static de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory.getRefreshHelperService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.common.BuildingBlockRelationMapping;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeRangeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.MixedColorCodingDecorator;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.MixedColorCodingDecorator.ContinuousColorCodingDecorator;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.RecursiveCluster;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ViewConfiguration;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterSecondOrderBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Line.LineOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.TimelineFeature;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.BarsOrderMethod;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.DiagramKeyType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.DiagramType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesSource;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.SingleBarOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Portfolio.PortfolioOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.vbb.VbbOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QFirstLevel;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QUserInput;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemInterfaceTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateTypeHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.type.QueryTypeHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.SealState;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;
import de.iteratec.iteraplan.model.attribute.TypeOfAttribute;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.queries.SavedQueryEntityInfo;
import de.iteratec.iteraplan.model.user.PermissionHelper;
import de.iteratec.iteraplan.model.xml.LandscapeDiagramXML;
import de.iteratec.iteraplan.model.xml.ReportXML;
import de.iteratec.iteraplan.model.xml.ReportXML.ReportTypeXML;
import de.iteratec.iteraplan.model.xml.query.ClusterOptionsXML;
import de.iteratec.iteraplan.model.xml.query.ColorOptionsXML;
import de.iteratec.iteraplan.model.xml.query.ColumnEntryXML;
import de.iteratec.iteraplan.model.xml.query.EnumAttributeXML;
import de.iteratec.iteraplan.model.xml.query.InformationFlowOptionsXML;
import de.iteratec.iteraplan.model.xml.query.KeyValueXML;
import de.iteratec.iteraplan.model.xml.query.LineOptionsXML;
import de.iteratec.iteraplan.model.xml.query.MasterplanOptionsXML;
import de.iteratec.iteraplan.model.xml.query.MasterplanRowTypeOptionsXML;
import de.iteratec.iteraplan.model.xml.query.PieBarOptionsXML;
import de.iteratec.iteraplan.model.xml.query.PortfolioOptionsXML;
import de.iteratec.iteraplan.model.xml.query.PostProcessingAdditionalOptionsXML;
import de.iteratec.iteraplan.model.xml.query.PostProcessingStrategiesXML;
import de.iteratec.iteraplan.model.xml.query.PostProcessingStrategyXML;
import de.iteratec.iteraplan.model.xml.query.QueryFormXML;
import de.iteratec.iteraplan.model.xml.query.QueryResultXML;
import de.iteratec.iteraplan.model.xml.query.SingleBarColorOptionsXML;
import de.iteratec.iteraplan.model.xml.query.TimelineFeatureXML;
import de.iteratec.iteraplan.model.xml.query.VbbOptionsXML;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


/**
 * Initializes {@link DynamicQueryFormData} and new {@link ManageReportMemoryBean}s for different
 * BuildingBlock types.
 */
@SuppressWarnings({ "PMD.ExcessiveImports", "PMD.TooManyMethods" })
public class InitFormHelperServiceImpl implements InitFormHelperService {

  private static final Logger         LOGGER = Logger.getIteraplanLogger(InitFormHelperServiceImpl.class);

  private QueryService                queryService;
  private SavedQueryService           savedQueryService;
  private AttributeValueService       attributeValueService;
  private AttributeTypeService        attributeTypeService;
  private GeneralBuildingBlockService generalBuildingBlockService;
  private DateIntervalService         dateIntervalService;
  private RefreshHelperService        refreshHelper;

  public void setRefreshHelper(RefreshHelperService refreshHelper) {
    this.refreshHelper = refreshHelper;
  }

  // ok without permissions, because permissions will be checked in query service, if necessary
  /** {@inheritDoc} */
  public List<BBAttribute> getUserdefAttributes(Type<?> type) {
    List<BBAttribute> availableAttributes = queryService.getUserdefAttributesForBBType(type);
    availableAttributes.add(0, new BBAttribute(null, BBAttribute.BLANK_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_BLANK, null));

    return availableAttributes;
  }

  /** {@inheritDoc} */
  public List<BBAttribute> getAvailableUserDefAttributes(Type<?> bbType, AttributeTypeGroupPermissionEnum permission) {
    List<BBAttribute> availableAttributes = queryService.getUserdefAttributesForBBType(bbType, permission);
    availableAttributes.add(0, new BBAttribute(null, BBAttribute.BLANK_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_BLANK, null));

    return availableAttributes;
  }

  /** {@inheritDoc} */
  public void setViewConfiguration(ManageReportMemoryBean memBean, TypeOfBuildingBlock type) {
    ViewConfiguration config = new ViewConfiguration(type, UserContext.getCurrentLocale());
    config.addAttributeTypesToView(attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(type, false));
    memBean.setViewConfiguration(config);
  }

  /** {@inheritDoc} */
  public ManageReportMemoryBean getInitializedReportMemBeanByDialogPerms(Permissions currentPerms) {
    for (Dialog dialog : Dialog.getBbElementDialogs()) {
      String dialogName = dialog.getDialogName();
      if (currentPerms.getUserHasDialogPermission(dialogName)) {
        // check for classes with "Release" at the end of their class name
        if ("InformationSystem".equals(dialogName) || "TechnicalComponent".equals(dialogName)) {
          dialogName += "Release";
        }
        try {
          return getInitializedReportMemBeanByViewPerms(
              TypeOfBuildingBlock.typeOfBuildingBlockForClass(Class.forName("de.iteratec.iteraplan.model." + dialogName)).getPluralValue(),
              Constants.ALL_TYPES_FOR_DISPLAY);
        } catch (ClassNotFoundException cnfe) {
          throw new IteraplanTechnicalException(cnfe);
        }
      }
    }

    throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);

  }

  /** {@inheritDoc} */
  public void dropRestrictionsFromQueryForm(DynamicQueryFormData<?> queryForm) {
    if (queryForm.getQueryUserInput().getTimespanQueryData() != null) {
      queryForm.getQueryUserInput().getTimespanQueryData().setStartDateAsString("");
      queryForm.getQueryUserInput().getTimespanQueryData().setEndDateAsString("");
    }
    if (queryForm.getQueryUserInput().getStatusQueryData() != null) {
      Set<?> statusKeys = queryForm.getQueryUserInput().getStatusQueryData().getStatusMap().keySet();
      for (Object key : statusKeys) {
        queryForm.getQueryUserInput().getStatusQueryData().setStatus(key.toString(), Boolean.TRUE);
      }
    }
  }

  /** {@inheritDoc} */
  public ManageReportMemoryBean getInitializedReportMemBeanByWritePerms(Permissions currentPerms) {
    // first try to initialize with informationsystem, because thats the most wanted BBT
    if (currentPerms.getUserHasBbTypeUpdatePermission(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL)) {
      return getInitializedReportMemBeanByViewPerms(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.ALL_TYPES_FOR_DISPLAY);
    }

    for (String displayType : Constants.ALL_TYPES_FOR_DISPLAY) {
      if (currentPerms.getUserHasBbTypeUpdatePermission(displayType)) {
        return getInitializedReportMemBeanByViewPerms(displayType, Constants.ALL_TYPES_FOR_DISPLAY);
      }
    }

    throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
  }

  /** {@inheritDoc} */
  public ManageReportMemoryBean getInitializedReportMemBean(String tobString) {
    TypeOfBuildingBlock tob = TypeOfBuildingBlock.fromPropertyString(tobString);
    Type<?> queryType = QueryTypeHelper.getTypeObject(tob);
    DynamicQueryFormData<?> form = getReportForm(queryType);
    form.setMassUpdateType(MassUpdateTypeHelper.getMassUpdateType(tob));

    // if a ReportMemBean is requested for BusinessMappings we want to query for Informationsystems
    // but the massupdatetype should be BusinessMapping
    if (Constants.BB_BUSINESSMAPPING.equals(tobString)) {
      tob = TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;
      queryType = QueryTypeHelper.getTypeObject(tob);
      form = getReportForm(queryType);
      form.setMassUpdateType(MassUpdateTypeHelper.getMassUpdateType(TypeOfBuildingBlock.BUSINESSMAPPING));
    }

    ManageReportMemoryBean memBean = new ManageReportMemoryBean(form, getTimeseriesQuery(queryType));

    if (!Constants.BB_BUSINESSMAPPING.equals(tobString)) {
      memBean.getAvailableResultFormats().add(new ExportOption(Constants.REPORTS_EXPORT_HTML));
      memBean.getAvailableResultFormats().add(new ExportOption(Constants.REPORTS_EXPORT_EXCEL_2007));
      memBean.getAvailableResultFormats().add(new ExportOption(Constants.REPORTS_EXPORT_EXCEL_2003));
    }

    memBean.getAvailableResultFormats().add(new ExportOption(Constants.REPORTS_EXPORT_XMI));

    // elements which can be used within a masterplan diagram, i.e. have a temporal extent
    List<String> masterplanElements = Arrays.asList(new String[] { Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, Constants.BB_PROJECT_PLURAL,
        Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL });

    if (masterplanElements.contains(tobString)) {

      // for all three masterplan elements
      memBean.getAvailableResultFormats().add(new ExportOption(Constants.REPORTS_EXPORT_MSPROJECT_MSPDI));
      memBean.getAvailableResultFormats().add(new ExportOption(Constants.REPORTS_EXPORT_MSPROJECT_MPX));

      // for releases of information systems and technical components only
      if (!Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL.equals(tobString)) {
        memBean.getAvailableResultFormats().add(new ExportOption(Constants.REPORTS_EXPORT_MSPROJECT_MSPDI_INCLUDING_SUBS));

        memBean.getAvailableResultFormats().add(new ExportOption(Constants.REPORTS_EXPORT_MSPROJECT_MPX_INCLUDING_SUBS));
      }

      // for information system releases exclusively
      /*
       * For ticket ITERAPLAN-2544 the csv export is currently deactivated and will be removed in further versions of iteraplan
       */
      //      if (Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL.equals(tobString)) {
      //        memBean.getAvailableResultFormats().add(new ExportOption(Constants.REPORTS_EXPORT_CSV));
      //      }
    }

    ReportType sqt = ReportType.fromValue(queryType.getTypeOfBuildingBlock().toString());
    memBean.setSavedQueries(savedQueryService.getSavedQueriesWithoutContent(sqt));

    setViewConfiguration(memBean, tob);
    memBean.setSelectedBuildingBlock(tobString);

    return memBean;

  }

  /** {@inheritDoc} */
  public ManageReportMemoryBean getInitializedReportMemBeanByViewPerms(String requestedTobString, List<String> possibleTypes) {
    return getInitializedReportMemBean(getPermittedTobString(requestedTobString, possibleTypes));
  }

  /**
   * Returns {@code requestedTobString} if the user has view permission for the related building block type.
   * Otherwise returns the first type of building block string from {@link Constants#ALL_TYPES_FOR_DISPLAY} which
   * the user has view permission for.
   * If none of the available building block types is permitted, an exception is thrown.
   * @param requestedTobString
   *          String-representation of the building block type we want to have
   * @param possibleTypes
   *          List of string-representations of the building block types we can choose from
   * @return String-representation of a permitted building block type
   */
  private String getPermittedTobString(String requestedTobString, List<String> possibleTypes) {
    String associatedPermission = PermissionHelper.getAssociatedPermission(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(requestedTobString));
    if (possibleTypes.contains(requestedTobString) && PermissionHelper.hasPermissionFor(associatedPermission)) {
      return requestedTobString;
    }
    else {
      for (String displayType : possibleTypes) {
        associatedPermission = PermissionHelper.getAssociatedPermission(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(displayType));
        if (PermissionHelper.hasPermissionFor(associatedPermission)) {
          return displayType;
        }
      }
    }
    throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
  }

  /** {@inheritDoc} */
  public DynamicQueryFormData<?> getReportForm(IPresentationExtension complexExtension) {
    Type<?> type = complexExtension.getRequestedType();
    LOGGER.debug("complexExtension.getRequestedType(): " + type);

    if (type != null) {
      DynamicQueryFormData<?> form = getReportForm(type);
      List<Extension> secondStepExtensions = complexExtension.getSecondStepExtensions();
      if (secondStepExtensions != null) {
        for (IPresentationExtension secondStepExtension : secondStepExtensions) {
          if (secondStepExtension != null) {
            LOGGER.debug("secondStepExtension.getRequestedType(): {0}", secondStepExtension.getRequestedType());
            DynamicQueryFormData<?> secondStepForm = getReportForm(secondStepExtension.getRequestedType());
            secondStepForm.setExtension(secondStepExtension);
            form.addSecondLevelQueryForm(secondStepForm);
          }
          else {
            LOGGER.error("a secondStepExtension for type " + type.getTypeNameDB() + " was null and not added to the form!");
          }
        }
      }
      form.setExtension(complexExtension);
      return form;
    }
    else {
      return null;
    }
  }

  /** {@inheritDoc} */
  public <T extends BuildingBlock> DynamicQueryFormData<T> getReportForm(Type<T> type) {
    List<BBAttribute> availableAttributes = getAvailableAttributes(type);
    DynamicQueryFormData<T> form = new DynamicQueryFormData<T>(availableAttributes, type, UserContext.getCurrentLocale());

    List<BBAttribute> dimensionAttributes = getGraphicalExportOptionAttributes(type);
    form.setDimensionAttributes(dimensionAttributes);

    return form;
  }

  /**
   * Creates a list of all available attributes for a given {@link Type}. Attributes for which the
   * user does not have a read permission are not included in the list.
   * 
   * @param type
   *          the Type of the building block
   * @return a list of BBAttribute objects that represent the associated fixed and user defined
   *         attributes.
   */
  private List<BBAttribute> getAvailableAttributes(Type<?> type) {
    List<BBAttribute> availableAttributes = queryService.getFixedAndUserdefAttributesForBBType(type);
    availableAttributes.add(0, new BBAttribute(null, BBAttribute.BLANK_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_BLANK, null));
    availableAttributes.add(1, new BBAttribute(Integer.valueOf(0), BBAttribute.FIXED_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_TYPEOFSTATUS,
        Constants.ATTRIBUTE_TYPEOFSTATUS));
    availableAttributes.add(2, new BBAttribute(Integer.valueOf(-11), BBAttribute.FIXED_ATTRIBUTE_TYPE, "seal", "seal"));

    return availableAttributes;
  }

  /**
   * Special query for InformationFlowDiagram. Creates a list of all available possible attributes
   * for a given {@link Type}. Attributes for which the user does not have a read permission are not
   * included in the list.
   * 
   * @param type
   *          the Type of the building block
   * @return a list of BBAttribute objects that represent the associated fixed and user defined
   *         attributes.
   */
  private List<BBAttribute> getGraphicalExportOptionAttributes(Type<?> type) {
    TypeOfBuildingBlock typeOfBB = TypeOfBuildingBlock.fromPropertyString(type.getTypeNamePresentationKey());
    List<BBAttribute> availableAttributes = queryService.getBBAttributesForGraphicalExport(typeOfBB);
    availableAttributes.add(0, new BBAttribute(null, BBAttribute.BLANK_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_BLANK, null));

    return availableAttributes;
  }

  /** {@inheritDoc} */
  public List<DynamicQueryFormData<?>> getSavedReportForm(List<QueryFormXML> queryForms, Map<String, IPresentationExtension> availableExtensions) {
    List<DynamicQueryFormData<?>> dynamicQueryForms = new ArrayList<DynamicQueryFormData<?>>();
    LOGGER.debug("Reading data comming from XML into DynamicQueryFormData + Check if attributes referenced in the XML query still exist");

    for (QueryFormXML formXML : queryForms) {
      DynamicQueryFormData<?> dynamicQuery = getReportForm(formXML.getType());

      LOGGER.debug("Transforming the general form data");
      Locale locale = UserContext.getCurrentLocale();
      formXML.update(dynamicQuery, locale);

      String queriedBbType = formXML.getType().getTypeOfBuildingBlock().getPluralValue();
      // is the user allowed to access the bbType referenced in the query?
      if (!UserContext.getCurrentPerms().getUserHasBbTypeFunctionalPermission(queriedBbType)) {
        QUserInput userInput = dynamicQuery.getQueryUserInput();
        if (userInput.isUserInputAvailable() || (userInput.getNoAssignements() != null && userInput.getNoAssignements().booleanValue())) {
          // no permission to evaluate that query
          throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
        }
        else {
          // no permission is ok if there is no condition specified on that bbType anyway
          // ensure initialized forms anyway because of timeseries queries
          dynamicQueryForms.add(dynamicQuery);
          continue;
        }
      }

      LOGGER.debug("Updating the available extensions - extensions used in the XML query must not be selectable anymore");
      formXML.updateExtensions(dynamicQuery, availableExtensions);

      LOGGER.debug("Updating the business mappings: Queries are stored in secondLevelForms ");
      if (formXML.getSecondLevel().size() > 0) {
        Map<String, IPresentationExtension> availableReportExtensions = new HashMap<String, IPresentationExtension>(formXML.getType()
            .getExtensionsForPresentation());

        List<DynamicQueryFormData<?>> businesMappingQueries = getSavedReportForm(formXML.getSecondLevel(), availableReportExtensions);
        dynamicQuery.setSecondLevelQueryForms(businesMappingQueries);
      }

      LOGGER.debug("Treating the attributes -> set refereneces to existing attributes");
      Map<Integer, BBAttribute> attributeMap = new HashMap<Integer, BBAttribute>();
      BBAttribute blankAttribute = null;
      for (BBAttribute attribute : dynamicQuery.getAvailableAttributes()) {
        if (BBAttribute.BLANK_ATTRIBUTE_TYPE.equals(attribute.getType())) {
          blankAttribute = attribute;
        }
        else {
          attributeMap.put(attribute.getId(), attribute);
        }
      }

      changeAttributeReferenceMethodToId(dynamicQuery, attributeMap, blankAttribute);
      dynamicQueryForms.add(dynamicQuery);
    }
    return dynamicQueryForms;
  }

  /**
   * Attributes are marshalled referenced by name instead of by ID. Change back
   * to reference by ID (see BBAttribute.getStringIdName); special treatment for the blank attribute
   * @param dynamicQuery
   *          the query data
   * @param attributeMap
   *          Id-to-BBAttribute map
   * @param blankAttribute
   */
  private void changeAttributeReferenceMethodToId(DynamicQueryFormData<?> dynamicQuery, Map<Integer, BBAttribute> attributeMap,
                                                  BBAttribute blankAttribute) {
    // Attributes are marshalled referenced by name instead of by ID. Change back
    // to reference by ID (see BBAttribute.getStringIdName)
    // special treatment for the blank attribute
    for (QFirstLevel qfl : dynamicQuery.getQueryUserInput().getQueryFirstLevels()) {
      for (QPart queryPart : qfl.getQuerySecondLevels()) {
        String attrStringIdName = queryPart.getChosenAttributeStringId();

        boolean attrIsBlank = (BBAttribute.BLANK_ATTRIBUTE_TYPE.equals(BBAttribute.getTypeByStringId(attrStringIdName)))
            || StringUtils.isEmpty(attrStringIdName);
        if (attrIsBlank && blankAttribute != null) {
          LOGGER.debug("Set the blank attribute");
          queryPart.setChosenAttributeStringId(blankAttribute.getStringId());
        }
        else {
          getAttributeId(attrStringIdName, attributeMap, dynamicQuery.getType());
          LOGGER.debug("Setting the attribute {0} which could correctly be found", attrStringIdName);

          queryPart.setChosenAttributeStringId(attributeMap.get(BBAttribute.getIdByStringId(attrStringIdName)).getStringId());
        }
      }
    }
  }

  /** {@inheritDoc} */
  public void initLandscapeDiagram(List<BBAttribute> availableDimensionAttributes, List<BBAttribute> availableAttributes,
                                   LandscapeDiagramXML landscapeDiagramXML) {
    // convert the ids of the attributes from the stringId saved in the report to their actual id
    Type<?> contentType = landscapeDiagramXML.getContentType().getQueryType();
    Map<Integer, BBAttribute> dimAttributeMap = getBBAttributeMap(availableDimensionAttributes);

    EnumAttributeXML color = landscapeDiagramXML.getColor();
    if (color != null && StringUtils.isNotEmpty(color.getAttributeName())) {
      color.setAttributeId(getAttributeId(color.getAttributeName(), dimAttributeMap, contentType));
    }
    EnumAttributeXML lineType = landscapeDiagramXML.getLineType();
    if (lineType != null && StringUtils.isNotEmpty(lineType.getAttributeName())) {
      lineType.setAttributeId(getAttributeId(lineType.getAttributeName(), dimAttributeMap, contentType));
    }
  }

  private Map<Integer, BBAttribute> getBBAttributeMap(List<BBAttribute> attributes) {
    Map<Integer, BBAttribute> attributeMap = new HashMap<Integer, BBAttribute>();
    for (BBAttribute attribute : attributes) {
      attributeMap.put(attribute.getId(), attribute);
    }
    return attributeMap;
  }

  /** {@inheritDoc} */
  public InformationFlowOptionsBean initInformationFlowDiagramForm(InformationFlowOptionsXML informationFlowOptions) {
    InformationFlowOptionsBean bean = new InformationFlowOptionsBean();
    bean.setDialogStep(informationFlowOptions.getDialogStep());

    if (informationFlowOptions.getColorAttributeId() != null) {
      bean.getColorOptionsBean().setDimensionAttributeId(informationFlowOptions.getColorAttributeId());
    }
    bean.getColorOptionsBean().setUseColorRange(informationFlowOptions.isUseColorRange());

    if (informationFlowOptions.getLineTypeAttributeId() != null) {
      bean.getLineOptionsBean().setDimensionAttributeId(informationFlowOptions.getLineTypeAttributeId());
    }

    refreshGraphicalExportColorOptions(bean.getColorOptionsBean(), TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    refreshGraphicalExportLineTypeOptions(bean.getLineOptionsBean(), TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);

    bean.getColorOptionsBean()
        .matchValuesFromSavedQuery(informationFlowOptions.getColorAttributeValues(), informationFlowOptions.getSelectedColors());

    bean.getLineOptionsBean().matchValuesFromSavedQuery(informationFlowOptions.getLineAttributeValues(),
        informationFlowOptions.getSelectedLineTypes());

    final List<BBAttribute> lineCaptionAttributes = getUserdefAttributes(InformationSystemInterfaceTypeQu.getInstance());
    bean.setIsiAttributes(lineCaptionAttributes);

    bean.setUseNamesLegend(informationFlowOptions.isUseNamesLegend());
    bean.setShowSavedQueryInfo(informationFlowOptions.isShowSavedQueryInfo());
    bean.setSelectedGraphicFormat(informationFlowOptions.getSelectedGraphicFormat());
    bean.setSelectedNodeLayout(informationFlowOptions.getSelectedNodeLayout());

    bean.setSelectionType(informationFlowOptions.getEdgeCaptionTypes());
    bean.setLineCaptionSelectedAttributeId(Integer.valueOf(informationFlowOptions.getEdgeAttributeId()));

    bean.setShowIsBusinessObjects(informationFlowOptions.isShowIsBusinessObjects());
    bean.setShowIsBaseComponents(informationFlowOptions.isShowIsBaseComponents());

    return bean;
  }

  /** {@inheritDoc} */
  public MasterplanOptionsBean initMasterplanDiagramForm(MasterplanOptionsXML masterplanOptions) {
    MasterplanOptionsBean bean = new MasterplanOptionsBean();
    bean.setDialogStep(masterplanOptions.getDialogStep());

    masterplanOptions.update(bean, UserContext.getCurrentLocale());

    if (masterplanOptions.getLevel0Options() == null) {
      //compatibility mode -> information is loaded from the historic masterplan saved query structure
      String selectedBbType = StringUtils.isEmpty(masterplanOptions.getSelectedBbType()) ? Constants.BB_INFORMATIONSYSTEMRELEASE : masterplanOptions
          .getSelectedBbType();

      bean.setLevel0Options(createMasterplanRowType(null, "", selectedBbType, 0));
      bean.setSelectedBbType(selectedBbType);
      bean.getLevel0Options().setHierarchicalSort("hierarchical".equals(masterplanOptions.getSortMethod().toString()));

      if (!Constants.REPORTS_EXPORT_SELECT_RELATION.equals(masterplanOptions.getSelectedRelatedType())) {
        String relatedToBb = getRelatedTypeOfBuildingBlock(masterplanOptions.getSelectedRelatedType());
        bean.setLevel1Options(createMasterplanRowType(bean.getLevel0Options().getTypeOfBuildingBlock(), masterplanOptions.getSelectedRelatedType(),
            relatedToBb, 1));
        bean.setSelectedLevel1Relation(masterplanOptions.getSelectedRelatedType());
        bean.getLevel1Options().setBuildClosure(!masterplanOptions.isUseOnlyNeighbouringRelatedElements());
      }

      if (masterplanOptions.getColorAttributeId() != null) {
        bean.getLevel0Options().getColorOptions().setDimensionAttributeId(masterplanOptions.getColorAttributeId());
      }
      bean.getLevel0Options().getColorOptions().setUseColorRange(masterplanOptions.isUseColorRange());
      refreshGraphicalExportColorOptions(bean.getLevel0Options().getColorOptions(),
          TypeOfBuildingBlock.getTypeOfBuildingBlockByString(masterplanOptions.getSelectedBbType()));
      bean.getLevel0Options().getColorOptions()
          .matchValuesFromSavedQuery(masterplanOptions.getColorAttributeValues(), masterplanOptions.getSelectedColors());

      initRowTypeCustomColumnsForm(bean.getLevel0Options(), masterplanOptions.getCustomColumns());
    }
    else {
      MasterplanRowTypeOptions levelOptions = initMasterplanLevelOptionsForm(null, masterplanOptions.getLevel0Options(), 0);
      bean.setLevel0Options(levelOptions);
      bean.setSelectedBbType(levelOptions.getSelectedBbType());
      if (masterplanOptions.getLevel1Options() != null) {
        levelOptions = initMasterplanLevelOptionsForm(levelOptions.getTypeOfBuildingBlock(), masterplanOptions.getLevel1Options(), 1);
        bean.setLevel1Options(levelOptions);
        bean.setSelectedLevel1Relation(levelOptions.getRelationToBbType());
        if (masterplanOptions.getLevel2Options() != null) {
          levelOptions = initMasterplanLevelOptionsForm(levelOptions.getTypeOfBuildingBlock(), masterplanOptions.getLevel2Options(), 2);
          bean.setLevel2Options(levelOptions);
          bean.setSelectedLevel2Relation(levelOptions.getRelationToBbType());
        }
      }
    }

    return bean;
  }

  private MasterplanRowTypeOptions initMasterplanLevelOptionsForm(TypeOfBuildingBlock fromType, MasterplanRowTypeOptionsXML xmlOptions, int level) {
    MasterplanRowTypeOptions levelOptions = createMasterplanRowType(fromType, xmlOptions.getRelationToBbType(), xmlOptions.getSelectedBbType(), level);

    levelOptions.setHierarchicalSort(xmlOptions.isHierarchicalSort());
    levelOptions.setBuildClosure(xmlOptions.isBuildClosure());

    levelOptions.setUseDefaultColoring(xmlOptions.isUseDefaultColoring());
    if (xmlOptions.getColorAttributeId() != null) {
      levelOptions.getColorOptions().setDimensionAttributeId(xmlOptions.getColorAttributeId());
    }
    levelOptions.getColorOptions().setUseColorRange(xmlOptions.isUseColorRange());
    refreshGraphicalExportColorOptions(levelOptions.getColorOptions(), levelOptions.getTypeOfBuildingBlock());
    levelOptions.getColorOptions().matchValuesFromSavedQuery(xmlOptions.getColorAttributeValues(), xmlOptions.getSelectedColors());

    initRowTypeCustomColumnsForm(levelOptions, xmlOptions.getCustomColumns());
    initRowTypeTimelinesForm(levelOptions, xmlOptions.getTimelineFeatures());

    return levelOptions;
  }

  private void initRowTypeCustomColumnsForm(MasterplanRowTypeOptions levelOptions, List<ColumnEntryXML> customColumnsXml) {
    if (!(customColumnsXml == null || customColumnsXml.isEmpty())) {
      List<ColumnEntry> availableEntriesToRemove = new ArrayList<ColumnEntry>();
      for (ColumnEntryXML savedEntry : customColumnsXml) {
        for (ColumnEntry availableEntry : levelOptions.getAvailableCustomColumns()) {
          if (savedEntry.getHead().equals(availableEntry.getHead())) {
            levelOptions.getSelectedCustomColumns().add(availableEntry);
            availableEntriesToRemove.add(availableEntry);
          }
        }
      }
      levelOptions.getAvailableCustomColumns().removeAll(availableEntriesToRemove);
    }
    if (levelOptions.getSelectedCustomColumns().size() >= IteraplanProperties
        .getIntProperty(IteraplanProperties.EXPORT_GRAPHICAL_MASTERPLAN_MAXCUSTOMCOLUMNS)) {
      levelOptions.setAdditionalCustomColumnsAllowed(false);
    }
  }

  private void initRowTypeTimelinesForm(MasterplanRowTypeOptions levelOptions, List<TimelineFeatureXML> timelinesXml) {
    if (levelOptions.getTypeOfBuildingBlock().hasRuntimePeriod()) {
      boolean keepRuntimePeriod = false;
      for (TimelineFeatureXML xmlTimeline : timelinesXml) {
        if (xmlTimeline.isRuntimePeriod()) {
          keepRuntimePeriod = true;
          break;
        }
      }
      if (!keepRuntimePeriod) {
        levelOptions.removeTimeLineByPosition(0);
      }
    }

    List<TimelineFeature> timelinesToAdd = Lists.newArrayList();

    for (TimelineFeatureXML xmlFeature : timelinesXml) {
      for (TimelineFeature avaliableTimeline : levelOptions.getAvailableTimeLines()) {
        if (xmlFeature.getName().equals(avaliableTimeline.getName())) {
          timelinesToAdd.add(avaliableTimeline);
        }
      }
    }

    for (TimelineFeature timelineFeature : timelinesToAdd) {
      levelOptions.addTimeline(timelineFeature);
    }
  }

  /** {@inheritDoc} */
  public void updateLineOptionAvailableAttributeType(ManageReportMemoryBean memBean) {
    LineOptionsBean lineOptions = GraphicalOptionsGetter.getLineOptions(memBean);
    lineOptions.setAvailableBbTypes(getBbTypesWithTimeseries());
    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(lineOptions.getSelectedBbType());

    List<AttributeType> attrs = attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(bbType, false);
    List<AttributeType> timeseriesTypes = new ArrayList<AttributeType>();
    for (AttributeType at : attrs) {
      if (at instanceof TimeseriesType) {
        timeseriesTypes.add(at);
      }
    }
    List<BBAttribute> attributesToSet = queryService.convertToBBAttributes(timeseriesTypes);

    lineOptions.setAvailableAttributeTypes(attributesToSet);
  }

  /** {@inheritDoc} */
  public ClusterOptionsBean initClusterDiagramForm(ClusterOptionsXML clusterOptionsXml) {
    ClusterOptionsBean bean = new ClusterOptionsBean();
    bean.resetSecondOrderBeans();

    TypeOfBuildingBlock bbType = null;

    List<String> selectedAttributeValues = clusterOptionsXml.getSelectedAttributeValues();
    if (clusterOptionsXml.getSelectedModus().equals(Constants.REPORTS_EXPORT_CLUSTER_MODE_ATTRIBUTE)) {
      selectedAttributeValues = checkClusterForAttributeMode(clusterOptionsXml.getSelectedAttributeType(), selectedAttributeValues);
    }
    else {
      bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(clusterOptionsXml.getSelectedBbType());
    }

    // Load common settings
    bean.setSelectedBbType(clusterOptionsXml.getSelectedBbType());
    initializeClusterForAttributeMode(bean);
    bean.setSelectedAttributeType(clusterOptionsXml.getSelectedAttributeType());

    bean.setSelectedHierarchicalLowerLevel(clusterOptionsXml.getLowerLevel());
    bean.setSelectedHierarchicalUpperLevel(clusterOptionsXml.getUpperLevel());
    bean.setAvailableHierarchicalLevels(clusterOptionsXml.getAvailableLevels());

    bean.setDialogStep(clusterOptionsXml.getDialogStep());

    bean.setSelectedClusterMode(clusterOptionsXml.getSelectedModus());
    bean.setSelectedAttributeValues(selectedAttributeValues);

    bean.setSelectedGraphicFormat(clusterOptionsXml.getSelectedGraphicsFormat());
    bean.setSwimlaneContent(clusterOptionsXml.isSwimLanes());
    bean.setUseNamesLegend(clusterOptionsXml.isUseNamesLegend());
    bean.setShowSavedQueryInfo(clusterOptionsXml.isShowSavedQueryInfo());

    // Main color options
    bean.getColorOptionsBean().setDimensionAttributeId(Integer.valueOf(clusterOptionsXml.getColorAttribute()));
    bean.getColorOptionsBean().setUseColorRange(clusterOptionsXml.isUseColorRange());
    refreshGraphicalExportColorOptions(bean.getColorOptionsBean(), bbType);
    bean.getColorOptionsBean().matchValuesFromSavedQuery(clusterOptionsXml.getColorAttributeValues(), clusterOptionsXml.getSelectedColors());

    // Initialize second order beans
    initializeClusterSecondOrderBeans(bean);

    setSecondOrderBeansData(clusterOptionsXml, bean);
    return bean;
  }

  private List<String> checkClusterForAttributeMode(int selectedAttributeTypeId, List<String> selectedAttributeValues) {
    // If attribute mode - verify the attribute still exists
    AttributeType selectedAttributeType = attributeTypeService.loadObjectById(Integer.valueOf(selectedAttributeTypeId));

    List<String> checkedAttributeValues = new ArrayList<String>(selectedAttributeValues);
    if (selectedAttributeType == null) {
      // The selected attribute does not exist any more
      throw new IteraplanBusinessException(IteraplanErrorMessages.SAVEDQUERY_INCONSISTENT_WITH_DB);
    }
    else {
      // check selected attribute values
      List<String> availableAttributeValues = getDimensionAttributeValues(selectedAttributeType.getId(), UserContext.getCurrentLocale(), null);
      if (selectedAttributeValues == null || selectedAttributeValues.isEmpty()) {
        // if nothing is selected, select all, including unspecified value
        checkedAttributeValues = new ArrayList<String>(availableAttributeValues);
        checkedAttributeValues.add(DimensionOptionsBean.DEFAULT_VALUE);
      }
      else {

        for (Iterator<String> avIter = checkedAttributeValues.iterator(); avIter.hasNext();) {
          String av = avIter.next();
          if (!availableAttributeValues.contains(av) && !DimensionOptionsBean.DEFAULT_VALUE.equals(av)) {
            // remove attribute values which are no longer available
            avIter.remove();
          }
        }
      }
    }
    return checkedAttributeValues;
  }

  /**
   * We load the second order beans of the saved query under the following convention: If a second
   * oder bean is available in both the xml and the beans initalized from the database, we load
   * the presentation according to the xml configuration. If a bean is only available in the xml
   * we ignore it, or unavailable from both sources we ignore it. If a bean has been initialized
   * from the database, but is not available in the saved query, we still add it to the
   * presentation, yet we set it disabled.
   * @param clusterOptionsXml
   * @param bean
   */
  private void setSecondOrderBeansData(ClusterOptionsXML clusterOptionsXml, ClusterOptionsBean bean) {
    TypeOfBuildingBlock bbType;
    Map<String, ColorOptionsXML> savedColorOptions = new HashMap<String, ColorOptionsXML>();
    for (ColorOptionsXML colorOptionsXml : clusterOptionsXml.getColorOptions()) {
      String savedSecondOrderBeanName = colorOptionsXml.getName();

      // for older saved reports which don't have a separate name property for SecondOrderBeans, set name to dimensionKey
      if (savedSecondOrderBeanName == null && colorOptionsXml.getDimensionKey() != null) {
        savedSecondOrderBeanName = colorOptionsXml.getDimensionKey();
      }

      if (savedSecondOrderBeanName != null) {
        savedColorOptions.put(savedSecondOrderBeanName, colorOptionsXml);
      }
    }
    List<String> savedTypeOrder = clusterOptionsXml.getTypeOrder();

    for (ClusterSecondOrderBean secondOrderBean : bean.getSecondOrderBeans()) {
      ColorOptionsXML colorOptionsXml = savedColorOptions.get(secondOrderBean.getName());
      if (colorOptionsXml == null) {
        // If not available in query: set disabled
        secondOrderBean.setSelected(false);
        savedTypeOrder.add(secondOrderBean.getName());
        LOGGER.info("Second order dimension of type " + secondOrderBean.getName() + " is not available in the saved query.");
      }
      else {
        // load with data
        secondOrderBean.setSelected(colorOptionsXml.isSelected());
        secondOrderBean.setSelectedBbShape(colorOptionsXml.getForm());
        secondOrderBean.setBeanType(colorOptionsXml.getDimensionType());

        if (secondOrderBean.getBeanType().equals(ClusterSecondOrderBean.BUILDING_BLOCK_BEAN)) {
          bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(secondOrderBean.getRepresentedType());
        }
        else {
          bbType = null;
        }

        secondOrderBean.getColorOptions().setDimensionAttributeId(Integer.valueOf(colorOptionsXml.getColorAttribute()));
        secondOrderBean.getColorOptions().setUseColorRange(colorOptionsXml.isUseColorRange());
        refreshGraphicalExportColorOptions(secondOrderBean.getColorOptions(), bbType);
        secondOrderBean.getColorOptions().matchValuesFromSavedQuery(colorOptionsXml.getChildrenColorAttributeValues(),
            colorOptionsXml.getChildrenSelectedColors());
      }
    }
    bean.setTypeOrder(savedTypeOrder);
  }

  /** {@inheritDoc} */
  public PortfolioOptionsBean initPortfolioDiagramForm(PortfolioOptionsXML portfolioOptions) {
    PortfolioOptionsBean bean = new PortfolioOptionsBean();
    // Set scaling option:
    bean.setScalingEnabled(portfolioOptions.isScalingEnabled());
    bean.setSelectedGraphicFormat(portfolioOptions.getSelectedGraphicsFormat());
    bean.setUseNamesLegend(portfolioOptions.isUseNamesLegend());
    bean.setShowSavedQueryInfo(portfolioOptions.isShowSavedQueryInfo());

    if (portfolioOptions.getXAxisAttributeId() != null) {
      bean.setXAxisAttributeId(portfolioOptions.getXAxisAttributeId());
    }
    if (portfolioOptions.getYAxisAttributeId() != null) {
      bean.setYAxisAttributeId(portfolioOptions.getYAxisAttributeId());
    }
    if (portfolioOptions.getColorAttributeId() != null) {
      bean.getColorOptionsBean().setDimensionAttributeId(portfolioOptions.getColorAttributeId());
    }
    bean.getColorOptionsBean().setUseColorRange(portfolioOptions.isUseColorRange());
    if (StringUtils.isNotEmpty(portfolioOptions.getSizeAttributeId().toString())) {
      bean.setSizeAttributeId(portfolioOptions.getSizeAttributeId());
    }
    bean.setDialogStep(portfolioOptions.getDialogStep());

    if (!StringUtils.isEmpty(portfolioOptions.getSelectedBbType())) {
      bean.setSelectedBbType(portfolioOptions.getSelectedBbType());
    }

    refreshGraphicalExportColorOptions(bean.getColorOptionsBean(),
        TypeOfBuildingBlock.getTypeOfBuildingBlockByString(portfolioOptions.getSelectedBbType()));

    // color attribute: XML saved values are matched to XML saved colors
    bean.getColorOptionsBean().matchValuesFromSavedQuery(portfolioOptions.getColorAttributeValues(), portfolioOptions.getSelectedColors());

    return bean;
  }

  /** {@inheritDoc} */
  public void initPieBarDiagramForm(ManageReportMemoryBean memBean, PieBarOptionsXML xmlOptions) {
    PieBarDiagramOptionsBean bean = GraphicalOptionsGetter.getPieBarOptions(memBean);

    bean.setNumberOfSelectedElements(memBean.getQueryResult().getSelectedResults().size());

    bean.setDiagramType(DiagramType.getTypeFromString(xmlOptions.getSelectedModus()));
    bean.setDiagramKeyType(DiagramKeyType.getTypeFromString(xmlOptions.getDiagramKeyType()));
    bean.setDiagramValuesType(ValuesType.getTypeFromString(xmlOptions.getDiagramValuesType()));

    if (xmlOptions.getValuesSource() != null) {
      bean.setValuesSource(ValuesSource.getTypeFromString(xmlOptions.getValuesSource()));
    }
    else { // for compatibility to older queries
      if (DiagramKeyType.ASSOCIATION_NAMES.equals(bean.getDiagramKeyType()) && DiagramType.PIE.equals(bean.getDiagramType())) {
        bean.setValuesSource(ValuesSource.ASSOCIATION);
      }
      else {
        bean.setValuesSource(ValuesSource.ATTRIBUTE);
      }
    }

    // Load common settings
    bean.setSelectedBbType(xmlOptions.getSelectedBbType());

    initializePieBarOptions(memBean);

    bean.setCheckAllBoxPie(true);
    for (SingleBarColorOptionsXML sbXML : xmlOptions.getColorOptions()) {
      SingleBarOptionsBean sbob = bean.getBarsMap().get(Integer.valueOf(sbXML.getColorAttribute()));
      if (sbob != null) {
        sbob.setSelected(sbXML.isSelected());
        if (!sbob.isSelected()) {
          bean.setCheckAllBoxPie(false);
        }
        sbob.getColorOptions().setUseColorRange(sbXML.isUseColorRange());
      }
    }

    bean.setSelectedKeyAssociation(xmlOptions.getSelectedKeyAssociation());
    bean.setSelectedKeyAttributeTypeId(xmlOptions.getSelectedKeyAttributeType());

    if (ValuesSource.ASSOCIATION.equals(bean.getValuesSource()) && DiagramType.PIE.equals(bean.getDiagramType())
        && "".equals(xmlOptions.getColorAssociation())) {
      // for compatibility with older queries
      bean.setSelectedAssociation(xmlOptions.getSelectedKeyAssociation());
    }
    else {
      bean.setSelectedAssociation(xmlOptions.getColorAssociation());
    }

    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(xmlOptions.getSelectedBbType());

    bean.getColorOptionsBean().setDimensionAttributeId(Integer.valueOf(xmlOptions.getColorAttributeId()));
    bean.getColorOptionsBean().setUseColorRange(xmlOptions.isUseColorRange());
    //needs to be after initialisation of the maxNumberOfAssociation-map (done in initializePieBarOptions)
    refreshGraphicalExportColorOptionsForPieBar(bean.getDiagramValuesType(), bean, bean.getColorOptionsBean(), bbType);
    bean.getColorOptionsBean().matchValuesFromSavedQuery(xmlOptions.getColorAttributeValues(), xmlOptions.getSelectedColors());

    bean.setSelectedLevelRange(xmlOptions.getAssociationTopLevel() + "_" + xmlOptions.getAssociationBottomLevel());

    setSingleBarBeansData(xmlOptions, bean);

    bean.setDialogStep(xmlOptions.getDialogStep());

    bean.setShowEmptyBars(xmlOptions.isShowEmptyBars());
    bean.setShowSegmentLabels(xmlOptions.isShowSegmentLabels());
    bean.setShowBarSizeLabels(xmlOptions.isShowBarSizeLabels());
    bean.setBarsOrderMethod(BarsOrderMethod.getTypeFromString(xmlOptions.getBarsOrderMethod()));

    bean.setSelectedGraphicFormat(xmlOptions.getSelectedGraphicsFormat());
    bean.setUseNamesLegend(xmlOptions.isUseNamesLegend());
    bean.setShowSavedQueryInfo(xmlOptions.isShowSavedQueryInfo());
  }

  public LineOptionsBean initLineDiagramForm(ManageReportMemoryBean memBean, LineOptionsXML lineOptions) {
    LineOptionsBean prevLineOptions = (LineOptionsBean) memBean.getGraphicalOptions();
    LineOptionsBean bean = new LineOptionsBean();
    bean.setAvailableBbTypes(getBbTypesWithTimeseries());
    bean.setSelectedKeyAttributeTypeId(lineOptions.getSelectedKeyAttributeType());

    bean.setTimeseriesAttributesActive(prevLineOptions.isTimeseriesAttributesActive());
    lineOptions.update(bean, UserContext.getCurrentLocale());

    if (!StringUtils.isEmpty(lineOptions.getSelectedBbType())) {
      bean.setSelectedBbType(lineOptions.getSelectedBbType());
    }

    refreshGraphicalExportColorOptions(bean.getColorOptionsBean(),
        TypeOfBuildingBlock.getTypeOfBuildingBlockByString(lineOptions.getSelectedBbType()));

    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(lineOptions.getSelectedBbType());
    List<AttributeType> attrs = attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(bbType, false);
    List<AttributeType> timeseriesTypes = new ArrayList<AttributeType>();
    for (AttributeType at : attrs) {
      if (at instanceof TimeseriesType) {
        timeseriesTypes.add(at);
      }
    }
    List<BBAttribute> attributesToSet = queryService.convertToBBAttributes(timeseriesTypes);
    bean.setAvailableAttributeTypes(attributesToSet);
    return bean;
  }

  /** {@inheritDoc} */
  public void initPieBarAssociationMetrics(ManageReportMemoryBean memBean) {
    /*
     * The session has to be cleared here to detach any of the selected building blocks and their related elements
     * from the hibernate session, should they still be persistent. Otherwise the materialization/loading of the
     * building blocks' collections (required by the calculations done in "calculatePieBarMetrics") will affect
     * the building blocks saved in the memory bean instead of only the temporary list used for said calculations.
     * This can lead to possible StackOverflowErrors during the Serialization/Deserialization done at each state
     * transition of the spring webflow, for large data sets with a high amount of connections between the elements.
     */
    generalBuildingBlockService.clearSession();

    PieBarDiagramOptionsBean pieBarOptions = GraphicalOptionsGetter.getPieBarOptions(memBean);
    List<BuildingBlock> selectedEntities = generalBuildingBlockService.refreshBuildingBlocks(memBean.getQueryResult().getSelectedResults());
    calculatePieBarMetrics(pieBarOptions, selectedEntities);
  }

  private void calculatePieBarMetrics(PieBarDiagramOptionsBean pieBarOptions, List<BuildingBlock> selectedEntities) {
    Map<String, Integer> bottomLevels = CollectionUtils.hashMap();
    Map<String, Integer> maxNumberOfAssociatedEntities = CollectionUtils.hashMap();

    for (BuildingBlock bb : selectedEntities) {
      Map<String, Set<? extends IdentityEntity>> buildingBlockMapping = new BuildingBlockRelationMapping(bb).getMapping();
      for (String association : pieBarOptions.getAllAvailableAssociations()) {
        List<BuildingBlock> associatedBbs = CollectionUtils.arrayList();
        for (IdentityEntity entity : buildingBlockMapping.get(association)) {
          associatedBbs.add((BuildingBlock) entity);
        }
        // number of associated entities
        int numberOfAssociatedEntities = associatedBbs.size();
        if (maxNumberOfAssociatedEntities.get(association) == null
            || maxNumberOfAssociatedEntities.get(association).intValue() < numberOfAssociatedEntities) {
          maxNumberOfAssociatedEntities.put(association, Integer.valueOf(numberOfAssociatedEntities));
        }
        // hierarchical Levels
        int[] levels = queryService.determineLevels(associatedBbs);
        if (bottomLevels.get(association) == null || bottomLevels.get(association).intValue() < levels[1]) {
          bottomLevels.put(association, Integer.valueOf(levels[1]));
        }
      }
    }
    pieBarOptions.setMaxNumberOfAssociatedEntities(maxNumberOfAssociatedEntities);
    pieBarOptions.setBottomLevels(bottomLevels);
  }

  private void setSingleBarBeansData(PieBarOptionsXML xmlOptions, PieBarDiagramOptionsBean bean) {
    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(xmlOptions.getSelectedBbType());

    for (SingleBarColorOptionsXML colorOptionsXml : xmlOptions.getColorOptions()) {
      int colorAttrId = colorOptionsXml.getColorAttribute();
      SingleBarOptionsBean singleBarBean = bean.getBarsMap().get(Integer.valueOf(colorAttrId));
      if (singleBarBean != null) {
        refreshGraphicalExportColorOptionsForPieBar(singleBarBean.getType(), bean, singleBarBean.getColorOptions(), bbType);
        singleBarBean.getColorOptions().matchValuesFromSavedQuery(colorOptionsXml.getChildrenColorAttributeValues(),
            colorOptionsXml.getChildrenSelectedColors());
      }
    }
  }

  public void setDateIntervalService(DateIntervalService dateIntervalService) {
    this.dateIntervalService = dateIntervalService;
  }

  public void setAttributeValueService(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  public void setGeneralBuildingBlockService(GeneralBuildingBlockService generalBuildingBlockService) {
    this.generalBuildingBlockService = generalBuildingBlockService;
  }

  public void setQueryService(QueryService queryService) {
    this.queryService = queryService;
  }

  public void setSavedQueryService(SavedQueryService savedQueryService) {
    this.savedQueryService = savedQueryService;
  }

  /** {@inheritDoc} */
  public void refreshGraphicalExportColorOptions(ColorDimensionOptionsBean colorOptions, TypeOfBuildingBlock bbType) throws IteraplanException {
    Locale locale = UserContext.getCurrentLocale();
    List<String> dimensionAttributeValues = getDimensionAttributeValues(colorOptions.getDimensionAttributeId(), locale, bbType);
    colorOptions.setColorRangeAvailable(attributeTypeService.isNumberAT(colorOptions.getDimensionAttributeId()));
    colorOptions.refreshDimensionOptions(dimensionAttributeValues);
  }

  /** {@inheritDoc} */
  public void refreshGraphicalExportColorOptionsForPieBar(ValuesType valuesType, PieBarDiagramOptionsBean pieBarOptions,
                                                          ColorDimensionOptionsBean colorOptions, TypeOfBuildingBlock bbType) {
    switch (valuesType) {
      case MAINTAINED:
        List<String> dimensionAttributeValuesMaintained = CollectionUtils.arrayList();
        dimensionAttributeValuesMaintained.add("graphicalReport.specified");
        colorOptions.setColorRangeAvailable(false);
        colorOptions.refreshDimensionOptions(dimensionAttributeValuesMaintained);
        break;
      case COUNT:
        int countSize;
        if (pieBarOptions.getValuesSource().equals(ValuesSource.ASSOCIATION) && !"".equals(pieBarOptions.getSelectedAssociation())) {
          countSize = pieBarOptions.getMaxNumberOfAssociatedEntities().get(pieBarOptions.getSelectedAssociation()).intValue();
        }
        else {
          Locale locale = UserContext.getCurrentLocale();
          countSize = getDimensionAttributeValues(colorOptions.getDimensionAttributeId(), locale, bbType).size();
        }
        List<String> dimensionAttributeValuesCount = CollectionUtils.arrayList();
        for (int i = 1; i <= countSize; i++) {
          dimensionAttributeValuesCount.add(String.valueOf(i));
        }
        colorOptions.refreshDimensionOptions(dimensionAttributeValuesCount);
        break;
      case VALUES:
        refreshGraphicalExportColorOptions(colorOptions, bbType);
        break;
      default:
    }
  }

  /** {@inheritDoc} */
  public void refreshGraphicalExportLineTypeOptions(LineDimensionOptionsBean lineOptions, TypeOfBuildingBlock bbType) {
    Integer selectedLineTypeAttributeId = lineOptions.getDimensionAttributeId();
    Locale locale = UserContext.getCurrentLocale();

    List<String> dimensionAttributeValues = getDimensionAttributeValues(selectedLineTypeAttributeId, locale, bbType);

    lineOptions.refreshDimensionOptions(dimensionAttributeValues);
  }

  /** {@inheritDoc} */
  public List<String> getDimensionAttributeValues(Integer selectedAttributeId, Locale locale, TypeOfBuildingBlock bbType) {
    List<String> result = null;

    switch (selectedAttributeId.intValue()) {
      case GraphicalExportBaseOptions.NOTHING_SELECTED:
        result = new ArrayList<String>();
        break;
      case GraphicalExportBaseOptions.STATUS_SELECTED:
        result = getStatusList(locale, bbType);
        break;
      case GraphicalExportBaseOptions.SEAL_SELECTED:
        result = getSealStatusList(locale);
        break;
      default: // Attribute selected
        List<String> values = attributeValueService.getAllAVStrings(selectedAttributeId);
        if (attributeTypeService.isNumberAT(selectedAttributeId)) {
          NumberAT attribute = (NumberAT) attributeTypeService.loadObjectById(selectedAttributeId);
          AttributeRangeAdapter adapter = new AttributeRangeAdapter(locale);
          adapter.init(attribute, values);
          values = adapter.getValues();
        }
        result = values;
        break;
    }

    return result;
  }

  /** {@inheritDoc} */
  public void initializeClusterSecondOrderBeans(ClusterOptionsBean clusterOptions) {
    List<TypeOfBuildingBlock> connectedBbTypes = new ArrayList<TypeOfBuildingBlock>();
    List<BBAttribute> connectedAttributes = new ArrayList<BBAttribute>();

    if (clusterOptions.getSelectedClusterMode().equals(Constants.REPORTS_EXPORT_CLUSTER_MODE_BB)) {
      TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(clusterOptions.getSelectedBbType());
      connectedBbTypes = new ArrayList<TypeOfBuildingBlock>(bbType.getConnectedTypesOfBuildingBlocks());
      connectedAttributes = getConnectedAttributesForTypeOfBuildingBlock(bbType);
    }
    else {
      AttributeType selectedAt = attributeTypeService.loadObjectById(Integer.valueOf(clusterOptions.getSelectedAttributeType()));

      for (BuildingBlockType bbt : selectedAt.getBuildingBlockTypes()) {
        connectedBbTypes.add(bbt.getTypeOfBuildingBlock());
      }
    }

    clusterOptions.configureSecondOrderBeans(getBbTypeToAttributesMap(connectedBbTypes), connectedAttributes);
  }

  /** {@inheritDoc} */
  public void initializePieBarOptions(ManageReportMemoryBean memBean) {
    PieBarDiagramOptionsBean pieBarOptions = GraphicalOptionsGetter.getPieBarOptions(memBean);
    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(pieBarOptions.getSelectedBbType());

    List<String> propertiesToSet = CollectionUtils.arrayList();
    if (bbType == TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE) {
      propertiesToSet.add(Constants.ATTRIBUTE_TYPEOFSTATUS);
      propertiesToSet.add("seal");
    }
    else if (bbType == TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE) {
      propertiesToSet.add(Constants.ATTRIBUTE_TYPEOFSTATUS);
    }

    propertiesToSet.add(Constants.ATTRIBUTE_DESCRIPTION);

    List<BBAttribute> attributesToSet = CollectionUtils.arrayList();
    List<DynamicQueryFormData<?>> queryForms = memBean.getQueryResult().getQueryForms();
    List<BBAttribute> dimensionAttributes = queryForms.get(0).getDimensionAttributes();
    for (BBAttribute att : queryForms.get(0).getAvailableAttributes()) {
      if ((att.getId().intValue() != -1 && dimensionAttributes.contains(att)) || propertiesToSet.contains(att.getName())) {
        attributesToSet.add(att);
      }
    }
    pieBarOptions.setAvailableAttributeTypes(attributesToSet);

    List<String> associations = CollectionUtils.arrayList();
    for (TypeOfBuildingBlock tobb : bbType.getConnectedTypesOfBuildingBlocks()) {
      if (tobb.equals(bbType)) {
        associations.addAll(tobb.getSelfReferencesPropertyKeys());
      }
      else {
        associations.add(tobb.getValue());
      }
    }
    pieBarOptions.setAvailableAssociations(associations);
    initPieBarAssociationMetrics(memBean);

    initSingleBarOptionsBeans(pieBarOptions);
  }

  private void initSingleBarOptionsBeans(PieBarDiagramOptionsBean pieBarOptions) {
    if (DiagramType.BAR.equals(pieBarOptions.getDiagramType())) {
      pieBarOptions.initBarsMap();

      TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(pieBarOptions.getSelectedBbType());
      for (SingleBarOptionsBean sbob : pieBarOptions.getBarsMap().values()) {
        refreshGraphicalExportColorOptionsForPieBar(sbob.getType(), pieBarOptions, sbob.getColorOptions(), tobb);
      }
    }
  }

  private Map<TypeOfBuildingBlock, List<BBAttribute>> getBbTypeToAttributesMap(List<TypeOfBuildingBlock> buildingBlockTypes) {
    Map<TypeOfBuildingBlock, List<BBAttribute>> bbTypeToAttributesMap = new HashMap<TypeOfBuildingBlock, List<BBAttribute>>();
    for (TypeOfBuildingBlock connectedType : buildingBlockTypes) {
      bbTypeToAttributesMap.put(connectedType, getAttributesForTypeOfBuildingBlock(connectedType));
    }
    return bbTypeToAttributesMap;
  }

  /** {@inheritDoc} */
  public List<BBAttribute> getAttributesForTypeOfBuildingBlock(TypeOfBuildingBlock bbType) {
    List<BBAttribute> permittedExportAttributes = new ArrayList<BBAttribute>();

    String permissionCheck = PermissionHelper.getAssociatedPermission(bbType);
    if (PermissionHelper.hasPermissionFor(permissionCheck)) {

      List<BBAttribute> allExportAttributes = queryService.getBBAttributesForGraphicalExport(bbType);

      permittedExportAttributes.add(0, new BBAttribute(null, BBAttribute.BLANK_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_BLANK, null));

      for (BBAttribute attribute : allExportAttributes) {
        final Integer attributeId = attribute.getId();
        if (attributeId.intValue() == GraphicalExportBaseOptions.STATUS_SELECTED
            || attributeId.intValue() == GraphicalExportBaseOptions.SEAL_SELECTED) {
          // Handle the status
          permittedExportAttributes.add(attribute);
        }
        else {
          // Handle ordinary attributes
          AttributeType attributeType = attributeTypeService.loadObjectById(attributeId);
          if (UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(attributeType.getAttributeTypeGroup(),
              AttributeTypeGroupPermissionEnum.READ)) {
            permittedExportAttributes.add(attribute);
          }
        }
      }
    }
    return permittedExportAttributes;
  }

  private List<ColumnEntry> getCustomColumnsForTypeOfBuildingBlock(TypeOfBuildingBlock type) {
    ViewConfiguration vc = new ViewConfiguration(type, UserContext.getCurrentLocale());
    vc.addAttributeTypesToView(attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(type, true));
    return vc.getAvailableColumns();
  }

  private List<BBAttribute> getConnectedAttributesForTypeOfBuildingBlock(TypeOfBuildingBlock bbType) {
    List<AttributeType> attrs = attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(bbType, false);
    List<AttributeType> permittedAttrs = new ArrayList<AttributeType>();
    for (AttributeType at : attrs) {
      if (UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ)) {
        permittedAttrs.add(at);
      }
    }
    return queryService.convertToBBAttributes(permittedAttrs);
  }

  /** {@inheritDoc} */
  public MasterplanRowTypeOptions createMasterplanRowType(TypeOfBuildingBlock fromType, String relationToBbType, String selectedBBType, int level) {
    TypeOfBuildingBlock bbType = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(selectedBBType);

    List<DateInterval> availableDateIntervals = getAvailableDateIntervals(bbType);
    List<BBAttribute> availableColorAttributes = getAttributesForTypeOfBuildingBlock(bbType);
    List<ColumnEntry> availableCustomColumns = getCustomColumnsForTypeOfBuildingBlock(bbType);

    MasterplanRowTypeOptions row = new MasterplanRowTypeOptions(relationToBbType, selectedBBType, level, availableDateIntervals,
        availableColorAttributes, availableCustomColumns);

    row.getColorOptions().setAvailableColors(SpringGuiFactory.getInstance().getMasterplanColors());
    row.setAvailableRelatedTypes(getRelationshipsToOtherBbTypes(selectedBBType));
    if (fromType != null) {
      //from type is null for level 0 row type opts
      row.setCanBuildClosure(fromType.getSelfReferencesPropertyKeys().contains(relationToBbType));
    }

    return row;
  }

  /**
   * Retrieves all date intervals available for a given type of building block.
   * @param tobString
   *    The string which identifies the type of building block.
   * @return
   *    The list of all date intervals available for this type of building block.
   */
  private List<DateInterval> getAvailableDateIntervals(TypeOfBuildingBlock bbType) {
    List<AttributeType> aTypes = attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(bbType, false);
    Set<Integer> dateATids = new HashSet<Integer>();
    for (AttributeType dat : aTypes) {
      if (dat instanceof DateAT) {
        dateATids.add(dat.getId());
      }
    }

    return Lists.newArrayList(dateIntervalService.findDateIntervalsByDateATs(dateATids));
  }

  /** {@inheritDoc} */
  public List<String> getRelationshipsToOtherBbTypes(String bbType) {
    List<String> result = new ArrayList<String>();

    result.add(Constants.REPORTS_EXPORT_SELECT_RELATION);
    result.addAll(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(bbType).getSelfReferencesPropertyKeys());
    TypeOfBuildingBlock typeOfBb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(bbType);
    for (TypeOfBuildingBlock relatedType : typeOfBb.getConnectedTypesOfBuildingBlocks()) {
      if (!typeOfBb.equals(relatedType) && UserContext.getCurrentPerms().userHasBbTypeCreatePermission(relatedType)) {
        result.add(relatedType.getPluralValue());
      }
    }
    return result;
  }

  private static String normalize(String input) {
    return input == null ? "" : input.trim();
  }

  public String getRelatedTypeOfBuildingBlock(String relationToBbType) {
    String normalizedRelation = normalize(relationToBbType);
    for (TypeOfBuildingBlock typeOfBb : TypeOfBuildingBlock.ALL) {
      if (typeOfBb.getPluralValue().equalsIgnoreCase(normalizedRelation)) {
        return typeOfBb.getValue();
      }
    }
    if (normalizedRelation.startsWith(normalize("graphicalReport." + Constants.BB_INFORMATIONSYSTEMRELEASE_BASE))) {
      return TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue();
    }
    else if (normalizedRelation.startsWith(normalize("graphicalReport." + Constants.BB_TECHNICALCOMPONENTRELEASE_BASE))) {
      return TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE.getValue();
    }
    else if (normalizedRelation.contains("product")) {
      return TypeOfBuildingBlock.PRODUCT.getValue();
    }
    else if (normalizedRelation.contains("business_function")) {
      return TypeOfBuildingBlock.BUSINESSFUNCTION.getValue();
    }
    else if (normalizedRelation.endsWith(normalize("generalisation")) || normalizedRelation.endsWith(normalize("specialisations"))) {
      return TypeOfBuildingBlock.BUSINESSOBJECT.getValue();
    }
    else if (normalizedRelation.endsWith(normalize("parentComponents.short")) || normalizedRelation.endsWith(normalize("baseComponents.short"))) {
      return TypeOfBuildingBlock.INFRASTRUCTUREELEMENT.getValue();
    }
    else if ((normalizedRelation.endsWith(normalize(".parent"))) || (normalizedRelation.endsWith(".children"))) {
      return normalizedRelation.replace("graphicalReport.", "").replace(".parent", "").replace(".children", "") + ".singular";
    }
    return relationToBbType;
  }

  /** {@inheritDoc} */
  public void initializeClusterForAttributeMode(ClusterOptionsBean clusterOptions) {
    List<AttributeType> allAttributeTypes = attributeTypeService.getAttributeTypesFiltered(null);
    List<AttributeType> permittedAttributeTypes = new ArrayList<AttributeType>();
    for (AttributeType at : allAttributeTypes) {
      if (UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ)) {
        permittedAttributeTypes.add(at);
      }
    }

    clusterOptions.setAvailableAttributeTypes(permittedAttributeTypes);
    if (!permittedAttributeTypes.isEmpty()) {
      clusterOptions.setSelectedAttributeType(permittedAttributeTypes.get(0).getId().intValue());
    }
  }

  /**
   * Get a language dependent list of status values
   * 
   * @return List of status values
   */
  private List<String> getStatusList(Locale locale, TypeOfBuildingBlock bbType) {
    if (TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.equals(bbType)) {
      return StringEnumReflectionHelper
          .getLanguageSpecificEnumValues(de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus.class, locale);
    }
    else {
      return StringEnumReflectionHelper.getLanguageSpecificEnumValues(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.class,
          locale);
    }
  }

  /**
   * Get a language dependent list of seal status values
   * 
   * @return List of seal status values
   */
  private List<String> getSealStatusList(Locale locale) {
    return StringEnumReflectionHelper.getLanguageSpecificEnumValues(SealState.class, locale);
  }

  /**
   * Returns the id of an attribute and validates the attribute
   * 
   * @param attributeStringId
   *          The string id of the attribute. See {@link BBAttribute#getStringIdName()}
   * @param attributeMap
   *          The map of all available attributes of the underlying type
   * @param contentType
   *          The type of the building block the attribute refers to
   * @return The id of the attribute
   */
  private Integer getAttributeId(String attributeStringId, Map<Integer, BBAttribute> attributeMap, Type<?> contentType) {
    BBAttribute attr = attributeMap.get(BBAttribute.getIdByStringId(attributeStringId));

    if (attr != null) {
      return attr.getId();
    }
    else {
      // --------------------------------------------------------------------------------------------
      // ------ if not found, gather information for throwing an exception further down -----
      // --------------------------------------------------------------------------------------------

      Locale locale = UserContext.getCurrentLocale();
      String type = MessageAccess.getStringOrNull(contentType.getTypeNamePresentationKey(), locale);
      String name;
      String typeString;

      try {
        // get the name of the attribute, i.e. the value that is stored on the db. e.g. "Costs"
        name = BBAttribute.getAttributeNameById(BBAttribute.getIdByStringId(attributeStringId));
      } catch (IteraplanException ie) {
        name = attributeStringId;
        typeString = "??";
        LOGGER.debug(ie.getMessage(), ie);
      }

      try {
        // get the type of the attribute (e.g. USERDEF_ENUM_ATTRIBUTE_TYPE)
        typeString = BBAttribute.getTypeByStringId(attributeStringId);
        // validate the type
        TypeOfAttribute typeOfAttribtue = BBAttribute.getTypeOfAttribute(typeString);
        if (typeOfAttribtue != null) {
          typeString = MessageAccess.getStringOrNull(typeOfAttribtue.toString(), locale);
        }
      } catch (IteraplanException ie) {
        typeString = attributeStringId;
        LOGGER.debug(ie.getMessage(), ie);
      }

      // --------------------------------------------------------------------------------------------
      // -------- throw exception with gathered information (typeString and type) ---------
      // --------------------------------------------------------------------------------------------

      throw new IteraplanBusinessException(IteraplanErrorMessages.ATTRIBUTE_NOT_FOUND, name, typeString, type);
    }
  }

  /** {@inheritDoc} */
  public <E extends ManageReportMemoryBean> E initMemBeanFromSavedGraphicalReport(E memBean, SavedQuery savedQuery, ReportXML savedReport) {
    memBean.setReportUpdated(false);
    if (savedReport.isReportUpdated()) {
      memBean.setReportUpdated(true);
    }

    if (hasPermissionToLoadReport(savedReport)) {
      if (savedReport.getQueryForms() != null && !savedReport.getQueryForms().isEmpty()) {
        // compatibility to older saved queries
        loadQueryForms(memBean, savedReport, savedQuery.getType());
      }
      else {
        loadQueryResults(memBean, savedReport, savedQuery.getType());
      }

      memBean.setXmlQueryName(savedQuery.getName());
      memBean.setXmlQueryDescription(savedQuery.getDescription());

      initIfLoadedMasterplan(memBean, savedReport);
      initIfLoadedPortfolio(memBean, savedReport);
      initIfLoadedInformationflow(memBean, savedReport);
      initIfLoadedCluster(memBean, savedReport);
      initIfLoadedPieBar(memBean, savedReport);
      initIfLoadedVbb(memBean, savedReport);
      initIfLoadedLine(memBean, savedReport);

      memBean.getGraphicalOptions().setSavedQueryInfo(new SavedQueryEntityInfo(savedQuery, memBean.getReportType()));
    }
    // no permission to load report
    else {
      memBean.setLoadedFromSavedQuery(false);
      throw new IteraplanBusinessException(IteraplanErrorMessages.NO_PERMISSION_TO_LOAD_QUERY);
    }

    for (QueryResult queryResult : memBean.getQueryResults().values()) {
      getRefreshHelperService().refreshAllForms(queryResult.getQueryForms());
    }
    memBean.setSavedQueryId(null);

    return memBean;
  }

  public <E extends ManageReportMemoryBean> void loadQueryResults(E memBean, ReportXML savedReport, ReportType type) {
    if (savedReport.getQueryResults().isEmpty()) {
      return;
    }
    for (QueryResultXML queryResultXML : savedReport.getQueryResults()) {
      QueryResult queryResult = getQueryResult(queryResultXML);
      memBean.setQueryResult(queryResult);

      if (!queryResult.getQueryForms().isEmpty()) {
        switchQuery(memBean, queryResult.getQueryName());
        initSelectedPostProcessingStrategies(memBean, queryResultXML.getQueryForms().get(0));

        memBean.setLoadedFromSavedQuery(true);
        queryService.requestEntityList(memBean, type);
      }
    }
    if (memBean.getQueryResults().containsKey(ManageReportBeanBase.MAIN_QUERY)) {
      switchQuery(memBean, ManageReportBeanBase.MAIN_QUERY);
    }
  }

  /**
   * Handling of query forms of old saved queries for backwards compatibility 
   */
  private <E extends ManageReportMemoryBean> void loadQueryForms(E memBean, ReportXML savedReport, ReportType reportType) {
    if (memBean.getQueryResult() == null) {
      return;
    }

    memBean.setLoadedFromSavedQuery(true);
    // get all report extensions
    HashMap<String, IPresentationExtension> availableReportExtensions = new HashMap<String, IPresentationExtension>(savedReport.getQueryForms()
        .get(0).getType().getExtensionsForPresentation());

    // get all query forms from the saved XML query and set query extensions. used extensions are
    // removed from availableReportExtensions. the map 'availableReportExtensions' hence after
    // this
    // method call only contains extensions not used yet
    List<DynamicQueryFormData<?>> forms = getSavedReportForm(savedReport.getQueryForms(), availableReportExtensions);

    // add forms and (remaining - not selected) report extensions
    memBean.setQueryForms(forms, availableReportExtensions);

    initSelectedPostProcessingStrategies(memBean, savedReport.getQueryForms().get(0));

    List<Integer> savedReportSelectedResults = savedReport.getSelectedResultIds();
    memBean.getQueryResult().setSelectedResultIds(savedReportSelectedResults.toArray(new Integer[savedReportSelectedResults.size()]));

    queryService.requestEntityList(memBean, reportType);
    setViewConfiguration(memBean, memBean.getQueryResult().getQueryForms().get(0).getType().getTypeOfBuildingBlock());
  }

  private boolean hasPermissionToLoadReport(ReportXML savedReport) {
    if (ReportTypeXML.MASTERPLAN.equals(savedReport.getReportType()) && savedReport.getMasterplanOptions() != null) {
      MasterplanOptionsXML masterplanOptions = savedReport.getMasterplanOptions();
      String bBType;
      if (masterplanOptions.getLevel0Options() == null) {
        //compatibility mode
        bBType = masterplanOptions.getSelectedBbType();
      }
      else {
        bBType = masterplanOptions.getLevel0Options().getSelectedBbType();
      }
      if (bBType != null && PermissionHelper.hasPermissionFor(bBType)) {
        return true;
      }
    }
    else if (ReportTypeXML.PORTFOLIO.equals(savedReport.getReportType())) {
      PortfolioOptionsXML portfolioOptions = savedReport.getPortfolioOptions();
      String bBType = portfolioOptions.getSelectedBbType();

      if (bBType != null && PermissionHelper.hasPermissionFor(bBType)) {
        return true;
      }
    }
    else if (ReportTypeXML.INFORMATIONFLOW.equals(savedReport.getReportType())
        && PermissionHelper.hasPermissionFor(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL)) {

      return true;
    }
    else if (ReportTypeXML.CLUSTER.equals(savedReport.getReportType())) {

      ClusterOptionsXML clusterOptions = savedReport.getClusterOptions();
      String bBType = clusterOptions.getSelectedBbType();

      if (bBType != null && PermissionHelper.hasPermissionFor(bBType)) {
        return true;
      }
    }
    else if (ReportTypeXML.PIEBAR.equals(savedReport.getReportType())) {
      PieBarOptionsXML pieBarOptions = savedReport.getPieBarOptions();
      String bBType = pieBarOptions.getSelectedBbType();

      if (bBType != null && PermissionHelper.hasPermissionFor(bBType)) {
        return true;
      }
    }
    else if (ReportTypeXML.VBB.equals(savedReport.getReportType())) {
      // Check for each value whether it might be a Type and check the permissions on it.
      VbbOptionsXML vbbOptions = savedReport.getVbbOptions();
      boolean hasPermission = true;
      for (KeyValueXML configOption : vbbOptions.getViewpointConfig()) {
        String configValue = configOption.getValue();
        String possibleType = configValue.contains(".") ? configValue.substring(0, configValue.indexOf('.')) : configValue;
        if (possibleType != null && PermissionHelper.permissionCommandExists(possibleType) && !PermissionHelper.hasPermissionFor(possibleType)) {
          hasPermission = false;
        }
      }
      return hasPermission;
    }
    else if (ReportTypeXML.LINE.equals(savedReport.getReportType())) {
      LineOptionsXML lineOptions = savedReport.getLineOptions();
      String bBType = lineOptions.getSelectedBbType();

      if (bBType != null && PermissionHelper.hasPermissionFor(bBType)) {
        return true;
      }
    }

    // fall-through
    return false;
  }

  /** {@inheritDoc} */
  public void initSelectedPostProcessingStrategies(ManageReportMemoryBean memBean, QueryFormXML queryFormXml) {
    memBean.resetPostProcessingStrategies();

    PostProcessingStrategiesXML strategies = queryFormXml.getPostProcessingStrategies();
    if (strategies == null || strategies.getPostProcessingStrategy() == null || strategies.getPostProcessingStrategy().isEmpty()) {
      return;
    }

    List<String> selectedPPStrategyKeys = new ArrayList<String>();
    Map<String, List<String>> strategyWithOptions = new HashMap<String, List<String>>();
    List<PostProcessingStrategyXML> listOfStrategies = strategies.getPostProcessingStrategy();

    for (PostProcessingStrategyXML strategy : listOfStrategies) {
      selectedPPStrategyKeys.add(strategy.getName());
      List<PostProcessingAdditionalOptionsXML> additionalOptions = strategy.getAdditionalOptions();
      List<String> selectedPPSOptionKeys = new ArrayList<String>();

      for (PostProcessingAdditionalOptionsXML option : additionalOptions) {
        selectedPPSOptionKeys.add(option.getAdditionalOption());
      }

      strategyWithOptions.put(strategy.getName(), selectedPPSOptionKeys);
    }

    memBean.getQueryResult().setSelectedPostprocessingStrategies(
        QueryResult.getPostProcessingStrategiesByKeys(queryFormXml.getType(), selectedPPStrategyKeys), strategyWithOptions);
  }

  private <E extends ManageReportMemoryBean> void initIfLoadedMasterplan(E memBean, ReportXML savedReport) {
    if (ReportTypeXML.MASTERPLAN.equals(savedReport.getReportType()) && savedReport.getMasterplanOptions() != null) {
      MasterplanOptionsXML masterplanOptions = savedReport.getMasterplanOptions();
      MasterplanOptionsBean bean = initMasterplanDiagramForm(masterplanOptions);

      memBean.setGraphicalOptions(bean);
    }
  }

  private <E extends ManageReportMemoryBean> void initIfLoadedPortfolio(E memBean, ReportXML savedReport) {
    if (ReportTypeXML.PORTFOLIO.equals(savedReport.getReportType())) {
      PortfolioOptionsBean bean = initPortfolioDiagramForm(savedReport.getPortfolioOptions());
      memBean.setGraphicalOptions(bean);
    }
  }

  private <E extends ManageReportMemoryBean> void initIfLoadedInformationflow(E memBean, ReportXML savedReport) {
    if (ReportTypeXML.INFORMATIONFLOW.equals(savedReport.getReportType())) {
      InformationFlowOptionsBean bean = initInformationFlowDiagramForm(savedReport.getInformationFlowOptions());
      memBean.setGraphicalOptions(bean);
    }
  }

  public QueryResult getQueryResult(QueryResultXML queryResultXML) {
    QueryResult queryResult = new QueryResult(queryResultXML.getQueryName());

    if (!queryResultXML.getQueryForms().isEmpty()) {
      Type<?> requestedType = queryResultXML.getQueryForms().get(0).getType();

      HashMap<String, IPresentationExtension> availableReportExtensions = new HashMap<String, IPresentationExtension>(
          requestedType.getExtensionsForPresentation());

      List<DynamicQueryFormData<?>> forms = getSavedReportForm(queryResultXML.getQueryForms(), availableReportExtensions);
      queryResult.setQueryForms(forms);
      queryResult.setAvailableReportExtensions(availableReportExtensions);

      List<Integer> selectedResultIds = queryResultXML.getSelectedResultIds();
      queryResult.setSelectedResultIds(selectedResultIds.toArray(new Integer[selectedResultIds.size()]));
    }

    // backward-compatibility: skip if saved query does not contain a timeseries query
    TimeseriesQuery timeseriesQuery = getTimeseriesQuery(queryResult.getResultType());
    queryResult.setTimeseriesQuery(timeseriesQuery);
    if (queryResultXML.getTimeseriesQuery() != null) {
      queryResultXML.getTimeseriesQuery().update(timeseriesQuery, UserContext.getCurrentLocale());
      refreshHelper.refreshTimeseriesQuery(queryResult);
    }

    return queryResult;
  }

  private <E extends ManageReportMemoryBean> void initIfLoadedCluster(E memBean, ReportXML savedReport) {
    if (ReportTypeXML.CLUSTER.equals(savedReport.getReportType())) {
      ClusterOptionsXML clusterOptions = savedReport.getClusterOptions();
      ClusterOptionsBean bean = initClusterDiagramForm(clusterOptions);

      memBean.setGraphicalOptions(bean);

      if (Constants.REPORTS_EXPORT_CLUSTER_MODE_ATTRIBUTE.equals(bean.getSelectedClusterMode())) {
        if (bean.getSelectedAttributeValues().equals(bean.getColorOptionsBean().getAttributeValues())) {
          memBean.setCheckAllBox(Boolean.TRUE);
        }
        else {
          memBean.setCheckAllBox(Boolean.FALSE);
        }
      }
    }

  }

  private <E extends ManageReportMemoryBean> void initIfLoadedPieBar(E memBean, ReportXML savedReport) {
    if (ReportTypeXML.PIEBAR.equals(savedReport.getReportType())) {
      PieBarDiagramOptionsBean bean = new PieBarDiagramOptionsBean();
      memBean.setGraphicalOptions(bean);
      initPieBarDiagramForm(memBean, savedReport.getPieBarOptions());
    }
  }

  private <E extends ManageReportMemoryBean> void initIfLoadedLine(E memBean, ReportXML savedReport) {
    if (ReportTypeXML.LINE.equals(savedReport.getReportType()) && savedReport.getLineOptions() != null) {
      LineOptionsBean bean = initLineDiagramForm(memBean, savedReport.getLineOptions());
      memBean.setGraphicalOptions(bean);
    }
  }

  private <E extends ManageReportMemoryBean> void initIfLoadedVbb(E memBean, ReportXML savedReport) {
    if (ReportTypeXML.VBB.equals(savedReport.getReportType())) {
      VbbOptionsBean bean = new VbbOptionsBean(memBean.getReportType());
      memBean.setGraphicalOptions(bean);
      savedReport.getVbbOptions().update(bean, UserContext.getCurrentLocale());

      //legacy support: if color attribute is selected, add decoration mode 'continuous' to the config map
      Map<String, String> vpConfigMap = bean.getViewpointConfigMap();
      String outerColoringKey = RecursiveCluster.CLASS_OUTER + "." + MixedColorCodingDecorator.ATTRIBUTE_COLOR;
      String outerDecorationModeKey = RecursiveCluster.CLASS_OUTER + "." + MixedColorCodingDecorator.VV_DECORATION_MODE;
      String innerColoringKey = RecursiveCluster.CLASS_INNER + "." + MixedColorCodingDecorator.ATTRIBUTE_COLOR;
      String innerDecorationModeKey = RecursiveCluster.CLASS_INNER + "." + MixedColorCodingDecorator.VV_DECORATION_MODE;

      if ("undefined".equals(vpConfigMap.get(outerColoringKey))) {
        vpConfigMap.put(outerColoringKey, "");
      }
      if ("undefined".equals(vpConfigMap.get(innerColoringKey))) {
        vpConfigMap.put(innerColoringKey, "");
      }

      if (vpConfigMap.get(outerColoringKey) != null && !vpConfigMap.get(outerColoringKey).isEmpty()
          && (vpConfigMap.get(outerDecorationModeKey) == null || vpConfigMap.get(outerDecorationModeKey).isEmpty())) {
        vpConfigMap.put(outerDecorationModeKey, MixedColorCodingDecorator.DECORATION_MODE_CONTINUOUS);
      }
      String outerDefaultColorKey = RecursiveCluster.CLASS_OUTER + "." + ContinuousColorCodingDecorator.VV_UNDEFINED_COLOR;
      if (vpConfigMap.get(outerDefaultColorKey) == null || vpConfigMap.get(outerDefaultColorKey).trim().isEmpty()) {
        vpConfigMap.put(RecursiveCluster.CLASS_OUTER + "." + ContinuousColorCodingDecorator.VV_UNDEFINED_COLOR, "#"
            + Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);
        vpConfigMap.put(RecursiveCluster.CLASS_OUTER + "." + ContinuousColorCodingDecorator.VV_OUT_OF_BOUNDS_COLOR,
            ContinuousColorCodingDecorator.DEFAULT_OUT_OF_BOUNDS_COLOR);
      }

      if (vpConfigMap.get(innerColoringKey) != null && !vpConfigMap.get(innerColoringKey).isEmpty()
          && (vpConfigMap.get(innerDecorationModeKey) == null || vpConfigMap.get(innerDecorationModeKey).isEmpty())) {
        vpConfigMap.put(innerDecorationModeKey, MixedColorCodingDecorator.DECORATION_MODE_CONTINUOUS);
      }
      String innerDefaultColorKey = RecursiveCluster.CLASS_INNER + "." + ContinuousColorCodingDecorator.VV_UNDEFINED_COLOR;
      if (vpConfigMap.get(innerDefaultColorKey) == null || vpConfigMap.get(innerDefaultColorKey).trim().isEmpty()) {
        vpConfigMap.put(RecursiveCluster.CLASS_INNER + "." + ContinuousColorCodingDecorator.VV_UNDEFINED_COLOR, "#"
            + Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);
        vpConfigMap.put(RecursiveCluster.CLASS_INNER + "." + ContinuousColorCodingDecorator.VV_OUT_OF_BOUNDS_COLOR,
            ContinuousColorCodingDecorator.DEFAULT_OUT_OF_BOUNDS_COLOR);
      }
    }
  }

  public <E extends ManageReportMemoryBean> void switchQuery(E memBean, String queryNameToSwitchTo) {
    if (!memBean.getQueryResults().containsKey(queryNameToSwitchTo)) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }

    memBean.setQueryResultName(queryNameToSwitchTo);
    QueryResult dimensionQuery = memBean.getQueryResult();

    if (dimensionQuery != null && !dimensionQuery.getQueryForms().isEmpty()) {

      TypeOfBuildingBlock type = dimensionQuery.getResultType().getTypeOfBuildingBlock();
      memBean.setViewConfiguration(new ViewConfiguration(type, UserContext.getCurrentLocale()));
      memBean.getViewConfiguration().addAttributeTypesToView(attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(type, true));
      memBean.setSelectedBuildingBlock(type.toString());

      boolean allResultsChecked = memBean.getQueryResult().getSelectedResults().size() == memBean.getResults().size();
      memBean.setCheckAllBox(Boolean.valueOf(allResultsChecked));
    }
  }

  public List<String> getBbTypesWithTimeseries() {
    List<String> bbTypesWithTimeseries = new ArrayList<String>();
    for (TypeOfBuildingBlock bbType : TypeOfBuildingBlock.DISPLAY) {
      boolean hasTimeseries = false;
      List<AttributeType> attrs = attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(bbType, false);
      List<BBAttribute> attributesBB = queryService.convertToBBAttributes(attrs);
      for (BBAttribute at : attributesBB) {
        if (at.isTimeseries()) {
          hasTimeseries = true;
        }
      }
      if (hasTimeseries) {
        bbTypesWithTimeseries.add(bbType.getPluralValue());
      }
    }
    return bbTypesWithTimeseries;
  }

  /**{@inheritDoc}**/
  public TimeseriesQuery getTimeseriesQuery(Type<?> type) {
    List<BBAttribute> availableTimeseriesAttributes = Lists.newArrayList(Collections2.filter(getAvailableAttributes(type),
        new Predicate<BBAttribute>() {
          public boolean apply(BBAttribute input) {
            return input.getId().equals(BBAttribute.UNDEFINED_ID_VALUE) || input.isTimeseries();
          }
        }));
    return new TimeseriesQuery(availableTimeseriesAttributes);
  }
}
