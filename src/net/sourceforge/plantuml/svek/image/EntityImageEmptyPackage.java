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
package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.AlignmentParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.Guillemet;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.activitydiagram3.ftile.EntityImageLegend;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.baraye.EntityImp;
import net.sourceforge.plantuml.baraye.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplayPositioned;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ClusterDecoration;
import net.sourceforge.plantuml.svek.ClusterPosition;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class EntityImageEmptyPackage extends AbstractEntityImage {

	private final TextBlock desc;
	private final static int MARGIN = 10;

	private final Stereotype stereotype;
	private final TextBlock stereoBlock;
	private final Url url;
	private final SName sname;
	private final double shadowing;
	private final HColor borderColor;
	private final UStroke stroke;
	private final double roundCorner;
	private final double diagonalCorner;
	private final HColor back;

	private Style getStyle() {
		return StyleSignatureBasic.of(SName.root, SName.element, sname, SName.package_, SName.title)
				.withTOBECHANGED(stereotype).getMergedStyle(getSkinParam().getCurrentStyleBuilder());
	}

	public EntityImageEmptyPackage(ILeaf entity, ISkinParam skinParam, PortionShower portionShower, SName sname) {
		super(entity, skinParam);
		this.sname = sname;

		final Colors colors = entity.getColors();
		final HColor specificBackColor = colors.getColor(ColorType.BACK);
		this.stereotype = entity.getStereotype();
		this.url = entity.getUrl99();

		Style style = getStyle();
		style = style.eventuallyOverride(colors);
		this.borderColor = style.value(PName.LineColor).asColor(getSkinParam().getIHtmlColorSet());
		this.shadowing = style.value(PName.Shadowing).asDouble();
		this.stroke = style.getStroke(colors);
		this.roundCorner = style.value(PName.RoundCorner).asDouble();
		this.diagonalCorner = style.value(PName.DiagonalCorner).asDouble();
		if (specificBackColor == null)
			this.back = style.value(PName.BackGroundColor).asColor(getSkinParam().getIHtmlColorSet());
		else
			this.back = specificBackColor;

		final FontConfiguration titleFontConfiguration = style.getFontConfiguration(getSkinParam().getIHtmlColorSet());
		final HorizontalAlignment titleHorizontalAlignment = style.getHorizontalAlignment();

		this.desc = entity.getDisplay().create(titleFontConfiguration, titleHorizontalAlignment, skinParam);

		final DisplayPositioned legend = ((EntityImp) entity).getLegend();
		if (legend != null) {
			final TextBlock legendBlock = EntityImageLegend.create(legend.getDisplay(), skinParam);
			stereoBlock = legendBlock;
		} else {
			if (stereotype == null || stereotype.getLabel(Guillemet.DOUBLE_COMPARATOR) == null
					|| portionShower.showPortion(EntityPortion.STEREOTYPE, entity) == false)
				stereoBlock = TextBlockUtils.empty(0, 0);
			else
				stereoBlock = TextBlockUtils.withMargin(Display.create(stereotype.getLabels(skinParam.guillemet()))
						.create(FontConfiguration.create(getSkinParam(), FontParam.PACKAGE_STEREOTYPE, stereotype),
								titleHorizontalAlignment, skinParam),
						1, 0);
		}

	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final XDimension2D dimDesc = desc.calculateDimension(stringBounder);
		XDimension2D dim = TextBlockUtils.mergeTB(desc, stereoBlock, HorizontalAlignment.LEFT)
				.calculateDimension(stringBounder);
		dim = dim.atLeast(0, 2 * dimDesc.getHeight());
		return dim.delta(MARGIN * 2, MARGIN * 2);
	}

	final public void drawU(UGraphic ug) {
		if (url != null)
			ug.startUrl(url);

		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dimTotal = calculateDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();

		final ClusterPosition clusterPosition = new ClusterPosition(0, 0, widthTotal, heightTotal);
		final ClusterDecoration decoration = new ClusterDecoration(getSkinParam().packageStyle(), null, desc,
				stereoBlock, clusterPosition, stroke);

		final HorizontalAlignment horizontalAlignment = getSkinParam()
				.getHorizontalAlignment(AlignmentParam.packageTitleAlignment, null, false, null);
		final HorizontalAlignment stereotypeAlignment = getSkinParam().getStereotypeAlignment();

		decoration.drawU(ug, back, borderColor, shadowing, roundCorner, horizontalAlignment, stereotypeAlignment,
				diagonalCorner);

		if (url != null)
			ug.closeUrl();

	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

}
