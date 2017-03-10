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
package de.iteratec.iteraplan.presentation.dialog.Templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.exchange.customDashboard.CustomDashboardTemplatesDialogMemory;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateLocatorService;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplatesManagerService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.CustomDashboardInstanceService;
import de.iteratec.iteraplan.businesslogic.service.CustomDashboardTemplateService;
import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.queries.CustomDashboardInstance;
import de.iteratec.iteraplan.model.queries.CustomDashboardTemplate;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;


@Controller
@SessionAttributes({ "dialogMemory" })
public class TemplatesController extends GuiController {
  private static final Logger            LOGGER    = Logger.getIteraplanLogger(TemplatesController.class);

  private static final String            INIT_VIEW = "templates/init";

  @Autowired
  private TemplatesManagerService        templatesManagerService;

  @Autowired
  private TemplateLocatorService         templateLocatorService;

  @Autowired
  private BuildingBlockTypeService       bbTypeService;

  @Autowired
  private SavedQueryService              savedQueryService;

  @Autowired
  private CustomDashboardInstanceService customDashboardInstanceService;

  @Autowired
  private CustomDashboardTemplateService customDashboardTemplateService;

  @Override
  public String getDialogName() {
    return Dialog.TEMPLATES.getDialogName();
  }

  @Override
  @RequestMapping(method = RequestMethod.GET)
  public void init(ModelMap model, HttpSession session, HttpServletRequest req) {
    super.init(model, session, req);
    model.addAttribute("componentMode", "READ");

    LOGGER.debug("TemplatesController#init");

    UserContext userContext = UserContext.getCurrentUserContext();
    userContext.getPerms().assureFunctionalPermission(TypeOfFunctionalPermission.TEMPLATES);
    GuiContext guiCtx = GuiContext.getCurrentGuiContext();

    TemplatesDialogMemory dialogMem;
    if (guiCtx.hasDialogMemory(this.getDialogName())) {
      dialogMem = (TemplatesDialogMemory) guiCtx.getDialogMemory(this.getDialogName());
    }
    else {
      dialogMem = new TemplatesDialogMemory();
    }
    refillDialogMem(dialogMem);

    initCustomDashboardDialogMemory(dialogMem);

    model.addAttribute("dialogMemory", dialogMem);
    updateGuiContext(dialogMem);
  }

