package net.etoc.user.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class User implements Serializable {

	private static final long serialVersionUID = -7529892007730101510L;

	/**
	 * 用户id
	 */
	private Integer userId;

	/**
	 * 身份证号
	 */
	private String idCardNo;

	/**
	 * 邮件，要求唯一
	 */
	private String email;

	/**
	 * 用户昵称，要求唯一
	 */
	private String nickName;

	/**
	 * 用户角色id
	 */
	private Integer userRoleId;

	/**
	 * 真实姓名
	 */
	private String realName;

	/**
	 * 密码
	 */
	private String passwd;

	/**
	 * 支付密码
	 */
	private String payPassword;

	/**
	 * 已绑定的手机号码，不要求唯一
	 */
	private String mobile;

	/**
	 * 所在城市
	 */
	private String city;

	/**
	 * 注册时间
	 */
	private Date registerTime;

	/**
	 * 角色，参见com.zkbc.core.consts.user.UserRolesType
	 */
	private Short roles;

	/**
	 * 可用现金余额 这条记录中的值才是用户真正可用的钱
	 */
	private BigDecimal cash;

	/**
	 * 已冻结的提现中现金
	 */
	private BigDecimal frozenWithDrawCash;

	/**
	 * 已冻结的投标中现金
	 */
	private BigDecimal frozenBiddingCash;

	/**
	 * 国政通验证次数，每次验证减1，默认每人最多验证3次
	 */
	private Short idVerifyLimit;

	/**
	 * 头像id
	 */
	private Integer portrait;

	/**
	 * 用户状态。用数值型的好处是今后可以扩充定义，参见com.zkbc.core.consts.user.UserStatusType
	 */
	private Short status;

	/**
	 * 禁止状态。参见com.zkbc.core.consts.user.UserForbidStatusType
	 */
	private Short forbidStatus;

	/**
	 * 个人相册容量，单位：MB。用户所有的userpic加起来不能大于此数值
	 */
	private Short albumCapacity;

	/**
	 * 安全等级
	 */
	private Short securityLevel;

	/**
	 * 新浪微博uid
	 */
	private String weiboUId;

	/**
	 * 新浪微博AccessToken
	 */
	private String weiboAccessToken;

	/**
	 * 腾讯uid
	 */
	private String qqUId;

	/**
	 * 腾讯AccessToken
	 */
	private String qqAccessToken;

	/**
	 * 用户渠道
	 */
	private Short origin;
	/**
	 * 客户经理
	 */
	private Integer staffId;
	/**
	 * 第三方支付账号
	 */
	private String userCode;

	/**
	 * 推荐人
	 */
	private String referee;

	/**
	 * 用户id
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * 用户id
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * 身份证号
	 */

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo == null ? null : idCardNo.trim();
	}

	/**
	 * 邮件，要求唯一
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 邮件，要求唯一
	 */
	public void setEmail(String email) {
		this.email = email == null ? null : email.trim();
	}

	/**
	 * 用户昵称，要求唯一
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * 用户昵称，要求唯一
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName == null ? null : nickName.trim();
	}

	/**
	 * 真实姓名
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * 真实姓名
	 */
	public void setRealName(String realName) {
		this.realName = realName == null ? null : realName.trim();
	}

	/**
	 * 密码
	 */
	public String getPasswd() {
		return passwd;
	}

	/**
	 * 密码
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd == null ? null : passwd.trim();
	}

	/**
	 * 支付密码
	 */
	public String getPayPassword() {
		return payPassword;
	}

	/**
	 * 支付密码
	 */
	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword == null ? null : payPassword.trim();
	}

	/**
	 * 已绑定的手机号码，不要求唯一
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * 已绑定的手机号码，不要求唯一
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
	}

	/**
	 * 所在城市
	 */
	public String getCity() {
		return city;
	}

	/**
	 * 所在城市
	 */
	public void setCity(String city) {
		this.city = city == null ? null : city.trim();
	}

	/**
	 * 注册时间
	 */
	public Date getRegisterTime() {
		return registerTime;
	}

	/**
	 * 注册时间
	 */
	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	/**
	 * 角色，参见com.zkbc.core.consts.user.UserRolesType
	 */
	public Short getRoles() {
		return roles;
	}

	/**
	 * 角色，参见com.zkbc.core.consts.user.UserRolesType
	 */
	public void setRoles(Short roles) {
		this.roles = roles;
	}

	/**
	 * 可用现金余额 这条记录中的值才是用户真正可用的钱
	 */
	public BigDecimal getCash() {
		return cash;
	}

	/**
	 * 可用现金余额 这条记录中的值才是用户真正可用的钱
	 */
	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

	/**
	 * 已冻结的提现中现金
	 */
	public BigDecimal getFrozenWithDrawCash() {
		return frozenWithDrawCash;
	}

	/**
	 * 已冻结的提现中现金
	 */
	public void setFrozenWithDrawCash(BigDecimal frozenWithDrawCash) {
		this.frozenWithDrawCash = frozenWithDrawCash;
	}

	/**
	 * 已冻结的投标中现金
	 */
	public BigDecimal getFrozenBiddingCash() {
		return frozenBiddingCash;
	}

	/**
	 * 已冻结的投标中现金
	 */
	public void setFrozenBiddingCash(BigDecimal frozenBiddingCash) {
		this.frozenBiddingCash = frozenBiddingCash;
	}

	/**
	 * 国政通验证次数，每次验证减1，默认每人最多验证3次
	 */
	public Short getIdVerifyLimit() {
		return idVerifyLimit;
	}

	/**
	 * 国政通验证次数，每次验证减1，默认每人最多验证3次
	 */
	public void setIdVerifyLimit(Short idVerifyLimit) {
		this.idVerifyLimit = idVerifyLimit;
	}

	/**
	 * 头像id
	 */
	public Integer getPortrait() {
		return portrait;
	}

	/**
	 * 头像id
	 */
	public void setPortrait(Integer portrait) {
		this.portrait = portrait;
	}

	/**
	 * 用户状态。用数值型的好处是今后可以扩充定义，参见com.zkbc.core.consts.user.UserStatusType
	 */
	public Short getStatus() {
		return status;
	}

	/**
	 * 用户状态。用数值型的好处是今后可以扩充定义，参见com.zkbc.core.consts.user.UserStatusType
	 */
	public void setStatus(Short status) {
		this.status = status;
	}

	/**
	 * 禁止状态。参见com.zkbc.core.consts.user.UserForbidStatusType
	 */
	public Short getForbidStatus() {
		return forbidStatus;
	}

	/**
	 * 禁止状态。参见com.zkbc.core.consts.user.UserForbidStatusType
	 */
	public void setForbidStatus(Short forbidStatus) {
		this.forbidStatus = forbidStatus;
	}

	/**
	 * 个人相册容量，单位：MB。用户所有的userpic加起来不能大于此数值
	 */
	public Short getAlbumCapacity() {
		return albumCapacity;
	}

	/**
	 * 个人相册容量，单位：MB。用户所有的userpic加起来不能大于此数值
	 */
	public void setAlbumCapacity(Short albumCapacity) {
		this.albumCapacity = albumCapacity;
	}

	/**
	 * 安全等级
	 */
	public Short getSecurityLevel() {
		return securityLevel;
	}

	/**
	 * 安全等级
	 */
	public void setSecurityLevel(Short securityLevel) {
		this.securityLevel = securityLevel;
	}

	/**
	 * 新浪微博uid
	 */
	public String getWeiboUId() {
		return weiboUId;
	}

	/**
	 * 新浪微博uid
	 */
	public void setWeiboUId(String weiboUId) {
		this.weiboUId = weiboUId == null ? null : weiboUId.trim();
	}

	/**
	 * 新浪微博AccessToken
	 */
	public String getWeiboAccessToken() {
		return weiboAccessToken;
	}

	/**
	 * 新浪微博AccessToken
	 */
	public void setWeiboAccessToken(String weiboAccessToken) {
		this.weiboAccessToken = weiboAccessToken == null ? null
				: weiboAccessToken.trim();
	}

	/**
	 * 腾讯uid
	 */
	public String getQqUId() {
		return qqUId;
	}

	/**
	 * 腾讯uid
	 */
	public void setQqUId(String qqUId) {
		this.qqUId = qqUId == null ? null : qqUId.trim();
	}

	/**
	 * 腾讯AccessToken
	 */
	public String getQqAccessToken() {
		return qqAccessToken;
	}

	/**
	 * 腾讯AccessToken
	 */
	public void setQqAccessToken(String qqAccessToken) {
		this.qqAccessToken = qqAccessToken == null ? null : qqAccessToken
				.trim();
	}

	public Short getOrigin() {
		return origin;
	}

	public void setOrigin(Short origin) {
		this.origin = origin;
	}

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	/**
	 * 第三方支付账号
	 */
	public String getUserCode() {
		return userCode;
	}

	/**
	 * 第三方支付账号
	 */
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getReferee() {
		return referee;
	}

	/**
	 * 推荐人
	 * 
	 * @param referee
	 */
	public void setReferee(String referee) {
		this.referee = referee;
	}

	public Integer getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(Integer userRoleId) {
		this.userRoleId = userRoleId;
	}

}
