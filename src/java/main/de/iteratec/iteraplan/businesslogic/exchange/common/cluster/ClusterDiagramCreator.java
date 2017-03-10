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
package de.iteratec.iteraplan.businesslogic.exchange.common.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeRangeAdapter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterSecondOrderBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.interfaces.IdentityStringEntity;
import de.iteratec.iteraplan.model.sorting.BuildingBlockSortHelper;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;


public class ClusterDiagramCreator {

  private static final String                                                             TITLE_KEY = "graphicalExport.cluster.title";
  private final Locale                                                                    locale;

  private MainAxis                                                                        mainAxis;
  private List<SecondaryAxisElement>                                                      sideElements;

  private Map<String, SecondaryAxisElement>                                               sideAxisKeyToSideAxisElementMap;
  private final Map<String, MainAxisElement<?>>                                           mainAxisValueToMainAxisElementMap;
  private final Map<String, Map<MainAxisElement<?>, Set<? extends IdentityStringEntity>>> bbMapping;

  private final ClusterOptionsBean                                                        clusterOptions;
  private final List<BuildingBlock>                                                       selectedEntities;

  private final GeneralBuildingBlockDAO                                                   generalBuildingBlockDao;
  private final AttributeTypeService                                                      attributeTypeService;
  private final AttributeValueService                                                     attributeValueService;

  public ClusterDiagramCreator(ClusterOptionsBean options, List<BuildingBlock> selectedEntities, GeneralBuildingBlockDAO generalBuildingBlockDAO,
      AttributeTypeService attributeTypeService, AttributeValueService attributeValueService) {
    this.mainAxisValueToMainAxisElementMap = new HashMap<String, MainAxisElement<?>>();
    this.bbMapping = new HashMap<String, Map<MainAxisElement<?>, Set<? extends IdentityStringEntity>>>();
    this.selectedEntities = selectedEntities;
    this.clusterOptions = options;
    this.locale = UserContext.getCurrentLocale();
    this.generalBuildingBlockDao = generalBuildingBlockDAO;
    this.attributeTypeService = attributeTypeService;
    this.attributeValueService = attributeValueService;

    // TODO the selected building block type should be in the opts, so that one doesen't have to use
    // an example building block to determine the type of the main axis.
  }

  public ClusterDiagram createClusterDiagram() {

    final String diagramTitle = MessageAccess.getStringOrNull(TITLE_KEY, locale);
    this.mainAxis = new MainAxis();
    this.sideElements = new LinkedList<SecondaryAxisElement>();

    loadMainAxis();
    loadSideAxis();
    loadContent();
    verifyAndSetTableHeight();

    final ClusterDiagram clusterDiagram = new ClusterDiagram(diagramTitle, mainAxis, sideElements);

    return clusterDiagram;
  }

  private void loadMainAxis() {
    if (clusterOptions.getSelectedClusterMode().equals(Constants.REPORTS_EXPORT_CLUSTER_MODE_BB)) {
      loadMainAxisBb();
    }
    else {
      loadMainAxisAt();
    }
    for (MainAxisElement<?> element : mainAxis.getElements()) {
      estimateMainAxisLengths(element);
    }

    int position = 0;
    for (MainAxisElement<?> element : mainAxis.getElements()) {
      element.setStartPosition(position);
      estimateStartPosition(element, position);
      position = position + element.getLength();
    }
    mainAxis.setMainAxisLength(position);
  }

  private void loadMainAxisBb() {

    final BuildingBlock example = selectedEntities.get(0);

    mainAxis.setTitle(getBbTypeName(example));

    if (example instanceof HierarchicalEntity<?>) {
      if (clusterOptions.getSelectedHierarchicalLowerLevel() == clusterOptions.getSelectedHierarchicalUpperLevel()) {
        createNonHierarchicalMainAxis();
      }
      else {
        createHierarchicalMainAxis();
      }
    }
    else {
      createNonHierarchicalMainAxis();
    }
  }

  private void loadMainAxisAt() {

    final AttributeType selectedAt = attributeTypeService.loadObjectById(Integer.valueOf(clusterOptions.getSelectedAttributeType()));
    mainAxis.setTitle(selectedAt.getName());

    final List<String> attributeValues = new ArrayList<String>(clusterOptions.getSelectedAttributeValues());

    for (String val : attributeValues) {
      final MainAxisElement<String> axisElement = new MainAxisElement<String>(val, mainAxis);
      axisElement.setName(val);
      mainAxis.addElement(axisElement);
      mainAxisValueToMainAxisElementMap.put(val, axisElement);
    }

    addBbMappingForAtMode();

  }

