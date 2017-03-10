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
package de.iteratec.iteraplan.presentation.dialog.common.model;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.hibernate.Hibernate;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.AttributesComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.permissions.UserEntityCM;


/**
 * A base component model for all {@link BuildingBlock}s. Contains fields that are common to all
 * BuildingBlocks, such as owning UserEntities, attributes and last modification information.
 * 
 * @param <T> The specific subclass of BuildingBlock.
 */
public abstract class BuildingBlockComponentModel<T extends BuildingBlock> extends AbstractComponentModelBase<T> {

  private static final long              serialVersionUID = -8015160573901535309L;

  private final UserEntityCM             owningUserEntityModel;
  private final AttributesComponentModel attributeModel;
  private final AttributesComponentModel toplevelAttributeModel;
  private T                              entity;

  public BuildingBlockComponentModel(ComponentMode componentMode) {
    super(componentMode);

    owningUserEntityModel = new UserEntityCM(componentMode, "owningUserEntity");
    // attributeModel containing all ATGs that are not ToplevelATG
    attributeModel = new AttributesComponentModel(componentMode, "attributes") {
      private static final long serialVersionUID = 7420573713305425850L;

      @Override
      public boolean showATG(AttributeTypeGroup atg) {
        return !atg.isToplevelATG().booleanValue();
      }
    };

    // propertyModel containing only ATGs that are ToplevelATGs
    toplevelAttributeModel = new AttributesComponentModel(componentMode, "toplevelAttributes") {
      private static final long serialVersionUID = 5827986062177633549L;

      @Override
      public boolean showATG(AttributeTypeGroup atg) {
        return atg.isToplevelATG().booleanValue();
      }
    };

  }

  public void initializeFrom(T source) {
    if (source == null) {
      throw new IllegalArgumentException("The element to initialize the component model must not be null");
    }

    this.entity = source;
    owningUserEntityModel.initializeFrom(source);
    attributeModel.initializeFrom(source);
    toplevelAttributeModel.initializeFrom(source);

    Hibernate.initialize(entity.getSubscribedUsers());
  }

  public void update() {
    owningUserEntityModel.update();
    attributeModel.update();
    toplevelAttributeModel.update();
  }

  public void configure(T target) {
    if (target == null) {
      throw new IllegalArgumentException("The element to configure must not be null");
    }

    owningUserEntityModel.configure(target);
    attributeModel.configure(target);
    toplevelAttributeModel.configure(target);
  }

  public AttributesComponentModel getAttributeModel() {
    return attributeModel;
  }

  public AttributesComponentModel getToplevelAttributeModel() {
    return toplevelAttributeModel;
  }

  public UserEntityCM getOwningUserEntityModel() {
    return owningUserEntityModel;
  }

  public String getLastModificationUser() {
    return entity.getLastModificationUser();
  }

  public User getLastModificationUserByLoginName() {    
    return SpringServiceFactory.getUserService().getUserByLoginIfExists(getLastModificationUser());
  }
  
  public Date getLastModificationTime() {
    return entity.getLastModificationTime();
  }

  public Set<User> getSubscribedUsers() {
    if (entity != null) {
      TypeOfBuildingBlock tobb = entity.getTypeOfBuildingBlock();
      BuildingBlockService<BuildingBlock, Integer> service = SpringServiceFactory.getBuildingBlockServiceLocator().getService(tobb);
      if (service != null) {
        BuildingBlock loadedEntity = service.loadObjectByIdIfExists(entity.getId());
        if (loadedEntity != null) {
          return loadedEntity.getSubscribedUsers();
        }
      }
    }
    return Collections.emptySet();
  }

  public boolean isSubscribed() {
    return getSubscribedUsers().contains(UserContext.getCurrentUserContext().getUser());
  }

  /**
   * Verifies that the given {@code name} is a valid part for a hierarchical name. Precisely
   * speaking, it checks that the name <b>contains no colon</b>.
   * 
   * @param name
   *          a building block name to checked for validity
   * @return true if the name can be used for a hierarchical building block
   */
  protected boolean isValidHierarchyPartName(String name) {
    return name.matches("^[^\\:]*$");
  }

  public abstract Type<? extends BuildingBlock> getManagedType();

  public T getEntity() {
    return entity;
  }

  public void setEntity(T entity) {
    this.entity = entity;
  }
}