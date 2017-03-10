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
package de.iteratec.iteraplan.businesslogic.exchange.common.landscape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.Axis;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.AxisElement;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.Content;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.ContentElement;
import de.iteratec.iteraplan.businesslogic.exchange.common.landscape.beans.LandscapeDiagram;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessProcessType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessUnitType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProductType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.GeneralBuildingBlockService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.HashBucketMatrix;
import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.dto.LandscapeDiagramConfigDTO;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;


public class LandscapeDiagramCreator {

  private static final String                                                 SIDE_REFS           = "side";
  private static final String                                                 TOP_REFS            = "top";

  private static final Logger                                                 LOGGER              = Logger
                                                                                                      .getIteraplanLogger(LandscapeDiagramCreator.class);

  /* Configuration parameters: */
  // the incoming config
  private final LandscapeDiagramConfigDTO                                     config;

  private boolean                                                             isBusinessMappingBased;
  /**
   * Maps content building blocks as key to sets of building blocks which are not displayed due to their level,
   * but have their relations aggregated to their key.
   */
  private final Map<BuildingBlock, Set<BuildingBlock>>                        aggregationMap      = CollectionUtils.hashMap();

  private Axis                                                                topAxis;
  private Axis                                                                sideAxis;

  private static final IdentityEntity                                         UNSPEC_AXIS_ELEMENT = UnspecifiedAxisElement.getInstance();

  // consolidated versions (after the distinction BuildingBlock vs Attribute has been resolved)
  private final List<IdentityEntity>                                          topAxisElements     = CollectionUtils.arrayList();
  private String                                                              topAxisLabel;
  private Type<?>                                                             topAxisType;
  private IdentityEntity                                                      topAxisUndefined;
  private final List<IdentityEntity>                                          sideAxisElements    = CollectionUtils.arrayList();
  private String                                                              sideAxisLabel;
  private Type<?>                                                             sideAxisType;
  private IdentityEntity                                                      sideAxisUndefined;

  private final Map<BuildingBlock, Set<IdentityEntity>>                       contentToTopMap     = CollectionUtils.hashMap();
  private final Map<BuildingBlock, Set<IdentityEntity>>                       contentToSideMap    = CollectionUtils.hashMap();

  /* Helpers */
  private AxisHelper                                                          topHelper;
  private AxisHelper                                                          sideHelper;

  /** Maps one side AxisElement and one top AxisElement to a list of ContentElements */
  private HashBucketMatrix<AxisElement<?>, AxisElement<?>, ContentElement<?>> contentElements;

  /** Holds the final LandscapeDiagram */
  private LandscapeDiagram                                                    landscapeDiagram;

  private final EnumAT                                                        status              = new EnumAT();

  private final AttributeTypeDAO                                              attributeTypeDao;
  private final GeneralBuildingBlockService                                   generalBuildingBlockService;

  /**
   * each other are merged together to one bigger content element.
   *
   * @param attributeTypeDao
   */
  public LandscapeDiagramCreator(LandscapeDiagramConfigDTO config, AttributeTypeDAO attributeTypeDao,
      GeneralBuildingBlockService generalBuildingBlockService) {
    super();
    this.config = config;
    this.attributeTypeDao = attributeTypeDao;
    this.generalBuildingBlockService = generalBuildingBlockService;

    if (config.getRowAttributeId() != null && config.getRowAttributeId().intValue() == 0
        || (config.getColumnAttributeId() != null && config.getColumnAttributeId().intValue() == 0)) {
      createAttributeStatus();
    }

    final Map<String, Extension> extensions = InformationSystemReleaseTypeQu.getInstance().getRelations();
    final Set<Extension> bmExtensions = new HashSet<Extension>();
    Extension extensionToAdd = extensions.get(InformationSystemReleaseTypeQu.EXTENSION_BM_BUSINESSPROCESS);
    if (extensionToAdd != null) {
      bmExtensions.add(extensionToAdd);
    }
    extensionToAdd = extensions.get(InformationSystemReleaseTypeQu.EXTENSION_BM_BUSINESSUNIT);
    if (extensionToAdd != null) {
      bmExtensions.add(extensionToAdd);
    }
    extensionToAdd = extensions.get(InformationSystemReleaseTypeQu.EXTENSION_BM_PRODUCT);
    if (extensionToAdd != null) {
      bmExtensions.add(extensionToAdd);
    }

    if (bmExtensions.contains(config.getColumnExtension()) && bmExtensions.contains(config.getRowExtension())) {
      this.isBusinessMappingBased = true;
    }
  }

