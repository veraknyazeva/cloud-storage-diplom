package ru.netology.cloudstoragediplom.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@ConditionalOnProperty(name = "spring.liquibase.enabled", havingValue = "true")
public class LiquibaseRunner {

    private static final String CLASSPATH_DB_LIQUIBASE_CHANGELOG_XML = "classpath:db/liquibase/changelog.xml";
    private static final String SCHEMA = "SCHEMA";
    @Value("${spring.datasource.hikari.schema:public}")
    private String schemaName;

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDefaultSchema(schemaName);
        liquibase.setChangeLog(CLASSPATH_DB_LIQUIBASE_CHANGELOG_XML);
        liquibase.setDataSource(dataSource);
        var parametersMap = new HashMap<String, String>();
        parametersMap.put(SCHEMA, schemaName);
        liquibase.setChangeLogParameters(parametersMap);
        return liquibase;
    }
}
