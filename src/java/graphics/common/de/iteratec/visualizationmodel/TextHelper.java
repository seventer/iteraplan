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
package de.iteratec.visualizationmodel;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache.fop.render.RenderingContext;


public final class TextHelper {

  public static final FontRenderContext DEFAULT_FONT_RENDER_CONTEXT = new FontRenderContext(new AffineTransform(), false, false);

  private TextHelper() {
    // empty private constructor
  }

  /**
   * Helper method for determining text bounds
   * 
   * @param text the Text whose bounds are to be determined
   * @param ctx the {@link RenderingContext} to determine the actual text length
   * @return the {@link Text}s bounds
   */
  public static Rectangle2D getTextBounds(Text text, FontRenderContext ctx) {
    return getTextBounds(text.getText(), getFont(text), ctx);
  }

  /**
   * Helper method for determining text bounds
   * 
   * @param text The String whose bounds are to be determined
   * @param font the font used to calculate the bounds of the string
   * @param ctx The rendering context to determine the actual text length.
   * @return the bounds of the String rendered with the specified {@link Font}
   */
  public static Rectangle2D getTextBounds(String text, Font font, FontRenderContext ctx) {
    Rectangle2D stringBounds = font.getStringBounds(text, ctx);
    return new Rectangle2D.Double(stringBounds.getCenterX(), stringBounds.getCenterY(), stringBounds.getWidth() * 1.05 + 2, stringBounds.getHeight());
  }

  /**
   * Helper method for determining the font for a given text element.
   * 
   * @param t The text to be processed.
   * 
   * @return The corresponding font.
   */
  private static Font getFont(Text t) {
    Font f = Font.decode(t.getFontName());
    int result = 0;
    if (t.getTextStyle() != null) {
      result |= (t.getTextStyle().length > 0 && t.getTextStyle()[0]) ? Font.BOLD : 0;
      result |= (t.getTextStyle().length > 1 && t.getTextStyle()[1]) ? Font.ITALIC : 0;
    }
    f = f.deriveFont(result, t.getTextSize());
    return f;
  }
}
