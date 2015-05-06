package net.afnf.blog.web.admin;

import net.afnf.blog.domain.Comment;
import net.afnf.blog.service.CommentService;
import net.afnf.blog.web.TokenCheckableAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommentsAction extends TokenCheckableAction {

    final static int LIMIT = 20;

    @Autowired
    private CommentService cs;

    @RequestMapping(value = "/_admin/comments", method = RequestMethod.GET)
    public String index(@RequestParam(value = "page", required = false) Integer page, Model model) {

        model.addAttribute("pagingList", cs.getComments(page));
        return "_admin/comments";
    }

    @RequestMapping(value = "/_admin/comments", params = "post", method = RequestMethod.POST)
    @ResponseBody
    public String postEdit(@ModelAttribute("comment") Comment comment) {
        checkToken();

        cs.updateCommentState(comment.getId(), comment.getState());

        return SUCCESS_JSON;
    }
}
