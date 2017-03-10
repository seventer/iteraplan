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
package de.iteratec.iteraplan.businesslogic.service.wiki;

import java.io.StringReader;

import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.rendering.converter.ConversionException;
import org.xwiki.rendering.converter.Converter;
import org.xwiki.rendering.renderer.printer.DefaultWikiPrinter;
import org.xwiki.rendering.renderer.printer.WikiPrinter;
import org.xwiki.rendering.syntax.Syntax;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.presentation.tags.TagUtils;


/**
 * This class implements a service for wiki text parsing, with the third-party component XWiki.
 * 
 * @see "http://code.xwiki.org/xwiki/bin/view/Modules/RenderingModule"
 * @author rfe
 */
public class XWikiParserServiceImpl implements WikiParserService {

  private final Logger     logger = Logger.getIteraplanLogger(getClass());
  /** The default iteraplan wiki syntax style. */
  private WikiSyntaxStyles iteraplanWikiSyntax;
  /** The current iteraplan wiki syntax style configuration. */
  private Syntax           xwikiSyntax;
  /** XWiki Converter component to convert between one syntax to another. */
  private Converter        xwikiConverter;

  /** {@inheritDoc} */
  public WikiSyntaxStyles getIteraplanWikiSyntax() {
    return iteraplanWikiSyntax;
  }

  /** {@inheritDoc} */
  public void setIteraplanWikiSyntax(WikiSyntaxStyles iteraplanWikiSyntax) {
    this.iteraplanWikiSyntax = iteraplanWikiSyntax;
  }

  /** {@inheritDoc} */
  public void initializeWikiParserService() {
    // Get the current iteraplan setting/property which wiki syntax to use and get the
    // corresponding xwiki syntax style.
    IteraplanProperties properties = IteraplanProperties.getProperties();
    this.iteraplanWikiSyntax = WikiSyntaxStyles.valueOf(WikiSyntaxStyles.class, properties.getProperty(IteraplanProperties.WIKI_SYNTAX));
    this.xwikiSyntax = this.getSyntaxForProperty(this.iteraplanWikiSyntax);

    try {
      logger.debug("initialize XWiki EmbeddableComponentManager");

      // Initialize XWiki Rendering components and allow getting instances
      EmbeddableComponentManager xwikiEcm = new EmbeddableComponentManager();
      xwikiEcm.initialize(Thread.currentThread().getContextClassLoader());
      // Use a the Converter component to convert between one syntax to another.
      this.xwikiConverter = xwikiEcm.getInstance(Converter.class);

    } catch (Exception e) {
      logger.warn(e.toString());
    }

  }

  /** {@inheritDoc} */
  public String convertWikiText(String source) {
    // convert the current wiki notation to unescaped html text
    String output = convertWikiTextToHtmlText(source);

    // remove the outlined paragraph to prevent layout problems
    if (output.startsWith("<p>") && output.endsWith("</p>")) {
      output = output.substring(3, output.length() - 4);
    }

    output = TagUtils.decorateFileLinks(output);
    return output;
  }

  /** {@inheritDoc} */
  public String convertWikiTextToHtmlText(String source) {
    String result = source;

    Syntax destinationSyntax = Syntax.XHTML_1_0;

    try {
      result = xwikiConvert(source, destinationSyntax);

    } catch (Exception e) {
      logger.warn(e.toString());
    }
    return result;
  }

  /** {@inheritDoc} */
  public String convertWikiTextToPlainText(String source) {
    String result = source;

    Syntax destinationSyntax = Syntax.PLAIN_1_0;

    try {
      result = xwikiConvert(source, destinationSyntax);

    } catch (Exception e) {
      logger.warn(e.toString());
    }
    return result;
  }

  private String xwikiConvert(String source, Syntax destinationSyntax) throws ConversionException {

    // Convert input in XWiki Syntax 2.0 into XHTML. The result is stored in the printer.
    WikiPrinter printer = new DefaultWikiPrinter();
    xwikiConverter.convert(new StringReader(source), this.xwikiSyntax, destinationSyntax, printer);

    String result = printer.toString();

    if (logger.isDebugEnabled()) {
      logger.debug("parse " + this.xwikiSyntax + ": (" + source + ") to " + destinationSyntax + ": (" + result + ")");
    }
    return result;
  }

  /**
   * Returns the XWiki correspondent to the interface enum WikiSyntax
   * 
   * @param syntax
   * @return XWiki syntax
   */
  private Syntax getSyntaxForProperty(WikiSyntaxStyles syntax) {
    Syntax result;

    switch (syntax) {
      case MEDIAWIKI:
        result = Syntax.MEDIAWIKI_1_0;
        break;
      case CONFLUENCE:
        result = Syntax.CONFLUENCE_1_0;
        break;
      case XWIKI:
        result = Syntax.XWIKI_2_0;
        break;
      case JSPWIKI:
        result = Syntax.JSPWIKI_1_0;
        break;
      case TWIKI:
        result = Syntax.TWIKI_1_0;
        break;
      case CREOLE:
        result = Syntax.CREOLE_1_0;
        break;

      default:
        result = Syntax.CONFLUENCE_1_0;
        break;
    }

    return result;
  }
}
