package cn.zipper.framwork.utils;

import java.util.HashMap;
import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import cn.zipper.framwork.core.ZSystemService;

public final class ZPackageFilter {

	public static enum FilterType {
		OR, AND
	};

	private ZPackageFilter() {
	}
	
	public static HashMap<String, PackageInfo> getCollection(String packageName, String sharedUserId, FilterType type) {
		HashMap<String, PackageInfo> map = new HashMap<String, PackageInfo>();
		
		PackageManager packageManager = ZSystemService.getPackageManager();
		List<PackageInfo> list = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		
		for (PackageInfo packageInfo : list) {
			boolean b1 = packageInfo.packageName != null && packageInfo.packageName.equals(packageName);
			boolean b2 = packageInfo.sharedUserId != null && packageInfo.sharedUserId.equals(sharedUserId);
			boolean b3 = type == FilterType.OR && (b1 || b2);
			boolean b4 = type == FilterType.AND && (b1 && b2);
			
			if (b3 || b4) {
				map.put(packageName, packageInfo);
			}
		}
		
		return map;
	}

}
