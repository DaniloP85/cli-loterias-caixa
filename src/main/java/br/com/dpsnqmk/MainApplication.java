package br.com.dpsnqmk;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import picocli.CommandLine;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(LoteriasCLI loteriasCLI) {
        return args -> {
            new CommandLine(loteriasCLI).execute(args);
            System.exit(0);  // Encerra após execução
        };
    }
}