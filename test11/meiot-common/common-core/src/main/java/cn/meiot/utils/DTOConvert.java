package cn.meiot.utils;

public interface DTOConvert<S,T> {
    T convert(S s);
}
