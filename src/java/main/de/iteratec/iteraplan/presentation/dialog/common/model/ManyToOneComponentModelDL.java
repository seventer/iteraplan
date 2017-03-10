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
package de.iteratec.iteraplan.presentation.dialog.common.model;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.service.EntityService;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * A component model class serving the same purpose as {@link ManyAssociationSetComponentModel} to be used in cases where the list of available elements is
 * dynamically loaded with AJAX.
 * FIXME May be unnecessary with the decision whether or not to render the available elements in the initial request
 *       being made by the JSP (see OneAssociationComponentComboboxView.jsp)
 */
public abstract class ManyToOneComponentModelDL<F extends IdentityEntity, T extends Comparable<? super T> & Entity> extends
    ManyToOneComponentModel<F, T> {

  private static final long serialVersionUID = -2396619670536402360L;

  public ManyToOneComponentModelDL(ComponentMode componentMode, String htmlId, String labelKey, boolean nullable) {
    super(componentMode, htmlId, labelKey, nullable);
  }

  @Override
  protected List<T> getAvailableElements(Integer id) {
    return new ArrayList<T>();
  }

  @Override
  public List<T> getAvailableElementsPresentation() {
    return new ArrayList<T>();
  }

  @Override
  public void update() {
    Integer connectedElementId = getConnectedElementId();
    this.setConnectedElement(null);
    if (connectedElementId != null && connectedElementId.intValue() > 0) {
      T connectedElement = getService().loadObjectById(getConnectedElementId());
      this.setConnectedElement(connectedElement);
    }
  }

  @Override
  public boolean isDynamicallyLoaded() {
    return true;
  }

  protected abstract EntityService<T, Integer> getService();

  public abstract TypeOfBuildingBlock getTypeOfBuildingBlock();
}
