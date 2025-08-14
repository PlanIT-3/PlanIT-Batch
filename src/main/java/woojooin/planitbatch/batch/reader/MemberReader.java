package woojooin.planitbatch.batch.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.mapper.MemberMapper;
import woojooin.planitbatch.domain.vo.Member;

@Slf4j
@Component
public class MemberReader implements ItemReader<List<Member>> {

	private final MemberMapper memberMapper;
	private static final int CHUNK_SIZE = 1000;
	private Long lastMemberId = null;
	private boolean hasMoreData = true;

	public MemberReader(MemberMapper memberMapper) {
		this.memberMapper = memberMapper;
	}

	@Override
	public List<Member> read() throws Exception {
		if (!hasMoreData) {
			return null;
		}

		try {
			Map<String, Object> params = new HashMap<>();
			params.put("limit", CHUNK_SIZE);
			if (lastMemberId != null) {
				params.put("lastMemberId", lastMemberId);
			}

			List<Member> members = memberMapper.getMembersPaginated(params);

			if (members.isEmpty() || members.size() < CHUNK_SIZE) {
				hasMoreData = false;
			}

			if (!members.isEmpty()) {
				lastMemberId = members.get(members.size() - 1).getMemberId();
			}

			log.debug("조회된 Member 수: {}, 마지막 memberId: {}", members.size(), lastMemberId);

			return members.isEmpty() ? null : members;

		} catch (Exception e) {
			log.error("Member 데이터 조회 실패: {}", e.getMessage());
			throw e;
		}
	}
}