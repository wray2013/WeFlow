package net.etoc.user.service;

import net.etoc.user.entity.User;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
	public User getUserByNickname(String nickName);
}
