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
package de.iteratec.iteraplan.businesslogic.reports.query.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.user.PermissionHelper;


/**
 * Combines several technical {@link Extension}s to one logical Extension. This class allows the
 * presentation tier to represent several paths as one logical path. The Extensions this class
 * combines are stored in {@link #getExtensionList()}. This list is used by the QueryTreeGenerator
 * to generate nodes for each possible technical path and interconnect them with an OR.
 */
public class CombinedExtension implements IPresentationExtension, Cloneable {

  /** Serialization version. */
  private static final long serialVersionUID = 3720749824485809290L;

  /** @see #getName() */
  private String            name;

  /** @see #getNameKeyForPresentation() */
  private String            nameKeyForPresentation;

  /** @see #getExtensionList() */
  private List<Extension>   extensionList    = new ArrayList<Extension>();

  /**
   * Constructor. Creates a simple CombinedExtension without additional "second step" Extensions.
   * 
   * @param name
   * @param nameKeyForPresentation
   * @param extensionList
   */
  public CombinedExtension(String name, String nameKeyForPresentation, List<Extension> extensionList) {
    this.nameKeyForPresentation = nameKeyForPresentation;
    this.extensionList = extensionList;
    this.name = name;
  }

  /**
   * A list of fist step Extension instances that represent all possible paths to the primary end
   * point building block.
   * 
   * @return The list of {@link Extension}s this class combines.
   */
  public List<Extension> getExtensionList() {
    return extensionList;
  }

  void setExtensionList(List<Extension> extensions) {
    this.extensionList = extensions;
  }

  public String getNameKeyForPresentation() {
    return nameKeyForPresentation;
  }

  public Type<? extends BuildingBlock> getRequestedType() {
    Extension extension = getExtensionList().get(0);
    if (extension != null) {
      return extension.getRequestedType();
    }
    else {
      return null;
    }
  }

  public String getName() {
    return name;
  }

  public boolean isWithAssociationType() {
    for (Extension extension : extensionList) {
      if (extension.isWithAssociationType()) {
        return true;
      }
    }
    return false;
  }

  // TODO what does this method do and what is it used for?
  /**
   * @return true, iff the last part of the logical path this combined extension represents is
   *         technically a multi-path.
   */
  public boolean isLastPartMultiEnded() {
    String currentLastPart = null;
    for (Extension extension : extensionList) {
      if (currentLastPart == null) {
        currentLastPart = extension.getLeafTypeJoinProperty();
      }
      else if (!currentLastPart.equals(extension.getLeafTypeJoinProperty())) {
        return true;
      }
    }
    return false;
  }

  public List<Extension> getSecondStepExtensions() {
    return new ArrayList<Extension>();
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNameKeyForPresentation(String nameKeyForPresentation) {
    this.nameKeyForPresentation = nameKeyForPresentation;
  }

  public void checkDeepPermission() {
    Iterator<Extension> it = getSecondStepExtensions().iterator();
    while (it.hasNext()) {
      Extension extension = it.next();
      if (!(PermissionHelper.hasPermissionFor(extension.getPermissionKey()))) {
        it.remove();
      }
      else {
        extension.checkDeepPermission();
      }
    }
    // TODO Does the list have to be filtered for combined extensions as well?
  }

  @Override
  public IPresentationExtension clone() throws CloneNotSupportedException {
    Object clonedObject = super.clone();

    if (clonedObject instanceof CombinedExtension) {
      CombinedExtension clonedExtension = (CombinedExtension) clonedObject;
      clonedExtension.setExtensionList(new ArrayList<Extension>());

      if (getExtensionList() != null) {
        for (Extension extensionToClone : getExtensionList()) {
          Extension extensionClone = (Extension) extensionToClone.clone();
          clonedExtension.getExtensionList().add(extensionClone);
        }
      }

      return clonedExtension;
    }
    else {
      // Should not happen...
      throw new CloneNotSupportedException();
    }

  }

  public String getPermissionKey() {
    return nameKeyForPresentation;
  }
}
