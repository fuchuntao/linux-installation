package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.Bulletin;
import cn.meiot.entity.vo.Result;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IBulletinService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wuyou
 * @since 2019-12-17
 */
@RequestMapping("/bulletin")
@RestController
@SuppressWarnings("all")
public class BulletinController extends BaseController {

    @Resource(name = "bulletinServiceImpl")
    private IBulletinService bulletinService;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private RedisUtil redisUtil;



    @RequestMapping(value = "add",method = RequestMethod.POST)
    @Log(operateModule = "消息模块", operateContent = "添加公告")
    public Result addBulletin( @RequestParam("title") String title,
                               @RequestParam("pushTime") String pushTime,
                               @RequestParam("content") String content,
                               @RequestParam("pushAims") Integer pushAims,
                               @RequestParam(value = "expireDate",required = false) String expireDate,
                               @RequestParam("expire") String expire) {
        Bulletin bulletin = new Bulletin();
        bulletin.setContent(content);
        bulletin.setCreateBy(getUserId());
        bulletin.setCreateTime(CommonUtil.getDate());
        bulletin.setPushTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(pushTime)));
        bulletin.setTitle(title);
        bulletin.setPushAims(pushAims);
        if ("1".equals(expire)) {
            if (StringUtils.isEmpty(expireDate)) {
                throw new MyServiceException("有效期公告必须提交有效期时间","500");
            }
            if (expireDate.compareTo(pushTime)<0) {
                throw new MyServiceException(" 过期时间必须大于推送时间，否则！ 都过期了还推送毛线啊！","500");
            }
            bulletin.setExpireDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(expireDate)));
        }
        bulletin.setExpire(Integer.valueOf(expire));

        return bulletinService.save(bulletin) ? Result.getDefaultTrue() : Result.getDefaultFalse();
    }

    @GetMapping("getList")
    @Log(operateModule = "消息模块", operateContent = "查询公告列表")
    public Result getList(@RequestParam("currentPage") Integer currentPage,
                          @RequestParam("pageSize") Integer pageSize) {
        Page<Bulletin> page = new Page<>(currentPage, pageSize);
        IPage<Bulletin> page1 = bulletinService.page(page, new QueryWrapper<Bulletin>().lambda().orderByDesc(Bulletin::getCreateTime));
        List<Bulletin> records = page1.getRecords();
        for (Bulletin record : records) {
            Long userId = record.getCreateBy();
            Object nickName = redisUtil.getHashValueByKey(RedisConstantUtil.USER_NIKNAMES, userId.toString());
            if (null == nickName) {
                nickName=userFeign.getNiknameByUserId(userId);
            }
            record.setNickName(nickName.toString());
        }
        Result result = Result.getDefaultTrue();
        result.setData(page1);
        return result;
    }

    @PostMapping("delete")
    @Log(operateModule = "消息模块", operateContent = "删除公告")
    public Result deleteBulletin(@RequestBody List<Long> ids) {
        return bulletinService.myRemoveByIds(ids) ? Result.getDefaultTrue() : Result.getDefaultFalse();
    }

    @PostMapping("updateBulletin")
    @Log(operateModule = "消息模块", operateContent = "修改公告")
    public Result updateBulletin(@RequestParam("title") String title,
                                 @RequestParam("pushTime") String pushTime,
                                 @RequestParam("content") String content,
                                 @RequestParam("pushAims") Integer pushAims,
                                 @RequestParam("id") Long id,
                                 @RequestParam(value = "expireDate",required = false) String expireDate,
                                 @RequestParam("expire") String expire) {
        Bulletin bulletin = bulletinService.getById(id);
        if (null == bulletin) {
            return Result.faild("此公告不存在");
        }
        if ("1".equals(expire)) {
            if (StringUtils.isEmpty(expireDate)) {
                throw new MyServiceException("有效期公告必须提交有效期时间","500");
            }
            if (expireDate.compareTo(pushTime)<0) {
                throw new MyServiceException(" 过期时间必须大于推送时间，否则！ 都过期了还推送毛线啊！","500");
            }
            bulletin.setExpireDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(expireDate)));
        }
        bulletin.setExpire(Integer.valueOf(expire));
        bulletin.setContent(content);
        bulletin.setCreateBy(getUserId());
        bulletin.setCreateTime(CommonUtil.getDate());
        bulletin.setPushTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(pushTime)));
        bulletin.setTitle(title);
        bulletin.setUpdateBy(getUserId());
        bulletin.setUpdateTime(CommonUtil.getDate());
        bulletin.setId(id);
        //如果修改了推送目标、则需要调用bulletinService.myUpdateById此方法来修改公告 因为 修改推送目标等于重新推送 需要删除用户消息表内的公告
        if (!bulletin.getPushAims().equals(pushAims)) {
            bulletin.setPushAims(pushAims);
          return   bulletinService.myUpdateById(bulletin) ? Result.getDefaultTrue() : Result.getDefaultFalse();
        }
        return bulletinService.updateById(bulletin)? Result.getDefaultTrue() : Result.getDefaultFalse();
    }

    @GetMapping("nofilter/getOneById/{id}")
    public Result getOneById(@PathVariable Long id){
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(bulletinService.getById(id));
        return defaultTrue;
    }

    @GetMapping("getBulletins")
    public Result getBulletins(){
        Result result = Result.getDefaultTrue();
        List<Bulletin> byType = bulletinService.findByType(2);
        result.setData(byType);
        return result;
    }
}