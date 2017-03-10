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
package de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.EntityService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessMappingEntity;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.model.sorting.BuildingBlockSortHelper;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.strategy.BusinessMappingStrategy;
import de.iteratec.iteraplan.presentation.dialog.common.model.businessmapping.strategy.BusinessMappingStrategyFactory;


/**
 * @param <T>
 *          the building block type from which the business mapping is seen. Currently,
 *          {@link InformationSystemRelease}, {@link Product}, {@link BusinessProcess} and
 *          {@link BusinessUnit} are accepted types.
 * @param <C>
 *          the building block type which is used for clustering the business mapping. Must be
 *          different from <code>T</code>! Currently, {@link InformationSystemRelease},
 *          {@link Product}, {@link BusinessProcess} and {@link BusinessUnit} are accepted types.
 */
public class BusinessMappingsComponentModel<T extends BuildingBlock, C extends BuildingBlock> extends AbstractComponentModelBase<T> {

  private static final Logger                         LOGGER           = Logger.getIteraplanLogger(BusinessMappingsComponentModel.class);

  private static final long                           serialVersionUID = 7971154137961834589L;

  private static final String                         ACTION_DELETE    = "delete";

  /** Property key for the header of the table. */
  private final String                                tableHeaderKey;

  /** The source instance for which the business mappings are managed. */
  private T                                           source;

  /** Ordered list of clustering parts (one part clusters all business mappings for a certain cluster element of type C). */
  private final List<ClusterComponentModelPart<T, C>> clusterParts     = Lists.newArrayList();
  private List<DisplayElements>                       elementDisplayOrder;

  /** Component Model Part that manages the creation of new business mapping instances. */
  private NewBusinessMappingComponentModelPart<T, C>  newMappingsPart  = null;

  private BusinessMappingItems                        bmiSorted        = new BusinessMappingItems();

  private final ClusterElementRetriever<C>            clusterElementRetriever;

  /** The GUI action to carry out within {@link #update()}. */
  private String                                      action;

  /**
   * The position of the cluster element which was selected for an action (e.g. delete).
   * <p>
   * The first element has position 1, the second 2, ... This unusual index start is necessary as
   * the web framework converts a null/empty value for an Integer into the value 0. The index must
   * therefore be adapted.
   * </p>
   */
  private Integer                                     selectedClusterPosition;

  /**
   * The position of the business mapping element which was selected for an action (e.g. delete).
   * This position is relative to the {@link #selectedClusterPosition selected cluster position},
   * i.e. in each cluster numbering starts at 1.
   * <p>
   * The first element has position 1, the second 2, ... This unusual index start is necessary as
   * the web framework converts a null/empty value for an Integer into the value 0. The index must
   * therefore be adapted.
   * </p>
   */
  private Integer                                     selectedMappingPosition;

  /**
   * Creates an instance of this class with the required parameters.
   * 
   * @param componentMode the component mode
   * @param clusterElementRetriever the cluster element retriever
   * @param htmlId the html id
   * @param tableHeaderKey the table header key
   * @param elementDisplayOrder The first item in this list should correspond to the second generic parameter to this class
   */
  public BusinessMappingsComponentModel(ComponentMode componentMode, ClusterElementRetriever<C> clusterElementRetriever, String htmlId,
      String tableHeaderKey, List<DisplayElements> elementDisplayOrder, Class<? extends BuildingBlock> owningBuildingBlockType) {

    super(componentMode, htmlId);
    this.clusterElementRetriever = clusterElementRetriever;
    this.tableHeaderKey = tableHeaderKey;
    this.newMappingsPart = new NewBusinessMappingComponentModelPart<T, C>(getComponentMode(), getHtmlId() + "_newMappings", this,
        owningBuildingBlockType);
    this.elementDisplayOrder = elementDisplayOrder;
  }

