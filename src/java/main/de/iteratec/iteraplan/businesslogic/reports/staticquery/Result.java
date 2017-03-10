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
package de.iteratec.iteraplan.businesslogic.reports.staticquery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class represents the results of a static query. It is used as a data transfer object in
 * order to correctly display the results.
 * <p>
 * The class contains the following information:
 * <ul>
 * <li>The query's name.</li>
 * <li>The query's domain.</li>
 * <li>The meta information about the columns of the result table.</li>
 * <li>The data contained in the rows of the result table.</li>
 * <li>The parameters the result is based on.</li>
 * <li>The comparison logic of the result's rows.</li>
 * </ul>
 */
public class Result implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The query's localized name.
   */
  private String                 name;

  /**
   * The query´s number as it appears on the GUI.
   */
  private int                    number;

  /**
   * The query's domain.
   */
  private Domain                 domain;

  /**
   * The meta information of the columns of the result.
   */
  private List<ResultColumn>     columns    = new ArrayList<ResultColumn>();

  /**
   * The data contained in the rows of the result.
   */
  private List<ResultRow>        rows       = new ArrayList<ResultRow>();

  /**
   * The parameters that form the basis of the result.
   */
  private Map<String, Parameter> parameters = new HashMap<String, Parameter>();

  /**
   * The comparator for the rows of the result.
   */
  private RowComparator          rowComparator;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Domain getDomain() {
    return domain;
  }

  public void setDomain(Domain domain) {
    this.domain = domain;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public List<ResultColumn> getColumns() {
    return columns;
  }

  public void setColumns(List<ResultColumn> columns) {
    this.columns = columns;
  }

  public List<ResultRow> getRows() {
    return rows;
  }

  public void setRows(List<ResultRow> rows) {
    this.rows = rows;
  }

  public Map<String, Parameter> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, Parameter> parameters) {
    this.parameters = parameters;
  }

  public RowComparator getRowComparator() {
    return rowComparator;
  }

  public void setRowComparator(RowComparator rowComparator) {
    this.rowComparator = rowComparator;
  }

  /**
   * This abstract inner class may be used for defining comparison code for the list of rows
   * encapsulated by the {@link Result} class. It defines a {@link #compare(ResultRow, ResultRow)}
   * method, that may be implemented, in conjunction with the outer class, as follows:
   * 
   * <pre>
   * result.setRowComparator(new RowComparator() {
   *   public int compare(Row row1, Row row2) {
   *     // Comparison code goes here.
   *   }
   * });
   * </pre>
   * 
   * The calling code might look as follows:
   * 
   * <pre>
   * Collections.sort(result.getRows(), result.getRowComparator());
   * </pre>
   */
  public abstract static class RowComparator implements Comparator<ResultRow>, Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 2923143868886577297L;

    /*
     * (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public abstract int compare(ResultRow row1, ResultRow row2);
  }

}