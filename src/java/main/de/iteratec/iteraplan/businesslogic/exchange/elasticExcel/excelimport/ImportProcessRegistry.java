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

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.ImportProcessFactory;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.ImportStrategy;


public class ImportProcessRegistry {

  /** Timeout in days until excel import processes will be removed with the next clean up action */
  // by default: three days
  private static final long        IMPORT_TIMEOUT_VALUE_IN_HOURS = IteraplanProperties.getIntProperty(IteraplanProperties.IMPORT_TIMEOUT);

  private static final long        MULTIPLICATION_FACTOR_TO_MS   = 1000 * 60 * 60;

  /**
   * Map from timestamp to Import process. The timestamp represents the start of the process and is represented as a Long object.
   */
  private Map<Long, ImportProcess> importProcesses               = Maps.newHashMap();

  private ImportProcessFactory     importProcessFactory;

  public void setImportProcessFactory(ImportProcessFactory importProcessFactory) {
    this.importProcessFactory = importProcessFactory;
  }

  public Long createNewExcelImport(InputStream in, ImportStrategy strategy) {
    //eam excel import:
    //ImportProcess process = new ExcelImportProcess(in, metamodelLoader, modelLoader, bbServiceLocator, attributeValueService);
    //mi excel import:
    ImportProcess process = importProcessFactory.createMiExcelImportProcess(strategy, in);
    return addProcessToRegistry(process);
  }

  public Long createNewXmiImport(InputStream in, ImportStrategy strategy) {
    ImportProcess process = importProcessFactory.createMiEmfImportProcess(strategy, in);
    return addProcessToRegistry(process);
  }

  protected Long addProcessToRegistry(ImportProcess process) {
    Long key = Long.valueOf(System.currentTimeMillis());
    importProcesses.put(key, process);
    return key;
  }

  public ImportProcess getImport(Long key) {
    return importProcesses.get(key);
  }

  public void removeImport(Long key) {
    importProcesses.remove(key);
  }

  /**
   * Iterates over all registered import processes, and removes any of them which has exceeded the timeout
   */
  public void cleanUpOldProcesses() {
    HashSet<Long> importProcessesKeySet = Sets.newHashSet();
    importProcessesKeySet.addAll(importProcesses.keySet());

    for (Long timestamp : importProcessesKeySet) {
      if (processHasExceededTimeout(timestamp)) {
        removeImport(timestamp);
      }
    }
  }

  private boolean processHasExceededTimeout(Long processStartTimestamp) {
    return System.currentTimeMillis() - processStartTimestamp.longValue() > IMPORT_TIMEOUT_VALUE_IN_HOURS * MULTIPLICATION_FACTOR_TO_MS;
  }

}
