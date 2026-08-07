package com.renato.transfer.infrastructure.observability;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMaskerTest {

    @Test
    void shouldMaskAllButLastFourDigits() {
        assertThat(AccountMasker.mask("1234567890")).isEqualTo("******7890");
    }

    @Test
    void shouldMaskShortAccountEntirely() {
        assertThat(AccountMasker.mask("123")).isEqualTo("****");
    }

    @Test
    void shouldReturnMaskPlaceholderForNull() {
        assertThat(AccountMasker.mask(null)).isEqualTo("****");
    }
}
