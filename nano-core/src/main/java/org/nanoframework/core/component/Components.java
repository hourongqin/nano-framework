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
package org.nanoframework.core.component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.nanoframework.commons.format.ClassCast;
import org.nanoframework.commons.loader.LoaderException;
import org.nanoframework.commons.loader.PropertiesLoader;
import org.nanoframework.commons.support.logging.Logger;
import org.nanoframework.commons.support.logging.LoggerFactory;
import org.nanoframework.commons.util.CollectionUtils;
import org.nanoframework.core.component.exception.BindRequestParamException;
import org.nanoframework.core.component.exception.ComponentInvokeException;
import org.nanoframework.core.component.scan.ComponentScan;
import org.nanoframework.core.component.stereotype.Component;
import org.nanoframework.core.component.stereotype.bind.MapperNode;
import org.nanoframework.core.component.stereotype.bind.PathVariable;
import org.nanoframework.core.component.stereotype.bind.RequestMapper;
import org.nanoframework.core.component.stereotype.bind.RequestMapping;
import org.nanoframework.core.component.stereotype.bind.RequestMethod;
import org.nanoframework.core.component.stereotype.bind.RequestParam;
import org.nanoframework.core.component.stereotype.bind.ValueConstants;
import org.nanoframework.core.globals.Globals;
import org.nanoframework.core.status.ComponentStatus;

import com.google.inject.Injector;

/**
 * 组件操作类
 * @author yanghe
 * @date 2015年6月5日 下午3:20:07 
 *
 */
public class Components {

	private static final Logger LOG = LoggerFactory.getLogger(Components.class);
	
	public static final String BASE_PACKAGE = "context.component-scan.base-package";
	
//	private static ConcurrentMap<String, RequestMapper> mapping = new ConcurrentHashMap<>();
	
	private static boolean isLoaded = false;
	
	/**
	 * 加载组件服务，并且装载至组件服务映射表中
	 * 
	 * @param contextPath context主路径
	 * @throws LoaderException 加载异常类
	 * @throws IOException IO异常类
	 * 
	 * @see ComponentScan#scan(String)
	 * @see ComponentScan#filter(Object, Method[], Class, String)
	 * 
	 */
	public static final void load() throws LoaderException, IOException {
		if(isLoaded) {
			throw new LoaderException("Component已经加载，这里不再进行重复的加载，如需重新加载请调用reload方法");
		}

		if(PropertiesLoader.PROPERTIES.size() == 0) {
			throw new LoaderException("没有加载任何的属性文件, 无法加载组件.");
			
		}
		
		PropertiesLoader.PROPERTIES.values().stream().filter(item -> item.get(BASE_PACKAGE) != null).forEach(item -> {
			String[] packageNames = item.getProperty(BASE_PACKAGE).split(",");
			for(String packageName : packageNames)
				ComponentScan.scan(packageName);
		});
		
		Set<Class<?>> componentClasses = ComponentScan.filter(Component.class);
		LOG.info("Component size: " + componentClasses.size());
		
		if(componentClasses.size() > 0) {
			for(Class<?> clz : componentClasses) {
				LOG.info("Inject Component Class: " + clz.getName());
				Object obj = Globals.get(Injector.class).getInstance(clz);
				Method[] methods = clz.getMethods();
				
				Map<String, Map<RequestMethod, RequestMapper>> mapper = ComponentScan.filter(obj, methods, RequestMapping.class, clz.isAnnotationPresent(RequestMapping.class) ? clz.getAnnotation(RequestMapping.class).value() : "");
				for(Entry<String, Map<RequestMethod, RequestMapper>> entry : mapper.entrySet()) {
					MapperNode.addLeaf(entry.getKey(), entry.getValue());
				}
			};
			
		}
		
		isLoaded = true;
	}
	
	/**
	 * 重新加载组件
	 * @throws LoaderException 加载异常类
	 * @throws IOException IO异常类
	 */
	public static final void reload() throws LoaderException, IOException {
//		mapping.clear();
		MapperNode.clear();
		isLoaded = false;
		load();
		
	}
	
