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
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


public class TemplatesManagerServiceImpl extends TemplatesBaseService implements TemplatesManagerService {
  private static final Logger LOGGER = Logger.getIteraplanLogger(TemplatesManagerServiceImpl.class);

  /**
   * Default constructor.
   * @param templatesBaseDir
   *          Directory where the sub-directories for each {@link TemplateType} are in classpath
   */
  public TemplatesManagerServiceImpl(String templatesBaseDir) {
    super(templatesBaseDir);
  }

  /**{@inheritDoc}**/
  public void saveTemplateFile(TemplateType templateType, MultipartFile uploadedFile) {
    try {
      String fileName = uploadedFile.getOriginalFilename();

      String newFileNameAndPath = getTemplateResourcePath(templateType) + fileName;

      File newTemplateFile = new File(newFileNameAndPath);
      if (!newTemplateFile.exists()) {
        uploadedFile.transferTo(newTemplateFile);
      }
    } catch (IllegalStateException e) {
      LOGGER.error("Template file has been moved and is not available for saving anymore.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    } catch (IOException e) {
      LOGGER.error("Template file could not be saved.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  /**{@inheritDoc}**/
  public void removeTemplate(TemplateType templateType, String fileName) {
    String fileNameToRemove = getTemplateResourcePath(templateType) + fileName;
    File fileToRemove = new File(fileNameToRemove);
    if (!fileToRemove.delete()) {
      LOGGER.warn("File '" + fileName + "' could not be deleted.");
    }
  }

}
