package net.afnf.blog.service;

import net.afnf.blog.domain.User;
import net.afnf.blog.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Throwable.class)
public class UserService {

    @Autowired
    private UserMapper um;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public int getUserCount() {
        return um.countByExample(null);
    }

    public void registerAdmin(String username, String password) {

        if (getUserCount() != 0) {
            throw new IllegalStateException("illegal1");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_ADMIN");
        um.insertSelective(user);

        if (getUserCount() != 1) {
            throw new IllegalStateException("illegal2");
        }
    }

}