  /**
   * Calls the individual steps to create a LandscapeDiagram.
   *
   * @return A fully initialized LandscapeDiagram.
   */
  public LandscapeDiagram createLandscapeDiagram() {
    if (landscapeDiagram != null) {
      return landscapeDiagram;
    }

    initializeContentStructures();

    buildAxisElements();
    buildContentElements();
    finalizeAxis();

    if (config.isSpanContentBetweenCells()) {
      mergeContentElements();
    }

    fillLandscapeDiagram();
    postprocessLandscapeDiagram();
    calculateInternalLevels();

    landscapeDiagram.setUseNamesLegend(config.isUseNamesLegend());

    return landscapeDiagram;
  }

  /**
   * Maps the data coming from the iteraplan queries into the more abstract inner model. This has to
   * be called before any other calculations. It is rather complicated and redundant in ways that
   * are not easy to avoid the problem is missing a common abstraction between the two types of
   * relations supported: building block to building block and building block to attribute. At least
   * the rest of this class and deeper code shouldn't have to care about it anymore after this
   * method is finished.
   */
  private void initializeContentStructures() {

    config.setContentBbs(refreshElements(config.getContentBbs()));

    final Extension columnExtension = config.getColumnExtension();
    topAxisLabel = initializeAxisElements(topAxisElements, config.getTopAxisBbs(), columnExtension, config.getColumnAttributeId());
    topAxisType = (columnExtension != null) ? columnExtension.getRequestedType() : null;

    final Extension rowExtension = config.getRowExtension();
    sideAxisLabel = initializeAxisElements(sideAxisElements, config.getSideAxisBbs(), rowExtension, config.getRowAttributeId());
    sideAxisType = (rowExtension != null) ? rowExtension.getRequestedType() : null;

    createAggregationMapping();

    buildContentToAxesMappings();

    verifyAxisSelectionNotEmpty();
  }

  private String initializeAxisElements(List<IdentityEntity> axisElements, List<? extends BuildingBlock> axisBbsFromConfig, Extension extension,
                                        Integer attributeId) {
    String axisLabel = "";
    if (extension != null) {
      axisElements.clear();
      axisElements.addAll(refreshElements(axisBbsFromConfig));
      axisLabel = MessageAccess.getStringOrNull(extension.getNameKeyForPresentation());
    }
    else {
      final AttributeType attrType = attributeTypeDao.loadObjectById(attributeId, AttributeType.class);

      axisElements.clear();
      if (attrType instanceof EnumAT) {
        axisElements.addAll(((EnumAT) attrType).getSortedAttributeValues());
        axisLabel = ((EnumAT) attrType).getName();
      }
      else if (attrType instanceof ResponsibilityAT) {
        axisElements.addAll(((ResponsibilityAT) attrType).getSortedAttributeValues());
        axisLabel = ((ResponsibilityAT) attrType).getName();
      }
      else if (attributeId.intValue() == 0) {
        axisElements.addAll(status.getSortedAttributeValues());
        axisLabel = status.getName();
      }
    }
    if (config.isShowUnspecifiedRelations()) {
      axisElements.add(UNSPEC_AXIS_ELEMENT);
    }
    return axisLabel;
  }

  private void createAggregationMapping() {
    if (useAggregation()) {
      final Set<BuildingBlock> contentToRemove = CollectionUtils.hashSet();

      for (BuildingBlock content : config.getContentBbs()) {
        final InformationSystemRelease isr = (InformationSystemRelease) content;

        if (isBusinessMappingBased) {
          Set<BusinessMapping> bms = isr.getBusinessMappings();
          for (BusinessMapping bm : bms) {
            addBbToAggregationMapping(bm, isr);
          }
        }

        final int level = isr.getLevel();
        if (level < config.getContentTopLevel()) {
          contentToRemove.add(content);
        }
        else if (level > config.getContentBottomLevel()) {
          InformationSystemRelease parent = isr.getParent();
          while ((!config.getContentBbs().contains(parent) || contentToRemove.contains(parent)) && parent != null) {
            parent = parent.getParent();
          }
          if (parent != null && parent.getLevel() >= config.getContentTopLevel()) {
            addBbToAggregationMapping(content, parent);
          }
          contentToRemove.add(content);
        }
      }
      config.getContentBbs().removeAll(contentToRemove);
    }
  }

  /**
   * @return true, if aggregation of relations is to be used
   */
  private boolean useAggregation() {
    return config.getContentType() instanceof InformationSystemReleaseTypeQu;
  }

  /**
   * See {@link #aggregationMap}
   * @param source
   *          building block which relations should be aggregated
   * @param parent
   *          building block which these relations should be aggregated to
   */
  private void addBbToAggregationMapping(BuildingBlock source, BuildingBlock parent) {
    if (!aggregationMap.containsKey(parent)) {
      aggregationMap.put(parent, new HashSet<BuildingBlock>());
    }
    final Set<BuildingBlock> bbsToAggregateRelationsFrom = aggregationMap.get(parent);
    bbsToAggregateRelationsFrom.add(source);
    if (aggregationMap.containsKey(source)) {
      bbsToAggregateRelationsFrom.addAll(aggregationMap.get(source));
      //      aggregationMap.remove(source);
    }
  }

