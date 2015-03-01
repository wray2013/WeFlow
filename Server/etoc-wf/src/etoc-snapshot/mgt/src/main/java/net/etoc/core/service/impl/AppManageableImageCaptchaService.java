package net.etoc.core.service.impl;

import net.etoc.wf.shiro.service.ShiroCaptchaService;

import org.springframework.stereotype.Component;

import com.octo.captcha.Captcha;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

@Component
public class AppManageableImageCaptchaService extends
		DefaultManageableImageCaptchaService implements ShiroCaptchaService {

	public AppManageableImageCaptchaService() {
		super(new FastHashMapCaptchaStore(), new AppCaptchaEngine(), 180,
				100000, 75000);
	}

	@Override
	public Boolean validateResponseForID(String ID, Object response,
			boolean last) {
		if (last) {
			return super.validateResponseForID(ID, response);
		}

		Captcha captcha = store.getCaptcha(ID);
		boolean valid = super.validateResponseForID(ID, response);
		store.storeCaptcha(ID, captcha, store.getLocale(ID));

		return valid;
	}

}