  private void refillDialogMem(TemplatesDialogMemory dialogMem) {
    // re-initialize the dialog memory, resetting its state
    dialogMem.reInit();

    for (TemplateType type : dialogMem.getAvailableTypes()) {
      dialogMem.putTemplateInfos(type, Lists.newArrayList(templateLocatorService.getTemplateInfos(type)));
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String onSubmit(@ModelAttribute("dialogMemory") TemplatesDialogMemory dialogMem, ModelMap model, HttpSession session,
                         MultipartHttpServletRequest req, HttpServletResponse response) {

    UserContext userContext = UserContext.getCurrentUserContext();
    userContext.getPerms().assureFunctionalPermission(TypeOfFunctionalPermission.TEMPLATES);
    response.reset();
    
    dialogMem.clearErrors();

    String action = dialogMem.getAction();
    if ("upload".equals(action)) {
      dialogMem.setUploadSuccessful(false);
      MultipartFile uploadedFile = uploadTemplate(dialogMem, req);
      if (uploadedFile == null) {
        return INIT_VIEW;
      }
      save(TemplateType.getTypeFromKey(dialogMem.getTargetTemplateType()), uploadedFile);
    }
    else if ("remove".equals(action)) {
      removeTemplate(dialogMem);
    }
    else if ("download".equals(action)) {
      downloadTemplate(dialogMem, response);
    }

    /*  Custom dashboard things */
    else if ("createDashboardTemplate".equals(action)) {
      // create a new dashboard template
      if (dialogMem.getSelectedBBTypeId() != null) { // it is necessary that a bbt has been selected

        CustomDashboardTemplatesDialogMemory cdtDialogMem = dialogMem.getCustomDashboardDialogMemory();

        // load the selected building Block Type
        BuildingBlockType bbt = bbTypeService.loadObjectById(dialogMem.getSelectedBBTypeId());

        // create a new empty dashboard template with the building Block Type
        cdtDialogMem.setCustomDashboardTemplate(new CustomDashboardTemplate(bbt, "", "", ""));

        // load the saved querys for the dashboard template editor
        cdtDialogMem.setSavedQueries(savedQueryService.getAllSavedQueryForDashboards(bbt));
      }
    }
    else if ("setDashboardTemplateToEdit".equals(action)) {
      // load the custom dashboard template
      CustomDashboardTemplate customDashboardTemplate = customDashboardTemplateService.findById(dialogMem.getCustomDashboardId());
      dialogMem.getCustomDashboardDialogMemory().setCustomDashboardTemplate(customDashboardTemplate);

      // load the saved queries for the dashboard template editor
      BuildingBlockType bbt = dialogMem.getCustomDashboardDialogMemory().getCustomDashboardSelectedBBType();
      dialogMem.getCustomDashboardDialogMemory().setSavedQueries(savedQueryService.getAllSavedQueryForDashboards(bbt));
    }
    else if ("rollbackDashboardTemplate".equals(action)) {
      initCustomDashboardDialogMemory(dialogMem);
    }
    else if ("saveDashboardTemplate".equals(action)) {
      // save Dashboard Template
      customDashboardTemplateService.saveCustomDashboardTemplate(dialogMem.getCustomDashboardDialogMemory().getCustomDashboardTemplate());
      initCustomDashboardDialogMemory(dialogMem);
    }
    else if ("deleteDashboardTemplate".equals(action)) {

      CustomDashboardTemplate customDashboardTemplate = customDashboardTemplateService.findById(dialogMem.getCustomDashboardId());

      List<CustomDashboardInstance> templates = customDashboardInstanceService.getCustomDashboardByDashboardTemplate(customDashboardTemplate);
      if (!templates.isEmpty()) {
        dialogMem.addError(MessageAccess.getString("customDashboard.deleteTemplate.warning"));
      }
      else {

        if (customDashboardTemplate.equals(dialogMem.getCustomDashboardDialogMemory().getCustomDashboardTemplate())) {
          dialogMem.getCustomDashboardDialogMemory().setCustomDashboardTemplate(null);
          dialogMem.getCustomDashboardDialogMemory().setCustomDashboardSelectedBBType(null);
        }
        customDashboardTemplateService.deleteCustomDashboardTemplate(customDashboardTemplate);
        initCustomDashboardDialogMemory(dialogMem);
      }
    }
    else if ("edit".equals(action)) {
      dialogMem.getCustomDashboardDialogMemory().setSelectedTab(CustomDashboardTemplatesDialogMemory.MODE_EDIT);
    }
    else if ("preview".equals(action)) {
      dialogMem.getCustomDashboardDialogMemory().setSelectedTab(CustomDashboardTemplatesDialogMemory.MODE_PREVIEW);
    }
    else if ("metadata".equals(action)) {
      dialogMem.getCustomDashboardDialogMemory().setSelectedTab(CustomDashboardTemplatesDialogMemory.MODE_METADATA);
    }

    refillDialogMem(dialogMem);

    updateGuiContext(dialogMem);

    return INIT_VIEW;
  }

  private void initCustomDashboardDialogMemory(TemplatesDialogMemory dialogMem) {
    dialogMem.getCustomDashboardDialogMemory().init();
    dialogMem.getCustomDashboardDialogMemory().setCustomDashboardTemplates(customDashboardTemplateService.getCustomDashboardTemplate()); // init List of all custom dashboard templates
    dialogMem.getCustomDashboardDialogMemory().setBbTAvailableForNewTemplate(bbTypeService.getAllBuildingBlockTypesForDisplay());
  }

  private void downloadTemplate(TemplatesDialogMemory dialogMem, HttpServletResponse response) {
    TemplateType type = TemplateType.getTypeFromKey(dialogMem.getTargetTemplateType());
    String targetTemplateName = dialogMem.getTargetTemplateName();

    response.setHeader("Content-Disposition", "attachment; filename=\"" + targetTemplateName + "\"");
    response.setContentType(type.getMimeType());

    File fileToDownload = templateLocatorService.getFile(type, targetTemplateName);

    try {
      OutputStream out = response.getOutputStream();
      FileInputStream fin = new FileInputStream(fileToDownload);
      IOUtils.copy(fin, out);
      out.flush();
      out.close();
      fin.close();
    } catch (IOException e) {
      LOGGER.error("Could not write the file '" + targetTemplateName + "' to the response OutputStream.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }

  }

  private void removeTemplate(TemplatesDialogMemory dialogMem) {
    TemplateType type = TemplateType.getTypeFromKey(dialogMem.getTargetTemplateType());
    String templateToRemove = dialogMem.getTargetTemplateName();

    templatesManagerService.removeTemplate(type, templateToRemove);
    templateLocatorService.clearCache();
  }

  private void save(TemplateType type, MultipartFile uploadedFile) {
    templatesManagerService.saveTemplateFile(type, uploadedFile);
    templateLocatorService.clearCache();
  }

  private MultipartFile uploadTemplate(TemplatesDialogMemory dialogMem, MultipartHttpServletRequest req) {
    TemplateType uploadedType = TemplateType.getTypeFromKey(dialogMem.getTargetTemplateType());
    MultipartFile file = req.getFile(uploadedType.getNameKey() + "_file");

    if (file == null || file.getSize() == 0) {
      dialogMem.setTemplateFileNull(true);
      dialogMem.setWrongFileType(false);
      return null;
    }
    else {
      dialogMem.setTemplateFileNull(false);
    }

    String originalFilename = file.getOriginalFilename();
    String extension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf('.')) : null;
    if (extension == null || !uploadedType.getExtensions().contains(extension)) {
      dialogMem.setWrongFileType(true);
      dialogMem.setTemplateFileNull(false);
      return null;
    }
    else {
      dialogMem.setWrongFileType(false);
    }
    return file;
  }
}
