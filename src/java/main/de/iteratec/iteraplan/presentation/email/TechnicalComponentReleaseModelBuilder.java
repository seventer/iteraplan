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

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.diffs.model.TechnicalComponentReleaseChangeset;
import de.iteratec.iteraplan.model.BuildingBlock;


public class TechnicalComponentReleaseModelBuilder extends BuildingBlockModelBuilder {

  private TechnicalComponentReleaseChangeset changeSet;

  public TechnicalComponentReleaseModelBuilder(BuildingBlock bb, HistoryBBChangeset changeSet, String applicationBaseUri) {
    super(bb, applicationBaseUri, changeSet);
    if (changeSet != null && ! (changeSet instanceof TechnicalComponentReleaseChangeset)) {
      throw new IllegalArgumentException("expecting an TechnicalComponentReleaseChangeSet");
    }

    this.changeSet = (TechnicalComponentReleaseChangeset) changeSet;
  }

  @Override
  public EmailModel createModel() {
    EmailModel model = super.createModel();

    if (changeSet == null) {
      return model;
    }

    List<EmailModel.Change> changes = model.getChanges();

    if (changeSet.getAvailableForInterfacesTo() != null) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Available for interfaces");
      change.setType(EmailModel.CHANGED);
      change.setFrom(String.valueOf(changeSet.getAvailableForInterfacesFrom()));
      change.setTo(String.valueOf(changeSet.getAvailableForInterfacesTo()));
      changes.add(change);
    }

    if ((changeSet.getStatusTo() != null) || (changeSet.getStatusFrom() != null)) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Status");
      if ((changeSet.getStatusTo() != null) && (changeSet.getStatusFrom() == null)) {
        change.setType(EmailModel.ADDED);
        change.setValue(MessageAccess.getString(changeSet.getStatusTo(), Locale.ENGLISH));
      }
      else if ((changeSet.getStatusTo() == null) && (changeSet.getStatusFrom() != null)) {
        change.setType(EmailModel.REMOVED);
        change.setValue(MessageAccess.getString(changeSet.getStatusFrom(), Locale.ENGLISH));
      }
      else {
        change.setType(EmailModel.CHANGED);
        change.setFrom(MessageAccess.getString(changeSet.getStatusFrom(), Locale.ENGLISH));
        change.setTo(MessageAccess.getString(changeSet.getStatusTo(), Locale.ENGLISH));
      }
      changes.add(change);
    }

    if ((changeSet.getRuntimeStartAdded() != null) || (changeSet.getRuntimeStartRemoved() != null)) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Runtime from");
      if ((changeSet.getRuntimeStartAdded() != null) && (changeSet.getRuntimeStartRemoved() == null)) {
        change.setType(EmailModel.ADDED);
        change.setValue(changeSet.getRuntimeStartAdded());
      }
      else if ((changeSet.getRuntimeStartAdded() == null) && (changeSet.getRuntimeStartRemoved() != null)) {
        change.setType(EmailModel.REMOVED);
        change.setValue(changeSet.getRuntimeStartRemoved());
      }
      else {
        change.setType(EmailModel.CHANGED);
        change.setFrom(changeSet.getRuntimeStartRemoved());
        change.setTo(changeSet.getRuntimeStartAdded());
      }
      changes.add(change);
    }

    if ((changeSet.getRuntimeEndAdded() != null) || (changeSet.getRuntimeEndRemoved() != null)) {
      EmailModel.Change change = new EmailModel.Change();
      change.setName("Runtime to");
      if ((changeSet.getRuntimeEndAdded() != null) && (changeSet.getRuntimeEndRemoved() == null)) {
        change.setType(EmailModel.ADDED);
        change.setValue(changeSet.getRuntimeEndAdded());
      }
      else if ((changeSet.getRuntimeEndAdded() == null) && (changeSet.getRuntimeEndRemoved() != null)) {
        change.setType(EmailModel.REMOVED);
        change.setValue(changeSet.getRuntimeEndRemoved());
      }
      else {
        change.setType(EmailModel.CHANGED);
        change.setFrom(changeSet.getRuntimeEndRemoved());
        change.setTo(changeSet.getRuntimeEndAdded());
      }
      changes.add(change);
    }

    this.addChange(changes, "Successor", EmailModel.ADDED, changeSet.getSuccessorsAdded());
    this.addChange(changes, "Successor", EmailModel.REMOVED, changeSet.getSuccessorsRemoved());
    this.addChange(changes, "Predecessor", EmailModel.ADDED, changeSet.getPredecessorsAdded());
    this.addChange(changes, "Predecessor", EmailModel.REMOVED, changeSet.getPredecessorsRemoved());
    this.addChange(changes, "Uses Technical Component", EmailModel.ADDED, changeSet.getBaseComponentsAdded());
    this.addChange(changes, "Uses Technical Component", EmailModel.REMOVED, changeSet.getBaseComponentsRemoved());
    this.addChange(changes, "Used by Technical Component", EmailModel.ADDED, changeSet.getParentComponentsAdded());
    this.addChange(changes, "Used by Technical Component", EmailModel.REMOVED, changeSet.getParentComponentsRemoved());
    this.addChange(changes, "Architectural domain", EmailModel.ADDED, changeSet.getArchitecturalDomainsAdded());
    this.addChange(changes, "Architectural domain", EmailModel.REMOVED, changeSet.getArchitecturalDomainsRemoved());
    this.addChange(changes, "Infrastructure element", EmailModel.ADDED, changeSet.getInfrastructureElementsAdded());
    this.addChange(changes, "Infrastructure element", EmailModel.REMOVED, changeSet.getInfrastructureElementsRemoved());
    this.addChange(changes, "Information system release", EmailModel.ADDED, changeSet.getInformationSystemReleasesAdded());
    this.addChange(changes, "Information system release", EmailModel.REMOVED, changeSet.getInformationSystemReleasesRemoved());

    return model;
  }
}
