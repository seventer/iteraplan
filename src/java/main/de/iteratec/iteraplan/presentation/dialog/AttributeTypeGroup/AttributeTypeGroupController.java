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
package de.iteratec.iteraplan.presentation.dialog.AttributeTypeGroup;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;


/**
 * This Controller handles all simple standard actions in the AttributeTypeGroup context, that are
 * not handled by Spring WebFlow.
 */
@Controller
public class AttributeTypeGroupController extends GuiController {

  private static final Logger       LOGGER         = Logger.getIteraplanLogger(AttributeTypeGroupController.class);
  private static final Integer      NONEXISTENT_ID = Integer.valueOf(-1);

  @Autowired
  private AttributeTypeGroupService attributeTypeGroupService;

  @Override
  protected String getDialogName() {
    return Dialog.ATTRIBUTE_TYPE_GROUP.getDialogName();
  }

  public void setAttributeTypeGroupService(AttributeTypeGroupService attributeTypeGroupService) {
    this.attributeTypeGroupService = attributeTypeGroupService;
  }

  /**
   * This method is used to initialize the view. If the query string parameter ?id= is present, the
   * corresponding attributeTypeGroup is fetched and shown. Otherwise the default attributeTypeGroup
   * is shown.
   * 
   * @param atgId
   *          the id of the attributeTypeGroup to show
   * @param model
   * @param session
   * @param request
   */
  @RequestMapping(method = RequestMethod.GET)
  public void init(@RequestParam(value = "id", required = false) String atgId, ModelMap model, HttpSession session, HttpServletRequest request) {
    super.init(model, session, request);

    GuiContext guiContext = GuiContext.getCurrentGuiContext();
    AttributeTypeGroupDialogMemory atgDialogMemory;
    AttributeTypeGroup atg = null;
    Integer id = NONEXISTENT_ID;

    // if an id is provided, try to get the associated attributeTypeGroup
    // if no queryString (?id=...) is provided, atgId will be null
    if (atgId != null) {
      try {
        id = Integer.valueOf(atgId);
      } catch (NumberFormatException e) {
        LOGGER.debug("Parsing of "+atgId+" failed, setting it to "+NONEXISTENT_ID, e);
        id = NONEXISTENT_ID;
      }
    }

    // if the query-param is non existent or null, look up the dialogMemory
    if (id.equals(NONEXISTENT_ID)) {
      // try to get the DialogMemory for the AttributeTypeGroup dialog
      if (guiContext.hasDialogMemory(getDialogName())) {
        atgDialogMemory = (AttributeTypeGroupDialogMemory) guiContext.getDialogMemory(getDialogName());
      }
      else {
        // create a new one with nonexistent id
        atgDialogMemory = new AttributeTypeGroupDialogMemory();
        atgDialogMemory.setSelectedAttributeTypeGroup(NONEXISTENT_ID);
      }
      // get the selected AttributeTypeGroup from the dialogMemory
      atg = attributeTypeGroupService.loadObjectByIdIfExists(atgDialogMemory.getSelectedAttributeTypeGroup());
    }
    // otherwise get the selected AttributeTypeGroup from the queryId
    else {
      atg = attributeTypeGroupService.loadObjectByIdIfExists(id);
    }

    // if no associated attributeTypeGroup could be found or if the id was not provided get the
    // standard attributeTypegroup
    if (atg == null) {
      atg = attributeTypeGroupService.getStandardAttributeTypeGroup();
    }

    AttributeTypeGroupMemBean memBean = new AttributeTypeGroupMemBean();
    memBean.getComponentModel().initializeFrom(atg);

    model.addAttribute("memBean", memBean);

    // save DialogMemory to GuiContext
    atgDialogMemory = new AttributeTypeGroupDialogMemory();
    atgDialogMemory.setSelectedAttributeTypeGroup(atg.getId());
    updateGuiContext(atgDialogMemory);
  }

  /**
   * Refreshes the view when another AttributeTypeGroup is selected.
   * 
   * @param memBean
   * @param model
   * @param session
   * @throws IOException
   */
  @RequestMapping(method = RequestMethod.POST)
  public void refresh(@ModelAttribute("memBean") AttributeTypeGroupMemBean memBean, Model model, HttpSession session, HttpServletResponse response)
      throws IOException {
    AttributeTypeGoupSelectionComponentModel cm = memBean.getComponentModel().getChooseAttributeTypeGroupComponentModel();
    Integer currentId = cm.getCurrentId();

    // if the AttributeTypeGroup should be moved
    if (!StringUtils.isEmpty(cm.getAction())) {
      AttributeTypeGroup entity = attributeTypeGroupService.loadObjectByIdIfExists(currentId);

      if (entity == null) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ);
      }

      cm.update();
      final Integer position = cm.getPosition();
      // only update the position, if it has changed at all.
      if (position != null && !entity.getPosition().equals(position)) {
        // this command requires only that the position has to updated. it
        // does not care about other properties (such as name, description,
        // attribute types and permissions for roles) because changing
        // positions of attribute type groups may only happen in READ mode.
        attributeTypeGroupService.updatePosition(entity, position);
      }

      model.addAttribute("memBean", memBean);
    }

    AttributeTypeGroupDialogMemory atgDialogMemory = new AttributeTypeGroupDialogMemory();
    atgDialogMemory.setSelectedAttributeTypeGroup(currentId);
    updateGuiContext(atgDialogMemory);

    response.sendRedirect("init.do?id=" + currentId);
  }
}
