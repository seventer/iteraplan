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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan;

import java.io.Serializable;
import java.util.Date;

import de.iteratec.iteraplan.model.BuildingBlock;


public abstract class TimelineFeature implements Serializable {

  private static final long              serialVersionUID = 1835991374965513738L;

  private final MasterplanRowTypeOptions owner;
  private final int                      position;

  TimelineFeature(int position, MasterplanRowTypeOptions owner) {
    this.position = position;
    this.owner = owner;
  }

  protected final MasterplanRowTypeOptions getOwner() {
    return this.owner;
  }

  public final int getPosition() {
    return this.position;
  }

  public abstract String getName();

  public abstract String getCaption(BuildingBlock buildingBlock);

  public abstract Date getFrom(BuildingBlock buildingBlock);

  public abstract Date getTo(BuildingBlock buildingBlock);

  public abstract String getDefaultColorHex();

  public abstract boolean isRuntimePeriod();

  public abstract boolean equals(Object obj);

  public abstract int hashCode();

}
