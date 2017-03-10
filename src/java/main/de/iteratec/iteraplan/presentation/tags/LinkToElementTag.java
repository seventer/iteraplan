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
package de.iteratec.iteraplan.presentation.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.dto.PermissionAttrTypeGroupDTO;
import de.iteratec.iteraplan.model.dto.SearchRowDTO;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.presentation.GuiContext;


/**
 * This tag creates a URI or a JavaScript expression that, when activated, forwards the user to the
 * management page of a supported object. The object is passed to the tag via the 'name' and
 * 'property' attribute (analogous to the itera:define tag). Currently the following classes are
 * supported:
 * <ul>
 * <li>{@link AttributeType}</li>
 * <li>{@link AttributeTypeGroup}</li>
 * <li>{@link BuildingBlock}</li>
 * <li>{@link PermissionAttrTypeGroup}</li>
 * <li>{@link PermissionAttrTypeGroupDTO}</li>
 * <li>{@link Role}</li>
 * <li>{@link User}</li>
 * <li>{@link UserGroup}</li>
 * <li>{@link SearchRowDTO}</li>
 * </ul>
 * The {@link LinkToElementTag#isrOnLeftHandSide} attribute is optional. It is only relevant when
 * linking to a building block of type {@link de.iteratec.iteraplan.model.InformationSystemInterface}. For this purpose, this
 * attribute contains the ID of the {@link de.iteratec.iteraplan.model.InformationSystemRelease} that is to be displayed on the
 * left hand side.
 * <p>
 * In order for this tag to work, certain condition have to be met:
 * <p>
 * The {@code changeLocation} and {@code createHiddenField} JavaScript method have to be
 * accessible from the JSP page where this tag is used.
 * </p>
 */
public class LinkToElementTag extends TagSupport {

  private static final long serialVersionUID = 8542694545036711606L;

  private static final Logger LOGGER      = Logger.getIteraplanLogger(LinkToElementTag.class);

  private String              name;

  private String              property;

  private String              isrOnLeftHandSide;

  // the type of the link that will be returned. (javascript methods/ plain html-address etc)
  private String              type;

  /** the servlet context name, i.e. the application deployment name as found in the URI */
  private String              context;

  private static final int    BUFFER_SIZE = 150;

  private enum Types {
    HTML, JS, JSON
  }

