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
package net.sourceforge.plantuml.objectdiagram.command;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.baraye.IEntity;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.BodierJSon;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.json.Json.DefaultHandler;
import net.sourceforge.plantuml.json.JsonParser;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.objectdiagram.AbstractClassOrObjectDiagram;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandCreateJsonSingleLine extends SingleLineCommand2<AbstractClassOrObjectDiagram> {

	public CommandCreateJsonSingleLine() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandCreateJsonSingleLine.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "json"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("NAME", "(?:[%g]([^%g]+)[%g][%s]+as[%s]+)?([%pLN_.]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<\\<.+\\>\\>)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				UrlBuilder.OPTIONAL, //
				RegexLeaf.spaceZeroOrMore(), //
				ColorParser.exp1(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("DATA", "(\\{.*\\})"), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(AbstractClassOrObjectDiagram diagram, LineLocation location,
			RegexResult arg) throws NoSuchColorException {
		final String name = arg.get("NAME", 1);
		final String data = arg.get("DATA", 0);
		final IEntity entity1 = executeArg0(diagram, arg);
		if (entity1 == null)
			return CommandExecutionResult.error("No such entity");

		final JsonValue json = getJsonValue(data);

		if (json == null)
			return CommandExecutionResult.error("Bad data");
		((BodierJSon) entity1.getBodier()).setJson(json);

		return CommandExecutionResult.ok();
	}

	private JsonValue getJsonValue(String data) {
		try {
			final DefaultHandler handler = new DefaultHandler();
			new JsonParser(handler).parse(data);
			final JsonValue json = handler.getValue();
			return json;
		} catch (Exception e) {
			return null;
		}
	}

	private IEntity executeArg0(AbstractClassOrObjectDiagram diagram, RegexResult line0) throws NoSuchColorException {
		final String name = line0.get("NAME", 1);
		final Ident ident = diagram.buildLeafIdent(name);
		final Code code = diagram.V1972() ? ident : diagram.buildCode(name);
		final String display = line0.get("NAME", 0);
		final String stereotype = line0.get("STEREO", 0);
		final boolean leafExist = diagram.V1972() ? diagram.leafExistSmart(ident) : diagram.leafExist(code);
		if (leafExist)
			return diagram.getOrCreateLeaf(diagram.buildLeafIdent(name), code, LeafType.JSON, null);

		final IEntity entity = diagram.createLeaf(ident, code, Display.getWithNewlines(display), LeafType.JSON, null);
		if (stereotype != null)
			entity.setStereotype(Stereotype.build(stereotype, diagram.getSkinParam().getCircledCharacterRadius(),
					diagram.getSkinParam().getFont(null, false, FontParam.CIRCLED_CHARACTER),
					diagram.getSkinParam().getIHtmlColorSet()));

		final String s = line0.get("COLOR", 0);
		entity.setSpecificColorTOBEREMOVED(ColorType.BACK, s == null ? null
				: diagram.getSkinParam().getIHtmlColorSet().getColor(s));
		return entity;
	}

}
