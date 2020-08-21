package cn.meiot.entity.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PublishSystemBulletin 
 * @Description: 系统公告
 * @author 贺志辉
 * @date 2019年9月5日
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishSystemBulletin implements Serializable {
	
	//id
	private Long id;
	//标题
	private String title;
	//时间
	private String createTime;

	/**
	 * 类型 1：新增   2：修改
	 */
	private Integer type;

}
