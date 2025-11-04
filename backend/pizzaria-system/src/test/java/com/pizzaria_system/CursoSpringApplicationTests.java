package com.pizzaria_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CursoSpringApplicationTests {

    @Test
    void contextLoads() {
        // Este teste agora irá iniciar o contexto usando a configuração
        // do Testcontainers, garantindo que o DataSource seja configurado corretamente.
    }
}