  public void initializeFrom(T sourceBB) {
    this.source = sourceBB;
    final HashBucketMap<Integer, BusinessMapping> bpToMappings = new HashBucketMap<Integer, BusinessMapping>();
    final List<BusinessMapping> bmList = getBusinessMappingsForSource(source);

    // first, build the mapping cluster element -> business mapping elements and the set of business
    // processes
    final Set<C> clustersSet = Sets.newHashSet();
    for (BusinessMapping bpc : bmList) {
      final C bp = clusterElementRetriever.getClusterElementFromMapping(bpc);
      if (bp == null) {
        LOGGER.debug("Business Mapping '" + bpc + "' has a null value at [...]. Skipping.");
        continue;
      }

      bpToMappings.add(bp.getId(), bpc);
      clustersSet.add(bp);
    }

    final List<C> clusters = Lists.newArrayList();
    clusters.addAll(clustersSet);
    BuildingBlockSortHelper.sortByDefault(clusters);

    if (getComponentMode() == ComponentMode.EDIT || getComponentMode() == ComponentMode.CREATE) {
      this.initializeBusinessMappingItems();

      // initialize the component model for new business mappings if in edit mode
      newMappingsPart.initializeFrom(bmiSorted);
    }

    // second, create new business mapping cluster parts for every business process
    for (C bp : clusters) {
      ClusterComponentModelPart<T, C> part = new ClusterComponentModelPart<T, C>(bp, getComponentMode());
      List<BusinessMapping> mappingsForCluster = bpToMappings.getBucketNotNull(bp.getId());
      Collections.sort(mappingsForCluster);
      part.initializeFrom(mappingsForCluster, bmiSorted);
      clusterParts.add(part);
    }
  }

  private List<BusinessMapping> getBusinessMappingsForSource(T sourceBB) {
    return SpringServiceFactory.getBusinessMappingService().getBusinessMappingsWithNoFunctions(sourceBB);
  }

  public void update() {
    for (ClusterComponentModelPart<T, C> part : clusterParts) {
      part.update(bmiSorted);
    }

    newMappingsPart.update(bmiSorted);

    if (action != null && ACTION_DELETE.equals(action)) {
      if (selectedClusterPosition != null && selectedClusterPosition.intValue() > 0) {
        int clusterIndex = selectedClusterPosition.intValue() - 1;
        ClusterComponentModelPart<T, C> selectedClusterPart = clusterParts.get(clusterIndex);

        if (selectedMappingPosition != null && selectedMappingPosition.intValue() > 0) {
          List<SingleBusinessMappingComponentModelPart> mappingParts = selectedClusterPart.getMappingParts();
          mappingParts.remove(selectedMappingPosition.intValue() - 1);
          if (mappingParts.isEmpty()) {
            clusterParts.remove(clusterIndex);
          }
        }
      }
      // reset variables necessary for deletion
      action = null;
      selectedClusterPosition = null;
      selectedMappingPosition = null;
    }
  }

  public void configure(T target) {
    if (target instanceof BusinessMappingEntity && !(target instanceof BusinessFunction)) {
      ((BusinessMappingEntity) target).removeBusinessMappings();
    }

    for (ClusterComponentModelPart<T, C> part : clusterParts) {
      part.configure(target);
    }
  }

  private void initializeBusinessMappingItems() {
    bmiSorted = new BusinessMappingItems(getAllInformationSystemRelsSorted(), getAllBusinessprocessesSorted(), getAllBusinessUnitsSorted(),
        getAllProductsSorted());
  }

  public List<InformationSystemRelease> getAllInformationSystemRelsSorted() {
    return getAllEntitiesSorted(SpringServiceFactory.getInformationSystemReleaseService());
  }

  public List<Product> getAllProductsSorted() {
    return getAllEntitiesSorted(SpringServiceFactory.getProductService());
  }

  public List<BusinessUnit> getAllBusinessUnitsSorted() {
    return getAllEntitiesSorted(SpringServiceFactory.getBusinessUnitService());
  }

  public List<BusinessProcess> getAllBusinessprocessesSorted() {
    return getAllEntitiesSorted(SpringServiceFactory.getBusinessProcessService());
  }

  private <E extends Entity> List<E> getAllEntitiesSorted(EntityService<E, ?> service) {
    List<E> elements = service.loadElementList();
    BuildingBlockSortHelper.sortByIdentityString(elements);
    return elements;
  }

  public List<ClusterComponentModelPart<T, C>> getClusterParts() {
    return clusterParts;
  }

  private ClusterComponentModelPart<T, C> getOrCreateClusterPart(C processUsedAsCluster) {
    final ClusterComponentModelPart<T, C> clusterPart = getExistingClusterPart(processUsedAsCluster);

    if (clusterPart == null) {
      final ClusterComponentModelPart<T, C> newClusterPart = new ClusterComponentModelPart<T, C>(processUsedAsCluster, getComponentMode());
      clusterParts.add(newClusterPart);
      return newClusterPart;
    }

    return clusterPart;
  }

  private ClusterComponentModelPart<T, C> getExistingClusterPart(C processUsedAsCluster) {
    for (ClusterComponentModelPart<T, C> part : clusterParts) {
      if (part.getClusteredByBuildingBlock().getId().equals(processUsedAsCluster.getId())) {
        return part;
      }
    }

    return null;
  }

