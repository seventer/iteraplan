/*
 * Copyright 2011 Christian M. Schweda & iteratec
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iteratec.visualizationmodel.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ListMultimap;

import de.iteratec.visualizationmodel.ASymbol;


public class SquareBox {

  private static final float INCREMENT = 1.2f;

  private float              spacing;
  private List<ASymbol>      containedMapSymbols;
  private ASymbol            outerCompositeMapSymbol;
  private float              width2heightRatio;

  SquareBox() {
    this.containedMapSymbols = new LinkedList<ASymbol>();
    this.spacing = 20f;
    this.width2heightRatio = 4f / 3f;
  }

  public SquareBox(ASymbol outerCompositeMapSymbol, Collection<ASymbol> containedMapSymbols) {
    this();
    this.outerCompositeMapSymbol = outerCompositeMapSymbol;
    this.containedMapSymbols.addAll(containedMapSymbols);
  }

  public SquareBox(Collection<ASymbol> containedMapSymbols) {
    this(null, containedMapSymbols);
  }

  public void layout() {
    if (!this.containedMapSymbols.isEmpty()) {
      ListMultimap<Bounds, ASymbol> symmetryFreeProblem = Bounds.deriveSymmetryBrokenProblem(this.containedMapSymbols);
      float area = Bounds.deriveTotalArea(this.containedMapSymbols, this.spacing);

      Bounds outerBounds = null;
      while (outerBounds == null) {
        float height = (float) Math.sqrt(area / this.width2heightRatio);
        float width = height * this.width2heightRatio;
        outerBounds = layout(symmetryFreeProblem, this.spacing, width, height);
        area *= INCREMENT;
      }

      if (this.outerCompositeMapSymbol != null) {
        this.outerCompositeMapSymbol.setXpos((outerBounds.getWidth() + this.spacing) / 2f);
        this.outerCompositeMapSymbol.setYpos((outerBounds.getHeight() + this.spacing) / 2f);
        this.outerCompositeMapSymbol.setWidth(outerBounds.getWidth() + this.spacing);
        this.outerCompositeMapSymbol.setHeight(outerBounds.getHeight() + this.spacing);
      }
    }
  }

  private static Bounds layout(ListMultimap<Bounds, ASymbol> problem, float spacing, float width, float height) {
    if (problem.keySet().size() == 1) {
      List<ASymbol> symbols = problem.get(problem.keySet().iterator().next());
      return layoutEquallySizedMapSymbols(symbols, spacing, width, height);
    }
    else {
      return layoutMapSymbols(problem, spacing, width, height);
    }
  }

  private static Bounds layoutEquallySizedMapSymbols(List<ASymbol> mapSymbols, float spacing, float width, float height) {
    float sWidth = mapSymbols.get(0).getWidth() + spacing;
    float sHeight = mapSymbols.get(0).getHeight() + spacing;

    int cX = (int) Math.floor(width / sWidth);
    int cY = (int) Math.floor(height / sHeight);

    if (cX * cY < mapSymbols.size()) {
      return null;
    }

    float x = (sWidth + spacing) / 2f;
    float maxY = 0f;
    int count = 0;
    for (int col = 0; col < cX && count < mapSymbols.size(); col++) {
      float y = (sHeight + spacing) / 2f;
      for (int row = 0; row < cY && count < mapSymbols.size(); row++) {
        mapSymbols.get(count).setXpos(x);
        mapSymbols.get(count++).setYpos(y);
        y += sHeight;
        maxY = Math.max(maxY, y);
      }
      x += sWidth;
    }
    return new Bounds(x - (sWidth + spacing) / 2f, maxY - (sHeight + spacing) / 2f);
  }

  private static Bounds layoutMapSymbols(ListMultimap<Bounds, ASymbol> symmetryFreeProblem, float spacing, float width, float height) {
    List<ASymbol> mapSymbols = new LinkedList<ASymbol>();
    List<Bounds> bounds = new ArrayList<Bounds>(symmetryFreeProblem.keySet());
    Collections.sort(bounds, new NFDHComparator());
    for (Bounds bound : bounds) {
      List<ASymbol> bMapSymbols = symmetryFreeProblem.get(bound);
      Collections.sort(bMapSymbols, new IdComparator());
      mapSymbols.addAll(bMapSymbols);
    }
    return layoutMapSymbols(mapSymbols, spacing, width, height);
  }

  private static Bounds layoutMapSymbols(Collection<ASymbol> mapSymbols, float spacing, float width, float height) {
    float cY = 0f;
    float cX = 0f;
    float cHeight = 0f;
    float cWidth = 0f;

    Iterator<ASymbol> mapSymbolsIter = mapSymbols.iterator();
    ASymbol current = mapSymbolsIter.hasNext() ? mapSymbolsIter.next() : null;
    while (current != null && cHeight >= 0f && cY + current.getHeight() + spacing <= height) {
      cX = 0f;
      cHeight = -2f * spacing;
      while (current != null && cX + current.getWidth() + spacing <= width) {
        current.setXpos(cX + (current.getWidth() + spacing) / 2f);
        current.setYpos(cY + (current.getHeight() + spacing) / 2f);
        cHeight = Math.max(cHeight, current.getHeight());
        cX += current.getWidth() + spacing;
        cWidth = Math.max(cWidth, cX);
        current = mapSymbolsIter.hasNext() ? mapSymbolsIter.next() : null;
      }
      cY += cHeight + spacing;
    }

    if (current == null) {
      return new Bounds(cWidth, cY);
    }
    else {
      return null;
    }
  }

  private static class NFDHComparator implements Comparator<Bounds>, Serializable {
    private static final long serialVersionUID = 1942595025232247428L;

    public int compare(Bounds o1, Bounds o2) {
      return o1.getHeight() < o2.getHeight() ? 1 : (o1.getHeight() > o2.getHeight() ? -1 : 0);
    }
  }
}