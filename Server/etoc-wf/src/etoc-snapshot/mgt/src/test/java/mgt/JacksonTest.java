/**
 * 创建时间
 * 2015年3月12日-下午2:01:40
 * 
 * 
 */
package mgt;

import java.util.Map;

import net.etoc.user.entity.WfMgtUser;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月12日 下午2:01:40
 * 
 * @version 1.0.0
 * 
 */

public class JacksonTest {
	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		WfMgtUser u = new WfMgtUser();
		u.setId(23232);
		u.setNickname("aa");
		u.setPassword("bbb");
		String s = mapper.writeValueAsString(u);

		String json = "{\"id\":\"1221\",\"nickname\":20,\"password\":844099200000}";
		Map<String, Object> ss = mapper.readValue(json, Map.class);
		System.out.println(ss.get("nickname"));

	}
}
