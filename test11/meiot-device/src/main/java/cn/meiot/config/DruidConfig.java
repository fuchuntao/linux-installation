package cn.meiot.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * @author fengshaoyu
 * @title: DruidConfig
 * @projectName spacepm
 * @description: 注入数据源
 * @date 2018-10-08 15:35
 */
@Configuration
public class DruidConfig {

	@SuppressWarnings("resource")
	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {

		DruidDataSource druidDataSource = new DruidDataSource();

		List filterList = new ArrayList<>();

		filterList.add(wallFilter());

		druidDataSource.setProxyFilters(filterList);

		return druidDataSource;

	}

	@Bean

	public WallFilter wallFilter() {

		WallFilter wallFilter = new WallFilter();

		wallFilter.setConfig(wallConfig());

		return wallFilter;

	}

	@Bean

	public WallConfig wallConfig() {

		WallConfig config = new WallConfig();

		config.setMultiStatementAllow(true);// 允许一次执行多条语句

		config.setNoneBaseStatementAllow(true);// 允许非基本语句的其他语句

		return config;

	}

}
