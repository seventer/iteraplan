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
package de.iteratec.iteraplan.businesslogic.exchange.common;

import java.util.Arrays;

import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;


@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public abstract class ImportProcess {

  private final CheckList<CheckPoint>       checkList = new CheckList<CheckPoint>(Arrays.asList(CheckPoint.values()));
  private final BuildingBlockServiceLocator bbServiceLocator;
  private final AttributeValueService       attributeValueService;

  protected ImportProcess(BuildingBlockServiceLocator bbServiceLocator, AttributeValueService attributeValueService) {
    this.bbServiceLocator = bbServiceLocator;
    this.attributeValueService = attributeValueService;
  }

  protected AttributeValueService getAttributeValueService() {
    return this.attributeValueService;
  }

  protected BuildingBlockServiceLocator getBuildingBlockServiceLocator() {
    return this.bbServiceLocator;
  }

  public CheckList<CheckPoint> getCurrentCheckList() {
    return checkList;
  }

  /**
   * @return if the step was successfully executed
   * 
   * @throws Exception to ensure caller catches any exception and possibly removes the importprocess from the registry
   */
  public abstract boolean importAndCheckFile() throws Exception;

  /**
   * @return if the step was successfully executed
   * 
   * @throws Exception to ensure caller catches any exception and possibly removes the importprocess from the registry
   */
  public abstract boolean compareMetamodel() throws Exception;

  /**
   * @return if the step was successfully executed
   * 
   * @throws Exception to ensure caller catches any exception and possibly removes the importprocess from the registry
   */
  public abstract boolean writeMetamodel() throws Exception;

  /**
   * @return if the step was successfully executed
   * 
   * @throws Exception to ensure caller catches any exception and possibly removes the importprocess from the registry
   */
  public abstract boolean dryRun() throws Exception;

  /**
   * @return if the step was successfully executed
   * 
   * @throws Exception to ensure caller catches any exception and possibly removes the importprocess from the registry
   */
  public abstract boolean mergeModelIntoDb() throws Exception;

  /**
   * Returns true, if the current ImportProcess is considered Partial in terms of an PartialExportMetamodel
   */
  public abstract boolean isPartial();

  /**
   * Returns the name of a filtered type, if the current ImportProcess is considered Partial in terms of an PartialExportMetamodel. 
   */
  public abstract String getFilteredTypeName();

  /**
   * Returns the PersistentName of a filtered type, if the current ImportProcess is considered Partial in terms of an PartialExportMetamodel.
   */
  public abstract String getFilteredTypePersistentName();

  /**
   * Returns the filter, if the current ImportProcess is considered Partial in terms of an PartialExportMetamodel.
   */
  public abstract String getExtendedFilter();

  /**
   * Returns the timestamp of an export file.
   */
  public abstract String getExportTimestamp();

  /**
   * Runs the following steps in the order as listed below
   * <ul>
   *   <li>{@link #importAndCheckFile()}</li>
   *   <li>For non-partial Imports only: 
   *    <ul>
   *        <li>{@link #compareMetamodel()}</li>
   *        <li>{@link #writeMetamodel()}</li>
   *    </ul>
   *   </li>
   *   <li>{@link #dryRun()}</li>
   *   <li>{@link #mergeModelIntoDb()}</li>
   * </ul>
   */
  public boolean executeAllApplicableSteps() throws Exception {
    boolean success = importAndCheckFile();
    if (success && isPartial()) {
      //skip metamodel checkPoints
      getCurrentCheckList().pending(CheckPoint.METAMODEL_COMPARE);
      getCurrentCheckList().done(CheckPoint.METAMODEL_COMPARE);
      getCurrentCheckList().pending(CheckPoint.METAMODEL_MERGE);
      getCurrentCheckList().done(CheckPoint.METAMODEL_MERGE);
    }
    else if (success) {
      success = compareMetamodel() && writeMetamodel();
    }
    return success && dryRun() && mergeModelIntoDb();
  }

  public abstract ImportProcessMessages getImportProcessMessages();

  /**
   * Check points indicating certain steps of the import process
   */
  public enum CheckPoint {
    FILE_CHECK("excel.import.check.file", "checkFile"), //
    METAMODEL_COMPARE("excel.import.compare.metamodel", "compareMetamodels"), //
    METAMODEL_MERGE("excel.import.merge.metamodel", "mergeMetamodels"), //
    MODEL_COMPARE("excel.import.compare.model", "compareModels"), //
    MODEL_WRITE("excel.import.merge.model", "writeModel");

    private final String presentationKey;
    private final String action;

    private CheckPoint(String presentationKey, String action) {
      this.presentationKey = presentationKey;
      this.action = action;
    }

    public String getPresentationKey() {
      return this.presentationKey;
    }

    public String getAction() {
      return this.action;
    }
  }
}