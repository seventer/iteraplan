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
package de.iteratec.iteraplan.presentation.dialog.Configuration;

import java.io.IOException;
import java.io.ObjectInputStream;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.RoutingDatasourceComponentModel;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


/**
 * Dialog Memory for the configuration page.
 */
public class ConfigurationDialogMemory extends DialogMemory {
  private static final long                         serialVersionUID = -5713781776278111932L;

  private boolean                                   showInactive     = IteraplanProperties.getBooleanProperty(IteraplanProperties.CONFIG_SHOW_INACTIVE);
  private boolean                                   doReset          = false;
  private boolean                                   purgeIndex       = false;

  private transient RoutingDatasourceComponentModel routingDatasourceModel;

  public ConfigurationDialogMemory() {
    init();
  }

  private void init() {
    routingDatasourceModel = new RoutingDatasourceComponentModel(ComponentMode.EDIT);
  }

  /**
   * @return Returns the showInactive.
   */
  public boolean isShowInactive() {
    return showInactive;
  }

  /**
   * @param showInactive
   *          The showInactive to set.
   */
  public void setShowInactive(boolean showInactive) {
    this.showInactive = showInactive;
  }

  public RoutingDatasourceComponentModel getRoutingDatasourceModel() {
    return routingDatasourceModel;
  }

  public void setSelectedDataSource(String dataSource) {
    this.routingDatasourceModel.setSelectedDataSource(dataSource);
  }

  public String getSelectedDataSource() {
    return routingDatasourceModel.getSelectedDataSource() == null ? Constants.MASTER_DATA_SOURCE : routingDatasourceModel.getSelectedDataSource();
  }

  public boolean isDoReset() {
    return doReset;
  }

  public void setDoReset(boolean doReset) {
    this.doReset = doReset;
  }

  public boolean isPurgeIndex() {
    return purgeIndex;
  }

  public void setPurgeIndex(boolean purgeIndex) {
    this.purgeIndex = purgeIndex;
  }

  /**
   * Implement readObject() to allow the deserialisation to init the transient field.
   * 
   * @throws ClassNotFoundException
   * @throws IOException
   */
  private void readObject(final ObjectInputStream is) throws IOException, ClassNotFoundException {
    if (is != null) {
      is.defaultReadObject();
    }
    init();
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + (doReset ? 1231 : 1237);
    result = prime * result + (showInactive ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ConfigurationDialogMemory other = (ConfigurationDialogMemory) obj;
    if (doReset != other.doReset) {
      return false;
    }
    if (showInactive != other.showInactive) {
      return false;
    }
    return true;
  }
}