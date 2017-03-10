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
package de.iteratec.iteraplan.xmi;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiImportService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.history.HistoryEventListener;
import de.iteratec.iteraplan.setup.LocalApplicationContextUtil;


/**
 * Uses the {@link XmiImportService} to import the iteraplan data using the XMI format. The data
 * will be read from the {@code iteraplanData.xmi} file.
 * 
 * @author agu
 *
 */
public class XmiImport {

  public enum HistoryInitialization {
    USE_CONFIG_FILE, FORCE_INITIALIZE, FORCE_DONT_INITIALIZE
  }

  private static final Logger LOGGER = Logger.getIteraplanLogger(XmiImport.class);

  /**
   * The entry point for data import.
   * 
   * @param args not used
   * @throws IOException if the {@code iteraplanData.xmi} file will be not found
   */
  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      LOGGER.error("Invalid method call! Use: XmiImport INITIAL_DATA|BANK_DATA");
    }

    XmiImport xmiImport = new XmiImport();

    if (StringUtils.equals(args[0], "BANK_DATA")) {
      xmiImport.importData();
    }
    else if (StringUtils.equals(args[0], "INITIAL_DATA")) {
      xmiImport.importInitialData();
    }
    else {
      LOGGER.error("Invalid method call! Use: XmiImport INITIAL_DATA|BANK_DATA");
    }
  }

  /**
   * Imports the XMI data.
   * 
   * @throws IOException if the {@code iteraplanData.xmi} file will be not found
   */
  public void importData() throws IOException {
    LOGGER.info("Start Bank Data Import");

    ConfigurableApplicationContext context = prepareEnvironment(HistoryInitialization.USE_CONFIG_FILE);
    try {
      XmiImportService xmiDeserializer = context.getBean("xmiImportService", XmiImportService.class);

      Resource importFile = new ClassPathResource("de/iteratec/iteraplan/xmi/iteraplanData.xmi");
      xmiDeserializer.importXmi(importFile.getInputStream());

      LOGGER.info("XMI data imported successfuly");
    } finally {
      context.close();
    }
  }

  /**
   * Imports the initial XMI data.
   * 
   * @throws IOException if the {@code iteraplanData.xmi} file will be not found
   */
  public void importInitialData() throws IOException {
    importInitialData(false, HistoryInitialization.USE_CONFIG_FILE);
  }

  /**
   * Imports the initial XMI data.
   * 
   * @param importAttributeTypes flag whether basic attribute types should be imported or not
   * @param initializeHistoryData Set to true if history base data should be initialized as well
   * @throws IOException if the {@code iteraplanData.xmi} file will be not found
   */
  public void importInitialData(boolean importAttributeTypes, HistoryInitialization initializeHistoryData) throws IOException {
    LOGGER.info("Start Initial Data Import");

    ConfigurableApplicationContext context = prepareEnvironment(initializeHistoryData);
    try {
      XmiImportService xmiDeserializer = context.getBean(XmiImportService.class);

      Resource importFile = new ClassPathResource("de/iteratec/iteraplan/xmi/iteraplanData.xmi");
      xmiDeserializer.importInitialXmi(importFile.getInputStream(), importAttributeTypes);

      LOGGER.info("XMI data imported successfuly");
    } finally {
      context.close();
    }
  }

  private ConfigurableApplicationContext prepareEnvironment(HistoryInitialization initializeHistoryData) {
    createTempUserContext();
    ConfigurableApplicationContext context = LocalApplicationContextUtil.getApplicationContext();

    HistoryEventListener historyListener = context.getBean(HistoryEventListener.class);
    switch(initializeHistoryData) {
      case FORCE_INITIALIZE:
        historyListener.setHistoryEnabled(true);
        break;
      case FORCE_DONT_INITIALIZE:
        historyListener.setHistoryEnabled(false);
        break;
      default:
        // do nothing
    }

    return context;
  }

  /**
   * Creates the temporary user context to be able to import data.
   */
  private void createTempUserContext() {
    User user = new User();
    user.setLoginName("XmiImport");
    Set<Role> roles = Sets.newHashSet();

    Role role = new Role();
    role.setRoleName(Role.SUPERVISOR_ROLE_NAME);

    roles.add(role);

    UserContext userContext = new UserContext("XmiImport", roles, Locale.UK, user);
    UserContext.setCurrentUserContext(userContext);
  }
}
