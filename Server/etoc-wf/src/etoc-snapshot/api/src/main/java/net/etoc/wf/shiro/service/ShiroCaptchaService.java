package net.etoc.wf.shiro.service;

public interface ShiroCaptchaService {

	public Boolean validateResponseForID(String ID, Object response,
			boolean last);

}
