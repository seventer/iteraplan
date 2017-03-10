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
package de.iteratec.iteraplan.presentation.dialog.MassUpdate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.MassUpdateData;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessMappingTypeMu;
import de.iteratec.iteraplan.businesslogic.service.GeneralBuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.MassUpdateService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.ConcurrentModificationException;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.presentation.dialog.ReportBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.InformationSystemInterfaceComponentModel;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.model.TransportAssociationComponentModel;
import de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.model.TransportInfoComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAssociationConfig;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttribute;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttributeConfig;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateLine;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateResult;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;


@Service("massUpdateFrontendService")
public class MassUpdateFrontendServiceImpl extends ReportBaseFrontendServiceImpl<ManageReportMemoryBean> implements MassUpdateFrontendService {

  private static final Logger         LOGGER = Logger.getIteraplanLogger(MassUpdateFrontendServiceImpl.class);

  @Autowired
  private MassUpdateService           massUpdateService;

  @Autowired
  private GeneralBuildingBlockService buildingBlockService;

  public void setMassUpdateService(MassUpdateService massUpdateService) {
    this.massUpdateService = massUpdateService;
  }

  public void setBuildingBlockService(GeneralBuildingBlockService buildingBlockService) {
    this.buildingBlockService = buildingBlockService;
  }

  public ManageReportMemoryBean getInitialMemBean() {
    ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBeanByWritePerms(UserContext.getCurrentPerms());
    memBean.getTabularOptions().setResultFormat(Constants.REPORTS_EXPORT_HTML);

    //set the report type to enable the initialization of the post processing strategies
    memBean.setReportType(ReportType.HTML);
    memBean.resetPostProcessingStrategies();
    return memBean;
  }

  public ManageReportMemoryBean getInitialMemBean(String tobString) {
    if (tobString == null) {
      return getInitialMemBean();
    }
    else {
      ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBean(tobString);
      memBean.setReportType(ReportType.HTML);
      memBean.resetPostProcessingStrategies();

      return memBean;
    }
  }

