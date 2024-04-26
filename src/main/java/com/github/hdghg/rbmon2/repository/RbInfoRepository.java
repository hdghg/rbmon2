package com.github.hdghg.rbmon2.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RbInfoRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public Map<String, String> correctNames() {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select name, correct_name from rb_info", Collections.emptyMap());
        return maps.stream().collect(Collectors.toMap(m -> m.get("name").toString(), m -> m.get("correct_name").toString()));
    }
}
