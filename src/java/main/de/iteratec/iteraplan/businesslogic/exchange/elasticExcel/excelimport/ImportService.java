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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.excelimport;

import de.iteratec.iteraplan.presentation.dialog.ExcelImport.MassdataMemBean;

/**
 * This Service defines a multi-step import process which must be walked through in a defined order:
 * <ol>
 *      <li>{@link #doImport(MassdataMemBean)}</li>
 *      <li>{@link #compareMetamodel(MassdataMemBean)}</li>
 *      <li>{@link #mergeMetamodel(MassdataMemBean)}</li>
 *      <li>{@link #modelDryrun(MassdataMemBean)}</li>
 *      <li>{@link #mergeModelToDb(MassdataMemBean)}</li>
 * </ol>
 * In particular cases, some of these cases may have to skipped, or the process aborts due to an error
 * condition. Details for such circumstances should be retrieved via {@link MassdataMemBean#getCurrentTodoList()},
 * and for textual presentation to the user also via {@link MassdataMemBean#getResultMessages()}.
 * 
 * {@link #removeCurrentImport(MassdataMemBean)} can be called at any time to abort a currently ongoing import process.
 */
public interface ImportService {

  /**
   * Accepts the initially uploaded file, and performs a consistency check on the file's internal structure.
   * @param memBean
   * @return {@code true} if this step completed successfully, {@code false} otherwise
   */
  boolean doImport(MassdataMemBean memBean);

  /**
   * Checks the metamodel information found in the previously uploaded file against iteraplan's currently persistent metamodel.
   * If any (significant, further details tbd) differences are detected, an update of iteraplan's metamodel
   * must be triggered in a next step thorugh {@link #mergeMetamodel(MassdataMemBean)}, so that both metamodels are compatible with each other
   * @param memBean
   * @return {@code true} if this step completed successfully, {@code false} otherwise
   */
  boolean compareMetamodel(MassdataMemBean memBean);

  /**
   * Applies necessary metamodel updates to iteraplan's current metamodel.
   * @param memBean
   * @return {@code true} if this step completed successfully, {@code false} otherwise
   */
  boolean mergeMetamodel(MassdataMemBean memBean);

  /**
   * Performs an in-memory dry-run of the model changes that are found in the uploaded file.
   * @param memBean
   * @return {@code true} if this step completed successfully, {@code false} otherwise
   */
  boolean modelDryrun(MassdataMemBean memBean);

  /**
   * Writes the model chnages from the file into the current iteraplan model, hence also the database.
   * @param memBean
   * @return {@code true} if this step completed successfully, {@code false} otherwise
   */
  boolean mergeModelToDb(MassdataMemBean memBean);

  /**
   * Removes all currently held management and state information about an ongoing import process.
   * @param memBean
   */
  void removeCurrentImport(MassdataMemBean memBean);

}
