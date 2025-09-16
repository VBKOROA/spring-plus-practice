package org.example.expert.domain.batch.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.example.expert.domain.user.enums.UserRole;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void insertBatch(List<UserInsertQuery> insertQueries) {
        String sql = "INSERT INTO users (email, nickname, password, user_role, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                UserInsertQuery query = insertQueries.get(i);
                ps.setString(1, query.randomStr());
                ps.setString(2, query.randomStr());
                ps.setString(3, query.randomStr());
                ps.setString(4, UserRole.USER.name());
                ps.setObject(5, query.time());
                ps.setObject(6, query.time());
            }

            @Override
            public int getBatchSize() {
                return insertQueries.size();
            }
        });
    }
}
