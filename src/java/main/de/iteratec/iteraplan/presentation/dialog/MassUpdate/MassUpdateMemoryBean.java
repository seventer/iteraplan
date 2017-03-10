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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QUserInput;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessMappingTypeMu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.MassUpdateType;
import de.iteratec.iteraplan.businesslogic.service.MassUpdateService;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.ArchitecturalDomainCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.BusinessObjectCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.BusinessProcessCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.InformationSystemDomainCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.InformationSystemInterfaceCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.InformationSystemReleaseCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.InfrastructureElementCmMu;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAssociationConfig;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateAttributeConfig;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateBusinessDomainComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateBusinessFunctionComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateBusinessUnitComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateConfigurator;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateLine;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateProductComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateProjectComponentModel;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdatePropertyConfig;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.TechnicalComponentReleaseCmMu;
import de.iteratec.iteraplan.presentation.dialog.common.BaseMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;


/**
 * Memory Bean for mass updates. Holds all information for carrying out a mass update.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MassUpdateMemoryBean extends BaseMemBean {
  private static final long                             serialVersionUID = -7047663864319853020L;

  /** Holds meta information about the BuildingBlock that will be updated * */
  private MassUpdateType                                massUpdateType;
  /** Lines holding the data for each selected buildingblock* */
  private List<MassUpdateLine<? extends BuildingBlock>> lines;
  /** Configuration properties for each MassUpdate column containing properties* */
  private List<MassUpdatePropertyConfig>                massUpdatePropertyConfig;
  /** Configuration properties for each MassUpdate column containing associations* */
  private List<MassUpdateAssociationConfig>             massUpdateAssociationConfig;
  /*************************************************************************************************
   * Configuration properties for each MassUpdate column containing attributes. Furthermore serves
   * as component model for the "Standard Value" dialogs
   ************************************************************************************************/
  private List<MassUpdateAttributeConfig>               massUpdateAttributeConfig;
  /*************************************************************************************************
   * The attributes that will be part of the mass update (selected by the user) Format:
   * {@link de.iteratec.iteraplan.model.attribute.BBAttribute#getStringId()}
   ************************************************************************************************/
  private List<String>                                  attributeIds;

  /** ComponentModel holding standard association info, used for loading standard association field **/
  private MassUpdateComponentModel                      componentModelForStandardAssociations;

  private Boolean                                       checkAllBox      = Boolean.TRUE;

  public MassUpdateType getMassUpdateType() {
    return massUpdateType;
  }

  public List<String> getAttributeIds() {
    return attributeIds;
  }

  public List<MassUpdateLine<? extends BuildingBlock>> getLines() {
    return lines;
  }

  public void setMassUpdateType(MassUpdateType massUpdateType) {
    this.massUpdateType = massUpdateType;
  }

  public MassUpdateMemoryBean(MassUpdateType massUpdateType) {
    this.massUpdateType = massUpdateType;
  }

  public List<MassUpdatePropertyConfig> getMassUpdatePropertyConfig() {
    return massUpdatePropertyConfig;
  }

  public List<MassUpdateAssociationConfig> getMassUpdateAssociationConfig() {
    return massUpdateAssociationConfig;
  }

  public List<MassUpdateAttributeConfig> getMassUpdateAttributeConfig() {
    return massUpdateAttributeConfig;
  }

  public Boolean getCheckAllBox() {
    return checkAllBox;
  }

  public int getTotalNumberOfColumns() {
    int ret = 0;
    if (massUpdatePropertyConfig != null) {
      ret += massUpdatePropertyConfig.size();
    }
    if (massUpdateAssociationConfig != null) {
      ret += massUpdateAssociationConfig.size();
    }
    if (massUpdateAttributeConfig != null) {
      ret += massUpdateAttributeConfig.size();
    }
    return ret;
  }

  /**
   * Takes the {@link ManageReportMemoryBean} and initializes the MassUpdate memBean. For each
   * BuildingBlock the user selected a {@link MassUpdateLine} and a ComponentModel is created. The
   * properties and associations selected by the user are initialized on the componentmodel.
   * 
   * @param manageReportMemoryBean
   *          The bean containing the configuration information for the massupdate
   */
  public void initFromMemoryBean(ManageReportMemoryBean manageReportMemoryBean, MassUpdateService massUpdateService) {
    lines = new ArrayList<MassUpdateLine<? extends BuildingBlock>>();
    QueryResult queryResult = manageReportMemoryBean.getQueryResult();

    QUserInput userInput = queryResult.getQueryForms().get(0).getQueryUserInput();

    List<String> properties = userInput.getMassUpdateData().getSelectedPropertiesList();

    List<String> associations = userInput.getMassUpdateData().getSelectedAssociationsList();

    this.attributeIds = userInput.getMassUpdateData().getSelectedAttributesList();

    List<? extends BuildingBlock> selectedResults = queryResult.getSelectedResults();
    for (BuildingBlock buildingBlock : selectedResults) {
      MassUpdateLine<? extends BuildingBlock> line = initMassUpdateLine(buildingBlock, properties, associations, massUpdateService);
      if (line != null) {
        lines.add(line);
      }
    }
    initAttributes(attributeIds, massUpdateService);

    initComponentModelForStandardAssociations(selectedResults.get(0).getBuildingBlockType().getTypeOfBuildingBlock(), properties, associations);

  }

  /**
   * Initializes the component model that is bound to the standard association field in the GUI (see
   * MassUpdateStandardValues.jsp).
   * 
   * @param typeOfBuildingBlock
   *          to be mass updated
   * @param properties
   *          (user input)
   * @param associations
   *          (user input)
   */
  private void initComponentModelForStandardAssociations(TypeOfBuildingBlock typeOfBuildingBlock, List<String> properties, List<String> associations) {
    BuildingBlock buildingBlockForStandardAssociation = null;
    switch (typeOfBuildingBlock) {
      case ARCHITECTURALDOMAIN:
        buildingBlockForStandardAssociation = new ArchitecturalDomain();
        componentModelForStandardAssociations = new ArchitecturalDomainCmMu();
        break;
      case BUSINESSFUNCTION:
        buildingBlockForStandardAssociation = new BusinessFunction();
        componentModelForStandardAssociations = new MassUpdateBusinessFunctionComponentModel();
        break;
      case BUSINESSOBJECT:
        buildingBlockForStandardAssociation = new BusinessObject();
        componentModelForStandardAssociations = new BusinessObjectCmMu();
        break;
      case BUSINESSPROCESS:
        buildingBlockForStandardAssociation = new BusinessProcess();
        componentModelForStandardAssociations = new BusinessProcessCmMu();
        break;
      case TECHNICALCOMPONENTRELEASE:
        buildingBlockForStandardAssociation = new TechnicalComponentRelease();
        new TechnicalComponent().addRelease((TechnicalComponentRelease) buildingBlockForStandardAssociation);
        componentModelForStandardAssociations = new TechnicalComponentReleaseCmMu();
        break;
      case INFRASTRUCTUREELEMENT:
        buildingBlockForStandardAssociation = new InfrastructureElement();
        componentModelForStandardAssociations = new InfrastructureElementCmMu();
        break;
      case INFORMATIONSYSTEMINTERFACE:
        buildingBlockForStandardAssociation = new InformationSystemInterface();
        componentModelForStandardAssociations = new InformationSystemInterfaceCmMu();
        break;
      case BUSINESSDOMAIN:
        buildingBlockForStandardAssociation = new BusinessDomain();
        componentModelForStandardAssociations = new MassUpdateBusinessDomainComponentModel();
        break;
      case INFORMATIONSYSTEMDOMAIN:
        buildingBlockForStandardAssociation = new InformationSystemDomain();
        componentModelForStandardAssociations = new InformationSystemDomainCmMu();
        break;
      case INFORMATIONSYSTEMRELEASE:
        buildingBlockForStandardAssociation = new InformationSystemRelease();
        new InformationSystem().addRelease((InformationSystemRelease) buildingBlockForStandardAssociation);
        componentModelForStandardAssociations = new InformationSystemReleaseCmMu();
        break;
      case PROJECT:
        buildingBlockForStandardAssociation = new Project();
        componentModelForStandardAssociations = new MassUpdateProjectComponentModel();
        break;
      case PRODUCT:
        buildingBlockForStandardAssociation = new Product();
        componentModelForStandardAssociations = new MassUpdateProductComponentModel();
        break;
      case BUSINESSUNIT:
        buildingBlockForStandardAssociation = new BusinessUnit();
        componentModelForStandardAssociations = new MassUpdateBusinessUnitComponentModel();
        break;

      default:
        break;
    }

    componentModelForStandardAssociations.initializeFrom(buildingBlockForStandardAssociation, properties, associations);
  }

  /**
   * Initializes the attributes taking part in the mass update. The set of
   * {@link MassUpdateAttributeConfig}s is initialized. Both the current attribute values and the
   * values for enums are retrieved from the db.
   * 
   * @param attrIds
   *          The ids of the attributes comming from the GUI
   */
  private void initAttributes(List<String> attrIds, MassUpdateService massUpdateService) {
    massUpdateAttributeConfig = new ArrayList<MassUpdateAttributeConfig>();
    int attributeIndex = 0;
    for (String selectedAttributeId : attrIds) {
      // initialization of attributes moved behind transaction boundaries to make DB calls more
      // efficient
      massUpdateService.initAttributes(selectedAttributeId, lines, massUpdateAttributeConfig, attributeIndex);
      attributeIndex++;
    }
    Collections.sort(massUpdateAttributeConfig);
  }

  /**
   * Creates a line for each buildingblock. Each line holds a component model for the corresponding
   * buildingblock, having the properties and associations selected by the user initialized.
   * 
   * @param buildingBlock
   *          The buildingblock for which the line is created
   * @return The mass update line
   */
  private MassUpdateLine<? extends BuildingBlock> initMassUpdateLine(BuildingBlock buildingBlock, List<String> properties, List<String> associations,
                                                           MassUpdateService massUpdateService) {
    
    MassUpdateComponentModel componentModel = initCm(buildingBlock);
    MassUpdateLine<? extends BuildingBlock> line = initLine(buildingBlock);
    MassUpdateConfigurator configurator = new MassUpdateConfigurator();
    
    if (line != null && componentModel != null) {
      if (massUpdatePropertyConfig == null) {
        setConfigurationSets(properties, associations, componentModel, configurator);
      }
      BuildingBlock initializedBuildingBlock = massUpdateService.initComponentModel(componentModel, buildingBlock, properties, associations);
      if (initializedBuildingBlock == null) {
        return null;
      }
      line.setBuildingBlockToUpdate(initializedBuildingBlock);
      line.setComponentModel(componentModel);

      // Add a Boolean.TRUE for every association the line has. Each Boolean is bound to
      // a "take over standard associations" checkbox in the GUI (MassUpdateLines.jsp) and is checked
      // by default.
      List<Boolean> associationsToTakeOver = new ArrayList<Boolean>();
      for (int i = 0; i < associations.size(); i++) {
        associationsToTakeOver.add(Boolean.TRUE);
      }
      line.setAssociations(associationsToTakeOver);

      return line;
    }
    throw new IllegalArgumentException("MassUpdateLine for generic BuildingBlock not available. MassUpdateLine for " + buildingBlock.getClass()
        + " has to be configured in MassUpdateMemoryBean.class");
  }
  
  /**
   * @param buildingBlock
   * @return initialized MassUpdateLine
   */
  private MassUpdateLine<? extends BuildingBlock> initLine(BuildingBlock buildingBlock) {
    if (buildingBlock instanceof ArchitecturalDomain) {
      return new MassUpdateLine<ArchitecturalDomain>();
    }
    else if (buildingBlock instanceof BusinessFunction) {
      return new MassUpdateLine<BusinessFunction>();
    }
    else if (buildingBlock instanceof BusinessObject) {
      return new MassUpdateLine<BusinessObject>();
    }
    else if (buildingBlock instanceof BusinessProcess) {
      return new MassUpdateLine<BusinessProcess>();
    }
    else if (buildingBlock instanceof TechnicalComponentRelease) {
      return new MassUpdateLine<TechnicalComponentRelease>();
    }
    else if (buildingBlock instanceof InfrastructureElement) {
      return new MassUpdateLine<InfrastructureElement>();
    }
    else if (buildingBlock instanceof InformationSystemInterface) {
      return new MassUpdateLine<InformationSystemInterface>();
    }
    else if (buildingBlock instanceof BusinessDomain) {
      return new MassUpdateLine<BusinessDomain>();
    }
    else if (buildingBlock instanceof InformationSystemDomain) {
      return new MassUpdateLine<InformationSystemDomain>();
    }
    else if (buildingBlock instanceof InformationSystemRelease) {
      return new MassUpdateLine<InformationSystemRelease>();
    }
    else if (buildingBlock instanceof Project) {
      return new MassUpdateLine<Project>();
    }
    else if (buildingBlock instanceof Product) {
      return new MassUpdateLine<Product>();
    }
    else if (buildingBlock instanceof BusinessUnit) {
      return new MassUpdateLine<BusinessUnit>();
    }
    
    return null;
  }

  /**
   * @param buildingBlock
   * @return initialized MassUpdateComponentModel 
   */
  private MassUpdateComponentModel initCm(BuildingBlock buildingBlock) {
    if (buildingBlock instanceof ArchitecturalDomain) {
      return new ArchitecturalDomainCmMu();
    }
    else if (buildingBlock instanceof BusinessFunction) {
      return new MassUpdateBusinessFunctionComponentModel();
    }
    else if (buildingBlock instanceof BusinessObject) {
      return new BusinessObjectCmMu();
    }
    else if (buildingBlock instanceof BusinessProcess) {
      return new BusinessProcessCmMu();
    }
    else if (buildingBlock instanceof TechnicalComponentRelease) {
      return new TechnicalComponentReleaseCmMu();
    }
    else if (buildingBlock instanceof InfrastructureElement) {
      return new InfrastructureElementCmMu();
    }
    else if (buildingBlock instanceof InformationSystemInterface) {
      return new InformationSystemInterfaceCmMu();
    }
    else if (buildingBlock instanceof BusinessDomain) {
      return new MassUpdateBusinessDomainComponentModel();
    }
    else if (buildingBlock instanceof InformationSystemDomain) {
      return new InformationSystemDomainCmMu();
    }
    else if (buildingBlock instanceof InformationSystemRelease) {
      return new InformationSystemReleaseCmMu();
    }
    else if (buildingBlock instanceof Project) {
      return new MassUpdateProjectComponentModel();
    }
    else if (buildingBlock instanceof Product) {
      return new MassUpdateProductComponentModel();
    }
    else if (buildingBlock instanceof BusinessUnit) {
      return new MassUpdateBusinessUnitComponentModel();
    }
    
    return null;
  }

  /**
   * Set the configurations sets - needed by the GUI both to display column headers and to determine
   * the sorting order of associations and properties. Needs to be done only once as
   * massUpdatePropertyConfig contains meta information valid for all lines
   * 
   * @param properties
   * @param associations
   * @param componentModel
   * @param configurator
   */
  private void setConfigurationSets(List<String> properties, List<String> associations, MassUpdateComponentModel componentModel,
                                    MassUpdateConfigurator configurator) {

    // Business Mappings need to be treated as special because the associations that were not
    // selected
    // by the user need to be concatenated for the header string.
    if (this.getMassUpdateType() instanceof BusinessMappingTypeMu) {
      if (associations.size() > 2) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.MASS_UPDATE_BUSINESS_MAPPING_TOO_MANY_ASSOCIATIONS);
      }
      massUpdatePropertyConfig = new ArrayList<MassUpdatePropertyConfig>();
      for (String key : BusinessMappingTypeMu.getMissingAssociationKeys(associations)) {
        MassUpdatePropertyConfig config = new MassUpdatePropertyConfig();
        config.setHeaderKey(key);
        massUpdatePropertyConfig.add(config);
      }
      massUpdateAssociationConfig = configurator.configureAssociations(massUpdateType, associations, componentModel);
    }
    else {
      // remaining buildingblocks
      massUpdatePropertyConfig = configurator.configureProperties(massUpdateType, properties, componentModel);
      massUpdateAssociationConfig = configurator.configureAssociations(massUpdateType, associations, componentModel);
    }
  }

  /**
   * Not supported for this class
   */
  public ComponentModel<?> getComponentModel() {
    throw new UnsupportedOperationException();
  }

  /**
   * Not supported for this class
   * 
   * @param componentModel
   */
  public void setComponentModel(ComponentModel componentModel) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void validateEdit(Errors errors) {
    // nothing to do here
  }

  public MassUpdateComponentModel getStandardAssociationCM() {
    return componentModelForStandardAssociations;
  }

  public void setStandardAssociationCM(MassUpdateComponentModel someMassUpdateCM) {
    this.componentModelForStandardAssociations = someMassUpdateCM;
  }

  public void setCheckAllBox(Boolean checkAllBox) {
    this.checkAllBox = checkAllBox;
  }

}
