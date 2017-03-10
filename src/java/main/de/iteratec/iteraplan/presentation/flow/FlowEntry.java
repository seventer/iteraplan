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
package de.iteratec.iteraplan.presentation.flow;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.springframework.webflow.execution.repository.support.CompositeFlowExecutionKey;

import de.iteratec.iteraplan.common.Constants;


public class FlowEntry implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = -6802835405684246238L;
  private String            flowId;
  private String            key;
  private String            label;
  private boolean           edit;
  private Integer           entityId;

  public FlowEntry(String flowId, String key, String label, Integer entityId) {
    this.flowId = flowId;
    this.key = key;
    this.label = label;
    this.edit = false;
    this.entityId = entityId;
  }

  /**
   * The Spring web flow notion of an execution key, i.e. what identifies a flow execution state
   */
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  /**
   * A flow label, to be shown to the user as an identifying name for the flow execution
   */
  public String getLabel() {
    return label;
  }

  public String getTruncatedLabel() {
    if (label == null) {
      return null;
    }
    return StringUtils.abbreviate(label, Constants.LENGTH_LABEL_TRUNCATED_BUILDINGBLOCK);
  }

  public boolean isTruncated() {
    if (label == null) {
      return false;
    }
    return (label.length() > Constants.LENGTH_LABEL_TRUNCATED_BUILDINGBLOCK);

  }

  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * The Spring web flow notion of a flow ID, i.e. an identifier of the flow definition
   */
  public String getFlowId() {
    return flowId;
  }

  public void setFlowId(String flowId) {
    this.flowId = flowId;
  }

  /**
   * The ID of the entity that is loaded in the flow, if applicable. May also be null. 
   */
  public Integer getEntityId() {
    return entityId;
  }

  public void setEntityId(Integer entityId) {
    this.entityId = entityId;
  }

  public boolean isStepOfSameFlow(String flowKey) {

    String[] keyPartsOwn = CompositeFlowExecutionKey.keyParts(key);
    String[] keyPartsEntry = CompositeFlowExecutionKey.keyParts(flowKey);

    return keyPartsEntry[0].equals(keyPartsOwn[0]);
  }

  public void setEdit(boolean edit) {
    this.edit = edit;
  }

  /**
   * Flow is in edit mode or not
   */
  public boolean isEdit() {
    return edit;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer(50);
    sb.append("Flow Entry (");
    sb.append(key);
    sb.append(") for flow '");
    sb.append(flowId);
    sb.append("': ");
    sb.append(label);
    sb.append("; isEdit: ");
    sb.append(edit);
    return sb.toString();
  }

}
