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
package de.iteratec.iteraplan.businesslogic.reports.query.postprocessing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.model.BuildingBlock;


/**
 * Abstract base class for post-processing strategies. A post-processing strategy is primarily used
 * for queries. A strategy specifies an action that is to be performed on the result set of a query.
 */
public abstract class AbstractPostprocessingStrategy<T extends BuildingBlock> implements Comparable<AbstractPostprocessingStrategy<T>>, Serializable {

  private final String   presentationKey;
  private final Integer  executionNumber;
  private final String[] reportTypes;

  /**
   * Constructor.
   * 
   * @param presentationKey
   *          The resource bundle key that is used to internationalize the name of this strategy.
   * @param executionNumber
   *          The execution number of this strategy.
   * @param exportFormats
   *          The exports formats that are supported by this strategy.
   */
  AbstractPostprocessingStrategy(String presentationKey, Integer executionNumber, String[] reportTypes) {
    this.presentationKey = presentationKey;
    this.executionNumber = executionNumber;
    this.reportTypes = (reportTypes == null) ? null : reportTypes.clone();
  }

  /**
   * Returns all additional options of the strategy. This default implementation returns an empty
   * list. If additional options shall be provided, concrete subclasses must overwrite this method.
   * 
   * @return The list of additional options.
   */
  public List<OptionConsiderStateAndDate> getAdditionalOptions() {
    return new ArrayList<OptionConsiderStateAndDate>();
  }

  /**
   * Returns the execution number of this post-processing strategy. The execution number defines a
   * strategy's place among all post-processing strategies. This number is extremely important,
   * since more than one strategy might be executed during a query. The processing done by one
   * strategy might affect the strategies coming afterwards.
   * 
   * @return The execution number of this strategy.
   */
  public Integer getExecutionNumber() {
    return executionNumber;
  }

  /**
   * Returns the resource bundle key that is used to internationalize the name of this strategy.
   * 
   * @return The resource bundle key.
   */
  public String getNameKeyForPresentation() {
    return presentationKey;
  }

  /**
   * Returns the supported export formats. Some post-processing strategies may modify the result set
   * in a way that is only useful to certain exports. For example, a post-processing strategy may
   * modify the results in order to create a certain graphical export. An excel export of this data
   * may not be meaningful and is thus not supported.
   * 
   * @return A string array of supported export formats.
   */
  public String[] getSupportedReportTypes() {
    if (reportTypes != null) {
      return reportTypes.clone();
    }
    else {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
  }

  /**
   * This method does the actual post-processing for the given list of elements.
   * 
   * @param elements
   *          The set of elements to process.
   * @param root
   *          The root node of the current query. This parameter is used rarely.
   * @return The set of processed elements. Note that these elements may have been reloaded and
   *         therefore the object identity is not necessarily the same.
   */
  public abstract Set<T> process(Set<T> elements, Node root);


  /*
   * (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(AbstractPostprocessingStrategy<T> strategy) {
    return executionNumber.compareTo(strategy.getExecutionNumber());
  }
  
  public int hashCode() {
    return new HashCodeBuilder(17, 31).
        append(presentationKey).
        append(executionNumber).
        append(reportTypes).
        toHashCode();
  }

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    
    @SuppressWarnings("unchecked")
    AbstractPostprocessingStrategy<T> rhs = (AbstractPostprocessingStrategy<T>) obj;
    return new EqualsBuilder().
        append(this.presentationKey, rhs.presentationKey).
        append(this.executionNumber, rhs.executionNumber).
        append(this.reportTypes, rhs.reportTypes).
        isEquals();
  }
  
}