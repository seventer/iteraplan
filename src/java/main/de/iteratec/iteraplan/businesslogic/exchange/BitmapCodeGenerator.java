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
package de.iteratec.iteraplan.businesslogic.exchange;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.xmlgraphics.util.io.Base64EncodeStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;


public final class BitmapCodeGenerator {

  private static final Logger LOGGER          = Logger.getIteraplanLogger(BitmapCodeGenerator.class);

  public final static int     URL_CODE_WIDTH  = 250;
  public final static int     URL_CODE_HEIGHT = 250;

  private BitmapCodeGenerator() {
    // empty constructor
  }

  public static String generateFastExportUrlCode(String serverUrl, Integer savedQueryId, String reportType) {
    String url = GeneralHelper.createFastExportUrl(serverUrl, reportType, savedQueryId);
    try {
      return generateB64Png(url, URL_CODE_WIDTH, URL_CODE_HEIGHT);
    } catch (WriterException e) {
      LOGGER.error(e);
    } catch (IOException e) {
      LOGGER.error(e);
    }

    return "";
  }

  private static String generateB64Png(String content, int width, int height) throws WriterException, IOException {
    QRCodeWriter w = new QRCodeWriter();
    BitMatrix qrcode = w.encode(content, BarcodeFormat.QR_CODE, width, height);

    BufferedImage image = MatrixToImageWriter.toBufferedImage(qrcode);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Base64EncodeStream b64 = new Base64EncodeStream(baos);

    ImageIO.write(image, "png", b64);

    return baos.toString("UTF-8");

  }

}
