package cn.meiot.utils;

import cn.meiot.entity.SysPermission;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MenuTreeUtil {


    public  List<SysPermission> menuList(List<SysPermission> menu){
        List<SysPermission> menuCommon = menu;
        Vector<SysPermission> list = new Vector<SysPermission>();
        for (SysPermission x : menu) {
            SysPermission sysPermission = new SysPermission();
            if(x.getPid()== 0){
                setProperty(sysPermission,x,menuCommon);
                list.add(sysPermission);
            }
        }
        return list;
    }

    public List<SysPermission> menuChild(Integer id,List<SysPermission> menuCommon){
        List<SysPermission> lists = new LinkedList<SysPermission>();
        for(SysPermission a:menuCommon){
            SysPermission sysPermission = new SysPermission();
            if(a.getPid().equals(id)){
                setProperty(sysPermission,a,menuCommon);
                lists.add(sysPermission);
            }

        }
        return lists;
    }


    private void setProperty(SysPermission sysPermission,SysPermission x,List<SysPermission> menuCommon){
        sysPermission.setId(x.getId());
        sysPermission.setName(x.getName());
        sysPermission.setPid(x.getPid());
        sysPermission.setPermission(x.getPermission());
        sysPermission.setChecked(x.getChecked());
        sysPermission.setChildNodes(menuChild(x.getId(),menuCommon));
    }

}
