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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


/**
 * Container for the parameters of the Bar and Pie diagrams.
 */
public class PieBarDiagramOptionsBean extends GraphicalExportBaseOptions implements Serializable {

  /** Serialization version. */
  private static final long                        serialVersionUID                = -4454754315235357723L;

  private static final Logger                      LOGGER                          = Logger.getIteraplanLogger(PieBarDiagramOptionsBean.class);

  /**
   * split symbol for axis levels (between top level and bottom level)
   */
  private static final String                      RANGE_SPLIT                     = "_";
  private static final int                         TOP_LEVEL                       = 0;
  private static final int                         BOTTOM_LEVEL                    = 1;

  private DiagramType                              diagramType                     = DiagramType.PIE;
  private DiagramKeyType                           diagramKeyType                  = DiagramKeyType.ASSOCIATION_NAMES;
  private ValuesSource                             valuesSource                    = ValuesSource.ATTRIBUTE;
  private ValuesType                               valuesType                      = ValuesType.MAINTAINED;

  private int                                      numberOfSelectedElements;

  private List<String>                             availableAssociations;
  private List<BBAttribute>                        availableAttributeTypes;
  private String                                   selectedAssociation             = "";
  private String                                   selectedKeyAssociation          = "";
  private int                                      selectedKeyAttributeTypeId      = -1;

  private int                                      selectedAssociationTopLevel     = 1;
  private int                                      selectedAssociationBottomLevel  = 1;

  private static final boolean                     SHOW_EMPTY_SEGMENTS             = false;
  private boolean                                  showEmptyBars                   = true;
  private boolean                                  showSegmentLabels               = false;
  private boolean                                  showBarSizeLabels               = false;
  private BarsOrderMethod                          barsOrderMethod                 = BarsOrderMethod.DEFAULT;
  private boolean                                  checkAllBoxPie                  = false;

  /** maps AttributeType-IDs to the corresponding SingleBarOptionsBeans */
  private final Map<Integer, SingleBarOptionsBean> attributeTypeBarsMap            = CollectionUtils.hashMap();

  private Map<String, Integer>                     maxNumberOfAssociatedEntities   = CollectionUtils.hashMap();
  private Map<String, Integer>                     bottomLevelOfAssociatedEntities = CollectionUtils.hashMap();

  private final Map<Integer, Integer>              countIndexToBarsMapKey          = CollectionUtils.hashMap();

  public PieBarDiagramOptionsBean() {
    super();
    initializePieBarOptions();
  }

  private void initializePieBarOptions() {

    getColorOptionsBean().setAvailableColors(SpringGuiFactory.getInstance().getPieBarColors());

    setSelectedGraphicFormat(Constants.REPORTS_EXPORT_GRAPHICAL_SVG);
    setAvailableGraphicFormats(ExportOption.getGraphicalExportOptions(false));
  }

  public void initBarsMap() {
    attributeTypeBarsMap.clear();
    countIndexToBarsMapKey.clear();
    int count = 0;
    for (BBAttribute at : availableAttributeTypes) {
      String name = at.getName();
      if (at.getId().intValue() <= 0) {
        name = MessageAccess.getStringOrNull(name, UserContext.getCurrentLocale());
      }
      SingleBarOptionsBean singleBar = new SingleBarOptionsBean(name);
      singleBar.getColorOptions().setDimensionAttributeId(at.getId());
      attributeTypeBarsMap.put(at.getId(), singleBar);
      countIndexToBarsMapKey.put(Integer.valueOf(count), at.getId());
      count++;
    }
    refreshSingleBarValueTypes();
  }

  public void refreshSingleBarValueTypes() {
    for (SingleBarOptionsBean singleBar : attributeTypeBarsMap.values()) {
      Integer attrId = singleBar.getColorOptions().getDimensionAttributeId();
      if (attrId.intValue() == DESCRIPTION_SELECTED) {
        singleBar.setType(ValuesType.MAINTAINED);
      }
      else {
        singleBar.setType(getDiagramValuesType());
      }
    }
  }

