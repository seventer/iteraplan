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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import de.iteratec.iteraplan.businesslogic.service.SavedQueryService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.StringUtil;
import de.iteratec.iteraplan.model.queries.SavedQuery;


/**
 * Parser Service to convert special interaplan Syntax into html text
 */
public class IteraplanSyntaxParserServiceImpl implements IteraplanSyntaxParserService {

  public static final String KEY_WORD_DIAGRAM = "Diagram";

  public static final String START_TAG_D      = "<" + KEY_WORD_DIAGRAM;
  public static final String END_TAG_D        = "</" + KEY_WORD_DIAGRAM + ">";

  private WikiParserService  wikiParserService;
  private SavedQueryService  savedQueryService;

  private final Logger       logger           = Logger.getIteraplanLogger(getClass());

  /**
   * Setter for WikiParserService.
   * This is needed for Dependency Injection.
   * 
   * @param wikiParserService
   */
  public void setWikiParserService(WikiParserService wikiParserService) {
    this.wikiParserService = wikiParserService;
  }

  /**
   * Setter for SavedQueryService.
   * This is needed for Dependency Injection.
   * 
   * @param savedQueryService
   */
  public void setSavedQueryService(SavedQueryService savedQueryService) {
    this.savedQueryService = savedQueryService;
  }

  /**{@inheritDoc}**/
  public boolean containsIteraplanSyntax(String source) {
    return (source != null && source.contains(START_TAG_D));
  }

  /**{@inheritDoc}**/
  public String convertIteraplanSyntax(String source, String userAgent) {
    return convertIteraplanSyntax(source, userAgent, null);
  }

  /**{@inheritDoc}**/
  public String convertIteraplanSyntax(String source, String userAgent, Integer refID) {
    WikiTextModel outputModel = extractAndConvertIteraplanElements(source, userAgent, refID);
    outputModel.setSource(wikiParserService.convertWikiText(outputModel.getSource()));
    String output = includeIterplanElements(outputModel);
    output = "<div style=\"display: table\">" + output + "</div>";
    return output;
  }

  /**{@inheritDoc}**/
  public WikiTextModel extractAndConvertIteraplanElements(String source, String userAgent, Integer refId) {
    WikiTextModel wikiTextModel = extractIteraplanSyntaxElements(source);
    wikiTextModel = convertIteraplanSyntaxElements(wikiTextModel, userAgent, refId);
    return wikiTextModel;
  }

  /**{@inheritDoc}**/
  public String includeIterplanElements(WikiTextModel source) {
    String text = source.getSource();
    HashMap<Integer, String> parstIteraplanSyntaxElements = source.getParstIteraplanSyntaxElements();
    Iterator<Entry<Integer, String>> iter;

    for (iter = parstIteraplanSyntaxElements.entrySet().iterator(); iter.hasNext();) {
      Entry<Integer, String> entry = iter.next();

      String makeshift = "<span id=\"" + entry.getKey() + "\"></span>";

      int posStart = text.indexOf(makeshift);
      int posEnd = posStart + makeshift.length() - 1;

      text = StringUtil.replaceStringAt(text, posStart, posEnd, entry.getValue());

    }

    return text;
  }

  /**
   * Extract iteraplan specific elements out of the source and
   * marked the original positions in the source with xwiki html markups and an ID
   * 
   * @param source
   * @return WikiTextModel
   */
  private WikiTextModel extractIteraplanSyntaxElements(String source) {
    WikiTextModel wikiTextModel = new WikiTextModel();
    wikiTextModel.setSource(source);

    wikiTextModel = searchIteraplanTags(wikiTextModel, START_TAG_D, END_TAG_D); // searching for Diagram Tags

    return wikiTextModel;
  }

