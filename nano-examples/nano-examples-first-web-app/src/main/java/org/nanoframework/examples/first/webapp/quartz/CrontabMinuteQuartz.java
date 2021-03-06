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
package org.nanoframework.examples.first.webapp.quartz;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.nanoframework.commons.format.Pattern;
import org.nanoframework.commons.support.logging.Logger;
import org.nanoframework.commons.support.logging.LoggerFactory;
import org.nanoframework.extension.concurrent.exception.QuartzException;
import org.nanoframework.extension.concurrent.quartz.BaseQuartz;
import org.nanoframework.extension.concurrent.quartz.Quartz;

/**
 * @author yanghe
 * @date 2015年11月1日 下午6:01:35
 */
@Quartz(name = "CrontabMinuteQuartz", beforeAfterOnly = true, cron = "0 */1 * * * ?", lazy = true, parallel = 1)
public class CrontabMinuteQuartz extends BaseQuartz {

	private Logger LOG = LoggerFactory.getLogger(CrontabMinuteQuartz.class);
	
	@Override
	public void before() throws QuartzException {

	}

	@Override
	public void execute() throws QuartzException {
		LOG.debug("当前时间(分级控制): " + DateFormatUtils.format(System.currentTimeMillis(), Pattern.TIMESTAMP.get()));
	}

	@Override
	public void after() throws QuartzException {

	}

	@Override
	public void destroy() throws QuartzException {

	}

}
