/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.ebnf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FloatingNote;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.utils.BlocLines;
import net.sourceforge.plantuml.utils.CharInspector;

public class PSystemEbnf extends TitledDiagram {

	private final List<TextBlockable> expressions = new ArrayList<>();

	public PSystemEbnf(UmlSource source) {
		super(source, UmlDiagramType.EBNF, null);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(EBNF)");
	}

	public CommandExecutionResult addBlocLines(BlocLines blines, String commentAbove, String commentBelow) {
		final boolean isCompact = getPragma().isDefine("compact");
		final CharInspector it = blines.inspector();
		final EbnfExpression tmp1 = EbnfExpression.create(it, isCompact, commentAbove, commentBelow);
		if (tmp1.isEmpty())
			return CommandExecutionResult.error("Unparsable expression");
		expressions.add(tmp1);
		return CommandExecutionResult.ok();

	}

	public CommandExecutionResult addNote(final Display note, Colors colors) {
		expressions.add(new TextBlockable() {
			@Override
			public TextBlock getUDrawable(ISkinParam skinParam) {
				final FloatingNote f = FloatingNote.create(note, skinParam, SName.ebnf);
				return TextBlockUtils.withMargin(f, 0, 0, 5, 15);
			}
		});
		return CommandExecutionResult.ok();
	}

	@Override
	protected ImageData exportDiagramNow(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		return createImageBuilder(fileFormatOption).drawable(getTextBlock()).write(os);
	}

	private TextBlockBackcolored getTextBlock() {
		if (expressions.size() == 0) {
			final Style style = ETile.getStyleSignature().getMergedStyle(getSkinParam().getCurrentStyleBuilder());
			final FontConfiguration fc = style.getFontConfiguration(getSkinParam().getIHtmlColorSet());

			final TextBlock tmp = EbnfEngine.syntaxError(fc, getSkinParam());
			return TextBlockUtils.addBackcolor(tmp, null);
		}

		TextBlock result = expressions.get(0).getUDrawable(getSkinParam());
		for (int i = 1; i < expressions.size(); i++)
			result = TextBlockUtils.mergeTB(result, expressions.get(i).getUDrawable(getSkinParam()),
					HorizontalAlignment.LEFT);
		return TextBlockUtils.addBackcolor(result, null);
	}

}
