package cn.zipper.framwork.security;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

public class ZSignature {
	public static String getSingInfo(Context c) {
		String ret = null;
		try {
			PackageInfo packageInfo = c.getPackageManager().getPackageInfo(
					c.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature[] signs = packageInfo.signatures;
			Signature sign = signs[0];
			ret = new String(parseSignature(sign.toByteArray()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;

	}

	public static byte[] parseSignature(byte[] signature) {
		byte[] buffer = null;
		try {
			CertificateFactory certFactory = CertificateFactory
					.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            buffer = cert.getEncoded();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return buffer;
	}
}
