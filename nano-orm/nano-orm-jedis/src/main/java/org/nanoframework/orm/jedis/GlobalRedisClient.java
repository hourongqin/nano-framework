/**
 * Copyright 2015- the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 			http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nanoframework.orm.jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * RedisClient的全局设置
 * 
 * @author yanghe
 * @date 2015年7月16日 下午4:52:05 
 *
 */
public class GlobalRedisClient {
	private static Map<String, RedisClient> globals = new HashMap<>();
	
	private GlobalRedisClient() { }
	
	public static void set(String type, RedisClient global) {
		globals.put(type, global);
		
	}
	
	public static final RedisClient get(String type) {
		return globals.get(type);
	}
	
	public static final Set<String> keys() {
		return globals.keySet();
	}
}
