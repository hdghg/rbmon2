package com.github.hdghg.rbmon2.repository;

import com.github.hdghg.rbmon2.model.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@Repository
public class TransitionRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void insert(Transition transition) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", transition.getName(), Types.VARCHAR);
        params.addValue("toStatus", transition.isAlive(), Types.BOOLEAN);
        params.addValue("at", transition.getAt(), Types.TIMESTAMP_WITH_TIMEZONE);
        jdbcTemplate.update("insert into transition(name, alive, at) values (:name, :toStatus, :at)",
                params);
    }

    private static final RowMapper<Transition> MAPPER = (rs, rowNum) -> {
        Transition transition = new Transition();
        transition.setAlive(rs.getBoolean("alive"));
        transition.setAt(rs.getObject("at", Timestamp.class));
        transition.setName(rs.getString("name"));
        return transition;
    };

    public List<Transition> listAll() {
        return jdbcTemplate.query("select * from transition", MAPPER);
    }

    public List<Transition> currentStatus() {
        return jdbcTemplate.query("""
                with cte as (
                  select max(transition_id) id from transition
                  group by name
                )
                select t.* from transition t
                where t.transition_id in (select id from cte c)
                order by at desc""", MAPPER);
    }

}