  @SuppressWarnings("boxing")
  private WikiTextModel searchIteraplanTags(WikiTextModel wikiTextModel, String startTag, String endTag) {

    String source = wikiTextModel.getSource();
    HashMap<Integer, String> iteraplanSyntaxElements = wikiTextModel.getIteraplanSyntaxElements();

    int currentPosition = 0;
    int endTagPosition = 0;

    int id = iteraplanSyntaxElements.size();

    while (currentPosition != -1 || currentPosition >= source.length()) {
      id++;

      // looking for the next start tag
      currentPosition = source.indexOf(startTag, currentPosition);

      if (currentPosition == -1) {
        break;
      }

      // looking for the next end tag
      endTagPosition = source.indexOf(endTag, currentPosition + 1);

      if (endTagPosition == -1) {
        break;
      }

      // separate and  put the found iteraplan syntax element into the hashmap
      String interaplanElement = source.substring(currentPosition, endTagPosition + endTag.length());
      source = StringUtil.replaceStringAt(source, currentPosition, currentPosition + interaplanElement.length() - 1, "{{html}}<span id='" + id
          + "' />{{/html}}");

      iteraplanSyntaxElements.put(id, interaplanElement);

      currentPosition++;
    }

    wikiTextModel.setIteraplanSyntaxElements(iteraplanSyntaxElements);
    wikiTextModel.setSource(source);

    return wikiTextModel;
  }

  /**
   * Convert the special Iteraplan Syntax Elements into SVG Text.
   * 
   * @param wikiTextModel
   * @param userAgent
   * @param refId
   * @return WikiTextModel
   */
  private WikiTextModel convertIteraplanSyntaxElements(WikiTextModel wikiTextModel, String userAgent, Integer refId) {
    HashMap<Integer, String> iteraplanSyntaxElements = wikiTextModel.getIteraplanSyntaxElements();
    HashMap<Integer, String> parstIteraplanSyntaxElements = wikiTextModel.getParstIteraplanSyntaxElements();

    Iterator<Entry<Integer, String>> iter;

    if (!iteraplanSyntaxElements.isEmpty()) {
      for (iter = iteraplanSyntaxElements.entrySet().iterator(); iter.hasNext();) {
        Entry<Integer, String> entry = iter.next();

        String element = entry.getValue();

        if (element.startsWith(START_TAG_D)) {
          parstIteraplanSyntaxElements.put(entry.getKey(), parseDiagramElement(element, userAgent, refId));
        }
      }
    }

    wikiTextModel.setParstIteraplanSyntaxElements(parstIteraplanSyntaxElements);
    return wikiTextModel;
  }

  /**
   * Parse Diagram Tag elements into HTML output format.
   * 
   * @param element
   * @param userAgent
   * @param refId
   * @return special html output format from viewpoints
   */
  private String parseDiagramElement(String element, String userAgent, Integer refId) {

    String diagramID = "";
    String param = "";

    boolean useRefID = true;

    int indexViewPoint = element.indexOf(START_TAG_D);
    int startTag = element.indexOf('>');
    int endTag = element.indexOf(END_TAG_D);

    if (startTag >= 0 && startTag < endTag) {
      diagramID = element.substring(startTag + 1, endTag); // read the ID between the start and the end Tag
      param = element.substring(indexViewPoint + START_TAG_D.length(), startTag); // read all parameters inside the start Tag
    }

    if (diagramID != null && !"".equals(diagramID)) {
      // get the saveQuery with the diagramID
      SavedQuery savedQuery = loadSavedQuery(diagramID);

      // compare the result type from the savedQuery with the result type of the referenced query
      if (refId != null && savedQuery != null) {
        SavedQuery refQuery = savedQueryService.getSavedQuery(refId);
        if (savedQuery.getResultBbType() == null || !refQuery.getResultBbType().getName().equals(savedQuery.getResultBbType().getName())) {
          useRefID = false;
        }
      }

      // get the width and the hight
      Double width = getParameterFromString(param, "width");
      Double height = getParameterFromString(param, "height");

      StringBuilder sbResult = new StringBuilder();
      sbResult.append("<div class='viewBoxPanel'>");

      sbResult.append("<div class='viewBoxPanel-body'>");

      // add warning messages if there exist some exceptions
      sbResult.append(getWarnings(savedQuery, useRefID));

      if (savedQuery != null) {
        if (useRefID) {
          sbResult.append(getDiagramAsHtmlTag(savedQuery, userAgent, refId, width, height));
        }
        else {
          sbResult.append(getDiagramAsHtmlTag(savedQuery, userAgent, null, width, height));
        }
      }

      // add warning messages if there exist some exceptions
      sbResult.append(getWarnings(savedQuery, useRefID));

      sbResult.append("</div></div>");

      return sbResult.toString();
    }
    return "";
  }

