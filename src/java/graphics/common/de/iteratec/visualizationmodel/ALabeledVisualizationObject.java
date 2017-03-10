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
package de.iteratec.visualizationmodel;

import java.awt.geom.Rectangle2D;

import de.iteratec.visualizationmodel.renderer.AVisualizationObjectVisitor;

public abstract class ALabeledVisualizationObject extends APlanarSymbol {

  private static final float STANDARD_TEXT_START_END_MARGIN = 0.1f;

	private Text text;
	
	/**
	 * Gets the text obj.
	 * 
	 * @return the primitive's associated text object
	 */
	public Text getText() {
		return text;
	}
	
	/**
	 * Sets the text obj.
	 * 
	 * @param text the primitive's associated text object
	 */
	public void setText(Text text) {
		this.text = text;
		
		this.text.setWidth(getWidth());
		this.text.setHeight(getHeight());
		this.text.setXpos(getXpos());
		this.text.setYpos(getYpos());
	}
	
	public void setWidth(float width) {
		super.setWidth(width);
		if (getText() != null) {
			getText().setWidth(width);
		}
	}

	public void setHeight(float height) {
		super.setHeight(height);
		if (getText() != null) {
			getText().setHeight(height);
		}
	}
	
	public void setXpos(float newXpos) {
		float currentXpos = getXpos();
		if (getText() != null && currentXpos != newXpos) {
			float delta = newXpos - currentXpos;
			getText().setXpos(delta + getText().getXpos());
		}
		super.setXpos(newXpos);
	}

	public void setYpos(float newYpos) {
		float currentYpos = getYpos();
		if (getText() != null && currentYpos != newYpos) {
			float delta = newYpos - currentYpos;
			getText().setYpos(delta + getText().getYpos());
		}
		super.setYpos(newYpos);
	}
	
  public void adjustSizeToText(float marginX, float marginY) {
    if (getText() != null && getText().getText() != null) {
      Rectangle2D textBounds = TextHelper.getTextBounds(getText(), TextHelper.DEFAULT_FONT_RENDER_CONTEXT);

      float newTextWidth = 0;
      float newTextHeight = 0;
      if (getText().isVertical()) {
        newTextHeight = (float) textBounds.getWidth() + STANDARD_TEXT_START_END_MARGIN;
        newTextWidth = (float) textBounds.getHeight();
      }
      else {
        newTextWidth = (float) textBounds.getWidth() + STANDARD_TEXT_START_END_MARGIN;
        newTextHeight = (float) textBounds.getHeight();
      }

      getText().setWidth(newTextWidth);
      getText().setHeight(newTextHeight);
    }
  }

	@Override
	public void visit(AVisualizationObjectVisitor visitor) {
		visitor.visit(this);
		if (getText() != null) {
			getText().visit(visitor);
		}
		visitor.endPrimitive(this);
	}
}
