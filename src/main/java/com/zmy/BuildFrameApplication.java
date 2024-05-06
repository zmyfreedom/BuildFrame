package com.zmy;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * @author zmy
 * @create 2024-04-14 14:24
 */
@SpringBootApplication
@EnableEncryptableProperties
public class BuildFrameApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuildFrameApplication.class, args);
    }
}
