package org.jlatexmath.android.font;

import org.scilab.forge.jlatexmath.exception.ResourceParseException;
import org.scilab.forge.jlatexmath.platform.font.Font;
import org.scilab.forge.jlatexmath.platform.font.FontLoader;

import android.content.res.AssetManager;
import android.graphics.Typeface;

public class FontLoaderA implements FontLoader {

	private AssetManager mAssetManager;

	public FontLoaderA(AssetManager assetManager) {
		mAssetManager = assetManager;
	}

	public Font loadFont(String name) throws ResourceParseException {
		Typeface typeface = Typeface.createFromAsset(mAssetManager, name);
		return new FontA(typeface, Math.round(PIXELS_PER_POINT));
	}

}
