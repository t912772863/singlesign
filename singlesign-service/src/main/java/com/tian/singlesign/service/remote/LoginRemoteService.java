package com.tian.singlesign.service.remote;

import com.tian.singlesign.remote.ILoginRemoteService;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/3/23 0023.
 */
@Service
public class LoginRemoteService implements ILoginRemoteService{
    public String testLogin(String s) {
        return "receive str: "+s;
    }
}
