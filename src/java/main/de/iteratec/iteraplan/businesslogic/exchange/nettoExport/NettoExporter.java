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
package de.iteratec.iteraplan.businesslogic.exchange.nettoExport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;

import de.iteratec.iteraplan.businesslogic.exchange.nettoExport.NettoExcelTransformer.ExcelVersion;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Exporter to create output (like Excel) from a {@link List} of {@link BuildingBlock} entities and write it to an {@link OutputStream}.
 */
public final class NettoExporter {

  private static final Logger          LOGGER              = Logger.getIteraplanLogger(NettoExporter.class);

  private static final NettoExportType DEFAULT_EXPORT_TYPE = NettoExportType.EXCEL_2007;

  private NettoExportType              exportType;
  private TypeOfBuildingBlock          typeOfBuildingBlock;

  public static enum NettoExportType {
    EXCEL_2003("xls", "application/vnd.ms-excel", "iteraplanExcelData"), EXCEL_2007("xlsx",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "iteraplanExcelData"), CSV("csv",
        "text/comma-separated-values; charset=UTF-8", "iteraplanCSVData");

    private String extension;
    private String contentType;
    private String baseFilename;

    private NettoExportType(String extension, String contentType, String baseFilename) {
      this.extension = extension;
      this.contentType = contentType;
      this.baseFilename = baseFilename;
    }

    public String getExtension() {
      return extension;
    }

    public String getContentType() {
      return contentType;
    }

    public String getBaseFilename() {
      return baseFilename;
    }
  }

  private NettoExporter() {
    // nothing to do
  }

  /**
   * Static factory method to generate a new NettoExport instance. 
   * @param typeOfBuildingBlock  The type of {@link BuildingBlock}s for the export.
   * @param downloadFormat  A parameter value from the HTTP request
   * @return  A NettoExporter instance for the specified format and type of building block.
   */
  public static NettoExporter newInstance(TypeOfBuildingBlock typeOfBuildingBlock, String downloadFormat) {
    Preconditions.checkNotNull(typeOfBuildingBlock, "Could not determine type of building block for netto exort.");

    NettoExporter exporter = new NettoExporter();
    exporter.typeOfBuildingBlock = typeOfBuildingBlock;

    if (downloadFormat == null) {
      exporter.exportType = DEFAULT_EXPORT_TYPE;
    }
    else if ("xls".equalsIgnoreCase(downloadFormat)) {
      exporter.exportType = NettoExportType.EXCEL_2003;
    }
    else if ("xlsx".equalsIgnoreCase(downloadFormat)) {
      exporter.exportType = NettoExportType.EXCEL_2007;
    }
    else if ("csv".equalsIgnoreCase(downloadFormat)) {
      exporter.exportType = NettoExportType.CSV;
    }

    return exporter;
  }

  /**
   * Perform the transformation of the result list to the target format, using the specified {@link TableStructure}
   * and write the result to an {@link OutputStream}
   * @param resultList  A {@link List} of {@link BuildingBlock} entities.
   * @param tableStructure  A {@link TableStructure} from the GUI.
   * @param outputStream  An {@link OutputStream} for the result.
   */
  public void exportToOutputStream(List<?> resultList, TableStructure tableStructure, OutputStream outputStream) {
    Preconditions.checkNotNull(resultList, "Invalid result list for netto export.");
    Preconditions.checkNotNull(tableStructure, "Invalid table structure for netto export.");
    Preconditions.checkNotNull(outputStream, "Invalid output stream for netto export.");

    NettoTransformer transformer;

    switch (exportType) {
      case EXCEL_2003:
        transformer = NettoExcelTransformer.newInstance(tableStructure, ExcelVersion.EXCEL_VERSION_2003);
        LOGGER.debug("Create Netto Export transformer for Excel 2003");
        break;

      case EXCEL_2007:
        transformer = NettoExcelTransformer.newInstance(tableStructure, ExcelVersion.EXCEL_VERSION_2007);
        LOGGER.debug("Create Netto Export transformer for Excel 2007");
        break;

      case CSV:
        transformer = NettoCSVTransformer.newInstance(tableStructure);
        LOGGER.debug("Create Netto Export transformer for CSV");
        break;

      default:
        transformer = NettoExcelTransformer.newInstance(tableStructure, ExcelVersion.EXCEL_VERSION_2007);
        LOGGER.debug("Create default Netto Export transformer (Excel 2007).");
        break;

    }

    try {
      transformer.transform(resultList, outputStream, getTypeOfBuildingBlock());
    } catch (Exception e) {
      LOGGER.warn("Could not perform netto export!");
      throw new IteraplanTechnicalException(e);
    }
  }

  /**
   * Perform the transformation of the result list to the target format, using the specified {@link TableStructure}
   * and write the result to the {@link HttpServletResponse}.
   * @param resultList  A {@link List} of {@link BuildingBlock} entities.
   * @param tableStructure  A {@link TableStructure} from the GUI.
   * @param response  The {@link HttpServletResponse}. Will be closed (=committed). 
   */
  public void exportToResponse(List<?> resultList, TableStructure tableStructure, HttpServletResponse response) {
    Preconditions.checkNotNull(response, "HTTP Reponse object must not be null!");
    Preconditions.checkArgument(!response.isCommitted(), "Could not write to HTTP Response (because it has already been committed)!");

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    exportToOutputStream(resultList, tableStructure, buffer);
    response.setHeader("Content-Type", getExportType().getContentType());
    response.setHeader("Content-Length", String.valueOf(buffer.size()));
    String downloadFilename = getFilename().replace(" ", "");
    response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFilename + "\"");
    try {
      ServletOutputStream outputStream = response.getOutputStream();
      outputStream.write(buffer.toByteArray());
      response.setStatus(HttpServletResponse.SC_OK);
      LOGGER.debug("Create download: " + downloadFilename);
      response.flushBuffer();
    } catch (IOException e) {
      LOGGER.warn("Could not write netto export file to servlet response!", e);
    }
  }

  /**
   * Create the filename for the export.
   * @return  A filename like "iteraplanExcelData_Project_YYYY-MM-DD.xlsx".
   */
  public String getFilename() {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd", UserContext.getCurrentLocale());
    String today = df.format(new Date());

    StringBuilder filenameBuilder = new StringBuilder(32);

    filenameBuilder.append(exportType.getBaseFilename());
    if (typeOfBuildingBlock != null) {
      String tobName = MessageAccess.getString(typeOfBuildingBlock.getValue());
      filenameBuilder.append("_");
      filenameBuilder.append(tobName);
    }
    filenameBuilder.append("_");
    filenameBuilder.append(today);
    filenameBuilder.append(".");
    filenameBuilder.append(exportType.getExtension());

    return filenameBuilder.toString();
  }

  public NettoExportType getExportType() {
    return exportType;
  }

  public TypeOfBuildingBlock getTypeOfBuildingBlock() {
    return typeOfBuildingBlock;
  }
}
