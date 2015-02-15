package com.cmmobi.looklook.info.weather;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;


public class AddressCodeParser {

	public AddressCodeParser() {

	}

	// 遍历查找是否有该城市信息，有则返回国际码，无则返回null
	public String parse(Context mContext, String fileName, String c, String d)
			throws Exception {
		String addresscode = null;
        DocumentBuilderFactory factory=null;
        DocumentBuilder builder=null;
        Document document=null;
        InputStream inputStream=null;
        if(c == null || d == null) {
        	return null;
        }
        //首先找到xml文件
        factory=DocumentBuilderFactory.newInstance();
		try {
            //找到xml，并加载文档
            builder=factory.newDocumentBuilder();
            inputStream=mContext.getResources().getAssets().open(fileName);
            document=builder.parse(inputStream);

            //找到根Element
            Element root = (Element) document.getDocumentElement();
            NodeList nodes=root.getElementsByTagName("City");
            for(int i=0;i<nodes.getLength();i++){
            	Element cityElement=(Element)(nodes.item(i));
            	if(c.equals(cityElement.getAttribute("name"))) {//城市名匹配
            		NodeList disnodes = cityElement.getElementsByTagName("district");
            		for(int j=0;j<disnodes.getLength();j++) {
            			Element district=(Element)(disnodes.item(j));
            			if(d.equals(district.getAttribute("name"))) {
            				addresscode = district.getAttribute("id");
            				return addresscode;
            			}
            		}
            	}
            }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return addresscode;
	}

	
	// 遍历查找是否有该城市信息，有则返回国际码，无则返回null
	// c ： 武汉市
	public String parse2(Context mContext, String fileName, String c, String d)
			throws Exception {
		if(c == null || c.equals("")) {
			return null;
		}
		//直辖市，查两级
		if(c.contains("北京")
				|| c.contains("上海")
				|| c.contains("天津")
				|| c.contains("重庆")) {
			return parse(mContext, fileName, c, d);
		}
		//普通市
		String addresscode = null;
        DocumentBuilderFactory factory=null;
        DocumentBuilder builder=null;
        Document document=null;
        InputStream inputStream=null;
        if(c == null || d == null) {
        	return null;
        }
        //首先找到xml文件
        factory=DocumentBuilderFactory.newInstance();
		try {
            //找到xml，并加载文档
            builder=factory.newDocumentBuilder();
            inputStream=mContext.getResources().getAssets().open(fileName);
            document=builder.parse(inputStream);

            //找到根Element
            Element root = (Element) document.getDocumentElement();
            NodeList nodes=root.getElementsByTagName("City");
            for(int i=0;i<nodes.getLength();i++){
            	Element cityElement=(Element)(nodes.item(i));
            	if(c.contains(cityElement.getAttribute("name"))) {//城市名匹配
            		return cityElement.getAttribute("id");
            	}
            }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return addresscode;
	}
}
