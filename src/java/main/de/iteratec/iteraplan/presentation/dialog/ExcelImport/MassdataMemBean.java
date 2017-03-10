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
package de.iteratec.iteraplan.presentation.dialog.ExcelImport;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import de.iteratec.iteraplan.businesslogic.exchange.common.CheckList;
import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess.CheckPoint;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages;
import de.iteratec.iteraplan.businesslogic.exchange.common.ResultMessages.ErrorLevel;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.common.PagePosition;


public class MassdataMemBean implements PagePosition, Serializable {

  /** Serialization version */
  private static final long       serialVersionUID = -4613895774348541077L;

  private String                  pagePositionY;
  private String                  pagePositionX;

  private Long                    importProcessId;

  private boolean                 importMetamodel  = false;
  private CheckList<CheckPoint>   checkList;
  private ResultMessages          resultMessages   = new ResultMessages();

  /** Temporary holds a file for upload. After upload access the file's content with {@link #getFileContent()}. */
  private transient MultipartFile fileToUpload;
  private transient MultipartFile timeseriesFileToUpload;

  private String                  fileName;
  private byte[]                  fileContent;
  private ImportType              importType;

  private ImportStrategy          importStrategy;

  /*---Export Properties---*/
  private boolean                 partialExport;
  private TypeOfBuildingBlock     filteredTypeExport;
  private String                  extendedFilterExport;

  /*---Import Properties---*/
  private boolean                 partialImport    = false;
  private String                  filteredTypeName;
  private String                  filteredTypePersistentName;
  private String                  extendedFilter;
  private String                  exportTimestamp;

  public void setImportProcessId(Long processId) {
    this.importProcessId = processId;
  }

  public Long getImportProcessId() {
    return this.importProcessId;
  }

  public List<CheckPoint> getCurrentCheckList() {
    if (checkList == null) {
      return Collections.emptyList();
    }
    return ImmutableList.copyOf(checkList.getDone());
  }

  public List<CheckPoint> getCurrentTodoList() {
    if (checkList == null) {
      return Collections.emptyList();
    }
    return ImmutableList.copyOf(checkList.getToDo());
  }

  public CheckPoint getPendingCheckPoint() {
    if (checkList == null) {
      return null;
    }
    return checkList.getPending();
  }

  public void addErrorMessage(String errorMessage) {
    this.resultMessages.addMessage(ErrorLevel.ERROR, errorMessage);
  }

  public void addResultMessages(ResultMessages messages) {
    this.resultMessages.addMessages(messages);
  }

  public ResultMessages getResultMessages() {
    return this.resultMessages;
  }

  public void updateCheckList(CheckList<CheckPoint> updatedCheckList) {
    this.checkList = updatedCheckList;
  }

  public void resetResultMessages() {
    this.resultMessages = new ResultMessages();
  }

  public void setFileToUpload(MultipartFile fileToUpload) {
    this.fileToUpload = fileToUpload;
  }

  public void setTimeseriesFileToUpload(MultipartFile fileToUpload) {
    this.timeseriesFileToUpload = fileToUpload;
  }

  public MultipartFile getFileToUpload() {
    return fileToUpload;
  }

