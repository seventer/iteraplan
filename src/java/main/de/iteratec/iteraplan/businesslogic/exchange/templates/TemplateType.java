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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Constants;


/**
 * Enum to denote the type a template is used for.
 * Also holds the relative path the templates are saved in and
 * property keys for displaying the type on the UI, as well as the extensions
 * for the according template files.
 */
public enum TemplateType {
  EXCEL_2007("/legacyExcel", Constants.REPORTS_EXPORT_EXCEL_2007, Sets.newHashSet(".xlsx"),
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", Constants.TEMPLATE_EXCEL_2003_EXTENDED_INFO), EXCEL_2003("/legacyExcel",
      Constants.REPORTS_EXPORT_EXCEL_2003, Sets.newHashSet(".xls"), "application/vnd.ms-excel", Constants.TEMPLATE_EXCEL_2007_EXTENDED_INFO), INFOFLOW(
      "/informationflow", Constants.REPORTS_EXPORT_INFORMATION_FLOW, Sets.newHashSet(".vdx"), "application/vnd.visio+xml",
      Constants.TEMPLATE_INFO_FLOW_EXTENDED_INFO);

  private static final Map<String, TemplateType> KEY_TO_TYPE = Maps.newHashMap();
  static {
    KEY_TO_TYPE.put(Constants.REPORTS_EXPORT_EXCEL_2003, EXCEL_2003);
    KEY_TO_TYPE.put(Constants.REPORTS_EXPORT_EXCEL_2007, EXCEL_2007);
    KEY_TO_TYPE.put(Constants.REPORTS_EXPORT_INFORMATION_FLOW, INFOFLOW);
  }

  /** Path where templates of this type should be saved, relative to the templates base dir */
  private String                                 path;
  /** Property key for localized display of this type's name */
  private String                                 namePropertyKey;
  /** Set of file extensions allowed for this template type */
  private Set<String>                            extensions;
  /** MIME type used when providing a download for files of this template type */
  private String                                 mimeType;
  /** Extended information to be displayed on the document template page */
  private String                                 extendedInfoPropertyKey;

  private TemplateType(String path, String namePropertyKey, Set<String> extensions, String mimeType, String extendedInfoPropertyKey) {
    this.path = path;
    this.namePropertyKey = namePropertyKey;
    this.extensions = extensions;
    this.mimeType = mimeType;
    this.extendedInfoPropertyKey = extendedInfoPropertyKey;
  }

  /**
   * Returns the path for this template type.
   * @return {@link #path}
   */
  public String getPath() {
    return path;
  }

  /**
   * Returns the name key for this template type.
   * @return {@link #namePropertyKey}
   */
  public String getNameKey() {
    return this.namePropertyKey;
  }

  /**
   * Returns the allowed file extensions for this template type.
   * @return {@link #extensions}
   */
  public Set<String> getExtensions() {
    return this.extensions;
  }

  /**
   * Returns the first allowed file extensions for this template type.
   * @return String with the name of the first found extension
   */
  public String getFirstExtension() {
    Iterator<String> it = this.extensions.iterator();
    return it.next();
  }

  /**
   * Returns the MIME type of templates of this type.
   * @return {@link #mimeType}
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * Returns the {@link TemplateType} belonging to the given name.
   * @param nameKey
   *          The {@link #namePropertyKey} of the type to get.
   * @return the according template type instance
   */
  public static TemplateType getTypeFromKey(String nameKey) {
    return KEY_TO_TYPE.get(nameKey);
  }

  /**
   * Returns the name key for this template type.
   * @return {@link #extendedInfoPropertyKey}
   */
  public String getExtendedInfoKey() {
    return this.extendedInfoPropertyKey;
  }

}