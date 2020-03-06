package life.liudong.community;

import org.h2.tools.Server;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.SQLException;


@SpringBootApplication
@MapperScan(basePackages = "life.liudong.community.mapper")
@EnableScheduling
public class CommunityApplication {

    private static void startH2Server() {
        try {
            Server h2Server = Server.createTcpServer().start();
            if (h2Server.isRunning(true)) {
                System.out.println("H2 server was started and is running.");
            } else {
                throw new RuntimeException("Could not start H2 server.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to start H2 server: ",e);
        }
    }

    public static void main(String[] args) {
        startH2Server();
        SpringApplication.run(CommunityApplication.class, args);
    }

}
