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
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.user.DataSource;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model for managing a {@link User}'s datasource.
 */
public class RoutingDatasourceComponentModel extends AbstractComponentModelBase<User> {

  /** Serialization version. */
  private static final long serialVersionUID = 34932718844050712L;
  private String            selectedDataSource;

  public RoutingDatasourceComponentModel(ComponentMode componentMode) {
    super(componentMode);
  }

  public String getSelectedDataSource() {
    return selectedDataSource;
  }

  public List<DataSource> getAvailableDataSources() {
    List<DataSource> availableDataSources;
    List<DataSource> list = new ArrayList<DataSource>();

    DataSource ds = new DataSource();
    ds.setKey(Constants.MASTER_DATA_SOURCE);

    list.add(ds);
    list.addAll(SpringServiceFactory.getDataSourceService().loadDataSources());

    availableDataSources = Collections.unmodifiableList(list);
    return availableDataSources;
  }

  public void setSelectedDataSource(String selectedDataSource) {
    this.selectedDataSource = selectedDataSource;
  }

  public void initializeFrom(User source) {
    this.selectedDataSource = source.getDataSource();
  }

  public void update() {
    throw new UnsupportedOperationException();
  }

  public void configure(User target) {

    if (StringUtils.isEmpty(selectedDataSource)) {
      throw new IllegalStateException("Must not select an empty data source");
    }

    target.setDataSource(selectedDataSource);
  }

  public void validate(Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedDataSource", "NAME_CANNOT_BE_EMPTY");
  }

}