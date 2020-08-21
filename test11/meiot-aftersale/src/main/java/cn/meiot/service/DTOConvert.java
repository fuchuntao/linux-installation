package cn.meiot.service;

public interface DTOConvert<S,T> {
    T convert(S s);
}
