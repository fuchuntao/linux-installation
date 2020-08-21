package cn.meiot.entity.device;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;


@Data
public class Desired {

    private List<SckData> arrays;
}
