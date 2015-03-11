package net.etoc.core.service.impl;

import java.util.Collections;
import java.util.List;

import net.etoc.wf.shiro.entity.ShiroResource;
import net.etoc.wf.shiro.service.ShiroResourceService;

import org.springframework.stereotype.Component;

@Component
public class ResourceServiceImpl implements ShiroResourceService {

	@Override
	public List<? extends ShiroResource> findAll() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

}
