package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EtapeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Etape.class);
        Etape etape1 = new Etape();
        etape1.setId(1L);
        Etape etape2 = new Etape();
        etape2.setId(etape1.getId());
        assertThat(etape1).isEqualTo(etape2);
        etape2.setId(2L);
        assertThat(etape1).isNotEqualTo(etape2);
        etape1.setId(null);
        assertThat(etape1).isNotEqualTo(etape2);
    }
}
