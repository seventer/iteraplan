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
package de.iteratec.visualizationmodel.renderer.impl.svg;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import de.iteratec.visualizationmodel.ASymbol;
import de.iteratec.visualizationmodel.CompositeSymbol;
import de.iteratec.visualizationmodel.EllipseArc;
import de.iteratec.visualizationmodel.Point;
import de.iteratec.visualizationmodel.Polygon;
import de.iteratec.visualizationmodel.Polyline;
import de.iteratec.visualizationmodel.Text;
import de.iteratec.visualizationmodel.renderer.AVisualizationObjectVisitor;



/**
 * Transforming visitor for computing the transformed coordinates and sizes for the visualization.
 * 
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public class TransformingVisitor extends AVisualizationObjectVisitor {
	
	public static final String ELLIPSIS = "...";

	private FontRenderContext ctx;
	private int xMin;
	private int yMin;
	
	/**
	 * Default constructor.
	 */
	public TransformingVisitor() {
		this.ctx = new FontRenderContext(new AffineTransform(),false,false);
	}
	
	public void setXMin(int xMin) {
		this.xMin = xMin;
	}
	
	public void setYMin(int yMin) {
		this.yMin = yMin;
	}

	@Override
	public void visit(ASymbol primi) {
		primi.setXpos(primi.getXpos() - primi.getWidth() / 2 - this.xMin);
		primi.setYpos(primi.getYpos() - primi.getHeight() / 2 - this.yMin);
	}
	
	@Override
	public void visit(CompositeSymbol composite) {
		// Don't move Composites as their Position and Dimensions are calculated 
		// dynamically by the contained Primitives.
	}
	
	@Override
	public void visit(Polygon poly) {
		for (Point p : poly.getPoints()) {
			p.setX(p.getX() - this.xMin);
			p.setY(p.getY() - this.yMin);
		}
	}
	
	@Override
	public void visit(Polyline poly) {
		for (Point p : poly.getPoints()) {
			p.setX(p.getX() - this.xMin);
			p.setY(p.getY() - this.yMin);
		}
	}

	@Override
	public void visit(Text t) {
		// TODO Fix this: We reset the coordinates here without knowing whether they 
		// have been changed. See below for explanation:
		// 
		// The center coordinates have been updated to top left based coordinates 
		// if this Text is contained in a ALabeledVisualizationObject. This happens 
		// by calling TransformingVisitor.visit(AVisualizationObject) which updates 
		// the x- and y-Position of the containing Objects and thus (via the 
		// ALabeledVisualizationObject.set(X|Y)pos()) the Position of this Text.
		if (t != null && t.getText() != null) {
			cutText(t, this.ctx, ELLIPSIS);
			
			float xPos = t.getXpos();
			// Reset xPos to center based coordinates.
			xPos += t.getWidth() / 2 + this.xMin;
			switch(t.getHorizontalAlignment()) {
				case LEFT: xPos -= t.getWidth() / 2; break;
				case RIGHT: xPos += t.getWidth() / 2; break;
				case CENTER: // CENTER: xPos stays untouched
				default: //Should not occur
			}
			t.setXpos(xPos - this.xMin);
			
			float yPos = t.getYpos();
			// Reset yPos to center based coordinates.
			yPos += t.getHeight() / 2 + this.yMin;
			switch(t.getVerticalAlignment()) {
				case TOP: yPos -= (t.getHeight() / 2 - t.getTextSize()); break;
				case MIDDLE: yPos += (float) Math.floor(t.getTextSize() / 2f); break;
				case BOTTOM: yPos += t.getHeight() / 2; break;
				default: //Should not occur
			}
			t.setYpos(yPos - this.yMin);
		}
	}
	
	@Override
	public void visit(EllipseArc ellarc) {
		// Ellipse arc is serialized and transformed in the SvgSerializingVisitor.
	}
	
	/**
	 * Helper method for determining the font for a given text element.
	 * 
	 * @param t The text to be processed.
	 * 
	 * @return The corresponding font.
	 */
	public static final Font getFont(Text t) {
		Font f = Font.decode(t.getFontName());
		int result = 0;
		if (t.getTextStyle() != null) {
			result |= (t.getTextStyle().length > 0 && t.getTextStyle()[0]) ? Font.BOLD : 0;
			result |= (t.getTextStyle().length > 1 && t.getTextStyle()[1]) ? Font.ITALIC : 0;
		}
		f = f.deriveFont(result, t.getTextSize());
		return f;
	}
	
	/**
	 * Helper method for cutting to long text elements using the specified ellipsis text.
	 * 
	 * @param text The text to be cut.
	 * @param ctx The rendering context to determine the actual text length.
	 * @param ellipsis The text used to replace the omission.
	 */
	public static void cutText(Text text, FontRenderContext ctx, String ellipsis) {
		Font f = getFont(text);
		Rectangle2D sbounds = f.getStringBounds(text.getText(), ctx);
		if (sbounds.getWidth() > text.getWidth()) {
			// TODO This calculation should be improved
			int newCharcount = (int)(Math.floor(text.getText().length() * text.getWidth() / sbounds.getWidth()));
			if( newCharcount > 3 ) {
				newCharcount -= ellipsis.length();
			}
			text.setText(text.getText().substring(0,newCharcount) + ellipsis);
		}
	}
}