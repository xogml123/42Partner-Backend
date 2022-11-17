package partner42.modulecommon.domain.model.match;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MatchStatus {
    MATCHED("매칭 완료"), CANCELED("취소");

    private final String value;
}
