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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.Line;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.Document;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.IteraplanProperties;


/**
 *
 */
public class JFreeChartSvgRenderer {

  protected static final String     MASTER_TITLE               = "TitleRoot";
  protected static final float      MARGIN_DOWN_GENERATED_TEXT = 40;
  protected static final int        MARGIN_TOP                 = 20;
  protected static final int        MARGIN_LEFT                = 20;
  protected static final String     COLOR_LOGO                 = "#b100af";
  protected static final String     CSS_GENERATED_INFORMATION  = "generatedInformation";
  private static final int          NAKED_MARGIN               = 50;
  private static final int          MARGIN                     = 200;
  private final IteraplanProperties properties                 = IteraplanProperties.getProperties();

  public String getMimeType() {
    return "image/svg+xml";
  }

  byte[] renderJFreeChart(JFreeChart chart, float width, float height, boolean naked, Date fromDate, Date toDate) throws IOException {
    String svgNamespaceUri = SVGDOMImplementation.SVG_NAMESPACE_URI;
    Document doc = SVGDOMImplementation.getDOMImplementation().createDocument(svgNamespaceUri, "svg", null);

    String generatedText = "Generated " + DateUtils.formatAsStringToLong(new Date(), UserContext.getCurrentLocale()) + " by "
        + MessageAccess.getStringOrNull("global.applicationname", UserContext.getCurrentLocale()) + " " + properties.getBuildId();
    String drawingInfo = MessageAccess.getStringOrNull("graphicalExport.timeline.drawInfo", UserContext.getCurrentLocale()) + ": "
        + DateUtils.formatAsString(fromDate, UserContext.getCurrentLocale()) + " -> "
        + DateUtils.formatAsString(toDate, UserContext.getCurrentLocale());

    SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(doc);
    ctx.setComment(generatedText);
    SVGGraphics2D svgGraphics = new SVGGraphics2D(ctx, false);

    if (!naked) {
      //Render the chart to the SVG graphics object
      chart.draw(svgGraphics, new Rectangle(20, 20, Math.round(width - MARGIN), Math.round(height - MARGIN)));

      //Add logo and generated text
      int widthIntForLogo = Math.round(width + 40 - MARGIN);
      int heightIntForLogo = Math.round(height - MARGIN + 20);

      int xLogoUpperRightCorner[] = { widthIntForLogo - 40, widthIntForLogo, widthIntForLogo, widthIntForLogo - 8, widthIntForLogo - 8,
          widthIntForLogo - 40 };
      int yLogoUpperRightCorner[] = { MARGIN_TOP, MARGIN_TOP, MARGIN_TOP + 40, MARGIN_TOP + 40, MARGIN_TOP + 8, MARGIN_TOP + 8 };

      GeneralPath logoUpperRightCorner = new GeneralPath();
      logoUpperRightCorner.moveTo(xLogoUpperRightCorner[0], yLogoUpperRightCorner[0]);
      for (int i = 1; i < xLogoUpperRightCorner.length; i++) {
        logoUpperRightCorner.lineTo(xLogoUpperRightCorner[i], yLogoUpperRightCorner[i]);
      }
      logoUpperRightCorner.closePath();
      svgGraphics.setColor(Color.decode(COLOR_LOGO));
      svgGraphics.fill(logoUpperRightCorner);
      svgGraphics.draw(logoUpperRightCorner);

      int xLogoLowerLeftCorner[] = { MARGIN_LEFT, MARGIN_LEFT + 8, MARGIN_LEFT + 8, MARGIN_LEFT + 40, MARGIN_LEFT + 40, MARGIN_LEFT };
      int yLogoLowerLeftCorner[] = { heightIntForLogo, heightIntForLogo, heightIntForLogo + 32, heightIntForLogo + 32, heightIntForLogo + 40,
          heightIntForLogo + 40 };

      GeneralPath logoLowerLeftCorner = new GeneralPath();
      logoLowerLeftCorner.moveTo(xLogoLowerLeftCorner[0], yLogoLowerLeftCorner[0]);
      for (int i = 1; i < xLogoLowerLeftCorner.length; i++) {
        logoLowerLeftCorner.lineTo(xLogoLowerLeftCorner[i], yLogoLowerLeftCorner[i]);
      }
      logoLowerLeftCorner.closePath();
      svgGraphics.setColor(Color.BLACK);
      svgGraphics.fill(logoLowerLeftCorner);
      svgGraphics.draw(logoLowerLeftCorner);

      Font f = new Font(null, Font.ITALIC, 12);
      svgGraphics.setFont(f);
      FontMetrics fontMetrics = svgGraphics.getFontMetrics(f);
      int charsWidthInfo = fontMetrics.stringWidth(drawingInfo);
      svgGraphics.drawString(drawingInfo, width - MARGIN - charsWidthInfo, height - MARGIN + MARGIN_DOWN_GENERATED_TEXT);
      int charsWidth = fontMetrics.stringWidth(generatedText);
      svgGraphics.drawString(generatedText, width - MARGIN - charsWidth, height - MARGIN + MARGIN_DOWN_GENERATED_TEXT + 20);

    }
    else {
      chart.draw(
          svgGraphics,
          new Rectangle(20, 20, Math.round(JFreeChartLineGraphicCreator.DEFAULT_HEIGHT - NAKED_MARGIN), Math
              .round(JFreeChartLineGraphicCreator.DEFAULT_HEIGHT - NAKED_MARGIN)));
    }

    svgGraphics.setSVGCanvasSize(new Dimension((int) width, (int) height));

    //Convert the SVGGraphics2D object to SVG XML 
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Writer out = new OutputStreamWriter(baos, "UTF-8");
    svgGraphics.stream(out, true);
    byte[] originalSvgXml = baos.toByteArray();

    //    return originalSvgXml;
    return addAdditionalAttributes(originalSvgXml);
  }

  private static byte[] addAdditionalAttributes(byte[] originalSvgXml) {
    //Implemented using basic string manipulation for enhanced performance
    try {
      String xml = new String(originalSvgXml, "UTF-8");
      int svgStartIndex = xml.indexOf("<svg");
      int svgEndIndex = xml.indexOf('>', svgStartIndex);
      StringBuilder sb = new StringBuilder();
      sb.append(xml.subSequence(0, svgEndIndex));
      sb.append(" viewBox=\"0 0 " + JFreeChartLineGraphicCreator.DEFAULT_WIDTH + " " + JFreeChartLineGraphicCreator.DEFAULT_HEIGHT + "\"");
      sb.append(xml.substring(svgEndIndex));

      return sb.toString().getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IteraplanTechnicalException(e);
    }
  }
}