  /**
   * Updates the business mapping model with new business mappings calculated by the cartesian
   * product of the given {@link BusinessProcess}, {@link BusinessUnit} and {@link Product} lists.
   */
  List<BusinessMapping> updateClusterPartsWithNewBusinessMapping(BusinessMappingItems bmi) {
    final BusinessMappingStrategy strategy = BusinessMappingStrategyFactory.getStrategyFor(source.getClass());

    strategy.validate(bmi);

    final List<BusinessMapping> businessMappings = strategy.createBusinessMappings(bmi);
    final List<BusinessMapping> existingMappings = this.findExistingBusinessMappings(businessMappings, strategy);
    final Iterable<BusinessMapping> newMappings = Iterables.filter(businessMappings, Predicates.not(Predicates.in(existingMappings)));

    for (BusinessMapping businessMapping : newMappings) {
      strategy.addOwningEntity(businessMapping, source);

      final SingleBusinessMappingComponentModelPart covPart = new SingleBusinessMappingComponentModelPart(businessMapping, getComponentMode());
      covPart.initializeFrom(this.bmiSorted);
      final C clusterElementFromMapping = clusterElementRetriever.getClusterElementFromMapping(businessMapping);
      final ClusterComponentModelPart<T, C> clusterPart = getOrCreateClusterPart(clusterElementFromMapping);
      clusterPart.addMappingPart(covPart);
    }

    if (Iterables.isEmpty(newMappings) && existingMappings.isEmpty()) {
      // the user tried to add topLevelElements only -> throw an exception
      throw new IteraplanBusinessException(IteraplanErrorMessages.CANNOT_ADD_INVALID_BUSINESS_MAPPINGS);
    }

    return existingMappings;
  }

  private List<BusinessMapping> findExistingBusinessMappings(List<BusinessMapping> businessMappings, BusinessMappingStrategy strategy) {
    final List<BusinessMapping> result = Lists.newArrayList();
    final List<BusinessMapping> existingMappings = getExistingMappings();

    for (BusinessMapping businessMapping : businessMappings) {
      if (strategy.doesMappingExist(existingMappings, businessMapping)) {
        result.add(businessMapping);
      }
    }

    return result;
  }

  private List<BusinessMapping> getExistingMappings() {
    List<BusinessMapping> existingMappings = Lists.newArrayList();

    for (ClusterComponentModelPart<T, C> part : this.clusterParts) {
      for (SingleBusinessMappingComponentModelPart singlePart : part.getMappingParts()) {
        existingMappings.add(singlePart.getMapping());
      }
    }

    return existingMappings;
  }

  public String getTableHeaderKey() {
    return tableHeaderKey;
  }

  public List<DisplayElements> getElementDisplayOrder() {
    return elementDisplayOrder;
  }

  public void setElementDisplayOrder(List<DisplayElements> elementDisplayOrder) {
    this.elementDisplayOrder = elementDisplayOrder;
  }

  public NewBusinessMappingComponentModelPart<T, C> getNewMappingsPart() {
    return newMappingsPart;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Integer getSelectedClusterPosition() {
    return selectedClusterPosition;
  }

  public void setSelectedClusterPosition(Integer selectedClusterPosition) {
    this.selectedClusterPosition = selectedClusterPosition;
  }

  public Integer getSelectedMappingPosition() {
    return selectedMappingPosition;
  }

  public void setSelectedMappingPosition(Integer selectedMappingPosition) {
    this.selectedMappingPosition = selectedMappingPosition;
  }

  public void validate(Errors errors) {
    // nothing to do (yet)
  }

  /**
   * @param <H>
   */
  public interface ClusterElementRetriever<H extends BuildingBlock> extends Serializable {
    H getClusterElementFromMapping(BusinessMapping mapping);
  }

  public static enum DisplayElements {
    INFORMATIONSSYSTEMRELEASE("mapping.informationSystemRelease", "informationSystemRelease.singular"), BUSINESSPROCESS("mapping.businessProcess",
        "businessProcess.singular"), BUSINESSUNIT("mapping.businessUnit", "businessUnit.singular"), PRODUCT("mapping.product", "global.product");

    private final String modelPath, elementTypeKey;

    private DisplayElements(String modelPath, String elementTypeKey) {
      this.modelPath = modelPath;
      this.elementTypeKey = elementTypeKey;
    }

    public String getModelPath() {
      return modelPath;
    }

    public String getElementTypeKey() {
      return elementTypeKey;
    }
  }
}