  @Override
  protected ManageReportMemoryBean getMemBeanForChangedQueryType(ManageReportMemoryBean memBean) {
    String selectedBuildingBlock = memBean.getSelectedBuildingBlock();

    ManageReportMemoryBean newMemBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(selectedBuildingBlock,
        memBean.getAvailableBbTypesForMassupdate());

    if (newMemBean == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    newMemBean.getTabularOptions().setResultFormat(Constants.REPORTS_EXPORT_HTML);
    newMemBean.setSelectedBuildingBlock(selectedBuildingBlock);
    return newMemBean;
  }

  public ManageReportMemoryBean fromInterchange(String bbType, String idList) {
    if (idList == null || bbType == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    ManageReportMemoryBean resultMemBean = getInitialMemBean();
    resultMemBean.setSelectedBuildingBlock(bbType);
    resultMemBean = getMemBeanForChangedQueryType(resultMemBean);
    getInitFormHelperService().dropRestrictionsFromQueryForm(resultMemBean.getQueryResult().getQueryForms().get(0));

    // cast needed so that the method in ReportBaseFrontendServiceImpl isn't ambiguous
    requestReport(resultMemBean, (HttpServletRequest) null, null);

    List<Integer> idsAsList = parseSelectedIds(idList);
    Integer[] ids = new Integer[idsAsList.size()];
    for (int i = 0; i < idsAsList.size(); i++) {
      ids[i] = idsAsList.get(i);
    }

    List<BuildingBlock> allEntities = buildingBlockService.getBuildingBlocksByType(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(bbType));
    removeVirtualElement(allEntities);
    Collections.sort(allEntities, new BuildingBlockComparator());
    List<DynamicQueryFormData<?>> queryForms = resultMemBean.getQueryResult().getQueryForms();
    resultMemBean.setQueryResult(new QueryResult(ManageReportBeanBase.MAIN_QUERY, queryForms, resultMemBean.getQueryResult().getTimeseriesQuery(),
        allEntities, ids, getReportType().getValue()));

    return resultMemBean;
  }

  public ManageReportMemoryBean massDelete(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    for (BuildingBlock buildingBlock : memBean.getQueryResult().getSelectedResults()) {
      massUpdateService.deleteBuildingBlock(buildingBlock);
    }

    // request the Report again, now the deleted Elements should not be shown anymore
    this.requestReport(memBean, context, flowContext);

    return memBean;
  }

  public ManageReportMemoryBean massSubscribe(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    for (BuildingBlock buildingBlock : memBean.getQueryResult().getSelectedResults()) {
      massUpdateService.subscribeBuildingBlock(buildingBlock, true);
    }

    return memBean;
  }

  public ManageReportMemoryBean massUnsubscribe(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    for (BuildingBlock buildingBlock : memBean.getQueryResult().getSelectedResults()) {
      massUpdateService.subscribeBuildingBlock(buildingBlock, false);
    }

    return memBean;
  }

  public MassUpdateMemoryBean prepareForMassUpdate(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    QueryResult queryResult = memBean.getQueryResult();
    MassUpdateData massUpdateData = queryResult.getQueryForms().get(0).getQueryUserInput().getMassUpdateData();

    int totalNumber = massUpdateData.getSelectedAssociationsList().size() + massUpdateData.getSelectedAttributesList().size()
        + massUpdateData.getSelectedPropertiesList().size();
    // at least one property, association or attribute has to be seleced
    if (totalNumber == 0) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.MASS_UPDATE_NO_PROPERTIES_SELECTED);
    }

    int maxSimultProps = IteraplanProperties.getIntProperty(IteraplanProperties.PROP_MAX_SIMULTANEOUS_MASSUPDATE_PROPERTIES);

    // at most "maxSimultEdits" properties, associations and attributes can be selected
    if (totalNumber > maxSimultProps) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.MASS_UPDATE_TOO_MANY_PROPERTIES, new Object[] { Integer.valueOf(maxSimultProps) });
    }

    int maxSimultBBs = IteraplanProperties.getIntProperty(IteraplanProperties.PROP_MAX_SIMULTANEOUS_MASSUPDATE_BUILDINGBLOCKS);
    // at least one and at most "maxSimultBBs" building blocks can be selected
    Integer[] selectedResultIds = queryResult.getSelectedResultIds();
    if ((selectedResultIds == null) || (selectedResultIds.length == 0) || (selectedResultIds.length > maxSimultBBs)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.MASS_UPDATE_TOO_MANY_BUILDINGBLOCKS, new Object[] { Integer.valueOf(maxSimultBBs) });

    }

    MassUpdateMemoryBean massMemBean = new MassUpdateMemoryBean(memBean.getMassUpdateType());

    // init the memBean
    massMemBean.initFromMemoryBean(memBean, massUpdateService);

    //this is a workaround to prevent LazyInitException when updating Business Domains
    visitAssociations(massMemBean.getLines());

    enterEditMode(flowContext);
    return massMemBean;
  }

  public MassUpdateMemoryBean updateComponentModel(MassUpdateMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    memBean.getStandardAssociationCM().update();
    for (MassUpdateLine<? extends BuildingBlock> line : memBean.getLines()) {
      line.getComponentModel().update();
      line.setMassUpdateResult(new MassUpdateResult());
    }

    return memBean;
  }

  private void visitAssociations(List<MassUpdateLine<? extends BuildingBlock>> lines) {
    for (MassUpdateLine<? extends BuildingBlock> line : lines) {
      BuildingBlock bb = line.getBuildingBlockToUpdate();
      if (bb instanceof BusinessDomain) {
        BusinessDomain bd = (BusinessDomain) bb;
        if (bd.getBusinessUnits() != null) {
          bd.getBusinessUnits().toString();
        }
        if (bd.getBusinessProcesses() != null) {
          bd.getBusinessProcesses().toString();
        }
        if (bd.getBusinessFunctions() != null) {
          bd.getBusinessFunctions().toString();
        }
        if (bd.getBusinessObjects() != null) {
          bd.getBusinessObjects().toString();
        }
        if (bd.getProducts() != null) {
          bd.getProducts().toString();
        }
      }
      else if (bb instanceof BusinessUnit) {
        BusinessUnit bu = (BusinessUnit) bb;
        if (bu.getBusinessDomains() != null) {
          bu.getBusinessDomains().toString();
        }
        if (bu.getBusinessMappings() != null) {
          this.viewBusinessMapping(bu.getBusinessMappings());
        }

      }
      else if (bb instanceof Product) {
        Product prod = (Product) bb;
        if (prod.getBusinessDomains() != null) {
          prod.getBusinessDomains().toString();
        }
        if (prod.getBusinessMappings() != null) {
          this.viewBusinessMapping(prod.getBusinessMappings());
        }
        if (prod.getAttributeValueAssignments() != null) {
          prod.getAttributeValueAssignments().toString();
        }
      }
      else if (bb instanceof BusinessProcess) {
        BusinessProcess bp = (BusinessProcess) bb;
        if (bp.getBusinessDomains() != null) {
          bp.getBusinessDomains().toString();
        }
        if (bp.getAttributeValueAssignments() != null) {
          bp.getAttributeValueAssignments().toString();
        }
        if (bp.getBusinessMappings() != null) {
          this.viewBusinessMapping(bp.getBusinessMappings());
        }
      }

    }
  }

  public MassUpdateMemoryBean setStandardAttributeValues(MassUpdateMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    // for every MassUpdateAttributeConfig (i.e. for every column of attributes in the gui)
    for (MassUpdateAttributeConfig config : memBean.getMassUpdateAttributeConfig()) {

      // check if the button to take over standard values for the current column has been clicked.
      // at most one column will be true!
      if (config.isSetStandardValue()) {

        if ((config.getStandardNewAttributeValue() != null) && !"".equals(config.getStandardNewAttributeValue())) {
          // check if the entered information is correct
          if (BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE.equals(config.getType())) {
            // try to parse the number - if no number, an exception is thrown
            BigDecimalConverter.parse(config.getStandardNewAttributeValue(), true, UserContext.getCurrentLocale());
          }
          if (BBAttribute.USERDEF_DATE_ATTRIBUTE_TYPE.equals(config.getType())) {
            // try to parse the date - if no date, an exception is thrown
            DateUtils.parseAsDate(config.getStandardNewAttributeValue(), UserContext.getCurrentLocale());
            if (config.getStandardNewAttributeValue().length() > 10) {
              throw new IteraplanBusinessException(IteraplanErrorMessages.INCORRECT_DATE_FORMAT);
            }
          }
        }

        for (MassUpdateLine<? extends BuildingBlock> line : memBean.getLines()) {
          // reset the Result Container
          line.setMassUpdateResult(new MassUpdateResult());
          // set the component model for each line
          MassUpdateAttribute attribute = line.getMassUpdateAttribute(config.getAttributeTypeId());
          // only copy the value if the checkbox to use the standard attributevalue is checked
          if (attribute.getMassUpdateAttributeItem().isUsesStandardAttributeValues()) {
            if (BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE.equals(attribute.getType())) {
              attribute.getMassUpdateAttributeItem().setSelectedAttributeValueStringIds(config.getSelectedStandardAtributeValueStringIds());
              attribute.getMassUpdateAttributeItem().setNewAttributeValue(null);
            }
            else if (BBAttribute.USERDEF_RESPONSIBILITY_ATTRIBUTE_TYPE.equals(attribute.getType())) {
              attribute.getMassUpdateAttributeItem().setSelectedAttributeValueStringIds(config.getSelectedStandardAtributeValueStringIds());
              attribute.getMassUpdateAttributeItem().setNewAttributeValue(null);
            }
            else {
              attribute.getMassUpdateAttributeItem().setNewAttributeValue(config.getStandardNewAttributeValue());
            }
          }
        }
      }
    }

    return memBean;
  }

  @SuppressWarnings({ "rawtypes", "PMD.ExcessiveMethodLength" })
  public MassUpdateMemoryBean setStandardAssociationValues(MassUpdateMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    // Run through config objects - i.e. run through columns of associations.
    for (MassUpdateAssociationConfig config : memBean.getMassUpdateAssociationConfig()) {

      // isSetStandardValue is true if the take over button in the GUI of the column, the config
      // represents, was pressed.
      if (!config.isSetStandardValue()) {
        continue;
      }

      try {
        // Fetch the component model representing the associations to the building blocks by
        // reflection. This cm is bound to the standard value field in the GUI.

        // The type of the component model that extends MassUpdateComponentModel depends on the type
        // of building block that is mass updated. In order to keep the method of accessing this
        // component model generic it is accessed
        // through its super class MassUpdateComponentModel. This access has to be done by
        // reflection as the sub class is not known.
        // To do this the (building block specific) getter method name for the cm
        // has to be used, which is fetched from the config. The method name is set during
        // initialization of the association config (MassUpdateConfigurator).
        Object manyAssociationSetCMObjectOfStandardAssociationCM = memBean.getStandardAssociationCM().getClass()
            .getMethod(config.getMethodNameOfGetModelOfAssociationMethod()).invoke(memBean.getStandardAssociationCM());

        if (!(manyAssociationSetCMObjectOfStandardAssociationCM instanceof ManyAssociationSetComponentModel)) {
          throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
        }
        // it's now safe to cast
        ManyAssociationSetComponentModel cmOfStandardAssociationCM = (ManyAssociationSetComponentModel) manyAssociationSetCMObjectOfStandardAssociationCM;
        cmOfStandardAssociationCM.update();

        @SuppressWarnings("unchecked")
        List<? extends IdEntity> standardListOfConnectedElements = cmOfStandardAssociationCM.getConnectedElements();

        List<Integer> idList = new ArrayList<Integer>();

        for (IdEntity standardConnectedElement : standardListOfConnectedElements) {
          idList.add(standardConnectedElement.getId());
        }

        // setElementIdsToAdd expects an array
        Integer[] idArray = new Integer[idList.size()];
        idArray = idList.toArray(idArray);

        for (MassUpdateLine<? extends BuildingBlock> line : memBean.getLines()) {

          // Every line object has a list of Booleans called associations. These Booleans are
          // bound to a <form:checkbox/> in MassUpdateLines.jsp. If a checkbox in front of an
          // association of a certain line is unselected,
          // the Boolean value at the list position of the association is set to FALSE.
          // The take over of the standard value is only executed if the Boolean at the index
          // position of the config is TRUE.
          if (line.getAssociations().get(memBean.getMassUpdateAssociationConfig().indexOf(config)).equals(Boolean.TRUE)) {

            // Fetch the component model representing the associations to the building blocks by
            // reflection. It belongs to the building block that is mass updated represented by a
            // line object.
            Object manyAssociationSetComponentModelObjectOfLineCM = line.getComponentModel().getClass()
                .getMethod(config.getMethodNameOfGetModelOfAssociationMethod()).invoke(line.getComponentModel());

            if (!(manyAssociationSetComponentModelObjectOfLineCM instanceof ManyAssociationSetComponentModel)) {
              throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
            }
            // it's now safe to cast
            ManyAssociationSetComponentModel cmOfLineCM = (ManyAssociationSetComponentModel) manyAssociationSetComponentModelObjectOfLineCM;

            cmOfLineCM.setElementIdsToAdd(idArray);

            // If the association is the special case of a transport association, create a map
            // of the element ids (key) and transport directions (value) of the standard transport
            // association.
            // This map is used during update to correctly take over the directions from the
            // standard component model to the line component models.

            if ((cmOfLineCM instanceof TransportAssociationComponentModel)
                && (cmOfStandardAssociationCM instanceof TransportAssociationComponentModel)) {

              List<Transport> transports = ((TransportAssociationComponentModel) cmOfStandardAssociationCM).getConnectedElements();
              TransportAssociationComponentModel transportLineCM = (TransportAssociationComponentModel) cmOfLineCM;
              for (Transport transport : transports) {
                transportLineCM.getTransportDirectionsToAdd().put(transport.getBusinessObject().getId(), transport.getTransportKey());
              }
            }
          }
        }
        // catch all the potential exceptions from reflection and wrap into technical exceptions
      } catch (SecurityException se) {
        throw new IteraplanTechnicalException(se);
      } catch (IllegalArgumentException e) {
        throw new IteraplanTechnicalException(e);
      } catch (IllegalAccessException e) {
        throw new IteraplanTechnicalException(e);
      } catch (InvocationTargetException e) {
        throw new IteraplanTechnicalException(e);
      } catch (NoSuchMethodException e) {
        throw new IteraplanTechnicalException(e);
      }
    }
    return memBean;
  }

  public MassUpdateMemoryBean runMassUpdate(MassUpdateMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    LOGGER.debug("Starting mass update...");

    boolean hadPropertiesOrAssociations;
    // update the BuildingBlock only if at least one property or association has possibly changed
    if (!(memBean.getMassUpdatePropertyConfig().isEmpty() && memBean.getMassUpdateAssociationConfig().isEmpty())) {
      for (MassUpdateLine<? extends BuildingBlock> line : memBean.getLines()) {
        BuildingBlock buildingBlockToUpdate = line.getBuildingBlockToUpdate();
        BuildingBlock buildingBlock = buildingBlockService.merge(buildingBlockToUpdate);
        line.setBuildingBlockToUpdate(buildingBlock);
      }

      // peform the update for each line
      for (MassUpdateLine<? extends BuildingBlock> line : memBean.getLines()) {
        if (line.getBuildingBlockToUpdate().getTypeOfBuildingBlock().equals(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE)) {
          InformationSystemInterfaceComponentModel isiCM = (InformationSystemInterfaceComponentModel) line.getComponentModel();
          TransportInfoComponentModel transportCM = isiCM.getTransportInfoModel();
          transportCM.update();
          isiCM.update();
        }
        else {
          line.getComponentModel().update();
        }
        MassUpdateResult result = new MassUpdateResult();
        line.setMassUpdateResult(result);
        if (line.isSelectedForMassUpdate()) {
          // update the buildingblock
          try {
            if (memBean.getMassUpdateType() instanceof BusinessMappingTypeMu) {
              // special treatment for business mappings
              massUpdateService.updateBusinessMappingLine(line);
            }
            else {
              // default update of BuildingBlocks
              massUpdateService.updateLine(line);
              result.setWasExecuted(true);
            }
            result.setWasSuccessful(true);
          } catch (RuntimeException ex) {
            storeExceptionInResult(ex, result);
          }
        }
      }
      hadPropertiesOrAssociations = true;
    }
    else {
      hadPropertiesOrAssociations = false;
    }

    if (!memBean.getMassUpdateAttributeConfig().isEmpty()) {
      // handle the attributes.
      processAttributes(memBean, massUpdateService, hadPropertiesOrAssociations);
    }

    leaveEditMode(flowContext);
    return memBean;
  }

  /**
   * handle the attributes. This is done in a separate for loop because the BuildingBlocks change in
   * the first loop. If the attributes were changed at the same time, optimistic locking exceptions
   * would occur due to the fact that the version ID is changed by updateLine /
   * updateBusinessMappingLine
   *
   * @param memBean
   *          The massupdate MemBean
   * @param service
   *          The mass update service class
   * @param hadPropertiesOrAssociations
   */
  private void processAttributes(MassUpdateMemoryBean memBean, MassUpdateService service, boolean hadPropertiesOrAssociations) {
    for (MassUpdateLine<? extends BuildingBlock> line : memBean.getLines()) {
      MassUpdateResult result = line.getMassUpdateResult();
      if (!hadPropertiesOrAssociations) {
        result = new MassUpdateResult();
        line.setMassUpdateResult(result);
      }
      // only update the attribute if the line is selected and no exception occurred so far
      if (line.isSelectedForMassUpdate() && (result.getException() == null)) {
        try {
          boolean attributesUpdated = service.updateAttributes(line);
          if (!hadPropertiesOrAssociations) {
            // set successful to true if the result is new - otherwise the state will either be true
            // already
            // or must be kept false
            result.setWasSuccessful(true);
          }
          result.setWasExecuted(attributesUpdated);
        } catch (RuntimeException ex) {
          result.setAttributeException(true);
          storeExceptionInResult(ex, result);
        }
      }
    }
  }

  private void storeExceptionInResult(RuntimeException ex, MassUpdateResult result) {
    result.setWasExecuted(true);
    result.setWasSuccessful(false);
    result.setException(ex);

    if (ex instanceof ConcurrentModificationException) {
      result.setException(new IteraplanBusinessException(IteraplanErrorMessages.MASS_UPDATE_CONCURRENT_MODIFICATION));
    }
    else if (ex instanceof DataIntegrityViolationException) {
      result.setException(new IteraplanBusinessException(IteraplanErrorMessages.ELEMENT_VIOLATES_CONSTRAINT));
    }
    else if (ex instanceof IteraplanBusinessException) {
      LOGGER.debug(ex);
    }
    else {
      LOGGER.error("An exception has occured in the mass update logic.", ex);
    }
  }

  private void viewBusinessMapping(Set<BusinessMapping> bmSet) {
    bmSet.toString();
    for (BusinessMapping bm : bmSet) {
      if (bm.getAttributeValueAssignments() != null) {
        bm.getAttributeValueAssignments().toString();
      }
      if (bm.getBusinessProcess() != null) {
        bm.getBusinessProcess().toString();
      }
      if (bm.getBusinessUnit() != null) {
        bm.getBusinessUnit().toString();
      }
      if (bm.getInformationSystemRelease() != null) {
        bm.getInformationSystemRelease().toString();
      }
    }
  }

  @Override
  protected String getFlowId() {
    return Dialog.MASS_UPDATE.getFlowId();
  }

  @Override
  protected ReportType getReportType() {
    return ReportType.HTML;
  }

  /**
   * when loading a saved query for mass update, make sure the result format gets set to html
   */
  @Override
  public ManageReportMemoryBean loadSavedQuery(ManageReportMemoryBean memBean) {
    if (memBean.getTabularOptions() != null) {
      memBean.getTabularOptions().setResultFormat(Constants.REPORTS_EXPORT_HTML);
    }
    return super.loadSavedQuery(memBean);
  }
}
