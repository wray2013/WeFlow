package net.zkbc.framework.fep.push.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import net.zkbc.framework.fep.push.protocol.MessagingMessage;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsDelegate;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.notnoop.apns.ReconnectPolicy;

public class NotnoopApnsPusher implements Pusher {

	private static final int CONNECT_STRATEGY_POOL = 1;
	private static final int CONNECT_STRATEGY_QUEUE = 2;
	private static final int CONNECT_STRATEGY_BATCHED = 3;

	private static final int RECONNECT_POLICY_NEVER = 1;
	private static final int RECONNECT_POLICY_EVERY_HALF_HOUR = 2;
	private static final int RECONNECT_POLICY_EVERY_NOTIFICATION = 3;

	private int poolSize;
	private int connectStrategy;
	private int reconnectPolicy;
	private String certificatePath;
	private String certificatePassword;
	private boolean isProduction;

	private SSLContext sslContext;

	private String gatewayHost;

	private int gatewayPort;

	private String feedbackHost;

	private int feedbackPort;

	private ApnsDelegate apnsDelegate;

	private ApnsService apnsService;

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int getConnectStrategy() {
		return connectStrategy;
	}

	public void setConnectStrategy(int connectStrategy) {
		this.connectStrategy = connectStrategy;
	}

	public int getReconnectPolicy() {
		return reconnectPolicy;
	}

	public void setReconnectPolicy(int reconnectPolicy) {
		this.reconnectPolicy = reconnectPolicy;
	}

	public String getCertificatePath() {
		return certificatePath;
	}

	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}

	public String getCertificatePassword() {
		return certificatePassword;
	}

	public void setCertificatePassword(String certificatePassword) {
		this.certificatePassword = certificatePassword;
	}

	public boolean isProduction() {
		return isProduction;
	}

	public void setProduction(boolean isProduction) {
		this.isProduction = isProduction;
	}

	public SSLContext getSslContext() {
		return sslContext;
	}

	public void setSslContext(SSLContext sslContext) {
		this.sslContext = sslContext;
	}

	public String getGatewayHost() {
		return gatewayHost;
	}

	public void setGatewayHost(String gatewayHost) {
		this.gatewayHost = gatewayHost;
	}

	public int getGatewayPort() {
		return gatewayPort;
	}

	public void setGatewayPort(int gatewayPort) {
		this.gatewayPort = gatewayPort;
	}

	public String getFeedbackHost() {
		return feedbackHost;
	}

	public void setFeedbackHost(String feedbackHost) {
		this.feedbackHost = feedbackHost;
	}

	public int getFeedbackPort() {
		return feedbackPort;
	}

	public void setFeedbackPort(int feedbackPort) {
		this.feedbackPort = feedbackPort;
	}

	public ApnsDelegate getApnsDelegate() {
		return apnsDelegate;
	}

	public void setApnsDelegate(ApnsDelegate apnsDelegate) {
		this.apnsDelegate = apnsDelegate;
	}

	public ApnsService getApnsService() {
		return apnsService;
	}

	public void setApnsService(ApnsService apnsService) {
		this.apnsService = apnsService;
	}

	@Override
	public void connect(String username, String password,
			int keepAliveInterval, MessagingMessage willMessage,
			boolean useSSL, Properties sslProperties) {

		ApnsServiceBuilder builder = APNS.newService();

		switch (connectStrategy) {
		case CONNECT_STRATEGY_POOL:
			builder = builder.asPool(poolSize);
			break;
		case CONNECT_STRATEGY_QUEUE:
			builder = builder.asQueued();
			break;
		case CONNECT_STRATEGY_BATCHED:
			builder = builder.asBatched();
			break;
		default:
			break;
		}

		switch (reconnectPolicy) {
		case RECONNECT_POLICY_NEVER:
			builder = builder
					.withReconnectPolicy(ReconnectPolicy.Provided.NEVER);
			break;
		case RECONNECT_POLICY_EVERY_HALF_HOUR:
			builder = builder
					.withReconnectPolicy(ReconnectPolicy.Provided.EVERY_HALF_HOUR);
			break;
		case RECONNECT_POLICY_EVERY_NOTIFICATION:
			builder = builder
					.withReconnectPolicy(ReconnectPolicy.Provided.EVERY_NOTIFICATION);
			break;
		default:
			break;
		}

		if (apnsDelegate != null) {
			builder = builder.withDelegate(apnsDelegate);
		}

		if (!StringUtils.isEmpty(gatewayHost)) {
			builder = builder.withGatewayDestination(gatewayHost, gatewayPort);
		}

		if (!StringUtils.isEmpty(feedbackHost)) {
			builder = builder.withFeedbackDestination(feedbackHost,
					feedbackPort);
		}

		if (sslContext != null) {
			builder = builder.withSSLContext(sslContext);
		} else {
			try {
				InputStream in = new PathMatchingResourcePatternResolver()
						.getResource(certificatePath).getInputStream();
				try {
					builder = builder.withCert(in, certificatePassword);
					if (isProduction) {
						builder = builder.withProductionDestination();
					} else {
						builder = builder.withSandboxDestination();
					}
				} finally {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		apnsService = builder.build();
	}

	@Override
	public void disconnect() {
	}

	@Override
	public void push(MessagingMessage msg) {
		List<String> deviceTokens = new ArrayList<String>();
		for (String deviceToken : msg.getDestinationName().split(",")) {
			int idx = deviceToken.lastIndexOf("/");
			if (idx != -1) {
				deviceToken = deviceToken.substring(idx + 1);
			}
			deviceTokens.add(deviceToken);
		}

		try {
			apnsService.push(deviceTokens,
					APNS.newPayload().alertBody(msg.getMessage()).build());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	@Override
	public boolean support(MessagingMessage msg) {
		String destinationType = msg.getDestinationType();

		return "ipad".equalsIgnoreCase(destinationType)
				|| "iphone".equalsIgnoreCase(destinationType);
	}
}
