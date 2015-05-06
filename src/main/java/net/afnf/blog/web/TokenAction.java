package net.afnf.blog.web;

import net.afnf.blog.service.TokenService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TokenAction {

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    @ResponseBody
    public String prepare() {
        String token = TokenService.getToken();
        return "{\"token\":\"" + token + "\"}";
    }
}
