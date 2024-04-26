package com.github.hdghg.rbmon2.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
public class CharacterRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public boolean insertOrIgnore(String nickname, @Nullable String party) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname, Types.VARCHAR);
        params.addValue("party", party, Types.VARCHAR);
        String sql = """
                with cte as (
                    select :nickname as nickname
                    except
                    select nickname from character where nickname = :nickname
                )
                insert into character(nickname, party) select nickname, :party from cte
                """;
        int result = jdbcTemplate.update(sql, params);
        return result > 0;
    }

    public boolean delete(String nickname) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname, Types.VARCHAR);
        String sql = "delete from character where nickname = :nickname";
        int result = jdbcTemplate.update(sql, params);
        return result > 0;
    }

}
