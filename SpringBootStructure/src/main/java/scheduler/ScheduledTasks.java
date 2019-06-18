package com.siemens.krawal.krawalcloudmanager.scheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.constants.APIConstants;

@Component
public class ScheduledTasks {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);

	@Scheduled(cron = "0 8 * * * ?")
	public void deleteOldLogs() throws IOException {
		try (Stream<Path> files = Files.list(Paths.get(APIConstants.LOG_PATH))) {
			files.forEach(path -> {
				File f = path.toFile();
				if (isFileOld(f)) {
					deleteFile(f);
				}
			});
		}
	}

	public void deleteFile(File file) {
		if (file.delete()) {
			LOGGER.info("file is deleted:" + file.getName());
		}
	}

	public boolean isFileOld(File file) {
		LocalDate fileDate = Instant.ofEpochMilli(file.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate oldDate = LocalDate.now().minusDays(7);
		return fileDate.isBefore(oldDate);
	}
}
