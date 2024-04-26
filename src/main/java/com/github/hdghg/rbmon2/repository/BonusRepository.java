package com.github.hdghg.rbmon2.repository;

import com.github.hdghg.rbmon2.model.CharacterBonus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.List;

@Repository
public class BonusRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<CharacterBonus> bonusStatus(String party) {
        String sqlNext25 = "select c.character_id as id, c.nickname, max(bl.at) as at from character c\n" +
                "left join bonus_log bl on c.character_id = bl.character_id\n" +
                "where party " + (party == null ? "is null" : "= :party") + "\n" +
                "group by c.character_id, c.nickname\n" +
                "order by at asc nulls first\n" +
                "limit 20\n";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("party", party, Types.VARCHAR);
        return jdbcTemplate.query(sqlNext25, params, new RowMapper<CharacterBonus>() {
            @Override
            public CharacterBonus mapRow(ResultSet rs, int rowNum) throws SQLException {
                CharacterBonus characterBonus = new CharacterBonus();
                characterBonus.setAt(rs.getTimestamp("at"));
                characterBonus.setId(rs.getInt("id"));
                characterBonus.setNickname(rs.getString("nickname"));
                return characterBonus;
            }
        });
    }

    public boolean registerBonusTaken(Integer id) {
        Timestamp now = Timestamp.from(Instant.now());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("at", now, Types.TIMESTAMP_WITH_TIMEZONE);
        params.addValue("discordUser", "todo", Types.VARCHAR);
        params.addValue("characterId", id, Types.INTEGER);
        String sql = "insert into bonus_log (at, discord_user, character_id) " +
                "values (:at, :discordUser, :characterId)";
        return jdbcTemplate.update(sql, params) > 0;
    }

}