  /**
   * Creates a basic mapping of content elements to the axes' elements.
   */
  private void buildContentToAxesMappings() {

    final Set<IdentityEntity> unreferencedTopElements = new HashSet<IdentityEntity>(topAxisElements);
    final Set<IdentityEntity> unreferencedSideElements = new HashSet<IdentityEntity>(sideAxisElements);

    contentToTopMap.clear();
    contentToSideMap.clear();
    for (BuildingBlock content : config.getContentBbs()) {
      Map<String, Set<IdentityEntity>> axesRefsMap = calculateAxesReferences(content);
      final Set<IdentityEntity> validTopRefs = axesRefsMap.get(TOP_REFS);
      final Set<IdentityEntity> validSideRefs = axesRefsMap.get(SIDE_REFS);

      if (aggregationMap.containsKey(content)) {
        for (BuildingBlock childContent : aggregationMap.get(content)) {
          Map<String, Set<IdentityEntity>> subAxesRefs = calculateAxesReferences(childContent);
          if (config.isStrictRelations()) {
            addRefsToContentMap(childContent, subAxesRefs.get(TOP_REFS), subAxesRefs.get(SIDE_REFS), unreferencedTopElements,
                unreferencedSideElements);
          }
          else {
            validTopRefs.addAll(subAxesRefs.get(TOP_REFS));
            validSideRefs.addAll(subAxesRefs.get(SIDE_REFS));
          }
        }
      }

      cleanupAggregatedRefs(validTopRefs, validSideRefs);

      addRefsToContentMap(content, validTopRefs, validSideRefs, unreferencedTopElements, unreferencedSideElements);
    }

    finalizeContentToAxesMappings(unreferencedTopElements, unreferencedSideElements);
  }

  private void cleanupAggregatedRefs(final Set<IdentityEntity> validTopRefs, final Set<IdentityEntity> validSideRefs) {
    if (validTopRefs.isEmpty() || validSideRefs.isEmpty()) {
      // only add references for building block 'content' if both axes are referenced
      validTopRefs.clear();
      validSideRefs.clear();
    }

    // if references to specific axis-elements exist, remove the unspecified axis element from the references
    if (validTopRefs.contains(UNSPEC_AXIS_ELEMENT) && validTopRefs.size() >= 2) {
      validTopRefs.remove(UNSPEC_AXIS_ELEMENT);
    }
    if (validSideRefs.contains(UNSPEC_AXIS_ELEMENT) && validSideRefs.size() >= 2) {
      validSideRefs.remove(UNSPEC_AXIS_ELEMENT);
    }
  }

  private void addRefsToContentMap(BuildingBlock childContent, Set<IdentityEntity> topRefs, Set<IdentityEntity> sideRefs,
                                   Set<IdentityEntity> unreferencedTopElements, Set<IdentityEntity> unreferencedSideElements) {
    contentToTopMap.put(childContent, topRefs);
    unreferencedTopElements.removeAll(topRefs);
    contentToSideMap.put(childContent, sideRefs);
    unreferencedSideElements.removeAll(sideRefs);
  }

  /**
   * Calculates the references a given building block has to the axes and returns these references.
   * In case of using the strict interpretation of relations, if the building block has only references to one of the axes, those
   * are discarded as invalid.
   * In case the "showUnspecifiedRelations" flag is set, missing relations to one or both of the axes are substituted by relations
   * to the corresponding "unspecified" axis element.
   * @param content
   *          The given building block
   * @return Map consisting of two entries of {@link IdentityEntity} sets: "top" for references to the top-axis, "side" for references
   *          to the side-axis.
   */
  private Map<String, Set<IdentityEntity>> calculateAxesReferences(BuildingBlock content) {
    Map<String, Set<IdentityEntity>> validAxisRefs = CollectionUtils.hashMap();
    Set<IdentityEntity> contentTopRefs = CollectionUtils.hashSet();
    Set<IdentityEntity> contentSideRefs = CollectionUtils.hashSet();

    if (!isBusinessMappingBased || isAggregationLeaf(content)) {
      // if the diagram's axes both consist of business mapping elements, only consider the elements which don't get relations
      // aggregated from subordinate elements
      contentTopRefs = getAssociatedAxisElements(topAxisElements, config.getColumnExtension(), config.getColumnAttributeId(), content);
      contentSideRefs = getAssociatedAxisElements(sideAxisElements, config.getRowExtension(), config.getRowAttributeId(), content);

      if (config.isShowUnspecifiedRelations()) {
        // add unspecified relation as reference to otherwise unreferenced axes
        if (contentTopRefs.isEmpty()) {
          contentTopRefs.add(UNSPEC_AXIS_ELEMENT);
        }
        if (contentSideRefs.isEmpty()) {
          contentSideRefs.add(UNSPEC_AXIS_ELEMENT);
        }
      }
      if (config.isStrictRelations() && (contentTopRefs.isEmpty() || contentSideRefs.isEmpty())) {
        // when using strict relations only add the references if there is at least one for each of the axes.
        contentTopRefs.clear();
        contentSideRefs.clear();
      }
    }

    validAxisRefs.put(TOP_REFS, contentTopRefs);
    validAxisRefs.put(SIDE_REFS, contentSideRefs);

    return validAxisRefs;
  }

