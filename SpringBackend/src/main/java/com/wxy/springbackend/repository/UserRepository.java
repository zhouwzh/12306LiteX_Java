package com.wxy.springbackend.repository;


import com.wxy.springbackend.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setPassword(rs.getString("password"));
        user.setUsername(rs.getString("user_name"));
        return user;
    };

    public int save(User user) {
        String sql = "INSERT INTO wxy_user (password, user_name) VALUES (?, ?)";
        return jdbcTemplate.update(sql,
                user.getPassword(),
                user.getUsername());
    }

    public User findByUsername(String userName) {
        String sql = "SELECT user_id, password, user_name FROM wxy_user WHERE user_name = ?";
        return jdbcTemplate.query(sql, new Object[]{userName}, userRowMapper)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public User findByUserID(Integer userID) {
        String sql = "SELECT user_id, password, user_name FROM wxy_user WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userID}, userRowMapper)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public int resetPasswordByUserID(Integer userid, String password) {
        String sql = "UPDATE wxy_user SET password = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql, password, userid);
    }

    public User changeInformation(User user){
        String sql = "UPDATE wxy_user SET user_name = ?, fname = ?, lname = ?, birth_date = ?, gender = ?," +
                "   nationality = ?, email = ?, phone = ? WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                user.getUsername(),
                user.getFname(),
                user.getLname(),
                user.getBirthDate(),
                user.getGender(),
                user.getNationality(),
                user.getEmail(),
                user.getPhone(),
                user.getId()
        );
        if(rowsAffected > 0){
            return user;
        }else{
            throw new RuntimeException("Failed to update user information. User ID: " + user.getId());
        }
    }

    private final RowMapper<User> userInfoRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("user_name"));
        user.setPassword(rs.getString("password"));
        user.setFname(rs.getString("fname"));
        user.setLname(rs.getString("lname"));
        if(rs.getString("birth_date") != null){
            user.setBirthDate(rs.getTimestamp("birth_date").toLocalDateTime());
        }else{
            user.setBirthDate(null);
        }

        user.setGender(rs.getString("gender"));
        user.setNationality(rs.getString("nationality"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        return user;
    };

    public User getUserInfoByUserID(Integer userID){
        String sql = "SELECT user_id, user_name, password, fname, lname, birth_date, gender, nationality, email, phone FROM wxy_user WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userID}, userInfoRowMapper)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
