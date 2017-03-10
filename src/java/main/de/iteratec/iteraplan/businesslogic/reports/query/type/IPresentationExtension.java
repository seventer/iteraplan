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

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import de.iteratec.iteraplan.common.Constants;


/**
 * Represents an extension for a type. <br/>
 * <br/>
 * An extension models a path from one type to another. This interface is primarily used by the GUI
 * and therefore does not include methods relevant for the processing of extensions. See
 * implementing classes for more information.
 */
public interface IPresentationExtension extends Serializable, Cloneable {

  /**
   * Comparator class for IPresentationExtensions.
   */
  final class ExtensionComparator implements Comparator<IPresentationExtension>, Serializable {
    /** Serialization version. */
    private static final long serialVersionUID = 3658071331535720997L;

    public int compare(IPresentationExtension ext1, IPresentationExtension ext2) {
      return ext1.getName().compareToIgnoreCase(ext2.getName());
    }
  }

  /**
   * Comparator class for IPresentationExtensions.
   */
  final class FunctionalExtensionComparator implements Comparator<IPresentationExtension>, Serializable {
    /** Serialization version. */
    private static final long serialVersionUID = 4450138546302453337L;

    public int compare(IPresentationExtension ext1, IPresentationExtension ext2) {
      return Integer.valueOf(Constants.EXTENSIONS_SORTED.indexOf(ext1.getNameKeyForPresentation())).compareTo(
          Integer.valueOf(Constants.EXTENSIONS_SORTED.indexOf(ext2.getNameKeyForPresentation())));
    }
  }

  /**
   * Compares two IPresentation objects by comparing their
   * {@link IPresentationExtension#getNameKeyForPresentation()}
   */
  final class PresentationKeyComparator implements Comparator<IPresentationExtension>, Serializable {
    /** Serialization version. */
    private static final long serialVersionUID = -3865939287971850698L;

    public int compare(IPresentationExtension e1, IPresentationExtension e2) {
      return e1.getNameKeyForPresentation().compareToIgnoreCase(e2.getNameKeyForPresentation());
    }
  }

  /**
   * Returns the identifier of the Extension as defined within its Type instance.
   * 
   * @return A unique identifier for this extension.
   */
  String getName();

  /**
   * An I18N key that can be used by the GUI as a name for the extension.
   * 
   * @return an I18N key.
   */
  String getNameKeyForPresentation();

  /**
   * @return key to determine permissions for this extension from
   */
  String getPermissionKey();

  /**
   * Returns the type instance that describes the end point of the Extension.
   * 
   * @return the requested type
   */
  Type<?> getRequestedType();

  // TODO is this needed in the interface? Can be derived from the passed type.
  /**
   * Returns true if the extension contains an invisible type. The {@code BusinessMapping} is an
   * example for an association type.
   * 
   * @return true iff extension contains an invisible type.
   */
  boolean isWithAssociationType();

  // TODO is this needed in the interface? Can be derived from the passed type.
  /**
   * Returns a list of second step Extension instances. They describe additional paths to other
   * building blocks starting at the end point of this Extension.
   * 
   * @return the extensions that start from the endpoint of this extension.
   */
  List<Extension> getSecondStepExtensions();

  /**
   * Checks all contained extensions recursively and removes all for which the current user has
   * insufficient permissons.
   */
  void checkDeepPermission();

  /**
   * Forces all subclasses to implement Cloneable. Not that PMD hsa two strange properties which
   * implies that the warning has to be suppressed:
   * <ul>
   * <li>PMD does not allow an interface which extends Cloneable to contain a clone method</li>
   * <li>PMD forces all implementations of this interface to implement Cloneable directly</li>
   * </ul>
   */
  @SuppressWarnings("PMD")
  IPresentationExtension clone() throws CloneNotSupportedException;
}