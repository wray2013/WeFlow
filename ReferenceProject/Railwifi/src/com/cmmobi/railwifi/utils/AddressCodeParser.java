package com.cmmobi.railwifi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

import android.content.Context;

public class AddressCodeParser {

	public AddressCodeParser() {

	}
	
	public String parseJsonFile(Context mContext, String filePath) {
		String json = readTxtFile(mContext, filePath);
		return json;
	}
	
	public List<String> parseJsonProv(Context mContext, String filePath, boolean sort) {
		String json = parseJsonFile(mContext, filePath);
		return parseJsonProvByJson(mContext, json, sort);
	}
	
	public List<String> parseJsonProvByJson(Context mContext, String json, boolean sort) {
		List<String> Provinces = new ArrayList<String>();
		Gson gson = new Gson();
		Address address = null;
		try {
			address = gson.fromJson(json, Address.class);

			if (address != null && address.citylist != null) {
				List<Province> prov = Arrays.asList(address.citylist);
				for (Province p : prov) {
					if (p != null) {
						Provinces.add(p.p);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(sort)
			Collections.sort(Provinces, new PinyinComparator());
		
		return Provinces;
	}
	
	public List<String> parseAreasByProvCity(String json, String provName, String cityName,
			boolean sort) {
		List<String> Areas = new ArrayList<String>();
		Gson gson = new Gson();
		Address address = null;
		try {
			address = gson.fromJson(json, Address.class);
			if (address != null && address.citylist != null) {
				List<Province> prov = Arrays.asList(address.citylist);
				for (Province p : prov) {
					if (p != null) {
						if (p.p.equals(provName)) {
							List<City> city = Arrays.asList(p.c);
							for (City c : city) {
								if(c != null) {
									if(c.n.equals(cityName)) {
										if(c.a != null) {
											List<Area> area = Arrays.asList(c.a);
											for (Area a : area) {
												if(a != null) {
													Areas.add(a.s);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sort)
			Collections.sort(Areas, new PinyinComparator());
		return Areas;
	}
	
	public List<String> parseCitiesByProv(String json, String provName,
			boolean sort) {
		List<String> Cities = new ArrayList<String>();
		Gson gson = new Gson();
		Address address = null;
		try {
			address = gson.fromJson(json, Address.class);
			if (address != null && address.citylist != null) {
				List<Province> prov = Arrays.asList(address.citylist);
				for (Province p : prov) {
					if (p != null) {
						if (p.p.equals(provName)) {
							List<City> city = Arrays.asList(p.c);
							for (City c : city) {
								if (c != null) {
									Cities.add(c.n);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sort)
			Collections.sort(Cities, new PinyinComparator());
		return Cities;
	}
	
	public List<String> parseJsonCities(Context mContext, String filePath, boolean sort) {
		String json = parseJsonFile(mContext, filePath);
		return parseJsonCitiesByJson(mContext, json, sort);
	}

	public List<String> parseJsonCitiesByJson(Context mContext, String json, boolean sort) {
		List<String> cities = new ArrayList<String>();
		Gson gson = new Gson();
		Address address = null;
		try {
			address = gson.fromJson(json, Address.class);

			if (address != null && address.citylist != null) {
				List<Province> prov = Arrays.asList(address.citylist);
				for (Province p : prov) {
					if (p != null) {
						List<City> city = Arrays.asList(p.c);
						for (City c : city) {
							if (c != null) {
								if (c.a != null) {
									cities.add(c.n);
								} else {
									cities.add(p.p); // 直辖市
									break;
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(sort)
			Collections.sort(cities, new PinyinComparator());
		
		return cities;
	}
	
	public static String readTxtFile(Context mContext, String filePath) {
//		String encoding = "utf-8";
		StringBuilder sb = new StringBuilder();
		try {
			if (filePath != null && !filePath.equals("")) { // 简单判断文件名是否合法
				InputStream read = mContext.getResources().getAssets()
						.open(filePath);// new InputStreamReader(new
										// FileInputStream(file), encoding);//
										// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(read));
				String str = "";
				while ((str = bufferedReader.readLine()) != null) {
					sb.append(str);
				}
				read.close();
				return sb.toString();
			} else {
				System.out.println("找不到指定的文件");
				return null;
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 打开xml文件
	 * 
	 * @param mContext
	 * @param fileName
	 * @return
	 */
	private Document OpenXmlFile(Context mContext, String fileName) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document document = null;
		InputStream inputStream = null;
		if (fileName != null && !fileName.equals("")) {
			factory = DocumentBuilderFactory.newInstance();
			try {
				// 找到xml，并加载文档
				builder = factory.newDocumentBuilder();
				inputStream = mContext.getResources().getAssets()
						.open(fileName);
				document = builder.parse(inputStream);

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
		}
		return document;
	}

	/**
	 * 得到全国城市列表
	 * 
	 * @param mContext
	 * @param fileName
	 * @return
	 */
	public List<String> parseXMLCities(Context mContext, String fileName) {
		List<String> citylist = new ArrayList<String>();
		Document dc = OpenXmlFile(mContext, fileName);
		if (dc != null) {
			// 找到根Element
			Element root = (Element) dc.getDocumentElement();
			NodeList nodes = root.getElementsByTagName("City");
			for (int i = 0; i < nodes.getLength(); i++) {
				Element cityElement = (Element) (nodes.item(i));
				String city = cityElement.getAttribute("name");// 城市名
				if (city != null && !city.equals("")) {
					citylist.add(city);
				}
			}
		}
		return citylist;
	}

	// 遍历查找是否有该城市信息，有则返回国际码，无则返回null
	public String parseXML(Context mContext, String fileName, String c, String d)
			throws Exception {
		String addresscode = null;
		if (c == null || d == null) {
			return null;
		}
		Document dc = OpenXmlFile(mContext, fileName);

		try {
			if (dc != null) {
				// 找到根Element
				Element root = (Element) dc.getDocumentElement();
				NodeList nodes = root.getElementsByTagName("City");
				for (int i = 0; i < nodes.getLength(); i++) {
					Element cityElement = (Element) (nodes.item(i));
					if (c.equals(cityElement.getAttribute("name"))) {// 城市名匹配
						NodeList disnodes = cityElement
								.getElementsByTagName("district");
						for (int j = 0; j < disnodes.getLength(); j++) {
							Element district = (Element) (disnodes.item(j));
							if (d.equals(district.getAttribute("name"))) {
								addresscode = district.getAttribute("id");
								return addresscode;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addresscode;
	}

	// 遍历查找是否有该城市信息，有则返回国际码，无则返回null
	// c ： 武汉市
	public String parseXML2(Context mContext, String fileName, String c,
			String d) throws Exception {
		if (c == null || c.equals("")) {
			return null;
		}
		// 直辖市，查两级
		if (c.contains("北京") || c.contains("上海") || c.contains("天津")
				|| c.contains("重庆")) {
			return parseXML(mContext, fileName, c, d);
		}

		Document dc = OpenXmlFile(mContext, fileName);
		// 普通市
		String addresscode = null;
		if (c == null || d == null) {
			return null;
		}
		try {
			// 找到根Element
			Element root = (Element) dc.getDocumentElement();
			NodeList nodes = root.getElementsByTagName("City");
			for (int i = 0; i < nodes.getLength(); i++) {
				Element cityElement = (Element) (nodes.item(i));
				if (c.contains(cityElement.getAttribute("name"))) {// 城市名匹配
					return cityElement.getAttribute("id");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addresscode;
	}

	public static class Address {
		Province[] citylist; // 省
	}

	public static class Province {
		String p; // 省名
		City[] c; // 城市
	}

	public static class City {
		String n; // 城市名
		Area[] a; // 区
	}

	public static class Area {
		String s; // 区名
	}

	public class PinyinComparator implements Comparator<String> {
		public int compare(String o1, String o2) {

			String s1 = CharacterParser.getInstance().getSpelling(o1);
			String s2 = CharacterParser.getInstance().getSpelling(o2);
			
			return s1.compareTo(s2);
		}
	}
}
