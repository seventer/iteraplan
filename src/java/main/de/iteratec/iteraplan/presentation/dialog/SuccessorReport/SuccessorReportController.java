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
package de.iteratec.iteraplan.presentation.dialog.SuccessorReport;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.businesslogic.service.ExportService;
import de.iteratec.iteraplan.businesslogic.service.ReleaseSuccessorService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO.SuccessionContainer;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;
import de.iteratec.iteraplan.presentation.views.ExcelView;


@Controller
public class SuccessorReportController extends GuiController {

  private static final String     DIALOG_MEMORY_LABEL = "dialogMemory";

  @Autowired
  private ReleaseSuccessorService successorService;

  @Autowired
  private ExportService           exportService;

  @Override
  @RequestMapping
  public void init(ModelMap model, HttpSession session, HttpServletRequest request) {

    super.init(model, session, request);

    GuiContext context = GuiContext.getCurrentGuiContext();

    // get new list of IS with Successors

    ReleaseSuccessorDialogMemory dialogMemory;
    if (context.hasDialogMemory(getDialogName())) {
      // if the page was already accessed once, use the existing dialogMemory
      dialogMemory = (ReleaseSuccessorDialogMemory) context.getDialogMemory(getDialogName());
    }
    else {
      // Create new dialogMemory fill it with the information and store it in the model
      dialogMemory = new ReleaseSuccessorDialogMemory();
    }

    fillDialogMemory(dialogMemory);

    model.addAttribute(DIALOG_MEMORY_LABEL, dialogMemory);
    this.updateGuiContext(dialogMemory);
  }

  private void fillDialogMemory(ReleaseSuccessorDialogMemory dialogMemory) {
    if (UserContext.getCurrentPerms().userHasFunctionalPermission(TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE)) {
      ReleaseSuccessorDTO<InformationSystemRelease> isrDTO = successorService.getIsReleaseSuccessorDTO(null, true);
      dialogMemory.setIsrSuccessorDTO(isrDTO);
    }
    else {
      dialogMemory.setIsrSuccessorDTO(null);
    }

    if (UserContext.getCurrentPerms().userHasFunctionalPermission(TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES)) {
      ReleaseSuccessorDTO<TechnicalComponentRelease> tcrDTO = successorService.getTcReleaseSuccessorDTO(null, true);
      dialogMemory.setTcrSuccessorDTO(tcrDTO);
    }
    else {
      dialogMemory.setTcrSuccessorDTO(null);
    }
  }

  @RequestMapping
  public ModelAndView requestReport(@ModelAttribute(DIALOG_MEMORY_LABEL) ReleaseSuccessorDialogMemory dialogMemory, HttpSession session,
                                    HttpServletRequest request) {

    // extract the settings
    boolean isrReportRequested = ReleaseSuccessorDialogMemory.CLICKED_BUTTON_ISR.equals(dialogMemory.getClickedButton());
    boolean tcrReportRequested = ReleaseSuccessorDialogMemory.CLICKED_BUTTON_TCR.equals(dialogMemory.getClickedButton());
    String applicationUrl = URLBuilder.getApplicationURL(request);

    if (isrReportRequested) {
      return requestInformationSystemReport(dialogMemory, applicationUrl);
    }
    else if (tcrReportRequested) {
      return requestTechnicalComponentReport(dialogMemory, applicationUrl);
    }

    // fallback: return MaV, use Tiles/JSP-Views
    return new ModelAndView();
  }