  public void setDiagramType(DiagramType diagramType) {
    this.diagramType = diagramType;
  }

  public DiagramType getDiagramType() {
    return diagramType;
  }

  public void setDiagramKeyType(DiagramKeyType diagramKeyType) {
    this.diagramKeyType = diagramKeyType;
    if (DiagramKeyType.ATTRIBUTE_TYPES.equals(diagramKeyType)) {
      valuesSource = ValuesSource.ATTRIBUTE;
    }
  }

  public DiagramKeyType getDiagramKeyType() {
    return diagramKeyType;
  }

  public DiagramKeyType[] getAvailableDiagramKeyTypes() {
    return DiagramKeyType.values();
  }

  public void setValuesSource(ValuesSource valuesSource) {
    this.valuesSource = valuesSource;
  }

  public ValuesSource getValuesSource() {
    return valuesSource;
  }

  public void setDiagramValuesType(ValuesType valuesType) {
    if (!this.valuesType.equals(valuesType)) {
      this.valuesType = valuesType;
      getColorOptionsBean().setToRefresh(true);
    }
  }

  public ValuesType getDiagramValuesType() {
    return valuesType;
  }

  public boolean isCheckAllBoxPie() {
    return checkAllBoxPie;
  }

  public void setCheckAllBoxPie(boolean checkAllBoxPie) {
    this.checkAllBoxPie = checkAllBoxPie;
  }

  /**
   * Returns the available ValuesTypes depending on the current settings
   * @return available ValuesTypes
   */
  public ValuesType[] getAvailableDiagramValuesTypes() {
    ValuesType[] m = { ValuesType.MAINTAINED };
    ValuesType[] mv = { ValuesType.VALUES, ValuesType.MAINTAINED };
    ValuesType[] mc = { ValuesType.MAINTAINED, ValuesType.COUNT };
    ValuesType[] mvc = { ValuesType.MAINTAINED, ValuesType.VALUES, ValuesType.COUNT };

    if (getColorOptionsBean().getDimensionAttributeId().intValue() < -1 && getColorOptionsBean().getDimensionAttributeId().intValue() != -11) {
      return m;
    }
    else if (diagramType.equals(DiagramType.PIE)) {
      if (ValuesSource.ATTRIBUTE.equals(valuesSource) && !isSelectedDimensionAttributeMultiValue()) {
        return mv;
      }
      else {
        return mc;
      }
    }
    else {
      if (ValuesSource.ASSOCIATION.equals(valuesSource) && !DiagramKeyType.ATTRIBUTE_TYPES.equals(diagramKeyType)) {
        return mc;
      }
      else if (!isSelectedDimensionAttributeMultiValue() || DiagramKeyType.ATTRIBUTE_TYPES.equals(diagramKeyType)) {
        return mv;
      }
    }
    return mvc;
  }

  public void setAvailableAssociations(List<String> availableAssociations) {
    this.availableAssociations = availableAssociations;
  }

  public List<String> getAllAvailableAssociations() {
    return availableAssociations;
  }

  public List<String> getAvailableAssociations() {
    List<String> associationsToRemove = CollectionUtils.arrayList();

    if (DiagramType.PIE.equals(getDiagramType())) {
      associationsToRemove.add(".parent");
      associationsToRemove.add(".generalisation");
    }

    return filteredAvailableAssociations(associationsToRemove, false);
  }

  public List<String> getAvailableKeyAssociations() {
    List<String> associationsToRemove = CollectionUtils.arrayList();

    if (DiagramType.PIE.equals(getDiagramType())) {
      return new ArrayList<String>();
    }
    else {
      switch (diagramKeyType) {
        case ASSOCIATION_COUNT:
          associationsToRemove.add(".parent");
          associationsToRemove.add(".generalisation");
          break;
        case ASSOCIATION_NAMES:
          associationsToRemove.add(".children");
          associationsToRemove.add(".specialisations");
          break;
        default:
          selectedKeyAssociation = "";
          return new ArrayList<String>();
      }
    }

    return filteredAvailableAssociations(associationsToRemove, true);
  }

