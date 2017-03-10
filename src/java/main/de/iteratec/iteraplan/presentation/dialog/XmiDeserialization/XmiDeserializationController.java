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
package de.iteratec.iteraplan.presentation.dialog.XmiDeserialization;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiImportConflictResolver;
import de.iteratec.iteraplan.businesslogic.exchange.xmi.importer.XmiImportService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;
import de.iteratec.iteraplan.presentation.responsegenerators.XMIResponseGenerator;


@Controller
@SessionAttributes({ "dialogMemory" })
public class XmiDeserializationController extends GuiController {
  private static final Logger  LOGGER           = Logger.getIteraplanLogger(XmiDeserializationController.class);

  private static final String  XMI_FILENAME     = "iteraplanModel.xmi";
  private static final String  ECORE_FILENAME   = "iteraplanMetamodel.ecore";
  private static final String  ZIP_FILENAME     = "iteraplanXMIExport.zip";
  private static final String  INIT_VIEW        = "xmideserialization/init";

  public static final String   CONTENT_TYPE_XMI = "application/xml";
  public static final String   CONTENT_TYPE_ZIP = "application/zip";

  @Autowired
  private XmiImportService     xmiDeserializerService;

  @Autowired
  private XMIResponseGenerator xmiGenerator;

  @Override
  public String getDialogName() {
    return Dialog.XMISERIALIZATION.getDialogName();
  }

  @Override
  @RequestMapping(method = RequestMethod.GET)
  public void init(ModelMap model, HttpSession session, HttpServletRequest req) {
    super.init(model, session, req);
    model.addAttribute("componentMode", "READ");

    LOGGER.debug("XmiDeserializationController#init");

    UserContext userContext = UserContext.getCurrentUserContext();
    userContext.getPerms().assureFunctionalPermission(TypeOfFunctionalPermission.XMISERIALIZATION);
    GuiContext guiCtx = GuiContext.getCurrentGuiContext();

    XmiDeserializationDialogMemory dialogMem;
    if (guiCtx.hasDialogMemory(this.getDialogName())) {
      dialogMem = (XmiDeserializationDialogMemory) guiCtx.getDialogMemory(this.getDialogName());
    }
    else {
      dialogMem = new XmiDeserializationDialogMemory();
    }
    // re-initialize the dialog memory, resetting its state
    dialogMem.reInit();

    model.addAttribute("dialogMemory", dialogMem);
    updateGuiContext(dialogMem);
  }

