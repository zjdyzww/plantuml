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
package net.sourceforge.plantuml.baraye;

import java.util.Collection;

import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.dot.Neighborhood;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.svek.Margins;

public interface ILeaf extends IEntity {

	public void setContainer(IGroup container);

	public Margins getMargins();

	public int getXposition();

	public void setXposition(int pos);

	public IEntityImage getSvekImage();

	public String getGeneric();

	public boolean muteToType(LeafType newType, USymbol newSymbol);

	public void setGeneric(String generic);

	public void setSvekImage(IEntityImage svekImage);

	public void setNeighborhood(Neighborhood neighborhood);

	public Neighborhood getNeighborhood();

	public Collection<String> getPortShortNames();

	public void addPortShortName(String portShortName);

	public void setVisibilityModifier(VisibilityModifier visibility);

	public VisibilityModifier getVisibilityModifier();

	public void setStatic(boolean isStatic);

	public boolean isStatic();

}