  public MultipartFile getTimeseriesFileToUpload() {
    return timeseriesFileToUpload;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileContent(byte[] fileContent) {
    this.fileContent = ArrayUtils.clone(fileContent);
  }

  public byte[] getFileContent() {
    return ArrayUtils.clone(fileContent);
  }

  public void setImportType(ImportType importType) {
    this.importType = importType;
  }

  public ImportType getImportType() {
    return importType;
  }

  public String getPagePositionY() {
    return pagePositionY;
  }

  public void setPagePositionY(String pagePositionY) {
    this.pagePositionY = pagePositionY;
  }

  public String getPagePositionX() {
    return pagePositionX;
  }

  public void setPagePositionX(String pagePositionX) {
    this.pagePositionX = pagePositionX;
  }

  public enum ImportType {
    EXCEL, XMI;
  }

  public String getImportTypeHeader() throws IllegalStateException {
    if (importType == null) {
      return "global.error_import_headline";
    }
    String result = null;
    switch (importType) {
      case EXCEL: {
        result = "global.excel_import_headline";
        break;
      }
      case XMI: {
        result = "global.xmi_import_headline";
        break;
      }
      default: {
        throw new IllegalStateException("Unknown filetype");
      }
    }
    return result;
  }

  /**
   * @return the available import strategies
   */
  public ImportStrategy[] getImportStrategies() {
    return ImportStrategy.values();
  }

  /**
   * @return the selected import strategy
   */
  public ImportStrategy getImportStrategy() {
    return importStrategy;
  }

  public void setImportStrategy(ImportStrategy importStrategy) {
    this.importStrategy = importStrategy;
  }

  /**
   * @return partialExport the partialExport
   */
  public boolean isPartialExport() {
    return partialExport;
  }

  public void setPartialExport(boolean partialExport) {
    this.partialExport = partialExport;
  }

  public List<TypeOfBuildingBlock> getTypeOfBuildingBlocksForPartial() {
    List<TypeOfBuildingBlock> excludedTypes = Lists.newArrayList(TypeOfBuildingBlock.BUSINESSMAPPING);
    List<TypeOfBuildingBlock> displayForPartial = Lists.newArrayList(TypeOfBuildingBlock.DISPLAY);
    displayForPartial.removeAll(excludedTypes);
    Function<TypeOfBuildingBlock, String> tobToLocalizedName = new Function<TypeOfBuildingBlock, String>() {
      @Override
      public String apply(TypeOfBuildingBlock input) {
        return MessageAccess.getString(input.toString(), UserContext.getCurrentLocale());
      }
    };
    return Lists.newArrayList(Ordering.natural().onResultOf(tobToLocalizedName).sortedCopy(displayForPartial));
  }

  /**
   * @return filteredTypeExport the filteredTypeExport
   */
  public TypeOfBuildingBlock getFilteredTypeExport() {
    return filteredTypeExport;
  }

  public void setFilteredTypeExport(TypeOfBuildingBlock filteredTypeExport) {
    this.filteredTypeExport = filteredTypeExport;
  }

  /**
   * @return extendedFilterExport the extendedFilterExport
   */
  public String getExtendedFilterExport() {
    return extendedFilterExport;

  }

  public void setExtendedFilterExport(String extendedFilterExport) {
    this.extendedFilterExport = extendedFilterExport;
  }

  public boolean isImportMetamodel() {
    //TODO other conditions here as well?
    //return importMetamodel && ImportStrategy.ADDITIVE.equals(importStrategy);
    return importMetamodel;
  }

  public void setImportMetamodel(boolean importMetamodel) {
    this.importMetamodel = importMetamodel;
  }

  public boolean isPartialImport() {
    return partialImport;
  }

  public void setPartialImport(boolean partialImport) {
    this.partialImport = partialImport;
  }

  public String getFilteredTypeName() {
    return filteredTypeName;
  }

  public void setFilteredTypeName(String filteredTypeName) {
    this.filteredTypeName = filteredTypeName;
  }

  public String getFilteredTypePersistentName() {
    return filteredTypePersistentName;
  }

  public void setFilteredTypePersistentName(String filteredTypePersistentName) {
    this.filteredTypePersistentName = filteredTypePersistentName;
  }

  public String getExtendedFilter() {
    return extendedFilter;
  }

  public void setExtendedFilter(String extendedFilter) {
    this.extendedFilter = extendedFilter;
  }

  public String getExportTimestamp() {
    return exportTimestamp;
  }

  public void setExportTimestamp(String exportTimestamp) {
    this.exportTimestamp = exportTimestamp;
  }
}
