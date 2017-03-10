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

import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.diffs.model.InterfaceChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.Transport;


public class InterfaceModelBuilder extends BuildingBlockModelBuilder {

  private InterfaceChangeset changeSet;

  public InterfaceModelBuilder(BuildingBlock bb, HistoryBBChangeset changeSet, String applicationBaseUri) {
    super(bb, applicationBaseUri, changeSet);
    if (changeSet != null && ! (changeSet instanceof InterfaceChangeset)) {
      throw new IllegalArgumentException("expecting an InterfaceChangeSet");
    }
    this.changeSet = (InterfaceChangeset) changeSet;
  }

  @Override
  public EmailModel createModel() {
    EmailModel model = super.createModel();

    if (changeSet == null) {
      return model;
    }

    List<EmailModel.Change> changes = model.getChanges();

    if (changeSet.getAddedInformationSystemA() != null) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Information system A");
      change.setType(EmailModel.CHANGED);
      change.setFrom(SubscriptionsUtil.extractName(changeSet.getRemovedInformationSystemA()));
      change.setTo(SubscriptionsUtil.extractName(changeSet.getAddedInformationSystemA()));
      change.setFromLink(this.createLink(changeSet.getRemovedInformationSystemA()));
      change.setToLink(this.createLink(changeSet.getAddedInformationSystemA()));
      changes.add(change);
    }

    if (changeSet.getAddedInformationSystemB() != null) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Information system B");
      change.setType(EmailModel.CHANGED);
      change.setFrom(SubscriptionsUtil.extractName(changeSet.getRemovedInformationSystemB()));
      change.setTo(SubscriptionsUtil.extractName(changeSet.getAddedInformationSystemB()));
      change.setFromLink(this.createLink(changeSet.getRemovedInformationSystemB()));
      change.setToLink(this.createLink(changeSet.getAddedInformationSystemB()));
      changes.add(change);
    }

    if (changeSet.getDirectionTo() != null) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Direction");
      change.setType(EmailModel.CHANGED);
      change.setFrom(changeSet.getDirectionFrom());
      change.setTo(changeSet.getDirectionTo());
      changes.add(change);
    }

    this.addChange(changes, "Technical component", EmailModel.ADDED, changeSet.getTechnicalComponentReleasesAdded());
    this.addChange(changes, "Technical component", EmailModel.REMOVED, changeSet.getTechnicalComponentReleasesRemoved());

    this.addTransportChanges(changes, "Transport", EmailModel.ADDED, changeSet.getTransportsAdded());
    this.addTransportChanges(changes, "Transport", EmailModel.REMOVED, changeSet.getTransportsRemoved());

    return model;
  }

  private void addTransportChanges(List<EmailModel.Change> changes, String name, String type, List<Transport> elements) {
    for (Transport childAdded : elements) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName(name);
      change.setType(type);
      change.setValue(
          childAdded.getTransportInfo().getTextRepresentation() + " " + SubscriptionsUtil.extractName(childAdded.getBusinessObject()));
      changes.add(change);
    }
  }
}