  /**
   * Checks if the given building block has child-elements to aggregate relations from or not.
   * @param bb
   *          a building block
   * @return true if the building block is a leaf, meaning has no children to get relations from
   */
  private boolean isAggregationLeaf(BuildingBlock bb) {
    return !aggregationMap.containsKey(bb) || aggregationMap.get(bb).isEmpty();
  }

  private void finalizeContentToAxesMappings(Set<IdentityEntity> unreferencedTopElements, Set<IdentityEntity> unreferencedSideElements) {
    if (!contentToTopMap.keySet().equals(contentToSideMap.keySet())) {
      LOGGER.error("Inconsistent state of contentToTopMap and contentToSideMap: different key-sets");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    // remove content elements which aren't appearing in the diagram from the selection.
    config.getContentBbs().retainAll(contentToTopMap.keySet());

    // remove unreferenced axis-elements, if according filters are set.
    if (config.isFilterEmptyColumns()) {
      topAxisElements.removeAll(unreferencedTopElements);
    }
    if (config.isFilterEmptyRows()) {
      sideAxisElements.removeAll(unreferencedSideElements);
    }

    // set unspecified axis elements if necessary
    if (topAxisElements.contains(UNSPEC_AXIS_ELEMENT)) {
      topAxisUndefined = UNSPEC_AXIS_ELEMENT;
    }
    if (sideAxisElements.contains(UNSPEC_AXIS_ELEMENT)) {
      sideAxisUndefined = UNSPEC_AXIS_ELEMENT;
    }
  }

  private Set<IdentityEntity> getAssociatedAxisElements(List<IdentityEntity> axisElements, Extension axisExtension, Integer axisAttributeId,
                                                        BuildingBlock content) {
    Set<IdentityEntity> associatedAxisElements;
    if (axisExtension != null) {
      associatedAxisElements = getAssociatedExtensionAxisElements(content, axisExtension, axisElements);
    }
    else {
      associatedAxisElements = getAssociatedAttributeAxisElements(content, axisAttributeId, axisElements);
    }
    return associatedAxisElements;
  }

  private void verifyAxisSelectionNotEmpty() {
    if (topAxisElements.size() == 0 || sideAxisElements.size() == 0) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.LANDSCAPE_DIAGRAM_EMPTY_AFTER_FILTER);
    }
  }

  @SuppressWarnings("unchecked")
  private void finalizeAxis() {
    topHelper.mergeBottomAxisElements(contentElements);
    sideHelper.mergeBottomAxisElements(contentElements);
    topAxis = new Axis(topAxisLabel, topHelper.getAxisElementTree(), topHelper.getMaxLevel());
    sideAxis = new Axis(sideAxisLabel, sideHelper.getAxisElementTree(), sideHelper.getMaxLevel());
  }

  /**
   * Fills the landscape diagram with the calculated axis and content elements.
   */
  private void fillLandscapeDiagram() {
    final Content content = new Content(contentElements);
    landscapeDiagram = new LandscapeDiagram(createTitleString(), topAxis, sideAxis, content, config.isColumnAxisScalesWithContent());
  }

  private String createTitleString() {
    final StringBuilder title = new StringBuilder();
    title.append(MessageAccess.getStringOrNull("graphicalExport.landscape.title.start")).append(" - ");
    title.append(MessageAccess.getStringOrNull("graphicalExport.reportDate") + " ");
    title.append(DateUtils.formatAsStringToLong(new Date(), UserContext.getCurrentLocale())).append("\n");
    title.append(MessageAccess.getStringOrNull("graphicalExport.landscape.title.axes")).append(": ");
    title.append(topAxisLabel).append(" ");
    title.append(MessageAccess.getStringOrNull("global.and")).append(" ");
    title.append(sideAxisLabel).append("\n");
    title.append(MessageAccess.getStringOrNull("graphicalExport.title.content")).append(": ");
    title.append(MessageAccess.getStringOrNull(config.getContentType().getTypeNamePluralPresentationKey()));
    return title.toString();
  }

  /**
   * This method assumes that the axis elements have already been built. It creates the
   * ContentElements and sets the references between Content- and AxisElements.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void buildContentElements() {
    contentElements = new HashBucketMatrix<AxisElement<?>, AxisElement<?>, ContentElement<?>>();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Building content elements (" + config.getContentBbs().size() + ")");
    }

    final HashBucketMatrix<IdentityEntity, IdentityEntity, BuildingBlock> relation = createLogicalContentMap();

    for (HashBucketMatrix.Entry<IdentityEntity, IdentityEntity, BuildingBlock> entry : relation.entrySet()) {
      final List<AxisElement> sideAxisElList = sideHelper.getBbToAxisElementList().getBucketNotNull(entry.getKey1());
      final List<AxisElement> topAxisElList = topHelper.getBbToAxisElementList().getBucketNotNull(entry.getKey2());

      for (AxisElement<ContentElement<BuildingBlock>> sideAxisEl : sideAxisElList) {
        for (AxisElement<ContentElement<BuildingBlock>> topAxisEl : topAxisElList) {

          final ContentElement<BuildingBlock> contentEl = new ContentElement<BuildingBlock>(topAxisEl, sideAxisEl, entry.getValue());

          topAxisEl.addContentElement(contentEl);
          sideAxisEl.addContentElement(contentEl);

          contentElements.add(sideAxisEl, topAxisEl, contentEl);

          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Created content element for : " + contentEl.getName() + " top: " + topAxisEl.getElement().getIdentityString() + " side: "
                + sideAxisEl.getElement().getIdentityString());
          }
        }
      }

    }
  }

  /**
   * For creating the contents, a logical content map is needed. It maps one side and one top
   * element to a list of content elements, depending on the configuration. This map then serves as
   * a basis to create the visual ContentElements.
   *
   * @return a logical content map: Side Axis Element + Top Axis Element -> Content Element(s)
   */
  private HashBucketMatrix<IdentityEntity, IdentityEntity, BuildingBlock> createLogicalContentMap() {
    final HashBucketMatrix<IdentityEntity, IdentityEntity, BuildingBlock> map = new HashBucketMatrix<IdentityEntity, IdentityEntity, BuildingBlock>();
    for (BuildingBlock content : config.getContentBbs()) {
      Set<BuildingBlock> bbSet = aggregationMap.get(content);
      if (bbSet == null) {
        bbSet = CollectionUtils.hashSet();
      }
      bbSet.add(content);

      for (BuildingBlock bb : bbSet) {
        if (contentToTopMap.containsKey(bb) && contentToSideMap.containsKey(bb)) {
          Set<IdentityEntity> topAxisRefs = contentToTopMap.get(bb);
          Set<IdentityEntity> sideAxisRefs = contentToSideMap.get(bb);

          for (IdentityEntity colRef : topAxisRefs) {
            for (IdentityEntity rowRef : sideAxisRefs) {
              if (!(map.getBucketNotNull(rowRef, colRef).contains(content))) {
                map.add(rowRef, colRef, content);
              }
            }
          }
        }
      }
    }
    return map;
  }

  private Set<IdentityEntity> getAssociatedExtensionAxisElements(BuildingBlock content, Extension extension, Collection<?> domain) {

    final Set<IdentityEntity> retVal = new HashSet<IdentityEntity>();
    if (content == null) {
      return retVal;
    }

    Set<BuildingBlock> associatedElements;
    if (content instanceof BusinessMapping) {
      associatedElements = Sets.newHashSet(getTypeFromBm(extension.getRequestedType(), (BusinessMapping) content));
    }
    else {
      associatedElements = extension.getConditionElements(content);
    }
    retVal.addAll(associatedElements);

    // add all children connections:
    if (this.config.isHideChildrenElements() && content instanceof InformationSystemRelease) {
      // select all children:
      final InformationSystemRelease isr = (InformationSystemRelease) content;
      // get connected elements
      for (BuildingBlock child : isr.getChildrenAsList()) {
        final Set<IdentityEntity> connectedElements = getAssociatedExtensionAxisElements(child, extension, domain);
        // add to collection
        retVal.addAll(connectedElements);
      }
    }
    retVal.retainAll(domain);

    return retVal;
  }

  private BuildingBlock getTypeFromBm(Type<?> type, BusinessMapping bm) {
    AbstractHierarchicalEntity<?> result = null;
    if (type instanceof BusinessProcessType) {
      result = bm.getBusinessProcess();
    }
    else if (type instanceof BusinessUnitType) {
      result = bm.getBusinessUnit();
    }
    else if (type instanceof ProductType) {
      result = bm.getProduct();
    }

    if (result == null || result.isTopLevelElement()) {
      return null;
    }
    else {
      return result;
    }
  }

  private Set<IdentityEntity> getAssociatedAttributeAxisElements(BuildingBlock content, Integer attributeTypeId, List<?> domain) {
    final Set<IdentityEntity> retVal = new HashSet<IdentityEntity>();

    //If the status attribute is selected
    if (attributeTypeId.intValue() == 0) {

      String typeOfStatus = null;
      String localizedTypeOfStatus = null;
      final List<EnumAV> attributeValues = new ArrayList<EnumAV>();

      if (content instanceof InformationSystemRelease) {
        typeOfStatus = ((InformationSystemRelease) content).getTypeOfStatusAsString();
      }
      else {
        typeOfStatus = ((TechnicalComponentRelease) content).getTypeOfStatusAsString();
      }

      localizedTypeOfStatus = MessageAccess.getStringOrNull(typeOfStatus);

      for (EnumAV av : status.getSortedAttributeValues()) {
        if (localizedTypeOfStatus.equals(av.getName())) {
          final AttributeValueAssignment ava = new AttributeValueAssignment();
          ava.setAttributeValue(av);
          ava.setBuildingBlock(content);

          final Set<AttributeValueAssignment> avaContainer = new HashSet<AttributeValueAssignment>();
          avaContainer.add(ava);

          av.setAttributeValueAssignments(avaContainer);

          attributeValues.add(av);
        }
      }

      retVal.addAll(attributeValues);
      retVal.retainAll(domain);

    }
    else {
      final AttributeType attributeType = attributeTypeDao.loadObjectById(attributeTypeId, AttributeType.class);
      retVal.addAll(content.getConnectedAttributeValues(attributeType));
      retVal.retainAll(domain);
    }

    return retVal;
  }

  /**
   * Merges two adjacent ContentElements together. The first ContentElement of a merged group is
   * increased in size and the other ContentElements are removed from the contentElements list. Note
   * that references between the content and the axis elements remain the same (they are not used by
   * the LandscapeDiagramExport when creating the shapes).
   */
  private void mergeContentElements() {
    if (!config.isMergeContent()) {
      return;
    }

    final List<List<Cell>> cells = getOrderedCells();

    LOGGER.debug("Merging content elements...");

    for (List<Cell> cellList : cells) {
      List<ContentElement<?>> currentContentEls = null;
      boolean first = true;
      for (Cell cell : cellList) {
        if (!first) {
          if (hasChildrenButNoContent(cell.getAxisElement())) {
            LOGGER.debug("skipping placeholder");
          }
          else {
            mergeCurrentContentElements(currentContentEls, cell);
          }
        }
        else {
          currentContentEls = new ArrayList<ContentElement<?>>(cell.getContentElements());
          first = false;
        }
      }
    }
  }

  /**
   * Merges the given content elements with the ones of the given cell, if possible.
   * Each of the current content elements, which couldn't be merged with an element of {@code cell}
   * is removed from {@code currentContentEls} since it won't be relevant for future merging.
   * Not merged elements from {@code cell} are added to {@code currentContentEls} for future merging.
   * @param currentContentEls
   *          content elements to check for merging
   * @param cell
   */
  private void mergeCurrentContentElements(List<ContentElement<?>> currentContentEls, Cell cell) {
    for (final Iterator<ContentElement<?>> itTmp = currentContentEls.iterator(); itTmp.hasNext();) {
      final ContentElement<?> contentEl = itTmp.next();
      if (!merged(contentEl, cell.getContentElements())) {
        itTmp.remove();
      }
    }
    currentContentEls.addAll(cell.getContentElements());
  }

  /**
   * Returns the diagrams content elements in a list of rows or columns, depending on
   * {@code config.columnAxisScalesWithContent}. Each row or column consists of a list
   * of {@link Cell} objects. 
   * @return List of List of {@link Cell} objects
   */
  private List<List<Cell>> getOrderedCells() {
    final List<List<Cell>> cells = new ArrayList<List<Cell>>();
    final List<AxisElement<?>> firstAxisElements = getFirstAxisElements();
    final List<AxisElement<?>> secondAxisElements = getSecondAxisElements();

    for (int firstIndex = 0; firstIndex < firstAxisElements.size(); firstIndex++) {
      final AxisElement<?> firstAxisEl = firstAxisElements.get(firstIndex);

      final List<Cell> cellList = new ArrayList<Cell>();
      for (int secondIndex = 0; secondIndex < secondAxisElements.size(); secondIndex++) {
        final AxisElement<?> secondAxisEl = secondAxisElements.get(secondIndex);
        List<ContentElement<?>> bucket;
        if (config.isColumnAxisScalesWithContent()) {
          bucket = contentElements.getBucketNotNull(secondAxisEl, firstAxisEl);
        }
        else {
          bucket = contentElements.getBucketNotNull(firstAxisEl, secondAxisEl);
        }
        cellList.add(new Cell(secondAxisEl, bucket));
      }
      cells.add(cellList);
    }

    return cells;
  }

  private void createAttributeStatus() {

    final Locale locale = UserContext.getCurrentLocale();
    final Set<EnumAV> attributeValues = Sets.newHashSet();
    List<String> statusValues;

    status.setId(Integer.valueOf(0));
    status.setName(MessageAccess.getStringOrNull("global.type_of_status"));

    if (config.getContentType() instanceof InformationSystemReleaseTypeQu) {
      statusValues = StringEnumReflectionHelper.getLanguageSpecificEnumValues(
          de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus.class, locale);
    }
    else {
      statusValues = StringEnumReflectionHelper.getLanguageSpecificEnumValues(
          de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.class, locale);
    }

    int pos = 0;
    for (String value : statusValues) {
      final EnumAV tmp = new EnumAV();
      tmp.setName(value);
      tmp.setAttributeType(status);
      tmp.setPosition(pos++);

      attributeValues.add(tmp);
    }

    status.setAttributeValues(attributeValues);
  }

  private List<AxisElement<?>> getSecondAxisElements() {
    if (config.isColumnAxisScalesWithContent()) {
      return sideAxis.getElements();
    }
    else {
      return topAxis.getElements();
    }
  }

  private List<AxisElement<?>> getFirstAxisElements() {
    if (config.isColumnAxisScalesWithContent()) {
      return topAxis.getElements();
    }
    else {
      return sideAxis.getElements();
    }
  }

  /**
   * if element has children, but no content elements, it is not relevant for merging.
   * @param aElTmp
   * @return true if element has children, but no content
   */
  private boolean hasChildrenButNoContent(AxisElement<?> aElTmp) {
    return !aElTmp.getChildren().isEmpty() && aElTmp.getContentElements().isEmpty();
  }

  private boolean merged(ContentElement<?> cEl, List<ContentElement<?>> neighbourBucket) {
    for (final Iterator<ContentElement<?>> itTmp = neighbourBucket.iterator(); itTmp.hasNext();) {
      final ContentElement<?> cElTmp = itTmp.next();
      if (cEl.getElement().equals(cElTmp.getElement())) {
        cEl.increaseSize();
        itTmp.remove();
        LOGGER.debug("merging elements!");
        return true;
      }
    }
    return false;
  }

  /**
   * Creates the AxisElements for the top and side axis. AxisElements with a level lower than
   * topAxisTopLevel and sideAxisTopLevel are not created. After this method completes, the
   * AxisElements are not fully initialized yet! They lack connection to the ContentElements.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void buildAxisElements() {
    topHelper = new AxisHelper(topAxisElements, config.getTopAxisTopLevel(), config.getTopAxisBottomLevel(), topAxisType, topAxisUndefined);
    sideHelper = new AxisHelper(sideAxisElements, config.getSideAxisTopLevel(), config.getSideAxisBottomLevel(), sideAxisType, sideAxisUndefined);
  }

  /**
   * This method determines the number of content elements per row/column depending on whether the
   * scaling of content elements is enabled or disabled.
   */
  @SuppressWarnings("rawtypes")
  private void postprocessLandscapeDiagram() {

    // Build the closures for all axis elements and all content elements
    if (config.isColumnAxisScalesWithContent()) {
      // Vertical -> work with the top axis
      for (AxisElement axisElement : landscapeDiagram.getTopAxis().getElements()) {
        axisElement.createContentElementGroups(config.isColumnAxisScalesWithContent(), config.isScaleDownGraphicElements(),
            config.isSpanContentBetweenCells());
      }
    }
    else {
      // Horizontal -> work with the side axis
      for (AxisElement axisElement : landscapeDiagram.getSideAxis().getElements()) {
        axisElement.createContentElementGroups(config.isColumnAxisScalesWithContent(), config.isScaleDownGraphicElements(),
            config.isSpanContentBetweenCells());
      }
    }

    // Set scaling in the bean
    landscapeDiagram.setScaleDownContentElements(config.isScaleDownGraphicElements());
  }

  /**
   * The internal levels will be calculated, and set to content elements. This method has to be called before:
   * {@link de.iteratec.iteraplan.businesslogic.exchange.common.landscape.LandscapeDiagramCreator#postprocessLandscapeDiagram()}
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void calculateInternalLevels() {

    if (config.isColumnAxisScalesWithContent()) {
      // Vertical -> work with the top axis
      for (AxisElement topAxisElement : landscapeDiagram.getTopAxis().getElements()) {
        for (AxisElement sideAxisElement : landscapeDiagram.getSideAxis().getElements()) {
          calculateInternalLevel(sideAxisElement, topAxisElement, true);
        }
      }
    }
    else {
      // Horizontal -> work with the side axis
      for (AxisElement sideAxisElement : landscapeDiagram.getSideAxis().getElements()) {
        for (AxisElement topAxisElement : landscapeDiagram.getTopAxis().getElements()) {
          calculateInternalLevel(sideAxisElement, topAxisElement, false);
        }
      }
    }

  }

  /**
   * This method sets the internal level for all content elements in the Content Group. So this method has to be called
   * for every cell in the table, identified by the sideAxisElement and the topAxisElement, once. In this case this 
   * method will not calculate the internal level again. If there is no contentElement in the selected cell, this
   * method will not set any internal levels. Note that there can be contentElements in an other cell of the group.
   * @param topAxisElement
   * @param sideAxisElement
   * @param vertical The scaling direction of the LandscapeDiagram.
   */
  @SuppressWarnings("unchecked")
  private <E extends IdentityEntity & Comparable<E>> void calculateInternalLevel(AxisElement<ContentElement<E>> sideAxisElement,
                                                                                 AxisElement<ContentElement<E>> topAxisElement, boolean vertical) {
    AxisElement<ContentElement<E>> axisElement = vertical ? topAxisElement : sideAxisElement;

    HashBucketMatrix<AxisElement<?>, AxisElement<?>, List<ContentElement<?>>> groups = axisElement.getContentElementGroups();
    List<List<ContentElement<?>>> bucket = groups.getBucket(sideAxisElement, topAxisElement);
    if (bucket == null || bucket.isEmpty()) {
      return;
    }

    List<ContentElement<?>> elementsInContent = bucket.get(0);
    if (elementsInContent == null || elementsInContent.isEmpty()) {
      return;
    }

    ContentElement<E> anyElement = (ContentElement<E>) elementsInContent.get(0);
    if (anyElement.getInternalLevel() > 0) {
      // calculateInternalLevel has  been called for this group, before
      return;
    }

    List<List<ContentElement<E>>> sortedGroups = anyElement.getContentElementGroupForElement(axisElement);

    int level = 1;
    for (Iterator<List<ContentElement<E>>> it = sortedGroups.iterator(); it.hasNext(); level++) {
      List<ContentElement<E>> l = it.next();
      for (ContentElement<E> ce : l) {
        ce.setInternalLevel(level);
      }
    }
  }

  /**
   * @param elements
   *          A list of Hibernate objects
   * @return The given list of Hibernate objects, but attached to the current Hibernate Session.
   */
  private <T extends BuildingBlock> List<BuildingBlock> refreshElements(Collection<T> elements) {
    final List<BuildingBlock> newElements = generalBuildingBlockService.refreshBuildingBlocks(elements);

    if (newElements.isEmpty()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.LANDSCAPE_NO_ELEMENTS);
    }
    return newElements;
  }

  /**
   * @param axisElementTree
   *          The AxisElements to print.
   * @param indentParam
   *          Needed for recursion. Should be an empty String when calling this method.
   */
  public static <CE extends ContentElement<?>> void printTree(List<AxisElement<CE>> axisElementTree, String indentParam) {
    String indent = indentParam;
    if (!LOGGER.isDebugEnabled()) {
      return;
    }
    indent += "   ";
    for (AxisElement<CE> element : axisElementTree) {
      LOGGER.debug(indent + element.toString());
      printTree(element.getChildren(), indent);
    }
  }

  /**
   * Represents a cell of the landscape grid 
   */
  private static class Cell {
    private final AxisElement<?>          axisElement;
    private final List<ContentElement<?>> contentElements;

    /**
     * Constructor
     * @param axisElement
     *          associated top-axis element, if cells are to be ordered in rows, side-axis element
     *          if cells are to be ordered in columns
     * @param contentElements
     *          List of content elements in this cell
     */
    public Cell(AxisElement<?> axisElement, List<ContentElement<?>> contentElements) {
      super();
      this.axisElement = axisElement;
      this.contentElements = contentElements;
    }

    public AxisElement<?> getAxisElement() {
      return axisElement;
    }

    public List<ContentElement<?>> getContentElements() {
      return contentElements;
    }
  }

  private static final class UnspecifiedAxisElement implements IdentityEntity {

    /** Serialization version */
    private static final long                   serialVersionUID = 7015906154952266658L;

    private static final Integer                ID               = Integer.valueOf(-1);
    private static final String                 IDENTITY_STRING  = " - ";

    private static final UnspecifiedAxisElement INSTANCE         = new UnspecifiedAxisElement();

    /** empty constructor */
    private UnspecifiedAxisElement() {
      // nothing happens here
    }

    public static UnspecifiedAxisElement getInstance() {
      return INSTANCE;
    }

    public String getIdentityString() {
      return IDENTITY_STRING;
    }

    public Integer getId() {
      return ID;
    }

    public void setId(Integer id) {
      // id not changeable
    }
  }

}
