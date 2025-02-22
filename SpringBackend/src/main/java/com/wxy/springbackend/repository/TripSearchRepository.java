package com.wxy.springbackend.repository;

import com.wxy.springbackend.model.TripSearch;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.*;

import static java.lang.Math.min;

@Repository
public class TripSearchRepository {
    private final JdbcTemplate jdbcTemplate;

    public TripSearchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TripSearch> findAll(String depart_station, String arrival_station, String datetime){

        String sql = """
                with DepartStation AS(
                    SELECT
                        ps.path_id,
                        ps.station_id,
                        s.station_name,
                        ps.start_time AS depart_time
                    FROM
                        wxy_path_station ps
                    JOIN
                        wxy_station s ON ps.station_id = s.station_id
                    WHERE
                        s.station_name = ? and DATE(ps.start_time) = ?
                ),
                ArrivalStation AS(
                    SELECT
                        ps.path_id,
                        ps.station_id,
                        s.station_name,
                        ps.start_time AS arrival_time
                    FROM
                        wxy_path_station ps
                    JOIN
                        wxy_station s ON ps.station_id = s.station_id
                    WHERE
                        s.station_name = ? and DATE(ps.start_time) = ?
                ),
                ValidPaths AS(
                SELECT
                    d.path_id
                FROM
                    DepartStation d
                JOIN
                    ArrivalStation a ON d.path_id = a.path_id
                WHERE
                    d.depart_time < a.arrival_time
                )
                SELECT
                    ps.path_id,
                    s.station_name,
                    ps.start_time,
                    ps.a_seats_available,
                    ps.b_seats_available,
                    ps.c_seats_available,
                    t.train_name
                FROM
                    wxy_path_station ps
                JOIN
                    wxy_station s ON ps.station_id = s.station_id
                JOIN
                    wxy_path p ON ps.path_id = p.path_id
                JOIN
                    wxy_train t ON p.train_id = t.train_id
                WHERE
                    ps.path_id IN (SELECT path_id FROM ValidPaths)
                AND
                    DATE(ps.start_time) = ?
                ORDER BY
                    ps.path_id, ps.start_time;
                """;
        return jdbcTemplate.query(sql, new Object[]{depart_station, datetime, arrival_station, datetime, datetime},
                (ResultSet rs) ->{
                    Map<Integer, TripSearch> map = new HashMap<>();

                    boolean flag = false;
                    int minSeatA = 1000;
                    int minSeatB = 1000;
                    int minSeatC = 1000;
                    while(rs.next()) {
                        int path_id = rs.getInt("path_id");
                        String station_name = rs.getString("station_name");
                        String start_time = rs.getString("start_time");
                        int aseats = rs.getInt("a_seats_available");
                        int bseats = rs.getInt("b_seats_available");
                        int cseats = rs.getInt("c_seats_available");
                        String train_name = rs.getString("train_name");
//                        System.out.println(path_id + " "+station_name +" "+ start_time + " "+aseats + " "+bseats + " "+cseats + " "+train_name);

                        TripSearch trip = map.computeIfAbsent(path_id, id -> { //check path_id, if absent create
                           TripSearch t = new TripSearch();
                           t.setPathId(id);
                           t.setStations(new ArrayList<>());
                           t.setArrivalTimeList(new ArrayList<>());
                           t.setDepartStationId(depart_station);
                           t.setArrivalStationId(arrival_station);
                           t.setTrain_name(train_name);
//                           System.out.println(t);
                           return t;
                        });

                        if(station_name.equals(depart_station)){
                            flag = true;
                            trip.getStations().add(station_name);
                            trip.getArrivalTimeList().add(start_time);
                            minSeatA = 1000;
                            minSeatB = 1000;
                            minSeatC = 1000;
                            minSeatA = min(minSeatA, aseats);
                            minSeatB = min(minSeatB, bseats);
                            minSeatC = min(minSeatC, cseats);
//                            System.out.println(trip);
                        }else if(station_name.equals(arrival_station)){
                            trip.getStations().add(station_name);
                            trip.getArrivalTimeList().add(start_time);
                            flag = false;
                            minSeatA = min(minSeatA, aseats);
                            minSeatB = min(minSeatB, bseats);
                            minSeatC = min(minSeatC, cseats);
                            trip.setaSeatsLeft(minSeatA);
                            trip.setbSeatsLeft(minSeatB);
                            trip.setcSeatsLeft(minSeatC);

                        }else if(flag){
                            trip.getStations().add(station_name);
                            trip.getArrivalTimeList().add(start_time);
                            minSeatA = min(minSeatA, aseats);
                            minSeatB = min(minSeatB, bseats);
                            minSeatC = min(minSeatC, cseats);

                        }


                    }
                    return new ArrayList<>(map.values());
                }
        );

    }



}
