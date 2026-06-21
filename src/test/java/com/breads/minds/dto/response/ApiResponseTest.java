package com.breads.minds.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ApiResponseTest {

    @Test
    void ok_withDataOnly_setsSuccessTrueAndData() {
        ApiResponse<String> resp = ApiResponse.ok("hello");
        assertThat(resp.isSuccess()).isTrue();
        assertThat(resp.getData()).isEqualTo("hello");
        assertThat(resp.getTimestamp()).isNotNull();
        assertThat(resp.getMessage()).isNull();
    }

    @Test
    void ok_withMessageAndData_setsAll() {
        ApiResponse<Integer> resp = ApiResponse.ok("done", 42);
        assertThat(resp.isSuccess()).isTrue();
        assertThat(resp.getMessage()).isEqualTo("done");
        assertThat(resp.getData()).isEqualTo(42);
        assertThat(resp.getTimestamp()).isNotNull();
    }

    @Test
    void error_setsSuccessFalseAndMessage() {
        ApiResponse<Void> resp = ApiResponse.error("something went wrong");
        assertThat(resp.isSuccess()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("something went wrong");
        assertThat(resp.getData()).isNull();
        assertThat(resp.getTimestamp()).isNotNull();
    }

    @Test
    void builder_setsAllFields() {
        ApiResponse<String> resp = ApiResponse.<String>builder()
                .success(true)
                .message("msg")
                .data("data")
                .build();
        assertThat(resp.isSuccess()).isTrue();
        assertThat(resp.getMessage()).isEqualTo("msg");
        assertThat(resp.getData()).isEqualTo("data");
    }
}
