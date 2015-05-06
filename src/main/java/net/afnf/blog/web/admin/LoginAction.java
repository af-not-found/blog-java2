package net.afnf.blog.web.admin;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.afnf.blog.domain.User;
import net.afnf.blog.service.UserService;
import net.afnf.blog.web.TokenCheckableAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginAction extends TokenCheckableAction {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected UserService us;

    @RequestMapping(value = "/_admin/pub/login", method = RequestMethod.GET)
    public String index(Model model) {

        if (us.getUserCount() >= 1) {
            return "_admin/pub/login";
        }
        else {
            model.addAttribute("user", new User());
            return "_admin/pub/register";
        }
    }

    @RequestMapping(value = "/_admin/pub/login", params = "post", method = RequestMethod.POST)
    @ResponseBody
    public String register(@ModelAttribute("user") @Valid User user) {
        checkToken();

        us.registerAdmin(user.getUsername(), user.getPassword());

        return SUCCESS_JSON;
    }

}
