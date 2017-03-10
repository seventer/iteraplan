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
package de.iteratec.iteraplan.presentation.dialog.AttributeTypeGroup;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;


/**
 * Component Model for selecting and sorting AttributeTypeGroups. Important: This component model is
 * not designed to be updated more than once!
 */
public class AttributeTypeGoupSelectionComponentModel extends AbstractComponentModelBase<AttributeTypeGroup> {

  /** Serialization version. */
  private static final long        serialVersionUID = -8765253824923647321L;
  private Integer                  currentId;
  private List<AttributeTypeGroup> available;
  private Integer                  position;
  // kvo: wird zwar nur in einer Methode verwendet, speichert aber den Wert zwischen den Aufrufen
  private boolean                  wasUpdated       = false;
  private Movement                 movement;

  /** The GUI action to carry out within update(). */
  private String                   action;

  private enum Movement {
    moveTop, moveUp, moveDown, moveBottom;
  }

  public AttributeTypeGoupSelectionComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
  }

  private List<AttributeTypeGroup> getAvailableElements() {
    return SpringServiceFactory.getAttributeTypeGroupService().loadElementList("position");
  }

  public void initializeFrom(AttributeTypeGroup source) {
    this.currentId = source.getId();
    this.available = getAvailableElements();
    if (this.currentId != null) {
      AttributeTypeGroup current = SpringServiceFactory.getAttributeTypeGroupService().loadObjectById(this.currentId);
      if (current != null) {
        this.position = current.getPosition();
      }
    }
  }

  public void update() {
    if (wasUpdated) {
      throw new IllegalStateException("This component model can only be updated once!");
    }
    if (getComponentMode() != ComponentMode.READ || currentId == null) {
      return;
    }
    this.available = getAvailableElements();

    AttributeTypeGroup current = SpringServiceFactory.getAttributeTypeGroupService().loadObjectById(this.currentId);
    int currentIndex = available.indexOf(current);
    int maxIndex = this.available.size() - 1;
    if (!StringUtils.isEmpty(this.getAction())) {
      this.setMovementAsString(this.action);
      switch (getMovement()) {
        case moveUp:
          if (currentIndex > 0) {
            AttributeTypeGroup atgToMove = available.get(currentIndex - 1);
            available.remove(currentIndex - 1);
            this.position = atgToMove.getPosition();
          }
          break;
        case moveTop:
          if (currentIndex > 0) {
            AttributeTypeGroup atgToMove = available.get(0);
            this.position = atgToMove.getPosition();
          }
          break;
        case moveDown:
          if (currentIndex < maxIndex) {
            AttributeTypeGroup atgToMove = available.get(currentIndex + 1);
            this.position = atgToMove.getPosition();
          }
          break;
        case moveBottom:
          if (currentIndex < maxIndex) {
            AttributeTypeGroup atgToMove = available.get(maxIndex);
            this.position = atgToMove.getPosition();
          }
          break;
        default:
          break;
      }
    }
    wasUpdated = true;
  }

  public void configure(AttributeTypeGroup target) {
    target.setPosition(this.position);
  }

  public void validate(Errors errors) {
    // do nothing
  }

  public List<AttributeTypeGroup> getAvailable() {
    return available;
  }

  public Integer getCurrentId() {
    return currentId;
  }

  public void setCurrentId(Integer currentId) {
    this.currentId = currentId;
  }

  public Movement getMovement() {
    return movement;
  }

  public void setMovementAsString(String movementAsString) {
    this.movement = Movement.valueOf(movementAsString);
  }

  public Integer getPosition() {
    return position;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
