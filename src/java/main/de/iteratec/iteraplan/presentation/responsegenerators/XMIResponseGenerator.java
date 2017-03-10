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
package de.iteratec.iteraplan.presentation.responsegenerators;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import de.iteratec.iteraplan.model.BuildingBlock;


public interface XMIResponseGenerator {

  /**
   * Serializes all entity-instances into an xmi file and writes the {@link java.io.FileOutputStream} into
   * the {@link HttpServletResponse}
   */
  void generateXmiResponseForExport(HttpServletResponse response, String filename);

  /**
   * Creates one ecore file, containing models of all entity-packages (at the moment
   * de.iteratec.iteraplan.model, de.iteratec.iteraplan.model.attribute,
   * de.iteratec.iteraplan.model.files, de.iteratec.iteraplan.model.user) and writes the
   * {@link java.io.FileOutputStream} to the {@link HttpServletResponse}
   */
  void generateEcoreForExport(HttpServletResponse response, String filename);

  /**
   * Serializes all entity-instances to an XMI file, creates ecore models for each Package and
   * writes all these files into a new {@link java.util.zip.ZipOutputStream}
   */
  void generateXmiAndEcoreZipBundle(HttpServletResponse response, String filename);

  /**
   * Generates EStructuralFeatures for all self-defined AttributeTypes to extend the Ecore model
   * defined in de.iteratec.iteraplan.xmi.IteraplanModelForTabularReporting.ecore
   */
  void generateEcoreResponseForTabularReporting(HttpServletResponse response, String filename);

  /**
   * Generates an xmi-file, containing EInstances for all passed BuildingBlocks
   * 
   * @param response
   *          The {@link HttpServletResponse}
   * @param buildingBlocks
   *          The passed {@link BuildingBlock} instances
   * @param fileName
   *          The name of the xml-file
   */
  void generateXmlResponseForTabularReporting(HttpServletResponse response, Collection<? extends BuildingBlock> buildingBlocks, String fileName);

  
  void generateCompleteXmlExport(HttpServletResponse response, String filename);
}