package cn.zipper.framwork.utils;

import android.content.res.Resources;
import cn.zipper.framwork.core.ZApplication;

public final class ZResourcesIdUtil {

	private static Resources resources = ZApplication.getInstance().getResources();
	private static String packageName = ZApplication.getInstance().getPackageName();

	private ZResourcesIdUtil() {
	}

	public static int getLayoutId(String paramString) {
		return resources.getIdentifier(paramString, "layout", packageName);
	}

	public static int getStringId(String paramString) {
		return resources.getIdentifier(paramString, "string", packageName);
	}

	public static int getDrawableId(String paramString) {
		return resources.getIdentifier(paramString, "drawable", packageName);
	}

	public static int getStyleId(String paramString) {
		return resources.getIdentifier(paramString, "style", packageName);
	}

	public static int getIdId(String paramString) {
		return resources.getIdentifier(paramString, "id", packageName);
	}

	public static int getColorId(String paramString) {
		return resources.getIdentifier(paramString, "color", packageName);
	}

}