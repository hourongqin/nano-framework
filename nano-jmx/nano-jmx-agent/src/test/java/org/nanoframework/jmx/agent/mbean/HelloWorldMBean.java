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
package org.nanoframework.jmx.agent.mbean;

/**
 * 
 * @author yanghe
 * @date 2015年8月18日 下午10:04:43 
 *
 */
public interface HelloWorldMBean {
    public String getName();

    public void setName(String name);

    public void printHello();

    public String printHello(String theName);

    String worldName();
}
