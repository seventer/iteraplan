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
package de.iteratec.iteraplan.presentation.dialog.ConsistencyCheck;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.Configuration;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Domain;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


/**
 * This class represents the memory bean for all consistency checks.
 */
public class ConsistencyCheckDialogMemory extends DialogMemory {

  /** Serialization version. */
  private static final long                    serialVersionUID = -1396925587344809970L;
  private List<Domain>                         domains          = new ArrayList<Domain>();
  private List<Domain>                         domainsResult    = new ArrayList<Domain>();
  private HashBucketMap<Domain, Configuration> configurations   = new HashBucketMap<Domain, Configuration>();
  private HashBucketMap<Domain, Result>        results          = new HashBucketMap<Domain, Result>();
  private String                               selectedConsistencyCheck;
  private int                                  selectedConsistencyCheckNumber;

  public List<Domain> getDomains() {
    return domains;
  }

  public void setDomain(List<Domain> domains) {
    this.domains = domains;
  }

  public List<Domain> getDomainsResult() {
    return domainsResult;
  }

  public void setDomainsResult(List<Domain> domainResult) {
    this.domainsResult = domainResult;
  }

  public HashBucketMap<Domain, Configuration> getConfigurations() {
    return configurations;
  }

  public void setConfigurations(HashBucketMap<Domain, Configuration> configurations) {
    this.configurations = configurations;
  }

  /**
   * Returns the {@link Configuration} for the given domain (the localized value of an Enum constant
   * specified in {@link Domain}) and index in the list of configurations for that domain. Needed as
   * accessor method for the JSP pages.
   * 
   * @param path
   *          The domain and index in the format "domain,index" for which the configuration shall be
   *          returned.
   * @return See method description.
   * @throws IllegalArgumentException
   *           If no Enum constant with the specified value exists.
   */
  public Configuration getConfiguration(String path) {
    // Split the path.
    String[] tokens = path.split(",");
    if (tokens.length < 2) {
      return null;
    }

    // Get the actual Domain object from the submitted string.
    Domain domain = Domain.getDomainByString(tokens[0]);
    if (configurations.containsKey(domain)) {
      List<Configuration> cfgl = configurations.get(domain);
      if (cfgl != null) {
        return cfgl.get(Integer.parseInt(tokens[1]));
      }
    }
    return null;
  }

  public List<Configuration> getConfigurationsForDomain(String domainString) {
    Domain domain = Domain.getDomainByString(domainString);
    if (configurations.containsKey(domain)) {
      return configurations.get(domain);
    }
    return null;
  }

  public HashBucketMap<Domain, Result> getResults() {
    return results;
  }

  public void setResults(HashBucketMap<Domain, Result> results) {
    this.results = results;
  }

  public String getSelectedConsistencyCheck() {
    return selectedConsistencyCheck;
  }

  public void setSelectedConsistencyCheck(String selectedConsistencyCheck) {
    this.selectedConsistencyCheck = selectedConsistencyCheck;
  }

  public int getSelectedConsistencyCheckNumber() {
    return selectedConsistencyCheckNumber;
  }

  public void setSelectedConsistencyCheckNumber(int selectedConsistencyCheckNumber) {
    this.selectedConsistencyCheckNumber = selectedConsistencyCheckNumber;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + ((domains == null) ? 0 : domains.hashCode());
    result = prime * result + ((domainsResult == null) ? 0 : domainsResult.hashCode());
    result = prime * result + ((selectedConsistencyCheck == null) ? 0 : selectedConsistencyCheck.hashCode());
    result = prime * result + selectedConsistencyCheckNumber;
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
    final ConsistencyCheckDialogMemory other = (ConsistencyCheckDialogMemory) obj;
    if (domains == null) {
      if (other.domains != null) {
        return false;
      }
    }
    else if (!domains.equals(other.domains)) {
      return false;
    }
    if (domainsResult == null) {
      if (other.domainsResult != null) {
        return false;
      }
    }
    else if (!domainsResult.equals(other.domainsResult)) {
      return false;
    }
    if (selectedConsistencyCheck == null) {
      if (other.selectedConsistencyCheck != null) {
        return false;
      }
    }
    else if (!selectedConsistencyCheck.equals(other.selectedConsistencyCheck)) {
      return false;
    }
    if (selectedConsistencyCheckNumber != other.selectedConsistencyCheckNumber) {
      return false;
    }
    return true;
  }

}