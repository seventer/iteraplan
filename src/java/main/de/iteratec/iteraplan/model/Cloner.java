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
package de.iteratec.iteraplan.model;

import java.util.HashMap;
import java.util.Map;


public class Cloner {

  private static Map<TypeOfBuildingBlock, Cloner> cloners = new HashMap<TypeOfBuildingBlock, Cloner>();

  static {
    cloners.put(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((ArchitecturalDomain) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.BUSINESSDOMAIN, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((BusinessDomain) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.BUSINESSFUNCTION, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((BusinessFunction) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.BUSINESSOBJECT, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((BusinessObject) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.BUSINESSPROCESS, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((BusinessProcess) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.BUSINESSMAPPING, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((BusinessMapping) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.BUSINESSUNIT, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((BusinessUnit) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((TechnicalComponentRelease) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((InfrastructureElement) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((InformationSystemInterface) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((InformationSystemDomain) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((InformationSystemRelease) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.PROJECT, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((Project) bb);
      }
    });
    cloners.put(TypeOfBuildingBlock.PRODUCT, new Cloner() {
      @Override
      public BuildingBlock clone(BuildingBlock bb) {
        return BuildingBlockUtil.clone((Product) bb);
      }
    });
  }

  /**
   * Clones the passed Building Block <code>bb</code> and returns the clone. If no cloning implementation
   * for bb's {@link TypeOfBuildingBlock} exists, it will return <code>null</code>.
   * @param bb The building block to be cloned.
   * @return The clone, or <code>null</code>.
   */
  public BuildingBlock clone(BuildingBlock bb) {
    if (bb == null) {
      return null;
    }
    Cloner cloner = cloners.get(bb.getTypeOfBuildingBlock());
    if (cloner != null) {
      return cloner.clone(bb);
    }

    return null;
  }

}
