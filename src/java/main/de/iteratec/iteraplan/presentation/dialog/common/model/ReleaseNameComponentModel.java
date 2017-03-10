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

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.IteraplanValidationUtils;


public abstract class ReleaseNameComponentModel<T extends BuildingBlock> extends AbstractComponentModelBase<T> {

  private static final long             serialVersionUID = -7855966294993586460L;

  /** The CM for the name String. */
  private final StringComponentModel<T> elementName;

  /** The CM for the version String. */
  private final StringComponentModel<T> releaseName;

  /**
   * Component model for building blocks whose name consists of two parts, e.g. CatalogItemRelease,
   * Ipurelease.
   * 
   * @param componentMode
   *          The component mode for this
   * @param htmlId
   *          The String to use as an identifier for the component on the web page.
   */
  public ReleaseNameComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
    this.elementName = new StringComponentModel<T>(getComponentMode(), htmlId + "_elementName", null) {
      @Override
      public String getStringFromElement(T tbbrelease) {
        return getNameFromElement(tbbrelease);
      }

      @Override
      public void setStringForElement(T target, String stringToSet) {
        setNameForElement(target, stringToSet);
      }
    };
    this.releaseName = new StringComponentModel<T>(getComponentMode(), htmlId + "_releaseName", null) {
      @Override
      public String getStringFromElement(T obj) {
        return getVersionFromElement(obj);
      }

      @Override
      public void setStringForElement(T target, String stringToSet) {
        setVersionForElement(target, stringToSet);
      }
    };
  }

  protected abstract String getNameFromElement(T element);

  protected abstract void setNameForElement(T element, String nameToSet);

  protected abstract String getVersionFromElement(T element);

  protected abstract void setVersionForElement(T element, String versionToSet);

  public void initializeFrom(T source) throws IteraplanException {
    this.elementName.initializeFrom(source);
    this.releaseName.initializeFrom(source);
  }

  public void configure(T target) throws IteraplanException {
    this.elementName.configure(target);
    this.releaseName.configure(target);
  }

  public void update() throws IteraplanException {
    this.elementName.update();
    this.releaseName.update();
  }

  public StringComponentModel<T> getElementName() {
    return elementName;
  }

  public StringComponentModel<T> getReleaseName() {
    return releaseName;
  }

  public String getElementHtmlId() {
    return super.getHtmlId() + "_element_text";
  }

  public String getReleaseHtmlId() {
    return super.getHtmlId() + "_release_text";
  }

  public String getName() {
    String result = this.elementName.getCurrent();
    if (!StringUtils.isEmpty(this.releaseName.getCurrent())) {
      result += Constants.VERSIONSEP + this.releaseName.getCurrent();
    }
    return result;
  }

  public void validate(Errors errors) {
    Object[] params = IteraplanValidationUtils.getLocalizedArgsWithSpanTags("global.name", "errorInline");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "elementName.current", "errors.required", params);

    if (elementName.getCurrent().contains("#") || elementName.getCurrent().contains(":")) {
      errors.rejectValue("elementName.current", "errors.releasemask");
    }
    if (releaseName.getCurrent().contains("#") || releaseName.getCurrent().contains(":")) {
      errors.rejectValue("releaseName.current", "errors.releasemask");
    }

    if (releaseName.getCurrent().length() > Constants.TEXT_SHORT) {
      errors.rejectValue("releaseName.current", "NAME_TOO_LONG");
    }

    if (elementName.getCurrent().length() > Constants.TEXT_SHORT) {
      errors.rejectValue("elementName.current", "NAME_TOO_LONG");
    }

  }
}
