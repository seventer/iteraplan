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
package de.iteratec.iteraplan.businesslogic.exchange.common.masterplan.getters;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


public final class RelatedISRGetter extends AbstractMasterplanRelatedElementsGetter implements IMasterplanRelatedElementsGetter {

  private static final IMasterplanRelatedElementsGetter INSTANCE = new RelatedISRGetter(
                                                                     Lists.newArrayList(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL));

  public static IMasterplanRelatedElementsGetter getInstance() {
    return INSTANCE;
  }

  private RelatedISRGetter(List<String> supported) {
    super(supported);
  }

  @Override
  protected Set<? extends BuildingBlock> getRelatedElements(BuildingBlock bb) {
    if (bb instanceof Project) {
      return Sets.newHashSet(((Project) bb).getInformationSystemReleases());
    }
    else if (bb instanceof InformationSystemDomain) {
      return Sets.newHashSet(((InformationSystemDomain) bb).getInformationSystemReleases());
    }
    else if (bb instanceof InfrastructureElement) {
      return Sets.newHashSet(((InfrastructureElement) bb).getInformationSystemReleases());
    }
    else if (bb instanceof TechnicalComponentRelease) {
      return Sets.newHashSet(((TechnicalComponentRelease) bb).getInformationSystemReleases());
    }
    else if (bb instanceof BusinessObject) {
      return Sets.newHashSet(((BusinessObject) bb).getInformationSystemReleases());
    }
    else if (bb instanceof BusinessFunction) {
      return Sets.newHashSet(((BusinessFunction) bb).getInformationSystems());
    }
    else if (bb instanceof Product) {
      Set<BuildingBlock> result = Sets.newHashSet();
      for (BusinessMapping bm : ((Product) bb).getBusinessMappings()) {
        result.add(bm.getInformationSystemRelease());
      }
      return result;
    }
    else if (bb instanceof BusinessProcess) {
      Set<BuildingBlock> result = Sets.newHashSet();
      for (BusinessMapping bm : ((BusinessProcess) bb).getBusinessMappings()) {
        result.add(bm.getInformationSystemRelease());
      }
      return result;
    }
    else if (bb instanceof BusinessUnit) {
      Set<BuildingBlock> result = Sets.newHashSet();
      for (BusinessMapping bm : ((BusinessUnit) bb).getBusinessMappings()) {
        result.add(bm.getInformationSystemRelease());
      }
      return result;
    }

    return null;
  }

}
