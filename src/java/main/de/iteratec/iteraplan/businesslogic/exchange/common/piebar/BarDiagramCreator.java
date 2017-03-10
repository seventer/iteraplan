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
package de.iteratec.iteraplan.businesslogic.exchange.common.piebar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.DimensionAdapter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.DiagramKeyType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesSource;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean.ValuesType;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.SingleBarOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.HierarchicalBuildingBlockService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.MultiassignementType;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


public class BarDiagramCreator extends AbstractPieBarDiagramCreator<BarDiagram> {

  private static class PieBarSizeComparator implements Comparator<PieBar>, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = -3967116568930296869L;

    public int compare(PieBar o1, PieBar o2) {
      return o2.getTotalSize() - o1.getTotalSize();
    }

  }

  private static class PieBarLabelComparator implements Comparator<PieBar>, Serializable {

    /** Serialization version */
    private static final long serialVersionUID = -6592823081750142578L;

    public int compare(PieBar o1, PieBar o2) {
      String label1 = o1.getLabel();
      String label2 = o2.getLabel();
      try {
        int labelNum1 = Integer.parseInt(label1);
        int labelNum2 = Integer.parseInt(label2);
        return labelNum1 - labelNum2;
      } catch (NumberFormatException e) {
        return label1.compareTo(label2);
      }
    }

  }

  static final Logger                                                  LOGGER                = Logger.getIteraplanLogger(BarDiagramCreator.class);
  private final BuildingBlockService<? extends BuildingBlock, Integer> bbService;
  private boolean                                                      showMultiValueWarning = false;

  public BarDiagramCreator(PieBarDiagramOptionsBean options, List<BuildingBlock> selectedBbs, AttributeTypeService attributeTypeService,
                           AttributeValueService attributeValueService, BuildingBlockService<? extends BuildingBlock, Integer> bbService) {
    super(options, selectedBbs, attributeTypeService, attributeValueService);
    this.bbService = bbService;
  }

  @Override
  public BarDiagram createDiagram() {
    LOGGER.info("creating bar diagram");

    if (!PieBarDiagramOptionsBean.DiagramType.BAR.equals(getOptions().getDiagramType())) {
      LOGGER.error("Wrong Diagram Type: {0}", getOptions().getDiagramType().name());
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }

    List<PieBar> bars = createBars();
    bars = sortBars(bars);

    Integer dimAttrId = getOptions().getColorOptionsBean().getDimensionAttributeId();
    boolean isMultiValue = !dimAttrId.equals(Integer.valueOf(-1)) && isMultiValueAttribute(dimAttrId);
    if (PieBarDiagramOptionsBean.ValuesType.VALUES.equals(getOptions().getDiagramValuesType()) && isMultiValue) {
      showMultiValueWarning = true;
    }
    String attributeName = "";
    if (!DiagramKeyType.ATTRIBUTE_TYPES.equals(getOptions().getDiagramKeyType())) {
      attributeName = getAttributeNameFromId(getOptions().getColorOptionsBean().getDimensionAttributeId());
    }

    ValuesSource valuesSource = getOptions().getValuesSource();
    boolean colorAssociationSet = ValuesSource.ASSOCIATION.equals(valuesSource) && !"".equals(getOptions().getSelectedAssociation());
    boolean colorAttributeSet = ValuesSource.ATTRIBUTE.equals(valuesSource)
        && (!Integer.valueOf(-1).equals(getOptions().getColorOptionsBean().getDimensionAttributeId()) || DiagramKeyType.ATTRIBUTE_TYPES
            .equals(getOptions().getDiagramKeyType()));
    boolean colorsSet = colorAssociationSet || colorAttributeSet;

    return new BarDiagram(createTitleString(), attributeName, createHorizontalAxisLabel(), bars, showMultiValueWarning, colorsSet);
  }

  private String createTitleString() {
    return getOptions().getNumberOfSelectedElements() + " " + MessageAccess.getStringOrNull(getOptions().getSelectedBbType(), getLocale());
  }

  private String createHorizontalAxisLabel() {
    StringBuilder label = new StringBuilder();
    switch (getOptions().getDiagramKeyType()) {
      case ASSOCIATION_COUNT:
        //$FALL-THROUGH$
      case ASSOCIATION_NAMES:
        label.append(MessageAccess.getStringOrNull(getOptions().getSelectedKeyAssociation(), getLocale())).append(" - ");
        break;
      case ATTRIBUTE_COUNT:
        //$FALL-THROUGH$
      case ATTRIBUTE_VALUES:
        Integer keyAttributeId = Integer.valueOf(getOptions().getSelectedKeyAttributeTypeId());
        label.append(getAttributeNameFromId(keyAttributeId)).append(" - ");
        break;
      case ATTRIBUTE_TYPES:
        break;
      default:
        LOGGER.error("Invalid DiagramKeyType: {0}", getOptions().getDiagramKeyType().name());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
    label.append(MessageAccess.getStringOrNull(getOptions().getDiagramKeyType().getValue(), getLocale()));
    return label.toString();
  }

  /**
   * Create and fill the {@link PieBar}-objects
   * @return List of {@link PieBar}-objects
   */
  private List<PieBar> createBars() {
    switch (getOptions().getDiagramKeyType()) {
      case ASSOCIATION_COUNT:
        return createAssociationCountBars();
      case ASSOCIATION_NAMES:
        return createAssociationNameBars();
      case ATTRIBUTE_COUNT:
        return createAttributeValuesCountBars();
      case ATTRIBUTE_VALUES:
        return createAttributeValueBars();
      case ATTRIBUTE_TYPES:
        return createAttributeTypeBars();
      default:
        LOGGER.error("Invalid DiagramKeyType: {0}", getOptions().getDiagramKeyType().name());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

  private List<PieBar> createAssociationCountBars() {
    List<String> keys = CollectionUtils.arrayList();
    String keyAssociation = getOptions().getSelectedKeyAssociation();
    int maxNumOfAssociatedEntities = getOptions().getMaxNumberOfAssociatedEntities().get(keyAssociation).intValue();
    for (int i = 0; i <= maxNumOfAssociatedEntities; i++) {
      keys.add(getLabelForSize(i));
    }

    Integer dimensionAttributeId = getOptions().getColorOptionsBean().getDimensionAttributeId();
    DimensionAdapter<?> adapter = getValuesAdapter(dimensionAttributeId, getOptions().getColorOptionsBean().isUseColorRange());

    Map<String, PieBar> barsMap = initBarsMap(keys, getOptions().getColorOptionsBean(), adapter);

    for (BuildingBlock bb : getSelectedEntities()) {
      String label = getLabelForSize(getAssociatedEntities(keyAssociation, bb).size());
      PieBar bar = getPieBarFromMap(label, barsMap, getValues(getOptions().getColorOptionsBean()), getOptions().getDiagramValuesType());

      addValuesToBar(adapter, bb, bar);
    }
    return new ArrayList<PieBar>(barsMap.values());
  }

  private void addValuesToBar(DimensionAdapter<?> adapter, BuildingBlock bb, PieBar bar) {
    if (ValuesSource.ATTRIBUTE.equals(getOptions().getValuesSource())) {
      bar.add(createValuesListFromBuildingBlock(bb, getOptions().getColorOptionsBean().getDimensionAttributeId(), adapter));
    }
    else if (ValuesSource.ASSOCIATION.equals(getOptions().getValuesSource())) {
      bar.add(createValuesListFromIdentityEntities(getAssociatedEntities(getOptions().getSelectedAssociation(), bb)));
    }
  }

  private List<PieBar> createAssociationNameBars() {
    Map<String, PieBar> barsMap;
    List<String> keys = getOrderedAssociationNamesList();
    if (keys != null && !getOptions().getColorOptionsBean().isUseColorRange()) {
      barsMap = initBarsMap(keys, getOptions().getColorOptionsBean(), null);
    }
    else {
      barsMap = new LinkedHashMap<String, PieBar>();
    }

    Integer dimensionAttributeId = getOptions().getColorOptionsBean().getDimensionAttributeId();
    DimensionAdapter<?> adapter = getValuesAdapter(dimensionAttributeId, getOptions().getColorOptionsBean().isUseColorRange());

    Set<String> usedEntityNames = CollectionUtils.hashSet();
    for (BuildingBlock bb : getSelectedEntities()) {
      String keyAssociation = getOptions().getSelectedKeyAssociation();
      Set<? extends IdentityEntity> associatedEntities = getAssociatedEntities(keyAssociation, bb);

      // remember all elements this building block was already associated to, to avoid multiple assignments when aggregating hierarchical levels
      Set<String> alreadyAssociatedEntities = CollectionUtils.hashSet();
      for (IdentityEntity entity : associatedEntities) {
        String name = getNameOfAppropriateHierarchicalLevel(entity);
        if (name != null && !alreadyAssociatedEntities.contains(name)) {
          PieBar bar = getPieBarFromMap(name, barsMap, getValues(getOptions().getColorOptionsBean(), adapter), getOptions().getDiagramValuesType());

          addValuesToBar(adapter, bb, bar);

          alreadyAssociatedEntities.add(name);
          usedEntityNames.add(name);
        }
      }
    }
    barsMap.keySet().retainAll(usedEntityNames);
    return new ArrayList<PieBar>(barsMap.values());
  }

  private List<String> getOrderedAssociationNamesList() {
    if (bbService instanceof HierarchicalBuildingBlockService) {
      HierarchicalBuildingBlockService<?, ?> hierarchicalService = (HierarchicalBuildingBlockService<?, ?>) bbService;
      HierarchicalEntity<?> root = hierarchicalService.getFirstElement();
      return buildAssociationNamesList(root);
    }
    else {
      return null;
    }
  }

  private List<String> buildAssociationNamesList(HierarchicalEntity<?> entity) {
    List<String> names = CollectionUtils.arrayList();
    if (entity != null) {
      names.add(entity.getHierarchicalName());
      if (!entity.getChildren().isEmpty()) {
        for (HierarchicalEntity<?> child : entity.getChildrenAsList()) {
          names.addAll(buildAssociationNamesList(child));
        }
      }
    }
    return names;
  }

  /**
   * Returns the name of the given {@code entity} if it's within the level-bounds specified
   * in the {@link PieBarDiagramOptionsBean} or not a hierarchical entity.
   * Otherwise returns the first of its ancestors meeting this requirement
   * or null, if there isn't one.
   * @param entity
   *          {@link IdEntity}
   * @return name of the entity to be used in the diagram
   */
  private String getNameOfAppropriateHierarchicalLevel(IdEntity entity) {
    if (entity == null) {
      return null;
    }
    if (entity instanceof HierarchicalEntity) {
      HierarchicalEntity<?> hierarchicalEntity = (HierarchicalEntity<?>) entity;
      int level = hierarchicalEntity.getLevel();

      if (level < getOptions().getSelectedTopLevel()) {
        return null;
      }
      else if (level <= getOptions().getSelectedBottomLevel()) {
        return hierarchicalEntity.getHierarchicalName();
      }
      else {
        return getNameOfAppropriateHierarchicalLevel(hierarchicalEntity.getParentElement());
      }
    }
    else {
      return ((BuildingBlock) entity).getHierarchicalName();
    }
  }

  private List<PieBar> createAttributeValuesCountBars() {
    List<String> keys = CollectionUtils.arrayList();
    Integer keyAttributeId = Integer.valueOf(getOptions().getSelectedKeyAttributeTypeId());
    int maxNumOfAssignments = getAttributeValueService().getAllAVs(keyAttributeId).size();
    for (int i = 0; i <= maxNumOfAssignments; i++) {
      keys.add(getLabelForSize(i));
    }
    Integer dimensionAttributeId = getOptions().getColorOptionsBean().getDimensionAttributeId();
    DimensionAdapter<?> adapter = getValuesAdapter(dimensionAttributeId, getOptions().getColorOptionsBean().isUseColorRange());
    Map<String, PieBar> barsMap = initBarsMap(keys, getOptions().getColorOptionsBean(), adapter);

    for (BuildingBlock bb : getSelectedEntities()) {
      List<String> keyAvs = createValuesListFromBuildingBlock(bb, keyAttributeId, null);

      PieBar bar = getPieBarFromMap(getLabelForSize(keyAvs.size()), barsMap, getValues(getOptions().getColorOptionsBean()), getOptions()
          .getDiagramValuesType());
      addValuesToBar(adapter, bb, bar);
    }
    return new ArrayList<PieBar>(barsMap.values());
  }

  private List<PieBar> createAttributeValueBars() {
    Integer dimensionAttributeId = getOptions().getColorOptionsBean().getDimensionAttributeId();
    DimensionAdapter<?> adapter = getValuesAdapter(dimensionAttributeId, getOptions().getColorOptionsBean().isUseColorRange());

    Integer keyAttributeId = Integer.valueOf(getOptions().getSelectedKeyAttributeTypeId());
    DimensionAdapter<?> keyAdapter = getValuesAdapter(keyAttributeId);

    Map<String, PieBar> barsMap = null;
    if (keyAdapter != null) {
      barsMap = initBarsMap(keyAdapter.getValues(), getOptions().getColorOptionsBean(), adapter);
    }
    else {
      barsMap = new LinkedHashMap<String, PieBar>();
    }

    for (BuildingBlock bb : getSelectedEntities()) {
      List<String> keyStrings = createValuesListFromBuildingBlock(bb, keyAttributeId, keyAdapter);
      if (keyStrings.isEmpty()) {
        keyStrings.add(DimensionOptionsBean.DEFAULT_VALUE);
      }

      for (String key : keyStrings) {
        PieBar bar = getPieBarFromMap(key, barsMap, getValues(getOptions().getColorOptionsBean()), getOptions().getDiagramValuesType());
        addValuesToBar(adapter, bb, bar);
      }
    }
    return new ArrayList<PieBar>(barsMap.values());
  }

  private List<PieBar> createAttributeTypeBars() {
    boolean maybeShowMultiValueWarning = false;
    if (ValuesType.VALUES.equals(getOptions().getDiagramValuesType())) {
      maybeShowMultiValueWarning = true;
    }

    Map<String, PieBar> barsMap = new LinkedHashMap<String, PieBar>();

    List<BBAttribute> ats = getOptions().getAvailableAttributeTypes();

    for (BBAttribute at : ats) {
      Integer atId = at.getId();
      SingleBarOptionsBean singleBar = getOptions().getBarsMap().get(atId);

      if (singleBar.isSelected()) {
        DimensionAdapter<?> adapter = getValuesAdapter(atId, singleBar.getColorOptions().isUseColorRange());
        PieBar bar = getPieBarFromMap(getAttributeNameFromId(atId), barsMap, getValues(singleBar.getColorOptions(), adapter), singleBar.getType());

        if (isMultiValueAttribute(atId) && maybeShowMultiValueWarning) {
          showMultiValueWarning = true;
          bar.setMultiValueATBar(true);
        }

        for (BuildingBlock bb : getSelectedEntities()) {
          bar.add(createValuesListFromBuildingBlock(bb, atId, adapter));
        }
      }
    }

    return new ArrayList<PieBar>(barsMap.values());
  }

  private boolean isMultiValueAttribute(Integer attrId) {
    if (attrId.intValue() <= 0) {
      return false;
    }
    AttributeType at = getAttributeTypeService().loadObjectById(attrId);
    return at instanceof MultiassignementType && ((MultiassignementType) at).isMultiassignmenttype();
  }

  private Map<String, PieBar> initBarsMap(List<String> keys, ColorDimensionOptionsBean colorOptions, DimensionAdapter<?> adapter) {
    Map<String, PieBar> barsMap = new LinkedHashMap<String, PieBar>();
    for (String key : keys) {
      createPieBar(key, barsMap, getValues(colorOptions, adapter), getOptions().getDiagramValuesType());
    }
    return barsMap;
  }

  /**
   * Returns a {@link PieBar}-object from {@code resultMap}, if there is one associated to {@code key}.
   * Otherwise creates it before returning.
   * @param key
   *          String-key referencing the expected {@link PieBar} in {@code resultMap}.
   * @param resultMap
   *          Map containing all {@link PieBar}s referenced by a String key
   * @param values
   *          values needed to create a new {@link PieBar}-object
   * @return the {@link PieBar} from {@code resultMap} referenced by {@code key}.
   */
  private PieBar getPieBarFromMap(String key, Map<String, PieBar> resultMap, List<String> values, ValuesType type) {
    if (!resultMap.containsKey(key)) {
      createPieBar(key, resultMap, values, type);
    }
    return resultMap.get(key);
  }

  private void createPieBar(String key, Map<String, PieBar> resultMap, List<String> values, ValuesType type) {
    PieBar bar = new PieBar(type, values);
    bar.setLabel(createLabelFromKey(key));
    resultMap.put(key, bar);
  }

  private String createLabelFromKey(String key) {
    if (DimensionOptionsBean.DEFAULT_VALUE.equals(key)) {
      return MessageAccess.getStringOrNull(key, getLocale());
    }
    return key;
  }

  @SuppressWarnings("PMD.MissingBreakInSwitch")
  // fall-through intended
  private List<PieBar> sortBars(List<PieBar> bars) {
    switch (getOptions().getBarsOrderMethod()) {
      case SIZE:
        Collections.sort(bars, new PieBarSizeComparator());
        return bars;
      case ALPHANUMERIC:
        Collections.sort(bars, new PieBarLabelComparator());
        return bars;
      case DEFAULT:
        switch (getOptions().getDiagramKeyType()) {
          case ASSOCIATION_COUNT:
          case ATTRIBUTE_COUNT:
            Collections.sort(bars, new PieBarLabelComparator());
            return bars;
          default:
            // do nothing
            return bars;
        }
      default:
        LOGGER.error("Invalid BarsOrderMethod: {0}", getOptions().getBarsOrderMethod().name());
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }
}
