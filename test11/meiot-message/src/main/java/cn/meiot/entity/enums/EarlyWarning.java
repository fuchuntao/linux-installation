package cn.meiot.entity.enums;

/**
 * @Package cn.meiot.entity.enums
 * @Description:
 * @author: 武有
 * @date: 2020/1/9 9:54
 * @Copyright: www.spacecg.cn
 */
public class EarlyWarning {;

   public static Integer get(Integer index){
       switch (index){
           case 1:
               return 10;
           case 2:
               return 11;
           case 3:
               return 12;
           case 4:
               return 13;
           case 5:
               return 14;
           case 6:
               return 15;
           case 7:
               return 16;
       }
       return null;

   }
}
