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
package net.sourceforge.plantuml.statediagram.command;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlMode;
import net.sourceforge.plantuml.baraye.IEntity;
import net.sourceforge.plantuml.baraye.IGroup;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.cucadiagram.NamespaceStrategy;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.USymbols;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.statediagram.StateDiagram;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandCreatePackage2 extends SingleLineCommand2<StateDiagram> {

	public CommandCreatePackage2() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandCreatePackage2.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("frame"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexOr(//
						new RegexConcat(//
								new RegexLeaf("CODE1", "([%pLN_.]+)"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("as"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("DISPLAY1", "[%g]([^%g]+)[%g]")), //
						new RegexConcat(//
								new RegexOptional(new RegexConcat( //
										new RegexLeaf("DISPLAY2", "[%g]([^%g]+)[%g]"), RegexLeaf.spaceOneOrMore(), //
										new RegexLeaf("as"), RegexLeaf.spaceOneOrMore() //
								)), //
								new RegexLeaf("CODE2", "([%pLN_.]+)"))), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREOTYPE", "(\\<\\<.*\\>\\>)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				UrlBuilder.OPTIONAL, //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexLeaf("LINECOLOR", "##(?:\\[(dotted|dashed|bold)\\])?(\\w+)?")),
				new RegexLeaf("(?:[%s]*\\{|[%s]+begin)"), RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	private String getNotNull(RegexResult arg, String v1, String v2) {
		if (arg.get(v1, 0) == null)
			return arg.get(v2, 0);

		return arg.get(v1, 0);
	}

	@Override
	protected CommandExecutionResult executeArg(StateDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final IGroup currentPackage = diagram.getCurrentGroup();
		final String idShort = getNotNull(arg, "CODE1", "CODE2");
		final Ident idNewLong = diagram.buildLeafIdentSpecial(idShort);
		final Code code = diagram.V1972() ? idNewLong : diagram.buildCode(idShort);
		String display = getNotNull(arg, "DISPLAY1", "DISPLAY2");
		if (display == null)
			display = code.getName();

		diagram.gotoGroup(idNewLong, code, Display.getWithNewlines(display), GroupType.PACKAGE, currentPackage,
				NamespaceStrategy.SINGLE);
		final IEntity p = diagram.getCurrentGroup();
		final String stereotype = arg.get("STEREOTYPE", 0);
		if (stereotype != null)
			p.setStereotype(Stereotype.build(stereotype));
		p.setUSymbol(USymbols.FRAME);

		final String urlString = arg.get("URL", 0);
		if (urlString != null) {
			final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), UrlMode.STRICT);
			final Url url = urlBuilder.getUrl(urlString);
			p.addUrl(url);
		}

		Colors colors = color().getColor(arg, diagram.getSkinParam().getIHtmlColorSet());
		final String s = arg.get("LINECOLOR", 1);

		final HColor lineColor = s == null ? null
				: diagram.getSkinParam().getIHtmlColorSet().getColor(s);
		if (lineColor != null)
			colors = colors.add(ColorType.LINE, lineColor);

		if (arg.get("LINECOLOR", 0) != null)
			colors = colors.addLegacyStroke(arg.get("LINECOLOR", 0));

		p.setColors(colors);
		return CommandExecutionResult.ok();
	}

}
