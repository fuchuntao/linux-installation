package cn.meiot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class HttpConstart {
	@Value("${php.http}")
	private String http; 
}