	/**
	 * 绑定参数，根据调用的方法参数列表的类型对传入的参数进行类型转换
	 * 
	 * @param method 调用的方法
	 * @param params 请求参数列表
	 * @param objs 附加参数列表
	 * @return 返回绑定结果
	 * 
	 * @see org.nanoframework.commons.format.ClassCast#cast(Object, String)
	 */
	public static final Object[] bindParam(Method method, Map<String, Object> params, Object... objs) {
		if(params == null)
			params = Collections.emptyMap();
		
		params.keySet().forEach(key -> key = key.toLowerCase());
		
		Parameter[] parameters = method.getParameters();
		if(parameters != null && parameters.length > 0) {
			List<Object> values = new ArrayList<>();
			for(Parameter parameter : parameters) {
				Class<?> type = parameter.getType();
				RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
				if(requestParam != null) {
					Object param = params.get(requestParam.name().toLowerCase());
					if(param == null && !StringUtils.equals(requestParam.defaultValue(), ValueConstants.DEFAULT_NONE))
						param = requestParam.defaultValue();
					
					/** 空字符参数现在也将被判定为空，在必填的情况下也将抛出绑定参数异常 by yanghe on 2015-07-01 19:08 */
					if(requestParam.required() && (param == null || (param instanceof String && StringUtils.isEmpty((String) param))))
						throw new BindRequestParamException("参数:["+requestParam.name().toLowerCase()+"]为必填项，但是获取的参数值为空.");
					
					try {
						Object obj = ClassCast.cast(param, type.getName());
						values.add(obj);
						
					} catch(org.nanoframework.commons.exception.ClassCastException e) {
						LOG.error(e.getMessage(), e);
						throw new BindRequestParamException("类型转换异常: 数据类型 [ " + type.getName() + " ], 值 [ " + param + " ]");
						
					}
				} else {
					PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
					if(pathVariable != null) {
						Object param = params.get(pathVariable.value().toLowerCase());
						if(param != null) {
							try {
								Object obj = ClassCast.cast(param, type.getName());
								values.add(obj);
								
							} catch(org.nanoframework.commons.exception.ClassCastException e) {
								LOG.error(e.getMessage(), e);
								throw new BindRequestParamException("类型转换异常: 数据类型 [ " + type.getName() + " ], 值 [ " + param + " ]");
								
							}
						} else {
							throw new BindRequestParamException("Restful风格参数:["+pathVariable.value().toLowerCase()+"]为必填项，但是获取的参数值为空.");
						}
					} else if(objs != null && objs.length > 0) {
						for(Object obj : objs) {
							if(type.isInstance(obj)) {
								values.add(obj);
								break;
							}
						}
					}
				}
				
			}
			
			return values.toArray(new Object[values.size()]);
		}
		
		return null;
	}
	
	/**
	 * 组件服务调用 
	 * @param mapper 组件映射
	 * @param parameter 参数列表
	 * @return 返回调用结果
	 * 
	 * @see org.nanoframework.web.component.Components#bindParam(Method, Map, Object...)
	 */
	public static final Object invoke(RequestMapper mapper, Map<String, Object> parameter, Object... objs) {
		if (mapper != null) {
			try {
				Map<String, String> param = mapper.getParam();
				if(!CollectionUtils.isEmpty(param)) parameter.putAll(param);
				Object obj = mapper.getObject();
				Method method = (Method) mapper.getMethod();
				Object[] bind = Components.bindParam(method, parameter, objs);
				return method.invoke(obj, bind);
				
			} catch(Exception e) {
				LOG.error(e.getMessage());
				if(e instanceof ComponentInvokeException)
					throw (ComponentInvokeException) e;
				
				else {
					Throwable tmp = e;
					while(tmp.getCause() != null) {
						if(tmp.getCause() instanceof ComponentInvokeException) {
							throw (ComponentInvokeException) tmp.getCause();
							
						} else {
							tmp = tmp.getCause();
						}
					}
					
					throw new ComponentInvokeException(tmp.getMessage(), tmp);
				}
			}

		} else
			return ComponentStatus.NOT_FOUND;
		
	}
	
	/**
	 * 获取地址-方法映射
	 * @param url HTTP调用 - 请求URL
	 * @return 返回映射
	 */
	public static final RequestMapper getMapper(String url, RequestMethod method) {
		return MapperNode.get(url, method);
	}
	
}