  private List<String> filteredAvailableAssociations(List<String> associationsToRemove, boolean keyAssociation) {
    List<String> applicableAssociations = CollectionUtils.arrayList();
    for (String association : availableAssociations) {
      if (!associationsToRemove.contains(association.substring(association.lastIndexOf(".")))) {
        applicableAssociations.add(association);
      }
    }
    if (keyAssociation) {
      if (!applicableAssociations.contains(selectedKeyAssociation) && !"".equals(selectedKeyAssociation)) {
        selectedKeyAssociation = applicableAssociations.get(0);
      }
    }
    else {
      if (!applicableAssociations.contains(selectedAssociation) && !"".equals(selectedAssociation)) {
        selectedAssociation = applicableAssociations.get(0);
      }
    }
    return applicableAssociations;
  }

  public void setAvailableAttributeTypes(List<BBAttribute> availableAttributeTypes) {
    this.availableAttributeTypes = availableAttributeTypes;
  }

  public List<BBAttribute> getAvailableAttributeTypes() {
    return availableAttributeTypes;
  }

  public List<BBAttribute> getAvailableKeyAttributeTypes() {
    List<BBAttribute> available = CollectionUtils.arrayList();
    for (BBAttribute att : availableAttributeTypes) {
      if (att.getId().intValue() > -1 || att.getId().intValue() == GraphicalExportBaseOptions.SEAL_SELECTED) {
        available.add(att);
      }
    }
    return available;
  }

  public List<BBAttribute> getAvailableMultiValueAttributeTypes() {
    List<BBAttribute> multiValue = CollectionUtils.arrayList();
    for (BBAttribute attribute : getAvailableAttributeTypes()) {
      if (attribute.isMultiValue()) {
        multiValue.add(attribute);
      }
    }
    return multiValue;
  }

  public boolean isSelectedDimensionAttributeMultiValue() {
    Integer attributeId = this.getColorOptionsBean().getDimensionAttributeId();
    if (!Integer.valueOf(-1).equals(attributeId)) {
      for (BBAttribute bba : getAvailableMultiValueAttributeTypes()) {
        if (bba.getId().equals(attributeId)) {
          return true;
        }
      }
    }
    return false;
  }

  public void setSelectedAssociation(String selectedAssociation) {
    if (!this.selectedAssociation.equals(selectedAssociation)) {
      this.selectedAssociation = selectedAssociation;
      getColorOptionsBean().setToRefresh(true);
    }
  }

  public String getSelectedAssociation() {
    return selectedAssociation;
  }

  public void setSelectedKeyAssociation(String selectedKeyAssociation) {
    this.selectedKeyAssociation = selectedKeyAssociation;
  }

  public String getSelectedKeyAssociation() {
    return selectedKeyAssociation;
  }

  public void setSelectedKeyAttributeTypeId(int selectedKeyAttributeTypeId) {
    this.selectedKeyAttributeTypeId = selectedKeyAttributeTypeId;
  }

  public int getSelectedKeyAttributeTypeId() {
    return selectedKeyAttributeTypeId;
  }

  /**
   * checks if an attribute / association to define color settings is selected
   * @return see method description
   */
  public boolean isColorAttributeAssociationSelected() {
    switch (this.getValuesSource()) {
      case ASSOCIATION:
        if (this.selectedAssociation.equals("")) {
          return false;
        }
        break;
      case ATTRIBUTE:
        if (this.getColorOptionsBean().getDimensionAttributeId().equals(Integer.valueOf(-1))) {
          return false;
        }
        break;
      default:
    }
    return true;
  }

  public Map<String, Integer> getMaxNumberOfAssociatedEntities() {
    return maxNumberOfAssociatedEntities;
  }

  public void setMaxNumberOfAssociatedEntities(Map<String, Integer> maxNumberOfAssociatedEntities) {
    this.maxNumberOfAssociatedEntities = maxNumberOfAssociatedEntities;
  }

