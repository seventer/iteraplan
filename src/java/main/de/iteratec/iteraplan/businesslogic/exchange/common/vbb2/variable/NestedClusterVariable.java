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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.variable;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation.Priority;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.variable.TypeVariable.TypeVariableParentCallback;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;


/**
 * Root variable of the nested cluster diagram configuration.
 */
public class NestedClusterVariable {

  protected static final String      KEY_OUTER                  = "outer";
  protected static final String      KEY_INNER                  = "inner";

  protected static final String      KEY_SHOW_ALL_INNER         = "showAllInner";
  protected static final String      KEY_BASE_URL               = "baseUrl";
  protected static final String      KEY_TITLE                  = "title";

  protected static final String      DEFAULT_STATIC_COLOR_OUTER = "#F2EEF1";
  protected static final String      DEFAULT_STATIC_COLOR_INNER = "#F2CBFE";

  private final TypeVariable         outer;
  private final TypeVariable         inner;
  private final RelationshipVariable outer2inner;

  private String                     baseUrl;
  private String                     title;
  private boolean                    showAllInner;

  public NestedClusterVariable(RMetamodel metamodel) {
    TypeVariableParentCallback outerCallback = new TypeVariableParentCallback() {
      @Override
      public void typeChanged(RStructuredTypeExpression type) {
        inner.setBondaryOppositeType(type);
        outer2inner.setBoundaryOuterType(type);
      }
    };
    this.outer = new TypeVariable(KEY_OUTER, metamodel, outerCallback, DEFAULT_STATIC_COLOR_OUTER);
    TypeVariableParentCallback innerCallback = new TypeVariableParentCallback() {
      @Override
      public void typeChanged(RStructuredTypeExpression type) {
        outer.setBondaryOppositeType(type);
        outer2inner.setBoundaryInnerType(type);
      }
    };
    this.inner = new TypeVariable(KEY_INNER, metamodel, innerCallback, DEFAULT_STATIC_COLOR_INNER);
    this.outer2inner = new RelationshipVariable(metamodel);
  }

  @SuppressWarnings("boxing")
  public void configureFrom(ViewpointConfiguration vpConfig) {
    this.outer.configureFrom(vpConfig);
    this.inner.configureFrom(vpConfig);
    // Caution: configure outer and inner first, so that outer2inner has boundaries
    this.outer2inner.configureFrom(vpConfig);

    this.baseUrl = vpConfig.get(KEY_BASE_URL);
    this.title = vpConfig.get(KEY_TITLE);
    this.showAllInner = Boolean.valueOf(vpConfig.get(KEY_SHOW_ALL_INNER));
  }

  public ViewpointRecommendation getRecommendation() {
    ViewpointRecommendation vpRec = ViewpointRecommendation.create();

    outer.appendRecommendations(vpRec);
    inner.appendRecommendations(vpRec);
    outer2inner.appendRecommendations(vpRec.withPrefix(KEY_OUTER));

    vpRec.addFlat(KEY_SHOW_ALL_INNER).append(String.valueOf(showAllInner), String.valueOf(showAllInner), Priority.HIGH);
    // try to do without rec.s for url, title, these are fixed!

    return vpRec;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getTitle() {
    return title;
  }

  public boolean isShowAllInner() {
    return showAllInner;
  }

  public TypeVariable getOuterTypeVariable() {
    return this.outer;
  }

  public TypeVariable getInnerTypeVariable() {
    return this.inner;
  }

  public RelationshipVariable getOuter2InnerVariable() {
    return this.outer2inner;
  }

}
