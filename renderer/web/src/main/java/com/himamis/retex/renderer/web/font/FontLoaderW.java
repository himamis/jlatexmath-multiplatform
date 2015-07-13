/**
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package com.himamis.retex.renderer.web.font;

import com.himamis.retex.renderer.share.cyrillic.CyrillicRegistration;
import com.himamis.retex.renderer.share.exception.ResourceParseException;
import com.himamis.retex.renderer.share.greek.GreekRegistration;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontLoader;

public class FontLoaderW implements FontLoader {

	private FontLoaderWrapper fontLoaderWrapper;

	public FontLoaderW(FontLoaderWrapper fontLoaderWrapper) {
		this.fontLoaderWrapper = fontLoaderWrapper;
	}

	@Override
	public Font loadFont(Object fontInt, String name)
			throws ResourceParseException {
		String fontName = extractFileName(name);
		String pathName = getPrefix(fontInt) + name;
		AsyncLoadedFont font = fontLoaderWrapper.createNativeFont(pathName,
				fontName, Font.PLAIN, Math.round(PIXELS_PER_POINT));
		return font;
	}

	private String extractFileName(String filePathName) {
		if (filePathName == null)
			return null;

		int dotPos = filePathName.lastIndexOf('.');
		int slashPos = filePathName.lastIndexOf('\\');
		if (slashPos == -1) {
			slashPos = filePathName.lastIndexOf('/');
		}
		slashPos = slashPos > -1 ? slashPos : 0;
		if (dotPos > slashPos) {
			return filePathName.substring(slashPos > 0 ? slashPos + 1 : 0,
					dotPos);
		}

		return filePathName.substring(slashPos > 0 ? slashPos + 1 : 0);
	}

	private String getPrefix(Object fontInt) {
		String prefix = "";
		if (CyrillicRegistration.class.equals(fontInt)) {
			prefix = "cyrillic/";
		} else if (GreekRegistration.class.equals(fontInt)) {
			prefix = "greek/";
		}
		return "font/" + prefix;
	}
}
