package com.cmmobi.looklook.common.listener;


/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.common.listener
 * @filename DiaryPagerTouchInterface.java
 * @summary 详情页ViewPager的Touch事件，用于处理手势
 * @author Lanhai
 * @date 2013-9-27
 * @version 1.0
 */
public interface DiaryPagerTouchInterface
{
	public boolean isIntercept();

	public void setIntercept(boolean intercept);
	
	public boolean isForbidMove();
	
	public void setForbidMovable(boolean movable);
	
}
