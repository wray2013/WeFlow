package com.cmmobi.looklook.common.utils;

import android.content.Intent;

/**
 * <ul>
 * <li><b>name : </b>		IntentUtil		</li>
 * <li><b>description :</b>	创建一些常用Intent的工具				</li>
 * <li></b>author : </b>	桥下一粒砂			</li>
 * <li><b>e-mail : </b>		chenyoca@gmail.com	</li>
 * <li><b>weibo : </b>		@桥下一粒砂			</li>
 * <li><b>date : </b>		2012-8-10 下午9:54:03		</li>
 * </ul>
 */
public class IntentUtil {

	/**
	 * </br><b>description :</b>将值设置到Intent里
	 * </br><b>time :</b>		2012-7-8 下午3:31:17
	 * @param intent			Inent对象
	 * @param key				Key
	 * @param val				Value
	 */
	public static void setValueToIntent(Intent intent, String key, Object val) {
	    if( null == key || null == val) return;
		if (val instanceof Boolean)
			intent.putExtra(key, (Boolean) val);
		else if (val instanceof Boolean[])
			intent.putExtra(key, (Boolean[]) val);
		else if (val instanceof String)
			intent.putExtra(key, (String) val);
		else if (val instanceof String[])
			intent.putExtra(key, (String[]) val);
		else if (val instanceof Integer)
			intent.putExtra(key, (Integer) val);
		else if (val instanceof Integer[])
			intent.putExtra(key, (Integer[]) val);
		else if (val instanceof Long)
			intent.putExtra(key, (Long) val);
		else if (val instanceof Long[])
			intent.putExtra(key, (Long[]) val);
		else if (val instanceof Double)
			intent.putExtra(key, (Double) val);
		else if (val instanceof Double[])
			intent.putExtra(key, (Double[]) val);
		else if (val instanceof Float)
			intent.putExtra(key, (Float) val);
		else if (val instanceof Float[])
			intent.putExtra(key, (Float[]) val);
		else{
			throw new IllegalArgumentException("Not support data Type!");
		}
	}
	
}
