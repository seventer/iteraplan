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
package de.iteratec.iteraplan.presentation.dialog.Restart;

import static de.iteratec.iteraplan.presentation.SessionConstants.GUI_CONTEXT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.DataSourceService;
import de.iteratec.iteraplan.businesslogic.service.ElasticMiContextAndStakeholderManagerInitializationService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.SessionConstants;
import de.iteratec.iteraplan.presentation.dialog.CommonFrontendServiceImpl;


@Service("restartFrontendService")
public class RestartFrontendServiceImpl extends CommonFrontendServiceImpl {

  @Autowired
  private DataSourceService                                          dataSourceService;

  @Autowired
  private ElasticMiContextAndStakeholderManagerInitializationService elasticMiInitializationService;

  private static final Logger                                        LOGGER = Logger.getIteraplanLogger(RestartFrontendServiceImpl.class);

  /**
   * Restart iteraplan
   */
  public void restart(RequestContext context) {

    // remove the GUI context from the session. otherwise the ContextInterceptor would restore it
    // later from there
    context.getExternalContext().getSessionMap().remove(GUI_CONTEXT);

    resetGuiContext(context);
    resetUserContext();
    resetElasticMiContext(context);
  }

  private void resetGuiContext(RequestContext context) {
    GuiContext guiContext = new GuiContext();
    // initialize menu status, first menu section should be expanded by default
    Boolean[] expandedMenuStatus = { Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE };
    guiContext.setExpandedMenuStatus(expandedMenuStatus);

    context.getExternalContext().getSessionMap().put(GUI_CONTEXT, guiContext);
    GuiContext.setCurrentGuiContext(guiContext);
  }

  private void resetUserContext() {
    String key = getUserContext().getDataSource();

    // Here the data source has to point explicitly to the MASTER data source because this command
    // gets called when the reset event is sent. Thus, without changing the data source, the
    // validateKey()-method (see below) uses the currently assigned data source.
    // This must not happen, because the data accessed in that method only exists in the MASTER
    // database.
    getUserContext().setDataSource(Constants.MASTER_DATA_SOURCE);
    try {
      dataSourceService.initializeDBwithKey(key);
    } catch (IteraplanTechnicalException ex) {
      LOGGER.error("Exception: Validation of data source.", ex);
      throw ex;
    }
    // Reset the key to the data source stored in the user context.
    getUserContext().setDataSource(key);
  }

  private void resetElasticMiContext(RequestContext requestContext) {
    elasticMiInitializationService.initializeMiContextAndStakeholderManager(getUserContext().getLoginName(), getUserContext().getDataSource());
    requestContext.getExternalContext().getSessionMap().put(SessionConstants.ELASTIC_MI_CONTEXT, ElasticMiContext.getCurrentContext());
  }

  private UserContext getUserContext() {
    return UserContext.getCurrentUserContext();
  }

  @Override
  protected String getFlowId() {
    return Dialog.RESTART.getFlowId();
  }

}