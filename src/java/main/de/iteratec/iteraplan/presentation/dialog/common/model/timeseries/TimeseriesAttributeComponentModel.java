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
package de.iteratec.iteraplan.presentation.dialog.common.model.timeseries;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.validation.Errors;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public class TimeseriesAttributeComponentModel extends AbstractTimeseriesCMBase<Timeseries> {
  private static final long                         serialVersionUID = -4492311061553495909L;

  private static final String                       ADD_ACTION       = "add";
  private static final String                       REMOVE_ACTION    = "remove";

  private AttributeType                             attribute;

  private final List<TimeseriesEntryComponentModel> entryComponentModels;
  private TimeseriesEntryComponentModel             newEntryComponentModel;
  private String                                    action;
  private Integer                                   position;

  public TimeseriesAttributeComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
    entryComponentModels = Lists.newArrayList();
  }

  public void initializeFrom(Timeseries source) {
    initAttributeInfo(source.getAttribute());

    for (TimeseriesEntry entry : source.getEntries()) {
      TimeseriesEntryComponentModel entryCM = new TimeseriesEntryComponentModel(attribute, getEntryComponentMode(),
          createEntryHtmlId(entry.getDate()));
      entryCM.initializeFrom(entry);
      entryComponentModels.add(entryCM);
    }
    sortEntries();

    initNewEntryCM();
  }

  /**
   * Component mode of already existing entries can't be CREATE, as CREATE allows for modification of the
   * values of the entry. EDIT allows for removal.
   * @return The component mode for already existing entries, based on the current component mode of the timeseries
   */
  private ComponentMode getEntryComponentMode() {
    return getComponentMode() == ComponentMode.READ ? ComponentMode.READ : ComponentMode.EDIT;
  }

  private void initAttributeInfo(AttributeType at) {
    attribute = at;
  }

  private String createEntryHtmlId(Date entryDate) {
    return getHtmlId() + "_entry_" + entryDate.getTime();
  }

  public void update() {
    clearErrors();
    for (TimeseriesEntryComponentModel entryCM : entryComponentModels) {
      entryCM.update();
    }
    newEntryComponentModel.update();

    boolean changed = performActions();

    if (changed) {
      sortEntries();
    }

    action = null;
    position = null;
  }

  private boolean performActions() {
    if (ADD_ACTION.equals(action)) {
      addEntry();
      return true;
    }
    else if (REMOVE_ACTION.equals(action) && position != null) {
      entryComponentModels.remove(position.intValue());
      return true;
    }
    return false;
  }

  private void addEntry() {
    if (check()) {
      Date entryDate = newEntryComponentModel.getDate();

      List<TimeseriesEntryComponentModel> currentEntries = ImmutableList.copyOf(entryComponentModels);
      for (TimeseriesEntryComponentModel entryModel : currentEntries) {
        if (new LocalDate(entryModel.getDate()).isEqual(new LocalDate(entryDate))) {
          // if the new entry is on the same day as an existing entry, remove the old entry before adding the new one
          entryComponentModels.remove(entryModel);
        }
      }

      TimeseriesEntry addedEntry = newEntryComponentModel.createEntry();

      TimeseriesEntryComponentModel addedEntryModel = new TimeseriesEntryComponentModel(attribute, getEntryComponentMode(),
          createEntryHtmlId(entryDate));
      addedEntryModel.initializeFrom(addedEntry);
      entryComponentModels.add(addedEntryModel);

      initNewEntryCM();
    }
  }

  private void initNewEntryCM() {
    newEntryComponentModel = new TimeseriesEntryComponentModel(attribute, ComponentMode.CREATE, "newTimeseriesEntryComponentModel");
    newEntryComponentModel.initializeFrom(null);
  }

  private void sortEntries() {
    Ordering<TimeseriesEntryComponentModel> dateOrdering = Ordering.natural().onResultOf(new Function<TimeseriesEntryComponentModel, Date>() {
      public Date apply(TimeseriesEntryComponentModel input) {
        return input.getDate();
      }
    });
    Collections.sort(entryComponentModels, dateOrdering.reverse());
  }

  public void configure(Timeseries target) {
    List<TimeseriesEntry> entriesToSet = Lists.newArrayList();
    for (TimeseriesEntryComponentModel entryCM : entryComponentModels) {
      TimeseriesEntry entryToAdd = entryCM.createEntry();
      entryCM.configure(entryToAdd);

      entriesToSet.add(entryToAdd);
    }

    target.setEntries(entriesToSet);
  }

  public void validate(Errors errors) {
    int count = 0;
    for (TimeseriesEntryComponentModel entry : entryComponentModels) {
      errors.pushNestedPath("entryComponentModels[" + count + "]");
      entry.validate(errors);
      errors.popNestedPath();
      count++;
    }
    if (!newEntryComponentModel.isEmpty()) {
      errors.pushNestedPath("newEntryComponentModel");
      newEntryComponentModel.validate(errors);
      errors.popNestedPath();
    }
  }

  @Override
  public boolean check() {
    // just need to check new entry, since old ones cannot be modified and we assume they are already valid
    if (!newEntryComponentModel.check()) {
      addErrorMessages(newEntryComponentModel.getErrorMessages());
    }
    return getErrorMessages().isEmpty();
  }

  /**
   * Convenience method for returning the value of the latest entry in this component model.
   * @return The value of the entry component model with the latest date
   */
  public String getLatestEntryValue() {
    if (entryComponentModels.isEmpty()) {
      return null;
    }

    // since the entries are sorted with the latest entry first, just return the first one
    return entryComponentModels.get(0).getValueModel().getAttributeValueAsString();
  }

  public String getAttributeName() {
    return attribute.getName();
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public List<TimeseriesEntryComponentModel> getEntryComponentModels() {
    return entryComponentModels;
  }

  public TimeseriesEntryComponentModel getNewEntryComponentModel() {
    return newEntryComponentModel;
  }

}
