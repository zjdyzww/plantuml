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
package net.sourceforge.plantuml.graphml;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.cucadiagram.ICucaDiagram;
import net.sourceforge.plantuml.descdiagram.DescriptionDiagram;
import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.utils.Log;
import net.sourceforge.plantuml.xmi.XmlDiagramTransformer;

public final class CucaDiagramGraphmlMaker {

	private final ICucaDiagram diagram;

	public CucaDiagramGraphmlMaker(ICucaDiagram diagram) throws IOException {
		this.diagram = diagram;
	}

	public static String getModel(UmlDiagram classDiagram) {
		return "model1";
	}

	public void createFiles(OutputStream fos) throws IOException {
		try {
			final XmlDiagramTransformer xmi;
			if (diagram instanceof DescriptionDiagram)
				xmi = new GraphmlDescriptionDiagram((DescriptionDiagram) diagram);
			else
				throw new UnsupportedOperationException();

			xmi.transformerXml(fos);
		} catch (ParserConfigurationException e) {
			Log.error(e.toString());
			Logme.error(e);
			throw new IOException(e.toString());
		} catch (TransformerException e) {
			Log.error(e.toString());
			Logme.error(e);
			throw new IOException(e.toString());
		}
	}

}
