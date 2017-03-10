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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read;

import java.io.InputStream;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.ImportStrategy;


public class IteraplanImportProcessFactory implements ImportProcessFactory {

  private BuildingBlockServiceLocator bbServiceLocator;

  private BuildingBlockTypeService    bbtService;

  private AttributeTypeGroupService   atgService;

  private AttributeTypeService        atService;

  private AttributeValueService       avService;

  private IteraplanMiLoadTaskFactory  loadTaskFactory;

  @Override
  public MiImportProcess createMiExcelImportProcess(ImportStrategy strategy, InputStream in) {
    return new MiExcelImportProcess(bbServiceLocator, bbtService, atgService, atService, avService, strategy, loadTaskFactory, in);
  }

  @Override
  public MiImportProcess createMiEmfImportProcess(ImportStrategy strategy, InputStream in) {
    return new MiEmfImportProcess(bbServiceLocator, bbtService, atgService, atService, avService, strategy, loadTaskFactory, in);
  }

  @Override
  public MiJsonMicroImportProcess createMiJsonMicroImportProcess() {
    return new MiJsonMicroImportProcess(bbServiceLocator, avService, loadTaskFactory);
  }

  public void setBbServiceLocator(BuildingBlockServiceLocator bbServiceLocator) {
    this.bbServiceLocator = bbServiceLocator;
  }

  public void setBbtService(BuildingBlockTypeService bbtService) {
    this.bbtService = bbtService;
  }

  public void setAtgService(AttributeTypeGroupService atgService) {
    this.atgService = atgService;
  }

  public void setAtService(AttributeTypeService atService) {
    this.atService = atService;
  }

  public void setAvService(AttributeValueService avService) {
    this.avService = avService;
  }

  public void setLoadTaskFactory(IteraplanMiLoadTaskFactory loadTaskFactory) {
    this.loadTaskFactory = loadTaskFactory;
  }
}