  private SavedQuery loadSavedQuery(String diagramID) {
    try {
      return savedQueryService.getSavedQuery(new Integer(diagramID));
    } catch (IteraplanTechnicalException e) {
      return null;
    }
  }

  /**
   * Parse Diagram Tag elements into an object Tag or img Tag which contains an a parameterized URL from a SavedQuery-object to accessing diagrams.
   * <br><br>
   * For different browsers different solutions are needed.<br>
   * <b>Internet Explorer 8 or earlier:</b><br>
   * Dose not support SVG. Therefore it is necessary to provide the images in a raster format like png.
   * For IE8 or earlier, viewpoint graphics are incorporated with the img tag as png file.<br><br>
   * <b>Internet Explorer 9:</b><br>
   * Since IE9, the Internet Explorer support the basic SVG feature set.
   * But he has problems with the onload function inside of object elements.
   * If the SVG graphics does written within the document, it exist also different problems with the representation.
   * Therefore, viewpoint graphics are also incorporated with the img tag as png file .<br><br>
   * <b>All other Browser:</b><br>
   * The most other browser support the integration from SVG.
   * Therefore, Viewpoints graphics are incorporated with the object tag as svg file.<br>
   * 
   * @param savedQuery
   * @param userAgent
   * @param refId
   * @param width
   * @param height
   * @return Return an string with an HTML-Object-Tag or an IMG-Tag<br>
   * <b>Example the HTML-Object-Tag:</b><br>
   * {@literal <object data=[URL for the Image (as SVG)] /> }<br>
   * <br>
   * [URL] = {@literal <path to iteraplan>/show/graphicalreporting/../fastexport/generateSavedQuery.do?id=<diagram id>&savedQueryType=<Type of the Saved Query>&outputMode=inline&resultFormat=<svg|png>}
   */
  private String getDiagramAsHtmlTag(SavedQuery savedQuery, String userAgent, Integer refId, Double width, Double height) {

    int randomID = (int) ((Math.random() * 1000000) + 1);

    StringBuilder sb;

    // create the loading image
    sb = new StringBuilder();
    sb.append("<span id='");
    sb.append(randomID);
    sb.append("'>");
    sb.append("<img border='0' src='../images/loading.gif' />");
    sb.append("</span>");

    if (userAgent.contains("MSIE")) {
      // in case of IE
      // include the image as with the img tag as png
      sb.append("<img src=\"");
      sb.append(createDiagramUrl(savedQuery, refId, "png", width, height));
      sb.append("\" onload=\"onGraphicLoaded('" + randomID + "')\" onerror=\"onGraphicLoadingError('" + randomID + "', '"
          + MessageAccess.getString("errors.viewpoint.general") + "')\" ");
      sb.append("/>");
    }
    else if (userAgent.contains("Firefox")) {

      String svg = createDiagramUrl(savedQuery, refId, "svg", width, height);
      svg = svg.replace("\\\'", "\\\\\'");

      // in case of FireFox
      sb.append("<iframe id=\"object" + randomID + "\" style=\"width:100%;height:100%\" onload=\"onSvgGraphicLoaded('" + randomID + "', this, '"
          + MessageAccess.getString("errors.viewpoint.general")
          + "')\" allowtransparency=true sandbox=\"allow-same-origin allow-forms allow-scripts\" scrolling=no ");
      sb.append("frameborder=0 ");
      sb.append("src=\"" + svg + "\" >");
      sb.append(MessageAccess.getString("errors.viewpoint.general"));
      sb.append("</iframe>");
    }
    else {
      // in case of all other browsers

      String svg = createDiagramUrl(savedQuery, refId, "svg", width, height);

      sb.append("<iframe id=\"object" + randomID + "\" style=\"width:100%;height:100%\" onload=\"onSvgGraphicLoaded('" + randomID + "', this, '"
          + MessageAccess.getString("errors.viewpoint.general")
          + "')\" allowtransparency=true sandbox=\"allow-same-origin allow-forms allow-scripts\" scrolling=no ");
      sb.append("frameborder=0 ");
      sb.append("src=\"" + svg + "\" >");
      sb.append("<img src=\"");
      sb.append(createDiagramUrl(savedQuery, refId, "png", width, height));
      sb.append("\" onload=\"onGraphicLoaded('" + randomID + "')\" onerror=\"onGraphicLoadingError('" + randomID + "', '"
          + MessageAccess.getString("errors.viewpoint.general") + "')\" ");
      sb.append("/>");
      sb.append("</iframe>");
    }
    return sb.toString();
  }

