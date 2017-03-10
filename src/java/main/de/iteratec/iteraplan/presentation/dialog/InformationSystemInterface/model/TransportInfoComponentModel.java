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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.TransportInfo;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;


public class TransportInfoComponentModel extends AbstractComponentModelBase<InformationSystemInterface> implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = -3427079844056832996L;
  private TransportInfo     transportInfo;
  private String            currentlySelectedDirection;
  private String            currentlySelectedGraphicDirection;

  public TransportInfoComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
  }

  public void initializeFrom(InformationSystemInterface source) {
    if (source.getInterfaceDirection() == null) {
      transportInfo = TransportInfo.NO_DIRECTION;
      currentlySelectedDirection = Constants.NO_DIRECTION;
      currentlySelectedGraphicDirection = TransportInfo.NO_DIRECTION.getTextRepresentation();
      return;
    }
    Direction direction = source.getInterfaceDirection();
    if (Direction.BOTH_DIRECTIONS.equals(direction)) {
      transportInfo = TransportInfo.BOTH_DIRECTIONS;
      currentlySelectedDirection = Constants.BOTH_DIRECTIONS;
      currentlySelectedGraphicDirection = TransportInfo.BOTH_DIRECTIONS.getTextRepresentation();
    }
    else if (Direction.FIRST_TO_SECOND.equals(direction)) {
      transportInfo = TransportInfo.FIRST_TO_SECOND;
      currentlySelectedDirection = Constants.FIRST_TO_SECOND;
      currentlySelectedGraphicDirection = TransportInfo.FIRST_TO_SECOND.getTextRepresentation();
    }
    else if (Direction.SECOND_TO_FIRST.equals(direction)) {
      transportInfo = TransportInfo.SECOND_TO_FIRST;
      currentlySelectedDirection = Constants.SECOND_TO_FIRST;
      currentlySelectedGraphicDirection = TransportInfo.SECOND_TO_FIRST.getTextRepresentation();
    }
    else {
      transportInfo = TransportInfo.NO_DIRECTION;
      currentlySelectedDirection = Constants.NO_DIRECTION;
      currentlySelectedGraphicDirection = TransportInfo.NO_DIRECTION.getTextRepresentation();
    }
  }

  public void update() {
    if (currentlySelectedDirection != null) {
    if (currentlySelectedDirection.equals(Constants.BOTH_DIRECTIONS)) {
      transportInfo = TransportInfo.BOTH_DIRECTIONS;
    }
    else if (currentlySelectedDirection.equals(Constants.FIRST_TO_SECOND)) {
      transportInfo = TransportInfo.FIRST_TO_SECOND;
    }
    else if (currentlySelectedDirection.equals(Constants.SECOND_TO_FIRST)) {
      transportInfo = TransportInfo.SECOND_TO_FIRST;
    }
    else {
      transportInfo = TransportInfo.NO_DIRECTION;
    }
    } else {
      transportInfo = TransportInfo.NO_DIRECTION;
    }
  }

  /**
   * used for setting the direction of the interface with the value from mass update
   * @param directionToSet
   */
  public void update(String directionToSet) {
    if (directionToSet != null) {
      if (directionToSet.equals(TransportInfo.BOTH_DIRECTIONS.getTextRepresentation())) {
        currentlySelectedDirection = Constants.BOTH_DIRECTIONS;
        transportInfo = TransportInfo.BOTH_DIRECTIONS;
      }
      else if (directionToSet.equals(TransportInfo.FIRST_TO_SECOND.getTextRepresentation())) {
        currentlySelectedDirection = Constants.FIRST_TO_SECOND;
        transportInfo = TransportInfo.FIRST_TO_SECOND;
      }
      else if (directionToSet.equals(TransportInfo.SECOND_TO_FIRST.getTextRepresentation())) {
        currentlySelectedDirection = Constants.SECOND_TO_FIRST;
        transportInfo = TransportInfo.SECOND_TO_FIRST;
      }
      else {
        currentlySelectedDirection = Constants.NO_DIRECTION;
        transportInfo = TransportInfo.NO_DIRECTION;
      }
    }
    else {
      currentlySelectedDirection = Constants.NO_DIRECTION;
      transportInfo = TransportInfo.NO_DIRECTION;
    }
  }

  public void configure(InformationSystemInterface target) {
    switch (transportInfo) {
      case BOTH_DIRECTIONS:
        target.setInterfaceDirection(Direction.BOTH_DIRECTIONS);
        break;
      case FIRST_TO_SECOND:
        target.setInterfaceDirection(Direction.FIRST_TO_SECOND);
        break;
      case SECOND_TO_FIRST:
        target.setInterfaceDirection(Direction.SECOND_TO_FIRST);
        break;
      default:
        target.setInterfaceDirection(Direction.NO_DIRECTION);
        break;
    }

  }

  public void validate(Errors errors) {
    // nothing to do
  }

  public List<NamedId> getAvailableTransportDirections() {
    List<NamedId> atd = new ArrayList<NamedId>();
    atd.add(new NamedId(null, TransportInfo.NO_DIRECTION.getTextRepresentation(), TransportInfo.NO_DIRECTION.getGraphicRepresentation()));
    atd.add(new NamedId(null, TransportInfo.FIRST_TO_SECOND.getTextRepresentation(), TransportInfo.FIRST_TO_SECOND.getGraphicRepresentation()));
    atd.add(new NamedId(null, TransportInfo.SECOND_TO_FIRST.getTextRepresentation(), TransportInfo.SECOND_TO_FIRST.getGraphicRepresentation()));
    atd.add(new NamedId(null, TransportInfo.BOTH_DIRECTIONS.getTextRepresentation(), TransportInfo.BOTH_DIRECTIONS.getGraphicRepresentation()));
    return atd;
  }

  public TransportInfo getTransportInfo() {
    return transportInfo;
  }

  public String getCurrentlySelectedDirection() {
    return currentlySelectedDirection;
  }

  public void setCurrentlySelectedDirection(String currentlySelectedDirection) {
    this.currentlySelectedDirection = currentlySelectedDirection;
  }

  public String getCurrentlySelectedGraphicDirection() {
    return currentlySelectedGraphicDirection;
  }

  public void setCurrentlySelectedGraphicDirection(String currentlySelectedGraphicDirection) {
    this.currentlySelectedGraphicDirection = currentlySelectedGraphicDirection;
  }

}
