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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess;
import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess.CheckPoint;
import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcessMessages;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages.ErrorLevel;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.MassdataMemBean;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.MassdataMemBean.ImportType;


public class ImportServiceImpl implements ImportService {
  private static final Logger   LOGGER = Logger.getIteraplanLogger(ImportServiceImpl.class);

  private ImportProcessRegistry importProcessRegistry;

  public void setImportProcessRegistry(ImportProcessRegistry importProcessRegistry) {
    this.importProcessRegistry = importProcessRegistry;
  }

  public boolean doImport(MassdataMemBean memBean) {
    InputStream in = new ByteArrayInputStream(memBean.getFileContent());
    if (ImportType.XMI.equals(memBean.getImportType())) {
      memBean.setImportProcessId(importProcessRegistry.createNewXmiImport(in, memBean.getImportStrategy()));
    }
    else {
      memBean.setImportProcessId(importProcessRegistry.createNewExcelImport(in, memBean.getImportStrategy()));
    }

    ImportProcess currentProcess = importProcessRegistry.getImport(memBean.getImportProcessId());
    try {
      currentProcess.importAndCheckFile();
    } catch (Exception e) {
      removeCurrentImport(memBean);
      throw new IteraplanTechnicalException(e);
    }

    setPartialInfoToMemBean(memBean, currentProcess);
    memBean.setExportTimestamp(currentProcess.getExportTimestamp());

    if ((!memBean.isImportMetamodel() || currentProcess.isPartial()) && currentProcess.getCurrentCheckList().getPending() == null) {
      //skip metamodel import if modelValidation has completed successfully 
      currentProcess.getCurrentCheckList().pending(CheckPoint.METAMODEL_COMPARE);
      currentProcess.getCurrentCheckList().done(CheckPoint.METAMODEL_COMPARE);
      currentProcess.getCurrentCheckList().pending(CheckPoint.METAMODEL_MERGE);
      currentProcess.getCurrentCheckList().done(CheckPoint.METAMODEL_MERGE);
    }

    return finishProcessStep(memBean, currentProcess);
  }

  public boolean compareMetamodel(MassdataMemBean memBean) {
    ImportProcess currentProcess = initProcessStep(memBean);
    try {
      currentProcess.compareMetamodel();
    } catch (Exception e) {
      removeCurrentImport(memBean);
      throw new IteraplanTechnicalException(e);
    }
    return finishProcessStep(memBean, currentProcess);
  }

  public boolean mergeMetamodel(MassdataMemBean memBean) {
    ImportProcess currentProcess = initProcessStep(memBean);
    try {
      currentProcess.writeMetamodel();
    } catch (Exception e) {
      removeCurrentImport(memBean);
      throw new IteraplanTechnicalException(e);
    }
    return finishProcessStep(memBean, currentProcess);
  }

  public boolean modelDryrun(MassdataMemBean memBean) {
    ImportProcess currentProcess = initProcessStep(memBean);
    try {
      currentProcess.dryRun();
    } catch (Exception e) {
      removeCurrentImport(memBean);
      throw new IteraplanTechnicalException(e);
    }
    return finishProcessStep(memBean, currentProcess);
  }

  public boolean mergeModelToDb(MassdataMemBean memBean) {
    ImportProcess currentProcess = initProcessStep(memBean);

    try {
      currentProcess.mergeModelIntoDb();
      logMessages(currentProcess.getImportProcessMessages());
    } catch (Exception e) {
      LOGGER.error("Import failed");
      logMessages(currentProcess.getImportProcessMessages());
      removeCurrentImport(memBean);
      throw new IteraplanTechnicalException(e);
    }

    ResultMessages result = currentProcess.getImportProcessMessages().asResultMessages();
    memBean.addResultMessages(result);
    memBean.updateCheckList(currentProcess.getCurrentCheckList());

    importProcessRegistry.removeImport(memBean.getImportProcessId());
    return result.getErrorLevel() != ErrorLevel.ERROR;
  }

  private void logMessages(ImportProcessMessages messages) {
    for (Message msg : messages.getMessages()) {
      switch (msg.getSeverity()) {
        case INFO:
          LOGGER.info(msg.getMessage());
          break;
        case WARNING:
          LOGGER.warn(msg.getMessage());
          break;
        case ERROR:
          LOGGER.error(msg.getMessage());
          break;
        default:
          break;
      }
    }
  }

  public void removeCurrentImport(MassdataMemBean memBean) {
    importProcessRegistry.removeImport(memBean.getImportProcessId());
    memBean.setImportProcessId(null);
  }

  private ImportProcess initProcessStep(MassdataMemBean memBean) {
    ImportProcess currentProcess = importProcessRegistry.getImport(memBean.getImportProcessId());
    currentProcess.getImportProcessMessages().clear();
    memBean.resetResultMessages();
    return currentProcess;
  }

  private boolean finishProcessStep(MassdataMemBean memBean, ImportProcess currentProcess) {
    memBean.addResultMessages(currentProcess.getImportProcessMessages().asResultMessages());
    memBean.updateCheckList(currentProcess.getCurrentCheckList());
    return isImportProcessStepSuccess(memBean);
  }

  private boolean isImportProcessStepSuccess(MassdataMemBean memBean) {
    boolean success = memBean.getResultMessages().getErrorLevel() != ErrorLevel.ERROR;
    if (!success) {
      removeCurrentImport(memBean);
    }
    return success;
  }

  private void setPartialInfoToMemBean(MassdataMemBean memBean, ImportProcess currentProcess) {
    if (currentProcess.isPartial()) {
      memBean.setPartialImport(currentProcess.isPartial());
      memBean.setFilteredTypeName(currentProcess.getFilteredTypeName());
      memBean.setFilteredTypePersistentName(currentProcess.getFilteredTypePersistentName());
      memBean.setExtendedFilter(currentProcess.getExtendedFilter());
    }
  }
}
