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
package de.iteratec.iteraplan.presentation.dialog.SupportingQuery;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.Configuration;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


/**
 * This class represents the memory bean for all permission queries.
 */
public class PermissionQueriesDialogMemory extends DialogMemory {

  /** Serialization version. */
  private static final long   serialVersionUID = -4089691542163415538L;

  /** {@link #getConfigurations()} */
  private List<Configuration> configurations   = new ArrayList<Configuration>();

  /** {@link #getResults()} */
  private List<Result>        results          = new ArrayList<Result>();

  /** {@link #getSelectedPermissionQuery()} */
  private String              selectedPermissionQuery;

  /** {@link #getSelectedPermissionQueryNumber()} */
  private int                 selectedPermissionQueryNumber;

  /**
   * Returns the list of {@link Configuration}s.
   * 
   * @return See method description.
   */
  public List<Configuration> getConfigurations() {
    return configurations;
  }

  public void setConfigurations(List<Configuration> configurations) {
    this.configurations = configurations;
  }

  /**
   * Convenience method to use in JSP pages to retrieve the {@link Configuration} in the
   * {@link #configurations} list at the specified index.
   * 
   * @param index
   *          The index of the configuration in the list that is to be retrieved.
   * @return See method description.
   */
  public Configuration getConfiguration(String index) {
    return configurations.get(Integer.parseInt(index));
  }

  public void setConfiguration(String index, Configuration configuration) {
    this.configurations.set(Integer.parseInt(index), configuration);
  }

  /**
   * Returns the list of {@link Result}s.
   * 
   * @return See method description.
   */
  public List<Result> getResults() {
    return results;
  }

  public void setResults(List<Result> results) {
    this.results = results;
  }

  /**
   * Returns the name of the permission query that the user selected.
   * 
   * @return See method description.
   */
  public String getSelectedPermissionQuery() {
    return selectedPermissionQuery;
  }

  public void setSelectedPermissionQuery(String selectedPermissionQuery) {
    this.selectedPermissionQuery = selectedPermissionQuery;
  }

  /**
   * Returns the number of the permission query that the user selected.
   * 
   * @return See method description.
   */
  public int getSelectedPermissionQueryNumber() {
    return selectedPermissionQueryNumber;
  }

  public void setSelectedPermissionQueryNumber(int selectedPermissionQueryNumber) {
    this.selectedPermissionQueryNumber = selectedPermissionQueryNumber;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + ((configurations == null) ? 0 : configurations.hashCode());
    result = prime * result + ((results == null) ? 0 : results.hashCode());
    result = prime * result + ((selectedPermissionQuery == null) ? 0 : selectedPermissionQuery.hashCode());
    result = prime * result + selectedPermissionQueryNumber;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PermissionQueriesDialogMemory other = (PermissionQueriesDialogMemory) obj;
    if (configurations == null) {
      if (other.configurations != null) {
        return false;
      }
    }
    else if (!configurations.equals(other.configurations)) {
      return false;
    }
    if (results == null) {
      if (other.results != null) {
        return false;
      }
    }
    else if (!results.equals(other.results)) {
      return false;
    }
    if (selectedPermissionQuery == null) {
      if (other.selectedPermissionQuery != null) {
        return false;
      }
    }
    else if (!selectedPermissionQuery.equals(other.selectedPermissionQuery)) {
      return false;
    }
    if (selectedPermissionQueryNumber != other.selectedPermissionQueryNumber) {
      return false;
    }
    return true;
  }

}