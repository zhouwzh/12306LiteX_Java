package com.wxy.springbackend.repository;

import com.wxy.springbackend.model.PathStation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PathStationRepository {
    private final JdbcTemplate jdbcTemplate;

    public PathStationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PathStation> getAllPathStation(){
        String sql = """
                SELECT
                    ps.path_id,
                    ps.start_time,
                    ps.station_id,
                    s.station_name,
                    ps.station_type,
                    t.train_name,
                    ps.a_seats_available,
                    ps.b_seats_available,
                    ps.c_seats_available
                FROM
                    wxy_path_station ps
                JOIN
                    wxy_station s ON ps.station_id = s.station_id
                JOIN
                    wxy_path p ON ps.path_id = p.path_id
                JOIN 
                    wxy_train t ON p.train_id = t.train_id;
                """;

        return jdbcTemplate.query(sql, new Object[]{},
                (ResultSet rs) -> {
                    Map<Integer, PathStation> map = new HashMap<>();

                    int i = 0;
                    while (rs.next()) {
                        int path_id = rs.getInt("path_id");
                        String start_time = rs.getString("start_time");
                        int station_id = rs.getInt("station_id");
                        String station_name = rs.getString("station_name");
                        String station_type = rs.getString("station_type");
                        String train_name = rs.getString("train_name");
                        int a_seats_available = rs.getInt("a_seats_available");
                        int b_seats_available = rs.getInt("b_seats_available");
                        int c_seats_available = rs.getInt("c_seats_available");

                        PathStation pathStation = new PathStation();

                        pathStation.setPathid(path_id);
                        pathStation.setStart_time(start_time);
                        pathStation.setStation_id(station_id);
                        pathStation.setStation_name(station_name);
                        pathStation.setStation_type(station_type);
                        pathStation.setTrain_name(train_name);
                        pathStation.setA_seats_available(a_seats_available);
                        pathStation.setB_seats_available(b_seats_available);
                        pathStation.setC_seats_available(c_seats_available);
                        map.put(i, pathStation);
                        i += 1;
                    }
                    return new ArrayList<>(map.values());
                }
        );
    }
}
