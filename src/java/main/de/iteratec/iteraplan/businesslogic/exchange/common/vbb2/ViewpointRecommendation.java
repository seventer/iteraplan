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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.presentation.ajax.VbbConfigurationService.Tag;


/**
 * Composite recommendation for the variables of a viewpoint.
 * 
 * Recommendations are extracted for the UI over the toRecommendationMap method.
 * <br>
 * For all recommendations, the following hold:<br>
 *  - Every recommendation has a priority, which is either 0 (high) or -1 (low).<br>
 *  - Recommendations are either flat or structured.<br>
 *  Flat recommendations are single-level, without children.<br>
 *  Structured recommendations can have children. Children always have priority -1 (low).<br>
 *  Persistent and localized names should not be null or empty.
 *  <br><br>Usage of this class<br>
 *  <li>Before actual recommendation values (variable bindings) can be added,
 *   a recommendation variable has to be registered. This is done by calling 
 *   either addFlat or addStructured. The key is the name of the viewpoint variable
 *   for which recommendations will be added.</li>
 *   <li>A flat recommendation is added by calling the append method of the
 *   FlatVariableRecommendation. It can have no children.</li>
 *   <li>A structured recommendation is added by calling the append method of the
 *   StructuredVariableRecommendation. The method returns a PrimaryRecommendation, which,
 *   like the flat variable recommendation is top-level. One sub-layer of child recommendations
 *   can be added by calling the append method of the PrimaryRecommendation.</li>
 */
public abstract class ViewpointRecommendation {

  /** Priority of a recommendation entry. 
   * The user interface can filter or order the recommended entries.
   */
  public enum Priority {
    HIGH(0), LOW(-1);

    private final int prio;

    private Priority(int prio) {
      this.prio = prio;
    }

    public int getIntValue() {
      return this.prio;
    }
  }

  public class FlatVariableRecommendation {
    private final List<Tag> tagList;

    FlatVariableRecommendation(List<Tag> tagList) {
      this.tagList = tagList;
    }

    public void append(String persistentName, String localizedName, Priority priority) {
      tagList.add(tag(persistentName, localizedName, priority, true));
    }
  }

  public class StructuredVariableRecommendation {

    private final List<Tag> tagList;

    StructuredVariableRecommendation(List<Tag> tagList) {
      this.tagList = tagList;
    }

    public PrimaryRecommendation append(String persistentName, String localizedName, Priority priority) {
      Tag tag = tag(persistentName, localizedName, priority, true);
      tagList.add(tag);
      return new PrimaryRecommendation(tag);
    }

  }

  public class PrimaryRecommendation {
    private final Tag tag;

    PrimaryRecommendation(Tag tag) {
      this.tag = tag;
    }

    public void append(String persistentName, String localizedName) {
      List<Tag> newChildren = Lists.newLinkedList(tag.getChildren());
      newChildren.add(tag(persistentName, localizedName, Priority.LOW, false));
      tag.setChildren(newChildren);
    }

  }

  public FlatVariableRecommendation addFlat(String key) {
    List<Tag> tags = Lists.newArrayList();
    put(key, tags);
    return new FlatVariableRecommendation(tags);
  }

  public StructuredVariableRecommendation addStructured(String key) {
    List<Tag> tags = Lists.newArrayList();
    put(key, tags);
    return new StructuredVariableRecommendation(tags);
  }

  protected abstract void put(String key, List<Tag> tags);

  public abstract ViewpointRecommendation withPrefix(String prefix);

  public abstract Map<String, Collection<Tag>> toRecommendationMap();

  public static ViewpointRecommendation create() {
    return new Root();
  }

  private Tag tag(String persistentName, String localizedName, Priority priority, boolean root) {
    return new Tag(persistentName, localizedName, priority.getIntValue(), root);
  }

  private static final class Root extends ViewpointRecommendation {

    private final Map<String, List<Tag>> recommendationsMap = Maps.newHashMap();

    /**{@inheritDoc}**/
    @Override
    protected void put(String key, List<Tag> tags) {
      recommendationsMap.put(key, tags);
    }

    public ViewpointRecommendation withPrefix(String prefix) {
      return new Prefixed(prefix);
    }

    public Map<String, Collection<Tag>> toRecommendationMap() {
      Map<String, Collection<Tag>> result = Maps.newHashMap();
      result.putAll(recommendationsMap);
      return result;
    }

    private final class Prefixed extends ViewpointRecommendation {

      private final String localPrefix;

      protected Prefixed(String prefix) {
        this.localPrefix = prefix;
      }

      /**{@inheritDoc}**/
      @Override
      protected void put(String key, List<Tag> tags) {
        recommendationsMap.put(localPrefix + "." + key, tags);
      }

      /**{@inheritDoc}**/
      @Override
      public ViewpointRecommendation withPrefix(String prefix) {
        return new Prefixed(localPrefix + "." + prefix);
      }

      /**{@inheritDoc}**/
      @Override
      public Map<String, Collection<Tag>> toRecommendationMap() {
        return Root.this.toRecommendationMap();
      }
    }

  }
}
