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
package de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.presentation.memory.ColumnDefinition;
import de.iteratec.iteraplan.presentation.memory.SearchDialogMemory;


public class InformationSystemInterfaceDialogMemory extends SearchDialogMemory {

  /** Serialization version. */
  private static final long serialVersionUID = -804007398861962229L;
  private String            informationSystemRelease;
  private String            description;

  @Override
  public List<Criterion> getCriteria() {
    List<Criterion> isiList = new ArrayList<Criterion>();
    isiList.add(new Criterion("fullTextSearchValue", "interface.search.label.fullTextSearchValueField",
        "interface.search.hint.fullTextSearchValueField"));
    return isiList;
  }

  @Override
  public List<ColumnDefinition> getInitialColumnDefinitions() {

    List<ColumnDefinition> props = new ArrayList<ColumnDefinition>();
    props.add(new ColumnDefinition("global.name", "name", "", true));
    props.add(new ColumnDefinition("interface.releaseA", "informationSystemReleaseA", "", true));
    props.add(new ColumnDefinition("global.direction", "direction", "", true));
    props.add(new ColumnDefinition("interface.releaseB", "informationSystemReleaseB", "", true));
    props.add(new ColumnDefinition("global.description", "description", "", true));
    return props;
  }

  public InformationSystemInterface toInformationSystemInterface() {
    InformationSystem isA = new InformationSystem();
    InformationSystem isB = new InformationSystem();
    InformationSystemRelease isrA = new InformationSystemRelease();
    InformationSystemRelease isrB = new InformationSystemRelease();
    InformationSystemInterface isi = new InformationSystemInterface();
    isA.setName(this.getInformationSystemRelease());
    isB.setName(this.getInformationSystemRelease());
    isrA.setInformationSystem(isA);
    isrB.setInformationSystem(isB);
    isi.setInformationSystemReleaseA(isrA);
    isi.setInformationSystemReleaseB(isrB);
    return isi;
  }

  public String getInformationSystemRelease() {
    return informationSystemRelease;
  }

  public void setInformationSystemRelease(String informationSystemReleaseA) {
    this.informationSystemRelease = informationSystemReleaseA;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**{@inheritDoc}**/
  @Override
  public String getIconCss() {
    return "icon_interface";
  }

  @Override
  public int hashCode() {
    return calculateHashCode(31, super.hashCode(), description, informationSystemRelease);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    InformationSystemInterfaceDialogMemory other = (InformationSystemInterfaceDialogMemory) obj;
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    }
    else if (!description.equals(other.description)) {
      return false;
    }
    if (informationSystemRelease == null) {
      if (other.informationSystemRelease != null) {
        return false;
      }
    }
    else if (!informationSystemRelease.equals(other.informationSystemRelease)) {
      return false;
    }
    return true;
  }

}
