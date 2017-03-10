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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportOptionsWithOrderedList;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


/**
 * Container for the parameters of the Cluster diagram.
 */
public class ClusterOptionsBean extends GraphicalExportOptionsWithOrderedList implements IClusterOptions, Serializable {

  /** Serialization version. */
  private static final long                   serialVersionUID               = 4677329221498139781L;
  private int                                 selectedAttributeType          = -1;
  private List<String>                        selectedAttributeValues        = CollectionUtils.arrayList();

  private List<AttributeType>                 availableAttributeTypes        = CollectionUtils.arrayList();

  private int                                 selectedHierarchicalUpperLevel = 1;
  private int                                 selectedHierarchicalLowerLevel = 1;

  private String                              selectedHierarchicalLevels;

  private static final String                 RANGE_SPLIT                    = "_";

  private int                                 availableHierarchicalLevels;

  private String                              selectedClusterMode            = Constants.REPORTS_EXPORT_CLUSTER_MODE_BB;

  private final List<String>                  availableClusterModes          = CollectionUtils.arrayList();

  private boolean                             swimlaneContent;

  private static final int                    NONE                           = -1;

  private Map<String, ClusterSecondOrderBean> secondOrderBeansMap            = new HashMap<String, ClusterSecondOrderBean>();

  private boolean                             checkAllBox                    = true;
  /**
   * Contains the building block types in the order in which they are to be drawn.
   */
  private List<String>                        typeOrder;

  public ClusterOptionsBean() {
    super();
    this.setSelectedBbType(Constants.BB_BUSINESSDOMAIN_PLURAL);
    initializeClusterOptions();
  }

  private void initializeClusterOptions() {

    getColorOptionsBean().setAvailableColors(SpringGuiFactory.getInstance().getClusterColors());

    availableClusterModes.add(Constants.REPORTS_EXPORT_CLUSTER_MODE_BB);
    availableClusterModes.add(Constants.REPORTS_EXPORT_CLUSTER_MODE_ATTRIBUTE);

    setSelectedGraphicFormat(Constants.REPORTS_EXPORT_GRAPHICAL_SVG);
    setAvailableGraphicFormats(ExportOption.getGraphicalExportOptions(false));

    typeOrder = new ArrayList<String>();
  }

  /**
   * Initializes the second order beans of a cluster graphic.
   * 
   * @param typeToAttributeListMap
   *          A map that links every connected {@link de.iteratec.iteraplan.model.BuildingBlockType BuildingBlockType}
   *          to a list of {@link AttributeType} containing its attributes.
   * @param connectedAttributes
   *          A list of the connected {@link BBAttribute}s. The list is empty if an attribute type
   *          is selected for the main axis. The list is filled with the related attributes for
   *          which the user has permissions if a building block type is selected for the main axis.
   */
  public void configureSecondOrderBeans(Map<TypeOfBuildingBlock, List<BBAttribute>> typeToAttributeListMap, List<BBAttribute> connectedAttributes) {

    // Add the building block types
    for (Entry<TypeOfBuildingBlock, List<BBAttribute>> entry : typeToAttributeListMap.entrySet()) {
      final String representedType = entry.getKey().getValue();

      final TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(getSelectedBbType());
      if (tobb.equals(entry.getKey()) && Constants.REPORTS_EXPORT_CLUSTER_MODE_BB.equals(selectedClusterMode)) {
        final List<String> selfReferences = entry.getKey().getSelfReferencesPropertyKeys();
        for (String secondOrderBeanName : selfReferences) {
          if (!secondOrderBeanName.endsWith(".parent") && !secondOrderBeanName.endsWith(".generalisation")) {
            addSecondOrderBean(secondOrderBeanName, representedType, entry.getValue(), ClusterSecondOrderBean.BUILDING_BLOCK_BEAN,
                Integer.valueOf(-1));
          }
        }
      }
      else {
        addSecondOrderBean(representedType, representedType, entry.getValue(), ClusterSecondOrderBean.BUILDING_BLOCK_BEAN, Integer.valueOf(-1));
      }
    }

    // Add the attributes
    for (BBAttribute attribute : connectedAttributes) {
      addSecondOrderBean(attribute.getName(), attribute.getName(), new ArrayList<BBAttribute>(), ClusterSecondOrderBean.ATTRIBUTE_BEAN,
          attribute.getId());
    }

  }

  /**
   * Reorders building blocks according to user's input.
   */
  @Override
  public void refreshOrder() {
    if (getMovedItem() != NONE) {
      switch (getMoveType()) {
        case TOP:
          typeOrder.add(0, typeOrder.remove(getMovedItem()));
          break;
        case UP:
          if (getMovedItem() > 0) {
            typeOrder.add(getMovedItem() - 1, typeOrder.remove(getMovedItem()));
          }
          break;
        case DOWN:
          if (getMovedItem() < (typeOrder.size() - 1)) {
            typeOrder.add(getMovedItem() + 1, typeOrder.remove(getMovedItem()));
          }
          break;
        case BOTTOM:
          final int position = typeOrder.size() - 1;
          typeOrder.add(position, typeOrder.remove(getMovedItem()));
          break;
        default:
          // Nothing to do.
          break;
      }
      // reset after performing movement
      setMovedItem(NONE);
      setMove(Movement.HOLD_POSITION.toInteger());
    }

    moveUncheckedElementsToBottom();
  }

  private void moveUncheckedElementsToBottom() {
    final LinkedList<String> toBottom = new LinkedList<String>();
    for (String type : typeOrder) {
      final ClusterSecondOrderBean secondOrderBean = getSecondOrderBean(type);
      if (secondOrderBean == null) {
        throw new IteraplanBusinessException();
      }
      if (!secondOrderBean.isSelected()) {
        toBottom.addLast(type);
      }
    }
    for (String type : toBottom) {
      typeOrder.remove(type);
      typeOrder.add(typeOrder.size(), type);
    }
  }

