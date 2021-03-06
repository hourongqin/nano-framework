/**
 * Copyright 2015 the original author or authors.
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
package org.nanoframework.core.chain;

/**
 * @author yanghe
 * @date 2015年12月3日 下午1:25:02
 */
public class PlusChain extends AbstractChain {

	@Override
	public Object execute(Object object) {
		System.out.println(object);
		long num = (long) object;
		return next(num + 2);
	}

}
