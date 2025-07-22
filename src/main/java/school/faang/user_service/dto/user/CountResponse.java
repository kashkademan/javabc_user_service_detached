package school.faang.user_service.dto.user;


import lombok.RequiredArgsConstructor;



public record CountResponse(long count) {
    @Override
    public long count() {
        return count;
    }
    public CountResponse(long count) {
        this.count = count;
    }
}