  public void setBottomLevels(Map<String, Integer> bottomLevels) {
    this.bottomLevelOfAssociatedEntities = bottomLevels;
  }

  public void setSelectedLevelRange(String selectedLevelRange) {
    String levels[] = selectedLevelRange.split(RANGE_SPLIT);
    int topLevel = Integer.parseInt(levels[TOP_LEVEL]);
    int bottomLevel = Integer.parseInt(levels[BOTTOM_LEVEL]);

    boolean isTopLevelHighEnough = topLevel >= getAvailableTopLevel();
    boolean isBottomLevelLowEnough = bottomLevel <= getAvailableBottomLevel();

    if (topLevel <= bottomLevel && isTopLevelHighEnough && isBottomLevelLowEnough) {
      this.selectedAssociationBottomLevel = bottomLevel;
      this.selectedAssociationTopLevel = topLevel;
    }
    else {
      this.selectedAssociationBottomLevel = getAvailableBottomLevel();
      this.selectedAssociationTopLevel = getAvailableTopLevel();
    }
  }

  public String getSelectedLevelRange() {
    return selectedAssociationTopLevel + "_" + selectedAssociationBottomLevel;
  }

  public Map<Integer, SingleBarOptionsBean> getBarsMap() {
    return attributeTypeBarsMap;
  }

  public List<SingleBarOptionsBean> getSingleBars() {
    final ArrayList<SingleBarOptionsBean> singleBars = new ArrayList<SingleBarOptionsBean>(attributeTypeBarsMap.size());
    for (int i = 0; i < countIndexToBarsMapKey.size(); i++) {
      singleBars.add(attributeTypeBarsMap.get(countIndexToBarsMapKey.get(Integer.valueOf(i))));
    }
    return singleBars;
  }

  public int getSelectedBottomLevel() {
    return selectedAssociationBottomLevel;
  }

  public int getSelectedTopLevel() {
    return selectedAssociationTopLevel;
  }

  public int getAvailableBottomLevel() {
    if (bottomLevelOfAssociatedEntities.containsKey(selectedKeyAssociation)) {
      return bottomLevelOfAssociatedEntities.get(selectedKeyAssociation).intValue();
    }
    else {
      return 1;
    }
  }

  public int getAvailableTopLevel() {
    return 1;
  }

  public boolean isShowEmptySegments() {
    return SHOW_EMPTY_SEGMENTS;
  }

  public void setShowEmptyBars(boolean showEmptyBars) {
    this.showEmptyBars = showEmptyBars;
  }

  public boolean isShowEmptyBars() {
    return showEmptyBars;
  }

  public void setShowSegmentLabels(boolean showSegmentLabels) {
    this.showSegmentLabels = showSegmentLabels;
  }

  public boolean isShowSegmentLabels() {
    return showSegmentLabels;
  }

  public void setShowBarSizeLabels(boolean showBarSizeLabels) {
    this.showBarSizeLabels = showBarSizeLabels;
  }

  public boolean isShowBarSizeLabels() {
    return showBarSizeLabels;
  }

  public BarsOrderMethod[] getAvailableBarsOrderMethods() {
    return BarsOrderMethod.values();
  }

  public void setBarsOrderMethod(BarsOrderMethod barsOrderMethod) {
    this.barsOrderMethod = barsOrderMethod;
  }

  public BarsOrderMethod getBarsOrderMethod() {
    return barsOrderMethod;
  }

  public void setNumberOfSelectedElements(int numberOfSelectedElements) {
    this.numberOfSelectedElements = numberOfSelectedElements;
  }

  public int getNumberOfSelectedElements() {
    return numberOfSelectedElements;
  }

