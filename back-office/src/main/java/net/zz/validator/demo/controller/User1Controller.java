package net.zz.validator.demo.controller;

import com.egzosn.validator.error.MsgCode;
import com.egzosn.validator.validator.annotation.Validated;
import com.egzosn.validator.validator.constraints.QQ;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by ZaoSheng on 2015/7/2.
 */
@RestController
@RequestMapping("bb")
@Validated
public class User1Controller extends SupportController {

    @RequestMapping("set")
    public Map<String, Object> set(@MsgCode(100101) @QQ String qq) {
        System.out.println(qq);
        return successData();
    }

}
