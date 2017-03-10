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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.common.BuildingBlockRelationMapping;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Cluster.ClusterOptionsBean;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


public class ClusterDiagramMapping extends BuildingBlockRelationMapping {
  private final ClusterOptionsBean clusterOptions;

  public ClusterDiagramMapping(ClusterOptionsBean clusterOptionsBean, BuildingBlock buildingBlock) {
    super(buildingBlock);
    this.clusterOptions = clusterOptionsBean;
  }

  @Override
  public Map<String, Set<? extends IdentityEntity>> getMapping() {
    Map<String, Set<? extends IdentityEntity>> map = super.getMapping();

    addAttributes(map);
    return map;
  }

  private void addAttributes(Map<String, Set<? extends IdentityEntity>> map) {
    HashBucketMap<AttributeType, AttributeValue> assignments = getBuildingBlock().getAttributeTypeToAttributeValues();

    for (AttributeType at : assignments.keySet()) {
      if (clusterOptions.getSecondOrderBean(at) != null && clusterOptions.getSecondOrderBean(at).isSelected()) {

        List<AttributeValue> values = assignments.getBucketNotNull(at);
        map.put(at.getName(), new HashSet<AttributeValue>(values));
      }
    }
  }

  /**
   * mappings only are to be added, if the secondOrderBean of the corresponding bb-Type is selected
   * @return true if the secondOrderBean referenced by {@code tobbString} is selected
   */
  @Override
  protected boolean isToBeAdded(String tobbString) {
    return clusterOptions.getSecondOrderBean(tobbString) != null && clusterOptions.getSecondOrderBean(tobbString).isSelected();
  }

}