  @Override
  public int doStartTag() throws JspException {

    // look up the requested property value
    Object object = TagUtils.lookup(pageContext, name, property, null);
    if (object == null) {
      // nothing to output
      return SKIP_BODY;
    }

    this.context = pageContext.getServletContext().getContextPath();

    String activeDialogName = GuiContext.getCurrentGuiContext().getActiveDialogName();

    if (!(object instanceof IdEntity)) {
      LOGGER.info("Objects of type '{0}' are currently not supported for linking.", object.getClass().getSimpleName());

      // nothing to output
      return SKIP_BODY;
    }

    // downcast to the interface understood by most special methods
    IdEntity entity = (IdEntity) object;
    if (entity instanceof BuildingBlock) {
      buildOutputForFlow(entity, null);
    }
    else if (entity instanceof AttributeType) {
      buildOutputForFlow(entity, null);
    }
    else if (entity instanceof Role) {
      buildOutputForFlow(entity, null);
    }
    else if (entity instanceof User || entity instanceof UserGroup) {
      // if the current page is object related permissions, instead of linking to the
      // user/usergroup, link to their object related permissions
      if (Dialog.OBJECT_RELATED_PERMISSION.getDialogName().equals(activeDialogName)) {
        buildOutputForFlow(entity, UserEntity.class);
      }
      else {
        buildOutputForFlow(entity, null);
      }
    }
    else if (entity instanceof AttributeTypeGroup) {
      buildOutputForMVC(entity);
    }
    else if (entity instanceof PermissionAttrTypeGroup) {
      PermissionAttrTypeGroup group = (PermissionAttrTypeGroup) entity;
      linkPermissionAttrTypeGroup(group);
    }
    else if (entity instanceof PermissionAttrTypeGroupDTO) {
      PermissionAttrTypeGroupDTO group = ((PermissionAttrTypeGroupDTO) entity);
      linkPermissionAttrTypeGroupDTO(group);
    }
    else if (object instanceof SearchRowDTO) {
      SearchRowDTO searchRowDTO = (SearchRowDTO) object;
      TypeOfBuildingBlock typeOfBB = TypeOfBuildingBlock.fromPropertyString(searchRowDTO.getBuildingBlockType());
      buildOutputForFlow(searchRowDTO, typeOfBB.getAssociatedClass());
    }
    else {
      LOGGER.info("Objects of type '{0}' are currently not supported for linking.", object.getClass().getSimpleName());
    }

    // continue processing this page
    return SKIP_BODY;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    try {
      this.name = TagUtils.evalString("name", this.name, this, pageContext);
    } catch (JspException e) {
      LOGGER.error(e);
    }
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getProperty() {
    return property;
  }

  public void setProperty(String property) {
    this.property = property;
    try {
      this.property = TagUtils.evalString("property", this.property, this, pageContext);
    } catch (JspException e) {
      LOGGER.error(e);
    }
  }

  public String getIsrOnLeftHandSide() {
    return isrOnLeftHandSide;
  }

  public void setIsrOnLeftHandSide(String id) {
    isrOnLeftHandSide = id;
    try {
      isrOnLeftHandSide = TagUtils.evalString("isrOnLeftHandSide", isrOnLeftHandSide, this, pageContext);
    } catch (JspException e) {
      LOGGER.error(e);
    }
  }

  private void linkPermissionAttrTypeGroup(PermissionAttrTypeGroup toLink) throws JspException {

    String activeDialogName = GuiContext.getCurrentGuiContext().getActiveDialogName();

    // if the active dialog is the attribute type group dialog, link to the role dialog.
    if (Dialog.ATTRIBUTE_TYPE_GROUP.getDialogName().equals(activeDialogName)) {
      buildOutputForFlow(toLink.getRole(), null);
    }
    // if the active dialog is the role dialog, link to the attribute type group dialog.
    else if (Dialog.ROLE.getDialogName().equals(activeDialogName)) {
      buildOutputForMVC(toLink.getAttrTypeGroup());
    }
    else {
      LOGGER.warn("Could not create Link. activeDialogName is not set!");
    }

  }

  private void linkPermissionAttrTypeGroupDTO(PermissionAttrTypeGroupDTO toLink) throws JspException {

    String activeDialogName = GuiContext.getCurrentGuiContext().getActiveDialogName();

    // if the active dialog is the attribute type group dialog, link to the role dialog.
    if (Dialog.ATTRIBUTE_TYPE_GROUP.getDialogName().equals(activeDialogName)) {
      buildOutputForFlow(toLink.getPermission().getRole(), null);
    }

    // if the active dialog is the role dialog, link to the attribute type group dialog.
    else if (Dialog.ROLE.getDialogName().equals(activeDialogName)) {
      buildOutputForMVC(toLink.getPermission().getAttrTypeGroup());
    }
    else {
      LOGGER.warn("Could not create Link. activeDialogName is not set!");
    }
  }

  /**
   * Builds a String representation of a Link to the flow-page of the entity.
   * Depending on the type a URL to the flow entry point or javascript method calls will be written
   * to the page context.
   * 
   * @param entity
   *          the model object to link to. The corresponding dialog is determined by entity's class
   * @param targetClassHint
   *          <code>null</code> (the most common case!), or an alternative class to use in order to
   *          determine the relevant dialog. If such a class is passed, dialog auto-detection is overridden.
   */
  private void buildOutputForFlow(IdEntity entity, Class<? extends IdEntity> targetClassHint) throws JspException {
    if (type == null) {
      return;
    }
    Types requestedLinkType = Types.valueOf(type.toUpperCase());

    switch (requestedLinkType) {
      case HTML:
        String relativeUrl = (targetClassHint == null ? URLBuilder.getRelativeURLforFlow(context, entity) :
          URLBuilder.getRelativeURLforFlow(context, entity, targetClassHint));
        TagUtils.write(pageContext, relativeUrl);
        break;

      case JS:
        StringBuilder outputBuffer = new StringBuilder(BUFFER_SIZE);
        outputBuffer.append("changeLocation('");
        if (targetClassHint == null) {
          outputBuffer.append(URLBuilder.getRelativeURLforFlow(context, entity));
        }
        else {
          outputBuffer.append(URLBuilder.getRelativeURLforFlow(context, entity, targetClassHint));
        }
        outputBuffer.append("');");
        TagUtils.write(pageContext, outputBuffer.toString());
        break;

      case JSON:
        if (! (entity instanceof IdentityEntity)) {
          LOGGER.warn("Cannot create JSON URL for entities that don't implement IdentityEntity: {0}", entity.getClass().getName());
          break;
        }
        String jsonEntityUrl = URLBuilder.getEntityURL((IdentityEntity) entity, GuiContext.getCurrentRequest(), URLBuilder.EntityRepresentation.JSON);
        TagUtils.write(pageContext, jsonEntityUrl);
        break;

      default:
        LOGGER.info("URL generation for links type {0} has not been implemented yet", requestedLinkType);
    }


  }

  /**
   * Builds a String representation of a Link to the MVC-page of the dialog/objectId combination.
   * Depending on the type a URL or javascript method calls will be written
   * to the page context.
   * 
   * @param dialog
   *          the Dialog name to link to (e.g. 'attributetype').
   * @param objectId
   *          the ID of the element to link to
   */
  private void buildOutputForMVC(IdEntity entity) throws JspException {
    if (type == null) {
      return;
    }
    Types requestedLinkType = Types.valueOf(type.toUpperCase());

    switch (requestedLinkType) {
      case HTML:
        TagUtils.write(pageContext, URLBuilder.getRelativeURLforMVC(context, entity));
        break;

      case JS:
        StringBuilder outputBuffer = new StringBuilder(BUFFER_SIZE);
        outputBuffer.append("changeLocation('");
        outputBuffer.append(URLBuilder.getRelativeURLforMVC(context, entity));
        outputBuffer.append("');");
        TagUtils.write(pageContext, outputBuffer.toString());
        break;

      default:
        LOGGER.info("Never to expected to create a JSON URI for an entity that is processed with MVC pages: {0}", entity.getClass().getName());
    }
  }

}