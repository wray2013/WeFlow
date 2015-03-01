package net.etoc.wf.shiro;

public interface PathFilter {

	/**
	 * 判断是否有权访问指定路径
	 * 
	 * @param path
	 * @return
	 */
	public boolean isAccessAllowed(String path);

}