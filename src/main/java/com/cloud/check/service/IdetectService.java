package com.cloud.check.service;

import com.cloud.check.model.HelloDTO;

public interface IdetectService {
    public HelloDTO detect(String name) throws Exception;
}
