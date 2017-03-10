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
 * A component model class serving the same purpose as {@link ManyAssociationListComponentModel} to be used in cases where the list of available elements is
 * dynamically loaded with AJAX.
 * FIXME May be unnecessary with the decision whether or not to render the available elements in the initial request
 *       being made by the JSP (see ManyAssociationListComponentView.jsp)
 */
public abstract class ManyAssociationListComponentModelDL<F extends IdentityEntity, T extends Comparable<? super T> & Entity> extends
    ManyAssociationListComponentModel<F, T> {

  private static final long serialVersionUID = -154701677088152501L;

  public ManyAssociationListComponentModelDL(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
      String[] connectedElementsFields, String availableElementsLabel, T dummyForPresentation) {
    super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
  }

  @Override
  protected List<T> getAvailableElements(F source, List<T> connected) {
    return new ArrayList<T>();
  }

  @Override
  protected List<T> getAvailableElements() {
    return new ArrayList<T>();
  }

  @Override
  public List<T> getAvailableElementsPresentation() {
    List<T> result = super.getAvailableElementsPresentation();
    result.remove(dummyForPresentation);
    return result;
  }

  @Override
  protected void processElementIdToAdd() {
    Integer elementToAddId = getElementIdToAdd();
    if (elementToAddId != null && elementToAddId.intValue() > 0) {
      T child = getService().loadObjectById(getElementIdToAdd());
      if (!getConnectedElements().contains(child)) {
        this.getConnectedElements().add(child);
      }
    }
  }

  @Override
  public boolean isDynamicallyLoaded() {
    return true;
  }

  protected abstract EntityService<T, Integer> getService();

  public abstract TypeOfBuildingBlock getTypeOfBuildingBlock();
}
