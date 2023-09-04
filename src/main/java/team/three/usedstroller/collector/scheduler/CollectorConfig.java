package team.three.usedstroller.collector.scheduler;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = {"team.three.usedstroller.collector.scheduler"})
@EnableScheduling
public class CollectorConfig {
}