  @RequestMapping(method = RequestMethod.POST)
  public String onSubmit(@ModelAttribute("dialogMemory") XmiDeserializationDialogMemory dialogMem, ModelMap model, HttpSession session,
                         MultipartHttpServletRequest req, HttpServletResponse response) {

    UserContext userContext = UserContext.getCurrentUserContext();
    userContext.getPerms().assureFunctionalPermission(TypeOfFunctionalPermission.XMISERIALIZATION);
    response.reset();

    dialogMem.setImportSuccessful(false);
    try {
      if (dialogMem.getClickedButton().equals("button.import.xmi.hide_conflicts")) {
        dialogMem.setConflicts(null);
        return INIT_VIEW;
      }
      else if (dialogMem.getClickedButton().equals("button.xmi.solve_and_import")) {
        XmiImportConflictResolver resolver = new XmiImportConflictResolver(XMI_FILENAME);
        resolver.resolveConflicts(dialogMem.getConflicts());
        MultipartFile file = req.getFile("xmiFile");
        xmiDeserializerService.importXmi(file.getInputStream());

        dialogMem.setConflicts(null);
        if (response.containsHeader("updatedXmi")) {
          response.sendRedirect(INIT_VIEW);
          response.getOutputStream().close();
        }
      }
      if (!dialogMem.getClickedButton().equals("button.import.xmi") && !dialogMem.getClickedButton().equals("button.xmi.solve_and_import")) {
        dialogMem.setXmiFileNull(false);
        dialogMem.setWrongFileType(false);
        dialogMem.setConflicts(null);
      }
      if (dialogMem.getClickedButton().equals("button.import.xmi")) {
        userContext.getPerms().assureFunctionalPermission(TypeOfFunctionalPermission.XMIDESERIALIZATION);
        MultipartFile file = req.getFile("xmiFile");
        if (file == null || file.getSize() == 0) {
          dialogMem.setXmiFileNull(true);
          dialogMem.setWrongFileType(false);
          dialogMem.setConflicts(null);
          return INIT_VIEW;
        }
        else {
          dialogMem.setXmiFileNull(false);
        }

        if (!file.getOriginalFilename().endsWith(".xmi")) {
          dialogMem.setWrongFileType(true);
          dialogMem.setXmiFileNull(false);
          dialogMem.setConflicts(null);
          return INIT_VIEW;
        }
        else {
          dialogMem.setWrongFileType(false);
        }

        xmiDeserializerService.importXmi(file.getInputStream());

        dialogMem.setConflicts(null);
        dialogMem.setImportSuccessful(true);
        if (response.containsHeader("updatedXmi")) {
          response.sendRedirect(INIT_VIEW);
          response.getOutputStream().close();
        }
      }
      if (dialogMem.getClickedButton().equals("button.export.xmi")) {
        xmiSerialization(response);
        response.getOutputStream().close();
      }
      if (dialogMem.getClickedButton().equals("button.export.xmi.extended")) {
        customizedXMLExport(response);
        response.getOutputStream().close();
      }
      if (dialogMem.getClickedButton().equals("button.export.ecore")) {
        ecoreSerialization(response, false);
        response.getOutputStream().close();
      }
      if (dialogMem.getClickedButton().equals("button.export.xmi.zip")) {
        zipBundle(response);
        response.getOutputStream().close();
      }
      if (dialogMem.getClickedButton().equals("button.export.ecore.modified")) {
        ecoreSerialization(response, true);
        response.getOutputStream().close();
      }
      if (dialogMem.getClickedButton().equals("button.import.xmi_restore")) {
        return manageRestoreTEMP(response);
      }
    } catch (IOException e) {
      LOGGER.error(e);
    }
    return INIT_VIEW;
  }

  private String manageRestoreTEMP(HttpServletResponse response) {
    try {
      response.setContentType("application/txt");
      response.setHeader("Content-disposition", "attachment;fileName=underConstruction.txt");
      response.getOutputStream().print("coming soon...");
      response.getOutputStream().close();
    } catch (IOException e) {
      LOGGER.error(e);
    }
    return INIT_VIEW;
  }

  /**
   * @see XMIResponseGenerator
   */
  private void xmiSerialization(HttpServletResponse response) {
    try {
      xmiGenerator.generateXmiResponseForExport(response, XMI_FILENAME);
      response.sendRedirect(INIT_VIEW);
    } catch (IOException e) {
      LOGGER.error("Failed to write xmi file to the response", e);
    }
  }

  private void customizedXMLExport(HttpServletResponse response) {
    try {
      xmiGenerator.generateCompleteXmlExport(response, "iteraplanXMI.xml");
      response.sendRedirect(INIT_VIEW);
    } catch (IOException e) {
      LOGGER.error("Failed to write xml file to the response", e);
    }
  }

  /**
   * @see XMIResponseGenerator
   */
  private void ecoreSerialization(HttpServletResponse response, boolean modified) {
    try {
      if (modified) {
        xmiGenerator.generateEcoreResponseForTabularReporting(response, "IteraplanModelForTabularReporting_extended.ecore");
      }
      else {
        xmiGenerator.generateEcoreForExport(response, ECORE_FILENAME);

      }
      response.sendRedirect(INIT_VIEW);
    } catch (IOException e) {
      LOGGER.error("Failed to write ecore file to the response", e);
    }
  }

  /**
   * @see XMIResponseGenerator
   */
  private void zipBundle(HttpServletResponse response) {
    xmiGenerator.generateXmiAndEcoreZipBundle(response, ZIP_FILENAME);

    try {
      response.sendRedirect(INIT_VIEW);
    } catch (IOException e) {
      LOGGER.error("Failed to write zip file to the response", e);
    }
  }

  protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
    binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
  }

}
