package woojooin.planitbatch.batch.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.vo.Member;

@Slf4j
@Component
public class MemberReader implements ItemReader<List<Member>> {

    private final JdbcTemplate jdbcTemplate;
    private static final int CHUNK_SIZE = 1000;
    private int currentOffset = 0;
    private boolean hasMoreData = true;

    public MemberReader(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Member> read() throws Exception {
        if (!hasMoreData) {
            return null;
        }

        try {
            String sql = "SELECT member_id as memberId FROM member ORDER BY member_id LIMIT ? OFFSET ?";

            List<Member> members = jdbcTemplate.query(
                sql,
                new Object[]{CHUNK_SIZE, currentOffset},
                new MemberRowMapper()
            );

            if (members.isEmpty() || members.size() < CHUNK_SIZE) {
                hasMoreData = false;
            }

            currentOffset += CHUNK_SIZE;

            log.debug("조회된 Member 수: {}, 현재 offset: {}", members.size(), currentOffset);

            return members.isEmpty() ? null : members;

        } catch (Exception e) {
            log.error("Member 데이터 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    private static class MemberRowMapper implements RowMapper<Member> {
        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
            Member member = new Member();
            member.setMemberId(rs.getLong("memberId"));
            return member;
        }
    }
}