  @Override
  public void validate() {
    switch (getDiagramType()) {
      case PIE:
        validatePieSettings();
        break;
      case BAR:
        validateBarSettings();
        break;
      default:
        LOGGER.error("Invalid DiagramType: {0}", diagramType);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

  }

  private void validateBarSettings() {
    switch (getDiagramKeyType()) {
      case ATTRIBUTE_TYPES:
        boolean somethingSelected = false;
        for (SingleBarOptionsBean sbob : getBarsMap().values()) {
          if (sbob.isSelected()) {
            somethingSelected = true;
            break;
          }
        }
        if (!somethingSelected) {
          throw new IteraplanBusinessException(IteraplanErrorMessages.NO_ATTRIBUTE_TYPE_CHOOSEN_BAR);
        }
        break;
      case ATTRIBUTE_COUNT:
        if (-1 == getSelectedKeyAttributeTypeId()) {
          throw new IteraplanBusinessException(IteraplanErrorMessages.NO_ATTRIBUTE_COUNT_CHOOSEN_BAR);
        }
        break;
      case ATTRIBUTE_VALUES:
        if (-1 == getSelectedKeyAttributeTypeId()) {
          throw new IteraplanBusinessException(IteraplanErrorMessages.NO_ATTRIBUTE_VALUE_CHOOSEN_BAR);
        }
        break;
      case ASSOCIATION_COUNT:
        if ("".equals(getSelectedKeyAssociation())) {
          throw new IteraplanBusinessException(IteraplanErrorMessages.NO_ASSOCIATION_COUNT_CHOOSEN_BAR);
        }
        break;
      case ASSOCIATION_NAMES:
        if ("".equals(getSelectedKeyAssociation())) {
          throw new IteraplanBusinessException(IteraplanErrorMessages.NO_ASSOCIATION_CHOOSEN_BAR);
        }
        break;
      default:
        LOGGER.error("Invalid DiagramKeyType: {0}", diagramKeyType);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  private void validatePieSettings() {
    int relevantAttributeId = getColorOptionsBean().getDimensionAttributeId().intValue();
    String relevantAssociation = getSelectedAssociation();

    boolean attributeNotSet = ValuesSource.ATTRIBUTE.equals(getValuesSource()) && -1 == relevantAttributeId;
    boolean associationNotSet = ValuesSource.ASSOCIATION.equals(getValuesSource()) && "".equals(relevantAssociation);

    if (attributeNotSet) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NO_ATTRIBUTE_CHOOSEN_PIE);
    }
    else if (associationNotSet) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NO_ASSOCIATION_CHOOSEN_PIE);
    }
  }

  public enum BarsOrderMethod {
    DEFAULT("graphicalExport.pieBar.bar.ordering.default"), ALPHANUMERIC("graphicalExport.pieBar.bar.ordering.alphanumeric"), SIZE(
        "graphicalExport.pieBar.bar.ordering.size");

    private final String                              value;

    private static final Map<String, BarsOrderMethod> STRING_TO_TYPE = CollectionUtils.hashMap();
    static {
      STRING_TO_TYPE.put("graphicalExport.pieBar.bar.ordering.default", DEFAULT);
      STRING_TO_TYPE.put("graphicalExport.pieBar.bar.ordering.alphanumeric", ALPHANUMERIC);
      STRING_TO_TYPE.put("graphicalExport.pieBar.bar.ordering.size", SIZE);
    }

    private BarsOrderMethod(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }

    public static BarsOrderMethod getTypeFromString(String keyString) {
      if (!STRING_TO_TYPE.containsKey(keyString)) {
        return DEFAULT;
      }
      else {
        return STRING_TO_TYPE.get(keyString);
      }
    }
  }

  public enum DiagramType {
    PIE("graphicalExport.pieDiagram"), BAR("graphicalExport.barDiagram");

    private final String                          value;

    private static final Map<String, DiagramType> STRING_TO_TYPE = CollectionUtils.hashMap();
    static {
      STRING_TO_TYPE.put("graphicalExport.pieDiagram", PIE);
      STRING_TO_TYPE.put("graphicalExport.barDiagram", BAR);
    }

    private DiagramType(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }

    public DiagramType[] getValues() {
      return values();
    }

    public static DiagramType getTypeFromString(String keyString) {
      if (!STRING_TO_TYPE.containsKey(keyString)) {
        return PIE;
      }
      else {
        return STRING_TO_TYPE.get(keyString);
      }
    }
  }

  private static final String GLOBAL_ASSOCIATION     = "global.associations";
  private static final String GLOBAL_ATTRIBUTES      = "global.attributes";
  private static final String GLOBAL_ATTRIBUTEVALUES = "global.attributevalues";

  public enum DiagramKeyType {
    ASSOCIATION_NAMES(GLOBAL_ASSOCIATION), ASSOCIATION_COUNT("graphicalExport.pieBar.assignmentCountAssociation"), ATTRIBUTE_TYPES(GLOBAL_ATTRIBUTES), ATTRIBUTE_VALUES(
        GLOBAL_ATTRIBUTEVALUES), ATTRIBUTE_COUNT("graphicalExport.pieBar.assignmentCountAttribute");

    private final String                             value;

    private static final Map<String, DiagramKeyType> STRING_TO_TYPE = CollectionUtils.hashMap();
    static {
      STRING_TO_TYPE.put(GLOBAL_ASSOCIATION, ASSOCIATION_NAMES);
      STRING_TO_TYPE.put("graphicalExport.pieBar.assignmentCountAssociation", ASSOCIATION_COUNT);
      STRING_TO_TYPE.put(GLOBAL_ATTRIBUTES, ATTRIBUTE_TYPES);
      STRING_TO_TYPE.put(GLOBAL_ATTRIBUTEVALUES, ATTRIBUTE_VALUES);
      STRING_TO_TYPE.put("graphicalExport.pieBar.assignmentCountAttribute", ATTRIBUTE_COUNT);
    }

    private DiagramKeyType(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }

    public static DiagramKeyType getTypeFromString(String keyString) {
      if (!STRING_TO_TYPE.containsKey(keyString)) {
        return ASSOCIATION_NAMES;
      }
      else {
        return STRING_TO_TYPE.get(keyString);
      }
    }
  }

  public enum ValuesSource {
    ATTRIBUTE(GLOBAL_ATTRIBUTES), ASSOCIATION(GLOBAL_ASSOCIATION);

    private final String                           value;

    private static final Map<String, ValuesSource> STRING_TO_TYPE = CollectionUtils.hashMap();
    static {
      STRING_TO_TYPE.put(GLOBAL_ATTRIBUTES, ATTRIBUTE);
      STRING_TO_TYPE.put(GLOBAL_ASSOCIATION, ASSOCIATION);
    }

    private ValuesSource(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }

    public ValuesSource[] getValues() {
      return values();
    }

    public static ValuesSource getTypeFromString(String keyString) {
      if (!STRING_TO_TYPE.containsKey(keyString)) {
        return ATTRIBUTE;
      }
      else {
        return STRING_TO_TYPE.get(keyString);
      }
    }
  }

  /**
   * Enumeration denoting the type of the bar's or pie's values: the usual values, maintained or not, number of assignments
   */
  public enum ValuesType {
    VALUES(GLOBAL_ATTRIBUTEVALUES), MAINTAINED("graphicalExport.pieBar.maintainance"), COUNT("graphicalExport.pieBar.count");

    private final String                         value;

    private static final Map<String, ValuesType> STRING_TO_TYPE = CollectionUtils.hashMap();
    static {
      STRING_TO_TYPE.put(GLOBAL_ATTRIBUTEVALUES, VALUES);
      STRING_TO_TYPE.put("graphicalExport.pieBar.maintainance", MAINTAINED);
      STRING_TO_TYPE.put("graphicalExport.pieBar.count", COUNT);
    }

    private ValuesType(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }

    public static ValuesType getTypeFromString(String keyString) {
      if (!STRING_TO_TYPE.containsKey(keyString)) {
        return VALUES;
      }
      else {
        return STRING_TO_TYPE.get(keyString);
      }
    }
  }

}