  /**
   * /**
   * Creates a parameterized URL from a SavedQuery-object to accessing diagrams.
   *
   * @param savedQuery
   * @param refId
   * @param format (svg|jpg|png)
   * @param width
   * @param height
   * @return parameterized URL to accessing diagrams
   */
  private String createDiagramUrl(SavedQuery savedQuery, Integer refId, String format, Double width, Double height) {
    // build the URL
    StringBuilder sb = new StringBuilder();
    sb.append("../show/fastexport/generateSavedQuery.do?");
    sb.append("id=");
    sb.append(savedQuery.getId());
    if (refId != null) {
      sb.append("&queryReferenceId=");
      sb.append(refId.intValue());
    }
    sb.append("&savedQueryType=");
    sb.append(savedQuery.getType());
    sb.append("&outputMode=inline");
    sb.append("&resultFormat=");
    sb.append(format);
    sb.append("&nakedExport=true");
    if (width != null) {
      sb.append("&width=");
      sb.append(width);
    }
    if (height != null) {
      sb.append("&height=");
      sb.append(height);
    }

    // escape #
    String diagramUrl = sb.toString().replace("#", "%23");
    logger.debug("generated diagram URL: {0}", diagramUrl);

    return diagramUrl;
  }

  private Double getParameterFromString(String source, String paramName) {
    String extendedName = paramName + "=\"";

    int start = source.indexOf(extendedName);

    if (start == -1) {
      return null;
    }

    int end = source.indexOf("\"", start + extendedName.length());
    if (end == -1) {
      return null;
    }

    return Double.valueOf(source.substring(start + extendedName.length(), end));
  }

  /**
   * Return warning messages for different kinds of exceptions.
   * @param savedQuery
   * @param useRefID
   * @return String with warning messages
   */
  private String getWarnings(SavedQuery savedQuery, boolean useRefID) {

    StringBuilder sbResult = new StringBuilder();

    // exception rule for VBB Cluster Diagrams and if the refId is null -> than show an additional warning below the graphic
    if (savedQuery == null) {
      sbResult.append("<div style='color:red'>");
      sbResult.append("<i class=\"icon-warning-sign\"></i>:");
      sbResult.append(MessageAccess.getString("customDashboard.savedQueryNotFound.warning"));
      sbResult.append("</div>");
    }
    else if (savedQuery.getType().getValue().equals(Constants.REPORTS_EXPORT_GRAPHICAL_VBB_CLUSTER)) {
      sbResult.append("<br/>");
      sbResult.append("<div style='color:red'>");
      sbResult.append("<i class=\"icon-warning-sign\"></i>:");
      sbResult.append(MessageAccess.getString("customDashboard.vbbcluster.warning"));
      sbResult.append("</div>");
    }
    else if (!useRefID) {
      sbResult.append("<br/>");
      sbResult.append("<div style='color:red'>");
      sbResult.append("<i class=\"icon-warning-sign\"></i> ");
      sbResult.append(MessageAccess.getString("customDashboard.incorrectFilter.warning"));
      sbResult.append("</div>");
    }

    return sbResult.toString();
  }
}