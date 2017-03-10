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
package de.iteratec.iteraplan.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.model.Sequence;


public class ReleaseSuccessorDTO<T extends Sequence<T>> implements Serializable {

  private static final long serialVersionUID = 9021462465405750689L;

  /**
   * List of available Releases to select from.
   */
  private List<T>                        availableReleases = new ArrayList<T>();

  /**
   * Flag to indicate whether this DTO is returned as the result of a query.
   */
  private boolean                        noQueryExecuted   = false;

  private boolean                        showSuccessor     = true;

  /**
   * List of {@link SuccessionContainer}s. Each element contains an {@link de.iteratec.iteraplan.model.InformationSystemRelease}
   * and its level in the succession of the release for which the report was requested. Level
   * information is needed for indentation.
   */
  private List<SuccessionContainer<T>>   succession        = new ArrayList<SuccessionContainer<T>>();

  /** The user-selected result format. */
  private String                                         selectedResultFormat;

  public List<T> getAvailableReleases() {
    return availableReleases;
  }

  public List<SuccessionContainer<T>> getSuccession() {
    return succession;
  }

  public boolean isNoQueryExecuted() {
    return noQueryExecuted;
  }

  public void setAvailableReleases(List<T> availableReleases) {
    this.availableReleases = availableReleases;
  }

  public void setNoQueryExecuted(boolean noQueryExcuted) {
    this.noQueryExecuted = noQueryExcuted;
  }

  public void setSuccession(List<SuccessionContainer<T>> succession) {
    this.succession = succession;
  }

  public boolean isShowSuccessor() {
    return showSuccessor;
  }

  public void setShowSuccessor(boolean showSuccessor) {
    this.showSuccessor = showSuccessor;
  }

  public String getSelectedResultFormat() {
    return selectedResultFormat;
  }

  public void setSelectedResultFormat(String selectedResultFormat) {
    this.selectedResultFormat = selectedResultFormat;
  }

  /**
   * This class represents a data container for succession information of a particular information
   * system release. It stores the release as well as its level in the succession.
   */
  public static class SuccessionContainer<T extends Sequence<T>> implements Serializable {

    private static final long serialVersionUID = 7056071600325421017L;

    private T   release;
    private int level;

    public T getRelease() {
      return release;
    }

    public void setRelease(T release) {
      this.release = release;
    }

    public int getLevel() {
      return level;
    }

    public void setLevel(int level) {
      this.level = level;
    }

  }

}