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
package de.iteratec.iteraplan.presentation.dialog.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.dto.PermissionAttrTypeGroupDTO;
import de.iteratec.iteraplan.model.interfaces.IdEntity;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;


public abstract class PermissionAttributeTypeGroupComponentModel<F extends IdEntity> extends
    ManyAssociationSetComponentModel<F, PermissionAttrTypeGroupDTO> implements Serializable {

  /** Serialization version */
  private static final long                      serialVersionUID           = 6896723222295610222L;

  private static final Logger                    LOGGER                     = Logger
                                                                                .getIteraplanLogger(PermissionAttributeTypeGroupComponentModel.class);

  private String                                 comboboxField;

  private static final String                    HTML_ID_FOR_ROLES          = "permissionAtgs";
  private static final String                    TABLE_HEADER_FOR_ROLES     = "manageRoles.permissionsAttrGroup";
  private static final String                    COLUMN_HEADER_1            = Constants.ATTRIBUTE_NAME;
  private static final String                    COLUMN_HEADER_2            = "manageRoles.setPermissions";
  private static final String                    ATG_NAME_FIELD             = "permission.attributeTypeGroupName";
  private static final String                    PERMISSION_TYPE_FIELD      = "permission.permissionKey";

  private static final String                    HTML_ID_FOR_ATGS           = "permissionForRoles";
  private static final String                    TABLE_HEADER_FOR_ATGS      = "atg.permissionsRole";
  private static final String                    ROLE_NAME_FIELD            = "permission.roleName";

  private static final String                    READ_PERMISSION_LABEL      = "manageRoles.readPermission";
  private static final String                    READWRITE_PERMISSION_LABEL = "manageRoles.readWritePermission";
  private static final List<String>              COMBOBOX_LABEL_VALUES;

  private Comparator<PermissionAttrTypeGroupDTO> comparator                 = null;

  static {
    COMBOBOX_LABEL_VALUES = new ArrayList<String>();
    COMBOBOX_LABEL_VALUES.add(READ_PERMISSION_LABEL);
    COMBOBOX_LABEL_VALUES.add(READWRITE_PERMISSION_LABEL);
  }

  public PermissionAttributeTypeGroupComponentModel(ComponentMode componentMode, boolean manageFromRoleSide) {

    super(componentMode, getHtmlId(manageFromRoleSide), getTableHeader(manageFromRoleSide), getColumnHeaders(),
        getConnectedElementFields(manageFromRoleSide), getAvailableElementField(manageFromRoleSide), getDummy(), getLookUpLables(), Boolean.FALSE,
        null);

    // sort permissions by name of the encapsulated Role
    // default behaviour: sort by name of the encapsulated AttributeTypeGroup
    if (!manageFromRoleSide) {
      comparator = new PermissionAttrTypeGroupDTOComparator();
    }
  }

  @Override
  protected final void processElementIdToAdd() {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Adding element with ID " + getElementIdToAdd());
    }

    if (getElementIdToAdd() != null && getElementIdToAdd().intValue() != 0) {
      for (Iterator<PermissionAttrTypeGroupDTO> it = getAvailableElements().iterator(); it.hasNext();) {
        PermissionAttrTypeGroupDTO available = it.next();
        Integer availableId = available.getId();
        if (getElementIdToAdd().equals(availableId)) {
          it.remove();
          if (getComboboxField().equals(READ_PERMISSION_LABEL)) {
            available.getPermission().setReadPermission(Boolean.TRUE);
            available.getPermission().setWritePermission(Boolean.FALSE);
          }
          else if (getComboboxField().equals(READWRITE_PERMISSION_LABEL)) {
            available.getPermission().setReadPermission(Boolean.TRUE);
            available.getPermission().setWritePermission(Boolean.TRUE);
          }
          else {
            throw new IllegalArgumentException("The value stored in comboboxField must be either '" + READ_PERMISSION_LABEL + "' or '"
                + READWRITE_PERMISSION_LABEL + "'");
          }
          getConnectedElements().add(available);
          break;
        }
      }
    }
  }

  @Override
  protected final void processElementIdToRemove() {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Removing element with ID " + getElementIdToRemove());
    }

    if (getElementIdToRemove() != null && getElementIdToRemove().intValue() != 0) {
      for (Iterator<PermissionAttrTypeGroupDTO> it = getConnectedElements().iterator(); it.hasNext();) {
        PermissionAttrTypeGroupDTO connected = it.next();
        Integer connectedId = connected.getId();
        if (getElementIdToRemove().equals(connectedId)) {
          it.remove();
          getAvailableElements().add(connected);
          break;
        }
      }
    }
  }

  @Override
  protected Comparator<PermissionAttrTypeGroupDTO> comparatorForSorting() {
    return comparator;
  }

  public List<String> getComboboxLabelValues() {
    return COMBOBOX_LABEL_VALUES;
  }

  public String getComboboxField() {
    return comboboxField;
  }

  public void setComboboxField(String comboboxField) {
    this.comboboxField = comboboxField;
  }

  private static Boolean[] getLookUpLables() {
    return new Boolean[] { Boolean.FALSE, Boolean.TRUE };
  }

  private static PermissionAttrTypeGroupDTO getDummy() {
    return new PermissionAttrTypeGroupDTO(null, new PermissionAttrTypeGroup());
  }

  private static String[] getColumnHeaders() {
    return new String[] { COLUMN_HEADER_1, COLUMN_HEADER_2 };
  }

  private static String getAvailableElementField(boolean manageFromRoleSide) {
    return manageFromRoleSide ? ATG_NAME_FIELD : ROLE_NAME_FIELD;
  }

  private static String[] getConnectedElementFields(boolean manageFromRoleSide) {
    return manageFromRoleSide ? new String[] { ATG_NAME_FIELD, PERMISSION_TYPE_FIELD } : new String[] { ROLE_NAME_FIELD, PERMISSION_TYPE_FIELD };
  }

  private static String getTableHeader(boolean manageFromRoleSide) {
    return manageFromRoleSide ? TABLE_HEADER_FOR_ROLES : TABLE_HEADER_FOR_ATGS;
  }

  private static String getHtmlId(boolean manageFromRoleSide) {
    return manageFromRoleSide ? HTML_ID_FOR_ROLES : HTML_ID_FOR_ATGS;
  }

}
