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
package de.iteratec.iteraplan.presentation.dialog.AttributeType.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.validation.Errors;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.sorting.UserEntityIntComparator;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;


/**
 * Component Model for managing the value list for responsibility attribute types. It defines
 * possible actions for the component model. The actions are read from the action property and
 * evaluated in the update method.
 * 
 * @author Abdeltif Klia (iteratec GmbH)
 */
public class ResponsibilityAttributeValuesComponentModel extends ManyAssociationSetComponentModel<ResponsibilityAT, UserEntity> {

  private Map<String, String>                 valueToColorMap     = Maps.newHashMap();

  /** List of available colors for attributes */
  private List<String>                        availableColors     = Lists.newArrayList();

  private String                              colorToAdd;

  private int                                 availableColorIndex = 0;

  private final Map<String, ResponsibilityAV> ueNameToAV          = Maps.newHashMap();

  /** Serialization version. */
  private static final long                   serialVersionUID    = 7391974545002408992L;

  public ResponsibilityAttributeValuesComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId, "manageAttributes.possiblevalues", new String[] { "global.type", "instancePermission.name",
        "manageAttributes.defaultcolor", "instancePermission.description" }, new String[] { "type", "identityString", "defaultColorHex",
        "descriptiveString" }, "identityString", null, new Boolean[] { Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE }, Boolean.FALSE,
        new String[] { "global.user", "global.usergroups" });
  }

  /**
   * Delete the attribute value to be removed.
   * 
   * @param reponsibilityAv the responsibility attribute value to be removed
   */
  private void deleteRemovedAV(ResponsibilityAV reponsibilityAv) {
    AttributeValueService attributeValueService = SpringServiceFactory.getAttributeValueService();
    ResponsibilityAV reloadedElement = attributeValueService.reload(reponsibilityAv);

    reloadedElement.getAttributeType().getAttributeValues().remove(reloadedElement);
    attributeValueService.deleteEntity(reloadedElement);
  }

  @Override
  public void initializeFrom(ResponsibilityAT source) {
    super.initializeFrom(source);

    availableColors = SpringGuiFactory.getInstance().getAttributeColors();
    // initialize colors
    for (ResponsibilityAV resAV : source.getAttributeValues()) {
      valueToColorMap.put(resAV.getUserEntity().toString(), resAV.getDefaultColorHex());
      ueNameToAV.put(resAV.getUserEntity().getIdentityString(), resAV);
    }
  }

  @Override
  public void update() {
    super.update();

    //entry was added (from availableElements and is therefore now in connectedElements)
    for (UserEntity ue : getConnectedElements()) {
      String name = ue.getIdentityString();
      if (!valueToColorMap.containsKey(name)) {
        valueToColorMap.put(name, colorToAdd);
        // shift the index
        availableColorIndex = (availableColorIndex + 1) % availableColors.size();
        colorToAdd = null;
      }
    }
  }

  @Override
  public void configure(ResponsibilityAT target) {
    super.configure(target);

    // here target already has the correct attribute values
    for (ResponsibilityAV av : target.getAttributeValues()) {
      av.setDefaultColorHex(valueToColorMap.get(av.getUserEntity().getIdentityString()));
    }
  }

  @Override
  public void validate(Errors errors) {
    // do nothing
  }

  /**
   * Returns an array of lists with available UserEntities:
   * <ol>
   *   <li>All available users.</li>
   *   <li>All available user groups.</li>
   * </ol>
   * @return List[User, UserGroup] 
   */
  @SuppressWarnings("unchecked")
  public List<UserEntity>[] getAvailableElementsPresentationGrouped() {
    List<UserEntity> users = new ArrayList<UserEntity>();
    List<UserEntity> userGroups = new ArrayList<UserEntity>();
    for (UserEntity userEntity : this.getAvailableElements()) {
      if (userEntity instanceof User) {
        users.add(userEntity);
      }
      else if (userEntity instanceof UserGroup) {
        userGroups.add(userEntity);
      }
    }
    return new List[] { users, userGroups };
  }

  public Map<String, String> getValueToColorMap() {
    return valueToColorMap;
  }

  public void setValueToColorMap(Map<String, String> valueToColorMap) {
    this.valueToColorMap = valueToColorMap;
  }

  public List<String> getAvailableColors() {
    return availableColors;
  }

  public void setAvailableColors(List<String> availableColors) {
    this.availableColors = availableColors;
  }

  public String getColorToAdd() {
    return colorToAdd;
  }

  public void setColorToAdd(String colorToAdd) {
    this.colorToAdd = colorToAdd;
  }

  public int getAvailableColorIndex() {
    return availableColorIndex;
  }

  public void setAvailableColorIndex(int availableColorIndex) {
    this.availableColorIndex = availableColorIndex;
  }

  /**{@inheritDoc}**/
  @Override
  protected Set<UserEntity> getConnectedElements(ResponsibilityAT source) {
    return Sets.newHashSet(Collections2.transform(source.getAttributeValues(), new ToUserEntityFunction()));
  }

  /**{@inheritDoc}**/
  @Override
  protected void setConnectedElements(ResponsibilityAT target, Set<UserEntity> toConnect) {
    Set<UserEntity> current = getConnectedElements(target);

    Set<UserEntity> toRemove = Sets.newHashSet(current);
    toRemove.removeAll(toConnect);

    Set<UserEntity> toAdd = Sets.newHashSet(toConnect);
    toAdd.removeAll(current);

    for (UserEntity toBeRemoved : toRemove) {
      deleteRemovedAV(ueNameToAV.get(toBeRemoved.getIdentityString()));
    }

    List<ResponsibilityAV> avsToBeAdded = Lists.newArrayList();
    for (UserEntity toBeAdded : toAdd) {
      ResponsibilityAV avToBeAdded = new ResponsibilityAV();
      avToBeAdded.setUserEntity(toBeAdded);
      avToBeAdded.setDefaultColorHex(valueToColorMap.get(toBeAdded.getIdentityString()));
      avsToBeAdded.add(avToBeAdded);
      ueNameToAV.put(toBeAdded.getIdentityString(), avToBeAdded);
    }
    target.addAttributeValuesTwoWay(avsToBeAdded);
  }

  /**{@inheritDoc}**/
  @Override
  protected List<UserEntity> getAvailableElements(Integer id, List<UserEntity> connected) {
    List<UserEntity> availableEntities = SpringServiceFactory.getUserEntityService().loadElementList();
    availableEntities.removeAll(connected);
    Collections.sort(availableEntities, new UserEntityIntComparator());
    return availableEntities;
  }

  private static class ToUserEntityFunction implements Function<ResponsibilityAV, UserEntity> {
    public UserEntity apply(final ResponsibilityAV from) {
      return from.getUserEntity();
    }
  }

}