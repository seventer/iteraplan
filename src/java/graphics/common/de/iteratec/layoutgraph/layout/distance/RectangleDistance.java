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
package de.iteratec.layoutgraph.layout.distance;

import de.iteratec.layoutgraph.LayoutNode;


public class RectangleDistance implements DistanceOperation {
  public DistResult distance(LayoutNode node1, LayoutNode node2) {
    DistResult result = new DistResult();
    double x1 = node1.getX();
    double y1 = node1.getY();
    double width1 = node1.getWidth();
    double height1 = node1.getHeight();
    double x2 = node2.getX();
    double y2 = node2.getY();
    double width2 = node2.getWidth();
    double height2 = node2.getHeight();

    result.setDx(Math.abs(x2 - x1) - width1 / 2 - width2 / 2);
    result.setDx((result.getDx() > 0) ? result.getDx() : 0);

    result.setDy(Math.abs(y2 - y1) - height1 / 2 - height2 / 2);
    result.setDy((result.getDy() > 0) ? result.getDy() : 0);

    result.setDist((result.getDx() > result.getDy()) ? result.getDx() : result.getDy());

    result.setDx(result.getDx() * (x2 > x1 ? 1 : -1));
    result.setDy(result.getDy() * (y2 > y1 ? 1 : -1));
    return result;
  }

  public String getName() {
    return "Rectangle distance";
  }
}
