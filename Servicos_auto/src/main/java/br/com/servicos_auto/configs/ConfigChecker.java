package br.com.servicos_auto.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConfigChecker implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigChecker.class);

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("----------------------------------------------------------------");
        LOGGER.info("VERIFICANDO CONFIGURAÇÕES ATIVAS NA APLICAÇÃO:");
        LOGGER.info("----------------------------------------------------------------");

        String dbUsername = environment.getProperty("spring.datasource.username");
        LOGGER.info("DataSource Username (spring.datasource.username): {}", dbUsername);

        String dbUrl = environment.getProperty("spring.datasource.url");
        LOGGER.info("DataSource URL (spring.datasource.url): {}", dbUrl);
        
        String cloudinaryCloudName = environment.getProperty("cloudinary.cloud-name");
        LOGGER.info("Cloudinary Cloud Name (cloudinary.cloud-name): {}", cloudinaryCloudName);

        String cloudinaryApiKey = environment.getProperty("cloudinary.api-key");
        LOGGER.info("Cloudinary API Key (cloudinary.api-key): {}", cloudinaryApiKey != null ? "******" : "null");

        LOGGER.info("----------------------------------------------------------------");
    }
}
