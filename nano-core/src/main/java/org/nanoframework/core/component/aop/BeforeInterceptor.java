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
package org.nanoframework.core.component.aop;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.nanoframework.core.globals.Globals;

import com.google.inject.Injector;

/**
 * @author yanghe
 * @date 2015年10月8日 下午5:30:23
 */
public class BeforeInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Before before = invocation.getMethod().getAnnotation(Before.class);
		Method method = before.classType().getMethod(before.methodName(), MethodInvocation.class);
		Object instance;
		if(before.singleton()) {
			if((instance = Globals.get(before.classType())) == null) {
				instance = Globals.get(Injector.class).getInstance(before.classType());
				Globals.set(before.classType(), instance);
			}
		} else 
			instance = before.classType().newInstance();
		
		method.invoke(instance, invocation);
		return invocation.proceed();
	}
}
