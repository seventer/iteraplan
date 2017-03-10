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
package de.iteratec.iteraplan.presentation.ajax;

import java.util.List;

import org.springframework.web.util.HtmlUtils;


/**
 *
 */
public interface MetamodelExplorerService {

  List<PropertyExpressionDTO> getProperties(String uTypePersistentName);

  abstract class NamedElementDTO {
    protected String localName;
    protected String persistentName;

    /**
     * @return localName the localName
     */
    public final String getLocalName() {
      return localName;
    }

    public final void setLocalName(String localName) {
      this.localName = HtmlUtils.htmlEscape(localName);
    }

    /**
     * @return persistentName the persistentName
     */
    public final String getPersistentName() {
      return persistentName;
    }

    public final void setPersistentName(String persistentName) {
      this.persistentName = HtmlUtils.htmlEscape(persistentName);
    }
  }

  class EnumerationLiteralDTO extends NamedElementDTO {
    //Nothing new here
  }

  class ComparisonOperatorDTO extends NamedElementDTO {
    //Nothing new here
  }

  class PropertyExpressionDTO extends NamedElementDTO {
    protected int    lower;
    protected int    upper;
    protected String type;

    /**
     * @return lower the lower
     */
    public final int getLower() {
      return lower;
    }

    public final void setLower(int lower) {
      this.lower = lower;
    }

    /**
     * @return upper the upper
     */
    public final int getUpper() {
      return upper;
    }

    public final void setUpper(int upper) {
      this.upper = upper;
    }

    /**
     * @return type the type
     */
    public final String getType() {
      return type;
    }

    public final void setType(String type) {
      this.type = type;
    }
  }

}
