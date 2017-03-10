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
package de.iteratec.iteraplan.businesslogic.exchange.templates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * A helper service for searching template files. This service supports caching of the present template files.
 * If enabled, the files will be read upon the first method call. The files
 * will be filtered using the specified file extensions. Sub-directories will be not
 * searched.
 * 
 * @author agu
 *
 */

public class TemplateLocatorServiceImpl extends TemplatesBaseService implements TemplateLocatorService {

  private static final Set<String>             UNDELETABLE_TEMPLATES = Sets.newHashSet("ExcelWorkbook.xls", "ExcelWorkbook.xlsx");

  private static final Logger                  LOGGER                = Logger.getIteraplanLogger(TemplateLocatorServiceImpl.class);

  private final Caching                        cache;

  /** Cached files list. */
  private Map<TemplateType, Map<String, File>> templateNamesToFiles;
  private Map<TemplateType, Set<TemplateInfo>> availableTemplates;

  /**
   * Creates this instance without caching files.
   * 
   * @param classPathDir the base directory path in classpath
   */
  public TemplateLocatorServiceImpl(String classPathDir) {
    this(classPathDir, Caching.DISABLED);
  }

  /**
   * Creates this instance with optionally cached files.
   * 
   * @param classPathDir the directory path in classpath
   * @param cache a flag indicating, if the files should be cached or not
   */
  public TemplateLocatorServiceImpl(String classPathDir, Caching cache) {
    super(classPathDir);
    this.cache = cache;
  }

  /** {@inheritDoc} */
  public Set<TemplateInfo> getTemplateInfos(TemplateType type) {
    if (cache.equals(Caching.DISABLED) || availableTemplates == null || availableTemplates.get(type) == null) {
      readTemplates(type);
    }

    return availableTemplates.get(type);
  }

  /** {@inheritDoc} */
  public File getFile(TemplateType type, String templateName) {
    if (StringUtils.isBlank(templateName)) {
      // purge cache, so it can be re-initialized
      clearCache();
      Exception detailMessage = new FileNotFoundException("The template file " + templateName + " does no longer exist in the filesystem.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.FILE_NOT_FOUND_EXCEPTION, detailMessage);
    }

    File file = getTemplateFiles(type).get(templateName);
    if ((file != null) && (!file.canRead())) {
      // purge cache, so it can be re-initialized
      clearCache();
      Exception detailMessage = new FileNotFoundException("The template file " + templateName + " does no longer exist in the filesystem.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.FILE_NOT_FOUND_EXCEPTION, detailMessage);
    }
    return file;
  }

  /**
   * Returns the filtered collection of files of the requested type.
   * 
   * @param type
   *          The requested {@link TemplateType}
   * @return the filtered collection of files
   */
  private Map<String, File> getTemplateFiles(TemplateType type) {
    if (cache.equals(Caching.DISABLED) || templateNamesToFiles == null || templateNamesToFiles.get(type) == null) {
      readTemplates(type);
    }

    return templateNamesToFiles.get(type);
  }

  private void readTemplates(TemplateType type) {
    try {
      File templatesDir = getTemplateResource(type).getFile();

      if (!templatesDir.isDirectory()) {
        String msg = String.format("The path %s is not a directory on the classpath!", getTemplateResource(type).getPath());
        throw new IllegalStateException(msg);
      }

      IOFileFilter suffixFileFilter = getFileExtensionsFilter(type.getExtensions());
      Collection<File> foundTemplates = FileUtils.listFiles(templatesDir, suffixFileFilter, null);

      Map<String, File> templateMap = Maps.newTreeMap();
      for (File file : foundTemplates) {
        String name = file.getName();
        templateMap.put(name, file);
      }
      putFileMapping(type, templateMap);
    } catch (IOException e) {
      LOGGER.error("Templates resource '" + getTemplateResource(type).getPath()
          + "' is not accessible in the file system. Maybe the iteraplan WAR file was not unpacked or the directory doesn't exist.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.FILE_ERROR_WAR_PACKED, e);
    }
  }

  private void putFileMapping(TemplateType type, Map<String, File> templateMap) {
    if (templateNamesToFiles == null) {
      templateNamesToFiles = Maps.newHashMap();
    }
    templateNamesToFiles.put(type, templateMap);
    putAvailableTemplateInfos(type, templateMap.keySet());
  }

  private void putAvailableTemplateInfos(TemplateType type, Set<String> templateNames) {
    if (availableTemplates == null) {
      availableTemplates = Maps.newHashMap();
    }
    Set<TemplateInfo> infos = availableTemplates.get(type);
    if (infos == null) {
      infos = Sets.newTreeSet();
      availableTemplates.put(type, infos);
    }

    for (String name : templateNames) {
      TemplateInfo info = new TemplateInfo(name);
      if (UNDELETABLE_TEMPLATES.contains(info.getName())) {
        info.setDeletable(false);
      }
      infos.add(info);
    }
  }

  /**
   * Creates a filter for filtering the files using the specified file extensions.
   * 
   * @param extensions the file extensions to create filter for
   * @return the file name filter
   */
  private IOFileFilter getFileExtensionsFilter(Set<String> extensions) {
    List<IOFileFilter> filters = Lists.newArrayList();
    for (String extension : extensions) {
      filters.add(FileFilterUtils.suffixFileFilter(extension));
    }

    IOFileFilter[] fileFilters = filters.toArray(new IOFileFilter[filters.size()]);

    return FileFilterUtils.or(fileFilters);
  }

  /**{@inheritDoc}**/
  public void clearCache() {
    this.templateNamesToFiles = null;
    this.availableTemplates = null;
  }

  public enum Caching {
    ENABLED, DISABLED;
  }

}
