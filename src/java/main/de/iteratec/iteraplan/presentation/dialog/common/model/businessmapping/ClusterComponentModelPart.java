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
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * @param <T>
 *          the building block type from which the business mapping is seen. Currently,
 *          {@link de.iteratec.iteraplan.model.InformationSystemRelease}, {@link de.iteratec.iteraplan.model.Product}, 
 *          {@link de.iteratec.iteraplan.model.BusinessProcess} and {@link de.iteratec.iteraplan.model.BusinessUnit} are accepted types.
 * @param <C>
 *          the building block type which is used for clustering the business mapping. Must be
 *          different from <code>T</code>! Currently, {@link de.iteratec.iteraplan.model.InformationSystemRelease},
 *          {@link de.iteratec.iteraplan.model.Product}, {@link de.iteratec.iteraplan.model.BusinessProcess} and 
 *          {@link de.iteratec.iteraplan.model.BusinessUnit} are accepted types.
 */
public class ClusterComponentModelPart<T extends BuildingBlock, C extends BuildingBlock> implements Serializable {

  /** Serialization version. */
  private static final long                             serialVersionUID = 7014977425314888485L;

  private ComponentMode                                 componentMode;

  private C                                             clusteredByBuildingBlock;

  private List<SingleBusinessMappingComponentModelPart> mappingParts     = Lists.newArrayList();

  public ClusterComponentModelPart(C clusterByBuildingBlock, ComponentMode componentMode) {
    this.componentMode = componentMode;
    this.clusteredByBuildingBlock = clusterByBuildingBlock;
  }

  public void initializeFrom(List<BusinessMapping> mappingsSorted, BusinessMappingItems bmiSorted) {
    for (BusinessMapping bizMapping : mappingsSorted) {
      final SingleBusinessMappingComponentModelPart part = new SingleBusinessMappingComponentModelPart(bizMapping, componentMode);
      part.initializeFrom(bmiSorted);
      mappingParts.add(part);
    }
  }

  public void update(BusinessMappingItems bmiAll) {
    for (SingleBusinessMappingComponentModelPart part : mappingParts) {
      part.update(bmiAll);
    }
  }

  /**
   * Configure a fresh target Information system release with the business mapping and attribute
   * value assignment information of this cluster.
   * 
   * @param target
   *          The fresh Information system release instance to configure.
   */
  public void configure(T target) {
    for (SingleBusinessMappingComponentModelPart part : mappingParts) {
      part.configure(target);
    }
  }

  public ComponentMode getComponentMode() {
    return componentMode;
  }

  public C getClusteredByBuildingBlock() {
    return clusteredByBuildingBlock;
  }

  public List<SingleBusinessMappingComponentModelPart> getMappingParts() {
    return mappingParts;
  }

  public void addMappingPart(SingleBusinessMappingComponentModelPart singleMappingPart) {
    mappingParts.add(singleMappingPart);
  }
}
