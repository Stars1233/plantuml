/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 * 
 *
 */
package net.sourceforge.plantuml.decoration.symbol;

import net.sourceforge.plantuml.klimt.Fashion;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.AbstractUGraphicHorizontalLine;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.klimt.shape.UEmpty;
import net.sourceforge.plantuml.klimt.shape.UHorizontalLine;
import net.sourceforge.plantuml.klimt.shape.ULine;
import net.sourceforge.plantuml.klimt.shape.UPolygon;
import net.sourceforge.plantuml.style.SName;

class USymbolNode extends USymbol {

	@Override
	public SName[] getSNames() {
		return new SName[] { SName.node };
	}

//	public USymbolNode(HorizontalAlignment stereotypeAlignement) {
//		this.stereotypeAlignement = stereotypeAlignement;
//	}
//
//	@Override
//	public USymbol withStereoAlignment(HorizontalAlignment alignment) {
//		return new USymbolNode(alignment);
//	}

	private void drawNode(UGraphic ug, double width, double height, double shadowing) {
		final UPolygon shape = new UPolygon();
		shape.addPoint(0, 10);
		shape.addPoint(10, 0);
		shape.addPoint(width, 0);
		shape.addPoint(width, height - 10);
		shape.addPoint(width - 10, height);
		shape.addPoint(0, height);
		shape.addPoint(0, 10);

		shape.setDeltaShadow(shadowing);

		ug.draw(shape);

		ug.apply(new UTranslate(width - 10, 10)).draw(new ULine(10, -10));

		ug.apply(UTranslate.dy(10)).draw(ULine.hline(width - 10));
		ug.apply(new UTranslate(width - 10, 10)).draw(ULine.vline(height - 10));

		ug.apply(new UTranslate(0, height)).draw(new UEmpty(10, 10));

	}

	class MyUGraphicNode extends AbstractUGraphicHorizontalLine {

		private final double endingX;

		@Override
		protected AbstractUGraphicHorizontalLine copy(UGraphic ug) {
			return new MyUGraphicNode(ug, endingX);
		}

		public MyUGraphicNode(UGraphic ug, double endingX) {
			super(ug);
			this.endingX = endingX;
		}

		@Override
		protected void drawHline(UGraphic ug, UHorizontalLine line, UTranslate translate) {
			ug = ug.apply(translate);

			drawHlineInternal(ug, line);
			if (line.isDouble()) {
				drawHlineInternal(ug.apply(UTranslate.dy(2)), line);
			}
			line.drawTitleInternal(ug, 0, endingX - 10, 0, true);
		}

		private void drawHlineInternal(UGraphic ug, UHorizontalLine line) {
			ug = ug.apply(line.getStroke()).apply(HColors.none().bg());
			ug.draw(ULine.hline(endingX - 10));
			ug.apply(UTranslate.dx(endingX - 10)).draw(new ULine(10, -10));
		}
	}

	private Margin getMargin() {
		return new Margin(10 + 5, 20 + 5, 15 + 5, 5 + 5);
	}

	@Override
	public TextBlock asSmall(TextBlock name, final TextBlock label, final TextBlock stereotype,
			final Fashion symbolContext, final HorizontalAlignment stereoAlignment) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final XDimension2D dim = calculateDimension(ug.getStringBounder());
				ug = symbolContext.apply(ug);
				drawNode(ug, dim.getWidth(), dim.getHeight(), symbolContext.getDeltaShadow());
				final Margin margin = getMargin();
				final TextBlock tb = TextBlockUtils.mergeTB(stereotype, label, stereoAlignment);
				final UGraphic ug2 = new MyUGraphicNode(ug, dim.getWidth());
				tb.drawU(ug2.apply(new UTranslate(margin.getX1(), margin.getY1())));
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				final XDimension2D dimLabel = label.calculateDimension(stringBounder);
				final XDimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				return getMargin().addDimension(dimStereo.mergeTB(dimLabel));
			}
		};
	}

	@Override
	public TextBlock asBig(final TextBlock title, HorizontalAlignment labelAlignment, final TextBlock stereotype,
			final double width, final double height, final Fashion symbolContext,
			final HorizontalAlignment stereoAlignment) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final XDimension2D dim = calculateDimension(ug.getStringBounder());
				ug = symbolContext.apply(ug);
				drawNode(ug, dim.getWidth(), dim.getHeight(), symbolContext.getDeltaShadow());
				ug = ug.apply(new UTranslate(-4, 11));

				final XDimension2D dimStereo = stereotype.calculateDimension(ug.getStringBounder());
				final double posStereoX;
				final double posStereoY = 2;
				if (stereoAlignment == HorizontalAlignment.RIGHT) {
					posStereoX = width - dimStereo.getWidth() - getMargin().getX1();
				} else {
					posStereoX = (width - dimStereo.getWidth()) / 2;

				}
				stereotype.drawU(ug.apply(new UTranslate(posStereoX, posStereoY)));
				final XDimension2D dimTitle = title.calculateDimension(ug.getStringBounder());
				final double posTitle = (width - dimTitle.getWidth()) / 2;
				title.drawU(ug.apply(new UTranslate(posTitle, 2 + dimStereo.getHeight())));

			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				return new XDimension2D(width, height);
			}
		};
	}

	@Override
	public int suppHeightBecauseOfShape() {
		return 5;
	}

	@Override
	public int suppWidthBecauseOfShape() {
		return 60;
	}

}