  private void loadContent() {

    if (clusterOptions.isSwimlaneContent()) {
      loadContentSwimlane();
    }
    else {
      for (String sideAxisRef : bbMapping.keySet()) {
        loadContentLinear(sideAxisRef);
      }
    }
  }

  private void loadContentLinear(String sideAxisRef) {

    final Map<MainAxisElement<?>, Set<? extends IdentityStringEntity>> references = bbMapping.get(sideAxisRef);
    final SecondaryAxisElement sideAxisElement = sideAxisKeyToSideAxisElementMap.get(sideAxisRef);
    int maxContentPerCell = 0;

    // iterate over all map entries
    for (Map.Entry<MainAxisElement<?>, Set<? extends IdentityStringEntity>> mapEntry : references.entrySet()) {

      final MainAxisElement<?> topAxisRef = mapEntry.getKey();
      final Set<? extends IdentityStringEntity> entitySet = mapEntry.getValue();
      int contentPositionInCell = 0;

      // sort the map entries elements
      final List<IdentityStringEntity> listordered = new ArrayList<IdentityStringEntity>(entitySet);
      BuildingBlockSortHelper.sortByIdentityString(listordered);

      for (IdentityStringEntity entity : listordered) {
        addContentElement(entity, topAxisRef, sideAxisElement, contentPositionInCell);
        contentPositionInCell++;
      }
      maxContentPerCell = Math.max(maxContentPerCell, contentPositionInCell);
    }

    sideAxisElement.setHeight(maxContentPerCell);
  }

