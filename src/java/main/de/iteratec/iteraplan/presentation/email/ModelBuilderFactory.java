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
package de.iteratec.iteraplan.presentation.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.service.diffs.BBChangesetFactory;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.GuiContext;


public final class ModelBuilderFactory {

  private ModelBuilderFactory() {
    // Nothing to comment
  }

  private abstract static class ModelBuilderInstantiator {
    public abstract AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri);
  }

  private static Map<TypeOfBuildingBlock, ModelBuilderInstantiator> instantiators = new HashMap<TypeOfBuildingBlock, ModelBuilderInstantiator>();

  static {
    instantiators.put(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new ArchitecturalDomainModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.BUSINESSDOMAIN, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new BusinessDomainModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.BUSINESSFUNCTION, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new BusinessFunctionModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.BUSINESSOBJECT, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new BusinessObjectModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.BUSINESSPROCESS, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new BusinessProcessModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.BUSINESSUNIT, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new BusinessUnitModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new TechnicalComponentReleaseModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new InfrastructureElementModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new InterfaceModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new InformationSystemDomainModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new InformationSystemReleaseModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.PROJECT, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new ProjectModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
    instantiators.put(TypeOfBuildingBlock.PRODUCT, new ModelBuilderInstantiator() {
      @Override
      public AbstractModelBuilder createModelBuilder(BuildingBlock newBB, HistoryBBChangeset changeset, String applicationBaseUri) {
        return new ProductModelBuilder(newBB, changeset, applicationBaseUri);
      }
    });
  }

  public static AbstractModelBuilder createModelBuilder(BuildingBlock newBB, BuildingBlock oldBB, List<String> changedTimeseriesNames) {
    AbstractModelBuilder modelBuilder = null;
    if (instantiators.containsKey(newBB.getTypeOfBuildingBlock())) {
      ModelBuilderInstantiator instantiator = instantiators.get(newBB.getTypeOfBuildingBlock());
      HistoryBBChangeset changeset = BBChangesetFactory.createChangeset(oldBB, newBB, null, null);
      changeset.setChangedTimeseriesNames(changedTimeseriesNames);

      HttpServletRequest req = GuiContext.getCurrentRequest();
      String applicationBaseUri = (req != null ? URLBuilder.getApplicationURL(req) : "");

      modelBuilder = instantiator.createModelBuilder(newBB, changeset, applicationBaseUri);
    }
    return modelBuilder;
  }

}
