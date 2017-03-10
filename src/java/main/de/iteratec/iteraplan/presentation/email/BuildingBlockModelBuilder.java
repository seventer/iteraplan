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
package de.iteratec.iteraplan.presentation.email;

import java.util.List;
import java.util.Locale;

import com.google.common.base.Joiner;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.diffs.model.HistoryBBAttributeGroupChangeset;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.presentation.email.EmailModel.Change;


public abstract class BuildingBlockModelBuilder extends AbstractModelBuilder {

  private HistoryBBChangeset changeSet;

  public BuildingBlockModelBuilder(BuildingBlock bb, String applicationBaseUri, HistoryBBChangeset changeSet) {
    super(bb, applicationBaseUri);
    this.changeSet = changeSet;
  }

  @Override
  public EmailModel createModel() {
    EmailModel model = super.createModel();

    model.setType(getType());

    if (changeSet == null) {
      return model;
    }

    List<EmailModel.Change> changes = model.getChanges();

    addNameChange(changes);
    addDescriptionChange(changes);
    addAttributeChanges(changes);
    addChangesForTimeseries(changes);
    return model;
  }

  private void addNameChange(List<EmailModel.Change> changes) {
    if (changeSet.getNameFrom() != null) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Name");
      change.setType(EmailModel.CHANGED);
      change.setFrom(changeSet.getNameFrom());
      change.setTo(changeSet.getNameTo());
      changes.add(change);
    }
  }

  private void addDescriptionChange(List<EmailModel.Change> changes) {
    if (changeSet.isDescriptionChanged()) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Description");
      setChangeValues(change, changeSet.getDescriptionFrom(), changeSet.getDescriptionTo());
      changes.add(change);
    }
  }

  private void addAttributeChanges(List<EmailModel.Change> changes) {
    for (HistoryBBAttributeGroupChangeset attributeChange : changeSet.getAttributeGroupChangesets()) {
      for (String[] attributeChanges : attributeChange.getChangedAttributes()) {
        EmailModel.Change change = new EmailModel.Change();
        change.setName("Attribute \"" + attributeChanges[0] + "\"");
        setChangeValues(change, attributeChanges[1], attributeChanges[2]);
        changes.add(change);
      }
    }
  }

  private Change setChangeValues(Change change, String fromValue, String toValue) {
    String from = (fromValue == null ? "" : fromValue);
    String to = (toValue == null ? "" : toValue);
    if (from.isEmpty() && !to.isEmpty()) {
      change.setType(EmailModel.ADDED);
      change.setValue(toValue);
    }
    else if (!from.isEmpty() && to.isEmpty()) {
      change.setType(EmailModel.REMOVED);
      change.setValue(fromValue);
    }
    else if (!from.isEmpty() && !to.isEmpty()) {
      change.setType(EmailModel.CHANGED);
      change.setFrom(fromValue);
      change.setTo(toValue);
    }
    return change;
  }

  /**
   * @param changes List the changes are being added to
   * @param bmList
   * @param type whether added or removed
   */
  protected void applyChangesFromBusinessMappings(List<Change> changes, List<BusinessMapping> bmList, String type) {
    for (BusinessMapping businessMapping : bmList) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Business mapping");
      change.setType(type);

      //      StringBuilder buffer = new StringBuilder();
      //      buffer.append(ModelBuilderHelper.extractName(businessMapping.getInformationSystemRelease()));
      //      buffer.append('/');
      //      buffer.append(ModelBuilderHelper.extractName(businessMapping.getBusinessProcess()));
      //      buffer.append('/');
      //      buffer.append(ModelBuilderHelper.extractName(businessMapping.getBusinessUnit()));
      change.setValue(SubscriptionsUtil.extractName(businessMapping));

      changes.add(change);
    }
  }

  private void addChangesForTimeseries(List<Change> changes) {
    List<String> changedTimeseriesNames = changeSet.getChangedTimeseriesNames();
    if (changedTimeseriesNames != null && !changedTimeseriesNames.isEmpty()) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Following timeseries");
      change.setType(EmailModel.CHANGED);
      change.setValue(Joiner.on(", ").join(changedTimeseriesNames));
      changes.add(change);
    }
  }

  protected String getType() {
    return MessageAccess.getString(bb.getTypeOfBuildingBlock().getValue(), Locale.ENGLISH);
  }
}
