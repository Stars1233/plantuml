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
package net.sourceforge.plantuml.project.draw;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.project.TimeHeaderParameters;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.MonthYear;
import net.sourceforge.plantuml.project.timescale.TimeScaleCompressed;

public class TimeHeaderMonthly extends TimeHeaderCalendar {

	public double getTimeHeaderHeight() {
		return 16 + 13;
	}

	public double getTimeFooterHeight() {
		return 16 + 13 - 1;
	}

	public TimeHeaderMonthly(TimeHeaderParameters thParam) {
		super(thParam, new TimeScaleCompressed(thParam.getStartingDay(), thParam.getScale()));
	}

	@Override
	public void drawTimeHeader(final UGraphic ug, double totalHeightWithoutFooter) {
		drawTextsBackground(ug, totalHeightWithoutFooter);
		drawYears(ug);
		drawMonths(ug.apply(UTranslate.dy(16)));
		printVerticalSeparators(ug, totalHeightWithoutFooter);
		drawHline(ug, 0);
		drawHline(ug, 16);
		drawHline(ug, getFullHeaderHeight());
	}

	@Override
	public void drawTimeFooter(UGraphic ug) {
		ug = ug.apply(UTranslate.dy(3));
		drawMonths(ug);
		drawYears(ug.apply(UTranslate.dy(13)));
		drawHline(ug, 0);
		drawHline(ug, 13);
		drawHline(ug, getTimeFooterHeight());
	}

	private void drawYears(final UGraphic ug) {
		MonthYear last = null;
		double lastChange = -1;
		for (Day wink = getMin(); wink.compareTo(getMax()) < 0; wink = wink.increment()) {
			final double x1 = getTimeScale().getStartingPosition(wink);
			if (last == null || wink.monthYear().year() != last.year()) {
				drawVline(ug.apply(getLineColor()), x1, 0, 15);
				if (last != null)
					printYear(ug, last, lastChange, x1);

				lastChange = x1;
				last = wink.monthYear();
			}
		}
		final double x1 = getTimeScale().getStartingPosition(getMax().increment());
		if (x1 > lastChange)
			printYear(ug, last, lastChange, x1);

		drawVline(ug.apply(getLineColor()), getTimeScale().getEndingPosition(getMax()), (double) 0, (double) 15);
	}

	private void drawMonths(UGraphic ug) {
		MonthYear last = null;
		double lastChange = -1;
		for (Day wink = getMin(); wink.compareTo(getMax()) < 0; wink = wink.increment()) {
			final double x1 = getTimeScale().getStartingPosition(wink);
			if (wink.monthYear().equals(last) == false) {
				drawVline(ug.apply(getLineColor()), x1, (double) 0, (double) 12);
				if (last != null)
					printMonth(ug, last, lastChange, x1);

				lastChange = x1;
				last = wink.monthYear();
			}
		}
		final double x1 = getTimeScale().getStartingPosition(getMax().increment());
		if (x1 > lastChange)
			printMonth(ug, last, lastChange, x1);

		drawVline(ug.apply(getLineColor()), getTimeScale().getEndingPosition(getMax()), (double) 0, (double) 12);
	}

	private void printYear(UGraphic ug, MonthYear monthYear, double start, double end) {
		final TextBlock small = getTextBlock("" + monthYear.year(), 12, true, openFontColor());
		printCentered(ug, false, start, end, small);
	}

	private void printMonth(UGraphic ug, MonthYear monthYear, double start, double end) {
		final TextBlock small = getTextBlock(monthYear.shortName(locale()), 10, false, openFontColor());
		final TextBlock big = getTextBlock(monthYear.longName(locale()), 10, false, openFontColor());
		printCentered(ug, false, start, end, small, big);
	}

	private void printLeft(UGraphic ug, TextBlock text, double start) {
		text.drawU(ug.apply(UTranslate.dx(start)));
	}

	@Override
	public double getFullHeaderHeight() {
		return getTimeHeaderHeight();
	}

}
