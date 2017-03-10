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
package de.iteratec.iteraplan.presentation.dialog.InformationSystemInterface.model;

import java.util.List;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;


/**
 * GUI model for the selection of a {@link InformationSystemInterface} to read or edit.
 */
public class SelectInformationSystemInterfaceCm extends AbstractComponentModelBase<InformationSystemInterface> {

  /** Serialization version. */
  private static final long              serialVersionUID = 291793851931666662L;

  /**
   * A list of information systems that have connections to other information systems. Used for the
   * first drop down list. The releases in this list represent the connection partner A within a
   * connection.
   */
  private List<InformationSystemRelease> releasesA;

  /**
   * The currently selected information system release.
   */
  private InformationSystemRelease       releaseA;

  /**
   * The currently selected information system release's ID.
   */
  private Integer                        releaseAId;

  /**
   * The information system release that corresponds to the selected entry in the
   * {@link #informationSystemInterfaces} list.
   */
  private InformationSystemRelease       releaseB;

  /** The id of the chosen connection. Used for user input. */
  private Integer                        id               = null;

  /**
   * A list of {@link NamedId}s that holds information to be displayed in the second drop down
   * list, i.e. the list of connection partners B. The 'id' fields contain the ID's of the
   * connections, the 'name' fields contain the labels for the drop down list and the 'description'
   * field contains the description of the connected information system.
   */
  private List<NamedId>                  informationSystemInterfaces;

  public SelectInformationSystemInterfaceCm(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public List<NamedId> getInformationSystemInterfaces() {
    return informationSystemInterfaces;
  }

  public InformationSystemRelease getReleaseA() {
    return releaseA;
  }

  public void setReleaseA(InformationSystemRelease releaseA) {
    this.releaseA = releaseA;
  }

  public Integer getReleaseAId() {
    return releaseAId;
  }

  public void setReleaseAId(Integer id) {
    this.releaseAId = id;
  }

  public InformationSystemRelease getReleaseB() {
    return releaseB;
  }

  public void setReleaseB(InformationSystemRelease releaseB) {
    this.releaseB = releaseB;
  }

  public List<InformationSystemRelease> getReleasesA() {
    return releasesA;
  }

  /**
   * @return the name of the interface, comprised of the name of both information systems
   */
  public String getName() {
    return releaseA.getName() + " <-> " + releaseB.getName();
  }

  public void initializeFrom(InformationSystemInterface source) {
    if (source.getId() == null) {
      return;
    }

    id = source.getId();

    if (source.getReferenceRelease() != null) {
      releaseA = source.getReferenceRelease();
      releaseAId = releaseA.getId();
      releaseB = source.getOtherRelease();
    }
    else {
      releaseA = source.getInformationSystemReleaseA();
      releaseAId = releaseA.getId();
      releaseB = source.getInformationSystemReleaseB();
    }

    InformationSystemInterfaceService service = SpringServiceFactory.getInformationSystemInterfaceService();
    boolean showInactive = UserContext.getCurrentUserContext().isShowInactiveStatus();

    releasesA = service.getInformationSystemReleasesWithConnections(releaseAId, showInactive);
    informationSystemInterfaces = service.getNamedIdsForConnectionsOfInformationSystemRelease(releaseAId);
  }

  public void update() {
    if (getComponentMode() != ComponentMode.READ || id == null) {
      return;
    }

    InformationSystemInterfaceService service = SpringServiceFactory.getInformationSystemInterfaceService();
    boolean showInactive = UserContext.getCurrentUserContext().isShowInactiveStatus();

    InformationSystemInterface connection = service.loadObjectById(id);
    releaseA = SpringServiceFactory.getInformationSystemReleaseService().loadObjectById(releaseAId);
    releaseB = connection.getInformationSystemReleaseA().getId().equals(releaseAId) ? connection.getInformationSystemReleaseB() : connection
        .getInformationSystemReleaseA();
    releasesA = service.getInformationSystemReleasesWithConnections(releaseAId, showInactive);
    informationSystemInterfaces = service.getNamedIdsForConnectionsOfInformationSystemRelease(releaseAId);
  }

  public void validate(Errors errors) {
    //nothing to do
  }

  public void configure(InformationSystemInterface target) {
    InformationSystemReleaseService isrService = SpringServiceFactory.getInformationSystemReleaseService();
    InformationSystemRelease releaseAReloaded = isrService.loadObjectById(releaseA.getId());
    InformationSystemRelease releaseBReloaded = isrService.loadObjectById(releaseB.getId());
    
    target.setInformationSystemReleaseA(releaseAReloaded);
    target.setInformationSystemReleaseB(releaseBReloaded);
  }

}