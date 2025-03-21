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
 */
package net.sourceforge.plantuml.tim;

import net.sourceforge.plantuml.preproc.EvalBoolean;
import net.sourceforge.plantuml.preproc.Truth;
import net.sourceforge.plantuml.tim.expression.TValue;
import net.sourceforge.plantuml.utils.StringLocated;

public class EaterIfdef extends Eater {

	private String expression;

	public EaterIfdef(StringLocated s) {
		super(s);
	}

	@Override
	public void analyze(TContext context, TMemory memory) throws EaterException {
		skipSpaces();
		checkAndEatChar("!ifdef");
		skipSpaces();
		expression = eatAllToEnd();
	}

	public boolean isTrue(final TContext context, final TMemory memory) {
		final EvalBoolean eval = new EvalBoolean(expression, new Truth() {

			public boolean isTrue(String varname) {
				final TValue currentValue = memory.getVariable(varname);
				return currentValue != null || context.doesFunctionExist(varname);
			}
		});

		return eval.eval();
	}

}
