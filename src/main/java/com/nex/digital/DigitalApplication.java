package com.nex.digital;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@SpringBootApplication
public class DigitalApplication  implements ApplicationListener<ContextRefreshedEvent> {

	private static final String FILE_PATH = "src/main/resources/battery-state.txt";


	public static void main(String[] args) {
		SpringApplication.run(DigitalApplication.class, args);
	}

	private final ResourceLoader resourceLoader;

	public DigitalApplication(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// Delete the content of the file
		deleteFileContent();
	}

	private void deleteFileContent() {
		try {
			FileUtils.write(new File(FILE_PATH), "", Charset.defaultCharset());
		} catch (IOException e) {
			System.out.println("Failed to delete file content: " + e.getMessage());
		}
	}
}
