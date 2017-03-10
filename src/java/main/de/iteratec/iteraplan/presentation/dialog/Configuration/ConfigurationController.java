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
package de.iteratec.iteraplan.presentation.dialog.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Cache;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.SessionFactoryImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import de.iteratec.iteraplan.businesslogic.service.ElasticeamService;
import de.iteratec.iteraplan.businesslogic.service.SearchService;
import de.iteratec.iteraplan.businesslogic.service.SearchService.PurgeMode;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;


@Controller
public class ConfigurationController extends GuiController {
  private static final Logger LOGGER              = Logger.getIteraplanLogger(ConfigurationController.class);

  private static final String DIALOG_MEMORY_LABEL = "dialogMemory";

  private String              resetPage;
  private String              initPage;

  @Autowired
  private SessionFactoryImplementor      sessionFactory;

  @Autowired
  private SearchService       searchService;

  @Autowired
  private ElasticeamService elasticService;

  public String getResetPage() {
    return resetPage;
  }

  public void setResetPage(String resetPage) {
    this.resetPage = resetPage;
  }

  public String getInitPage() {
    return initPage;
  }

  public void setInitPage(String initPage) {
    this.initPage = initPage;
  }

  @Override
  protected String getDialogName() {
    return Dialog.CONFIGURATION.getDialogName();
  }

  @Override
  @RequestMapping
  public void init(ModelMap model, HttpSession session, HttpServletRequest req) {
    super.init(model, session, req);

    LOGGER.debug("ConfigurationController#init");
    UserContext userContext = UserContext.getCurrentUserContext();
    GuiContext guiCtx = GuiContext.getCurrentGuiContext();

    ConfigurationDialogMemory dialogMem;
    if (guiCtx.hasDialogMemory(this.getDialogName())) {
      dialogMem = (ConfigurationDialogMemory) guiCtx.getDialogMemory(this.getDialogName());
    }
    else {
      dialogMem = new ConfigurationDialogMemory();
      dialogMem.setShowInactive(userContext.isShowInactiveStatus());
      dialogMem.setSelectedDataSource(userContext.getDataSource());
    }

    updateGuiContext(dialogMem);
    model.addAttribute(DIALOG_MEMORY_LABEL, dialogMem);
  }

  @RequestMapping
  public String refreshIndex(@ModelAttribute(DIALOG_MEMORY_LABEL) ConfigurationDialogMemory dialogMem, HttpSession session, HttpServletRequest req) {
    if (!UserContext.getCurrentPerms().getUserHasDialogPermission(getDialogName())) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }
    PurgeMode indexPurgeMode = (dialogMem.isPurgeIndex() ? PurgeMode.PURGE : PurgeMode.NO_PURGE);
    searchService.createIndex(indexPurgeMode);

    updateGuiContext(dialogMem);
    return initPage;
  }

  @RequestMapping
  public String saveDataSource(@ModelAttribute(DIALOG_MEMORY_LABEL) ConfigurationDialogMemory dialogMem, HttpSession session, HttpServletRequest req) {

    if (!UserContext.getCurrentPerms().getUserHasDialogPermission(getDialogName()) || !UserContext.getCurrentPerms().isUserHasFuncPermDatasources()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }

    UserContext userContext = UserContext.getCurrentUserContext();

    userContext.setDataSource(dialogMem.getSelectedDataSource());

    // notify ElasticeamService (bean), that the metamodel and model for the 'new' datasource needs to be loaded
    elasticService.initOrReload();

    LOGGER.debug("Set configuration option data source to {0}", userContext.getDataSource());

    if (dialogMem.isDoReset()) {
      GuiContext.getCurrentGuiContext().resetAllDialogs();
      LOGGER.debug("Resetting application");
      return resetPage;
    }

    updateGuiContext(dialogMem);
    return initPage;
  }

  @RequestMapping
  public String saveConfiguration(@ModelAttribute(DIALOG_MEMORY_LABEL) ConfigurationDialogMemory dialogMem, HttpSession session,
                                  HttpServletRequest req) {

    if (!UserContext.getCurrentUserContext().getPerms().getUserHasDialogPermission(getDialogName())) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }

    UserContext userContext = UserContext.getCurrentUserContext();

    userContext.setShowInactiveStatus(dialogMem.isShowInactive());

    LOGGER.debug("Set configuration option 'showInactive' to {0}", Boolean.valueOf(userContext.isShowInactiveStatus()));

    updateGuiContext(dialogMem);
    return initPage;
  }

  /**
   * Clears the Hibernate second-level cache.
   * 
   * @return the init page
   */
  @RequestMapping
  public String clearHibernateCache() {
    Settings settings = sessionFactory.getSettings();
    if (settings.isSecondLevelCacheEnabled()) {
      Cache cache = sessionFactory.getCache();
      cache.evictEntityRegions();
      cache.evictCollectionRegions();
    }

    if (settings.isQueryCacheEnabled()) {
      Cache cache = sessionFactory.getCache();
      cache.evictDefaultQueryRegion();
      cache.evictQueryRegions();
    }

    return initPage;
  }

}