  @SuppressWarnings("boxing")
  private void loadContentSwimlane() {

    for (String sideAxisKey : bbMapping.keySet()) {

      Map<MainAxisElement<?>, Set<? extends IdentityStringEntity>> axisSegmentMap = bbMapping.get(sideAxisKey);
      SecondaryAxisElement sideAxis = sideAxisKeyToSideAxisElementMap.get(sideAxisKey);

      // we only order the elements of the building block dimensions
      if (clusterOptions.getSecondOrderBean(sideAxis.getType()).getBeanType().equals(ClusterSecondOrderBean.BUILDING_BLOCK_BEAN)) {

        // collect all entities
        Set<IdentityStringEntity> allContentElements = new HashSet<IdentityStringEntity>();

        for (Map.Entry<MainAxisElement<?>, Set<? extends IdentityStringEntity>> mapEntry : axisSegmentMap.entrySet()) {
          allContentElements.addAll(mapEntry.getValue());
        }

        // sort the entities
        List<IdentityStringEntity> sortedContentElemnts = new ArrayList<IdentityStringEntity>(allContentElements);

        BuildingBlockSortHelper.sortByIdentityString(sortedContentElemnts);

        // map every entity to its (global for the row) position
        Map<IdentityStringEntity, Integer> positionsMap = new HashMap<IdentityStringEntity, Integer>();
        int position = 0;
        for (IdentityStringEntity entity : sortedContentElemnts) {
          positionsMap.put(entity, position);
          position++;
        }

        // create the content elements
        for (Map.Entry<MainAxisElement<?>, Set<? extends IdentityStringEntity>> mapEntry : axisSegmentMap.entrySet()) {
          for (IdentityStringEntity contentElement : mapEntry.getValue()) {
            addContentElement(contentElement, mapEntry.getKey(), sideAxis, positionsMap.get(contentElement));
          }
        }

        sideAxis.setHeight(position);
      }
      else {
        // Manage AT dimensions
        loadContentLinear(sideAxisKey);
      }
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void addContentElement(IdentityStringEntity contentEntity, MainAxisElement topAxisRef, SecondaryAxisElement sideAxisRef, int positionInCell) {
    ContentElement element = new ContentElement(contentEntity);
    element.setMainAxisReference(topAxisRef);
    element.setSecondaryAxisReference(sideAxisRef);
    element.setPositionInCell(positionInCell);
    sideAxisRef.addContentElement(element);
    topAxisRef.addContentElement(element);
  }

  private void createNonHierarchicalMainAxis() {

    int positionCount = 0;

    for (BuildingBlock bb : selectedEntities) {
      MainAxisElement<BuildingBlock> axisElement = new MainAxisElement<BuildingBlock>(bb, mainAxis);
      axisElement.setStartPosition(positionCount);
      positionCount++;
      mainAxis.addElement(axisElement);
      mainAxisValueToMainAxisElementMap.put(bb.getId().toString(), axisElement);
      addBbMappingForBuildingBlockMode(bb, axisElement);
    }
  }

  private void createHierarchicalMainAxis() {

    // Transform entities into hierarchical entities
    List<HierarchicalEntity<?>> hierarchicalSelection = new ArrayList<HierarchicalEntity<?>>();

    for (BuildingBlock bb : selectedEntities) {
      hierarchicalSelection.add((HierarchicalEntity<?>) bb);
    }

    // Retrieve missing parent elements
    hierarchicalSelection = addMissingParentElements(hierarchicalSelection);

    // Create axis elements for all entities
    Map<Integer, MainAxisElement<BuildingBlock>> idToAxisElementMap = new HashMap<Integer, MainAxisElement<BuildingBlock>>();
    for (HierarchicalEntity<?> entity : hierarchicalSelection) {
      BuildingBlock bb = (BuildingBlock) entity;
      MainAxisElement<BuildingBlock> newElement = new MainAxisElement<BuildingBlock>(bb, mainAxis);
      newElement.setDepth(entity.getLevel());
      newElement.setName(entity.getNonHierarchicalName());
      idToAxisElementMap.put(bb.getId(), newElement);
      mainAxisValueToMainAxisElementMap.put(bb.getId().toString(), newElement);
      addBbMappingForBuildingBlockMode(bb, newElement);
    }

    // Create parent-child relations
    for (HierarchicalEntity<?> entity : hierarchicalSelection) {
      HierarchicalEntity<?> parentEntity = entity.getParentElement();
      MainAxisElement<BuildingBlock> axisElement = idToAxisElementMap.get(entity.getId());
      if (parentEntity != null && parentEntity.getLevel() > 0) {
        MainAxisElement<BuildingBlock> parentAxisElement = idToAxisElementMap.get(parentEntity.getId());
        axisElement.setParent(parentAxisElement);
        parentAxisElement.addChild(axisElement);
      }
    }

    // The lowest level of depth in the hierarchy. The elements of this level should be the root
    // elements
    final int lowestLevel = clusterOptions.getSelectedHierarchicalUpperLevel();

    // Filter all elements whose level is not as deep as the lowest level.
    for (HierarchicalEntity<?> entity : hierarchicalSelection) {
      if (entity.getLevel() == lowestLevel) {
        mainAxis.addElement(idToAxisElementMap.get(entity.getId()));
      }
    }

    // Remove children that are deeper than the deepest selected hierarchical level.
    removeDeepChildren(idToAxisElementMap);

    // Make sure the top level elements have depth 1
    mainAxis.adjustAxisDepths();
  }

  private void removeDeepChildren(Map<Integer, MainAxisElement<BuildingBlock>> idToAxisElementMap) {

    final int highestLevel = clusterOptions.getSelectedHierarchicalLowerLevel();
    List<MainAxisElement<BuildingBlock>> highestLevelAcceptableElements = new LinkedList<MainAxisElement<BuildingBlock>>();
    Map<MainAxisElement<BuildingBlock>, MainAxisElement<BuildingBlock>> nearestParentMap = new HashMap<MainAxisElement<BuildingBlock>, MainAxisElement<BuildingBlock>>();

    for (Map.Entry<Integer, MainAxisElement<BuildingBlock>> mapElement : idToAxisElementMap.entrySet()) {
      MainAxisElement<BuildingBlock> element = mapElement.getValue();
      if (element.getDepth() == highestLevel) {
        highestLevelAcceptableElements.add(element);
      }
    }

    Set<MainAxisElement<BuildingBlock>> aggregatedChildren;

    for (MainAxisElement<BuildingBlock> element : highestLevelAcceptableElements) {
      aggregatedChildren = getAggregatedChildren(element, new HashSet<MainAxisElement<BuildingBlock>>());

      for (MainAxisElement<BuildingBlock> child : aggregatedChildren) {
        nearestParentMap.put(child, element);
        mainAxis.getAllElements().remove(child);
        child.getParent().setChildren(new ArrayList<MainAxisElement<BuildingBlock>>());
        child.setParent(null);
      }

      // Additionally, map building blocks to the moved main axis elements.
      for (MainAxisElement<BuildingBlock> child : aggregatedChildren) {
        addBbMappingForBuildingBlockMode(child.getElement(), nearestParentMap.get(idToAxisElementMap.get(child.getElement().getId())));
      }
    }
  }

  private Set<MainAxisElement<BuildingBlock>> getAggregatedChildren(MainAxisElement<BuildingBlock> element,
                                                                    Set<MainAxisElement<BuildingBlock>> resultList) {
    for (MainAxisElement<BuildingBlock> child : element.getChildren()) {
      resultList.add(child);
      resultList.addAll(getAggregatedChildren(child, resultList));
    }
    return resultList;
  }

  private List<HierarchicalEntity<?>> addMissingParentElements(List<HierarchicalEntity<?>> hierarchicalSelection) {

    List<HierarchicalEntity<?>> elementsToInsert = new LinkedList<HierarchicalEntity<?>>();
    for (HierarchicalEntity<?> entity : hierarchicalSelection) {
      HierarchicalEntity<?> parent = entity.getParentElement();
      while (parent != null && parent.getLevel() != 0 && !elementsToInsert.contains(parent) && !hierarchicalSelection.contains(parent)) {
        parent = (HierarchicalEntity<?>) generalBuildingBlockDao.loadObjectById(parent.getId());
        elementsToInsert.add(parent);
        parent = parent.getParentElement();
      }
    }
    hierarchicalSelection.addAll(elementsToInsert);

    return hierarchicalSelection;
  }

  private int estimateMainAxisLengths(MainAxisElement<?> element) {
    int childLength = 0;
    for (MainAxisElement<?> child : element.getChildren()) {
      childLength = childLength + estimateMainAxisLengths(child);
    }
    element.setLength(1 + childLength);
    return element.getLength();
  }

  private void loadSideAxis() {

    List<String> orderedKeyList = clusterOptions.getOrderedColorDimensionsKeyList();
    sideAxisKeyToSideAxisElementMap = new HashMap<String, SecondaryAxisElement>();

    for (String type : orderedKeyList) {
      ClusterSecondOrderBean bean = clusterOptions.getSecondOrderBean(type);
      if (bean != null && bean.isSelected()) {

        SecondaryAxisElement sideAxisElement = new SecondaryAxisElement(type);

        if (bean.getBeanType().equals(ClusterSecondOrderBean.BUILDING_BLOCK_BEAN)) {
          sideAxisElement.setTitle(MessageAccess.getStringOrNull(type, locale));
        }
        else {
          sideAxisElement.setTitle(type);
        }

        sideElements.add(sideAxisElement);
        sideAxisKeyToSideAxisElementMap.put(type, sideAxisElement);
      }
    }
  }

  private void addBbMappingForBuildingBlockMode(BuildingBlock buildingBlock, MainAxisElement<BuildingBlock> mainAxisElement) {

    Map<String, Set<? extends IdentityEntity>> bbMappings = new ClusterDiagramMapping(clusterOptions, buildingBlock).getMapping();

    for (Map.Entry<String, Set<? extends IdentityEntity>> entry : bbMappings.entrySet()) {
      putBbMapping(entry.getKey(), mainAxisElement, entry.getValue());
    }

  }

  private void addBbMappingForAtMode() {

    List<BuildingBlock> buildingBlocks;
    List<String> aValues;
    final AttributeType selectedAt = attributeTypeService.loadObjectById(Integer.valueOf(clusterOptions.getSelectedAttributeType()));

    for (String type : clusterOptions.getOrderedColorDimensionsKeyList()) {
      if (clusterOptions.getSecondOrderBean(type) != null && clusterOptions.getSecondOrderBean(type).isSelected()) {
        buildingBlocks = generalBuildingBlockDao.getBuildingBlocksByType(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(type));
        bbMapping.put(type, new HashMap<MainAxisElement<?>, Set<? extends IdentityStringEntity>>());

        for (BuildingBlock bb : buildingBlocks) {

          aValues = getAttributeValueStringForMainAxis(selectedAt, bb);
          for (String value : aValues) {

            if (!mainAxisValueToMainAxisElementMap.containsKey(value)) {
              continue;
            }
            Set<? extends IdentityStringEntity> entries = bbMapping.get(type).get(mainAxisValueToMainAxisElementMap.get(value));
            Set<IdentityStringEntity> newEntries;

            if (entries == null) {
              newEntries = new HashSet<IdentityStringEntity>();
            }
            else {
              newEntries = new HashSet<IdentityStringEntity>(entries);
            }
            newEntries.add(bb);
            entries = new HashSet<IdentityStringEntity>(newEntries);
            putBbMapping(type, mainAxisValueToMainAxisElementMap.get(value), entries);
          }
        }
      }
    }
  }

  private void putBbMapping(String type, MainAxisElement<?> mainAxisElement, Set<? extends IdentityStringEntity> entities) {

    Map<MainAxisElement<?>, Set<? extends IdentityStringEntity>> innerMap = bbMapping.get(type);

    if (innerMap == null) {
      innerMap = new HashMap<MainAxisElement<?>, Set<? extends IdentityStringEntity>>();

    }
    if (innerMap.get(mainAxisElement) == null) {
      innerMap.put(mainAxisElement, entities);
    }
    else {
      Set<IdentityStringEntity> newEntities = new HashSet<IdentityStringEntity>(entities);
      newEntities.addAll(innerMap.get(mainAxisElement));
      innerMap.put(mainAxisElement, newEntities);
    }
    bbMapping.put(type, innerMap);
  }

  /**
   * Gets type's name of the given BuildingBlock.
   * 
   * @param bb
   *          BuildingBlock which name has to be found.
   * @return the name of the type.
   */
  private String getBbTypeName(BuildingBlock bb) {
    return getBbTypeName(bb.getClass());
  }

  /**
   * Gets type's name of the given BuildingBlock's Class.
   * 
   * @param clazz
   *          the class of the BuildingBlocks
   * @return the name of the type.
   */
  private String getBbTypeName(Class<?> clazz) {
    return getBbTypeName(TypeOfBuildingBlock.typeOfBuildingBlockForClass(clazz).getValue());
  }

  /**
   * Gets the singular name of the BuildingBlock that corresponds to the given key.
   * 
   * @param row
   *          the message key.
   * @return the name of the BUildingBlock
   */
  private String getBbTypeName(String row) {
    String key = row;
    if (row.endsWith(".plural")) {
      key = row.substring(0, row.length() - 7) + ".singular";
    }
    else if (row.endsWith("s")) {
      key = row.substring(0, row.length() - 1);
    }
    return MessageAccess.getString(key, locale);
  }

  /**
   * Verifies and sets the height of the table. Ideally, this verification should be done in the
   * command class that starts the export. This is indeed possible but would mean that the complete
   * categorization of all selected entities has to be done once more which would make things
   * inefficient. Because of this we leave the procedure here.
   */
  private void verifyAndSetTableHeight() {

    int tableHeight = 0;
    for (String sideAxisRef : bbMapping.keySet()) {
      tableHeight = tableHeight + sideAxisKeyToSideAxisElementMap.get(sideAxisRef).getHeight();
    }

    int maxRowNum = IteraplanProperties.getIntProperty(IteraplanProperties.EXPORT_GRAPHICAL_CLUSTER_MAXROWS);
    if (tableHeight > maxRowNum) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CLUSTER_TOO_MANY_ROWS, Integer.valueOf(maxRowNum));
    }

    int minRowNum = IteraplanProperties.getIntProperty(IteraplanProperties.EXPORT_GRAPHICAL_CLUSTER_MINROWS);
    if (tableHeight < minRowNum) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.CLUSTER_NO_ROWS);
    }
  }

  private void estimateStartPosition(MainAxisElement<?> parent, int parentPosition) {
    int position = 1 + parentPosition;
    for (MainAxisElement<?> child : parent.getChildren()) {
      child.setStartPosition(position);
      estimateStartPosition(child, position);
      position = position + child.getLength();
    }
  }

  private List<String> getAttributeValueStringForMainAxis(AttributeType selectedType, BuildingBlock block) {
    List<AttributeValue> aValues = block.getAttributeTypeToAttributeValues().get(selectedType);

    List<String> results = new ArrayList<String>();

    if (aValues == null) {
      results.add(DimensionOptionsBean.DEFAULT_VALUE);
      return results;
    }

    if (attributeTypeService.isNumberAT(selectedType.getId())) {
      List<String> values = attributeValueService.getAllAVStrings(selectedType.getId());

      NumberAT attribute = (NumberAT) attributeTypeService.loadObjectById(selectedType.getId());

      AttributeRangeAdapter adapter = new AttributeRangeAdapter(locale);
      adapter.init(attribute, values);

      results.add(adapter.getResultForValue(aValues.get(0).getLocalizedValueString(locale)));
    }
    else {

      for (AttributeValue val : aValues) {
        results.add(val.getLocalizedValueString(locale));
      }
    }
    return results;
  }
}
