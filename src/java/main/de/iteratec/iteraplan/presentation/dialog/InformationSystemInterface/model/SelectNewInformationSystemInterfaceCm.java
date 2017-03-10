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

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;


/**
 * GUI model for the creation of a new connection.
 */
public class SelectNewInformationSystemInterfaceCm extends AbstractComponentModelBase<InformationSystemInterface> {

  /** Serialization version. */
  private static final long              serialVersionUID = -8711986740349906146L;
  private List<InformationSystemRelease> releasesA        = new ArrayList<InformationSystemRelease>();
  private List<InformationSystemRelease> releasesB        = new ArrayList<InformationSystemRelease>();

  private Integer                        releaseAId;
  private InformationSystemRelease       releaseA;

  private Integer                        releaseBId;
  private InformationSystemRelease       releaseB;

  public SelectNewInformationSystemInterfaceCm(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
  }

  public InformationSystemRelease getReleaseA() {
    return releaseA;
  }

  /**
   * helper method to find the information system release with the given id, used from jsps when
   * being called from information systems
   * 
   * @return the isr corresponding to the specified id
   */
  public InformationSystemRelease getReleaseAFromId() {
    for (InformationSystemRelease isrA : this.releasesA) {
      if (isrA.getId().equals(releaseAId)) {
        return isrA;
      }
    }
    throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND);
  }

  public Integer getReleaseAId() {
    return releaseAId;
  }

  public void setReleaseAId(Integer partnerAId) {
    this.releaseAId = partnerAId;
  }

  public InformationSystemRelease getReleaseB() {
    return releaseB;
  }

  public Integer getReleaseBId() {
    return releaseBId;
  }

  public void setReleaseBId(Integer partnerBId) {
    this.releaseBId = partnerBId;
  }

  public List<InformationSystemRelease> getReleasesA() {
    return releasesA;
  }

  public List<InformationSystemRelease> getReleasesB() {
    return releasesB;
  }

  public void initializeFrom(InformationSystemInterface source) {
    boolean showInactive = UserContext.getCurrentUserContext().isShowInactiveStatus();
    releasesA = SpringServiceFactory.getInformationSystemReleaseService().getInformationSystemsFiltered(null, showInactive);
    releasesB = new ArrayList<InformationSystemRelease>(releasesA);

    if (!releasesA.isEmpty()) {
      if (source.getReferenceRelease() != null) {
        releaseA = source.getReferenceRelease();
      }
      else {
        releaseA = releasesA.get(0);
      }
      releaseAId = releaseA.getId();

      if (!releasesB.isEmpty() && source.getInformationSystemReleaseB() != null) {
        releaseB = source.getInformationSystemReleaseB();
      }
      else {
        releaseB = releasesB.get(0);
      }
      releaseBId = releaseB.getId();
    }
  }

  public void update() {
    // if no partner A has been selected yet, choose the first one in the list.
    if (!releasesA.isEmpty() && releaseAId == null) {
      releaseAId = releasesA.get(0).getId();
    }

    // if no partner B has been selected yet, choose the first one in the list.
    if (!releasesB.isEmpty() && releaseBId == null) {
      releaseBId = releasesB.get(0).getId();
    }

    // load the selected partners.
    InformationSystemReleaseService isrService = SpringServiceFactory.getInformationSystemReleaseService();
    releaseA = isrService.loadObjectById(releaseAId);
    releaseB = isrService.loadObjectById(releaseBId);
  }

  public void configure(InformationSystemInterface target) {
    if (target.getId() != null) {
      return;
    }

    target.setInformationSystemReleaseA(releaseA);
    target.setInformationSystemReleaseB(releaseB);
  }

  public void validate(Errors errors) {
    // nothing to do
  }
}