  private ModelAndView requestTechnicalComponentReport(ReleaseSuccessorDialogMemory dialogMemory, String applicationUrl) {
    Integer releaseId = dialogMemory.getSelectedTcrId();
    boolean showSuccessor = dialogMemory.getTcrSuccessorDTO().isShowSuccessor();
    
    TypeOfBuildingBlock buildingBlockType = TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE;

    String selectedResultFormat = dialogMemory.getTcrSuccessorDTO().getSelectedResultFormat();

    // re-initialize the dialog memory (Spring MVC passed us in a new, uninitialized instance)
    fillDialogMemory(dialogMemory);

    ReleaseSuccessorDTO<TechnicalComponentRelease> dto = successorService.getTcReleaseSuccessorDTO(releaseId, showSuccessor);
    dto.setShowSuccessor(showSuccessor);
    dialogMemory.setTcrSuccessorDTO(dto);
    dialogMemory.setSelectedTcrId(releaseId);
    
    if (isExcelExportRequested(selectedResultFormat)) {
      List<SuccessionContainer<? extends Sequence<?>>> succession = Lists.newArrayList();
      for (SuccessionContainer<TechnicalComponentRelease> container : dto.getSuccession()) {
        succession.add(container);
      }
      
      TemplateType templateType = getExcelTemplateTypeByResultFormat(selectedResultFormat);

      ExportWorkbook workbook = exportService.getReleaseSuccessorExcelExport(succession, buildingBlockType, applicationUrl, templateType);
      dialogMemory.setExcelWorkbook(workbook);
      return new ModelAndView(new ExcelView(), new ModelMap(DIALOG_MEMORY_LABEL, dialogMemory));
    }
  
    // Store the dialogMemory in the GuiContext
    updateGuiContext(dialogMemory);

    // return MaV, use Tiles/JSP-Views
    return new ModelAndView();
  }
  
  private ModelAndView requestInformationSystemReport(ReleaseSuccessorDialogMemory dialogMemory, String applicationUrl) {
    Integer releaseId = dialogMemory.getSelectedIsrId();
    boolean showSuccessor = dialogMemory.getIsrSuccessorDTO().isShowSuccessor();

    TypeOfBuildingBlock buildingBlockType = TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE;

    String selectedResultFormat = dialogMemory.getIsrSuccessorDTO().getSelectedResultFormat();
    
    // re-initialize the dialog memory (Spring MVC passed us in a new, uninitialized instance)
    fillDialogMemory(dialogMemory);

    ReleaseSuccessorDTO<InformationSystemRelease> dto = successorService.getIsReleaseSuccessorDTO(releaseId, showSuccessor);
    dto.setShowSuccessor(showSuccessor);
    dialogMemory.setIsrSuccessorDTO(dto);
    dialogMemory.setSelectedIsrId(releaseId);

    if (isExcelExportRequested(selectedResultFormat)) {
      List<SuccessionContainer<? extends Sequence<?>>> succession = Lists.newArrayList();
      for (SuccessionContainer<InformationSystemRelease> container : dto.getSuccession()) {
        succession.add(container);
      }
      
      TemplateType templateType = getExcelTemplateTypeByResultFormat(selectedResultFormat);
      
      ExportWorkbook workbook = exportService.getReleaseSuccessorExcelExport(succession, buildingBlockType, applicationUrl, templateType);
      dialogMemory.setExcelWorkbook(workbook);
      return new ModelAndView(new ExcelView(), new ModelMap(DIALOG_MEMORY_LABEL, dialogMemory));
    }

    // Store the dialogMemory in the GuiContext
    updateGuiContext(dialogMemory);

    // return MaV, use Tiles/JSP-Views
    return new ModelAndView();
  }

  private boolean isExcelExportRequested(String selectedResultFormat) {
    
    if(Constants.REPORTS_EXPORT_EXCEL_2003.equals(selectedResultFormat) || Constants.REPORTS_EXPORT_EXCEL_2007.equals(selectedResultFormat)) {
      return true;
    }
    return false;
  }
   
  private TemplateType getExcelTemplateTypeByResultFormat(String selectedResultFormat) {
    
    if(Constants.REPORTS_EXPORT_EXCEL_2007.equals(selectedResultFormat)) {
      return TemplateType.EXCEL_2007;
    }
    else {
      return TemplateType.EXCEL_2003;
    }

  }

  @Override
  protected String getDialogName() {
    return Dialog.SUCCESSOR_REPORTS.getDialogName();
  }
}
