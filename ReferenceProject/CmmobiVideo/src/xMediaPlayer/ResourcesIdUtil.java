package xMediaPlayer;

import android.content.Context;
import android.content.res.Resources;

public final class ResourcesIdUtil {

	private Resources resources;
	private String packageName;

	public ResourcesIdUtil(Context context) {
		resources = context.getResources();
		packageName = context.getPackageName();
	}

	public int getLayoutId(String paramString) {
		return resources.getIdentifier(paramString, "layout", packageName);
	}

	public int getStringId(String paramString) {
		return resources.getIdentifier(paramString, "string", packageName);
	}

	public int getDrawableId(String paramString) {
		return resources.getIdentifier(paramString, "drawable", packageName);
	}

	public int getStyleId(String paramString) {
		return resources.getIdentifier(paramString, "style", packageName);
	}

	public int getIdId(String paramString) {
		return resources.getIdentifier(paramString, "id", packageName);
	}

	public int getColorId(String paramString) {
		return resources.getIdentifier(paramString, "color", packageName);
	}
	
	public int getAnimationId(String paramString) {
		return resources.getIdentifier(paramString, "anim", packageName);
	}

}