  public List<ClusterSecondOrderBean> getSecondOrderBeans() {
    final ArrayList<ClusterSecondOrderBean> colorDimensions = new ArrayList<ClusterSecondOrderBean>(secondOrderBeansMap.size());
    for (String type : typeOrder) {
      final ClusterSecondOrderBean secondOrderBean = getSecondOrderBean(type);
      if (secondOrderBean != null) {
        colorDimensions.add(secondOrderBean);
      }
    }
    return colorDimensions;
  }

  @Override
  public void switchDimensionOptionsToPresentationMode() {

    super.switchDimensionOptionsToPresentationMode();
    for (ClusterSecondOrderBean bean : secondOrderBeansMap.values()) {
      bean.getColorOptions().switchToPresentationMode();
    }
  }

  @Override
  public void switchDimensionOptionsToGenerationMode() {

    super.switchDimensionOptionsToGenerationMode();
    for (ClusterSecondOrderBean bean : secondOrderBeansMap.values()) {
      bean.getColorOptions().switchToGenerationMode();
    }
  }

  public List<String> getOrderedColorDimensionsKeyList() {
    return typeOrder;
  }

  public int getSelectedAttributeType() {
    return selectedAttributeType;
  }

  private void addSecondOrderBean(String secondOrderBeanName, String representedType, List<BBAttribute> availableAttributes, String beanType,
                                  Integer attributeId) {
    final ClusterSecondOrderBean bean = new ClusterSecondOrderBean(secondOrderBeanName, representedType, availableAttributes, beanType);
    bean.getColorOptions().setDimensionAttributeId(attributeId);
    secondOrderBeansMap.put(secondOrderBeanName, bean);
    if (!bean.isSelected()) {
      setCheckAllBox(false);
    }
    typeOrder.add(secondOrderBeanName);
  }

  public ClusterSecondOrderBean getSecondOrderBean(String key) {
    return secondOrderBeansMap.get(key);
  }

  public ClusterSecondOrderBean getSecondOrderBean(TypeOfBuildingBlock key) {
    return secondOrderBeansMap.get(key.getValue());
  }

  public ClusterSecondOrderBean getSecondOrderBean(AttributeType attributeType) {
    return secondOrderBeansMap.get(attributeType.getName());
  }

  public void resetSecondOrderBeans() {
    secondOrderBeansMap = new HashMap<String, ClusterSecondOrderBean>();
    typeOrder = new ArrayList<String>();
    setCheckAllBox(true);
  }

  public List<String> getTypeOrder() {
    return typeOrder;
  }

  public void setTypeOrder(List<String> typeOrder) {
    this.typeOrder = typeOrder;
  }

  public void setSelectedAttributeType(int selectedAttributeType) {
    this.selectedAttributeType = selectedAttributeType;
  }

  public void setAvailableAttributeTypes(List<AttributeType> availableAttributeTypes) {
    this.availableAttributeTypes = availableAttributeTypes;
  }

  public List<AttributeType> getAvailableAttributeTypes() {
    return this.availableAttributeTypes;
  }

  public String getSelectedClusterMode() {
    return selectedClusterMode;
  }

  public void setSelectedClusterMode(String clusterMode) {
    this.selectedClusterMode = clusterMode;
  }

  public List<String> getAvailableClusterModes() {
    return availableClusterModes;
  }

  public int getSelectedHierarchicalUpperLevel() {
    return selectedHierarchicalUpperLevel;
  }

  public void setSelectedHierarchicalUpperLevel(int selectedHierarchicalUpperLevel) {
    this.selectedHierarchicalUpperLevel = selectedHierarchicalUpperLevel;
  }

  public int getSelectedHierarchicalLowerLevel() {
    return selectedHierarchicalLowerLevel;
  }

  public void setSelectedHierarchicalLowerLevel(int selectedHierarchicalLowerLevel) {
    this.selectedHierarchicalLowerLevel = selectedHierarchicalLowerLevel;
  }

  public int getAvailableHierarchicalLevels() {
    return availableHierarchicalLevels;
  }

  public void setAvailableHierarchicalLevels(int availableHierarchicalLevels) {
    this.availableHierarchicalLevels = availableHierarchicalLevels;
  }

  public String getSelectedHierarchicalLevels() {
    return selectedHierarchicalLevels;
  }

  public void setSelectedHierarchicalLevels(String selectedHierarchicalLevels) {

    this.selectedHierarchicalLevels = selectedHierarchicalLevels;

    final String[] levels = selectedHierarchicalLevels.split(RANGE_SPLIT);
    selectedHierarchicalUpperLevel = Integer.parseInt(levels[0]);
    selectedHierarchicalLowerLevel = Integer.parseInt(levels[1]);
  }

  public boolean isSwimlaneContent() {
    return swimlaneContent;
  }

  public void setSwimlaneContent(boolean swimlaneContent) {
    this.swimlaneContent = swimlaneContent;
  }

  public void setCheckAllBox(boolean checkAllBox) {
    this.checkAllBox = checkAllBox;
  }

  public boolean isCheckAllBox() {
    return checkAllBox;
  }

  public void setSelectedAttributeValues(List<String> selectedAttributeValues) {
    this.selectedAttributeValues = selectedAttributeValues;
  }

  public List<String> getSelectedAttributeValues() {
    return selectedAttributeValues;
  }

  public List<String> getQueryResultNames() {
    return ImmutableList.of(ManageReportBeanBase.MAIN_QUERY);
  }

}
