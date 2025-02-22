package com.wxy.springbackend.service;

import com.wxy.springbackend.model.User;
import com.wxy.springbackend.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        if(userRepository.findByUsername(username) != null) {
            return false;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        int rows = userRepository.save(user);
        return rows > 0;
    }

    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) return false;
        return user.getPassword().equals(password);
    }

    public boolean resetPassword(Integer userid, String oldPassword, String newPassword) {
        User user = userRepository.findByUserID(userid);
        if (user == null) return false;
        if (!user.getPassword().equals(oldPassword)) return false;
        int isSuccess = userRepository.resetPasswordByUserID(userid,newPassword);
        return isSuccess != 0;
    }

    public User changeInformation(User user){
        return userRepository.changeInformation(user);
    }

    public User getUserInfoById(Integer userid){

        return userRepository.getUserInfoByUserID(userid);
    }


}
